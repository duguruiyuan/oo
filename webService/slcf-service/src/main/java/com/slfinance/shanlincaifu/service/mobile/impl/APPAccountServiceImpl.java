/** 
 * @(#)APPAccountServiceImpl.java 1.0.0 2015年5月19日 下午5:49:55  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.mobile.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductRateInfoEntity;
import com.slfinance.shanlincaifu.entity.SystemMessageInfoEntity;
import com.slfinance.shanlincaifu.entity.SystemMessageReadInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductRateInfoRepository;
import com.slfinance.shanlincaifu.repository.SystemMessageInfoRepository;
import com.slfinance.shanlincaifu.repository.SystemMessageReadInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.AccountFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.CustActivityInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.FixedInvestRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.SystemMessageRepositoryCustom;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.AccountService;
import com.slfinance.shanlincaifu.service.AfficheInfoService;
import com.slfinance.shanlincaifu.service.BankCardService;
import com.slfinance.shanlincaifu.service.CustActivityInfoService;
import com.slfinance.shanlincaifu.service.CustomerService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.TradeFlowService;
import com.slfinance.shanlincaifu.service.mobile.APPAccountService;
import com.slfinance.shanlincaifu.service.mobile.APPBankService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.PageFuns;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

/**   
 * APP手机端账户模块业务接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月19日 下午5:49:55 $ 
 */
@Service
public class APPAccountServiceImpl implements APPAccountService {

	@Autowired
	private SystemMessageInfoRepository systemMessageInfoRepository;
	
	@Autowired
	private SystemMessageReadInfoRepository systemMessageReadInfoRepository;
	
	@Autowired
	private SystemMessageRepositoryCustom systemMessageRepositoryCustom;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private CustActivityInfoService custActivityInfoService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private AccountFlowService accountFlowService;
	
	@Autowired
	private ProductRateInfoRepository productRateInfoRepository;
	
	@Autowired
	private BankCardService bankCardService;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private AccountFlowInfoRepositoryCustom accountFlowInfoRepositoryCustom;
	
	@Autowired
	private AccountFlowInfoRepository accountFlowInfoRepository;
	
	@Autowired
	private APPBankService appBankService;
	
	@Autowired
	private InvestInfoRepository investInfoRepository;
	
	@Autowired
	private FixedInvestRepositoryCustom fixedInvestRepositoryCustom;
	
	@Autowired
	private CustActivityInfoRepositoryCustom custActivityInfoRepositoryCustom;
	
	@Autowired
	private AfficheInfoService afficheInfoService;
	
	@Autowired
	private TradeFlowService tradeFlowService;
	/**
	 * 账户首页
	 */
	@Override
	public ResultVo user(Map<String, Object> paramsMap)throws SLException {
		ResultVo resultVo = new ResultVo(true);
		
		Map<String,Object> resultMap = Maps.newHashMap();
		/**未读消息数**/
		if(paramsMap.get("newUnreadMsgMthod")!=null && "true".equals(paramsMap.get("newUnreadMsgMthod"))){
			//改造后获取未读消息数的方法
			// update by wangjf 2015-12-14
			// 1) 新增“站内消息总数”
			// 2) 新增“未读公告数”
			// 3) 新增“公告总数”
			int count = systemMessageInfoRepository.unreadMessageCount((String)paramsMap.get("custId"));
			resultMap.put("unReadMsgNum", count);
			resultMap.put("allMsgNum", systemMessageInfoRepository.countAllMessage((String)paramsMap.get("custId")));	
			resultMap.putAll(afficheInfoService.countAffiche(paramsMap));
		}else{
			List<Object[]> list = systemMessageInfoRepository.unreadMessage4Cust((String)paramsMap.get("custId"));
			if(list != null && list.size() > 0 && list.get(0) != null && ( (Object[])list.get(0)).length > 1)
				resultMap.put("unReadMsgNum", null != list.get(0)[1]?String.valueOf(list.get(0)[1]):"0");
		}
		
		/**获取累计收益 修改为活期宝累计收益加上体验宝累计收益**/
		resultMap.put("sumTradeAmount", getSumTradeAmount(paramsMap));
		
		/**获取昨日收益修改为活期宝收益加上体验宝收益**/
		paramsMap.put("tradeDate", DateUtils.getAfterDay(new Date(), -1));
		/**活期宝收益 体验宝收益 定期宝到期收益**/
		paramsMap.put("tradeType", Arrays.asList(SubjectConstant.TRADE_FLOW_TYPE_01,SubjectConstant.TRADE_FLOW_TYPE_03,SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_INCOME));
		resultMap.put("yesterdayTradeAmount",accountFlowInfoRepositoryCustom.findIncomeByCustId(paramsMap).toPlainString() );
		
		/**持有份额**/
		resultMap.put("shareHoldingAmount", accountFlowInfoRepositoryCustom.getShareHoldingAmount(paramsMap));
		
		/**账户总览=可用金额+冻结金额+定期宝在投金额+活期宝持有份额**/
		BigDecimal sumAccountAmount = BigDecimal.ZERO;
		
		/**获取可用余额**/
		ResultVo accountVo = accountService.findAccountInfo(paramsMap);
		if(ResultVo.isSuccess(accountVo)){
			AccountInfoEntity accountInfo = (AccountInfoEntity)accountVo.getValue("data");
			resultMap.put("accountAmount", accountInfo.getAccountAvailableAmount());
			resultMap.put("freezeAmount", accountInfo.getAccountFreezeAmount());
			/**可用金额+冻结金额**/
			sumAccountAmount = ArithUtil.add(sumAccountAmount, accountInfo.getAccountAvailableAmount()); 
			sumAccountAmount = ArithUtil.add(sumAccountAmount, accountInfo.getAccountFreezeAmount()); 
		}
		sumAccountAmount = ArithUtil.add(sumAccountAmount,accountFlowInfoRepositoryCustom.getHoldingAmount((String)paramsMap.get("custId"), Arrays.asList(Constant.PRODUCT_TYPE_01)));
		sumAccountAmount = ArithUtil.add(sumAccountAmount,investInfoRepository.getInvestingAmout((String)paramsMap.get("custId"), Constant.PRODUCT_TYPE_04));
		resultMap.put("sumAccountAmount", sumAccountAmount);
		resultVo.putValue("data", resultMap);
		return resultVo;
	}
	
	/**
	 * 账户总览
	 */
	@Override
	public ResultVo accountALL(Map<String, Object> paramsMap)throws SLException {
		
		ResultVo resultVo = new ResultVo(true);
		Map<String,Object> resultMap = Maps.newHashMap();
		
		/**账户总览=可用金额+冻结金额+定期宝在投金额+活期宝持有份额**/
		BigDecimal sumAccountAmount = BigDecimal.ZERO;
		
		/**获取可用余额和冻结余额**/
		ResultVo accountVo = accountService.findAccountInfo(paramsMap);
		if(ResultVo.isSuccess(accountVo)){
			AccountInfoEntity accountInfo = (AccountInfoEntity)accountVo.getValue("data");
			resultMap.put("enableAccountAmount", accountInfo.getAccountAvailableAmount());
			resultMap.put("freezeAmount", accountInfo.getAccountFreezeAmount());
			sumAccountAmount = ArithUtil.add(sumAccountAmount, ArithUtil.formatScale2(accountInfo.getAccountAvailableAmount())); 
			sumAccountAmount = ArithUtil.add(sumAccountAmount, ArithUtil.formatScale2(accountInfo.getAccountFreezeAmount())); 
		}
		sumAccountAmount = ArithUtil.add(sumAccountAmount,accountFlowInfoRepositoryCustom.getHoldingAmount((String)paramsMap.get("custId"), Arrays.asList(Constant.PRODUCT_TYPE_01)));
		sumAccountAmount = ArithUtil.add(sumAccountAmount,investInfoRepository.getInvestingAmout((String)paramsMap.get("custId"), Constant.PRODUCT_TYPE_04));
		/**计算账户总览金额**/
		resultMap.put("sumAccountAmount", sumAccountAmount);
		
		/**可用体验金相关信息**/ 
		Map<String,Object> expGoldMap = custActivityInfoService.findExperienceGoldById(paramsMap);
		resultMap.put("enableTasteAmount", null != expGoldMap ? expGoldMap.get("ExperienceGoldUnUsed"):0);
		
		/**待结算现金奖励**/
		Map<String,Object> rewardMap = custActivityInfoService.findRewardById(paramsMap);
		resultMap.put("rewardNotSettle", null != rewardMap ? rewardMap.get("rewardNotSettle"):0);
		
		/**获取累计收益**/
		resultMap.put("sumTradeAmount", getSumTradeAmount(paramsMap));
		
		paramsMap.put("tradeDate", DateUtils.getAfterDay(new Date(), -1));
		/**活期宝收益 体验宝收益 定期宝到期收益**/
		paramsMap.put("tradeType", Arrays.asList(SubjectConstant.TRADE_FLOW_TYPE_01,SubjectConstant.TRADE_FLOW_TYPE_03,SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_INCOME));
		resultMap.put("yesterdayTradeAmount",accountFlowInfoRepositoryCustom.findIncomeByCustId(paramsMap).toPlainString());
		resultVo.putValue("data", resultMap);
		return resultVo;
	}

	/**
	 * 交易记录
	 */
	@Override
	public ResultVo tradeListALL(Map<String, Object> paramsMap)throws SLException {
		paramsMap.put("pageNumber", paramsMap.get("pageNum"));
		Page<AccountFlowInfoEntity> flowInfoPage = accountFlowService.findAccountFlowInfoPagable(paramsMap);
		ResultVo tradeResult = new ResultVo(true);
		tradeResult.putValue("data", PageFuns.pageVoToMap(flowInfoPage));
		return tradeResult;
	}

	/**
	 * 消息列表 new
	 */
	@Override
	public ResultVo messageListALLNew(Map<String, Object> paramsMap)throws SLException {
		ResultVo result = new ResultVo(true);
		try {
			Page<Map<String, Object>> page = systemMessageRepositoryCustom.findAllByReceiveCustId(paramsMap);
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("data", page.getContent());
			data.put("iTotalDisplayRecords", page.getTotalElements());
			data.put("unReadMsgNum", systemMessageInfoRepository.unreadMessageCount((String)paramsMap.get("custId")));
			result.putValue("data", data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 消息列表 new By salesMan APP
	 * @author  lyy
	 * @date    2016-11-14 19:39:40
	 */
	@Override
	public ResultVo messageListBySalesMan(Map<String, Object> paramsMap)throws SLException {
		ResultVo result = new ResultVo(true);
		try {
			Page<Map<String, Object>> page = systemMessageRepositoryCustom.findAllByReceiveCustIdOrSalesMan(paramsMap);
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("data", page.getContent());
			data.put("iTotalDisplayRecords", page.getTotalElements());
			data.put("unReadMsgNum", systemMessageInfoRepository.unreadMessageCountBySalesMan((String)paramsMap.get("custId")));
			result.putValue("data", data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 未读消息 By salesMan APP
	 * @author  lyy
	 * @date    2016-11-14 19:39:40
	 */
	@Override
	public ResultVo unreadMessageCountBySalesMan(Map<String, Object> paramsMap) {
		if(StringUtils.isEmpty((String)paramsMap.get("custId"))){
			throw new IllegalArgumentException("custId不能为空");
		}
		
		int count = systemMessageInfoRepository.unreadMessageCountBySalesMan(paramsMap.get("custId").toString());
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("count", count);
		return new ResultVo(true, "操作成功", data);
	}
	
	/**
	 * 消息列表
	 */
	@Override
	public ResultVo messageListALL(Map<String, Object> paramsMap)throws SLException {
		/*
			--NEW--
		ResultVo result = new ResultVo(true);
		try {
			Page<Map<String, Object>> page = systemMessageRepositoryCustom.findAllByReceiveCustId(paramsMap);
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("data", page.getContent());
			data.put("iTotalDisplayRecords", page.getTotalElements());
			result.putValue("data", data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;*/
		
		PageRequest pageRequest = new PageRequest(Integer.parseInt((String)paramsMap.get("pageNum")), Integer.parseInt((String)paramsMap.get("pageSize")));
		ResultVo result = new ResultVo(true);
		result.putValue("data", PageFuns.pageVoToMap(systemMessageInfoRepository.findByReceiveCustId((String)paramsMap.get("custId"), pageRequest)));
		return result;
	}
	
	/**
	 * 更新用户已读状态
	 * 
	 * @param custId  客户id
	 * 		  id      消息id
	 *        messageType 消息类型
	 * @return ResultVo
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo updateIsReadNew(Map<String,Object> paramsMap)throws SLException{
		String custId = paramsMap.get("custId") + "";
		String messageId = paramsMap.get("id") + "";
		
		if(!StringUtils.isEmpty((String)paramsMap.get("messageType")) 
				&& Constant.AFFICHE_TYPE_ALL.equals((String)paramsMap.get("messageType"))) {
			SystemMessageReadInfoEntity smri = systemMessageReadInfoRepository.findOneByRelatePrimaryAndCustId(messageId,custId);
			if(smri!=null){
				return new ResultVo(false,"已经为'已读'状态", paramsMap);
			}
			afficheInfoService.readAffiche(paramsMap);
		}
		else {
			SystemMessageReadInfoEntity smri = systemMessageReadInfoRepository.findOneByMessageIdAndCustId(messageId,custId);
			if(smri!=null){
				return new ResultVo(false,"已经为'已读'状态", paramsMap);
			}
			SystemMessageReadInfoEntity smri2 = new SystemMessageReadInfoEntity();
			CustInfoEntity receiveCust = new CustInfoEntity();
			receiveCust.setId(custId);
			SystemMessageInfoEntity message = new SystemMessageInfoEntity();
			message.setId(messageId);
			smri2.setReceiveCust(receiveCust);
			smri2.setMessage(message);
			smri2.setIsRead(Constant.SITE_MESSAGE_ISREAD);
			smri2.setCreateDate(new Timestamp(System.currentTimeMillis()));
			smri2.setCreateUser(custId);
			systemMessageReadInfoRepository.save(smri2);
		}
		
		return new ResultVo(true, "操作成功", paramsMap);
	}
	
	/**
	 * 更新用户已读状态
	 * 
	 * @param custId  客户id
	 * 		  id      消息id
	 * @return ResultVo
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo updateIsRead(Map<String,Object> paramsMap)throws SLException{
		SystemMessageInfoEntity message = systemMessageInfoRepository.findOne((String)paramsMap.get("id"));
		SystemMessageInfoEntity messageUpd = new SystemMessageInfoEntity();
		messageUpd.setIsRead(Constant.SITE_MESSAGE_ISREAD);
		messageUpd.setBasicModelProperty((String)paramsMap.get("custId"), false);
		if(!message.updateMessage(messageUpd))
			return new ResultVo(false);
		return new ResultVo(true,"操作成功",paramsMap);
	}

	/**
	 * 用户加入记录、赎回记录、收益记录
	 */
	public ResultVo tradeListALLInfo(Map<String, Object> paramsMap)throws SLException{
		paramsMap.put("pageNum", Integer.parseInt(paramsMap.get("pageNum") ==null ? "0":(String) paramsMap.get("pageNum")));
		paramsMap.put("pageSize", Integer.parseInt(paramsMap.get("pageSize") == null ? "0":(String) paramsMap.get("pageSize")));
		paramsMap.put("pageNum", 0 == (int)paramsMap.get("pageNum") ? paramsMap.get("pageNum") : (int)paramsMap.get("pageNum") * (int)paramsMap.get("pageSize")) ;
		ResultVo tradeResult = new ResultVo(true);
		tradeResult.putValue("data", PageFuns.pageVoToMap(accountFlowInfoRepositoryCustom.getUnionAccountFlowPage(paramsMap)));
		return tradeResult;
	}
	
	
	/**
	 * 获取金牌推荐人统计信息
	 */
	public ResultVo findCustCommissionListMobile(Map<String, Object> paramsMap)throws SLException{
		paramsMap.put("start", Integer.parseInt(paramsMap.get("pageNum") ==null ? "0":(String) paramsMap.get("pageNum")));
		paramsMap.put("length", Integer.parseInt(paramsMap.get("pageSize") == null ? "0":(String) paramsMap.get("pageSize")));
		paramsMap.put("start", 0 == Integer.parseInt((String)paramsMap.get("pageNum")) ?  Integer.parseInt((String)paramsMap.get("pageNum")) : Integer.parseInt((String)paramsMap.get("pageNum")) * Integer.parseInt((String)paramsMap.get("pageSize"))) ;
		ResultVo tradeResult = new ResultVo(true);
		Map<String, Object> result =  PageFuns.pageVoToMap(custActivityInfoRepositoryCustom.findCustCommissionList(paramsMap));
		result.put("total", custActivityInfoRepositoryCustom.findCustCommissionInfo(paramsMap));
		tradeResult.putValue("data",result);
		return tradeResult;
	}
	/**
	 * 获取我的佣金详情记录
	 */
	public ResultVo findCustCommissionDetailListMobile(Map<String, Object> paramsMap)throws SLException{
		paramsMap.put("start", Integer.parseInt(paramsMap.get("pageNum") ==null ? "0":(String) paramsMap.get("pageNum")));
		paramsMap.put("length", Integer.parseInt(paramsMap.get("pageSize") == null ? "0":(String) paramsMap.get("pageSize")));
		paramsMap.put("start", 0 == Integer.parseInt((String)paramsMap.get("pageNum")) ?  Integer.parseInt((String)paramsMap.get("pageNum")) : Integer.parseInt((String)paramsMap.get("pageNum")) * Integer.parseInt((String)paramsMap.get("pageSize"))) ;
		ResultVo tradeResult = new ResultVo(true);
		tradeResult.putValue("data", PageFuns.pageVoToMap(custActivityInfoRepositoryCustom.findCustCommissionDetailList(paramsMap)));
		return tradeResult;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo queryAccount(Map<String, Object> params) throws SLException {
		ResultVo resultVo = customerService.findTotalIncomeByCustId(params);
		BigDecimal sumAccountAmount = (BigDecimal)((Map<String, Object>)resultVo.getValue("data")).get("sumAccountAmount");
		resultVo = customerService.findBaoCountInfoByCustId(params);
		Map<String, Object> dataMap = (Map<String, Object>)resultVo.getValue("data");
		dataMap.put("sumAccountAmount", sumAccountAmount);
		List<ProductRateInfoEntity> prodList= productRateInfoRepository.findProductRateInfoByTypeName((String)params.get("productType"));
		if(prodList == null || prodList.size() == 0){
			dataMap.put("yearRate", new BigDecimal("0"));
		} 
		else {
			dataMap.put("yearRate", prodList.get(0).getYearRate());
		}
		return resultVo;
	}

	@Override
	public ResultVo queryBank(Map<String, Object> params) throws SLException {
		
		Map<String, Object> resultMap = bankCardService.queryBankCardDetail(params);
		CustInfoEntity custInfoEntity = customerService.findByCustId((String)params.get("custId"));
		resultMap.put("custName", custInfoEntity.getCustName());
		resultMap.put("credentialsCode", custInfoEntity.getCredentialsCode());
		AccountInfoEntity accountInfoEntity = accountService.findByCustId((String)params.get("custId"));
		resultMap.put("withdrawalAmount", accountInfoEntity.getAccountAvailableAmount());
		resultMap.put("withdrawalExpense", paramService.findWithDrawExpenseAmount());
		resultMap.put("unBindState", appBankService.getBankCardState((String)resultMap.get("id")));
		resultMap.put("custType", custInfoEntity.getCustType());
		return new ResultVo(true, "查询银行信息", resultMap);
	}

	@Override
	public ResultVo querySupportBank() throws SLException {
		Map<String, Object> params = Maps.newConcurrentMap();
//		Map<String, Object> resultMap = bankCardService.querySupportBank(params);
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//		Iterator<Entry<String, Object>> iter = resultMap.entrySet().iterator();
//		while(iter.hasNext()) {
//			Entry<String, Object> entry = iter.next();
//			Map<String, Object> bankMap = new HashMap<String, Object>();
//			bankMap.put("bankCode", entry.getKey());
//			bankMap.put("bankName", (String)entry.getValue());
//			list.add(bankMap);
//		}
//		return new ResultVo(true, "查询第三方支持的银行卡", list);
		params.put("type", "authPayBankLLPay");
		return bankCardService.queryRechargeBankList(params);
	}

	@Override
	public ResultVo queryThirdBankByCardNo(Map<String, Object> paramsMap)
			throws SLException {
		Map<String, Object> resultMap = bankCardService.queryThirdBankByCardNo(paramsMap);
		return new ResultVo(true, "根据银行卡号从第三方查询", resultMap);
	}
	
//----------------私有方法区------------------------------------------------------------------------------------------------------------------

	private BigDecimal getSumTradeAmount(Map<String,Object> paramsMap) {
		//活期宝累计收益
		paramsMap.put("tradeType", Arrays.asList(SubjectConstant.TRADE_FLOW_TYPE_01,SubjectConstant.TRADE_FLOW_TYPE_03));
		BigDecimal sumTradeAmount = accountFlowInfoRepositoryCustom.findIncomeByCustId(paramsMap);
		return ArithUtil.add( sumTradeAmount , fixedInvestRepositoryCustom.getAtonedAmount((String)paramsMap.get("custId"),  Arrays.asList(Constant.PRODUCT_TYPE_04), Arrays.asList(Constant.TERM_INVEST_STATUS_FINISH,Constant.TERM_INVEST_STATUS_ADVANCE_FINISH)));
	}

	@Override
	public ResultVo tradeFlowList(Map<String, Object> paramsMap)
			throws SLException {
		paramsMap.put("pageNumber", paramsMap.get("pageNum"));
		Page<TradeFlowInfoEntity> flowInfoPage = tradeFlowService.findTradeFlowInfoPagable(paramsMap);
		ResultVo tradeResult = new ResultVo(true);
		tradeResult.putValue("data", PageFuns.pageVoToMap(flowInfoPage));
		return tradeResult;
	}

	@Override
	public ResultVo queryWithdrawalAmount(Map<String, Object> params)
			throws SLException {
		
		Map<String, Object> resultMap = Maps.newHashMap();
		CustInfoEntity custInfoEntity = customerService.findByCustId((String)params.get("custId"));
		resultMap.put("custName", custInfoEntity.getCustName());
		resultMap.put("credentialsCode", custInfoEntity.getCredentialsCode());
		AccountInfoEntity accountInfoEntity = accountService.findByCustId((String)params.get("custId"));
		resultMap.put("withdrawalAmount", accountInfoEntity.getAccountAvailableAmount());
		resultMap.put("withdrawalExpense", paramService.findWithDrawExpenseAmount());
		return new ResultVo(true, "查询银行信息", resultMap);
	}
}
