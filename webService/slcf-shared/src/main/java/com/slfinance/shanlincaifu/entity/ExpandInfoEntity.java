package com.slfinance.shanlincaifu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BAO_T_EXPAND_INFO")
public class ExpandInfoEntity  extends BaseEntity {

	private static final long serialVersionUID = -3619781754219698234L;
	private String relateTableIdentification;
	private String relatePrimary;
	private String innerTradeCode;
	private String tradeCode;
	private String thirdPartyType;
	private String interfaceType;
	private String merchantCode;
	private String requestTime;
	private String execStatus;
	private int alreadyNotifyTimes;
	private String meId;
	private String meVersion;
	private String appSource;
	private String responseCode;
	private String responseMessage;
	private String callBack;
	

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

	@Column(name = "INNER_TRADE_CODE", length = 150)
	public String getInnerTradeCode() {
		return innerTradeCode;
	}

	public void setInnerTradeCode(String innerTradeCode) {
		this.innerTradeCode = innerTradeCode;
	}

	@Column(name = "TRADE_CODE", length = 150)
	public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
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

	@Column(name = "MERCHANT_CODE", length = 150)
	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	@Column(name = "REQUEST_TIME", length = 150)
	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	
	@Column(name = "EXEC_STATUS", length = 150)
	public String getExecStatus() {
		return execStatus;
	}

	public void setExecStatus(String execStatus) {
		this.execStatus = execStatus;
	}

	@Column(name = "ALREADY_NOTIFY_TIMES")
	public int getAlreadyNotifyTimes() {
		return alreadyNotifyTimes;
	}

	public void setAlreadyNotifyTimes(int alreadyNotifyTimes) {
		this.alreadyNotifyTimes = alreadyNotifyTimes;
	}

	@Column(name = "ME_ID", length = 50)
	public String getMeId() {
		return meId;
	}
	public void setMeId(String meId) {
		this.meId = meId;
	}
	
	@Column(name = "ME_VERSION", length = 50)
	public String getMeVersion() {
		return meVersion;
	}
	public void setMeVersion(String meVersion) {
		this.meVersion = meVersion;
	}
	
	@Column(name = "APP_SOURCE", length = 50)
	public String getAppSource() {
		return appSource;
	}
	public void setAppSource(String appSource) {
		this.appSource = appSource;
	}

	@Column(name = "RESPONSE_CODE", length = 50)
	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	@Column(name = "RESPONSE_MESSAGE", length = 500)
	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	@Column(name = "CALL_BACK", length = 500)
	public String getCallBack() {
		return callBack;
	}

	public void setCallBack(String callBack) {
		this.callBack = callBack;
	} 
}
