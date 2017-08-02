package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;


/**
 *  entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_WEALTH_INFO")
public class WealthInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 产品类型
	 */
	private String wealthTypeId;

	/**
	 * 年月日-产品序号-项目序号
项目序号同一天同一个产品，新建多个往回递增
	 */
	private String lendingNo;

	/**
	 * 待审核、审核回退、拒绝、待发布、发布中、已满额、收益中、到期处理中、已到期、流标
	 */
	private String wealthStatus;

	/**
	 * 流程状态,小状态
待审核、审核回退、拒绝、待发布、发布中、已满额、收益中、到期处理中、已到期、流标
	 */
	private String flowStatus;

	/**
	 * 计划总金额
	 */
	private BigDecimal planTotalAmount;

	/**
	 * 最小投资金额
	 */
	private BigDecimal investMinAmount;

	/**
	 * 最大投资金额
	 */
	private BigDecimal investMaxAmount;

	/**
	 * 
	 */
	private BigDecimal increaseAmount;

	/**
	 * 发布日期
	 */
	private Date releaseDate;

	/**
	 * 生效日期
	 */
	private Date effectDate;

	/**
	 * 到期时间
	 */
	private Date endDate;

	/**
	 * 还款日 固定一天
	 */
	private String nextRepayDay;

	/**
	 * 首期还款日
	 */
	private String firstRepayDay;

	/**
	 * 末期还款日
	 */
	private String lastRepayDay;
	
	/**
	 * 上个返息日
	 */
	private String prevRepayDay;

	/**
	 * 奖励利率
	 */
	private BigDecimal awardRate;

	/**
	 * 违约金率
	 */
	private BigDecimal penaltyRate;

	/**
	 * 保本、保息、保本保息
	 */
	private String ensureMethod;

	/**
	 * 项目描述
	 */
	private String wealthDescr;

	/**
	 * 停用/启用
	 */
	private String enableStatus;

	/**
	 * 年利率
	 */
	private BigDecimal yearRate;


	@Column(name = "WEALTH_TYPE_ID", length = 50)
	public String getWealthTypeId() {
		return this.wealthTypeId;
	}

	public void setWealthTypeId(String wealthTypeId) {
		this.wealthTypeId = wealthTypeId;
	}

	@Column(name = "LENDING_NO", length = 50)
	public String getLendingNo() {
		return this.lendingNo;
	}

	public void setLendingNo(String lendingNo) {
		this.lendingNo = lendingNo;
	}

	@Column(name = "WEALTH_STATUS", length = 50)
	public String getWealthStatus() {
		return this.wealthStatus;
	}

	public void setWealthStatus(String wealthStatus) {
		this.wealthStatus = wealthStatus;
	}

	@Column(name = "FLOW_STATUS", length = 50)
	public String getFlowStatus() {
		return this.flowStatus;
	}

	public void setFlowStatus(String flowStatus) {
		this.flowStatus = flowStatus;
	}

	@Column(name = "PLAN_TOTAL_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getPlanTotalAmount() {
		return this.planTotalAmount;
	}

	public void setPlanTotalAmount(BigDecimal planTotalAmount) {
		this.planTotalAmount = planTotalAmount;
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

	@Column(name = "INCREASE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getIncreaseAmount() {
		return this.increaseAmount;
	}

	public void setIncreaseAmount(BigDecimal increaseAmount) {
		this.increaseAmount = increaseAmount;
	}

	@Column(name = "RELEASE_DATE", length = 7)
	public Date getReleaseDate() {
		return this.releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	@Column(name = "EFFECT_DATE", length = 7)
	public Date getEffectDate() {
		return this.effectDate;
	}

	public void setEffectDate(Date effectDate) {
		this.effectDate = effectDate;
	}

	@Column(name = "END_DATE", length = 7)
	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name = "NEXT_REPAY_DAY", length = 8)
	public String getNextRepayDay() {
		return this.nextRepayDay;
	}

	public void setNextRepayDay(String nextRepayDay) {
		this.nextRepayDay = nextRepayDay;
	}

	@Column(name = "FIRST_REPAY_DAY", length = 8)
	public String getFirstRepayDay() {
		return this.firstRepayDay;
	}

	public void setFirstRepayDay(String firstRepayDay) {
		this.firstRepayDay = firstRepayDay;
	}

	@Column(name = "LAST_REPAY_DAY", length = 8)
	public String getLastRepayDay() {
		return this.lastRepayDay;
	}

	public void setLastRepayDay(String lastRepayDay) {
		this.lastRepayDay = lastRepayDay;
	}

	@Column(name = "PREV_REPAY_DAY", length = 8)
	public String getPrevRepayDay() {
		return prevRepayDay;
	}

	public void setPrevRepayDay(String prevRepayDay) {
		this.prevRepayDay = prevRepayDay;
	}

	@Column(name = "AWARD_RATE", precision = 22, scale = 18)
	public BigDecimal getAwardRate() {
		return this.awardRate;
	}

	public void setAwardRate(BigDecimal awardRate) {
		this.awardRate = awardRate;
	}

	@Column(name = "PENALTY_RATE", precision = 22, scale = 18)
	public BigDecimal getPenaltyRate() {
		return this.penaltyRate;
	}

	public void setPenaltyRate(BigDecimal penaltyRate) {
		this.penaltyRate = penaltyRate;
	}

	@Column(name = "ENSURE_METHOD", length = 50)
	public String getEnsureMethod() {
		return this.ensureMethod;
	}

	public void setEnsureMethod(String ensureMethod) {
		this.ensureMethod = ensureMethod;
	}

	@Column(name = "WEALTH_DESCR", length = 4000)
	public String getWealthDescr() {
		return this.wealthDescr;
	}

	public void setWealthDescr(String wealthDescr) {
		this.wealthDescr = wealthDescr;
	}

	@Column(name = "ENABLE_STATUS", length = 50)
	public String getEnableStatus() {
		return this.enableStatus;
	}

	public void setEnableStatus(String enableStatus) {
		this.enableStatus = enableStatus;
	}

	@Column(name = "YEAR_RATE", precision = 22, scale = 18)
	public BigDecimal getYearRate() {
		return yearRate;
	}

	public void setYearRate(BigDecimal yearRate) {
		this.yearRate = yearRate;
	}
}
