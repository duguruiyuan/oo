package com.slfinance.shanlincaifu.entity;
// default package

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BaoTUnbindInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_UNBIND_INFO")
public class UnbindInfoEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields
	private String relateType;
	private String relatePrimary;
	private String unbindCode;
	private String unbindType;
	private String unbindStatus;
	private String unbindDesc;
	private String custId;
	
	// Constructors

	/** default constructor */
	public UnbindInfoEntity() {
	}

	/** minimal constructor */
	public UnbindInfoEntity(String id, Date createDate) {
		this.id = id;
		this.createDate = createDate;
	}

	/** full constructor */
	public UnbindInfoEntity(String id, String relateType, String relatePrimary, String unbindCode, String unbindType, String unbindStatus, String unbindDesc, String recordStatus, String createUser, Date createDate, String lastUpdateUser, Date lastUpdateDate, Integer version, String memo) {
		this.id = id;
		this.relateType = relateType;
		this.relatePrimary = relatePrimary;
		this.unbindCode = unbindCode;
		this.unbindType = unbindType;
		this.unbindStatus = unbindStatus;
		this.unbindDesc = unbindDesc;
		this.recordStatus = recordStatus;
		this.createUser = createUser;
		this.createDate = createDate;
		this.lastUpdateUser = lastUpdateUser;
		this.lastUpdateDate = lastUpdateDate;
		this.version = version;
		this.memo = memo;
	}

	// Property accessors
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

	@Column(name = "UNBIND_CODE", length = 50)
	public String getUnbindCode() {
		return this.unbindCode;
	}

	public void setUnbindCode(String unbindCode) {
		this.unbindCode = unbindCode;
	}

	@Column(name = "UNBIND_TYPE", length = 300)
	public String getUnbindType() {
		return this.unbindType;
	}

	public void setUnbindType(String unbindType) {
		this.unbindType = unbindType;
	}

	@Column(name = "UNBIND_STATUS", length = 300)
	public String getUnbindStatus() {
		return this.unbindStatus;
	}

	public void setUnbindStatus(String unbindStatus) {
		this.unbindStatus = unbindStatus;
	}

	@Column(name = "UNBIND_DESC", length = 2000)
	public String getUnbindDesc() {
		return this.unbindDesc;
	}

	public void setUnbindDesc(String unbindDesc) {
		this.unbindDesc = unbindDesc;
	}

	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}
	
	
}