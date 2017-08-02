/** 
 * @(#)APPFixedInvestController.java 1.0.0 2015年7月20日 上午8:56:04  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller.mobile;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.mobile.AppThirdService;
import com.slfinance.vo.ResultVo;

/**   
 * 手机APP端三期定期宝模块功能控制器
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年7月20日 上午8:56:04 $ 
 */
@Controller
@RequestMapping(value="/thr", produces="application/json;charset=UTF-8")
public class APPFixedInvestController {
	
	@Autowired
	private AppThirdService  appThirdService;

	/**
	 * 活期宝、体验宝、定期宝产品列表
	 */
	@RequestMapping(value="/investListAll", method = RequestMethod.POST)
	public @ResponseBody ResultVo investListAll(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appThirdService.investListAll(paramsMap);
	}
	
	/**
	 * 活期宝、体验宝、定期宝购买投资页面
	 */	
	@RequestMapping(value="/investbBuyDetail", method = RequestMethod.POST)
	public @ResponseBody ResultVo  investbBuyDetail(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appThirdService.investbBuyDetail(paramsMap);
	}
	
	/**
	 * 交易记录
	 */
	@RequestMapping(value="/transactionList", method = RequestMethod.POST)
	public @ResponseBody ResultVo transactionList( @RequestBody Map<String,Object> paramsMap) throws SLException{
		return appThirdService.transactionList(paramsMap);
	}
	
	/**
	 * 我的投资-活期宝、定期宝、体验宝页面
	 */
	@RequestMapping(value="/investStatisticsInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo  investStatisticsInfo(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appThirdService.investStatisticsInfo(paramsMap);
	}
	
	/**
	 * 定期宝投资详情
	 */
	@RequestMapping(value="/fixedInvestDetail", method = RequestMethod.POST)
	public @ResponseBody ResultVo fixedInvestDetail(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appThirdService.fixedInvestDetail(paramsMap);
	}
	
	/**
	 * 提前退出定期宝详情
	 */
	@RequestMapping(value="/preAtoneDetail", method = RequestMethod.POST)
	public @ResponseBody ResultVo preAtoneDetail(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appThirdService.preAtoneDetail(paramsMap);
	}
	
	/**
	 * 购买定期宝
	 */
	@RequestMapping(value="/joinTermBao", method = RequestMethod.POST)
	public @ResponseBody ResultVo joinTermBao(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appThirdService.joinTermBao(paramsMap);
	}
	
	/**
	 * 定期宝产品加入记录
	 */
	@RequestMapping(value="/joinListInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo joinListInfo(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appThirdService.joinListInfoTrd(paramsMap);
	}
	
	/**
	 * 提前赎回
	 */
	@RequestMapping(value="/termWithdrawApply", method = RequestMethod.POST)
	public @ResponseBody ResultVo termWithdrawApply(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appThirdService.termWithdrawApply(paramsMap);
	}
	
	/**
	 * 申请成为金牌推荐人条件判断
	 */
	@RequestMapping(value="/recommendFalg", method = RequestMethod.POST)
	public @ResponseBody ResultVo recommendFalg(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appThirdService.recommendFalg(paramsMap);
	}
	
	/**
	 * 申请金牌推荐人
	 */
	@RequestMapping(value="/putRecommend", method = RequestMethod.POST)
	public @ResponseBody ResultVo putRecommend(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appThirdService.putRecommend(paramsMap);
	}
	
	/**
	 * 金牌推荐人佣金统计信息
	 */
	@RequestMapping(value="/RecInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo RecInfo(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appThirdService.RecInfo(paramsMap);
	}
	
	/**
	 * 佣金列表
	 */
	@RequestMapping(value="/awardList", method = RequestMethod.POST)
	public @ResponseBody ResultVo awardList(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appThirdService.awardList(paramsMap);
	}
	
	/**
	 * 统计信息(推广奖励、年化投资、推荐好友)和推广奖励信息(活期宝、定期宝)
	 */
	@RequestMapping(value="/recommedInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo recommedInfo(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appThirdService.recommedInfo(paramsMap);
	}
	
	/**
	 * 金牌推荐人当天或当月在投详情
	 */
	@RequestMapping(value="/investListDetail", method = RequestMethod.POST)
	public @ResponseBody ResultVo investListDetail(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appThirdService.investListDetail(paramsMap);
	}
	
	/**
	 * 活期宝、体验宝、定期宝产品列表(带利率)
	 */
	@RequestMapping(value="/investListAllWithYearRateList", method = RequestMethod.POST)
	public @ResponseBody ResultVo investListAllWithYearRateList(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appThirdService.investListAllWithYearRateList(paramsMap);
	}
	
}
