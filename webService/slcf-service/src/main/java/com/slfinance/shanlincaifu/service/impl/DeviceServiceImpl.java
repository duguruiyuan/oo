/** 
 * @(#)DeviceServiceImpl.java 1.0.0 2015年12月22日 下午4:47:55  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.DeviceInfoEntity;
import com.slfinance.shanlincaifu.repository.DeviceInfoRepository;
import com.slfinance.shanlincaifu.service.DeviceService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**   
 * 设备服务实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年12月22日 下午4:47:55 $ 
 */
@Service("deviceService")
public class DeviceServiceImpl implements DeviceService {
	
	@Autowired
	private DeviceInfoRepository deviceInfoRepository;

	@Autowired
	private ParamService paramService;
	
	@Override
	public List<DeviceInfoEntity> cacheDevice(Map<String, Object> params) {
		List<DeviceInfoEntity> deviceList =  deviceInfoRepository.findByAppSourceAndTradeType(Constant.APP_SOURCE_IOS, Constant.OPERATION_TYPE_16);		
		return deviceList;
	}

	@Cacheable(value="slcf_device", key = "'device_' + #params['meId']", condition = "#params['meId'] != null and #params['meId'] != ''", unless = "#result[#params['meId']] == '0'", cacheManager="redis")
	@Override
	public Map<String, Object> queryCacheDevice(Map<String, Object> params) {
		
		Map<String, Object> result = Maps.newHashMap();
		result.put((String)params.get("meId"), "0");
		return result;
	}

	@Cacheable(value="slcf_device", key = "'device_' + #meId", condition = "#meId != null and #meId != ''", cacheManager="redis")
	@Override
	public Map<String, Object> queryDevice(String meId) {
		
		Map<String, Object> result = Maps.newHashMap();
		if(StringUtils.isEmpty(meId)) {
			result.put("error", "0");
		}
		else {
			int counts = deviceInfoRepository.countByMeId(Constant.APP_SOURCE_IOS, Constant.OPERATION_TYPE_16, meId);
			if(counts == 0) {
				result.put(meId, "0");
			}
			else {
				result.put(meId, "1");
			}
		}
		
		return result;
	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@CachePut(value = "slcf_device", key = "'device_' + #params['meId']", condition = "#savedEntity.custSource == 'ios' and #params['meId'] != null and #params['meId'] != ''", cacheManager="redis")
	public Map<String, Object> saveDeviceInfo(CustInfoEntity savedEntity, Map<String, Object> params) {
		// 记录设备号
		DeviceInfoEntity deviceInfoEntity = new DeviceInfoEntity();
		deviceInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_CUST_INFO);
		deviceInfoEntity.setRelatePrimary(savedEntity.getId());
		deviceInfoEntity.setCustId(savedEntity.getId());
		deviceInfoEntity.setTradeType(Constant.OPERATION_TYPE_16);
		deviceInfoEntity.setMeId((String)params.get("meId"));
		deviceInfoEntity.setMeVersion((String)params.get("meVersion"));
		deviceInfoEntity.setAppSource(savedEntity.getCustSource());
		if(StringUtils.isEmpty(savedEntity.getChannelSource())) {
			deviceInfoEntity.setChannelNo((String)params.get("source"));
		}
		else {
			deviceInfoEntity.setChannelNo(savedEntity.getChannelSource());
		}
		deviceInfoEntity.setRequestUrl((String)params.get("requestUrl"));
		deviceInfoEntity.setUtmCampaign((String)params.get("campaign"));
		deviceInfoEntity.setUtmContent((String)params.get("content"));
		deviceInfoEntity.setUtmMedium((String)params.get("medium"));
		deviceInfoEntity.setUtmSource((String)params.get("source"));
		deviceInfoEntity.setUtmTerm((String)params.get("term"));
		deviceInfoEntity.setBasicModelProperty(savedEntity.getId(), true);
		deviceInfoRepository.save(deviceInfoEntity);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put((String)params.get("meId"), "1");
		return result;
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo saveUserDevice(Map<String, Object> params) {
		// 记录设备号
		DeviceInfoEntity deviceInfoEntity = new DeviceInfoEntity();
		deviceInfoEntity.setRelateTableIdentification((String)params.get("relateType"));
		deviceInfoEntity.setRelatePrimary((String)params.get("relatePrimary"));
		deviceInfoEntity.setCustId((String)params.get("custId"));
		deviceInfoEntity.setTradeType((String)params.get("tradeType"));
		deviceInfoEntity.setMeId((String)params.get("meId"));
		deviceInfoEntity.setMeVersion((String)params.get("meVersion"));
		deviceInfoEntity.setAppSource((String)params.get("appSource"));
		deviceInfoEntity.setChannelNo(paramService.findByChannelNo((String)params.get("channelNo")));
		deviceInfoEntity.setRequestUrl((String)params.get("requestUrl"));
		deviceInfoEntity.setUtmCampaign((String)params.get("campaign"));
		deviceInfoEntity.setUtmContent((String)params.get("content"));
		deviceInfoEntity.setUtmMedium((String)params.get("medium"));
		deviceInfoEntity.setUtmSource((String)params.get("source"));
		deviceInfoEntity.setUtmTerm((String)params.get("term"));
		deviceInfoEntity.setBasicModelProperty((String)params.get("userId"), true);
		deviceInfoRepository.save(deviceInfoEntity);
		return new ResultVo(true, "保存设备信息成功");
	}

}
