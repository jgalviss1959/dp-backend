package com.digitalmoneyhouse.transaction_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ActivityDTO {
    private Long id;
    private String type;
    private BigDecimal amount;
    private LocalDateTime date;

    public ActivityDTO() {
    }

    public ActivityDTO(Long id, String type, BigDecimal amount, LocalDateTime date) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

}
