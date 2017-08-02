package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustBusinessHistoryEntity;
import com.slfinance.shanlincaifu.repository.CustBusinessHistoryRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.InvestInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.TradeFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.SummarizationBusinessHistoryService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;

/**
 * 业务数据汇总
 * @author zhangt
 *
 */
@Service
public class SummarizationBusinessHistoryServiceImpl implements SummarizationBusinessHistoryService{
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private TradeFlowInfoRepositoryCustom tradeFlowInfoRepositoryCustom;
	
	@Autowired
	private InvestInfoRepositoryCustom investInfoRepositoryCustom;
	
	@Autowired
	private CustBusinessHistoryRepository custBusinessHistoryRepository;

	@Transactional
	@Override
	public void sumBusinessHistory() throws SLException {
		
		//先删除全部数据
		custBusinessHistoryRepository.deleteAll();
		//定义集合存放CustBusinessHistoryEntity
		List<CustBusinessHistoryEntity> custBusinessHistoryList = new ArrayList<CustBusinessHistoryEntity>();
		//1 注册次数
		List<Map<String, Object>> registerList = custInfoRepository.findRigsterCount();
		for(int i = 0; i < registerList.size(); i++){
			CustBusinessHistoryEntity custBusinessHistoryEntity = new CustBusinessHistoryEntity();
			Date date = (Date) registerList.get(i).get("createDate");
			custBusinessHistoryEntity.setRecordDate(DateUtils.formatDate(date, "yyyyMMdd"));
			custBusinessHistoryEntity.setAppSource((String)registerList.get(i).get("custSource"));
			custBusinessHistoryEntity.setRegisterCount(registerList.get(i).get("regCount") != null ? 
					Integer.parseInt(registerList.get(i).get("regCount").toString()) : null);
			custBusinessHistoryEntity.setCreateDate(DateUtils.parseDate(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"));
			custBusinessHistoryEntity.setLastUpdateDate(DateUtils.parseDate(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"));
			custBusinessHistoryEntity.setCreateUser(Constant.SYSTEM_USER_BACK);
			custBusinessHistoryEntity.setLastUpdateUser(Constant.SYSTEM_USER_BACK);
			custBusinessHistoryList.add(custBusinessHistoryEntity);
		}
		
		//2 充值、提现
		List<Map<String, Object>> tradeList = tradeFlowInfoRepositoryCustom.findTradeFlowBusinessHistory();
		for(int i = 0; i < custBusinessHistoryList.size(); i++){
			String recordDate = custBusinessHistoryList.get(i).getRecordDate() != null
					? custBusinessHistoryList.get(i).getRecordDate() : "";
			String source = custBusinessHistoryList.get(i).getAppSource() != null 
					? custBusinessHistoryList.get(i).getAppSource() : "";
			for(int j = 0; j < tradeList.size(); j++){
				Date date = (Date) tradeList.get(j).get("createDate");
				String createDate = DateUtils.formatDate(date, "yyyyMMdd");
				String tradeSource = tradeList.get(j).get("tradeSource") !=null ?
						tradeList.get(j).get("tradeSource").toString() : "";
				Integer rechargeCount = tradeList.get(j).get("rechargeCount") != null ? 
						Integer.parseInt(tradeList.get(j).get("rechargeCount").toString()) : null;
				Integer rechargeSuccCount = tradeList.get(j).get("rechargeSuccCount") != null ? 
						Integer.parseInt(tradeList.get(j).get("rechargeSuccCount").toString()) : null;
				BigDecimal rechargeAmount = (BigDecimal) tradeList.get(j).get("rechargAmount");
				Integer withdrawCount = tradeList.get(j).get("withdrawCount") !=null ?
						Integer.parseInt(tradeList.get(j).get("withdrawCount").toString()) : null;
				BigDecimal withdrawAmount = (BigDecimal) tradeList.get(j).get("withdrawAmount");
				if (recordDate.equals(createDate) && source.equals(tradeSource)) {
					custBusinessHistoryList.get(i).setRechargeCount(rechargeCount);
					custBusinessHistoryList.get(i).setRechargeSuccessCount(rechargeSuccCount);
					custBusinessHistoryList.get(i).setRechargeSummary(rechargeAmount);
					custBusinessHistoryList.get(i).setWithdrawSuccessCount(withdrawCount);
					custBusinessHistoryList.get(i).setWithdrawSummary(withdrawAmount);
					tradeList.remove(j);
				} 
			}
		}
		// 充值、提现 与 注册 不同日期，不同appsource的记录
		for(int i=0; i < tradeList.size(); i++){
			CustBusinessHistoryEntity custBusinessHistoryEntity = new CustBusinessHistoryEntity();
			Date date = (Date) tradeList.get(i).get("createDate");
			custBusinessHistoryEntity.setRecordDate(DateUtils.formatDate(date, "yyyyMMdd"));
			custBusinessHistoryEntity.setAppSource((String) tradeList.get(i).get("tradeSource"));
			custBusinessHistoryEntity.setRechargeCount(tradeList.get(i).get("rechargeCount") != null ? 
					Integer.parseInt(tradeList.get(i).get("rechargeCount").toString()) : null);
			custBusinessHistoryEntity.setRechargeSuccessCount(tradeList.get(i).get("rechargeSuccCount") != null ? 
					Integer.parseInt(tradeList.get(i).get("rechargeSuccCount").toString()) : null);
			custBusinessHistoryEntity.setRechargeSummary((BigDecimal) tradeList.get(i).get("rechargAmount"));
			custBusinessHistoryEntity.setWithdrawSuccessCount(tradeList.get(i).get("withdrawCount") !=null ?
					Integer.parseInt(tradeList.get(i).get("withdrawCount").toString()) : null);
			custBusinessHistoryEntity.setWithdrawSummary((BigDecimal) tradeList.get(i).get("withdrawAmount"));
			custBusinessHistoryEntity.setCreateDate(DateUtils.parseDate(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"));
			custBusinessHistoryEntity.setLastUpdateDate(DateUtils.parseDate(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"));
			custBusinessHistoryEntity.setCreateUser(Constant.SYSTEM_USER_BACK);
			custBusinessHistoryEntity.setLastUpdateUser(Constant.SYSTEM_USER_BACK);
			custBusinessHistoryList.add(custBusinessHistoryEntity);
		}
		//3投资
		List<Map<String, Object>> investList = investInfoRepositoryCustom.findInvestBusinessHistory();
		for(int i = 0; i < custBusinessHistoryList.size(); i++){
			String recordDate = custBusinessHistoryList.get(i).getRecordDate() != null ? custBusinessHistoryList.get(i).getRecordDate() : "";
			String source = custBusinessHistoryList.get(i).getAppSource() != null ? custBusinessHistoryList.get(i).getAppSource() : "";
			for(int j = 0; j < investList.size(); j++){
				Date date = (Date) investList.get(j).get("createDate");
				String createDate = DateUtils.formatDate(date, "yyyyMMdd");
				String investSource = investList.get(j).get("investSource") != null ?
						investList.get(j).get("investSource").toString() : "";
				BigDecimal investCount = (BigDecimal) investList.get(j).get("investCount");
				BigDecimal investAmount = (BigDecimal) investList.get(j).get("investAmount");
				if (recordDate.equals(createDate) && source.equals(investSource)) {
					custBusinessHistoryList.get(i).setInvestCount(investCount);
					custBusinessHistoryList.get(i).setInvestSummary(investAmount);
					investList.remove(j);
				} 
			}
			
		}
		//投资与custBusinessHistoryList 不同日期，不同appsource的记录
		for (int i = 0; i < investList.size(); i++){
			CustBusinessHistoryEntity custBusinessHistoryEntity = new CustBusinessHistoryEntity();
			Date date = (Date) investList.get(i).get("createDate");
			custBusinessHistoryEntity.setRecordDate(DateUtils.formatDate(date, "yyyyMMdd"));
			custBusinessHistoryEntity.setAppSource((String) investList.get(i).get("investSource"));
			custBusinessHistoryEntity.setInvestCount((BigDecimal) investList.get(i).get("investCount"));
			custBusinessHistoryEntity.setInvestSummary((BigDecimal) investList.get(i).get("investAmount"));
			custBusinessHistoryEntity.setCreateDate(DateUtils.parseDate(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"));
			custBusinessHistoryEntity.setLastUpdateDate(DateUtils.parseDate(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"));
			custBusinessHistoryEntity.setCreateUser(Constant.SYSTEM_USER_BACK);
			custBusinessHistoryEntity.setLastUpdateUser(Constant.SYSTEM_USER_BACK);
			custBusinessHistoryList.add(custBusinessHistoryEntity);
		}
		
		//4实名认证人数
		List<Map<String, Object>> realNameList = tradeFlowInfoRepositoryCustom.findRealNameHistory();
		for(int i = 0; i < custBusinessHistoryList.size(); i++){
			String recordDate = custBusinessHistoryList.get(i).getRecordDate() != null ? custBusinessHistoryList.get(i).getRecordDate() : "";
			String source = custBusinessHistoryList.get(i).getAppSource() != null ? custBusinessHistoryList.get(i).getAppSource() : "";
			for(int j = 0; j < realNameList.size(); j++){
				Date date = (Date) realNameList.get(j).get("createDate");
				String createDate = DateUtils.formatDate(date, "yyyyMMdd");
				String realNameSource = realNameList.get(j).get("realNameSource") != null ?
						realNameList.get(j).get("realNameSource").toString() : "";
				Integer realnameCount = realNameList.get(j).get("certiCount") != null ? 
						Integer.parseInt(realNameList.get(j).get("certiCount").toString()) : null;
				if(recordDate.equals(createDate) && source.equals(realNameSource)){
					custBusinessHistoryList.get(i).setRealnameCount(realnameCount);
					realNameList.remove(j);
				}
			}
		}
		//实名认证与custBusinessHistoryList 不同日期，不同appsource的记录
		for(int i = 0; i < realNameList.size(); i++){
			CustBusinessHistoryEntity custBusinessHistoryEntity = new CustBusinessHistoryEntity();
			Date date = (Date) realNameList.get(i).get("createDate");
			custBusinessHistoryEntity.setRecordDate(DateUtils.formatDate(date, "yyyyMMdd"));
			custBusinessHistoryEntity.setAppSource((String) realNameList.get(i).get("realNameSource"));
			custBusinessHistoryEntity.setRealnameCount(realNameList.get(i).get("certiCount") != null ? 
					Integer.parseInt(realNameList.get(i).get("certiCount").toString()) : null);
			custBusinessHistoryEntity.setCreateDate(DateUtils.parseDate(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"));
			custBusinessHistoryEntity.setLastUpdateDate(DateUtils.parseDate(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"));
			custBusinessHistoryEntity.setCreateUser(Constant.SYSTEM_USER_BACK);
			custBusinessHistoryEntity.setLastUpdateUser(Constant.SYSTEM_USER_BACK);
			custBusinessHistoryList.add(custBusinessHistoryEntity);
		}
		
		custBusinessHistoryRepository.save(custBusinessHistoryList);
		
	}

}
