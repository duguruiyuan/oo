/** 
 * @(#)FinancialStatisticsCustomImpl.java 1.0.0 2015年8月11日 上午11:26:06  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.FinancialStatisticsCustom;
import com.slfinance.shanlincaifu.utils.DateUtils;

/**   
 * 财务统计接口实现
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年8月11日 上午11:26:06 $ 
 */
@Repository
public class FinancialStatisticsCustomImpl implements FinancialStatisticsCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	/**还款统计返回字段值**/
	final String repaymentColumn = " a.loan_code \"loanCode\",a.loan_amount \"loanAmount\",a.hold_amount \"holdAmount\",trunc(a.hold_scale,4) \"holdScale\",a.loan_term \"loanTerm\",to_char(a.invest_start_date,'yyyyMMdd') \"investStartDate\",to_char(a.invest_end_date,'yyyyMMdd') \"investEndDate\",b.parameter_name \"parameterName\",c.current_term \"currentTerm\", c.expect_repayment_date  \"expectRepaymentDate\",c.repayment_total_amount \"repaymentTotalAmount\",a.loan_desc \"loanDesc\" ,a.repayment_method \"repaymentMethod\" ";
	
	/**还款统计表关联**/
	final String repaymennTable = " from bao_t_loan_info a,(select * from com_t_param where type = 'baoLoanSource') b,bao_t_repayment_plan_info c ";

	/**还款统计条件**/
	final String repaymennWhere = " where a.debt_source_code = b.value and a.id = c.loan_id and c.expect_repayment_date = ? order by a.loan_code ";
//	final String repaymennWhere = " where a.debt_source_code = b.value and a.id = c.loan_id order by a.id, c.expect_repayment_date ";
	
	/**
	 * 当天未还款数据汇总
	 */
	@Override
	public List<Map<String, Object>> getRepaymentList() throws SLException {
		StringBuilder sql = new StringBuilder(" select ").append(repaymentColumn).append(repaymennTable).append(repaymennWhere);
		return repositoryUtil.queryForMap(sql.toString(), new Object[]{DateUtils.getCurrentDate("yyyyMMdd")});
	}

}
