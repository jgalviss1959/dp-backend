package com.digitalmoneyhouse.transaction_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class AccountDTO {

    private Long id;
    private String cvu;
    private BigDecimal balance;

    public AccountDTO() {}

    public AccountDTO(Long id, String cvu, BigDecimal balance) {
        this.id = id;
        this.cvu = cvu;
        this.balance = balance;
    }
}
