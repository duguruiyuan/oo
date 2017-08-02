package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

public interface GroupService {

	/**
	 * 一键出借入队
	 *
	 * @author  wangjf
	 * @date    2017年7月21日 上午10:37:05
	 * @param params
	 * @return
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
	public ResultVo pushLoanGroup(Map<String, Object> params) throws SLException;
	
	/**
	 * 一键出借出队
	 *
	 * @author  wangjf
	 * @date    2017年7月21日 上午10:37:21
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo popLoanGroup()throws SLException;
	
	/**
	 * 获取可购买金额
	 *
	 * @author  wangjf
	 * @date    2017年7月21日 上午10:49:54
	 * @return
	 * @throws SLException
	 */
	public ResultVo getCanBuyTotalAmount(Map<String, Object> params)throws SLException;
	
	/**
	 * 将处理中的重新入队
	 *
	 * @author  wangjf
	 * @date    2017年7月21日 下午3:44:05
	 * @return
	 * @throws SLException
	 */
	public ResultVo loadLoanGroup();
	
	/**
	 * 允许队列中的数据
	 *
	 * @author  wangjf
	 * @date    2017年7月21日 下午4:35:21
	 * @return
	 */
	public ResultVo runPopGroup();
}
