package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**
 * 散标还款服务类
 * 
 * @author Administrator
 *
 */
public interface LoanRepaymentService {
	
	/**
	 * 还款冻结(含充值)(商务还款冻结)
	 *
	 * @author  wangjf
	 * @date    2016年12月2日 下午5:11:09
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo creditFreezeRepayment(Map<String, Object> params) throws SLException;

	/**
	 * 正常还款
	 *
	 * @author  wangjf
	 * @date    2016年12月2日 下午1:20:24
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo normalRepayment(Map<String, Object> params) throws SLException;
	
	/**
	 * 用户还款
	 *
	 * @author  wangjf
	 * @date    2016年12月22日 下午1:56:46
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo userRepayment(Map<String, Object> params) throws SLException;
	
}
