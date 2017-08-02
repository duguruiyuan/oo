package com.slfinance.shanlincaifu.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.ActivityAwardEntity;

public interface ActivityAwardRepository extends PagingAndSortingRepository<ActivityAwardEntity, String>, JpaSpecificationExecutor<ActivityAwardEntity>{

	
}
