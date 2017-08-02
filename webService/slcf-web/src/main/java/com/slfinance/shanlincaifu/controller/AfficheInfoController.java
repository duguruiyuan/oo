/** 
 * @(#)AfficheInfoController.java 1.0.0 2015年5月4日 下午6:38:06  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AfficheInfoEntity;
import com.slfinance.shanlincaifu.service.AfficheInfoService;
import com.slfinance.util.BeanMapConvertUtil;
import com.slfinance.vo.ResultVo;

/**   
 * 公告业务控制器
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月4日 下午6:38:06 $ 
 */
@Controller
@RequestMapping(value="/afficheInfo", produces="application/json;charset=UTF-8")
public class AfficheInfoController {

	@Autowired
	private AfficheInfoService afficheInfoService;
	
	/**
	 * 分页查询网站公告列表
	 */
	@RequestMapping(value="/findAllAffiche", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> findAllAffiche(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return afficheInfoService.findAllAffiche(paramsMap);
	}

	/**
	 * 网站公告--新增
	 */
	@RequestMapping(value="/createAffiche", method = RequestMethod.POST)
	public @ResponseBody ResultVo createAffiche(@RequestBody Map<String, Object> paramsMap) throws SLException{
		AfficheInfoEntity afficheInfoEntity = BeanMapConvertUtil.toBean(AfficheInfoEntity.class, paramsMap);
		afficheInfoEntity.setCreateUser((String)paramsMap.get("custId"));
		return afficheInfoService.createAffiche(afficheInfoEntity);
	}

	/**
	 * 网站公告--查询单个
	 */
	@RequestMapping(value="/findOneAffiche", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> findOneAffiche(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return afficheInfoService.findOneAffiche((String)paramsMap.get("id"));
	}
	
	/**
	 * 网站公告--查询单个(替换img图片的src增加前缀http://image.shanlinbao.com)
	 */
	@RequestMapping(value="/findOneAfficheReplaceImgSrc", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> findOneAfficheReplaceImgSrc(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return afficheInfoService.findOneAfficheReplaceImgSrc((String)paramsMap.get("id"));
	}

	/**
	 * 网站公告--修改
	 */
	@RequestMapping(value="/updateAffiche", method = RequestMethod.POST)
	public @ResponseBody ResultVo updateAffiche(@RequestBody Map<String, Object> paramsMap) throws SLException{
		AfficheInfoEntity afficheInfoEntity = BeanMapConvertUtil.toBean(AfficheInfoEntity.class, paramsMap);
		afficheInfoEntity.setLastUpdateUser((String)paramsMap.get("custId"));
		return afficheInfoService.updateAffiche(afficheInfoEntity);
	}

	/**
	 * 网站公告--失效
	 */
	@RequestMapping(value="/invalidAffiche", method = RequestMethod.POST)
	public @ResponseBody ResultVo invalidAffiche(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return afficheInfoService.invalidAffiche((String)paramsMap.get("id"),(String)paramsMap.get("custId"));
	}

	/**
	 * 网站公告--发布
	 */
	@RequestMapping(value="/publishAffiche", method = RequestMethod.POST)
	public @ResponseBody ResultVo publishAffiche(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return afficheInfoService.publishAffiche((String)paramsMap.get("id"),(String)paramsMap.get("custId"));
	}

	/**
	 * 网站公告--删除
	 */
	@RequestMapping(value="/deleteAffiche", method = RequestMethod.POST)
	public @ResponseBody ResultVo deleteAffiche(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return afficheInfoService.deleteAffiche((String)paramsMap.get("id"),(String)paramsMap.get("custId"));
	}
	
	/**
	 * 网站公告--网站最新公告查询	
	 */
	@RequestMapping(value="/findNewestWebsiteAffiche", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> findNewestWebsiteAffiche() throws SLException{
		return afficheInfoService.findNewestWebsiteAffiche();
	}
	
	/**
	 * 网站公告--网站最新公告查询	
	 */
	@RequestMapping(value="/findAffiche", method = RequestMethod.POST)
	public @ResponseBody List<Map<String,Object>> findAffiche() throws SLException{
		return afficheInfoService.findAffiche();
	}
	
}
