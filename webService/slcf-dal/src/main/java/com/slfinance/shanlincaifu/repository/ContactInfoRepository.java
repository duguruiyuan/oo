/** 
 * @(#)ContactInfoRepository.java 1.0.0 2015年7月9日 上午10:21:22  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.ContactInfoEntity;

/**   
 * 联系人数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月9日 上午10:21:22 $ 
 */
public interface ContactInfoRepository extends PagingAndSortingRepository<ContactInfoEntity, String>{

}
