/** 
 * @(#)LoanAllotHistory.java 1.0.0 2015年5月23日 下午2:52:37  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

/**   
 * BAO债权历史情况表
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月23日 下午2:52:37 $ 
 */
@Entity
@Table(name = "BAO_T_LOAN_ALLOT_HISTORY")
public class LoanAllotHistoryEntity implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String loanId;
	private String productName;
	private String recordDate;
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(length = 50)
	private String id;

	/** 创建时间 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate = new Date();

	
	/** 版本 */
	@Version
	private Integer version = 0;

	@Column(name = "LOAN_ID", length = 50)
	public String getLoanId() {
		return loanId;
	}


	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	@Column(name = "PRODUCT_NAME", length = 50)
	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Column(name = "RECORD_DATE", length = 8)
	public String getRecordDate() {
		return recordDate;
	}


	public void setRecordDate(String recordDate) {
		this.recordDate = recordDate;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
}
