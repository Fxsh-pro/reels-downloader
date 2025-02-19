package com.instagram.proxy.service

import com.github.instagram4j.instagram4j.IGClient
import com.github.instagram4j.instagram4j.models.media.reel.ReelImageMedia
import com.github.instagram4j.instagram4j.models.media.reel.ReelVideoMedia
import com.github.instagram4j.instagram4j.models.media.timeline.ImageCarouselItem
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineCarouselMedia
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineImageMedia
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineVideoMedia
import com.github.instagram4j.instagram4j.models.media.timeline.VideoCarouselItem
import com.github.instagram4j.instagram4j.requests.media.MediaInfoRequest
import com.github.instagram4j.instagram4j.utils.IGUtils
import com.instagram.proxy.integration.dto.DownloadRequest
import io.minio.MinioClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import okhttp3.OkHttpClient
import okhttp3.Request

@Service
class InstagramService(
    @Value("\${instagram.login}") private val igLogin: String,
    @Value("\${instagram.password}") private val igPassword: String,
    private val minioClient: MinioClient,
    @Value("\${minio.bucket-name}") private val bucketName: String
) {
    private val LOG = LoggerFactory.getLogger(InstagramService::class.java)
    private lateinit var instagramClient: IGClient
    private val okHttpClient = OkHttpClient()


    init {
        try {
            initializeClient()
        } catch (e: Exception) {
            println(e)
        }
    }

    private fun initializeClient() {
        instagramClient = IGClient.builder()
            .username(igLogin)
            .password(igPassword)
            .onChallenge { client, response ->
                println("Challenge required: ${response}")
                null
            }
            .login()
        LOG.info("Instagram client initialized successfully.")
    }

    fun fetchUserStories(username: String): List<File> {
        try {
            val user = instagramClient.actions.users().findByUsername(username).get().user
            val storyResponse = instagramClient.actions.story().userStory(user.pk).get()
            val files = mutableListOf<File>()

            storyResponse.reel.items.forEach { item ->
                when (item) {
                    is ReelVideoMedia -> {
                        val videoUrl = item.video_versions[0].url
                        files.add(downloadFile(videoUrl, "story_video_${item.id}.mp4"))
                    }

                    is ReelImageMedia -> {
                        val imageUrl = item.image_versions2.candidates[0].url
                        files.add(downloadFile(imageUrl, "story_image_${item.id}.jpg"))
                    }
                }
            }
            return files
        } catch (e: Exception) {
            LOG.error("Error fetching stories for user $username: ${e.message}", e)
            throw e
        }
    }

    private fun downloadFile(fileUrl: String, fileName: String): File {
        val url = URL(fileUrl)
        val file = File(fileName)
        url.openStream().use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun resolveRedirect(url: String): String {
        val request = Request.Builder()
            .url(url)
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            val resolvedUrl = response.request.url.toString()
            println("Resolved URL: $resolvedUrl")
            return resolvedUrl
        }
    }

    private fun extractShortcode(url: String): String {
        val parts = url.split("/").filter { it.isNotEmpty() }
        if (parts.size < 2) throw IllegalArgumentException("Invalid Instagram URL")

        return parts[parts.size - 2]
    }

    fun downloadContent(downloadRequest: DownloadRequest): List<File> {
        try {
            var url = downloadRequest.url
            LOG.info("Received URL: $url")

            if (url.contains("/share/")) {
                LOG.info("Resolving share URL...")
                url = resolveRedirect(url)
                LOG.info("Resolved URL: $url")
            }

            val shortcode = extractShortcode(url)
            LOG.info("Extracted Shortcode: $shortcode")

            val request = MediaInfoRequest(IGUtils.fromCode(shortcode).toString())
            val response = instagramClient.sendRequest(request).join()
            val files = mutableListOf<File>()

            val targetFolder = File("downloads/$shortcode")
            if (!targetFolder.exists()) {
                targetFolder.mkdirs()
            }

            response.items.forEach { item ->
                when (item) {
                    is TimelineCarouselMedia -> {
                        var count = 0
                        item.carousel_media.forEach { media ->
                            count++
                            when (media) {
                                is ImageCarouselItem -> {
                                    val imageCandidate = media.image_versions2.candidates.firstOrNull()
                                    if (imageCandidate != null) {
                                        val imageUrl = imageCandidate.url
                                        val fileName = "image_${count}_${item.id}.jpg"
                                        val filePath = File(targetFolder, fileName)
                                        files.add(downloadFile(imageUrl, filePath.absolutePath))
                                    }
                                }

                                is VideoCarouselItem -> {
                                    val videoCandidate = media.video_versions.firstOrNull()
                                    if (videoCandidate != null) {
                                        val videoUrl = videoCandidate.url
                                        val fileName = "video_${count}_${item.id}.mp4"
                                        val filePath = File(targetFolder, fileName)
                                        files.add(downloadFile(videoUrl, filePath.absolutePath))
                                    }
                                }
                            }
                        }
                    }

                    is TimelineVideoMedia -> {
                        val videoVersion = item.video_versions.firstOrNull()
                        if (videoVersion != null) {
                            val videoUrl = videoVersion.url
                            val fileName = "video_${item.id}.mp4"
                            val filePath = File(targetFolder, fileName)
                            files.add(downloadFile(videoUrl, filePath.absolutePath))
                        }
                    }

                    is TimelineImageMedia -> {
                        val imageCandidate = item.image_versions2.candidates.firstOrNull()
                        if (imageCandidate != null) {
                            val imageUrl = imageCandidate.url
                            val fileName = "image_${item.id}.jpg"
                            val filePath = File(targetFolder, fileName)
                            files.add(downloadFile(imageUrl, filePath.absolutePath))
                        }
                    }
                }
            }
            LOG.info("All files downloaded in folder: ${targetFolder.absolutePath}")
            return files
        } catch (e: Exception) {
            LOG.error("Error downloading content: ${e.message}", e)
            throw e
        }
    }
}

