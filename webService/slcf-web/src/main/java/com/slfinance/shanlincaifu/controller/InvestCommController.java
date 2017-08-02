package com.slfinance.shanlincaifu.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.InvestCommService;

/**
 * 
 * @author zhangt
 * 活期宝业绩统计
 */
@Controller
@RequestMapping(value = "/investComm", produces = "application/json;charset=UTF-8")
public class InvestCommController {

	@Autowired
	private InvestCommService investCommService;
	
	/**
	 * 业绩列表
	 * @param param
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/findAllInvestCommInfo")
	public @ResponseBody Map<String, Object> findAllInvestCommInfo(@RequestBody Map<String, Object> param) throws SLException {
		return investCommService.findAllInvestCommInfo(param);
	}
	
	/**
	 * 业绩详情
	 * @param param
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/findInvestCommInfoByMonth")
	public @ResponseBody Map<String, Object> findInvestCommInfoByMonth(@RequestBody Map<String, Object> param) throws SLException {
		return investCommService.findInvestCommInfoByMonth(param);
	}
	
	/**
	 * 导出数据
	 * @param param
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/export")
	public @ResponseBody List<Map<String, Object>> export(@RequestBody Map<String, Object> param) throws SLException {
		return investCommService.export(param);
	}
}
