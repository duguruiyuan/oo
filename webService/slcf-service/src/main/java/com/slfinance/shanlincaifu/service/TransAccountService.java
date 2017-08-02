package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**
 * 企业借款调账管理
 * @author zhangt
 *
 */
public interface TransAccountService {
	
	/**
	 * 查询企业借款调帐
	 * @param params
	 * @return
	 */
	public ResultVo queryCompanyTransAccountList(Map<String, Object> param);
	
	/**
	 * 新建调账
	 * @param params
	 * @return
	 */
	@Rules(rules = {
			@Rule(name = "transType", required = true, requiredMessage = "调账类型不能为空"),
			@Rule(name = "companyId", required = true, requiredMessage = "商户id不能为空"),
			@Rule(name = "transAmount", required = true, requiredMessage = "调账金额不能为空", number = true, numberMessage = "调账金额只能为数字")
			})
	public ResultVo saveCompanyTransAccount(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询调账
	 * @param params
	 * @return
	 */
	public ResultVo queryCompanyTransAccountById(Map<String, Object> params);
	
	/**
	 * 审核调帐
	 * @param params
	 * @return
	 */
	public ResultVo auditCompanyTransAccount(Map<String, Object> params) throws SLException;

}
