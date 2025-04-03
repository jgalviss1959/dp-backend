package com.digitalmoneyhouse.wallet_service.service;

import com.digitalmoneyhouse.wallet_service.client.UserClient;
import com.digitalmoneyhouse.wallet_service.dto.AccountDTO;
import com.digitalmoneyhouse.wallet_service.dto.UserAliasDTO;
import com.digitalmoneyhouse.wallet_service.entity.Account;
import com.digitalmoneyhouse.wallet_service.exception.AccountNotFoundException;
import com.digitalmoneyhouse.wallet_service.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserClient userClient;

    public Account getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        UserAliasDTO userInfo = userClient.getUserAliasInfo(id);
        account.setCvu(userInfo.getCvu());
        account.setAlias(userInfo.getAlias());

        return account;
    }

    public Double getBalance(Long accountId) {
        Account account = getAccountById(accountId);
        return account.getBalance().doubleValue();
    }

    @Transactional
    public void updateBalance(Long accountId, BigDecimal newBalance) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    @Transactional
    public void increaseBalance(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        BigDecimal currentBalance = account.getBalance();
        account.setBalance(currentBalance.add(amount));
        accountRepository.save(account);
    }

    public AccountDTO findByCvu(String cvu) {
        return accountRepository.findByCvu(cvu)
                .map(account -> new AccountDTO(account.getId(), account.getBalance(), account.getAlias(), account.getCvu()))
                .orElseThrow(() -> new AccountNotFoundException("Cuenta con CVU " + cvu + " no encontrada"));
    }

}
