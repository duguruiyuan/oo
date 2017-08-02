package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;



/**
 * ProjectCompanyService Test. @author Tools
 */
public class ProjectCompanyServiceTest extends AbstractSpringContextTestSupport  {


	@Autowired
	private ProjectCompanyService projectCompanyService;


	/**
	 * 测试公司列表
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:42
	 */
	@Test
	public void testQueryCompanyList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "10");
		ResultVo result = projectCompanyService.queryCompanyList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试新建公司
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:42
	 */
	@Test
	public void testSaveCompany() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("companyName", "星火融资");
		param.put("projectType", Constant.CUST_KIND_02);
		param.put("telephone", "13717712345");
		param.put("custName", "苏乞儿");
		param.put("communAddress", "祖冲之路2290号");
		param.put("memo", "");
		param.put("userId", "1");
		ResultVo result = projectCompanyService.saveCompany(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试查看公司
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:42
	 */
	@Test
	public void testQueryCompanyById() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("companyId", "3");
		ResultVo result = projectCompanyService.queryCompanyById(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试查询所有公司名称
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:42
	 */
	@Test
	public void testFindByCompanyName() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("params", "");
		ResultVo result = projectCompanyService.findByCompanyName(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


}
