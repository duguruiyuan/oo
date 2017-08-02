package com.slfinance.shanlincaifu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BAO_T_JOB_LISTENER_DETAIL")
public class JobListenerDetailEntity extends BaseEntity {

	private static final long serialVersionUID = -5537895865978834258L;

	private String jobId;
	private Date startTime;
	private Date endTime;
	private String serverIp;
	private String serverName;
	private String exception;

	@Column(name = "JOB_ID", length = 50)
	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	@Column(name = "START_TIME")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Column(name = "END_TIME")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Column(name = "SERVER_IP",length=50)
	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	@Column(name = "SERVER_NAME",length=50)
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Column(name = "EXCEPTION",length=500)
	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

}
