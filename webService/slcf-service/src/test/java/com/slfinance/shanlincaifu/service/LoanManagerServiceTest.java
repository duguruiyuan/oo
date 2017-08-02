package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;




/**
 * LoanManagerService Test. @author Tools
 */
public class LoanManagerServiceTest extends AbstractSpringContextTestSupport  {


	@Autowired
	private LoanManagerService loanManagerService;


	/**
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-11-29 11:29:20
	 */
	@Test
	public void testQueryBusinessManageList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "10");
//		param.put("loanCode", "");
//		param.put("custName", "");
//		param.put("loanType", "");
//		param.put("loanStatus", "");
//		param.put("loanTerm", "");
//		param.put("repaymentMethod", "");
//		param.put("publishDateStart", "");
//		param.put("publishDateEnd", "");
		ResultVo result = loanManagerService.queryBusinessManageList(param);
		assertNotNull(result);
	}
	

	/**
	 * 测试我的投资总览查询
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-11-29 15:53:41
	 */
	@Test
	public void testQueryMyTotalInvest() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "");
		ResultVo result = loanManagerService.queryMyTotalInvest(param);
		assertNotNull(result);
	}


	/**
	 * 测试债权收益查询
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-11-29 15:53:41
	 */
	@Test
	public void testQueryMyCreditIncome() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "");
		ResultVo result = loanManagerService.queryMyCreditIncome(param);
		assertNotNull(result);
	}

	/**
	 * 测试散标投资列表查询
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-11-29 19:44:08
	 */
	@Test
	public void testQueryMyDisperseList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("custId", "");
//		param.put("disperseStatus", "");
		param.put("start", "0");
		param.put("length", "10");
		ResultVo result = loanManagerService.queryMyDisperseList(param);
		assertNotNull(result);
	}


	/**
	 * 测试投资详情
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-11-29 19:44:08
	 */
	@Test
	public void testQueryMyDisperseDetail() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("disperseId", "1");
		param.put("custId", "1");
		ResultVo result = loanManagerService.queryMyDisperseDetail(param);
		assertNotNull(result);
	}


	/**
	 * 测试回款计划
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-11-29 19:44:08
	 */
	@Test
	public void testQueryMyDispersePaybackPlan() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("disperseId", "1");
		param.put("custId", "1");
		param.put("start", "0");
		param.put("length", "10");
		ResultVo result = loanManagerService.queryMyDispersePaybackPlan(param);
		assertNotNull(result);
	}


	/**
	 * 测试发布
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-11-29 19:44:08
	 */
	@Test
	public void testPublishLoanInfo() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("LoadIds", "");
		param.put("loanId", "");
		ResultVo result = loanManagerService.publishLoanInfo(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试强制流标
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-11-29 19:44:08
	 */
	@Test
	public void testForcebidders() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("params", "");
		ResultVo result = loanManagerService.forcebidders(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}

	/**
	 * 测试审核
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-12-01 12:27:31
	 */
	@Test
	public void testAuditLoan() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("loanId", "d3763df5-b418-4f53-b27e-ba04682a4988");
		param.put("userId", "1");
		param.put("auditStatus", Constant.AUDIT_STATUS_PASS);
		param.put("aduitMemo", "通过");
		ResultVo result = loanManagerService.auditLoan(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}

	/**
	 * 测试购买散标投资ByApp
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-12-01 12:27:31
	 */
	@Test
	public void testBuyDispersion() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("disperseId", "d3763df5-b418-4f53-b27e-ba04682a4988");
		param.put("custId", "20150427000000000000000000000000001");
		param.put("tradeAmount", "1000");
		param.put("channelNo", "");
		param.put("meId", "");
		param.put("meVersion", "");
		param.put("appSource", "");
		param.put("ipAddress", "");
		ResultVo result = loanManagerService.buyDispersion(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	@Test
	public void batchLending() throws SLException{
		Map<String, Object> params = Maps.newHashMap();
		List<String> loanIds = Lists.newArrayList();
		loanIds.add("d3763df5-b418-4f53-b27e-ba04682a4988");
		params.put("loanIds", loanIds);
		
		ResultVo result = loanManagerService.batchLending(params);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	@Test
	public void queryMyDisperseIncome()throws SLException{
		Map<String, Object> params = Maps.newHashMap();
		params.put("custId", "dasd");
		ResultVo result = loanManagerService.queryMyDisperseIncome(params);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
}
