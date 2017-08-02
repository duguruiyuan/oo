package com.slfinance.shanlincaifu.entity;
// default package

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * BaoTLoginLogInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_LOGIN_LOG_INFO")
public class LoginLogInfoEntity  {

	// Fields

	
	/**
	 * 
	 */
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(length = 50)
	protected String id;
	private String custId;
	private Date loginDate;
	private Date logoutDate;
	private String loginIp;
	private String meId;
	private String meVersion;
	private String appSource;
	private String appVersion;

	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "LOGIN_DATE", length = 7)
	public Date getLoginDate() {
		return this.loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	@Column(name = "LOGOUT_DATE", length = 7)
	public Date getLogoutDate() {
		return this.logoutDate;
	}

	public void setLogoutDate(Date logoutDate) {
		this.logoutDate = logoutDate;
	}

	@Column(name = "LOGIN_IP", length = 50)
	public String getLoginIp() {
		return this.loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	@Column(name = "ME_ID", length = 50)
	public String getMeId() {
		return meId;
	}
	public void setMeId(String meId) {
		this.meId = meId;
	}
	
	@Column(name = "ME_VERSION", length = 50)
	public String getMeVersion() {
		return meVersion;
	}
	public void setMeVersion(String meVersion) {
		this.meVersion = meVersion;
	}
	
	@Column(name = "APP_SOURCE", length = 50)
	public String getAppSource() {
		return appSource;
	}
	public void setAppSource(String appSource) {
		this.appSource = appSource;
	}
	
	@Column(name = "APP_VERSION", length = 50)
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
}