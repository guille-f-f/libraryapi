package com.egg.libraryapi.services;

import com.egg.libraryapi.entities.Author;
import com.egg.libraryapi.entities.Book;
import com.egg.libraryapi.entities.Editorial;
import com.egg.libraryapi.exceptions.ObjectNotFoundException;
import com.egg.libraryapi.models.BookRequestDTO;
import com.egg.libraryapi.models.BookResponseDTO;
import com.egg.libraryapi.repositories.BookRepository;

import org.modelmapper.ModelMapper;
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
    private ModelMapper modelMapper;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorService authorService, EditorialService editorialService, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.editorialService = editorialService;
        this.modelMapper = modelMapper;
    }

    // Create
    // @Transactional
    // public Book createBook(BookRequestDTO bookRequestDTO) {
    //     Book book = populateBook(new Book(), bookRequestDTO.getIsbn(), bookRequestDTO.getBookTitle(),
    //             bookRequestDTO.getSpecimens(), bookRequestDTO.getIdAuthor(), bookRequestDTO.getIdEditorial());
    //     return bookRepository.save(book);
    // }

    @Transactional
    public Book createBook(BookRequestDTO bookRequestDTO) {
        Book book = modelMapper.map(bookRequestDTO, Book.class);
        return bookRepository.save(book);
    }

    // Read by id
    @Transactional(readOnly = true)
    public Book getBookById(Long ISBN) {
        return getBookOrThrow(ISBN);
    }

    @Transactional(readOnly = true)
    public BookResponseDTO getBookResponseDTOByISBN(Long ISBN) {
        return bookRepository.findBookResponseDTOByISBN(ISBN);
    }

    // Read books
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Read books by editorial
    public List<BookResponseDTO> getAllBooksByEditorial(String editorialName) {
        Editorial editorial = editorialService.getEditorialByName(editorialName);
        return bookRepository.findBooksByEditorial(editorial.getIdEditorial());
    }

    // Read books by author
    public List<BookResponseDTO> getAllBooksByAuthor(String authorName) {
        Author author = authorService.getAuthorByName(authorName);
        return bookRepository.findBooksByAuthor(author.getIdAuthor());
    }

    // Read books by editorial and author
    public List<BookResponseDTO> getAllBooksByEditorialAndAuthor(String editorialName, String authorName) {
        Editorial editorial = editorialService.getEditorialByName(editorialName);
        Author author = authorService.getAuthorByName(authorName);
        return bookRepository.findBooksByEditorialAndAuthor(editorial.getIdEditorial(), author.getIdAuthor());
    }
    
    // Read actives
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getAllActiveBooks() {
        return bookRepository.findBooksActives();
    }

    // Update
    @Transactional
    public Book updateBook(BookRequestDTO bookRequestDTO) {
        Book book = getBookOrThrow(bookRequestDTO.getIsbn());
        return bookRepository.save(populateBook(book, bookRequestDTO.getIsbn(), bookRequestDTO.getBookTitle(),
                bookRequestDTO.getSpecimens(), bookRequestDTO.getIdAuthor(), bookRequestDTO.getIdEditorial()));
    }

    // Delete
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

    // Mapping the book object with the request data
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
