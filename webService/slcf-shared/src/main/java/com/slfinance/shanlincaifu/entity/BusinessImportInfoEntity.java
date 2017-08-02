package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;


/**
 * 营业部组织导入表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_BUSINESS_IMPORT_INFO")
public class BusinessImportInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 导入日期
	 */
	private Date importDate;

	/**
	 * 导入月份
	 */
	private String importMonth;

	/**
	 * 导入状态
	 */
	private String importStatus;



	@Column(name = "IMPORT_DATE", length = 7)
	public Date getImportDate() {
		return this.importDate;
	}

	public void setImportDate(Date importDate) {
		this.importDate = importDate;
	}

	@Column(name = "IMPORT_MONTH", length = 6)
	public String getImportMonth() {
		return this.importMonth;
	}

	public void setImportMonth(String importMonth) {
		this.importMonth = importMonth;
	}

	@Column(name = "IMPORT_STATUS", length = 50)
	public String getImportStatus() {
		return this.importStatus;
	}

	public void setImportStatus(String importStatus) {
		this.importStatus = importStatus;
	}

}
