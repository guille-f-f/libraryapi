package com.egg.libraryapi.services;

import com.egg.libraryapi.entities.Author;
import com.egg.libraryapi.entities.Book;
import com.egg.libraryapi.entities.Editorial;
import com.egg.libraryapi.exceptions.ObjectNotFoundException;
import com.egg.libraryapi.models.BookRequestDTO;
import com.egg.libraryapi.models.BookResponseDTO;
import com.egg.libraryapi.repositories.BookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookService {

    private BookRepository bookRepository;
    private AuthorService authorService;
    private EditorialService editorialService;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorService authorService, EditorialService editorialService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.editorialService = editorialService;
    }

    // CREATE
    @Transactional
    public Book createBook(BookRequestDTO bookRequestDTO) {
        Book book = populateBook(new Book(), bookRequestDTO.getIsbn(), bookRequestDTO.getBookTitle(),
                bookRequestDTO.getSpecimens(), bookRequestDTO.getIdAuthor(), bookRequestDTO.getIdEditorial());
        System.out.println(book);
        return bookRepository.save(book);
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

    // READ ACTIVES
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getAllActiveBooks() {
        return bookRepository.findBooksActives();
    }

    // UPDATE
    @Transactional
    public Book updateBook(BookRequestDTO bookRequestDTO) {
        Book book = getBookOrThrow(bookRequestDTO.getIsbn());
        return bookRepository.save(populateBook(book, bookRequestDTO.getIsbn(), bookRequestDTO.getBookTitle(),
                bookRequestDTO.getSpecimens(), bookRequestDTO.getIdAuthor(), bookRequestDTO.getIdEditorial()));
    }

    // DELETE
    public Book handleBookActivation(Long ISBN) {
        Book book = getBookOrThrow(ISBN);
        book.setBookActive(!book.getBookActive());
        bookRepository.save(book);
        return book;
    }

    // =======================
    // Private methods
    // =======================

    private Book getBookOrThrow(Long ISBN) {
        return bookRepository.findById(ISBN).orElseThrow(
                () -> new ObjectNotFoundException("Book with ISBN " + ISBN + " not found."));
    }

    private Book populateBook(Book book, Long ISBN, String bookName, Integer specimens, UUID idAuthor,
            UUID idEditorial) {
        book.setISBN(ISBN);
        book.setBookTitle(bookName);
        book.setSpecimens(specimens);
        Editorial editorial = editorialService.getEditorialById(idEditorial);
        if (editorial != null) {
            book.setEditorial(editorial);
        }
        System.out.println("editorial: " + editorial);
        Author author = authorService.getAuthorById(idAuthor);
        if (author != null) {
            book.setAuthor(author);
        }
        return book;
    }

}
