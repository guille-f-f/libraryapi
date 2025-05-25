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

    // ¿Qué hace este filtro en cada request?
    // Intercepta el request antes de que llegue al controlador.
    // Busca si hay un header Authorization con un JWT.
    // Si encuentra un token válido → autentica al usuario sin necesidad de login.
    // Si no hay token, simplemente pasa el control y la seguridad de Spring
    // determinará si está autorizado o no.
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        System.out.println("Ingresamos a: JwtFilter.doFilterInternal");
        // request: es el objeto HTTP que entra.
        // response: es el objeto HTTP que se devolverá.
        // filterChain: es el flujo que permite continuar con los filtros siguientes (o
        // el controlador, si ya es el final).

        // Obtiene el JWT desde el header Authorization
        // Busca el token en el header Authorization.
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        System.out.println("Header: " + authHeader);
        // Si está presente y comienza con "Bearer ", lo recorta.
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            boolean tokenIsExpired = jwtUtil.isTokenExpired(token);
            System.out.println("Token expirado: " + tokenIsExpired);
            // Extrae el username del token usando jwtUtil.
            username = jwtUtil.extractUsername(token);
        }

        System.out.println("Token: " + token + "\nUsername: " + username);

        // Si hay un username y el usuario aún no está autenticado
        // Verifica que el token contenía un usuario.
        // Asegura que aún no haya nadie autenticado en el contexto de seguridad para
        // evitar sobreescribir algo que ya esté.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Carga al usuario y valida el token
            // Usa userDetailsService para traer los datos del usuario (roles, permisos).
            // Valida que el token no esté vencido y sea correcto.
            // Si es válido:
            // Crea un objeto UsernamePasswordAuthenticationToken, que representa a un
            // usuario autenticado.
            // Lo guarda en el SecurityContextHolder → ahora Spring sabe que el usuario está
            // logueado.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.isTokenValid(token, userDetails)) {
                String role = jwtUtil.extractRole(token);

                // 🔁 Convertir a una lista de GrantedAuthority
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, authorities);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Context: " + SecurityContextHolder.getContext().getAuthentication());
            }
        }

        // Continúa con el resto de filtros o el controlador
        filterChain.doFilter(request, response);
    }
}
