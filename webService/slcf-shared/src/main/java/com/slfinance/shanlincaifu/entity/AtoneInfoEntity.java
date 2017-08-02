package com.slfinance.shanlincaifu.entity;
// default package

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BaoTAtoneInfo entity. @author MyEclipse Persistence Tools
 */
/**
 * @author Administrator
 *
 */
@Entity
@Table(name = "BAO_T_ATONE_INFO")
public class AtoneInfoEntity extends BaseEntity {

	private static final long serialVersionUID = -5625898699345673292L;
	private String custId;
	private String operType;
	private Date cleanupDate;
	private String atoneMethod;
	private BigDecimal atoneExpenses;
	private String atoneStatus;
	private String auditStatus;
	private BigDecimal atoneTotalAmount;
	private BigDecimal alreadyAtoneAmount;
	private String tradeCode;
	private String productId;
	private String tradeSource;
	private String investId;
	private BigDecimal atoneTotalValue;
	

	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "OPER_TYPE", length = 50)
	public String getOperType() {
		return this.operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	@Column(name = "CLEANUP_DATE", length = 7)
	public Date getCleanupDate() {
		return this.cleanupDate;
	}

	public void setCleanupDate(Date cleanupDate) {
		this.cleanupDate = cleanupDate;
	}

	@Column(name = "ATONE_METHOD", length = 50)
	public String getAtoneMethod() {
		return this.atoneMethod;
	}

	public void setAtoneMethod(String atoneMethod) {
		this.atoneMethod = atoneMethod;
	}

	@Column(name = "ATONE_EXPENSES", precision = 22, scale = 8)
	public BigDecimal getAtoneExpenses() {
		return this.atoneExpenses;
	}

	public void setAtoneExpenses(BigDecimal atoneExpenses) {
		this.atoneExpenses = atoneExpenses;
	}

	@Column(name = "ATONE_STATUS", length = 50)
	public String getAtoneStatus() {
		return this.atoneStatus;
	}

	public void setAtoneStatus(String atoneStatus) {
		this.atoneStatus = atoneStatus;
	}

	@Column(name = "AUDIT_STATUS", length = 150)
	public String getAuditStatus() {
		return this.auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	@Column(name = "ATONE_TOTAL_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getAtoneTotalAmount() {
		return this.atoneTotalAmount;
	}

	public void setAtoneTotalAmount(BigDecimal atoneTotalAmount) {
		this.atoneTotalAmount = atoneTotalAmount;
	}

	@Column(name = "ALREADY_ATONE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getAlreadyAtoneAmount() {
		return this.alreadyAtoneAmount;
	}

	public void setAlreadyAtoneAmount(BigDecimal alreadyAtoneAmount) {
		this.alreadyAtoneAmount = alreadyAtoneAmount;
	}

	
	@Column(name = "TRADE_CODE", length = 50)
	public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	@Column(name = "PRODUCT_ID", length = 50)
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Column(name = "TRADE_SOURCE", length = 300)
	public String getTradeSource() {
		return tradeSource;
	}

	public void setTradeSource(String tradeSource) {
		this.tradeSource = tradeSource;
	}

	@Column(name = "INVEST_ID", length = 50)
	public String getInvestId() {
		return investId;
	}

	public void setInvestId(String investId) {
		this.investId = investId;
	}

	@Column(name = "ATONE_TOTAL_VALUE", precision = 22, scale = 8)
	public BigDecimal getAtoneTotalValue() {
		return atoneTotalValue;
	}

	public void setAtoneTotalValue(BigDecimal atoneTotalValue) {
		this.atoneTotalValue = atoneTotalValue;
	}
}