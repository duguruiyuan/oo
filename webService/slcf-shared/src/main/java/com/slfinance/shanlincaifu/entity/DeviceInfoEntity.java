/** 
 * @(#)DeviceInfoEntity.java 1.0.0 2015年10月19日 下午3:19:17  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**   
 * BAO设备信息表
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年10月19日 下午3:19:17 $ 
 */
@Entity
@Table(name = "BAO_T_DEVICE_INFO")
public class DeviceInfoEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String relateTableIdentification;
	private String relatePrimary;
	private String custId;
	private String tradeType;
	private String meId;
	private String meVersion;
	private String appSource;
	private String requestNo;
	private String requestTime;
	private String channelNo;
	private String requestUrl;
	/**
	 * 广告来源
	 */
	private String utmSource;

	/**
	 * 广告媒介
	 */
	private String utmMedium;

	/**
	 * 广告名称
	 */
	private String utmCampaign;

	/**
	 * 广告内容
	 */
	private String utmContent;

	/**
	 * 广告字词
	 */
	private String utmTerm;
	
	@Column(name = "RELATE_TABLE_IDENTIFICATION", length = 50)
	public String getRelateTableIdentification() {
		return relateTableIdentification;
	}
	public void setRelateTableIdentification(String relateTableIdentification) {
		this.relateTableIdentification = relateTableIdentification;
	}
	
	@Column(name = "RELATE_PRIMARY", length = 50)
	public String getRelatePrimary() {
		return relatePrimary;
	}
	public void setRelatePrimary(String relatePrimary) {
		this.relatePrimary = relatePrimary;
	}
	
	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	
	@Column(name = "TRADE_TYPE", length = 50)
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	
	@Column(name = "ME_ID", length = 50)
	public String getMeId() {
		return meId;
	}
	public void setMeId(String meId) {
		this.meId = meId;
	}
	
	@Column(name = "ME_VERSION", length = 50)
	public String getMeVersion() {
		return meVersion;
	}
	public void setMeVersion(String meVersion) {
		this.meVersion = meVersion;
	}
	
	@Column(name = "APP_SOURCE", length = 50)
	public String getAppSource() {
		return appSource;
	}
	public void setAppSource(String appSource) {
		this.appSource = appSource;
	}
	
	@Column(name = "REQUEST_NO", length = 50)
	public String getRequestNo() {
		return requestNo;
	}
	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}
	
	@Column(name = "REQUEST_TIME", length = 50)
	public String getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}
	
	@Column(name = "CHANNEL_NO", length = 50)
	public String getChannelNo() {
		return channelNo;
	}
	public void setChannelNo(String channelNo) {
		this.channelNo = channelNo;
	}
	
	@Column(name = "REQUEST_URL", length = 500)
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	@Column(name = "UTM_SOURCE", length = 300)
	public String getUtmSource() {
		return this.utmSource;
	}

	public void setUtmSource(String utmSource) {
		this.utmSource = utmSource;
	}

	@Column(name = "UTM_MEDIUM", length = 300)
	public String getUtmMedium() {
		return this.utmMedium;
	}

	public void setUtmMedium(String utmMedium) {
		this.utmMedium = utmMedium;
	}

	@Column(name = "UTM_CAMPAIGN", length = 300)
	public String getUtmCampaign() {
		return this.utmCampaign;
	}

	public void setUtmCampaign(String utmCampaign) {
		this.utmCampaign = utmCampaign;
	}

	@Column(name = "UTM_CONTENT", length = 300)
	public String getUtmContent() {
		return this.utmContent;
	}

	public void setUtmContent(String utmContent) {
		this.utmContent = utmContent;
	}

	@Column(name = "UTM_TERM", length = 300)
	public String getUtmTerm() {
		return this.utmTerm;
	}

	public void setUtmTerm(String utmTerm) {
		this.utmTerm = utmTerm;
	}

}
