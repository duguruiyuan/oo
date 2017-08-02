/** 
 * @(#)CommissionDetailInfoRepository.java 1.0.0 2015年8月24日 下午3:26:31  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.CommissionDetailInfoEntity;

/**   
 * 提现明细数据访问层
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月24日 下午3:26:31 $ 
 */
public interface CommissionDetailInfoRepository extends PagingAndSortingRepository<CommissionDetailInfoEntity, String> {

}
