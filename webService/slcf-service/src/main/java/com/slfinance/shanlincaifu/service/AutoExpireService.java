package com.slfinance.shanlincaifu.service;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**
 * 
 * <红包过期自动更改红包状态>
 * <功能详细描述>
 * 
 * @author  lzh
 * @version  [版本号, 2017年6月29日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface AutoExpireService {
	
	/**
	 * 
	 * <更新过期红包状态>
	 * <功能详细描述>
	 *
	 * @return
	 * @throws SLException [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	ResultVo AutoExpireDay() throws SLException;
}
