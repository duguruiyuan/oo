package com.slfinance.shanlincaifu.entity;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 
 */
@Entity
@Table(name = "BAO_T_LOAN_RESERVE_RELATION")
public class LoanReserveRelationEntity extends BaseEntity  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "LOAN_RESERVE_ID", length = 50)
	private String loanReserveId;
	
	@Column(name = "CUST_ID", length = 50)
	private String custId;
	
	@Column(name = "LOAN_ID", length = 50)
	private String loanId;
	
	@Column(name = "INVEST_ID", length = 50)
	private String investId;
	
	@Column(name = "TRADE_AMOUNT", precision = 22, scale = 8)
	private BigDecimal tradeAmount;
	
	/**
	 * 交易状态
	 */
	private String tradeStatus;


	public String getLoanReserveId() {
		return loanReserveId;
	}

	public void setLoanReserveId(String loanReserveId) {
		this.loanReserveId = loanReserveId;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getLoanId() {
		return loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	public String getInvestId() {
		return investId;
	}

	public void setInvestId(String investId) {
		this.investId = investId;
	}

	public BigDecimal getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Column(name = "TRADE_STATUS", length = 50)
	public String getTradeStatus() {
		return this.tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
}
