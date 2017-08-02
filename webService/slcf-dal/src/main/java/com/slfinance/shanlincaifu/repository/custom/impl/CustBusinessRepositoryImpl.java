/** 
 * @(#)CustBusinessRepositoryImpl.java 1.0.0 2015年12月11日 上午9:47:57  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.CustBusinessRepositoryCustom;
import com.slfinance.shanlincaifu.utils.DateUtils;

/**   
 * 自定义客户业务数据访问实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年12月11日 上午9:47:57 $ 
 */
@Repository
public class CustBusinessRepositoryImpl implements CustBusinessRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Override
	public Page<Map<String, Object>> findRegisterReport(
			Map<String, Object> params) {
		
		StringBuilder sqlString= new StringBuilder()
		.append("  select ")
		.append("         to_char(\"summaryDate\", 'yyyy-MM-dd') \"summaryDate\", \"appSource\", ")
		.append("         \"utmSource\", \"utmMedium\", \"utmCampaign\", ")
		.append("         \"utmContent\", \"utmTerm\", \"channelNo\", ")
		.append("         \"newRegister\" ")
		.append("  from ( ")
		.append("  select trunc(cust.create_date) \"summaryDate\",  ")
		.append(String.format("        %s \"appSource\",   ", decodeAppSource("cust.cust_source")))
		.append("        t.utm_source \"utmSource\", ")
		.append("        t.utm_medium \"utmMedium\", ")
		.append("        t.utm_campaign \"utmCampaign\", ")
		.append("        t.utm_content \"utmContent\", ")
		.append("        t.utm_term \"utmTerm\", ")
		.append("        NVL(cust.channel_source, '善林财富网站') \"channelNo\", ")
		.append("        count(1) \"newRegister\" ")
		.append("  from bao_t_cust_info cust " )
		.append("  left join bao_t_device_info t on t.relate_primary = cust.id and t.trade_Type = '注册'  ")
		.append("  where cust.create_date > to_date('2015-06-01', 'yyyy-MM-dd') %s ")
		.append("   and (cust.is_recommend is null or cust.is_recommend = '否') ")  // 排除线下金牌推荐人
		.append("   and not exists ( ") // 排除线下金牌推荐人推荐的人
		.append("       select 1 ")
		.append("       from bao_t_cust_info m ")
		.append("       where cust.invite_origin_id = m.id ")
		.append("       and m.is_recommend = '是' ")
		.append("   ) ")
//		.append("   and not exists ( ") // 排除线下金牌推荐人
//		.append("       select 1 ")
//		.append("       from bao_t_cust_info s ")
//		.append("       where t.relate_primary = s.id ")
//		.append("       and s.is_recommend = '是' ")
//		.append("   ) ")
//		.append("   and not exists ( ") // 排除线下金牌推荐人推荐的人
//		.append("       select 1 ")
//		.append("       from bao_t_cust_info m, bao_t_cust_info n ")
//		.append("       where t.relate_primary = m.id ")
//		.append("       and m.invite_origin_id = n.id ")
//		.append("       and n.is_recommend = '是' ")
//		.append("   ) ")
		.append(" group by trunc(cust.create_date),  ")
		.append(decodeAppSource("cust.cust_source") + ",")
		.append("        NVL(cust.channel_source, '善林财富网站'), ")
		.append("        t.utm_source, ")
		.append("        t.utm_medium, ")
		.append("        t.utm_campaign, ")
		.append("        t.utm_content, ")
		.append("        t.utm_term ")		
		.append(" ) ")
		.append(" order by ")
		.append("         \"summaryDate\", \"appSource\", \"channelNo\", ")
		.append("         \"utmSource\", \"utmMedium\", \"utmCampaign\", ")
		.append("         \"utmContent\", \"utmTerm\"");
		
		StringBuilder whereSqlString= new StringBuilder();
		List<Object> objList=new ArrayList<Object>();
		
//		if (!StringUtils.isEmpty(params.get("tradeType"))) {
//			whereSqlString.append(" and t.trade_type = ?");
//			objList.add(params.get("tradeType"));
//		}
		
		if (!StringUtils.isEmpty(params.get("channelNo"))) {
			whereSqlString.append(" and NVL(cust.channel_source, '善林财富网站') = ?");
			objList.add(params.get("channelNo"));
		}
		
		if (!StringUtils.isEmpty(params.get("appSource"))) {
			whereSqlString.append(String.format(" and %s = ? ", decodeAppSource("cust.cust_source")));
			objList.add(((String)params.get("appSource")).toLowerCase());
		}
		
		if (!StringUtils.isEmpty(params.get("summaryDate"))) {
			whereSqlString.append(" and cust.create_date >= trunc(?) and cust.create_date < trunc(?) + 1");
			// 需将年月格式处理一下，如2015-12则转换为2015-12-1至2015-12-31
			Date summaryBeginDate = DateUtils.parseDate(params.get("summaryDate") + "-01", "yyyy-MM-dd");
			Date summaryEndDate = DateUtils.parseDate(DateUtils.getLastDay(summaryBeginDate, "yyyy-MM-dd"), "yyyy-MM-dd");
			objList.add(summaryBeginDate);
			objList.add(summaryEndDate);
		}
		
		if (!StringUtils.isEmpty(params.get("summaryDateBegin"))) {
			whereSqlString.append(" and cust.create_date >= ? "); 
			objList.add(DateUtils.parseDate((String)params.get("summaryDateBegin"), "yyyy-MM-dd HH"));//精确到小时
		}
		
		if (!StringUtils.isEmpty(params.get("summaryDateEnd"))) {
			whereSqlString.append(" and cust.create_date < ?");
			objList.add(DateUtils.getDateAfterByMinute(DateUtils.parseDate((String)params.get("summaryDateEnd"), "yyyy-MM-dd HH"), 60));//精确到小时
		}
		
		if (!StringUtils.isEmpty(params.get("utmSource"))) {
			whereSqlString.append(" and t.utm_source = ?");
			objList.add(params.get("utmSource"));
		}
		
		if (!StringUtils.isEmpty(params.get("utmMedium"))) {
			whereSqlString.append(" and t.utm_medium = ?");
			objList.add(params.get("utmMedium"));
		}
		
		return repositoryUtil.queryForPageMap(
				String.format(sqlString.toString(), whereSqlString.toString()),
				objList.toArray(), Integer.valueOf(params.get("start").toString()),
				Integer.valueOf(params.get("length").toString()));
	}

	@Override
	public int countRegisterReport(Map<String, Object> params) {
		
		StringBuilder sqlString= new StringBuilder()
		.append("  select count(1) \"total\" from ( ")
		.append("  select trunc(cust.create_date) \"summaryDate\",  ")
		.append(String.format("        %s \"appSource\",   ", decodeAppSource("cust.cust_source")))
		.append("        t.utm_source \"utmSource\", ")
		.append("        t.utm_medium \"utmMedium\", ")
		.append("        t.utm_campaign \"utmCampaign\", ")
		.append("        t.utm_content \"utmContent\", ")
		.append("        t.utm_term \"utmTerm\", ")
		.append("        NVL(cust.channel_source, '善林财富网站') \"channelNo\", ")
		.append("        count(1) \"newRegister\" ")
		.append("  from bao_t_cust_info cust " )
		.append("  left join bao_t_device_info t on t.relate_primary = cust.id and t.trade_Type = '注册' ")
		.append("  where cust.create_date > to_date('2015-06-01', 'yyyy-MM-dd') %s ")
		.append("   and (cust.is_recommend is null or cust.is_recommend = '否') ")  // 排除线下金牌推荐人
		.append("   and not exists ( ") // 排除线下金牌推荐人推荐的人
		.append("       select 1 ")
		.append("       from bao_t_cust_info m ")
		.append("       where cust.invite_origin_id = m.id ")
		.append("       and m.is_recommend = '是' ")
		.append("   ) ")
//		.append("   and not exists ( ") // 排除线下金牌推荐人
//		.append("       select 1 ")
//		.append("       from bao_t_cust_info s ")
//		.append("       where t.relate_primary = s.id ")
//		.append("       and s.is_recommend = '是' ")
//		.append("   ) ")
//		.append("   and not exists ( ") // 排除线下金牌推荐人推荐的人
//		.append("       select 1 ")
//		.append("       from bao_t_cust_info m, bao_t_cust_info n ")
//		.append("       where t.relate_primary = m.id ")
//		.append("       and m.invite_origin_id = n.id ")
//		.append("       and n.is_recommend = '是' ")
//		.append("   ) ")
		.append(" group by trunc(cust.create_date),  ")
		.append(decodeAppSource("cust.cust_source") + ",")
		.append("        NVL(cust.channel_source, '善林财富网站'), ")
		.append("        t.utm_source, ")
		.append("        t.utm_medium, ")
		.append("        t.utm_campaign, ")
		.append("        t.utm_content, ")
		.append("        t.utm_term ")	
		.append(" ) ");
		
		StringBuilder whereSqlString= new StringBuilder();
		List<Object> objList=new ArrayList<Object>();
		
//		if (!StringUtils.isEmpty(params.get("tradeType"))) {
//			whereSqlString.append(" and t.trade_type = ?");
//			objList.add(params.get("tradeType"));
//		}
		
		if (!StringUtils.isEmpty(params.get("channelNo"))) {
			whereSqlString.append(" and NVL(cust.channel_source, '善林财富网站') = ?");
			objList.add(params.get("channelNo"));
		}
		
		if (!StringUtils.isEmpty(params.get("appSource"))) {
			whereSqlString.append(String.format(" and %s = ? ", decodeAppSource("cust.cust_source")));
			objList.add(((String)params.get("appSource")).toLowerCase());
		}
		
		if (!StringUtils.isEmpty(params.get("summaryDate"))) {
			whereSqlString.append(" and cust.create_date >= trunc(?) and cust.create_date < trunc(?) + 1");
			// 需将年月格式处理一下，如2015-12则转换为2015-12-1至2015-12-31
			Date summaryBeginDate = DateUtils.parseDate(params.get("summaryDate") + "-01", "yyyy-MM-dd");
			Date summaryEndDate = DateUtils.parseDate(DateUtils.getLastDay(summaryBeginDate, "yyyy-MM-dd"), "yyyy-MM-dd");
			objList.add(summaryBeginDate);
			objList.add(summaryEndDate);
		}
		
		if (!StringUtils.isEmpty(params.get("summaryDateBegin"))) {
			whereSqlString.append(" and cust.create_date >= ? "); 
			objList.add(DateUtils.parseDate((String)params.get("summaryDateBegin"), "yyyy-MM-dd HH"));//精确到小时
		}
		
		if (!StringUtils.isEmpty(params.get("summaryDateEnd"))) {
			whereSqlString.append(" and cust.create_date < ?");
			objList.add(DateUtils.getDateAfterByMinute(DateUtils.parseDate((String)params.get("summaryDateEnd"), "yyyy-MM-dd HH"), 60));//精确到小时
		}
		
		if (!StringUtils.isEmpty(params.get("utmSource"))) {
			whereSqlString.append(" and t.utm_source = ?");
			objList.add(params.get("utmSource"));
		}
		
		if (!StringUtils.isEmpty(params.get("utmMedium"))) {
			whereSqlString.append(" and t.utm_medium = ?");
			objList.add(params.get("utmMedium"));
		}
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				String.format(sqlString.toString(), whereSqlString.toString()),
				objList.toArray());
		
		if (list == null || list.size() == 0)
			return 0;
		Map<String, Object> map = list.get(0);
		if (map == null || map.size() == 0)
			return 0;

		return Integer.valueOf(map.get("total").toString());
	}
	
	private String decodeAppSource(String cloumnName) {
		return String.format("decode(lower(%s), "
						+ "'pc端', 'web', "
						+ "'手机wap', 'wap', "
						+ "nvl(lower(%s), 'web'))", cloumnName, cloumnName);
	}

}
