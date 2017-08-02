/** 
 * @(#)ProductTypeInfoRepository.java 1.0.0 2015年4月24日 下午4:49:05  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.ProductTypeInfoEntity;

/**   
 * 产品类型表
 *  
 * @author  caoyi
 * @version $Revision:1.0.0, $Date: 2015年4月24日 下午4:49:05 $ 
 */
public interface ProductTypeInfoRepository extends PagingAndSortingRepository<ProductTypeInfoEntity, String> {

	ProductTypeInfoEntity findByTypeName(String typeName);
}
