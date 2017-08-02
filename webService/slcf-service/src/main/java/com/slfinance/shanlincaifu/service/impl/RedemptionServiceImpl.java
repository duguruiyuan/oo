/** 
 * @(#)InvestService.java 1.0.0 2015年4月24日 下午2:11:13  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.ProductDetailInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.RedemptionService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 
 * 赎回管理
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年4月24日 下午2:11:13 $ 
 */
@Service("redemptionService")
public class RedemptionServiceImpl implements RedemptionService {

	@Autowired
	private ProductDetailInfoRepositoryCustom productDetailInfoRepository;
	@Autowired
	private ParamService paramService;
	@Autowired
	private SubAccountInfoRepository subAccountInfoRepository;
	
	//	赎回管理-列表（多表）
	public Map<String, Object> findAtoneListByCondition(Map<String,Object> params){
		Map<String,Object> rtnMap=new HashMap<String, Object>();
		Page<Map<String, Object>> page=productDetailInfoRepository
				.findAtoneListByCondition(params);
		rtnMap.put("iTotalDisplayRecords", page.getTotalElements());
		rtnMap.put("data", page.getContent());
		if(page.getTotalElements()!=0){
			BigDecimal sumAmount=productDetailInfoRepository.countAtoneListByCondition(params);
			rtnMap.put("totalAmount",sumAmount==null?BigDecimal.ZERO:sumAmount);
		}
		return rtnMap;
	}

	//	赎回管理-额度查询（统计）
	public Map<String, Object> countByCondition(Map<String, Object> params){
		Map<String,Object> rtnMap=new HashMap<String, Object>();
		//总额度,参数表
		BigDecimal redemptionSumAmount=paramService.findFixLimitedAmount();
		rtnMap.put("redemptionSumAmount",redemptionSumAmount);
		//分账户，剩余额度
		BigDecimal redemptionAvailableAmount=subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_ERAN).getAccountAmount();
		rtnMap.put("redemptionAvailableAmount",redemptionAvailableAmount);
		//已使用额度
		BigDecimal amount=redemptionSumAmount.subtract(redemptionAvailableAmount);
		rtnMap.put("amount",amount);
		return rtnMap;
	}
	
	//	赎回管理--详情
	public Map<String, Object> findDetailByCondition(Map<String, Object> params){
		Map<String, Object> maps=this.productDetailInfoRepository.findDetailByCondition(params);
		maps.put("credentialsType", "身份证");
		return maps;
	}
	
}
