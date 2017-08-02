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
 * EmployeeService Test. @author Tools
 */
public class EmployeeServiceTest extends AbstractSpringContextTestSupport  {


	@Autowired
	private EmployeeService employeeService;


	/**
	 * ����ҵ������
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-04-21 14:12:20
	 */
	@Test
	public void testGetCommissionInfo() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "");
		param.put("length", "");
		param.put("commMonth", "");
		param.put("empName", "");
		param.put("credentialsCode", "");
		param.put("proName", "");
		param.put("cityName", "");
		param.put("dept", "");
		param.put("dept", "");
		ResultVo result =employeeService.queryCommissionList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * ����ҵ����ϸ
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-04-21 14:12:20
	 */
	@Test
	public void testGetCommissionInfoDetail() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("commissionId", "");
		param.put("Id", "");
		param.put("commMonth", "");
		ResultVo result = employeeService.queryCommissionAtoneDetailList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * �����������
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-04-21 14:12:20
	 */
	@Test
	public void testGetAtoneInfo() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "");
		param.put("length", "");
		param.put("commMonth", "");
		param.put("empName", "");
		param.put("credentialsCode", "");
		param.put("proName", "");
		param.put("cityName", "");
		param.put("dept", "");
		param.put("dept", "");
		ResultVo result = employeeService.queryCommissionAtoneList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * ���������ϸ
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-04-21 14:12:20
	 */
	@Test
	public void testGetAtoneInfoDetail() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("commissionId", "");
		param.put("Id", "");
		param.put("commMonth", "");
		ResultVo result = employeeService.queryCommissionAtoneDetailList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * ����Ա��ҵ���б�
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-04-21 14:12:20
	 */
	@Test
	public void testQueryEmployeeMonthAchievement() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 1);
//		param.put("commMonth", "");
		ResultVo result = employeeService.queryEmployeeMonthAchievement(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


}
