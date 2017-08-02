package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;



/**
 * AppVersionCheckService Test. @author Tools
 */
public class AppVersionCheckServiceTest extends AbstractSpringContextTestSupport  {


	@Autowired
	private AppVersionCheckService appVersionCheckService;
	
	@Autowired
	private AppManageService appManageService;


	/**
	 * 测试查询App版本信息
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-07-18 13:44:02
	 */
	@Test
	public void testCheckAPPVersion() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("version", "1.0");
		param.put("appSource", "android");
		ResultVo result = appVersionCheckService.checkAPPVersion(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试列表
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-07-18 13:44:02
	 */
	@Test
	public void testQueryAppVersionList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("appSource", "android");
		ResultVo result = appManageService.queryAppVersionList(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试详情
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-07-18 13:44:02
	 */
	@Test
	public void testQueryAppVersionById() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", "f90d47ea-7a7f-4d77-a157-5b7c2a22fd16");
		ResultVo result = appManageService.queryAppVersionById(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试保存/更新
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-07-18 13:44:02
	 */
	@Test
	public void testSaveAppVersion() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", "f90d47ea-7a7f-4d77-a157-5b7c2a22fd16");
		param.put("appSource", "ios");
		param.put("appVersion", "1.6");
		param.put("appSupportedVersion", "1.5");
		param.put("updateUrl", "https://itunes.apple.com/us/app/shan-lin-cai-fu/id1097437552?l=zh&ls=1&mt=8");
		ResultVo result = appManageService.saveAppVersion(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试查询APP版本
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-07-18 13:44:02
	 */
	@Test
	public void testQueryAppVersion() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("appSource", "ios");
		ResultVo result = appManageService.queryAppVersion(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


}
