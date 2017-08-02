/** 
 * @(#)AccountFlowServiceImpl.java 1.0.0 2015年4月25日 下午6:19:21  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.entity.BankCardInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.ActivityInfoRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRepository;
import com.slfinance.shanlincaifu.repository.BankCardInfoRepository;
import com.slfinance.shanlincaifu.repository.CustActivityDetailEntityRepository;
import com.slfinance.shanlincaifu.repository.CustActivityInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.DeviceInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.TradeFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.AccountFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.BankCardInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.CustActivityInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.TradeFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.searchFilter.DynamicSpecifications;
import com.slfinance.shanlincaifu.repository.searchFilter.SearchFilter;
import com.slfinance.shanlincaifu.repository.searchFilter.SearchFilter.Operator;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.CustActivityInfoService;
import com.slfinance.shanlincaifu.service.DeviceService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.OpenService;
import com.slfinance.shanlincaifu.service.OpenThirdService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.thirdpp.util.SharePropertyConstant;
import com.slfinance.vo.ResultVo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**   
 * 账户流水接口实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月25日 下午6:19:21 $ 
 */
@Slf4j
@Service("accountFlowService")
public class AccountFlowServiceImpl implements AccountFlowService {

	@Autowired
	private AccountFlowInfoRepositoryCustom accountFlowInfoRepositoryCustom;
	
	@Autowired
	private TradeFlowInfoRepositoryCustom tradeFlowInfoRepositoryCustom;
	
	@Autowired
	AccountFlowInfoRepository accountFlowInfoRepository;
	
//	@Autowired
//	AccountFlowDetailRepository accountFlowDetailRepository;
//	
//	@Autowired
//	FlowBusiRelationRepository flowBusiRelationRepository;
	
	@Autowired
	TradeFlowInfoRepository tradeFlowInfoRepository;
	
	@Autowired
	AccountInfoRepository accountInfoRepository;
	
	@Autowired
	BankCardInfoRepositoryCustom bankCardInfoRepositoryCustom;

	@Autowired
	CustInfoRepository custInfoRepository;
	
	@Autowired
	LogInfoEntityRepository logInfoEntityRepository;
	
	@Autowired
	BankCardInfoRepository bankCardInfoRepository;
	
	@Autowired
	SubAccountInfoRepository subAccountInfoRepository;
	
	@Autowired
	ParamService paramService;
	
	@Autowired
	FlowNumberService numberService;
	
	@Autowired
	CustAccountService custAccountService;
	
	@Autowired
	private ActivityInfoRepository activityInfoRepository;
	
	@Autowired
	private CustActivityInfoRepository custActivityInfoRepository;
	
	@Autowired
	private CustActivityInfoRepositoryCustom custActivityInfoRepositoryCustom;
	
	@Autowired
	CustActivityDetailEntityRepository custActivityDetailEntityRepository;
	
	@Autowired
	private OpenService openService;
	
	@Autowired
	private DeviceInfoRepository deviceInfoRepository;
	
	@Autowired
	private CustActivityInfoService custActivityInfoService;
	
	@Autowired
	private AuditInfoRepository auditInfoRepository;
	
	@Autowired
	private OpenThirdService openThirdService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Override
	public Map<String, Object> findAllAccountFlowList(Map<String, Object> param) {
		// TODO 参数校验
		
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = accountFlowInfoRepositoryCustom.findAllAccountFlowList(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	@Override
	public Map<String, Object> findAllRechargeSum(Map<String, Object> map) {
		// TODO 参数校验
		
		return tradeFlowInfoRepositoryCustom.findAllRechargeSum(map);
	}

	@Override
	public Map<String, Object> findAllRechargeList(Map<String, Object> map) {
		// TODO 参数校验
		
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = tradeFlowInfoRepositoryCustom.findAllRechargeList(map);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	@Override
	public Map<String, Object> findRechargeDetailInfo(Map<String, Object> map) {
		// TODO 参数校验
		
		return tradeFlowInfoRepositoryCustom.findRechargeDetailInfo((String)map.get("flowId"));
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public void insertRechargeFlowProcessInfo(Map<String, Object> params) throws SLException{
		
		String custId = (String)params.get("custId"); // 客户ID
		String cardNo = (String)params.get("bankCardNo"); // 银行卡号
		String bankNo = (String)params.get("bankNo"); // 银行编号
		String ipAddress = (String)params.get("ipAddress"); // 客户IP地址
		String tradeCode = (String)params.get("tradeCode"); // 交易编号
		String payType = (String)params.get("payType"); // 付款方式
		BigDecimal tradeAmount = new BigDecimal(params.get("tradeAmount").toString());// 交易金额
		String bankName = ""; // 银行名称
		String appSource = (String)params.get("appSource"); // 来源
		
		/** 1)认证支付时，自动绑定银行卡 */
		if(payType.equals(Constant.PAY_TYPE_01)) {
			// 判断银行卡号是否存在，若不存在则新增一条记录
			List<BankCardInfoEntity> bankList = bankCardInfoRepository.findByCardNoOrderByCreateDateDesc(custId, cardNo, Constant.BANK_FLAG_ONLINE);
			if(bankList == null || bankList.size() == 0){ // 系统中未查到该银行卡号则新增一张
				if(StringUtils.isEmpty(bankNo)){
					throw new SLException("该银行卡为新卡，请选择银行类型");
				}
				bankName = bankCardInfoRepositoryCustom.findByBankCode(bankNo);
				BankCardInfoEntity bankCardInfoEntity = new BankCardInfoEntity();
				CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
				bankCardInfoEntity.setCustInfoEntity(custInfoEntity);
				bankCardInfoEntity.setBankName(bankName);
				bankCardInfoEntity.setCardNo(cardNo);
				bankCardInfoEntity.setIsDefault("1");
				bankCardInfoEntity.setBasicModelProperty(custId, true);
				bankCardInfoEntity.setRecordStatus(Constant.VALID_STATUS_INVALID);//设置为无效
				bankCardInfoEntity.setBankFlag(Constant.BANK_FLAG_ONLINE);
				bankCardInfoRepositoryCustom.insertBank(bankCardInfoEntity);
				//bankCardInfoRepository.save(bankCardInfoEntity);
			}
			else {
				bankName = bankList.get(0).getBankName();
				bankNo = bankCardInfoRepositoryCustom.findByBankName(bankName);
				params.put("bankNo", bankNo);
			}
		} 
		else {
			bankName = bankCardInfoRepositoryCustom.findByBankCode(bankNo);
		}
		
		/** 2)查询客户账户信息 */
		AccountInfoEntity cust = accountInfoRepository.findByCustId(custId);
		if (cust == null) {
			throw new SLException("未找到客户账户信息");
		}

		/** 3)插入过程流水 */
		TradeFlowInfoEntity flow = new TradeFlowInfoEntity();
		flow.setCustId(custId);
		flow.setCustAccountId(cust.getId());
		flow.setRelateType(Constant.TABLE_BAO_T_ACCOUNT_INFO);
		flow.setRelatePrimary(cust.getId());
		flow.setTradeAmount(tradeAmount);
		flow.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_RECHARGE);
		flow.setBankCardNo(cardNo);
		flow.setBankName(bankName);
		flow.setTradeNo(tradeCode);
		flow.setTradeStatus(Constant.TRADE_STATUS_02);
		flow.setBasicModelProperty(custId, true);
		flow.setTradeDate(new Date());
		flow.setTradeDesc("");
		flow.setTradeExpenses(new BigDecimal("0"));
		/*flow.setSubTradeType(payType);// 填写认证方式
		flow.setMemo(payType.equals(Constant.PAY_TYPE_01)? "认证充值":"网银充值");*/
		flow.setSubTradeType(payType.equals(Constant.PAY_TYPE_03) ? Constant.PAY_TYPE_02 : payType);// 填写认证方式
		flow.setMemo(payType.equals(Constant.PAY_TYPE_01) ? "认证充值": payType.equals(Constant.PAY_TYPE_02) ? "个人网银" : "公司网银");
		flow.setTradeSource(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource);
		flow = tradeFlowInfoRepository.save(flow);
		
		/** 4)记录日志 */
		CustInfoEntity custEntity = custInfoRepository.findOne(custId);
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
		logInfoEntity.setRelatePrimary(flow.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_05);
		logInfoEntity.setOperDesc(appSource);
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress(ipAddress);
		logInfoEntity.setMemo(String.format("%s充值金额%s,支付方式:%s", custEntity.getLoginName(), tradeAmount.toString(), 
				payType.equals(Constant.PAY_TYPE_01)? "认证支付":"网银支付"));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		params.put("tradeFlowId", flow.getId());
		openService.saveRecharge(params);
		
		// 记录设备信息(充值)
		if(params.containsKey("channelNo")) {
			Map<String, Object> deviceParams = Maps.newConcurrentMap();
			deviceParams.putAll(params);
			deviceParams.put("relateType", Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
			deviceParams.put("relatePrimary", flow.getId());
			deviceParams.put("tradeType", Constant.OPERATION_TYPE_05);
			deviceParams.put("userId", custId);
			deviceService.saveUserDevice(deviceParams);
		}
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public void callbackRechargeSuccess(Map<String, Object> params) throws SLException {
		String tradeNo = (String)params.get("tradeCode");
		TradeFlowInfoEntity flowProcess = tradeFlowInfoRepository.findByTradeNo(tradeNo);
		if (flowProcess == null) {
			log.info("未找到流水号:" + tradeNo);
			throw new SLException("未找到流水号:" + tradeNo);
		}
		if (isTradeFinished(flowProcess)) {
			return;
		}

		/** 1)更新账户过程流水 */
		flowProcess.setTradeStatus(Constant.TRADE_STATUS_03);
		flowProcess.setLastUpdateDate(new Date());

		log.info("客户ID：" + flowProcess.getRelatePrimary());

		/** 2)更新客户账户信息，入充值 */
		// 2.1 更新客户账户
		AccountInfoEntity custAccount = accountInfoRepository.findOne(flowProcess.getRelatePrimary());
		if (custAccount == null) {
			log.info("未找到客户账户信息:" + flowProcess.getRelatePrimary());
			throw new SLException("未找到客户账户信息");
		}
		custAccount.setAccountTotalAmount(ArithUtil.add(custAccount.getAccountTotalAmount(), flowProcess.getTradeAmount()));
		custAccount.setAccountAvailableAmount(ArithUtil.add(custAccount.getAccountAvailableAmount(), flowProcess.getTradeAmount()));
		custAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		
		// 2.2 记录 账户流水信息表	
		AccountFlowInfoEntity accountFlowInfoEntity = saveAccountFlow(custAccount, null, null, null, "1", 
				SubjectConstant.TRADE_FLOW_TYPE_RECHARGE, flowProcess.getTradeNo(), SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				flowProcess.getTradeAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RECHARGE, 
				null, null, SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		/** 3)更新公司账户，出手续费*/
		// 3.1 更新公司收益主账户
		SubAccountInfoEntity earnSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_ERAN); // 公司收益分账户
		if(earnSubAccount == null || earnSubAccount.getAccountAvailableValue() == null) {
			throw new SLException("未初始化公司收益分账户！");
		}
		AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(earnSubAccount.getAccountId()); // 公司收益主账户
		if(earnMainAccount == null) {
			throw new SLException("未初始化公司收益主账户！");
		}
		
		BigDecimal rechargeExpenseAmount = new BigDecimal("0");
		BigDecimal rechargeExpenseScale = new BigDecimal("0");
		BigDecimal rechargeExpenseMinAmount = new BigDecimal("0");
		if(flowProcess.getSubTradeType().equals(Constant.PAY_TYPE_01)) {
			rechargeExpenseScale = paramService.findRechargeAuthPayExpenseScale();
			rechargeExpenseMinAmount = paramService.findRechargeAuthPayExpenseMinAmount();
			rechargeExpenseAmount = ArithUtil.mul(flowProcess.getTradeAmount(), rechargeExpenseScale, 2);
		} 
		else {
			if ("个人网银".equals(flowProcess.getMemo())) {
				rechargeExpenseScale = paramService.findRechargeBankPayExpenseScale();
				rechargeExpenseMinAmount = paramService.findRechargeBankPayExpenseMinAmount();
				rechargeExpenseAmount = ArithUtil.mul(flowProcess.getTradeAmount(), rechargeExpenseScale, 2);
			} else if ("公司网银".equals(flowProcess.getMemo())) {
				rechargeExpenseAmount = paramService.findCompanyRechargeBankPayExpense();
			}
		}
		
		if(rechargeExpenseAmount.compareTo(rechargeExpenseMinAmount) < 0) {
			rechargeExpenseAmount = rechargeExpenseMinAmount;
		}
		
		earnMainAccount.setAccountTotalAmount(ArithUtil.sub(earnMainAccount.getAccountTotalAmount(), rechargeExpenseAmount));
		earnMainAccount.setAccountAvailableAmount(ArithUtil.sub(earnMainAccount.getAccountAvailableAmount(), rechargeExpenseAmount));
		earnMainAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		
		// 3.2 记录 账户流水信息表		
		AccountFlowInfoEntity earnAccountFlowInfoEntity = saveAccountFlow(earnMainAccount, null, null, null, "1", 
				SubjectConstant.TRADE_FLOW_TYPE_RECHARGE_EXPENSE, flowProcess.getTradeNo(), SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				rechargeExpenseAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RECHARGE_EXPENSE, 
				null, null, SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 将充值流水与手续费流水的交易请求设置为同一个批次号
		accountFlowInfoEntity.setRequestNo(numberService.generateTradeBatchNumber());
		earnAccountFlowInfoEntity.setRequestNo(accountFlowInfoEntity.getRequestNo());
		
		/** 4)认证支付更新对应银行卡号*/ 
		if(flowProcess.getSubTradeType().equals(Constant.PAY_TYPE_01)) {
			List<BankCardInfoEntity> bankList = bankCardInfoRepository.findByCardNoOrderByCreateDateDesc(custAccount.getCustId(), flowProcess.getBankCardNo(), Constant.BANK_FLAG_ONLINE);
			if(bankList != null && bankList.size() > 0){
				BankCardInfoEntity bankCardInfoEntity = bankList.get(0);
				//修改客户类型 为 线上
				if(bankCardInfoEntity.getCustInfoEntity() == null){
					throw new SLException("银行卡没有关联的客户！");
				}
				CustInfoEntity custInfoEntity = custInfoRepository.findOne(bankCardInfoEntity.getCustInfoEntity().getId());
				if(custInfoEntity == null){
					throw new SLException("银行卡关联的客户不存在！");
				}
				custInfoEntity.setCustType(Constant.CUST_TYPE_ONLINE);
				custInfoEntity.setBasicModelProperty(flowProcess.getCreateUser(), false);
				
				if(Constant.VALID_STATUS_INVALID.equals(bankCardInfoEntity.getRecordStatus())){
					bankCardInfoEntity.setBasicModelProperty(flowProcess.getCreateUser(), false);
					bankCardInfoEntity.setRecordStatus(Constant.VALID_STATUS_VALID);
					bankCardInfoEntity.setProtocolNo((String)params.get("noAgree"));
					
					/** 4)记录日志 */
					List<LogInfoEntity> flowLogList = logInfoEntityRepository.findLogInfoEntityByRelatePrimary(flowProcess.getId());
					LogInfoEntity logInfoEntity = new LogInfoEntity();
					logInfoEntity.setRelateType(Constant.TABLE_BAO_T_BANK_CARD_INFO);
					logInfoEntity.setRelatePrimary(bankCardInfoEntity.getId());
					logInfoEntity.setLogType(Constant.OPERATION_TYPE_07);
					logInfoEntity.setOperDesc("");
					logInfoEntity.setOperPerson(custAccount.getCustId());
					if(flowLogList != null && flowLogList.size() != 0){
						logInfoEntity.setOperIpaddress(flowLogList.get(0).getOperIpaddress());
					}
					logInfoEntity.setMemo(String.format("认证支付绑定银行卡%s", replaceSpecialWord(bankCardInfoEntity.getCardNo())));
					logInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
					logInfoEntityRepository.save(logInfoEntity);
					
					// 判断用户是否是借款用户，是的话则需通知商务
					Map<String, Object> requestParam = Maps.newConcurrentMap();
					requestParam.put("custId", custInfoEntity.getId());
					requestParam.put("bankId", bankCardInfoEntity.getId());
					openThirdService.bindBankNotify(requestParam);
				}
				else if(StringUtils.isEmpty(bankCardInfoEntity.getProtocolNo()) && !StringUtils.isEmpty((String)params.get("noAgree"))) {
					bankCardInfoEntity.setProtocolNo((String)params.get("noAgree"));
				}
			}
		}
		
		// 10、生成推荐奖励
		CustInfoEntity  custInfoEntity = custInfoRepository.findOne(custAccount.getCustId());
		if(StringUtils.isEmpty(custInfoEntity.getInviteOriginId()) // 用户受邀邀请码不为空时才需计算推荐奖励
				|| StringUtils.isEmpty(custInfoEntity.getSpreadLevel()) // 推广级别不能为空
				|| custInfoEntity.getSpreadLevel().equals("0")){          // 并且推广级别非0
			return;
		}

		// 活动2：奖励现金
		Map<String, Object> custActivityMap = new HashMap<String, Object>();
		custActivityMap.put("activityId", Constant.ACTIVITY_ID_REGIST_02);
		custActivityMap.put("custInfoEntity", custInfoEntity);
		custActivityMap.put("tradeNo", tradeNo);
		custActivityInfoService.custActivityRecommend(custActivityMap);
		
		// 活动3：奖励体验金
		custActivityMap.put("activityId", Constant.ACTIVITY_ID_REGIST_03);
		custActivityMap.put("custInfoEntity", custInfoEntity);
		custActivityMap.put("custAccount", custAccount);
		custActivityMap.put("tradeNo", tradeNo);
		custActivityInfoService.custActivityRecommend(custActivityMap);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public void callbackRechargeFailed(Map<String, Object> params) throws SLException {
		String tradeNo = (String)params.get("tradeCode");
		TradeFlowInfoEntity flowProcess = tradeFlowInfoRepository.findByTradeNo(tradeNo);
		if (isTradeFinished(flowProcess)) {
			return;
		}

		/** 1)更新账户过程流水 */
		flowProcess.setTradeStatus(Constant.TRADE_STATUS_04);
		flowProcess.setLastUpdateDate(new Date());
		tradeFlowInfoRepository.save(flowProcess);
		
	}
	
	/**
	 * 判断交易是否完成
	 *
	 * @author  wangjf
	 * @date    2015年4月28日 上午11:50:05
	 * @param flowProcess
	 * @return
	 */
	private boolean isTradeFinished(TradeFlowInfoEntity flowProcess) {
		return Constant.TRADE_STATUS_03.equals(flowProcess.getTradeStatus()) || Constant.TRADE_STATUS_04.equals(flowProcess.getTradeStatus());
	}
	
	/**
	 * 替换敏感字符
	 *
	 * @author  wangjf
	 * @date    2015年6月3日 下午1:40:10
	 * @param words
	 * @return
	 */
	private String replaceSpecialWord(String words){
		if(StringUtils.isEmpty(words)){
			return "";
		}
		
		if(words.length() <= 8) return words;
		
		return words.substring(0, 4) + "***" + words.substring(words.length() - 4, words.length());
	}
	
	@Override
	public Page<AccountFlowInfoEntity> findAccountFlowInfoPagable(Map<String, Object> param) {
		int pageNumber = 0;
		int pageSize = 2;
		if(!StringUtils.isEmpty(param.get("pageNumber")) && !StringUtils.isEmpty(param.get("pageSize"))) {
			pageNumber = Integer.parseInt(param.get("pageNumber")+"");
			pageSize = Integer.parseInt(param.get("pageSize")+"");
		}
		PageRequest pageRequest = new PageRequest(pageNumber, pageSize,
				new Sort(Direction.DESC, "tradeNo"));
		
		List<SearchFilter> filters = Lists.newArrayList();
		
		filters.add(new SearchFilter("accountType", Operator.EQ, Constant.ACCOUNT_TYPE_MAIN));
		
		if (!StringUtils.isEmpty(param.get("custId")))
			filters.add(new SearchFilter("custId", Operator.EQ, param.get("custId")));
		
		if (!StringUtils.isEmpty(param.get("tradeType"))){
//			if (param.get("tradeType").toString().contains("出借")) {
//				param.get("tradeType").toString().replaceAll("出借", "购买");		
//			}
			filters.add(new SearchFilter("tradeType", Operator.EQ,param.get("tradeType")));
		}
		else {
			filters.add(new SearchFilter("tradeType", Operator.NE,
					SubjectConstant.TRADE_FLOW_TYPE_JOIN + "(内部)"));
			filters.add(new SearchFilter("tradeType", Operator.NE,
					SubjectConstant.TRADE_FLOW_TYPE_ATONE + "(内部)"));
			filters.add(new SearchFilter("tradeType", Operator.NE,
					SubjectConstant.TRADE_FLOW_TYPE_ATONE_NORMAL + "(内部)"));
			filters.add(new SearchFilter("tradeType", Operator.NE,
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW_FREEZE));
			filters.add(new SearchFilter("tradeType", Operator.NE,
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW));
			filters.add(new SearchFilter("tradeType", Operator.NE,
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_OFFLINE_WITHDRAW_FREEZE));
			filters.add(new SearchFilter("tradeType", Operator.NE,
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_OFFLINE_UNFREEZE_WITHDRAW));
		}
		
		if (param.get("beginDate") != null) {
			filters.add(new SearchFilter("createDate", Operator.GTE, DateTime.parse(param.get("beginDate")+"").toDate()));
		}
		
		if (param.get("endDate") != null)
			filters.add(new SearchFilter("createDate", Operator.LT, DateTime.parse(param.get("endDate")+"").plusDays(1).toDate()));

		filters.add(new SearchFilter("tradeAmount", Operator.GTE, new BigDecimal("0.01")));
		
		Specification<AccountFlowInfoEntity> spec = DynamicSpecifications.bySearchFilter(
				filters, AccountFlowInfoEntity.class);
		Page<AccountFlowInfoEntity> pageContent = accountFlowInfoRepository.findAll(spec, pageRequest);
		for (AccountFlowInfoEntity content : pageContent) {
			if (Constant.OPERATION_TYPE_81.equals(content.getTradeType()) && Constant.BANKROLL_FLOW_DIRECTION_IN.equals(content.getBankrollFlowDirection())) {
				content.setMemo(Constant.OPERATION_TYPE_82 + content.getMemo().substring(6));
			}
			content.setTradeType(content.getTradeType()!=null?content.getTradeType().replaceAll("购买", "出借"):"");
			content.setMemo(content.getMemo()!=null?content.getMemo().replaceAll("购买", "出借"):"");
		}
		return pageContent;
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public AccountFlowInfoEntity saveAccountFlow(
			AccountInfoEntity sourceMainAccount,
			SubAccountInfoEntity sourceSubAccount,
			AccountInfoEntity destMainAccount,
			SubAccountInfoEntity destSubAccount, String accountType,
			String tradeType, String reqeustNo, String bankrollFlowDirection,
			BigDecimal tradeAmount, String subjectType,
			String flowBusiRelateType, String flowRelatePrimary, String flowType) {
		
		AccountFlowInfoEntity accountFlowInfo = new AccountFlowInfoEntity();
		if(accountType.equals("1") || accountType.equals("2")){ // 总账
			accountFlowInfo.setAccountTotalAmount(sourceMainAccount.getAccountTotalAmount());
			accountFlowInfo.setAccountAvailable(sourceMainAccount.getAccountAvailableAmount());
			accountFlowInfo.setAccountFreezeAmount(sourceMainAccount.getAccountFreezeAmount());
			accountFlowInfo.setCashAmount(new BigDecimal("0"));
			accountFlowInfo.setAccountType(Constant.ACCOUNT_TYPE_MAIN);
			accountFlowInfo.setAccountId(sourceMainAccount.getId());
			accountFlowInfo.setCustId(sourceMainAccount.getCustId());
		}
		else if(accountType.equals("3") || accountType.equals("4")) { // 分账
			accountFlowInfo.setAccountTotalAmount(sourceSubAccount.getAccountTotalValue());
			accountFlowInfo.setAccountAvailable(sourceSubAccount.getAccountAvailableValue());
			accountFlowInfo.setAccountFreezeAmount(sourceSubAccount.getAccountFreezeValue());
			accountFlowInfo.setCashAmount(sourceSubAccount.getAccountAmount());
			accountFlowInfo.setAccountType(Constant.ACCOUNT_TYPE_SUB);
			accountFlowInfo.setAccountId(sourceSubAccount.getId());
			accountFlowInfo.setCustId(sourceSubAccount.getCustId());
		}
		
		accountFlowInfo.setTradeType(tradeType);
		if(accountType.equals("4") 
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_NORMAL_FREEZE)
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_NORMAL_UNFREEZE)
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_JOIN_EXPERIENCE)
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_DIFF ) // 定期宝补差收益 A
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_EXPENSE ) // 提前赎回手续费 A
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_RISK ) // 计提风险金 A
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_MANAGE ) // 账户管理费 A
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_AWARD ) // 到期奖励 A
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM_ADVANCE ) // 提前赎回// A																			
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM_FINISH)   // 到期赎回// A
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM)   // 赎回定期宝
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_INCOME)) { // 定期宝到期收益
			accountFlowInfo.setTradeType(tradeType + "(内部)");
		}
		accountFlowInfo.setRequestNo(reqeustNo);// 请求编号
		if(tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_RECHARGE)
				//|| tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW)
				|| tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_FREEZE)
				|| tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_REALNAME_AUTH)) {
			accountFlowInfo.setTradeNo(reqeustNo);
		}
		else {
			accountFlowInfo.setTradeNo(numberService.generateTradeNumber()); // 交易编号
		}
		accountFlowInfo.setBankrollFlowDirection(bankrollFlowDirection);//资金方向转出
		accountFlowInfo.setTradeAmount(tradeAmount);
		accountFlowInfo.setTradeDate(new Timestamp(System.currentTimeMillis()));
		accountFlowInfo.setFlowType(flowType);
		if(accountType.equals("1") || accountType.equals("3")){ // 总帐
			if(destMainAccount != null)
				accountFlowInfo.setTargetAccount(destMainAccount.getId());
		}
		else if(accountType.equals("2") || accountType.equals("4")){
			if(destSubAccount != null)
				accountFlowInfo.setTargetAccount(destSubAccount.getId());
		}
		if(!StringUtils.isEmpty(flowBusiRelateType) && !StringUtils.isEmpty(flowRelatePrimary))
		{
			accountFlowInfo.setRelateType(flowBusiRelateType);
			accountFlowInfo.setRelatePrimary(flowRelatePrimary);
		}
		accountFlowInfo.setBasicModelProperty(accountFlowInfo.getCustId(), true);
		accountFlowInfo = accountFlowInfoRepository.save(accountFlowInfo);
					
		return accountFlowInfo;
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public AccountFlowInfoEntity saveAccountFlowExt(
			AccountInfoEntity sourceMainAccount,
			SubAccountInfoEntity sourceSubAccount,
			AccountInfoEntity destMainAccount,
			SubAccountInfoEntity destSubAccount, String accountType,
			String tradeType, String reqeustNo, String bankrollFlowDirection,
			BigDecimal tradeAmount, String subjectType,
			String flowBusiRelateType, String flowRelatePrimary, String flowType) {

		AccountFlowInfoEntity accountFlowInfo = new AccountFlowInfoEntity();
		if(accountType.equals("1") || accountType.equals("2")){ // 总账
			if (sourceMainAccount == null) {
				sourceMainAccount = new AccountInfoEntity();
			}
			accountFlowInfo.setAccountTotalAmount(sourceMainAccount.getAccountTotalAmount());
			accountFlowInfo.setAccountAvailable(sourceMainAccount.getAccountAvailableAmount());
			accountFlowInfo.setAccountFreezeAmount(sourceMainAccount.getAccountFreezeAmount());
			accountFlowInfo.setCashAmount(new BigDecimal("0"));
			accountFlowInfo.setAccountType(Constant.ACCOUNT_TYPE_MAIN);
			accountFlowInfo.setAccountId(sourceMainAccount.getId());
			accountFlowInfo.setCustId(sourceMainAccount.getCustId());
		}
		else if(accountType.equals("3") || accountType.equals("4")) { // 分账
			accountFlowInfo.setAccountTotalAmount(sourceSubAccount.getAccountTotalValue());
			accountFlowInfo.setAccountAvailable(sourceSubAccount.getAccountAvailableValue());
			accountFlowInfo.setAccountFreezeAmount(sourceSubAccount.getAccountFreezeValue());
			accountFlowInfo.setCashAmount(sourceSubAccount.getAccountAmount());
			accountFlowInfo.setAccountType(Constant.ACCOUNT_TYPE_SUB);
			accountFlowInfo.setAccountId(sourceSubAccount.getId());
			accountFlowInfo.setCustId(sourceSubAccount.getCustId());
		}

		accountFlowInfo.setTradeType(tradeType);
		if(accountType.equals("4")
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_NORMAL_FREEZE)
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_NORMAL_UNFREEZE)
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_JOIN_EXPERIENCE)
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_DIFF ) // 定期宝补差收益 A
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_EXPENSE ) // 提前赎回手续费 A
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_RISK ) // 计提风险金 A
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_MANAGE ) // 账户管理费 A
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_AWARD ) // 到期奖励 A
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM_ADVANCE ) // 提前赎回// A
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM_FINISH)   // 到期赎回// A
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM)   // 赎回定期宝
				&& !tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_ATONE_INCOME)) { // 定期宝到期收益
			accountFlowInfo.setTradeType(tradeType + "(内部)");
		}
		accountFlowInfo.setRequestNo(reqeustNo);// 请求编号
		if(tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_RECHARGE)
				//|| tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW)
				|| tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_FREEZE)
				|| tradeType.equals(SubjectConstant.TRADE_FLOW_TYPE_REALNAME_AUTH)) {
			accountFlowInfo.setTradeNo(reqeustNo);
		}
		else {
			accountFlowInfo.setTradeNo(numberService.generateTradeNumber()); // 交易编号
		}
		accountFlowInfo.setBankrollFlowDirection(bankrollFlowDirection);//资金方向转出
		accountFlowInfo.setTradeAmount(tradeAmount);
		accountFlowInfo.setTradeDate(new Timestamp(System.currentTimeMillis()));
		accountFlowInfo.setFlowType(flowType);
		if(accountType.equals("1") || accountType.equals("3")){ // 总帐
			if(destMainAccount != null)
				accountFlowInfo.setTargetAccount(destMainAccount.getId());
		}
		else if(accountType.equals("2") || accountType.equals("4")){
			if(destSubAccount != null)
				accountFlowInfo.setTargetAccount(destSubAccount.getId());
		}
		if(!StringUtils.isEmpty(flowBusiRelateType) && !StringUtils.isEmpty(flowRelatePrimary))
		{
			accountFlowInfo.setRelateType(flowBusiRelateType);
			accountFlowInfo.setRelatePrimary(flowRelatePrimary);
		}
		accountFlowInfo.setBasicModelProperty(accountFlowInfo.getCustId(), true);
		accountFlowInfo = accountFlowInfoRepository.save(accountFlowInfo);

		return accountFlowInfo;
	}

	/**
	 * 实名认证之后扣除手续费
	 * 
	 * @author  zhangzs
	 * @date    2015年5月22日 上午17:54:09
	 * @param resultVo 第三方返回信息
	 * @param custInfo 客户信息
	 * @param thirdPartyPayParams 请求第三方参数
	 * @throws SLException 
	 */
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	public void insertRealNameAuthExpense(ResultVo resultVo, CustInfoEntity custInfo,Map<String, Object> thirdPartyPayParams) throws SLException{
		/**在居间人账户中扣除3元、记录账户流水和账户流水详情信息**/
		/**只要请求成功扣除手续费**/
		if( !( ResultVo.isSuccess(resultVo) || ( !ResultVo.isSuccess(resultVo) && null != resultVo.getValue(SharePropertyConstant.REQUEST_IS_SUCCESS) && (boolean)resultVo.getValue(SharePropertyConstant.REQUEST_IS_SUCCESS))) )
			return;
		
		/**查询居间人账户**/
		AccountInfoEntity accountCenter = accountInfoRepository.findOne(Constant.ACCOUNT_ID_ERAN);
		if(null == accountCenter)
			throw new SLException("收益账户不存在");
		
		AccountInfoEntity userAccount = accountInfoRepository.findByCustId(custInfo.getId());
		if(null == userAccount)
			throw new SLException("用户账户不存在");
		
		BigDecimal realNameExpensive = paramService.findRealNameExpenseAmount();
		
		/**组装账户流水信息数据**/
		AccountFlowInfoEntity accountFlow = new AccountFlowInfoEntity(accountCenter.getId(), accountCenter.getCustId(),SubjectConstant.TRADE_FLOW_TYPE_REALNAME_AUTH,(String) thirdPartyPayParams.get("tradeCode"), "",SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT,realNameExpensive, new Date(),SubjectConstant.TRADE_FLOW_TYPE_REALNAME_AUTH,Constant.ACCOUNT_TYPE_MAIN);
		accountFlow.setRequestNo((String) thirdPartyPayParams.get("batchCode"));//请求编号
		accountFlow.setAccountAvailable( ArithUtil.sub(accountCenter.getAccountAvailableAmount(), realNameExpensive) );//减去手续费之后的可用金额
		accountFlow.setAccountFreezeAmount(accountCenter.getAccountFreezeAmount());//减去手续费之后的冻结金额
		accountFlow.setAccountTotalAmount( ArithUtil.sub(accountCenter.getAccountTotalAmount(), realNameExpensive) );//减去手续费之后的总金额
		accountFlow.setFlowType(SubjectConstant.SUBJECT_TYPE_AMOUNT);
		accountFlow.setTargetAccount(userAccount.getId());
		accountFlow.setRelatePrimary(custInfo.getId());
		accountFlow.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
		accountFlow.setBasicModelProperty(custInfo.getId(), true);
		
		/**更新居间人帐户数据信息**/
		AccountInfoEntity accountUpd = new AccountInfoEntity();
		//可用金额减去实名认证手续费
		accountUpd.setAccountAvailableAmount( ArithUtil.sub(accountCenter.getAccountAvailableAmount(), realNameExpensive) );
		//账户总金额减去实名认证手续费
		accountUpd.setAccountTotalAmount( ArithUtil.sub(accountCenter.getAccountTotalAmount(), realNameExpensive) );
		accountUpd.setBasicModelProperty(custInfo.getId(), false);
		if(!accountCenter.updAccountInfo(accountUpd))
			throw new SLException("更新帐户失败");
		
		/**记录帐户流水**/
		accountFlowInfoRepository.save(accountFlow);
		
//		/**记录帐户流水详情**/
//		AccountFlowDetailEntity accountFlowDetail = new AccountFlowDetailEntity();
//		accountFlowDetail.setAccountFlowId(accountFlow.getId());
//		accountFlowDetail.setSubjectType(SubjectConstant.TRADE_FLOW_TYPE_REALNAME_AUTH);
//		accountFlowDetail.setSubjectDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT);
//		accountFlowDetail.setTradeAmount(realNameExpensive);
//		accountFlowDetail.setTargetAccount(userAccount.getId());
//		accountFlowDetail.setTradeDesc("实名认证手续费扣除");
//		accountFlowDetail.setBasicModelProperty(custInfo.getId(), true);
//		accountFlowDetailRepository.save(accountFlowDetail);
//		
//		/**记录流水关系表**/
//		FlowBusiRelationEntity flowBusi = new FlowBusiRelationEntity();
//		flowBusi.setAccountFlowId(accountFlow.getId());
//		flowBusi.setRelatePrimary(custInfo.getId());
//		flowBusi.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
//		flowBusi.setMemo("实名认证手续费扣除");
//		flowBusiRelationRepository.save(flowBusi);	
	}
	
	/**
	 * 根据交易编号查询账户流水信息,事务是只读的;
	 * 
	 * @author  zhangzs
	 * @date    2015年5月22日 上午17:54:09
	 * @param tradeNo
	 * @return
	 */
	@Transactional(readOnly=true,isolation=Isolation.SERIALIZABLE,propagation=Propagation.REQUIRES_NEW)
	public AccountFlowInfoEntity findByTradeNoIsRead(String tradeNo){
		if(null == tradeNo)
			return null;
		return accountFlowInfoRepository.findFirstByTradeNoOrderByCreateDateDesc(tradeNo);
	}

	@Override
	public Map<String, Object> findCompanyAccount(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = accountFlowInfoRepositoryCustom.findCompanyAccount(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	@Override
	public Map<String, Object> findCompanyAccountSum(Map<String, Object> map) {
		
		return accountFlowInfoRepositoryCustom.findCompanyAccountSum(map);
	}

	@Override
	public ResultVo findProjectAuditInfoPage(Map<String, Object> param) {
		 return accountFlowInfoRepositoryCustom.findAllAuditInfoPage(param);
	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo insertRiskRechargeInfo(Map<String, Object> param) throws SLException {
		
		String userId = (String)param.get("custId");
		
		// 5.插入风险金垫付审核信息
		AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
		auditInfoEntity.setCustId(Constant.CUST_ID_PROJECT_RISK);
		auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_ACCOUNT_INFO);
		auditInfoEntity.setRelatePrimary(Constant.ACCOUNT_ID_PROJECT_RISK);
		auditInfoEntity.setApplyType(SubjectConstant.TRADE_FLOW_TYPE_RISK_RECHARGE);
		auditInfoEntity.setApplyTime(new Timestamp(System.currentTimeMillis()));
		auditInfoEntity.setTradeAmount(new BigDecimal(param.get("tradeAmount").toString()));
		auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_01);
		auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_UNREVIEW);
		auditInfoEntity.setBasicModelProperty(userId, true);
		auditInfoEntity = auditInfoRepository.save(auditInfoEntity);

		// 6.插入风险金垫付流水过程记录
		TradeFlowInfoEntity flow = new TradeFlowInfoEntity();
		flow.setCustId(Constant.CUST_ID_PROJECT_RISK);
		flow.setCustAccountId(Constant.ACCOUNT_ID_PROJECT_RISK);
		flow.setRelateType(Constant.TABLE_BAO_T_AUDIT_INFO);
		flow.setRelatePrimary(auditInfoEntity.getId());
		flow.setTradeAmount(new BigDecimal(param.get("tradeAmount").toString()));
		flow.setTradeExpenses(BigDecimal.ZERO);
		flow.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_RISK_RECHARGE);
		flow.setTradeNo(numberService.generateTradeNumber());
		flow.setTradeStatus(Constant.TRADE_STATUS_01);
		flow.setTradeDate(new Date());
		flow.setTradeDesc("充值");
		flow.setTradeSource(Constant.APP_SOURCE_WEB);
		flow.setMemo((String)param.get("memo"));
		flow.setBasicModelProperty(userId, true);
		flow = tradeFlowInfoRepository.save(flow);

		// 8.记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
		logInfoEntity.setRelatePrimary(flow.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_36);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperIpaddress("");
		logInfoEntity.setOperPerson(userId);
		logInfoEntity.setMemo(String.format("风险金充值%s元", param.get("tradeAmount").toString()));
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntityRepository.save(logInfoEntity);
					
		return new ResultVo(true);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo auditRiskRechargeInfo(Map<String, Object> param) throws SLException {
		String auditId = (String) param.get("auditId");
		if(StringUtils.isEmpty(auditId)){
			return new ResultVo(false, "审核id不能为空");
		}
		
		String auditStatus = (String) param.get("auditStatus");
		String userId = (String)param.get("custId");

		AuditInfoEntity auditInfoEntity = auditInfoRepository.findOne(auditId);
		String operBeforeContent = auditInfoEntity.getAuditStatus();
		if(!Constant.AUDIT_STATUS_UNREVIEW.equals(operBeforeContent)){
			return new ResultVo(false, "不是待审核状态，不能进行审核操作");
		}
		
		TradeFlowInfoEntity flowProcess = tradeFlowInfoRepository.findFirstByRelatePrimaryOrderByCreateDateDesc(auditInfoEntity.getId());
		if(flowProcess == null) {
			return new ResultVo(false, "风险金充值过程交易流水不存在");
		}
		
		flowProcess.setTradeStatus(Constant.TRADE_STATUS_03);
		flowProcess.setBasicModelProperty(userId, false);
		
		auditInfoEntity.setMemo((String) param.get("auditMemo"));
		auditInfoEntity.setAuditStatus(auditStatus);
		auditInfoEntity.setAuditUser(userId);
		auditInfoEntity.setAuditTime(new Date());
		auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03);
		auditInfoEntity.setBasicModelProperty(userId, false);
		
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_36);
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUDIT_INFO);
		logInfoEntity.setRelatePrimary(auditInfoEntity.getId());
		logInfoEntity.setOperBeforeContent(operBeforeContent);
		logInfoEntity.setOperAfterContent(auditStatus);
		logInfoEntity.setOperPerson(userId);
		logInfoEntity.setMemo((String) param.get("auditMemo"));
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		if(Constant.AUDIT_STATUS_REfUSE.equals(auditStatus)){
			flowProcess.setTradeStatus(Constant.TRADE_STATUS_04);
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_04);
			return new ResultVo(true);
		}
		
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findOne(Constant.ACCOUNT_ID_PROJECT_RISK);
		if(accountInfoEntity == null){
			return new ResultVo(false, "没有企业借款风险金账户");
		}
		BigDecimal tradeAmount = auditInfoEntity.getTradeAmount(); 
		accountInfoEntity.setAccountAvailableAmount(accountInfoEntity.getAccountAvailableAmount().add(tradeAmount));
		accountInfoEntity.setAccountTotalAmount(accountInfoEntity.getAccountTotalAmount().add(tradeAmount));
		accountInfoEntity.setBasicModelProperty(userId, false);
		
		saveAccountFlow(accountInfoEntity, null, null, null, "1", 
				SubjectConstant.TRADE_FLOW_TYPE_RISK_RECHARGE, flowProcess.getTradeNo(), SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				flowProcess.getTradeAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_RECHARGE, 
				null, null, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			
		return new ResultVo(true);
	}

	@Override
	public ResultVo findTradeFlowInfoById(Map<String, Object> param) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String auditId = (String) param.get("auditId");
		if(StringUtils.isEmpty(auditId)){
			return new ResultVo(false, "审核id不能为空");
		}
		
		AuditInfoEntity auditInfoEntity = auditInfoRepository.findOne(auditId);
		if(auditInfoEntity == null){
			return new ResultVo(false, "没有找到充值信息");
		}
		
		TradeFlowInfoEntity flowProcess = tradeFlowInfoRepository.findFirstByRelatePrimaryOrderByCreateDateDesc(auditInfoEntity.getId());
		if(flowProcess == null) {
			return new ResultVo(false, "风险金充值过程交易流水不存在");
		}
		
		List<Map<String, Object>> logInfoEntities = logInfoEntityRepository.findLogInfoByRelatePrimaryAndLogType(
				auditInfoEntity.getId(), Constant.OPERATION_TYPE_36);
		
		resultMap.put("memo", flowProcess.getMemo());
		resultMap.put("auditInfoEntity", auditInfoEntity);
		resultMap.put("logInfoEntities", logInfoEntities);
		
		return new ResultVo(true, "查询风险金充值成功", resultMap);
	}

	@Override
	public ResultVo findProjectAccountFlowPage(Map<String, Object> param) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Page<Map<String, Object>> page = accountFlowInfoRepositoryCustom.findAllAccountFlowPage(param);
		BigDecimal totalTradeAmount = accountFlowInfoRepositoryCustom.findSumAccountFlow(param);
	
		resultMap.put("totalTradeAmount", totalTradeAmount);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		return new ResultVo(true, "企业借款风险金流水查询成功", resultMap);
	}
	
	@Override
	public ResultVo queryMyWealthFlow(Map<String, Object> params)throws SLException {
		params.put("accountType", Constant.ACCOUNT_TYPE_SUB);
		return accountFlowInfoRepositoryCustom.queryMyWealthFlow(params);
	}
	
	@Override
	public ResultVo findRechargeList(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		Page<Map<String, Object>> page = tradeFlowInfoRepositoryCustom.findRechargeList(param);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		return new ResultVo(true, "查询充值流水成功", resultMap);
	}

	@Override
	public int queryByTradeTypeAndRelatePrimary(String tradeType,
			String relatePrimary) {
		return accountFlowInfoRepository.countByTradeTypeAndRelatePrimary(tradeType, relatePrimary);
	}
}
