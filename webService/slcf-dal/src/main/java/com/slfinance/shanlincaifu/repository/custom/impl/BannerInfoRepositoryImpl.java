/** 
 * @(#)BannerInfoRepositoryImpl.java 1.0.0 2015年10月19日 上午11:05:37  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.BannerInfoRepositoryCustom;

/**   
 * Banner实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年10月19日 上午11:05:37 $ 
 */
@Repository
public class BannerInfoRepositoryImpl implements BannerInfoRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public Page<Map<String, Object>> queryBanner(Map<String, Object> params) {
		StringBuilder sqlString= new StringBuilder()
		.append("  select t.ID \"id\", banner_title \"bannerTitle\", ")
		.append("  banner_url \"bannerUrl\", banner_sort \"bannerSort\", ")
		.append("  banner_type \"bannerType\", trade_type \"tradeType\", ")
		.append("  app_source \"appSource\", banner_status \"bannerStatus\", ")
		.append("  m.user_name \"createUser\", t.LAST_UPDATE_DATE \"createDate\", ")
		.append("  s.storage_path || '/' ||s.attachment_name \"bannerImagePath\", ")
		.append("  t.banner_content \"bannerContent\", t.is_recommend \"isRecommend\" ")
		.append("  from bao_t_banner_info t ")
		.append("  left join bao_t_attachment_info s on s.relate_primary = t.id ")
		.append("  left  join com_t_user m on m.id = t.LAST_UPDATE_USER ")
		.append("  where t.banner_status != '已删除' ");
		
		List<Object> objList = new ArrayList<Object>();

		if (!StringUtils.isEmpty(params.get("bannerTitle"))) {
			sqlString.append(" and t.banner_title = ?");
			objList.add(params.get("bannerTitle").toString());
		}

		if (!StringUtils.isEmpty(params.get("appSource"))) {
			sqlString.append(" and t.app_source = ?");
			objList.add(params.get("appSource").toString());
		}
		
		if (!StringUtils.isEmpty(params.get("bannerStatus"))) {
			sqlString.append(" and t.banner_status = ?");
			objList.add(params.get("bannerStatus").toString());
		}
		
		if (!StringUtils.isEmpty(params.get("tradeType"))) {
			sqlString.append(" and t.trade_type = ?");
			objList.add(params.get("tradeType").toString());
		}
		
		if (!StringUtils.isEmpty(params.get("bannerType"))) {
			sqlString.append(" and t.banner_type = ?");
			objList.add(params.get("bannerType").toString());
		}
		
		sqlString.append("order by t.create_date desc, banner_sort asc");

		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), Integer.valueOf(params.get("start").toString()),
				Integer.valueOf(params.get("length").toString()));
	}

	@Override
	public Map<String, Object> queryBannerById(Map<String, Object> params) {
		
		StringBuilder sqlString= new StringBuilder()
		.append("  select t.ID \"id\", banner_title \"bannerTitle\", ")
		.append("  banner_url \"bannerUrl\", banner_sort \"bannerSort\", ")
		.append("  banner_type \"bannerType\", trade_type \"tradeType\", ")
		.append("  app_source \"appSource\", banner_status \"bannerStatus\", ")
		.append("  m.user_name \"createUser\", t.create_date \"createDate\", ")
		.append("  s.storage_path || '/' ||s.attachment_name \"bannerImagePath\", ")
		.append("  t.banner_content \"bannerContent\", t.is_recommend \"isRecommend\", ")
		.append("  t.is_share \"isShare\" ")
		.append("  from bao_t_banner_info t ")
		.append("  left join bao_t_attachment_info s on s.relate_primary = t.id ")
		.append("  left  join com_t_user m on m.id = t.create_user ")
		.append("  where t.id = ? ");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[] { params.get("id").toString()});
		Map<String, Object> result = new HashMap<String, Object>();
		if(list == null || list.size() > 0) {
			result = list.get(0);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> queryLatestBanner(
			Map<String, Object> params) {
		StringBuilder sqlString= new StringBuilder()
		.append("  select t.ID \"id\", banner_title \"bannerTitle\", ")
		.append("  banner_url \"bannerUrl\", banner_sort \"bannerSort\", ")
		.append("  banner_type \"bannerType\", trade_type \"tradeType\", ")
		.append("  app_source \"appSource\", banner_status \"bannerStatus\", ")
		.append("  m.user_name \"createUser\", t.create_date \"createDate\", ")
		.append("  s.storage_path || '/' ||s.attachment_name \"bannerImagePath\", ")
		.append("  t.is_share \"isShare\", ")
		.append("  t.banner_content \"bannerContent\", t.is_recommend \"isRecommend\" ")
		.append("  from bao_t_banner_info t ")
		.append("  left join bao_t_attachment_info s on s.relate_primary = t.id ")
		.append("  left  join com_t_user m on m.id = t.create_user ")
		.append("  where 1 = 1 ");
//		.append("  and t.create_date = ( ")
//		.append("     select max(m.create_date) ")
//		.append("     from bao_t_banner_info m ")
//		.append("     where m.app_source = t.app_source ")
//		.append("     and m.banner_sort = t.banner_sort ")
//		.append(" ) ");
		
		List<Object> objList = new ArrayList<Object>();

		if (!StringUtils.isEmpty(params.get("bannerTitle"))) {
			sqlString.append(" and t.banner_title = ?");
			objList.add(params.get("bannerTitle").toString());
		}

		if (!StringUtils.isEmpty(params.get("appSource"))) {
			sqlString.append(" and t.app_source = ?");
			objList.add(params.get("appSource").toString());
		}
		
		if (!StringUtils.isEmpty(params.get("bannerStatus"))) {
			sqlString.append(" and t.banner_status = ?");
			objList.add(params.get("bannerStatus").toString());
		}
		
		if (!StringUtils.isEmpty(params.get("tradeType"))) {
			sqlString.append(" and t.trade_type = ?");
			objList.add(params.get("tradeType").toString());
		}
		
		if (!StringUtils.isEmpty(params.get("bannerType"))) {
			sqlString.append(" and t.banner_type = ?");
			objList.add(params.get("bannerType").toString());
		}
		
		sqlString.append(" order by banner_sort asc");

		return repositoryUtil.queryForMap(sqlString.toString(), objList.toArray());
	}

	@Override
	public List<Map<String, Object>> queryAllBannerTitle(
			Map<String, Object> params) {
		
		StringBuilder sqlString = new StringBuilder()
		.append(" select distinct t.banner_title \"bannerTitle\" ")
		.append(" from bao_t_banner_info t  ")
		.append(" where t.trade_type in ('首页', '活动地址')  ")
		.append(" and t.banner_status = '已发布'  ");

		return repositoryUtil.queryForMap(sqlString.toString(), null);
	}

}
