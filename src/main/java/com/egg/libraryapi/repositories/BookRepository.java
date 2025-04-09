package com.egg.libraryapi.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.egg.libraryapi.entities.Book;
import com.egg.libraryapi.models.BookResponseDTO;

public interface BookRepository extends JpaRepository<Book, Long> {
    // Only the active books are required.
    @Query("SELECT new com.egg.libraryapi.models.BookResponseDTO(b.bookTitle, b.specimens) FROM Book b WHERE b.bookActive = true")
    List<BookResponseDTO> findBooksActives();

    @Query("SELECT new com.egg.libraryapi.models.BookResponseDTO(b.bookTitle, b.specimens) " +
            "FROM Book b WHERE b.editorial.idEditorial = :idEditorial")
    List<BookResponseDTO> findBooksByEditorial(@Param("idEditorial") UUID idEditorial);

}
