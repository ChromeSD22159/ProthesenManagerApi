package de.frederikkohler.service

import io.ktor.http.content.*
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*

class ImageUploadService {
    suspend fun upload(userID: String?, multipart: MultiPartData): Map<String, String> {
        runBlocking { // Wait for the upload directory to exist
            ensureUploadsDirectoryExists("uploads")
            if (userID != null) ensureUploadsDirectoryExists("uploads/Users/$userID")
        }

        var imageUrl: String? = null

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    val ext = part.originalFileName?.let { File(it).extension }
                    val fileName = UUID.randomUUID().toString() + "." + ext
                    val directoryPath = if (userID == null) "uploads" else "uploads/Users/$userID"
                    val filePath = File("$directoryPath/$fileName")
                    part.streamProvider().use { its -> filePath.outputStream().buffered().use { its.copyTo(it) } }
                    imageUrl = "$directoryPath/$fileName"
                }
                else -> Unit
            }
            part.dispose()
        }

        return imageUrl?.let { mapOf("imageUrl" to it) } ?: throw IllegalArgumentException("Image upload failed")
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