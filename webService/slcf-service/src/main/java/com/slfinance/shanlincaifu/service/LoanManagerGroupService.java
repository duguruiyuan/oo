package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

public interface LoanManagerGroupService {

	/**
	 * 一键出借交易详情页
	 *
	 * @author  guoyk
	 * @date    2017-07-21 10:38:28
	 * @return
	 *      <tt>totalcount        :String:标的总个数</tt><br>
     *      <tt>totalAmount       :String:标的总金额</tt><br>
     *      <tt>loanTermMapList   :String:List<Map<String,Object>>:期限集合</tt><br>
     *      <tt>Map<String,Object>:String:</tt><br>
     *      <tt>loanTerm          :String:期限</tt><br>
     *      <tt>loanUnit          :String:期限单位</tt><br>
     *      <tt>minYearRate       :String:最小年化利率</tt><br>
     *      <tt>maxYearRate       :String:最大年化利率</tt><br>
     *      <tt>minInvestAmount   :String:起投金额</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryTradeInfoByOneStepInvest(Map<String, Object> params)  throws SLException;
	/**
	 * 查询借款期限和单位
	 *
	 * @author  fengyl
	 * @date    2017-07-22 
	 * @return
     *      <tt>loanTerm          :String:期限</tt><br>
     *      <tt>loanUnit          :String:期限单位</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryLoanTermAndLoanUnit(Map<String, Object> params)  throws SLException;
	
	/**
	 * 一键出借交易详情页-查询标的总金额和数量
	 *
	 * @author  guoyk
	 * @date    2017-07-21 10:38:28
	 * @param params
     *      <tt>loanTerm     :String:期限</tt><br>
     *      <tt>loanUnit     :String:期限单位</tt><br>
     *      <tt>transferType :String:转让类型</tt><br>
     *      <tt>repaymentType:String:还款类型</tt><br>
	 * @return
	 *      <tt>totalAmount		  :String:标的总金额</tt><br>
     *      <tt>totalcount 	      :String:标的总个数</tt><br>
	 *      <tt>minYearRate       :String:最小年化利率</tt><br>
     *      <tt>maxYearRate       :String:最大年化利率</tt><br>
     *      <tt>minInvestAmount   :String:起投金额</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "loanTerm", required = true, requiredMessage = "期限属性不能为空"),
			@Rule(name = "loanUnit", required = true, requiredMessage = "期限单位不能为空"),
			@Rule(name = "transferType", required = true, requiredMessage = "转让属性不能为空"),
			@Rule(name = "repaymentType", required = true, requiredMessage = "还款方式不能为空")
	})
	public ResultVo queryTotalCountAndTotalAmountByOneStepInvest(Map<String, Object> params)  throws SLException;
	
	/**
	 * 优选项目一键出借
	 * @author  liyy
	 * @date    2017-07-15 14:38:28
	 * @return
     *      ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "custId不能为空"), 
			@Rule(name = "loanTerm", required = true, requiredMessage = "期限属性不能为空"),
			@Rule(name = "loanUnit", required = true, requiredMessage = "期限单位不能为空"),
			@Rule(name = "transferType", required = true, requiredMessage = "转让属性不能为空"),
			@Rule(name = "repaymentType", required = true, requiredMessage = "还款方式不能为空"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "交易金额不能为空")
	})
	public ResultVo buyLoanForGroup(Map<String, Object> params) throws SLException;
	
	/**
	 * 优选项目-我的出借
	 *
	 * @author  guoyk
	 * @date    2017-07-21 10:38:28
	 * @param params
     *      <tt>custId        :String:客户ID</tt><br>
     *      <tt>disperseStatus:String:散标状态</tt><br>
     *      <tt>start         :String:起始值</tt><br>
     *      <tt>length        :String:长度</tt><br>
	 * @return List<Map<String, Object>>
     *      <tt>disperseId    :String:散标主键</tt><br>
     *      <tt>disperseType  :String:散标名称</tt><br>
     *      <tt>creditNo      :String:债权编号</tt><br>
     *      <tt>yearRate      :String:年化收益率</tt><br>
     *      <tt>investlAmount :String:投资金额</tt><br>
     *      <tt>typeTerm      :String:项目期限(月)</tt><br>
     *      <tt>investScale   :String:已投百分比</tt><br>
     *      <tt>disperseStatus:String:散标状态</tt><br>
     *      <tt>disperseDate  :String:投资时间</tt><br>
     *      <tt>groupBatchNo  :String:一键出借批次号</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryMyDisperseListByGroup(Map<String, Object> params) throws SLException;
	
	/**
	 * 优选项目-我的出借-详情
	 *
	 * @author  guoyk
	 * @date    2017-07-21 10:38:28
	 * @param params
     *      <tt>groupBatchNo     :String:一键出借批次号</tt><br>
     *      <tt>custId     		 :String:客户id</tt><br>
     *      <tt>start            :String:起始值</tt><br>
     *      <tt>length           :String:长度</tt><br>
	 * @return
	 *     ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryMyDisperseListDetailByGroup(Map<String, Object> params)throws SLException;
}
