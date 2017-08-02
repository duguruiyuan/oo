package com.slfinance.shanlincaifu.service;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.job.ComputeLoanPvJob;
import com.slfinance.shanlincaifu.job.DailySettlementJob;
import com.slfinance.shanlincaifu.job.ExperienceDailySettlementJob;
import com.slfinance.shanlincaifu.job.ExperienceWithdrawJob;
//import com.slfinance.shanlincaifu.timer.TimeTaskExecuteService;
import com.slfinance.shanlincaifu.utils.DateUtils;

@ContextConfiguration(locations = { "classpath:/application-test.xml" , "classpath:/applicationContext-restclient.xml"})
@ActiveProfiles("dev")
public class TimeTaskExecuteServiceTest extends AbstractJUnit4SpringContextTests{
//	@Autowired
//	TimeTaskExecuteService timeTaskExecuteService;
	@Autowired
	DailySettlementJob dailySettlementJob;
	@Autowired
	ExperienceDailySettlementJob tybDailySettlementJob;
	@Autowired
	private ComputeLoanPvJob computeLoanPvJob;
	@Autowired
	ExperienceWithdrawJob tybWithdrawJob;
	@Autowired
	private ProductService productService;
	
//	@Test
//	public void execLoanPv(){
//		try {
//			timeTaskExecuteService.execLoanPv();
//		} catch (SLException e) {
//			e.printStackTrace();
//		}
//	}
	@Test
	public void testDailySettlement() throws SLException, ParseException{
		productService.currentDailySettlement(new Date());
	}
	
	@Test
	public void testTYBDailySettlement() throws SLException, ParseException{
		Date now = new Date();
		productService.experienceDailySettlement(new Date());
		System.out.println("============耗时：" + String.valueOf(DateUtils.secondPhaseDiffer(now, new Date())));
		System.out.println("============end============");
	}
	
	@Test
	public void testTYBWithdraw() throws SLException, ParseException{
		productService.experienceWithdraw(new Date());
	}

	@Test
	public void testPV() throws SLException, ParseException{
		computeLoanPvJob.execute();
	}
}
