package com.digitalmoneyhouse.wallet_service.repository;

import com.digitalmoneyhouse.wallet_service.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByAccountId(Long accountId);
    Optional<Card> findByCardNumber(String cardNumber);
}
