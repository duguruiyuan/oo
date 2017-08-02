package com.slfinance.shanlincaifu.entity;
// default package

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

/**
 * BaoTAfficheInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_AFFICHE_INFO")
public class AfficheInfoEntity extends BaseEntity {

	// Fields

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7432175403451203932L;
	private String afficheTitle;
	private String afficheType;
	@Basic(fetch=FetchType.LAZY)
	@Lob
	@Column(columnDefinition="CLOB")
	@Type(type = "org.hibernate.type.StringClobType") 
	private String afficheContent;
	private String afficheStatus;
	private BigDecimal sortNum;
	private String publishUser;
	@Temporal(TemporalType.TIMESTAMP)
	private Date publishTime;
	
	
	

	@Column(name = "AFFICHE_TITLE", length = 150)
	public String getAfficheTitle() {
		return this.afficheTitle;
	}

	public void setAfficheTitle(String afficheTitle) {
		this.afficheTitle = afficheTitle;
	}

	@Column(name = "AFFICHE_TYPE", length = 150)
	public String getAfficheType() {
		return this.afficheType;
	}

	public void setAfficheType(String afficheType) {
		this.afficheType = afficheType;
	}

	@Column(name = "AFFICHE_CONTENT")
	public String getAfficheContent() {
		return this.afficheContent;
	}

	public void setAfficheContent(String afficheContent) {
		this.afficheContent = afficheContent;
	}

	@Column(name = "AFFICHE_STATUS", length = 50)
	public String getAfficheStatus() {
		return this.afficheStatus;
	}

	public void setAfficheStatus(String afficheStatus) {
		this.afficheStatus = afficheStatus;
	}

	@Column(name = "SORT_NUM", precision = 22, scale = 0)
	public BigDecimal getSortNum() {
		return this.sortNum;
	}

	public void setSortNum(BigDecimal sortNum) {
		this.sortNum = sortNum;
	}

	@Column(name = "PUBLISH_USER", length = 50)
	public String getPublishUser() {
		return this.publishUser;
	}

	public void setPublishUser(String publishUser) {
		this.publishUser = publishUser;
	}

	@Column(name = "PUBLISH_TIME", length = 7)
	public Date getPublishTime() {
		return this.publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}
	
	public boolean updateAffiche(AfficheInfoEntity afficheInfo){
		if(StringUtils.isNotEmpty(afficheInfo.getAfficheTitle()))
			this.afficheTitle = afficheInfo.getAfficheTitle();
		if(StringUtils.isNotEmpty(afficheInfo.getAfficheType()))
			this.afficheType = afficheInfo.getAfficheType();
		if(StringUtils.isNotEmpty(afficheInfo.getAfficheContent()))
			this.afficheContent = afficheInfo.getAfficheContent();
		if(StringUtils.isNotEmpty(afficheInfo.getAfficheStatus()))
			this.afficheStatus = afficheInfo.getAfficheStatus();
		if(StringUtils.isNotEmpty(afficheInfo.getPublishUser()))
			this.publishUser = afficheInfo.getPublishUser();
		if(null != afficheInfo.getPublishTime())
			this.publishTime = afficheInfo.getPublishTime();
		if(null != afficheInfo.getLastUpdateDate())
			this.lastUpdateDate = afficheInfo.getLastUpdateDate();
		if(null != afficheInfo.getLastUpdateUser())
			this.lastUpdateUser = afficheInfo.getLastUpdateUser();
		return true;
	}
	
	public AfficheInfoEntity cloneAfficheInfo(AfficheInfoEntity afficheInfo) {
		AfficheInfoEntity clone = new AfficheInfoEntity();
		if(StringUtils.isNotEmpty(afficheInfo.getId()))
			clone.setId(afficheInfo.getId());
		if(StringUtils.isNotEmpty(afficheInfo.getAfficheTitle()))
			clone.setAfficheTitle(afficheInfo.getAfficheTitle());
		if(StringUtils.isNotEmpty(afficheInfo.getAfficheType()))
			clone.setAfficheType(afficheInfo.getAfficheType());
		if(StringUtils.isNotEmpty(afficheInfo.getAfficheContent()))
			clone.setAfficheContent(afficheInfo.getAfficheContent());
		if(StringUtils.isNotEmpty(afficheInfo.getAfficheStatus()))
			clone.setAfficheStatus(afficheInfo.getAfficheStatus());
		if(StringUtils.isNotEmpty(afficheInfo.getPublishUser()))
			clone.setPublishUser(afficheInfo.getPublishUser());
		if(null != afficheInfo.getPublishTime())
			clone.setPublishTime(afficheInfo.getPublishTime());
		if(null != afficheInfo.getCreateDate())
			clone.setCreateDate(afficheInfo.getCreateDate());
		if(null != afficheInfo.getCreateUser())
			clone.setCreateUser(afficheInfo.getCreateUser());
		if(null != afficheInfo.getLastUpdateDate())
			clone.setLastUpdateDate(afficheInfo.getLastUpdateDate());
		if(null != afficheInfo.getLastUpdateUser())
			clone.setLastUpdateUser(afficheInfo.getLastUpdateUser());
		return clone;
	}
	
	
}