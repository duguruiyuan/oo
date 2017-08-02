package com.slfinance.shanlincaifu.entity;
// default package

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ComTAgent entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "COM_T_AGENT")
public class AgentEntity extends BaseEntity implements java.io.Serializable {

	// Fields

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2409673557188425719L;
	private String agentName;
	private String agentParentId;
	private String agentType;
	private String dataPermission;
	private String agentCode;
	private String tel;
	private String address;
	private String cityId;
	private String status;
	private String createUserId;
	
	private String updateUserId;
	private Timestamp updateDate;
	

	@Column(name = "AGENT_NAME", length = 150)
	public String getAgentName() {
		return this.agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	@Column(name = "AGENT_PARENT_ID", length = 150)
	public String getAgentParentId() {
		return this.agentParentId;
	}

	public void setAgentParentId(String agentParentId) {
		this.agentParentId = agentParentId;
	}

	@Column(name = "AGENT_TYPE", length = 150)
	public String getAgentType() {
		return this.agentType;
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

	@Column(name = "DATA_PERMISSION", length = 150)
	public String getDataPermission() {
		return this.dataPermission;
	}

	public void setDataPermission(String dataPermission) {
		this.dataPermission = dataPermission;
	}

	@Column(name = "AGENT_CODE", length = 150)
	public String getAgentCode() {
		return this.agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	@Column(name = "TEL", length = 150)
	public String getTel() {
		return this.tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	@Column(name = "ADDRESS", length = 150)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "CITY_ID", length = 50)
	public String getCityId() {
		return this.cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
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