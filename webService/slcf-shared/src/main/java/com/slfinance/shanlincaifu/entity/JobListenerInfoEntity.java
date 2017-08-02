package com.slfinance.shanlincaifu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BAO_T_JOB_LISTENER_INFO")
public class JobListenerInfoEntity extends BaseEntity{

	private static final long serialVersionUID = -2973096755718951763L;
	
	private String jobClassName;
	private String jobName;
	private String executeStatus;
	
	@Column(name = "JOB_CLASS_NAME", length = 200)
	public String getJobClassName() {
		return jobClassName;
	}
	
	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}
	
	@Column(name = "JOB_NAME", length = 100)
	public String getJobName() {
		return jobName;
	}
	
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	@Column(name = "EXECUTE_STATUS", length = 50)
	public String getExecuteStatus() {
		return executeStatus;
	}
	
	public void setExecuteStatus(String executeStatus) {
		this.executeStatus = executeStatus;
	}

}
