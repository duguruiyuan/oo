package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.repository.AuditInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.TransAccountInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SqlCondition;
import com.slfinance.vo.ResultVo;

@Repository
public class TransAccountInfoRepositoryCustomImpl implements
		TransAccountInfoRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	
	@Autowired
	private AuditInfoRepository auditInfoRepository;

	@Override
	public Page<Map<String, Object>> findTransAccountInfoEntity(
			Map<String, Object> param) {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT BCI.LOGIN_NAME,BCI.CUST_NAME,BCI.CREDENTIALS_CODE,BTAI.TRANS_TYPE,BTAI.TRADE_AMOUNT,");
		sb.append("BTAI.CREATE_USER,BAI.AUDIT_USER,BAI.AUDIT_TIME,BAI.TRADE_STATUS,");
		sb.append("BAI.AUDIT_STATUS,BTAI.MEMO,BTAI.CREATE_DATE,BTAI.ID FROM BAO_T_CUST_INFO BCI,BAO_T_AUDIT_INFO BAI,BAO_T_TRANS_ACCOUNT_INFO BTAI ");
		sb.append("WHERE BAI.RELATE_PRIMARY = BTAI.ID AND BAI.CUST_ID = BCI.ID AND BTAI.TRANS_TYPE !='"+Constant.TANS_ACCOUNT_TYPE_02+"'");
		sb.append(" AND (BCI.CUST_KIND IS NULL OR BCI.CUST_KIND = '网站用户') ");

		// 客户姓名
		if (!StringUtils.isEmpty(param.get("loginName"))) {
			String loginName = (String) param.get("loginName");
			sb.append(" AND BCI.CUST_NAME=? ");
			listObject.add(loginName);
		}
		// 证件号码
		if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
			String credentialsCode = (String) param.get("credentialsCode");
			sb.append(" AND BCI.CREDENTIALS_CODE=? ");
			listObject.add(credentialsCode);
		}
//		// createTime
//		if (!StringUtils.isEmpty(param.get("createTime"))) {
//			String createTime = (String) param.get("createTime");
//			sb.append(" AND  to_char(BTAI.CREATE_DATE,'YYYY-MM-DD') =? ");
//			listObject.add(createTime);
//		}
		
		if(!StringUtils.isEmpty(param.get("startCreateTime"))){
			sb.append(" and to_char(BTAI.CREATE_DATE,'YYYY-MM-DD') >= ?");
			listObject.add((String)param.get("startCreateTime"));
		}

		if(!StringUtils.isEmpty(param.get("endCreateTime"))){
			sb.append(" and to_char(BTAI.CREATE_DATE,'YYYY-MM-DD') <= ?");
			listObject.add((String)param.get("endCreateTime"));
		}		
				
		
		// trans_type
		if (!StringUtils.isEmpty(param.get("tradeType"))) {
			String tradeType = (String) param.get("tradeType");
			sb.append(" AND  BTAI.TRANS_TYPE =? ");
			listObject.add(tradeType);
		}
		if (!StringUtils.isEmpty(param.get("tradeStatus"))) {
			String tradeStatus = (String) param.get("tradeStatus");
			sb.append(" AND  BAI.TRADE_STATUS =? ");
			listObject.add(tradeStatus);
		}
		if (!StringUtils.isEmpty(param.get("atoneStatus"))) {
			String atoneStatus = (String) param.get("atoneStatus");
			sb.append(" AND  BAI.AUDIT_STATUS =? ");
			listObject.add(atoneStatus);
		}

		sb.append(" ORDER BY BTAI.CREATE_DATE DESC");

		int pageNum = Integer.valueOf(param.get("start") + "");
		int pageSize = Integer.valueOf(param.get("length") + "");

		return repositoryUtil.queryForPageMap(sb.toString(),
				listObject.toArray(), pageNum, pageSize);
	}

	@Override
	public Page<Map<String, Object>> findTransAccountInfoEntityRecommendedAwards(
			Map<String, Object> param) {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT BCI.LOGIN_NAME,BCI.CUST_NAME,BCI.CREDENTIALS_CODE,BTAI.TRANS_TYPE,BTAI.TRADE_AMOUNT,BCI.ID AS TJID,");
		sb.append(" BTAI.CREATE_USER,BAI.AUDIT_USER,BAI.AUDIT_TIME,BAI.TRADE_STATUS,");
		sb.append(" BAI.AUDIT_STATUS,BTAI.MEMO,BTAI.CREATE_DATE,BTAI.ID FROM BAO_T_CUST_INFO BCI,BAO_T_AUDIT_INFO BAI,BAO_T_TRANS_ACCOUNT_INFO BTAI ");
		sb.append(" WHERE BAI.RELATE_PRIMARY = BTAI.ID AND BAI.CUST_ID = BCI.ID ");
		sb.append(" AND  BTAI.TRANS_TYPE ='")
				.append(Constant.TANS_ACCOUNT_TYPE_02).append("' ");

		// 客户姓名
		if (!StringUtils.isEmpty(param.get("loginName"))) {
			String loginName = (String) param.get("loginName");
			sb.append(" AND BCI.CUST_NAME=? ");
			listObject.add(loginName);
		}
		// 证件号码
		if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
			String credentialsCode = (String) param.get("credentialsCode");
			sb.append(" AND BCI.CREDENTIALS_CODE=? ");
			listObject.add(credentialsCode);
		}
		// createTime
//		if (!StringUtils.isEmpty(param.get("createTime"))) {
//			String createTime = (String) param.get("createTime");
//			sb.append(" AND  to_char(BTAI.CREATE_DATE,'YYYY-MM-DD') =? ");
//			listObject.add(createTime);
//		}
//		

//		if(!StringUtils.isEmpty(param.get("startCreateTime"))){
//			sb.append(" and to_char(BTAI.CREATE_DATE,'YYYY-MM-DD') >= ?");
//			listObject.add((String)param.get("startCreateTime"));
//		}
//
//		if(!StringUtils.isEmpty(param.get("endCreateTime"))){
//			sb.append(" and to_char(BTAI.CREATE_DATE,'YYYY-MM-DD') <= ?");
//			listObject.add((String)param.get("endCreateTime"));
//		}		
//		
//		
//
//		if(!StringUtils.isEmpty(param.get("startAuditTime"))){
//			sb.append(" and to_char(BAI.LAST_UPDATE_DATE,'YYYY-MM-DD') >= ?");
//			listObject.add((String)param.get("startAuditTime"));
//		}
//
//		if(!StringUtils.isEmpty(param.get("endAuditTime"))){
//			sb.append(" and to_char(BAI.LAST_UPDATE_DATE,'YYYY-MM-DD') <= ?");
//			listObject.add((String)param.get("endAuditTime"));
//		}		
		
		
		if(!StringUtils.isEmpty(param.get("startCreateTime"))){
			sb.append(" and to_char(BCI.CREATE_DATE,'YYYY-MM-DD') >= ?");
			listObject.add((String)param.get("startCreateTime"));
		}

		if(!StringUtils.isEmpty(param.get("endCreateTime"))){
			sb.append(" and to_char(BCI.CREATE_DATE,'YYYY-MM-DD') <= ?");
			listObject.add((String)param.get("endCreateTime"));
		}		
		
		

		if(!StringUtils.isEmpty(param.get("startAuditTime"))){
			sb.append(" and to_char(BTAI.CREATE_DATE,'YYYY-MM-DD') >= ?");
			listObject.add((String)param.get("startAuditTime"));
		}

		if(!StringUtils.isEmpty(param.get("endAuditTime"))){
			sb.append(" and to_char(BTAI.CREATE_DATE,'YYYY-MM-DD') <= ?");
			listObject.add((String)param.get("endAuditTime"));
		}			
		
		
		
//		
//		if (!StringUtils.isEmpty(param.get("auditTime"))) {
//			String createTime = (String) param.get("auditTime");
//			sb.append(" AND  to_char(BAI.LAST_UPDATE_DATE,'YYYY-MM-DD') =? ");
//			listObject.add(createTime);
//		}

		if (!StringUtils.isEmpty(param.get("tradeStatus"))) {
			String tradeStatus = (String) param.get("tradeStatus");
			sb.append(" AND  BAI.TRADE_STATUS =? ");
			listObject.add(tradeStatus);
		}
		if (!StringUtils.isEmpty(param.get("atoneStatus"))) {
			String atoneStatus = (String) param.get("atoneStatus");
			sb.append(" AND  BAI.AUDIT_STATUS =? ");
			listObject.add(atoneStatus);
		}

		sb.append(" ORDER BY BTAI.CREATE_DATE DESC");

		int pageNum = Integer.valueOf(param.get("start") + "");
		int pageSize = Integer.valueOf(param.get("length") + "");
		return repositoryUtil.queryForPageMap(sb.toString(),
				listObject.toArray(), pageNum, pageSize);
	}

	public Map<String, Object> findTransAccountInfoEntityRecommendedAwardsCount(
			Map<String, Object> param) {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT sum(BTAI.TRADE_AMOUNT) as TRADE_AMOUNT");
		sb.append("   FROM BAO_T_CUST_INFO BCI,BAO_T_AUDIT_INFO BAI,BAO_T_TRANS_ACCOUNT_INFO BTAI ");
		sb.append(" WHERE BAI.RELATE_PRIMARY = BTAI.ID AND BAI.CUST_ID = BCI.ID ");
		sb.append(" AND  BTAI.TRANS_TYPE ='")
				.append(Constant.TANS_ACCOUNT_TYPE_02).append("' ");

		// 客户姓名
		if (!StringUtils.isEmpty(param.get("loginName"))) {
			String loginName = (String) param.get("loginName");
			sb.append(" AND BCI.CUST_NAME=? ");
			listObject.add(loginName);
		}
		// 证件号码
		if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
			String credentialsCode = (String) param.get("credentialsCode");
			sb.append(" AND BCI.CREDENTIALS_CODE=? ");
			listObject.add(credentialsCode);
		}
		

		if(!StringUtils.isEmpty(param.get("startCreateTime"))){
			sb.append(" and to_char(BCI.CREATE_DATE,'YYYY-MM-DD') >= ?");
			listObject.add((String)param.get("startCreateTime"));
		}

		if(!StringUtils.isEmpty(param.get("endCreateTime"))){
			sb.append(" and to_char(BCI.CREATE_DATE,'YYYY-MM-DD') <= ?");
			listObject.add((String)param.get("endCreateTime"));
		}		
		
		

		if(!StringUtils.isEmpty(param.get("startAuditTime"))){
			sb.append(" and to_char(BTAI.CREATE_DATE,'YYYY-MM-DD') >= ?");
			listObject.add((String)param.get("startAuditTime"));
		}

		if(!StringUtils.isEmpty(param.get("endAuditTime"))){
			sb.append(" and to_char(BTAI.CREATE_DATE,'YYYY-MM-DD') <= ?");
			listObject.add((String)param.get("endAuditTime"));
		}			
		

		if (!StringUtils.isEmpty(param.get("tradeStatus"))) {
			String tradeStatus = (String) param.get("tradeStatus");
			sb.append(" AND  BAI.TRADE_STATUS =? ");
			listObject.add(tradeStatus);
		}
		if (!StringUtils.isEmpty(param.get("atoneStatus"))) {
			String atoneStatus = (String) param.get("atoneStatus");
			sb.append(" AND  BAI.AUDIT_STATUS =? ");
			listObject.add(atoneStatus);
		}
		return repositoryUtil.queryForMap(sb.toString(), listObject.toArray())
				.get(0);
	}

	/**
	 * 用户查询明细
	 */
	public Page<Map<String, Object>> findCustActivityDetailEntityById(
			Map<String, Object> param) {
		List<Object> listObject = new ArrayList<Object>();

		// 用户ID
		String custId = (String) param.get("userIdDetail");
		// 注册日期
		String regDateStart = (String) param.get("regDateStart");
		String regDateEnd = (String) param.get("regDateEnd");
		// 投资日期
		String investDateStart = (String) param.get("investDateStart");
		String investDateEnd = (String) param.get("investDateEnd");
		// 阶层
		String userLevel = (String) param.get("userLevel");
		// 是否实名认证
		String credentialsStatus = (String) param.get("credentialsStatus");
		// 是否投资
		String investStatus = (String) param.get("investStatus");
		// 是否结算
		String tradeStatus = (String) param.get("tradeStatus");

		StringBuffer sb = new StringBuffer(
				"SELECT T.TJCUSTNAME,T.ID,T.TRADEAMOUNT,T.BTJCUSTNAME,T.REGDATE,T.INVESTDATE,T.CREDENTIALSSTATUS,T.INVESTSTATUS,T.USERLEVEL,T.TRADE_STATUS FROM (");
		sb.append("SELECT A.CUST_NAME AS TJCUSTNAME,B.TRADE_AMOUNT AS TRADEAMOUNT,");
		sb.append("B.CUST_NAME AS BTJCUSTNAME,B.REG_DATE AS REGDATE,A.ID,");
		sb.append("B.INVEST_DATE AS INVESTDATE,DECODE(B.CREDENTIALS_CODE, '', '否', '是') AS CREDENTIALSSTATUS,");
		sb.append("DECODE(B.CUST_ID, '', '否', '是') AS INVESTSTATUS,B.TRADE_STATUS,");
		sb.append("TO_NUMBER(NVL(B.SPREAD_LEVEL, 0)) - TO_NUMBER(NVL(A.SPREAD_LEVEL, 0)) AS USERLEVEL FROM (");
		sb.append("SELECT TJ.ID,TJ.CUST_NAME,TJ.SPREAD_LEVEL,TJ.INVITE_ORIGIN_ID FROM BAO_T_CUST_INFO TJ WHERE TJ.ID IN (SELECT BJT.INVITE_ORIGIN_ID FROM BAO_T_CUST_INFO BJT WHERE BJT.INVITE_ORIGIN_ID != '0')) A");
		sb.append("  LEFT JOIN ");
		sb.append("(SELECT CACTD.CUST_ID, CUST.CUST_NAME,CUST.CREATE_DATE REG_DATE,CACT.CREATE_DATE INVEST_DATE,CACTD.TRADE_AMOUNT,CACTD.TRADE_STATUS,CUST.CREDENTIALS_CODE,CUST.SPREAD_LEVEL");
		sb.append(" FROM BAO_T_CUST_INFO            CUST,BAO_T_CUST_ACTIVITY_INFO   CACT,BAO_T_CUST_ACTIVITY_DETAIL CACTD ");
		sb.append(" WHERE CUST.ID = CACT.CUST_ID AND CACT.ID = CACTD.CUST_ACTIVITY_ID  AND CACT.ACTIVITY_ID = '2') B ON A.ID = B.CUST_ID ");
		sb.append(" WHERE TO_NUMBER(NVL(B.SPREAD_LEVEL, 0)) - TO_NUMBER(NVL(A.SPREAD_LEVEL, 0)) <= 8) T  WHERE 1=1 ");

		if (!StringUtils.isEmpty(custId)) {
			sb.append(" AND T.ID = ? ");
			listObject.add(custId);
		}
		if (!StringUtils.isEmpty(regDateStart)) {
			sb.append(" AND to_char(T.REGDATE,'YYYY-MM-DD') >=? ");
			listObject.add(regDateStart);

		}
		if (!StringUtils.isEmpty(regDateEnd)) {
			sb.append(" AND to_char(T.REGDATE,'YYYY-MM-DD') <=? ");
			listObject.add(regDateEnd);
		}

		if (!StringUtils.isEmpty(investDateStart)) {
			sb.append(" AND to_char(T.INVESTDATE,'YYYY-MM-DD') >=? ");
			listObject.add(investDateStart);

		}
		if (!StringUtils.isEmpty(investDateEnd)) {
			sb.append(" AND to_char(T.INVESTDATE,'YYYY-MM-DD') <=? ");
			listObject.add(investDateEnd);
		}
		if (!StringUtils.isEmpty(userLevel)) {
			sb.append(" AND T.userLevel=? ");
			listObject.add(userLevel);
		}

		if (!StringUtils.isEmpty(credentialsStatus)) {
			sb.append(" AND T.CREDENTIALSSTATUS=? ");
			listObject.add(credentialsStatus);
		}

		if (!StringUtils.isEmpty(investStatus)) {
			sb.append(" AND T.INVESTSTATUS=? ");
			listObject.add(investStatus);
		}
		if (!StringUtils.isEmpty(tradeStatus)) {
			sb.append(" AND T.TRADE_STATUS=? ");
			listObject.add(tradeStatus);
		}

		int pageNum = Integer.valueOf(param.get("start") + "");
		int pageSize = Integer.valueOf(param.get("length") + "");
		return repositoryUtil.queryForPageMap(sb.toString(),
				listObject.toArray(), pageNum, pageSize);

	}

	@Override
	public Page<Map<String, Object>> findCompanyTransAccountList(
			Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
		.append(" select a.* from (")
		.append("  select bttai.id           \"transId\", ")
		.append("         btci.login_name    \"companyName\", ")
		.append("         btci.id		     \"companyId\", ")
		.append("         btci.cust_kind     \"projectType\", ")
		.append("         btci.tel           \"telephone\", ")
		.append("         bttai.trade_amount \"transAmount\", ")
		.append("         bttai.trans_type   \"transType\", ")
		.append("         bttai.audit_status \"auditStatus\", ")
		.append("         bttai.create_date  \"createDate\", ")
		.append("         ctu.user_name      \"createUser\", ")
		.append("         ctu2.user_name    \"auditUser\" ")
		.append("    from bao_t_trans_account_info bttai, ")
		.append("         bao_t_cust_info          btci, ")
		.append("         bao_t_audit_info         btai, ")
		.append("         bao_t_account_info       btacc, ")
		.append("         com_t_user               ctu,")
		.append("         com_t_user               ctu2")
		.append("   where bttai.id = btai.relate_primary(+) ")
		.append(" and (case when bttai.trans_type='"+Constant.ACCOUNT_TRANS_DIRECTION_IN+"' then bttai.into_account else '' end = btacc.account_no")
		.append(" or ")
		.append(" case when bttai.trans_type='"+Constant.ACCOUNT_TRANS_DIRECTION_OUT+"' then bttai.expend_account else '' end=btacc.account_no)")
		.append(" and btacc.cust_id = btci.id ")
		.append(" and bttai.create_user = ctu.id(+) ")
		.append(" and btai.audit_user  = ctu2.id(+) %s")
		.append(" and bttai.audit_status = '待审核' order by bttai.create_date) a")
		.append(" union all")
		.append(" select b.* from (")
		.append("  select bttai.id           \"transId\", ")
		.append("         btci.login_name    \"companyName\", ")
		.append("         btci.id		     \"companyId\", ")
		.append("         btci.cust_kind     \"projectType\", ")
		.append("         btci.tel           \"telephone\", ")
		.append("         bttai.trade_amount \"transAmount\", ")
		.append("         bttai.trans_type   \"transType\", ")
		.append("         bttai.audit_status \"auditStatus\", ")
		.append("         bttai.create_date  \"createDate\", ")
		.append("         ctu.user_name      \"createUser\", ")
		.append("         ctu2.user_name    \"auditUser\" ")
		.append("    from bao_t_trans_account_info bttai, ")
		.append("         bao_t_cust_info          btci, ")
		.append("         bao_t_audit_info         btai, ")
		.append("         bao_t_account_info       btacc, ")
		.append("         com_t_user               ctu,")
		.append("         com_t_user               ctu2")
		.append("   where bttai.id = btai.relate_primary(+) ")
		.append(" and (case when bttai.trans_type='"+Constant.ACCOUNT_TRANS_DIRECTION_IN+"' then bttai.into_account else '' end = btacc.account_no")
		.append(" or ")
		.append(" case when bttai.trans_type='"+Constant.ACCOUNT_TRANS_DIRECTION_OUT+"' then bttai.expend_account else '' end=btacc.account_no)")
		.append(" and btacc.cust_id = btci.id ")
		.append(" and bttai.create_user = ctu.id(+) ")
		.append(" and btai.audit_user  = ctu2.id(+) %s")
		.append(" and bttai.audit_status <> '待审核' order by bttai.create_date desc) b");
		
		
		StringBuilder sqlString = new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(sqlString, param);
		sqlCondition.addString("projectType", "btci.cust_kind")
					.addString("companyId", "btci.id")
					.addString("transType", "bttai.trans_type")
					.addString("auditStatus", "bttai.audit_status")
					.addList("projectTypeList", "btci.cust_kind");
		
		List<Object> objList = Lists.newArrayList();
		objList.addAll(sqlCondition.getObjectList());
		objList.addAll(sqlCondition.getObjectList());				
		return repositoryUtil.queryForPageMap(
				String.format(sql.toString(), sqlCondition.toString(), sqlCondition.toString()), 
				objList.toArray(), 
				Integer.parseInt(param.get("start").toString()), 
				Integer.parseInt(param.get("length").toString()));
	}

	@Override
	public ResultVo findCompanyTransAccountById(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
		.append(" select bttai.id                      \"transId\", ")
		.append("        btci.login_name               \"companyName\", ")
		.append("        btci.id                       \"companyId\", ")
		.append("        btci.cust_kind                \"projectType\", ")
		.append("        btai.account_available_amount \"accountAvailableAmount\", ")
		.append("        bttai.trade_amount            \"transAmount\", ")
		.append("        bttai.trans_type              \"transType\", ")
		.append("        bttai.memo                    \"transMemo\" ")
		.append("   from bao_t_trans_account_info bttai, ")
		.append("        bao_t_account_info       btai, ")
		.append("        bao_t_cust_info          btci ")
		.append("  where btai.cust_id = btci.id ")
		.append("    and (case when bttai.trans_type = '"+Constant.ACCOUNT_TRANS_DIRECTION_IN+"' then bttai.into_account else '' end = btai.account_no ")
		.append("        or ")
		.append("     case when bttai.trans_type = '"+Constant.ACCOUNT_TRANS_DIRECTION_OUT+"' then bttai.expend_account else '' end = btai.account_no)");
		
		String transId = (String) param.get("transId");
		if (!StringUtils.isEmpty(transId)) {
			sql.append(" and bttai.id = ? ");
			listObject.add(transId);
		}
		
		List<Map<String, Object>> result = repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
		if(result.size() > 0){
			resultMap = result.get(0);
		}
		
		AuditInfoEntity auditInfoEntity = auditInfoRepository.findAuditInfoEntityByRelatePrimary(transId);
		if(auditInfoEntity != null){
			List<Map<String, Object>> logInfoList = logInfoEntityRepository.findLogInfoByRelatePrimaryAndLogType(auditInfoEntity.getId(), Constant.LOG_TYPE_AUDIT);
			resultMap.put("logInfoList", logInfoList);
		}
		
		return new ResultVo(true, "查看调账信息成功", resultMap);
	}
}
