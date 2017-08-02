package com.slfinance.shanlincaifu.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.PlatformTradeDataEntity;

public interface PlatformTradeDataRepository extends PagingAndSortingRepository<PlatformTradeDataEntity, String>, JpaSpecificationExecutor<PlatformTradeDataEntity> {
	
	@Query("select A from PlatformTradeDataEntity A order by A.createDate desc")
	List<PlatformTradeDataEntity> findLastOne();
	
	@Query("select A from PlatformTradeDataEntity A where A.tradeAmountMonth is not null order by A.createDate desc")
	List<PlatformTradeDataEntity> findLastMonth();

	

}
