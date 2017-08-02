/** 
 * @(#)PropertiesUtil.java 1.0.0 2015年5月9日 上午10:12:04  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**   
 * 
 *  
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年5月9日 上午10:12:04 $ 
 */
public class PropertiesUtil {

	private static Map<String,Properties> propMap=new HashMap<String, Properties>();
	
	private static String propPath="/memcache.properties";
	
//	public static void main(String[] args) {
//		System.out.println(getProperty("autoproxy_sleep_time","/a.properties"));
//		System.out.println(getProperty("autoproxy_sleep_time","/a.properties"));
//		System.out.println(getProperty("autoproxy_sleep_time"));
//		System.out.println(getProperty("autoproxy_sleep_time"));
//	}
	
	/**
	 * 2013-11-11 上午10:59:00 by HuYaHui
	 * @param propertyName
	 * 			属性名(键)
	 * @param propertyFileName
	 * 			属性文件名如(/application.properties)
	 * @return
	 * 			获得对应的值
	 */
	public static String getProperty(String propertyName,String propertyFileName) {
		String properValue = "";
		try {
			if(propMap.get(propertyFileName)==null){
				Properties ppt = System.getProperties();
				ppt.load(PropertiesUtil.class.getResourceAsStream(propertyFileName));
				propMap.put(propertyFileName, ppt);
				properValue = ppt.getProperty(propertyName);
			}else{
				properValue = propMap.get(propertyFileName).getProperty(propertyName);
			}
		} catch (FileNotFoundException fe) {
			System.out.println("文件不正确");
		} catch (IOException e) {
			System.out.println("读取错误");
		}
		return properValue;
	}	

	/**
	 * 读取配置文件，保存属性内容
	 * @param propertyName		属性名(键)
	 * @return					获得对应的值
	 */
	public static String getProperty(String propertyName) {
		String properValue = "";
		try {
			if(propMap.get(propPath)==null){
				Properties ppt = System.getProperties();
				ppt.load(PropertiesUtil.class.getResourceAsStream(propPath));
				propMap.put(propPath, ppt);
				properValue = ppt.getProperty(propertyName);
			}else{
				properValue = propMap.get(propPath).getProperty(propertyName);
			}
		} catch (FileNotFoundException fe) {
			System.out.println("文件不正确");
		} catch (IOException e) {
			System.out.println("读取错误");
		}
		return properValue;
	}

}
