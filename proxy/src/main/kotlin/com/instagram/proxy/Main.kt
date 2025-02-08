import io.minio.BucketExistsArgs
import io.minio.DownloadObjectArgs
import io.minio.GetObjectArgs
import io.minio.ListObjectsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.UploadObjectArgs
import io.minio.errors.MinioException
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun main() {
    val minioClient = MinioClient.builder()
        .endpoint("http://localhost:9000")
        .credentials("minio_user", "minio_password")
        .build()

    val bucketName = "instagram-content"
    val filePath = "path/to/your/local/file.txt"
    val objectName = "file.txt"

    // val result = minioClient.listObjects(
    //     ListObjectsArgs.builder()
    //         .bucket(bucketName)
    //         .recursive(false)
    //         .build()
    // )
    //
    // val firstFolder =
    //     result.filter { it.get().objectName().endsWith("/") }.firstOrNull { it.get().objectName() == "DAxPZoIyHFa/"}?.get()?.objectName()
    //
    // if (firstFolder == null) {
    //     println("No folders found in the bucket.")
    //     return
    // }
    //
    // val folderObjects = minioClient.listObjects(
    //     ListObjectsArgs.builder()
    //         .bucket(bucketName)
    //         .prefix(firstFolder)
    //         .recursive(true)
    //         .build()
    // )
    //
    // folderObjects.forEach { println(it.get().objectName())}
    // val downloadDir = "downloads"
    // folderObjects.forEach { obj ->
    //     val item = obj.get()
    //     val objectName = item.objectName()
    //     val localFilePath = Paths.get(downloadDir, objectName).toString()
    //
    //     // Ensure local directories exist
    //     Files.createDirectories(Paths.get(localFilePath).parent)
    //
    //     // Download the object
    //     minioClient.getObject(
    //         GetObjectArgs.builder()
    //             .bucket(bucketName)
    //             .`object`(objectName)
    //             .build()
    //     ).use { inputStream ->
    //         File(localFilePath).outputStream().use { outputStream ->
    //             inputStream.copyTo(outputStream)
    //         }
    //     }
    //
    //     println("Downloaded: $objectName to $localFilePath")
    // }
    //
    // println("All objects from the folder '$firstFolder' have been downloaded.")
    // println(minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).build()))
    // Upload the file to MinIO
    // uploadFile(minioClient, bucketName, filePath, objectName)

    // Download the file from MinIO
    // downloadFile(minioClient, bucketName, objectName, "downloaded_$objectName")
}

// Function to upload a file to MinIO
fun uploadFile(minioClient: MinioClient, bucketName: String, filePath: String, objectName: String) {
    try {
        val bucket = BucketExistsArgs.builder().bucket(bucketName).build()
        val makeBucket = MakeBucketArgs.builder().bucket(bucketName).build()
        if (!minioClient.bucketExists(bucket)) {
            minioClient.makeBucket(makeBucket)
            println("Bucket $bucketName created.")
        }

        // Upload the file
        val file = File(filePath)
        minioClient.uploadObject(
            UploadObjectArgs.builder()
                .bucket(bucketName)
                .`object`("pom.xml")
                .filename("/Users/mnikishaev/DEV/proxy/pom.xml")
                .build()
        )
        println("File '$objectName' uploaded to bucket '$bucketName'.")
    } catch (e: MinioException) {
        println("Error occurred: ${e.message}")
    }
}

fun downloadFile(minioClient: MinioClient, bucketName: String, objectName: String, downloadPath: String) {
    try {
        // minioClient.bu
        // Download the file from MinIO
        val filePath: Path = Paths.get(downloadPath)
        val l = DownloadObjectArgs.builder().bucket(bucketName).`object`("2025-01-07_08-35-00_UTC.mp4").filename("2025-01-07_08-35-00_UTC.mp4")
            .build()
        val f = minioClient.downloadObject(l)
        println("DONE")
    } catch (e: MinioException) {
        println("Error occurred: ${e.message}")
    }
}
