/** 
 * @(#)AccountFlowInfoRepository.java 1.0.0 2015年4月25日 上午11:26:06  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.vo.ResultVo;

/**   
 * 自定义账户流水数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月25日 上午11:26:06 $ 
 */
public interface AccountFlowInfoRepositoryCustom {
	
	/**
	 * 用户资金流水列表
	 *
	 * @author  wangjf
	 * @date    2015年4月25日 上午11:32:03
	 * @param param
	        <tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
	  		<tt>nickName：String:用户昵称</tt><br>
	  		<tt>custName：String:真实姓名</tt><br>
	  		<tt>credentialsCode：String:证件号码</tt><br>
	  		<tt>opearteDateBegin：String:操作开始时间</tt><br>
	  		<tt>opearteDateEnd：String:操作开始时间</tt><br>
	  		<tt>tradeType：String:交易类型</tt><br>
	 * @return
	 	    <tt>id：String:用户ID</tt><br>
	  		<tt>nickName：String:用户昵称</tt><br>
	 		<tt>tradeType：String:交易类型</tt><br>
	 		<tt>tradeAmount：BigDecimal:操作金额</tt><br>
	  		<tt>accountAvailable：BigDecimal:可用余额</tt><br>
	  		<tt>accountFreezeAmount：BigDecimal:冻结余额</tt><br>
	 		<tt>bankrollFlowDirection：String:资金流向</tt><br>
	  		<tt>targetNickName：String:对方账户</tt><br>
	  		<tt>operateDate：Date:操作时间</tt><br>
	 */
	public Page<Map<String, Object>> findAllAccountFlowList(Map<String, Object> param);
	
	
	/**
	 * 根据客户ID统计用户受益
	 *
	 * @author  wangjf
	 * @date    2015年4月28日 下午3:49:11
	 * @param param
	 		<tt>custId： String:客户ID</tt><br>
	 		<tt>tradeType： List<String>:交易类型(当需统计多个受益时可为多个)</tt><br>
	 		<tt>tradeDate： String:交易日期（可为空，当为空时统计所有时间收益）</tt><br>
	 * @return
	 */
	public BigDecimal findIncomeByCustId(Map<String, Object> param);
	
	/**
	 * 我的账户-我的投资-活期宝-交易明细
	 *
	 * @author  wangjf
	 * @date    2015年4月28日 下午4:38:49
	 * @param param
	 		<tt>custId： String:客户ID</tt><br>
	 		<tt>tradeType： String:交易类型(可以为空)</tt><br>
	 		<tt>opearteDateBegin：String:操作开始时间(可以为空)</tt><br>
	  		<tt>opearteDateEnd：String:操作开始时间(可以为空)</tt><br>
	  		<tt>productType： String:产品类型(可以为空)</tt><br>
	 * @return
	 		<tt>requestNo： String:请求编号</tt><br>
	 		<tt>tradeDate： String:交易日期</tt><br>
	 		<tt>tradeType： String:交易类型</tt><br>
	 		<tt>tradeAmount： BigDecimal:交易金额</tt><br>
	 */
	public Page<Map<String, Object>> findAllBaoAccountDetailByCustId(Map<String, Object> param);
	
	/**
	 * 获取用户总资产
	 *
	 * @author  wangjf
	 * @date    2015年5月11日 上午10:34:09
	 * @param param
	 		<tt>custId： String:客户ID</tt><br>
	 * @return
	 */
	public BigDecimal findTotalAccountAmount(Map<String, Object> param);
	
	/**
	 * 交易查询
	 *
	 * @author  HuangXiaodong
	 * @date    2015年5月19日 上午11:33:31
	 * @param param
	  		<tt>tradeType： String:交易类型</tt><br>
	  		<tt>tradeDateBegin： Date:交易开始时间</tt><br>
	  		<tt>tradeDateEnd： Date:交易结束时间</tt><br>
	  		<tt>custId:用户id</tt><br>
	 * @return
	 * List<Map<String, Object>>
	 		<tt>tradeAmount:交易金额</tt><br>
	  		<tt>tradeDate： BigDecimal:交易时间</tt><br>
	 */
	public List<Map<String, Object>> findSumTradeAmount(Map<String, Object> param) ;
	
	/**
	 * 获取加入记录
	 * 
	 * @author zhangzs
	 * @date 2015年6月15日 上午10:07:06 
	 * @param paramsMap	
	 * 	  	  <ul>
	 *	     	<li>productName 产品名称{@link java.lang.String}</li>
	 *        </ul>
	 * @return
	 *       </ul>
	 *	     	<li>count 加入记录数 {@link java.math.BigDecimal}</li>
	 *       </ul>
	 * @throws SLException
	 */
	public Map<String,Object> joinedCount(Map<String,Object> paramsMap) throws SLException;

	/**
	 * 查询用户持有份额
	 * 
	 * @author zhangzs
	 * @param 
	 *         <ul>
	 *	     	<li>custId 	         反馈信息用户的id {@link java.lang.String}</li>
	 *         </ul>
	 * @return <ul>
	 *        </ul>
	 * @throws SLException 
	 */
	BigDecimal getShareHoldingAmount(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询用户对产品的持有份额
	 * 
	 * @author zhangzs
	 * @param 
	 *         <ul>
	 *	     	<li>custId			反馈信息用户的id 	{@link java.lang.String}</li>
	 *	     	<li>productList		产品数据			{@link java.util.List}</li>
	 *         </ul>
	 * @return <ul>
	 *        </ul>
	 * @throws SLException 
	 */
	BigDecimal getHoldingAmount(String custId,List<String> productList)throws SLException;
	
	
	/**
	 * 查询合并后的分页记录数据
	 * 
	 * @author zhangzs
	 */
	Page<AccountFlowInfoEntity> getUnionAccountFlowPage(Map<String, Object> params)throws SLException;
	
	/**
	 * 公司资金流水列表
	 *
	 * @author  wangjf
	 * @date    2015年4月25日 上午11:32:03
	 * @param param
	        <tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
	  		<tt>nickName：String:用户昵称</tt><br>
	  		<tt>custName：String:真实姓名</tt><br>
	  		<tt>credentialsCode：String:证件号码</tt><br>
	  		<tt>opearteDateBegin：String:操作开始时间</tt><br>
	  		<tt>opearteDateEnd：String:操作开始时间</tt><br>
	  		<tt>tradeType：String:交易类型</tt><br>
	  		<tt>bankrollFlowDirection：String:资金流向</tt><br>
	  		<tt>companyType：String:公司类型(01：居间人账户， 02：收益账户，03:风险金账户)</tt><br>
	 * @return
	  		iTotalDisplayRecords: 总条数
	 		data:List<Map<String, object>>
	 		Map<String, object>:
	 	    <tt>id：String:用户ID</tt><br>
	  		<tt>nickName：String:昵称</tt><br>
	 		<tt>tradeType：String:交易类型</tt><br>
	 		<tt>tradeAmount：BigDecimal:操作金额</tt><br>
	 		<tt>bankrollFlowDirection：String:资金流向</tt><br>
	  		<tt>targetNickName：String:对方昵称</tt><br>
	  		<tt>targetCustName：String:对方姓名</tt><br>
	  		<tt>targetCredentialsCode：String:对方证件号码</tt><br>
	  		<tt>memo：String:描述</tt><br>
	  		<tt>operateDate：Date:操作时间</tt><br>
	 */
	public Page<Map<String, Object>> findCompanyAccount(Map<String, Object> param);
	
	/**
	 * 汇总公司账户
	 *
	 * @author  wangjf
	 * @date    2015年7月13日 下午3:27:47
	 * @param map
	 * 		<tt>totalTradeAmount： BigDecimal:汇总交易金额</tt><br>
	 * @return
	 */
	public Map<String, Object> findCompanyAccountSum(Map<String, Object> map);
	
	/**
	 * 风险金帐户审核列表
	 * @author zhangt
	 * @param param
	 * @return
	 */
	public ResultVo findAllAuditInfoPage(Map<String, Object> param);
	
	/**
	 * 风险金帐户流水列表
	 * @author zhangt
	 * @param param
	 * @return
	 */
	public Page<Map<String, Object>> findAllAccountFlowPage(Map<String, Object> param);
	
	/**
	 * 风险金帐户流水汇总
	 *
	 * @author  wangjf
	 * @date    2016年1月18日 下午7:21:24
	 * @param param
	 * @return
	 */
	public BigDecimal findSumAccountFlow(Map<String, Object> param);
	
	/**
	 * 交易查询(升级版，含企业借款)
	 * @author  wangjf
	 * @date    2016年1月23日 下午3:19:05
	 * @param param
	  		<tt>tradeType： String:交易类型</tt><br>
	  		<tt>tradeDateBegin： Date:交易开始时间</tt><br>
	  		<tt>tradeDateEnd： Date:交易结束时间</tt><br>
	  		<tt>custId:用户id</tt><br>
	 * @return
	 * List<Map<String, Object>>
	 		<tt>tradeAmount:交易金额</tt><br>
	  		<tt>tradeDate： BigDecimal:交易时间</tt><br>
	 */
	public List<Map<String, Object>> findSumTradeAmountNew(Map<String, Object> param) ;
	
	/**
	 * 我的资金流水
	 * 
	 * @date 2016-03-04
	 * @author zhiwen_feng
	 * @param params
	 * @return ResultVo
	 * @throws SLException
	 */
	public ResultVo queryMyWealthFlow(Map<String, Object> params)throws SLException;
}
