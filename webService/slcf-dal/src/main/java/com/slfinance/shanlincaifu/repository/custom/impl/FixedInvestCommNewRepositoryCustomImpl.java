package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.FixedInvestCommNewRepositoryCustom;

/**
 * 
 * @author zhangt
 * 报表管理--新定期宝业绩统计
 */
@Repository
public class FixedInvestCommNewRepositoryCustomImpl implements
		FixedInvestCommNewRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	//业绩统计
	@Override
	public Page<Map<String, Object>> getFixedInvestCommListPage(
			Map<String, Object> param) throws SLException {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer()
		.append("select sum(btci.year_invest_amount) \"yearInvestAmount\", btci.comm_month \"commMonth\", max(btci.create_date) \"createDate\" ")
		.append(" from bao_t_commission_info btci, bao_t_cust_info btcii,com_t_user ctu, bao_t_product_type_info btpt ")
		.append(" where btci.cust_id = btcii.id and btcii.credentials_code = ctu.credentials_code(+) and btci.product_type_id = btpt.id and btpt.type_name='定期宝'");
		
		if (!StringUtils.isEmpty(param.get("startDate"))) {
			String startDate = (String) param.get("startDate");
			sb.append(" and btci.comm_month >= ?");
			listObject.add(startDate.replace("-", ""));
		}
		
		if(!StringUtils.isEmpty(param.get("endDate"))) {
			String endDate = (String) param.get("endDate");
			sb.append(" and btci.comm_month <= ?");
			listObject.add(endDate.replace("-", ""));	
		}
		
		sb.append(" GROUP BY btci.comm_month ORDER BY btci.comm_month DESC");
		
		return repositoryUtil.queryForPageMap(sb.toString(), listObject.toArray(), (Integer) param.get("start"), (Integer) param.get("length"));
	}

	//业绩详情
	@Override
	public Page<Map<String, Object>> getFixedInvestCommListByMonth(
			Map<String, Object> param) throws SLException {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer()
		.append("select btci.comm_month \"commMonth\", btci.year_invest_amount \"yearInvestAmount\", btcii.cust_name \"custName\", btcii.credentials_code  \"credentialsCode\",ctu.user_number \"userNumber\", ")
		.append(" btci.invest_amount \"investAmount\" ")
		.append(" from bao_t_commission_info btci,bao_t_cust_info btcii ,com_t_user ctu, bao_t_product_type_info btpt")
		.append(" where btci.cust_id = btcii.id and btcii.credentials_code = ctu.credentials_code(+) and btci.product_type_id = btpt.id and btpt.type_name = '定期宝'");
		
		// 统计月份查詢
		if (!StringUtils.isEmpty(param.get("commMonth"))) {
			String commMonth = (String) param.get("commMonth");
			sb.append(" AND btci.comm_month =  ? ");
			listObject.add(commMonth);
		}
		
		// 姓名加入查詢
		if (!StringUtils.isEmpty(param.get("custName"))) {
			String custName = (String) param.get("custName");
			sb.append(" and btcii.cust_name  like ? ");
			listObject.add("%" + custName + "%");
		}
		
		// 员工工号加入查询
		if (!StringUtils.isEmpty(param.get("userNumber"))) {
			String userNumber = (String) param.get("userNumber");
			sb.append(" and ctu.user_number = ? ");
			listObject.add(userNumber);
		}
		
		// 证件号加入查询
		if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
			String credentialsCode = (String) param.get("credentialsCode");
			sb.append(" and btcii.credentials_code = ? ");
			listObject.add(credentialsCode);
		}

		sb.append(" order by btcii.cust_name");
		
		return repositoryUtil.queryForPageMap(sb.toString(), listObject.toArray(), (Integer) param.get("start"), (Integer) param.get("length"));	
	}

	//导出数据
	@Override
	public List<Map<String, Object>> export(Map<String, Object> param)
			throws SLException {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer()
		.append("select btci.comm_month \"commMonth\", btci.year_invest_amount \"yearInvestAmount\", btcii.cust_name \"custName\", btcii.credentials_code  \"credentialsCode\",ctu.user_number \"userNumber\", ")
		.append(" btci.invest_amount \"investAmount\" ")
		.append(" from bao_t_commission_info btci,bao_t_cust_info btcii ,com_t_user ctu, bao_t_product_type_info btpt")
		.append(" where  btci.cust_id = btcii.id and btcii.credentials_code = ctu.credentials_code(+) and btci.product_type_id = btpt.id and btpt.type_name = '定期宝'");
		
		// 统计月份查詢
		if (!StringUtils.isEmpty(param.get("commMonth"))) {
			String commMonth = (String) param.get("commMonth");
			sb.append(" and btci.comm_month =  ? ");
			listObject.add(commMonth);
		}
		
		sb.append(" order by btcii.cust_name");
		
		return repositoryUtil.queryForMap(sb.toString(), listObject.toArray());
	}

}
