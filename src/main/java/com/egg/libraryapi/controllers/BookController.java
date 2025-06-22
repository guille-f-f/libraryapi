package com.egg.libraryapi.controllers;

import java.util.List;
import java.util.UUID;

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
import org.springframework.web.multipart.MultipartFile;

import com.egg.libraryapi.entities.Book;
import com.egg.libraryapi.exceptions.ObjectNotFoundException;
import com.egg.libraryapi.models.BookRequestDTO;
import com.egg.libraryapi.models.BookResponseDTO;
import com.egg.libraryapi.services.BookService;
import com.egg.libraryapi.services.EditorialService;
import com.egg.libraryapi.services.FileStorageService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/books")
public class BookController {
    private BookService bookService;
    private FileStorageService fileStorageService;

    @Autowired
    public BookController(BookService bookService, EditorialService editorialService,
            FileStorageService fileStorageService) {
        this.bookService = bookService;
        this.fileStorageService = fileStorageService;
    }

    // Create
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody @Valid BookRequestDTO bookCreateDTO) {
        try {
            Book bookEntity = bookService.createBook(bookCreateDTO);
            return ResponseEntity.ok(bookEntity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create with image
    @PostMapping("/create-with-image")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createBookWithImage(
            @RequestParam("isbn") Long isbn,
            @RequestParam("bookTitle") String bookTitle,
            @RequestParam("bookActive") Boolean bookActive,
            @RequestParam("specimens") Integer specimens,
            @RequestParam("idEditorial") UUID idEditorial,
            @RequestParam("idAuthor") UUID idAuthor,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            // Lógica de guardado de imagen si existe
            String imageUrl = file != null ? fileStorageService.storeBookImage(isbn, file) : "";
            // Lógica de creación del libro
            BookRequestDTO dto = new BookRequestDTO(isbn, bookActive, bookTitle, specimens, imageUrl, idEditorial,
                    idAuthor);
            bookService.createBook(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }

    // Read by isbn
    @GetMapping("/{isbn}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookResponseDTO> getBookByIsbn(@PathVariable Long isbn) {
        System.out.println("Libro por ISBN");
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookResponseDTO>> listBooks() {
        List<BookResponseDTO> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(books);
    }

    // Active list
    @GetMapping("/active")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookResponseDTO>> listActiveBooks() {
        List<BookResponseDTO> books = bookService.getAllActiveBooks();
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    // Filter by editorial
    @GetMapping("/editorial")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookResponseDTO>> listBooksByEditorial(
            @RequestParam @NotBlank String editorialName) {
        List<BookResponseDTO> books = bookService.getAllBooksByEditorial(editorialName);
        return books.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(books);
    }

    // Filter by author
    @GetMapping("/author")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookResponseDTO>> listBooksByAuthor(
            @RequestParam @NotBlank String authorName) {
        List<BookResponseDTO> books = bookService.getAllBooksByAuthor(authorName);
        return books.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(books);
    }

    // Filter by editorial and author
    @GetMapping("/editorial-author")
    @PreAuthorize("hasRole('USER')")
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
    @PreAuthorize("hasRole('USER')")
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
