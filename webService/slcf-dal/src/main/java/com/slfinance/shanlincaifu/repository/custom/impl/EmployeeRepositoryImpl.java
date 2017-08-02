package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.EmployeeRepositoryCustom;
import com.slfinance.shanlincaifu.utils.SqlCondition;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

	@Autowired
	RepositoryUtil repositoryUtil; 
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	/**
	 * 业绩详情
	 * @author  liyy
	 * @date    2016年4月19日
	 * @param map
	 		<tt>start：String:起始值</tt><br>
	 		<tt>length：String:长度</tt><br>
	 		<tt>commMonth：String:业绩月份</tt><br>
	  		<tt>empName：String:姓名(可选)</tt><br>
	  		<tt>credentialsCode：String:身份证号码(可选)</tt><br>
	  		<tt>proName：String:省份(可选)</tt><br>
	  		<tt>cityName：String:城市(可选)</tt><br>
	  		<tt>dept2：String:所在团队(可选)</tt><br>
	  		<tt>dept1：String:所在门店/营业部(可选)</tt><br>
	 * @return
			<tt>iTotalDisplayRecords：String:起始值</tt><br>
			<tt>data：List:起始值</tt><br>
	 */
	public Page<Map<String, Object>> getCommissionInfo(Map<String, Object> param) {
		SqlCondition sqlCon = getCommissionSql(param, false);
		
		return repositoryUtil.queryForPageMap(sqlCon.toString(), sqlCon.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}
	/** 累计金额 */
	public List<Map<String, Object>> getAmount(Map<String, Object> param) {
		SqlCondition sqlCon = getCommissionSql(param, true);
		
		return repositoryUtil.queryForMap(sqlCon.toString(), sqlCon.toArray());
	}

	/**
	 * 业绩明细
	 * @author  liyy
	 * @date    2016年4月19日
	 * @param map
	 		<tt>custId：String:客户ID</tt><br>
	 		<tt>commMonth：String:业绩月份</tt><br>
	 		<tt>start：String:业绩月份</tt><br>
	 		<tt>length：String:业绩月份</tt><br>
	 * @return
	 */
	public Page<Map<String, Object>> getCommissionInfoDetail(
			Map<String, Object> param) {
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql(" SELECT cust.CUST_NAME  \"custName\" ")
		.addSql(" , cust.MOBILE          \"mobile\" ")
		.addSql(" , invest.CREATE_DATE   \"investDate\" ")
		.addSql(" , TRUNC(invest.INVEST_AMOUNT, 2) \"investAmount\" ")
		.addSql(" , invest.INVEST_STATUS \"investStatus\"  ")
		.addSql(" , CASE  ")
		.addSql("     WHEN commD.RELATE_TYPE='BAO_T_PROJECT_INFO' THEN project.TYPE_TERM||'' ")
		.addSql("     WHEN commD.RELATE_TYPE='BAO_T_WEALTH_INFO'  THEN wealthType.TYPE_TERM||'' ")
		.addSql("   ELSE '' END AS        \"typeTerm\" ")
		.addSql(" , CASE  ")
		.addSql("     WHEN commD.RELATE_TYPE='BAO_T_PROJECT_INFO' THEN project.PROJECT_NAME ")
		.addSql("     WHEN commD.RELATE_TYPE='BAO_T_WEALTH_INFO'  THEN wealthType.LENDING_TYPE ")
		.addSql("   ELSE '' END AS        \"lendingType\" ")
		.addSql(" , CASE  ")
		.addSql("     WHEN commD.RELATE_TYPE='BAO_T_PROJECT_INFO' THEN project.PROJECT_NO ")
		.addSql("     WHEN commD.RELATE_TYPE='BAO_T_WEALTH_INFO'  THEN wealth.LENDING_NO||'期' ")
		.addSql("   ELSE '' END AS        \"lendingNo\" ")
		.addSql(" FROM  ")
		.addSql(" (SELECT comm.COMM_MONTH, comm.CUST_ID, commDetail.* ")
		.addSql("    FROM BAO_T_COMMISSION_INFO comm, BAO_T_COMMISSION_DETAIL_INFO commDetail  ")
		.addSql("	WHERE comm.CUST_ID = '" + param.get("custId").toString() + "' ")
		.addSql("	  AND comm.COMM_MONTH = '" + param.get("commMonth").toString() + "' ")
		.addSql("     AND commDetail.COMMISSION_ID = comm.ID ")
		.addSql(" ) commD ")
		.addSql(" INNER JOIN BAO_T_EMPLOYEE_INFO emp ON emp.CUST_ID = commD.CUST_ID ") 
		.addSql(" INNER JOIN BAO_T_EMPLOYEE_LOAD_INFO l on l.import_batch_no = commD.COMM_MONTH and l.id = emp.id ")
		.addSql(" LEFT JOIN BAO_T_CUST_INFO cust ON cust.ID = commD.QUILT_CUST_ID ")
		.addSql(" LEFT JOIN BAO_T_INVEST_INFO invest ON invest.ID = commD.INVEST_ID ")
		.addSql(" LEFT JOIN BAO_T_PROJECT_INFO project ON project.ID = commD.RELATE_PRIMARY ")
		.addSql(" LEFT JOIN BAO_T_WEALTH_INFO wealth ON wealth.Id = commD.RELATE_PRIMARY ")
		.addSql(" LEFT JOIN BAO_T_WEALTH_TYPE_INFO wealthType ON wealthType.Id = wealth.WEALTH_TYPE_ID ")
		.addSql(" order by invest.CREATE_DATE desc ")
		;
		
		return repositoryUtil.queryForPageMap(sqlCon.toString(), sqlCon.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}
	
	/**
	 * 赎回详情
	 * @author  liyy
	 * @date    2016年4月19日
	 * @param map
	 		<tt>start：String:起始值</tt><br>
	 		<tt>length：String:长度</tt><br>
	 		<tt>commMonth：String:业绩月份</tt><br>
	  		<tt>empName：String:姓名(可选)</tt><br>
	  		<tt>credentialsCode：String:身份证号码(可选)</tt><br>
	  		<tt>provinceName：String:省份(可选)</tt><br>
	  		<tt>cityName：String:城市(可选)</tt><br>
	  		<tt>team3：String:所在小团队(可选)</tt><br>
	  		<tt>team2：String:所在大团队(可选)</tt><br>
	  		<tt>team1：String:所在门店/营业部(可选)</tt><br>
	 * @return 
	 */
	public Page<Map<String, Object>> getAtoneInfo(Map<String, Object> param) {
		SqlCondition sqlCon = getAtoneSql(param, false);
		
		return repositoryUtil.queryForPageMap(sqlCon.toString(), sqlCon.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}
	/** 累计金额 */
	public List<Map<String, Object>> getAtoneAmount(Map<String, Object> param) {
		SqlCondition sqlCon = getAtoneSql(param, true);
		
		return repositoryUtil.queryForMap(sqlCon.toString(), sqlCon.toArray());
	}
	
	/**
	 * 赎回明细
	 * @author  liyy
	 * @date    2016年4月21日
	 * @param map
	 		<tt>commissionId：String:提成Id</tt><br>
	 		<tt>commMonth：String:业绩月份</tt><br>
	 * @return
	 */
	public Page<Map<String, Object>> getAtoneInfoDetail(
			Map<String, Object> param) {
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql(" SELECT cust.CUST_NAME  \"custName\" ")
		.addSql(" , cust.MOBILE          \"mobile\" ")
		.addSql(" , invest.LAST_UPDATE_DATE   \"atoneDate\" ")
		.addSql(" , TRUNC(invest.INVEST_AMOUNT, 2) \"investAmount\" ")
		.addSql(" , invest.INVEST_STATUS \"investStatus\"  ")
		.addSql(" , CASE  ")
		.addSql("     WHEN commD.RELATE_TYPE='BAO_T_PROJECT_INFO' THEN project.TYPE_TERM||'' ")
		.addSql("     WHEN commD.RELATE_TYPE='BAO_T_WEALTH_INFO'  THEN wealthType.TYPE_TERM||'' ")
		.addSql("   ELSE '' END AS        \"typeTerm\" ")
		.addSql(" , CASE  ")
		.addSql("     WHEN commD.RELATE_TYPE='BAO_T_PROJECT_INFO' THEN project.PROJECT_NAME ")
		.addSql("     WHEN commD.RELATE_TYPE='BAO_T_WEALTH_INFO'  THEN wealthType.LENDING_TYPE ")
		.addSql("   ELSE '' END AS        \"lendingType\" ")
		.addSql(" , CASE  ")
		.addSql("     WHEN commD.RELATE_TYPE='BAO_T_PROJECT_INFO' THEN project.PROJECT_NO ")
		.addSql("     WHEN commD.RELATE_TYPE='BAO_T_WEALTH_INFO'  THEN wealth.LENDING_NO||'期' ")
		.addSql("   ELSE '' END AS        \"lendingNo\" ")
		.addSql(" FROM  ")
		.addSql(" (SELECT comm.COMM_MONTH, comm.CUST_ID, commDetail.* ")
		.addSql("    FROM BAO_T_COMMISSION_INFO comm, BAO_T_COMMISSION_DETAIL_INFO commDetail  ")
		.addSql("	WHERE comm.CUST_ID = '" + param.get("custId").toString() + "' ")
		.addSql("	  AND comm.COMM_MONTH = '" + param.get("commMonth").toString() + "' ")
		.addSql("     AND commDetail.COMMISSION_ID = comm.ID ")
		.addSql(" ) commD ")
		.addSql(" INNER JOIN BAO_T_EMPLOYEE_INFO emp ON emp.CUST_ID = commD.CUST_ID ") 
		.addSql(" INNER JOIN BAO_T_EMPLOYEE_LOAD_INFO l on l.import_batch_no = commD.COMM_MONTH and l.id = emp.id /* 有效数据 */ ")
		.addSql(" INNER JOIN BAO_T_CUST_INFO cust ON cust.ID = commD.QUILT_CUST_ID ")
		.addSql(" INNER JOIN BAO_T_INVEST_INFO invest ON invest.ID = commD.INVEST_ID AND invest.INVEST_STATUS IN ('到期赎回','提前赎回','无效') AND to_char(invest.LAST_UPDATE_DATE, 'yyyyMM') = '" + param.get("commMonth").toString() + "' ")
//		.addSql(" INNER JOIN BAO_T_ATONE_INFO atone ON atone.INVEST_ID = commD.INVEST_ID AND atone.ATONE_STATUS='处理成功' AND to_char(atone.CLEANUP_DATE, 'yyyyMM') = '" + param.get("commMonth").toString() + "' ")
		.addSql(" LEFT JOIN BAO_T_PROJECT_INFO project ON project.ID = commD.RELATE_PRIMARY ")
		.addSql(" LEFT JOIN BAO_T_WEALTH_INFO wealth ON wealth.Id = commD.RELATE_PRIMARY ")
		.addSql(" LEFT JOIN BAO_T_WEALTH_TYPE_INFO wealthType ON wealthType.Id = wealth.WEALTH_TYPE_ID ")
//		.addSql(" order by atone.CLEANUP_DATE desc ")
		.addSql(" order by invest.LAST_UPDATE_DATE desc ")
		;
		
		return repositoryUtil.queryForPageMap(sqlCon.toString(), sqlCon.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}
	
	/** 业绩详情sql */
	private SqlCondition getCommissionSql(Map<String, Object> param, boolean sumFlag){
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql(" WITH A AS (SELECT  ")
		.addSql(" 		 comm.CUST_ID ")
		.addSql(" 		, emp.EMP_NO ")
		.addSql(" 		, emp.EMP_NAME ")
		.addSql(" 		, emp.CREDENTIALS_CODE ")
		.addSql("		, emp.DEPT_ID ")
		.addSql("		, comm.YEAR_INVEST_AMOUNT /* 当月年化业绩 */ ")
		.addSql("		, comm.INVEST_AMOUNT /* 当月进账总额 */ ")
		.addSql("	 FROM ( ")
		.addSql("	 		SELECT COMM_MONTH, CUST_ID ")
		.addSql("				, sum(TRUNC(YEAR_INVEST_AMOUNT, 2)) YEAR_INVEST_AMOUNT /* 当月年化业绩 */ ")
		.addSql("				, sum(TRUNC(INVEST_AMOUNT, 2)) INVEST_AMOUNT /* 当月进账总额 */ ")
		.addSql("			 FROM BAO_T_COMMISSION_INFO ")
		.addSql("			WHERE COMM_MONTH = '" + param.get("commMonth").toString() + "' ")
		.addSql("			GROUP BY CUST_ID, COMM_MONTH")
		.addSql("         ) comm ")
		.addSql("	       INNER JOIN BAO_T_EMPLOYEE_INFO emp ON emp.CUST_ID = comm.CUST_ID ")
		.addSql("	       INNER JOIN BAO_T_EMPLOYEE_LOAD_INFO l on l.import_batch_no = comm.COMM_MONTH and l.id = emp.id ")
		.addSql("	WHERE comm.CUST_ID = emp.CUST_ID ")
		.addSql("	), ")
		// 取得团队经理，大团队经理，营业部经理，营业部sql
		/*
	       dept1 "所属营业部", dept2 "所属大团队", dept3 "所属小团队", dept4,
	       cust1 "营业部经理", cust2 "大团队经理", cust3 "小团队经理", cust4
		 * */
		.addSql(getDept())
		;
		// 
		if(sumFlag){
			sqlCon.addSql("	SELECT nvl(sum(\"yearInvestAmount\"),0) totalYearInvestAmount, nvl(sum(\"investAmount\"),0) totalInvestAmount FROM ( ");
		}
		sqlCon.addSql("	SELECT  ")
		.addSql("	 A.CUST_ID            \"custId\" ")
		.addSql("	, A.EMP_NO             \"empNo\" ")
		.addSql("	, A.EMP_NAME           \"empName\" ")
		.addSql("	, A.CREDENTIALS_CODE   \"credentialsCode\" ")
		.addSql("	, A.DEPT_ID            \"deptId\" ")
		.addSql("	, A.YEAR_INVEST_AMOUNT \"yearInvestAmount\" ")
		.addSql("	, A.INVEST_AMOUNT      \"investAmount\" ")
		.addSql("	, t.PRO_NAME           \"provinceName\" ")
		.addSql("	, t.CITY_NAME          \"cityName\" ")
		.addSql("	, t.cust3              \"team3Manager\" ")
		.addSql("	, t.cust2              \"team2Manager\" ")
		.addSql("	, t.cust1              \"team1Manager\" ")
		.addSql("	, t.dept1              \"team1\" ")
		.addSql("	FROM A  ")
		.addSql("	LEFT JOIN t ON A.DEPT_ID = t.id ")
		.addSql("   WHERE 1=1 ")
		.addLike("empName", "A.EMP_NAME")
		.addString("credentialsCode", "A.CREDENTIALS_CODE")
		.addString("provinceName", "t.PRO_NAME")
		.addString("cityName", "t.CITY_NAME")
		.addString("team1", "t.dept1") // 所在门店/营业部
		;
		// 所在团队
		if(!StringUtils.isEmpty(param.get("team2"))){
			sqlCon.addSql(" and (t.dept2 = ? or t.dept3 = ? )"); 
			sqlCon.getObjectList().add(param.get("team2").toString());
			sqlCon.getObjectList().add(param.get("team2").toString());
		}
		sqlCon.addSql(" order by A.EMP_NO ");
		if(sumFlag){
			sqlCon.addSql("	) ");
		}
		;
		return sqlCon;
	}
	
	/** 赎回详情sql */
	private SqlCondition getAtoneSql(Map<String, Object> param, boolean sumFlag){
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql(" WITH A AS (SELECT comm.COMM_MONTH, comm.ID, comm.CUST_ID, commDetail.QUILT_CUST_ID  ")
		.addSql(" 		 , commDetail.INVEST_ID, commDetail.RELATE_TYPE, commDetail.RELATE_PRIMARY ")
		.addSql("	 FROM BAO_T_COMMISSION_INFO comm, BAO_T_COMMISSION_DETAIL_INFO commDetail ")
		.addSql("	WHERE comm.COMM_MONTH = '" + param.get("commMonth").toString() + "' ")
		.addSql("	  AND commDetail.COMMISSION_ID = comm.ID ")
		.addSql("	), ")
		// 取得团队经理，大团队经理，营业部经理，营业部sql
		/*
	       dept1 "所属营业部", dept2 "所属大团队", dept3 "所属小团队", dept4,
	       cust1 "营业部经理", cust2 "大团队经理", cust3 "小团队经理", cust4
		 * */
		.addSql(getDept())
		;
		// 
		if(sumFlag){
			sqlCon.addSql("	SELECT nvl(sum(\"yearInvestAmount\"),0) totalYearInvestAmount, nvl(sum(\"investAmount\"),0) totalInvestAmount FROM ( ");
		}
		sqlCon.addSql("	SELECT  ")
		.addSql("	 Y.CUST_ID            \"custId\" ")
		.addSql("	, Y.INVEST_AMOUNT      \"investAmount\" ")
		.addSql("	, Y.YEAR_INVEST_AMOUNT \"yearInvestAmount\" ")
		.addSql("	, emp.EMP_NO           \"empNo\" ")
		.addSql("	, emp.EMP_NAME         \"empName\" ")
		.addSql("	, emp.CREDENTIALS_CODE \"credentialsCode\" ")
		.addSql("	, emp.DEPT_ID          \"deptId\" ")
		.addSql("	, t.PRO_NAME           \"provinceName\" ")
		.addSql("	, t.CITY_NAME          \"cityName\" ")
		.addSql("	, t.cust3              \"team3Manager\" ")
		.addSql("	, t.cust2              \"team2Manager\" ")
		.addSql("	, t.cust1              \"team1Manager\" ")
		.addSql("	, t.dept1              \"team1\" ")
		.addSql("  FROM(SELECT COMM_MONTH, CUST_ID, sum(INVEST_AMOUNT) INVEST_AMOUNT, sum(YEAR_INVEST_AMOUNT) YEAR_INVEST_AMOUNT ")
		.addSql("	FROM(SELECT A.COMM_MONTH, A.CUST_ID ")
		.addSql("			, TRUNC(invest.INVEST_AMOUNT, 2) INVEST_AMOUNT ")
		.addSql("			, CASE   ")
		.addSql("				WHEN A.RELATE_TYPE='BAO_T_PROJECT_INFO' THEN project.TYPE_TERM ")
		.addSql("				WHEN A.RELATE_TYPE='BAO_T_WEALTH_INFO'  THEN wealthType.TYPE_TERM ")
		.addSql("			END AS TYPE_TERM ")
		.addSql("			, TRUNC(TRUNC(invest.INVEST_AMOUNT, 2)*( ")
		.addSql("				CASE   ")
		.addSql("					WHEN A.RELATE_TYPE='BAO_T_PROJECT_INFO' THEN project.TYPE_TERM ")
		.addSql("					WHEN A.RELATE_TYPE='BAO_T_WEALTH_INFO'  THEN wealthType.TYPE_TERM ")
		.addSql("				END ")
		.addSql("			)/12, 2) AS YEAR_INVEST_AMOUNT ")
		.addSql("		 FROM A ")
		.addSql("		INNER JOIN BAO_T_INVEST_INFO invest ON invest.ID = A.INVEST_ID AND invest.INVEST_STATUS IN ('到期赎回','提前赎回','无效') AND to_char(invest.LAST_UPDATE_DATE, 'yyyyMM') = '" + param.get("commMonth").toString() + "' ")
//		.addSql("		INNER JOIN BAO_T_ATONE_INFO atone ON atone.INVEST_ID = A.INVEST_ID AND atone.ATONE_STATUS='处理成功' AND to_char(atone.CLEANUP_DATE, 'yyyyMM') = '" + param.get("commMonth").toString() + "'  ")
		.addSql("		LEFT JOIN BAO_T_PROJECT_INFO project ON project.ID = A.RELATE_PRIMARY ")
		.addSql("		LEFT JOIN BAO_T_WEALTH_INFO wealth ON wealth.Id = A.RELATE_PRIMARY ")
		.addSql("		LEFT JOIN BAO_T_WEALTH_TYPE_INFO wealthType ON wealthType.Id = wealth.WEALTH_TYPE_ID ")
		.addSql("	) X GROUP BY CUST_ID, COMM_MONTH ")
		.addSql(" ) Y  ")
		.addSql(" INNER JOIN BAO_T_EMPLOYEE_INFO emp ON emp.CUST_ID = Y.CUST_ID ") 
		.addSql(" INNER JOIN BAO_T_EMPLOYEE_LOAD_INFO l on l.import_batch_no = Y.COMM_MONTH and l.id = emp.id ")
		.addSql(" LEFT JOIN t ON t.id = emp.DEPT_ID ")
		.addSql(" WHERE 1=1 ")
		.addLike("empName", "emp.EMP_NAME")
		.addString("credentialsCode", "emp.CREDENTIALS_CODE")
		.addString("provinceName", "t.PRO_NAME")
		.addString("cityName", "t.CITY_NAME")
		.addString("team1", "t.dept1") // 所在门店/营业部
		;
		// 所在团队
		if(!StringUtils.isEmpty(param.get("team2"))){
			sqlCon.addSql(" and (t.dept2 = ? or t.dept3 = ? )"); 
			sqlCon.getObjectList().add(param.get("team2").toString());
			sqlCon.getObjectList().add(param.get("team2").toString());
		}
		sqlCon.addSql(" order by emp.EMP_NO ");
		if(sumFlag){
			sqlCon.addSql("	) ");
		}
		;
		return sqlCon;
	}

	/** 部门-团队经理等 */
	private String getDept(){
		StringBuffer buf = new StringBuffer()
		.append(" t1 as (                                                               ")
		.append("      select m.id, m.dept_type, m.pro_name, m.city_name,               ")
		.append("             m.dept_name dept1, '' dept2, '' dept3, '' dept4,          ")
		.append("             n.emp_name  cust1, '' cust2, '' cust3, '' cust4,          ")
		.append("             n.credentials_code code1, '' code2, '' code3, '' code4    ")
		.append("      from bao_t_dept_info m                                           ")
		.append("      left join bao_t_employee_info n on m.dept_manager_id = n.id      ")
		.append("      where m.dept_type = '01'                                         ")
		.append(" ),                                                                    ")
		.append(" t2 as (                                                               ")
		.append("    select m.id, m.dept_type, m.pro_name, m.city_name,                 ")
		.append("             t1.dept1, m.dept_name dept2, '' dept3, '' dept4,          ")
		.append("             t1.cust1, n.emp_name cust2, '' cust3, '' cust4,           ")
		.append("             t1.code1, n.credentials_code code2, '' code3, '' code4    ")
		.append("    from bao_t_dept_info m                                             ")
		.append("    left join bao_t_employee_info n on m.dept_manager_id = n.id        ")
		.append("    left join t1 on m.parent_id = t1.id                                ")
		.append("    where m.dept_type = '02'                                           ")
		.append(" ),                                                                    ")
		.append(" t3 as (                                                               ")
		.append("    select m.id, m.dept_type, m.pro_name, m.city_name,                 ")
		.append("             t2.dept1,  t2.dept2, m.dept_name dept3, '' dept4,         ")
		.append("             t2.cust1,  t2.cust2, n.emp_name cust3, '' cust4,          ")
		.append("             t2.code1,  t2.code2, n.credentials_code code3, '' code4   ")
		.append("    from bao_t_dept_info m                                             ")
		.append("    left join bao_t_employee_info n on m.dept_manager_id = n.id        ")
		.append("    left join t2 on m.parent_id = t2.id                                ")
		.append("    where m.dept_type = '03'                                           ")
		.append(" ),                                                                    ")
		.append(" t4 as (                                                               ")
		.append("    select m.id, m.dept_type, m.pro_name, m.city_name,                 ")
		.append("             t1.dept1, '' dept2, m.dept_name dept3, '' dept4,          ")
		.append("             t1.cust1, '' cust2, n.emp_name cust3, '' cust4,           ")
		.append("             t1.code1, '' code2, n.credentials_code code3, '' code4    ")
		.append("    from bao_t_dept_info m                                             ")
		.append("    left join bao_t_employee_info n on m.dept_manager_id = n.id        ")
		.append("    left join t1 on m.parent_id = t1.id                                ")
		.append("    where m.dept_type = '04'                                           ")
		.append(" ),                                                                    ")
		.append(" t as (                                                                ")
		.append("   select * from t1                                                    ")
		.append("   union all                                                           ")
		.append("   select * from t2                                                    ")
		.append("   union all                                                           ")
		.append("   select * from t3                                                    ")
		.append("   union all                                                           ")
		.append("   select * from t4                                                    ")
		.append(" )                                                                     ")
		;
		return buf.toString();
	}
	
	@Override
	public Page<Map<String, Object>> queryEmployeeMonthAchievement(Map<String, Object> params) throws SLException {
		StringBuffer SqlString = new StringBuffer()
				.append(" select min(t.commDate)          			\"commDate\",  ")
				.append("        sum(t.incomeAmount)      \"incomeAmount\", ")
				.append("        sum(t.yearInvestAmount)  \"yearInvestAmount\", ")
				.append("        t.commMonth              \"commMonth\" ")
				.append(" from ( ")
				.append("  select c.comm_month 			\"commMonth\", ")
				.append("        c.comm_date            commDate, ")
				.append("        trunc(c.year_invest_amount, 2)   yearInvestAmount, ")
				.append("        trunc(c.invest_amount, 2)         incomeAmount, ")
				.append("        c.comm_month    		commMonth             ")
				.append("   from bao_t_commission_info c ")
				.append("    inner join bao_t_employee_info e on e.cust_id = c.cust_id ")
				.append("	 inner join bao_t_employee_load_info l on l.import_batch_no = c.comm_month and l.id = e.id ")
				.append("  where c.record_status = '有效' ")
				.append(" ) t  where 1 = 1 %s ")
				.append(" group by t.commMonth ");
		
		StringBuilder whereSqlString= new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(whereSqlString, params);
		sqlCondition.addString("commMonth", "t.commMonth");
		
		return repositoryUtil.queryForPageMap(String.format(SqlString.toString(), sqlCondition.toString()), sqlCondition.toArray(), 
				Integer.valueOf(params.get("start").toString()), Integer.valueOf(params.get("length").toString()));
	}
	
	/* 
	 * 参考sql
with 
t1 as (
     select m.id, m.dept_type, m.pro_name, m.city_name,
            m.dept_name dept1, '' dept2, '' dept3, '' dept4,
            n.emp_name  cust1, '' cust2, '' cust3, '' cust4,
            n.credentials_code code1, '' code2, '' code3, '' code4
     from bao_t_dept_info m 
     left join bao_t_employee_info n on m.dept_manager_id = n.id
     where m.dept_type = '01'
),
t2 as (
   select m.id, m.dept_type, m.pro_name, m.city_name,
            t1.dept1, m.dept_name dept2, '' dept3, '' dept4,
            t1.cust1, n.emp_name cust2, '' cust3, '' cust4,
            t1.code1, n.credentials_code code2, '' code3, '' code4
   from bao_t_dept_info m 
   left join bao_t_employee_info n on m.dept_manager_id = n.id
   left join t1 on m.parent_id = t1.id
   where m.dept_type = '02'
),
t3 as (
   select m.id, m.dept_type, m.pro_name, m.city_name,
            t2.dept1,  t2.dept2, m.dept_name dept3, '' dept4,
            t2.cust1,  t2.cust2, n.emp_name cust3, '' cust4,
            t2.code1,  t2.code2, n.credentials_code code3, '' code4
   from bao_t_dept_info m 
   left join bao_t_employee_info n on m.dept_manager_id = n.id
   left join t2 on m.parent_id = t2.id
   where m.dept_type = '03'
),
t4 as (
   select m.id, m.dept_type, m.pro_name, m.city_name,
            t1.dept1, '' dept2, m.dept_name dept3, '' dept4,
            t1.cust1, '' cust2, n.emp_name cust3, '' cust4,
            t1.code1, '' code2, n.credentials_code code3, '' code4
   from bao_t_dept_info m 
   left join bao_t_employee_info n on m.dept_manager_id = n.id
   left join t1 on m.parent_id = t1.id
   where m.dept_type = '04'
),
t as (
  select * from t1
  union all
  select * from t2
  union all
  select * from t3
  union all
  select * from t4
)
select 
       s.id, s.emp_no, s.emp_name,
       decode(t.dept_type, 
       '01', case when t.code1 = s.credentials_code then '' else t.cust1 end, 
       '02', case when t.code2 = s.credentials_code then t.cust1 else t.cust2 end, 
       '03', case when t.code3 = s.credentials_code then t.cust2 else t.cust3 end, 
       '04', case when t.code3 = s.credentials_code then t.cust1 else t.cust3 end) "直属领导",
       dept1 "所属营业部", dept2 "所属大团队", dept3 "所属小团队", dept4,
       cust1 "营业部经理", cust2 "大团队经理", cust3 "小团队经理", cust4
from bao_t_employee_info s 
left join t on s.dept_id = t.id
order by s.emp_no;

	 */

}
