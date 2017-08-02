/** 
 * @(#)CustAttachInfoRepository.java 1.0.0 2015年7月2日 上午11:53:17  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.ExpandInfoEntity;

/**   
 * 客户附属信息数据访问
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月2日 上午11:53:17 $ 
 */
public interface ExpandInfoRepository extends PagingAndSortingRepository<ExpandInfoEntity, String>{

	/**
	 * 根据通知次数查询
	 *
	 * @author  wangjf
	 * @date    2015年7月2日 下午3:26:36
	 * @param execStatus 处理成功
	 * @param alreadyNotifyTimes 通知次数 
	 * @return
	 */
	@Query("select a from ExpandInfoEntity a where a.thirdPartyType = ?1 and a.interfaceType = ?2 and (a.execStatus is null or a.execStatus != ?3) and a.alreadyNotifyTimes < ?4 ")
	public List<ExpandInfoEntity> findByAlreadyNotifyTimes(String thirdPartyType, String interfaceType, String execStatus, int alreadyNotifyTimes);
	
	/**
	 * 根据外关联表与交易编号查询
	 *
	 * @author  wangjf
	 * @date    2015年8月8日 下午4:11:59
	 * @param relatePrimary
	 * @param thirdPartyType
	 * @param interfaceType
	 * @param tradeCode
	 * @return
	 */
	@Query("select a from ExpandInfoEntity a where a.relatePrimary = ?1 and a.thirdPartyType = ?2 and a.interfaceType = ?3 and a.tradeCode = ?4 ")
	public List<ExpandInfoEntity> findByRelatePrimaryAndTradeCode(String relatePrimary, String thirdPartyType, String interfaceType, String tradeCode);

	/**
	 * 查询外部推送充值结果
	 *
	 * @author  wangjf
	 * @date    2015年8月8日 下午4:31:10
	 * @param custId
	 * @param thirdPartyType
	 * @param interfaceType
	 * @param tradeCode
	 * @return
	 */
	@Query("select SUM(b.tradeAmount) from TradeFlowInfoEntity b where b.custId = ?1 and b.tradeType = '充值' and b.tradeStatus = '处理成功' "
			+ "and b.id in (select a.relatePrimary from ExpandInfoEntity a where a.thirdPartyType = ?2 and a.interfaceType = ?3 and a.tradeCode = ?4)")
	public BigDecimal sumByCustIdAndTradeCode(String custId, String thirdPartyType, String interfaceType, String tradeCode);
	
	/**
	 * 查询外部推送结果
	 *
	 * @author  wangjf
	 * @date    2015年8月18日 下午5:12:02
	 * @param custId
	 * @param thirdPartyType
	 * @param interfaceType
	 * @param tradeCode
	 * @return
	 */
	@Query("select a from ExpandInfoEntity a where a.relatePrimary in (select b.id from TradeFlowInfoEntity b where b.custId = ?1 and b.tradeType = '充值' and b.tradeStatus = '处理成功') and a.thirdPartyType = ?2 and a.interfaceType = ?3 and a.tradeCode = ?4 ")
	public List<ExpandInfoEntity> findByCustIdAndTradeCode(String custId, String thirdPartyType, String interfaceType, String tradeCode);
	
	/**
	 * 根据通知次数查询
	 *
	 * @author  wangjf
	 * @date    2015年8月21日 上午10:02:58
	 * @param thirdPartyType
	 * @param interfaceType
	 * @param execStatus
	 * @param alreadyNotifyTimes
	 * @return
	 */
	@Query("select a from ExpandInfoEntity a where a.thirdPartyType = ?1 and a.interfaceType = ?2 and (a.execStatus is null or a.execStatus != ?3) and a.alreadyNotifyTimes < ?4 and a.memo = ?5 ")
	public List<ExpandInfoEntity> findByAlreadyNotifyTimes(String thirdPartyType, String interfaceType, String execStatus, int alreadyNotifyTimes, String memo);
	
	/**
	 * 根据meid和通知次数查询
	 * 
	 * @author zhangt
	 * @date   2015年10月21日下午4:20:52
	 * @param thirdPartyType
	 * @param interfaceType
	 * @param execStatus
	 * @param alreadyNotifyTimes
	 * @return
	 */
	@Query("select a from ExpandInfoEntity a where a.thirdPartyType = ?1 and a.interfaceType = ?2 and (a.execStatus is null or a.execStatus != ?3) and a.alreadyNotifyTimes < ?4 "
			+ " and a.meId in (select b.meId from DeviceInfoEntity b where b.tradeType = '注册') ")
	public List<ExpandInfoEntity> findByMeidAndAlreadyNotifyTimes(String thirdPartyType, String interfaceType, String execStatus, int alreadyNotifyTimes);
	
	/**
	 * 根据innerTradeCode查询
	 * 
	 * @author zhangt
	 * @date   2015年10月23日上午10:36:47
	 * @param tradeCode
	 * @return
	 */
	public ExpandInfoEntity findByInnerTradeCode(String innerTradeCode);
	
	/**
	 * 根据transcode和meId查询
	 * 
	 * @author zhangt
	 * @date   2015年10月23日下午4:39:23
	 * @param tradeCode
	 * @param meId
	 * @return
	 */
	public ExpandInfoEntity findByTradeCodeAndMeId(String tradeCode, String meId);
	
	/**
	 * 根据通知次数查询
	 *
	 * @author  wangjf
	 * @date    2016年10月18日 下午3:23:48
	 * @param interfaceType
	 * @param execStatus
	 * @param alreadyNotifyTimes
	 * @return
	 */
	@Query("select a from ExpandInfoEntity a where a.interfaceType = ?1 and (a.execStatus is null or a.execStatus != ?2) and a.alreadyNotifyTimes < ?3 order by createDate")
	public List<ExpandInfoEntity> findNeedSendRecord(String interfaceType, String execStatus, int alreadyNotifyTimes);

}
