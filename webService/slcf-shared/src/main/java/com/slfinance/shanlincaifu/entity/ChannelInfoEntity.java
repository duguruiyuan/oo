package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 渠道表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_CHANNEL_INFO")
public class ChannelInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 部门名称
	 */
	private String deptName;

	/**
	 * 部门负责人
	 */
	private String deptManager;

	/**
	 * 渠道名称
	 */
	private String channelName;

	/**
	 * 渠道编号
	 */
	private String channelNo;

	/**
	 * 终端
	 */
	private String channelSource;



	@Column(name = "DEPT_NAME", length = 50)
	public String getDeptName() {
		return this.deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	@Column(name = "DEPT_MANAGER", length = 50)
	public String getDeptManager() {
		return this.deptManager;
	}

	public void setDeptManager(String deptManager) {
		this.deptManager = deptManager;
	}

	@Column(name = "CHANNEL_NAME", length = 50)
	public String getChannelName() {
		return this.channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	@Column(name = "CHANNEL_NO", length = 50)
	public String getChannelNo() {
		return this.channelNo;
	}

	public void setChannelNo(String channelNo) {
		this.channelNo = channelNo;
	}

	@Column(name = "CHANNEL_SOURCE", length = 50)
	public String getChannelSource() {
		return this.channelSource;
	}

	public void setChannelSource(String channelSource) {
		this.channelSource = channelSource;
	}

}
