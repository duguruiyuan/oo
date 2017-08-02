package com.slfinance.shanlincaifu.job;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.shanlincaifu.service.ProductService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 体验金到期赎回，发送短信任务
 *  
 * @author  caoyi
 * @version $Revision:1.0.0, $Date: 2015年7月2日 下午5:51:37 $ 
 */
@Component
public class ExperienceWithdrawSendSmsJob extends AbstractJob{
	
	@Autowired
	private ProductService productService;
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		try {
			productService.experienceWithdrawSendSms(new Date());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}	
	}

	@Override
	protected String getJobName() {
		// TODO Auto-generated method stub
		return Constant.JOB_NAME_TYBWITHDRAWSENDSMSJOB;
	}

}
