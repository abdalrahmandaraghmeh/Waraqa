package com.waraqa.backend.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID

@RestController
@RequestMapping("/api/v1/upload")
class FileUploadController(
    @Value("\${app.upload.max-size-bytes:5242880}") // 5MB default
    private val maxFileSizeBytes: Long
) {
    private val uploadDir = Paths.get("uploads").toAbsolutePath().normalize()

    private val allowedExtensions = setOf("jpg", "jpeg", "png", "webp")
    private val allowedContentTypes = setOf("image/jpeg", "image/png", "image/webp")

    init {
        val dirFile = uploadDir.toFile()
        if (!dirFile.exists()) {
            dirFile.mkdirs()
        }
    }

    @PostMapping
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, String>> {
        if (file.isEmpty) {
            return ResponseEntity.badRequest().body(mapOf("error" to "File cannot be empty"))
        }

        if (file.size > maxFileSizeBytes) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(mapOf("error" to "File size exceeds the maximum limit (5MB)"))
        }

        val contentType = file.contentType?.lowercase()
        if (contentType == null || contentType !in allowedContentTypes) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Only image files (JPEG, PNG, WEBP) are allowed"))
        }

        val originalFilename = file.originalFilename ?: "uploaded_image.jpg"
        val rawExtension = originalFilename.substringAfterLast('.', "").lowercase()
        val extension = if (rawExtension in allowedExtensions) rawExtension else "jpg"

        val uniqueFilename = "${UUID.randomUUID()}.$extension"
        val targetPath = uploadDir.resolve(uniqueFilename)
        Files.copy(file.inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)

        val fileUrl = "/uploads/$uniqueFilename"
        return ResponseEntity.ok(mapOf("url" to fileUrl))
    }
}
