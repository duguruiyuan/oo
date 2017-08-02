package com.slfinance.shanlincaifu.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.InterfaceDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeLogInfoEntity;
import com.slfinance.shanlincaifu.repository.InterfaceDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanInfoRepository;
import com.slfinance.shanlincaifu.repository.TradeLogInfoRepository;
import com.slfinance.shanlincaifu.service.ExpandInfoService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.OpenThirdService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.Json;
import com.slfinance.shanlincaifu.validate.RuleUtils;
import com.slfinance.vo.ResultVo;

@Slf4j
@Service("openThirdService")
public class OpenThirdServiceImpl implements OpenThirdService {

	@Autowired
	@Qualifier("thirdPartyPayRestClientService")
	private RestOperations slRestClient;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private TradeLogInfoRepository tradeLogInfoRepository;
	
	@Autowired
	private InterfaceDetailInfoRepository interfaceDetailInfoRepository;
	
	@Autowired
	private LoanInfoRepository loanInfoRepository;
	
	@Autowired
	private ExpandInfoService expandInfoService;
	
	@Override
	public ResultVo bindBankNotify(Map<String, Object> params)
			throws SLException {
		
		List<InterfaceDetailInfoEntity> list = interfaceDetailInfoRepository.findByInterfaceType(Constant.NOTIFY_TYPE_LOAN_BIND_CARD);
		for(InterfaceDetailInfoEntity i : list) {
			String bankId = (String)params.get("bankId");
			Map<String, Object> requestParam = Maps.newConcurrentMap();
			requestParam.put("relateType", Constant.TABLE_BAO_T_BANK_CARD_INFO);
			requestParam.put("relatePrimary", bankId);
			requestParam.put("thirdPartyType", i.getThirdPartyType());
			requestParam.put("interfaceType", i.getInterfaceType());
			requestParam.put("userId", Constant.SYSTEM_USER_BACK);
			expandInfoService.saveExpandInfo(requestParam);
		}

		return new ResultVo(true);
	}

	@Override
	public ResultVo unBindBankNotify(Map<String, Object> params)
			throws SLException {

		String bankId = (String)params.get("bankId");

		List<InterfaceDetailInfoEntity> list = interfaceDetailInfoRepository.findByInterfaceType(Constant.NOTIFY_TYPE_LOAN_UNBIND_CARD);
		for(InterfaceDetailInfoEntity i : list) {
			Map<String, Object> requestParam = Maps.newConcurrentMap();
			requestParam.put("relateType", Constant.TABLE_BAO_T_BANK_CARD_INFO);
			requestParam.put("relatePrimary", bankId);
			requestParam.put("thirdPartyType", i.getThirdPartyType());
			requestParam.put("interfaceType", i.getInterfaceType());
			requestParam.put("userId", Constant.SYSTEM_USER_BACK);
			expandInfoService.saveExpandInfo(requestParam);
		}
		
		return new ResultVo(true);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo isRealName(Map<String, Object> params) throws SLException {
		
		List<InterfaceDetailInfoEntity> list = interfaceDetailInfoRepository.findByInterfaceType(Constant.NOTIFY_TYPE_LOAN_QUERY_REAL_NAME);
		for(InterfaceDetailInfoEntity interfaceDetailInfoEntity : list) {
			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("custName", params.get("custName"));
			requestMap.put("idCard", params.get("idCard"));
			
			// 记录通知报文
			String innerTradeCode = numberService.generateOpenServiceTradeNumber();
			TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
			tradeLogInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_CUST_INFO);
			tradeLogInfoEntity.setRelatePrimary((String)params.get("custId"));
			tradeLogInfoEntity.setThirdPartyType(interfaceDetailInfoEntity.getThirdPartyType());
			tradeLogInfoEntity.setInterfaceType(interfaceDetailInfoEntity.getInterfaceType());
			tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
			tradeLogInfoEntity.setTradeCode("");
			tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
			tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(requestMap));
			tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			tradeLogInfoEntity.setMemo("通知");
			tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);
			
			Map<String, Object> responseMap = new HashMap<String, Object>();		
			try {
				responseMap = sendNotify(interfaceDetailInfoEntity.getSyncRedirectUrl(), requestMap);
			} catch (Exception e) {
				log.error("查询失败！" + e.getMessage());
				tradeLogInfoEntity.setResponseMessage(e.getMessage());
				tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
				continue;
			}
			
			tradeLogInfoEntity.setResponseMessage(Json.ObjectMapper.writeValue(responseMap));
			tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			if(Constant.NOTIFY_TYPE_CODE_SUCCESS.equals((String)responseMap.get("code"))) {
				Map<String, Object> result = (Map<String, Object>)responseMap.get("result");
				if(result != null && result.size() > 0 && !StringUtils.isEmpty((String)result.get("custCode"))) {
					return new ResultVo(true, "查询成功", result);
				}
			}
		}
		
		return new ResultVo(false, "查询失败");
	}

	@Override
	public ResultVo login(Map<String, Object> params) throws SLException {
		Map<String, Object> requestMap = Maps.newConcurrentMap();
		requestMap.put("username", "admin");
		requestMap.put("password", "admin123");
		requestMap.put("type", "1");
		
		Map<String, Object> reponseMap = sendNotify("http://192.16.150.71:8080/auth/login", requestMap);
		return new ResultVo(true, "登录成功", reponseMap);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> sendNotify(String url, Map<String, Object> requestMap) {
		
		String t = String.valueOf(new Date().getTime());
		String v = String.valueOf(1);
		String body = Json.ObjectMapper.writeValue(requestMap);
	    String encrytString1 = Hashing.md5().hashString(body + t, Charsets.UTF_8).toString();
	    String encrytString2 = Hashing.md5().hashString(encrytString1 + v, Charsets.UTF_8).toString();
		
		HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.add("t", t);	
        headers.add("v", v);
        headers.add("ip", "0.0.0.0");
        headers.add("cv", "windows");
        headers.add("di", "1234567890");
        headers.add("pf", "windows");
        /*headers.add("at", "");
        headers.add("appKey", "e58591b579fd074b");*/
        headers.add("sign", encrytString2);
        
        HttpEntity<Map<String, Object>> formEntity = new HttpEntity<Map<String, Object>>(requestMap, headers);
        return slRestClient.postForObject(url, formEntity, Map.class);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo nofity(Map<String, Object> params) throws SLException {
				
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType((String)params.get("companyName"), Constant.NOTIFY_TYPE_LOAN_STATUS);
		if(interfaceDetailInfoEntity == null) {
			log.error(String.format("接口未找到！ThirdPartyType:%s, InterfaceType:%s",  (String)params.get("companyName"), Constant.NOTIFY_TYPE_LOAN_STATUS));
			return new ResultVo(false, "接口不存在");
		}
		
		Map<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("loanNo", params.get("loanNo"));
		requestMap.put("loanStatus", params.get("loanStatus"));
		requestMap.put("grantMoneyDate", params.get("grantMoneyDate"));
		requestMap.put("accountManageAmount", params.get("accountManageAmount"));
		if(params.containsKey("result")) {		
			requestMap.put("result", params.get("result"));
		}
		
		// 记录通知报文
		String innerTradeCode = numberService.generateOpenServiceTradeNumber();
		TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
		tradeLogInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_LOAN_INFO);
		tradeLogInfoEntity.setRelatePrimary((String)params.get("loanId"));
		tradeLogInfoEntity.setThirdPartyType(interfaceDetailInfoEntity.getThirdPartyType());
		tradeLogInfoEntity.setInterfaceType(interfaceDetailInfoEntity.getInterfaceType());
		tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		tradeLogInfoEntity.setTradeCode("");
		tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
		tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(requestMap));
		tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		tradeLogInfoEntity.setMemo("通知");
		tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);
		
		Map<String, Object> responseMap = new HashMap<String, Object>();		
		try {
			responseMap = sendNotify(interfaceDetailInfoEntity.getSyncRedirectUrl(), requestMap);
			log.info("资产端{}标的信息{}回馈的报文信息：{}",(String)params.get("companyName"),(String)params.get("loanId"),responseMap.toString());
			if(responseMap == null || StringUtils.isEmpty((String)responseMap.get("code"))) { // 返回信息有误
				throw new SLException("返回的信息有误，缺少域code");
			}

			if(Constant.NOTIFY_TYPE_CODE_SUCCESS.equals((String)responseMap.get("code"))) { // 放款成功
				
				Map<String, Object> result = (Map<String, Object>)responseMap.get("result");
				if(result == null || !result.containsKey("repaymentDayList")) {
					throw new SLException("返回的信息有误，缺少域repaymentDayList");
				}
				List<Map<String, Object>> repaymentList = (List<Map<String, Object>>)result.get("repaymentDayList");
				List<Map<String, Object>> repaymentDayList = Lists.newArrayList();
            /** for测试：还款计划暂时写固定值 begin */
            /*Map<String, Object> map1 = Maps.newConcurrentMap();
            map1.put("currentTerm", 1);
            map1.put("expectRepaymentDate", "20170815");
            repaymentDayList.add(map1);
            Map<String, Object> map2 = Maps.newConcurrentMap();
            map2.put("currentTerm", 2);
            map2.put("expectRepaymentDate", "20170915");
            repaymentDayList.add(map2);
            Map<String, Object> map3 = Maps.newConcurrentMap();
            map3.put("currentTerm", 3);
            map3.put("expectRepaymentDate", "20171015");
            repaymentDayList.add(map3);
            Map<String, Object> map4 = Maps.newConcurrentMap();
            map4.put("currentTerm", 4);
            map4.put("expectRepaymentDate", "20171115");
            repaymentDayList.add(map4);
            Map<String, Object> map5 = Maps.newConcurrentMap();
            map5.put("currentTerm", 5);
            map5.put("expectRepaymentDate", "20171215");
            repaymentDayList.add(map5);
            Map<String, Object> map6 = Maps.newConcurrentMap();
            map6.put("currentTerm", 6);
            map6.put("expectRepaymentDate", "20180115");
            repaymentDayList.add(map6);*/
            /** for测试：还款计划暂时写固定值 end */

				for(Map<String, Object> m : repaymentList) {
					if(!RuleUtils.required(m.get("currentTerm"))
							|| !RuleUtils.required(m.get("expectRepaymentDate"))) {
						throw new SLException("返回的信息有误，缺少域currentTerm或expectRepaymentDate");
					}
					Map<String, Object> repayment = Maps.newConcurrentMap();
					repayment.put("currentTerm", Integer.parseInt(m.get("currentTerm").toString()));
					if (m.get("expectRepaymentDate") instanceof String) {
						repayment.put("expectRepaymentDate", m.get("expectRepaymentDate").toString());
					}else{
						repayment.put("expectRepaymentDate", DateUtils.formatDate(new Date(Long.parseLong(m.get("expectRepaymentDate").toString())), "yyyyMMdd"));
					}
					repaymentDayList.add(repayment);
				}
				
				resultMap.put("grantStatus", Constant.GRANT_STATUS_02);
				resultMap.put("repaymentList", repaymentDayList);
			}
			else {
				resultMap.put("grantStatus", Constant.GRANT_STATUS_03);
			}
		}
		catch (Exception e) {
			log.error("放款通知失败！" + e.getMessage());
			tradeLogInfoEntity.setResponseMessage(e.getMessage());
			tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			return new ResultVo(false, e.getMessage());
		}
		
		tradeLogInfoEntity.setResponseMessage(Json.ObjectMapper.writeValue(responseMap));
		tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
				
		return new ResultVo(true, "通知成功", resultMap);
	}
	
	
}
