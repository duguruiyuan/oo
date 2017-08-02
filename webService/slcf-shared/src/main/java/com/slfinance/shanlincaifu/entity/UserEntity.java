package com.slfinance.shanlincaifu.entity;

// default package

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

/**
 * ComTUser entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "COM_T_USER")
public class UserEntity {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(length = 50)
	protected String id;

 
	/**
	 * 
	 */
	private static final long serialVersionUID = 815190905056998990L;
	private String agentId;
	private String userLoginAccount;
	private String userNumber;
	private String userPasswd;
	private String userName;
	private String email;
	private String userType;
	private String status;
	private String createUserId;

	private String updateUserId;
	private Timestamp updateDate;

	private String credentialsType;
	private String credentialsCode;
	private String mobile;

	@Column(name = "CREDENTIALS_TYPE", length = 50)
	public String getCredentialsType() {
		return credentialsType;
	}

	public void setCredentialsType(String credentialsType) {
		this.credentialsType = credentialsType;
	}

	@Column(name = "CREDENTIALS_CODE", length = 50)
	public String getCredentialsCode() {
		return credentialsCode;
	}

	public void setCredentialsCode(String credentialsCode) {
		this.credentialsCode = credentialsCode;
	}

	@Column(name = "MOBILE", length = 50)
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name = "AGENT_ID", length = 50)
	public String getAgentId() {
		return this.agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	@Column(name = "USER_LOGIN_ACCOUNT", length = 150)
	public String getUserLoginAccount() {
		return this.userLoginAccount;
	}

	public void setUserLoginAccount(String userLoginAccount) {
		this.userLoginAccount = userLoginAccount;
	}

	@Column(name = "USER_NUMBER", length = 150)
	public String getUserNumber() {
		return this.userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	@Column(name = "USER_PASSWD", length = 150)
	public String getUserPasswd() {
		return this.userPasswd;
	}

	public void setUserPasswd(String userPasswd) {
		this.userPasswd = userPasswd;
	}

	@Column(name = "USER_NAME", length = 150)
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "EMAIL", length = 150)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "USER_TYPE", length = 150)
	public String getUserType() {
		return this.userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	@Column(name = "STATUS", length = 150)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "CREATE_USER_ID", length = 50)
	public String getCreateUserId() {
		return this.createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	@Column(name = "UPDATE_USER_ID", length = 50)
	public String getUpdateUserId() {
		return this.updateUserId;
	}

	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	@Column(name = "UPDATE_DATE", length = 7)
	public Timestamp getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}

}