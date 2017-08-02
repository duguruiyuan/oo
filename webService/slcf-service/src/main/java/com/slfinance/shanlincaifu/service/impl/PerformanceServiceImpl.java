/** 
 * @(#)ProjectServiceImpl.java 1.0.0 2016年12月5日 下午12:10:22  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.custom.PerformanceRepositoryCustom;
import com.slfinance.shanlincaifu.service.PerformanceService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**   
 * 善林大师奖励
 * @author  lyy
 * @version $Revision:1.0.0, $Date: 2016年12月5日 上午9:48:37 $ 
 */

@Service("performanceService")
public class PerformanceServiceImpl implements PerformanceService {
	
	@Autowired
	private PerformanceRepositoryCustom performanceRepositoryCustom;
	
	@Override
	public ResultVo queryYesterdayAward(Map<String, Object> params)
			throws SLException {
		
		BigDecimal amount = performanceRepositoryCustom.queryYesterdayAward(params);
		
		Map<String, Object> data = Maps.newHashMap();
		data.put("yesterdayAwardAmount", amount);
		return new ResultVo(true, "昨日奖励查询成功", data);
	}

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
	@Override
	public ResultVo queryMyTotalAward(Map<String, Object> params)
			throws SLException {
		String monthlySettlement = "0";
		String monthlyUnSettlement = "0";
		String totalSettlement = "0";
		String totalUnSettlement = "0";
		
		List<Map<String, Object>> monthList = performanceRepositoryCustom.queryMyTotalAwardMonth(params);
		for(Map<String, Object> temp : monthList){
			if(Constant.PAYMENT_STATUS_01.equals(temp.get("paymentStatus"))){
				monthlySettlement = temp.get("amount").toString();
			} else if(Constant.PAYMENT_STATUS_02.equals(temp.get("paymentStatus"))){
				monthlyUnSettlement = temp.get("amount").toString();
			}
		}
		
		List<Map<String, Object>> hisTotalList = performanceRepositoryCustom.queryMyTotalAwardHisTotalList(params);
		for(Map<String, Object> temp : hisTotalList){
			if(Constant.PAYMENT_STATUS_01.equals(temp.get("paymentStatus"))){
				totalSettlement = temp.get("amount").toString();
			} else if(Constant.PAYMENT_STATUS_02.equals(temp.get("paymentStatus"))){
				totalUnSettlement = temp.get("amount").toString();
			}
		}

		BigDecimal yesterdayAwardAmount = performanceRepositoryCustom.queryYesterdayAward(params);
		
		Map<String, Object> data = Maps.newHashMap();
		data.put("monthlySettlement", monthlySettlement);
		data.put("monthlyUnSettlement", monthlyUnSettlement);
		data.put("totalSettlement", totalSettlement);
		data.put("totalUnSettlement", totalUnSettlement);
		data.put("yesterdayAwardAmount", yesterdayAwardAmount);
		
		return new ResultVo(true, "奖励汇总查询成功", data);
	}

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
	public ResultVo queryMyAwardList(Map<String, Object> params)
			throws SLException {
		
		Page<Map<String, Object>> pageVo = performanceRepositoryCustom.queryMyAwardList(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "奖励列表查询成功", result);
	}


	/**
	 * 我的奖励
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 */
	public ResultVo queryMyAwardProjectList(Map<String, Object> params)
			throws SLException {
		Page<Map<String, Object>> pageVo = performanceRepositoryCustom.queryMyAwardProjectList(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "奖励项目列表查询成功", result);
	}

	/**
	 * 本月注册人数
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 */
	public ResultVo queryMonthlyRegisterList(Map<String, Object> params)
			throws SLException {
		Page<Map<String, Object>> pageVo = performanceRepositoryCustom.queryMonthlyRegisterList(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "本月注册人数查询成功", result);
	}

	/**
	 * 本月投资人数
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 */
	public ResultVo queryMonthlyInvestorList(Map<String, Object> params)
			throws SLException {
		Page<Map<String, Object>> pageVo = performanceRepositoryCustom.queryMonthlyInvestorList(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "本月投资人数查询成功", result);
	}

	@Override
	public ResultVo queryCustDetail(Map<String, Object> params)
			throws SLException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
