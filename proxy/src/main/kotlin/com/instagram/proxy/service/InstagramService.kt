// package com.instagram.proxy.service
//
// import com.github.instagram4j.instagram4j.IGClient
// import com.github.instagram4j.instagram4j.models.media.reel.ReelImageMedia
// import com.github.instagram4j.instagram4j.models.media.reel.ReelVideoMedia
// import org.slf4j.LoggerFactory
// import org.springframework.beans.factory.annotation.Value
// import org.springframework.stereotype.Service
// import java.io.File
// import java.io.FileOutputStream
// import java.net.URL
//
// @Service
// class InstagramService(
//     @Value("\${instagram.login}") private val igLogin: String,
//     @Value("\${instagram.password}") private val igPassword: String
// ) {
//
//     private val LOG = LoggerFactory.getLogger(InstagramService::class.java)
//     private lateinit var instagramClient: IGClient
//
//     init {
//         initializeClient()
//     }
//
//     private fun initializeClient() {
//         instagramClient = IGClient.builder()
//             .username(igLogin)
//             .password(igPassword)
//             .onChallenge { client, response ->
//                 println("Challenge required: ${response}")
//                 null
//             }
//             .login()
//         LOG.info("Instagram client initialized successfully.")
//     }
//
//     fun fetchUserStories(username: String): List<File> {
//         try {
//             val user = instagramClient.actions.users().findByUsername(username).get().user
//             val storyResponse = instagramClient.actions.story().userStory(user.pk).get()
//             val files = mutableListOf<File>()
//
//             storyResponse.reel.items.forEach { item ->
//                 when (item) {
//                     is ReelVideoMedia -> {
//                         val videoUrl = item.video_versions[0].url
//                         files.add(downloadFile(videoUrl, "story_video_${item.id}.mp4"))
//                     }
//
//                     is ReelImageMedia -> {
//                         val imageUrl = item.image_versions2.candidates[0].url
//                         files.add(downloadFile(imageUrl, "story_image_${item.id}.jpg"))
//                     }
//                 }
//             }
//             return files
//         } catch (e: Exception) {
//             LOG.error("Error fetching stories for user $username: ${e.message}", e)
//             throw e
//         }
//     }
//
//     // fun saveReelByLink(reelLink: String): File {
//     //     try {
//     //         LOG.info("Processing reel link: $reelLink")
//     //         val mediaIdResponse = instagramClient.actions. .request(reelLink).get()
//     //         val mediaId = mediaIdResponse.media_id
//     //
//     //         val reelMediaResponse = instagramClient.actions.media().info(mediaId).get() as MediaResponse.Media
//     //         val reelUrl = when {
//     //             reelMediaResponse.media_type == 1 -> reelMediaResponse.image_versions2!!.candidates[0].url
//     //             reelMediaResponse.media_type == 2 -> reelMediaResponse.video_versions!![0].url
//     //             else -> throw IllegalArgumentException("Unsupported media type for reels")
//     //         }
//     //
//     //         val fileExtension = if (reelUrl.endsWith(".mp4")) "mp4" else "jpg"
//     //         val fileName = "reel_${mediaId}.$fileExtension"
//     //         return downloadFile(reelUrl, fileName)
//     //     } catch (e: Exception) {
//     //         LOG.error("Error saving reel from link $reelLink: ${e.message}", e)
//     //         throw e
//     //     }
//     // }
//
//     private fun downloadFile(fileUrl: String, fileName: String): File {
//         val url = URL(fileUrl)
//         val file = File(fileName)
//         url.openStream().use { input ->
//             FileOutputStream(file).use { output ->
//                 input.copyTo(output)
//             }
//         }
//         return file
//     }
// }