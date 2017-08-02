package com.slfinance.shanlincaifu.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.LoanDetailInfoEntity;


/**   
 * BAO借款信息详情表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-12-01 15:06:43 $ 
 */
public interface LoanDetailInfoRepository extends PagingAndSortingRepository<LoanDetailInfoEntity, String>{

	@Query(value=" select * from BAO_T_LOAN_DETAIL_INFO where loan_id = ?1 ", nativeQuery=true)
	LoanDetailInfoEntity findByLoanId(String loanId);
}
