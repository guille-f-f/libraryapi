package com.egg.libraryapi.models;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Integer id;
    private String title;
    private String slug;
    private Integer price;
    private String description;
    private CategoryDTO category;
    private List<String> images;
}
