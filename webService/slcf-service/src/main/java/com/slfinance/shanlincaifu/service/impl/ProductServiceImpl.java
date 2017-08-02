/** 
 * @(#)ProductServiceImpl.java 1.0.0 2015年5月1日 上午10:44:07  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import com.google.common.collect.Lists;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.DailyInterestInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.ProductDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.DailySettlementRepository;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.LoanInfoService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.ProductService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.BizUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SharedUtil;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.util.BeanMapConvertUtil;
import com.slfinance.vo.ResultVo;

/**   
 * 产品服务实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月1日 上午10:44:07 $ 
 */
@Slf4j
@Service("productService")
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductInfoRepository productInfoRepository;
	
	@Autowired
	private ProductDetailInfoRepository productDetailInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private SubAccountInfoRepository subAccountInfoRepository;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private AccountFlowInfoRepository accountFlowInfoRepository;
	
//	@Autowired
//	private AccountFlowDetailRepository accountFlowDetailRepository;
//	
//	@Autowired
//	private FlowBusiRelationRepository flowBusiRelationRepository;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private DailySettlementRepository dailySettlementRepository;
	
	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private InvestInfoRepository investInfoRepository;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	
	@Autowired
	private AccountFlowService accountFlowService;
	
	@Autowired
	SMSService smsService;
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo closeJob(Map<String, Object> param) throws SLException {
		
		// 1 产品状态设置为已满额
		ProductInfoEntity productInfoEntity = productInfoRepository.findProductInfoByProductTypeName(Constant.PRODUCT_TYPE_01);
		if(productInfoEntity == null) {
			throw new SLException("关闭失败！产品不存在！");
		}
		
		if(!productInfoEntity.getProductStatus().equals(Constant.PRODUCT_STATUS_BID_ING)){
			log.warn("关闭失败！产品状态非开放中，无法关闭！");
			return new ResultVo(false, "关闭失败！产品状态非开放中，无法关闭！");
		}
		
		productInfoEntity.setProductStatus(Constant.PRODUCT_STATUS_BID_FINISH);// 产品状态设置为关闭
		productInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		
		// 2 当前产品可用价值改为0
		ProductDetailInfoEntity productDetailInfoEntity = productDetailInfoRepository.findByProductId(productInfoEntity.getId());
		if(productDetailInfoEntity == null) {
			throw new SLException("关闭失败！产品详情不存在！");
		}
		
		productDetailInfoEntity.setCurrUsableValue(new BigDecimal("0"));//可用价值改为0
		productDetailInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
		return new ResultVo(true);
	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo recoverAtoneLimited(Map<String, Object> param)
			throws SLException {
		
		// 1 产品状态设置为已满额
		ProductInfoEntity productInfoEntity = productInfoRepository.findProductInfoByProductTypeName(Constant.PRODUCT_TYPE_01);
		if(productInfoEntity == null) {
			throw new SLException("产品不存在！");
		}
		
		if(!productInfoEntity.getProductStatus().equals(Constant.PRODUCT_STATUS_BID_FINISH)){
			log.warn("产品为非满额状态，暂时不能恢复提现额度！");
			return new ResultVo(false, "产品为非满额状态，暂时不能恢复提现额度！");
		}

		// 3 恢复提现金额
		SubAccountInfoEntity earnSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_ERAN); // 公司收益分账户
		AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(earnSubAccount.getAccountId()); // 公司收益主账户
		boolean fixLimited = paramService.findFixLimited();// 提现保证金是否固定额度（true固定，false比例）
		BigDecimal fixLimitedAmount = paramService.findFixLimitedAmount();// 固定额度提现保证金
		
		if(fixLimited){ // 按固定额度
			if(earnSubAccount.getAccountAmount().compareTo(fixLimitedAmount) < 0){ // 若分账户金额小于固定额度，则从主账户中打钱到分账户
				
				String reqeustNo = numberService.generateTradeBatchNumber();
				// 可转入收益分账户金额
				BigDecimal transferAmount = ArithUtil.sub(fixLimitedAmount, earnSubAccount.getAccountAmount());
				// 7.3.1 更新收益分账户
				earnSubAccount.setAccountAmount(ArithUtil.add(earnSubAccount.getAccountAmount(), transferAmount));
				earnSubAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
				
				// 7.3.2 记录收益分账户流水
				accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, earnMainAccount, earnSubAccount, "3", 
						SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW_LIMITED, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
						transferAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_WITHDRAW_LIMITED, null, null, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				
				// 7.3.3 更新收益主账户
				earnMainAccount.setAccountTotalAmount(ArithUtil.sub(earnMainAccount.getAccountTotalAmount(), transferAmount));
				earnMainAccount.setAccountAvailableAmount(ArithUtil.sub(earnMainAccount.getAccountAvailableAmount(), transferAmount));
				earnMainAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
				
				// 7.3.4 记录收益主账户流水
				accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, earnMainAccount, earnSubAccount, "2", 
						SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW_LIMITED, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
						transferAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_WITHDRAW_LIMITED, null, null, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			}
		}
		
		return new ResultVo(true);
	}

	/**
	 * 活期宝每日结息
	 * 步骤：1.计算活期宝实际总利息
	 *     2.计算活期宝预计利息
	 *     3.获取客户分账信息列表
	 *     4.循环分账列表，计算每个分账的预计利息和实际利息
	 *     5.生成流水信息
	 *     6.更新客户分账信息
	 * 	   7.批量插入结息表
	 * @author linhj
	 * @throws SLException 
	 * @date 2015年05月01日 下午午15:43:41
	*/
//	@Transactional(readOnly = false, rollbackFor = SLException.class)
//	@Override
//	public void currentDailySettlement(Date now) throws SLException {
//		Map<String,Object> params=new HashMap<String,Object>();
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
//		Date preDate=new Date(now.getTime() - DateUtils.DAY_MILLIS);
//		/*当前执行时间*/
//		String execDate="";
//		/*前一天时间*/
//		String preExecDate="";
//		
//		/* 判断每日结息每天重复执行 */
//		params.put("currDate", sdf.format(now));
//		/**产品类型-活期宝  */
//		params.put("typeName", Constant.PRODUCT_TYPE_01);
//		int count = dailySettlementRepository.findCurrDateRecordCount(params);
//		if (count > 0) {
//			log.error("每日结息不能重复执行");
//			throw new SLException("每日结息不能重复执行");
//		}
//		/* 定义当天时间 */
//		execDate = sdf.format(now);
//		/* 定义前一天时间 */
//		preExecDate = sdf.format(now.getTime() - DateUtils.DAY_MILLIS);
//		
//		params.put("execDate", execDate);
//		params.put("preExecDate", preExecDate);
//		loanInfoService.saveDailyLoanAndValue(preDate,Constant.PRODUCT_TYPE_01);
//		/*活期宝总价值*/
//		BigDecimal totalPV=dailySettlementRepository.findTotalPV(params);
//		/*活期宝实际总利息*/
//		BigDecimal totalActualInterest=dailySettlementRepository.findActualInterest(params);
//		/*活期宝预计利息*/
//		params.put("totalPV", totalPV);
//		//BigDecimal totalExceptInterest=dailySettlementRepository.findExceptInterest(params);
//		List<Map<String,Object>> exceptInterestList=dailySettlementRepository.findExceptInterest(params);
//		/*还款未处理金额*/
//		BigDecimal untreatedAmount=dailySettlementRepository.untreatedAmount();
//		/*查询投资有效的分账户信息*/
//		List<Map<String,Object>> subAccountList=dailySettlementRepository.findSubAccount(params);
//		
//		List<DailyInterestInfoEntity> list=new ArrayList<DailyInterestInfoEntity>();
//		 
//		 /** 公司收益分账户*/
//		SubAccountInfoEntity earnSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_ERAN);
//		/*公司收益账户的可用价值*/
//		BigDecimal accountAvailableValue=earnSubAccount.getAccountAvailableValue();
//		 /** 公司居间人分账户*/
//		SubAccountInfoEntity centerSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_CENTER);
//		BigDecimal totalEarn=new BigDecimal("0");
//		sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		Map<String,String> requestNOMap=new HashMap<String,String>();
//		for(Map<String,Object> subAccount:subAccountList){
//			DailyInterestInfoEntity dailyInterest=new DailyInterestInfoEntity();
//			
//			/*账户总价值*/
//			BigDecimal accountTotalValue=new BigDecimal(subAccount.get("accountTotalValue").toString());
//			/*账户可用价值*/
//			BigDecimal accountValue=new BigDecimal(subAccount.get("accountAvailableValue").toString());
//			/*账户冻结价值*/
//			BigDecimal accountFreezeValue=new BigDecimal(subAccount.get("accountFreezeValue").toString());
//			/*若客户分账户冻结价值大于0，则收益计入公司收益账户*/
//			BigDecimal freezeValueEarn=new BigDecimal("0");
//			if(CompareHelper.greaterThan(accountFreezeValue, new BigDecimal("0"))){
//				freezeValueEarn=getFreezeValueEarn(accountFreezeValue,totalPV,untreatedAmount,totalActualInterest);
//			}
//			/*客户分账户预计利息*/
//			//BigDecimal exceptInterest=ArithUtil.div(accountValue, totalPV.add(untreatedAmount)).multiply(totalExceptInterest);
//			BigDecimal exceptInterest=new BigDecimal("0");
//			for(Map<String,Object> exceptInterestMap:exceptInterestList){
//				if(exceptInterestMap.get("id").equals(subAccount.get("id"))){
//					exceptInterest=new BigDecimal(exceptInterestMap.get("exceptInterest").toString());
//					break;
//				}
//			}
//			/*客户分账户实际利息*/
//			BigDecimal actualInterest=ArithUtil.div(accountValue, totalPV.add(untreatedAmount)).multiply(totalActualInterest);
//			String reqeustNo = "";
//			//同一个客户的收益，记录流水时，REQUEST_NO要是同一个
//			if(requestNOMap.containsKey(subAccount.get("custId").toString())){
//				reqeustNo=requestNOMap.get(subAccount.get("custId"));
//			}else{
//				reqeustNo = numberService.generateTradeBatchNumber();
//				requestNOMap.put(subAccount.get("custId").toString(), reqeustNo);
//			}
//			actualInterest=ArithUtil.formatScale(actualInterest, 8);
//			exceptInterest=ArithUtil.formatScale(exceptInterest, 8);
//			accountTotalValue=ArithUtil.formatScale(accountTotalValue, 8);
//			accountValue=ArithUtil.formatScale(accountValue, 8);
//			accountFreezeValue=ArithUtil.formatScale(accountFreezeValue, 8);	
//			freezeValueEarn=ArithUtil.formatScale(freezeValueEarn, 8);
//			
//			/*客户收益额*/
//			BigDecimal tradeAmount=new BigDecimal("0");
//			/*公司收益额*/
//			BigDecimal earn=new BigDecimal("0");
//			//AccountInfoEntity account=accountInfoRepository.findOne(subAccount.get("accountId").toString());
//			if(CompareHelper.greaterThan(exceptInterest,actualInterest)){
//				tradeAmount=actualInterest;
//			}else{
//				tradeAmount=exceptInterest;
//				earn=actualInterest.subtract(exceptInterest);
//			}
//			tradeAmount=ArithUtil.formatScale(tradeAmount, 8);
//			earn=ArithUtil.formatScale(earn, 8);
//			/*若投资时间早于15点，则正常计息，否则客户计息表的利息为0*/ 
//			if(!execDate.equals(subAccount.get("investDate").toString())){
//				dailyInterest.setExpectInterest(exceptInterest);
//				dailyInterest.setFactInterest(actualInterest.add(freezeValueEarn));
//				dailyInterest.setFactGainInterest(tradeAmount);//实际收益
//			}else{
//				dailyInterest.setExpectInterest(exceptInterest);
//				dailyInterest.setFactInterest(actualInterest.add(freezeValueEarn));
//				dailyInterest.setFactGainInterest(new BigDecimal("0"));//实际收益给公司了
//			} 
//			dailyInterest.setCurrDate(execDate);
//			dailyInterest.setSubAccountId(subAccount.get("id").toString());
//			dailyInterest.setCreateDate(new Date());
//			list.add(dailyInterest);
//			SubAccountInfoEntity custSubAccount=BeanMapConvertUtil.toBean(SubAccountInfoEntity.class, subAccount);
//			/*若投资时间早于15点，则正常记录客户分账流水和更新分账价值，否则不更新*/ 
//			if(!execDate.equals(subAccount.get("investDate").toString())){
//				custSubAccount.setAccountTotalValue(ArithUtil.formatScale(accountTotalValue.add(tradeAmount),8));
//				custSubAccount.setAccountAvailableValue(ArithUtil.formatScale(accountValue.add(tradeAmount),8));
//				/* 记录客户分账户流水 */
//				BizUtil.saveAccountFlow(null,custSubAccount, SubjectConstant.TRADE_FLOW_TYPE_01,reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,ArithUtil.formatScale(tradeAmount,8), SubjectConstant.TRADE_FLOW_TYPE_01, null,Constant.PRODUCT_TYPE_01,"",preDate,numberService,accountFlowInfoRepository,accountFlowDetailRepository,flowBusiRelationRepository,SubjectConstant.SUBJECT_TYPE_VALUE);
//				/* 更新客户分账的价值 */
//				subAccountInfoRepository.updateById(ArithUtil.formatScale(tradeAmount.add(accountTotalValue),8),ArithUtil.formatScale(tradeAmount.add(accountValue),8),new Date(),subAccount.get("id").toString());
//			}else{
//				earn=actualInterest;//投资时间晚于昨天15点则收益归公司
//			}
//			/*公司分账增加金额*/
//			earnSubAccount.setAccountTotalValue(ArithUtil.formatScale(earnSubAccount.getAccountTotalValue(),8).add(earn.add(freezeValueEarn)));
//			earnSubAccount.setAccountAvailableValue(ArithUtil.formatScale(earnSubAccount.getAccountAvailableValue(),8).add(earn.add(freezeValueEarn)));
//			/* 记录公司收益分账户流水 */
//			BizUtil.saveAccountFlow(null,earnSubAccount,SubjectConstant.TRADE_FLOW_TYPE_COMPANY,reqeustNo,
//					SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,ArithUtil.formatScale(earn.add(freezeValueEarn),8), SubjectConstant.TRADE_FLOW_TYPE_COMPANY,"",Constant.PRODUCT_TYPE_01,custSubAccount.getId(),preDate,numberService,accountFlowInfoRepository,accountFlowDetailRepository,flowBusiRelationRepository,SubjectConstant.SUBJECT_TYPE_VALUE);
//			
//			totalEarn=totalEarn.add(earn.add(freezeValueEarn));
//			/*更新公司收益分账的价值*/
//			//subAccountInfoRepository.updateById(earnSubAccount.getAccountTotalValue().add(earn.add(freezeValueEarn)), earnSubAccount.getId());
//		}
//		/*公司居间人分账户结息,不累计客户的利息差*/
//		companyInterest(centerSubAccount,centerSubAccount.getAccountAvailableValue(),totalPV,untreatedAmount,totalActualInterest,execDate,list,new BigDecimal("0"),preDate);
//		/*公司收益分账户结息，累计客户的利息差*/
//		companyInterest(earnSubAccount,accountAvailableValue,totalPV,untreatedAmount,totalActualInterest,execDate,list,totalEarn,preDate);
//		/*批量插入结息表*/
//		dailySettlementRepository.batchInsert(list);
//		
//	}
	
//	/**
//	 * 公司分账户（居间人账户和收益账户结息）
//	 */
//	private void companyInterest(SubAccountInfoEntity subAccount,BigDecimal accountAvailableValue,BigDecimal totalPV,BigDecimal untreatedAmount,BigDecimal totalActualInterest,String execDate,List<DailyInterestInfoEntity> list,BigDecimal totalEarn,Date preDate){
//		/*公司分账户实际利息和预计利息相同*/
//		BigDecimal actualInterest=ArithUtil.div(accountAvailableValue, totalPV.add(untreatedAmount)).multiply(totalActualInterest);
//		/*构造结息表数据*/
//		actualInterest=ArithUtil.formatScale(actualInterest, 8);
//		DailyInterestInfoEntity dailyInterest=new DailyInterestInfoEntity();
//		dailyInterest.setCurrDate(execDate);
//		dailyInterest.setExpectInterest(actualInterest);
//		dailyInterest.setFactInterest(actualInterest);
//		dailyInterest.setFactGainInterest(actualInterest.add(ArithUtil.formatScale(totalEarn,8)));
//		dailyInterest.setSubAccountId(subAccount.getId());
//		dailyInterest.setCreateDate(new Date());
//		list.add(dailyInterest);
//		
//		subAccount.setAccountTotalValue(ArithUtil.formatScale(subAccount.getAccountTotalValue(),8).add(actualInterest));
//		subAccount.setAccountAvailableValue(ArithUtil.formatScale(subAccount.getAccountAvailableValue(),8).add(actualInterest));
//		/* 记录公司收益分账户流水 */
//		BizUtil.saveAccountFlow(null,subAccount,SubjectConstant.TRADE_FLOW_TYPE_COMPANY,numberService.generateTradeBatchNumber(),
//				SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,ArithUtil.formatScale(actualInterest,8), SubjectConstant.TRADE_FLOW_TYPE_COMPANY,"",Constant.PRODUCT_TYPE_01,"",preDate,numberService,accountFlowInfoRepository,accountFlowDetailRepository,flowBusiRelationRepository,SubjectConstant.SUBJECT_TYPE_VALUE);
//		/*更新公司分账的价值*/
//		subAccountInfoRepository.updateById(ArithUtil.formatScale(subAccount.getAccountTotalValue(),8),ArithUtil.formatScale(subAccount.getAccountAvailableValue(),8), new Date(),subAccount.getId());	
//	}
//	
//	/**
//	 * 获取客户分账户冻结价值的利息收益
//	 */
//	private BigDecimal getFreezeValueEarn(BigDecimal accountValue,BigDecimal totalPV,BigDecimal untreatedAmount,BigDecimal totalActualInterest){
//		/*客户分账户实际利息*/
//		BigDecimal freezeValueEarn=ArithUtil.div(accountValue, totalPV.add(untreatedAmount)).multiply(totalActualInterest);
//		return freezeValueEarn;
//	}
	

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public void currentDailySettlement(Date now) throws SLException {
		Map<String,Object> params=new HashMap<String,Object>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		Date preDate=new Date(now.getTime() - DateUtils.DAY_MILLIS);
		/*当前执行时间*/
		String execDate="";
		/*前一天时间*/
		String preExecDate="";
		
		/* 判断每日结息每天重复执行 */
		params.put("currDate", sdf.format(now));
		/**产品类型-活期宝  */
		params.put("typeName", Constant.PRODUCT_TYPE_01);
		int count = dailySettlementRepository.findCurrDateRecordCount(params);
		if (count > 0) {
			log.error("每日结息不能重复执行");
			throw new SLException("每日结息不能重复执行");
		}
		/* 定义当天时间 */
		execDate = sdf.format(now);
		/* 定义前一天时间 */
		preExecDate = sdf.format(now.getTime() - DateUtils.DAY_MILLIS);
		
		params.put("execDate", execDate);
		params.put("preExecDate", preExecDate);
		
		loanInfoService.saveDailyLoanAndValue(preDate,Constant.PRODUCT_TYPE_01);
		
		/*活期宝总价值*/
		BigDecimal totalPV=dailySettlementRepository.findTotalPV(params);
		/*活期宝实际总利息*/
		BigDecimal totalActualInterest=dailySettlementRepository.findActualInterest(params);
		/*还款未处理金额*/
		BigDecimal untreatedAmount=dailySettlementRepository.untreatedAmount(params);
		/*活期宝预计利息*/
		params.put("totalPV", totalPV);
		params.put("untreatedAmount", untreatedAmount);
		params.put("totalActualInterest", totalActualInterest);
		
		 /** 公司收益分账户*/
		SubAccountInfoEntity earnSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_ERAN);
		 /** 公司居间人分账户*/
		SubAccountInfoEntity centerSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_CENTER);
		 /** 公司收益汇总 */
		BigDecimal totalEarn=new BigDecimal("0");
		// 公司可用余额
		BigDecimal accountAvailableValue = earnSubAccount.getAccountAvailableValue();
		
		// 取待结息数据
		int total = dailySettlementRepository.countWaitInterestForPage(params);
		Map<String, String> allRequestNoMap = new HashMap<String, String>();
		int page = 5000;
		for(int i = 0; i < total/page; i ++) {
			params.put("start", i*page);
			params.put("length", page);
			List<Map<String,Object>> subAccountList=dailySettlementRepository.findWaitInterestForPage(params);
			totalEarn = totalEarn.add(singleDailySettlement(subAccountList, preDate, execDate, earnSubAccount, allRequestNoMap));
		}
		
		if( total%page != 0 ) {
			params.put("start", total - total%page);
			params.put("length", total%page);
			List<Map<String,Object>> subAccountList=dailySettlementRepository.findWaitInterestForPage(params);
			totalEarn = totalEarn.add(singleDailySettlement(subAccountList, preDate, execDate, earnSubAccount, allRequestNoMap));
		}

		/*公司居间人分账户结息,不累计客户的利息差*/
		companyInterest(centerSubAccount, centerSubAccount.getAccountAvailableValue(), totalPV,
				untreatedAmount, totalActualInterest, execDate, new BigDecimal("0"), preDate);
		/*公司收益分账户结息，累计客户的利息差*/
		companyInterest(earnSubAccount, accountAvailableValue, totalPV, 
				untreatedAmount, totalActualInterest, execDate, totalEarn, preDate);	
	}
	
	/**
	 * 
	 *
	 * @author  wangjf
	 * @date    2015年9月6日 下午3:48:38
	 * @param subAccountList
	 * @param preDate
	 * @param execDate
	 * @param earnSubAccount
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public BigDecimal singleDailySettlement(List<Map<String,Object>> subAccountList, Date preDate, String execDate,
			SubAccountInfoEntity earnSubAccount, Map<String, String> allRequestNoMap) {
       
		List<DailyInterestInfoEntity> list=new ArrayList<DailyInterestInfoEntity>();
		List<AccountFlowInfoEntity> accountFlowList = new ArrayList<AccountFlowInfoEntity>();
//		List<AccountFlowDetailEntity> accountFlowDetailList = new ArrayList<AccountFlowDetailEntity>();
//		List<FlowBusiRelationEntity> flowBusiList = new ArrayList<FlowBusiRelationEntity>();
		List<AccountFlowInfoEntity> subAccountFlowList = new ArrayList<AccountFlowInfoEntity>();
		 
		Map<String, Integer> requestNOMap = new HashMap<String, Integer>();
		Map<String,SubAccountInfoEntity> subAccountMap = new HashMap<String,SubAccountInfoEntity>();
		BigDecimal totalCompanyIncome = new BigDecimal("0");
		StopWatch sw = new StopWatch();
		sw.start("singleDailySettlement");
		int countRequestNo = 0;
		List<String> custIdList = new ArrayList<String>();
		for(int i = 0; i < subAccountList.size(); i ++){
			
			Map<String,Object> subAccount = subAccountList.get(i);
			
			/*客户分账户预计利息*/
			BigDecimal exceptInterest = new BigDecimal(subAccount.get("exceptInterest").toString());
			BigDecimal factInterest = new BigDecimal(subAccount.get("factInterest").toString());
			BigDecimal userIncome = new BigDecimal(subAccount.get("userIncome").toString());
			BigDecimal companyIncome = new BigDecimal(subAccount.get("companyIncome").toString());
			totalCompanyIncome = totalCompanyIncome.add(companyIncome);
			
			SubAccountInfoEntity custSubAccount = new SubAccountInfoEntity();
			custSubAccount.setId(subAccount.get("id").toString());
			custSubAccount.setCustId(subAccount.get("custId").toString());
			custSubAccount.setRelatePrimary(subAccount.get("relatePrimary").toString());
			custSubAccount.setRelateType(subAccount.get("relateType").toString());
			custSubAccount.setAccountTotalValue(new BigDecimal(subAccount.get("accountTotalValue").toString()).add(userIncome));
			custSubAccount.setAccountAvailableValue(new BigDecimal(subAccount.get("accountAvailableValue").toString()).add(userIncome));
			custSubAccount.setAccountFreezeValue(new BigDecimal(subAccount.get("accountFreezeValue").toString()));
			custSubAccount.setAccountAmount(new BigDecimal(subAccount.get("accountAmount").toString()));
			subAccountMap.put(custSubAccount.getId(), custSubAccount);

			earnSubAccount.setAccountTotalValue(earnSubAccount.getAccountTotalValue().add(companyIncome));
			earnSubAccount.setAccountAvailableValue(earnSubAccount.getAccountAvailableValue().add(companyIncome));
			
			//同一个客户的收益，记录流水时，REQUEST_NO要是同一个
			if(!allRequestNoMap.containsKey(subAccount.get("custId").toString()) 
					&& !requestNOMap.containsKey(subAccount.get("custId").toString())){
				requestNOMap.put(subAccount.get("custId").toString(), countRequestNo ++);
			}
			/*若投资时间早于15点，则正常计息，否则客户计息表的利息为0*/ 
			DailyInterestInfoEntity dailyInterest = new DailyInterestInfoEntity();
			dailyInterest.setId(SharedUtil.getUniqueString());
			dailyInterest.setCurrDate(execDate);
			dailyInterest.setSubAccountId(custSubAccount.getId());
			dailyInterest.setCreateDate(new Date());
			dailyInterest.setExpectInterest(exceptInterest);
			dailyInterest.setFactInterest(factInterest);
			dailyInterest.setFactGainInterest(userIncome);
			list.add(dailyInterest);
			
			if(userIncome.compareTo(BigDecimal.ZERO) != 0){		
				/* 记录客户分账户流水 */
				AccountFlowInfoEntity accountFlowInfo = new AccountFlowInfoEntity();
				accountFlowInfo.setId(SharedUtil.getUniqueString());
				accountFlowInfo.setAccountTotalAmount(custSubAccount.getAccountTotalValue());
				accountFlowInfo.setAccountAvailable(custSubAccount.getAccountAvailableValue());
				accountFlowInfo.setAccountFreezeAmount(custSubAccount.getAccountFreezeValue());
				accountFlowInfo.setAccountType(Constant.ACCOUNT_TYPE_SUB);
				accountFlowInfo.setAccountId(custSubAccount.getId());
				accountFlowInfo.setCustId(custSubAccount.getCustId());
				accountFlowInfo.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_01);
				accountFlowInfo.setRequestNo(subAccount.get("custId").toString());// 请求编号
				accountFlowInfo.setTradeNo(""); // 交易编号
				accountFlowInfo.setBankrollFlowDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING);
				accountFlowInfo.setTradeAmount(userIncome);
				accountFlowInfo.setTradeDate(preDate);
				accountFlowInfo.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				accountFlowInfo.setFlowType(SubjectConstant.SUBJECT_TYPE_VALUE);
				accountFlowInfo.setMemo("");
				accountFlowInfo.setCashAmount(custSubAccount.getAccountAmount());
				accountFlowInfo.setRelateType(custSubAccount.getRelateType());
				accountFlowInfo.setRelatePrimary(custSubAccount.getRelatePrimary());
				accountFlowInfo.setTargetAccount(earnSubAccount.getId());
				accountFlowList.add(accountFlowInfo);
				subAccountFlowList.add(accountFlowInfo);
				
//				AccountFlowDetailEntity accountFlowDetailEntity = new AccountFlowDetailEntity();
//				accountFlowDetailEntity.setId(SharedUtil.getUniqueString());
//				accountFlowDetailEntity.setAccountFlowId(accountFlowInfo.getId());
//				accountFlowDetailEntity.setSubjectType(SubjectConstant.TRADE_FLOW_TYPE_01);
//				accountFlowDetailEntity.setSubjectDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING);
//				accountFlowDetailEntity.setTradeAmount(accountFlowInfo.getTradeAmount());
//				accountFlowDetailEntity.setTradeDesc(Constant.PRODUCT_TYPE_01);
//				accountFlowDetailEntity.setTargetAccount(earnSubAccount.getId());
//				accountFlowDetailEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
//				accountFlowDetailList.add(accountFlowDetailEntity);
//				
//				FlowBusiRelationEntity flowBusiRelationEntity = new FlowBusiRelationEntity();
//				flowBusiRelationEntity.setId(SharedUtil.getUniqueString());
//				flowBusiRelationEntity.setAccountFlowId(accountFlowInfo.getId());
//				flowBusiRelationEntity.setRelateType(custSubAccount.getRelateType());
//				flowBusiRelationEntity.setRelatePrimary(custSubAccount.getRelatePrimary());
//				flowBusiRelationEntity.setCreateDate(new Date());
//				flowBusiList.add(flowBusiRelationEntity);
			}
			
			/* 记录公司分账户流水 */
			AccountFlowInfoEntity accountFlowInfo = new AccountFlowInfoEntity();
			accountFlowInfo.setId(SharedUtil.getUniqueString());
			accountFlowInfo.setAccountTotalAmount(earnSubAccount.getAccountTotalValue());
			accountFlowInfo.setAccountAvailable(earnSubAccount.getAccountAvailableValue());
			accountFlowInfo.setAccountFreezeAmount(earnSubAccount.getAccountFreezeValue());
			accountFlowInfo.setAccountType(Constant.ACCOUNT_TYPE_SUB);
			accountFlowInfo.setAccountId(earnSubAccount.getId());
			accountFlowInfo.setCustId(earnSubAccount.getCustId());
			accountFlowInfo.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_COMPANY);
			accountFlowInfo.setRequestNo(subAccount.get("custId").toString());// 请求编号
			accountFlowInfo.setTradeNo(""); // 交易编号
			accountFlowInfo.setBankrollFlowDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING);
			accountFlowInfo.setTradeAmount(companyIncome);
			accountFlowInfo.setTradeDate(preDate);
			accountFlowInfo.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			accountFlowInfo.setFlowType(SubjectConstant.SUBJECT_TYPE_VALUE);
			accountFlowInfo.setMemo("");
			accountFlowInfo.setCashAmount(earnSubAccount.getAccountAmount());
			accountFlowInfo.setRelateType(earnSubAccount.getRelateType());
			accountFlowInfo.setRelatePrimary(earnSubAccount.getRelatePrimary());
			accountFlowInfo.setTargetAccount(custSubAccount.getId());
			accountFlowList.add(accountFlowInfo);
			
//			AccountFlowDetailEntity accountFlowDetailEntity = new AccountFlowDetailEntity();
//			accountFlowDetailEntity.setId(SharedUtil.getUniqueString());
//			accountFlowDetailEntity.setAccountFlowId(accountFlowInfo.getId());
//			accountFlowDetailEntity.setSubjectType(SubjectConstant.TRADE_FLOW_TYPE_COMPANY);
//			accountFlowDetailEntity.setSubjectDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING);
//			accountFlowDetailEntity.setTradeAmount(accountFlowInfo.getTradeAmount());
//			accountFlowDetailEntity.setTradeDesc(Constant.PRODUCT_TYPE_01);
//			accountFlowDetailEntity.setTargetAccount(custSubAccount.getId());
//			accountFlowDetailEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
//			accountFlowDetailList.add(accountFlowDetailEntity);
//			
//			FlowBusiRelationEntity flowBusiRelationEntity = new FlowBusiRelationEntity();
//			flowBusiRelationEntity.setId(SharedUtil.getUniqueString());
//			flowBusiRelationEntity.setAccountFlowId(accountFlowInfo.getId());
//			flowBusiRelationEntity.setRelateType(earnSubAccount.getRelateType());
//			flowBusiRelationEntity.setRelatePrimary(earnSubAccount.getRelatePrimary());
//			flowBusiRelationEntity.setCreateDate(new Date());
//			flowBusiList.add(flowBusiRelationEntity);
			
			if(!custIdList.contains(subAccount.get("custId").toString())){
				custIdList.add(subAccount.get("custId").toString());
			}
		}
		subAccountMap.put(earnSubAccount.getId(), earnSubAccount);
		
		/**
		 * 为了使同一个用户使用同一个RequestNO，现将程序实现如下：
		 * 1)生成流水对象时使用requestNOMap(key:custId value:记录标识)记录当前批次共使用到多少个RequestNo
		 * 2)流水对象的requestNo暂时设置为custId，便于后续从requestNOMap取真正对应的requestNo
		 * 3)allRequestNoMap用来存储所有的RequestNo，由于担心数据量过大，
		 *   在此批次处理过程中会判断之前存在的RequestNo对应的CustId在本次处理时是否出现，
		 *   若没有出现则可以删除，因为现在数据是按照custId排序的，若之前出现的custId本次没有出现说明后面不会再出现，即可以删除。
		 * 4)按照countRequestNo个数（即requestNOMap大小），从数据库一次性取出countRequestNo个RequestNo，假设为集合requestNoList
		 * 5)按照requestNOMap中value值（序号）依次取requestNoList中对应的RequestNo，并把[custId,RequestNo]放入allRequestNoMap
		 * 6)循环流水对象按照流水对象中requestNo字段（前面存的是custId），依次从allRequestNoMap中取RequestNo
		 */
		// 从库里面批量取RequestNo和TradeNo
		List<String> requestNoList = numberService.generateTradeBatchNumber(countRequestNo);
		List<String> tradeNoList = numberService.generateTradeNumber(accountFlowList.size());
		
		// 删除allRequestNoMap中已经不存在的custId
		Iterator<Map.Entry<String, String>> it = allRequestNoMap.entrySet().iterator(); 
        while(it.hasNext())
        { 
            Map.Entry<String, String> entry = it.next(); 
            if(!custIdList.contains(entry.getKey())) {
            	it.remove();
            }
        } 
		
        // 把新的requestNo放至allRequestNoMap
		for(Map.Entry<String, Integer> entry: requestNOMap.entrySet()){   
		     allRequestNoMap.put(entry.getKey(), requestNoList.get(entry.getValue()));
		}   
		
		// 从allRequestNoMap取RequestNo
		for(int i = 0; i < accountFlowList.size(); i ++) {
			accountFlowList.get(i).setTradeNo(tradeNoList.get(i));
			//accountFlowList.get(i).setRequestNo(requestNoList.get(requestNOMap.get(accountFlowList.get(i).getRequestNo())));
			accountFlowList.get(i).setRequestNo(allRequestNoMap.get(accountFlowList.get(i).getRequestNo()));
		}
		
		sw.stop();
		log.info(sw.prettyPrint());
		
		/*批量插入结息表*/
		dailySettlementRepository.batchDailyInterestInfo(list);
		dailySettlementRepository.batchInsertAccountFlow(accountFlowList);		
//		dailySettlementRepository.batchInsertAccountFlowDetail(accountFlowDetailList); 
//		dailySettlementRepository.batchInsertFlowBusiRelation(flowBusiList);
		dailySettlementRepository.batchUpdateSubAccount(subAccountFlowList);		
		return totalCompanyIncome;
	} 
	
	/**
	 * 公司分账户（居间人账户和收益账户结息）
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void companyInterest(SubAccountInfoEntity subAccount, BigDecimal accountAvailableValue, BigDecimal totalPV,
			BigDecimal untreatedAmount, BigDecimal totalActualInterest, String execDate, 
			BigDecimal totalEarn, Date preDate){
		/*公司分账户实际利息和预计利息相同*/
		BigDecimal actualInterest=ArithUtil.div(accountAvailableValue, totalPV.add(untreatedAmount)).multiply(totalActualInterest);
		/*构造结息表数据*/
		actualInterest=ArithUtil.formatScale(actualInterest, 8);
		DailyInterestInfoEntity dailyInterest = new DailyInterestInfoEntity();
		dailyInterest.setId(SharedUtil.getUniqueString());
		dailyInterest.setCurrDate(execDate);
		dailyInterest.setExpectInterest(actualInterest);
		dailyInterest.setFactInterest(actualInterest);
		dailyInterest.setFactGainInterest(actualInterest.add(ArithUtil.formatScale(totalEarn,8)));
		dailyInterest.setSubAccountId(subAccount.getId());
		dailyInterest.setCreateDate(new Date());
		dailySettlementRepository.batchDailyInterestInfo(Arrays.asList(dailyInterest));
		
		subAccount.setAccountTotalValue(ArithUtil.formatScale(subAccount.getAccountTotalValue(),8).add(actualInterest));
		subAccount.setAccountAvailableValue(ArithUtil.formatScale(subAccount.getAccountAvailableValue(),8).add(actualInterest));
//		/* 记录公司收益分账户流水 */
//		BizUtil.saveAccountFlow(null,subAccount,SubjectConstant.TRADE_FLOW_TYPE_COMPANY,numberService.generateTradeBatchNumber(),
//				SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,ArithUtil.formatScale(actualInterest,8), SubjectConstant.TRADE_FLOW_TYPE_COMPANY,"",Constant.PRODUCT_TYPE_01,subAccount.getId(),preDate,numberService,accountFlowInfoRepository,accountFlowDetailRepository,flowBusiRelationRepository,SubjectConstant.SUBJECT_TYPE_VALUE);
		
		AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(null, subAccount, null, subAccount, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_COMPANY, numberService.generateTradeBatchNumber(), SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				ArithUtil.formatScale(actualInterest,8), SubjectConstant.TRADE_FLOW_TYPE_COMPANY, Constant.PRODUCT_TYPE_01, subAccount.getId(), SubjectConstant.SUBJECT_TYPE_VALUE);
		accountFlowInfoEntity.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_COMPANY);
		accountFlowInfoEntity.setTradeDate(preDate);
	}

	/**
	 * 体验宝每日结息
	 * 步骤：1.计算活期宝预计利息
	 *     2.获取客户分账信息列表
	 *     3.循环分账列表，计算每个分账的预计利息
	 *     4.生成流水信息
	 *     5.更新客户分账信息
	 * 	   6.批量插入结息表
	 * @author linhj
	 * @throws SLException 
	 * @date 2015年05月01日 下午午15:43:41
	*/
//	@Transactional
//	@Override
//	public void experienceDailySettlement(Date now) throws SLException {
//		Map<String,Object> params=new HashMap<String,Object>();
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
//		Date preDate=new Date(now.getTime() - DateUtils.DAY_MILLIS);
//		/*当前执行时间*/
//		String execDate="";
//		/*前一天时间*/
//		String preExecDate="";
//		
//		/* 判断每日结息每天重复执行 */
//		params.put("currDate", sdf.format(now));
//		/*产品类型为体验宝*/
//		params.put("typeName", Constant.PRODUCT_TYPE_03);
//		int count = dailySettlementRepository.findCurrDateRecordCount(params);
//		if (count > 0) {
//			log.error("每日结息不能重复执行");
//			throw new SLException("每日结息不能重复执行");
//		}
//		/* 定义当天时间 */
//		execDate = sdf.format(now);
//		/* 定义前一天时间 */
//		preExecDate = sdf.format(now.getTime() - DateUtils.DAY_MILLIS);
//		
//		params.put("execDate", execDate);
//		params.put("preExecDate", preExecDate);
//		 
//		//List<Map<String,Object>> exceptInterestList=dailySettlementRepository.findExceptInterest(params);
//		 
//		/*查询投资有效的分账户信息*/
//		List<Map<String,Object>> subAccountList=dailySettlementRepository.findSubAccount(params);
//		
//		List<DailyInterestInfoEntity> list=new ArrayList<DailyInterestInfoEntity>();
//		 
//		sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		Map<String,String> requestNOMap=new HashMap<String,String>();
//		for(Map<String,Object> subAccount:subAccountList){
//			DailyInterestInfoEntity dailyInterest=new DailyInterestInfoEntity();
//			/*账户总价值*/
//			BigDecimal accountTotalValue=new BigDecimal(subAccount.get("accountTotalValue").toString());
//			/*账户可用价值*/
//			BigDecimal accountValue=new BigDecimal(subAccount.get("accountAvailableValue").toString());
//			/*客户分账户预计利息*/
//			BigDecimal exceptInterest=new BigDecimal(subAccount.get("exceptInterest").toString());
////			for(Map<String,Object> exceptInterestMap:exceptInterestList){
////				if(exceptInterestMap.get("id").equals(subAccount.get("id"))){
////					exceptInterest=new BigDecimal(exceptInterestMap.get("exceptInterest").toString());
////				}
////			}
//			
//			if(exceptInterest.compareTo(new BigDecimal("0")) == 0) {
//				continue;
//			}
//			 
//			dailyInterest.setCurrDate(execDate);
//			/*若投资时间早于15点，则正常计息，否则客户计息表的利息为0*/ 
//			exceptInterest=ArithUtil.formatScale(exceptInterest, 8);
//			accountTotalValue=ArithUtil.formatScale(accountTotalValue,8);
//			accountValue=ArithUtil.formatScale(accountValue,8);
//			if(!execDate.equals(subAccount.get("investDate").toString())){
//				dailyInterest.setExpectInterest(exceptInterest);
//				dailyInterest.setFactInterest(exceptInterest);
//				dailyInterest.setFactGainInterest(exceptInterest);
//			}else{
//				dailyInterest.setExpectInterest(new BigDecimal("0"));
//				dailyInterest.setFactInterest(new BigDecimal("0"));
//				dailyInterest.setFactGainInterest(new BigDecimal("0"));
//			}
//			dailyInterest.setSubAccountId(subAccount.get("id").toString());
//			dailyInterest.setCreateDate(new Date());
//			list.add(dailyInterest);
//			String reqeustNo = "";
//			//同一个客户的收益，记录流水时，REQUEST_NO要是同一个
//			if(requestNOMap.containsKey(subAccount.get("custId").toString())){
//				reqeustNo=requestNOMap.get(subAccount.get("custId"));
//			}else{
//				reqeustNo = numberService.generateTradeBatchNumber();
//				requestNOMap.put(subAccount.get("custId").toString(), reqeustNo);
//			}
//			/*若投资时间早于15点，则正常计息，否则客户计息表的利息为0*/ 
//			if(!execDate.equals(subAccount.get("investDate").toString())){
//				SubAccountInfoEntity custSubAccount=BeanMapConvertUtil.toBean(SubAccountInfoEntity.class, subAccount);
//				custSubAccount.setAccountTotalValue(ArithUtil.formatScale(accountTotalValue.add(exceptInterest),8));
//				custSubAccount.setAccountAvailableValue(ArithUtil.formatScale(accountValue.add(exceptInterest),8));
//				/* 记录客户分账户流水 */
//				BizUtil.saveAccountFlow(null,custSubAccount, SubjectConstant.TRADE_FLOW_TYPE_03,reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,ArithUtil.formatScale(exceptInterest,8), SubjectConstant.TRADE_FLOW_TYPE_03, null,Constant.PRODUCT_TYPE_03,"",preDate,numberService,accountFlowInfoRepository,accountFlowDetailRepository,flowBusiRelationRepository,SubjectConstant.SUBJECT_TYPE_VALUE);
//				/* 更新客户分账的价值 */
//				subAccountInfoRepository.updateById(ArithUtil.formatScale(exceptInterest.add(accountTotalValue),8),ArithUtil.formatScale(exceptInterest.add(accountValue),8),new Date(),subAccount.get("id").toString());
//			}
//		}
//		/*批量插入结息表*/
//		dailySettlementRepository.batchInsert(list);
//		
//	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public void experienceDailySettlement(Date now) throws SLException {
		Map<String,Object> params=new HashMap<String,Object>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		Date preDate=new Date(now.getTime() - DateUtils.DAY_MILLIS);
		/*当前执行时间*/
		String execDate="";
		/*前一天时间*/
		String preExecDate="";
		
		/* 判断每日结息每天重复执行 */
		params.put("currDate", sdf.format(now));
		/*产品类型为体验宝*/
		params.put("typeName", Constant.PRODUCT_TYPE_03);
		int count = dailySettlementRepository.findCurrDateRecordCount(params);
		if (count > 0) {
			log.error("每日结息不能重复执行");
			throw new SLException("每日结息不能重复执行");
		}
		/* 定义当天时间 */
		execDate = sdf.format(now);
		/* 定义前一天时间 */
		preExecDate = sdf.format(now.getTime() - DateUtils.DAY_MILLIS);
		
		params.put("execDate", execDate);
		params.put("preExecDate", preExecDate);
		 
		/*查询投资有效的分账户信息*/
		int total = dailySettlementRepository.countSubAccount(params);
		int page = 5000;
		for(int i = 0; i < total/page; i ++) {
			params.put("start", i*page);
			params.put("length", page);
			List<Map<String,Object>> subAccountList=dailySettlementRepository.findSubAccountForPage(params);
			singleExperienceDailySettlement(subAccountList, preDate, execDate);
		}
		
		if( total%page != 0 ) {
			params.put("start", total - total%page);
			params.put("length", total%page);
			List<Map<String,Object>> subAccountList=dailySettlementRepository.findSubAccountForPage(params);
			singleExperienceDailySettlement(subAccountList, preDate, execDate);
		}
		
	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void singleExperienceDailySettlement(List<Map<String,Object>> subAccountList, Date preDate, String execDate) {
		List<DailyInterestInfoEntity> list=new ArrayList<DailyInterestInfoEntity>();
		List<AccountFlowInfoEntity> accountFlowList = new ArrayList<AccountFlowInfoEntity>();
//		List<AccountFlowDetailEntity> accountFlowDetailList = new ArrayList<AccountFlowDetailEntity>();
//		List<FlowBusiRelationEntity> flowBusiList = new ArrayList<FlowBusiRelationEntity>();
		 
		Map<String,String> requestNOMap = new HashMap<String,String>();
		Map<String,SubAccountInfoEntity> subAccountMap = new HashMap<String,SubAccountInfoEntity>();
		for(Map<String,Object> subAccount:subAccountList){
			
			/*客户分账户预计利息*/
			BigDecimal exceptInterest = ArithUtil.formatScale(new BigDecimal(subAccount.get("exceptInterest").toString()), 8);
			if(exceptInterest.compareTo(new BigDecimal("0")) == 0) {
				continue;
			}
			
			SubAccountInfoEntity custSubAccount = new SubAccountInfoEntity();
			custSubAccount.setId(subAccount.get("id").toString());
			custSubAccount.setCustId(subAccount.get("custId").toString());
			custSubAccount.setRelatePrimary(subAccount.get("relatePrimary").toString());
			custSubAccount.setRelateType(subAccount.get("relateType").toString());
			custSubAccount.setAccountTotalValue(new BigDecimal(subAccount.get("accountTotalValue").toString()).add(exceptInterest));
			custSubAccount.setAccountAvailableValue(new BigDecimal(subAccount.get("accountAvailableValue").toString()).add(exceptInterest));
			custSubAccount.setAccountFreezeValue(new BigDecimal(subAccount.get("accountFreezeValue").toString()));
			custSubAccount.setAccountAmount(new BigDecimal(subAccount.get("accountAmount").toString()));
			subAccountMap.put(custSubAccount.getId(), custSubAccount);

			String reqeustNo = "";
			//同一个客户的收益，记录流水时，REQUEST_NO要是同一个
			if(requestNOMap.containsKey(subAccount.get("custId").toString())){
				reqeustNo=requestNOMap.get(subAccount.get("custId"));
			}else{
				reqeustNo = numberService.generateTradeBatchNumber();
				requestNOMap.put(subAccount.get("custId").toString(), reqeustNo);
			}
			/*若投资时间早于15点，则正常计息，否则客户计息表的利息为0*/ 
			DailyInterestInfoEntity dailyInterest=new DailyInterestInfoEntity();
			dailyInterest.setCurrDate(execDate);
			dailyInterest.setSubAccountId(custSubAccount.getId());
			dailyInterest.setCreateDate(new Date());
			if(!execDate.equals(subAccount.get("investDate").toString())){
				
				dailyInterest.setExpectInterest(exceptInterest);
				dailyInterest.setFactInterest(exceptInterest);
				dailyInterest.setFactGainInterest(exceptInterest);
				
				/* 记录客户分账户流水 */
				AccountFlowInfoEntity accountFlowInfo = new AccountFlowInfoEntity();
				accountFlowInfo.setAccountTotalAmount(custSubAccount.getAccountTotalValue());
				accountFlowInfo.setAccountAvailable(custSubAccount.getAccountAvailableValue());
				accountFlowInfo.setAccountFreezeAmount(custSubAccount.getAccountFreezeValue());
				accountFlowInfo.setAccountType(Constant.ACCOUNT_TYPE_SUB);
				accountFlowInfo.setAccountId(custSubAccount.getId());
				accountFlowInfo.setCustId(custSubAccount.getCustId());
				accountFlowInfo.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_03);
				accountFlowInfo.setRequestNo(reqeustNo);// 请求编号
				accountFlowInfo.setTradeNo(numberService.generateTradeNumber()); // 交易编号
				accountFlowInfo.setBankrollFlowDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING);
				accountFlowInfo.setTradeAmount(ArithUtil.formatScale(exceptInterest,8));
				accountFlowInfo.setTradeDate(preDate);
				accountFlowInfo.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				accountFlowInfo.setFlowType(SubjectConstant.SUBJECT_TYPE_VALUE);
				accountFlowInfo.setMemo("");
				accountFlowInfo.setCashAmount(custSubAccount.getAccountAmount());
				accountFlowInfo.setTargetAccount(custSubAccount.getId());
				accountFlowInfo.setRelateType(custSubAccount.getRelateType());
				accountFlowInfo.setRelatePrimary(custSubAccount.getRelatePrimary());
	
				accountFlowList.add(accountFlowInfo);
			}
			else{
				dailyInterest.setExpectInterest(new BigDecimal("0"));
				dailyInterest.setFactInterest(new BigDecimal("0"));
				dailyInterest.setFactGainInterest(new BigDecimal("0"));
			}
			
			list.add(dailyInterest);
		}
		
		/*批量插入结息表*/
		dailySettlementRepository.batchInsertList(list);
		dailySettlementRepository.batchInsertList(accountFlowList);
		
//		for(AccountFlowInfoEntity a : accountFlowList) {
//			AccountFlowDetailEntity accountFlowDetailEntity = new AccountFlowDetailEntity();
//			accountFlowDetailEntity.setAccountFlowId(a.getId());
//			accountFlowDetailEntity.setSubjectType(SubjectConstant.TRADE_FLOW_TYPE_03);
//			accountFlowDetailEntity.setSubjectDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING);
//			accountFlowDetailEntity.setTradeAmount(a.getTradeAmount());
//			accountFlowDetailEntity.setTradeDesc(Constant.PRODUCT_TYPE_03);
//			accountFlowDetailEntity.setTargetAccount(a.getAccountId());
//			accountFlowDetailEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
//			
//			FlowBusiRelationEntity flowBusiRelationEntity = new FlowBusiRelationEntity();
//			flowBusiRelationEntity.setAccountFlowId(a.getId());
//			flowBusiRelationEntity.setRelateType(subAccountMap.get(a.getAccountId()).getRelateType());
//			flowBusiRelationEntity.setRelatePrimary(subAccountMap.get(a.getAccountId()).getRelatePrimary());
//			flowBusiRelationEntity.setCreateDate(new Date());
//			
//			accountFlowDetailList.add(accountFlowDetailEntity);
//			flowBusiList.add(flowBusiRelationEntity);
//		}
//		
//		dailySettlementRepository.batchInsertList(accountFlowDetailList);
//		dailySettlementRepository.batchInsertList(flowBusiList);
		dailySettlementRepository.batchUpdateSubAccount(accountFlowList);
	} 

	/**
	 * 体验宝到期赎回
	 * 1.查询投资有效的所有到期的体验宝分账户信息，关联产品类型表，投资表，分账表，查询15天到期的分账户
	 * 2.分账户价值置0，状态无效，投资表状态置无效
	 * 3.记账,若体验金本金1000，收益为10元，则：公司收益账户 现金-10
	 * 						    客户主账户     赎回体验宝    +1010
	 *                                回收体验金   -1000
	 * 						    客户分账户     价值 赎回体验宝    -1010
	 *                                
	 * @param now
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public void experienceWithdraw(Date now) throws SLException {
		Map<String,Object> params=new HashMap<String,Object>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		/*当前执行时间*/
		String execDate=sdf.format(now);
		/**产品类型-体验宝  */
		params.put("typeName", Constant.PRODUCT_TYPE_03);
		/* 定义当天时间 */
		params.put("execDate", execDate);
		/*查询投资有效的所有到期的体验宝分账户信息*/
		List<Map<String,Object>> subAccountList=dailySettlementRepository.findTYBExpireSubAccount(params);
		 /** 公司收益分账户*/
		SubAccountInfoEntity earnSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_ERAN);
		/** 公司收益主账户*/
		AccountInfoEntity  earnAccount=accountInfoRepository.findByAccountNo(Constant.ACCOUNT_NO_ERAN);
		/*公司收益账户总共支出金额*/
		BigDecimal totalAmount=new BigDecimal("0");
		for(Map<String,Object> subAccount:subAccountList){
			String reqeustNo = numberService.generateTradeBatchNumber();
			/*分账户价值置0，状态置无效*/
			subAccountInfoRepository.updateByIdForWithdraw(new Date(),subAccount.get("id").toString());
			/*投资状态置无效*/
			investInfoRepository.updateStatusByIdForWithdraw(new Date(),subAccount.get("investId").toString());
			/*账户可用价值*/
			BigDecimal accountValue=new BigDecimal(subAccount.get("accountAvailableValue").toString());
			/*投资金额*/
			BigDecimal investAmount=new BigDecimal(subAccount.get("investAmount").toString());
			/*收益*/
			BigDecimal income=accountValue.subtract(investAmount);
			/*客户分账*/
			SubAccountInfoEntity custSubAccount=BeanMapConvertUtil.toBean(SubAccountInfoEntity.class, subAccount);
			/*客户总账*/
			AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(subAccount.get("custId").toString());
			/*公司主账扣除金额*/
			earnAccount.setAccountTotalAmount(ArithUtil.formatScale(earnAccount.getAccountTotalAmount().subtract(income),8));
			earnAccount.setAccountAvailableAmount(ArithUtil.formatScale(earnAccount.getAccountAvailableAmount().subtract(income),8));
			/* 记录公司收益主账户流水 出 10元*/
			BizUtil.saveAccountFlow(earnAccount,earnSubAccount,SubjectConstant.TRADE_FLOW_TYPE_TYBWITHDRAW,reqeustNo,
					SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT,ArithUtil.formatScale(income,8), SubjectConstant.TRADE_FLOW_TYPE_TYBWITHDRAW,"",Constant.PRODUCT_TYPE_03,accountInfoEntity.getId(),now,numberService,accountFlowInfoRepository,SubjectConstant.SUBJECT_TYPE_AMOUNT);
			
			custSubAccount.setAccountTotalValue(ArithUtil.formatScale(custSubAccount.getAccountTotalValue().subtract(accountValue),8));
			custSubAccount.setAccountAvailableValue(ArithUtil.formatScale(custSubAccount.getAccountAvailableValue().subtract(accountValue),8));
			/*客户体验宝分账 --赎回体验宝 出1010*/
			BizUtil.saveAccountFlow(null,custSubAccount,SubjectConstant.TRADE_FLOW_TYPE_TYBWITHDRAW,reqeustNo,
					SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT,ArithUtil.formatScale(accountValue,8), SubjectConstant.TRADE_FLOW_TYPE_TYBWITHDRAW,"",Constant.PRODUCT_TYPE_03,accountInfoEntity.getId(),now,numberService,accountFlowInfoRepository,SubjectConstant.SUBJECT_TYPE_VALUE);
			/*客户体验宝分账 --赎回收益*/
			/*BizUtil.saveAccountFlow(null,custSubAccount,SubjectConstant.TRADE_FLOW_TYPE_TYBINCOME_WITHDRAW,reqeustNo,
					SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT,ArithUtil.formatScale(income,8), SubjectConstant.TRADE_FLOW_TYPE_TYBINCOME_WITHDRAW,subAccount.get("id").toString(),Constant.PRODUCT_TYPE_03,numberService,accountFlowInfoRepository,accountFlowDetailRepository,flowBusiRelationRepository);
			*/
			
			/*客户主账增加金额*/
			accountInfoEntity.setAccountTotalAmount(ArithUtil.formatScale(accountInfoEntity.getAccountTotalAmount().add(accountValue),8));
			accountInfoEntity.setAccountAvailableAmount(ArithUtil.formatScale(accountInfoEntity.getAccountAvailableAmount().add(accountValue),8));
			/* 记录客户主账户流水  赎回体验宝 进1010元*/
			BizUtil.saveAccountFlow(accountInfoEntity,custSubAccount,SubjectConstant.TRADE_FLOW_TYPE_TYBWITHDRAW,reqeustNo,
					SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,ArithUtil.formatScale(accountValue,8), SubjectConstant.TRADE_FLOW_TYPE_TYBWITHDRAW,"",Constant.PRODUCT_TYPE_03,earnAccount.getId(),now,numberService,accountFlowInfoRepository,SubjectConstant.SUBJECT_TYPE_AMOUNT);
			
			/*客户主账减去金额*/
			accountInfoEntity.setAccountTotalAmount(ArithUtil.formatScale(accountInfoEntity.getAccountTotalAmount().subtract(investAmount),8));
			accountInfoEntity.setAccountAvailableAmount(ArithUtil.formatScale(accountInfoEntity.getAccountAvailableAmount().subtract(investAmount),8));
			/* 记录客户主账户流水  回收体验金  出1000元*/
			BizUtil.saveAccountFlow(accountInfoEntity,custSubAccount,SubjectConstant.TRADE_FLOW_TYPE_TYBAMOUNT_WITHDRAW,reqeustNo,
					SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT,ArithUtil.formatScale(investAmount,8), SubjectConstant.TRADE_FLOW_TYPE_TYBAMOUNT_WITHDRAW,"",Constant.PRODUCT_TYPE_03,earnAccount.getId(),now,numberService,accountFlowInfoRepository,SubjectConstant.SUBJECT_TYPE_AMOUNT);
			/*更新客户总账的价值 */
			accountInfoRepository.updateByIdForWithdraw(ArithUtil.formatScale(accountInfoEntity.getAccountTotalAmount(),8), ArithUtil.formatScale(accountInfoEntity.getAccountAvailableAmount(),8),new Date(), accountInfoEntity.getId());
			
			/*记录操作日志*/
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
			logInfoEntity.setRelatePrimary(subAccount.get("investId").toString());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_15);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(subAccount.get("custId").toString());
			logInfoEntity.setMemo(Constant.OPERATION_TYPE_15);
			logInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			logInfoEntityRepository.save(logInfoEntity);
			/*累计公司需要扣除的收益*/
			totalAmount=totalAmount.add(income);
		}
		/*更新公司收益主账的价值 */
		//subAccountInfoRepository.updateById(ArithUtil.formatScale(earnSubAccount.getAccountTotalValue().subtract(totalAmount),8),ArithUtil.formatScale(earnSubAccount.getAccountAvailableValue().subtract(totalAmount),8), earnSubAccount.getId());
		accountInfoRepository.updateByIdForWithdraw(ArithUtil.formatScale(earnAccount.getAccountTotalAmount(),8), ArithUtil.formatScale(earnAccount.getAccountAvailableAmount(),8),new Date(), earnAccount.getId());		
	}

	/**
	 * 体验宝到期赎回 短信通知用户
	 * 查询需要通知的用户发送短信
	 *                                
	 * @param now
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public void experienceWithdrawSendSms(Date now) throws SLException {
		Map<String,Object> params=new HashMap<String,Object>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		/*当前执行时间*/
		String execDate=sdf.format(now);
		params.put("execDate", execDate);
		/*查询应该发送短信的用户*/
		List<Map<String,Object>> custList=dailySettlementRepository.findExperienceSendSmsCust(params);
		for(Map<String,Object> cust:custList){
			Map<String, Object> smsParams = new HashMap<String, Object>();
			smsParams.put("mobile", cust.get("mobile").toString());
			smsParams.put("custId", cust.get("id").toString());
			smsParams.put("messageType", Constant.SMS_TYPE_EXPERIENCE_WITHDRAW);
			smsParams.put("values", new String[]{cust.get("experienceAmount").toString(),cust.get("incomeAmount").toString()});
			smsService.asnySendSMS(smsParams);
		}
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo closeTermJob(Map<String, Object> param) throws SLException {
		List<ProductInfoEntity> productList = productInfoRepository.findTermProductInfoByProductTypeName(Constant.PRODUCT_TYPE_04);
		if(productList == null || productList.size() == 0 ){
			log.warn("产品不存在");
			return new ResultVo(false);
		}
		
		for(ProductInfoEntity productInfoEntity : productList) {
			if(!productInfoEntity.getProductStatus().equals(Constant.PRODUCT_STATUS_BID_ING)){
				log.warn("关闭失败！产品状态非开放中，无法关闭！");
				continue;
			}
			
			productInfoEntity.setProductStatus(Constant.PRODUCT_STATUS_BID_FINISH);// 产品状态设置为关闭
			productInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			// 2 当前产品可用价值改为0
			ProductDetailInfoEntity productDetailInfoEntity = productDetailInfoRepository.findByProductId(productInfoEntity.getId());
			if(productDetailInfoEntity == null) {
				log.warn("关闭失败！产品详情不存在！");
				continue;
			}
			
			productDetailInfoEntity.setCurrUsableValue(new BigDecimal("0"));//可用价值改为0
			productDetailInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		}

		return new ResultVo(true);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo recoverUnAtone(Map<String, Object> param)
			throws SLException {
		 /** 公司收益分账户*/
		SubAccountInfoEntity earnSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_ERAN);
		Map<String, Integer> requestNOMap = new HashMap<String, Integer>();
		int countRequestNo = 0;
		List<SubAccountInfoEntity> subAccountList = new ArrayList<SubAccountInfoEntity>();
		List<AccountFlowInfoEntity> accountFlowList = new ArrayList<AccountFlowInfoEntity>();
//		List<AccountFlowDetailEntity> accountFlowDetailList = new ArrayList<AccountFlowDetailEntity>();
//		List<FlowBusiRelationEntity> flowBusiList = new ArrayList<FlowBusiRelationEntity>();
		List<AccountFlowInfoEntity> subAccountFlowList = new ArrayList<AccountFlowInfoEntity>();
		List<InvestInfoEntity> investList = new ArrayList<InvestInfoEntity>();
		List<Map<String, Object>> atoneSubAccountList = dailySettlementRepository.findWaitRecoverAtone(param);
		for(Map<String, Object> subAccount : atoneSubAccountList) {
			
			BigDecimal tradeAmount = new BigDecimal(subAccount.get("accountTotalValue").toString());
			
			SubAccountInfoEntity custSubAccount = new SubAccountInfoEntity();
			custSubAccount.setId(subAccount.get("subAccountId").toString());
			custSubAccount.setCustId(subAccount.get("custId").toString());
			custSubAccount.setAccountTotalValue(BigDecimal.ZERO);
			custSubAccount.setAccountAvailableValue(BigDecimal.ZERO);
			custSubAccount.setAccountFreezeValue(new BigDecimal(subAccount.get("accountFreezeValue").toString()));
			custSubAccount.setAccountAmount(new BigDecimal(subAccount.get("accountAmount").toString()));
			custSubAccount.setVersion(Integer.valueOf(subAccount.get("subAccountVersion").toString()));
			custSubAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			subAccountList.add(custSubAccount);
			
			earnSubAccount.setAccountTotalValue(ArithUtil.add(earnSubAccount.getAccountTotalValue(), tradeAmount));
			earnSubAccount.setAccountAvailableValue(ArithUtil.add(earnSubAccount.getAccountAvailableValue(), tradeAmount));
			
			InvestInfoEntity investInfoEntity = new InvestInfoEntity();
			investInfoEntity.setId(subAccount.get("investId").toString());
			investInfoEntity.setInvestStatus(Constant.VALID_STATUS_INVALID);
			investInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			investList.add(investInfoEntity);
			
			//同一个客户的收益，记录流水时，REQUEST_NO要是同一个
			if(!requestNOMap.containsKey(subAccount.get("custId").toString())){
				requestNOMap.put(subAccount.get("custId").toString(), countRequestNo ++);
			}
			
			{
				/* 记录客户分账户流水 */
				AccountFlowInfoEntity accountFlowInfo = new AccountFlowInfoEntity();
				accountFlowInfo.setId(SharedUtil.getUniqueString());
				accountFlowInfo.setAccountTotalAmount(custSubAccount.getAccountTotalValue());
				accountFlowInfo.setAccountAvailable(custSubAccount.getAccountAvailableValue());
				accountFlowInfo.setAccountFreezeAmount(custSubAccount.getAccountFreezeValue());
				accountFlowInfo.setAccountType(Constant.ACCOUNT_TYPE_SUB);
				accountFlowInfo.setAccountId(custSubAccount.getId());
				accountFlowInfo.setCustId(custSubAccount.getCustId());
				accountFlowInfo.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_BAO_UNATONE);
				accountFlowInfo.setRequestNo(subAccount.get("custId").toString());// 请求编号
				accountFlowInfo.setTradeNo(""); // 交易编号
				accountFlowInfo.setBankrollFlowDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT);
				accountFlowInfo.setTradeAmount(tradeAmount);
				accountFlowInfo.setTradeDate(new Date());
				accountFlowInfo.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				accountFlowInfo.setFlowType(SubjectConstant.SUBJECT_TYPE_VALUE);
				accountFlowInfo.setMemo("");
				accountFlowInfo.setCashAmount(custSubAccount.getAccountAmount());
				accountFlowInfo.setTargetAccount(earnSubAccount.getId());
				accountFlowList.add(accountFlowInfo);
				subAccountFlowList.add(accountFlowInfo);
				
//				AccountFlowDetailEntity accountFlowDetailEntity = new AccountFlowDetailEntity();
//				accountFlowDetailEntity.setId(SharedUtil.getUniqueString());
//				accountFlowDetailEntity.setAccountFlowId(accountFlowInfo.getId());
//				accountFlowDetailEntity.setSubjectType(SubjectConstant.TRADE_FLOW_TYPE_BAO_UNATONE);
//				accountFlowDetailEntity.setSubjectDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT);
//				accountFlowDetailEntity.setTradeAmount(accountFlowInfo.getTradeAmount());
//				accountFlowDetailEntity.setTradeDesc(Constant.PRODUCT_TYPE_01);
//				accountFlowDetailEntity.setTargetAccount(earnSubAccount.getId());
//				accountFlowDetailEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
//				accountFlowDetailList.add(accountFlowDetailEntity);			
			}
			
			{
				/* 记录公司分账户流水 */
				AccountFlowInfoEntity accountFlowInfo = new AccountFlowInfoEntity();
				accountFlowInfo.setId(SharedUtil.getUniqueString());
				accountFlowInfo.setAccountTotalAmount(earnSubAccount.getAccountTotalValue());
				accountFlowInfo.setAccountAvailable(earnSubAccount.getAccountAvailableValue());
				accountFlowInfo.setAccountFreezeAmount(earnSubAccount.getAccountFreezeValue());
				accountFlowInfo.setAccountType(Constant.ACCOUNT_TYPE_SUB);
				accountFlowInfo.setAccountId(earnSubAccount.getId());
				accountFlowInfo.setCustId(earnSubAccount.getCustId());
				accountFlowInfo.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_BAO_UNATONE);
				accountFlowInfo.setRequestNo(subAccount.get("custId").toString());// 请求编号
				accountFlowInfo.setTradeNo(""); // 交易编号
				accountFlowInfo.setBankrollFlowDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING);
				accountFlowInfo.setTradeAmount(tradeAmount);
				accountFlowInfo.setTradeDate(new Date());
				accountFlowInfo.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				accountFlowInfo.setFlowType(SubjectConstant.SUBJECT_TYPE_VALUE);
				accountFlowInfo.setMemo("");
				accountFlowInfo.setCashAmount(earnSubAccount.getAccountAmount());
				accountFlowInfo.setTargetAccount(custSubAccount.getId());
				accountFlowList.add(accountFlowInfo);
				
//				AccountFlowDetailEntity accountFlowDetailEntity = new AccountFlowDetailEntity();
//				accountFlowDetailEntity.setId(SharedUtil.getUniqueString());
//				accountFlowDetailEntity.setAccountFlowId(accountFlowInfo.getId());
//				accountFlowDetailEntity.setSubjectType(SubjectConstant.TRADE_FLOW_TYPE_BAO_UNATONE);
//				accountFlowDetailEntity.setSubjectDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING);
//				accountFlowDetailEntity.setTradeAmount(accountFlowInfo.getTradeAmount());
//				accountFlowDetailEntity.setTradeDesc(Constant.PRODUCT_TYPE_01);
//				accountFlowDetailEntity.setTargetAccount(custSubAccount.getId());
//				accountFlowDetailEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
//				accountFlowDetailList.add(accountFlowDetailEntity);
			}
		}
		
		List<String> requestNoList = Lists.newArrayList();
		List<String> tradeNoList = Lists.newArrayList();
		int page = 5000;
		for(int i = 0; i < countRequestNo/page; i ++) {
			requestNoList.addAll(numberService.generateTradeBatchNumber(page));
		}
		
		if( countRequestNo%page != 0 ) {
			requestNoList.addAll(numberService.generateTradeBatchNumber(countRequestNo%page));
		}
		
		int tradeNoSize = accountFlowList.size();
		for(int i = 0; i < tradeNoSize/page; i ++) {
			tradeNoList.addAll(numberService.generateTradeNumber(page));
		}
		
		if( tradeNoSize%page != 0 ) {
			tradeNoList.addAll(numberService.generateTradeNumber(tradeNoSize%page));
		}
		
		//List<String> requestNoList = numberService.generateTradeBatchNumber(countRequestNo);
		//List<String> tradeNoList = numberService.generateTradeNumber(accountFlowList.size());
		
		// 从allRequestNoMap取RequestNo
		for(int i = 0; i < accountFlowList.size(); i ++) {
			accountFlowList.get(i).setTradeNo(tradeNoList.get(i));
			accountFlowList.get(i).setRequestNo(requestNoList.get(requestNOMap.get(accountFlowList.get(i).getRequestNo())));
		}
		
		/*批量插入表*/
		dailySettlementRepository.batchInsertAccountFlow(accountFlowList);		
//		dailySettlementRepository.batchInsertAccountFlowDetail(accountFlowDetailList); 
//		dailySettlementRepository.batchInsertFlowBusiRelation(flowBusiList);
		dailySettlementRepository.batchUpdateSubAccountInfo(subAccountList);	
		dailySettlementRepository.batchUpdateInvest(investList);
		
		return new ResultVo(true);
	}

}
