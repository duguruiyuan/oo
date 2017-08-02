package com.slfinance.shanlincaifu.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.LoginLogInfoEntity;

public interface LoginLogInfoRepository extends PagingAndSortingRepository<LoginLogInfoEntity, String>{
	/**根据用户id和交易类型汇总所有交易金额*/
	@Query("select tr.loginDate  from LoginLogInfoEntity tr where tr.custId=? and tr.loginDate<?  order by tr.loginDate desc   ")
	public List<Object[]> findLastLoginInfoOrderByLoginDateDesc(String custId,Date sysDate);


}