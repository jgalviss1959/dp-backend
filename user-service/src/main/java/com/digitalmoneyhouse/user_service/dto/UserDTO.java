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
}
