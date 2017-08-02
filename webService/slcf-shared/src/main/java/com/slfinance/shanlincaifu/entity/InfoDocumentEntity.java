package com.slfinance.shanlincaifu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BAO_T_INFO_DOCUMENT")
public class InfoDocumentEntity extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3951850975210529939L;

	/**
	 * 标题
	 */
	private String title;
	/**
	 * 发布状态
	 */
	private String issueStatus;
	/**
	 * 报告时间
	 */
	private String reportTime;
	/**
	 * 内容
	 */
	private String content;
	
	
	public String getReportTime() {
		return reportTime;
	}
	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Column(name = "TITLE", length = 200)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Column(name = "ISSUE_STATUS", length = 50)
	public String getIssueStatus() {
		return issueStatus;
	}
	public void setIssueStatus(String issueStatus) {
		this.issueStatus = issueStatus;
	}


	
	
}
