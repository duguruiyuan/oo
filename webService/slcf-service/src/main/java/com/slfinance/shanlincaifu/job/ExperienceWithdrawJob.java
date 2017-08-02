package com.slfinance.shanlincaifu.job;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.shanlincaifu.service.ProductService;
import com.slfinance.shanlincaifu.utils.Constant;

@Component
public class ExperienceWithdrawJob extends AbstractJob{
	
	@Autowired
	private ProductService productService;
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		try {
			productService.experienceWithdraw(new Date());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}	
	}

	@Override
	protected String getJobName() {
		// TODO Auto-generated method stub
		return Constant.JOB_NAME_TYBWITHDRAWJOB;
	}

}
