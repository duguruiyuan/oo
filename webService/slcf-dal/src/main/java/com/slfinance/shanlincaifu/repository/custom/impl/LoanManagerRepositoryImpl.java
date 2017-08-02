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
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.ParamRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.LoanManagerRepositoryCustom;
import com.slfinance.shanlincaifu.utils.*;

@Repository
public class LoanManagerRepositoryImpl implements LoanManagerRepositoryCustom{

	@Autowired
	RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private ParamRepository paramRepository;

	/**     
	 * 借款管理-投资记录列表查询
	 * @param param Map
	 *      <tt>start               :String:起始值</tt><br>
     *      <tt>length              :String:长度</tt><br>
     *      <tt>loanCode            :String:借款编号(可以为空)</tt><br>
     *      <tt>custName            :String:客户姓名(可以为空)</tt><br>
     *      <tt>mobile              :String:手机号码(可以为空)</tt><br>
     *      <tt>managerName         :String:手机号码(可以为空)</tt><br>
     *      <tt>investStatus        :String:投资状态(可以为空)</tt><br>
     *      <tt>investDateStart     :String:投资时间-区间头(可以为空)</tt><br>
     *      <tt>investDateEnd       :String:投资时间-区间末(可以为空)</tt><br>
     *      <tt>investStartDateStart:String:起息日期(借款信息表)-区间头(可以为空)</tt><br>
     *      <tt>investStartDateEnd  :String:起息日期(借款信息表)-区间末(可以为空)</tt><br>
     *      <tt>expireDateStart     :String:到期日期-区间头(可以为空)</tt><br>
     *      <tt>expireDateEnd       :String:到期日期-区间末(可以为空)</tt><br>
	 * @return 
     *      <tt>custName         :String:客户姓名</tt><br>
     *      <tt>mobile           :String:手机号码</tt><br>
     *      <tt>investAmount     :String:投资金额（元）</tt><br>
     *      <tt>loanCode         :String:借款编号</tt><br>
     *      <tt>investDate       :String:投资时间</tt><br>
     *      <tt>investStartDate  :String:起息日期(借款信息表)</tt><br>
     *      <tt>expireDate       :String:到期日期</tt><br>
     *      <tt>holdScale        :String:当前持比(借款信息表)</tt><br>
     *      <tt>investStatus     :String:投资状态</tt><br>
	 */
	public Page<Map<String, Object>> queryInvestListForPageMap(
			Map<String, Object> param) {
		SqlCondition sqlCondition = getInvestListSql(param);
		
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}
	
	private SqlCondition getInvestListSql(Map<String, Object> param){
		StringBuilder sql = new StringBuilder()
		.append("   SELECT \"custName\"   , \"mobile\"    ,    \"investAmount\"   ,  ")
		.append("          \"loanCode\"   , \"investDate\",    \"investStartDate\",  ")
		.append("          \"expireDate\" , \"holdScale\" ,    \"investStatus\"   , ")
		.append("          \"managerName\", \"managerId\" ,    \"loanId\", \"investSource\"  ")
		.append("   FROM ( ")
		.append("    SELECT cust.CUST_NAME \"custName\"  ")
		.append("              , cust.MOBILE \"mobile\"  ")
		.append("              , TRUNC(invest.INVEST_AMOUNT,2) \"investAmount\"  ")
		.append("              , loan.LOAN_CODE \"loanCode\"  ")
		.append("              , invest.CREATE_DATE \"investDate\"  ")
		.append("              , to_date(invest.effect_date, 'yyyyMMdd') \"investStartDate\"  ")
		.append("              , to_date(invest.expire_date, 'yyyyMMdd') \"expireDate\"  ")
//		.append("              , round(invest.INVEST_AMOUNT/loan.LOAN_AMOUNT*100, 2) \"holdScale\"  ")
		.append("              , invest.INVEST_AMOUNT/loan.LOAN_AMOUNT*100 \"holdScale\"  ")
		.append("              , invest.INVEST_STATUS \"investStatus\"  ")
		.append("              , decode(nvl(cust.is_employee, '否'), '是', cust.cust_name, custManage.Cust_Name) \"managerName\" ")
		.append("              , decode(nvl(cust.is_employee, '否'), '是', cust.id, custManage.id) \"managerId\" ")
		.append("              , loan.ID \"loanId\"  ")
		.append("              , case when b.invest_source = 'auto' then '自动投标' when b.invest_source = 'reserveinvest' then '优先投' else '手动投标' end \"investSource\"  ")
		.append("          FROM BAO_T_INVEST_INFO invest ")
		.append("          INNER JOIN BAO_T_LOAN_INFO loan on loan.id = invest.loan_id ")
		.append("          inner join bao_t_invest_detail_info b on b.invest_id = invest.id ")
		.append("          INNER JOIN BAO_T_CUST_INFO cust on cust.id = invest.cust_id ")
		.append("          LEFT  JOIN bao_t_cust_recommend_info r on r.quilt_cust_id = invest.cust_id and r.record_status = '有效'   ")
		.append("          left join  bao_t_cust_info custManage on custManage.id = r.cust_id ")
		.append("        WHERE (invest.effect_date is null or invest.effect_date >= ?) ")
		.append("     UNION      ")
		.append("      SELECT cust.CUST_NAME \"custName\"  ")
		.append("              , cust.MOBILE \"mobile\"  ")
		.append("              , TRUNC(invest.INVEST_AMOUNT,2) \"investAmount\"  ")
		.append("              , loan.LOAN_CODE \"loanCode\"  ")
		.append("              , invest.CREATE_DATE \"investDate\"  ")
		.append("              , to_date(invest.effect_date, 'yyyyMMdd') \"investStartDate\"  ")
		.append("              , to_date(invest.expire_date, 'yyyyMMdd') \"expireDate\"  ")
//		.append("              , round(invest.INVEST_AMOUNT/loan.LOAN_AMOUNT*100, 2) \"holdScale\"  ")
		.append("              , invest.INVEST_AMOUNT/loan.LOAN_AMOUNT*100 \"holdScale\"  ")
		.append("              , invest.INVEST_STATUS \"investStatus\"  ")
		.append("              , custManage.Cust_Name \"managerName\" ")
		.append("              , custManage.id \"managerId\" ")
		.append("              , loan.ID \"loanId\"  ")
		.append("              , case when b.invest_source = 'auto' then '自动投标' when b.invest_source = 'reserveinvest' then '优先投' else '手动投标' end \"investSource\"  ")
		.append("          FROM BAO_T_INVEST_INFO invest ")
		.append("          inner join bao_t_invest_detail_info b on b.invest_id = invest.id ")
		.append("          INNER JOIN BAO_T_LOAN_INFO loan on loan.id = invest.loan_id ")
		.append("          INNER JOIN BAO_T_CUST_INFO cust on cust.id = invest.cust_id ")
		.append("          left join bao_t_commission_detail_info q on q.invest_id = invest.id ")
		.append("          left join bao_t_commission_info p on p.id = q.commission_id ")
		.append("          left join  bao_t_cust_info custManage on custManage.id = p.cust_id ")
		.append("      WHERE  invest.effect_date < ? ")
		.append("   ) a ")
		.append("   where 1 = 1 ")
		;
		
		String newDay = DateUtils.formatDate(new Date(), "yyyyMM") + "01";
		List<Object> objList = Lists.newArrayList();
		objList.add(newDay);
		objList.add(newDay);
		
		SqlCondition sqlCondition = new SqlCondition(sql, param, objList)
		.addString("loanCode", "a.\"loanCode\"")
		.addString("custName", "a.\"custName\"")
		.addString("mobile", "a.\"mobile\"")
		.addString("investStatus", "a.\"investStatus\"")
		.addString("managerName", "a.\"managerName\"")
		.addBeginDate("investDateStart", "a.\"investDate\"")
		.addEndDate("investDateEnd", "a.\"investDate\"")
		.addBeginDate("investStartDateStart", "a.\"investStartDate\"")
		.addEndDate("investStartDateEnd", "a.\"investStartDate\"")
		.addBeginDate("expireDateStart", "a.\"expireDate\"")
		.addEndDate("expireDateEnd", "a.\"expireDate\"")
		.addSql(" ORDER BY decode(a.\"investStatus\",'投资中',1,'满标复核',2,'收益中',3,'已到期',4,'提前结清',5,'流标',6) ")
		.addSql("        , a.\"investDate\" desc  ")
		;
		return sqlCondition;
	}

	/**
	 * 借款管理-投资记录列表查询-金额汇总
	 * @return investTotalAmount:String:投资金额
	 */
	public BigDecimal queryInvestTotalAmount(Map<String, Object> param) {
		SqlCondition sqlCondition = getInvestListSql(param);
		
		StringBuilder sql = new StringBuilder()
		.append(" SELECT sum(\"investAmount\") \"investTotalAmount\" FROM ( ")
		.append(sqlCondition.toString())
		.append(" ) ")
		;
		BigDecimal investTotalAmount = jdbcTemplate.queryForObject(sql.toString(), sqlCondition.toArray(), BigDecimal.class);
		return investTotalAmount;
	}

	/**
	 * 转让记录列表查询
	 * @param Map<String, Object>
     *      <tt>start            :String:起始值</tt><br>
     *      <tt>length           :String:长度</tt><br>
     *      <tt>receiveCustName  :String:受让人(可以为空)</tt><br>
     *      <tt>receiveCustMobile:String:受让人手机号码(可以为空)</tt><br>
     *      <tt>senderCustName   :String:转让人(可以为空)</tt><br>
     *      <tt>senderCustMobile :String:转让人手机号码(可以为空)</tt><br>
     *      <tt>loanCode         :String:借款编号(可以为空)</tt><br>
     *      <tt>investStatus     :String:投资状态(可以为空)</tt><br>
     *      <tt>createDateStart  :String:购买日期-区间头(可以为空)</tt><br>
     *      <tt>createDateEnd    :String:购买日期-区间末可以为空)</tt><br>
     *      <tt>expireDateStart  :String:到期日期-区间头(可以为空)</tt><br>
     *      <tt>expireDateEnd    :String:到期日期-区间末(可以为空)</tt><br>
	 * @return Page < Map < String, Object > >.list<br>
     *      <tt>receiveCustName    :String:受让人(债权转让表)</tt><br>
     *      <tt>receiveCustMobile  :String:手机号码</tt><br>
     *      <tt>tradeAmount        :String:受让价格（元）</tt><br>
     *      <tt>tradeValue         :String:受让价值（元） (转让比例*债权PV)</tt><br>
     *      <tt>loanCode           :String:借款编号</tt><br>
     *      <tt>senderCustName     :String:转让人(债权转让表)</tt><br>
     *      <tt>senderCustMobile   :String:手机号码</tt><br>
     *      <tt>createDate         :String:购买日期(债权转让表)</tt><br>
     *      <tt>expireDate         :String:到期日期</tt><br>
     *      <tt>investStatus       :String:投资状态</tt><br>
	 */
	public Page<Map<String, Object>> queryLoanTransferListForPageMap(
			Map<String, Object> param) {
		SqlCondition sql = getLoanTransferListSql(param);
		
		return repositoryUtil.queryForPageMap(sql.toString(), sql.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}

	private SqlCondition getLoanTransferListSql(Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
		.append("  SELECT receiver.CUST_NAME \"receiveCustName\"  ")
		.append("  	 , decode(receiver.MOBILE,NULL,'',substr(receiver.MOBILE,1,3)||'****' || substr(receiver.MOBILE,-4)) \"receiveCustMobile\"  ")
//		.append("  	 , TRUNC(lt.TRADE_AMOUNT, 2) AS \"tradeAmount\"  ")
//		.append("  	 , TRUNC(lt.TRADE_VALUE, 2) AS \"tradeValue\"  ")
		.append("  	 , case when lta.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then TRUNC(lt.TRADE_AMOUNT, 2) else TRUNC(lt.TRADE_PRINCIPAL * lta.REDUCED_SCALE, 2) end AS \"tradeAmount\"  ")
		.append("  	 , case when lta.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then TRUNC(lt.TRADE_VALUE, 2) else TRUNC(lt.TRADE_PRINCIPAL, 2) end AS \"tradeValue\"  ")
		.append("  	 , loan.LOAN_CODE \"loanCode\" ")
		.append("  	 , sender.CUST_NAME \"senderCustName\"  ")
		.append("  	 , decode(sender.MOBILE,NULL,'',substr(sender.MOBILE,1,3)||'****' || substr(sender.MOBILE,-4)) \"senderCustMobile\"  ")
		.append("  	 , lt.CREATE_DATE \"buyDate\"  ")
		.append("  	 , loan.INVEST_END_DATE \"expireDate\"  ")
		.append("  	 , invest.INVEST_STATUS \"investStatus\"  ")
		.append("  	 , custManage.ID \"custManagerId\" ")
		.append("  	 , custManage.CUST_NAME \"custManagerName\" ")
		.append("  	 , loan.id \"loanId\" ")
		.append("  	 , case when b.invest_source = 'auto' then '自动投标' when b.invest_source = 'reserveinvest' then '优先投' else '手动投标' end \"investSource\" ")
		.append("   FROM BAO_T_LOAN_TRANSFER lt ")
		.append("   INNER JOIN BAO_T_LOAN_TRANSFER_APPLY lta ON lta.ID = lt.TRANSFER_APPLY_ID ")
		.append("   INNER JOIN BAO_T_WEALTH_HOLD_INFO wh ON wh.ID = lt.RECEIVE_HOLD_ID ")
		.append("   INNER JOIN BAO_T_INVEST_INFO invest ON invest.INVEST_STATUS IN ('收益中', '已转让', '已到期', '提前结清')  ")
		.append("     AND invest.LOAN_ID IS NOT NULL ")
		.append("     AND invest.ID = wh.INVEST_ID ")
		.append("   inner join bao_t_invest_detail_info b on b.invest_id = invest.id ")
		.append("   INNER JOIN BAO_T_LOAN_INFO loan ON loan.ID = lt.RECEIVE_LOAN_ID ")
		.append("   INNER JOIN BAO_T_CUST_INFO sender ON sender.ID = lt.SENDER_CUST_ID ")
		.append("   INNER JOIN BAO_T_CUST_INFO receiver ON receiver.ID = lt.RECEIVE_CUST_ID ")
		.append("   LEFT join bao_t_commission_detail_info q ")
		.append("   on q.INVEST_ID = invest.id ")
		.append("   left join bao_t_commission_info p ")
		.append("       on p.ID = q.COMMISSION_ID ")
		.append("     left join bao_t_cust_info custManage ")
		.append("       on custManage.id = p.cust_id ")
		.append("  WHERE 1 = 1 ")
		;
		List<Object> objList = Lists.newArrayList();
		objList.add((String)param.get("onlineTime"));
		objList.add((String)param.get("onlineTime"));
		
		SqlCondition sqlCondition = new SqlCondition(sql, param, objList)
		.addString("receiveCustName", "receiver.CUST_NAME")
		.addString("receiveCustMobile", "receiver.MOBILE")
		.addString("senderCustName", "sender.CUST_NAME")
		.addString("senderCustMobile", "sender.MOBILE")
		.addString("loanCode", "loan.LOAN_CODE")
		.addString("investStatus", "invest.INVEST_STATUS")
		.addBeginDate("buyDateStart", "lt.CREATE_DATE")
		.addEndDate("buyDateEnd", "lt.CREATE_DATE")
		.addBeginDate("expireDateStart", "loan.INVEST_END_DATE")
		.addEndDate("expireDateEnd", "loan.INVEST_END_DATE")
		.addString("custManagerName", "custManage.CUST_NAME")
		.addSql(" order by decode(invest.INVEST_STATUS,'收益中',1,'已转让',2,'已到期',3,'提前结清',4,99) ")
		.addSql("         , lt.CREATE_DATE desc ")
		;
		return sqlCondition;
	}
	
	/**
	 * 转让记录列表-汇总数据查询
	 * @date 2016年11月25日下午16:25:59
	 * @author lyy
	 * @return  <tt>transferCount</tt><br>
	 *			<tt>transferTotalAmount</tt><br>
	 *			<tt>transferTotalValue</tt><br>
	 */
	public Map<String, Object> queryLoanTransferTotalAmount(
			Map<String, Object> param) {
		SqlCondition sqlCondition = getLoanTransferListSql(param);
		
		StringBuilder sql = new StringBuilder()
		.append(" SELECT count(1) \"transferCount\" ")
		.append(" , nvl(sum(\"tradeAmount\"),0) \"transferTotalAmount\" ")
		.append(" , nvl(sum(\"tradeValue\"),0) \"transferTotalValue\" ")
		.append(" FROM ( ")
		.append(sqlCondition.toString())
		.append(" ) ")
		;
		return jdbcTemplate.queryForMap(sql.toString(), sqlCondition.toArray());
	}
	
	/**
	 * 借款信息列表查询
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>loanCode       :String:借款编号(可以为空)</tt><br>
     *      <tt>custName       :String:客户姓名(可以为空)</tt><br>
     *      <tt>mobile         :String:手机号码(可以为空)</tt><br>
     *      <tt>credentialsCode:String:证件号码(可以为空)</tt><br>
     *      <tt>loanTerm       :String:借款期限(可以为空)</tt><br>
     *      <tt>loanType       :String:借款类型(可以为空)</tt><br>
     *      <tt>loanStatus     :String:借款状态(可以为空)</tt><br>
     *      <tt>companyName    :String:公司名称(可以为空)</tt><br>
     *      <tt>repaymentMethod:String:还款方式(可以为空)</tt><br>
     *      <tt>createDateStart:String:申请日期-区间头(可以为空)</tt><br>
     *      <tt>createDateEnd  :String:申请日期-区间末(可以为空)</tt><br>
     *      <tt>auditDateStart :String:审核日期-区间头(可以为空)</tt><br>
     *      <tt>auditDateEnd   :String:审核日期-区间末(可以为空)</tt><br>
     *      <tt>receiveUser    :String:领取人(可以为空)</tt><br>
     *      <tt>receiveStatus  :String:领取状态(可以为空)</tt><br>
	 * @return
     *      <tt>data           :String:List<Map<String,Object>></tt><br>
     *      <tt>loanId         :String:借款信息表主键Id</tt><br>
     *      <tt>loanCode       :String:借款编号</tt><br>
     *      <tt>custName       :String:客户姓名</tt><br>
     *      <tt>mobile         :String:手机号码</tt><br>
     *      <tt>credentialsCode:String:证件类型</tt><br>
     *      <tt>loanType       :String:借款类型</tt><br>
     *      <tt>loanAmount     :String:借款金额（元）</tt><br>
     *      <tt>loanTerm       :String:借款期限</tt><br>
     *      <tt>yearIrr        :String:借款利率 （详细表）</tt><br>
     *      <tt>repaymentMethod:String:还款方式</tt><br>
     *      <tt>loanStatus     :String:借款状态</tt><br>
     *      <tt>createDate     :String:申请日期</tt><br>
     *      <tt>receiveStatus  :String:领取状态</tt><br>
     *      <tt>auditTime      :String:审核日期</tt><br>
     *      <tt>receiveUser    :String:领取人员</tt><br>
	 * @throws SLException
	 */
	public Page<Map<String, Object>> queryLoanInfoList(Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT loan.ID \"loanId\" ")
		.append("      , loan.LOAN_CODE \"loanCode\" ")
		.append("      , lc.CUST_NAME \"custName\" ")
		.append("      , lc.MOBILE \"mobile\" ")
		.append("      , decode(lc.CREDENTIALS_CODE,NULL,'',substr(lc.CREDENTIALS_CODE,1,3)||'****' || substr(lc.CREDENTIALS_CODE,-4)) \"credentialsCode\" ")
		.append("      , loan.LOAN_DESC \"loanDesc\" ")
		.append("      , loan.invest_end_date \"investEndDate\" ")
		.append("      , loan.LOAN_TYPE \"loanType\" ")
		.append("      , loan.LOAN_AMOUNT \"loanAmount\" ")
		.append("      , loan.LOAN_TERM \"loanTerm\" ")
		.append("      , loan.LOAN_UNIT \"loanUnit\" ")
		.append("      ,  nvl(trunc(round(loan.award_rate,8)*100,2),0) \"awardRate\" ")
		.append("      , round(ld.YEAR_IRR*100, 2) \"yearIrr\" ")// 需求
		.append("      , loan.REPAYMENT_METHOD \"repaymentMethod\" ");
		//isBackStage为借款信息则是大后台的接口信息查询,否则是前端直投预约信息列表查询
		if (!StringUtils.isEmpty(param.get("isBackStage")) && "借款信息".equals(param.get("isBackStage"))) {
			sql.append("      , loan.LOAN_STATUS \"loanStatus\" ");
		} else {
			sql.append("      , case ")
			.append("          when loan.LOAN_TYPE is not null and loan.loan_type = '善真贷' then ")
			.append("           CASE ")
			.append("             WHEN loan.loan_status = '满标复核' THEN ")
			.append("              '募集中' ")
			.append("             WHEN loan.loan_status = '正常' or loan.loan_status = '提前结清' or ")
			.append("                  loan.loan_status = '已到期' then ")
			.append("              '募集成功' ")
			.append("             ELSE ")
			.append("              loan.loan_status ")
			.append("           END ").append("          else ")
			.append("           loan.loan_status ")
			.append("        end \"loanStatus\" ");
		};
		sql.append("      , loan.APPLY_TIME \"createDate\" ")
		.append("      , loan.CREATE_DATE \"pushDate\" ")
		.append("      , lr.FLAG \"loanFlag\" ")
		.append("      , loan.LOAN_TITLE \"loanTitle\" ")
		.append("      , loan.RECEIVE_STATUS \"receiveStatus\" ")
		.append("      , ai.AUDIT_TIME \"auditTime\" ")
		.append("      , u.USER_NAME \"receiveUser\" ")
		.append("      , ai.AUDIT_STATUS \"auditStatus\" ")
		.append("      , lc.CUST_GENDER \"custGender\" ")
		.append("      , lc.CUST_AGE \"custAge\" ")
		.append("      , lc.MARRIAGE_STATE \"marriageState\" ")
		.append("      , lc.HOME_ADDRESS \"homeAddress\" ")
		.append("      , lc.WORK_CORPORATION \"workCorporation\" ")
		.append("      , lc.WORK_ADDRESS \"workAddress\" ")
		.append("      , lc.WORK_TELEPHONE \"worTelephone\" ")
		.append("      , ai.MEMO \"memo\" ")
		.append("      , loan.RASIE_END_DATE \"rasieEndDate\" ")
		.append("  FROM BAO_T_LOAN_INFO loan ")
		.append(" INNER JOIN BAO_T_LOAN_CUST_INFO lc ON lc.ID = loan.RELATE_PRIMARY ")
		.append(" INNER JOIN BAO_T_LOAN_DETAIL_INFO ld ON ld.LOAN_ID = loan.ID ")
		.append(" LEFT JOIN BAO_T_LOAN_REBATE_INFO lr ON lr.REPAYMENT_METHOD = loan.REPAYMENT_METHOD ")
		.append(" AND lr.LOAN_TERM = loan.LOAN_TERM ")
		.append(" AND lr.PRODUCT_TYPE = loan.LOAN_TYPE ")
		.append(" LEFT JOIN BAO_T_AUDIT_INFO ai ON ai.RELATE_PRIMARY = loan.ID ")
		.append(" AND ai.APPLY_TYPE = '"+Constant.OPERATION_TYPE_60+"'")
		.append(" LEFT JOIN COM_T_USER u ON u.ID = loan.RECEIVE_USER ")
		.append(" WHERE loan.LOAN_STATUS IS NOT NULL ")
		;
		
		SqlCondition sqlCondition = new SqlCondition(sql, param)
		.addString("loanCode", "loan.LOAN_CODE")
		.addString("custName", "lc.CUST_NAME")
		.addString("mobile", "lc.MOBILE")
		.addString("credentialsCode", "lc.CREDENTIALS_CODE")
		.addString("loanTerm", "loan.LOAN_TERM")
		.addString("loanUnit", "loan.LOAN_UNIT")
		.addString("loanType", "loan.LOAN_TYPE")
		.addString("loanStatus", "loan.LOAN_STATUS")
		.addString("companyName", "loan.COMPANY_NAME")
		.addString("repaymentMethod", "loan.REPAYMENT_METHOD")
		.addBeginDate("createDateStart", "loan.APPLY_TIME")
		.addEndDate("createDateEnd", "loan.APPLY_TIME")
		.addBeginDate("pushDateStart", "loan.CREATE_DATE")
		.addEndDate("pushDateEnd", "loan.CREATE_DATE")
		.addString("loanDesc", "loan.LOAN_DESC")
		.addString("loanTitle", "loan.LOAN_TITLE")
		.addBeginDate("auditDateStart", "ai.AUDIT_TIME")
		.addEndDate("auditDateEnd", "ai.AUDIT_TIME")
		.addString("receiveUser", "u.USER_NAME")
		.addString("receiveStatus", "loan.RECEIVE_STATUS");
		if (!StringUtils.isEmpty(param.get("isBackStage")) && "借款信息".equals(param.get("isBackStage"))) {
			sqlCondition.addSql(" ORDER BY decode(loan.LOAN_STATUS,'待审核',1,'审核回退',2,'通过',3,'待发布',4,'募集中',5,'满标复核',6,'复核通过',7,'正常',8,'逾期',9,'提前结清',10,'已到期',11,'流标',12,'复核拒绝',13,'拒绝',14) , ")
			.addSql(" decode(loan.RECEIVE_STATUS,'未领取',1,'已领取',2) , loan.APPLY_TIME ");
		}else{
			sqlCondition.addSql(" and loan.LOAN_STATUS in ('募集中','满标复核','正常','提前结清','已到期')")
			.addSql(" ORDER BY decode(loan.LOAN_STATUS,'募集中',1,'满标复核',2,'正常',3,'提前结清',4,'已到期',5) ");
		}
		;
		
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}
	
	
	/**
	 * 借款信息统计查询
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>loanCode       :String:借款编号(可以为空)</tt><br>
     *      <tt>custName       :String:客户姓名(可以为空)</tt><br>
     *      <tt>mobile         :String:手机号码(可以为空)</tt><br>
     *      <tt>credentialsCode:String:证件号码(可以为空)</tt><br>
     *      <tt>loanTerm       :String:借款期限(可以为空)</tt><br>
     *      <tt>loanType       :String:借款类型(可以为空)</tt><br>
     *      <tt>loanStatus     :String:借款状态(可以为空)</tt><br>
     *      <tt>repaymentMethod:String:还款方式(可以为空)</tt><br>
     *      <tt>createDateStart:String:申请日期-区间头(可以为空)</tt><br>
     *      <tt>createDateEnd  :String:申请日期-区间末(可以为空)</tt><br>
     *      <tt>auditDateStart :String:审核日期-区间头(可以为空)</tt><br>
     *      <tt>auditDateEnd   :String:审核日期-区间末(可以为空)</tt><br>
     *      <tt>receiveUser    :String:领取人(可以为空)</tt><br>
     *      <tt>receiveStatus  :String:领取状态(可以为空)</tt><br>
	 * @return
     *      <tt>loanInfoCount       :String:审核笔数</tt><br>
     *      <tt>loanInfoAmountCount :String:审核金额</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryLoanInfoStatistics(Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT count(1) \"loanInfoCount\" ,sum(loan.LOAN_AMOUNT) \"loanInfoAmountCount\" ")
		.append("  FROM BAO_T_LOAN_INFO loan ")
		.append(" INNER JOIN BAO_T_LOAN_CUST_INFO lc ON lc.ID = loan.RELATE_PRIMARY ")
		.append(" INNER JOIN BAO_T_LOAN_DETAIL_INFO ld ON ld.LOAN_ID = loan.ID ")
		.append(" LEFT JOIN BAO_T_LOAN_REBATE_INFO lr ON lr.REPAYMENT_METHOD = loan.REPAYMENT_METHOD ")
		.append(" AND lr.LOAN_TERM = loan.LOAN_TERM ")
		.append(" AND lr.PRODUCT_TYPE = loan.LOAN_TYPE ")
		.append(" LEFT JOIN BAO_T_AUDIT_INFO ai ON ai.RELATE_PRIMARY = loan.ID ")
		.append(" AND ai.APPLY_TYPE = '"+Constant.OPERATION_TYPE_60+"'")
		.append(" LEFT JOIN COM_T_USER u ON u.ID = loan.RECEIVE_USER ")
		.append(" WHERE loan.LOAN_STATUS IS NOT NULL ")
		;
		SqlCondition sqlCondition = new SqlCondition(sql, param)
		.addString("loanCode", "loan.LOAN_CODE")
		.addString("custName", "lc.CUST_NAME")
		.addString("mobile", "lc.MOBILE")
		.addString("credentialsCode", "lc.CREDENTIALS_CODE")
		.addString("loanTerm", "loan.LOAN_TERM")
		.addString("loanUnit", "loan.LOAN_UNIT")
		.addString("loanType", "loan.LOAN_TYPE")
		.addString("loanStatus", "loan.LOAN_STATUS")
		.addString("repaymentMethod", "loan.REPAYMENT_METHOD")
		.addBeginDate("createDateStart", "loan.APPLY_TIME")
		.addEndDate("createDateEnd", "loan.APPLY_TIME")
		.addBeginDate("pushDateStart", "loan.CREATE_DATE")
		.addEndDate("pushDateEnd", "loan.CREATE_DATE")
		.addString("loanDesc", "loan.LOAN_DESC")
		.addString("loanTitle", "loan.LOAN_TITLE")
		.addBeginDate("auditDateStart", "ai.AUDIT_TIME")
		.addEndDate("auditDateEnd", "ai.AUDIT_TIME")
		.addString("receiveUser", "u.USER_NAME")
		.addString("receiveStatus", "loan.RECEIVE_STATUS");
		if ( !"借款信息".equals(param.get("isBackStage"))) {
			// if (!StringUtils.isEmpty(param.get("loanType")) && Constant.LOAN_PRODUCT_NAME_05.equals(param.get("loanType"))) {
			sqlCondition.addSql(" and loan.LOAN_STATUS in ('募集中','满标复核','正常','提前结清','已到期')");
		}
		
		return jdbcTemplate.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
	}
	/**
	 * 领取页面金额查询
	 */
	public Map<String, Object> queryLoanInfoReceives(Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT count(1) \"loanInfoCount\" ,sum(loan.LOAN_AMOUNT) \"loanInfoAmountCount\" ")
		.append("  FROM BAO_T_LOAN_INFO loan ")
		.append(" INNER JOIN BAO_T_LOAN_CUST_INFO lc ON lc.ID = loan.RELATE_PRIMARY ")
		.append(" INNER JOIN BAO_T_LOAN_DETAIL_INFO ld ON ld.LOAN_ID = loan.ID ")
		.append(" LEFT JOIN BAO_T_LOAN_REBATE_INFO lr ON lr.REPAYMENT_METHOD = loan.REPAYMENT_METHOD ")
		.append(" AND lr.LOAN_TERM = loan.LOAN_TERM ")
		.append(" AND lr.PRODUCT_TYPE = loan.LOAN_TYPE ")
		.append(" LEFT JOIN BAO_T_AUDIT_INFO ai ON ai.RELATE_PRIMARY = loan.ID ")
		.append(" AND ai.APPLY_TYPE = '"+Constant.OPERATION_TYPE_60+"'")
		.append(" WHERE loan.LOAN_STATUS IS NOT NULL ")
		.append(" AND loan.RECEIVE_USER = '"+param.get("userId")+"'")
		;
		SqlCondition sqlCondition = new SqlCondition(sql, param)
		.addString("loanCode", "loan.LOAN_CODE")
		.addString("custName", "lc.CUST_NAME")
		.addString("mobile", "lc.MOBILE")
		.addString("credentialsCode", "lc.CREDENTIALS_CODE")
		.addString("loanTerm", "loan.LOAN_TERM")
		.addString("loanUnit", "loan.LOAN_UNIT")
		.addString("loanType", "loan.LOAN_TYPE")
		.addString("loanStatus", "loan.LOAN_STATUS")
		.addString("repaymentMethod", "loan.REPAYMENT_METHOD")
		.addBeginDate("createDateStart", "loan.APPLY_TIME")
		.addEndDate("createDateEnd", "loan.APPLY_TIME")
		.addString("loanDesc", "loan.LOAN_DESC")
		.addString("loanTitle", "loan.LOAN_TITLE")
		.addBeginDate("auditDateStart", "ai.AUDIT_TIME")
		.addEndDate("auditDateEnd", "ai.AUDIT_TIME")
		.addString("receiveUser", "u.USER_NAME")
		.addString("receiveStatus", "loan.RECEIVE_STATUS");
		
		return jdbcTemplate.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
	}
	/**
	 * 借款信息详情
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>loanCode       :String:借款编号</tt><br>
     *      <tt>loanType       :String:借款类型</tt><br>
     *      <tt>loanDesc       :String:借款用途</tt><br>
     *      <tt>repaymentMethod:String:还款方式</tt><br>
     *      <tt>loanAmount     :String:借款金额</tt><br>
     *      <tt>loanTerm       :String:借款期限</tt><br>
     *      <tt>yearIrr        :String:借款利率 （详细表）</tt><br>
     *      <tt>rasieEndDate   :String:募集日期</tt><br>
     *      <tt>publishDate    :String:发布日期</tt><br>
     *      <tt>investStartDate:String:起息日期</tt><br>
     *      <tt>investEndDate  :String:到期日期</tt><br>
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryLoanBasicInfoByLoanId(
			Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT loan.ID \"loanId\" ")
		.append("      , loan.LOAN_CODE \"loanCode\" ")
		.append("      , loan.LOAN_TYPE \"loanType\" ")
		.append("      , loan.LOAN_DESC \"loanDesc\" ")
		.append("      , loan.REPAYMENT_METHOD \"repaymentMethod\" ")
		.append("      , loan.LOAN_AMOUNT \"loanAmount\" ")
		.append("      , loan.LOAN_TERM \"loanTerm\" ")
		.append("      , loan.LOAN_UNIT \"loanUnit\" ")
		.append("      , loan.RASIE_END_DATE \"rasieEndDate\" ")
		.append("      , loan.PUBLISH_DATE \"publishDate\" ")
		.append("      , loan.INVEST_START_DATE \"investStartDate\" ")
		.append("      , loan.INVEST_END_DATE \"investEndDate\" ")
		.append("      , round(ld.YEAR_IRR*100, 2) \"yearIrr\" ")
		.append("      , loan.LOAN_STATUS \"loanStatus\"")
		.append("      , loan.APPLY_TIME \"applyTime\"")
		.append("      , loan.CAR_TYPE \"carType\"")
		.append("      , loan.HOUSE_TYPE \"houseType\"")
		.append("      , loan.PROPERTY_RIGHT \"propertyRight\"")
		.append("      , loan.RASIE_DAYS \"rasieDays\"")
		.append("      , loan.CUST_ID \"custId\" ")
		.append("      , loan.PROTOCAL_TYPE \"protocolType\" ")
		.append("      , round(loan.MANAGE_RATE*100, 2) \"manageRate\" ")
		.append("      , round(loan.MONTHLY_MANAGE_RATE*100, 2) \"monthlyManageRate\" ")
		.append("      ,  nvl(trunc(round(loan.award_rate,8)*100,2),0) \"awardRate\" ")
		.append("      , loan.INVEST_MIN_AMOUNT \"investMinAmount\" ")
		.append("      , loan.INCREASE_AMOUNT \"increaseAmount\" ")
		.append("      , loan.LOAN_INFO \"loanInfo\" ")
		.append("      , loan.SERVICE_NAME \"serviceName\" ")
		.append("      , loan.SERVICE_CODE \"serviceCode\" ")
		.append("      , lr.FLAG \"loanFlag\" ")
		.append("      , loan.LOAN_TITLE \"loanTitle\" ")
		.append("      , loan.IS_ALLOW_AUTO_INVEST \"isAllowAutoInvest\" ")
		.append("      , loan.SEAT_TERM \"seatTerm\" ")
		.append("      , loan.newer_flag \"newerFlag\" ")//新手标
		.append(" 	   , nvl(loan.manage_expense_deal_type,'线下') \"manageExpenseDealType\" ")
		.append("      , loan.grant_type \"grantType\" ")
		.append("      , loan.invest_max_amount \"investMaxAmount\" ")
		.append("      , loan.debt_source_code \"debtSourceCode\" ")
		.append("      , loan.CHANNEL_FLAG \"channelFlag\" ")// 是否用于渠道
		.append("      , loan.company_name \"companyName\" ")
		.append("  FROM BAO_T_LOAN_INFO loan ")
		.append(" INNER JOIN BAO_T_LOAN_DETAIL_INFO ld ON ld.LOAN_ID = loan.ID ")
		.append("  LEFT JOIN BAO_T_LOAN_REBATE_INFO lr ON lr.REPAYMENT_METHOD = loan.REPAYMENT_METHOD ")
		.append("        AND lr.LOAN_TERM = loan.LOAN_TERM ")
		.append("        AND lr.PRODUCT_TYPE = loan.LOAN_TYPE ")
		.append(" WHERE loan.ID = ? ")
		;
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("loanId"));
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}
	
	/**
	 * 借款人信息
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>custName       :String:姓名</tt><br>
     *      <tt>custGender     :String:性别</tt><br>
     *      <tt>age            :String:年龄(根据身份证算)</tt><br>
     *      <tt>jobType        :String:职业类别</tt><br>
     *      <tt>custEducation  :String:学历</tt><br>
     *      <tt>marriageState  :String:婚否</tt><br>
     *      <tt>credentialsType:String:证件类型</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryLoanerInfoByLoanId(
			Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT loan.ID \"loanId\" ")
		.append("      , lc.CUST_NAME \"custName\" ")
		.append("      , lc.CUST_GENDER \"custGender\" ")
		.append("      , lc.CUST_AGE \"age\" ")
		.append("      , lc.JOB_TYPE \"jobType\" ")
		.append("      , lc.CUST_EDUCATION \"custEducation\" ")
		.append("      , lc.MARRIAGE \"marriageState\" ")
		.append("      , param.PARAMETER_NAME \"credentialsType\" ")
		.append("      , decode(lc.CREDENTIALS_CODE,NULL,'',lc.CREDENTIALS_CODE) \"credentialsCode\" ")
		.append("      , lc.HOME_ADDRESS \"homeAddress\" ")
		.append("      , lc.WORK_CORPORATION \"workCorporation\" ")
		.append("      , lc.WORK_YEAR \"workYear\" ")
		.append("      , lc.SALARY_TYPE \"salaryType\" ")
		.append("      , lc.WORK_ADDRESS \"workAddress\" ")
		.append("      , lc.WORK_TELEPHONE \"workTelephone\" ")
		.append("      , lc.BANK_NAME \"bankName\" ")
		.append("      , lc.CARD_NO \"cardNo\" ")
		.append("      , lc.MOBILE \"mobile\" ")
		.append("  FROM BAO_T_LOAN_INFO loan  ")
		.append(" INNER JOIN BAO_T_LOAN_CUST_INFO lc ON lc.ID = loan.RELATE_PRIMARY ")
		.append("  LEFT JOIN (SELECT \"TYPE\", TYPE_NAME, PARAMETER_NAME, VALUE FROM COM_T_PARAM WHERE TYPE = '5') param  ")
		.append("         ON param.VALUE = lc.CREDENTIALS_TYPE ")
		.append(" WHERE loan.ID = ? ")
		;
		
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("loanId"));
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}
	
	/**
	 * 散标投资列表查询ByApp
	 * @date 2016年11月25日下午16:25:59
	 * @author lyy
	 * @param param Map<String, Object>
     *      <tt>start      :String:起始值</tt><br>
     *      <tt>length     :String:长度</tt><br>
     *      <tt>orderBy    :String:排序字段</tt><br>
     *      <tt>loanType   :String:借款类型</tt><br>
     *      <tt>minTerm    :String:最小投标期限</tt><br>
     *      <tt>maxTerm    :String:最大投标期限</tt><br>
     *      <tt>minYearRate:String:最小年利率</tt><br>
     *      <tt>maxYearRate:String:最大年利率</tt><br>
     * @return ResultVo.data.data :List<Map<String,Object>></tt><br>
     *      <tt>disperseId     :String:散标主键</tt><br>
     *      <tt>disperseType   :String:散标名称 </tt><br>
     *      <tt>loanUse:借款用途 </tt><br>
     *      <tt>loanUserSex:性别 </tt><br>
     *      <tt>loanUserAge:年龄 </tt><br>
     *      <tt>loanUserCity:省市 </tt><br>
     *      <tt>description:说明             </tt><br>
     *      <tt>yearRate       :String:年化收益率</tt><br>
     *      <tt>typeTerm       :String:项目期限(月)</tt><br>
     *      <tt>remainAmount   :String:剩余金额</tt><br>
     *      <tt>investScale    :String:已投百分比</tt><br>
     *      <tt>investMinAmount:String:起投金额</tt><br>
     *      <tt>repaymentMethod:String:还款方式</tt><br>
     *      <tt>security       :String:安全保障</tt><br>
     *      <tt>disperseStatus :String:散标状态</tt><br>
	 */
	public Page<Map<String, Object>> queryDisperseList(Map<String, Object> param) {	
		StringBuilder sql = new StringBuilder()
		.append(" SELECT \"disperseId\",\"disperseType\",\"loanUse\",\"loanUserSex\",\"loanUserAge\",\"loanUserCity\" ")
		.append("  		,\"description\",\"yearRate\",\"typeTerm\",\"loanUnit\",\"loanAmount\",\"investMinAmount\" ")
		.append("     	,\"investMaxAmount\",\"increaseAmount\",\"publishDate\",\"remainAmount\",\"investScale\",\"alreadyInvestPeoples\"  ")
		.append("  		,\"repaymentMethod\",\"security\",\"disperseStatus\", \"rebateRatio\" ,\"loanTitle\", \"newerFlag\",\"isAddYearRate\",\"transferCondition\",\"rasieEndDate\" ")
		.append("       ,\"lastUpdateDate\", \"investStartDate\" ,\"awardRate\" ")
		.append("     from (       ")
		.append(" SELECT loan.ID \"disperseId\" ")
		.append("      , loan.LOAN_TYPE \"disperseType\" ")
		.append("      , loan.RASIE_END_DATE \"rasieEndDate\" ")
		.append("      , loan.last_update_date \"lastUpdateDate\" ")
		.append("      , loan.invest_start_date \"investStartDate\" ")
		.append("      , loan.LOAN_DESC \"loanUse\" ")
		.append("      , lc.CUST_GENDER \"loanUserSex\" ")
		.append("      , lc.CUST_AGE \"loanUserAge\" ")
		.append("      , lc.OPEN_PROVINCE || lc.OPEN_CITY \"loanUserCity\" ")
		.append("      , loan.MEMO \"description\" ")
		.append("      , nvl(ld.YEAR_IRR,0) \"yearRate\" ")
		.append("      , nvl(loan.LOAN_TERM,0) \"typeTerm\" ")
		.append("      , loan.LOAN_UNIT \"loanUnit\" ")
		.append("      , loan.LOAN_AMOUNT \"loanAmount\" ")
		.append("      , loan.INVEST_MIN_AMOUNT \"investMinAmount\" ")
		.append("      , loan.INVEST_MAX_AMOUNT \"investMaxAmount\" ")
		.append("      , loan.INCREASE_AMOUNT \"increaseAmount\" ")
		.append("      , loan.PUBLISH_DATE \"publishDate\" ")
		.append("      , TRUNC(loan.LOAN_AMOUNT,2) - nvl(TRUNC(pi.ALREADY_INVEST_AMOUNT,2),0) \"remainAmount\" ")
		.append("      , nvl(TRUNC(pi.ALREADY_INVEST_SCALE*100),0) \"investScale\" ")
		.append("      , pi.ALREADY_INVEST_PEOPLES \"alreadyInvestPeoples\" ")
		.append("      , loan.REPAYMENT_METHOD \"repaymentMethod\" ")
		.append("      , '' AS  \"security\" ")
		.append("      , loan.LOAN_STATUS \"disperseStatus\" ")
		.append("      , nvl(loan.REBATE_RATIO,0) \"rebateRatio\" ")
		.append("      , loan.LOAN_TITLE \"loanTitle\" ")
		.append("      , loan.newer_flag \"newerFlag\" ")
		.append("      , nvl(loan.award_rate,0) \"awardRate\" ")
//		.append(" 	   , CASE WHEN (loan.SEAT_TERM = -1 or loan.SEAT_TERM IS NULL) ")//端午加息活动 2017/5/25 SLCF-2852
//		.append("             and loan.REPAYMENT_METHOD = '等额本息'  ")
//		.append("             and loan.LOAN_STATUS = '募集中'  ")
//		.append("             and to_char(sysdate,'yyyy-MM-dd') <= "+Constant.DWHD_END_TIME+" ")
//	    .append("             and to_char(sysdate,'yyyy-MM-dd') >= "+Constant.DWHD_START_TIME+" ") 
//		.append("             then '是' else '否' end \"isAddYearRate\" ")
		.append(" 	   , CASE WHEN (loan.SEAT_TERM = -1 or loan.SEAT_TERM IS NULL) ")//六月加息活动 2017/6/15 - 2017/7/15 SLCF-3027
		.append("             and loan.REPAYMENT_METHOD = '等额本息'  ")
		.append("             and loan.LOAN_STATUS = '募集中'  ")
		.append("             and to_char(sysdate,'yyyy-MM-dd') >= ? ")
	    .append("             and to_char(sysdate,'yyyy-MM-dd') <= ? ") 
		.append("             then '是' else '否' end \"isAddYearRate\" ")
		.append("      , CASE WHEN loan.SEAT_TERM = -1 OR loan.SEAT_TERM IS NULL THEN '不允许转让' WHEN loan.SEAT_TERM = 0 THEN '可以立即转让' ELSE '出借'||loan.SEAT_TERM||'天即可转让' END \"transferCondition\" ")
		.append("  FROM BAO_T_LOAN_INFO loan ")
		.append(" INNER JOIN BAO_T_LOAN_CUST_INFO lc ON lc.ID = loan.RELATE_PRIMARY ")
		.append(" LEFT JOIN BAO_T_LOAN_DETAIL_INFO ld ON ld.LOAN_ID = loan.ID ")
		.append("  LEFT JOIN BAO_T_PROJECT_INVEST_INFO pi ON pi.LOAN_ID=loan.ID ")
		.append(" WHERE 1=1 ")
		//.append("   AND (SELECT instr(value, loan.LOAN_TYPE) FROM COM_T_PARAM WHERE TYPE='shanLinCaiFuSet' AND PARAMETER_NAME='优选项目列表可投产品') <= 0 ")
		//.append("   AND loan.LOAN_TYPE not in (" + getCanInvestProjectForList() +  ") ")
		.append("   AND (loan.loan_type in (" + getCanInvestProjectForList() +  ") or (loan.IS_RUN_AUTO_INVEST ='Y' and loan.loan_type not in (" + getCanInvestProjectForList() +  "))) ")
//		.append("   AND loan.LOAN_STATUS in ( '募集中','满标复核','正常','逾期','提前结清','已到期','流标 ') ")
		.append("   AND loan.LOAN_STATUS = '募集中' ")
		.append("   AND loan.newer_flag ='普通标' ")
        .append("   AND loan.CHANNEL_FLAG = '否' ")// 是否用于渠道
        .append("   AND (loan.SPECIAL_USERS_FLAG != '是' or loan.SPECIAL_USERS_FLAG is null) ")//优选项目列表不显示特殊用户标的
//		.append("  union all ")
//		.append("        select * from ")
//		.append("       	(")
//		.append(" 		  	SELECT loan.ID \"disperseId\"  ")
//		.append(" 		      , loan.LOAN_TYPE \"disperseType\"  ")
//		.append("      		  , loan.RASIE_END_DATE \"rasieEndDate\" ")
//		.append("    		  , loan.last_update_date \"lastUpdateDate\" ")
//		.append("     		  , loan.invest_start_date \"investStartDate\" ")
//		.append(" 		      , loan.LOAN_DESC \"loanUse\"  ")
//		.append(" 		      , lc.CUST_GENDER \"loanUserSex\"  ")
//		.append(" 		      , lc.CUST_AGE \"loanUserAge\"  ")
//		.append(" 		      , lc.OPEN_PROVINCE || lc.OPEN_CITY \"loanUserCity\"  ")
//		.append(" 		      , loan.MEMO \"description\"  ")
//		.append(" 		      , nvl(ld.YEAR_IRR,0) \"yearRate\"  ")
//		.append(" 		      , nvl(loan.LOAN_TERM,0) \"typeTerm\"  ")
//		.append(" 		      , loan.LOAN_UNIT \"loanUnit\"  ")
//		.append(" 		      , loan.LOAN_AMOUNT \"loanAmount\"  ")
//		.append(" 		      , loan.INVEST_MIN_AMOUNT \"investMinAmount\"  ")
//		.append(" 		      , loan.INVEST_MAX_AMOUNT \"investMaxAmount\"  ")
//		.append(" 		      , loan.INCREASE_AMOUNT \"increaseAmount\"  ")
//		.append(" 		      , loan.PUBLISH_DATE \"publishDate\"  ")
//		.append(" 		      , TRUNC(loan.LOAN_AMOUNT,2) - nvl(TRUNC(pi.ALREADY_INVEST_AMOUNT,2),0) \"remainAmount\"  ")
//		.append(" 		      , nvl(TRUNC(pi.ALREADY_INVEST_SCALE*100),0) \"investScale\"  ")
//		.append(" 		      , pi.ALREADY_INVEST_PEOPLES \"alreadyInvestPeoples\"  ")
//		.append(" 		      , loan.REPAYMENT_METHOD \"repaymentMethod\"  ")
//		.append(" 		      , '' AS  \"security\"  ")
//		.append(" 		      , loan.LOAN_STATUS \"disperseStatus\"  ")
//		.append(" 		      , nvl(loan.REBATE_RATIO,0) \"rebateRatio\"  ")
//		.append(" 		      , loan.LOAN_TITLE \"loanTitle\"  ")
//		.append(" 		      , loan.newer_flag \"newerFlag\"  ")
//		.append("      		  , nvl(loan.award_rate,0) \"awardRate\" ")
//		.append(" 	   		  , CASE WHEN (loan.SEAT_TERM = -1 or loan.SEAT_TERM IS NULL) ")//六月加息活动 2017/6/15 - 2017/7/15 SLCF-3027
//		.append("             and loan.REPAYMENT_METHOD = '等额本息'  ")
//		.append("             and loan.LOAN_STATUS = '募集中'  ")
//		.append("             and to_char(sysdate,'yyyy-MM-dd') >= ? ")
//	    .append("             and to_char(sysdate,'yyyy-MM-dd') <= ? ") 
//		.append("             then '是' else '否' end \"isAddYearRate\" ")
//		.append("           , CASE WHEN loan.SEAT_TERM = -1 OR loan.SEAT_TERM IS NULL THEN '不允许转让' WHEN loan.SEAT_TERM = 0 THEN '可以立即转让' ELSE '出借'||loan.SEAT_TERM||'天即可转让' END \"transferCondition\"  ")
//		.append("       FROM BAO_T_LOAN_INFO loan  ")
//		.append("      INNER JOIN BAO_T_LOAN_CUST_INFO lc ON lc.ID = loan.RELATE_PRIMARY  ")
//		.append("      LEFT JOIN BAO_T_LOAN_DETAIL_INFO ld ON ld.LOAN_ID = loan.ID  ")
//		.append("       LEFT JOIN BAO_T_PROJECT_INVEST_INFO pi ON pi.LOAN_ID=loan.ID  ")
//		.append("      WHERE 1=1  ")
//		//.append("        AND (SELECT instr(value, loan.LOAN_TYPE) FROM COM_T_PARAM WHERE TYPE='shanLinCaiFuSet' AND PARAMETER_NAME='优选项目列表可投产品') > 0  ")
//		.append("   AND loan.LOAN_TYPE in (" + getCanInvestProjectForList() +  ") ")
////		.append("        AND loan.LOAN_STATUS in ( '募集中','满标复核','正常','逾期','提前结清','已到期', '流标')  ")
//		.append("        AND loan.LOAN_STATUS in ( '募集中')  ")
//		.append("        AND loan.newer_flag !='新手标'  ")
//        .append("        AND (loan.CHANNEL_FLAG != '是' or loan.CHANNEL_FLAG is null) ")// 是否用于渠道
////		.append("        order by decode(loan.LOAN_STATUS,'募集中',1,99),loan.publish_date ") //性能损耗
//		.append(" 			)")
//		.append("        where  rownum <=15 ")		
        .append(" 	) ")
		.append(" where 1=1");
		
		List<Object> args = new ArrayList<Object>();
		//六月加息活动
		args.add(param.get("startTime"));
		args.add(param.get("endTime"));
//		args.add(param.get("startTime"));
//		args.add(param.get("endTime"));
		if(!StringUtils.isEmpty(param.get("loanType"))){
			sql.append(" and \"disperseType\" = ? ");
			args.add(param.get("loanType"));
		}
		if(!StringUtils.isEmpty(param.get("minTerm"))){
			sql.append(" and \"typeTerm\" *decode(\"loanUnit\", '天',1,'月',30,1) >= ? ");
			int minTerm = Integer.parseInt(param.get("minTerm").toString())*30;
			// 大于12月，前台传minTerm=12；maxTerm=120，要特殊处理
			int maxTerm = Integer.parseInt(param.get("maxTerm").toString());
			if(maxTerm == 120) {
				minTerm = 12*30 + 1;
			}
			args.add(minTerm);
		}
		if(!StringUtils.isEmpty(param.get("maxTerm"))){
			sql.append(" and \"typeTerm\" *decode(\"loanUnit\", '天',1,'月',30,1) <= ? ");
			int maxTerm = Integer.parseInt(param.get("maxTerm").toString())*30;
			// 小于1月，前台传minTerm=0；maxTerm=1，要特殊处理
			int minTerm = Integer.parseInt(param.get("minTerm").toString());
			if(minTerm == 0){
				maxTerm = 29;
			}
			args.add(maxTerm);
		}
		if(!StringUtils.isEmpty(param.get("minYearRate"))){
			sql.append(" and \"yearRate\" > ? ");
			args.add(param.get("minYearRate"));
		}
		if(!StringUtils.isEmpty(param.get("maxYearRate"))){
			sql.append(" and \"yearRate\" <= ? ");
			args.add(param.get("maxYearRate"));
		}

		// 排序
		sql.append(" order by ");
		if(!StringUtils.isEmpty(param.get("orderBy"))){
			if("利率".equals(param.get("orderBy"))){
				sql.append(" \"yearRate\" ");
				if("降".equals(param.get("isRise"))){
					sql.append(" desc ");
				}
				sql.append(" , ");
			}
			if("期限".equals(param.get("orderBy"))){
				sql.append(" \"typeTerm\"*decode(\"loanUnit\", '天',1,'月',30,1) ");
				if("降".equals(param.get("isRise"))){
					sql.append(" desc ");
				}
				sql.append(" , ");
			}
		}
		// 借款状态：待审核、通过、待发布、募集中、满标复核、正常、逾期、提前结清、已到期、流标、拒绝
//		sql.append(" loan.CREATE_DATE desc ");
		sql
//		.append(" decode(\"disperseStatus\" ,'募集中',5,'满标复核',6,'正常',8,'逾期',9,'提前结清',10,'已到期',11,'流标',12), ")
		.append("  decode(\"disperseStatus\" , '募集中', \"rasieEndDate\", sysdate) asc, ")
//		.append("  decode(\"disperseStatus\" , '满标复核', \"lastUpdateDate\", sysdate) desc, ")
//		.append("  decode(\"disperseStatus\" , '正常',  \"investStartDate\", sysdate) desc, ")
		.append("  decode(\"disperseStatus\" , '募集中', nvl(\"investScale\", 0), 1) desc, ")
		.append("  decode(\"disperseStatus\" , '募集中', \"typeTerm\" * ")
		.append("  decode(\"disperseStatus\" , '天', 1, '月', 30, 1), 1) ")
		;
		return repositoryUtil.queryForPageMap(sql.toString(), args.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}

    @Override
    public Page<Map<String, Object>> queryDisperseListInSpecialChannel(Map<String, Object> param) {   	
        StringBuilder sql = new StringBuilder()
                .append(" SELECT \"disperseId\",\"disperseType\",\"loanUse\",\"loanUserSex\",\"loanUserAge\",\"loanUserCity\" ")
                .append("  		,\"description\",\"yearRate\",\"typeTerm\",\"loanUnit\",\"loanAmount\",\"investMinAmount\" ")
                .append("     	,\"investMaxAmount\",\"increaseAmount\",\"publishDate\",\"remainAmount\",\"investScale\",\"alreadyInvestPeoples\"  ")
                .append("  		,\"repaymentMethod\",\"security\",\"disperseStatus\", \"rebateRatio\" ,\"loanTitle\", \"newerFlag\",\"isAddYearRate\",\"transferCondition\",\"rasieEndDate\" ")
                .append("       ,\"lastUpdateDate\", \"investStartDate\" ,\"awardRate\" ")
                .append("     from (       ")
                .append(" SELECT loan.ID \"disperseId\" ")
                .append("      , loan.LOAN_TYPE \"disperseType\" ")
                .append("      , loan.RASIE_END_DATE \"rasieEndDate\" ")
                .append("      , loan.last_update_date \"lastUpdateDate\" ")
                .append("      , loan.invest_start_date \"investStartDate\" ")
                .append("      , loan.LOAN_DESC \"loanUse\" ")
                .append("      , lc.CUST_GENDER \"loanUserSex\" ")
                .append("      , lc.CUST_AGE \"loanUserAge\" ")
                .append("      , lc.OPEN_PROVINCE || lc.OPEN_CITY \"loanUserCity\" ")
                .append("      , loan.MEMO \"description\" ")
                .append("      , nvl(ld.YEAR_IRR,0) \"yearRate\" ")
                .append("      , nvl(loan.LOAN_TERM,0) \"typeTerm\" ")
                .append("      , loan.LOAN_UNIT \"loanUnit\" ")
                .append("      , loan.LOAN_AMOUNT \"loanAmount\" ")
                .append("      , loan.INVEST_MIN_AMOUNT \"investMinAmount\" ")
                .append("      , loan.INVEST_MAX_AMOUNT \"investMaxAmount\" ")
                .append("      , loan.INCREASE_AMOUNT \"increaseAmount\" ")
                .append("      , loan.PUBLISH_DATE \"publishDate\" ")
                .append("      , TRUNC(loan.LOAN_AMOUNT,2) - nvl(TRUNC(pi.ALREADY_INVEST_AMOUNT,2),0) \"remainAmount\" ")
                .append("      , nvl(TRUNC(pi.ALREADY_INVEST_SCALE*100),0) \"investScale\" ")
                .append("      , pi.ALREADY_INVEST_PEOPLES \"alreadyInvestPeoples\" ")
                .append("      , loan.REPAYMENT_METHOD \"repaymentMethod\" ")
                .append("      , '' AS  \"security\" ")
                .append("      , loan.LOAN_STATUS \"disperseStatus\" ")
                .append("      , nvl(loan.REBATE_RATIO,0) \"rebateRatio\" ")
                .append("      , loan.LOAN_TITLE \"loanTitle\" ")
                .append("      , loan.newer_flag \"newerFlag\" ")
                .append("      , nvl(loan.award_rate,0) \"awardRate\" ")
//		.append(" 	   , CASE WHEN (loan.SEAT_TERM = -1 or loan.SEAT_TERM IS NULL) ")//端午加息活动 2017/5/25 SLCF-2852
//		.append("             and loan.REPAYMENT_METHOD = '等额本息'  ")
//		.append("             and loan.LOAN_STATUS = '募集中'  ")
//		.append("             and to_char(sysdate,'yyyy-MM-dd') <= "+Constant.DWHD_END_TIME+" ")
//	    .append("             and to_char(sysdate,'yyyy-MM-dd') >= "+Constant.DWHD_START_TIME+" ")
//		.append("             then '是' else '否' end \"isAddYearRate\" ")
                .append(" 	   , CASE WHEN (loan.SEAT_TERM = -1 or loan.SEAT_TERM IS NULL) ")//六月加息活动 2017/6/15 - 2017/7/15 SLCF-3027
                .append("             and loan.REPAYMENT_METHOD = '等额本息'  ")
                .append("             and loan.LOAN_STATUS = '募集中'  ")
                .append("             and to_char(sysdate,'yyyy-MM-dd') >= ? ")
                .append("             and to_char(sysdate,'yyyy-MM-dd') <= ? ")
                .append("             then '是' else '否' end \"isAddYearRate\" ")
                .append("      , CASE WHEN loan.SEAT_TERM = -1 OR loan.SEAT_TERM IS NULL THEN '不允许转让' WHEN loan.SEAT_TERM = 0 THEN '可以立即转让' ELSE '出借'||loan.SEAT_TERM||'天即可转让' END \"transferCondition\" ")
                .append("  FROM BAO_T_LOAN_INFO loan ")
                .append(" INNER JOIN BAO_T_LOAN_CUST_INFO lc ON lc.ID = loan.RELATE_PRIMARY ")
                .append(" LEFT JOIN BAO_T_LOAN_DETAIL_INFO ld ON ld.LOAN_ID = loan.ID ")
                .append("  LEFT JOIN BAO_T_PROJECT_INVEST_INFO pi ON pi.LOAN_ID=loan.ID ")
                .append(" WHERE 1=1 ")
                //.append("   AND (SELECT instr(value, loan.LOAN_TYPE) FROM COM_T_PARAM WHERE TYPE='shanLinCaiFuSet' AND PARAMETER_NAME='优选项目列表可投产品') <= 0 ")
                .append("   AND loan.LOAN_TYPE not in (" + getCanInvestProjectForList() +  ") ")
                .append("   AND loan.newer_flag !='新手标' ")
                .append("   AND loan.IS_RUN_AUTO_INVEST ='Y' ");
                if (CommonUtils.isEmpty(param.get("specialUsersFlag"))) {
                    sql.append("   AND loan.LOAN_STATUS in ( '募集中','满标复核','正常','逾期','提前结清','已到期','流标 ') ")
                    .append(" AND loan.CHANNEL_FLAG = '是' ");// 是否用于渠道
                } else if ("是".equals(CommonUtils.emptyToString(param.get("specialUsersFlag")))) {
                    sql.append("   AND loan.LOAN_STATUS in ('募集中') ")
                    .append(" AND loan.SPECIAL_USERS_FLAG = '是'");// 是否用于特殊用户
                }
                sql.append("  union all ")
                .append("        select * from ")
                .append("       	(")
                .append(" 		  	SELECT loan.ID \"disperseId\"  ")
                .append(" 		      , loan.LOAN_TYPE \"disperseType\"  ")
                .append("      		  , loan.RASIE_END_DATE \"rasieEndDate\" ")
                .append("    		  , loan.last_update_date \"lastUpdateDate\" ")
                .append("     		  , loan.invest_start_date \"investStartDate\" ")
                .append(" 		      , loan.LOAN_DESC \"loanUse\"  ")
                .append(" 		      , lc.CUST_GENDER \"loanUserSex\"  ")
                .append(" 		      , lc.CUST_AGE \"loanUserAge\"  ")
                .append(" 		      , lc.OPEN_PROVINCE || lc.OPEN_CITY \"loanUserCity\"  ")
                .append(" 		      , loan.MEMO \"description\"  ")
                .append(" 		      , nvl(ld.YEAR_IRR,0) \"yearRate\"  ")
                .append(" 		      , nvl(loan.LOAN_TERM,0) \"typeTerm\"  ")
                .append(" 		      , loan.LOAN_UNIT \"loanUnit\"  ")
                .append(" 		      , loan.LOAN_AMOUNT \"loanAmount\"  ")
                .append(" 		      , loan.INVEST_MIN_AMOUNT \"investMinAmount\"  ")
                .append(" 		      , loan.INVEST_MAX_AMOUNT \"investMaxAmount\"  ")
                .append(" 		      , loan.INCREASE_AMOUNT \"increaseAmount\"  ")
                .append(" 		      , loan.PUBLISH_DATE \"publishDate\"  ")
                .append(" 		      , TRUNC(loan.LOAN_AMOUNT,2) - nvl(TRUNC(pi.ALREADY_INVEST_AMOUNT,2),0) \"remainAmount\"  ")
                .append(" 		      , nvl(TRUNC(pi.ALREADY_INVEST_SCALE*100),0) \"investScale\"  ")
                .append(" 		      , pi.ALREADY_INVEST_PEOPLES \"alreadyInvestPeoples\"  ")
                .append(" 		      , loan.REPAYMENT_METHOD \"repaymentMethod\"  ")
                .append(" 		      , '' AS  \"security\"  ")
                .append(" 		      , loan.LOAN_STATUS \"disperseStatus\"  ")
                .append(" 		      , nvl(loan.REBATE_RATIO,0) \"rebateRatio\"  ")
                .append(" 		      , loan.LOAN_TITLE \"loanTitle\"  ")
                .append(" 		      , loan.newer_flag \"newerFlag\"  ")
                .append("      		  , nvl(loan.award_rate,0) \"awardRate\" ")
                .append(" 	   		  , CASE WHEN (loan.SEAT_TERM = -1 or loan.SEAT_TERM IS NULL) ")//六月加息活动 2017/6/15 - 2017/7/15 SLCF-3027
                .append("             and loan.REPAYMENT_METHOD = '等额本息'  ")
                .append("             and loan.LOAN_STATUS = '募集中'  ")
                .append("             and to_char(sysdate,'yyyy-MM-dd') >= ? ")
                .append("             and to_char(sysdate,'yyyy-MM-dd') <= ? ")
                .append("             then '是' else '否' end \"isAddYearRate\" ")
                .append("           , CASE WHEN loan.SEAT_TERM = -1 OR loan.SEAT_TERM IS NULL THEN '不允许转让' WHEN loan.SEAT_TERM = 0 THEN '可以立即转让' ELSE '出借'||loan.SEAT_TERM||'天即可转让' END \"transferCondition\"  ")
                .append("       FROM BAO_T_LOAN_INFO loan  ")
                .append("      INNER JOIN BAO_T_LOAN_CUST_INFO lc ON lc.ID = loan.RELATE_PRIMARY  ")
                .append("      LEFT JOIN BAO_T_LOAN_DETAIL_INFO ld ON ld.LOAN_ID = loan.ID  ")
                .append("       LEFT JOIN BAO_T_PROJECT_INVEST_INFO pi ON pi.LOAN_ID=loan.ID  ")
                .append("      WHERE 1=1  ")
                //.append("        AND (SELECT instr(value, loan.LOAN_TYPE) FROM COM_T_PARAM WHERE TYPE='shanLinCaiFuSet' AND PARAMETER_NAME='优选项目列表可投产品') > 0  ")
                .append("      AND loan.LOAN_TYPE in (" + getCanInvestProjectForList() +  ") ")
                .append("        AND loan.newer_flag !='新手标'  ");
                if (CommonUtils.isEmpty(param.get("specialUsersFlag"))) {
                    sql.append(" AND loan.LOAN_STATUS in ( '募集中','满标复核','正常','逾期','提前结清','已到期','流标 ') ")
                       .append(" AND loan.CHANNEL_FLAG = '是' ");// 是否用于渠道
                } else if ("是".equals(CommonUtils.emptyToString(param.get("specialUsersFlag")))) {
                    sql.append(" AND loan.LOAN_STATUS in ('募集中') ")
                       .append(" AND loan.SPECIAL_USERS_FLAG = '是'");// 是否用于特殊用户
                }
                sql.append("        order by decode(loan.LOAN_STATUS,'募集中',1,99),loan.publish_date ")
                .append(" 			)")
                .append("        where  rownum <=(SELECT value FROM COM_T_PARAM WHERE TYPE='shanLinCaiFuSet' AND PARAMETER_NAME='优选项目列表可投产品个数') ")
                .append(" 	) ")
                .append(" where 1=1");

        List<Object> args = new ArrayList<Object>();
        //六月加息活动
        args.add(param.get("startTime"));
        args.add(param.get("endTime"));
        args.add(param.get("startTime"));
        args.add(param.get("endTime"));
        if(!StringUtils.isEmpty(param.get("loanType"))){
            sql.append(" and \"disperseType\" = ? ");
            args.add(param.get("loanType"));
        }
        if(!StringUtils.isEmpty(param.get("minTerm"))){
            sql.append(" and \"typeTerm\" *decode(\"loanUnit\", '天',1,'月',30,1) >= ? ");
            int minTerm = Integer.parseInt(param.get("minTerm").toString())*30;
            // 大于12月，前台传minTerm=12；maxTerm=120，要特殊处理
            int maxTerm = Integer.parseInt(param.get("maxTerm").toString());
            if(maxTerm == 120) {
                minTerm = 12*30 + 1;
            }
            args.add(minTerm);
        }
        if(!StringUtils.isEmpty(param.get("maxTerm"))){
            sql.append(" and \"typeTerm\" *decode(\"loanUnit\", '天',1,'月',30,1) <= ? ");
            int maxTerm = Integer.parseInt(param.get("maxTerm").toString())*30;
            // 小于1月，前台传minTerm=0；maxTerm=1，要特殊处理
            int minTerm = Integer.parseInt(param.get("minTerm").toString());
            if(minTerm == 0){
                maxTerm = 29;
            }
            args.add(maxTerm);
        }
        if(!StringUtils.isEmpty(param.get("minYearRate"))){
            sql.append(" and \"yearRate\" > ? ");
            args.add(param.get("minYearRate"));
        }
        if(!StringUtils.isEmpty(param.get("maxYearRate"))){
            sql.append(" and \"yearRate\" <= ? ");
            args.add(param.get("maxYearRate"));
        }

        // 排序
        sql.append(" order by ");
        if(!StringUtils.isEmpty(param.get("orderBy"))){
            if("利率".equals(param.get("orderBy"))){
                sql.append(" \"yearRate\" ");
                if("降".equals(param.get("isRise"))){
                    sql.append(" desc ");
                }
                sql.append(" , ");
            }
            if("期限".equals(param.get("orderBy"))){
                sql.append(" \"typeTerm\"*decode(\"loanUnit\", '天',1,'月',30,1) ");
                if("降".equals(param.get("isRise"))){
                    sql.append(" desc ");
                }
                sql.append(" , ");
            }
        }
        // 借款状态：待审核、通过、待发布、募集中、满标复核、正常、逾期、提前结清、已到期、流标、拒绝
//		sql.append(" loan.CREATE_DATE desc ");
        sql.append(" decode(\"disperseStatus\" ,'募集中',5,'满标复核',6,'正常',8,'逾期',9,'提前结清',10,'已到期',11,'流标',12), ")
                .append("  decode(\"disperseStatus\" , '募集中', \"rasieEndDate\", sysdate) asc, ")
                .append("  decode(\"disperseStatus\" , '满标复核', \"lastUpdateDate\", sysdate) desc, ")
                .append("  decode(\"disperseStatus\" , '正常',  \"investStartDate\", sysdate) desc, ")
                .append("  decode(\"disperseStatus\" , '募集中', nvl(\"investScale\", 0), 1) desc, ")
                .append("  decode(\"disperseStatus\" , '募集中', \"typeTerm\" * ")
                .append("  decode(\"disperseStatus\" , '天', 1, '月', 30, 1), 1) ")
        ;
        return repositoryUtil.queryForPageMap(sql.toString(), args.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
    }

    public List<Map<String, Object>> queryDisperseListForJob(Map<String, Object> param) {
    	
    	
    	
		StringBuilder sql = new StringBuilder()
		.append(" SELECT loan.ID \"disperseId\" ")
//		.append("      , loan.LOAN_TYPE \"disperseType\" ")
//		.append("      , loan.LOAN_DESC \"loanUse\" ")
//		.append("      , lc.CUST_GENDER \"loanUserSex\" ")
//		.append("      , lc.CUST_AGE \"loanUserAge\" ")
//		.append("      , lc.OPEN_PROVINCE || lc.OPEN_CITY \"loanUserCity\" ")
//		.append("      , loan.MEMO \"description\" ")
//		.append("      , nvl(ld.YEAR_IRR,0) \"yearRate\" ")
//		.append("      , nvl(loan.LOAN_TERM,0) \"typeTerm\" ")
//		.append("      , loan.LOAN_UNIT \"loanUnit\" ")
//		.append("      , loan.LOAN_AMOUNT \"loanAmount\" ")
//		.append("      , loan.INVEST_MIN_AMOUNT \"investMinAmount\" ")
//		.append("      , loan.INVEST_MAX_AMOUNT \"investMaxAmount\" ")
//		.append("      , loan.INCREASE_AMOUNT \"increaseAmount\" ")
//		.append("      , loan.PUBLISH_DATE \"publishDate\" ")
		.append("      , TRUNC(loan.LOAN_AMOUNT,2) - nvl(TRUNC(pi.ALREADY_INVEST_AMOUNT,2),0) \"remainAmount\" ")
		.append("      , nvl(TRUNC(pi.ALREADY_INVEST_SCALE*100),0) \"investScale\" ")
//		.append("      , pi.ALREADY_INVEST_PEOPLES \"alreadyInvestPeoples\" ")
//		.append("      , loan.REPAYMENT_METHOD \"repaymentMethod\" ")
//		.append("      , '' AS  \"security\" ")
//		.append("      , loan.LOAN_STATUS \"disperseStatus\" ")
//		.append("      , nvl(loan.REBATE_RATIO,0) \"rebateRatio\" ")
//		.append("      , loan.LOAN_TITLE \"loanTitle\" ")
//		.append("      , CASE WHEN loan.SEAT_TERM = -1 OR loan.SEAT_TERM IS NULL THEN '不允许转让' WHEN loan.SEAT_TERM = 0 THEN '可以立即转让' ELSE '持有'||loan.SEAT_TERM||'天即可转让' END \"transferCondition\" ")
		.append("  FROM BAO_T_LOAN_INFO loan ")
		.append(" INNER JOIN BAO_T_LOAN_CUST_INFO lc ON lc.ID = loan.RELATE_PRIMARY ")
		.append(" LEFT JOIN BAO_T_LOAN_DETAIL_INFO ld ON ld.LOAN_ID = loan.ID ")
		.append("  LEFT JOIN BAO_T_PROJECT_INVEST_INFO pi ON pi.LOAN_ID=loan.ID ")
		.append(" WHERE 1=1 ")
		.append("   AND loan.LOAN_STATUS = '募集中' ")
		.append("   AND loan.IS_ALLOW_AUTO_INVEST = '"+Constant.IS_ALLOW_AUTO_INVEST_01+"' ")
//		.append("   AND loan.loan_type != '善真贷' ")
	    //.append("   AND (SELECT instr(value, loan.LOAN_TYPE) FROM COM_T_PARAM WHERE TYPE='shanLinCaiFuSet' AND PARAMETER_NAME='优选项目列表可投产品') <= 0 ")
		.append("      AND loan.LOAN_TYPE not in (" + getCanInvestProjectForList() +  ") ")
		;
		
		List<Object> args = new ArrayList<Object>();
		@SuppressWarnings("unchecked")
		List<Integer> seatTerms = (List<Integer>) param.get("seatTerm");
		if(seatTerms != null && seatTerms.size() > 0) {
			
//			if(seatTerm == -1){// 不可转让
//				sql.append(" and loan.SEAT_TERM = ?  ");
//				args.add(seatTerm);
//			} else {
//				sql.append(" and loan.SEAT_TERM != -1  ");
//			}
			
			sql.append(" and ( ");
			for (int i = 0; i < seatTerms.size(); i++) {
				int seatTerm = seatTerms.get(i);
				if(i != 0){
					sql.append(" OR ");
				}
				if(seatTerm == -1){
					sql.append(" loan.SEAT_TERM = -1 ");
				} else {
//					sql.append(" loan.SEAT_TERM = ? ");
					sql.append(" loan.SEAT_TERM != -1 ");
				}
//				args.add(seatTerm);
			}
			sql.append(" ) ");
		}
		
		if(!StringUtils.isEmpty(param.get("limitedTermMin"))){
			sql.append(" and loan.LOAN_TERM*decode(loan.LOAN_UNIT, '天',1,'月',30,1) >= ? ");
			int days = 0;
			if (!StringUtils.isEmpty(param.get("loanUnit"))&& "天".equals(param.get("loanUnit").toString())) {
			   days = Integer.parseInt(param.get("limitedTermMin").toString());
			}else if(!StringUtils.isEmpty(param.get("loanUnit"))&& "月".equals(param.get("loanUnit").toString())){
				days = Integer.parseInt(param.get("limitedTermMin").toString())*30;
			}
			args.add(days);
		}
		if(!StringUtils.isEmpty(param.get("maxTerm"))){
			sql.append(" and loan.LOAN_TERM*decode(loan.LOAN_UNIT, '天',1,'月',30,1) <= ? ");
			int days = 0;
			if (!StringUtils.isEmpty(param.get("loanUnit"))&& "天".equals(param.get("loanUnit").toString())) {
			   days = Integer.parseInt(param.get("maxTerm").toString());
			}else if(!StringUtils.isEmpty(param.get("loanUnit"))&& "月".equals(param.get("loanUnit").toString())){
			   days = Integer.parseInt(param.get("maxTerm").toString())*30;
			}
			args.add(days);
		}
		if(!StringUtils.isEmpty(param.get("minYearRate"))){
			sql.append(" and ld.YEAR_IRR >= ? ");
			args.add(param.get("minYearRate"));
		}
		if(!StringUtils.isEmpty(param.get("limitedYearRateMax"))){
			sql.append(" and ld.YEAR_IRR <= ? ");
			args.add(param.get("limitedYearRateMax"));
		}
		
		if(!StringUtils.isEmpty(param.get("repaymentMethod"))){
			String repaymentMethodSplit = (String) param.get("repaymentMethod");
			String[] repaymentMethodList = repaymentMethodSplit.split(",");
			sql.append(" and ( ");
			for (int i = 0; i < repaymentMethodList.length; i++) {
				if (i == 0) {
					sql.append(" loan.repayment_method = ? ");
				} else {
					sql.append(" OR loan.repayment_method = ? ");
				}
				args.add(repaymentMethodList[i]);
			}
			sql.append(" ) ");
		}

		sql.append(" order by ")
		.append(" \"remainAmount\" asc ")
		.append(" ,\"investScale\" desc ")
		.append("  , loan.RASIE_END_DATE asc ")
//		.append("  , nvl(pi.ALREADY_INVEST_SCALE desc ")
//		.append("  , loan.LOAN_TERM * decode(loan.LOAN_UNIT, '天', 1, '月', 30, 1), 1) ")
		;
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}
	
	/**
	 * 散标投资详情ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>disperseId:String:散标主键</tt><br>
	 * @return 
	 * @return 
	 * @return
     *      <tt>disperseId       :String:散标主键</tt><br>
     *      <tt>disperseType     :String:散标名称</tt><br>
     *      <tt>loanUse          :String:借款用途</tt><br>
     *      <tt>loanUserSex      :String:性别</tt><br>
     *      <tt>loanUserAge      :String:年龄</tt><br>
     *      <tt>loanUserCity     :String:省市</tt><br>
     *      <tt>remainTime       :String:剩余时间</tt><br>
     *      <tt>description      :String:说明</tt><br>
     *      <tt>yearRate         :String:年化收益率</tt><br>
     *      <tt>typeTerm         :String:项目期限(月)</tt><br>
     *      <tt>totalAmount      :String:项目总额</tt><br>
     *      <tt>remainAmount     :String:剩余金额</tt><br>
     *      <tt>investScale      :String:已投百分比</tt><br>
     *      <tt>investMinAmount  :String:起投金额</tt><br>
     *      <tt>increaseAmount   :String:递增金额</tt><br>
     *      <tt>repaymentMethod  :String:还款方式</tt><br>
     *      <tt>loanNo           :String:借款编号</tt><br>
     *      <tt>transferCondition:String:转让条件</tt><br>
     *      <tt>security         :String:安全保障</tt><br>
     *      <tt>disperseStatus   :String:散标状态</tt><br>
     *      <tt>publishDate      :String:发布借款日期</tt><br>
     *      <tt>interestStartDate:String:开始计息日期</tt><br>
     *      <tt>expireDate       :String:借款到期日期</tt><br>
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryDisperseDetail(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT loan.ID \"disperseId\" ")
		.append("      , loan.LOAN_TYPE \"disperseType\" ")
		.append("      , loan.LOAN_DESC \"loanUse\" ")
		.append("      , lc.CUST_GENDER \"loanUserSex\" ")
		.append("      , lc.CUST_AGE \"loanUserAge\" ")
		.append("      , lc.OPEN_PROVINCE||lc.OPEN_CITY \"loanUserCity\" ")
		.append("      , loan.RASIE_END_DATE AS \"rasieEndDate\" ")
		.append("      , decode(loan.LOAN_TYPE, '信用贷', '借款描述：借款人为某公司员工，'|| lc.CUST_GENDER ||'，'||lc.cust_age||'岁，需要借款'||loan.loan_amount||'元，用于'||loan.loan_desc||'；以个人薪资为第一还款来源，以'||loan.repayment_method||'的方式进行还款。', '') \"description\" ")
		.append("      , nvl(ld.YEAR_IRR,0) \"yearRate\" ")
		.append("      , nvl(loan.LOAN_TERM,0) \"typeTerm\" ")
		.append("      , loan.LOAN_UNIT \"loanUnit\" ")
		.append("      , TRUNC(loan.LOAN_AMOUNT,2) \"totalAmount\" ")
		.append("      , TRUNC(loan.LOAN_AMOUNT,2) - nvl(TRUNC(pi.ALREADY_INVEST_AMOUNT,2),0) \"remainAmount\" ")
		.append("      , nvl(TRUNC(pi.ALREADY_INVEST_SCALE*100),0) \"investScale\"  ")
		.append("      , nvl(pi.ALREADY_INVEST_PEOPLES,0) \"alreadyInvestPeoples\"  ")
		.append("      , nvl(loan.INVEST_MIN_AMOUNT,0) \"investMinAmount\" ")
		.append("      , nvl(loan.INCREASE_AMOUNT,0) \"increaseAmount\" ")
		.append("      , loan.REPAYMENT_METHOD \"repaymentMethod\" ")
		.append("      , loan.LOAN_CODE \"loanNo\" ")
		.append("      , loan.SEAT_TERM  \"seatTerm\" ")
		.append("      , '' AS \"transferCondition\" ")
		.append("      , '' AS \"security\" ")
		.append("      , loan.LOAN_STATUS \"disperseStatus\" ")
		.append("      , loan.PUBLISH_DATE \"publishDate\" ")
		.append("      , loan.INVEST_START_DATE \"interestStartDate\" ")
		.append("      , loan.INVEST_END_DATE \"expireDate\" ")
		.append("      , loan.LOAN_INFO \"loanInfo\" ")
		.append("      , loan.LOAN_TITLE \"loanTitle\" ")
		.append("      , loan.PROTOCAL_TYPE \"protocolType\" ")
		.append("      , lr.FLAG \"loanFlag\" ")
		.append("      , loan.CHANNEL_FLAG \"channelFlag\" ")
		.append("      , CASE WHEN loan.SEAT_TERM = -1 OR loan.SEAT_TERM IS NULL THEN '不允许转让' WHEN loan.SEAT_TERM = 0 THEN '可以立即转让' ELSE '出借'||loan.SEAT_TERM||'天即可转让' END \"transferCondition\" ")
		.append("      , CASE WHEN loan.LOAN_TYPE='善转贷' then '《债权转让及回购协议》' when loan.LOAN_TYPE='善融贷' then '《应收账款转让协议》' else '《借款与服务协议》' end \"protocolName\" ")
		.append("      , loan.newer_flag \"newerFlag\" ")
		.append("      , loan.invest_max_amount \"investMaxAmount\" ")
		.append("      , nvl(loan.award_rate,0) \"awardRate\" ")
		//.append("      , CASE WHEN (SELECT instr(value, loan.LOAN_TYPE) FROM COM_T_PARAM WHERE TYPE='shanLinCaiFuSet' AND PARAMETER_NAME='可投产品') > 0 then '否' else '否' end \"reseverFlag\" ")
		.append("      , '否' \"reseverFlag\" ")
		.append(" FROM BAO_T_LOAN_INFO loan ")
		.append(" INNER JOIN BAO_T_LOAN_CUST_INFO lc ON lc.ID = loan.RELATE_PRIMARY ")
		.append(" LEFT JOIN BAO_T_LOAN_DETAIL_INFO ld ON ld.LOAN_ID = loan.ID ")
		.append("  LEFT JOIN BAO_T_PROJECT_INVEST_INFO pi ON pi.LOAN_ID=loan.ID ")
		.append("  LEFT JOIN BAO_T_LOAN_REBATE_INFO lr ON lr.REPAYMENT_METHOD = loan.REPAYMENT_METHOD ")
		.append("        AND lr.LOAN_TERM = loan.LOAN_TERM ")
		.append("        AND lr.PRODUCT_TYPE = loan.LOAN_TYPE ")
		.append(" WHERE loan.ID = ? ")
		;
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("disperseId"));
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}

	/**
	 * 审核日志
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>data       :String:List<Map<String,Object>>（BAO_T_LOG_INFO）</tt><br>
     *      <tt>auditDate  :String:审核时间</tt><br>
     *      <tt>auditUser  :String:审核人员</tt><br>
     *      <tt>auditStatus:String:审核结果</tt><br>
     *      <tt>auditMemo  :String:审核备注</tt><br>
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryLoanAuditInfoByLoanId(
			Map<String, Object> params) {
		
		StringBuffer sql = new StringBuffer()
		.append("  SELECT log.ID \"auditId\", log.CREATE_DATE \"auditDate\", CASE WHEN c.CUST_NAME IS NOT NULL THEN c.CUST_NAME ELSE u.USER_NAME END \"auditUser\", log.OPER_AFTER_CONTENT \"auditStatus\", log.MEMO \"auditMemo\" ")
		.append("    FROM BAO_T_LOG_INFO log ")
		.append("    LEFT JOIN BAO_T_CUST_INFO c ON c.ID = log.OPER_PERSON ")
		.append("    LEFT JOIN COM_T_USER u ON u.ID = log.OPER_PERSON ")
		.append("   WHERE 1=1 ")
		.append("     AND log.RELATE_TYPE = ? ")
		.append("     AND log.RELATE_PRIMARY = ? ")
		.append("     AND log.LOG_TYPE = ? ")
		.append("  ORDER BY log.CREATE_DATE DESC ");
		
		List<Object> args = new ArrayList<Object>();
		args.add(Constant.TABLE_BAO_T_LOAN_INFO);
		args.add(params.get("loanId"));
		args.add(Constant.OPERATION_TYPE_60);
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}

	/**
	 * 还款计划
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>expectRepaymentDate :String:还款日期(yyyy-MM-dd)</tt><br>
     *      <tt>repaymentStatus     :String:还款状态</tt><br>
     *      <tt>repaymentTotalAmount:String:应还本息和</tt><br>
     *      <tt>repaymentPrincipal  :String:应还本金</tt><br>
     *      <tt>repaymentInterest   :String:应还利息</tt><br>
     *      <tt>penaltyAmount       :String:提前还款补偿金</tt><br>
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryRepaymentPlanByLoanId(
			Map<String, Object> params) {
		StringBuffer sql = new StringBuffer()
		.append(" SELECT to_date(rl.EXPECT_REPAYMENT_DATE, 'yyyy-MM-dd') \"expectRepaymentDate\" ")
		.append("      , rl.REPAYMENT_STATUS \"repaymentStatus\" ")
		.append("      , rl.REPAYMENT_TOTAL_AMOUNT \"repaymentTotalAmount\" ")
		.append("      , rl.REPAYMENT_PRINCIPAL \"repaymentPrincipal\" ")
		.append("      , rl.REPAYMENT_INTEREST \"repaymentInterest\" ")
		.append("      , rl.PENALTY_AMOUNT \"penaltyAmount\" ")
		.append("      , 0 as  \"punishPayment\"  ")
		.append("      , rl.CURRENT_TERM as  \"currentTerm\"  ")
		.append("  FROM BAO_T_REPAYMENT_PLAN_INFO rl ")
		.append(" WHERE rl.LOAN_ID = ? ")
		.append(" order by rl.EXPECT_REPAYMENT_DATE ")
		;
		
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("loanId"));
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}

	/**
	 * 投资记录
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>data        :String:List<Map<String,Object>></tt><br>
     *      <tt>custName    :String:投资人</tt><br>
     *      <tt>investAmount:String:投资金额（元）</tt><br>
     *      <tt>investDate  :String:投资日期</tt><br>
	 * @throws SLException
	 */
	public Page<Map<String, Object>> queryInvestInfoByLoanId(
			Map<String, Object> params) {
		StringBuffer sql = new StringBuffer()
		.append(" SELECT cust.CUST_NAME \"custName\" ")
		.append("      , TRUNC(invest.INVEST_AMOUNT,2) \"investAmount\" ")
		.append("      , invest.CREATE_DATE \"investDate\" ")
		.append("  FROM BAO_T_INVEST_INFO invest ")
		.append(" INNER JOIN BAO_T_CUST_INFO cust ON cust.ID = invest.CUST_ID ")
		.append(" WHERE invest.LOAN_ID = ? ")
		.append(" ORDER BY invest.CREATE_DATE desc ")
		;
		
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("loanId"));
		return repositoryUtil.queryForPageMap(sql.toString(), args.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}
	
	/**
	 * 转让记录APP
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>data             :String:List<Map<String,Object>></tt><br>
     *      <tt>receiveCustName  :String:受让人</tt><br>
     *      <tt>tradeValue:String:受让债权价值（元）</tt><br>
     *      <tt>tradeAmount      :String:受让价格（元）</tt><br>
     *      <tt>senderCustName   :String:转让人</tt><br>
     *      <tt>transferDate     :String:转让日期</tt><br>
	 * @throws SLException
	 */
	public Page<Map<String, Object>> queryLoanTransferByLoanId(
			Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT decode(receiveCust.MOBILE,NULL,'',substr(receiveCust.MOBILE,1,3)||'****'||substr(receiveCust.MOBILE,-4)) \"receiveCustName\" ")
		.append("      , decode(senderCust.MOBILE,NULL ,'',substr(senderCust.MOBILE,1,3) ||'****'||substr(senderCust.MOBILE ,-4)) \"senderCustName\" ")
//		.append("      , trunc(lt.TRADE_VALUE,2) \"tradeValue\" ")
//		.append("      , trunc(lt.TRADE_AMOUNT,2) \"tradeAmount\" ")
		.append("      , case when lta.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then trunc(lt.TRADE_VALUE,2) else TRUNC(lt.TRADE_PRINCIPAL, 2) end  AS \"tradeValue\"  ")
		.append("      , case when lta.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then trunc(lt.TRADE_AMOUNT,2) else TRUNC(lt.TRADE_PRINCIPAL * lta.REDUCED_SCALE, 2) end AS \"tradeAmount\"  ")
		.append("      , lt.CREATE_DATE \"transferDate\" ")
		.append("      , case when b.invest_source = 'auto' then '自动投标' when b.invest_source = 'reserveinvest' then '优先投' else '手动投标' end \"investSource\" ")
		.append("   FROM BAO_T_LOAN_TRANSFER lt ")
		.append("   INNER JOIN BAO_T_LOAN_TRANSFER_APPLY lta ON lta.ID = lt.TRANSFER_APPLY_ID ")
		.append("   INNER JOIN BAO_T_CUST_INFO receiveCust ON receiveCust.ID = lt.RECEIVE_CUST_ID ")
		.append("   INNER JOIN BAO_T_CUST_INFO senderCust ON senderCust.ID = lt.SENDER_CUST_ID ")
		.append("          INNER JOIN BAO_T_WEALTH_HOLD_INFO wh ON wh.ID = lt.RECEIVE_HOLD_ID  ")
		.append("          INNER JOIN BAO_T_INVEST_INFO invest ON invest.INVEST_STATUS IN ('收益中', '已转让', '已到期', '提前结清')   ")
		.append(" 		     AND invest.LOAN_ID IS NOT NULL  ")
		.append(" 		     AND invest.ID = wh.INVEST_ID  ")
		.append("         Inner Join bao_t_invest_detail_info b on b.invest_id = invest.id ")
		.append("  WHERE 1=1 ");
		
		List<Object> objList = new ArrayList<Object>();
		objList.add((String)params.get("onlineTime"));
		objList.add((String)params.get("onlineTime"));
		
		SqlCondition sqlCondition = new SqlCondition(sql, params, objList);
		sqlCondition.addString("loanId", "lt.SENDER_LOAN_ID")
					.addString("transferApplyId", "lt.TRANSFER_APPLY_ID")
					.addSql(" ORDER BY lt.CREATE_DATE DESC ");
		
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}

	/**
	 * 借款人详情ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>disperseId:String:散标主键</tt><br>
	 * @return
     *      <tt>name     :String:姓名</tt><br>
     *      <tt>sex      :String:性别</tt><br>
     *      <tt>age      :String:年龄</tt><br>
     *      <tt>marriage :String:婚否</tt><br>
     *      <tt>education:String:学历</tt><br>
     *      <tt>jobType  :String:职业类型</tt><br>
     *      <tt>cardType :String:证件类型</tt><br>
     *      <tt>cardNo   :String:证件号码</tt><br>
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryDisperseLoanUser(
			Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT loan.ID \"loanId\" ")
//		.append("      , lc.CUST_NAME \"name\" ")
		.append("      , decode(lc.CUST_NAME,NULL, '', substr(lc.CUST_NAME,1,1)||'**')  \"name\" ")
		.append("      , lc.CUST_GENDER \"sex\" ")
		.append("      , lc.CUST_AGE \"age\" ")
		.append("      , lc.JOB_TYPE \"jobType\" ")
		.append("      , lc.CUST_EDUCATION \"education\" ")
		.append("      , lc.MARRIAGE \"marriage\" ")
		.append("      , param.PARAMETER_NAME \"cardType\" ")
		.append("      , decode(lc.CREDENTIALS_CODE,NULL,'',substr(lc.CREDENTIALS_CODE,1,2)||'******' || substr(lc.CREDENTIALS_CODE,-2)) \"cardNo\" ")
		.append("  FROM BAO_T_LOAN_INFO loan  ")
		.append(" INNER JOIN BAO_T_LOAN_CUST_INFO lc ON lc.ID = loan.RELATE_PRIMARY ")
		.append("  LEFT JOIN (SELECT \"TYPE\", TYPE_NAME, PARAMETER_NAME, VALUE FROM COM_T_PARAM WHERE TYPE = '5') param  ")
		.append("         ON param.VALUE = lc.CREDENTIALS_TYPE ")
		.append(" WHERE loan.ID = ? ")
		;
		
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("disperseId"));
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}

	/**
	 * 投资记录ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>disperseId:String:散标主键</tt><br>
     *      <tt>start     :String:起始值</tt><br>
     *      <tt>length    :String:长度</tt><br>
	 * @return
     *      <tt>investor    :String:投资人</tt><br>
     *      <tt>investAmount:String:投资金额（元）</tt><br>
     *      <tt>investDate  :String:投资日期</tt><br>
	 * @throws SLException
	 */
	public Page<Map<String, Object>> queryDisperseInvestRecord(
			Map<String, Object> params) {
		StringBuffer sql = new StringBuffer()
		.append(" SELECT decode(cust.mobile,NULL,'',substr(cust.mobile, 1, 3) || '****' || substr(cust.mobile, -4)) \"investor\" ")
		.append("      , TRUNC(invest.INVEST_AMOUNT,2) \"investAmount\" ")
		.append("      , invest.CREATE_DATE \"investDate\" ")
		.append("      , case when b.invest_source = 'auto' then '自动投标' when b.invest_source = 'reserveinvest' then '优先投' else '手动投标' end \"investSource\" ")
		.append("  FROM BAO_T_INVEST_INFO invest ")
		.append("  inner join bao_t_invest_detail_info b on b.invest_id = invest.id ")
		.append(" INNER JOIN BAO_T_CUST_INFO cust ON cust.ID = invest.CUST_ID ")
		.append(" WHERE invest.LOAN_ID = ? AND invest.INVEST_MODE = '加入' ")
		.append(" ORDER BY invest.CREATE_DATE desc ")
		;
		
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("disperseId"));
		return repositoryUtil.queryForPageMap(sql.toString(), args.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}

	/**
	 * 还款计划ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>disperseId:String:散标主键</tt><br>
	 * @return
     *      <tt>paymentDate     :String:还款日期</tt><br>
     *      <tt>paymentStatus   :String:还款状态</tt><br>
     *      <tt>principalPayment:String:应还本息</tt><br>
     *      <tt>punishPayment   :String:应还罚息</tt><br>
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryDispersePaymentPlan(
			Map<String, Object> params) {
		StringBuffer sql = new StringBuffer()
		.append(" SELECT to_date(rl.EXPECT_REPAYMENT_DATE, 'yyyy-MM-dd') \"expectRepaymentDate\" ")
		.append("      , rl.REPAYMENT_STATUS \"repaymentStatus\" ")
		.append("      , rl.REPAYMENT_PRINCIPAL + rl.REPAYMENT_INTEREST \"repaymentTotalAmount\" ")
		.append("      , rl.REPAYMENT_PRINCIPAL \"repaymentPrincipal\" ")
		.append("      , rl.REPAYMENT_INTEREST \"repaymentInterest\" ")
		.append("      , rl.PENALTY_AMOUNT \"penaltyAmount\" ")
		.append("      , 0 as  \"punishPayment\" ")
		.append("  FROM BAO_T_REPAYMENT_PLAN_INFO rl ")
		.append(" WHERE rl.LOAN_ID = ? ")
		.append(" order by rl.EXPECT_REPAYMENT_DATE ")
		;
		
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("disperseId"));
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}

	/**
	 * 历史借款
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>credentialsCode:String:证件号码</tt><br>
	 */
	public int queryLoanHisInfoCount(Map<String, Object> params) {
		StringBuffer sql = new StringBuffer()
		.append(" SELECT count(1) \"count\" ")
		.append("  FROM BAO_T_LOAN_INFO loan, BAO_T_LOAN_CUST_INFO lc ")
		.append(" WHERE lc.ID = loan.RELATE_PRIMARY ")
		.append("   AND loan.LOAN_STATUS IN (" + params.get("status") + ") ")
		.append("   AND lc.CREDENTIALS_CODE = (SELECT c.CREDENTIALS_CODE FROM BAO_T_LOAN_INFO l, BAO_T_LOAN_CUST_INFO c  WHERE c.ID=l.RELATE_PRIMARY AND l.ID = ? ) ")
		.append("   AND loan.id != ? ")
		;
		
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("loanId"));
		args.add(params.get("loanId"));
		Integer count = jdbcTemplate.queryForObject(sql.toString(), args.toArray(), Integer.class);
		return count.intValue();
	}

	@Override
	public BigDecimal queryLoanHisInfoAmount(Map<String, Object> params) {
		StringBuffer sql = new StringBuffer()
		.append(" SELECT nvl(sum(TRUNC(loan.LOAN_AMOUNT,2)), 0) \"amount\" ")
		.append("  FROM BAO_T_LOAN_INFO loan, BAO_T_LOAN_CUST_INFO lc ")
		.append(" WHERE lc.ID = loan.RELATE_PRIMARY ")
		.append("   AND loan.LOAN_STATUS IN (" + params.get("status") + ") ")
		.append("   AND lc.CREDENTIALS_CODE = (SELECT c.CREDENTIALS_CODE FROM BAO_T_LOAN_INFO l, BAO_T_LOAN_CUST_INFO c  WHERE c.ID=l.RELATE_PRIMARY AND l.ID = ? ) ")
		.append("   AND loan.id != ? ")
		;
		
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("loanId"));
		args.add(params.get("loanId"));
		return jdbcTemplate.queryForObject(sql.toString(), args.toArray(), BigDecimal.class);
	}

	@Override
	public BigDecimal queryLoanHisInfoPay(Map<String, Object> params) {
		StringBuffer sql = new StringBuffer()
		.append(" SELECT nvl(sum(TRUNC(nvl(rp.REPAYMENT_TOTAL_AMOUNT,0),2)),0) \"amount\" ")
		.append("  FROM BAO_T_LOAN_INFO loan, BAO_T_LOAN_CUST_INFO lc ")
		.append("     , BAO_T_REPAYMENT_PLAN_INFO rp ")
		.append(" WHERE rp.LOAN_ID = loan.ID ")
		.append("   AND rp.REPAYMENT_STATUS = '未还款' ")
		.append("   AND lc.ID = loan.RELATE_PRIMARY ")
		.append("   AND loan.LOAN_STATUS IN (" + params.get("status") + ") ")
		.append("   AND lc.CREDENTIALS_CODE = (SELECT c.CREDENTIALS_CODE FROM BAO_T_LOAN_INFO l, BAO_T_LOAN_CUST_INFO c  WHERE c.ID=l.RELATE_PRIMARY AND l.ID = ? ) ")
		.append("   AND loan.id != ? ")
		;
		
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("loanId"));
		args.add(params.get("loanId"));
		return jdbcTemplate.queryForObject(sql.toString(), args.toArray(), BigDecimal.class);
	}

	/**
	 * 债权投资列表查询
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
     *      <tt>start  :String:起始值</tt><br>
     *      <tt>length :String:</tt><br>
     *      <tt>orderBy:String:排序字段（可以为空）</tt><br>
     *      <tt>isRise :String:升or降（可以为空）</tt><br>
	 * @return
     *      <tt>transferApplyId   :String:转让申请ID</tt><br>
     *      <tt>disperseId        :String:散标主键</tt><br>
     *      <tt>loanTitle         :String:借款名称</tt><br>
     *      <tt>yearRate          :String: 借款年利率</tt><br>
     *      <tt>tradeValue        :String: 债权价值</tt><br>
     *      <tt>tradeAmount       :String: 转让金额</tt><br>
     *      <tt>nextPayDate       :String: 距下次还款</tt><br>
     *      <tt>remainInvestAmount:String:剩余可投金额</tt><br>
     *      <tt>remainTerm        :String:剩余期限</tt><br>
     *      <tt>investScale       :String:进度</tt><br>
     *      <tt>transferStatus    :String:转让状态</tt><br>
	 * @throws SLException
	 */
	public Page<Map<String, Object>> queryCreditList(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
//		.append("   select \"transferApplyId\", ")
//		.append("          \"disperseId\", ")
//		.append("          \"loanTitle\",  ")
//		.append("          \"yearRate\", ")
////		.append("          \"tradeValue\", ")
////		.append("          \"tradeAmount\", ")
////		.append("          \"remainAmount\", ")
//		.append("          \"transAmount\", ")
//		.append("          \"remainAmount\", ")
//		.append("          \"reducedScale\", ")
//		.append("          \"nextPayDate\", ")
//		.append("          \"remainTerm\",  ")
//		.append("          \"investScale\", ")
//		.append("          \"transferStatus\", ")
//		.append("          \"rebateRatio\", ")
//		.append("          \"typeTerm\", ")
//		.append("          \"loanUnit\", ")
//		.append("          \"investMinAmount\", ")
//		.append("          \"increaseAmount\", ")
//		.append("          \"disperseStatus\"  ")
//		.append("          , \"repaymentMethod\"  ")
//		.append("          , \"transferCondition\"  ")
//		.append("          \"transferEndDate\" ")
		.append("   select a.* ")
		.append("   from ( ")
		.append(" SELECT lta.ID \"transferApplyId\" ")
		.append("      , loan.ID \"disperseId\" ")
		.append("      , loan.LOAN_TITLE \"loanTitle\" ")
		.append("      , ld.YEAR_IRR \"yearRate\" ")
//		.append("      , TRUNC(lta.TRADE_SCALE  ")
//		.append("                  * decode(nvl(r.repayment_status, '未还款'),'未还款' ")
//		.append("                    , cr.value_repayment_before ")
//		.append("                    , cr.value_repayment_after) ")
//		.append("               , 2) AS \"tradeValue\" ")
//		.append("      , TRUNC(TRUNC(lta.TRADE_SCALE *  ")
//		.append("                    decode(nvl(r.repayment_status, '未还款'), '未还款', ")
//		.append("                    cr.value_repayment_before, ")
//		.append("                    cr.value_repayment_after), ")
//		.append("                   2) * lta.REDUCED_SCALE, ")
//		.append("             2) \"tradeAmount\" ")
//		.append("      , TRUNC(TRUNC(lta.REMAINDER_TRADE_SCALE *  ")
//		.append("                    decode(nvl(r.repayment_status, '未还款'), '未还款', ")
//		.append("                    cr.value_repayment_before, ")
//		.append("                    cr.value_repayment_after), ")
//		.append("                   2) * lta.REDUCED_SCALE, ")
//		.append("             2) \"remainAmount\" ")
		.append("      , TRUNC_AMOUNT_WEB(lta.TRADE_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL) \"transAmount\" ") // 转让金额
		.append("      , TRUNC_AMOUNT_WEB(lta.REMAINDER_TRADE_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL) \"remainAmount\" ") // 剩余可投金额
		.append("      , lta.REDUCED_SCALE \"reducedScale\" ")// 折价比例
		.append("      , ld.NEXT_EXPIRY - TRUNC(?) + 1 \"nextPayDate\" ")
		.append("      , ceil(months_between(loan.invest_end_date, lta.TRANSFER_START_DATE)) || '个月' AS \"remainTerm\" ")
		.append("      , loan.invest_end_date \"investEndDate\" ")
		.append("      , trunc((1 - lta.REMAINDER_TRADE_SCALE/lta.TRADE_SCALE)*100,0) AS \"investScale\" ")
		.append("      , decode(decode(lta.APPLY_STATUS, '部分转让成功', '待转让', lta.APPLY_STATUS), '待转让', '待转让', loan.loan_status) \"transferStatus\" ")
		.append("      , loan.REBATE_RATIO \"rebateRatio\" ")
		.append("      , ceil(months_between(loan.invest_end_date, ?)) AS \"typeTerm\" ")
		.append("      , loan.LOAN_UNIT  AS \"loanUnit\" ")
		.append("      , loan.INVEST_MIN_AMOUNT  AS \"investMinAmount\" ")
		.append("      , loan.INCREASE_AMOUNT  AS \"increaseAmount\" ")
		.append("      , loan.LOAN_STATUS \"disperseStatus\" ")
		.append("      , lta.TRANSFER_END_DATE \"transferEndDate\" ")
		.append("      , loan.REPAYMENT_METHOD \"repaymentMethod\" ")
		.append("    	,lta.sticky_status, ")
		.append("       lta.sticky_level, ")
		.append("       trunc(loan.rasie_end_date) rasie_end_date  ")
//		.append("      , CASE WHEN loan.SEAT_TERM = -1 OR loan.SEAT_TERM IS NULL THEN '不允许转让' WHEN loan.SEAT_TERM = 0 THEN '可以立即转让' ELSE '持有'||loan.SEAT_TERM||'天即可转让' END \"transferCondition\" ")
		.append("      , CASE WHEN lta.transfer_seat_term = -1 OR lta.transfer_seat_term IS NULL THEN '不允许转让' WHEN lta.transfer_seat_term = 0 THEN '可以立即转让' ELSE '出借'||lta.transfer_seat_term||'天即可转让' END \"transferCondition\" ")
		
		.append("   FROM BAO_T_LOAN_TRANSFER_APPLY lta ")
		.append("      , BAO_T_WEALTH_HOLD_INFO wh ")
		.append("      , BAO_T_LOAN_INFO loan ")
		.append("   LEFT JOIN (SELECT * from bao_t_repayment_plan_info ")
		.append("                      WHERE expect_repayment_date = to_char(?, 'yyyyMMdd')) r ")
		.append("          ON r.loan_id = loan.ID ")
		.append("      , BAO_T_LOAN_DETAIL_INFO ld ")
//		.append("      , BAO_T_CREDIT_RIGHT_VALUE cr ")
		.append("  WHERE 1=1 ")
//		.append("    AND cr.LOAN_ID = loan.ID ")
//		.append("    AND cr.VALUE_DATE = to_char(?, 'yyyyMMdd') ")
		.append("    AND ld.LOAN_ID = loan.ID ")
		.append("    AND loan.ID = wh.LOAN_ID ")
		.append("    AND wh.ID = lta.SENDER_HOLD_ID ")
		.append("    AND lta.APPLY_STATUS in ('待转让', '部分转让成功') ")
		.append("    AND lta.CANCEL_STATUS = '未撤销' ")
		.append("    and  lta.AUDIT_STATUS = '通过' ")
		.append("    and  lta.is_run_auto_invest = 'Y' ")
		.append("    AND lta.TRANSFER_END_DATE > ? ")
		.append("    AND (loan.CHANNEL_FLAG != '是' or loan.CHANNEL_FLAG is null) ")// 是否用于渠道
		.append("   )a ")
		;
		Date date = new Date();
		List<Object> args = new ArrayList<Object>();
		args.add(date);
		args.add(date);
		args.add(date);
//		args.add(date);
		args.add(date);
		
		// 排序
		sql.append(" order by ");
		if(!StringUtils.isEmpty(params.get("orderBy"))){
			if("remainTerm".equals(params.get("orderBy"))){
				sql.append(" ceil(months_between(\"investEndDate\", ?)) ");
				args.add(date);
			} else {
				sql.append(String.format(" \"%s\" ", params.get("orderBy")));
			}
			
			if(!StringUtils.isEmpty(params.get("isRise"))) {
				sql.append(params.get("isRise"));
			}
			sql.append(" , ");
		}
		sql.append(" a.sticky_status,a.sticky_level,trunc(a.rasie_end_date), \"investScale\" desc ");
		return repositoryUtil.queryForPageMap(sql.toString(), args.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}

    @Override
    public Page<Map<String, Object>> queryCreditListInSpecialChannel(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder()
//		.append("   select \"transferApplyId\", ")
//		.append("          \"disperseId\", ")
//		.append("          \"loanTitle\",  ")
//		.append("          \"yearRate\", ")
////		.append("          \"tradeValue\", ")
////		.append("          \"tradeAmount\", ")
////		.append("          \"remainAmount\", ")
//		.append("          \"transAmount\", ")
//		.append("          \"remainAmount\", ")
//		.append("          \"reducedScale\", ")
//		.append("          \"nextPayDate\", ")
//		.append("          \"remainTerm\",  ")
//		.append("          \"investScale\", ")
//		.append("          \"transferStatus\", ")
//		.append("          \"rebateRatio\", ")
//		.append("          \"typeTerm\", ")
//		.append("          \"loanUnit\", ")
//		.append("          \"investMinAmount\", ")
//		.append("          \"increaseAmount\", ")
//		.append("          \"disperseStatus\"  ")
//		.append("          , \"repaymentMethod\"  ")
//		.append("          , \"transferCondition\"  ")
//		.append("          \"transferEndDate\" ")
                .append("   select a.* ")
                .append("   from ( ")
                .append(" SELECT lta.ID \"transferApplyId\" ")
                .append("      , loan.ID \"disperseId\" ")
                .append("      , loan.LOAN_TITLE \"loanTitle\" ")
                .append("      , ld.YEAR_IRR \"yearRate\" ")
//		.append("      , TRUNC(lta.TRADE_SCALE  ")
//		.append("                  * decode(nvl(r.repayment_status, '未还款'),'未还款' ")
//		.append("                    , cr.value_repayment_before ")
//		.append("                    , cr.value_repayment_after) ")
//		.append("               , 2) AS \"tradeValue\" ")
//		.append("      , TRUNC(TRUNC(lta.TRADE_SCALE *  ")
//		.append("                    decode(nvl(r.repayment_status, '未还款'), '未还款', ")
//		.append("                    cr.value_repayment_before, ")
//		.append("                    cr.value_repayment_after), ")
//		.append("                   2) * lta.REDUCED_SCALE, ")
//		.append("             2) \"tradeAmount\" ")
//		.append("      , TRUNC(TRUNC(lta.REMAINDER_TRADE_SCALE *  ")
//		.append("                    decode(nvl(r.repayment_status, '未还款'), '未还款', ")
//		.append("                    cr.value_repayment_before, ")
//		.append("                    cr.value_repayment_after), ")
//		.append("                   2) * lta.REDUCED_SCALE, ")
//		.append("             2) \"remainAmount\" ")
                .append("      , TRUNC_AMOUNT_WEB(lta.TRADE_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL) \"transAmount\" ") // 转让金额
                .append("      , TRUNC_AMOUNT_WEB(lta.REMAINDER_TRADE_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL) \"remainAmount\" ") // 剩余可投金额
                .append("      , lta.REDUCED_SCALE \"reducedScale\" ")// 折价比例
                .append("      , ld.NEXT_EXPIRY - TRUNC(?) + 1 \"nextPayDate\" ")
                .append("      , ceil(months_between(loan.invest_end_date, lta.TRANSFER_START_DATE)) || '个月' AS \"remainTerm\" ")
                .append("      , loan.invest_end_date \"investEndDate\" ")
                .append("      , trunc((1 - lta.REMAINDER_TRADE_SCALE/lta.TRADE_SCALE)*100,0) AS \"investScale\" ")
                .append("      , decode(decode(lta.APPLY_STATUS, '部分转让成功', '待转让', lta.APPLY_STATUS), '待转让', '待转让', loan.loan_status) \"transferStatus\" ")
                .append("      , loan.REBATE_RATIO \"rebateRatio\" ")
                .append("      , ceil(months_between(loan.invest_end_date, ?)) AS \"typeTerm\" ")
                .append("      , loan.LOAN_UNIT  AS \"loanUnit\" ")
                .append("      , loan.INVEST_MIN_AMOUNT  AS \"investMinAmount\" ")
                .append("      , loan.INCREASE_AMOUNT  AS \"increaseAmount\" ")
                .append("      , loan.LOAN_STATUS \"disperseStatus\" ")
                .append("      , lta.TRANSFER_END_DATE \"transferEndDate\" ")
                .append("      , loan.REPAYMENT_METHOD \"repaymentMethod\" ")
                .append("    	,lta.sticky_status, ")
                .append("       lta.sticky_level, ")
                .append("       trunc(loan.rasie_end_date) rasie_end_date  ")
//		.append("      , CASE WHEN loan.SEAT_TERM = -1 OR loan.SEAT_TERM IS NULL THEN '不允许转让' WHEN loan.SEAT_TERM = 0 THEN '可以立即转让' ELSE '持有'||loan.SEAT_TERM||'天即可转让' END \"transferCondition\" ")
                .append("      , CASE WHEN lta.transfer_seat_term = -1 OR lta.transfer_seat_term IS NULL THEN '不允许转让' WHEN lta.transfer_seat_term = 0 THEN '可以立即转让' ELSE '出借'||lta.transfer_seat_term||'天即可转让' END \"transferCondition\" ")

                .append("   FROM BAO_T_LOAN_TRANSFER_APPLY lta ")
                .append("      , BAO_T_WEALTH_HOLD_INFO wh ")
                .append("      , BAO_T_LOAN_INFO loan ")
                .append("   LEFT JOIN (SELECT * from bao_t_repayment_plan_info ")
                .append("                      WHERE expect_repayment_date = to_char(?, 'yyyyMMdd')) r ")
                .append("          ON r.loan_id = loan.ID ")
                .append("      , BAO_T_LOAN_DETAIL_INFO ld ")
//		.append("      , BAO_T_CREDIT_RIGHT_VALUE cr ")
                .append("  WHERE 1=1 ")
//		.append("    AND cr.LOAN_ID = loan.ID ")
//		.append("    AND cr.VALUE_DATE = to_char(?, 'yyyyMMdd') ")
                .append("    AND ld.LOAN_ID = loan.ID ")
                .append("    AND loan.ID = wh.LOAN_ID ")
                .append("    AND wh.ID = lta.SENDER_HOLD_ID ")
                .append("    AND lta.APPLY_STATUS in ('待转让', '部分转让成功') ")
                .append("    AND lta.CANCEL_STATUS = '未撤销' ")
                .append("    and  lta.AUDIT_STATUS = '通过' ")
                .append("    and  lta.is_run_auto_invest = 'Y' ")
                .append("    AND lta.TRANSFER_END_DATE > ? ")
                .append("    AND loan.CHANNEL_FLAG = '是' ")// 是否用于渠道
                .append("   )a ")
                ;
        Date date = new Date();
        List<Object> args = new ArrayList<Object>();
        args.add(date);
        args.add(date);
        args.add(date);
//		args.add(date);
        args.add(date);

        // 排序
        sql.append(" order by ");
        if(!StringUtils.isEmpty(params.get("orderBy"))){
            if("remainTerm".equals(params.get("orderBy"))){
                sql.append(" ceil(months_between(\"investEndDate\", ?)) ");
                args.add(date);
            } else {
                sql.append(String.format(" \"%s\" ", params.get("orderBy")));
            }

            if(!StringUtils.isEmpty(params.get("isRise"))) {
                sql.append(params.get("isRise"));
            }
            sql.append(" , ");
        }
        sql.append(" a.sticky_status,a.sticky_level,trunc(a.rasie_end_date), \"investScale\" desc ");
        return repositoryUtil.queryForPageMap(sql.toString(), args.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
    }

    public List<Map<String, Object>> queryCreditListForJob(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT lta.ID \"transferApplyId\" ")
//		.append("      , loan.ID \"disperseId\" ")
//		.append("      , loan.LOAN_TITLE \"loanTitle\" ")
//		.append("      , ld.YEAR_IRR \"yearRate\" ")
//		.append("      , TRUNC(lta.TRADE_SCALE  ")
//		.append("                  * decode(nvl(r.repayment_status, '未还款'),'未还款' ")
//		.append("                    , cr.value_repayment_before ")
//		.append("                    , cr.value_repayment_after) ")
//		.append("               , 2) AS \"tradeValue\" ")
//		.append("      , TRUNC(TRUNC(lta.TRADE_SCALE *  ")
//		.append("                    decode(nvl(r.repayment_status, '未还款'), '未还款', ")
//		.append("                    cr.value_repayment_before, ")
//		.append("                    cr.value_repayment_after), ")
//		.append("                   2) * lta.REDUCED_SCALE, ")
//		.append("             2) \"tradeAmount\" ")
//		.append("      , TRUNC(TRUNC(lta.REMAINDER_TRADE_SCALE *  ")
//		.append("                    decode(nvl(r.repayment_status, '未还款'), '未还款', ")
//		.append("                    cr.value_repayment_before, ")
//		.append("                    cr.value_repayment_after), ")
//		.append("                   2) * lta.REDUCED_SCALE, ")
//		.append("             2) \"remainAmount\" ")
		.append("      , TRUNC(TRUNC(lta.REMAINDER_TRADE_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL, 2) * lta.REDUCED_SCALE, 2) \"remainAmount\" ") // 剩余可投金额（需要折价的金额）
//		.append("      , ld.NEXT_EXPIRY - TRUNC(?) + 1 \"nextPayDate\" ")
//		.append("      , ceil(months_between(loan.invest_end_date, lta.TRANSFER_START_DATE)) || '个月' AS \"remainTerm\" ")
//		.append("      , loan.invest_end_date \"investEndDate\" ")
		.append("      , trunc((1 - lta.REMAINDER_TRADE_SCALE/lta.TRADE_SCALE)*100,0) AS \"investScale\" ")
//		.append("      , decode(decode(lta.APPLY_STATUS, '部分转让成功', '待转让', lta.APPLY_STATUS), '待转让', '待转让', loan.loan_status) \"transferStatus\" ")
//		.append("      , loan.REBATE_RATIO \"rebateRatio\" ")
//		.append("      , ceil(months_between(loan.invest_end_date, ?)) AS \"typeTerm\" ")
//		.append("      , loan.LOAN_UNIT  AS \"loanUnit\" ")
//		.append("      , loan.INVEST_MIN_AMOUNT  AS \"investMinAmount\" ")
//		.append("      , loan.INCREASE_AMOUNT  AS \"increaseAmount\" ")
//		.append("      , loan.LOAN_STATUS \"disperseStatus\" ")
		.append("      , lta.TRANSFER_END_DATE \"transferEndDate\" ")
		.append("   FROM BAO_T_LOAN_TRANSFER_APPLY lta ")
		.append("      , BAO_T_WEALTH_HOLD_INFO wh ")
		.append("      , BAO_T_LOAN_INFO loan ")
		.append("   LEFT JOIN (SELECT * from bao_t_repayment_plan_info ")
		.append("                      WHERE expect_repayment_date = to_char(?, 'yyyyMMdd')) r ")
		.append("          ON r.loan_id = loan.ID ")
		.append("      , BAO_T_LOAN_DETAIL_INFO ld ")
		.append("      , BAO_T_CREDIT_RIGHT_VALUE cr ")
		.append("  WHERE 1=1 ")
		.append("    AND cr.LOAN_ID = loan.ID ")
		.append("    AND cr.VALUE_DATE = to_char(?, 'yyyyMMdd') ")
		.append("    AND ld.LOAN_ID = loan.ID ")
		.append("    AND loan.ID = wh.LOAN_ID ")
		.append("    AND wh.ID = lta.SENDER_HOLD_ID ")
		.append("    AND lta.APPLY_STATUS in ('待转让', '部分转让成功') ")
		.append("    AND lta.CANCEL_STATUS = '未撤销' ")
		.append("    and lta.AUDIT_STATUS = '通过' ")
		.append("    AND lta.TRANSFER_END_DATE > ? ")
		.append("    AND loan.IS_ALLOW_AUTO_INVEST ='"+Constant.IS_ALLOW_AUTO_INVEST_01+"'")
		;
		Date date = new Date();
		List<Object> args = new ArrayList<Object>();
		args.add(date);
		args.add(date);
		args.add(date);
//		args.add(date);
//		args.add(date);
		
		@SuppressWarnings("unchecked")
		List<Integer> seatTerms = (List<Integer>) params.get("loanSeatTerm");
		if(seatTerms != null && seatTerms.size() > 0){
			sql.append(" and ( ");
			for (int i = 0; i < seatTerms.size(); i++) {
				int seatTerm = seatTerms.get(i);
				if(i != 0){
					sql.append(" OR ");
				}
				if(seatTerm == -1){
					sql.append(" lta.transfer_seat_term = -1 ");
				} else {
					sql.append(" lta.transfer_seat_term != -1 ");
				}
			}
			sql.append(" ) ");
		}
		
		if(!StringUtils.isEmpty(params.get("maxTerm"))){
			sql.append(" and loan.invest_end_date - TRUNC(?) <= ? ");
			int days = 0;
			if (!StringUtils.isEmpty(params.get("loanUnit")) && "天".equals(params.get("loanUnit").toString())) {
				 days = Integer.parseInt(params.get("maxTerm").toString());
			}else if(!StringUtils.isEmpty(params.get("loanUnit")) && "月".equals(params.get("loanUnit").toString())){
				 days = Integer.parseInt(params.get("maxTerm").toString())*30;
			}
			args.add(date);
			args.add(days);
		}
		if(!StringUtils.isEmpty(params.get("limitedTermMin"))){
			sql.append(" and loan.invest_end_date - TRUNC(?) >= ? ");
			int days = 0;
			if (!StringUtils.isEmpty(params.get("loanUnit")) && "天".equals(params.get("loanUnit").toString())) {
				 days = Integer.parseInt(params.get("limitedTermMin").toString());
			}else if(!StringUtils.isEmpty(params.get("loanUnit")) && "月".equals(params.get("loanUnit").toString())){
				 days = Integer.parseInt(params.get("limitedTermMin").toString())*30;
			}
			args.add(date);
			args.add(days);
		}
		if(!StringUtils.isEmpty(params.get("minYearRate"))){
			sql.append(" and ld.YEAR_IRR >= ? ");
			args.add(params.get("minYearRate"));
		}
		if(!StringUtils.isEmpty(params.get("limitedYearRateMax"))){
			sql.append(" and ld.YEAR_IRR <= ? ");
			args.add(params.get("limitedYearRateMax"));
		}
		if(!StringUtils.isEmpty(params.get("repaymentMethod"))){
			String repaymentMethodSplit = (String) params.get("repaymentMethod");
			String[] repaymentMethodList = repaymentMethodSplit.split(",");
			sql.append(" and ( ");
			for (int i = 0; i < repaymentMethodList.length; i++) {
				if (i == 0) {
					sql.append(" loan.repayment_method = ? ");
				} else {
					sql.append(" OR loan.repayment_method = ? ");
				}
				args.add(repaymentMethodList[i]);
			}
			sql.append(" ) ");
		}
		
		// 排序
		sql.append(" order by \"remainAmount\" asc, \"investScale\" desc, \"transferEndDate\" ");
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}

	/**
	 * 债权转让详情
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
     *      <tt>transferApplyId:String:转让申请ID</tt><br>
	 * @return
     *      <tt>transferApplyId  :String:转让申请ID</tt><br>
     *      <tt>disperseId       :String:散标主键</tt><br>
     *      <tt>loanTitle        :String:借款名称</tt><br>
     *      <tt>transferCode     :String: 转让编号</tt><br>
     *      <tt>remainTime       :String:剩余时间</tt><br>
     *      <tt>yearRate         :String:年化收益率</tt><br>
     *      <tt>remainTerm       :String:剩余期限</tt><br>
     *      <tt>tradeAmount      :String:转让总额</tt><br>
     *      <tt>tradeScale       :String:转让总比</tt><br>
     *      <tt>remainAmount     :String:剩余金额</tt><br>
     *      <tt>investScale      :String:已投百分比</tt><br>
     *      <tt>tradeValue       :String:债权价值</tt><br>
     *      <tt>interestTime     :String:计息日期</tt><br>
     *      <tt>repaymentMethod  :String:还款方式</tt><br>
     *      <tt>nextTermPay      :String:下期还款</tt><br>
     *      <tt>transferCondition:String:转让条件</tt><br>
     *      <tt>security         :String:安全保障</tt><br>
     *      <tt>loanStatus       :String:债权状态</tt><br>
     *      <tt>transferStartDate:String:转让开始时间（发布日期）</tt><br>
     *      <tt>transferEndDate  :String:转让结束日期</tt><br>
     *      <tt>serverDate       :String:系统当前时间</tt><br>
     *      <tt>totalAmount      :String:项目总额（总本金）</tt><br>
     *      <tt>totalValue       :String:项目总值（总价值）</tt><br>
     *      <tt>totalInterest    :String:总利息</tt><br>
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryCreditDetail(
			Map<String, Object> params) {
		Date now = new Date();
		StringBuilder sql = new StringBuilder()
		.append(" SELECT lta.ID \"transferApplyId\"  ")
		.append("      , loan.ID \"disperseId\" ")
		.append("      , loan.LOAN_TITLE \"loanTitle\" ")
		.append("      , lta.TRANSFER_NO \"transferCode\" ")
		.append("      , lta.TRANSFER_END_DATE \"transferEndDate\" ")
		.append("      , ld.YEAR_IRR \"yearRate\" ")
//		.append("      , TRUNC(TRUNC(lta.TRADE_SCALE *  ")
//		.append("                    decode(nvl(r.repayment_status, '未还款'), '未还款', ")
//		.append("                    cr.value_repayment_before, ")
//		.append("                    cr.value_repayment_after), ")
//		.append("                   2) * lta.REDUCED_SCALE, ")
//		.append("             2) \"tradeAmount\" ")
//		.append("      , decode(lta.CANCEL_STATUS,'已撤销', 0, decode(lta.APPLY_STATUS, '转让成功', 0, 1)) ")
//		.append("              * TRUNC(TRUNC(lta.REMAINDER_TRADE_SCALE *  ")
//		.append("                          decode(nvl(r.repayment_status, '未还款'), '未还款', ")
//		.append("                          cr.value_repayment_before, ")
//		.append("                          cr.value_repayment_after), ")
//		.append("                      2) * lta.REDUCED_SCALE, ")
//		.append("                2) \"remainAmount\" ")
		.append("      , TRUNC_AMOUNT_WEB(lta.TRADE_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL) \"transAmount\" ")// 转让金额
		.append("      , TRUNC_AMOUNT_WEB(lta.REMAINDER_TRADE_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL) \"remainAmount\" ") // 剩余可投金额
		.append("      , lta.REDUCED_SCALE \"reducedScale\" ")// 折价比例
		.append("      , (ld.NEXT_EXPIRY - trunc(?) + case when trunc(?) >= ld.NEXT_EXPIRY then 1 else 0 end) /* 还款日当天未还款前转让，让一天利息 */ \"holdDays\" ") // 计息天数
		.append("      , (ld.NEXT_EXPIRY - nvl(ld.Last_expiry, trunc(loan.INVEST_START_DATE))) \"daysOfmonth\" ")// 当月总天数
		.append("      , case when loan.LOAN_STATUS='正常' then TRUNC(lta.TRADE_SCALE * (SELECT REPAYMENT_INTEREST  ")
		.append("                                 FROM bao_t_repayment_plan_info  ")
		.append("                                WHERE LOAN_ID = loan.ID  ")
		.append("                                  AND EXPECT_REPAYMENT_DATE = to_char(ld.NEXT_EXPIRY,'yyyyMMdd')) ")
		.append("             * (ld.NEXT_EXPIRY - trunc(?) + case when trunc(?) >= ld.NEXT_EXPIRY then 1 else 0 end) /* 还款日当天未还款前转让，让一天利息 */ ") // 计息天数
		.append("             / (ld.NEXT_EXPIRY - nvl(ld.Last_expiry, trunc(loan.INVEST_START_DATE))) ") // 当月总天数
		.append("        , 2) else 0 end \"currentInterest\" ") // 转让金额当期收益 = 投资金额在计息日到下个还款日产生的利息
		.append("      , case when loan.LOAN_STATUS='正常' then TRUNC(lta.TRADE_SCALE * (SELECT nvl(sum(REPAYMENT_INTEREST),0) ")
		.append("                                 FROM bao_t_repayment_plan_info  ")
		.append("                                WHERE LOAN_ID = loan.ID  ")
		.append("                                  AND EXPECT_REPAYMENT_DATE > to_char(ld.NEXT_EXPIRY,'yyyyMMdd')) ")
		.append("        , 2) else 0 end \"remainInterest\" ") // 剩余收益
		.append("      , ceil(months_between(loan.invest_end_date, trunc(?))) || '个月' \"remainTerm\" ")
		.append("      , lta.TRADE_SCALE \"tradeScale\" ")
		.append("      , trunc((1 - lta.REMAINDER_TRADE_SCALE/lta.TRADE_SCALE)*100, 0) \"investScale\" ")
//		.append("      , TRUNC(lta.TRADE_SCALE ")
//		.append("                  * decode(nvl(r.repayment_status, '未还款'),'未还款' ")
//		.append("                    , cr.value_repayment_before ")
//		.append("                    , cr.value_repayment_after) ")
//		.append("               , 2) AS \"tradeValue\" ")
		.append("      , '当日计息' AS \"interestTime\" ")
		.append("      , loan.REPAYMENT_METHOD \"repaymentMethod\" ")
		.append("      , ld.NEXT_EXPIRY - TRUNC(?) + 1 \"nextTermPay\" ")
		.append("      , CASE WHEN lta.transfer_seat_term = -1 OR lta.transfer_seat_term IS NULL THEN '不允许转让' WHEN lta.transfer_seat_term = 0 THEN '可以立即转让' ELSE '出借'||lta.transfer_seat_term||'天即可转让' END \"transferCondition\" ")
		.append("      , '安全保障' \"security\" ")
		.append("      , loan.LOAN_STATUS \"loanStatus\" ")
		.append("      , lta.TRANSFER_START_DATE \"transferStartDate\" ")
		.append("      , lta.TRANSFER_END_DATE \"transferEndDate\" ")
		.append("      , ld.CREDIT_REMAINDER_PRINCIPAL \"totalAmount\" ")
//		.append("      , decode(nvl(r.repayment_status, '未还款'), '未还款' ")
//		.append("                  ,cr.value_repayment_before ")
//		.append("                  ,cr.value_repayment_after) \"totalValue\" ")
		.append("      , (SELECT sum(REPAYMENT_INTEREST) FROM bao_t_repayment_plan_info ")
		.append("          WHERE LOAN_ID=loan.ID AND REPAYMENT_STATUS='未还款') \"totalInterest\" ")
		.append("      , decode(lta.CANCEL_STATUS,'已撤销','转让已结束',decode(lta.APPLY_STATUS, '部分转让成功', '待转让', '转让成功', '转让已结束', lta.APPLY_STATUS)) \"transferStatus\" ")
		.append("      , lta.PROTOCOL_TYPE \"protocolType\" ")
//		.append("      , loan.INVEST_MIN_AMOUNT \"investMinAmount\" ")
//		.append("      , loan.INCREASE_AMOUNT \"increaseAmount\" ")
		.append("      , lta.INVEST_MIN_AMOUNT \"investMinAmount\" ")
		.append("      , lta.INCREASE_AMOUNT \"increaseAmount\" ")
		.append("      , lr.FLAG \"loanFlag\" ")
		.append("      , loan.INVEST_END_DATE \"endDate\" ")
		.append("      , (SELECT count(DISTINCT lt.RECEIVE_CUST_ID) ")
		.append("           FROM BAO_T_LOAN_TRANSFER lt ")
		.append("          WHERE lt.TRANSFER_APPLY_ID = lta.ID) \"alreadyInvestPeoples\" ")
		.append("      , loan.LOAN_DESC \"loanUse\" ")
		.append("      , loan.LOAN_INFO \"loanInfo\" ")
		.append("   FROM BAO_T_LOAN_TRANSFER_APPLY lta ")
		.append("      , BAO_T_WEALTH_HOLD_INFO wh ")
//		.append("      , BAO_T_CREDIT_RIGHT_VALUE cr ")
		.append("      , BAO_T_LOAN_DETAIL_INFO ld ")
		.append("      , BAO_T_LOAN_INFO loan  ")
		.append("   left join (SELECT * from bao_t_repayment_plan_info ")
		.append("                      WHERE expect_repayment_date = to_char(?, 'yyyyMMdd')) r ")
		.append("     on r.loan_id = loan.ID ")
		.append("   LEFT JOIN BAO_T_LOAN_REBATE_INFO lr ")
		.append("     ON lr.REPAYMENT_METHOD = loan.REPAYMENT_METHOD ")
		.append("    AND lr.LOAN_TERM = loan.LOAN_TERM ")
		.append("    AND lr.PRODUCT_TYPE = loan.LOAN_TYPE ")
		.append("  WHERE 1=1 ")
//		.append("    AND cr.LOAN_ID = loan.ID ")
//		.append("    AND cr.VALUE_DATE = to_char(?, 'yyyyMMdd') ")
		.append("    AND ld.LOAN_ID = loan.ID ")
		.append("    AND loan.ID = wh.LOAN_ID ")
		.append("    AND wh.ID = lta.SENDER_HOLD_ID ")
		.append("    AND lta.ID = ? ")
		;
		List<Object> args = new ArrayList<Object>();
		args.add(now);// 计息天数
		args.add(now);// 计息天数
		args.add(now);// 当期收益-计息天数
		args.add(now);// 当期收益-计息天数
		
		args.add((Date)params.get("currentDate"));
		args.add((Date)params.get("currentDate"));
		args.add((Date)params.get("currentDate"));
//		args.add((Date)params.get("currentDate"));
		args.add(params.get("transferApplyId"));
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}

	@Override
	public List<Map<String, Object>> queryCreditPaymentPlan(
			Map<String, Object> params) {
		StringBuffer sql = new StringBuffer()
		.append("  SELECT a.id \"id\", rl.loan_id \"loanId\", to_date(rl.EXPECT_REPAYMENT_DATE, 'yyyy-MM-dd') \"expectRepaymentDate\"  ")
		.append(" 		      , rl.REPAYMENT_STATUS \"repaymentStatus\"  ")
		.append(" 		      , TRUNC_AMOUNT_WEB(rl.REPAYMENT_PRINCIPAL*a.trade_scale) + TRUNC_AMOUNT_WEB(rl.REPAYMENT_INTEREST*a.trade_scale) \"repaymentTotalAmount\"  ")
		.append(" 		      , TRUNC_AMOUNT_WEB(rl.REPAYMENT_PRINCIPAL*a.trade_scale) \"repaymentPrincipal\"  ")
		.append(" 		      , TRUNC_AMOUNT_WEB(rl.REPAYMENT_INTEREST*a.trade_scale) \"repaymentInterest\"  ")
		.append(" 		      , TRUNC_AMOUNT_WEB(rl.PENALTY_AMOUNT*a.trade_scale) \"penaltyAmount\"  ")
		.append(" 		      , 0 as  \"punishPayment\"  ")
		.append(" 		  FROM bao_t_loan_transfer_apply a ")
		.append("       inner join bao_t_wealth_hold_info h on h.id = a.sender_hold_id ")
		.append("       inner join BAO_T_REPAYMENT_PLAN_INFO rl on rl.loan_id = h.loan_id ")
		.append(" 		 WHERE a.id = ? ")
		.append("      and rl.expect_repayment_date >= to_char(a.transfer_start_date, 'yyyyMMdd') ")
		.append(" 		 order by rl.EXPECT_REPAYMENT_DATE ")
		;
		
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("transferApplyId"));
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}

	/**
	 * 资产信息列表（债权列表）
	 * @author  guoyk
	 * @date    2017-02-24 9:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
     *      <tt>start:String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
	 * @return
     *      <tt>iTotalDisplayRecords:String: 总条数</tt><br>
     *      <tt>data                :String:资产信息列表:List<Map<String, Object>></tt><br>
     *      <tt>custName            :String:借款人姓名</tt><br>
     *      <tt>credentialsCode     :String:借款人身份证号码</tt><br>
     *      <tt>loanAmount          :String:借款金额</tt><br>
     *      <tt>investEndDate       :String:借款到期日</tt><br>
     *      <tt>repaymentMethod     :String:还款方式</tt><br>
     *      <tt>loanUse             :String:借款用途</tt><br>
	 * @throws SLException
	 */
	@Override
	public Page<Map<String, Object>> queryAssetListByLoanId(
			Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT CASE WHEN loan.LOAN_TYPE='善意贷' THEN asset.CUST_NAME else decode(asset.CUST_NAME,NULL,'',substr(asset.CUST_NAME,0,1)||'**') END \"custName\"  ")
		.append(" 		 		,asset.CREDENTIALS_CODE \"credentialsCode\"  ")
		.append(" 		 		,TRUNC(asset.LOAN_AMOUNT,2) \"loanAmount\"  ")
		.append(" 		 		,asset.INVEST_END_DATE \"investEndDate\"  ")
		.append(" 		 		,asset.REPAYMENT_METHOD \"repaymentMethod\"  ")
		.append(" 		 		,asset.LOAN_DESC \"loanUse\"  ")
		.append(" 			 FROM BAO_T_ASSET_INFO asset, BAO_T_LOAN_INFO loan ")
		.append(" 		 	 WHERE asset.LOAN_ID = loan.ID ")
		.append(" 		 	   and loan.ID =? ")
		;
		
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("loanId"));
		return repositoryUtil.queryForPageMap(sql.toString(), args.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}

	/**
	 * 查询自动投标
	 */
	@Override
	public Page<Map<String, Object>> queryAutoInvestList(
			Map<String, Object> params) {
		SqlCondition sqlCondition = getAutoInvestListSql(params);
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}
	private SqlCondition getAutoInvestListSql(Map<String, Object> param){
		StringBuilder sql = new StringBuilder()
		.append(" select  ")
		.append(" decode(ci.login_name,NULL,'',ci.login_name) \"loginName\", ")
		.append(" trunc(aci.account_available_amount,2) \"availableAmount\", ")
		.append(" nvl(trunc(aui.keep_available_amount, 2),0) \"keepAvailableAmount\" ,")
        .append(" case when aci.account_available_amount - aui.keep_available_amount > 0 then trunc(aci.account_available_amount, 2) - trunc(aui.keep_available_amount, 2) else 0 end \"realAvailableAmount\", ")
		.append(" trunc(aui.limited_year_rate*100,2) \"limitedYearRate\", ")
		.append(" nvl(trunc(aui.limited_year_rate_max*100,2),15) \"limitedYearRateMax\", ")
		.append(" aui.limited_term \"limitedTerm\", ")
		.append(" nvl(aui.limited_term_min,0) \"limitedTermMin\", ")
		.append(" nvl(aui.loan_unit,'月') \"loanUnit\", ")
		.append(" aui.open_date \"realOpenDate\", ")
//		.append(" nvl((select min(c.create_date)   ")//SLCF-3038 后台智能投顾开启时间取第一次的时间
//		.append("            from  bao_t_log_info c  ")
//		.append("            where c.relate_type = 'BAO_T_AUTO_INVEST_INFO'  ")
//		.append("            and c.relate_primary = aui.id),aui.open_date)  \"openDate\", ")
		.append("   nvl( b.max_create_date, ")
		.append("    aui.open_date) \"openDate\", ")
		.append(" nvl(aui.can_invest_product, '优选项目（可转让标的）,优选项目（不可转让标的）,转让专区（可转让标的）,转让专区（不可转让标的）') \"canInvestProduct\", ")
		.append(" nvl(aui.repayment_method, '等额本息,到期还本付息,每期还息到期付本') \"repaymentMethod\" ")
		.append(" ,nvl(aui.small_quantity_invest, '否') \"smallQuantityInvest\" ")
		.append(" from  ")
		.append(" bao_t_auto_invest_info aui ")
		.append(" inner join bao_t_account_info aci  ")
		.append(" on aui.cust_id = aci.cust_id ")
		.append(" inner join bao_t_cust_info ci  ")
		.append(" on aui.cust_id = ci.id ")
		.append("  inner join ")
		.append("           ( ")
		.append("             select aui.id, max_create_date from bao_t_auto_invest_info aui ")
		.append("             inner join ")
		.append("             ( select lg.relate_primary, max(lg.create_date) max_create_date from bao_t_log_info lg where lg.oper_after_content = '启用' group by lg.relate_primary) a on a.relate_primary = aui.id ")
		.append("             where aui.open_status = '启用' ")
		.append("           union   ")
		.append("             select aui.id, max_create_date from bao_t_auto_invest_info aui ")
		.append("             inner join ")
		.append("             ( select lg.relate_primary, max(lg.create_date) max_create_date  ")
		.append("             from bao_t_log_info lg  ")
		.append("             where lg.relate_type = 'BAO_T_AUTO_INVEST_INFO'   ")
		.append("             and not exists (select 1 from bao_t_log_info li where li.relate_primary = lg.relate_primary and li.oper_after_content = '启用')  ")
		.append("             group by lg.relate_primary) a on a.relate_primary = aui.id ")
		.append("             where aui.open_status = '启用' ")
		.append("           union ")
		.append("             select aui.id, aui.create_date max_create_date from bao_t_auto_invest_info aui  ")
		.append("             where aui.open_status = '启用' and not exists ( ")
		.append("                   select 1 ")
		.append("                   from bao_t_log_info li where li.relate_primary = aui.id ")
		.append("             ) ")
		.append("          ) b on b.id = aui.id ")
		.append("      where aui.open_status = '启用'  ")
		;
		
		List<Object> args=new ArrayList<Object>();
		
		//期限（包含关系）
		if(!StringUtils.isEmpty(param.get("minTerm"))){
			sql.append(" and aui.limited_term >= ? ");
			int maxTerm = Integer.parseInt(param.get("maxTerm").toString());
			args.add(maxTerm);
		}
		if(!StringUtils.isEmpty(param.get("maxTerm"))){
			sql.append(" and aui.limited_term_min <= ? ");
			int minTerm = Integer.parseInt(param.get("minTerm").toString());
			args.add(minTerm);
		}
		//利率（包含关系）
		if(!StringUtils.isEmpty(param.get("minYearRate"))){
			sql.append(" and aui.limited_year_rate_max >= ? ");
			BigDecimal maxYearRate = ArithUtil.div(new BigDecimal(param.get("maxYearRate").toString()),new BigDecimal("100"));
			args.add(maxYearRate);
		}
		if(!StringUtils.isEmpty(param.get("maxYearRate"))){
			sql.append(" and aui.limited_year_rate <= ? ");
			BigDecimal minYearRate = ArithUtil.div(new BigDecimal(param.get("minYearRate").toString()),new BigDecimal("100"));
			args.add(minYearRate);
		}
		
		SqlCondition sqlCondition = new SqlCondition(sql,param,args )
		.addString("loanUnit", "aui.loan_unit")
		.addLike("canInvestProduct","aui.can_invest_product")//复投类型(包含关系)
//		.addString("availableAmount", "aci.account_available_amount")
//		.addString("limitedYearRate", "aui.limited_year_rate")
//		.addString("limitedTerm", "aui.limited_term")
//		.addString("repaymentMethod", "aui.repayment_method")
//		.addSql(" order by \"availableAmount\" desc,\"keepAvailableAmount\" desc,\"openDate\" desc ");
		.addSql(" order by \"openDate\" desc ");
		return sqlCondition;
	}
	
	/**
	 * 查询自动投标-可用金额汇总
	 * @return totalAmount:String:当前自动投标可用总金额
	 */
	public List<Map<String, Object>> queryAutoInvestTotalAmount(Map<String, Object> param) {
		SqlCondition sqlCondition = getAutoInvestListSql(param);
		StringBuilder sql = new StringBuilder()
		.append(" SELECT ")
		.append(" nvl(sum(\"availableAmount\"),0) \"totalAmount\", ")
		.append(" nvl(sum(\"keepAvailableAmount\"),0) \"totalKeepAvailableAmount\", ")
		.append(" nvl(sum(\"realAvailableAmount\"),0) \"totalRealAvailableAmount\" FROM (")
		.append(sqlCondition.toString())
		.append(" ) ")
		;
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql.toString(),sqlCondition.toArray());
//		BigDecimal totalAmount = jdbcTemplate.queryForObject(sql.toString(), sqlCondition.toArray(), BigDecimal.class);
		return queryForList;
	}
	
	/**
	 * 查询自动转让
	 */
	@Override
	public Page<Map<String, Object>> queryAutoTransferList(
			Map<String, Object> params) {
		SqlCondition sqlCondition = getAutoTransferListSql(params);
		sqlCondition.append(" ORDER BY \"openDate\"  ");
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}
	private SqlCondition getAutoTransferListSql(Map<String, Object> param){
		StringBuilder sql = new StringBuilder()
		.append("         select          ")
		.append("          ci.login_name \"custName\", ")
		.append("          l.loan_title \"loanTitle\", ")
		.append("          TRUNC(h.HOLD_SCALE *  decode(nvl(rp.repayment_status, '未还款'),     '未还款',    cr.value_repayment_before,   cr.value_repayment_after),   2) \"canTransferAmount\",          ")
		.append("          trunc(d.year_irr * 100,2) \"yearRate\",         ")
		.append("          ceil(months_between(l.invest_end_date, ?)) \"typeTerm\", ")
		.append("          l.REPAYMENT_METHOD \"repaymentMethod\", ")
		.append("          case when i.INVEST_MODE='加入' then '普通标的' when i.INVEST_MODE='转让' then '转让标的' else i.INVEST_MODE end \"investMode\", ")
		.append("          (to_date(i.effect_date, 'yyyyMMdd') + autoTran.Limited_Term * 30)    \"openDate\" ")
		.append("             from bao_t_wealth_hold_info h   ")
		.append("             inner join bao_t_loan_info l on l.id = h.loan_id   ")
		.append("             inner join bao_t_loan_detail_info d on d.loan_id = l.id   ")
		.append("             inner join bao_t_invest_info i on i.id = h.invest_id and i.loan_id = h.loan_id  ")
		.append("             inner join bao_t_auto_transfer_info autoTran on autoTran.Open_Status='启用' and autoTran.Cust_Id = i.cust_id ")
		.append("              inner join bao_t_cust_info ci    on autoTran.cust_id = ci.id  ")
		.append("             INNER JOIN bao_t_credit_right_value cr ON cr.loan_id = h.loan_id AND cr.value_date = to_char(?, 'yyyyMMdd') ")
		.append("             LEFT JOIN bao_t_repayment_plan_info rp ON rp.loan_id = h.loan_id and expect_repayment_date = to_char(?, 'yyyyMMdd') ")
		.append("             where i.invest_status in ('收益中')   ")
		.append("             and i.effect_date <= to_char(? - ?, 'yyyyMMdd')  ")  // 持有债权时间大于30天
		.append("             and l.invest_end_date > ? + ?   ") // 距离债权到期日大于30天
//		.append("             AND h.HOLD_SCALE * (SELECT decode(nvl(rp.repayment_status, '未还款'),'未还款'  ")
//		.append("                                              , cr.value_repayment_before  ")
//		.append("                                              , cr.value_repayment_after)  ")
//		.append("                                   FROM bao_t_credit_right_value cr  ")
//		.append("                                   left JOIN bao_t_repayment_plan_info rp   ")
//		.append("                                          ON rp.loan_id = cr.loan_id   ")
//		.append("                                         and expect_repayment_date = to_char(?, 'yyyyMMdd')  ")
//		.append("                                  WHERE cr.loan_id = h.loan_id  ")
//		.append("                                    AND cr.value_date = to_char(?, 'yyyyMMdd')  ")
//		.append("                 ) >= 1000  ")
		.append("             AND h.HOLD_SCALE * d.CREDIT_REMAINDER_PRINCIPAL >= 1000 ") // 债权转让调整本金>=1000转让
		.append("             AND l.SEAT_TERM != -1  ")
		.append("    ");		

		Date date = new Date();
		ArrayList<Object> listObject=new ArrayList<Object>();
		listObject.add(date);		
		listObject.add(date);		
		listObject.add(date);
		
		listObject.add(date);
		listObject.add(param.get("needHoldDay"));
		listObject.add(date);
		listObject.add(param.get("fromEndDay"));
		
//		listObject.add(date);
//		listObject.add(date);
		
		// update by liyy 2017/3/30 追加限制条件 
		sql
		.append(" AND (autoTran.MIN_YEAR_RATE IS NULL OR autoTran.MIN_YEAR_RATE <= d.YEAR_IRR) ")
		.append(" AND (autoTran.MAX_YEAR_RATE IS NULL OR autoTran.MAX_YEAR_RATE >= d.YEAR_IRR) ")
		.append(" AND (autoTran.MIN_TERM IS NULL OR autoTran.MIN_TERM * 30  <= l.LOAN_TERM*decode(l.LOAN_UNIT, '天',1,'月',30,1)) ")
		.append(" AND (autoTran.MAX_TERM IS NULL OR autoTran.MAX_TERM * 30  >= l.LOAN_TERM*decode(l.LOAN_UNIT, '天',1,'月',30,1)) ")
		.append(" AND (autoTran.REPAYMENT_METHOD IS NULL OR autoTran.REPAYMENT_METHOD LIKE '%'||l.REPAYMENT_METHOD||'%' ) ")
		.append(" AND (autoTran.CAN_TRANSFER_PRODUCT IS NULL OR autoTran.CAN_TRANSFER_PRODUCT LIKE '%'||(case when i.INVEST_MODE='加入' then '普通标的' when i.INVEST_MODE='转让' then '转让标的' else i.INVEST_MODE end )||'%') ")
		;
		
		if(!StringUtils.isEmpty(param.get("transferDateBegin"))){
			sql.append("and (to_date(i.effect_date,'yyyyMMdd')+autoTran.Limited_Term*30) >=? ");
			listObject.add(DateUtils.parseStandardDate(param.get("transferDateBegin").toString()));
		}
		if(!StringUtils.isEmpty(param.get("transferDateEnd"))){
			sql.append("and (to_date(i.effect_date,'yyyyMMdd')+autoTran.Limited_Term*30) < ? ");
			listObject.add(DateUtils.getAfterDay(DateUtils.parseStandardDate(param.get("transferDateEnd").toString()), 1));
		}
		
		SqlCondition sqlCondition = new SqlCondition(sql,param,listObject);
		
		return sqlCondition;
	}
	/**
	 * 查询自动转让-可用金额汇总
	 * @return totalAmount:String:当前自动转让可用金额
	 */
	public BigDecimal queryAutoTransferTotalAmount(Map<String, Object> param) {
		SqlCondition sqlCondition = getAutoTransferListSql(param);
		StringBuilder sql = new StringBuilder()
		.append(" SELECT sum(\"canTransferAmount\") \"totalAmount\" FROM ( ")
		.append(sqlCondition.toString())
		.append(" ) ")
		;
		BigDecimal totalAmount = jdbcTemplate.queryForObject(sql.toString(), sqlCondition.toArray(), BigDecimal.class);
		return totalAmount;
	}
	/**
	 * 查询转让审核列表
	 */
	public Page<Map<String, Object>> queryAuditTransferList(Map<String, Object> params) {
		SqlCondition sqlCondition = getAuditTransferList(params);
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}
	private SqlCondition getAuditTransferList(Map<String, Object> param){
		StringBuilder sql = new StringBuilder()
		.append(" select a.id \"transferApplyId\", ")
		.append("             a.audit_status \"auditStatus\",  ")
		.append("             a.create_date \"applyDate\", ")
		.append("             l.loan_code \"loanCode\",  ")
		.append("             l.id \"loanId\", ")
		.append("             l.loan_type \"loanType\", ")
		.append("             t.audit_time \"passDate\", ")
		.append("             l.rasie_end_date \"rasieDate\", ")
		.append("             trunc((1 - a.REMAINDER_TRADE_SCALE/a.TRADE_SCALE)*100,0) AS \"investScale\", ")
		.append("             a.sticky_status \"stickyStatus\", ")
//		.append("               TRUNC(TRUNC(a.remainder_trade_scale *   ")
//		.append("                           decode(nvl(r.repayment_status, '未还款'), '未还款',  ")
//		.append("                           c.value_repayment_before,  ")
//		.append("                           c.value_repayment_after),  ")
//		.append("                          2) * a.REDUCED_SCALE,  ")
//		.append("                    2) \"tradeAmount\",        ")
		.append("            case when a.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then ")
		.append("                 TRUNC(TRUNC(a.remainder_trade_scale *   ")
		.append("                            decode(nvl(r.repayment_status, '未还款'), '未还款',  ")
		.append("                                       c.value_repayment_before,  ")
		.append("                                       c.value_repayment_after)  ")
		.append("                       , 2) * a.REDUCED_SCALE ")
		.append("                 , 2) ")
		.append("                 else TRUNC_AMOUNT_WEB(a.trade_scale * d.CREDIT_REMAINDER_PRINCIPAL) end \"tradeAmount\", ")
		.append("            round(a.REDUCED_SCALE*100, 0) \"reducedScale\", ")// 折价比例
		.append("            NVL( ceil(months_between(l.invest_end_date, trunc(?))) || '个月', 0)  \"remainTerm\",  ")
		.append("            trunc(d.year_irr*100,2) \"yearRate\" ")
		.append("  from bao_t_loan_transfer_apply a  ")
		.append("        inner join bao_t_wealth_hold_info h on h.id = a.sender_hold_id  ")
		.append("        inner join bao_t_invest_info i on i.id = h.invest_id  ")
		.append("        inner join bao_t_loan_info l on l.id = h.loan_id  ")
		.append("        inner join bao_t_loan_detail_info d on d.loan_id = l.id  ")
		.append("        inner join bao_t_credit_right_value c on c.loan_id = h.loan_id and c.value_date = to_char(?, 'yyyyMMdd')  ")
		.append("        left  join bao_t_repayment_plan_info r on r.loan_id = h.loan_id and r.expect_repayment_date = to_char(?, 'yyyyMMdd')  ")
		.append("        left join bao_t_audit_info t on t.relate_primary = a.id  ")
		.append("  where 1=1");
		if(Constant.TRANSFER_APPLY_STATUS_PASS.equals(param.get("auditStatus"))){
			sql.append("  AND a.APPLY_STATUS in ('待转让', '部分转让成功')")
			.append("    AND a.CANCEL_STATUS = '未撤销' ")
			.append("    AND a.TRANSFER_END_DATE > ? ");
		}
		
		Date date = new Date();
		List<Object> args = new ArrayList<Object>();
		args.add((String)param.get("onlineTime"));
		
		args.add(date);
		args.add(date);
		args.add(date);
		if(Constant.TRANSFER_APPLY_STATUS_PASS.equals(param.get("auditStatus"))){
			args.add(date);
		}
		
		
		SqlCondition sqlCondition = new SqlCondition(sql, param, args)
		.addString("auditStatus", "a.audit_status")
		.addString("loanCode", "l.loan_code")
		;
		if(Constant.TRANSFER_APPLY_STATUS_UNREVIEW.equals(param.get("auditStatus"))){
		sqlCondition.append("order by decode(\"auditStatus\", '待审核',a.create_date)asc");
		}
		if(Constant.TRANSFER_APPLY_STATUS_PASS.equals(param.get("auditStatus"))){
			sqlCondition.append("order by a.sticky_status, a.sticky_level, trunc(l.rasie_end_date) , trunc((1 - a.REMAINDER_TRADE_SCALE/a.TRADE_SCALE)*100,0) desc, trunc(d.year_irr*100,2) desc");
		}
		
		return sqlCondition;
	}
	public Page<Map<String, Object>> queryNewerFlagList(Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT loan.ID \"disperseId\" ")
		.append("      , loan.LOAN_TYPE \"disperseType\" ")
		.append("      , loan.LOAN_DESC \"loanUse\" ")
		.append("      , lc.CUST_GENDER \"loanUserSex\" ")
		.append("      , lc.CUST_AGE \"loanUserAge\" ")
		.append("      , lc.OPEN_PROVINCE || lc.OPEN_CITY \"loanUserCity\" ")
		.append("      , loan.MEMO \"description\" ")
		.append("      , nvl(ld.YEAR_IRR,0) \"yearRate\" ")
		.append("      , nvl(loan.LOAN_TERM,0) \"typeTerm\" ")
		.append("      , loan.LOAN_UNIT \"loanUnit\" ")
		.append("      , loan.LOAN_AMOUNT \"loanAmount\" ")
		.append("      , loan.INVEST_MIN_AMOUNT \"investMinAmount\" ")
		.append("      , loan.INVEST_MAX_AMOUNT \"investMaxAmount\" ")
		.append("      , loan.INCREASE_AMOUNT \"increaseAmount\" ")
		.append("      , loan.PUBLISH_DATE \"publishDate\" ")
		.append("      , TRUNC(loan.LOAN_AMOUNT,2) - nvl(TRUNC(pi.ALREADY_INVEST_AMOUNT,2),0) \"remainAmount\" ")
		.append("      , nvl(TRUNC(pi.ALREADY_INVEST_SCALE*100),0) \"investScale\" ")
		.append("      , nvl(pi.ALREADY_INVEST_PEOPLES, 0) \"alreadyInvestPeoples\" ")
		.append("      , loan.REPAYMENT_METHOD \"repaymentMethod\" ")
		.append("      , '' AS  \"security\" ")
		.append("      , loan.LOAN_STATUS \"disperseStatus\" ")
		.append("      , nvl(loan.REBATE_RATIO,0) \"rebateRatio\" ")
		.append("      , loan.LOAN_TITLE \"loanTitle\" ")
		.append("      , loan.newer_flag \"newerFlag\" ")
		.append("      , nvl(loan.award_rate,0) \"awardRate\" ")
		.append("      , CASE WHEN loan.SEAT_TERM = -1 OR loan.SEAT_TERM IS NULL THEN '不允许转让' WHEN loan.SEAT_TERM = 0 THEN '可以立即转让' ELSE '出借'||loan.SEAT_TERM||'天即可转让' END \"transferCondition\" ")
		.append("  FROM BAO_T_LOAN_INFO loan ")
		.append(" INNER JOIN BAO_T_LOAN_CUST_INFO lc ON lc.ID = loan.RELATE_PRIMARY ")
		.append(" LEFT JOIN BAO_T_LOAN_DETAIL_INFO ld ON ld.LOAN_ID = loan.ID ")
		.append("  LEFT JOIN BAO_T_PROJECT_INVEST_INFO pi ON pi.LOAN_ID=loan.ID ")
		.append(" WHERE 1=1 ");
		if (param.get("isNewerFlag") != null && (Boolean) param.get("isNewerFlag")) {
			sql.append("   AND loan.LOAN_STATUS in ( '募集中', '启用')");
		} else {
			sql.append("   AND loan.LOAN_STATUS in ( '募集中','满标复核', '启用') ");
		}
		//新增体验标条件-huifei
		sql.append("   AND loan.newer_flag in ('新手标', '体验标') ");

		List<Object> args = new ArrayList<Object>();
		// 排序
		sql.append(" order by loan.newer_flag asc, decode(loan.LOAN_STATUS,'募集中',1,'满标复核',2) ,loan.publish_date");
		return repositoryUtil.queryForPageMap(sql.toString(), args.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}

	@Override
	public int updateISRunAutoInvest() {
		//优选项目标的 把跑过智能投顾标识修改为Y
		StringBuilder sql = new StringBuilder()
		.append(" update bao_t_loan_info t  ")
		.append(" set t.is_run_auto_invest = 'Y' ")
		.append(" where t.is_run_auto_invest = 'N'  ")
		.append(" and exists( ")
		.append("       select 1 ")
		.append("       FROM BAO_T_LOAN_INFO loan  ")
		.append(" 	    INNER JOIN BAO_T_LOAN_CUST_INFO lc ON lc.ID = loan.RELATE_PRIMARY  ")
		.append(" 		  LEFT JOIN BAO_T_LOAN_DETAIL_INFO ld ON ld.LOAN_ID = loan.ID  ")
		.append(" 		  LEFT JOIN BAO_T_PROJECT_INVEST_INFO pi ON pi.LOAN_ID=loan.ID  ")
		.append("        ) ");
		//审核通过标的 把跑过智能投顾标识修改为Y
		StringBuilder sqlString = new StringBuilder()
		.append(" update bao_t_loan_transfer_apply lta   ")
		.append(" set lta.is_run_auto_invest = 'Y' ")
		.append(" where lta.is_run_auto_invest = 'N'  ")
//		.append(" AND lta.APPLY_STATUS in ('待转让', '部分转让成功') ")
//		.append(" and lta.CANCEL_STATUS = '未撤销' ")
		.append(" and lta.AUDIT_STATUS = '通过'   ");
		int[] count = jdbcTemplate.batchUpdate(sql.toString(),sqlString.toString());
//		int update02 = jdbcTemplate.update(sqlString.toString());
		return (count != null ? count.length : 0);
	}
	
	/**
	 * 领取列表查询
	 * @author  zhangyb
	 * @date    2017-06-12 10:01:18
	 * @param params
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>loanCode       :String:借款编号(可以为空)</tt><br>
     *      <tt>custName       :String:客户姓名(可以为空)</tt><br>
     *      <tt>mobile         :String:手机号码(可以为空)</tt><br>
     *      <tt>credentialsCode:String:证件号码(可以为空)</tt><br>
     *      <tt>loanTerm       :String:借款期限(可以为空)</tt><br>
     *      <tt>loanType       :String:借款类型(可以为空)</tt><br>
     *      <tt>loanStatus     :String:借款状态(可以为空)</tt><br>
     *      <tt>repaymentMethod:String:还款方式(可以为空)</tt><br>
     *      <tt>createDateStart:String:申请日期-区间头(可以为空)</tt><br>
     *      <tt>createDateEnd  :String:申请日期-区间末(可以为空)</tt><br>
     *      <tt>auditTime       :String:审核日期(可以为空)</tt><br>
	 * @return
     *      <tt>data           :String:List<Map<String,Object>></tt><br>
     *      <tt>loanId         :String:借款信息表主键Id</tt><br>
     *      <tt>loanCode       :String:借款编号</tt><br>
     *      <tt>custName       :String:客户姓名</tt><br>
     *      <tt>mobile         :String:手机号码</tt><br>
     *      <tt>credentialsCode:String:证件类型</tt><br>
     *      <tt>loanType       :String:借款类型</tt><br>
     *      <tt>loanAmount     :String:借款金额（元）</tt><br>
     *      <tt>loanTerm       :String:借款期限</tt><br>
     *      <tt>yearIrr        :String:借款利率 （详细表）</tt><br>
     *      <tt>repaymentMethod:String:还款方式</tt><br>
     *      <tt>loanStatus     :String:借款状态</tt><br>
     *      <tt>createDate     :String:申请日期</tt><br>
     *      <tt>auditTime      :String:审核日期(可以为空)</tt><br>
	 * @throws SLException
	 */
	public Page<Map<String, Object>> queryReceiveList(Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT loan.ID \"loanId\" ")
		.append("      , loan.LOAN_CODE \"loanCode\" ")
		.append("      , lc.CUST_NAME \"custName\" ")
		.append("      , lc.MOBILE \"mobile\" ")
		.append("      , decode(lc.CREDENTIALS_CODE,NULL,'',substr(lc.CREDENTIALS_CODE,1,3)||'****' || substr(lc.CREDENTIALS_CODE,-4)) \"credentialsCode\" ")
		.append("      , loan.LOAN_DESC \"loanDesc\" ")
		.append("      , loan.LOAN_TYPE \"loanType\" ")
		.append("      , loan.LOAN_AMOUNT \"loanAmount\" ")
		.append("      , loan.LOAN_TERM \"loanTerm\" ")
		.append("      , loan.LOAN_UNIT \"loanUnit\" ")
		.append("      , nvl(trunc(round(loan.award_rate,8)*100,2),0) \"awardRate\" ")// 奖励利率
		.append("      , round(ld.YEAR_IRR*100, 2) \"yearIrr\" ")// 需求
		.append("      , loan.REPAYMENT_METHOD \"repaymentMethod\" ")
		.append("      , loan.LOAN_STATUS \"loanStatus\" ")
		.append("      , loan.APPLY_TIME \"createDate\" ")
		.append("      , lr.FLAG \"loanFlag\" ")
		.append("      , loan.LOAN_TITLE \"loanTitle\" ")
		.append("      , la.AUDIT_TIME \"auditTime\" ")
		.append("      , la	.AUDIT_STATUS \"auditStatus\" ")
		.append(" FROM BAO_T_LOAN_INFO loan ")
		.append(" INNER JOIN BAO_T_LOAN_CUST_INFO lc ON lc.ID = loan.RELATE_PRIMARY ")
		.append(" INNER JOIN BAO_T_LOAN_DETAIL_INFO ld ON ld.LOAN_ID = loan.ID ")
		.append(" LEFT JOIN BAO_T_AUDIT_INFO la ON la.RELATE_PRIMARY = loan.ID ")
		.append(" AND la.APPLY_TYPE = '"+Constant.OPERATION_TYPE_60+"'")
		.append(" LEFT JOIN BAO_T_LOAN_REBATE_INFO lr ON lr.REPAYMENT_METHOD = loan.REPAYMENT_METHOD ")
		.append("        AND lr.LOAN_TERM = loan.LOAN_TERM ")
		.append("        AND lr.PRODUCT_TYPE = loan.LOAN_TYPE ")
		.append(" WHERE loan.LOAN_STATUS IS NOT NULL ")
		.append("        AND loan.RECEIVE_STATUS = '已领取' ")
		.append(" 		 AND loan.RECEIVE_USER = '"+param.get("userId")+"'")
		;
		SqlCondition sqlCondition = new SqlCondition(sql, param)
		.addString("loanCode", "loan.LOAN_CODE")
		.addString("custName", "lc.CUST_NAME")
		.addString("mobile", "lc.MOBILE")
		.addString("credentialsCode", "lc.CREDENTIALS_CODE")
		.addString("loanTerm", "loan.LOAN_TERM")
		.addString("loanUnit", "loan.LOAN_UNIT")
		.addString("loanType", "loan.LOAN_TYPE")
		.addString("loanStatus", "loan.LOAN_STATUS")
		.addString("repaymentMethod", "loan.REPAYMENT_METHOD")
		.addBeginDate("createDateStart", "loan.APPLY_TIME")
		.addEndDate("createDateEnd", "loan.APPLY_TIME")
		.addString("loanDesc", "loan.LOAN_DESC")
		.addString("loanTitle", "loan.LOAN_TITLE")
		.addSql(" ORDER BY decode(loan.LOAN_STATUS,'待审核',1,'审核回退',2,'通过',3,'待发布',4,'募集中',5,'满标复核',6,'复核通过',7,'正常',8,'逾期',9,'提前结清',10,'已到期',11,'流标',12,'复核拒绝',13,'拒绝',14) ")
		.addSql("        , loan.APPLY_TIME ")
		.addSql("        , loan.CREATE_DATE ")
		;
		
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}
	
	/**
	 * 优选项目列表可投产品
	 *
	 * @author  wangjf
	 * @date    2017年7月20日 下午12:53:48
	 * @return
	 */
	private String getCanInvestProjectForList() {
		String value = paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_20).getValue();
		String loanValue = "'" + value.replaceAll(",", "','") + "'";
		return loanValue;
	}
	
	/**
	 * 可投产品
	 *
	 * @author  wangjf
	 * @date    2017年7月20日 下午12:53:43
	 * @return
	 */
	private String getCanInvestProject() {
		String value = paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_05).getValue();
		String loanValue = "'" + value.replaceAll(",", "','") + "'";
		return loanValue;
	}

}
