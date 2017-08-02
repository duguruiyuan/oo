package com.slfinance.shanlincaifu.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.LoanReserveEntity;

public interface LoanReserveRepository extends PagingAndSortingRepository<LoanReserveEntity, String> {
	
	/**
	 * 通过状态查询所有一键出借数据
	 *
	 * @author  wangjf
	 * @date    2017年7月22日 下午12:50:08
	 * @param reserveStatus
	 * @return
	 */
	List<LoanReserveEntity> findByReserveStatus(String reserveStatus);
	
	/**
	 * 统计某个客户某天一键出借情况
	 *
	 * @author  wangjf
	 * @date    2017年7月22日 下午12:50:38
	 * @param custId
	 * @param createDate
	 * @return
	 */
	@Query("select count(id) from LoanReserveEntity A where A.custId = :custId and A.createDate >= :createDate and A.createDate < :createDate + 1")
	int countByCustIdAndCreateDate(@Param("custId")String custId, @Param("createDate")Date createDate);
	
	/**
	 * 查询目前所有存在的组合
	 *
	 * @author  wangjf
	 * @date    2017年7月22日 下午5:25:01
	 * @return
	 */
	@Query("select distinct new LoanReserveEntity(A.loanTerm, A.loanUnit, A.transferType, A.repaymentType) from LoanReserveEntity A")
	List<LoanReserveEntity> findAllGroup();

	
	/**
	 * 通过loanId查询一键出借情况
	 * 
	 * @param loanId
	 * @return
	 */
	@Query("select A from LoanReserveEntity A where A.id in (select B.loanReserveId from LoanReserveRelationEntity B where B.loanId = :loanId ) ")
	List<LoanReserveEntity> findByLoanId(@Param("loanId")String loanId);

	/**预约金额查询
	 * @author zhangyb
	 * @return
	 */
	@Query(value="select sum(t.remainder_amount) from slcf.bao_t_loan_reserve t where t.reserve_status = '排队中'", nativeQuery = true)
	BigDecimal getTotalAmount();
}
