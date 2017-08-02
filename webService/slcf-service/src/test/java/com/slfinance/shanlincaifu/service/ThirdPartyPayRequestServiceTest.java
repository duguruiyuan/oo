package com.slfinance.shanlincaifu.service;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.ThridPartyPayRequestEntity;

/**   
 * 测试记录第三方报文
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月5日 上午11:09:21 $ 
 */
@ContextConfiguration(locations = { "classpath:/application-test.xml" })
@ActiveProfiles("dev")
public class ThirdPartyPayRequestServiceTest extends AbstractJUnit4SpringContextTests{
	
	@Autowired
	private ThirdPartyPayRequestService thirdPartyPayRequestService;
	
	/**
	 * 测试保存第三方数据
	 *
	 * @author  wangjf
	 * @throws SLException 
	 * @date    2015年5月22日 下午5:10:50
	 */
	@Test
	public void testSaveThirdPartyPayRequest() throws SLException {
		ThridPartyPayRequestEntity thridPartyPayRequestEntity = new ThridPartyPayRequestEntity();
		thridPartyPayRequestEntity.setRequestUrl("192.16.2.108");
		thridPartyPayRequestEntity.setRequestBody("111111111111111");
		thridPartyPayRequestEntity.setCreateDate(new Date());
		thridPartyPayRequestEntity.setLastUpdateDate(new Date());
		thridPartyPayRequestEntity.setRequestTime("2015-05-23 12:12:12");
		thridPartyPayRequestEntity.setResponseTime("2015-05-23 12:12:12");
		thirdPartyPayRequestService.saveThirdPartyPayRequest(thridPartyPayRequestEntity);
	}
}
