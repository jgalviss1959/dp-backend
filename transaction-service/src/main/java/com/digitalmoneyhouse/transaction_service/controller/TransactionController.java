package com.digitalmoneyhouse.transaction_service.controller;

import com.digitalmoneyhouse.transaction_service.dto.TransactionDTO;
import com.digitalmoneyhouse.transaction_service.entity.Transaction;
import com.digitalmoneyhouse.transaction_service.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // GET /transactions/account/{accountId}/last5
    @GetMapping("/account/{accountId}/last5")
    public ResponseEntity<List<TransactionDTO>> getLast5Transactions(@PathVariable Long accountId) {
        List<Transaction> transactions = transactionService.getLast5TransactionsByAccountId(accountId);

        // Convertir a DTO
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(tx -> new TransactionDTO(
                        tx.getId(),
                        tx.getAccountId(),
                        tx.getAmount(),
                        tx.getDate(),
                        tx.getType()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(transactionDTOs);
    }
}
