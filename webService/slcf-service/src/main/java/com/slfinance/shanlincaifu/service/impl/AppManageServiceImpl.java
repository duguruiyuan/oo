package com.slfinance.shanlincaifu.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AppManageInfoEntity;
import com.slfinance.shanlincaifu.repository.AppManageInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.AppManageRepositoryCustom;
import com.slfinance.shanlincaifu.service.AppManageService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

@Service("appManageService")
public class AppManageServiceImpl implements AppManageService {
	
	@Autowired
	private AppManageRepositoryCustom appManageRepositoryCustom;
	
	@Autowired
	private AppManageInfoRepository appManageInfoRepository;

	@Override
	public ResultVo queryAppVersionList(Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> page = appManageRepositoryCustom.queryAppVersionList(params);
		result.put("iTotalDisplayRecords", page.size());
		result.put("data", page);
		return new ResultVo(true, "查询列表成功", result);
	}

	@Override
	public ResultVo queryAppVersionById(Map<String, Object> params) {
		AppManageInfoEntity appManageInfoEntity = appManageInfoRepository.findOne((String)params.get("id"));
		if(appManageInfoEntity == null) {
			return new ResultVo(false, "查询记录失败");
		}
		return new ResultVo(true, "查询记录成功", appManageInfoEntity);
	}

	@Caching(evict = { @CacheEvict(value = "slcf_appversion", allEntries = true, cacheManager="redis")})
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo saveAppVersion(Map<String, Object> params)
			throws SLException {
		AppManageInfoEntity appManageInfoEntity = null;
		if(!StringUtils.isEmpty((String)params.get("id"))) {
			appManageInfoEntity = appManageInfoRepository.findOne((String)params.get("id"));
			if(appManageInfoEntity == null) {
				return new ResultVo(false, "查询记录失败");
			}
			appManageInfoEntity.setBasicModelProperty((String)params.get("userId"), false);
		}
		else {
			appManageInfoEntity = appManageInfoRepository.findByAppSource((String)params.get("appSource"));
			if(appManageInfoEntity != null) {
				return new ResultVo(false, "类型[" + (String)params.get("appSource") + "]已经存在，请新增其它类型");
			}
			
			appManageInfoEntity = new AppManageInfoEntity();
			appManageInfoEntity.setAppSource((String)params.get("appSource"));
			appManageInfoEntity.setBasicModelProperty((String)params.get("userId"), true);
		}
		appManageInfoEntity.setAppSupportedVersion((String)params.get("appSupportedVersion"));
		appManageInfoEntity.setAppVersion((String)params.get("appVersion"));
		appManageInfoEntity.setUpdateUrl((String)params.get("updateUrl"));
		
		appManageInfoRepository.save(appManageInfoEntity);
		return new ResultVo(true, "保存成功");
	}

	@Cacheable(value="slcf_appversion",key="'app_appversion' + #params['appSource'] + #params['appName']", cacheManager="redis")
	@Override
	public ResultVo queryAppVersion(Map<String, Object> params)
			throws SLException {
		Map<String, String> result = Maps.newConcurrentMap();
		
		if(!StringUtils.isEmpty((String)params.get("appName"))
				&& Constant.APP_NAME_SALES.equals((String)params.get("appName"))) {
			String appSource = (String)params.get("appSource");
			if(Constant.APP_SOURCE_IOS.equals((String)params.get("appSource"))) {
				appSource = "ios-salesman";
			}
			else {
				appSource = "android-salesman";
			}
			AppManageInfoEntity appManageInfoEntity = appManageInfoRepository.findByAppSource(appSource);
			if(appManageInfoEntity == null) {
				result.put("appVersion", "2.0");
				result.put("appSupportedVersion", "2.0");
				result.put("urlString", Constant.APP_SOURCE_IOS.equals((String)params.get("appSource")) ? 
						  "https://www.pgyer.com/903P" // 默认IOS地址
						: "https://app.shanlincaifu.com/download/shanlindashi.apk"); // 默认android地址
			}
			else {
				result.put("appVersion", appManageInfoEntity.getAppVersion());
				result.put("appSupportedVersion", appManageInfoEntity.getAppSupportedVersion());
				result.put("urlString", appManageInfoEntity.getUpdateUrl());
			}
		}
		else {
			AppManageInfoEntity appManageInfoEntity = appManageInfoRepository.findByAppSource((String)params.get("appSource"));
			if(appManageInfoEntity == null) {
				result.put("appVersion", "1.0");
				result.put("appSupportedVersion", "1.0");
				result.put("urlString", Constant.APP_SOURCE_IOS.equals((String)params.get("appSource")) ? 
						  "https://itunes.apple.com/us/app/shan-lin-cai-fu/id1097437552?l=zh&ls=1&mt=8" // 默认IOS地址
						: "https://app.shanlincaifu.com/download/shanlincaifu.apk"); // 默认android地址
			}
			else {
				result.put("appVersion", appManageInfoEntity.getAppVersion());
				result.put("appSupportedVersion", appManageInfoEntity.getAppSupportedVersion());
				result.put("urlString", appManageInfoEntity.getUpdateUrl());
			}
		}

		return new ResultVo(true, "查询成功", result);
	}
}
