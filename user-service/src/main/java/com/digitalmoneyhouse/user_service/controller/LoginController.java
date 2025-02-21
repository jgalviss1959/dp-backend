package com.digitalmoneyhouse.user_service.controller;

import com.digitalmoneyhouse.user_service.dto.LoginDTO;
import com.digitalmoneyhouse.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return ResponseEntity.ok(token);
    }
}
