package com.slfinance.shanlincaifu.vo;

import java.io.Serializable;

public class RegisterVo implements Serializable {

	private static final long serialVersionUID = -2685554208950432894L;
	private String loginName; // 用户名
	private String mobile; // 注册手机
	private String verityCode; // 手机验证码
	private String smsType; // 短信类型
	private String loginPassword; // 登录密码
	private String inviteCode;
	private String requestUrl; //链接地址

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getVerityCode() {
		return verityCode;
	}

	public void setVerityCode(String verityCode) {
		this.verityCode = verityCode;
	}

	public String getSmsType() {
		return smsType;
	}

	public void setSmsType(String smsType) {
		this.smsType = smsType;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

}
