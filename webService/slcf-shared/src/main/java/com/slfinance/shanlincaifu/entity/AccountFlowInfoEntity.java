package com.slfinance.shanlincaifu.entity;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * BAO账户流水信息表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_ACCOUNT_FLOW_INFO")
public class AccountFlowInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 客户信息表主键ID
	 */
	private String custId;

	/**
	 * 账户主键ID(总账/分账)
	 */
	private String accountId;

	/**
	 * 总账、分账
	 */
	private String accountType;

	/**
	 * 交易类型
	 */
	private String tradeType;

	/**
	 * 用于区分价值流水、现金流程
	 */
	private String flowType;

	/**
	 * 单个请求编号,可对应多个交易编号
	 */
	private String requestNo;

	/**
	 * 
	 */
	private String oldTradeNo;

	/**
	 * 交易编号隶属于请求编号
	 */
	private String tradeNo;

	/**
	 * 转入/转出/无
	 */
	private String bankrollFlowDirection;

	/**
	 * 交易金额
	 */
	private BigDecimal tradeAmount;

	/**
	 * 交易日期
	 */
	private Date tradeDate;

	/**
	 * 账户总金额
	 */
	private BigDecimal accountTotalAmount;

	/**
	 * 账户冻结金额
	 */
	private BigDecimal accountFreezeAmount;

	/**
	 * 账户可用金额
	 */
	private BigDecimal accountAvailable;

	/**
	 * 现金金额
	 */
	private BigDecimal cashAmount;

	/**
	 * 目标账户
	 */
	private String targetAccount;

	/**
	 * 关联类型(默认表明大写)
	 */
	private String relateType;

	/**
	 * 关联主键
	 */
	private String relatePrimary;

	/**
	 * 体验金活动金额
	 */
	private BigDecimal accountActivityAmount;

	@Column(name = "ACCOUNT_ACTIVITY_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getAccountActivityAmount() {
		return accountActivityAmount;
	}

	public void setAccountActivityAmount(BigDecimal accountActivityAmount) {
		this.accountActivityAmount = accountActivityAmount;
	}

	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "ACCOUNT_ID", length = 50)
	public String getAccountId() {
		return this.accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	@Column(name = "ACCOUNT_TYPE", length = 50)
	public String getAccountType() {
		return this.accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	@Column(name = "TRADE_TYPE", length = 50)
	public String getTradeType() {
		return this.tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	@Column(name = "FLOW_TYPE", length = 50)
	public String getFlowType() {
		return this.flowType;
	}

	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

	@Column(name = "REQUEST_NO", length = 50)
	public String getRequestNo() {
		return this.requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

	@Column(name = "OLD_TRADE_NO", length = 50)
	public String getOldTradeNo() {
		return this.oldTradeNo;
	}

	public void setOldTradeNo(String oldTradeNo) {
		this.oldTradeNo = oldTradeNo;
	}

	@Column(name = "TRADE_NO", length = 50)
	public String getTradeNo() {
		return this.tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	@Column(name = "BANKROLL_FLOW_DIRECTION", length = 200)
	public String getBankrollFlowDirection() {
		return this.bankrollFlowDirection;
	}

	public void setBankrollFlowDirection(String bankrollFlowDirection) {
		this.bankrollFlowDirection = bankrollFlowDirection;
	}

	@Column(name = "TRADE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getTradeAmount() {
		return this.tradeAmount;
	}

	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	@Column(name = "TRADE_DATE", length = 7)
	public Date getTradeDate() {
		return this.tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	@Column(name = "ACCOUNT_TOTAL_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getAccountTotalAmount() {
		return this.accountTotalAmount;
	}

	public void setAccountTotalAmount(BigDecimal accountTotalAmount) {
		this.accountTotalAmount = accountTotalAmount;
	}

	@Column(name = "ACCOUNT_FREEZE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getAccountFreezeAmount() {
		return this.accountFreezeAmount;
	}

	public void setAccountFreezeAmount(BigDecimal accountFreezeAmount) {
		this.accountFreezeAmount = accountFreezeAmount;
	}

	@Column(name = "ACCOUNT_AVAILABLE", precision = 22, scale = 8)
	public BigDecimal getAccountAvailable() {
		return this.accountAvailable;
	}

	public void setAccountAvailable(BigDecimal accountAvailable) {
		this.accountAvailable = accountAvailable;
	}

	@Column(name = "CASH_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getCashAmount() {
		return this.cashAmount;
	}

	public void setCashAmount(BigDecimal cashAmount) {
		this.cashAmount = cashAmount;
	}

	@Column(name = "TARGET_ACCOUNT", length = 50)
	public String getTargetAccount() {
		return this.targetAccount;
	}

	public void setTargetAccount(String targetAccount) {
		this.targetAccount = targetAccount;
	}

	@Column(name = "RELATE_TYPE", length = 200)
	public String getRelateType() {
		return this.relateType;
	}

	public void setRelateType(String relateType) {
		this.relateType = relateType;
	}

	@Column(name = "RELATE_PRIMARY", length = 50)
	public String getRelatePrimary() {
		return this.relatePrimary;
	}

	public void setRelatePrimary(String relatePrimary) {
		this.relatePrimary = relatePrimary;
	}
	
	public AccountFlowInfoEntity() {
		
	}
	
	public AccountFlowInfoEntity(String accountId,String custId, String tradeType, String tradeNo, String oldTradeNo, String bankrollFlowDirection, BigDecimal tradeAmount, Date tradeDate, String memo, String accountType) {
		this.accountId=accountId;
		this.custId = custId;
		this.tradeType = tradeType;
		this.tradeNo = tradeNo;
		this.oldTradeNo = oldTradeNo;
		this.bankrollFlowDirection = bankrollFlowDirection;
		this.tradeAmount = tradeAmount;
		this.tradeDate = tradeDate;
		this.memo=memo;
		this.accountType=accountType;
	}
	public AccountFlowInfoEntity(String accountId,String custId, String tradeType, String tradeNo, String oldTradeNo,String requestNo, String bankrollFlowDirection, BigDecimal tradeAmount, Date tradeDate, String memo, String accountType, BigDecimal accountTotalAmount,BigDecimal accountFreezeAmount, BigDecimal accountAvailable, String flowType, BigDecimal cashAmount) {
		this.accountId=accountId;
		this.custId = custId;
		this.tradeType = tradeType;
		this.tradeNo = tradeNo;
		this.oldTradeNo = oldTradeNo;
		this.requestNo = requestNo;
		this.bankrollFlowDirection = bankrollFlowDirection;
		this.tradeAmount = tradeAmount;
		this.tradeDate = tradeDate;
		this.memo = memo;
		this.accountType = accountType;
		this.accountTotalAmount = accountTotalAmount;
		this.accountFreezeAmount = accountFreezeAmount;
		this.accountAvailable = accountAvailable;
		this.flowType = flowType;
		this.cashAmount = cashAmount;
	}
	
	public void setAccountInfo(AccountInfoEntity accountInfo) {
		this.accountTotalAmount = accountInfo.getAccountTotalAmount();
		this.accountFreezeAmount = accountInfo.getAccountFreezeAmount();
		this.accountAvailable = accountInfo.getAccountAvailableAmount();
		this.accountId = accountInfo.getId();
		this.custId = accountInfo.getCustId();
	}

	public AccountFlowInfoEntity clone() {
		AccountFlowInfoEntity accountFlowInfoEntity = new AccountFlowInfoEntity();
		accountFlowInfoEntity.setId(this.id);
		accountFlowInfoEntity.setCreateDate(this.createDate);
		accountFlowInfoEntity.setCreateUser(this.createUser);
		accountFlowInfoEntity.setLastUpdateDate(this.lastUpdateDate);
		accountFlowInfoEntity.setLastUpdateUser(this.lastUpdateUser);
		accountFlowInfoEntity.setCustId(this.custId);
		accountFlowInfoEntity.setAccountId(this.accountId);
		accountFlowInfoEntity.setTradeType(this.tradeType);
		accountFlowInfoEntity.setTradeNo(this.tradeNo);
		accountFlowInfoEntity.setOldTradeNo(this.oldTradeNo);
		accountFlowInfoEntity.setRequestNo(this.requestNo);
		accountFlowInfoEntity.setBankrollFlowDirection(this.bankrollFlowDirection);
		accountFlowInfoEntity.setTradeAmount(this.tradeAmount);
		accountFlowInfoEntity.setTradeDate(this.tradeDate);
		accountFlowInfoEntity.setMemo(this.memo);
		accountFlowInfoEntity.setAccountType(this.accountType);
		accountFlowInfoEntity.setAccountTotalAmount(this.accountTotalAmount);
		accountFlowInfoEntity.setAccountFreezeAmount(this.accountFreezeAmount);
		accountFlowInfoEntity.setAccountAvailable(this.accountAvailable);
		accountFlowInfoEntity.setFlowType(this.flowType);
		accountFlowInfoEntity.setCashAmount(this.cashAmount);
		return accountFlowInfoEntity;
	}
}
