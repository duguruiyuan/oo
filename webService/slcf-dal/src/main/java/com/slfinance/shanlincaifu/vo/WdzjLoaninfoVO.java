package com.slfinance.shanlincaifu.vo;

import java.util.ArrayList;
import java.util.List;

public class WdzjLoaninfoVO {
	private String projectId;
	private String title;
	private String amount;
	private String schedule;//进度
	private String interestRate;//利率
	private String deadline;//借款期限
	private String deadlineUnit;//期限单位* 仅限 ‘天’ 或 ‘月’
	private String reward; //奖励  传值0(平台无此字段)
	private String type;//信用标，债权转让标
	/**
	 * 还款方式
	 * 1：到期还本息(到期还本付息，一次性还本付息，按日计息到期还本,一次性付款、秒还) 2：每月等额本息(按月分期，按月等额本息)
	 * 3：每季分期（按季分期，按季等额本息） 5：每月付息到期还本(先息后本) 6：等额本金(按月等额本金) 7：每季付息到期还本（按季付息到期还本）
	 * 8：每月付息分期还本 9：先付息到期还本
	 */
	private String repaymentType; 
	private String userName;
	private String loanUrl;//标的详细页面地址链接
	private String successTime;//标被投满的时间
	private List<WdzjInvestinfoVo> subscribes=new ArrayList<WdzjInvestinfoVo>();
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getSchedule() {
		return schedule;
	}
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	public String getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(String interestRate) {
		this.interestRate = interestRate;
	}
	public String getDeadline() {
		return deadline;
	}
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}
	public String getDeadlineUnit() {
		return deadlineUnit;
	}
	public void setDeadlineUnit(String deadlineUnit) {
		this.deadlineUnit = deadlineUnit;
	}
	public String getReward() {
		return reward;
	}
	public void setReward(String reward) {
		this.reward = reward;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
    
	public String getRepaymentType() {
		return repaymentType;
	}
	public void setRepaymentType(String repaymentType) {
		this.repaymentType = repaymentType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLoanUrl() {
		return loanUrl;
	}
	public void setLoanUrl(String loanUrl) {
		this.loanUrl = loanUrl;
	}
	public String getSuccessTime() {
		return successTime;
	}
	public void setSuccessTime(String successTime) {
		this.successTime = successTime;
	}
	public List<WdzjInvestinfoVo> getSubscribes() {
		return subscribes;
	}
	public void setSubscribes(List<WdzjInvestinfoVo> subscribes) {
		this.subscribes = subscribes;
	}
	

	
}