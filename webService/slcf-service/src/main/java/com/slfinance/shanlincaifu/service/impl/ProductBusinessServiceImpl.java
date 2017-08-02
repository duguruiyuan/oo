/** 
 * @(#)ProductBusinessServiceImpl.java 1.0.0 2015年4月23日 下午2:55:28  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.ProductInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductRateInfoEntity;
import com.slfinance.shanlincaifu.repository.ProductDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductRateInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.ProductBusinessRepositoryCustom;
import com.slfinance.shanlincaifu.service.InvestService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.ProductBusinessService;
import com.slfinance.shanlincaifu.service.ProjectService;
import com.slfinance.shanlincaifu.service.WealthInfoService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.shanlincaifu.validate.RuleUtils;
import com.slfinance.vo.ResultVo;

/**   
 * 产品业务服务
 *  
 * @author  caoyi
 * @version $Revision:1.0.0, $Date: 2015年4月23日 下午2:55:28 $ 
 */
@Slf4j
@Service("productBusinessService")
public class ProductBusinessServiceImpl implements ProductBusinessService{

	@Autowired
	private ProductBusinessRepositoryCustom productBusinessRepositoryCustom;
	
	@Autowired
	private ProductRateInfoRepository productRateInfoRepository;
	
	@Autowired
	private InvestService investService;
	
	@Autowired
	private ProductInfoRepository productInfoRepository;
	
	@Autowired
	private ProductDetailInfoRepository productDetailInfoRepository;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private WealthInfoService wealthInfoService;
	
	/**
	 * 获取活期宝今日明细
	 * 
	 * @author caoyi
	 * @date 2015年04月23日 下午13:56:25
	 * @return ResultVo
	 */
	@Override
	public Map<String, Object> findBaoCurrentDetailInfo() throws SLException {
		Map<String, Object> map= new HashMap<String, Object>();
		map.put("productType", Constant.PRODUCT_TYPE_01);
		map.put("eranAccount", Constant.SUB_ACCOUNT_NO_ERAN);
		map.put("centerAccount", Constant.SUB_ACCOUNT_NO_CENTER);
		map.put("dateFormat", DateUtils.getCurrentDate("yyyyMMdd"));
		Map<String, Object>  result=productBusinessRepositoryCustom.findBaoCurrentDetailInfo(map);
		log.info("===========查询活期宝今日明细==============");
		return result;
	}

	/**
	 * 获取活期宝当前价值分配
	 * 
	 * @author caoyi
	 * @date 2015年04月28日 下午16:08:25
	 * @return ResultVo
	 */
	@Override
	public Map<String, Object> findBaoCurrentVauleSum() throws SLException {
		Map<String, Object>  result=productBusinessRepositoryCustom.findBaoCurrentVauleSum();
		log.info("===========查询活期宝当前价值分配==============");
		return result;
	}

	/**
	 * 获取活期宝分配规则
	 * 
	 * @author caoyi
	 * @date 2015年04月28日 下午16:49:25
	 * @return ResultVo
	 */
	@Override
	public Map<String, Object> findBaoCurrentVauleSet() throws SLException {
		Map<String, Object>  result=productBusinessRepositoryCustom.findBaoCurrentVauleSet();
		log.info("===========查询活期宝分配规则==============");
		return result;
	}

	/**
	 * 重定义活期宝分配规则
	 * 
	 * @author caoyi
	 * @date 2015年04月28日 下午18:23:24
	 * @return ResultVo
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo updateBaoSetVaule(Map<String, Object> param) throws SLException {
		BigDecimal allotScale = new BigDecimal((String)param.get("allotScale")) ;
		if(allotScale.compareTo(new BigDecimal("1"))==1){
			throw new SLException(" 活期宝分配比例不能超过100%!");
		}
		param.put("lastUpdateDate", new Date());
		param.put("productType", Constant.PRODUCT_TYPE_01);
		int row=productBusinessRepositoryCustom.updateBaoPreVaule(param);
		if (row < 1) {
			throw new SLException(" 更新活期宝预留价值失败!");
		}
		int row2=productBusinessRepositoryCustom.updateBaoOpenScale(param);
		if (row2 < 1) {
			throw new SLException(" 更新活期宝开放比例失败!");
		}
		log.info("===========重定义活期宝分配规则==============");
		ResultVo resultVo = new ResultVo(true);
		return resultVo;
	}

	/**
	 * 手工修改活期宝当前价值分配
	 * 
	 * @author caoyi
	 * @date 2015年04月29日 下午16:22:43
	 * @return ResultVo
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo updateBaoCurrentVaule(Map<String, Object> param) throws SLException {
		BigDecimal canOpenValue = param.containsKey("canOpenValue") ? new BigDecimal((String)param.get("canOpenValue")) : new BigDecimal("0");
		BigDecimal openValue = param.containsKey("openValue") ? new BigDecimal((String)param.get("openValue")) : new BigDecimal("0");
		BigDecimal alreadyPreValue = param.containsKey("alreadyPreValue") ? new BigDecimal((String)param.get("alreadyPreValue")) : new BigDecimal("0");
		BigDecimal unopenValue = param.containsKey("unopenValue") ? new BigDecimal((String)param.get("unopenValue")) : new BigDecimal("0");
		Boolean systemFlag = param.containsKey("systemFlag") ? (Boolean)param.get("systemFlag") : false ;
		Map<String, Object>  map=this.findBaoCurrentVauleSum();
		BigDecimal canOpenValueReal=(BigDecimal)map.get("canOpenValue");
		//判断是系统调整当前可开放价值，还是人工调整可开放价值
		if(!systemFlag){
			if(canOpenValue.compareTo(canOpenValueReal)!=0){
				throw new SLException(" 手工修改活期宝当前价值分配失败!");
			}
			if(canOpenValue.compareTo(openValue.add(alreadyPreValue).add(unopenValue))!=0){
				throw new SLException(" 手工修改活期宝当前价值分配失败!");
			}
		}	
		openValue=canOpenValue.subtract(alreadyPreValue).subtract(unopenValue);
		param.put("openValue", openValue);
		param.put("lastUpdateDate", new Date());
		param.put("productType", Constant.PRODUCT_TYPE_01);
		int row=productBusinessRepositoryCustom.updateBaoOpenValue(param);
		if (row < 1) {
			throw new SLException(" 更新活期宝预计开放金额失败!");
		}
		int row2=productBusinessRepositoryCustom.updateBaoAlreadyPreValue(param);
		if (row2 < 1) {
			throw new SLException(" 更新活期宝实际预留价值失败!");
		}
		int row3=productBusinessRepositoryCustom.updateBaoUnOpenVaule(param);
		if (row3 < 1) {
			throw new SLException(" 更新活期宝未开放价值失败!");
		}		
		log.info("===========手工修改活期宝当前价值分配==============");
		ResultVo resultVo = new ResultVo(true);
		return resultVo;
	}

	/**
	 * 获取债权还款预算
	 * 
	 * @author caoyi
	 * @date 2015年04月29日 下午18:06:33
	 * @return ResultVo
	 */
	@Override
	public Map<String, Object> loanRepaymentForecast(Map<String, Object> param) throws SLException {
		//参数验证
		String startDate = param.containsKey("startDate") ? String.valueOf(param.get("startDate")) : "";
		String endDate = param.containsKey("endDate") ? String.valueOf(param.get("endDate")) : "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
		try {
			Date startDateFormat=format.parse(startDate);
			startDate=format2.format(startDateFormat);
			Date endDateFormat=format.parse(endDate);
			endDate=format2.format(endDateFormat);
		} catch (ParseException e) {
			throw new SLException(" 日期格式不对!");
		}
		param.put("startDate", startDate);
		param.put("endDate", endDate);
		Map<String, Object>  result=productBusinessRepositoryCustom.loanRepaymentForecast(param);
		log.info("===========查询债权还款预算==============");
		return result;
	}

	/**
	 * 查询相关产品预计还款明细
	 * 
	 * @author caoyi
	 * @date 2015年04月29日 下午20:34:33
	 * @return ResultVo
	 */
	@Override
	public Map<String, Object> findLoanRepaymentList(Map<String, Object> param) throws SLException {
		//参数验证
		String startDate = param.containsKey("startDate") ? String.valueOf(param.get("startDate")) : "";
		String endDate = param.containsKey("endDate") ? String.valueOf(param.get("endDate")) : "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
		try {
			Date startDateFormat=format.parse(startDate);
			startDate=format2.format(startDateFormat);
			Date endDateFormat=format.parse(endDate);
			endDate=format2.format(endDateFormat);
			param.put("startDate", startDate);
			param.put("endDate", endDate);
		} catch (ParseException e) {
			throw new SLException(" 日期格式不对!");
		}
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page =productBusinessRepositoryCustom.findLoanRepaymentList(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		log.info("===========查询债权还款预算==============");
		return result;
	}

	/**
	 * 获取债权价值预算
	 * 
	 * @author caoyi
	 * @date 2015年04月30日 上午10:52:46
	 * @return ResultVo
	 */
	@Override
	public Map<String, Object> loanValueForecast(Map<String, Object> param) throws SLException {
		//参数验证
		String queryDate = param.containsKey("queryDate") ? String.valueOf(param.get("queryDate")) : "";
		String productType = param.containsKey("productType") ? String.valueOf(param.get("productType")) : "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
		try {
			Date dateFormat=format.parse(queryDate);
			queryDate=format2.format(dateFormat);			
		} catch (ParseException e) {
			throw new SLException(" 日期格式不对!");
		}
		param.put("queryDate", queryDate);
		//活期宝
		if(productType.equals(Constant.PRODUCT_TYPE_01)){
			param.put("eranAccount", Constant.SUB_ACCOUNT_NO_ERAN);
			param.put("centerAccount", Constant.SUB_ACCOUNT_NO_CENTER);
		}
		//定期宝
		if(productType.equals(Constant.PRODUCT_TYPE_04)){
			param.put("eranAccount", Constant.SUB_ACCOUNT_NO_ERAN_12);
			param.put("centerAccount", Constant.SUB_ACCOUNT_NO_CENTER_11);
		}
		param.put("dateFormat", DateUtils.getCurrentDate("yyyyMMdd"));
		param.put("date", DateUtils.truncateDate(new Date()));
		Map<String, Object>  result=productBusinessRepositoryCustom.loanValueForecast(param);
		log.info("===========获取债权价值预算==============");
		return result;
	}

	/**
	 * 发标(手动或定时) 
	 * 现在只有活期宝
	 * 
	 * @author caoyi
	 * @date 2015年05月01日下午13:44:36
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public void releaseBid() throws SLException {
		//活期宝发标
		//获取活期宝当前价值分配，活期宝预计开放价值
		Map<String, Object>  result=productBusinessRepositoryCustom.findBaoCurrentVauleSum();
		BigDecimal openValue=(BigDecimal)result.get("openValue");
		if(openValue.compareTo(new BigDecimal("0"))==1){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("productStatus", Constant.PRODUCT_STATUS_BID_ING);
			map.put("lastUpdateDate", new Date());
			map.put("productType", Constant.PRODUCT_TYPE_01);
			int row=productBusinessRepositoryCustom.updateProductStatus(map);
			if (row < 1) {
				throw new SLException(" 发标失败,更新产品状态失败!");
			}
			map.put("tradeType", SubjectConstant.TRADE_FLOW_TYPE_01);
			int row2=productBusinessRepositoryCustom.updateProductDetail(map);
			if (row2 < 1) {
				throw new SLException(" 发标失败,更新产品详情失败!");
			}
			map.put("openValue", 0);
			int row3=productBusinessRepositoryCustom.updateBaoOpenValue(map);
			if (row3 < 1) {
				throw new SLException(" 发标失败,更新预计开放金额失败!");
			}
		}
		
		//定期宝发标
		//获取定期宝当前价值分配，定期宝预计开放价值
		Map<String, Object>  result2=productBusinessRepositoryCustom.findTermCurrentVauleSum2();
		BigDecimal termOpenValue=(BigDecimal)result2.get("openValue");
		if(termOpenValue.compareTo(new BigDecimal("0"))==1){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("productStatus", Constant.PRODUCT_STATUS_BID_ING);
			map.put("lastUpdateDate", new Date());
			map.put("productType", Constant.PRODUCT_TYPE_04);
			int row=productBusinessRepositoryCustom.updateProductStatus(map);
			if (row < 1) {
				throw new SLException(" 发标失败,更新产品状态失败!");
			}
			map.put("tradeType", SubjectConstant.TRADE_FLOW_TYPE_04);
			int row2=productBusinessRepositoryCustom.updateProductDetail(map);
			if (row2 < 1) {
				throw new SLException(" 发标失败,更新产品详情失败!");
			}
			map.put("openValue", 0);
			int row3=productBusinessRepositoryCustom.updateBaoOpenValue(map);
			if (row3 < 1) {
				throw new SLException(" 发标失败,更新预计开放金额失败!");
			}
		}
		log.info("===========发标==============");		
	}

	
	/**
	 * 可开放价值计算(手动或定时)
	 * 
	 * 
	 * @author caoyi
	 * @date 2015年05月01日下午15:45:18
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public void computeOpenValue() throws SLException {
		computeBaoOpenValue();
		computeTermOpenValue();
		
		log.info("===========可开放价值计算JOB==============");	
	}

	/**
	 * 可开放价值计算(手动或定时)
	 * 活期宝
	 * 
	 * @author caoyi
	 * @date 2015年08月20日上午11:25:28
	 */
	@Override
	public void computeBaoOpenValue() throws SLException {
		//活期宝可开放价值计算
		//获取活期宝今日明细
		Map<String, Object> result1 =this.findBaoCurrentDetailInfo();
//		活期宝分配未处理债权价值
//		BigDecimal untreatedAllotAmount=(BigDecimal)result1.get("untreatedAllotAmount");
		//活期宝分配可使用债权价值
		BigDecimal canUseAllotAmount=(BigDecimal)result1.get("canUseAllotAmount");
		//活期宝可开放价值
		BigDecimal canOpenAmount=(BigDecimal)result1.get("canRealOpenAmount");
		canOpenAmount=canOpenAmount.add(canUseAllotAmount);
		if(canOpenAmount.compareTo(BigDecimal.ZERO) < 0) {
			canOpenAmount = BigDecimal.ZERO;
		}
		//获取分配规则  
		Map<String, Object> result2=productBusinessRepositoryCustom.findBaoCurrentVauleSet();
		//活期宝比例
		BigDecimal allotScale=(BigDecimal)result2.get("allotScale");
		//预留价值
		BigDecimal expectPreValue=(BigDecimal)result2.get("expectPreValue");
		
		//已经预留价值
		BigDecimal alreadyPreValue=new BigDecimal("0");
		//未开放价值
		BigDecimal unopenValue=new BigDecimal("0");
		//预计开放价值
		BigDecimal openValue=new BigDecimal("0");
		if(canOpenAmount.compareTo(expectPreValue)!=1){
			alreadyPreValue=canOpenAmount;
		}		
		else{
			alreadyPreValue=expectPreValue;
			openValue=(canOpenAmount.subtract(expectPreValue)).multiply(allotScale);
			unopenValue=canOpenAmount.subtract(expectPreValue).subtract(openValue);
		}
		
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("canOpenValue", canOpenAmount.toString());
		map.put("openValue", openValue.toString());
		map.put("alreadyPreValue", alreadyPreValue.toString());
		map.put("unopenValue", unopenValue.toString());
		map.put("systemFlag", true);
		this.updateBaoCurrentVaule(map);
		
		map.put("allotStatus", Constant.ALLOT_STATUS_02);
		map.put("oldAllotStatus", Constant.ALLOT_STATUS_01);
		map.put("lastUpdateDate", new Date());
		map.put("productType", Constant.PRODUCT_TYPE_01);
		map.put("dateFormat", DateUtils.getCurrentDate("yyyyMMdd"));
		productBusinessRepositoryCustom.updateBaoAllotStatus(map);
		
		log.info("===========活期宝可开放价值计算==============");	
	}
	
	/**
	 * 可开放价值计算(手动或定时)
	 * 定期宝
	 * 
	 * @author caoyi
	 * @date 2015年08月20日上午11:26:12
	 */
	@Override
	public void computeTermOpenValue() throws SLException {
		//定期宝可开放价值计算
		//获取定期宝今日明细
		Map<String, Object> result1 =this.findTermCurrentDetailInfo();
		//定期宝分配可使用债权价值
		BigDecimal canUseAllotAmount=(BigDecimal)result1.get("canUseAllotAmount");
		//定期宝宝可开放价值
		BigDecimal canOpenAmount=(BigDecimal)result1.get("canRealOpenAmount");
		canOpenAmount=canOpenAmount.add(canUseAllotAmount);
		if(canOpenAmount.compareTo(BigDecimal.ZERO) < 0) {
			canOpenAmount = BigDecimal.ZERO;
		}
		//获取分配规则  
		List<Map<String, Object>> result2=productBusinessRepositoryCustom.findTermCurrentVauleSet();	
		BigDecimal allotScalesSum = new BigDecimal("0") ;
	    BigDecimal expectPreValue = new BigDecimal("0") ;		
		for(int i = 0;i < result2.size(); i ++){
			Map<String, Object> map= result2.get(i);
			if(map.get("type").equals("product")){
	            	allotScalesSum = allotScalesSum.add((BigDecimal) map.get("value"));
	        }
            if(map.get("type").equals("expectPreValue")){
            	expectPreValue = expectPreValue.add((BigDecimal) map.get("value"));
            }
        }	
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
		if(canOpenAmount.compareTo(expectPreValue)!=1){
			for(int i = 0;i < result2.size(); i ++){
				Map<String, Object> map= result2.get(i);
	            if(map.get("type").equals("product")){
	    			map.put("value", new BigDecimal("0"));	    			
	    			list.add(map);
	            }
	        }
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("name", "实际预留价值");
			map1.put("type", "alreadyPreValue");
			map1.put("value", canOpenAmount);
			map1.put("code", "alreadyPreValue");
			list.add(map1);
			
			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("name", "未开放价值");
			map2.put("type", "unopenValue");
			map2.put("value", new BigDecimal("0"));
			map2.put("code", "unopenValue");
			list.add(map2);
		}		
		else{
			BigDecimal openValueSum=new BigDecimal("0");
			for(int i = 0;i < result2.size(); i ++){
				Map<String, Object> map= result2.get(i);
	            if(map.get("type").equals("product")){
	    			map.put("value", ArithUtil.formatScale((canOpenAmount.subtract(expectPreValue)).multiply((BigDecimal)map.get("value")),8));	
	    			if(i==(result2.size()-2) && allotScalesSum.compareTo(new BigDecimal("1"))==0){
	    				map.put("value", canOpenAmount.subtract(expectPreValue).subtract(openValueSum));	
	    			}
	    			list.add(map);
	    			openValueSum=openValueSum.add((BigDecimal)map.get("value"));
	    			
	            }
	        }	
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("name", "实际预留价值");
			map1.put("type", "alreadyPreValue");
			map1.put("value", expectPreValue);
			map1.put("code", "alreadyPreValue");
			list.add(map1);
			
			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("name", "未开放价值");
			map2.put("type", "unopenValue");
			map2.put("value", canOpenAmount.subtract(expectPreValue).subtract(openValueSum));
			map2.put("code", "unopenValue");
			list.add(map2);
		}
		
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("data", list);
		map.put("systemFlag", true);
		this.updateTermCurrentVaule(map);
		
		map.put("allotStatus", Constant.ALLOT_STATUS_02);
		map.put("oldAllotStatus", Constant.ALLOT_STATUS_01);
		map.put("lastUpdateDate", new Date());
		map.put("productType", Constant.PRODUCT_TYPE_04);
		map.put("dateFormat", DateUtils.getCurrentDate("yyyyMMdd"));
		productBusinessRepositoryCustom.updateBaoAllotStatus(map);		
		
		log.info("===========定期宝可开放价值计算==============");
	}
	
	/**
	 * 获取累计成交统计
	 * 
	 * @author caoyi
	 * @date 2015年05月05日下午16:38:18
	 */
	@Override
	//@Cacheable(value="cache1")
	public Map<String, Object> findTotalTradetInfo() throws SLException {
		Map<String, Object> map= new HashMap<String, Object>();
		map.put("baoTradeType01", SubjectConstant.TRADE_FLOW_TYPE_01);
		map.put("baoTradeType03", SubjectConstant.TRADE_FLOW_TYPE_03);
		map.put("baoTradeType04", SubjectConstant.TRADE_FLOW_TYPE_04);
		Map<String, Object>  result=productBusinessRepositoryCustom.findTotalTradetInfo(map);
		log.info("===========查询累计成交统计==============");
		return result;
	}

	/**
	 * 收益计算器
	 * 
	 * @author caoyi
	 * @date 2015年07月14日 下午17:44:25 
	 * @param param
	 * 		 <tt>1)活期宝</tt><br>
	 *       <tt>investMethod ：String:投资方式</tt><br>
	 *       <tt>investAmount ：String:投资金额</tt><br>
	 *       <tt>days ：String:投资天数</tt><br>
	 *       <tt>2)定期宝</tt><br>
	 *       <tt>investMethod ：String:投资方式</tt><br>
	 *       <tt>investAmount ：String:投资金额</tt><br>
	 *       <tt>productId ：String:产品ID</tt><br>
	 *       <tt>3)体验宝</tt><br>
	 *       <tt>investMethod ：String:投资方式</tt><br>
	 *       <tt>investAmount ：String:投资金额</tt><br>  
	 *       <tt>4)企业借款</tt><br>
	 *       <tt>investMethod ：String:投资方式</tt><br>
     *       <tt>repaymentMethod:String:还款方式</tt><br>
     *       <tt>typeTerm:String:投资期限</tt><br>
     *       <tt>tradeAmount:String:投资金额</tt><br>
     *       <tt>yearRate:String:年化收益</tt><br> 
     *       <tt>5)推广佣金收益</tt><br>
     *       <tt>investMethod ：String:投资方式</tt><br>
	 *       <tt>investAmount ：String:投资金额</tt><br>
	 *       <tt>days ：String:投资天数</tt><br>
	 * <br>
	 * @return Map<String, object>: 
	 * 		  <tt>1)活期宝</tt><br>
	 * 		  <tt>incomeAmount ： String:累计收益</tt><br>
	 *        <tt>totalAmount： String:持有份额</tt><br>
	 *        <tt>2)定期宝</tt><br>
	 *        <tt>incomeAmount ： String:累计收益</tt><br>
	 *        <tt>awardAmount： String:奖励收益</tt><br>
	 *        <tt>totalAmount： String:持有份额</tt><br>
	 *        <tt>3)体验宝</tt><br>
	 *        <tt>incomeAmount ： String:累计收益</tt><br>
	 *        <tt>totalAmount： String:持有份额</tt><br>
	 *        <tt>4)企业借款</tt><br>
     *        <tt>planList     :String:还款计划列表:List<Map<String, Object>></tt><br>
			           <tt>当前期数         :String:        currentTerm</tt><br>
			           <tt>本息           :String:        principalInterest</tt><br>
			           <tt>本金           :String:        principal</tt><br>
			           <tt>利息           :String:        interest</tt><br>
			           <tt>剩余本金         :String:        remainderPrincipal</tt><br>
     *        <tt>totalAmount  :String:到期本息</tt><br>
     *        <tt>termInterest :String:每期利息</tt><br>
     *        <tt>totalInterest:String:到期利息</tt><br>
     *        <tt>5)推广佣金收益</tt><br>
     *        <tt>incomeAmount ： String:累计收益</tt><br>
	 *        <tt>totalAmount： String:持有份额</tt><br>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> incomeCalculator(Map<String, Object> param) throws SLException {

		String investMethod = param.containsKey("investMethod") ? String.valueOf(param.get("investMethod")) : "";

		if(Constant.PRODUCT_TYPE_01.equals(investMethod)) {
			return incomeProduct1(param);
		} else if(Constant.PRODUCT_TYPE_04.equals(investMethod)) {
			return incomeProduct2(param);
		}else if("推广佣金".equals(investMethod)) {
			return incomeProduct3(param);
		}else if (Constant.PRODUCT_TYPE_03.equals(investMethod)) {
			return incomeProduct4(param);
		}else if (Constant.PRODUCT_TYPE_05.equals(investMethod)) {
			ResultVo result = projectService.caclProject(param);
			return (Map<String, Object>)result.getValue("data");
		}else if (Constant.PRODUCT_TYPE_06.equals(investMethod)){
			ResultVo result = wealthInfoService.caclWealth2(param);
			return (Map<String, Object>)result.getValue("data");
		}
		else {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("incomeAmount", 0);
			result.put("totalAmount", 0);
			return result;
		}
	}
	
	/**
	 * 活期宝收益计算
	 *
	 * @author  wangjf
	 * @date    2015年8月19日 上午11:56:09
	 * @param param
	 * @return
	 */
	private Map<String, Object> incomeProduct1(Map<String, Object> param) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		BigDecimal incomeAmount = new BigDecimal("0"); // 累积收益
		BigDecimal totalAmount = new BigDecimal("0"); // 持有份额
		
		if(!RuleUtils.required((String)param.get("investAmount"))
				|| !RuleUtils.required((String)param.get("days"))
				|| !RuleUtils.isArithmeticNumber((String)param.get("investAmount"))
				|| !RuleUtils.isPositiveNumber((String)param.get("days"))) {
			result.put("incomeAmount", incomeAmount);
			result.put("totalAmount", totalAmount);
			return result;
		}
		
		BigDecimal investAmount = param.containsKey("investAmount") ? new BigDecimal((String)param.get("investAmount")) : new BigDecimal("0");		
		BigDecimal days = param.containsKey("days") ? new BigDecimal((String)param.get("days")) : new BigDecimal("0");
		Date expireDate = DateUtils.parseDate("2015-12-31", "yyyy-MM-dd");// 奖励失效日期
		Date now = new Date();
		
		if(days.compareTo(new BigDecimal("0")) <= 0) {
			result.put("incomeAmount", incomeAmount);
			result.put("totalAmount", totalAmount);
			return result;
		}
		
		// 取利率
		int reminderDays = days.intValue();
		List<ProductRateInfoEntity> rateList = productRateInfoRepository.findProductRateInfoByTypeName(Constant.PRODUCT_TYPE_01);
		for(ProductRateInfoEntity p : rateList) {
			if(reminderDays == 0)break;
			
			if(days.intValue() < p.getLowerLimitDay()) { // 小于最低天数则不在利率范围内
				continue;
			}
			else if(days.intValue() <= p.getUpperLimitDay()) { // 小于最高天数则在利率范围内
				
				int transferDay = reminderDays;
				incomeAmount = calculateIncome(incomeAmount, ArithUtil.add(investAmount, incomeAmount), transferDay, expireDate, p.getYearRate(), p.getAwardRate(), now);
				now = DateUtils.getAfterDay(now, transferDay);
				reminderDays -= transferDay;
				break;
			}
			else { // 大于最高天数计算本利率范围
				
				// 判断当前时间加上投资天数是否大于奖励失效日期，
				// 若大于则分两部分计算，一部分为失效之前奖励部分，一部分为没有奖励
				int transferDay = p.getUpperLimitDay() - (days.intValue() - reminderDays);
				incomeAmount = calculateIncome(incomeAmount, ArithUtil.add(investAmount, incomeAmount), transferDay, expireDate, p.getYearRate(), p.getAwardRate(), now);
				now = DateUtils.getAfterDay(now, transferDay);
				reminderDays -= transferDay;
			}
		}
		
		totalAmount = ArithUtil.add(investAmount, incomeAmount);
        
		result.put("incomeAmount", ArithUtil.formatScale2(incomeAmount));
		result.put("totalAmount", ArithUtil.formatScale2(totalAmount));
		return result;		
	}
	
	/**
	 * 定期宝收益计算
	 *
	 * @author  wangjf
	 * @date    2015年8月19日 上午11:56:25
	 * @param param
	 * @return
	 */
	private Map<String, Object> incomeProduct2(Map<String, Object> param) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		BigDecimal incomeAmount = new BigDecimal("0"); // 累积收益
		BigDecimal totalAmount = new BigDecimal("0"); // 持有份额
		BigDecimal awardAmount = new BigDecimal("0"); // 奖励收益
		
		if(!RuleUtils.required((String)param.get("productId"))
				|| !RuleUtils.required((String)param.get("investAmount"))
				|| !RuleUtils.isArithmeticNumber((String)param.get("investAmount"))) {
			result.put("incomeAmount", incomeAmount);
			result.put("totalAmount", totalAmount);
			return result;
		}
		
		BigDecimal investAmount = param.containsKey("investAmount") ? new BigDecimal((String)param.get("investAmount")) : new BigDecimal("0");
		String productId = (String)param.get("productId");		
		ProductInfoEntity productInfoEntity = productInfoRepository.findOne(productId);
		if(productInfoEntity == null) {
			log.error("投资{}未找到产品信息", productId);
			result.put("awardAmount", awardAmount);
			result.put("incomeAmount", incomeAmount);
			result.put("totalAmount", totalAmount);
			return result;
		}
		
		List<ProductRateInfoEntity> rateList = productRateInfoRepository.findProductRateInfoByProductId(productId);
		ProductRateInfoEntity productRateInfoEntity = null;
		for(ProductRateInfoEntity rate : rateList){
			if(rate.getProductId().equals(productId)) {
				productRateInfoEntity = rate;
			}
		}
		if(productRateInfoEntity == null) {
			log.error("投资{}未找到产品利率", productId);
			result.put("awardAmount", awardAmount);
			result.put("incomeAmount", incomeAmount);
			result.put("totalAmount", totalAmount);
			return result;
		}
		
		// 预期收益：投资金额*年利率/12*月数
		// 奖励收益：投资金额*奖励利率/12*月数
		BigDecimal exceptInterest = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(investAmount, productRateInfoEntity.getYearRate()), productInfoEntity.getTypeTerm()), new BigDecimal("12"));
		BigDecimal awardInterest = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(investAmount, productRateInfoEntity.getAwardRate()), productInfoEntity.getTypeTerm()), new BigDecimal("12"));
	
		incomeAmount = exceptInterest;
		awardAmount = awardInterest;
		totalAmount = ArithUtil.add(ArithUtil.add(investAmount, incomeAmount), awardAmount);
		
		result.put("awardAmount", ArithUtil.formatScale2(awardAmount));
		result.put("incomeAmount", ArithUtil.formatScale2(incomeAmount));
		result.put("totalAmount", ArithUtil.formatScale2(totalAmount));
		return result;	
	}
	
	/**
	 * 推广佣金收益计算
	 *
	 * @author  wangjf
	 * @date    2015年8月25日 下午6:35:24
	 * @param param
	 * @return
	 */
	private Map<String, Object> incomeProduct3(Map<String, Object> param) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		BigDecimal incomeAmount = new BigDecimal("0"); // 累积收益
		BigDecimal totalAmount = new BigDecimal("0"); // 持有份额
		
		if(!RuleUtils.required((String)param.get("investAmount"))
				|| !RuleUtils.required((String)param.get("days"))
				|| !RuleUtils.isArithmeticNumber((String)param.get("investAmount"))
				|| !RuleUtils.isPositiveNumber((String)param.get("days"))) {
			result.put("incomeAmount", incomeAmount);
			result.put("totalAmount", totalAmount);
			return result;
		}
	
		BigDecimal investAmount = param.containsKey("investAmount") ? new BigDecimal((String)param.get("investAmount")) : new BigDecimal("0");
		BigDecimal days = param.containsKey("days") ? new BigDecimal((String)param.get("days")) : new BigDecimal("0");
			
		int iInvestAmount = 0;
		if(investAmount.doubleValue() > 8000000) { // 超过800万取800万
			iInvestAmount = 8000000;
		}
		else {
			iInvestAmount = investAmount.intValue();
		}
		
		ProductRateInfoEntity productRateInfoEntity = productRateInfoRepository.findProductRateInfoByProductIdAndInvestAmount(Constant.PRODUCT_ID_GOLD, iInvestAmount);
		if(productRateInfoEntity == null) {
			log.error("未找到产品利率");
			result.put("incomeAmount", incomeAmount);
			result.put("totalAmount", totalAmount);
			return result;
		}
		
		BigDecimal yearRate = productRateInfoEntity.getYearRate();
		BigDecimal awardRate  = productRateInfoEntity.getAwardRate();
		yearRate = (yearRate == null ? BigDecimal.ZERO : yearRate);
		awardRate = (awardRate == null ? BigDecimal.ZERO : awardRate);
		yearRate = ArithUtil.add(yearRate, awardRate);
		
		// 预期收入：再投金额*(年利率+奖励利率)/365*天数
		incomeAmount = ArithUtil.div(ArithUtil.mul(investAmount, yearRate), new BigDecimal("365"));
		totalAmount = ArithUtil.mul(incomeAmount, days);
		
		result.put("incomeAmount", ArithUtil.formatScale2(incomeAmount));
		result.put("totalAmount", ArithUtil.formatScale2(totalAmount));
		return result;	
	}
	
	
	/**
	 * 体验宝计算 
	 *
	 * @author  wangjf
	 * @date    2016年1月15日 上午9:48:05
	 * @param param
	 * @return
	 */
	private Map<String, Object> incomeProduct4(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<String, Object>();
		BigDecimal incomeAmount = new BigDecimal("0"); // 累积收益
		BigDecimal totalAmount = new BigDecimal("0"); // 持有份额
		
		if(!RuleUtils.required((String)param.get("investAmount"))
				|| !RuleUtils.isArithmeticNumber((String)param.get("investAmount"))) {
			result.put("incomeAmount", incomeAmount);
			result.put("totalAmount", totalAmount);
			return result;
		}
		
		BigDecimal investAmount = param.containsKey("investAmount") ? new BigDecimal((String)param.get("investAmount")) : new BigDecimal("0");		
		int days = paramService.findExpireDays();
		
		
		// 取利率
		List<ProductRateInfoEntity> rateList = productRateInfoRepository.findProductRateInfoByTypeName(Constant.PRODUCT_TYPE_03);
		if(rateList == null || rateList.size() == 0) {
			result.put("incomeAmount", incomeAmount);
			result.put("totalAmount", totalAmount);
			return result;
		}
		
		// 收入
		incomeAmount = incomeMath(investAmount, rateList.get(0).getYearRate(), days);
		totalAmount = ArithUtil.add(investAmount, incomeAmount);
        
		result.put("incomeAmount", ArithUtil.formatScale2(incomeAmount));
		result.put("totalAmount", ArithUtil.formatScale2(totalAmount));
		return result;		
	}
	
	/**
	 * 计算收益
	 *
	 * @author  wangjf
	 * @date    2015年7月21日 下午1:02:46
	 * @param incomeAmount
	 * @param investAmount
	 * @param transferDay
	 * @param expireDate
	 * @param productYearRate
	 * @param productAwardRate
	 * @return
	 */
	private BigDecimal calculateIncome(BigDecimal incomeAmount, BigDecimal investAmount, int transferDay, 
			Date expireDate, BigDecimal productYearRate, BigDecimal productAwardRate, Date now) {
		BigDecimal yearRate = null;
		
		// 判断当前时间加上投资天数是否大于奖励失效日期，
		// 若大于则分两部分计算，一部分为失效之前奖励部分，一部分为没有奖励
		if(DateUtils.getAfterDay(now, transferDay).compareTo(expireDate) <= 0) {
			yearRate = ArithUtil.add(productYearRate, productAwardRate);
			incomeAmount = ArithUtil.add(incomeAmount, incomeMath(investAmount, yearRate, transferDay));
		}
		else {
			
			int validDays = now.compareTo(expireDate) >= 0 ? 0 : DateUtils.datePhaseDiffer(now, expireDate);// 奖励生效天数
			int otherDays = transferDay - validDays; // 剩余天数
			
			yearRate = ArithUtil.add(productYearRate, productAwardRate);
			BigDecimal validIncomeAmount = incomeMath(investAmount, yearRate, validDays);
			incomeAmount = ArithUtil.add(incomeAmount, validIncomeAmount);
			yearRate = productYearRate;
			BigDecimal otherIncomeAmount = incomeMath(ArithUtil.add(investAmount, validIncomeAmount), yearRate, otherDays);
			incomeAmount = ArithUtil.add(incomeAmount, otherIncomeAmount);					
		}
		
		return incomeAmount;
	}
	
	/**
	 * 理财计算公式((1+7%/365)^30-1)*10000
	 *
	 * @author  wangjf
	 * @date    2015年7月21日 下午1:29:26
	 * @param investAmount
	 * @param yearRate
	 * @param days
	 * @return
	 */
	private BigDecimal incomeMath(BigDecimal investAmount, BigDecimal yearRate, int days) {
		return ArithUtil.mul(new BigDecimal(String.valueOf(Math.pow(1+yearRate.doubleValue()/365, days)-1)), investAmount, 8);	
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo batchAuditWithdrawBaoNormal(Map<String, Object> params)
			throws SLException {
		
		List<String> auditList = (List<String>)params.get("auditList");
		String auditStatus = (String)params.get("auditStatus");
		String memo = (String)params.get("auditMemo");
		String custId = (String)params.get("auditCustId");
		String ipAddress = (String)params.get("ipAddress");
		
		if(auditList == null || auditList.size() == 0) {
			return new ResultVo(false, "审核ID不能为空");
		}
		
		int success = 0, failed = 0;
		for(String id : auditList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("atoneId", id);
			map.put("auditStatus", auditStatus);
			map.put("auditMemo", memo);
			map.put("auditCustId", custId);
			map.put("ipAddress", ipAddress);
			
			ResultVo resultVo = new ResultVo(false);
			try 
			{
				resultVo = investService.auditWithdrawBaoNormal(map);
			}
			catch(SLException ex) 
			{
				log.error(String.format("赎回申请审核失败！审核ID：%s，异常信息：%s", id, ex.getMessage()));
			}
			if(ResultVo.isSuccess(resultVo)) {
				success ++;
			}
			else {
				failed ++;
			}
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", success);
		result.put("failed", failed);
		
		return new ResultVo(true, "审核成功", result);
	}

	/**
	 * 获取定期宝今日明细
	 * 
	 * @author caoyi
	 * @date 2015年08月15日 下午15:00:15
	 * @return ResultVo
	 */
	@Override
	public Map<String, Object> findTermCurrentDetailInfo() throws SLException {
		Map<String, Object> map= new HashMap<String, Object>();
		map.put("productType", Constant.PRODUCT_TYPE_04);
		map.put("eranAccount", Constant.SUB_ACCOUNT_NO_ERAN_12);
		map.put("centerAccount", Constant.SUB_ACCOUNT_NO_CENTER_11);
		map.put("dateFormat", DateUtils.getCurrentDate("yyyyMMdd"));
		Map<String, Object>  result=productBusinessRepositoryCustom.findBaoCurrentDetailInfo(map);
		log.info("===========查询定期宝今日明细==============");
		return result;
	}
	
	
	/**
	 * 获取定期宝当前价值分配
	 * 
	 * @author caoyi
	 * @date 2015年08月17日 下午15:06:16
	 * @return List Map <br>
	 *         <tt>key: name, title:名称, type:{@link String} </tt><br>
	 *         <tt>key: type, title:类型, type:{@link String} </tt><br>
	 *         <tt>key: value, title:价值, type:{@link String} </tt><br>
	 *         <tt>key: code, title:编号, type:{@link String} </tt><br>
	 */
	@Override
	public List<Map<String, Object>> findTermCurrentVauleSum() throws SLException {
		List<Map<String, Object>>  result=productBusinessRepositoryCustom.findTermCurrentVauleSum();
		log.info("===========查询定期宝当前价值分配==============");
		return result;
	}

	
	/**
	 * 获取定期宝分配规则
	 * 
	 * @author caoyi
	 * @date 2015年08月18日 上午10:39:14
	 * @return List Map <br>
	 *         <tt>key: name, title:名称, type:{@link String} </tt><br>
	 *         <tt>key: type, title:类型, type:{@link String} </tt><br>
	 *         <tt>key: value, title:值, type:{@link String} </tt><br>
	 *         <tt>key: code, title:编号, type:{@link String} </tt><br>
	 */
	@Override
	public List<Map<String, Object>> findTermCurrentVauleSet() throws SLException {
		List<Map<String, Object>>   result=productBusinessRepositoryCustom.findTermCurrentVauleSet();
		log.info("===========查询定期宝分配规则==============");
		return result;
	}

	/**
	 * 重定义定期宝分配规则
	 * 
	 * @author caoyi
	 * @date 2015年08月18日 上午10:43:34
	 * @param List<Map> <br>
	 *         <tt>key: name, title:名称, type:{@link String} </tt><br>
	 *         <tt>key: type, title:类型, type:{@link String} </tt><br>
	 *         <tt>key: value, title:值, type:{@link String} </tt><br>
	 *         <tt>key: code, title:编号, type:{@link String} </tt><br>
	 * @return ResultVo
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo updateTermSetVaule(Map<String, Object> paramList) throws SLException {
		List<Map<String, Object>> list=(List<Map<String, Object>>) paramList.get("data");
		BigDecimal allotScalesSum = new BigDecimal("0") ;
		
		for(int i = 0;i < list.size(); i ++){
			Map<String, Object> map= list.get(i);
            if(map.get("type").equals("product")){
            	allotScalesSum = allotScalesSum.add(new BigDecimal((String)map.get("value")));
            }
        }	
		if(allotScalesSum.compareTo(new BigDecimal("1"))==1){
			throw new SLException(" 定期宝分配比例不能超过100%!");
		}
		
		Map<String, Object> param= new HashMap<String, Object>();
		param.put("lastUpdateDate", new Date());
		param.put("productType", Constant.PRODUCT_TYPE_04);
		for(int i = 0;i < list.size(); i ++){
			Map<String, Object> map= list.get(i);
            if(map.get("type").equals("product")){
            	param.put("productName", map.get("name"));
            	param.put("allotScale", map.get("value"));            	
            	int row=productBusinessRepositoryCustom.updateTermOpenScale(param);
        		if (row < 1) {
        			throw new SLException(" 更新定期宝开放比例失败!");
        		}
            }
            if(map.get("type").equals("expectPreValue")){
            	param.put("expectPreValue", map.get("value"));
            	int row=productBusinessRepositoryCustom.updateBaoPreVaule(param);
        		if (row < 1) {
        			throw new SLException(" 更新定期宝预留价值失败!");
        		}
            }
        }		
		log.info("===========重定义定期宝分配规则==============");
		ResultVo resultVo = new ResultVo(true);
		return resultVo;
	}

	
	/**
	 * 手工修改定期宝当前价值分配
	 * 
	 * @author caoyi
	 * @date 2015年08月18日 下午17:14:28
	 * @param List<Map> <br>
	 *         <tt>key: name, title:名称, type:{@link String} </tt><br>
	 *         <tt>key: type, title:类型, type:{@link String} </tt><br>
	 *         <tt>key: value,title:值, type:{@link String} </tt><br>
	 *         <tt>key: code, title:编号, type:{@link String} </tt><br>
	 * @return ResultVo
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo updateTermCurrentVaule(Map<String, Object> paramList) throws SLException {
		List<Map<String, Object>> list=(List<Map<String, Object>>) paramList.get("data");
		Boolean systemFlag = paramList.containsKey("systemFlag") ? (Boolean)paramList.get("systemFlag") : false ;
		//判断是系统调整当前可开放价值，还是人工调整可开放价值
		if(!systemFlag){
			BigDecimal canOpenValue = new BigDecimal("0") ;	
			Map<String, Object>  result=productBusinessRepositoryCustom.findTermCurrentVauleSum2();
			BigDecimal canOpenValueReal=(BigDecimal)result.get("canOpenValue");
			for(int i = 0;i < list.size(); i ++){
				Map<String, Object> map= list.get(i);
	            if(map.get("type").equals("product") || map.get("type").equals("alreadyPreValue") || map.get("type").equals("unopenValue")){
	            	canOpenValue = canOpenValue.add(new BigDecimal((String)map.get("value")));
	            }
	        }	
			if(canOpenValue.compareTo(canOpenValueReal)!=0){
				throw new SLException(" 手工修改活期宝当前价值分配失败!");
			}
		}					
		Map<String, Object> param= new HashMap<String, Object>();
		param.put("lastUpdateDate", new Date());
		for(int i = 0;i < list.size(); i ++){
			Map<String, Object> map= list.get(i);
            if(map.get("type").equals("product")){         	
            	param.put("openValue", map.get("value")); 
            	param.put("productName", map.get("name"));         	
            	int row=productBusinessRepositoryCustom.updateTermOpenValue(param);
        		if (row < 1) {
        			throw new SLException(" 更新定期宝预计开放金额失败!");
        		}
            }
            if(map.get("type").equals("alreadyPreValue")){
            	param.put("alreadyPreValue", map.get("value"));
            	param.put("productType",Constant.PRODUCT_TYPE_04);
            	int row=productBusinessRepositoryCustom.updateBaoAlreadyPreValue(param);
        		if (row < 1) {
        			throw new SLException(" 更新定期宝实际预留价值失败!");
        		}
            }
            if(map.get("type").equals("unopenValue")){
            	param.put("unopenValue", map.get("value"));
            	param.put("productType",Constant.PRODUCT_TYPE_04);
            	int row=productBusinessRepositoryCustom.updateBaoUnOpenVaule(param);
        		if (row < 1) {
        			throw new SLException(" 更新定期宝未开放价值失败!");
        		}
            }
        }		
		log.info("===========手工修改定期宝当前价值分配==============");
		ResultVo resultVo = new ResultVo(true);
		return resultVo;
	}

	@Cacheable(value="cache1", key="'findPartakeForDisplay_' + #productName")
	@Override
	public Map<String, Object> findPartakeForDisplay(String productName) {
		Map<String, Object> result = new HashMap<String, Object>();
		Object partakeObj = productDetailInfoRepository.queryPartakeForDisplay(productName);
		Object[] partake = (Object[])partakeObj;
		result.put("partakeOrganizs", (BigDecimal)partake[0]);
		result.put("partakeCrerigs", (BigDecimal)partake[1]);
		return result;
	}

}
