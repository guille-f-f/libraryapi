package com.egg.libraryapi.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.egg.libraryapi.entities.Editorial;

@Repository
public interface EditorialRepository extends JpaRepository<Editorial, UUID> {
    @Query("SELECT e FROM Editorial e WHERE e.editorialActive = true")
    List<Editorial> getActiveEditorials();
}