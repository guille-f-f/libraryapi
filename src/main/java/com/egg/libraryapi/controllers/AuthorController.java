package com.egg.libraryapi.controllers;

import com.egg.libraryapi.entities.Author;
import com.egg.libraryapi.models.AuthorResponseDTO;
import com.egg.libraryapi.models.AuthorRequestDTO;
import com.egg.libraryapi.services.AuthorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // Create
    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> createAuthorController(@RequestBody AuthorRequestDTO authorResquestDTO) {
        try {
            return ResponseEntity.ok(authorService.createAuthor(authorResquestDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating author: " + e.getMessage());
        }
    }

    // Read
    @GetMapping("/{idAuthor}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AuthorResponseDTO> readAuthorController(@PathVariable String idAuthor) {
        try {
            return ResponseEntity.ok(authorService.getAuthorResponseDTOById(UUID.fromString(idAuthor)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    // @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Author>> readAllAuthorsController() {
        System.out.println("Listando autores...");
        try {
            return ResponseEntity.ok(authorService.getAllAuthors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update
    @PatchMapping("/{idAuthor}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Author> updateAuthorController(@PathVariable String idAuthor,
            @RequestBody AuthorRequestDTO authorResquestDTO) {
        try {
            return ResponseEntity.ok(authorService.updateAuthor(UUID.fromString(idAuthor), authorResquestDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
