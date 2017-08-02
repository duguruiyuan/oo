package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.vo.ResultVo;



/**
 * WealthJobService Test. @author Tools
 */
public class WealthJobServiceTest extends AbstractSpringContextTestSupport  {


	@Autowired
	private WealthJobService wealthJobService;


	/**
	 * 测试自动发布优选计划
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-26 11:15:16
	 */
	@Test
	public void testAutoPublishWealth() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("params", "");
		ResultVo result = wealthJobService.autoPublishWealth(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试自动生效优选计划
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-26 11:15:16
	 */
	@Test
	public void testAutoReleaseWealth() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("params", "");
		ResultVo result = wealthJobService.autoReleaseWealthJob(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试流标
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-26 11:15:16
	 */
	@Test
	public void testAutoUnReleaseWealth() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("currentDate", new Date());
		ResultVo result = wealthJobService.autoUnReleaseWealth(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试自动匹配债权(首次撮合、还款撮合)
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-26 11:15:16
	 */
	@Test
	public void testAutoMatchLoan() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		ResultVo result = wealthJobService.autoMatchLoan(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试生成业务报表
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-26 11:15:16
	 */
	@Test
	public void testAutoMonthlyWealth() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("params", "");
		ResultVo result = wealthJobService.autoMonthlyWealth(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试还款
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-26 11:15:16
	 */
	@Test
	public void testAutoRepaymentWealth() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("expectRepaymentDate", "20160315");
		ResultVo result = wealthJobService.autoRepaymentWealth(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试月返息
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-26 11:15:16
	 */
	@Test
	public void testAutoRecoveryWealth() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("currentDate", "20160312");
		ResultVo result = wealthJobService.autoRecoveryWealth(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试到期和提前赎回处理
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-26 11:15:16
	 */
	@Test
	public void testAutoAtoneWealth() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("advanceDate", DateUtils.parseDate("20160315", "yyyyMMdd"));
		param.put("dueDate", DateUtils.parseDate("20160315", "yyyyMMdd"));
		ResultVo result = wealthJobService.autoAtoneWealth(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


}
