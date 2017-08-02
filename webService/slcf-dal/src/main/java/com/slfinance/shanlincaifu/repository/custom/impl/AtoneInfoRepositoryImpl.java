/** 
 * @(#)AtoneInfoRepositoryImpl.java 1.0.0 2015年5月1日 下午2:20:41  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AtoneDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.AtoneInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.repository.ProductInfoRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.AtoneInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.vo.ResultVo;

/**   
 * 赎回数据访问接口实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月1日 下午2:20:41 $ 
 */
@Repository
public class AtoneInfoRepositoryImpl implements AtoneInfoRepositoryCustom {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private ProductInfoRepository productInfoRepository;
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Override
	public ResultVo batchInsertAllotDetail(AtoneDetailInfoEntity atoneDetailInfoEntity) throws SLException {
		StringBuffer sqlString= new StringBuffer()
		.append(" INSERT INTO BAO_T_ATONE_DETAIL_INFO ")
		.append("  (ID, LOAN_ID, ATONE_ID, ATONE_AMOUNT, ATONE_SCALE, RECORD_STATUS, CREATE_USER, CREATE_DATE, LAST_UPDATE_USER, LAST_UPDATE_DATE, VERSION, MEMO) ")
		.append(" SELECT ")
		.append(" sys_guid(), A.LOAN_ID, ? , ?, ?, '', ?, ?, ?, ?, 0, '' ")
		.append(" FROM BAO_T_ALLOT_DETAIL_INFO A ")
		.append(" INNER JOIN BAO_T_ALLOT_INFO B ON B.ID = A.ALLOT_ID ")
		.append(" INNER JOIN BAO_T_PRODUCT_TYPE_INFO C ON C.ID = B.RELATE_PRIMARY ")
		.append(" INNER JOIN BAO_T_PRODUCT_INFO D ON D.PRODUCT_TYPE = C.ID ")
		.append(" INNER JOIN BAO_T_LOAN_DETAIL_INFO E ON E.LOAN_ID = A.LOAN_ID ")
		.append(" WHERE C.TYPE_NAME = ? AND B.ALLOT_STATUS IN ('已分配', '已使用') AND E.CREDIT_RIGHT_STATUS = '正常' ");
		
		jdbcTemplate.update(sqlString.toString(), 
				new Object[]{	atoneDetailInfoEntity.getAtoneId(), 
								atoneDetailInfoEntity.getAtoneAmount(),
								atoneDetailInfoEntity.getAtoneScale(),
								atoneDetailInfoEntity.getCreateUser(),
								atoneDetailInfoEntity.getCreateDate(),
								atoneDetailInfoEntity.getLastUpdateUser(),
								atoneDetailInfoEntity.getLastUpdateDate(),
								Constant.PRODUCT_TYPE_01});
		
		return new ResultVo(true);
	}

	@Override
	public Page<Map<String, Object>> findAllAtoneByCustId(
			Map<String, Object> param) {
		StringBuffer sqlString= new StringBuffer()
		.append(" SELECT T.ID \"id\", TO_CHAR(T.CLEANUP_DATE, 'YYYY-MM-DD') \"tradeDate\", T.ATONE_METHOD \"tradeType\",  ")
		.append("        T.ATONE_TOTAL_AMOUNT \"tradeAmount\", T.ATONE_EXPENSES \"atoneExpenses\",  ")
		.append("        T.ATONE_STATUS \"atoneStatus\", T.MEMO \"memo\" ")
		.append(" FROM BAO_T_ATONE_INFO T ")
		.append(" WHERE T.CUST_ID = ? AND T.PRODUCT_ID = ? ");
		
		List<Object> objList = new ArrayList<Object>();
		objList.add((String)param.get("custId"));
		ProductInfoEntity productInfoEntity = productInfoRepository.findProductInfoByProductTypeName((String)param.get("productType"));
		objList.add(productInfoEntity.getId());
		
		if(!StringUtils.isEmpty(param.get("opearteDateBegin"))){
			sqlString.append(" and CLEANUP_DATE >= ?");
			objList.add(DateUtils.parseStandardDate((String)param.get("opearteDateBegin")));
		}

		if(!StringUtils.isEmpty(param.get("opearteDateEnd"))){
			sqlString.append(" and CLEANUP_DATE <= ?");
			objList.add(DateUtils.getEndDate(DateUtils.parseStandardDate((String)param.get("opearteDateEnd"))));
		}
		
		if(!StringUtils.isEmpty(param.get("tradeType"))){
			sqlString.append(" and ATONE_METHOD = ?");
			objList.add(param.get("tradeType"));
		}
		
		if(!StringUtils.isEmpty(param.get("tradeStatus"))){
			sqlString.append(" and ATONE_STATUS = ?");
			objList.add(param.get("tradeStatus"));
		}
		
		sqlString.append(" ORDER BY T.CLEANUP_DATE DESC ");
		
		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), (int)param.get("start"), (int)param.get("length"));
	}

	/**
	 * 计算已经审核处理成功的已赎回金额
	 */
	public BigDecimal findSumAlreadyAtoneAmount(String tradeType,String custId)throws SLException{
		if(null == tradeType || null == custId)
			return BigDecimal.ZERO;
		StringBuffer sql = new StringBuffer(" SELECT NVL(SUM(Q.TRADE_AMOUNT),0) \"amount\" FROM BAO_T_ACCOUNT_FLOW_INFO Q  INNER JOIN BAO_T_CUST_INFO S ON Q.CUST_ID = S.ID");
		List<Object> objList=new ArrayList<Object>();
		
		//总账条件
		sql.append(" WHERE Q.ACCOUNT_TYPE = ? ");
		objList.add(Constant.ACCOUNT_TYPE_MAIN);
		
		//分账条件
		sql.append(" AND Q.ACCOUNT_ID NOT IN (?, ?, ?)");
		objList.add(Constant.ACCOUNT_ID_CENTER);
		objList.add(Constant.ACCOUNT_ID_ERAN);
		objList.add(Constant.ACCOUNT_ID_RISK);	
		
		//交易类型
		sql.append(" AND Q.TRADE_TYPE = ? ");
		objList.add(tradeType);	
		
		//用户id
		sql.append(" AND S.ID = ? ");
		objList.add(custId);
		List<Map<String, Object>> resultMap = repositoryUtil.queryForMap(sql.toString(), objList.toArray());
		BigDecimal sumAtoneAmount = BigDecimal.ZERO;
		if(resultMap != null && resultMap.size() > 0 && resultMap.get(0) != null )
			sumAtoneAmount = (BigDecimal)resultMap.get(0).get("amount");
		return sumAtoneAmount;
	}

	@Override
	public List<Map<String, Object>> findTermAtone(Map<String, Object> param) {
		
		StringBuffer sqlString = new StringBuffer()
		.append(" select t.id \"subAccountId\", t.cust_id \"custId\", t.account_id \"accountId\",  ")
		.append("        t.relate_type \"relateType\", t.relate_primary \"relatePrimary\", ")
		.append("        t.account_total_value \"accountTotalValue\", t.account_freeze_value \"accountFreezeValue\", t.account_available_value \"accountAvailableValue\", ")
		.append("        t.account_amount \"accountAmount\", p.id \"investId\", p.invest_amount \"investAmount\", p.product_id \"productId\", p.curr_term \"currTerm\", ")
		.append("        q.id \"atoneId\", q.atone_total_amount \"atoneTotalAmount\", ")
		.append("        q.atone_method \"atoneMethod\", q.atone_expenses \"atoneExpenses\", q.already_atone_amount \"alreadyAtoneAmount\", ")
		.append("        r.account_total_amount \"accountTotalAmount\", r.account_freeze_amount \"accountFreezeAmount\", r.account_available_amount \"accountAvailableAmount\", ")
		.append("        t.version \"subVersion\", p.version \"investVersion\", q.version \"atoneVersion\", r.version \"accountVersion\", s.mobile \"mobile\", ")
		.append("        to_char(p.create_date, 'yyyy-mm-dd') \"investDate\", a.product_name \"productName\" ")
		.append(" from bao_t_sub_account_info t, bao_t_invest_info p, bao_t_atone_info q, bao_t_account_info r, bao_t_cust_info s, bao_t_product_info a, bao_t_product_type_info b ")
		.append(" where t.relate_primary = p.id and p.id = q.invest_id and t.account_id = r.id and t.cust_id = s.id and q.product_id = a.id and a.product_type = b.id and b.type_name = ? ")
		//.append(" and q.product_id in (select a.id from bao_t_product_info a, bao_t_product_type_info b where a.product_type = b.id and b.type_name = ?) ")
		.append(" and q.atone_status = ? and t.account_total_value = 0 ")
		.append(" order by q.create_date asc, p.create_date asc ");
		
		return repositoryUtil.queryForPage(sqlString.toString(), new Object[]{ param.get("typeName"), Constant.TRADE_STATUS_01 }, (int)param.get("start"), (int)param.get("length"));
	}

	@Override
	public int countTermAtone(Map<String, Object> param) {
		
		StringBuffer sqlString = new StringBuffer()
		.append(" select count(1) \"total\" ")
		.append(" from bao_t_sub_account_info t, bao_t_invest_info p, bao_t_atone_info q  ")
		.append(" where t.relate_primary = p.id and p.id = q.invest_id  ")
		.append(" and q.product_id in (select a.id from bao_t_product_info a, bao_t_product_type_info b where a.product_type = b.id and b.type_name = ?) ")
		.append(" and q.atone_status = ? and t.account_total_value = 0 ")
		.append(" order by q.create_date asc, p.create_date asc ");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[] {(String)param.get("typeName"), Constant.TRADE_STATUS_01});
		Map<String, Object> map = list.get(0);
		return Integer.valueOf(map.get("total").toString());
	}

	@Override
	public void batchUpdateAtone(final List<AtoneInfoEntity> list) {
		StringBuffer sql=new StringBuffer();
		sql.append("UPDATE BAO_T_ATONE_INFO SET ALREADY_ATONE_AMOUNT = ?, ATONE_STATUS = ?, VERSION = VERSION + 1, LAST_UPDATE_USER = ?, LAST_UPDATE_DATE = ? WHERE ID = ? AND VERSION = ? ");
	    jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
                     ps.setBigDecimal(1, list.get(i).getAlreadyAtoneAmount());
                     ps.setString(2, list.get(i).getAtoneStatus());
                     ps.setString(3, list.get(i).getLastUpdateUser());                  
                     ps.setTimestamp(4, new Timestamp(list.get(i).getCreateDate().getTime()));                    
                     ps.setString(5,  list.get(i).getId());
                     ps.setInt(6, list.get(i).getVersion());
			}
			
			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public void batchUpdateInvest(final List<InvestInfoEntity> list) {
		StringBuffer sql=new StringBuffer();
		sql.append("UPDATE BAO_T_INVEST_INFO SET INVEST_STATUS = ?, VERSION = VERSION + 1, LAST_UPDATE_USER = ?, LAST_UPDATE_DATE = ? WHERE ID = ? AND VERSION = ? ");
	    jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
                     ps.setString(1, list.get(i).getInvestStatus());
                     ps.setString(2, list.get(i).getLastUpdateUser());                  
                     ps.setTimestamp(3, new Timestamp(list.get(i).getCreateDate().getTime()));                    
                     ps.setString(4,  list.get(i).getId());
                     ps.setInt(5, list.get(i).getVersion());
			}
			
			@Override
			public int getBatchSize() {
				return list.size();
			}
		});		
	}

	@Override
	public void batchUpdateSubAccount(final List<SubAccountInfoEntity> list) {
		StringBuffer sql=new StringBuffer();
		sql.append("UPDATE BAO_T_SUB_ACCOUNT_INFO SET ACCOUNT_TOTAL_VALUE = ?, ACCOUNT_FREEZE_VALUE = ?, ACCOUNT_AVAILABLE_VALUE = ?, ACCOUNT_AMOUNT = ?, VERSION = VERSION + 1, LAST_UPDATE_USER = ?, LAST_UPDATE_DATE = ? WHERE ID = ? AND VERSION = ? ");
	    jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
                     ps.setBigDecimal(1, list.get(i).getAccountTotalValue());
                     ps.setBigDecimal(2, list.get(i).getAccountFreezeValue());
                     ps.setBigDecimal(3, list.get(i).getAccountAvailableValue());
                     ps.setBigDecimal(4, list.get(i).getAccountAmount());
                     ps.setString(5, list.get(i).getLastUpdateUser());                  
                     ps.setTimestamp(6, new Timestamp(list.get(i).getCreateDate().getTime()));                    
                     ps.setString(7,  list.get(i).getId());
                     ps.setInt(8, list.get(i).getVersion());
			}
			
			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
		
	}

	@Override
	public void batchUpdateAccount(final List<AccountInfoEntity> list) {
		StringBuffer sql=new StringBuffer();
		sql.append("UPDATE BAO_T_ACCOUNT_INFO SET ACCOUNT_TOTAL_AMOUNT = ?, ACCOUNT_FREEZE_AMOUNT = ?, ACCOUNT_AVAILABLE_AMOUNT = ?, VERSION = VERSION + 1, LAST_UPDATE_USER = ?, LAST_UPDATE_DATE = ? WHERE ID = ? AND VERSION = ? ");
	    jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
                     ps.setBigDecimal(1, list.get(i).getAccountTotalAmount());
                     ps.setBigDecimal(2, list.get(i).getAccountFreezeAmount());
                     ps.setBigDecimal(3, list.get(i).getAccountAvailableAmount());
                     ps.setString(4, list.get(i).getLastUpdateUser());                  
                     ps.setTimestamp(5, new Timestamp(list.get(i).getCreateDate().getTime()));                    
                     ps.setString(6,  list.get(i).getId());
                     ps.setInt(7, list.get(i).getVersion());
			}
			
			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
		
	}
	
}
