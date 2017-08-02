/** 
 * @(#)FixedInvestController.java 1.0.0 2015年8月15日 上午11:14:32  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.FixedInvestService;
import com.slfinance.shanlincaifu.service.RecommedBusiService;
import com.slfinance.vo.ResultVo;

/**   
 * 定期宝业务列表、统计、交易、投资记录控制器
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年8月15日 上午11:14:32 $ 
 */
@Controller
@RequestMapping(value="/fix")
public class FixedInvestController {
	
	@Autowired
	private FixedInvestService fixedInvestService;
	@Autowired
	private RecommedBusiService recommedBusiService;
	
	/**
	 * 定期宝分页列表查询
	 */
	@RequestMapping(value="/getFixedInvestListPage", method = RequestMethod.POST)
	public @ResponseBody ResultVo getFixedInvestListPage(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return fixedInvestService.getFixedInvestListPage(paramsMap);
	}
	
	/**
	 * 定期宝分页列表查询-按照欢迎程序排序
	 */
	@RequestMapping(value="/getInvestListByFavoPage", method = RequestMethod.POST)
	public @ResponseBody ResultVo getInvestListByFavoPage(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return fixedInvestService.getInvestListByFavoPage(paramsMap);
	}
	
	/**
	 * 定期宝购买页面详情
	 */
	@RequestMapping(value="/getFixedInvestDatail", method = RequestMethod.POST)
	public @ResponseBody ResultVo getFixedInvestDatail(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return fixedInvestService.getFixedInvestDatail(paramsMap);
	}

	/**
	 * 定期宝产品加入记录
	 */
	@RequestMapping(value="/getInvestListByProIdPage", method = RequestMethod.POST)
	public @ResponseBody ResultVo getInvestListByProIdPage(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return fixedInvestService.getInvestListByProIdPage(paramsMap);
	}
	
	/**
	 * 帐户总览统计信息
	 */
	@RequestMapping(value="/getFixedInvestStatisicInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo getFixedInvestStatisicInfo(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return fixedInvestService.getFixedInvestStatisicInfo(paramsMap);
	}

	/**
	 * 定期宝投资详情
	 */
	@RequestMapping(value="/getFixedInvestDetail", method = RequestMethod.POST)
	public @ResponseBody ResultVo getFixedInvestDetail(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return fixedInvestService.getFixedInvestDetail(paramsMap);
	}

	/**
	 * 定期宝投资详情-加入记录
	 */
	@RequestMapping(value="/getFixedTradeInfoPage", method = RequestMethod.POST)
	public @ResponseBody ResultVo getFixedTradeInfoPage(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return fixedInvestService.getFixedTradeInfoPage(paramsMap);
	}
	
	/**
	 * 申请成为金牌推荐人条件判断
	 */
	@RequestMapping(value="/getRecommendFalg", method = RequestMethod.POST)
	public @ResponseBody ResultVo getRecommendFalg(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return fixedInvestService.getRecommendFalg(paramsMap);
	}
	
	/**
	 * 申请金牌推荐人
	 */
	@RequestMapping(value="/putRecommendInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo putRecommendInfo(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return fixedInvestService.putRecommendInfo(paramsMap);
	}
	
	/**
	 * 统计信息(推广奖励、年化投资、推荐好友)和推广奖励信息(活期宝、定期宝)
	 */
	@RequestMapping(value="/getRecommedInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo getRecommedInfo(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return recommedBusiService.getRecommedInfo(paramsMap);
	}
	
	/**
	 * 金牌推荐人当天或当月在投详情
	 */
	@RequestMapping(value="/getInvestListDetail", method = RequestMethod.POST)
	public @ResponseBody ResultVo getInvestListDetail(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return recommedBusiService.getInvestListDetail(paramsMap);
	}

}
