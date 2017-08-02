package com.slfinance.shanlincaifu.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoanInfoVo implements Serializable {

	private static final long serialVersionUID = -2685554208950432894L;
	private String loanId;
	private Date investStartDate; 
	private Date investEndDate;
	private BigDecimal loanAmount;
	private BigDecimal holdAmount;
	private BigDecimal holdScale;
	private String loanCode;
	private String importDate;
	private String repaymentDay;
	private String repaymentMethod;
	private int loanTerm;
	private int repaymentCycle;
	
	private List<RepaymentPlanVo> repaymentPlanVoList=new ArrayList<RepaymentPlanVo>();

	public String getLoanId() {
		return loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	public Date getInvestStartDate() {
		return investStartDate;
	}

	public void setInvestStartDate(Date investStartDate) {
		this.investStartDate = investStartDate;
	}

	public Date getInvestEndDate() {
		return investEndDate;
	}

	public void setInvestEndDate(Date investEndDate) {
		this.investEndDate = investEndDate;
	}

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
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

	public List<RepaymentPlanVo> getRepaymentPlanVoList() {
		return repaymentPlanVoList;
	}

	public void setRepaymentPlanVoList(List<RepaymentPlanVo> repaymentPlanVoList) {
		this.repaymentPlanVoList = repaymentPlanVoList;
	}

	public String getLoanCode() {
		return loanCode;
	}

	public void setLoanCode(String loanCode) {
		this.loanCode = loanCode;
	}

	public String getImportDate() {
		return importDate;
	}

	public void setImportDate(String importDate) {
		this.importDate = importDate;
	}

	public String getRepaymentDay() {
		return repaymentDay;
	}

	public void setRepaymentDay(String repaymentDay) {
		this.repaymentDay = repaymentDay;
	}

	public String getRepaymentMethod() {
		return repaymentMethod;
	}

	public void setRepaymentMethod(String repaymentMethod) {
		this.repaymentMethod = repaymentMethod;
	}

	public int getLoanTerm() {
		return loanTerm;
	}

	public void setLoanTerm(int loanTerm) {
		this.loanTerm = loanTerm;
	}

	public int getRepaymentCycle() {
		return repaymentCycle;
	}

	public void setRepaymentCycle(int repaymentCycle) {
		this.repaymentCycle = repaymentCycle;
	}
	
	
}
