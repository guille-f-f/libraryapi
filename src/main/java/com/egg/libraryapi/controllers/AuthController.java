package com.egg.libraryapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.egg.libraryapi.models.AuthRequestDTO;
import com.egg.libraryapi.services.AuthService;

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
        System.out.println("Login request: " + request);
        return authService.loginService(request);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequestDTO request) {
        return authService.registerService(request);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken() {
        System.out.println("Ingresamos al controlador: validateToken()");
        return ResponseEntity.ok(true);
    }
}
