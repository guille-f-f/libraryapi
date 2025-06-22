package com.egg.libraryapi.models;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDTO {
    private Long isbn;
    private Boolean bookActive;
    @NotBlank(message = "El título no puede estar vacío")
    @Size(min = 5, max = 100, message = "El tamaño del texto debe estar entre 5 y 10 caracteres.")
    private String bookTitle;
    private Integer specimens;
    private String imageUrl;
    @NotNull(message = "idEditorial is required")
    private UUID idEditorial;
    @NotNull(message = "idAuthor is required")
    private UUID idAuthor;
}