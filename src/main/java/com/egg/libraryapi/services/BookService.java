package com.egg.libraryapi.services;

import com.egg.libraryapi.entities.Book;
import com.egg.libraryapi.exceptions.ObjectNotFoundException;
import com.egg.libraryapi.repositories.BookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // CREATE
    @Transactional
    public Book createBook(Long ISBN, String bookName) {
        return bookRepository.save(populateBook(new Book(), ISBN, bookName));
    }

    // READ BY ID
    @Transactional(readOnly = true)
    public Book getBookById(Long ISBN) {
        return getBookOrThrow(ISBN);
    }

    // READ ALL
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // UPDATE
    @Transactional
    public Book updateBook(Long ISBN, String bookName) {
        Book book = getBookOrThrow(ISBN);
        return bookRepository.save(populateBook(book, ISBN, bookName));
    }

    // DELETE
    public Book handleBookActivation(Long ISBN) {
        Book book = getBookOrThrow(ISBN);
        book.setBookActive(!book.getBookActive());
        return book;
    }

    // =======================
    // Private methods
    // =======================

    private Book getBookOrThrow(Long ISBN) {
        return bookRepository.findById(ISBN).orElseThrow(
                () -> new ObjectNotFoundException("Book with ISBN " + ISBN + " not found."));
    }

    private Book populateBook(Book book, Long ISBN, String bookName) {
        book.setISBN(ISBN);
        book.setBookTitle(bookName);
        book.setBookActive(true);
        return book;
    }

}
