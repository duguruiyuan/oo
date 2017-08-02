package com.slfinance.shanlincaifu.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.impl.RefereeAuditService;
import com.slfinance.vo.ResultVo;

/**
 * 
 * @author zhumin
 *
 *         金牌推荐人
 */
@RestController
@RequestMapping(value = "/refereeAudit", produces = "application/json;charset=UTF-8")
public class RefereeAuditController {
	
	@Autowired
	private RefereeAuditService refereeAuditService;
	
	/**
	 * 金牌推荐人
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/list", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> list(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return refereeAuditService.list(paramsMap);
	}
	
	
	
	/**
	 * 金牌推荐人
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/distributionloanDetail", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> distributionloanDetail(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return refereeAuditService.distributionloanDetail(paramsMap);
	}	
	
	/**
	 * 金牌推荐人操作
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/auditOper", method = RequestMethod.POST)
	public @ResponseBody ResultVo auditOper(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return refereeAuditService.auditOper(paramsMap);
	}	
	
	/**
	 * 添加金牌推荐人操作
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/createOper", method = RequestMethod.POST)
	public @ResponseBody ResultVo createOper(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return refereeAuditService.createOper(paramsMap);
	}
	
	/**
	 * 添加金牌推荐人操作
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/relieveReferee", method = RequestMethod.POST)
	public @ResponseBody ResultVo relieveReferee(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return refereeAuditService.relieveReferee(paramsMap);
	}
}
