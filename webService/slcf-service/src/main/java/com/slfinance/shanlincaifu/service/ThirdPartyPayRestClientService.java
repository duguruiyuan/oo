/** 
 * @(#)ThirdPartyPayRestClientService.java 1.0.0 2014年12月12日 下午8:00:52  
 *  
 * Copyright © 2014 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.service;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;

import com.slfinance.shanlincaifu.entity.ThridPartyPayRequestEntity;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SlRestClient;
import com.slfinance.thirdpp.util.ShareUtil;

/**
 * rest调用
 * 
 * @author 孟山
 * @version $Revision:1.0.0, $Date: 2014年12月12日 下午8:00:52 $
 */
public class ThirdPartyPayRestClientService extends SlRestClient {

	public ThirdPartyPayRestClientService() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ThirdPartyPayRestClientService(ClientHttpRequestFactory requestFactory) {
		super(requestFactory);
		// TODO Auto-generated constructor stub
	}

	public ThirdPartyPayRestClientService(List<HttpMessageConverter<?>> messageConverters) {
		super(messageConverters);
		// TODO Auto-generated constructor stub
	}

	@Autowired
	private ThirdPartyPayRequestService thirdPartyPayRequestService;

	public <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {

		HttpEntityRequestCallback requestCallback = new HttpEntityRequestCallback(request, responseType);
		HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType, getMessageConverters());
		return execute(getRequestURI(url), HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	public <T> T postForObject(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {

		HttpEntityRequestCallback requestCallback = new HttpEntityRequestCallback(request, responseType);
		HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType, getMessageConverters());
		return execute(getRequestURI(url), HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	public <T> T postForObject(URI url, Object request, Class<T> responseType) throws RestClientException {
		HttpEntityRequestCallback requestCallback = new HttpEntityRequestCallback(request, responseType);
		HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType, getMessageConverters());
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor);
	}

	/**
	 * Execute the given method on the provided URI.
	 * <p>
	 * The {@link ClientHttpRequest} is processed using the
	 * {@link RequestCallback}; the response with the {@link ResponseExtractor}.
	 * 
	 * 
	 * <strong> additional: 添加获取http client 请求和响应记录</strong
	 * 
	 * @param url
	 *            the fully-expanded URL to connect to
	 * @param method
	 *            the HTTP method to execute (GET, POST, etc.)
	 * @param requestCallback
	 *            object that prepares the request (can be {@code null})
	 * @param responseExtractor
	 *            object that extracts the return value from the response (can
	 *            be {@code null})
	 * @return an arbitrary object, as returned by the {@link ResponseExtractor}
	 */
	@Override
	protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) throws RestClientException {
		Assert.notNull(url, "'url' must not be null");
		Assert.notNull(method, "'method' must not be null");

		/**
		 * 初始化http client详情记录对象
		 */
		ThridPartyPayRequestEntity thirdPartyPayRequest = new ThridPartyPayRequestEntity();

		ClientHttpResponse response = null;
		try {
			ClientHttpRequest request = createRequest(url, method);
			if (requestCallback != null) {
				requestCallback.doWithRequest(request);
			}

			/**
			 * 设置http client详情对象请求信息
			 */
			if (requestCallback instanceof HttpEntityRequestCallback) {
				HttpEntityRequestCallback httpEntityRequestCallback = (HttpEntityRequestCallback) requestCallback;
				HttpEntity<?> httpEntity = httpEntityRequestCallback.getRequestEntity();
				thirdPartyPayRequest.setRequestUrl(url.toString());
				thirdPartyPayRequest.setRequestMethod(method.name());
				thirdPartyPayRequest.setRequestHeaders(request.getHeaders().toString());
				thirdPartyPayRequest.setRequestBody(httpEntity.hasBody() ? httpEntity.getBody().toString() : null);
				Matcher matcher = Pattern.compile("<batchCode>(.*?)</batchCode>").matcher(thirdPartyPayRequest.getRequestBody());
				if(matcher.find()){
					thirdPartyPayRequest.setRequestBatchNumber(matcher.group(1));
				}
			}

			thirdPartyPayRequest.setRequestTime(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
			response = request.execute();
			thirdPartyPayRequest.setResponseTime(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));

			/**
			 * 设置http client详情对象 响应状态和头信息
			 */
			thirdPartyPayRequest.setResponseStatusCode(String.valueOf(response.getStatusCode().value()));
			thirdPartyPayRequest.setResponseStatusText(response.getStatusText());
			thirdPartyPayRequest.setResponseHeaders(response.getHeaders().toString());

			if (!getErrorHandler().hasError(response)) {
				if (logger.isDebugEnabled()) {
					try {
						logger.debug(method.name() + " request for \"" + url + "\" resulted in " + response.getStatusCode() + " (" + response.getStatusText() + ")");
					} catch (IOException e) {
						// ignore
					}
				}
			} else {
				if (logger.isWarnEnabled()) {
					try {
						logger.warn(method.name() + " request for \"" + url + "\" resulted in " + response.getStatusCode() + " (" + response.getStatusText() + "); invoking error handler");
					} catch (IOException e) {
						// ignore
					}
				}
				getErrorHandler().handleError(response);
			}
			if (responseExtractor != null) {
				T t = responseExtractor.extractData(response);
				/**
				 * 设置http client详情对象 响应内容
				 */
				thirdPartyPayRequest.setResponseBody(t.toString());
				return t;
			} else {
				return null;
			}
		} catch (IOException ex) {
			/**
			 * 设置http client详情对象 异常状态和异常信息
			 */
			thirdPartyPayRequest.setResponseTime(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
			thirdPartyPayRequest.setResponseStatusCode(String.valueOf(HttpStatus.GATEWAY_TIMEOUT.value()));
			thirdPartyPayRequest.setResponseStatusText(HttpStatus.GATEWAY_TIMEOUT.getReasonPhrase());
			thirdPartyPayRequest.setException(ex.getMessage());
			throw new ResourceAccessException("I/O error on " + method.name() + " request for \"" + url + "\": " + ex.getMessage(), ex);
		} finally {
			if (response != null) {
				response.close();
			}
			if (HttpMethod.POST.compareTo(method) == 0) {
				try {
					thirdPartyPayRequest.setId(ShareUtil.getUniqueString());
					thirdPartyPayRequest.setVersion(0);
					thirdPartyPayRequest.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
					thirdPartyPayRequestService.saveThirdPartyPayRequest(thirdPartyPayRequest);
				} catch (Exception e) {
					logger.warn("third party pay request insert error.........................................", e);
				}

			}
		}
	}
}
