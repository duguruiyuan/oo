/** 
 * @(#)AllotDetailInfoRepository.java 1.0.0 2015年5月1日 上午11:24:33  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.AllotDetailInfoEntity;

/**   
 * 分配信息数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月1日 上午11:24:33 $ 
 */
public interface AllotDetailInfoRepository extends PagingAndSortingRepository<AllotDetailInfoEntity, String>{

}
