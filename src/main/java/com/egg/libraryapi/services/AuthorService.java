package com.egg.libraryapi.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.egg.libraryapi.entities.Author;
import com.egg.libraryapi.exceptions.ObjectNotFoundException;
import com.egg.libraryapi.models.AuthorResquestDTO;
import com.egg.libraryapi.repositories.AuthorRepository;

@Service
public class AuthorService {

    private AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // Create
    @Transactional
    public Author createAuthor(AuthorResquestDTO authorRequestDTO) {
        return authorRepository.save(populateAuthor(new Author(), authorRequestDTO.getAuthorName()));
    }

    // Read by id
    @Transactional(readOnly = true)
    public Author getAuthorById(UUID idAutor) {
        return getAuthorOrThrow(idAutor);
    }

    // Read all
    @Transactional(readOnly = true)
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    // Read by name
    @Transactional(readOnly = true)
    public Author getAuthorByName(String authorName) {
        return getAuthorOrThrow(authorName);
    }

    // Update
    @Transactional
    public Author updateAuthor(UUID idAuthor, AuthorResquestDTO authorRequestDTO) {
        return authorRepository.save(populateAuthor(getAuthorOrThrow(idAuthor), authorRequestDTO.getAuthorName()));
    }

    // Delete
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

    private Author getAuthorOrThrow(String authorName) {
        Author author = authorRepository.findByAuthorName(authorName).orElseThrow(
            () -> new ObjectNotFoundException("Author with name " + authorName + " not found."));
        return author;
    }

    private Author populateAuthor(Author author, String authorName) {
        author.setAuthorName(authorName);
        return author;
    }

}
