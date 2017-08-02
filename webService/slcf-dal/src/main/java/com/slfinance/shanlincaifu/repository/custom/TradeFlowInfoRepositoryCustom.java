/** 
 * @(#)TradeFlowInfoRepository.java 1.0.0 2015年4月21日 下午2:04:46  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;

/**   
 * 自定义交易流水数据访问接口
 *  
 * @author  HuangXiaodong
 * @version $Revision:1.0.0, $Date: 2015年4月21日 下午2:04:46 $ 
 */
public interface TradeFlowInfoRepositoryCustom{
	
	/**根据用户id和交易类型汇总所有交易金额*/
	public List<TradeFlowInfoEntity> findTrade(String custId,Date starDate, Date endDate,String tradeType);

	/**
	 * 充值管理--统计
	 *
	 * @author  wangjf
	 * @date    2015年4月25日 上午11:35:54
	 * @param map
	 		<tt>nickName：String:用户昵称</tt><br>
	  		<tt>custName：String:真实姓名</tt><br>
	  		<tt>credentialsCode：String:证件号码</tt><br>
	  		<tt>opearteDateBegin：String:操作开始时间</tt><br>
	  		<tt>opearteDateEnd：String:操作开始时间</tt><br>
	  		<tt>tradeType：String:交易类型</tt><br>
	  		<tt>tradeStatus：String:交易状态</tt><br>
	  		<tt>tradeNo：String:交易编号</tt><br>
	 * @return
	  		<tt>totalTradeAmount：BigDecimal:充值金额汇总</tt><br>
	  		<tt>totalTradeExpenses：BigDecimal:充值手续费汇总</tt><br>
	  		<tt>totalFactAmount：BigDecimal:实际到账金额汇总</tt><br>
	 */
	public Map<String, Object> findAllRechargeSum(Map<String, Object> map);
	
	/**
	 * 充值管理--列表
	 *
	 * @author  wangjf
	 * @date    2015年4月25日 上午11:33:55
	 * @param map
	        <tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
	 		<tt>nickName：String:用户昵称</tt><br>
	  		<tt>custName：String:真实姓名</tt><br>
	  		<tt>credentialsCode：String:证件号码</tt><br>
	  		<tt>opearteDateBegin：String:操作开始时间</tt><br>
	  		<tt>opearteDateEnd：String:操作开始时间</tt><br>
	  		<tt>tradeType：String:交易类型</tt><br>
	  		<tt>tradeStatus：String:交易状态</tt><br>
	  		<tt>tradeNo：String:交易编号</tt><br>
	 * @return
	        <tt>flowId：String:交易过程流水ID</tt><br>
	  		<tt>nickName：String:用户昵称</tt><br>
	  		<tt>tradeNo：String:交易编号</tt><br>
	 		<tt>tradeType：String:交易类型</tt><br>
	 		<tt>bankName：String:充值银行</tt><br>
	 		<tt>tradeAmount：BigDecimal:充值金额</tt><br>
	  		<tt>tradeExpenses：BigDecimal:手续额</tt><br>
	  		<tt>factAmount：BigDecimal:实际到账金额</tt><br>
	 		<tt>tradeStatus：String:交易状态</tt><br>
	  		<tt>operateDate：Date:操作时间</tt><br>
	  		<tt>ipAddress：String:IP地址</tt><br>
	 */
	public Page<Map<String, Object>> findAllRechargeList(Map<String, Object> map);
	
	/**
	 * 充值管理--明细
	 *
	 * @author  wangjf
	 * @date    2015年4月25日 上午11:33:55
	 * @param flowId
	 * @return
	        <tt>id：String:用户ID</tt><br>
	  		<tt>nickName：String:用户昵称</tt><br>
	  		<tt>custName：String:真实姓名</tt><br>
	  		<tt>credentialsCode：String:证件号码</tt><br>
	  		<tt>tradeNo：String:交易编号</tt><br>
	 		<tt>tradeType：String:交易类型</tt><br>
	 		<tt>bankName：String:充值银行</tt><br>
	 		<tt>branchBankName：String:支行名称</tt><br>
	 		<tt>bankCardNo：String:银行卡号</tt><br>
	 		<tt>tradeAmount：BigDecimal:充值金额</tt><br>
	  		<tt>tradeExpenses：BigDecimal:手续额</tt><br>
	  		<tt>factAmount：BigDecimal:实际到账金额</tt><br>
	  		<tt>memo：String:备注</tt><br>
	 		<tt>tradeStatus：String:交易状态</tt><br>
	  		<tt>operateDate：Date:操作时间</tt><br>	
	  		<tt>ipAddress：String:IP地址</tt><br>	
	 */
	public Map<String, Object> findRechargeDetailInfo(String flowId);
	
	/**
	 * 统计充值次数、单日充值金额、单月充值金额
	 *
	 * @author  wangjf
	 * @date    2015年5月30日 下午5:00:23
	 * @param custId
	 * @return
	 */
	public List<Map<String, Object>> countRecharge(String custId);
	
	/**
	 * 数据总览--充值发起/充值成功/充值总额/提现成功/提现总额
	 * 
	 * @author zhangt
	 * @date   2015年12月11日下午2:39:22
	 * @return
	 */
	public List<Map<String, Object>> findTradeFlowBusinessHistory();
	
	/**
	 * 数据总览 --实名认证人数
	 * @return
	 */
	public List<Map<String, Object>> findRealNameHistory();
	
	/**
	 * 线下充值/提现列表
	 * 
	 * @author zhangt
	 * @date   2016年2月24日上午10:53:22
	 * @param param
	 * @return
	 */
	public Page<Map<String, Object>> findWealthTradeFlowList(Map<String, Object> param);
	
	/**
	 * 线下充值/提现详情
	 * 
	 * @author zhangt
	 * @date   2016年2月24日上午11:56:22
	 * @param param
	 * @return
	 */
	public Map<String, Object> queryWealthTradeFlowDetailById(Map<String, Object> param);
		
	/**
	 * 线下提现 财务导出
	 * 
	 * @author zhangt
	 * @date   2016年2月27日上午10:03:31
	 * @param param
	 * @return
	 */
	public Page<Map<String, Object>> queryExportWealthWithDraw(Map<String, Object> param);

	/**
	 * 线下充值列表（客户自己的数据）
	 * 
	 * @author liyy
	 * @date   2016年6月1日
	 * @param param
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>custName        :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode :String:证件号码(可选)</tt><br>
     *      <tt>mobile          :String:手机号（可选）</tt><br>
     *      <tt>auditStatus     :String:审核状态（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
     *      <tt>custId          :String:客户ID</tt><br>
	 * @return
	 *      <tt>tradeFlowId    :String:交易过程ID</tt><br>
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>tradeType      :String:交易类型</tt><br>
     *      <tt>tradeAmount    :String:交易金额</tt><br>
     *      <tt>createUser     :String:创建人</tt><br>
     *      <tt>createDate     :String:创建时间</tt><br>
     *      <tt>auditStatus    :String:审核状态</tt><br>
	 */
	public Page<Map<String, Object>> queryWealthRechargeListByCustId(
			Map<String, Object> param);

	/**
	 * 线下充值，有待审核的数据
	 * 取大于waitingAuditDate且小于等于now的所有待审核数据条数
	 * @param lastDate Date
	 * @param nowDate Date
	 */
	public int countOffLineRechargeData(Date lastDate, Date nowDate);
	
	/**
	 * 附属银行卡，有待审核的数据
	 * 取大于waitingAuditDate且小于等于now的所有待审核数据条数
	 * @param lastDate Date
	 * @param nowDate Date
	 */
	public int countOffLineCardData(Date lastDate, Date nowDate);
	
	/**
	 * 查询充值记录
	 * 
	 * @author zhangt
	 * @date 2016年12月2日下午1:49:57
	 * @param params
	 * @return
	 */
	public Page<Map<String, Object>> findRechargeList(Map<String, Object> params);
}
