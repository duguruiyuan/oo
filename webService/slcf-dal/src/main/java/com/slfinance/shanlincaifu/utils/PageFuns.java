/** 
 * @(#)PageFuns.java 1.0.0 2015年4月27日 下午5:42:43  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.google.common.collect.Maps;

/**   
 * sql语句拼接工具
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月27日 下午5:42:43 $ 
 */
public class PageFuns {

	/**
	 * 拼接SQL的WHERE或者AND
	 * @param condition
	 * @return
	 */
	public static StringBuilder buildWhereSql( StringBuilder condition ) {
		if(null ==condition)
			return new StringBuilder();
		if( condition.length() > 0 ){
			condition.append(" AND ");
		}else{
			condition.append(" WHERE ");
		}
		return 	condition;
	}
	
	/**
	 * 拼接SQL的IN查询
	 * @param condition
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static StringBuilder buildWhereInParams(List<?> paramsList,List<Object> objList) {
		StringBuilder inParams = new StringBuilder();
		if(paramsList == null || ( paramsList == null && paramsList.size() == 0 ) )
			return inParams;
		int paramsSize = paramsList.size();
		for( int i = 0; i < paramsSize; i++  ){
			inParams.append(" ? ");
			if( paramsSize > 1 && i != paramsSize - 1  )
				inParams.append(",");
		}
		for( String Object: (List<String>)paramsList ){
			objList.add(Object);
		}
		return 	new StringBuilder("( ").append(inParams).append(" )");
	}

	/**
	 * page转Map到指定的key值
	 * @param page
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static <T> Map<String, Object> pageVoToMap(Page<T> page) {
		Map<String,Object> map = Maps.newHashMap();
		if(null == page || ( page != null && page.getTotalPages() < 1  )){
			map.put("iTotalDisplayRecords", 0);
			map.put("data", new ArrayList());
		}
		map.put("iTotalDisplayRecords", page.getTotalElements());
		map.put("data", page.getContent());
		return map;
	}
	
	/**
	 * page转Map到指定的key值
	 * @param page
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static <T> Map<String, Object> pageVoToMap(Page<T> page,List<T> list) {
		Map<String,Object> map = Maps.newHashMap();
		if(null == page || ( page != null && page.getTotalPages() < 1  )){
			map.put("iTotalDisplayRecords", 0);
			map.put("data", new ArrayList());
		}
		map.put("iTotalDisplayRecords", page.getTotalElements());
		map.put("data", null == list ? page.getContent() : list);
		return map;
	}
	
	/**
	 * start 转pageNum
	 * length 转pageSize
	 * @param paramsMap
	 * @return
	 */
	public static Map<String, Object> paramsConvert(Map<String,Object> paramsMap) {
		if( paramsMap.isEmpty() || ( paramsMap != null && paramsMap.size() < 1  ))
			return paramsMap;
		if(paramsMap.containsKey("start"))
			paramsMap.put("pageNum", (int)paramsMap.get("start"));
		if(paramsMap.containsKey("length"))
			paramsMap.put("pageSize", (int)paramsMap.get("length"));
		return paramsMap;
	}
	
	/**
	 * 分页数据String转int
	 * 
	 * @param paramsMap
	 * @return
	 */
	public static Map<String, Object> stringParamsConvertInt(Map<String,Object> paramsMap) {
		if( paramsMap.isEmpty() || ( paramsMap != null && paramsMap.size() < 1  ))
			return paramsMap;
		if(paramsMap.containsKey("start"))
			paramsMap.put("start", Integer.valueOf(paramsMap.get("start").toString()));
		if(paramsMap.containsKey("length"))
			paramsMap.put("length", Integer.valueOf(paramsMap.get("length").toString()));
		if(paramsMap.containsKey("pageNum"))
			paramsMap.put("pageNum", Integer.valueOf(paramsMap.get("pageNum").toString()));
		if(paramsMap.containsKey("pageSize"))
			paramsMap.put("pageSize", Integer.valueOf(paramsMap.get("pageSize").toString()));
		return paramsMap;
	}
	
	/**
	 * start 转pageNum 索引开始位置转换成页数开始位置-1
	 * length 转pageSize
	 * @param paramsMap
	 * @return
	 */
	public static Map<String, Object> pageIndexToPageNum(Map<String,Object> paramsMap) {
		if( paramsMap.isEmpty() || ( paramsMap != null && paramsMap.size() < 1  ))
			return paramsMap;
		if(paramsMap.containsKey("start"))
			paramsMap.put("pageNum", (int)paramsMap.get("start"));
		if(paramsMap.containsKey("length"))
			paramsMap.put("pageSize", (int)paramsMap.get("length"));
		if(paramsMap.containsKey("pageNum") && paramsMap.containsKey("pageSize") )
			paramsMap.put("pageNum", 0 == (int)paramsMap.get("pageNum") ? (int)paramsMap.get("pageNum") : (int)paramsMap.get("pageNum") / (int)paramsMap.get("pageSize"));
		return paramsMap;
	}
	
	/**
	 * start||pageNum 页数开始位置-1 转换成 索引开始数
	 * length||pageSize 
	 * @param paramsMap
	 * @return
	 */
	public static Map<String, Object> pageNumToPageIndex(Map<String,Object> paramsMap) {
		if( paramsMap.isEmpty() || ( paramsMap != null && paramsMap.size() < 1  ))
			return paramsMap;
		if(paramsMap.containsKey("start")){
			paramsMap.put("start", "0".equals(paramsMap.get("start").toString())? paramsMap.get("start") : Integer.parseInt(paramsMap.get("start").toString()) * Integer.parseInt(paramsMap.get("length").toString()) ) ;
			paramsMap.put("start", paramsMap.get("start").toString());
		}
		if(paramsMap.containsKey("pageNum")){
			paramsMap.put("pageNum", "0".equals(paramsMap.get("pageNum").toString())? paramsMap.get("pageNum") : Integer.parseInt(paramsMap.get("pageNum").toString()) * Integer.parseInt(paramsMap.get("pageSize").toString()) ) ;
			paramsMap.put("pageNum", paramsMap.get("pageNum").toString());
		}
		return paramsMap;
	}
	
	/**
	 * pageNum 页数开始位置-1 转换成 索引开始数
	 * pageSize 
	 * @param paramsMap
	 * @return
	 */
	public static Map<String, Object> numToIndex(Map<String,Object> paramsMap) {
		if( paramsMap.isEmpty() || ( paramsMap != null && paramsMap.size() < 1  ))
			return paramsMap;
		if(paramsMap.containsKey("pageNum")){
			paramsMap.put("pageNum", 0 == (int)paramsMap.get("pageNum") ? paramsMap.get("pageNum") :(int)paramsMap.get("pageNum") * (int)paramsMap.get("pageSize")) ;
		}
		return paramsMap;
	}

	
}
