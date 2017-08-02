package com.slfinance.shanlincaifu.entity;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 用户预约投资关联表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_RESERVE_INVEST_RELATION")
public class ReserveInvestRelationEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 用户预约ID
	 */
	private String reserveId;

	/**
	 * 投资ID
	 */
	private String investId;
	
	/**
	 * 匹配金额
	 */
	private BigDecimal tradeAmount;

	@Column(name = "RESERVE_ID", length = 50)
	public String getReserveId() {
		return this.reserveId;
	}

	public void setReserveId(String reserveId) {
		this.reserveId = reserveId;
	}

	@Column(name = "INVEST_ID", length = 50)
	public String getInvestId() {
		return this.investId;
	}

	public void setInvestId(String investId) {
		this.investId = investId;
	}
	
	@Column(name = "TRADE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getTradeAmount() {
		return this.tradeAmount;
	}

	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

}
