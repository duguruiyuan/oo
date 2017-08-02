/** 
 * @(#)AllotServiceImpl.java 1.0.0 2015年4月23日 下午8:03:27  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AllotDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.AllotInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductTypeInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthHoldInfoEntity;
import com.slfinance.shanlincaifu.repository.AllotInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductTypeInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthHoldInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.AllotRepositoryCustom;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.AllotService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

/**   
 * 分配信息服务接口实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月23日 下午8:03:27 $ 
 */
@Slf4j
@Service("allotService")
@Transactional(readOnly = true)
public class AllotServiceImpl implements AllotService {

	@Autowired
	AllotRepositoryCustom allotRepositoryCustom;
	
	@Autowired
	AllotInfoRepository allotInfoRepository;
	
	@Autowired
	SubAccountInfoRepository subAccountInfoRepository;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private ProductTypeInfoRepository productTypeInfoRepository;
	
	@Autowired
	private AccountFlowService accountFlowService;
	
	@Autowired
	private ProductInfoRepository productInfoRepository;
	
	@Autowired
	private ProductDetailInfoRepository productDetailInfoRepository;
	
	@Autowired
	private WealthHoldInfoRepository wealthHoldInfoRepository;
	
	@Override
	public Map<String, Object> findLoanAllotList(Map<String, Object> param) {
		// TODO 参数校验

		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = allotRepositoryCustom.findAllot(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo cancelLoanAllot(Map<String, Object> param) throws SLException{
		// TODO 参数校验
		
		AllotInfoEntity entity = allotInfoRepository.findOne((String)param.get("id"));
		if(entity == null) {
			throw new SLException("撤销失败！该笔分配不存在");
		}
		if(!entity.getAllotStatus().equals(Constant.ALLOT_STATUS_01)){
			throw new SLException("撤销失败！非分配状态不允许撤销");
		}
		
		ProductTypeInfoEntity productTypeInfoEntity = productTypeInfoRepository.findOne(entity.getRelatePrimary());
		
		// 取消分配给债权人的价值（按当前实际价值撤销）
		BigDecimal tradeAmount = allotRepositoryCustom.findAllotLoanCount(param);
		log.debug("===========公司居间人分账价值减少==============");
		// 3.1 公司居间人分账价值减少
		SubAccountInfoEntity subAccountInfoEntity = subAccountInfoRepository.findBySubAccountNo(Constant.PRODUCT_TYPE_01.equals(productTypeInfoEntity.getTypeName()) ? Constant.SUB_ACCOUNT_NO_CENTER : Constant.SUB_ACCOUNT_NO_CENTER_11);
		subAccountInfoEntity.setAccountTotalValue(ArithUtil.sub(subAccountInfoEntity.getAccountTotalValue(), tradeAmount));
		subAccountInfoEntity.setAccountAvailableValue(ArithUtil.sub(subAccountInfoEntity.getAccountAvailableValue(), tradeAmount));
		subAccountInfoEntity.setBasicModelProperty((String)param.get("updateUser"), false);
		
		// 3.2 记录 账户流水信息表		
		accountFlowService.saveAccountFlow(null, subAccountInfoEntity, null, null, "3", 
				SubjectConstant.TRADE_FLOW_TYPE_ALLOT_CANCEL, numberService.generateTradeBatchNumber(), SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_ALLOT_CANCEL, 
				Constant.TABLE_BAO_T_ALLOT_INFO, entity.getId(), SubjectConstant.SUBJECT_TYPE_VALUE);
		
		entity.setAllotStatus(Constant.ALLOT_STATUS_03);//状态置为已撤销
		entity.setBasicModelProperty((String)param.get("updateUser"), false);
		
		// 4、更新参与机构数、债权数	
		Object partakeObj = productDetailInfoRepository.queryPartake(productTypeInfoEntity.getTypeName());
		Object[] partake = (Object[])partakeObj;
		List<ProductInfoEntity> productList = productInfoRepository.findTermProductInfoByProductTypeName(productTypeInfoEntity.getTypeName());
		for(ProductInfoEntity productInfoEntity : productList) {
			ProductDetailInfoEntity productDetailInfoEntity = productDetailInfoRepository.findByProductId(productInfoEntity.getId());
			productDetailInfoEntity.setPartakeOrganizs((BigDecimal)partake[0]);
			productDetailInfoEntity.setPartakeCrerigs((BigDecimal)partake[1]);
		}
		return new ResultVo(true, "撤销成功！");
	}

	@Override
	public Map<String, Object> findCanAllotLoanList(Map<String, Object> param) {
		// TODO 参数校验
		
		// 1、债权价值、金额、笔数汇总
		Map<String, Object> result = allotRepositoryCustom.findCanAllotLoanCount(param);
				
		// 2、查询可分配额的债权
		Page<Map<String, Object>> page = allotRepositoryCustom.findCanAllotLoan(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());	
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo allotLoan(Map<String, Object> param) throws SLException{
		if(param.get("loanList") == null){
			throw new SLException("债权列表不能为空");
		}
		
		Date useDate = DateUtils.parseDate((String)param.get("useDate"), "yyyy-MM-dd");
		if(useDate.compareTo(DateUtils.getStartDate(new Date())) < 0) {
			throw new SLException("使用日期不能小于当天");
		}
		
		List<Map<String, Object>> loanList = (List<Map<String, Object>>)param.get("loanList");
		BigDecimal judgeAlloted = allotRepositoryCustom.judgeAllotedLoanIdList(loanList);
		if(judgeAlloted.compareTo(new BigDecimal("0")) != 0){
			throw new SLException("包含已经分配的债权，请勿重复分配");
		}
		BigDecimal tradeAmount = allotRepositoryCustom.sumByLoanIdList(loanList);//分配金额
		
		String productTypeName = (String)param.get("productId");
		ProductTypeInfoEntity productTypeInfoEntity = productTypeInfoRepository.findByTypeName(productTypeName);
		
		// 1、生成分配信息
		log.debug("===========生成分配信息==============");
		AllotInfoEntity entity = new AllotInfoEntity();
		entity.setRelateType(Constant.TABLE_BAO_T_PRODUCT_TYPE_INFO);
		entity.setRelatePrimary(productTypeInfoEntity.getId());
		entity.setAllotCode(numberService.generateAllotNumber());// 分配编码
		entity.setAllotDate(new Timestamp(System.currentTimeMillis()));
		entity.setAllotAmount(tradeAmount);
		entity.setUseDate(useDate);
		entity.setAllotStatus(Constant.ALLOT_STATUS_01);
		entity.setBasicModelProperty((String)param.get("createUser"), true);
		entity = allotInfoRepository.save(entity);
		
		// 2、生成分配详情信息
		log.debug("===========生成分配详情信息==============");
		List<AllotDetailInfoEntity> list = new ArrayList<AllotDetailInfoEntity>();
		List<Map<String, Object>> pvloanList = allotRepositoryCustom.findByLoanIdList(loanList);
		for(Map<String, Object> map : pvloanList)
		{
			if(StringUtils.isEmpty((String)map.get("loanId"))){
				throw new SLException("债权主键不能为空");
			}
			if(StringUtils.isEmpty(map.get("currentValue").toString())){
				throw new SLException("债权价值不能为空");
			}	
			AllotDetailInfoEntity detail = new AllotDetailInfoEntity();
			detail.setAllotId(entity.getId());
			detail.setLoanId((String)map.get("loanId"));
			detail.setTradeCode(numberService.generateTradeNumber());// 交易编号
			detail.setTradeAmount(new BigDecimal(map.get("currentValue").toString())); //交易金额
			detail.setBasicModelProperty((String)param.get("createUser"), true);
			list.add(detail);
		}
		allotRepositoryCustom.batchInsertAllotDetail(list);
		
		// 3、公司居间人分账价值增长
		log.debug("===========公司居间人分账价值增长==============");
		// 3.1 公司居间人分账价值增长
		SubAccountInfoEntity subAccountInfoEntity = subAccountInfoRepository.findBySubAccountNo(Constant.PRODUCT_TYPE_01.equals(productTypeInfoEntity.getTypeName()) ? Constant.SUB_ACCOUNT_NO_CENTER : Constant.SUB_ACCOUNT_NO_CENTER_11);
		subAccountInfoEntity.setAccountTotalValue(ArithUtil.add(subAccountInfoEntity.getAccountTotalValue(), tradeAmount));
		subAccountInfoEntity.setAccountAvailableValue(ArithUtil.add(subAccountInfoEntity.getAccountAvailableValue(), tradeAmount));
		subAccountInfoEntity.setBasicModelProperty((String)param.get("createUser"), false);
		
		// 3.2 记录 账户流水信息表		
		accountFlowService.saveAccountFlow(null, subAccountInfoEntity, null, null, "3", 
				SubjectConstant.TRADE_FLOW_TYPE_ALLOT, numberService.generateTradeBatchNumber(), SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_ALLOT, 
				Constant.TABLE_BAO_T_ALLOT_INFO, entity.getId(), SubjectConstant.SUBJECT_TYPE_VALUE);
		
		// 4、更新参与机构数、债权数	
		Object partakeObj = productDetailInfoRepository.queryPartake(productTypeInfoEntity.getTypeName());
		Object[] partake = (Object[])partakeObj;
		List<ProductInfoEntity> productList = productInfoRepository.findTermProductInfoByProductTypeName(productTypeInfoEntity.getTypeName());
		for(ProductInfoEntity productInfoEntity : productList) {
			ProductDetailInfoEntity productDetailInfoEntity = productDetailInfoRepository.findByProductId(productInfoEntity.getId());
			productDetailInfoEntity.setPartakeOrganizs((BigDecimal)partake[0]);
			productDetailInfoEntity.setPartakeCrerigs((BigDecimal)partake[1]);
		}
		
		return new ResultVo(true, "分配成功！");
	}

	@Override
	public Map<String, Object> findLoanAllotDetailList(Map<String, Object> param) {
		// TODO 参数校验
		
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = allotRepositoryCustom.findAllotLoanById(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo autoAllotWealth(Map<String, Object> param) {

		// 取分配总值
		Map<String, Object> result = allotRepositoryCustom.findCanAllotLoanCount(param);
		BigDecimal allotAmount = new BigDecimal(result.get("totalPv").toString());
		if(allotAmount.compareTo(BigDecimal.ZERO) == 0) {
			return new ResultVo(true, "本次没有待分配数据");
		}
		
		// 记录分配表
		ProductTypeInfoEntity productTypeInfoEntity = productTypeInfoRepository.findByTypeName(Constant.PRODUCT_TYPE_06);
		AllotInfoEntity entity = new AllotInfoEntity();
		entity.setRelateType(Constant.TABLE_BAO_T_PRODUCT_TYPE_INFO);
		entity.setRelatePrimary(productTypeInfoEntity.getId());
		entity.setAllotCode(numberService.generateAllotNumber());// 分配编码
		entity.setAllotDate(new Timestamp(System.currentTimeMillis()));
		entity.setAllotAmount(allotAmount);
		entity.setUseDate(new Date());
		entity.setAllotStatus(Constant.ALLOT_STATUS_02);
		entity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		entity = allotInfoRepository.save(entity);
		
		// 记录分配明细与持有价值情况
		param.put("start", 0);
		param.put("length", Integer.MAX_VALUE);
		Page<Map<String, Object>> page = allotRepositoryCustom.findCanAllotLoan(param);
		List<AllotDetailInfoEntity> detailList = Lists.newArrayList();
		List<WealthHoldInfoEntity> holdList = Lists.newArrayList();
		for(Map<String, Object> map : page.getContent())
		{
			// 记录分配明细
			AllotDetailInfoEntity detail = new AllotDetailInfoEntity();
			detail.setAllotId(entity.getId());
			detail.setLoanId((String)map.get("id"));
			detail.setTradeCode(numberService.generateTradeNumber());// 交易编号
			detail.setTradeAmount(new BigDecimal(map.get("currentValue").toString())); //交易金额
			detail.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			detailList.add(detail);
			
			// 记录持有明细
			WealthHoldInfoEntity wealthHoldInfoEntity = new WealthHoldInfoEntity();
			wealthHoldInfoEntity.setLoanId((String)map.get("id"));
			wealthHoldInfoEntity.setCustId(Constant.CUST_ID_WEALTH_CENTER);
			wealthHoldInfoEntity.setHoldScale(new BigDecimal("1"));
			wealthHoldInfoEntity.setHoldAmount(new BigDecimal(map.get("currentValue").toString()));
			wealthHoldInfoEntity.setHoldStatus(Constant.HOLD_STATUS_01);
			wealthHoldInfoEntity.setIsCenter(Constant.IS_CENTER_01);
			holdList.add(wealthHoldInfoEntity);
		}
		allotRepositoryCustom.batchInsertAllotDetail(detailList);
		wealthHoldInfoRepository.save(holdList);

		return new ResultVo(true, "分配成功！");
	}

}
