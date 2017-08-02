package com.slfinance.shanlincaifu.vo;

public class WdzjInvestinfoVo {
	private String subscribeUserName;
	private	String amount;
	private	String validAmount;
	private String addDate;
	private	String status;
	private	String type;
	private String projectId;
	public String getSubscribeUserName() {
		return subscribeUserName;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getValidAmount() {
		return validAmount;
	}
	public void setValidAmount(String validAmount) {
		this.validAmount = validAmount;
	}
	public String getAddDate() {
		return addDate;
	}
	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setSubscribeUserName(String subscribeUserName) {
		this.subscribeUserName = subscribeUserName;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
}
