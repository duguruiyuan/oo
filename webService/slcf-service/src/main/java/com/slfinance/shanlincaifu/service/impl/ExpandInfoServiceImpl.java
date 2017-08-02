package com.slfinance.shanlincaifu.service.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.DeviceInfoEntity;
import com.slfinance.shanlincaifu.entity.ExpandInfoEntity;
import com.slfinance.shanlincaifu.entity.InterfaceDetailInfoEntity;
import com.slfinance.shanlincaifu.repository.ExpandInfoRepository;
import com.slfinance.shanlincaifu.repository.InterfaceDetailInfoRepository;
import com.slfinance.shanlincaifu.service.ExpandInfoService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.vo.ResultVo;

@Service("expandInfoService")
public class ExpandInfoServiceImpl implements ExpandInfoService {
	
	@Autowired
	private ExpandInfoRepository expandInfoRepository;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private InterfaceDetailInfoRepository interfaceDetailInfoRepository;

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo saveExpandInfo(Map<String, Object> params)
			throws SLException {
		String relateType = (String) params.get("relateType");
		String relatePrimary = (String) params.get("relatePrimary");
		String innerTradeCode = (String) params.get("innerTradeCode");
		String tradeCode = (String) params.get("tradeCode");
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = (InterfaceDetailInfoEntity) params.get("interfaceDetailInfoEntity");
		DeviceInfoEntity deviceInfoEntity = (DeviceInfoEntity) params.get("deviceInfoEntity");
		String userId = (String) params.get("userId");
		
		if(StringUtils.isEmpty(innerTradeCode)) {
			innerTradeCode = numberService.generateOpenServiceTradeNumber();
		}
		
		if(interfaceDetailInfoEntity == null 
				&& !StringUtils.isEmpty((String)params.get("thirdPartyType"))
				&& !StringUtils.isEmpty((String)params.get("interfaceType"))) {
			interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType((String)params.get("thirdPartyType"), (String)params.get("interfaceType"));
		}
		
		ExpandInfoEntity expandInfoEntity = new ExpandInfoEntity();
		expandInfoEntity.setRelateTableIdentification(relateType);
		expandInfoEntity.setRelatePrimary(relatePrimary);
		expandInfoEntity.setInnerTradeCode(innerTradeCode);
		expandInfoEntity.setTradeCode(tradeCode);
		expandInfoEntity.setThirdPartyType(interfaceDetailInfoEntity.getThirdPartyType());
		expandInfoEntity.setInterfaceType(interfaceDetailInfoEntity.getInterfaceType());
		expandInfoEntity.setMerchantCode(interfaceDetailInfoEntity.getMerchantCode());
		expandInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		expandInfoEntity.setAlreadyNotifyTimes(0);
		expandInfoEntity.setMemo((String)params.get("memo"));// 此标识表示需要同步
		if (deviceInfoEntity != null) {
			expandInfoEntity.setMeId(deviceInfoEntity.getMeId());
			expandInfoEntity.setMeVersion(deviceInfoEntity.getMeVersion());
			expandInfoEntity.setAppSource(Strings.nullToEmpty(deviceInfoEntity.getAppSource()).toLowerCase());
		}
		expandInfoEntity.setBasicModelProperty(userId, true);
		expandInfoRepository.save(expandInfoEntity);
		
		return new ResultVo(true);
	}

}
