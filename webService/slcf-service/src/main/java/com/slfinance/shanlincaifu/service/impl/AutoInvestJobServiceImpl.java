package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AutoInvestInfoEntity;
import com.slfinance.shanlincaifu.entity.AutoTransferInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.CustRecommendInfoEntity;
import com.slfinance.shanlincaifu.entity.InterfaceDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanRebateInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanTransferApplyEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.ParamEntity;
import com.slfinance.shanlincaifu.entity.ProjectInvestInfoEntity;
import com.slfinance.shanlincaifu.entity.ReserveInvestInfoEntity;
import com.slfinance.shanlincaifu.entity.ReserveInvestRelationEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.AutoInvestInfoRepository;
import com.slfinance.shanlincaifu.repository.AutoTransferInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.CustRecommendInfoRepository;
import com.slfinance.shanlincaifu.repository.InterfaceDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.JobRunDetailRepository;
import com.slfinance.shanlincaifu.repository.JobRunListenerRepository;
import com.slfinance.shanlincaifu.repository.LoanDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanRebateInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanTransferApplyRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.ParamRepository;
import com.slfinance.shanlincaifu.repository.ProjectInvestInfoRepository;
import com.slfinance.shanlincaifu.repository.ReserveInvestInfoRepository;
import com.slfinance.shanlincaifu.repository.ReserveInvestRelationRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.AutoInvestRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanManagerRepositoryCustom;
import com.slfinance.shanlincaifu.service.AutoInvestJobService;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.DeviceService;
import com.slfinance.shanlincaifu.service.ExpandInfoService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.JobListenerService;
import com.slfinance.shanlincaifu.service.LoanManagerService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

@Slf4j
@Service("autoInvestJobService")
public class AutoInvestJobServiceImpl implements AutoInvestJobService{

	@Autowired
	private JobRunListenerRepository jobRunListenerRepository;
	
	@Autowired
	private JobRunDetailRepository jobRunDetailRepository;
	
	@Autowired
	private JobListenerService jobListenerService;
	
	@Autowired
	private AutoInvestInfoRepository autoInvestInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private LoanManagerService loanManagerService;
	
	@Autowired
	private AutoTransferInfoRepository autoTransferInfoRepository;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private LoanManagerRepositoryCustom loanManagerRepositoryCustom;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	
	@Autowired
	private ReserveInvestInfoRepository reserveInvestInfoRepository;
	
	@Autowired
	private InnerClass innerClass;
	
	@Autowired
	@Qualifier("loanAuditThreadPoolTaskExecutor")
	private Executor executor;
	
	@Autowired
	private LoanInfoRepository loanInfoRepository;
	
	@Autowired
	private LoanInfoRepositoryCustom loanInfoRepositoryCustom;
	
	@Autowired
	private LoanDetailInfoRepository loanDetailInfoRepository;
	
	@Autowired
	private AutoInvestRepositoryCustom autoInvestRepositoryCustom;
	
	@Autowired
	private LoanTransferApplyRepository loanTransferApplyRepository;
	
	@Autowired
	private ParamRepository paramRepository;
	
	private AutoInvestJobService self;//AOP增强后的代理对象  
	
	@Override
	public void setSelf(Object proxyBean) {
		self = (AutoInvestJobService) proxyBean;
		System.out.println("AutoInvestJobService = " + AopUtils.isAopProxy(this.self)); 
	}
	@Override
//	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo autoInvest(Map<String, Object> params) throws SLException {
//		// a）判断Job_listener中该任务是否正在执行，正在执行则结束任务。 
//		JobRunListenerEntity jobRunListenerEntity = jobRunListenerRepository.findByJobName(Constant.JOB_NAME_AUTO_INVEST);
//		if(jobRunListenerEntity == null) {
//			return new ResultVo(false, String.format("暂未设置%s定时任务，请先设置", Constant.JOB_NAME_AUTO_INVEST));
//		}
//		if(Constant.EXEC_STATUS_RUNNING.equals(jobRunListenerEntity.getExecuteStatus())) {
//			JobRunDetailEntity jobRunDetailEntity = jobRunDetailRepository.findRunningByJobId(jobRunListenerEntity.getId());
//			if(jobRunDetailEntity != null){
//				jobListenerService.stopJobRun(jobRunDetailEntity.getId(), null);
//			}
//		}
		Date d1 = new Date();
		// b) 按照CUST_PRIORITY（用户优先级，默认都为1，此处是为了后期可能区分优质客户使用）desc， OPEN_DATE asc取BAO_T_AUTO_INVEST_INFO中开启状态的用户。 
		List<String> custList = autoInvestInfoRepository.findByOpenStatusOrderByCustPriorityDescOpenDateAsc(Constant.AUTO_INVEST_INFO_OPEN_STATUS_01);
		Date d2 = new Date();
		int second1 = DateUtils.secondPhaseDiffer(d1, d2);
		// c) 按照用户利率和期限，先到转让申请表查询符合条件的数据，若存在则调用购买债权转让流程。若不存在则继续查询借款表，若存在则调用购买借款流程。 
		// 符合条件的数据按照投资进度由大到小排序。
		for (int i = 0; i < custList.size(); i++) {
			String id = custList.get(i);
			//实时查询 该客户是否已经修改或者关闭智能投顾，取最新的自动投标实体  by guoyk 2017/05/22  SLCF-2821
//			AutoInvestInfoEntity custEntity = autoInvestInfoRepository.findByCustId(custEntity2.getCustId());
			AutoInvestInfoEntity custEntity = autoInvestInfoRepository.findOne(id);
			if (custEntity!=null && Constant.AUTO_INVEST_INFO_OPEN_STATUS_02.equals(custEntity.getOpenStatus())) {
				continue;
			}
			//是否开启小额复投
//			String smallQuantityInvest = custEntity.getSmallQuantityInvest();
			//复投类型(有可能为NUll)
			String canInvestProduct = custEntity.getCanInvestProduct();
			AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(custEntity.getCustId());
			if (accountInfoEntity==null) {
				log.warn("客户的账户查询出错，custId="+custEntity.getCustId());
				continue;
			}
			// 客户账户可用金额
			BigDecimal availableAmount = accountInfoEntity.getAccountAvailableAmount();
			//客户保留余额
			BigDecimal keepAvailableAmount = custEntity.getKeepAvailableAmount()==null?BigDecimal.ZERO:custEntity.getKeepAvailableAmount();
			// 交易金额
			BigDecimal tradeAmount = ArithUtil.sub(availableAmount, keepAvailableAmount);
			// 余额为0, next
			if(accountInfoEntity == null || accountInfoEntity.getAccountAvailableAmount().compareTo(BigDecimal.ZERO) <= 0){
				try {
					self.updateAutoTransfer(id);
				} catch (Exception e) {
					log.warn(String.format("修改智能投顾开启时间出错，主键id是：%s", id));
					e.printStackTrace();
				}
				continue ;
			}
			//用户可投资余额<=0 next 
			if(tradeAmount.compareTo(BigDecimal.ZERO) <= 0){
				try {
					self.updateAutoTransfer(id);
				} catch (Exception e) {
					log.warn(String.format("修改智能投顾开启时间出错，主键id是：%s", id));
					e.printStackTrace();
				}
				continue ;
			}
			
			//定义变量
			boolean credit = false;
			boolean disperse = false;
			List<Integer> seatTerm = Lists.newArrayList();
			List<Integer> loanSeatTerm = new ArrayList<Integer>();
			if(StringUtils.isNotEmpty(canInvestProduct)){
				String[] canInvestProductList = canInvestProduct.split(",");
				for (int c = 0; c < canInvestProductList.length; c++) {
					if(canInvestProductList[c].equals("转让专区（可转让标的）")){
						credit = true;
						loanSeatTerm.add(30);
					}
					if(canInvestProductList[c].equals("转让专区（不可转让标的）")){
						credit = true;
						loanSeatTerm.add(-1);
					}
					if(canInvestProductList[c].equals("优选项目（可转让标的）")){
						disperse = true;
						seatTerm.add(30);
					}
					if(canInvestProductList[c].equals("优选项目（不可转让标的）")){
						disperse = true;
						seatTerm.add(-1);
					}
				}
			} else {
				credit = true;
				disperse = true;
			}
			
			/*
				1.可用余额>=剩余可投金额----全投
				2.可用余额<起投金额----下个项目
				3.匹配金额公式----投资（起投金额  <=可用余额 < 剩余可投金额）
			 */
			if (credit) {
				// 购买债券转让
//				boolean flag = false;
				List<Map<String, Object>> creditList = null;
				// 预入参数-取得数据后不用再判断
				Map<String, Object> creditParams = Maps.newHashMap();
				//如果客户的可用投资金额小于1000元优先复投30天以下标的，且不判断其他规则 
//				if ("是".equals(smallQuantityInvest) && tradeAmount.compareTo(new BigDecimal("1000"))<0) {
//					creditParams.put("maxTerm", 30);
//					creditParams.put("limitedTermMin", 0);
//					creditParams.put("loanUnit", "天");
//					creditList = loanManagerService.queryCreditListForJob(creditParams);
//					//如果用户开启了小额复投，并且账户余额小于1000，但是没有复核条件的标的，需要再按照原先规则跑
//					if (creditList.size()==0||creditList==null) {
//						creditParams.put("minYearRate", custEntity.getLimitedYearRate()); 
//						creditParams.put("limitedYearRateMax", custEntity.getLimitedYearRateMax());
//						creditParams.put("maxTerm", custEntity.getLimitedTerm());
//						creditParams.put("limitedTermMin", custEntity.getLimitedTermMin());
//						creditParams.put("repaymentMethod", custEntity.getRepaymentMethod());
//						creditParams.put("loanSeatTerm", loanSeatTerm);
//						creditParams.put("loanUnit", custEntity.getLoanUnit());
//						creditList = loanManagerService.queryCreditListForJob(creditParams);
//					}
//				}else {
//					creditParams.put("minYearRate", custEntity.getLimitedYearRate()); 
//					creditParams.put("limitedYearRateMax", custEntity.getLimitedYearRateMax());
//					creditParams.put("maxTerm", custEntity.getLimitedTerm());
//					creditParams.put("limitedTermMin", custEntity.getLimitedTermMin());
//					creditParams.put("repaymentMethod", custEntity.getRepaymentMethod());
//					creditParams.put("loanSeatTerm", loanSeatTerm);
//					creditParams.put("loanUnit", custEntity.getLoanUnit());
//					flag = true;
//					creditList = loanManagerService.queryCreditListForJob(creditParams);
//				}
				creditParams.put("minYearRate", custEntity.getLimitedYearRate()); 
				creditParams.put("limitedYearRateMax", custEntity.getLimitedYearRateMax());
				creditParams.put("maxTerm", custEntity.getLimitedTerm());
				creditParams.put("limitedTermMin", custEntity.getLimitedTermMin());
				creditParams.put("repaymentMethod", custEntity.getRepaymentMethod());
				creditParams.put("loanSeatTerm", loanSeatTerm);
				creditParams.put("loanUnit", custEntity.getLoanUnit());
				creditList = loanManagerService.queryCreditListForJob(creditParams);
				//List<Map<String, Object>> creditList = loanManagerService.queryCreditListForJob(creditParams);
				//根据规则查询好的标的，循环投标 by guoyk 2017/05/22 SLCF-2821
				self.credit(creditList, custEntity);
			}
			
			if(disperse){
				// 购买散标
//				boolean flag = false;
				List<Map<String, Object>> loanList = null;
				// 预入参数-取得数据后不用再判断
				Map<String, Object> loanParams = Maps.newHashMap();
				//如果客户的可用投资金额小于1000元优先复投30天以下标的，且不判断其他规则 
//				if ("是".equals(smallQuantityInvest) && tradeAmount.compareTo(new BigDecimal("1000"))<0) {
//					loanParams.put("maxTerm", 30);
//					loanParams.put("limitedTermMin", 0);
//					loanParams.put("loanUnit", "天");
//					loanList = loanManagerService.queryDisperseListForJob(loanParams);
//					//如果用户开启了小额复投，并且账户余额小于1000，但是没有复核条件的标的，需要再按照原先规则跑
//					if (loanList.size()==0||loanList==null) {
//						loanParams.put("minYearRate", custEntity.getLimitedYearRate()); 
//						loanParams.put("limitedYearRateMax", custEntity.getLimitedYearRateMax());
//						loanParams.put("maxTerm", custEntity.getLimitedTerm());
//						loanParams.put("limitedTermMin", custEntity.getLimitedTermMin());
//						loanParams.put("repaymentMethod", custEntity.getRepaymentMethod());
//						loanParams.put("seatTerm", seatTerm);
//						loanParams.put("loanUnit", custEntity.getLoanUnit());
//						loanList = loanManagerService.queryDisperseListForJob(loanParams);
//					}
//				}else {
//					loanParams.put("minYearRate", custEntity.getLimitedYearRate()); 
//					loanParams.put("limitedYearRateMax", custEntity.getLimitedYearRateMax());
//					loanParams.put("maxTerm", custEntity.getLimitedTerm());
//					loanParams.put("limitedTermMin", custEntity.getLimitedTermMin());
//					loanParams.put("repaymentMethod", custEntity.getRepaymentMethod());
//					loanParams.put("seatTerm", seatTerm);
//					loanParams.put("loanUnit", custEntity.getLoanUnit());
//					flag = true;
//					loanList = loanManagerService.queryDisperseListForJob(loanParams);
//				}
				loanParams.put("minYearRate", custEntity.getLimitedYearRate()); 
				loanParams.put("limitedYearRateMax", custEntity.getLimitedYearRateMax());
				loanParams.put("maxTerm", custEntity.getLimitedTerm());
				loanParams.put("limitedTermMin", custEntity.getLimitedTermMin());
				loanParams.put("repaymentMethod", custEntity.getRepaymentMethod());
				loanParams.put("seatTerm", seatTerm);
				loanParams.put("loanUnit", custEntity.getLoanUnit());
				loanList = loanManagerService.queryDisperseListForJob(loanParams);
				//根据规则查询好的标的，循环投标 by guoyk 2017/05/22 SLCF-2821
				self.disperse(loanList, custEntity);
				
			}
			// d) 将用户OPEN_DATE修改为最新，不管购买成功与失败（包括没有符合的）均改为最新。 
//			custEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
//			custEntity.setOpenDate(new Date());
			try {
				self.updateAutoTransfer(id);
			} catch (Exception e) {
				log.warn(String.format("修改智能投顾开启时间出错，主键id是：%s", id));
				e.printStackTrace();
			}
			
		} // for cust
		// 	1.一轮智能投顾结束，新发布的标的(允许智能投顾的标的)默认状态是N，把新发布的标的状态修改为Y
		// 2.转让审核通过的标的默认状态是N，修改状态为Y
		Date d3 = new Date();
		int a = 0;
		while (a<3){
			try {
				self.updateISRunAutoInvest();
				break;
			} catch (Exception e) {
				log.warn("修改智能投顾标识出错:"+a);
				e.printStackTrace();
			}
			a++;
		}
		Date d4 = new Date();
		int second2 = DateUtils.secondPhaseDiffer(d3, d4);
		int second3 = DateUtils.secondPhaseDiffer(d1, d4);
		//记录跑完一轮花费时间日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUTO_INVEST_INFO);
		logInfoEntity.setRelatePrimary("ZNTG");
		logInfoEntity.setLogType(Constant.JOB_NAME_AUTO_INVEST);
		logInfoEntity.setOperDesc(String.format("查询所有开启智能投顾用户耗时：%s秒，修改是否跑过智能投顾标识耗时：%s秒，一轮智能投顾总耗时：%s秒", second1,second2,second3));
		logInfoEntity.setOperPerson("系统用户");
		logInfoEntityRepository.save(logInfoEntity);
		return new ResultVo(true, "智能投顾执行结束");
	}
	
	public void credit(List<Map<String, Object>> creditList,AutoInvestInfoEntity custEntity){
		for(int j = 0; j < creditList.size(); j++){
			Map<String, Object> transferMap = creditList.get(j);
			// 客户账户可用金额  -- 每次投标账户金额发生变化
			BigDecimal availableAmount = accountInfoRepository.findByCustId(custEntity.getCustId()).getAccountAvailableAmount();
			//客户的保留余额
			BigDecimal keepAvailableAmount = custEntity.getKeepAvailableAmount()==null?BigDecimal.ZERO:custEntity.getKeepAvailableAmount();
			// 交易金额(自动投标金额=账户可用金额-保留余额；)
			BigDecimal tradeAmount = ArithUtil.sub(availableAmount, keepAvailableAmount);
			// 剩余投资金额
			BigDecimal remainAmount = (BigDecimal)transferMap.get("remainAmount");
			//标的为转让标且转让金额小于100元由智能投顾全投 SLCF-3181 by guoyk
			if (remainAmount.compareTo(new BigDecimal("100"))<100) {
				
			}
			
			// 起投金额  (改为 0 by guoyk 2017/5/17 SLCF-2821)
//			BigDecimal investMinAmount = (BigDecimal)transferMap.get("investMinAmount");
//			BigDecimal investMinAmount = BigDecimal.ZERO;
			// 过滤条件 
//			if (tradeAmount.compareTo(remainAmount) >= 0) {
//				tradeAmount = remainAmount;
//			} else if (tradeAmount.compareTo(investMinAmount) < 0) {// 小于起投金额, 且大于0，全投。by guoyk 2017/6/1 SLCF-2821
//				continue ;
//			} else {// 起投金额  <=交易金额 < 剩余可投金额   的情况
//				if(tradeAmount.compareTo(investMinAmount) > 0 
//						&& !ArithUtil.isDivInt((BigDecimal)transferMap.get("increaseAmount"), ArithUtil.sub(tradeAmount, investMinAmount))){
//					// 递增金额
//					BigDecimal increaseAmount = (BigDecimal)transferMap.get("increaseAmount");
//					// 预计可投资金额
//					BigDecimal expectedAmount = ArithUtil.sub(remainAmount, investMinAmount);
//					
//					// 递增倍数=（min(预计可投资金额，可用余额)-起投）/递增， 大于0 截取整数; 小于0，不可投资；
//					BigDecimal multipleBig = ArithUtil.div(ArithUtil.sub(tradeAmount.min(expectedAmount), investMinAmount), increaseAmount);
//					if(multipleBig.compareTo(BigDecimal.ZERO) < 0){
//						continue ;
//					}
//					int multiple = multipleBig.intValue();
//					// 投资金额=起投金额+递增金额*递增倍数
//					tradeAmount = ArithUtil.add(investMinAmount, ArithUtil.mul(increaseAmount, new BigDecimal(multiple), 0));
//				}
//			}
			//过滤条件 ：SLCF-2821 智能投顾不判断起投金额和递增金额 by guoyk 2017/6/1
			if(tradeAmount.compareTo(new BigDecimal("100"))<0){//首先，账户可用投资余额若小于100，直接pass
				continue ;
				//若账户可用投资余额>=100,且小于标的剩余金额
			}else if(tradeAmount.compareTo(new BigDecimal("100"))>=0 && tradeAmount.compareTo(remainAmount)<0){
				// TODO 判断如果购买后，标的还剩余的金额 小于 100 就 控制投标金额，让其标的剩余余额减去100
				BigDecimal remainderAmount = ArithUtil.sub(remainAmount, tradeAmount);
				if (remainderAmount.compareTo(new BigDecimal("100"))<0) {
					tradeAmount = ArithUtil.sub(remainAmount, new BigDecimal("100"));
					//此时 有可能 tradeAmount 小于100
					if (tradeAmount.compareTo(new BigDecimal("100"))<0) {
						continue ;
					}
				}else {
					//对可投金额取整
					tradeAmount = new BigDecimal(String.valueOf(tradeAmount.intValue()));
				}
				
			}else if (tradeAmount.compareTo(remainAmount) >= 0) {//若大于标的剩余金额，全投
				tradeAmount = remainAmount;
			}

			Map<String, Object> buyCreditParams = Maps.newHashMap();
			buyCreditParams.put("transferApplyId", transferMap.get("transferApplyId"));
			buyCreditParams.put("custId", custEntity.getCustId());
			buyCreditParams.put("tradeAmount", tradeAmount);
			buyCreditParams.put("appSource", "auto");
			try{
				// 调用购买债权接口
				ResultVo buyCreditVo = loanManagerService.buyCredit(buyCreditParams);
				if(ResultVo.isSuccess(buyCreditVo)){
					log.info(String.format("交易成功[custId=%s, tradaAmount=%s, availableAmount=%s, transferApplyId=%s]"
							, custEntity.getCustId(), tradeAmount.toPlainString(), availableAmount.toPlainString(), transferMap.get("transferApplyId")));
				
//					String string = String.format("交易成功[custId=%s, tradaAmount=%s, availableAmount=%s, transferApplyId=%s]\r\n"
//							, custEntity.getCustId(), tradeAmount.toPlainString(), availableAmount.toPlainString(), transferMap.get("transferApplyId"));
//					File file = new File("C:/Users/konyeah/Desktop/test.txt");
//					FileOutputStream outputStream = new FileOutputStream(file, true);
//					outputStream.write(string.getBytes());
//					outputStream.close();
				}else{
					log.warn(String.format("交易失败[custId=%s, tradaAmount=%s, remainAmount=%s, transferApplyId=%s, errorMessage=%s]"
							, custEntity.getCustId(), tradeAmount.toPlainString(), remainAmount.toPlainString(), transferMap.get("transferApplyId"), buyCreditVo.getValue("message").toString()));
				}
				//再实时查询投完标后客户的可投余额 by guoyk 2017/5/23
				// 客户账户可用金额  -- 每次投标账户金额发生变化
//				availableAmount = accountInfoRepository.findByCustId(custEntity.getCustId()).getAccountAvailableAmount();
				//客户的保留余额
//				keepAvailableAmount = custEntity.getKeepAvailableAmount()==null?BigDecimal.ZERO:custEntity.getKeepAvailableAmount();
				// 交易金额(自动投标金额=账户可用金额-保留余额；)
//				tradeAmount = ArithUtil.sub(availableAmount, keepAvailableAmount);
				//假如可用投资金额一开始大于1000，投标过后小于1000
//				if ("是".equals(smallQuantityInvest)&&tradeAmount.compareTo(new BigDecimal("1000"))<0 && tradeAmount.compareTo(BigDecimal.ZERO)>0 && flag) {
//					Map<String, Object> creditParams = Maps.newHashMap();
//					creditParams.put("maxTerm", 30);
//					creditParams.put("limitedTermMin", 0);
//					creditParams.put("loanUnit", "天"); 
//					flag = false;
//					try {
//						List<Map<String, Object>> creditListByDay = loanManagerService.queryCreditListForJob(creditParams);
//						//有符合的标的
//						if (creditListByDay.size()!=0 || creditListByDay!=null) {
//							self.credit(creditListByDay, custEntity, flag,smallQuantityInvest);
//							continue;
//						}
//					} catch (SLException e) {
//						e.printStackTrace();
//					}
//				}	
			} catch(Exception e){
				log.warn(String.format("交易失败[custId=%s, tradaAmount=%s, remainAmount=%s, transferApplyId=%s, errorMessage=%s]"
						, custEntity.getCustId(), tradeAmount.toPlainString(), remainAmount.toPlainString(), transferMap.get("transferApplyId"), e.getMessage()));
			} finally {
			}
		}// for transfer
	}
	
	@Override
	public void disperse(List<Map<String, Object>> loanList,AutoInvestInfoEntity custEntity) {
		for (int k = 0; k < loanList.size(); k++) {
			Map<String, Object> loanMap = loanList.get(k);
			// 客户账户可用金额 -- 每次投标账户金额发生变化
			BigDecimal availableAmount = accountInfoRepository.findByCustId(custEntity.getCustId()).getAccountAvailableAmount();
			//客户的保留余额
			BigDecimal keepAvailableAmount = custEntity.getKeepAvailableAmount()==null?BigDecimal.ZERO:custEntity.getKeepAvailableAmount();
			// 交易金额(自动投标金额=账户可用金额-保留余额；)
			BigDecimal tradeAmount = ArithUtil.sub(availableAmount, keepAvailableAmount);
			// 可投资金额
			BigDecimal remainAmount = (BigDecimal)loanMap.get("remainAmount");
			// 起投金额  (改为 0 by guoyk 2017/5/17 SLCF-2821)
//			BigDecimal investMinAmount = (BigDecimal)loanMap.get("investMinAmount");
//			BigDecimal investMinAmount = BigDecimal.ZERO;
			// 过滤条件
//			if (tradeAmount.compareTo(remainAmount) >= 0) {// 小于起投金额, 跳过
//				tradeAmount = remainAmount;
//			} else if (tradeAmount.compareTo(investMinAmount) <= 0) {
//				continue ;
//			} else {// 起投金额 <=交易金额 < 剩余可投金额  的情况
//				// 调整投资金额为递增倍数
//				if(tradeAmount.compareTo(investMinAmount) > 0 && !ArithUtil.isDivInt((BigDecimal)loanMap.get("increaseAmount"), ArithUtil.sub(tradeAmount, investMinAmount))){
//					// 递增金额
//					BigDecimal increaseAmount = (BigDecimal)loanMap.get("increaseAmount");
//					// 预计可投资金额
//					BigDecimal expectedAmount = ArithUtil.sub(remainAmount, investMinAmount);
//					
//					// 递增倍数=（min(预计可投资金额，可用余额)-起投）/递增， 大于0 截取整数; 小于0，不可投资；
//					BigDecimal multipleBig = ArithUtil.div(ArithUtil.sub(tradeAmount.min(expectedAmount), investMinAmount), increaseAmount);
//					if(multipleBig.compareTo(BigDecimal.ZERO) < 0){
//						continue ;
//					}
//					int multiple = multipleBig.intValue();
//					// 投资金额=起投金额+递增金额*递增倍数
//					tradeAmount = ArithUtil.add(investMinAmount, ArithUtil.mul(increaseAmount, new BigDecimal(multiple), 0));
//				}
//			}
			
			//过滤条件 ：SLCF-2821 智能投顾不判断起投金额和递增金额 by guoyk 2017/6/1
			if(tradeAmount.compareTo(new BigDecimal("100"))<0){//首先，账户可用投资余额若小于100，直接pass
				continue ;
				//若账户可用投资余额>=100,且小于标的剩余金额
			}else if(tradeAmount.compareTo(new BigDecimal("100"))>=0 && tradeAmount.compareTo(remainAmount)<0){
				// TODO 判断如果购买后，标的还剩余的金额 小于 100 就 控制投标金额，让其标的剩余余额减去100
				BigDecimal remainderAmount = ArithUtil.sub(remainAmount, tradeAmount);
				if (remainderAmount.compareTo(new BigDecimal("100"))<0) {
					tradeAmount = ArithUtil.sub(remainAmount, new BigDecimal("100"));
					//此时 有可能 tradeAmount 小于100
					if (tradeAmount.compareTo(new BigDecimal("100"))<0) {
						continue ;
					}
				} else {
					//对可投金额取整
					tradeAmount = new BigDecimal(String.valueOf(tradeAmount.intValue()));
				}
				
			}else if (tradeAmount.compareTo(remainAmount) >= 0) {//若大于等于标的剩余金额，全投
				tradeAmount = remainAmount;
			}
			
			// 调用购买散标接口
			Map<String, Object> buyDispersionParams = Maps.newHashMap();
			buyDispersionParams.put("disperseId", loanMap.get("disperseId"));
			buyDispersionParams.put("custId", custEntity.getCustId());
			buyDispersionParams.put("tradeAmount", tradeAmount);
			buyDispersionParams.put("appSource", "auto");
			try {
				ResultVo dispersionVo = loanManagerService.buyDispersion(buyDispersionParams);
				if(ResultVo.isSuccess(dispersionVo)){
					log.info(String.format("交易成功[custId=%s, tradaAmount=%s, availableAmount=%s, loanId=%s]"
							, custEntity.getCustId(), tradeAmount.toPlainString(), availableAmount.toPlainString(), loanMap.get("disperseId")));
					
//					String string = String.format("交易成功[custId=%s, tradaAmount=%s, availableAmount=%s, loanId=%s]\r\n"
//							, custEntity.getCustId(), tradeAmount.toPlainString(), availableAmount.toPlainString(), loanMap.get("disperseId"));
//					File file = new File("C:/Users/konyeah/Desktop/test.txt");
//					FileOutputStream outputStream = new FileOutputStream(file, true);
//					outputStream.write(string.getBytes());
//					outputStream.close();
				} else {
					log.warn(String.format("交易失败[custId=%s, tradaAmount=%s, remainAmount=%s, loanId=%s, errorMessage=%s]"
							, custEntity.getCustId(), tradeAmount.toPlainString(), remainAmount.toPlainString(), loanMap.get("disperseId"), dispersionVo.getValue("message").toString()));
				}
				
				//再实时查询投完标后客户的可投余额 by guoyk 2017/5/23
				// 客户账户可用金额 -- 每次投标账户金额发生变化
//				availableAmount = accountInfoRepository.findByCustId(custEntity.getCustId()).getAccountAvailableAmount();
				//客户的保留余额
//				keepAvailableAmount = custEntity.getKeepAvailableAmount()==null?BigDecimal.ZERO:custEntity.getKeepAvailableAmount();
				// 交易金额(自动投标金额=账户可用金额-保留余额；)
//				tradeAmount = ArithUtil.sub(availableAmount, keepAvailableAmount);
				//假如可用投资金额一开始大于1000，投标过后小于1000
//				if ("是".equals(smallQuantityInvest)&&tradeAmount.compareTo(new BigDecimal("1000"))<0 && tradeAmount.compareTo(BigDecimal.ZERO)>0 && flag) {
//					Map<String, Object> loanParams = Maps.newHashMap();
//					loanParams.put("maxTerm", 30);
//					loanParams.put("limitedTermMin", 0);
//					loanParams.put("loanUnit", "天");
//					flag = false;
//					List<Map<String, Object>> disperseListByDay = loanManagerService.queryDisperseListForJob(loanParams);
//					//有符合的标的
//					if (disperseListByDay.size()!=0 || disperseListByDay!=null) {
//						self.disperse(disperseListByDay, custEntity, flag,smallQuantityInvest);
//						continue;
//					}
//				}
			} catch(Exception e){
				log.warn(String.format("交易失败[custId=%s, tradaAmount=%s, remainAmount=%s, loanId=%s, errorMessage=%s]"
						, custEntity.getCustId(), tradeAmount.toPlainString(), remainAmount.toPlainString(), loanMap.get("disperseId"), e.getMessage()));
			} finally {
				
			}
		} // for loan
		
	}
	
	
	@SuppressWarnings("static-access")
	@Override
//	// 成功一笔转一笔不用在这里加事务
//	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo autoTransfer(Map<String, Object> params) throws SLException {
		List<AutoTransferInfoEntity> custList = autoTransferInfoRepository
				.findByOpenStatusOrderByCustPriorityDescOpenDateAsc(Constant.AUTO_INVEST_INFO_OPEN_STATUS_01);
		for (AutoTransferInfoEntity custEntity : custList) {
//			CustInfoEntity custInfo = custInfoRepository.findOne(custEntity.getCustId());
			Map<String, Object> creditParams = Maps.newHashMap();
			creditParams.put("custId", custEntity.getCustId());
			creditParams.put("limitedTerm", custEntity.getLimitedTerm());
			creditParams.put("minTerm", custEntity.getMinTerm());
			creditParams.put("maxTerm", custEntity.getMaxTerm());
			creditParams.put("minYearRate", custEntity.getMinYearRate());
			creditParams.put("maxYearRate", custEntity.getMaxYearRate());
			creditParams.put("repaymentMethod", custEntity.getRepaymentMethod());
			creditParams.put("canTransferProduct", custEntity.getCanTransferProduct());
			// 债权转让列表查询Forjob
			List<Map<String, Object>> creditList = (List<Map<String, Object>>) loanManagerService.queryMyCreditListForJob(creditParams);
out:		for (Map<String, Object> transferMap : creditList) {

				Map<String, Object> param = Maps.newHashMap();
				param.put("holdId", transferMap.get("holdId"));
				param.put("custId", custEntity.getCustId());
				param.put("tradeScale", 1);
				param.put("discountScale", 1);
//				param.put("tradePass", custInfo.getTradePassword());
				param.put("appSource", "auto");
				param.put("userId", Constant.SYSTEM_USER_BACK);
				int maxAttemptTimes = 1;
				do{
					// 债权转让
					try {
						ResultVo transferDebt = loanManagerService.transferDebt(param);
						if (!ResultVo.isSuccess(transferDebt)) {
							log.warn(transferDebt.getValue("message").toString());
							continue out;
						}
						break ;
					} catch (Exception e) {
						log.error(String.format("自动债权转让时失败,custId=%s,holdId=%s,error=%s"
								, param.get("custId"),param.get("holdId"),e.getMessage()));
						e.printStackTrace();
					}
					try {
						Thread.currentThread().sleep(50);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}while(maxAttemptTimes++ < 3);
			}

		}
		return new ResultVo(true, "自动转让执行结束");
	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo updateAutoTransfer(String id)throws SLException{
		AutoInvestInfoEntity custEntity = autoInvestInfoRepository.findOne(id);
		custEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		custEntity.setOpenDate(new Date());
		return new ResultVo(true, "操作成功");
	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo updateISRunAutoInvest()throws SLException{
		int updateISRunAutoInvest = loanManagerRepositoryCustom.updateISRunAutoInvest();
		if (updateISRunAutoInvest==0) {
			return new ResultVo(false, "修改跑过智能投顾标识失败");
		}
		return new ResultVo(true, "修改跑过智能投顾标识成功");
	}
	
	/**
	 * 预约投资-现金贷
	 * 购买项目-根据预约记录
	 */
//	@Override
//	public ResultVo autoReserveInvestForReserveInvestId(final String loanId) {
//		LoanInfoEntity loanInfo = loanInfoRepository.findReserveLoan(loanId);
//		if(loanInfo == null){
//			return new ResultVo(false, "项目不存在");
//		}
//		
//		// 预约记录循环
//		List<String> ids = reserveInvestInfoRepository.findIdsByParamOrderByDate();
//		for (String id : ids) {
//			ResultVo buyResultVo = null;
//			try {
//				Map<String, Object> buyParam = Maps.newHashMap();
//				buyParam.put("loanId", loanId);
//				buyParam.put("reserveInvestId", id); 
//				buyParam.put("appSource", "reserveInvest");
//				// 预约投资
//				buyResultVo = innerClass.buyReserveInvestByReserveInvestId(buyParam);
//				if(!ResultVo.isSuccess(buyResultVo)) {
//					// 逻辑校验不过
//					log.warn("直投预约购买时校验不通过：" + buyResultVo.getValue("message").toString());
//					continue;
//				}
//				@SuppressWarnings("unchecked")
//				Map<String, Object> data = (Map<String, Object>) buyResultVo.getValue("data");
//				if(data != null){
//					// 没满标去做智能投顾
//					if(!(boolean) data.get("fullScale")) {
//						// 不满足可转让
//						LoanInfoEntity loanInfoEntity = loanInfoRepository.findInfoToAutoInvest(loanId);
//						if(loanInfoEntity != null){
//							executor.execute(new Runnable() {
//								@Override
//								public void run() {
//									try {
//										Map<String, Object> autoParam = Maps.newHashMap();
//										autoParam.put("id", loanId);
//										autoParam.put("type", loanId);
//										autoParam.put("isShow", false);
//										
//										self.autoInvestUp(autoParam);
//									} catch (Exception e) {
//										log.warn("预约未满，智能投顾出错！ loanId=" + loanId);
//									}
//								}
//							});
//						}
//					} else if((boolean) data.get("fullScale")) {
//						// 如果满标就放款
//						executor.execute(new Runnable() {
//							@Override
//							public void run() {
//								try {
//									List<String> loanIds = Lists.newArrayList();
//									loanIds.add(loanId);
//									Map<String, Object> grantParam = Maps.newHashMap();
//									grantParam.put("loanIds", loanIds);
//									loanManagerService.batchLending(grantParam);
//								} catch (Exception e) {
//									log.warn("预约满标，放款出错！ loanId=" + loanId);
//								}
//							}
//						});
//					}
//				}
//				
//			} catch (SLException e) {
//				log.warn("直投预约购买时出错！"+e.getMessage());
//				continue;
//			}
//		}
//		return new ResultVo(true, String.format("loanId=%s,预约投资完成", loanId));
//	}
	
	/**
	 * 预约投资-现金贷
	 * 购买项目-根据（同一个客户集成预约记录）
	 */
	@Override
	public ResultVo autoReserveInvestForCustId(final String loanId) {
//		LoanInfoEntity loanInfo = loanInfoRepository.findReserveLoan(loanId);
		String loanInfo = loanInfoRepositoryCustom.findReserveLoan(loanId);
		if(StringUtils.isEmpty(loanInfo)){
			return new ResultVo(false, "项目不存在");
		}
		
		// 预约记录循环
		List<String> custIds = reserveInvestInfoRepository.findReserveAmountForCustId();
		for (String custId : custIds) {
			ResultVo buyResultVo = null;
			try {
				Map<String, Object> buyParam = Maps.newHashMap();
				buyParam.put("loanId", loanId);
				buyParam.put("custId", custId); 
				buyParam.put("appSource", "reserveInvest");
				// 预约投资
				buyResultVo = innerClass.buyReserveInvestByCustId(buyParam);
				if(!ResultVo.isSuccess(buyResultVo)) {
					// 逻辑校验不过
					log.warn("直投预约购买时校验不通过：" + buyResultVo.getValue("message").toString());
					continue;
				}else{// 如果如果满标了就退出循环
					@SuppressWarnings("unchecked")
					Map<String, Object> data = (Map<String, Object>) buyResultVo.getValue("data");
					if(data != null && (boolean) data.get("fullScale")){
						// 如果满标就放款
						// // 使用线程太占用线程池
//						executor.execute(new Runnable() { 
//							@Override
//							public void run() {
//								try {
//									List<String> loanIds = Lists.newArrayList();
//									loanIds.add(loanId);
//									Map<String, Object> grantParam = Maps.newHashMap();
//									grantParam.put("loanIds", loanIds);
//									grantParam.put("userId", "1");
//									loanManagerService.batchLending(grantParam);
//								} catch (Exception e) {
//									log.warn("预约满标，放款出错！ loanId=" + loanId);
//								}
//							}
//						});
						break;
					}
				}
			} catch (SLException e) {
				log.warn("直投预约购买时出错！"+e.getMessage());
				continue;
			}
		}
		
//		// 所有可够买的人购买之后，没满标并符合一定条件去做智能投顾
//		LoanInfoEntity loanInfoEntity = loanInfoRepository.findInfoToAutoInvest(loanId);
//		if(loanInfoEntity != null){
//      // 使用线程太占用线程池
//			executor.execute(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						Map<String, Object> autoParam = Maps.newHashMap();
//						autoParam.put("id", loanId);
//						autoParam.put("type", "优选标");
//						autoParam.put("isShow", false);
//						
//						self.autoInvestUp(autoParam);
//					} catch (Exception e) {
//						log.warn("预约未满，智能投顾出错！ loanId=" + loanId);
//					}
//				}
//			});
//		}
		return new ResultVo(true, String.format("loanId=%s,预约投资完成", loanId));
	}
	
	@Service
	static class InnerClass {
		
		@Autowired
		CustInfoRepository custInfoRepository;
		
		@Autowired
		AccountInfoRepository accountInfoRepository;
		
		@Autowired
		SubAccountInfoRepository subAccountInfoRepository;
		
		@Autowired
		LoanInfoRepository loanInfoRepository;
		
		@Autowired
		InvestInfoRepository investInfoRepository;
		
		@Autowired
		InvestDetailInfoRepository investDetailInfoRepository;
		
		@Autowired
		CustAccountService custAccountService;
		
		@Autowired
		ProjectInvestInfoRepository projectInvestInfoRepository;
		
		@Autowired
		CustRecommendInfoRepository custRecommendInfoRepository;
		
		@Autowired
		FlowNumberService numberService;
		
		@Autowired
		DeviceService deviceService;
		
		@Autowired
		LogInfoEntityRepository logInfoEntityRepository;
		
		@Autowired
		LoanRebateInfoRepository loanRebateInfoRepository;
		
		@Autowired
		InterfaceDetailInfoRepository interfaceDetailInfoRepository;
		
		@Autowired
		ExpandInfoService expandInfoService;
		
		@Autowired
		ReserveInvestInfoRepository reserveInvestInfoRepository;
		
		@Autowired
		ReserveInvestRelationRepository reserveInvestRelationRepository;
		
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo buyReserveInvestByCustId(Map<String, Object> params) throws SLException {
			String loanId = (String) params.get("loanId"); 
			String custId = (String)params.get("custId");
			String appSource = (String)params.get("appSource");
			appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
			
			// check.0 判断用户是否正常
			CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
			if(null == custInfoEntity) {
				return new ResultVo(false, "用户不存在！");
			}
			// check.2 验证项目是否存在且状态为发布中
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
			if(loanInfoEntity == null || !Constant.LOAN_STATUS_05.equals(loanInfoEntity.getLoanStatus())){
				return new ResultVo(false, "该项目不在募集中， 请刷新重试！");
			}
			//新手标校验
			if (Constant.LOAN_INFO_NEWER_FLAG.equals(loanInfoEntity.getNewerFlag())) {
				BigDecimal investCount = investInfoRepository.investCountInfoByCustId(custId);
				if (investCount.compareTo(BigDecimal.ZERO) >0) {
					return new ResultVo(false, "您已投资过新手标！");
				}
			}

			// 客户预约汇总
			List<ReserveInvestInfoEntity> reserveList = reserveInvestInfoRepository.findByCustIdAndReserveStatusOrderByReserveDate(custId, Constant.RESERVE_INVEST_01);
			BigDecimal custAmount = BigDecimal.ZERO;
			for(ReserveInvestInfoEntity reInfo : reserveList){
				custAmount = ArithUtil.add(custAmount, reInfo.getRemainderAmount());
			}
			if(custAmount.compareTo(BigDecimal.ZERO) <= 0){
				return new ResultVo(false, "预约余额为零");
			}
			// 作为交易金额
			BigDecimal tradeAmount = ArithUtil.add(custAmount, BigDecimal.ZERO);
			// check.1 验证投资人账户是否足额
			AccountInfoEntity investorAccount = accountInfoRepository.findByCustId(custId); //客户账户表
			if(tradeAmount.compareTo(investorAccount.getAccountFreezeAmount()) > 0) {
				return new ResultVo(false, "账户冻结金额不足， 请确保数据正确性");
			}
			
			// check.3 验证交易金额是否小于等于项目可投金额
			ProjectInvestInfoEntity projectInvestInfoEntity = projectInvestInfoRepository.findByLoanId(loanId);
			if(projectInvestInfoEntity == null){
				return new ResultVo(false, "项目出借情况数据异常！");
			}
			// 项目剩余可投金额
			BigDecimal remainderAmount = ArithUtil.sub(loanInfoEntity.getLoanAmount(), projectInvestInfoEntity.getAlreadyInvestAmount());
			// 调整交易金额
			if (tradeAmount.compareTo(remainderAmount) >= 0) { // 交易金额大于剩余可投，全投
				tradeAmount = remainderAmount;
			} 
//			else { // 预约金额全投
//				for(ReserveInvestInfoEntity reInfo : reserveList) {
//					reInfo.setReserveStatus(Constant.RESERVE_INVEST_02);
//					reInfo.setAlreadyInvestAmount(ArithUtil.add(reInfo.getAlreadyInvestAmount(), reInfo.getRemainderAmount()));
//					reInfo.setRemainderAmount(BigDecimal.ZERO);
//					reInfo.setBasicModelProperty(custId, false);
//				}
//			}

			// todo list 1 修改可投金额、可投比例、投资人数、已投金额
			int investCounts = investInfoRepository.countByLoanIdAndCustIdAndInvestStatus(loanId,custId,Constant.INVEST_STATUS_INVESTING); // 阅读 todo list 2
			projectInvestInfoEntity.setAlreadyInvestAmount(ArithUtil.add(tradeAmount, projectInvestInfoEntity.getAlreadyInvestAmount()));//已投金额 = 已投金额 + 投资金额
			projectInvestInfoEntity.setAlreadyInvestScale(ArithUtil.div(projectInvestInfoEntity.getAlreadyInvestAmount(), loanInfoEntity.getLoanAmount()));//投资比例
			if(investCounts == 0){
				projectInvestInfoEntity.setAlreadyInvestPeoples(projectInvestInfoEntity.getAlreadyInvestPeoples() + 1);// 投资人+1
			}
			projectInvestInfoEntity.setBasicModelProperty(custId, false);
			
			// todo list 2 新建一笔投资
			/*
			1）投资表新增一条记录
			2）投资详情表新增一条记录
			3）投资人新增分账户，
			4）将投资用户分账户的资金打到借款用户分账户
			若是同一个标的重复投资，则合并为一笔投资
			*/
			// 2.1 投资表
			InvestInfoEntity investInfoEntity = new InvestInfoEntity();
			investInfoEntity.setCustId(custId);
			investInfoEntity.setInvestAmount(tradeAmount);
			investInfoEntity.setInvestStatus(Constant.INVEST_STATUS_INVESTING); //投资中
			investInfoEntity.setInvestMode(Constant.INVEST_METHOD_JOIN); //加入
			investInfoEntity.setInvestDate(DateUtils.formatDate(new Date(), "yyyyMMdd"));
			investInfoEntity.setLoanId(loanId);
			investInfoEntity.setProductId("优先投");// 预约投资的标记
			investInfoEntity.setBasicModelProperty(custId, true);
			
			// 添加客户经理
			CustRecommendInfoEntity custRecommendInfoEntity = custRecommendInfoRepository.findByQuiltCustIdAndRecordStatus(custInfoEntity.getId(), Constant.VALID_STATUS_VALID);
			if(null != custRecommendInfoEntity) 
			investInfoEntity.setCustManagerId(custRecommendInfoEntity.getCustId());
			investInfoEntity = investInfoRepository.save(investInfoEntity);
			
			// 2.2 投资详情
			InvestDetailInfoEntity investDetailInfoEntity = new InvestDetailInfoEntity();
			investDetailInfoEntity.setInvestId(investInfoEntity.getId());
			investDetailInfoEntity.setTradeNo(numberService.generateLoanContractNumber());
			investDetailInfoEntity.setInvestAmount(tradeAmount);
			investDetailInfoEntity.setInvestSource(appSource);
			investDetailInfoEntity.setBasicModelProperty(custId, true);
			investDetailInfoEntity = investDetailInfoRepository.save(investDetailInfoEntity);
			
			// 投资关系建立
			// 预约金额小于等于剩余可投金额，预约金额全投，预约成功
			if(custAmount.compareTo(remainderAmount) <= 0){
				String batchNo = numberService.generateReserveBatchNumber();
				ReserveInvestRelationEntity relation;
				for(ReserveInvestInfoEntity reInfo : reserveList) {
					// 为零的记录不记录关系表（1.为零，2.2剩余200，第一笔不用记录，他在其它大方为零了。）
					if(reInfo.getRemainderAmount().compareTo(BigDecimal.ZERO) > 0){
						relation = new ReserveInvestRelationEntity();
						relation.setBasicModelProperty(custId, true);
						relation.setReserveId(reInfo.getId());
						relation.setInvestId(investInfoEntity.getId());
						relation.setTradeAmount(ArithUtil.add(reInfo.getRemainderAmount(), BigDecimal.ZERO));
						reserveInvestRelationRepository.save(relation);
					}

					reInfo.setBatchNo(batchNo);
					reInfo.setReserveStatus(Constant.RESERVE_INVEST_02);
					reInfo.setAlreadyInvestAmount(ArithUtil.add(reInfo.getAlreadyInvestAmount(), reInfo.getRemainderAmount()));
					reInfo.setRemainderAmount(ArithUtil.sub(reInfo.getRemainderAmount(), reInfo.getRemainderAmount()));
					reInfo.setBasicModelProperty(custId, false);
				}
			} else {// 排队预约待投总金额大于投资金额，还有剩余待投金额
				// 项目剩余可投金额
				BigDecimal tradeAmountBak =  ArithUtil.add(tradeAmount, BigDecimal.ZERO);
				ReserveInvestRelationEntity relation;
				for(ReserveInvestInfoEntity reInfo : reserveList) {
					if(reInfo.getRemainderAmount().compareTo(BigDecimal.ZERO) <= 0){
						continue;
					}
					// 某笔待投
					BigDecimal reserveRemain = ArithUtil.add(reInfo.getRemainderAmount(), BigDecimal.ZERO);
					if(reserveRemain.compareTo(tradeAmountBak) >= 0){
						reInfo.setAlreadyInvestAmount(ArithUtil.add(reInfo.getAlreadyInvestAmount(), tradeAmountBak));
						reInfo.setRemainderAmount(ArithUtil.sub(reInfo.getRemainderAmount(), tradeAmountBak));
						reInfo.setBasicModelProperty(custId, false);
						
						relation = new ReserveInvestRelationEntity();
						relation.setBasicModelProperty(custId, true);
						relation.setReserveId(reInfo.getId());
						relation.setInvestId(investInfoEntity.getId());
						relation.setTradeAmount(ArithUtil.add(tradeAmountBak, BigDecimal.ZERO));

						tradeAmountBak = ArithUtil.sub(tradeAmountBak, tradeAmountBak); // == 0
					} else { // reserveRemain < tradeAmountBak
						reInfo.setAlreadyInvestAmount(ArithUtil.add(reInfo.getAlreadyInvestAmount(), reserveRemain));
						reInfo.setRemainderAmount(ArithUtil.sub(reInfo.getRemainderAmount(), reserveRemain));// == 0
						reInfo.setBasicModelProperty(custId, false);
						
						relation = new ReserveInvestRelationEntity();
						relation.setBasicModelProperty(custId, true);
						relation.setReserveId(reInfo.getId());
						relation.setInvestId(investInfoEntity.getId());
						relation.setTradeAmount(ArithUtil.add(reserveRemain, BigDecimal.ZERO));
						
						tradeAmountBak = ArithUtil.sub(tradeAmountBak, reserveRemain);
					}
					reserveInvestRelationRepository.save(relation);
					
					if(tradeAmountBak.compareTo(BigDecimal.ZERO) <= 0) {
						break;
					}
				} 
			}

			// 2.3  添加分账、分账户流水
			// 2.3.1 添加投资人分账
			SubAccountInfoEntity investorSubAccount = new SubAccountInfoEntity();
			investorSubAccount.setBasicModelProperty(custId, true);
			investorSubAccount.setCustId(custId);
			investorSubAccount.setAccountId(investorAccount.getId());
			investorSubAccount.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
			investorSubAccount.setRelatePrimary(investInfoEntity.getId());
			investorSubAccount.setSubAccountNo(numberService.generateCustomerNumber());
			investorSubAccount.setAccountTotalValue(BigDecimal.ZERO);
			investorSubAccount.setAccountFreezeValue(BigDecimal.ZERO);
			investorSubAccount.setAccountAvailableValue(BigDecimal.ZERO);
			investorSubAccount.setAccountAmount(tradeAmount);
			investorSubAccount.setDeviationAmount(BigDecimal.ZERO);
			investorSubAccount = subAccountInfoRepository.save(investorSubAccount);
			
			String reqeustNo = numberService.generateTradeBatchNumber();
			
			// 2.3.2.1 解冻预约投资金额
			custAccountService.updateAccount(investorAccount, null, null, 
					null, "8", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN_FOR_RESEVER, 
					reqeustNo, tradeAmount, SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN_FOR_RESEVER, 
					Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),  
					"预约出借解冻", custId);
			
			// 2.3.2 投资人主账户金额-加入金额、添加流水 更新客户主账户（投资人主账户——>投资人分账户）
			custAccountService.updateAccount(investorAccount, null, null, 
					investorSubAccount, "2", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN_FOR_RESEVER, 
					reqeustNo, tradeAmount, SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN_FOR_RESEVER, 
					Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),  
					String.format("优选项目预约出借[%s-%s]", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()), custId);
			
			// 2.3.4 投资人分账户出
			// 借款分账户
			SubAccountInfoEntity loanerSubAccount = subAccountInfoRepository.findByRelatePrimary(loanInfoEntity.getId());
			custAccountService.updateAccount(null, investorSubAccount, null, 
					loanerSubAccount, "4", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN_FOR_RESEVER, 
					reqeustNo, tradeAmount, SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN_FOR_RESEVER, 
					Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),  
					String.format("优选项目预约出借[%s-%s]", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()), custId);
			
			// 3 记录设备信息(优选)
			Map<String, Object> deviceParams = Maps.newConcurrentMap();
			deviceParams.putAll(params);
			deviceParams.put("relateType", Constant.TABLE_BAO_T_INVEST_INFO);
			deviceParams.put("relatePrimary", investInfoEntity.getId());
			deviceParams.put("tradeType", Constant.OPERATION_TYPE_67);
			deviceParams.put("userId", custId);
			deviceService.saveUserDevice(deviceParams);
			
			// 4 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
			logInfoEntity.setRelatePrimary(investInfoEntity.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_67);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(custId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("%s优先项目预约出借，出借金额%s", custInfoEntity.getLoginName(), tradeAmount.toString()));
			logInfoEntity.setBasicModelProperty(custId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			boolean fullScale = false;
			Map<String, Object> flag = Maps.newHashMap();
			flag.put("fullScale", false);
			flag.put("isNewerFlag", false);
			// 剩余可投金额为0，改项目为满标复核	
			if(ArithUtil.sub(remainderAmount, tradeAmount).compareTo(BigDecimal.ZERO) == 0) {
				boolean isNewerFlag = false;
				flag = Maps.newHashMap();
				if (Constant.LOAN_INFO_NEWER_FLAG.equals(loanInfoEntity.getNewerFlag())) {
					isNewerFlag = true;
				}
				loanInfoEntity.setLoanStatus(Constant.LOAN_STATUS_06);
				loanInfoEntity.setBasicModelProperty(custId, false);
				fullScale = true;
				flag.put("fullScale", fullScale);
				flag.put("isNewerFlag", isNewerFlag);
				
				if(Constant.DEBT_SOURCE_XCJF.equals(loanInfoEntity.getCompanyName())) {
					String repaymentMethod = loanInfoEntity.getRepaymentMethod();
					Integer loanTerm = Integer.parseInt(loanInfoEntity.getLoanTerm().toString());
					String loanType =  loanInfoEntity.getLoanType();
					String loanUnit =  loanInfoEntity.getLoanUnit();
					// 判断借款类型、还款方式、还款期限在项目折年系数表中是否为商务（Flag标识），若为商务则表示需要通知。
					if(needToNotifyBiz(repaymentMethod, loanTerm, loanType, loanUnit)
							|| Constant.PUSH_FLAG_YES.equals(loanInfoEntity.getPushFlag())){
						// 通知雪橙满标
						InterfaceDetailInfoEntity interfaceDetailInfoEntity = 
								interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType(loanInfoEntity.getCompanyName(), Constant.NOTIFY_TYPE_LOAN_STATUS);
						if (interfaceDetailInfoEntity != null) {
							Map<String, Object> map = Maps.newHashMap();
							map.put("relateType", Constant.TABLE_BAO_T_LOAN_INFO);
							map.put("relatePrimary", loanId);
							map.put("tradeCode", numberService.generateTradeNumber());
							map.put("innerTradeCode", numberService.generateOpenServiceTradeNumber());
							map.put("interfaceDetailInfoEntity", interfaceDetailInfoEntity);
							map.put("userId", Constant.SYSTEM_USER_BACK);
							map.put("memo", Constant.OPERATION_TYPE_84);
							expandInfoService.saveExpandInfo(map);
						}
					}
				}
			}
			
			return new ResultVo(true, "购买操作成功", flag);
		}
		
		/**
		 * 预约投资
		 */
//		@Transactional(readOnly = false, rollbackFor = SLException.class)
//		public ResultVo buyReserveInvestByReserveInvestId(Map<String, Object> params) throws SLException {
//			String loanId = (String) params.get("loanId"); 
//			String reserveInvestId = (String)params.get("reserveInvestId");
//			String appSource = (String)params.get("appSource");
//			appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
//			
//			ReserveInvestInfoEntity reInfo = reserveInvestInfoRepository.findOne(reserveInvestId);
//			if(reInfo == null || reInfo.getRemainderAmount().compareTo(BigDecimal.ZERO) <= 0 || !Constant.RESERVE_INVEST_01.equals(reInfo.getReserveStatus())){
//				return new ResultVo(false, "该预约不错在或不在处理中");
//			}
//			String custId = reInfo.getCustId();
//			BigDecimal tradeAmount = reInfo.getRemainderAmount();
//			
//			// check.0 判断用户是否正常
//			CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
//			if(null == custInfoEntity) {
//				return new ResultVo(false, "用户不存在！");
//			}
//			
//			// check.1 验证投资人账户是否足额
//			AccountInfoEntity investorAccount = accountInfoRepository.findByCustId(custId); //客户账户表
//			if(tradeAmount.compareTo(investorAccount.getAccountFreezeAmount()) > 0) {
//				return new ResultVo(false, "账户冻结金额不足， 请确保数据正确性");
//			}
//			
//			// check.2 验证项目是否存在且状态为发布中
//			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
//			if(loanInfoEntity == null || !Constant.LOAN_STATUS_05.equals(loanInfoEntity.getLoanStatus())){
//				return new ResultVo(false, "该项目不在募集中， 请刷新重试！");
//			}
//			//新手标校验
//			if (Constant.LOAN_INFO_NEWER_FLAG.equals(loanInfoEntity.getNewerFlag())) {
//				BigDecimal investCount = investInfoRepository.investCountInfoByCustId(custId);
//				if (investCount.compareTo(BigDecimal.ZERO) >0) {
//					return new ResultVo(false, "您已投资过新手标！");
//				}
//			}
//			// check.3 验证交易金额是否小于等于项目可投金额
//			ProjectInvestInfoEntity projectInvestInfoEntity = projectInvestInfoRepository.findByLoanId(loanId);
//			if(projectInvestInfoEntity == null){
//				return new ResultVo(false, "项目投资情况数据异常！");
//			}
//			// 项目剩余可投金额
//			BigDecimal remainderAmount = ArithUtil.sub(loanInfoEntity.getLoanAmount(), projectInvestInfoEntity.getAlreadyInvestAmount());
//			// 调整交易金额
//			if (tradeAmount.compareTo(remainderAmount) >= 0) { // 交易金额大于剩余可投，全投
//				tradeAmount = remainderAmount;
//			} else { // 预约金额全投
//				reInfo.setReserveStatus(Constant.RESERVE_INVEST_02);
//			}
//			reInfo.setReserveDate(new Date());
//			reInfo.setAlreadyInvestAmount(ArithUtil.add(reInfo.getAlreadyInvestAmount(), tradeAmount));
//			reInfo.setRemainderAmount(ArithUtil.sub(reInfo.getRemainderAmount(), tradeAmount));
//			reInfo.setBasicModelProperty(custId, false);
//			// 交易金额等于剩余可投金额表示最后一笔投资，不受起投金额、投资递增金额等条件制约
//			remainderAmount = ArithUtil.sub(remainderAmount, tradeAmount);
//			
////		else if (tradeAmount.compareTo(investMinAmount) <= 0) {
////			continue ;
////		} else {// 起投金额 <=交易金额 < 剩余可投金额  的情况
////			// 调整投资金额为递增倍数
////			if(tradeAmount.compareTo(investMinAmount) > 0 && !ArithUtil.isDivInt((BigDecimal)loanMap.get("increaseAmount"), ArithUtil.sub(tradeAmount, investMinAmount))){
////				// 递增金额
////				BigDecimal increaseAmount = (BigDecimal)loanMap.get("increaseAmount");
////				// 预计可投资金额
////				BigDecimal expectedAmount = ArithUtil.sub(remainAmount, investMinAmount);
////				
////				// 递增倍数=（min(预计可投资金额，可用余额)-起投）/递增， 大于0 截取整数; 小于0，不可投资；
////				BigDecimal multipleBig = ArithUtil.div(ArithUtil.sub(tradeAmount.min(expectedAmount), investMinAmount), increaseAmount);
////				if(multipleBig.compareTo(BigDecimal.ZERO) < 0){
////					continue ;
////				}
////				int multiple = multipleBig.intValue();
////				// 投资金额=起投金额+递增金额*递增倍数
////				tradeAmount = ArithUtil.add(investMinAmount, ArithUtil.mul(increaseAmount, new BigDecimal(multiple), 0));
////			}
////		}
////			// 交易金额等于剩余可投金额表示最后一笔投资，不受起投金额、投资递增金额等条件制约
////			remainderAmount = ArithUtil.sub(remainderAmount, tradeAmount);
////			
////			BigDecimal investMinAmount = loanInfoEntity.getInvestMinAmount();
////			if(remainderAmount.compareTo(BigDecimal.ZERO) != 0) { 
////				// check.3 验证投资金额是否大于起投金额
////				if(tradeAmount.compareTo(investMinAmount) < 0){
////					return new ResultVo(false, "投资金额不能小于起投金额！");
////				}
////				// check.4 验证投资金额是否是递增倍数
////				//  by guoyk 2017/6/1 SLCF-2821 如果是智能投顾,不判断递增金额
////				if (!"auto".equals(appSource)) {
////					if(tradeAmount.compareTo(investMinAmount) > 0 
////							&& !ArithUtil.isDivInt(loanInfoEntity.getIncreaseAmount(), ArithUtil.sub(tradeAmount, investMinAmount))){
////						return new ResultVo(false, "投资金额必须是起投金额加递增金额整数倍！");
////					}
////				}
////				
////				// check.6 投资后剩余金额小于投标金额，需要补满
////				if(remainderAmount.compareTo(investMinAmount) < 0){
////					return new ResultVo(false, "投资后剩余可投金额小于起投金额，您必须购买所有剩余可投金额!");
////				}
////				//add by fengyl 新手标checek 投资后剩余可投金额大于起投金额,判断投资金额是否小于投资限额  
////				if (Constant.LOAN_INFO_NEWER_FLAG.equals(loanInfoEntity.getNewerFlag())&& tradeAmount.compareTo(loanInfoEntity.getInvestMaxAmount()) > 0
////						&&remainderAmount.compareTo(investMinAmount) >= 0) {
////					return new ResultVo(false, String.format("每人限购%s元！", loanInfoEntity.getInvestMaxAmount()));
////				}
////			}
//
//			// todo list 1 修改可投金额、可投比例、投资人数、已投金额
//			int investCounts = investInfoRepository.countByLoanIdAndCustIdAndInvestStatus(loanId,custId,Constant.INVEST_STATUS_INVESTING); // 阅读 todo list 2
//			projectInvestInfoEntity.setAlreadyInvestAmount(ArithUtil.add(tradeAmount, projectInvestInfoEntity.getAlreadyInvestAmount()));//已投金额 = 已投金额 + 投资金额
//			projectInvestInfoEntity.setAlreadyInvestScale(ArithUtil.div(projectInvestInfoEntity.getAlreadyInvestAmount(), loanInfoEntity.getLoanAmount()));//投资比例
//			if(investCounts == 0){
//				projectInvestInfoEntity.setAlreadyInvestPeoples(projectInvestInfoEntity.getAlreadyInvestPeoples() + 1);// 投资人+1
//			}
//			projectInvestInfoEntity.setBasicModelProperty(custId, false);
//			
//			// todo list 2 新建一笔投资
//			/*
//			1）投资表新增一条记录
//			2）投资详情表新增一条记录
//			3）投资人新增分账户，
//			4）将投资用户分账户的资金打到借款用户分账户
//			若是同一个标的重复投资，则合并为一笔投资
//			*/
//			// 2.1 投资表
//			InvestInfoEntity investInfoEntity = new InvestInfoEntity();
//			investInfoEntity.setCustId(custId);
//			investInfoEntity.setInvestAmount(tradeAmount);
//			investInfoEntity.setInvestStatus(Constant.INVEST_STATUS_INVESTING); //投资中
//			investInfoEntity.setInvestMode(Constant.INVEST_METHOD_JOIN); //加入
//			investInfoEntity.setInvestDate(DateUtils.formatDate(new Date(), "yyyyMMdd"));
//			//investInfoEntity.setExpireDate(DateUtilSL.print(loanInfoEntity.getInvestEndDate(), "yyyyMMdd"));
//			investInfoEntity.setLoanId(loanId);
////			investInfoEntity.setProductId(reserveInvestId);// 预约投资的标记
//			investInfoEntity.setProductId("优先投");
//			investInfoEntity.setBasicModelProperty(custId, true);
//			
//			// 添加客户经理
//			CustRecommendInfoEntity custRecommendInfoEntity = custRecommendInfoRepository.findByQuiltCustIdAndRecordStatus(custInfoEntity.getId(), Constant.VALID_STATUS_VALID);
//			if(null != custRecommendInfoEntity) 
//			investInfoEntity.setCustManagerId(custRecommendInfoEntity.getCustId());
//			investInfoEntity = investInfoRepository.save(investInfoEntity);
//			
//			// 2.2 投资详情
//			InvestDetailInfoEntity investDetailInfoEntity = new InvestDetailInfoEntity();
//			investDetailInfoEntity.setInvestId(investInfoEntity.getId());
//			investDetailInfoEntity.setTradeNo(numberService.generateLoanContractNumber());
//			investDetailInfoEntity.setInvestAmount(tradeAmount);
//			investDetailInfoEntity.setInvestSource(appSource);
//			investDetailInfoEntity.setBasicModelProperty(custId, true);
//			investDetailInfoEntity = investDetailInfoRepository.save(investDetailInfoEntity);
//			
//			// 2.3  添加分账、分账户流水
//			// 2.3.1 添加投资人分账
//			SubAccountInfoEntity investorSubAccount = new SubAccountInfoEntity();
//			investorSubAccount.setBasicModelProperty(custId, true);
//			investorSubAccount.setCustId(custId);
//			investorSubAccount.setAccountId(investorAccount.getId());
//			investorSubAccount.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
//			investorSubAccount.setRelatePrimary(investInfoEntity.getId());
//			investorSubAccount.setSubAccountNo(numberService.generateCustomerNumber());
//			investorSubAccount.setAccountTotalValue(BigDecimal.ZERO);
//			investorSubAccount.setAccountFreezeValue(BigDecimal.ZERO);
//			investorSubAccount.setAccountAvailableValue(BigDecimal.ZERO);
//			investorSubAccount.setAccountAmount(tradeAmount);
//			investorSubAccount.setDeviationAmount(BigDecimal.ZERO);
//			investorSubAccount = subAccountInfoRepository.save(investorSubAccount);
//			
//			String reqeustNo = numberService.generateTradeBatchNumber();
//			
//			// 2.3.2.1 解冻预约投资金额
//			custAccountService.updateAccount(investorAccount, null, null, 
//					null, "8", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN, 
//					reqeustNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN, 
//					Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),  
//					String.format("购买优选项目[%s-%s]", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()), custId);
//			
//			// 2.3.2 投资人主账户金额-加入金额、添加流水 更新客户主账户（投资人主账户——>投资人分账户）
//			custAccountService.updateAccount(investorAccount, null, null, 
//					investorSubAccount, "2", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN, 
//					reqeustNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN, 
//					Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),  
//					String.format("购买优选项目[%s-%s]", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()), custId);
//			
//			// 2.3.4 投资人分账户出
//			// 借款分账户
//			SubAccountInfoEntity loanerSubAccount = subAccountInfoRepository.findByRelatePrimary(loanInfoEntity.getId());
//			custAccountService.updateAccount(null, investorSubAccount, null, 
//					loanerSubAccount, "4", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN, 
//					reqeustNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN, 
//					Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),  
//					String.format("购买优选项目[%s-%s]", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()), custId);
//			
//			// 3 记录设备信息(优选)
//			Map<String, Object> deviceParams = Maps.newConcurrentMap();
//			deviceParams.putAll(params);
//			deviceParams.put("relateType", Constant.TABLE_BAO_T_INVEST_INFO);
//			deviceParams.put("relatePrimary", investInfoEntity.getId());
//			deviceParams.put("tradeType", Constant.OPERATION_TYPE_67);
//			deviceParams.put("userId", custId);
//			deviceService.saveUserDevice(deviceParams);
//			
//			// 4 记录日志
//			LogInfoEntity logInfoEntity = new LogInfoEntity();
//			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
//			logInfoEntity.setRelatePrimary(investInfoEntity.getId());
//			logInfoEntity.setLogType(Constant.OPERATION_TYPE_67);
//			logInfoEntity.setOperDesc("");
//			logInfoEntity.setOperPerson(custId);
//			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
//			logInfoEntity.setMemo(String.format("%s购买优选项目，投资金额%s", custInfoEntity.getLoginName(), tradeAmount.toString()));
//			logInfoEntity.setBasicModelProperty(custId, true);
//			logInfoEntityRepository.save(logInfoEntity);
//			
//			boolean fullScale = false;
//			Map<String, Object> flag = Maps.newHashMap();
//			flag.put("fullScale", false);
//			flag.put("isNewerFlag", false);
//			// 剩余可投金额为0，改项目为满标复核	
//			if(remainderAmount.compareTo(BigDecimal.ZERO) == 0) {
//				boolean isNewerFlag = false;
//				flag = Maps.newHashMap();
//				if (Constant.LOAN_INFO_NEWER_FLAG.equals(loanInfoEntity.getNewerFlag())) {
//					isNewerFlag = true;
//				}
//				loanInfoEntity.setLoanStatus(Constant.LOAN_STATUS_06);
//				loanInfoEntity.setBasicModelProperty(custId, false);
//				fullScale = true;
//				flag.put("fullScale", fullScale);
//				flag.put("isNewerFlag", isNewerFlag);
//				
//				if(Constant.DEBT_SOURCE_XCJF.equals(loanInfoEntity.getCompanyName())) {
//					String repaymentMethod = loanInfoEntity.getRepaymentMethod();
//					Integer loanTerm = Integer.parseInt(loanInfoEntity.getLoanTerm().toString());
//					String loanType =  loanInfoEntity.getLoanType();
//					String loanUnit =  loanInfoEntity.getLoanUnit();
//					// 判断借款类型、还款方式、还款期限在项目折年系数表中是否为商务（Flag标识），若为商务则表示需要通知。
//					if(needToNotifyBiz(repaymentMethod, loanTerm, loanType, loanUnit)
//							|| Constant.PUSH_FLAG_YES.equals(loanInfoEntity.getPushFlag())){
//						// 通知雪橙满标
//						InterfaceDetailInfoEntity interfaceDetailInfoEntity = 
//								interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType(loanInfoEntity.getCompanyName(), Constant.NOTIFY_TYPE_LOAN_STATUS);
//						if (interfaceDetailInfoEntity != null) {
//							Map<String, Object> map = Maps.newHashMap();
//							map.put("relateType", Constant.TABLE_BAO_T_LOAN_INFO);
//							map.put("relatePrimary", loanId);
//							map.put("tradeCode", numberService.generateTradeNumber());
//							map.put("innerTradeCode", numberService.generateOpenServiceTradeNumber());
//							map.put("interfaceDetailInfoEntity", interfaceDetailInfoEntity);
//							map.put("userId", Constant.SYSTEM_USER_BACK);
//							map.put("memo", Constant.OPERATION_TYPE_84);
//							expandInfoService.saveExpandInfo(map);
//						}
//					}
//				}
//			}
//			
//			return new ResultVo(true, "购买操作成功", flag);
//		}
		
		/**
		 * 判断借款类型、还款方式、还款期限在项目折年系数表中是否为商务（Flag标识），若为商务则表示需要通知。
		 */
		private boolean needToNotifyBiz(String repaymentMethod, Integer loanTerm, String loanType, String loanUnit){
			LoanRebateInfoEntity loanRebateInfoEntity = loanRebateInfoRepository.findByRepaymentMethodAndLoanTermAndProductTypeAndLoanUnit(repaymentMethod, loanTerm, loanType, loanUnit);
			if(loanRebateInfoEntity != null && Constant.LOAN_FLAG_01.equals(loanRebateInfoEntity.getFlag())) {
				return true;
			}
			return false;
		}
	}
	
	/**
	 * 智能投顾.改(针对单标)
	 * @author  guoyk
	 * @date    2017-6-16 
	 * @param params
     *      <tt>limitedTerm       :String:最大期限</tt><br>
     *      <tt>limitedTermMin    :String:最小期限</tt><br>
     *      <tt>limitedYearRateMax:String:最大年化利率</tt><br>
     *      <tt>limitedYearRate   :String:最小年化利率</tt><br>
     *      <tt>loanUnit          :String:期限单位</tt><br>
     *      <tt>repaymentMethod   :String:还款方式</tt><br>
     *      <tt>canInvestProduct  :String:项目类型</tt><br>
     *      <tt>type          :String:标的类型</tt><br>
	 * @return
	 */
	public ResultVo autoInvestUp(Map<String, Object> params) throws SLException {
		
		Date startDate = new Date();
		// TODO Auto-generated method stub
		String type = params.get("type").toString();
		final String id = params.get("id").toString();
		boolean isShow = (boolean)params.get("isShow");
		//根据标的信息匹配用户
		Map<String, Object> paramMap = Maps.newHashMap();
		//如果传来的是优选标,查询标的详细信息
		if ("优选标".equals(type)) {
			LoanInfoEntity loanInfo = loanInfoRepository.findOne(id);
			LoanDetailInfoEntity loanDetailInfo = loanDetailInfoRepository.findByLoanId(id);
			paramMap.put("loanTerm", CommonUtils.emptyToString(loanInfo.getLoanTerm()));
			paramMap.put("loanUnit", CommonUtils.emptyToString(loanInfo.getLoanUnit()));
			paramMap.put("yearRate", CommonUtils.emptyToString(ArithUtil.formatScale2(loanDetailInfo.getYearIrr())));
			paramMap.put("repaymentMethod", CommonUtils.emptyToString(loanInfo.getRepaymentMethod()));
			paramMap.put("seatTerm", CommonUtils.emptyToString(loanInfo.getSeatTerm()));
			paramMap.put("type", "优选标");
		}
		//如果传来的是转让标,查询标的详细信息
		if ("转让标".equals(type)) {
			Map<String, Object> resultMap = loanInfoRepositoryCustom.queryLoanInfoByApplyId(id);
			paramMap.put("loanTerm", CommonUtils.emptyToString(resultMap.get("loanTerm")));
			paramMap.put("loanUnit", CommonUtils.emptyToString(resultMap.get("loanUnit")));
			paramMap.put("yearRate", CommonUtils.emptyToString(resultMap.get("yearRate")));
			paramMap.put("repaymentMethod", CommonUtils.emptyToString(resultMap.get("repaymentMethod")));
			paramMap.put("seatTerm", CommonUtils.emptyToString(resultMap.get("seatTerm")));
			paramMap.put("type", "转让标");
		}
		
		List<Map<String, Object>> autoInvestInfoList = autoInvestRepositoryCustom.queryAutoInvestInfoForSZD(paramMap);
		//根据标的 找人，记录一轮匹配有多少人
		int f = 0;
		//循环所有的用户
		for (int i = 0; i < autoInvestInfoList.size(); i++) {
			String autoInvestId = autoInvestInfoList.get(i).get("id").toString();
			//实时查询 该客户是否已经修改或者关闭智能投顾，取最新的自动投标实体 
			AutoInvestInfoEntity custEntity = autoInvestInfoRepository.findOne(autoInvestId);
			// 实时判断此时的客户是否满足标的的信息 把 autoInvestId，openStatus = 开启， 当参数 
			paramMap.put("autoInvestId", autoInvestId);
			List<Map<String, Object>> autoInvestInfoCurrentList = autoInvestRepositoryCustom.queryAutoInvestInfoCurrent(paramMap);
			if (autoInvestInfoCurrentList == null || autoInvestInfoCurrentList.size() == 0 ) {
				continue;
			}
//			if (custEntity!=null && Constant.AUTO_INVEST_INFO_OPEN_STATUS_02.equals(custEntity.getOpenStatus())) {
//				continue;
//			}
			//是否开启小额复投
//			String smallQuantityInvest = custEntity.getSmallQuantityInvest();
//			//复投类型(有可能为NUll)
//			String canInvestProduct = custEntity.getCanInvestProduct();
			AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(custEntity.getCustId());
			if (accountInfoEntity==null) {
				log.warn("客户的账户查询出错，custId="+custEntity.getCustId());
				continue;
			}
			// 客户账户可用金额
			BigDecimal availableAmount = accountInfoEntity.getAccountAvailableAmount();
			//客户保留余额
			BigDecimal keepAvailableAmount = custEntity.getKeepAvailableAmount()==null?BigDecimal.ZERO:custEntity.getKeepAvailableAmount();
			// 交易金额
			BigDecimal tradeAmount = ArithUtil.sub(availableAmount, keepAvailableAmount);
			// 余额为0, next
			if(accountInfoEntity == null || accountInfoEntity.getAccountAvailableAmount().compareTo(BigDecimal.ZERO) <= 0){
				try {
					self.updateAutoTransfer(autoInvestId);
				} catch (Exception e) {
					log.warn(String.format("修改智能投顾开启时间出错，主键id是：%s", autoInvestId));
					e.printStackTrace();
				}
				continue ;
			}
			//用户可投资余额<=0 next 
			if(tradeAmount.compareTo(BigDecimal.ZERO) <= 0){
				try {
					self.updateAutoTransfer(autoInvestId);
				} catch (Exception e) {
					log.warn(String.format("修改智能投顾开启时间出错，主键id是：%s", autoInvestId));
					e.printStackTrace();
				}
				continue ;
			}
			//根据传来的参数loanType进行判断是转让标还是优选标
			if ("优选标".equals(type)) {
				//查询该标的剩余可投金额
				BigDecimal remainAmount = loanInfoRepository.findRemainAmountForDisperseByLoanId(id);
				
				//过滤条件 ：SLCF-2821 智能投顾不判断起投金额和递增金额 by guoyk 2017/6/1
				if(tradeAmount.compareTo(new BigDecimal("100"))<0){//首先，账户可用投资余额若小于100，直接pass
					continue ;
					//若账户可用投资余额>=100,且小于标的剩余金额
				}else if(tradeAmount.compareTo(new BigDecimal("100"))>=0 && tradeAmount.compareTo(remainAmount)<0){
					// TODO 判断如果购买后，标的还剩余的金额 小于 100 就 控制投标金额，让其标的剩余余额减去100
					BigDecimal remainderAmount = ArithUtil.sub(remainAmount, tradeAmount);
					if (remainderAmount.compareTo(new BigDecimal("100"))<0) {
						tradeAmount = ArithUtil.sub(remainAmount, new BigDecimal("100"));
						//此时 有可能 tradeAmount 小于100
						if (tradeAmount.compareTo(new BigDecimal("100"))<0) {
							continue ;
						}
					}else {
						//对可投金额取整
						tradeAmount = new BigDecimal(String.valueOf(tradeAmount.intValue()));
					}
					
				}else if (tradeAmount.compareTo(remainAmount) >= 0) {//若大于标的剩余金额，全投
					tradeAmount = remainAmount;
				}
				
				//调用购买优选项目接口
				Map<String, Object> buyDispersionParams = Maps.newHashMap();
				buyDispersionParams.put("disperseId", id);
				buyDispersionParams.put("custId", custEntity.getCustId());
				buyDispersionParams.put("tradeAmount", tradeAmount);
				buyDispersionParams.put("appSource", "auto");
				try {
					ResultVo dispersionVo = loanManagerService.buyDispersion(buyDispersionParams);
					if(ResultVo.isSuccess(dispersionVo)){
						f++;//购买成功，记录一人
						log.info(String.format("交易成功[custId=%s, tradaAmount=%s, availableAmount=%s, loanId=%s]"
								, custEntity.getCustId(), tradeAmount.toPlainString(), availableAmount.toPlainString(), id));
						//如果购买成功，并且满标
						@SuppressWarnings("unchecked")
						Map<String, Object> flagMap =(Map<String, Object>)dispersionVo.getValue("data");
						if((boolean)flagMap.get("fullScale")){
							//并且该标的是善真贷同类产品
							ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName("shanLinCaiFuSet", "可投产品");
							LoanInfoEntity loanInfo = loanInfoRepository.findOne(id);
							if (paramEntity.getValue().contains(loanInfo.getLoanType())) {
								 // 如果满标就放款
					            executor.execute(new Runnable() {
					              @Override
					              public void run() {
					                try {
					                  List<String> loanIds = Lists.newArrayList();
					                  loanIds.add(id);
					                  Map<String, Object> grantParam = Maps.newHashMap();
					                  grantParam.put("loanIds", loanIds);
					                  loanManagerService.batchLending(grantParam);
					                } catch (Exception e) {
					                  log.warn("智能投顾善真贷同类产品出错！ loanId=" + id);
					                }
					              }
					            });
							}
							break;//如果满标，则跳出循环
						}
						
					} else {
						log.warn(String.format("交易失败[custId=%s, tradaAmount=%s, remainAmount=%s, loanId=%s, errorMessage=%s]"
								, custEntity.getCustId(), tradeAmount.toPlainString(), remainAmount.toPlainString(), id, dispersionVo.getValue("message").toString()));
					}
				} catch(Exception e){
					log.warn(String.format("交易失败[custId=%s, tradaAmount=%s, remainAmount=%s, loanId=%s, errorMessage=%s]"
							, custEntity.getCustId(), tradeAmount.toPlainString(), remainAmount.toPlainString(), id, e.getMessage()));
				} finally {
					
				}
			}
			if ("转让标".equals(type)) {
				//查询该标的剩余可投金额
				BigDecimal remainAmount = loanInfoRepository.findRemainAmountForCreditByApplyId(id);

				//过滤条件 ：SLCF-2821 智能投顾不判断起投金额和递增金额 by guoyk 2017/6/1
				if(tradeAmount.compareTo(new BigDecimal("100"))<0){//首先，账户可用投资余额若小于100，直接pass
					continue ;
					//若账户可用投资余额>=100,且小于标的剩余金额
				}else if(tradeAmount.compareTo(new BigDecimal("100"))>=0 && tradeAmount.compareTo(remainAmount)<0){
					// TODO 判断如果购买后，标的还剩余的金额 小于 100 就 控制投标金额，让其标的剩余余额减去100
					BigDecimal remainderAmount = ArithUtil.sub(remainAmount, tradeAmount);
					if (remainderAmount.compareTo(new BigDecimal("100"))<0) {
						tradeAmount = ArithUtil.sub(remainAmount, new BigDecimal("100"));
						//此时 有可能 tradeAmount 小于100
						if (tradeAmount.compareTo(new BigDecimal("100"))<0) {
							continue ;
						}
					}else {
						//对可投金额取整
						tradeAmount = new BigDecimal(String.valueOf(tradeAmount.intValue()));
					}
					
				}else if (tradeAmount.compareTo(remainAmount) >= 0) {//若大于标的剩余金额，全投
					tradeAmount = remainAmount;
				}
				
				//调用购买转让标接口
				Map<String, Object> buyCreditParams = Maps.newHashMap();
				buyCreditParams.put("transferApplyId", id);
				buyCreditParams.put("custId", custEntity.getCustId());
				buyCreditParams.put("tradeAmount", tradeAmount);
				buyCreditParams.put("appSource", "auto");
				try{
					// 调用购买债权接口
					ResultVo buyCreditVo = loanManagerService.buyCredit(buyCreditParams);
					if(ResultVo.isSuccess(buyCreditVo)){
						f++;//购买成功，记录一人
						log.info(String.format("交易成功[custId=%s, tradaAmount=%s, availableAmount=%s, transferApplyId=%s]"
								, custEntity.getCustId(), tradeAmount.toPlainString(), availableAmount.toPlainString(), id));
					}else{
						log.warn(String.format("交易失败[custId=%s, tradaAmount=%s, remainAmount=%s, transferApplyId=%s, errorMessage=%s]"
								, custEntity.getCustId(), tradeAmount.toPlainString(), remainAmount.toPlainString(), id, buyCreditVo.getValue("message").toString()));
					}
			
				} catch(Exception e){
					log.warn(String.format("交易失败[custId=%s, tradaAmount=%s, remainAmount=%s, transferApplyId=%s, errorMessage=%s]"
							, custEntity.getCustId(), tradeAmount.toPlainString(), remainAmount.toPlainString(), id, e.getMessage()));
				} finally {
				}
			}
			//每个用户无论购买成功或失败，都修改下时间
			try {
				self.updateAutoTransfer(autoInvestId);
			} catch (Exception e) {
				log.warn(String.format("修改智能投顾开启时间出错，主键id是：%s", autoInvestId));
				e.printStackTrace();
			}
		}
		//针对单标 做是否跑过智能投顾的修改
		if (isShow) {
			int a = 0;
			while(a<3){
				try {
					self.updateISRunAutoInvestForSingle(type,id);
					break;
				} catch (Exception e) {
					log.warn("修改智能投顾标识出错：");
					e.printStackTrace();
				}
				a++;
			}
		}
		Date endDate = new Date();
		int second = DateUtils.secondPhaseDiffer(startDate, endDate);
		//记录跑完一轮花费时间日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUTO_INVEST_INFO);
		logInfoEntity.setRelatePrimary("ZNTG_DB");
		logInfoEntity.setLogType(Constant.JOB_NAME_AUTO_INVEST);
		logInfoEntity.setOperDesc(String.format("一轮智能投顾(单标)总耗时：%s秒", second));
		logInfoEntity.setOperPerson("系统用户");
		logInfoEntity.setMemo(String.format("一轮智能投顾(单标)共有：%s人投标，投标类型：%s", f,type));
		logInfoEntityRepository.save(logInfoEntity);
		return new ResultVo(true, "单标智能投顾执行结束");
	}
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo updateISRunAutoInvestForSingle(String type,String id) throws SLException {
		if ("优选标".equals(type)) {
			LoanInfoEntity loanInfo = loanInfoRepository.findOne(id);
			if (loanInfo==null) {
				throw new SLException("修改失败！该标的不存在");
			}
			loanInfo.setIsRunAutoInvest(Constant.IS_RUN_AUTO_INVEST_01);
		}
		if ("转让标".equals(type)) {
			LoanTransferApplyEntity loanTransferApply = loanTransferApplyRepository.findOne(id);
			if (loanTransferApply==null) {
				throw new SLException("修改失败！该标的不存在");
			}
			loanTransferApply.setIsRunAutoInvest(Constant.IS_RUN_AUTO_INVEST_01);
		}
		return new ResultVo(true, "修改是否跑过智能投顾标识成功(单标)");
	}
	
}