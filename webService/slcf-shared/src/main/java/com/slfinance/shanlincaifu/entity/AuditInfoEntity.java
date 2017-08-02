package com.slfinance.shanlincaifu.entity;
// default package

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;

import com.slfinance.shanlincaifu.utils.Constant;

/**
 * BaoTAuditInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_AUDIT_INFO")
public class AuditInfoEntity extends BaseEntity {

	// Fields

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1335262241069077157L;
	private String custId;
	private String relateType;
	private String relatePrimary;
	private String applyType;
	private BigDecimal tradeAmount;
	@Temporal(TemporalType.TIMESTAMP)
	private Date applyTime;
	@Temporal(TemporalType.TIMESTAMP)
	private Date auditTime;
	private String auditUser;
	private String auditStatus;
	private String tradeStatus;
	
	


	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

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

	@Column(name = "APPLY_TYPE", length = 150)
	public String getApplyType() {
		return this.applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	@Column(name = "TRADE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getTradeAmount() {
		return this.tradeAmount;
	}

	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	@Column(name = "APPLY_TIME", length = 7)
	public Date getApplyTime() {
		return this.applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	@Column(name = "AUDIT_TIME", length = 7)
	public Date getAuditTime() {
		return this.auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	@Column(name = "AUDIT_USER", length = 150)
	public String getAuditUser() {
		return this.auditUser;
	}

	public void setAuditUser(String auditUser) {
		this.auditUser = auditUser;
	}

	@Column(name = "AUDIT_STATUS", length = 150)
	public String getAuditStatus() {
		return this.auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	@Column(name = "TRADE_STATUS", length = 150)
	public String getTradeStatus() {
		return this.tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
	
	public AuditInfoEntity() {
		this.recordStatus = Constant.VALID_STATUS_VALID;
	}
	
	public AuditInfoEntity(String custId,String relateType,String relatePrimary,Date auditTime) {
		this.custId=custId;
		this.relateType=relateType;
		this.relatePrimary=relatePrimary;
		this.auditTime=auditTime;
	}
	
	public AuditInfoEntity(String custId,String relateType,String relatePrimary,String applyType,Date applyTime,String auditStatus,String tradeStatus) {
		this.custId=custId;
		this.relateType=relateType;
		this.relatePrimary=relatePrimary;
		this.applyType=applyType;
		this.applyTime=applyTime;
		this.auditStatus=auditStatus;
		this.tradeStatus=tradeStatus;
	}
	
	public AuditInfoEntity(String auditStatus,String tradeStatus,Date auditTime,String auditUser,String memo) {
		this.auditStatus=auditStatus;
		this.tradeStatus=tradeStatus;
		this.auditTime=auditTime;
		this.auditUser=auditUser;
		this.memo=memo;
	}
	
	public boolean updateAuditInfo( AuditInfoEntity auditInfo ){
		if(StringUtils.isNotEmpty(auditInfo.getAuditStatus()))
			this.auditStatus = auditInfo.getAuditStatus();
		if(StringUtils.isNotEmpty(auditInfo.getTradeStatus()))
			this.tradeStatus = auditInfo.getTradeStatus();
		if(StringUtils.isNotEmpty(auditInfo.getAuditUser()))
			this.auditUser = auditInfo.getAuditUser();
		if(StringUtils.isNotEmpty(auditInfo.getLastUpdateUser()))
			this.lastUpdateUser = auditInfo.getLastUpdateUser();
		if(null != auditInfo.getAuditTime())
			this.auditTime = auditInfo.getAuditTime();
		if(null != auditInfo.getLastUpdateDate())
			this.lastUpdateDate = auditInfo.getLastUpdateDate();
		if(StringUtils.isNotEmpty(auditInfo.getMemo()))
			this.memo = auditInfo.getMemo();
		return true;
	}

}