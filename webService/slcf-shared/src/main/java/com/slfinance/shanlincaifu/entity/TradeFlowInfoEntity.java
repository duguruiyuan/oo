package com.slfinance.shanlincaifu.entity;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;


/**
 * BAO交易流水信息表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_TRADE_FLOW_INFO")
public class TradeFlowInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 关联主键
	 */
	private String relatePrimary;

	/**
	 * 客户主键ID 外键
	 */
	private String custId;

	/**
	 * 客户账户主键ID
	 */
	private String custAccountId;

	/**
	 * 交易类型 充值、提现、冻结
	 */
	private String tradeType;

	/**
	 * 未处理、处理中、处理成功、处理失败
	 */
	private String tradeStatus;

	/**
	 * 原交易编号
	 */
	private String oldTradeNo;

	/**
	 * 交易编号
	 */
	private String tradeNo;

	/**
	 * 交易金额
	 */
	private BigDecimal tradeAmount;

	/**
	 * 交易日期
	 */
	private Date tradeDate;

	/**
	 * 交易描述
	 */
	private String tradeDesc;

	/**
	 * 银行名称
	 */
	private String bankName;

	/**
	 * 银行卡号
	 */
	private String bankCardNo;

	/**
	 * 支行名称
	 */
	private String branchBankName;

	/**
	 * 交易费用
	 */
	private BigDecimal tradeExpenses;

	/**
	 * 关联类型(默认表名大写)
	 */
	private String relateType;

	/**
	 * 子业务类型 认证充值、网银充值
	 */
	private String subTradeType;

	/**
	 * 交易来源 APP/PC
	 */
	private String tradeSource;

	/**
	 * 
	 */
	private String openProvince;

	/**
	 * 
	 */
	private String openCity;

	/**
	 * 
	 */
	private String thirdPay;



	@Column(name = "RELATE_PRIMARY", length = 50)
	public String getRelatePrimary() {
		return this.relatePrimary;
	}

	public void setRelatePrimary(String relatePrimary) {
		this.relatePrimary = relatePrimary;
	}

	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "CUST_ACCOUNT_ID", length = 50)
	public String getCustAccountId() {
		return this.custAccountId;
	}

	public void setCustAccountId(String custAccountId) {
		this.custAccountId = custAccountId;
	}

	@Column(name = "TRADE_TYPE", length = 50)
	public String getTradeType() {
		return this.tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	@Column(name = "TRADE_STATUS", length = 50)
	public String getTradeStatus() {
		return this.tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
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

	@Column(name = "TRADE_DESC", length = 200)
	public String getTradeDesc() {
		return this.tradeDesc;
	}

	public void setTradeDesc(String tradeDesc) {
		this.tradeDesc = tradeDesc;
	}

	@Column(name = "BANK_NAME", length = 100)
	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@Column(name = "BANK_CARD_NO", length = 50)
	public String getBankCardNo() {
		return this.bankCardNo;
	}

	public void setBankCardNo(String bankCardNo) {
		this.bankCardNo = bankCardNo;
	}

	@Column(name = "BRANCH_BANK_NAME", length = 200)
	public String getBranchBankName() {
		return this.branchBankName;
	}

	public void setBranchBankName(String branchBankName) {
		this.branchBankName = branchBankName;
	}

	@Column(name = "TRADE_EXPENSES", precision = 22, scale = 8)
	public BigDecimal getTradeExpenses() {
		return this.tradeExpenses;
	}

	public void setTradeExpenses(BigDecimal tradeExpenses) {
		this.tradeExpenses = tradeExpenses;
	}

	@Column(name = "RELATE_TYPE", length = 200)
	public String getRelateType() {
		return this.relateType;
	}

	public void setRelateType(String relateType) {
		this.relateType = relateType;
	}

	@Column(name = "SUB_TRADE_TYPE", length = 50)
	public String getSubTradeType() {
		return this.subTradeType;
	}

	public void setSubTradeType(String subTradeType) {
		this.subTradeType = subTradeType;
	}

	@Column(name = "TRADE_SOURCE", length = 300)
	public String getTradeSource() {
		return this.tradeSource;
	}

	public void setTradeSource(String tradeSource) {
		this.tradeSource = tradeSource;
	}

	@Column(name = "OPEN_PROVINCE", length = 50)
	public String getOpenProvince() {
		return this.openProvince;
	}

	public void setOpenProvince(String openProvince) {
		this.openProvince = openProvince;
	}

	@Column(name = "OPEN_CITY", length = 50)
	public String getOpenCity() {
		return this.openCity;
	}

	public void setOpenCity(String openCity) {
		this.openCity = openCity;
	}

	@Column(name = "THIRD_PAY", length = 50)
	public String getThirdPay() {
		return this.thirdPay;
	}

	public void setThirdPay(String thirdPay) {
		this.thirdPay = thirdPay;
	}

	public boolean updateTradeFlowInfo( TradeFlowInfoEntity flowInfo ){
		if(StringUtils.isNotEmpty(flowInfo.getTradeStatus()))
			this.tradeStatus = flowInfo.getTradeStatus();
		if(StringUtils.isNotEmpty(flowInfo.getLastUpdateUser()))
			this.lastUpdateUser = flowInfo.getLastUpdateUser();
		if(null != flowInfo.getLastUpdateDate())
			this.lastUpdateDate = flowInfo.getLastUpdateDate();
		if(null != flowInfo.getMemo())
			this.memo = flowInfo.getMemo();
		return true;
	}
}
