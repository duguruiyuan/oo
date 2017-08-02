package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * 自动发布规则表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_AUTO_PUBLISH_INFO")
public class AutoPublishInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 资产来源
	 */
	private String debtSource;

	/**
	 * 最低期限
	 */
	private Integer minTerm;

	/**
	 * 最高期限
	 */
	private Integer maxTerm;

	/**
	 * 最低利率
	 */
	private BigDecimal minYearRate;

	/**
	 * 最高利率
	 */
	private BigDecimal maxYearRate;

	/**
	 * 最低募集天数
	 */
	private Integer minRasieDays;

	/**
	 * 最高募集天数
	 */
	private Integer maxRasieDays;

	/**
	 * 还款方式
	 */
	private String repaymentMethod;

	/**
	 * 最低借款金额
	 */
	private BigDecimal minLoanAmount;

	/**
	 * 最高借款金额
	 */
	private BigDecimal maxLoanAmount;

	/**
	 * 标的个数
	 */
	private Integer maxLoanNumber;

	/**
	 * 最低线上金额
	 */
	private BigDecimal minTotalLoanAmount;

	/**
	 * 最高线上金额
	 */
	private BigDecimal maxTotalLoanAmount;



	@Column(name = "DEBT_SOURCE", length = 500)
	public String getDebtSource() {
		return this.debtSource;
	}

	public void setDebtSource(String debtSource) {
		this.debtSource = debtSource;
	}

	@Column(name = "MIN_TERM", length = 22)
	public Integer getMinTerm() {
		return this.minTerm;
	}

	public void setMinTerm(Integer minTerm) {
		this.minTerm = minTerm;
	}

	@Column(name = "MAX_TERM", length = 22)
	public Integer getMaxTerm() {
		return this.maxTerm;
	}

	public void setMaxTerm(Integer maxTerm) {
		this.maxTerm = maxTerm;
	}

	@Column(name = "MIN_YEAR_RATE", precision = 22, scale = 18)
	public BigDecimal getMinYearRate() {
		return this.minYearRate;
	}

	public void setMinYearRate(BigDecimal minYearRate) {
		this.minYearRate = minYearRate;
	}

	@Column(name = "MAX_YEAR_RATE", precision = 22, scale = 18)
	public BigDecimal getMaxYearRate() {
		return this.maxYearRate;
	}

	public void setMaxYearRate(BigDecimal maxYearRate) {
		this.maxYearRate = maxYearRate;
	}

	@Column(name = "MIN_RASIE_DAYS", length = 22)
	public Integer getMinRasieDays() {
		return this.minRasieDays;
	}

	public void setMinRasieDays(Integer minRasieDays) {
		this.minRasieDays = minRasieDays;
	}

	@Column(name = "MAX_RASIE_DAYS", length = 22)
	public Integer getMaxRasieDays() {
		return this.maxRasieDays;
	}

	public void setMaxRasieDays(Integer maxRasieDays) {
		this.maxRasieDays = maxRasieDays;
	}

	@Column(name = "REPAYMENT_METHOD", length = 500)
	public String getRepaymentMethod() {
		return this.repaymentMethod;
	}

	public void setRepaymentMethod(String repaymentMethod) {
		this.repaymentMethod = repaymentMethod;
	}

	@Column(name = "MIN_LOAN_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getMinLoanAmount() {
		return this.minLoanAmount;
	}

	public void setMinLoanAmount(BigDecimal minLoanAmount) {
		this.minLoanAmount = minLoanAmount;
	}

	@Column(name = "MAX_LOAN_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getMaxLoanAmount() {
		return this.maxLoanAmount;
	}

	public void setMaxLoanAmount(BigDecimal maxLoanAmount) {
		this.maxLoanAmount = maxLoanAmount;
	}

	@Column(name = "MAX_LOAN_NUMBER", length = 22)
	public Integer getMaxLoanNumber() {
		return this.maxLoanNumber;
	}

	public void setMaxLoanNumber(Integer maxLoanNumber) {
		this.maxLoanNumber = maxLoanNumber;
	}

	@Column(name = "MIN_TOTAL_LOAN_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getMinTotalLoanAmount() {
		return this.minTotalLoanAmount;
	}

	public void setMinTotalLoanAmount(BigDecimal minTotalLoanAmount) {
		this.minTotalLoanAmount = minTotalLoanAmount;
	}

	@Column(name = "MAX_TOTAL_LOAN_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getMaxTotalLoanAmount() {
		return this.maxTotalLoanAmount;
	}

	public void setMaxTotalLoanAmount(BigDecimal maxTotalLoanAmount) {
		this.maxTotalLoanAmount = maxTotalLoanAmount;
	}

}
