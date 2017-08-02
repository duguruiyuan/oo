package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.ProjectJobService;
import com.slfinance.shanlincaifu.utils.Constant;

/**
 * 项目审核拒绝定时任务
 * @author zhangt
 *
 */
@Component
public class ProjectAuditRefuseJob extends AbstractJob {
	
	@Autowired
	private ProjectJobService projectJobService;
	
	@Override
	protected void execute() {
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			projectJobService.autoRefuseProject(params);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}

	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_PROJECT_AUDIT_REFUSE;
	}

}
