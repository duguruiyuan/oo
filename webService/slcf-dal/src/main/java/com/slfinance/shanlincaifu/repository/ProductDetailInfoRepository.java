/** 
 * @(#)ProductDetailInfoRepository.java 1.0.0 2015年4月24日 下午2:10:00  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.ProductDetailInfoEntity;
import com.slfinance.shanlincaifu.repository.custom.ProductDetailInfoRepositoryCustom;

/**   
 * 产品详情信息表
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年4月24日 下午2:10:00 $ 
 */
public interface ProductDetailInfoRepository 
			extends PagingAndSortingRepository<ProductDetailInfoEntity, String>,ProductDetailInfoRepositoryCustom{
	
	/**
	 * 通过产品ID查询产品详情
	 *
	 * @author  wangjf
	 * @date    2015年4月29日 下午2:21:20
	 * @param productId
	 * @return
	 */
	public ProductDetailInfoEntity findByProductId(String productId);

	
	@Query("select A from  ProductDetailInfoEntity A  where A.productId in "
			+ " (select B.id from ProductInfoEntity B where B.productType = "
			+ " (select C.id from ProductTypeInfoEntity C where C.typeName=?)) ")
	public ProductDetailInfoEntity findProductDetailInfoByProductName(String productName);
	
	/**
	 * 获取参与机构数、参与债权数
	 *
	 * @author  wangjf
	 * @date    2015年5月28日 下午6:29:25
	 * @param productName
	 * @return BigDecimal[2]
	 */
	@Query(value = " SELECT COUNT(DISTINCT D.DEBT_SOURCE_CODE), COUNT(D.ID) "
			+" FROM BAO_T_PRODUCT_TYPE_INFO A, "
			+" BAO_T_ALLOT_INFO        B, "
			+" BAO_T_ALLOT_DETAIL_INFO C, "
			+" BAO_T_LOAN_INFO         D, "
			+" BAO_T_LOAN_DETAIL_INFO  E "
			+" WHERE A.ID = B.RELATE_PRIMARY "
			+" AND B.ID = C.ALLOT_ID "
			+" AND C.LOAN_ID = D.ID "
			+" AND D.ID = E.LOAN_ID "
			+" AND B.ALLOT_STATUS IN ('已分配', '已使用') "
			+" AND E.CREDIT_RIGHT_STATUS = '正常' "
			+" AND A.TYPE_NAME = ? ", nativeQuery = true)
	public Object queryPartake(String productName);
	
	/**
	 * 通过产品类型名称查询产品明细
	 *
	 * @author  wangjf
	 * @date    2015年8月20日 下午12:20:10
	 * @param productName
	 * @return
	 */
	@Query("select A from  ProductDetailInfoEntity A  where A.productId in "
			+ " (select B.id from ProductInfoEntity B where B.productType = "
			+ " (select C.id from ProductTypeInfoEntity C where C.typeName=?)) ")
	public List<ProductDetailInfoEntity> findTermProductDetailInfoByProductName(String productName);
	
	/**
	 * 通过产品名称查询产品详情
	 *
	 * @author  wangjf
	 * @date    2015年11月11日 下午6:34:57
	 * @param productName
	 * @return
	 */
	@Query("select NVL(sum(alreadyInvestAmount), 0) from ProductDetailInfoEntity A where A.productId in"
		    + " (select B.id from ProductInfoEntity B where B.productName = ?)")
	public Object findByProductName(String productName);
	
	/**
	 * 获取参与机构数、参与债权数
	 *
	 * @author  wangjf
	 * @date    2015年12月14日 下午3:51:34
	 * @param productName
	 * @return
	 */
	@Query(value = " SELECT COUNT(DISTINCT D.DEBT_SOURCE_CODE), COUNT(D.ID) "
			+" FROM BAO_T_PRODUCT_TYPE_INFO A, "
			+" BAO_T_ALLOT_INFO        B, "
			+" BAO_T_ALLOT_DETAIL_INFO C, "
			+" BAO_T_LOAN_INFO         D, "
			+" BAO_T_LOAN_DETAIL_INFO  E "
			+" WHERE A.ID = B.RELATE_PRIMARY "
			+" AND B.ID = C.ALLOT_ID "
			+" AND C.LOAN_ID = D.ID "
			+" AND D.ID = E.LOAN_ID "
			+" AND B.ALLOT_STATUS IN ('已分配', '已使用') "
			+" AND E.CREDIT_RIGHT_STATUS = '正常' "
			+" AND D.DEBT_SOURCE_CODE NOT IN ('052', '054') " // 排除债权来源“企业借款”和“海程融资租赁”
			+" AND A.TYPE_NAME = ? ", nativeQuery = true)
	public Object queryPartakeForDisplay(String productName);
}
