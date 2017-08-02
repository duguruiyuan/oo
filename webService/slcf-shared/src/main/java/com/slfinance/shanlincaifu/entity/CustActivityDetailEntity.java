package com.slfinance.shanlincaifu.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BAO_T_CUST_ACTIVITY_DETAIL")
public class CustActivityDetailEntity extends BaseEntity {

	private static final long serialVersionUID = -5215661640003470645L;
	private String custId;
	private String custActivityId;
	private String tradeStatus;
	private BigDecimal tradeAmount;
	private BigDecimal usableAmount;
	
	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	
	@Column(name = "CUST_ACTIVITY_ID", length = 50)
	public String getCustActivityId() {
		return custActivityId;
	}
	
	public void setCustActivityId(String custActivityId) {
		this.custActivityId = custActivityId;
	}
	
	@Column(name = "TRADE_AMOUNT",precision = 22, scale = 8)
	public BigDecimal getTradeAmount() {
		return tradeAmount;
	}
	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}
	
	
	@Column(name = "TRADE_STATUS", length = 50)
	public String getTradeStatus() {
		return tradeStatus;
	}
	
	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
	
	
	@Column(name = "USABLE_AMOUNT",precision = 22, scale = 8)
	public BigDecimal getUsableAmount() {
		return usableAmount;
	}
	
	public void setUsableAmount(BigDecimal usableAmount) {
		this.usableAmount = usableAmount;
	}
	
}
