package com.slfinance.shanlincaifu.cfg;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.rest.SpringBootRepositoryRestMvcConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.slfinance.shanlincaifu.service.ThirdPartyPayRestClientService;
import com.slfinance.shanlincaifu.utils.RestClient;
import com.slfinance.shanlincaifu.utils.RestClientProperties;

@Configuration
public class RestClientConfig extends SpringBootRepositoryRestMvcConfiguration{
	@Autowired
	RestClientProperties clientProp;
	
	/**
	 * Foundtion
	 * @return
	 */
	@Bean
	public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
		SimpleClientHttpRequestFactory simpleClientHttpRequestFactory  = new SimpleClientHttpRequestFactory();
		simpleClientHttpRequestFactory.setReadTimeout(clientProp.getFoundtionClient().getReadTimeout());
		simpleClientHttpRequestFactory.setConnectTimeout(clientProp.getFoundtionClient().getConnectTimeout());
		return simpleClientHttpRequestFactory;
		
	}
	@Bean
	public RestClient restClient() {
		RestClient restClient = new RestClient();
		restClient.setRequestFactory(simpleClientHttpRequestFactory());
		return restClient;
	}
	
	/**
	 * TPP
	 * @return
	 */
	@Bean
	public ClientHttpRequestFactory simpleClientHttpRequestFactory2() {
		SimpleClientHttpRequestFactory simpleClientHttpRequestFactory  = new SimpleClientHttpRequestFactory();
		simpleClientHttpRequestFactory.setReadTimeout(clientProp.getTppClient().getReadTimeout());
		simpleClientHttpRequestFactory.setConnectTimeout(clientProp.getTppClient().getConnectTimeout());
		return simpleClientHttpRequestFactory;
		
	}
	@Bean(name = "thirdPartyPayRestClientService")
	public ThirdPartyPayRestClientService thirdPartyPayRestClientService() {
		ThirdPartyPayRestClientService thirdPartyPayRestClientService = new ThirdPartyPayRestClientService();
		thirdPartyPayRestClientService.setRequestFactory(simpleClientHttpRequestFactory());
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		configureMessageConverters(messageConverters);
		thirdPartyPayRestClientService.setMessageConverters(messageConverters);
		return thirdPartyPayRestClientService;
	}
	
	@Override
	public void configureMessageConverters(
	        List<HttpMessageConverter<?>> messageConverters) {
	    MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
	    jsonMessageConverter.setSupportedMediaTypes(MediaType
	            .parseMediaTypes("application/json,application/*+json"));
	    StringHttpMessageConverter stringMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
	    stringMessageConverter.setSupportedMediaTypes(MediaType
	            .parseMediaTypes("application/xml,text/xml,text/plain,text/html"));
	    messageConverters.add(stringMessageConverter);
	    messageConverters.add(jsonMessageConverter);
	}
}
