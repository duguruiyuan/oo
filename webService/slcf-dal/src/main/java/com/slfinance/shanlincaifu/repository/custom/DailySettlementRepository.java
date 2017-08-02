package com.slfinance.shanlincaifu.repository.custom;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.DailyInterestInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;

/**   
 * 每日结息数据访问接口
 *  
 * @author  linhj
 * @version $Revision:1.0.0, $Date: 2015年4月25日 上午11:26:06 $ 
 */
public interface DailySettlementRepository {
	/**
	 * 查询总价值
	 * @param param
	 * @return
	 */
	public BigDecimal findTotalPV(Map<String, Object> param);
	/**
	 * 查询实际总利息
	 * @param param
	 * @return
	 */
	public BigDecimal findActualInterest(Map<String, Object> param);
	
	/**
	 * 查询预计总利息
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findExceptInterest(Map<String, Object> param);
	
	/**
	 * 查询投资状态正常的所有分账信息
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findSubAccount(Map<String, Object> param);
	
	/**
	 * 查询投资状态正常的所有分账信息
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findSubAccountForPage(Map<String, Object> param);
	
	public int countSubAccount(Map<String, Object> param);
	
	/**
	 * 查询还款未处理金额
	 * @param param
	 * @return
	 */
	public BigDecimal untreatedAmount(Map<String, Object> param);
	
	/**
	 * 批量插入结息表
	 * @param param
	 * @return
	 */
	public void batchInsert(List<DailyInterestInfoEntity> list);
	
	public int findCurrDateRecordCount(Map<String, Object> param);
	
	/**
	 * 查询体验宝到期的所有分账信息
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findTYBExpireSubAccount(Map<String, Object> param);
	
	/**
     * 获取体验宝赎回需要发送短信的用户
     */
	public List<Map<String, Object>>  findExperienceSendSmsCust(Map<String, Object> map);
	
	@SuppressWarnings("rawtypes")
	public void batchUpdateList(List list);
	@SuppressWarnings("rawtypes")
	public void batchInsertList(List list);
	
	/**
	 * 通过流水批量更新账户
	 *
	 * @author  wangjf
	 * @date    2015年8月7日 下午2:00:14
	 * @param list
	 */
	public void batchUpdateSubAccount(final List<AccountFlowInfoEntity> list);
	
	/**
	 * 批量更新主账户
	 *
	 * @author  wangjf
	 * @date    2015年8月25日 上午10:55:01
	 * @param list
	 */
	public void batchUpdateAccount(final List<AccountInfoEntity> list);
	
	/**
	 * 批量更新分账户 
	 *
	 * @author  wangjf
	 * @date    2015年8月25日 下午2:02:52
	 * @param list
	 */
	public void batchUpdateSubAccountInfo(final List<SubAccountInfoEntity> list);
	
	/**
	 * 保存定期宝结息数据
	 *
	 * @author  wangjf
	 * @date    2015年8月15日 下午4:59:34
	 * @param totalPv
	 * @param untreatedAmount
	 * @param totalInterest
	 */
	public void saveTermDailyInterest(Date now, BigDecimal totalPv, BigDecimal untreatedAmount, BigDecimal totalInterest);
	
	/**
	 * 保存定期宝结息分账
	 *
	 * @author  wangjf
	 * @date    2015年8月15日 下午5:09:42
	 * @param totalPv
	 * @param untreatedAmount
	 * @param totalInterest
	 */
	public void saveTermSubAccount(Date now, BigDecimal totalPv, BigDecimal untreatedAmount, BigDecimal totalInterest);
	
	/**
	 * 查询定期宝待结息分账
	 *
	 * @author  wangjf
	 * @date    2015年8月15日 下午5:53:24
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findTermSubAccount(Map<String, Object> param);
	
	/**
	 * 统计定期待结息分账
	 *
	 * @author  wangjf
	 * @date    2015年8月15日 下午6:05:59
	 * @param param
	 * @return
	 */
	public int countTermSubAccount(Map<String, Object> param);
	
	/**
	 * 查询待结息数据
	 *
	 * @author  wangjf
	 * @date    2015年9月6日 下午3:03:19
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findWaitInterestForPage(Map<String, Object> param);
	
	/**
	 * 统计待结息数据
	 *
	 * @author  wangjf
	 * @date    2015年9月6日 下午3:03:42
	 * @param param
	 * @return
	 */
	public int countWaitInterestForPage(Map<String, Object> param); 
	
	/**
	 * 批量插入流水
	 *
	 * @author  wangjf
	 * @date    2015年9月9日 上午10:24:32
	 * @param list
	 */
	public void batchInsertAccountFlow(final List<AccountFlowInfoEntity> list);
	
//	/**
//	 * 批量插入流水详情
//	 *
//	 * @author  wangjf
//	 * @date    2015年9月9日 上午10:28:35
//	 * @param list
//	 */
//	public void batchInsertAccountFlowDetail(final List<AccountFlowDetailEntity> list);
	
//	/**
//	 * 批量插入关系流水
//	 *
//	 * @author  wangjf
//	 * @date    2015年9月9日 上午10:28:44
//	 * @param list
//	 */
//	public void batchInsertFlowBusiRelation(final List<FlowBusiRelationEntity> list);
//	
	/**
	 * 批量插入结息数据
	 *
	 * @author  wangjf
	 * @date    2015年9月9日 上午10:28:55
	 * @param list
	 */
	public void batchDailyInterestInfo(final List<DailyInterestInfoEntity> list);
	
	/**
	 * 查询待回收的赎回记录
	 *
	 * @author  wangjf
	 * @date    2015年12月15日 下午3:57:04
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findWaitRecoverAtone(Map<String, Object> param);
	
	/**
	 * 批量更新投资
	 *
	 * @author  wangjf
	 * @date    2015年12月15日 下午6:09:08
	 * @param list
	 */
	public void batchUpdateInvest(final List<InvestInfoEntity> list);
}
