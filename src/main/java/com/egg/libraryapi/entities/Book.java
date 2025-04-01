package com.egg.libraryapi.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {
    @Id
    @Column(name = "isbn")
    private Long ISBN;

    @Column(name = "specimens")
    private Integer specimens;

    @Column(name = "book_active")
    private Boolean bookActive;

    @Column(name = "book_title")
    private String bookTitle;

    @ManyToOne
    private Editorial editorial;

    @ManyToOne
    private Author author;
    
}
