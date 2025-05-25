package com.egg.libraryapi.services;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.egg.libraryapi.entities.User;
import com.egg.libraryapi.models.AuthRequestDTO;
import com.egg.libraryapi.models.AuthResponseDTO;
import com.egg.libraryapi.repositories.UserRepository;
import com.egg.libraryapi.utils.JwtUtil;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private CustomUserDetailsService userDetailsService;

    @Autowired
    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    // Login
    public ResponseEntity<?> loginService(AuthRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        User user = userOpt.get();

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getRole().name()); // El de larga
                                                                                                       // duración

        // AuthResponseDTO response = new AuthResponseDTO(token, "Login exitoso");
        AuthResponseDTO response = new AuthResponseDTO(accessToken, refreshToken, "Login exitoso");
        return ResponseEntity.ok(response);
    }

    // Register
    public ResponseEntity<?> registerService(AuthRequestDTO request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("The user already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return ResponseEntity.ok("Successfully registered user");
    }

    // Refresh token
    public ResponseEntity<?> refreshAccessTokenService(String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh token requerido");
        }

        try {
            System.out.println("refreshAccessTokenService: Refresh Token " + refreshToken);
            String username = jwtUtil.extractUsername(refreshToken);

            if (jwtUtil.isTokenExpired(refreshToken)) {
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Refresh token expirado");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String newAccessToken = jwtUtil.generateAccessToken(userDetails.getUsername(),
                    userDetails.getAuthorities().iterator().next().getAuthority());
            return ResponseEntity.ok(Map.of(
                    "accessToken", newAccessToken));

        } catch (JwtException e) {
            System.out.println("Error parseando refresh token: " + e.getMessage());
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Refresh token inválido");
        }
    }

}
