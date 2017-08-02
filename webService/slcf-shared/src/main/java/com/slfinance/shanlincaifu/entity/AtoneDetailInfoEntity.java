package com.slfinance.shanlincaifu.entity;
// default package

import javax.persistence.Column;import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BaoTAtoneDetailInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_ATONE_DETAIL_INFO")
public class AtoneDetailInfoEntity extends BaseEntity {

	// Fields

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3981233824821741184L;
	private String loanId;
	private String atoneId;
	private BigDecimal atoneAmount;
	private BigDecimal atoneScale;

	@Column(name = "LOAN_ID", length = 50)
	public String getLoanId() {
		return this.loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	@Column(name = "ATONE_ID", length = 50)
	public String getAtoneId() {
		return this.atoneId;
	}

	public void setAtoneId(String atoneId) {
		this.atoneId = atoneId;
	}

	@Column(name = "ATONE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getAtoneAmount() {
		return this.atoneAmount;
	}

	public void setAtoneAmount(BigDecimal atoneAmount) {
		this.atoneAmount = atoneAmount;
	}

	@Column(name = "ATONE_SCALE", precision = 22, scale = 18)
	public BigDecimal getAtoneScale() {
		return this.atoneScale;
	}

	public void setAtoneScale(BigDecimal atoneScale) {
		this.atoneScale = atoneScale;
	}
}