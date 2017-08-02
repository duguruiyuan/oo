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

import lombok.Data;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * BaoTSystemMessageInfo entity. @author MyEclipse Persistence Tools
 */
@Data
@Entity
@Table(name = "BAO_T_SYSTEM_MESSAGE_READ_INFO")
public class SystemMessageReadInfoEntity extends BaseEntity {

	// Fields

	private static final long serialVersionUID = -7905379325491651456L;
	
	//请勿修改成ALL
	@ManyToOne(targetEntity=SystemMessageInfoEntity.class,cascade={CascadeType.DETACH},fetch=FetchType.EAGER,optional=true)
	@JoinColumn(name="MESSAGE_ID",unique = false,nullable = true,insertable = true,updatable=true)
	@NotFound(action=NotFoundAction.IGNORE)//用户找不到时，仍返回消息信息
	private SystemMessageInfoEntity message;
	
	//请勿修改成ALL
	@ManyToOne(targetEntity=CustInfoEntity.class,cascade={CascadeType.DETACH},fetch=FetchType.EAGER,optional=true)
    @JoinColumn(name="RECEIVE_CUST_ID",unique = false,nullable = true,insertable = true,updatable=true )
	@NotFound(action=NotFoundAction.IGNORE)//用户找不到时，仍返回消息信息
	private CustInfoEntity receiveCust;
	
	private String isRead;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date readDate;
	
	private String relateType;
	
	private String relatePrimary;
	
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
	
	public CustInfoEntity getReceiveCust() {
		return receiveCust;
	}

	public void setReceiveCust(CustInfoEntity receiveCust) {
		this.receiveCust = receiveCust;
	}

	public Date getReadDate() {
		return readDate;
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
}