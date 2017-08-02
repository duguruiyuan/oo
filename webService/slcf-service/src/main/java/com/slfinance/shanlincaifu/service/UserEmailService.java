/** 
 * @(#)CustSafeInfoService.java 1.0.0 2015年4月22日 下午2:08:42  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 客户安全中心业务接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月22日 下午2:08:42 $ 
 */
public interface UserEmailService {

	/**
	 * 检查该邮箱是否已经存在
	 * 
	 * @author zhangzs
	 * @param paramsMap
	 * 				<tt>email:String:邮箱地址</tt>
	 * @return boolean
	 */
	@Rules(rules = {
			@Rule(name = "email", required = true, requiredMessage = "邮箱地址不能为空")
			})
	public boolean checkEmailIsExist( Map<String,Object> paramsMap ) throws SLException;
	
	/** 
	 * 登陆用户发送绑定邮箱业务
	 * 
	 * @author zhangzs
	 * @param paramsMap
	 * 				<tt>custId:String:用户Id</tt>
	 * 				<tt>targetAddress:String:邮箱地址</tt>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空"),
			@Rule(name = "targetAddress", required = true, requiredMessage = "邮箱地址不能为空")
			})
	public ResultVo sendMailForBindEmail( Map<String,Object> paramsMap ) throws SLException;
	
	/**
	 * 邮箱回调地址校验邮箱验证码和验证地址
	 * 
	 * @author zhangzs
	 * @param paramsMap
	 *            <tt>verityCode:String:邮箱验证码</tt>
	 *            <tt>targetAddress:String:邮箱地址</tt>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "verityCode", required = true, requiredMessage = "邮箱验证码不能为空"),
			@Rule(name = "targetAddress", required = true, requiredMessage = "邮箱地址不能为空")
			})
	public ResultVo checkVerityCodeAndTargetAddress(Map<String, Object> paramsMap) throws SLException;
	

	/**
	 * 验证用户成功，更新验证码信息和用户信息
	 * 
	 * @author zhangzs
	 * @param paramsMap
	 *            <tt>custId:String:客户ID</tt>
	 *            <tt>verityCode:String:邮箱验证码</tt>
	 *            <tt>targetAddress:String:邮箱地址</tt>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空"),
			@Rule(name = "verityCode", required = true, requiredMessage = "邮箱验证码不能为空"),
			@Rule(name = "targetAddress", required = true, requiredMessage = "邮箱地址不能为空")
			})
	public ResultVo updateCustEmail(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 *  修改绑定邮箱--发送修改邮箱邮件
	 *  
	 * @author zhangzs 
	 * @param paramsMap
	 * 				<tt>custId:String:用户Id</tt>
	 * 				<tt>targetAddressOld:String:旧邮箱地址</tt>
	 * 				<tt>targetAddress:String:新邮箱地址</tt>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空"),
			@Rule(name = "targetAddressOld", required = true, requiredMessage = "旧邮箱地址不能为空"),
			@Rule(name = "targetAddress", required = true, requiredMessage = "邮箱地址不能为空")
			})
	public ResultVo sendMailForUpdateEmail(Map<String, Object> paramsMap) throws SLException;
	
}
