/** 
 * @(#)ProductDetailInfoRepositoryTest.java 1.0.0 2015年4月25日 下午3:50:14  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.dal;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.slfinance.modules.test.spring.Profiles;
import com.slfinance.shanlincaifu.entity.ProductDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductInfoEntity;
import com.slfinance.shanlincaifu.repository.ProductDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductInfoRepository;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 
 *  
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年4月25日 下午3:50:14 $ 
 */

@ActiveProfiles(Profiles.DEVELOPMENT)
@ContextConfiguration(locations = { "/application-jpa.xml" })
public class ProductDetailInfoRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests{
	
	@Autowired
	private ProductDetailInfoRepository productDetailInfoRepository;
	
	@Autowired
	private EntityManager manager;
	@Autowired
	private ProductInfoRepository productInfoRepository;
	
	@Test
	public void findProductInfoByProductTypeNameTest(){
		ProductInfoEntity pie=this.productInfoRepository.findProductInfoByProductTypeName("活期宝");
		
	}
	
	@Test
	public void findAtoneListByConditionTest(){
		Map<String,Object> params=new HashMap<>();
		params.put("start", 0);
		params.put("length", 10);
		Page<Map<String, Object>> page=productDetailInfoRepository.findAtoneListByCondition(params);
		System.out.println(page);
	}
	
	@Test
	public void findDetailByConditionTest(){
		Map<String,Object> params=new HashMap<>();
		params.put("tradeCode", "BAO-TRADE-1000000000660");
		Map<String, Object> map=productDetailInfoRepository.findDetailByCondition(params);
		System.out.println(map==null);
	}
	
	@Test
	public void findByConditionTest(){
		Map<String,Object> paramMap=new HashMap<String,Object>();
		
		paramMap.put("credentialsCode", "0");
		paramMap.put("custName", "0");
		paramMap.put("loginName", "0");
		paramMap.put("start", "0");
		paramMap.put("length", 10);
		Page<Map<String,Object>> page=productDetailInfoRepository.findByCondition(paramMap);
		for(Map<String,Object> il:page.getContent()){
//			System.out.println(il.getLoginName()+","+il.getCustName()+","+il.getCredentialsCode());
		}		
	}
	
	/**
	 * 测试通过产品ID查询产品详情
	 *
	 * @author  wangjf
	 * @date    2015年5月4日 下午5:40:16
	 */
	@Test
	public void testFindProductDetailInfoByProductId(){
//		ProductInfoEntity productInfoEntity = productInfoRepository.findTopByProductTypeOrderByFavoriteSortAsc("3");
		ProductInfoEntity productInfoEntity = null;
		assertNotNull(productInfoEntity);
		ProductDetailInfoEntity productDetailInfoEntity = productDetailInfoRepository.findByProductId("1");
		assertNotNull(productDetailInfoEntity);
	}
	
	@Test
	public void testQueryPartake() {
		Object querys = productDetailInfoRepository.queryPartake(Constant.PRODUCT_TYPE_01);
		System.out.println(querys);
	}
}
