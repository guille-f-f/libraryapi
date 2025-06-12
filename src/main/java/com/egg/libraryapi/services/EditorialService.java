package com.egg.libraryapi.services;

import com.egg.libraryapi.exceptions.ObjectNotFoundException;
import com.egg.libraryapi.models.EditorialRequestDTO;
import com.egg.libraryapi.models.EditorialResponseDTO;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.egg.libraryapi.entities.Editorial;
import com.egg.libraryapi.repositories.EditorialRepository;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EditorialService {

    private EditorialRepository editorialRepository;

    public EditorialService(EditorialRepository editorialRepository) {
        this.editorialRepository = editorialRepository;
    }

    // Create
    @Transactional
    public Editorial createEditorial(EditorialRequestDTO editorialRequestDTO) {
        return editorialRepository.save(populateEditorial(new Editorial(), editorialRequestDTO.getEditorialName()));
    }

    // Read by id
    @Transactional(readOnly = true)
    public Editorial getEditorialById(UUID idEditorial) {
        return getEditorialOrThrow(idEditorial);
    }

    @Transactional(readOnly = true)
    public EditorialResponseDTO getEditorialResponseDTOById(UUID idEditorial) {
        return getEditorialResponseDTOOrThrow(idEditorial);
    }

    // Read by name
    @Transactional(readOnly = true)
    public EditorialResponseDTO getEditorialResponseDTOByName(String editorialName) {
        return getEditorialResponseDTOOrThrow(editorialName);
    }

    @Transactional(readOnly = true)
    public Editorial getEditorialByName(String editorialName) {
        return getEditorialOrThrow(editorialName);
    }

    // Read all
    @Transactional(readOnly = true)
    public List<Editorial> getAllEditorials() {
        return editorialRepository.findAll();
    }

    // Update
    @Transactional
    public Editorial updateEditorial(UUID idEditorial, EditorialRequestDTO editorialRequestDTO) {
        Editorial editorial = getEditorialOrThrow(idEditorial);
        return editorialRepository.save(populateEditorial(editorial, editorialRequestDTO.getEditorialName()));
    }

    // Delete
    public Editorial handleEditorialActivation(UUID idEditorial) {
        Editorial editorial = getEditorialOrThrow(idEditorial);
        editorial.setEditorialActive(!editorial.getEditorialActive());
        editorialRepository.save(editorial);
        return editorial;
    }

    public void deleteEditorial(UUID idEditorial) throws DataIntegrityViolationException {
        Editorial editorial = getEditorialOrThrow(idEditorial);
        editorialRepository.delete(editorial);
    }

    // Get actives editorials
    public List<EditorialResponseDTO> getActivesEditorials() {
        return editorialRepository.findEditorialsActives();
    }

    // Get inactives editorials
    public List<Editorial> getInactivesEditorials() {
        return editorialRepository.findByEditorialActiveFalse();
    }

    // =======================
    // Private methods
    // =======================

    private EditorialResponseDTO getEditorialResponseDTOOrThrow(UUID idEditorial) {
        return editorialRepository.findEditorialById(idEditorial).orElseThrow(
                () -> new ObjectNotFoundException("Editorial with id " + idEditorial + " not found."));
    }

    private EditorialResponseDTO getEditorialResponseDTOOrThrow(String editorialName) {
        return editorialRepository.findByQueryWithEditorialName(editorialName).orElseThrow(
                () -> new ObjectNotFoundException("Editorial " + editorialName + " not found."));
    }

    private Editorial getEditorialOrThrow(UUID idEditorial) {
        return editorialRepository.findById(idEditorial).orElseThrow(
                () -> new ObjectNotFoundException("Editorial with id " + idEditorial + " not found."));
    }

    private Editorial getEditorialOrThrow(String editorialName) {
        return editorialRepository.findByEditorialName(editorialName).orElseThrow(
                () -> new ObjectNotFoundException("Editorial " + editorialName + " not found."));
    }

    private Editorial populateEditorial(Editorial editorial, String editorialName) {
        editorial.setEditorialName(editorialName);
        return editorial;
    }

}