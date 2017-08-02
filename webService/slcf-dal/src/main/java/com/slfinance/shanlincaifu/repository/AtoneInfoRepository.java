/** 
 * @(#)AtoneInfoRepository.java 1.0.0 2015年4月30日 上午11:55:45  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.AtoneInfoEntity;

/**   
 * 赎回数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月30日 上午11:55:45 $ 
 */
public interface AtoneInfoRepository  extends PagingAndSortingRepository<AtoneInfoEntity, String>{

	
	/**
	 * 查询某天赎回总额，赎回次数
	 *
	 * @author  wangjf
	 * @date    2015年4月30日 下午3:09:56
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Query("select NULLIF(sum(A.atoneTotalAmount),0), count(A.id) from AtoneInfoEntity A where A.custId = ?1 and A.productId = ?2 and A.createDate between ?3 and ?4 and A.atoneMethod = ?5")
	public List<Object> queryByCreateDate(String custId, String productId, Date startDate, Date endDate, String atoneMethod);
	
	/**
	 * 查询所有赎回金额
	 *
	 * @author  wangjf
	 * @date    2015年5月27日 下午6:27:25
	 * @param custId
	 * @param productId
	 * @return
	 */
	@Query(value="SELECT NVL(SUM(TRUNC_AMOUNT_WEB(A.ATONE_TOTAL_AMOUNT)), 0) FROM BAO_T_ATONE_INFO A where A.CUST_ID = ? AND A.PRODUCT_ID = ? ", nativeQuery = true)
	public BigDecimal queryTotalAtoneAmountByCustId(String custId, String productId);
	
	/**
	 * 根据结清时间查询不存在详情的赎回信息
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 上午11:13:40
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Query("select A from AtoneInfoEntity A where A.auditStatus = ?1 and A.cleanupDate between ?2 and ?3 and A.id not in (select B.atoneId from AtoneDetailInfoEntity B)")
	public List<AtoneInfoEntity> queryByCleanupDate(String auditStatus, Date startDate, Date endDate);
	
	/**
	 * 查询所有赎回中金额
	 *
	 * @author  wangjf
	 * @date    2015年5月27日 下午6:27:25
	 * @param custId
	 * @param productId
	 * @return
	 */
	@Query(value="SELECT NVL(SUM(TRUNC_AMOUNT_WEB(A.ATONE_TOTAL_AMOUNT)), 0) FROM BAO_T_ATONE_INFO A where A.CUST_ID = ? AND A.PRODUCT_ID = ? AND A.ATONE_STATUS = '未处理' ", nativeQuery = true)
	public BigDecimal queryTotalAtoningAmountByCustId(String custId, String productId);
	
	/**
	 * 计算已经审核处理成功的已赎回金额
	 *
	 * @author  zhangzs
	 * @date    2015年6月10日 下午5:31:25
	 * @param custId
	 * @param productId
	 * @param atoneStatus
	 * @param auditStatus
	 * @return
	 */
	@Query(value="SELECT NVL(SUM(TRUNC_AMOUNT_WEB(ALREADY_ATONE_AMOUNT)), 0) FROM BAO_T_ATONE_INFO  where CUST_ID = ?1 AND PRODUCT_ID = ?2 AND ATONE_STATUS = ?3 AND AUDIT_STATUS =?4 ", nativeQuery = true)
	
	public BigDecimal findSumAlreadyAtoneAmount(String custId, String productId,String atoneStatus,String auditStatus);
	/**
	 * 计算已经审核处理成功的已赎回金额
	 *
	 * @author  zhangzs
	 * @date    2015年6月10日 下午5:31:25
	 * @param custId
	 * @param productId
	 * @param atoneStatus
	 * @param auditStatus
	 * @return
	 */
	@Query(value="SELECT NVL(SUM(TRUNC_AMOUNT_WEB(ALREADY_ATONE_AMOUNT)), 0) FROM BAO_T_ATONE_INFO  where CUST_ID = ?1 AND ATONE_STATUS = ?2 AND AUDIT_STATUS =?3 AND PRODUCT_ID IN ( SELECT ID FROM BAO_T_PRODUCT_INFO WHERE PRODUCT_TYPE  IN ( SELECT ID FROM BAO_T_PRODUCT_TYPE_INFO WHERE TYPE_NAME = ?4 )  ) ", nativeQuery = true)
	public BigDecimal findAtonedAmountByType(String custId,String atoneStatus,String auditStatus, String productType);
	
	/**
	 * 根据投资ID查询
	 *
	 * @author  wangjf
	 * @date    2015年8月15日 下午4:17:28
	 * @param investId
	 * @return
	 */
	public AtoneInfoEntity findByInvestId(String investId);
	
	/**
	 * 查询理财计划赎回记录
	 *
	 * @author  wangjf
	 * @date    2016年2月24日 上午10:38:08
	 * @param createDate
	 * @return
	 */
	@Query(value = "select A from AtoneInfoEntity A "
			+ "where A.cleanupDate >= :cleanupDate and A.cleanupDate < :cleanupDate + 1 "
			+ " and A.atoneMethod = :atoneMethod"
			+ " and A.auditStatus = :auditStatus"
			+ " and A.investId in (select id from InvestInfoEntity B where B.wealthId is not null)" )
	public List<AtoneInfoEntity> findByCleanupDateAndAtoneMethodAndAuditStatus(@Param("cleanupDate")Date cleanupDate, @Param("atoneMethod")String atoneMethod, @Param("auditStatus")String auditStatus);
}
