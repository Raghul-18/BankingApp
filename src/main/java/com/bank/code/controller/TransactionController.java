package com.bank.code.controller;

import com.bank.code.entity.Account;
import com.bank.code.entity.Transaction;
import com.bank.code.entity.User;
import com.bank.code.repository.AccountRepository;
import com.bank.code.repository.TransactionRepository;
import com.bank.code.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionController(UserRepository userRepository, AccountRepository accountRepository,
                                 TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping
    public String viewTransactions(@RequestParam(required = false) Long accountId,
                                   @RequestParam(required = false) String transactionType,
                                   @RequestParam(required = false) String fromDate,
                                   @RequestParam(required = false) String toDate,
                                   Principal principal,
                                   Model model) {

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Account> accounts = accountRepository.findByUserId(user.getUserId());

        LocalDate from = (fromDate != null && !fromDate.isEmpty()) ? LocalDate.parse(fromDate) : null;
        LocalDate to = (toDate != null && !toDate.isEmpty()) ? LocalDate.parse(toDate) : null;

        List<Transaction> transactions = transactionRepository.findUserTransactions(
                user.getUserId(), accountId, transactionType, from, to);

        double totalCredits = transactions.stream()
                .filter(t -> "CREDIT".equalsIgnoreCase(t.getTransactionType()))
                .mapToDouble(t -> t.getAmount().doubleValue())
                .sum();

        double totalDebits = transactions.stream()
                .filter(t -> "DEBIT".equalsIgnoreCase(t.getTransactionType()))
                .mapToDouble(t -> t.getAmount().doubleValue())
                .sum();

        model.addAttribute("userAccounts", accounts);
        model.addAttribute("transactions", transactions);
        model.addAttribute("totalCredits", totalCredits);
        model.addAttribute("totalDebits", totalDebits);
        model.addAttribute("transactionCount", transactions.size());

        // ✅ FIX to prevent Thymeleaf pagination error
        model.addAttribute("currentPage", 0);
        model.addAttribute("pageSize", 10);
        model.addAttribute("totalPages", 1);

        return "transactions_template";
    }
}
