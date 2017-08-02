/** 
 * @(#)TradeFlowInfoRepository.java 1.0.0 2015年4月21日 下午2:04:46  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;

/**   
 * 
 *  
 * @author  HuangXiaodong
 * @version $Revision:1.0.0, $Date: 2015年4月21日 下午2:04:46 $ 
 */
public interface TradeFlowInfoRepository 
					extends PagingAndSortingRepository<TradeFlowInfoEntity, String>, JpaSpecificationExecutor<TradeFlowInfoEntity>{
	
	/**根据时间和用户id和交易类型查询指定一天的交易*/
	@Query("select tr.tradeAmount  from  TradeFlowInfoEntity tr  where tr.tradeDate=? and tr.custId=? and tradeType=?  ")
	public BigDecimal findByTradeDateAndCustId(Date tradeDate, String custId, String tradeType);
	
	/**根据用户id和交易类型汇总所有交易金额*/
	@Query("select sum(NULLIF(tr.tradeAmount,0)) from  TradeFlowInfoEntity tr  where tr.custId=? and tradeType=? ")
	public BigDecimal sumTradeAmountBycustId(String custId,String tradeType);
	
	/**
	 * 根据交易编号查询过程流水记录
	 *
	 * @author  wangjf
	 * @date    2015年4月28日 上午11:47:52
	 * @param tradeNo
	 * @return
	 */
	public TradeFlowInfoEntity findByTradeNo(@Param("tradeNo") String tradeNo);
	
	/**
	 * 根据关联主键查询交易流水信息
	 * @author  zhangzs
	 * @param relatePrimary
	 * @return
	 */
	public TradeFlowInfoEntity findFirstByRelatePrimaryOrderByCreateDateDesc(String relatePrimary);

	/**
	 * 充值成功次数
	 *
	 * @author  wangjf
	 * @date    2015年6月24日 下午3:19:55
	 * @param custId
	 * @return
	 */
	@Query("select count(tr.id) from TradeFlowInfoEntity tr where tr.custId = ?1 and tr.tradeType = ?2 and tr.tradeStatus = ?3 ")
	public BigDecimal countSuccessfulRechargeByCustId(String custId, String tradeType, String tradeStatus);
	
	/**
	 * 查询提现记录
	 *
	 * @author  wangjf
	 * @date    2015年7月17日 下午5:50:32
	 * @param custId
	 * @param applyType
	 * @param auditStatus
	 * @return
	 */
	@Query("select a from TradeFlowInfoEntity a where a.relatePrimary in (select b.id from AuditInfoEntity b where b.custId = ? and b.applyType = ? and b.auditStatus = ?)")
	public List<TradeFlowInfoEntity> findWithDraw(String custId, String applyType, String auditStatus);
	
	/**
	 * 通过客户ID、交易类型、交易状态、交易描述、备注统计次数
	 *
	 * @author  wangjf
	 * @date    2015年11月11日 上午10:02:36
	 * @param custId
	 * @param tradeType
	 * @param tradeStatus
	 * @return
	 */
	@Query("select count(tr.id) from TradeFlowInfoEntity tr where tr.tradeDesc = ?1 and tr.tradeType = '实名认证' and tr.tradeStatus = '处理失败' and tr.memo in ('认证不一致','认证无数据记录')")
	public int countRealNameFailedByTradeDesc(String tradeDesc);
	
	/**
	 * 查询企业借款累计赎回
	 * 
	 * @author zhangt
	 * @date 2016年1月13日 
	 * @param tradeType
	 * @return
	 */
	@Query(value="select nvl(sum(round(bttfi.trade_amount, 2)),0) from bao_t_trade_flow_info bttfi where bttfi.trade_type = ?1", nativeQuery = true)
	public BigDecimal findProjectTotalAtoneAmount(String tradeType);
	
	/**
	 * 更具客户id，交易类型，交易状态查询
	 * 
	 * @author zhangt
	 * @date   2016年2月29日下午7:55:25
	 * @param custId
	 * @param tradeType
	 * @param tradeStatus
	 * @return
	 */
	public List<TradeFlowInfoEntity> findByCustIdAndTradeTypeAndTradeStatus(String custId, String tradeType, String tradeStatus);
}
