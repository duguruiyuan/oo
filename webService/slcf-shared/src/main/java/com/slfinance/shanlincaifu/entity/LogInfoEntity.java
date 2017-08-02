package com.slfinance.shanlincaifu.entity;
// default package

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.slfinance.shanlincaifu.utils.Constant;

/**
 * BaoTLogInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_LOG_INFO")
public class LogInfoEntity extends BaseEntity {

	// Fields

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2445589954680130629L;
	private String relateType;
	private String relatePrimary;
	private String logType;
	private String operBeforeContent;
	private String operAfterContent;
	private String operDesc;
	private String operPerson;
	private String operIpaddress;
	
	
	public LogInfoEntity(){
	}
	public LogInfoEntity(String relateType,String relatePrimary,String logType,String operBeforeContent,String operAfterContent,String operDesc,String operPerson){
		this.relateType=relateType;
		this.relatePrimary=relatePrimary;
		this.logType=logType;
		this.operBeforeContent=operBeforeContent;
		this.operAfterContent=operAfterContent;
		this.operDesc=operDesc;
		this.operPerson=operPerson;
		this.recordStatus=Constant.VALID_STATUS_VALID;
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

	@Column(name = "LOG_TYPE", length = 200)
	public String getLogType() {
		return this.logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	@Column(name = "OPER_BEFORE_CONTENT", length = 3000)
	public String getOperBeforeContent() {
		return this.operBeforeContent;
	}

	public void setOperBeforeContent(String operBeforeContent) {
		this.operBeforeContent = operBeforeContent;
	}

	@Column(name = "OPER_AFTER_CONTENT", length = 3000)
	public String getOperAfterContent() {
		return this.operAfterContent;
	}

	public void setOperAfterContent(String operAfterContent) {
		this.operAfterContent = operAfterContent;
	}

	@Column(name = "OPER_DESC", length = 3000)
	public String getOperDesc() {
		return this.operDesc;
	}

	public void setOperDesc(String operDesc) {
		this.operDesc = operDesc;
	}

	@Column(name = "OPER_PERSON", length = 50)
	public String getOperPerson() {
		return this.operPerson;
	}

	public void setOperPerson(String operPerson) {
		this.operPerson = operPerson;
	}
	
	@Column(name = "OPER_IPADDRESS", length = 50)
	public String getOperIpaddress() {
		return operIpaddress;
	}
	
	public void setOperIpaddress(String operIpaddress) {
		this.operIpaddress = operIpaddress;
	}

	
}