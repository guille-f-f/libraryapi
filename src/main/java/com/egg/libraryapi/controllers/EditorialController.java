package com.egg.libraryapi.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.egg.libraryapi.entities.Editorial;
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
    public ResponseEntity<Map<String, String>> createEditorial(
            @RequestBody @Valid EditorialRequestDTO editorialRequestDTO) {
        try {
            editorialService.createEditorial(editorialRequestDTO);
            Map<String, String> response = Map.of("message", "Editorial created successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = Map.of("error", "Failed to create editorial: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get all editorials
    @GetMapping
    public ResponseEntity<List<Editorial>> getAllEditorials() {
        try {
            List<Editorial> editorials = editorialService.getAllEditorials();
            return ResponseEntity.ok(editorials);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all actives editorials
    @GetMapping("/actives")
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
    public ResponseEntity<String> deleteEditorial(@PathVariable String idEditorial) {
        try {
            editorialService.handleEditorialActivation(UUID.fromString(idEditorial));
            return ResponseEntity.ok("Delete editorial successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{idEditorial}")
    public ResponseEntity<String> deleteEditorialById(@PathVariable String idEditorial) {
        try {
            editorialService.deleteEditorial(UUID.fromString(idEditorial));
            return ResponseEntity.ok("Editorial deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete editorial: " + e.getMessage());
        }
    }

}
