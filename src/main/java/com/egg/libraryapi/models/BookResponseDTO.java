package com.egg.libraryapi.models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponseDTO {
    private Long isbn;
    private Boolean bookActive;
    private String bookTitle;
    private Integer specimens;
    private String imageUrl;
    private EditorialResponseDTO editorialResponseDTO;
    private AuthorResponseDTO authorResponseDTO;
}
