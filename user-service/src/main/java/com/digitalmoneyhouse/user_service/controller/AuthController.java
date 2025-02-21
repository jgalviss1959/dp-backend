package com.digitalmoneyhouse.user_service.controller;

import com.digitalmoneyhouse.user_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Token no proporcionado o formato incorrecto");
            }

            token = token.substring(7);
            authService.logout(token);
            return ResponseEntity.ok("Logout exitoso. Token invalidado.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno al cerrar sesión");
        }
    }
}
