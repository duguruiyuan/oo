package com.slfinance.shanlincaifu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BAO_T_BANNER_INFO")
public class BannerInfoEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String bannerTitle;	
	private String bannerUrl;	
	private Integer bannerSort;
	private String appSource;
	private String bannerStatus;
	private String bannerType;
	private String tradeType;
	private String bannerContent;
	private String isRecommend;
	private String isShare;
	
	@Column(name = "BANNER_TITLE", length = 500)
	public String getBannerTitle() {
		return bannerTitle;
	}
	public void setBannerTitle(String bannerTitle) {
		this.bannerTitle = bannerTitle;
	}
	
	@Column(name = "BANNER_URL", length = 150)
	public String getBannerUrl() {
		return bannerUrl;
	}
	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}
	
	@Column(name = "BANNER_SORT")
	public Integer getBannerSort() {
		return bannerSort;
	}
	public void setBannerSort(Integer bannerSort) {
		this.bannerSort = bannerSort;
	}
	
	@Column(name = "APP_SOURCE", length = 50)
	public String getAppSource() {
		return appSource;
	}
	public void setAppSource(String appSource) {
		this.appSource = appSource;
	}
	
	@Column(name = "BANNER_STATUS", length = 50)
	public String getBannerStatus() {
		return bannerStatus;
	}
	public void setBannerStatus(String bannerStatus) {
		this.bannerStatus = bannerStatus;
	}
	
	@Column(name = "BANNER_TYPE", length = 50)
	public String getBannerType() {
		return bannerType;
	}
	public void setBannerType(String bannerType) {
		this.bannerType = bannerType;
	}
	
	@Column(name = "TRADE_TYPE", length = 50)
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	
	@Column(name = "BANNER_CONTENT", length = 4000)
	public String getBannerContent() {
		return bannerContent;
	}
	public void setBannerContent(String bannerContent) {
		this.bannerContent = bannerContent;
	}
	
	@Column(name = "IS_RECOMMEND", length = 50)
	public String getIsRecommend() {
		return isRecommend;
	}
	public void setIsRecommend(String isRecommend) {
		this.isRecommend = isRecommend;
	}
	@Column(name = "IS_SHARE", length = 50)
	public String getIsShare() {
		return isShare;
	}
	public void setIsShare(String isShare) {
		this.isShare = isShare;
	}

}
