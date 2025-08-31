package io.github.joannazadlo.recipedash.service;

import io.github.joannazadlo.recipedash.exception.image.FileTooLargeException;
import io.github.joannazadlo.recipedash.exception.image.InvalidFileFormatException;
import io.github.joannazadlo.recipedash.repository.entity.Recipe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ImageServiceTest {

    private ImageService imageService;

    @BeforeEach
    void setup() {
        imageService = new ImageService();
    }

    @Test
    void validateImageFile_shouldThrowIfFormatUnsupported() {
        MultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "content".getBytes());

        InvalidFileFormatException ex = assertThrows(InvalidFileFormatException.class,
                () -> imageService.validateImageFile(file));

        Assertions.assertTrue(ex.getMessage().contains("Unsupported image file format"));
    }

    @Test
    void validateImageFile_shouldThrowIfFileTooLarge() {
        byte[] largeContent = new byte[6 * 1024 * 1024];
        MultipartFile file = new MockMultipartFile(
                "file", "image.png", "image/png", largeContent);

        FileTooLargeException ex = assertThrows(FileTooLargeException.class,
                () -> imageService.validateImageFile(file));

        Assertions.assertTrue(ex.getMessage().contains("Maximum allowed size is 5MB"));
    }

    @Test
    void validateImageFile_shouldPassForValidFile() {
        MultipartFile file = new MockMultipartFile(
                "file", "image.jpg", "image/jpeg", "content".getBytes());

        assertDoesNotThrow(() -> imageService.validateImageFile(file));
    }

    @Test
    void saveImage_shouldSaveFileAndReturnPath() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", "content".getBytes());

        String path = imageService.saveImage(file);

        assertTrue(path.startsWith("/uploads/"));

        Path savedFile = Paths.get("uploads", path.substring("/uploads/".length()));
        assertTrue(Files.exists(savedFile));

        Files.deleteIfExists(savedFile);
    }

    @Test
    void deleteImage_shouldDeleteFile() throws IOException {
        Path testFile = Paths.get("uploads", "toDelete.png");
        Files.createDirectories(testFile.getParent());
        Files.write(testFile, "content".getBytes());

        assertTrue(Files.exists(testFile));

        imageService.deleteImage("/uploads/toDelete.png");

        assertFalse(Files.exists(testFile));
    }

    @Test
    void handleImageUpdate_shouldUpdateImageSourceAndDeleteOldImage() throws IOException {
        MultipartFile newImage = new MockMultipartFile(
                "file", "new.png", "image/png", "newcontent".getBytes());

        Path oldFile = Paths.get("uploads", "oldImage.png");
        Files.createDirectories(oldFile.getParent());
        Files.write(oldFile, "oldcontent".getBytes());

        Recipe recipe = new Recipe();
        recipe.setImageSource("/uploads/oldImage.png");

        imageService.handleImageUpdate(newImage, false, recipe);

        assertFalse(Files.exists(oldFile));

        assertNotNull(recipe.getImageSource());
        assertTrue(recipe.getImageSource().startsWith("/uploads/"));

        Path newFile = Paths.get("uploads", recipe.getImageSource().substring("/uploads/".length()));
        Files.deleteIfExists(newFile);
    }
}
