package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.EmployeeInfoEntity;

/**
 * 员工表数据访问层
 * @author zhiwen_feng
 *
 */
public interface EmployeeInfoRepository extends PagingAndSortingRepository<EmployeeInfoEntity, String> {

}
