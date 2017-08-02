package com.slfinance.shanlincaifu.repository.custom.impl;/**
 * <描述：<b>TODO</b>
 *
 * @author:张祥
 * @date 2017/7/13
 */


import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.WithHoldingRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 代扣项目定义数据访问层
 * @author 张祥
 * @create 2017-07-13 10:11
 **/
@Repository
public class WithHoldingRepositoryCustomImpl implements WithHoldingRepositoryCustom{

    @Autowired
    RepositoryUtil repositoryUtil;

    @Override
    public List<Map<String, Object>> doFindWatingRepayPlanCope(String repayMentDate) {

        StringBuilder sb = new StringBuilder();
        sb.append("select  p.loan_id  \"loanId\",p.loan_code \"repaymentNo\",p.current_term \"repaymentTerm\",");
        sb.append("p.expect_repayment_date \"scheduleRepaymentDate \",p.repayment_total_amount \"moneyOrder\" ,");
        sb.append("a.no_agree \"noAgree\" ,a.cust_id \"custId\" from bao_t_repayment_plan_cope p ");
        sb.append("left join bao_t_loan_info l on p.loan_id = l.id ");
        sb.append("left join bao_t_loan_agree a on l.relate_primary = a.cust_id ");
        sb.append("where l.loan_status =? and l.grant_status = ? and p.expect_repayment_date = ? ");
        sb.append("and p.repayment_status = ? and p.loan_code not in ");
        sb.append(" (select f.REPAYMENT_NO from BAO_T_WITH_HOLDING_FLOW f where f.REPAYMENT_NO = p.loan_code group by f.REPAYMENT_NO having count(f.REPAYMENT_NO)>= 1)  ");

        return repositoryUtil.queryForMap(sb.toString(),new Object[]{Constant.LOAN_STATUS_08,Constant.GRANT_STATUS_02,
                repayMentDate, Constant.REPAYMENT_STATUS_WAIT});

//        StringBuilder sb = new StringBuilder();
//        sb.append("select  p.loan_id  \"loanId\",p.loan_code \"repaymentNo\",p.current_term \"repaymentTerm\",");
//        sb.append("p.expect_repayment_date \"scheduleRepaymentDate\",p.repayment_total_amount \"moneyOrder\" ,");
//        sb.append("a.no_agree \"noAgree\" ,a.cust_id \"custId\" from bao_t_repayment_plan_cope p ");
//        sb.append("left join bao_t_loan_info l on p.loan_id = l.id ");
//        sb.append("left join bao_t_loan_agree a on l.relate_primary = a.cust_id ");
//        sb.append("where p.expect_repayment_date = ? ");
//        sb.append("and p.repayment_status = ? and p.loan_code not in ");
//        sb.append(" (select f.REPAYMENT_NO from BAO_T_WITH_HOLDING_FLOW f where f.REPAYMENT_NO = p.loan_code group by f.REPAYMENT_NO having count(f.REPAYMENT_NO)>= 1)  ");
//
//        return repositoryUtil.queryForMap(sb.toString(),new Object[]{repayMentDate, Constant.REPAYMENT_STATUS_WAIT});
    }

    @Override
    public List<Map<String, Object>> doFindWatingRepayPlanCopeByLoanInfo(String loanNo, String currentTerm) {

        StringBuilder sb = new StringBuilder();
        sb.append("select  p.loan_id  \"loanId\",p.loan_code \"repaymentNo\",p.current_term \"repaymentTerm\",");
        sb.append("p.expect_repayment_date \"scheduleRepaymentDate \",p.repayment_total_amount \"moneyOrder\" ,");
        sb.append("a.no_agree \"noAgree\" ,a.cust_id \"custId\" ,p.REPAYMENT_STATUS \"repaymentStatus\" ");
        sb.append("from bao_t_repayment_plan_cope p  left join bao_t_loan_info l on p.loan_id = l.id ");
        sb.append("left join bao_t_loan_agree a on l.relate_primary = a.cust_id ");
        sb.append(" where p.loan_code = ? and p.current_term = ? and l.loan_status =? and l.grant_status = ?");
        return repositoryUtil.queryForMap(sb.toString(),new Object[]{loanNo,currentTerm,Constant.LOAN_STATUS_08,Constant.GRANT_STATUS_02});

//        StringBuilder sb = new StringBuilder();
//        sb.append("select  p.loan_id  \"loanId\",p.loan_code \"repaymentNo\",p.current_term \"repaymentTerm\",");
//        sb.append("p.expect_repayment_date \"scheduleRepaymentDate\",p.repayment_total_amount \"moneyOrder\" ,");
//        sb.append("a.no_agree \"noAgree\" ,a.cust_id \"custId\" ,p.REPAYMENT_STATUS \"repaymentStatus\" ");
//        sb.append("from bao_t_repayment_plan_cope p  left join bao_t_loan_info l on p.loan_id = l.id ");
//        sb.append("left join bao_t_loan_agree a on l.relate_primary = a.cust_id ");
//        sb.append(" where p.loan_code = ? and p.current_term = ? ");
//        return repositoryUtil.queryForMap(sb.toString(),new Object[]{loanNo,currentTerm});
    }
}
