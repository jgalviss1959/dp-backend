package com.digitalmoneyhouse.transaction_service.service;

import com.digitalmoneyhouse.transaction_service.entity.Transaction;
import com.digitalmoneyhouse.transaction_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getLast5TransactionsByAccountId(Long accountId) {
        return transactionRepository.findTop5ByAccountIdOrderByDateDesc(accountId);
    }
}
