/** 
 * @(#)InvestDetailInfoRepository.java 1.0.0 2015年4月29日 下午3:37:20  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.InvestDetailInfoEntity;

/**   
 * 投资详情数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月29日 下午3:37:20 $ 
 */
public interface InvestDetailInfoRepository extends PagingAndSortingRepository<InvestDetailInfoEntity, String>{

	/**
	 * 获取投资笔数
	 *
	 * @author  caoyi
	 * @date    2015年7月18日 下午13:54:25
	 * @param productName
	 * @return BigDecimal[1]
	 */
	@Query(value = " select count(1) from"
			+" bao_t_invest_info        a, "
			+" bao_t_invest_detail_info b, "
			+" bao_t_product_info       c, "
			+" bao_t_product_type_info  d  "
			+" where a.id = b.invest_id "
			+" and a.product_id = c.id "
			+" and c.product_type = d.id "
			+" and d.type_name=? ", nativeQuery = true)
	public int findInvestCount(String productName);
	

	/**
	 * 获取某产品的投资个数
	 * 
	 * zhangzs
	 * 2015年8月20日 上午10:50:22
	 * @param productId
	 * @param currTerm
	 * @return
	 */
	@Query(value = " select count(b.id) from bao_t_invest_info a, bao_t_invest_detail_info b,bao_t_product_info c where a.id = b.invest_id and a.product_id = c.id and a.product_id = ?", nativeQuery = true)
	public int findInvestCountByProductId(String productId);
	
	/**
	 * 获取某个产品的投资个数
	 *
	 * @author  wangjf
	 * @date    2015年11月11日 下午6:49:34
	 * @param productName
	 * @return
	 */
	@Query(value = " select count(b.id) from bao_t_invest_info a, bao_t_invest_detail_info b,bao_t_product_info c where a.id = b.invest_id and a.product_id = c.id and c.product_name = ?", nativeQuery = true)
	public int findInvestCountByProductName(String productName);
}
