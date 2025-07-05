package com.egg.libraryapi.controllers;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.egg.libraryapi.models.AuthRequestDTO;
import com.egg.libraryapi.models.AuthResponseDTO;
import com.egg.libraryapi.models.LoginResultDTO;
import com.egg.libraryapi.services.AuthService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        try {
            LoginResultDTO loginResult = authService.loginService(request);

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", loginResult.getRefreshToken())
                    .httpOnly(true)
                    .secure(false) // cambiar a true en producción
                    .path("/")
                    .maxAge(Duration.ofDays(7))
                    .sameSite("Lax")
                    .build();

            AuthResponseDTO response = new AuthResponseDTO(loginResult.getAccessToken(), "Successful login");

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(response);

        } catch (AuthenticationException e) {
            if (e instanceof UsernameNotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(0) // <- esto la elimina
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Session closed successfully");
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody AuthRequestDTO request) {
        return authService.registerService(request);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@CookieValue(name = "refreshToken") String refreshToken) {
        return authService.refreshAccessTokenService(refreshToken);
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (authService.validateToken(token)) {
                    return ResponseEntity.ok(Map.of("valid", true));
                } else {
                    return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
                }
            } catch (ExpiredJwtException e) {
                System.out.println("Expired token: " + e.getMessage());
                return ResponseEntity.status(401).body(Map.of("error", "Token expired"));
            } catch (JwtException e) {
                System.out.println("Invalid token: " + e.getMessage());
                return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
            }
        }

        return ResponseEntity.status(400).body(Map.of("error", "Authorization header is missing or malformed"));
    }

}
