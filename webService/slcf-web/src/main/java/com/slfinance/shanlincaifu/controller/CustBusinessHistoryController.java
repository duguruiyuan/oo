package com.slfinance.shanlincaifu.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.slfinance.shanlincaifu.service.CustBusinessHistoryService;

/**
 * 报表--数据总览
 * 
 * @author zhangt
 */
@RestController
@RequestMapping(value = "/custBussinessHistory", produces = "application/json;charset=UTF-8")
public class CustBusinessHistoryController {

	@Autowired
	private CustBusinessHistoryService custBusinessHistoryService;
	
	@RequestMapping(value="/findCustBusinessHistoryInfo", method=RequestMethod.POST)
	public @ResponseBody Map<String, Object> findCustBusinessHistoryInfo(@RequestBody Map<String, Object> param) {
		return custBusinessHistoryService.findCustBusinessHistoryInfo(param);
	}

	@RequestMapping(value="/export", method=RequestMethod.POST)
	public @ResponseBody List<Map<String, Object>> export(@RequestBody Map<String, Object> param)  {
		return custBusinessHistoryService.export(param);
	}
	
}
