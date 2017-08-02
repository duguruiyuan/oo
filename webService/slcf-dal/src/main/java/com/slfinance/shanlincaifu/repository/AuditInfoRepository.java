/** 
 * @(#)AuditInfoRepository.java 1.0.0 2015年4月28日 上午11:54:43  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月28日 上午11:54:43 $ 
 */
public interface AuditInfoRepository extends PagingAndSortingRepository<AuditInfoEntity, Serializable> {

	public List<AuditInfoEntity> findByCustIdAndApplyTypeAndRecordStatusOrderByApplyTimeDesc(String custId,String applyType,String recordStatus);
	
	public AuditInfoEntity findFirstByCustIdAndApplyTypeAndRecordStatusOrderByApplyTimeDesc(String custId,String applyType,String recordStatus);
	
	public AuditInfoEntity findAuditInfoEntityByRelatePrimary(String relatePrimary);
	
	public AuditInfoEntity findAuditInfoEntityByCustId(String custId);
	
	public AuditInfoEntity findByIdAndAuditStatus(String id,String auditStatus);
	
	/**
	 * 根据关联主键查询
	 * @param relatePrimary
	 * @return
	 */
	public List<AuditInfoEntity> findByRelatePrimary(String relatePrimary);
	
	public AuditInfoEntity findByRelatePrimaryAndAuditStatus(String relatePrimary,String auditStatus);
	
	@Query(value ="select a.* from bao_t_audit_info a where a.relate_primary = ?1 and a.audit_status in ('待审核', '审核回退')", nativeQuery = true)
	public AuditInfoEntity findAuditInfoEntityByRelatePrimaryAudits(String relatePrimary);
	
	/**
	 * 根据关联主键查询
	 * lyy
	 * @param relatePrimary
	 * @return
	 */
	@Query(value ="select a.* from bao_t_audit_info a where a.relate_primary = ?1 "
			+ "and a.APPLY_TYPE not in( '"+Constant.OPERATION_TYPE_60+"'"
			+ ", '"+Constant.OPERATION_TYPE_06+"' ) "
			, nativeQuery = true)
	public List<AuditInfoEntity> findByRelatePrimaryForLoan(String relatePrimary);
}
