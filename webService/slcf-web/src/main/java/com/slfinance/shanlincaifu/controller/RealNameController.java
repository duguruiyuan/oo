/** 
 * @(#)UserEmailController.java 1.0.0 2015年4月22日 下午7:26:19  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller;

import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.slfinance.annotation.AutoDispatch;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.RealNameAuthenticationService;
import com.slfinance.vo.ResultVo;

/**   
 * 实名认证业务操作控制器
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月22日 下午7:26:19 $ 
 */
@RestController
@AutoDispatch(serviceInterface= {RealNameAuthenticationService.class})
@RequestMapping(value="/realName", produces="application/json;charset=UTF-8")
public class RealNameController  extends WelcomeController {

	protected static final Logger logger = LoggerFactory.getLogger(RealNameController.class);
	
	@Autowired
	private RealNameAuthenticationService realNameService;

	@RequestMapping(value="/{functionName:[a-zA-Z-]+}Auto",  method=RequestMethod.POST)
	public @ResponseBody Object dipatch(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestBody Map<String, Object> model)  throws SLException{
		return autoDispatch(request, response, name, model);
	}
	
	/**
	 * 实名认证次数
	 * 
	 * @author zhangzs
	 * @param Map
	 * 	          <tt>custId:String:客户主键ID</tt>
	 * @return 
	 */
	@RequestMapping(value="/realNameCount", method = RequestMethod.POST)
	public @ResponseBody BigDecimal getRealNameAuthCount(@RequestBody Map<String, Object> paramsMap) throws SLException{
		try {
			return realNameService.getRealNameAuthCount(paramsMap);
		} catch (Exception e) {
			throw new SLException(e.getMessage());
		}
	}
	
	/**
	 * 实名认证次数APP
	 * 
	 * @author zhangzs
	 * @param Map
	 * 	          <tt>custId:String:客户主键ID</tt>
	 * @return 
	 */
	@RequestMapping(value="/countRealName", method = RequestMethod.POST)
	public @ResponseBody ResultVo getCountRealNameAuth(@RequestBody Map<String, Object> paramsMap) throws SLException{
		try {
			return new ResultVo(true, "实名认证次数", "{\"count\":"+realNameService.getRealNameAuthCount(paramsMap)+"}");
		} catch (Exception e) {
			throw new SLException(e.getMessage());
		}
	}
	
}
