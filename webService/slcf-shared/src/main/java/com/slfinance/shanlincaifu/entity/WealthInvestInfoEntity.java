package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.math.BigDecimal;


/**
 * 原线下理财投资信息表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_WEALTH_INVEST_INFO")
public class WealthInvestInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 客户信息表主键ID
	 */
	private String customerId;

	/**
	 * 投资生效日期
	 */
	private Date investStartDate;

	/**
	 * 投资失效日期
	 */
	private Date investEndDate;

	/**
	 * 匹配金额(无效字段)
	 */
	private BigDecimal matchAmt;

	/**
	 * 投资金额
	 */
	private BigDecimal investAmt;

	/**
	 * 产品类型
	 */
	private String productType;

	/**
	 * 流程ID(投资状态)
02000002：通过
02000003：拒绝
02000004：撤销
02000012：作废 
	 */
	private String flowId;

	/**
	 * 协议编号
	 */
	private String protocolNo;

	/**
	 * 出借编号
	 */
	private String lendingNo;

	/**
	 * 数据状态
1：有效，9无效
	 */
	private String status;

	/**
	 * 
	 */
	private Date auditDate;



	@Column(name = "CUSTOMER_ID", length = 50)
	public String getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	@Column(name = "INVEST_START_DATE", length = 7)
	public Date getInvestStartDate() {
		return this.investStartDate;
	}

	public void setInvestStartDate(Date investStartDate) {
		this.investStartDate = investStartDate;
	}

	@Column(name = "INVEST_END_DATE", length = 7)
	public Date getInvestEndDate() {
		return this.investEndDate;
	}

	public void setInvestEndDate(Date investEndDate) {
		this.investEndDate = investEndDate;
	}

	@Column(name = "MATCH_AMT", precision = 22, scale = 7)
	public BigDecimal getMatchAmt() {
		return this.matchAmt;
	}

	public void setMatchAmt(BigDecimal matchAmt) {
		this.matchAmt = matchAmt;
	}

	@Column(name = "INVEST_AMT", precision = 22, scale = 7)
	public BigDecimal getInvestAmt() {
		return this.investAmt;
	}

	public void setInvestAmt(BigDecimal investAmt) {
		this.investAmt = investAmt;
	}

	@Column(name = "PRODUCT_TYPE", length = 50)
	public String getProductType() {
		return this.productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	@Column(name = "FLOW_ID", length = 8)
	public String getFlowId() {
		return this.flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	@Column(name = "PROTOCOL_NO", length = 30)
	public String getProtocolNo() {
		return this.protocolNo;
	}

	public void setProtocolNo(String protocolNo) {
		this.protocolNo = protocolNo;
	}

	@Column(name = "LENDING_NO", length = 30)
	public String getLendingNo() {
		return this.lendingNo;
	}

	public void setLendingNo(String lendingNo) {
		this.lendingNo = lendingNo;
	}

	@Column(name = "STATUS", length = 4)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "AUDIT_DATE", length = 7)
	public Date getAuditDate() {
		return this.auditDate;
	}

	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}

}
