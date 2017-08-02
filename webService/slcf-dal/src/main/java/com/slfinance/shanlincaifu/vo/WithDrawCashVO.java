/** 
 * @(#)WithDrawCachVo.java 1.0.0 2015年4月27日 上午11:53:50  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现业务实体模型
 * 
 * @author zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月27日 上午11:53:50 $
 */
@SuppressWarnings("serial")
public class WithDrawCashVO implements Serializable {

	private String id;

	/**
	 * 用户ID
	 */
	private String custId;
	/**
	 * 用户编号
	 */
	private String custCode;
	/**
	 * 用户姓名
	 */
	private String custName;
	/**
	 * 证件号码
	 */
	private String credentialsCode;
	/**
	 * 证件类型
	 */
	private String credentialsType;
	/**
	 * 用户昵称
	 */
	private String nickName;
	/**
	 * 订单编号
	 */
	private String tradeNo;
	/**
	 * 类型
	 */
	private String tradeType;
	/**
	 * 提现银行
	 */
	private String bankName;
	/**
	 * 提现金额
	 */
	private BigDecimal tradeAmount;
	/**
	 * 提现手续费
	 */
	private BigDecimal withdrawExpenses;
	/**
	 * 实际到账金额
	 */
	private BigDecimal realTradeAmount;
	/**
	 * 交易状态
	 */
	private String tradeStatus;
	/**
	 * 审核状态
	 */
	private String auditStatus;
	/**
	 * 操作时间
	 */
	private Date withdrawTime;
	/**
	 * 用户ID
	 */
	private Date createDate;
	/**
	 * 分支银行卡号
	 */
	private String bankCardNumber;
	/**
	 * 分支银行名称
	 */
	private String bankBranchName;
	/**
	 * 备注
	 */
	private String memo;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCredentialsCode() {
		return credentialsCode;
	}

	public void setCredentialsCode(String credentialsCode) {
		this.credentialsCode = credentialsCode;
	}

	public String getCredentialsType() {
		return credentialsType;
	}

	public void setCredentialsType(String credentialsType) {
		this.credentialsType = credentialsType;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public BigDecimal getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public BigDecimal getWithdrawExpenses() {
		return withdrawExpenses;
	}

	public void setWithdrawExpenses(BigDecimal withdrawExpenses) {
		this.withdrawExpenses = withdrawExpenses;
	}

	public BigDecimal getRealTradeAmount() {
		return realTradeAmount;
	}

	public void setRealTradeAmount(BigDecimal realTradeAmount) {
		this.realTradeAmount = realTradeAmount;
	}

	public String getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public Date getWithdrawTime() {
		return withdrawTime;
	}

	public void setWithdrawTime(Date withdrawTime) {
		this.withdrawTime = withdrawTime;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getBankCardNumber() {
		return bankCardNumber;
	}

	public void setBankCardNumber(String bankCardNumber) {
		this.bankCardNumber = bankCardNumber;
	}

	public String getBankBranchName() {
		return bankBranchName;
	}

	public void setBankBranchName(String bankBranchName) {
		this.bankBranchName = bankBranchName;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
