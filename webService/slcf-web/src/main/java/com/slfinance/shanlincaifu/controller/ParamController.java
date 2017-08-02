/** 
 * @(#)ParamController.java 1.0.0 2015年5月9日 上午9:51:35  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.slfinance.annotation.AutoDispatch;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.ParamService;

/**   
 * 系统参数控制器
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月9日 上午9:51:35 $ 
 */
@RestController
@AutoDispatch(serviceInterface= {ParamService.class})
@RequestMapping("param")
public class ParamController extends WelcomeController{
	
	@Autowired
	private ParamService paramService;
	
	@RequestMapping(value="/{functionName:[a-zA-Z-]+}Auto", method=RequestMethod.POST)
	public @ResponseBody Object dipatch(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestBody Map<String, Object> model) throws SLException{
		return autoDispatch(request, response, name, model);
	}
	@RequestMapping(value="/{functionName:[a-zA-Z-]+}Auto", method=RequestMethod.GET)
	public @ResponseBody Object dipatch2(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestParam Map<String, Object> model) throws SLException{
		return autoDispatch(request, response, name, model);
	}
	
	/**
	 * 取系统参数
	 *
	 * @author  wangjf
	 * @date    2015年5月9日 上午10:03:50
	 * @param request
	 * @param response
	 * @return
	 * 		Map<String, object>:
	  		<tt>investValidTime： String:投资生效时间</tt><br>
	  		<tt>maxDayWithdrawAmount： BigDecimal:每人每天最大可赎回额度</tt><br>
	  		<tt>maxDayWithdrawCount： long:每人每天最大可赎回次数</tt><br>
	  		<tt>maxMonthWithdrawCount： long:每人每月最大可赎回次数</tt><br>
	  		<tt>expireDays： long:体验宝天数</tt><br>
	 * @throws SLException
	 */
	@RequestMapping(value="/queryParm", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> queryLoan(HttpServletRequest request,HttpServletResponse response) throws SLException{
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("investValidTime", paramService.findInvestValidTime());
		result.put("maxDayWithdrawAmount", paramService.findMaxDayWithdrawAmount());
		result.put("maxDayWithdrawCount", paramService.findMaxDayWithdrawCount());
		result.put("maxMonthWithdrawCount", paramService.findMaxMonthWithdrawCount());
		result.put("expireDays", paramService.findExpireDays());
		return result;
	}
}
