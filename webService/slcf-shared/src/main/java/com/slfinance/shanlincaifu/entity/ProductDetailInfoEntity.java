package com.slfinance.shanlincaifu.entity;
// default package

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * BaoTProductDetailInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_PRODUCT_DETAIL_INFO")
public class ProductDetailInfoEntity extends BaseEntity {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -6371255077320647902L;
	private String productId;
	private BigDecimal alreadyInvestPeople;
	private BigDecimal alreadyInvestAmount;
	private BigDecimal currUsableValue;
	private BigDecimal partakeOrganizs;
	private BigDecimal partakeCrerigs;
	private BigDecimal accumulativeLucre;
	
	@Getter
	@Setter
	@Column(name = "CURR_TERM", length = 50)
	/**产品期数**/
	private String currTerm;
	// Constructors
	@Column(name = "PRODUCT_ID", length = 50)
	public String getProductId() {
		return this.productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Column(name = "ALREADY_INVEST_PEOPLE", precision = 22, scale = 0)
	public BigDecimal getAlreadyInvestPeople() {
		return this.alreadyInvestPeople;
	}

	public void setAlreadyInvestPeople(BigDecimal alreadyInvestPeople) {
		this.alreadyInvestPeople = alreadyInvestPeople;
	}

	@Column(name = "ALREADY_INVEST_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getAlreadyInvestAmount() {
		return this.alreadyInvestAmount;
	}

	public void setAlreadyInvestAmount(BigDecimal alreadyInvestAmount) {
		this.alreadyInvestAmount = alreadyInvestAmount;
	}

	@Column(name = "CURR_USABLE_VALUE", precision = 22, scale = 8)
	public BigDecimal getCurrUsableValue() {
		return currUsableValue;
	}

	public void setCurrUsableValue(BigDecimal currUsableValue) {
		this.currUsableValue = currUsableValue;
	}
	@Column(name = "PARTAKE_ORGANIZS", precision = 22, scale = 0)
	public BigDecimal getPartakeOrganizs() {
		return partakeOrganizs;
	}

	public void setPartakeOrganizs(BigDecimal partakeOrganizs) {
		this.partakeOrganizs = partakeOrganizs;
	}
	@Column(name = "PARTAKE_CRERIGS", precision = 22, scale = 0)
	public BigDecimal getPartakeCrerigs() {
		return partakeCrerigs;
	}

	public void setPartakeCrerigs(BigDecimal partakeCrerigs) {
		this.partakeCrerigs = partakeCrerigs;
	}
	@Column(name = "ACCUMULATIVE_LUCRE", precision = 22, scale = 8)
	public BigDecimal getAccumulativeLucre() {
		return accumulativeLucre;
	}

	public void setAccumulativeLucre(BigDecimal accumulativeLucre) {
		this.accumulativeLucre = accumulativeLucre;
	}

}