package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;



/**
 * EmployeeLoadService Test. @author Tools
 */
public class EmployeeLoadServiceTest extends AbstractSpringContextTestSupport  {


	@Autowired
	private EmployeeLoadService employeeLoadService;


	/**
	 * ���Ե���ԭʼ����
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-04-15 15:22:47
	 */
	@Test
	public void testImportEmployeeLoadInfo() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", "");
		param.put("list", "");
		param.put("empNo", "");
		param.put("empName", "");
		param.put("jobStatus", "");
		param.put("credentialsType", "");
		param.put("credentialsCode", "");
		param.put("province", "");
		param.put("city", "");
		param.put("team", "");
		param.put("team", "");
		param.put("team", "");
		param.put("team", "");
		param.put("jobPosition", "");
		param.put("jobLevel", "");
		param.put("custManagerName", "");
		param.put("custManagerCode", "");
		ResultVo result = employeeLoadService.importEmployeeLoadInfo(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * ���Բ�ѯ���е���ԭʼ�����б�
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-04-15 15:22:47
	 */
	@Test
	public void testQueryAllEmployeeLoadInfo() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "10");
		param.put("empName", "");
		param.put("credentialsCode", "");
		param.put("province", "");
		param.put("city", "");
		param.put("term", "");
		param.put("term", "");
		ResultVo result = employeeLoadService.queryAllEmployeeLoadInfo(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	@Test
	public void queryAllImportEmployeeLoadInfo() throws SLException {
		Map<String, Object> params = Maps.newHashMap();
		params.put("start", "0");
		params.put("length", "10");
		ResultVo result = employeeLoadService.queryAllImportEmployeeLoadInfo(params);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 处理原始数据
	 * 
	 * @author zhiwen_feng
	 * @dare 2016-04-18
	 * @return ResultVo
	 * @throws SLException
	 */
	@Test
	public void handleOriginalData()throws SLException{
		ResultVo result = employeeLoadService.handleOriginalData();
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


}
