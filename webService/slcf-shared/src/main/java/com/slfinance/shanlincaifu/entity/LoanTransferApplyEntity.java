package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 债权转让申请表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_LOAN_TRANSFER_APPLY")
public class LoanTransferApplyEntity extends BaseEntity  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 转让人持有情况ID
	 */
	private String senderHoldId;

	/**
	 * 转让人客户主键
	 */
	private String senderCustId;

	/**
	 * 转让持有比例
	 */
	private BigDecimal tradeScale;

	/**
	 * 剩余可转让持有比例
	 */
	private BigDecimal remainderTradeScale;

	/**
	 * 误差系数是为了兼容我们现有系统与预算公式之间的误差
	 */
	private BigDecimal deviationScale;

	/**
	 * 折价系数指债权必须按照一定的折扣转让
	 */
	private BigDecimal reducedScale;

	/**
	 * 已转让金额
	 */
	private BigDecimal tradeAmount;

	/**
	 * 转让手续费
	 */
	private BigDecimal manageAmount;

	/**
	 * 已转让次数
	 */
	private Integer tradeTimes;

	/**
	 * 待转让、转让成功、转让失败
	 */
	private String applyStatus;

	/**
	 * 转让编号
	 */
	private String transferNo;

	/**
	 * 转让开始日期
	 */
	private Date transferStartDate;

	/**
	 * 转让结束日期
	 */
	private Date transferEndDate;

	/**
	 * 已转让价值
	 */
	private BigDecimal tradeValue;

	/**
	 * 已转让本金
	 */
	private BigDecimal tradePrincipal;

	/**
	 * 未撤销、已撤销
	 */
	private String cancelStatus;
	
	/**
	 * 转让比例
	 */
	private BigDecimal transferScale;
	
	/**
	 * 协议模板
	 */
	private String protocolType;
	
	/**
	 * 审核状态
	 */
	private String auditStatus;
	
	/**
	 * 可转让标识
	 */
	private Integer transferSeatTerm;
	
	/**
	 * 转让费率
	 */
	private BigDecimal transferRate;
	
	/**
	 * 最小投资金额
	 */
	private BigDecimal investMinAmount;

	/**
	 * 递增金额
	 */
	private BigDecimal increaseAmount;
	
	/**
	 * 是否跑过智能投顾标识：Y/N，转让审核通过默认为N
	 */
	private String isRunAutoInvest;
	
	@Column(name = "INVEST_MIN_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getInvestMinAmount() {
		return this.investMinAmount;
	}

	public void setInvestMinAmount(BigDecimal investMinAmount) {
		this.investMinAmount = investMinAmount;
	}
	
	@Column(name = "INCREASE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getIncreaseAmount() {
		return this.increaseAmount;
	}

	public void setIncreaseAmount(BigDecimal increaseAmount) {
		this.increaseAmount = increaseAmount;
	}
	
	/**
	 * 置顶状态：1代表置顶
	 */
	private String stickyStatus;
	
	/**
	 * 置顶级别
	 */
	private String stickyLevel;
	
	
	@Column(name = "TRANSFER_RATE", precision = 22, scale = 18)
	public BigDecimal getTransferRate() {
		return transferRate;
	}

	public void setTransferRate(BigDecimal transferRate) {
		this.transferRate = transferRate;
	}

	@Column(name = "AUDIT_STATUS", length = 50)
	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	@Column(name = "SENDER_HOLD_ID", length = 50)
	public String getSenderHoldId() {
		return this.senderHoldId;
	}

	public void setSenderHoldId(String senderHoldId) {
		this.senderHoldId = senderHoldId;
	}

	@Column(name = "SENDER_CUST_ID", length = 50)
	public String getSenderCustId() {
		return this.senderCustId;
	}

	public void setSenderCustId(String senderCustId) {
		this.senderCustId = senderCustId;
	}

	@Column(name = "TRADE_SCALE", precision = 22, scale = 18)
	public BigDecimal getTradeScale() {
		return this.tradeScale;
	}

	public void setTradeScale(BigDecimal tradeScale) {
		this.tradeScale = tradeScale;
	}

	@Column(name = "REMAINDER_TRADE_SCALE", precision = 22, scale = 18)
	public BigDecimal getRemainderTradeScale() {
		return this.remainderTradeScale;
	}

	public void setRemainderTradeScale(BigDecimal remainderTradeScale) {
		this.remainderTradeScale = remainderTradeScale;
	}

	@Column(name = "DEVIATION_SCALE", precision = 22, scale = 18)
	public BigDecimal getDeviationScale() {
		return this.deviationScale;
	}

	public void setDeviationScale(BigDecimal deviationScale) {
		this.deviationScale = deviationScale;
	}

	@Column(name = "REDUCED_SCALE", precision = 22, scale = 18)
	public BigDecimal getReducedScale() {
		return this.reducedScale;
	}

	public void setReducedScale(BigDecimal reducedScale) {
		this.reducedScale = reducedScale;
	}

	@Column(name = "TRADE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getTradeAmount() {
		return this.tradeAmount;
	}

	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	@Column(name = "MANAGE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getManageAmount() {
		return this.manageAmount;
	}

	public void setManageAmount(BigDecimal manageAmount) {
		this.manageAmount = manageAmount;
	}

	@Column(name = "TRADE_TIMES", length = 22)
	public Integer getTradeTimes() {
		return this.tradeTimes;
	}

	public void setTradeTimes(Integer tradeTimes) {
		this.tradeTimes = tradeTimes;
	}

	@Column(name = "APPLY_STATUS", length = 50)
	public String getApplyStatus() {
		return this.applyStatus;
	}

	public void setApplyStatus(String applyStatus) {
		this.applyStatus = applyStatus;
	}

	@Column(name = "TRANSFER_NO", length = 50)
	public String getTransferNo() {
		return this.transferNo;
	}

	public void setTransferNo(String transferNo) {
		this.transferNo = transferNo;
	}

	@Column(name = "TRANSFER_START_DATE", length = 7)
	public Date getTransferStartDate() {
		return this.transferStartDate;
	}

	public void setTransferStartDate(Date transferStartDate) {
		this.transferStartDate = transferStartDate;
	}

	@Column(name = "TRANSFER_END_DATE", length = 7)
	public Date getTransferEndDate() {
		return this.transferEndDate;
	}

	public void setTransferEndDate(Date transferEndDate) {
		this.transferEndDate = transferEndDate;
	}

	@Column(name = "TRADE_VALUE", precision = 22, scale = 8)
	public BigDecimal getTradeValue() {
		return this.tradeValue;
	}

	public void setTradeValue(BigDecimal tradeValue) {
		this.tradeValue = tradeValue;
	}

	@Column(name = "TRADE_PRINCIPAL", precision = 22, scale = 8)
	public BigDecimal getTradePrincipal() {
		return this.tradePrincipal;
	}

	public void setTradePrincipal(BigDecimal tradePrincipal) {
		this.tradePrincipal = tradePrincipal;
	}

	@Column(name = "CANCEL_STATUS", length = 50)
	public String getCancelStatus() {
		return this.cancelStatus;
	}

	public void setCancelStatus(String cancelStatus) {
		this.cancelStatus = cancelStatus;
	}
	
	@Column(name = "TRANSFER_SCALE", precision = 22, scale = 18)
	public BigDecimal getTransferScale() {
		return this.transferScale;
	}

	public void setTransferScale(BigDecimal transferScale) {
		this.transferScale = transferScale;
	}
	
	@Column(name = "PROTOCOL_TYPE", length = 50)
	public String getProtocolType() {
		return this.protocolType;
	}

	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}
	
	@Column(name = "TRANSFER_SEAT_TERM", length = 22)
	public Integer getTransferSeatTerm() {
		return this.transferSeatTerm;
	}

	public void setTransferSeatTerm(Integer transferSeatTerm) {
		this.transferSeatTerm = transferSeatTerm;
	}
	
	@Column(name = "IS_RUN_AUTO_INVEST", length = 50)
	public String getIsRunAutoInvest() {
		return this.isRunAutoInvest;
	}

	public void setIsRunAutoInvest(String isRunAutoInvest) {
		this.isRunAutoInvest = isRunAutoInvest;
	}
	
	@Column(name = "STICKY_STATUS", length = 50)
	public String getStickyStatus() {
		return stickyStatus;
	}

	public void setStickyStatus(String stickyStatus) {
		this.stickyStatus = stickyStatus;
	}
	
	@Column(name = "STICKY_LEVEL")
	public String getStickyLevel() {
		return stickyLevel;
	}

	public void setStickyLevel(String stickyLevel) {
		this.stickyLevel = stickyLevel;
	}
}
