package com.digitalmoneyhouse.transaction_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ActivityDTO {
    private Long id;
    private String type;
    private BigDecimal amount;
    private LocalDateTime date;

    public ActivityDTO(Long id, String type, BigDecimal amount, LocalDateTime date) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
