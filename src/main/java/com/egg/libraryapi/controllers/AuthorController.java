package com.egg.libraryapi.controllers;

import com.egg.libraryapi.entities.Author;
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

    @PostMapping("/create")
    public ResponseEntity<Object> createAuthorController(@RequestParam String authorName) {
        try {
            authorService.createAuthor(authorName);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating author: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Author>> readAllAuthorsController() {
        try {
            List<Author> authors = authorService.getAllAuthors();
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<Void> updateAuthorController(@RequestParam UUID idAuthor, @RequestParam String authorName) {
        try {
            authorService.updateAuthor(idAuthor, authorName);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
