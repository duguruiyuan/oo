package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 营业部组织结构表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_BUSINESS_DEPT_INFO")
public class BusinessDeptInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 导入表ID
	 */
	private String bussinessImportId;

	/**
	 * 
	 */
	private String custId;

	/**
	 * 证件号码
	 */
	private String credentialsCode;

	/**
	 * 工号
	 */
	private String empNo;

	/**
	 * 姓名
	 */
	private String empName;

	/**
	 * 部门
	 */
	private String deptName;

	/**
	 * 省
	 */
	private String provinceName;

	/**
	 * 市
	 */
	private String cityName;



	@Column(name = "BUSSINESS_IMPORT_ID", length = 50)
	public String getBussinessImportId() {
		return this.bussinessImportId;
	}

	public void setBussinessImportId(String bussinessImportId) {
		this.bussinessImportId = bussinessImportId;
	}

	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "CREDENTIALS_CODE", length = 50)
	public String getCredentialsCode() {
		return this.credentialsCode;
	}

	public void setCredentialsCode(String credentialsCode) {
		this.credentialsCode = credentialsCode;
	}

	@Column(name = "EMP_NO", length = 50)
	public String getEmpNo() {
		return this.empNo;
	}

	public void setEmpNo(String empNo) {
		this.empNo = empNo;
	}

	@Column(name = "EMP_NAME", length = 50)
	public String getEmpName() {
		return this.empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	@Column(name = "DEPT_NAME", length = 500)
	public String getDeptName() {
		return this.deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	@Column(name = "PROVINCE_NAME", length = 250)
	public String getProvinceName() {
		return this.provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	@Column(name = "CITY_NAME", length = 250)
	public String getCityName() {
		return this.cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

}
