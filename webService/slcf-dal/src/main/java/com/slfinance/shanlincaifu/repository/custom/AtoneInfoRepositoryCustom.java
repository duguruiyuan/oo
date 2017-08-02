/** 
 * @(#)AtoneInfoRepositoryCustom.java 1.0.0 2015年5月1日 下午2:18:09  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AtoneDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.AtoneInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.vo.ResultVo;

/**   
 * 自定义赎回数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月1日 下午2:18:09 $ 
 */
public interface AtoneInfoRepositoryCustom {

	/**
	 * 批量插入赎回详情
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午2:19:36
	 * @param atoneDetailInfoEntity
	 * @return
	 * @throws SLException
	 */
	public ResultVo batchInsertAllotDetail(AtoneDetailInfoEntity atoneDetailInfoEntity) throws SLException;
	
	/**
	 * 查询赎回记录
	 *
	 * @author  wangjf
	 * @date    2015年6月3日 下午4:31:07
	 * @param param
	 *            <tt>start：int:分页起始页</tt><br>
	 *            <tt>length：int:每页长度</tt><br>
	 *            <tt>custId： String:客户ID</tt><br>
	 *            <tt>tradeType： String:交易类型(可以为空)</tt><br>
	 *            <tt>tradeStatus： String:交易状态(可以为空)</tt><br>
	 *            <tt>opearteDateBegin：String:操作开始时间(可以为空)</tt><br>
	 *            <tt>opearteDateEnd：String:操作开始时间(可以为空)</tt><br>
	 *            <tt>productType： String:产品类型(可以为空)</tt><br>
	 * @return 
	 *         <tt>tradeDate： String:交易日期</tt><br>
	 *         <tt>tradeType： String:交易类型</tt><br>
	 *         <tt>tradeAmount： BigDecimal:交易金额</tt><br>
	 *         <tt>atoneExpenses： BigDecimal:手续费</tt><br>
	 *         <tt>atoneStatus： String:状态</tt><br>
	 */
	public Page<Map<String, Object>> findAllAtoneByCustId(Map<String, Object> param); 
	
	/**
	 * 计算已经审核处理成功的已赎回金额-体验宝
	 * 
	 * @author zhangzs
	 * @date  2015年6月11日 下午15:12:07
	 * @param tradeType
	 * @param custId
	 * @return
	 * @throws SLException
	 */
	public BigDecimal findSumAlreadyAtoneAmount(String tradeType,String custId)throws SLException;
	
	/**
	 * 查询定期赎回记录
	 *
	 * @author  wangjf
	 * @date    2015年8月17日 上午10:26:46
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findTermAtone(Map<String, Object> param);
	
	/**
	 * 统计定期赎回记录
	 *
	 * @author  wangjf
	 * @date    2015年8月17日 上午10:35:28
	 * @param param
	 * @return
	 */
	public int countTermAtone(Map<String, Object> param);
	
	/**
	 * 批量更新赎回信息
	 *
	 * @author  wangjf
	 * @date    2015年8月17日 下午3:56:51
	 * @param list
	 * @return
	 */
	public void batchUpdateAtone(final List<AtoneInfoEntity> list);
	
	/**
	 * 批量更新投资信息
	 *
	 * @author  wangjf
	 * @date    2015年8月17日 下午3:56:51
	 * @param list
	 * @return
	 */
	public void batchUpdateInvest(final List<InvestInfoEntity> list);
	
	/**
	 * 批量更新分账信息
	 *
	 * @author  wangjf
	 * @date    2015年8月17日 下午3:56:51
	 * @param list
	 * @return
	 */
	public void batchUpdateSubAccount(final List<SubAccountInfoEntity> list);
	
	/**
	 * 批量更新账户信息
	 *
	 * @author  wangjf
	 * @date    2015年8月17日 下午3:56:51
	 * @param list
	 * @return
	 */
	public void batchUpdateAccount(final List<AccountInfoEntity> list);
	
}
