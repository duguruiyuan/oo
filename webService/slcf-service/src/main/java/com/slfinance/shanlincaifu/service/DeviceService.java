/** 
 * @(#)DeviceService.java 1.0.0 2015年12月22日 下午4:45:19  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.List;
import java.util.Map;

import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.DeviceInfoEntity;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 设备服务接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年12月22日 下午4:45:19 $ 
 */
public interface DeviceService {

	/**
	 * 缓存设备信息
	 *
	 * @author  wangjf
	 * @date    2015年12月22日 下午4:46:27
	 * @param params
	 * @return
	 */
	public List<DeviceInfoEntity> cacheDevice(Map<String, Object> params);
	
	/**
	 * 查询设备信息（缓存）
	 *
	 * @author  wangjf
	 * @date    2015年12月22日 下午4:46:52
	 * @param params
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "meId", 
						required = true, requiredMessage = "meId参数不能为空") 
	})
	public Map<String, Object> queryCacheDevice(Map<String, Object> params);
	
	/**
	 * 查询设备信息（物理）
	 *
	 * @author  wangjf
	 * @date    2015年12月22日 下午4:46:52
	 * @param params
	 * @return
	 */
	public Map<String, Object> queryDevice(String meId);
	
	/**
	 * 保存设备信息
	 *
	 * @author  wangjf
	 * @date    2015年12月22日 下午5:53:30
	 * @param savedEntity
	 * @param params
	 * @return
	 */
	public Map<String, Object> saveDeviceInfo(CustInfoEntity savedEntity, Map<String, Object> params);
	
	/**
	 * 保存用户设备信息
	 *
	 * @author  wangjf
	 * @date    2017年1月22日 上午11:33:35
	 * @param params
	 * @return
	 */
	public ResultVo saveUserDevice(Map<String, Object> params);
}
