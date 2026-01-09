package com.flowcommerce.core.support.utils;

import com.flowcommerce.core.support.error.CoreException;
import com.flowcommerce.core.support.error.ErrorType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Component
public class DocumentService {
    @Value("${file.upload.dir}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );

    public FileInfo save(String directory, MultipartFile file) throws IOException {
        validateFile(file);
        String extension = getExtension(file.getOriginalFilename());
        String storedFileName = UUID.randomUUID() + extension;

        String fullPath = uploadDir + "/" + directory;
        Path directoryPath = Paths.get(fullPath);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        Path filePath = directoryPath.resolve(storedFileName);
        file.transferTo(filePath);

        return new FileInfo(
                storedFileName,
                directory + "/" + storedFileName,
                file.getSize()
        );
    }

    public void delete(String filePath) throws IOException {
        Path path = Paths.get(uploadDir + "/" + filePath);
        Files.deleteIfExists(path);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CoreException(ErrorType.DOCUMENT_REQUIRED);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CoreException(ErrorType.DOCUMENT_SIZE_EXCEEDED);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new CoreException(ErrorType.DOCUMENT_INVALID_TYPE);
        }
    }
}
