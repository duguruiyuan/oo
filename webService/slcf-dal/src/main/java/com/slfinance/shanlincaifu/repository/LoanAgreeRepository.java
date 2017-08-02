package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.AccreditRequestEntity;
import com.slfinance.shanlincaifu.entity.BannerInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanAgreeEntity;

/**
 * 授权申请
 * @author liao1018
 *
 */
public interface LoanAgreeRepository extends PagingAndSortingRepository<LoanAgreeEntity, String>{

	LoanAgreeEntity findByCustId(String custId);

}
