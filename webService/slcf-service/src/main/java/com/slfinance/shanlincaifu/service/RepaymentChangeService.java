package com.slfinance.shanlincaifu.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;
public interface RepaymentChangeService {
	/**
	 * 还款计划变更
	 *
	 * @author  wudl
	 * @date    2017年7月12日 下午14:51:03
	 * @param paramMap
	 * @return
	 * @throws SLException
	 */
	public ResultVo repaymentChange(Map<String, Object> paramMap,List<Map<String,Object>> repaymentList) throws SLException;


}
