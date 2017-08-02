package com.slfinance.shanlincaifu.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BAO_T_PRODUCT_RATE_INFO")
public class ProductRateInfoEntity extends BaseEntity {

	private static final long serialVersionUID = 1269068016242721844L;
	private String productId;
	private BigDecimal yearRate;
	private BigDecimal awardRate;
	private Integer lowerLimitDay;
	private Integer upperLimitDay;
	
	@Column(name = "PRODUCT_ID", length=50)
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	@Column(name = "YEAR_RATE", precision = 22, scale = 18)
	public BigDecimal getYearRate() {
		return yearRate;
	}
	public void setYearRate(BigDecimal yearRate) {
		this.yearRate = yearRate;
	}
	
	@Column(name = "AWARD_RATE", precision = 22, scale = 18)
	public BigDecimal getAwardRate() {
		return awardRate;
	}
	public void setAwardRate(BigDecimal awardRate) {
		this.awardRate = awardRate;
	}
	
	@Column(name = "LOWER_LIMIT_DAY")
	public Integer getLowerLimitDay() {
		return lowerLimitDay;
	}
	public void setLowerLimitDay(Integer lowerLimitDay) {
		this.lowerLimitDay = lowerLimitDay;
	}
	
	@Column(name = "UPPER_LIMIT_DAY")
	public Integer getUpperLimitDay() {
		return upperLimitDay;
	}
	public void setUpperLimitDay(Integer upperLimitDay) {
		this.upperLimitDay = upperLimitDay;
	}

}
