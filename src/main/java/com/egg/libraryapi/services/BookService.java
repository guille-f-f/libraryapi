package com.egg.libraryapi.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.egg.libraryapi.entities.Author;
import com.egg.libraryapi.entities.Book;
import com.egg.libraryapi.entities.Editorial;
import com.egg.libraryapi.exceptions.ObjectNotFoundException;
import com.egg.libraryapi.models.AuthorResponseDTO;
import com.egg.libraryapi.models.BookRequestDTO;
import com.egg.libraryapi.models.BookResponseDTO;
import com.egg.libraryapi.models.EditorialResponseDTO;
import com.egg.libraryapi.repositories.BookRepository;

@Service
public class BookService {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.upload.book-images-path:uploads/images/books}")
    private String uploadDir;

    @Value("${app.default-book-image:default-book.jpg}")
    private String defaultBookImage;

    private BookRepository bookRepository;
    private FileStorageService fileStorageService;
    private AuthorService authorService;
    private EditorialService editorialService;
    private ModelMapper modelMapper;

    @Autowired
    public BookService(BookRepository bookRepository, FileStorageService fileStorageService,
            AuthorService authorService, EditorialService editorialService,
            ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.fileStorageService = fileStorageService;
        this.authorService = authorService;
        this.editorialService = editorialService;
        this.modelMapper = modelMapper;
    }

    // Create
    @Transactional
    public BookResponseDTO createBook(BookRequestDTO bookRequestDTO) {
        Book book = modelMapper.map(bookRequestDTO, Book.class);
        Editorial editorial = editorialService.getEditorialById(bookRequestDTO.getIdEditorial());
        Author author = authorService.getAuthorById(bookRequestDTO.getIdAuthor());
        book.setEditorial(editorial);
        book.setAuthor(author);
        bookRepository.save(book);
        BookResponseDTO bookDTO = modelMapper.map(book, BookResponseDTO.class);
        bookDTO.setEditorialResponseDTO(modelMapper.map(book.getEditorial(), EditorialResponseDTO.class));
        bookDTO.setAuthorResponseDTO(modelMapper.map(book.getAuthor(), AuthorResponseDTO.class));
        bookDTO.setImageUrl(baseUrl + "/uploads/images/books/" + book.getImageUrl());
        return bookDTO;
    }

    // Upload image
    @Transactional
    public String uploadAndSetImage(Long isbn, MultipartFile file) throws IOException {
        Book book = getBookOrThrow(isbn);
        if (book.getImageUrl() != null && !book.getImageUrl().isBlank()) {
            fileStorageService.deleteImageIfExists(book.getImageUrl());
        }
        String imageUrl = fileStorageService.storeBookImage(isbn, file);
        book.setImageUrl(imageUrl);
        bookRepository.save(book);
        return imageUrl;
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
    public List<BookResponseDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        List<BookResponseDTO> bookResponseDTOs = new ArrayList<>();
        for (Book book : books) {
            bookResponseDTOs.add(mapToDTO(book));
        }
        return bookResponseDTOs;
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
    public BookResponseDTO updateBook(BookRequestDTO bookRequestDTO) {
        Book book = getBookOrThrow(bookRequestDTO.getIsbn());

        book = populateBook(book,
                bookRequestDTO.getIsbn(),
                bookRequestDTO.getBookTitle(),
                bookRequestDTO.getBookActive(),
                bookRequestDTO.getSpecimens(),
                bookRequestDTO.getIdAuthor(),
                bookRequestDTO.getIdEditorial(),
                bookRequestDTO.getImageUrl());

        bookRepository.save(book);

        BookResponseDTO bookDTO = modelMapper.map(book, BookResponseDTO.class);
        bookDTO.setEditorialResponseDTO(modelMapper.map(book.getEditorial(), EditorialResponseDTO.class));
        bookDTO.setAuthorResponseDTO(modelMapper.map(book.getAuthor(), AuthorResponseDTO.class));
        bookDTO.setImageUrl(baseUrl + "/uploads/images/books/" + book.getImageUrl());

        return bookDTO;
    }

    // Delete
    public Book handleBookActivation(Long isbn) {
        Book book = getBookOrThrow(isbn);
        book.setBookActive(!book.getBookActive());
        bookRepository.save(book);
        return book;
    }

    public void deleteBookByIsbn(Long isbn) throws DataIntegrityViolationException, IOException {
        Book book = getBookOrThrow(isbn);
        System.out.println("\n\nBOOK: " + book);
        fileStorageService.deleteImageIfExists(book.getImageUrl()); 
        bookRepository.delete(book);
    }

    // =======================
    // Private methods
    // =======================

    private Book getBookOrThrow(Long isbn) {
        return bookRepository.findById(isbn).orElseThrow(
                () -> new ObjectNotFoundException("Book with isbn " + isbn + " not found."));
    }

    // Mapping the book object with the request data
    private Book populateBook(Book book, Long isbn, String bookTitle, Boolean bookActive, Integer specimens,
            UUID idAuthor,
            UUID idEditorial, String imageUrl) {

        book.setIsbn(isbn);
        book.setBookTitle(bookTitle);
        book.setBookActive(bookActive);
        book.setSpecimens(specimens);

        Editorial editorial = editorialService.getEditorialById(idEditorial);
        if (editorial != null) {
            book.setEditorial(editorial);
        }

        Author author = authorService.getAuthorById(idAuthor);
        if (author != null) {
            book.setAuthor(author);
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            book.setImageUrl(imageUrl);
        }

        return book;
    }

    private BookResponseDTO mapToDTO(Book book) {

        String imageName = (book.getImageUrl() == null || book.getImageUrl().isBlank())
                ? defaultBookImage
                : book.getImageUrl();

        EditorialResponseDTO editorialDTO = null;
        if (book.getEditorial() != null) {
            editorialDTO = EditorialResponseDTO.builder()
                    .idEditorial(book.getEditorial().getIdEditorial())
                    .editorialName(book.getEditorial().getEditorialName())
                    .editorialActive(book.getEditorial().getEditorialActive())
                    .build();
        }

        AuthorResponseDTO authorDTO = null;
        if (book.getAuthor() != null) {
            authorDTO = AuthorResponseDTO.builder()
                    .idAuthor(book.getAuthor().getIdAuthor())
                    .authorName(book.getAuthor().getAuthorName())
                    .authorActive(book.getAuthor().getAuthorActive())
                    .build();
        }

        return BookResponseDTO.builder()
                .isbn(book.getIsbn())
                .bookActive(book.getBookActive())
                .bookTitle(book.getBookTitle())
                .specimens(book.getSpecimens())
                .imageUrl(String.format("%s/uploads/images/books/%s", baseUrl, imageName))
                .editorialResponseDTO(editorialDTO)
                .authorResponseDTO(authorDTO)
                .build();
    }

}
