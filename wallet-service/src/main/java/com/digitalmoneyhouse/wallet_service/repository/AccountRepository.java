package com.digitalmoneyhouse.wallet_service.repository;

import com.digitalmoneyhouse.wallet_service.dto.AccountDTO;
import com.digitalmoneyhouse.wallet_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByCvu(String cvu);
}
