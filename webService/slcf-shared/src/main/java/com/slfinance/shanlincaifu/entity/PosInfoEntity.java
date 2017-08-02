package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * POS信息表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_POS_INFO")
public class PosInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	private String tradeFlowId;

	/**
	 * 
	 */
	private String posNo;

	/**
	 * 
	 */
	private String referenceNo;

	/**
	 * 
	 */
	private String tradeStatus;



	@Column(name = "TRADE_FLOW_ID", length = 50)
	public String getTradeFlowId() {
		return this.tradeFlowId;
	}

	public void setTradeFlowId(String tradeFlowId) {
		this.tradeFlowId = tradeFlowId;
	}

	@Column(name = "POS_NO", length = 50)
	public String getPosNo() {
		return this.posNo;
	}

	public void setPosNo(String posNo) {
		this.posNo = posNo;
	}

	@Column(name = "REFERENCE_NO", length = 50)
	public String getReferenceNo() {
		return this.referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	@Column(name = "TRADE_STATUS", length = 50)
	public String getTradeStatus() {
		return this.tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

}
