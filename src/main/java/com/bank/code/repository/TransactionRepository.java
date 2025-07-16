package com.bank.code.repository;

import com.bank.code.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t " +
            "JOIN t.account a " +
            "JOIN a.user u " +
            "WHERE u.userId = :userId " +
            "AND (:accountId IS NULL OR a.accountId = :accountId) " +
            "AND (:transactionType IS NULL OR t.transactionType = :transactionType) " +
            "AND (:fromDate IS NULL OR t.transactionDate >= :fromDate) " +
            "AND (:toDate IS NULL OR t.transactionDate <= :toDate)")
    List<Transaction> findUserTransactions(@Param("userId") Long userId,
                                           @Param("accountId") Long accountId,
                                           @Param("transactionType") String transactionType,
                                           @Param("fromDate") LocalDate fromDate,
                                           @Param("toDate") LocalDate toDate);
}
