package com.instagram.proxy.service

import io.minio.GetObjectArgs
import io.minio.ListObjectsArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.errors.MinioException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

@Service
class MinioService(
    private val minioClient: MinioClient,
    @Value("\${minio.bucket-name}") private val bucketName: String
) {

    private val log = LoggerFactory.getLogger(MinioService::class.java)

    fun uploadFile(file: File) {
        try {
            FileInputStream(file).use { inputStream ->
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .`object`(file.name)
                        .stream(inputStream, file.length(), -1)
                        .contentType("application/octet-stream")
                        .build()
                )
            }
            log.info("File uploaded to MinIO: ${file.name}")
            file.delete()
        } catch (e: MinioException) {
            log.error("Error uploading file to MinIO: ", e)
        } catch (e: IOException) {
            log.error("Error handling file: ", e)
        }
    }

    fun listObjects(prefix: String = ""): List<String> {
        val objectList = mutableListOf<String>()
        val results = minioClient.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .recursive(true)
                .build()
        )

        for (result in results) {
            objectList.add(result.get().objectName())
        }
        return objectList
    }

    fun downloadFile(objectName: String, downloadDir: String): File {
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

        return File(localFilePath)
    }
}
