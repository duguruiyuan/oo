/** 
 * @(#)ProductRateInfoRepository.java 1.0.0 2015年5月8日 下午3:43:54  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.ProductRateInfoEntity;

/**   
 * 
 *  
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年5月8日 下午3:43:54 $ 
 */
public interface ProductRateInfoRepository extends PagingAndSortingRepository<ProductRateInfoEntity, String>{

	
	/**
	 * 获取产品利率
	 * @param typeName
	 * @return
	 */
	@Query("select A from ProductRateInfoEntity A  where A.productId in "
			+ " (select B.id from ProductInfoEntity B where B.productType = "
			+ " (select C.id from ProductTypeInfoEntity C where C.typeName=?)) order by A.yearRate asc")
	public List<ProductRateInfoEntity> findProductRateInfoByTypeName(String typeName);
	

	/**
	 * 查询产品利率
	 *
	 * @author  wangjf
	 * @date    2015年8月15日 上午11:10:27
	 * @param productId
	 * @return
	 */
	@Query("select A from ProductRateInfoEntity A  where A.productId = ?1 order by A.yearRate asc ")
	public List<ProductRateInfoEntity> findProductRateInfoByProductId(String productId);

	/**
	 * 根据产品id查询产品利率
	 * 
	 * @author  zhangzs
	 * @param productId
	 * @return
	 */
	public ProductRateInfoEntity findTopByProductIdOrderByYearRateAscAwardRateAsc(String productId);
	
	/**
	 * 根据产品id和投资金额查询产品利率
	 *
	 * @author  wangjf
	 * @date    2015年8月25日 下午7:07:09
	 * @param productId
	 * @param investAmount
	 * @return
	 */
	@Query("select A from ProductRateInfoEntity A where A.productId = ?1 and lowerLimitDay <= ?2 and upperLimitDay > ?2")
	public ProductRateInfoEntity findProductRateInfoByProductIdAndInvestAmount(String productId, Integer investAmount);
}
