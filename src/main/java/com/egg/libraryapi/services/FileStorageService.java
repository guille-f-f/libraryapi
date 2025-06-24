package com.egg.libraryapi.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileStorageService {

    @Value("${app.upload.book-images-path:uploads/images/books}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    // public String storeBookImage(Long isbn, MultipartFile file) throws
    // IOException {
    // Path uploadPath = Paths.get(uploadDir);
    // if (!Files.exists(uploadPath)) {
    // Files.createDirectories(uploadPath);
    // }
    // String originalFilename =
    // Path.of(file.getOriginalFilename()).getFileName().toString(); // sanitizar
    // String fileName = isbn + "_" + System.currentTimeMillis() + "_" +
    // originalFilename;
    // Path filePath = uploadPath.resolve(fileName);
    // Files.copy(file.getInputStream(), filePath,
    // StandardCopyOption.REPLACE_EXISTING);
    // return baseUrl + "/uploads/images/books/" + fileName;
    // }

    public String storeBookImage(Long isbn, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String originalFilename = Path.of(file.getOriginalFilename()).getFileName().toString(); // Sanitizar
        String fileName = isbn + "_" + System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName; // 👉 Guardamos solo el nombre del archivo
    }

    // public void deleteImageIfExists(String imageUrl) throws IOException {
    // if (imageUrl == null || imageUrl.isBlank())
    // return;
    // // Elimina el prefijo "/uploads/images/books/" del path
    // String fileName = Path.of(imageUrl).getFileName().toString();
    // Path imagePath = Paths.get(uploadDir).resolve(fileName);
    // if (Files.exists(imagePath)) {
    // Files.delete(imagePath);
    // }
    // }

    public void deleteImageIfExists(String fileName) throws IOException {
        if (fileName == null || fileName.isBlank())
            return;

        System.out.println("\n\nEstamos en el servicio de eliminación...\n\n");
        Path imagePath = Paths.get(uploadDir).resolve(fileName).toAbsolutePath();
        System.out.println("\n\nRuta de la imagen: " + imagePath + "\n\n");

        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
            System.out.println("Imagen eliminada exitosamente.");
        } else {
            System.out.println("La imagen no existe en el sistema.");
        }
    }
}