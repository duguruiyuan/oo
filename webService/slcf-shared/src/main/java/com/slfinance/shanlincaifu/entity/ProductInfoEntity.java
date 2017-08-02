package com.slfinance.shanlincaifu.entity;
// default package

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BaoTProductInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_PRODUCT_INFO")
public class ProductInfoEntity extends BaseEntity {

	// Fields
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5823341107027348929L;
	private String productType;
	private String productName;
	private String productStatus;
	private String incomeType;
	private String incomeHandleMethod;
	private BigDecimal typeTerm;
	private BigDecimal seatTerm;
	private BigDecimal investMinAmount;
	private BigDecimal investMaxAmount;
	private BigDecimal planTotalAmount;
	private Timestamp productEndTime;
	private String ensureMethod;
	private String lucreIntoMethod;
	private String investBearinteMethod;
	private String grantRule;
	private String productDesc;
	private BigDecimal serviceChargeRate;
	private BigDecimal rechargeRate;
	private BigDecimal quitRate;
	private String enableStatus;
	private BigDecimal increaseAmount;
	private String favoriteSort;

	// Property accessors
	@Column(name = "PRODUCT_TYPE", nullable = false, length = 50)
	public String getProductType() {
		return this.productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	@Column(name = "PRODUCT_NAME", length = 150)
	public String getProductName() {
		return this.productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Column(name = "PRODUCT_STATUS", length = 50)
	public String getProductStatus() {
		return this.productStatus;
	}

	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
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

	@Column(name = "TYPE_TERM", precision = 22, scale = 0)
	public BigDecimal getTypeTerm() {
		return this.typeTerm;
	}

	public void setTypeTerm(BigDecimal typeTerm) {
		this.typeTerm = typeTerm;
	}

	@Column(name = "SEAT_TERM", precision = 22, scale = 0)
	public BigDecimal getSeatTerm() {
		return this.seatTerm;
	}

	public void setSeatTerm(BigDecimal seatTerm) {
		this.seatTerm = seatTerm;
	}

	@Column(name = "INVEST_MIN_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getInvestMinAmount() {
		return this.investMinAmount;
	}

	public void setInvestMinAmount(BigDecimal investMinAmount) {
		this.investMinAmount = investMinAmount;
	}

	@Column(name = "INVEST_MAX_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getInvestMaxAmount() {
		return this.investMaxAmount;
	}

	public void setInvestMaxAmount(BigDecimal investMaxAmount) {
		this.investMaxAmount = investMaxAmount;
	}

	@Column(name = "PLAN_TOTAL_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getPlanTotalAmount() {
		return this.planTotalAmount;
	}

	public void setPlanTotalAmount(BigDecimal planTotalAmount) {
		this.planTotalAmount = planTotalAmount;
	}

	@Column(name = "PRODUCT_END_TIME", length = 7)
	public Timestamp getProductEndTime() {
		return this.productEndTime;
	}

	public void setProductEndTime(Timestamp productEndTime) {
		this.productEndTime = productEndTime;
	}

	@Column(name = "ENSURE_METHOD", length = 50)
	public String getEnsureMethod() {
		return this.ensureMethod;
	}

	public void setEnsureMethod(String ensureMethod) {
		this.ensureMethod = ensureMethod;
	}

	@Column(name = "LUCRE_INTO_METHOD", length = 50)
	public String getLucreIntoMethod() {
		return this.lucreIntoMethod;
	}

	public void setLucreIntoMethod(String lucreIntoMethod) {
		this.lucreIntoMethod = lucreIntoMethod;
	}

	@Column(name = "INVEST_BEARINTE_METHOD", length = 50)
	public String getInvestBearinteMethod() {
		return this.investBearinteMethod;
	}

	public void setInvestBearinteMethod(String investBearinteMethod) {
		this.investBearinteMethod = investBearinteMethod;
	}

	@Column(name = "GRANT_RULE", length = 500)
	public String getGrantRule() {
		return this.grantRule;
	}

	public void setGrantRule(String grantRule) {
		this.grantRule = grantRule;
	}

	@Column(name = "PRODUCT_DESC", length = 2000)
	public String getProductDesc() {
		return this.productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	@Column(name = "SERVICE_CHARGE_RATE", precision = 22, scale = 18)
	public BigDecimal getServiceChargeRate() {
		return this.serviceChargeRate;
	}

	public void setServiceChargeRate(BigDecimal serviceChargeRate) {
		this.serviceChargeRate = serviceChargeRate;
	}

	@Column(name = "RECHARGE_RATE", precision = 22, scale = 18)
	public BigDecimal getRechargeRate() {
		return this.rechargeRate;
	}

	public void setRechargeRate(BigDecimal rechargeRate) {
		this.rechargeRate = rechargeRate;
	}

	@Column(name = "QUIT_RATE", precision = 22, scale = 18)
	public BigDecimal getQuitRate() {
		return this.quitRate;
	}

	public void setQuitRate(BigDecimal quitRate) {
		this.quitRate = quitRate;
	}

	@Column(name = "ENABLE_STATUS", length = 50)
	public String getEnableStatus() {
		return this.enableStatus;
	}

	public void setEnableStatus(String enableStatus) {
		this.enableStatus = enableStatus;
	}

	@Column(name = "INCREASE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getIncreaseAmount() {
		return increaseAmount;
	}

	public void setIncreaseAmount(BigDecimal increaseAmount) {
		this.increaseAmount = increaseAmount;
	}

	@Column(name = "FAVORITE_SORT", length = 50)
	public String getFavoriteSort() {
		return favoriteSort;
	}

	public void setFavoriteSort(String favoriteSort) {
		this.favoriteSort = favoriteSort;
	}
	
}