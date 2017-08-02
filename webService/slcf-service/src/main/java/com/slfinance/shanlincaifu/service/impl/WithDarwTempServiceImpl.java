/** 
 * @(#)WithDarwTempServiceImpl.java 1.0.0 2015年5月28日 上午9:57:35  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.entity.BankCardInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRepository;
import com.slfinance.shanlincaifu.repository.BankCardInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.TradeFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.UserRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.BankCardInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.WithdrawCashRepositoryCustom;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.service.ThirdPartyPayService;
import com.slfinance.shanlincaifu.service.WithDarwTempService;
import com.slfinance.shanlincaifu.service.WithDrawCheckService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.HttpRequestUtil;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

/**   
 * 提现模拟回调成功或失败模拟业务接口 实现，仅提供测试用
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月28日 上午9:57:35 $ 
 */
@Service
@Slf4j
public class WithDarwTempServiceImpl implements WithDarwTempService {

	@Autowired
	private WithDrawCheckService withDrawCheckService;
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private WithdrawCashRepositoryCustom withdrawCashRepository;
	
	@Autowired
	private AuditInfoRepository auditInfoRepository;
	
	@Autowired
	private LogInfoEntityRepository logInfoRepository;
	
	@Autowired
	private TradeFlowInfoRepository tradeFlowInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
//	@Autowired
//	private AccountFlowDetailRepository accountFlowDetailRepository;
	
	@Autowired
	private AccountFlowInfoRepository accountFlowInfoRepository;

	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FlowNumberService numberService;	
	
	@Autowired
	private SMSService smsService;
	
	@Autowired
	private ThirdPartyPayService thirdPartyPayService;
	
	@Autowired
	private BankCardInfoRepository bankCardInfoRepository;
	
	@Autowired
	private BankCardInfoRepositoryCustom bankCardInfoRepositoryCustom;
	
	@Autowired
	private CustAccountService custAccountService;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private AccountFlowService accountFlowService;
	
	/**
	 * 提现管理--提现审核提交
	 * 
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveWithdrawCashAudit(Map<String, Object> paramsMap) throws SLException {
		/**更新审核信息 、新增审核日志信息**/
		AuditInfoEntity auditInfoEntity = auditInfoRepository.findOne((String)paramsMap.get("id"));
		if( null == auditInfoEntity )
			throw new SLException("非法的提现审核操作,没有找到该提现的申请信息!");
		//检查审核状态
		if(Constant.AUDIT_STATUS_REfUSE.equals(auditInfoEntity.getAuditStatus()) || Constant.AUDIT_STATUS_PASS.equals(auditInfoEntity.getAuditStatus()))
			return new ResultVo(false, "提现已经处理完成,请勿 重复提交!");
		TradeFlowInfoEntity flowInfo = tradeFlowInfoRepository.findFirstByRelatePrimaryOrderByCreateDateDesc((String)paramsMap.get("id"));
		if (null == flowInfo)
			throw new SLException("非法的提现审核操作,没有找到该比提现的流水过程信息!");
		AccountInfoEntity accountInfo =  accountInfoRepository.findFirstByCustIdOrderByCreateDateDesc(auditInfoEntity.getCustId());
		if( null == accountInfo )
			throw new SLException("非法的提现审核操作,没有账户信息!");
		// 取原冻结流水
		AccountFlowInfoEntity accountFlowFreeze = accountFlowService.findByTradeNoIsRead(flowInfo.getTradeNo());
		if( null == accountFlowFreeze )
			throw new SLException("非法的提现审核操作,没有冻结流水信息!");
		//用户账户信息
		CustInfoEntity cust = custInfoRepository.findOne(auditInfoEntity.getCustId());
		
		//查询账户默认银行卡
		BankCardInfoEntity bankInfo = bankCardInfoRepository.findFirstByCustInfoEntityAndCardNoAndIsDefaultAndRecordStatus(cust,flowInfo.getBankCardNo(),Constant.BANKCARD_DEFAULT,Constant.VALID_STATUS_VALID);
		if(null == bankInfo)
			throw new SLException("用户银行卡信息不存在");
		
		//提现手续费
		BigDecimal withDrawExpense = paramService.findWithDrawExpenseAmount();
		ResultVo resultVo = null;
		String message = "提现审核成功";
		switch ((String)paramsMap.get("auditStatus")) {
			case Constant.AUDIT_STATUS_PASS:
				// 调用结算平台接口,执行提现操作
				Map<String, Object> thirdPartyPayParams = Maps.newHashMap();
				//交易金额变为提现申请金额减去提现手续费
				thirdPartyPayParams.put("tradeAmount", ArithUtil.formatScale2(ArithUtil.sub(auditInfoEntity.getTradeAmount(), withDrawExpense)));
				thirdPartyPayParams.put("bankCardNo", bankInfo.getCardNo());
				thirdPartyPayParams.put("accountName", cust.getCustName());
				thirdPartyPayParams.put("tradeDesc", "提现");
				thirdPartyPayParams.put("openAccountProv", bankInfo.getOpenProvince());
				thirdPartyPayParams.put("openAccountCity", bankInfo.getOpenCity());
				thirdPartyPayParams.put("subBranchName", bankInfo.getSubBranchName());
				thirdPartyPayParams.put("bankCode", bankCardInfoRepositoryCustom.findByBankName(bankInfo.getBankName()));
				thirdPartyPayParams.put("tradeCode", flowInfo.getTradeNo());
				thirdPartyPayParams.put("accountProperties", accountInfo.getAccountType());
				try {
					//调用第三方信息 暂时不调用第三方提现操作
					thirdPartyPayService.trustWithdrawCash(thirdPartyPayParams);
					resultVo = new ResultVo(true);
				} catch (Exception e) {
					withDrawCheckService.updateWithdrawAndFlowProcess(auditInfoEntity.getId(), flowInfo.getId(), (String)paramsMap.get("custId"));
					log.error("提现审核调用TPP出现异常:" + e.toString());
					throw new SLException("提现审核调用TPP出现异常!");
				}
				if (!ResultVo.isSuccess(resultVo)) {
					// 提现解冻 记录账户流水详情更新账户表
					custAccountService.updateCustAccount("",flowInfo, accountFlowFreeze, accountInfo, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW);
					flowInfo.setTradeStatus(Constant.TRADE_STATUS_04);
					auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_04);
					message = resultVo.getValue("message") != null ? (String)resultVo.getValue("message"):"调用TPP业务没有成功"; 
				} else {
					flowInfo.setTradeStatus(Constant.TRADE_STATUS_02);
					auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_02);
				}
				auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_PASS);
				break;
			case Constant.AUDIT_STATUS_REfUSE:
				try {
					//生成提现解冻流水和流水详情，更新账户可用余额和冻结金额
					custAccountService.updateCustAccount("提现审核拒绝,解冻流水记录",flowInfo, accountFlowFreeze, accountInfo, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW);
					//提现审核拒绝
					sendSms((String)paramsMap.get("custId"),null != cust ? cust.getMobile():"", Constant.SMS_TYPE_WITHDRAW_FAIL, accountFlowFreeze.getTradeAmount().toString(),auditInfoEntity.getCreateDate());
					
				} catch (Exception e) {
					log.error("更新客户账户信息,并且记录账户流水信息失败", e);
					throw new SLException("更新账户出现异常!");
				}
				flowInfo.setTradeStatus(Constant.TRADE_STATUS_04);
				auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_04);
				auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_REfUSE);
				break;
			default:
				break;
		}
		//组装更新的交易流水信息
		TradeFlowInfoEntity flowInfoUpd = new TradeFlowInfoEntity();
		flowInfoUpd.setTradeStatus(flowInfo.getTradeStatus());
		if(Constant.AUDIT_STATUS_REfUSE.equals((String)paramsMap.get("auditStatus")))
			flowInfoUpd.setMemo((String)paramsMap.get("memo"));
		flowInfoUpd.setBasicModelProperty((String)paramsMap.get("custId"), false);
		
		//组装审核信息
		AuditInfoEntity auditInfo = new AuditInfoEntity((String)paramsMap.get("auditStatus"), auditInfoEntity.getTradeStatus(), new Date(), (String)paramsMap.get("custId"), (String)paramsMap.get("memo"));
		auditInfo.setBasicModelProperty((String)paramsMap.get("custId"), false);
		
		//组装日志信息
		String  userName = userRepository.findUserNameById((String)paramsMap.get("custId"));
		StringBuilder operDesc = new StringBuilder("用户：").append(null != userName ? userName:(String)paramsMap.get("custId")).append("，做").append(Constant.OPERATION_TYPE_10).append("操作");
		LogInfoEntity logInfo = new LogInfoEntity(Constant.TABLE_BAO_T_AUDIT_INFO,(String)paramsMap.get("id"), Constant.OPERATION_TYPE_10, auditInfoEntity.getAuditStatus(), (String)paramsMap.get("auditStatus"), operDesc.toString(),auditInfoEntity.getCustId());
		//createUser作为被操作人的id存储，方便会员管理的日志查询  朱敏让修改的
		logInfo.setOperIpaddress((String)paramsMap.get("ipAddress"));
		logInfo.setBasicModelProperty((String)paramsMap.get("custId"), true);

		//更新交易流水信息 拒绝时更新
		if(Constant.AUDIT_STATUS_REfUSE.equals((String)paramsMap.get("auditStatus")) && !flowInfo.updateTradeFlowInfo(flowInfoUpd))
			throw new SLException("更新交易流水信息失败");
		
		//更新审核信息 拒绝时更新
		if(Constant.AUDIT_STATUS_REfUSE.equals((String)paramsMap.get("auditStatus")) && !auditInfoEntity.updateAuditInfo(auditInfo))
			throw new SLException("更新审核信息");
		
		//新增审核日志信息
		logInfoRepository.save(logInfo);
		
		//模拟成功回调
		if(Constant.AUDIT_STATUS_PASS.equals((String)paramsMap.get("auditStatus")) && ResultVo.isSuccess(resultVo)){
			Map<String, Object> paramsMapSuccess = Maps.newHashMap();
			paramsMapSuccess.put("status", Constant.TRADE_STATUS_03);
			paramsMapSuccess.put("auditStatus", Constant.AUDIT_STATUS_PASS);
			paramsMapSuccess.put("tradeStatus", Constant.TRADE_STATUS_03);
			//1：根据交易编号查询账户流水信息，并检查该交易是否已经结束。
			if(Constant.TRADE_STATUS_03.equals(flowInfo.getTradeStatus()) || Constant.TRADE_STATUS_04.equals(flowInfo.getTradeStatus()))
				return new ResultVo(false,"已经处理");
			//2：未结束的情况下更新交易流水交易状态
			TradeFlowInfoEntity flowUpd = new TradeFlowInfoEntity();
			flowUpd.setTradeStatus((String)paramsMapSuccess.get("status"));
			flowUpd.setBasicModelProperty(flowInfo.getLastUpdateUser(), false);
			if (!flowInfo.updateTradeFlowInfo(flowUpd))
				throw new SLException("更新流水过程详情失败!");
			
			//生成提现解冻流水和流水详情，更新账户可用余额和冻结金额
			custAccountService.updateCustAccount("",flowInfo, accountFlowFreeze, accountInfo, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW);
			//生成提现手续费流水和流水详情，更新账户可用余额和总金额
			custAccountService.updateCustAccount("",flowInfo, accountFlowFreeze, accountInfo, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW_EXPENSE);
			//生成提现流水和流水详情，更新账户可用余额和总金额
			custAccountService.updateCustAccount("",flowInfo, accountFlowFreeze, accountInfo, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW);
			
			
	       //修改审核表状态审核通过、交易成功、交易成功
		   AuditInfoEntity auditInfoUpd  = new AuditInfoEntity((String)paramsMapSuccess.get("auditStatus"), Constant.TRADE_STATUS_03, new Date(), (String)paramsMap.get("custId"), (String)paramsMap.get("memo"));
		   auditInfoUpd.setBasicModelProperty(auditInfo.getLastUpdateUser(),false);
		   if(!auditInfoEntity.updateAuditInfo(auditInfoUpd))
			   throw new SLException("更新提现信息失败");
			//提现审核通过发送提示短信
			//用户账户信息
			sendSms((String)paramsMap.get("custId"),null != cust ? cust.getMobile():"", Constant.SMS_TYPE_WITHDRAW_SUCCESS, accountFlowFreeze.getTradeAmount().toString(),auditInfo.getCreateDate());
		}
		
		//模拟失败回调 
		if(Constant.AUDIT_STATUS_PASS.equals((String)paramsMap.get("auditStatus")) && !ResultVo.isSuccess(resultVo)){
			Map<String, Object> paramsMapFail = Maps.newHashMap();
			paramsMapFail.put("status", Constant.TRADE_STATUS_04);
			paramsMapFail.put("auditStatus", Constant.AUDIT_STATUS_PASS);
			paramsMapFail.put("tradeStatus", Constant.TRADE_STATUS_04);
			//1：根据交易编号查询账户流水信息，并检查该交易是否已经结束。
			if(Constant.TRADE_STATUS_03.equals(flowInfo.getTradeStatus()) || Constant.TRADE_STATUS_04.equals(flowInfo.getTradeStatus()))
				return new ResultVo(false,"已经处理");
			//2：未结束的情况下更新交易流水交易状态
			TradeFlowInfoEntity flowUpd = new TradeFlowInfoEntity();
			flowUpd.setTradeStatus((String)paramsMapFail.get("status"));
			flowUpd.setBasicModelProperty(flowInfo.getLastUpdateUser(), false);
			if (!flowInfo.updateTradeFlowInfo(flowInfo))
				throw new SLException("更新流水过程详情失败!");
			
			//3：根据账户流水信息查询的管理主键，查询账户的提现申请的审核信息。
			// 4：修改审核表状态审核通过、交易状态失败交易失败
			AuditInfoEntity auditInfoUpd = new AuditInfoEntity((String) paramsMapFail.get("auditStatus"), (String) paramsMapFail.get("tradeStatus"), new Date(), (String) paramsMap.get("custId"), (String) paramsMap.get("memo"));
			auditInfoUpd.setBasicModelProperty(auditInfo.getLastUpdateUser(), false);
			if (!auditInfo.updateAuditInfo(auditInfoUpd))
				throw new SLException("更新提现信息失败");
			// 提现解冻 记录账户流水详情更新账户表
			custAccountService.updateCustAccount("提现审核TPP失败回调,解冻流水记录",flowInfo, accountFlowFreeze, accountInfo, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW);
			//提现审核拒绝发送提示短信
			sendSms((String)paramsMap.get("custId"),null != cust ? cust.getMobile():"", Constant.SMS_TYPE_WITHDRAW_FAIL, accountFlowFreeze.getTradeAmount().toString(),auditInfo.getCreateDate());
		}
		
		return new ResultVo(true,message);
	}	
	
	/**
	 * 异步发送邮件
	 * @param mobile
	 * @param messageType
	 * @param tradeMount
	 */
	private void sendSms(String custId,String mobile,String messageType,String tradeMount,Date sendDate) {
		final Map<String,Object> mailParams = Maps.newHashMap();
		mailParams.put("custId", custId);
		mailParams.put("mobile", mobile);
		mailParams.put("messageType", messageType);
		mailParams.put("values", new String[]{DateUtils.formatDate(sendDate, "yyyy-MM-dd HH:mm:ss"),tradeMount});
		//做异步发送邮件处理,短信服务已经做了异步处理
		smsService.sendSMS(mailParams);
	}
	
}
