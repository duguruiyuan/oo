/** 
 * @(#)RepaymentPlanInfoRepository.java 1.0.0 2015年5月1日 下午4:21:52  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.RepaymentPlanCopeEntity;
import org.springframework.data.repository.query.Param;

/**   
 * 还款计划数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月1日 下午4:21:52 $ 
 */
public interface RepaymentPlanCopeRepository extends PagingAndSortingRepository<RepaymentPlanCopeEntity, String>{
	/*
	 * 根据loancode查询还款计划副本
	 */
	@Query("select A from RepaymentPlanCopeEntity A where A.loanCode = ? order by A.expectRepaymentDate")
	List<RepaymentPlanCopeEntity> findByLoanCode(String loanCode);
	/*
	 * 根据loanId查询还款计划副本列表
	 */
	@Query("select A from RepaymentPlanCopeEntity A where A.loanEntity.id = ?1")
	List<RepaymentPlanCopeEntity> queryByLoanId(String loanId);
	
	/**
	 * 取指定日期的还款计划
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午4:23:19
	 * @param expectRepaymentDate
	 * @return
	 */
	
	/*@Query(value="select NVL(trunc(sum(REPAYMENT_INTEREST + "+
			  "  case when l.loan_unit = '天' "+
		     "  then l.loan_amount * l.loan_term/360 * nvl(l.award_rate,0) "+
		      " else "+
		      "  (cc.repayment_principal + cc.remainder_principal) * nvl(l.award_rate,0) "+
		      "   *(case when l.repayment_method = '到期还本付息' then l.loan_term "+
		       "         when l.repayment_method = '等额本息' then 1 "+
		       "         when l.repayment_method = '每期还息到期付本' then 1 "+
		        "        else 0 end) / 12"+
		     " end ),2), 0)"+
			"from BAO_T_REPAYMENT_PLAN_INFO cc,bao_t_loan_info l where cc.loan_id = l.id and l.id = ?", nativeQuery=true)
	public BigDecimal sumRepaymentInterestByLoanId(String loanId);*/

	/***
	 * 根据借款编号和期数 查询计划副本信息
	 * @param loanCode
	 * @param currentTerm
	 * @return
	 */
	public RepaymentPlanCopeEntity findByLoanCodeAndCurrentTerm(@Param("loanCode") String loanCode,@Param("currentTerm") Integer currentTerm);
	/****
	 * 根据还款编号和还款时间查询还款计划副本
	 * @param loanCode
	 * @param expectRepaymentDate
	 * @return
	 */
	public RepaymentPlanCopeEntity findByLoanCodeAndExpectRepaymentDate(@Param("loanCode") String loanCode,@Param("expectRepaymentDate") String expectRepaymentDate);
}
