package com.digitalmoneyhouse.transaction_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DepositRequestDTO {
    private Long accountId;
    private String cardNumber;
    private BigDecimal amount;

    public DepositRequestDTO() {}

    public DepositRequestDTO(Long accountId, String cardNumber, BigDecimal amount) {
        this.accountId = accountId;
        this.cardNumber = cardNumber;
        this.amount = amount;
    }

}
