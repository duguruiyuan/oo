package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 债权持有情况历史表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_WEALTH_HOLD_HISTORY_INFO")
public class WealthHoldHistoryInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	private String holdId;

	/**
	 * 
	 */
	private String investId;

	/**
	 * 
	 */
	private String subAccountId;

	/**
	 * 客户信息主键ID
	 */
	private String custId;

	/**
	 * 债权主键ID
	 */
	private String loanId;

	/**
	 * 
	 */
	private BigDecimal holdScale;

	/**
	 * 
	 */
	private BigDecimal holdAmount;

	/**
	 * 
	 */
	private BigDecimal exceptAmount;

	/**
	 * 
	 */
	private BigDecimal receivedAmount;

	/**
	 * 持有中、待转让、转让中、已转让
	 */
	private String holdStatus;

	/**
	 * 首期、回款
	 */
	private String holdSource;

	/**
	 * 
	 */
	private String isCenter;

	/**
	 * 
	 */
	private String oldCreateUser;

	/**
	 * 
	 */
	private Date oldCreateDate;

	/**
	 * 
	 */
	private String oldLastUpdateUser;

	/**
	 * 
	 */
	private Date oldLastUpdateDate;

	/**
	 * 
	 */
	private String oldMemo;

	private String tradeNo;
	
	private String requestNo;

	@Column(name = "HOLD_ID", length = 50)
	public String getHoldId() {
		return this.holdId;
	}

	public void setHoldId(String holdId) {
		this.holdId = holdId;
	}

	@Column(name = "INVEST_ID", length = 50)
	public String getInvestId() {
		return this.investId;
	}

	public void setInvestId(String investId) {
		this.investId = investId;
	}

	@Column(name = "SUB_ACCOUNT_ID", length = 50)
	public String getSubAccountId() {
		return this.subAccountId;
	}

	public void setSubAccountId(String subAccountId) {
		this.subAccountId = subAccountId;
	}

	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "LOAN_ID", length = 50)
	public String getLoanId() {
		return this.loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	@Column(name = "HOLD_SCALE", precision = 22, scale = 18)
	public BigDecimal getHoldScale() {
		return this.holdScale;
	}

	public void setHoldScale(BigDecimal holdScale) {
		this.holdScale = holdScale;
	}

	@Column(name = "HOLD_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getHoldAmount() {
		return this.holdAmount;
	}

	public void setHoldAmount(BigDecimal holdAmount) {
		this.holdAmount = holdAmount;
	}

	@Column(name = "EXCEPT_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getExceptAmount() {
		return this.exceptAmount;
	}

	public void setExceptAmount(BigDecimal exceptAmount) {
		this.exceptAmount = exceptAmount;
	}

	@Column(name = "RECEIVED_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getReceivedAmount() {
		return this.receivedAmount;
	}

	public void setReceivedAmount(BigDecimal receivedAmount) {
		this.receivedAmount = receivedAmount;
	}

	@Column(name = "HOLD_STATUS", length = 50)
	public String getHoldStatus() {
		return this.holdStatus;
	}

	public void setHoldStatus(String holdStatus) {
		this.holdStatus = holdStatus;
	}

	@Column(name = "HOLD_SOURCE", length = 50)
	public String getHoldSource() {
		return this.holdSource;
	}

	public void setHoldSource(String holdSource) {
		this.holdSource = holdSource;
	}

	@Column(name = "IS_CENTER", length = 50)
	public String getIsCenter() {
		return this.isCenter;
	}

	public void setIsCenter(String isCenter) {
		this.isCenter = isCenter;
	}

	@Column(name = "OLD_CREATE_USER", length = 50)
	public String getOldCreateUser() {
		return this.oldCreateUser;
	}

	public void setOldCreateUser(String oldCreateUser) {
		this.oldCreateUser = oldCreateUser;
	}

	@Column(name = "OLD_CREATE_DATE", length = 7)
	public Date getOldCreateDate() {
		return this.oldCreateDate;
	}

	public void setOldCreateDate(Date oldCreateDate) {
		this.oldCreateDate = oldCreateDate;
	}

	@Column(name = "OLD_LAST_UPDATE_USER", length = 50)
	public String getOldLastUpdateUser() {
		return this.oldLastUpdateUser;
	}

	public void setOldLastUpdateUser(String oldLastUpdateUser) {
		this.oldLastUpdateUser = oldLastUpdateUser;
	}

	@Column(name = "OLD_LAST_UPDATE_DATE", length = 7)
	public Date getOldLastUpdateDate() {
		return this.oldLastUpdateDate;
	}

	public void setOldLastUpdateDate(Date oldLastUpdateDate) {
		this.oldLastUpdateDate = oldLastUpdateDate;
	}

	@Column(name = "OLD_MEMO", length = 300)
	public String getOldMemo() {
		return this.oldMemo;
	}

	public void setOldMemo(String oldMemo) {
		this.oldMemo = oldMemo;
	}
	
	@Column(name = "TRADE_NO", length = 50)
	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	@Column(name = "REQUEST_NO", length = 50)
	public String getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

	
}
