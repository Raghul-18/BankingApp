package com.bank.code.controller;

import com.bank.code.entity.Loan;
import com.bank.code.entity.User;
import com.bank.code.service.LoanService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public String viewLoans(Model model, @AuthenticationPrincipal User loggedInUser) {
        List<Loan> userLoans = loanService.getLoansByUser(loggedInUser);
        model.addAttribute("loans", userLoans);
        return "loans_template";
    }

    @GetMapping("/apply/{loanType}")
    public String applyLoanFromLink(@PathVariable String loanType,
                                    @AuthenticationPrincipal User loggedInUser) {
        BigDecimal defaultAmount = switch (loanType.toLowerCase()) {
            case "home" -> BigDecimal.valueOf(5000000);
            case "car" -> BigDecimal.valueOf(800000);
            case "personal" -> BigDecimal.valueOf(250000);
            case "business" -> BigDecimal.valueOf(10000000);
            default -> BigDecimal.valueOf(500000);
        };

        loanService.applyForLoan(loggedInUser, loanType.toUpperCase(), defaultAmount);
        return "redirect:/loans?message=" + loanType + " loan application submitted";
    }

    @PostMapping("/apply")
    public String applyLoan(@RequestParam String loanType,
                            @RequestParam BigDecimal amount,
                            @AuthenticationPrincipal User loggedInUser) {
        loanService.applyForLoan(loggedInUser, loanType, amount);
        return "redirect:/loans?message=Loan application submitted";
    }

    @PostMapping("/{loanId}/payment")
    public String makeLoanPayment(@PathVariable Long loanId,
                                  @RequestParam BigDecimal amount,
                                  @AuthenticationPrincipal User loggedInUser) {
        loanService.makePayment(loggedInUser, loanId, amount);
        return "redirect:/loans?message=Payment successful";
    }
}
