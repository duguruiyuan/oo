package com.slfinance.shanlincaifu.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.slfinance.shanlincaifu.repository.custom.CommissionInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.PlatformDataService;
import com.slfinance.vo.ResultVo;

/**
 * 
 * <平台数据的实现类>
 * <功能详细描述>
 * 
 * @author  lzh
 * @version  [版本号, 2017年6月15日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Service("platformDataService")
public class PlatformDataServiceImpl implements PlatformDataService {

	@Autowired
	private CommissionInfoRepositoryCustom commissionInfoRepositoryCustom;
	
	/**
	 * 查询平台交易数据
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ResultVo queryPlatformData(Map<String, Object> param) {
		Map<String, Object> map= (Map<String, Object>) commissionInfoRepositoryCustom.queryPlatformData();
		Map<String, Object> res = (Map<String, Object>) map.get("data");
		if (res.entrySet().size() < 1) {
			res.put("data", "数据为空");
			return new ResultVo(false, "查询成功",res);
		}

		return new ResultVo(true, "查询成功",res);
	}

	/**
	 * 查询用户数据
	 * @param param
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ResultVo queryCustData(Map<String, Object> param) {
		String custType = (String) (param.get("custType") == null ? "" :  param.get("custType"));
		Map<String, Object> map =  commissionInfoRepositoryCustom.queryCustData(custType);
		Map<String, Object> res = (Map<String, Object>) map.get("data");
		if (res.entrySet().size() < 1) {
			res.put("data", "数据为空");
			return new ResultVo(false, "查询成功",res);
		}
		return new ResultVo(true, "查询成功",res);
	}

}
