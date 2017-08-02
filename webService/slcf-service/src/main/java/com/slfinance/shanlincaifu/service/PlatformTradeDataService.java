package com.slfinance.shanlincaifu.service;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;
/**
 * 
 * <统计平台数据>
 * <功能详细描述>
 * 
 * @author  lzh
 * @version  [版本号, 2017年6月15日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface PlatformTradeDataService {

	/**
	 * 
	 * <每天统计平台数据>
	 * <功能详细描述>
	 *
	 * @return
	 * @throws SLException [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	ResultVo findTradeDataDay() throws SLException;
	
	/**
	 * 
	 * <每月统计平台数据>
	 * <功能详细描述>
	 *
	 * @return
	 * @throws SLException [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	ResultVo findTradeDataMonth() throws SLException;
	
}
