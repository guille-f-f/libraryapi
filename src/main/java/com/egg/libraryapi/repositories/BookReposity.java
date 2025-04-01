package com.egg.libraryapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.egg.libraryapi.entities.Book;

public interface BookReposity extends JpaRepository<Book, Long> {
    
}
