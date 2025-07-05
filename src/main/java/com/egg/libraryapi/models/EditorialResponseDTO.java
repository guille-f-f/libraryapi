package com.egg.libraryapi.models;

import java.util.UUID;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditorialResponseDTO {
    private UUID idEditorial;
    private String editorialName;
    private boolean editorialActive;
}
