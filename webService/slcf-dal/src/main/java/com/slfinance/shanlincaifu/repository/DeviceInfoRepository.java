/** 
 * @(#)DeviceInfoRepository.java 1.0.0 2015年10月19日 下午3:25:11  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.DeviceInfoEntity;

/**   
 * 设备信息数据访问层
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年10月19日 下午3:25:11 $ 
 */
public interface DeviceInfoRepository extends PagingAndSortingRepository<DeviceInfoEntity, String>{

	/**
	 * 通过MeId查询
	 *
	 * @author  wangjf
	 * @date    2015年12月22日 下午2:19:07
	 * @param meId
	 * @return
	 */
	@Query("select count(a) from DeviceInfoEntity a where LOWER(a.appSource) = LOWER(?1) and a.tradeType = ?2 and a.meId = ?3")
	public int countByMeId(String appSource, String tradeType, String meId);
	
	/**
	 * 通过app来源和交易类型查询
	 *
	 * @author  wangjf
	 * @date    2015年12月22日 下午5:04:06
	 * @param appSource
	 * @param tradeType
	 * @return
	 */
	@Query("select a from DeviceInfoEntity a where LOWER(a.appSource) = LOWER(?1) and a.tradeType = ?2")
	public List<DeviceInfoEntity> findByAppSourceAndTradeType(String appSource, String tradeType);
	
}
