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
 * CustBusinessService Test. @author Tools
 */
public class CustBusinessServiceTest extends AbstractSpringContextTestSupport  {


	@Autowired
	private CustBusinessService custBusinessService;


	/**
	 * 测试查询注册报告
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2015-12-11 10:53:51
	 */
	@Test
	public void testFindRegisterReport() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "10");
		param.put("tradeType", "注册");
		param.put("appSource", "");
		param.put("summaryDate", "2015-11");
		param.put("summaryDateBegin", "2015-11-01 01");
		param.put("summaryDateEnd", "2015-11-22 23");
		param.put("utmSource", "");
		param.put("utmMedium", "");
		Map<String, Object> result = custBusinessService.findRegisterReport(param);
		assertNotNull(result);
		assertEquals(result.size() != 0, true);
	}


	/**
	 * 测试导出注册报告
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2015-12-11 10:53:51
	 */
	@Test
	public void testExportRegisterReport() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("tradeType", "");
		param.put("appSource", "");
		param.put("summaryDate", "");
		param.put("summaryDateBegin", "");
		param.put("summaryDateEnd", "");
		param.put("utmSource", "");
		param.put("utmMedium", "");
		ResultVo result = custBusinessService.exportRegisterReport(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


}
