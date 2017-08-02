package com.slfinance.shanlincaifu.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BAO_T_CUST_ACTIVITY_INFO")
public class CustActivityInfoEntity extends BaseEntity {

	private static final long serialVersionUID = -5215661640003470645L;
	private String custId;
	private String activityId;
	private String activitySource;
	private String activityDesc;
	private String tradeCode;
	private String tradeStatus;
	private BigDecimal totalAmount;
	private BigDecimal usableAmount;
	private Date startDate;
	private Date expireDate;
	private String rewardShape;
	private String quiltCustId;

	private String activityAwardId;
	private String loanId;
	private String investAmount;
	
	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	
	@Column(name = "ACTIVITY_ID", length = 50)
	public String getActivityId() {
		return activityId;
	}
	
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	
	@Column(name = "ACTIVITY_SOURCE", length = 50)
	public String getActivitySource() {
		return activitySource;
	}
	
	public void setActivitySource(String activitySource) {
		this.activitySource = activitySource;
	}
	
	@Column(name = "TRADE_CODE", length = 50)
	public String getTradeCode() {
		return tradeCode;
	}
	
	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}
	
	@Column(name = "TRADE_STATUS", length = 50)
	public String getTradeStatus() {
		return tradeStatus;
	}
	
	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
	
	@Column(name = "ACTIVITY_DESC", length = 300)
	public String getActivityDesc() {
		return activityDesc;
	}
	public void setActivityDesc(String activityDesc) {
		this.activityDesc = activityDesc;
	}
	
	@Column(name = "TOTAL_AMOUNT",precision = 22, scale = 8)
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	@Column(name = "USABLE_AMOUNT",precision = 22, scale = 8)
	public BigDecimal getUsableAmount() {
		return usableAmount;
	}
	
	public void setUsableAmount(BigDecimal usableAmount) {
		this.usableAmount = usableAmount;
	}
	
	@Column(name = "START_DATE")
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Column(name = "EXPIRE_DATE")
	public Date getExpireDate() {
		return expireDate;
	}
	
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}
	
	@Column(name = "REWARD_SHAPE", length = 50)
	public String getRewardShape() {
		return rewardShape;
	}
	
	public void setRewardShape(String rewardShape) {
		this.rewardShape = rewardShape;
	}
	
	@Column(name = "QUILT_CUST_ID", length = 50)
	public String getQuiltCustId() {
		return quiltCustId;
	}
	public void setQuiltCustId(String quiltCustId) {
		this.quiltCustId = quiltCustId;
	}

	@Column(name = "ACTIVITY_AWARD_ID", length = 50)
	public String getActivityAwardId() {
		return activityAwardId;
	}

	public void setActivityAwardId(String activityAwardId) {
		this.activityAwardId = activityAwardId;
	}

	@Column(name = "LOAN_ID", length = 50)
	public String getLoanId() {
		return loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	@Column(name = "INVEST_AMOUNT",precision = 22, scale = 8)
	public String getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(String investAmount) {
		this.investAmount = investAmount;
	}
}
