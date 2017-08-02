/** 
 * @(#)AfficheInfoServiceTest.java 1.0.0 2015年4月28日 下午5:44:00  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.AfficheInfo;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AfficheInfoEntity;
import com.slfinance.shanlincaifu.service.AbstractSpringContextTestSupport;
import com.slfinance.shanlincaifu.service.AfficheInfoService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**   
 * 网站公告业务管理
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月28日 下午5:44:00 $ 
 */
public class AfficheInfoServiceTest extends AbstractSpringContextTestSupport {

	@Autowired
	private AfficheInfoService afficheInfoService;
	
	/**
	 * 分页查询网站公告列表
	 */
	@Test
	public void testFindAllAffiche() throws SLException{
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("start", 0);
		paramsMap.put("length", 12);
		paramsMap.put("custId", "05395af8-a2ab-4e50-8289-7c8ade68d952");
		afficheInfoService.findAllAffiche(paramsMap);
	}
	
	/**
	 * 分页查询网站公告列表
	 */
	@Test
	public void testFindAllAffiche2() throws SLException{
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("start", 0);
		paramsMap.put("length", 12);
		paramsMap.put("afficheStatus", Constant.AFFICHE_STATUS_PUBLISHED);
		paramsMap.put("afficheType",  Constant.AFFICHE_TYPE_ALL);
		paramsMap.put("cache",  1);
		afficheInfoService.findAllAffiche(paramsMap);
	}
	
	/**
	 * 分页查询最新动态列表
	 */
	@Test
	public void testFindAllAffiche3() throws SLException{
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("start", 0);
		paramsMap.put("length", 12);
		paramsMap.put("afficheStatus", Constant.AFFICHE_STATUS_PUBLISHED);
		List<String> trendsTypeList = new ArrayList<String>();
		trendsTypeList.add(Constant.AFFICHE_TYPE_NEWS);
		trendsTypeList.add(Constant.AFFICHE_TYPE_MEDIA);
		paramsMap.put("statusList", trendsTypeList);
		paramsMap.put("cache",  2);
		afficheInfoService.findAllAffiche(paramsMap);
	}
	
	/**
	 * 网站公告--新增
	 */
	@Test
	public void createAffiche() throws SLException{
		AfficheInfoEntity afficheInfo = new AfficheInfoEntity();
		afficheInfo.setAfficheTitle("妹子我看上你了");
		afficheInfo.setAfficheType(Constant.AFFICHE_TYPE_ALL);
		afficheInfo.setAfficheContent("妹子我看上你了");
		afficheInfo.setCreateUser("zhangzsceshinihaohohoih");
		ResultVo reusltVo = afficheInfoService.createAffiche(afficheInfo);
		assertEquals(ResultVo.isSuccess(reusltVo), true);
	}

	/**
	 * 网站公告--查询单个
	 */
	@Test
	public void findOneAffiche() throws SLException{
		log.info(JSONObject.toJSONString(afficheInfoService.findOneAffiche("5fbac82a-5bb3-4a1a-9655-f64b7b77f325")));
	}

	/**
	 * 网站公告--修改
	 */
	@Test
	public void updateAffiche() throws SLException{
		AfficheInfoEntity afficheInfo = new AfficheInfoEntity();
		afficheInfo.setId("23a1372a-7ddb-4f12-b9f7-c4815758d4eb");
		afficheInfo.setAfficheTitle("当我容颜老去，可否还我当年比你收下的全部温柔");
		afficheInfo.setAfficheType(Constant.AFFICHE_TYPE_NOTICE);
		afficheInfo.setAfficheContent("滚，神经病");
		ResultVo reusltVo =  afficheInfoService.updateAffiche(afficheInfo);
		assertEquals(ResultVo.isSuccess(reusltVo), true);
	}

	/**
	 * 网站公告--失效
	 */
	@Test
	public void invalidAffiche( ) throws SLException{
		ResultVo reusltVo = afficheInfoService.invalidAffiche("883ab8db-5e61-42f2-8377-c189c34d367a", "preaty!");
		assertEquals(ResultVo.isSuccess(reusltVo), true);
	}

	/**
	 * 网站公告--发布
	 */
	@Test
	public void publishAffiche( ) throws SLException{
		ResultVo reusltVo = afficheInfoService.publishAffiche("2851bda3-5dc5-4912-9b79-eeb1cb72cf7c", "hi!");
		assertEquals(ResultVo.isSuccess(reusltVo), true);
	}

	/**
	 * 网站公告--删除
	 */
	@Test
	public void deleteAffiche( ) throws SLException{
		ResultVo reusltVo = afficheInfoService.deleteAffiche("883ab8db-5e61-42f2-8377-c189c34d367a", "face!");
		assertEquals(ResultVo.isSuccess(reusltVo), true);
	}
	
	/**
	 * 网站公告--网站最新公告查询	
	 */
	@Test
	public void findNewestWebsiteAffiche() throws SLException{
		log.info(JSONObject.toJSONString(afficheInfoService.findNewestWebsiteAffiche()));
	}
	
	/**
	 * 读公告
	 *
	 * @author  wangjf
	 * @date    2015年12月15日 下午1:42:56
	 * @throws SLException
	 */
	@Test
	public void testReadAffiche() throws SLException{
		Map<String, Object> params = Maps.newHashMap();
		params.put("custId", "20150427000000000000000000000000001");
		params.put("id", "2851bda3-5dc5-4912-9b79-eeb1cb72cf7c");
		afficheInfoService.readAffiche(params);
	}
	
	/**
	 * 统计公告
	 *
	 * @author  wangjf
	 * @date    2015年12月15日 下午1:44:25
	 * @throws SLException
	 */
	@Test
	public void testCountAffiche() throws SLException{
		Map<String, Object> params = Maps.newHashMap();
		params.put("custId", "20150427000000000000000000000000001");
		Map<String, Object> result = afficheInfoService.countAffiche(params);
		System.out.println(result);
	}
}
