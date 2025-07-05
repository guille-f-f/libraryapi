package com.egg.libraryapi.entities;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "author")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id_author")
    private UUID idAuthor;

    @Column(name = "author_active")
    private Boolean authorActive;

    @Column(name = "author_name")
    private String authorName;

    @PrePersist
    private void onCreate() {
        if (this.authorActive == null) {
            this.authorActive = true;
        }
    }

}
