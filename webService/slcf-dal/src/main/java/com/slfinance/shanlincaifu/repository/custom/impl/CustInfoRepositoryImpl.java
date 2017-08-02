package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.CustInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SqlCondition;
import com.slfinance.vo.ResultVo;

@Repository
public class CustInfoRepositoryImpl implements CustInfoRepositoryCustom {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private RepositoryUtil repositoryUtil;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public BigDecimal getOwnerAmount(Map<String, Object> params) {
		String sql = "select sum(subaccount.account_total_value) from bao_t_cust_info cust,bao_t_sub_account_info subaccount,bao_t_invest_info invest,bao_t_product_info product,bao_t_product_type_info protype"
				+ " where cust.id=invest.cust_id and invest.product_id=product.id and product.product_type=protype.id and protype.type_name=? "
				+ " and invest.id=subaccount.relate_primary"
				+ " and subaccount.relate_type='"
				+ Constant.TABLE_BAO_T_INVEST_INFO
				+ "' and subaccount.record_status='有效' and cust.id=?";
		Object obj = entityManager.createNativeQuery(sql)
				.setParameter(1, params.get("productName"))
				.setParameter(2, params.get("custId")).getSingleResult();
		if (obj == null) {
			return BigDecimal.ZERO;
		} else {
			return new BigDecimal(obj + "");
		}
	}

	/**
	 * 用户定期宝产品可购买额度
	 */
	public BigDecimal getUserHoldAmount(String custId, String productId)
			throws SLException {
		// String
		// sql="select sum(subaccount.account_total_value) from bao_t_cust_info cust,bao_t_sub_account_info subaccount,bao_t_invest_info invest,bao_t_product_info product "+
		// " where product.id = ? and cust.id=? and cust.id=invest.cust_id and invest.product_id=product.id and invest.id=subaccount.relate_primary "+
		// " and subaccount.relate_type='"+Constant.TABLE_BAO_T_INVEST_INFO+"' and subaccount.record_status='有效' ";
		String date = DateUtils.formatDate(Calendar.getInstance().getTime(),
				"yyyyMMdd");

		String sql = "select sum(btii.invest_amount) from bao_t_invest_info  btii where btii.PRODUCT_ID= ? and btii.CUST_ID = ?"
				+ " and btii.invest_date='"
				+ date
				+ "' and btii.record_status='有效' ";

		Object obj = entityManager.createNativeQuery(sql)
				.setParameter(1, productId).setParameter(2, custId)
				.getSingleResult();
		if (obj == null) {
			return BigDecimal.ZERO;
		} else {
			return new BigDecimal(obj + "");
		}
	}

	public Page<Map<String, Object>> findOwnerList(Map<String, Object> params) {
		String productId = params.get("productName") + "";
		int pageNum = Integer.valueOf(params.get("start") + "");
		int pageSize = Integer.valueOf(params.get("length") + "");

		StringBuffer sql = new StringBuffer();
		sql.append("select btci.LOGIN_NAME \"loginName\", btidi.INVEST_AMOUNT \"investAmount\",btidi.CREATE_DATE \"createDate\""
				+ " from BAO_T_INVEST_DETAIL_INFO btidi,BAO_T_INVEST_INFO btii,BAO_T_CUST_INFO btci"
				+ " where btidi.invest_id=btii.id and btii.cust_id=btci.id  and btii.PRODUCT_ID="
				+ "(select id from BAO_T_PRODUCT_INFO where product_type=(select id from BAO_T_PRODUCT_TYPE_INFO where type_name=?))"
				+ " order by btidi.CREATE_DATE desc");

		return repositoryUtil.queryForPageMap(sql.toString(),
				new Object[] { productId }, pageNum, pageSize);
	}

	public Map<String, Object> countOwnerList(Map<String, Object> params) {
		String productId = params.get("productName") + "";
		StringBuffer sql = new StringBuffer();
		sql.append("select btci.LOGIN_NAME \"loginName\",sum(btidi.INVEST_AMOUNT) \"investAmount\""
				+ " from BAO_T_INVEST_DETAIL_INFO btidi,BAO_T_INVEST_INFO btii,BAO_T_CUST_INFO btci"
				+ " where btidi.invest_id=btii.id and btii.cust_id=btci.id and btii.PRODUCT_ID=(select id from BAO_T_PRODUCT_INFO where PRODUCT_NAME=?)");
		sql.append(" group by btci.LOGIN_NAME");
		return jdbcTemplate.queryForMap(sql.toString(),
				new Object[] { productId });
	}

	@Override
	public Page<Map<String, Object>> findCustInfoEntityByPage(
			Map<String, Object> param) {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ID,LOGIN_NAME,CREDENTIALS_TYPE,CREDENTIALS_CODE,CUST_NAME,CUST_CODE,BIRTHDAY,CUST_LEVEL,SAFE_LEVEL,CUST_GENDER,CUST_SOURCE,CUST_TYPE,NATVICE_PLACE_PROVINCE ,NATVICE_PLACE_CITY,NATVICE_PLACE_COUNTY,NATVICE_PLACE_AREA,COMMUN_ADDRESS,MOBILE,EMAIL,PORTRAIT_PATH,REAL_NAME_AUTH_COUNT,IS_LUMPER,MSG_ON_OFF,ENABLE_STATUS,RECORD_STATUS,CREATE_USER,CREATE_DATE,LAST_UPDATE_USER,LAST_UPDATE_DATE,VERSION,MEMO FROM BAO_T_CUST_INFO  WHERE 1=1 ");
		sb.append(" AND ID NOT IN('C00001','C00002','C00003','C00004','C00005','C00006','C00007','SYSTEM_ADMIN','KF0001') AND (CUST_KIND IS NULL OR CUST_KIND = '网站用户') ");
		if (!StringUtils.isEmpty(param.get("loginName"))) {
			String loginName = (String) param.get("loginName");
			sb.append(" AND LOGIN_NAME LIKE ? ");
			listObject.add("%" + loginName + "%");
		}

		if (!StringUtils.isEmpty(param.get("isSecurity"))) {
			String isSecurity = (String) param.get("isSecurity");
			// 已认证
			if ("1".equals(isSecurity)) {
				sb.append(" AND CREDENTIALS_CODE IS NOT NULL ");
			}
			// 未认证
			else if ("0".equals(isSecurity)) {
				sb.append(" AND CREDENTIALS_CODE IS  NULL ");
			}
		}
		String realName = (String) param.get("realName");
		if (!StringUtils.isEmpty(realName)) {
			sb.append(" AND CUST_NAME = ? ");
			listObject.add(realName);
		}

		// 证件类型
		if (!StringUtils.isEmpty(param.get("credentialsType"))) {
			String credentialsType = (String) param.get("credentialsType");
			sb.append(" AND CREDENTIALS_TYPE= ? ");
			listObject.add(credentialsType);
		}
		// 证件号码
		if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
			String credentialsCode = (String) param.get("credentialsCode");
			sb.append("AND CREDENTIALS_CODE= ? ");
			listObject.add(credentialsCode);
		}

		// 手机号码
		if (!StringUtils.isEmpty(param.get("mobile"))) {
			String mobile = (String) param.get("mobile");
			sb.append(" AND MOBILE=? ");
			listObject.add(mobile);
		}
		// 状态
		if (!StringUtils.isEmpty(param.get("status"))) {
			String enableStatus = (String) param.get("status");
			sb.append(" AND ENABLE_STATUS=? ");
			listObject.add(enableStatus);
		}
		// 日期查询

		if (!StringUtils.isEmpty(param.get("startDate"))) {
			String beginTime = (String) param.get("startDate");

			sb.append(" AND to_char(CREATE_DATE,'YYYY-MM-DD') >=? ");
			listObject.add(beginTime);

		}
		if (!StringUtils.isEmpty(param.get("endDate"))) {
			String endTime = (String) param.get("endDate");
			sb.append(" AND to_char(CREATE_DATE,'YYYY-MM-DD') <=? ");
			listObject.add(endTime);
		}

		sb.append(" ORDER BY CREATE_DATE DESC");
		int pageNum = Integer.valueOf(param.get("start") + "");
		int pageSize = Integer.valueOf(param.get("length") + "");

		return repositoryUtil.queryForPageMap(sb.toString(),
				listObject.toArray(), pageNum, pageSize);

	}


	@Override
	public ResultVo queryInviteData(Map<String, Object> custMap)
			throws SLException {
		StringBuffer sql = new StringBuffer();
		sql.append(
				"SELECT T.SPREAD_LEVEL \"spreadLevel\",COUNT(*) \"total\",COUNT(*) - SUM(NVL(DECODE(CAT.ID, '', 1),0)) \"untotal\",SUM(NVL(DECODE(CAT.ID, '', 1),0)) \"acttotal\" ")
				.append(" FROM BAO_T_CUST_INFO T LEFT JOIN (SELECT * FROM BAO_T_CUST_ACTIVITY_INFO CA WHERE CA.ACTIVITY_ID='2') CAT ON T.ID = CAT.CUST_ID WHERE 1=1 ");
		if (!StringUtils.isEmpty(custMap.get("queryPermission"))) {
			String queryPermission = CommonUtils.emptyToString(custMap
					.get("queryPermission"));
			sql.append(" AND T.QUERY_PERMISSION LIKE '")
					.append(queryPermission).append("%'");
		}
		sql.append(" GROUP BY T.SPREAD_LEVEL ORDER BY T.SPREAD_LEVEL");
		// 添加升序排列
		String orderSql = " SELECT * FROM (" + sql.toString()
				+ ") ORDER BY TO_NUmber(\"spreadLevel\") ASC ";
		List<Map<String, Object>> queryMapList = jdbcTemplate
				.queryForList(orderSql);
		return new ResultVo(true, "成功", queryMapList);
	}

	@Override
	public ResultVo queryCustSpreadLevel(Map<String, Object> custMap)
			throws SLException {
		StringBuffer sql = new StringBuffer();
		sql.append(
				"SELECT T.CUST_NAME \"custName\",T.LOGIN_NAME \"loginName\",T.CREATE_DATE \"createDate\",T.SPREAD_LEVEL \"spreadLevel\",A.AWARD_AMOUNT \"awardAmount\",T.ID \"id\" FROM BAO_T_CUST_INFO T,BAO_T_ACCOUNT_INFO A")
				.append(" WHERE T.ID=A.CUST_ID ");

		if (!StringUtils.isEmpty(custMap.get("queryPermission"))) {
			System.out.println(custMap.get("queryPermission").toString());
			String queryPermission = CommonUtils.emptyToString(custMap
					.get("queryPermission"));
			sql.append(" AND T.QUERY_PERMISSION LIKE '")
					.append(queryPermission).append("%'");
		}

		if (!StringUtils.isEmpty(custMap.get("spreadLevel"))) {
			String spreadLevel = CommonUtils.emptyToString(custMap
					.get("spreadLevel"));
			sql.append(" AND T.SPREAD_LEVEL='").append(spreadLevel).append("'");
		}
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql
				.toString());
		return new ResultVo(true, "成功", queryForList);
	}

	@Override
	public Map<String, Object> findCustInfoEntityDetail(
			Map<String, Object> custMap) throws SLException {
		String id = (String) custMap.get("id");

		Map<String, Object> result = new HashMap<String, Object>();

		String sql_cust_info = "SELECT T.LOGIN_NAME \"loginName\",T.CUST_NAME \"custName\",T.CREDENTIALS_TYPE \"credentialsType\",T.CREDENTIALS_CODE \"credentialsCode\",T.CUST_GENDER \"custGender\",T.MOBILE \"mobile\",to_char(T.CREATE_DATE, 'yyyy-mm-dd hh24:mi:ss') \"createDate\",T.EMAIL \"email\" FROM BAO_T_CUST_INFO T WHERE T.ID='"
				+ id + "'";
		List<Map<String, Object>> custInfoMap = jdbcTemplate
				.queryForList(sql_cust_info);
		if (custInfoMap.size() > 0)
			result.putAll(custInfoMap.get(0));

		String sql_loginLog = "SELECT * FROM (SELECT to_char(T.LOGIN_DATE, 'yyyy-mm-dd hh24:mi:ss') \"loginDate\",T.LOGIN_IP \"loginIP\" FROM BAO_T_LOGIN_LOG_INFO T WHERE T.CUST_ID='"
				+ id + "'  order by T.login_date desc)WHERE  rownum<=1";
		List<Map<String, Object>> loginLogMap = jdbcTemplate
				.queryForList(sql_loginLog);
		if (loginLogMap.size() > 0)
			result.putAll(loginLogMap.get(0));

		String sql_account_info = "SELECT T.ACCOUNT_AVAILABLE_AMOUNT \"accountAvailableAmount\",T.ACCOUNT_FREEZE_AMOUNT \"accountFreezeAmount\" FROM BAO_T_ACCOUNT_INFO T WHERE T.CUST_ID='"
				+ id + "'";
		List<Map<String, Object>> accountInfoMap = jdbcTemplate
				.queryForList(sql_account_info);
		if (accountInfoMap.size() > 0)
			result.putAll(accountInfoMap.get(0));

		return result;
	}

	@Override
	public int updateCustInfoToAvoidDuplicate(String custId, boolean flag) {
		
		int rows = 0;
		
		if(flag) { // 设置标志位
			StringBuilder sqlString = new StringBuilder()
			.append("update bao_t_cust_info set memo = 'locking' where id = ? and memo is null");
			
			rows = jdbcTemplate.update(sqlString.toString(), new Object[]{custId});	
		}
		else { // 取消标志位
			StringBuilder sqlString = new StringBuilder()
			.append("update bao_t_cust_info set memo = null where id = ? and memo = 'locking'");
			
			rows = jdbcTemplate.update(sqlString.toString(), new Object[]{custId});
		}
		
		return rows;
	}

	@Override
	public List<Map<String, Object>> findRigsterCount() {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer()
		.append("select count(1) \"regCount\",trunc(btci.create_date) \"createDate\",btci.cust_source \"custSource\" from bao_t_cust_info btci ")
		.append(" where btci.login_name not in ('居间人账户','公司收益账户','系统管理员','公司客服账户','风险金账户')")
		.append(" and (btci.is_recommend != '是' or btci.is_recommend is null)")
		.append("  and btci.invite_origin_id not in (select id from bao_t_cust_info btcii where btcii.is_recommend = '是' )")
		.append(" group by trunc(btci.create_date), btci.cust_source order by trunc(btci.create_date)");
		
		return repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
	}

	@Override
	public Page<Map<String, Object>> findCompanyList(Map<String, Object> params) {
		
		StringBuilder sqlString = new StringBuilder()
		.append("  select t.id \"id\", t.login_name \"companyName\", t.cust_kind \"projectType\", ")
		.append("        t.tel \"telephone\", t.cust_name \"custName\", t.commun_address \"communAddress\",  ")
		.append("        t.memo \"memo\" ")
		.append("  from bao_t_cust_info t ")
		.append("  where 1 = 1 ");
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, params);
		sqlCondition.addString("companyId", "t.id")
					.addString("companyName", "t.login_name")
					.addString("projectType", "t.cust_kind")
					.addString("telephone", "t.tel")
					.addString("custName", "t.cust_name")
					.addSql(" order by t.create_date desc ");

		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.valueOf(params.get("start").toString()),
				Integer.valueOf(params.get("length").toString()));
	}

	@Override
	public Boolean judgeUserIsEmployee(Map<String, Object> params) {
		StringBuilder sqlString = new StringBuilder()
		.append("   select count(1) \"counts\"  ")
		.append("   from vw_bd_psndoc t   ")
		.append("   where t.状态 = '在职'   ")
		.append("   and (t.职位 like '%P%'  ")
		.append("       or t.职位  in ( ")
		.append("           '初级团队经理', ")
		.append("           '中级团队经理', ")
		.append("           '高级团队经理', ")
		.append("           '资深团队经理', ")
		.append("           '初级店长', ")
		.append("           '中级店长', ")
		.append("           '高级店长', ")
		.append("           '资深店长', ")
		.append("           '初级大团队经理', ")
		.append("           '高级大团队经理', ")
		.append("           '初级营业部经理', ")
		.append("           '中级营业部经理', ")
		.append("           '高级营业部经理', ")
		.append("           '资深营业部经理', ")
		.append("           '初级城市经理', ")
		.append("           '中级城市经理', ")
		.append("           '高级城市经理', ")
		.append("           '初级区域经理', ")
		.append("           '中级区域经理', ")
		.append("           '高级区域经理', '副总裁' ")
		.append("       ) ")
		.append("   )   ")
		.append("   and upper(t.证件号码) = upper(?)  ")
		.append("   and t.姓名 = ?  ");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[]{params.get("credentialsCode"), params.get("custName")});
		if(list != null && list.size() > 0) {
			return (Integer.parseInt(list.get(0).get("counts").toString()) > 0);
		}
		else {
			return false;
		}
	}

	@Override
	public int leaveJob(Map<String, Object> params) {
		
		StringBuilder sqlString = new StringBuilder()
		.append(" update bao_t_cust_info t ")
		.append("    set t.working_state    = ?, ")
		.append("        t.last_update_date = ?, ")
		.append("        t.last_update_user = ?, ")
		.append("        t.version          = version + 1 ")
		.append("  where t.is_employee = '是' ")
		.append("    and (t.working_state is null or t.working_state = ?) ")
		.append("    and exists ")
		.append("  (select 1 ")
		.append("           from vw_bd_psndoc s ")
		.append("          where (s.状态 = '离职' or ")
		.append("                (s.职位 not like '%P%' and ")
		.append("                s.职位 not in ('初级团队经理', ")
		.append("                               '中级团队经理', ")
		.append("                               '高级团队经理', ")
		.append("                               '资深团队经理', ")
		.append("                               '初级店长', ")
		.append("                               '中级店长', ")
		.append("                               '高级店长', ")
		.append("                               '资深店长', ")
		.append("                               '初级大团队经理', ")
		.append("                               '高级大团队经理', ")
		.append("                               '初级营业部经理', ")
		.append("                               '中级营业部经理', ")
		.append("                               '高级营业部经理', ")
		.append("                               '资深营业部经理', ")
		.append("                               '初级城市经理', ")
		.append("                               '中级城市经理', ")
		.append("                               '高级城市经理', ")
		.append("                               '初级区域经理', ")
		.append("                               '中级区域经理', ")
		.append("                               '高级区域经理', '副总裁'))) ")
		.append("            and upper(t.credentials_code) = upper(s.证件号码)) ");
		
		return jdbcTemplate.update(sqlString.toString(), new Object[] {
			Constant.WORKING_STATE_OUT, new Date(), Constant.SYSTEM_USER_BACK, 
			Constant.WORKING_STATE_IN});
	}

	/**
	 * 企业商户-商户列表
	 * @author liyy
	 * @date   2016年12月16日下午15:30:00
	 * @param param
	 *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
	 * @return
     *      <tt>custId                :String:客户编号</tt><br>
     *      <tt>custName              :String:姓名</tt><br>
     *      <tt>accountAvailableAmount:String:账户余额</tt><br>
     *      <tt>bankName              :String:银行卡名称</tt><br>
     *      <tt>cardNo                :String:银行卡卡号</tt><br>
     *      <tt>tel                   :String:联系电话（客户表联系电话）</tt><br>
     *      <tt>address               :String:地址（客户表通讯地址）</tt><br>
	 */
	public Page<Map<String, Object>> queryCompanyUserList(
			Map<String, Object> params) {
		StringBuilder sqlString = new StringBuilder()
		.append(" SELECT cust.ID \"custId\" ")
		.append("      , cust.LOGIN_NAME \"custName\" ")
		.append("      , cust.TEL \"tel\" ")
		.append("      , cust.COMMUN_ADDRESS \"address\" ")
		.append("      , acc.ACCOUNT_AVAILABLE_AMOUNT \"accountAvailableAmount\" ")
		.append("      , bank.BANK_NAME \"bankName\" ")
//		.append("      , bank.CARD_NO \"cardNo\" ")
		.append("      , decode(bank.CARD_NO,NULL,'',substr(bank.CARD_NO,1,4)||'****' || substr(bank.CARD_NO,-4)) \"cardNo\" ")
		.append("      , bank.SUB_BRANCH_NAME \"subBranchName\" ")
		.append("   FROM bao_t_cust_info cust ")
		.append("      , BAO_T_ACCOUNT_INFO acc ")
		.append("      , BAO_T_BANK_CARD_INFO bank ")
		.append("  WHERE acc.CUST_ID = cust.ID ")
		.append("    AND bank.CUST_ID = cust.ID ")
		.append("    AND bank.RECORD_STATUS = '有效' ")
		.append("    AND cust.CUST_TYPE = '公司客户' ")// '公司客户'
		.append("  order by cust.create_date desc ")
		;
		List<Object> objList = Lists.newArrayList();
		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), Integer.valueOf(params.get("start").toString()), Integer.valueOf(params.get("length").toString()));
	}

    @Override
    public List<Map<String, Object>> getFirstAchievingDateAndRanking(String custId) {
        StringBuilder sql = new StringBuilder()
                .append("select * from (")
                .append("select t.ranking RANKING, min(t.curr_date) MIN_DATE ")
                .append("  from bao_t_daily_remainder_info t ")
                .append(" where t.cust_id = ? and t.curr_date >= '20170601' ")
                .append(" group by t.ranking) ")
                .append(" order by MIN_DATE ")
                ;
        return jdbcTemplate.queryForList(sql.toString(), custId);
    }

    @Override
    public List<Map<String, Object>> getEndDateOfFirstAchieving(String custId, int ranking, String nextBeginDate) {
        StringBuilder sql = new StringBuilder()
                .append("select max(t.curr_date) MAX_DATE")
                .append("  from bao_t_daily_remainder_info t ")
                .append(" where t.cust_id = ? and t.curr_date >= '20170601' ")
                .append("   and t.ranking = ? ")
                .append("   and t.curr_date < ? ")
                ;
        return jdbcTemplate.queryForList(sql.toString(), custId, ranking, nextBeginDate);
    }

    public Page<Map<String, Object>> queryCustRiskList(
			Map<String, Object> params) throws SLException {
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
				.append(" select t.risk_assessment \"riskAssessment\", ")
				.append("        t.login_name \"loginName\", ")
				.append("        t.assess_time \"assessTime\", ")
				.append("        substr(REPLACE(t.risk_assessment_answer,',',''), 1, 1) \"num1\", ")
				.append("        substr(REPLACE(t.risk_assessment_answer,',',''), 2, 1) \"num2\", ")
				.append("        substr(REPLACE(t.risk_assessment_answer,',',''), 3, 1) \"num3\", ")
				.append("        substr(REPLACE(t.risk_assessment_answer,',',''), 4, 1) \"num4\", ")
				.append("        substr(REPLACE(t.risk_assessment_answer,',',''), 5, 1) \"num5\", ")
				.append("        substr(REPLACE(t.risk_assessment_answer,',',''), 6, 1) \"num6\", ")
				.append("        substr(REPLACE(t.risk_assessment_answer,',',''), 7, 1) \"num7\", ")
				.append("        substr(REPLACE(t.risk_assessment_answer,',',''), 8, 1) \"num8\", ")
				.append("        substr(REPLACE(t.risk_assessment_answer,',',''), 9, 1) \"num9\", ")
				.append("        substr(REPLACE(t.risk_assessment_answer,',',''), 10, 1) \"num10\" ")
				.append("   from bao_t_cust_info t ")
				.append("   where t.risk_assessment is not null ");
		if (!StringUtils.isEmpty(params.get("riskAssessment"))) {
			sql.append(" and t.risk_assessment = ?");
			listObject.add(params.get("riskAssessment"));
		}
		if (!StringUtils.isEmpty(params.get("loginName"))) {
			sql.append(" and t.login_name like ?");
			listObject.add("%" + params.get("loginName") + "%");
		}

		return repositoryUtil.queryForPageMap(sql.toString(),
				listObject.toArray(),
				Integer.parseInt(params.get("start").toString()),
				Integer.parseInt(params.get("length").toString()));
	}
    
    
    
    /**
	 * 实名认证
	 * 
	 * @author HuangXiaodong
	 * @date 2017年6月24日
	 * @param Map
	 *         <tt>start           :String:起始值</tt><br>
     *         <tt>length          :String:长度</tt><br>
	 *         <tt>custName           :String:客户姓名(可以为空)</tt><br>
	 *         <tt>credentialsCode       :String:证件号码(可以为空)</tt><br>
	 *         <tt>tradeStatus        :String:认证结果(可以为空)</tt><br>
	 *         <tt>loginName          :String:认证人(可以为空)</tt><br>
	 *          <tt>startDate   ：Date:认证开始时间</tt><br>
	 *          <tt>endDate：        Date:认证结束时间</tt><br>
	 * @return ResultVo <tt>custName  :String:客户姓名</tt><br>
	 *         <tt>credentialsCode        :String:证件号码</tt><br>
	 *         <tt>tradeStatus        :String:认证结果</tt><br>
	 *         <tt>custAuthDate： BigDecimal:认证时间</tt><br>
	 *         <tt>loginName          :String:认证人</tt><br>
	 * @throws SLException
	 */
    
    public Page<Map<String, Object>> queryCustAuthInfoList(
			Map<String, Object> param) {
    	ArrayList<Object> listObject = new ArrayList<Object>();

		StringBuilder sql = new StringBuilder()
		.append(" select t.cust_name        \"custName\", ")
		.append("        t.credentials_code     \"credentialsCode\", ")
		.append("        t.trade_status     \"tradeStatus\", ")
		.append("        t.last_update_date   \"custAuthDate\", ")
		.append("        c.user_name      \"loginName\" ")
		.append("   from Bao_t_Real_Name_Info  t left join  ")
		.append("   com_t_user  c on t.last_update_user=c.id  ")
		.append("  where 1=1  ");
		if (!StringUtils.isEmpty(param.get("startDate"))) {
			sql.append(" and t.create_date >= TRUNC(TO_DATE(?,'YYYY-MM-DD')) ");
			listObject.add(param.get("startDate").toString());
		}
		if (!StringUtils.isEmpty(param.get("endDate"))) {
			sql.append(" and t.create_date <= TRUNC(TO_DATE(?,'YYYY-MM-DD'))+0.99999");
			listObject.add(param.get("endDate").toString());
		}
		if (!StringUtils.isEmpty(param.get("tradeStatus"))) {
			sql.append(" and t.trade_status = ? ");
			listObject.add(param.get("tradeStatus").toString());
		}
		SqlCondition sqlCondition = new SqlCondition(sql, param,listObject)
		.addLike("custName", "t.cust_name")
		.addLike("credentialsCode", "t.credentials_code")
		.addLike("loginName", "c.user_name ")
		;
		
		sql.append(" order by t.last_update_date desc ");
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}

	@Override
	public int updateOpenIdByMobile(Map<String, Object> paraMap) {
		StringBuilder sqlString = new StringBuilder()
		.append("update bao_t_cust_info t ")
		.append("set t.OPEN_ID = ? ")
		.append("where t.mobile = ? ");
			
		return jdbcTemplate.update(sqlString.toString(), new Object[]{paraMap.get("openId"),paraMap.get("mobile")});
	}

}
