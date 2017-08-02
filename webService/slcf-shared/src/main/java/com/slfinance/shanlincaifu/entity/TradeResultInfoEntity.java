package com.slfinance.shanlincaifu.entity;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 线下交易处理结果表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_TRADE_RESULT_INFO")
public class TradeResultInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 文件名称
	 */
	private String fileName;

	/**
	 * 交易类型
	 */
	private String tradeType;

	/**
	 * 交易金额
	 */
	private BigDecimal tradeAmount;

	/**
	 * 付款人ID
	 */
	private String payeeCustId;

	/**
	 * 付款人姓名
	 */
	private String payeeCustName;

	/**
	 * 付款人银行卡ID
	 */
	private String payeeBankId;

	/**
	 * 付款人账号
	 */
	private String payeeBankCardNo;

	/**
	 * 收款人ID
	 */
	private String payeyCustId;

	/**
	 * 收款人姓名
	 */
	private String payeyCustName;

	/**
	 * 收款人账号
	 */
	private String payeyBankCardNo;

	/**
	 * 收款人银行卡ID
	 */
	private String payeyBankId;

	/**
	 * 转账类型
	 */
	private String transferType;

	/**
	 * 币种
	 */
	private String currencyType;

	/**
	 * 文件中处理时间
	 */
	private Date fileTradeDate;

	/**
	 * 文件中处理结果
	 */
	private String fileTradeStatus;

	/**
	 * 本系统处理时间
	 */
	private Date tradeDate;

	/**
	 * 本系统处理结果
	 */
	private String tradeStatus;



	@Column(name = "FILE_NAME", length = 50)
	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Column(name = "TRADE_TYPE", length = 50)
	public String getTradeType() {
		return this.tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	@Column(name = "TRADE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getTradeAmount() {
		return this.tradeAmount;
	}

	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	@Column(name = "PAYEE_CUST_ID", length = 50)
	public String getPayeeCustId() {
		return this.payeeCustId;
	}

	public void setPayeeCustId(String payeeCustId) {
		this.payeeCustId = payeeCustId;
	}

	@Column(name = "PAYEE_CUST_NAME", length = 50)
	public String getPayeeCustName() {
		return this.payeeCustName;
	}

	public void setPayeeCustName(String payeeCustName) {
		this.payeeCustName = payeeCustName;
	}

	@Column(name = "PAYEE_BANK_ID", length = 50)
	public String getPayeeBankId() {
		return this.payeeBankId;
	}

	public void setPayeeBankId(String payeeBankId) {
		this.payeeBankId = payeeBankId;
	}

	@Column(name = "PAYEE_BANK_CARD_NO", length = 50)
	public String getPayeeBankCardNo() {
		return this.payeeBankCardNo;
	}

	public void setPayeeBankCardNo(String payeeBankCardNo) {
		this.payeeBankCardNo = payeeBankCardNo;
	}

	@Column(name = "PAYEY_CUST_ID", length = 50)
	public String getPayeyCustId() {
		return this.payeyCustId;
	}

	public void setPayeyCustId(String payeyCustId) {
		this.payeyCustId = payeyCustId;
	}

	@Column(name = "PAYEY_CUST_NAME", length = 50)
	public String getPayeyCustName() {
		return this.payeyCustName;
	}

	public void setPayeyCustName(String payeyCustName) {
		this.payeyCustName = payeyCustName;
	}

	@Column(name = "PAYEY_BANK_CARD_NO", length = 50)
	public String getPayeyBankCardNo() {
		return this.payeyBankCardNo;
	}

	public void setPayeyBankCardNo(String payeyBankCardNo) {
		this.payeyBankCardNo = payeyBankCardNo;
	}

	@Column(name = "PAYEY_BANK_ID", length = 50)
	public String getPayeyBankId() {
		return this.payeyBankId;
	}

	public void setPayeyBankId(String payeyBankId) {
		this.payeyBankId = payeyBankId;
	}

	@Column(name = "TRANSFER_TYPE", length = 50)
	public String getTransferType() {
		return this.transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	@Column(name = "CURRENCY_TYPE", length = 50)
	public String getCurrencyType() {
		return this.currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	@Column(name = "FILE_TRADE_DATE", length = 7)
	public Date getFileTradeDate() {
		return this.fileTradeDate;
	}

	public void setFileTradeDate(Date fileTradeDate) {
		this.fileTradeDate = fileTradeDate;
	}

	@Column(name = "FILE_TRADE_STATUS", length = 50)
	public String getFileTradeStatus() {
		return this.fileTradeStatus;
	}

	public void setFileTradeStatus(String fileTradeStatus) {
		this.fileTradeStatus = fileTradeStatus;
	}

	@Column(name = "TRADE_DATE", length = 7)
	public Date getTradeDate() {
		return this.tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	@Column(name = "TRADE_STATUS", length = 50)
	public String getTradeStatus() {
		return this.tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

}
