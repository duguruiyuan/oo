/** 
 * @(#)TermServiceTest.java 1.0.0 2015年8月17日 下午4:55:12  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**   
 * 定期宝服务测试
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月17日 下午4:55:12 $ 
 */
public class TermServiceTest extends AbstractSpringContextTestSupport{

	@Autowired
	private TermService termService;
	
	@Autowired
	private SMSService smsService;
	
	/**
	 * 测试购买定期宝
	 *
	 * @author  wangjf
	 * @throws SLException 
	 * @date    2015年8月17日 下午4:56:42
	 */
	@Test
	public void testJoinTermBao() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("productId", "3");
		params.put("custId", "20150427000000000000000000000000001");
		params.put("tradeAmount", "1000");
		params.put("appSource", "web");
		
		ResultVo resultVo = termService.joinTermBao(params);
		assertEquals(ResultVo.isSuccess(resultVo), true);
	}
	
	/**
	 * 测试购买定期宝
	 *
	 * @author  wangjf
	 * @throws SLException 
	 * @date    2015年8月17日 下午4:56:42
	 */
	@Test
	public void testJoinTermBao2() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("productId", "4");
		params.put("custId", "20150427000000000000000000000000001");
		params.put("tradeAmount", "100");
		
		ResultVo resultVo = termService.joinTermBao(params);
		assertEquals(ResultVo.isSuccess(resultVo), true);
	}
	
	/**
	 * 测试提前赎回
	 *
	 * @author  wangjf
	 * @date    2015年8月17日 下午5:33:15
	 * @throws SLException
	 */
	@Test
	public void testTermWithdrawApply() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("investId", "f76ff746-2bca-4e97-b930-ea254b6c9b11");
		params.put("custId", "20150427000000000000000000000000001");
		params.put("tradePassword", "440220aa487a592881324212d7c9ef6f");
		
		ResultVo resultVo = termService.termWithdrawApply(params);
		assertEquals(ResultVo.isSuccess(resultVo), true);
	}
	
	/**
	 * 测试到期赎回
	 *
	 * @author  wangjf
	 * @date    2015年8月17日 下午6:20:52
	 * @throws SLException
	 */
	@Test
	public void testTermAtoneWithdraw() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		ResultVo resultVo = termService.termAtoneWithdraw(params);
		assertEquals(ResultVo.isSuccess(resultVo), true);
	}
	
	/**
	 * 公司回购
	 *
	 * @author  wangjf
	 * @date    2015年8月17日 下午6:30:28
	 * @throws SLException
	 */
	@Test
	public void testTermAtoneBuy() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		ResultVo resultVo = termService.termAtoneBuy(params);
		assertEquals(ResultVo.isSuccess(resultVo), true);
	}
	
	/**
	 * 测试每日结息
	 *
	 * @author  wangjf
	 * @date    2015年8月17日 下午7:07:54
	 * @throws SLException
	 */
	@Test
	public void testTermDailySettlement() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		ResultVo resultVo = termService.termDailySettlement(params);
		assertEquals(ResultVo.isSuccess(resultVo), true);
	}
	
	/**
	 * 赎回到帐
	 *
	 * @author  wangjf
	 * @date    2015年8月17日 下午7:15:07
	 * @throws SLException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testTermAtoneSettlement() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		ResultVo resultVo = termService.termAtoneSettlement(params);
		if(ResultVo.isSuccess(resultVo)) {
			List<Map<String, Object>> smsList = (List<Map<String, Object>>)resultVo.getValue("data");
			for(Map<String, Object> sms : smsList) {
				if(Constant.ATONE_METHOD_EXPIRE.equals((String)sms.get("atoneMethod"))) { // 到期赎回
					smsService.asnySendSMS(sms);
				}
			}
		}
		assertEquals(ResultVo.isSuccess(resultVo), true);
	}
	
	/**
	 * 定期宝详情
	 *
	 * @author  wangjf
	 * @date    2015年8月18日 下午2:49:59
	 */
	@Test
	public void findBAODetailTest(){
		Map<String, Object> params=new HashMap<>();
		//params.put("productId", "4");
		Map<String,Object> rtnMap=termService.findTermBAODetail(params);
		System.out.println(rtnMap==null);
	}
	
	/**
	 * 测试查询提前赎回金额
	 *
	 * @author  wangjf
	 * @date    2015年8月20日 上午10:58:41
	 */
	@Test
	public void testFindAdvancedAtone() {
		Map<String, Object> params=new HashMap<>();
		params.put("investId", "2af400d8-f5fc-4023-a983-7abe63acb79f");
		Map<String,Object> rtnMap = termService.findAdvancedAtone(params);
		System.out.println(rtnMap==null);
	}
	
//	public static void main(String[] args) {
//		BigDecimal investAmount = new BigDecimal("3000");
//		BigDecimal yearRate = new BigDecimal("0.028");
//		BigDecimal month = new BigDecimal("12");
//		BigDecimal typeTerm = new BigDecimal("3");
//		
//		BigDecimal exceptInterest = ArithUtil.mul(ArithUtil.mul(investAmount, ArithUtil.div(yearRate, new BigDecimal("12"))), typeTerm);		
//		System.out.println(exceptInterest);
//		exceptInterest = investAmount.multiply(yearRate).divide(month).multiply(typeTerm);
//		System.out.println(exceptInterest);
//	}
}
