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
import com.slfinance.shanlincaifu.service.ActivityAmountWithdrawService;
import com.slfinance.vo.ResultVo;

@Controller
@RequestMapping("activityAmountWithdraw")
public class ActivityAmountWithdrawController {
	@Autowired
	private ActivityAmountWithdrawService activityAmountWithdrawService;
	
	
	@RequestMapping(value = "/saveWithdrawApply", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody ResultVo saveWithdrawApply(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams)throws SLException{
		return activityAmountWithdrawService.saveWithdrawApply(requestParams);
	}
	@RequestMapping(value = "/findAllWithdrawAuditList", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> findAllWithdrawAuditList(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException{
		return activityAmountWithdrawService.findAllWithdrawAuditList(requestParams);
	}
	@RequestMapping(value = "/saveWithdrawAudit", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody ResultVo saveWithdrawAudit(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException{
		return activityAmountWithdrawService.saveWithdrawAudit(requestParams);
	}
	@RequestMapping(value = "/saveBatchWithdrawAudit", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody ResultVo saveBatchWithdrawAudit(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException{
		return activityAmountWithdrawService.saveBatchWithdrawAudit(requestParams);
	}
}
