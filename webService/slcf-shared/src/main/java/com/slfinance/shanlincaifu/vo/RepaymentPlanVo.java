package com.slfinance.shanlincaifu.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class RepaymentPlanVo implements Serializable {

	private static final long serialVersionUID = 8979881872107357314L;
	private String loanId;
	private int currentTerm;
	private BigDecimal monthIrr;
	private Date investStartDate;
	private Date expectRepaymentDate;
	private BigDecimal loanAmount;
	private BigDecimal holdAmount;
	private BigDecimal holdScale;
	private BigDecimal remainderPrincipal;
	private BigDecimal repaymentPrincipal;
	private BigDecimal repaymentInterest;
	private BigDecimal repaymentTotalAmount;

	public String getLoanId() {
		return loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	public BigDecimal getMonthIrr() {
		return monthIrr;
	}

	public void setMonthIrr(BigDecimal monthIrr) {
		this.monthIrr = monthIrr;
	}

	public Date getInvestStartDate() {
		return investStartDate;
	}

	public void setInvestStartDate(Date investStartDate) {
		this.investStartDate = investStartDate;
	}

	public int getCurrentTerm() {
		return currentTerm;
	}

	public void setCurrentTerm(int currentTerm) {
		this.currentTerm = currentTerm;
	}

	public Date getExpectRepaymentDate() {
		return expectRepaymentDate;
	}

	public void setExpectRepaymentDate(Date expectRepaymentDate) {
		this.expectRepaymentDate = expectRepaymentDate;
	}

	public BigDecimal getRemainderPrincipal() {
		return remainderPrincipal;
	}

	public void setRemainderPrincipal(BigDecimal remainderPrincipal) {
		this.remainderPrincipal = remainderPrincipal;
	}

	public BigDecimal getRepaymentPrincipal() {
		return repaymentPrincipal;
	}

	public void setRepaymentPrincipal(BigDecimal repaymentPrincipal) {
		this.repaymentPrincipal = repaymentPrincipal;
	}

	public BigDecimal getRepaymentTotalAmount() {
		return repaymentTotalAmount;
	}

	public void setRepaymentTotalAmount(BigDecimal repaymentTotalAmount) {
		this.repaymentTotalAmount = repaymentTotalAmount;
	}

	public BigDecimal getRepaymentInterest() {
		return repaymentInterest;
	}

	public void setRepaymentInterest(BigDecimal repaymentInterest) {
		this.repaymentInterest = repaymentInterest;
	}

	public BigDecimal getHoldAmount() {
		return holdAmount;
	}

	public void setHoldAmount(BigDecimal holdAmount) {
		this.holdAmount = holdAmount;
	}

	public BigDecimal getHoldScale() {
		return holdScale;
	}

	public void setHoldScale(BigDecimal holdScale) {
		this.holdScale = holdScale;
	}

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}
	
	
	
}
