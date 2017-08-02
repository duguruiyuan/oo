package com.slfinance.shanlincaifu.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.AccreditRequestEntity;
import com.slfinance.shanlincaifu.entity.BannerInfoEntity;

/**
 * 授权申请
 * @author liao1018
 *
 */
public interface AccreditRequestRepository extends PagingAndSortingRepository<AccreditRequestEntity, String>{

	public List<AccreditRequestEntity> findByStatus(String status);

}
