package com.egg.libraryapi.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.egg.libraryapi.entities.Editorial;

@Repository
public interface EditorialRepository extends JpaRepository<Editorial, UUID> {

}