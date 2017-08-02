package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 用户自动投标表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_AUTO_INVEST_INFO")
public class AutoInvestInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 客户信息主键ID
	 */
	private String custId;

	/**
	 * 最低利率
	 */
	private BigDecimal limitedYearRate;

	/**
	 * 最高期限
	 */
	private Integer limitedTerm;

	/**
	 * 回款方式,等额本息,每期还息到期付本,到期还本付息，多个用逗号隔开
	 */
	private String repaymentMethod;

	/**
	 * 可投产品：优选项目、债权转让，多个用逗号隔开
	 */
	private String canInvestProduct;

	/**
	 * 开启时间
	 */
	private Date openDate;

	/**
	 * 开启状态
	 */
	private String openStatus;

	/**
	 * 优先级别
	 */
	private String custPriority;

	/**
	 * 最高利率
	 */
	private BigDecimal limitedYearRateMax;

	/**
	 * 最低期限
	 */
	private Integer limitedTermMin;

	/**
	 * 提示状态：Y/N
	 */
	private String pointStatus;

	/**
	 * 保留金额
	 */
	private BigDecimal keepAvailableAmount;

	/**
	 * 期限单位
	 */
	private String loanUnit;
	
	/**
	 * 充值提示状态:Y/N
	 */
	private String rechargePointStatus;
	
	/**
	 * 小额复投状态:是/否
	 */
	private String smallQuantityInvest;
	
	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "LIMITED_YEAR_RATE", precision = 22, scale = 18)
	public BigDecimal getLimitedYearRate() {
		return this.limitedYearRate;
	}

	public void setLimitedYearRate(BigDecimal limitedYearRate) {
		this.limitedYearRate = limitedYearRate;
	}

	@Column(name = "LIMITED_TERM", length = 22)
	public Integer getLimitedTerm() {
		return this.limitedTerm;
	}

	public void setLimitedTerm(Integer limitedTerm) {
		this.limitedTerm = limitedTerm;
	}

	@Column(name = "REPAYMENT_METHOD", length = 500)
	public String getRepaymentMethod() {
		return this.repaymentMethod;
	}

	public void setRepaymentMethod(String repaymentMethod) {
		this.repaymentMethod = repaymentMethod;
	}

	@Column(name = "CAN_INVEST_PRODUCT", length = 500)
	public String getCanInvestProduct() {
		return this.canInvestProduct;
	}

	public void setCanInvestProduct(String canInvestProduct) {
		this.canInvestProduct = canInvestProduct;
	}

	@Column(name = "OPEN_DATE", length = 7)
	public Date getOpenDate() {
		return this.openDate;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}

	@Column(name = "OPEN_STATUS", length = 50)
	public String getOpenStatus() {
		return this.openStatus;
	}

	public void setOpenStatus(String openStatus) {
		this.openStatus = openStatus;
	}

	@Column(name = "CUST_PRIORITY", length = 50)
	public String getCustPriority() {
		return this.custPriority;
	}

	public void setCustPriority(String custPriority) {
		this.custPriority = custPriority;
	}

	@Column(name = "LIMITED_YEAR_RATE_MAX", precision = 22, scale = 18)
	public BigDecimal getLimitedYearRateMax() {
		return this.limitedYearRateMax;
	}

	public void setLimitedYearRateMax(BigDecimal limitedYearRateMax) {
		this.limitedYearRateMax = limitedYearRateMax;
	}

	@Column(name = "LIMITED_TERM_MIN", length = 22)
	public Integer getLimitedTermMin() {
		return this.limitedTermMin;
	}

	public void setLimitedTermMin(Integer limitedTermMin) {
		this.limitedTermMin = limitedTermMin;
	}

	@Column(name = "POINT_STATUS", length = 50)
	public String getPointStatus() {
		return this.pointStatus;
	}

	public void setPointStatus(String pointStatus) {
		this.pointStatus = pointStatus;
	}

	@Column(name = "KEEP_AVAILABLE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getKeepAvailableAmount() {
		return this.keepAvailableAmount;
	}

	public void setKeepAvailableAmount(BigDecimal keepAvailableAmount) {
		this.keepAvailableAmount = keepAvailableAmount;
	}

	@Column(name = "LOAN_UNIT", length = 50)
	public String getLoanUnit() {
		return this.loanUnit;
	}

	public void setLoanUnit(String loanUnit) {
		this.loanUnit = loanUnit;
	}
	
	@Column(name = "RECHARGE_POINT_STATUS", length = 50)
	public String getRechargePointStatus() {
		return this.rechargePointStatus;
	}

	public void setRechargePointStatus(String rechargePointStatus) {
		this.rechargePointStatus = rechargePointStatus;
	}
	
	@Column(name = "SMALL_QUANTITY_INVEST", length = 50)
	public String getSmallQuantityInvest() {
		return this.smallQuantityInvest;
	}

	public void setSmallQuantityInvest(String smallQuantityInvest) {
		this.smallQuantityInvest = smallQuantityInvest;
	}
}
