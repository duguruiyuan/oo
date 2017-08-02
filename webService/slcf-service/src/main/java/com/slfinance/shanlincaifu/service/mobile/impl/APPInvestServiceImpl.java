/** 
 * @(#)APPInvestServiceImpl.java 1.0.0 2015年5月21日 上午10:07:06  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.mobile.impl;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductRateInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.AtoneInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductRateInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductTypeInfoRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.AccountFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.AtoneInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.FixedInvestRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.CustActivityInfoService;
import com.slfinance.shanlincaifu.service.CustomerService;
import com.slfinance.shanlincaifu.service.InvestService;
import com.slfinance.shanlincaifu.service.LoanInfoService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.ProjectService;
import com.slfinance.shanlincaifu.service.mobile.APPInvestService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.PageFuns;
import com.slfinance.shanlincaifu.utils.UserUtils;
import com.slfinance.vo.ResultVo;

/**   
 * 手机端我的投资-活期宝业务接口实现
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月21日 上午10:07:06 $ 
 */
@Service
public class APPInvestServiceImpl implements APPInvestService {

	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private InvestService investService;
	
	@Autowired
	private ProductRateInfoRepository productRateInfoRepository;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private CustActivityInfoService custActivityInfoService;
	
	@Autowired
	private AtoneInfoRepository atoneInfoRepository;
	
	@Autowired
	private ProductInfoRepository productInfoRepository;

	@Autowired
	private AtoneInfoRepositoryCustom atoneInfoRepositoryCustom;
	
	@Autowired
	AccountFlowInfoRepositoryCustom accountFlowInfoRepositoryCustom;
	
	@Autowired
	InvestInfoRepository  investInfoRepository;
	
	@Autowired
	ProductTypeInfoRepository productTypeInfoRepository;
	
	@Autowired
	ProductDetailInfoRepository productDetailInfoRepository;
	
	@Autowired
	LoanInfoRepositoryCustom loanInfoRepositoryCustom;
	
	@Autowired
	FixedInvestRepositoryCustom fixedInvestRepositoryCustom;
	
	@Autowired
	RepositoryUtil repositoryUtil;
	
	@Autowired
	ProjectService projectService;
	
	/**
	 * 账户信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo accountInfo(Map<String, Object> paramsMap)throws SLException {
		
		ResultVo result = new ResultVo(true);
		Map<String,Object> resultMap = Maps.newHashMap();
		
		/**获取昨日收益、累计收益、累计接入金额、累计赎回金额、持有债券数**/
		ResultVo latestResult =  customerService.findBaoCountInfoByCustId(paramsMap);
		if(ResultVo.isSuccess(latestResult)){
			Map<String,Object> map = (Map<String,Object>)latestResult.getValue("data");
			resultMap.put("yesterdayTradeAmount", map.get("yesterdayTradeAmount"));
			resultMap.put("yesterdayTradeAmountIos", map.get("yesterdayTradeAmount") ==null ? "0":((BigDecimal)map.get("yesterdayTradeAmount")) .toPlainString());
			resultMap.put("sumIncome", map.get("sumTradeAmount"));
			resultMap.put("sumJoinAmount", map.get("sumJoinAmount"));
			resultMap.put("loanCount", map.get("accountAmount"));
			resultMap.put("accountAmount", map.get("accountAmount"));
		}
		
		/**个人累计投资奖励**/
		resultMap.put("alreadyInvestAmount", investInfoRepository.getAlreadyInvestAmount((String)paramsMap.get("custId"), (String)paramsMap.get("productType")));
		
		/**赎回体验宝时赎回表不新增记录，好X的设计**/
		switch ((String)paramsMap.get("productType")) {
		case Constant.PRODUCT_TYPE_01:
			ProductInfoEntity productInfo = productInfoRepository.findProductInfoByProductTypeName((String)paramsMap.get("productType"));
			resultMap.put("sumRedemptionsAmount", atoneInfoRepository.findSumAlreadyAtoneAmount((String)paramsMap.get("custId"), productInfo.getId(), Constant.TRADE_STATUS_03, Constant.AUDIT_STATUS_PASS));
			break;
		default:
			resultMap.put("sumRedemptionsAmount",atoneInfoRepositoryCustom.findSumAlreadyAtoneAmount(Constant.OPERATION_TYPE_15, (String)paramsMap.get("custId")));
			break;
		}
		
		/**年化收益率**/
		List<ProductRateInfoEntity> rateInfoList = productRateInfoRepository.findProductRateInfoByTypeName((String)paramsMap.get("productType"));

		if(rateInfoList != null &&  rateInfoList.size() > 0){
			resultMap.put("minYearRate", rateInfoList.get(0).getYearRate());
			resultMap.put("minAwardRate", rateInfoList.get(0).getAwardRate());
			if(rateInfoList.size() > 1)
				resultMap.put("maxYearRate", rateInfoList.get(rateInfoList.size() - 1).getYearRate());
		}
		result.putValue("data", resultMap);
		
		return result;
	}

	/**
	 * 赎回操作详细页面
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo redeemInfo(Map<String, Object> paramsMap)throws SLException {
		ResultVo result = new ResultVo(true);
		Map<String,Object> resultMap = Maps.newHashMap();
		/**可赎回金额(当前持有价值)**/
		ResultVo latestResult = customerService.findBaoCountInfoByCustId(paramsMap);
		if(ResultVo.isSuccess(latestResult)){
			Map<String,Object> map = (Map<String,Object>)latestResult.getValue("data");
			resultMap.put("accountAmount", map.get("accountAvailableValue"));
		}
		result.putValue("data", resultMap);
		return result;
	}

	/**
	 * 赎回操作
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo redeem(Map<String, Object> paramsMap) throws SLException {
		if(!Arrays.asList(Constant.ATONE_METHOD_NORMAL,Constant.ATONE_METHOD_IMMEDIATE).contains((String)paramsMap.get("redeemType")))
			return new ResultVo(false,"赎回失败","赎回方式不正确");
		
		ResultVo result = new ResultVo(true);
		switch ((String) paramsMap.get("redeemType")) {
		/**普通赎回**/
		case Constant.ATONE_METHOD_NORMAL:
			result = investService.withdrawBaoNormal(paramsMap);
			break;
		/**快速赎回**/
		case Constant.ATONE_METHOD_IMMEDIATE:
			result = investService.withdrawBao(paramsMap);
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * 债权组成列表
	 */
	@Override
	public ResultVo loanList(Map<String, Object> paramsMap) throws SLException {
		ResultVo result = new ResultVo(true);
		paramsMap.put("start", Integer.valueOf((String)paramsMap.get("pageNum")));
		paramsMap.put("length", Integer.valueOf((String)paramsMap.get("pageSize")));
		int start = (int)paramsMap.get("start");
		if(start > 0)
			start  = start * (int)paramsMap.get("length") ;
		paramsMap.put("start", start);
		result.putValue("data", loanInfoService.findLoanList(paramsMap));
		return result;
	}

	/**
	 * 购买活期宝页面详细
	 */
	public ResultVo buyDetailToCurrent(Map<String,Object> paramsMap)throws SLException{
		
		Map<String,Object> model = Maps.newHashMap();
		paramsMap.put("productName", Constant.PRODUCT_TYPE_01);
//		/**获取加入记录**/
//		Map<String,Object> joinedCountMap = accountFlowInfoRepositoryCustom.joinedCount(paramsMap);
//		model.put("joinedCount", null != joinedCountMap ? joinedCountMap.get("count"):0);
		
		/**获取产品详情信息**/ 
		paramsMap.put("exclueInCome", "true");
		Map<String, Object> loanDetail = investService.findBAODetail(paramsMap);
		model.put("currAllowTenderValue", loanDetail.get("currUsableValue"));
		model.put("minYearRate", loanDetail.get("minYearRate"));
		model.put("maxYearRate", loanDetail.get("maxYearRate"));
		model.put("minAwardRate", loanDetail.get("minAwardRate"));
		model.put("joinedCount", loanDetail.get("investCount"));
		

		/**投资开始时间**/
		String interestStartTime = "";
		/**收益总额时间**/
		String incomeToAmountTime = "";
		/**获取产品有效时间**/
		String investValidTime =  paramService.findInvestValidTime();
		if (DateUtils.getCurrentDate("HH:mm:ss").compareTo(investValidTime) < 0) { // 小于生效时间
			interestStartTime = DateUtils.formatDate(new Date(), "yyyy-MM-dd");
			incomeToAmountTime = DateUtils.formatDate(DateUtils.getAfterDay(new Date(), 1), "yyyy-MM-dd");
		} else {
			interestStartTime = DateUtils.formatDate(DateUtils.getAfterDay(new Date(), 1), "yyyy-MM-dd");
			incomeToAmountTime = DateUtils.formatDate(DateUtils.getAfterDay(new Date(), 2), "yyyy-MM-dd");
		}
		model.put("interestStartTime", interestStartTime);
		model.put("incomeToAmountTime", incomeToAmountTime);
		
		if(StringUtils.isNotEmpty((String)paramsMap.get("custId"))){
			/**获取可用余额**/
			AccountInfoEntity custAccountInfo = accountInfoRepository.findByCustId((String)paramsMap.get("custId"));
			if(null == custAccountInfo)
				return new ResultVo(false,"客户账户信息为空",paramsMap);
			model.put("accountAvailableAmount", custAccountInfo.getAccountAvailableAmount());
			model.put("currUsableValue", loanDetail.get("userAllowAmount"));//用户
			model.put("currAllowTenderValue", loanDetail.get("currUsableValue"));//产品
		}
		
		// 取债权笔数
		paramsMap.put("productType", Constant.PRODUCT_TYPE_01);
		Map<String, Object> loanMap = loanInfoRepositoryCustom.findLoanListCount(paramsMap);
		model.put("totalLoans", loanMap.get("totalLoans"));
		
		
		// 取所有利率
		List<ProductRateInfoEntity> rateList = productRateInfoRepository.findProductRateInfoByTypeName(Constant.PRODUCT_TYPE_01);
		model.put("rateList", rateList);
		
		// 判断是否可以售卖
		UserUtils.judgeCanSale(model);
		
		return new ResultVo(true, "活期宝页面详细", model);
	}

	/**
	 * 购买体验宝页面详细
	 */
	public ResultVo buyDetailToExperience(Map<String,Object> paramsMap)throws SLException{
		Map<String,Object> model = Maps.newHashMap();
		paramsMap.put("productName", Constant.PRODUCT_TYPE_03);
		
		/**获取加入记录**/
		Map<String,Object> joinedCountMap = accountFlowInfoRepositoryCustom.joinedCount(paramsMap);
		model.put("joinedCount", null != joinedCountMap ? joinedCountMap.get("count"):0);
		
		/**获取产品详情信息**/ 
		Map<String, Object> loanDetail = investService.findBAODetail(paramsMap);
		model.put("currAllowTenderValue", loanDetail.get("currUsableValue"));//体验宝当前可投金额
		model.put("minYearRate", loanDetail.get("minYearRate"));
		model.put("maxYearRate", loanDetail.get("maxYearRate"));
		model.put("minAwardRate", loanDetail.get("minAwardRate"));
		/**获取产品有效时间**/
		String investValidTime =  paramService.findInvestValidTime();		
		String interestStartTime = "";
		String incomeToAmountTime = "";

		if (DateUtils.getCurrentDate("HH:mm:ss").compareTo(investValidTime) < 0) { // 小于生效时间
			interestStartTime = DateUtils.formatDate(new Date(), "yyyy-MM-dd");
			incomeToAmountTime = DateUtils.formatDate(DateUtils.getAfterDay(new Date(), 1), "yyyy-MM-dd");
		} else {
			interestStartTime = DateUtils.formatDate(DateUtils.getAfterDay(new Date(), 1), "yyyy-MM-dd");
			incomeToAmountTime = DateUtils.formatDate(DateUtils.getAfterDay(new Date(), 2), "yyyy-MM-dd");
		}

		model.put("interestStartTime", interestStartTime);
		model.put("incomeToAmountTime", incomeToAmountTime);
		
		if(StringUtils.isNotEmpty((String)paramsMap.get("custId"))){
			// 体验金奖励信息
			Map<String, Object> experAmountMap = custActivityInfoService.findExperienceGoldById(paramsMap);
			if (experAmountMap != null) {
				model.put("accountAvailableAmount", experAmountMap.get("ExperienceGoldUnUsed"));
			}
			//体验宝的产品和个人的可购买份额是一样的。
			model.put("currUsableValue", loanDetail.get("currUsableValue"));
			model.put("currAllowTenderValue", loanDetail.get("currUsableValue"));
		}
		
		return new ResultVo(true, "体验宝页面详细", model);
	}
	
	/**
	 * 购买活期宝
	 */
	public ResultVo joinCurrentBao(Map<String, Object> params) throws SLException{
		return investService.joinBao(params);
	}
	
	/**
	 * 购买体验宝
	 */
	public ResultVo joinExperienceBao(Map<String, Object> params) throws SLException{
		return investService.joinExperienceBao(params);
	}
	
	/**
	 * 投资列表-体验宝和活期宝信息详细
	 */
	@Override
	public ResultVo investInfo(Map<String, Object> params) throws SLException {
		Map<String,Object> model = Maps.newHashMap();
		params.put("productName", params.get("productType"));
		Map<String, Object> loanDetail = investService.findBAODetail(params);
		model.put("minYearRate", loanDetail.get("minYearRate"));
		model.put("maxYearRate", loanDetail.get("maxYearRate"));
		model.put("currAllowTenderValue", loanDetail.get("currUsableValue"));
		Map<String,Object> joinedCountMap = accountFlowInfoRepositoryCustom.joinedCount(params);
		model.put("joinedCount", null != joinedCountMap ? joinedCountMap.get("count"):0);
		return new ResultVo(true, "体验宝页面详细", model);
	}	
	
	/**
	 * 投资列表-加入记录-加入列表
	 */
	@Override
	public ResultVo joinListInfo(Map<String, Object> params) throws SLException{
		params.put("productName", params.get("productType"));
		PageFuns.pageNumToPageIndex(params);
		Map<String,Object> model = investService.findOwnerList(params);
		model.remove("totalInvestPeopleNum");
		model.remove("totalInvestAmount");
		return new ResultVo(true, "加入列表", model);
	}
	
	@Override
	public ResultVo queryBaoDetail(Map<String, Object> params)
			throws SLException {
		investService.findBAODetail(params);
		
		return new ResultVo(true);
	}

	@Override
	public ResultVo queryExperienceDetail(Map<String, Object> params)
			throws SLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 投资首页展示用户购买活期宝记录，活期宝和体验宝产品介绍。
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo investIndex(Map<String, Object> params)throws SLException{
		Map<String,Object> model = Maps.newHashMap(); 
		//params.put("productName", Constant.PRODUCT_TYPE_01);
		//购买活期宝记录
		//params.put("productList", Arrays.asList(Constant.PRODUCT_TYPE_01,Constant.PRODUCT_TYPE_03,Constant.PRODUCT_TYPE_04));
		//Page<Map<String,Object>> page = fixedInvestRepositoryCustom.findInvestListByProduct(PageFuns.pageIndexToPageNum(PageFuns.stringParamsConvertInt(params)));
		//model.put("buyList", page != null ? page.getContent() : Lists.newArrayList());
		model.put("buyList",  Lists.newArrayList());
		model.put("productList", fixedInvestRepositoryCustom.getProdctYearRateList(params));
		List<Map<String, Object>> result = fixedInvestRepositoryCustom.getPriorProductList(params);
		model.put("termProduct", result.get(0));
		
		params.put("start", 0);
		params.put("length", Integer.MAX_VALUE);
		ResultVo resultVo = projectService.queryAllProjectList(params);
		Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
		if(data != null) {
			List<Map<String, Object>> dataList = (List<Map<String, Object>>)data.get("data");
			if(dataList != null && dataList.size() > 0) {
				model.put("project", dataList.get(0));
			}
		}
		
		model.put("show", Constant.PRODUCT_TYPE_05);

//		//产品介绍记录
//		List<Map<String,Object>> productList = new ArrayList<Map<String,Object>>();
//		for( String productName : Arrays.asList(Constant.PRODUCT_TYPE_01,Constant.PRODUCT_TYPE_03) ){
//			Map<String,Object> productInfo = Maps.newHashMap(); 
//			List<ProductRateInfoEntity> prodList = productRateInfoRepository.findProductRateInfoByTypeName(productName);
//			if( prodList != null && prodList.size() > 0){
//				productInfo.put("minYearRate", prodList.get(0) != null ? prodList.get(0).getYearRate() : BigDecimal.ZERO);
//				productInfo.put("awardRate", prodList.get(0) != null ? prodList.get(0).getAwardRate() : BigDecimal.ZERO);
//			}
//			productInfo.put("procuctName", productName);
//			productList.add(productInfo);
//		}
//		model.put("productList", productList);
//		
//	
//		ProductTypeInfoEntity productTypeInfoEntity = productTypeInfoRepository.findByTypeName(Constant.PRODUCT_TYPE_04);
//		
//		ProductInfoEntity productInfoEntity = repositoryUtil.queryForList("select * From BAO_T_PRODUCT_INFO  where product_type='"+productTypeInfoEntity.getId()+"' and ENABLE_STATUS = '启用' order by to_number(favorite_sort) asc", ProductInfoEntity.class).get(0);
//		ProductDetailInfoEntity productDetailInfoEntity = productDetailInfoRepository.findByProductId(productInfoEntity.getId());
//		ProductRateInfoEntity productRateInfoEntity = productRateInfoRepository.findTopByProductIdOrderByYearRateAscAwardRateAsc(productInfoEntity.getId());
//		
//		//mzhu 20150902
//		HashMap<String,Object> param = new HashMap<String,Object>();
//		param.put("pageNum", 0);
//		param.put("pageSize", 100);
//		param.put("proId", productInfoEntity.getId());
//		
//		Page<Map<String,Object>>  result = fixedInvestRepositoryCustom.getInvestPage(param);
//		
//		// 产品总金额
//		BigDecimal alreadyInvestAmount = investInfoRepository.sumTotalInvestAmountByProductIdAndInvestDate(productInfoEntity.getId(), DateUtils.formatDate(new Date(), "yyyyMMdd"), Constant.TERM_INVEST_STATUS_EARN, productDetailInfoEntity.getCurrTerm());
//		BigDecimal planTotalAmount = ArithUtil.add(alreadyInvestAmount, productDetailInfoEntity.getCurrUsableValue());
//		
//		Map<String, Object> termProduct = Maps.newHashMap(); 
//		termProduct.put("procuctId", productInfoEntity.getId());
//		termProduct.put("procuctType", productTypeInfoEntity.getTypeName());
//		termProduct.put("procuctName", productInfoEntity.getProductName());
//		termProduct.put("currentTerm", productDetailInfoEntity.getCurrTerm());
//		termProduct.put("yearRate", productRateInfoEntity.getYearRate());
//		termProduct.put("awardRate", productRateInfoEntity.getAwardRate());
//		termProduct.put("typeTerm", productInfoEntity.getTypeTerm());
//		termProduct.put("investMinAmount", productInfoEntity.getInvestMinAmount());
//		termProduct.put("investBearinteMethod", productInfoEntity.getInvestBearinteMethod());
//		termProduct.put("incomeHandleMethod", "投资无上限");
//		termProduct.put("planTotalAmount", planTotalAmount);
//		termProduct.put("alreadyInvestAmount", alreadyInvestAmount == null ?  0:alreadyInvestAmount);
//		
//		termProduct.putAll(result.getContent().get(0));
//		model.put("termProduct", termProduct);

		return new ResultVo(true,"购买记录和产品介绍记录查询成功",model);
	}
	
}
