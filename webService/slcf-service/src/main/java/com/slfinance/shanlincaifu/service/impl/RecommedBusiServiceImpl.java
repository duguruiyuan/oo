/** 
 * @(#)RecommedBusiServiceImpl.java 1.0.0 2015年10月12日 下午4:24:56  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.repository.CommissionInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.CommissionInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.RecommedBusiService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.PageFuns;
import com.slfinance.vo.ResultVo;

/**   
 * 金牌推荐人前端WEB业务相关接口实现
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年10月12日 下午4:24:56 $ 
 */
@Service
public class RecommedBusiServiceImpl implements RecommedBusiService {

	@Autowired
	private CommissionInfoRepository commissionInfoRepository;
	
	@Autowired
	private CommissionInfoRepositoryCustom commissionInfoRepositoryCustom;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	private String SETTLED = "是",SETTLE = "否";
	
	/**
	 * 统计信息(推广奖励、年化投资、推荐好友)和推广奖励信息(活期宝、定期宝)
	 */
	@Override
	public ResultVo getRecommedInfo(Map<String, Object> paramsMap)throws SLException {
		Map<String, Object> data = Maps.newHashMap();
		
		if(!Arrays.asList(Constant.PRODUCT_TYPE_01,Constant.PRODUCT_TYPE_04).contains((String)paramsMap.get("productType")))
			throw new SLException("产品类型报错");
		CustInfoEntity cust = custInfoRepository.findOne((String)paramsMap.get("custId"));
		if(null == cust)
			throw new SLException("用户不存在");
		
		/**统计信息(推广奖励、年化投资、推荐好友)**/
		Map<String,Object> statisticInfo = Maps.newHashMap();
		List<Map<String,Object>> awardAndInvest = commissionInfoRepository.sumInvestAmount((String)paramsMap.get("custId"),Constant.PRODUCT_TYPE_01,Constant.PRODUCT_TYPE_04);
		statisticInfo.put("recAward", awardAndInvest != null && awardAndInvest.size() > 0 ? awardAndInvest.get(0): BigDecimal.ZERO );
		BigDecimal yearInveAmount = commissionInfoRepository.getCurrentYearInveAmount(cust.getId(), Constant.PRODUCT_TYPE_04, com.slfinance.shanlincaifu.utils.DateUtils.getFirstDay(DateTime.now().toDate(), "yyyyMMdd"), com.slfinance.shanlincaifu.utils.DateUtils.getLastDay(DateTime.now().toDate(), "yyyyMMdd"));
		
//		statisticInfo.put("investAmount", awardAndInvest != null && awardAndInvest.size() > 1 ? awardAndInvest.get(1): BigDecimal.ZERO );
		statisticInfo.put("investAmount", ArithUtil.add(yearInveAmount != null ? yearInveAmount : BigDecimal.ZERO, awardAndInvest.size() > 1 ? (BigDecimal)awardAndInvest.get(1): BigDecimal.ZERO ));
		statisticInfo.put("recFrieCount", custInfoRepository.getRecCountByCustId(cust.getId(), cust.getId()));
		data.put("statisticInfo", statisticInfo);
		
		/**推广奖励信息(活期宝、定期宝)**/
		if(StringUtils.isNotEmpty((String)paramsMap.get("isSettled")) && SETTLED.equals((String)paramsMap.get("isSettled")))
			paramsMap.put("isSettled",Constant.USER_ACTIVITY_TRADE_STATUS_05);
		
		if(StringUtils.isNotEmpty((String)paramsMap.get("isSettled")) && SETTLE.equals((String)paramsMap.get("isSettled")))
			paramsMap.put("isSettled",Constant.USER_ACTIVITY_TRADE_STATUS_06);
		if(Constant.PRODUCT_TYPE_04.equals((String)paramsMap.get("productType")))
			PageFuns.numToIndex(paramsMap);
		
		data.put("recAwareList", Constant.PRODUCT_TYPE_01.equals((String)paramsMap.get("productType")) 
				? 
				PageFuns.pageVoToMap(commissionInfoRepository.getResListPage(cust.getId(), (String)paramsMap.get("productType"), StringUtils.isEmpty((String)paramsMap.get("startDate")) ? DateUtils.addYears(new Date(), -100):DateTime.parse((String)paramsMap.get("startDate")).toDate(), StringUtils.isEmpty((String)paramsMap.get("endDate")) ? DateUtils.addYears(new Date(), 100):DateTime.parse((String)paramsMap.get("endDate")).toDate(),StringUtils.isEmpty((String)paramsMap.get("isSettled"))? Arrays.asList(Constant.USER_ACTIVITY_TRADE_STATUS_05,Constant.USER_ACTIVITY_TRADE_STATUS_06) : Arrays.asList((String)paramsMap.get("isSettled")),  new PageRequest((int)paramsMap.get("pageNum"), (int)paramsMap.get("pageSize"))))
				:
//				PageFuns.pageVoToMap(commissionInfoRepository.getResListMonthPage(cust.getId(),(String)paramsMap.get("productType"), DateFormatUtils.format(StringUtils.isEmpty((String)paramsMap.get("startDate")) ? DateUtils.addYears(new Date(), -100) : DateTime.parse((String)paramsMap.get("startDate")).toDate(), DEFAUTL_FORMATER), DateFormatUtils.format(StringUtils.isEmpty((String)paramsMap.get("endDate")) ? DateUtils.addYears(new Date(), 100) : DateTime.parse((String)paramsMap.get("endDate")).toDate(), DEFAUTL_FORMATER), new PageRequest((int)paramsMap.get("pageNum"), (int)paramsMap.get("pageSize")))));
				commissionInfoRepositoryCustom.findCommissInfoPage((String)paramsMap.get("custId"), DateTime.now().toDate(), Constant.PRODUCT_TYPE_04, (String)paramsMap.get("startDate"), (String)paramsMap.get("endDate"), (int)paramsMap.get("pageNum"), (int)paramsMap.get("pageSize")));
		return new ResultVo(true,"统计信息、推广奖励信息查询成功",data);
	}

	/**
	 * 金牌推荐人当天或当月在投详情
	 */
	@Override
	public ResultVo getInvestListDetail(Map<String, Object> paramsMap)throws SLException {
		ResultVo result = new ResultVo(false);
		if(!Arrays.asList(Constant.PRODUCT_TYPE_01,Constant.PRODUCT_TYPE_04).contains((String)paramsMap.get("productType")))
			throw new SLException("产品类型报错");
		CustInfoEntity cust = custInfoRepository.findOne((String)paramsMap.get("custId"));
		if(null == cust)
			throw new SLException("用户不存在");
		
		switch ((String)paramsMap.get("productType")) {
		case Constant.PRODUCT_TYPE_01:
			result = new ResultVo(true,"活期宝详情查询成功",PageFuns.pageVoToMap(commissionInfoRepository.getResListDetailPage(cust.getId(), (String)paramsMap.get("productType"),(String)paramsMap.get("id"),new PageRequest((int)paramsMap.get("pageNum"), (int)paramsMap.get("pageSize")))));
			break;
		default:
//			result = new ResultVo(true,"定期宝详情查询成功",PageFuns.pageVoToMap(commissionInfoRepository.getResListMonthDetailPage(cust.getId(),(String)paramsMap.get("productType"),(String)paramsMap.get("id"),new PageRequest((int)paramsMap.get("pageNum"), (int)paramsMap.get("pageSize")))));
			PageFuns.numToIndex(paramsMap);
			result = new ResultVo(true,"定期宝详情查询成功",commissionInfoRepositoryCustom.findCommissDetailPage((String)paramsMap.get("id"), cust.getId(), DateTime.now().toDate(), Constant.PRODUCT_TYPE_04, (int)paramsMap.get("pageNum"), (int)paramsMap.get("pageSize")));
			break;
		}
		return result;
	}

}
