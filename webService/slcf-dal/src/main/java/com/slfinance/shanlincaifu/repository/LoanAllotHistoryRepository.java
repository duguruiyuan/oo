/** 
 * @(#)LoanAllotHistoryRepository.java 1.0.0 2015年5月23日 下午3:05:31  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.LoanAllotHistoryEntity;

/**   
 * 债权历史数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月23日 下午3:05:31 $ 
 */
public interface LoanAllotHistoryRepository extends PagingAndSortingRepository<LoanAllotHistoryEntity, String>{

}
