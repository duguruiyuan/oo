/** 
 * @(#)RestClient.java 1.0.0 2014年6月19日 下午2:25:50  
 *  
 * Copyright © 2014 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.common.io.BaseEncoding;

/**
 * esb 访问 客户端
 * 
 * @author 孟山
 * @version $Revision:1.0.0, $Date: 2014年6月19日 下午2:25:50 $
 */
public class RestClient extends RestTemplate {
	
	@Autowired
	private RestClientProperties clientProp;
	
	public RestClient() {
		super();
	}

	/**
	 * Create a new instance of the {@link RestTemplate} based on the given
	 * {@link ClientHttpRequestFactory}.
	 * 
	 * @param requestFactory
	 *            HTTP request factory to use
	 * @see org.springframework.http.client.SimpleClientHttpRequestFactory
	 * @see org.springframework.http.client.HttpComponentsClientHttpRequestFactory
	 */
	public RestClient(ClientHttpRequestFactory requestFactory) {
		super(requestFactory);
	}

	/**
	 * Create a new instance of the {@link RestTemplate} using the given list of
	 * {@link HttpMessageConverter} to use
	 * 
	 * @param messageConverters
	 *            the list of {@link HttpMessageConverter} to use
	 * @since 3.2.7
	 */
	public RestClient(List<HttpMessageConverter<?>> messageConverters) {
		super(messageConverters);
	}

	@Getter
	@Setter
	private String prefix;

	/**
	 * 重载 父类 填充请求url
	 */
	@Override
	public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor, Map<String, ?> urlVariables) throws RestClientException {
		return super.execute(getRequestURI(url), method, requestCallback, responseExtractor, urlVariables);
	}

	/**
	 * 重载 父类 填充请求url
	 */
	@Override
	public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor, Object... urlVariables) throws RestClientException {
		return super.execute(getRequestURI(url), method, requestCallback, responseExtractor, urlVariables);
	}

	public <T> T putForObject(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
		HttpEntityRequestCallback requestCallback = new HttpEntityRequestCallback(request, responseType);
		HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType, getMessageConverters());
		return execute(url, HttpMethod.PUT, requestCallback, responseExtractor, uriVariables);
	}

	public <T> T deleteForObject(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
		HttpEntityRequestCallback requestCallback = new HttpEntityRequestCallback(request, responseType);
		HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType, getMessageConverters());
		return execute(url, HttpMethod.DELETE, requestCallback, responseExtractor, uriVariables);
	}

	/**
	 * 补充端口号 url 当出现://则不会添加 url前缀
	 * 
	 * @param servicePath
	 * @return
	 */
	public String getRequestURI(String servicePath) {
		if (servicePath != null) {
			if (servicePath.indexOf("://") == -1) {
				if (!servicePath.startsWith("/")) {
					servicePath = "/" + servicePath;
				}
				servicePath = prefix + servicePath;
			}
		}
		return servicePath;
	}
	
	/**
	 * Request callback implementation that writes the given object to the
	 * request stream.
	 */
	private class HttpEntityRequestCallback extends AcceptHeaderRequestCallback {

		private final HttpEntity<?> requestEntity;

		private HttpEntityRequestCallback(Object requestBody) {
			this(requestBody, null);
		}

		private HttpEntityRequestCallback(Object requestBody, Type responseType) {
			super(responseType);
			if (requestBody instanceof HttpEntity) {
				this.requestEntity = (HttpEntity<?>) requestBody;
			} else if (requestBody != null) {
				this.requestEntity = new HttpEntity<Object>(requestBody);
			} else {
				this.requestEntity = HttpEntity.EMPTY;
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public void doWithRequest(ClientHttpRequest httpRequest) throws IOException {
			super.doWithRequest(httpRequest);
			if (!requestEntity.hasBody()) {
				HttpHeaders httpHeaders = httpRequest.getHeaders();
				HttpHeaders requestHeaders = requestEntity.getHeaders();
				if (!requestHeaders.isEmpty()) {
					httpHeaders.putAll(requestHeaders);
				}
				if (httpHeaders.getContentLength() == -1) {
					httpHeaders.setContentLength(0L);
				}
			} else {
				Object requestBody = requestEntity.getBody();
				Class<?> requestType = requestBody.getClass();
				HttpHeaders requestHeaders = requestEntity.getHeaders();
				MediaType requestContentType = requestHeaders.getContentType();
				for (HttpMessageConverter<?> messageConverter : getMessageConverters()) {
					if (messageConverter.canWrite(requestType, requestContentType)) {
						if (!requestHeaders.isEmpty()) {
							httpRequest.getHeaders().putAll(requestHeaders);
						}
						if (logger.isDebugEnabled()) {
							if (requestContentType != null) {
								logger.debug("Writing [" + requestBody + "] as \"" + requestContentType + "\" using [" + messageConverter + "]");
							} else {
								logger.debug("Writing [" + requestBody + "] using [" + messageConverter + "]");
							}

						}
						((HttpMessageConverter<Object>) messageConverter).write(requestBody, requestContentType, httpRequest);
						return;
					}
				}
				String message = "Could not write request: no suitable HttpMessageConverter found for request type [" + requestType.getName() + "]";
				if (requestContentType != null) {
					message += " and content type [" + requestContentType + "]";
				}
				throw new RestClientException(message);
			}
		}
	}

	/**
	 * Request callback implementation that prepares the request's accept
	 * headers.
	 */
	private class AcceptHeaderRequestCallback implements RequestCallback {

		private final Type responseType;

		private AcceptHeaderRequestCallback(Type responseType) {
			this.responseType = responseType;
		}

		public void doWithRequest(ClientHttpRequest request) throws IOException {
			if (responseType != null) {
				Class<?> responseClass = null;
				if (responseType instanceof Class) {
					responseClass = (Class<?>) responseType;
				}

				List<MediaType> allSupportedMediaTypes = new ArrayList<MediaType>();
				for (HttpMessageConverter<?> converter : getMessageConverters()) {
					if (responseClass != null) {
						if (converter.canRead(responseClass, null)) {
							allSupportedMediaTypes.addAll(getSupportedMediaTypes(converter));
						}
					} else if (converter instanceof GenericHttpMessageConverter) {

						GenericHttpMessageConverter<?> genericConverter = (GenericHttpMessageConverter<?>) converter;
						if (genericConverter.canRead(responseType, null, null)) {
							allSupportedMediaTypes.addAll(getSupportedMediaTypes(converter));
						}
					}

				}
				if (!allSupportedMediaTypes.isEmpty()) {
					MediaType.sortBySpecificity(allSupportedMediaTypes);
					if (logger.isDebugEnabled()) {
						logger.debug("Setting request Accept header to " + allSupportedMediaTypes);
					}
					request.getHeaders().setAccept(allSupportedMediaTypes);
				}
			}
		}

		private List<MediaType> getSupportedMediaTypes(HttpMessageConverter<?> messageConverter) {
			List<MediaType> supportedMediaTypes = messageConverter.getSupportedMediaTypes();
			List<MediaType> result = new ArrayList<MediaType>(supportedMediaTypes.size());
			for (MediaType supportedMediaType : supportedMediaTypes) {
				if (supportedMediaType.getCharSet() != null) {
					supportedMediaType = new MediaType(supportedMediaType.getType(), supportedMediaType.getSubtype());
				}
				result.add(supportedMediaType);
			}
			return result;
		}
	}

	
	@Override
	protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
		ClientHttpRequest clientHttpRequest = super.createRequest(url, method);
		String service = url.getPath().substring(url.getPath().lastIndexOf("/") + 1, url.getPath().length());
//		String appSource = clientConfig.getClient().getAppSource();
		long serviceTime = System.currentTimeMillis();
		clientHttpRequest.getHeaders().add("service", service);
		clientHttpRequest.getHeaders().add("serviceTime", Long.toString(serviceTime));
//		clientHttpRequest.getHeaders().add("appSource", appSource);
		
//		StringBuilder builder = new StringBuilder();
//		String hashString = builder.append(service).append(serviceTime)
//				.append(appSource)
//				.append(clientConfig.getClient().getMd5Seeds()).toString();
//		clientHttpRequest.getHeaders().add("sign", 
//				Hashing.md5().hashString(hashString , Charsets.UTF_8).toString());
		
		//Basic Authorization
		String authorization = clientProp.getFoundtionClient().getUser() + ":" + clientProp.getFoundtionClient().getPwd();
		clientHttpRequest.getHeaders().add(HttpHeaders.AUTHORIZATION,"Basic "+BaseEncoding.base64().encode(authorization.getBytes()));
		return clientHttpRequest;
	}

}
