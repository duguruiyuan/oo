package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AutoPublishInfoEntity;
import com.slfinance.shanlincaifu.repository.AutoPublishInfoRepository;
import com.slfinance.shanlincaifu.service.AutoPublishJobService;
import com.slfinance.shanlincaifu.utils.Constant;

/**
 * @Desc 自动发布JOB类
 * @author guoyk
 * @date 2017/04/11 19:59:30
 */
@Component
public class AutoPublishJob extends AbstractJob {

	@Autowired
	AutoPublishJobService autoPublishJobService;
	
	@Autowired
	AutoPublishInfoRepository autoPublishInfoRepository;
	
	@Override
	protected void execute() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
				autoPublishJobService.autoPublish(params);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_AUTO_PUBLISH;
	}

}
