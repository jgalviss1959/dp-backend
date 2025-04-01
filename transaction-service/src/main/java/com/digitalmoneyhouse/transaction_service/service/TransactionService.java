package com.digitalmoneyhouse.transaction_service.service;

import com.digitalmoneyhouse.transaction_service.config.WalletClient;
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


}
