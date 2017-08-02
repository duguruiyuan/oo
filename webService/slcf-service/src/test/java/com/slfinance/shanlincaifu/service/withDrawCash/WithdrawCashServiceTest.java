/** 
 * @(#)WithdrawCashService.java 1.0.0 2015年4月27日 下午2:24:19  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.withDrawCash;

import java.util.Date;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.AbstractSpringContextTestSupport;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.service.WithDrawAuditService;
import com.slfinance.shanlincaifu.service.WithdrawCashService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;

/**   
 * 提现业务测试
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月27日 下午2:24:19 $ 
 */
@Slf4j
public class WithdrawCashServiceTest extends AbstractSpringContextTestSupport {

	@Autowired
	private WithdrawCashService withdrawCashService;
	
	@Autowired
	private WithDrawAuditService withDrawAuditService;
	
	Map<String,Object> paramsMap = Maps.newHashMap();
	
	@Autowired
	private SMSService smsService;
	
	/**
	 * 测试提现管理--列表(分页查询)
	 * @throws SLException
	 */
	@Test
	public void testFindAllWithdrawCashList() throws SLException{
		paramsMap.clear();
		paramsMap.put("pageNum", 0);
		paramsMap.put("pageSize", 12);
		paramsMap.put("nickName", "zhangzhisheng");
//		paramsMap.put("custName", "12");
//		paramsMap.put("tradeNo", "12");
//		paramsMap.put("tradeStatus", "12");
//		paramsMap.put("auditStatus", "12");
//		paramsMap.put("credentialsCode", "12");
//		paramsMap.put("startDate", "2013-09-09");
//		paramsMap.put("endDate", "2014-09-09");
//		paramsMap.put("applyType", "赎回活期宝");
		log.info(JSONObject.toJSONString(withdrawCashService.findAllWithdrawCashList(paramsMap)));
	}
	
	/**
	 * 测试提现管理--明细
	 * @throws SLException
	 */
	@Test
	public void testFindWithdrawalCashDetailInfo() throws SLException{
		paramsMap.clear();
		paramsMap.put("id", "2cd887d2-e8cf-4e6b-b34e-5a3cdee981c9");
		log.info(JSONObject.toJSONString(withdrawCashService.findWithdrawalCashDetailInfo(paramsMap)));
	}

	/**
	 *  测试提现管理--统计
	 */
	@Test
	public void testFindAllWithdrawCashSum() throws SLException {
		paramsMap.clear();
//		paramsMap.put("custName", "12");
//		paramsMap.put("nickName", "12");
//		paramsMap.put("tradeNo", "12");
//		paramsMap.put("tradeStatus", "12");
//		paramsMap.put("auditStatus", "12");
//		paramsMap.put("credentialsCode", "12");
//		paramsMap.put("startDate", "2013-09-09");
//		paramsMap.put("endDate", "2014-09-09");
		log.info(JSONObject.toJSONString(withdrawCashService.findAllWithdrawCashSum(paramsMap)));
	}
	
	/**
	 * 测试提现管理--明细-审核记录
	 */
	@Test
	public void testFindAuditLogByWithDraw() throws SLException {
		paramsMap.clear();
//		paramsMap.put("id", "zhangzsceshi");
		paramsMap.put("custId", "zhangzsceshi");
		paramsMap.put("applyType", "赎回活期宝");
		withdrawCashService.findAuditLogByWithDraw(paramsMap);
	}
	
	/**
	 * 测试提现管理--提现审核查询-用户历史提现记录
	 */
	@Test
	public void testFindWithdrawCashAuditList() throws SLException {
		paramsMap.clear();
		paramsMap.put("custId", "zhangzs");
		System.out.println(withdrawCashService.findWithdrawCashAuditList(paramsMap));
	}
	
	/**
	 * 测试提现管理--提现审核查询-用户历史提现记录分页
	 */
	@Test
	public void testFindPageWithdrawCashAuditList() throws SLException {
		paramsMap.clear();
		paramsMap.put("custId", "zhangzs");
		paramsMap.put("start", "0");
		paramsMap.put("length", "10");
		withdrawCashService.findWithdrawCashAuditListPage(paramsMap);
	}

	/**
	 * 测试提现管理--提现审核提交
	 * @throws InterruptedException 
	 */
	@Test
	public void testSaveWithdrawCashAudit() throws SLException, InterruptedException{
		
		Thread threadl = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					paramsMap.clear();
					paramsMap.put("id", "7f7a7c05-13b6-4d46-aafa-1c81ab6a0e4f");
					paramsMap.put("auditStatus", "通过");
					paramsMap.put("memo", "你的说法都是");
					paramsMap.put("custId", "zhangzs");
					paramsMap.put("version", "0");
					withDrawAuditService.saveWithdrawCashAudit(paramsMap);
				} catch (Exception e) {
					
				} finally {
					
				}
			}
		});
		
		
		Thread thread2 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					paramsMap.clear();
					paramsMap.put("id", "7f7a7c05-13b6-4d46-aafa-1c81ab6a0e4f");
					paramsMap.put("auditStatus", "通过");
					paramsMap.put("memo", "你的说法都是");
					paramsMap.put("custId", "zhangzs");
					paramsMap.put("version", "0");
					withDrawAuditService.saveWithdrawCashAudit(paramsMap);
				} catch (Exception e) {
					
				} finally {
					
				}
			}
		});
		
		threadl.start();
		thread2.start();
		
		threadl.join();
		thread2.join();
		
	}
	
	
	/**
	 * 提现回调成功处理业务
	 */
	@Test
	public void testCallbackWithdrawCash() throws SLException{
		paramsMap.clear();
		paramsMap.put("tradeNo", "BAO-TRADE-1000000000747");
		paramsMap.put("tradeCode", "000000");
		paramsMap.put("status", Constant.TRADE_STATUS_03);
		paramsMap.put("auditStatus", Constant.AUDIT_STATUS_PASS);
		paramsMap.put("tradeStatus", Constant.TRADE_STATUS_03);
			//第三方处理成功业务处理
		withdrawCashService.callbackWithdrawCash(paramsMap);
	}

	/**
	 * 提现回调失败处理业务
	 */
	@Test
	public void callbackWithdrawCashFailed()throws SLException{
		paramsMap.clear();
		paramsMap.put("tradeNo", "BAO-TRADE-1000000000747");
		paramsMap.put("tradeCode", "000000");
		paramsMap.put("status", Constant.TRADE_STATUS_03);
		paramsMap.put("auditStatus", Constant.AUDIT_STATUS_PASS);
		paramsMap.put("tradeStatus", Constant.TRADE_STATUS_03);
		//第三方处理成功业务处理
		withdrawCashService.callbackWithdrawCashFailed(paramsMap);
	}
	
	/**
	 * 测试发送审核通过或者拒绝发送短信
	 */
	@Test
	public void testSendSms() {
		Map<String,Object> mailParams = Maps.newHashMap();
		mailParams.put("custId", "1");
		mailParams.put("mobile", "15001990514");
		//拒绝Constant.SMS_TYPE_WITHDRAW_FAIL
		mailParams.put("messageType", Constant.SMS_TYPE_WITHDRAW_SUCCESS);
		mailParams.put("values", new String[]{DateUtils.formatDate(new Date(), "yyyy-MM-dd hh:mm:ss"),"200"});
		//做异步处理
		smsService.sendSMS(mailParams);
	}
	
}
