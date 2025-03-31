package com.digitalmoneyhouse.api_gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8087")
public interface TokenClient {
    @GetMapping("/api/auth/isRevoked/{token}")
    Boolean isTokenRevoked(@PathVariable("token") String token);
}
