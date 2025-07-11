package com.egg.libraryapi.controllers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.egg.libraryapi.entities.Editorial;
import com.egg.libraryapi.exceptions.ObjectNotFoundException;
import com.egg.libraryapi.models.EditorialRequestDTO;
import com.egg.libraryapi.models.EditorialResponseDTO;
import com.egg.libraryapi.services.EditorialService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/editorials")
public class EditorialController {

    private final EditorialService editorialService;

    public EditorialController(EditorialService editorialService) {
        this.editorialService = editorialService;
    }

    // Create
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Editorial> createEditorial(
            @RequestBody @Valid EditorialRequestDTO editorialRequestDTO) {
        try {
            System.out.println("Ingresamos");
            Editorial editorialEntity = editorialService.createEditorial(editorialRequestDTO);
            return ResponseEntity.ok(editorialEntity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get by id
    @GetMapping("/{idEditorial}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<EditorialResponseDTO> getEditorialById(@PathVariable String idEditorial) {
        try {
            return ResponseEntity.ok(editorialService.getEditorialResponseDTOById(UUID.fromString(idEditorial)));
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all editorials
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Editorial>> getAllEditorials() {

        System.out.println("Entramos a getAllEditorials...");

        try {
            List<Editorial> editorials = editorialService.getAllEditorials();
            return ResponseEntity.ok(editorials);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all actives editorials
    @GetMapping("/actives")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<EditorialResponseDTO>> getActivesEditorials() {
        try {
            List<EditorialResponseDTO> activesEditorials = editorialService.getActivesEditorials();
            return ResponseEntity.ok(activesEditorials);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all inactives editorials
    @GetMapping("/inactives")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Editorial>> getInactivesEditorials() {
        try {
            List<Editorial> inactivesEditorials = editorialService.getInactivesEditorials();
            return ResponseEntity.ok(inactivesEditorials);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update
    @PatchMapping("/{idEditorial}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Editorial> renameEditorial(@PathVariable String idEditorial,
            @RequestBody EditorialRequestDTO editorialRequestDTO) {
        try {
            Editorial editorial = editorialService.updateEditorial(UUID.fromString(idEditorial), editorialRequestDTO);
            return ResponseEntity.ok(editorial);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Delete
    @DeleteMapping("/deactivate/{idEditorial}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> disableEditorial(@PathVariable String idEditorial) {
        try {
            editorialService.handleEditorialActivation(UUID.fromString(idEditorial));
            return ResponseEntity.ok(Map.of("Message", "Editorial state update successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", "Failed to update editorial state: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{idEditorial}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> deleteEditorialById(@PathVariable String idEditorial) {
        try {
            editorialService.deleteEditorialById(UUID.fromString(idEditorial));
            return ResponseEntity.ok(Map.of("Message", "Editorial deleted successfully."));
        } catch (DataIntegrityViolationException e) {
            System.out.println("\nERROR!, there are other entities that depend on this");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Failed to delete editorial, there are other entities that depend on this: "
                            + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", "Failed to delete editorial: " + e.getMessage()));
        }
    }

}
