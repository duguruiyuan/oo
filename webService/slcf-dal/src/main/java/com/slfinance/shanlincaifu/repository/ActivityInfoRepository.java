/** 
 * @(#)ActivityInfoRepository.java 1.0.0 2015年5月16日 下午2:38:50  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.ActivityInfoEntity;

/**   
 * 
 *  
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年5月16日 下午2:38:50 $ 
 */
public interface ActivityInfoRepository extends PagingAndSortingRepository<ActivityInfoEntity, String> {
	@Query("select a from ActivityInfoEntity a where a.id=? and a.activityStatus=? and (? between a.startDate and a.expireDate)")
	public ActivityInfoEntity findByActId(String id, String activityStatus, Date dt);
	
	@Query("select a from ActivityInfoEntity a where a.id=? and (? between a.startDate and a.expireDate)")
	public ActivityInfoEntity findByActIdNoStatus(String id, Date dt);
	
	@Query(value=" SELECT * FROM BAO_T_ACTIVITY_INFO WHERE ID = ?1 AND ACTIVITY_STATUS = ?2 AND START_DATE<=?3 AND EXPIRE_DATE+1 > ?3 ", nativeQuery=true)
	public ActivityInfoEntity findByIdAndActivityStatusAndDate(String id, String activityStatus, Date dt);

	@Query("select a from ActivityInfoEntity a where a.id=? and a.activityStatus=?")
	public ActivityInfoEntity findByActIdAndStatus(String id, String activityStatus);
	/**
	 * 平台大促-查询活动信息列表
	 *
	 * @author fengyl
	 * @date 2017年6月29日
	 * @param
	 * @return
	 */
	@Query(value = "select a.* from BAO_T_ACTIVITY_INFO a  where a.id in ('13', '17') order by a.start_date desc", nativeQuery = true)
	List<ActivityInfoEntity> findListActivityInfo();
		
}
