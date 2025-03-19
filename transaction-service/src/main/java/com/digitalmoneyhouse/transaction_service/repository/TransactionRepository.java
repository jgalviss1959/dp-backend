package com.digitalmoneyhouse.transaction_service.repository;

import com.digitalmoneyhouse.transaction_service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Retorna las últimas 5 transacciones por fecha descendente
    List<Transaction> findTop5ByAccountIdOrderByDateDesc(Long accountId);
}
