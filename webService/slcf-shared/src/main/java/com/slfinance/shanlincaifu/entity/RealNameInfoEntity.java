package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 实名认证 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_REAL_NAME_INFO")
public class RealNameInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 客户姓名
	 */
	private String custName;

	/**
	 * 证件号码
	 */
	private String credentialsCode;

	/**
	 * 交易状态（认证成功，认证失败）
	 */
	private String tradeStatus;

	/**
	 * 
	 */
	private String batchNo;



	@Column(name = "CUST_NAME", length = 50)
	public String getCustName() {
		return this.custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	@Column(name = "CREDENTIALS_CODE", length = 50)
	public String getCredentialsCode() {
		return this.credentialsCode;
	}

	public void setCredentialsCode(String credentialsCode) {
		this.credentialsCode = credentialsCode;
	}

	@Column(name = "TRADE_STATUS", length = 50)
	public String getTradeStatus() {
		return this.tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	@Column(name = "BATCH_NO", length = 50)
	public String getBatchNo() {
		return this.batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

}
