package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.ChannelManagerRepositoryCustom;
import com.slfinance.shanlincaifu.utils.SqlCondition;

@Repository
public class ChannelManagerRepositoryImpl implements ChannelManagerRepositoryCustom{
	@Autowired
	RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Page<Map<String, Object>> queryChannelInfoList(
			Map<String, Object> param) {

		StringBuilder sql = new StringBuilder()
		.append(" select t.dept_name        \"deptName\", ")
		.append("        t.dept_manager     \"deptManager\", ")
		.append("        t.channel_name     \"channelName\", ")
		.append("        t.channel_source   \"channelSource\", ")
		.append("        t.channel_no       \"channelNo\", ")
		.append("        t.record_status    \"recordStatus\", ")
		.append("        t.last_update_date \"operateDate\" ")
		.append("   from BAO_T_CHANNEL_INFO t where 1=1 ");
		SqlCondition sqlCondition = new SqlCondition(sql, param)
		.addLike("deptName", "t.DEPT_NAME")
		.addLike("recordStatus", "t.RECORD_STATUS")
		.addLike("channelName", "t.CHANNEL_NAME")
		.addLike("channelNo", "t.CHANNEL_NO")
		;
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}

	public Page<Map<String, Object>> queryChannelCountList(Map<String, Object> param) {
		SqlCondition sqlCondition = getQueryChannelCountSql(param);
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}

	public Page<Map<String, Object>> queryInvestAmountDetail(
			Map<String, Object> param) {
		ArrayList<Object> listObject = new ArrayList<Object>();

		StringBuilder sql = new StringBuilder()
				.append(" select * ")
				.append("   from (select a.channel_no \"channelNo\",cust.id, ")
				.append("                cust.cust_name \"custName\", ")
				.append("                substr(cust.mobile, 1, 3) || '****' || ")
				.append("                substr(cust.mobile, length(cust.mobile) - 3, 4) \"mobile\", ")
				.append("                sum(i.invest_amount) \"investAmount\", ")
//				.append("                decode(i.transfer_apply_id, ")
//				.append("                       null, ")
//				.append("                       loan.id, ")
//				.append("                       ta.id) \"loanId\", ")
				.append("                loan.loan_code \"loanId\", ")//借款编号
				.append("                i.invest_status \"investStatus\", ")
				.append("                loan.loan_type \"loanType\", ")
				.append("                decode(decode(i.transfer_apply_id, ")
				.append("                              null, ")
				.append("                              loan.seat_term, ")
				.append("                              ta.transfer_seat_term), ")
				.append("                       -1, ")
				.append("                       '不可转让', ")
				.append("                       '可转让') \"seatTerm\", ")
				.append("                nvl(TRUNC(ld.YEAR_IRR * 100, 2), 0) || '%' \"yearIrr\", ")
				.append("                decode(i.transfer_apply_id, ")
				.append("                       null, ")
				.append("                       loan.loan_term, ")
				.append("                       ceil(months_between(loan.invest_end_date, sysdate))) \"loanTerm\", ")
				.append("                loan.repayment_method \"repaymentMethod\", ")
				.append("                max(i.invest_date) \"investDate\" ")
				.append("           from BAO_T_CHANNEL_INFO a ")
//				.append("          inner join bao_t_device_info b ")
//				.append("             on a.channel_name = b.channel_no ")
//				.append("            and a.record_status = '已生效' ")
//				.append("            and b.trade_type in ")
//				.append("                ('购买优选项目', '购买债权转让', '购买优选计划') ")
				.append("          inner join bao_T_cust_info cust ")
//				.append("             on cust.id = b.cust_id ")
				.append("             on cust.channel_source = a.channel_name ")
				.append("            and a.record_status = '已生效' ")
				.append("          inner join bao_t_invest_info i ")
//				.append("             on i.id = b.relate_primary ")
				.append("             on i.cust_id = cust.id ")
//				.append("            and i.invest_status in ('投资中', ")
//				.append("                                    '收益中', ")
//				.append("                                    '提前赎回中', ")
//				.append("                                    '提前赎回', ")
//				.append("                                    '到期赎回中', ")
//				.append("                                    '到期赎回', ")
//				.append("                                    '已到期', ")
//				.append("                                    '已转让') ")
				.append("           inner join bao_t_loan_info loan ")
				.append("             on i.loan_id = loan.id ")
				.append("            and loan.loan_status in ('正常','已到期') ")
				.append("           inner join BAO_T_LOAN_DETAIL_INFO ld ")
				.append("             on ld.loan_id = loan.id ")
				.append("           left join bao_t_loan_transfer_apply ta ")
				.append("             on ta.id = i.transfer_apply_id ")
				.append("          group by cust.cust_name, ")
				.append("                   cust.id, ")
				.append("                   cust.mobile, ")
//				.append("                decode(i.transfer_apply_id, ")
//				.append("                       null, ")
//				.append("                       loan.id, ")
//				.append("                       ta.id), ")
				.append("                   loan.loan_code, ")
				.append("                   i.invest_status，loan.loan_type, ")
				.append("                   i.transfer_apply_id, ")
				.append("                   loan.repayment_method, ")
				.append("                   decode(i.transfer_apply_id, ")
				.append("                          null, ")
				.append("                          loan.seat_term, ")
				.append("                          ta.transfer_seat_term), ")
				.append("                   ld.YEAR_IRR, ")
				.append("                   decode(i.transfer_apply_id, ")
				.append("                          null, ")
				.append("                          loan.loan_term, ")
				.append("                          ceil(months_between(loan.invest_end_date, sysdate))),a.channel_no ")
				.append("          order by max(i.invest_date) desc) t  ")
				.append("          where  \"channelNo\" = ?  ");
		listObject.add(param.get("channelNo"));
		if (!StringUtils.isEmpty(param.get("investStartDate"))) {
			sql.append(" and to_date(\"investDate\",'yyyy-MM-dd hh24:mi:ss') >= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
			listObject.add(param.get("investStartDate").toString());
		}
		if (!StringUtils.isEmpty(param.get("investEndDate"))) {
			sql.append(" and to_date(\"investDate\",'yyyy-MM-dd hh24:mi:ss') <= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
			listObject.add(param.get("investEndDate").toString());
		}
		SqlCondition sqlCondition = new SqlCondition(sql, param, listObject);
		return repositoryUtil.queryForPageMap(sqlCondition.toString(),sqlCondition.toArray(),Integer.parseInt(param.get("start").toString()),Integer.parseInt(param.get("length").toString()));

	}

	public Map<String, Object> queryToatlChannelInfo(Map<String, Object> param) {
		SqlCondition sqlCondition = getQueryChannelCountSql(param);
		StringBuilder sql = new StringBuilder()
				.append(" SELECT sum(\"registerCount\") \"registerCountTotal\",sum(\"rechargeAmount\") \"rechargeAmountTotal\",sum(\"depositAmount\") \"depositAmountTotal\",sum(\"investAmount\") \"investAmountTotal\" FROM ( ")
				.append(sqlCondition.toString()).append(" ) ");
		Map<String, Object> totalAmount = jdbcTemplate.queryForMap(
				sql.toString(), sqlCondition.toArray());
		return totalAmount;
	}

	private SqlCondition getQueryChannelCountSql(Map<String, Object> param) {
		ArrayList<Object> listObject = new ArrayList<Object>();

		StringBuilder sql = new StringBuilder()
				// .append(" select t.*,(select COUNT(distinct(cust.id)) ")
				// .append("           from BAO_T_CHANNEL_INFO a, ")
				// .append("                bao_t_device_info  b, ")
				// .append("                bao_T_cust_info    cust ")
				// .append("          where cust.id = b.relate_primary ");
				// if (!StringUtils.isEmpty(param.get("startDate"))) {
				// sql.append(" and cust.create_date >= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
				// listObject.add(param.get("startDate").toString());
				// }
				// if (!StringUtils.isEmpty(param.get("endDate"))) {
				// sql.append(" and cust.create_date <= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
				// listObject.add(param.get("endDate").toString());
				// }
				// sql.append("            and a.channel_name = b.channel_no ")
				// .append("            and a.record_status = '已生效' ")
				// .append("            and b.trade_type = '注册' and a.channel_no=\"channelNo\" ")
				// .append("            ) \"registerCount\", ")
				// .append("        (select COUNT(distinct(cust.id)) ")
				// .append("           from BAO_T_CHANNEL_INFO a, ")
				// .append("                bao_t_device_info  b, ")
				// .append("                bao_T_cust_info    cust ")
				// .append("          where cust.id = b.relate_primary ")
				// .append("            and a.channel_name = b.channel_no ")
				// .append("            and a.record_status = '已生效' ")
				// .append("            and b.trade_type = '实名认证' and a.channel_no=\"channelNo\") \"realNameCount\", ")
				// .append("        (select COUNT(distinct(cust.id)) ")
				// .append("           from BAO_T_CHANNEL_INFO a, ")
				// .append("                bao_t_device_info  b, ")
				// .append("                bao_T_cust_info    cust, ")
				// .append("                BAO_T_TRADE_FLOW_INFO acc ")
				// .append("          where cust.id = b.cust_id ");
				// if (!StringUtils.isEmpty(param.get("startDate"))) {
				// sql.append(" and acc.create_date >= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
				// listObject.add(param.get("startDate").toString());
				// }
				// if (!StringUtils.isEmpty(param.get("endDate"))) {
				// sql.append(" and acc.create_date <= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
				// listObject.add(param.get("endDate").toString());
				// }
				// sql.append("            and a.channel_name = b.channel_no ")
				// .append("            and b.relate_primary = acc.id ")
				// .append("            and a.record_status = '已生效' ")
				// .append("            and acc.trade_status = '处理成功' ")
				// .append("            and b.trade_type = '充值' and a.channel_no=\"channelNo\") \"rechargeCount\", ")
				// .append("        (select nvl(sum(acc.trade_amount),0) ")
				// .append("           from BAO_T_CHANNEL_INFO    a, ")
				// .append("                bao_t_device_info     b, ")
				// .append("                bao_T_cust_info       cust, ")
				// .append("                BAO_T_TRADE_FLOW_INFO acc ")
				// .append("          where b.relate_primary = acc.id ");
				// if (!StringUtils.isEmpty(param.get("startDate"))) {
				// sql.append(" and acc.create_date >= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
				// listObject.add(param.get("startDate").toString());
				// }
				// if (!StringUtils.isEmpty(param.get("endDate"))) {
				// sql.append(" and acc.create_date <= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
				// listObject.add(param.get("endDate").toString());
				// }
				// sql.append("            and a.channel_name = b.channel_no ")
				// .append("            and cust.id = b.cust_id ")
				// .append("            and a.record_status = '已生效' ")
				// .append("            and acc.trade_status = '处理成功' ")
				// .append("            and b.trade_type = '充值' and a.channel_no=\"channelNo\") \"rechargeAmount\", ")
				// .append("        (select COUNT(distinct(cust.id)) ")
				// .append("           from BAO_T_CHANNEL_INFO a, ")
				// .append("                bao_t_device_info  b, ")
				// .append("                bao_T_cust_info    cust, ")
				// .append("                BAO_T_TRADE_FLOW_INFO acc ")
				// .append("          where b.relate_primary = acc.id ");
				// if (!StringUtils.isEmpty(param.get("startDate"))) {
				// sql.append(" and acc.create_date >= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
				// listObject.add(param.get("startDate").toString());
				// }
				// if (!StringUtils.isEmpty(param.get("endDate"))) {
				// sql.append(" and acc.create_date <= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
				// listObject.add(param.get("endDate").toString());
				// }
				// sql.append("            and a.channel_name = b.channel_no ")
				// .append("            and cust.id = b.cust_id ")
				// .append("            and a.record_status = '已生效' ")
				// .append("            and acc.trade_status = '处理成功' ")
				// .append("            and b.trade_type = '提现' and a.channel_no=\"channelNo\") \"depositCount\", ")
				// .append("         ")
				// .append("        (select nvl(sum(acc.trade_amount),0) ")
				// .append("           from BAO_T_CHANNEL_INFO    a, ")
				// .append("                bao_t_device_info     b, ")
				// .append("                bao_T_cust_info       cust, ")
				// .append("                BAO_T_TRADE_FLOW_INFO acc ")
				// .append("          where cust.id = b.cust_id ");
				// if (!StringUtils.isEmpty(param.get("startDate"))) {
				// sql.append(" and acc.create_date >= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
				// listObject.add(param.get("startDate").toString());
				// }
				// if (!StringUtils.isEmpty(param.get("endDate"))) {
				// sql.append(" and acc.create_date <= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
				// listObject.add(param.get("endDate").toString());
				// }
				// sql.append("            and a.channel_name = b.channel_no ")
				// .append("            and b.relate_primary = acc.id ")
				// .append("            and a.record_status = '已生效' ")
				// .append("            and acc.trade_status = '处理成功' ")
				// .append("            and b.trade_type = '提现' and a.channel_no=\"channelNo\") \"depositAmount\", ")
				// .append("        (select count(distinct(cust.id)) ")
				// .append("           from BAO_T_CHANNEL_INFO a, ")
				// .append("                bao_t_device_info  b, ")
				// .append("                bao_T_cust_info    cust, ")
				// .append("                bao_t_invest_info  i ")
				// .append("          where cust.id = b.cust_id ");
				// if (!StringUtils.isEmpty(param.get("startDate"))) {
				// sql.append(" and i.create_date >= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
				// listObject.add(param.get("startDate").toString());
				// }
				// if (!StringUtils.isEmpty(param.get("endDate"))) {
				// sql.append(" and i.create_date <= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
				// listObject.add(param.get("endDate").toString());
				// }
				// sql.append("            and a.channel_name = b.channel_no ")
				// .append("            and b.relate_primary = i.id ")
				// .append("            and a.record_status = '已生效' ")
				// .append("            and i.invest_status in ('投资中', ")
				// .append("                                    '收益中', ")
				// .append("                                    '提前赎回中', ")
				// .append("                                    '提前赎回', ")
				// .append("                                    '到期赎回中', ")
				// .append("                                    '到期赎回', ")
				// .append("                                    '已到期', ")
				// .append("                                    '已转让') ")
				// .append("            and b.trade_type in ")
				// .append("                ('购买优选项目', '购买债权转让', '购买优选计划') and a.channel_no=\"channelNo\") \"investCount\", ")
				// .append("         ")
				// .append("        (select nvl(sum(i.invest_amount),0) ")
				// .append("           from BAO_T_CHANNEL_INFO a, ")
				// .append("                bao_t_device_info  b, ")
				// .append("                bao_T_cust_info    cust, ")
				// .append("                bao_t_invest_info  i ")
				// .append("          where cust.id = b.cust_id ");
				// if (!StringUtils.isEmpty(param.get("startDate"))) {
				// sql.append(" and i.create_date >= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
				// listObject.add(param.get("startDate").toString());
				// }
				// if (!StringUtils.isEmpty(param.get("endDate"))) {
				// sql.append(" and i.create_date <= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
				// listObject.add(param.get("endDate").toString());
				// }
				// sql.append("            and a.channel_name = b.channel_no ")
				// .append("            and b.relate_primary = i.id ")
				// .append("            and a.record_status = '已生效' ")
				// .append("            and i.invest_status in ('投资中', ")
				// .append("                                    '收益中', ")
				// .append("                                    '提前赎回中', ")
				// .append("                                    '提前赎回', ")
				// .append("                                    '到期赎回中', ")
				// .append("                                    '到期赎回', ")
				// .append("                                    '已到期', ")
				// .append("                                    '已转让') ")
				// .append("            and b.trade_type in ")
				// .append("                ('购买优选项目', '购买债权转让', '购买优选计划') and a.channel_no=\"channelNo\") \"investAmount\", ")
				// .append("         ")
				// .append("        (select nvl(sum(i.invest_amount * loan.loan_term / ")
				// .append("                    decode(loan.loan_unit, '月', 12, '天', 360, 12)),0) ")
				// .append("           from BAO_T_CHANNEL_INFO a, ")
				// .append("                bao_t_device_info  b, ")
				// .append("                bao_T_cust_info    cust, ")
				// .append("                bao_t_invest_info  i, ")
				// .append("                bao_t_loan_info    loan ")
				// .append("          where cust.id = b.cust_id ");
				// if (!StringUtils.isEmpty(param.get("startDate"))) {
				// sql.append(" and i.create_date >= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
				// listObject.add(param.get("startDate").toString());
				// }
				// if (!StringUtils.isEmpty(param.get("endDate"))) {
				// sql.append(" and i.create_date <= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
				// listObject.add(param.get("endDate").toString());
				// }
				// sql.append("            and i.loan_id = loan.id ")
				// .append("            and i.cust_id = cust.id ")
				// .append("            and a.channel_name = b.channel_no ")
				// .append("            and b.relate_primary = i.id ")
				// .append("            and a.record_status = '已生效' ")
				// .append("            and i.invest_status in ('投资中', ")
				// .append("                                    '收益中', ")
				// .append("                                    '提前赎回中', ")
				// .append("                                    '提前赎回', ")
				// .append("                                    '到期赎回中', ")
				// .append("                                    '到期赎回', ")
				// .append("                                    '已到期', ")
				// .append("                                    '已转让') ")
				// .append("            and b.trade_type in ")
				// .append("                ('购买优选项目', '购买债权转让', '购买优选计划') and a.channel_no=\"channelNo\") \"yearInvestAmount\" from (select a.dept_name \"deptName\", ")
				// .append("        a.dept_manager \"deptManager\", ")
				// .append("        a.channel_name \"channelName\", ")
				// .append("        a.channel_source \"channelSource\",  ")
				// .append("        a.channel_no \"channelNo\"   ")
				// .append(" from BAO_T_CHANNEL_INFO a, bao_t_device_info b ")
				// .append("  where a.record_status='已生效' ")
				// .append("  group by a.dept_name, a.dept_manager, a.channel_name, a.channel_source,a.channel_no) t  where 1=1 ");
				.append(" select * ")
				.append("   from (select \"deptName\", ")
				.append("        \"deptManager\", ")
				.append("        \"channelName\", ")
				.append("        \"channelSource\", ")
				.append("        \"channelNo\", ")
				.append("        \"registerCount\", ")
				.append("        \"realNameCount\", ")
				.append("        \"rechargeCount\", ")
				.append("        \"rechargeAmount\", ")
				.append("        \"depositCount\", ")
				.append("        \"depositAmount\", ")
				.append("        \"investCount\", ")
				.append("        \"investAmount\", ")
				.append("        \"yearInvestAmount\" ")
				.append("   from (select a.dept_name      \"deptName\", ")
				.append("                a.dept_manager   \"deptManager\", ")
				.append("                a.channel_name   \"channelName\", ")
				.append("                a.channel_source \"channelSource\", ")
				.append("                a.channel_no     \"channelNo\" ")
				.append("           from BAO_T_CHANNEL_INFO a ")
				.append("          where a.record_status = '已生效' ")
				.append("          group by a.dept_name, ")
				.append("                   a.dept_manager, ")
				.append("                   a.channel_name, ")
				.append("                   a.channel_source, ")
				.append("                   a.channel_no) t ")
				.append("   left join (select COUNT(cust.id) \"registerCount\", ")
				.append("                     count(case ")
				.append("                             when cust.credentials_code is not null then ")
				.append("                              1 ")
				.append("                           end) \"realNameCount\", ")
				.append("                     a.channel_no ")
				.append("                from BAO_T_CHANNEL_INFO a, bao_T_cust_info cust ")
				.append("               where a.channel_name = cust.channel_source ");
		if (!StringUtils.isEmpty(param.get("startDate"))) {
			sql.append(" and cust.create_date >= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
			listObject.add(param.get("startDate").toString());
		}
		if (!StringUtils.isEmpty(param.get("endDate"))) {
			sql.append(" and cust.create_date <= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
			listObject.add(param.get("endDate").toString());
		}
		;
		sql.append("               group by a.channel_no) b ")
				.append("     on b.channel_no = t.\"channelNo\" ")
				.append("   left join (select TRUNC_AMOUNT_WEB(sum(i.invest_amount * loan.loan_term / ")
				.append("                             decode(loan.loan_unit, '月', 12, '天', 360, 12)) ")
				.append("                         ) \"yearInvestAmount\", ")
				.append("                     TRUNC_AMOUNT_WEB(sum(i.invest_amount)) \"investAmount\", ")
				.append("                     count(distinct(cust.id)) \"investCount\", ")
				.append("                     a.channel_no ")
				.append("                from BAO_T_CHANNEL_INFO a, ")
				.append("                     bao_T_cust_info    cust, ")
				.append("                     bao_t_invest_info  i, ")
				.append("                     bao_t_loan_info    loan ")
				.append("               where a.channel_name = cust.channel_source ");
		if (!StringUtils.isEmpty(param.get("startDate"))) {
			sql.append(" and i.create_date >= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
			listObject.add(param.get("startDate").toString());
		}
		if (!StringUtils.isEmpty(param.get("endDate"))) {
			sql.append(" and i.create_date <= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
			listObject.add(param.get("endDate").toString());
		}
		sql.append("                 and i.loan_id = loan.id ")
				.append("                 and i.cust_id = cust.id ")
//				.append("                 and i.invest_status in ('投资中', ")
//				.append("                                         '收益中', ")
//				.append("                                         '提前赎回中', ")
//				.append("                                         '提前赎回', ")
//				.append("                                         '到期赎回中', ")
//				.append("                                         '到期赎回', ")
//				.append("                                         '已到期', ")
//				.append("                                         '已转让') ")
				.append("                 and loan.loan_status in ('正常','已到期') ")
				.append("               group by a.channel_no) t1 ")
				.append("     on t1.channel_no = t.\"channelNo\" ")
				.append("   left join (select TRUNC_AMOUNT_WEB(sum(acc.trade_amount)) \"depositAmount\", ")
				.append("                     COUNT(distinct(cust.id)) \"depositCount\", ")
				.append("                     a.channel_no ")
				.append("                from BAO_T_CHANNEL_INFO    a, ")
				.append("                     bao_T_cust_info       cust, ")
				.append("                     BAO_T_TRADE_FLOW_INFO acc ")
				.append("               where cust.id = acc.cust_id ")
				.append("                 and a.channel_name = cust.channel_source ");
		if (!StringUtils.isEmpty(param.get("startDate"))) {
			sql.append(" and acc.create_date >= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
			listObject.add(param.get("startDate").toString());
		}
		if (!StringUtils.isEmpty(param.get("endDate"))) {
			sql.append(" and acc.create_date <= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
			listObject.add(param.get("endDate").toString());
		}
		sql.append("                 and acc.trade_status = '处理成功' ")
				.append("                 and acc.trade_type = '提现' ")
				.append("               group by a.channel_no) t2 ")
				.append("     on t2.channel_no = t.\"channelNo\" ")
				.append("   left join (select TRUNC_AMOUNT_WEB(sum(acc.trade_amount)) \"rechargeAmount\", ")
				.append("                     COUNT(distinct(cust.id)) \"rechargeCount\", ")
				.append("                     a.channel_no ")
				.append("                from BAO_T_CHANNEL_INFO    a, ")
				.append("                     bao_T_cust_info       cust, ")
				.append("                     BAO_T_TRADE_FLOW_INFO acc ")
				.append("               where a.channel_name = cust.channel_source ");
		if (!StringUtils.isEmpty(param.get("startDate"))) {
			sql.append(" and acc.create_date >= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
			listObject.add(param.get("startDate").toString());
		}
		if (!StringUtils.isEmpty(param.get("endDate"))) {
			sql.append(" and acc.create_date <= to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
			listObject.add(param.get("endDate").toString());
		}
		sql.append("                 and cust.id = acc.cust_id ")
				.append("                 and acc.trade_status = '处理成功' ")
				.append("                 and acc.trade_type = '充值' ")
				.append("               group by a.channel_no) t3 ")
				.append("     on t3.channel_no = t.\"channelNo\" ")
		        .append("     ) s where 1=1 ");
		SqlCondition sqlCondition = new SqlCondition(sql, param, listObject)
				.addLike("deptName", "s.\"deptName\"")
				.addLike("deptManager", "s.\"deptManager\"")
				.addLike("channelName", "s.\"channelName\"")
				.addLike("channelNo", "s.\"channelNo\"");
		
		if (!StringUtils.isEmpty(param.get("channelSource"))) {
			sql.append(" and upper(s.\"channelSource\") like upper(?) ");
			listObject.add("%" + param.get("channelSource").toString() + "%");
		}
		return sqlCondition;
	}


}
