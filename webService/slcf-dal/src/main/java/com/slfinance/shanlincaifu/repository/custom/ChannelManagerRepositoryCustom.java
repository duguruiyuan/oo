package com.slfinance.shanlincaifu.repository.custom;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;

/**
 * 
 * 渠道管理访问接口
 * 
 * 
 * @author fengyl
 * @version $Revision:1.0.0, $Date: 2017年6月10日
 */
public interface ChannelManagerRepositoryCustom {
	/**
	 * 渠道类型管理列表查询
	 * 
	 * @author fengyl
	 * @date 2017年6月10日
	 * @param Map
	 *         <tt>deptName           :String:部门名称</tt><br>
	 *         <tt>recordStatus       :String:状态</tt><br>
	 *         <tt>channelName        :String:渠道名称</tt><br>
	 *         <tt>channelNo          :String:渠道编号</tt><br>
	 * @return ResultVo <tt>deptName  :String:部门</tt><br>
	 *         <tt>deptManager        :String:部门负责人</tt><br>
	 *         <tt>channelName        :String:渠道名称</tt><br>
	 *         <tt>channelSource      :String:终端</tt><br>
	 *         <tt>channelNo  :String:渠道编号</tt><br>
	 * @throws SLException
	 */
	Page<Map<String, Object>> queryChannelInfoList(Map<String, Object> param);
	/**
	 * 渠道统计列表查询
	 * 
	 * @date 2017年月10日
	 * @author fengyl
	 * @param Map
	 * 	          <tt>start           :String:起始值</tt><br>
     *            <tt>length          :String:长度</tt><br>
	 *            <tt>deptName           :String:部门名称</tt><br>
	 *            <tt>deptManager        :String:部门负责人</tt><br>
	 *            <tt>channelName        :String:渠道名称</tt><br>
	 *            <tt>channelSource      :String:终端</tt><br>
	 *            <tt>channelNo          :String:渠道编号</tt><br>
	 * @return ResultVo.data <tt>deptName     :String:部门名称</tt><br>
	 *         <tt>deptManager        :String:部门负责人</tt><br>
	 *         <tt>channelNname       :String:渠道名称</tt><br>
	 *         <tt>channelSource      :String:终端</tt><br>
	 *         <tt>registerCount      :String:注册人数</tt><br>
	 *         <tt>realNameCount      :String:实名人数</tt><br>
	 *         <tt>rechargeCount      :String:充值人数</tt><br>
	 *         <tt>rechargeAmount     :String:充值金额(元)</tt><br>
	 *         <tt>depositCount       :String:提现人数</tt><br>
	 *         <tt>depositAmount      :String:提现金额(元)</tt><br>
	 *         <tt>investCount        :String:投资人数</tt><br>
	 *         <tt>investAmount       :String:投资金额</tt><br>
	 *         <tt>yearInvestAmount   :String:年化投资金额(元)</tt><br>
	 */
	Page<Map<String, Object>> queryChannelCountList(Map<String, Object> param);
	/**
	 * 投资金额明细标列表查询
	 * 
	 * @author fengyl
	 * @date 2017-6-10
	 * @param params
	 * 	          <tt>start           :String:起始值</tt><br>
     *            <tt>length          :String:长度</tt><br>
	 *            <tt>investStartDate:String:投资日期-区间头(可以为空)</tt><br>
	 *            <tt>investEndDate  :String:投资日期-区间末(可以为空)</tt><br>
	 * @return <tt>data             :String:List<Map<String,Object>></tt><br>
	 *         <tt>custName         :String:客户姓名</tt><br>
	 *         <tt>mobile           :String:手机号</tt><br>
	 *         <tt>investAmount     :String:投资金额</tt><br>
	 *         <tt>loanType         :String:借款类型</tt><br>
	 *         <tt>seatTerm         :String:是否可流转</tt><br>
	 *         <tt>loanTerm         :String:标的期限</tt><br>
	 *         <tt>yearIrr          :String:利率 </tt><br>
	 *         <tt>repaymentMethod  :String:还款方式</tt><br>
	 *         <tt>investDate       :String:投资日期"</tt><br>
	 * @throws SLException
	 */
	Page<Map<String, Object>> queryInvestAmountDetail(Map<String, Object> param);
	/**
	 * 渠道统计列表查询
	 * 
	 * @date 2017年月10日
	 * @author fengyl
	 * @param Map
	 * 	          <tt>start           :String:起始值</tt><br>
     *            <tt>length          :String:长度</tt><br>
	 *            <tt>deptName           :String:部门名称</tt><br>
	 *            <tt>deptManager        :String:部门负责人</tt><br>
	 *            <tt>channelName        :String:渠道名称</tt><br>
	 *            <tt>channelSource      :String:终端</tt><br>
	 *            <tt>channelNo          :String:渠道编号</tt><br>
	 * @return ResultVo.data <tt>deptName     :String:部门名称</tt><br>
	 *         <tt>registerCountTotal      :String:注册人数</tt><br>
	 *         <tt>rechargeAmountTotal     :String:充值金额</tt><br>
	 *         <tt>depositAmountTotal      :String:提现金额</tt><br>
	 *         <tt>investAmountTotal       :String:投资金额</tt><br>
	 */
	Map<String, Object> queryToatlChannelInfo(Map<String, Object> param);
}
