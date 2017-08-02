/** 
 * @(#)BannerServiceImpl.java 1.0.0 2015年10月16日 下午1:54:10  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AttachmentInfoEntity;
import com.slfinance.shanlincaifu.entity.BannerInfoEntity;
import com.slfinance.shanlincaifu.repository.AttachmentRepository;
import com.slfinance.shanlincaifu.repository.BannerInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.BannerInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.BannerService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.util.BeanMapConvertUtil;
import com.slfinance.vo.ResultVo;

/**   
 * Banner服务实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年10月16日 下午1:54:10 $ 
 */
@Slf4j
@Service("bannerService")
public class BannerServiceImpl implements BannerService {

	@Autowired
	private BannerInfoRepository bannerInfoRepository;
	
	@Autowired
	private AttachmentRepository attachmentRepository;
	
	@Autowired
	private BannerInfoRepositoryCustom bannerInfoRepositoryCustom;
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	@Caching(evict = { @CacheEvict(value = "slcf_appguid", allEntries = true, cacheManager="redis"), 
			@CacheEvict(value = "slcf_appstart", allEntries = true, cacheManager="redis"),
			@CacheEvict(value = "slcf_appbanner", allEntries = true, cacheManager="redis"),
			@CacheEvict(value = "slcf_banner", key= "#result.result['data']", cacheManager="redis"),
			@CacheEvict(value = "slcf_queryallbannertitle", allEntries = true, cacheManager="redis") })
	public ResultVo saveBanner(Map<String, Object> params) throws SLException {
		
		BannerInfoEntity bannerInfoEntity = BeanMapConvertUtil.toBean(BannerInfoEntity.class, params);
		bannerInfoEntity.setAppSource(Strings.nullToEmpty(bannerInfoEntity.getAppSource()).toLowerCase());
		bannerInfoEntity.setBannerStatus(Constant.AFFICHE_STATUS_NEW);
		bannerInfoEntity.setBasicModelProperty((String)params.get("userId"), true);
		
		// 主键非空表示编辑
		if(!StringUtils.isEmpty(bannerInfoEntity.getId())) {
			BannerInfoEntity oldBannerInfoEntity = bannerInfoRepository.findOne(bannerInfoEntity.getId());
			if(oldBannerInfoEntity == null) {
				log.error("未找到banner信息，banner:{}", params.toString());
				throw new SLException("未找到banner信息");
			}
			oldBannerInfoEntity.setBannerTitle(bannerInfoEntity.getBannerTitle());
			oldBannerInfoEntity.setBannerUrl(bannerInfoEntity.getBannerUrl());
			oldBannerInfoEntity.setBannerStatus(bannerInfoEntity.getBannerStatus());
			oldBannerInfoEntity.setAppSource(bannerInfoEntity.getAppSource());
			oldBannerInfoEntity.setBannerSort(bannerInfoEntity.getBannerSort());
			oldBannerInfoEntity.setBannerType(bannerInfoEntity.getBannerType());
			oldBannerInfoEntity.setTradeType(bannerInfoEntity.getTradeType());
			oldBannerInfoEntity.setIsShare(bannerInfoEntity.getIsShare());
			oldBannerInfoEntity.setBasicModelProperty((String)params.get("userId"), false);

			AttachmentInfoEntity attachmentInfoEntity = attachmentRepository.findByRelatePrimary(oldBannerInfoEntity.getId());
			if(attachmentInfoEntity == null) {
				log.error("未找到banner图片，banner:{}", oldBannerInfoEntity.getId());
				throw new SLException("未找到banner图片");
			}
			attachmentInfoEntity.setAttachmentName(getFileNameOrDirectory((String)params.get("bannerImagePath"), 3));
			attachmentInfoEntity.setStoragePath(getFileNameOrDirectory((String)params.get("bannerImagePath"), 2));
			attachmentInfoEntity.setBasicModelProperty((String)params.get("userId"), false);
		}
		else {
			bannerInfoEntity = bannerInfoRepository.save(bannerInfoEntity);
			AttachmentInfoEntity attachmentInfoEntity = new AttachmentInfoEntity();
			attachmentInfoEntity.setRelateType(Constant.TABLE_BAO_T_BANNER_INFO);
			attachmentInfoEntity.setRelatePrimary(bannerInfoEntity.getId());
			attachmentInfoEntity.setAttachmentType(Constant.BANNER_ATTACHMENT_TYPE_INDEX);
			attachmentInfoEntity.setAttachmentName(getFileNameOrDirectory((String)params.get("bannerImagePath"), 3));
			attachmentInfoEntity.setStoragePath(getFileNameOrDirectory((String)params.get("bannerImagePath"), 2));
			attachmentInfoEntity.setDocType(Constant.UNBIND_CARD_ATTACHMENT_DOC_TYPE_PIG);
			attachmentInfoEntity.setBasicModelProperty((String)params.get("userId"), true);
			attachmentRepository.save(attachmentInfoEntity);
		}
		
		return new ResultVo(true, "保存Banner成功", bannerInfoEntity.getId());
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	@Caching(evict = { @CacheEvict(value = "slcf_appguid", allEntries = true, cacheManager="redis"), 
			@CacheEvict(value = "slcf_appstart", allEntries = true, cacheManager="redis"), 
			@CacheEvict(value = "slcf_appbanner", allEntries = true, cacheManager="redis"),
			@CacheEvict(value = "slcf_banner", key= "#params['id']", cacheManager="redis"),
			@CacheEvict(value = "slcf_queryallbannertitle", allEntries = true, cacheManager="redis") })
	public ResultVo publishBanner(Map<String, Object> params)
			throws SLException {
		BannerInfoEntity bannerInfoEntity = bannerInfoRepository.findOne((String)params.get("id"));
		if(bannerInfoEntity == null) {
			log.error("未找到banner信息，banner:{}", params.toString());
			return new ResultVo(false, "未找到banner信息");
		}
		
		if(params.containsKey("bannerSort")) {
			bannerInfoEntity.setBannerSort(new Integer(params.get("bannerSort").toString()));
		}
		
		if(params.containsKey("bannerStatus")) {
			bannerInfoEntity.setBannerStatus((String)params.get("bannerStatus"));
		}
		bannerInfoEntity.setBasicModelProperty((String)params.get("userId"), false);
		return new ResultVo(true, "操作成功");
	}

	@Override
	public Map<String, Object> queryBanner(Map<String, Object> params)
			throws SLException {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();

		Page<Map<String, Object>> page = bannerInfoRepositoryCustom.queryBanner(params);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		
		return resultMap;
	}

	/**
	 * 获取文件名称或者目录
	 *
	 * @author  wangjf
	 * @date    2015年10月16日 下午6:23:20
	 * @param path
	 * @param flag 1 返回全路径 2 返回目录 3返回文件名称
	 * @return
	 */
	private String getFileNameOrDirectory(String path, int flag) {
				
		switch(flag) {
		case 1:
			return path;
		case 2:
			return path.substring(0, path.lastIndexOf("/"));
		case 3:
			return path.substring(path.lastIndexOf("/") + 1);
		default:
				break;
		}
		
		return "";
	}

	@Override
	@Cacheable(value="slcf_appbanner",key="'appbanner_' + #params['appSource']", cacheManager="redis")
	public ResultVo queryAppBanner(Map<String, Object> params)
			throws SLException {
		params.put("bannerStatus", Constant.AFFICHE_STATUS_PUBLISHED);
		params.put("tradeType", Constant.BANNER_TRADE_TYPE_INDEX);
		return new ResultVo(true, "查询Banner列表成功", bannerInfoRepositoryCustom.queryLatestBanner(params));
	}

	@Override
	@Cacheable(value="slcf_banner",key="#params['id']", cacheManager="redis")
	public Map<String, Object> queryBannerById(Map<String, Object> params) {
		return bannerInfoRepositoryCustom.queryBannerById(params);
	}

	@Override
	@Cacheable(value="slcf_appguid",key="'appguid_' + #params['appSource']", cacheManager="redis")
	public ResultVo queryAppGuide(Map<String, Object> params)
			throws SLException {
		params.put("bannerStatus", Constant.AFFICHE_STATUS_PUBLISHED);
		params.put("tradeType", Constant.BANNER_TRADE_TYPE_GUIDE);
		return new ResultVo(true, "查询Banner列表成功", bannerInfoRepositoryCustom.queryLatestBanner(params));
	}

	@Override
	@Cacheable(value="slcf_appstart",key="'appstart_' + #params['appSource']", cacheManager="redis")
	public ResultVo queryAppStart(Map<String, Object> params)
			throws SLException {
		params.put("bannerStatus", Constant.AFFICHE_STATUS_PUBLISHED);
		params.put("tradeType", Constant.BANNER_TRADE_TYPE_START);
		return new ResultVo(true, "查询Banner列表成功", bannerInfoRepositoryCustom.queryLatestBanner(params));
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	@Caching(evict = { @CacheEvict(value = "slcf_banner", key= "#result.result['data']", cacheManager="redis"),
			@CacheEvict(value = "slcf_appactivity", allEntries = true, cacheManager="redis")})
	public ResultVo saveActivity(Map<String, Object> params) throws SLException {
		BannerInfoEntity bannerInfoEntity = BeanMapConvertUtil.toBean(BannerInfoEntity.class, params);
		bannerInfoEntity.setBannerSort(0);
		bannerInfoEntity.setAppSource(Constant.APP_SOURCE_OHTERS);
		bannerInfoEntity.setBannerStatus(Constant.AFFICHE_STATUS_PUBLISHED);
		bannerInfoEntity.setBasicModelProperty((String)params.get("userId"), true);
		
		// 主键非空表示编辑
		if(!StringUtils.isEmpty(bannerInfoEntity.getId())) {
			BannerInfoEntity oldBannerInfoEntity = bannerInfoRepository.findOne(bannerInfoEntity.getId());
			if(oldBannerInfoEntity == null) {
				log.error("未找到活动信息，活动:{}", params.toString());
				throw new SLException("未找到活动信息");
			}
			oldBannerInfoEntity.setBannerTitle(bannerInfoEntity.getBannerTitle());
			oldBannerInfoEntity.setBannerContent(bannerInfoEntity.getBannerContent());
			oldBannerInfoEntity.setBannerUrl(bannerInfoEntity.getBannerUrl());
			oldBannerInfoEntity.setBannerStatus(bannerInfoEntity.getBannerStatus());
			oldBannerInfoEntity.setBannerType(bannerInfoEntity.getBannerType());
			oldBannerInfoEntity.setTradeType(bannerInfoEntity.getTradeType());
			oldBannerInfoEntity.setIsRecommend(bannerInfoEntity.getIsRecommend());
			oldBannerInfoEntity.setBasicModelProperty((String)params.get("userId"), false);
			
			AttachmentInfoEntity attachmentInfoEntity = attachmentRepository.findByRelatePrimary(oldBannerInfoEntity.getId());
			if(attachmentInfoEntity == null) {
				log.error("未找到活动图片，活动:{}", oldBannerInfoEntity.getId());
				throw new SLException("未找到活动图片");
			}
			attachmentInfoEntity.setAttachmentName(getFileNameOrDirectory((String)params.get("bannerImagePath"), 3));
			attachmentInfoEntity.setStoragePath(getFileNameOrDirectory((String)params.get("bannerImagePath"), 2));
			attachmentInfoEntity.setBasicModelProperty((String)params.get("userId"), false);
		}
		else {
			bannerInfoEntity = bannerInfoRepository.save(bannerInfoEntity);
			AttachmentInfoEntity attachmentInfoEntity = new AttachmentInfoEntity();
			attachmentInfoEntity.setRelateType(Constant.TABLE_BAO_T_BANNER_INFO);
			attachmentInfoEntity.setRelatePrimary(bannerInfoEntity.getId());
			attachmentInfoEntity.setAttachmentType(Constant.BANNER_ATTACHMENT_TYPE_INDEX);
			attachmentInfoEntity.setAttachmentName(getFileNameOrDirectory((String)params.get("bannerImagePath"), 3));
			attachmentInfoEntity.setStoragePath(getFileNameOrDirectory((String)params.get("bannerImagePath"), 2));
			attachmentInfoEntity.setDocType(Constant.UNBIND_CARD_ATTACHMENT_DOC_TYPE_PIG);
			attachmentInfoEntity.setBasicModelProperty((String)params.get("userId"), true);
			attachmentRepository.save(attachmentInfoEntity);
		}
		
		return new ResultVo(true, "保存活动成功", bannerInfoEntity.getId());
	}

	@Cacheable(value="slcf_appactivity",key="'appactivity_' + #params['bannerType']", cacheManager="redis")
	@Override
	public ResultVo queryActivity(Map<String, Object> params)
			throws SLException {
		params.put("bannerStatus", Constant.AFFICHE_STATUS_PUBLISHED);
		params.put("tradeType", Constant.BANNER_TRADE_TYPE_SHARE);
		//分享页appsource 保存的是others，但是手机端会分别传 android ，ios
		params.remove("appSource");
		
		List<Map<String, Object>> list = bannerInfoRepositoryCustom.queryLatestBanner(params);
		if(list == null || list.size() == 0) {
			params.put("bannerType", Constant.BANNER_TYPE_AWARD);
			list = bannerInfoRepositoryCustom.queryLatestBanner(params);
		}
		
		return new ResultVo(true, "查询Banner列表成功", list);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	@Caching(evict = { @CacheEvict(value = "slcf_banner", key= "#result.result['data']", cacheManager="redis"),
			@CacheEvict(value = "slcf_appactivityurl", allEntries = true, cacheManager="redis"),
			@CacheEvict(value = "slcf_queryallbannertitle", allEntries = true, cacheManager="redis") })
	public ResultVo saveActivityUrl(Map<String, Object> params)
			throws SLException {
		BannerInfoEntity bannerInfoEntity = BeanMapConvertUtil.toBean(BannerInfoEntity.class, params);
		bannerInfoEntity.setBannerSort(0);
		bannerInfoEntity.setAppSource(Constant.APP_SOURCE_OHTERS);
		bannerInfoEntity.setBannerStatus(Constant.AFFICHE_STATUS_PUBLISHED);
		bannerInfoEntity.setBasicModelProperty((String)params.get("userId"), true);
		
		// 主键非空表示编辑
		if(!StringUtils.isEmpty(bannerInfoEntity.getId())) {
			BannerInfoEntity oldBannerInfoEntity = bannerInfoRepository.findOne(bannerInfoEntity.getId());
			if(oldBannerInfoEntity == null) {
				log.error("未找到活动信息，活动:{}", params.toString());
				throw new SLException("未找到活动信息");
			}
			oldBannerInfoEntity.setBannerTitle(bannerInfoEntity.getBannerTitle());
			oldBannerInfoEntity.setBannerContent(bannerInfoEntity.getBannerContent());
			oldBannerInfoEntity.setBannerUrl(bannerInfoEntity.getBannerUrl());
			oldBannerInfoEntity.setBannerStatus(bannerInfoEntity.getBannerStatus());
			oldBannerInfoEntity.setBannerType(bannerInfoEntity.getBannerType());
			oldBannerInfoEntity.setTradeType(bannerInfoEntity.getTradeType());
			oldBannerInfoEntity.setIsRecommend(bannerInfoEntity.getIsRecommend());
			oldBannerInfoEntity.setBasicModelProperty((String)params.get("userId"), false);
		}
		else {
			bannerInfoEntity = bannerInfoRepository.save(bannerInfoEntity);
		}
		
		return new ResultVo(true, "保存活动成功", bannerInfoEntity.getId());
	}

	@Cacheable(value="slcf_appactivityurl",key="'appactivityurl_' + #params['bannerType']", cacheManager="redis")
	@Override
	public ResultVo queryActivityUrl(Map<String, Object> params)
			throws SLException {
		params.put("bannerStatus", Constant.AFFICHE_STATUS_PUBLISHED);
		params.put("tradeType", Constant.BANNER_TRADE_TYPE_ACTIVITY_URL);
		return new ResultVo(true, "查询Banner列表成功", bannerInfoRepositoryCustom.queryLatestBanner(params));
	}

	@Cacheable(value="slcf_queryallbannertitle",key="'queryallbannertitle'", cacheManager="redis")
	@Override
	public ResultVo queryAllBannerTitle(Map<String, Object> params)
			throws SLException {
		return new ResultVo(true, "查询Banner列表成功", bannerInfoRepositoryCustom.queryAllBannerTitle(params));
	}
}
