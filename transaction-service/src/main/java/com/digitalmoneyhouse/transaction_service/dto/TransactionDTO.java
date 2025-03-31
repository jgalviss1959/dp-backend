package com.digitalmoneyhouse.transaction_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
public class TransactionDTO {

    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private LocalDateTime date;
    private String type;

    public TransactionDTO() {
    }

    public TransactionDTO(Long id, Long accountId, BigDecimal amount, LocalDateTime date, String type) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.date = date;
        this.type = type;
    }

}
