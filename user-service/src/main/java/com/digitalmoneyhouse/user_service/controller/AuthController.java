package com.digitalmoneyhouse.user_service.controller;

import com.digitalmoneyhouse.user_service.exception.InvalidTokenException;
import com.digitalmoneyhouse.user_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new InvalidTokenException("Token no proporcionado o formato incorrecto");
        }

        authService.logout(token.substring(7));
        return ResponseEntity.ok().body("Logout exitoso. Token invalidado.");
    }

    @GetMapping("/isRevoked/{token}")
    public boolean isRevoked(@PathVariable String token) {
        return authService.isTokenRevoked(token);
    }

}
