package com.digitalmoneyhouse.user_service.service;

import com.digitalmoneyhouse.user_service.entity.RevokedToken;
import com.digitalmoneyhouse.user_service.repository.RevokedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {

    @Autowired
    private RevokedTokenRepository revokedTokenRepository;

    public void logout(String token) {
        RevokedToken revokedToken = new RevokedToken();
        revokedToken.setToken(token);
        revokedToken.setRevokedAt(new Date());
        revokedTokenRepository.save(revokedToken);
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.findByToken(token).isPresent();
    }
}
