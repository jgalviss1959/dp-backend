package com.digitalmoneyhouse.api_gateway.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user-service")
                .pathsToMatch("/api/users/**", "/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi walletApi() {
        return GroupedOpenApi.builder()
                .group("wallet-service")
                .pathsToMatch("/api/accounts/**")
                .build();
    }

    @Bean
    public GroupedOpenApi transactionApi() {
        return GroupedOpenApi.builder()
                .group("transaction-service")
                .pathsToMatch("/api/transactions/**")
                .build();
    }

    @Bean
    public GroupedOpenApi paymentApi() {
        return GroupedOpenApi.builder()
                .group("payment-service")
                .pathsToMatch("/api/payments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi reportApi() {
        return GroupedOpenApi.builder()
                .group("report-service")
                .pathsToMatch("/api/reports/**")
                .build();
    }
}
