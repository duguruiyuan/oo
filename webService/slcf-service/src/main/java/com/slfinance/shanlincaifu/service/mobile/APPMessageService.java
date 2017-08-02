/** 
 * @(#)APPMessageService.java 1.0.0 2015年5月15日 下午2:52:15  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.mobile;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * APP手机端消息管理业务接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月15日 下午2:52:15 $ 
 */
public interface APPMessageService {
	
	/**
	 * 客户反馈信息保存
	 * 
	 * @param 
	 *	     	<li>custId 	         反馈信息用户的id {@link java.lang.String}</li>
	 *	     	<li>sendContent反馈内容                    {@link java.lang.String}</li>
	 * @return
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException 
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "反馈信息用户的id数据不能为空"),
			@Rule(name = "sendContent", required = true, requiredMessage = "反馈内容数据不能为空")
			})
	public ResultVo saveFeedback(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 查询最新网站公告
	 * 
	 * @return 
	 * 	 		<li>id 	                              公告ID 	  {@link java.lang.String}</li>
	 *	     	<li>afficheTitle  标题                {@link java.lang.String}</li>
	 *	     	<li>afficheType   类型                {@link java.lang.String}</li>
	 *	     	<li>createUser    创建人            {@link java.lang.String}</li>
	 *	     	<li>createDate    创建时间        {@link java.util.Date}</li>
	 *	     	<li>publishUser   发布人            {@link java.lang.String}</li>
	 *	     	<li>publishTime   发布时间        {@link java.util.Date}</li>
	 * @throws SLException 
	 */
	public ResultVo findLatestMessage() throws SLException;
	
	/**
	 * 分页查询网站公告列表
	 * 
	 * 	 * @param paramsMap
	 *            <ul>
	 *            	<li>start        	分页开始记录数当前页数减1{@link java.lang.String}</li>
	 *              <li>length      	每页显示记录数 		 {@link java.lang.String}</li>
	 *            </ul>
	 * @return <ul>
	 *         <li>iTotalDisplayRecords 总记录数 {@link java.lang.Integer}</li>
	 *         <li>data {@link java.util.List} 分页记录结果集</li>
	 *         <ul>
	 *         <li>{@link java.util.Map} 每行记录</li>
     *         <ul>
	 *		  	<li>id 	                              公告ID 	  {@link java.lang.String}</li>
	 *	     	<li>afficheTitle  标题                {@link java.lang.String}</li>
	 *	     	<li>afficheType   类型                {@link java.lang.String}</li>
	 *	     	<li>createUser    创建人            {@link java.lang.String}</li>
	 *	     	<li>createDate    创建时间        {@link java.util.Date}</li>
	 *	     	<li>publishUser   发布人            {@link java.lang.String}</li>
	 *	     	<li>publishTime   发布时间        {@link java.util.Date}</li>
	 *        </ul> 
	 *        </ul>
	 */
	public ResultVo findMessagePage(Map<String,Object> paramsMap) throws SLException;
}
