package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

public class CustManagerServiceTest extends AbstractSpringContextTestSupport {
	
	@Autowired
	CustManagerService custManagerService;

	@Test
	public void queryCustTransferList() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "10");
		param.put("custName", "");
		param.put("credentialsCode", "");
		param.put("mobile", "");
		param.put("auditStatus", "");
		param.put("custManagerId", "");
		ResultVo result = custManagerService.queryCustTransferList(param);
		assertNotNull(result);
	}
	
	@Test
	public void queryCustTransferDetailById() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
	    param.put("custApplyId", "test-CUST_APPLY_001");
	    
		ResultVo result = custManagerService.queryCustTransferDetailById(param);
		assertNotNull(result);
	}
	
	// param.put("", "");
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void saveCustTransfer() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
//	    param.put("custApplyId", "050264d5-baaf-4779-874f-66780c91d81b");
	    param.put("custId", "050264d5-baaf-4779-874f-66780c91d81b");
	    param.put("userId", "20150427000000000000000000000000001");
	    param.put("oldCustManagerId", "20150427000000000000000000000000001");
	    param.put("oldCustManagerName", "孔雄");
	    param.put("oldCustManagerMobile", "13817611024");
	    param.put("oldCustManagerCredentialsCode", "612732199006164811");
	    param.put("newCustManagerId", "f855e82c-7c02-47bb-819e-bc3f5d3663d6");
	    param.put("newCustManagerName", "曹毅");
	    param.put("newCustManagerMobile", "15601938545");
	    param.put("newCustManagerCredentialsCode", "320602198601013031");
	    
	    List<Map<String, Object>> paramSub = new ArrayList<Map<String, Object>>();
	    Map map = new HashMap();
	    map.put("attachmentType", "pdf");
	    map.put("attachmentName", "激发三等奖");
	    map.put("storagePath", "/aa/bb/test/");
	    paramSub.add(map);
	    
	    param.put("attachmentList", paramSub);
	    
		ResultVo result = custManagerService.saveCustTransfer(param);
		assertNotNull(result);
	}
	
	public void saveCustTransferByManager()throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "050264d5-baaf-4779-874f-66780c91d81b");
	    param.put("userId", "20150427000000000000000000000000001");
	}
	
	@Test
	public void saveCustTransferForSyncOldOffLineWealth() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", "test-lyy-002");
		param.put("custId", "test-lyy-002"); // 数据的custid
		param.put("newCustManagerId", "f6417c16-209d-4e7a-a0ae-1af31b1c7839");// 转入
		param.put("memo", "实名认证");
		ResultVo result = custManagerService.saveCustTransferForSyncOldOffLineWealth(param);
		assertNotNull(result);
	}
	
	@Test
	public void auditCustTransfer() throws SLException{
		
	}
	
	@Test
	public void queryCustByManager() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "10");
		param.put("custId", "123");
		param.put("custName", "123");
		param.put("credentialsCode", "123");
		param.put("mobile", "123");
		param.put("beginRegisterDate", "");
		param.put("endRegisterDate", "");
		ResultVo result = custManagerService.queryCustByManager(param);
		assertNotNull(result);
	}
	
	@Test
	public void queryCustWealthByManager() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "10");
		param.put("custId", "123");
		ResultVo result = custManagerService.queryCustWealthByManager(param);
		assertNotNull(result);
	}
	
	@Test
	public void queryCustBankByManager() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "10");
		param.put("custId", "123");
		ResultVo result = custManagerService.queryCustBankByManager(param);
		assertNotNull(result);
	}
	
	@Test
	public void queryCustBankDetailByManager() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("tradeFlowId", "test-tradeId-lyy001");
		ResultVo result = custManagerService
				.queryCustBankDetailByManager(param);
		assertNotNull(result);
	}
	
	@Test
	public void saveCustBankByManager() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "b9b30ff8-1159-486d-9715-12fad4ae4e13");
		param.put("userId", "00080002");
		param.put("bankName", "00080002");
		param.put("openProvince", "00080002");
		param.put("openCity", "00080002");
		param.put("branchBankName", "00080002");
		param.put("bankCardNo", "121212");
		ResultVo result = custManagerService
				.saveCustBankByManager(param);
		assertNotNull(result);
	}
	
	@Test
	public void abandonCustBankByManager() throws SLException {
		
	}
	
	@Test
	public void queryCustNameByManager() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("custManagerId", "test-tradeId-lyy001");
		param.put("custName", "王井");
		ResultVo result = custManagerService.queryCustNameByManager(param);
		assertNotNull(result);
	}
	
	@Test
	public void queryCustManager() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custName", "王井");
		ResultVo result = custManagerService.queryCustManager(param);
		assertNotNull(result);
	}
	
	@Test
	public void queryCustBankByCustId() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "32132111111111312312");
		ResultVo result = custManagerService.queryCustManager(param);
		assertNotNull(result);
	}

	
	/**
	 * 测试数据汇总
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-03-05 13:23:56
	 */
	@Test
	public void testQueryMyWeathSummary() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "ada2861e-80aa-4665-b8ce-c092ff904769");
		ResultVo result = custManagerService.queryMyWeathSummary(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试业绩查询
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-03-05 13:23:56
	 */
	@Test
	public void testQueryMyWeathSummaryList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "10");
		param.put("custId", "f855e82c-7c02-47bb-819e-bc3f5d3663d6");
		param.put("beginInvestDate", "");
		param.put("endInvestDate", "");
		param.put("custName", "");
		param.put("mobile", "");
		ResultVo result = custManagerService.queryMyWeathSummaryList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	@Test
	public void testQueryAllCustManager()throws SLException{
		Map<String, Object> param = Maps.newHashMap();
		param.put("custName", "张三");
		ResultVo result = custManagerService.queryCustName(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	

	
//	/**
//	 * 测试数据汇总
//	 *
//	 * @author  Tools
//	 * @throws SLException 
//	 * @date    2016-03-04 13:36:00
//	 */
//	@Test
//	public void testQueryMyWeathSummary() throws SLException{
//		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("custId", "asdf");
//		ResultVo result = custManagerService.queryMyWeathSummary(param);
//		assertNotNull(result);
//		assertEquals(ResultVo.isSuccess(result), true);
//	}

}
