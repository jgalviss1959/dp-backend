package com.digitalmoneyhouse.user_service.controller;

import com.digitalmoneyhouse.user_service.dto.UserDTO;
import com.digitalmoneyhouse.user_service.dto.UserUpdateDTO;
import com.digitalmoneyhouse.user_service.entity.User;
import com.digitalmoneyhouse.user_service.service.UserService;
import com.digitalmoneyhouse.wallet_service.dto.UserAliasDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserDTO userDTO) {
        logger.info("🔹 Se recibió una solicitud de registro: {}", userDTO);
        User registeredUser = userService.registerUser(userDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/alias")
    public ResponseEntity<UserAliasDTO> getUserAlias(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserAliasDTO userAliasDTO = new UserAliasDTO();
        userAliasDTO.setId(user.getId());
        userAliasDTO.setAlias(user.getAlias());
        userAliasDTO.setCvu(user.getCvu());
        return ResponseEntity.ok(userAliasDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO updateDTO) {
        userService.updateUser(id, updateDTO);
        return ResponseEntity.ok("Perfil actualizado con éxito");
    }

}
