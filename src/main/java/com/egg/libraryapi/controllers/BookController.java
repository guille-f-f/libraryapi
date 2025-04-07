package com.egg.libraryapi.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.egg.libraryapi.models.BookCreateDTO;
import com.egg.libraryapi.models.BookResponseDTO;
import com.egg.libraryapi.services.BookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/books")
public class BookController {
    private BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Map<String, String>> createBook(@RequestBody @Valid BookCreateDTO bookCreateDTO) {
        try {
            bookService.createBook(bookCreateDTO);
            Map<String, String> response = Map.of("message", "Book created successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = Map.of("error", "Failed to create book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ACTIVE LIST
    @GetMapping("/active")
    public ResponseEntity<List<BookResponseDTO>> listActiveBooks() {
        List<BookResponseDTO> books = bookService.getAllActiveBooks();
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    // DELETE
    @DeleteMapping("/{isbn}")
    public String deleteBook(@PathVariable Long isbn) {
        System.out.println(isbn);
        try {
            bookService.handleBookActivation(isbn);
            return "Book successfully deleted.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
