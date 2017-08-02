package com.slfinance.shanlincaifu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


/**
 * BAO员工表
 * 
 * @author zhiwen_feng
 * 
 */
@Entity
@Getter
@Setter
@Table(name="BAO_T_EMPLOYEE_INFO")
public class EmployeeInfoEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 客户表ID
	 */
	@Column(name="CUST_ID", length = 50)
	private String custId;
	
	/**
	 * 工号
	 */
	@Column(name="EMP_NO", length = 50)
	private String empNo;
	
	/**
	 * 姓名
	 */
	@Column(name="EMP_NAME", length = 50)
	private String empName;
	
	/**
	 * 状态
	 */
	@Column(name="JOB_STATUS", length = 50)
	private String jobStatus;
	
	/**
	 * 证件类型
	 */
	@Column(name="CREDENTIALS_TYPE", length = 50)
	private String credentialsType;

	/**
	 * 证件号码
	 */
	@Column(name="CREDENTIALS_CODE", length = 50)
	private String credentialsCode;

	/**
	 * 所属部门
	 */
	@Column(name="DEPT_ID", length = 50)
	private String deptId;
	
	/**
	 * 职位
	 */
	@Column(name="JOB_POSITION", length = 50)
	private String jobPosition;
	
	/**
	 * 职级
	 */
	@Column(name="JOB_LEVEL", length = 50)
	private String jobLevel;

	/**
	 * 直属领导姓名
	 */
	@Column(name="CUST_MANAGER_NAME", length = 50)
	private String custManagerName;
	
	/**
	 * 直属领导证件号码
	 */
	@Column(name="CUST_MANAGER_CODE", length = 50)
	private String custManagerCode;

	public EmployeeInfoEntity() {
	}

}