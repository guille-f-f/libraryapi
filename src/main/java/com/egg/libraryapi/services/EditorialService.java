package com.egg.libraryapi.services;

import com.egg.libraryapi.exceptions.ObjectNotFoundException;
import com.egg.libraryapi.models.EditorialRequestDTO;

import org.springframework.stereotype.Service;

import com.egg.libraryapi.entities.Editorial;
import com.egg.libraryapi.repositories.EditorialRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EditorialService {

    private EditorialRepository editorialRepository;

    public EditorialService(EditorialRepository editorialRepository) {
        this.editorialRepository = editorialRepository;
    }

    // CREATE
    @Transactional
    public Editorial createEditorial(String editorialName) {
        return editorialRepository.save(populateEditorial(new Editorial(), editorialName));
    }

    @Transactional
    public Editorial createEditorial(EditorialRequestDTO editorialRequestDTO) {
        return editorialRepository.save(populateEditorial(new Editorial(), editorialRequestDTO.getEditorialName()));
    }
    
    // READ BY ID
    @Transactional(readOnly = true)
    public Editorial getEditorialById(UUID idEditorial) {
        return getEditorialOrThrow(idEditorial);
    }

    // READ ALL
    @Transactional(readOnly = true)
    public List<Editorial> getAllEditorials() {
        return editorialRepository.findAll();
    }

    // UPDATE
    @Transactional
    public Editorial updateEditorial(UUID idEditorial, String editorialName) {
        Editorial editorial = getEditorialOrThrow(idEditorial);
        return editorialRepository.save(populateEditorial(editorial, editorialName));
    }

    // DELETE
    public Editorial handleEditorialActivation(UUID idEditorial) {
        Editorial editorial = getEditorialOrThrow(idEditorial);
        editorial.setEditorialActive(!editorial.getEditorialActive());
        editorialRepository.save(editorial);
        return editorial;
    }

    // Get actives Editorials
    public List<Editorial> getActivesEditorials() {
        return editorialRepository.findByEditorialActiveTrue();
    }

    // Get inactives Editorials
    public List<Editorial> getInactivesEditorials() {
        return editorialRepository.findByEditorialActiveFalse();
    }

    // =======================
    // Private methods
    // =======================

    private Editorial getEditorialOrThrow(UUID idEditorial) {
        return editorialRepository.findById(idEditorial).orElseThrow(
                () -> new ObjectNotFoundException("Editorial with id " + idEditorial + " not found."));
    }

    private Editorial populateEditorial(Editorial editorial, String editorialName) {
        editorial.setEditorialName(editorialName);
        return editorial;
    }

}