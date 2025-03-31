package com.digitalmoneyhouse.transaction_service.service;

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

        // Crear nueva transacción de depósito
        Transaction transaction = new Transaction(
                depositRequest.getAccountId(),
                depositRequest.getAmount(),
                LocalDateTime.now(),
                "DEPOSIT"
        );

        return transactionRepository.save(transaction);
    }

    public List<ActivityDTO> getAccountActivity(Long accountId) {
        return transactionRepository.findByAccountIdOrderByDateDesc(accountId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


}
