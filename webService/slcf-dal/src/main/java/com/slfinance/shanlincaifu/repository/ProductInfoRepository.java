/** 
 * @(#)ProductInfoRepository.java 1.0.0 2015年4月29日 上午11:10:12  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.ProductInfoEntity;

/**   
 * 产品数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月29日 上午11:10:12 $ 
 */
public interface ProductInfoRepository extends PagingAndSortingRepository<ProductInfoEntity, String>{

	/**
	 * 根据产品名称查询产品
	 *
	 * @author  wangjf
	 * @date    2015年4月29日 下午2:14:49
	 * @param typeName
	 * @return
	 */
	@Query("select A from ProductInfoEntity A  where A.productType in (select B.id from ProductTypeInfoEntity B where B.typeName = ?) ")
	public ProductInfoEntity findProductInfoByProductTypeName(String typeName);
	
	/**
	 * 查找定期宝产品
	 *
	 * @author  wangjf
	 * @date    2015年8月15日 下午2:32:13
	 * @param typeName
	 * @return
	 */
	@Query("select A from ProductInfoEntity A  where A.productType in (select B.id from ProductTypeInfoEntity B where B.typeName = ?) ")
	public List<ProductInfoEntity> findTermProductInfoByProductTypeName(String typeName);
	
	/**
	 * 查询
	 * 
	 * @author  zhangzs
	 * @param productId
	 * @return
	 */
	public ProductInfoEntity findTopByProductTypeOrderByFavoriteSortDesc(String productType);
}
