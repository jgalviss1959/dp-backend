package com.digitalmoneyhouse.wallet_service.dto;

import com.digitalmoneyhouse.transaction_service.dto.TransactionDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class DashboardDTO {
    private Long id;
    private String alias;
    private String cvu;
    private Double balance;
    private List<TransactionDTO> lastTransactions;
}

