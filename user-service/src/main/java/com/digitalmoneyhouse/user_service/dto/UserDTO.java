package com.digitalmoneyhouse.user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String firstName;
    private String lastName;
    private String dni;
    private String email;
    private String phone;
    private String password;

    public UserDTO() {
    }

    public UserDTO(String firstName, String lastName, String dni, String email, String phone, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }
}
