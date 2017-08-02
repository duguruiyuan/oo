package com.slfinance.shanlincaifu.entity;

// default package

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BaoTCustRecommendInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_CUST_RECOMMEND_INFO")
public class CustRecommendInfoEntity extends BaseEntity {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String custId;
	private String quiltCustId;
	private String applyId;
	private String source;
	private Timestamp startDate;
	private Timestamp expireDate;

	// Constructors

	/** default constructor */
	public CustRecommendInfoEntity() {
	}

	/** minimal constructor */
	public CustRecommendInfoEntity(String id, Timestamp createDate) {
		this.id = id;
		this.createDate = createDate;
	}

	/** full constructor */
	public CustRecommendInfoEntity(String id, String custId, String quiltCustId, String applyId, String source, Timestamp startDate, Timestamp expireDate, String recordStatus, String createUser, Timestamp createDate, String lastUpdateUser, Timestamp lastUpdateDate, String memo) {
		this.id = id;
		this.custId = custId;
		this.quiltCustId = quiltCustId;
		this.applyId = applyId;
		this.source = source;
		this.startDate = startDate;
		this.expireDate = expireDate;
		this.recordStatus = recordStatus;
		this.createUser = createUser;
		this.createDate = createDate;
		this.lastUpdateUser = lastUpdateUser;
		this.lastUpdateDate = lastUpdateDate;
		this.memo = memo;
	}

	// Property accessors
	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "QUILT_CUST_ID", length = 50)
	public String getQuiltCustId() {
		return this.quiltCustId;
	}

	public void setQuiltCustId(String quiltCustId) {
		this.quiltCustId = quiltCustId;
	}

	@Column(name = "APPLY_ID", length = 300)
	public String getApplyId() {
		return this.applyId;
	}

	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}

	@Column(name = "SOURCE", length = 50)
	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Column(name = "START_DATE", length = 7)
	public Timestamp getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	@Column(name = "EXPIRE_DATE", length = 7)
	public Timestamp getExpireDate() {
		return this.expireDate;
	}

	public void setExpireDate(Timestamp expireDate) {
		this.expireDate = expireDate;
	}
}