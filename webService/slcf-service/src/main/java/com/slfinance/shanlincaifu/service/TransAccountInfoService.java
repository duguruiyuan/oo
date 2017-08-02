package com.slfinance.shanlincaifu.service;

import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

public interface TransAccountInfoService {
	public boolean batchImport(List<Map<String, Object>> listMap);

	public Map<String, Object> list(Map<String, Object> param);

	public ResultVo sendAdjustAccountProcess(Map<String, Object> param) throws SLException;
	
	/**
	 * 查询融租宝调帐
	 * @param params
	 * @return
	 */
	public ResultVo queryCompanyTransAccountList(Map<String, Object> param);
	
	/**
	 * 新建调账
	 * @param params
	 * @return
	 */
	public ResultVo saveCompanyTransAccount(Map<String, Object> params);
	
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
	public ResultVo auditCompanyTransAccount(Map<String, Object> params);
}
