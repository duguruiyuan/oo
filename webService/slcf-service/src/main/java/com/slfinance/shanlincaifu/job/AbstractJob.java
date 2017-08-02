package com.slfinance.shanlincaifu.job;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.shanlincaifu.service.JobListenerService;
import com.slfinance.vo.ResultVo;

/**
 * 
 * Job任务调度需要实现此接口
 * <p>
 * 添加 JOB监听 启动前检查是否已运行，如果已运行则不运行此次JOB;为了防止监听影响JOB正常运行,如果监听报错 则JOB会继续运行
 * </p>
 * 
 * @author larry
 * @version 1.0
 */
abstract class AbstractJob {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	abstract protected void execute();

	@Autowired
	private JobListenerService jobListenerService;
	
	private String jobRunDetailId = null;
	private Date lastUpdateDate = null;

	/**
	 * 获取当前job的名称，主要以功能命名
	 * 
	 * @return
	 */
	abstract protected String getJobName();

	/**
	 * 调度任务执行逻辑
	 * <p>
	 * <b>去掉final job使用到了代理,final类型的不能被代理 导致父类不能注入;故最后去掉final</b>
	 * </p>
	 */
	public ResultVo invoke() {
		logger.info("{} invoke....", this.getClass().getName());
		try {
			if (!StringUtils.isEmpty(jobRunDetailId)) {
				logger.error("{}【{}】已存在运行JOB,此次启动失败!(单台服务器重复运行此JOB)", this.getClass().getName(), getJobName());
				jobListenerService.sendEmail(lastUpdateDate, getJobName(), this.getClass().getName(), false);
				return new ResultVo(false, getJobName() + "JOB已运行,请勿重复操作!");
			} else if ((jobRunDetailId = jobListenerService.getStartJobRunByClassAndVersion(this.getClass().getName(), getJobName())) == null) {
				logger.error("{}【{}】已存在运行JOB,此次启动失败!(多台服务器重复运行此JOB或者为JOB运行时关闭服务器)", this.getClass().getName(), getJobName());
				return new ResultVo(false, getJobName() + "JOB已运行,请勿重复操作!!");
			}
		} catch (Exception e) {
			logger.error("{}【{}】JOB监听启动异常", this.getClass().getName(), e);
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					execute();
					callback();
				} catch (Exception e) {
					catchException(e);
				} finally {
					finallyCallBack();
				}
			}
		}).start();
		return new ResultVo(true);
	}

	/**
	 * 任务调度完成回调扩展接口 eg: 任务完成后发送邮件等
	 */
	protected void callback() {
	}

	protected void catchException(Throwable t) {
		stopJob(t.getMessage());
	}

	protected void finallyCallBack() {
		stopJob(null);
	}

	protected void stopJob(String exception) {
		if (jobRunDetailId != null) {
			try {
				jobListenerService.stopJobRun(jobRunDetailId, exception);
				logger.info("{}【{}】JOB停止运行....", this.getClass().getName(), getJobName());
			} catch (Exception e) {
				logger.error(this.getClass().getName() + "JOB监听停止异常", e);
			} finally {
				jobRunDetailId = null;
				logger.info(this.getClass().getName() + "JOB运行结束...");
			}
		}
	}

}
