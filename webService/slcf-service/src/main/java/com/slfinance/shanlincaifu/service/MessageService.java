/** 
 * @(#)AccountService.java 1.0.0 2015年4月21日 上午11:24:11  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 站内消息服务
 *  
 * @author  HuangXiaodong
 * @version $Revision:1.0.0, $Date: 2015年4月21日 上午11:24:11 $ 
 */
public interface MessageService{
	
	
	/**
	 * 查找未读系统消息
	 *
	 * @author  richard
	 * @date    2015年4月23日 下午7:59:07
	 * @param param
	        <tt>bankCard：BankCardInfoEntity:银行卡信息</tt><br>
	 * @return
	 		<tt>resultVo： isSuccess:是否成功</tt><br>
	 */
	public ResultVo unReadMessage(Map<String, Object> param) throws SLException;
	
	/**
	 * 查找未读系统消息
	 *
	 * @author  Ric.w
	 * @date    2015年10月18日 下午4:17:07
	 * @param param
	        <tt>custId：接收消息的用户id</tt><br>
	        <tt>messageId：消息id</tt><br>
	 * @return
	 		<tt>resultVo： isSuccess:是否成功</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空"), 
			@Rule(name = "messageId", required = true, requiredMessage = "消息ID不能为空"),
	})
	public ResultVo updateStatus(Map<String, Object> param) throws SLException;
	
	/**
	 * 客户反馈
	 * @param param
	 * 		<tt>key:custId, title:客户ID, type:{@link String} </tt><br>
	 * 		<tt>key:suggestionType, title:反馈类型, type:{@link String} </tt><br>
	 * 		<tt>key:suggestionContent, title:反馈内容, type:{@link String} </tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空"), 
			@Rule(name = "suggestionType", required = true, requiredMessage = "反馈类型不能为空"),
			@Rule(name = "suggestionContent", required = true, requiredMessage = "反馈内容不能为空"),
	})
	public ResultVo feedback(Map<String, Object> param) throws SLException;

}
