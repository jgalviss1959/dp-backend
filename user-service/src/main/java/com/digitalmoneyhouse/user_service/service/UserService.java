package com.digitalmoneyhouse.user_service.service;

import com.digitalmoneyhouse.user_service.dto.LoginDTO;
import com.digitalmoneyhouse.user_service.dto.UserDTO;
import com.digitalmoneyhouse.user_service.entity.User;
import com.digitalmoneyhouse.user_service.exception.UserNotFoundException;
import com.digitalmoneyhouse.user_service.repository.UserRepository;
import com.digitalmoneyhouse.user_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public User registerUser(UserDTO userDTO) {
        User user = new User();
        user.setNombreApellido(userDTO.getNombreApellido());
        user.setDni(userDTO.getDni());
        user.setEmail(userDTO.getEmail());
        user.setTelefono(userDTO.getTelefono());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setCvu(generateCVU());
        user.setAlias(generateAlias());

        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
    }

    private String generateCVU() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 22);
    }

    private String generateAlias() {
        return UUID.randomUUID().toString().substring(0, 3) + "." +
                UUID.randomUUID().toString().substring(4, 7) + "." +
                UUID.randomUUID().toString().substring(8, 11);
    }

    public String login(LoginDTO loginDTO) {
        Optional<User> userOptional = userRepository.findByEmail(loginDTO.getEmail());

        if (userOptional.isEmpty() || !passwordEncoder.matches(loginDTO.getPassword(), userOptional.get().getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return jwtUtil.generateToken(loginDTO.getEmail());
    }

}
