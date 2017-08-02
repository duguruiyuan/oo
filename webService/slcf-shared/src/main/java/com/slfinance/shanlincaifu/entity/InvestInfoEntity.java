package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * BAO投资信息表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_INVEST_INFO")
public class InvestInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 产品信息表主键ID
	 */
	private String productId;

	/**
	 * 客户信息主键ID
	 */
	private String custId;

	/**
	 * 投资金额
	 */
	private BigDecimal investAmount;

	/**
	 * 投资状态
	 */
	private String investStatus;

	/**
	 * 加入、购买
	 */
	private String investMode;

	/**
	 * 投资时间 yyyyMMdd
	 */
	private String investDate;

	/**
	 * 到期时间
	 */
	private String expireDate;

	/**
	 * 期数
	 */
	private String currTerm;

	/**
	 * 
	 */
	private String projectId;

	/**
	 * 
	 */
	private String wealthId;
	
	/**
	 * 客户经理id
	 */
	private String custManagerId;
	
	/**
	 * 接口ID
	 */
	private String loanId;
	
	/**
	 * 起息时间
	 */
	private String effectDate;

	/**
	 * 转让申请ID
	 */
	private String transferApplyId;
	
	/**
	 * 投资使用的红包金额、体验金待收益、加息劵待收益
	 */
	private BigDecimal investRedPacket;
	
	/**
	 * 红包类型
	 */
	private String redPacketType;
	
	/**
	 * 用户使用的红包ID
	 */
	private String custActivityId;
	
	private String groupBatchNo;
	
	@Column(name = "CUST_ACTIVITY_ID", length = 50)
	public String getCustActivityId() {
		return custActivityId;
	}

	public void setCustActivityId(String custActivityId) {
		this.custActivityId = custActivityId;
	}

	@Column(name = "RED_PACKET_TYPE", length = 50)
	public String getRedPacketType() {
		return redPacketType;
	}

	public void setRedPacketType(String redPacketType) {
		this.redPacketType = redPacketType;
	}

	@Column(name = "INVEST_RED_PACKET", precision = 22, scale = 8)
	public BigDecimal getInvestRedPacket() {
		return investRedPacket;
	}

	public void setInvestRedPacket(BigDecimal investRedPacket) {
		this.investRedPacket = investRedPacket;
	}

	@Column(name = "PRODUCT_ID", length = 50)
	public String getProductId() {
		return this.productId;
	}

	@Column(name = "CUST_MANAGER_ID", length = 50)
	public String getCustManagerId() {
		return custManagerId;
	}

	public void setCustManagerId(String custManagerId) {
		this.custManagerId = custManagerId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "INVEST_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getInvestAmount() {
		return this.investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	@Column(name = "INVEST_STATUS", length = 50)
	public String getInvestStatus() {
		return this.investStatus;
	}

	public void setInvestStatus(String investStatus) {
		this.investStatus = investStatus;
	}

	@Column(name = "INVEST_MODE", length = 200)
	public String getInvestMode() {
		return this.investMode;
	}

	public void setInvestMode(String investMode) {
		this.investMode = investMode;
	}

	@Column(name = "INVEST_DATE", length = 8)
	public String getInvestDate() {
		return this.investDate;
	}

	public void setInvestDate(String investDate) {
		this.investDate = investDate;
	}

	@Column(name = "EXPIRE_DATE", length = 8)
	public String getExpireDate() {
		return this.expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}

	@Column(name = "CURR_TERM", length = 50)
	public String getCurrTerm() {
		return this.currTerm;
	}

	public void setCurrTerm(String currTerm) {
		this.currTerm = currTerm;
	}

	@Column(name = "PROJECT_ID", length = 50)
	public String getProjectId() {
		return this.projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
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
	
	@Column(name = "EFFECT_DATE", length = 8)
	public String getEffectDate() {
		return this.effectDate;
	}

	public void setEffectDate(String effectDate) {
		this.effectDate = effectDate;
	}

	@Column(name = "TRANSFER_APPLY_ID", length = 50)
	public String getTransferApplyId() {
		return this.transferApplyId;
	}

	public void setTransferApplyId(String transferApplyId) {
		this.transferApplyId = transferApplyId;
	}
	
	@Column(name = "GROUP_BATCH_NO", length = 50)
	public String getGroupBatchNo() {
		return groupBatchNo;
	}
	
	public void setGroupBatchNo(String groupBatchNo) {
		this.groupBatchNo = groupBatchNo;
	}
}
