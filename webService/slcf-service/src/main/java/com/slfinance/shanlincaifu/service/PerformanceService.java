/** 
 * @(#)ProjectService.java 1.0.0 2016年12月5日 上午9:48:37  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 善林大师奖励
 * @author  lyy
 * @version $Revision:1.0.0, $Date: 2016年12月5日 上午9:48:37 $ 
 */
public interface PerformanceService {
	
	/**
	 * 昨日奖励
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>custId:String:客户经理ID</tt><br>
	 * @return
     *      <tt>yesterdayAwardAmount:String:奖励金额</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户经理不能为空") 
	})
	public ResultVo queryYesterdayAward(Map<String, Object> params)  throws SLException;

	/**
	 * 奖励汇总
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>custId:String:客户经理ID</tt><br>
	 * @return
     *      <tt>monthlySettlement  :String:本月已结算奖励</tt><br>
     *      <tt>monthlyUnSettlement:String:本月未结算奖励</tt><br>
     *      <tt>totalSettlement    :String:累计已结算奖励</tt><br>
     *      <tt>totalUnSettlement  :String:累计未结算奖励</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户经理不能为空") 
	})
	public ResultVo queryMyTotalAward(Map<String, Object> params)  throws SLException;

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
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户经理不能为空"),
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryMyAwardList(Map<String, Object> params)  throws SLException;

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
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户经理不能为空"),
			@Rule(name = "awardDate", required = true, requiredMessage = "奖励日期不能为空"), 
	})
	public ResultVo queryMyAwardProjectList(Map<String, Object> params)  throws SLException;

	/**
	 * 我的奖励
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>custManagerId:String:客户经理ID</tt><br>
     *      <tt>investId     :String:投资ID（可以为空，为空表示查询所有，不为空表示查询当笔投资的奖励情况）</tt><br>
	 * @return
     *      <tt>awardDate        :String:奖励日期</tt><br>
     *      <tt>exceptAwardAmount:String:应奖励金额</tt><br>
     *      <tt>factAwardAmount  :String:已结算奖励</tt><br>
	 * @throws SLException
	 */
//	public ResultVo queryMyAwardList(Map<String, Object> params)  throws SLException;

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
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户经理不能为空"),
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryMonthlyRegisterList(Map<String, Object> params)  throws SLException;

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
	public ResultVo queryMonthlyInvestorList(Map<String, Object> params)  throws SLException;

	/**
	 * 客户详情
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>custId:String:客户ID</tt><br>
	 * @return
     *      <tt>custId         :String:客户ID</tt><br>
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>investList     :String:List<Map<String,Object>>:投资列表</tt><br>
     *      <tt>projectName    :String:项目名称</tt><br>
     *      <tt>investDate     :String:投资时间</tt><br>
     *      <tt>investAmount   :String:投资金额</tt><br>
     *      <tt>investStatus   :String:投资状态</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryCustDetail(Map<String, Object> params)  throws SLException;
}
