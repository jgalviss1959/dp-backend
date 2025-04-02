package com.digitalmoneyhouse.wallet_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(Long id) {
        super("Cuenta no encontrada con id: " + id);
    }

    public AccountNotFoundException(String cvu) {
        super("Cuenta con CVU " + cvu + " no encontrada.");
    }
}

