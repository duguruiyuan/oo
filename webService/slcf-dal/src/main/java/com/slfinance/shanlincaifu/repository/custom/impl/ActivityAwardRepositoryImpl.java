package com.slfinance.shanlincaifu.repository.custom.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.ActivityAwardRepositoryCustom;

@Repository
public class ActivityAwardRepositoryImpl implements	ActivityAwardRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 获得红包信息列表
	 * @param params
	 * @return
	 */
	@Override
	public Page<Map<String, Object>> queryRedPackedList(
			Map<String, Object> params) {
		StringBuilder sqlString= new StringBuilder()
		.append("  select t.ID \"id\",t.award_name \"awardName\", ")
		.append("  t.award_type \"awardType\", t.grant_amount \"grantAmount\", ")
		.append("  t.start_amount \"startAmount\", t.start_time \"startTime\", ")
		.append("  t.deadline_time \"deadlineTime\", t.use_scope \"useScope\", ")
		.append("  t.loan_allotted_time \"loanAllottedTime\", t.is_transfer \"isTransfer\", ")
		.append("  t.award_status \"awardStatus\", ")
		.append("  t.memo \"memo\", ")
		.append("  t.seat_term \"seatTerm\", t.increase_unit \"increaseUnit\",t.subject_repayment_methods \"repaymentMethods\" ")
		.append("  from bao_t_activity_award t where t.record_status = '有效' ");
		
		List<Object> objList = new ArrayList<Object>();

		if (!StringUtils.isEmpty(params.get("awardType"))) {
			sqlString.append(" and t.award_type = ?");
			objList.add(params.get("awardType").toString());
		}

		if (!StringUtils.isEmpty(params.get("startTime"))) {
			sqlString.append(" and t.start_time >= TO_DATE(?,'YYYY/MM/DD')");
			objList.add(params.get("startTime").toString());
		}
		
		if (!StringUtils.isEmpty(params.get("deadlineTime"))) {
			sqlString.append(" and t.deadline_time <= TO_DATE(?,'YYYY/MM/DD')");
			objList.add(params.get("deadlineTime").toString());
		}
		
		if (!StringUtils.isEmpty(params.get("awardStatus"))) {
			sqlString.append(" and t.award_status = ?");
			objList.add(params.get("awardStatus").toString());
		}
		
		if (!StringUtils.isEmpty(params.get("awardName"))) {
			sqlString.append(" and t.award_name = ?");
			objList.add(params.get("awardName").toString());
		}
		
		sqlString.append("order by t.create_date desc");

		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), Integer.valueOf(params.get("start").toString()),
				Integer.valueOf(params.get("length").toString()));
	}

	/**
	 * 查询红包使用详情
	 * @param params
	 * @return
	 * @throws ParseException 
	 */
	@Override
	public Page<Map<String, Object>> queryRedPacketUseDetails(
			Map<String, Object> params) throws ParseException {
		
		StringBuilder sqlString= new StringBuilder()
		.append("  select aa.award_type \"redPacketType\",aa.grant_amount \"redPacketAmount\", ")
		.append("  (select ct.user_name from com_t_user ct where ct.id = ? ) \"grantUser\", ca.create_date \"grantTime\", ")
		.append(" to_char(ca.last_update_date,'yyyymmdd')  \"investTime\", ")
		.append("  ci.cust_name \"userName\", ")
		.append("  ci.mobile \"contactWay\", ")
		.append(" (select tl.loan_code from bao_t_loan_info tl where tl.id = ca.loan_id ) \"loanCode\", ")
		.append("  ca.invest_amount \"investAmount\" ")
		.append("  from bao_t_activity_award aa ")
		.append("  left join bao_t_cust_activity_info ca ")
		.append(" on aa.id = ca.activity_award_id ")
		.append(" left join bao_t_cust_info ci ")
		.append(" on ca.cust_id = ci.id ")
		.append(" where ")
		.append(" aa.record_status = '有效' and ca.record_status = '有效' and ca.trade_status = '全部使用' and  aa.id = ? and ca.reward_shape = ? ");
		
		List<Object> objList = new ArrayList<Object>();
		objList.add(params.get("custId").toString());
		objList.add(params.get("awardId").toString());
		objList.add(params.get("awardType").toString());
		if (!StringUtils.isEmpty(params.get("investTime"))) {
			sqlString.append(" and to_char(ca.last_update_date,'yyyymmdd') = ? ");
			objList.add(((String)params.get("investTime")).replaceAll("-",""));
		}
		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), Integer.valueOf(params.get("start").toString()),
				Integer.valueOf(params.get("length").toString()));
	}
	
	/**
	 * 查询红包使用总金额和张数
	 * @param params
	 * @return
	 */
	@Override
	public Map<String, Object> queryUsedData(Map<String, Object> params) {
		
		StringBuilder sqlString= new StringBuilder()
		.append(" select ");
		if (!"加息券".equals(params.get("awardType"))) {
			
			sqlString.append(" NVL(sum(ca.total_amount),0) \"usedAmount\", ");
		}
		sqlString.append(" count(ca.id) \"usedTotalNumber\" ")
		.append(" from bao_t_cust_activity_info ca ")
		.append(" left join bao_t_activity_award aa ")
		.append(" on ca.activity_award_id = aa.id ")
		.append(" where aa.record_status = '有效' and ca.record_status = '有效' and aa.id = ? and ca.reward_shape = ? and ca.trade_status = '全部使用' ");
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), 
				new Object[] { 
			(String)params.get("awardId"),(String)params.get("awardType")
		});
		Map<String, Object> map = new HashMap<String, Object>();
		if (null !=list && list.size() > 0) {
			map = list.get(0);
			//如果是加息券，使用总金额为0
			if ("加息券".equals(params.get("awardType"))) {
				map.put("usedAmount", "0");
			}
		}
		return map;
	}

	/**
	 * 查询红包发放总金额和张数
	 * @param params
	 * @return
	 */
	@Override
	public Map<String, Object> queryTotalData(Map<String, Object> params) {
		
		StringBuilder sqlString= new StringBuilder()
		.append("  select ");
		if (!"加息券".equals(params.get("awardType"))) {
			
			sqlString.append(" NVL(sum(ca.total_amount),0) \"totalgrantAmount\", ");
		}
		sqlString.append(" count(ca.id) \"totalNumber\" ")
		.append(" from bao_t_cust_activity_info ca ")
		.append(" left join bao_t_activity_award aa ")
		.append(" on ca.activity_award_id = aa.id ")
		.append(" where ca.record_status = '有效' and aa.record_status = '有效' and aa.id = ? and ca.reward_shape = ? ");
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), 
				new Object[] { 
			(String)params.get("awardId"),(String)params.get("awardType")
		});
		Map<String, Object> map = new HashMap<String, Object>();
		if (null !=list && list.size() > 0) {
			 map = list.get(0);
			 //如果是加息券，总金额为0
			 if ("加息券".equals(params.get("awardType"))) {
					map.put("totalgrantAmount", "0");
				}
		}
		return map;
	}

    @Override
    public List<Map<String, Object>> findByCustActivityId(String custActivityId) {
        StringBuilder sqlString = new StringBuilder()
                .append("SELECT A.* FROM BAO_T_CUST_ACTIVITY_INFO T ")
                .append(" INNER JOIN BAO_T_ACTIVITY_AWARD A ON T.ACTIVITY_AWARD_ID = A.ID ")
                .append(" WHERE T.ID = ? ")
                ;
        return jdbcTemplate.queryForList(sqlString.toString(), new Object[]{custActivityId});
    }

}
