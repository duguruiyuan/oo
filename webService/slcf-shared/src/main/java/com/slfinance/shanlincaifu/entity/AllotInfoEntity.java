package com.slfinance.shanlincaifu.entity;
// default package

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BaoTAllotInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_ALLOT_INFO")
public class AllotInfoEntity extends BaseEntity  {

	// Fields

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6483642519678684651L;
	private String relateType;
	private String relatePrimary;
	private String allotCode;
	private Timestamp allotDate;
	private BigDecimal allotAmount;
	private String allotStatus;
	private Date useDate;
	
	
	
	
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

	@Column(name = "ALLOT_CODE", length = 50)
	public String getAllotCode() {
		return this.allotCode;
	}

	public void setAllotCode(String allotCode) {
		this.allotCode = allotCode;
	}

	@Column(name = "ALLOT_DATE", length = 7)
	public Timestamp getAllotDate() {
		return this.allotDate;
	}

	public void setAllotDate(Timestamp allotDate) {
		this.allotDate = allotDate;
	}

	@Column(name = "ALLOT_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getAllotAmount() {
		return this.allotAmount;
	}

	public void setAllotAmount(BigDecimal allotAmount) {
		this.allotAmount = allotAmount;
	}

	@Column(name = "ALLOT_STATUS", length = 50)
	public String getAllotStatus() {
		return this.allotStatus;
	}

	public void setAllotStatus(String allotStatus) {
		this.allotStatus = allotStatus;
	}

	@Column(name = "USE_DATE", length = 7)
	public Date getUseDate() {
		return useDate;
	}

	public void setUseDate(Date useDate) {
		this.useDate = useDate;
	}
}