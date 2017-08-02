package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.EmployeeLoadInfoEntity;

/**
 * 花名册原始数据表数据访问层
 * 
 * @author zhiwen_feng
 *
 */
public interface EmployeeLoadInfoRepository extends PagingAndSortingRepository<EmployeeLoadInfoEntity, String>{

}
