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
import org.springframework.web.bind.annotation.PutMapping;
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

    // Update
    @PutMapping("/update-with-image/{isbn}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateBookWithImage(
            @PathVariable Long isbn,
            @RequestParam("bookTitle") String bookTitle,
            @RequestParam("bookActive") Boolean bookActive,
            @RequestParam("specimens") Integer specimens,
            @RequestParam("idEditorial") String idEditorial,
            @RequestParam("idAuthor") String idAuthor,
            @RequestParam(value = "file", required = false) MultipartFile file)
            throws IOException, MaxUploadSizeExceededException {

        try {
            // Sanitización básica
            bookTitle = bookTitle.trim();
            if (bookTitle.isEmpty() || specimens == null || specimens < 0) {
                return ResponseEntity.badRequest().body("Invalid input data.");
            }

            if (idEditorial == null || idEditorial.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Editorial ID cannot be null or empty.");
            }

            if (idAuthor == null || idAuthor.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Author ID cannot be null or empty.");
            }

            UUID idEditorialUUID;
            UUID idAuthorUUID;

            try {
                idEditorialUUID = UUID.fromString(idEditorial.trim());
                idAuthorUUID = UUID.fromString(idAuthor.trim());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid UUID format for author or editorial.");
            }

            String imageUrl = (file != null && !file.isEmpty())
                    ? fileStorageService.storeBookImage(isbn, file)
                    : "";

            BookRequestDTO dto = new BookRequestDTO(isbn, bookActive, bookTitle, specimens, imageUrl,
                    idEditorialUUID, idAuthorUUID);

            BookResponseDTO updatedBook = bookService.updateBook(dto);

            return ResponseEntity.ok(updatedBook);

        } catch (MaxUploadSizeExceededException e) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("Uploaded file is too large.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    // Read by isbn
    @GetMapping("/{isbn}")
    // @PreAuthorize("hasRole('USER')")
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
    public ResponseEntity<List<BookResponseDTO>> listBooks() {
        System.out.println("Entramos al controlador para leer los libros...");
        List<BookResponseDTO> books = bookService.getAllBooks();
        System.out.println(books);
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
