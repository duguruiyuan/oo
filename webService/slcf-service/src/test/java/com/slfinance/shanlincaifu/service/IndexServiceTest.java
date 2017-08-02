package com.slfinance.shanlincaifu.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/** 
 * @author gaoll
 * @version 创建时间：2015年11月9日 上午11:34:12 
 */
@ContextConfiguration(locations = { "classpath:/application-test.xml" })
@ActiveProfiles("dev")
public class IndexServiceTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private IndexService indexService;
	
	@Test
	public void testQueryProduct() throws SLException {
		ResultVo resultVo = indexService.queryProduct();
		System.out.println(resultVo);
	}

}
