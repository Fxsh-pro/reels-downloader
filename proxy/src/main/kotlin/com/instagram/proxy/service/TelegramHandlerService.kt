package com.instagram.proxy.service

import com.instagram.proxy.domain.model.TelegramMessage
import com.instagram.proxy.integration.InstagramContentClient
import com.instagram.proxy.integration.dto.DownloadRequest
import io.minio.GetObjectArgs
import io.minio.ListObjectsArgs
import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import org.springframework.util.StopWatch

@Service
class TelegramHandlerService(
    val instagramContentClient: InstagramContentClient,
    val minioClient: MinioClient,
    @Value("\${minio.bucket-name}") val bucketName: String,
) {
    fun handleIncomingMessage(telegramMessage: TelegramMessage): List<File> {
        val watch = StopWatch()

        watch.start("Download Instagram Content")
        val res = instagramContentClient.downloadInstagramContent(DownloadRequest(telegramMessage.text))
        watch.stop()
        println("Time taken for downloading Instagram content: ${watch.lastTaskTimeMillis} ms")

        watch.start("List Objects in MinIO")
        val result = minioClient.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucketName)
                .recursive(false)
                .build()
        )
        watch.stop()
        println("Time taken to list objects in MinIO: ${watch.lastTaskTimeMillis} ms")

        watch.start("Find First Folder")
        val firstFolder = result.filter { it.get().objectName().endsWith("/") }
            .firstOrNull { it.get().objectName() == res.shortcode + "/" }?.get()?.objectName()
        watch.stop()
        println("Time taken to find first folder: ${watch.lastTaskTimeMillis} ms")

        if (firstFolder == null) {
            println("No folders found in the bucket.")
            return emptyList()
        }

        watch.start("List Folder Objects")
        val folderObjects = minioClient.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(firstFolder)
                .recursive(true)
                .build()
        ).toList()
        watch.stop()
        println("Time taken to list folder objects: ${watch.lastTaskTimeMillis} ms")

        watch.start("Filter File Types")
        val mp4Files = folderObjects.filter { it.get().objectName().endsWith(".mp4") }.map { it.get() }
        val txtFiles = folderObjects.filter { it.get().objectName().endsWith(".txt") }.map { it.get() }
        val jpgFiles = folderObjects.filter { it.get().objectName().endsWith(".jpg") }.map { it.get() }.sortedBy { it.objectName() }
        watch.stop()
        println("Time taken to filter file types: ${watch.lastTaskTimeMillis} ms")

        val downloadDir = "downloads"
        val downloadedFiles = mutableListOf<File>()

        if (mp4Files.isNotEmpty()) {
            watch.start("Download MP4 File")
            val mp4File = mp4Files.first()
            downloadedFiles.add(downloadFile(mp4File.objectName(), downloadDir))
            watch.stop()
            println("Time taken to download MP4 file: ${watch.lastTaskTimeMillis} ms")

            watch.start("Download TXT File (if available)")
            val txtFile = txtFiles.firstOrNull()
            if (txtFile != null) {
                downloadedFiles.add(downloadFile(txtFile.objectName(), downloadDir))
            } else {
                println("No .txt file found.")
            }
            watch.stop()
            println("Time taken to download TXT file: ${watch.lastTaskTimeMillis} ms")
        } else {
            if (jpgFiles.isNotEmpty()) {
                println("Downloading .jpg files:")
                watch.start("Download JPG Files")
                jpgFiles.forEach { jpg ->
                    downloadedFiles.add(downloadFile(jpg.objectName(), downloadDir))
                }
                watch.stop()
                println("Time taken to download JPG files: ${watch.lastTaskTimeMillis} ms")
            } else {
                println("No .jpg files found.")
            }

            watch.start("Download TXT File (if available)")
            val txtFile = txtFiles.firstOrNull()
            if (txtFile != null) {
                downloadedFiles.add(downloadFile(txtFile.objectName(), downloadDir))
            } else {
                println("No .txt file found.")
            }
            watch.stop()
            println("Time taken to download TXT file: ${watch.lastTaskTimeMillis} ms")
        }

        println(watch.prettyPrint()) // Prints all task execution times in a formatted way

        return downloadedFiles
    }

    // fun handleIncomingMessage(telegramMessage: TelegramMessage): List<File> {
    //     val watch = StopWatch()
    //     watch.start()
    //     val res = instagramContentClient.downloadInstagramContent(DownloadRequest(telegramMessage.text))
    //     watch.stop()
    //
    //     println("Execution time: ${watch.totalTimeMillis} ms")
    //
    //     val result = minioClient.listObjects(
    //         ListObjectsArgs.builder()
    //             .bucket(bucketName)
    //             .recursive(false)
    //             .build()
    //     )
    //
    //     val firstFolder = result.filter { it.get().objectName().endsWith("/") }
    //         .firstOrNull { it.get().objectName() == res.shortcode + "/" }?.get()?.objectName()
    //
    //     if (firstFolder == null) {
    //         println("No folders found in the bucket.")
    //         return emptyList()
    //     }
    //
    //     val folderObjects = minioClient.listObjects(
    //         ListObjectsArgs.builder()
    //             .bucket(bucketName)
    //             .prefix(firstFolder)
    //             .recursive(true)
    //             .build()
    //     ).toList()
    //
    //     val mp4Files = folderObjects.filter { it.get().objectName().endsWith(".mp4") }.map { it.get() }
    //     val txtFiles = folderObjects.filter { it.get().objectName().endsWith(".txt") }.map { it.get() }
    //     val jpgFiles = folderObjects.filter { it.get().objectName().endsWith(".jpg") }.map { it.get() }.sortedBy { it.objectName() }
    //
    //     val downloadDir = "downloads"
    //     val downloadedFiles = mutableListOf<File>()
    //
    //     if (mp4Files.isNotEmpty()) {
    //         val mp4File = mp4Files.first()
    //         downloadedFiles.add(downloadFile(mp4File.objectName(), downloadDir))
    //
    //         val txtFile = txtFiles.firstOrNull()
    //         if (txtFile != null) {
    //             downloadedFiles.add(downloadFile(txtFile.objectName(), downloadDir))
    //         } else {
    //             println("No .txt file found.")
    //         }
    //     } else {
    //         if (jpgFiles.isNotEmpty()) {
    //             println("Downloading .jpg files:")
    //             jpgFiles.forEach { jpg ->
    //                 downloadedFiles.add(downloadFile(jpg.objectName(), downloadDir))
    //             }
    //         } else {
    //             println("No .jpg files found.")
    //         }
    //
    //         val txtFile = txtFiles.firstOrNull()
    //         if (txtFile != null) {
    //             downloadedFiles.add(downloadFile(txtFile.objectName(), downloadDir))
    //         } else {
    //             println("No .txt file found.")
    //         }
    //     }
    //
    //     return downloadedFiles
    // }

    private fun downloadFile(objectName: String, downloadDir: String): File {
        val localFilePath = Paths.get(downloadDir, objectName).toString()
        Files.createDirectories(Paths.get(localFilePath).parent)

        minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucketName)
                .`object`(objectName)
                .build()
        ).use { inputStream ->
            File(localFilePath).outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        // println("Downloaded: $objectName to $localFilePath")
        return File(localFilePath)
    }
}
