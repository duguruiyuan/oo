package com.slfinance.shanlincaifu.entity;
// default package

import java.sql.Timestamp;

import javax.persistence.Column;import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BaoTLoanEnterpriseInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_LOAN_ENTERPRISE_INFO")
public class LoanEnterpriseInfoEntity extends BaseEntity {

	// Fields

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -683257445212764068L;
	private String enterpriseName;
	private String fictitiousName;
	private String credentialsType;
	private String credentialsCode;
	private String registrationNo;
	private String enterpriseType;
	private String registerType;
	private BigDecimal registerCapitial;
	private Timestamp registerTime;
	private String registerProv;
	private String registerCity;
	private String registerArea;
	private String registerAddress;
	private BigDecimal assetAmount;
	private BigDecimal debtAmount;
	private String companyTelephone;
	private String mobile;
	private String dboProv;
	private String dboCity;
	private String dboArea;
	private String dboAddress;
	private String enterpriseIntroduce;
	
	
	@Column(name = "ENTERPRISE_NAME", length = 150)
	public String getEnterpriseName() {
		return this.enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	@Column(name = "FICTITIOUS_NAME", length = 150)
	public String getFictitiousName() {
		return this.fictitiousName;
	}

	public void setFictitiousName(String fictitiousName) {
		this.fictitiousName = fictitiousName;
	}

	@Column(name = "CREDENTIALS_TYPE", length = 50)
	public String getCredentialsType() {
		return this.credentialsType;
	}

	public void setCredentialsType(String credentialsType) {
		this.credentialsType = credentialsType;
	}

	@Column(name = "CREDENTIALS_CODE", length = 50)
	public String getCredentialsCode() {
		return this.credentialsCode;
	}

	public void setCredentialsCode(String credentialsCode) {
		this.credentialsCode = credentialsCode;
	}

	@Column(name = "REGISTRATION_NO", length = 50)
	public String getRegistrationNo() {
		return this.registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	@Column(name = "ENTERPRISE_TYPE", length = 50)
	public String getEnterpriseType() {
		return this.enterpriseType;
	}

	public void setEnterpriseType(String enterpriseType) {
		this.enterpriseType = enterpriseType;
	}

	@Column(name = "REGISTER_TYPE", length = 50)
	public String getRegisterType() {
		return this.registerType;
	}

	public void setRegisterType(String registerType) {
		this.registerType = registerType;
	}

	@Column(name = "REGISTER_CAPITIAL", precision = 22, scale = 8)
	public BigDecimal getRegisterCapitial() {
		return this.registerCapitial;
	}

	public void setRegisterCapitial(BigDecimal registerCapitial) {
		this.registerCapitial = registerCapitial;
	}

	@Column(name = "REGISTER_TIME", length = 7)
	public Timestamp getRegisterTime() {
		return this.registerTime;
	}

	public void setRegisterTime(Timestamp registerTime) {
		this.registerTime = registerTime;
	}

	@Column(name = "REGISTER_PROV", length = 50)
	public String getRegisterProv() {
		return this.registerProv;
	}

	public void setRegisterProv(String registerProv) {
		this.registerProv = registerProv;
	}

	@Column(name = "REGISTER_CITY", length = 50)
	public String getRegisterCity() {
		return this.registerCity;
	}

	public void setRegisterCity(String registerCity) {
		this.registerCity = registerCity;
	}

	@Column(name = "REGISTER_AREA", length = 50)
	public String getRegisterArea() {
		return this.registerArea;
	}

	public void setRegisterArea(String registerArea) {
		this.registerArea = registerArea;
	}

	@Column(name = "REGISTER_ADDRESS", length = 500)
	public String getRegisterAddress() {
		return this.registerAddress;
	}

	public void setRegisterAddress(String registerAddress) {
		this.registerAddress = registerAddress;
	}

	@Column(name = "ASSET_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getAssetAmount() {
		return this.assetAmount;
	}

	public void setAssetAmount(BigDecimal assetAmount) {
		this.assetAmount = assetAmount;
	}

	@Column(name = "DEBT_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getDebtAmount() {
		return this.debtAmount;
	}

	public void setDebtAmount(BigDecimal debtAmount) {
		this.debtAmount = debtAmount;
	}

	@Column(name = "COMPANY_TELEPHONE", length = 50)
	public String getCompanyTelephone() {
		return this.companyTelephone;
	}

	public void setCompanyTelephone(String companyTelephone) {
		this.companyTelephone = companyTelephone;
	}

	@Column(name = "MOBILE", length = 50)
	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name = "DBO_PROV", length = 50)
	public String getDboProv() {
		return this.dboProv;
	}

	public void setDboProv(String dboProv) {
		this.dboProv = dboProv;
	}

	@Column(name = "DBO_CITY", length = 50)
	public String getDboCity() {
		return this.dboCity;
	}

	public void setDboCity(String dboCity) {
		this.dboCity = dboCity;
	}

	@Column(name = "DBO_AREA", length = 50)
	public String getDboArea() {
		return this.dboArea;
	}

	public void setDboArea(String dboArea) {
		this.dboArea = dboArea;
	}

	@Column(name = "DBO_ADDRESS", length = 500)
	public String getDboAddress() {
		return this.dboAddress;
	}

	public void setDboAddress(String dboAddress) {
		this.dboAddress = dboAddress;
	}

	@Column(name = "ENTERPRISE_INTRODUCE", length = 1000)
	public String getEnterpriseIntroduce() {
		return this.enterpriseIntroduce;
	}

	public void setEnterpriseIntroduce(String enterpriseIntroduce) {
		this.enterpriseIntroduce = enterpriseIntroduce;
	}

}