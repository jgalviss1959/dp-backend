package com.digitalmoneyhouse.api_gateway.config;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/swagger")
public class SwaggerGatewayController {

    @GetMapping("/user")
    public ResponseEntity<Void> redirectToUserSwagger() {
        return ResponseEntity.status(302)
                .header("Location", "http://localhost:8087/swagger-ui.html")
                .build();
    }

    @GetMapping("/wallet")
    public ResponseEntity<Void> redirectToWalletSwagger() {
        return ResponseEntity.status(302)
                .header("Location", "http://localhost:8086/swagger-ui.html")
                .build();
    }

    @GetMapping("/transaction")
    public ResponseEntity<Void> redirectToTransactionSwagger() {
        return ResponseEntity.status(302)
                .header("Location", "http://localhost:8085/swagger-ui.html")
                .build();
    }
}
