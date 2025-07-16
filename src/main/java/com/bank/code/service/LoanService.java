package com.bank.code.service;

import com.bank.code.entity.Loan;
import com.bank.code.entity.User;
import com.bank.code.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public List<Loan> getLoansByUser(User user) {
        return loanRepository.findByUserUserId(user.getUserId());
    }

    @Transactional
    public void applyForLoan(User user, String loanType, BigDecimal amount) {
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setLoanType(loanType);
        loan.setAmount(amount);
        loan.setOutstandingAmount(amount);
        loan.setStatus("PENDING");
        loan.setCreatedDate(LocalDate.now());
        loanRepository.save(loan);
    }

    @Transactional
    public void makePayment(User user, Long loanId, BigDecimal paymentAmount) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (!loan.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized access to loan");
        }

        if (loan.getOutstandingAmount().compareTo(paymentAmount) < 0) {
            throw new RuntimeException("Payment exceeds outstanding loan amount.");
        }

        loan.setOutstandingAmount(loan.getOutstandingAmount().subtract(paymentAmount));

        if (loan.getOutstandingAmount().compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus("CLOSED");
        }

        loanRepository.save(loan);
    }
}
