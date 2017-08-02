/** 
 * @(#)ParamServiceTest.java 1.0.0 2015年10月23日 下午3:31:31  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;

/**   
 * 参数测试类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年10月23日 下午3:31:31 $ 
 */
public class ParamServiceTest extends AbstractSpringContextTestSupport{

	@Autowired
	private ParamService paramService;
	
	/**
	 * 根据参数类型查询
	 *
	 * @author  wangjf
	 * @date    2015年10月26日 下午5:07:27
	 * @throws Exception
	 */
	@Test
	public void testFindByParamType() throws Exception {
		Map<String, Object> params = Maps.newHashMap();
		params.put("type", "baoProductTradeType");
		Map<String, Object> result = paramService.findByParamType(params);
		System.out.println(result);
	}
	
	/**
	 * 根据父ID查询参数列表
	 *
	 * @author  wangjf
	 * @date    2015年10月26日 下午5:09:44
	 * @throws Exception
	 */
	@Test
	public void testFindByParentId() throws Exception {
		Map<String, Object> params = Maps.newHashMap();
		params.put("parentId", "100");
		Map<String, Object> result = paramService.findByParentId(params);
		System.out.println(result);
	}
	
	/**
	 * 通过城市ID和银行名称 查询支行记录
	 *
	 * @author  wangjf
	 * @date    2015年10月26日 下午5:14:30
	 * @throws Exception
	 */
	@Test
	public void testFindByCityIdAndBankName() throws Exception {
		Map<String, Object> params = Maps.newHashMap();
		params.put("cityId", "2147");
		params.put("bankName", "中国农业银行");
		params.put("subBankName", "广德县");
		Map<String, Object> result = paramService.findByCityIdAndBankName(params);
		System.out.println(result);
	}
	
	/**
	 * 查询所有银行
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFindAllBankList() throws Exception {
		Map<String, Object> params = Maps.newHashMap();
		params.put("type", "8");
		Map<String, Object> result = paramService.findAllBankList(params);
		System.out.println(result);
	}
	
}
