package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 资产信息表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_ASSET_INFO")
public class AssetInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 借款ID
	 */
	private String loanId;

	/**
	 * 客户姓名
	 */
	private String custName;

	/**
	 * 证件类型
	 */
	private String credentialsType;

	/**
	 * 证件号码
	 */
	private String credentialsCode;

	/**
	 * 贷款期数
	 */
	private Integer loanTerm;

	/**
	 * 
	 */
	private String loanUnit;

	/**
	 * 贷款金额
	 */
	private BigDecimal loanAmount;

	/**
	 * 等额本息、每期还息到期付本、到期一次性还本付息
	 */
	private String repaymentMethod;

	/**
	 * 借款描述
	 */
	private String loanDesc;

	/**
	 * 起息开始日期
	 */
	private Date investStartDate;

	/**
	 * 起息结束日期
	 */
	private Date investEndDate;



	@Column(name = "LOAN_ID", length = 50)
	public String getLoanId() {
		return this.loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

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

	@Column(name = "LOAN_TERM", length = 22)
	public Integer getLoanTerm() {
		return this.loanTerm;
	}

	public void setLoanTerm(Integer loanTerm) {
		this.loanTerm = loanTerm;
	}

	@Column(name = "LOAN_UNIT", length = 50)
	public String getLoanUnit() {
		return this.loanUnit;
	}

	public void setLoanUnit(String loanUnit) {
		this.loanUnit = loanUnit;
	}

	@Column(name = "LOAN_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getLoanAmount() {
		return this.loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}

	@Column(name = "REPAYMENT_METHOD", length = 300)
	public String getRepaymentMethod() {
		return this.repaymentMethod;
	}

	public void setRepaymentMethod(String repaymentMethod) {
		this.repaymentMethod = repaymentMethod;
	}

	@Column(name = "LOAN_DESC", length = 2000)
	public String getLoanDesc() {
		return this.loanDesc;
	}

	public void setLoanDesc(String loanDesc) {
		this.loanDesc = loanDesc;
	}

	@Column(name = "INVEST_START_DATE", length = 7)
	public Date getInvestStartDate() {
		return this.investStartDate;
	}

	public void setInvestStartDate(Date investStartDate) {
		this.investStartDate = investStartDate;
	}

	@Column(name = "INVEST_END_DATE", length = 7)
	public Date getInvestEndDate() {
		return this.investEndDate;
	}

	public void setInvestEndDate(Date investEndDate) {
		this.investEndDate = investEndDate;
	}

}
