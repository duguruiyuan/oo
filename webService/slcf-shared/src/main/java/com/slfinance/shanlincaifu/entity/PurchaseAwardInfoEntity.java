package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * 
 * <加息劵使用对应还款方式产生的奖励计划表>
 * <加息劵使用对应还款方式产生的奖励计划表>
 * 
 * @author  xp_Huang
 * @version  [版本号, 2017年7月11日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Entity
@Table(name = "BAO_T_PURCHASE_AWARD")
public class PurchaseAwardInfoEntity extends BaseEntity  {

	// <变量的意义、目的、功能和可能被用到的地方>
	private static final long serialVersionUID = -6803071549452094327L;

	/**
	 * 借款ID
	 */
	private String loanId;
	
	/**
	 * 当期期数
	 */
	private Integer currentTerm;
	
	/**
	 * 奖励状态：已结清、未结清
	 */
	private String awardStatus;
	
	/**
	 * 用户ID
	 */
	private String custId;
	
	/**
	 * 加息券的奖励金额
	 */
	private BigDecimal awardAmount;
	
	/**
	 * 此次购买投资的金额
	 */
	private BigDecimal investAmonut;
	
	/**
	 * 转让申请ID
	 */
	private String transferApplyId;
	
	/**
	 * 投资ID
	 */
	private String investId;

	/**
	 * 对应回款计划编号
	 */
	private String paymentPlanId;

	
	@Column(name="TRANSFER_APPLY_ID",length=50)
	public String getTransferApplyId() {
		return transferApplyId;
	}

	public void setTransferApplyId(String transferApplyId) {
		this.transferApplyId = transferApplyId;
	}
	
	@Column(name="INVEST_ID",length=50)
	public String getInvestId() {
		return investId;
	}

	public void setInvestId(String investId) {
		this.investId = investId;
	}

	@Column(name="LOAN_ID",length=50)
	public String getLoanId() {
		return loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	@Column(name="CURRENT_TERM",length=22)
	public Integer getCurrentTerm() {
		return currentTerm;
	}

	public void setCurrentTerm(Integer currentTerm) {
		this.currentTerm = currentTerm;
	}

	@Column(name="AWARD_STATUS",length=50)
	public String getAwardStatus() {
		return awardStatus;
	}

	public void setAwardStatus(String awardStatus) {
		this.awardStatus = awardStatus;
	}

	@Column(name="CUST_ID",length=50)
	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name="AWARD_AMOUNT",precision = 22, scale = 8)
	public BigDecimal getAwardAmount() {
		return awardAmount;
	}

	public void setAwardAmount(BigDecimal awardAmount) {
		this.awardAmount = awardAmount;
	}

	@Column(name="INVEST_AMONUT",precision = 22, scale = 8)
	public BigDecimal getInvestAmonut() {
		return investAmonut;
	}

	public void setInvestAmonut(BigDecimal investAmonut) {
		this.investAmonut = investAmonut;
	}

	@Column(name = "PAYMENT_PLAN_ID", length = 50)
	public String getPaymentPlanId() {
		return paymentPlanId;
	}

	public void setPaymentPlanId(String paymentPlanId) {
		this.paymentPlanId = paymentPlanId;
	}
}
