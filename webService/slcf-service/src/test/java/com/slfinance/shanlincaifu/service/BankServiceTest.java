/** 
 * @(#)BankServiceTest.java 1.0.0 2015年7月9日 下午3:27:39  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**   
 * 测试银行卡服务
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月9日 下午3:27:39 $ 
 */
public class BankServiceTest extends AbstractSpringContextTestSupport{

	@Autowired
	BankCardService bankCardService;
	
	/**
	 * 测试解约银行卡
	 *
	 * @author  wangjf
	 * @date    2015年7月9日 下午3:41:00
	 * @throws SLException
	 */
	@Test
	public void testUnBindBankCard() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		List<Map<String, String>> attachmentList = new ArrayList<Map<String, String>>();
		
		Map<String, String> map01 = new HashMap<String, String>();
		map01.put("attachmentType", Constant.UNBIND_CARD_ATTACHMENT_TYPE_BANK_LOST);
		map01.put("attachmentName", "银行卡丢失证明");
		map01.put("storagePath", "/tmp");
		map01.put("docType", Constant.UNBIND_CARD_ATTACHMENT_DOC_TYPE_PIG);
		
		Map<String, String> map02 = new HashMap<String, String>();
		map02.put("attachmentType", Constant.UNBIND_CARD_ATTACHMENT_TYPE_PAPER_BEFORE);
		map02.put("attachmentName", "手持身份证正面");
		map02.put("storagePath", "/tmp");
		map02.put("docType", Constant.UNBIND_CARD_ATTACHMENT_DOC_TYPE_PIG);
		
		Map<String, String> map03 = new HashMap<String, String>();
		map03.put("attachmentType", Constant.UNBIND_CARD_ATTACHMENT_TYPE_PAPER_BEHIND);
		map03.put("attachmentName", "手持身份证反面");
		map03.put("storagePath", "/tmp");
		map03.put("docType", Constant.UNBIND_CARD_ATTACHMENT_DOC_TYPE_PIG);
		
		attachmentList.add(map01);
		attachmentList.add(map02);
		attachmentList.add(map03);
		
		param.put("custId", "20150427000000000000000000000000002");
		param.put("bankId", "2cb51c15-0af1-465f-9b51-d30b734dfc36");
		param.put("tradePassword", "f735292e5d7e3a3a2d11d2f1085510ba");
		param.put("unbindType", Constant.UNBIND_CARD_TYPE_LOST);
		param.put("unbindReason", "卡片丢失");
		param.put("attachmentList", attachmentList);
		
		ResultVo result = bankCardService.unBindBankCard(param);
		System.out.println(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 查询解绑银行卡
	 *
	 * @author  wangjf
	 * @date    2015年7月9日 下午4:08:41
	 * @throws SLException
	 */
	@Test
	public void testQueryUnBindCard() throws SLException {
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("start", 0);
		params.put("length", 10);
		params.put("custId", "20150427000000000000000000000000002");
		Map<String, Object> resultMap = bankCardService.queryUnBindCard(params);
		System.out.println(resultMap);
		assertNotNull(resultMap);
		assertEquals(resultMap.size() != 0, true);
	}
	
	/**
	 * 根据申请ID查询解绑信息
	 *
	 * @author  wangjf
	 * @date    2015年7月9日 下午4:18:19
	 * @throws SLException
	 */
	@Test
	public void testQueryUnBindCardById() throws SLException {
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("unbindId", "77f5d27c-a5be-435e-bb06-67cacfcd8694");
		Map<String, Object> resultMap = bankCardService.queryUnBindCardById(params);
		System.out.println(resultMap);
		assertNotNull(resultMap);
		assertEquals(resultMap.size() != 0, true);
	}
	
	/**
	 * 测试查询所有解绑信息
	 *
	 * @author  wangjf
	 * @date    2015年7月14日 下午4:33:36
	 * @throws SLException
	 */
	@Test
	public void testQueryAllUnBindCard() throws SLException {
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("start", 0);
		params.put("length", 10);
		Map<String, Object> resultMap = bankCardService.queryAllUnBindCard(params);
		System.out.println(resultMap);
		assertNotNull(resultMap);
		assertEquals(resultMap.size() != 0, true);
	}
	
	/**
	 * 测试查询审核用的解绑信息
	 *
	 * @author  wangjf
	 * @date    2015年7月14日 下午4:35:24
	 * @throws SLException
	 */
	@Test
	public void testQueryAuditUnBindCardById() throws SLException {
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("unbindId", "77f5d27c-a5be-435e-bb06-67cacfcd8694");
		Map<String, Object> resultMap = bankCardService.queryAuditUnBindCardById(params);
		System.out.println(resultMap);
		assertNotNull(resultMap);
		assertEquals(resultMap.size() != 0, true);
	}
	
	/**
	 * 测试审核解绑银行卡
	 *
	 * @author  wangjf
	 * @date    2015年7月14日 下午4:37:25
	 * @throws SLException
	 */
	@Test
	public void testAuditUnBindCard()  throws SLException {
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("auditId", "5e6ebe6c-47e4-4edc-be58-10ecd6b60eb4");
		params.put("auditUserId", "1");
		params.put("auditStatus", Constant.AUDIT_STATUS_PASS);
		params.put("auditMemo", "通过");
		ResultVo result = bankCardService.preauditUnBindCard(params);
		result = bankCardService.auditUnBindCard(params);
		result = bankCardService.postauditUnBindCard(result);
		System.out.println(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 查询银行卡管理列表
	 *
	 * @author  wangjf
	 * @date    2015年7月16日 下午3:32:57
	 * @throws SLException
	 */
	@Test
	public void testQueryBankManager() throws SLException {
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("start", 0);
		params.put("length", 10);
		params.put("custId", "20150427000000000000000000000000002");
		Map<String, Object> resultMap = bankCardService.queryBankManager(params);
		System.out.println(resultMap);
		assertNotNull(resultMap);
		assertEquals(resultMap.size() != 0, true);
	}
	
	/**
	 * 测试银行卡补单
	 *
	 * @author  wangjf
	 * @date    2015年8月6日 下午1:39:11
	 * @throws SLException
	 */
	@Test
	public void testMendBankCard() throws SLException {
		bankCardService.mendBankCard();
	}

	@Test
	public void testQueryThirdBankByCardNo() throws SLException {
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("bankCardNo", "6226090210001570");
		Map<String, Object> resultMap = bankCardService.queryThirdBankByCardNo(params);
		System.out.println(resultMap);
		assertNotNull(resultMap);
		assertEquals(resultMap.size() != 0, true);
	}
	
	/** 线下业务-附属银行卡-客户银行卡审核列表 */
	@Test
	public void queryWealthBankList() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", "0");
		param.put("length", "30");
		param.put("custName", "dsfa");
		param.put("credentialsCode", "gag");
		param.put("mobile", "gaf");
		param.put("auditStatus", "gdasfg");
		param.put("bankCardNo", "dasfd");

		ResultVo resultVo = bankCardService.queryWealthBankList(param);
		assertNotNull(resultVo);
	}
	
	/** 线下业务-附属银行卡-客户银行卡查看详情 */
	@Test
	public void queryWealthBankDetailById() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("tradeFlowId", "d2c8b86b-17ff-4565-9569-aee8af8debaf");

		ResultVo resultVo = bankCardService.queryWealthBankDetailById(param);
		assertNotNull(resultVo);
	}
	
	/** 线下业务-附属银行卡-客户银行卡查看详情 
	 * @throws SLException */
	@Test
	public void auditWealthBank() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("tradeFlowId", "test-tradeId-lyy001");
		
		param.put("auditStatus", "审核回退");
		param.put("auditMemo", "呵呵");
		param.put("userId", "999");

		ResultVo resultVo = bankCardService.auditWealthBank(param);
		assertNotNull(resultVo);
	}
	
	@Test
	public void queryCanShowCustBank()throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "123456");
		
		ResultVo resultVo = bankCardService.queryCanShowCustBank(param);
		assertNotNull(resultVo);
	}
}
