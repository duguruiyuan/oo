/** 
 * @(#)LocalCacheController.java 1.0.0 2015年12月11日 下午1:56:29  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.slfinance.shanlincaifu.entity.DeviceInfoEntity;
import com.slfinance.shanlincaifu.service.DeviceService;
import com.slfinance.shanlincaifu.service.GroupService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.utils.CacheUtils;
import com.slfinance.vo.ResultVo;

/**   
 * 本地缓存访问控制层
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年12月11日 下午1:56:29 $ 
 */
@RestController
@RequestMapping("/localCache")
public class LocalCacheController {
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private GroupService groupService;
	
	/**
	 *	获取所有key
	 *	@return
	 */
	@RequestMapping(value="{cache}/keys",method = RequestMethod.GET)
	@ResponseBody
	public List<String> getKeys(@PathVariable String cache) {
		return CacheUtils.getKeys(cache);
	} 
	
	/**
	 *	得到相应的缓存值
	 *	@return
	 */
	@RequestMapping(value = "{cache}/{key}", method = RequestMethod.GET)
	@ResponseBody
	public Object get(@PathVariable String cache,@PathVariable String key) {
		return CacheUtils.get(cache, key);
	} 
	
	/**
	 *	删除相应的缓存值
	 *	@return
	 */
	@RequestMapping(value = "{cache}/{key}", method = RequestMethod.POST)
	public void delete(@PathVariable String cache,@PathVariable String key) {
		CacheUtils.remove(cache, key);
	} 
	
	/**
	 *	删除某缓存中的的所有缓存值
	 *	@return
	 */
	@RequestMapping(value = "{cache}", method = RequestMethod.POST)
	public void deleteAll(@PathVariable String cache) {
		CacheUtils.removeAll(cache);
	}
	
	@RequestMapping(value="/test",method = RequestMethod.GET)
	@ResponseBody
	public String testCache() throws Exception {
		return paramService.findInvestValidTime();
		//Map<String, Object> params = new HashMap<String, Object>();
		//params.put("type", "8");
		//return paramService.findByChannelNo("2015070100000001");
		//return result.toString();
	}
	
	@RequestMapping(value = "/cacheDevice", method = RequestMethod.POST)
	@ResponseBody
	public ResultVo cacheDevice() throws Exception {
		List<DeviceInfoEntity> list = deviceService.cacheDevice(null);
		for(DeviceInfoEntity d : list) {
			deviceService.queryDevice(d.getMeId());
		}
		return new ResultVo(true);
	}
	
	@RequestMapping(value = "/loadLoanGroup", method = RequestMethod.POST)
	@ResponseBody
	public ResultVo loadLoanGroup() throws Exception {
		groupService.loadLoanGroup();
		return new ResultVo(true);
	}
}
