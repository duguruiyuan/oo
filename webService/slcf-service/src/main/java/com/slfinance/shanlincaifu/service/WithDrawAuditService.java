/** 
 * @(#)WithdrawCashService.java 1.0.0 2015年4月27日 上午10:27:55  
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
 * 提现审核模块业务Service
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月27日 上午10:27:55 $ 
 */
public interface WithDrawAuditService {
	
	
	/**
	 * 提现管理--提现审核提交 
	 * 
	 * @param paramsMap
	 *         <ul>
	 *		  	<li>id 	                     提现审核ID 	 {@link java.lang.String}</li>
	 *		  	<li>auditStatus提现审核状态      {@link java.lang.String}</li>
	 *		  	<li>memo 	         提现审核备注 	 {@link java.lang.String}</li>
	 *	     	<li>custId 	         操作用户的id {@link java.lang.String}</li>
	 *			<li>version    版本信息             {@link java.lang.Integer}</li>
	 *         </ul>
	 * @return
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String} required</li>
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "id", required = true, requiredMessage = "提现审核ID不能为空"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "提现审核状态不能为空"),
			@Rule(name = "memo", required = true, requiredMessage = "提现审核备注不能为空"),
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空"),
			@Rule(name = "ipAddress", required = true, requiredMessage = "用户IP地址不能为空")
			})
	public ResultVo saveWithdrawCashAudit(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 提现回调成功处理业务
	 * 
	 * @param paramsMap
	 * 	       <ul>
	 *		  	<li>tradeNo    交易编号 	    {@link java.lang.String}</li>
	 *		  	<li>tradeCode  第三方返回交易码       {@link java.lang.String}</li>
	 *		  	<li>status 	         过程处理状态 	    {@link java.lang.String}</li>
	 *	     	<li>auditStatus审核状态                       {@link java.lang.String}</li>
	 *			<li>tradeStatus交易状态                       {@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "tradeNo", required = true, requiredMessage = "交易编号不能为空"),
			@Rule(name = "tradeCode", required = true, requiredMessage = "第三方返回交易码不能为空"),
			@Rule(name = "status", required = true, requiredMessage = "过程处理状态不能为空"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空"),
			@Rule(name = "tradeStatus", required = true, requiredMessage = "交易状态不能为空")
			})
	public ResultVo callbackWithdrawCash(Map<String,Object> paramsMap) throws SLException;

	/**
	 * 提现回调失败处理业务

	 * @param paramsMap
	 * 	  	 <ul>
	 *		  	<li>tradeNo    交易编号 	    {@link java.lang.String}</li>
	 *		  	<li>tradeCode  第三方返回交易码       {@link java.lang.String}</li>
	 *		  	<li>status 	         过程处理状态 	    {@link java.lang.String}</li>
	 *	     	<li>auditStatus审核状态                       {@link java.lang.String}</li>
	 *			<li>tradeStatus交易状态                       {@link java.lang.String}</li>
	 *       <ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "tradeNo", required = true, requiredMessage = "交易编号不能为空"),
			@Rule(name = "tradeCode", required = true, requiredMessage = "第三方返回交易码不能为空"),
			@Rule(name = "status", required = true, requiredMessage = "过程处理状态不能为空"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空"),
			@Rule(name = "tradeStatus", required = true, requiredMessage = "交易状态不能为空")
			})
	public ResultVo callbackWithdrawCashFailed(Map<String, Object> paramsMap)throws SLException;
	
}
