package com.egg.libraryapi.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.egg.libraryapi.entities.Editorial;
import com.egg.libraryapi.models.EditorialResponseDTO;

@Repository
public interface EditorialRepository extends JpaRepository<Editorial, UUID> {
    List<Editorial> findByEditorialActiveTrue();
    
    List<Editorial> findByEditorialActiveFalse();

    Optional<Editorial> findByEditorialName(String editorialName);

    @Query("SELECT new com.egg.libraryapi.models.EditorialResponseDTO(e.idEditorial, e.editorialName, e.editorialActive) FROM Editorial e WHERE e.editorialName = :editorialName")
    Optional<EditorialResponseDTO> findByQueryWithEditorialName(@Param("editorialName") String editorialName);

    // Filter by idEditorial, and return the EditorialResponseDTO with the editorialName.
    @Query("SELECT new com.egg.libraryapi.models.EditorialResponseDTO(e.idEditorial, e.editorialName, e.editorialActive) FROM Editorial e WHERE e.idEditorial = :idEditorial")
    Optional<EditorialResponseDTO> findEditorialById(@Param("idEditorial") UUID idEditorial);
    
    // Only the active books are required.
    @Query("SELECT new com.egg.libraryapi.models.EditorialResponseDTO(e.idEditorial, e.editorialName, e.editorialActive) FROM Editorial e WHERE e.editorialActive = true")
    List<EditorialResponseDTO> findEditorialsActives();

}