package com.slfinance.shanlincaifu.entity;
// default package

import javax.persistence.Column;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

/**
 * BaoTSubAccountInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_SUB_ACCOUNT_INFO")
public class SubAccountInfoEntity extends BaseEntity {

	// Fields

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4835675056318709962L;
	private String custId;
	private String accountId;
	private String relateType;
	private String relatePrimary;
	private String subAccountNo;
	private BigDecimal accountTotalValue;
	private BigDecimal accountFreezeValue;
	private BigDecimal accountAvailableValue;
	private BigDecimal accountAmount;
	private BigDecimal deviationAmount;
	
	
	

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

	@Column(name = "SUB_ACCOUNT_NO", length = 50)
	public String getSubAccountNo() {
		return this.subAccountNo;
	}

	public void setSubAccountNo(String subAccountNo) {
		this.subAccountNo = subAccountNo;
	}

	@Column(name = "ACCOUNT_TOTAL_VALUE", precision = 22, scale = 8)
	public BigDecimal getAccountTotalValue() {
		return this.accountTotalValue;
	}

	public void setAccountTotalValue(BigDecimal accountTotalValue) {
		this.accountTotalValue = accountTotalValue;
	}

	@Column(name = "ACCOUNT_FREEZE_VALUE", precision = 22, scale = 8)
	public BigDecimal getAccountFreezeValue() {
		return this.accountFreezeValue;
	}

	public void setAccountFreezeValue(BigDecimal accountFreezeValue) {
		this.accountFreezeValue = accountFreezeValue;
	}

	@Column(name = "ACCOUNT_AVAILABLE_VALUE", precision = 22, scale = 8)
	public BigDecimal getAccountAvailableValue() {
		return this.accountAvailableValue;
	}

	public void setAccountAvailableValue(BigDecimal accountAvailableValue) {
		this.accountAvailableValue = accountAvailableValue;
	}

	@Column(name = "ACCOUNT_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getAccountAmount() {
		return this.accountAmount;
	}

	public void setAccountAmount(BigDecimal accountAmount) {
		this.accountAmount = accountAmount;
	}

	@Column(name = "DEVIATION_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getDeviationAmount() {
		return deviationAmount;
	}

	public void setDeviationAmount(BigDecimal deviationAmount) {
		this.deviationAmount = deviationAmount;
	}
	
	public boolean updSubAccountInfo( SubAccountInfoEntity subAccountInfo ) {
		if( null != subAccountInfo.getAccountTotalValue() )
			this.accountTotalValue = subAccountInfo.getAccountTotalValue();
		if( null != subAccountInfo.getAccountFreezeValue())
			this.accountFreezeValue = subAccountInfo.getAccountFreezeValue();
		if( null != subAccountInfo.getAccountAvailableValue() )
			this.accountAvailableValue = subAccountInfo.getAccountAvailableValue();
		if( null != subAccountInfo.getAccountAmount() )
			this.accountAmount = subAccountInfo.getAccountAmount();
		if( null != subAccountInfo.getLastUpdateDate() )
			this.lastUpdateDate = subAccountInfo.getLastUpdateDate();
		if( StringUtils.isNotEmpty(subAccountInfo.getLastUpdateUser()) )
			this.lastUpdateUser= subAccountInfo.getLastUpdateUser();
		return true;
	}
	
}