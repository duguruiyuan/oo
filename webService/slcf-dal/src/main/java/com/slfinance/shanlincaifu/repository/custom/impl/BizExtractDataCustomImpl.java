package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.BizExtractDataCustom;

@Repository
public class BizExtractDataCustomImpl implements BizExtractDataCustom {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private RepositoryUtil repositoryUtil;

	@Override
	public List<Map<String, Object>> dailyDataSummary(Map<String, Object> params) {
		// --日充值金额(元)  日提现金额(元)  日投资金额(元)  日发布金额(元)  日转让金额(元)  日新增注册用户数
		StringBuilder sql = new StringBuilder()
		.append(" select \"日充值金额(元)\", \"日提现金额(元)\", \"日投资金额(元)\", \"日发布金额(元)\", \"日转让金额(元)\", \"日新增注册用户数\",\"当日新增投资用户数\", \"当日投资用户总数\", \"历史总用户数\", \"库存单数\", \"库存金额\" ")
		.append(" from ")
		.append(" ( ")
		.append(" select count(1) \"日新增注册用户数\" ")
		.append(" from bao_t_cust_info t ")
		.append(" where t.create_date > trunc(?-1)  + 17/24  and t.create_date <= trunc(?) + 17/24  ")
		.append(" )a, ")
		.append(" ( ")
		.append("   select nvl(sum(s.trade_amount),0) \"日充值金额(元)\" ")
		.append("   from bao_t_account_flow_info s ")
		.append("   where s.trade_type = '充值'  ")
		.append("   and s.create_date > trunc(?-1)  + 17/24  and s.create_date <= trunc(?) + 17/24  ")
		.append(" )b, ")
		.append(" ( ")
		.append("   select nvl(sum(s.trade_amount),0) \"日提现金额(元)\" ")
		.append("   from bao_t_trade_flow_info s ")
		.append("   where s.trade_type = '提现' and s.trade_desc = '提现' ")
		.append("   and s.create_date > trunc(?-1)  + 17/24  and s.create_date <= trunc(?) + 17/24  ")
		.append(" )c, ")
		.append(" ( ")
		.append("   select nvl(sum(s.loan_amount),0) \"日发布金额(元)\" ")
		.append("   from bao_t_loan_info s ")
		.append("   where s.PUBLISH_DATE > trunc(?-1)  + 17/24  and s.PUBLISH_DATE <= trunc(?) + 17/24  ")
		.append(" )c, ")
		.append(" ( ")
		.append(" select nvl(sum(s.invest_amount),0) \"日投资金额(元)\" ")
		.append(" from bao_t_invest_info s ")
		.append(" where s.create_date > trunc(?-1)  + 17/24  and s.create_date <= trunc(?) + 17/24  ")
		.append(" )e, ")
		.append(" ( ")
		.append(" select nvl(sum(s.invest_amount),0) \"日转让金额(元)\" ")
		.append(" from bao_t_invest_info s ")
		.append(" where s.INVEST_MODE = '转让'  ")
		.append(" and s.create_date > trunc(?-1)  + 17/24  and s.create_date <= trunc(?) + 17/24  ")
		.append(" )f, ")
		.append(" ( ")
		.append("   select count(distinct s.cust_id) \"当日新增投资用户数\" ")
		.append("   from bao_t_invest_info s ")
		.append("   where s.create_date > trunc(?-1)  + 17/24  and s.create_date <= trunc(?) + 17/24  ")
		.append("   and s.cust_id in ( ")
		.append("       select t.id ")
		.append("       from bao_t_cust_info t ")
		.append("       where t.create_date > trunc(?-1)  + 17/24  and t.create_date <= trunc(?) + 17/24  ")
		.append("   ) ")
		.append(" )g, ")
		.append(" ( ")
		.append("   select count(distinct s.cust_id) \"当日投资用户总数\" ")
		.append("   from bao_t_invest_info s ")
		.append("   where s.create_date > trunc(?-1)  + 17/24  and s.create_date <= trunc(?) + 17/24  ")
		.append(" )h, ")
		.append(" ( ")
		.append("   select count(1) \"历史总用户数\" ")
		.append("   from bao_t_cust_info t ")
		.append("   where t.mobile is not null ")
		.append(" )i, ")
		.append(" ( ")
		.append("   select count(t.loan_amount) \"库存单数\", sum(t.loan_amount) \"库存金额\" ")
		.append("   from bao_t_loan_info t ")
		.append("   where t.loan_status = '待发布' ")
		.append(" )j ")
		;
		
		List<Object> args = new ArrayList<Object>();
		Date date = new Date();
		for (int i = 0; i < 18; i++) {
			args.add(date);	
		}
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
		
	}

	@Override
	public List<Map<String, Object>> dailyDataPropellingSummary(
			Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" select  ")
		.append(" tci.cust_name  \"客户名称\" ")
		.append(" ,substr(tci.mobile, 1, 3) || '****' ||substr(tci.mobile, length(tci.mobile)-3, 4)  \"手机号\" ")
		.append(" ,tii.invest_date  \"投资日期\" ")
		.append(" ,tii.invest_amount  \"投资金额\" ")
		.append(" ,tii.invest_status  \"投资状态\" ")
		.append(" ,tli.loan_title  \"投资标的\" ")
		.append(" from bao_t_invest_info tii  ")
		.append(" left join bao_t_loan_info tli on tii.loan_id=tli.id ")
		.append(" left join bao_t_cust_info tci on tii.cust_id=tci.id ")
		.append(" where tii.record_status='有效'  ")
		.append(" and tii.cust_id in ")
		.append(" ( ")
		.append(" select distinct tdi.cust_id ")
		.append(" from bao_t_device_info tdi ")
		.append(" where tdi.record_status='有效' ")
		.append(" and tdi.trade_type='注册' ")
		.append(" and request_url like '%http://m.shanlincaifu.com/register?channelNo=jinxiansheng%' ")
		.append(" ) ")
		.append(" and tii.invest_date = to_char(? - 1, 'yyyyMMdd') ")
		.append(" order by tii.create_date desc ");
		
		List<Object> args = new ArrayList<Object>();
		Date date = new Date();
		args.add(date);	
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}

	@Override
	public List<Map<String, Object>> dailyDataYZloanAccountSummary(
			Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append("select ")
		.append(" t.loan_code \"借款编号\"")
		.append(" ,t.company_name \"公司名称\"")
		.append(" ,t.loan_amount \"借款金额\"")
		.append(" ,t.grant_status \"放款状态\"")
		.append(" ,t.grant_date \"放款时间\"")
		.append(" ,t.create_date \"推标时间\"")
		.append("  from bao_t_loan_info t ")
		.append("  where t.loan_status = '正常' and t.company_name = '意真金融' ")
		.append("  and to_char(t.grant_date,'yyyy-mm-dd') ='"+params.get("grantDate")+"' ");
		List<Object> args = new ArrayList<Object>();
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
		
	
	}
	
}
