package com.egg.libraryapi.models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    private Integer id;
    private String name;
    private String image;
    private String slug;
}
