/** 
 * @(#)TermService.java 1.0.0 2015年8月12日 上午9:41:45  
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
 * 定期宝接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月12日 上午9:41:45 $ 
 */
public interface TermService {

	/**
	 * 加入定期宝
	 *
	 * @author  wangjf
	 * @date    2015年8月12日 上午9:42:31
	 * @param params
	 * @param params
	 *      <tt>productId： String:产品ID</tt><br>
	 		<tt>custId： String:用户ID</tt><br>
	  		<tt>tradeAmount： String:加入金额</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "productId", required = true, requiredMessage = "产品主键不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "交易金额不能为空!", number = true, numberMessage = "交易金额只能为数字", digist = true, digistMessage = "交易金额只能为整数")			
	})
	public ResultVo joinTermBao(Map<String, Object> params) throws SLException;
	
	/**
	 * 申请提前赎回定期宝
	 *
	 * @author  wangjf
	 * @date    2015年8月12日 上午9:51:54
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "investId", required = true, requiredMessage = "投资主键不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!"),
			@Rule(name = "tradePassword", required = true, requiredMessage = "交易密码不能为空!")
	})
	public ResultVo termWithdrawApply(Map<String, Object> params) throws SLException;
	
	/**
	 * 到期赎回
	 *
	 * @author  wangjf
	 * @date    2015年8月15日 上午10:07:57
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo termAtoneWithdraw(Map<String, Object> params)throws SLException;
	
	/**
	 * 公司回购
	 *
	 * @author  wangjf
	 * @date    2015年8月15日 上午10:07:15
	 * @return
	 * @throws SLException
	 */
	public ResultVo termAtoneBuy(Map<String, Object> params)throws SLException;
	
	/**
	 * 定期宝结息
	 *
	 * @author  wangjf
	 * @date    2015年8月12日 上午9:54:00
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo termDailySettlement(Map<String, Object> params)throws SLException;
	
	/**
	 * 定期宝赎回到帐
	 *
	 * @author  wangjf
	 * @date    2015年8月15日 上午10:06:29
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo termAtoneSettlement(Map<String, Object> params)throws SLException;
	
	/**
	 * 获取定期宝产品详情
	 *
	 * @author  wangjf
	 * @date    2015年8月18日 下午2:28:21
	 * @param params
	 * 		<tt>productId： String:产品ID</tt><br>
	 * @return
	 *            <tt>key:currUsableValue, title:当前可投额度, type:{@link String} </tt><br>
	 *            <tt>key:partakeOrganizs, title:参与机构, type:{@link String} </tt><br>
	 *            <tt>key:partakeCrerigs, title:参与债权, type:{@link String} </tt><br>
	 *            <tt>key:alreadyInvestPeople, title:参与人数, type:{@link String} </tt><br>
	 *            <tt>key:ensureMethod, title:保障方式, type:{@link String} </tt><br>
	 *            <tt>key:alreadyInvestAmount, title:累计投资额, type:{@link String} </tt><br>
	 *            <tt>key:accumulativeLucre, title:为用户累计赚取, type:{@link String} </tt><br>
	 */
	public Map<String, Object> findTermBAODetail(Map<String, Object> params); 
	
	/**
	 * 查询提前赎回金额
	 *
	 * @author  wangjf
	 * @date    2015年8月20日 上午10:15:26
	 * @param params
	 *      <tt>investId： String:投资ID</tt><br>
	 		<tt>custId： String:用户ID</tt><br>
	 * @return
	 * 		<tt>investTitle： String:退出期数</tt><br>
	 * 		<tt>investAmount： String:加入金额</tt><br>
	 * 		<tt>exceptAmount： String:预计回收金额</tt><br>
	 * 		<tt>expense： String:提前退出费用</tt><br>
	 */
	public Map<String, Object> findAdvancedAtone(Map<String, Object> params);
}
