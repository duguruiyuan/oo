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
 * BaoTLoanDetailInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_LOAN_DETAIL_INFO")
public class LoanDetailInfoEntity extends BaseEntity  {

	private static final long serialVersionUID = 2568640458888465852L;
	private Timestamp currTremEndDate;
	private Timestamp lastExpiry;
	private Timestamp nextExpiry;
	private BigDecimal alreadyPaymentTerm;
	private BigDecimal currTerm;
	private BigDecimal creditRemainderPrincipal;
	private BigDecimal wealthRemainderPrincipal;
	private BigDecimal currPayableInterest;
	private BigDecimal currReceivableInterest;
	private String creditRightStatus;
	private String execPvStatus;
	
	private BigDecimal dayIrr;
	private BigDecimal monthIrr;
	private BigDecimal yearIrr;
	@Transient
	private BigDecimal importPv;
	
	@ManyToOne(cascade={CascadeType.ALL})           
	@JoinColumn(name="LOAN_ID")
	@JsonIgnore
	private LoanInfoEntity loanInfoEntity=new LoanInfoEntity();

	@Column(name = "CURR_TREM_END_DATE", length = 7)
	public Timestamp getCurrTremEndDate() {
		return this.currTremEndDate;
	}

	public void setCurrTremEndDate(Timestamp currTremEndDate) {
		this.currTremEndDate = currTremEndDate;
	}

	@Column(name = "LAST_EXPIRY", length = 7)
	public Timestamp getLastExpiry() {
		return this.lastExpiry;
	}

	public void setLastExpiry(Timestamp lastExpiry) {
		this.lastExpiry = lastExpiry;
	}

	@Column(name = "NEXT_EXPIRY", length = 7)
	public Timestamp getNextExpiry() {
		return this.nextExpiry;
	}

	public void setNextExpiry(Timestamp nextExpiry) {
		this.nextExpiry = nextExpiry;
	}

	@Column(name = "ALREADY_PAYMENT_TERM", precision = 22, scale = 0)
	public BigDecimal getAlreadyPaymentTerm() {
		return this.alreadyPaymentTerm;
	}

	public void setAlreadyPaymentTerm(BigDecimal alreadyPaymentTerm) {
		this.alreadyPaymentTerm = alreadyPaymentTerm;
	}

	@Column(name = "CURR_TERM", precision = 22, scale = 0)
	public BigDecimal getCurrTerm() {
		return this.currTerm;
	}

	public void setCurrTerm(BigDecimal currTerm) {
		this.currTerm = currTerm;
	}

	@Column(name = "CREDIT_REMAINDER_PRINCIPAL", precision = 22, scale = 8)
	public BigDecimal getCreditRemainderPrincipal() {
		return this.creditRemainderPrincipal;
	}

	public void setCreditRemainderPrincipal(BigDecimal creditRemainderPrincipal) {
		this.creditRemainderPrincipal = creditRemainderPrincipal;
	}

	@Column(name = "WEALTH_REMAINDER_PRINCIPAL", precision = 22, scale = 8)
	public BigDecimal getWealthRemainderPrincipal() {
		return this.wealthRemainderPrincipal;
	}

	public void setWealthRemainderPrincipal(BigDecimal wealthRemainderPrincipal) {
		this.wealthRemainderPrincipal = wealthRemainderPrincipal;
	}

	@Column(name = "CURR_PAYABLE_INTEREST", precision = 22, scale = 8)
	public BigDecimal getCurrPayableInterest() {
		return this.currPayableInterest;
	}

	public void setCurrPayableInterest(BigDecimal currPayableInterest) {
		this.currPayableInterest = currPayableInterest;
	}

	@Column(name = "CURR_RECEIVABLE_INTEREST", precision = 22, scale = 8)
	public BigDecimal getCurrReceivableInterest() {
		return this.currReceivableInterest;
	}

	public void setCurrReceivableInterest(BigDecimal currReceivableInterest) {
		this.currReceivableInterest = currReceivableInterest;
	}

	@Column(name = "CREDIT_RIGHT_STATUS", length = 50)
	public String getCreditRightStatus() {
		return this.creditRightStatus;
	}

	public void setCreditRightStatus(String creditRightStatus) {
		this.creditRightStatus = creditRightStatus;
	}

	@Column(name = "EXEC_PV_STATUS", length = 2)
	public String getExecPvStatus() {
		return this.execPvStatus;
	}

	public void setExecPvStatus(String execPvStatus) {
		this.execPvStatus = execPvStatus;
	}

	public LoanInfoEntity getLoanInfoEntity() {
		return loanInfoEntity;
	}

	public void setLoanInfoEntity(LoanInfoEntity loanInfoEntity) {
		this.loanInfoEntity = loanInfoEntity;
	}

	@Column(name = "DAY_IRR", precision = 22, scale = 18)
	public BigDecimal getDayIrr() {
		return dayIrr;
	}

	public void setDayIrr(BigDecimal dayIrr) {
		this.dayIrr = dayIrr;
	}

	@Column(name = "MONTH_IRR", precision = 22, scale = 18)
	public BigDecimal getMonthIrr() {
		return monthIrr;
	}

	public void setMonthIrr(BigDecimal monthIrr) {
		this.monthIrr = monthIrr;
	}

	@Column(name = "YEAR_IRR", precision = 22, scale = 18)
	public BigDecimal getYearIrr() {
		return yearIrr;
	}

	public void setYearIrr(BigDecimal yearIrr) {
		this.yearIrr = yearIrr;
	}
	
	public BigDecimal getImportPv() {
		return importPv;
	}

	public void setImportPv(BigDecimal importPv) {
		this.importPv = importPv;
	}
	
}