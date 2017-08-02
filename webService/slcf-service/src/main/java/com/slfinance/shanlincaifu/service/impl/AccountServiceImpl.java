/** 
 * @(#)AccountServiceImpl.java 1.0.0 2015年4月21日 上午11:35:12  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.entity.AutoInvestInfoEntity;
import com.slfinance.shanlincaifu.entity.AutoTransferInfoEntity;
import com.slfinance.shanlincaifu.entity.BankCardInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductRateInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.UnbindInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRespository;
import com.slfinance.shanlincaifu.repository.AutoInvestInfoRepository;
import com.slfinance.shanlincaifu.repository.AutoTransferInfoRepository;
import com.slfinance.shanlincaifu.repository.BankCardInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.DeviceInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.ProductRateInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.SystemMessageInfoRepository;
import com.slfinance.shanlincaifu.repository.TradeFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.UnbindInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthPaymentPlanInfoRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.AccountInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.BankCardInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanManagerRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.TradeFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.AccessService;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.AccountService;
import com.slfinance.shanlincaifu.service.AfficheInfoService;
import com.slfinance.shanlincaifu.service.BankCardService;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.DeviceService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.service.ThirdPartyPayService;
import com.slfinance.shanlincaifu.service.WithDrawAuditService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**
 * 账户服务实现类
 * 
 * @author HuangXiaodong
 * @version $Revision:1.0.0, $Date: 2015年4月21日 上午11:35:12 $
 */
@Slf4j
@Service("accountService")
public class AccountServiceImpl implements AccountService {
	@Autowired
	private AutoTransferInfoRepository autoTransferInfoRepository;
	@Autowired
	private AccountInfoRepository accountInfoRepository;

	@Autowired
	private AccountInfoRepositoryCustom accountInfoRepositoryCustom;

	@Autowired
	ThirdPartyPayService thirdPartyPayService;

	@Autowired
	AccountFlowService accountFlowService;

	@Autowired
	CustInfoRepository custInfoRepository;

	@Autowired
	SMSService smsService;

	@Autowired
	BankCardInfoRepository bankCardInfoRepository;

	@Autowired
	FlowNumberService numberService;

	@Autowired
	AccountFlowInfoRepository accountFlowInfoRepository;

//	@Autowired
//	AccountFlowDetailRepository accountFlowDetailRepository;

	@Autowired
	AuditInfoRespository auditInfoRespository;

	@Autowired
	TradeFlowInfoRepository tradeFlowInfoRepository;

	@Autowired
	LogInfoEntityRepository logInfoEntityRepository;

	@Autowired
	BankCardInfoRepositoryCustom bankCardInfoRepositoryCustom;

	@Autowired
	FlowNumberService flowNumberService;

	@Autowired
	ParamService paramService;

	@Autowired
	TradeFlowInfoRepositoryCustom tradeFlowInfoRepositoryCustom;

	@Autowired
	SubAccountInfoRepository subAccountInfoRepository;

	@Autowired
	RepositoryUtil repositoryUtil;

	@Autowired
	UnbindInfoRepository unbindInfoRepository;

	@Autowired
	WithDrawAuditService withDrawAuditService;

	@Autowired
	ProductRateInfoRepository productRateInfoRepository;
	
	@Autowired
	DeviceInfoRepository deviceInfoRepository;
	
	@Autowired
	AccessService accessService;
	
	@Autowired
	private InvestInfoRepository investInfoRepository;
	
	@Autowired
	private WealthPaymentPlanInfoRepository wealthPaymentPlanInfoRepository;
	
	@Autowired
	private SystemMessageInfoRepository systemMessageInfoRepository;
	
	@Autowired
	private AfficheInfoService afficheInfoService;
	
	@Autowired
	private BankCardService bankCardService;
	
	@Autowired
	private LoanInfoRepositoryCustom loanInfoRepositoryCustom;
	
	@Autowired
	private LoanManagerRepositoryCustom loanManagerRepositoryCustom;
	
	@Autowired
	private CustAccountService custAccountService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private AutoInvestInfoRepository autoInvestInfoRepository;
	
	@Autowired 
	AccountInnerClass accountInnerClass;

	/**
	 * @author HuangXiaodong 查询账户信息
	 * @return ResultVo
	 */
	@Override
	public ResultVo findAccountInfo(Map<String, Object> param) {
		String custId = param.get("custId") + "";
		if (StringUtils.isEmpty(custId)) {
			return new ResultVo(false, "用户id不能为空!");
		}
		AccountInfoEntity accountinfo = accountInfoRepository
				.findByCustId(custId);
		if (null == accountinfo) {
			return new ResultVo(false, "用户账户信息不存在!");
		}
		ResultVo resultVo = new ResultVo(true, "账户查询成功");
		resultVo.putValue("data", accountinfo);
		return resultVo;
	}

	@Override
	public Map<String, Object> findAllCustAccountSum(Map<String, Object> param) {
		// TODO 参数校验

		return accountInfoRepositoryCustom.findAllCustAccountSum(param);
	}

	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"),
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数") })
	@Override
	public Map<String, Object> findAllCustAccountList(Map<String, Object> param) {
		// TODO 参数校验

		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = accountInfoRepositoryCustom
				.findAllCustAccountList(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	/**
	 * 提现申请
	 *
	 * @author wangjf
	 * @date 2015年4月28日 上午10:09:35
	 * @param params
	 *            <tt>custId： String:用户ID</tt><br>
	 *            <tt>mobile： String:手机号</tt><br>
	 *            <tt>mobileVerifyCode： String:验证码</tt><br>
	 *            <tt>amount： BigDecimal:提现金额</tt><br>
	 *            <tt>withdrawPsd： String:提现密码</tt><br>
	 *            <tt>bankId： String:用户提现的银行卡ID</tt><br>
	 * @return
	 * @throws SLException
	 */

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo withdrawalCashAudit(Map<String, Object> params)
			throws SLException {

		String bankId = (String) params.get("bankId");
		BigDecimal withdrawAmount = new BigDecimal(params.get("amount")
				.toString());
		String withdrawPsd = (String) params.get("withdrawPsd");
		String custId = (String) params.get("custId");
//		String openProvince = (String) params.get("openProvince");
//		String openCity = (String) params.get("openCity");
//		String subBranchName = (String) params.get("subBranchName");
		String ipAddress = (String) params.get("ipAddress");
		String appSource = (String) params.get("appSource"); // 来源

		// 1.提现规则校验

		// 2. 验证用户状态
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if (!custInfoEntity.getEnableStatus().equals(Constant.ENABLE_STATUS_01)) {
			return new ResultVo(false, "账户为非正常状态，可能被冻结，请联系客服!");
		}

		// 判断是否实名认证
		if (StringUtils.isEmpty(custInfoEntity.getCustName())
				|| StringUtils.isEmpty(custInfoEntity.getCredentialsCode())) {
			return new ResultVo(false, "未实名认证，请先做实名认证!");
		}

		if (!withdrawPsd.equals(custInfoEntity.getTradePassword())) {
			return new ResultVo(false, "交易密码错误,请重新输入!");
		}

		// 判断银行卡是否在解绑申请中
		List<UnbindInfoEntity> unbindList = unbindInfoRepository
				.findByRelatePrimaryAndUnbindStatus(bankId,
						Constant.TRADE_STATUS_02);
		if (unbindList != null && unbindList.size() > 0) {
			return new ResultVo(false, "您已经为该卡提交了解绑申请，暂时无法提现");
		}

		// 3.检查银行卡是否存在
		BankCardInfoEntity bank = bankCardInfoRepository.findOne(bankId);
		if (bank == null) {
			return new ResultVo(false, "客户银行卡不存在!");
		} else if (!bank.getCustInfoEntity().getId().equals(custId)) {
			return new ResultVo(false, "银行卡和客户不匹配");
		}

//		// 设置开户行
//		bank.setSubBranchName(subBranchName);
//		bank.setOpenProvince(openProvince);
//		bank.setOpenCity(openCity);

		// 4.提现冻结 记录账户流水 更新账户表
		// 生成交易编号
		String tradeCode = numberService.generateTradeNumber();
		String requestNo = numberService.generateTradeBatchNumber();

		// 4.1 更新账户
		AccountInfoEntity custAccount = accountInfoRepository
				.findByCustId(custId);
		if (custAccount == null) {
			log.info("未找到客户账户信息:客户ID" + custId);
			throw new SLException("未找到客户账户信息");
		}

		if (withdrawAmount.compareTo(custAccount.getAccountAvailableAmount()) > 0) {
			return new ResultVo(false, "提现金额大于账户可用金额");
		}
		
		//线下提现修改
		String tradeType = SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW; //交易类型
		String applyType = SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW; //审核申请类型
		String logType = Constant.OPERATION_TYPE_06; //日志类型
		String tradeFlowType = SubjectConstant.TRADE_FLOW_TYPE_FREEZE; //流水交易类型--提现冻结
		String accountFlowType = SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW_FREEZE; //账户资金流水--提现冻结
		String memo = "线上提现";
		BigDecimal calculateWithdrawFee = paramService
				.findWithDrawExpenseAmount();// 计算提现手续费
		String flowBusiRelateType = null;
		String flowRelatePrimary = null;
		if(Constant.BANK_FLAG_OFFLINE.equals(bank.getBankFlag())){
			tradeType = SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_WITHDRAW;
			applyType = SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_WITHDRAW;
			logType = Constant.LOG_TYPE_OFFLINE_WITHDRAW_APPLY;
			tradeFlowType = SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_FREEZE; //流水交易类型--线下提现冻结
			accountFlowType = SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_OFFLINE_WITHDRAW_FREEZE; //账户资金流水--线下提现冻结
			memo = Constant.OPERATION_TYPE_43;
			calculateWithdrawFee = BigDecimal.ZERO;
			flowBusiRelateType = Constant.TABLE_BAO_T_ACCOUNT_INFO;
			flowRelatePrimary = custAccount.getId();
		}

		custAccount.setAccountAvailableAmount(ArithUtil.sub(
				custAccount.getAccountAvailableAmount(), withdrawAmount));
		custAccount.setAccountFreezeAmount(ArithUtil.add(
				custAccount.getAccountFreezeAmount(), withdrawAmount));
		custAccount.setBasicModelProperty(custId, false);
		accountInfoRepository.save(custAccount);

		// 4.2 记录 账户流水信息表
		AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService
				.saveAccountFlow(
						custAccount,
						null,
						null,
						null,
						"1",
						tradeFlowType,
						requestNo,
						null,
						withdrawAmount,
						accountFlowType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
		accountFlowInfoEntity.setTradeNo(tradeCode);
		accountFlowInfoEntity.setMemo(memo);

		// 5.插入提现审核信息
		AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
		auditInfoEntity.setCustId(custId);
		auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_ACCOUNT_INFO);
		auditInfoEntity.setRelatePrimary(custAccount.getId());
		auditInfoEntity.setApplyType(applyType);
		auditInfoEntity.setApplyTime(new Timestamp(System.currentTimeMillis()));
		auditInfoEntity.setTradeAmount(withdrawAmount);
		auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_01);
		auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_REVIEWD);
		auditInfoEntity.setBasicModelProperty(custId, true);
		auditInfoEntity = auditInfoRespository.save(auditInfoEntity);

		// 6.插入提现流水过程记录
		TradeFlowInfoEntity flow = new TradeFlowInfoEntity();
		flow.setCustId(custId);
		flow.setCustAccountId(custAccount.getId());
		flow.setRelateType(Constant.TABLE_BAO_T_AUDIT_INFO);
		flow.setRelatePrimary(auditInfoEntity.getId());
		flow.setTradeAmount(withdrawAmount);
		flow.setTradeExpenses(calculateWithdrawFee);
		flow.setTradeType(tradeType);
		flow.setBankName(bank.getBankName());
		flow.setBranchBankName(bank.getSubBranchName());
		flow.setBankCardNo(bank.getCardNo());
		flow.setTradeNo(tradeCode);
		flow.setTradeStatus(Constant.TRADE_STATUS_01);
		flow.setTradeDate(new Date());
		flow.setTradeDesc("提现");
		flow.setTradeSource(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC
				: appSource);
		// flow.setMemo(String.format("提现手续费%s元",
		// calculateWithdrawFee.toString()));
		flow.setMemo(memo);
		flow.setBasicModelProperty(custId, true);
		flow.setOpenProvince(bank.getOpenProvince());
		flow.setOpenCity(bank.getOpenCity());
		flow = tradeFlowInfoRepository.save(flow);

		// 8.记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
		logInfoEntity.setRelatePrimary(flow.getId());
		logInfoEntity.setLogType(logType);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperIpaddress(ipAddress);
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setMemo(String.format("%s提现金额%s，手续费%s",
				custInfoEntity.getLoginName(), withdrawAmount.toString(),
				calculateWithdrawFee.toString()));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);

		// 9.发送提现成功的消息
		Map<String, Object> smsParams = new HashMap<String, Object>();
		smsParams.put("mobile", custInfoEntity.getMobile());
		smsParams.put("custId", custId);
		smsParams.put("messageType", Constant.SMS_TYPE_WITHDRAWAL_APPLY);
		smsParams.put("values", withdrawAmount.toString());
		
		// 记录设备信息(提现)
		if(params.containsKey("channelNo")) {
			Map<String, Object> deviceParams = Maps.newConcurrentMap();
			deviceParams.putAll(params);
			deviceParams.put("relateType", Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
			deviceParams.put("relatePrimary", flow.getId());
			deviceParams.put("tradeType", Constant.OPERATION_TYPE_06);
			deviceParams.put("userId", custId);
			deviceService.saveUserDevice(deviceParams);
		}

		return new ResultVo(true, "提现申请提交成功!", smsParams);
	}

	@Override
	public AccountInfoEntity findByCustId(String custId) {
		AccountInfoEntity aie = this.accountInfoRepository.findByCustId(custId);
		return aie;
	}

	@Override
	public ResultVo rechargeApply(Map<String, Object> params)
			throws SLException {
		try {
			// 校验参数及充值验证
			checkRecharge(params);

			// 生成流水号
			String tradeCode = flowNumberService.generateTradeNumber();
			params.put("tradeCode", tradeCode);

			// 新增过程流水
			accountFlowService.insertRechargeFlowProcessInfo(params);

			// 若是pos版本则通过wap渠道来充值
			if("pos".equals((String)params.get("appSource"))
					&& !StringUtils.isEmpty((String)params.get("wapSource"))) {
				params.put("appSource", "wap");
			}
			// 回调
			// accountFlowService.callbackRechargeSuccess(params);

			// 调用第三方接口
			return thirdPartyPayService.trustRecharge(params);
		} catch (SLException e) {
			log.error(String.format("用户%s充值失败！%s",
					(String) params.get("custId"), e.getMessage()));
			return new ResultVo(false, e.getMessage());
		} catch (Exception e) {
			log.error(String.format("用户%s充值失败！%s",
					(String) params.get("custId"), e.getMessage()));
			return new ResultVo(false, e.getMessage());
		}
	}

	@Override
	public void checkRecharge(Map<String, Object> params) throws SLException {

		String custId = (String) params.get("custId");
		BigDecimal tradeAmount = new BigDecimal(
				(String) params.get("tradeAmount"));
		String payType = (String) params.get("payType");
		
		// 检查是否是ios设备，若是ios设备且缺少应用版本号则不允许充值
		ResultVo resultVo = accessService.checkAppVersion(params);
		if(!ResultVo.isSuccess(resultVo)) {
			throw new SLException((String)resultVo.getValue("message"));
		}

		// 1 验证充值金额不能为0
		BigDecimal minRechargeAmountLimited = paramService
				.findMinRechargeAmountLimited();
		if (tradeAmount.compareTo(minRechargeAmountLimited) < 0) {
			throw new SLException(String.format("充值金额必须大于等于%s元",
					minRechargeAmountLimited.toString()));
		}

		// 单笔金额不能超过上限
		BigDecimal authRechargeSingleLimited = paramService
				.findAuthRechargeSingleLimited();
		if (Constant.PAY_TYPE_01.equals(payType)) {
			if (tradeAmount.compareTo(authRechargeSingleLimited) > 0) {
				throw new SLException(String.format("单笔交易金额过大，不能高于%s",
						authRechargeSingleLimited.toString()));
			}
		}

		// 2 验证不同支付方式
		if (!StringUtils.isEmpty(payType)) {

			// 若是网银支付，则银行编号不能为空
			if ((payType).equals(Constant.PAY_TYPE_02) 
					|| (payType).equals(Constant.PAY_TYPE_03))  {
				if (StringUtils.isEmpty(params.get("bankNo")))
					throw new SLException("网银支付时银行编号不能为空");
			} else {

				// 认证支付时，银行卡号和协议号不能同时为空
				if (StringUtils.isEmpty(params.get("bankCardNo"))
						&& StringUtils.isEmpty(params.get("agreeNo")))
					throw new SLException("协议号和银行卡号不能同时为空");

				// 认证支付且用银行卡支付时（协议号为空表示用银行卡支付），银行编号不能为空
				if (StringUtils.isEmpty(params.get("agreeNo"))
						&& StringUtils.isEmpty(params.get("bankNo"))) {
					throw new SLException("认证支付非协议号支付时银行编号不能为空");
				}

				if (!StringUtils.isEmpty(params.get("bankCardNo"))) {
					String bankCardNo = (String) params.get("bankCardNo");
					bankCardNo = bankCardNo.replace(" ", "");// 去除空格
					params.put("bankCardNo", bankCardNo);
				}
			}
		} else {
			throw new SLException("支付方式不能为空");
		}

		// 验证客户状态
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if (custInfoEntity == null) {
			throw new SLException("该账户不存在");
		}
		if (!custInfoEntity.getEnableStatus().equals(Constant.ENABLE_STATUS_01)) {
			throw new SLException("账户为非正常状态，可能被冻结，请联系客服！");
		}

		// 判断是否实名认证
		if (StringUtils.isEmpty(custInfoEntity.getCredentialsCode())
				|| StringUtils.isEmpty(custInfoEntity.getCustName())) {
			throw new SLException("必须先实名认证");
		}

		// 针对认证充值：单笔最高限额5万，单日最高限额5万，单月最高限额20万
		// 针对充值成功的： 认证充值：15次/天 网银充值：20次/天
		// 针对请求次数的： 认证充值：50次/天 网银充值：50次/天
		BigDecimal authRechargeDailyAllRequest = paramService
				.findAuthRechargeDailyAllRequest();
		BigDecimal bankRechargeDailyAllRequest = paramService
				.findBankRechargeDailyAllRequest();
		BigDecimal authRechargeDailySuccessRequest = paramService
				.findAuthRechargeDailySuccessRequest();
		BigDecimal bankRechargeDailySuccessRequest = paramService
				.findBankRechargeDailySuccessRequest();
		BigDecimal authRechargeDailyLimited = paramService
				.findAuthRechargeDailyLimited();
		BigDecimal authRechargeMonthlyLimited = paramService
				.findAuthRechargeMonthlyLimited();

		List<Map<String, Object>> countsList = tradeFlowInfoRepositoryCustom
				.countRecharge(custId);
		if (countsList == null || countsList.size() == 0) { // 没有充值信息，满足条件
			return;
		}

		Map<String, Object> countsMap = null;

		if (countsList.size() == 1) { // 有一种充值

			if (!payType.equals((String) countsList.get(0).get("subTradeType"))) {
				return;
			}
			countsMap = countsList.get(0);
		} else { // 含两种充值
			if (payType.equals((String) countsList.get(0).get("subTradeType"))) {
				countsMap = countsList.get(0);
			} else {
				countsMap = countsList.get(1);
			}
		}

		if (Constant.PAY_TYPE_01.equals((String) countsMap.get("subTradeType"))) { // 认证充值
			BigDecimal allRequest = (BigDecimal) countsMap.get("allRequest");
			BigDecimal successRequest = (BigDecimal) countsMap
					.get("successRequest");
			BigDecimal sumDailyTradeAmount = (BigDecimal) countsMap
					.get("sumDailyTradeAmount");
			BigDecimal sumMonthlyTradeAmount = (BigDecimal) countsMap
					.get("sumMonthlyTradeAmount");

			if (allRequest.compareTo(authRechargeDailyAllRequest) > 0) {
				throw new SLException(String.format("系统繁忙，请稍后再试"));
			}
			if (successRequest.compareTo(authRechargeDailySuccessRequest) > 0) {
				throw new SLException(String.format("系统繁忙，请稍后再试"));
			}
			if (ArithUtil.add(sumDailyTradeAmount, tradeAmount).compareTo(
					authRechargeDailyLimited) > 0) {
				throw new SLException(String.format("单日交易金额过大，不能高于%s",
						authRechargeDailyLimited.toString()));
			}
			if (ArithUtil.add(sumMonthlyTradeAmount, tradeAmount).compareTo(
					authRechargeMonthlyLimited) > 0) {
				throw new SLException(String.format("单月交易金额过大，不能高于%s",
						authRechargeMonthlyLimited.toString()));
			}
		} else if (Constant.PAY_TYPE_02.equals((String) countsMap
				.get("subTradeType"))) { // 网银充值
			BigDecimal allRequest = (BigDecimal) countsMap.get("allRequest");
			BigDecimal successRequest = (BigDecimal) countsMap
					.get("successRequest");
			// BigDecimal sumDailyTradeAmount =
			// (BigDecimal)countsMap.get("sumDailyTradeAmount");
			// BigDecimal sumMonthlyTradeAmount =
			// (BigDecimal)countsMap.get("sumMonthlyTradeAmount");

			if (allRequest.compareTo(bankRechargeDailyAllRequest) > 0) {
				throw new SLException(String.format("系统繁忙，请稍后再试"));
			}
			if (successRequest.compareTo(bankRechargeDailySuccessRequest) > 0) {
				throw new SLException(String.format("系统繁忙，请稍后再试"));
			}
			// if(tradeAmount.compareTo(authRechargeSingleLimited) > 0){
			// throw new SLException(String.format("单笔交易金额过大，不能高于%s",
			// authRechargeSingleLimited.toString()));
			// }
			// if(ArithUtil.add(sumDailyTradeAmount,
			// tradeAmount).compareTo(authRechargeDailyLimited) > 0){
			// throw new SLException(String.format("单日交易金额过大，不能高于%s",
			// authRechargeDailyLimited.toString()));
			// }
			// if(ArithUtil.add(sumMonthlyTradeAmount,
			// tradeAmount).compareTo(authRechargeMonthlyLimited) > 0){
			// throw new SLException(String.format("单月交易金额过大，不能高于%s",
			// authRechargeMonthlyLimited.toString()));
			// }
		}
	}

	/**
	 * 获取奖励信息活期宝
	 * productId 产品类型
	 *  custId 用户id
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findRewardInfo(Map<String, Object> param) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		String productId = (String) param.get("productId");
		String custId = (String) param.get("custId");
		List<ProductRateInfoEntity> list = productRateInfoRepository
				.findProductRateInfoByProductId(productId);
		for (ProductRateInfoEntity prie : list) {
			String sql = "select nvl(sum(account_total_value),0) \"accountTotalValue\" from ("
					+ "select TRUNC(sysdate-to_date(btii.invest_date,'yyyyMMdd'))  investDay,btsai.account_total_value,btii.cust_id From BAO_T_INVEST_INFO btii,BAO_T_SUB_ACCOUNT_INFO btsai where btsai.relate_primary=btii.id and btsai.relate_type='BAO_T_INVEST_INFO' and btii.product_id='"
					+ productId
					+ "' and  btii.invest_status='有效' and btii.cust_id='"
					+ custId + "'" + ") t where t.investDay between "
					+ prie.getLowerLimitDay() + " and " + prie.getUpperLimitDay()
					+ "";
			List<Map<String, Object>> accountTotalList = repositoryUtil
					.queryForMap(sql, null);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("lowerLimitDay", prie.getLowerLimitDay());
			resultMap.put("upperLimitDay", prie.getUpperLimitDay());
			resultMap.put("yearRate", prie.getYearRate());
			resultMap.put("awardRate", prie.getAwardRate());
			if (accountTotalList.size() >= 0)
				resultMap.putAll(accountTotalList.get(0));
			result.add(resultMap);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> findAllAccountFlowSumNew(
			Map<String, Object> param) {
		StringBuffer whereCustSqlString = new StringBuffer();
		StringBuffer whereAccountSqlString = new StringBuffer();

		StringBuffer sqlString = new StringBuffer()
				.append(" select  ")
				.append("        TO_CHAR(SUM (TRUNC_AMOUNT_WEB(Q.TRADE_AMOUNT))) \"tradeAmount\"  ")
				.append("   from BAO_T_ACCOUNT_FLOW_INFO Q ")
				//.append("   INNER JOIN BAO_T_ACCOUNT_FLOW_DETAIL N ON Q.ID = N.ACCOUNT_FLOW_ID ")
				.append("   INNER JOIN BAO_T_CUST_INFO S ON Q.CUST_ID = S.ID ")
				.append("    and (s.cust_kind is null or s.cust_kind = '网站用户') ")
				// .append("   LEFT JOIN BAO_T_ACCOUNT_INFO T ON T.ID = N.TARGET_ACCOUNT ")
				// .append("   LEFT JOIN BAO_T_CUST_INFO S2 ON S2.ID = T.ID ")
				.append("   WHERE Q.ACCOUNT_TYPE = ? %s ")
				.append("   ORDER BY Q.TRADE_NO DESC ");

		List<Object> objList = new ArrayList<Object>();
		objList.add(Constant.ACCOUNT_TYPE_MAIN);
		if (!StringUtils.isEmpty(param.get("nickName"))) {
			whereCustSqlString.append(" and S.LOGIN_NAME LIKE ?");
			objList.add(new StringBuffer().append("%")
					.append(param.get("nickName")).append("%"));
		}

		if (!StringUtils.isEmpty(param.get("custName"))) {
			whereCustSqlString.append(" and S.CUST_NAME = ?");
			objList.add(param.get("custName"));
		}

		if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
			whereCustSqlString.append(" and S.CREDENTIALS_CODE = ?");
			objList.add(param.get("credentialsCode"));
		}

		// 过滤公司账户
		whereAccountSqlString.append(" and Q.ACCOUNT_ID NOT IN (?, ?, ?, ?, ?, ?, ?)");
		objList.add(Constant.ACCOUNT_ID_CENTER);
		objList.add(Constant.ACCOUNT_ID_ERAN);
		objList.add(Constant.ACCOUNT_ID_RISK);
		objList.add(Constant.ACCOUNT_ID_PROJECT_ERAN);
		objList.add(Constant.ACCOUNT_ID_PROJECT_RISK);
		objList.add(Constant.ACCOUNT_ID_REPAYMENT);
		objList.add(Constant.ACCOUNT_ID_WEALTH_CENTER);
		

		if (!StringUtils.isEmpty(param.get("opearteDateBegin"))) {
			whereAccountSqlString.append(" and Q.CREATE_DATE >= ?");
			objList.add(DateUtils.parseStandardDate((String) param
					.get("opearteDateBegin")));
		}

		if (!StringUtils.isEmpty(param.get("opearteDateEnd"))) {
			whereAccountSqlString.append(" and Q.CREATE_DATE <= ?");
			objList.add(DateUtils.getEndDate(DateUtils
					.parseStandardDate((String) param.get("opearteDateEnd"))));
		}

		if (!StringUtils.isEmpty(param.get("tradeType"))) {
			whereAccountSqlString.append(" and Q.TRADE_TYPE = ?");
			objList.add(param.get("tradeType"));
		} else {
			whereAccountSqlString.append(" and Q.TRADE_TYPE NOT IN (?, ?) ");
			objList.add(SubjectConstant.TRADE_FLOW_TYPE_FREEZE);
			objList.add(SubjectConstant.TRADE_FLOW_TYPE_UNFREEZE);
		}

		StringBuffer whereSqlString = new StringBuffer();
		if (!StringUtils.isEmpty(whereCustSqlString.toString())) {
			whereSqlString.append(String.format(" %s",
					whereCustSqlString.toString()));
		}

		if (!StringUtils.isEmpty(whereAccountSqlString.toString())) {
			whereSqlString.append(String.format(" %s",
					whereAccountSqlString.toString()));
		}

		return repositoryUtil.queryForMap(
				String.format(sqlString.toString(), whereSqlString.toString()),
				objList.toArray());
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo saveBatchWithdrawCashAudit(Map<String, Object> paramsMap)
			throws SLException {
		List<String> auditList = (List<String>) paramsMap.get("auditList");
		String auditStatus = (String) paramsMap.get("auditStatus");
		String memo = (String) paramsMap.get("memo");
		String custId = (String) paramsMap.get("custId");
		String ipAddress = (String) paramsMap.get("ipAddress");

		if (auditList == null || auditList.size() == 0) {
			return new ResultVo(false, "审核ID不能为空");
		}

		int success = 0, failed = 0;
		for (String id : auditList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", id);
			map.put("auditStatus", auditStatus);
			map.put("memo", memo);
			map.put("custId", custId);
			map.put("ipAddress", ipAddress);

			ResultVo resultVo = new ResultVo(false);
			try {
				resultVo = withDrawAuditService.saveWithdrawCashAudit(map);
			} catch (SLException ex) {
				log.error(String.format("提现申请审核失败！审核ID：%s，异常信息：%s", id,
						ex.getMessage()));
			}
			if (ResultVo.isSuccess(resultVo)) {
				success++;
			} else {
				failed++;
			}
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", success);
		result.put("failed", failed);

		return new ResultVo(true, "审核成功", result);
	}

	@Override
	public ResultVo mendRecharge(Map<String, Object> param) throws SLException {
		
		TradeFlowInfoEntity tradeFlowInfoEntity = tradeFlowInfoRepository.findOne((String)param.get("tradeFlowId"));
		if(tradeFlowInfoEntity == null) {
			return new ResultVo(false, "该笔充值不存在");
		}
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("tradeCode", tradeFlowInfoEntity.getTradeNo());
		paramsMap.put("tradeDate", DateUtils.formatDate(tradeFlowInfoEntity.getTradeDate(), "yyyyMMddHHmmss"));
		ResultVo resultVo = thirdPartyPayService.queryRecharge(paramsMap);
		
		if(ResultVo.isSuccess(resultVo)) {
			String retCode = resultVo.getValue("data").toString();
			if(Constant.TRADE_STATUS_03.equals(retCode)) { // 处理成功
				accountFlowService.callbackRechargeSuccess(paramsMap);
			}
			else if(Constant.TRADE_STATUS_04.equals(retCode)) { // 处理失败
				accountFlowService.callbackRechargeFailed(paramsMap);
			}
		}
		
		return resultVo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo mendWithdrawCash(Map<String, Object> param)
			throws SLException {
		
		TradeFlowInfoEntity tradeFlowInfoEntity = tradeFlowInfoRepository.findOne((String)param.get("tradeFlowId"));
		if(tradeFlowInfoEntity == null) {
			return new ResultVo(false, "该笔提现不存在");
		}
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("tradeNo", tradeFlowInfoEntity.getTradeNo());
		paramsMap.put("tradeDate", tradeFlowInfoEntity.getTradeDate());
		
		ResultVo resultVo = thirdPartyPayService.queryWithdrawCash(paramsMap);
		
		if(ResultVo.isSuccess(resultVo)) {
			String retCode = resultVo.getValue("data").toString();
			if(Constant.TRADE_STATUS_03.equals(retCode)) { // 处理成功
				paramsMap.put("status", Constant.TRADE_STATUS_03);
				paramsMap.put("auditStatus", Constant.AUDIT_STATUS_PASS);
				paramsMap.put("tradeStatus", Constant.TRADE_STATUS_03);
				resultVo = withDrawAuditService.callbackWithdrawCash(paramsMap);
			}
			else if(Constant.TRADE_STATUS_04.equals(retCode)) { // 处理失败
				paramsMap.put("status", Constant.TRADE_STATUS_04);
				paramsMap.put("auditStatus", Constant.AUDIT_STATUS_PASS);
				paramsMap.put("tradeStatus", Constant.TRADE_STATUS_04);
				resultVo = withDrawAuditService.callbackWithdrawCashFailed(paramsMap);
			}
			
			if(ResultVo.isSuccess(resultVo) && null != resultVo.getValue("data")){
				try {
					smsService.asnySendSMS((Map<String,Object>)resultVo.getValue("data"));
				} catch (Exception e) {
					log.error(String.format("%,发送邮件信息失败,%",resultVo.getValue("data") , e.getMessage() != null ? e.getMessage() : e.getCause()));
				}
			}
		}

		return resultVo;
	}

	@Override
	public ResultVo findAccountByCustId(Map<String, Object> param) {
		if(StringUtils.isEmpty((String)param.get("custId"))){
			return new ResultVo(false, "客户ID不能为空");
		}
		AccountInfoEntity aie = this.accountInfoRepository.findByCustId((String)param.get("custId"));
		if (aie == null) {
			return new ResultVo(false, "没有数据！");
		}
		return new ResultVo(true, "查询账户成功", aie);
	}

	@Override
	public ResultVo queryAccountNew(Map<String, Object> param) {
		String custId = (String) param.get("custId");
		//基本信息
		Map<String, Object> resultMap = accountInfoRepositoryCustom.findAccountInfoByCustId(param);
		//企业借款待收收益
		BigDecimal projExceptRepayAmount = investInfoRepository.queryProjectExceptAmountByCustId(custId);
		//企业借款 :项目状态为 发布中 和满标复合的在投金额
		BigDecimal investAmount = investInfoRepository.queryProjectInvestAmountByCustId(custId);
		//企业借款待收本金
		BigDecimal projExceptRepayPrincipal = ArithUtil.add(investAmount, 
				investInfoRepository.queryProjectRemainderPrincipal(custId));
		/**体验金**/
		Map<String, Object> loanExperience = loanInfoRepositoryCustom.queryEarnAndExceptTotalExperience(param);
		//体验金待收收益
		BigDecimal exceptTotalExperience = new BigDecimal(loanExperience.get("exceptTotalExperience").toString());
		//体验金累计收益
		BigDecimal earnTotalExperience = new BigDecimal(loanExperience.get("earnTotalExperience").toString());
		
		//优选计划待收收益
		BigDecimal planExceptPayInterest = wealthPaymentPlanInfoRepository.
				findExceptPaymentInterestByCustId(custId, Constant.WEALTH_PAYMENT_PLAN_STATUS_NOT);
		//优选计划已收收益
		BigDecimal planEarnPayInterest = wealthPaymentPlanInfoRepository.
				findExceptPaymentInterestByCustId(custId, Constant.WEALTH_PAYMENT_PLAN_STATUS_ALREADY);
		//优选计划待收奖励
		BigDecimal planExceptPaymentAward = wealthPaymentPlanInfoRepository.
				findExceptPaymentAwardByCustId(custId, Constant.WEALTH_PAYMENT_PLAN_STATUS_NOT);
		//优选计划已收奖励
		BigDecimal planEarnPaymentAward = wealthPaymentPlanInfoRepository.
				findExceptPaymentAwardByCustId(custId, Constant.WEALTH_PAYMENT_PLAN_STATUS_ALREADY);
		//优选计划投资状态为投资中的的在投金额
		BigDecimal planInvestAmount = investInfoRepository.queryPlanInvestAmountByCustId(custId, Constant.INVEST_STATUS_INVESTING);
		//优选计划待收本金
		BigDecimal planExceptRepayPrincipal = ArithUtil.add(planInvestAmount, wealthPaymentPlanInfoRepository.
				findExceptPaymentPrincipalByCustId(custId, Constant.WEALTH_PAYMENT_PLAN_STATUS_NOT));
		//优选计划已收本金
		BigDecimal planEarnPrincipal = wealthPaymentPlanInfoRepository.findExceptPaymentPrincipalByCustId(custId, Constant.WEALTH_PAYMENT_PLAN_STATUS_ALREADY);
		
		/** add at 2016-12-06 zhiwen_feng 累计收益+=散表收益 */
		Map<String, Object> loanAmount = null;
		String onlineTime = paramService.findTransferRefromOnlineTimeByParameterName(Constant.TRANSFERRE_FROM_ONLINE_TIME_20170531);
		param.put("onlineTime", onlineTime);
		try{
			loanAmount = loanInfoRepositoryCustom.queryMyDisperseIncome(param);
			
		}catch(SLException e) {
			e.printStackTrace();
		}
		Map<String, Object> loanPrincipal = loanInfoRepositoryCustom.queryEarnTotalPrincipal(param);
		//散标代收收益
		BigDecimal loanExceptPayInterest = new BigDecimal(loanAmount.get("exceptTotalAmount").toString());
		//散标再投金额（投资中）
		BigDecimal loanNotStatyInvestAmount = new BigDecimal(loanAmount.get("notStatyInvestAmount").toString());
		//散表待收本金（收益中）
		BigDecimal loanExceptTotalPrincipal = new BigDecimal(loanAmount.get("exceptTotalPrincipal").toString());
		//散标待收本金 
		BigDecimal loanExceptRepayPrincipal = ArithUtil.add(loanNotStatyInvestAmount, loanExceptTotalPrincipal);
		//散标已回收本金
		BigDecimal loanEarnPrincipal = new BigDecimal(loanPrincipal.get("earnTotalPrincipal").toString());
		//散标已收收益
		BigDecimal loanEarnTotalAmount = new BigDecimal(loanPrincipal.get("earnTotalAmount").toString());
		//橙信贷待收奖励
		BigDecimal exceptTotalAward = new BigDecimal(loanPrincipal.get("exceptTotalAward").toString());
		//橙信贷已收奖励
		BigDecimal earnTotalAward = new BigDecimal(loanPrincipal.get("earnTotalAward").toString());
		//加息券待收奖励
		BigDecimal exceptTotalIncreaseInterest = new BigDecimal(loanPrincipal.get("exceptTotalIncreaseInterest").toString());
		//加息券已收奖励
		BigDecimal earnTotalIncreaseInterest = new BigDecimal(loanPrincipal.get("earnTotalIncreaseInterest").toString());
		
		
		//用户待收本金
		BigDecimal exceptRepayPrincipal = ArithUtil.add(loanExceptRepayPrincipal, ArithUtil.add(projExceptRepayPrincipal, planExceptRepayPrincipal));
		//用户待收收益
		BigDecimal exceptRepayAmount = ArithUtil.add(loanExceptPayInterest, ArithUtil.add(projExceptRepayAmount,
				ArithUtil.add(planExceptPayInterest,ArithUtil.add(exceptTotalExperience ,ArithUtil.add(planExceptPaymentAward,ArithUtil.add(exceptTotalAward,exceptTotalIncreaseInterest)))))) ;
		//账户价值 = 可用价值 +　冻结资金
		BigDecimal accountAmount = ArithUtil.add((BigDecimal) resultMap.get("accountFreezeAmount"), (BigDecimal) resultMap.get("accountAvailableAmount"));
		//账户总值
		resultMap.put("accountTotalAmount", ArithUtil.add(accountAmount, ArithUtil.add(exceptRepayPrincipal, ArithUtil.add(exceptRepayAmount,(BigDecimal)resultMap.get("accountActivityAmount")))));
		//用户总已收本金
		BigDecimal earnTotalPrincipal =  ArithUtil.add(loanEarnPrincipal,planEarnPrincipal);
		//用户累计收益
		BigDecimal earnTotalPayInterest = ArithUtil.add(loanEarnTotalAmount,ArithUtil.add(planEarnPayInterest,ArithUtil.add(planEarnPaymentAward,ArithUtil.add(earnTotalExperience,ArithUtil.add(earnTotalAward,earnTotalIncreaseInterest)))));
		
		//是否有消息未读
		String isRead = "否";
		//个人未读信息
		int count = systemMessageInfoRepository.unreadMessageCount(custId);
		//网站公告
		Map<String, Object> afficheMap = Maps.newHashMap();
		try {
			afficheMap = afficheInfoService.countAffiche(param);
		} catch (SLException e) {
			log.error("查询网站公告未读信息异常");
			e.printStackTrace();
		}
		int unReadAfficheNum = (int) afficheMap.get("unReadAfficheNum");
		if(count + unReadAfficheNum > 0){
			isRead = "是";
		}
		BigDecimal  investCount=investInfoRepository.investCountInfoByCustId(custId);
		if(investCount.compareTo(BigDecimal.ZERO)>0){
			resultMap.put("isNewerFlag", "1");//isNewerFlag：  是否投资过
		}else{
			resultMap.put("isNewerFlag", "2");
		}

		CustInfoEntity cust = custInfoRepository.findOne(custId);
		AutoInvestInfoEntity autoInvest = autoInvestInfoRepository.findByCustId(custId);
		AutoTransferInfoEntity autoTransfer = autoTransferInfoRepository.findByCustId(custId);
		if(autoInvest == null){
			resultMap.put("investOpenStatus", "");
			resultMap.put("investPointStatus", "");
		} else {
			resultMap.put("investOpenStatus", autoInvest.getOpenStatus());
			resultMap.put("investPointStatus", autoInvest.getPointStatus()==null?"Y":autoInvest.getPointStatus());
		}
		if(null == autoTransfer){
			resultMap.put("transferOpenStatus", "");
			resultMap.put("transferPointStatus", "");
		}else{
			resultMap.put("transferOpenStatus", autoTransfer.getOpenStatus());
			resultMap.put("transferPointStatus", autoTransfer.getPointStatus()==null?"Y":autoTransfer.getPointStatus());
		}
		resultMap.put("riskAssessment", cust.getRiskAssessment());
		resultMap.put("exceptRepayAmount", exceptRepayAmount);
		resultMap.put("exceptRepayPrincipal", exceptRepayPrincipal);
		resultMap.put("earnTotalPrincipal", earnTotalPrincipal);
		resultMap.put("earnTotalPayInterest", earnTotalPayInterest);
		resultMap.put("isRead", isRead);
		
		/*// add @2016/5/31 by liyy
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity != null && !Constant.IS_RECOMMEND_YES.equals(custInfoEntity.getIsEmployee())) {
			ResultVo vo = bankCardService.queryCanShowCustBank(param);
			resultMap.put("isShow", ((Map)vo.getValue("data")).get("isShow"));
		}
		else {
			resultMap.put("isShow", "否");
		}*/
		// 不允许展示我是理财师菜单
		resultMap.put("isShow", "否");
		return new ResultVo(true, "查询账户成功", resultMap);
	}
	
	@Override
	public ResultVo grantAccount(Map<String, Object> param) throws SLException {
		// a.检查数据是否正确
		ResultVo checkResultVo = accountInnerClass.grantAccountCheck(param);
		if(!ResultVo.isSuccess(checkResultVo)){
			return checkResultVo;
		}
		// 流水号
		String tradeCode = numberService.generateTradeNumber();
		String requestNo = numberService.generateTradeBatchNumber(); 
		// b.插入流水
		try {
			accountInnerClass.grantAccountPre(param, tradeCode, requestNo);
		} catch (Exception e) {
			return new ResultVo(false, "更新账户失败", "代付失败");
		}
		
		String custId = (String)param.get("custId");
		String userId = (String)param.get("userId");
		BigDecimal tradeAmount = new BigDecimal(param.get("tradeAmount").toString());
		
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		AccountInfoEntity accountMain = accountInfoRepository.findByCustId(custId);
		Map<String, Object> bankRequest = Maps.newHashMap();
		bankRequest.put("custId", custId);
		if(!StringUtils.isEmpty((String)param.get("cardNo"))) {
			bankRequest.put("cardNo", (String)param.get("cardNo"));
		}
		Map<String, Object> bankResponse = bankCardService.queryBankCard(bankRequest);
		
		// c 调用第三方接口完成代付
		// 9) 调用第三方接口完成代付
		Map<String, Object> thirdPartyPayParams = Maps.newHashMap();
		thirdPartyPayParams.put("tradeAmount", tradeAmount);
		thirdPartyPayParams.put("bankCardNo", (String)bankResponse.get("cardNo"));
		thirdPartyPayParams.put("accountName", custInfoEntity.getCustName());
		thirdPartyPayParams.put("tradeDesc", Constant.OPERATION_TYPE_78);
		thirdPartyPayParams.put("openAccountProv", (String)bankResponse.get("openProvince"));
		thirdPartyPayParams.put("openAccountCity", (String)bankResponse.get("openCity"));
		thirdPartyPayParams.put("subBranchName", (String)bankResponse.get("subBranchName"));
		thirdPartyPayParams.put("bankCode", (String)bankResponse.get("bankCode"));
		thirdPartyPayParams.put("tradeCode", tradeCode);
		thirdPartyPayParams.put("accountProperties", Constant.ACCOUNT_TYPE_02.equals(accountMain.getAccountType()) ? Constant.ACCOUNT_TYPE_02 : Constant.ACCOUNT_TYPE_01);
		ResultVo resultVo = null;
		try {
			 resultVo = thirdPartyPayService.trustWithdrawCash(thirdPartyPayParams);
//			resultVo = new ResultVo(true);
		} catch (Exception e) {
			log.error("提现审核调用TPP出现异常:" + e.toString());
			// 审核状态和过程流水状态均改为处理中
			return accountInnerClass.grantAccountThirdEx(userId, tradeCode);
		}
		
		// d 成功后处理
		ResultVo postResultVo = accountInnerClass.grantAccountPost(param, tradeCode, requestNo, resultVo);
		
		return postResultVo;
	}

	@Override
	public ResultVo queryCompanyAccount(Map<String, Object> param)
			throws SLException {
		
		return thirdPartyPayService.queryAccountAmount(param);
	}
	
	@Service
	static class AccountInnerClass {
		
		@Autowired
		AccountInfoRepository accountInfoRepository;
		
		@Autowired
		CustInfoRepository custInfoRepository;
		
		@Autowired
		BankCardService bankCardService;
		
		@Autowired
		ThirdPartyPayService thirdPartyPayService;
		
		@Autowired
		CustAccountService custAccountService;
		
		@Autowired
		AuditInfoRespository auditInfoRespository;
		
		@Autowired
		TradeFlowInfoRepository tradeFlowInfoRepository;
		
		@Autowired
		LogInfoEntityRepository logInfoEntityRepository;
		
		public ResultVo grantAccountCheck(Map<String, Object> param) throws SLException{
			String custId = (String)param.get("custId");
			BigDecimal tradeAmount = new BigDecimal(param.get("tradeAmount").toString());
			
			// 1) 验证用户状态
			CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
			if (custInfoEntity == null) {
				return new ResultVo(false, "客户不存在!", "代付失败");
			}

			// 2) 验证账户是否存在
			AccountInfoEntity accountMain = accountInfoRepository.findByCustId(custId);
			if(accountMain == null) {
				return new ResultVo(false, "未查询到账户信息", "代付失败");
			}
			
			// 3) 验证商户银行卡是否绑定
			Map<String, Object> bankRequest = Maps.newHashMap();
			bankRequest.put("custId", custId);
			Map<String, Object> bankResponse = bankCardService.queryBankCard(bankRequest);
			if(bankResponse == null || !bankResponse.containsKey("id")) {
				return new ResultVo(false, "未查询到银行卡信息", "代付失败");
			}
			
			// 4) 验证公司账户资金是否足额
			ResultVo companyAccountResponse = thirdPartyPayService.queryAccountAmount(bankRequest);
			if(!ResultVo.isSuccess(companyAccountResponse)) {
				return new ResultVo(false, "查询连连支付公司账户余额失败", "代付失败");
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> companyAccountMap = (Map<String, Object>)companyAccountResponse.getValue("data");
			if(new BigDecimal(companyAccountMap.get("amtBalance").toString()).compareTo(tradeAmount) < 0) {
				return new ResultVo(false, String.format("连连支付公司账户余额%s小于放款金额%s", companyAccountMap.get("amtBalance").toString(), tradeAmount.toPlainString()), "代付失败");
			}
			return new ResultVo(true);
		}
		
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo grantAccountPre(Map<String, Object> param, String tradeCode, String requestNo) throws SLException{
			String custId = (String)param.get("custId");
			String userId = (String)param.get("userId");
			BigDecimal tradeAmount = new BigDecimal(param.get("tradeAmount").toString());
			String relateType = (String)param.get("relateType");
			String relatePrimary = (String)param.get("relatePrimary");
			String auditMemo = (String)param.get("auditMemo");
			
			CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
			AccountInfoEntity accountMain = accountInfoRepository.findByCustId(custId);
			Map<String, Object> bankRequest = Maps.newHashMap();
			bankRequest.put("custId", custId);
			Map<String, Object> bankResponse = bankCardService.queryBankCard(bankRequest);
			
			// 5) 插入冻结流水
			List<AccountFlowInfoEntity> freezeFlowList = custAccountService.updateAccount(accountMain, null, null, null, "7", 
					SubjectConstant.TRADE_FLOW_TYPE_FREEZE, requestNo, tradeAmount, 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW_FREEZE, relateType, relatePrimary, "代付冻结", userId);
			freezeFlowList.get(0).setTradeNo(tradeCode);
			
			// 6) 插入提现审核信息
			AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
			auditInfoEntity.setCustId(custId);
			if(!StringUtils.isEmpty(relateType)) {
				auditInfoEntity.setRelateType(relateType);
				auditInfoEntity.setRelatePrimary(relatePrimary);
			}
			else {
				auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_ACCOUNT_INFO);
				auditInfoEntity.setRelatePrimary(accountMain.getId());
			}
			auditInfoEntity.setApplyType(SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW);
			auditInfoEntity.setApplyTime(new Timestamp(System.currentTimeMillis()));
			auditInfoEntity.setTradeAmount(tradeAmount);
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_01);
			auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_PASS);
			auditInfoEntity.setBasicModelProperty(userId, true);
			auditInfoEntity.setMemo(auditMemo);
			auditInfoEntity = auditInfoRespository.save(auditInfoEntity);

			// 7) 插入提现流水过程记录
			TradeFlowInfoEntity flow = new TradeFlowInfoEntity();
			flow.setCustId(custId);
			flow.setCustAccountId(accountMain.getId());
			flow.setRelateType(Constant.TABLE_BAO_T_AUDIT_INFO);
			flow.setRelatePrimary(auditInfoEntity.getId());
			flow.setTradeAmount(tradeAmount);
			flow.setTradeExpenses(BigDecimal.ZERO);//提现手续费设置为0表示由收益账户出
			flow.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW);
			flow.setBankName((String)bankResponse.get("bankName"));
			flow.setBranchBankName((String)bankResponse.get("subBranchName"));
			flow.setBankCardNo((String)bankResponse.get("cardNo"));
			flow.setTradeNo(freezeFlowList.get(0).getTradeNo());
			flow.setTradeStatus(Constant.TRADE_STATUS_01);
			flow.setTradeDate(new Date());
			flow.setTradeDesc(Constant.OPERATION_TYPE_78);
			flow.setTradeSource(Constant.INVEST_SOURCE_PC);
			flow.setBasicModelProperty(userId, true);
			flow = tradeFlowInfoRepository.save(flow);
			
			// 8) 插入日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
			logInfoEntity.setRelatePrimary(flow.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_78);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperIpaddress("");
			logInfoEntity.setOperPerson(custId);
			logInfoEntity.setMemo(String.format("%s代付申请金额%s，手续费0",
					custInfoEntity.getLoginName(), tradeAmount.toPlainString()));
			logInfoEntity.setBasicModelProperty(custId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			return new ResultVo(true);
		}
		
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo grantAccountPost(Map<String, Object> param, String tradeCode, String requestNo, ResultVo resultVo) throws SLException{
			String custId = (String)param.get("custId");
			String userId = (String)param.get("userId");
			BigDecimal tradeAmount = new BigDecimal(param.get("tradeAmount").toString());
			String relateType = (String)param.get("relateType");
			String relatePrimary = (String)param.get("relatePrimary");
			
			CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
			AccountInfoEntity accountMain = accountInfoRepository.findByCustId(custId);
			TradeFlowInfoEntity flow = tradeFlowInfoRepository.findByTradeNo(tradeCode);
			AuditInfoEntity auditInfoEntity = auditInfoRespository.findOne(flow.getRelatePrimary());
			
			// 10) 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
			logInfoEntity.setRelatePrimary(flow.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_78);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperIpaddress("");
			logInfoEntity.setOperPerson(custId);
			logInfoEntity.setMemo(String.format("%s代付%s",
					custInfoEntity.getLoginName(), flow.getTradeStatus()));
			logInfoEntity.setBasicModelProperty(custId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			// 11) 若第三方接口调用失败，则解冻原金额
			if (!ResultVo.isSuccess(resultVo)) {
				// 调用失败，解冻账户
				List<AccountFlowInfoEntity> unFreezeFlowList = custAccountService.updateAccount(accountMain, null, null, null, "8", 
						SubjectConstant.TRADE_FLOW_TYPE_UNFREEZE, requestNo, tradeAmount, 
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW, relateType, relatePrimary, "代付解冻", userId);
				unFreezeFlowList.get(0).setOldTradeNo(tradeCode); // 解冻流水的原流水号为冻结流水
				flow.setTradeStatus(Constant.TRADE_STATUS_04);
				flow.setBasicModelProperty(userId, false);
				auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_04);
				auditInfoEntity.setAuditUser(userId);
				auditInfoEntity.setAuditTime(new Date());
				auditInfoEntity.setBasicModelProperty(userId, false);
				return new ResultVo(false, resultVo.getValue("message"), "代付失败");
			}
			else {
				flow.setTradeStatus(Constant.TRADE_STATUS_02);
				flow.setBasicModelProperty(userId, false);
				auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_02);
				auditInfoEntity.setAuditUser(userId);
				auditInfoEntity.setAuditTime(new Date());
				auditInfoEntity.setBasicModelProperty(userId, false);
				return new ResultVo(true, "代付成功");
			}
		}
		
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo grantAccountThirdEx(String userId, String tradeCode) throws SLException{
			TradeFlowInfoEntity flow = tradeFlowInfoRepository.findByTradeNo(tradeCode);
			AuditInfoEntity auditInfoEntity = auditInfoRespository.findOne(flow.getRelatePrimary());
			
			// 审核状态和过程流水状态均改为处理中
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_02);
			auditInfoEntity.setMemo("提现调用TPP发生异常");
			auditInfoEntity.setAuditUser(userId);
			auditInfoEntity.setAuditTime(new Date());
			auditInfoEntity.setBasicModelProperty(userId, false);
			flow.setTradeStatus(Constant.TRADE_STATUS_02);
			flow.setBasicModelProperty(userId, false);
			return new ResultVo(false, "提现审核调用TPP出现异常!");
		}
	}
}
