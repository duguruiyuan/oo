package com.slfinance.shanlincaifu.entity;
// default package

import javax.persistence.Column;import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BaoTAllotDetailInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_ALLOT_DETAIL_INFO")
public class AllotDetailInfoEntity extends BaseEntity {

	// Fields
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 661041868976094655L;
	private String allotId;
	private String loanId;
	private String tradeCode;
	private BigDecimal tradeAmount;
	

	@Column(name = "ALLOT_ID", length = 50)
	public String getAllotId() {
		return this.allotId;
	}

	public void setAllotId(String allotId) {
		this.allotId = allotId;
	}

	@Column(name = "LOAN_ID", length = 50)
	public String getLoanId() {
		return this.loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	@Column(name = "TRADE_CODE", length = 50)
	public String getTradeCode() {
		return this.tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	@Column(name = "TRADE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getTradeAmount() {
		return this.tradeAmount;
	}

	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

}