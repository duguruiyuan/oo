/** 
 * @(#)RepaymentService.java 1.0.0 2015年5月1日 下午4:19:10  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**   
 * 还款服务接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月1日 下午4:19:10 $ 
 */
public interface RepaymentService {

	/**
	 * 还款计算
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午4:20:43
	 * @param param
	 		<tt>expectRepaymentDate： String:还款日期（yyyyMMdd）</tt><br>
	 * @return
	 * @throws SLException
	 */
	public ResultVo repaymentJob(Map<String, Object> param) throws SLException;
}
