/** 
 * @(#)ImgSrcReplaceUtil.java 1.0.0 2015年7月16日 上午10:35:38  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 
package com.slfinance.shanlincaifu.utils;

import java.io.File;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;

/**   
 * 替换HTML中图片img的src路径工具类
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年7月16日 上午10:35:38 $ 
 */
@Slf4j
public class ImgSrcReplaceUtil {
	
	private static String PATH = "http://image.shanlincaifu.com/slcf/ueditor/image/"; 
	//private static String PATH = "http://image.shanlinbao.com/bao/";
	
	private static String imgReg = "(?s)(<img.*?)(src\\s*?=\\s*?(?:\"|').*?(?:\"|'))";
	
	private static String baoReg = "(?s)(image.*?)\\/|\\=";
//	private static String baoReg = "(?s)(bao.*?)\\/|\\=";
	
	public static String getContent( String html ) {
		log.info("original html:"+html);
		String name="";
		Pattern p = Pattern.compile(imgReg);
		Matcher m = p.matcher(html); 
		StringBuffer content = new StringBuffer();
		while(m.find()){
			log.info("img url :" + m.group());
			String g2 = m.group(2);
		    String[] names=g2.split(baoReg);
		     if(names.length>=1)
		    	 name = names[names.length-1];
		    name = name.replaceAll("\"|'","");
		    m.appendReplacement(content,"$1src=\"" + PATH + name+"\"");
		}
		m.appendTail(content);
		log.info("replace html:" + content);
		return content.toString();
	}
	public static void downloadFromUrl(String formUrl,String toDir) {  
        try {  
            URL httpurl = new URL(formUrl);  
            String fileName = getFileNameFromUrl(formUrl);  
            log.info("fileName:" + fileName);
            File f = new File(toDir + fileName);  
            FileUtils.copyURLToFile(httpurl, f);  
        } catch (Exception e) {  
        	log.error("下载失败",e);
        }   
    }  
	
	public static String getFileNameFromUrl(String url){  
        String name = new Long(System.currentTimeMillis()).toString() + ".X";  
        int index = url.lastIndexOf("/");  
        if(index > 0){  
            name = url.substring(index + 1);  
            if(name.trim().length()>0){  
                return name;  
            }  
        }  
        return name;  
    }  
 
 	
}
