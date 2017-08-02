package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * 理财返息计划表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_WEALTH_PAYMENT_PLAN_INFO")
public class WealthPaymentPlanInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 客户信息主键ID
	 */
	private String custId;

	/**
	 * 投资记录主键
	 */
	private String investId;

	/**
	 * 理财计划主键
	 */
	private String wealthId;

	/**
	 * 当前期数
	 */
	private Integer currentTerm;

	/**
	 * 预收日期
	 */
	private String exceptPaymentDate;

	/**
	 * 预收总额
	 */
	private BigDecimal exceptPaymentAmount;

	/**
	 * 预收本金
	 */
	private BigDecimal exceptPaymentPrincipal;

	/**
	 * 预收利息
	 */
	private BigDecimal exceptPaymentInterest;

	/**
	 * 预收奖励
	 */
	private BigDecimal exceptPaymentAward;

	/**
	 * 实收日期
	 */
	private String factPaymentDate;

	/**
	 * 实收总额
	 */
	private BigDecimal factPaymentAmount;

	/**
	 * 未回收、已回收
	 */
	private String paymentStatus;



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

	@Column(name = "WEALTH_ID", length = 50)
	public String getWealthId() {
		return this.wealthId;
	}

	public void setWealthId(String wealthId) {
		this.wealthId = wealthId;
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

	@Column(name = "EXCEPT_PAYMENT_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getExceptPaymentAmount() {
		return this.exceptPaymentAmount;
	}

	public void setExceptPaymentAmount(BigDecimal exceptPaymentAmount) {
		this.exceptPaymentAmount = exceptPaymentAmount;
	}

	@Column(name = "EXCEPT_PAYMENT_PRINCIPAL", precision = 22, scale = 8)
	public BigDecimal getExceptPaymentPrincipal() {
		return this.exceptPaymentPrincipal;
	}

	public void setExceptPaymentPrincipal(BigDecimal exceptPaymentPrincipal) {
		this.exceptPaymentPrincipal = exceptPaymentPrincipal;
	}

	@Column(name = "EXCEPT_PAYMENT_INTEREST", precision = 22, scale = 8)
	public BigDecimal getExceptPaymentInterest() {
		return this.exceptPaymentInterest;
	}

	public void setExceptPaymentInterest(BigDecimal exceptPaymentInterest) {
		this.exceptPaymentInterest = exceptPaymentInterest;
	}

	@Column(name = "EXCEPT_PAYMENT_AWARD", precision = 22, scale = 8)
	public BigDecimal getExceptPaymentAward() {
		return this.exceptPaymentAward;
	}

	public void setExceptPaymentAward(BigDecimal exceptPaymentAward) {
		this.exceptPaymentAward = exceptPaymentAward;
	}

	@Column(name = "FACT_PAYMENT_DATE", length = 8)
	public String getFactPaymentDate() {
		return this.factPaymentDate;
	}

	public void setFactPaymentDate(String factPaymentDate) {
		this.factPaymentDate = factPaymentDate;
	}

	@Column(name = "FACT_PAYMENT_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getFactPaymentAmount() {
		return this.factPaymentAmount;
	}

	public void setFactPaymentAmount(BigDecimal factPaymentAmount) {
		this.factPaymentAmount = factPaymentAmount;
	}

	@Column(name = "PAYMENT_STATUS", length = 50)
	public String getPaymentStatus() {
		return this.paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

}
