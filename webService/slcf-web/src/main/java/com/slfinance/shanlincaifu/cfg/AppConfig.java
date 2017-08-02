package com.slfinance.shanlincaifu.cfg;

import lombok.Data;
import lombok.ToString;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 系统参数配置
 * 
 * @author larry
 * 
 */
@Data
@ToString
@Component
@ConfigurationProperties(prefix = "slb", ignoreUnknownFields = false)
public class AppConfig {

	private SignAuth signAuth;
	
	@Data
	@ToString
	public static class SignAuth {
		private boolean enabled;
		private String interceptorTime;
		private String appSource;
		private String seeds;
	}
}
