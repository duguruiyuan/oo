package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.WealthJobService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;

/**   
 * 优选计划返息
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月4日 下午4:26:43 $ 
 */
@Component
public class WealthRecoveryJob extends AbstractJob {

	@Autowired
	private WealthJobService wealthJobService;
	
	@Override
	public void execute() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("currentDate", DateUtils.getCurrentDate("yyyyMMdd"));
			wealthJobService.autoRecoveryWealth(param);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}	
	}

	@Override
	protected String getJobName() {
		// TODO Auto-generated method stub
		return Constant.JOB_NAME_WEALTH_RECOVERY;
	}

}
