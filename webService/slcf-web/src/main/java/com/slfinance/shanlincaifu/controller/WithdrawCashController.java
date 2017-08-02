/** 
 * @(#)WithdrawCashController.java 1.0.0 2015年5月7日 上午10:33:19  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.service.WithDarwTempService;
import com.slfinance.shanlincaifu.service.WithDrawAuditService;
import com.slfinance.shanlincaifu.service.WithdrawCashService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SharedUtil;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.thirdpp.util.ShareConstant;
import com.slfinance.thirdpp.util.ShareUtil;
import com.slfinance.thirdpp.vo.TradeResponseVo;
import com.slfinance.thirdpp.vo.TradeResultVo;
import com.slfinance.vo.ResultVo;

/**   
 * 提现业务控制器
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月7日 上午10:33:19 $ 
 */
@Slf4j
@Controller
@RequestMapping(value="/withdrawCash")
public class WithdrawCashController {

	@Autowired
	private WithdrawCashService withdrawCashService;
	
	@Autowired
	private WithDarwTempService withDrawTempService;
	
	@Autowired
	private WithDrawAuditService withDrawAuditService;
	
	@Autowired
	private SMSService smsService;
	
	/**
	 * 提现管理--列表
	 */
	@RequestMapping(value="/findAllWithdrawCashList", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> findAllWithdrawCashList( @RequestBody Map<String,Object> paramsMap) throws SLException{
		return withdrawCashService.findAllWithdrawCashList(paramsMap);
	}
	
	/**
	 * 提现管理--明细-提现详细
	 */
	@RequestMapping(value="/findWithdrawalCashDetailInfo", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> findWithdrawalCashDetailInfo(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return withdrawCashService.findWithdrawalCashDetailInfo(paramsMap);
	}
	
	/**
	 *  提现管理--明细-审核记录
	 */
	@RequestMapping(value="/findAuditLogByWithDraw", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, Object>> findAuditLogByWithDraw(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return withdrawCashService.findAuditLogByWithDraw(paramsMap);
	}
	
	/**
	 * 提现管理--统计
	 */
	@RequestMapping(value="/findAllWithdrawCashSum", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> findAllWithdrawCashSum(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return withdrawCashService.findAllWithdrawCashSum(paramsMap);
	}
	
	/**
	 * 提现管理--提现审核查询-用户历史提现记录
	 */
	@RequestMapping(value="/findWithdrawCashAuditList", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, Object>> findWithdrawCashAuditList(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return withdrawCashService.findWithdrawCashAuditList(paramsMap);
	}
	
	/**
	 * 提现管理--提现审核查询-用户历史提现记录-分页
	 */
	@RequestMapping(value="/findWithdrawCashAuditListPage", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> findWithdrawCashAuditListPage(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return withdrawCashService.findWithdrawCashAuditListPage(paramsMap);
	}
	
	/**
	 * 提现管理--提现审核提交
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/saveWithdrawCashAudit", method = RequestMethod.POST)
	public @ResponseBody ResultVo saveWithdrawCashAudit(@RequestBody Map<String,Object> paramsMap) throws SLException{
//		return withdrawCashService.saveWithdrawCashAudit(paramsMap);//
//		return withDrawTempService.saveWithdrawCashAudit(paramsMap);//模拟环境
//		return withDrawAuditService.saveWithdrawCashAudit(paramsMap);//重构提现
		ResultVo resultWithDraw = withDrawAuditService.saveWithdrawCashAudit(paramsMap);
		if(ResultVo.isSuccess(resultWithDraw) && null != resultWithDraw.getValue("data")){
			try {
				smsService.asnySendSMS((Map<String,Object>)resultWithDraw.getValue("data"));
			} catch (Exception e) {
				log.error(String.format("%,发送邮件信息失败,%",resultWithDraw.getValue("data") , e.getMessage() != null ? e.getMessage() : e.getCause()));
			}
		}
		return resultWithDraw;
	}
	
	/**
	 * 提现回调方法
	 * @param request
	 * @param response
	 * @return
	 * @throws SLException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/withdraw", method = RequestMethod.POST,produces="text/plain;charset=UTF-8")
	public @ResponseBody String withdraw(HttpServletRequest request, HttpServletResponse response) throws SLException {
		ResultVo callBackResult = new ResultVo(false);
		try {
			log.debug("=====================开始处理tpp提现回调=========================");
			String xml = SharedUtil.receiveInputStream(request.getInputStream());
			TradeResultVo tradeResultVo = ShareUtil.xmlToObject(xml, TradeResultVo.class);
			TradeResponseVo tradeResponseVo = tradeResultVo.getItemVos().get(0);

			Map<String, Object> flowMap = Maps.newHashMap();
			String tradeNo = tradeResponseVo.getTradeCode();
			String tradeCode = tradeResponseVo.getResponseCode();
			flowMap.put("tradeNo", tradeNo);
			flowMap.put("tradeCode", tradeCode);
			switch (tradeCode) {
			case SubjectConstant.TRADE_STATUS_SUCCESS_CODE:
				flowMap.put("status", Constant.TRADE_STATUS_03);
				flowMap.put("auditStatus", Constant.AUDIT_STATUS_PASS);
				flowMap.put("tradeStatus", Constant.TRADE_STATUS_03);
				//第三方处理成功业务处理
//				withdrawCashService.callbackWithdrawCash(flowMap);
//				withDrawAuditService.callbackWithdrawCash(flowMap);//重构提现回调成功
				callBackResult = withDrawAuditService.callbackWithdrawCash(flowMap);//重构提现回调成功
				break;
			default:
				flowMap.put("status", Constant.TRADE_STATUS_04);
				flowMap.put("auditStatus", Constant.AUDIT_STATUS_PASS);
				flowMap.put("tradeStatus", Constant.TRADE_STATUS_04);
				//第三方处理失败业务处理
//				withdrawCashService.callbackWithdrawCashFailed(flowMap);
//				withDrawAuditService.callbackWithdrawCashFailed(flowMap);//重构提现回调失败
				callBackResult = withDrawAuditService.callbackWithdrawCashFailed(flowMap);//重构提现回调失败
				break;
			}
			log.debug("=====================结束处理tpp提现回调=========================");
			
			if(ResultVo.isSuccess(callBackResult) && null != callBackResult.getValue("data")){
				try {
					Map<String,Object> data = (Map<String,Object>)callBackResult.getValue("data");
					if(!StringUtils.isEmpty((String)data.get("mobile"))) {
						smsService.asnySendSMS(data);
					}
				} catch (Exception e) {
					log.error(String.format("%,发送邮件信息失败,%",callBackResult.getValue("data") , e.getMessage() != null ? e.getMessage() : e.getCause()));
				}
			}
			return ShareConstant.TRADE_STATUS_SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * 提现回调方法页面模拟
	 * @param request
	 * @param response
	 * @return
	 * @throws SLException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/withdrawJsp",method = RequestMethod.POST)
	public @ResponseBody String withdraw(String tradeNo, String tradeCode) throws SLException {
		ResultVo callBackResult = new ResultVo(false);
		try {
			log.debug("=====================开始处理tpp提现回调=========================");
			Map<String,Object> flowMap = Maps.newHashMap();
			flowMap.put("tradeNo",tradeNo);
			flowMap.put("tradeCode", tradeCode);
			switch ((String)flowMap.get("tradeCode")) {
			case SubjectConstant.TRADE_STATUS_SUCCESS_CODE:
				flowMap.put("status", Constant.TRADE_STATUS_03);
				flowMap.put("auditStatus", Constant.AUDIT_STATUS_PASS);
				flowMap.put("tradeStatus", Constant.TRADE_STATUS_03);
				//第三方处理成功业务处理
				callBackResult = withdrawCashService.callbackWithdrawCash(flowMap);
				break;
			default:
				flowMap.put("status", Constant.TRADE_STATUS_04);
				flowMap.put("auditStatus", Constant.AUDIT_STATUS_PASS);
				flowMap.put("tradeStatus", Constant.TRADE_STATUS_04);
				//第三方处理失败业务处理
				callBackResult = withdrawCashService.callbackWithdrawCashFailed(flowMap);
				break;
			}
			log.debug("=====================结束处理tpp提现回调=========================");
			if(ResultVo.isSuccess(callBackResult) && null != callBackResult.getValue("data")){
				try {
					smsService.asnySendSMS((Map<String,Object>)callBackResult.getValue("data"));
				} catch (Exception e) {
					log.error(String.format("%,发送邮件信息失败,%",callBackResult.getValue("data") , e.getMessage() != null ? e.getMessage() : e.getCause()));
				}
			}
			return ShareConstant.TRADE_STATUS_SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
}
