package com.slfinance.shanlincaifu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


/**
 * BAO花名册原始数据表
 * 
 * @author zhiwen_feng
 * 
 */
@Entity
@Getter
@Setter
@Table(name="BAO_T_EMPLOYEE_LOAD_INFO")
public class EmployeeLoadInfoEntity extends BaseEntity{
	private static final long serialVersionUID = 1L;
	
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
	 * 省份
	 */
	@Column(name="PROVINCE", length = 50)
	private String province;
	
	/**
	 * 城市
	 */
	@Column(name="CITY", length = 50)
	private String city;
	
	/**
	 * 部门
	 */
	@Column(name="TEAM1", length = 50)
	private String team1;
	
	/**
	 * 所在团队
	 */
	@Column(name="TEAM4", length = 500)
	private String team4;
	
	/**
	 * 所在门店
	 */
	@Column(name="TEAM2", length = 500)
	private String team2;
	
	/**
	 * 所在大团队
	 */
	@Column(name="TEAM3", length = 500)
	private String team3;
	
	/**
	 * 职位
	 */
	@Column(name="JOB_POSITION", length = 500)
	private String jobPosition;
	
	/**
	 * 职级
	 */
	@Column(name="JOB_LEVEL", length = 500)
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
	
	/**
	 * 处理状态
	 */
	@Column(name="STATUS", length = 50)
	private String status;
	
	/**
	 * 部门ID
	 */
	@Column(name="TEAM_ID", length = 50)
	private String teamId;
	
	/**
	 * 部门级别
	 */
	@Column(name="DEPT_TYPE", length = 50)
	private String deptType;
	
	/**
	 * 导入批次
	 */
	@Column(name="IMPORT_BATCH_NO", length = 50)
	private String importBatchNo;

	public EmployeeLoadInfoEntity() {
	}

}