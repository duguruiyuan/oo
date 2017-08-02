/** 
 * @(#)CustDailyValueHistory.java 1.0.0 2015年5月23日 下午12:23:29  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;

/**   
 * 用户每日持有份额数据访问类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月23日 下午12:23:29 $ 
 */
public interface CustDailyValueHistoryRepositroyCustom {
	
	/**
	 * 查询用户每天价值情况
	 *
	 * @author  wangjf
	 * @date    2015年5月23日 下午12:03:43
	 * @param param
	 * 		<tt>start：int:分页起始页</tt><br>
	 		<tt>length：int:每页长度</tt><br>
	 		<tt>productName：String:产品名称</tt><br>
	 		<tt>custId：String:客户编号</tt><br>
	 		<tt>dateBegin： String:日期开始(可为空)</tt><br>
			<tt>dateEnd： String:日期结束(可为空)</tt><br>
	 * @return
	 * 		<tt>dailyValueId： String:主键</tt><br>
	 		<tt>date： String:日期</tt><br>
	  		<tt>productName： String:产品名称</tt><br>
	 		<tt>holdValue： BigDecimal:持有价值</tt><br>
	 */
	public Page<Map<String, Object>> findDailyValueList(Map<String, Object> param);
	
	/**
	 * 保存用户每日持有份额
	 *
	 * @author  wangjf
	 * @date    2015年5月23日 下午3:22:52
	 * @param now
	 */
	public void saveDailyValueList(Date now, String productName) throws SLException;
	
	/**
	 * 查询用户价值买入卖出情况
	 *
	 * @author  wangjf
	 * @date    2015年8月26日 下午3:47:07
	 * @param param
	 * 		<tt>start：int:分页起始页</tt><br>
	 		<tt>length：int:每页长度</tt><br>
	 		<tt>productName：String:产品名称</tt><br>
	 		<tt>custId：String:客户编号</tt><br>
	 		<tt>dateBegin： String:日期开始(可为空)</tt><br>
			<tt>dateEnd： String:日期结束(可为空)</tt><br>
	 * @return
	 * 		<tt>dailyValueId： String:主键(若是当天则为空)</tt><br>
	 		<tt>date： String:日期</tt><br>
	  		<tt>productName： String:产品名称</tt><br>
	  		<tt>direction： String:方向</tt><br>
	 		<tt>holdValue： BigDecimal:持有价值</tt><br>
	 */
	public Page<Map<String, Object>> findDailyLoan(Map<String, Object> param);
	
	/**
	 * 查询当前价值(所有债权总价值+未处理价值)
	 *
	 * @author  wangjf
	 * @date    2015年8月26日 下午5:10:10
	 * @param param
	 * 		<tt>productName：String:产品名称</tt><br>
	 * @return
	 */
	public BigDecimal queryCurrentValue(String productName); 
	
	/**
	 * 查询用户价值(当前价值)
	 *
	 * @author  wangjf
	 * @date    2015年8月26日 下午5:33:10
	 * @param custId 客户主键
	 * @param subAccountId 若为活期宝，此值可以为空
	 * @param productName
	 * @return
	 */
	public BigDecimal queryUserValue(String custId, String subAccountId, String productName);
}
