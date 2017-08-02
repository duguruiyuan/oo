/** 
 * @(#)FixedInvestServiceImpl.java 1.0.0 2015年8月15日 上午11:23:03  
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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AtoneInfoEntity;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.entity.CustApplyInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductRateInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.AtoneInfoRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRepository;
import com.slfinance.shanlincaifu.repository.CustApplyInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.PaymentRecordInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductRateInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthPaymentPlanInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.AccountFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.AccountInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.FixedInvestRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.CustActivityInfoService;
import com.slfinance.shanlincaifu.service.FixedInvestService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.InvestService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.ProductBusinessService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.PageFuns;
import com.slfinance.vo.ResultVo;

/**   
 * 定期宝业务列表、统计、交易、投资记录业务接口实现
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年8月15日 上午11:23:03 $ 
 */
@Service
@Slf4j
public class FixedInvestServiceImpl implements FixedInvestService {

	@Autowired
	private FixedInvestRepositoryCustom fixedInvestRepositoryCustom;
	
	@Autowired
	private ProductInfoRepository productInfoRepository;
	
	@Autowired
	private ProductDetailInfoRepository productDetailInfoRepository;
	
	@Autowired
	private ProductRateInfoRepository productRateInfoRepository;
	
	@Autowired
	private InvestDetailInfoRepository investDetailInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private CustActivityInfoService custActivityInfoService;
	
	@Autowired
	private AccountFlowInfoRepositoryCustom accountFlowInfoRepositoryCustom;
	
	@Autowired
	private InvestInfoRepository investInfoRepository;
	
	@Autowired
	private AtoneInfoRepository atoneInfoRepository;
	
	@Autowired
	private SubAccountInfoRepository subAccountInfoRepository;
	
	@Autowired
	private InvestService investService;
	
	@Autowired
	private AuditInfoRepository auditInfoRepository;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	
	@Autowired
	private CustApplyInfoRepository custApplyInfoRepository;
	
	@Autowired
	private FlowNumberService flowNumberService;
	
	@Autowired
	private ProductBusinessService productBusinessService;
	
	@Autowired
	private PaymentRecordInfoRepository paymentRecordInfoRepository;
	
	@Autowired
	private WealthPaymentPlanInfoRepository wealthPaymentPlanInfoRepository;
	
	@Autowired
	private LoanInfoRepositoryCustom loanInfoRepositoryCustom;
	@Autowired 
	private AccountInfoRepositoryCustom accountInfoRepositoryCustom;
	
	/**
	 * 定期宝分页列表查询
	 */
	@Override
	public ResultVo getFixedInvestListPage(Map<String, Object> paramsMap)throws SLException {
		Page<Map<String, Object>> page = fixedInvestRepositoryCustom.getFixedInvestListPage(paramsMap);
		return new ResultVo(true,"定期宝分页列表查询成功",PageFuns.pageVoToMap(page));
	}

	/**
	 * 定期宝购买页面详情
	 */
	@Override
	public ResultVo getFixedInvestDatail(Map<String, Object> paramsMap)throws SLException {
		Map<String,Object> data = Maps.newHashMap();
		ProductInfoEntity productInfo =  productInfoRepository.findOne((String)paramsMap.get("id"));
		if(productInfo == null)
			throw new SLException("产品不存在");
		
		ProductDetailInfoEntity productDetail =  productDetailInfoRepository.findByProductId(productInfo.getId());
		if(productDetail == null)
			throw new SLException("加载产品详情出错");
		
		ProductRateInfoEntity proRate =  productRateInfoRepository.findTopByProductIdOrderByYearRateAscAwardRateAsc(productInfo.getId());
		if(proRate == null )
			throw new SLException("加载产品利率出错");
		
		BigDecimal alreadyInvestAmount = productDetail.getAlreadyInvestAmount();
		
		// 查出名称跟本产品一样的且停用的产品，若存在则把总投资笔数，累积投资金额加上
		Object obj = productDetailInfoRepository.findByProductName(productInfo.getProductName());
		alreadyInvestAmount = (obj == null ? BigDecimal.ZERO : new BigDecimal(obj.toString()));
		
		data.put("id", (String)paramsMap.get("id"));//产品ID
		data.put("useBuyAmount", productDetail.getCurrUsableValue());//可购份额
		data.put("productName", productInfo.getProductName());//产品名称
		data.put("termName", productDetail.getCurrTerm());//期数(产品代表名称+日期)
		data.put("productDesc", productInfo.getProductDesc());//简介
		data.put("yearRate", proRate.getYearRate());//预计年化收益
		data.put("awardRate", proRate.getAwardRate());//奖励利率
		data.put("typeTerm", productInfo.getTypeTerm());//投资期限
		data.put("useableAmount", productDetail.getCurrUsableValue());//剩余金额
		data.put("incomeType", productInfo.getIncomeHandleMethod());//收益类型
		data.put("investCount", investDetailInfoRepository.findInvestCountByProductName(productInfo.getProductName()));//投资笔数
		// 
		Map<String, Object> partakes = productBusinessService.findPartakeForDisplay(Constant.PRODUCT_TYPE_04);
		data.put("loanCount", partakes.get("partakeCrerigs"));//持有债权数量，不是债权价值数量,需要从投资金额里面取
//		data.put("totalInvestAmount", investInfoRepository.sumInvestAmountByProductId((String)paramsMap.get("id")));//累计投资  
		data.put("totalInvestAmount", alreadyInvestAmount);//累计投资  
		data.put("incomeToAmountTime", investService.getInvestIncomeDate());//收益日期 
		data.put("investMinAmount", productInfo.getInvestMinAmount());//起投金额 
		data.put("increaseAmount", productInfo.getIncreaseAmount());//递增金额 
		/**用户登陆时返回的数据**/
		if(StringUtils.isNotEmpty((String)paramsMap.get("custId"))){
			paramsMap.put("productId", (String)paramsMap.get("id"));
			data.put("currUseBuyAmount", getCurrUseBuyAmount(paramsMap));//当前可购份额  
			AccountInfoEntity accountInfo = accountInfoRepository.findByCustId((String)paramsMap.get("custId"));
			data.put("userUseableAmount", accountInfo != null ? accountInfo.getAccountAvailableAmount() : BigDecimal.ZERO );//可用余额  
		}else{
			data.put("investMaxAmount",  paramService.findMaxInvestAmount());//投资上限  
		}
		
		// 判断是否可以售卖
		//UserUtils.judgeCanSale(data);	
		
		return new ResultVo(true,"加载详情成功",data);
	}

	/**
	 * 定期宝产品加入记录
	 */
	@Override
	public ResultVo getInvestListByProIdPage(Map<String, Object> paramsMap)throws SLException {
		return new ResultVo(true,"定期宝产品加入记录成功",PageFuns.pageVoToMap(fixedInvestRepositoryCustom.findInvestList(PageFuns.numToIndex(paramsMap))));
	}

	/**
	 * 帐户总览统计信息
	 */
	@Override
	public ResultVo getFixedInvestStatisicInfo(Map<String, Object> paramsMap)throws SLException {
		Map<String,Object> result = Maps.newHashMap();
	
		/**1:账户统计信息**/
		Map<String,Object> accountInfoData = Maps.newHashMap();
		//基本信息
				Map<String, Object> resultMap = accountInfoRepositoryCustom.findAccountInfoByCustId(paramsMap);
		accountInfoData.put("useableAmount", resultMap.get("accountAvailableAmount"));//可用金额
		accountInfoData.put("freezeAmount",  resultMap.get("accountFreezeAmount"));//冻结金额
		accountInfoData.put("accountAmount",ArithUtil.add((BigDecimal) resultMap.get("accountFreezeAmount"), (BigDecimal) resultMap.get("accountAvailableAmount")));//账户余额=可用金额+冻结金额
		accountInfoData.put("accountActivityAmount", resultMap.get("accountActivityAmount"));//活动金额
		// update by wangjf 2016-12-28
		/**未结算现金奖励**//*
		Map<String,Object> rewardMap = custActivityInfoService.findRewardById(paramsMap);
		accountInfoData.put("rewardNotSettle", null != rewardMap ? rewardMap.get("rewardNotSettle") : BigDecimal.ZERO);//未结算现金奖励
		
		*//**活期宝、体验宝累计收益 **//*
		paramsMap.put("tradeType", Arrays.asList(SubjectConstant.TRADE_FLOW_TYPE_01,SubjectConstant.TRADE_FLOW_TYPE_03));
		BigDecimal sumTradeAmount = accountFlowInfoRepositoryCustom.findIncomeByCustId(paramsMap);
		*//**定期宝累计盈亏**//*
		*//**add at 2016/1/23 zhangt 累计收益 += 企业借款已获收益 *//*
		BigDecimal projectReceivedAmount = paymentRecordInfoRepository.queryReceivedAmountByCustId((String)paramsMap.get("custId"));
		BigDecimal sumSyAmount = ArithUtil.add(sumTradeAmount, fixedInvestRepositoryCustom.getAtonedAmount((String)paramsMap.get("custId"), Arrays.asList(Constant.PRODUCT_TYPE_04), Arrays.asList(Constant.TERM_INVEST_STATUS_FINISH,Constant.TERM_INVEST_STATUS_ADVANCE_FINISH))).add(projectReceivedAmount == null ? BigDecimal.ZERO : projectReceivedAmount);*/
		
		// 企业借款收益
		BigDecimal projectReceivedAmount = paymentRecordInfoRepository.queryReceivedAmountByCustId((String)paramsMap.get("custId"));
		projectReceivedAmount = (projectReceivedAmount == null ? BigDecimal.ZERO : projectReceivedAmount);
		
		/** add at 2016-12-06 zhiwen_feng 累计收益+=散表收益 */
		String onlineTime = paramService.findTransferRefromOnlineTimeByParameterName(Constant.TRANSFERRE_FROM_ONLINE_TIME_20170531);
		paramsMap.put("onlineTime", onlineTime);
		Map<String, Object> loanAmount = loanInfoRepositoryCustom.queryMyDisperseIncome(paramsMap);
		// 散表收益
		BigDecimal loanReceivedAmount = new BigDecimal(loanAmount.get("earnTotalAmount").toString());
		accountInfoData.put("sumTradeAmount", ArithUtil.add(projectReceivedAmount, loanReceivedAmount));//累计收益 
		
		/**未使用体验金奖励相关信息**//* 
		Map<String,Object> expGoldMap = custActivityInfoService.findExperienceGoldById(paramsMap);
		//未使用体验金奖励
		accountInfoData.put("enableExpeAmount", expGoldMap != null ? expGoldMap.get("ExperienceGoldUnUsed") : BigDecimal.ZERO);*/
		/** 体验金待收收益**/
		Map<String, Object> loanExperience = loanInfoRepositoryCustom.queryEarnAndExceptTotalExperience(paramsMap);
		BigDecimal exceptTotalExperience = new BigDecimal(loanExperience.get("exceptTotalExperience").toString());
		/** 待收本金, 待收利息*/
		//企业借款待收收益
		BigDecimal projExceptRepayAmount = investInfoRepository.queryProjectExceptAmountByCustId((String)paramsMap.get("custId"));
		//企业借款 :项目状态为 发布中 和满标复合的在投金额
		BigDecimal investAmount = investInfoRepository.queryProjectInvestAmountByCustId((String)paramsMap.get("custId"));
		//企业借款待收本金
		BigDecimal projExceptRepayPrincipal = ArithUtil.add(investAmount, 
				investInfoRepository.queryProjectRemainderPrincipal((String)paramsMap.get("custId")));
		//优选计划待收收益
		BigDecimal planExceptPayInterest = wealthPaymentPlanInfoRepository.
				findExceptPaymentInterestByCustId((String)paramsMap.get("custId"), Constant.WEALTH_PAYMENT_PLAN_STATUS_NOT);
		//优选计划待收奖励
		BigDecimal planExceptPaymentAward = wealthPaymentPlanInfoRepository.
				findExceptPaymentAwardByCustId((String)paramsMap.get("custId"), Constant.WEALTH_PAYMENT_PLAN_STATUS_NOT);
		//优选计划投资状态为投资中的的在投金额
		BigDecimal planInvestAmount = investInfoRepository.queryPlanInvestAmountByCustId((String)paramsMap.get("custId"), Constant.INVEST_STATUS_INVESTING);
		//优选计划待收本金
		BigDecimal planExceptRepayPrincipal = ArithUtil.add(planInvestAmount, wealthPaymentPlanInfoRepository.
				findExceptPaymentPrincipalByCustId((String)paramsMap.get("custId"), Constant.WEALTH_PAYMENT_PLAN_STATUS_NOT));

		//散标代收收益
		BigDecimal loanExceptPayInterest = new BigDecimal(loanAmount.get("exceptTotalAmount").toString());
		//散标再投金额（投资中）
		BigDecimal loanNotStatyInvestAmount = new BigDecimal(loanAmount.get("notStatyInvestAmount").toString());
		//散表待收本金（收益中）
		BigDecimal loanExceptTotalPrincipal = new BigDecimal(loanAmount.get("exceptTotalPrincipal").toString());
		//散标待收本金 
		BigDecimal loanExceptRepayPrincipal = ArithUtil.add(loanNotStatyInvestAmount, loanExceptTotalPrincipal);
		
		Map<String, Object> loanPrincipal = loanInfoRepositoryCustom.queryEarnTotalPrincipal(paramsMap);
		//橙信贷待收奖励
		BigDecimal exceptTotalAward = new BigDecimal(loanPrincipal.get("exceptTotalAward").toString());
		//加息券待收奖励
		BigDecimal exceptTotalIncreaseInterest = new BigDecimal(loanPrincipal.get("exceptTotalIncreaseInterest").toString());
		
		//用户总待收本金
		BigDecimal exceptRepayPrincipal = ArithUtil.add(projExceptRepayPrincipal, ArithUtil.add(loanExceptRepayPrincipal, planExceptRepayPrincipal));
		//用户总待收收益
		BigDecimal exceptRepayAmount = ArithUtil
				.add(projExceptRepayAmount,
						ArithUtil.add(
								loanExceptPayInterest,
								ArithUtil.add(
										planExceptPayInterest,
										ArithUtil
												.add(planExceptPaymentAward,
														ArithUtil
																.add(exceptTotalExperience,
																		ArithUtil
																				.add(exceptTotalAward,
																						exceptTotalIncreaseInterest))))));

		accountInfoData.put("exceptRepayAmount", exceptRepayAmount.toPlainString());
		accountInfoData.put("exceptRepayPrincipal", exceptRepayPrincipal.toPlainString());
		
		/** 总资产 = 用户待收本金 + 用户待收收益 + 可用余额 + 冻结金额+活动金额*/
		accountInfoData.put("totalAmount", ArithUtil.add((BigDecimal)accountInfoData.get("accountAmount"), ArithUtil.add(exceptRepayPrincipal, ArithUtil.add(exceptRepayAmount,(BigDecimal)resultMap.get("accountActivityAmount")))));
		
		result.put("accountInfo", accountInfoData);
//		/**2:投资列表数据信息**/
//		Map<String,Object> investList = Maps.newHashMap();
//		result.put("investList", investList);
			
		return new ResultVo(true,"账户总览统计信息查询成功",result);
	}

	/**
	 * 定期宝投资详情
	 */
	@Override
	public ResultVo getFixedInvestDetail(Map<String, Object> paramsMap)throws SLException {

		ProductInfoEntity productInfo =  productInfoRepository.findOne((String)paramsMap.get("id"));
		if(productInfo == null)
			throw new SLException("定期宝产品不存在");
		
		ProductDetailInfoEntity productDetail =  productDetailInfoRepository.findByProductId(productInfo.getId());
		if(productDetail == null)
			throw new SLException("加载产品详情出错");
		
		ProductRateInfoEntity proRate =  productRateInfoRepository.findTopByProductIdOrderByYearRateAscAwardRateAsc(productInfo.getId());
		if(proRate == null)
			throw new SLException("加载产品利率出错");
		
		InvestInfoEntity invest = investInfoRepository.findOne((String)paramsMap.get("investId"));
		if(invest == null)
			throw new SLException("投资产品查询出错");
		
		Map<String,Object> result = Maps.newHashMap();
		result.put("productName", productInfo.getProductName());
		result.put("termName", invest.getCurrTerm());
		result.put("yearRate", proRate.getYearRate());
		result.put("awardRate", proRate.getAwardRate());
		result.put("typeTerm", productInfo.getTypeTerm());
		result.put("incomeType", productInfo.getIncomeHandleMethod());
		result.put("incomeDate", invest.getInvestDate());//起息日期=投资日期(利息当天投资当天产生)
		result.put("investCount", invest.getInvestAmount());//投资金额
		
		AtoneInfoEntity atoneInfo = atoneInfoRepository.findByInvestId((String)paramsMap.get("investId"));
		String state = "";
		if( atoneInfo == null )
			state = Constant.TERM_INVEST_STATUS_EARN;
		if( atoneInfo != null && Arrays.asList(Constant.TERM_INVEST_STATUS_WAIT,Constant.TERM_INVEST_STATUS_ADVANCE).contains(invest.getInvestStatus())){
			state =  invest.getInvestStatus();
			result.put("withDrawAmount", atoneInfo.getAtoneTotalAmount());//赎回金额(已到期投资)
		}

		if( atoneInfo != null && Arrays.asList(Constant.TERM_INVEST_STATUS_FINISH,Constant.TERM_INVEST_STATUS_ADVANCE_FINISH).contains(invest.getInvestStatus())){
			state =  invest.getInvestStatus();//已退出显示金额
			result.put("withDrawAmount", atoneInfo.getAtoneTotalAmount());//赎回金额(已到期投资)
			result.put("withDrawDate", atoneInfo.getCreateDate());//赎回日期(已到期投资) 
		}
		
		//持有中、赎回中显示金额
		if(Arrays.asList(Constant.TERM_INVEST_STATUS_EARN,Constant.TERM_INVEST_STATUS_WAIT,Constant.TERM_INVEST_STATUS_ADVANCE).contains(state)){
			BigDecimal preIncome = ArithUtil.mul(invest.getInvestAmount(), ArithUtil.add(proRate.getYearRate(), proRate.getAwardRate()));
			preIncome = ArithUtil.div(ArithUtil.mul(preIncome, productInfo.getTypeTerm()), new BigDecimal("12"));
			result.put("preIncome", preIncome);//预计收益=投资金额*(利率+奖励利率)*期数/12
			result.put("date", invest.getExpireDate());//到期日期
		}
		result.put("state", state);
		result.put("enable", getEnableAtone(invest.getId(), productInfo.getId()));
		log.info("yangyanqin测试：----是否可以赎回,"+result.get("enable"));
		return new ResultVo(true,"定期宝投资详情查询成功",result);
	}

	/**
	 * 定期宝投资详情-加入记录
	 */
	@Override
	public ResultVo getFixedTradeInfoPage(Map<String, Object> paramsMap)throws SLException {
		PageFuns.numToIndex(paramsMap);
		paramsMap.put("start", paramsMap.get("pageNum"));
		paramsMap.put("length", paramsMap.get("pageSize"));
		paramsMap.put("opearteDateBegin", paramsMap.get("startDate"));
		paramsMap.put("opearteDateEnd", paramsMap.get("endDate"));
		paramsMap.put("productType", Constant.PRODUCT_TYPE_04);
		return new ResultVo(true," 查询加入记录成功",PageFuns.pageVoToMap(accountFlowInfoRepositoryCustom.findAllBaoAccountDetailByCustId(paramsMap)));
	}
	
	
	/**
	 * 用户产品可购买额度 
	 */
	@Override
	public BigDecimal getCurrUseBuyAmount(Map<String,Object> paramsMap) throws SLException{
		BigDecimal maxInvest = paramService.findMaxInvestAmount();//个人可投资最大金额
		BigDecimal ownerAmount = custInfoRepository.getUserHoldAmount((String)paramsMap.get("custId"), (String)paramsMap.get("productId"));//用户持有份额
		BigDecimal _amount = maxInvest.subtract(ownerAmount);
		BigDecimal currUseBuyAmount = _amount;
		ProductDetailInfoEntity productDetail =  productDetailInfoRepository.findByProductId((String)paramsMap.get("productId"));//产品详情
		log.info("maxInvest:"+maxInvest+",ownerAmount"+ownerAmount+""+",currUsableValue"+productDetail.getCurrUsableValue());
		if( _amount.compareTo(BigDecimal.ZERO)<0 ){
			currUseBuyAmount = BigDecimal.ZERO;
		}else if(_amount.compareTo(productDetail.getCurrUsableValue())>0){
			currUseBuyAmount = productDetail.getCurrUsableValue();
		}
		return currUseBuyAmount;
	}
	
	/**
	 * 校验产品时候可以提前赎回 
	 */
	@Override
	public boolean getEnableAtone(String investId,String id)throws SLException{
		ProductInfoEntity product = productInfoRepository.findOne(id);
		if(product == null)
			throw new SLException("产品不存在");
		
		InvestInfoEntity invest = investInfoRepository.findOne(investId);
		if(invest == null)
			throw new SLException("投资不存在");
		
		if(Arrays.asList(Constant.TERM_INVEST_STATUS_WAIT,Constant.TERM_INVEST_STATUS_FINISH,Constant.TERM_INVEST_STATUS_ADVANCE,Constant.TERM_INVEST_STATUS_ADVANCE_FINISH).contains(invest.getInvestStatus()))
			return false;
		
		BigDecimal seatTerm = product.getSeatTerm();
		if( seatTerm == null )
			return false;
		
		return DateUtils.compare_date(new Date(), org.apache.commons.lang3.time.DateUtils.addMonths(DateUtils.parseDate(invest.getInvestDate(), "yyyyMMdd"), seatTerm.intValue()));
	}
	
	/**
	 * 定期宝分页列表查询-按照欢迎程度
	 */
	@Override
	public ResultVo getInvestListByFavoPage(Map<String, Object> paramsMap) throws SLException{
		Page<Map<String, Object>> page = fixedInvestRepositoryCustom.getInvestListByFavoPage(paramsMap);
		return new ResultVo(true,"定期宝分页列表查询成功",PageFuns.pageVoToMap(page));
	}

	/**
	 * 申请成为金牌推荐人条件判断
	 */
	@Override
	public ResultVo getRecommendFalg(Map<String, Object> paramsMap)throws SLException {
		Map<String,Object> data = Maps.newHashMap();
		data.put("amountIsEnable", ResultVo.isSuccess(getInvestAmountIsEnable(paramsMap)) ? true : false);
		data.put("recomNumIsEnable", ResultVo.isSuccess(recomNumIsEnable(paramsMap)) ? true : false);
//		AuditInfoEntity audit =  auditInfoRepository.findFirstByCustIdAndApplyTypeAndRecordStatusOrderByApplyTimeDesc((String)paramsMap.get("custId"), Constant.OPERATION_TYPE_23, Constant.VALID_STATUS_VALID);
		List<AuditInfoEntity> auditInfoList = auditInfoRepository.findByCustIdAndApplyTypeAndRecordStatusOrderByApplyTimeDesc((String)paramsMap.get("custId"), Constant.OPERATION_TYPE_23, Constant.VALID_STATUS_VALID);
		AuditInfoEntity audit = null ;
		if(auditInfoList != null && auditInfoList.size() > 0){
			audit = auditInfoList.get(0);
		}
		
		String isApplied = Constant.UNAPPLY_RECOMMEND;
		if(audit != null){
			switch (audit.getAuditStatus() != null ? audit.getAuditStatus() : "") {
			case Constant.AUDIT_STATUS_REVIEWD_RECOMMEND:
				isApplied =  Constant.APPLYING_RECOMMEND;
				break;
			case Constant.AUDIT_STATUS_PASS_RECOMMEND:
				isApplied =  Constant.APPLYED_RECOMMEND;
				break;
			case Constant.AUDIT_STATUS_REfUSE_RECOMMEND:
				isApplied =  Constant.APPLYED_RECOMMEND_REFUSE;
				break;
			case Constant.AUDIT_STATUS_PASS_RELIEVE:
				isApplied =  Constant.AUDIT_STATUS_PASS_RELIEVE;
				break;
			default:
				break;
			}
		}
		data.put("isApplied",isApplied);
		return new ResultVo(true,"查询申请金牌推荐人条件成功",data);
	}
	
	/**
	 * 定期宝在投金额是否合格(不小于1000)
	 */
	@Override
	public ResultVo getInvestAmountIsEnable(Map<String, Object> paramsMap) throws SLException{
		BigDecimal investedAmount = investInfoRepository.getInvestedAmount((String)paramsMap.get("custId"), Constant.PRODUCT_TYPE_04);
		if(Constant.INEST_AMOUNT_MIN.compareTo(investedAmount)==1)
			return new ResultVo(false);
		return new ResultVo(true,"定期宝在投金额校验合格");
	}
	
	/**
	 * 推荐好友数是否合格
	 */
	@Override
	public ResultVo recomNumIsEnable(Map<String, Object> paramsMap) throws SLException{
		if(5 > custInfoRepository.getRecCountByCustId((String)paramsMap.get("custId"), (String)paramsMap.get("custId")))
			return new ResultVo(false);
		return new ResultVo(true,"推荐好友数校验合格");
	}
	
	/**
	 * 申请金牌推荐人
	 */
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Override
	public ResultVo putRecommendInfo(Map<String, Object> paramsMap)throws SLException {
		/**校验投资金额**/
		ResultVo investAmountIsEnableResult = getInvestAmountIsEnable(paramsMap);
		if(!ResultVo.isSuccess(investAmountIsEnableResult))
			return investAmountIsEnableResult;
		
		/**校验推荐人数**/
		ResultVo recomNumIsEnableResult = recomNumIsEnable(paramsMap);
		if(!ResultVo.isSuccess(recomNumIsEnableResult))
			return recomNumIsEnableResult;
		
		/**提交审核数据**/
		CustInfoEntity custInfo = custInfoRepository.findOne((String)paramsMap.get("custId"));
		if(null == custInfo)
			throw new SLException("用户不存在");
		if(null != custInfo && Constant.IS_RECOMMEND_YES.equals(custInfo.getIsRecommend()))
			throw new SLException("用户已经是金牌推荐人");
		
//		AuditInfoEntity auditInfo = auditInfoRepository.findFirstByCustIdAndApplyTypeAndRecordStatusOrderByApplyTimeDesc(custInfo.getId(),Constant.OPERATION_TYPE_23, Constant.VALID_STATUS_VALID);
		List<AuditInfoEntity> auditInfoList = auditInfoRepository.findByCustIdAndApplyTypeAndRecordStatusOrderByApplyTimeDesc(custInfo.getId(), Constant.OPERATION_TYPE_23, Constant.VALID_STATUS_VALID);
		AuditInfoEntity auditInfo = null ;
		if(auditInfoList != null && auditInfoList.size() > 0){
			auditInfo = auditInfoList.get(0);
		}
		
		if(null != auditInfo && ( Arrays.asList(Constant.AUDIT_STATUS_REVIEWD_RECOMMEND,Constant.AUDIT_STATUS_PASS_RECOMMEND).contains(auditInfo.getAuditStatus())) )
			throw new SLException("已经提交申请");
		/**提交申请**/
		CustApplyInfoEntity custApply = new CustApplyInfoEntity(custInfo.getId(), Constant.OPERATION_TYPE_23, new StringBuffer(custInfo.getLoginName()).append("申请金牌推荐人").toString(), Constant.AUDIT_STATUS_REVIEWD_RECOMMEND, flowNumberService.generateRecCustApplyNo(), new Date(), "");
		custApply.setBasicModelProperty(custInfo.getId(), true);
		custApplyInfoRepository.save(custApply);
		
//		AuditInfoEntity auditInfoEntity = new AuditInfoEntity(custInfo.getId(), Constant.TABLE_BAO_T_CUST_INFO, custInfo.getId(), Constant.OPERATION_TYPE_23, new Date(), Constant.AUDIT_STATUS_REVIEWD_RECOMMEND, null);
		AuditInfoEntity auditInfoEntity = new AuditInfoEntity(custInfo.getId(), Constant.TABLE_BAO_T_CUST_APPLY_INFO, custApply.getId(), Constant.OPERATION_TYPE_23, new Date(), Constant.AUDIT_STATUS_REVIEWD_RECOMMEND, null);
		auditInfoEntity.setBasicModelProperty(custInfo.getId(), true);
		auditInfoRepository.save(auditInfoEntity);
		
		LogInfoEntity recLog = new LogInfoEntity(Constant.TABLE_BAO_T_CUST_INFO, custInfo.getId(), Constant.OPERATION_TYPE_23, null, null, custApply.getApplyDesc(), custInfo.getId());
		recLog.setBasicModelProperty(custInfo.getId(), true); 
		recLog.setOperIpaddress((String)paramsMap.get("operIpaddress"));
		logInfoEntityRepository.save(recLog);
		
		return new ResultVo(true,"申请成功");
	}

}
