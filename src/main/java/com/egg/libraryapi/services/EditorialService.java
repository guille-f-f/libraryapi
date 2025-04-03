package com.egg.libraryapi.services;

import com.egg.libraryapi.exceptions.ObjectNotFoundException;
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
        return editorial;
    }

    // Get active Editorials
    public List<Editorial> getActiveEditorials() {
        return editorialRepository.getActiveEditorials();
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
        editorial.setEditorialActive(true);
        return editorial;
    }

}