package com.slfinance.shanlincaifu.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.FixedInvestCommNewService;

/**
 * 
 * @author zhangt
 * 报表管理--新定期宝业绩统计
 */
@Controller
@RequestMapping(value = "/fixedInvestCommNew", produces = "application/json;charset=UTF-8")
public class FixedInvestCommNewController {

	@Autowired
	private FixedInvestCommNewService fixedInvestCommNewService;

	/**
	 * 业绩统计
	 * @param param
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/findAllFixedInvestCommInfo")
	public @ResponseBody Map<String, Object> findAllFixedInvestCommInfo(@RequestBody Map<String, Object> param) throws SLException {
		return fixedInvestCommNewService.findAllFixedInvestCommInfo(param);
	}
	
	/**
	 * 统计详细
	 * @param param
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/getFixedInvestCommListByMonth")
	public @ResponseBody Map<String, Object> getFixedInvestCommListByMonth(@RequestBody Map<String, Object> param) throws SLException {
		return fixedInvestCommNewService.getFixedInvestCommListByMonth(param);
	}

	/**
	 * 导出数据
	 * @param param
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/export")
	public @ResponseBody List<Map<String, Object>> export(@RequestBody Map<String, Object> param) throws SLException {
		return fixedInvestCommNewService.export(param);
	}
}
