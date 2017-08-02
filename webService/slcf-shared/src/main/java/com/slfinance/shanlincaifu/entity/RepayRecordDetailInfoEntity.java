package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * BAO还款记录详情信息表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_REPAY_RECORD_DETAIL_INFO")
public class RepayRecordDetailInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 还款记录表主键ID
	 */
	private String repayRecordId;

	/**
	 * 还款计划表主键ID
	 */
	private String repayPlanId;

	/**
	 * 科目类型：本金、利息、管理费等
	 */
	private String subjectType;

	/**
	 * 科目方向:收入/支出
	 */
	private String subjectDirection;

	/**
	 * 交易金额
	 */
	private BigDecimal tradeAmount;



	@Column(name = "REPAY_RECORD_ID", length = 50)
	public String getRepayRecordId() {
		return this.repayRecordId;
	}

	public void setRepayRecordId(String repayRecordId) {
		this.repayRecordId = repayRecordId;
	}

	@Column(name = "REPAY_PLAN_ID", length = 50)
	public String getRepayPlanId() {
		return this.repayPlanId;
	}

	public void setRepayPlanId(String repayPlanId) {
		this.repayPlanId = repayPlanId;
	}

	@Column(name = "SUBJECT_TYPE", length = 50)
	public String getSubjectType() {
		return this.subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	@Column(name = "SUBJECT_DIRECTION", length = 50)
	public String getSubjectDirection() {
		return this.subjectDirection;
	}

	public void setSubjectDirection(String subjectDirection) {
		this.subjectDirection = subjectDirection;
	}

	@Column(name = "TRADE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getTradeAmount() {
		return this.tradeAmount;
	}

	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

}
