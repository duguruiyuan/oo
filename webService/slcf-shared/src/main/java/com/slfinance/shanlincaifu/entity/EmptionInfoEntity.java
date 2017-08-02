package com.slfinance.shanlincaifu.entity;
// default package

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BaoTEmptionInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_EMPTION_INFO")
public class EmptionInfoEntity extends BaseEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7752981966982616796L;
	private String atoneId;
	private BigDecimal emptionAmount;
	private Date emptionDate;
	private String accountId;
	private String custId;

	// Constructors

	/** default constructor */
	public EmptionInfoEntity() {
	}


	@Column(name = "ATONE_ID", length = 50)
	public String getAtoneId() {
		return this.atoneId;
	}

	public void setAtoneId(String atoneId) {
		this.atoneId = atoneId;
	}

	@Column(name = "EMPTION_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getEmptionAmount() {
		return this.emptionAmount;
	}

	public void setEmptionAmount(BigDecimal emptionAmount) {
		this.emptionAmount = emptionAmount;
	}

	@Column(name = "EMPTION_DATE", length = 7)
	public Date getEmptionDate() {
		return this.emptionDate;
	}

	public void setEmptionDate(Date emptionDate) {
		this.emptionDate = emptionDate;
	}

	@Column(name = "ACCOUNT_ID", length = 50)
	public String getAccountId() {
		return this.accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

}