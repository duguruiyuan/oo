package com.slfinance.shanlincaifu.entity;
// default package

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.GenericGenerator;

/**
 * BaoTThridPartyPayRequest entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_THRID_PARTY_PAY_REQUEST")
public class ThridPartyPayRequestEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7370086654619886571L;
	private String requestUrl;
	private String requestMethod;
	private String requestHeaders;
	private String requestBatchNumber;
	private String responseStatusCode;
	private String responseStatusText;
	private String responseHeaders;
	private String requestTime;
	private String responseTime;
	private String exception;
	private String requestBody;
	private String responseBody;
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(length = 50)
	@Getter
	@Setter
	private String id;

	/** 创建时间 */
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date createDate;

	/** 备注 */
	@Column(length = 300)
	@Getter
	@Setter
	private String memo;

	/** 更新时间 */
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date lastUpdateDate = new Date();
	
	/** 版本 */
	@Version
	@Getter
	@Setter
	private Integer version = 0;
	
	/**
	 * @author HuangXiaodong 2015-04-17
	 * 设置记录基础信息
	 * 
	 * @param custId
	 *            操作人
	 * @param isInsert
	 *            是否插入
	 */
	public void setBasicModelProperty(String custId, boolean isInsert) {
		if (isInsert) {
			this.setCreateDate(new Date());
		}
		this.setLastUpdateDate(new Date());
	}

	
	// Constructors

	/** default constructor */
	public ThridPartyPayRequestEntity() {
	}

	/** minimal constructor */
	public ThridPartyPayRequestEntity(String id, Timestamp createDate) {
		this.id = id;
		this.createDate = createDate;
	}
	
	public ThridPartyPayRequestEntity(String requestURL, String requestMethod, String requestHeaders, String requestBatchNumber, String requestBody, String responseStatusCode, String responseStatusText, String responseHeanders, String responseBody, String requestTime, String responseTime, String exception) {
		this.requestUrl = requestURL;
		this.requestMethod = requestMethod;
		this.requestHeaders = requestHeaders;
		this.requestBatchNumber = requestBatchNumber;
		this.requestBody = requestBody;
		this.responseStatusCode = responseStatusCode;
		this.responseStatusText = responseStatusText;
		this.responseHeaders = responseHeanders;
		this.responseBody = responseBody;
		this.requestTime = requestTime;
		this.responseTime = responseTime;
		this.exception = exception;
	}

	/** full constructor */
	public ThridPartyPayRequestEntity(String id, String requestUrl, String requestMethod, String requestHeaders, String requestBatchNumber, String requestBody, String responseStatusCode, String responseStatusText, String responseHeaders, String responseBody, String requestTime, String responseTime, String exception, Timestamp createDate, Timestamp lastUpdateDate, String memo) {
		this.id = id;
		this.requestUrl = requestUrl;
		this.requestMethod = requestMethod;
		this.requestHeaders = requestHeaders;
		this.requestBatchNumber = requestBatchNumber;
		this.requestBody = requestBody;
		this.responseStatusCode = responseStatusCode;
		this.responseStatusText = responseStatusText;
		this.responseHeaders = responseHeaders;
		this.responseBody = responseBody;
		this.requestTime = requestTime;
		this.responseTime = responseTime;
		this.exception = exception;
		this.createDate = createDate;
		this.lastUpdateDate = lastUpdateDate;
		this.memo = memo;
	}

	@Column(name = "REQUEST_URL", length = 1000)
	public String getRequestUrl() {
		return this.requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	@Column(name = "REQUEST_METHOD", length = 10)
	public String getRequestMethod() {
		return this.requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	@Column(name = "REQUEST_HEADERS", length = 3000)
	public String getRequestHeaders() {
		return this.requestHeaders;
	}

	public void setRequestHeaders(String requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	@Column(name = "REQUEST_BATCH_NUMBER", length = 100)
	public String getRequestBatchNumber() {
		return this.requestBatchNumber;
	}

	public void setRequestBatchNumber(String requestBatchNumber) {
		this.requestBatchNumber = requestBatchNumber;
	}

	@Lob
	@Column(name = "REQUEST_BODY")
	public String getRequestBody() {
		return this.requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	@Column(name = "RESPONSE_STATUS_CODE", length = 50)
	public String getResponseStatusCode() {
		return this.responseStatusCode;
	}

	public void setResponseStatusCode(String responseStatusCode) {
		this.responseStatusCode = responseStatusCode;
	}

	@Column(name = "RESPONSE_STATUS_TEXT", length = 200)
	public String getResponseStatusText() {
		return this.responseStatusText;
	}

	public void setResponseStatusText(String responseStatusText) {
		this.responseStatusText = responseStatusText;
	}

	@Column(name = "RESPONSE_HEADERS", length = 3000)
	public String getResponseHeaders() {
		return this.responseHeaders;
	}

	public void setResponseHeaders(String responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	@Lob
	@Column(name = "RESPONSE_BODY")
	public String getResponseBody() {
		return this.responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	@Column(name = "REQUEST_TIME")
	public String getRequestTime() {
		return this.requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	@Column(name = "RESPONSE_TIME")
	public String getResponseTime() {
		return this.responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	@Column(name = "EXCEPTION", length = 500)
	public String getException() {
		return this.exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

}