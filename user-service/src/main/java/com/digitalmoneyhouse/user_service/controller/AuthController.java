package com.digitalmoneyhouse.user_service.controller;

import com.digitalmoneyhouse.user_service.exception.InvalidTokenException;
import com.digitalmoneyhouse.user_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Manejo de tokens JWT")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Cerrar sesión",
            description = "Revoca el token JWT para impedir su reutilización futura."
    )
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            logger.warn("Token no proporcionado o con formato inválido");
            throw new InvalidTokenException("Token no proporcionado o formato incorrecto");
        }

        authService.logout(token.substring(7));
        logger.info("Deshabilitando el token");

        return ResponseEntity.ok().body("Logout exitoso. Token invalidado.");
    }

    @GetMapping("/isRevoked/{token}")
    @Operation(
            summary = "Validar token",
            description = "Revisa si el token es válido o ya fue revocado."
    )
    public boolean isRevoked(@PathVariable String token) {
        logger.debug("Verificando si el token fue revocado: {}", token);
        return authService.isTokenRevoked(token);
    }

}
