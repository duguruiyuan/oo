package com.slfinance.shanlincaifu.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.LoanReserveRelationEntity;

public interface LoanReserveRelationRepository extends PagingAndSortingRepository<LoanReserveRelationEntity, String> {
	/**
	 * 通过预约id和交易状态查询所有一键出借预约关系表
	 * 
	 * @author fengl
	 * @param loanReserveId
	 * @param tradeStatus
	 * @return
	 */
	List<LoanReserveRelationEntity> findByLoanReserveIdAndTradeStatus(String loanReserveId, String tradeStatus);

	/**
	 * 通过借款id查询所有一键出借预约关系表
	 * 
	 * @author fengl
	 * @param loanReserveId
	 * @param tradeStatus
	 * @return
	 */
	List<LoanReserveRelationEntity> findByLoanId(String loanId);
}
