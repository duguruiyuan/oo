/** 
 * @(#)UnbindInfoRepository.java 1.0.0 2015年7月9日 上午10:18:40  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.UnbindInfoEntity;

/**   
 * 银行卡解绑申请数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月9日 上午10:18:40 $ 
 */
public interface UnbindInfoRepository extends PagingAndSortingRepository<UnbindInfoEntity, String>{

	/**
	 * 通过关联主键和解绑状态查询
	 *
	 * @author  wangjf
	 * @date    2015年7月16日 下午1:58:59
	 * @param relatePrimary
	 * @param unbindStatus
	 * @return
	 */
	public List<UnbindInfoEntity> findByRelatePrimaryAndUnbindStatus(@Param("relatePrimary") String relatePrimary, @Param("unbindStatus") String unbindStatus);
	
}
