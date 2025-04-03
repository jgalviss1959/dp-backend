package com.digitalmoneyhouse.transaction_service.repository;

import com.digitalmoneyhouse.transaction_service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findTop5ByAccountIdOrderByDateDesc(Long accountId);

    List<Transaction> findByAccountIdOrderByDateDesc(Long accountId);

    Optional<Transaction> findByAccountIdAndId(Long accountId, Long activityId);

}
