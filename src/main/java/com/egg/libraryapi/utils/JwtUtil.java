package com.egg.libraryapi.utils;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenExpiration;

    public String generateAccessToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String generateRefreshToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        System.out.println("Ingresamos a: JwtUtil.isTokenValid");
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        System.out.println("Ingresamos a: JwtUtil.isTokenExpired");
        try {
            Date expiration = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            boolean isExpired = expiration.before(new Date());

            if (isExpired) {
                System.out.println("Token expirado");
            } else {
                System.out.println("Token NO expirado");
            }

            return isExpired;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.out.println("Token realmente expirado (excepción lanzada)");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Error en el token: " + e.getMessage());
            return true; // Tratamos todo token inválido como expirado o inválido
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isRefreshTokenValid(String refreshToken, UserDetails userDetails) {
        try {
            return extractUsername(refreshToken).equals(userDetails.getUsername())
                    && !isTokenExpired(refreshToken);
        } catch (JwtException e) {
            return false;
        }
    }
}
