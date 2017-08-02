package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 借款服务费计划表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_LOAN_SERVICE_PLAN_INFO")
public class LoanServicePlanInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 借款ID
	 */
	private String loanId;

	/**
	 * 当前期数 月
	 */
	private Integer currentTerm;

	/**
	 * 当期应收日期
	 */
	private String exceptDate;

	/**
	 * 当期应收金额
	 */
	private BigDecimal exceptAmount;

	/**
	 * 实际收款日期
	 */
	private Date factDate;

	/**
	 * 实际收款金额
	 */
	private BigDecimal factAmount;

	/**
	 * 待收、已收
	 */
	private String paymentStatus;



	@Column(name = "LOAN_ID", length = 50)
	public String getLoanId() {
		return this.loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	@Column(name = "CURRENT_TERM", length = 22)
	public Integer getCurrentTerm() {
		return this.currentTerm;
	}

	public void setCurrentTerm(Integer currentTerm) {
		this.currentTerm = currentTerm;
	}

	@Column(name = "EXCEPT_DATE", length = 8)
	public String getExceptDate() {
		return this.exceptDate;
	}

	public void setExceptDate(String exceptDate) {
		this.exceptDate = exceptDate;
	}

	@Column(name = "EXCEPT_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getExceptAmount() {
		return this.exceptAmount;
	}

	public void setExceptAmount(BigDecimal exceptAmount) {
		this.exceptAmount = exceptAmount;
	}

	@Column(name = "FACT_DATE", length = 7)
	public Date getFactDate() {
		return this.factDate;
	}

	public void setFactDate(Date factDate) {
		this.factDate = factDate;
	}

	@Column(name = "FACT_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getFactAmount() {
		return this.factAmount;
	}

	public void setFactAmount(BigDecimal factAmount) {
		this.factAmount = factAmount;
	}

	@Column(name = "PAYMENT_STATUS", length = 50)
	public String getPaymentStatus() {
		return this.paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

}
