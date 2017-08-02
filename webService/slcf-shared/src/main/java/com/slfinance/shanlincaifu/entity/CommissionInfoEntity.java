package com.slfinance.shanlincaifu.entity;

// default package

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BaoTCommissionInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_COMMISSION_INFO")
public class CommissionInfoEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields
	private String custId;
	private Timestamp commDate;
	private Double investAmount;
	private Double commissionAmount;
	private Double rewardAmount;
	private String tradeStatus;
	private String productTypeId;
	private String commMonth;
	private Double yearInvestAmount;
	/**
	 * 部门
	 */
	private String deptName;

	/**
	 * 省
	 */
	private String provinceName;

	/**
	 * 市
	 */
	private String cityName;

	// Constructors

	/** default constructor */
	public CommissionInfoEntity() {
	}

	/** minimal constructor */
	public CommissionInfoEntity(String id, Timestamp createDate) {
		this.id = id;
		this.createDate = createDate;
	}

	/** full constructor */
	public CommissionInfoEntity(String id, String custId, Timestamp commDate, Double investAmount, Double commissionAmount, Double rewardAmount, String tradeStatus, String recordStatus, String createUser, Timestamp createDate, String lastUpdateUser, Timestamp lastUpdateDate, String memo, String productTypeId, String commonMonth, Double yearInvestAmount) {
		this.id = id;
		this.custId = custId;
		this.commDate = commDate;
		this.investAmount = investAmount;
		this.commissionAmount = commissionAmount;
		this.rewardAmount = rewardAmount;
		this.tradeStatus = tradeStatus;
		this.recordStatus = recordStatus;
		this.createUser = createUser;
		this.createDate = createDate;
		this.lastUpdateUser = lastUpdateUser;
		this.lastUpdateDate = lastUpdateDate;
		this.memo = memo;
		this.productTypeId = productTypeId;
		this.commMonth = commonMonth;
		this.yearInvestAmount = yearInvestAmount;
	}

	// Property accessors
	@Column(name = "CUST_ID", length = 200)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "COMM_DATE", length = 7)
	public Timestamp getCommDate() {
		return this.commDate;
	}

	public void setCommDate(Timestamp commDate) {
		this.commDate = commDate;
	}

	@Column(name = "INVEST_AMOUNT", precision = 22, scale = 8)
	public Double getInvestAmount() {
		return this.investAmount;
	}

	public void setInvestAmount(Double investAmount) {
		this.investAmount = investAmount;
	}

	@Column(name = "COMMISSION_AMOUNT", precision = 22, scale = 8)
	public Double getCommissionAmount() {
		return this.commissionAmount;
	}

	public void setCommissionAmount(Double commissionAmount) {
		this.commissionAmount = commissionAmount;
	}

	@Column(name = "REWARD_AMOUNT", precision = 22, scale = 8)
	public Double getRewardAmount() {
		return this.rewardAmount;
	}

	public void setRewardAmount(Double rewardAmount) {
		this.rewardAmount = rewardAmount;
	}

	@Column(name = "TRADE_STATUS", length = 50)
	public String getTradeStatus() {
		return this.tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	@Column(name = "PRODUCT_TYPE_ID", length = 50)
	public String getProductTypeId() {
		return productTypeId;
	}

	public void setProductTypeId(String productTypeId) {
		this.productTypeId = productTypeId;
	}

	@Column(name = "COMM_MONTH", length = 6)
	public String getCommMonth() {
		return commMonth;
	}

	public void setCommMonth(String commMonth) {
		this.commMonth = commMonth;
	}

	@Column(name = "YEAR_INVEST_AMOUNT", precision = 22, scale = 8)
	public Double getYearInvestAmount() {
		return yearInvestAmount;
	}

	public void setYearInvestAmount(Double yearInvestAmount) {
		this.yearInvestAmount = yearInvestAmount;
	}
	
	@Column(name = "DEPT_NAME", length = 500)
	public String getDeptName() {
		return this.deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	@Column(name = "PROVINCE_NAME", length = 250)
	public String getProvinceName() {
		return this.provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	@Column(name = "CITY_NAME", length = 250)
	public String getCityName() {
		return this.cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
}