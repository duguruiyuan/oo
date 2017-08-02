/** 
 * @(#)BanerServiceTest.java 1.0.0 2015年10月19日 下午1:41:11  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**   
 * Banner测试类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年10月19日 下午1:41:11 $ 
 */
public class BanerServiceTest extends AbstractSpringContextTestSupport{

	@Autowired
	private BannerService bannerService;
	
	/**
	 * 测试保存Banner
	 *
	 * @author  wangjf
	 * @throws SLException 
	 * @date    2015年10月19日 下午1:42:36
	 */
	@Test
	public void testSaveBanner() throws SLException {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", "83959f58-6c5b-425e-a0da-b899c9151e57");
		params.put("bannerTitle", "RichardTest");
		params.put("bannerUrl", "http://www.shanlinbao.com/loan/detail");
		params.put("bannerSort", "2");
		params.put("appSource", Constant.APP_SOURCE_IOS);
		params.put("bannerImagePath", "/upload/banner/ios/201510190002.jpg");
		params.put("userId", "1");
		params.put("bannerType", Constant.BANNER_TYPE_INTERNAL);
		params.put("tradeType", Constant.BANNER_TRADE_TYPE_INDEX);
		ResultVo reusltVo = bannerService.saveBanner(params);
		assertEquals(ResultVo.isSuccess(reusltVo), true);
	}
	
	/**
	 * 测试发布banner
	 *
	 * @author  wangjf
	 * @date    2015年10月19日 下午2:03:47
	 * @throws SLException
	 */
	@Test
	public void testPublishBanner() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", "32e23e5c-2330-440a-8acc-ad04515b4dbd");
		params.put("bannerStatus", Constant.AFFICHE_STATUS_PUBLISHED);
		ResultVo reusltVo = bannerService.publishBanner(params);
		assertEquals(ResultVo.isSuccess(reusltVo), true);
	}
	
	/**
	 * 测试Banner
	 *
	 * @author  wangjf
	 * @date    2015年10月19日 下午2:05:56
	 * @throws SLException
	 */
	@Test
	public void testQueryBanner() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("start", 0);
		params.put("length", 10);
		params.put("appSource", Constant.APP_SOURCE_IOS);
		Map<String, Object> result = bannerService.queryBanner(params);
		assertNotNull(result);
		assertEquals(result.size() != 0, true);
	}
	
	/**
	 * 通过ID查询Banner
	 *
	 * @author  wangjf
	 * @date    2015年10月28日 下午4:02:57
	 * @throws SLException
	 */
	@Test
	public void testQueryBannerById() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", "83959f58-6c5b-425e-a0da-b899c9151e57");
		Map<String, Object> result = bannerService.queryBannerById(params);
		assertNotNull(result);
		assertEquals(result.size() != 0, true);
	}
	
	/**
	 * 查询appbanner
	 *
	 * @author  wangjf
	 * @date    2015年11月7日 上午10:00:49
	 * @throws SLException
	 */
	@Test
	public void testQueryAppBanner() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appSource", Constant.APP_SOURCE_IOS);
		ResultVo result = bannerService.queryAppBanner(params);
		System.out.println(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 查询app引导页
	 * 
	 * @author gaoll
	 * @date 2015年11月27日 上午16:40:49
	 * @throws SLException
	 */
	@Test
	public void testQueryAppGuide() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appSource", Constant.APP_SOURCE_ANDROID);
		ResultVo result = bannerService.queryAppGuide(params);
		System.out.println(result);
		assertEquals(ResultVo.isSuccess(result), true);
		
	}
	
	/**
	 * 查询app启动页
	 * 
	 * @author gaoll
	 * @date 2015年11月27日 上午19:40:49
	 * @throws SLException
	 */
	@Test
	public void testQueryAppStart() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appSource", Constant.APP_SOURCE_ANDROID);
//		params.put();
		ResultVo result = bannerService.queryAppStart(params);
		System.out.println(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试保存活动
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2015-12-30 11:55:51
	 */
	@Test
	public void testSaveActivity() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", "");
		param.put("bannerTitle", "");
		param.put("bannerUrl", "");
		param.put("bannerContent", "");
		param.put("bannerImagePath", "");
		param.put("userId", "");
		param.put("bannerType", "");
		param.put("tradeType", "");
		param.put("isRecommend", "");
		ResultVo result = bannerService.saveActivity(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试查询活动
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2015-12-30 11:55:51
	 */
	@Test
	public void testQueryActivity() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("bannerType", "");
		ResultVo result = bannerService.queryActivity(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
}
