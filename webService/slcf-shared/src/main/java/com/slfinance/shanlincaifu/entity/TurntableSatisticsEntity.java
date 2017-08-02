package com.slfinance.shanlincaifu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 转盘活动统计表
 * @author lixx
 */
@Entity
@Table(name = "BAO_T_TRUNTABLE_STATISTICS")
public class TurntableSatisticsEntity extends BaseEntity {

	private static final long serialVersionUID = 6641020145066299761L;
	
	//转盘活动ID
	private String turntableId;
	
	//参与人数
	private int participateNumber;
	
	//中奖人数
	private int prizeNumber;
	
	//整体中奖概率
	private float prizeProbability;
	
	//分享次数
	private int shareNumber;
	
	//页面访问量
	private int pageViews;
	
	//访客数量
	private int visitorsNumber;
	
	@Column(name = "TURNTABLE_ID", length = 50)
	public String getTurntableId() {
		return turntableId;
	}

	public void setTurntableId(String turntableId) {
		this.turntableId = turntableId;
	}
	
	@Column(name = "PARTICIPATE_NUMBER", length = 50)
	public int getParticipateNumber() {
		return participateNumber;
	}

	public void setParticipateNumber(int participateNumber) {
		this.participateNumber = participateNumber;
	}
	
	@Column(name = "PRIZE_NUMBER", length = 50)
	public int getPrizeNumber() {
		return prizeNumber;
	}

	public void setPrizeNumber(int prizeNumber) {
		this.prizeNumber = prizeNumber;
	}
	
	@Column(name = "PRIZE_PROBABILITY", length = 50)
	public float getPrizeProbability() {
		return prizeProbability;
	}

	public void setPrizeProbability(float prizeProbability) {
		this.prizeProbability = prizeProbability;
	}
	
	@Column(name = "SHARE_NUMBER", length = 50)
	public int getShareNumber() {
		return shareNumber;
	}

	public void setShareNumber(int shareNumber) {
		this.shareNumber = shareNumber;
	}
	
	@Column(name = "PAGE_VIEWS", length = 50)
	public int getPageViews() {
		return pageViews;
	}

	public void setPageViews(int pageViews) {
		this.pageViews = pageViews;
	}
	
	@Column(name = "VISITORS_NUMBER", length = 50)
	public int getVisitorsNumber() {
		return visitorsNumber;
	}

	public void setVisitorsNumber(int visitorsNumber) {
		this.visitorsNumber = visitorsNumber;
	}
	
}
