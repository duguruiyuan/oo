/** 
 * @(#)CustActivityRepositoryCustom.java 1.0.0 2015年5月19日 下午3:06:28  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */
package com.slfinance.shanlincaifu.repository.custom;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.ActivityInfoEntity;
import com.slfinance.shanlincaifu.entity.CustActivityDetailEntity;
import com.slfinance.shanlincaifu.entity.CustActivityInfoEntity;
import com.slfinance.vo.ResultVo;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 
 * 善林财富活动
 * 
 * @author caoyi
 * @version $Revision:1.0.0, $Date: 2015年5月19日 下午3:06:28 $
 */

public interface CustActivityInfoRepositoryCustom {
	/**
	 * 体验金管理
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> findExperienceGold(Map<String, Object> param);

	public Map<String, Object> findExperienceGoldAudit(Map<String, Object> param);

	/**
	 * 体验金总奖励
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> findExperienceGoldById(Map<String, Object> param);

	/**
	 * 获取体验金信息
	 * 
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findExperienceGoldDetail(Map<String, Object> param);

	/**
	 * 推荐奖励信息
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> findRewardById(Map<String, Object> param);

	/**
     * 获取我的推荐记录
	 * 
	 * @author caoyi
	 * @date 2015年05月19日 上午11:49:25 
	 * @param Map<String, Object>
	 * @return Page<Map<String, Object>>
	 */
	public Page<Map<String, Object>> findCustRecommendList(Map<String, Object> map);
	
	/**
     * 获取我的体验金记录
	 * 
	 * @author caoyi
	 * @date 2015年05月19日 下午14:36:25 
	 * @param Map<String, Object>
	 * @return Page<Map<String, Object>>
	 */
	public Page<Map<String, Object>> findCustExperienceList(Map<String, Object> map);
	
	/**
	 * 生成客户活动记录
	 *
	 * @author  wangjf
	 * @date    2015年6月4日 下午3:07:35
	 * @param map
	 */
	public List<Map<String, Object>> caclCustActivityDetail(Map<String, Object> map);
	
	/**
	 * 批量插入客户活动记录
	 *
	 * @author  wangjf
	 * @date    2015年6月4日 下午3:53:28
	 * @param list
	 */
	public void batchInsertActivityDetail(List<CustActivityDetailEntity> list);
	
	
	/**
	 * 根据用户id查询用户推荐的体验总奖励
	 * @param 
	 * 	   <li>custId 当前登陆用户			{@link java.lang.String}</li>
	 * @return
	 * @throws SLException
	 */
	public BigDecimal getExpAmountByCustId(String custId)throws SLException;
	
	/**
     * 获取金牌推荐人统计信息
	 * 
	 * @author caoyi
	 * @date 2015年08月25日 上午11:42:36 
	 * @param Map<String, Object>
	 * @return Map<String, Object>
	 */
	public Map<String, Object> findCustCommissionInfo(Map<String, Object> map);
	
	/**
     * 获取我的佣金记录
	 * 
	 * @author caoyi
	 * @date 2015年08月25日 上午11:44:32 
	 * @param Map<String, Object>
	 * @return Page<Map<String, Object>>
	 */
	public Page<Map<String, Object>> findCustCommissionList(Map<String, Object> map);
	
	
	/**
     * 获取我的佣金详情记录
	 * 
	 * @author caoyi
	 * @date 2015年08月25日 上午11:45:32 
	 * @param Map<String, Object>
	 * @return Page<Map<String, Object>>
	 */
	public Page<Map<String, Object>> findCustCommissionDetailList(Map<String, Object> map);

	/**
	 * 查询绑定银行卡奖励的客户
	 *
	 * @author  wangjf
	 * @date    2016年12月29日 上午10:55:30
	 * @param map
	 * @return
	 */
	public Page<Map<String, Object>> findCustByBindCard(Map<String, Object> map);
	/**
	 * 查询红包奖励榜
	 *
	 * @author  fengyl
	 * @date    2017年4月27日 
	 * @return
	 */
	public Page<Map<String, Object>> queryAwardList(Map<String, Object> map);

	/**
	 * 2017 市场部6月活动 奖励
	 */
	public BigDecimal findCustAwardFor201706(String custId);

	/**
	 * 2017 市场部6月活动 邀请人数
	 */
	public int findCustInvitedFor201706(String custId);

	/**
	 * 2017-6-27 我的红包列表
	 * @param requestParams
	 * @return
	 */
  Object redEnvelopeList(Map<String, Object> requestParams);

	/**
	 * 根据红包ID	和客户ID查找活动记录
	 * @param custActivityId 活动id
	 * @param custId 客户id
	 * @return 活动记录
	 */
	Map<String,Object> findRewardByIdAndCustId(String custActivityId, String custId);

	/**
	 * 根据活动id查找活动信息
	 * @param custActivityId 活动id
	 * @return 活动信息
	 */
	CustActivityInfoEntity findById(String custActivityId);

	/**
	 * 2017 平台大促需求 奖励管理-奖励金
	 */
	public BigDecimal findCustAwardAmount(Map<String, Object> map);

//	/**
//	 * 2017平台大促需求 投资排行拿豪礼列表
//	 */
//	public Page<Map<String, Object>> queryActualTimeAwardList(
//			Map<String, Object> map);

	/**
	 * 2017平台大促需求 奖励管理-累计出借金额，年化出借金额
	 */
	public List<Map<String, Object>> findTotalInvestAmountAndTotalYearAmount(
			Map<String, Object> map);

	/**
	 * 2017平台大促需求 奖励管理-邀请人数
	 */
	public int getCountByInviteOriginId(Map<String, Object> param);
	
	/**
	 * 2017平台大促需求 奖励管理-活动列表
	 */
	public List<ActivityInfoEntity> getListActivityInfo();

	/**
	 * 通过loanId查询体验标投资详情
	 * @param loanId
	 * @return
	 */
	ResultVo findExpLoadDetail(String loanId, String investId, String custId);

	/**
	 * 查询已领取和全部使用状态下已过期的体验金
	 * @param params
	 * @return
	 */
	List<CustActivityInfoEntity> findNotUsedAndUsedExpireExpLoan();

	/**
	 * 查询体验金
	 * @param params
	 * @return
	 */
	ResultVo findExpCouponList(Map<String, Object> params);
	
	/**
	 * 更新已过期的红包状态为已过期
	 * @return
	 */
	public int updateActivityByExpireDate();
}
