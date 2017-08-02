package com.slfinance.shanlincaifu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BAO_T_ACTIVITY_INFO")
public class ActivityInfoEntity extends BaseEntity {

	private static final long serialVersionUID = 7266448637031735004L;
	private String activityCode;
	private String activityName;
	private String activityStatus;
	private Date startDate;
	private Date expireDate;
	private String activityDesc;
	private String activityContent;
	
	
	@Column(name = "ACTIVITY_CODE", length = 50)
	public String getActivityCode() {
		return activityCode;
	}
	
	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}
	
	@Column(name = "ACTIVITY_NAME", length = 150)
	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	@Column(name = "ACTIVITY_STATUS",precision = 50)
	public String getActivityStatus() {
		return activityStatus;
	}
	
	public void setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
	}
	
	@Column(name = "START_DATE")
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Column(name = "EXPIRE_DATE")
	public Date getExpireDate() {
		return expireDate;
	}
	
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}
	
	@Column(name = "ACTIVITY_DESC", length = 300)
	public String getActivityDesc() {
		return activityDesc;
	}
	
	public void setActivityDesc(String activityDesc) {
		this.activityDesc = activityDesc;
	}

	@Column(name = "ACTIVITY_CONTENT", length = 4000)
	public String getActivityContent() {
		return this.activityContent;
	}

	public void setActivityContent(String activityContent) {
		this.activityContent = activityContent;
	}

}
