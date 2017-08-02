package com.slfinance.shanlincaifu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "BAO_T_TRADE_LOG_INFO")
public class TradeLogInfoEntity extends BaseEntity {

	private static final long serialVersionUID = 5544328969151592581L;
	
	private String relateTableIdentification;
	private String relatePrimary;
	private String thirdPartyType;
	private String interfaceType;
	private String requestTime;
	private String tradeCode;
	private String innerTradeCode;
	private String requestMessage;
	private String responseMessage;

	@Column(name = "RELATE_TABLE_IDENTIFICATION", length = 150)
	public String getRelateTableIdentification() {
		return relateTableIdentification;
	}

	public void setRelateTableIdentification(String relateTableIdentification) {
		this.relateTableIdentification = relateTableIdentification;
	}

	@Column(name = "RELATE_PRIMARY", length = 50)
	public String getRelatePrimary() {
		return relatePrimary;
	}

	public void setRelatePrimary(String relatePrimary) {
		this.relatePrimary = relatePrimary;
	}

	@Column(name = "THIRD_PARTY_TYPE", length = 150)
	public String getThirdPartyType() {
		return thirdPartyType;
	}

	public void setThirdPartyType(String thirdPartyType) {
		this.thirdPartyType = thirdPartyType;
	}

	@Column(name = "INTERFACE_TYPE", length = 150)
	public String getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(String interfaceType) {
		this.interfaceType = interfaceType;
	}

	@Column(name = "REQUEST_TIME", length = 150)
	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	@Column(name = "TRADE_CODE", length = 150)
	public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	@Column(name = "INNER_TRADE_CODE", length = 150)
	public String getInnerTradeCode() {
		return innerTradeCode;
	}

	public void setInnerTradeCode(String innerTradeCode) {
		this.innerTradeCode = innerTradeCode;
	}

	@Lob
	@Column(name = "REQUEST_MESSAGE", length = 4000)
	public String getRequestMessage() {
		return requestMessage;
	}

	public void setRequestMessage(String requestMessage) {
		this.requestMessage = requestMessage;
	}

	@Lob
	@Column(name = "RESPONSE_MESSAGE", length = 4000)
	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

}
