/** 
 * @(#)AccountInfoRepositoryCustom.java 1.0.0 2015年4月25日 上午11:21:46  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;

/**
 * 自定义账户数据访问接口
 * 
 * @author wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月25日 上午11:21:46 $
 */
public interface AccountInfoRepositoryCustom {

	/**
	 * 用户资金统计
	 *
	 * @author wangjf
	 * @date 2015年4月25日 上午11:24:31
	 * @param param
	 *            <tt>nickName：String:用户昵称</tt><br>
	 *            <tt>custName：String:真实姓名</tt><br>
	 *            <tt>credentialsCode：String:证件号码</tt><br>
	 *            <tt>opearteDateBegin：String:操作开始时间</tt><br>
	 *            <tt>opearteDateEnd：String:操作开始时间</tt><br>
	 *            <tt>tradeType：String:交易类型</tt><br>
	 * @return <tt>totalAccountTotalAmount：BigDecimal:账户余额</tt><br>
	 *         <tt>totalAccountAvailable：BigDecimal:可用余额</tt><br>
	 *         <tt>totalAccountFreezeAmount：BigDecimal:冻结余额</tt><br>
	 *         <tt>totalIncomeAmount：BigDecimal:已得收益</tt><br>
	 */
	public Map<String, Object> findAllCustAccountSum(Map<String, Object> param);

	/**
	 * 用户资金列表
	 *
	 * @author wangjf
	 * @date 2015年4月25日 上午11:28:39
	 * @param param
	 *            <tt>start：int:分页起始页</tt><br>
	 *            <tt>length：int:每页长度</tt><br>
	 *            <tt>nickName：String:用户昵称</tt><br>
	 *            <tt>custName：String:真实姓名</tt><br>
	 *            <tt>credentialsCode：String:证件号码</tt><br>
	 *            <tt>opearteDateBegin：String:操作开始时间</tt><br>
	 *            <tt>opearteDateEnd：String:操作开始时间</tt><br>
	 * @return <tt>id：String:用户ID</tt><br>
	 *         <tt>nickName：String:用户昵称</tt><br>
	 *         <tt>custName：String:真实姓名</tt><br>
	 *         <tt>credentialsCode：String:证件号码</tt><br>
	 *         <tt>accountAvailable：BigDecimal:可用余额</tt><br>
	 *         <tt>accountFreezeAmount：BigDecimal:冻结余额</tt><br>
	 *         <tt>incomeAmount：BigDecimal:已得收益</tt><br>
	 *         <tt>operateDate：Date:最近操作时间</tt><br>
	 */
	public Page<Map<String, Object>> findAllCustAccountList(
			Map<String, Object> param);

	/**
	 * 根据序列取序列编号
	 *
	 * @author wangjf
	 * @date 2015年4月28日 下午2:00:29
	 * @param sequenceName
	 * @return
	 */
	public long findSequenceValueByName(String sequenceName);	
	
	/**
	 * 获取序列
	 *
	 * @author  wangjf
	 * @date    2015年9月9日 下午3:55:42
	 * @param sequenceName
	 * @param size
	 * @return
	 */
	public List<String> findMoreSequenceValueByName(String prefix, String sequenceName, int size);

	/**
	 * 统计用户持有价值
	 *
	 * @author wangjf
	 * @date 2015年4月28日 下午4:15:49
	 * @param params
	 *            <tt>custId： String:客户ID</tt><br>
	 *            <tt>typeName： String:产品类型名称</tt><br>
	 * @return <tt>accountAvailableValue： BigDecimal:可用余额</tt><br>
	 *         <tt>accountTotalValue： BigDecimal:账户余额</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> findAllValueByCustId(Map<String, Object> params);

	/**
	 * 更新账户信息表
	 * **/
	public int updateAccountById(Map<String, Object> paramMap)
			throws SLException;

	/**
	 * 
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findAllAccountFlowSumNew(
			Map<String, Object> param);
	
	/**
	 * 通过客户id查询账户信息
	 * 
	 * @author zhangt
	 * @date   2016年3月2日上午9:47:19
	 * @param param
	 * @return
	 */
	public Map<String, Object> findAccountInfoByCustId(Map<String, Object> param);
}
