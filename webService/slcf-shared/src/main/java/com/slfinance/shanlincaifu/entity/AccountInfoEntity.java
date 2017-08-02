package com.slfinance.shanlincaifu.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

/**
 * BaoTAccountInfo entity. @author MyEclipse Persistence Tools
 * 
 * 
 */
@Entity
@Table(name = "BAO_T_ACCOUNT_INFO")
public class AccountInfoEntity extends BaseEntity  {

	private static final long serialVersionUID = -3114336854960478228L;
	private String custId;
	private String accountNo;
	private BigDecimal accountTotalAmount=BigDecimal.ZERO;
	private BigDecimal accountFreezeAmount=BigDecimal.ZERO;
	private BigDecimal accountAvailableAmount=BigDecimal.ZERO;
	
	private String accountType;

	//体验金活动金额
	private BigDecimal accountActivityAmount=BigDecimal.ZERO;

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

	@Column(name = "ACCOUNT_NO", length = 50)
	public String getAccountNo() {
		return this.accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
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

	@Column(name = "ACCOUNT_AVAILABLE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getAccountAvailableAmount() {
		return this.accountAvailableAmount;
	}

	public void setAccountAvailableAmount(BigDecimal accountAvailableAmount) {
		this.accountAvailableAmount = accountAvailableAmount;
	}

	@Column(name = "ACCOUNT_TYPE", length = 50)
	public String getAccountType() {
		return this.accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public boolean updAccountInfo( AccountInfoEntity accountInfo ) {
		if( null != accountInfo.getAccountTotalAmount() )
			this.accountTotalAmount = accountInfo.getAccountTotalAmount();
		if( null != accountInfo.getAccountFreezeAmount())
			this.accountFreezeAmount = accountInfo.getAccountFreezeAmount();
		if( null != accountInfo.getAccountAvailableAmount())
			this.accountAvailableAmount = accountInfo.getAccountAvailableAmount();
		if( null != accountInfo.getLastUpdateDate() )
			this.lastUpdateDate = accountInfo.getLastUpdateDate();
		if( StringUtils.isNotEmpty(accountInfo.getLastUpdateUser()) )
			this.lastUpdateUser= accountInfo.getLastUpdateUser();
		return true;
	}

}