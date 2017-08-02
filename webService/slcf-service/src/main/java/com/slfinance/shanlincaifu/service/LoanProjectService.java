package com.slfinance.shanlincaifu.service;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

import java.util.Map;

public interface LoanProjectService {
	/**
	 * 撤销
	 *
	 * @author  张泽
	 * @date    2017年6月9日 下午4:19:03
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo cancel(Map<String, Object> params) throws SLException;
	
	/**
	 * 流标
	 *
	 * @author  wangjf
	 * @date    2016年11月28日 下午7:19:03
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo unRelease(Map<String, Object> params) throws SLException;
	
	/**
	 * 保存借款
	 *
	 * @author  wangjf
	 * @date    2016年11月28日 下午9:00:29
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo saveLoanProject(Map<String, Object> params) throws SLException;
		
	/**
	 * 查询项目
	 *
	 * @author  wangjf
	 * @date    2016年11月28日 下午9:03:56
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo queryProject(Map<String, Object> params) throws SLException;
	
	/**
	 * 流标/放款
	 *
	 * @author  zhangze
	 * @date    2017年6月2日 下午2:34:34
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo operateProject(Map<String, Object> params) throws SLException;
	
	/**
	 * 生效项目
	 *
	 * @author  wangjf
	 * @date    2016年11月29日 上午9:35:24
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo releaseLoan(Map<String, Object> params) throws SLException;
	
	/**
	 * 放款通知
	 *
	 * @author  wangjf
	 * @date    2016年11月29日 下午6:56:41
	 * @param params
	 *      <tt>loadId:String:借款id</tt><br>
	 *      <tt>userId:String:放款用户ID</tt><br>
	 * @return
	 * @throws SLException
	 */
	public ResultVo notifyGrant(Map<String, Object> params) throws SLException;
	
	/**
	 * 放款申请（企业借款）
	 *
	 * @author  wangjf
	 * @date    2016年12月17日 上午11:41:56
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo grantApplyCompany(Map<String, Object> params) throws SLException;
	
	/**
	 * 放款申请（商务借款）
	 *
	 * @author  wangjf
	 * @date    2016年12月17日 上午11:41:56
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo grantApplyBussiness(Map<String, Object> params) throws SLException;
	
	/**
	 * 放款确认接口（财富放款申请之后）
	 *
	 * 注：财务放款申请提交后，第三方支付处理完成并回调。
	 * @author  wangjf
	 * @date    2016年10月29日 下午4:38:17
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo grantConfirm(Map<String, Object> params) throws SLException;
	
	/**
	 * 生成还款计划的日期
	 *
	 * @author  wangjf
	 * @date    2016年12月17日 下午1:31:39
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo generateRepaymentPlanDay(Map<String, Object> params) throws SLException;
	
	/**
	 * 购买债权转让
	 *
	 * @author  wangjf
	 * @date    2017年1月6日 下午2:53:34
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo buyCredit(Map<String, Object> params) throws SLException;

	/**
	 * 购买债权转让 -- 使用红包
	 *
	 * @author  wangjf
	 * @date    2017年1月6日 下午2:53:34
	 * @param params
	 * @return
	 * @throws SLException
	 */
	ResultVo buyCreditExt(Map<String, Object> params) throws SLException;
	
	/**
	 * 定时任务（未授权的还款计划发送授权申请）
	 * @author liaobingbing
	 * @param params
	 * @throws SLException
	 */
	public void AccreditRequestJob(Map<String, Object> params) throws SLException;
}
