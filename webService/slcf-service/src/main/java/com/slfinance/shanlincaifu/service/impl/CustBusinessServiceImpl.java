/** 
 * @(#)CustBusinessServiceImpl.java 1.0.0 2015年12月11日 上午9:42:33  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.custom.CustBusinessRepositoryCustom;
import com.slfinance.shanlincaifu.service.CustBusinessService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**   
 * 客户业务信息服务实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年12月11日 上午9:42:33 $ 
 */
@Service("custBusinessService")
public class CustBusinessServiceImpl implements CustBusinessService {

	@Autowired
	private CustBusinessRepositoryCustom custBusinessRepositoryCustom;
	
	@Override
	public Map<String, Object> findRegisterReport(Map<String, Object> params) {
		
		if(StringUtils.isEmpty((String)params.get("tradeType"))) {
			params.put("tradeType", Constant.OPERATION_TYPE_16);
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = custBusinessRepositoryCustom.findRegisterReport(params);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	@Override
	public ResultVo exportRegisterReport(Map<String, Object> params) throws SLException{
		
		if(StringUtils.isEmpty((String)params.get("tradeType"))) {
			params.put("tradeType", Constant.OPERATION_TYPE_16);
		}
		
		int totalNumber = custBusinessRepositoryCustom.countRegisterReport(params);
		if(totalNumber > 5000) {
			return new ResultVo(false, "当前条件查询的记录总数查过5000条，数据量太大无法导出");
		}
		
		// 取数据报表
		params.put("start", "0");
		params.put("length", Integer.MAX_VALUE);

		return new ResultVo(true, "获取报表数据成功", findRegisterReport(params));
	}

}
