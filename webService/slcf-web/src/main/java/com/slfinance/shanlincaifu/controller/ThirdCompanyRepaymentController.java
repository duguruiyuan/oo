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
import com.slfinance.shanlincaifu.service.ThirdCompanyRepaymentService;

/**
 * 商户还款
 * 
 * @author zhangt
 * @date 2016年11月30日上午9:38:17
 */
@RestController
@AutoDispatch(serviceInterface= {ThirdCompanyRepaymentService.class})
@RequestMapping("thirdCompanyRepayment")
public class ThirdCompanyRepaymentController extends WelcomeController {

	@RequestMapping(value="/{functionName:[a-zA-Z-]+}Auto", method=RequestMethod.POST)
	public @ResponseBody Object dipatch(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestBody Map<String, Object> model) throws SLException {
		return autoDispatch(request, response, name, model);
	}
	@RequestMapping(value="/{functionName:[a-zA-Z-]+}Auto", method=RequestMethod.GET)
	public @ResponseBody Object dipatch2(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestParam Map<String, Object> model) throws SLException {
		return autoDispatch(request, response, name, model);
	}
}
