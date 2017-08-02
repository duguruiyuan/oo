/** 
 * @(#)RequestVo.java 1.0.0 2014年12月12日 上午9:47:17  
 *  
 * Copyright © 2014 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.vo;

import java.util.Date;

import com.slfinance.shanlincaifu.entity.BaseEntity;

/**
 * 记录请求TPP的http client 的请求和响应 详情
 * 
 * @author 孟山
 * @version $Revision:1.0.0, $Date: 2014年12月12日 上午9:47:17 $
 */
public class ThirdPartyPayRequest extends BaseEntity {
	private static final long serialVersionUID = 707051992176246344L;
	/**
	 * 请求url
	 */
	private String requestURL;
	/**
	 * 请求方法 POST|GET|PUT|DELETE|HEAD|OPTIONS|PATCH|TRACE
	 */
	private String requestMethod;
	/**
	 * 请求头信息
	 */
	private String requestHeaders;
	/**
	 * 请求批次号
	 */
	private String requestBatchNumber;
	/**
	 * 请求数据
	 */
	private String requestBody;

	/**
	 * 响应HTTP CODE
	 */
	private int responseStatusCode;
	/**
	 * 响应状态对应的描述
	 */
	private String responseStatusText;
	/**
	 * 响应头信息
	 */
	private String responseHeanders;
	/**
	 * 响应内容
	 */
	private String responseBody;

	/**
	 * http请求时间
	 */
	private Date requestTime;
	/**
	 * http响应时间
	 */
	private Date responseTime;
	/**
	 * 如果抛出异常时的异常信息
	 */
	private String exception;

	public ThirdPartyPayRequest() {
	}

	public ThirdPartyPayRequest(String requestURL, String requestMethod, String requestHeaders, String requestBatchNumber, String requestBody, int responseStatusCode, String responseStatusText, String responseHeanders, String responseBody, Date requestTime, Date responseTime, String exception) {
		this.requestURL = requestURL;
		this.requestMethod = requestMethod;
		this.requestHeaders = requestHeaders;
		this.requestBatchNumber = requestBatchNumber;
		this.requestBody = requestBody;
		this.responseStatusCode = responseStatusCode;
		this.responseStatusText = responseStatusText;
		this.responseHeanders = responseHeanders;
		this.responseBody = responseBody;
		this.requestTime = requestTime;
		this.responseTime = responseTime;
		this.exception = exception;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public ThirdPartyPayRequest setRequestURL(String requestURL) {
		this.requestURL = requestURL;
		return this;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public ThirdPartyPayRequest setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
		return this;
	}

	public String getResponseHeanders() {
		return responseHeanders;
	}

	public String getRequestHeaders() {
		return requestHeaders;
	}

	public ThirdPartyPayRequest setRequestHeaders(String requestHeaders) {
		this.requestHeaders = requestHeaders;
		return this;
	}

	public ThirdPartyPayRequest setResponseHeanders(String responseHeanders) {
		this.responseHeanders = responseHeanders;
		return this;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public ThirdPartyPayRequest setRequestBody(String requestBody) {
		this.requestBody = requestBody;
		return this;
	}

	public int getResponseStatusCode() {
		return responseStatusCode;
	}

	public ThirdPartyPayRequest setResponseStatusCode(int responseStatusCode) {
		this.responseStatusCode = responseStatusCode;
		return this;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public ThirdPartyPayRequest setResponseBody(String responseBody) {
		this.responseBody = responseBody;
		return this;
	}

	public String getResponseStatusText() {
		return responseStatusText;
	}

	public ThirdPartyPayRequest setResponseStatusText(String responseStatusText) {
		this.responseStatusText = responseStatusText;
		return this;
	}

	public String getException() {
		return exception;
	}

	public ThirdPartyPayRequest setException(String exception) {
		this.exception = exception;
		return this;
	}

	public String getRequestBatchNumber() {
		return requestBatchNumber;
	}

	public ThirdPartyPayRequest setRequestBatchNumber(String requestBatchNumber) {
		this.requestBatchNumber = requestBatchNumber;
		return this;
	}

	public Date getRequestTime() {
		return requestTime;
	}

	public ThirdPartyPayRequest setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
		return this;
	}

	public Date getResponseTime() {
		return responseTime;
	}

	public ThirdPartyPayRequest setResponseTime(Date responseTime) {
		this.responseTime = responseTime;
		return this;
	}

	@Override
	public String toString() {
		return "ThirdPartyPayRequest [requestURL=" + requestURL + ", requestMethod=" + requestMethod + ", requestHeaders=" + requestHeaders + ", requestBatchNumber=" + requestBatchNumber + ", requestBody=" + requestBody + ", responseStatusCode=" + responseStatusCode + ", responseStatusText=" + responseStatusText + ", responseHeanders=" + responseHeanders + ", responseBody=" + responseBody + ", requestTime=" + requestTime + ", responseTime=" + responseTime + ", exception=" + exception + "]";
	}

}
