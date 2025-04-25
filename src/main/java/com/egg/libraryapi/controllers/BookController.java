package com.egg.libraryapi.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.egg.libraryapi.entities.Book;
import com.egg.libraryapi.exceptions.ObjectNotFoundException;
import com.egg.libraryapi.models.BookRequestDTO;
import com.egg.libraryapi.models.BookResponseDTO;
import com.egg.libraryapi.services.BookService;
import com.egg.libraryapi.services.EditorialService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/books")
public class BookController {
    private BookService bookService;

    @Autowired
    public BookController(BookService bookService, EditorialService editorialService) {
        this.bookService = bookService;
    }

    // Create
    @PostMapping
    public ResponseEntity<Map<String, String>> createBook(@RequestBody @Valid BookRequestDTO bookCreateDTO) {
        try {
            Book book = bookService.createBook(bookCreateDTO);
            Map<String, String> response = Map.of("message", "Book created successfully.", "book", book.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = Map.of("error", "Failed to create book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Read by id
    @GetMapping("/{isbn}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDTO> getBookByIsbn(@PathVariable Long isbn) {
        BookResponseDTO book = null;
        try {
            book = bookService.getBookResponseDTOByISBN(isbn);
            if (book == null) {
                return ResponseEntity.notFound().build();
            }
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(book);
    }
    
    // Read all
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Book>> listBooks() {
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(books);
    }
    
    // Active list
    @GetMapping("/active")
    public ResponseEntity<List<BookResponseDTO>> listActiveBooks() {
        List<BookResponseDTO> books = bookService.getAllActiveBooks();
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    // Filter by editorial
    @GetMapping("/editorial")
    public ResponseEntity<List<BookResponseDTO>> listBooksByEditorial(
            @RequestParam @NotBlank String editorialName) {
        List<BookResponseDTO> books = bookService.getAllBooksByEditorial(editorialName);
        return books.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(books);
    }

    // Filter by author
    @GetMapping("/author")
    public ResponseEntity<List<BookResponseDTO>> listBooksByAuthor(
            @RequestParam @NotBlank String authorName) {
        List<BookResponseDTO> books = bookService.getAllBooksByAuthor(authorName);
        return books.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(books);
    }

    // Filter by editorial and author
    @GetMapping("/editorial-author")
    public ResponseEntity<List<BookResponseDTO>> listBooksByEditorialAndAuthor(
            @RequestParam @NotBlank String editorialName,
            @RequestParam @NotBlank String authorName) {
        List<BookResponseDTO> books = bookService.getAllBooksByEditorialAndAuthor(editorialName, authorName);
        return books.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(books);
    }

    // Delete
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
