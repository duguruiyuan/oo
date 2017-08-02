package com.slfinance.shanlincaifu.entity;
// default package

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * BaoTBankCardInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_BANK_CARD_INFO")
public class BankCardInfoEntity extends BaseEntity {


	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 22432419166247425L;
	@ManyToOne(cascade={CascadeType.ALL})           
	@JoinColumn(name="CUST_ID")
	@JsonIgnore
	private CustInfoEntity custInfoEntity;
	private String bankName;
	private String cardNo;
	private String openProvince;
	private String openCity;
	private String subBranchName;
	private String isDefault;
	private String protocolNo;
	private String bankFlag;

	// Constructors

	/** default constructor */
	public BankCardInfoEntity() {
	}

	/** minimal constructor */
	public BankCardInfoEntity(String id, Date createDate) {
		this.id = id;
		this.createDate = createDate;
	}
	
	public BankCardInfoEntity(String bankName, String cardNo) {
		this.bankName = bankName;
		this.cardNo = cardNo;
	}

	/** full constructor */
	public BankCardInfoEntity(String id, CustInfoEntity custId, String bankName, String cardNo, String openProvince, String openCity, String subBranchName, String isDefault, String recordStatus, String createUser, Date createDate, String lastUpdateUser, Date lastUpdateDate, Integer version, String memo) {
		this.id = id;
		this.custInfoEntity = custId;
		this.bankName = bankName;
		this.cardNo = cardNo;
		this.openProvince = openProvince;
		this.openCity = openCity;
		this.subBranchName = subBranchName;
		this.isDefault = isDefault;
		this.recordStatus = recordStatus;
		this.createUser = createUser;
		this.createDate = createDate;
		this.lastUpdateUser = lastUpdateUser;
		this.lastUpdateDate = lastUpdateDate;
		this.version = version;
		this.memo = memo;
	}

	// Property accessors
	public CustInfoEntity getCustInfoEntity() {
		return this.custInfoEntity;
	}

	public void setCustInfoEntity(CustInfoEntity custId) {
		this.custInfoEntity = custId;
	}

	@Column(name = "BANK_NAME", length = 150)
	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@Column(name = "CARD_NO", length = 150)
	public String getCardNo() {
		return this.cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	@Column(name = "OPEN_PROVINCE", length = 150)
	public String getOpenProvince() {
		return this.openProvince;
	}

	public void setOpenProvince(String openProvince) {
		this.openProvince = openProvince;
	}

	@Column(name = "OPEN_CITY", length = 150)
	public String getOpenCity() {
		return this.openCity;
	}

	public void setOpenCity(String openCity) {
		this.openCity = openCity;
	}

	@Column(name = "SUB_BRANCH_NAME", length = 150)
	public String getSubBranchName() {
		return this.subBranchName;
	}

	public void setSubBranchName(String subBranchName) {
		this.subBranchName = subBranchName;
	}

	@Column(name = "IS_DEFAULT", length = 50)
	public String getIsDefault() {
		return this.isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
	
	@Column(name = "PROTOCOL_NO", length = 300)
	public String getProtocolNo() {
		return protocolNo;
	}

	public void setProtocolNo(String protocolNo) {
		this.protocolNo = protocolNo;
	}

	@Column(name = "BANK_FLAG", length = 50)
	public String getBankFlag() {
		return bankFlag;
	}

	public void setBankFlag(String bankFlag) {
		this.bankFlag = bankFlag;
	}
	
	

}