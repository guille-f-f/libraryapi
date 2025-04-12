package com.egg.libraryapi.entities;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Libro {
    private String titulo;
    private String descripcion;
    private String imagen;
}