package com.slfinance.shanlincaifu.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.slfinance.exception.SLException;

@ContextConfiguration(locations = { "classpath:/application-test.xml", "classpath:/applicationContext-restclient.xml" })
@ActiveProfiles("dev")
public class SummarizationBusinessHistoryServiceTest extends AbstractJUnit4SpringContextTests{
	
	@Autowired
	private SummarizationBusinessHistoryService summarizationBusinessHistoryService;
	
	@Test
	public void sumBusinessHistoryTest() throws SLException{
		summarizationBusinessHistoryService.sumBusinessHistory();
	}

}
