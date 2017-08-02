package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.math.BigDecimal;


/**
 * BAO项目投资情况表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_PROJECT_INVEST_INFO")
public class ProjectInvestInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 借款信息主键ID
	 */
	private String projectId;

	/**
	 * 已投标人数
	 */
	private Integer alreadyInvestPeoples;

	/**
	 * 已投标金额
	 */
	private BigDecimal alreadyInvestAmount;

	/**
	 * 已投标比例
	 */
	private BigDecimal alreadyInvestScale;

	/**
	 * 
	 */
	private String wealthId;

	/**
	 * 接口ID
	 */
	private String loanId;

	@Column(name = "PROJECT_ID", length = 50)
	public String getProjectId() {
		return this.projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	@Column(name = "ALREADY_INVEST_PEOPLES", length = 22)
	public Integer getAlreadyInvestPeoples() {
		return this.alreadyInvestPeoples;
	}

	public void setAlreadyInvestPeoples(Integer alreadyInvestPeoples) {
		this.alreadyInvestPeoples = alreadyInvestPeoples;
	}

	@Column(name = "ALREADY_INVEST_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getAlreadyInvestAmount() {
		return this.alreadyInvestAmount;
	}

	public void setAlreadyInvestAmount(BigDecimal alreadyInvestAmount) {
		this.alreadyInvestAmount = alreadyInvestAmount;
	}

	@Column(name = "ALREADY_INVEST_SCALE", precision = 22, scale = 18)
	public BigDecimal getAlreadyInvestScale() {
		return this.alreadyInvestScale;
	}

	public void setAlreadyInvestScale(BigDecimal alreadyInvestScale) {
		this.alreadyInvestScale = alreadyInvestScale;
	}

	@Column(name = "WEALTH_ID", length = 50)
	public String getWealthId() {
		return this.wealthId;
	}

	public void setWealthId(String wealthId) {
		this.wealthId = wealthId;
	}

	@Column(name = "LOAN_ID", length = 50)
	public String getLoanId() {
		return this.loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}
}
