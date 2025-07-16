package com.bank.code.service;

import com.bank.code.dto.request.TransferRequestDto;
import com.bank.code.entity.Account;
import com.bank.code.entity.Transaction;
import com.bank.code.repository.AccountRepository;
import com.bank.code.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransferService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void transfer(TransferRequestDto request) {
        Account fromAccount = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("From Account not found."));

        BigDecimal amount = request.getAmount();
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        accountRepository.save(fromAccount);

        Transaction transaction = new Transaction();
        transaction.setAccount(fromAccount);
        transaction.setTransactionType("DEBIT");
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setDescription(request.getDescription());
        transactionRepository.save(transaction);
    }
}
