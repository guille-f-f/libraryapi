package com.egg.libraryapi.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.egg.libraryapi.entities.Author;
import com.egg.libraryapi.exceptions.ObjectNotFoundException;
import com.egg.libraryapi.repositories.AuthorRepository;

@Service
public class AuthorService {

    private AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // CREATE
    @Transactional
    public Author createAuthor(String authorName) {
        return authorRepository.save(populateAuthor(new Author(), authorName));
    }

    // READ BY ID
    @Transactional(readOnly = true)
    public Author getAuthorById(UUID idAutor) {
        return getAuthorOrThrow(idAutor);
    }

    // READ ALL
    @Transactional(readOnly = true)
    public List<Author> getAllUsers() {
        return authorRepository.findAll();
    }

    // UPDATE
    @Transactional
    public Author updateAuthor(UUID idAuthor, String authorName) {
        return authorRepository.save(populateAuthor(getAuthorOrThrow(idAuthor), authorName));
    }

    // DELETE
    @Transactional
    public Author handleAuthorActivation(UUID idAuthor) {
        Author author = getAuthorById(idAuthor);
        author.setAuthorActive(!author.getAuthorActive());
        return author;
    }

    // =======================
    // Private methods
    // =======================

    private Author getAuthorOrThrow(UUID idAutor) {
        Author author = authorRepository.findById(idAutor).orElseThrow(
                () -> new ObjectNotFoundException("Author with id " + idAutor + " not found."));
        return author;
    }

    private Author populateAuthor(Author author, String authorName) {
        author.setAuthorName(authorName);
        author.setAuthorActive(true);
        return author;
    }

}
