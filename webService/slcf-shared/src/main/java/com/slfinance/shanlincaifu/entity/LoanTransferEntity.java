package com.slfinance.shanlincaifu.entity;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * BAO债权转让表 entity. @author Tools
 */
/**
 * @author Administrator
 *
 */
@Entity
@Table(name = "BAO_T_LOAN_TRANSFER")
public class LoanTransferEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	private String senderHoldId;

	/**
	 * 
	 */
	private String receiveHoldId;

	/**
	 * 投标信息表主键
	 */
	private String senderCustId;

	/**
	 * 投标信息表主键
	 */
	private String receiveCustId;

	/**
	 * 
	 */
	private String senderLoanId;

	/**
	 * 
	 */
	private String receiveLoanId;

	/**
	 * 转让金额
	 */
	private BigDecimal tradeAmount;

	/**
	 * 转让比例
	 */
	private BigDecimal tradeScale;

	/**
	 * 转让手续费
	 */
	private BigDecimal transferExpenses;
	
	private String tradeNo;

	private String requestNo;
	
	/**
	 * 转让价值
	 */
	private BigDecimal tradeValue;

	/**
	 * 转让申请ID
	 */
	private String transferApplyId;
	
	/**
	 * 转让本金
	 */
	private BigDecimal tradePrincipal;


	@Column(name = "SENDER_HOLD_ID", length = 50)
	public String getSenderHoldId() {
		return this.senderHoldId;
	}

	public void setSenderHoldId(String senderHoldId) {
		this.senderHoldId = senderHoldId;
	}

	@Column(name = "RECEIVE_HOLD_ID", length = 50)
	public String getReceiveHoldId() {
		return this.receiveHoldId;
	}

	public void setReceiveHoldId(String receiveHoldId) {
		this.receiveHoldId = receiveHoldId;
	}

	@Column(name = "SENDER_CUST_ID", length = 50)
	public String getSenderCustId() {
		return this.senderCustId;
	}

	public void setSenderCustId(String senderCustId) {
		this.senderCustId = senderCustId;
	}

	@Column(name = "RECEIVE_CUST_ID", length = 50)
	public String getReceiveCustId() {
		return this.receiveCustId;
	}

	public void setReceiveCustId(String receiveCustId) {
		this.receiveCustId = receiveCustId;
	}

	@Column(name = "SENDER_LOAN_ID", length = 50)
	public String getSenderLoanId() {
		return this.senderLoanId;
	}

	public void setSenderLoanId(String senderLoanId) {
		this.senderLoanId = senderLoanId;
	}

	@Column(name = "RECEIVE_LOAN_ID", length = 50)
	public String getReceiveLoanId() {
		return this.receiveLoanId;
	}

	public void setReceiveLoanId(String receiveLoanId) {
		this.receiveLoanId = receiveLoanId;
	}

	@Column(name = "TRADE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getTradeAmount() {
		return this.tradeAmount;
	}

	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	@Column(name = "TRADE_SCALE", precision = 22, scale = 18)
	public BigDecimal getTradeScale() {
		return this.tradeScale;
	}

	public void setTradeScale(BigDecimal tradeScale) {
		this.tradeScale = tradeScale;
	}

	@Column(name = "TRANSFER_EXPENSES", precision = 22, scale = 8)
	public BigDecimal getTransferExpenses() {
		return this.transferExpenses;
	}

	public void setTransferExpenses(BigDecimal transferExpenses) {
		this.transferExpenses = transferExpenses;
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
	
	@Column(name = "TRADE_VALUE", precision = 22, scale = 8)
	public BigDecimal getTradeValue() {
		return this.tradeValue;
	}

	public void setTradeValue(BigDecimal tradeValue) {
		this.tradeValue = tradeValue;
	}

	@Column(name = "TRANSFER_APPLY_ID", length = 50)
	public String getTransferApplyId() {
		return this.transferApplyId;
	}

	public void setTransferApplyId(String transferApplyId) {
		this.transferApplyId = transferApplyId;
	}
	
	@Column(name = "TRADE_PRINCIPAL", precision = 22, scale = 8)
	public BigDecimal getTradePrincipal() {
		return this.tradePrincipal;
	}

	public void setTradePrincipal(BigDecimal tradePrincipal) {
		this.tradePrincipal = tradePrincipal;
	}
}
