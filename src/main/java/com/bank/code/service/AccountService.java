package com.bank.code.service;

import com.bank.code.dto.request.AccountRequest;
import com.bank.code.entity.Account;
import com.bank.code.entity.Transaction;
import com.bank.code.entity.User;
import com.bank.code.repository.AccountRepository;
import com.bank.code.repository.TransactionRepository;
import com.bank.code.repository.UserRepository;
import com.bank.code.exception.InvalidTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;


    @Transactional
    public Account createAccount(AccountRequest request, User loggedInUser) {
        log.debug("Processing account creation request ID: {} for user: {}", UUID.randomUUID(), loggedInUser.getUsername());
        log.debug("AccountRequest: agreeTerms={}, readDisclosures={}, electronicConsent={}, initialDeposit={}, accountType={}, branchCode={}",
                request.getAgreeTerms(), request.getReadDisclosures(), request.getElectronicConsent(),
                request.getInitialDeposit(), request.getAccountType(), request.getBranchCode());

        // Validate terms and conditions
        if (!request.getAgreeTerms() || !request.getReadDisclosures()) {
            log.error("Validation failed: Terms and conditions not accepted (agreeTerms={}, readDisclosures={})",
                    request.getAgreeTerms(), request.getReadDisclosures());
            throw new InvalidTransactionException("All mandatory terms and conditions must be accepted");
        }

        // Validate initial deposit
        if (request.getInitialDeposit() == null || request.getInitialDeposit().compareTo(new BigDecimal("25.00")) < 0) {
            log.error("Validation failed: Initial deposit invalid. Provided: {}", request.getInitialDeposit());
            throw new InvalidTransactionException("Initial deposit must be at least $25.00");
        }

        // Validate funding source
        if ("TRANSFER".equals(request.getFundingSource())) {
            List<Account> userAccounts = accountRepository.findByUserId(loggedInUser.getUserId());
            log.debug("Found {} existing accounts for user ID: {}", userAccounts.size(), loggedInUser.getUserId());
            if (userAccounts.isEmpty()) {
                log.error("Validation failed: No existing account found for transfer");
                throw new InvalidTransactionException("No existing account found for transfer");
            }
        }

        // Validate account type
        if (!List.of("SAVINGS", "BUSINESS").contains(request.getAccountType())) {
            log.error("Validation failed: Invalid account type: {}", request.getAccountType());
            throw new InvalidTransactionException("Invalid account type: " + request.getAccountType());
        }

        // Validate branch code
        if (request.getBranchCode() != null && !request.getBranchCode().isEmpty()) {
            if (!List.of("BR000", "BR001", "BR002", "BR003", "BR004").contains(request.getBranchCode())) {
                log.error("Validation failed: Invalid branch code: {}", request.getBranchCode());
                throw new InvalidTransactionException("Invalid branch code: " + request.getBranchCode());
            }
        }

        // Generate unique account number
        String accountNumber;
        int attempts = 0;
        do {
            accountNumber = generateAccountNumber();
            attempts++;
            log.debug("Generated account number: {} (attempt {})", accountNumber, attempts);
        } while (accountRepository.existsByAccountNumber(accountNumber) && attempts < 10);

        if (attempts >= 10) {
            log.error("Failed to generate unique account number after {} attempts", attempts);
            throw new InvalidTransactionException("Unable to generate unique account number");
        }

        // Create account
        Account account = new Account();
        account.setUser(loggedInUser);
        account.setAccountNumber(accountNumber);
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialDeposit());
        account.setCreatedDate(LocalDateTime.now());
        account.setStatus("ACTIVE");
        account.setBranchCode(request.getBranchCode());
        account.setAccountStatus("OPEN");

        log.debug("Saving account: number={}, type={}, balance={}, branchCode={}",
                accountNumber, request.getAccountType(), request.getInitialDeposit(), request.getBranchCode());

        Account savedAccount = accountRepository.save(account);
        log.debug("Account saved successfully with ID: {}", savedAccount.getAccountId());

        return savedAccount;
    }

    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("AC");
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public long getTotalAccounts() {
        return accountRepository.count();
    }

    public double getTotalBalance() {
        return accountRepository.findAllBalances()
                .stream()
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
    }

    public long getActiveAccountsCount() {
        return accountRepository.countByAccountStatus("ACTIVE");
    }

    public long getSuspendedAccountsCount() {
        return accountRepository.countByAccountStatus("SUSPENDED");
    }

    public List<Account> findByUserUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    @Transactional
    public void depositToAccount(Long accountId, BigDecimal amount, User user) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Deposit amount must be greater than zero.");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new InvalidTransactionException("Account not found"));

        if (!account.getUser().getUserId().equals(user.getUserId())) {
            throw new InvalidTransactionException("Unauthorized deposit attempt");
        }

        if (!"ACTIVE".equals(account.getStatus())) {
            throw new InvalidTransactionException("Cannot deposit to inactive account");
        }

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        // ✅ Add this to record the deposit as a CREDIT transaction
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType("CREDIT");
        transaction.setAmount(amount);
        transaction.setTransactionDate(java.time.LocalDate.now());
        transaction.setDescription("Deposit");

        transactionRepository.save(transaction);
    }


}
