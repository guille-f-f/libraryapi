package com.egg.libraryapi.models;

import com.egg.libraryapi.utils.Messages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditorialRequestDTO {
    @NotBlank
    @Size(min = 5, max = 150, message = Messages.SIZE_ERROR)
    private String editorialName;
}
