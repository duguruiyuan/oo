package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;



/**
 * ProjectService Test. @author Tools
 */
public class ProjectServiceTest extends AbstractSpringContextTestSupport  {


	@Autowired
	private ProjectService projectService;


	/**
	 * 测试我要投资-直投项目-融资租赁列表
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testQueryAllProjectList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		ResultVo result = projectService.queryAllProjectList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试我要投资-直投项目-融资租赁明细
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testQueryProjectDetail() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectId", "");
		ResultVo result = projectService.queryProjectDetail(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试我要投资-直投项目-加入记录
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testQueryProjectJoinList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "");
		param.put("length", "");
		param.put("projectId", "");
		ResultVo result = projectService.queryProjectJoinList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试我要投资-直投项目-融资租赁收益计算
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCaclProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("repaymentMethod", Constant.REPAYMENT_METHOD_04);
		param.put("typeTerm", "12");
		param.put("actualYearRate", "0.1512");
		param.put("yearRate", "0.1131");
		param.put("tradeAmount", "11549");
		ResultVo result = projectService.caclProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
		List<Map<String, Object>> planList = (List<Map<String, Object>>)((Map<String, Object>)result.getValue("data")).get("planList");
		for(Map<String, Object> m : planList) {
			System.out.println(String.format("期数:%4$7s 还款总额:%1$10s 本金:%2$10s 利息:%3$10s 账户管理费:%5$10s 本金+利息+账户管理费:%6$10s", 
					m.get("repaymentTotalAmount").toString(), 
					m.get("principal").toString(), 
					m.get("interest").toString(), 
					m.get("currentTerm").toString(),
					m.get("accountManageExpense").toString(),
					ArithUtil.add(ArithUtil.add(new BigDecimal(m.get("principal").toString()), new BigDecimal(m.get("interest").toString())), new BigDecimal(m.get("accountManageExpense").toString()))));
		}
		System.out.println(result);
	}


	/**
	 * 测试我要投资-直投项目-立即购买
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testJoinProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectId", "20160121140123");
		param.put("tradeAmount", "1000");
		param.put("custId", "20150427000000000000000000000000003");
		param.put("channelNo", "");
		param.put("meId", "");
		param.put("meVersion", "");
		param.put("appSource", "web");
		param.put("ipAddress", "");
		ResultVo result = projectService.joinProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
		
		param.put("projectId", "20160121140123");
		param.put("tradeAmount", "1000");
		param.put("custId", "20150427000000000000000000000000002");
		param.put("channelNo", "");
		param.put("meId", "");
		param.put("meVersion", "");
		param.put("appSource", "web");
		param.put("ipAddress", "");
		result = projectService.joinProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
		
		param.put("projectId", "20160121140123");
		param.put("tradeAmount", "1000");
		param.put("custId", "20150427000000000000000000000000001");
		param.put("channelNo", "");
		param.put("meId", "");
		param.put("meVersion", "");
		param.put("appSource", "web");
		param.put("ipAddress", "");
		result = projectService.joinProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试我的账户-账户总览-企业借款收益情况
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testQueryProjectIncome() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "");
		ResultVo result = projectService.queryProjectIncome(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试投资管理-直投列表-项目列表
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testQueryMyProjectList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "");
		param.put("length", "");
		param.put("custId", "");
		ResultVo result = projectService.queryMyProjectList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试投资管理-直投列表-还款明细
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testQueryProjectRepaymentList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectId", "");
		ResultVo result = projectService.queryProjectRepaymentList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试投资管理-直投列表-投资记录
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testQueryProjectInvestList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "");
		param.put("length", "");
		param.put("projectId", "");
		ResultVo result = projectService.queryProjectInvestList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试投资管理-直投列表-查看合同
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testQueryProjectContract() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectId", "");
		ResultVo result = projectService.queryProjectContract(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试项目管理-新建/编辑/审核项目-项目列表
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testQueryProjectList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "10");
		param.put("length", "10");
		param.put("projectType", "");
		param.put("companyName", "");
		param.put("projectNo", "");
		param.put("projectName", "");
		param.put("productTerm", "");
		param.put("repaymentMethod", "");
		param.put("projectStatus", "");
		param.put("beginReleaseDate", "");
		param.put("endReleaseDate", "");
		param.put("beginEffectDate", "");
		param.put("endEffectDate", "");
		param.put("beginProjectEndDate", "");
		param.put("endPojectEndDate", "");
		ResultVo result = projectService.queryProjectList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试项目管理-新建/编辑/审核项目-项目明细（查看项目、编辑时查看、审核时查看）
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testQueryProjectDetailById() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectId", "");
		ResultVo result = projectService.queryProjectDetailById(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试项目管理-新建/编辑/审核项目-暂存项目
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testApplyProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectType", "");
		param.put("projectNo", "");
		param.put("companyName", "");
		param.put("projectName", "");
		param.put("projectStatus", "");
		param.put("actualYearRate", "");
		param.put("yearRate", "");
		param.put("projectTotalAmount", "");
		param.put("typeTerm", "");
		param.put("seatTerm", "");
		param.put("investMinAmount", "");
		param.put("increaseAmount", "");
		param.put("releaseDate", "");
		param.put("effectDate", "");
		param.put("projectEndDate", "");
		param.put("repaymnetMethod", "");
		param.put("ensureMethod", "");
		param.put("isAtone", "");
		param.put("projectDescr", "");
		param.put("companyDescr", "");
		param.put("List", "");
		param.put("attachmentType", "");
		param.put("attachmentName", "");
		param.put("storagePath", "");
		param.put("userId", "");
		ResultVo result = projectService.applyProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试项目管理-新建/编辑/审核项目-提交项目（保存并审核）
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testSaveProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectType", "");
		param.put("projectNo", "");
		param.put("companyName", "");
		param.put("projectName", "");
		param.put("projectStatus", "");
		param.put("actualYearRate", "");
		param.put("yearRate", "");
		param.put("projectTotalAmount", "");
		param.put("typeTerm", "");
		param.put("seatTerm", "");
		param.put("investMinAmount", "");
		param.put("increaseAmount", "");
		param.put("releaseDate", "");
		param.put("effectDate", "");
		param.put("projectEndDate", "");
		param.put("repaymnetMethod", "");
		param.put("ensureMethod", "");
		param.put("isAtone", "");
		param.put("projectDescr", "");
		param.put("companyDescr", "");
		param.put("List", "");
		param.put("attachmentType", "");
		param.put("attachmentName", "");
		param.put("storagePath", "");
		param.put("userId", "");
		ResultVo result = projectService.saveProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试删除项目
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testDeleteProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectId", "");
		param.put("userId", "");
		ResultVo result = projectService.deleteProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试审核项目
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testAuditProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectId", "");
		param.put("auditStatus", "");
		param.put("auditMemo", "");
		param.put("userId", "");
		ResultVo result = projectService.auditProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试发布项目
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testPublishProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectId", "20160301150203");
		param.put("userId", "1");
		ResultVo result = projectService.publishProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试流标项目（未满标人工流标）
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testUnReleaseProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectId", "");
		param.put("userId", "");
		ResultVo result = projectService.unReleaseProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试发标项目（未满标人工发标）
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testReleaseProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectId", "23");
		param.put("userId", "1");
		ResultVo result = projectService.releaseProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试判断是否有相同期限的项目正在发布中
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-01-12 10:56:32
	 */
	@Test
	public void testJudgeProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("params", "");
		ResultVo result = projectService.judgeProject(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
}
