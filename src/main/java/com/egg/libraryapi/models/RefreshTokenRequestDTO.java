package com.egg.libraryapi.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenRequestDTO {
    private String refreshToken;
}
