package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

public interface LoanReserveService {

	/**
	 * 已预约金额与加入人数
	 *
	 * @author  wangjf
	 * @date    2017-06-17 11:41:29
	 * @param params
     *      <tt>currentDate:String:当前时间</tt><br>
	 * @return
     *      <tt>totalReserveAmount:String:已预约金额</tt><br>
     *      <tt>totalReservePeople:String:已加入人数</tt><br>
     *      <tt>minLoanTerm:String:最低历史平均年化收益率</tt><br>
     *      <tt>maxLoanTerm:String:最高历史平均年化收益率</tt><br>
     *      <tt>investMinAmount:String:起投金额</tt><br>
     *      <tt>increaseAmount:String:递增金额</tt><br>
     *      <tt>seatTerm:String:是否可转让</tt><br>
     *      <tt>repaymentMethod:String:还款方式</tt><br>
     *      
	 * @throws SLException
	 */
	public ResultVo queryJoinReserve(Map<String, Object> params)  throws SLException;

//	/**
//	 * 查询优先投借款信息列表
//	 *
//	 * @author  wangjf
//	 * @date    2017-06-17 11:41:29
//	 * @param params
//     *      <tt>start :String:起始值</tt><br>
//     *      <tt>length:String:</tt><br>
//	 * @return
//     *      <tt>iTotalDisplayRecords:String: 总条数</tt><br>
//     *      <tt>data                :String:List<Map<String,Object>></tt><br>
//     *      <tt>reserveStartDate              :String:预约日期</tt><br>
//     *      <tt>reserveAmount            :String:预约金额</tt><br>
//     *      <tt>remainderAmount     :String:待投金额（元）</tt><br>
//     *      <tt>alreadyInvestAmount          :String:已投金额（元）</tt><br>
//     *      <tt>reserveStatus       :String:状态</tt><br>
//     *      <tt>operate     :String:操作</tt><br>
//	 * @throws SLException
//	 */
//	@Rules(rules = {
//			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"),
//			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数") })
//	public ResultVo queryReserveLoanList(Map<String, Object> params)  throws SLException;

	/**
	 * 可用余额与待投金额
	 *
	 * @author  wangjf
	 * @date    2017-06-17 11:41:29
	 * @param params
     *      <tt>custId:String:客户ID</tt><br>
	 * @return
     *      <tt>accountAvailableAmount:String:可用余额</tt><br>
     *      <tt>remainderReserveAmount:String:待投金额</tt><br>
     *      <tt>investMinAmount:String:起投金额</tt><br>
     *      <tt>increaseAmount:String:递增金额</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage =  "客户ID不能为空")
	})
	public ResultVo queryUserReserve(Map<String, Object> params)  throws SLException;

	/**
	 * 直投预约/追加预约
	 *
	 * @author  wangjf
	 * @date    2017-06-17 11:41:29
	 * @param params
     *      <tt>custId     :String:客户ID</tt><br>
     *      <tt>tradeAmount:String:预约金额</tt><br>
     *      <tt>tradePass  :String:交易密码</tt><br>
     *      <tt>channelNo  :String:渠道号（可以为空）</tt><br>
     *      <tt>meId       :String:设备ID（可以为空）</tt><br>
     *      <tt>meVersion  :String:设备版本号（可以为空）</tt><br>
     *      <tt>appSource  :String:设备来源（可以为空）</tt><br>
     *      <tt>ipAddress  :String:ip地址（可以为空）</tt><br>
	 * @return
     *      ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空"), 
			@Rule(name = "tradeAmount", required = true, requiredMessage = "投资金额不能为空!", number = true, numberMessage = "投资金额必须是数字！", digist =true, digistMessage = "投资金额必须是整数！"),
			@Rule(name = "tradePass", required = true, requiredMessage = "交易密码不能为空!")
	})

	public ResultVo joinReserve(Map<String, Object> params)  throws SLException;

	/**
	 * 预约投资汇总
	 *
	 * @author  wangjf
	 * @date    2017-06-17 11:41:29
	 * @param params
     *      <tt>custId:String:客户ID</tt><br>
	 * @return
     *      <tt>earnTotalAmount   :String:已赚金额</tt><br>
     *      <tt>exceptTotalAmount :String:待收收益</tt><br>
     *      <tt>investTotalAmount :String:在投金额</tt><br>
     *      <tt>reserveTotalAmount:String:预约总金额</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage =  "客户ID不能为空")
	})
	public ResultVo queryMyReserveIncome(Map<String, Object> params)  throws SLException;

	/**
	 * 预约投资列表查询
	 *
	 * @author  wangjf
	 * @date    2017-06-17 11:41:29
	 * @param params
     *      <tt>custId          :String:客户ID</tt><br>
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>reserveStartDate:String:预约起始日期（可选）</tt><br>
     *      <tt>reserveEndDate  :String:预约到期日期（可选）</tt><br>
	 * @return
     *      <tt>reserveId          :String:预约ID</tt><br>
     *      <tt>reserveDate        :String:预约日期</tt><br>
     *      <tt>reserveAmount      :String:预约金额</tt><br>
     *      <tt>remainderAmount    :String:待投金额</tt><br>
     *      <tt>alreadyInvestAmount:String:已投金额</tt><br>
     *      <tt>reserveStatus      :String:预约状态</tt><br>
     *      <tt>minLoanTerm:String:最低历史平均年化收益率</tt><br>
     *      <tt>maxLoanTerm:String:最高历史平均年化收益率</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"),
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空") })
	public ResultVo queryMyReserveList(Map<String, Object> params)  throws SLException;

	/**
	 * 预约撤销
	 *
	 * @author  wangjf
	 * @date    2017-06-17 11:41:29
	 * @param params
     *      <tt>reserveId:String:预约ID</tt><br>
     *      <tt>custId   :String:客户ID</tt><br>
     *      <tt>tradePass:String:交易密码</tt><br>
     *      <tt>channelNo:String:渠道号（可以为空）</tt><br>
     *      <tt>meId     :String:设备ID（可以为空）</tt><br>
     *      <tt>meVersion:String:设备版本号（可以为空）</tt><br>
     *      <tt>appSource:String:设备来源（可以为空）</tt><br>
     *      <tt>ipAddress:String:ip地址（可以为空）</tt><br>
	 * @return
     *      ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空"), 
			@Rule(name = "tradePass", required = true, requiredMessage = "交易密码不能为空!")
	})
	public ResultVo cancelReserve(Map<String, Object> params)  throws SLException;
	/**
	 * 预约撤销job
	 *
	 * @author  wangjf
	 * @date    2017-06-17 11:41:29
	 * @param params
     *      <tt>reserveId:String:预约ID</tt><br>
     *      <tt>custId   :String:客户ID</tt><br>
     *      <tt>tradePass:String:交易密码</tt><br>
     *      <tt>channelNo:String:渠道号（可以为空）</tt><br>
     *      <tt>meId     :String:设备ID（可以为空）</tt><br>
     *      <tt>meVersion:String:设备版本号（可以为空）</tt><br>
     *      <tt>appSource:String:设备来源（可以为空）</tt><br>
     *      <tt>ipAddress:String:ip地址（可以为空）</tt><br>
	 * @return
     *      ResultVo
	 * @throws SLException
	 */
	public ResultVo cancelReserveByJob(Map<String, Object> params)  throws SLException;
	/**
	 * 查询是否有排队中的优先投预约数据
	 *
	 * @author  fengyl
	 * @date    2017-07-21 
	 * @param params
     *      <tt>custId   :String:客户ID</tt><br>
	 * @return
     *      ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空")
	})
	public ResultVo queryIsJoinReserve(Map<String, Object> params)  throws SLException;
	
	/**
	 * 预约金额查询
	 * @author zhangyb
	 * @params
	 *  	<tt>loanNo:String:借款编号</tt><br>
	 *  	<tt>requestTime:String:请求时间</tt><br>
	 *  	<tt>companyName:String:机构名称</tt><br>
	 *  	<tt>tradeCode:String:交易编号</tt><br>
	 *  	<tt>sign:String:签名</tt><br>
	 */
	public ResultVo queryRemainAmount(Map<String, Object> params);
	
	
}
