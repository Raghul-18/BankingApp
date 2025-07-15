package com.bank.code.controller;

import com.bank.code.dto.request.AccountRequest;
import com.bank.code.entity.Account;
import com.bank.code.entity.User;
import com.bank.code.exception.InvalidTransactionException;
import com.bank.code.service.AccountService;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/accounts")
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @PostConstruct
    public void init() {
        logger.info("AccountController initialized");
    }

    @GetMapping
    public String showAccounts(Model model, @AuthenticationPrincipal User loggedInUser) {
        logger.debug("Handling GET /accounts for user: {}", loggedInUser.getUsername());
        List<Account> accounts = accountService.findByUserUserId(loggedInUser.getUserId());
        model.addAttribute("accounts", accounts);
        model.addAttribute("username", loggedInUser.getUsername());
        model.addAttribute("isAdmin", loggedInUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        model.addAttribute("isManager", loggedInUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER")));
        model.addAttribute("totalBalance", accounts.stream()
                .map(Account::getBalance)
                .mapToDouble(BigDecimal::doubleValue)
                .sum());
        model.addAttribute("totalAccounts", accounts.size());
        model.addAttribute("activeAccounts", accounts.stream()
                .filter(account -> "ACTIVE".equals(account.getStatus()))
                .count());
        return "accounts";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, @AuthenticationPrincipal User loggedInUser) {
        logger.debug("Handling GET /accounts/create for user: {}", loggedInUser.getUsername());
        model.addAttribute("accountRequest", new AccountRequest());
        model.addAttribute("username", loggedInUser.getUsername());
        model.addAttribute("currentUser", loggedInUser); // Added to fix null reference in Thymeleaf template
        model.addAttribute("isAdmin", false);
        model.addAttribute("isManager", false);
        return "create-account";
    }

    @PostMapping("/create")
    public String createAccount(@Valid @ModelAttribute("accountRequest") AccountRequest accountRequest,
                               BindingResult result,
                               @AuthenticationPrincipal User loggedInUser,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        logger.debug("Handling POST /accounts/create for user: {}", loggedInUser.getUsername());
        
        // Add common model attributes (including currentUser)
        model.addAttribute("username", loggedInUser.getUsername());
        model.addAttribute("currentUser", loggedInUser); // ADD THIS LINE
        model.addAttribute("isAdmin", false);
        model.addAttribute("isManager", false);

        if (result.hasErrors()) {
        	logger.debug("Validation errors in account creation: {}", result.getAllErrors());
            model.addAttribute("errors", result.getAllErrors()); // Add errors to model
            return "create-account";
        }

        try {
            accountService.createAccount(accountRequest, loggedInUser);
            redirectAttributes.addFlashAttribute("message", "Account successfully created!");
            return "redirect:/accounts";
        } catch (InvalidTransactionException e) {
            logger.error("Invalid transaction during account creation: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "create-account";
        } catch (Exception e) {
            //logger.error("Unexpected error during account creation: {}", e.getMessage(), e);
            logger.error("Unexpected error creating account", e);    
            model.addAttribute("error", "Unexpected error creating account: " + e.getMessage());
            return "create-account";
        }
    }
}