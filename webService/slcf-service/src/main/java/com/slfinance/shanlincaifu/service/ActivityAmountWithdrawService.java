package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;
/**
 * 
 * <活动金提现相关>
 * <功能详细描述>
 * 
 * @author  yangcheng
 * @version  [版本号, 2017年6月28日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface ActivityAmountWithdrawService {
	/**
	 * 
	 * <提现申请>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空"),
			@Rule(name = "amount", required = true, requiredMessage = "提现金额不能为空")
			})
	ResultVo saveWithdrawApply(Map<String, Object> params)throws SLException;
	/**
	 * 
	 * <提现审核>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	@Rules(rules = {
			@Rule(name = "id", required = true, requiredMessage = "审核ID不能为空"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空")
			})
	ResultVo saveWithdrawAudit(Map<String, Object> params)throws SLException;
	/**
	 * 
	 * <批量审核>
	 * <功能详细描述>
	 *
	 * @param paramsMap
	 * @return
	 * @throws SLException [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	@Rules(rules = {
			@Rule(name = "ids", required = true, requiredMessage = "审核ID不能为空"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空")
			})
	ResultVo saveBatchWithdrawAudit(Map<String,Object> paramsMap)throws SLException;
	/**
	 * 
	 * <活动金提现——提现审核管理列表>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "起始页数不能为空"),
			@Rule(name = "length", required = true, requiredMessage = "显示条数不能为空")
			})
	Map<String, Object> findAllWithdrawAuditList(Map<String, Object> params);

}
