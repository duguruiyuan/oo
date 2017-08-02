package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.CustBusinessHistoryRepositoryCustom;

/**
 * 报表--数据总览
 * @author zhangt
 *
 */
@Repository
public class CustBusinessHistoryRepositoryCustomImpl implements
		CustBusinessHistoryRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	//数据总览
	@Override
	public  Page<Map<String, Object>> getCustBusinessHistoryPage(Map<String, Object> param) {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer()
		.append("select btcbh.record_date \"recordDate\", max(btcbh.app_source) \"appSource\", nvl(sum(btcbh.register_count), 0) \"registerCount\", nvl(sum(btcbh.realname_count), 0) \"realnameCount\",")
		.append(" nvl(sum(btcbh.recharge_count), 0) \"rechargeCount\", nvl(sum(btcbh.recharge_success_count), 0) \"rechargeSuccessCount\",nvl(round(sum(btcbh.recharge_summary), 2), 0) \"rechargeSummary\",nvl(sum(btcbh.withdraw_success_count), 0) \"withdrawCount\",")
		.append(" nvl(round(sum(btcbh.withdraw_summary), 2), 0) \"withdrawSummary\",nvl(sum(btcbh.invest_count), 0) \"investCount\",nvl(round(sum(btcbh.invest_summary), 2), 0) \"investSummary\" from bao_t_cust_business_history btcbh where 1=1");
		
		//终端
		if (!StringUtils.isEmpty(param.get("appSource"))) {
			String appSource = (String) param.get("appSource");
			sb.append(" and btcbh.app_source = ?");
			listObject.add(appSource);

		}
		
		//
		if(!StringUtils.isEmpty(param.get("recordDate"))){
			String recordDate = ((String) param.get("recordDate")).replace("-", "");
			sb.append(" and substr(btcbh.record_date, 0, 6) = ?");
			listObject.add(recordDate);
		}
		//时间
		if (!StringUtils.isEmpty(param.get("startDate"))) {
			String startDate = ((String) param.get("startDate")).replace("-", "");
			sb.append(" and btcbh.record_date >= ?");
			listObject.add(startDate);
		}
		
		if (!StringUtils.isEmpty(param.get("endDate"))) {
			String endDate = ((String) param.get("endDate")).replace("-", "");
			sb.append(" and btcbh.record_date <= ?");
			listObject.add(endDate);
		}
		
		sb.append(" group by btcbh.record_date order by btcbh.record_date");

		return repositoryUtil.queryForPageMap(sb.toString(), listObject.toArray(), (Integer) param.get("start"), (Integer) param.get("length"));
	}
	
	@Override
	public List<Map<String, Object>> export(Map<String, Object> param) {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer()
		.append("select to_char(to_date(btcbh.record_date,'yyyy-MM-dd'),'yyyy-MM-dd') \"recordDate\", nvl(sum(btcbh.register_count), 0) \"registerCount\", nvl(sum(btcbh.realname_count),0) \"realnameCount\",")
		.append(" nvl(sum(btcbh.recharge_count), 0) \"rechargeCount\", nvl(sum(btcbh.recharge_success_count), 0) \"rechargeSuccessCount\", nvl(round(sum(btcbh.recharge_summary), 2),0) \"rechargeSummary\",nvl(sum(btcbh.withdraw_success_count),0) \"withdrawCount\",")
		.append(" nvl(round(sum(btcbh.withdraw_summary), 2),0) \"withdrawSummary\",nvl(sum(btcbh.invest_count),0) \"investCount\", nvl(round(sum(btcbh.invest_summary), 2),0) \"investSummary\" from bao_t_cust_business_history btcbh where 1=1");
		
		//终端
		if (!StringUtils.isEmpty(param.get("appSource"))) {
			String appSource = (String) param.get("appSource");
			sb.append(" and btcbh.app_source = ?");
			listObject.add(appSource);
		}
		
		if(!StringUtils.isEmpty(param.get("recordDate"))){
			String recordDate = ((String) param.get("recordDate")).replace("-", "");
			sb.append(" and substr(btcbh.record_date, 0, 6) = ?");
			listObject.add(recordDate);
		}
		
		//时间
		if (!StringUtils.isEmpty(param.get("startDate"))) {
			String startDate = ((String) param.get("startDate")).replace("-", "");
			sb.append(" and btcbh.record_date >= ?");
			listObject.add(startDate);
		}
		
		if (!StringUtils.isEmpty(param.get("endDate"))) {
			String endDate = ((String) param.get("endDate")).replace("-", "");
			sb.append(" and btcbh.record_date <= ?");
			listObject.add(endDate);
		}
		
		sb.append(" group by btcbh.record_date order by btcbh.record_date");
		
		return repositoryUtil.queryForMap(sb.toString(), listObject.toArray());
	}

}
