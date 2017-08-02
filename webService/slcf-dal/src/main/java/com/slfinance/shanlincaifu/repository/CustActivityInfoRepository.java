/** 
 * @(#)ActivityInfoRepository.java 1.0.0 2015年5月16日 下午2:38:50  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import com.slfinance.shanlincaifu.entity.CustActivityInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 *
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年5月16日 下午2:38:50 $
 */
public interface CustActivityInfoRepository extends PagingAndSortingRepository<CustActivityInfoEntity, String> {


	@Query("select a from CustActivityInfoEntity a where a.custId=? and a.rewardShape = ? order by a.createDate asc ")
	public List<CustActivityInfoEntity> findByCustId(String custId, String rewardShape);

	@Query("SELECT a from CustActivityInfoEntity a where a.activityId = (SELECT b from ActivityInfoEntity b where b.activityName=?) and a.tradeStatus=? and a.custId=? and  to_char(a.createDate,'mm')=? and a.recordStatus='有效' ")
	public List<CustActivityInfoEntity> findCustActivityInfoByDefine(String activityName,String tradeStatus,String custId,String mm);

	/**
	 * 统计用户活动奖励个数
	 *
	 * @author  wangjf
	 * @date    2015年6月25日 下午3:17:25
	 * @param custId
	 * @param activityId
	 * @return
	 */
	@Query("select count(a.id) from CustActivityInfoEntity a where a.custId = ?1 and a.activityId = ?2 ")
	public BigDecimal countByCustId(String custId, String activityId);

	/**
	 * 统计用户活动奖励个数（充值送体验金）
	 *
	 * @author  wangjf
	 * @date    2015年7月30日 下午5:51:00
	 * @param quiltCustId
	 * @param activityId
	 * @return
	 */
	@Query("select count(a.id) from CustActivityInfoEntity a where a.quiltCustId = ?1 and a.activityId = ?2 ")
	public BigDecimal countByQuiltCustId(String quiltCustId, String activityId);

	@Query("select a from CustActivityInfoEntity a where a.activityId = ?1 and a.custId = ?2 ")
	public CustActivityInfoEntity findByActivityIdAndCustId(String activityId,String custId);

	/**
	 * 根据客户编号和活动编号查找客户参与的活动记录.
	 * @param custId 客户编号
	 * @param activityId 活动编号
	 * @return 用户活动集合
	 */
	@Query("select a from CustActivityInfoEntity a where a.custId=?1 and a.activityId=?2")
	List<CustActivityInfoEntity> findByCustIdAndActivityId(String custId, String activityId);


	@Query("select A from CustActivityInfoEntity A where A.expireDate < to_date(to_char(sysdate,'yyyy/MM/dd'),'yyyy/MM/dd') and A.tradeStatus = '已领取'")
	public List<CustActivityInfoEntity> findBySystemDate();


	/**
	 * 查找等待红包账户扣款的活动记录
	 * @param memo 待扣款的活动记录
	 * @param tradeStatus 红包状态-全部使用
	 * @return 客户活动列表
	 */
	@Query("select a from CustActivityInfoEntity a where a.memo like CONCAT('%',?1,'%') and  tradeStatus=?2")
  List<CustActivityInfoEntity> findByAwaitingPayment(String memo, String tradeStatus);

	/**
	 * 查询使用标的使用活动记录
	 * @param loadId 标的编号
	 * @param rewardShape 活动类型
	 * @return
	 */
	List<CustActivityInfoEntity> findByLoanIdAndRewardShapeAndTradeStatus(String loadId, String rewardShape, String tradeStatus);


	@Query("select a from CustActivityInfoEntity a where id = ?1")
	CustActivityInfoEntity findById(String id);
	@Query("select A from CustActivityInfoEntity A where A.activityAwardId = ?1 and A.tradeStatus = ?2")
	public List<CustActivityInfoEntity> findByAwardIdAndTradeStatus(String awardId, String tradeStatus);

}
