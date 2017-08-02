/** 
 * @(#)TestController.java 1.0.0 2014年12月1日 下午8:38:05  
 *  
 * Copyright © 2014 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.controller;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.LoanAgreeEntity;
import com.slfinance.shanlincaifu.job.ActivityId13Job;
import com.slfinance.shanlincaifu.job.AutoReserveInvestJob;
import com.slfinance.shanlincaifu.job.DailyDataSummaryJob;
import com.slfinance.shanlincaifu.job.ExperienceDailySettlementJob;
import com.slfinance.shanlincaifu.service.*;
import com.slfinance.shanlincaifu.service.impl.FinancialStatisticsService;
import com.slfinance.shanlincaifu.service.impl.RefereeAuditService;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SharedUtil;
import com.slfinance.vo.ResultVo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping("job")
public class TestController {

	@Autowired
	private RepaymentService repaymentService;

	@Autowired
	private ProductBusinessService productBusinessService;

	@Autowired
	private ProductService productService;

	@Autowired
	private InvestService investService;

	@Autowired
	ExperienceDailySettlementJob tYBDailySettlementJob;

	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private OpenNotifyService openNotifyService;
	
	@Autowired
	private BankCardService bankCardService; 
	
	@Autowired
	private FinancialStatisticsService financialStatisticsService;
	
	private Lock lock=new ReentrantLock();
	
	@Autowired
	private TermService termService;
	
	@Autowired
	private GoldService goldService;
	
	@Autowired
	private SummarizationBusinessHistoryService summarizationBusinessHistoryService;
	
	@Autowired
	private SMSService smsService;
	
	@Autowired
	private ProjectJobService projectJobService;
	
	@Autowired
	private WealthJobService wealthJobService;
	
	@Autowired
	private AllotService allotService;
	
	@Autowired
	private EmployeeLoadService employeeLoadService;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	OfflineWealthService offlineWealthService;
	
	@Autowired
	RefereeAuditService refereeAuditService;
	
	@Autowired
	LoanJobService loanJobService;
	
	@Autowired
	AutoInvestJobService autoInvestJobService;
	
	@Autowired
	BizExtractData bizExtractData;
	
	@Autowired
	SendRepaymentService sendRepaymentService;
	@Autowired
	AutoPublishJobService autoPublishJobService;
	
	@Autowired
	DailyDataSummaryJob dailyDataSummaryJob;
	
	@Autowired
	CalcCommService calcCommService;
	
	@Autowired
	LoanManagerService loanManagerService; 
	@Autowired
	private PlatformTradeDataService platformTradeDataService;
	@Autowired 
	AutoReserveInvestJob autoReserveInvestJob;
	@Autowired
	private AutoExpireService autoExpireService;

	@Autowired
	private CustActivityInfoService custActivityInfoService;

	/**
	 * 发送手动还款请求<br>
	 */
	@ResponseBody
	@RequestMapping("sendRepayment")
	public ResultVo testSendRepayment() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			sendRepaymentService.sendRepayment(param);
			resultVo = new ResultVo(true);
		} catch (Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	/**
	 * 自动发布<br>
	 */
	@ResponseBody
	@RequestMapping("autoPublishJobService")
	public ResultVo testautoPublish() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			autoPublishJobService.autoPublish(param);
			resultVo = new ResultVo(true);
		} catch (Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	/**
	 * 邮件发送平台每日邮件汇总<br>
	 */
	@ResponseBody
	@RequestMapping("dailyDataSummary")
	public ResultVo testDailyDataSummary() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			bizExtractData.dailyDataSummary(param);
//			dailyDataSummaryJob.invoke();
			resultVo = new ResultVo(true);
		} catch (Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	/**
	 * 邮件发送平台推送每日邮件汇总<br>
	 */
	@ResponseBody
	@RequestMapping("dailyPushDataSummary")
	public ResultVo testdailyPushDataSummaryJob() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			bizExtractData.dailyDataPropellingSummary(param);
			resultVo = new ResultVo(true);
		} catch (Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("autoInvest")
	public ResultVo testAutoInvest() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			autoInvestJobService.autoInvest(param);
			resultVo = new ResultVo(true);
		} catch (Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	@ResponseBody
	@RequestMapping("autoTransfer")
	public ResultVo testAutoTransfer() {
		ResultVo resultVo=null;
		try {
			Map<String, Object> params=new HashMap<String, Object>();
			autoInvestJobService.autoTransfer(params);
		} catch (Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		    resultVo=new ResultVo(true) ;
		return resultVo;
	}

	@ResponseBody
	@RequestMapping("AtoneJob")
	public ResultVo testContendTender() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			investService.fullAtoneDetail(param);
			resultVo = new ResultVo(true);
		} catch (Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}

	@ResponseBody
	@RequestMapping("CloseJob")
	public ResultVo testCreateCouponOffer() {
//		ResultVo resultVo = null;
//		int attemptTimes = 0;
//		while( attemptTimes ++ < 3)
//		{
//			try {
//				Map<String, Object> param = new HashMap<String, Object>();
//				// 定时关标
//				productService.closeJob(param);
//				// 恢复可提现额度
//				productService.recoverAtoneLimited(param);
//				// 定期宝关标
//				productService.closeTermJob(param);
//				
//				resultVo = new ResultVo(true);
//				break;
//			} catch (Exception e) {
//				resultVo = new ResultVo(false, e.getMessage());
//				try {
//					Thread.sleep(300); // 防止操作过快，若发生异常等待300毫秒再处理
//				} catch (InterruptedException ie) {
//					resultVo = new ResultVo(false, ie.getMessage());
//				}
//			}
//			// 失败次数达到3次
//			if(attemptTimes == 3) {
//				resultVo = new ResultVo(false, "关标失败！尝试次数达到最大次数");
//			}
//		}
		
		ResultVo resultVo = null;
		try
		{
			Map<String, Object> param = new HashMap<String, Object>();
			// 定时关标
			productService.closeJob(param);
			// 恢复可提现额度
			productService.recoverAtoneLimited(param);
			// 定期宝关标
			productService.closeTermJob(param);
			resultVo = new ResultVo(true);
			
		}
		catch(Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		
		return resultVo;
	}

	@ResponseBody
	@RequestMapping("ComputeLoanPvJob")
	public ResultVo testCreatePaymentOffer() {
		ResultVo resultVo = null;
		try {
			lock.lock();
			loanInfoService.execLoanPv();
			// 将债权分配给居间人
			Map<String, Object> params = Maps.newHashMap();
			allotService.autoAllotWealth(params);	
			resultVo = new ResultVo(true);
		} catch (Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}finally{
			lock.unlock();
		}
		return resultVo;
	}

	@ResponseBody
	@RequestMapping("ComputeOpenValueJob")
	public ResultVo testSendOfferRecord() {
		ResultVo resultVo = null;
		System.out.println("sendOfferRecord");
		try {
			productBusinessService.computeOpenValue();
			resultVo = new ResultVo(true);
		} catch (Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}

	@ResponseBody
	@RequestMapping("DailySettlementJob")
	public ResultVo testHandleReturnOffer() {
		ResultVo resultVo = null;
		System.out.println("handleReturnOffer");
		try {
			productService.currentDailySettlement(new Date());
			resultVo = new ResultVo(true);
		} catch (Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}

	@ResponseBody
	@RequestMapping("OpenJob")
	public ResultVo testCreateRiskGoldAdvanceOffer() {
		ResultVo resultVo = null;
		try {
			productBusinessService.releaseBid();
			resultVo = new ResultVo(true);
		} catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}

	@ResponseBody
	@RequestMapping("RepaymentJob")
	public ResultVo testCreateRepaymentOffer() {
		ResultVo resultVo = null;
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("expectRepaymentDate",
				DateUtils.formatDate(new Date(), "yyyyMMdd"));
		try {
			resultVo = repaymentService.repaymentJob(param);
		} catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}

		return resultVo;
	}

	@ResponseBody
	@RequestMapping("TYBWithdrawJob")
	public ResultVo testTYBWithdrawJob() {
		ResultVo resultVo = null;
		try {
			productService.experienceWithdraw(new Date());
			return new ResultVo(true);
		} catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}

		return resultVo;
	}

	@ResponseBody
	@RequestMapping("TYBDailySettlementJob")
	public ResultVo testTYBDailySettlementJob() {
		ResultVo resultVo = null;
		try {
			productService.experienceDailySettlement(new Date());
			return new ResultVo(true);
		} catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}

		return resultVo;
	}

	/**
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("TYBRecommendedAwardsJob")
	public ResultVo testTYBRecommendedAwardsJob() {
		ResultVo resultVo = null;
		try {
//			recommendedAwardsService.grentRecommendedAwardsData(null);
			return new ResultVo(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}

		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("OpenServiceNotifyJob")
	public ResultVo testOpenServiceNotifyJob() {
		ResultVo resultVo = null;
		try {
			openNotifyService.asynNotify();
			resultVo = new ResultVo(true);
		} catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("ExperienceWithdrawSendSmsJob")
	public ResultVo testExperienceWithdrawSendSmsJob() {
		ResultVo resultVo = null;
		try {
			productService.experienceWithdrawSendSms(new Date());
			resultVo = new ResultVo(true);
		} catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("MendBankJob")
	public ResultVo testMendBankJob() {
		ResultVo resultVo = null;
		try {
			bankCardService.mendBankCard();
			resultVo = new ResultVo(true);
		} catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	} 
	
	@ResponseBody
	@RequestMapping("FinancialStatisticsJob")
	public ResultVo testFinancialStatisticsJob()throws SLException {
		financialStatisticsService.sendRepaymentEmail();
		return new ResultVo(true);
	} 
	
	@ResponseBody
	@RequestMapping("TermAtoneBuyJob")
	public ResultVo TermAtoneBuyJob() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			termService.termAtoneBuy(param);
			resultVo = new ResultVo(true);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@SuppressWarnings({ "unchecked"})
	@ResponseBody
	@RequestMapping("TermAtoneSettlementJob")
	public ResultVo TermAtoneSettlement() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			ResultVo result = termService.termAtoneSettlement(param);
			if(ResultVo.isSuccess(result)) {
				List<Map<String, Object>> smsList = (List<Map<String, Object>>)result.getValue("data");
				for(Map<String, Object> sms : smsList) {
					smsService.asnySendSMSAndSystemMessage(sms);
				}
			}
			resultVo = new ResultVo(true);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("TermAtoneWithdrawJob")
	public ResultVo TermAtoneWithdrawJob() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			termService.termAtoneWithdraw(param);
			resultVo = new ResultVo(true);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("TermDailySettlementJob")
	public ResultVo TermDailySettlementJob() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			termService.termDailySettlement(param);
			resultVo = new ResultVo(true);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("GoldDailySettlementJob")
	public ResultVo GoldDailySettlementJob() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			return goldService.goldDailySettlement(param);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("GoldWithdrawJob")
	public ResultVo GoldWithdrawJob() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			return goldService.goldWithdraw(param);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("GoldMonthlySettlement")
	public ResultVo GoldMonthlySettlement() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			return goldService.goldMonthlySettlement(param);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("RecoverAtoneJob")
	public ResultVo RecoverAtoneJob() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			return productService.recoverUnAtone(param);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("SumBusinessHistory")
	public ResultVo sumBusinessHistory() throws SLException{
		ResultVo resultVo = null;
		try {
			summarizationBusinessHistoryService.sumBusinessHistory();
			resultVo = new ResultVo(true);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("autoPublishProject")
	public ResultVo autoPublishProject() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return projectJobService.autoPublishProject(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("autoReleaseProject")
	public ResultVo autoReleaseProject() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return projectJobService.autoReleaseProject(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("autoUnReleaseProject")
	public ResultVo autoUnReleaseProject() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return projectJobService.autoUnReleaseProject(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("autoCompensateProject")
	public ResultVo autoCompensateProject() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return projectJobService.autoCompensateProject(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("autoRepaymentProject")
	public ResultVo autoRepaymentProject() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return projectJobService.autoRepaymentProject(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("autoRiskRepaymentProject")
	public ResultVo autoRiskRepaymentProject() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return projectJobService.autoRiskRepaymentProject(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("autoAuditRefuseProject")
	public ResultVo autoAuditRefuseProject() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return projectJobService.autoRefuseProject(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	
	
	@ResponseBody
	@RequestMapping("autoPublishWealth")
	public ResultVo autoPublishWealth() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return wealthJobService.autoPublishWealth(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("autoReleaseWealth")
	public ResultVo autoReleaseWealth() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return wealthJobService.autoReleaseWealthJob(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("autoMatchLoan")
	public ResultVo autoMatchLoan() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return wealthJobService.autoMatchLoan(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("autoUnReleaseWealth")
	public ResultVo autoUnReleaseWealth() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("currentDate", new Date());
			return wealthJobService.autoUnReleaseWealth(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("autoRepaymentWealth")
	public ResultVo autoRepaymentWealth() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("expectRepaymentDate", DateUtils.getCurrentDate("yyyyMMdd"));
			return wealthJobService.autoRepaymentWealth(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("autoRecoveryWealth")
	public ResultVo autoRecoveryWealth(@RequestBody Map<String, Object> params) throws SLException{
		ResultVo resultVo = null;
		try {
			if(StringUtils.isEmpty((String)params.get("currentDate"))) {
				params.put("currentDate", DateUtils.getCurrentDate("yyyyMMdd"));
			}
			return wealthJobService.autoRecoveryWealth(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("autoAtoneWealth")
	public ResultVo autoAtoneWealth() throws SLException{
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("advanceDate", DateUtils.getAfterDay(DateUtils.truncateDate(new Date()), 1));
			params.put("dueDate", DateUtils.truncateDate(new Date()));
			return wealthJobService.autoAtoneWealth(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("autoMonthlyWealth")
	public ResultVo autoMonthlyWealth(@RequestBody Map<String, Object> params) throws SLException{
		ResultVo resultVo = null;
		try {
			if(StringUtils.isEmpty((String)params.get("currentDate"))) {
				params.put("currentDate", DateUtils.getCurrentDate("yyyyMMdd"));
			}
			else {
				params.put("currentDate", (String)params.get("currentDate"));
			}
			return goldService.goldMonthlySettlement(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("handleOriginalData")
	public ResultVo handleOriginalData() throws SLException{
		ResultVo resultVo = null;
		try {
			return employeeLoadService.handleOriginalData();
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("offLineTransferToSLFinanceJob")
	public ResultVo offLineTransferToSLFinanceJob() throws SLException{
		ResultVo resultVo = null;
		try {
			
			resultVo =  offlineWealthService.transferOffLineCustManagerjob();
		}catch (SLException e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("synchronizeCustInfoAndInvestInfoFromWealthToSlcf")
	public ResultVo synchronizeCustInfoAndInvestInfoFromWealthToSlcf() throws SLException{
		ResultVo resultVo = null;
		try {
			// 增量同步线下理财的客户信息和投资信息到财富中
			resultVo =  offlineWealthService.synchronizeCustInfoAndInvestInfoFromWealthToSlcf();
		}catch (Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@Autowired	
	WaitingAuditSendMessageService waitingAuditSendMessageService;
	@ResponseBody
	@RequestMapping("waitingAuditSendMessage")
	public ResultVo waitingAuditSendMessage() {
		waitingAuditSendMessageService.waitingAuditSendMessage();
		return new ResultVo(true);
	}
	@ResponseBody
	@RequestMapping("monitorPosOnOnlineAndOffline")
	public ResultVo monitorPosOnOnlineAndOffline() {
		waitingAuditSendMessageService.monitorPosOnOnlineAndOffline();
		return new ResultVo(true);
	}
	
	@ResponseBody
	@RequestMapping("leaveJob")
	public ResultVo leaveJob() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return refereeAuditService.leaveJob(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("loanRepaymentJob")
	public ResultVo loanRepaymentJob(@RequestBody Map<String, Object> params) {
		ResultVo resultVo = null;
		try {
			if(StringUtils.isEmpty((String)params.get("currentDate"))) {
				params.put("repaymentDate", DateUtils.getCurrentDate("yyyyMMdd"));
			}
			else {
				params.put("repaymentDate", (String)params.get("currentDate"));
			}
			return loanJobService.autoRepaymentLoan(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("loanUnReleaseJob")
	public ResultVo loanUnReleaseJob() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return loanJobService.autoUnReleaseLoan(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("goldCommisionJob")
	public ResultVo goldCommisionJob() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return loanJobService.caclCommission(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("commisionWithdrawJob")
	public ResultVo commisionWithdrawJob(@RequestBody Map<String, Object> params) {
		ResultVo resultVo = null;
		try {
			if(StringUtils.isEmpty((String)params.get("currentDate"))) {
				params.put("currentDate", DateUtils.getCurrentDate("yyyyMMdd"));
			}
			else {
				params.put("currentDate", (String)params.get("currentDate"));
			}
			return loanJobService.commissionWithdraw(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("loanGrantConfirmJob")
	public ResultVo loanGrantConfirmJob() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return loanJobService.autoGrantConfirm(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("loanGrantConfirmYZJob")
	public ResultVo loanGrantConfirmYZJob() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return loanJobService.autoGrantConfirmYZ(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("loanGrantConfirmCFJob")
	public ResultVo loanGrantConfirmCFJob() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("companyName", Constant.DEBT_SOURCE_CFXJD);
			return loanJobService.autoGrantConfirm4Company(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}

    /***
     *
     * <b>方法名：</b>：loanGrantConfirmJLJob<br>
     * <p><b>功能说明：</b>巨涟定时放款确认 触发器 </p>
     * @author <font color='blue'>张祥</font>
     * @date  2017/7/6 16:44
     * @param
     * @return com.slfinance.vo.ResultVo
     */
    @ResponseBody
    @RequestMapping("loanGrantConfirmJLJob")
    public ResultVo loanGrantConfirmJLJob() {
        ResultVo resultVo = null;
        try {
        	Map<String, Object> params = new HashMap<String, Object>();
			params.put("companyName", Constant.DEBT_SOURCE_JLJR);
			return loanJobService.autoGrantConfirm4Company(params);
        }catch (SLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            resultVo = new ResultVo(false, e.getMessage());
        }
        return resultVo;
    }
	
	@ResponseBody
	@RequestMapping("loanGrantJob")
	public ResultVo loanGrantJob() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return loanJobService.autoGrant(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("loanGrantCFJob")
	public ResultVo loanGrantCFJob() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("companyName", Constant.DEBT_SOURCE_CFXJD);
			return loanJobService.autoGrant4Company(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("loanGrantYZJob")
	public ResultVo loanGrantYZJob() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return loanJobService.autoGrantYZ(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}

    /***
     *
     * <b>方法名：</b>：loanGrantJLJob<br>
     * <p><b>功能说明：</b>巨涟放款确认触发器 </p>
     * @author <font color='blue'>张祥</font>
     * @date  2017/7/6 16:38
     * @param
     * @return com.slfinance.vo.ResultVo
     */
    @ResponseBody
    @RequestMapping("loanGrantJLJob")
	public ResultVo loanGrantJLJob(){

		ResultVo resultVo = null;
		try{
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("companyName", Constant.DEBT_SOURCE_JLJR);
			return loanJobService.autoGrant4Company(params);
        }catch (SLException e){
		    e.printStackTrace();
		    resultVo = new ResultVo(false,e.getMessage());
        }
        return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("loanCancelJob")
	public ResultVo loanCancelJob() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return loanJobService.autoCancelLoan(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	@ResponseBody
	@RequestMapping("loanPublishJob")
	public ResultVo loanPublishJob() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return loanJobService.autoPublishLoan(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	
	@ResponseBody
	@RequestMapping("calcPerformanceJob")
	public ResultVo calcPerformanceJob(@RequestBody Map<String, Object> params) {
		ResultVo resultVo = null;
		try {
            if (!CommonUtils.isEmpty(params.get("currentDate"))) {
                params.put("currentDate", CommonUtils.emptyToString(params.get("currentDate")).substring(0, 6));
            }
            return calcCommService.calcPerformance(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}

    @ResponseBody
	@RequestMapping("calcCommissionJob")
	public ResultVo calcCommissionJob() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return calcCommService.calcCommission(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
   
    /**
     * 拿米借款项目批量审核、发布
     * @return
     */
    @ResponseBody
	@RequestMapping("batchAuditPass4NM")
	public ResultVo batchAuditPass4NM() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("start", "0");
			params.put("length", "20000");
			params.put("loanStatus", Constant.LOAN_STATUS_01);
			params.put("companyName", Constant.DEBT_SOURCE_NMJR);
			params.put("isBackStage", "借款信息");
			return loanJobService.autoAuditLoanNM(params);
		}catch (SLException e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
    /**
     * 意真借款项目批量审核、发布
     * @return
     */
    @ResponseBody
	@RequestMapping("batchAuditPass4YZ")
	public ResultVo batchAuditPass4YZ() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("start", "0");
			params.put("length", "20000");
			params.put("loanStatus", Constant.LOAN_STATUS_01);
			params.put("companyName", Constant.DEBT_SOURCE_YZJR);
			params.put("isBackStage", "借款信息");
			return loanJobService.autoAuditLoanYZ(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
    
    /**
     * 财富借款项目批量审核、发布
     * @return
     */
    @ResponseBody
	@RequestMapping("batchAuditPass4CF")
	public ResultVo batchAuditPass4CF() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("start", "0");
			params.put("length", "20000");
			params.put("loanStatus", Constant.LOAN_STATUS_01);
			params.put("companyName", Constant.DEBT_SOURCE_CFXJD);
			params.put("isBackStage", "借款信息");
			return loanJobService.autoAuditLoan4Company(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}

    /***
     *
     * <b>方法名：</b>：batchAuditPass4JL<br>
     * <p><b>功能说明：</b>巨涟定时审核与发布 定时任务触发器 </p>
     * @author <font color='blue'>张祥</font>
     * @date  2017/7/6 16:54
     * @param
     * @return com.slfinance.vo.ResultVo
     */
    @ResponseBody
	@RequestMapping("batchAuditPass4JL")
	public ResultVo batchAuditPass4JL() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("start", "0");
			params.put("length", "20000");
			params.put("loanStatus", Constant.LOAN_STATUS_01);
			params.put("companyName", Constant.DEBT_SOURCE_JLJR);
			params.put("isBackStage", "借款信息");
			return loanJobService.autoAuditLoan4Company(params);
		}catch (SLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
    
    /**
     * 意真放款统计发送邮件
     * @return
     */
    @ResponseBody
	@RequestMapping("dailyDataYZloanAccountSummary")
	public ResultVo dailyDataYZloanAccountSummary() {
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			return bizExtractData.dailyDataYZloanAccountSummary(params);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
    
    @Autowired
    ActivityId13Job activityId13Job;
    @ResponseBody
	@RequestMapping("activityId13Job")
	public ResultVo activityId13Job() {
		try {
			activityId13Job.invoke();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResultVo(true);
	}
    @ResponseBody
	@RequestMapping("autoReserveCancel")
	public ResultVo autoReserveCancel() {
		try {
			autoReserveInvestJob.invoke();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResultVo(true);
	}
    /**
     * 每日统计平台数据
     * @return
     */
    @ResponseBody
	@RequestMapping("timeTaskDay")
	public ResultVo timeTaskDay() {
		ResultVo resultVo = null;
		try {
			return platformTradeDataService.findTradeDataDay();
		}catch (Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
    /**
     * 每月统计平台数据
     * @return
     */
    @ResponseBody
	@RequestMapping("timeTaskMonth")
	public ResultVo timeTaskMonth() {
		ResultVo resultVo = null;
		try {
			return platformTradeDataService.findTradeDataMonth();
		}catch (Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
    /**
     * 
     * <更新过期红包状态>
     * <功能详细描述>
     *
     * @return [参数说明]
     * @return ResultVo [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    @ResponseBody
	@RequestMapping("autoExpire")
    public ResultVo autoExpire(){
    	
    	ResultVo resultVo = null;
		try {
			return autoExpireService.AutoExpireDay();
		}catch (Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
    }

	/**
	 * 体验金定时任务
	 * @return
	 */
	@ResponseBody
	@RequestMapping("expLoanJob")
	public ResultVo expLoanJob(){

		ResultVo resultVo = null;
		try {
			return custActivityInfoService.expLoanTimeJob();
		}catch (Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	
	
	@Autowired
	private LoanProjectService loanProjectService;
	/**
	 * 体验授权申请定时任务
	 * @return
	 */
	@ResponseBody
	@RequestMapping("accreditRequestJob")
	public ResultVo accreditRequestJob(){
		
		ResultVo resultVo = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			loanProjectService.AccreditRequestJob(params);
		    return resultVo = new ResultVo(true);
		}catch (SLException e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}
	
	
//	@Autowired
//	private LoanAgreeService loanAgreeService;
	/**
	 * 测试保存借款协议
	 * @return
	 */
	/*@ResponseBody
	@RequestMapping(value = "laonAgree")
	public ResultVo laonAgree(){
		
		ResultVo resultVo = null;
		try {
			  LoanAgreeEntity loanAgreeEntity = new LoanAgreeEntity();
		      loanAgreeEntity.setId(SharedUtil.getUniqueString());
//		      loanAgreeEntity.setCustId(param.get("custId").toString());
//		      loanAgreeEntity.setBankName(param.get("bankName").toString());
//		      loanAgreeEntity.setBankNo(param.get("bankNo").toString());
//		      loanAgreeEntity.setNoAgree(param.get("noAgree").toString());
		      loanAgreeEntity.setCustId("123456");
		      loanAgreeEntity.setBankName("招商银行");
		      loanAgreeEntity.setBankNo("12345648978");
		      loanAgreeEntity.setNoAgree("987654321");
		      loanAgreeEntity = loanAgreeService.save(loanAgreeEntity);
		      resultVo = new ResultVo(true, "保存借款协议成功");
		}catch (Exception e) {
			e.printStackTrace();
			resultVo = new ResultVo(false, e.getMessage());
		}
		return resultVo;
	}*/
}
