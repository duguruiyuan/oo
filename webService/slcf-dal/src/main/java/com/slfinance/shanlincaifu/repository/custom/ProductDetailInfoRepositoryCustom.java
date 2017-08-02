/** 
 * @(#)ProductDetailInfoRepository.java 1.0.0 2015年4月24日 下午2:10:00  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.data.domain.Page;


/**   
 * 产品详情信息表
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年4月24日 下午2:10:00 $ 
 */
public interface ProductDetailInfoRepositoryCustom{

	
	public Map<String, Object> findDetailByCondition(Map<String,Object> params);
	public Page<Map<String, Object>> findAtoneListByCondition(Map<String,Object> paramMap);
	
	public BigDecimal countAtoneListByCondition(Map<String,Object> params);
	public BigDecimal countInvestByCondition(Map<String,Object> paramMap);
	public Page<Map<String,Object>> findByCondition(Map<String,Object> paramMap);
}
