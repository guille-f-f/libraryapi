package com.egg.libraryapi.services;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.egg.libraryapi.entities.Author;
import com.egg.libraryapi.entities.Editorial;
import com.egg.libraryapi.exceptions.ObjectNotFoundException;
import com.egg.libraryapi.models.AuthorResponseDTO;
import com.egg.libraryapi.models.AuthorRequestDTO;
import com.egg.libraryapi.repositories.AuthorRepository;

@Service
public class AuthorService {

    private ModelMapper modelMapper;
    private AuthorRepository authorRepository;

    @Autowired
    public AuthorService(ModelMapper modelMapper, AuthorRepository authorRepository) {
        this.modelMapper = modelMapper;
        this.authorRepository = authorRepository;
    }

    // Create
    @Transactional
    public Author createAuthor(AuthorRequestDTO authorRequestDTO) {
        return authorRepository.save(populateAuthor(new Author(), authorRequestDTO.getAuthorName()));
    }

    // Read by id
    @Transactional(readOnly = true)
    public Author getAuthorById(UUID idAutor) {
        return getAuthorOrThrow(idAutor);
    }

    @Transactional(readOnly = true)
    public AuthorResponseDTO getAuthorResponseDTOById(UUID idAutor) {
        return getAuthorResponseDTOOrThrow(idAutor);
    }

    // Read by name
    @Transactional(readOnly = true)
    public Author getAuthorByName(String authorName) {
        return getAuthorOrThrow(authorName);
    }

    @Transactional(readOnly = true)
    public AuthorResponseDTO getAuthorResponseDTOByName(String authorName) {
        return modelMapper.map(getAuthorOrThrow(authorName), AuthorResponseDTO.class);
    }

    // Read all
    @Transactional(readOnly = true)
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    // Update
    @Transactional
    public Author updateAuthor(UUID idAuthor, AuthorRequestDTO authorRequestDTO) {
        return authorRepository.save(populateAuthor(getAuthorOrThrow(idAuthor), authorRequestDTO.getAuthorName()));
    }

    // Delete
    @Transactional
    public Author handleAuthorActivation(UUID idAuthor) {
        Author author = authorRepository.findById(idAuthor)
                .orElseThrow(() -> new ObjectNotFoundException("Author with id " + idAuthor + " not found."));
        author.setAuthorActive(!author.getAuthorActive());
        return authorRepository.save(author);
    }

    public void deleteAuthorById(UUID idAuthor) throws DataIntegrityViolationException {
        Author author = getAuthorOrThrow(idAuthor);
        authorRepository.delete(author);
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

    private AuthorResponseDTO getAuthorResponseDTOOrThrow(UUID idAutor) {
        AuthorResponseDTO author = authorRepository.findAuthorById(idAutor).orElseThrow(
                () -> new ObjectNotFoundException("Author with id " + idAutor + " not found."));
        return author;
    }

    // private Author getAuthorResponseDTOOrThrow(String authorName) {
    // Author author = authorRepository.findByAuthorName(authorName).orElseThrow(
    // () -> new ObjectNotFoundException("Author with name " + authorName + " not
    // found."));
    // return author;
    // }

    private Author populateAuthor(Author author, String authorName) {
        author.setAuthorName(authorName);
        return author;
    }

}
