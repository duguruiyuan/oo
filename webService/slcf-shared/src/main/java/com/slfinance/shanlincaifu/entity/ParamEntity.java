package com.slfinance.shanlincaifu.entity;
// default package

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * ComTParam entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "COM_T_PARAM")
public class ParamEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -843368689869124463L;
	// Fields

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(length = 50)
	private String id;
	private String type;
	private String typeName;
	private String parameterName;
	private String value;
	private String parentId;
	private BigDecimal sort;
	private String isEdit;
	private String status;
	private String createUserId;
	
	private String updateUserId;
	private Timestamp updateDate;
	
	private Date createDate;
	private String memo;
	

	@Column(name = "TYPE", length = 150)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "TYPE_NAME", length = 150)
	public String getTypeName() {
		return this.typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Column(name = "PARAMETER_NAME", length = 300)
	public String getParameterName() {
		return this.parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	@Column(name = "VALUE", length = 150)
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Column(name = "PARENT_ID", length = 50)
	public String getParentId() {
		return this.parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@Column(name = "SORT", precision = 22, scale = 0)
	public BigDecimal getSort() {
		return this.sort;
	}

	public void setSort(BigDecimal sort) {
		this.sort = sort;
	}

	@Column(name = "IS_EDIT", length = 50)
	public String getIsEdit() {
		return this.isEdit;
	}

	public void setIsEdit(String isEdit) {
		this.isEdit = isEdit;
	}

	@Column(name = "STATUS", length = 50)
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

	@Column(name = "CREATE_DATE", length = 7)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Column(name = "MEMO", length = 500)
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	
}