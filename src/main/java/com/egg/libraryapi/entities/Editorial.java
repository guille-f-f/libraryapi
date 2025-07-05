package com.egg.libraryapi.entities;

import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "editorial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Editorial {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id_editorial", updatable = false, nullable = false)
    private UUID idEditorial;

    @Column(name = "editorial_active")
    private Boolean editorialActive;

    @Column(name = "editorial_name", unique = true)
    private String editorialName;

    @PrePersist
    private void onCreate() {
        if (this.editorialActive == null) {
            this.editorialActive = true;
        }
    };
}
