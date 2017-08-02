package com.slfinance.shanlincaifu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * 大转盘活动表
 * @author lixx
 */
@Entity
@Table(name = "BAO_T_TRUNTABLE_ACTIVITY")
public class TurntableActivityEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1731073530204754851L;
	
	//活动名称
	private String activityName;
	
	//开始时间
	private Date startTime;
	
	//结束时间
	private Date endTime;
	
	//活动链接
	private String activityLink;
	
	//状态：启用：1；禁用：0
	private String activityStatus;
	
	//背景图片
	private String backgroundImage;
	
	//指针图片
	private String pointImage;
	
	//转盘图片
	private String turntableImage;
	
	//活动规则
	private String activityRule;
	
	//分享标题
	private String shareTitle;
	
	//分享副标题
	private String shareContent;
	
	//分享图片
	private String shareImage;
	
	//活动参与规则：0：不限；1：一个微信号一天一次；2：一个终端设备一天一次；3：一个IP每天一次；
	private String participateRule;
	
	//活动参与人员：0：不限；1：关注（微信号）才可抽奖
	private String participatePensonnel;
	
	//转盘状态：0：未设置；1：已设置
	private String turntableStatus;
	
	//背景图片名
	private String backgroundImgName;
	
	//指针图片名
	private String pointImgName;
	
	//转盘图片名
	private String turntableImgName;
	
	//分享图片名
	private String shareImgName;
	
	@Column(name = "ACTIVITY_NAME", length = 50)
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	@Column(name = "START_TIME")
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	@Column(name = "END_TIME")
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	@Column(name = "ACTIVITY_LINK", length = 50)
	public String getActivityLink() {
		return activityLink;
	}
	public void setActivityLink(String activityLink) {
		this.activityLink = activityLink;
	}
	@Column(name = "ACTIVITY_STATUS", length = 50)
	public String getActivityStatus() {
		return activityStatus;
	}
	public void setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
	}
	@Column(name = "BACKGROUND_IMAGE", length = 50)
	public String getBackgroundImage() {
		return backgroundImage;
	}
	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}
	@Column(name = "POINT_IMAGE", length = 50)
	public String getPointImage() {
		return pointImage;
	}
	public void setPointImage(String pointImage) {
		this.pointImage = pointImage;
	}
	@Column(name = "TURNTABLE_IMAGE", length = 50)
	public String getTurntableImage() {
		return turntableImage;
	}
	public void setTurntableImage(String turntableImage) {
		this.turntableImage = turntableImage;
	}
	@Column(name = "ACTIVITY_RULE", length = 500)
	public String getActivityRule() {
		return activityRule;
	}
	public void setActivityRule(String activityRule) {
		this.activityRule = activityRule;
	}
	@Column(name = "SHARE_TITLE", length = 50)
	public String getShareTitle() {
		return shareTitle;
	}
	public void setShareTitle(String shareTitle) {
		this.shareTitle = shareTitle;
	}
	@Column(name = "SHARE_CONTENT", length = 50)
	public String getShareContent() {
		return shareContent;
	}
	public void setShareContent(String shareContent) {
		this.shareContent = shareContent;
	}
	@Column(name = "SHARE_IMAGE", length = 50)
	public String getShareImage() {
		return shareImage;
	}
	public void setShareImage(String shareImage) {
		this.shareImage = shareImage;
	}
	@Column(name = "PARTICIPATE_RULE", length = 50)
	public String getParticipateRule() {
		return participateRule;
	}
	public void setParticipateRule(String participateRule) {
		this.participateRule = participateRule;
	}
	@Column(name = "PARTICIPATE_PENSONNEL", length = 50)
	public String getParticipatePensonnel() {
		return participatePensonnel;
	}
	public void setParticipatePensonnel(String participatePensonnel) {
		this.participatePensonnel = participatePensonnel;
	}
	@Column(name = "TURNTABLE_STATUS", length = 50)
	public String getTurntableStatus() {
		return turntableStatus;
	}
	public void setTurntableStatus(String turntableStatus) {
		this.turntableStatus = turntableStatus;
	}
	@Column(name = "BACKGROUND_IMG_NAME", length = 50)
	public String getBackgroundImgName() {
		return backgroundImgName;
	}
	public void setBackgroundImgName(String backgroundImgName) {
		this.backgroundImgName = backgroundImgName;
	}
	@Column(name = "POINT_IMG_NAME", length = 50)
	public String getPointImgName() {
		return pointImgName;
	}
	public void setPointImgName(String pointImgName) {
		this.pointImgName = pointImgName;
	}
	@Column(name = "TURNTABLE_IMG_NAME", length = 50)
	public String getTurntableImgName() {
		return turntableImgName;
	}
	public void setTurntableImgName(String turntableImgName) {
		this.turntableImgName = turntableImgName;
	}
	@Column(name = "SHARE_IMG_NAME", length = 50)
	public String getShareImgName() {
		return shareImgName;
	}
	public void setShareImgName(String shareImgName) {
		this.shareImgName = shareImgName;
	}
	
}
