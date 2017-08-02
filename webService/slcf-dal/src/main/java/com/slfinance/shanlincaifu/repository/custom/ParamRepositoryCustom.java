/** 
 * @(#)ParamRepositoryCustom.java 1.0.0 2015年10月22日 下午4:57:39  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

/**   
 * 自定义参数数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年10月22日 下午4:57:39 $ 
 */
public interface ParamRepositoryCustom {
	
	/**
	 * 根据城市ID和银行名称查询支行数据
	 *
	 * @author  wangjf
	 * @date    2015年10月22日 下午5:16:10
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>>  findByCityIdAndBankName(Map<String, Object> map)throws Exception;
	
	/**
	 * 查询支持的银行卡
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> findBankByType(Map<String, Object> params);

	/**
	 * 划扣公司
	 * @return
	 */
	public List<Map<String, Object>> findThirdPayList(Map<String, Object> params);
}
