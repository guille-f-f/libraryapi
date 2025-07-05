package com.egg.libraryapi.models;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorResponseDTO {
    private UUID idAuthor;
    private String authorName;
    private boolean authorActive;
}
