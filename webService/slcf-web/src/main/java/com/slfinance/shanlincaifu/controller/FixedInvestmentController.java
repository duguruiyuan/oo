package com.slfinance.shanlincaifu.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.impl.FixedInvestmentService;
import com.slfinance.vo.ResultVo;

/**
 * 
 * @author zhumin
 *
 */
@RestController
@RequestMapping(value = "/fixedInvestmentThird", produces = "application/json;charset=UTF-8")
public class FixedInvestmentController {

	@Autowired
	private FixedInvestmentService fixedInvestmentService;
	
	@RequestMapping(value="/findFixedInvestmentList", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> findFixedInvestmentList(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return fixedInvestmentService.findFixedInvestmentList(paramsMap);
	}
	
	@RequestMapping(value="/findTotalFixedInvestment", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> findTotalFixedInvestment(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return fixedInvestmentService.findTotalFixedInvestment(paramsMap);
	}
	/**
	 * 统计 预期收益 在投金额 累计收益 加入总额
	 * @return
	 */
	@RequestMapping(value="/queryFixedInvestmentCount", method = RequestMethod.POST)
	public @ResponseBody ResultVo queryFixedInvestmentCount(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return fixedInvestmentService.queryFixedInvestmentCount(paramsMap);
	}
	
	/**
	 * 统计 预期收益 在投金额 累计收益 加入总额
	 * @return
	 */
	@RequestMapping(value="/queryFixedInvestment", method = RequestMethod.POST)
	public @ResponseBody ResultVo queryFixedInvestment(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return fixedInvestmentService.queryFixedInvestment(paramsMap);
	}
}
