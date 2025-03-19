package com.digitalmoneyhouse.transaction_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;
    private BigDecimal amount;
    private LocalDateTime date;
    private String type; // "DEBIT" o "CREDIT", por ejemplo

    public Transaction() {
    }

    public Transaction(Long accountId, BigDecimal amount, LocalDateTime date, String type) {
        this.accountId = accountId;
        this.amount = amount;
        this.date = date;
        this.type = type;
    }
}
