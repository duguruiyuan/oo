/** 
 * @(#)APPBankServiceImpl.java 1.0.0 2015年7月20日 下午5:49:55  
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
 * APP手机端银行卡管理业务接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年7月20日 下午5:49:55 $ 
 */
public interface APPBankService  {

	/***
	 * 解绑银行卡
	 * 
	 * @author zhangzs
	 * @date 2015年7月20日 下午5:49:55
	 * @param 
	 * 	 		<li>custId			用户id		{@link java.lang.String}</li>
	 *	     	<li>bankId			银行卡id		{@link java.lang.String}</li>
	 *	     	<li>tradePassword	交易密码		{@link java.lang.String}</li>
	 *	     	<li>unbindType		解绑类型		{@link java.lang.String}</li>
	 *	     	<li>unbindReason 	解绑原因		{@link java.lang.String}</li>
	 *	     	<li>ipAddress 	    IP			{@link java.lang.String}</li>
	 *
	 * @return <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!"),
			@Rule(name = "bankId", required = true, requiredMessage = "银行主键不能为空!"),
			@Rule(name = "tradePassword", required = true, requiredMessage = "交易密码不能为空!"),
			@Rule(name = "unbindType", required = true, requiredMessage = "解绑类型不能为空!"),
			@Rule(name = "unbindReason", required = true, requiredMessage = "解绑原因不能为空!"),
			@Rule(name = "ipAddress", required = true, requiredMessage = "IP不能为空!")
	})
	public ResultVo unBindBankCard(Map<String, Object> param) throws SLException;
	
	/***
	 * 根据银行卡ID查询银行的状态
	 * 
	 * @author zhangzs
	 * @date 2015年7月20日 下午5:49:55
	 * @param 
	 *	     	<li>id			银行卡id		{@link java.lang.String}</li>
	 *
	 * @return 
	 * 		   <li>String {@link java.lang.String} 已解绑、解绑审核中、已绑定、其他</li>
	 * @throws SLException
	 */
	public String getBankCardState( String id )throws SLException;
	
	
	/***
	 * 银行卡列表
	 * 
	 * @author zhangzs
	 * @date 2015年7月20日 下午5:49:55
	 * @param 
	 * 	 		<li>custId		用户id	{@link java.lang.String}</li>
	 *
	 * @return <li>List {@link java.util.Map} 返回结果</li>
	 *         <ul>
	 *         <li>id 			银行卡主键 	{@link java.lang.String}</li>
	 *         <li>custId 		客户ID 	{@link java.lang.String}</li>
	 *         <li>bankName 	银行名称 	{@link java.lang.String}</li>
	 *         <li>bankCode 	银行编号 	{@link java.lang.String}</li>
	 *         <li>cardNo 		银行卡卡号 	{@link java.lang.String}</li>
	 *         <li>recordStatus 绑定状态 	{@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!")
	})
	public ResultVo getBankCardList(Map<String, Object> param) throws SLException;
	
}
