package com.slfinance.shanlincaifu.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BaoTCommissionDetailInfo entity. @author MyEclipse Persistence Tools
 */
/**
 * @author Administrator
 *
 */
@Entity
@Table(name = "BAO_T_COMMISSION_DETAIL_INFO")
public class CommissionDetailInfoEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields
	private String commissionId;
	private String quiltCustId;
	private Double investAmount;
	private Double commissionAmount;
	private Double rewardAmount;
	private String tradeStatus;
	private String productId;
	private Double yearInvestAmount;
	private String investId;
	/**
	 * 关联类型(默认表明大写)
	 */
	private String relateType;

	/**
	 * 关联主键
	 */
	private String relatePrimary;


	// Constructors

	/** default constructor */
	public CommissionDetailInfoEntity() {
	}

	/** minimal constructor */
	public CommissionDetailInfoEntity(String id, Timestamp createDate) {
		this.id = id;
		this.createDate = createDate;
	}

	/** full constructor */
	public CommissionDetailInfoEntity(String id, String commissionId, String quiltCustId, Double investAmount, Double commissionAmount, Double rewardAmount, String tradeStatus, String recordStatus, String createUser, Timestamp createDate, String lastUpdateUser, Timestamp lastUpdateDate, String memo, String productId, Double yearInvestAmount) {
		this.id = id;
		this.commissionId = commissionId;
		this.quiltCustId = quiltCustId;
		this.investAmount = investAmount;
		this.commissionAmount = commissionAmount;
		this.rewardAmount = rewardAmount;
		this.tradeStatus = tradeStatus;
		this.recordStatus = recordStatus;
		this.createUser = createUser;
		this.createDate = createDate;
		this.lastUpdateUser = lastUpdateUser;
		this.lastUpdateDate = lastUpdateDate;
		this.memo = memo;
		this.productId = productId;
		this.yearInvestAmount = yearInvestAmount;
	}

	// Property accessors
	@Column(name = "COMMISSION_ID", length = 50)
	public String getCommissionId() {
		return this.commissionId;
	}

	public void setCommissionId(String commissionId) {
		this.commissionId = commissionId;
	}

	@Column(name = "QUILT_CUST_ID", length = 50)
	public String getQuiltCustId() {
		return this.quiltCustId;
	}

	public void setQuiltCustId(String quiltCustId) {
		this.quiltCustId = quiltCustId;
	}

	@Column(name = "INVEST_AMOUNT", precision = 22, scale = 8)
	public Double getInvestAmount() {
		return this.investAmount;
	}

	public void setInvestAmount(Double investAmount) {
		this.investAmount = investAmount;
	}

	@Column(name = "COMMISSION_AMOUNT", precision = 22, scale = 8)
	public Double getCommissionAmount() {
		return this.commissionAmount;
	}

	public void setCommissionAmount(Double commissionAmount) {
		this.commissionAmount = commissionAmount;
	}

	@Column(name = "REWARD_AMOUNT", precision = 22, scale = 8)
	public Double getRewardAmount() {
		return this.rewardAmount;
	}

	public void setRewardAmount(Double rewardAmount) {
		this.rewardAmount = rewardAmount;
	}

	@Column(name = "TRADE_STATUS", length = 50)
	public String getTradeStatus() {
		return this.tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	@Column(name = "PRODUCT_ID", length = 50)
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Column(name = "YEAR_INVEST_AMOUNT", precision = 22, scale = 8)
	public Double getYearInvestAmount() {
		return yearInvestAmount;
	}

	public void setYearInvestAmount(Double yearInvestAmount) {
		this.yearInvestAmount = yearInvestAmount;
	}

	@Column(name = "INVEST_ID", length = 50)
	public String getInvestId() {
		return investId;
	}

	public void setInvestId(String investId) {
		this.investId = investId;
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