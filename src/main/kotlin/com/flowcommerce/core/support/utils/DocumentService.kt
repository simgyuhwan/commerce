package com.flowcommerce.core.support.utils

import com.flowcommerce.core.support.error.CoreException
import com.flowcommerce.core.support.error.ErrorType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Component
class DocumentService {
    @Value("\${file.upload.dir}")
    private lateinit var uploadDir: String

    companion object {
        private const val MAX_FILE_SIZE = 5L * 1024 * 1024
        private val ALLOWED_TYPES = listOf(
            "application/pdf",
            "image/jpeg",
            "image/png"
        )
    }

    @Throws(IOException::class)
    fun save(directory: String, file: MultipartFile): FileInfo {
        validateFile(file)
        val extension = getExtension(file.originalFilename)
        val storedFileName = "${UUID.randomUUID()}$extension"

        val fullPath = "$uploadDir/$directory"
        val directoryPath = Paths.get(fullPath)
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath)
        }

        val filePath = directoryPath.resolve(storedFileName)
        file.transferTo(filePath)

        return FileInfo(
            storedFileName = storedFileName,
            filePath = "$directory/$storedFileName",
            fileSize = file.size
        )
    }

    @Throws(IOException::class)
    fun delete(filePath: String) {
        val path = Paths.get("$uploadDir/$filePath")
        Files.deleteIfExists(path)
    }

    private fun getExtension(filename: String?): String {
        if (filename == null || !filename.contains(".")) {
            return ""
        }
        return filename.substring(filename.lastIndexOf("."))
    }

    private fun validateFile(file: MultipartFile) {
        if (file.isEmpty) {
            throw CoreException(ErrorType.DOCUMENT_REQUIRED)
        }

        if (file.size > MAX_FILE_SIZE) {
            throw CoreException(ErrorType.DOCUMENT_SIZE_EXCEEDED)
        }

        val contentType = file.contentType
        if (contentType == null || contentType !in ALLOWED_TYPES) {
            throw CoreException(ErrorType.DOCUMENT_INVALID_TYPE)
        }
    }
}
