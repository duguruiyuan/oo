/** 
 * @(#)BannerInfoRepositoryCustom.java 1.0.0 2015年10月19日 上午11:03:47  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;



/**   
 * Banner数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年10月19日 上午11:03:47 $ 
 */
public interface BannerInfoRepositoryCustom {

	/**
	 * 查询Banner
	 *
	 * @author  wangjf
	 * @date    2015年10月19日 上午11:04:54
	 * @param appSource
	 * @return
	 */
	Page<Map<String, Object>> queryBanner(Map<String, Object> params);
	
	/**
	 * 根据ID查询Banner
	 *
	 * @author  wangjf
	 * @date    2015年10月28日 下午3:32:58
	 * @param params
	 * @return
	 */
	Map<String, Object> queryBannerById(Map<String, Object> params);
	
	/**
	 * 查询最近Banner
	 *
	 * @author  wangjf
	 * @date    2015年10月30日 下午3:56:42
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryLatestBanner(Map<String, Object> params);
	
	/**
	 * 查询所有Banner标题
	 *
	 * @author  wangjf
	 * @date    2015年12月31日 上午11:10:49
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryAllBannerTitle(Map<String, Object> params);
}
