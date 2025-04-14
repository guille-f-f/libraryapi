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
    
    @Column(name = "book_active")
    private Boolean bookActive;

    @Column(name = "book_title")
    private String bookTitle;
    
    @Column(name = "specimens")
    private Integer specimens;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "id_editorial", nullable = false)
    private Editorial editorial;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "id_author", nullable = false)
    private Author author;

    @PrePersist
    private void onCreate() {
        this.bookActive = true;
    }
    
}
