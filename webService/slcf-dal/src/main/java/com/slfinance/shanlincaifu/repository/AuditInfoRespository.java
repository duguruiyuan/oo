/** 
 * @(#)AuditInfoRespository.java 1.0.0 2015年4月28日 下午2:22:13  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.slfinance.shanlincaifu.entity.AuditInfoEntity;

/**   
 * 审核数据访问类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月28日 下午2:22:13 $ 
 */
@RepositoryRestResource(collectionResourceRel = "audit", path = "audits")
public interface AuditInfoRespository extends PagingAndSortingRepository<AuditInfoEntity, String>{

	/**
	 * 根据关联主键查询
	 *
	 * @author  wangjf
	 * @date    2015年7月10日 上午11:09:51
	 * @param relatePrimary
	 * @return
	 */
	public AuditInfoEntity findByRelatePrimary(String relatePrimary);
	
	/**
	 * 根据关联主键、申请类型、审核状态查询
	 *
	 * @author  wangjf
	 * @date    2015年7月17日 下午3:12:49
	 * @param custId
	 * @param applyType
	 * @param auditStatus
	 * @return
	 */
	public List<AuditInfoEntity> findByCustIdAndApplyTypeAndAuditStatus(String custId, String applyType, String auditStatus);
	
	/**
	 * 查询审核记录
	 *
	 * @author  wangjf
	 * @date    2016年10月17日 下午8:34:42
	 * @param relatePrimary
	 * @return
	 */
	@Query(value="select * from (select * from BAO_T_AUDIT_INFO where RELATE_PRIMARY = :relatePrimary AND APPLY_TYPE = :applyType order by CREATE_DATE desc)a where rownum = 1", nativeQuery=true)
	public AuditInfoEntity findFirstByRelatePrimaryOrderByCreateDateDesc(@Param("relatePrimary")String relatePrimary, @Param("applyType")String applyType);
	
	/**
	 * 根据关联主键查询
	 * @author  liyy
	 * @date    2016年12月19日 上午11:09:51
	 * @param relatePrimary
	 * @return
	 */
	public List<AuditInfoEntity> findLoanByRelatePrimary(String relatePrimary);
}
