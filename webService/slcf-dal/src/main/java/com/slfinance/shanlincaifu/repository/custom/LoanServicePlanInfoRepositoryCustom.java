package com.slfinance.shanlincaifu.repository.custom;

import java.util.Map;

import org.springframework.data.domain.Page;

public interface LoanServicePlanInfoRepositoryCustom {

	/**
	 * 服务费回款列表查询
	 * @author liyy
	 * @date 2017-2-24
	 * @param
     *      <tt>loanServicePlanId:String:借款服务费计划ID</tt><br>
     *      <tt>loanId           :String:借款ID</tt><br>
     *      <tt>companyName      :String:商户名称</tt><br>
     *      <tt>loanTitle        :String:借款标题</tt><br>
     *      <tt>manageRate       :String:服务费率</tt><br>
     *      <tt>exceptAmount     :String:应收服务费</tt><br>
     *      <tt>exceptDate       :String:应收日期</tt><br>
     *      <tt>payStatus        :String:收款状态</tt><br>
	 */
	Page<Map<String, Object>> queryLoanExpenseList(Map<String, Object> params);
}
