/** 
 * @(#)WithdrawCashServiceImpl.java 1.0.0 2015年4月27日 上午11:40:39  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
import com.slfinance.shanlincaifu.repository.custom.BankCardInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.WithdrawCashRepositoryCustom;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.service.ThirdPartyPayService;
import com.slfinance.shanlincaifu.service.WithDrawAuditService;
import com.slfinance.shanlincaifu.service.WithDrawCheckService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.HttpRequestUtil;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

/**   
 * 提现审核模块业务Service实现
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月27日 上午11:40:39 $ 
 */
@Slf4j
@Service(value="withDrawAuditService")
@Transactional(readOnly=true)
public class WithDrawAuditServiceImpl implements WithDrawAuditService {
	
	@Autowired
	private WithDrawCheckService withDrawCheckService;
	
	@Autowired
	private AuditInfoRepository auditInfoRepository;
	
	@Autowired
	private LogInfoEntityRepository logInfoRepository;
	
	@Autowired
	private TradeFlowInfoRepository tradeFlowInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private AccountFlowInfoRepository accountFlowInfoRepository;
	
	@Autowired
	private WithdrawCashRepositoryCustom withdrawCashRepositoryCustom;

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
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	public  ResultVo saveWithdrawCashAudit(Map<String, Object> paramsMap) throws SLException {
		Map<String,Object> conditionMap=new HashMap<String, Object>();
		conditionMap.put("id", (String)paramsMap.get("id"));
		conditionMap.put("auditStatus", Constant.AUDIT_STATUS_REVIEWD);
		int rows = withdrawCashRepositoryCustom.updateWithDrawCashRecord(conditionMap);
		if(rows==0){
			throw new SLException("重复操作,请返回列表查看!!!");
		}

		AuditInfoEntity auditInfoEntity = auditInfoRepository.findByIdAndAuditStatus((String)paramsMap.get("id"),Constant.AUDIT_STATUS_REVIEWD);
		if( null == auditInfoEntity )
			return new ResultVo(false, "非法的提现审核操作,没有找到该提现的申请信息!");
		
		//检查审核状态
		if(Constant.AUDIT_STATUS_REfUSE.equals(auditInfoEntity.getAuditStatus()) || Constant.AUDIT_STATUS_PASS.equals(auditInfoEntity.getAuditStatus()))
			return new ResultVo(false, "提现已经处理完成,请勿 重复提交!");
		TradeFlowInfoEntity flowInfo = tradeFlowInfoRepository.findFirstByRelatePrimaryOrderByCreateDateDesc((String)paramsMap.get("id"));
		if (null == flowInfo)
			return new ResultVo(false, "非法的提现审核操作,没有找到该比提现的流水过程信息!");
		AccountInfoEntity accountInfo =  accountInfoRepository.findFirstByCustIdOrderByCreateDateDesc(auditInfoEntity.getCustId());
		if( null == accountInfo )
			return new ResultVo(false, "非法的提现审核操作,没有账户信息!");
		
		// 取原冻结流水
		AccountFlowInfoEntity oldFreezeAccountFlowInfo = accountFlowInfoRepository.findFirstByTradeNoOrderByCreateDateDesc(flowInfo.getTradeNo());
		if(null == oldFreezeAccountFlowInfo)
			return new ResultVo(false, "客户的账户流水信息不存在");
		AccountFlowInfoEntity accountFlowInfo = oldFreezeAccountFlowInfo.clone();

		//用户账户信息
		CustInfoEntity cust = custInfoRepository.findOne(auditInfoEntity.getCustId());
	
		//查询账户默认银行卡
		BankCardInfoEntity bankInfo = bankCardInfoRepository.findFirstByCustInfoEntityAndCardNoAndIsDefaultAndRecordStatus(cust,flowInfo.getBankCardNo(),Constant.BANKCARD_DEFAULT,Constant.VALID_STATUS_VALID);
		if(null == bankInfo && Constant.AUDIT_STATUS_PASS.equals((String)paramsMap.get("auditStatus")) )
			return new ResultVo(false, "用户银行卡信息不存在");
		
		//提现手续费
		BigDecimal withDrawExpense = paramService.findWithDrawExpenseAmount();
		String message="提现审核成功";
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
				ResultVo resultVo = null;
				try {
					resultVo = thirdPartyPayService.trustWithdrawCash(thirdPartyPayParams);
				} catch (Exception e) {
					withDrawCheckService.updateWithdrawAndFlowProcess(auditInfoEntity.getId(), flowInfo.getId(), (String)paramsMap.get("custId"));
					log.error("提现审核调用TPP出现异常:" + e.toString());
					throw new SLException("提现审核调用TPP出现异常!");
				}
				if (!ResultVo.isSuccess(resultVo)) {
					//记录提现解冻流水和流水详情,更新账户的冻结金额和可用金额
					custAccountService.updateCustAccount("",flowInfo, accountFlowInfo, accountInfo, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW);
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
					custAccountService.updateCustAccount("提现审核拒绝,解冻流水记录",flowInfo, accountFlowInfo, accountInfo, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW);
				} catch (Exception e) {
					log.error("更新客户账户信息,并且记录账户流水信息失败", e);
					throw new SLException("更新账户出现异常!");
				}
				flowInfo.setTradeStatus(Constant.TRADE_STATUS_04);
				auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_04);
				auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_REfUSE);
				//向微信推送提现失败
				sendSmsToVX(cust,null != cust ? cust.getMobile():"", Constant.SMS_TYPE_VX_2, accountFlowInfo.getTradeAmount().toString(),auditInfoEntity.getCreateDate());
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
		LogInfoEntity logInfo = new LogInfoEntity(Constant.TABLE_BAO_T_AUDIT_INFO,(String)paramsMap.get("id"),  Constant.OPERATION_TYPE_10, auditInfoEntity.getAuditStatus(), (String)paramsMap.get("auditStatus"), operDesc.toString(), auditInfoEntity.getCustId());
		logInfo.setOperIpaddress((String)paramsMap.get("ipAddress"));
		//createUser作为被操作人的id存储，方便会员管理的日志查询 朱敏让修改的
		logInfo.setBasicModelProperty((String)paramsMap.get("custId"), true);

		//更新交易流水信息
		if(!flowInfo.updateTradeFlowInfo(flowInfoUpd))
			throw new SLException("更新交易流水信息失败");
		
		//更新审核信息
		if(!auditInfoEntity.updateAuditInfo(auditInfo))
			throw new SLException("更新审核信息");
		
		//新增审核日志信息
		logInfoRepository.save(logInfo);
		return new ResultVo(true,message,Constant.AUDIT_STATUS_REfUSE.equals((String)paramsMap.get("auditStatus")) ? sendSms((String)paramsMap.get("custId"),null != cust ? cust.getMobile():"", Constant.SMS_TYPE_WITHDRAW_FAIL, accountFlowInfo.getTradeAmount().toString(),auditInfoEntity.getCreateDate()) : null);
	}

	/**
	 * 提现回调成功处理业务
	 */
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Override
	public ResultVo callbackWithdrawCash(Map<String, Object> paramsMap)throws SLException {
		
		
      /**		
        * 一 ：TPP第三方提现冻结成功。
		1：根据交易编号查询账户流水信息，并检查该交易是否已经结束。
		2：未结束的情况下更新交易状态
		3：根据账户流水信息查询的关联主键，查询账户的提现申请的审核信息。
		4：根据用户ID查询客户的账户信息。
		5：根据交易编号查询账户流水信息。
		 提现解冻 记录账户流水  更新账户表  调用第三方做解冻操作
		 提现操作 计算提现手续费 记录账户流水 更新账户表 然后再做提现操作。
		6：修改审核表状态审核通过、交易失败、交易成功
		*/
		TradeFlowInfoEntity flowInfo = tradeFlowInfoRepository.findByTradeNo((String)paramsMap.get("tradeNo"));	
		if(null == flowInfo)
			throw new SLException("该交易编号的交易流水数据不存在");
		//1：根据交易编号查询账户流水信息，并检查该交易是否已经结束。
		if(Constant.TRADE_STATUS_03.equals(flowInfo.getTradeStatus()) || Constant.TRADE_STATUS_04.equals(flowInfo.getTradeStatus()))
			throw new SLException("交易已经结束");
		//2：未结束的情况下更新交易流水交易状态
		TradeFlowInfoEntity flowUpd = new TradeFlowInfoEntity();
		flowUpd.setTradeStatus((String)paramsMap.get("status"));
		flowUpd.setBasicModelProperty(flowInfo.getLastUpdateUser(), false);
		if (!flowInfo.updateTradeFlowInfo(flowUpd))
			throw new SLException("更新流水过程详情失败!");
	
		//3：根据账户流水信息查询的管理主键，查询账户的提现申请的审核信息。
		AuditInfoEntity auditInfo = auditInfoRepository.findOne(flowInfo.getRelatePrimary());
		if(null == auditInfo)
			throw new SLException("提现申请数据不存在");
		
		//4：根据用户ID查询客户的账户信息。
		AccountInfoEntity accountInfo =  accountInfoRepository.findFirstByCustIdOrderByCreateDateDesc(auditInfo.getCustId());
		if(null == accountInfo)
			throw new SLException("客户的账户信息不存在");
		//5：根据交易编号查询账户流水信息。
		AccountFlowInfoEntity accountFlowInfo = accountFlowService.findByTradeNoIsRead(flowInfo.getTradeNo());
		if(null == accountFlowInfo)
			throw new SLException("客户的账户流水信息不存在");
		
		// update by wangjf 2016-12-17
		/*//生成提现解冻流水和流水详情，更新账户可用余额和冻结金额
		custAccountService.updateCustAccount("",flowInfo, accountFlowInfo, accountInfo, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW);
		//生成提现手续费流水和流水详情，更新账户可用余额和总金额
		custAccountService.updateCustAccount("",flowInfo, accountFlowInfo, accountInfo, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW_EXPENSE);
		//生成提现流水和流水详情，更新账户可用余额和总金额
		custAccountService.updateCustAccount("",flowInfo, accountFlowInfo, accountInfo, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW);
		*/
		// 提现解冻
		String requestNo = accountFlowInfo.getRequestNo();
		BigDecimal tradeAmount = accountFlowInfo.getTradeAmount();
		BigDecimal tradeExpenses = flowInfo.getTradeExpenses();
		List<AccountFlowInfoEntity> unFreeAccountList = custAccountService.updateAccount(accountInfo, null, null, 
				null, "8", SubjectConstant.TRADE_FLOW_TYPE_UNFREEZE, 
				requestNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW, 
				null, null,  
				null, Constant.SYSTEM_USER_BACK);
		unFreeAccountList.get(0).setOldTradeNo(accountFlowInfo.getTradeNo());// 设置解冻的原流水号为冻结流水号
		
		// 提现手续费
		if(tradeExpenses.compareTo(BigDecimal.ZERO) == 0) { // 公司收益账户出
			AccountInfoEntity accEarn = accountInfoRepository.findOne(Constant.ACCOUNT_ID_ERAN);
			if(accEarn == null) {
				throw new SLException("公司收益账户不存在");
			}
			
			custAccountService.updateAccount(accEarn, null, null, 
					null, "6", SubjectConstant.TRADE_FLOW_TYPE_EXPENSE, 
					requestNo, paramService.findWithDrawExpenseAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW_EXPENSE, 
					null, null,  
					null, Constant.SYSTEM_USER_BACK);
		}
		else { // 用户自己出			
			custAccountService.updateAccount(accountInfo, null, null, 
					null, "6", SubjectConstant.TRADE_FLOW_TYPE_EXPENSE, 
					requestNo, tradeExpenses, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW_EXPENSE, 
					null, null,  
					null, Constant.SYSTEM_USER_BACK);
		}
		
		// 提现金额
		custAccountService.updateAccount(accountInfo, null, null, 
				null, "6", SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW, 
				requestNo, ArithUtil.sub(tradeAmount, tradeExpenses), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW, 
				null, null,  
				null, Constant.SYSTEM_USER_BACK);
		
       //修改审核表状态审核通过、交易成功、交易成功
	   AuditInfoEntity auditInfoUpd  = new AuditInfoEntity();		
	   auditInfoUpd.setTradeStatus((String)paramsMap.get("tradeStatus"));
	   auditInfoUpd.setAuditStatus((String)paramsMap.get("auditStatus"));
	   auditInfoUpd.setBasicModelProperty(auditInfo.getLastUpdateUser(),false);
	   if(!auditInfo.updateAuditInfo(auditInfoUpd))
		   throw new SLException("更新提现信息失败");
	   
		//提现审核通过发送提示短信
		CustInfoEntity cust = custInfoRepository.findOne(auditInfo.getCustId());
		//向微信推送提现成功消息
		sendSmsToVX(cust,null != cust ? cust.getMobile():"", Constant.SMS_TYPE_VX_1, accountFlowInfo.getTradeAmount().toString(),auditInfo.getCreateDate());
		return new ResultVo(true,"提现回调正常业务处理成功",sendSms(cust.getId(),null != cust ? cust.getMobile():"", Constant.SMS_TYPE_WITHDRAW_SUCCESS, accountFlowInfo.getTradeAmount().toString(),auditInfo.getCreateDate()));
	}

	/**
	 * 提现回调失败处理业务
	 */
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Override
	public ResultVo callbackWithdrawCashFailed(Map<String, Object> paramsMap)throws SLException {
		TradeFlowInfoEntity flowInfo = tradeFlowInfoRepository.findByTradeNo((String)paramsMap.get("tradeNo"));	
		if(null == flowInfo)
			throw new SLException("该交易编号的交易流水数据不存在");
		//校验交易是否已经结束。
		if(Constant.TRADE_STATUS_03.equals(flowInfo.getTradeStatus()) || Constant.TRADE_STATUS_04.equals(flowInfo.getTradeStatus()))
			throw new SLException("交易已经结束");
		//更新交易流水
		TradeFlowInfoEntity flowUpd = new TradeFlowInfoEntity();
		flowUpd.setTradeStatus((String)paramsMap.get("status"));
		flowUpd.setBasicModelProperty(flowInfo.getLastUpdateUser(), false);
		if (!flowInfo.updateTradeFlowInfo(flowUpd))
			throw new SLException("更新流水过程详情失败!");
		
		//根据账户流水信息查询的管理主键，查询账户的提现申请的审核信息。
		AuditInfoEntity auditInfo = auditInfoRepository.findOne(flowInfo.getRelatePrimary());
		if(null == auditInfo)
			throw new SLException("提现申请数据不存在");
		//修改审核表状态审核通过、交易状态失败交易失败
		AuditInfoEntity auditInfoUpd = new AuditInfoEntity();
		auditInfoUpd.setTradeStatus((String) paramsMap.get("tradeStatus"));
		auditInfoUpd.setAuditStatus((String) paramsMap.get("auditStatus"));
		auditInfoUpd.setBasicModelProperty(auditInfo.getLastUpdateUser(), false);
		if (!auditInfo.updateAuditInfo(auditInfoUpd))
			throw new SLException("更新提现信息失败");

		//根据用户ID查询客户的账户信息。
		AccountInfoEntity accountInfo =  accountInfoRepository.findFirstByCustIdOrderByCreateDateDesc(auditInfo.getCustId());
		if(null == accountInfo)
			throw new SLException("客户的账户信息不存在");
		//查询原冻结账户流水
		AccountFlowInfoEntity accountFlowInfo = accountFlowService.findByTradeNoIsRead(flowInfo.getTradeNo());
		if(null == accountFlowInfo)
			throw new SLException("客户的账户流水信息不存在");
		
		//生成提现解冻流水和流水详情，更新账户可用余额和冻结金额
		custAccountService.updateCustAccount("",flowInfo, accountFlowInfo, accountInfo, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW);
		//提现审核拒绝发送提示短信
		CustInfoEntity cust = custInfoRepository.findOne(auditInfo.getCustId());
		
		//向微信推送提现失败
		sendSmsToVX(cust,null != cust ? cust.getMobile():"", Constant.SMS_TYPE_VX_2, accountFlowInfo.getTradeAmount().toString(),auditInfo.getCreateDate());
		return new ResultVo(true,"提现回调异常业务处理成功",sendSms(cust.getId(),null != cust ? cust.getMobile():"", Constant.SMS_TYPE_WITHDRAW_FAIL, accountFlowInfo.getTradeAmount().toString(),auditInfo.getCreateDate()));
	}

//----------私有方法区---------------------------------------------------------------------------------------------------------------------------------	
	
	/**
	 * 异步发送邮件
	 * @param mobile
	 * @param messageType
	 * @param tradeMount
	 */
	private Map<String,Object> sendSms(String custId,String mobile,String messageType,String tradeMount,Date sendDate) {
		final Map<String,Object> mailParams = Maps.newHashMap();
		mailParams.put("custId", custId);
		mailParams.put("mobile", mobile);
		mailParams.put("messageType", messageType);
		mailParams.put("values", new String[]{DateUtils.formatDate(sendDate, "yyyy-MM-dd HH:mm:ss"),tradeMount});
//		//做异步发送邮件处理,短信服务已经做了异步处理
//		try {
//			smsService.asnySendSMS(mailParams);
//		} catch (Exception e) {
//			log.info(e.getCause().toString());
//		}
		return mailParams;
	}
	
	/**
	 * 向微信公众号推送消息
	 * @param cust
	 * @param mobile
	 * @param dataType
	 * @param tradeMount
	 * @param sendDate
	 */
	@SuppressWarnings("deprecation")
	private void sendSmsToVX(CustInfoEntity cust,String mobile,String dataType,String tradeMount,Date sendDate) {
		final Map<String,Object> Params = Maps.newHashMap();
		String openId=cust.getOpenid();
		if(openId!=null){
			Params.put("openId", openId);
			Params.put("template_id", dataType);
			
			StringBuilder sb=new StringBuilder()
				.append(sendDate)
				.append("|")
				.append(tradeMount);
			
			Params.put("data", java.net.URLEncoder.encode(sb.toString()));
			String sign=HttpRequestUtil.sign(openId,dataType, sb.toString());
			Params.put("sign", sign);
			HttpRequestUtil.pushSmsToVX(Params);
		}
	}
	
}
