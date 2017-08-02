/** 
 * @(#)AccessServiceImpl.java 1.0.0 2015年11月3日 上午11:25:47  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.AppManageInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.AppManageRepositoryCustom;
import com.slfinance.shanlincaifu.service.AppManageService;
import com.slfinance.shanlincaifu.service.AppVersionCheckService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.vo.ResultVo;

/**
 * 查询App版本信息服务实现类
 * 
 * @author gaoll
 * @version $Revision:1.0.0, $Date: 2015年11月27日 下午21:25:47 $
 */
@Service("appVersionCheckService")
public class AppVersionCheckServiceImpl implements AppVersionCheckService {
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private AppManageRepositoryCustom appManageRepositoryCustom;
	
	@Autowired
	private AppManageInfoRepository appManageInfoRepository;
	
	@Autowired
	private AppManageService appManageService;

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo checkAPPVersion(Map<String, Object> params) throws SLException {
		Map<String,Object> data = Maps.newHashMap();
	
		ResultVo localVersionResult = appManageService.queryAppVersion(params);
		Map<String, String> mapResult = (Map<String, String>)localVersionResult.getValue("data");
		data.put("urlString", mapResult.get("urlString")); // 更新URL
		data.put("needUpdate", needUpdate(String.valueOf(params.get("version")), mapResult.get("appSupportedVersion"))); // 强制更新
		data.put("canUpdate", needUpdate(String.valueOf(params.get("version")), mapResult.get("appVersion")));	// 可以更新
	
		return new ResultVo(true, "查询App版本信息成功", data);
		
	}
	
	/**
	 * 判断是否为最新版本, 将版本号根据.切分为int数组比较
	 * 
	 * @author gaoll
	 * @param onlineVersion
	 *            线上版本号
	 * @param localVersion
	 *            本地版本号
	 * @return
	 */
	private static boolean needUpdate(String onlineVersion, String localVersion)
	{
		if (onlineVersion.equals(localVersion))
		{
			return false; // true:需要更新
		}
		String[] onlineArray = onlineVersion.split("\\.");
		String[] localArray = localVersion.split("\\.");
		
		int length = onlineArray.length < localArray.length ? onlineArray.length : localArray.length;

		for (int i = 0; i < length; i++)
		{
			if (Integer.parseInt(onlineArray[i]) > Integer.parseInt(localArray[i]))
			{
				return false;
			}
			else if (Integer.parseInt(onlineArray[i]) < Integer.parseInt(localArray[i]))
			{
				return true;
			}
			// 相等, 比较下一组值
		}
		
		return onlineArray.length > localArray.length ? false : true;

	}
}
