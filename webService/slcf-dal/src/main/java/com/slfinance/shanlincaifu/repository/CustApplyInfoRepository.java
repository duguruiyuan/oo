/** 
 * @(#)CustApplyInfoRepository.java 1.0.0 2015年8月24日 下午3:22:32  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.CustApplyInfoEntity;

/**   
 * 客户申请数据访问层
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月24日 下午3:22:32 $ 
 */
public interface CustApplyInfoRepository extends PagingAndSortingRepository<CustApplyInfoEntity, String> {

	@Query(" FROM CustApplyInfoEntity caie where caie.custId = ? order by createDate desc")
	public List<CustApplyInfoEntity> findCustApplyInfoEntityCustomer(String custId);

	/**
	 * 客户实名认证
	 * @param custId :String 客户Id
	 */
	@Query(value = " SELECT count(1) count FROM BAO_T_CUST_INFO WHERE ID = ?1 AND CREDENTIALS_CODE IS NOT NULL ", nativeQuery = true)
	public int checkRealNameAuth(String custId);

	public List<CustApplyInfoEntity> findByCustIdAndApplyTypeAndRecordStatusOrderByCreateDateDesc(
			String custId, String applyTpye, String recordStatus);
	
	/**
	 * 通过客户ID、类型、审核状态查询
	 * 
	 * @param custId
	 * @param applyTpye
	 * @param applyStatus
	 * @return
	 */
	public List<CustApplyInfoEntity> findByCustIdAndApplyTypeAndApplyStatusOrderByCreateDateDesc(
			String custId, String applyTpye, String applyStatus);
	
}
