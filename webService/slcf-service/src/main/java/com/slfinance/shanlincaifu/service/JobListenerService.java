/** 
 * @(#)JobListenerService.java 1.0.0 2015年1月19日 上午11:57:49  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.service;

import java.util.Date;
import java.util.List;

import com.slfinance.shanlincaifu.entity.JobRunListenerEntity;

/**
 * job监控
 * 
 * @author 孟山
 * @version $Revision:1.0.0, $Date: 2015年1月19日 上午11:57:49 $
 */
public interface JobListenerService {

	/**
	 * 版本控制 获取跑JOB的权限
	 * 
	 * @param jobClass
	 * @param jobName
	 * @param version
	 * @return
	 */
	public String getStartJobRunByClassAndVersion(String jobClass, String jobName);

	/**
	 * job结束,释放资源
	 * 
	 * @param jobRunDetailId
	 * @return
	 */
	public boolean stopJobRun(String jobRunDetailId, String exception);

	/**
	 * 获取所有JOB列表(主要用于监控当前运行的JOB)
	 * 
	 * @return
	 */
	public List<JobRunListenerEntity> selectAllJobRunListener();

	/**
	 * 如果一个JOB连续运行5个小时以上开始给管理员发送邮件 或者直接 发送邮件
	 * 
	 * @param lastUpdateDate
	 *            最后更新时间
	 * @param jobName
	 *            JOB名称
	 * @param jobClass
	 *            JOB class权限名
	 * @param compareTime
	 *            是否比较时间
	 */
	public void sendEmail(Date lastUpdateDate, String jobName, String jobClass, boolean compareTime);
	
	/**
	 * 重置任务
	 * 注：每次重启程序时将运行中的JobRun置为未运行
	 *
	 * @author  wangjf
	 * @date    2017年6月19日 上午9:30:42
	 */
	public void resetJobRun();
}
