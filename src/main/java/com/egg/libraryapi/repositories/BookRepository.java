package com.egg.libraryapi.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.egg.libraryapi.entities.Book;
import com.egg.libraryapi.models.BookResponseDTO;

public interface BookRepository extends JpaRepository<Book, Long> {
        // Find BookResponseDTO by ISBN
        @Query("SELECT new com.egg.libraryapi.models.BookResponseDTO(b.ISBN, b.bookTitle, b.specimens, new com.egg.libraryapi.models.EditorialResponseDTO(b.editorial.editorialName), new com.egg.libraryapi.models.AuthorResponseDTO(b.author.authorName)) FROM Book b WHERE b.ISBN = :isbn")
        BookResponseDTO findBookResponseDTOByISBN(@Param("isbn") Long isbn);

        // Only the active books are required
        @Query("SELECT new com.egg.libraryapi.models.BookResponseDTO(b.ISBN, b.bookTitle, b.specimens, new com.egg.libraryapi.models.EditorialResponseDTO(b.editorial.editorialName), new com.egg.libraryapi.models.AuthorResponseDTO(b.author.authorName)) FROM Book b WHERE b.bookActive = true")
        List<BookResponseDTO> findBooksActives();

        // Filter by editorial
        @Query("SELECT new com.egg.libraryapi.models.BookResponseDTO(b.ISBN, b.bookTitle, b.specimens, new com.egg.libraryapi.models.EditorialResponseDTO(b.editorial.editorialName), new com.egg.libraryapi.models.AuthorResponseDTO(b.author.authorName)) FROM Book b WHERE b.editorial.idEditorial = :idEditorial")
        List<BookResponseDTO> findBooksByEditorial(@Param("idEditorial") UUID idEditorial);

        // Filter by author
        @Query("SELECT new com.egg.libraryapi.models.BookResponseDTO(b.ISBN, b.bookTitle, b.specimens, new com.egg.libraryapi.models.EditorialResponseDTO(b.editorial.editorialName), new com.egg.libraryapi.models.AuthorResponseDTO(b.author.authorName)) FROM Book b WHERE b.author.idAuthor = :idAuthor")
        List<BookResponseDTO> findBooksByAuthor(@Param("idAuthor") UUID idAuthor);

        // Filter by editorial and author
        @Query("SELECT new com.egg.libraryapi.models.BookResponseDTO(b.ISBN, b.bookTitle, b.specimens, new com.egg.libraryapi.models.EditorialResponseDTO(b.editorial.editorialName), new com.egg.libraryapi.models.AuthorResponseDTO(b.author.authorName)) FROM Book b WHERE b.editorial.idEditorial = :idEditorial AND b.author.idAuthor = :idAuthor")
        List<BookResponseDTO> findBooksByEditorialAndAuthor(@Param("idEditorial") UUID idEditorial,
                        @Param("idAuthor") UUID idAuthor);

}
