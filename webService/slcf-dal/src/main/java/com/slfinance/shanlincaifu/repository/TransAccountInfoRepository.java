package com.slfinance.shanlincaifu.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.TransAccountInfoEntity;

public interface TransAccountInfoRepository extends PagingAndSortingRepository<TransAccountInfoEntity, String>, JpaSpecificationExecutor<TransAccountInfoEntity>{

}
