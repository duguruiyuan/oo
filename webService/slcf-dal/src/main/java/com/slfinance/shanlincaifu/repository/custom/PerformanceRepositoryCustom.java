/** 
 * @(#)ProjectInfoRepositoryCustom.java 1.0.0 2016年1月5日 下午1:39:28  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;


/**   
 * 善林大师奖励
 *  
 * @author  lyy
 * @version $Revision:1.0.0, $Date: 2016年12月5日 下午1:39:28 $ 
 */
public interface PerformanceRepositoryCustom {

	BigDecimal queryYesterdayAward(Map<String, Object> params);

	/**
	 * 奖励汇总
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>custId:String:客户经理ID</tt><br>
	 * @return
     *      <tt>monthlySettlement  :String:本月已结算奖励</tt><br>
     *      <tt>monthlyUnSettlement:String:本月未结算奖励</tt><br>
	 * @throws SLException
	 */
	List<Map<String, Object>> queryMyTotalAwardMonth(Map<String, Object> params);

	/**
	 * 奖励汇总
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>custId:String:客户经理ID</tt><br>
	 * @return
     *      <tt>totalSettlement    :String:累计已结算奖励</tt><br>
     *      <tt>totalUnSettlement  :String:累计未结算奖励</tt><br>
	 * @throws SLException
	 */
	List<Map<String, Object>> queryMyTotalAwardHisTotalList(
			Map<String, Object> params);

	/**
	 * 奖励列表
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>custId  :String:客户经理ID</tt><br>
     *      <tt>investId:String:投资ID（可以为空，为空表示查询所有，不为空表示查询当笔投资的奖励情况）</tt><br>
	 * @return
     *      <tt>awardDate        :String:奖励日期</tt><br>
     *      <tt>exceptAwardAmount:String:应奖励金额</tt><br>
     *      <tt>factAwardAmount  :String:已结算奖励</tt><br>
	 * @throws SLException
	 */
	Page<Map<String, Object>> queryMyAwardList(Map<String, Object> params);

	/**
	 * 奖励项目列表
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>custId   :String:客户经理ID</tt><br>
     *      <tt>awardDate:String:奖励日期</tt><br>
	 * @return
     *      <tt>projectName      :String:奖励来源</tt><br>
     *      <tt>exceptAwardAmount:String:应奖励金额</tt><br>
	 * @throws SLException
	 */
	Page<Map<String, Object>> queryMyAwardProjectList(Map<String, Object> params);

	/**
	 * 本月注册人数
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
     *      <tt>custId:String:客户ID（客户经理）</tt><br>
	 * @return
     *      <tt>custId         :String:客户ID</tt><br>
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>registerDate   :String:注册时间</tt><br>
	 * @throws SLException
	 */
	Page<Map<String, Object>> queryMonthlyRegisterList(
			Map<String, Object> params);

	/**
	 * 本月投资人数
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
     *      <tt>custId:String:客户ID（客户经理）</tt><br>
	 * @return
     *      <tt>custId         :String:客户ID</tt><br>
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>investStatus   :String:投资状态</tt><br>
	 * @throws SLException
	 */
	Page<Map<String, Object>> queryMonthlyInvestorList(
			Map<String, Object> params);
	
	

}
