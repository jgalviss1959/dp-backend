package com.digitalmoneyhouse.wallet_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal balance;

    private String alias;

    private String cvu;

    public Account() {
    }

    public Account(BigDecimal balance) {
        this.balance = balance;
    }

}
