package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.DeptInfoEntity;

/**
 * BAO部门表数据访问层
 * 
 * @author zhiwen_feng
 *
 */
public interface DeptInfoRepository extends PagingAndSortingRepository<DeptInfoEntity, String>{

}
