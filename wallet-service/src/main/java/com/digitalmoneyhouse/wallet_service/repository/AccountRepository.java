package com.digitalmoneyhouse.wallet_service.repository;

import com.digitalmoneyhouse.wallet_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
