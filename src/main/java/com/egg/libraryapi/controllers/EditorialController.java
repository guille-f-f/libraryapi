package com.egg.libraryapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.egg.libraryapi.entities.Editorial;
import com.egg.libraryapi.services.EditorialService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/editorial")
public class EditorialController {

    private EditorialService editorialService;

    @Autowired
    public EditorialController(EditorialService editorialService) {
        this.editorialService = editorialService;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createEditorialController(@RequestParam String editorialName) {
        try {
            editorialService.createEditorial(editorialName);
            Map<String, String> response = Map.of("Response", "Editorial created successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = Map.of("Error", "Failed to create editorial: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Editorial>> getAllEditorialController() {
        try {
            List<Editorial> editorials = editorialService.getAllEditorials();
            return ResponseEntity.ok().body(editorials);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/actives")
    public ResponseEntity<List<Editorial>> getActiveEditorialsController() {
        try {
            List<Editorial> activeEditorials = editorialService.getActiveEditorials();
            return ResponseEntity.ok(activeEditorials);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
