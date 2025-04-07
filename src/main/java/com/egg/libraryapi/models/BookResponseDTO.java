package com.egg.libraryapi.models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {
    private String bookTitle;
    private Integer specimens;
}
