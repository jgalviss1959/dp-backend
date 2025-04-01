package com.digitalmoneyhouse.api_gateway.client;

import com.digitalmoneyhouse.api_gateway.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", configuration = FeignClientConfig.class)
public interface TokenClient {
    @GetMapping("/api/auth/isRevoked/{token}")
    Boolean isTokenRevoked(@PathVariable("token") String token);
}
