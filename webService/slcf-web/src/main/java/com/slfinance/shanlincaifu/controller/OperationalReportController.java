package com.slfinance.shanlincaifu.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.OperationalReportService;
import com.slfinance.vo.ResultVo;
/**
 * 
 * <运营报告>
 * <功能详细描述>
 * 
 * @author  yangcheng
 * @version  [版本号, 2017年6月13日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Controller
@RequestMapping("operationalReport")
public class OperationalReportController {
	@Autowired
	private OperationalReportService operationalReportService;
	
	@RequestMapping(value = "/createReport", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody ResultVo createReport(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams){
		ResultVo result = null;
		try {
			result = operationalReportService.createReport(requestParams);
		} catch (SLException e) {
			result = new ResultVo(false,"新建报告出现异常！");
		}
		return result;
	}
	@RequestMapping(value = "/backFindAllReportList", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> backFindAllReportList(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams){

		return operationalReportService.backFindAllReportList(requestParams);
		
	}
	@RequestMapping(value = "/frontFindAllReportList", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> frontFindAllReportList(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams){
		return operationalReportService.frontFindAllReportList(requestParams);
		
	}
	@RequestMapping(value = "/updateReleaseStatus", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody ResultVo updateReleaseStatus(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams){
		ResultVo result = null;
		try {
			result = operationalReportService.updateReleaseStatus(requestParams);
		} catch (SLException e) {
			result = new ResultVo(false,"操作出现异常！");
		}
		return result;
	}
	@RequestMapping(value = "/findByReportId", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> findByReportId(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams){
		
		return operationalReportService.findByReportId(requestParams);
		
	}
	
	@RequestMapping(value = "/findAllReportTime", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody List<Map<String, Object>> findAllReportTime(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams){
		
		return operationalReportService.findAllReportTime(requestParams);
		
	}
}
