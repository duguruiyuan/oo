package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 用户预约投资表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_RESERVE_INVEST_INFO")
public class ReserveInvestInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 客户信息主键ID
	 */
	private String custId;

	/**
	 * 预约金额
	 */
	private BigDecimal reserveAmount;

	/**
	 * 已投预约金额
	 */
	private BigDecimal alreadyInvestAmount;

	/**
	 * 剩余预约金额
	 */
	private BigDecimal remainderAmount;

	/**
	 * 预约开始时间
	 */
	private Date reserveStartDate;

	/**
	 * 预约结束时间
	 */
	private Date reserveEndDate;

	/**
	 * 排队时间
	 */
	private Date reserveDate;

	/**
	 * 排队中、预约成功（仅剩余预约金额等于0时）、待投金额已撤销、待投金额超时退回
	 */
	private String reserveStatus;

	/**
	 * 可投期限，XX天-XX天,XX天-XX天
	 */
	private String canInvestTerm;

	/**
	 * 可投产品：XX贷，XX贷，多个用逗号隔开
	 */
	private String canInvestProduct;

	/**
	 * 批次号
	 */
	private String batchNo;

	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "RESERVE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getReserveAmount() {
		return this.reserveAmount;
	}

	public void setReserveAmount(BigDecimal reserveAmount) {
		this.reserveAmount = reserveAmount;
	}

	@Column(name = "ALREADY_INVEST_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getAlreadyInvestAmount() {
		return this.alreadyInvestAmount;
	}

	public void setAlreadyInvestAmount(BigDecimal alreadyInvestAmount) {
		this.alreadyInvestAmount = alreadyInvestAmount;
	}

	@Column(name = "REMAINDER_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getRemainderAmount() {
		return this.remainderAmount;
	}

	public void setRemainderAmount(BigDecimal remainderAmount) {
		this.remainderAmount = remainderAmount;
	}

	@Column(name = "RESERVE_START_DATE", length = 7)
	public Date getReserveStartDate() {
		return this.reserveStartDate;
	}

	public void setReserveStartDate(Date reserveStartDate) {
		this.reserveStartDate = reserveStartDate;
	}

	@Column(name = "RESERVE_END_DATE", length = 7)
	public Date getReserveEndDate() {
		return this.reserveEndDate;
	}

	public void setReserveEndDate(Date reserveEndDate) {
		this.reserveEndDate = reserveEndDate;
	}

	@Column(name = "RESERVE_DATE", length = 7)
	public Date getReserveDate() {
		return this.reserveDate;
	}

	public void setReserveDate(Date reserveDate) {
		this.reserveDate = reserveDate;
	}

	@Column(name = "RESERVE_STATUS", length = 50)
	public String getReserveStatus() {
		return this.reserveStatus;
	}

	public void setReserveStatus(String reserveStatus) {
		this.reserveStatus = reserveStatus;
	}

	@Column(name = "CAN_INVEST_TERM", length = 500)
	public String getCanInvestTerm() {
		return this.canInvestTerm;
	}

	public void setCanInvestTerm(String canInvestTerm) {
		this.canInvestTerm = canInvestTerm;
	}

	@Column(name = "CAN_INVEST_PRODUCT", length = 500)
	public String getCanInvestProduct() {
		return this.canInvestProduct;
	}

	public void setCanInvestProduct(String canInvestProduct) {
		this.canInvestProduct = canInvestProduct;
	}

	@Column(name = "BATCH_NO", length = 50)
	public String getBatchNo() {
		return this.batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
}
