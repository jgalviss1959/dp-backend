package com.digitalmoneyhouse.wallet_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountDTO {

    private Long id;
    private BigDecimal balance;

    private String alias;
    private String cvu;

    public AccountDTO() {
    }

    public AccountDTO(Long id, BigDecimal balance, String alias, String cvu) {
        this.id = id;
        this.balance = balance;
        this.alias = alias;
        this.cvu = cvu;
    }
}
