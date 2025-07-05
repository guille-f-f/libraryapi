package com.egg.libraryapi.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.egg.libraryapi.entities.RefreshToken;
import com.egg.libraryapi.entities.User;
import com.egg.libraryapi.exceptions.ObjectNotFoundException;
import com.egg.libraryapi.models.AuthRequestDTO;
import com.egg.libraryapi.models.LoginResultDTO;
import com.egg.libraryapi.repositories.RefreshTokenRepository;
import com.egg.libraryapi.repositories.UserRepository;
import com.egg.libraryapi.utils.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private CustomUserDetailsService userDetailsService;

    @Autowired
    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService,
            RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // Login
    @Transactional
    public LoginResultDTO loginService(AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getRole().name());

        RefreshToken refreshTokenDDBB = new RefreshToken();
        refreshTokenDDBB.setRefreshToken(refreshToken);
        refreshTokenDDBB.setUser(user);
        refreshTokenDDBB.setExpiryDate(jwtUtil.extractExpiration(refreshToken).toInstant());

        refreshTokenRepository.save(refreshTokenDDBB);

        return new LoginResultDTO(accessToken, refreshToken);
    }

    // Logout
    @Transactional
    public void logout(String refreshToken) {
        SecurityContextHolder.clearContext();
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }

    // Register
    @Transactional
    public ResponseEntity<Map<String, String>> registerService(AuthRequestDTO request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "The user already exists"));
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Successfully registered user"));
    }

    // Refresh token
    @Transactional(readOnly = true)
    public ResponseEntity<?> refreshAccessTokenService(String requestRefreshToken) {
        if (requestRefreshToken == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token is null"));
        }

        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByRefreshToken(requestRefreshToken);

        if (refreshTokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh token not found"));
        }

        RefreshToken refreshToken = refreshTokenOpt.get();
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token expired"));
        }

        try {
            String username = null;
            try {
                username = jwtUtil.extractUsername(refreshToken.getRefreshToken());
            } catch (ExpiredJwtException e) {
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                        .body(Map.of("error", "Token expired"));
            } catch (Exception e) {
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token"));
            }

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ObjectNotFoundException("User not found"));

            if (!isStoredAndValid(user, refreshToken.getRefreshToken())) {
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                        .body(Map.of("error", "Refresh token invalid or mismatch"));
            }

            String newAccessToken = jwtUtil.generateAccessToken(
                    user.getUsername(),
                    user.getRole().name());

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));

        } catch (JwtException e) {
            System.out.println("Error parsing refresh token: " + e.getMessage());
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                    .body(Map.of("error", "Invalid token refresh"));
        }
    }

    // Validate token
    public boolean validateToken(String token) {
        return jwtUtil.isTokenValid(token, userDetailsService.loadUserByUsername(jwtUtil.extractUsername(token)));
    }

    // =======================
    // Private methods
    // =======================

    private boolean isStoredAndValid(User user, String refreshToken) {
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByUser(user);

        System.out.println("USUARIO: " + user);
        System.out.println("TOKEN ALMACENADO: " + storedToken);

        return storedToken.isPresent() &&
                storedToken.get().getRefreshToken().equals(refreshToken) &&
                storedToken.get().getExpiryDate()
                        .isAfter(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

}
