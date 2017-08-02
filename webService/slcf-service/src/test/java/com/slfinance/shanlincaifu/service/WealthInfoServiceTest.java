package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.custom.WealthInfoRepositoryCustom;
import com.slfinance.vo.ResultVo;



/**
 * WealthInfoService Test. @author Tools
 */

public class WealthInfoServiceTest extends AbstractSpringContextTestSupport {


	@Autowired
	WealthInfoService wealthInfoService;

	@Autowired
	WealthInfoRepositoryCustom wealthInfoRepositoryCustom; 

	/**
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-23 11:34:53
	 */
	@Test
	public void testQueryWealthList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 1);
		param.put("lendingNo", "16022901");
//		param.put("lendingType", "");
//		param.put("typeTerm", "");
//		param.put("wealthStatus", "");
//		param.put("beginReleaseDate", "");
//		param.put("endReleaseDate", "");
//		param.put("beginEffectDate", "");
//		param.put("endEffectDate", "");
//		param.put("beginEndDate", "");
//		param.put("endEndDate", "");
		ResultVo result = wealthInfoService.queryWealthList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试查看项目
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-23 15:46:16
	 */
	@Test
	public void testQueryWealthDetailById() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("wealthId", "1");
		ResultVo result = wealthInfoService.queryWealthDetailById(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}

	/**
	 * 测试新建/编辑项目
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-23 15:46:16
	 */
	@Test
	public void testSaveWealth() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("wealthId", "4031c45a-9e69-45f2-85ab-95ac17a06fa0");
		param.put("wealthTypeId", "1");
		param.put("planTotalAmount", "20000");
		param.put("investMinAmount", "1000");
		param.put("investMaxAmount", "10000");
		param.put("increaseAmount", "1000");
		param.put("releaseDate", "2016-02-23");
		param.put("effectDate", "2016-02-25");
		param.put("wealthStatus", "新建");
		param.put("wealthDescr", "desc");
		param.put("userId", "1");
		ResultVo result = wealthInfoService.saveWealth(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试用户计划投资列表
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-23 18:27:17
	 */
	@Test
	public void testQueryWealthJoinList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
//		param.put("custName", "");
//		param.put("investStatus", "");
//		param.put("lendingType", "");
//		param.put("typeTerm", "");
//		param.put("beginEffectDate", "");
//		param.put("endEffectDate", "");
//		param.put("beginEndDate", "");
//		param.put("endEndDate", "");
//		param.put("beginAtoneDate", "");
//		param.put("endAtoneDate", "");
		ResultVo result = wealthInfoService.queryWealthJoinList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试审核项目
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-24 13:58:08
	 */
	@Test
	public void testAuditWealth() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("wealthId", "4031c45a-9e69-45f2-85ab-95ac17a06fa0");
		param.put("auditStatus", "待发布");
		param.put("auditMemo", "111");
		param.put("userId", "1");
		ResultVo result = wealthInfoService.auditWealth(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试发布项目
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-24 13:58:08
	 */
	@Test
	public void testPublishWealth() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("wealthId", "4031c45a-9e69-45f2-85ab-95ac17a06fa0");
		param.put("userId", "1");
		ResultVo result = wealthInfoService.publishWealth(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试项目列表
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-25 10:53:19
	 */
	@Test
	public void testQueryAllWealthList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
//		param.put("lendingType", "1");
//		param.put("typeTerm", "");
//		param.put("wealthStatus", "");
//		param.put("beginReleaseDate", "");
//		param.put("endReleaseDate", "");
//		param.put("beginEffectDate", "");
//		param.put("endEffectDate", "");
//		param.put("beginEndDate", "");
//		param.put("endEndDate", "");
		ResultVo result = wealthInfoService.queryAllWealthList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试待匹配列表
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-25 11:27:50
	 */
	@Test
	public void testQueryWaitingMatchList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 1);
//		param.put("custName", "");
//		param.put("beginOperateDate", "");
		ResultVo result = wealthInfoService.queryWaitingMatchList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试债权预算
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-25 19:03:12
	 */
	@Test
	public void testQueryMatchLoanList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("currentDate", "");
		ResultVo result = wealthInfoService.queryMatchLoanList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试自动生效优选计划
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-26 18:00:57
	 */
	@Test
	public void testAutoReleaseWealth() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("wealthId", "ba440f33-5456-4161-bc37-a71e3821bd5a");
		ResultVo result = wealthInfoService.autoReleaseWealth(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}

	
	/**
	 * 测试优选计划列表（精选）
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-26 15:41:53
	 */
	@Test
	public void testQueryPriorityWealthList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("typeName", "");
		ResultVo result = wealthInfoService.queryPriorityWealthList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试查看优选计划
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-26 17:07:50
	 */
	@Test
	public void testQueryWealthDetail() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("wealthId", "15171fa3-b798-4929-86a3-817eb3607c4f");
//		param.put("custId", "");
		ResultVo result = wealthInfoService.queryWealthDetail(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	

	/**
	 * 测试马上投资
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-27 15:09:54
	 */
	@Test
	public void testJoinWealth() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("wealthId", "15171fa3-b798-4929-86a3-817eb3607c4f");
		param.put("custId", "20150427000000000000000000000000001");
		param.put("tradeAmount", "2000");
		param.put("channelNo", "1");
		ResultVo result = wealthInfoService.joinWealth(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试收益计算器
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-02-27 17:45:21
	 */
	@Test
	public void testCaclWealth() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("wealthId", "5899657c-b89f-4b9f-8467-20934d67bfd2");
		param.put("tradeAmount", "10000");
		ResultVo result = wealthInfoService.caclWealth(param);
		System.out.println(result);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 投资记录
	 * 
	 * @DATE 2016-02-27
	 * @author zhiwen_feng
	 * @param params
     *      <tt>start   :String:起始值</tt><br>
     *      <tt>length  :String:长度</tt><br>
     *      <tt>wealthId:String:项目ID</tt><br>
	 * @return ResultVo
     *      <tt>investDate  :String:购买时间</tt><br>
     *      <tt>investAmount:String:购买金额</tt><br>
     *      <tt>loginName   :String:用户名称</tt><br> 
	 * @throws SLException
	 */
	@Test
	public void testQueryWealthInvestList()throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 1);
		param.put("wealthId", "15171fa3-b798-4929-86a3-817eb3607c4f");
		
		ResultVo result = wealthInfoService.queryWealthInvestList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试提前赎回查看
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-03-02 14:53:06
	 */
	@Test
	public void testQueryAdvancedWealthAtoneDetail() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("wealthId", "13506fab-3250-49e8-90e6-3d4b066c0630");
		param.put("custId", "ada2861e-80aa-4665-b8ce-c092ff904769");
		ResultVo result = wealthInfoService.queryAdvancedWealthAtoneDetail(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试提前赎回
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-03-02 14:53:06
	 */
	@Test
	public void testAdvancedAtoneWealth() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("wealthId", "014876f3-e500-4411-97a0-b5f5650e0858");
		param.put("custId", "050264d5-baaf-4779-874f-66780c91d81b");
		param.put("tradePassword", "f735292e5d7e3a3a2d11d2f1085510ba");
		ResultVo result = wealthInfoService.advancedAtoneWealth(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试优选计划收益展示
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-03-03 12:05:22
	 */
	@Test
	public void testQueryProjectIncome() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "050264d5-baaf-4779-874f-66780c91d81b");
		ResultVo result = wealthInfoService.queryWealthTotalIncome(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试优选计划列表(投资人)
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-03-03 13:49:53
	 */
	@Test
	public void testQueryMyWealthList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		param.put("beginEffectDate", "");
		param.put("endEffectDate", "");
		param.put("investStatus", "");
		ResultVo result = wealthInfoService.queryMyWealthList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试优选计划查看
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-03-03 14:23:39
	 */
	@Test
	public void testQueryMyWealthDetail() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("wealthId", "15171fa3-b798-4929-86a3-817eb3607c4f");
		param.put("custId", "050264d5-baaf-4779-874f-66780c91d81b");
		ResultVo result = wealthInfoService.queryMyWealthDetail(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试我的债权列表
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-03-04 09:55:01
	 */
	@Test
	public void testQueryMyWealthLoan() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		param.put("wealthId", "5899657c-b89f-4b9f-8467-20934d67bfd2");
		param.put("custId", "b9b30ff8-1159-486d-9715-12fad4ae4e13");
		ResultVo result = wealthInfoService.queryMyWealthLoan(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}

	/**
	 * 测试数据汇总
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-03-04 13:36:00
	 */
	@Test
	public void testQueryMyWeathSummary() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "050264d5-baaf-4779-874f-66780c91d81b");
		ResultVo result = wealthInfoService.queryMyWealthIncome(param);
		System.out.println(result);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试查看协议
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-03-07 15:18:42
	 */
	@Test
	public void testQueryWealthContract() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("wealthId", "5899657c-b89f-4b9f-8467-20934d67bfd2");
		param.put("custId", "");
		ResultVo result = wealthInfoService.queryWealthContract(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试债权协议下载
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-03-07 16:30:01
	 */
	@Test
	public void testQueryWealthLoanContract() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("wealthId", "15171fa3-b798-4929-86a3-817eb3607c4f");
		param.put("custId", "20150427000000000000000000000000001");
		param.put("loanId", "cddddc3c-955d-44f5-bd13-86b7513e68eb");
		ResultVo result = wealthInfoService.queryWealthLoanContract(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试优选计划列表
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-03-07 20:15:17
	 */
	@Test
	public void testQueryShowWealthList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "10");
		ResultVo result = wealthInfoService.queryShowWealthList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 查询生效发短信需要信息
	 * 
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Test
	public void testQuerySendSmsEffectWealthInfo()throws SLException {
		Map<String, Object> params = Maps.newHashMap();
		params.put("wealthId", "8fb9b511-df00-4a90-9d71-675a64fccf76");
		List<Map<String, Object>> result = wealthInfoRepositoryCustom.querySendSmsEffectWealthInfo(params);
		assertNotNull(result);
	}
	
	@Test
	public void queryPriority()throws SLException{
		Map<String, Object> params = Maps.newHashMap();
		ResultVo result = wealthInfoService.queryPriority(params);
		System.out.println(result);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	@Test
	public void testQueryLoanInfoByWealthId()throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "10");
		param.put("wealthId", "68f63366-7bc5-47e0-94f4-888e1e521129");
		ResultVo result = wealthInfoService.queryLoanInfoByWealthId(param);
		System.out.println(result);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	@Test
	public void queryLoanInfoDetailByLoanId()throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "10");
		param.put("loanId", "6ece0318-53ef-4a8a-b902-e68e255f3f10");
		ResultVo result = wealthInfoService.queryLoanInfoDetailByLoanId(param);
		System.out.println(result);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}

}
