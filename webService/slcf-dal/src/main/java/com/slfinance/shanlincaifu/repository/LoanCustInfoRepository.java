package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.LoanCustInfoEntity;

public interface LoanCustInfoRepository extends PagingAndSortingRepository<LoanCustInfoEntity, String> {

	public LoanCustInfoEntity findByCustNameAndCredentialsCode(String custName, String credentialsCode);

	public LoanCustInfoEntity findById(String custId);
}
