package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * 客户业务数据历史信息表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_CUST_BUSINESS_HISTORY")
public class CustBusinessHistoryEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 记录日期
	 */
	private String recordDate;

	/**
	 * 来源
	 */
	private String appSource;

	/**
	 * 注册人数
	 */
	private Integer registerCount;

	/**
	 * 实名认证人数
	 */
	private Integer realnameCount;

	/**
	 * 充值次数
	 */
	private Integer rechargeCount;

	/**
	 * 充值成功次数
	 */
	private Integer rechargeSuccessCount;

	/**
	 * 充值总额
	 */
	private BigDecimal rechargeSummary;

	/**
	 * 提现次数
	 */
	private Integer withdrawCount;

	/**
	 * 提现成功次数
	 */
	private Integer withdrawSuccessCount;

	/**
	 * 提现总额
	 */
	private BigDecimal withdrawSummary;

	/**
	 * 投资成功次数
	 */
	private BigDecimal investCount;

	/**
	 * 投资总额
	 */
	private BigDecimal investSummary;



	@Column(name = "RECORD_DATE", length = 8)
	public String getRecordDate() {
		return this.recordDate;
	}

	public void setRecordDate(String recordDate) {
		this.recordDate = recordDate;
	}

	@Column(name = "APP_SOURCE", length = 50)
	public String getAppSource() {
		return this.appSource;
	}

	public void setAppSource(String appSource) {
		this.appSource = appSource;
	}

	@Column(name = "REGISTER_COUNT", length = 22)
	public Integer getRegisterCount() {
		return this.registerCount;
	}

	public void setRegisterCount(Integer registerCount) {
		this.registerCount = registerCount;
	}

	@Column(name = "REALNAME_COUNT", length = 22)
	public Integer getRealnameCount() {
		return this.realnameCount;
	}

	public void setRealnameCount(Integer realnameCount) {
		this.realnameCount = realnameCount;
	}

	@Column(name = "RECHARGE_COUNT", length = 22)
	public Integer getRechargeCount() {
		return this.rechargeCount;
	}

	public void setRechargeCount(Integer rechargeCount) {
		this.rechargeCount = rechargeCount;
	}

	@Column(name = "RECHARGE_SUCCESS_COUNT", length = 22)
	public Integer getRechargeSuccessCount() {
		return this.rechargeSuccessCount;
	}

	public void setRechargeSuccessCount(Integer rechargeSuccessCount) {
		this.rechargeSuccessCount = rechargeSuccessCount;
	}

	@Column(name = "RECHARGE_SUMMARY", precision = 22, scale = 8)
	public BigDecimal getRechargeSummary() {
		return this.rechargeSummary;
	}

	public void setRechargeSummary(BigDecimal rechargeSummary) {
		this.rechargeSummary = rechargeSummary;
	}

	@Column(name = "WITHDRAW_COUNT", length = 22)
	public Integer getWithdrawCount() {
		return this.withdrawCount;
	}

	public void setWithdrawCount(Integer withdrawCount) {
		this.withdrawCount = withdrawCount;
	}

	@Column(name = "WITHDRAW_SUCCESS_COUNT", length = 22)
	public Integer getWithdrawSuccessCount() {
		return this.withdrawSuccessCount;
	}

	public void setWithdrawSuccessCount(Integer withdrawSuccessCount) {
		this.withdrawSuccessCount = withdrawSuccessCount;
	}

	@Column(name = "WITHDRAW_SUMMARY", precision = 22, scale = 8)
	public BigDecimal getWithdrawSummary() {
		return this.withdrawSummary;
	}

	public void setWithdrawSummary(BigDecimal withdrawSummary) {
		this.withdrawSummary = withdrawSummary;
	}

	@Column(name = "INVEST_COUNT", precision = 22, scale = 8)
	public BigDecimal getInvestCount() {
		return this.investCount;
	}

	public void setInvestCount(BigDecimal investCount) {
		this.investCount = investCount;
	}

	@Column(name = "INVEST_SUMMARY", precision = 22, scale = 8)
	public BigDecimal getInvestSummary() {
		return this.investSummary;
	}

	public void setInvestSummary(BigDecimal investSummary) {
		this.investSummary = investSummary;
	}

}
