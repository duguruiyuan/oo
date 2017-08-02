/** 
 * @(#)CustSafeInfoServiceImpl.java 1.0.0 2015年4月22日 下午3:57:37  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.SmsInfoEntity;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.SmsInfoRepository;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.service.UserEmailService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.spring.mail.MailType;
import com.slfinance.util.CommUtil;
import com.slfinance.vo.ResultVo;

/**   
 * 客户安全中心业务接口实现
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月22日 下午3:57:37 $ 
 */
@Service("userEmailService")
@Transactional(readOnly = true)
public class UserEmailServiceImpl implements UserEmailService {

	@Value("${emailBackUrl.webService}")
	private String emailBackUrl;
	
	@Value("${email.effectiveHours}")
	private String effectiveHours;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private SmsInfoRepository smsInfoRepository;
	
	@Autowired
	private SMSService smsService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	
	/**
	 * 检查该邮箱是否已经存在
	 * @param paramsMap
	 * <tt>email:String:邮箱地址</tt>
	 * @return boolean
	 */
	@Override
	public boolean checkEmailIsExist(Map<String, Object> paramsMap)throws SLException {
		return custInfoRepository.countByEmail((String)paramsMap.get("email")) > 0 ? true:false;
	}
	
	/** 
	 * 登陆用户发送绑定邮箱业务
	 * 
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	public ResultVo sendMailForBindEmail(Map<String, Object> paramsMap) throws SLException {
		/**校验邮箱是否唯一、新增短信消息记录和发送邮件**/
		
		//校验邮箱
		paramsMap.put("email", paramsMap.get("targetAddress"));
		if(checkEmailIsExist(paramsMap))
			throw new SLException("该邮箱已绑定");
		CustInfoEntity custInfo = custInfoRepository.findOne((String)paramsMap.get("custId"));
		if (custInfo == null)
			throw new SLException("该客户不存在");
		
	
		paramsMap.put("effectiveHours", effectiveHours);
		SmsInfoEntity smsInfoEntity = new SmsInfoEntity((String)paramsMap.get("targetAddress"), Constant.TARGET_TYPE_MAIL, "绑定邮箱", new Date(), Constant.SMS_SEND_STATUS_SENT,CommUtil.getUniqueString(), DateUtils.addHours(new Date(), Integer.parseInt((String)paramsMap.get("effectiveHours"))), Constant.VALID_STATUS_VALID, Constant.SMS_TYPE_BINDING_EMAIL, Constant.VALID_STATUS_VALID);
		smsInfoEntity.setBasicModelProperty((String)paramsMap.get("custId"), true);
		
		//发送邮件
		paramsMap.put("title", "善林财富-绑定邮箱");
		ResultVo result = emailService.sendEmail(assembleSmsInfo(paramsMap, smsInfoEntity));
		if(!ResultVo.isSuccess(result))
			throw new SLException(null != result?(String)result.getValue("message"):"发送邮件服务失败,请检查路径或者邮件模板文件");
		
		//新增短信消息
		if(ResultVo.isSuccess(result) && StringUtils.isEmpty(smsInfoRepository.save(smsInfoEntity).getId()))
			throw new SLException("发送邮件失败");

		return new ResultVo(true);
	}
	
	/**
	 * 邮箱回调地址校验邮箱验证码和验证地址
	 */
	public ResultVo checkVerityCodeAndTargetAddress(Map<String, Object> paramsMap) throws SLException {
		paramsMap.put("messageType", Constant.SMS_TYPE_BINDING_EMAIL);
		return smsService.checkVerityCode(paramsMap);
	}


	/**
	 * 验证用户成功，更新验证码信息和用户信息 	 
	 */
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	public ResultVo updateCustEmail(Map<String, Object> paramsMap)throws SLException {
		/**校验验证码，验证成功：更新验证码信息无效,更新用户信息的邮件地址**/
		
		//检验验证码有效
		if ( !ResultVo.isSuccess(checkVerityCodeAndTargetAddress(paramsMap))) {
			throw new SLException("验证码失效");
		}
		
		//根据目标地址、消息类型、验证码修改验证码失效
		if( 0 > smsInfoRepository.updMessageStatusByTypeAndEmail(Constant.VALID_STATUS_INVALID, (String)paramsMap.get("messageType"), (String)paramsMap.get("targetAddress"),(String)paramsMap.get("verityCode")))
			throw new SLException("更新验证码状态失败");
		
		CustInfoEntity custInfo = custInfoRepository.findOne((String)paramsMap.get("custId"));
		if(null == custInfo)
			throw new SLException("该用户不存在");
		CustInfoEntity updateInfo = new CustInfoEntity();
		updateInfo.setEmail((String)paramsMap.get("targetAddress"));
		updateInfo.setLastUpdateDate(new Date());
		updateInfo.setLastUpdateUser((String)paramsMap.get("custId"));
		if(!custInfo.update(updateInfo))
			throw new SLException("更新用户信息失败");
		
		// 新增记录日志 add by wangjf 2015-07-30
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
		logInfoEntity.setRelatePrimary(custInfo.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_19);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperIpaddress((String)paramsMap.get("ipAddress"));
		logInfoEntity.setOperPerson(custInfo.getId());
		logInfoEntity.setMemo(String.format("%s绑定了邮箱",
				custInfo.getLoginName()));
		logInfoEntity.setBasicModelProperty(custInfo.getId(), true);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true);
	}
	
	/**
	 *  修改绑定邮箱--发送修改邮箱邮件
	 */
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	public ResultVo sendMailForUpdateEmail(Map<String, Object> paramsMap) throws SLException {
		/**校验邮箱信息、保存邮件发送信息、发送邮件**/
		if ( paramsMap.get("targetAddress").equals((String)paramsMap.get("targetAddressOld")) )
			throw new SLException("邮箱与原邮箱一致无需修改!");
		
		CustInfoEntity custInfo = custInfoRepository.findOne( (String)paramsMap.get("custId") );
		if ( null == custInfo )
			throw new SLException("用户不存在!");
		if ( StringUtils.isEmpty(custInfo.getEmail()) )
			throw new SLException("邮箱不存在!");
		if ( !custInfo.getEmail().equals((String)paramsMap.get("targetAddressOld")) )
			throw new SLException("原邮箱填写有误!");
		
		//新增短信消息
		paramsMap.put("effectiveHours", effectiveHours);
		SmsInfoEntity smsInfoEntity = new SmsInfoEntity((String)paramsMap.get("targetAddress"), Constant.TARGET_TYPE_MAIL, "绑定邮箱", new Date(), Constant.SMS_SEND_STATUS_SENT,CommUtil.getUniqueString(), DateUtils.addHours(new Date(), Integer.parseInt((String)paramsMap.get("effectiveHours"))), Constant.VALID_STATUS_VALID, Constant.SMS_TYPE_BINDING_EMAIL, Constant.VALID_STATUS_VALID);
		smsInfoEntity.setBasicModelProperty((String)paramsMap.get("custId"), true);
		if(StringUtils.isEmpty(smsInfoRepository.save(smsInfoEntity).getId()))
			throw new SLException("发送邮件失败");

		//发送邮件
		paramsMap.put("title", "善林财富-修改绑定邮箱");
		ResultVo result = emailService.sendEmail(assembleSmsInfo(paramsMap, smsInfoEntity));
		if(!ResultVo.isSuccess(result))
			throw new SLException(null != result?(String)result.getValue("message"):"发送邮件服务失败,请检查路径或者邮件模板文件");
		return new ResultVo(true);
	}
	
//--------------私有方法区---------------------------------------------------------------------------------------------------------------------------		

	/**
	 * 组装邮件发送数据
	 * @param paramsMap
	 * @param smsInfoEntity
	 * @return
	 */
	private Map<String,Object> assembleSmsInfo(Map<String,Object> paramsMap, SmsInfoEntity smsInfoEntity) {
		
		Map<String,Object> smsInfo = Maps.newHashMap();
		//组装邮件基本数据
		smsInfo.put("to", smsInfoEntity.getTargetAddress());
		smsInfo.put("type", MailType.TEXT);
		smsInfo.put("title", paramsMap.get("title"));
		smsInfo.put("ftlPath", Constant.BIND_MAIL_TEMPLATE);
		smsInfo.put("fromNickName",Constant.FORM_PLAT_NAME);
		smsInfo.put("fromAddress",Constant.PLAT_EMAIL_ADDRESS);
		
		//组装数据模板数据
		Map<String,Object> dataSet = Maps.newHashMap(); 
		dataSet.put("url", emailBackUrl);
		dataSet.put("custId", paramsMap.get("custId"));
		dataSet.put("verityCode", smsInfoEntity.getVerityCode());
		dataSet.put("targetAddress", smsInfoEntity.getTargetAddress());
		dataSet.put("invalidHours", paramsMap.get("effectiveHours"));
		dataSet.put("invalidDate", DateFormatUtils.format(smsInfoEntity.getLastValidTime(), "yyyy-MM-dd HH:mm:ss"));
		dataSet.put("customerTle", Constant.PLATFORM_SERVICE_TEL);
		dataSet.put("customerEmail", Constant.CUSTOMER_EMAIL);
		smsInfo.put("dataSet", dataSet);
		
		return smsInfo;
	}

}
