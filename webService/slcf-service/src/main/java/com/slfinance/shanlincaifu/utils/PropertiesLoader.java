package com.slfinance.shanlincaifu.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Properties文件载入工具类. 可载入多个properties文件, 相同的属性在最后载入的文件中的值将会覆盖之前的值.
 * 
 */
@Slf4j
public class PropertiesLoader{

	private static ResourceLoader resourceLoader = new DefaultResourceLoader();	
	private static final String PROPERTIES_RESOURCE_APPLICATION = "application.properties";
	private static final Properties applicationProperties = new Properties();
	
	static {
		try {
			ClassLoader cl = PropertiesLoader.class.getClassLoader();
			URL url = (cl != null ? cl.getResource(PROPERTIES_RESOURCE_APPLICATION) :
					ClassLoader.getSystemResource(PROPERTIES_RESOURCE_APPLICATION));
			if (url != null) {
				InputStream is = url.openStream();
				try {
					applicationProperties.load(is);
				}
				finally {
					is.close();
				}
			}
		}
		catch (IOException ex) {
			if (log.isInfoEnabled()) {
				log.info("Could not load 'spring.properties' file from local classpath: " + ex);
			}
		}
	}

	public PropertiesLoader() {
		
	}

	/**
	 * 载入多个文件, 文件路径使用Spring Resource格式.
	 */
	public static Properties loadProperties(String... resourcesPaths) {
		Properties props = new Properties();

		for (String location : resourcesPaths) {

			log.debug("Loading properties file from:" + location);

			Resource resource = resourceLoader.getResource(location);
			try (InputStream is = resource.getInputStream()) {
				props.load(is);
			} catch (IOException ex) {
				log.info("Could not load properties from path:" + location
						+ ", " + ex.getMessage());
			}
		}
		return props;
	}
		
	/**
	 * eg. 
	 *
	 * @param name
	 * @return
	 */
	public static String getContextProperty(String name) {
		try
		{
			String properties = "channelno" + "." + "invitecode" + ".";
			for(int i = 1; i < 10; i ++ ) {
				String value = applicationProperties.getProperty(properties + Integer.valueOf(i));
				if(StringUtils.isEmpty(value)) {
					break;
				}
				if(value.contains("|")) {
					String[] vals = value.split("\\|");
					if(vals != null && vals.length == 2) {
						if(name.equals(vals[0])) {
							if(!StringUtils.isEmpty(vals[1]) && vals[1].contains(",")) {
								String[] codes = vals[1].split("\\,");
								if(codes != null && codes.length > 0) {
									int index=(int)(Math.random()*codes.length);
									return codes[index].trim();
								}
							}
							else if(!StringUtils.isEmpty(vals[1])) {
								return vals[1].trim();
							}
							else {
								break;
							}
						}
					}
				}
			}
		}catch(Exception e) {
			log.warn("Could not getContextProperty." + e.getMessage());
		}
    	
    	return "";
    }
}
