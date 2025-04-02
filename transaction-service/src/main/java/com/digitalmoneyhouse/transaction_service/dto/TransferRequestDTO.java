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
}
