package com.digitalmoneyhouse.transaction_service.config;

import com.digitalmoneyhouse.transaction_service.dto.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(name = "wallet-service")
public interface WalletClient {

    @PutMapping("/api/accounts/{accountId}/balance")
    void updateAccountBalance(@PathVariable Long accountId, @RequestBody BigDecimal newBalance);

    @PutMapping("/api/accounts/{accountId}/deposit")
    void increaseAccountBalance(@PathVariable("accountId") Long accountId, @RequestBody BigDecimal amount);

    @GetMapping("/api/accounts/{accountId}/balance")
    BigDecimal getAccountBalance(@PathVariable Long accountId);

    @GetMapping("/api/accounts/cvu/{cvu}")
    AccountDTO getAccountByCvu(@PathVariable String cvu);

}