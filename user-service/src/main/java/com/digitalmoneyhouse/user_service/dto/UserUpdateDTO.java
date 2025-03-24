package com.digitalmoneyhouse.user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserUpdateDTO {
    private String email;
    private String phone;

    public UserUpdateDTO() {
    }

    public UserUpdateDTO(String email, String phone) {
        this.email = email;
        this.phone = phone;
    }
}

