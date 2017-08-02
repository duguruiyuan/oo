/** 
 * @(#)WithdrawCashRepositoryImpl.java 1.0.0 2015年4月28日 上午10:51:43  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.WithdrawCashRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.PageFuns;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.shanlincaifu.vo.WithDrawCashStatisticsVO;
import com.slfinance.shanlincaifu.vo.WithDrawCashVO;

/**   
 * 提现相关业务
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月28日 上午10:51:43 $ 
 */
@Repository
public class WithdrawCashRepositoryImpl implements WithdrawCashRepositoryCustom {
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**提现管理查询返回字段**/
	private final String pageColumn = " AUD.ID,CUS.ID AS CUST_ID,CUS.CUST_NAME,CUS.LOGIN_NAME AS NICK_NAME, CUS.CUST_CODE,CUS.CREDENTIALS_CODE,CUS.CREDENTIALS_TYPE,PRO.TRADE_NO,NVL(PRO.TRADE_TYPE,'提现') TRADE_TYPE,PRO.BANK_NAME BANKNAME,TRUNC(AUD.TRADE_AMOUNT,2) TRADE_AMOUNT,TRUNC(PRO.TRADE_EXPENSES,2) WITHDRAW_EXPENSES,( NVL(PRO.TRADE_AMOUNT, 0) - NVL(PRO.TRADE_EXPENSES,0) ) REAL_TRADE_AMOUNT,AUD.TRADE_STATUS,AUD.CREATE_DATE AS　WITHDRAW_TIME,AUD.AUDIT_STATUS ";

	/**提现管理统计信息字段**/
	private final String statisticsColumn = " NVL(SUM(NVL(PRO.TRADE_AMOUNT, 0)),0) \"tradeAmountCount\", SUM( NVL(PRO.TRADE_AMOUNT, 0) - NVL(PRO.TRADE_EXPENSES, 0)) \"realTradeAmountCount\" ,NVL(SUM(NVL(PRO.TRADE_EXPENSES, 0)),0) \"tradeCostCount\" ";
	
	/**提现查询关联表**/
	private final String table = " FROM BAO_T_AUDIT_INFO AUD LEFT JOIN BAO_T_TRADE_FLOW_INFO PRO ON AUD.ID = PRO.RELATE_PRIMARY INNER JOIN BAO_T_CUST_INFO CUS ON CUS.ID = AUD.CUST_ID ";
	
	/**倒叙语句**/
	private final String orderDescSql = "ORDER BY decode(AUD.trade_status, '未处理', 1, '处理中', 2, '处理成功', 3, 4) asc, AUD.CREATE_DATE DESC";
	
	/**提现审核日志查询字段**/
	private final String logColumn = " B.ID,B.APPLY_TIME,B.TRADE_AMOUNT,B.AUDIT_TIME,U.USER_NAME AS AUDIT_USER, B.AUDIT_STATUS,B.MEMO ";
	
	/**提现审核日志查询关联表**/
	private final String logTable = " FROM BAO_T_AUDIT_INFO B LEFT JOIN COM_T_USER U ON B.AUDIT_USER = U.ID LEFT JOIN BAO_T_LOG_INFO L ON L.RELATE_PRIMARY = U.ID ";
	
	@Override
	public Page<WithDrawCashVO> findAllWithdrawCashList(Map<String, Object> paramsMap) throws SLException {
		StringBuilder condition = new StringBuilder();
		List<Object> objList = whereObj(paramsMap, condition);
		StringBuffer pageSql = new StringBuffer(" SELECT ").append( pageColumn ).append( table ).append(condition).append(orderDescSql);
		Page<WithDrawCashVO> page = repositoryUtil.queryForPage(pageSql.toString(), objList.toArray(), (int)paramsMap.get("pageNum"), (int)paramsMap.get("pageSize"),WithDrawCashVO.class);
		return page;
	}
	
	@Override
	public WithDrawCashStatisticsVO findAllWithdrawCashSum(Map<String, Object> paramsMap) throws SLException{
		StringBuilder condition = new StringBuilder();
		List<Object> objList = whereObj(paramsMap, condition);
		StringBuffer statisticsSql = new StringBuffer(" SELECT ").append( statisticsColumn ).append( table ).append(condition);
		List<WithDrawCashStatisticsVO> list = repositoryUtil.queryForList(statisticsSql.toString(), objList.toArray(), WithDrawCashStatisticsVO.class);
		if( list != null && list.size() > 0)
			return list.get(0);
		return new WithDrawCashStatisticsVO();
	}
	
	@Override
	public WithDrawCashVO findWithdrawalCashDetailInfo(Map<String, Object> paramsMap) throws SLException {
		StringBuilder condition = new StringBuilder();
		String otherColumn = "PRO.BANK_CARD_NO BANK_CARD_NUMBER,PRO.BRANCH_BANK_NAME BANK_BRANCH_NAME,AUD.MEMO,AUD.VERSION";
		StringBuffer sql = new StringBuffer(" SELECT ").append( pageColumn +"," + otherColumn ).append( table );
		List<Object> objList=new ArrayList<>(); 
		//提现ID
		if(!StringUtils.isEmpty(paramsMap.get("id"))){
			PageFuns.buildWhereSql(condition).append(" AUD.ID=? ");
			objList.add(paramsMap.get("id"));
		}
		PageFuns.buildWhereSql(condition).append(" ROWNUM=? ");
		objList.add(1);
		List<WithDrawCashVO> list =  repositoryUtil.queryForList(sql.append(condition).toString(), objList.toArray(), WithDrawCashVO.class);
		if( list != null && list.size() > 0)
			return list.get(0);
		return new WithDrawCashVO();
	}
	
	public List<AuditInfoEntity> findByCustIdOrId(Map<String,Object> paramsMap) throws SLException{
		StringBuilder condition = new StringBuilder();
		StringBuffer sql = new StringBuffer(" SELECT ").append( logColumn ).append( logTable );
		List<Object> objList=new ArrayList<>(); 
		//用户
		if(!StringUtils.isEmpty((String)paramsMap.get("custId"))){
			PageFuns.buildWhereSql(condition).append(" B.CUST_ID=? ");
			objList.add((String)paramsMap.get("custId"));
		}
		//提现审核ID
		if(!StringUtils.isEmpty((String)paramsMap.get("id"))){
			PageFuns.buildWhereSql(condition).append(" B.ID=? ");
			objList.add((String)paramsMap.get("id"));
		}
		if(!StringUtils.isEmpty((String)paramsMap.get("relatePrimary"))){
			PageFuns.buildWhereSql(condition).append(" B.RELATE_PRIMARY=? ");
			objList.add((String)paramsMap.get("relatePrimary"));
		}
		//申请类型默认是提现记录
		if(StringUtils.isEmpty((String)paramsMap.get("applyType"))){
			PageFuns.buildWhereSql(condition).append(" B.APPLY_TYPE=? ");
			objList.add(SubjectConstant.APPLY_TYPE_WITHDRAW);
		}
		//赎回活期宝或者善林财富处理
		if(!StringUtils.isEmpty((String)paramsMap.get("applyType")) 
				&& !SubjectConstant.APPLY_TYPE_WITHDRAW.equals((String)paramsMap.get("applyType"))
				&& !"赎回活期宝".equals(paramsMap.get("applyType"))){
			String applyType = (String)paramsMap.get("applyType");
			PageFuns.buildWhereSql(condition).append(" B.APPLY_TYPE=? ");
			objList.add(applyType);
		}
		//默认查询有效的记录
		PageFuns.buildWhereSql(condition).append(" B.RECORD_STATUS=? ");
		objList.add(Constant.VALID_STATUS_VALID);
		
		return repositoryUtil.queryForList(sql.append(condition).append("ORDER BY B.CREATE_DATE DESC").toString(), objList.toArray(), AuditInfoEntity.class);
	}
	
	public Page<AuditInfoEntity> findPageByCustIdOrId(Map<String,Object> paramsMap) throws SLException{
		StringBuilder condition = new StringBuilder();
		StringBuffer sql = new StringBuffer(" SELECT ").append( logColumn ).append( logTable );
		List<Object> objList=new ArrayList<>(); 
		//用户
		if(!StringUtils.isEmpty((String)paramsMap.get("custId"))){
			PageFuns.buildWhereSql(condition).append(" B.CUST_ID=? ");
			objList.add((String)paramsMap.get("custId"));
		}
		//提现审核ID
		if(!StringUtils.isEmpty((String)paramsMap.get("id"))){
			PageFuns.buildWhereSql(condition).append(" B.ID=? ");
			objList.add((String)paramsMap.get("id"));
		}
		if(!StringUtils.isEmpty((String)paramsMap.get("relatePrimary"))){
			PageFuns.buildWhereSql(condition).append(" B.RELATE_PRIMARY=? ");
			objList.add((String)paramsMap.get("relatePrimary"));
		}
		//申请类型默认是提现记录
		if(StringUtils.isEmpty((String)paramsMap.get("applyType"))){
			PageFuns.buildWhereSql(condition).append(" B.APPLY_TYPE=? ");
			objList.add(SubjectConstant.APPLY_TYPE_WITHDRAW);
		}
		//赎回活期宝或者善林财富处理
		if(!StringUtils.isEmpty((String)paramsMap.get("applyType")) 
				&& !SubjectConstant.APPLY_TYPE_WITHDRAW.equals((String)paramsMap.get("applyType"))
				&& !"赎回活期宝".equals(paramsMap.get("applyType"))){
			String applyType = (String)paramsMap.get("applyType");
			PageFuns.buildWhereSql(condition).append(" B.APPLY_TYPE=? ");
			objList.add(applyType);
		}
		//默认查询有效的记录
		PageFuns.buildWhereSql(condition).append(" B.RECORD_STATUS=? ");
		objList.add(Constant.VALID_STATUS_VALID);
		
		Page<AuditInfoEntity> page = repositoryUtil.queryForPage(sql.append(condition).append("ORDER BY B.CREATE_DATE DESC").toString(), objList.toArray(), (int)paramsMap.get("pageNum"), (int)paramsMap.get("pageSize"),AuditInfoEntity.class);
		return page;
	}	
	
//----------私有方法区域--------------------------------------------------------------------------------------------------------------------------------	
	
	private List<Object> whereObj(Map<String, Object> paramsMap, final StringBuilder condition) {
		List<Object> objList = new ArrayList<Object>();
		
		//用户
		if(!StringUtils.isEmpty(paramsMap.get("custName"))){
			PageFuns.buildWhereSql(condition).append(" CUS.CUST_NAME=? ");
			objList.add(paramsMap.get("custName"));
		}
		//用户昵称
		if(!StringUtils.isEmpty(paramsMap.get("nickName"))){
			PageFuns.buildWhereSql(condition).append(" CUS.LOGIN_NAME LIKE ? ");
			objList.add("%"+paramsMap.get("nickName")+ "%");
		}
		//订单编号：
		if(!StringUtils.isEmpty(paramsMap.get("tradeNo"))){
			PageFuns.buildWhereSql(condition).append(" PRO.TRADE_NO=? ");
			objList.add(paramsMap.get("tradeNo"));
		}
		//交易状态
		if(!StringUtils.isEmpty(paramsMap.get("tradeStatus"))){
			PageFuns.buildWhereSql(condition).append(" AUD.TRADE_STATUS=? ");
			objList.add(paramsMap.get("tradeStatus"));
		}
		//审核状态AUD.AUDIT_STATUS
		if(!StringUtils.isEmpty(paramsMap.get("auditStatus"))){
			PageFuns.buildWhereSql(condition).append(" AUD.AUDIT_STATUS=? ");
		
			objList.add(paramsMap.get("auditStatus"));
		}
		//证件号码 
		if(!StringUtils.isEmpty(paramsMap.get("credentialsCode"))){
			PageFuns.buildWhereSql(condition).append(" CUS.CREDENTIALS_CODE=? ");
			objList.add(paramsMap.get("credentialsCode"));
		}
		//操作时间[开始时间]
		if(!StringUtils.isEmpty(paramsMap.get("startDate"))){
			PageFuns.buildWhereSql(condition).append(" TRUNC(AUD.CREATE_DATE) >= TO_DATE(?,'yyyy-MM-dd') ");
			objList.add(paramsMap.get("startDate"));
		}
		//操作时间[结束时间]
		if(!StringUtils.isEmpty(paramsMap.get("endDate"))){
			PageFuns.buildWhereSql(condition).append(" TRUNC(AUD.CREATE_DATE) <= TO_DATE(?,'yyyy-MM-dd') ");
			objList.add(paramsMap.get("endDate"));
		}
		//申请类型是提现的操作
		PageFuns.buildWhereSql(condition).append(" AUD.APPLY_TYPE = ? ");
		objList.add(Constant.OPERATION_TYPE_06);
		//默认查询有效的记录
//		PageFuns.buildWhereSql(condition).append(" BANK.RECORD_STATUS = ? ");
//		objList.add(Constant.VALID_STATUS_VALID);
		return objList;
	}

	@Override
	public int updateWithDrawCashRecord(Map<String,Object> paramMap){
		String id = paramMap.get("id").toString();
		String auditStatus = paramMap.get("auditStatus").toString();
		StringBuffer sql=new StringBuffer();
		sql.append("UPDATE BAO_T_AUDIT_INFO T SET T.VERSION=T.VERSION WHERE T.ID=").append("'").append(id).append("'")
			.append(" AND T.AUDIT_STATUS=").append("'").append(auditStatus).append("'");
		int rows = jdbcTemplate.update(sql.toString());
		return rows;
	}

	@Override
	public Page<Map<String, Object>> findAllWithdrawAuditList(
			Map<String, Object> params)  {
		StringBuffer sqlString= new StringBuffer()
		.append(" select a.id \"id\",t.trade_no \"tradeNo\",c.cust_name \"custName\",c.login_name \"loginName\", ")
		.append(" a.trade_amount \"tradeAmount\",a.audit_status \"auditStatus\",a.memo \"memo\",a.apply_time \"applyTime\" ")
		.append(" from bao_t_audit_info a left join bao_t_trade_flow_info t on a.id = t.relate_primary inner join bao_t_cust_info c on c.id = a.cust_id ")
		.append(" where a.apply_type = '活动金额提现' ");
		List<Object> objList = new ArrayList<Object>();
		if(!StringUtils.isEmpty(params.get("custName"))){
			sqlString.append(" and c.cust_name=? ");
			objList.add(params.get("custName"));
		}
		//用户昵称
		if(!StringUtils.isEmpty(params.get("loginName"))){
			sqlString.append(" and c.login_name like ? ");
			objList.add("%"+params.get("loginName")+ "%");
		}
		//审核状态AUD.AUDIT_STATUS
		if(!StringUtils.isEmpty(params.get("auditStatus"))){
			sqlString.append(" and a.audit_status=? ");
			objList.add(params.get("auditStatus"));
		}
		//操作时间[开始时间]
		if(!StringUtils.isEmpty(params.get("startDate"))){
			sqlString.append(" and trunc(a.apply_time) >= to_date(?,'yyyy-MM-dd') ");
			objList.add(params.get("startDate"));
		}
		//操作时间[结束时间]
		if(!StringUtils.isEmpty(params.get("endDate"))){
			sqlString.append(" and trunc(a.apply_time) <= to_date(?,'yyyy-MM-dd') ");
			objList.add(params.get("endDate"));
		}

		sqlString.append(" order by decode(a.audit_status, '审核中', 1) asc, a.apply_time desc ");
		Page<Map<String, Object>> data =  repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), (int)params.get("start"), (int)params.get("length"));
		return data;
	
	}
}
