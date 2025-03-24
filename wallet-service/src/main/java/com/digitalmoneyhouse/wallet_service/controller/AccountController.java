package com.digitalmoneyhouse.wallet_service.controller;

import com.digitalmoneyhouse.transaction_service.dto.TransactionDTO;
import com.digitalmoneyhouse.wallet_service.client.TransactionClient;
import com.digitalmoneyhouse.wallet_service.dto.AccountDTO;
import com.digitalmoneyhouse.wallet_service.dto.DashboardDTO;
import com.digitalmoneyhouse.wallet_service.entity.Account;
import com.digitalmoneyhouse.wallet_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
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


}
