package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;
/**
 * 
 * <平台数据接口>
 * <功能详细描述>
 * 
 * @author  lzh
 * @version  [版本号, 2017年6月15日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface PlatformDataService {

	/**
	 * 
	 * <查询平台交易数据>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	ResultVo queryPlatformData(Map<String, Object> param);
	
	/**
	 * 
	 * <查询用户数据>
	 * <功能详细描述>
	 *
	 * @param param
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	@Rules(rules = { 
			@Rule(name = "custType", required = true, requiredMessage = "用户类型不能为空!")
	})
	ResultVo queryCustData(Map<String, Object> param);
}
