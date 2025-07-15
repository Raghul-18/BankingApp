package com.bank.code.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class AccountRequest {

    @NotBlank(message = "Account type is required")
    private String accountType;

    private String accountNickname;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Branch code is required")
    private String branchCode;

    @NotBlank(message = "Account purpose is required")
    private String accountPurpose;

    @NotNull(message = "Initial deposit is required")
    @DecimalMin(value = "25.00", message = "Initial deposit must be at least $25.00")
    private BigDecimal initialDeposit;

    @NotBlank(message = "Funding source is required")
    private String fundingSource;

    @NotNull(message = "You must agree to the terms and conditions")
    @AssertTrue(message = "You must agree to the terms and conditions")
    private Boolean agreeTerms;

    @NotNull(message = "You must acknowledge the account disclosures")
    @AssertTrue(message = "You must acknowledge the account disclosures")
    private Boolean readDisclosures;

    @NotNull(message = "You must consent to electronic statements")
    @AssertTrue(message = "You must consent to electronic statements")
    private Boolean electronicConsent;

    // Getters and Setters
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public String getAccountNickname() { return accountNickname; }
    public void setAccountNickname(String accountNickname) { this.accountNickname = accountNickname; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
    public String getAccountPurpose() { return accountPurpose; }
    public void setAccountPurpose(String accountPurpose) { this.accountPurpose = accountPurpose; }
    public BigDecimal getInitialDeposit() { return initialDeposit; }
    public void setInitialDeposit(BigDecimal initialDeposit) { this.initialDeposit = initialDeposit; }
    public String getFundingSource() { return fundingSource; }
    public void setFundingSource(String fundingSource) { this.fundingSource = fundingSource; }
    public Boolean getAgreeTerms() { return agreeTerms; }
    public void setAgreeTerms(Boolean agreeTerms) { this.agreeTerms = agreeTerms; }
    public Boolean getReadDisclosures() { return readDisclosures; }
    public void setReadDisclosures(Boolean readDisclosures) { this.readDisclosures = readDisclosures; }
    public Boolean getElectronicConsent() { return electronicConsent; }
    public void setElectronicConsent(Boolean electronicConsent) { this.electronicConsent = electronicConsent; }
}