/** 
 * @(#)ProjectRepaymentController.java 1.0.0 2016年1月12日 上午10:35:00  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller;

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
import com.slfinance.shanlincaifu.service.ProjectPaymentService;
import com.slfinance.shanlincaifu.service.ProjectRepaymentService;
import com.slfinance.vo.ResultVo;

/**   
 * 企业借款还款控制访问层
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月12日 上午10:35:00 $ 
 */
@RestController
@AutoDispatch(serviceInterface= {ProjectRepaymentService.class})
@RequestMapping("/repayment")
public class ProjectRepaymentController extends WelcomeController {

	@Autowired
	private ProjectPaymentService projectPaymentService;
	
	@RequestMapping("/{functionName:[a-zA-Z-]+}Auto")
	public @ResponseBody Object dipatch(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestBody Map<String, Object> model) throws SLException{
		return super.autoDispatch(request, response, name, model);
	}
	
	@RequestMapping(value="/{functionName:[a-zA-Z-]+}Auto", method=RequestMethod.GET)
	public @ResponseBody Object dipatch2(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestParam Map<String, Object> model) throws SLException{
		return autoDispatch(request, response, name, model);
	}
	
	@RequestMapping(value="/queryPaymentList", method = RequestMethod.POST)
	public @ResponseBody ResultVo queryPaymentList(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return projectPaymentService.queryPaymentList(paramsMap);
	}
}
