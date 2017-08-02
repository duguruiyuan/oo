/** 
 * @(#)ProductBusinessServiceImpl.java 1.0.0 2015年4月23日 下午3:18:28  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 
package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

/**
 * 
 *  产品业务
 * 
 * @author caoyi
 * @version $Revision:1.0.0, $Date: 2015年04月23日 下午13:56:25 $
 */
public interface ProductBusinessRepositoryCustom{
	
	/** 
     * 查询活期宝、定期宝 今日明细
     */
	public Map<String, Object>  findBaoCurrentDetailInfo(Map<String, Object> map);
	
	/**
     * 查询活期宝当前价值分配
     */
	public Map<String, Object>  findBaoCurrentVauleSum();
	
	/**
     * 查询活期宝分配规则
     */
	public Map<String, Object>  findBaoCurrentVauleSet();
	
	/**
     * 更新活期宝、定期宝计划预留价值
     * 
     * @author  caoyi
	 * @date    2015年8月18日 下午14:03:15
	 * @param param
	 * @return
     */
	public int updateBaoPreVaule(Map<String, Object> map);
	
	/**
     * 更新活期宝开放比例
     */
	public int updateBaoOpenScale(Map<String, Object> map);
	
	/**
     * 更新活期宝预计开放金额
     */
	public int updateBaoOpenValue(Map<String, Object> map);
	
	/**
     * 更新活期宝实际预留价值
     */
	public int updateBaoAlreadyPreValue(Map<String, Object> map);
	
	/**
     * 更新活期宝未开放价值
     */
	public int updateBaoUnOpenVaule(Map<String, Object> map);

	/**
     * 查询债权还款预算
     */
	public Map<String, Object>  loanRepaymentForecast(Map<String, Object> map);
	
	/**
     * 查询产品预计还款明细
     */
	public Page<Map<String, Object>> findLoanRepaymentList(Map<String, Object> map);
	
	/**
     * 获取债权价值预算
     */
	public Map<String, Object>  loanValueForecast(Map<String, Object> map);
	
	/**
     * 更新产品状态
     */
	public int updateProductStatus(Map<String, Object> map);
	
	/**
     * 更新产品详情
     * 参与机构数、参与债权数、累计收益、当前产品可用价值
     */
	public int updateProductDetail(Map<String, Object> map);
	
	/**
     * 更新活期宝分配状态
     */
	public int updateBaoAllotStatus(Map<String, Object> map);
	
	/**
     * 获取累计成交统计
     */
	public Map<String, Object>  findTotalTradetInfo(Map<String, Object> map);
	
	/**
	 * 更新参与机构数、债权数
	 *
	 * @author  wangjf
	 * @date    2015年5月27日 下午3:02:53
	 * @param param
	 * @return
	 */
	public int updateProductDetailDebtSource(Map<String, Object> param);
	
	/**
     * 查询定期宝当前价值分配
     * 
     * @author  caoyi
	 * @date    2015年8月17日 下午15:43:53
	 * @param param
	 * @return
     */
	public List<Map<String, Object>>  findTermCurrentVauleSum();
	
	/**
     * 查询定期宝当前价值分配--map版本
     * 
     * @author  caoyi
	 * @date    2015年8月18日 下午19:28:13
	 * @param param
	 * @return
     */
	public Map<String, Object>  findTermCurrentVauleSum2();
	
	/**
     * 查询定期宝分配规则
     * 
     * @author  caoyi
	 * @date    2015年8月17日 下午20:40:43
	 * @param param
	 * @return
     */
	public List<Map<String, Object>>  findTermCurrentVauleSet();
	
	
	/**
     * 更新定期宝开放比例
     * 
     * @author  caoyi
	 * @date    2015年8月18日 下午14:27:31
	 * @param param
	 * @return
     */
	public int updateTermOpenScale(Map<String, Object> map);
	
	/**
     * 更新定期宝预计开放金额
     * 
     * @author  caoyi
	 * @date    2015年8月18日 下午19:51:11
	 * @param param
	 * @return
     */
	public int updateTermOpenValue(Map<String, Object> map);
	
}
