package com.slfinance.shanlincaifu.entity;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 
 */
@Entity
@Table(name = "BAO_T_LOAN_RESERVE")
public class LoanReserveEntity extends BaseEntity  {
	
	public LoanReserveEntity() {
		
	}
	
	public LoanReserveEntity(Integer loanTerm, String loanUnit, String transferType, String repaymentType) {
		this.loanTerm = loanTerm;
		this.loanUnit = loanUnit;
		this.transferType = transferType;
		this.repaymentType = repaymentType;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "CUST_ID", length = 50)
	private String custId;
	/**
	 * 新手标集合Id
	 */
	private String loanId;
	
	/**
	 * 预约金额
	 */
	@Column(name = "RESERVE_AMOUNT", precision = 22, scale = 8)
	private BigDecimal reserveAmount;

	/**
	 * 已投预约金额
	 */
	@Column(name = "ALREADY_INVEST_AMOUNT", precision = 22, scale = 8)
	private BigDecimal alreadyInvestAmount;

	/**
	 * 剩余预约金额
	 */
	@Column(name = "REMAINDER_AMOUNT", precision = 22, scale = 8)
	private BigDecimal remainderAmount;
	
	/**
	 * 排队时间
	 */
	@Column(name = "RESERVE_DATE", length = 7)
	private Date reserveDate;
	
	/**
	 * 预约开始时间
	 */
	@Column(name = "RESERVE_START_DATE", length = 7)
	private Date reserveStartDate;

	/**
	 * 预约结束时间
	 */
	@Column(name = "RESERVE_END_DATE", length = 7)
	private Date reserveEndDate;
	
	@Column(name = "RESERVE_STATUS", length = 50)
	private String reserveStatus;
	
	/**
	 * 一键出借标题
	 */
	private String reserveTitle;

	/**
	 * 一键出借期限
	 */
	private Integer loanTerm;

	/**
	 * 一键出借单位
	 */
	private String loanUnit;

	/**
	 * 一键出借转让类型
	 */
	private String transferType;

	/**
	 * 一键出借还款方式
	 */
	private String repaymentType;
	
	/**
	 * 一键出借来源
	 */
	private String appSource;
	
	/**
	 * 一键出借批次号
	 */
	private String requestNo;
	
	/**
	 * 一键出借IP地址
	 */
	private String ipAddress;



	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}
	
	@Column(name = "LOAN_ID", length = 50)
	public String getLoanId() {
		return this.loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	public BigDecimal getReserveAmount() {
		return reserveAmount;
	}

	public void setReserveAmount(BigDecimal reserveAmount) {
		this.reserveAmount = reserveAmount;
	}

	public BigDecimal getAlreadyInvestAmount() {
		return alreadyInvestAmount;
	}

	public void setAlreadyInvestAmount(BigDecimal alreadyInvestAmount) {
		this.alreadyInvestAmount = alreadyInvestAmount;
	}

	public BigDecimal getRemainderAmount() {
		return remainderAmount;
	}

	public void setRemainderAmount(BigDecimal remainderAmount) {
		this.remainderAmount = remainderAmount;
	}

	public Date getReserveDate() {
		return reserveDate;
	}

	public void setReserveDate(Date reserveDate) {
		this.reserveDate = reserveDate;
	}

	public Date getReserveStartDate() {
		return reserveStartDate;
	}

	public void setReserveStartDate(Date reserveStartDate) {
		this.reserveStartDate = reserveStartDate;
	}

	public Date getReserveEndDate() {
		return reserveEndDate;
	}

	public void setReserveEndDate(Date reserveEndDate) {
		this.reserveEndDate = reserveEndDate;
	}

	public String getReserveStatus() {
		return reserveStatus;
	}

	public void setReserveStatus(String reserveStatus) {
		this.reserveStatus = reserveStatus;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Column(name = "RESERVE_TITLE", length = 50)
	public String getReserveTitle() {
		return this.reserveTitle;
	}

	public void setReserveTitle(String reserveTitle) {
		this.reserveTitle = reserveTitle;
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

	@Column(name = "TRANSFER_TYPE", length = 50)
	public String getTransferType() {
		return this.transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	@Column(name = "REPAYMENT_TYPE", length = 250)
	public String getRepaymentType() {
		return this.repaymentType;
	}

	public void setRepaymentType(String repaymentType) {
		this.repaymentType = repaymentType;
	}
	
	@Column(name = "APP_SOURCE", length = 250)
	public String getAppSource() {
		return this.appSource;
	}

	public void setAppSource(String appSource) {
		this.appSource = appSource;
	}
	
	@Column(name = "REQUEST_NO", length = 250)
	public String getRequestNo() {
		return this.requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}
	
	@Column(name = "IP_ADDRESS", length = 250)
	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
}
