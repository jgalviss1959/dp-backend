package com.digitalmoneyhouse.transaction_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class TransferRequestDTO {
    private String origin;
    private String destination;
    private BigDecimal amount;
    private String name; // Opcional

    public TransferRequestDTO() {
    }

    public TransferRequestDTO(String origin, String destination, BigDecimal amount, String name) {
        this.origin = origin;
        this.destination = destination;
        this.amount = amount;
        this.name = name;
    }
}
