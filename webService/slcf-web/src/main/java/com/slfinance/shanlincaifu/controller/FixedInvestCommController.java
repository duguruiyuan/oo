package com.slfinance.shanlincaifu.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.FixedInvestCommService;

@Controller
@RequestMapping(value = "/fixedInvestComm", produces = "application/json;charset=UTF-8")
public class FixedInvestCommController {
	@Autowired
	private FixedInvestCommService fixedInvestCommService;

	@RequestMapping(value="/findAllFixedInvestCommInfo")
	public @ResponseBody Map<String, Object> findAllFixedInvestCommInfo(@RequestBody Map<String, Object> param) throws SLException {
		return fixedInvestCommService.findAllFixedInvestCommInfo(param);
	}
	
	@RequestMapping(value="/getFixedInvestCommListByMonth")
	public @ResponseBody Map<String, Object> getFixedInvestCommListByMonth(@RequestBody Map<String, Object> param) throws SLException {
		return fixedInvestCommService.getFixedInvestCommListByMonth(param);
	}

	@RequestMapping(value="/export")
	public @ResponseBody List<Map<String, Object>> export(@RequestBody Map<String, Object> param) throws SLException {
		return fixedInvestCommService.export(param);
	}
}
