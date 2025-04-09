package com.egg.libraryapi.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.egg.libraryapi.entities.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {
    public Optional<Author> findByAuthorName(String authorName);
}