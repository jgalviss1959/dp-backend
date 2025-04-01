package com.digitalmoneyhouse.wallet_service.service;

import com.digitalmoneyhouse.wallet_service.client.UserClient;
import com.digitalmoneyhouse.wallet_service.dto.UserAliasDTO;
import com.digitalmoneyhouse.wallet_service.entity.Account;
import com.digitalmoneyhouse.wallet_service.repository.AccountRepository;
import com.digitalmoneyhouse.wallet_service.exception.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserClient userClient; // Inyectamos el cliente Feign

    public Account getAccountById(Long id) {
        // Por ejemplo, obtenemos la cuenta y luego actualizamos su información con alias y CVU
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        // Suponiendo que el account.id coincide con el user id en user-service
        UserAliasDTO userInfo = userClient.getUserAliasInfo(id);
        // Ahora, puedes actualizar la cuenta con la información recibida
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

}
