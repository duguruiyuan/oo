/** 
 * @(#)SMSServiceImpl.java 1.0.0 2015年4月21日 下午12:03:38  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.SmsInfoEntity;
import com.slfinance.shanlincaifu.entity.SmsLogInfoEntity;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.SmsInfoRepository;
import com.slfinance.shanlincaifu.repository.SmsLogInfoRepository;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.service.SystemMessageService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.HttpRequestUtil;
import com.slfinance.shanlincaifu.utils.OpenSerivceCode;
import com.slfinance.shanlincaifu.utils.RestClient;
import com.slfinance.shanlincaifu.utils.RestClientProperties;
import com.slfinance.util.BeanMapConvertUtil;
import com.slfinance.vo.ResultVo;

/**   
 * 
 *  
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年4月21日 下午12:03:38 $ 
 */
@Slf4j
@Service("sMSService")
public class SMSServiceImpl implements SMSService{
	private Lock lock = new ReentrantLock();
	private static final String PROPERTIES_RESOURCE_LOCATION = "smsContent.properties";
	
	private static final Properties localProperties = new Properties();
	
	@Autowired
	@Qualifier("smsThreadPoolTaskExecutor")
	private Executor smsThreadPoolTaskExecutor;
	
	@Autowired
	private StringRedisTemplate redisTemplate2;
	
	@Autowired
	private SystemMessageService systemMessageService;
	
	@Autowired
	private ParamService paramService;
	
	
	static {
		try {
			ClassLoader cl = SMSServiceImpl.class.getClassLoader();
			URL url = (cl != null ? cl.getResource(PROPERTIES_RESOURCE_LOCATION) :
					ClassLoader.getSystemResource(PROPERTIES_RESOURCE_LOCATION));
			if (url != null) {
				log.info("Found 'sms.properties' file in local classpath");
				InputStream is = url.openStream();
				try {
					localProperties.load(is);
				}
				finally {
					is.close();
				}
			}
		}
		catch (IOException ex) {
			if (log.isInfoEnabled()) {
				log.info("Could not load 'spring.properties' file from local classpath: " + ex);
			}
		}
	}
	
	private static String smsContentKey(final String type) {
		String key = "";
		switch(type){
		case Constant.SMS_TYPE_REGISTER:
			key = "sms.content.register";
			break;
		case Constant.SMS_TYPE_FIND_PASSWORD:
			key = "sms.content.findpassword";
			break;
		case Constant.SMS_TYPE_TRADE_PASSWD:
			key = "sms.content.withdrawalBack";
			break;
		case Constant.SMS_TYPE_BINDING_MOBILE:
		case Constant.SMS_TYPE_BINDING_NEW_MOBILE:
			key = "sms.content.bindingMobile";
			break;
		case Constant.SMS_TYPE_UPDATE_MOBILE:
			key = "sms.content.updatingMobile";
			break;
		case Constant.SMS_TYPE_WITHDRAWAL:
			key = "sms.content.withdrawalCash";
			break;
		case Constant.SMS_TYPE_WITHDRAWAL_APPLY:
			key = "sms.content.withdrawalCashApply";
			break;
		case Constant.SMS_TYPE_WITHDRAW_SUCCESS:
			key = "sms.content.withdrawSuccess";
			break;
		case Constant.SMS_TYPE_WITHDRAW_FAIL:
			key = "sms.content.withdrawFail";
			break;
		case Constant.SMS_TYPE_EXPERIENCE_WITHDRAW:
			key = "sms.content.experienceWithdraw";
			break;
		case Constant.SMS_TYPE_UNBIND_PASS:
			key = "sms.content.unbandSuccess";
			break;
		case Constant.SMS_TYPE_UNBIND_REFUSE:
			key = "sms.content.unbandFail";
			break;	
		case Constant.SMS_TYPE_TERM_ATONE:
			key = "sms.content.termAtone";
			break;	
		case Constant.SMS_TYPE_ADVANCED_ATONE_APPLY:
			key = "sms.content.advancedAtoneApply";
			break;
		case Constant.SMS_TYPE_ADVANCED_ATONE_SUCCESS:
			key = "sms.content.advancedAtoneSuccess";
			break;
		case Constant.SMS_TYPE_PROJECT_UNRELEASE:
			key = "sms.content.projectUnrelease";
			break;
		case Constant.SMS_TYPE_PROJECT_RELEASE:
			key = "sms.content.projectRelease";
			break;
		case Constant.SMS_TYPE_PROJECT_NORMAL_REPAYMENT:
			key = "sms.content.normalRepayment";
			break;
		case Constant.SMS_TYPE_PROJECT_FINAL_REPAYMENT:
			key = "sms.content.finalRepayment";
			break;
		case Constant.SMS_TYPE_PROJECT_EARLY_REPAYMENT:
			key = "sms.content.earlyRepayment";
			break;
		case Constant.SMS_TYPE_WEALTH_RELEASE:
			key = "sms.content.effect.wealth";
			break;
		case Constant.SMS_TYPE_WEALTH_INTEREST:
			key = "sms.content.every.term.back";
			break;
		case Constant.SMS_TYPE_WEALTH_DUE_ATONE:
			key = "sms.content.end.wealth";
			break;
		case Constant.SMS_TYPE_WEALTH_ADVANCE_ATONE:
			key = "sms.content.advance.wealth";
			break;
		case Constant.SMS_TYPE_WEALTH_ADVANCED_ATONE_APPLY:
			key = "sms.content.advance.apply.wealth";
			break;
		case Constant.SMS_TYPE_OFFLINE_RECHARGE: //线下充值成功，发送短信
			key = "sms.content.offline.recharge";
			break;
		case Constant.SMS_TYPE_BIND_BANKCARD: //银行卡绑定成功，发送短信
			key = "sms.content.bind.bankcard";
			break;
		case Constant.SMS_TYPE_LOAN_UNRELEASE:
			key = "sms.content.loanUnrelease";
			break;
		case Constant.SMS_TYPE_LOAN_RELEASE:
			key = "sms.content.loanRelease";
			break;
		case Constant.SMS_TYPE_LOAN_NORMAL_REPAYMENT:
			key = "sms.content.loanRepayment";
			break;
		case Constant.SMS_TYPE_LOAN_TRASNFER:
			key = "sms.content.loanTransfer";
			break;
		case Constant.SMS_TYPE_ACTIVITY_REWARD://活动奖励短信
			key = "sms.content.activityReward";
			break;
		case Constant.SMS_TYPE_RED_BAG://红包短信
			key = "sms.content.redActivity";
			break;
		case Constant.SMS_TYPE_ACTIVITY_AMOUNT_WITHDRAW_SUCCESS:
			key = "sms.content.activityAmountWithdrawSuccess";
			break;
		case Constant.SMS_TYPE_ACTIVITY_AMOUNT_WITHDRAW_FAIL:
			key = "sms.content.ActivityAmountWithdrawFail";
			break;
		}
		
		return key;
	}
	
	private static String sysContentKey(final String type) {
		String key = "";
		switch(type){
		case Constant.SMS_TYPE_TERM_ATONE:
			key = "sys.content.termAtone";
			break;	
		case Constant.SMS_TYPE_ADVANCED_ATONE_APPLY:
			key = "sys.content.advancedAtoneApply";
			break;
		case Constant.SMS_TYPE_ADVANCED_ATONE_SUCCESS:
			key = "sys.content.advancedAtoneSuccess";
			break;
		case Constant.SMS_TYPE_PROJECT_UNRELEASE:
			key = "sys.content.projectUnrelease";
			break;
		case Constant.SMS_TYPE_PROJECT_RELEASE:
			key = "sys.content.projectRelease";
			break;
		case Constant.SMS_TYPE_PROJECT_NORMAL_REPAYMENT:
			key = "sys.content.normalRepayment";
			break;
		case Constant.SMS_TYPE_PROJECT_FINAL_REPAYMENT:
			key = "sys.content.finalRepayment";
			break;
		case Constant.SMS_TYPE_PROJECT_EARLY_REPAYMENT:
			key = "sys.content.earlyRepayment";
			break;
		case Constant.SMS_TYPE_WEALTH_RELEASE:
			key = "sys.content.effect.wealth";
			break;
		case Constant.SMS_TYPE_WEALTH_INTEREST:
			key = "sys.content.every.term.back";
			break;
		case Constant.SMS_TYPE_WEALTH_DUE_ATONE:
			key = "sys.content.end.wealth";
			break;
		case Constant.SMS_TYPE_WEALTH_ADVANCE_ATONE:
			key = "sys.content.advance.wealth";
			break;
		case Constant.SMS_TYPE_WEALTH_ADVANCED_ATONE_APPLY:
			key = "sys.content.advance.apply.wealth";
			break;
		case Constant.SYS_TYPE_OFFLINE_RECHARGE_FAIL: //线下充值审核拒绝、回退
			key = "sys.content.offline.recharge.fail";
			break;
		case Constant.SYS_TYPE_BIND_BANKCARD_FAIL: //绑定线下银行卡审核拒绝、回退
			key = "sys.content.bind.bankcard.fail";
			break;
		case Constant.SMS_TYPE_LOAN_UNRELEASE:
			key = "sys.content.loanUnrelease";
			break;
		case Constant.SMS_TYPE_LOAN_RELEASE:
			key = "sys.content.loanRelease";
			break;
		case Constant.SMS_TYPE_LOAN_NORMAL_REPAYMENT:
			key = "sys.content.loanRepayment";
			break;
		case Constant.SMS_TYPE_LOAN_TRASNFER:
			key = "sys.content.loanTransfer";
			break;	
		case Constant.SMS_TYPE_LOAN_TRASNFER_OWNER:
			key = "sys.content.loanTransferOwner";
			break;	
		}
		
		return key;
	}
		
	@Autowired
	private SmsInfoRepository smsInfoRepository;
	
	@Autowired
	private SmsLogInfoRepository smsLogInfoRepository;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private RestClient client;
	
	@Autowired 
	private RestClientProperties restProp;

	private long maxLengthRandom(int num) {
		return (long) ((Math.random() * (num - 1) + 1) * Math.pow(10, num-1));
	}
	
	public ResultVo checkMobileAndMessageType(Map<String, Object> params) throws SLException{
		String mobile=params.get("mobile")+"";
		String messageType=params.get("messageType")+"";
		String verityCode=params.get("verityCode")+"";
		List<SmsInfoEntity> sies=smsInfoRepository.
				findByTargetAddressAndTargetTypeOrderBySendDateDesc(mobile, messageType);
		if(sies!=null && sies.size()>0){
			SmsInfoEntity smsInfoEntity = sies.get(0);
			Date sendDate = smsInfoEntity.getSendDate();
			//判断时间
			Date enlabelDate=DateUtils.getDateAfterByMinute(sendDate, 30);
			if(new Date().getTime()>enlabelDate.getTime()){
				//如果当前日期大于有效时间,返回验证码超时
				return new ResultVo(false, "返回验证码超时!", OpenSerivceCode.ERR_VALIDATE_SMSCODE);
			}
			
			//判断是否一致
			if(smsInfoEntity.getVerityCode().equals(verityCode)){
				//成功
				return new ResultVo(true,smsInfoEntity.getVerityCode(),smsInfoEntity);	
			}else{
				return new ResultVo(false,"验证码错误!", OpenSerivceCode.ERR_VALIDATE_SMSCODE);
			}
		}else{
			return new ResultVo(false,"验证码错误!", OpenSerivceCode.ERR_VALIDATE_SMSCODE);
		}
	}

	@Override
	public ResultVo findByMobileAndMessageType(Map<String, Object> params) {
		String mobile=params.get("mobile")+"";
		String verityCode=params.get("verityCode")+"";
		String messageType=params.get("messageType")+"";
		SmsInfoEntity sie=smsInfoRepository.
				findByTargetAddressAndVerityCodeAndMessageType(mobile,verityCode, messageType);
		if(sie!=null){
			return new ResultVo(true,sie.getVerityCode(),sie);
		}else{
			return new ResultVo(false,"验证码错误!");
		}
	}
	
	/**
	 * 
	 * 检查手机号是否存在
	 * @param params
	 * @return
	 */
	public ResultVo presendSMS(Map<String, Object> params) {
		String mobile=params.get("mobile")+"";
		String messageType=params.get("messageType")+"";
		if(messageType.equals(Constant.SMS_TYPE_REGISTER) || messageType.equals(Constant.SMS_TYPE_BINDING_NEW_MOBILE))
			return new ResultVo(true);
		else
			return new ResultVo(custInfoRepository.findByMobile(mobile) != null);
	}
	
	public Map<String,Object> findByAddressAndTypeAndDate(Map<String, Object> params){
		String mobile=params.get("mobile")+"";
		String smsType=params.get("smsType")+"";
		log.info("mobile:"+mobile+",smsType:"+smsType);
		List<Object[]> objList=
				smsInfoRepository.findByAddressAndTypeAndDate(mobile, smsType, 
				DateUtils.getCurrentDate("yyyy-MM-dd"), 
				DateUtils.getFirstDay(new Date(),"yyyy-MM-dd"), 
				DateUtils.getLastDay(new Date(),"yyyy-MM-dd"));
		
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("day", objList.get(0)[1]);
		map.put("month", objList.get(1)[1]);
		
//		//查询月发送总数,如果>=500,返回ERROR001
//		if(Integer.valueOf(objList.get(1)[1]+"")>=500){
//			return new ResultVo(true,Integer.valueOf(objList.get(1)[1]+""));
//		}
//		//查询月发送总数,如果>=100,返回ERROR002
//		if(Integer.valueOf(objList.get(1)[1]+"")>=100){
//			return new ResultVo(true,Integer.valueOf(objList.get(1)[1]+""));
//		}
//		//查询日发送总数,如果>=5,返回ERROR003
//		if(Integer.valueOf(objList.get(0)[1]+"")>=5){
//			return new ResultVo(true,Integer.valueOf(objList.get(0)[1]+""));
//		}
//		//查询日发送总数,如果>=10，返回ERROR004
//		if(Integer.valueOf(objList.get(0)[1]+"")>=10){
//			return new ResultVo(true,Integer.valueOf(objList.get(0)[1]+""));
//		}
		return map;
	}
	
	// 不受发短信规则限制的业务类型
	private static List<String> SMS_NO_SEND_LIMIT_LIST = Lists.newArrayList(
			Constant.SMS_TYPE_WITHDRAWAL_APPLY, 
			Constant.SMS_TYPE_WITHDRAW_SUCCESS,
			Constant.SMS_TYPE_WITHDRAW_FAIL,
			Constant.SMS_TYPE_EXPERIENCE_WITHDRAW,
			Constant.SMS_TYPE_UNBIND_REFUSE,
			Constant.SMS_TYPE_UNBIND_PASS,
			Constant.SMS_TYPE_TERM_ATONE, 
			Constant.SMS_TYPE_ADVANCED_ATONE_APPLY, 
			Constant.SMS_TYPE_ADVANCED_ATONE_SUCCESS,
			Constant.SMS_TYPE_PROJECT_UNRELEASE,
			Constant.SMS_TYPE_PROJECT_RELEASE,
			Constant.SMS_TYPE_PROJECT_NORMAL_REPAYMENT,
			Constant.SMS_TYPE_PROJECT_FINAL_REPAYMENT,
			Constant.SMS_TYPE_PROJECT_EARLY_REPAYMENT,
			Constant.SMS_TYPE_OFFLINE_RECHARGE,
			Constant.SMS_TYPE_BIND_BANKCARD,
			Constant.SMS_TYPE_LOAN_UNRELEASE,
			Constant.SMS_TYPE_LOAN_RELEASE,
			Constant.SMS_TYPE_LOAN_NORMAL_REPAYMENT,
			Constant.SMS_TYPE_ACTIVITY_REWARD,
			Constant.SMS_TYPE_ACTIVITY_AMOUNT_WITHDRAW_SUCCESS,
			Constant.SMS_TYPE_ACTIVITY_AMOUNT_WITHDRAW_FAIL
			);
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo sendSMS(Map<String, Object> params) {
		try {
			//手机号
			String mobile=params.get("mobile")+"";
			//消息类型,可以为空(注册,忘记密码...)
			String messageType=params.get("messageType")+"";
			log.info("mobile:"+mobile+",messageType:"+messageType);
//			String verityCode="123456";
			String verityCode=maxLengthRandom(6)+"";
			
			//获取传入的key，对应sms.properties配置文件
			String key = smsContentKey(messageType);
		
			//获取传入的values,如果不为空,设置模板内容,使用values,如果为空,使用随机数
			Object values=params.get("values");
			//根据key获取内容,从配置获取短信模板内容
			String sendContent=localProperties.getProperty(key);
			if(values !=null){
				if(values instanceof String[]){
					String[] _values=(String[])values;
					sendContent=MessageFormat.format(sendContent, _values[0],_values[1]);
				}
				if(values instanceof Object[]){
					sendContent=MessageFormat.format(sendContent, (Object[])values);
				}
				else{
					sendContent=MessageFormat.format(sendContent, values);	
				}
			}else{
				sendContent=MessageFormat.format(sendContent, verityCode);
			}
			params.put("isSync", true);
			params.put("content", sendContent);
			params.put("tunnel", "DHST"); // 设置手机厂商为大汉三通
			Object sendTime=params.get("sendTime");
			if(StringUtils.isEmpty(sendTime)){
				params.put("sendTime", "");
			}
			//调用远程接口，发送短信
			//		* @param mobile		手机号，多个使用半角逗号分隔
			//		* @param content	内容
			//		* @param sendTime	定时时间，格式2010-10-24 09:08:10，小于当前时间或为空表示立即发送
			//		* @param custId		操作人ID
			//		* @param isSync
			//{mobile=13111111111, sendContent=异步测试短信, sendTime=1429605443776, extno=, returnStatus=Success, returnMessage=ok, remainPoint=00000, taskId=999999, successCounts=1}
			
			// 改造发短信，区分具体业务类型，类型“提现 update by wangjf 2015-12-28
			if(SMS_NO_SEND_LIMIT_LIST.contains(messageType)) {
				ResultVo vo=null;
				lock.lock();
				try
				{
					vo = client.postForObject(restProp.getFoundtionClient().getServicePrefix() + "sms/sendSms", 
							params, ResultVo.class);	
					if(!ResultVo.isSuccess(vo)){
						return vo;
					}
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}finally{
					lock.unlock();// 释放锁 
				}
				
				Map<String,Object> dataMap=(Map<String,Object>)vo.getValue("data");
				SmsLogInfoEntity smsLogInfo = BeanMapConvertUtil.toBean(SmsLogInfoEntity.class, dataMap);
				smsLogInfo.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				if (smsLogInfoRepository.save(smsLogInfo)==null) {
					return new ResultVo(false,"发送短信失败!");
				}
				return new ResultVo(true,"发送短信成功.");
			}
			else {
				ResultVo vo=null;
				lock.lock();
				try {
					log.info("发送短信，手机号:"+mobile);
					Object _d = redisTemplate2.opsForValue().get("SMS_MOBILE_"+mobile);
					if(_d!=null){
						log.info("发送频率过高mobile:"+mobile+",上次发送时间为："+_d.toString());	
						return new ResultVo(false,"频率过高,发送短信失败!");
					}
					
					String dateCounts = redisTemplate2.opsForValue().get("SMS_MOBILE_"+mobile + "_"+messageType);
					Long maxTimes = paramService.findMaxSendSmsTimes();
					if(!StringUtils.isEmpty(dateCounts) && Long.valueOf(dateCounts) >= maxTimes) {
						log.info("手机{}{}当天发短信次数超过{}次，已发送{}次", mobile, messageType, maxTimes, dateCounts);	
						return new ResultVo(false, String.format("每日限发%d条短信，您已经超过规定次数！", maxTimes));
					}
					
					vo = client.postForObject(restProp.getFoundtionClient().getServicePrefix() + "sms/sendSms", 
							params, ResultVo.class);
					redisTemplate2.opsForValue().set("SMS_MOBILE_"+mobile, new Date().toString(), 30, TimeUnit.SECONDS);
					
					long counts = redisTemplate2.opsForValue().increment("SMS_MOBILE_"+mobile + "_"+messageType, 1);
					if(counts == 1) {
						//redisTemplate2.expire("SMS_MOBILE_"+mobile + "_"+messageType, 1, TimeUnit.DAYS);
						// 设置为第二天0点过期
						redisTemplate2.expireAt("SMS_MOBILE_"+mobile + "_"+messageType, DateUtils.getAfterDay(new Date(), 1));
					}
					
					if(!ResultVo.isSuccess(vo)){
						return vo;
					}
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}finally{
					lock.unlock();// 释放锁 
				}

				Map<String,Object> dataMap=(Map<String,Object>)vo.getValue("data");
				SmsLogInfoEntity smsLogInfo = BeanMapConvertUtil.toBean(SmsLogInfoEntity.class, dataMap);
				smsLogInfo.setCreateDate(new Date());
				if (smsLogInfoRepository.save(smsLogInfo)==null) {
					return new ResultVo(false,"发送短信失败!");
				}else{				
					SmsInfoEntity entities=new SmsInfoEntity();
					entities.setCreateDate(new Date());
					entities.setTargetAddress(mobile);
					entities.setTargetType(Constant.TARGET_TYPE_TEL);
					entities.setSendDate(new Date());
					entities.setSendStatus("已发送");
					//读取配置文件，替换内容
					entities.setSendContent(sendContent);
					entities.setVerityCode(verityCode);
					entities.setMessageStatus("有效");
					entities.setMessageType(messageType);
					//获取30分钟之后的时间
					entities.setLastValidTime(DateUtils.getDateAfterByMinute(new Date(), 30));
					if(smsInfoRepository.save(entities)==null){
						log.info("发送短信失败!"+smsLogInfo.getSendContent());
					}else{
						log.info("发送短信成功."+smsLogInfo.getSendContent());
					}
					return new ResultVo(true,"发送短信成功.");
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResultVo(false,"发送短信失败!");
		}
	}

	/**
	 * 
	 * <红包短信>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo sendRedSMS(Map<String, Object> params) {
		try {
			//手机号
			String mobile=params.get("mobile")+"";
			String sendContent = params.get("sendContent")+"";
			//消息类型,可以为空(注册,忘记密码...)
//			String messageType=params.get("messageType")+"";
//			log.info("mobile:"+mobile+",messageType:"+messageType);
//			String verityCode="123456";
			String verityCode=maxLengthRandom(6)+"";
			
			//获取传入的key，对应sms.properties配置文件
//			String key = smsContentKey(messageType);
		
			//获取传入的values,如果不为空,设置模板内容,使用values,如果为空,使用随机数
//			Object values=params.get("values");
			//根据key获取内容,从配置获取短信模板内容
//			String sendContent=localProperties.getProperty(key);
//			if(values !=null){
//				if(values instanceof String[]){
//					String[] _values=(String[])values;
//					sendContent=MessageFormat.format(sendContent, _values[0],_values[1]);
//				}
//				if(values instanceof Object[]){
//					sendContent=MessageFormat.format(sendContent, (Object[])values);
//				}
//				else{
//					sendContent=MessageFormat.format(sendContent, values);	
//				}
//			}else{
//				sendContent=MessageFormat.format(sendContent, verityCode);
//			}
			params.put("isSync", true);
			params.put("content", sendContent);
			params.put("tunnel", "DHST"); // 设置手机厂商为大汉三通
			Object sendTime = params.get("sendTime");
			if(StringUtils.isEmpty(sendTime)){
				params.put("sendTime", "");
			}
			//调用远程接口，发送短信
			//		* @param mobile		手机号，多个使用半角逗号分隔
			//		* @param content	内容
			//		* @param sendTime	定时时间，格式2010-10-24 09:08:10，小于当前时间或为空表示立即发送
			//		* @param custId		操作人ID
			//		* @param isSync
			//{mobile=13111111111, sendContent=异步测试短信, sendTime=1429605443776, extno=, returnStatus=Success, returnMessage=ok, remainPoint=00000, taskId=999999, successCounts=1}
			
				ResultVo vo=null;
				lock.lock();
				try {
					log.info("发送短信，手机号:"+mobile);
					Object _d = redisTemplate2.opsForValue().get("SMS_MOBILE_"+mobile);
					if(_d!=null){
						log.info("发送频率过高mobile:"+mobile+",上次发送时间为："+_d.toString());	
						return new ResultVo(false,"频率过高,发送短信失败!");
					}
					
					String dateCounts = redisTemplate2.opsForValue().get("SMS_MOBILE_"+mobile);
					Long maxTimes = paramService.findMaxSendSmsTimes();
					if(!StringUtils.isEmpty(dateCounts) && Long.valueOf(dateCounts) >= maxTimes) {
						log.info("手机{}{}当天发短信次数超过{}次，已发送{}次", mobile, maxTimes, dateCounts);	
						return new ResultVo(false, String.format("每日限发%d条短信，您已经超过规定次数！", maxTimes));
					}
					
					vo = client.postForObject(restProp.getFoundtionClient().getServicePrefix() + "sms/sendSms", 
							params, ResultVo.class);
					redisTemplate2.opsForValue().set("SMS_MOBILE_"+mobile, new Date().toString(), 30, TimeUnit.SECONDS);
					
					long counts = redisTemplate2.opsForValue().increment("SMS_MOBILE_"+mobile + "_"+"", 1);
					if(counts == 1) {
						//redisTemplate2.expire("SMS_MOBILE_"+mobile + "_"+messageType, 1, TimeUnit.DAYS);
						// 设置为第二天0点过期
						redisTemplate2.expireAt("SMS_MOBILE_"+mobile, DateUtils.getAfterDay(new Date(), 1));
					}
					
					if(!ResultVo.isSuccess(vo)){
						return vo;
					}
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}finally{
					lock.unlock();// 释放锁 
				}

				Map<String,Object> dataMap=(Map<String,Object>)vo.getValue("data");
				SmsLogInfoEntity smsLogInfo = BeanMapConvertUtil.toBean(SmsLogInfoEntity.class, dataMap);
				smsLogInfo.setCreateDate(new Date());
				if (smsLogInfoRepository.save(smsLogInfo)==null) {
					return new ResultVo(false,"发送短信失败!");
				}else{				
					SmsInfoEntity entities=new SmsInfoEntity();
					entities.setCreateDate(new Date());
					entities.setTargetAddress(mobile);
					entities.setTargetType(Constant.TARGET_TYPE_TEL);
					entities.setSendDate(new Date());
					entities.setSendStatus("已发送");
					//读取配置文件，替换内容
					entities.setSendContent(sendContent);
					entities.setVerityCode(verityCode);
					entities.setMessageStatus("有效");
					//获取30分钟之后的时间
					entities.setLastValidTime(DateUtils.getDateAfterByMinute(new Date(), 30));
					if(smsInfoRepository.save(entities)==null){
						log.info("发送短信失败!"+smsLogInfo.getSendContent());
					}else{
						log.info("发送短信成功."+smsLogInfo.getSendContent());
					}
					return new ResultVo(true,"发送短信成功.");
				}
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResultVo(false,"发送短信失败!");
		}
	}
	
	/**
	 * 校验验证码
	 */
	@Override
	public ResultVo checkVerityCode(Map<String, Object> params)throws SLException {
		
		//根据邮箱地址和验证码和消息类型，查询最新消息信息 是为了统计有哪些验证码没有使用
		SmsInfoEntity smsInfoEntity = smsInfoRepository.checkTargetAddressAndVerityCode( (String)params.get("targetAddress"), (String)params.get("messageType"), new Date());
		if (smsInfoEntity == null)
			return new ResultVo(false,"验证码失效或者输入错误");
		if (!Constant.VALID_STATUS_VALID.equals(smsInfoEntity.getMessageStatus()))
			return new ResultVo(false,"验证码已失效");
		if (StringUtils.isEmpty(smsInfoEntity.getVerityCode()))
			return new ResultVo(false,"验证码不能为空");
		if (!smsInfoEntity.getVerityCode().equals((String)params.get("verityCode"))) {
			return new ResultVo(false,"验证码输入错误");
		}
		
		updateVerificationCodeStatus(smsInfoEntity);
		return new ResultVo(true);
	}

	/***    
	 * 校验验证码
	 */
	public ResultVo checkSmsCode(Map<String, Object> params) throws SLException{
		//根据邮箱地址和验证码和消息类型，查询最新消息信息 是为了统计有哪些验证码没有使用
		SmsInfoEntity smsInfoEntity = smsInfoRepository.checkTargetAddressAndVerityCode( (String)params.get("targetAddress"), (String)params.get("messageType"), new Date());
		if (smsInfoEntity == null)
			return new ResultVo(false,"验证码失效或者输入错误");
		if (!Constant.VALID_STATUS_VALID.equals(smsInfoEntity.getMessageStatus()))
			return new ResultVo(false,"验证码已失效");
		if (StringUtils.isEmpty(smsInfoEntity.getVerityCode()))
			return new ResultVo(false,"验证码不能为空");
		if (!smsInfoEntity.getVerityCode().equals((String)params.get("verityCode"))) {
			return new ResultVo(false,"验证码输入错误");
		}
		return new ResultVo(true);
	}
	
	@Override
	public ResultVo updateVerificationCodeStatus(SmsInfoEntity smsInfoEntity)
			throws SLException {
		/** 根据目标地址和消息类型修改验证码失效 **/		
		smsInfoEntity.setMessageStatus(Constant.VALID_STATUS_INVALID);
		smsInfoEntity.setLastUpdateDate(new Date());
		smsInfoRepository.save(smsInfoEntity);
		
		return new ResultVo(true);
	}

	@Override
	public ResultVo asnySendSMS(final Map<String, Object> params) {
		smsThreadPoolTaskExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				sendSMS(params);
				
			}
		});
		
		return new ResultVo(true); 
	}
	
	@Override
	public ResultVo asnySendRedSMS(final Map<String, Object> params) {
		smsThreadPoolTaskExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				sendRedSMS(params);
				
			}
		});
		
		return new ResultVo(true); 
	}

	@Override
	public ResultVo asnySendSMSAndSystemMessage(final Map<String, Object> params) {
		
		smsThreadPoolTaskExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				
				// 发短信
				if(params.containsKey("values")) {
					sendSMS(params);
				}

				// 发送站内信
				if(params.containsKey("systemMessage")) {
					String[] content = localProperties.getProperty(sysContentKey((String)params.get("messageType"))).split("\\|");
					Map<String, Object> messageMap = Maps.newHashMap();
					messageMap.put("custId", (String)params.get("custId"));
					messageMap.put("title", content[0]);
					messageMap.put("content", MessageFormat.format(content[1], (Object[])params.get("systemMessage")));	
					systemMessageService.sendSystemMessage(messageMap);
				}
			}
		});
		
		return new ResultVo(true); 
	}

	@Override
	public ResultVo asnySendSystemMessage(final Map<String, Object> params) {
		
		smsThreadPoolTaskExecutor.execute(new Runnable() {

			@Override
			public void run() {
				// 发送站内信
				String[] content = localProperties.getProperty(sysContentKey((String)params.get("messageType"))).split("\\|");
				Map<String, Object> messageMap = Maps.newHashMap();
				messageMap.put("custId", (String)params.get("custId"));
				messageMap.put("title", content[0]);
				messageMap.put("content", MessageFormat.format(content[1], (Object[])params.get("systemMessage")));	
				systemMessageService.sendSystemMessage(messageMap);
			}
			
		});
		return new ResultVo(true); 
	}
}
