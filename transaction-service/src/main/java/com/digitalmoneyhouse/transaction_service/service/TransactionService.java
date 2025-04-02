package com.digitalmoneyhouse.transaction_service.service;

import com.digitalmoneyhouse.transaction_service.config.WalletClient;
import com.digitalmoneyhouse.transaction_service.dto.AccountDTO;
import com.digitalmoneyhouse.transaction_service.dto.ActivityDTO;
import com.digitalmoneyhouse.transaction_service.dto.DepositRequestDTO;
import com.digitalmoneyhouse.transaction_service.entity.Transaction;
import com.digitalmoneyhouse.transaction_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletClient walletClient;

    public TransactionService(WalletClient walletClient) {
        this.walletClient = walletClient;
    }

    public List<Transaction> getLast5TransactionsByAccountId(Long accountId) {
        return transactionRepository.findTop5ByAccountIdOrderByDateDesc(accountId);
    }

    public Optional<ActivityDTO> getActivityDetail(Long accountId, Long activityId) {
        return transactionRepository.findByAccountIdAndId(accountId, activityId)
                .map(this::convertToDTO);
    }

    public boolean accountExists(Long accountId) {
        return transactionRepository.existsById(accountId);
    }

    private ActivityDTO convertToDTO(Transaction transaction) {
        return new ActivityDTO(transaction.getId(), transaction.getType(), transaction.getAmount(), transaction.getDate());
    }

    @Transactional
    public Transaction depositToWallet(DepositRequestDTO depositRequest) {
        // Validar que el monto sea positivo
        if (depositRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }

        Long accountId = depositRequest.getAccountId();
        BigDecimal amount = depositRequest.getAmount();

        // Crear nueva transacción de depósito
        Transaction transaction = new Transaction(
                accountId,
                amount,
                LocalDateTime.now(),
                "DEPOSIT"
        );

        transactionRepository.save(transaction);

        // Llamar al wallet-service para aumentar el saldo de la cuenta
        try {
            walletClient.increaseAccountBalance(accountId, amount);
        } catch (Exception e) {
            // Loggear el error al intentar actualizar el saldo.
            System.err.println("Error al actualizar el saldo en wallet-service para la cuenta " + accountId + ": " + e.getMessage());
        }

        return transaction;
    }

    public List<ActivityDTO> getAccountActivity(Long accountId) {
        return transactionRepository.findByAccountIdOrderByDateDesc(accountId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Transaction transferBetweenAccounts(String fromCvu, String toCvu, BigDecimal amount) {
        // Validaciones iniciales
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }

        // Obtener las cuentas desde wallet-service usando el cliente Feign
        AccountDTO originAccount = null;
        AccountDTO destinationAccount = null;

        try {
            originAccount = walletClient.getAccountByCvu(fromCvu);
            destinationAccount = walletClient.getAccountByCvu(toCvu);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al obtener las cuentas: " + e.getMessage());
        }

        if (originAccount == null) {
            throw new IllegalArgumentException("Cuenta origen no encontrada.");
        }
        if (destinationAccount == null) {
            throw new IllegalArgumentException("Cuenta destino no encontrada.");
        }

        // Validar saldo suficiente
        if (originAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }

        // Descontamos el saldo de la cuenta de origen y sumamos al saldo de la cuenta destino
        walletClient.updateAccountBalance(originAccount.getId(), originAccount.getBalance().subtract(amount));
        walletClient.updateAccountBalance(destinationAccount.getId(), destinationAccount.getBalance().add(amount));

        // Crear la transacción de transferencia
        Transaction transaction = new Transaction();
        transaction.setAccountId(originAccount.getId());
        transaction.setAmount(amount);
        transaction.setDate(LocalDateTime.now());
        transaction.setType("TRANSFER");

        // Guardar la transacción
        return transactionRepository.save(transaction);
    }
}
