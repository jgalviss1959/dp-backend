package com.digitalmoneyhouse.user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String nombreApellido;
    private String dni;
    private String email;
    private String telefono;
    private String password;
}
