package com.slfinance.shanlincaifu.entity;
// default package

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * BaoTSmsInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_SMS_INFO")
public class SmsInfoEntity extends BaseEntity {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 4350102196070532940L;
	private String targetAddress;
	private String targetType;
	private String sendContent;
	private Date sendDate;
	private String sendStatus;
	private String verityCode;
	private Date lastValidTime;
	private String messageStatus;
	private String messageType;

	public SmsInfoEntity() {
		
	}
	
	public SmsInfoEntity(String targetAddress,String targetType,String sendContent,Date sendDate,String sendStatus,String verityCode,Date lastValidTime,String messageStatus,String messageType,String recordStatus) {
		this.targetAddress=targetAddress;
		this.targetType=targetType;
		this.sendContent=sendContent;
		this.sendDate=sendDate;
		this.sendStatus=sendStatus;
		this.verityCode=verityCode;
		this.lastValidTime=lastValidTime;
		this.messageStatus=messageStatus;
		this.messageType=messageType;
		this.recordStatus=recordStatus;
	}
	
	

	@Column(name = "TARGET_ADDRESS", length = 50)
	public String getTargetAddress() {
		return this.targetAddress;
	}

	public void setTargetAddress(String targetAddress) {
		this.targetAddress = targetAddress;
	}

	@Column(name = "TARGET_TYPE", length = 50)
	public String getTargetType() {
		return this.targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	@Column(name = "SEND_CONTENT", length = 300)
	public String getSendContent() {
		return this.sendContent;
	}

	public void setSendContent(String sendContent) {
		this.sendContent = sendContent;
	}

	@Column(name = "SEND_DATE", length = 7)
	public Date getSendDate() {
		return this.sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	@Column(name = "SEND_STATUS", length = 50)
	public String getSendStatus() {
		return this.sendStatus;
	}

	public void setSendStatus(String sendStatus) {
		this.sendStatus = sendStatus;
	}

	@Column(name = "VERITY_CODE", length = 50)
	public String getVerityCode() {
		return this.verityCode;
	}

	public void setVerityCode(String verityCode) {
		this.verityCode = verityCode;
	}

	@Column(name = "LAST_VALID_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastValidTime() {
		return this.lastValidTime;
	}

	public void setLastValidTime(Date lastValidTime) {
		this.lastValidTime = lastValidTime;
	}

	@Column(name = "MESSAGE_STATUS", length = 50)
	public String getMessageStatus() {
		return this.messageStatus;
	}

	public void setMessageStatus(String messageStatus) {
		this.messageStatus = messageStatus;
	}

	@Column(name = "MESSAGE_TYPE", length = 50)
	public String getMessageType() {
		return this.messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}


}