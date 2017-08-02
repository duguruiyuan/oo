package com.slfinance.shanlincaifu.entity;
// default package

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BaoTSmsLogInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_SMS_LOG_INFO")
public class SmsLogInfoEntity extends BaseEntity  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2659589608081235261L;
	private String mobile;
	private String sendContent;
	private Date sendTime;
	private String extno;
	private String returnStatus;
	private String returnMessage;
	private String remainPoint;
	private String taskId;
	private Long successCounts;

	// Constructors

	/** default constructor */
	public SmsLogInfoEntity() {
	}

	/** minimal constructor */
	public SmsLogInfoEntity(String id, Date createDate) {
		this.id = id;
		this.createDate = createDate;
	}

	/** full constructor */
	public SmsLogInfoEntity(String id, String mobile, String sendContent, Date sendTime, String extno, String returnStatus, String returnMessage, String remainPoint, String taskId, Long successCounts, String recordStatus, String createUser, Date createDate, String lastUpdateUser, Date lastUpdateDate, String memo) {
		this.id = id;
		this.mobile = mobile;
		this.sendContent = sendContent;
		this.sendTime = sendTime;
		this.extno = extno;
		this.returnStatus = returnStatus;
		this.returnMessage = returnMessage;
		this.remainPoint = remainPoint;
		this.taskId = taskId;
		this.successCounts = successCounts;
		this.recordStatus = recordStatus;
		this.createUser = createUser;
		this.createDate = createDate;
		this.lastUpdateUser = lastUpdateUser;
		this.lastUpdateDate = lastUpdateDate;
		this.memo = memo;
	}

	@Column(name = "MOBILE", length = 1000)
	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name = "SEND_CONTENT", length = 500)
	public String getSendContent() {
		return this.sendContent;
	}

	public void setSendContent(String sendContent) {
		this.sendContent = sendContent;
	}

	@Column(name = "SEND_TIME", length = 7)
	public Date getSendTime() {
		return this.sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	@Column(name = "EXTNO", length = 10)
	public String getExtno() {
		return this.extno;
	}

	public void setExtno(String extno) {
		this.extno = extno;
	}

	@Column(name = "RETURN_STATUS", length = 10)
	public String getReturnStatus() {
		return this.returnStatus;
	}

	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}

	@Column(name = "RETURN_MESSAGE", length = 500)
	public String getReturnMessage() {
		return this.returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

	@Column(name = "REMAIN_POINT", length = 20)
	public String getRemainPoint() {
		return this.remainPoint;
	}

	public void setRemainPoint(String remainPoint) {
		this.remainPoint = remainPoint;
	}

	@Column(name = "TASK_ID", length = 10)
	public String getTaskId() {
		return this.taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@Column(name = "SUCCESS_COUNTS", precision = 22, scale = 0)
	public Long getSuccessCounts() {
		return this.successCounts;
	}

	public void setSuccessCounts(Long successCounts) {
		this.successCounts = successCounts;
	}

}