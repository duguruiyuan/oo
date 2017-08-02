/** 
 * @(#)SmsService.java 1.0.0 2015年4月21日 上午11:36:51  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.SmsInfoEntity;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 
 *  
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年4月21日 上午11:36:51 $ 
 */
public interface SMSService {
	
	public ResultVo checkMobileAndMessageType(Map<String, Object> params) throws SLException;
	
	/**
	 * 根据手机号跟消息类型,查询验证码
	 * @param mobile			手机号
	 * @param messageType		消息类型(注册,忘记密码...)
	 * @return
	 */
	public ResultVo findByMobileAndMessageType(Map<String, Object> params);
	
	/**
	 * 根据手机号跟类型统计已发送短信数量
	 * @param params
	 * @return
	 */
	public Map<String,Object> findByAddressAndTypeAndDate(Map<String, Object> params);
	/**
	 * 发送短信验证码业务验证
	 * @param params
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "mobile", required = true, requiredMessage = "手机号不能为空", mobile = true, mobileMessage = "手机号格式不对")
	})
	public ResultVo presendSMS(Map<String, Object> params);
	/**
	 * 发送短信验证码
	 * @param params
	 * 				mobile		手机号
	 * 				messageType	消息类型,可以为空(注册,忘记密码...)
	 * 				key			消息的格式，可以为空
	 * 				values		替换内容(为空随机生成6位数字)
	 * 				custId		用户ID, 当前登录用户或系统用户
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "mobile", required = true, requiredMessage = "手机号不能为空", mobile = true, mobileMessage = "手机号格式不对"), 
			@Rule(name = "messageType", required = true, requiredMessage = "消息类型不能为空"),
			@Rule(name = "custId", required = true, requiredMessage = "用户ID, 当前登录用户或系统用户")
	})
	public ResultVo sendSMS(Map<String, Object> params);
	
	/***    
	 * 校验验证码
	 * 
	 * @author zhangzs
	 * @param params
	 *            <tt>messageType:String:消息类型</tt>
	 *            <tt>verityCode:String:邮箱验证码</tt>
	 *            <tt>targetAddress:String:邮箱地址</tt>
	 * @return
	 * @throws SLException
	 */
	public ResultVo checkVerityCode(Map<String, Object> params) throws SLException;
	
	/***    
	 * 校验验证码
	 * 
	 * @author zhangzs
	 * @param params
	 *            <tt>messageType:String:消息类型</tt>
	 *            <tt>verityCode:String:邮箱验证码</tt>
	 *            <tt>targetAddress:String:邮箱地址</tt>
	 * @return
	 * @throws SLException
	 */
	public ResultVo checkSmsCode(Map<String, Object> params) throws SLException;
	
	/**
	 * 更新校验码为失效状态
	 *
	 * @author  wangjf
	 * @date    2015年4月28日 下午2:38:20
	 * @param smsInfoEntity
	 * @return
	 * @throws SLException
	 */
	public ResultVo updateVerificationCodeStatus(SmsInfoEntity smsInfoEntity) throws SLException;
	
	/**
	 * 异步发送短信验证码
	 * @param params
	 * 				mobile		手机号
	 * 				messageType	消息类型,可以为空(注册,忘记密码...)
	 * 				key			消息的格式，可以为空
	 * 				values		替换内容(为空随机生成6位数字)
	 * 				custId		用户ID, 当前登录用户或系统用户
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "mobile", required = true, requiredMessage = "手机号不能为空", mobile = true, mobileMessage = "手机号格式不对"), 
			@Rule(name = "messageType", required = true, requiredMessage = "消息类型不能为空"),
			@Rule(name = "custId", required = true, requiredMessage = "用户ID, 当前登录用户或系统用户")
	})
	public ResultVo asnySendSMS(final Map<String, Object> params);
	
	/**
	 * 异步发送短信和系统站内信
	 *
	 * @author  wangjf
	 * @date    2015年12月26日 下午1:26:57
	 * @param params
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "mobile", required = true, requiredMessage = "手机号不能为空", mobile = true, mobileMessage = "手机号格式不对"), 
			@Rule(name = "messageType", required = true, requiredMessage = "消息类型不能为空"),
			@Rule(name = "custId", required = true, requiredMessage = "用户ID, 当前登录用户或系统用户")
	})
	public ResultVo asnySendSMSAndSystemMessage(final Map<String, Object> params);
	
	/**
	 * 发送站内信
	 * 
	 * @author zhiwen_feng
	 * @date    2016年07月06日 
	 * @param params
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "messageType", required = true, requiredMessage = "消息类型不能为空"),
			@Rule(name = "custId", required = true, requiredMessage = "用户ID, 当前登录用户或系统用户")
	})
	public ResultVo asnySendSystemMessage(final Map<String, Object> params);
	
	/**
	 * 异步发送短信验证码
	 * @param params
	 * 				mobile		手机号
	 * 				messageType	消息类型,可以为空(注册,忘记密码...)
	 * 				key			消息的格式，可以为空
	 * 				values		替换内容(为空随机生成6位数字)
	 * 				custId		用户ID, 当前登录用户或系统用户
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "mobile", required = true, requiredMessage = "手机号不能为空", mobile = true, mobileMessage = "手机号格式不对"), 
			@Rule(name = "sendContent", required = true, requiredMessage = "短信内容不能为空"),
			@Rule(name = "custId", required = true, requiredMessage = "用户ID, 当前登录用户或系统用户")
	})
	public ResultVo asnySendRedSMS(final Map<String, Object> params);
	
}
