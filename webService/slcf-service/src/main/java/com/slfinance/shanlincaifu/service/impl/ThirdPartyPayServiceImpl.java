/** 
 * @(#)ThirdPartyPayServiceImpl.java 1.0.0 2015年4月28日 下午6:28:07  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.service.AccountService;
import com.slfinance.shanlincaifu.service.CustomerService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.ThirdPartyPayService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.thirdpp.util.ShareConstant;
import com.slfinance.thirdpp.util.SharePropertyConstant;
import com.slfinance.thirdpp.util.ShareUtil;
import com.slfinance.thirdpp.vo.RealnameAuthVo;
import com.slfinance.thirdpp.vo.SignBankCardInfoVo;
import com.slfinance.thirdpp.vo.TradeInfoVo;
import com.slfinance.thirdpp.vo.TradeRequestInfoVo;
import com.slfinance.thirdpp.vo.TradeResultVo;
import com.slfinance.vo.ResultVo;

/**   
 * 第三方接口实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月28日 下午6:28:07 $ 
 */
@Slf4j
@Service("thirdPartyPayService")
public class ThirdPartyPayServiceImpl implements ThirdPartyPayService {

	@Autowired
	@Qualifier("thirdPartyPayRestClientService")
	private RestOperations slRestClient;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private AccountService accountService;
	
	@Value("${thirdPartyPay.base.request.url}")
	private String thirdPartyPayRequestUrl;
	
	private final String thirdPay = ShareConstant.THIRD_PARTY_TYPE_LLPay; // 第三方支付机构，如连连支付
	
	private final String systemType = ShareConstant.SYSTEM_TYPE_SLCF; // 当前系统名称，如善林财富
			
	
	@Override
	public ResultVo trustRecharge(Map<String, Object> params) throws SLException {
			
		try {
			// 1.构建结算系统请求参数
			CustInfoEntity custInfoEntity = customerService.findByCustId((String)params.get("custId"));
			
			TradeRequestInfoVo tradeRequest = buildTradeRequestInfoVo(thirdPay, systemType, 
					((String)params.get("payType")).equals(Constant.PAY_TYPE_01) ? ("wap".equals(params.get("appSource")) ? ShareConstant.SERVICE_TYPE_ONLINE_AUTH_PAY_WAP : ShareConstant.SERVICE_TYPE_ONLINE_AUTH_PAY) : ShareConstant.SERVICE_TYPE_ONLINE_BANK_PAY, numberService.generateTradeBatchNumber(), ShareConstant.CONSTANT_TYPE_YES, ShareConstant.CONSTANT_TYPE_NO);
			
			TradeInfoVo tradeInfoVo = new TradeInfoVo();
			tradeInfoVo.setTradeCode((String)params.get("tradeCode"));
			tradeInfoVo.setRequestTime(tradeRequest.getRequestTime());
			tradeInfoVo.setTradeAmount(new BigDecimal((String)params.get("tradeAmount")));
			tradeInfoVo.setBankCode((String)params.get("bankNo"));
			tradeInfoVo.setBankCardNo((String)params.get("bankCardNo"));
			tradeInfoVo.setAccountName(custInfoEntity.getCustName());
			tradeInfoVo.setCredentialsCode(custInfoEntity.getCredentialsCode());
			Map<String, Object> otherPropertyMap = new HashMap<String, Object>();
			otherPropertyMap.put("custCode", custInfoEntity.getCustCode());
			otherPropertyMap.put("agreeNo", params.get("agreeNo"));
			otherPropertyMap.put("backUrl", params.get("backUrl"));
			otherPropertyMap.put("payType", params.get("payType"));
			otherPropertyMap.put("custIp", params.get("ipAddress"));
			otherPropertyMap.put("appSource", params.get("appSource"));
			otherPropertyMap.put("registerTime", DateUtils.formatDate(custInfoEntity.getCreateDate(), "yyyyMMddHHmmss"));
			otherPropertyMap.put("wapSource", params.get("wapSource"));
			otherPropertyMap.put("returnUrl", params.get("returnUrl"));
			tradeInfoVo.setOtherPropertyMap(otherPropertyMap);
			
			// 下面为TPP所需
			tradeInfoVo.setCredentialsType(custInfoEntity.getCredentialsType());
			tradeInfoVo.setMobile(custInfoEntity.getMobile());
			tradeInfoVo.setAccountType(Constant.PAY_TYPE_03.equals((String)params.get("payType")) ? Constant.ACCOUNT_TYPE_02 : Constant.ACCOUNT_TYPE_01);
			
			tradeRequest.setItemList(Lists.newArrayList(tradeInfoVo));
			// 3.调用结算系统接口
			String result = slRestClient.postForObject(thirdPartyPayRequestUrl + "/onlinePay", ShareUtil.getXmlLogInfo(tradeRequest), String.class);
			// 4.获取请求响应
			TradeResultVo tradeResultVo = (TradeResultVo) ShareUtil.xmlToObject(result, TradeResultVo.class);
			if (!tradeResultVo.getResponseCode().equals(ShareConstant.TRADE_STATUS_SUCCESS_CODE)) {
				log.warn("充值失败...原因是:{}", tradeResultVo.getResponseInfo());
				throw new SLException("充值失败,失败原因:" + tradeResultVo.getResponseInfo());
			}
			return new ResultVo(true, "充值成功", tradeResultVo.getOtherPropertyMap());			
		} catch (SLException e) {
			log.error("充值失败：" + e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("充值失败：" + e.getMessage());
			throw new SLException("系统异常，请稍后重试!");
		}
	}

	/**
	 * 连连支付提现
	 */
	@Override
	public ResultVo trustWithdrawCash(Map<String, Object> paramsMap) throws SLException {
		// 1.构建提现交易数据
		TradeInfoVo withDrawInfo = new TradeInfoVo();
		withDrawInfo.setRequestTime(ShareUtil.getCurrentTime());
		withDrawInfo.setTradeAmount((BigDecimal)paramsMap.get("tradeAmount"));
		withDrawInfo.setBankCardNo((String)paramsMap.get("bankCardNo"));
		withDrawInfo.setAccountName((String)paramsMap.get("accountName"));
		withDrawInfo.setTradeDesc((String)paramsMap.get("tradeDesc"));
		withDrawInfo.setBankCode((String)paramsMap.get("bankCode"));
		withDrawInfo.setOpenAccountProv((String)paramsMap.get("openAccountProv"));
		withDrawInfo.setOpenAccountCity((String)paramsMap.get("openAccountCity"));
		withDrawInfo.setSubbranchName((String)paramsMap.get("subBranchName"));
		withDrawInfo.setTradeCode((String)paramsMap.get("tradeCode"));
		withDrawInfo.setAccountProperties((String)paramsMap.get("accountProperties"));
		TradeRequestInfoVo tradeRequest = buildTradeRequestInfoVo(thirdPay, systemType, ShareConstant.SERVICE_TYPE_ONLINE_INSTANT_WITHDRAW_DEPOSIT_APPLY, numberService.generateTradeBatchNumber(), ShareConstant.CONSTANT_TYPE_YES, ShareConstant.CONSTANT_TYPE_NO);
		tradeRequest.setItemList(Lists.newArrayList(withDrawInfo));
		// 3.调用结算系统接口
		String result = slRestClient.postForObject(thirdPartyPayRequestUrl + "/onlineInstantWithDrawDeposit", ShareUtil.getXmlLogInfo(tradeRequest), String.class);
		// 4.获取请求响应
		TradeResultVo tradeResultVo = (TradeResultVo) ShareUtil.xmlToObject(result, TradeResultVo.class);
		//5tpp处理提现最后成功返回结果是处理中
		if (!checkState(tradeResultVo)) {
			return new ResultVo(false, "提现失败,失败原因:" + tradeResultVo.getResponseInfo());
		}
		return new ResultVo(true);
		
	}
	
	/**
	 * 连连实名认证
	 */
	@Override
	public ResultVo realNameAuth(Map<String, Object> paramsMap) throws SLException{
		// 1.构建实名认证请求参数
		RealnameAuthVo realNameAuth = new RealnameAuthVo();
		realNameAuth.setCustName((String)paramsMap.get("custName"));
		realNameAuth.setCustCode((String)paramsMap.get("custCode"));
		realNameAuth.setCredentialsCode((String)paramsMap.get("credentialsCode"));
		realNameAuth.setCredentialsType((String)paramsMap.get("credentialsType"));   
		realNameAuth.setTradeCode((String)paramsMap.get("tradeCode"));   
		TradeRequestInfoVo tradeRequest = buildTradeRequestInfoVo((String)paramsMap.get("thirdPartyRealName"), systemType, ShareConstant.SERVICE_TYPE_ONLINE_REAL_NAME_AUTH,(String)paramsMap.get("batchCode"), ShareConstant.CONSTANT_TYPE_YES, ShareConstant.CONSTANT_TYPE_NO);
		tradeRequest.setItemList(Lists.newArrayList(realNameAuth));
		// 3.调用结算系统接口
		String result = slRestClient.postForObject(thirdPartyPayRequestUrl + "/setRealNameAuth", ShareUtil.getXmlLogInfo(tradeRequest), String.class);
		// 4.获取请求响应
		TradeResultVo tradeResultVo = (TradeResultVo) ShareUtil.xmlToObject(result, TradeResultVo.class);
		if (!tradeResultVo.getResponseCode().equals(ShareConstant.TRADE_STATUS_SUCCESS_CODE)) {
			ResultVo busiResult = new ResultVo(false, "实名认证失败,失败原因:" + tradeResultVo.getResponseInfo());
			busiResult.putValue(SharePropertyConstant.REQUEST_IS_SUCCESS, null != tradeResultVo.getOtherPropertyMap() ? tradeResultVo.getOtherPropertyMap().get(SharePropertyConstant.REQUEST_IS_SUCCESS) : null );
			busiResult.putValue("tradeResultVo", tradeResultVo);
			return busiResult;
		}
		return new ResultVo(true);
	}

	/**
	 * 构建交易请求对象
	 * 
	 * @param thirdPartyType
	 *            第三方类型
	 * @param bizSystemType
	 *            业务系统类型
	 * @param tradeType
	 *            交易类型
	 * @param batchCode
	 *            批次号
	 * @param isSync
	 *            是否同步调用
	 * @param isAsyncNotify
	 *            是否异步通知
	 * @return
	 */
	public TradeRequestInfoVo buildTradeRequestInfoVo(String thirdPartyType, String bizSystemType, String tradeType, String batchCode, String isSync, String isAsyncNotify) {
		TradeRequestInfoVo tradeRequest = new TradeRequestInfoVo();
		tradeRequest.setThirdPartyType(thirdPartyType);
		tradeRequest.setBizSystemType(bizSystemType);
		tradeRequest.setTradeType(tradeType);
		tradeRequest.setBatchCode(batchCode == null ? numberService.generateTradeBatchNumber() : batchCode);
		tradeRequest.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		tradeRequest.setIsSync(isSync);
		tradeRequest.setIsAsyncNotify(isAsyncNotify);
		return tradeRequest;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> querySupportBank() throws SLException {
		TradeRequestInfoVo tradeRequest = buildTradeRequestInfoVo(thirdPay, systemType, ShareConstant.SERVICE_TYPE_ONLINE_AUTH_PAY, numberService.generateTradeBatchNumber(), ShareConstant.CONSTANT_TYPE_YES, ShareConstant.CONSTANT_TYPE_NO);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("thirdPartyType", thirdPay);
		params.put("typeValue", "0001");
		tradeRequest.getItemList().add(params);
		
		log.debug("==============开始查询支持的银行===========");
		
		// 3.调用结算系统接口
		String result = slRestClient.postForObject(thirdPartyPayRequestUrl + "/queryParam", ShareUtil.getXmlLogInfo(tradeRequest), String.class);
		// 4.获取请求响应
		TradeResultVo tradeResultVo = (TradeResultVo) ShareUtil.xmlToObject(result, TradeResultVo.class);
		if (!tradeResultVo.getResponseCode().equals(ShareConstant.TRADE_STATUS_SUCCESS_CODE)) {
			log.warn("查询失败...原因是:{}", tradeResultVo.getResponseInfo());
			throw new SLException("查询失败,失败原因:" + tradeResultVo.getResponseInfo());
		}
		log.debug("查询到支持的银行：" + tradeResultVo.getOtherPropertyMap().toString());
		return tradeResultVo.getOtherPropertyMap();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> queryThirdBankByCardNo(
			Map<String, Object> paramsMap) throws SLException {
		TradeRequestInfoVo tradeRequest = buildTradeRequestInfoVo(thirdPay, systemType, ShareConstant.SERVICE_TYPE_BANK_CARD_BIN_QUERY, numberService.generateTradeBatchNumber(), ShareConstant.CONSTANT_TYPE_YES, ShareConstant.CONSTANT_TYPE_NO);
		SignBankCardInfoVo signBankCardInfoVo = new SignBankCardInfoVo();
		signBankCardInfoVo.setBankCardNo((String)paramsMap.get("bankCardNo"));
		tradeRequest.getItemList().add(signBankCardInfoVo);
		
		log.debug("==============开始查询银行卡号===========");
		
		// 3.调用结算系统接口
		String result = slRestClient.postForObject(thirdPartyPayRequestUrl + "/bankCardBinQuery", ShareUtil.getXmlLogInfo(tradeRequest), String.class);
		// 4.获取请求响应
		TradeResultVo tradeResultVo = (TradeResultVo) ShareUtil.xmlToObject(result, TradeResultVo.class);
		if (!tradeResultVo.getResponseCode().equals(ShareConstant.TRADE_STATUS_SUCCESS_CODE)) {
			log.warn("查询银行卡号失败...原因是:{}", tradeResultVo.getResponseInfo());
			throw new SLException("查询银行卡号失败,失败原因:" + tradeResultVo.getResponseInfo());
		}
		log.debug("查询的银行卡信息：" + tradeResultVo.getOtherPropertyMap().toString());
		return tradeResultVo.getOtherPropertyMap();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> queryUserBank(Map<String, Object> paramsMap)
			throws SLException {
		
		String bankCardNo = (String)paramsMap.get("bankCardNo");
		
		TradeRequestInfoVo tradeRequest = buildTradeRequestInfoVo(thirdPay, systemType, ShareConstant.SERVICE_TYPE_SIGN_BANK_CARD_QUERY, numberService.generateTradeBatchNumber(), ShareConstant.CONSTANT_TYPE_YES, ShareConstant.CONSTANT_TYPE_NO);
		SignBankCardInfoVo signBankCardInfoVo = new SignBankCardInfoVo();
		signBankCardInfoVo.setBankCardNo(bankCardNo);
		signBankCardInfoVo.setCustCode((String)paramsMap.get("custCode"));
		signBankCardInfoVo.setResponseCardId((String)paramsMap.get("noAgree"));
		tradeRequest.getItemList().add(signBankCardInfoVo);
		
		log.debug("==============开始查询用户签约信息===========");
		
		// 3.调用结算系统接口
		String result = slRestClient.postForObject(thirdPartyPayRequestUrl + "/signBankCardQuery", ShareUtil.getXmlLogInfo(tradeRequest), String.class);
		// 4.获取请求响应
		TradeResultVo tradeResultVo = (TradeResultVo) ShareUtil.xmlToObject(result, TradeResultVo.class);
		if (!tradeResultVo.getResponseCode().equals(ShareConstant.TRADE_STATUS_SUCCESS_CODE)) {
			log.warn("查询用户签约信息失败...原因是:{}", tradeResultVo.getResponseInfo());
			throw new SLException("查询用户签约信息失败,失败原因:" + tradeResultVo.getResponseInfo());
		}
		log.debug("查询的用户签约信息：" + tradeResultVo.getOtherPropertyMap().toString());
		List<Map<String, Object>> agreementList = (List<Map<String, Object>>)tradeResultVo.getOtherPropertyMap().get("agreementList");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		for(Map<String, Object> m : agreementList){
			if(bankCardNo.substring(bankCardNo.length() - 4, bankCardNo.length()).equals((String)m.get("card_no"))){
				resultMap = m;
			}
		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> appSynAuth(Map<String, Object> paramsMap)
			throws SLException {
		TradeRequestInfoVo tradeRequest = buildTradeRequestInfoVo(thirdPay, systemType, ShareConstant.SERVICE_TYPE_ONLINE_AUTH_PAY, numberService.generateTradeBatchNumber(), ShareConstant.CONSTANT_TYPE_YES, ShareConstant.CONSTANT_TYPE_NO);
		TradeInfoVo tradeInfoVo = new TradeInfoVo();
		tradeInfoVo.setTradeCode((String)paramsMap.get("tradeCode"));
		tradeRequest.getItemList().add(tradeInfoVo);
		
		String result = slRestClient.postForObject(thirdPartyPayRequestUrl + "/appAuthPaySynNotify", ShareUtil.getXmlLogInfo(tradeRequest), String.class);
		TradeResultVo tradeResultVo = (TradeResultVo) ShareUtil.xmlToObject(result, TradeResultVo.class);
		
		return tradeResultVo.getOtherPropertyMap();
	}
	
//----私有方法区-------------------------------------------------------------------------------------------------------------------	
	
	private boolean checkState(TradeResultVo tradeResultVo){
		boolean checkState = false;
		if(null == tradeResultVo)
			return false;
		String reaponseCode = tradeResultVo.getResponseCode();
		//返回结果为空也是空间状态
		if(StringUtils.isEmpty(reaponseCode))
			return true;
		switch (reaponseCode) {
		case ShareConstant.TRADE_STATUS_EXECING_CODE:
			checkState = true;
			break;
		case ShareConstant.TRADE_STATUS_EXCEPTION_CODE:
			checkState = true;
			break;
		default:
			break;
		}
		return checkState;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo unbindUserBank(Map<String, Object> paramsMap)
			throws SLException {
		
		String custCode = (String)paramsMap.get("custCode");
		String noAgree = (String)paramsMap.get("noAgree");
		String tradeCode = (String)paramsMap.get("tradeCode");
		
		TradeRequestInfoVo tradeRequest = buildTradeRequestInfoVo(thirdPay, systemType, ShareConstant.SERVICE_TYPE_RELIEVE_BANK_CARD, numberService.generateTradeBatchNumber(), ShareConstant.CONSTANT_TYPE_YES, ShareConstant.CONSTANT_TYPE_NO);
		SignBankCardInfoVo signBankCardInfoVo = new SignBankCardInfoVo();
		signBankCardInfoVo.setCustCode(custCode);
		signBankCardInfoVo.setResponseCardId(noAgree);
		signBankCardInfoVo.setTradeCode(tradeCode);
		tradeRequest.getItemList().add(signBankCardInfoVo);
		
		log.debug("==============开始查询用户签约信息===========");
		
		// 3.调用结算系统接口
		String result = slRestClient.postForObject(thirdPartyPayRequestUrl + "/relieveBankCard", ShareUtil.getXmlLogInfo(tradeRequest), String.class);
		// 4.获取请求响应
		TradeResultVo tradeResultVo = (TradeResultVo) ShareUtil.xmlToObject(result, TradeResultVo.class);
		if (!tradeResultVo.getResponseCode().equals(ShareConstant.TRADE_STATUS_SUCCESS_CODE)) {
			log.warn("解约失败...原因是:{}", tradeResultVo.getResponseInfo());
			throw new SLException("解约失败,失败原因:" + tradeResultVo.getResponseInfo());
		}

		return new ResultVo(true);
	}

	@Override
	public ResultVo queryRecharge(Map<String, Object> params)
			throws SLException {
		try{	
			TradeRequestInfoVo tradeRequest = buildTradeRequestInfoVo(thirdPay, systemType, ShareConstant.SERVICE_TYPE_ONLINE_AUTH_PAY_QUERY, numberService.generateTradeBatchNumber(), ShareConstant.CONSTANT_TYPE_YES, ShareConstant.CONSTANT_TYPE_NO);
			TradeInfoVo tradeInfoVo = new TradeInfoVo();
			tradeInfoVo.setTradeCode((String)params.get("tradeCode"));
			tradeInfoVo.setRequestTime((String)params.get("tradeDate"));
			tradeRequest.setItemList(Lists.newArrayList(tradeInfoVo));
			
			String result = slRestClient.postForObject(thirdPartyPayRequestUrl + "/onlineTradeQuery", ShareUtil.getXmlLogInfo(tradeRequest), String.class);
			TradeResultVo tradeResultVo = (TradeResultVo) ShareUtil.xmlToObject(result, TradeResultVo.class);
			
			if(tradeResultVo == null||tradeResultVo.getResponseCode() == null) {
				log.error(result);
				log.error("查询充值交易失败,未返回应答信息!");
				return new ResultVo(false);
			}
			
			String tradeStatus = "";
			switch(tradeResultVo.getResponseCode()) {
			case ShareConstant.TRADE_STATUS_SUCCESS_CODE:// 交易成功
				tradeStatus = Constant.TRADE_STATUS_03;
				break;
			case ShareConstant.TRADE_STATUS_FAIL_CODE:// 交易失败
				tradeStatus = Constant.TRADE_STATUS_04;
				break;
			case ShareConstant.TRADE_STATUS_EXECING_CODE:// 正在处理中
				tradeStatus = Constant.TRADE_STATUS_02;
				break;
			case ShareConstant.TRADE_STATUS_EXCEPTION_CODE: // 异常
				tradeStatus = Constant.TRADE_STATUS_02;
				break;
			case ShareConstant.TRADE_STATUS_LEGAL_FAIL_CODE: // 参数错误
				tradeStatus = Constant.TRADE_STATUS_02;
				break;
			}
			
			return new ResultVo(true, tradeResultVo.getResponseInfo(), tradeStatus);
		}
		catch (Exception e) {
			log.error("查询充值交易失败：" + e.getMessage());
			return new ResultVo(false, "系统异常，请稍后重试!");
		}
	}

	@Override
	public ResultVo queryWithdrawCash(Map<String, Object> params)
			throws SLException {
		try{	
			TradeRequestInfoVo tradeRequest = buildTradeRequestInfoVo(thirdPay, systemType, ShareConstant.SERVICE_TYPE_ONLINE_WITHDRAW_DEPOSIT_QUERY, numberService.generateTradeBatchNumber(), ShareConstant.CONSTANT_TYPE_YES, ShareConstant.CONSTANT_TYPE_NO);
			TradeInfoVo tradeInfoVo = new TradeInfoVo();
			tradeInfoVo.setTradeCode((String)params.get("tradeNo"));
			tradeInfoVo.setRequestTime((String)params.get("tradeDate"));
			tradeRequest.setItemList(Lists.newArrayList(tradeInfoVo));
			
			String result = slRestClient.postForObject(thirdPartyPayRequestUrl + "/onlineTradeQuery", ShareUtil.getXmlLogInfo(tradeRequest), String.class);
			TradeResultVo tradeResultVo = (TradeResultVo) ShareUtil.xmlToObject(result, TradeResultVo.class);
			
			if(tradeResultVo == null||tradeResultVo.getResponseCode() == null) {
				log.error(result);
				log.error("查询提现交易失败,未返回应答信息!");
				return new ResultVo(false);
			}
			
			String tradeStatus = "";
			switch(tradeResultVo.getResponseCode()) {
			case ShareConstant.TRADE_STATUS_SUCCESS_CODE:// 交易成功
				tradeStatus = Constant.TRADE_STATUS_03;
				break;
			case ShareConstant.TRADE_STATUS_FAIL_CODE:// 交易失败
				tradeStatus = Constant.TRADE_STATUS_04;
				break;
			case ShareConstant.TRADE_STATUS_EXECING_CODE:// 正在处理中
				tradeStatus = Constant.TRADE_STATUS_02;
				break;
			case ShareConstant.TRADE_STATUS_EXCEPTION_CODE: // 异常
				tradeStatus = Constant.TRADE_STATUS_02;
				break;
			case ShareConstant.TRADE_STATUS_LEGAL_FAIL_CODE: // 参数错误
				tradeStatus = Constant.TRADE_STATUS_02;
				break;
			}
			return new ResultVo(true, "查询提现交易成功", tradeStatus);
		}
		catch (Exception e) {
			log.error("查询提现交易失败：" + e.getMessage());
			return new ResultVo(false, "系统异常，请稍后重试!");
		}
	}
	
	@Override
	public ResultVo queryAccountAmount(Map<String, Object> params)
			throws SLException {
		try{	
			TradeRequestInfoVo tradeRequest = buildTradeRequestInfoVo(thirdPay, systemType, ShareConstant.SERVICE_TYPE_TRADER_ACT_QUERY, numberService.generateTradeBatchNumber(), ShareConstant.CONSTANT_TYPE_YES, ShareConstant.CONSTANT_TYPE_NO);
			TradeInfoVo tradeInfoVo = new TradeInfoVo();
			tradeInfoVo.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
			tradeRequest.setItemList(Lists.newArrayList(tradeInfoVo));
			
			String result = slRestClient.postForObject(thirdPartyPayRequestUrl + "/traderAcctQuery", ShareUtil.getXmlLogInfo(tradeRequest), String.class);
			TradeResultVo tradeResultVo = (TradeResultVo) ShareUtil.xmlToObject(result, TradeResultVo.class);

			if(tradeResultVo == null||tradeResultVo.getResponseCode() == null) {
				log.error(result);
				log.error("查询提现交易失败,未返回应答信息!");
				return new ResultVo(false);
			}
			
			Map<String, Object> data = Maps.newConcurrentMap();

			switch(tradeResultVo.getResponseCode()) {
			case ShareConstant.TRADE_STATUS_SUCCESS_CODE:// 交易成功
				data = tradeResultVo.getOtherPropertyMap();
				break;
			case ShareConstant.TRADE_STATUS_FAIL_CODE:// 交易失败
			case ShareConstant.TRADE_STATUS_EXECING_CODE:// 正在处理中
			case ShareConstant.TRADE_STATUS_EXCEPTION_CODE: // 异常
			case ShareConstant.TRADE_STATUS_LEGAL_FAIL_CODE: // 参数错误
				throw new SLException(tradeResultVo.getResponseInfo());
			}
			return new ResultVo(true, "查询公司余额成功", data);
		}
		catch (Exception e) {
			log.error("查询公司余额失败：" + e.getMessage());
			return new ResultVo(false, "系统异常，请稍后重试!");
		}
	}
	
}
