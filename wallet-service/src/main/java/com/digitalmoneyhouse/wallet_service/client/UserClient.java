package com.digitalmoneyhouse.wallet_service.client;

import com.digitalmoneyhouse.wallet_service.dto.UserAliasDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    // Asumimos que en user-service tienes un endpoint GET /users/{id} que devuelve la info de alias y CVU
    @GetMapping("/users/{id}")
    UserAliasDTO getUserAliasInfo(@PathVariable("id") Long userId);
}
