package com.digitalmoneyhouse.wallet_service.controller;

import com.digitalmoneyhouse.transaction_service.dto.TransactionDTO;
import com.digitalmoneyhouse.wallet_service.client.TransactionClient;
import com.digitalmoneyhouse.wallet_service.dto.AccountDTO;
import com.digitalmoneyhouse.wallet_service.dto.DashboardDTO;
import com.digitalmoneyhouse.wallet_service.entity.Account;
import com.digitalmoneyhouse.wallet_service.exception.AccountNotFoundException;
import com.digitalmoneyhouse.wallet_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired(required = false)
    private TransactionClient transactionClient;

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(account.getId());
        accountDTO.setBalance(account.getBalance());
        accountDTO.setAlias(account.getAlias());
        accountDTO.setCvu(account.getCvu());

        return ResponseEntity.ok(accountDTO);
    }


    @GetMapping("/dashboard/{accountId}")
    public ResponseEntity<DashboardDTO> getDashboard(@PathVariable Long accountId) {
        Account account = accountService.getAccountById(accountId);
        Double balance = account.getBalance().doubleValue();
        List<TransactionDTO> last5 = transactionClient.getLast5Transactions(accountId);

        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setId(account.getId());
        dashboard.setAlias(account.getAlias());
        dashboard.setCvu(account.getCvu());
        dashboard.setBalance(balance);
        dashboard.setLastTransactions(last5);

        return ResponseEntity.ok(dashboard);
    }


    @PutMapping("/{accountId}/deposit")
    public ResponseEntity<Void> increaseAccountBalance(@PathVariable Long accountId, @RequestBody BigDecimal amount) {
        try {
            accountService.increaseBalance(accountId, amount);
            return ResponseEntity.noContent().build();
        } catch (com.digitalmoneyhouse.wallet_service.exception.AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/balance/{accountId}")
    public ResponseEntity<Void> updateAccountBalance(@PathVariable Long accountId, @RequestBody BigDecimal newBalance) {
        try {
            accountService.updateBalance(accountId, newBalance);
            return ResponseEntity.noContent().build();
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cvu/{cvu}")
    public ResponseEntity<AccountDTO> getAccountByCvu(@PathVariable String cvu) {
        try {
            AccountDTO accountDTO = accountService.findByCvu(cvu);
            return ResponseEntity.ok(accountDTO);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}
