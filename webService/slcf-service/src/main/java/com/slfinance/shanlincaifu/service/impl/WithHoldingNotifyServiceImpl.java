package com.slfinance.shanlincaifu.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.InterfaceDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeLogInfoEntity;
import com.slfinance.shanlincaifu.entity.WithHoldingExpandEntity;
import com.slfinance.shanlincaifu.repository.InterfaceDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.TradeLogInfoRepository;
import com.slfinance.shanlincaifu.repository.WithHoldingExpandRepostory;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.OpenThirdService;
import com.slfinance.shanlincaifu.service.WithHoldingNotifyService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.Json;
import com.slfinance.shanlincaifu.utils.WithHoldingConstant;

/**
 * 代扣对外通知
 * @author lixx
 * @create 2017-07-17 10:48
 **/
@Slf4j
@Service("withHoldingNotifyService")
public class WithHoldingNotifyServiceImpl implements WithHoldingNotifyService {
	
	@Autowired
	private InterfaceDetailInfoRepository interfaceDetailInfoRepository;
	
	@Autowired
	private WithHoldingExpandRepostory withHoldingExpandRepostory;
	
	@Autowired
	private OpenThirdService openThirdService;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private TradeLogInfoRepository tradeLogInfoRepository;
	
	/***
     *发送代扣通知
     */
	@Override
	public void asynNotify() throws SLException {
		
		
		
		List<WithHoldingExpandEntity> withHoldingExpandEntityList = withHoldingExpandRepostory.findNeedSendNotify(WithHoldingConstant.REPAYMENT_INTERFACE_INFO, Constant.OFFLINE_WITHDRAW_STATUS_SUCCESS, 5);
		
		if(withHoldingExpandEntityList != null && !withHoldingExpandEntityList.isEmpty()){
			for(WithHoldingExpandEntity withHoldingExpandEntity : withHoldingExpandEntityList){
				sendNotify(withHoldingExpandEntity);
			}
		}
		// TODO Auto-generated method stub
	}
	
	
	public void sendNotify(WithHoldingExpandEntity withHoldingExpandEntity){
		int alreadyNotifyTimes = withHoldingExpandEntity.getAlreadyNotifyTimes();
		withHoldingExpandEntity.setAlreadyNotifyTimes(++alreadyNotifyTimes);
		withHoldingExpandEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		withHoldingExpandRepostory.save(withHoldingExpandEntity);
		
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType(withHoldingExpandEntity.getThirdPartyType(), withHoldingExpandEntity.getInterfaceType());
		if(interfaceDetailInfoEntity == null) {
			log.error(String.format("接口未找到！ThirdPartyType:%s, InterfaceType:%s", withHoldingExpandEntity.getThirdPartyType(), withHoldingExpandEntity.getInterfaceType()));
			return;
		}
		
		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> requestMap = new HashMap<String, Object>();
		
		requestMap.put("loanNo", withHoldingExpandEntity.getRepaymentNo());
		requestMap.put("repaymentTerm", withHoldingExpandEntity.getRepaymentTerm());
		requestMap.put("expectRemaymentDate", withHoldingExpandEntity.getRepaymentDate());
		requestMap.put("tradeAmout", withHoldingExpandEntity.getTradeAmout());
		requestMap.put("requestTime", DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		requestMap.put("tradeStatus", withHoldingExpandEntity.getTradeStatus());
		requestMap.put("tradeType", withHoldingExpandEntity.getTradeType());
		requestMap.put("acountId", withHoldingExpandEntity.getAccountId());
		
		responseMap = openThirdService.sendNotify(interfaceDetailInfoEntity.getSyncRedirectUrl(), requestMap);
		
		// 记录通知报文
		String innerTradeCode = numberService.generateOpenServiceTradeNumber();
		TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
		tradeLogInfoEntity.setRelateTableIdentification(Constant.BAO_T_WITH_HOLDING_EXPAND);
		tradeLogInfoEntity.setRelatePrimary(withHoldingExpandEntity.getId());
		tradeLogInfoEntity.setThirdPartyType(withHoldingExpandEntity.getThirdPartyType());
		tradeLogInfoEntity.setInterfaceType(withHoldingExpandEntity.getInterfaceType());
		tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		tradeLogInfoEntity.setTradeCode(innerTradeCode);
		tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
		tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(requestMap));
		tradeLogInfoEntity.setResponseMessage(Json.ObjectMapper.writeValue(responseMap));
		tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		tradeLogInfoEntity.setMemo("通知");
		tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);
		
	}
	
}
