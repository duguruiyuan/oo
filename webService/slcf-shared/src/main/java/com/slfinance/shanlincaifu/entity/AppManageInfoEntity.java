package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 *  entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_APP_MANAGE_INFO")
public class AppManageInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * APP类型
	 */
	private String appSource;

	/**
	 * APP当前版本
	 */
	private String appVersion;

	/**
	 * APP支持最低版本
	 */
	private String appSupportedVersion;

	/**
	 * 更新地址
	 */
	private String updateUrl;



	@Column(name = "APP_SOURCE", length = 50)
	public String getAppSource() {
		return this.appSource;
	}

	public void setAppSource(String appSource) {
		this.appSource = appSource;
	}

	@Column(name = "APP_VERSION", length = 50)
	public String getAppVersion() {
		return this.appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	@Column(name = "APP_SUPPORTED_VERSION", length = 50)
	public String getAppSupportedVersion() {
		return this.appSupportedVersion;
	}

	public void setAppSupportedVersion(String appSupportedVersion) {
		this.appSupportedVersion = appSupportedVersion;
	}

	@Column(name = "UPDATE_URL", length = 100)
	public String getUpdateUrl() {
		return this.updateUrl;
	}

	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}

}
