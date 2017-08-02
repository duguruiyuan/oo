package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**
 * @author Administrator
 *
 */
public interface LoanJobService {
	
	/***
	 * 
	 * <b>方法名：</b>：巨涟自动审核并发布<br>
	 * <b>功能说明：</b>TODO<br>
	 * @author <font color='blue'>张祥</font> 
	 * @date  2017年7月4日 下午1:47:45
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoAuditLoanJL(Map<String, Object> params) throws SLException;
	
	/**
	 * 意真自动审核通过并发布项目
	 * 
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoAuditLoanYZ(Map<String, Object> params) throws SLException;
	
	/**
	 * 拿米自动审核通过并发布项目
	 * 
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoAuditLoanNM(Map<String, Object> params) throws SLException;
	
	/**
	 * 根据公司名称自动审核通过并发布项目
	 * 
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoAuditLoan4Company(Map<String, Object> params) throws SLException;

	/**
	 * 自动发布项目
	 * 
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoPublishLoan(Map<String, Object> params) throws SLException;
	
	/**
	 * 流标
	 * 
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoUnReleaseLoan(Map<String, Object> params) throws SLException;
	
	/**
	 * 新开事物发布优选计划
	 * @author lyy
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo publishLoan(Map<String, Object> params)throws SLException;
	
	/**
	 * 定时还款
	 *
	 * @author  wangjf
	 * @date    2016年12月2日 下午5:19:08
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoRepaymentLoan(Map<String, Object> params)throws SLException;
	
	/**
	 * 统计业务员业绩
	 *
	 *  注：主要统计散标、债权转让业绩情况，此处统计为最新佣金方案
	 *
	 * @author  wangjf
	 * @date    2016年12月5日 下午6:40:49
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo caclCommission(Map<String, Object> params)throws SLException;
	
	/**
	 * 业务员业绩到期处理
	 *
	 * @author  wangjf
	 * @date    2016年12月8日 下午2:17:32
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo commissionWithdraw(Map<String, Object> params)throws SLException;
	
	/**
	 * Job项目定时放款确认
	 *
	 * @author  wangjf
	 * @date    2016年12月17日 下午12:38:04
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoGrantConfirm(Map<String, Object> params) throws SLException;
	
	/**
	 * Job根据资产公司名称对项目定时放款确认
	 *
	 * @author  zhangze
	 * @date    2017年7月6日 下午9:38:04
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoGrantConfirm4Company(Map<String, Object> params) throws SLException;
	
	
	/**
	 * Job意真项目定时放款确认
	 *
	 * @author  zhangze
	 * @date    2017年6月19日 下午12:38:04
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoGrantConfirmYZ(Map<String, Object> params) throws SLException;
	
	/***
	 * 
	 * <b>方法名：</b>：autoGrantConfirmJL<br>
	 * <b>功能说明：</b>巨涟定时放款确认<br>
	 * @author <font color='blue'>张祥</font> 
	 * @date  2017年7月4日 下午2:32:48
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoGrantConfirmJL(Map<String, Object> params) throws SLException;
	
	/**
	 * Job项目定时放款
	 *
	 * @author  wangjf
	 * @date    2016年12月17日 下午3:38:28
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoGrant(Map<String, Object> params) throws SLException;
	
	/**
	 * Job根据资产公司名称对项目定时放款
	 *
	 * @author  zhangze
	 * @date    2017年6月12日 下午3:38:28
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoGrant4Company(Map<String, Object> params) throws SLException;
	
	/**
	 * Job意真项目定时放款
	 *
	 * @author  zhangze
	 * @date    2017年6月12日 下午3:38:28
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoGrantYZ(Map<String, Object> params) throws SLException;
	
	/***
	 * 
	 * <b>方法名：</b>：autoGrantJL<br>
	 * <b>功能说明：</b>巨涟自动放款<br>
	 * @author <font color='blue'>张祥</font> 
	 * @date  2017年7月4日 下午2:03:50
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoGrantJL(Map<String, Object> params) throws SLException;
	
	/**
	 * Job项目定时撤销转让
	 *
	 * @author  wangjf
	 * @date    2016年12月29日 上午9:12:53
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoCancelLoan(Map<String, Object> params) throws SLException;
}
