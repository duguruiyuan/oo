package com.slfinance.shanlincaifu.entity;
// default package

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

/**
 * BaoTLoanCustInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_LOAN_CUST_INFO")
public class LoanCustInfoEntity implements java.io.Serializable {

	// Fields

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4699240287489369108L;
	private String custName;
	private String credentialsType;
	private String credentialsCode;
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(length = 50)
	protected String id;

	/** 创建时间 */
	@Temporal(TemporalType.TIMESTAMP)
	protected Date createDate = new Date();

	/** 备注 */
	@Column(length = 300)
	protected String memo;
	
	/** 版本 */
	@Version
	protected Integer version = 0;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="loanCustInfoEntity",fetch=FetchType.LAZY)
	private List<LoanInfoEntity> loanList=new ArrayList<LoanInfoEntity>();
	
	private String custGender;
	
	private String jobType;
	
	private String custEducation;
	
	private String marriageState;
	
	/**
	 * 年龄
	 */
	private Integer custAge;

	/**
	 * 银行名称
	 */
	private String bankName;

	/**
	 * 银行卡号
	 */
	private String cardNo;

	/**
	 * 开户省份
	 */
	private String openProvince;

	/**
	 * 开户城市
	 */
	private String openCity;

	/**
	 * 支行名称
	 */
	private String subBranchName;

	/**
	 * 手机号
	 */
	private String mobile;

	/**
	 * 客户编号
	 */
	private String custCode;

	/**
	 * 婚姻状况
	 */
	private String marriage;

	/**
	 * 居住地址
	 */
	private String homeAddress;

	/**
	 * 公司名称
	 */
	private String workCorporation;

	/**
	 * 单位地址
	 */
	private String workAddress;

	/**
	 * 单位联系固话
	 */
	private String workTelephone;

	/**
	 * 当前单位工作年限
	 */
	private String workYear;

	/**
	 * 工资发放形式
	 */
	private String salaryType;

	@Column(name = "CUST_NAME", length = 200)
	public String getCustName() {
		return this.custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
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

	public List<LoanInfoEntity> getLoanList() {
		return loanList;
	}

	public void setLoanList(List<LoanInfoEntity> loanList) {
		this.loanList = loanList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "CUST_GENDER", length = 50)
	public String getCustGender() {
		return custGender;
	}

	public void setCustGender(String custGender) {
		this.custGender = custGender;
	}

	@Column(name = "JOB_TYPE", length = 50)
	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	@Column(name = "CUST_EDUCATION", length = 50)
	public String getCustEducation() {
		return custEducation;
	}

	public void setCustEducation(String custEducation) {
		this.custEducation = custEducation;
	}

	@Column(name = "MARRIAGE_STATE", length = 50)
	public String getMarriageState() {
		return marriageState;
	}

	public void setMarriageState(String marriageState) {
		this.marriageState = marriageState;
	}

	@Column(name = "CUST_AGE", length = 22)
	public Integer getCustAge() {
		return this.custAge;
	}

	public void setCustAge(Integer custAge) {
		this.custAge = custAge;
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

	@Column(name = "MOBILE", length = 50)
	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name = "CUST_CODE", length = 50)
	public String getCustCode() {
		return this.custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	@Column(name = "MARRIAGE", length = 50)
	public String getMarriage() {
		return this.marriage;
	}

	public void setMarriage(String marriage) {
		this.marriage = marriage;
	}

	@Column(name = "HOME_ADDRESS", length = 500)
	public String getHomeAddress() {
		return this.homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

	@Column(name = "WORK_CORPORATION", length = 500)
	public String getWorkCorporation() {
		return this.workCorporation;
	}

	public void setWorkCorporation(String workCorporation) {
		this.workCorporation = workCorporation;
	}

	@Column(name = "WORK_ADDRESS", length = 500)
	public String getWorkAddress() {
		return this.workAddress;
	}

	public void setWorkAddress(String workAddress) {
		this.workAddress = workAddress;
	}

	@Column(name = "WORK_TELEPHONE", length = 50)
	public String getWorkTelephone() {
		return this.workTelephone;
	}

	public void setWorkTelephone(String workTelephone) {
		this.workTelephone = workTelephone;
	}

	@Column(name = "WORK_YEAR", length = 50)
	public String getWorkYear() {
		return this.workYear;
	}

	public void setWorkYear(String workYear) {
		this.workYear = workYear;
	}

	@Column(name = "SALARY_TYPE", length = 50)
	public String getSalaryType() {
		return this.salaryType;
	}

	public void setSalaryType(String salaryType) {
		this.salaryType = salaryType;
	}
}