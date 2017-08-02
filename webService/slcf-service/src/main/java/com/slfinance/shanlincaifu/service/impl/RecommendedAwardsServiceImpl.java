package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.TransAccountInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.RecommendedAwardsService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

@Service
@Slf4j
public class RecommendedAwardsServiceImpl implements RecommendedAwardsService {

	@Autowired
	private TransAccountInfoRepositoryCustom transAccountInfoRepositoryCustom;
	@Autowired
	private RepositoryUtil repositoryUtil;
	@Autowired
	private CustInfoRepository custInfoRepository;
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 查询数据
	 */
	public Map<String, Object> list(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = transAccountInfoRepositoryCustom.findTransAccountInfoEntityRecommendedAwards(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		result.put("totalInfo", transAccountInfoRepositoryCustom.findTransAccountInfoEntityRecommendedAwardsCount(param));
		return result;
	}


	/**
	 * 查询数据
	 */
	public Map<String, Object> findCustActivityDetailEntityById(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = transAccountInfoRepositoryCustom.findCustActivityDetailEntityById(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}
	
	
	/**
	 * 生成当月的优惠活动数据
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo grentRecommendedAwardsData(Map<String, Object> paramsMap) {
		synchronized (this) {
			try {
				log.info("======grentRecommendedAwardsData==============生产优惠活动数据开始");
				final Map<String, Object> paramsMaps = paramsMap;
				Calendar cal = Calendar.getInstance();
				int currentMonth =  cal.get(Calendar.MONTH) + 1;
				cal.add(Calendar.MONTH, -1);
				int month = cal.get(Calendar.MONTH) + 1;
				String queryMonth = String.format("%02d", month);
//				String countQuerySQL = "SELECT COUNT(1) as RECORDS FROM BAO_T_CUST_ACTIVITY_DETAIL TCAD WHERE TCAD.TRADE_STATUS='" + Constant.USER_ACTIVITY_TRADE_STATUS_06 + "' AND TO_CHAR(TCAD.CREATE_DATE, 'mm') = '" + queryMonth + "'";
//				List<Map<String, Object>> countMap= repositoryUtil.queryForMap(countQuerySQL, null);
//				BigDecimal countBg = (BigDecimal)countMap.get(0).get("RECORDS");
//				if(countBg.compareTo(new BigDecimal(1)) >=0)
//					return new ResultVo(false, queryMonth+"月份数据已生成");
				
				//查询 BAO_T_AUDIT_INFO 表的当月数据
				String currentMonthStr = String.format("%02d", currentMonth);
				String countQuerySQL = "SELECT COUNT(1) as RECORDS FROM BAO_T_AUDIT_INFO BTTAI WHERE TO_CHAR(BTTAI.CREATE_DATE, 'mm') = '"+currentMonthStr+"' AND BTTAI.APPLY_TYPE='"+Constant.TANS_ACCOUNT_TYPE_02+"'";
				List<Map<String, Object>> countMap= repositoryUtil.queryForMap(countQuerySQL, null);
				BigDecimal countBg = (BigDecimal)countMap.get(0).get("RECORDS");
				if(countBg.compareTo(new BigDecimal(1)) >=0)
					return new ResultVo(false, queryMonth+"月份数据已生成");
				
				final List<Map<String, Object>> listMapStore = new ArrayList<Map<String, Object>>();
				// 统计
				String querySQL = "SELECT SUM(TCAD.TRADE_AMOUNT) TRADEAMOUNT,TCAD.CUST_ID   FROM BAO_T_CUST_ACTIVITY_DETAIL TCAD WHERE TCAD.CUST_ACTIVITY_ID IN (SELECT ID FROM BAO_T_CUST_ACTIVITY_INFO TCAI WHERE TCAI.ACTIVITY_ID =(SELECT TAI.ID FROM BAO_T_ACTIVITY_INFO TAI WHERE TAI.ID = '2')) AND TCAD.TRADE_STATUS='" + Constant.USER_ACTIVITY_TRADE_STATUS_06 + "'   AND TO_CHAR(TCAD.CREATE_DATE, 'mm') = '" + queryMonth + "' GROUP BY TCAD.CUST_ID";
				final List<Map<String, Object>> listMap = repositoryUtil.queryForMap(querySQL, null);
				if (!StringUtils.isEmpty(listMap)) {
					for (Map<String, Object> param : listMap) {
						String custId = (String) param.get("CUST_ID");
						CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
						AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(custId);
						HashMap<String, Object> result = new HashMap<String, Object>();
						result.put("custName", custInfoEntity.getCustName());
						result.put("credentialsCode", custInfoEntity.getCredentialsCode());
						result.put("corporateAccount", accountInfoEntity.getAccountNo());
						result.put("adjustAmount", param.get("TRADEAMOUNT"));
						result.put("custId",custId);
						String sqlAccountCode = "SELECT T.ACCOUNT_NO FROM BAO_T_ACCOUNT_INFO T WHERE  T.CUST_ID= ?";
						List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlAccountCode, new Object[] { StringUtils.trimAllWhitespace(custInfoEntity.getId()) });
						if(!StringUtils.isEmpty(list) && list.size()>0){
							// 转让账号
							result.put("intoAccount", list.get(0).get("ACCOUNT_NO"));
						}
						// 转出账号
						String sqlCorporateAccount = "SELECT T.ACCOUNT_NO FROM BAO_T_ACCOUNT_INFO T WHERE T.CUST_ID = (SELECT TT.ID FROM BAO_T_CUST_INFO TT WHERE TT.LOGIN_NAME=? )";
						List<Map<String, Object>> listCorporateAccount = repositoryUtil.queryForMap(sqlCorporateAccount, new Object[] { StringUtils.trimAllWhitespace(Constant.ACCOUNT_TYPE_COMPANY) });
						if(!StringUtils.isEmpty(listCorporateAccount) && listCorporateAccount.size()>0){
							// 转让账号
							result.put("expendAccount", listCorporateAccount.get(0).get("ACCOUNT_NO"));
						}
						listMapStore.add(result);
					}


					// 转账表
					StringBuffer baotTransAccountInfoSQL = new StringBuffer();
					baotTransAccountInfoSQL.append("INSERT INTO BAO_T_TRANS_ACCOUNT_INFO(ID,INTO_ACCOUNT,EXPEND_ACCOUNT,TRADE_AMOUNT,TRANS_TYPE").append(",RECORD_STATUS,CREATE_USER,CREATE_DATE,LAST_UPDATE_USER,LAST_UPDATE_DATE,VERSION,MEMO)").append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
					jdbcTemplate.batchUpdate(baotTransAccountInfoSQL.toString(), new BatchPreparedStatementSetter() {
						@Override
						public void setValues(PreparedStatement ps, int i) throws SQLException {
							String taciId = UUID.randomUUID().toString();
							ps.setString(1, taciId);
							listMapStore.get(i).put("taciId", taciId);
							ps.setString(2, (String) listMapStore.get(i).get("intoAccount"));
							ps.setString(3, (String) listMapStore.get(i).get("expendAccount"));
							ps.setBigDecimal(4, (BigDecimal) listMapStore.get(i).get("adjustAmount"));
							ps.setString(5, Constant.TANS_ACCOUNT_TYPE_02);
							ps.setString(6, Constant.AUDIT_STATUS_REVIEWD);
							ps.setString(7, (String)paramsMaps.get("userOperName"));
							ps.setTimestamp(8, new Timestamp(new java.util.Date().getTime()));
							ps.setString(9, "");
							ps.setTimestamp(10, new Timestamp(new java.util.Date().getTime()));
							ps.setInt(11, Integer.valueOf(0));
							ps.setString(12, "");
						}

						@Override
						public int getBatchSize() {
							return listMapStore.size();
						}
					});

					// 审核表
					StringBuffer baotAuditInfoSQL = new StringBuffer();
					baotAuditInfoSQL.append("INSERT INTO BAO_T_AUDIT_INFO(ID,CUST_ID,RELATE_TYPE,RELATE_PRIMARY,APPLY_TYPE").append(",TRADE_AMOUNT,APPLY_TIME,AUDIT_STATUS,TRADE_STATUS,RECORD_STATUS,CREATE_USER,CREATE_DATE,VERSION,MEMO)").append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					jdbcTemplate.batchUpdate(baotAuditInfoSQL.toString(), new BatchPreparedStatementSetter() {
						@Override
						public void setValues(PreparedStatement ps, int i) throws SQLException {
							ps.setString(1, UUID.randomUUID().toString());
							ps.setString(2, (String) listMapStore.get(i).get("custId"));
							ps.setString(3, Constant.TABLE_BAO_T_TRANS_ACCOUNT_INFO);
							ps.setString(4, (String) listMapStore.get(i).get("taciId"));
							ps.setString(5, Constant.TANS_ACCOUNT_TYPE_02);
							ps.setBigDecimal(6, (BigDecimal) listMapStore.get(i).get("adjustAmount"));
							ps.setTimestamp(7, new Timestamp(new java.util.Date().getTime()));
							ps.setString(8, Constant.AUDIT_STATUS_REVIEWD);
							ps.setString(9, Constant.TRADE_STATUS_01);
							ps.setString(10, Constant.VALID_STATUS_VALID);
							ps.setString(11, "admin");
							ps.setTimestamp(12, new Timestamp(new java.util.Date().getTime()));
							ps.setInt(13, Integer.valueOf(0));
							ps.setString(14, "");
						}

						@Override
						public int getBatchSize() {
							return listMapStore.size();
						}
					});

				}
				log.info("======grentRecommendedAwardsData==============生产优惠活动数据结束");

			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}

		}
		return new ResultVo(true, "处理成功");
	}
	
	
	
}
