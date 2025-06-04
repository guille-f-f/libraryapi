package com.egg.libraryapi.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResultDTO {
    private String accessToken;
    private String refreshToken;
}
