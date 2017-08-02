/** 
 * @(#)WealthHoldInfoRepositoryImpl.java 1.0.0 2016年2月23日 下午7:28:07  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.common.collect.Lists;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.WealthHoldInfoEntity;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.WealthHoldInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.SqlCondition;

/**   
 * 自定义持有价值数据访问实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年2月23日 下午7:28:07 $ 
 */
public class WealthHoldInfoRepositoryImpl implements
		WealthHoldInfoRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<Map<String, Object>> findUserHoldPv(
			Map<String, Object> params) {
		
		StringBuilder sqlString = new StringBuilder()
		.append("   select decode(nvl(m.repayment_status, '未还款'), '未还款', s.value_repayment_before, s.value_repayment_after) \"pv\",  ")
		.append("          t.hold_scale*decode(nvl(m.repayment_status, '未还款'), '未还款', s.value_repayment_before, s.value_repayment_after) \"holdPv\", ")
		.append("          t.id \"id\", t.invest_id \"investId\", t.sub_account_id \"subAccountId\",  ")
		.append("          t.cust_id \"custId\", t.loan_id \"loanId\", t.hold_scale \"holdScale\",  ")
		.append("          t.hold_amount \"holdAmount\", t.except_amount \"exceptAmount\", t.received_amount \"receivedAmount\",  ")
		.append("          t.hold_status \"holdStatus\", t.hold_source \"holdSource\", t.is_center \"isCenter\",  ")
		.append("          t.record_status \"recordStatus\", t.create_user \"createUser\", t.create_date \"createDate\", t.last_update_user \"lastUpdateUser\",  ")
		.append("          t.last_update_date \"lastUpdateDate\", t.version \"version\", t.memo  \"memo\", ")
		.append("          q.loan_code \"loanCode\" ")
		.append("  from bao_t_wealth_hold_info t " )
		.append("  inner join bao_t_credit_right_value s  on s.loan_id = t.loan_id and s.value_date = ? ")
		.append("  inner join bao_t_loan_info q on q.id = t.loan_id ")
		.append("  left  join bao_t_repayment_plan_info m on m.loan_id = t.loan_id and expect_repayment_date = ? ")
		.append("  where 1 = 1 ");
		
		List<Object> objList = Lists.newArrayList();
		objList.add((String)params.get("valueDate"));
		objList.add((String)params.get("valueDate"));
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, params, objList);
		sqlCondition.addString("investId", "t.invest_id")
					.addString("valueDate", "s.value_date")
					.addList("holdStatusList", "t.hold_status");
		
		return repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
	}

	@Override
	public List<Map<String, Object>> findCenterHoldPv(Map<String, Object> params) {
		StringBuilder sqlString = new StringBuilder()
		.append("   select decode(nvl(m.repayment_status, '未还款'), '未还款', s.value_repayment_before, s.value_repayment_after) \"pv\",  ")
		.append("          t.hold_scale*decode(nvl(m.repayment_status, '未还款'), '未还款', s.value_repayment_before, s.value_repayment_after) \"holdPv\", ")
		.append("          t.id \"id\", t.invest_id \"investId\", t.sub_account_id \"subAccountId\",  ")
		.append("          t.cust_id \"custId\", t.loan_id \"loanId\", t.hold_scale \"holdScale\",  ")
		.append("          t.hold_amount \"holdAmount\", t.except_amount \"exceptAmount\", t.received_amount \"receivedAmount\",  ")
		.append("          t.hold_status \"holdStatus\", t.hold_source \"holdSource\", t.is_center \"isCenter\",  ")
		.append("          t.record_status \"recordStatus\", t.create_user \"createUser\", t.create_date \"createDate\", t.last_update_user \"lastUpdateUser\",  ")
		.append("          t.last_update_date \"lastUpdateDate\", t.version \"version\", t.memo  \"memo\" ")
		.append("  from bao_t_wealth_hold_info t " )
		.append("  inner join bao_t_credit_right_value s  on s.loan_id = t.loan_id and s.value_date = ? ")
		.append("  left  join bao_t_repayment_plan_info m on m.loan_id = t.loan_id and expect_repayment_date = ? ")
		.append("  where 1 = 1 ");
		
		List<Object> objList = Lists.newArrayList();
		objList.add((String)params.get("valueDate"));
		objList.add((String)params.get("valueDate"));
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, params, objList);
		sqlCondition.addList("loanList", "t.loan_id")
					.addString("valueDate", "s.value_date")
					.addString("isCenter", "t.is_center");
		
		return repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
	}

	@Override
	public void batchUpdate(final List<WealthHoldInfoEntity> wealthHoldList) throws SLException{
		StringBuilder sql=new StringBuilder()
		.append(" update bao_t_wealth_hold_info t  ")
		.append(" set t.hold_scale = ?,  ")
		.append(" t.hold_status = ?, ")
		.append(" t.last_update_user = ?, ")
		.append(" t.last_update_date = ?, ")
		.append(" t.version = version + 1 ")
		.append(" where id = ? and version = ? ");
		
		int[] rows = jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
                     ps.setBigDecimal(1, wealthHoldList.get(i).getHoldScale());
                     ps.setString(2, wealthHoldList.get(i).getHoldStatus());
                     ps.setString(3, wealthHoldList.get(i).getLastUpdateUser());
                     ps.setTimestamp(4, new Timestamp(wealthHoldList.get(i).getLastUpdateDate().getTime()));
                     ps.setString(5, wealthHoldList.get(i).getId());
                     ps.setInt(6, wealthHoldList.get(i).getVersion());
			}
			
			@Override
			public int getBatchSize() {
				return wealthHoldList.size();
			}
		});
		
		if(rows.length != wealthHoldList.size()) {
			throw new SLException("存在未执行成功的记录");
		}
		
		for(int i : rows) {
			if(i == 0) {
				throw new SLException("存在未执行成功的记录");
			}
		}
		
	}

	@Override
	public void batchLoanTransfer(Map<String, Object> params) throws SLException {
			
		// Step-2) 批量插入债权转让数据
		StringBuilder sql = new StringBuilder()
		.append(" insert into bao_t_loan_transfer ")
		.append("   (id, sender_hold_id, receive_hold_id,  ")
		.append("    sender_cust_id, receive_cust_id, sender_loan_id,  ")
		.append("    receive_loan_id, trade_amount, trade_scale,  ")
		.append("    transfer_expenses, record_status, create_user,  ")
		.append("    create_date, last_update_user, last_update_date,  ")
		.append("    version, memo, trade_no, request_no) ")
		.append(" select  ")
		.append("   sys_guid(), t.id, s.id,  ")
		.append("    t.cust_id, s.cust_id, t.loan_id,  ")
		.append("    s.loan_id, t.hold_scale*decode(nvl(m.repayment_status, '未还款'), '未还款', n.value_repayment_before, n.value_repayment_after), t.hold_scale,  ")
		.append("    0, '有效', ?, ")
		.append("    ?, ?, ?,  ")
		.append("    0, '赎回债权', 'SLCF_' || TO_CHAR(SYSDATE, 'yyyyMMdd') || lpad(TO_CHAR(LOAN_TRANSFER_NO_SEQ.NEXTVAL), 12, '0'), ? ")
		.append("  from bao_t_wealth_hold_info t " )
		.append("  inner join bao_t_wealth_hold_info s on s.loan_id = t.loan_id and s.is_center = '是' ")
		.append("  inner join bao_t_credit_right_value n on n.loan_id = t.loan_id and n.value_date = ? ")
		.append("  left  join bao_t_repayment_plan_info m on m.loan_id = t.loan_id and expect_repayment_date = ? ")
		.append("    where t.invest_id = ? and t.hold_status in ('持有中', '待转让') ");
		
		int rows = jdbcTemplate.update(sql.toString(), 
				new Object[]{ (String)params.get("lastUpdateUser"),
							  (Date)params.get("lastUpdateDate"),
							  (String)params.get("lastUpdateUser"),
							  (Date)params.get("lastUpdateDate"),
							  (String)params.get("requestNo"),
							  (String)params.get("valueDate"),
							  (String)params.get("valueDate"),
							  (String)params.get("investId")} );
		if(rows <= 0) {
			throw new SLException("插入债权转让信息失败");
		}
		
		// Step-1) 批量插入历史持有债权情况
		sql=new StringBuilder()
		.append(" insert into bao_t_wealth_hold_history_info ")
		.append("   (id, hold_id, invest_id,  ")
		.append("    sub_account_id, cust_id, loan_id,  ")
		.append("    hold_scale, hold_amount, except_amount,  ")
		.append("    received_amount, hold_status, hold_source,  ")
		.append("    is_center, old_create_user, old_create_date,  ")
		.append("    old_last_update_user, old_last_update_date, old_memo,  ")
		.append("    record_status, create_user, create_date,  ")
		.append("    last_update_user, last_update_date, version, memo, ")
		.append("    trade_no, request_no) ")
		.append(" select ")
		.append("   sys_guid(), t.id, t.invest_id,  ")
		.append("   t.sub_account_id, t.cust_id, t.loan_id,  ")
		.append("   t.hold_scale, t.hold_amount, t.except_amount,  ")
		.append("   t.received_amount, t.hold_status, t.hold_source,  ")
		.append("   t.is_center, t.create_user, t.create_date,  ")
		.append("   t.last_update_user, t.last_update_date, t.memo,  ")
		.append("   '有效', ?, ?,  ")
		.append("   ?, ?, 0, '', ")
		.append("   p.trade_no, p.request_no ")
		.append("   from bao_t_wealth_hold_info t ")
		.append("   inner join bao_t_loan_transfer p on p.sender_hold_id = t.id and p.request_no = ? ")
		.append("   where t.invest_id = ? and t.hold_status in ('持有中', '待转让') ")
		.append(" union all ")
		.append(" select ")
		.append("   sys_guid(), t.id, t.invest_id,  ")
		.append("   t.sub_account_id, t.cust_id, t.loan_id,  ")
		.append("   t.hold_scale, t.hold_amount, t.except_amount,  ")
		.append("   t.received_amount, t.hold_status, t.hold_source,  ")
		.append("   t.is_center, t.create_user, t.create_date,  ")
		.append("   t.last_update_user, t.last_update_date, t.memo,  ")
		.append("   '有效', ?, ?,  ")
		.append("   ?, ?, 0, '', ")
		.append("   p.trade_no, p.request_no ")
		.append("   from bao_t_wealth_hold_info t ")
		.append("   inner join bao_t_loan_transfer p on p.receive_hold_id = t.id and p.request_no = ? ")
		.append("   where t.is_center = '是'  ")
		.append("   and t.loan_id in ( ")
		.append("         select loan_id ")
		.append("         from bao_t_wealth_hold_info s ")
		.append("         where s.invest_id = ? and s.hold_status in ('持有中', '待转让') ")
		.append("   ) ");
		
		rows = jdbcTemplate.update(sql.toString(), 
				new Object[]{ (String)params.get("lastUpdateUser"),
							  (Date)params.get("lastUpdateDate"),
							  (String)params.get("lastUpdateUser"),
							  (Date)params.get("lastUpdateDate"),
							  (String)params.get("requestNo"),
							  (String)params.get("investId"),
							  (String)params.get("lastUpdateUser"),
							  (Date)params.get("lastUpdateDate"),
							  (String)params.get("lastUpdateUser"),
							  (Date)params.get("lastUpdateDate"),
							  (String)params.get("requestNo"),
							  (String)params.get("investId")} );
		if(rows <= 0) {
			throw new SLException("插入历史持有债权信息失败");
		}
		
		// Step-3) 批量更新公司债权持有情况（将持有人的债权转让给公司）
		sql = new StringBuilder()
		.append(" update bao_t_wealth_hold_info s ")
		.append(" set (s.hold_scale, s.last_update_user, s.last_update_date, s.hold_status)  ")
		.append(" = ( ")
		.append("     select s.hold_scale + t.hold_scale, ?, ?, '持有中' ")
		.append("     from bao_t_wealth_hold_info t ")
		.append("     where t.loan_id = s.loan_id ")
		.append("     and t.invest_id = ? and t.hold_status in ('持有中', '待转让') ")
		.append(" ) ")
		.append(" where s.is_center = '是' ")
		.append(" and exists ( ")
		.append("     select hold_scale ")
		.append("     from bao_t_wealth_hold_info t ")
		.append("     where t.loan_id = s.loan_id ")
		.append("     and t.invest_id = ? and t.hold_status in ('持有中', '待转让') ")
		.append(" ) ");
		
		rows = jdbcTemplate.update(sql.toString(), 
				new Object[]{ (String)params.get("lastUpdateUser"),
			  				  (Date)params.get("lastUpdateDate"),
							  (String)params.get("investId"),
							  (String)params.get("investId")} );
		if(rows <= 0) {
			throw new SLException("插入债权转让信息失败");
		}
		
		// Step-4) 更新用户持有债权情况
		sql = new StringBuilder()
		.append(" update bao_t_wealth_hold_info t ")
		.append(" set t.hold_scale = 0, ")
		.append("     t.hold_status = '已转让', ")
		.append("     t.last_update_user = ?, ")
		.append("     t.last_update_date = ? ")
		.append(" where t.invest_id = ? and t.hold_status in ('持有中', '待转让') ");
		
		rows = jdbcTemplate.update(sql.toString(), 
				new Object[]{ (String)params.get("lastUpdateUser"),
			  				  (Date)params.get("lastUpdateDate"),
							  (String)params.get("investId")} );
		if(rows <= 0) {
			throw new SLException("插入债权转让信息失败");
		}
	}

	@Override
	public BigDecimal sumUserHoldPv(Map<String, Object> params) {
		StringBuilder sqlString = new StringBuilder()
		.append("   select sum(trunc(t.hold_scale*decode(nvl(m.repayment_status, '未还款'), '未还款', s.value_repayment_before, s.value_repayment_after), 2)) \"holdTotalPv\"  ")
		.append("  from bao_t_wealth_hold_info t " )
		.append("  inner join bao_t_credit_right_value s  on s.loan_id = t.loan_id and s.value_date = ? ")
		.append("  left  join bao_t_repayment_plan_info m on m.loan_id = t.loan_id and expect_repayment_date = ? ")
		.append("  where 1 = 1 ");
		
		List<Object> objList = Lists.newArrayList();
		objList.add((String)params.get("valueDate"));
		objList.add((String)params.get("valueDate"));
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, params, objList);
		sqlCondition.addString("investId", "t.invest_id")
					.addString("valueDate", "s.value_date")
					.addList("holdStatusList", "t.hold_status");
		
		List<Map<String, Object>> result = repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
		if(result == null || result.size() == 0) {
			return BigDecimal.ZERO;
		}
		if(result.get(0).get("holdTotalPv") == null) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(result.get(0).get("holdTotalPv").toString());
	}

	@Override
	public Page<Map<String, Object>> queryCanUserLoan(Map<String, Object> params) throws SLException {
		StringBuffer SqlString = new StringBuffer()
				.append("  select l.loan_code       \"loanCode\",  ")
				.append("         substr(lc.cust_name, 0, 1) || '**'     \"loanCustName\",  ")
				.append("         l.loan_desc       \"loanDesc\",  ")
				.append("         l.loan_amount     \"loanAmount\",  ")
				.append("         p.parameter_name  \"assetTypeCode\",  ")
				.append("         l.id              \"loanId\" ")
				.append("  from  bao_t_wealth_hold_info h   ")
				.append("  inner join bao_t_loan_info l on l.id = h.loan_id  ")
				.append("  inner join bao_t_loan_cust_info lc on l.relate_primary = lc.id  ")
				.append("  inner join com_t_param p on p.value = l.debt_source_code and p.type = 'baoLoanSource' ")
				.append("  where h.is_center = '是'  ")
				.append("    AND h.HOLD_STATUS = '持有中'  ")
				.append("  order by l.invest_start_date desc, l.loan_code desc   ");
		return repositoryUtil.queryForPageMap(SqlString.toString(), new Object[] {}, 
				Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}

	@Override
	public Page<Map<String, Object>> queryPageAlreadyAutoLoanByWealthId(Map<String, Object> params) throws SLException {
		StringBuffer SqlString = new StringBuffer()
				.append("  select l.loan_code       \"loanCode\",  ")
				.append("         substr(lc.cust_name, 0, 1) || '**'     \"loanCustName\",  ")
				.append("         l.loan_desc       \"loanDesc\",  ")
				.append("         l.loan_amount     \"loanAmount\",  ")
				.append("         p.parameter_name  \"assetTypeCode\",  ")
				.append("       l.id              \"loanId\" ")
				.append("    from bao_t_loan_info l   ")
				.append("    inner join bao_t_loan_cust_info lc on l.relate_primary = lc.id  ")
				.append("    inner join com_t_param p on p.value = l.debt_source_code and p.type = 'baoLoanSource' ")
				.append("   where exists (  ")
				.append("         select * from bao_t_invest_info i, bao_t_wealth_hold_info h   ")
				.append("         where i.wealth_id = ? and h.invest_id = i.id and l.id = h.loan_id  ")
				.append("   )  ")
				.append("  order by l.invest_start_date desc, l.loan_code desc   ");
		
		List<Object> objList = Lists.newArrayList();
		objList.add((String)params.get("wealthId"));
		return repositoryUtil.queryForPageMap(SqlString.toString(), objList.toArray(), 
				Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}

}
