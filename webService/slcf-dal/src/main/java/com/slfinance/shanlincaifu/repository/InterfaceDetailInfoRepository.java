/** 
 * @(#)InterfaceDetailInfoRepository.java 1.0.0 2015年7月2日 上午11:51:23  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.InterfaceDetailInfoEntity;

/**   
 * 对外接口数据访问
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月2日 上午11:51:23 $ 
 */
public interface InterfaceDetailInfoRepository extends PagingAndSortingRepository<InterfaceDetailInfoEntity, String>{

	/**
	 * 通过渠道号查找
	 *
	 * @author  wangjf
	 * @date    2015年7月2日 下午2:56:25
	 * @param merchantCode
	 * @return
	 */
	@Query("select a from InterfaceDetailInfoEntity a where a.thirdPartyType = ?1 and a.interfaceType = ?2 and a.merchantCode = ?3 and a.interfaceStatus = '有效'")
	public InterfaceDetailInfoEntity findByThirdPartyMerchantCode(String thirdPartyType, String interfaceType, String merchantCode);
	
	/**
	 * 通过渠道号和接口类型查找
	 *
	 * @author  wangjf
	 * @date    2015年7月3日 上午10:49:29
	 * @param interfaceType
	 * @param merchantCode
	 * @return
	 */
	@Query("select a from InterfaceDetailInfoEntity a where a.interfaceType = ?1 and a.merchantCode = ?2 and a.interfaceStatus = '有效'")
	public InterfaceDetailInfoEntity findByInterfaceMerchantCode(String interfaceType, String merchantCode);
	
	/**
	 * 通过接口类型查询
	 *
	 * @author  wangjf
	 * @date    2015年10月28日 下午5:43:07
	 * @param interfaceType
	 * @return
	 */
	public List<InterfaceDetailInfoEntity> findByInterfaceType(String interfaceType);
	
	/**
	 * 通过渠道编号查询
	 *
	 * @author  wangjf
	 * @date    2015年10月30日 下午3:28:42
	 * @param merchantCode
	 * @return
	 */
	public List<InterfaceDetailInfoEntity> findByMerchantCode(String merchantCode);
	
	/**
	 * 通过第三方名称和业务类型查询接口信息
	 *
	 * @author  wangjf
	 * @date    2016年10月17日 下午4:40:18
	 * @param thirdPartyType 第三方名称
	 * @param interfaceType 业务类型
	 * @return
	 */
	@Query("select a from InterfaceDetailInfoEntity a where a.thirdPartyType = ?1 and a.interfaceType = ?2 and a.interfaceStatus = '有效'")
	public InterfaceDetailInfoEntity findByThirdPartyTypeInterfaceType(String thirdPartyType, String interfaceType);
}
