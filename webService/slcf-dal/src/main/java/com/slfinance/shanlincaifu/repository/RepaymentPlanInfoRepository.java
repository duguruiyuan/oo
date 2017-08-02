/** 
 * @(#)RepaymentPlanInfoRepository.java 1.0.0 2015年5月1日 下午4:21:52  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;

/**   
 * 还款计划数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月1日 下午4:21:52 $ 
 */
public interface RepaymentPlanInfoRepository extends PagingAndSortingRepository<RepaymentPlanInfoEntity, String>{

	/**
	 * 取指定日期的还款计划
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午4:23:19
	 * @param expectRepaymentDate
	 * @return
	 */
	@Query("select A from RepaymentPlanInfoEntity A JOIN FETCH A.loanEntity JOIN A.loanEntity.loanDetailInfoEntity where A.expectRepaymentDate = ?1 and A.repaymentStatus = ?2 and A.loanEntity.loanDetailInfoEntity.creditRightStatus='正常' and A.loanEntity.loanStatus is null ")
	public List<RepaymentPlanInfoEntity> findByExpectRepaymentDate(String expectRepaymentDate, String repaymentStatus);
	
	/**
	 * 根据项目Id查询
	 * @author zhangt
	 * @param projectId
	 * @return
	 */
	public List<RepaymentPlanInfoEntity> findByprojectId(String projectId);
	
	/**
	 * 根据还款日期查询两个日期之间的应还金额
	 * @author zhiwen_feng
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Query("select NVL(sum(repaymentTotalAmount - nvl(termAlreadyRepayAmount, 0)), 0) from RepaymentPlanInfoEntity where expectRepaymentDate >= ?1 and expectRepaymentDate <= ?2")
	public BigDecimal sumRepaymentTotalAmountByExpectRepaymentDate(String beginDate, String endDate);
	
	/**
	 * 通过借款ID
	 *
	 * @author  wangjf
	 * @date    2016年12月1日 下午2:53:37
	 * @param loanId
	 * @return
	 */
	@Query(value="select NVL(trunc(sum(REPAYMENT_INTEREST + "+
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
	public BigDecimal sumRepaymentInterestByLoanId(String loanId);
	
	/**
	 * 通过借款ID
	 *
	 * @author  guoyk
	 * @date    2017年7月11日 下午2:53:37
	 * @param loanId
	 * @return
	 */
	@Query(value="select NVL(trunc(sum(REPAYMENT_INTEREST),2),0)from BAO_T_REPAYMENT_PLAN_INFO cc,bao_t_loan_info l where cc.loan_id = l.id and l.id = ?", nativeQuery=true)
	public BigDecimal sumRepaymentInterestByLoanIdNotAwardRate(String loanId);
	
	/**
	 * 通过借款ID
	 *
	 * @author guoyk
	 * @date    2017年7月2日 下午2:53:37
	 * @param loanId
	 * @return
	 */
	@Query(value="select NVL(sum(REPAYMENT_INTEREST+REPAYMENT_PRINCIPAL), 0) from BAO_T_REPAYMENT_PLAN_INFO where loan_id = ?", nativeQuery=true)
	public BigDecimal sumRepaymentInterestAndRepaymentPrincipalByLoanId(String loanId);
	
	/**
	 * 根据项目Id查询
	 * @author zhangt
	 * @param loanId
	 * @return
	 */
	@Query("select A from RepaymentPlanInfoEntity A JOIN FETCH A.loanEntity where A.loanEntity.id = :loanId order by A.currentTerm")
	public List<RepaymentPlanInfoEntity> findByLoanId(@Param("loanId")String loanId);
	
	/**
	 * 查询待还款还款计划
	 *
	 * @author  wangjf
	 * @date    2016年12月2日 下午5:34:49
	 * @param expectRepaymentDate
	 * @param loanStatus
	 * @return
	 */
	@Query("select A from RepaymentPlanInfoEntity A JOIN FETCH A.loanEntity where A.expectRepaymentDate <= :expectRepaymentDate and A.repaymentStatus = :repaymentStatus and A.loanEntity.loanStatus = :loanStatus and A.loanEntity.publishDate is not null order by A.expectRepaymentDate")
	public List<RepaymentPlanInfoEntity> findWaitingRepaymentPlan(@Param("expectRepaymentDate")String expectRepaymentDate, @Param("repaymentStatus")String repaymentStatus, @Param("loanStatus")String loanStatus);

	@Modifying
	@Query(value = " delete from BAO_T_REPAYMENT_PLAN_INFO rp where rp.LOAN_ID = ?1 ", nativeQuery=true)
	public void deleteByLoanId(String loanId);
	
	/**
	 * 查询总利息
	 *
	 * @author  wangjf
	 * @date    2016年12月28日 下午1:23:15
	 * @param loanId
	 * @param repaymentStatus
	 * @return
	 */
	@Query(value="select NVL(sum(REPAYMENT_INTEREST), 0) from BAO_T_REPAYMENT_PLAN_INFO where loan_id = ? and repayment_status = ?", nativeQuery=true)
	public BigDecimal sumRepaymentInterestByLoanIdAndRepaymentStatus(String loanId, String repaymentStatus);
	
}
