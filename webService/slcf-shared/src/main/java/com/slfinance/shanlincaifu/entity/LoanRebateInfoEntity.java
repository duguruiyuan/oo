package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * 用于计算业绩，录入借款时需判断是否在本表范围内 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_LOAN_REBATE_INFO")
public class LoanRebateInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 还款方式
	 */
	private String repaymentMethod;

	/**
	 * 期限
	 */
	private Integer loanTerm;

	/**
	 * 折年系数
	 */
	private BigDecimal rebateRatio;

	/**
	 * 排序
	 */
	private Integer sort;

	/**
	 * 管理费率
	 */
	private BigDecimal manageRate;

	/**
	 * 产品类型
	 */
	private String productType;

	/**
	 * 商务、企业
	 */
	private String flag;

	/**
	 * 期限单位
	 */
	private String loanUnit;

	/**
	 * 总打包价年化利率
	 */
	private BigDecimal totalYearRate;

	/**
	 * 年化利率
	 */
	private BigDecimal yearRate;
	
	/**
	 * 奖励利率
	 */
	private BigDecimal awardRate;

	@Column(name = "REPAYMENT_METHOD", length = 300)
	public String getRepaymentMethod() {
		return this.repaymentMethod;
	}

	public void setRepaymentMethod(String repaymentMethod) {
		this.repaymentMethod = repaymentMethod;
	}

	@Column(name = "LOAN_TERM", length = 22)
	public Integer getLoanTerm() {
		return this.loanTerm;
	}

	public void setLoanTerm(Integer loanTerm) {
		this.loanTerm = loanTerm;
	}

	@Column(name = "REBATE_RATIO", precision = 22, scale = 18)
	public BigDecimal getRebateRatio() {
		return this.rebateRatio;
	}

	public void setRebateRatio(BigDecimal rebateRatio) {
		this.rebateRatio = rebateRatio;
	}

	@Column(name = "SORT", length = 22)
	public Integer getSort() {
		return this.sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	@Column(name = "MANAGE_RATE", precision = 22, scale = 18)
	public BigDecimal getManageRate() {
		return this.manageRate;
	}

	public void setManageRate(BigDecimal manageRate) {
		this.manageRate = manageRate;
	}

	@Column(name = "PRODUCT_TYPE", length = 50)
	public String getProductType() {
		return this.productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	@Column(name = "FLAG", length = 50)
	public String getFlag() {
		return this.flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	@Column(name = "LOAN_UNIT", length = 50)
	public String getLoanUnit() {
		return this.loanUnit;
	}

	public void setLoanUnit(String loanUnit) {
		this.loanUnit = loanUnit;
	}

	@Column(name = "TOTAL_YEAR_RATE", precision = 22, scale = 18)
	public BigDecimal getTotalYearRate() {
		return this.totalYearRate;
	}

	public void setTotalYearRate(BigDecimal totalYearRate) {
		this.totalYearRate = totalYearRate;
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
}
