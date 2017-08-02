/** 
 * @(#)JobRunDetailRepository.java 1.0.0 2015年5月4日 下午4:00:45  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.JobRunDetailEntity;

/**   
 * JOB监听明细数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月4日 下午4:00:45 $ 
 */
public interface JobRunDetailRepository extends PagingAndSortingRepository<JobRunDetailEntity, String>{

	@Query(value=" select * from JobRunDetailEntity where end_time is null and job_id = ? ", nativeQuery=true)
	JobRunDetailEntity findRunningByJobId(String jobId);
}
