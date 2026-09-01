package com.waraqa.backend.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID

@RestController
@RequestMapping("/api/upload")
class FileUploadController {
    private val uploadDir = Paths.get("uploads").toAbsolutePath().normalize()

    init {
        val dirFile = uploadDir.toFile()
        if(!dirFile.exists()) {
            dirFile.mkdirs()
        }
    }
    @PostMapping
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, String>> {
        if (file.isEmpty) {
            return ResponseEntity.badRequest().body(mapOf("error" to "File cannot be empty"))
        }
        val contentType = file.contentType
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Only image files (JPEG, PNG, WEBP) are allowed"))
        }

        val originalFilename = file.originalFilename ?: "uploaded_file"
        val extension = originalFilename.substringAfterLast('.', "jpg")
        val uniqueFilename = "${UUID.randomUUID()}.$extension"

        val targetPath = uploadDir.resolve(uniqueFilename)
        Files.copy(file.inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)

        val fileUrl = "http://localhost:8080/uploads/$uniqueFilename"
        return ResponseEntity.ok(mapOf("url" to fileUrl))
    }
}
