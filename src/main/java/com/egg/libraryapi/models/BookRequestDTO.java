package com.egg.libraryapi.models;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDTO {
    private Long isbn;
    private Integer specimens;
    @NotBlank(message = "El título no puede estar vacío")
    @Size(min = 5, max = 100, message = "El tamaño del texto debe estar entre 5 y 10 caracteres.")
    private String bookTitle;
    private UUID idEditorial;
    private UUID idAuthor;
}