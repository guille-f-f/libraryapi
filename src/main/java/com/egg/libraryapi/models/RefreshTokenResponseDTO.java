package com.egg.libraryapi.models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenResponseDTO {
    private String accessToken;
}