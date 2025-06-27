package com.egg.libraryapi.controllers;

import com.egg.libraryapi.services.BookService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/books")
public class FileUploadController {

    @Autowired
    private BookService bookService;

    @PostMapping("/{isbn}/upload-image")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> uploadBookImage(
            @PathVariable Long isbn,
            @RequestParam("file") MultipartFile file) {
                System.out.println("entaramos");
        try {
            String imageUrl = bookService.uploadAndSetImage(isbn, file);
            return ResponseEntity.ok(Map.of(
                "message", "Image uploaded successfully",
                "imageUrl", imageUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        }
    }
}
