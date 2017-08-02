/** 
 * @(#)AllotInfoRepository.java 1.0.0 2015年4月23日 下午8:18:00  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.slfinance.shanlincaifu.entity.AllotInfoEntity;

/**   
 * 分配信息
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月23日 下午8:18:00 $ 
 */
@RepositoryRestResource(collectionResourceRel = "allots", path = "allot")
public interface AllotInfoRepository extends PagingAndSortingRepository<AllotInfoEntity, String> {
	
}
