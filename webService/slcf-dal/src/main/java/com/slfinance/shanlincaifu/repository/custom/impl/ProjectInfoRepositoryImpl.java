/** 
 * @(#)ProjectInfoRepositoryImpl.java 1.0.0 2016年1月5日 下午1:40:42  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.PaymentRecordInfoRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.AccountFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.ProjectInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SqlCondition;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;


/**   
 * 自定义项目数据访问实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月5日 下午1:40:42 $ 
 */
@Repository
public class ProjectInfoRepositoryImpl implements ProjectInfoRepositoryCustom {
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private AccountFlowInfoRepositoryCustom accountFlowInfoRepositoryCustom;
	
	@Autowired
	private InvestInfoRepository investInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private AccountFlowInfoRepository accountFlowInfoRepository;
	
	@Autowired
	private PaymentRecordInfoRepository paymentRecordInfoRepository;

	@Override
	public Page<Map<String, Object>> queryAllProjectPage(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" select btpi.id                     \"id\", ")
		.append("        btpi.project_type           \"projectType\", ")
		.append("        btpi.project_no             \"projectNo\", ")
		.append("        btpi.company_name           \"companyName\", ")
		.append("        btpi.AWARD_RATE             \"awardRate\", ")
		.append("        btpi.AWARD_RATE * 100       \"awardRate2\", ") //大后台
		.append("        btpi.project_name           \"projectName\", ")
		.append("        btpi.project_status         \"projectStatus\", ")
		.append("        btpi.year_rate              \"yearRate\", ")
		.append("        btpi.year_rate * 100        \"yearRate2\", ") //大后台
		.append("        btpi.project_total_amount   \"projectTotalAmount\", ")
		.append("        nvl(btpii.already_invest_amount, 0) \"investTotalAmount\", ")
		.append("        case when btpi.project_status in ('"+Constant.PROJECT_STATUS_12+"', '"+Constant.PROJECT_STATUS_06+"') then ")
		.append("            (btpi.project_total_amount - btpii.already_invest_amount) else 0 end   \"currUsableValue\",")
		.append("        case when btpi.project_status in ('"+Constant.PROJECT_STATUS_12+"', '"+Constant.PROJECT_STATUS_06+"') then ")
		.append("            btpii.already_invest_scale  else 1 end \"investScale\", ")
		.append("        btpi.type_term              \"typeTerm\", ")
		.append("        btpi.seat_term              \"seatTerm\", ")
		.append("        btpi.invest_min_amount      \"investMinAmount\", ")
		.append("        btpi.invest_max_amount      \"investMaxAmount\", ")
		.append("        btpi.increase_amount        \"increaseAmount\", ")
		.append("        btpi.release_date           \"releaseDate\", ")
		.append("        btpi.effect_date            \"effectDate\", ")
		.append("        btpi.project_end_date       \"projectEndDate\", ")
		.append("        btpi.repaymnet_method       \"repaymnetMethod\", ")
		.append("        btpi.ensure_method          \"ensureMethod\", ")
		.append("        btpi.is_atone               \"isAtone\", ")
		.append("        '当日计息' 					 \"interestsMethod\", ")
		.append("		 btpii.already_invest_peoples  \"investPeoples\" ")
		.append("   from bao_t_project_info btpi, bao_t_project_invest_info btpii ")
		.append("  where btpi.id = btpii.project_id(+) %s");
		
		String order = (String)params.get("order");
		if("front".equals(order)){
			sql.append(" order by decode(btpi.project_status, '发布中', 1,'满标复核',2,'还款中',3), btpi.type_term, btpi.release_date desc, btpi.effect_date asc");
		} else {
			sql.append(" order by btpi.release_date desc, btpi.project_no desc");
		}

		StringBuilder whereSqlString= new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(whereSqlString, params);
		sqlCondition.addList("projectStatusList", "btpi.project_status")
					.addString("projectType", "btpi.project_type")
					.addString("companyId", "btpi.cust_id")
					.addString("projectNo", "btpi.project_no")
					.addString("projectName", "btpi.project_name")
					.addString("productTerm", "btpi.type_term")
					.addString("repaymentMethod", "btpi.repaymnet_method")
					.addString("projectStatus", "btpi.project_status")
					.addBeginDate("beginReleaseDate", "btpi.release_date")
					.addEndDate("endReleaseDate", "btpi.release_date")
					.addBeginDate("beginEffectDate", "btpi.effect_date")
					.addEndDate("endEffectDate", "btpi.effect_date")
					.addBeginDate("beginProjectEndDate", "btpi.project_end_date")
					.addEndDate("endPojectEndDate", "btpi.project_end_date")
					.addList("projectIdList", "btpi.id");

		return repositoryUtil.queryForPageMap(
				String.format(sql.toString(), sqlCondition.toString()),
				sqlCondition.toArray(),
				Integer.parseInt(params.get("start").toString()),
				Integer.parseInt(params.get("length").toString()));
	}

	@Override
	public Map<String, Object> queryProjectDetail(Map<String, Object> params) {
		Map<String, Object> resultMap = Maps.newHashMap();
		List<Object> listObject = new ArrayList<Object>();
		String startAppendStr =  "<!doctype html>\n" +
	            "<html>\n" +
	            "<head>\n" +
	            "<meta charset=\"utf-8\">\n" +
	            "<meta name=\"HandheldFriendly\" content=\"true\">\n" +
	            "<meta name=\"MobileOptimized\" content=\"width\">\n" +
	            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1,user-scalable=no\" />\n" +
	            "<style>\n" +
	            "body{margin:0;word-wrap:break-word;}\n" +
	            "img{border: 0;vertical-align:middle; max-width:100%;height:auto;}\n" +
	            "#wrapper { padding:10px;}\n" +
	            "</style>\n" +
	            "</head>\n" +
	            "<body>\n" +
	            "<div id=\"wrapper\">";
		
		String endAppendStr =  "</div>\n" +
	            "</body>\n" +
	            "</html>\n";
		
		StringBuffer sql = new StringBuffer()
		.append(" select btpi.id                      \"id\", ")
		.append("        btpi.project_type            \"projectType\", ")
		.append("        btpi.project_no              \"projectNo\", ")
		.append("        btpi.company_name            \"companyName\", ")
		.append("        btpi.cust_id                 \"companyId\", ")
		.append("        btpi.project_name            \"projectName\", ")
		.append("        btpi.project_status          \"projectStatus\", ")
		.append("        btpi.year_rate               \"yearRate\", ")
		.append("        btpi.year_rate * 100          \"yearRate2\", ") //大后台
		.append("        btpi.project_total_amount    \"projectTotalAmount\", ")
		.append("        case when btpi.project_status in ('"+Constant.PROJECT_STATUS_12+"', '"+Constant.PROJECT_STATUS_06+"') then ")
		.append("            (btpi.project_total_amount - btpii.already_invest_amount) else 0 end  \"currUsableValue\", ")
		.append("        case when btpi.project_status in ('"+Constant.PROJECT_STATUS_12+"', '"+Constant.PROJECT_STATUS_06+"') then ")
		.append("            btpii.already_invest_scale else 1 end  \"investScale\", ")
		.append("        btpii.already_invest_peoples \"investPeoples\", ")
		.append("        btpii.already_invest_amount  \"alreadyInvestAmount\", ")
		.append("        btpi.type_term               \"typeTerm\", ")
		.append("        btpi.seat_term               \"seatTerm\", ")
		.append("        btpi.invest_min_amount       \"investMinAmount\", ")
		.append("        btpi.invest_max_amount       \"investMaxAmount\", ")
		.append("        btpi.increase_amount         \"increaseAmount\", ")
		.append("        btpi.release_date            \"releaseDate\", ")
		.append("        btpi.effect_date             \"effectDate\", ")
		.append("        btpi.project_end_date        \"projectEndDate\", ")
		.append("        btpi.repaymnet_method        \"repaymnetMethod\", ")
		.append("        btpi.ensure_method           \"ensureMethod\", ")
		.append("        decode(btpi.is_atone, '"+Constant.IS_ATONE_YES+"', '满' || to_char(btpi.seat_term) || '天可赎', btpi.is_atone)                \"isAtone\", ")
		.append("        btpi.project_descr           \"projectDescr\", ")
		.append("        btpi.company_descr           \"companyDescr\", ")
		.append("        '" + startAppendStr + "' || btpi.project_descr || '" + endAppendStr + "'           \"decoratedProjectDescr\", ")
		.append("        '" + startAppendStr + "' || btpi.company_descr || '" + endAppendStr + "'           \"decoratedCompanyDescr\", ")
		.append("        btpi.actual_year_rate        \"actualYearRate\", ")
		.append("        btpi.actual_year_rate *100   \"actualYearRate2\", ") //大后台
		.append("        btpi.award_rate              \"awardRate\", ")
		.append("        btpi.award_rate * 100        \"awardRate2\", ") //大后台
		.append("        '当日计息'                      \"interestsMethod\"  ")
		.append("   from bao_t_project_info btpi, bao_t_project_invest_info btpii ")
		.append("  where btpi.id = btpii.project_id(+) ");
		
		String projectId = (String) params.get("projectId");
		if(!StringUtils.isEmpty(params.get("projectId"))){	
			sql.append(" and btpi.id = ?");
			listObject.add(projectId);
		}
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
		if(list.size() > 0){
			resultMap = list.get(0);
		}
		
		//项目/个人最大投资金额
		BigDecimal investMaxAmount = new BigDecimal(resultMap.get("investMaxAmount") == null ? "0" : resultMap.get("investMaxAmount").toString());
		//项目已投资金额
		BigDecimal alreadyInvestAmount = new BigDecimal(resultMap.get("alreadyInvestAmount") == null ?
				"0" : resultMap.get("alreadyInvestAmount").toString());
		//剩余投资金额
		BigDecimal remainInvestAmount = investMaxAmount.subtract(alreadyInvestAmount);
		//可购份额
		BigDecimal availableInvestAmount = BigDecimal.ZERO;
		//可用余额
		BigDecimal availableAmount = BigDecimal.ZERO;
		
		String custId = (String)params.get("custId");
		if(!StringUtils.isEmpty(custId)){
			AccountInfoEntity accountInfo = accountInfoRepository.findByCustId(custId);
			availableAmount = accountInfo != null ? accountInfo.getAccountAvailableAmount() : BigDecimal.ZERO;
			//个人投资
			BigDecimal investAmount = investInfoRepository.queryAlreadyInvestAmountByCustIdAndProjectId(custId, projectId);
			//个人可投资金额
			BigDecimal custAvailableInvestAmount = investMaxAmount.subtract(investAmount);
			availableInvestAmount = remainInvestAmount.min(custAvailableInvestAmount);
		} else {
			availableInvestAmount = remainInvestAmount;
		}
		//项目状态
		String projectStatus = (String) resultMap.get("projectStatus");
		if(!Constant.PROJECT_STATUS_06.equals(projectStatus)){
			availableInvestAmount = BigDecimal.ZERO;
		}
		resultMap.put("availableAmount", availableAmount);
		resultMap.put("availableInvestAmount", availableInvestAmount);
		
		return resultMap;
	}

	@Override
	public ResultVo findProjectIncomeByCustId(Map<String, Object> params) {
		Map<String, Object> resultMap = Maps.newHashMap();
		String custId = (String) params.get("custId");

		List<String> list = new ArrayList<String>();
		list.add(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_ALL_PROJECT);
		//累计收益
		params.put("tradeType", list);
		BigDecimal sumTradeAmount = accountFlowInfoRepositoryCustom.findIncomeByCustId(params);
		//已获收益
		BigDecimal receivedAmount = paymentRecordInfoRepository.queryReceivedAmountByCustId(custId);
		//在投金额
		BigDecimal holdAmount = investInfoRepository.queryTotalProjectInvestAmountByCustId(custId, Constant.VALID_STATUS_VALID);
		//项目状态为 发布中 和满标复合的在投金额
		BigDecimal investAmount = investInfoRepository.queryProjectInvestAmountByCustId(custId);
		//代收本金
		BigDecimal exceptRepayPrincipal = ArithUtil.add(investAmount, investInfoRepository.queryProjectRemainderPrincipal(custId));
		//持有份额
		BigDecimal totalAmount = holdAmount;		
		//预期收益
		BigDecimal exceptAmount = investInfoRepository.queryProjectExceptAmountByCustId(custId);
		//待收收益
		BigDecimal exceptRepayAmount = exceptAmount;
		//累计赎回
		BigDecimal totalAtoneAmount = accountFlowInfoRepository.sumTradeAmountByCustIdAndTradeType(custId, SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_PROJECT);
		//累计投资
		BigDecimal totalInventAmount = investInfoRepository.queryTotalProjectInvestAmountByCustId(custId);
		
		resultMap.put("holdAmount", holdAmount == null ? BigDecimal.ZERO : holdAmount);
		resultMap.put("sumTradeAmount", sumTradeAmount == null ? BigDecimal.ZERO : sumTradeAmount);
		resultMap.put("totalAmount", totalAmount == null ? BigDecimal.ZERO : totalAmount);
		resultMap.put("exceptRepayPrincipal", exceptRepayPrincipal == null ? BigDecimal.ZERO : exceptRepayPrincipal);
		resultMap.put("receivedAmount", receivedAmount == null ? BigDecimal.ZERO : receivedAmount);
		resultMap.put("exceptAmount", exceptAmount == null ? BigDecimal.ZERO : exceptAmount);
		resultMap.put("exceptRepayAmount", exceptRepayAmount == null ? BigDecimal.ZERO : exceptRepayAmount);
		resultMap.put("totalAtoneAmount", totalAtoneAmount == null ? BigDecimal.ZERO : totalAtoneAmount);
		resultMap.put("totalInventAmount", totalInventAmount == null ? BigDecimal.ZERO : totalInventAmount);
		
		return new ResultVo(true, "企业借款收益情况查询成功", resultMap);
	}
	
	@Override
	public Page<Map<String, Object>> queryProjectList(Map<String, Object> params) {
		
		StringBuilder sqlString = new StringBuilder()		
		.append("  select t.id \"id\",  t.project_type \"projectType\", t.project_no \"projectNo\", ")
		.append("        t.company_name \"companyName\", t.project_name \"projectName\", t.project_status \"projectStatus\", ")
		.append("        t.year_rate \"yearRate\", t.project_total_amount \"projectTotalAmount\", (t.project_total_amount - s.already_invest_amount) \"currUsableValue\", ")
		.append("        s.already_invest_scale \"investScale\", t.type_term \"typeTerm\", t.seat_term \"seatTerm\", ")
		.append("        t.invest_min_amount \"investMinAmount\", t.invest_max_amount \"investMaxAmount\", t.increase_amount \"increaseAmount\", ")
		.append("        t.release_date \"releaseDate\", t.effect_date \"effectDate\", t.project_end_date \"projectEndDate\", ")
		.append("        t.repaymnet_method \"repaymnetMethod\", t.ensure_method \"ensureMethod\", t.is_atone \"isAtone\", ")
		.append("        t.project_descr \"projectDescr\", t.company_descr \"companyDescr\", '当日计息' \"interestsMethod\" ")
		.append("  from bao_t_project_info t  ")
		.append("  inner join bao_t_project_invest_info s on t.id = s.project_id ")
		.append("  where 1 = 1  ");
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, params);
		sqlCondition.addString("projectId", "t.id")
					.addString("projectType", "t.project_type")
		            .addString("companyName", "t.company_name") 
		            .addString("projectNo", "t.project_no")	
		            .addString("projectName", "t.project_name")
		            .addString("productTerm", "t.type_term")
		            .addString("repaymnetMethod", "t.repaymnet_method")
		            .addString("projectStatus", "t.project_status")
		            .addBeginDate("beginReleaseDate", "t.release_date")
		            .addEndDate("endReleaseDate", "t.release_date")
		            .addBeginDate("beginEffectDate", "t.effect_date")
		            .addEndDate("endEffectDate", "t.effect_date")
		            .addBeginDate("beginProjectEndDate", "t.project_end_date")
		            .addEndDate("endPojectEndDate", "t.project_end_date")
		            .addList("projectStatusList", "t.project_status");
				
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.valueOf(params.get("start").toString()),
				Integer.valueOf(params.get("length").toString()));
	}

	@Override
	public Page<Map<String, Object>> queryProjectJoinList(
			Map<String, Object> params) {
		
		StringBuilder sqlString = new StringBuilder()
		.append("  select m.login_name \"loginName\", t.invest_amount \"tradeAmount\", t.create_date \"tradeDate\", ")
		.append("         m.mobile \"mobile\", t.invest_date \"investDate\", t.cust_id \"custId\", ")
		.append("         m.open_id \"openId\" ")
		.append("  from bao_t_invest_info t, bao_t_cust_info m ")
		.append("  where t.cust_id = m.id ");
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, params);
		sqlCondition.addString("projectId", "t.project_id")
					.addString("loanId", "t.loan_id");
		
		return repositoryUtil.queryForPageMap(sqlString.toString(), sqlCondition.toArray(), Integer.valueOf(params.get("start").toString()),
				Integer.valueOf(params.get("length").toString()));
	}

	@Override
	public List<Map<String, Object>> queryWaitingSubsidyList(
			Map<String, Object> params) {
		
		StringBuilder sqlString = new StringBuilder()
		.append("   select m.cust_id \"custId\", nvl(m.invest_amount * t.year_rate/12 / to_number(to_char(last_day(t.release_date), 'DD')), 0) \"tradeAmount\",  ")
		.append("          n.id \"accountId\", n.account_total_amount \"accountTotalAmount\", n.account_freeze_amount \"accountFreezeAmount\", n.account_available_amount \"accountAvailableAmount\", ")
		.append("          n.version \"accountVersion\", m.Id \"investId\", t.project_name \"projectName\", t.project_no \"projectNo\" ")
		.append("   from bao_t_project_info t, bao_t_project_invest_info s, bao_t_invest_info m, bao_t_account_info n ")
		.append("   where t.id = s.project_id  ")
		.append("   and t.id = m.project_id ")
		.append("   and m.cust_id = n.cust_id ");
		
		List<Object> objList = Lists.newArrayList();
		if(!StringUtils.isEmpty(params.get("investDate"))) {
			sqlString.append(" and m.invest_date <= ? ");
			objList.add((String)params.get("investDate"));
		}
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, params, objList);
		sqlCondition.addList("projectStatusList", "t.project_status");
		
		return repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
	}

	@Override
	public Page<Map<String, Object>> queryMyProjectPage(
			Map<String, Object> params) {
		StringBuilder sqlString = new StringBuilder()
		.append("  select btpi.project_no 	\"projectNo\", ")
		.append("		  btpi.id    \"id\",")
		.append("		  btpi.project_name   \"projectName\",")
		.append("		  btpi.company_name   \"companyName\",")
		.append("		  btpi.project_type   \"projectType\",")
		.append("		  btpi.project_total_amount   \"projectTotalAmount\",")
		.append("		  (btpi.project_total_amount - btpii.already_invest_amount)    \"currUsableValue\",")
		.append("		  max(btpii.already_invest_scale)  \"investScale\",")
		.append("		  btpi.type_term    \"typeTerm\",")
		.append("		  btpi.seat_term    \"seatTerm\",")
		.append("		  btpi.invest_min_amount  \"investMinAmount\",")
		.append("		  btpi.invest_max_amount   \"investMaxAmount\",")
		.append("		  btpi.increase_amount    \"increaseAmount\",")
		.append("		  btpi.release_date   \"releaseDate\",")
		.append("		  btpi.ensure_method   \"ensureMethod\",")
		.append("         btpi.year_rate \"yearRate\", ")
		.append("         btpi.award_rate \"awardRate\", ")
		.append("         btpi.repaymnet_method \"repaymnetMethod\", ")
		.append("         btpi.effect_date \"effectDate\", ")
		.append("         btpi.project_end_date \"projectEndDate\", ")
		.append("         sum(btii.invest_amount) \"investAmount\",       ")
		.append("         case when btpi.project_status in ('"+Constant.PROJECT_STATUS_08+"', '"+Constant.PROJECT_STATUS_10+"') then nvl((max(btrpi.repayment_interest) * sum(btii.invest_amount) / ")
		.append("         		max(btpii.already_invest_amount)), 0) + ")
		.append("            (case when max(btrpi.project_id) is null then 0 else")
		.append("               nvl(trunc(sum(btii.invest_amount) * btpi.award_rate * btpi.type_term / 12, 2), 0) end ) else 0 end \"exceptRepayAmount\",    ")
		.append("         btpi.project_status \"projectStatus\", ")
		.append("         btpi.project_status \"investStatus\" ")
		.append("    from bao_t_invest_info         btii, ")
		.append("         bao_t_project_info        btpi, ")
		.append("         bao_t_project_invest_info btpii, ")
		.append("         (select sum(a.repayment_interest) as repayment_interest,a.project_id as project_id from bao_t_repayment_plan_info a")
		.append("              where a.repayment_status = '未还款' and a.is_riskamount_repay = '否' group by a.project_id) btrpi ")
		.append("   where btii.project_id = btpi.id ")
		.append("     and btpi.id = btpii.project_id ")
		.append("     and btpi.id = btrpi.project_id (+) %s ")
		.append("   group by btii.project_id, btpi.id, btpi.project_no, btpi.project_name, btpi.company_name, btpi.project_type, btpi.project_total_amount, btpii.already_invest_amount, btpi.type_term, btpi.seat_term, ")
		.append("     btpi.invest_min_amount, btpi.invest_max_amount, btpi.increase_amount, btpi.release_date, btpi.ensure_method, btpi.year_rate, btpi.award_rate, btpi.repaymnet_method, btpi.effect_date, ")
		.append("     btpi.project_end_date, btpi.project_status ")
		.append("   order by decode(btpi.project_status, '"+ Constant.PROJECT_STATUS_06 +"', 1, '"+ Constant.PROJECT_STATUS_07 +"', 2, '"+ Constant.PROJECT_STATUS_08 +"', 3, '"+ Constant.PROJECT_STATUS_10 +"', 4, ")
		.append("         '"+ Constant.PROJECT_STATUS_09 +"', 5, '"+ Constant.PROJECT_STATUS_11 +"', 6, '"+ Constant.PROJECT_STATUS_12 +"', 7),")
		.append(" btpi.effect_date, btpi.project_end_date ");
		
		StringBuilder whereSqlString= new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(whereSqlString, params);
		sqlCondition.addString("custId", "btii.cust_id")
					.addBeginDate("beginInvestDate", "btpi.effect_date")
					.addEndDate("endInvestDate", "btpi.effect_date")
					.addString("projectStatus", "btpi.project_status")
					.addString("investStatus", "btpi.project_status");
		
		return repositoryUtil.queryForPageMap(
				String.format(sqlString.toString(), sqlCondition.toString()),
				sqlCondition.toArray(),
				Integer.parseInt(params.get("start").toString()),
				Integer.parseInt(params.get("length").toString()));
	}

	@Override
	public ResultVo queryProjectContract(Map<String, Object> params) {
		StringBuilder sqlString = new StringBuilder()
		.append(" select btpi.company_name \"companyName\", ")
		.append("        btpi.effect_date \"effectDate\", ")
		.append("        btpi.project_end_date \"projectEndDate\", ")
		.append("        btci.cust_name \"custName\", ")
		.append("        btci.credentials_code \"credentialsCode\", ")
		.append("        btii.invest_amount \"investAmount\", ")
		.append("        (nvl(trunc(btrpi.repayment_interest * btii.invest_amount / ")
		.append("            btpii.already_invest_amount, 2), ")
		.append("            0) + ")
		.append("   (case when btrpi.repayment_interest is null then 0 else")
		.append("        nvl(trunc(btii.invest_amount * btpi.award_rate * btpi.type_term / 12, 2) , 0) end) + ")
		.append("        nvl(trunc(btrpi.repayment_principal * btii.invest_amount / ")
		.append("            btpii.already_invest_amount, ")
		.append("            2), 0)) \"totalAmount\",       ")
		.append("        case when btpi.repaymnet_method = '"+Constant.REPAYMENT_METHOD_01+"' then btpi.type_term ")
		.append("           when btpi.repaymnet_method = '"+Constant.REPAYMENT_METHOD_03+"' then 1  ")
		.append("              when btpi.repaymnet_method = '"+Constant.REPAYMENT_METHOD_05+"' then btpi.type_term  ")
		.append("                 when btpi.repaymnet_method = '"+Constant.REPAYMENT_METHOD_04+"' then btpi.type_term / 3  ")
		.append("                   end \"typeTerm\",                ")
		.append("        case when btpi.repaymnet_method = '"+Constant.REPAYMENT_METHOD_01+"' then  '一个月' ")
		.append("           when btpi.repaymnet_method = '"+Constant.REPAYMENT_METHOD_03+"' then btpi.type_term || '个月'  ")
		.append("              when btpi.repaymnet_method = '"+Constant.REPAYMENT_METHOD_05+"' then '一个月'  ")
		.append("                 when btpi.repaymnet_method = '"+Constant.REPAYMENT_METHOD_04+"' then '三个月' ")
		.append("                   end \"cycle\"  ")
		.append("   from bao_t_invest_info btii, ")
		.append("        bao_t_project_info btpi, ")
		.append("        bao_t_project_invest_info btpii, ")
		.append("        bao_t_cust_info btci, ")
		.append("        (select sum(a.repayment_interest) as repayment_interest, ")
		.append("                sum(a.repayment_principal) as repayment_principal, ")
		.append("                a.project_id as project_id ")
		.append("           from bao_t_repayment_plan_info a ")
		.append("          group by a.project_id) btrpi ")
		.append("  where btii.project_id = btpi.id ")
		.append("    and btii.project_id = btpii.project_id ")
		.append("    and btii.project_id = btrpi.project_id (+) ")
		.append("    and btii.cust_id = btci.id    ");
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, params);
		sqlCondition.addString("projectId", "btii.project_id")
					.addString("custId", "btii.cust_id");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				sqlCondition.toString(), sqlCondition.toArray());
		
		if(list == null || list.size() == 0){
			return new ResultVo(false, "没有查到合同信息");
		} 
		
		return new ResultVo(true, "查询合同信息成功", list.get(0));	
	}

	@Override
	public List<Map<String, Object>> queryMyProjectDetail(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" select bpro.id \"projectId\", ")
		.append("        bpro.project_name \"projectName\", ")
		.append("        bpro.type_term \"typeTerm\", ")
		.append("        bpro.year_rate \"yearRate\", ")
		.append("        bpro.award_rate \"awardRate\", ")
		.append("        bpro.repaymnet_method \"incomeType\", ")
		.append("        bpro.release_date \"releaseDate\", ")
		.append("        bpro.effect_date \"effectDate\", ")
		.append("        bpro.project_end_date \"endDate\", ")
		.append("        bpro.project_status \"projectStatus\", ")
		.append("        binv.invest_amount \"investAmount\", ")
		.append("        case ")
		.append("          when bpro.project_status in ('还款中', '已逾期') then ")
		.append("           trunc(nvl(btrpi.repayment_interest * binv.invest_amount / ")
		.append("                     bpinv.already_invest_amount, ")
		.append("                     0), ")
		.append("                 2) + (case ")
		.append("                         when btrpi.project_id is null then ")
		.append("                          0 ")
		.append("                         else ")
		.append("                          trunc(nvl(binv.invest_amount * bpro.award_rate * bpro.type_term / 12,  ")
		.append("                                    0), ")
		.append("                                2) ")
		.append("                       end) ")
		.append("          else ")
		.append("           0 ")
		.append("        end \"exceptAmount\", ")
		.append("        nvl(bpay.trade_amount, 0) \"receviedAmount\", ")
		.append("        bpro.project_status \"investStatus\", ")
		.append("       0  \"accountAmount\", ")
		.append("        case ")
		.append("          when bpro.project_status in ('"+ Constant.PROJECT_STATUS_09 +"', '"+ Constant.PROJECT_STATUS_11 +"', '"+ Constant.PROJECT_STATUS_12 +"' ) then ")
		.append("           0 ")
		.append("          when bpro.project_status in ('"+ Constant.PROJECT_STATUS_06 +"', '"+ Constant.PROJECT_STATUS_07 +"') then trunc(bpro.project_end_date) - trunc(bpro.effect_date) ")
		.append("          else ")
		.append("           trunc(bpro.project_end_date) - trunc(sysdate) ")
		.append("        end \"reminderDay\" ")
		.append("   from bao_t_project_info bpro, ")
		.append("        bao_t_invest_info binv, ")
		.append("        bao_t_project_invest_info bpinv, ")
		.append("        (select sum(a.repayment_interest) as repayment_interest, ")
		.append("                a.project_id as project_id ")
		.append("           from bao_t_repayment_plan_info a ")
		.append("          where a.repayment_status = '未还款' ")
		.append("            and a.is_riskamount_repay = '否' ")
		.append("          group by a.project_id) btrpi, ")
		.append("         ")
		.append("        (select a.project_id project_id, sum(b.trade_amount) trade_amount, a.cust_id cust_id ")
		.append("           from bao_t_payment_record_info a, bao_t_payment_record_detail b ")
		.append("          where a.id = b.pay_record_id ")
		.append("            and b.subject_type not in ('本金', '风险金垫付本金') ")
		.append("          group by a.project_id, a.cust_id) bpay ")
		.append("  where binv.project_id = bpro.id ")
		.append("    and binv.project_id = bpinv.project_id ")
		.append("    and binv.project_id = btrpi.project_id(+) ")
		.append("    and binv.project_id = bpay.project_id(+) ")
		.append("    and binv.cust_id = bpay.cust_id(+) ");
		
		SqlCondition sqlCondition = new SqlCondition(sql, params);
		sqlCondition.addString("projectId", "bpro.id")
					.addString("custId", "binv.cust_id");
		
		return repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray()); 
	}

	@Override
	public Map<String, Object> queryProjectTotalIncome(
			Map<String, Object> params) {
		Map<String, Object> resultMap = Maps.newHashMap();
		StringBuilder sql = new StringBuilder()
		.append(" select count(binv.project_id) \"totalCounts\", ")
		.append("        nvl(sum(trunc(binv.invest_amount, 2)), 0) \"totalTradeAmount\", ")
		.append("        nvl(sum(trunc(bpay.trade_amount, 2)), 0) \"totalIncomeAmount\", ")
		.append("        0 \"totalPrevTradeAmount\" ")
		.append("   from bao_t_invest_info binv, ")
		.append("        bao_t_project_info bpro, ")
		.append("        (select sum(trunc(nvl(b.trade_amount, 0), 2)) trade_amount, ")
		.append("                a.project_id project_id, ")
		.append("                a.cust_id cust_id ")
		.append("           from bao_t_payment_record_info a, bao_t_payment_record_detail b ")
		.append("          where a.id = b.pay_record_id ")
		.append("            and b.subject_type not in ('本金', '风险金垫付本金') ")
		.append("          group by a.project_id, a.cust_id) bpay ")
		.append("  where binv.project_id = bpro.id ")
		.append("    and binv.cust_id = bpay.cust_id(+) ")
		.append("    and binv.project_id = bpay.project_id(+) ");
		
		SqlCondition sqlCondition = new SqlCondition(sql, params);
		sqlCondition.addString("custId", "binv.cust_id")
					.addList("projectStatus", "bpro.project_status");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
		if(list != null && list.size() > 0){
			resultMap = list.get(0);
		} else {
			resultMap.put("totalCounts", 0);
			resultMap.put("totalTradeAmount", BigDecimal.ZERO);
			resultMap.put("totalIncomeAmount", BigDecimal.ZERO);
			resultMap.put("totalPrevTradeAmount", BigDecimal.ZERO);
		}
		
		return resultMap;
	}

}
