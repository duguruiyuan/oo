package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**
 * 商户还款服务
 * 
 * @author zhangt
 * @date 2016年11月30日上午9:38:44
 */
public interface ThirdCompanyRepaymentService {
	
	/**
	 * 登录
	 * @author zhangt
	 * @date 2016年11月30日上午9:40:55
	 * @param param
	 *      <tt>loginName:String: 昵称</tt><br>
     *      <tt>password :String:密码 </tt><br>
	 * @return
	 */
//	@Rules(rules = { 
//			@Rule(name = "loginName", required = true, requiredMessage = "昵称不能为空!")
//	})
	public ResultVo login(Map<String, Object> param);

	/**
	 * 查询待还款列表
	 * 
	 * @author zhangt
	 * @date 2016年11月30日上午9:48:15
	 * @param param
	 *      <tt>projectName       :String:项目名称</tt><br>
     *      <tt>startRepaymentDate:String:到期日期</tt><br>
     *      <tt>endRepaymentDate  :String:到期日期</tt><br>
     *      <tt>repaymentStatus   :String:还款状态</tt><br>
     *      <tt>custId            :String:登录人Id</tt><br>
     *      <tt>start   		  :String:起始位置</tt><br>
     *      <tt>length   		  :String:长度</tt><br>
	 * @return
	 * 		<tt>availableAccAmount  :String:账户可用余额</tt><br>
	 *      <tt>projectName         :String:项目名称</tt><br>
     *      <tt>currentTerm         :String:当期期数</tt><br>
     *      <tt>sumTerm             :String:总期数</tt><br>
     *      <tt>repaymentDate       :String:到期日期</tt><br>
     *      <tt>repaymentPI         :String:到期本息</tt><br>
     *      <tt>overdueExpense      :String:逾期罚息</tt><br>
     *      <tt>manageExpense       :String:管理费</tt><br>
     *      <tt>serviceExpense      :String:服务费</tt><br>
     *      <tt>repaymentTotalAmount:String:应还总额</tt><br>
     *      <tt>repaymentStatus     :String:还款状态</tt><br>
     *      <tt>repaymentPlanId     :String:还款计划id</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字"),
			@Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字"),
			@Rule(name = "userId", required = true, requiredMessage = "登录人Id不能为空!")
	})
	public ResultVo queryRepaymentList(Map<String, Object> param);
	
	/**
	 * 项目详情
	 * 
	 * @author zhangt
	 * @date 2016年12月1日下午6:15:04
	 * @param param
	 * @return
	 * @throws SLException
	 */
	public ResultVo queryProjectDetailInfo(Map<String, Object> param) throws SLException;
	
	/**
	 * 正常还款
	 * @author zhangt
	 * @date 2016年11月30日下午3:11:30
	 * @param param
	 * 		<tt>repaymentPlanId     :String:还款计划id</tt><br>
	 *      <tt>repaymentTotalAmount:String:应还总额</tt><br>
	 *      <tt>userId              :String:登录人Id</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "repaymentPlanId", required = true, requiredMessage = "还款计划id不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "登录人Id不能为空!")
	})
	public ResultVo normalRepayment(Map<String, Object> param) throws SLException;
}
