package com.slfinance.shanlincaifu.job;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.shanlincaifu.service.ProductService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 体验宝结息
 *  
 * @author  linhj
 * @version $Revision:1.0.0, $Date: 2015年5月16日 下午4:44:01 $ 
 */
@Component
public class ExperienceDailySettlementJob extends AbstractJob{
	
	@Autowired
	private ProductService productService;

	@Override
	public void execute() {
		try {
			productService.experienceDailySettlement(new Date());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}		
		
	}

	@Override
	protected String getJobName() {
		// TODO Auto-generated method stub
		return Constant.JOB_NAME_TYBDAILYACCRUALJOB;
	}
	
	
}
