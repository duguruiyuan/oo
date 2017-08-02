/** 
 * @(#)AppThirdServiceImpl.java 1.0.0 2015年8月19日 下午6:25:29  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.mobile.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.ProductDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductRateInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.AtoneInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductRateInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.AccountFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.AtoneInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.FixedInvestRepositoryCustom;
import com.slfinance.shanlincaifu.service.AccountService;
import com.slfinance.shanlincaifu.service.CustActivityInfoService;
import com.slfinance.shanlincaifu.service.CustomerService;
import com.slfinance.shanlincaifu.service.FixedInvestService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.RecommedBusiService;
import com.slfinance.shanlincaifu.service.TermService;
import com.slfinance.shanlincaifu.service.impl.FixedInvestmentService;
import com.slfinance.shanlincaifu.service.mobile.AppThirdService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.PageFuns;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.shanlincaifu.utils.UserUtils;
import com.slfinance.vo.ResultVo;

/**   
 *  善林财富三期手机端接口实现
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年8月19日 下午6:25:29 $ 
 */
@Service
public class AppThirdServiceImpl implements AppThirdService {

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
	private FixedInvestService fixedInvestService;
	
	@Autowired
	private FixedInvestmentService fixedInvestmentService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private TermService termService;
	
	@Autowired
	private AtoneInfoRepositoryCustom atoneInfoRepositoryCustom;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private RecommedBusiService recommedBusiService;
	
	/**
	 * 活期宝、体验宝、定期宝产品列表
	 */
	@Override
	public ResultVo investListAll(Map<String, Object> paramsMap)throws SLException {

//		List<Map<String,Object>> data = Lists.newArrayList();
//		
//		Map<String,Object> paramsMapSec = Maps.newHashMap();
//		paramsMapSec.put("pageNum", 0);
//		paramsMapSec.put("pageSize", Integer.MAX_VALUE);
//		paramsMapSec.put("productList", Arrays.asList(Constant.PRODUCT_TYPE_01,Constant.PRODUCT_TYPE_03));
//		Page<Map<String,Object>> secPage = fixedInvestRepositoryCustom.getInvestPage(paramsMapSec);
//		if(secPage != null )
//			data.addAll(secPage.getContent());
//		
//		Map<String,Object> paramsMapFixed = Maps.newHashMap();
//		paramsMapFixed.put("pageNum", 0);
//		paramsMapFixed.put("pageSize", Integer.MAX_VALUE);
//		Page<Map<String,Object>> fixedPage = fixedInvestRepositoryCustom.getInvestPage(paramsMapFixed);
//		if(fixedPage != null )
//			data.addAll(fixedPage.getContent());
//		
//		Collections.sort(data, new ProductComparator());
		
		Map<String,Object> paramsMapSec = Maps.newHashMap();
		paramsMapSec.put("pageNum", 0);
		paramsMapSec.put("pageSize", Integer.MAX_VALUE);
		if(org.springframework.util.StringUtils.isEmpty(paramsMap.get("productList")) && StringUtils.isEmpty((String)paramsMap.get("typeName"))) {
			paramsMapSec.put("productList", Arrays.asList(Constant.PRODUCT_TYPE_01,Constant.PRODUCT_TYPE_03,Constant.PRODUCT_TYPE_04));
		}
		else if(!StringUtils.isEmpty((String)paramsMap.get("typeName"))){
			paramsMapSec.put("typeName", (String)paramsMap.get("typeName"));
		}
		else {
			paramsMapSec.put("productList", paramsMap.get("productList"));
		}
		
		Page<Map<String,Object>> secPage = fixedInvestRepositoryCustom.getInvestPage(paramsMapSec);
		
		//log.debug(data.toString());
		
		return new ResultVo(true,"产品列表查询成功", secPage.getContent());
	}
	
	/**
	 * 优先级排序
	 * 
	 * @author wangjf
	 *
	 */
	public class ProductComparator implements Comparator<Map<String, Object>> {

		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			String priority1 = (String)o1.get("productCat");
			String priority2 = (String)o2.get("productCat");			
			
			int p1 = priority1.equals(Constant.PRODUCT_TYPE_01) ? 1 : (priority1.equals(Constant.PRODUCT_TYPE_04) ? 2 : 3);
			int p2 = priority2.equals(Constant.PRODUCT_TYPE_01) ? 1 : (priority2.equals(Constant.PRODUCT_TYPE_04) ? 2 : 3);
			
			return p1 > p2 ? 1 : (p1 == p2 ? 0 : -1);
		}
		
	}

	/**
	 * 活期宝、体验宝、定期宝购买投资页面
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo investbBuyDetail(Map<String, Object> paramsMap)throws SLException {
		Map<String,Object> data = Maps.newHashMap();
		paramsMap.put("id", (String)paramsMap.get("productId"));
		ResultVo result = fixedInvestService.getFixedInvestDatail(paramsMap);
		if(!ResultVo.isSuccess(result) )
			return new ResultVo(false,"加载投资信息出错");
		Map<String,Object> dataInvest = (Map<String,Object>)result.getValue("data");
		if(dataInvest == null)
			return new ResultVo(false,"加载投资信息出错");
		data.put("id", (String)paramsMap.get("productId"));
		data.put("yearRate", dataInvest.get("yearRate"));
		data.put("termName", dataInvest.get("termName"));
		data.put("awardRate", dataInvest.get("awardRate"));
		data.put("useableAmount", dataInvest.get("useableAmount"));
		data.put("incomeTopDate", dataInvest.get("incomeToAmountTime"));
		data.put("investCount", dataInvest.get("investCount"));
		data.put("loanCount", dataInvest.get("loanCount"));
		data.put("currUseBuyAmount", dataInvest.get("currUseBuyAmount"));
		data.put("userUseableAmount", dataInvest.get("userUseableAmount"));
		data.put("investMaxAmount", dataInvest.get("investMaxAmount"));
		data.put("typeTerm", dataInvest.get("typeTerm"));
		data.put("productDesc", dataInvest.get("productDesc"));
		data.put("incomeDate", dataInvest.get("incomeDate"));
		data.put("withDrawDate", dataInvest.get("withDrawDate"));
		data.put("date", dataInvest.get("date"));
		data.put("state", dataInvest.get("state"));
		
		UserUtils.judgeCanSale(data);
		
		return new ResultVo(true,"查询投资页面成功",data);
	}

	/**
	 * 交易记录
	 */
	@Override
	public ResultVo transactionList(Map<String, Object> paramsMap)throws SLException {
		return new ResultVo(true,"查询交易记录成功",fixedInvestService.getFixedTradeInfoPage(PageFuns.stringParamsConvertInt(paramsMap)));
	}

	/**
	 * 我的投资-活期宝、定期宝、体验宝页面
	 */
	@Override
	public ResultVo investStatisticsInfo(Map<String, Object> paramsMap)throws SLException {
		Map<String,Object> data = Maps.newHashMap();
		Map<String,Object> investStatis = Maps.newHashMap();
		String productType = (String)paramsMap.get("productType");
		String tradeType = "";
		switch (productType) {
		case Constant.PRODUCT_TYPE_01:
			tradeType = SubjectConstant.TRADE_FLOW_TYPE_01;
			getInvestStatis(investStatis,productType,(String)paramsMap.get("custId"),tradeType);
			break;
		case Constant.PRODUCT_TYPE_03:
			tradeType = SubjectConstant.TRADE_FLOW_TYPE_03;
			getInvestStatis(investStatis,productType,(String)paramsMap.get("custId"),tradeType);
			break;
		case Constant.PRODUCT_TYPE_04:
			if( paramsMap.get("pageNum") == null  || paramsMap.get("pageSize") == null)
				throw new SLException("分页参数不能为空");
			PageFuns.pageNumToPageIndex(paramsMap);
			paramsMap.put("userId", paramsMap.get("custId"));
			Map<String,Object> rateMap = fixedInvestRepositoryCustom.getProductRate(Constant.PRODUCT_TYPE_04);
			investStatis.put("yearRate", rateMap != null ? rateMap.get("minYearRate") : BigDecimal.ZERO);
			investStatis.put("maxYearRate", rateMap != null ? rateMap.get("maxYearRate") : BigDecimal.ZERO);
			investStatis.put("awardRate", rateMap != null ? rateMap.get("minAwardRate") : BigDecimal.ZERO);
			ResultVo investListResult = fixedInvestmentService.queryFixedInvestment(paramsMap);
			if(ResultVo.isSuccess(investListResult))
				data.put("investList", investListResult.getValue("data") != null ? investListResult.getValue("data") : Maps.newHashMap());
			investStatis.remove("yesterdayTradeAmount");
			investStatis.put("preIncome", fixedInvestRepositoryCustom.getPreIncomeAllProduct(Arrays.asList(productType), Arrays.asList(Constant.TERM_INVEST_STATUS_ADVANCE,Constant.TERM_INVEST_STATUS_EARN,Constant.TERM_INVEST_STATUS_WAIT), (String)paramsMap.get("custId"), new Date()));
			tradeType = SubjectConstant.TRADE_FLOW_TYPE_03;
			/**累计收益(盈亏)**/
			investStatis.put("totalIncome", fixedInvestRepositoryCustom.getAtonedAmount((String)paramsMap.get("custId"), Arrays.asList(Constant.PRODUCT_TYPE_04), Arrays.asList(Constant.TERM_INVEST_STATUS_FINISH,Constant.TERM_INVEST_STATUS_ADVANCE_FINISH)));
			/**持有份额**/
			investStatis.put("holdAmount", investInfoRepository.getInvestingAmout((String)paramsMap.get("custId"), productType));
			break;
		}
		
		/**昨日收益**/
		Map<String,Object> yesMap = Maps.newHashMap();
		yesMap.put("custId", paramsMap.get("custId"));
		yesMap.put("tradeDate", DateUtils.addDays(new Date(), -1));
		yesMap.put("tradeType", Arrays.asList(tradeType));
		investStatis.put("yestIncome", accountFlowInfoRepositoryCustom.findIncomeByCustId(yesMap).toPlainString());
		
		/**用户累计赎回金额**/
		investStatis.put("totalAtone",Constant.PRODUCT_TYPE_03.equals(productType) ? atoneInfoRepositoryCustom.findSumAlreadyAtoneAmount(Constant.OPERATION_TYPE_15, (String)paramsMap.get("custId")):atoneInfoRepository.findAtonedAmountByType((String)paramsMap.get("custId"),Constant.TRADE_STATUS_03, Constant.AUDIT_STATUS_PASS,productType));
		/**累计投资**/
		investStatis.put("totalInvest", investInfoRepository.getInvestedAmount((String)paramsMap.get("custId"), productType));
		
		data.put("investStatis", investStatis);
		return new ResultVo(true,"查询投资页面成功",data);

	}

	/**
	 * 定期宝投资详情
	 */
	@Override
	public ResultVo fixedInvestDetail(Map<String, Object> paramsMap)throws SLException {
		ResultVo result =  fixedInvestService.getFixedInvestDetail(paramsMap);
		return new ResultVo(true,"定期宝投资详情查询",ResultVo.isSuccess(result) ? result.getValue("data") :Maps.newHashMap());
	}

	/**
	 * 提前退出定期宝详情
	 */
	@Override
	public ResultVo preAtoneDetail(Map<String, Object> paramsMap)throws SLException {
		Map<String,Object> atoneMap = termService.findAdvancedAtone(paramsMap);
		if(atoneMap != null){
			atoneMap.put("termName", atoneMap.get("investTitle"));//退出期数
			atoneMap.put("tradeAmount", atoneMap.get("investAmount"));//加入金额
			atoneMap.put("preIncomeAmount", atoneMap.get("exceptAmount"));//预计回收金额
			atoneMap.put("expensive", atoneMap.get("expense"));//提前退出费用
		}
		return new ResultVo(true,"提前退出定期宝详情",atoneMap);
	}
	
	
	/**
	 * 购买定期宝
	 */
	@Override
	public ResultVo joinTermBao(Map<String, Object> params) throws SLException{
		return termService.joinTermBao(params);
	}
	
	/**
	 * 定期宝产品加入记录
	 */
	@Override
	public ResultVo joinListInfoTrd(Map<String,Object> paramsMap) throws SLException{
		return fixedInvestService.getInvestListByProIdPage(PageFuns.stringParamsConvertInt(paramsMap));
	}

	/**
	 * 申请提前赎回定期宝
	 */
	@Override
	public ResultVo termWithdrawApply(Map<String, Object> params) throws SLException{
		return termService.termWithdrawApply(params);
	}

	/**
	 * 申请成为金牌推荐人条件判断
	 */
	@Override
	public ResultVo recommendFalg(Map<String, Object> paramsMap)throws SLException {
		return fixedInvestService.getRecommendFalg(paramsMap);
	}

	/**
	 * 申请金牌推荐人
	 */
	@Override
	public ResultVo putRecommend(Map<String, Object> paramsMap)throws SLException {
		return fixedInvestService.putRecommendInfo(paramsMap);
	}

	/**
	 *  金牌推荐人佣金统计信息
	 */
	@Override
	public ResultVo RecInfo(Map<String, Object> paramsMap) throws SLException {
		return null;
	}

	/**
	 * 佣金详情列表
	 */
	@Override
	public ResultVo awardList(Map<String, Object> paramsMap) throws SLException {
		return null;
	}
	
	/**
	 * 统计信息(推广奖励、年化投资、推荐好友)和推广奖励信息(活期宝、定期宝)
	 */
	@Override
	public ResultVo recommedInfo(Map<String, Object> paramsMap)throws SLException {
		return recommedBusiService.getRecommedInfo(PageFuns.stringParamsConvertInt(paramsMap));
	}

	/**
	 * 金牌推荐人当天或当月在投详情
	 */
	@Override
	public ResultVo investListDetail(Map<String, Object> paramsMap)throws SLException {
		return recommedBusiService.getInvestListDetail(PageFuns.stringParamsConvertInt(paramsMap));
	}
	
	
	
//------------私有方法----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	/***
	 * 私有方法-组装产品id、利率、奖励利率信息
	 * 
	 * zhangzs
	 * 2015年8月20日
	 * @param investStatis
	 * @param productType
	 * @param custId
	 * @param tradeType
	 * @throws SLException
	 */
	private void getInvestStatis(Map<String, Object> investStatis,String productType,String custId,String tradeType) throws SLException{
		ProductInfoEntity product = productInfoRepository.findProductInfoByProductTypeName(productType);
		if(product == null)
			throw new SLException("产品不存在");
		
		ProductDetailInfoEntity productDetail =  productDetailInfoRepository.findByProductId(product.getId());
		if(productDetail == null)
			throw new SLException("加载产品详情出错");
		
		ProductRateInfoEntity productRate = productRateInfoRepository.findTopByProductIdOrderByYearRateAscAwardRateAsc(product.getId());
		if(productRate == null )
			throw new SLException("加载产品利率出错");
		
		investStatis.put("id", product.getId());
		investStatis.put("yearRate", productRate.getYearRate());
		investStatis.put("awardRate", productRate.getAwardRate());
		
		Map<String,Object> totalIncomeMap = Maps.newHashMap();
		totalIncomeMap.put("custId", custId);
		totalIncomeMap.put("tradeType", Arrays.asList(tradeType));
		investStatis.put("totalIncome", accountFlowInfoRepositoryCustom.findIncomeByCustId(totalIncomeMap).toPlainString());
		
		/**持有份额**/
		investStatis.put("holdAmount", accountFlowInfoRepositoryCustom.getHoldingAmount(custId, Arrays.asList(productType)));
		
		/**活期宝添加投资管理类别记录列表**/
		if(Constant.PRODUCT_TYPE_01.equals(productType)){
			Map<String,Object> manCatMap = Maps.newHashMap();
			manCatMap.put("custId", custId);
			manCatMap.put("productId", product.getId());
			investStatis.put("investManage", accountService.findRewardInfo(manCatMap));
		}
		
	}

	@Override
	public ResultVo investListAllWithYearRateList(Map<String, Object> paramsMap)
			throws SLException {
		
		Map<String, Object> result = Maps.newHashMap();
		
		Map<String,Object> paramsMapSec = Maps.newHashMap();
		paramsMapSec.put("pageNum", 0);
		paramsMapSec.put("pageSize", Integer.MAX_VALUE);
		if(org.springframework.util.StringUtils.isEmpty(paramsMap.get("productList")) && StringUtils.isEmpty((String)paramsMap.get("typeName"))) {
			paramsMapSec.put("productList", Arrays.asList(Constant.PRODUCT_TYPE_01,Constant.PRODUCT_TYPE_03,Constant.PRODUCT_TYPE_04));
		}
		else if(!StringUtils.isEmpty((String)paramsMap.get("typeName"))){
			paramsMapSec.put("typeName", (String)paramsMap.get("typeName"));
			List<ProductRateInfoEntity> rateList = productRateInfoRepository.findProductRateInfoByTypeName((String)paramsMap.get("typeName"));
			result.put("rateList", rateList);
		}
		else {
			paramsMapSec.put("productList", paramsMap.get("productList"));
		}
		
		Page<Map<String,Object>> secPage = fixedInvestRepositoryCustom.getInvestPage(paramsMapSec);
		result.put("productList", secPage.getContent());

		return new ResultVo(true,"产品列表查询成功", result);
	}
}
