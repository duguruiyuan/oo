package com.slfinance.shanlincaifu.controller;

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
import com.slfinance.shanlincaifu.service.LawsAndRegulationsService;
import com.slfinance.vo.ResultVo;
/**
 * 
 * <法律法规>
 * <功能详细描述>
 * 
 * @author  yangcheng
 * @version  [版本号, 2017年6月13日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Controller
@RequestMapping("lawsAndRegulations")
public class LawsAndRegulationsController {
	@Autowired
	private LawsAndRegulationsService lawsAndRegulationsService;
	
	@RequestMapping(value = "/createLawsAndRegulations", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody ResultVo createLawsAndRegulations(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams){
		ResultVo result = null;
		try {
			result = lawsAndRegulationsService.createLawsAndRegulations(requestParams);
		} catch (SLException e) {
			result = new ResultVo(false,"创建出现异常！");
		}
		return result;
		
	}
	@RequestMapping(value = "/updateReleaseStatus", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody ResultVo updateReleaseStatus(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams){
		ResultVo result = null;
		try {
			result = lawsAndRegulationsService.updateReleaseStatus(requestParams);
		} catch (SLException e) {
			result = new ResultVo(false,"操作出现异常！");
		}
		return result;
		
	}
	@RequestMapping(value = "/findAllLawsAndRegulationsList", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> findAllLawsAndRegulationsList(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams){
		return lawsAndRegulationsService.findAllLawsAndRegulationsList(requestParams);
		
	}
	
	@RequestMapping(value = "/backFindAllLawsAndRegulationsList", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> backFindAllLawsAndRegulationsList(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams){
		return lawsAndRegulationsService.backFindAllLawsAndRegulationsList(requestParams);
		
	}
	
	@RequestMapping(value = "/findBylawsAndRegulationsId", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> findBylawsAndRegulationsId(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams){
		return lawsAndRegulationsService.findBylawsAndRegulationsId(requestParams);
		
	}
}
