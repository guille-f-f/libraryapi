package com.egg.libraryapi.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.egg.libraryapi.entities.Editorial;
import com.egg.libraryapi.models.EditorialResponseDTO;

@Repository
public interface EditorialRepository extends JpaRepository<Editorial, UUID> {
    List<Editorial> findByEditorialActiveTrue();
    
    List<Editorial> findByEditorialActiveFalse();
    
    // Only the active books are required.
    @Query("SELECT new com.egg.libraryapi.models.EditorialResponseDTO(e.editorialName) FROM Editorial e WHERE e.editorialActive = true")
    List<EditorialResponseDTO> findEditorialsActives();

}