/** 
 * @(#)CustRecommendInfoRepositoryCustom.java 1.0.0 2015年8月24日 下午4:09:54  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustRecommendInfoEntity;

/**   
 * 自定义推荐人客户关系接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月24日 下午4:09:54 $ 
 */
public interface CustRecommendInfoRepositoryCustom {
	
	/**
	 * 查询待结息推荐人
	 *
	 * @author  wangjf
	 * @date    2015年8月24日 下午4:12:10
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> findWaitSettlement(Map<String,Object> params);
	
	/**
	 * 汇总待结息推荐人
	 *
	 * @author  wangjf
	 * @date    2015年8月24日 下午4:12:12
	 * @param params
	 * @return
	 */
	public int countWaitSettlement(Map<String,Object> params);

	/**
	 * 我是业务员-公用-查询客户经理名下所有客户
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param :Map<br>
     *      <tt>custManagerId:String:客户经理主键(可选)</tt><br>
     *      <tt>custName     :String:用户姓名(可选)</tt><br>
	 * @return List<Map<String, Object>><br>
	 *      		<tt>custId         :String:客户ID</tt><br>
	 *      		<tt>custName:String:用户名</tt><br>
	 *      		<tt>credentialsCode:String:证件号码</tt><br>
	 *      		<tt>mobile         :String:手机</tt><br>
	 *      		<tt>registerDate   :String:注册时间</tt><br>
	 *      		<tt>custManagerId  :String:客户经理Id</tt><br>  
     * @throws SLException
     */
	public List<Map<String, Object>> queryCustNameByManager(
			Map<String, Object> param);

	/**
	 * 我是业务员-公用-查询业务员信息
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>custName       :String:用户姓名(可选)</tt><br>
     *      <tt>credentialsCode:String:证件号码（可以为空）</tt><br>
     *      <tt>mobile:String:手机号（可以为空）</tt><br>
	 * @return List<Map<String, Object>><br>
     *      		<tt>custId         :String:客户ID</tt><br>
     *      		<tt>custName       :String:用户名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     * @throws SLException
     */
	public List<Map<String, Object>> queryCustManager(Map<String, Object> param);

	/**
	 * 我是业务员-公用-查询客户下面附属银行
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>custId:String:客户ID</tt><br>
	 * @return List<Map<String, Object>><br>
     *      		<tt>bankId  :String:银行主键</tt><br>
     *      		<tt>bankName:String:bankName</tt><br>
     * @throws SLException
     */
	public List<Map<String, Object>> queryCustBankByCustId(
			Map<String, Object> param);
	
	/**
	 * 查询所有业务员信息
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryCustName(Map<String, Object> params)throws SLException;

	/** 批量更新 */
	public int batchUpdateRecommend(
			List<CustRecommendInfoEntity> list);
	
	/**
	 * 查询推荐客户数据
	 * 
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryTransferByQuiltCustId(Map<String, Object> params);
}
