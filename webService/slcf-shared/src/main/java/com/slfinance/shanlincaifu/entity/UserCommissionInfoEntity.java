package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 业务员佣金表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_USER_COMMISSION_INFO")
public class UserCommissionInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 业务员ID
	 */
	private String custId;

	/**
	 * 投资ID
	 */
	private String investId;

	/**
	 * 期数
	 */
	private Integer currentTerm;

	/**
	 * 返佣日期
	 */
	private String exceptPaymentDate;

	/**
	 * 预计返佣总额
	 */
	private BigDecimal totalPaymentAmount;

	/**
	 * 预计佣金金额
	 */
	private BigDecimal paymentPrincipal;

	/**
	 * 预计补贴金额
	 */
	private BigDecimal paymentInterest;

	/**
	 * 实际返佣总额
	 */
	private BigDecimal factPaymentAmount;

	/**
	 * 实际返佣日期
	 */
	private Date factPaymentDate;

	/**
	 * 预计返佣利率
	 */
	private BigDecimal paymentPrincipalRate;

	/**
	 * 预计补贴利率
	 */
	private BigDecimal paymentInterestRate;

	/**
	 * 补充利率
	 */
	private BigDecimal makeUpRate;

	/**
	 * 补充金额
	 */
	private BigDecimal makeUpAmount;

	/**
	 * 补充日期
	 */
	private Date makeUpDate;

	/**
	 * 未结算、已结算
	 */
	private String paymentStatus;

	/**
	 * 交易编号
	 */
	private String tradeNo;

	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "INVEST_ID", length = 50)
	public String getInvestId() {
		return this.investId;
	}

	public void setInvestId(String investId) {
		this.investId = investId;
	}

	@Column(name = "CURRENT_TERM", length = 22)
	public Integer getCurrentTerm() {
		return this.currentTerm;
	}

	public void setCurrentTerm(Integer currentTerm) {
		this.currentTerm = currentTerm;
	}

	@Column(name = "EXCEPT_PAYMENT_DATE", length = 8)
	public String getExceptPaymentDate() {
		return this.exceptPaymentDate;
	}

	public void setExceptPaymentDate(String exceptPaymentDate) {
		this.exceptPaymentDate = exceptPaymentDate;
	}

	@Column(name = "TOTAL_PAYMENT_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getTotalPaymentAmount() {
		return this.totalPaymentAmount;
	}

	public void setTotalPaymentAmount(BigDecimal totalPaymentAmount) {
		this.totalPaymentAmount = totalPaymentAmount;
	}

	@Column(name = "PAYMENT_PRINCIPAL", precision = 22, scale = 8)
	public BigDecimal getPaymentPrincipal() {
		return this.paymentPrincipal;
	}

	public void setPaymentPrincipal(BigDecimal paymentPrincipal) {
		this.paymentPrincipal = paymentPrincipal;
	}

	@Column(name = "PAYMENT_INTEREST", precision = 22, scale = 8)
	public BigDecimal getPaymentInterest() {
		return this.paymentInterest;
	}

	public void setPaymentInterest(BigDecimal paymentInterest) {
		this.paymentInterest = paymentInterest;
	}

	@Column(name = "FACT_PAYMENT_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getFactPaymentAmount() {
		return this.factPaymentAmount;
	}

	public void setFactPaymentAmount(BigDecimal factPaymentAmount) {
		this.factPaymentAmount = factPaymentAmount;
	}

	@Column(name = "FACT_PAYMENT_DATE", length = 7)
	public Date getFactPaymentDate() {
		return this.factPaymentDate;
	}

	public void setFactPaymentDate(Date factPaymentDate) {
		this.factPaymentDate = factPaymentDate;
	}

	@Column(name = "PAYMENT_PRINCIPAL_RATE", precision = 22, scale = 18)
	public BigDecimal getPaymentPrincipalRate() {
		return this.paymentPrincipalRate;
	}

	public void setPaymentPrincipalRate(BigDecimal paymentPrincipalRate) {
		this.paymentPrincipalRate = paymentPrincipalRate;
	}

	@Column(name = "PAYMENT_INTEREST_RATE", precision = 22, scale = 18)
	public BigDecimal getPaymentInterestRate() {
		return this.paymentInterestRate;
	}

	public void setPaymentInterestRate(BigDecimal paymentInterestRate) {
		this.paymentInterestRate = paymentInterestRate;
	}

	@Column(name = "MAKE_UP_RATE", precision = 22, scale = 18)
	public BigDecimal getMakeUpRate() {
		return this.makeUpRate;
	}

	public void setMakeUpRate(BigDecimal makeUpRate) {
		this.makeUpRate = makeUpRate;
	}

	@Column(name = "MAKE_UP_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getMakeUpAmount() {
		return this.makeUpAmount;
	}

	public void setMakeUpAmount(BigDecimal makeUpAmount) {
		this.makeUpAmount = makeUpAmount;
	}

	@Column(name = "MAKE_UP_DATE", length = 7)
	public Date getMakeUpDate() {
		return this.makeUpDate;
	}

	public void setMakeUpDate(Date makeUpDate) {
		this.makeUpDate = makeUpDate;
	}

	@Column(name = "PAYMENT_STATUS", length = 50)
	public String getPaymentStatus() {
		return this.paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	
	@Column(name = "TRADE_NO", length = 50)
	public String getTradeNo() {
		return this.tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
}
