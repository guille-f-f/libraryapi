package com.egg.libraryapi.controllers;

import com.egg.libraryapi.entities.Author;
import com.egg.libraryapi.models.AuthorResponseDTO;
import com.egg.libraryapi.models.AuthorRequestDTO;
import com.egg.libraryapi.services.AuthorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<AuthorResponseDTO> readAuthorController(@PathVariable String idAuthor) {
        try {
            return ResponseEntity.ok(authorService.getAuthorResponseDTOById(UUID.fromString(idAuthor)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Author>> readAllAuthorsController() {
        try {
            return ResponseEntity.ok(authorService.getAllAuthors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update
    @PatchMapping("/{idAuthor}")
    public ResponseEntity<Author> updateAuthorController(@PathVariable String idAuthor,
            @RequestBody AuthorRequestDTO authorResquestDTO) {
        try {
            return ResponseEntity.ok(authorService.updateAuthor(UUID.fromString(idAuthor), authorResquestDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
