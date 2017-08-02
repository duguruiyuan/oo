package com.slfinance.shanlincaifu.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slfinance.exception.SLException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class CommonUtils {
	
	public static String emptyToString(Object value){
		if(StringUtils.isEmpty(value)){
			return "";
		}
		return value.toString();
	}
	
	/**
	 * 对象转化为浮点型 DATE：2014/07/04
	 * 
	 * @author zhoudl
	 * **/
	public static Double emptyToDouble(Object value) {
		if ((value == null) || "".equals(value)) {
			return Double.valueOf("0");
		}
		return Double.valueOf(value.toString());
	}
	
	/***
	 * 对象转化为BigDecimal
	 * zhoudl
	 * **/
	public static BigDecimal emptyToDecimal(Object value){
		if((value==null)||("".equals(value))){
			return new BigDecimal("0");
		}
		return new BigDecimal(value.toString());
	}
	
	/**
	 * 对象转化为整形数字 DATE：2014/07/04
	 * 
	 * @author zhoudl
	 * **/
	public static int emptyToInt(Object value) {
		if ((value == null) || "".equals(value)) {
			return 0;
		}
		return Integer.valueOf(value.toString());
	}

	
	/**
	 * 获取两日期相差天数 DATE：2014/07/04
	 * 
	 * @author zhoudl
	 * **/
	public static BigDecimal getEightBitDigit(BigDecimal value) {
		DecimalFormat decimalFormat = new DecimalFormat("#0.000000000000000000");
		String digit = decimalFormat.format(value);
		BigDecimal decimal = new BigDecimal(digit);
		return decimal;
	}

	/**
	 * 数据流转为字符串
	 * **/
	public static String receiveInputStream(InputStream inputStream) throws SLException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			if (inputStream != null) {
				int length = 0;
				while ((length = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, length);
				}
				outputStream.flush();
			}
			byte[] responseBytes = outputStream.toByteArray();
			String responseString = new String(responseBytes, "UTF-8");
			return responseString;
		} catch (Exception e) {
			throw new SLException(e.getMessage());
		}
	}
	
	/**
	 * JSON字符串转换为实体对象
	 * @return 
	 * **/
	public static <T> Object jsonToObject(String json,Class<T> clazz) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, clazz);
	}
	
	/**
	 * XML字符串转换为OBject
	 * @author zhoudl 20140809
	 * **/
	@SuppressWarnings("unchecked")
	public static <T> T xmlToObject(String xml,Class<T> clazz){
		XStream xstream=new XStream(new DomDriver());
		T vo  = (T)xstream.fromXML(xml);
		return vo;
	}
	
	/**
	 * Object转换为xml
	 * @author zhoudl 20140809
	 * **/
	public static String objectToXml(Object obj){
		XStream xstream=new XStream(new DomDriver());
		String xml = xstream.toXML(obj);
		return xml;
	}
	
	/** 
	 * 将map转换成url 
	 * @param map 
	 * @return 
	 */  
	public static String getUrlParamsByMap(Map<String, Object> map) {  
	    if (map == null) {  
	        return "";  
	    }  
	    StringBuffer sb = new StringBuffer();  
	    for (Map.Entry<String, Object> entry : map.entrySet()) {  
	        sb.append(entry.getKey() + "=" + entry.getValue());  
	        sb.append("&");  
	    }  
	    String s = sb.toString();  
	    if (s.endsWith("&")) {  
	        s = org.apache.commons.lang3.StringUtils.substringBeforeLast(s, "&");  
	    }  
	    return s;  
	}

    /**
     * 判断对象是否Empty(null或元素为0)<br>
     * 实用于对如下对象做判断:String Collection及其子类 Map及其子类
     *
     * @param obj 待检查对象
     * @return boolean 返回的布尔值
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null)
            return true;
        if (obj == "")
            return true;
        if (obj instanceof String) {
            if (((String) obj).length() == 0 || "null".equals(obj)) {
                return true;
            }
        } else if (obj instanceof Collection) {
            if (((Collection) obj).size() == 0) {
                return true;
            }
        } else if (obj instanceof Map) {
            if (((Map) obj).size() == 0) {
                return true;
            }
        }
        return false;
    }
}
