/** 
 * @(#)AccessServiceImpl.java 1.0.0 2015年11月3日 上午11:25:47  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AppAccessInfoEntity;
import com.slfinance.shanlincaifu.repository.AppAccessInfoRepository;
import com.slfinance.shanlincaifu.service.AccessService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SharedUtil;
import com.slfinance.vo.ResultVo;

/**   
 * 用户访问服务实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年11月3日 上午11:25:47 $ 
 */
@Service("accessService")
public class AccessServiceImpl implements AccessService {

	@Autowired
	private AppAccessInfoRepository appAccessInfoRepository;
	
	@Autowired
	private ParamService paramService;
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo saveAccess(Map<String, Object> params) throws SLException {
		
		AppAccessInfoEntity appAccessInfoEntity = new AppAccessInfoEntity();
		appAccessInfoEntity.setMeId((String)params.get("meId"));
		appAccessInfoEntity.setMeVersion((String)params.get("meVersion"));
		appAccessInfoEntity.setAppSource(Strings.nullToEmpty((String)params.get("appSource")).toLowerCase());
		appAccessInfoEntity.setChannelNo((String)params.get("channelNo"));
		appAccessInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		
		appAccessInfoRepository.save(appAccessInfoEntity);
		
		return new ResultVo(true);
	}

	@Override
	public ResultVo checkAppVersion(Map<String, Object> params)
			throws SLException {
		
		String appSource = "";
		
		if(params.containsKey("appSource")) { // 判断是否含有设备信息
			appSource = (String)params.get("appSource");
		}
		
		if(params.containsKey("investSource")) { // 判断是否含有设备信息
			appSource = (String)params.get("investSource");
		}
		
		// 若不包含设备信息则提示更新APP
		if(StringUtils.isEmpty(appSource)) {
			return new ResultVo(false, "您当前版本过低，本业务需要更高版本支持，请立即更新App");
		}
		
		// 若是IOS设备且不包含appVersion则返回异常
		if(appSource.compareToIgnoreCase("ios") == 0
				&& StringUtils.isEmpty((String)params.get("appVersion"))) {
			return new ResultVo(false, "您当前版本过低，本业务需要更高版本支持，请立即更新App");
		}
		
		return new ResultVo(true);
	}

	@Override
	public ResultVo judgeVersion(Map<String, Object> params) throws SLException {
		
		String appVersion = (String)params.get("appVersion");
		Map<String, Object> currentVersion = paramService.findCurrentIOSVersion(params);
		
		if(StringUtils.isEmpty(appVersion) || StringUtils.isEmpty((String)currentVersion.get("appVersion"))) {
			return new ResultVo(false);
		}
		
		if(SharedUtil.compareVersion(appVersion, (String)currentVersion.get("appVersion")) <= 0) {
			return new ResultVo(false);
		}
		
		return new ResultVo(true);
	}

}
