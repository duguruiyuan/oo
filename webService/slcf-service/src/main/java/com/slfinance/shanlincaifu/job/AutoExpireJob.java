package com.slfinance.shanlincaifu.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.AutoExpireService;
import com.slfinance.shanlincaifu.utils.Constant;

/**
 * 
 * <更改过期红包的状态定时任务>
 * <功能详细描述>
 * 
 * @author  lzh
 * @version  [版本号, 2017年6月29日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Component
public class AutoExpireJob extends AbstractJob{

	@Autowired
	private AutoExpireService autoExpireService;
	
	@Override
	public void execute() {
		try {
			autoExpireService.AutoExpireDay();
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
		
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_AUTO_EXPIRE_DAY;
	}
	
}
