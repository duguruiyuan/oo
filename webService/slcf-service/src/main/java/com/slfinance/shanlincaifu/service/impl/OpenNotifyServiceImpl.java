/** 
 * @(#)OpenNotifyServiceImpl.java 1.0.0 2015年7月2日 下午3:24:29  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestOperations;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.BankCardInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.ExpandInfoEntity;
import com.slfinance.shanlincaifu.entity.InterfaceDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeLogInfoEntity;
import com.slfinance.shanlincaifu.repository.BankCardInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.ExpandInfoRepository;
import com.slfinance.shanlincaifu.repository.InterfaceDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.TradeFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.TradeLogInfoRepository;
import com.slfinance.shanlincaifu.service.BankCardService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.LoanProjectService;
import com.slfinance.shanlincaifu.service.OpenNotifyService;
import com.slfinance.shanlincaifu.service.OpenThirdService;
import com.slfinance.shanlincaifu.utils.AesUtil;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.Json;
import com.slfinance.vo.ResultVo;

/**   
 * 对外异步通知实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月2日 下午3:24:29 $ 
 */
@Slf4j
@Service("openNotifyService")
public class OpenNotifyServiceImpl implements OpenNotifyService {

	@Autowired
	private ExpandInfoRepository expandInfoRepository;

	@Override
	public void asynNotify() throws SLException {
		
/*		// 1) 点赞网充值通知
		List<ExpandInfoEntity> rechargeList = expandInfoRepository.findByAlreadyNotifyTimes(Constant.THIRD_PARTY_TYPE_DIANZAN, Constant.OPERATION_TYPE_05, Constant.TRADE_STATUS_03, 4);
		notify(rechargeList, Constant.OPERATION_TYPE_05);
		
		// 2) 点赞网注册通知
		List<ExpandInfoEntity> registerList = expandInfoRepository.findByAlreadyNotifyTimes(Constant.THIRD_PARTY_TYPE_DIANZAN, Constant.OPERATION_TYPE_16, Constant.TRADE_STATUS_03, 4, "waiting");
		notify(registerList, Constant.OPERATION_TYPE_16);
		
		// 3) 巨宝朋下载通知
		List<ExpandInfoEntity> downlodList = expandInfoRepository.findByMeidAndAlreadyNotifyTimes(Constant.THIRD_PARTY_TYPE_JUPENG, Constant.OPERATION_TYPE_24, Constant.TRADE_STATUS_03, 4);
		notify(downlodList, Constant.OPERATION_TYPE_24);
		
		// 4) 吉融通注册通知
		List<ExpandInfoEntity> jrtRegisterList = expandInfoRepository.findByAlreadyNotifyTimes(Constant.THIRD_PARTY_TYPE_JIRONGTONG, Constant.OPERATION_TYPE_16, Constant.TRADE_STATUS_03, 4);
		notify(jrtRegisterList, Constant.OPERATION_TYPE_16);
		
		// 5) 吉融通购买定期宝通知
		List<ExpandInfoEntity> jrtInvestList = expandInfoRepository.findByAlreadyNotifyTimes(Constant.THIRD_PARTY_TYPE_JIRONGTONG, Constant.OPERATION_TYPE_21, Constant.TRADE_STATUS_03, 4);
		notify(jrtInvestList, Constant.OPERATION_TYPE_21);
		
		// 6) 掌上互动注册通知
		List<ExpandInfoEntity> zshdDownlodList = expandInfoRepository.findByMeidAndAlreadyNotifyTimes(Constant.THIRD_PARTY_TYPE_ZHANGSHANG, Constant.OPERATION_TYPE_24, Constant.TRADE_STATUS_03, 4);
		notify(zshdDownlodList, Constant.OPERATION_TYPE_24);*/
		
		// 7) 善林商务项目状态通知
		List<ExpandInfoEntity> sendProjectList = expandInfoRepository.findNeedSendRecord(Constant.NOTIFY_TYPE_LOAN_STATUS, Constant.TRADE_STATUS_03, 4);
		notify(sendProjectList, Constant.NOTIFY_TYPE_LOAN_STATUS);
		
		// 8) 善林商务绑卡通知
		List<ExpandInfoEntity> sendBindCardList = expandInfoRepository.findNeedSendRecord(Constant.NOTIFY_TYPE_LOAN_BIND_CARD, Constant.TRADE_STATUS_03, 4);
		notify(sendBindCardList, Constant.NOTIFY_TYPE_LOAN_BIND_CARD);
		
		// 9) 善林商务解绑通知
		List<ExpandInfoEntity> sendUnBindCardList = expandInfoRepository.findNeedSendRecord(Constant.NOTIFY_TYPE_LOAN_UNBIND_CARD, Constant.TRADE_STATUS_03, 4);
		notify(sendUnBindCardList, Constant.NOTIFY_TYPE_LOAN_UNBIND_CARD);
	}
	
	public void notify(List<ExpandInfoEntity> list, String operType) throws SLException {
		for(ExpandInfoEntity e : list) { 
			
			int alreadyNotifyTimes = e.getAlreadyNotifyTimes();
			switch(alreadyNotifyTimes) { // 判断通知时间是否合适
			case 0:// 无需间隔
				break;
			case 1:// 间隔五分钟发
				if(DateUtils.minutePhaseDiffer(e.getLastUpdateDate(), new Date()) < 5) {
					continue;
				}
				break;
				
			case 2:// 间隔30分钟发
				if(DateUtils.minutePhaseDiffer(e.getLastUpdateDate(), new Date()) < 30) {
					continue;
				}
				break;
				
			case 3:// 间隔2小时发
				if(DateUtils.minutePhaseDiffer(e.getLastUpdateDate(), new Date()) < 120) {
					continue;
				}
				break;
			}
			
			try
			{
				switch(operType){
				case Constant.OPERATION_TYPE_16: // 注册	
					switch (e.getThirdPartyType()) {
					case Constant.THIRD_PARTY_TYPE_DIANZAN: //点赞网
						internalOpenNotifyService.singleAsynNotify(e);
						break;
					case Constant.THIRD_PARTY_TYPE_JIRONGTONG: //吉融通
						internalOpenNotifyService.jrtSingleAsynNotify(e);
						break;
					}
					break;
				case Constant.OPERATION_TYPE_05: // 充值
					internalOpenNotifyService.singleAsynNotifyRecharge(e);
					break;
				case Constant.OPERATION_TYPE_24: //下载
					switch(e.getThirdPartyType()) {
					case Constant.THIRD_PARTY_TYPE_JUPENG: //巨宝朋
						internalOpenNotifyService.singleDownloadAsynNotify(e);
						break;
					case Constant.THIRD_PARTY_TYPE_ZHANGSHANG: //掌上互动
						internalOpenNotifyService.zshdSingleDownloadAsynNotify(e);
						break;	
					}
					break;
				case Constant.OPERATION_TYPE_21: //吉融通购买定期宝
					internalOpenNotifyService.jrtSingleAsynNotify(e);
					break;
				case Constant.NOTIFY_TYPE_LOAN_STATUS:// 善林商务通知
					internalOpenNotifyService.thirdProjectAsynNotify(e);
					break;
				case Constant.NOTIFY_TYPE_LOAN_BIND_CARD:// 善林商务绑卡通知
				case Constant.NOTIFY_TYPE_LOAN_UNBIND_CARD:// 善林商务解绑通知
					internalOpenNotifyService.bindCardAsynNotify(e);
					break;
				}
			}
			catch(Exception ex) {
				log.error("发送消息失败：" + ex.getMessage());
			}
		}
	}
	
	@Autowired
	private InternalOpenNotifyService internalOpenNotifyService;
	
	@Service
	public static class InternalOpenNotifyService {
		
		@PersistenceContext
		private EntityManager manager;
		
		@Autowired
		private ExpandInfoRepository expandInfoRepository;
		
		@Autowired
		@Qualifier("thirdPartyPayRestClientService")
		private RestOperations slRestClient;
		
		@Autowired
		private InterfaceDetailInfoRepository interfaceDetailInfoRepository;
		
		@Autowired
		private CustInfoRepository custInfoRepository;
		
		@Autowired
		private FlowNumberService numberService;
		
		@Autowired
		private TradeLogInfoRepository tradeLogInfoRepository;
		
		@Autowired
		private TradeFlowInfoRepository tradeFlowInfoRepository;
		
		@Autowired
		private InvestInfoRepository investInfoRepository;
		
		@Autowired
		private LoanProjectService loanProjectService;
		
		@Autowired
		private OpenThirdService openThirdService;
		
		@Autowired
		private BankCardInfoRepository bankCardInfoRepository;
		
		@Autowired
		private BankCardService bankCardService;
		
		/**
		 * 点赞网充值结果通知
		 *
		 * @author  wangjf
		 * @date    2015年10月27日 上午11:09:49
		 * @param e
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void singleAsynNotifyRecharge(ExpandInfoEntity e)
				throws SLException {
			
			// 将游离对象变为受控对象
			e = manager.merge(e);
			
			int alreadyNotifyTimes = e.getAlreadyNotifyTimes();
			e.setAlreadyNotifyTimes(++alreadyNotifyTimes);
			e.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyMerchantCode(e.getThirdPartyType(), e.getInterfaceType(), e.getMerchantCode());
			if(interfaceDetailInfoEntity == null) {
				log.error(String.format("接口未找到！ThirdPartyType:%s, InterfaceType:%s, MerchantCode:%s", e.getThirdPartyType(), e.getInterfaceType(), e.getMerchantCode()));
				return;
			}
			
			TradeFlowInfoEntity tradeFlowInfoEntity = tradeFlowInfoRepository.findOne(e.getRelatePrimary());
			if(tradeFlowInfoEntity == null) {
				log.error(String.format("交易记录未找到！tradeId:%s", e.getRelatePrimary()));
				return;
			}
			
			CustInfoEntity custInfoEntity = custInfoRepository.findOne(tradeFlowInfoEntity.getCustId());
			if(custInfoEntity == null) {
				log.error(String.format("用户未找到！custId:%s", tradeFlowInfoEntity.getCustId()));
				return;
			}
			
			BigDecimal total = expandInfoRepository.sumByCustIdAndTradeCode(custInfoEntity.getId(), e.getThirdPartyType(), Constant.OPERATION_TYPE_05, e.getTradeCode());
			if(total == null || total.compareTo(new BigDecimal("1")) < 0) { // 总充值金额小于1元，未满足条件不通知
				return;
			}
			
			String type = "1"; // 如果utid为dianzan.it提供，则type取1.如果utid为合作方事先提供，则type取2
			String hashString = "dzjj" + custInfoEntity.getMobile() + e.getTradeCode() + type;
			String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mobile", custInfoEntity.getMobile());
			map.put("utid", e.getTradeCode());
			map.put("type", type);
			map.put("token", sign);
			String result = "F";
			
			try
			{			
				result = slRestClient.getForObject(String.format("%s?%s", interfaceDetailInfoEntity.getAsyncNotifyUrl(), CommonUtils.getUrlParamsByMap(map)), String.class, map);
			}
			catch(Exception ex) {
				log.error(ex.getMessage());
			}
			
			if(result.equals("T")) {
				e.setExecStatus(Constant.TRADE_STATUS_03);
				// 将所有充值状态改为处理成功
				List<ExpandInfoEntity> list = expandInfoRepository.findByCustIdAndTradeCode(custInfoEntity.getId(), e.getThirdPartyType(), Constant.OPERATION_TYPE_05, e.getTradeCode());
				for(ExpandInfoEntity expandInfoEntity : list){
					expandInfoEntity.setExecStatus(Constant.TRADE_STATUS_03);
					expandInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
				}
			}
			else {
				e.setExecStatus(Constant.TRADE_STATUS_04);
			}
			
			// 记录通知报文
			String innerTradeCode = numberService.generateOpenServiceTradeNumber();
			TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
			tradeLogInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_EXPAND_INFO);
			tradeLogInfoEntity.setRelatePrimary(e.getId());
			tradeLogInfoEntity.setThirdPartyType(Constant.THIRD_PARTY_TYPE_SHANLIN);
			tradeLogInfoEntity.setInterfaceType(Constant.OPERATION_TYPE_05);
			tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
			tradeLogInfoEntity.setTradeCode("");
			tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
			tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(map));
			tradeLogInfoEntity.setResponseMessage(result);
			tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);
		}

		/**
		 * 点赞网注册结果通知
		 *
		 * @author  wangjf
		 * @date    2015年10月27日 上午11:10:19
		 * @param e
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void singleAsynNotify(ExpandInfoEntity e)
				throws SLException {
			
			// 将游离对象变为受控对象
			e = manager.merge(e);
			
			int alreadyNotifyTimes = e.getAlreadyNotifyTimes();
			e.setAlreadyNotifyTimes(++alreadyNotifyTimes);
			e.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyMerchantCode(e.getThirdPartyType(), e.getInterfaceType(), e.getMerchantCode());
			if(interfaceDetailInfoEntity == null) {
				log.error(String.format("接口未找到！ThirdPartyType:%s, InterfaceType:%s, MerchantCode:%s", e.getThirdPartyType(), e.getInterfaceType(), e.getMerchantCode()));
				return;
			}
			
			CustInfoEntity custInfoEntity = custInfoRepository.findOne(e.getRelatePrimary());
			if(custInfoEntity == null) {
				log.error(String.format("用户未找到！custId:%s", e.getRelatePrimary()));
				return;
			}
			String type = "1"; // 如果utid为dianzan.it提供，则type取1.如果utid为合作方事先提供，则type取2
			String hashString = "dzjj" + custInfoEntity.getMobile() + e.getTradeCode() + type;
			String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mobile", custInfoEntity.getMobile());
			map.put("utid", e.getTradeCode());
			map.put("type", type);
			map.put("token", sign);
			String result = "F";	
			try
			{			
				result = slRestClient.getForObject(String.format("%s?%s", interfaceDetailInfoEntity.getAsyncNotifyUrl(), CommonUtils.getUrlParamsByMap(map)), String.class, map);
			}
			catch(Exception ex) {
				log.error(ex.getMessage());
			}
			
			if(result.equals("T")) {
				e.setExecStatus(Constant.TRADE_STATUS_03);
			}
			else {
				e.setExecStatus(Constant.TRADE_STATUS_04);
			}
			
			// 记录通知报文
			String innerTradeCode = numberService.generateOpenServiceTradeNumber();
			TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
			tradeLogInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_EXPAND_INFO);
			tradeLogInfoEntity.setRelatePrimary(e.getId());
			tradeLogInfoEntity.setThirdPartyType(Constant.THIRD_PARTY_TYPE_SHANLIN);
			tradeLogInfoEntity.setInterfaceType(Constant.OPERATION_TYPE_16);
			tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
			tradeLogInfoEntity.setTradeCode("");
			tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
			tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(map));
			tradeLogInfoEntity.setResponseMessage(result);
			tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);
		}

		/**
		 * 巨宝朋注册结果通知
		 *
		 * @author  zhangt
		 * @date    2015年10月27日 上午11:10:38
		 * @param e
		 * @throws SLException
		 */
		@SuppressWarnings("unchecked")
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void singleDownloadAsynNotify(ExpandInfoEntity e)
				throws SLException {
			
			// 将游离对象变为受控对象
			e = manager.merge(e);
			
			int alreadyNotifyTimes = e.getAlreadyNotifyTimes();
			e.setAlreadyNotifyTimes(++alreadyNotifyTimes);
			e.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyMerchantCode(e.getThirdPartyType(), e.getInterfaceType(), e.getMerchantCode());
			if(interfaceDetailInfoEntity == null) {
				log.error(String.format("接口未找到！ThirdPartyType:%s, InterfaceType:%s, MerchantCode:%s", e.getThirdPartyType(), e.getInterfaceType(), e.getMerchantCode()));
				return;
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("appid", e.getTradeCode());
			map.put("idfa", e.getMeId());
			map.put("mac", "");		
			Map<String, Object> resMap = new HashMap<String, Object>();
			try
			{			
				resMap = slRestClient.getForObject(String.format("%s?%s", interfaceDetailInfoEntity.getAsyncNotifyUrl(), CommonUtils.getUrlParamsByMap(map)), Map.class, map);
			}
			catch(Exception ex) {
				log.error(ex.getMessage());
			}
			String result = (String) resMap.get("success");
			if("true".equals(result)) {
				e.setExecStatus(Constant.TRADE_STATUS_03);
			} else {
				e.setExecStatus(Constant.TRADE_STATUS_04);
			}
			
			// 记录通知报文
			String innerTradeCode = numberService.generateOpenServiceTradeNumber();
			TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
			tradeLogInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_EXPAND_INFO);
			tradeLogInfoEntity.setRelatePrimary(e.getId());
			tradeLogInfoEntity.setThirdPartyType(Constant.THIRD_PARTY_TYPE_JUPENG);
			tradeLogInfoEntity.setInterfaceType(Constant.OPERATION_TYPE_24);
			tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
			tradeLogInfoEntity.setTradeCode("");
			tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
			tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(map));
			tradeLogInfoEntity.setResponseMessage(Json.ObjectMapper.writeValue(resMap));
			tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);
		}
	 
		/**
		 * 吉融通注册/购买定期宝结果通知
		 *
		 * @author  zhangt
		 * @date    2015年10月27日 上午11:11:25
		 * @param e
		 * @throws SLException
		 */
		@SuppressWarnings("unchecked")
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void jrtSingleAsynNotify(ExpandInfoEntity e) 
				throws SLException{
			
			// 将游离对象变为受控对象
			e = manager.merge(e);
			
			int alreadyNotifyTimes = e.getAlreadyNotifyTimes();
			e.setAlreadyNotifyTimes(++alreadyNotifyTimes);
			e.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyMerchantCode(e.getThirdPartyType(), e.getInterfaceType(), e.getMerchantCode());
			if(interfaceDetailInfoEntity == null) {
				log.error(String.format("接口未找到！ThirdPartyType:%s, InterfaceType:%s, MerchantCode:%s", e.getThirdPartyType(), e.getInterfaceType(), e.getMerchantCode()));
				return;
			}
			
			String cusId = "";
			if(Constant.OPERATION_TYPE_16.equals(e.getInterfaceType())){
				cusId = e.getRelatePrimary();
			} else if (Constant.OPERATION_TYPE_21.equals(e.getInterfaceType())){
				InvestInfoEntity investInfoEntity = investInfoRepository.findOne(e.getRelatePrimary());
				if(investInfoEntity == null) {
					log.error(String.format("投资未找到！investId:%s", e.getRelatePrimary()));
					return;
				}
				cusId = investInfoEntity.getCustId();
			}
			CustInfoEntity custInfoEntity = custInfoRepository.findOne(cusId);
			if(custInfoEntity == null) {
				log.error(String.format("用户未找到！custId:%s", cusId));
				return;
			}
			
			Map<String, Object> reqOrderMap = new HashMap<String, Object>();
			Map<String, Object> reqVerifiMap = new HashMap<String, Object>();
			Map<String, Object> resOrderMap = new HashMap<String, Object>();
			Map<String, Object> resVerifiMap = new HashMap<String, Object>();
			Map<String, Object> orderMap = new HashMap<String, Object>();
			Map<String, Object> verifiMap = new HashMap<String, Object>();
			String [] interface_desces = interfaceDetailInfoEntity.getInterfaceDesc().trim().split("\\|");
			String [] secretKey = interfaceDetailInfoEntity.getSecretKey().trim().split("\\|");
			orderMap.put("request_no", e.getInnerTradeCode());
			orderMap.put("plat_offer_id", interface_desces[0]);
			orderMap.put("phone_id", custInfoEntity.getMobile());
			orderMap.put("meid", e.getMeId());
			orderMap.put("activity_id", interface_desces[1]);
			orderMap.put("order_type", 1);
			orderMap.put("channel_id", interface_desces[2]);
			//加密map
			String orderCode = AesUtil.encodeBytes(
					AesUtil.cbc128Encrypt(secretKey[1], secretKey[0], Json.ObjectMapper.writeValue(orderMap)));
			reqOrderMap.put("partner_no", e.getMerchantCode());
			reqOrderMap.put("code", orderCode);
			verifiMap.put("request_no", e.getInnerTradeCode());
			verifiMap.put("phone_id", custInfoEntity.getMobile());
			verifiMap.put("meid", e.getMeId());
			verifiMap.put("request_type", "3");
			String verifiCode = AesUtil.encodeBytes(
					AesUtil.cbc128Encrypt(secretKey[1], secretKey[0], Json.ObjectMapper.writeValue(verifiMap)));
			reqVerifiMap.put("partner_no", e.getMerchantCode());
			reqVerifiMap.put("code", verifiCode);
			try
			{
				resVerifiMap = slRestClient.postForObject(interfaceDetailInfoEntity.getSyncRedirectUrl(), reqVerifiMap, Map.class);
				if("00000".equals(resVerifiMap.get("result_code"))) {
					resOrderMap = slRestClient.postForObject(interfaceDetailInfoEntity.getAsyncNotifyUrl(), reqOrderMap, Map.class);
				} 		
			}
			catch(Exception ex) {
				log.error(ex.getMessage());
			}
			
			String result = (String) resOrderMap.get("result_code");
			if("00000".equals(result)) {
				e.setExecStatus(Constant.TRADE_STATUS_03);
			} else {
				e.setExecStatus(Constant.TRADE_STATUS_04);
			}
			
			// 记录通知报文
			String innerTradeCode = numberService.generateOpenServiceTradeNumber();
			TradeLogInfoEntity tradeLogInfoEntity1 = new TradeLogInfoEntity();
			tradeLogInfoEntity1.setRelateTableIdentification(Constant.TABLE_BAO_T_EXPAND_INFO);
			tradeLogInfoEntity1.setRelatePrimary(e.getId());
			tradeLogInfoEntity1.setThirdPartyType(Constant.THIRD_PARTY_TYPE_JIRONGTONG);
			tradeLogInfoEntity1.setInterfaceType(e.getInterfaceType());
			tradeLogInfoEntity1.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
			tradeLogInfoEntity1.setTradeCode("");
			tradeLogInfoEntity1.setInnerTradeCode(innerTradeCode);
			tradeLogInfoEntity1.setRequestMessage(Json.ObjectMapper.writeValue(reqVerifiMap));
			tradeLogInfoEntity1.setResponseMessage(Json.ObjectMapper.writeValue(resVerifiMap));
			tradeLogInfoEntity1.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			tradeLogInfoEntity1 = tradeLogInfoRepository.save(tradeLogInfoEntity1);
			if("00000".equals(resVerifiMap.get("result_code"))) {
				TradeLogInfoEntity tradeLogInfoEntity2 = new TradeLogInfoEntity();
				tradeLogInfoEntity2.setRelateTableIdentification(Constant.TABLE_BAO_T_EXPAND_INFO);
				tradeLogInfoEntity2.setRelatePrimary(e.getId());
				tradeLogInfoEntity2.setThirdPartyType(Constant.THIRD_PARTY_TYPE_JIRONGTONG);
				tradeLogInfoEntity2.setInterfaceType(e.getInterfaceType());
				tradeLogInfoEntity2.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
				tradeLogInfoEntity2.setTradeCode("");
				tradeLogInfoEntity2.setInnerTradeCode(innerTradeCode);
				tradeLogInfoEntity2.setRequestMessage(Json.ObjectMapper.writeValue(reqOrderMap));
				tradeLogInfoEntity2.setResponseMessage(Json.ObjectMapper.writeValue(resOrderMap));
				tradeLogInfoEntity2.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				tradeLogInfoEntity2 = tradeLogInfoRepository.save(tradeLogInfoEntity2);
			}
		}
		
		/**
		 * 掌上互动注册结果通知
		 *
		 * @author  wangjf
		 * @date    2015年11月9日 下午6:50:20
		 * @param e
		 * @throws SLException
		 */
		@SuppressWarnings("unchecked")
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void zshdSingleDownloadAsynNotify(ExpandInfoEntity e)
				throws SLException {
			
			// 将游离对象变为受控对象
			e = manager.merge(e);
			
			int alreadyNotifyTimes = e.getAlreadyNotifyTimes();
			e.setAlreadyNotifyTimes(++alreadyNotifyTimes);
			e.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyMerchantCode(e.getThirdPartyType(), e.getInterfaceType(), e.getMerchantCode());
			if(interfaceDetailInfoEntity == null) {
				log.error(String.format("接口未找到！ThirdPartyType:%s, InterfaceType:%s, MerchantCode:%s", e.getThirdPartyType(), e.getInterfaceType(), e.getMerchantCode()));
				return;
			}
				
			Map<String, Object> resMap = new HashMap<String, Object>();
			try
			{			
				String result = slRestClient.getForObject(e.getCallBack(), String.class, resMap);
				resMap = Json.ObjectMapper.readValue(result, Map.class);
			}
			catch(Exception ex) {
				log.error(ex.getMessage());
			}

			// status为1表示成功
			// status为0且message包含“此激活请求信息已经存在”也表示成功
			if("1".equals((String) resMap.get("status")) 
					|| ("0".equals((String) resMap.get("status")) && StringUtils.stripToEmpty((String) resMap.get("message")).contains("此激活请求信息已经存在"))) {
				e.setExecStatus(Constant.TRADE_STATUS_03);
			} else {
				e.setExecStatus(Constant.TRADE_STATUS_04);
			}
			
			// 记录通知报文
			String innerTradeCode = numberService.generateOpenServiceTradeNumber();
			TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
			tradeLogInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_EXPAND_INFO);
			tradeLogInfoEntity.setRelatePrimary(e.getId());
			tradeLogInfoEntity.setThirdPartyType(e.getThirdPartyType());
			tradeLogInfoEntity.setInterfaceType(e.getInterfaceType());
			tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
			tradeLogInfoEntity.setTradeCode("");
			tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
			tradeLogInfoEntity.setRequestMessage(e.getCallBack());
			tradeLogInfoEntity.setResponseMessage(Json.ObjectMapper.writeValue(resMap));
			tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);
		}
		
		@SuppressWarnings("unchecked")
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void thirdProjectAsynNotify(ExpandInfoEntity e) {
			// 将游离对象变为受控对象
			e = manager.merge(e);
			
			int alreadyNotifyTimes = e.getAlreadyNotifyTimes();
			e.setAlreadyNotifyTimes(++alreadyNotifyTimes);
			e.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyMerchantCode(e.getThirdPartyType(), e.getInterfaceType(), e.getMerchantCode());
			if(interfaceDetailInfoEntity == null) {
				log.error(String.format("接口未找到！ThirdPartyType:%s, InterfaceType:%s, MerchantCode:%s", e.getThirdPartyType(), e.getInterfaceType(), e.getMerchantCode()));
				return;
			}
				
			Map<String, Object> responseMap = new HashMap<String, Object>();
			Map<String, Object> requestMap = new HashMap<String, Object>();
			try
			{	
				requestMap.put("loanId", e.getRelatePrimary());
				ResultVo resultVo = loanProjectService.queryProject(requestMap);
				if(!ResultVo.isSuccess(resultVo)) {
					log.error("查询项目失败!{}", resultVo.getValue("message"));
					return;
				}
				requestMap.clear();
				switch(e.getMemo()) {
				case Constant.OPERATION_TYPE_68: // 发布
					requestMap.put("loanStatus", "发布中");
					break;
				case Constant.OPERATION_TYPE_69: // 流标
					requestMap.put("loanStatus", Constant.LOAN_STATUS_12);
					break;
				case Constant.OPERATION_TYPE_60: // 审核拒绝
					requestMap.put("loanStatus", Constant.LOAN_STATUS_14);
					break;
				case Constant.OPERATION_TYPE_84: // 满标
					requestMap.put("loanStatus", Constant.LOAN_STATUS_06);
					break;
				default:
					log.warn("除发布、流标、拒绝、满标外，不允许通知其他状态");
					e.setExecStatus(Constant.TRADE_STATUS_04);
					e.setResponseMessage("除发布、流标、拒绝、满标外，不允许通知其他状态");
					return;
				}
				Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
				requestMap.put("loanNo", data.get("loanNo"));				
				requestMap.put("grantMoneyDate", new Date());
				requestMap.put("accountManageAmount", 0);
				if(data.containsKey("result")) {
					requestMap.put("result", data.get("result"));
				}
				else {
					requestMap.put("result", Maps.newConcurrentMap());
				}
				
				responseMap = openThirdService.sendNotify(interfaceDetailInfoEntity.getSyncRedirectUrl(), requestMap);
			}
			catch(Exception ex) {
				log.error(ex.getMessage());
			}

			if(Constant.NOTIFY_TYPE_CODE_SUCCESS.equals((String)responseMap.get("code"))) {
				e.setExecStatus(Constant.TRADE_STATUS_03);
			} else {
				e.setExecStatus(Constant.TRADE_STATUS_04);
			}
			
			// 记录通知报文
			String innerTradeCode = numberService.generateOpenServiceTradeNumber();
			TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
			tradeLogInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_EXPAND_INFO);
			tradeLogInfoEntity.setRelatePrimary(e.getId());
			tradeLogInfoEntity.setThirdPartyType(e.getThirdPartyType());
			tradeLogInfoEntity.setInterfaceType(e.getInterfaceType());
			tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
			tradeLogInfoEntity.setTradeCode("");
			tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
			tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(requestMap));
			tradeLogInfoEntity.setResponseMessage(Json.ObjectMapper.writeValue(responseMap));
			tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			tradeLogInfoEntity.setMemo("通知");
			tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);			
		}
		
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void bindCardAsynNotify(ExpandInfoEntity e) {
			// 将游离对象变为受控对象
			e = manager.merge(e);
			
			int alreadyNotifyTimes = e.getAlreadyNotifyTimes();
			e.setAlreadyNotifyTimes(++alreadyNotifyTimes);
			e.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyMerchantCode(e.getThirdPartyType(), e.getInterfaceType(), e.getMerchantCode());
			if(interfaceDetailInfoEntity == null) {
				log.error(String.format("接口未找到！ThirdPartyType:%s, InterfaceType:%s, MerchantCode:%s", e.getThirdPartyType(), e.getInterfaceType(), e.getMerchantCode()));
				return;
			}
				
			Map<String, Object> responseMap = new HashMap<String, Object>();
			Map<String, Object> requestMap = new HashMap<String, Object>();
			try
			{	
				BankCardInfoEntity bankCardInfoEntity = bankCardInfoRepository.findOne(e.getRelatePrimary());
				if(bankCardInfoEntity == null) {
					log.error(String.format("未找到指定银行卡号，id:%s", e.getRelatePrimary()));
					return;
				}
				
				// 协议号为空，则补全协议号
				String protocolNo = bankCardInfoEntity.getProtocolNo();
				if(Constant.NOTIFY_TYPE_LOAN_BIND_CARD.equals(e.getInterfaceType())) { // 如果是绑卡则补全协议号
					if(StringUtils.isEmpty(protocolNo)) {
						Map<String, Object> paramsMap = new HashMap<String, Object>();
						paramsMap.put("bankCardNo", bankCardInfoEntity.getCardNo());
						paramsMap.put("custCode", bankCardInfoEntity.getCustInfoEntity().getCustCode());
						paramsMap.put("noAgree", bankCardInfoEntity.getProtocolNo());
						paramsMap.put("bankId", bankCardInfoEntity.getId());
						ResultVo resultVo = bankCardService.mendOneBank(paramsMap);
						if(ResultVo.isSuccess(resultVo)) {
							protocolNo = (String)resultVo.getValue("data");
						}
					}
				}
				
				CustInfoEntity custInfoEntity = custInfoRepository.findOne(bankCardInfoEntity.getCustInfoEntity().getId());
				if(custInfoEntity == null) {
					log.error(String.format("未找到指定客户，id:%s", bankCardInfoEntity.getCustInfoEntity().getId()));
					return;
				}
				requestMap.put("custCode", custInfoEntity.getCustCode());
				requestMap.put("custName", custInfoEntity.getCustName());
				requestMap.put("idCard", custInfoEntity.getCredentialsCode());
				requestMap.put("bankName", bankCardInfoEntity.getBankName());
				requestMap.put("bankCardAccount", bankCardInfoEntity.getCardNo());
				requestMap.put("protocolNumber", protocolNo);
				
				responseMap = openThirdService.sendNotify(interfaceDetailInfoEntity.getSyncRedirectUrl(), requestMap);
			}
			catch(Exception ex) {
				log.error(ex.getMessage());
			}

			if(Constant.NOTIFY_TYPE_CODE_SUCCESS.equals((String)responseMap.get("code"))) {
				e.setExecStatus(Constant.TRADE_STATUS_03);
			} else {
				e.setExecStatus(Constant.TRADE_STATUS_04);
			}
			
			// 记录通知报文
			String innerTradeCode = numberService.generateOpenServiceTradeNumber();
			TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
			tradeLogInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_EXPAND_INFO);
			tradeLogInfoEntity.setRelatePrimary(e.getId());
			tradeLogInfoEntity.setThirdPartyType(e.getThirdPartyType());
			tradeLogInfoEntity.setInterfaceType(e.getInterfaceType());
			tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
			tradeLogInfoEntity.setTradeCode("");
			tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
			tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(requestMap));
			tradeLogInfoEntity.setResponseMessage(Json.ObjectMapper.writeValue(responseMap));
			tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			tradeLogInfoEntity.setMemo("通知");
			tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);			
		}
	}
}
