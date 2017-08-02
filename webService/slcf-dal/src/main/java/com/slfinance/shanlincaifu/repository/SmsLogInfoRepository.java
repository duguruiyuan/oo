/** 
 * @(#)SmsLogInfoRepository.java 1.0.0 2015年4月21日 下午1:01:04  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.SmsLogInfoEntity;

/**   
 * 
 *  
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年4月21日 下午1:01:04 $ 
 */
public interface SmsLogInfoRepository extends PagingAndSortingRepository<SmsLogInfoEntity, String>{

}
