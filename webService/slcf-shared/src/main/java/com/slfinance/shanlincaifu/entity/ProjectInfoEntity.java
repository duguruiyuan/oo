package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import java.math.BigDecimal;
import java.util.Date;


/**
 * BAO项目信息表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_PROJECT_INFO")
public class ProjectInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 公司
	 */
	private String custId;

	/**
	 * 融资租赁、私募基金
	 */
	private String projectType;

	/**
	 * 项目编号
	 */
	private String projectNo;

	/**
	 * 公司名称
	 */
	private String companyName;

	/**
	 * 项目名称
	 */
	private String projectName;

	/**
	 * 暂存、待审核、审核回退、拒绝、待发布、发布中、满标复核、还款中、已到期、已逾期、提前结清、流标
	 */
	private String projectStatus;

	/**
	 * 流程状态,小状态
暂存、待审核、审核回退、拒绝、待发布、发布中、满标复核、还款中、已到期、已逾期、提前结清、流标
	 */
	private String flowStatus;

	/**
	 * 原始年化利率
	 */
	private BigDecimal actualYearRate;

	/**
	 * 用户年化利率
	 */
	private BigDecimal yearRate;

	/**
	 * 奖励利率
	 */
	private BigDecimal awardRate;

	/**
	 * 项目总金额
	 */
	private BigDecimal projectTotalAmount;

	/**
	 * 单位：月
	 */
	private Integer typeTerm;

	/**
	 * 单位:天
	 */
	private Integer seatTerm;

	/**
	 * 最小投资金额
	 */
	private BigDecimal investMinAmount;

	/**
	 * 最大投资金额
	 */
	private BigDecimal investMaxAmount;

	/**
	 * 递增金额
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
	private Date projectEndDate;

	/**
	 * 到期还本付息
按季付息，到期还本
按月付息，到期还本、
按月还款，等额本息
	 */
	private String repaymnetMethod;

	/**
	 * 保本、保息、保本保息
	 */
	private String ensureMethod;

	/**
	 * 是、否
	 */
	private String isAtone;

	/**
	 * 项目描述
	 */
	private String projectDescr;

	/**
	 * 企业信息
	 */
	private String companyDescr;

	/**
	 * 违约金率
	 */
	private BigDecimal penaltyRate;

	/**
	 * 风险金率
	 */
	private BigDecimal riskRate;

	/**
	 * 排序
	 */
	private String favoriteSort;

	/**
	 * 还款日 固定一天
	 */
	private String repaymentDay;

	/**
	 * 首期还款日
	 */
	private String firstTermRepayDay;

	/**
	 * 末期还款日
	 */
	private String lastTermRepayDay;

	/**
	 * 剩余期数
	 */
	private Integer remainderTerms;

	/**
	 * 剩余本金
	 */
	private BigDecimal remainderPrincipal;

	/**
	 * 停用/启用
	 */
	private String enableStatus;

	
	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "PROJECT_TYPE", length = 50)
	public String getProjectType() {
		return this.projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	@Column(name = "PROJECT_NO", length = 100)
	public String getProjectNo() {
		return this.projectNo;
	}

	public void setProjectNo(String projectNo) {
		this.projectNo = projectNo;
	}

	@Column(name = "COMPANY_NAME", length = 500)
	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Column(name = "PROJECT_NAME", length = 150)
	public String getProjectName() {
		return this.projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Column(name = "PROJECT_STATUS", length = 50)
	public String getProjectStatus() {
		return this.projectStatus;
	}

	public void setProjectStatus(String projectStatus) {
		this.projectStatus = projectStatus;
	}

	@Column(name = "FLOW_STATUS", length = 50)
	public String getFlowStatus() {
		return this.flowStatus;
	}

	public void setFlowStatus(String flowStatus) {
		this.flowStatus = flowStatus;
	}

	@Column(name = "ACTUAL_YEAR_RATE", precision = 22, scale = 18)
	public BigDecimal getActualYearRate() {
		return this.actualYearRate;
	}

	public void setActualYearRate(BigDecimal actualYearRate) {
		this.actualYearRate = actualYearRate;
	}

	@Column(name = "YEAR_RATE", precision = 22, scale = 18)
	public BigDecimal getYearRate() {
		return this.yearRate;
	}

	public void setYearRate(BigDecimal yearRate) {
		this.yearRate = yearRate;
	}

	@Column(name = "AWARD_RATE", precision = 22, scale = 18)
	public BigDecimal getAwardRate() {
		return this.awardRate;
	}

	public void setAwardRate(BigDecimal awardRate) {
		this.awardRate = awardRate;
	}

	@Column(name = "PROJECT_TOTAL_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getProjectTotalAmount() {
		return this.projectTotalAmount;
	}

	public void setProjectTotalAmount(BigDecimal projectTotalAmount) {
		this.projectTotalAmount = projectTotalAmount;
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

	@Column(name = "PROJECT_END_DATE", length = 7)
	public Date getProjectEndDate() {
		return this.projectEndDate;
	}

	public void setProjectEndDate(Date projectEndDate) {
		this.projectEndDate = projectEndDate;
	}

	@Column(name = "REPAYMNET_METHOD", length = 50)
	public String getRepaymnetMethod() {
		return this.repaymnetMethod;
	}

	public void setRepaymnetMethod(String repaymnetMethod) {
		this.repaymnetMethod = repaymnetMethod;
	}

	@Column(name = "ENSURE_METHOD", length = 50)
	public String getEnsureMethod() {
		return this.ensureMethod;
	}

	public void setEnsureMethod(String ensureMethod) {
		this.ensureMethod = ensureMethod;
	}

	@Column(name = "IS_ATONE", length = 50)
	public String getIsAtone() {
		return this.isAtone;
	}

	public void setIsAtone(String isAtone) {
		this.isAtone = isAtone;
	}

	@Lob
	@Column(name = "PROJECT_DESCR")
	public String getProjectDescr() {
		return this.projectDescr;
	}

	public void setProjectDescr(String projectDescr) {
		this.projectDescr = projectDescr;
	}

	@Lob
	@Column(name = "COMPANY_DESCR", length = 4000)
	public String getCompanyDescr() {
		return this.companyDescr;
	}

	public void setCompanyDescr(String companyDescr) {
		this.companyDescr = companyDescr;
	}

	@Column(name = "PENALTY_RATE", precision = 22, scale = 18)
	public BigDecimal getPenaltyRate() {
		return this.penaltyRate;
	}

	public void setPenaltyRate(BigDecimal penaltyRate) {
		this.penaltyRate = penaltyRate;
	}

	@Column(name = "RISK_RATE", precision = 22, scale = 18)
	public BigDecimal getRiskRate() {
		return this.riskRate;
	}

	public void setRiskRate(BigDecimal riskRate) {
		this.riskRate = riskRate;
	}

	@Column(name = "FAVORITE_SORT", length = 50)
	public String getFavoriteSort() {
		return this.favoriteSort;
	}

	public void setFavoriteSort(String favoriteSort) {
		this.favoriteSort = favoriteSort;
	}

	@Column(name = "REPAYMENT_DAY", length = 50)
	public String getRepaymentDay() {
		return this.repaymentDay;
	}

	public void setRepaymentDay(String repaymentDay) {
		this.repaymentDay = repaymentDay;
	}

	@Column(name = "FIRST_TERM_REPAY_DAY", length = 8)
	public String getFirstTermRepayDay() {
		return this.firstTermRepayDay;
	}

	public void setFirstTermRepayDay(String firstTermRepayDay) {
		this.firstTermRepayDay = firstTermRepayDay;
	}

	@Column(name = "LAST_TERM_REPAY_DAY", length = 8)
	public String getLastTermRepayDay() {
		return this.lastTermRepayDay;
	}

	public void setLastTermRepayDay(String lastTermRepayDay) {
		this.lastTermRepayDay = lastTermRepayDay;
	}

	@Column(name = "REMAINDER_TERMS", length = 22)
	public Integer getRemainderTerms() {
		return this.remainderTerms;
	}

	public void setRemainderTerms(Integer remainderTerms) {
		this.remainderTerms = remainderTerms;
	}

	@Column(name = "REMAINDER_PRINCIPAL", precision = 22, scale = 8)
	public BigDecimal getRemainderPrincipal() {
		return this.remainderPrincipal;
	}

	public void setRemainderPrincipal(BigDecimal remainderPrincipal) {
		this.remainderPrincipal = remainderPrincipal;
	}

	@Column(name = "ENABLE_STATUS", length = 50)
	public String getEnableStatus() {
		return this.enableStatus;
	}

	public void setEnableStatus(String enableStatus) {
		this.enableStatus = enableStatus;
	}
	public ProjectInfoEntity() {
	}

	public ProjectInfoEntity(String projectType, String projectNo,
			String companyName, String projectName, String projectStatus,
			BigDecimal actualYearRate, BigDecimal yearRate,
			BigDecimal projectTotalAmount, Integer typeTerm, Integer seatTerm,
			BigDecimal investMinAmount, BigDecimal increaseAmount,
			Date releaseDate, Date effectDate, Date projectEndDate,
			String repaymnetMethod, String ensureMethod, String isAtone,
			String projectDescr, String companyDescr) {
		this.projectType = projectType;
		this.projectNo = projectNo;
		this.companyName = companyName;
		this.projectName = projectName;
		this.projectStatus = projectStatus;
		this.actualYearRate = actualYearRate;
		this.yearRate = yearRate;
		this.projectTotalAmount = projectTotalAmount;
		this.typeTerm = typeTerm;
		this.seatTerm = seatTerm;
		this.investMinAmount = investMinAmount;
		this.increaseAmount = increaseAmount;
		this.releaseDate = releaseDate;
		this.effectDate = effectDate;
		this.projectEndDate = projectEndDate;
		this.repaymnetMethod = repaymnetMethod;
		this.ensureMethod = ensureMethod;
		this.isAtone = isAtone;
		this.projectDescr = projectDescr;
		this.companyDescr = companyDescr;
	}

}
