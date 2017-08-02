package com.slfinance.shanlincaifu.service;

import java.text.ParseException;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**
 * 
 * <红包管理接口>
 * <红包管理>
 * 
 * @author  lzh
 * @version  [版本号, 2017年6月27日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface RedPacketManagementService {

	/**
	 * 
	 * <查询红包信息>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "开始页数不能为空", digist = true, digistMessage = "开始页数必须为整数"),
			@Rule(name = "length", required = true, requiredMessage = "结束页数不能为空", digist = true, digistMessage = "结束页数必须为整数")
	})
	Map<String, Object> queryRedPacketList(Map<String, Object> params);
	
	/**
	 * 
	 * <保存红包信息>
	 * <新增红包>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @throws SLException 
	 * @see [类、类#方法、类#成员]
	 */
	@Rules(rules = {
			@Rule(name = "awardName", required = true, requiredMessage = "红包名称不能为空"),
			@Rule(name = "awardType", required = true, requiredMessage = "红包类型不能为空"),
			@Rule(name = "grantAmount", required = true, requiredMessage = "红包金额不能为空"),
			@Rule(name = "startTime", required = true, requiredMessage = "有效期开始时间不能为空"),
			@Rule(name = "deadlineTime", required = true, requiredMessage = "有效期结束时间不能为空"),
	})
	ResultVo saveRedPacketInfo(Map<String, Object> params) throws SLException;
	
	/**
	 * 
	 * <查询红包使用详情>
	 * <红包使用详情>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @throws ParseException 
	 * @see [类、类#方法、类#成员]
	 */
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "开始页数不能为空", digist = true, digistMessage = "开始页数必须为整数"),
			@Rule(name = "length", required = true, requiredMessage = "结束页数不能为空", digist = true, digistMessage = "结束页数必须为整数"),
			@Rule(name = "awardId", required = true, requiredMessage = "红包id不能为空"),
			@Rule(name = "awardType", required = true, requiredMessage = "红包类型不能为空")
	})
	Map<String, Object> queryRedPacketUseDetails(Map<String, Object> params) throws ParseException;
	
	/**
	 * 
	 * <更新红包状态>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	@Rules(rules = {
			@Rule(name = "awardStatus", required = true, requiredMessage = "红包状态"),
			@Rule(name = "awardId", required = true, requiredMessage = "红包id不能为空")
	})
	ResultVo updateRedPacketStatus(Map<String, Object> params);
	
	/**
	 * 
	 * <导入用户红包信息>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @throws ParseException 
	 * @see [类、类#方法、类#成员]
	 */
	ResultVo importCustRedPacketInfo(Map<String, Object> params) throws ParseException;
}
