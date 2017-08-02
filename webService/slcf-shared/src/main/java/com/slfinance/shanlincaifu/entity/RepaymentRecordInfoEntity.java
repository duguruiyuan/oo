/** 
 * @(#)RepaymentRecordInfoEntity.java 1.0.0 2015年5月1日 下午4:27:42  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**   
 * 
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月1日 下午4:27:42 $ 
 */
@Entity
@Table(name = "BAO_T_REPAYMENT_RECORD_INFO")
public class RepaymentRecordInfoEntity extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String loanId;
	private BigDecimal repayAmount;
	private BigDecimal alreadyRepayAmt;
	private String handleStatus;

	/**
	 * 正常还款、逾期还款、一次性结清(全部)
	 */
	private String tradeType;

	/**
	 * 项目信息表主键ID
	 */
	private String projectId;

	/**
	 * 关联表标识
	 */
	private String relateType;

	/**
	 * 关联主键
	 */
	private String relatePrimary;
	
	@Column(name = "LOAN_ID", length = 50)
	public String getLoanId() {
		return loanId;
	}
	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}
	
	@Column(name = "REPAY_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getRepayAmount() {
		return repayAmount;
	}
	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}
	
	@Column(name = "ALREADY_REPAY_AMT", precision = 22, scale = 8)
	public BigDecimal getAlreadyRepayAmt() {
		return alreadyRepayAmt;
	}
	public void setAlreadyRepayAmt(BigDecimal alreadyRepayAmt) {
		this.alreadyRepayAmt = alreadyRepayAmt;
	}
	
	@Column(name = "HANDLE_STATUS", length = 10)
	public String getHandleStatus() {
		return handleStatus;
	}
	public void setHandleStatus(String handleStatus) {
		this.handleStatus = handleStatus;
	}
	
	@Column(name = "TRADE_TYPE", length = 50)
	public String getTradeType() {
		return this.tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	@Column(name = "PROJECT_ID", length = 50)
	public String getProjectId() {
		return this.projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	@Column(name = "RELATE_TYPE", length = 200)
	public String getRelateType() {
		return this.relateType;
	}

	public void setRelateType(String relateType) {
		this.relateType = relateType;
	}

	@Column(name = "RELATE_PRIMARY", length = 50)
	public String getRelatePrimary() {
		return this.relatePrimary;
	}

	public void setRelatePrimary(String relatePrimary) {
		this.relatePrimary = relatePrimary;
	}	
}
