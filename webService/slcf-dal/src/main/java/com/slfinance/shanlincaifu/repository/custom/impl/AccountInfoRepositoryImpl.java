/** 
 * @(#)AccountInfoRepositoryImpl.java 1.0.0 2015年4月25日 下午12:09:23  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.AccountInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SqlCondition;
import com.slfinance.shanlincaifu.utils.SubjectConstant;

/**
 * 自定义账户数据访问类
 * 
 * @author wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月25日 下午12:09:23 $
 */
@Repository
public class AccountInfoRepositoryImpl implements AccountInfoRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 用户资金统计
	 *
	 * @author wangjf
	 * @date 2015年4月25日 上午11:24:31
	 * @param param
	 *            <tt>nickName： String:用户昵称</tt><br>
	 *            <tt>custName： String:真实姓名</tt><br>
	 *            <tt>credentialsCode： String:证件号码</tt><br>
	 *            <tt>opearteDateBegin： Date:操作开始时间</tt><br>
	 *            <tt>opearteDateEnd： Date:操作结束时间</tt><br>
	 *            <tt>tradeType：String:交易类型</tt><br>
	 * @return <tt>totalAccountTotalAmount：BigDecimal:账户余额</tt><br>
	 *         <tt>totalAccountAvailable： BigDecimal:可用余额</tt><br>
	 *         <tt>totalAccountFreezeAmount： BigDecimal:冻结余额</tt><br>
	 *         <tt>totalIncomeAmount： BigDecimal:已得收益</tt><br>
	 */
	@Override
	public Map<String, Object> findAllCustAccountSum(Map<String, Object> param) {
		StringBuffer whereCustSqlString = new StringBuffer();
		StringBuffer whereAccountSqlString = new StringBuffer();

		List<Object> objList = new ArrayList<Object>();

		if (!StringUtils.isEmpty(param.get("nickName"))) {
			whereCustSqlString.append(" and LOGIN_NAME LIKE ?");
			objList.add(new StringBuffer().append("%")
					.append(param.get("nickName")).append("%"));
		}

		if (!StringUtils.isEmpty(param.get("custName"))) {
			whereCustSqlString.append(" and CUST_NAME = ?");
			objList.add(param.get("custName"));
		}

		if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
			whereCustSqlString.append(" and CREDENTIALS_CODE = ?");
			objList.add(param.get("credentialsCode"));
		}

		// 过滤公司账户
		whereAccountSqlString.append(" and T.ID NOT IN (?, ?, ?, ?, ?, ?, ?, ?)");
		objList.add(Constant.ACCOUNT_ID_CENTER);
		objList.add(Constant.ACCOUNT_ID_ERAN);
		objList.add(Constant.ACCOUNT_ID_RISK);
		objList.add(Constant.ACCOUNT_ID_PROJECT_ERAN);
		objList.add(Constant.ACCOUNT_ID_PROJECT_RISK);
		objList.add(Constant.ACCOUNT_ID_REPAYMENT);
		objList.add(Constant.ACCOUNT_ID_WEALTH_CENTER);
		objList.add(Constant.ACCOUNT_ID_COMMISION);

		if (!StringUtils.isEmpty(param.get("opearteDateBegin"))) {
			whereAccountSqlString.append(" and T.LAST_UPDATE_DATE >= ?");
			objList.add(DateUtils.parseStandardDate((String) param
					.get("opearteDateBegin")));
		}

		if (!StringUtils.isEmpty(param.get("opearteDateEnd"))) {
			whereAccountSqlString.append(" and T.LAST_UPDATE_DATE <= ?");
			objList.add(DateUtils.getEndDate(DateUtils
					.parseStandardDate((String) param.get("opearteDateEnd"))));
		}

		if (!StringUtils.isEmpty(param.get("tradeType"))) {
			whereAccountSqlString
					.append(" and T.ID IN (SELECT ACCOUNT_ID FROM BAO_T_ACCOUNT_FLOW_INFO WHERE Trade_Type = ?)");
			objList.add(param.get("tradeType"));
		}
		
		// 已得收益中排除本金（收支明细表）
		List<String> excludeSubjectTypeList = Arrays.asList(
				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_LOAN,
				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PRINCIPAL_LOAN,
				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_PROJECT,
				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PRINCIPAL_PROJECT
		);
		
		String excludeSubjectTypeString = Joiner.on(",").join(Iterables.transform(excludeSubjectTypeList,
			      new Function<String, String>() {
			          @Override
			          public String apply(String input) {
			              return String.format("'%s'", input);
			          }
			      }));
		
		// 已得收益中优选计划回款（收支总表）
		List<String> excludeTradeTypeList = Arrays.asList(
				SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_WEALTH
		);
		
		String excludeTradeTypeString = Joiner.on(",").join(Iterables.transform(excludeTradeTypeList,
			      new Function<String, String>() {
			          @Override
			          public String apply(String input) {
			              return String.format("'%s'", input);
			          }
			      }));
		
		StringBuffer sqlString= new StringBuffer()
		.append(" select TO_CHAR(NVL(SUM(totalAccountTotalAmount), 0)) \"totalAccountTotalAmount\", TO_CHAR(NVL(SUM(totalAccountAvailable), 0)) \"totalAccountAvailable\", TO_CHAR(NVL(SUM(totalAccountFreezeAmount), 0)) \"totalAccountFreezeAmount\", TO_CHAR(NVL(SUM(totalIncomeAmount), 0)) \"totalIncomeAmount\" ")
		.append(" from  ")
		.append(" ( ")
		.append("   select T.id, T.CUST_ID, NVL(sum(TRUNC_AMOUNT_WEB(t.ACCOUNT_TOTAL_AMOUNT)),0) totalAccountTotalAmount, NVL(sum(TRUNC_AMOUNT_WEB(t.account_available_amount)),0) totalAccountAvailable,  ")
		.append("          NVL(sum(TRUNC_AMOUNT_WEB(t.account_freeze_amount)), 0) totalAccountFreezeAmount ")
		.append("   from BAO_T_ACCOUNT_INFO T ")
		.append("   where T.CUST_ID IN (SELECT S.ID FROM BAO_T_CUST_INFO S WHERE (S.CUST_KIND IS NULL OR S.CUST_KIND = '网站用户')) %s")
		.append("   group by t.id, T.CUST_ID ")
		.append(" )AA ")
		.append(" left join  ")
		.append(" ( ")
		.append(" select c.cust_id cust_id, sum(c.payment_interest) totalIncomeAmount ")
		.append("                from (select a.cust_id cust_id, ")
		.append("                             nvl(sum(trunc(b.trade_amount, 2)), 0) payment_interest ")
		.append("                        from bao_t_payment_record_info   a, ")
		.append("                             bao_t_payment_record_detail b ")
		.append("                       where a.id = b.pay_record_id ")
		.append("                         and b.subject_type not in (" + excludeSubjectTypeString + ") and a.trade_type not in (" + excludeTradeTypeString +") ")
		.append("                       group by a.cust_id ")
		.append("                      union all ")
		.append("                      select w.cust_id cust_id, ")
		.append("                             nvl(sum(trunc(w.fact_payment_amount, 2) - trunc(w.except_payment_principal, 2)), 0) + nvl(max(b.atone_expenses), 0) payment_interest ")
		.append("                        from bao_t_wealth_payment_plan_info w, ")
		.append("                            (select nvl(sum(a.atone_expenses), 2) atone_expenses, a.cust_id cust_id from bao_t_atone_info a where a.atone_method = '提前赎回' and a.atone_status = '处理成功' group by a.cust_id) b")
		.append("                       where w.payment_status = '已回收' and w.cust_id = b.cust_id (+) ")
		.append("                       group by w.cust_id) c ")
		.append("               group by c.cust_id ")
		.append(" )BB ON AA.CUST_ID = BB.CUST_ID ");
		
		StringBuffer whereSqlString = new StringBuffer();
		if (!StringUtils.isEmpty(whereCustSqlString.toString())) {
			whereSqlString
					.append(String
							.format(" and CUST_ID IN (select id from BAO_T_CUST_INFO where 1=1 %s)",
									whereCustSqlString.toString()));
		}

		if (!StringUtils.isEmpty(whereAccountSqlString.toString())) {
			whereSqlString.append(String.format(" %s",
					whereAccountSqlString.toString()));
		}

		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				String.format(sqlString.toString(), whereSqlString.toString()),
				objList.toArray());
		return list.get(0);
	}

	/**
	 * 用户资金列表
	 *
	 * @author wangjf
	 * @date 2015年4月25日 上午11:28:39
	 * @param param
	 *            <tt>start：int:分页起始页</tt><br>
	 *            <tt>length：int:每页长度</tt><br>
	 *            <tt>nickName：String:用户昵称</tt><br>
	 *            <tt>custName：String:真实姓名</tt><br>
	 *            <tt>credentialsCode：String:证件号码</tt><br>
	 *            <tt>opearteDateBegin：String:操作开始时间</tt><br>
	 *            <tt>opearteDateEnd：String:操作开始时间</tt><br>
	 * @return <tt>id：String:用户ID</tt><br>
	 *         <tt>nickName： String:用户昵称</tt><br>
	 *         <tt>custName： String:真实姓名</tt><br>
	 *         <tt>credentialsCode： String:证件号码</tt><br>
	 *         <tt>accountAvailable： BigDecimal:可用余额</tt><br>
	 *         <tt>accountFreezeAmount： BigDecimal:冻结余额</tt><br>
	 *         <tt>incomeAmount： BigDecimal:已得收益</tt><br>
	 *         <tt>operateDate： Date:最近操作时间</tt><br>
	 */
	@Override
	public Page<Map<String, Object>> findAllCustAccountList(
			Map<String, Object> param) {
		StringBuffer whereCustSqlString = new StringBuffer();
		StringBuffer whereAccountSqlString = new StringBuffer();

		List<Object> objList = new ArrayList<Object>();

		if (!StringUtils.isEmpty(param.get("nickName"))) {
			whereCustSqlString.append(" and LOGIN_NAME LIKE ?");
			objList.add(new StringBuffer().append("%")
					.append(param.get("nickName")).append("%"));
		}

		if (!StringUtils.isEmpty(param.get("custName"))) {
			whereCustSqlString.append(" and CUST_NAME = ?");
			objList.add(param.get("custName"));
		}

		if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
			whereCustSqlString.append(" and CREDENTIALS_CODE = ?");
			objList.add(param.get("credentialsCode"));
		}

		// 过滤公司账户
		whereAccountSqlString.append(" and T.ID NOT IN (?, ?, ?, ?, ?, ?, ?, ?)");
		objList.add(Constant.ACCOUNT_ID_CENTER);
		objList.add(Constant.ACCOUNT_ID_ERAN);
		objList.add(Constant.ACCOUNT_ID_RISK);
		objList.add(Constant.ACCOUNT_ID_PROJECT_ERAN);
		objList.add(Constant.ACCOUNT_ID_PROJECT_RISK);
		objList.add(Constant.ACCOUNT_ID_REPAYMENT);
		objList.add(Constant.ACCOUNT_ID_WEALTH_CENTER);
		objList.add(Constant.ACCOUNT_ID_COMMISION);

		if (!StringUtils.isEmpty(param.get("opearteDateBegin"))) {
			whereAccountSqlString.append(" and T.LAST_UPDATE_DATE >= ?");
			objList.add(DateUtils.parseStandardDate((String) param
					.get("opearteDateBegin")));
		}

		if (!StringUtils.isEmpty(param.get("opearteDateEnd"))) {
			whereAccountSqlString.append(" and T.LAST_UPDATE_DATE <= ?");
			objList.add(DateUtils.getEndDate(DateUtils
					.parseStandardDate((String) param.get("opearteDateEnd"))));
		}
		
		StringBuffer sqlString= new StringBuffer()
		.append(" select AA.ID \"id\", AA.LOGIN_NAME \"nickName\", AA.CUST_NAME \"custName\",  ")
		.append("        AA.CREDENTIALS_CODE \"credentialsCode\", NVL(AA.accountAvailableAmount,0) \"accountAvailable\",  ")
		.append("        NVL(AA.accountFreezeAmount,0) \"accountFreezeAmount\",  ")
		.append("        AA.Last_Update_Date \"operateDate\", NVL(BB.incomeAmount,0) \"incomeAmount\" ")
		.append(" from  ")
		.append(" ( ")
		.append("   select S.ID, T.ID ACCOUNT_ID, T.CUST_ID, S.LOGIN_NAME, S.CUST_NAME, S.CREDENTIALS_CODE, ")
		.append("   TRUNC_AMOUNT_WEB(T.ACCOUNT_AVAILABLE_AMOUNT) accountAvailableAmount, TRUNC_AMOUNT_WEB(T.ACCOUNT_FREEZE_AMOUNT) accountFreezeAmount, T.Last_Update_Date ")
		.append("   from BAO_T_ACCOUNT_INFO T, BAO_T_CUST_INFO S ")
		.append("   where T.CUST_ID = S.ID  AND (S.CUST_KIND IS NULL OR S.CUST_KIND = '网站用户')  %s")
		.append(" )AA ")
		.append(" left join  ")
		.append(" ( ")
		.append(" select c.cust_id cust_id, sum(c.payment_interest) incomeAmount ")
		.append("   from (select a.cust_id cust_id, ")
		.append("                nvl(sum(trunc(b.trade_amount, 2)), 0) payment_interest ")
		.append("           from bao_t_payment_record_info a, bao_t_payment_record_detail b ")
		.append("          where a.id = b.pay_record_id ")
		.append("            and b.subject_type not in ('本金', '风险金垫付本金') and a.loan_id is null  ")
		.append("          group by a.cust_id ")
		.append("         union all ")
		.append("         select w.cust_id cust_id, ")
		.append("                nvl(sum(trunc(w.fact_payment_amount, 2) - trunc(w.except_payment_principal, 2)), 0) + nvl(max(b.atone_expenses), 0) payment_interest ")
		.append("           from bao_t_wealth_payment_plan_info w, ")
		.append("                (select nvl(sum(a.atone_expenses), 2) atone_expenses, a.cust_id cust_id from bao_t_atone_info a where a.atone_method = '提前赎回' and a.atone_status = '处理成功' group by a.cust_id) b")
		.append("          where w.payment_status = '已回收' and w.cust_id = b.cust_id (+) ")
		.append("          group by w.cust_id) c ")
		.append("  group by c.cust_id ")
		.append(" )BB ON AA.CUST_ID = BB.CUST_ID ")
		.append(" ORDER BY Aa.LAST_UPDATE_DATE DESC");
		
		StringBuffer whereSqlString = new StringBuffer();
		if (!StringUtils.isEmpty(whereCustSqlString.toString())) {
			whereSqlString.append(String.format(" %s",
					whereCustSqlString.toString()));
		}
		if (!StringUtils.isEmpty(whereAccountSqlString.toString())) {
			whereSqlString.append(String.format(" %s",
					whereAccountSqlString.toString()));
		}

		return repositoryUtil.queryForPageMap(
				String.format(sqlString.toString(), whereSqlString.toString()),
				objList.toArray(), (int) param.get("start"),
				(int) param.get("length"));
	}

	@Override
	public long findSequenceValueByName(String sequenceName) {
		StringBuffer sqlString = new StringBuffer()
				.append(String.format(
						"select %s.nextval \"sequenceNumber\" from dual",
						sequenceName));

		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				sqlString.toString(), new Object[] {});
		if (list == null || list.size() == 0)
			return 0;
		Map<String, Object> map = list.get(0);
		if (map == null || map.size() == 0)
			return 0;

		return ((BigDecimal) map.get("sequenceNumber")).longValue();
	}

	@Override
	public Map<String, Object> findAllValueByCustId(Map<String, Object> params) {
		StringBuffer sqlString = new StringBuffer()
				.append(" SELECT NVL(SUM(TRUNC_AMOUNT_WEB(T.ACCOUNT_TOTAL_VALUE)), 0) \"accountTotalValue\", NVL(SUM(TRUNC_AMOUNT_WEB(T.ACCOUNT_AVAILABLE_VALUE)), 0) \"accountAvailableValue\" ")
				.append(" FROM BAO_T_SUB_ACCOUNT_INFO T  ")
				.append(" WHERE T.CUST_ID = ? AND T.RELATE_TYPE = ?  ")
				.append(" AND T.RELATE_PRIMARY IN ( ")
				.append("       SELECT ID ")
				.append("       FROM BAO_T_INVEST_INFO S ")
				.append("       WHERE INVEST_STATUS = ? AND S.PRODUCT_ID IN ( ")
				.append("             SELECT ID ")
				.append("             FROM BAO_T_PRODUCT_INFO M ")
				.append("             WHERE M.PRODUCT_TYPE IN ( ")
				.append("                   SELECT ID ")
				.append("                   FROM BAO_T_PRODUCT_TYPE_INFO N ")
				.append("                   WHERE N.TYPE_NAME = ? ")
				.append("             ) ").append("       ) ").append(" )");

		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				sqlString.toString(),
				new Object[] { (String) params.get("custId"),
						Constant.TABLE_BAO_T_INVEST_INFO,
						Constant.VALID_STATUS_VALID,
						(String) params.get("typeName") });
		if (list == null || list.size() == 0)
			return new HashMap<String, Object>();
		Map<String, Object> map = list.get(0);
		if (map == null || map.size() == 0)
			return new HashMap<String, Object>();

		return map;
	}

	@Override
	public int updateAccountById(final Map<String, Object> paramMap)
			throws SLException {
		String sql = "UPDATE BAO_T_ACCOUNT_INFO T SET T.ACCOUNT_TOTAL_AMOUNT=T.ACCOUNT_TOTAL_AMOUNT-?,T.ACCOUNT_AVAILABLE_AMOUNT=T.ACCOUNT_AVAILABLE_AMOUNT-?,T.VERSION=T.VERSION+1 WHERE T.ID=? AND T.ACCOUNT_AVAILABLE_AMOUNT>=?";
		return jdbcTemplate.update(sql, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setBigDecimal(1,
						CommonUtils.emptyToDecimal(paramMap.get("amount")));
				ps.setBigDecimal(2,
						CommonUtils.emptyToDecimal(paramMap.get("amount")));
				ps.setString(3, CommonUtils.emptyToString(paramMap.get("id")));
				ps.setBigDecimal(4,
						CommonUtils.emptyToDecimal(paramMap.get("amount")));

			}
		});
	}

	@Override
	public List<Map<String, Object>> findAllAccountFlowSumNew(
			Map<String, Object> param) {
		StringBuffer whereCustSqlString = new StringBuffer();
		StringBuffer whereAccountSqlString = new StringBuffer();

		StringBuffer sqlString = new StringBuffer()
				.append(" select  ")
				.append("        TO_CHAR(SUM (TRUNC_AMOUNT_WEB(Q.TRADE_AMOUNT))) \"tradeAmount\"  ")
				.append("   from BAO_T_ACCOUNT_FLOW_INFO Q ")
				.append("   INNER JOIN BAO_T_CUST_INFO S ON Q.CUST_ID = S.ID ")
				// .append("   LEFT JOIN BAO_T_ACCOUNT_INFO T ON T.ID = N.TARGET_ACCOUNT ")
				// .append("   LEFT JOIN BAO_T_CUST_INFO S2 ON S2.ID = T.ID ")
				.append("   WHERE Q.ACCOUNT_TYPE = ? %s ")
				.append("   ORDER BY Q.TRADE_NO DESC ");

		List<Object> objList = new ArrayList<Object>();
		objList.add(Constant.ACCOUNT_TYPE_MAIN);
		if (!StringUtils.isEmpty(param.get("nickName"))) {
			whereCustSqlString.append(" and S.LOGIN_NAME = ?");
			objList.add(new StringBuffer().append(param.get("nickName")));
		}

		if (!StringUtils.isEmpty(param.get("custName"))) {
			whereCustSqlString.append(" and S.CUST_NAME = ?");
			objList.add(param.get("custName"));
		}

		if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
			whereCustSqlString.append(" and S.CREDENTIALS_CODE = ?");
			objList.add(param.get("credentialsCode"));
		}

		// 过滤公司账户		
		whereAccountSqlString.append(" and Q.ACCOUNT_ID NOT IN (?, ?, ?, ?, ?, ?, ?, ?)");
		objList.add(Constant.ACCOUNT_ID_CENTER);
		objList.add(Constant.ACCOUNT_ID_ERAN);
		objList.add(Constant.ACCOUNT_ID_RISK);
		objList.add(Constant.ACCOUNT_ID_PROJECT_ERAN);
		objList.add(Constant.ACCOUNT_ID_PROJECT_RISK);
		objList.add(Constant.ACCOUNT_ID_REPAYMENT);
		objList.add(Constant.ACCOUNT_ID_WEALTH_CENTER);
		objList.add(Constant.ACCOUNT_ID_COMMISION);

		if (!StringUtils.isEmpty(param.get("opearteDateBegin"))) {
			whereAccountSqlString.append(" and Q.CREATE_DATE >= ?");
			objList.add(DateUtils.parseStandardDate((String) param
					.get("opearteDateBegin")));
		}

		if (!StringUtils.isEmpty(param.get("opearteDateEnd"))) {
			whereAccountSqlString.append(" and Q.CREATE_DATE <= ?");
			objList.add(DateUtils.getEndDate(DateUtils
					.parseStandardDate((String) param.get("opearteDateEnd"))));
		}

		if (!StringUtils.isEmpty(param.get("tradeType"))) {
			whereAccountSqlString.append(" and Q.TRADE_TYPE = ?");
			objList.add(param.get("tradeType"));
		} else {
			whereAccountSqlString.append(" and Q.TRADE_TYPE NOT IN (?, ?) ");
			objList.add(SubjectConstant.TRADE_FLOW_TYPE_FREEZE);
			objList.add(SubjectConstant.TRADE_FLOW_TYPE_UNFREEZE);
		}

		StringBuffer whereSqlString = new StringBuffer();
		if (!StringUtils.isEmpty(whereCustSqlString.toString())) {
			whereSqlString.append(String.format(" %s",
					whereCustSqlString.toString()));
		}

		if (!StringUtils.isEmpty(whereAccountSqlString.toString())) {
			whereSqlString.append(String.format(" %s",
					whereAccountSqlString.toString()));
		}

		return repositoryUtil.queryForMap(
				String.format(sqlString.toString(), whereSqlString.toString()),
				objList.toArray());
	}
	
	@Override
	public List<String> findMoreSequenceValueByName(String prefix, String sequenceName, int size) {
		
		List<String> result = new ArrayList<String>();
		
		StringBuilder sqlString= new StringBuilder()
		.append("select ? || case when length(COLUMN_VALUE)< 12 then lpad(COLUMN_VALUE, 12, '0') else to_char(COLUMN_VALUE) end \"value\" from table(getSequences(?, ?))");
		
		List<Map<String, Object>> list =repositoryUtil.queryForMap(sqlString.toString(), new Object[] {prefix, sequenceName, size});
		
		for(Map<String, Object> m : list) {
			result.add(m.get("value").toString());
		}
		
		return result;
	}

	@Override
	public Map<String, Object> findAccountInfoByCustId(Map<String, Object> param) {
		Map<String, Object> result = Maps.newHashMap();
		StringBuilder sql = new StringBuilder()
		.append(" select bcust.id                      \"custId\", ")
		.append("        bcust.login_name              \"loginName\", ")
		.append("        bcust.portrait_path           \"portraitPath\", ")
		.append("        TRUNC_AMOUNT_WEB(bacc.account_freeze_amount)    \"accountFreezeAmount\", ")
		.append("        TRUNC_AMOUNT_WEB(bacc.account_available_amount) \"accountAvailableAmount\", ")
		.append(" TRUNC_AMOUNT_WEB(bacc.account_activity_amount) \"accountActivityAmount\" ")
		.append("   from bao_t_cust_info bcust, bao_t_account_info bacc ")
		.append("  where bcust.id = bacc.cust_id ");
		
		SqlCondition sqlCondition = new SqlCondition(sql, param);
		sqlCondition.addString("custId", "bcust.id");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
		if(list != null && list.size() > 0){
			result = list.get(0);
		}
		
		return result;
	}

	public static void main(String[] args) {
		List<String> excludeSubjectTypeList = Arrays.asList(
				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_LOAN,
				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PRINCIPAL_LOAN,
				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_PROJECT,
				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PRINCIPAL_PROJECT,
				SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_WEALTH
		);
		
		String excludeSubjectTypeString = Joiner.on(",").join(Iterables.transform(excludeSubjectTypeList,
			      new Function<String, String>() {
			          @Override
			          public String apply(String input) {
			              return String.format("'%s'", input);
			          }
			      }));
		
		System.out.println(excludeSubjectTypeString);
	}
}
