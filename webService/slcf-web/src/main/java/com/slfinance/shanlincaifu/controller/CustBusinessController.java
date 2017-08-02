/** 
 * @(#)CustBusinessController.java 1.0.0 2015年12月11日 上午11:14:59  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.slfinance.annotation.AutoDispatch;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.CustBusinessService;

/**   
 * 客户业务信息访问控制层
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年12月11日 上午11:14:59 $ 
 */
@RestController
@AutoDispatch(serviceInterface= {CustBusinessService.class})
@RequestMapping("business")
public class CustBusinessController extends WelcomeController {

	@RequestMapping("/{functionName:[a-zA-Z-]+}Auto")
	public @ResponseBody Object dipatch(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestBody Map<String, Object> model) throws SLException{
		return autoDispatch(request, response, name, model);
	}
	@RequestMapping(value="/{functionName:[a-zA-Z-]+}Auto", method=RequestMethod.GET)
	public @ResponseBody Object dipatch2(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestParam Map<String, Object> model) throws SLException{
		return autoDispatch(request, response, name, model);
	}
	
}
