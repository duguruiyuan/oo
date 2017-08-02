package com.slfinance.shanlincaifu.entity;
// default package

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * BaoTCreditRightValue entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_CREDIT_RIGHT_VALUE")
public class CreditRightValueEntity implements java.io.Serializable  {

	// Fields

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4042314497682297407L;
	private String loanId;
	private String valueDate;
	private BigDecimal valueRepaymentBefore;
	private BigDecimal valueRepaymentAfter;
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(length = 50)
	protected String id;

	/** 创建时间 */
	@Column(name = "CREATE_DATE")
	protected Timestamp createDate = new Timestamp(System.currentTimeMillis());
	
	public String getLoanId() {
		return this.loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	@Column(name = "VALUE_DATE", length = 8)
	public String getValueDate() {
		return this.valueDate;
	}

	public void setValueDate(String valueDate) {
		this.valueDate = valueDate;
	}

	@Column(name = "VALUE_REPAYMENT_BEFORE", precision = 22, scale = 8)
	public BigDecimal getValueRepaymentBefore() {
		return this.valueRepaymentBefore;
	}

	public void setValueRepaymentBefore(BigDecimal valueRepaymentBefore) {
		this.valueRepaymentBefore = valueRepaymentBefore;
	}

	@Column(name = "VALUE_REPAYMENT_AFTER", precision = 22, scale = 8)
	public BigDecimal getValueRepaymentAfter() {
		return this.valueRepaymentAfter;
	}

	public void setValueRepaymentAfter(BigDecimal valueRepaymentAfter) {
		this.valueRepaymentAfter = valueRepaymentAfter;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	
}