package com.slfinance.shanlincaifu.timer.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.CreditRightValueRepository;
import com.slfinance.shanlincaifu.repository.LoanInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.DailySettlementRepository;
import com.slfinance.shanlincaifu.repository.custom.LoanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.LoanInfoService;
import com.slfinance.shanlincaifu.service.ProductBusinessService;
import com.slfinance.shanlincaifu.timer.TimeTaskExecuteService;

/**   
 * 
 * 定时任务统一实现类
 * @author  zhoudl
 * @version $Revision:1.0.0, $Date: 2015年4月23日 上午10:04:46 $ 
 */
@Service
public class TimeTaskExecuteServiceImpl implements TimeTaskExecuteService {
	
	@Autowired
	private LoanInfoRepositoryCustom loanInfoRepositoryCustom; 
	@Autowired
	private CreditRightValueRepository creditRightValueRepository;
	@Autowired
	private LoanInfoRepository loanInfoRepository;
	@Autowired
	private ProductBusinessService productBusinessService;
	@Autowired
	private LoanInfoService loanInfoService;
	@Autowired
	private SubAccountInfoRepository subAccountInfoRepository;
	@Autowired
	private DailySettlementRepository dailySettlementRepository;
	
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
	private AccountInfoRepository accountInfoRepository;

	/**
	 * @desc 预算债权PV值
	 * @author zhoudl
	 * @date 2015年04月15日 下午午13:41:36
	 * **/
	@Override
	public void execLoanPv() throws SLException {
		loanInfoService.execLoanPv();
	}

	/**
	 * 定时发标
	 * 
	 * @author caoyi
	 * @date 2015年05月01日 下午午13:41:36
	*/
	@Override
	public void autoOpenBid() throws SLException {
		productBusinessService.releaseBid();
	}

	/**
	 * 可开放价值计算
	 * 
	 * @author caoyi
	 * @date 2015年05月01日 下午午15:43:41
	*/
	@Override
	public void computeOpenValue() throws SLException {
		productBusinessService.computeOpenValue();
	}
	
	/**
	 * 每日结息
	 * 步骤：1.计算活期宝实际总利息
	 *     2.计算活期宝预计总利息
	 *     3.获取客户分账信息列表
	 *     4.循环分账列表，计算每个分账的预计利息和实际利息
	 *     5.生成流水信息
	 *     6.更新客户分账信息
	 * 	   7.批量插入结息表
	 * @author linhj
	 * @date 2015年05月01日 下午午15:43:41
	*/
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Override
	public void dailySettlement() throws SLException {
		/*Map<String,Object> params=new HashMap<String,Object>();
		params.put("typeName", Constant.PRODUCT_TYPE_01);
		活期宝总价值
		BigDecimal totalPV=dailySettlementRepository.findTotalPV(params);
		活期宝实际总利息
		BigDecimal totalActualInterest=dailySettlementRepository.findActualInterest(params);
		活期宝预计总利息
		params.put("totalPV", totalPV);
		BigDecimal totalExceptInterest=dailySettlementRepository.findExceptInterest(params);
		
		还款未处理金额
		BigDecimal untreatedAmount=dailySettlementRepository.untreatedAmount();
		查询投资有效的分账户信息
		List<Map<String,Object>> subAccountList=dailySettlementRepository.findSubAccount();
		
		List<DailyInterestInfoEntity> list=new ArrayList<DailyInterestInfoEntity>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd"); 
		String date=sdf.format(new Date());
		 *//** 公司收益分账户*//*
		SubAccountInfoEntity earnSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_ERAN);
		for(Map<String,Object> subAccount:subAccountList){
			DailyInterestInfoEntity dailyInterest=new DailyInterestInfoEntity();
			账户总价值
			BigDecimal accountValue=new BigDecimal(subAccount.get("accountTotalValue").toString());
			客户分账户预计利息
			BigDecimal exceptInterest=ArithUtil.div(accountValue, totalPV.add(untreatedAmount)).multiply(totalExceptInterest);
			客户分账户实际利息
			BigDecimal actualInterest=ArithUtil.div(accountValue, totalPV.add(untreatedAmount)).multiply(totalActualInterest);
			dailyInterest.setCurrDate(date);
			dailyInterest.setExpectInterest(exceptInterest);
			dailyInterest.setFactInterest(actualInterest);
			dailyInterest.setSubAccountId(subAccount.get("id").toString());
			dailyInterest.setCreateDate(new Date());
			list.add(dailyInterest);
			String reqeustNo = numberService.generateTradeNumber();
			客户收益额
			BigDecimal tradeAmount=new BigDecimal("0");
			公司收益额
			BigDecimal earn=new BigDecimal("0");
			//AccountInfoEntity account=accountInfoRepository.findOne(subAccount.get("accountId").toString());
			if(CompareHelper.greaterThan(exceptInterest,actualInterest)){
				tradeAmount=actualInterest;
			}else{
				tradeAmount=exceptInterest;
				earn=actualInterest.subtract(exceptInterest);
			}
			记录客户分账户流水
			SubAccountInfoEntity custSubAccount=BeanMapConvertUtil.toBean(SubAccountInfoEntity.class, subAccount);
			saveAccountFlow(custSubAccount,SubjectConstant.TRADE_FLOW_TYPE_01,reqeustNo,
					SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,tradeAmount, SubjectConstant.TRADE_FLOW_TYPE_01,null);
			更新客户分账的价值
			subAccountInfoRepository.updateById(tradeAmount.add(accountValue), subAccount.get("id").toString());
			记录公司收益分账户流水
			saveAccountFlow(earnSubAccount,SubjectConstant.TRADE_FLOW_TYPE_COMPANY,reqeustNo,
					SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,earn, SubjectConstant.TRADE_FLOW_TYPE_COMPANY,custSubAccount.getId());
			
		}
		更新公司收益分账的价值
		if(CompareHelper.greaterThan(totalActualInterest,totalExceptInterest)){
			subAccountInfoRepository.updateById(totalActualInterest.subtract(totalExceptInterest), earnSubAccount.getId());
		} 
		
		批量插入结息表
		dailySettlementRepository.batchInsert(list);*/
	}
	
//	/**
//	 * 生成流水记录
//	 * @param sourceSubAccount
//	 * @param tradeType
//	 * @param reqeustNo
//	 * @param bankrollFlowDirection
//	 * @param tradeAmount
//	 * @param subjectType
//	 * @param memo
//	 */
//	private void saveAccountFlow(SubAccountInfoEntity sourceSubAccount, 
//			String tradeType, String reqeustNo, 
//			String bankrollFlowDirection, BigDecimal tradeAmount, String subjectType,String memo)
//	{
//		AccountFlowInfoEntity accountFlowInfo = new AccountFlowInfoEntity();
//
//		accountFlowInfo.setAccountTotalAmount(sourceSubAccount.getAccountTotalValue());
//		accountFlowInfo.setAccountAvailable(sourceSubAccount.getAccountAvailableValue());
//		accountFlowInfo.setAccountFreezeAmount(sourceSubAccount.getAccountFreezeValue());
//		accountFlowInfo.setAccountType(Constant.ACCOUNT_TYPE_SUB);
//		accountFlowInfo.setAccountId(sourceSubAccount.getId());
//		accountFlowInfo.setCustId(sourceSubAccount.getCustId());
//
//		accountFlowInfo.setTradeType(tradeType);
//		accountFlowInfo.setRequestNo(reqeustNo);// 请求编号
//		accountFlowInfo.setTradeNo(numberService.generateTradeNumber()); // 交易编号
//		accountFlowInfo.setBankrollFlowDirection(bankrollFlowDirection);//资金方向转出
//		accountFlowInfo.setTradeAmount(tradeAmount);
//		accountFlowInfo.setTradeDate(new Timestamp(System.currentTimeMillis()));
//		accountFlowInfo.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
//		accountFlowInfo.setRelateType(sourceSubAccount.getRelateType());
//		accountFlowInfo.setRelatePrimary(sourceSubAccount.getRelatePrimary());
//		/*公司收益分账户的备注记录的是从哪个客户分账户上过来的收益*/
//		accountFlowInfo.setMemo(memo);
//		accountFlowInfo = accountFlowInfoRepository.save(accountFlowInfo);
//		
//		// 3.3 记录账户流水详情表
////		AccountFlowDetailEntity accountFlowDetailEntity = new AccountFlowDetailEntity();
////		accountFlowDetailEntity.setAccountFlowId(accountFlowInfo.getId());
////		accountFlowDetailEntity.setSubjectType(subjectType);
////		accountFlowDetailEntity.setSubjectDirection(bankrollFlowDirection);
////		accountFlowDetailEntity.setTradeAmount(tradeAmount);
////		accountFlowDetailEntity.setTradeDesc("活期宝");
////		//accountFlowDetailEntity.setTargetAccount(destSubAccount.getId());
////		accountFlowDetailEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
////		accountFlowDetailRepository.save(accountFlowDetailEntity);	
////	
////		// 3.4 记录业务流水
////		FlowBusiRelationEntity flowBusiRelationEntity = new FlowBusiRelationEntity();
////		flowBusiRelationEntity.setAccountFlowId(accountFlowInfo.getId());
////		flowBusiRelationEntity.setRelateType(sourceSubAccount.getRelateType());
////		flowBusiRelationEntity.setRelatePrimary(sourceSubAccount.getRelatePrimary());
////		flowBusiRelationEntity.setCreateDate(new Date());
////		flowBusiRelationRepository.save(flowBusiRelationEntity);	
//	}
}
