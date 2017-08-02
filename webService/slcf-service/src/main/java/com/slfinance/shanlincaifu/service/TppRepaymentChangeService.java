package com.slfinance.shanlincaifu.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

public interface TppRepaymentChangeService {
	
	/*
	 * 对接tpp
	 * */
	public ResultVo repaymentChange_Tpp(Map<String, Object> params) throws SLException;
}
