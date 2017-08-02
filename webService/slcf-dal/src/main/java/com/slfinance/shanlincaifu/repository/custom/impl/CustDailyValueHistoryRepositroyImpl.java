/** 
 * @(#)CustDailyValueHistoryRepositroyImpl.java 1.0.0 2015年5月23日 下午1:48:03  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.CustDailyValueHistoryRepositroyCustom;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;

/**   
 * 用户每日持有份额数据实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月23日 下午1:48:03 $ 
 */
@Repository
public class CustDailyValueHistoryRepositroyImpl implements
		CustDailyValueHistoryRepositroyCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public Page<Map<String, Object>> findDailyValueList(
			Map<String, Object> param) {
	
		StringBuffer sqlString= new StringBuffer()
		.append(" SELECT ID \"dailyValueId\", RECORD_DATE \"date\", PRODUCT_NAME \"productName\", TRUNC_AMOUNT_WEB(TOTAL_VALUE) \"holdValue\" ")
		.append(" FROM BAO_T_CUST_DAILY_VALUE_HISTORY ")
		.append(" WHERE 1= 1 ");
				
		List<Object> objList=new ArrayList<Object>();
		if(!StringUtils.isEmpty(param.get("productName"))){
			sqlString.append(" and PRODUCT_NAME = ?");
			objList.add(param.get("productName"));
		}
		
		if(!StringUtils.isEmpty(param.get("custId"))){
			sqlString.append(" and CUST_ID = ?");
			objList.add(param.get("custId"));
		}
		
		if(!StringUtils.isEmpty(param.get("dateBegin"))){
			sqlString.append(" and RECORD_DATE >= ?");
			objList.add(DateUtils.formatDate(DateUtils.parseStandardDate((String)param.get("dateBegin")), "yyyyMMdd"));
		}

		if(!StringUtils.isEmpty(param.get("dateEnd"))){
			sqlString.append(" and RECORD_DATE <= ?");
			objList.add(DateUtils.formatDate(DateUtils.getEndDate(DateUtils.parseStandardDate((String)param.get("dateEnd"))), "yyyyMMdd"));
		}
		
		sqlString.append(" ORDER BY RECORD_DATE DESC");
	
		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), (int)param.get("start"), (int)param.get("length"));
	}

	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Override
	public void saveDailyValueList(Date now, String productName)
			throws SLException {
		
		// 所有债权当前价值总和
		BigDecimal allLoanValue = new BigDecimal("0");
		BigDecimal allLoanValueWithOut = new BigDecimal("0");
		
		// 计算当前债权所有价值
		StringBuffer sumValueSqlString= new StringBuffer()
		.append(" SELECT NVL(SUM(D.VALUE_REPAYMENT_AFTER), 0) \"totalPv\" ")
		.append(" FROM BAO_T_LOAN_INFO A  ")
		.append(" INNER JOIN BAO_T_LOAN_DETAIL_INFO B ON B.LOAN_ID = A.ID ")
		.append(" INNER JOIN BAO_T_CREDIT_RIGHT_VALUE D ON A.ID = D.LOAN_ID AND D.VALUE_DATE = TO_CHAR(?, 'YYYYMMDD') ")
		.append(" WHERE B.CREDIT_RIGHT_STATUS = '正常' AND A.ID IN ( ")
		.append("       SELECT T.LOAN_ID ")
		.append("       FROM BAO_T_ALLOT_DETAIL_INFO T ")
		.append("       WHERE T.ALLOT_ID IN  ")
		.append("       ( ")
		.append("             SELECT S.ID FROM BAO_T_ALLOT_INFO S ")
		.append("             WHERE S.ALLOT_STATUS != '已撤销' ")
		.append("                  AND S.RELATE_PRIMARY IN ( ")
		.append("                      SELECT ID ")
		.append("                      FROM BAO_T_PRODUCT_TYPE_INFO M ")
		.append("                      WHERE M.TYPE_NAME = ? ")
		.append("                  ) ")
		.append("       ) ")
		.append(" )");		
		
		List<Map<String, Object>> sumValueList = repositoryUtil.queryForMap(sumValueSqlString.toString(), new Object[]{now, productName});
		if(sumValueList == null || sumValueList.size() == 0) {
			throw new SLException("未查询到分配的债权价值");
		}
		allLoanValue = ArithUtil.add(allLoanValue, new BigDecimal(sumValueList.get(0).get("totalPv").toString()));
		allLoanValueWithOut = ArithUtil.add(allLoanValueWithOut, new BigDecimal(sumValueList.get(0).get("totalPv").toString()));
		
		// 计算未处理的还款
		StringBuffer sumUnHandleSqlString= new StringBuffer()
		.append(" SELECT NVL(SUM(A.REPAY_AMOUNT - ALREADY_REPAY_AMT), 0) \"totalPv\" ")
		.append(" FROM BAO_T_REPAYMENT_RECORD_INFO A  ")
		.append(" WHERE A.HANDLE_STATUS = '未处理' AND A.LOAN_ID IN ( ")
		.append("       SELECT T.LOAN_ID ")
		.append("       FROM BAO_T_ALLOT_DETAIL_INFO T ")
		.append("       WHERE T.ALLOT_ID IN  ")
		.append("       ( ")
		.append("             SELECT S.ID FROM BAO_T_ALLOT_INFO S ")
		.append("             WHERE S.ALLOT_STATUS != '已撤销' ")
		.append("                  AND S.RELATE_PRIMARY IN ( ")
		.append("                      SELECT ID ")
		.append("                      FROM BAO_T_PRODUCT_TYPE_INFO M ")
		.append("                      WHERE M.TYPE_NAME = ? ")
		.append("                  ) ")
		.append("       ) ")
		.append(" ) ");
		List<Map<String, Object>> sumUnHandleList = repositoryUtil.queryForMap(sumUnHandleSqlString.toString(), new Object[]{productName});
		if(sumUnHandleList == null || sumUnHandleList.size() == 0) {
			throw new SLException("未查询到分配的债权价值");
		}
		allLoanValue = ArithUtil.add(allLoanValue, new BigDecimal(sumUnHandleList.get(0).get("totalPv").toString()));
		
		if(Constant.PRODUCT_TYPE_01.equals(productName)) { // 活期宝
			// 生成当天用户价值情况汇总
			StringBuffer sqlString= new StringBuffer()
			.append("   INSERT INTO BAO_T_CUST_DAILY_VALUE_HISTORY ")
			.append("     (ID, CUST_ID, PRODUCT_NAME, " )
			.append("      TOTAL_VALUE, FREEZE_VALUE, AVAILABLE_VALUE, " )
			.append("      HOLD_SCALE, RECORD_DATE, CREATE_DATE, " )
			.append("      VERSION, REAL_HOLD_SCALE, IS_SHOW) ")
			.append("   SELECT ")
			.append("     sys_guid(), T.CUST_ID, ?, ") 
			.append("     NVL(SUM(T.ACCOUNT_TOTAL_VALUE), 0), NVL(SUM(T.ACCOUNT_FREEZE_VALUE), 0), NVL(SUM(T.ACCOUNT_AVAILABLE_VALUE), 0), ")
			.append("     TRUNC(NVL(SUM(T.ACCOUNT_TOTAL_VALUE), 0)/?, 18), TO_CHAR(?, 'YYYYMMDD'), ?, ")
			.append("     0, TRUNC(NVL(SUM(T.ACCOUNT_TOTAL_VALUE), 0)/?, 18), '是' ")
			.append("   FROM BAO_T_SUB_ACCOUNT_INFO T ")
			.append("   WHERE T.RELATE_PRIMARY IN ( ")
			.append("         SELECT ID ")
			.append("         FROM BAO_T_INVEST_INFO S ")
			.append("         WHERE S.INVEST_STATUS = '有效' AND S.PRODUCT_ID IN ( ")
			.append("               SELECT ID ")
			.append("               FROM BAO_T_PRODUCT_INFO M ")
			.append("               WHERE M.PRODUCT_TYPE IN ( ")
			.append("                     SELECT ID ")
			.append("                      FROM BAO_T_PRODUCT_TYPE_INFO N ")
			.append("                      WHERE N.TYPE_NAME = ? ")
			.append("               ) ")
			.append("         ) ")
			.append("   ) AND NOT EXISTS ( ")
			.append("   					SELECT 1 FROM  BAO_T_CUST_DAILY_VALUE_HISTORY ")
			.append("   					WHERE  PRODUCT_NAME = ? AND RECORD_DATE = TO_CHAR(?, 'YYYYMMDD') ) ")
			.append("   GROUP BY T.CUST_ID ");
			
			jdbcTemplate.update(sqlString.toString(), 
					new Object[]{	productName,
									allLoanValue,
									now,									
									new Date(),
									allLoanValueWithOut,
									productName,
									productName,
									now});	
			
			// 生成当天公司价值情况汇总
			StringBuffer sqlStringCompany= new StringBuffer()
			.append("   INSERT INTO BAO_T_CUST_DAILY_VALUE_HISTORY ")
			.append("     (ID, CUST_ID, PRODUCT_NAME, ") 
			.append("      TOTAL_VALUE, FREEZE_VALUE, AVAILABLE_VALUE, ")
			.append("      HOLD_SCALE, RECORD_DATE, CREATE_DATE, ")
			.append("      VERSION, REAL_HOLD_SCALE, IS_SHOW) ")
			.append("   SELECT ")
			.append("     sys_guid(), T.CUST_ID, ?, ")
			.append("     NVL(SUM(T.ACCOUNT_TOTAL_VALUE), 0), NVL(SUM(T.ACCOUNT_FREEZE_VALUE), 0), NVL(SUM(T.ACCOUNT_AVAILABLE_VALUE), 0), ")
			.append("     TRUNC(NVL(SUM(T.ACCOUNT_TOTAL_VALUE), 0)/?, 18), TO_CHAR(?, 'YYYYMMDD'), ?, ")
			.append("     0, TRUNC(NVL(SUM(T.ACCOUNT_TOTAL_VALUE), 0)/?, 18), '是' ")
			.append("   FROM BAO_T_SUB_ACCOUNT_INFO T ")
			.append("   WHERE T.ID IN ( ")
			.append("     ?, ? ")
			.append("   ) AND NOT EXISTS ( ")
			.append("   					SELECT 1 FROM  BAO_T_CUST_DAILY_VALUE_HISTORY S ")
			.append("   					WHERE  PRODUCT_NAME = ? AND RECORD_DATE = TO_CHAR(?, 'YYYYMMDD') AND S.CUST_ID = T.CUST_ID) ")
			.append("   GROUP BY T.CUST_ID ");
			
			jdbcTemplate.update(sqlStringCompany.toString(), 
					new Object[]{	productName,
									allLoanValue,
									now,
									new Date(),
									allLoanValueWithOut,
									Constant.SUB_ACCOUNT_ID_CENTER,
									Constant.SUB_ACCOUNT_ID_ERAN,
									productName,
									now});	
		}
		else { // 定期宝
			// 生成当天用户价值情况汇总
			StringBuffer sqlString= new StringBuffer()
			.append("   INSERT INTO BAO_T_CUST_DAILY_VALUE_HISTORY ")
			.append("     (ID, CUST_ID, PRODUCT_NAME, " )
			.append("      TOTAL_VALUE, FREEZE_VALUE, AVAILABLE_VALUE, " )
			.append("      HOLD_SCALE, RECORD_DATE, CREATE_DATE, " )
			.append("      VERSION, SUB_ACCOUNT_ID, REAL_HOLD_SCALE, ")
			.append("      IS_SHOW) ")
			.append("   SELECT ")
			.append("     sys_guid() ID, T.CUST_ID, ? PRODUCT_NAME, ") 
			.append("     NVL(SUM(T.ACCOUNT_TOTAL_VALUE), 0) TOTAL_VALUE, NVL(SUM(T.ACCOUNT_FREEZE_VALUE), 0) FREEZE_VALUE, NVL(SUM(T.ACCOUNT_AVAILABLE_VALUE), 0) AVAILABLE_VALUE, ")
			.append("     TRUNC(NVL(SUM(T.ACCOUNT_TOTAL_VALUE), 0)/?, 18) HOLD_SCALE, TO_CHAR(?, 'YYYYMMDD') RECORD_DATE, ? CREATE_DATE, ")
			.append("     0 VERSION, T.ID SUB_ACCOUNT_ID, TRUNC(NVL(SUM(T.ACCOUNT_TOTAL_VALUE), 0)/?, 18) REAL_HOLD_SCALE,  ")
			.append("     case INVEST_STATUS when ? then '是' else '否' end IS_SHOW ")
			.append("   FROM BAO_T_SUB_ACCOUNT_INFO T, BAO_T_INVEST_INFO S ")
			.append("   WHERE T.RELATE_PRIMARY = S.ID  ")
			.append("         AND S.INVEST_STATUS NOT IN (?, ?) ")
			.append("         AND S.INVEST_DATE < ? ")
			.append("         AND S.PRODUCT_ID IN ( ")
			.append("               SELECT ID ")
			.append("               FROM BAO_T_PRODUCT_INFO M ")
			.append("               WHERE M.PRODUCT_TYPE IN ( ")
			.append("                     SELECT ID ")
			.append("                      FROM BAO_T_PRODUCT_TYPE_INFO N ")
			.append("                      WHERE N.TYPE_NAME = ? ")
			.append("               ) ")
			.append("         ) ")
			.append("   AND NOT EXISTS ( ")
			.append("   					SELECT 1 FROM  BAO_T_CUST_DAILY_VALUE_HISTORY ")
			.append("   					WHERE  PRODUCT_NAME = ? AND RECORD_DATE = TO_CHAR(?, 'YYYYMMDD') ) ")
			.append("   GROUP BY T.CUST_ID, T.ID, INVEST_STATUS ")
			.append("   UNION ALL ")
			.append("   SELECT SYS_GUID() ID, T.CUST_ID,  ? PRODUCT_NAME, ")
			.append("        T.TRADE_AMOUNT TOTAL_VALUE, 0 FREEZE_VALUE, T.TRADE_AMOUNT AVAILABLE_VALUE, ")
			.append("        TRUNC(NVL(T.TRADE_AMOUNT, 0)/?, 18) HOLD_SCALE, TO_CHAR(?, 'YYYYMMDD') RECORD_DATE, ? CREATE_DATE, ")
			.append("        0 VERSION, T.ACCOUNT_ID SUB_ACCOUNT_ID, TRUNC(NVL(T.TRADE_AMOUNT, 0)/?, 18) REAL_HOLD_SCALE, '是' IS_SHOW ")
			.append(" FROM BAO_T_ACCOUNT_FLOW_INFO T  ")
			.append(" WHERE T.TRADE_TYPE = ? and t.trade_date = trunc(?) ")
			;
			
			jdbcTemplate.update(sqlString.toString(), 
					new Object[]{	productName,
									allLoanValue,
									now,
									new Date(),
									allLoanValueWithOut,
									Constant.TERM_INVEST_STATUS_EARN,
									Constant.TERM_INVEST_STATUS_ADVANCE_FINISH,
									Constant.TERM_INVEST_STATUS_FINISH,
									DateUtils.formatDate(new Date(), "yyyyMMdd"),
									productName,
									productName,									
									now,
									productName,
									allLoanValue,
									now,
									new Date(),
									allLoanValueWithOut,
									SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM,
									now});	
			// 生成当天公司价值情况汇总
			StringBuffer sqlStringCompany= new StringBuffer()
			.append("   INSERT INTO BAO_T_CUST_DAILY_VALUE_HISTORY ")
			.append("     (ID, CUST_ID, PRODUCT_NAME, ") 
			.append("      TOTAL_VALUE, FREEZE_VALUE, AVAILABLE_VALUE, ")
			.append("      HOLD_SCALE, RECORD_DATE, CREATE_DATE, ")
			.append("      VERSION, SUB_ACCOUNT_ID, REAL_HOLD_SCALE, IS_SHOW) ")
			.append("   SELECT ")
			.append("     sys_guid(), T.CUST_ID, ?, ")
			.append("     NVL(SUM(T.ACCOUNT_TOTAL_VALUE), 0), NVL(SUM(T.ACCOUNT_FREEZE_VALUE), 0), NVL(SUM(T.ACCOUNT_AVAILABLE_VALUE), 0), ")
			.append("     TRUNC(NVL(SUM(T.ACCOUNT_TOTAL_VALUE), 0)/?, 18), TO_CHAR(?, 'YYYYMMDD'), ?, ")
			.append("     0, T.ID, TRUNC(NVL(SUM(T.ACCOUNT_TOTAL_VALUE), 0)/?, 18), '是' ")
			.append("   FROM BAO_T_SUB_ACCOUNT_INFO T ")
			.append("   WHERE T.ID IN ( ")
			.append("     ?, ? ")
			.append("   ) AND NOT EXISTS ( ")
			.append("   					SELECT 1 FROM  BAO_T_CUST_DAILY_VALUE_HISTORY S ")
			.append("   					WHERE  PRODUCT_NAME = ? AND RECORD_DATE = TO_CHAR(?, 'YYYYMMDD') AND S.CUST_ID = T.CUST_ID) ")
			.append("   GROUP BY T.CUST_ID, T.ID ");
			
			jdbcTemplate.update(sqlStringCompany.toString(), 
					new Object[]{	productName,
									allLoanValue,
									now,
									new Date(),
									allLoanValueWithOut,
									Constant.SUB_ACCOUNT_ID_CENTER_11,
									Constant.SUB_ACCOUNT_ID_ERAN_12,
									productName,
									now});		
		}
	}

	@Override
	public Page<Map<String, Object>> findDailyLoan(Map<String, Object> param) {
		
		StringBuffer sqlString= new StringBuffer()
		.append(" with t1 as( ")
		.append(" select 'today_product_1' id, m.cust_id, '' sub_account_id, ? product_name, NVL(SUM(m.ACCOUNT_TOTAL_VALUE), 0) total_value, NVL(SUM(m.ACCOUNT_TOTAL_VALUE), 0) / ? hold_scale, to_char(?, 'yyyymmdd') record_date, '买入' direc ")
		.append(" from BAO_T_SUB_ACCOUNT_INFO m ")
		.append(" WHERE m.RELATE_PRIMARY IN (  ")
		.append("     SELECT ID  ")
		.append("     FROM BAO_T_INVEST_INFO S  ")
		.append("     WHERE S.INVEST_STATUS = ?  ")
		.append("     AND S.PRODUCT_ID IN (  ")
		.append("          SELECT ID  ")
		.append("          FROM BAO_T_PRODUCT_INFO M  ")
		.append("          WHERE M.PRODUCT_TYPE IN (  ")
		.append("                SELECT ID  ")
		.append("                 FROM BAO_T_PRODUCT_TYPE_INFO N  ")
		.append("                 WHERE N.TYPE_NAME = ?  ")
		.append("          )  ")
		.append("       )  ")
		.append("     ) ")
		.append("     group by m.cust_id ")
		.append(" ), ")
		.append(" t2 as( ")
		.append("      select 'today_product_4' id, m.cust_id, m.id sub_account_id, ? product_name, NVL(SUM(m.ACCOUNT_TOTAL_VALUE), 0) total_value, NVL(SUM(m.ACCOUNT_TOTAL_VALUE), 0) / ? hold_scale, to_char(?, 'yyyymmdd') record_date, '买入' direc ")
		.append("      FROM BAO_T_SUB_ACCOUNT_INFO m        ")
		.append("       WHERE m.RELATE_PRIMARY IN (  ")
		.append(" 			         SELECT ID  ")
		.append(" 			         FROM BAO_T_INVEST_INFO S  ")
		.append(" 			         WHERE S.INVEST_STATUS = ?  ")
		.append(" 			         AND S.PRODUCT_ID IN (  ")
		.append(" 			               SELECT ID  ")
		.append(" 			               FROM BAO_T_PRODUCT_INFO M  ")
		.append(" 			               WHERE M.PRODUCT_TYPE IN (  ")
		.append(" 			                     SELECT ID  ")
		.append(" 			                      FROM BAO_T_PRODUCT_TYPE_INFO N  ")
		.append(" 			                      WHERE N.TYPE_NAME = ?   ")
		.append(" 			               )  ")
		.append(" 			         )  ")
		.append(" 			   ) ")
		.append("      GROUP BY m.CUST_ID, m.ID ")
		.append(" ) ")
		.append(" select tt1.id  \"dailyValueId\", tt1.cust_id \"custId\", tt1.sub_account_id \"subAccountId\",  ")
		.append("        tt1.total_value \"holdValue\", tt1.hold_scale \"holdScale\", tt1.recordDate \"date\", tt1.direc \"direction\",  ")
		.append("        case tt1.product_name when '定期宝' then '' || tt3.product_name || '' || tt2.curr_term || '期'  else tt1.product_name end \"productName\" ")
		.append(" from  ")
		.append(" ( ")
		.append(" select s.id, s.cust_id, s.sub_account_id, s.product_name, s.total_value, s.hold_scale, to_char(to_date(s.record_date, 'yyyyMMdd') + 1, 'yyyyMMdd') recordDate, '卖出' direc  ")
		.append(" from bao_t_cust_daily_value_history s where s.is_show is null or s.is_show = '是' ")
		.append(" union ")
		.append(" select s.id, s.cust_id, s.sub_account_id, s.product_name, s.total_value, s.hold_scale, s.record_date recordDate, '买入' direc ")
		.append("  from bao_t_cust_daily_value_history s where s.is_show is null or s.is_show = '是' ")
		.append(" union ")
		.append(" select s.id, s.cust_id, s.sub_account_id, s.product_name, s.total_value, s.hold_scale, s.record_date recordDate, '买入' direc ")
		.append("  from t1 s ")
		.append(" union ")
		.append(" select s.id, s.cust_id, s.sub_account_id, s.product_name, s.total_value, s.hold_scale, s.record_date recordDate, '买入' direc ")
		.append("  from t2 s ")
		.append(" )tt1  ")
		.append(" LEFT JOIN BAO_T_SUB_ACCOUNT_INFO tt4 ON tt4.id = tt1.sub_account_id  ")
		.append(" LEFT JOIN BAO_T_INVEST_INFO tt2 ON tt2.id = tt4.relate_primary ")
		.append(" LEFT JOIN BAO_T_PRODUCT_INFO tt3 ON tt3.id = tt2.product_id ")
		.append(" where  1 = 1  ");
		
		List<Object> objList=new ArrayList<Object>();
		objList.add(Constant.PRODUCT_TYPE_01);
		objList.add(queryCurrentValue(Constant.PRODUCT_TYPE_01));
		objList.add(new Date());
		objList.add(Constant.VALID_STATUS_VALID);
		objList.add(Constant.PRODUCT_TYPE_01);
		
		objList.add(Constant.PRODUCT_TYPE_04);
		objList.add(queryCurrentValue(Constant.PRODUCT_TYPE_04));
		objList.add(new Date());
		objList.add(Constant.TERM_INVEST_STATUS_EARN);
		objList.add(Constant.PRODUCT_TYPE_04);
		
		if(!StringUtils.isEmpty(param.get("productName"))){
			sqlString.append(" and tt1.PRODUCT_NAME = ?");
			objList.add(param.get("productName"));
		}
		
		if(!StringUtils.isEmpty(param.get("custId"))){
			sqlString.append(" and tt1.CUST_ID = ?");
			objList.add(param.get("custId"));
		}
		
		if(!StringUtils.isEmpty(param.get("dateBegin"))){
			sqlString.append(" and tt1.recordDate >= ?");
			objList.add(DateUtils.formatDate(DateUtils.parseStandardDate((String)param.get("dateBegin")), "yyyyMMdd"));
		}

		if(!StringUtils.isEmpty(param.get("dateEnd"))){
			sqlString.append(" and tt1.recordDate <= ?");
			objList.add(DateUtils.formatDate(DateUtils.getEndDate(DateUtils.parseStandardDate((String)param.get("dateEnd"))), "yyyyMMdd"));
		}
		
		sqlString.append(" order by tt1.recordDate desc, tt2.curr_term desc, tt1.direc asc ");
		
		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), (int)param.get("start"), (int)param.get("length"));
	}

	@Override
	public BigDecimal queryCurrentValue(String productName) {
		
		Date now = new Date();
		BigDecimal allLoanValue = new BigDecimal("0");
		
		// 计算当前债权所有价值
		StringBuffer sumValueSqlString= new StringBuffer()
		.append(" SELECT NVL(SUM(D.VALUE_REPAYMENT_AFTER), 0) \"totalPv\" ")
		.append(" FROM BAO_T_LOAN_INFO A  ")
		.append(" INNER JOIN BAO_T_LOAN_DETAIL_INFO B ON B.LOAN_ID = A.ID ")
		.append(" INNER JOIN BAO_T_CREDIT_RIGHT_VALUE D ON A.ID = D.LOAN_ID AND D.VALUE_DATE = TO_CHAR(?, 'YYYYMMDD') ")
		.append(" WHERE B.CREDIT_RIGHT_STATUS = '正常' AND A.ID IN ( ")
		.append("       SELECT T.LOAN_ID ")
		.append("       FROM BAO_T_ALLOT_DETAIL_INFO T ")
		.append("       WHERE T.ALLOT_ID IN  ")
		.append("       ( ")
		.append("             SELECT S.ID FROM BAO_T_ALLOT_INFO S ")
		.append("             WHERE S.ALLOT_STATUS != '已撤销' ")
		.append("                  AND S.RELATE_PRIMARY IN ( ")
		.append("                      SELECT ID ")
		.append("                      FROM BAO_T_PRODUCT_TYPE_INFO M ")
		.append("                      WHERE M.TYPE_NAME = ? ")
		.append("                  ) ")
		.append("       ) ")
		.append(" )");		
		
		List<Map<String, Object>> sumValueList = repositoryUtil.queryForMap(sumValueSqlString.toString(), new Object[]{now, productName});
		if(sumValueList == null || sumValueList.size() == 0) {
			return new BigDecimal("0");
		}
		allLoanValue = ArithUtil.add(allLoanValue, new BigDecimal(sumValueList.get(0).get("totalPv").toString()));

		// 计算未处理的还款
		StringBuffer sumUnHandleSqlString= new StringBuffer()
		.append(" SELECT NVL(SUM(A.REPAY_AMOUNT - ALREADY_REPAY_AMT), 0) \"totalPv\" ")
		.append(" FROM BAO_T_REPAYMENT_RECORD_INFO A  ")
		.append(" WHERE A.HANDLE_STATUS = '未处理' AND A.LOAN_ID IN ( ")
		.append("       SELECT T.LOAN_ID ")
		.append("       FROM BAO_T_ALLOT_DETAIL_INFO T ")
		.append("       WHERE T.ALLOT_ID IN  ")
		.append("       ( ")
		.append("             SELECT S.ID FROM BAO_T_ALLOT_INFO S ")
		.append("             WHERE S.ALLOT_STATUS != '已撤销' ")
		.append("                  AND S.RELATE_PRIMARY IN ( ")
		.append("                      SELECT ID ")
		.append("                      FROM BAO_T_PRODUCT_TYPE_INFO M ")
		.append("                      WHERE M.TYPE_NAME = ? ")
		.append("                  ) ")
		.append("       ) ")
		.append(" ) ");
		List<Map<String, Object>> sumUnHandleList = repositoryUtil.queryForMap(sumUnHandleSqlString.toString(), new Object[]{productName});
		if(sumUnHandleList == null || sumUnHandleList.size() == 0) {
			return new BigDecimal("0");
		}
		allLoanValue = ArithUtil.add(allLoanValue, new BigDecimal(sumUnHandleList.get(0).get("totalPv").toString()));
		
		return allLoanValue;
	}

	@Override
	public BigDecimal queryUserValue(String custId, String subAccountId, String productName) {
		
		if(Constant.PRODUCT_TYPE_01.equals(productName)) { // 活期宝
			StringBuffer sqlString= new StringBuffer()
			.append("   SELECT ")
			.append("     NVL(SUM(T.ACCOUNT_TOTAL_VALUE), 0) \"totalPv\" ")
			.append("   FROM BAO_T_SUB_ACCOUNT_INFO T ")
			.append("   WHERE T.RELATE_PRIMARY IN ( ")
			.append("         SELECT ID ")
			.append("         FROM BAO_T_INVEST_INFO S ")
			.append("         WHERE S.INVEST_STATUS = '有效' AND S.PRODUCT_ID IN ( ")
			.append("               SELECT ID ")
			.append("               FROM BAO_T_PRODUCT_INFO M ")
			.append("               WHERE M.PRODUCT_TYPE IN ( ")
			.append("                     SELECT ID ")
			.append("                      FROM BAO_T_PRODUCT_TYPE_INFO N ")
			.append("                      WHERE N.TYPE_NAME = ? ")
			.append("               ) ")
			.append("         ) ")
			.append("   ) AND CUST_ID = ? ");
			
			List<Map<String, Object>> sumValueList = repositoryUtil.queryForMap(sqlString.toString(), new Object[]{productName, custId});
			if(sumValueList == null || sumValueList.size() == 0) {
				return new BigDecimal("0");
			}
			return new BigDecimal(sumValueList.get(0).get("totalPv").toString());

		}
		else {
			StringBuffer sqlString= new StringBuffer()
			.append("   SELECT ")
			.append("     NVL(SUM(T.ACCOUNT_TOTAL_VALUE), 0) \"totalPv\" ")
			.append("   FROM BAO_T_SUB_ACCOUNT_INFO T ")
			.append("   WHERE T.RELATE_PRIMARY IN ( ")
			.append("         SELECT ID ")
			.append("         FROM BAO_T_INVEST_INFO S ")
			.append("         WHERE S.INVEST_STATUS NOT IN (?, ?) ")
			.append("         AND S.PRODUCT_ID IN ( ")
			.append("               SELECT ID ")
			.append("               FROM BAO_T_PRODUCT_INFO M ")
			.append("               WHERE M.PRODUCT_TYPE IN ( ")
			.append("                     SELECT ID ")
			.append("                      FROM BAO_T_PRODUCT_TYPE_INFO N ")
			.append("                      WHERE N.TYPE_NAME = ? ")
			.append("               ) ")
			.append("         ) ")
			.append("   ) AND CUST_ID = ? AND ID = ? ");
			
			List<Map<String, Object>> sumValueList = repositoryUtil.queryForMap(sqlString.toString(), new Object[]{Constant.TERM_INVEST_STATUS_ADVANCE_FINISH, Constant.TERM_INVEST_STATUS_FINISH, productName, custId, subAccountId});
			if(sumValueList == null || sumValueList.size() == 0) {
				return new BigDecimal("0");
			}
			return new BigDecimal(sumValueList.get(0).get("totalPv").toString());
		}
	}
}
