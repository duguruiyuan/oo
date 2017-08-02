/** 
 * @(#)JobListenerServiceImpl.java 1.0.0 2015年1月19日 下午12:01:57  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.service.impl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slfinance.shanlincaifu.entity.JobRunDetailEntity;
import com.slfinance.shanlincaifu.entity.JobRunListenerEntity;
import com.slfinance.shanlincaifu.repository.JobRunDetailRepository;
import com.slfinance.shanlincaifu.repository.JobRunListenerRepository;
import com.slfinance.shanlincaifu.service.JobListenerService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.util.SystemUtil;

/**
 * job监控
 * 
 * @author 孟山
 * @version $Revision:1.0.0, $Date: 2015年1月19日 下午12:01:57 $
 */
@Service
public class JobListenerServiceImpl implements JobListenerService {

	/** 可以使用资源 */
	public static final String JOB_LISTENER_STATUS_VALID = "未运行";
	/** 不可以使用资源 */
	public static final String JOB_LISTENER_STATUS_INVALID = "运行中";

	@Autowired
	private JobRunListenerRepository jobRunListenerMapper;
	@Autowired
	private JobRunDetailRepository jobRunDetailMapper;
//	@Autowired
//	private MailService mailService;
//	/**
//	 * JOB 运行时间超过5小时 下次再有此JOB运行时会发送邮件
//	 */
//	private long noticeLongInteval = 3600000;
//	private int hour = 5;
//	@Value("${mail.smtp.to}")
//	private String emailTo;

	private transient Logger logger = LoggerFactory.getLogger(JobListenerServiceImpl.class);

	@Transactional
	@Override
	public String getStartJobRunByClassAndVersion(String jobClass, String jobName) {
		JobRunDetailEntity result = null;
		JobRunListenerEntity jobRunListener = jobRunListenerMapper.findByJobClassName(jobClass);
		if (jobRunListener == null) {
			jobRunListener = new JobRunListenerEntity(jobClass, jobName);
			jobRunListener.setExecuteStatus(JOB_LISTENER_STATUS_VALID);
			jobRunListener.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			jobRunListener = jobRunListenerMapper.save(jobRunListener);
		}
		else if(Constant.VALID_STATUS_INVALID.equals(jobRunListener.getRecordStatus())){
			return null; // 无效任务不执行
		}
		
		if (!JOB_LISTENER_STATUS_INVALID.equals(jobRunListener.getExecuteStatus())) {
			jobRunListener.setExecuteStatus(JOB_LISTENER_STATUS_INVALID);
			jobRunListener.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			String ip = null;
			try {
				ip = SystemUtil.getSystemLocalIp();
			} catch (UnknownHostException e) {
			}
			JobRunDetailEntity jobRunDetail = new JobRunDetailEntity(jobRunListener.getId(), ip, SystemUtil.getLocalHost());
			jobRunDetail.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			jobRunDetail.setStartTime(new Date());
			result = jobRunDetailMapper.save(jobRunDetail);
		} else {
			// 如果在运行中并且已经隔天没跑
			//sendEmail(jobRunListener.getLastUpdateDate(), jobName, jobClass, true);
			// 此时是在跑JOB时服务器停止了发生的情况
			logger.error("{} JOB发生异常:请及时查收", jobName);
			logger.error("{} JOB发生异常:请及时查收!", jobName);
		}
		
		if(result == null)
			return null;
		return result.getId();
	}

	@Transactional
	@Override
	public boolean stopJobRun(String jobRunDetailId, String exception) {
		JobRunDetailEntity jobRunDetail = jobRunDetailMapper.findOne(jobRunDetailId);
		jobRunDetail.setException(exception);
		jobRunDetail.setEndTime(new Date());
		jobRunDetail.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		
		JobRunListenerEntity jobRunListener = jobRunListenerMapper.findOne(jobRunDetail.getJobId());
		jobRunListener.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		jobRunListener.setExecuteStatus(JOB_LISTENER_STATUS_VALID);
		return true;
	}

	@Override
	public List<JobRunListenerEntity> selectAllJobRunListener() {
		Iterable<JobRunListenerEntity> iters = jobRunListenerMapper.findAll();
		List<JobRunListenerEntity> list = new ArrayList<JobRunListenerEntity>();
		for(Iterator<JobRunListenerEntity> iter = iters.iterator(); iter.hasNext();){
			JobRunListenerEntity entity = iter.next();
			list.add(entity);
		}
		return list;
	}

	public void sendEmail(Date lastUpdateDate, String jobName, String jobClass, boolean compareTime) {
//		String title = "";
//		String message = "";
//		if (compareTime) {
//			if (lastUpdateDate != null && System.currentTimeMillis() - lastUpdateDate.getTime() > noticeLongInteval * hour) {
//				title = jobName + "JOB已持续运行" + hour + "小时以上，请及时查看是否运行异常";
//				message = title + "(" + jobClass + ")";
//			}
//		} else {
//			title = jobName + "JOB上次运行未正常关闭，请及时检查";
//			message = title + "(" + jobClass + ")";
//		}
//		mailService.sendMailToDevelopmentLeader(new MailInfo(title, message, emailTo));
	}

	@Transactional
	@Override
	public void resetJobRun() {
		jobRunListenerMapper.updateJob(JOB_LISTENER_STATUS_VALID, JOB_LISTENER_STATUS_INVALID);		
	}
}
