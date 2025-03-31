package com.digitalmoneyhouse.transaction_service.dto;

import java.math.BigDecimal;

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

    public Long getAccountId() {
        return accountId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
