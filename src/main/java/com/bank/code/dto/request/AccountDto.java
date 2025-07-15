package com.bank.code.dto.request;

public class AccountDto {
    private Long userId;
    private String accountType;
    public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Double getInitialDeposit() {
		return initialDeposit;
	}
	public void setInitialDeposit(Double initialDeposit) {
		this.initialDeposit = initialDeposit;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getAccountPurpose() {
		return accountPurpose;
	}
	public void setAccountPurpose(String accountPurpose) {
		this.accountPurpose = accountPurpose;
	}
	private String currency;
    private Double initialDeposit;
    private String branchCode;
    private String accountPurpose;
    // Getters and Setters
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
}