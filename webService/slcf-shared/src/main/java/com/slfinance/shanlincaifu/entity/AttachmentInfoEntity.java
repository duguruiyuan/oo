package com.slfinance.shanlincaifu.entity;
// default package

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BaoTAttachmentInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_ATTACHMENT_INFO")
public class AttachmentInfoEntity extends BaseEntity {

	// Fields

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5269239502394142742L;
	private String relateType;
	private String relatePrimary;
	private String attachmentType;
	private String attachmentName;
	private String storagePath;
	private String docType;
	private String showType;
	
	

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

	@Column(name = "ATTACHMENT_TYPE", length = 50)
	public String getAttachmentType() {
		return this.attachmentType;
	}

	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}

	@Column(name = "ATTACHMENT_NAME", length = 150)
	public String getAttachmentName() {
		return this.attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	@Column(name = "STORAGE_PATH", length = 200)
	public String getStoragePath() {
		return this.storagePath;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	@Column(name = "DOC_TYPE", length = 50)
	public String getDocType() {
		return this.docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	@Column(name = "SHOW_TYPE", length = 50)
	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	
}