package com.digitalmoneyhouse.api_gateway.security;

import com.digitalmoneyhouse.api_gateway.client.TokenClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    @Value("${jwt.secret}")
    private String secretKey;

    @Lazy
    @Autowired
    private TokenClient tokenClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        System.out.println(">>> Path is: " + path);

        if (path.startsWith("/api/auth/login") ||
                path.startsWith("/api/users/register") ||
                path.startsWith("/api/auth/logout") ||
                path.startsWith("/v3/api-docs/**") ||
                path.startsWith("/swagger-ui/**") ||
                path.startsWith("swagger-ui.html")) {
            return chain.filter(exchange);
        }

        // Verificar que exista el header Authorization
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Extraer el header Authorization
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        final String token = (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7)
                : null;


        // Validar la firma y expiración del token
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.out.println("🚨 Error validando JWT (firma/expiración): " + e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Usar Feign para verificar si el token está revocado
        return Mono.fromCallable(() -> tokenClient.isTokenRevoked(token))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(isRevoked -> {
                    if (Boolean.TRUE.equals(isRevoked)) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    } else {
                        // Reestablecer el header Authorization en la request:
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header(HttpHeaders.AUTHORIZATION, authHeader)
                                .build();

                        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                        return chain.filter(mutatedExchange);
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Error al verificar revocación del token: " + e.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }


    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

//@Component
//public class JwtAuthenticationFilter implements GlobalFilter {
//
//    @Value("${jwt.secret}")
//    private String secretKey;
//
//    @Lazy
//    @Autowired
//    private TokenClient tokenClient;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        String path = request.getURI().getPath();
//        System.out.println(">>> Path is: " + path);
//
//        if (path.startsWith("/api/auth/login") ||
//                path.startsWith("/api/users/register") ||
//                path.startsWith("/api/auth/logout") ||
//                path.startsWith("/v3/api-docs/**") ||
//                path.startsWith("/swagger-ui/**") ||
//                path.startsWith("swagger-ui.html")) {
//            return chain.filter(exchange);
//        }
//
//        // Verificar que exista el header Authorization
//        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        // Extraer el header Authorization
//        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        final String token = (authHeader != null && authHeader.startsWith("Bearer "))
//                ? authHeader.substring(7)
//                : null;
//
//        if (token == null) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        // Validar la firma y expiración del token
//        Claims claims;
//        try {
//        claims = Jwts.parserBuilder()
//        .setSigningKey(getSigningKey())
//        .build()
//        .parseClaimsJws(token)
//        .getBody();
//        } catch (Exception e) {
//        System.out.println("🚨 Error validando JWT: " + e.getMessage());
//        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//        return exchange.getResponse().setComplete();
//        }
//
//        // Usar Feign para verificar si el token está revocado
//        return Mono.fromCallable(() -> tokenClient.isTokenRevoked(token))
//                .subscribeOn(Schedulers.boundedElastic())
//                .flatMap(isRevoked -> {
//                    if (Boolean.TRUE.equals(isRevoked)) {
//                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                        return exchange.getResponse().setComplete();
//                    } else {
//                        // Reestablecer el header Authorization en la request:
//                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
//                                .header(HttpHeaders.AUTHORIZATION, authHeader)
//                                .build();
//
//                        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
//                        return chain.filter(mutatedExchange);
//                    }
//                })
//                .onErrorResume(e -> {
//                    System.err.println("Error al verificar revocación del token: " + e.getMessage());
//                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                    return exchange.getResponse().setComplete();
//                });
//    }
//
//    private Key getSigningKey() {
//        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//}

//@Component
//public class JwtAuthenticationFilter implements GlobalFilter {
//
//    @Value("${jwt.secret}")
//    private String secretKey;
//
//    private final TokenClient tokenClient;
//
//
//    public JwtAuthenticationFilter(@Lazy TokenClient tokenClient) {
//        this.tokenClient = tokenClient;
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        String path = request.getURI().getPath();
//        System.out.println(">>> Path is: " + path);
//
//        if (path.startsWith("/api/auth/login") ||
//                path.startsWith("/api/users/register") ||
//                path.startsWith("/api/auth/logout") ||
//                path.startsWith("/v3/api-docs/**") ||
//                path.startsWith("/swagger-ui/**") ||
//                path.startsWith("swagger-ui.html"))
//        {
//            return chain.filter(exchange);
//        }
//
//        // Verificar que exista el header Authorization
//        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        // Extraer el header Authorization y procesarlo en una variable final
//        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        final String token = (authHeader != null && authHeader.startsWith("Bearer "))
//                ? authHeader.substring(7)
//                : authHeader;
//
//        // Validar la firma y expiración del token
//        Claims claims;
//        try {
//            claims = Jwts.parserBuilder()
//                    .setSigningKey(getSigningKey())
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//        } catch (Exception e) {
//            System.out.println("🚨 Error validando JWT: " + e.getMessage());
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                .build();
//        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
//        return chain.filter(mutatedExchange);
//    }
//
//    private Key getSigningKey() {
//        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//
//}