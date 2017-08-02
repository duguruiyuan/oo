/** 
 * @(#)JobRunDetail.java 1.0.0 2015年1月19日 上午9:17:47  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * job运行日志记录
 * 
 * @author 孟山
 * @version $Revision:1.0.0, $Date: 2015年1月19日 上午9:17:47 $
 */
@Entity
@Table(name = "BAO_T_JOB_LISTENER_DETAIL")
public class JobRunDetailEntity extends BaseEntity {
	private static final long serialVersionUID = -4535238837224978654L;
	/** job监听ID */
	private String jobId;
	/** job开始时间 */
	private Date startTime;
	/** job终止时间 */
	private Date endTime;
	/** job 中间异常信息 */
	private String exception;
	/** 服务器IP */
	private String serverIp;
	/** 服务器名称 */
	private String serverName;

	public JobRunDetailEntity(String jobId, String serverIp, String serverName) {
		this.jobId = jobId;
		this.serverIp = serverIp;
		this.serverName = serverName;
	}

	public JobRunDetailEntity() {
		super();
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

}
