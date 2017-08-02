/** 
 * @(#)UserEmailController.java 1.0.0 2015年4月22日 下午7:26:19  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.UserEmailService;
import com.slfinance.vo.ResultVo;

/**   
 * 用户邮件业务操作控制器
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月22日 下午7:26:19 $ 
 */
@Controller
@RequestMapping(value="/userEmail", produces="application/json;charset=UTF-8")
public class UserEmailController {

	protected static final Logger logger = LoggerFactory.getLogger(UserEmailController.class);
	
	@Autowired
	private UserEmailService userEmailService;

	/**
	 * 登陆用户绑定邮件
	 * 
	 * @param map
	 * 			<tt>custId:String:用户Id</tt>
	 * 			<tt>targetAddress:String:邮箱地址</tt>
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/sendEmail", method = RequestMethod.POST)
	public @ResponseBody ResultVo sendMailForBindEmail(@RequestBody Map<String, Object> map) throws SLException {
		try {
			return userEmailService.sendMailForBindEmail(map);
		} catch (Exception e) {
			throw new SLException(e.getMessage());
		}
	}
	
	/**
	 * 邮箱回调地址校验邮箱验证码和验证地址
	 * 
	 * @param paramsMap
	 *            <tt>verityCode:String:邮箱验证码</tt>
	 *            <tt>targetAddress:String:邮箱地址</tt>
	 * @return ResultVo
	 * @throws SLException
	 */
	@RequestMapping(value="/checkVerityCode", method = RequestMethod.POST)
	public @ResponseBody ResultVo checkVerityCodeAndTargetAddress(@RequestBody Map<String, Object> paramsMap) throws SLException{
		try {
			return userEmailService.checkVerityCodeAndTargetAddress(paramsMap);
		} catch (Exception e) {
			throw new SLException(e.getMessage());
		}
	}
	

	/**
	 * 验证用户成功，更新验证码信息和用户信息
	 * 
	 * @param paramsMap
	 *            <tt>custId:String:客户ID</tt>
	 *            <tt>messageType:String:消息类型</tt>
	 *            <tt>verityCode:String:邮箱验证码</tt>
	 *            <tt>targetAddress:String:邮箱地址</tt>
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/callBackEmailUpdate", method = RequestMethod.POST)
	public @ResponseBody ResultVo callBackEmailUpdate(@RequestBody Map<String, Object> paramsMap) throws SLException{
		try {
			return userEmailService.updateCustEmail(paramsMap);
		} catch (Exception e) {
			throw new SLException(e.getMessage());
		}
	}
	
	/**
	 *  修改绑定邮箱--发送修改邮箱邮件
	 *  
	 * @param paramsMap
	 * 				<tt>custId:String:用户Id</tt>
	 * 				<tt>targetAddressOld:String:旧邮箱地址</tt>
	 * 				<tt>targetAddress:String:新邮箱地址</tt>
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/sendUpdateEmail", method = RequestMethod.POST)
	public @ResponseBody ResultVo sendMailForUpdateEmail(@RequestBody Map<String, Object> paramsMap) throws SLException{
		try {
			return userEmailService.sendMailForUpdateEmail(paramsMap);
		} catch (Exception e) {
			throw new SLException(e.getMessage());
		}
	}
}
