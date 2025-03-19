package com.digitalmoneyhouse.wallet_service.controller;

import com.digitalmoneyhouse.wallet_service.dto.AccountDTO;
import com.digitalmoneyhouse.wallet_service.entity.Account;
import com.digitalmoneyhouse.wallet_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        AccountDTO accountDTO = new AccountDTO(account.getId(), account.getBalance());
        return ResponseEntity.ok(accountDTO);
    }
}
