/** 
 * @(#)WithDarwTempService.java 1.0.0 2015年5月28日 上午9:55:42  
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
 * 提现模拟回调成功或失败模拟业务接口 ，仅提供测试用
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月28日 上午9:55:42 $ 
 */
public interface WithDarwTempService {

	
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
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空")
			})
	public ResultVo saveWithdrawCashAudit(Map<String,Object> paramsMap) throws SLException;	

}
