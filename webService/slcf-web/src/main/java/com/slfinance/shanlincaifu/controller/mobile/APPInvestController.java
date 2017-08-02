/** 
 * @(#)APPInvestController.java 1.0.0 2015年5月19日 上午8:56:04  
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
import com.slfinance.shanlincaifu.service.mobile.APPInvestService;
import com.slfinance.vo.ResultVo;

/**   
 * 手机APP端投资模块功能控制器
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月19日 上午8:56:04 $ 
 */
@Controller
@RequestMapping(value="/invest", produces="application/json;charset=UTF-8")
public class APPInvestController {

	@Autowired
	private APPInvestService  appInvestService;
	
	/**
	 * 账户信息
	 */
	@RequestMapping(value="/accountInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo accountInfo(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appInvestService.accountInfo(paramsMap);
	}
	
	/**
	 * 赎回操作详细页面
	 */
	@RequestMapping(value="/redeemInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo redeemInfo(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appInvestService.redeemInfo(paramsMap);
	}
	
	/**
	 * 赎回操作
	 */
	@RequestMapping(value="/redeem", method = RequestMethod.POST)
	public @ResponseBody ResultVo redeem(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appInvestService.redeem(paramsMap);
	}
	
	/**
	 * 债权组成列表
	 * 
	 * @param 
	 */
	@RequestMapping(value="/loanList", method = RequestMethod.POST)
	public @ResponseBody ResultVo loanList(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appInvestService.loanList(paramsMap);
	}
	
	/**
	 * 购买活期宝页面详细
	 */
	@RequestMapping(value="/buyDetailToCurrent", method = RequestMethod.POST)
	public @ResponseBody ResultVo buyDetailToCurrent(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appInvestService.buyDetailToCurrent(paramsMap);
	}

	/**
	 * 购买体验宝页面详细
	 */
	@RequestMapping(value="/buyDetailToExperience", method = RequestMethod.POST)
	public @ResponseBody ResultVo buyDetailToExperience(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appInvestService.buyDetailToExperience(paramsMap);
	}
	
	/**
	 * 购买活期宝
	 */
	@RequestMapping(value="/joinCurrentBao", method = RequestMethod.POST)
	public @ResponseBody ResultVo joinCurrentBao(@RequestBody Map<String, Object> params) throws SLException{
		return appInvestService.joinCurrentBao(params);
	}
	
	/**
	 * 购买体验宝
	 */
	@RequestMapping(value="/joinExperienceBao", method = RequestMethod.POST)
	public @ResponseBody ResultVo joinExperienceBao(@RequestBody Map<String, Object> params) throws SLException{
		return appInvestService.joinExperienceBao(params);
	}
	
	/**
	 * 投资列表-体验宝和活期宝信息详细
	 */
	@RequestMapping(value="/investInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo investInfo(@RequestBody Map<String, Object> params) throws SLException{
		return appInvestService.investInfo(params);
	}
	
	/**
	 * 投资列表-加入记录-加入列表
	 */
	@RequestMapping(value="/joinListInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo joinListInfo(@RequestBody Map<String, Object> params) throws SLException{
		return appInvestService.joinListInfo(params);
	}
	
	/**
	 * 投资首页展示用户购买活期宝记录，活期宝和体验宝产品介绍。
	 */
	@RequestMapping(value="/investIndex", method = RequestMethod.POST)
	public @ResponseBody ResultVo investIndex(@RequestBody Map<String, Object> params) throws SLException{
		return appInvestService.investIndex(params);
	}
}
