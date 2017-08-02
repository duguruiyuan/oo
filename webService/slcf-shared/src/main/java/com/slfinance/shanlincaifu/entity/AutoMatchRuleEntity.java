package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * 自动撮合规则表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_AUTO_MATCH_RULE")
public class AutoMatchRuleEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 投资金额下限
	 */
	private BigDecimal investMiniAmt;

	/**
	 * 投资金额上限
	 */
	private BigDecimal investMaxAmt;

	/**
	 * 债券金额下限
	 */
	private BigDecimal debtMiniAmt;

	/**
	 * 债券金额上限
	 */
	private BigDecimal debtMaxAmt;

	/**
	 * 金额单位
	 */
	private String amtUnit;

	/**
	 * 债券来源
	 */
	private String debtSource;

	/**
	 * 1:有效
0：无效
	 */
	private String status;



	@Column(name = "INVEST_MINI_AMT", precision = 22, scale = 8)
	public BigDecimal getInvestMiniAmt() {
		return this.investMiniAmt;
	}

	public void setInvestMiniAmt(BigDecimal investMiniAmt) {
		this.investMiniAmt = investMiniAmt;
	}

	@Column(name = "INVEST_MAX_AMT", precision = 22, scale = 8)
	public BigDecimal getInvestMaxAmt() {
		return this.investMaxAmt;
	}

	public void setInvestMaxAmt(BigDecimal investMaxAmt) {
		this.investMaxAmt = investMaxAmt;
	}

	@Column(name = "DEBT_MINI_AMT", precision = 22, scale = 8)
	public BigDecimal getDebtMiniAmt() {
		return this.debtMiniAmt;
	}

	public void setDebtMiniAmt(BigDecimal debtMiniAmt) {
		this.debtMiniAmt = debtMiniAmt;
	}

	@Column(name = "DEBT_MAX_AMT", precision = 22, scale = 8)
	public BigDecimal getDebtMaxAmt() {
		return this.debtMaxAmt;
	}

	public void setDebtMaxAmt(BigDecimal debtMaxAmt) {
		this.debtMaxAmt = debtMaxAmt;
	}

	@Column(name = "AMT_UNIT", length = 50)
	public String getAmtUnit() {
		return this.amtUnit;
	}

	public void setAmtUnit(String amtUnit) {
		this.amtUnit = amtUnit;
	}

	@Column(name = "DEBT_SOURCE", length = 500)
	public String getDebtSource() {
		return this.debtSource;
	}

	public void setDebtSource(String debtSource) {
		this.debtSource = debtSource;
	}

	@Column(name = "STATUS", length = 50)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
