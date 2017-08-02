package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 *  entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_APP_ACCESS_INFO")
public class AppAccessInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private String meId;

	/**
	 * 
	 */
	private String meVersion;

	/**
	 * 
	 */
	private String appSource;

	/**
	 * 
	 */
	private String channelNo;



	@Column(name = "ME_ID", length = 50)
	public String getMeId() {
		return this.meId;
	}

	public void setMeId(String meId) {
		this.meId = meId;
	}

	@Column(name = "ME_VERSION", length = 50)
	public String getMeVersion() {
		return this.meVersion;
	}

	public void setMeVersion(String meVersion) {
		this.meVersion = meVersion;
	}

	@Column(name = "APP_SOURCE", length = 50)
	public String getAppSource() {
		return this.appSource;
	}

	public void setAppSource(String appSource) {
		this.appSource = appSource;
	}

	@Column(name = "CHANNEL_NO", length = 50)
	public String getChannelNo() {
		return this.channelNo;
	}

	public void setChannelNo(String channelNo) {
		this.channelNo = channelNo;
	}

}
