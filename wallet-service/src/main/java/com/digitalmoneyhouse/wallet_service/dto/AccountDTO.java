package com.digitalmoneyhouse.wallet_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountDTO {

    private Long id;
    private BigDecimal balance;

    public AccountDTO() {
    }

    public AccountDTO(Long id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }


}
