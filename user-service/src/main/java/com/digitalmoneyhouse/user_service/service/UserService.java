package com.digitalmoneyhouse.user_service.service;

import com.digitalmoneyhouse.user_service.dto.LoginDTO;
import com.digitalmoneyhouse.user_service.dto.UserDTO;
import com.digitalmoneyhouse.user_service.entity.User;
import com.digitalmoneyhouse.user_service.exception.MissingRequiredFieldsException;
import com.digitalmoneyhouse.user_service.exception.UserNotFoundException;
import com.digitalmoneyhouse.user_service.exception.EmailAlreadyExistsException;
import com.digitalmoneyhouse.user_service.exception.InvalidCredentialsException;
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

    public User registerUser(UserDTO userDTO) {
        validateRequiredFields(userDTO);

        // 🔹 Validar si el email ya existe
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("El email ya está registrado.");
        }

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

    private void validateRequiredFields(UserDTO userDTO) {
        if (userDTO.getNombreApellido() == null || userDTO.getNombreApellido().trim().isEmpty() ||
                userDTO.getDni() == null || userDTO.getDni().trim().isEmpty() ||
                userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty() ||
                userDTO.getTelefono() == null || userDTO.getTelefono().trim().isEmpty() ||
                userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            throw new MissingRequiredFieldsException("Faltan datos obligatorios. Verifique que todos los campos estén completos.");
        }
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
    }

    public String login(LoginDTO loginDTO) {
        Optional<User> userOptional = userRepository.findByEmail(loginDTO.getEmail());

        if (userOptional.isEmpty() || !passwordEncoder.matches(loginDTO.getPassword(), userOptional.get().getPassword())) {
            throw new InvalidCredentialsException("Email o contraseña incorrectos.");
        }

        return jwtUtil.generateToken(loginDTO.getEmail());
    }

    private String generateCVU() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 22);
    }

    private String generateAlias() {
        return UUID.randomUUID().toString().substring(0, 3) + "." +
                UUID.randomUUID().toString().substring(4, 7) + "." +
                UUID.randomUUID().toString().substring(8, 11);
    }
}
