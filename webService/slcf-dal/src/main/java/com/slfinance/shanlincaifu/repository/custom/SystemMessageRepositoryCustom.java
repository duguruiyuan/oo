/** 
 * @(#)ParamRepositoryCustom.java 1.0.0 2015年10月22日 下午4:57:39  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

/**   
 * 消息中心数据自定义接口
 *  
 * @author  Ric.w
 */
public interface SystemMessageRepositoryCustom {
	
	/**
	 * 根据用户id查询所有消息
	 *
	 * @author  Ric.w
	 * @date    2015年10月29日 下午2:16:10
	 * @param param
	        <tt>start：int:分页起始</tt><br>
	  		<tt>length：int:数据长度</tt><br>
	  		<tt>custId：String:用户id</tt><br>
	 * @return
	 * ResultVo data data<List<Map<String,String>>>
	 	    <tt>id：String:用户ID</tt><br>
	  		<tt>sendTitle：String:消息标题</tt><br>
	 		<tt>sendContent：String:发送内容</tt><br>
	 		<tt>sendDate：Date:发送时间</tt><br>
	  		<tt>recordStatus：String:状态</tt><br>
	  		<tt>createUser：String:发送用户id</tt><br>
	 		<tt>readDate：Date:阅读时间</tt><br>
	  		<tt>isRead：String: 已读/null 是否已读</tt><br>
	 * @throws Exception
	 */
	public Page<Map<String, Object>>  findAllByReceiveCustId(Map<String, Object> map)throws Exception;
	
	/**
	 * 根据用户id查询所有消息 By salesMan APP
	 *
	 * @author  lyy
	 * @date    2016年11月11日 下午7:21:10
	 * @param param
	        <tt>start：int:分页起始</tt><br>
	  		<tt>length：int:数据长度</tt><br>
	  		<tt>custId：String:用户id</tt><br>
	 * @return
	 * ResultVo data data<List<Map<String,String>>>
	 	    <tt>id：String:用户ID</tt><br>
	  		<tt>sendTitle：String:消息标题</tt><br>
	 		<tt>sendContent：String:发送内容</tt><br>
	 		<tt>sendDate：Date:发送时间</tt><br>
	  		<tt>recordStatus：String:状态</tt><br>
	  		<tt>createUser：String:发送用户id</tt><br>
	 		<tt>readDate：Date:阅读时间</tt><br>
	  		<tt>isRead：String: 已读/null 是否已读</tt><br>
	 * @throws Exception
	 */
	public Page<Map<String, Object>>  findAllByReceiveCustIdOrSalesMan(Map<String, Object> map)throws Exception;
	
}
