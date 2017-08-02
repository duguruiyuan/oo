/** 
 * @(#)PublishProject.java 1.0.0 2016年1月12日 上午9:42:14  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.shanlincaifu.service.ProjectJobService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 发布项目定时任务
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月12日 上午9:42:14 $ 
 */
@Component
public class ProjectPublishJob extends AbstractJob {

	@Autowired
	private ProjectJobService projectJobService;
	
	@Override
	public void execute() {
		Map<String, Object> params = new HashMap<String, Object>();
		try
		{
			projectJobService.autoPublishProject(params);			
		}
		catch(Exception e) {
			logger.error(e.getMessage());
		}		
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_PROJECT_PUBLISH;
	}

}
