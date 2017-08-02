/** 
 * @(#)EmptionInfoRepository.java 1.0.0 2015年4月30日 下午4:09:55  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.EmptionInfoEntity;

/**   
 * 购买数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月30日 下午4:09:55 $ 
 */
public interface EmptionInfoRepository  extends PagingAndSortingRepository<EmptionInfoEntity, String>{

}
