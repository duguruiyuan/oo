package com.slfinance.shanlincaifu.entity;
// default package

import javax.persistence.Column;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

/**
 * BaoTDailyInterestInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_DAILY_INTEREST_INFO")
public class DailyInterestInfoEntity implements Serializable {

	private static final long serialVersionUID = 988645616002769743L;
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(length = 50)
	protected String id;
	
	private String subAccountId;
	private String currDate;
	private BigDecimal expectInterest;
	private BigDecimal factInterest;
	private BigDecimal factGainInterest;
	
	/** 版本 */
	@Version
	protected Integer version = 0;
	
	/** 创建时间 */
	@Temporal(TemporalType.TIMESTAMP)
	protected Date createDate;

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Column(name = "SUB_ACCOUNT_ID", length = 50)
	public String getSubAccountId() {
		return this.subAccountId;
	}

	public void setSubAccountId(String subAccountId) {
		this.subAccountId = subAccountId;
	}

	@Column(name = "CURR_DATE", length = 8)
	public String getCurrDate() {
		return this.currDate;
	}

	public void setCurrDate(String currDate) {
		this.currDate = currDate;
	}

	@Column(name = "EXPECT_INTEREST", precision = 22, scale = 8)
	public BigDecimal getExpectInterest() {
		return this.expectInterest;
	}

	public void setExpectInterest(BigDecimal expectInterest) {
		this.expectInterest = expectInterest;
	}

	@Column(name = "FACT_INTEREST", precision = 22, scale = 8)
	public BigDecimal getFactInterest() {
		return this.factInterest;
	}

	public void setFactInterest(BigDecimal factInterest) {
		this.factInterest = factInterest;
	}

	@Column(name = "FACT_GAIN_INTEREST", precision = 22, scale = 8)
	public BigDecimal getFactGainInterest() {
		return factGainInterest;
	}

	public void setFactGainInterest(BigDecimal factGainInterest) {
		this.factGainInterest = factGainInterest;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}