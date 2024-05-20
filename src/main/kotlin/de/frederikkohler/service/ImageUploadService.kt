package de.frederikkohler.service

import io.ktor.http.content.*
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*

class ImageUploadService {
    suspend fun upload(userID: String?, multipart: MultiPartData, imageSize: Int? = null): Map<String, List<String>> {
        runBlocking { // Wait for the upload directory to exist
            ensureUploadsDirectoryExists("uploads")
            if (userID != null) ensureUploadsDirectoryExists("uploads/Users/$userID")
        }

        val imageUrls = mutableListOf<String>()

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    val ext = part.originalFileName?.let { File(it).extension }
                    val fileName = UUID.randomUUID().toString() + "." + ext
                    val directoryPath = if (userID == null) "uploads" else "uploads/Users/$userID"
                    val filePath = File("$directoryPath/$fileName")
                    part.streamProvider().use { its -> filePath.outputStream().buffered().use { its.copyTo(it) } }
                    val imageUrl = "$directoryPath/$fileName"
                    imageUrls.add(imageUrl)
                }

                else -> Unit
            }
            part.dispose()
        }

        return mapOf("imageUrls" to imageUrls)
    }

    fun deleteImageByImageName(imagesUrls: List<String>): Boolean {
        val deletedImages: MutableList<String> = mutableListOf()

        try {
            imagesUrls.forEach { imageUrl ->
                val file = File(imageUrl)
                if (file.exists()) {
                    file.delete()
                    deletedImages.add(imageUrl)
                } else {
                    println("Image not found: $imageUrl")
                }
            }


        } catch (e: Exception) {
            println("Error deleting image: ${e.message}")
        }

        return deletedImages.size == imagesUrls.size
    }

    private fun ensureUploadsDirectoryExists(directoryName: String): Boolean {
        val directory = File(directoryName)
        return if (!directory.exists()) {
            directory.mkdirs()
            true
        } else {
            false
        }
    }
}