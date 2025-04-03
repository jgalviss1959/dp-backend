package com.digitalmoneyhouse.user_service.service;

import com.digitalmoneyhouse.user_service.dto.LoginDTO;
import com.digitalmoneyhouse.user_service.dto.UserDTO;
import com.digitalmoneyhouse.user_service.dto.UserUpdateDTO;
import com.digitalmoneyhouse.user_service.entity.User;
import com.digitalmoneyhouse.user_service.exception.EmailAlreadyExistsException;
import com.digitalmoneyhouse.user_service.exception.InvalidCredentialsException;
import com.digitalmoneyhouse.user_service.exception.MissingRequiredFieldsException;
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

    public User registerUser(UserDTO userDTO) {
        System.out.println(userDTO.getFirstName() + " nombre");
        System.out.println(userDTO.getLastName() + " apellido");
        System.out.println(userDTO.getPhone() + " tel");
        System.out.println(userDTO.getDni()+" dni");
        System.out.println(userDTO.getEmail()+" mail");
        System.out.println(userDTO.getPassword()+" pwd");
        validateRequiredFields(userDTO);

        // 🔹 Validar si el email ya existe
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("El email ya está registrado.");
        }

        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setDni(userDTO.getDni());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setCvu(generateCVU());
        user.setAlias(generateAlias());

        return userRepository.save(user);
    }

    private void validateRequiredFields(UserDTO userDTO) {
        if (userDTO.getFirstName() == null || userDTO.getFirstName().trim().isEmpty() ||
                userDTO.getLastName() == null || userDTO.getLastName().trim().isEmpty() ||
                userDTO.getDni() == null || userDTO.getDni().trim().isEmpty() ||
                userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty() ||
                userDTO.getPhone() == null || userDTO.getPhone().trim().isEmpty() ||
                userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {

            throw new MissingRequiredFieldsException("Faltan datos obligatorios. Verifique que todos los campos estén completos.");
        }
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
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

    public void updateUser(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario con ID " + id + " no encontrado"));

        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());

        userRepository.save(user);
    }

}
