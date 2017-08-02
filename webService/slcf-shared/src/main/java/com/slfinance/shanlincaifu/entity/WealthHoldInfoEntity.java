package com.slfinance.shanlincaifu.entity;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * BAO理财计划用户持有情况表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_WEALTH_HOLD_INFO")
public class WealthHoldInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	private String investId;

	/**
	 * 
	 */
	private String subAccountId;

	/**
	 * 客户信息主键ID
	 */
	private String custId;

	/**
	 * 债权主键ID
	 */
	private String loanId;

	/**
	 * 
	 */
	private BigDecimal holdScale;

	/**
	 * 
	 */
	private BigDecimal holdAmount;

	/**
	 * 
	 */
	private BigDecimal exceptAmount;

	/**
	 * 
	 */
	private BigDecimal receivedAmount;

	/**
	 * 持有中、待转让、转让中、已转让
	 */
	private String holdStatus;

	/**
	 * 首期、回款
	 */
	private String holdSource;

	/**
	 * 是/否
	 */
	private String isCenter;

	/**
	 * 转让持有比例
	 */
	private BigDecimal tradeScale;

	@Column(name = "INVEST_ID", length = 50)
	public String getInvestId() {
		return this.investId;
	}

	public void setInvestId(String investId) {
		this.investId = investId;
	}

	@Column(name = "SUB_ACCOUNT_ID", length = 50)
	public String getSubAccountId() {
		return this.subAccountId;
	}

	public void setSubAccountId(String subAccountId) {
		this.subAccountId = subAccountId;
	}

	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "LOAN_ID", length = 50)
	public String getLoanId() {
		return this.loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	@Column(name = "HOLD_SCALE", precision = 22, scale = 18)
	public BigDecimal getHoldScale() {
		return this.holdScale;
	}

	public void setHoldScale(BigDecimal holdScale) {
		this.holdScale = holdScale;
	}

	@Column(name = "HOLD_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getHoldAmount() {
		return this.holdAmount;
	}

	public void setHoldAmount(BigDecimal holdAmount) {
		this.holdAmount = holdAmount;
	}

	@Column(name = "EXCEPT_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getExceptAmount() {
		return this.exceptAmount;
	}

	public void setExceptAmount(BigDecimal exceptAmount) {
		this.exceptAmount = exceptAmount;
	}

	@Column(name = "RECEIVED_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getReceivedAmount() {
		return this.receivedAmount;
	}

	public void setReceivedAmount(BigDecimal receivedAmount) {
		this.receivedAmount = receivedAmount;
	}

	@Column(name = "HOLD_STATUS", length = 50)
	public String getHoldStatus() {
		return this.holdStatus;
	}

	public void setHoldStatus(String holdStatus) {
		this.holdStatus = holdStatus;
	}

	@Column(name = "HOLD_SOURCE", length = 50)
	public String getHoldSource() {
		return this.holdSource;
	}

	public void setHoldSource(String holdSource) {
		this.holdSource = holdSource;
	}

	@Column(name = "IS_CENTER", length = 50)
	public String getIsCenter() {
		return this.isCenter;
	}

	public void setIsCenter(String isCenter) {
		this.isCenter = isCenter;
	}

	@Column(name = "TRADE_SCALE", precision = 22, scale = 18)
	public BigDecimal getTradeScale() {
		return this.tradeScale;
	}

	public void setTradeScale(BigDecimal tradeScale) {
		this.tradeScale = tradeScale;
	}
}
