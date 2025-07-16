package com.bank.code.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "LOANS")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOAN_ID")
    private Long loanId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "LOAN_TYPE")
    private String loanType;

    @Column(name = "LOAN_AMOUNT")
    private BigDecimal amount;

    @Column(name = "ISSUE_DATE")
    private LocalDate createdDate;

    @Column(name = "DUE_DATE")
    private LocalDate dueDate;

    @Column(name = "STATUS")
    private String status;

    // Optional: Real columns if you plan to persist them. Otherwise, keep as @Transient.
    @Transient
    private BigDecimal interestRate;

    @Transient
    private BigDecimal monthlyPayment;

    @Transient
    private BigDecimal remainingBalance;

    @Transient
    private int paidPercentage;

    // --- GETTERS AND SETTERS ---

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public int getPaidPercentage() {
        return paidPercentage;
    }

    public void setPaidPercentage(int paidPercentage) {
        this.paidPercentage = paidPercentage;
    }
}
