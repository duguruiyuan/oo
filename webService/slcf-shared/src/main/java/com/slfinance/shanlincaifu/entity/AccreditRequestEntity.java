package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 授权申请表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_ACCREDIT_REQUEST")
public class AccreditRequestEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 借款id
	 */
	private String loanId;
	
	/**
	 * 还款计划编号
	 */
	private String loanNo;

	/**
	 * 授权状态,是否授权成功
	 */
	private String status;     //未授权      已授权
	
	/**
	 * 还款计划
	 */
	private String rempaymentPlan;
	
	/**
	 * @return the rempaymentPlan
	 */
	@Column(name = "REMPAYMENT_PLAN", length = 500)
	public String getRempaymentPlan() {
		return rempaymentPlan;
	}

	/**
	 * @param rempaymentPlan the rempaymentPlan to set
	 */
	public void setRempaymentPlan(String rempaymentPlan) {
		this.rempaymentPlan = rempaymentPlan;
	}

	@Column(name = "LOAN_ID", length = 50)
	public String getLoanId() {
		return loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	@Column(name = "LOAN_NO", length = 50)
	public String getLoanNo() {
		return loanNo;
	}

	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
	}
	
	@Column(name = "STATUS", length = 50)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	

}
