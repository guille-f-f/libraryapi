package com.egg.libraryapi.models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponseDTO {
    private String bookTitle;
    private Integer specimens;
}
