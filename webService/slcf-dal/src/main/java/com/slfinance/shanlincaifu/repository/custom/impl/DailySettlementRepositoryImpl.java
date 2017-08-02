package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.DailyInterestInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.DailySettlementRepository;
import com.slfinance.shanlincaifu.utils.Constant;

@Repository
public class DailySettlementRepositoryImpl implements DailySettlementRepository {
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public BigDecimal findActualInterest(Map<String, Object> param) {
		StringBuffer sqlString = new StringBuffer()

				.append("SELECT nvl(SUM(t1.VALUE_REPAYMENT_BEFORE - t2.VALUE_REPAYMENT_AFTER),0) \"actualInterest\"                                                                                                    ")
				.append("   FROM (SELECT PV.VALUE_REPAYMENT_BEFORE, PV.LOAN_ID   ")
				.append("           FROM BAO_T_PRODUCT_TYPE_INFO PT,                                                                                                                          ")
				.append("                BAO_T_ALLOT_INFO ALLOT,                                                                                                                              ")
				.append("                BAO_T_ALLOT_DETAIL_INFO ALLOTDETAIL,                                                                                                                 ")
				.append("                BAO_T_LOAN_INFO LOAN,                                                                                                                                ")
				.append("                BAO_T_CREDIT_RIGHT_VALUE PV,BAO_T_LOAN_DETAIL_INFO LOANDETAIL                                                                                                                          ")
				.append("          WHERE     PT.ID = ALLOT.RELATE_PRIMARY                                                                                                                     ")
				.append("                AND ALLOT.RELATE_TYPE = 'BAO_T_PRODUCT_TYPE_INFO'                                                                                                    ")
				.append("                AND ALLOT.ID = ALLOTDETAIL.ALLOT_ID                                                                                                                  ")
				.append("                AND ALLOTDETAIL.LOAN_ID = LOAN.ID                                                                                                                    ")
				.append("                AND LOAN.ID = PV.LOAN_ID                                                                                                                            ")
				.append(" 				 AND LOAN.ID =LOANDETAIL.LOAN_ID AND LOANDETAIL.CREDIT_RIGHT_STATUS='正常'")
				.append("                AND VALUE_DATE = '"+ param.get("execDate")	+ "'                                                                                                       ")
				.append("                and ALLOT.ALLOT_STATUS in('已使用','已分配')                                                                                                               ")
				.append("                AND PT.TYPE_NAME = '"+ param.get("typeName")+ "')  t1,                                                                                                                     ")
				.append("        (SELECT PV.id, PV.VALUE_REPAYMENT_AFTER, PV.LOAN_ID  ")
				.append("           FROM BAO_T_PRODUCT_TYPE_INFO PT,                                                                                                                          ")
				.append("                BAO_T_ALLOT_INFO ALLOT,                                                                                                                              ")
				.append("                BAO_T_ALLOT_DETAIL_INFO ALLOTDETAIL,                                                                                                                 ")
				.append("                BAO_T_LOAN_INFO LOAN,                                                                                                                                ")
				.append("                BAO_T_CREDIT_RIGHT_VALUE PV,BAO_T_LOAN_DETAIL_INFO LOANDETAIL                                                                                                                        ")
				.append("          WHERE     PT.ID = ALLOT.RELATE_PRIMARY                                                                                                                     ")
				.append("                AND ALLOT.RELATE_TYPE = 'BAO_T_PRODUCT_TYPE_INFO'                                                                                                    ")
				.append("                AND ALLOT.ID = ALLOTDETAIL.ALLOT_ID                                                                                                                  ")
				.append("                AND ALLOTDETAIL.LOAN_ID = LOAN.ID                                                                                                                    ")
				.append("                AND LOAN.ID = PV.LOAN_ID                                                                                                                             ")
				.append(" 				 AND LOAN.ID =LOANDETAIL.LOAN_ID AND LOANDETAIL.CREDIT_RIGHT_STATUS='正常'")
				.append("                AND VALUE_DATE = '"+ param.get("preExecDate")    	+ "'                                                                                                      ")
				.append("                and ALLOT.ALLOT_STATUS in('已使用','已分配')                                                                                                                    ")
				.append("                AND PT.TYPE_NAME = '"+ param.get("typeName")+ "') t2                                                                                                                      ")
				.append("  WHERE t1.LOAN_ID = t2.LOAN_ID                                                                                                                                      ");

		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				sqlString.toString(), null);
		if (list != null)
			return new BigDecimal(list.get(0).get("actualInterest").toString());
		else
			return new BigDecimal("0");
	}

	@Override
	public List<Map<String, Object>> findExceptInterest(Map<String, Object> param) {
		// TODO Auto-generated method stub
		StringBuffer sqlString = new StringBuffer()
				.append(" SELECT account.id \"id\",nvl(account.ACCOUNT_AVAILABLE_VALUE * (rate.YEAR_RATE + rate.AWARD_RATE)/365,0) \"exceptInterest\"")
				.append(" FROM BAO_T_PRODUCT_TYPE_INFO PT,")
				.append("      BAO_T_PRODUCT_INFO PRO,")
				.append("      BAO_T_PRODUCT_RATE_INFO rate,")
				.append("      BAO_T_INVEST_INFO invest,")
				.append("      BAO_T_SUB_ACCOUNT_INFO account,")
				.append("      (SELECT (to_date('"+ param.get("execDate")+ "','yyyyMMdd') - TO_DATE (INVEST.INVEST_DATE, 'yyyyMMdd')) dates, invest.id")
				.append("      FROM BAO_T_INVEST_INFO invest WHERE invest.INVEST_STATUS = '有效') t")
				.append(" WHERE PT.ID = PRO.PRODUCT_TYPE")
				.append("  	AND rate.PRODUCT_ID = PRO.id")
				.append("   AND invest.PRODUCT_ID = PRO.id")
				.append("   AND invest.ID = account.RELATE_PRIMARY")
				.append("	AND invest.INVEST_STATUS = '有效'")
				.append(" 	AND t.dates >= LOWER_LIMIT_DAY ")
				.append("  	AND t.dates <= UPPER_LIMIT_DAY")
				.append("   AND PT.TYPE_NAME = '"+ param.get("typeName")+ "' ")
				.append(" 	AND t.id=invest.id");
				//.append(" 	AND account.id='"+ param.get("accountId")+ "'");
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), null);
		return list;
		/*if(list!=null)
			return new BigDecimal(list.get(0).get("exceptInterest").toString());
		else
			return new BigDecimal("0");*/
	}
	
	public List<Map<String, Object>> findSubAccount(Map<String, Object> param) {
		StringBuffer sqlString = new StringBuffer()
		//.append("select PRO.PRODUCT_TYPE \"productType\",RATE.YEAR_RATE \"yearRate\",RATE.YEAR_RATE/365 \"dateRate\",")		
		.append(" select a.id \"id\", a.exceptInterest \"exceptInterest\", b.investDate \"investDate\", ")
		.append("             b.accountId \"accountId\", b.accountTotalValue \"accountTotalValue\", b.subAccountNo \"subAccountNo\", ")
		.append("             b.accountAvailableValue \"accountAvailableValue\", b.accountFreezeValue \"accountFreezeValue\", ")
		.append("             b.custId \"custId\", b.relatePrimary \"relatePrimary\", b.relateType \"relateType\", b.accountAmount \"accountAmount\"  ")
		.append(" from   ")
		.append(" ( ")
		.append(" SELECT account.id id,nvl(account.ACCOUNT_AVAILABLE_VALUE * (rate.YEAR_RATE + rate.AWARD_RATE)/365,0) exceptInterest")
		.append(" FROM BAO_T_PRODUCT_TYPE_INFO PT,")
		.append("      BAO_T_PRODUCT_INFO PRO,")
		.append("      BAO_T_PRODUCT_RATE_INFO rate,")
		.append("      BAO_T_INVEST_INFO invest,")
		.append("      BAO_T_SUB_ACCOUNT_INFO account,")
		.append("      (SELECT (to_date('"+ param.get("execDate")+ "','yyyyMMdd') - TO_DATE (INVEST.INVEST_DATE, 'yyyyMMdd')) dates, invest.id")
		.append("      FROM BAO_T_INVEST_INFO invest WHERE invest.INVEST_STATUS = '有效') t")
		.append(" WHERE PT.ID = PRO.PRODUCT_TYPE")
		.append("  	AND rate.PRODUCT_ID = PRO.id")
		.append("   AND invest.PRODUCT_ID = PRO.id")
		.append("   AND invest.ID = account.RELATE_PRIMARY")
		.append("	AND invest.INVEST_STATUS = '有效'")
		.append(" 	AND t.dates >= LOWER_LIMIT_DAY ")
		.append("  	AND t.dates <= UPPER_LIMIT_DAY")
		.append("   AND PT.TYPE_NAME = '"+ param.get("typeName")+ "' ")
		.append(" 	AND t.id=invest.id")
		.append("  ) a, ")
		.append(" ( ")
		.append("select invest.INVEST_DATE investDate,ACCOUNT.ACCOUNT_ID accountId,account.id id,account.ACCOUNT_TOTAL_VALUE accountTotalValue,account.SUB_ACCOUNT_NO subAccountNo,")
		.append("nvl(ACCOUNT.ACCOUNT_AVAILABLE_VALUE,0) accountAvailableValue,")
		.append("nvl(ACCOUNT.ACCOUNT_FREEZE_VALUE,0) accountFreezeValue,")
		.append("ACCOUNT.CUST_ID custId,")
		.append("ACCOUNT.RELATE_PRIMARY relatePrimary,")
		.append("ACCOUNT.RELATE_TYPE relateType,")
		.append("ACCOUNT.ACCOUNT_AMOUNT accountAmount")
		.append("   FROM BAO_T_PRODUCT_TYPE_INFO PT,")
		.append("         BAO_T_PRODUCT_INFO PRO,")
		.append("         BAO_T_PRODUCT_DETAIL_INFO PRODETA,")
		.append("         BAO_T_INVEST_INFO invest,")
		.append("         BAO_T_SUB_ACCOUNT_INFO account")
		.append("   WHERE PT.ID = PRO.PRODUCT_TYPE ")
		.append("   AND PRO.ID = PRODETA.PRODUCT_ID")
		.append("   and invest.PRODUCT_ID=PRO.id")
		.append("  	and invest.ID=account.RELATE_PRIMARY")
		.append("   AND to_date(INVEST_DATE,'yyyymmdd')<= to_date('"+ param.get("execDate")+"','yyyymmdd') ")
		.append("   AND PT.TYPE_NAME = '"+ param.get("typeName")+ "' ")
		.append(" 	and invest.INVEST_STATUS='有效'")
		.append(" 	) b where a.id = b.id ");
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), null);
		return list;
	}
	
	public List<Map<String, Object>> findSubAccountForPage(Map<String, Object> param) {
		StringBuffer sqlString = new StringBuffer()
		//.append("select PRO.PRODUCT_TYPE \"productType\",RATE.YEAR_RATE \"yearRate\",RATE.YEAR_RATE/365 \"dateRate\",")		
		.append(" select a.id \"id\", a.exceptInterest \"exceptInterest\", b.investDate \"investDate\", ")
		.append("             b.accountId \"accountId\", b.accountTotalValue \"accountTotalValue\", b.subAccountNo \"subAccountNo\", ")
		.append("             b.accountAvailableValue \"accountAvailableValue\", b.accountFreezeValue \"accountFreezeValue\", ")
		.append("             b.custId \"custId\", b.relatePrimary \"relatePrimary\", b.relateType \"relateType\", b.accountAmount \"accountAmount\"  ")
		.append(" from   ")
		.append(" ( ")
		.append(" SELECT account.id id,nvl(account.ACCOUNT_AVAILABLE_VALUE * (rate.YEAR_RATE + rate.AWARD_RATE)/365,0) exceptInterest")
		.append(" FROM BAO_T_PRODUCT_TYPE_INFO PT,")
		.append("      BAO_T_PRODUCT_INFO PRO,")
		.append("      BAO_T_PRODUCT_RATE_INFO rate,")
		.append("      BAO_T_INVEST_INFO invest,")
		.append("      BAO_T_SUB_ACCOUNT_INFO account,")
		.append("      (SELECT (to_date(?,'yyyyMMdd') - TO_DATE (INVEST.INVEST_DATE, 'yyyyMMdd')) dates, invest.id")
		.append("      FROM BAO_T_INVEST_INFO invest WHERE invest.INVEST_STATUS = '有效') t")
		.append(" WHERE PT.ID = PRO.PRODUCT_TYPE")
		.append("  	AND rate.PRODUCT_ID = PRO.id")
		.append("   AND invest.PRODUCT_ID = PRO.id")
		.append("   AND invest.ID = account.RELATE_PRIMARY")
		.append("	AND invest.INVEST_STATUS = '有效'")
		.append(" 	AND t.dates >= LOWER_LIMIT_DAY ")
		.append("  	AND t.dates <= UPPER_LIMIT_DAY")
		.append("   AND PT.TYPE_NAME = ? ")
		.append(" 	AND t.id=invest.id")
		.append("  ) a, ")
		.append(" ( ")
		.append("select invest.INVEST_DATE investDate,ACCOUNT.ACCOUNT_ID accountId,account.id id,account.ACCOUNT_TOTAL_VALUE accountTotalValue,account.SUB_ACCOUNT_NO subAccountNo,")
		.append("nvl(ACCOUNT.ACCOUNT_AVAILABLE_VALUE,0) accountAvailableValue,")
		.append("nvl(ACCOUNT.ACCOUNT_FREEZE_VALUE,0) accountFreezeValue,")
		.append("ACCOUNT.CUST_ID custId,")
		.append("ACCOUNT.RELATE_PRIMARY relatePrimary,")
		.append("ACCOUNT.RELATE_TYPE relateType,")
		.append("ACCOUNT.ACCOUNT_AMOUNT accountAmount, ")
		.append("invest.CREATE_DATE createDate ")
		.append("   FROM BAO_T_PRODUCT_TYPE_INFO PT,")
		.append("         BAO_T_PRODUCT_INFO PRO,")
		.append("         BAO_T_PRODUCT_DETAIL_INFO PRODETA,")
		.append("         BAO_T_INVEST_INFO invest,")
		.append("         BAO_T_SUB_ACCOUNT_INFO account")
		.append("   WHERE PT.ID = PRO.PRODUCT_TYPE ")
		.append("   AND PRO.ID = PRODETA.PRODUCT_ID")
		.append("   and invest.PRODUCT_ID=PRO.id")
		.append("  	and invest.ID=account.RELATE_PRIMARY")
		.append("   AND to_date(INVEST_DATE,'yyyymmdd')<= to_date(?,'yyyymmdd') ")
		.append("   AND PT.TYPE_NAME = ? ")
		.append(" 	and invest.INVEST_STATUS='有效'")
		.append(" 	) b where a.id = b.id ")
		.append("   order by b.createDate asc");
		List<Map<String, Object>> list = repositoryUtil.queryForPage(sqlString.toString(), new Object[] {(String)param.get("execDate"), (String)param.get("typeName"), (String)param.get("execDate"), (String)param.get("typeName")}, Integer.valueOf(param.get("start").toString()), Integer.valueOf(param.get("length").toString()));
		return list;
	}
	
	public int countSubAccount(Map<String, Object> param) {
		StringBuffer sqlString = new StringBuffer()
		.append("select count(1) \"total\" ")
		.append("   FROM BAO_T_PRODUCT_TYPE_INFO PT,")
		.append("         BAO_T_PRODUCT_INFO PRO,")
		.append("         BAO_T_PRODUCT_DETAIL_INFO PRODETA,")
		.append("         BAO_T_INVEST_INFO invest,")
		.append("         BAO_T_SUB_ACCOUNT_INFO account")
		.append("   WHERE PT.ID = PRO.PRODUCT_TYPE ")
		.append("   AND PRO.ID = PRODETA.PRODUCT_ID")
		.append("   and invest.PRODUCT_ID=PRO.id")
		.append("  	and invest.ID=account.RELATE_PRIMARY")
		.append("   AND to_date(INVEST_DATE,'yyyymmdd')<= to_date(?,'yyyymmdd') ")
		.append("   AND PT.TYPE_NAME = ? ")
		.append(" 	and invest.INVEST_STATUS='有效'");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[] {(String)param.get("execDate"), (String)param.get("typeName")});
		Map<String, Object> map = list.get(0);
		return Integer.valueOf(map.get("total").toString());
	}

	@Override
	public BigDecimal untreatedAmount(Map<String, Object> param) {
		StringBuffer sqlString = new StringBuffer()
				.append("select nvl(sum(record.REPAY_AMOUNT-record.ALREADY_REPAY_AMT),0) \"untreatedAmount\"")
				.append(" FROM BAO_T_PRODUCT_TYPE_INFO  PT,")
				.append("  BAO_T_ALLOT_INFO         ALLOT,")
				.append("  BAO_T_ALLOT_DETAIL_INFO  ALLOTDETAIL,")
				.append("  BAO_T_LOAN_INFO LOAN,")
				.append("  BAO_T_REPAYMENT_RECORD_INFO record ")
				.append(" WHERE PT.ID = ALLOT.RELATE_PRIMARY")
				.append("  AND ALLOT.RELATE_TYPE = 'BAO_T_PRODUCT_TYPE_INFO'")
				.append("  AND ALLOT.ID = ALLOTDETAIL.ALLOT_ID")
				.append("  AND ALLOTDETAIL.LOAN_ID = LOAN.ID")
				.append("  and record.LOAN_ID = LOAN.ID")
				.append("  and record.HANDLE_STATUS='未处理'")
				.append("  and PT.TYPE_NAME=? ");
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[]{(String)param.get("typeName")});
		if(list!=null)
			return new BigDecimal(list.get(0).get("untreatedAmount").toString());
		else
			return new BigDecimal("0");
	}

	@Override
	public BigDecimal findTotalPV(Map<String, Object> param) {
		StringBuffer sqlString = new StringBuffer()
		.append(" select nvl(sum(pv.VALUE_REPAYMENT_AFTER),0) \"totalPV\"")
		.append("   FROM BAO_T_PRODUCT_TYPE_INFO  PT,")
		.append("      BAO_T_ALLOT_INFO         ALLOT,")
		.append("      BAO_T_ALLOT_DETAIL_INFO  ALLOTDETAIL,")
		.append("      BAO_T_LOAN_INFO LOAN,BAO_T_LOAN_DETAIL_INFO LOANDETAIL,")
		.append("      BAO_T_CREDIT_RIGHT_VALUE PV")
		.append(" WHERE PT.ID = ALLOT.RELATE_PRIMARY")
		.append("  AND ALLOT.RELATE_TYPE = 'BAO_T_PRODUCT_TYPE_INFO'")
		.append("  AND ALLOT.ID = ALLOTDETAIL.ALLOT_ID")
		.append("  AND ALLOTDETAIL.LOAN_ID = LOAN.ID")
		.append("  AND LOAN.ID=PV.LOAN_ID")
		.append("  AND ALLOT.ALLOT_STATUS in('已使用','已分配')")  
		.append("  AND LOAN.ID = LOANDETAIL.LOAN_ID AND LOANDETAIL.CREDIT_RIGHT_STATUS='正常'")
		.append("  AND PV.VALUE_DATE = '"+ param.get("preExecDate")	+ "'")
		.append("  AND PT.TYPE_NAME='"+param.get("typeName")+"'");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), null);
		if(list!=null)
			return new BigDecimal(list.get(0).get("totalPV").toString());
		else
			return new BigDecimal("0");
	}
	
	@Override
	public int findCurrDateRecordCount(Map<String, Object> param) {
		StringBuffer sqlString = new StringBuffer()
		.append(" SELECT count(*) \"total\" ")
		.append("   FROM BAO_T_PRODUCT_TYPE_INFO PT,")
		.append("         BAO_T_PRODUCT_INFO PRO,")
		.append("         BAO_T_PRODUCT_DETAIL_INFO PRODETA,")
		.append("         BAO_T_INVEST_INFO invest,")
		.append("         BAO_T_SUB_ACCOUNT_INFO account,")
		.append(" 		  BAO_T_DAILY_INTEREST_INFO interest")
		.append("   WHERE PT.ID = PRO.PRODUCT_TYPE ")
		.append("   AND PRO.ID = PRODETA.PRODUCT_ID")
		.append("   and invest.PRODUCT_ID=PRO.id")
		.append("  	and invest.ID=account.RELATE_PRIMARY")
		.append("  	and interest.SUB_ACCOUNT_ID=account.id")
		.append("   AND PT.TYPE_NAME = '"+ param.get("typeName")+ "' ")
		.append(" 	and invest.INVEST_STATUS not in ( ?, ?, ?)")
		.append("   and to_char(interest.CREATE_DATE,'yyyyMMdd')='"+param.get("currDate")+"'");
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[] { Constant.VALID_STATUS_INVALID, Constant.TERM_INVEST_STATUS_FINISH, Constant.TERM_INVEST_STATUS_ADVANCE_FINISH});
		if(list!=null)
			return Integer.parseInt((list.get(0).get("total").toString()));
		else
			return 0;
	}

	@Transactional
	@Override
	public void batchInsert(List<DailyInterestInfoEntity> list) {
		for (int i = 0; i < list.size(); i++) {
			manager.persist(list.get(i));
		}
		manager.flush();
		manager.clear();
	}

	@Override
	public List<Map<String, Object>> findTYBExpireSubAccount(
			Map<String, Object> param) {
		StringBuffer sqlString = new StringBuffer()
		.append("select invest.INVEST_AMOUNT \"investAmount\",invest.id \"investId\",invest.INVEST_DATE \"investDate\",ACCOUNT.ACCOUNT_ID \"accountId\",account.id \"id\",account.ACCOUNT_TOTAL_VALUE \"accountTotalValue\",account.SUB_ACCOUNT_NO \"subAccountNo\",")
		.append("nvl(ACCOUNT.ACCOUNT_AVAILABLE_VALUE,0) \"accountAvailableValue\",")
		.append("nvl(ACCOUNT.ACCOUNT_FREEZE_VALUE,0) \"accountFreezeValue\",")
		.append("ACCOUNT.CUST_ID \"custId\",")
		.append("ACCOUNT.RELATE_PRIMARY \"relatePrimary\",")
		.append("ACCOUNT.RELATE_TYPE \"relateType\"")
		.append("   FROM BAO_T_PRODUCT_TYPE_INFO PT,")
		.append("         BAO_T_PRODUCT_INFO PRO,")
		.append("         BAO_T_PRODUCT_DETAIL_INFO PRODETA,")
		.append("         BAO_T_INVEST_INFO invest,")
		.append("         BAO_T_SUB_ACCOUNT_INFO account")
		.append("   WHERE PT.ID = PRO.PRODUCT_TYPE ")
		.append("   AND PRO.ID = PRODETA.PRODUCT_ID")
		.append("   and invest.PRODUCT_ID=PRO.id")
		.append("  	and invest.ID=account.RELATE_PRIMARY")
		.append("   AND to_date(invest.EXPIRE_DATE,'yyyymmdd')=to_date('"+ param.get("execDate")+ "','yyyymmdd')")
		.append("   AND PT.TYPE_NAME = '"+ param.get("typeName")+ "' ")
		.append(" 	and invest.INVEST_STATUS='有效'");
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), null);
		return list;
	}

	@Override
	public List<Map<String, Object>> findExperienceSendSmsCust(Map<String, Object> map) {
		StringBuffer sqlString = new StringBuffer()
		.append(" select b.id as \"id\" ,b.cust_name as \"custName\",b.mobile as \"mobile\" , a.trade_amount as \"experienceAmount\",trunc(c.trade_amount,2) as \"incomeAmount\" ")
		.append("   from bao_t_account_flow_info a, ")
		.append("        bao_t_cust_info         b, ")
		.append("        bao_t_account_flow_info c ")
		.append("  where a.cust_id = b.id ")
		.append("    and a.request_no=c.request_no ")
		.append("    and c.cust_id='C00002' ")
		.append("    and a.trade_type = '回收体验金' ")
		.append("    and a.trade_date = to_date('"+ map.get("execDate")+ "', 'yyyymmdd') ")
		.append("    and c.trade_amount>=0.01 ");
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), null);
		return list;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void batchUpdateList(List list) {
		for (int i = 0; i < list.size(); i++) {
			manager.merge(list.get(i));
			if(i%50 == 0) {
				manager.flush();
				manager.clear();
			}
		}		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void batchInsertList(List list) {
		for (int i = 0; i < list.size(); i++) {
			manager.persist(list.get(i));
			if(i%50 == 0) {
				manager.flush();
				manager.clear();
			}
		}
	
	}

	@Override
	public void batchUpdateSubAccount(final List<AccountFlowInfoEntity> list) {
		StringBuffer sql=new StringBuffer();
		sql.append("UPDATE BAO_T_SUB_ACCOUNT_INFO SET ACCOUNT_TOTAL_VALUE = ACCOUNT_TOTAL_VALUE + ?, ACCOUNT_AVAILABLE_VALUE = ACCOUNT_AVAILABLE_VALUE + ?, VERSION = VERSION + 1, LAST_UPDATE_DATE = ? WHERE ID = ? ");
	    jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
                     ps.setBigDecimal(1, list.get(i).getTradeAmount());
                     ps.setBigDecimal(2, list.get(i).getTradeAmount());
                     ps.setTimestamp(3, new Timestamp(list.get(i).getCreateDate().getTime()));
                     ps.setString(4,  list.get(i).getAccountId());
			}
			
			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}
	
	@Override
	public void batchUpdateAccount(final List<AccountInfoEntity> list) {
		
		StringBuffer sql=new StringBuffer()
		.append(" update bao_t_account_info set ")
	    .append(" account_total_amount = ?, ")
	    .append(" account_available_amount = ?, ")
	    .append(" version = version + 1, ")
	    .append(" last_update_user = ?, ")
	    .append(" last_update_date = ?  ")
	    .append(" where id = ? and version = ? ");
		
		jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
                     ps.setBigDecimal(1, list.get(i).getAccountTotalAmount());
                     ps.setBigDecimal(2, list.get(i).getAccountAvailableAmount());
                     ps.setString(3, list.get(i).getLastUpdateUser());
                     ps.setTimestamp(4, new Timestamp(list.get(i).getLastUpdateDate().getTime()));
                     ps.setString(5,  list.get(i).getId());
                     ps.setInt(6,  list.get(i).getVersion());
			}
			
			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
		
	}
	
	@Override
	public void batchUpdateSubAccountInfo(final List<SubAccountInfoEntity> list) {
		StringBuffer sql=new StringBuffer()
		.append(" UPDATE BAO_T_SUB_ACCOUNT_INFO SET ") 
		.append(" ACCOUNT_TOTAL_VALUE = ?, ")
		.append(" ACCOUNT_AVAILABLE_VALUE = ? , ")
		.append(" ACCOUNT_FREEZE_VALUE = ? , ")
		.append(" ACCOUNT_AMOUNT = ? , ")
		.append(" VERSION = VERSION + 1, ")
		.append(" LAST_UPDATE_DATE = ? ")
		.append(" WHERE ID = ? AND VERSION = ?");
	    jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
                     ps.setBigDecimal(1, list.get(i).getAccountTotalValue());
                     ps.setBigDecimal(2, list.get(i).getAccountAvailableValue());
                     ps.setBigDecimal(3, list.get(i).getAccountFreezeValue());
                     ps.setBigDecimal(4, list.get(i).getAccountAmount());
                     ps.setTimestamp(5, new Timestamp(list.get(i).getLastUpdateDate().getTime()));
                     ps.setString(6,  list.get(i).getId());
                     ps.setInt(7, list.get(i).getVersion());
			}
			
			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
		
	}


	@Override
	public List<Map<String, Object>> findWaitInterestForPage(Map<String, Object> param) {
		
		StringBuffer sqlString = new StringBuffer()
		.append("  select a.id \"id\", a.exceptInterest \"exceptInterest\", a.factInterest + a.freezeInterest \"factInterest\", b.investDate \"investDate\",  ")
		.append("                  b.accountId \"accountId\", b.accountTotalValue \"accountTotalValue\", b.subAccountNo \"subAccountNo\",  ")
		.append("                  b.accountAvailableValue \"accountAvailableValue\", b.accountFreezeValue \"accountFreezeValue\",  ")
		.append("                  b.custId \"custId\", b.relatePrimary \"relatePrimary\", b.relateType \"relateType\", b.accountAmount \"accountAmount\", ")
		.append("                  case when b.investDate < ? then least(a.exceptInterest, a.factInterest) ")
		.append("                       else 0 end \"userIncome\", ")
		.append("                  case when b.investDate < ? then case when a.factInterest > a.exceptInterest then a.factInterest - a.exceptInterest else 0 end  ")
		.append("                       else a.factInterest end + a.freezeInterest \"companyIncome\"                       ")
		.append("       ")
		.append("      from (  ")
		.append("      SELECT account.id id, ")
		.append("             trunc(nvl(account.ACCOUNT_AVAILABLE_VALUE * (rate.YEAR_RATE + rate.AWARD_RATE)/365,0), 8) exceptInterest, ")
		.append("             trunc(nvl(account.ACCOUNT_AVAILABLE_VALUE/(?+?)*?, 0), 8) factInterest,  ")
		.append("             trunc(nvl(account.account_freeze_value/(?+?)*?, 0), 8) freezeInterest ")
		.append("      FROM BAO_T_PRODUCT_TYPE_INFO PT, ")
		.append("           BAO_T_PRODUCT_INFO PRO, ")
		.append("           BAO_T_PRODUCT_RATE_INFO rate, ")
		.append("           BAO_T_INVEST_INFO invest, ")
		.append("           BAO_T_SUB_ACCOUNT_INFO account, ")
		.append("           (SELECT (to_date(?,'yyyyMMdd') - TO_DATE (INVEST.INVEST_DATE, 'yyyyMMdd')) dates, invest.id ")
		.append("           FROM BAO_T_INVEST_INFO invest WHERE invest.INVEST_STATUS = '有效') t ")
		.append("      WHERE PT.ID = PRO.PRODUCT_TYPE ")
		.append("         AND rate.PRODUCT_ID = PRO.id ")
		.append("        AND invest.PRODUCT_ID = PRO.id ")
		.append("        AND invest.ID = account.RELATE_PRIMARY ")
		.append("       AND invest.INVEST_STATUS = '有效' ")
		.append("        AND t.dates >= LOWER_LIMIT_DAY  ")
		.append("         AND t.dates <= UPPER_LIMIT_DAY ")
		.append("        AND PT.TYPE_NAME = ?  ")
		.append("        AND t.id=invest.id ")
		.append("       ) a,  ")
		.append("      (  ")
		.append("     select invest.INVEST_DATE investDate,ACCOUNT.ACCOUNT_ID accountId,account.id id, ")
		.append("     account.ACCOUNT_TOTAL_VALUE accountTotalValue, account.SUB_ACCOUNT_NO subAccountNo, ")
		.append("     nvl(ACCOUNT.ACCOUNT_AVAILABLE_VALUE,0) accountAvailableValue, ")
		.append("     nvl(ACCOUNT.ACCOUNT_FREEZE_VALUE,0) accountFreezeValue, ")
		.append("     ACCOUNT.CUST_ID custId, ")
		.append("     ACCOUNT.RELATE_PRIMARY relatePrimary, ")
		.append("     ACCOUNT.RELATE_TYPE relateType, ")
		.append("     ACCOUNT.ACCOUNT_AMOUNT accountAmount ")
		.append("        FROM BAO_T_PRODUCT_TYPE_INFO PT, ")
		.append("              BAO_T_PRODUCT_INFO PRO, ")
		.append("              BAO_T_PRODUCT_DETAIL_INFO PRODETA, ")
		.append("              BAO_T_INVEST_INFO invest, ")
		.append("              BAO_T_SUB_ACCOUNT_INFO account ")
		.append("        WHERE PT.ID = PRO.PRODUCT_TYPE  ")
		.append("        AND PRO.ID = PRODETA.PRODUCT_ID ")
		.append("        and invest.PRODUCT_ID=PRO.id ")
		.append("         and invest.ID=account.RELATE_PRIMARY ")
		.append("        AND INVEST_DATE <= ?  ")
		.append("        AND PT.TYPE_NAME = ?  ")
		.append("        and invest.INVEST_STATUS='有效' ")
		.append("        ) b where a.id = b.id order by b.custId");
		
		List<Map<String, Object>> list = repositoryUtil.queryForPage(sqlString.toString(), new Object[] {
			(String)param.get("execDate"), 
			(String)param.get("execDate"), 
			(BigDecimal)param.get("totalPV"), 
			(BigDecimal)param.get("untreatedAmount"), 
			(BigDecimal)param.get("totalActualInterest"), 
			(BigDecimal)param.get("totalPV"), 
			(BigDecimal)param.get("untreatedAmount"), 
			(BigDecimal)param.get("totalActualInterest"), 
			(String)param.get("execDate"), 
			(String)param.get("typeName"), 
			(String)param.get("execDate"), 
			(String)param.get("typeName")}, 
			Integer.valueOf(param.get("start").toString()), 
			Integer.valueOf(param.get("length").toString()));
		return list;
	}

	@Override
	public int countWaitInterestForPage(Map<String, Object> param) {
		
		StringBuffer sqlString = new StringBuffer()
		.append("  select count(*) \"total\"  ")
		.append("  from (select  a.id, b.accountId    ")
		.append("      from (  ")
		.append("      SELECT account.id id ")
		.append("      FROM BAO_T_PRODUCT_TYPE_INFO PT, ")
		.append("           BAO_T_PRODUCT_INFO PRO, ")
		.append("           BAO_T_PRODUCT_RATE_INFO rate, ")
		.append("           BAO_T_INVEST_INFO invest, ")
		.append("           BAO_T_SUB_ACCOUNT_INFO account, ")
		.append("           (SELECT (to_date(?,'yyyyMMdd') - TO_DATE (INVEST.INVEST_DATE, 'yyyyMMdd')) dates, invest.id ")
		.append("           FROM BAO_T_INVEST_INFO invest WHERE invest.INVEST_STATUS = '有效') t ")
		.append("      WHERE PT.ID = PRO.PRODUCT_TYPE ")
		.append("         AND rate.PRODUCT_ID = PRO.id ")
		.append("        AND invest.PRODUCT_ID = PRO.id ")
		.append("        AND invest.ID = account.RELATE_PRIMARY ")
		.append("       AND invest.INVEST_STATUS = '有效' ")
		.append("        AND t.dates >= LOWER_LIMIT_DAY  ")
		.append("         AND t.dates <= UPPER_LIMIT_DAY ")
		.append("        AND PT.TYPE_NAME = ?  ")
		.append("        AND t.id=invest.id ")
		.append("       ) a,  ")
		.append("      (  ")
		.append("     select invest.INVEST_DATE investDate,ACCOUNT.ACCOUNT_ID accountId,account.id id, ")
		.append("     account.ACCOUNT_TOTAL_VALUE accountTotalValue, account.SUB_ACCOUNT_NO subAccountNo, ")
		.append("     nvl(ACCOUNT.ACCOUNT_AVAILABLE_VALUE,0) accountAvailableValue, ")
		.append("     nvl(ACCOUNT.ACCOUNT_FREEZE_VALUE,0) accountFreezeValue, ")
		.append("     ACCOUNT.CUST_ID custId, ")
		.append("     ACCOUNT.RELATE_PRIMARY relatePrimary, ")
		.append("     ACCOUNT.RELATE_TYPE relateType, ")
		.append("     ACCOUNT.ACCOUNT_AMOUNT accountAmount ")
		.append("        FROM BAO_T_PRODUCT_TYPE_INFO PT, ")
		.append("              BAO_T_PRODUCT_INFO PRO, ")
		.append("              BAO_T_PRODUCT_DETAIL_INFO PRODETA, ")
		.append("              BAO_T_INVEST_INFO invest, ")
		.append("              BAO_T_SUB_ACCOUNT_INFO account ")
		.append("        WHERE PT.ID = PRO.PRODUCT_TYPE  ")
		.append("        AND PRO.ID = PRODETA.PRODUCT_ID ")
		.append("        and invest.PRODUCT_ID=PRO.id ")
		.append("         and invest.ID=account.RELATE_PRIMARY ")
		.append("        AND INVEST_DATE <= ?  ")
		.append("        AND PT.TYPE_NAME = ?  ")
		.append("        and invest.INVEST_STATUS='有效' ")
		.append("        ) b where a.id = b.id ")
		.append("  )  ");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[] {
			(String)param.get("execDate"), 
			(String)param.get("typeName"), 
			(String)param.get("execDate"), 
			(String)param.get("typeName")});
		if(list!=null)
			return Integer.parseInt((list.get(0).get("total").toString()));
		else
			return 0;
	}

	@Override
	public void batchInsertAccountFlow(final List<AccountFlowInfoEntity> list) {
		StringBuffer sql=new StringBuffer()
		.append(" insert into bao_t_account_flow_info ")
		.append(" (id, cust_id, account_id,  ")
		.append("  account_type, trade_type, request_no,  ")
		.append("  old_trade_no, trade_no, bankroll_flow_direction,  ")
		.append("  trade_amount, trade_date, account_total_amount,  ")
		.append("  account_freeze_amount, account_available, record_status,  ")
		.append("  create_user, create_date, last_update_user,  ")
		.append("  last_update_date, version, memo,  ")
		.append("  flow_type, cash_amount) ")
		.append(" values ")
		.append(" (?, ?, ?,  ")
		.append("  ?, ?, ?,  ")
		.append("  ?, ?, ?,  ")
		.append("  ?, ?, ?,  ")
		.append("  ?, ?, ?,  ")
		.append("  ?, ?, ?,  ")
		.append("  ?, ?, ?,  ")
		.append("  ?, ?) ");
		
	    jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
                     ps.setString(1, list.get(i).getId());
                     ps.setString(2, list.get(i).getCustId());
                     ps.setString(3, list.get(i).getAccountId());
                     ps.setString(4, list.get(i).getAccountType());
                     ps.setString(5, list.get(i).getTradeType());
                     ps.setString(6, list.get(i).getRequestNo());
                     ps.setString(7, list.get(i).getOldTradeNo());
                     ps.setString(8, list.get(i).getTradeNo());
                     ps.setString(9, list.get(i).getBankrollFlowDirection());
                     ps.setBigDecimal(10, list.get(i).getTradeAmount());
                     ps.setDate(11, new java.sql.Date(list.get(i).getTradeDate().getTime()));
                     ps.setBigDecimal(12, list.get(i).getAccountTotalAmount());
                     ps.setBigDecimal(13, list.get(i).getAccountFreezeAmount());
                     ps.setBigDecimal(14, list.get(i).getAccountAvailable());
                     ps.setString(15, list.get(i).getRecordStatus());
                     ps.setString(16, list.get(i).getCreateUser());
                     ps.setTimestamp(17, new Timestamp(list.get(i).getCreateDate().getTime()));
                     ps.setString(18, list.get(i).getLastUpdateUser());
                     ps.setTimestamp(19, new Timestamp(list.get(i).getLastUpdateDate().getTime()));
                     ps.setInt(20, list.get(i).getVersion());
                     ps.setString(21,  list.get(i).getMemo());
                     ps.setString(22,  list.get(i).getFlowType());
                     ps.setBigDecimal(23,  list.get(i).getCashAmount());
			}
			
			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
		
	}

//	@Override
//	public void batchInsertAccountFlowDetail(final List<AccountFlowDetailEntity> list) {
//		StringBuffer sql=new StringBuffer()
//		.append("  insert into bao_t_account_flow_detail ")
//		.append("    (id, account_flow_id, subject_type,  ")
//		.append("     subject_direction, trade_amount, trade_desc,  ")
//		.append("     record_status, create_user, create_date,  ")
//		.append("     last_update_user, last_update_date, version,  ")
//		.append("     memo, target_account) ")
//		.append("  values ")
//		.append("    (?, ?, ?,  ")
//		.append("     ?, ?, ?,  ")
//		.append("     ?, ?, ?,  ")
//		.append("     ?, ?, ?,  ")
//		.append("     ?, ?) ");
//		
//	    jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
//			
//			@Override
//			public void setValues(PreparedStatement ps, int i) throws SQLException {
//                     ps.setString(1, list.get(i).getId());
//                     ps.setString(2, list.get(i).getAccountFlowId());
//                     ps.setString(3, list.get(i).getSubjectType());
//                     ps.setString(4, list.get(i).getSubjectDirection());
//                     ps.setBigDecimal(5, list.get(i).getTradeAmount());
//                     ps.setString(6, list.get(i).getTradeDesc());
//                     ps.setString(7, list.get(i).getRecordStatus());
//                     ps.setString(8, list.get(i).getCreateUser());
//                     ps.setTimestamp(9, new Timestamp(list.get(i).getCreateDate().getTime()));
//                     ps.setString(10, list.get(i).getLastUpdateUser());
//                     ps.setTimestamp(11, new Timestamp(list.get(i).getLastUpdateDate().getTime()));
//                     ps.setInt(12, list.get(i).getVersion());
//                     ps.setString(13,  list.get(i).getMemo());
//                     ps.setString(14,  list.get(i).getTargetAccount());
//			}
//			
//			@Override
//			public int getBatchSize() {
//				return list.size();
//			}
//		});
//		
//	}

//	@Override
//	public void batchInsertFlowBusiRelation(final List<FlowBusiRelationEntity> list) {
//		
//		StringBuffer sql=new StringBuffer()
//		.append("  insert into bao_t_flow_busi_relation ")
//		.append("    (id, account_flow_id, relate_type,  ")
//		.append("     relate_primary, create_date,  ")
//		.append("     memo, version) ")
//		.append("  values ")
//		.append("    (?, ?, ?,  ")
//		.append("     ?, ?, ")
//		.append("     ?, ?) ");
//		
//	    jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
//			
//			@Override
//			public void setValues(PreparedStatement ps, int i) throws SQLException {
//                     ps.setString(1, list.get(i).getId());
//                     ps.setString(2, list.get(i).getAccountFlowId());
//                     ps.setString(3, list.get(i).getRelateType());
//                     ps.setString(4, list.get(i).getRelatePrimary());
//                     ps.setTimestamp(5, new Timestamp(list.get(i).getCreateDate().getTime()));
//                     ps.setString(6,  list.get(i).getMemo());
//                     ps.setInt(7, list.get(i).getVersion());
//			}
//			
//			@Override
//			public int getBatchSize() {
//				return list.size();
//			}
//		});
//	}

	@Override
	public void batchDailyInterestInfo(final List<DailyInterestInfoEntity> list) {
		
		StringBuffer sql=new StringBuffer()
		.append("   insert into bao_t_daily_interest_info ")
		.append("     (id, sub_account_id, curr_date,  ")
		.append("      expect_interest, fact_interest, create_date,  ")
		.append("      version, fact_gain_interest) ")
		.append("   values ")
		.append("     (?, ?, ?,  ")
		.append("     ?, ?, ?,  ")
		.append("     ?, ?) ");
		
	    jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
                     ps.setString(1, list.get(i).getId());
                     ps.setString(2, list.get(i).getSubAccountId());
                     ps.setString(3, list.get(i).getCurrDate());
                     ps.setBigDecimal(4, list.get(i).getExpectInterest());
                     ps.setBigDecimal(5,  list.get(i).getFactInterest());
                     ps.setTimestamp(6, new Timestamp(list.get(i).getCreateDate().getTime()));                   
                     ps.setInt(7, list.get(i).getVersion());
                     ps.setBigDecimal(8,  list.get(i).getFactGainInterest());
			}
			
			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public void saveTermDailyInterest(Date now, BigDecimal totalPv,
			BigDecimal untreatedAmount, BigDecimal totalInterest) {
		StringBuffer sql=new StringBuffer()
		.append(" insert into bao_t_daily_interest_info ")
		.append("     (id, sub_account_id, curr_date,  ")
		.append("     expect_interest, fact_interest, create_date,  ")
		.append("     version, fact_gain_interest) ")
		.append(" select  ")
		.append("     (sys_guid(), t.id, to_char(?, 'yyyyMMdd'),  ")
		.append("     t.account_total_value / (? + ?) * ? , t.account_total_value / (? + ?) * ? , ?,  ")
		.append("     0, t.account_total_value / (? + ?) * ?  ")
		.append(" from bao_t_sub_account_info t ")
		.append(" where t.relate_primary in ( ")
		.append("       select m.id ")
		.append("       from bao_t_invest_info m,  ")
		.append("            bao_t_product_info n,  ")
		.append("            bao_t_product_type_info q ")
		.append("       where m.product_id = n.id  ")
		.append("       and n.product_type = q.id  ")
		.append("       and q.type_name = ? ")
		.append("       and m.invest_status not in (?, ?) ")
		.append(" )  ");
		
		jdbcTemplate.update(sql.toString(), 
				new Object[]{	now,
								totalPv,
								untreatedAmount,
								totalInterest,
								totalPv,
								untreatedAmount,
								totalInterest,
								new Date(),
								totalPv,
								untreatedAmount,
								totalInterest,
								Constant.PRODUCT_TYPE_04,
								Constant.TERM_INVEST_STATUS_ADVANCE_FINISH,
								Constant.TERM_INVEST_STATUS_FINISH} );
	}

	@Override
	public void saveTermSubAccount(Date now, BigDecimal totalPv,
			BigDecimal untreatedAmount, BigDecimal totalInterest) {
		
		StringBuffer sql=new StringBuffer()
		.append(" update bao_t_sub_account_info t  ")
		.append(" set t.account_total_value = t.account_total_value / (? + ?) * ?,  ")
		.append(" t.account_available_value = t.account_available_value / (? + ?) * ?, ")
		.append(" t.LAST_UPDATE_USER = ?, ")
		.append(" t.LAST_UPDATE_DATE = ?, ")
		.append(" t.VERSION = VERSION + 1 ")
		.append(" where t.relate_primary in ( ")
		.append("       select m.id ")
		.append("       from bao_t_invest_info m,  ")
		.append("            bao_t_product_info n,  ")
		.append("            bao_t_product_type_info q ")
		.append("       where m.product_id = n.id  ")
		.append("       and n.product_type = q.id  ")
		.append("       and q.type_name = ? ")
		.append("       and m.invest_status not in (?, ?) ")
		.append(" ) ");
		
		jdbcTemplate.update(sql.toString(), 
				new Object[]{	totalPv,
								untreatedAmount,
								totalInterest,
								totalPv,
								untreatedAmount,
								totalInterest,
								Constant.SYSTEM_USER_BACK,
								new Date(),
								Constant.PRODUCT_TYPE_04,
								Constant.TERM_INVEST_STATUS_ADVANCE_FINISH,
								Constant.TERM_INVEST_STATUS_FINISH} );
	}

	@Override
	public List<Map<String, Object>> findTermSubAccount(
			Map<String, Object> param) {
		StringBuffer sqlString = new StringBuffer()
		.append(" select t.ACCOUNT_ID \"accountId\", ")
		.append("        t.id \"id\", ")
		.append("        NVL(t.ACCOUNT_TOTAL_VALUE, 0) \"accountTotalValue\", ")
		.append("        t.SUB_ACCOUNT_NO \"subAccountNo\",  ")
		.append("        NVL(t.ACCOUNT_AVAILABLE_VALUE, 0) \"accountAvailableValue\", ")
		.append("        NVL(t.ACCOUNT_FREEZE_VALUE, 0) \"accountFreezeValue\", ")
		.append("        NVL(t.ACCOUNT_AMOUNT, 0) \"accountAmount\", ")
		.append("        t.CUST_ID \"custId\", ")
		.append("        t.RELATE_PRIMARY \"relatePrimary\", ")
		.append("        t.RELATE_TYPE \"relateType\", ")
		.append("        NVL(t.account_total_value / (? + ?) * ?, 0) \"factInterest\", ")
		.append("        t.VERSION \"version\" ")
		.append(" from bao_t_sub_account_info t  ")
		.append(" where t.relate_primary in ( ")
		.append("       select m.id ")
		.append("       from bao_t_invest_info m,  ")
		.append("            bao_t_product_info n,  ")
		.append("            bao_t_product_type_info q ")
		.append("       where m.product_id = n.id  ")
		.append("       and n.product_type = q.id  ")
		.append("       and q.type_name = ? ")
		.append("       and m.invest_status not in (?, ?)  ")
		.append("       and m.invest_date < ?  ")
		.append(" ) or id in (?, ?)");
		
		return repositoryUtil.queryForPage(sqlString.toString(), new Object[] {param.get("totalPv"), 
																				param.get("untreatedAmount"), 
																				param.get("totalInterest"), 
																				param.get("typeName"), 
																				Constant.TERM_INVEST_STATUS_ADVANCE_FINISH, 
																				Constant.TERM_INVEST_STATUS_FINISH, 
																				param.get("currDate"),
																				Constant.SUB_ACCOUNT_ID_CENTER_11, 
																				Constant.SUB_ACCOUNT_ID_ERAN_12}, Integer.valueOf(param.get("start").toString()), Integer.valueOf(param.get("length").toString()));
	}

	@Override
	public int countTermSubAccount(Map<String, Object> param) {
		StringBuffer sqlString = new StringBuffer()
		.append(" select count(1) \"total\" ")
		.append(" from bao_t_sub_account_info t  ")
		.append(" where t.relate_primary in ( ")
		.append("       select m.id ")
		.append("       from bao_t_invest_info m,  ")
		.append("            bao_t_product_info n,  ")
		.append("            bao_t_product_type_info q ")
		.append("       where m.product_id = n.id  ")
		.append("       and n.product_type = q.id  ")
		.append("       and q.type_name = ? ")
		.append("       and m.invest_status not in (?, ?)      ")
		.append("       and m.invest_date < ?  ")
		.append(" ) or id in (?, ?) ");
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[] {(String)param.get("typeName"), 
																										 Constant.TERM_INVEST_STATUS_ADVANCE_FINISH, 
																										 Constant.TERM_INVEST_STATUS_FINISH, 
																										 param.get("currDate"),
																										 Constant.SUB_ACCOUNT_ID_CENTER_11, 
																										 Constant.SUB_ACCOUNT_ID_ERAN_12});
		Map<String, Object> map = list.get(0);
		return Integer.valueOf(map.get("total").toString());
	}

	@Override
	public List<Map<String, Object>> findWaitRecoverAtone(
			Map<String, Object> param) {
		StringBuilder sqlString = new StringBuilder()
		.append(" select t.id \"subAccountId\", s.id \"investId\", t.account_total_value \"accountTotalValue\",  ")
		.append("        t.account_freeze_value \"accountFreezeValue\", t.account_available_value \"accountAvailableValue\", t.account_amount \"accountAmount\", ")
		.append("        t.version \"subAccountVersion\", t.cust_id \"custId\" ")
		.append("   from bao_t_sub_account_info t, bao_t_invest_info s ")
		.append("  where t.relate_primary = s.id ")
		.append("    and t.account_total_value < 0.01 ")
		.append("    and t.account_total_value != 0 ")
		.append("    and t.account_total_value = t.account_available_value ")
		.append("    and s.product_id = '1' ")
		.append("    and s.invest_status = '有效' ");
		
		return repositoryUtil.queryForMap(sqlString.toString(), null);
	}

	@Override
	public void batchUpdateInvest(final List<InvestInfoEntity> list) {
		StringBuilder sql = new StringBuilder()
		.append(" update bao_t_invest_info t ")
		.append(" set t.invest_status = ?, ")
		.append(" t.last_update_user = ?, ")
		.append(" t.last_update_date = ?, ")
		.append(" t.version = t.version + 1 ")
		.append(" where t.id = ? ");
		
		jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
                     ps.setString(1, list.get(i).getInvestStatus());
                     ps.setString(2, list.get(i).getLastUpdateUser());
                     ps.setTimestamp(3, new Timestamp(list.get(i).getLastUpdateDate().getTime()));
                     ps.setString(4, list.get(i).getId());
			}
			
			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

}
