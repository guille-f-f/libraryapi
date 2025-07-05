package com.egg.libraryapi.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.egg.libraryapi.services.CustomUserDetailsService;
import com.egg.libraryapi.utils.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // @Override
    // protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull
    // HttpServletResponse response,
    // @NonNull FilterChain filterChain) throws ServletException, IOException {
    // System.out.println("Ingresamos a: JwtFilter.doFilterInternal");

    // String authHeader = request.getHeader("Authorization");
    // System.out.println("Header: " + authHeader);

    // String accessToken = null;
    // String username = null;

    // String path = request.getRequestURI();

    // if (path.startsWith("/auth/") || path.startsWith("/uploads/")) {
    // filterChain.doFilter(request, response);
    // return;
    // }

    // if (authHeader != null && authHeader.startsWith("Bearer ")) {
    // accessToken = authHeader.substring(7);
    // try {
    // username = jwtUtil.extractUsername(accessToken);
    // } catch (ExpiredJwtException e) {
    // System.out.println("EL TOKEN SE EXPIRO");
    // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    // response.setContentType("application/json");
    // response.getWriter().write("{\"error\": \"Token expired\"}");
    // return;
    // } catch (Exception e) {
    // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    // response.setContentType("application/json");
    // response.getWriter().write("{\"error\": \"Invalid token\"}");
    // return;
    // }
    // }

    // System.out.println("Token: " + accessToken + "\nUsername: " + username);

    // if (username == null && !path.startsWith("/auth/") &&
    // !path.startsWith("/uploads/")) {
    // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    // response.setContentType("application/json");
    // response.getWriter().write("{\"error\": \"Missing or invalid token\"}");
    // return;
    // }

    // if (username != null &&
    // SecurityContextHolder.getContext().getAuthentication() == null) {
    // UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    // if (jwtUtil.isTokenValid(accessToken, userDetails)) {
    // String role = jwtUtil.extractRole(accessToken);
    // List<GrantedAuthority> authorities = List.of(new
    // SimpleGrantedAuthority(role));
    // UsernamePasswordAuthenticationToken authToken = new
    // UsernamePasswordAuthenticationToken(userDetails,
    // null, authorities);
    // authToken.setDetails(new
    // WebAuthenticationDetailsSource().buildDetails(request));
    // SecurityContextHolder.getContext().setAuthentication(authToken);
    // System.out.println("Context: " +
    // SecurityContextHolder.getContext().getAuthentication());
    // }
    // }

    // // Continúa con el resto de filtros o el controlador
    // filterChain.doFilter(request, response);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Ingresamos a: JwtFilter.doFilterInternal");

        String authHeader = request.getHeader("Authorization");
        String accessToken = null;
        String username = null;

        String path = request.getRequestURI();
        System.out.println("Header: " + authHeader + " | Path: " + path);

        // Rutas públicas (permitidas sin token)
        if (path.startsWith("/auth/") || path.startsWith("/uploads/") || isBookPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            System.out.println("Habilitamos la request...");
            return;
        }

        // Si la ruta requiere autenticación, validamos el token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(accessToken);
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token expired\"}");
                return;
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid token\"}");
                return;
            }
        } else {
            // Si la ruta protegida no tiene token, bloqueamos
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Missing or invalid token\"}");
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.isTokenValid(accessToken, userDetails)) {
                String role = jwtUtil.extractRole(accessToken);
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Context: " + SecurityContextHolder.getContext().getAuthentication());
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isBookPublicEndpoint(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();

        // GET /books y GET /books/{isbn} son públicos
        if (method.equalsIgnoreCase("GET") && (path.equals("/books") || path.matches("/books/\\d+"))) {
            System.out.println("Es una ruta de book publica...");
            return true;
        }

        return false;
    }
}
