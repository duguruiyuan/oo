/** 
 * @(#)JobRunListenerRepository.java 1.0.0 2015年5月4日 下午4:00:19  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.JobRunListenerEntity;

/**   
 * Job监听数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月4日 下午4:00:19 $ 
 */
public interface JobRunListenerRepository extends PagingAndSortingRepository<JobRunListenerEntity, String> {

	/**
	 * 通过任务名称查询
	 *
	 * @author  wangjf
	 * @date    2015年5月4日 下午4:03:29
	 * @param jobClassName
	 * @return
	 */
	public JobRunListenerEntity findByJobClassName(String jobClassName);
	

	/**
	 * 通过任务名称查询
	 * @author  wangjf
	 * @date    2015年5月4日 下午4:03:29
	 * @param jobClassName
	 * @return
	 */
	public JobRunListenerEntity findByJobName(String jobName);
	
	/**
	 * 将任务状态由运行中改为未运行
	 *
	 * @author  wangjf
	 * @date    2017年6月19日 上午9:36:07
	 * @param unRunningStatus
	 * @param runningStatus
	 */
	@Modifying
	@Query("update JobRunListenerEntity set executeStatus = :unRunningStatus where executeStatus = :runningStatus ")
	public int updateJob(@Param("unRunningStatus")String unRunningStatus, @Param("runningStatus")String runningStatus);
}
