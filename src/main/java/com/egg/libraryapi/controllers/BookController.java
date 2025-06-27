package com.egg.libraryapi.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody @Valid BookRequestDTO bookCreateDTO) {
        try {
            BookResponseDTO bookEntity = bookService.createBook(bookCreateDTO);
            return ResponseEntity.ok(bookEntity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create with image
    @PostMapping("/create-with-image")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookResponseDTO> createBookWithImage(
            @RequestParam("isbn") Long isbn,
            @RequestParam("bookTitle") String bookTitle,
            @RequestParam("bookActive") Boolean bookActive,
            @RequestParam("specimens") Integer specimens,
            @RequestParam("idEditorial") UUID idEditorial,
            @RequestParam("idAuthor") UUID idAuthor,
            @RequestParam(value = "file", required = false) MultipartFile file)
            throws IOException, MaxUploadSizeExceededException {

        String imageUrl = file != null ? fileStorageService.storeBookImage(isbn, file) : "";

        BookRequestDTO dto = new BookRequestDTO(isbn, bookActive, bookTitle, specimens, imageUrl, idEditorial,
                idAuthor);
        BookResponseDTO bookEntity = bookService.createBook(dto);
        return ResponseEntity.ok(bookEntity);
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
        return ResponseEntity.ok(books);
    }

    // Active list
    @GetMapping("/active")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookResponseDTO>> listActiveBooks() {
        List<BookResponseDTO> books = bookService.getAllActiveBooks();
        return ResponseEntity.ok(books);
    }

    // Filter by editorial
    @GetMapping("/editorial")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookResponseDTO>> listBooksByEditorial(
            @RequestParam @NotBlank String editorialName) {
        List<BookResponseDTO> books = bookService.getAllBooksByEditorial(editorialName);
        return ResponseEntity.ok(books);
    }

    // Filter by author
    @GetMapping("/author")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookResponseDTO>> listBooksByAuthor(
            @RequestParam @NotBlank String authorName) {
        List<BookResponseDTO> books = bookService.getAllBooksByAuthor(authorName);
        return ResponseEntity.ok(books);
    }

    // Filter by editorial and author
    @GetMapping("/editorial-author")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookResponseDTO>> listBooksByEditorialAndAuthor(
            @RequestParam @NotBlank String editorialName,
            @RequestParam @NotBlank String authorName) {
        List<BookResponseDTO> books = bookService.getAllBooksByEditorialAndAuthor(editorialName, authorName);
        return ResponseEntity.ok(books);
    }

    // Delete
    @DeleteMapping("/deactivate/{isbn}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> disableBook(@PathVariable Long isbn) {
        try {
            bookService.handleBookActivation(isbn);
            return ResponseEntity.ok(Map.of("Message", "Book state update successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", "Failed to update book state: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{isbn}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> deleteBookByIsbn(@PathVariable Long isbn) {
        System.out.println("INGRESAMOS AL CONTROLADOR...");
        try {
            bookService.deleteBookByIsbn(isbn);
            return ResponseEntity.ok(Map.of("Message", "Book deleted successfully."));
        } catch (DataIntegrityViolationException e) {
            System.out.println("\nERROR!, there are other entities that depend on this");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("Error", "Failed to delete book, there are other entities that depend on this: "
                            + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", "Failed to delete book: " + e.getMessage()));
        }
    }

}
