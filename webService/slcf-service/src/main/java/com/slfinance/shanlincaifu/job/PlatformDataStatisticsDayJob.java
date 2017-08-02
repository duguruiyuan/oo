package com.slfinance.shanlincaifu.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.PlatformTradeDataService;
import com.slfinance.shanlincaifu.utils.Constant;
/**
 * 
 * <每天统计平台数据定时任务>
 * <功能详细描述>
 * 
 * @author  lzh
 * @version  [版本号, 2017年6月17日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Component
public class PlatformDataStatisticsDayJob extends AbstractJob{

	@Autowired
	private PlatformTradeDataService platformTradeDataService;
	
	@Override
	public void execute() {
		try {
			platformTradeDataService.findTradeDataDay();
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
		
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_AUTO_PLATFORM_DATA_DAY;
	}

}
