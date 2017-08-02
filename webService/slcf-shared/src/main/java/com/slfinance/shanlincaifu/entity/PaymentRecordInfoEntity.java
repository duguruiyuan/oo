package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.math.BigDecimal;


/**
 * BAO付款记录表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_PAYMENT_RECORD_INFO")
public class PaymentRecordInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 关联主键
	 */
	private String relatePrimary;

	/**
	 * 关联表标识
	 */
	private String relateType;

	/**
	 * 项目信息表主键ID
	 */
	private String projectId;

	/**
	 * 客户主键
	 */
	private String custId;

	/**
	 * 正常还款、逾期还款、一次性结清(全部)
	 */
	private String tradeType;

	/**
	 * 当前还款总金额
	 */
	private BigDecimal repayAmount;

	/**
	 * 债权主键ID
	 */
	private String loanId;

	@Column(name = "RELATE_PRIMARY", length = 50)
	public String getRelatePrimary() {
		return this.relatePrimary;
	}

	public void setRelatePrimary(String relatePrimary) {
		this.relatePrimary = relatePrimary;
	}

	@Column(name = "RELATE_TYPE", length = 200)
	public String getRelateType() {
		return this.relateType;
	}

	public void setRelateType(String relateType) {
		this.relateType = relateType;
	}

	@Column(name = "PROJECT_ID", length = 50)
	public String getProjectId() {
		return this.projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "TRADE_TYPE", length = 50)
	public String getTradeType() {
		return this.tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	@Column(name = "REPAY_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getRepayAmount() {
		return this.repayAmount;
	}

	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}

	@Column(name = "LOAN_ID", length = 50)
	public String getLoanId() {
		return this.loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}
}
