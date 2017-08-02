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
import com.slfinance.vo.ResultVo;



/**
 * ProjectRepaymentService Test. @author Tools
 */
public class ProjectRepaymentServiceTest extends AbstractSpringContextTestSupport  {


	@Autowired
	private ProjectRepaymentService projectRepaymentService;

	/**
	 * 测试近期应还数据列表
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:37
	 */
	@Test
	public void testQueryLatestRepaymentList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "10");
//		param.put("projectType", "融资租赁");
		param.put("companyName", "商户x01");
//		param.put("projectNo", "20160116162628");
//		param.put("beginExceptDate", "2016-01-22");
//		param.put("endExceptDate", "2016-02-05");
		ResultVo result = projectRepaymentService.queryLatestRepaymentList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试应还总额、已还总额、公司可用余额查询
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:37
	 */
	@Test
	public void testQueryLatesttRepaymentTotal() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("projectType", "融资租赁");
//		param.put("companyName", "星火融资");
//		param.put("projectNo", "20160116162628");
		param.put("beginExceptDate", "2016-01-22");
		param.put("endExceptDate", "2016-02-05");
		ResultVo result = projectRepaymentService.queryLatesttRepaymentTotal(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试批量还款
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:37
	 */
	@Test
	public void testMultiRepayment() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		List<Map<String, Object>> list = Lists.newArrayList();
		Map<String, Object> m = Maps.newHashMap();
		m.put("replaymentPlanId", "9c21152d-068b-4b36-b489-2dc579d6e148");
		list.add(m);
		param.put("replaymentPlanList", list);
		param.put("userId", "1");
		ResultVo result = projectRepaymentService.multiRepayment(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试单笔还款
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:37
	 */
	@Test
	public void testSingleRepayment() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("replaymentPlanId", "9c21152d-068b-4b36-b489-2dc579d6e148");
		param.put("userId", "1");
		ResultVo result = projectRepaymentService.singleRepayment(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试逾期中数据列表
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:37
	 */
	@Test
	public void testQueryOverdueRepaymentList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "10");
		param.put("projectType", "融资租赁");
		param.put("companyName", "星火融资");
		param.put("projectNo", "20160116162628");
		ResultVo result = projectRepaymentService.queryOverdueRepaymentList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试应还总额、已还总额、公司可用余额、逾期总额查询
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:37
	 */
	@Test
	public void testQueryOverdueRepaymentTotal() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectType", "融资租赁");
		param.put("companyName", "星火融资");
		param.put("projectNo", "20160116162628");
		ResultVo result = projectRepaymentService.queryOverdueRepaymentTotal(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试逾期费用计算（确认框中费用）
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:37
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testCaclOverdueRepayment() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("replaymentPlanId", "");
		ResultVo result = projectRepaymentService.caclOverdueRepayment(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试逾期还款
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:37
	 */
	@Test
	public void testOverdueRepayment() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectId", "20160116162628");
		param.put("userId", "1");
		ResultVo result = projectRepaymentService.overdueRepayment(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试还款数据列表
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:37
	 */
	@Test
	public void testQueryAllRepaymentList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "10");
//		param.put("projectType", "融资租赁");
//		param.put("companyName", "星火融资");
//		param.put("projectNo", "20160116162628");
//		param.put("productTerm", "3");
//		param.put("repaymentMethod", "等额本息");
		param.put("projectStatus", "");
		param.put("beginReleaseDate", "");
		param.put("endReleaseDate", "");
		param.put("beginProjectEndDate", "");
		param.put("endPojectEndDate", "");
		param.put("beginRepaymentDate", "");
		param.put("endRepaymentDate", "");
		ResultVo result = projectRepaymentService.queryAllRepaymentList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试已投总额、剩余本金总额、本期应还总额、提前结清手续费总额
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:37
	 */
	@Test
	public void testQueryAllRepaymentTotal() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectType", "融资租赁");
		param.put("companyName", "星火融资");
		param.put("projectNo", "20160116162628");
		param.put("productTerm", "");
		param.put("repaymentMethod", "");
		param.put("projectStatus", "");
		param.put("beginReleaseDate", "");
		param.put("endReleaseDate", "");
		param.put("beginProjectEndDate", "");
		param.put("endPojectEndDate", "");
		param.put("beginRepaymentDate", "");
		param.put("endRepaymentDate", "");
		ResultVo result = projectRepaymentService.queryAllRepaymentTotal(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试提前结清费用计算（确认框中费用）
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:37
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testCaclEarlyRepayment() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectId", "");
		ResultVo result = projectRepaymentService.caclEarlyRepayment(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试提前结清还款
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:37
	 */
	@Test
	public void testEarlyRepayment() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectId", "20160120160948");
		param.put("userId", "1");
		ResultVo result = projectRepaymentService.earlyRepayment(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试正常还款
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:37
	 */
	@Test
	public void testNormalRepayment() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("replaymentPlanId", "bd99e96d-cacd-43d7-8026-c05b95dc6aa1");
		param.put("userId", "1");
		ResultVo result = projectRepaymentService.normalRepayment(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试风险金垫付
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:37
	 */
	@Test
	public void testRiskRepayment() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("replaymentPlanId", "");
		param.put("userId", "");
		ResultVo result = projectRepaymentService.riskRepayment(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


}
