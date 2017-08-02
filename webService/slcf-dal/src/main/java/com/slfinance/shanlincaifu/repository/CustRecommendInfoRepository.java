/** 
 * @(#)CustRecommendInfoRepository.java 1.0.0 2015年8月24日 下午3:41:13  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.CustRecommendInfoEntity;

/**   
 * 推荐人客户关系数据访问层
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月24日 下午3:41:13 $ 
 */
public interface CustRecommendInfoRepository extends PagingAndSortingRepository<CustRecommendInfoEntity, String> {

	/** 上一次解除前有效的数据 */
	@Query(value = " SELECT * FROM BAO_T_CUST_RECOMMEND_INFO WHERE CUST_ID = ?1 AND MEMO = ?2 ", nativeQuery = true)
	public List<CustRecommendInfoEntity> findRecommendByCustIdAndApplyId(String custId, String applyIdOld);
	
	@Query(value = " SELECT count(1) FROM BAO_T_CUST_RECOMMEND_INFO WHERE CUST_ID = ?1 AND QUILT_CUST_ID = ?2 AND RECORD_STATUS='有效' ",nativeQuery = true)
	public int findCountCustRecommendByCustIdAndQuiltCustId(String custId, String quiltCustId);

	@Query(value = " select * from (SELECT cr.* FROM BAO_T_CUST_APPLY_INFO ca, BAO_T_CUST_RECOMMEND_INFO cr WHERE cr.CUST_ID = ca.CUST_ID AND cr.QUILT_CUST_ID = ca.TRANSFER_CUST_ID AND ca.ID = ?1 order by cr.CREATE_DATE desc) a where rownum = 1",nativeQuery = true)
	public CustRecommendInfoEntity findOneByApplyIdOfTableOfCustApply(String custApplyId);

	/**
	 * 解除关系
	 * @author liyy
	 * @param custId String 客户经理Id
	 * @param userId String 操作人Id
	 * @data 2016/4/6
	 */ 
	@Modifying
	@Query(value = " UPDATE BAO_T_CUST_RECOMMEND_INFO SET RECORD_STATUS='无效' , EXPIRE_DATE = ?3 , MEMO = ?4 , VERSION = VERSION + 1 , LAST_UPDATE_USER = ?2 , LAST_UPDATE_DATE = ?3 WHERE RECORD_STATUS = '有效' AND CUST_ID = ?1 ",nativeQuery = true)
	public void setRelieveByCustId(String custId, String userId, Date lastUpdateDate, String memo);
	
	/**
	 * 根据客户id查询归属关系 
	 */
	@Query(value = " SELECT count(1) FROM BAO_T_CUST_RECOMMEND_INFO WHERE QUILT_CUST_ID = ?1 AND RECORD_STATUS='有效' ",nativeQuery = true)
	public int findCountCustRecommendByQuiltCustId(String quiltCustId);
	
	public CustRecommendInfoEntity findByQuiltCustIdAndRecordStatus(String quiltCustId, String recordStatus);
	
	/**
	 * 根据客户id查询归属关系 
	 */
	@Query(value = " SELECT * FROM BAO_T_CUST_RECOMMEND_INFO WHERE QUILT_CUST_ID = ?1 AND RECORD_STATUS='有效' ",nativeQuery = true)
	public CustRecommendInfoEntity findInfoCustRecommendByQuiltCustId(String quiltCustId);
}
