package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**
 * 特殊用户标的设置有效期
 *
 * @author  mali
 * @version $Revision:1.0.0, $Date: 2017年7月21日 下午11:16:12 $
 */
public interface SpecialUsersUrlService {

	/**
	 * 
	 * <生成链接>
	 * <功能详细描述>
	 *
	 * @param param
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	 @Rules(rules = {
				@Rule(name = "url", required = true, requiredMessage = "链接地址不能为空")
		})
    ResultVo genericExpire(Map<String, Object> param);
    /**
     * 
     * <查询token是否失效>
     * <功能详细描述>
     *
     * @param param
     * @return [参数说明]
     * @return ResultVo [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    @Rules(rules = {
			@Rule(name = "token", required = true, requiredMessage = "token不能为空")
	})
    ResultVo queryByToken(Map<String, Object> param);
}
