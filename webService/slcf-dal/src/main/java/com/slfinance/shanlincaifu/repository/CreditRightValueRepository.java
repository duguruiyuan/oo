package com.slfinance.shanlincaifu.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.CreditRightValueEntity;

public interface CreditRightValueRepository extends
		PagingAndSortingRepository<CreditRightValueEntity, String> {

	/**
	 * 通过借款ID和时间查询债权价值
	 *
	 * @author  wangjf
	 * @date    2016年12月27日 下午2:35:07
	 * @param loanId
	 * @param valueDate
	 * @return
	 */
	@Query(value = "select decode(nvl((select r.repayment_status "
			+ "					from bao_t_repayment_plan_info r "
			+ "					where r.loan_id = :loanId and r.expect_repayment_date = :valueDate), '未还款'), '未还款', c.value_repayment_before, c.value_repayment_after) "
			+ " from bao_t_credit_right_value c "
			+ " where c.loan_id = :loanId and c.value_date= :valueDate ", nativeQuery = true)
	BigDecimal findByLoanIdAndValueDate(@Param("loanId")String loanId, @Param("valueDate")String valueDate);

}
