package com.digitalmoneyhouse.user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    private String email;
    private String password;

    public LoginDTO() {
    }

    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
