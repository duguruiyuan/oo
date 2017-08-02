package com.slfinance.shanlincaifu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


/**
 * BAO部门表
 * 
 * @author zhiwen_feng
 * 
 */
@Entity
@Getter
@Setter
@Table(name="BAO_T_DEPT_INFO")
public class DeptInfoEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 部门名称
	 */
	@Column(name="DEPT_NAME", length = 500)
	private String deptName;

	/**
	 * 部门负责人ID
	 */
	@Column(name="DEPT_MANAGER_ID", length = 50)
	private String deptManagerId;

	/**
	 * 上级部门ID
	 */
	@Column(name="PARENT_ID", length = 50)
	private String parentId;
	
	/**
	 * 部门编号
	 */
	@Column(name="DEPT_NO", length = 4000)
	private String deptNo;
	
	/**
	 * 部门类型
	 */
	@Column(name="DEPT_TYPE", length = 50)
	private String deptType;
	
	/**
	 * 所在省
	 */
	@Column(name="PRO_NAME", length = 50)
	private String proName;
	
	/**
	 * 所在市
	 */
	@Column(name="CITY_NAME", length = 50)
	private String cityName;

	public DeptInfoEntity() {
	}
}