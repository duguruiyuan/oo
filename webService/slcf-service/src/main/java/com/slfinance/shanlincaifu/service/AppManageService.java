package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

public interface AppManageService {
	/**
	 * 列表
	 * 
	 * @param params
	 * @return
	   *      <tt>appSource          :String:APP类型</tt><br>
	   *      <tt>appVersion         :String:APP当前版本</tt><br>
	   *      <tt>appSupportedVersion:String:APP支持最低版本</tt><br>
	   *      <tt>updateUrl          :String:更新地址</tt><br>
	 */
	public ResultVo queryAppVersionList(Map<String, Object> params);
	
	/**
	 * 详情
	 * 
	 * @param params
	   *      <tt>id                 :String:主键</tt><br>
	 * @return
	   *      <tt>appSource          :String:APP类型</tt><br>
	   *      <tt>appVersion         :String:APP当前版本</tt><br>
	   *      <tt>appSupportedVersion:String:APP支持最低版本</tt><br>
	   *      <tt>updateUrl          :String:更新地址</tt><br>
	 */
	@Rules(rules = {
			@Rule(name = "id", required = true, requiredMessage = "id不能为空"),
	})
	public ResultVo queryAppVersionById(Map<String, Object> params);
	
	/**
	 * 保存/更新
	 * 
	 * @param params
	   *      <tt>appSource          :String:APP类型</tt><br>
	   *      <tt>appVersion         :String:APP当前版本</tt><br>
	   *      <tt>appSupportedVersion:String:APP支持最低版本</tt><br>
	   *      <tt>updateUrl          :String:更新地址</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "appSource", required = true, requiredMessage = "APP类型不能为空", inRange = { "android", "ios", "android-salesman", "ios-salesman"}, inRangeMessage = "APP类型不在区间内"),
			@Rule(name = "appVersion", required = true, requiredMessage = "APP当前版本不能为空"),
			@Rule(name = "appSupportedVersion", required = true, requiredMessage = "APP支持最低版本不能为空"),
			@Rule(name = "updateUrl", required = true, requiredMessage = "更新地址不能为空"),
	})
	public ResultVo saveAppVersion(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询APP版本
	 * 
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "appSource", required = true, requiredMessage = "应用来源不能为空", inRange = { "android", "ios"}, inRangeMessage = "APP类型不在区间内")
	})
	public ResultVo queryAppVersion(Map<String, Object> params) throws SLException;
}
