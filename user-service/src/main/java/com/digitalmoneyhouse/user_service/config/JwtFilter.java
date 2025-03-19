//package com.digitalmoneyhouse.user_service.config;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import com.digitalmoneyhouse.user_service.service.AuthService;
//
//import java.io.IOException;
//import java.security.Key;
//
//@Component
//public class JwtFilter extends OncePerRequestFilter {
//
//    @Value("${jwt.secret}")
//    private String secretKey;
//
//    private final UserDetailsService userDetailsService;
//    private final AuthService authService; // Servicio que verifica la lista negra
//
//    public JwtFilter(UserDetailsService userDetailsService, AuthService authService) {
//        this.userDetailsService = userDetailsService;
//        this.authService = authService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        // Ignorar rutas públicas (login y registro)
//        String uri = request.getRequestURI();
//        if (uri.equals("/api/auth/login") || uri.equals("/api/users/register")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        // Extraer el token del header Authorization
//        String token = extractToken(request);
//        if (token != null) {
//            // Primero, verificar si el token está revocado (lista negra)
//            if (authService.isTokenRevoked(token)) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("Token inválido o revocado");
//                return;
//            }
//
//            // Luego, validar la firma y la expiración para obtener el username
//            try {
//                Claims claims = Jwts.parserBuilder()
//                        .setSigningKey(getSigningKey())
//                        .build()
//                        .parseClaimsJws(token)
//                        .getBody();
//                String username = claims.getSubject();
//                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                UsernamePasswordAuthenticationToken authenticationToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            } catch (Exception e) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                return;
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private String extractToken(HttpServletRequest request) {
//        String header = request.getHeader("Authorization");
//        if (header != null && header.startsWith("Bearer ")) {
//            return header.substring(7);
//        }
//        return null;
//    }
//
//    private Key getSigningKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//}
