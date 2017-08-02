package com.slfinance.shanlincaifu.entity;
// default package

import javax.persistence.Column;import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BaoTInvestDetailInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_INVEST_DETAIL_INFO")
public class InvestDetailInfoEntity extends BaseEntity {

	// Fields

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1630037454367148813L;
	private String investId;
	private String tradeNo;
	private BigDecimal investAmount;
	private String investSource;
	
	
	
	@Column(name = "INVEST_ID", length = 50)
	public String getInvestId() {
		return this.investId;
	}

	public void setInvestId(String investId) {
		this.investId = investId;
	}

	@Column(name = "TRADE_NO", length = 50)
	public String getTradeNo() {
		return this.tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	@Column(name = "INVEST_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getInvestAmount() {
		return this.investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	@Column(name = "INVEST_SOURCE", length = 50)
	public String getInvestSource() {
		return this.investSource;
	}

	public void setInvestSource(String investSource) {
		this.investSource = investSource;
	}

}