/** 
 * @(#)RealNameAuthenticationServiceImpl.java 1.0.0 2015年4月25日 上午10:35:38  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executor;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.RealNameInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.DeviceInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.RealNameInfoRepository;
import com.slfinance.shanlincaifu.repository.TradeFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.impl.CustInfoRepositoryImpl;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.BankCardService;
import com.slfinance.shanlincaifu.service.CustManagerService;
import com.slfinance.shanlincaifu.service.CustomerService;
import com.slfinance.shanlincaifu.service.DeviceService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.OfflineWealthService;
import com.slfinance.shanlincaifu.service.OpenService;
import com.slfinance.shanlincaifu.service.OpenThirdService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.RealNameAuthenticationService;
import com.slfinance.shanlincaifu.service.ThirdPartyPayService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.thirdpp.util.SharePropertyConstant;
import com.slfinance.thirdpp.vo.TradeResultVo;
import com.slfinance.vo.ResultVo;

/**   
 * 实名认证业务相关接口实现
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月25日 上午10:35:38 $ 
 */
@Slf4j
@Service("realNameAuthenticationService")
public class RealNameAuthenticationServiceImpl implements RealNameAuthenticationService {

	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private LogInfoEntityRepository logInfoRepository;
	
	@Autowired
	private ThirdPartyPayService thirdPartyPayService;
	
	@Autowired
	private FlowNumberService numberService;	
	
	@Autowired
	private AccountFlowService accountFlowService;
	
	@Autowired
	private CustomerService  customerService;
	
	@Autowired
	private OpenService openService;
	
	@Autowired
	private CustInfoRepositoryImpl custInfoRepositoryImpl;
	
	@Autowired
	private RealNameInfoRepository realNameInfoRepository;

	private final static ThreadLocal<CustInfoEntity> localCustInfo = new ThreadLocal<>();
	private final static ThreadLocal<AccountInfoEntity> localAccountInfo = new ThreadLocal<>();
	
	@Autowired
	private TradeFlowInfoRepository tradeFlowInfoRepository;
	
	@Value("${thirdPartyPay.base.request.realname}")
	private String thirdPartyRealName;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	@Qualifier("commonThreadPoolTaskExecutor")
	Executor executor;
	
	@Autowired
	CustManagerService custManagerService;
	
	@Autowired
	OfflineWealthService offlineWealthService;
	
	@Autowired
	RefereeAuditService refereeAuditService;
	
	@Autowired
	OpenThirdService openThirdService;
	
	@Autowired
	BankCardService bankCardService;
//	/**
//	 * 
//	 * 实名认证- 预处理
//	 */
//	@Override
//	public ResultVo  preverifyIdentification(Map<String, Object> paramsMap) throws SLException {
//		ResultVo resultVo = new ResultVo(true);
//		/**校验实名认证次数、调用第三方平台进行实名认证；认证成功更新实名认证信息、认证失败更新次数和相关业务,认证**/
//		// 判断是否可以实名认证
//		CustInfoEntity custInfo = custInfoRepository.findOne((String)paramsMap.get("custId"));
//		if( null == custInfo )
//			return new ResultVo(false,"用户信息不存在");
//		if (Constant.ENABLE_STATUS_02.equals(custInfo.getEnableStatus()))
//			return new ResultVo(false, "该账户实名认证次数超过三次，已被暂时冻结，请联系客服解冻");
//		if(0 < custInfoRepository.countByCredentialsCode((String)paramsMap.get("credentialsCode")))
//			return new ResultVo(false,"身份信息已经被使用");
//		if (StringUtils.isNotEmpty(custInfo.getCustName()) && StringUtils.isNotEmpty(custInfo.getCredentialsCode()))
//			return new ResultVo(false, "该账户已经实名认证");
//		
//		localCustInfo.set(custInfo);
//		return resultVo;
//	}
//	/**
//	 * 
//	 * 实名认证
//	 */
//	@Override
//	public ResultVo verifyIdentification(Map<String, Object> paramsMap) throws SLException {
//		CustInfoEntity custInfo =  localCustInfo.get();
//		//用户名称去空
//		paramsMap.put("custName", StringUtils.trim((String)paramsMap.get("custName")));
//		// 调用第三方接口实名认证
//		String tradeCode = numberService.generateTradeNumber();
//		String batchCode =numberService.generateTradeBatchNumber();
//		Map<String, Object> thirdPartyPayParams = Maps.newHashMap();
//		thirdPartyPayParams.put("custCode", custInfo.getCustCode());
//		thirdPartyPayParams.put("custName", (String)paramsMap.get("custName"));
//		thirdPartyPayParams.put("credentialsCode", (String)paramsMap.get("credentialsCode"));
//		thirdPartyPayParams.put("credentialsType", "01");
//		thirdPartyPayParams.put("tradeCode", tradeCode);
//		thirdPartyPayParams.put("batchCode", batchCode);
//		ResultVo resultVo = null;
//	
//		try {
//			resultVo = thirdPartyPayService.realNameAuth(thirdPartyPayParams);
//		} catch (Exception e) {
//			resultVo = new ResultVo(false,"认证系统异常，请稍后再试！","认证系统异常，请稍后再试！");
//			log.info("调用TPP实名认证异常"+e.getMessage() !=null ? e.getMessage() : e.getCause().toString());
//		}
//
//		return internalVerifyIdentification.doVerifyIdentification(paramsMap, thirdPartyPayParams,resultVo);
//	}
//	
//	// 注：事务在调用的时候，像以下方式无法调用，需将methodB单独写一个类再引用
//	//	class ServiceA {
//	//		 
//	//	    public void methodA() {
//	//	        methodB(); 
//	//	    }
//	//	 
//	//	    @Transactional
//	//	    public void methodB(){
//	//	        doSomething();
//	//	        throw new RuntimeException();
//	//	    }
//	//	 
//	//	}
//	@Autowired
//	private InternalVerifyIdentification internalVerifyIdentification;
//	
//	@Service
//	public static class InternalVerifyIdentification {
//		
//		@Autowired
//		private OpenService openService;
//		
//		@Autowired
//		private LogInfoEntityRepository logInfoRepository;
//		
//		@Autowired
//		private AccountFlowService accountFlowService;
//		
//		@Autowired
//		private CustomerService  customerService;
//		
//		@Transactional(readOnly=false,rollbackFor=SLException.class)
//		public ResultVo doVerifyIdentification(final Map<String, Object> paramsMap, final Map<String, Object> thirdPartyPayParams, final ResultVo resultVo) throws SLException {
//			String message = "实名认证成功";
//			boolean success = true;
//			CustInfoEntity custInfo =  localCustInfo.get();
//			// 判断是否成功,实名认证成功更新用户信息,计算客户性别、年龄,只有在实名认证失败后才更新他的实名认证次数
//			CustInfoEntity updateInfo = new CustInfoEntity();
//			updateInfo.setBasicModelProperty((String)paramsMap.get("custId"), false);
//			
//			//保存冻结日志（可选）和实名认证日志
//			List<LogInfoEntity> logList = new ArrayList<LogInfoEntity>();
//			switch (String.valueOf(resultVo.getValue("success"))) {
//				case "true":
//					updateInfo.setRealNameAuth((String)paramsMap.get("custName"), (String)paramsMap.get("credentialsCode"));
//				break;
//				default:
//					custInfo.setRealNameAuthCount(custInfo.getRealNameAuthCount() == null ? BigDecimal.ZERO : custInfo.getRealNameAuthCount());
//					updateInfo.setRealNameAuthCount(ArithUtil.add(custInfo.getRealNameAuthCount(), new BigDecimal("1")));
//					if (updateInfo.getRealNameAuthCount().compareTo(new BigDecimal("3")) == 1 || updateInfo.getRealNameAuthCount().compareTo(new BigDecimal("3")) == 0){
//						updateInfo.setEnableStatus(Constant.ENABLE_STATUS_02);
//						//客户冻结加上日志，operPerson记录字段是被操作人的ID,createUser记录是操作人的ID
//						LogInfoEntity logInfo = new LogInfoEntity(Constant.TABLE_BAO_T_CUST_INFO,custInfo.getId(), Constant.OPERATION_TYPE_01, "", "", custInfo.getLoginName()+"做实名认证操作",(String)paramsMap.get("custId") );
//						logInfo.setMemo(custInfo.getLoginName()+"做实名认证操作三次，冻结。");
//						logInfo.setOperIpaddress((String)paramsMap.get("ipAddress"));
//						logInfo.setBasicModelProperty((String)paramsMap.get("custId"), true);
//						logInfo.setCreateUser(Constant.SYSTEM_USER_BACK);
//						logList.add(logInfo);
//					}
//					success = false;
//					message = (String)resultVo.getValue("message");
//				break;
//			}
//			
//			//组装日志信息
//			StringBuilder operDesc = new StringBuilder("用户：").append(custInfo.getLoginName()).append("，做").append(Constant.OPERATION_TYPE_03).append("操作");
//			LogInfoEntity logInfo = new LogInfoEntity(Constant.TABLE_BAO_T_CUST_INFO,custInfo.getId(), Constant.OPERATION_TYPE_03, "", "", operDesc.toString(), custInfo.getId());
//			logInfo.setMemo(operDesc.toString());
//			logInfo.setOperIpaddress((String)paramsMap.get("ipAddress"));
//			logInfo.setBasicModelProperty((String)paramsMap.get("custId"), true);
//			//新增审核日志信息
//			logList.add(logInfo);
//			logInfoRepository.save(logList);
//			
//			/**实名认证成功(通信成功，无关业务)之后扣除实名认证手续费**/
//			try {
//				accountFlowService.insertRealNameAuthExpense(resultVo,custInfo,thirdPartyPayParams);
//			} catch (Exception e) {
//				/**处理并发情况下，更新用户的实名认证次数和相关信息**/
//				log.error(e.getMessage() != null ? e.getMessage().toString():e.getCause().toString());
//				custInfo.setRealNameAuthCount(custInfo.getRealNameAuthCount() == null ? BigDecimal.ZERO : custInfo.getRealNameAuthCount());
//				updateInfo.setRealNameAuthCount(ArithUtil.add(custInfo.getRealNameAuthCount(), new BigDecimal("1")));
//				if (updateInfo.getRealNameAuthCount().compareTo(new BigDecimal("3")) == 1 || updateInfo.getRealNameAuthCount().compareTo(new BigDecimal("3")) == 0){
//					updateInfo.setEnableStatus(Constant.ENABLE_STATUS_02);
//					//客户冻结加上日志，operPerson记录字段是被操作人的ID,createUser记录是操作人的ID
//					LogInfoEntity logInfoExp = new LogInfoEntity(Constant.TABLE_BAO_T_CUST_INFO,custInfo.getId(), Constant.OPERATION_TYPE_01, "", "", custInfo.getLoginName()+"做实名认证操作",(String)paramsMap.get("custId") );
//					logInfoExp.setMemo(custInfo.getLoginName()+"做实名认证操作三次，冻结。");
//					logInfoExp.setOperIpaddress((String)paramsMap.get("ipAddress"));
//					logInfoExp.setBasicModelProperty((String)paramsMap.get("custId"), true);
//					logInfoExp.setCreateUser(Constant.SYSTEM_USER_BACK);
//					logList.add(logInfoExp);
//				}
//				updateInfo.setRealNameAuthClear();
//				/**新开事务更新用户信息**/
//				customerService.updCustAndInsertLog(updateInfo,logList,(String)paramsMap.get("custId"));
//				throw new SLException("记录账户流水出现异常!");
//			}
//			
//			if(!custInfo.update(updateInfo))
//				throw new SLException("更新用户信息失败");
//			
//			if (updateInfo.getRealNameAuthCount().compareTo(new BigDecimal("3")) == 1 || updateInfo.getRealNameAuthCount().compareTo(new BigDecimal("3")) == 0)
//				message = "该账户实名认证次数超过三次，已被暂时冻结，请联系客服解冻";
//			
//			openService.saveRealNameAuth(paramsMap);
//			
//			return new ResultVo(success,message);
//		}
//	}
//
//	/**
//	 * 
//	 * 实名认证- 后处理
//	 */
//	@Override
//	public ResultVo  postverifyIdentification(ResultVo resultVo) throws SLException {
//		localCustInfo.remove();
//		return new ResultVo(true);
//	}
//
	/**
	 * 实名认证次数
	 */
	@Override
	public BigDecimal getRealNameAuthCount(Map<String, Object> paramsMap)
			throws SLException {
		CustInfoEntity custInfo = custInfoRepository.findOne((String)paramsMap.get("custId"));
		if (custInfo == null)
			throw new SLException("客户信息不存在!");
		return custInfo.getRealNameAuthCount();
	}
	
	@Override
	public ResultVo preverifyIdentification(Map<String, Object> paramsMap)
			throws SLException {
		
		//用户名称和身份证去空
		paramsMap.put("custName", StringUtils.trim((String)paramsMap.get("custName")));
		paramsMap.put("credentialsCode", StringUtils.trim((String)paramsMap.get("credentialsCode")));
		
		String custId = (String)paramsMap.get("custId");
		String credentialsCode = (String)paramsMap.get("credentialsCode");
		String custName = (String)paramsMap.get("custName");
		credentialsCode = credentialsCode.toUpperCase();
		
		ResultVo resultVo = new ResultVo(true);
		
		// check-1) 验证用户是否存在
		CustInfoEntity custInfo = custInfoRepository.findOne(custId);
		if( null == custInfo )
			return new ResultVo(false,"用户信息不存在");
		
		// check-2) 验证客户是否冻结
		if (Constant.ENABLE_STATUS_02.equals(custInfo.getEnableStatus()))
			return new ResultVo(false, "该账户实名认证次数超过三次，已被暂时冻结，请联系客服解冻");
		
		// check-3) 验证用户是否已经实名认证
		if (StringUtils.isNotEmpty(custInfo.getCustName()) && StringUtils.isNotEmpty(custInfo.getCredentialsCode()))
			return new ResultVo(false, "该账户已经实名认证");
		
		// check-4) 验证身份证号是否被使用
		if(0 < custInfoRepository.countByCredentialsCode(credentialsCode))
			return new ResultVo(false,"身份信息已经被使用");
		
		// check-5) 验证错误身份证重复认证
		if(0 < tradeFlowInfoRepository.countRealNameFailedByTradeDesc(custName + ":" + credentialsCode)) {
			return new ResultVo(false, "您提交的认证信息有误，请仔细核对");
		}
		
		// check-6) 验证用户账户
		AccountInfoEntity accountInfo = accountInfoRepository.findByCustId(custId);
		if(null == accountInfo) {
			return new ResultVo(false,"用户账户不存在");
		}
		
		localCustInfo.set(custInfo);
		localAccountInfo.set(accountInfo);
		return resultVo;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo verifyIdentification(final Map<String, Object> paramsMap)
			throws SLException {
		
		final String custId = (String)paramsMap.get("custId");
		final String credentialsCode = ((String)paramsMap.get("credentialsCode")).toUpperCase();
		String custName = (String)paramsMap.get("custName");
		thirdPartyRealName = StringUtils.defaultIfBlank(thirdPartyRealName, "LLPay");
			
		// 防重复提交
		if(0 >= custInfoRepository.updateCustInfoToAvoidDuplicate(custId, true)) {
			return new ResultVo(false, "您已经提交实名认证，请勿重复提交"); 
		}

		// 调用第三方接口实名认证
		CustInfoEntity custInfo =  localCustInfo.get();	
		Map<String, Object> thirdPartyPayParams = Maps.newHashMap();
		String tradeCode = numberService.generateTradeNumber();
		String batchCode =numberService.generateTradeBatchNumber();			
		thirdPartyPayParams.put("custCode", custInfo.getCustCode());
		thirdPartyPayParams.put("custName", custName);
		thirdPartyPayParams.put("credentialsCode", credentialsCode);
		thirdPartyPayParams.put("credentialsType", "01");
		thirdPartyPayParams.put("tradeCode", tradeCode);
		thirdPartyPayParams.put("batchCode", batchCode);
		thirdPartyPayParams.put("thirdPartyRealName", thirdPartyRealName);
		
		Map<String, Object> requestParam = Maps.newConcurrentMap();
		requestParam.put("custId", custId);
		requestParam.put("custName", custName);
		requestParam.put("idCard", credentialsCode);
		ResultVo resultVo = openThirdService.isRealName(requestParam);
		if(!ResultVo.isSuccess(resultVo)) {
			try {
				resultVo = thirdPartyPayService.realNameAuth(thirdPartyPayParams);
			} catch (Exception e) {
				resultVo = new ResultVo(false,"认证系统异常，请稍后再试！","认证系统异常，请稍后再试！");
				log.error("调用TPP实名认证异常"+e.getMessage() !=null ? e.getMessage() : e.getCause().toString());
			}
		}
		else {
			Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
			thirdPartyPayParams.put("custCode", data.get("custCode"));
			if(data.containsKey("bank") && data.get("bank") != null) { // 返回的数据存在银行卡信息，则绑定银卡
				Map<String, Object> bankRequestMap = Maps.newConcurrentMap();
				Map<String, Object> bank = (Map<String, Object>)data.get("bank");
				bankRequestMap.put("tradeType", Constant.NOTIFY_TYPE_TRADE_TYPE_BIND_CARD);
				bankRequestMap.put("custCode", thirdPartyPayParams.get("custCode"));
				bankRequestMap.put("custName", thirdPartyPayParams.get("custName"));
				bankRequestMap.put("credentialsCode", thirdPartyPayParams.get("credentialsCode"));
				bankRequestMap.put("bankCardNo", bank.get("bankCardAccount"));
				bankRequestMap.put("bankName", bank.get("bankName"));
				if(bank.containsKey("protocolNumber") && !StringUtils.isEmpty((String)bank.get("protocolNumber"))) {
					bankRequestMap.put("responseCardId", bank.get("protocolNumber"));
				}
				if(bank.containsKey("openBankProvince") && !StringUtils.isEmpty((String)bank.get("openBankProvince"))) {
					bankRequestMap.put("openProvince", bank.get("openBankProvince").toString());
				}
				if(bank.containsKey("openBankCity")&& !StringUtils.isEmpty((String)bank.get("openBankCity"))) {
					bankRequestMap.put("openCity", bank.get("openBankCity").toString()); 
				}
				if(bank.containsKey("branchName")&& !StringUtils.isEmpty((String)bank.get("branchName"))) {
					bankRequestMap.put("subBranchName", bank.get("branchName").toString());
				}
				bankRequestMap.put("custInfoEntity", custInfo);
				bankCardService.bindBankCard(bankRequestMap);
			}
		}

		// 鉴于公司账户并发问题可能导致失败，需加一些尝试机制
		try
		{
			int numAttempts = 0;
			BigDecimal realNameAuthCount = custInfo.getRealNameAuthCount();
			do {
	            numAttempts++;
	            try {
	            	custInfo.setRealNameAuthCount(realNameAuthCount);// 为保证多次尝试认证次数均保持为原值，此处需先设置
	            	resultVo = internalVerifyIdentification.doVerifyIdentification(paramsMap, thirdPartyPayParams, resultVo);
	            	break;
	            }
	            catch(OptimisticLockingFailureException e) {
	            	log.error("实名认证异常：" + e.getMessage());
	            	try {
						Thread.sleep(300);
					} catch (InterruptedException e1) {
						log.error("实名认证异常：" + e1.getMessage());
					}
	            }
	            log.info("用户{}实名认证[{}:{}]开始第{}次尝试", custInfo.getLoginName(), custName, credentialsCode, numAttempts + 1);
	        } while(numAttempts <= 5);
		}
		catch (Exception e) {
			log.error("实名认证异常：" + e.getMessage());
		}
		finally {
			custInfoRepository.updateCustInfoToAvoidDuplicate(custId, false);
		}
		// 2016-06-29 update by liyy for synchronise with offLine wealth. Start
		//异步处理数据
		/*if(ResultVo.isSuccess(resultVo)) {
			executor.execute(new Runnable() {
				public void run() {
					try {
						offlineWealthService.custTransfer(custId, credentialsCode);
					} catch (Exception e) {
						log.error("客户转移失败!" + e.getMessage());
					}
				}
			});
		}*/
		// 2016-06-29 update by liyy for synchronise with offLine wealth. End
		return resultVo;
	}

	@Override
	public ResultVo postverifyIdentification(ResultVo resutlVo)
			throws SLException {
		localCustInfo.remove();
		localAccountInfo.remove();
		return new ResultVo(true);
	}

	@Autowired
	private InternalVerifyIdentification internalVerifyIdentification;
	
	@Service
	public static class InternalVerifyIdentification {
		
		@Autowired
		private OpenService openService;
		
		@Autowired
		private LogInfoEntityRepository logInfoRepository;
		
		@Autowired
		private AccountFlowService accountFlowService;
		
		@Autowired
		private CustomerService  customerService;
		
		@Autowired
		private ParamService paramService;
		
		@Autowired
		private TradeFlowInfoRepository tradeFlowInfoRepository;
		
		@Autowired
		private AccountInfoRepository accountInfoRepository;
		
		@Autowired
		private DeviceInfoRepository deviceInfoRepository;
		
		@Autowired
		private DeviceService deviceService;
		@Transactional(readOnly=false,rollbackFor=SLException.class)
		public ResultVo doVerifyIdentification(final Map<String, Object> paramsMap, final Map<String, Object> thirdPartyPayParams, final ResultVo resultVo) throws SLException {
			
			String custId = (String)paramsMap.get("custId");
			String credentialsCode = (String)paramsMap.get("credentialsCode");
			String custName = (String)paramsMap.get("custName");
			String ipAddress = (String)paramsMap.get("ipAddress");
			String tradeCode = (String)thirdPartyPayParams.get("tradeCode");
			String batchCode = (String)thirdPartyPayParams.get("batchCode");
			String thirdPartyRealName = (String)thirdPartyPayParams.get("thirdPartyRealName");
			String custCode = (String)thirdPartyPayParams.get("custCode");
			credentialsCode = credentialsCode.toUpperCase();
			CustInfoEntity custInfo =  localCustInfo.get();
			AccountInfoEntity accountInfo = localAccountInfo.get();
			if(custInfo.getRealNameAuthCount() == null) { // 实名认证次数为空时设置次数为0
				custInfo.setRealNameAuthCount(BigDecimal.ZERO);
			}
			
			// 1) 记录交易过程流水
			TradeFlowInfoEntity flow = new TradeFlowInfoEntity();
			flow.setCustId(custId);
			flow.setCustAccountId(accountInfo.getId());
			flow.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
			flow.setRelatePrimary(custId);
			flow.setTradeType(Constant.OPERATION_TYPE_03);
			flow.setTradeNo(tradeCode);
			flow.setTradeStatus(Constant.TRADE_STATUS_02);		
			flow.setTradeDate(new Date());
			flow.setTradeDesc(custName + ":" + credentialsCode);
			flow.setTradeSource((String)paramsMap.get("appSource"));
			flow.setTradeExpenses(paramService.findRealNameExpenseAmount(thirdPartyRealName));
			flow.setSubTradeType(thirdPartyRealName.equals("LLPay") ? "连连支付" : "国政通");// 填写认证方式
			flow.setBasicModelProperty(custId, true);
			flow = tradeFlowInfoRepository.save(flow);
			
			// 2) 记录调用日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
			logInfoEntity.setRelatePrimary(custId);
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_03);
			logInfoEntity.setOperDesc(String.format("用户%s进行实名认证", custInfo.getLoginName()));
			logInfoEntity.setOperPerson(custId);
			logInfoEntity.setOperIpaddress(ipAddress);
			logInfoEntity.setMemo(logInfoEntity.getOperDesc());
			logInfoEntity.setBasicModelProperty(custId, true);
			logInfoRepository.save(logInfoEntity);
			
			// 3) 处理认证结果
			if(ResultVo.isSuccess(resultVo)) { // 实名认证成功
				
				// 3-1) 更新姓名和身份证
				custInfo.setCustName(custName);
				custInfo.setCredentialsCode(credentialsCode);
				custInfo.setBirthday(credentialsCode.substring(6, 14));
				custInfo.setCustGender(Integer.parseInt(credentialsCode.substring(16, 17)) % 2 != 0 ? Constant.SEX_MAN : Constant.SEX_WOMAN);
				custInfo.setCredentialsType(Constant.CREDENTIALS_ID_CARD);
				custInfo.setCustCode(custCode);
				custInfo.setBasicModelProperty(custId, false);
				
				// 3-2) 更新交易流水成功
				flow.setTradeStatus(Constant.TRADE_STATUS_03);
				flow.setBasicModelProperty(custId, false);
				
				// 3-3) 第三方实名认证
				openService.saveRealNameAuth(paramsMap);
			}
			else { // 实名认证失败
				
				// 3-1) 更新交易流水失败
				flow.setTradeStatus(Constant.TRADE_STATUS_04);
				if(resultVo.getValue("tradeResultVo") != null) {
					flow.setMemo(((TradeResultVo)resultVo.getValue("tradeResultVo")).getResponseInfo());
				}
				else {
					flow.setMemo((String)resultVo.getValue("message"));
				}
				flow.setBasicModelProperty(custId, false);
				
				// 3-2) 验证次数加1
				custInfo.setRealNameAuthCount(ArithUtil.add(custInfo.getRealNameAuthCount(), new BigDecimal("1")));
				custInfo.setBasicModelProperty(custId, false);
				
				// 验证次数大于等于3？
				if(custInfo.getRealNameAuthCount().compareTo(new BigDecimal("3")) >= 0) {
					// 3-3) 冻结客户
					custInfo.setEnableStatus(Constant.ENABLE_STATUS_02);
					
					// 3-4) 记录冻结日志
					LogInfoEntity freezeLogInfoEntity = new LogInfoEntity();
					freezeLogInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
					freezeLogInfoEntity.setRelatePrimary(custId);
					freezeLogInfoEntity.setLogType(Constant.OPERATION_TYPE_01);
					freezeLogInfoEntity.setOperDesc(String.format("用户%s做实名认证操作三次，冻结。", custInfo.getLoginName()));
					freezeLogInfoEntity.setOperPerson(custId);
					freezeLogInfoEntity.setOperIpaddress(ipAddress);
					freezeLogInfoEntity.setMemo(freezeLogInfoEntity.getOperDesc());
					freezeLogInfoEntity.setBasicModelProperty(custId, true);
					logInfoRepository.save(freezeLogInfoEntity);
					
					// 3-5) 修改返回信息
					resultVo.putValue("message", "该账户实名认证次数超过三次，已被暂时冻结，请联系客服解冻");
				}
			}
			
			// 4) 记录设备信息
			if(paramsMap.containsKey("channelNo")) {
//				DeviceInfoEntity deviceInfoEntity = new DeviceInfoEntity();
//				deviceInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
//				deviceInfoEntity.setRelatePrimary(flow.getId());
//				deviceInfoEntity.setCustId(custId);
//				deviceInfoEntity.setTradeType(Constant.OPERATION_TYPE_03);
//				deviceInfoEntity.setMeId((String)paramsMap.get("meId"));
//				deviceInfoEntity.setMeVersion((String)paramsMap.get("meVersion"));
//				deviceInfoEntity.setAppSource(Strings.nullToEmpty((String)paramsMap.get("appSource")).toLowerCase());
//				deviceInfoEntity.setChannelNo(paramService.findByChannelNo((String)paramsMap.get("channelNo")));
//				deviceInfoEntity.setBasicModelProperty(custId, true);
//				deviceInfoRepository.save(deviceInfoEntity);
				Map<String, Object> deviceParams = Maps.newConcurrentMap();
				deviceParams.putAll(paramsMap);
				deviceParams.put("relateType", Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
				deviceParams.put("relatePrimary", flow.getId());
				deviceParams.put("tradeType", Constant.OPERATION_TYPE_03);
				deviceParams.put("userId", custId);
				deviceService.saveUserDevice(deviceParams);
			}
			
			// 5) 记录实名认证手续费
			if( ResultVo.isSuccess(resultVo) || ( !ResultVo.isSuccess(resultVo) && null != resultVo.getValue(SharePropertyConstant.REQUEST_IS_SUCCESS) && (boolean)resultVo.getValue(SharePropertyConstant.REQUEST_IS_SUCCESS))) {
				AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_ERAN); // 公司收益主账户				
				earnMainAccount.setAccountTotalAmount(ArithUtil.sub(earnMainAccount.getAccountTotalAmount(), flow.getTradeExpenses()));
				earnMainAccount.setAccountAvailableAmount(ArithUtil.sub(earnMainAccount.getAccountAvailableAmount(), flow.getTradeExpenses()));
				earnMainAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
				
				AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(earnMainAccount, null, accountInfo, null, "1", 
						SubjectConstant.TRADE_FLOW_TYPE_REALNAME_AUTH, flow.getTradeNo(), SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
						flow.getTradeExpenses(), SubjectConstant.TRADE_FLOW_TYPE_REALNAME_AUTH, 
						Constant.TABLE_BAO_T_CUST_INFO, custId, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity.setRequestNo(batchCode);
			}
			
			return resultVo;
		}
	}

	@Override
	public ResultVo salesManVerifyIdentification(Map<String, Object> paramsMap)
			throws SLException {
		
		ResultVo resultVo = verifyIdentification(paramsMap);
		if(ResultVo.isSuccess(resultVo)) { // 实名认证成功，调用标记业务员
//			if(!custInfoRepository.judgeUserIsEmployee(paramsMap)) { // 用户未在人事系统找到 
//				return resultVo; 
//			}
			
			// 生成业务员
			ResultVo isEmployee = refereeAuditService.createEmployee(paramsMap);
			if(!ResultVo.isSuccess(isEmployee)) {
				return resultVo; 
			}
			
			Map<String, Object> data = Maps.newConcurrentMap();
			data.put("flag", "yes");
			
			return new ResultVo(true, "success", data);
		}
		
		return resultVo;
	}

	@Override
	public ResultVo presalesManVerifyIdentification(
			Map<String, Object> paramsMap) throws SLException {
		
		return preverifyIdentification(paramsMap);
	}

	@Override
	public ResultVo postsalesManVerifyIdentification(ResultVo resutlVo)
			throws SLException {
		
		return postverifyIdentification(resutlVo);
	}
	
	/**
	 * 实名认证列表
	 * 
	 * @author HuangXiaodong
	 * @date 2017年6月24日
	 * @param Map
	 *         <tt>start           :String:起始值</tt><br>
     *         <tt>length          :String:长度</tt><br>
	 *         <tt>custName           :String:客户姓名(可以为空)</tt><br>
	 *         <tt>credentialsCode       :String:证件号码(可以为空)</tt><br>
	 *         <tt>tradeStatus        :String:认证结果(可以为空)</tt><br>
	 *         <tt>loginName          :String:认证人(可以为空)</tt><br>
	 *          <tt>startDate   ：Date:认证开始时间</tt><br>
	 *          <tt>endDate：        Date:认证结束时间</tt><br>
	 * @return ResultVo <tt>custName  :String:客户姓名</tt><br>
	 *         <tt>credentialsCode        :String:证件号码</tt><br>
	 *         <tt>tradeStatus        :String:认证结果</tt><br>
	 *         <tt>custAuthDate： BigDecimal:认证时间</tt><br>
	 *         <tt>loginName          :String:认证人</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryCustAuthInfoList(Map<String, Object> param) {
		Page<Map<String, Object>> pageVo = custInfoRepositoryImpl.queryCustAuthInfoList(param);
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "渠道管理列表查询成功", result);
	}
	
	/**
	 * 实名认证记录
	 * 
	 * @author HuangXiaodong
	 * @date 2017年6月24日
	 * @param params
	 *         <tt>custName           :String:客户姓名(不能为空)</tt><br>
	 *         <tt>credentialsCode       :String:证件号码(不能为空)</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveCustAuthInfo(Map<String, Object> params)
			throws SLException {
		String custName = (String) params.get("custName");
		String credentialsCode = (String) params.get("credentialsCode");
		String custId = params.get("custId").toString();
		
		RealNameInfoEntity obj=realNameInfoRepository.findByCredentialsCodeAndTradeStatus(credentialsCode, "匹配");
		if(obj!=null){
			return new ResultVo(true, "此用户已认证成功过！");
		}
		String  message="恭喜您，认证通过！";
		// 调用第三方接口实名认证
		CustInfoEntity custInfo =  localCustInfo.get();	
		Map<String, Object> thirdPartyPayParams = Maps.newHashMap();
		String tradeCode = numberService.generateTradeNumber();
		String batchCode =numberService.generateTradeBatchNumber();			
		thirdPartyPayParams.put("custCode", "111111");
		thirdPartyPayParams.put("custName", custName);
		thirdPartyPayParams.put("credentialsCode", credentialsCode);
		thirdPartyPayParams.put("credentialsType", "01");
		thirdPartyPayParams.put("tradeCode", tradeCode);
		thirdPartyPayParams.put("batchCode", batchCode);
		thirdPartyPayParams.put("thirdPartyRealName", thirdPartyRealName);
		ResultVo resultVo =new ResultVo(false);
		try {
			resultVo = thirdPartyPayService.realNameAuth(thirdPartyPayParams);
		} catch (Exception e) {
			resultVo = new ResultVo(false,"认证系统异常，请稍后再试！","认证系统异常，请稍后再试！");
			log.error("调用TPP实名认证异常"+e.getMessage() !=null ? e.getMessage() : e.getCause().toString());
		}
		
		RealNameInfoEntity realNameInfoEntity = new RealNameInfoEntity();
		realNameInfoEntity.setCustName((String) params.get("custName"));
		realNameInfoEntity.setCredentialsCode((String) params.get("credentialsCode"));
		if(resultVo.result.get("success")!=null&&resultVo.result.get("success").toString().equals("true")){
			realNameInfoEntity.setTradeStatus("匹配");
		}else{
			if(resultVo.result.get("requestSuccess")!=null&&resultVo.result.get("requestSuccess").toString().equals("false")){
				realNameInfoEntity.setTradeStatus("不匹配");
				message="实名认证失败！请稍后再试！";
			}else{
				realNameInfoEntity.setTradeStatus("不匹配");
				message="您输入的姓名与证件号码不匹配，请重新输入！";
			}
		}
		
		realNameInfoEntity.setBatchNo(batchCode);
		if(resultVo.getValue("tradeResultVo") != null) {
			realNameInfoEntity.setMemo(((TradeResultVo)resultVo.getValue("tradeResultVo")).getResponseInfo());
		}
		else {
			realNameInfoEntity.setMemo((String)resultVo.getValue("message"));
		}
		realNameInfoEntity.setBasicModelProperty(custId, true);
		realNameInfoRepository.save(realNameInfoEntity);
		// 记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_REAL_NAME_INFO);
		logInfoEntity.setRelatePrimary(realNameInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_89);
		logInfoEntity.setOperBeforeContent("");
		logInfoEntity.setOperAfterContent(String.format(
				"客户姓名=%s，证件号码=%s，认证结果=%s，认证编号=%s",
				realNameInfoEntity.getCustName(),
				realNameInfoEntity.getCredentialsCode(),
				realNameInfoEntity.getTradeStatus(),
				realNameInfoEntity.getBatchNo()));
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoRepository.save(logInfoEntity);
		return new ResultVo(true, message);
	}
}
