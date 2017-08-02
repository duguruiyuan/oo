package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * 基础产品类型表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_WEALTH_TYPE_INFO")
public class WealthTypeInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	private String productNo;

	/**
	 * 产品类型
	 */
	private String lendingType;

	/**
	 * 单位：月
	 */
	private Integer typeTerm;

	/**
	 * 单位:月
	 */
	private Integer seatTerm;

	/**
	 * 年利率
	 */
	private BigDecimal yearRate;

	/**
	 * 到期结算本息、按月返息到期返本
	 */
	private String incomeType;

	/**
	 * 停用/启用
	 */
	private String enableStatus;
	
	/**
	 * 排序
	 */
	private Integer sort;
	
	/**
	 * 折年系数
	 */
	private BigDecimal rebateRatio;


	public BigDecimal getRebateRatio() {
		return rebateRatio;
	}

	public void setRebateRatio(BigDecimal rebateRatio) {
		this.rebateRatio = rebateRatio;
	}

	@Column(name = "PRODUCT_NO", length = 50)
	public String getProductNo() {
		return this.productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}

	@Column(name = "LENDING_TYPE", length = 50)
	public String getLendingType() {
		return this.lendingType;
	}

	public void setLendingType(String lendingType) {
		this.lendingType = lendingType;
	}

	@Column(name = "TYPE_TERM", length = 22)
	public Integer getTypeTerm() {
		return this.typeTerm;
	}

	public void setTypeTerm(Integer typeTerm) {
		this.typeTerm = typeTerm;
	}

	@Column(name = "SEAT_TERM", length = 22)
	public Integer getSeatTerm() {
		return this.seatTerm;
	}

	public void setSeatTerm(Integer seatTerm) {
		this.seatTerm = seatTerm;
	}

	@Column(name = "YEAR_RATE", precision = 22, scale = 18)
	public BigDecimal getYearRate() {
		return this.yearRate;
	}

	public void setYearRate(BigDecimal yearRate) {
		this.yearRate = yearRate;
	}

	@Column(name = "INCOME_TYPE", length = 100)
	public String getIncomeType() {
		return this.incomeType;
	}

	public void setIncomeType(String incomeType) {
		this.incomeType = incomeType;
	}

	@Column(name = "ENABLE_STATUS", length = 50)
	public String getEnableStatus() {
		return this.enableStatus;
	}

	public void setEnableStatus(String enableStatus) {
		this.enableStatus = enableStatus;
	}

	@Column(name = "SORT", length = 22)
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
}
