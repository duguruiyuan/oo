package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanReserveEntity;
import com.slfinance.shanlincaifu.entity.LoanReserveRelationEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.ProjectInvestInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanReserveRelationRepository;
import com.slfinance.shanlincaifu.repository.LoanReserveRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.ProjectInvestInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.LoanManagerGroupRepositoryCustom;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.DeviceService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.GroupService;
import com.slfinance.shanlincaifu.service.LoanManagerService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.shanlincaifu.utils.UserUtils;
import com.slfinance.vo.ResultVo;
@Slf4j
@Service("groupService")
public class GroupServiceImpl implements GroupService {

	@Autowired
	private RedisTemplate<String, Object>  listRedisTemplate;
	
	@Autowired
	private LoanManagerGroupRepositoryCustom loanManagerGroupRepositoryCustom;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	@Qualifier("withdrawThreadPoolTaskExecutor")
	private Executor withdrawThreadPoolTaskExecutor;
	
	@Autowired
	private LoanReserveRelationRepository loanReserveRelationRepository;
	
	@Autowired
	private LoanManagerService loanManagerService;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private LoanReserveRepository loanReserveRepository;
	
	@Override
	public ResultVo pushLoanGroup(Map<String, Object> params)
			throws SLException {
		
		String custId = (String)params.get("custId");
		Integer loanTerm =Integer.parseInt(params.get("loanTerm").toString());
		String loanUnit = (String)params.get("loanUnit");
		String transferTypeCode = (String)params.get("transferType");
		String repaymentTypeCode = (String)params.get("repaymentType");
		BigDecimal tradeAmount = new BigDecimal(params.get("tradeAmount").toString());
		
		// 1) 判断账户余额是否大于交易金额
		AccountInfoEntity custAccount = accountInfoRepository.findByCustId(custId);
		if(custAccount.getAccountAvailableAmount().compareTo(tradeAmount) < 0) {
			return new ResultVo(false, "账户余额不足，请重试");
		}
//		if(tradeAmount.compareTo(BigDecimal.ZERO) < 0) {
//			return new ResultVo(false, "交易金额不能小于0");
//		}
		if(tradeAmount.compareTo(new BigDecimal(100)) < 0 && custAccount.getAccountAvailableAmount().compareTo(new BigDecimal(100)) < 0) {
			return new ResultVo(false, "出借金额必须≥100，请充值");
		}
		if(tradeAmount.compareTo(new BigDecimal(100)) < 0) {
			return new ResultVo(false, "出借金额必须≥100");
		}
		if(!ArithUtil.isDivInt(new BigDecimal(1),ArithUtil.sub(tradeAmount, new BigDecimal(100)))) {
			return new ResultVo(false, "出借金额必须是起投金额加递增金额整数倍！");
		}
		// 2) 判断交易金额是否大于可购买金额
		ResultVo result = getCanBuyTotalAmount(params);
		if(!ResultVo.isSuccess(result)) {
			return new ResultVo(false, "查询出借标的金额失败，请重试");
		}
		BigDecimal canBuyTotalAmount = new BigDecimal(result.getValue("data").toString());
		if(canBuyTotalAmount.compareTo(BigDecimal.ZERO) == 0) {
			return new ResultVo(false, "可出借标的金额为0，出借失败");
		}
		if(tradeAmount.compareTo(canBuyTotalAmount) > 0) {
			return new ResultVo(false, "交易金额大于可出借标的金额，请重试");
		}
		
		// Key结构：SLCF_期限_期限单位_可转让属性编码_还款方式编码
		String amountKey = String.format("slcf:yjcj:%s:%s:%s:%s", loanTerm, "月".equals(loanUnit) ? "MONTH" : "DAY", transferTypeCode, repaymentTypeCode);
		Object dwaitingAmount = listRedisTemplate.opsForValue().get(amountKey);
		BigDecimal waitingAmount = BigDecimal.ZERO;
		if(dwaitingAmount != null) {
			waitingAmount = new BigDecimal(dwaitingAmount.toString());
		}
		if(waitingAmount.compareTo(BigDecimal.ZERO) < 0) {
			waitingAmount = BigDecimal.ZERO;
		}
		BigDecimal factCanBuyTotalAmount = ArithUtil.sub(canBuyTotalAmount, waitingAmount);
		if(factCanBuyTotalAmount.compareTo(BigDecimal.ZERO) <= 0) {
			return new ResultVo(false, "可出借标的金额为0，出借失败");
		}
		if(tradeAmount.compareTo(factCanBuyTotalAmount) > 0) {
			return new ResultVo(false, "交易金额大于可出借标的金额，请重试");
		}
		
		// 3) 冻结账户余额
		result = innerGroupService.freezeUserAccount(params);
		
		// 4) 交易入队
		listRedisTemplate.opsForList().leftPush(Constant.LIST_REDIS_KEY, result.getValue("data"));
		
		// 5) 更新排队中的金额(+)
		listRedisTemplate.opsForValue().increment(amountKey, Double.valueOf(tradeAmount.doubleValue()));

		return new ResultVo(true, "一键出借申请成功");
	}

	@Override
	public ResultVo popLoanGroup() throws SLException {
		
		// 1) 交易出队
		final LoanReserveEntity loanReserve = (LoanReserveEntity)listRedisTemplate.opsForList().rightPop(Constant.LIST_REDIS_KEY);
		if(loanReserve == null) {
			return new ResultVo(false, "未找到排队的一键出借数据");
		}
		
		if(Constant.TRADE_STATUS_03.equals(loanReserve.getReserveStatus())) {
			return new ResultVo(false, "已处理成功");
		}
		
		log.info("start handle id:" + loanReserve.getId());
		
		String[] transferType = loanReserve.getTransferType().split("\\|");
		String[] repaymentType = loanReserve.getRepaymentType().split("\\|");
		String amountKey = String.format("slcf:yjcj:%s:%s:%s:%s", loanReserve.getLoanTerm(), "月".equals(loanReserve.getLoanUnit()) ? "MONTH" : "DAY", transferType[1], repaymentType[1]);
		
		// 2) 循环锁定标的
		while (loanReserve.getRemainderAmount().compareTo(BigDecimal.ZERO) > 0) { // 当待锁定金额大于0时，循环执行 
			Map<String, Object> params = Maps.newHashMap();
			params.put("repaymentType", repaymentType[0]);
			params.put("transferType", transferType[0]);
			params.put("loanTerm", loanReserve.getLoanTerm());
			params.put("loanUnit", loanReserve.getLoanUnit());
			
			List<Map<String, Object>> canInvestLoanList = loanManagerGroupRepositoryCustom.findCanInvestLoanList(params);
			if(canInvestLoanList == null || canInvestLoanList.size() == 0 ) break; // 没有可以锁定的标的则退出循环
			for(Map<String, Object> m : canInvestLoanList) {
				try {
					ResultVo result = innerGroupService.lockGroupLoan(loanReserve, m.get("loanId").toString());
					if(ResultVo.isSuccess(result)) {
						
						BigDecimal tradeAmount = (BigDecimal)result.getValue("data");
						// 更新剩余待锁定金额
						loanReserve.setRemainderAmount(ArithUtil.sub(loanReserve.getRemainderAmount(), tradeAmount));
						// 更新排队中的金额(-)
						listRedisTemplate.opsForValue().increment(amountKey, 0-tradeAmount.doubleValue());
						if(loanReserve.getRemainderAmount().compareTo(BigDecimal.ZERO) <=0) {
							break;
						}
					}	
				} 
				catch (Exception e) {
					log.error("loanId:" + m.get("loanId").toString() + "一键出借发生匹配失败!" + e.getMessage());
				}
			}
		}
		
		log.info("finish match id:" + loanReserve.getId());
		
		// 检查待匹配的资金是否全部锁定，若未全部锁定且未超时则放回队列等待下一批处理
		if(loanReserve.getRemainderAmount().compareTo(BigDecimal.ZERO) > 0) {
			int lockMinutes = paramService.findLoanGroupLockMinutes();
			// 若当前时间小于锁定时间，则重新放入队列
			if(new Date().compareTo(DateUtils.getDateAfterByMinute(loanReserve.getCreateDate(), lockMinutes)) < 0) {
				listRedisTemplate.opsForList().leftPush(Constant.LIST_REDIS_KEY, loanReserve);
			}
			else { // 已超过锁定时间，对账户进行解冻
				// 解冻账户
				innerGroupService.unfreezeUserAccount(loanReserve.getCustId(), loanReserve.getRemainderAmount(), loanReserve.getId(), 
						loanReserve.getRequestNo(), "超过锁定时间资金解冻");
				// 更新排队中的金额(-)
				listRedisTemplate.opsForValue().increment(amountKey, 0-loanReserve.getRemainderAmount().doubleValue());
				// 清空剩余待预约金额
				innerGroupService.clearReserve(loanReserve);
			}
		}
		
		log.info("start execute id:" + loanReserve.getId());
		
		// 4) 开启线程处理锁定的标的(执行购买)
		withdrawThreadPoolTaskExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					log.info("start buy id:" + loanReserve.getId());
					List<LoanReserveRelationEntity> loanList = loanReserveRelationRepository.findByLoanReserveIdAndTradeStatus(loanReserve.getId(), Constant.TRADE_STATUS_01);					
					for(LoanReserveRelationEntity loan : loanList) {
						
						Map<String, Object> buyCreditMap = Maps.newHashMap();
						buyCreditMap.put("custId", loan.getCustId());
						buyCreditMap.put("disperseId", loan.getLoanId());
						buyCreditMap.put("tradeAmount", loan.getTradeAmount());
						buyCreditMap.put("appSource", loanReserve.getAppSource());
						buyCreditMap.put("ipAddress", loanReserve.getIpAddress());
						buyCreditMap.put("loanManagerGroup", Constant.LOAN_MANAGER_GROUP_01);
						buyCreditMap.put("groupBatchNo", loanReserve.getRequestNo());
						buyCreditMap.put("loanReserveId", loanReserve.getId());

						try {
							innerGroupService.buyLoan(buyCreditMap, loanReserve, loan);
						} catch (Exception e) {
							log.error( "一键出借购买失败！BAO_T_LOAN_RESERVE_RELATION_ID:" + loan.getId() + e.getMessage());
						}
					}
					// 整体更新为成功
					List<LoanReserveRelationEntity> handleLoanList = loanReserveRelationRepository.findByLoanReserveIdAndTradeStatus(loanReserve.getId(), Constant.TRADE_STATUS_01);
					LoanReserveEntity loanReserveEntity = loanReserveRepository.findOne(loanReserve.getId());
					if(loanReserveEntity.getRemainderAmount().compareTo(BigDecimal.ZERO) == 0 && handleLoanList.size() == 0) { // 已全部处理成功
						if(loanReserveEntity.getAlreadyInvestAmount().compareTo(BigDecimal.ZERO) > 0) { // 若存在购买成功的，则成功
							innerGroupService.updateReserve(loanReserve, Constant.TRADE_STATUS_03);
						}
						else {
							innerGroupService.updateReserve(loanReserve, Constant.TRADE_STATUS_04);
						}
					}
					else if(handleLoanList.size() > 0){
						// 重新入队等待下次再处理
						loanReserve.setRemainderAmount(BigDecimal.ZERO);// 此处置为0保证只用于重新跑购买，而不需要重新匹配
						listRedisTemplate.opsForList().leftPush(Constant.LIST_REDIS_KEY, loanReserve);
					}
					log.info("end buy id:" + loanReserve.getId());					
				} catch (Exception e) {
					log.error("出借失败" + e.getMessage());
				}
			}
		});
		
		return new ResultVo(true, "一键出借成功");
	}

	@Override
	public ResultVo getCanBuyTotalAmount(Map<String, Object> params) throws SLException {
		Map<String, Object> requestParams = Maps.newHashMap();
		requestParams.putAll(params);
		String transferType = UserUtils.convertTransferType2Word(requestParams.get("transferType").toString());
		requestParams.put("transferType", transferType);
		String repaymentType = UserUtils.convertRepaymentType2Word(requestParams.get("repaymentType").toString());
		requestParams.put("repaymentType", repaymentType);
		Map<String, Object> resultMap = loanManagerGroupRepositoryCustom.queryTradeInfoByLoanTermAndLoanUnit(requestParams);
		BigDecimal remainAmount = new BigDecimal(resultMap.get("remainAmount").toString());
	
		return new ResultVo(true, "查询成功", remainAmount);
	}

	@Autowired
	private InnerGroupService innerGroupService;
	
	@Service
	public static class InnerGroupService {
		
		@Autowired
		private FlowNumberService numberService;
		
		@Autowired
		private CustAccountService custAccountService;
		
		@Autowired
		private AccountInfoRepository accountInfoRepository;
		
		@Autowired
		private LoanReserveRepository loanReserveRepository;
		
		@Autowired
		private LoanInfoRepository loanInfoRepository;
		
		@Autowired
		private ProjectInvestInfoRepository projectInvestInfoRepository;
		
		@Autowired
		private LoanReserveRelationRepository loanReserveRelationRepository;
		
		@Autowired
		private DeviceService deviceService;
		
		@Autowired
		private LogInfoEntityRepository logInfoEntityRepository;
		
		@Autowired
		private LoanManagerService loanManagerService;
		
		/**
		 * 冻结用户资金
		 *
		 * @author  wangjf
		 * @date    2017年7月21日 上午11:21:51
		 * @param custId
		 * @param tradeAmount
		 * @throws SLException
		 */
		@Transactional(readOnly=false, rollbackFor=SLException.class)
		public ResultVo freezeUserAccount(Map<String, Object> params) throws SLException {
			
			String custId = (String)params.get("custId");
			Integer loanTerm = Integer.parseInt(params.get("loanTerm").toString());
			String loanUnit = (String)params.get("loanUnit");
			String transferTypeCode = (String)params.get("transferType");
			String repaymentTypeCode = (String)params.get("repaymentType");
			BigDecimal tradeAmount = new BigDecimal(params.get("tradeAmount").toString());
			String appSource = (String)params.get("appSource");
			appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
			
			int reserveNumber = loanReserveRepository.countByCustIdAndCreateDate(custId, DateUtils.truncateDate(new Date()));
			
			String requestNo = numberService.generateTradeBatchNumber();
			LoanReserveEntity loanReserve = new LoanReserveEntity();
			loanReserve.setBasicModelProperty(custId, true);
			loanReserve.setCustId(custId);
			loanReserve.setReserveAmount(tradeAmount);
			loanReserve.setAlreadyInvestAmount(BigDecimal.ZERO);
			loanReserve.setRemainderAmount(tradeAmount);
			loanReserve.setReserveStatus(Constant.TRADE_STATUS_02);
			loanReserve.setReserveTitle(String.format("一键出借%s%04d", DateUtils.formatDate(new Date(), "yyyyMMdd"), reserveNumber + 1));// 从1开始
			loanReserve.setLoanTerm(loanTerm);
			loanReserve.setLoanUnit(loanUnit);
			loanReserve.setTransferType(UserUtils.convertTransferType2Word(transferTypeCode) + "|" + transferTypeCode );
			loanReserve.setRepaymentType(UserUtils.convertRepaymentType2Word(repaymentTypeCode) + "|" + repaymentTypeCode );
			loanReserve.setRequestNo(requestNo);
			loanReserve.setAppSource(appSource);
			loanReserve.setIpAddress(StringUtils.isEmpty(params.get("ipAddress"))?"":params.get("ipAddress").toString());
			loanReserve = loanReserveRepository.save(loanReserve);
			
			AccountInfoEntity custAccount = accountInfoRepository.findByCustId(custId);
			
			custAccountService.updateAccount(custAccount, null, null,
					null, "7", SubjectConstant.TRADE_FLOW_TYPE_BUY_GROUP_FREEZEN, 
					requestNo, tradeAmount, SubjectConstant.TRADE_FLOW_TYPE_BUY_GROUP_FREEZEN, 
					Constant.TABLE_BAO_T_LOAN_RESERVE, loanReserve.getId(),
					"一键出借资金冻结", custId);
			
			Map<String, Object> deviceParams = Maps.newConcurrentMap();
			deviceParams.putAll(params);
			deviceParams.put("relateType", Constant.TABLE_BAO_T_LOAN_RESERVE);
			deviceParams.put("relatePrimary", loanReserve.getId());
			deviceParams.put("tradeType", Constant.OPERATION_TYPE_92);
			deviceParams.put("userId", custId);
			deviceService.saveUserDevice(deviceParams);
			
			// 4 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_RESERVE);
			logInfoEntity.setRelatePrimary(loanReserve.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_92);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(custId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("一键出借金额%s", tradeAmount.toString()));
			logInfoEntity.setBasicModelProperty(custId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			return new ResultVo(true, "一键出借冻结成功", loanReserve);
		}
		
		/**
		 * 解冻用户资金
		 *
		 * @author  wangjf
		 * @date    2017年7月21日 下午2:24:25
		 * @param params
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly=false, rollbackFor=SLException.class)
		public ResultVo unfreezeUserAccount(String custId, BigDecimal tradeAmount, String loanReserveId, 
				String requestNo, String memo) throws SLException {
			AccountInfoEntity custAccount = accountInfoRepository.findByCustId(custId);
			
			custAccountService.updateAccount(custAccount, null, null,
					null, "8", SubjectConstant.TRADE_FLOW_TYPE_BUY_GROUP_UNFREEZE, 
					requestNo, tradeAmount, SubjectConstant.TRADE_FLOW_TYPE_BUY_GROUP_UNFREEZE, 
					Constant.TABLE_BAO_T_LOAN_RESERVE, loanReserveId,
					memo, custId);
			
			return new ResultVo(true, "一键出借解冻成功");
		}
		
		/**
		 * 锁定标的
		 *
		 * @author  wangjf
		 * @date    2017年7月21日 下午2:24:08
		 * @param loanReserve
		 * @param loanId
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly=false, rollbackFor=SLException.class)
		public ResultVo lockGroupLoan(LoanReserveEntity loanReserve, String loanId) throws SLException{
			BigDecimal remainderAmount = loanReserve.getRemainderAmount();
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
			ProjectInvestInfoEntity projectInvestInfo = projectInvestInfoRepository.findByLoanId(loanId);
			BigDecimal canInvestAmount = ArithUtil.sub(loanInfoEntity.getLoanAmount(), projectInvestInfo.getAlreadyInvestAmount());
			if(canInvestAmount.compareTo(BigDecimal.ZERO) <= 0) { // 可投金额小于0则返回
				return new ResultVo(false, "可投金额小于等于0");
			}
			
			BigDecimal tradeAmount = BigDecimal.ZERO;
			if(remainderAmount.compareTo(canInvestAmount) >= 0) { // 交易金额大于等于可投金额，则全部锁定
				tradeAmount = canInvestAmount;
			}
			else { // 交易金额小于可投金额，则部分锁定
				tradeAmount = remainderAmount;
			}
			loanInfoEntity.setLoanStatus(Constant.LOAN_MANAGER_GROUP_03);
			loanInfoEntity.setBasicModelProperty(loanReserve.getCustId(), false);
			projectInvestInfo.setBasicModelProperty(loanReserve.getCustId(), false);

			// 生成一键出借与借款的锁定关系
			LoanReserveRelationEntity loanReserveRelationEntity = new LoanReserveRelationEntity();
			loanReserveRelationEntity.setLoanId(loanInfoEntity.getId());
			loanReserveRelationEntity.setLoanReserveId(loanReserve.getId());
			loanReserveRelationEntity.setCustId(loanReserve.getCustId());
			loanReserveRelationEntity.setTradeAmount(tradeAmount);
			loanReserveRelationEntity.setTradeStatus(Constant.TRADE_STATUS_01);
			loanReserveRelationEntity.setBasicModelProperty(loanReserve.getCustId(), true);
			loanReserveRelationRepository.save(loanReserveRelationEntity);
			
			LoanReserveEntity loanReserveEntity = loanReserveRepository.findOne(loanReserve.getId());
			loanReserveEntity.setRemainderAmount(ArithUtil.sub(loanReserveEntity.getRemainderAmount(), tradeAmount));
			loanReserveEntity.setBasicModelProperty(loanReserve.getCustId(), false);
			
			return new ResultVo(true, "成功锁定标的", tradeAmount);
		}
		
		/**
		 * 解锁标的
		 *
		 * @author  wangjf
		 * @date    2017年7月21日 下午2:25:15
		 * @param loanReserve
		 * @param loanId
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly=false, rollbackFor=SLException.class)
		public ResultVo unlockGroupLoan(LoanReserveEntity loanReserve, String loanId) throws SLException{
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
			if(!Constant.LOAN_MANAGER_GROUP_03.equals(loanInfoEntity.getLoanStatus())) {
				return new ResultVo(true, "非锁定状态不能解锁");
			}
			ProjectInvestInfoEntity projectInvestInfo = projectInvestInfoRepository.findByLoanId(loanId);
			loanInfoEntity.setLoanStatus(Constant.LOAN_STATUS_05);
			loanInfoEntity.setBasicModelProperty(loanReserve.getCustId(), false);
			projectInvestInfo.setBasicModelProperty(loanReserve.getCustId(), false);
			
			return new ResultVo(true, "解锁标的成功");
		}
		
		/**
		 * 更新预约情况
		 *
		 * @author  wangjf
		 * @date    2017年7月21日 下午3:27:41
		 * @param loanReserve
		 * @param id
		 * @param tradeStatus
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly=false, rollbackFor=SLException.class)
		public ResultVo updateReserveRelation(LoanReserveEntity loanReserve, String id, String tradeStatus, String investId) throws SLException{
			LoanReserveRelationEntity loanReserveRelationEntity = loanReserveRelationRepository.findOne(id);
			loanReserveRelationEntity.setTradeStatus(tradeStatus);
			loanReserveRelationEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			if(Constant.TRADE_STATUS_03.equals(tradeStatus)) { // 若处理成功，则更新预约
				
				loanReserveRelationEntity.setInvestId(investId);
				
				LoanReserveEntity loanReserveEntity = loanReserveRepository.findOne(loanReserve.getId());
				loanReserveEntity.setAlreadyInvestAmount(ArithUtil.add(loanReserveEntity.getAlreadyInvestAmount(), loanReserveRelationEntity.getTradeAmount()));
				loanReserveEntity.setBasicModelProperty(loanReserve.getCustId(), false);
			}
						
			return new ResultVo(true);
		}
		
		/**
		 * 更新预约总体情况
		 *
		 * @author  wangjf
		 * @date    2017年7月21日 下午3:32:50
		 * @param loanReserve
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly=false, rollbackFor=SLException.class)
		public ResultVo updateReserve(LoanReserveEntity loanReserve, String tradeStatus) throws SLException{
			LoanReserveEntity loanReserveEntity = loanReserveRepository.findOne(loanReserve.getId());
			loanReserveEntity.setReserveStatus(tradeStatus);
			loanReserveEntity.setBasicModelProperty(loanReserve.getCustId(), false);
			
			return new ResultVo(true);
		}
		
		/**
		 * 清空剩余待匹配金额
		 *
		 * @author  wangjf
		 * @date    2017年7月21日 下午4:01:23
		 * @param loanReserve
		 * @param tradeStatus
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly=false, rollbackFor=SLException.class)
		public ResultVo clearReserve(LoanReserveEntity loanReserve) throws SLException{
			LoanReserveEntity loanReserveEntity = loanReserveRepository.findOne(loanReserve.getId());
			loanReserveEntity.setRemainderAmount(BigDecimal.ZERO);
			loanReserveEntity.setBasicModelProperty(loanReserve.getCustId(), false);
			
			return new ResultVo(true);
		}
		
		/**
		 * 购买借款
		 *
		 * @author  wangjf
		 * @date    2017年7月21日 下午5:48:02
		 * @param buyCreditMap
		 * @param loanReserve
		 * @param loan
		 * @return
		 * @throws SLException
		 */
		@SuppressWarnings("unchecked")
		@Transactional(readOnly=false, rollbackFor=SLException.class)
		public ResultVo buyLoan(Map<String, Object> buyCreditMap, LoanReserveEntity loanReserve, LoanReserveRelationEntity loan) throws SLException {
			ResultVo result = loanManagerService.buyDispersion(buyCreditMap);
			if(ResultVo.isSuccess(result)) {
				Map<String, Object> data = (Map<String, Object>)result.getValue("data");
				updateReserveRelation(loanReserve, loan.getId(), Constant.TRADE_STATUS_03, (String)data.get("investId"));
			}
			else {
				updateReserveRelation(loanReserve, loan.getId(), Constant.TRADE_STATUS_04, null);
				// 购买失败需解冻账户和解锁标的
				// 解锁账户
				unfreezeUserAccount(loan.getCustId(), loan.getTradeAmount(), loanReserve.getId(), 
						loanReserve.getRequestNo(), "出借失败资金解冻");
			}
			// 解锁标的
			// 不管购买成功或者失败，都可以进行解锁。
			// 1）购买成功，若状态变为满标复核，无需解锁，若状态是原状态则需解锁
			// 2）购买失败，购买失败需解锁标的
			unlockGroupLoan(loanReserve, loan.getLoanId());
			return result;
		}
	}

	@Override
	public ResultVo loadLoanGroup(){
		
		// 第一次加载时将队列清空，后面将库中处理中的数据重新放回队列
		while(listRedisTemplate.opsForList().size(Constant.LIST_REDIS_KEY) != 0) {
			listRedisTemplate.opsForList().rightPop(Constant.LIST_REDIS_KEY);
		}
		
		// 第一次加载初始化队列，设置各种期限的金额为0
		List<LoanReserveEntity> groupList = loanReserveRepository.findAllGroup();
		for(LoanReserveEntity loanReserve : groupList) {
			String[] transferType = loanReserve.getTransferType().split("\\|");
			String[] repaymentType = loanReserve.getRepaymentType().split("\\|");
			String amountKey = String.format("slcf:yjcj:%s:%s:%s:%s", loanReserve.getLoanTerm(), "月".equals(loanReserve.getLoanUnit()) ? "MONTH" : "DAY", transferType[1], repaymentType[1]);
			listRedisTemplate.opsForValue().set(amountKey, 0.0);
		}
		
		// 处理中的重新入队
		List<LoanReserveEntity> loanReserveList = loanReserveRepository.findByReserveStatus(Constant.TRADE_STATUS_02);
		for(LoanReserveEntity loanReserve : loanReserveList) {
			// 重新入队
			listRedisTemplate.opsForList().leftPush(Constant.LIST_REDIS_KEY, loanReserve);
			String[] transferType = loanReserve.getTransferType().split("\\|");
			String[] repaymentType = loanReserve.getRepaymentType().split("\\|");
			String amountKey = String.format("slcf:yjcj:%s:%s:%s:%s", loanReserve.getLoanTerm(), "月".equals(loanReserve.getLoanUnit()) ? "MONTH" : "DAY", transferType[1], repaymentType[1]);
			listRedisTemplate.opsForValue().increment(amountKey, Double.valueOf(loanReserve.getRemainderAmount().doubleValue()));
		}
		return new ResultVo(true);
	}

	@Override
	public ResultVo runPopGroup() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while (true) {
					try {						
						popLoanGroup();
						Thread.sleep(500);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			}
		}).start();
		return new ResultVo(true);
	}
}
