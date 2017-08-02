package com.slfinance.shanlincaifu.entity;
// default package

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * BaoTRepaymentPlanInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_REPAYMENT_PLAN_INFO")
public class RepaymentPlanInfoEntity extends BaseEntity {

	private static final long serialVersionUID = -4948993252661425321L;
	private Integer currentTerm;
	private String expectRepaymentDate;
	private BigDecimal repaymentTotalAmount;
	private BigDecimal termAlreadyRepayAmount;
	private BigDecimal repaymentInterest;
	private BigDecimal repaymentPrincipal;
	private BigDecimal remainderPrincipal;
	private BigDecimal advanceCleanupTotalAmount;
	private String repaymentStatus;
	private Timestamp factRepaymentDate;
	private Timestamp penaltyStartDate;
	private BigDecimal accountManageExpense;
	@Transient
	private String loanCode;
	private String projectId;
	private BigDecimal penaltyAmount;
	private String isAmountFrozen;
	private String isRiskamountRepay;
	private BigDecimal riskRepayAmount;
	
	/**
	 * 奖励金额
	 */
	private BigDecimal awardAmount;

	@ManyToOne(cascade={CascadeType.ALL})           
	@JoinColumn(name="LOAN_ID")
	@JsonIgnore
	private LoanInfoEntity loanEntity=new LoanInfoEntity();
	
	@Column(name = "CURRENT_TERM")
	public Integer getCurrentTerm() {
		return this.currentTerm;
	}

	public void setCurrentTerm(Integer currentTerm) {
		this.currentTerm = currentTerm;
	}

	@Column(name = "EXPECT_REPAYMENT_DATE", length = 50)
	public String getExpectRepaymentDate() {
		return this.expectRepaymentDate;
	}

	public void setExpectRepaymentDate(String expectRepaymentDate) {
		this.expectRepaymentDate = expectRepaymentDate;
	}

	@Column(name = "REPAYMENT_TOTAL_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getRepaymentTotalAmount() {
		return this.repaymentTotalAmount;
	}

	public void setRepaymentTotalAmount(BigDecimal repaymentTotalAmount) {
		this.repaymentTotalAmount = repaymentTotalAmount;
	}

	@Column(name = "TERM_ALREADY_REPAY_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getTermAlreadyRepayAmount() {
		return this.termAlreadyRepayAmount;
	}

	public void setTermAlreadyRepayAmount(BigDecimal termAlreadyRepayAmount) {
		this.termAlreadyRepayAmount = termAlreadyRepayAmount;
	}

	@Column(name = "REPAYMENT_INTEREST", precision = 22, scale = 8)
	public BigDecimal getRepaymentInterest() {
		return this.repaymentInterest;
	}

	public void setRepaymentInterest(BigDecimal repaymentInterest) {
		this.repaymentInterest = repaymentInterest;
	}

	@Column(name = "REPAYMENT_PRINCIPAL", precision = 22, scale = 8)
	public BigDecimal getRepaymentPrincipal() {
		return this.repaymentPrincipal;
	}

	public void setRepaymentPrincipal(BigDecimal repaymentPrincipal) {
		this.repaymentPrincipal = repaymentPrincipal;
	}

	@Column(name = "REMAINDER_PRINCIPAL", precision = 22, scale = 8)
	public BigDecimal getRemainderPrincipal() {
		return this.remainderPrincipal;
	}

	public void setRemainderPrincipal(BigDecimal remainderPrincipal) {
		this.remainderPrincipal = remainderPrincipal;
	}

	@Column(name = "ADVANCE_CLEANUP_TOTAL_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getAdvanceCleanupTotalAmount() {
		return this.advanceCleanupTotalAmount;
	}

	public void setAdvanceCleanupTotalAmount(BigDecimal advanceCleanupTotalAmount) {
		this.advanceCleanupTotalAmount = advanceCleanupTotalAmount;
	}

	@Column(name = "REPAYMENT_STATUS", length = 50)
	public String getRepaymentStatus() {
		return this.repaymentStatus;
	}

	public void setRepaymentStatus(String repaymentStatus) {
		this.repaymentStatus = repaymentStatus;
	}

	@Column(name = "FACT_REPAYMENT_DATE", length = 7)
	public Timestamp getFactRepaymentDate() {
		return this.factRepaymentDate;
	}

	public void setFactRepaymentDate(Timestamp factRepaymentDate) {
		this.factRepaymentDate = factRepaymentDate;
	}

	@Column(name = "PENALTY_START_DATE", length = 7)
	public Timestamp getPenaltyStartDate() {
		return this.penaltyStartDate;
	}

	public void setPenaltyStartDate(Timestamp penaltyStartDate) {
		this.penaltyStartDate = penaltyStartDate;
	}

	@Column(name = "ACCOUNT_MANAGE_EXPENSE", precision = 22, scale = 8)
	public BigDecimal getAccountManageExpense() {
		return this.accountManageExpense;
	}

	public void setAccountManageExpense(BigDecimal accountManageExpense) {
		this.accountManageExpense = accountManageExpense;
	}

	public String getLoanCode() {
		return loanCode;
	}

	public void setLoanCode(String loanCode) {
		this.loanCode = loanCode;
	}

	public LoanInfoEntity getLoanEntity() {
		return loanEntity;
	}

	public void setLoanEntity(LoanInfoEntity loanEntity) {
		this.loanEntity = loanEntity;
	}

	@Column(name = "PROJECT_ID", length = 50)
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	@Column(name = "PENALTY_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getPenaltyAmount() {
		return penaltyAmount;
	}

	public void setPenaltyAmount(BigDecimal penaltyAmount) {
		this.penaltyAmount = penaltyAmount;
	}

	@Column(name = "IS_AMOUNT_FROZEN", length = 50)
	public String getIsAmountFrozen() {
		return isAmountFrozen;
	}

	public void setIsAmountFrozen(String isAmountFrozen) {
		this.isAmountFrozen = isAmountFrozen;
	}

	@Column(name = "IS_RISKAMOUNT_REPAY", length = 50)
	public String getIsRiskamountRepay() {
		return isRiskamountRepay;
	}

	public void setIsRiskamountRepay(String isRiskamountRepay) {
		this.isRiskamountRepay = isRiskamountRepay;
	}

	@Column(name = "RISK_REPAY_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getRiskRepayAmount() {
		return riskRepayAmount;
	}

	public void setRiskRepayAmount(BigDecimal riskRepayAmount) {
		this.riskRepayAmount = riskRepayAmount;
	}
	

	@Column(name = "AWARD_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getAwardAmount() {
		return this.awardAmount;
	}

	public void setAwardAmount(BigDecimal awardAmount) {
		this.awardAmount = awardAmount;
	}
}