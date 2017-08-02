package com.slfinance.shanlincaifu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BAO_T_INTERFACE_DETAIL_INFO")
public class InterfaceDetailInfoEntity  extends BaseEntity {

	private static final long serialVersionUID = 7784594168234970305L;
	
	private String thirdPartyType;
	private String interfaceType;
	private String interfaceDesc;
	private String reqMode;
	private String reqUrl;
	private String merchantCode;
	private String privateKeyPath;
	private String publicKeyPath;
	private String certPath;
	private String secretKey;
	private String asyncNotifyUrl;
	private String syncRedirectUrl;
	private String interfaceStatus;
	private String isNotice;

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

	@Column(name = "INTERFACE_DESC", length = 150)
	public String getInterfaceDesc() {
		return interfaceDesc;
	}

	public void setInterfaceDesc(String interfaceDesc) {
		this.interfaceDesc = interfaceDesc;
	}

	@Column(name = "REQ_MODE", length = 150)
	public String getReqMode() {
		return reqMode;
	}

	public void setReqMode(String reqMode) {
		this.reqMode = reqMode;
	}

	@Column(name = "REQ_URL", length = 150)
	public String getReqUrl() {
		return reqUrl;
	}

	public void setReqUrl(String reqUrl) {
		this.reqUrl = reqUrl;
	}

	@Column(name = "MERCHANT_CODE", length = 150)
	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	@Column(name = "PRIVATE_KEY_PATH", length = 150)
	public String getPrivateKeyPath() {
		return privateKeyPath;
	}

	public void setPrivateKeyPath(String privateKeyPath) {
		this.privateKeyPath = privateKeyPath;
	}

	@Column(name = "PUBLIC_KEY_PATH", length = 150)
	public String getPublicKeyPath() {
		return publicKeyPath;
	}

	public void setPublicKeyPath(String publicKeyPath) {
		this.publicKeyPath = publicKeyPath;
	}

	@Column(name = "CERT_PATH", length = 150)
	public String getCertPath() {
		return certPath;
	}

	public void setCertPath(String certPath) {
		this.certPath = certPath;
	}

	@Column(name = "SECRET_KEY", length = 300)
	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	@Column(name = "ASYNC_NOTIFY_URL", length = 150)
	public String getAsyncNotifyUrl() {
		return asyncNotifyUrl;
	}

	public void setAsyncNotifyUrl(String asyncNotifyUrl) {
		this.asyncNotifyUrl = asyncNotifyUrl;
	}

	@Column(name = "SYNC_REDIRECT_URL", length = 150)
	public String getSyncRedirectUrl() {
		return syncRedirectUrl;
	}

	public void setSyncRedirectUrl(String syncRedirectUrl) {
		this.syncRedirectUrl = syncRedirectUrl;
	}

	@Column(name = "INTERFACE_STATUS", length = 150)
	public String getInterfaceStatus() {
		return interfaceStatus;
	}

	public void setInterfaceStatus(String interfaceStatus) {
		this.interfaceStatus = interfaceStatus;
	}

	@Column(name = "IS_NOTICE", length = 20)
	public String getIsNotice() {
		return isNotice;
	}

	public void setIsNotice(String isNotice) {
		this.isNotice = isNotice;
	}
}
