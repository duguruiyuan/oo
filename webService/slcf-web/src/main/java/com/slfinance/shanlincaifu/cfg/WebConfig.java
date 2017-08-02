/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.slfinance.shanlincaifu.cfg;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.slfinance.shanlincaifu.interceptor.SLInterceptor;
import com.slfinance.shanlincaifu.utils.InjectBeanSelfProcessor;

@Configuration
public class WebConfig {

	@Autowired
	AppConfig appConfig;
	
	@Value("${web.slb.signauth.exclude.paths}")
	private String excludePathPatterns;
	
	@Bean
	public WebMvcConfigurerAdapter mvcConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				System.out.println(excludePathPatterns);
				registry.addInterceptor(new SLInterceptor(appConfig))
				.addPathPatterns("/**").excludePathPatterns(StringUtils.isEmpty(excludePathPatterns) ? new String[]{"/account/returnUrl", "/account/recharge", "/job/**","/withHolding/recivedNotify"}: excludePathPatterns.split(","));
			}
		};
	}
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer(){
	    return new MyCustomizer();
	}
	
	@Bean
	public BeanPostProcessor injectBeanSelfProcessor() {
		return new InjectBeanSelfProcessor();
	}
	
	private static class MyCustomizer implements EmbeddedServletContainerCustomizer {

	    @Override
	    public void customize(ConfigurableEmbeddedServletContainer container) {
	        container.addErrorPages(new ErrorPage(Throwable.class, "/WEB-INF/views/errors/500.jsp"));
	        container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/WEB-INF/views/errors/404.jsp"));
	        container.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/WEB-INF/views/errors/500.jsp"));
	    }
	}

}