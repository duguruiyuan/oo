package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.RepaymentPlanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.SqlCondition;
import com.slfinance.vo.ResultVo;

@Repository
public class RepaymentPlanInfoRepositoryImpl implements
		RepaymentPlanInfoRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Override
	public ResultVo findRepaymentPlanInfoList(Map<String, Object> params) {
		Map<String, Object> resultMap = Maps.newHashMap();
		double rate = (double) params.get("rate");
		//项目已投资金额
		StringBuilder sql = new StringBuilder()
		.append("  select btrpi.current_term              \"currentTerm\", ")
		.append("         btrpi.expect_repayment_date     \"expectRepaymentDate\", ")
		.append("         (btrpi.repayment_interest + btrpi.repayment_principal) * " + rate + "   \"repaymentTotalAmount\", ")
		.append("         btrpi.repayment_total_amount    \"totalRepaymentAmount\", ")
		.append("         btrpi.term_already_repay_amount \"termAlreadyRepayAmount\", ")
		.append("         btrpi.repayment_interest * " + rate + "      \"repaymentInterest\", ")
		.append("         btrpi.repayment_principal * " + rate + "     \"repaymentPrincipal\", ")
		.append("         btrpi.remainder_principal       \"remainderPrincipal\", ")
		.append("         btrpi.repayment_status          \"repaymentStatus\", ")
		.append("         btrpi.account_manage_expense    \"accountManageExpense\", ")
		.append("         (btrpi.repayment_total_amount - btrpi.account_manage_expense)  \"deservedAmount\", ")
		.append("         case when btrpi.current_term = b.current_term then b.repayment_principal * " + rate + " * c.award_rate / 12 * c.type_term else 0 end \"awardIncome\" ")
		.append("    from bao_t_repayment_plan_info btrpi, ")
		.append("         (select a.project_id as project_id, max(a.current_term) as current_term, sum(a.repayment_principal) as repayment_principal ")
		.append("              from bao_t_repayment_plan_info a  group by a.project_id) b,")
		.append("         bao_t_project_info c ")
		.append("       where btrpi.project_id = b.project_id and btrpi.project_id = c.id %s ")
		.append("   order by btrpi.current_term ");
		
		
		StringBuilder summaSql = new StringBuilder()
		.append("select count(1) \"totalRepayCount\", ")
		.append("sum(case when btrpi.repayment_status = '已还款' then 1 else 0 end) \"alreadyRepayCount\",")
		.append(" sum(case when btrpi.repayment_status = '未还款'  then btrpi.repayment_principal else 0 end ) \"totalRemainderPrincipal\",")
		.append("    sum(btrpi.repayment_total_amount) \"alreadyRepayAmount\",")
		.append("    sum(btrpi.repayment_principal) \"alreadyRepayPrincipal\",")
		.append("    sum(btrpi.repayment_interest) \"alreadyRepayInterest\",")
		.append("    sum(btrpi.account_manage_expense) \"totalManageExpense\" ")
		.append("from bao_t_repayment_plan_info btrpi ")
		.append(" where 1 = 1 %s");
		
		StringBuilder sqlString = new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(sqlString, params);
		sqlCondition.addString("projectId", "btrpi.project_id");
		
		Page<Map<String, Object>> page = repositoryUtil.queryForPageMap(
				String.format(sql.toString(), sqlCondition.toString()),
				sqlCondition.toArray(), 
				Integer.parseInt(params.get("start").toString()), 
				Integer.parseInt(params.get("length").toString()));
		
		List<Map<String, Object>> summaList = repositoryUtil.queryForMap(String.format(summaSql.toString(), sqlCondition.toString()), sqlCondition.toArray());
		Map<String, Object> summaMap = Maps.newHashMap();
		if(summaList != null && summaList.size() > 0){
			summaMap = summaList.get(0);
		}
		resultMap.put("data", page.getContent());
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("summaMap", summaMap);
		
		return new ResultVo(true, "还款列表查询成功", resultMap);
	}

	@Override
	public BigDecimal findAwardRateInfoForNewerFlag(String investId) {
		// TODO Auto-generated method stub
		StringBuilder sql = new StringBuilder()
//		.append(" select  ")
//		.append(" trunc(case when l.newer_flag = '新手标' ")
//		.append("            then ")
//		.append("                 case when l.loan_unit = '天'  ")
//		.append("                      then i.invest_amount * l.loan_term/360 * l.award_rate  ")
//		.append("                      else (select sum((cc.repayment_principal + cc.remainder_principal)*h.hold_scale* ")
//		.append("                       (case when l.repayment_method = '到期还本付息' then l.loan_term when l.repayment_method = '等额本息'then 1 when l.repayment_method = '每期还息到期付本' then 1 else 0 end)    ")
//		.append("                       * l.award_rate /12 )from bao_t_repayment_plan_info cc where cc.loan_id = l.id and cc.repayment_status = '未还款' )  ")
//		.append("                      end  ")
//		.append("            else 0 END ,2) \"awardRateAmount\"  ")
//		.append("  from bao_t_loan_info l , ")
//		.append("  bao_t_invest_info i , ")
//		.append("  bao_t_wealth_hold_info h ")
//		.append("  where i.loan_id = l.id ")
//		.append("  and h.invest_id = i.id  ")
//		.append("  and i.id = ? ");
//		.append(" SELECT  trunc(sum(case when l.repayment_method = '等额本息'  ")
//		.append("              then cc.REPAYMENT_INTEREST * l.award_rate/d.year_irr*h.hold_scale         ")
//		.append("              else  (cc.repayment_principal + cc.remainder_principal)*l.award_rate *decode(l.loan_unit,'天',1,'月',30)*l.loan_term/360*h.hold_scale                       ")
//		.append("              end ) ,2) \"awardRateAmount\" ")
//		.append("               from bao_t_loan_info l ,  ")
//		.append("                   bao_t_invest_info i ,   ")
//		.append("                   bao_t_wealth_hold_info h , ")
//		.append("                   bao_t_loan_detail_info d , ")
//		.append("                   bao_t_repayment_plan_info cc ")
//		.append("                   where i.loan_id = l.id  ")
//		.append("                   and h.invest_id = i.id  ")
//		.append("                   and d.loan_id = l.id ")
//		.append("                   and cc.loan_id = l.id  ")
//		.append("                   and i.id = ? ");
		.append("   select \"awardRateAmount\" from ( SELECT  case when l.repayment_method = '等额本息'   ")
		.append("                                                       then cc.REPAYMENT_INTEREST * l.award_rate/d.year_irr*h.hold_scale  ")
		.append("                                                       when l.repayment_method = '每期还息到期付本' ")
		.append("                                                       then  (cc.repayment_principal + cc.remainder_principal)*l.award_rate *30/360*h.hold_scale       ")
		.append("           else  (cc.repayment_principal + cc.remainder_principal)*l.award_rate *decode(l.loan_unit,'天',1,'月',30)*l.loan_term/360*h.hold_scale                        ")
		.append("           end  \"awardRateAmount\" ,cc.current_term ")
		.append("            from bao_t_loan_info l ,   ")
		.append("                bao_t_invest_info i ,    ")
		.append("                bao_t_wealth_hold_info h ,  ")
		.append("                bao_t_loan_detail_info d ,  ")
		.append("                bao_t_repayment_plan_info cc  ")
		.append("                where i.loan_id = l.id   ")
		.append("                and h.invest_id = i.id   ")
		.append("                and d.loan_id = l.id  ")
		.append("                and cc.loan_id = l.id   ")
		.append("                and i.id = ?  ")
		.append("                and cc.repayment_status = '未还款' ")
		.append("                order by cc.current_term asc   ) ")
		.append("                where rownum=1  ");
		BigDecimal awardRateAmount = BigDecimal.ZERO;
		List<Map<String, Object>> resultList = repositoryUtil.queryForMap(sql.toString(), new Object[]{investId});
		if (resultList!=null && resultList.size()!=0) {
			Map<String, Object> map = resultList.get(0);
		    awardRateAmount = CommonUtils.emptyToDecimal(map.get("awardRateAmount"));
		}
		return awardRateAmount;
	}

}
