package com.slfinance.shanlincaifu.entity;
// default package

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * BaoTSystemMessageInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_SYSTEM_MESSAGE_INFO")
public class SystemMessageInfoEntity extends BaseEntity  {

	// Fields

	private static final long serialVersionUID = -7905379325491651456L;
	
	//请勿修改成ALL
	@ManyToOne(targetEntity=CustInfoEntity.class,cascade={CascadeType.DETACH},fetch=FetchType.EAGER,optional=true)
	@JoinColumn(name="SEND_CUST_ID",unique = false,nullable = true,insertable = true,updatable=true)
	@NotFound(action=NotFoundAction.IGNORE)//用户找不到时，仍返回消息信息
	private CustInfoEntity sendCust;
	
	//请勿修改成ALL
	@ManyToOne(targetEntity=CustInfoEntity.class,cascade={CascadeType.DETACH},fetch=FetchType.EAGER,optional=true)
    @JoinColumn(name="RECEIVE_CUST_ID",unique = false,nullable = true,insertable = true,updatable=true )
	@NotFound(action=NotFoundAction.IGNORE)//用户找不到时，仍返回消息信息
	private CustInfoEntity receiveCust;
	
	private String sendTitle;
	
	private String sendContent;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date sendDate;
	
	private String isRead;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date readDate;

	@Column(name = "SEND_TITLE", length = 200)
	public String getSendTitle() {
		return this.sendTitle;
	}

	public void setSendTitle(String sendTitle) {
		this.sendTitle = sendTitle;
	}

	@Column(name = "SEND_CONTENT", length = 3000)
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

	@Column(name = "IS_READ", length = 50)
	public String getIsRead() {
		return this.isRead;
	}

	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

	@Column(name = "READ_DATE", length = 7)
	public Date getReadDate(Date readDate) {
		return this.readDate = readDate;
	}

	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}

	public CustInfoEntity getSendCust() {
		return sendCust;
	}

	public void setSendCust(CustInfoEntity sendCust) {
		this.sendCust = sendCust;
	}
	
	public CustInfoEntity getReceiveCust() {
		return receiveCust;
	}

	public void setReceiveCust(CustInfoEntity receiveCust) {
		this.receiveCust = receiveCust;
	}

	public Date getReadDate() {
		return readDate;
	}

	public SystemMessageInfoEntity() {
	} 
	
	public SystemMessageInfoEntity(String sendTitle,String sendContent) {
		this.sendTitle=sendTitle;
		this.sendContent=sendContent;
	} 	
	
	public SystemMessageInfoEntity(CustInfoEntity sendCust,CustInfoEntity receiveCust,String sendTitle,String sendContent,Date sendDate,String isRead,Date readDate,String recordStatus) {
		this.sendCust=sendCust;
		this.receiveCust=receiveCust;
		this.sendTitle=sendTitle;
		this.sendContent=sendContent;
		this.sendDate=sendDate;
		this.isRead=isRead;
		this.readDate=readDate;
		this.recordStatus=recordStatus;
	}
	
	public boolean updateMessage( SystemMessageInfoEntity message ){
		if(StringUtils.isNotEmpty(message.getRecordStatus()))
			this.recordStatus = message.getRecordStatus();
		if(StringUtils.isNotEmpty(message.getIsRead()))
			this.isRead = message.getIsRead();
		if(StringUtils.isNotEmpty(message.getLastUpdateUser()))
			this.lastUpdateUser = message.getLastUpdateUser();
		if(null != message.getLastUpdateDate())
			this.lastUpdateDate = message.getLastUpdateDate();
		return true;
	}
	
	@Transient
	public String getMessageSendDate() {
		return DateFormatUtils.format(this.createDate, "yyyy-MM-dd");
	}
	
}