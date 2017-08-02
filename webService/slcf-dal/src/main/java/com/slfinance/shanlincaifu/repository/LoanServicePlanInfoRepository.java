package com.slfinance.shanlincaifu.repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.LoanServicePlanInfoEntity;


/**   
 * 借款服务费计划表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2017-02-24 11:14:03 $ 
 */
public interface LoanServicePlanInfoRepository extends PagingAndSortingRepository<LoanServicePlanInfoEntity, String>{
	/**
	 * 通过借款ID删除借款服务费计划表
	 * @author  fengyl
	 * @date    2017年2月24日
	 * @param loanId
	 */
	@Modifying
	@Query(value = " delete from BAO_T_LOAN_SERVICE_PLAN_INFO rp where rp.LOAN_ID = ?1 ", nativeQuery=true)
	public void deleteByLoanId(String loanId);

	/**
	 * 通过借款ID查询
	 *
	 * @author  wangjf
	 * @date    2017年2月24日 下午2:08:00
	 * @param loanId
	 * @return
	 */
	List<LoanServicePlanInfoEntity> findByLoanId(String loanId);
}
