package com.digitalmoneyhouse.wallet_service.client;

import com.digitalmoneyhouse.transaction_service.dto.TransactionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "transaction-service")
public interface TransactionClient {
    @GetMapping("/api/transactions/account/{accountId}/last")
    List<TransactionDTO> getLast5Transactions(@PathVariable Long accountId);
}

