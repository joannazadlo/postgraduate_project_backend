package io.github.joannazadlo.recipedash.service;

import io.github.joannazadlo.recipedash.exception.image.FileTooLargeException;
import io.github.joannazadlo.recipedash.exception.image.ImageUploadException;
import io.github.joannazadlo.recipedash.exception.image.InvalidFileFormatException;
import io.github.joannazadlo.recipedash.repository.entity.Recipe;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    private static final String UPLOADS_DIR = "uploads/";
    private static final String UPLOADS_URL_PREFIX = "/uploads/";

    public void handleImageUpdate(MultipartFile imageFile, boolean imageRemoved, Recipe existing) {
        if ((imageRemoved || (imageFile != null && !imageFile.isEmpty())) && existing.getImageSource() != null) {
            deleteImage(existing.getImageSource());
            existing.setImageSource(null);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageSource = saveImage(imageFile);
            existing.setImageSource(imageSource);
        }
    }

    public String saveImage(MultipartFile imageFile) {
        validateImageFile(imageFile);

        String contentType = imageFile.getContentType();
        String extension = getExtensionFromContentType(contentType);

        String fileName = UUID.randomUUID() + extension;
        Path filePath = Paths.get(UPLOADS_DIR + fileName);

        try {
            Files.createDirectories(filePath.getParent());
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new ImageUploadException("Failed to save image to disk: " + fileName, ex);
        }

        return UPLOADS_URL_PREFIX + fileName;
    }

    public void deleteImage(String imageSource) {
        if (imageSource != null && !imageSource.isEmpty()) {
            if (imageSource.startsWith(UPLOADS_URL_PREFIX)) {
                imageSource = imageSource.substring(UPLOADS_URL_PREFIX.length());
            }

            Path filePath = Paths.get(UPLOADS_DIR, imageSource);

            try {
                if (Files.exists(filePath)) {
                    Files.deleteIfExists(filePath);
                }
            } catch (IOException e) {
                throw new ImageUploadException("Failed to delete image: " + filePath, e);
            }
        }
    }

    public void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }
        String contentType = file.getContentType();
        if (!List.of("image/jpg", "image/jpeg", "image/png", "image/gif").contains(contentType.toLowerCase())) {
            throw new InvalidFileFormatException("Unsupported image file format " + contentType);
        }

        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new FileTooLargeException("File is too large. Maximum allowed size is 5MB.");
        }
    }

    private String getExtensionFromContentType(String contentType) {
        return switch (contentType) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            default -> throw new InvalidFileFormatException("Unsupported content type: " + contentType);
        };
    }
}
