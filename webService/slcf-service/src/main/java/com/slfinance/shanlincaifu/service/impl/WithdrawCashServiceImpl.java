/** 
 * @(#)WithdrawCashServiceImpl.java 1.0.0 2015年4月27日 上午11:40:39  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
import com.slfinance.shanlincaifu.service.WithDrawCheckService;
import com.slfinance.shanlincaifu.service.WithdrawCashService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.HttpRequestUtil;
import com.slfinance.shanlincaifu.utils.PageFuns;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.util.BeanMapConvertUtil;
import com.slfinance.vo.ResultVo;

/**   
 * 提现模块业务Service实现
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月27日 上午11:40:39 $ 
 */
@Slf4j
@Service(value="withdrawCashService")
@Transactional(readOnly=true)
public class WithdrawCashServiceImpl implements WithdrawCashService {
	
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
	 *  提现管理--列表(分页查询)
	 *  
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@Override
	public Map<String, Object> findAllWithdrawCashList(Map<String, Object> paramsMap) throws SLException {
		PageFuns.paramsConvert(paramsMap);
		return PageFuns.pageVoToMap(withdrawCashRepository.findAllWithdrawCashList(paramsMap));
	}

	/**
	 *  提现管理--明细-提现详细
	 *  
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@Override
	public Map<String, Object> findWithdrawalCashDetailInfo(Map<String, Object> paramsMap) throws SLException {
		return BeanMapConvertUtil.beanToMap(withdrawCashRepository.findWithdrawalCashDetailInfo(paramsMap));
	}
	
	/**
	 *  提现管理--明细-审核记录
	 *  
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@Override
	public List<Map<String, Object>> findAuditLogByWithDraw(Map<String, Object> paramsMap) throws SLException {
		return BeanMapConvertUtil.beanToMapList(withdrawCashRepository.findByCustIdOrId(paramsMap));
	}

	/**
	 *  提现管理--统计
	 *  
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@Override
	public Map<String, Object> findAllWithdrawCashSum(Map<String, Object> paramsMap) throws SLException {
		return BeanMapConvertUtil.toMap(withdrawCashRepository.findAllWithdrawCashSum(paramsMap));
	}

	/**
	 * 提现管理--提现审核查询-历史提现记录
	 * 
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@Override
	public List<Map<String, Object>> findWithdrawCashAuditList(Map<String, Object> paramsMap) throws SLException {
		return BeanMapConvertUtil.beanToMapList(withdrawCashRepository.findByCustIdOrId(paramsMap));
	}
	
	/**
	 * 提现管理--提现审核查询-历史提现记录-分页
	 * 
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@Override
	public Map<String, Object> findWithdrawCashAuditListPage(Map<String, Object> paramsMap)throws SLException {
		PageFuns.paramsConvert(paramsMap);
		return PageFuns.pageVoToMap(withdrawCashRepository.findPageByCustIdOrId(paramsMap));
	}
	
	
	/**
	 * 提现管理--提现审核提交
	 * 
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@Override
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	public ResultVo saveWithdrawCashAudit(Map<String, Object> paramsMap) throws SLException {
		/**更新审核信息 、新增审核日志信息**/
		AuditInfoEntity auditInfoEntity = auditInfoRepository.findOne((String)paramsMap.get("id"));
		if( null == auditInfoEntity )
			throw new SLException("非法的提现审核操作,没有找到该提现的申请信息!");
		//检查审核状态
		if(Constant.AUDIT_STATUS_REfUSE.equals(auditInfoEntity.getAuditStatus()) || Constant.AUDIT_STATUS_PASS.equals(auditInfoEntity.getAuditStatus()))
			return new ResultVo(false, "提现已经处理完成,请勿重复提交!");
		TradeFlowInfoEntity flowInfo = tradeFlowInfoRepository.findFirstByRelatePrimaryOrderByCreateDateDesc((String)paramsMap.get("id"));
		if (null == flowInfo)
			throw new SLException("非法的提现审核操作,没有找到该比提现的流水过程信息!");
		
		//检查交易状态
		if(Constant.TRADE_STATUS_03.equals(flowInfo.getTradeStatus()) || Constant.TRADE_STATUS_04.equals(auditInfoEntity.getTradeStatus()))
			return new ResultVo(false, "提现已经处理完成,请勿重复提交!");
		
		AccountInfoEntity accountInfo =  accountInfoRepository.findFirstByCustIdOrderByCreateDateDesc(auditInfoEntity.getCustId());
		if( null == accountInfo )
			throw new SLException("非法的提现审核操作,没有账户信息!");
		
		// 取原冻结流水
		AccountFlowInfoEntity accountflow = accountFlowService.findByTradeNoIsRead(flowInfo.getTradeNo());
		if( null == accountflow )
			throw new SLException("非法的提现审核操作,没有冻结流水信息!");
		
		//用户账户信息
		CustInfoEntity cust = custInfoRepository.findOne(auditInfoEntity.getCustId());
		if(null == cust)
			throw new SLException("用户信息不存在");
		
		//查询账户默认银行卡
		BankCardInfoEntity bankInfo = bankCardInfoRepository.findFirstByCustInfoEntityAndCardNoAndIsDefaultAndRecordStatus(cust,flowInfo.getBankCardNo(),Constant.BANKCARD_DEFAULT,Constant.VALID_STATUS_VALID);
		if(null == bankInfo && Constant.AUDIT_STATUS_PASS.equals((String)paramsMap.get("auditStatus")))
			throw new SLException("用户银行卡信息不存在");
		//提现手续费
		String message="提现审核成功";
		BigDecimal withDrawExpense = paramService.findWithDrawExpenseAmount();
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
					//调用第三方信息 暂时不调用第三方提现操作
					resultVo = thirdPartyPayService.trustWithdrawCash(thirdPartyPayParams);
				} catch (Exception e) {
					//调用第三方发生异常，审核信息状态不做任何处理更新处理,更新交易状态为处理中
					withDrawCheckService.updateWithdrawAndFlowProcess(auditInfoEntity.getId(), flowInfo.getId(), (String)paramsMap.get("custId"));
					log.error("提现审核调用TPP出现异常:" + e.toString());
					throw new SLException("提现审核调用TPP出现异常!");
				}
				//第三方成功之后更新处理中状态，等待支付系统回调；否则对当前提现不做任何操作，直到第三方处理成功
				if (!ResultVo.isSuccess(resultVo)) {
					try {
						//需要记录解冻流水
						BigDecimal unFreezeOfTotal = accountInfo.getAccountTotalAmount();
						BigDecimal freezeAmount = ArithUtil.formatScale2(ArithUtil.sub(accountInfo.getAccountFreezeAmount(), flowInfo.getTradeAmount()));
						BigDecimal unFreezeAvailable = ArithUtil.formatScale2(ArithUtil.add(accountInfo.getAccountAvailableAmount(), flowInfo.getTradeAmount()));
						AccountFlowInfoEntity unFreezeFlow = new AccountFlowInfoEntity(accountInfo.getId(), accountInfo.getCustId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW, numberService.generateTradeNumber(), accountflow.getTradeNo(),accountflow.getRequestNo(),"", accountflow.getTradeAmount(), new Date(), "", Constant.ACCOUNT_TYPE_MAIN,unFreezeOfTotal, freezeAmount, unFreezeAvailable, SubjectConstant.SUBJECT_TYPE_AMOUNT, BigDecimal.ZERO);
						unFreezeFlow.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
						accountFlowInfoRepository.save(unFreezeFlow);
						// 提现解冻 记录账户流水详情更新账户表
//						AccountFlowDetailEntity accountDetailUnFreeze = new AccountFlowDetailEntity(unFreezeFlow.getId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW, "", accountflow.getTradeAmount(), "", "提现解冻");
//						accountDetailUnFreeze.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
						custAccountService.updateCustAccount(accountflow, accountInfo);
						flowInfo.setTradeStatus(Constant.TRADE_STATUS_04);
						auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_04);
						message = resultVo.getValue("message") != null ? (String)resultVo.getValue("message"):"调用TPP业务没有成功"; 
					} catch (Exception e) {
						log.error("更新客户账户信息,并且记录账户流水信息失败", e);
						throw new SLException("更新账户出现异常!");
					}
				} else {
					flowInfo.setTradeStatus(Constant.TRADE_STATUS_02);
					auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_02);
				}
				auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_PASS);
				break;
			case Constant.AUDIT_STATUS_REfUSE:
				try {
					//审核拒绝,需要记录解冻流水
					//审核拒绝,需要记录解冻流水
					BigDecimal unFreezeOfTotal = accountInfo.getAccountTotalAmount();
					BigDecimal freezeAmount = ArithUtil.formatScale2(ArithUtil.sub(accountInfo.getAccountFreezeAmount(), flowInfo.getTradeAmount()));
					BigDecimal unFreezeAvailable = ArithUtil.formatScale2(ArithUtil.add(accountInfo.getAccountAvailableAmount(), flowInfo.getTradeAmount()));
					AccountFlowInfoEntity unFreezeFlow = new AccountFlowInfoEntity(accountInfo.getId(), accountInfo.getCustId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW, numberService.generateTradeNumber(), accountflow.getTradeNo(),accountflow.getRequestNo(),"", accountflow.getTradeAmount(), new Date(), "提现审核拒绝,解冻流水记录", Constant.ACCOUNT_TYPE_MAIN,unFreezeOfTotal, freezeAmount, unFreezeAvailable, SubjectConstant.SUBJECT_TYPE_AMOUNT, BigDecimal.ZERO);
					unFreezeFlow.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
					accountFlowInfoRepository.save(unFreezeFlow);
					// 提现解冻 记录账户流水详情更新账户表
//					AccountFlowDetailEntity accountFlowDetail = new AccountFlowDetailEntity(unFreezeFlow.getId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW, "", accountflow.getTradeAmount(), "", "提现解冻");
//					accountFlowDetail.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
					custAccountService.updateCustAccount(accountflow, accountInfo);
					message = "提现拒绝审核成功";
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
		return new ResultVo(true,message,Constant.AUDIT_STATUS_REfUSE.equals((String)paramsMap.get("auditStatus")) ? sendSms((String)paramsMap.get("custId"),null != cust ? cust.getMobile():"", Constant.SMS_TYPE_WITHDRAW_FAIL, accountflow.getTradeAmount().toString(),auditInfoEntity.getCreateDate()) : null);
	}

	/**
	 * 提现回调成功处理业务
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
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
		// 5.1提现解冻 记录账户流水  更新账户表  
		AccountFlowInfoEntity accountflow = accountFlowService.findByTradeNoIsRead(flowInfo.getTradeNo());
		if( null == accountflow )
			throw new SLException("客户的账户流水信息不存在");
		
		//新增帐户流水和更新帐户信息
		//需要记录解冻流水
		BigDecimal unFreezeOfTotal = accountInfo.getAccountTotalAmount();
		BigDecimal freezeAmount = ArithUtil.formatScale2(ArithUtil.sub(accountInfo.getAccountFreezeAmount(), flowInfo.getTradeAmount()));
		BigDecimal unFreezeAvailable = ArithUtil.formatScale2(ArithUtil.add(accountInfo.getAccountAvailableAmount(), flowInfo.getTradeAmount()));
		AccountFlowInfoEntity unFreezeFlow = new AccountFlowInfoEntity(accountInfo.getId(), accountInfo.getCustId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW, numberService.generateTradeNumber(), accountflow.getTradeNo(),accountflow.getRequestNo(),"", accountflow.getTradeAmount(), new Date(), "", Constant.ACCOUNT_TYPE_MAIN,unFreezeOfTotal, freezeAmount, unFreezeAvailable, SubjectConstant.SUBJECT_TYPE_AMOUNT, BigDecimal.ZERO);
		unFreezeFlow.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		accountFlowInfoRepository.save(unFreezeFlow);
		// 提现解冻 记录账户流水详情更新账户表
//		AccountFlowDetailEntity accountDetailUnFreeze = new AccountFlowDetailEntity(unFreezeFlow.getId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW, "", accountflow.getTradeAmount(), "", "提现解冻");
//		accountDetailUnFreeze.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		custAccountService.updateCustAccount(accountflow, accountInfo);
		
		// 提现操作 计算提现手续费 记录账户流水 更新账户表  此时生成的账户流水详情信息中，交易金额= 提现金额 - 提现手续费
		//记录提现流水 总金额=总金额-交易金额+提现手续费   可用金额 = 可用金额-交易金额+提现手续费 
		//此处这样处理是为了不改变 custAccountService中accountflow中的值
		//记录提现手续费流水 ,提现手续费tradeNo比提现的tradeNo要先生成
		BigDecimal withDrawExpense = paramService.findWithDrawExpenseAmount();//提现手续费
		BigDecimal expenseTotal = ArithUtil.sub(accountInfo.getAccountTotalAmount(), withDrawExpense);
		BigDecimal expenseAvailable = ArithUtil.sub(accountInfo.getAccountAvailableAmount(), withDrawExpense);
		AccountFlowInfoEntity withDrawExpenseFlow = new AccountFlowInfoEntity(accountInfo.getId(), accountInfo.getCustId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW_EXPENSE, numberService.generateTradeNumber(), "",accountflow.getRequestNo(),SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, withDrawExpense, new Date(), "", Constant.ACCOUNT_TYPE_MAIN,expenseTotal, accountInfo.getAccountFreezeAmount(), expenseAvailable, SubjectConstant.SUBJECT_TYPE_AMOUNT, BigDecimal.ZERO);
		withDrawExpenseFlow.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		
		BigDecimal withDrawTotal = ArithUtil.sub(accountInfo.getAccountTotalAmount(), flowInfo.getTradeAmount());
		BigDecimal withDrawAvailable =ArithUtil.sub(accountInfo.getAccountAvailableAmount(), flowInfo.getTradeAmount());
		AccountFlowInfoEntity withDrawFlow = new AccountFlowInfoEntity(accountInfo.getId(), accountInfo.getCustId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW, numberService.generateTradeNumber(), "",accountflow.getRequestNo(),SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, ArithUtil.formatScale2(ArithUtil.sub(accountflow.getTradeAmount(), withDrawExpense)), new Date(), "", Constant.ACCOUNT_TYPE_MAIN,withDrawTotal, accountInfo.getAccountFreezeAmount(), withDrawAvailable, SubjectConstant.SUBJECT_TYPE_AMOUNT, BigDecimal.ZERO);
		withDrawFlow.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		
		accountFlowInfoRepository.save(Arrays.asList(withDrawFlow,withDrawExpenseFlow));
		
//		//提现申请和提现手续费修改为分别记账户流水信息
//		AccountFlowDetailEntity withdrawDatailExpense = new AccountFlowDetailEntity(withDrawExpenseFlow.getId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW_EXPENSE, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, withDrawExpense, "", "提现手续费");
//		withdrawDatailExpense.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
//		accountFlowDetailRepository.save(withdrawDatailExpense);
		
//		//记录提现流水详情和手续费详情
//		AccountFlowDetailEntity accountDetailWithDraw = new AccountFlowDetailEntity(withDrawFlow.getId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, ArithUtil.formatScale2(ArithUtil.sub(accountflow.getTradeAmount(), withDrawExpense)), "", "提现");
//		accountDetailWithDraw.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		custAccountService.updateCustAccount(accountflow, accountInfo);
		
       //修改审核表状态审核通过、交易成功、交易成功
	   AuditInfoEntity auditInfoUpd  = new AuditInfoEntity();		
	   auditInfoUpd.setTradeStatus((String)paramsMap.get("tradeStatus"));
	   auditInfoUpd.setAuditStatus((String)paramsMap.get("auditStatus"));
	   auditInfoUpd.setBasicModelProperty(auditInfo.getLastUpdateUser(),false);
	   if(!auditInfo.updateAuditInfo(auditInfoUpd))
		   throw new SLException("更新提现信息失败");
	   
		//提现审核通过发送提示短信
		//用户账户信息
		CustInfoEntity cust = custInfoRepository.findOne(auditInfo.getCustId());
		return new ResultVo(true,"提现回调正常业务处理成功",sendSms(cust.getId(),null != cust ? cust.getMobile():"", Constant.SMS_TYPE_WITHDRAW_SUCCESS, accountflow.getTradeAmount().toString(),auditInfo.getCreateDate()));
	}

	/**
	 * 提现回调失败处理业务
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo callbackWithdrawCashFailed(Map<String, Object> paramsMap)throws SLException {
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
		// 4：修改审核表状态审核通过、交易状态失败交易失败
		AuditInfoEntity auditInfoUpd = new AuditInfoEntity();
		auditInfoUpd.setTradeStatus((String) paramsMap.get("tradeStatus"));
		auditInfoUpd.setAuditStatus((String) paramsMap.get("auditStatus"));
		auditInfoUpd.setBasicModelProperty(auditInfo.getLastUpdateUser(), false);
		if (!auditInfo.updateAuditInfo(auditInfoUpd))
			throw new SLException("更新提现信息失败");

		//4：根据用户ID查询客户的账户信息。
		AccountInfoEntity accountInfo =  accountInfoRepository.findFirstByCustIdOrderByCreateDateDesc(auditInfo.getCustId());
		if(null == accountInfo)
			throw new SLException("客户的账户信息不存在");
		
		// 5.1提现解冻 记录账户流水  更新账户表  
		AccountFlowInfoEntity accountflow = accountFlowService.findByTradeNoIsRead(flowInfo.getTradeNo());
		if(null == accountflow)
			throw new SLException("客户的账户流水信息不存在");
		//新增帐户流水和更新帐户信息
		//需要记录解冻流水
		BigDecimal unFreezeOfTotal = accountInfo.getAccountTotalAmount();
		BigDecimal freezeAmount = ArithUtil.formatScale2(ArithUtil.sub(accountInfo.getAccountFreezeAmount(), flowInfo.getTradeAmount()));
		BigDecimal unFreezeAvailable = ArithUtil.formatScale2(ArithUtil.add(accountInfo.getAccountAvailableAmount(), flowInfo.getTradeAmount()));
		AccountFlowInfoEntity unFreezeFlow = new AccountFlowInfoEntity(accountInfo.getId(), accountInfo.getCustId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW, numberService.generateTradeNumber(), accountflow.getTradeNo(),accountflow.getRequestNo(),"", accountflow.getTradeAmount(), new Date(), "提现审核TPP失败回调,解冻流水记录", Constant.ACCOUNT_TYPE_MAIN,unFreezeOfTotal, freezeAmount,unFreezeAvailable , SubjectConstant.SUBJECT_TYPE_AMOUNT, BigDecimal.ZERO);
		unFreezeFlow.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		accountFlowInfoRepository.save(unFreezeFlow);
		
//		// 提现解冻 记录账户流水详情更新账户表
//		AccountFlowDetailEntity accountFlowInfoUnFreeze = new AccountFlowDetailEntity(unFreezeFlow.getId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW,"" , accountflow.getTradeAmount(), accountInfo.getAccountNo(), "提现审核TPP失败回调,解冻流水详情记录");
//		accountFlowInfoUnFreeze.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		
		custAccountService.updateCustAccount(accountflow, accountInfo);
		//提现审核拒绝发送提示短信
		//取用户信息
		CustInfoEntity cust = custInfoRepository.findOne(auditInfo.getCustId());
		sendSms(cust.getId(),null != cust ? cust.getMobile():"", Constant.SMS_TYPE_WITHDRAW_FAIL, accountflow.getTradeAmount().toString(),auditInfo.getCreateDate());
		return new ResultVo(true,"提现回调异常业务处理成功",sendSms(cust.getId(),null != cust ? cust.getMobile():"", Constant.SMS_TYPE_WITHDRAW_FAIL, accountflow.getTradeAmount().toString(),auditInfo.getCreateDate()));
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
		//做异步发送邮件处理,短信服务已经做了异步处理
//		try {
//			smsService.asnySendSMS(mailParams);
//		} catch (Exception e) {
//			log.info(e.getCause().toString());
//		}
		return mailParams;
	}
	
}
