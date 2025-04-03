package com.digitalmoneyhouse.user_service.controller;

import com.digitalmoneyhouse.user_service.dto.UserDTO;
import com.digitalmoneyhouse.user_service.dto.UserUpdateDTO;
import com.digitalmoneyhouse.user_service.entity.User;
import com.digitalmoneyhouse.user_service.service.UserService;
import com.digitalmoneyhouse.wallet_service.dto.UserAliasDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Operaciones relacionadas a usuarios")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Registra un usuario nuevo validando campos obligatorios y genera CVU y alias únicos."
    )
    public ResponseEntity<User> registerUser(@RequestBody UserDTO userDTO) {
        logger.info("Se recibió una solicitud de registro: {}", userDTO);
        User registeredUser = userService.registerUser(userDTO);
        return ResponseEntity.ok(registeredUser);
    }


    @GetMapping("/email/{email}")
    @Operation(
            summary = "Buscar usuario por email",
            description = "Devuelve los datos del usuario que coincide con el email especificado."
    )
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        logger.info("Buscando usuario con email: {}", email);
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/id/{id}")
    @Operation(
            summary = "Buscar usuario por ID",
            description = "Obtiene los datos del usuario con el ID especificado."
    )
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        logger.info("Buscando usuario con ID: {}", id);
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/alias")
    @Operation(
            summary = "Obtener CVU y alias del usuario",
            description = "Retorna el alias y el CVU asociados a un usuario según su ID."
    )
    public ResponseEntity<UserAliasDTO> getUserAlias(@PathVariable Long id) {
        logger.info("Obteniendo alias y CVU para usuario con ID: {}", id);
        User user = userService.getUserById(id);
        UserAliasDTO userAliasDTO = new UserAliasDTO();
        userAliasDTO.setId(user.getId());
        userAliasDTO.setAlias(user.getAlias());
        userAliasDTO.setCvu(user.getCvu());
        return ResponseEntity.ok(userAliasDTO);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza los campos editables (tel y email) del usuario con el ID especificado."
    )
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO updateDTO) {
        logger.info("Actualizando usuario con ID: {}", id);
        userService.updateUser(id, updateDTO);
        return ResponseEntity.ok("Perfil actualizado con éxito");
    }

}
