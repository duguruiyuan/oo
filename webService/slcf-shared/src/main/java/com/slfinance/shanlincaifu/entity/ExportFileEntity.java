package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 系统生成文件记录表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_EXPORT_FILE")
public class ExportFileEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 客户ID
	 */
	private String custId;

	/**
	 * 关联类型(默认表名大写)
	 */
	private String relateType;

	/**
	 * 关联主键
	 */
	private String relatePrimary;

	/**
	 * 优选计划协议
优选计划债权协议
企业借款协议
	 */
	private String fileType;

	/**
	 * 文件名称
	 */
	private String fileName;

	/**
	 * 
	 */
	private String path;
	
	/**
	 * 文件个数
	 */
	private Integer fileCounts;



	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "RELATE_TYPE", length = 200)
	public String getRelateType() {
		return this.relateType;
	}

	public void setRelateType(String relateType) {
		this.relateType = relateType;
	}

	@Column(name = "RELATE_PRIMARY", length = 50)
	public String getRelatePrimary() {
		return this.relatePrimary;
	}

	public void setRelatePrimary(String relatePrimary) {
		this.relatePrimary = relatePrimary;
	}

	@Column(name = "FILE_TYPE", length = 50)
	public String getFileType() {
		return this.fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	@Column(name = "FILE_NAME", length = 500)
	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Column(name = "PATH", length = 500)
	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Column(name = "FILE_COUNTS", length = 22)
	public Integer getFileCounts() {
		return fileCounts;
	}

	public void setFileCounts(int fileCounts) {
		this.fileCounts = fileCounts;
	}

	
}
