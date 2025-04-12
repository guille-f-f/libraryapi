package com.egg.libraryapi.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.egg.libraryapi.entities.Author;
import com.egg.libraryapi.models.AuthorResponseDTO;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {
    public Optional<Author> findByAuthorName(String authorName);

    @Query("SELECT new com.egg.libraryapi.models.AuthorResponseDTO(a.authorName) FROM Author a WHERE a.idAuthor = :idAuthor")
    public Optional<AuthorResponseDTO> findAuthorById(@Param("idAuthor") UUID idAuthor);
}