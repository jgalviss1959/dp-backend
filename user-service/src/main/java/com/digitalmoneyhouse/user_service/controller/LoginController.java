package com.digitalmoneyhouse.user_service.controller;

import com.digitalmoneyhouse.user_service.dto.LoginDTO;
import com.digitalmoneyhouse.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Login", description = "Manejo de inicio de sesión de usuarios")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica a un usuario mediante email y contraseña, y retorna un token JWT si es exitoso."
    )
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {
        logger.info("Intentando iniciar sesión para el email: {}", loginDTO.getEmail());
        String token = userService.login(loginDTO);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}
