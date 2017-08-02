/** 
 * @(#)AttachmentRepository.java 1.0.0 2015年7月8日 下午4:30:22  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.AttachmentInfoEntity;

/**   
 * 附件数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月8日 下午4:30:22 $ 
 */
public interface AttachmentRepository extends PagingAndSortingRepository<AttachmentInfoEntity, String>{

	/**
	 * 通过关联主键查询附件
	 *
	 * @author  wangjf
	 * @date    2015年10月16日 下午6:09:42
	 * @param relatePrimary
	 * @return
	 */
	AttachmentInfoEntity findByRelatePrimary(String relatePrimary);
	
	/**
	 * 通过关联主键查询附件
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午3:19:06
	 * @param relatePrimary
	 * @return
	 */
	List<AttachmentInfoEntity> findByRelatePrimaryAndShowType(String relatePrimary, String showType);
	
	/**
	 * 通过关联主键查询附件
	 * @author  liyy
	 * @date    2017年2月7日 下午4:19:06
	 * @param relatePrimary
	 * @return
	 */
	List<AttachmentInfoEntity> findByRelatePrimaryAndMemoAndShowType(String relatePrimary, String memo, String showType);
	
	
	/**
	 * 通过主键和状态查询
	 * @author zhangt
	 * @param relatePrimary
	 * @param recordStatus
	 * @return
	 */
	List<AttachmentInfoEntity> findByRelatePrimaryAndRecordStatus(String relatePrimary, String recordStatus);
	
	@Modifying
	@Query(value = "delete from BAO_T_ATTACHMENT_INFO a where a.RELATE_PRIMARY = ?1", nativeQuery=true)
	void deleteByRelatePrimary(String relatePrimary);

	/**
	 * 更新附件信息为无效
	 * @author liyy
	 * @param relateType String
	 * @param recordStatus String
	 * @return
	 */
	@Modifying
	@Query(value = " update BAO_T_ATTACHMENT_INFO set RECORD_STATUS='无效' where RELATE_TYPE = ?1 and RELATE_PRIMARY = ?2 and RECORD_STATUS = '有效' ", nativeQuery=true)
	void updateByTypeAndPrimary(String relateType,
			String relatePrimary);
	
	/**
	 * 通过主键和状态查询出List<Map<String, Object>>
	 * 
	 * @author zhangt
	 * @date   2016年2月24日下午4:14:48
	 * @param relatePrimary
	 * @param recordStatus
	 * @return
	 */
	@Query("select new Map(a.attachmentType as attachmentType, a.attachmentName as attachmentName, a.storagePath as storagePath) from AttachmentInfoEntity a where a.relatePrimary = :relatePrimary and a.recordStatus = :recordStatus order by a.createDate")
	List<Map<String, Object>> findMapByRelatePrimaryAndRecordStatus(@Param("relatePrimary") String relatePrimary, @Param("recordStatus") String recordStatus);

	/**
	 * 
	 * <根据主键和附件类型查询附件信息>
	 * <功能详细描述>
	 *
	 * @param reportId
	 * @param string
	 * @return [参数说明]
	 * @return AttachmentInfoEntity [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	AttachmentInfoEntity findByRelatePrimaryAndDocType(String reportId,
			String string);
}
