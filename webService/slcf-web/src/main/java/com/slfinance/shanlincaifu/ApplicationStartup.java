package com.slfinance.shanlincaifu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.slfinance.shanlincaifu.service.GroupService;
import com.slfinance.shanlincaifu.service.JobListenerService;

public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	JobListenerService jobListenerService;
	
	@Autowired
	GroupService groupService;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		jobListenerService.resetJobRun();		
		
		groupService.loadLoanGroup();
		
		groupService.runPopGroup();
	}

}
