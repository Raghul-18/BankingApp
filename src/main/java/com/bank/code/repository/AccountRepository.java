package com.bank.code.repository;

import com.bank.code.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    long countByAccountStatus(String status);

    @Query("SELECT a FROM Account a WHERE a.user.userId = :userId")
    List<Account> findByUserId(@Param("userId") Long userId);

    @Query("SELECT a.balance FROM Account a")
    List<BigDecimal> findAllBalances();

    boolean existsByAccountNumber(String accountNumber);
    long countByUserUserId(Long userId);

}