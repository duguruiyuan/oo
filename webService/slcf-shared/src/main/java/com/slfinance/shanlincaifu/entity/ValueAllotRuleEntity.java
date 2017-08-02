package com.slfinance.shanlincaifu.entity;
// default package

import javax.persistence.Column;import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BaoTValueAllotRule entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_VALUE_ALLOT_RULE")
public class ValueAllotRuleEntity extends BaseEntity  {

	// Fields

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7891084910209133497L;
	private String productTypeId;
	private String productId;
	private BigDecimal allotScale;
	private BigDecimal openValue;
	

	@Column(name = "PRODUCT_TYPE_ID", length = 50)
	public String getProductTypeId() {
		return this.productTypeId;
	}

	public void setProductTypeId(String productTypeId) {
		this.productTypeId = productTypeId;
	}

	@Column(name = "PRODUCT_ID", length = 50)
	public String getProductId() {
		return this.productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Column(name = "ALLOT_SCALE", precision = 22, scale = 18)
	public BigDecimal getAllotScale() {
		return this.allotScale;
	}

	public void setAllotScale(BigDecimal allotScale) {
		this.allotScale = allotScale;
	}

	@Column(name = "OPEN_VALUE", precision = 22, scale = 8)
	public BigDecimal getOpenValue() {
		return this.openValue;
	}

	public void setOpenValue(BigDecimal openValue) {
		this.openValue = openValue;
	}

}