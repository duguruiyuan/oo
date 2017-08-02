/** 
 * @(#)CustDailyValueHistoryRepository.java 1.0.0 2015年5月23日 下午3:05:04  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.CustDailyValueHistoryEntity;


/**   
 * 用户每日持有份额数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月23日 下午3:05:04 $ 
 */
public interface CustDailyValueHistoryRepository extends PagingAndSortingRepository<CustDailyValueHistoryEntity, String>{

}
