package com.slfinance.shanlincaifu.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BAO_T_TRANS_ACCOUNT_INFO")
public class TransAccountInfoEntity extends BaseEntity {

	private static final long serialVersionUID = 4949097380220143177L;
	private String intoAccount;
	private String expendAccount;
	private BigDecimal tradeAmount;
	private String transType;
	private String auditStatus;
	private String tradeStatus;
	
	@Column(name = "INTO_ACCOUNT", length = 50)
	public String getIntoAccount() {
		return intoAccount;
	}
	
	public void setIntoAccount(String intoAccount) {
		this.intoAccount = intoAccount;
	}
	
	@Column(name = "EXPEND_ACCOUNT", length = 50)
	public String getExpendAccount() {
		return expendAccount;
	}
	
	public void setExpendAccount(String expendAccount) {
		this.expendAccount = expendAccount;
	}
	
	@Column(name = "TRADE_AMOUNT",precision = 22, scale = 8)
	public BigDecimal getTradeAmount() {
		return tradeAmount;
	}
	
	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}
	
	@Column(name = "TRANS_TYPE", length = 50)
	public String getTransType() {
		return transType;
	}
	
	public void setTransType(String transType) {
		this.transType = transType;
	}

	@Column(name = "AUDIT_STATUS", length = 150)
	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
	
	@Column(name = "TRADE_STATUS", length = 150)
	public String getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
	
}
