package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

public interface SendRepaymentService {

	ResultVo sendRepayment(Map<String, Object> param) throws SLException;
}
