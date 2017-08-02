/** 
 * @(#)JobRunListener.java 1.0.0 2015年1月19日 上午9:14:17  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.time.DateUtils;

/**
 * job监听器model
 * 
 * @author 孟山
 * @version $Revision:1.0.0, $Date: 2015年1月19日 上午9:14:17 $
 */
@Entity
@Table(name = "BAO_T_JOB_LISTENER_INFO")
public class JobRunListenerEntity extends BaseEntity {
	private static final long serialVersionUID = 1985067219546390551L;
	/** job 类名的权限名 */
	private String jobClassName;
	/** job名称 */
	private String jobName;
	/** 当前JOB状态 运行中|未运行 */
	private String executeStatus;

	public JobRunListenerEntity(String jobClassName, String jobName) {
		super();
		this.jobClassName = jobClassName;
		this.jobName = jobName;
	}

	public JobRunListenerEntity(String id) {
		super();
		setId(id);
	}

	public JobRunListenerEntity() {
		super();
	}

	public String getJobClassName() {
		return jobClassName;
	}

	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}

	public String getExecuteStatus() {
		return executeStatus;
	}

	public void setExecuteStatus(String executeStatus) {
		this.executeStatus = executeStatus;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public void setContinueHour(Date continueHour) {
		DateUtils.addHours(new Date(), -10);
	}

}
