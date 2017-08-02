package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;



/**
 * ProjectJobService Test. @author Tools
 */
public class ProjectJobServiceTest extends AbstractSpringContextTestSupport  {


	@Autowired
	private ProjectJobService projectJobService;


	/**
	 * 测试Job项目定时发布（待发布->发布中）
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-19 11:04:39
	 */
	@Test
	public void testAutoPublishProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("params", "");
		ResultVo result = projectJobService.autoPublishProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试Job项目定时生效（满标复核->还款中）
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-19 11:04:39
	 */
	@Test
	public void testAutoReleaseProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("params", "");
		ResultVo result = projectJobService.autoReleaseProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试Job项目自动流标
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-19 11:04:39
	 */
	@Test
	public void testAutoUnReleaseProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("params", "");
		ResultVo result = projectJobService.autoUnReleaseProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试Job项目自动贴息
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-19 11:04:39
	 */
	@Test
	public void testAutoCompensateProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("params", "");
		ResultVo result = projectJobService.autoCompensateProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试Job项目定时还款
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-19 11:04:39
	 */
	@Test
	public void testAutoRepaymentProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("params", "");
		ResultVo result = projectJobService.autoRepaymentProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试Job项目定时垫付
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-19 11:04:39
	 */
	@Test
	public void testAutoRiskRepaymentProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("params", "");
		ResultVo result = projectJobService.autoRiskRepaymentProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


}
