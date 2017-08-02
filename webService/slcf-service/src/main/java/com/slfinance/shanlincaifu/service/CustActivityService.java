package com.slfinance.shanlincaifu.service;

import com.slfinance.shanlincaifu.entity.CustActivityInfoEntity;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;
import java.util.Map;

public interface CustActivityService {

	/**
	 * 查询奖励绑卡银行卡客户列表
	 *
	 * @author  wangjf
	 * @date    2016年12月29日 上午11:02:01
	 * @param params
	 * 		<tt>start： String:起始值</tt><br>
	 * 		<tt>length： String:长度</tt><br>
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数") 
	})
	public ResultVo queryAwardBankCardList(Map<String, Object> params);
	
	/**
	 * 查询红包奖励榜
	 *
	 * @author  fengyl
	 * @date    2017年4月27日 
	 * @param params
	 * 		<tt>start： String:起始值</tt><br>
	 * 		<tt>length： String:长度</tt><br>
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数") 
	})
	public ResultVo queryAwardList(Map<String, Object> params);

	/**
	 * 我的红包
	 * @param params
	 * <tt>start： String:起始值</tt><br>
	 *   <tt>length： String:长度</tt><br>
	 * @return
	 */
	@Rules(rules = {
//			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"),
//			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
			@Rule(name = "custId", required = true, requiredMessage = "客户编号不能为空")
	})
  ResultVo redEnvelopeList(Map<String, Object> params);

	/**
	 * 根据红包ID	和客户ID查找活动记录
	 * @param custActivityId 活动id
	 * @param custId 客户id
	 * @return 活动记录
	 */
	Map<String,Object> findRewardByIdAndCustId(String custActivityId, String custId);

	/**
	 * 根据活动id查找活动信息.
	 * @param custActivityId 活动id
	 * @return 活动信息
	 */
	CustActivityInfoEntity findById(String custActivityId);

//	/**
//	 * 查询实时豪礼榜列表
//	 *
//	 * @author fengyl
//	 * @date 2017年6月29日
//	 * @param params
//	 *            <tt>start： String:起始值</tt><br>
//	 *            <tt>length： String:长度</tt><br>
//	 * @return <tt> custName:String: 客户姓名</tt><br>
//	 *         <tt>mobile：String: 手机号</tt><br>
//	 *         <tt>totalYearInvestAmount：String: 累计年化出借金额</tt><br>
//	 *         <tt> currentAward：String: 当前奖品</tt><br>
//	 */
//	@Rules(rules = {
//			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"),
//			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数") })
//	public ResultVo queryActualTimeAwardList(Map<String, Object> params);

}
