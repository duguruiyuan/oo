package com.slfinance.shanlincaifu.entity;
// default package

import javax.persistence.Column;import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BaoTProductTypeInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_PRODUCT_TYPE_INFO")
public class ProductTypeInfoEntity extends BaseEntity {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -4295199002158844279L;
	private String typeName;
	private String typeStatus;
	private String incomeType;
	private String incomeHandleMethod;
	private BigDecimal alreadyPreValue;
	private BigDecimal expectPreValue;
	private BigDecimal unopenValue;
	private String enableStatus;

	// Property accessors
	@Column(name = "TYPE_NAME", nullable = false, length = 50)
	public String getTypeName() {
		return this.typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Column(name = "TYPE_STATUS", length = 50)
	public String getTypeStatus() {
		return this.typeStatus;
	}

	public void setTypeStatus(String typeStatus) {
		this.typeStatus = typeStatus;
	}

	@Column(name = "INCOME_TYPE", length = 100)
	public String getIncomeType() {
		return this.incomeType;
	}

	public void setIncomeType(String incomeType) {
		this.incomeType = incomeType;
	}

	@Column(name = "INCOME_HANDLE_METHOD", length = 50)
	public String getIncomeHandleMethod() {
		return this.incomeHandleMethod;
	}

	public void setIncomeHandleMethod(String incomeHandleMethod) {
		this.incomeHandleMethod = incomeHandleMethod;
	}


	@Column(name = "EXPECT_PRE_VALUE", precision = 22, scale = 8)
	public BigDecimal getExpectPreValue() {
		return this.expectPreValue;
	}

	public BigDecimal getAlreadyPreValue() {
		return alreadyPreValue;
	}
	
	@Column(name = "ALREADY_PRE_VALUE", precision = 22, scale = 8)
	public void setAlreadyPreValue(BigDecimal alreadyPreValue) {
		this.alreadyPreValue = alreadyPreValue;
	}

	public void setExpectPreValue(BigDecimal expectPreValue) {
		this.expectPreValue = expectPreValue;
	}
	
	@Column(name = "UNOPEN_VALUE", precision = 22, scale = 8)
	public BigDecimal getUnopenValue() {
		return unopenValue;
	}

	public void setUnopenValue(BigDecimal unopenValue) {
		this.unopenValue = unopenValue;
	}

	@Column(name = "ENABLE_STATUS", length = 50)
	public String getEnableStatus() {
		return this.enableStatus;
	}

	public void setEnableStatus(String enableStatus) {
		this.enableStatus = enableStatus;
	}

}