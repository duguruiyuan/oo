/** 
 * @(#)ExportFileService.java 1.0.0 2016年3月7日 下午2:17:45  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 导出文件服务
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年3月7日 下午2:17:45 $ 
 */
public interface ExportFileService extends BeanSelfAware {
	/**
	 * 下载企业借款协议
	 *
	 * @author  wangjf
	 * @date    2016年3月7日 下午2:30:42
	 * @param params
     *      <tt>projectId:String:项目ID</tt><br>
     *      <tt>custId   :String:客户ID</tt><br>
	 * @return
	 */
	@Rules(rules = {
			@Rule(name = "projectId", required = true, requiredMessage = "项目ID不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
			})
	public ResultVo downloadProjectContract(Map<String, Object> params) throws SLException;
	

	/**
	 * 下载优选计划协议
	 *
	 * @author  wangjf
	 * @date    2016年3月7日 下午2:30:42
	 * @param params
     *      <tt>wealthId:String:计划主键</tt><br>
     *      <tt>custId  :String:客户ID</tt><br>
	 * @return
	 */
	@Rules(rules = {
			@Rule(name = "wealthId", required = true, requiredMessage = "计划主键不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
			})
	public ResultVo downloadWealthContract(Map<String, Object> params) throws SLException;
	
	/**
	 * 下载优选计划债权协议
	 *
	 * @author  wangjf
	 * @date    2016年3月7日 下午2:32:14
	 * @param params
     *      <tt>wealthId:String:计划主键</tt><br>
     *      <tt>custId  :String:客户ID</tt><br>
     *      <tt>loanId  :String:债权ID</tt><br>
	 * @return
	 */
	@Rules(rules = {
			@Rule(name = "wealthId", required = true, requiredMessage = "计划主键不能为空!"),
			@Rule(name = "loanId", required = true, requiredMessage = "债权ID不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
			})
	public ResultVo downloadWealthLoanContract(Map<String, Object> params) throws SLException;
	
	/**
	 * 下载优选项目债权协议
	 *
	 * @author  wangjf
	 * @date    2016年12月8日 下午7:58:26
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "loanId", required = true, requiredMessage = "债权ID不能为空!")
			})
	public ResultVo downloadLoanContract(Map<String, Object> params) throws SLException;
	
	/**
	 * 下载债权转让协议
	 *
	 * @author  wangjf
	 * @date    2017年1月4日 上午9:20:56
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "transferId", required = true, requiredMessage = "转让记录主键不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!")
			})
	public ResultVo downloadTransferContract(Map<String, Object> params) throws SLException;
	
	/**
	 * 下载债权转让及回购协议
	 * @author liyy
	 * @date 2017年2月23日 下午16:01:23
	 * @param params
	 *        <tt>loanId:String:借款信息表主键Id</tt><br>
	 *        <tt>custId:String:客户信息Id</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "loanId", required = true, requiredMessage = "借款信息不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户信息不能为空!") 
	})
	public ResultVo downloadAssetContract(Map<String, Object> params) throws SLException;
	
	
	/**
	 * 下载善融贷协议
	 */
	@Rules(rules = {
			@Rule(name = "investId", required = true, requiredMessage = "投资Id不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户信息不能为空!") 
	})
	public ResultVo downloadFinancingContract(Map<String, Object> params) throws SLException;
	
	/**
	 * 下载善意贷协议
	 */
	@Rules(rules = {
			@Rule(name = "investId", required = true, requiredMessage = "投资Id不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户信息不能为空!") 
	})
	public ResultVo downloadFingertipContract(Map<String, Object> params) throws SLException;
	
	/**
	 * 下载雪澄协议
	 */
	@Rules(rules = {
			@Rule(name = "investId", required = true, requiredMessage = "投资Id不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户信息不能为空!") 
	})
	public ResultVo downloadSnowOrangeContract(Map<String, Object> params) throws SLException;
	
	/**
	 * 下载协议通用接口
	 * @author liyy
	 * @date 2017年2月27日 下午14:26:23
	 * @param params
	 *        <tt>investId:String:投资Id</tt><br>
	 *        <tt>custId:String:客户信息Id</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "investId", required = true, requiredMessage = "投资Id不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户信息不能为空!") 
	})
	public ResultVo downloadContract(Map<String, Object> params) throws SLException;
	
	/**
	 * 异步下载
	 *
	 * @author  wangjf
	 * @date    2017年3月25日 上午10:54:41
	 * @param params
	 * @throws SLException
	 */
	public void asynDownloadContract(Map<String, Object> params) throws SLException;
}
