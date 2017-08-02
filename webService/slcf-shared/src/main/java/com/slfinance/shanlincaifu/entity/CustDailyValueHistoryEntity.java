/** 
 * @(#)CustDailyValueHistoryEntity.java 1.0.0 2015年5月23日 下午2:56:56  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.entity;

import java.math.BigDecimal;
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
 * BAO用户每日持有份额表
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月23日 下午2:56:56 $ 
 */
@Entity
@Table(name = "BAO_T_CUST_DAILY_VALUE_HISTORY")
public class CustDailyValueHistoryEntity implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String custId;
	private String productName;
	private BigDecimal totalValue;
	private BigDecimal freezeValue;
	private BigDecimal availableValue;
	private BigDecimal holdScale;
	private String recordDate;
	private String subAccountId;
	private BigDecimal realHoldScale;
	private String isShow;
	
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

	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return custId;
	}


	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "PRODUCT_NAME", length = 50)
	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Column(name = "TOTAL_VALUE", precision = 22, scale = 8)
	public BigDecimal getTotalValue() {
		return totalValue;
	}

	
	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

	@Column(name = "FREEZE_VALUE", precision = 22, scale = 8)
	public BigDecimal getFreezeValue() {
		return freezeValue;
	}


	public void setFreezeValue(BigDecimal freezeValue) {
		this.freezeValue = freezeValue;
	}

	@Column(name = "AVAILABLE_VALUE", precision = 22, scale = 8)
	public BigDecimal getAvailableValue() {
		return availableValue;
	}


	public void setAvailableValue(BigDecimal availableValue) {
		this.availableValue = availableValue;
	}

	@Column(name = "HOLD_SCALE", precision = 22, scale = 18)
	public BigDecimal getHoldScale() {
		return holdScale;
	}


	public void setHoldScale(BigDecimal holdScale) {
		this.holdScale = holdScale;
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

	@Column(name = "SUB_ACCOUNT_ID", length = 50)
	public String getSubAccountId() {
		return subAccountId;
	}


	public void setSubAccountId(String subAccountId) {
		this.subAccountId = subAccountId;
	}

	@Column(name = "REAL_HOLD_SCALE", precision = 22, scale = 18)
	public BigDecimal getRealHoldScale() {
		return realHoldScale;
	}


	public void setRealHoldScale(BigDecimal realHoldScale) {
		this.realHoldScale = realHoldScale;
	}

	@Column(name = "IS_SHOW", length = 10)
	public String getIsShow() {
		return isShow;
	}


	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

}
