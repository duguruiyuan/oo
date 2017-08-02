package com.slfinance.shanlincaifu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 转盘信息表
 * @author lixx
 */
@Entity
@Table(name = "BAO_T_TRUNTABLE_INFO")
public class TurntableInfoEntity extends BaseEntity {

	private static final long serialVersionUID = -1388397888709631407L;
	
	//转盘活动ID
	private String turntableId;
	
	//序号
	private String awardIndex;
	
	//奖项：一等奖，二等奖等.....
	private String awardGrand;
	
	//奖品
	private String award;
	
	//奖品图片
	private String awardImage;
	
	//每日奖品数量
	private String awardAmountEveryday;
	
	//奖品总数
	private String awardTotal;
	
	//中奖概率
	private String awardProbability;
	
	//出奖开始时间
	private String awardStartTime;
	
	//出奖结束时间
	private String awardEndTime;
	
	@Column(name = "TURNTABLE_ID", length = 50)
	public String getTurntableId() {
		return turntableId;
	}
	public void setTurntableId(String turntableId) {
		this.turntableId = turntableId;
	}
	@Column(name = "AWARD_INDEX", length = 50)
	public String getAwardIndex() {
		return awardIndex;
	}
	public void setAwardIndex(String awardIndex) {
		this.awardIndex = awardIndex;
	}
	@Column(name = "AWARD_GRAND", length = 50)
	public String getAwardGrand() {
		return awardGrand;
	}
	public void setAwardGrand(String awardGrand) {
		this.awardGrand = awardGrand;
	}
	@Column(name = "AWARD", length = 50)
	public String getAward() {
		return award;
	}
	public void setAward(String award) {
		this.award = award;
	}
	@Column(name = "AWARD_IMAGE", length = 50)
	public String getAwardImage() {
		return awardImage;
	}
	public void setAwardImage(String awardImage) {
		this.awardImage = awardImage;
	}
	@Column(name = "AWARD_AMOUNT_EVERYDAY", length = 50)
	public String getAwardAmountEveryday() {
		return awardAmountEveryday;
	}
	public void setAwardAmountEveryday(String awardAmountEveryday) {
		this.awardAmountEveryday = awardAmountEveryday;
	}
	@Column(name = "AWARD_TOTAL", length = 50)
	public String getAwardTotal() {
		return awardTotal;
	}
	public void setAwardTotal(String awardTotal) {
		this.awardTotal = awardTotal;
	}
	@Column(name = "AWARD_PROBABILITY", length = 50)
	public String getAwardProbability() {
		return awardProbability;
	}
	public void setAwardProbability(String awardProbability) {
		this.awardProbability = awardProbability;
	}
	@Column(name = "AWARD_START_TIME")
	public String getAwardStartTime() {
		return awardStartTime;
	}
	public void setAwardStartTime(String awardStartTime) {
		this.awardStartTime = awardStartTime;
	}
	@Column(name = "AWARD_END_TIME")
	public String getAwardEndTime() {
		return awardEndTime;
	}
	public void setAwardEndTime(String awardEndTime) {
		this.awardEndTime = awardEndTime;
	}
	
}
