package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.vo.ResultVo;
import com.slfinance.exception.SLException;



/**
 * OfflineWealthService Test. @author Tools
 */
public class OfflineWealthServiceTest extends AbstractSpringContextTestSupport  {


	@Autowired
	private OfflineWealthService offlineWealthService;



	/**
	 * 历史投资 (线下理财)
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-07-11 18:02:58
	 */
	@Test
	public void testQueryOfflineInvestList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "1");
		param.put("custId", "050264d5-baaf-4779-874f-66780c91d81b");
//		param.put("beginInvestDate", "");
//		param.put("endInvestDate", "");
//		param.put("investStatus", "");
		ResultVo result = offlineWealthService.queryOfflineInvestList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


}
