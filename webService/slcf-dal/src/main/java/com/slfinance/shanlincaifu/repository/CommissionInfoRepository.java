/** 
 * @(#)CommissionInfoRepository.java 1.0.0 2015年8月24日 下午3:25:38  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CommissionInfoEntity;

/**   
 * 提成数据访问层
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月24日 下午3:25:38 $ 
 */
public interface CommissionInfoRepository extends PagingAndSortingRepository<CommissionInfoEntity, String>{

	/**
	 * 更新状态
	 *
	 * @author  wangjf
	 * @date    2015年8月25日 上午11:16:37
	 * @param date
	 * @return
	 */
	@Modifying
	@Query(" update CommissionInfoEntity set tradeStatus = '已结算' where tradeStatus = '未结算' and commDate < ? ")
	public int updateTradeStatusByCommDate(Date date);
	
	/**
	 * 统计个数
	 *
	 * @author  wangjf
	 * @date    2015年8月25日 上午11:45:52
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Query(" select count(id) from CommissionInfoEntity where productTypeId = ?1 and commDate between ?2 and ?3 ")
	public int countByCommDate(String productTypeId, Date startDate, Date endDate);
	
	/**
	 * 统计个数
	 *
	 * @author  wangjf
	 * @date    2015年10月12日 下午3:26:26
	 * @param productTypeId
	 * @param commonMonth
	 * @return
	 */
	@Query(" select count(id) from CommissionInfoEntity where productTypeId = ?1 and commMonth = ?2 ")
	public int countByCommonMonth(String productTypeId, String commonMonth);
	
	/**
	 *	查询用户的累计推广奖励和再投金额
	 *
	 * @author zhangzs
	 * @param  custId
	 * @return
	 */
	@Query(nativeQuery = true, value = " select sum(nvl(REWARD_AMOUNT,0)) from bao_t_commission_info where cust_id = :custId and product_type_id in ( select id from bao_t_product_type_info where type_name = :typeName ) union all select sum(nvl(YEAR_INVEST_AMOUNT,0)) from bao_t_commission_info where cust_id = :custId and product_type_id in ( select id from bao_t_product_type_info where type_name = :typeNameFixed )")
	List<Map<String,Object>> sumInvestAmount(@Param(value="custId")String custId,@Param(value="typeName")String typeName,@Param(value="typeNameFixed")String typeNameFixed)throws SLException;
	
	/**
	 *	查询用户的推荐信息(日期)分页
	 *
	 * @author zhangzs
	 * @param  custId
	 * @param  productType
	 * @param  startDate
	 * @param  endDate
	 * @param  pageable
	 * @return
	 */
	@Query(value = " select new Map(id as id,commDate as date,investAmount as dateInvestAmount,rewardAmount as recAward,tradeStatus as isSettled ) from CommissionInfoEntity where productTypeId = (select id from ProductTypeInfoEntity where typeName = :typeName and rownum = 1 ) and custId = :custId and tradeStatus in (:isSettled) and trunc(commDate) between trunc(:startDate)  and trunc(:endDate)  order by commDate desc  ", countQuery = " select count(id) from CommissionInfoEntity where productTypeId = (select id from ProductTypeInfoEntity where typeName = :typeName and rownum = 1 ) and custId = :custId and tradeStatus in (:isSettled)  and trunc(commDate) between trunc(:startDate)  and trunc(:endDate)   ")
	Page<Map<String,Object>> getResListPage(@Param("custId") String custId,@Param("typeName")String productType, @Param("startDate") Date startDate,@Param("endDate") Date endDate,@Param("isSettled") Collection<String> isSettled, Pageable pageable);

	/**
	 *	查询用户的推荐信息(月份)分页
	 *
	 * @author zhangzs
	 * @param  custId
	 * @param  productType
	 * @param  startDate
	 * @param  endDate
	 * @param  isSettled
	 * @param  pageable
	 * @return
	 */
	@Query( value = " select new Map(id as id,commMonth as month,yearInvestAmount as monthInvestAmount) from CommissionInfoEntity where productTypeId = (select id from ProductTypeInfoEntity where typeName = :typeName and rownum = 1 ) and custId = :custId and to_number(commMonth) between :startDate  and :endDate order by commMonth desc  ", countQuery = "select count(id) from CommissionInfoEntity where productTypeId = (select id from ProductTypeInfoEntity where typeName = :typeName and rownum = 1 ) and custId = :custId and commMonth between :startDate  and :endDate " )
	Page<Map<String,Object>> getResListMonthPage(@Param("custId") String custId,@Param("typeName")String productType, @Param("startDate") String startDate,@Param("endDate") String endDate,Pageable pageable)throws SLException;
	
	/**
	 *	查询用户的推荐信息详细(日期)分页
	 *
	 * @author zhangzs
	 * @param  custId
	 * @param  productType
	 * @param  id
	 * @param  pageable
	 * @return
	 */
	@Query(value = " select new Map(c.commDate as date,u.custName as custName,t.investAmount as investAmount ) from CommissionInfoEntity c, CommissionDetailInfoEntity t,CustInfoEntity u where t.quiltCustId = u.id and c.id = t.commissionId and c.productTypeId = (select id from ProductTypeInfoEntity where typeName = ?2 and rownum = 1 ) and c.custId = ?1 and c.id = ?3 order by u.createDate desc  ",countQuery="select count(t.id) from CommissionInfoEntity c, CommissionDetailInfoEntity t,CustInfoEntity u where t.quiltCustId = u.id and c.id = t.commissionId and c.productTypeId = (select id from ProductTypeInfoEntity where typeName = ?2 and rownum = 1 ) and c.custId = ?1 and c.id = ?3 ")
	Page<Map<String,Object>> getResListDetailPage(String custId,String productType,String id, Pageable pageable)throws SLException;
	
	/**
	 *	查询用户的推荐信息详情(月份)分页
	 *
	 * @author zhangzs
	 * @param  custId
	 * @param  productType
	 * @param  id
	 * @param  pageable
	 * @return
	 */
	@Query(value = " select new Map(c.commMonth as month,u.custName as custName,p.productName as productName,t.yearInvestAmount as yearInvestAmount,i.currTerm as currTerm ) from CommissionInfoEntity c, CommissionDetailInfoEntity t,CustInfoEntity u ,ProductInfoEntity p,InvestInfoEntity i where p.id = t.productId and t.quiltCustId = u.id and c.id = t.commissionId and t.investId = i.id and c.productTypeId = (select id from ProductTypeInfoEntity where typeName = ?2 and rownum = 1 ) and c.custId = ?1 and c.id = ?3 order by i.createDate desc  ", countQuery="select count(t.id) from CommissionInfoEntity c, CommissionDetailInfoEntity t,CustInfoEntity u,ProductInfoEntity p ,InvestInfoEntity i where p.id = t.productId and t.quiltCustId = u.id and c.id = t.commissionId and t.investId = i.id and c.productTypeId = (select id from ProductTypeInfoEntity where typeName = ?2 and rownum = 1 ) and c.custId = ?1 and c.id = ?3")
	Page<Map<String,Object>> getResListMonthDetailPage(String custId,String productType,String id, Pageable pageable)throws SLException;
	
	/**
	 *	查询用户的当前月金额
	 *
	 * @author zhangzs
	 * @param  custId
	 * @param  productType
	 * @param  startDate
	 * @param  endDate
	 * @return
	 */
	@Query(nativeQuery = true, value = ""+
			"select sum(trunc(t.invest_amount * m.type_term / 12,8)) commissionInvestAmount from bao_t_invest_info  t,bao_t_product_info m "+
            "where t.product_id = m.id "+
              " and t.cust_id in (select id from bao_t_cust_info where (id = ?1 and IS_RECOMMEND = '是') or (INVITE_ORIGIN_ID = ?1 and (IS_RECOMMEND is null or IS_RECOMMEND != '是'))) "+
              " and t.product_id in (select id from bao_t_product_info n where n.product_type in (select id from bao_t_product_type_info m  where m.type_name = ?2)) "+
              " and t.invest_date >= ?3 "+
              " and t.invest_date <= ?4 "
			+ "")
	BigDecimal getCurrentYearInveAmount(String custId,String productType,String startDate,String endDate)throws SLException;
	
}
