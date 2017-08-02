package com.slfinance.shanlincaifu.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.LogInfoEntity;

/**
 * 登录日志查询
 * 
 * @author zhumin
 *
 */
public interface LogInfoEntityRepository extends PagingAndSortingRepository<LogInfoEntity, String> {
	// 根据不同的类型查询不同的日志
	@Query("FROM LogInfoEntity le WHERE le.relatePrimary=?")
	Page<LogInfoEntity> findLogInfoEntityByRelatePrimary(String relatePrimary, Pageable pageable);

	List<LogInfoEntity> findLogInfoEntityByRelatePrimary(String relatePrimary);
	
	List<LogInfoEntity> findLogInfoEntityByRelatePrimaryOrderByCreateDateDesc(String relatePrimary);

	// 根据不同的类型查询不同的日志
	@Query("FROM LogInfoEntity le WHERE le.logType=? and le.operPerson=? order by createDate desc")
	Page<LogInfoEntity> findLogInfoEntityByLogType(String logType, String operPerson, Pageable pageable);

	@Query("FROM LogInfoEntity le WHERE le.operPerson=? order by createDate desc")
	Page<LogInfoEntity> findLogInfoEntityByOperPerson(String operPerson, Pageable pageable);
	
	/**
	 * 通过关联主键和登录类型查询日志信息
	 *
	 * @author  wangjf
	 * @date    2015年11月2日 下午12:04:56
	 * @param relatePrimary
	 * @param logType
	 * @return
	 */
	List<LogInfoEntity> findByRelatePrimaryAndLogType(String relatePrimary, String logType);

	/**
	 * 根据主键和日志类型查询
	 * @param relatePrimary
	 * @param logType
	 * @return
	 */
	@Query("select new Map(l.createDate as createDate, u.userName as operPerson, l.operAfterContent as operAfterContent, l.memo as memo) from LogInfoEntity l, UserEntity u where l.operPerson = u.id and l.relatePrimary = :relatePrimary  and l.logType = :logType order by l.createDate desc")
	List<Map<String, Object>> findLogInfoByRelatePrimaryAndLogType(@Param("relatePrimary") String relatePrimary, @Param("logType")String logType);
	
	/**
	 * 根据主键和日志类型查询
	 * 
	 * @author zhangt
	 * @date   2016年2月25日上午11:10:40
	 * @param relatePrimary
	 * @param logType
	 * @return
	 */
	@Query("select new Map(l.id as auditId, l.createDate as auditDate, u.userName as auditUser, l.operAfterContent as auditStatus, l.memo as auditMemo) from LogInfoEntity l, UserEntity u where l.operPerson = u.id and l.relatePrimary = :relatePrimary  and l.logType = :logType order by l.createDate desc")
	List<Map<String, Object>> findListByRelatePrimaryAndLogType(@Param("relatePrimary") String relatePrimary, @Param("logType")String logType);
}
