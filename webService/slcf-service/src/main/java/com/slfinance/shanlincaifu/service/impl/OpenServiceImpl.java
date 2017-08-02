/** 
 * @(#)OpenServiceImpl.java 1.0.0 2015年7月2日 下午1:53:55  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.slfinance.shanlincaifu.service.*;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.google.common.xml.XmlEscapers;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.ExpandInfoEntity;
import com.slfinance.shanlincaifu.entity.InterfaceDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeLogInfoEntity;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.ExpandInfoRepository;
import com.slfinance.shanlincaifu.repository.InterfaceDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.TradeLogInfoRepository;
import com.slfinance.shanlincaifu.utils.AesUtil;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.Json;
import com.slfinance.shanlincaifu.utils.OpenSerivceCode;
import com.slfinance.shanlincaifu.utils.PasswordUtil;
import com.slfinance.shanlincaifu.validate.RuleUtils;
import com.slfinance.vo.ResultVo;

/**   
 * 开放服务实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月2日 下午1:53:55 $ 
 */
@Slf4j
@Service("openServiceImpl")
public class OpenServiceImpl implements OpenService {

	@Autowired
	private ExpandInfoRepository expandInfoRepository;
	
	@Autowired
	private TradeLogInfoRepository tradeLogInfoRepository;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private OpenNotifyService openNotifyService;
	
	@Autowired
	private InterfaceDetailInfoRepository interfaceDetailInfoRepository;
	
	@Autowired
	private SMSService sMSService;
	
//	@Autowired
//	private AmqpTemplate amqpTemplate;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private LoanProjectService loanProjectService;
	
	@Autowired
	private LoanManagerService loanManagerService;
	
	@Autowired
	private LoanReserveService loanReserveService;
	
	@Autowired
	private RepaymentChangeService repaymentChangeService;
	@Autowired
	private BankCardService bankCardService;
	
	@Autowired
	private LoanRepaymentService loanRepaymentService;
	
	@Autowired
	private WdzjService wdzjService;
	
	@Autowired
	@Qualifier("smsThreadPoolTaskExecutor")
	private Executor smsThreadPoolTaskExecutor;
	
	@Autowired
	private ExportFileService exportFileService;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private WithHoldingService withHoldingService;
	
	@Value("${wx.appID}")
	private String appID;

	@Value("${wx.appSecret}")
	private String appSecret;
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public Map<String, Object> openRegister(Map<String, Object> params) throws SLException {
		if(params.containsKey("validateCode")) {
			return openNewRegister(params);
		}
		else {
			params.put("noValidateCode", "true");
			return openOldRegister(params);
		}
//		// 仅支持新接口
//		return openNewRegister(params);
	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public Map<String, Object> openOldRegister(Map<String, Object> params) throws SLException {
		log.info("收到的报文：" + params.toString());
		// 设置接口类型为注册
		params.put("interfaceType", Constant.OPERATION_TYPE_16);
		// 1、记录接收到的报文
		String innerTradeCode = numberService.generateOpenServiceTradeNumber();
		TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
		tradeLogInfoEntity.setThirdPartyType("");
		tradeLogInfoEntity.setInterfaceType((String)params.get("interfaceType"));
		tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		tradeLogInfoEntity.setTradeCode((String)params.get("utid"));
		tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
		tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(params));
		internalOpenService.saveResponseTradeLog("");
		tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);
		
		// 2、字段不全或缺少必填字段校验
		if(!RuleUtils.required(params.get("mobile"))
			|| !RuleUtils.required(params.get("loginName"))
			|| !RuleUtils.required(params.get("loginPassword"))
			|| !RuleUtils.required(params.get("utid"))
			|| !RuleUtils.required(params.get("channelNo"))
			|| !RuleUtils.required(params.get("requestTime"))
			|| !RuleUtils.required(params.get("sign"))
			) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		
		// 3、手机格式错误
		if(!RuleUtils.isMobile(params.get("mobile"))){
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_MOBILE_RULE.toString());
			return OpenSerivceCode.ERR_MOBILE_RULE.toMap();
		}
		
		// 4、昵称格式错误
		if(	!RuleUtils.minLength(params.get("loginName"), 7)
			|| !RuleUtils.maxLength(params.get("loginName"), 17)
			|| !RuleUtils.isLetterOrDigitOrSymbol(params.get("loginName"))){
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_LOGINNAME_RULE.toString());
			return OpenSerivceCode.ERR_LOGINNAME_RULE.toMap();
		}
		
		// 5、密码格式错误
		if(	!RuleUtils.minLength(params.get("loginPassword"), 7)
				|| !RuleUtils.maxLength(params.get("loginPassword"), 17)
				|| RuleUtils.isLetter(params.get("loginPassword")) // 纯字母
				|| RuleUtils.isDigist(params.get("loginPassword")) // 纯数字
				|| !RuleUtils.containsLetterAndDigit(params.get("loginPassword")) // 同时包含数字和字母
				){
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_PASSWORD_RULE.toString());
				return OpenSerivceCode.ERR_PASSWORD_RULE.toMap();
			}
		
		// 6、请求时间格式错误
		if(	!RuleUtils.dateFormat(params.get("requestTime"), "yyyyMMddHHmmss")){
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_REQUESTTIME_RULE.toString());
			return OpenSerivceCode.ERR_REQUESTTIME_RULE.toMap();
		}
		
		// 7、验证渠道是否有效
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByInterfaceMerchantCode((String)params.get("interfaceType"), (String)params.get("channelNo"));
		if(interfaceDetailInfoEntity == null) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_INVALID_CHANNEL.toString());
			return OpenSerivceCode.ERR_INVALID_CHANNEL.toMap();
		}
		else {
			params.put("thirdPartyType", interfaceDetailInfoEntity.getThirdPartyType());
			tradeLogInfoEntity.setThirdPartyType(interfaceDetailInfoEntity.getThirdPartyType());
		}
		
		// 8、验签错误
		String hashString = "slb" + (String)params.get("mobile") + (String)params.get("loginName")
		+ (String)params.get("loginPassword") + (String)params.get("ipAddress") + (String)params.get("utid") 
		+ (String)params.get("channelNo") + (String)params.get("requestTime");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		if (!sign.equalsIgnoreCase((String)params.get("sign"))) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_SIGN.toString());
			return OpenSerivceCode.ERR_SIGN.toMap();
		}
		
		// 9、调用注册接口
		String loginPassword = (String)params.get("loginPassword");
		params.put("loginPassword", Hashing.md5().hashString(XmlEscapers.xmlContentEscaper().escape(loginPassword), Charsets.UTF_8).toString());//密码进行MD5加密
		ResultVo resultVo = customerService.innerRegister(params);
		if(!ResultVo.isSuccess(resultVo)) {
			log.error("第三方注册失败：" + resultVo.toString());
			OpenSerivceCode openCode = (OpenSerivceCode)resultVo.getValue("data");
			if(openCode == null){ // 没有返回错误，则返回其他错误
				tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_OTHER.toString());
				return OpenSerivceCode.ERR_OTHER.toMap();
			}
			else { // 其他异常
				tradeLogInfoEntity.setResponseMessage(openCode.toString());
				return openCode.toMap();
			}
		}
		
		// 10、记录客户附属信息
		CustInfoEntity custInfoEntity = custInfoRepository.findByMobile((String)params.get("mobile"));
		custInfoEntity.setLoginPwdLevel(PasswordUtil.getPwdLevel(PasswordUtil.getPwdSecurityLevel(loginPassword)));
		custInfoEntity.setChannelSource((String)params.get("thirdPartyType"));
		ExpandInfoEntity expandInfoEntity = new ExpandInfoEntity();
		expandInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_CUST_INFO);
		expandInfoEntity.setRelatePrimary(custInfoEntity.getId());
		expandInfoEntity.setInnerTradeCode(innerTradeCode);
		expandInfoEntity.setTradeCode((String)params.get("utid"));
		expandInfoEntity.setThirdPartyType((String)params.get("thirdPartyType"));
		expandInfoEntity.setInterfaceType((String)params.get("interfaceType"));
		expandInfoEntity.setMerchantCode((String)params.get("channelNo"));
		expandInfoEntity.setRequestTime((String)params.get("requestTime"));
		expandInfoEntity.setAlreadyNotifyTimes(0);
		expandInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		expandInfoEntity = expandInfoRepository.save(expandInfoEntity);
		
		// 11、更新日志
		tradeLogInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_CUST_INFO);
		tradeLogInfoEntity.setRelatePrimary(custInfoEntity.getId());
		tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.SUCCESS.toString());
		
		// 12、异步通知
		//openNotifyService.singleAsynNotify(expandInfoEntity);
		
		return OpenSerivceCode.SUCCESS.toMap();
	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public Map<String, Object> openNewRegister(Map<String, Object> params) throws SLException {
		log.info("收到的报文：" + params.toString());
		// 设置接口类型为注册
		params.put("interfaceType", Constant.OPERATION_TYPE_16);
		// 1、记录接收到的报文
		String innerTradeCode = numberService.generateOpenServiceTradeNumber();
		TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
		tradeLogInfoEntity.setThirdPartyType("");
		tradeLogInfoEntity.setInterfaceType((String)params.get("interfaceType"));
		tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		tradeLogInfoEntity.setTradeCode((String)params.get("utid"));
		tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
		tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(params));
		tradeLogInfoEntity.setResponseMessage("");
		tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);
		
		// 2、字段不全或缺少必填字段校验
		if(!RuleUtils.required(params.get("mobile"))
			//|| !RuleUtils.required(params.get("loginName"))
			|| !RuleUtils.required(params.get("loginPassword"))
			//|| !RuleUtils.required(params.get("utid"))
			|| !RuleUtils.required(params.get("channelNo"))
			|| !RuleUtils.required(params.get("requestTime"))
			|| !RuleUtils.required(params.get("validateCode"))
			|| !RuleUtils.required(params.get("appSource"))
			|| !RuleUtils.required(params.get("sign"))
			) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		
		// 3、手机格式错误
		if(!RuleUtils.isMobile(params.get("mobile"))){
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_MOBILE_RULE.toString());
			return OpenSerivceCode.ERR_MOBILE_RULE.toMap();
		}
		
		// 4、昵称格式错误
		if(!StringUtils.isEmpty((String)params.get("loginName"))) { // 若包含昵称则校验昵称，否则自动生成一个昵称
			if(	!RuleUtils.minLength(params.get("loginName"), 7)
					|| !RuleUtils.maxLength(params.get("loginName"), 17)
					|| !RuleUtils.isLetterOrDigitOrSymbol(params.get("loginName"))){
					tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_LOGINNAME_RULE.toString());
					return OpenSerivceCode.ERR_LOGINNAME_RULE.toMap();
				}
		}

		// 5、密码格式错误
		if(	!RuleUtils.minLength(params.get("loginPassword"), 7)
				|| !RuleUtils.maxLength(params.get("loginPassword"), 17)
				|| RuleUtils.isLetter(params.get("loginPassword")) // 纯字母
				|| RuleUtils.isDigist(params.get("loginPassword")) // 纯数字
				|| !RuleUtils.containsLetterAndDigit(params.get("loginPassword")) // 同时包含数字和字母
				){
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_PASSWORD_RULE.toString());
				return OpenSerivceCode.ERR_PASSWORD_RULE.toMap();
			}
		
		// 6、请求时间格式错误
		if(	!RuleUtils.dateFormat(params.get("requestTime"), "yyyyMMddHHmmss")){
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_REQUESTTIME_RULE.toString());
			return OpenSerivceCode.ERR_REQUESTTIME_RULE.toMap();
		}
		
		// 7、验证码格式校验
		if( !RuleUtils.minLength(params.get("validateCode"), 5)
				||!RuleUtils.maxLength(params.get("validateCode"), 7)
				||!RuleUtils.isDigist(params.get("validateCode"))) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_SMSCODE_RULE.toString());
			return OpenSerivceCode.ERR_SMSCODE_RULE.toMap();
		}
		
		// 8、来源格式校验
		if(!RuleUtils.inRange(params.get("appSource"), new String[] {"web","wap","android","ios","winphone","others"})) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_INVALID_APPSOURCE.toString());
			return OpenSerivceCode.ERR_INVALID_APPSOURCE.toMap();
		}
		
		// 9、验证渠道是否有效
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByInterfaceMerchantCode((String)params.get("interfaceType"), (String)params.get("channelNo"));
		if(interfaceDetailInfoEntity == null) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_INVALID_CHANNEL.toString());
			return OpenSerivceCode.ERR_INVALID_CHANNEL.toMap();
		}
		else {
			params.put("thirdPartyType", interfaceDetailInfoEntity.getThirdPartyType());
			tradeLogInfoEntity.setThirdPartyType(interfaceDetailInfoEntity.getThirdPartyType());
		}
		
		// 10、验签错误
		String hashString = "slb" + (String)params.get("mobile") + StringUtils.stripToEmpty((String)params.get("loginName"))
		+ (String)params.get("loginPassword") + StringUtils.stripToEmpty((String)params.get("ipAddress")) + StringUtils.stripToEmpty((String)params.get("utid")) 
		+ (String)params.get("channelNo") + (String)params.get("requestTime") + (String)params.get("validateCode") 
		+ (String)params.get("appSource");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		if (!sign.equalsIgnoreCase((String)params.get("sign"))) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_SIGN.toString());
			return OpenSerivceCode.ERR_SIGN.toMap();
		}
		
		// 11、调用注册接口
		String loginPassword = (String)params.get("loginPassword");
		params.put("loginPassword", Hashing.md5().hashString(XmlEscapers.xmlContentEscaper().escape(loginPassword), Charsets.UTF_8).toString());//密码进行MD5加密
		params.put("custSource", (String)params.get("appSource"));
		params.put("verityCode", (String)params.get("validateCode"));
		if(StringUtils.isEmpty((String)params.get("loginName"))) {
			params.put("loginName", numberService.generateUserNickName());
		}
		ResultVo resultVo = customerService.innerRegister(params);
		if(!ResultVo.isSuccess(resultVo)) {
			log.error("第三方注册失败：" + resultVo.toString());
			OpenSerivceCode openCode = (OpenSerivceCode)resultVo.getValue("data");
			if(openCode == null){ // 没有返回错误，则返回其他错误
				tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_OTHER.toString());
				return OpenSerivceCode.ERR_OTHER.toMap();
			}
			else { // 其他异常
				tradeLogInfoEntity.setResponseMessage(openCode.toString());
				return openCode.toMap();
			}
		}
		
		// 12、记录客户附属信息
		CustInfoEntity custInfoEntity = custInfoRepository.findByMobile((String)params.get("mobile"));
		custInfoEntity.setLoginPwdLevel(PasswordUtil.getPwdLevel(PasswordUtil.getPwdSecurityLevel(loginPassword)));
		custInfoEntity.setChannelSource((String)params.get("thirdPartyType"));
		ExpandInfoEntity expandInfoEntity = new ExpandInfoEntity();
		expandInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_CUST_INFO);
		expandInfoEntity.setRelatePrimary(custInfoEntity.getId());
		expandInfoEntity.setInnerTradeCode(innerTradeCode);
		expandInfoEntity.setTradeCode((String)params.get("utid"));
		expandInfoEntity.setThirdPartyType((String)params.get("thirdPartyType"));
		expandInfoEntity.setInterfaceType((String)params.get("interfaceType"));
		expandInfoEntity.setMerchantCode((String)params.get("channelNo"));
		expandInfoEntity.setRequestTime((String)params.get("requestTime"));
		expandInfoEntity.setAlreadyNotifyTimes(0);
		expandInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		expandInfoEntity = expandInfoRepository.save(expandInfoEntity);
		
		// 13、更新日志
		tradeLogInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_CUST_INFO);
		tradeLogInfoEntity.setRelatePrimary(custInfoEntity.getId());
		tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.SUCCESS.toString());
		
		// 14、异步通知
		//openNotifyService.singleAsynNotify(expandInfoEntity);
		
		return OpenSerivceCode.SUCCESS.toMap();
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public Map<String, Object> openSendSms(Map<String, Object> params)
			throws SLException {
		
		log.info("收到的报文：" + params.toString());
		// 设置接口类型为注册
		params.put("interfaceType", "注册短信验证码");
		
		// 1、记录接收到的报文
		String innerTradeCode = numberService.generateOpenServiceTradeNumber();
		TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
		tradeLogInfoEntity.setThirdPartyType("");
		tradeLogInfoEntity.setInterfaceType((String)params.get("interfaceType"));
		tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		tradeLogInfoEntity.setTradeCode((String)params.get("utid"));
		tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
		tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(params));
		tradeLogInfoEntity.setResponseMessage("");
		tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);
		
		// 2、字段不全或缺少必填字段校验
		if(!RuleUtils.required(params.get("mobile"))
			|| !RuleUtils.required(params.get("utid"))
			|| !RuleUtils.required(params.get("channelNo"))
			|| !RuleUtils.required(params.get("requestTime"))
			|| !RuleUtils.required(params.get("sign"))
			) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		
		// 3、手机格式错误
		if(!RuleUtils.isMobile(params.get("mobile"))){
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_MOBILE_RULE.toString());
			return OpenSerivceCode.ERR_MOBILE_RULE.toMap();
		}
		
		// 4、请求时间格式错误
		if(	!RuleUtils.dateFormat(params.get("requestTime"), "yyyyMMddHHmmss")){
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_REQUESTTIME_RULE.toString());
			return OpenSerivceCode.ERR_REQUESTTIME_RULE.toMap();
		}
		
		// 5、验证渠道是否有效
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByInterfaceMerchantCode((String)params.get("interfaceType"), (String)params.get("channelNo"));
		if(interfaceDetailInfoEntity == null) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_INVALID_CHANNEL.toString());
			return OpenSerivceCode.ERR_INVALID_CHANNEL.toMap();
		}
		else {
			params.put("thirdPartyType", interfaceDetailInfoEntity.getThirdPartyType());
			tradeLogInfoEntity.setThirdPartyType(interfaceDetailInfoEntity.getThirdPartyType());
		}
		
		// 6、验签错误
		String hashString = "slb" + (String)params.get("mobile") + StringUtils.stripToEmpty((String)params.get("utid")) 
		+ (String)params.get("channelNo") + (String)params.get("requestTime");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		if (!sign.equalsIgnoreCase((String)params.get("sign"))) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_SIGN.toString());
			return OpenSerivceCode.ERR_SIGN.toMap();
		}
		
		// 7、手机号码重复
		CustInfoEntity custInfoEntity = custInfoRepository.findByMobile((String)params.get("mobile"));
		if(custInfoEntity != null) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_MOBILE_REPEATE.toString());
			return OpenSerivceCode.ERR_MOBILE_REPEATE.toMap();
		}
		
		// 8、日发送次数超过10次或月超过100次
		params.put("smsType", (String)params.get("messageType"));
		Map<String,Object> countTimes = sMSService.findByAddressAndTypeAndDate(params);
		if(new Integer(countTimes.get("day").toString()).compareTo(10) > 0
				|| new Integer(countTimes.get("month").toString()).compareTo(100) > 0) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_FREQUENCY_SMSCODE.toString());
			return OpenSerivceCode.ERR_FREQUENCY_SMSCODE.toMap();
		}
		
		// 9、发送短信验证码
		ResultVo resultVo = sMSService.sendSMS(params);
		if(!ResultVo.isSuccess(resultVo)) {
			log.error("第三方注册失败：" + resultVo.toString());
			Object obj = resultVo.getValue("data");
			OpenSerivceCode openCode = null;
			if(obj!= null && obj.getClass() == OpenSerivceCode.class) {
				openCode = (OpenSerivceCode)obj;
			}
			if(openCode == null){ // 没有返回错误，则返回其他错误
				tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_OTHER.toString());
				return OpenSerivceCode.ERR_OTHER.toMap();
			}
			else { // 其他异常
				tradeLogInfoEntity.setResponseMessage(openCode.toString());
				return openCode.toMap();
			}
		}
		
		tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.SUCCESS.toString());
		return OpenSerivceCode.SUCCESS.toMap();
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public void saveRealNameAuth(Map<String, Object> params)
			throws SLException {
		
		String custId = (String)params.get("custId");
		String channelNo = (String)params.get("channelNo");
		String utid = (String)params.get("utid");
		
		// 渠道号为空或者校花号为空则返回
		if(StringUtils.isEmpty(channelNo) 
				|| StringUtils.isEmpty(utid)){
			return;
		}
		
		// 判断渠道号是否非法
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByInterfaceMerchantCode(Constant.OPERATION_TYPE_03, channelNo);
		if(interfaceDetailInfoEntity == null) {
			log.warn(String.format("渠道号[%s]非法", channelNo));
			return;
		}
		
		// 判断是否已经完成实名认证
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(StringUtils.isEmpty(custInfoEntity.getCredentialsCode()) 
				|| StringUtils.isEmpty(custInfoEntity.getCustName()) ){
			return;
		}
		
		// 保存通过某个校花实名认证信息
		ExpandInfoEntity expandInfoEntity = new ExpandInfoEntity();
		expandInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_CUST_INFO);
		expandInfoEntity.setRelatePrimary(custInfoEntity.getId());
		expandInfoEntity.setInnerTradeCode(numberService.generateOpenServiceTradeNumber());
		expandInfoEntity.setTradeCode(utid);
		expandInfoEntity.setThirdPartyType(interfaceDetailInfoEntity.getThirdPartyType());
		expandInfoEntity.setInterfaceType(Constant.OPERATION_TYPE_03);
		expandInfoEntity.setMerchantCode(channelNo);
		expandInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		expandInfoEntity.setAlreadyNotifyTimes(0);
		expandInfoEntity.setBasicModelProperty(custId, true);
		expandInfoRepository.save(expandInfoEntity);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public void saveRecharge(Map<String, Object> params) throws SLException {
		
		String tradeFlowId = (String)params.get("tradeFlowId");
		String custId = (String)params.get("custId");
		String channelNo = (String)params.get("channelNo");
		String utid = (String)params.get("utid");
		
		// 渠道号为空或者校花号为空则返回
		if(StringUtils.isEmpty(channelNo) 
				|| StringUtils.isEmpty(utid)){
			return;
		}
		
		// 判断渠道号是否非法
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByInterfaceMerchantCode(Constant.OPERATION_TYPE_05, channelNo);
		if(interfaceDetailInfoEntity == null) {
			log.warn(String.format("渠道号[%s]非法", channelNo));
			return;
		}
		
		// 保存通过某个校花充值信息
		ExpandInfoEntity expandInfoEntity = new ExpandInfoEntity();
		expandInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
		expandInfoEntity.setRelatePrimary(tradeFlowId);
		expandInfoEntity.setInnerTradeCode(numberService.generateOpenServiceTradeNumber());
		expandInfoEntity.setTradeCode(utid);
		expandInfoEntity.setThirdPartyType(interfaceDetailInfoEntity.getThirdPartyType());
		expandInfoEntity.setInterfaceType(Constant.OPERATION_TYPE_05);
		expandInfoEntity.setMerchantCode(channelNo);
		expandInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		expandInfoEntity.setAlreadyNotifyTimes(0);
		expandInfoEntity.setBasicModelProperty(custId, true);
		expandInfoRepository.save(expandInfoEntity);
	}

	@Override
	public Map<String, Object> queryRealNameAuth(Map<String, Object> params)
			throws SLException {
		log.info("收到的报文：" + params.toString());
		// 设置接口类型为查询实名认证
		params.put("interfaceType", "查询实名认证");
		
		// 1、记录接收到的报文
		String innerTradeCode = numberService.generateOpenServiceTradeNumber();
		TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
		tradeLogInfoEntity.setThirdPartyType("");
		tradeLogInfoEntity.setInterfaceType((String)params.get("interfaceType"));
		tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		tradeLogInfoEntity.setTradeCode((String)params.get("utid"));
		tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
		tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(params));
		tradeLogInfoEntity.setResponseMessage("");
		tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);
		
		// 2、字段不全或缺少必填字段校验
		if(!RuleUtils.required(params.get("mobile"))
			|| !RuleUtils.required(params.get("utid"))
			|| !RuleUtils.required(params.get("channelNo"))
			|| !RuleUtils.required(params.get("requestTime"))
			|| !RuleUtils.required(params.get("sign"))
			) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		
		// 3、手机格式错误
		if(!RuleUtils.isMobile(params.get("mobile"))){
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_MOBILE_RULE.toString());
			return OpenSerivceCode.ERR_MOBILE_RULE.toMap();
		}
		
		// 4、请求时间格式错误
		if(	!RuleUtils.dateFormat(params.get("requestTime"), "yyyyMMddHHmmss")){
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_REQUESTTIME_RULE.toString());
			return OpenSerivceCode.ERR_REQUESTTIME_RULE.toMap();
		}
		
		// 5、验证渠道是否有效
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByInterfaceMerchantCode((String)params.get("interfaceType"), (String)params.get("channelNo"));
		if(interfaceDetailInfoEntity == null) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_INVALID_CHANNEL.toString());
			return OpenSerivceCode.ERR_INVALID_CHANNEL.toMap();
		}
		else {
			params.put("thirdPartyType", interfaceDetailInfoEntity.getThirdPartyType());
			tradeLogInfoEntity.setThirdPartyType(interfaceDetailInfoEntity.getThirdPartyType());
		}
		
		// 6、验签错误
		String hashString = "slb" + (String)params.get("mobile") + (String)params.get("utid") 
		+ (String)params.get("channelNo") + (String)params.get("requestTime");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		if (!sign.equalsIgnoreCase((String)params.get("sign"))) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_SIGN.toString());
			return OpenSerivceCode.ERR_SIGN.toMap();
		}
		
		// 7、查询实名认证情况
		CustInfoEntity custInfoEntity = custInfoRepository.findByMobile((String)params.get("mobile"));
		if(custInfoEntity == null){
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_USER_NO_DATA_FOUND.toString());
			return OpenSerivceCode.ERR_USER_NO_DATA_FOUND.toMap();
		}
		List<ExpandInfoEntity> expandList = expandInfoRepository.findByRelatePrimaryAndTradeCode(custInfoEntity.getId(), (String)params.get("thirdPartyType"), Constant.OPERATION_TYPE_03, (String)params.get("utid"));
		Map<String, Object> result = OpenSerivceCode.SUCCESS.toMap();
		if(expandList != null && expandList.size() > 0) {
			result.put("task", "T");
		}
		else {
			result.put("task", "F");
		}
		return result;
	}

	@Override
	public Map<String, Object> queryRecharge(Map<String, Object> params) throws SLException {
		log.info("收到的报文：" + params.toString());
		// 设置接口类型为查询充值
		params.put("interfaceType", "查询充值");
		
		// 1、记录接收到的报文
		String innerTradeCode = numberService.generateOpenServiceTradeNumber();
		TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
		tradeLogInfoEntity.setThirdPartyType("");
		tradeLogInfoEntity.setInterfaceType((String)params.get("interfaceType"));
		tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		tradeLogInfoEntity.setTradeCode((String)params.get("utid"));
		tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
		tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(params));
		tradeLogInfoEntity.setResponseMessage("");
		tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);
		
		// 2、字段不全或缺少必填字段校验
		if(!RuleUtils.required(params.get("mobile"))
			|| !RuleUtils.required(params.get("utid"))
			|| !RuleUtils.required(params.get("channelNo"))
			|| !RuleUtils.required(params.get("requestTime"))
			|| !RuleUtils.required(params.get("sign"))
			) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		
		// 3、手机格式错误
		if(!RuleUtils.isMobile(params.get("mobile"))){
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_MOBILE_RULE.toString());
			return OpenSerivceCode.ERR_MOBILE_RULE.toMap();
		}
		
		// 4、请求时间格式错误
		if(	!RuleUtils.dateFormat(params.get("requestTime"), "yyyyMMddHHmmss")){
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_REQUESTTIME_RULE.toString());
			return OpenSerivceCode.ERR_REQUESTTIME_RULE.toMap();
		}
		
		// 5、验证渠道是否有效
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByInterfaceMerchantCode((String)params.get("interfaceType"), (String)params.get("channelNo"));
		if(interfaceDetailInfoEntity == null) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_INVALID_CHANNEL.toString());
			return OpenSerivceCode.ERR_INVALID_CHANNEL.toMap();
		}
		else {
			params.put("thirdPartyType", interfaceDetailInfoEntity.getThirdPartyType());
			tradeLogInfoEntity.setThirdPartyType(interfaceDetailInfoEntity.getThirdPartyType());
		}
		
		// 6、验签错误
		String hashString = "slb" + (String)params.get("mobile") + (String)params.get("utid") 
		+ (String)params.get("channelNo") + (String)params.get("requestTime");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		if (!sign.equalsIgnoreCase((String)params.get("sign"))) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_SIGN.toString());
			return OpenSerivceCode.ERR_SIGN.toMap();
		}
		
		// 7、查询充值情况
		CustInfoEntity custInfoEntity = custInfoRepository.findByMobile((String)params.get("mobile"));
		if(custInfoEntity == null){
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_USER_NO_DATA_FOUND.toString());
			return OpenSerivceCode.ERR_USER_NO_DATA_FOUND.toMap();
		}
		BigDecimal total = expandInfoRepository.sumByCustIdAndTradeCode(custInfoEntity.getId(), (String)params.get("thirdPartyType"), Constant.OPERATION_TYPE_05, (String)params.get("utid"));
		Map<String, Object> result = OpenSerivceCode.SUCCESS.toMap();
		if(total != null && total.compareTo(new BigDecimal("8")) >= 0) { // 总充值金额大于8元
			result.put("task", "T");
		}
		else {
			result.put("task", "F");
		}
		return result;
		
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public Map<String, Object> saveDownloadMessage(Map<String, Object> params)
			throws SLException {
		
		log.info("收到的报文：" + params.toString());
		
		// 设置接口类型为下载
		params.put("interfaceType", Constant.OPERATION_TYPE_24);
		
		// 1、记录接收到的报文
		String innerTradeCode = numberService.generateOpenServiceTradeNumber();
		TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
		tradeLogInfoEntity.setThirdPartyType("");
		tradeLogInfoEntity.setInterfaceType((String)params.get("interfaceType"));
		tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		tradeLogInfoEntity.setTradeCode((String)params.get("requestNo"));
		tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
		tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(params));
		tradeLogInfoEntity.setResponseMessage("");
		tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);
		
		// 2、字段不全或缺少必填字段校验
		if(!RuleUtils.required(params.get("meid"))
				|| !RuleUtils.required(params.get("appSource"))
				|| !RuleUtils.required(params.get("requestNo"))
				|| !RuleUtils.required(params.get("requestTime"))
				|| !RuleUtils.required(params.get("channelNo"))
				|| !RuleUtils.required(params.get("sign"))
				) {
				tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_LACK_FIELD.toString());
				return OpenSerivceCode.ERR_LACK_FIELD.toMap();
			}
		
		// 3、请求时间格式错误
		if(	!RuleUtils.dateFormat(params.get("requestTime"), "yyyyMMddHHmmss")){
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_REQUESTTIME_RULE.toString());
			return OpenSerivceCode.ERR_REQUESTTIME_RULE.toMap();
		}
		
		// 4、来源格式校验
		if(!RuleUtils.inRange(params.get("appSource"), Constant.APP_SOURCES)) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_INVALID_APPSOURCE.toString());
			return OpenSerivceCode.ERR_INVALID_APPSOURCE.toMap();
		}
		
		// 5、验证渠道是否有效
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByInterfaceMerchantCode((String)params.get("interfaceType"), (String)params.get("channelNo"));
		if(interfaceDetailInfoEntity == null) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_INVALID_CHANNEL.toString());
			return OpenSerivceCode.ERR_INVALID_CHANNEL.toMap();
		}
		else {
			params.put("thirdPartyType", interfaceDetailInfoEntity.getThirdPartyType());
			tradeLogInfoEntity.setThirdPartyType(interfaceDetailInfoEntity.getThirdPartyType());
		}
		
		// 6、验签错误
		String hashString = "slb" + StringUtils.stripToEmpty((String)params.get("mobile")) + (String)params.get("meid")
		+ StringUtils.stripToEmpty((String)params.get("meversion")) + (String)params.get("appSource") + (String)params.get("requestNo") 
		+ (String)params.get("requestTime") + (String)params.get("channelNo") + StringUtils.stripToEmpty((String)params.get("partner_no"))
		+ StringUtils.stripToEmpty((String)params.get("callback"));
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		if (!sign.equalsIgnoreCase((String)params.get("sign"))) {
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.ERR_SIGN.toString());
			return OpenSerivceCode.ERR_SIGN.toMap();
		}
		
		// 7、记录扩展信息
		ExpandInfoEntity expandInfoEntity = expandInfoRepository.findByTradeCodeAndMeId((String)params.get("partner_no"), (String)params.get("meid"));
		if(expandInfoEntity != null) {
			tradeLogInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_EXPAND_INFO);
			tradeLogInfoEntity.setRelatePrimary(expandInfoEntity.getId());
			tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.SUCCESS.toString());
			return OpenSerivceCode.SUCCESS.toMap();
		}
		expandInfoEntity = new ExpandInfoEntity();
		expandInfoEntity.setInnerTradeCode(innerTradeCode);
		expandInfoEntity.setTradeCode((String)params.get("partner_no"));
		expandInfoEntity.setThirdPartyType((String)params.get("thirdPartyType"));
		expandInfoEntity.setInterfaceType((String)params.get("interfaceType"));
		expandInfoEntity.setMerchantCode((String)params.get("channelNo"));
		expandInfoEntity.setRequestTime((String)params.get("requestTime"));
		expandInfoEntity.setMeId((String)params.get("meid"));
		expandInfoEntity.setMeVersion((String)params.get("meversion"));
		expandInfoEntity.setAppSource(Strings.nullToEmpty((String)params.get("appSource")).toLowerCase());
		expandInfoEntity.setCallBack((String)params.get("callback"));
		expandInfoEntity.setAlreadyNotifyTimes(0);
		expandInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		expandInfoEntity = expandInfoRepository.save(expandInfoEntity);
		
		// 8、更新日志
		tradeLogInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_EXPAND_INFO);
		tradeLogInfoEntity.setRelatePrimary(expandInfoEntity.getId());
		tradeLogInfoEntity.setResponseMessage(OpenSerivceCode.SUCCESS.toString());
		
		return OpenSerivceCode.SUCCESS.toMap();
	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public String confirmStatus(Map<String, Object> params)
			throws SLException {
		log.info("收到的报文：" + params.toString());
		
		//固定返回值
		String fixedRtn = "1";
		
		// 1、记录接收到的报文
		String innerTradeCode = numberService.generateOpenServiceTradeNumber();
		TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
		tradeLogInfoEntity.setThirdPartyType("");
		tradeLogInfoEntity.setInterfaceType("");
		tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		tradeLogInfoEntity.setTradeCode((String)params.get("request_no"));
		tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
		tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(params));
		tradeLogInfoEntity.setResponseMessage("");
		tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);
		
		// 2、更新扩展
		ExpandInfoEntity expandInfoEntity = expandInfoRepository.findByInnerTradeCode((String)params.get("request_no"));
		if(expandInfoEntity == null) {
			tradeLogInfoEntity.setResponseMessage(fixedRtn);
			return fixedRtn;
		}
		expandInfoEntity.setResponseCode((String)params.get("result_code"));
		
		// 3、更新日志
		tradeLogInfoEntity.setThirdPartyType(expandInfoEntity.getThirdPartyType());
		tradeLogInfoEntity.setInterfaceType(expandInfoEntity.getInterfaceType());
		tradeLogInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_EXPAND_INFO);
		tradeLogInfoEntity.setRelatePrimary(expandInfoEntity.getId());
		tradeLogInfoEntity.setResponseMessage(fixedRtn);
		
		return fixedRtn;
	}

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> params)
			throws SLException {
		//amqpTemplate.convertAndSend(Constant.AMQP_QUEUE_OPENSERVICE, params);
		return OpenSerivceCode.SUCCESS.toMap();
	}

	@Override
	public Map<String, Object> queryRegister(Map<String, Object> params)
			throws SLException {
		return deviceService.queryCacheDevice(params);
	}

	@Override
	public ResultVo queryFlowDataActivityUrl(Map<String, Object> params)
			throws SLException {
		
		String thirdPartyType = Constant.THIRD_PARTY_TYPE_JIRONGTONG;
		String interfaceType  = Constant.OPERATION_TYPE_40;
		String merchantCode = (String)params.get("channelNo");
		String custId = (String)params.get("custId");
		
		// 记录通知报文
		String innerTradeCode = numberService.generateOpenServiceTradeNumber();
		TradeLogInfoEntity tradeLogInfoEntity1 = new TradeLogInfoEntity();
		tradeLogInfoEntity1.setRelateTableIdentification(Constant.TABLE_BAO_T_CUST_INFO);
		tradeLogInfoEntity1.setRelatePrimary(custId);
		tradeLogInfoEntity1.setThirdPartyType(thirdPartyType);
		tradeLogInfoEntity1.setInterfaceType(interfaceType);
		tradeLogInfoEntity1.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		tradeLogInfoEntity1.setTradeCode("");
		tradeLogInfoEntity1.setInnerTradeCode(innerTradeCode);
		tradeLogInfoEntity1.setRequestMessage(Json.ObjectMapper.writeValue(params));
		tradeLogInfoEntity1.setResponseMessage("");
		tradeLogInfoEntity1.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		tradeLogInfoEntity1 = tradeLogInfoRepository.save(tradeLogInfoEntity1);
		
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyMerchantCode(thirdPartyType, interfaceType, merchantCode);
		if(interfaceDetailInfoEntity == null) {
			log.error(String.format("接口未找到！ThirdPartyType:%s, InterfaceType:%s, MerchantCode:%s", thirdPartyType, interfaceType, merchantCode));
			tradeLogInfoEntity1.setResponseMessage("渠道非法");
			return new ResultVo(false, "渠道非法");
		}
		
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null) {
			log.error(String.format("用户未找到！custId:%s", custId));
			tradeLogInfoEntity1.setResponseMessage("用户未找到");
			return new ResultVo(false, "用户非法");
		}
		
		// 非吉融用户
		if(!thirdPartyType.equals(custInfoEntity.getChannelSource())) {
			log.error(String.format("用户非渠道%s用户，不能参加活动！custId:%s", thirdPartyType, custId));
			tradeLogInfoEntity1.setResponseMessage(String.format("用户非渠道%s用户", thirdPartyType));
			return new ResultVo(false, "很抱歉，您无法参加活动哦！详情请见活动规则");
		}
				
		Map<String, Object> map = Maps.newHashMap();
		String activityId = interfaceDetailInfoEntity.getInterfaceDesc();
		String [] secretKey = interfaceDetailInfoEntity.getSecretKey().trim().split("\\|");
		map.put("uid", custId);
		map.put("uright", "1");
		map.put("phone_number", custInfoEntity.getMobile());

		//加密map
		String orderCode = AesUtil.encodeBytes(
				AesUtil.cbc128Encrypt(secretKey[1], secretKey[0], Json.ObjectMapper.writeValue(map)));
		
		Map<String, Object> orderMap = Maps.newHashMap();
		orderMap.put("activity_id", activityId);
		orderMap.put("param", orderCode);
		String url = String.format("%s?%s", interfaceDetailInfoEntity.getAsyncNotifyUrl(), CommonUtils.getUrlParamsByMap(orderMap));
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("data", url);
		
		tradeLogInfoEntity1.setResponseMessage(Json.ObjectMapper.writeValue(result));
		
		return new ResultVo(true, "查询流量活动地址", result);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> saveProject(Map<String, Object> params) throws SLException {
		log.info("收到的报文：" + params.toString());
		
		params.put("interfaceType", Constant.NOTIFY_LOAN_LOAN_PROJECT);
		
		// 1、记录接收到的报文
		String tradeCode = (String)params.get("tradeCode");
		internalOpenService.saveRequestTradeLog(params);
		
		// 2、字段不全或缺少必填字段校验
		if(!RuleUtils.required(params.get("loanNo"))
			|| !RuleUtils.required(params.get("companyName"))
			|| !RuleUtils.required(params.get("requestTime"))
			|| !RuleUtils.required(params.get("tradeCode"))
			|| !RuleUtils.required(params.get("applyTime"))
			|| !RuleUtils.required(params.get("loanDesc"))
			|| !RuleUtils.required(params.get("loanType"))
			|| !RuleUtils.required(params.get("loanAmount"))
			|| !RuleUtils.required(params.get("loanTerm"))
			|| !RuleUtils.required(params.get("termUnit"))
			|| !RuleUtils.required(params.get("yearIrr"))
			|| !RuleUtils.required(params.get("repaymentMethod"))
			|| !RuleUtils.required(params.get("repaymentCycle"))
			|| !RuleUtils.required(params.get("sign"))
			) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		
		Map<String, Object> bankMap = (Map<String, Object>) params.get("bankMap");
		//如果是雪橙必须要有协议号码
//		if(StringUtils.equals(Constant.DEBT_SOURCE_XCJF, (String)params.get("companyName"))) {
//			if(!RuleUtils.required(params.get("noAgree"))) {
//				internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
//				return OpenSerivceCode.ERR_LACK_FIELD.toMap();
//			}
//		}
		if(Constant.REPAYMENT_METHOD_02.equals(params.get("repaymentMethod").toString())) {
			params.put("repaymentMethod", "每期还息到期付本");
		}
		
		// 3、请求时间格式错误
		if(	!RuleUtils.dateFormat(params.get("requestTime"), "yyyyMMddHHmmss")){
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_REQUESTTIME_RULE.toString());
			return OpenSerivceCode.ERR_REQUESTTIME_RULE.toMap();
		}
		
		// 检查客户信息是否存在
		if(!params.containsKey("custMap")) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		else {
			Map<String, Object> custMap = (Map<String, Object>)params.get("custMap");
			if(!RuleUtils.required(custMap.get("mobile"))
					|| !RuleUtils.required(custMap.get("custName"))
					|| !RuleUtils.required(custMap.get("custCode"))
					|| !RuleUtils.required(custMap.get("credentialsType"))
					|| !RuleUtils.required(custMap.get("credentialsCode"))
					|| !RuleUtils.required(custMap.get("education"))
					|| !RuleUtils.required(custMap.get("marriage"))
					) {
					internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
					return OpenSerivceCode.ERR_LACK_FIELD.toMap();
				}
		}
		
		// 检查银行信息是否存在
		/*if(!params.containsKey("bankMap")) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		else {
			if(!RuleUtils.required(bankMap.get("bankName"))
					|| !RuleUtils.required(bankMap.get("cardNo"))
					) {
					internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
					return OpenSerivceCode.ERR_LACK_FIELD.toMap();
				}
		}*/
		
		// 检查审核信息是否存在
		if(!params.containsKey("auditMap")) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		else {
			List<Map<String, Object>> auditMapList = (List<Map<String, Object>>)params.get("auditMap");
			for(Map<String, Object> auditMap : auditMapList) {
				if(!RuleUtils.required(auditMap.get("auditType"))
						//|| !RuleUtils.required(auditMap.get("auditUser"))
						|| !RuleUtils.required(auditMap.get("auditDate"))
						) {
						internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
						return OpenSerivceCode.ERR_LACK_FIELD.toMap();
					}
			}
		}
		
		// 检查还款信息是否存在
		if(!params.containsKey("repaymentList")) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		else {
			List<Map<String, Object>> repaymentList = (List<Map<String, Object>>)params.get("repaymentList");
			BigDecimal totalRepaymentPrincipal = BigDecimal.ZERO;
			for(Map<String, Object> repaymentMap : repaymentList) {
				if(!RuleUtils.required(repaymentMap.get("currentTerm"))
						|| !RuleUtils.required(repaymentMap.get("repaymentTotalAmount"))
						|| !RuleUtils.required(repaymentMap.get("repaymentPrincipal"))
						|| !RuleUtils.required(repaymentMap.get("repaymentInterest"))
						|| !RuleUtils.required(repaymentMap.get("advanceCleanupTotalAmount"))
						) {
						internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
						return OpenSerivceCode.ERR_LACK_FIELD.toMap();
					}
				totalRepaymentPrincipal = ArithUtil.add(totalRepaymentPrincipal, new BigDecimal(repaymentMap.get("repaymentPrincipal").toString()));
			}
			BigDecimal loanAmount = new BigDecimal(params.get("loanAmount").toString());
			if(totalRepaymentPrincipal.compareTo(loanAmount) != 0) { // 还款总本金不等于借款金额
				internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_INVALID_REPAYMENT.toString());
				return OpenSerivceCode.ERR_INVALID_REPAYMENT.toMap();
			}
		}
		
		// 3、验证渠道是否有效
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType((String)params.get("companyName"), (String)params.get("interfaceType"));
		if(interfaceDetailInfoEntity == null) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_INVALID_CHANNEL.toString());
			return OpenSerivceCode.ERR_INVALID_CHANNEL.toMap();
		}
		else {
			params.put("thirdPartyType", interfaceDetailInfoEntity.getThirdPartyType());
			params.put("debtSourceCode", interfaceDetailInfoEntity.getMerchantCode());
			internalOpenService.saveThirdPartyTypeTradeLog(interfaceDetailInfoEntity.getThirdPartyType());
		}
		
		// 4、验签错误 md5(Key+ companyName + effectDate +  requestTime)
		String hashString = interfaceDetailInfoEntity.getSecretKey() + (String)params.get("loanNo") + (String)params.get("requestTime") 
				+ (String)params.get("companyName") + (String)params.get("tradeCode");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		if (!sign.equalsIgnoreCase((String)params.get("sign"))) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_SIGN.toString());
			return OpenSerivceCode.ERR_SIGN.toMap();
		}
		
		// 5、验证交易编号是否重复
		TradeLogInfoEntity oldTradeLogInfoEntity = tradeLogInfoRepository.findByTradeCode(tradeCode);
		if(oldTradeLogInfoEntity != null) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_TRADE_NO_REPEATE.toString());
			return OpenSerivceCode.ERR_TRADE_NO_REPEATE.toMap();
		}
		
		// 保存借款
		try {
			ResultVo resultVo = loanProjectService.saveLoanProject(params);
			if(!ResultVo.isSuccess(resultVo)) {
				Object data = resultVo.getValue("data");
				Map<String, Object> result = Maps.newHashMap();
				if(data != null && data instanceof OpenSerivceCode) {
					result = ((OpenSerivceCode)data).toMap();
				}
				else {
					result = OpenSerivceCode.ERR_OTHER.toMap();
				}
				result.put("retMsg", resultVo.getValue("message") == null ? "发生未知异常" : (String)resultVo.getValue("message"));
				internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
				return result;
			}
		} catch (Exception e) {
			Map<String, Object> result = OpenSerivceCode.ERR_OTHER.toMap();
			result.put("retMsg", e.getMessage() == null ? "发生未知异常" : e.getMessage());
			internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
			return result;
		}
		
		internalOpenService.saveTradeCodeTradeLog(tradeCode);
		internalOpenService.saveResponseTradeLog(OpenSerivceCode.SUCCESS.toString());
		return OpenSerivceCode.SUCCESS.toMap();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> queryProject(Map<String, Object> params)
			throws SLException {
		log.info("收到的报文：" + params.toString());
		params.put("interfaceType", Constant.NOTIFY_TYPE_LOAN_STATUS);
		
		// 1、记录接收到的报文
		String tradeCode = (String)params.get("tradeCode");
		internalOpenService.saveRequestTradeLog(params);
		
		// 2、字段不全或缺少必填字段校验
		if(!RuleUtils.required(params.get("loanNo"))
			|| !RuleUtils.required(params.get("requestTime"))
			|| !RuleUtils.required(params.get("companyName"))
			|| !RuleUtils.required(params.get("tradeCode"))
			|| !RuleUtils.required(params.get("sign"))
			) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		
		// 3、请求时间格式错误
		if(	!RuleUtils.dateFormat(params.get("requestTime"), "yyyyMMddHHmmss")){
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_REQUESTTIME_RULE.toString());
			return OpenSerivceCode.ERR_REQUESTTIME_RULE.toMap();
		}
		
		// 4、验证渠道是否有效
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType((String)params.get("companyName"), (String)params.get("interfaceType"));
		if(interfaceDetailInfoEntity == null) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_INVALID_CHANNEL.toString());
			return OpenSerivceCode.ERR_INVALID_CHANNEL.toMap();
		}
		else {
			params.put("thirdPartyType", interfaceDetailInfoEntity.getThirdPartyType());
			internalOpenService.saveThirdPartyTypeTradeLog(interfaceDetailInfoEntity.getThirdPartyType());
		}
		
		// 5、验签错误 md5(Key+ companyName + projectNo +  requestTime)
		String hashString = interfaceDetailInfoEntity.getSecretKey() + (String)params.get("loanNo") + (String)params.get("requestTime") 
				+ (String)params.get("companyName") + (String)params.get("tradeCode");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		if (!sign.equalsIgnoreCase((String)params.get("sign"))) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_SIGN.toString());
			return OpenSerivceCode.ERR_SIGN.toMap();
		}
		
		// 5、验证交易编号是否重复
		TradeLogInfoEntity oldTradeLogInfoEntity = tradeLogInfoRepository.findByTradeCode(tradeCode);
		if(oldTradeLogInfoEntity != null) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_TRADE_NO_REPEATE.toString());
			return OpenSerivceCode.ERR_TRADE_NO_REPEATE.toMap();
		}
		params.put("openFlag", "flag");
		// 6、查询项目
		ResultVo resultVo = loanProjectService.queryProject(params);
		if(ResultVo.isSuccess(resultVo)) { // 查询项目成功
			Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
			Map<String, Object> result = OpenSerivceCode.SUCCESS.toMap();
			result.putAll(data);
			internalOpenService.saveTradeCodeTradeLog(tradeCode);
			internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
			return result;
		}
		else { // 查询项目失败
			Map<String, Object> result = OpenSerivceCode.ERR_NOT_EXISTS_PROJECT.toMap();
			result.put("retMsg", resultVo.getValue("message"));
			internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
			return result;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> queryPreAmount(Map<String, Object> params)
			throws SLException {
		log.info("收到的报文：" + params.toString());
		params.put("interfaceType", Constant.NOTIFY_TYPE_LOAN_AMOUNT);
		
		// 1、记录接收到的报文
		String tradeCode = (String)params.get("tradeCode");
		internalOpenService.saveRequestTradeLog(params);
		
		// 2、字段不全或缺少必填字段校验
		if(!RuleUtils.required(params.get("loanNo"))
			|| !RuleUtils.required(params.get("requestTime"))
			|| !RuleUtils.required(params.get("companyName"))
			|| !RuleUtils.required(params.get("tradeCode"))
			|| !RuleUtils.required(params.get("sign"))
			) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		
		// 3、请求时间格式错误
		if(	!RuleUtils.dateFormat(params.get("requestTime"), "yyyyMMddHHmmss")){
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_REQUESTTIME_RULE.toString());
			return OpenSerivceCode.ERR_REQUESTTIME_RULE.toMap();
		}
		
		// 4、验证渠道是否有效
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType((String)params.get("companyName"), (String)params.get("interfaceType"));
		if(interfaceDetailInfoEntity == null) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_INVALID_CHANNEL.toString());
			return OpenSerivceCode.ERR_INVALID_CHANNEL.toMap();
		}
		else {
			params.put("thirdPartyType", interfaceDetailInfoEntity.getThirdPartyType());
			internalOpenService.saveThirdPartyTypeTradeLog(interfaceDetailInfoEntity.getThirdPartyType());
		}
		
		// 5、验签错误 md5(Key+ companyName + projectNo +  requestTime)
		String hashString = interfaceDetailInfoEntity.getSecretKey() + (String)params.get("loanNo") + (String)params.get("requestTime") 
				+ (String)params.get("companyName") + (String)params.get("tradeCode");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		if (!sign.equalsIgnoreCase((String)params.get("sign"))) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_SIGN.toString());
			return OpenSerivceCode.ERR_SIGN.toMap();
		}
		
		// 5、验证交易编号是否重复
		TradeLogInfoEntity oldTradeLogInfoEntity = tradeLogInfoRepository.findByTradeCode(tradeCode);
		if(oldTradeLogInfoEntity != null) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_TRADE_NO_REPEATE.toString());
			return OpenSerivceCode.ERR_TRADE_NO_REPEATE.toMap();
		}
		params.put("openFlag", "flag");
		// 6、查询预约金额
		ResultVo resultVo = loanReserveService.queryRemainAmount(params);
		if(ResultVo.isSuccess(resultVo)) { // 查询项目成功
			Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
			Map<String, Object> result = OpenSerivceCode.SUCCESS.toMap();
			result.putAll(data);
			internalOpenService.saveTradeCodeTradeLog(tradeCode);
			internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
			return result;
		}
		else { // 查询预约金额失败
			Map<String, Object> result = OpenSerivceCode.ERR_NOT_EXISTS_PROJECT.toMap();
			result.put("retMsg", resultVo.getValue("message"));
			internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
			return result;
		}
	}
	
	/**
     * 接收流标/放款通知
     *
     * @author  zhagnze
     * @date    2017年6月2日 下午1:31:30
     * @param params
     * @return
     * @throws SLException
     */
    public Map<String, Object> operateProject(Map<String, Object> params) throws SLException {
        log.info("收到的报文：" + params.toString());
        if ("流标".equals(params.get("operateType"))) {
        	params.put("interfaceType", Constant.NOTIFY_TYPE_LOAN_BIDDERS);
        } else if ("放款".equals(params.get("operateType"))) {
        	params.put("interfaceType", Constant.NOTIFY_TYPE_LOAN_LENDING);
        } else if ("撤销".equals(params.get("operateType"))) {
        	params.put("interfaceType", Constant.NOTIFY_TYPE_LOAN_CANCEL);
        }
        // 1、记录接收到的报文
        String tradeCode = (String)params.get("tradeCode");
        internalOpenService.saveRequestTradeLog(params);
        
        // 2、字段不全或缺少必填字段校验
        if(!RuleUtils.required(params.get("loanNo"))
            || !RuleUtils.required(params.get("requestTime"))
            || !RuleUtils.required(params.get("companyName"))
            || !RuleUtils.required(params.get("tradeCode"))
            || !RuleUtils.required(params.get("operateType"))
            || !RuleUtils.required(params.get("sign"))
            ) {
            internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
            return OpenSerivceCode.ERR_LACK_FIELD.toMap();
        }
        
        // 3、请求时间格式错误
        if( !RuleUtils.dateFormat(params.get("requestTime"), "yyyyMMddHHmmss")){
            internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_REQUESTTIME_RULE.toString());
            return OpenSerivceCode.ERR_REQUESTTIME_RULE.toMap();
        }
        
        // 4、验证渠道是否有效
        InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType((String)params.get("companyName"), (String)params.get("interfaceType"));
        if(interfaceDetailInfoEntity == null) {
            internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_INVALID_CHANNEL.toString());
            return OpenSerivceCode.ERR_INVALID_CHANNEL.toMap();
        }
        else {
            params.put("thirdPartyType", interfaceDetailInfoEntity.getThirdPartyType());
            internalOpenService.saveThirdPartyTypeTradeLog(interfaceDetailInfoEntity.getThirdPartyType());
        }
        
        // 5、验签错误 md5(Key + loanNo +  requestTime +  companyName +  tradeCode + operateType)  
        String hashString = interfaceDetailInfoEntity.getSecretKey() + (String)params.get("loanNo") + (String)params.get("requestTime") 
                + (String)params.get("companyName") + (String)params.get("tradeCode") + (String)params.get("operateType");
        String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
        if (!sign.equalsIgnoreCase((String)params.get("sign"))) {
            internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_SIGN.toString());
            return OpenSerivceCode.ERR_SIGN.toMap();
        }
        
        // 5、验证交易编号是否重复
        TradeLogInfoEntity oldTradeLogInfoEntity = tradeLogInfoRepository.findByTradeCode(tradeCode);
        if(oldTradeLogInfoEntity != null) {
            internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_TRADE_NO_REPEATE.toString());
            return OpenSerivceCode.ERR_TRADE_NO_REPEATE.toMap();
        }
        // 6、操作项目
		try {
			ResultVo resultVo = loanProjectService.operateProject(params);
	        if(ResultVo.isSuccess(resultVo)) { // 操作项目成功
	            Map<String, Object> result = OpenSerivceCode.SUCCESS.toMap();
	            internalOpenService.saveTradeCodeTradeLog(tradeCode);
	            internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
	            return result;
	        }
	        else { // 查询项目失败
	            Map<String, Object> result = OpenSerivceCode.ERR_NOT_EXISTS_PROJECT.toMap();
	            result.put("retMsg", resultVo.getValue("message"));
	            internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
	            return result;
	        }
		} catch (Exception e) {
			Map<String, Object> result = OpenSerivceCode.ERR_OTHER.toMap();
			result.put("retMsg", e.getMessage() == null ? "发生未知异常" : e.getMessage());
			internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
			return result;
		}
    }

	@SuppressWarnings({ "unchecked" })
	@Override
	public Map<String, Object> repayment(Map<String, Object> params)
			throws SLException {
		log.info("收到的报文：" + params.toString());
		params.put("interfaceType", Constant.NOTIFY_TYPE_LOAN_REPAYMENT);
		
		// 1、记录接收到的报文
		String tradeCode = (String)params.get("tradeCode");
		internalOpenService.saveRequestTradeLog(params);
		
		// 2、字段不全或缺少必填字段校验
		if(!RuleUtils.required(params.get("loanNo"))
			|| !RuleUtils.required(params.get("requestTime"))
			|| !RuleUtils.required(params.get("companyName"))
			|| !RuleUtils.required(params.get("tradeCode"))
			|| !RuleUtils.required(params.get("repaymentStatus"))
			|| !RuleUtils.required(params.get("sign"))
			) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		
		// 3、请求时间格式错误
		if(	!RuleUtils.dateFormat(params.get("requestTime"), "yyyyMMddHHmmss")){
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_REQUESTTIME_RULE.toString());
			return OpenSerivceCode.ERR_REQUESTTIME_RULE.toMap();
		}
		
		List<String> repaymentStatusList = Arrays.asList(
				Constant.NOTIFY_TYPE_REPAYMENT_TYPE_01,
				Constant.NOTIFY_TYPE_REPAYMENT_TYPE_02,
				Constant.NOTIFY_TYPE_REPAYMENT_TYPE_03);
		if(!RuleUtils.inRange(params.get("repaymentStatus"), repaymentStatusList.toArray(new String[0]))) {
			Map<String, Object> resultMap = OpenSerivceCode.ERR_LACK_FIELD.toMap();
			resultMap.put("retMsg", "还款状态必须为" + repaymentStatusList.toString() + "之一");
			internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(resultMap));
			return resultMap;
		}
		
		// 检查还款信息是否存在
		List<Map<String, Object>> repaymentList = null;
		if(!params.containsKey("result")) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		else {
			Map<String, Object> result = (Map<String, Object>)params.get("result");
			if(!result.containsKey("repaymentList")) {
				internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
				return OpenSerivceCode.ERR_LACK_FIELD.toMap();
			}
			if(!result.containsKey("totalRepaymentTerms")) {
				internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
				return OpenSerivceCode.ERR_LACK_FIELD.toMap();
			}
			repaymentList = (List<Map<String, Object>>)result.get("repaymentList");
			if(Integer.parseInt(result.get("totalRepaymentTerms").toString()) != repaymentList.size()) {
				Map<String, Object> resultMap = OpenSerivceCode.ERR_NOT_EXISTS_PROJECT.toMap();
				resultMap.put("retMsg", "总还款期数与还款列表不等");
				internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(resultMap));
				return resultMap;
			}
			for(Map<String, Object> repaymentMap : repaymentList) {
				if(!RuleUtils.required(repaymentMap.get("currentTerm"))
						|| !RuleUtils.required(repaymentMap.get("expectRepaymentDate"))
						|| !RuleUtils.required(repaymentMap.get("penaltyInterest"))
						) {
						internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
						return OpenSerivceCode.ERR_LACK_FIELD.toMap();
					}
			}
		}
		
		// 4、验证渠道是否有效
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType((String)params.get("companyName"), (String)params.get("interfaceType"));
		if(interfaceDetailInfoEntity == null) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_INVALID_CHANNEL.toString());
			return OpenSerivceCode.ERR_INVALID_CHANNEL.toMap();
		}
		else {
			params.put("thirdPartyType", interfaceDetailInfoEntity.getThirdPartyType());
			internalOpenService.saveThirdPartyTypeTradeLog(interfaceDetailInfoEntity.getThirdPartyType());
		}
		
		// 5、验签错误 md5(Key+ companyName + loanNo +  requestTime )
		String hashString = interfaceDetailInfoEntity.getSecretKey() + (String)params.get("companyName") + (String)params.get("loanNo") 
				+ (String)params.get("requestTime");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		if (!sign.equalsIgnoreCase((String)params.get("sign"))) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_SIGN.toString());
			return OpenSerivceCode.ERR_SIGN.toMap();
		}
		
		// 5、验证交易编号是否重复
		TradeLogInfoEntity oldTradeLogInfoEntity = tradeLogInfoRepository.findByTradeCode(tradeCode);
		if(oldTradeLogInfoEntity != null) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_TRADE_NO_REPEATE.toString());
			return OpenSerivceCode.ERR_TRADE_NO_REPEATE.toMap();
		}
		
		// 6、查询项目
		if(Constant.NOTIFY_TYPE_REPAYMENT_TYPE_01.equals((String)params.get("repaymentStatus"))) {
			
			Map<String, Object> freeRequestParam = Maps.newConcurrentMap();
			freeRequestParam.put("loanNo", params.get("loanNo"));
			freeRequestParam.put("repaymentList", repaymentList);
			final ResultVo resultVo = loanRepaymentService.creditFreezeRepayment(freeRequestParam);
			
			if(ResultVo.isSuccess(resultVo)) { // 还款冻结成功之后
				// 取结果集中的还款列表
				final Map<String, Object> requestParam = (Map<String, Object>)resultVo.getValue("data");
						
				// 异步执行正常还款
				smsThreadPoolTaskExecutor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							log.info("开始执行还款逻辑");
							loanRepaymentService.normalRepayment(requestParam);
							log.info("完成执行还款逻辑");
						} catch (SLException e) {
							log.error("正常还款失败!" + e.getMessage());
						}
					}
				});
				
				Map<String, Object> result = OpenSerivceCode.SUCCESS.toMap();
				internalOpenService.saveTradeCodeTradeLog(tradeCode);
				internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
				return result;
			}
			else {
				Map<String, Object> result = OpenSerivceCode.ERR_OTHER.toMap();
				result.put("retMsg", resultVo.getValue("message"));
				internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
				return result;
			}	
		}
		else {
			Map<String, Object> result = OpenSerivceCode.ERR_OTHER.toMap();
			result.put("retMsg", "目前只支持正常还款一种还款方式，请联系管理员");
			internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
			return result;
		}
	}
	
	@Autowired
	private InternalOpenService internalOpenService;
	
	@Service
	public static class InternalOpenService {
		
		@Autowired
		private TradeLogInfoRepository tradeLogInfoRepository;
		
		@Autowired
		private FlowNumberService numberService;
		
		@PersistenceContext
		private EntityManager manager;
		
		private final static ThreadLocal<TradeLogInfoEntity> localTradeLog = new ThreadLocal<>();
		
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void saveRequestTradeLog(Map<String, Object> params) {			
			String innerTradeCode = numberService.generateOpenServiceTradeNumber();
			TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
			tradeLogInfoEntity.setThirdPartyType((String)params.get("companyName"));
			tradeLogInfoEntity.setInterfaceType((String)params.get("interfaceType"));
			tradeLogInfoEntity.setRequestTime((String)params.get("requestTime"));
			//tradeLogInfoEntity.setTradeCode((String)params.get("tradeCode"));
			tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
			tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(params));
			tradeLogInfoEntity.setResponseMessage("");
			tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			tradeLogInfoEntity.setMemo("查询");
			tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);
			localTradeLog.set(tradeLogInfoEntity);
		}
		
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void saveResponseTradeLog(String response) {
			TradeLogInfoEntity tradeLogInfoEntity = localTradeLog.get();
			tradeLogInfoEntity = manager.merge(tradeLogInfoEntity);
			tradeLogInfoEntity.setResponseMessage(response);
			tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		}
		
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void saveThirdPartyTypeTradeLog(String thirdPartyType) {
			TradeLogInfoEntity tradeLogInfoEntity = localTradeLog.get();
			tradeLogInfoEntity = manager.merge(tradeLogInfoEntity);
			tradeLogInfoEntity.setThirdPartyType(thirdPartyType);
			tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		}
		
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void saveTradeCodeTradeLog(String tradeCode) {
			TradeLogInfoEntity tradeLogInfoEntity = localTradeLog.get();
			tradeLogInfoEntity = manager.merge(tradeLogInfoEntity);
			tradeLogInfoEntity.setTradeCode(tradeCode);
			tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		}
		
	}

	@Override
	public Map<String, Object> queryRealName(Map<String, Object> params)
			throws SLException {
		
		log.info("收到的报文：" + params.toString());
		params.put("interfaceType", Constant.NOTIFY_TYPE_LOAN_QUERY_REAL_NAME);
		if(StringUtils.isEmpty((String)params.get("companyName"))) {
			params.put("companyName", Constant.NOTIFY_LOAN_COMPANY_01);
		}
		
		// 1、记录接收到的报文
		String tradeCode = (String)params.get("tradeCode");
		internalOpenService.saveRequestTradeLog(params);
		
		// 2、字段不全或缺少必填字段校验
		if(!RuleUtils.required(params.get("requestTime"))
			|| !RuleUtils.required(params.get("tradeCode"))
			|| !RuleUtils.required(params.get("credentialsCode"))
			|| !RuleUtils.required(params.get("custName"))
			|| !RuleUtils.required(params.get("sign"))
			) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		
		// 3、请求时间格式错误
		if(	!RuleUtils.dateFormat(params.get("requestTime"), "yyyyMMddHHmmss")){
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_REQUESTTIME_RULE.toString());
			return OpenSerivceCode.ERR_REQUESTTIME_RULE.toMap();
		}
		
		// 4、验证渠道是否有效
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType((String)params.get("companyName"), (String)params.get("interfaceType"));
		if(interfaceDetailInfoEntity == null) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_INVALID_CHANNEL.toString());
			return OpenSerivceCode.ERR_INVALID_CHANNEL.toMap();
		}
		else {
			params.put("thirdPartyType", interfaceDetailInfoEntity.getThirdPartyType());
			internalOpenService.saveThirdPartyTypeTradeLog(interfaceDetailInfoEntity.getThirdPartyType());
		}
		
		// 5、验签错误 md5( custName +credentialsCode +  requestTime )
		String hashString = interfaceDetailInfoEntity.getSecretKey() + (String)params.get("custName") + (String)params.get("credentialsCode") 
				+ (String)params.get("requestTime");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		if (!sign.equalsIgnoreCase((String)params.get("sign"))) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_SIGN.toString());
			return OpenSerivceCode.ERR_SIGN.toMap();
		}
		
		// 5、验证交易编号是否重复
		TradeLogInfoEntity oldTradeLogInfoEntity = tradeLogInfoRepository.findByTradeCode(tradeCode);
		if(oldTradeLogInfoEntity != null) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_TRADE_NO_REPEATE.toString());
			return OpenSerivceCode.ERR_TRADE_NO_REPEATE.toMap();
		}
		
		// 6、查询项目
		try {
			CustInfoEntity custInfoEntity = custInfoRepository.findByCredentialsCodeAndCustName((String)params.get("credentialsCode"), (String)params.get("custName"));
			if(custInfoEntity != null) { // 查询项目成功
				
				Map<String, Object> requestMap = Maps.newConcurrentMap();
				requestMap.put("custId", custInfoEntity.getId());
				requestMap.put("bankFlag", Constant.BANK_FLAG_ONLINE);
				Map<String, Object> bankMap = bankCardService.queryBankCard(requestMap);
				Map<String, Object> result = OpenSerivceCode.SUCCESS.toMap();
				Map<String, Object> custMap = Maps.newHashMap();
				custMap.put("custCode", custInfoEntity.getCustCode());
				if(bankMap != null && bankMap.size() > 0) {
					custMap.put("bankName", bankMap.get("bankName"));
					custMap.put("bankCardNo", bankMap.get("cardNo"));
					custMap.put("bankCode", bankMap.get("bankCode"));
					custMap.put("openProvince", bankMap.get("openProvince"));
					custMap.put("openCity", bankMap.get("openCity"));
					custMap.put("subBranchName", bankMap.get("subBranchName"));
					custMap.put("responseCardId", bankMap.get("protocolNo"));
				}
				result.put("result", custMap);
				internalOpenService.saveTradeCodeTradeLog(tradeCode);
				internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
				return result;
			}
			else { // 查询项目失败
				Map<String, Object> result = OpenSerivceCode.ERR_NOT_EXISTS_CUST.toMap();
				internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
				return result;
			}
		} catch (Exception e) {
			Map<String, Object> result = OpenSerivceCode.ERR_OTHER.toMap();
			result.put("retMsg", e.getMessage());
			internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
			return result;
		}
	}

	@Override
	public Map<String, Object> bindCard(Map<String, Object> params)
			throws SLException {
		log.info("收到的报文：" + params.toString());
		if("绑卡".equals((String)params.get("tradeType"))) {
			params.put("interfaceType", Constant.NOTIFY_TYPE_LOAN_BIND_CARD);
		}
		else if("解绑".equals((String)params.get("tradeType"))){
			params.put("interfaceType", Constant.NOTIFY_TYPE_LOAN_UNBIND_CARD);
		}
				
		if(StringUtils.isEmpty((String)params.get("companyName"))) {
			params.put("companyName", Constant.NOTIFY_LOAN_COMPANY_01);
		}
		
		// 1、记录接收到的报文
		String tradeCode = (String)params.get("tradeCode");
		internalOpenService.saveRequestTradeLog(params);
		
		// 2、字段不全或缺少必填字段校验
		if(!RuleUtils.required(params.get("tradeType"))
			|| !RuleUtils.required(params.get("requestTime"))
			|| !RuleUtils.required(params.get("tradeCode"))
			|| !RuleUtils.required(params.get("custName"))
			|| !RuleUtils.required(params.get("credentialsCode"))
			|| !RuleUtils.required(params.get("custCode"))
			|| !RuleUtils.required(params.get("bankName"))
			|| !RuleUtils.required(params.get("bankCardNo"))
			//|| !RuleUtils.required(params.get("responseCardId"))
			|| !RuleUtils.required(params.get("sign"))
			) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		
		// 3、请求时间格式错误
		if(	!RuleUtils.dateFormat(params.get("requestTime"), "yyyyMMddHHmmss")){
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_REQUESTTIME_RULE.toString());
			return OpenSerivceCode.ERR_REQUESTTIME_RULE.toMap();
		}
		
		// 4、验证渠道是否有效
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType((String)params.get("companyName"), (String)params.get("interfaceType"));
		if(interfaceDetailInfoEntity == null) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_INVALID_CHANNEL.toString());
			return OpenSerivceCode.ERR_INVALID_CHANNEL.toMap();
		}
		else {
			params.put("thirdPartyType", interfaceDetailInfoEntity.getThirdPartyType());
			internalOpenService.saveThirdPartyTypeTradeLog(interfaceDetailInfoEntity.getThirdPartyType());
		}
		
		// 5、验签错误 md5(Key+ custName +credentialsCode +  custCode + bankCardNo + requestTime  )
		String hashString = interfaceDetailInfoEntity.getSecretKey() + (String)params.get("custName") + (String)params.get("credentialsCode") 
				+ (String)params.get("custCode") + (String)params.get("bankCardNo") + (String)params.get("requestTime");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		if (!sign.equalsIgnoreCase((String)params.get("sign"))) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_SIGN.toString());
			return OpenSerivceCode.ERR_SIGN.toMap();
		}
		
		// 5、验证交易编号是否重复
		TradeLogInfoEntity oldTradeLogInfoEntity = tradeLogInfoRepository.findByTradeCode(tradeCode);
		if(oldTradeLogInfoEntity != null) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_TRADE_NO_REPEATE.toString());
			return OpenSerivceCode.ERR_TRADE_NO_REPEATE.toMap();
		}

		// 6、查询项目
		try {
			ResultVo resultVo = bankCardService.bindBankCard(params);
			if(ResultVo.isSuccess(resultVo)) { // 查询项目成功
				Map<String, Object> result = OpenSerivceCode.SUCCESS.toMap();
				internalOpenService.saveTradeCodeTradeLog(tradeCode);
				internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
				return result;
			}
			else { // 查询项目失败
				Map<String, Object> result = OpenSerivceCode.ERR_OTHER.toMap();
				result.put("retMsg", resultVo.getValue("message"));
				internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
				return result;
			}
		} catch (Exception e) {
			Map<String, Object> result = OpenSerivceCode.ERR_OTHER.toMap();
			result.put("retMsg", e.getMessage());
			internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
			return result;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> queryProtocol(Map<String, Object> params)
			throws SLException {
		log.info("收到的报文：" + params.toString());
		params.put("interfaceType", Constant.NOTIFY_TYPE_LOAN_PROTOCAL);
		
		// 1、记录接收到的报文
		String tradeCode = (String)params.get("tradeCode");
		internalOpenService.saveRequestTradeLog(params);
		
		// 2、字段不全或缺少必填字段校验
		if(!RuleUtils.required(params.get("loanNo"))
				|| !RuleUtils.required(params.get("requestTime"))
				|| !RuleUtils.required(params.get("companyName"))
				|| !RuleUtils.required(params.get("tradeCode"))
				|| !RuleUtils.required(params.get("sign"))
			) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		
		// 3、请求时间格式错误
		if(	!RuleUtils.dateFormat(params.get("requestTime"), "yyyyMMddHHmmss")){
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_REQUESTTIME_RULE.toString());
			return OpenSerivceCode.ERR_REQUESTTIME_RULE.toMap();
		}
		
		// 4、验证渠道是否有效
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType((String)params.get("companyName"), (String)params.get("interfaceType"));
		if(interfaceDetailInfoEntity == null) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_INVALID_CHANNEL.toString());
			return OpenSerivceCode.ERR_INVALID_CHANNEL.toMap();
		}
		else {
			params.put("thirdPartyType", interfaceDetailInfoEntity.getThirdPartyType());
			internalOpenService.saveThirdPartyTypeTradeLog(interfaceDetailInfoEntity.getThirdPartyType());
		}
		
		// 5、验签错误 md5(Key+ custName +credentialsCode +  custCode + bankCardNo + requestTime  )
		String hashString = interfaceDetailInfoEntity.getSecretKey() + (String)params.get("loanNo") + (String)params.get("companyName")
				 + (String)params.get("tradeCode") + (String)params.get("requestTime");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		if (!sign.equalsIgnoreCase((String)params.get("sign"))) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_SIGN.toString());
			return OpenSerivceCode.ERR_SIGN.toMap();
		}
		
		// 5、验证交易编号是否重复
		TradeLogInfoEntity oldTradeLogInfoEntity = tradeLogInfoRepository.findByTradeCode(tradeCode);
		if(oldTradeLogInfoEntity != null) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_TRADE_NO_REPEATE.toString());
			return OpenSerivceCode.ERR_TRADE_NO_REPEATE.toMap();
		}
		
		// 6、查询项目
		try {
			ResultVo resultVo = loanManagerService.findByLoanCode((String)params.get("loanNo"));
			if(ResultVo.isSuccess(resultVo)) { // 查询项目成功
				LoanInfoEntity loanInfoEntity = (LoanInfoEntity)resultVo.getValue("data");
				params.put("loanId", loanInfoEntity.getId());
				params.put("custId", "1");
				resultVo = exportFileService.downloadLoanContract(params);
				if(ResultVo.isSuccess(resultVo)) { // 查询项目成功
					Map<String, Object> result = OpenSerivceCode.SUCCESS.toMap();
					String pdfUrl = (String)((Map<String, Object>)resultVo.getValue("data")).get("url");
					// 移除/upload，使商务能通过域名加地址直接访问
					// pdf地址:http://image.shanlincaifu.com + pdfUrl
					// h5地址:http://m.shanlincaifu.com + h5Url
					int pos = pdfUrl.indexOf("upload");
					if(pos != -1) {
						pdfUrl = pdfUrl.substring(pos + 6);
					}
					result.put("pdfUrl", pdfUrl);
					result.put("h5Url", String.format("/agreement/disperseAgreement/%s/%s", (String)params.get("custId"), (String)params.get("loanId")));
					internalOpenService.saveTradeCodeTradeLog(tradeCode);
					internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
					return result;
				}
				else {
					Map<String, Object> result = OpenSerivceCode.ERR_OTHER.toMap();
					result.put("retMsg", resultVo.getValue("message"));
					internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
					return result;
				}
			}
			else { // 查询项目失败
				Map<String, Object> result = OpenSerivceCode.ERR_NOT_EXISTS_PROJECT.toMap();
				internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
				return result;
			}
		} catch (Exception e) {
			Map<String, Object> result = OpenSerivceCode.ERR_OTHER.toMap();
			result.put("retMsg", e.getMessage());
			internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
			return result;
		}
		
	}

	@Override
	public Map<String, Object> queryWdzj(Map<String, Object> params)
			throws SLException {
		return wdzjService.queryWdzj(params);
	} 

	@Override
	public CustInfoEntity loginWdzj(String username, String password) {
		password = DigestUtils.md5DigestAsHex(password.getBytes(Charsets.UTF_8));
		return custInfoRepository.findByLoginNameAndLoginPassword(username, password);
	}
	
	@Override
	public Map<String,Object> getWXSignature(Map<String, Object> params)
		throws SLException {
		Map<String, Object> result = new HashMap<String, Object>();		
		String jsapiTicket = redisTemplate.opsForValue().get("jsapi_ticket");
		String isForceRefresh = (String)params.get("isForceRefresh");
		String url = (String)params.get("url");
		//如果请求方要求强制刷新或者缓存中没有ticket时需要去微信端重新请求
		if("yes".equals(isForceRefresh) || StringUtils.isEmpty(jsapiTicket)){						
			if((appID == null) || (appSecret == null)){
				result.put("error", "appID or appSecret is empty");
				return result;
			}
			//先请求token
			Map<String, String> requestParams = new HashMap<String, String>();
			requestParams.put("appID", appID);
			requestParams.put("appSecret", appSecret);
			String requestResult = new RestTemplate().getForObject("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appID}&secret={appSecret}", String.class, requestParams);		
	        JSONObject object = JSON.parseObject(requestResult); 
	        String requestToken = object.get("access_token").toString();
	        if(!StringUtils.isEmpty(requestToken)){
	        	redisTemplate.opsForValue().set("access_token", requestToken, Long.parseLong(object.get("expires_in").toString())-100, TimeUnit.SECONDS);
	        	log.info("access_token重新请求成功，access_token:"+requestToken);
	        	//再用请求到的token去请求ticket
	        	Map<String, String> requestParams2 = new HashMap<String, String>();
	        	requestParams2.put("requestToken", requestToken);
	    		String requestResult2 = new RestTemplate().getForObject("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token={requestToken}&type=jsapi", String.class, requestParams2);		
	            JSONObject object2 = JSON.parseObject(requestResult2); 
	            String requestTicket = object2.get("ticket").toString();
	        	if(!StringUtils.isEmpty(requestTicket)){
	        		redisTemplate.opsForValue().set("jsapi_ticket", requestTicket, Long.parseLong(object2.get("expires_in").toString())-100, TimeUnit.SECONDS);
	        		log.info("jsapi_ticket重新请求成功，jsapi_ticket:"+requestTicket);
	        		result = sign(requestTicket,url);
	        		result.put("appID", appID);
	        	}else{
		        	log.error("jsapi_ticket重新请求失败。"+object2.get("errmsg").toString());
		        	result.put("error", object2.get("errmsg").toString());  
	        	}	        		        	      	
	        }else{
	        	log.error("access_token重新请求失败。"+object.get("errmsg").toString());
	        	result.put("error", object.get("errmsg").toString());        	
	        }					
		}else{
    		result = sign(jsapiTicket,url);
    		result.put("appID", appID);			
		}
		return result;
	}
	
	private static Map<String, Object> sign(String jsapi_ticket, String url) {
        Map<String, Object> result = new HashMap<String, Object>();
        String nonce_str = UUID.randomUUID().toString();
        String timestamp = Long.toString(System.currentTimeMillis()/1000);	        
        String signature = "";	 
        //注意这里参数名必须全部小写，且必须有序
        String str = "jsapi_ticket=" + jsapi_ticket +
                  "&noncestr=" + nonce_str +
                  "&timestamp=" + timestamp +
                  "&url=" + url;	 
        try{
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(str.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
            
            result.put("url", url);
            result.put("jsapi_ticket", jsapi_ticket);
            result.put("nonceStr", nonce_str);
            result.put("timestamp", timestamp);
            result.put("signature", signature);
        }
        catch (NoSuchAlgorithmException e){
            log.error(e.getMessage());
            result.put("error",e.getMessage());
        }
        catch (UnsupportedEncodingException e){
            log.error(e.getMessage());
            result.put("error",e.getMessage());
        }
        return result;
	}
	
    private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash){
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
    }



	@Override
	public Map<String, Object> timeLimitWithHold(Map<String, Object> params) throws SLException {
		log.info("收到的报文：" + params.toString());
		params.put("interfaceType", Constant.REPAYMENT_CHANGE);

		// 1、记录接收到的报文
		String tradeCode = (String)params.get("tradeCode");
		internalOpenService.saveRequestTradeLog(params);
		// 2、字段不全或缺少必填字段校验
		if(!RuleUtils.required(params.get("loanNo"))
				|| !RuleUtils.required(params.get("companyName"))
				|| !RuleUtils.required(params.get("requestTime"))
				|| !RuleUtils.required(params.get("tradeCode"))
				|| !RuleUtils.required(params.get("repaymentList"))
				|| !RuleUtils.required(params.get("sign"))
				) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
			return OpenSerivceCode.ERR_LACK_FIELD.toMap();
		}
		// 3、请求时间格式错误
		if(	!RuleUtils.dateFormat(params.get("requestTime"), "yyyyMMddHHmmss")){
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_REQUESTTIME_RULE.toString());
			return OpenSerivceCode.ERR_REQUESTTIME_RULE.toMap();
		}
		// 4、验证渠道是否有效
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType((String)params.get("companyName"), (String)params.get("interfaceType"));
		if(interfaceDetailInfoEntity == null) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_INVALID_CHANNEL.getMessage());
			return OpenSerivceCode.ERR_INVALID_CHANNEL.toMap();
		}
		else {
			params.put("thirdPartyType", interfaceDetailInfoEntity.getThirdPartyType());
			internalOpenService.saveThirdPartyTypeTradeLog(interfaceDetailInfoEntity.getThirdPartyType());
		}
		List<Map<String,Object>> repaymentList = (List<Map<String, Object>>)params.get("repaymentList");
		for(Map<String, Object> repaymentMap : repaymentList) {
			if(!RuleUtils.required(repaymentMap.get("currentTerm"))
					|| !RuleUtils.required(repaymentMap.get("expectRepaymentDate"))
					|| !RuleUtils.required(repaymentMap.get("repaymentTotalAmount"))
					|| !RuleUtils.required(repaymentMap.get("repaymentPrincipal"))
					|| !RuleUtils.required(repaymentMap.get("repaymentInterest"))
					|| !RuleUtils.required(repaymentMap.get("advanceCleanupTotalAmount"))
					) {
				internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_LACK_FIELD.toString());
				return OpenSerivceCode.ERR_LACK_FIELD.toMap();
			}
		}
		// 5、验签错误 md5(loanNO+ companyName + requestTime +  tradeCode + repaymentList)
		String hashString = interfaceDetailInfoEntity.getSecretKey() + (String)params.get("loanNo")+ (String)params.get("requestTime")+(String)params.get("companyName")+ (String)params.get("tradeCode")+ params.get("repaymentList").toString();
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		if (!sign.equalsIgnoreCase((String)params.get("sign"))) {
			internalOpenService.saveResponseTradeLog(OpenSerivceCode.ERR_SIGN.toString());
			return OpenSerivceCode.ERR_SIGN.toMap();
		}
		try {

			Map<String,Object> changeMap = Maps.newConcurrentMap();
			changeMap.put("loanNo",params.get("loanNo"));
			changeMap.put("companyName",params.get("companyName"));
			ResultVo result = withHoldingService.timeLimitWithHold(changeMap,repaymentList);
			Map<String,Object> resMap = Maps.newConcurrentMap();
			if (ResultVo.isSuccess(result)){
				resMap = OpenSerivceCode.SUCCESS.toMap();
				internalOpenService.saveTradeCodeTradeLog(tradeCode);
				internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(resMap));
				return resMap;
			}else{
				resMap = OpenSerivceCode.ERR_OTHER.toMap();
				resMap.put("retMsg", result.getValue("message"));
				internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
				return resMap;
			}
		}catch (Exception e){
			Map<String, Object> result = OpenSerivceCode.ERR_OTHER.toMap();
			result.put("retMsg", e.getMessage());
			internalOpenService.saveResponseTradeLog(Json.ObjectMapper.writeValue(result));
			return result;
		}
	}
}