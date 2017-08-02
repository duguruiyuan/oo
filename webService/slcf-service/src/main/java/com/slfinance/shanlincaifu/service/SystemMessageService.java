/** 
 * @(#)SystemMessageService.java 1.0.0 2015年4月29日 下午2:41:02  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.SystemMessageInfoEntity;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 系统消息业务接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月29日 下午2:41:02 $ 
 */
public interface SystemMessageService {

	/**
	 * 站内消息--保存
	 * @param sysMessage
	 *         <ul>
	 *           <li>sendTitle   标题 	{@link java.lang.String}</li>
	 *           <li>sendContent 内容     {@link java.lang.String}</li>
	 *         </ul>
	 * @return
	 *         </ul>
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String} required</li>
	 *         </ul>
	 * @throws SLException
	 */
	public ResultVo saveSiteMessage(SystemMessageInfoEntity sysMessage, String afficheType) throws SLException;
	
	/**
	 * 客户反馈--查询（分页）
	 * 
	 * @param paramsMap
	 *            <ul>
	 *            	<li>start        	分页开始记录数 		{@link java.lang.Integer}</li>
	 *              <li>length      	每页显示记录数 		{@link java.lang.Integer}</li>
	 *			  	<li>custName 	 	客户名称				{@link java.lang.String}</li>
	 *			  	<li>recordStatus 	处理结果    			{@link java.lang.String}</li>
	 *			  	<li>startDate       操作时间[开始时间]	{@link java.util.Date}  </li>
	 * 				<li>endDate         操作时间[结束时间] 	{@link java.util.Date}  </li>
	 *            </ul>
	 * @return <ul>
	 *         <li>iTotalDisplayRecords 总记录数 {@link java.lang.Integer}</li>
	 *         <li>data {@link java.util.List} 分页记录结果集</li>
	 *         <ul>
	 *         <li>{@link java.util.Map} 每行记录</li>
     *         <ul>
	 *         <li>id                  id      {@link java.lang.String}</li>
	 *         <li>sendCust.custName   用户姓名        {@link java.lang.String}</li>
	 *         <li>sendCust.mobile     手机号码        {@link java.lang.String}</li>
	 *         <li>createDate          反馈日期       {@link java.lang.String}</li>
	 *         <li>recordStatus        处理结果       {@link java.lang.String}</li>
	 *        </ul> 
	 *        </ul>
	 */
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页数据不能为空"),
			@Rule(name = "length", required = true, requiredMessage = "分页数据不能为空")
			})
	public Map<String,Object> findFeedback(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 客户反馈--查看详情
	 * 
	 * @param id
	 * 	          <ul>
	 *			  	<li>id 	ID    {@link java.lang.String}</li>
	 *            </ul>
	 * @return 
	 * 		<ul>
	 *         <li>message 消息信息 {@link SystemMessageInfoEntity} </li>
     *         <ul>
	 *         <li>id                  id      {@link java.lang.String}</li>
	 *         <li>custId              用户ID	   {@link java.lang.String}</li>
	 *         <li>sendCust.custName   用户姓名        {@link java.lang.String}</li>
	 *         <li>sendCust.mobile     手机号码        {@link java.lang.String}</li>
	 *         <li>recordStatus        当前状态        {@link java.lang.String}</li>
	 *         <li>sendTitle           标题                {@link java.lang.String}</li>
	 *         <li>sendContent         内容                {@link java.lang.String}</li>
	 *        </ul> 
	 * 		<ul>
	 * 		<ul>
	 *         <li>logList 消息信息 {@link LogInfoEntity} </li>
     *         <ul>
	 *         <li>id                  id    {@link java.lang.String}</li>
	 *         <li>operBeforeContent 操作前状态   {@link java.lang.String}</li>
	 *         <li>operAfterContent  操作后状态   {@link java.lang.String}</li>
	 *         <li>createDate        处理日期       {@link java.util.Date}</li>
	 *         <li>operDesc          操作描述       {@link java.lang.String}</li>
	 *         <li>operPerson        操作人           {@link java.lang.String}</li>
	 *        </ul> 
	 * 		<ul>
	 * @throws SLException
	 */
	public Map<String,Object> findFeedbackDetail(String id) throws SLException;
	
	/**
	 * 客户反馈--更新处理结果
	 * 
	 * @param paramsMap
	 * 	       <ul>
	 *			  <li>id 	    ID    {@link java.lang.String}</li>
	 *			  <li>custId    客户ID {@link java.lang.String}</li>
	 *			  <li>memo 	            备注         {@link java.lang.String}</li>
	 *			  <li>result    处理结果 {@link java.lang.String}</li>
	 *			  <li>ipAddress IP地址   {@link java.lang.String}</li>
	 *         </ul>
	 * @return
	 *         </ul>
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String} required</li>
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "id", required = true, requiredMessage = "ID不能为空"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空"),
			@Rule(name = "memo", required = true, requiredMessage = "备注不能为空"),
			@Rule(name = "result", required = true, requiredMessage = "处理结果不能为空")
			})
	public ResultVo updateFeedback(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 发送站内信
	 *
	 * @author  wangjf
	 * @date    2015年12月26日 下午1:32:33
	 * @param paramsMap
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "title", required = true, requiredMessage = "站内信标题号不能为空"), 
			@Rule(name = "content", required = true, requiredMessage = "站内信内容不能为空"),
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空")
	})
	public void sendSystemMessage(Map<String, Object> paramsMap);
	
}
