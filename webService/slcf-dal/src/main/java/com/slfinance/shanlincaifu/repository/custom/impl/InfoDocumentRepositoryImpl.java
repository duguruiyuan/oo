package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.InfoDocumentRepositoryCustom;
/**
 * 
 * <自定义信息披露数据访问接口实现类>
 * <功能详细描述>
 * 
 * @author  yangcheng
 * @version  [版本号, 2017年6月19日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Repository
public class InfoDocumentRepositoryImpl implements InfoDocumentRepositoryCustom{
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Override
	public Page<Map<String, Object>> backFindAllReportList(Map<String, Object> param) {
		StringBuffer sqlString= new StringBuffer()
		.append(" select t.id \"reportId\" ,t.last_update_date \"updateTime\",t.title \"reportName\",t.issue_status \"issueStatus\", ")
		.append(" t.report_time \"reportTime\" ")
		.append(" from bao_t_info_document t ")
		.append(" where t.content is null ");
		List<Object> objList = new ArrayList<Object>();
		if(!StringUtils.isEmpty(param.get("reportName"))){
			sqlString.append(" and t.report_name = ? ");
			objList.add(param.get("reportName"));
		}
		
		if(!StringUtils.isEmpty(param.get("issueStatus"))){
			sqlString.append(" and t.issue_status = ? ");
			objList.add(param.get("issueStatus"));
		}
		
		if(!StringUtils.isEmpty(param.get("reportTime"))){
			sqlString.append(" and t.report_time = ? ");
			objList.add(param.get("reportTime"));
		}
		
		sqlString.append(" order by t.last_update_date desc ");
		
		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), (int)param.get("start"), (int)param.get("length"));
	}

	@Override
	public Page<Map<String, Object>> frontFindAllReportList(Map<String, Object> param) {
		
		StringBuffer sqlString= new StringBuffer()
		.append(" select t.id \"reportId\" ,t.last_update_date \"updateTime\",t.title \"reportName\",t.issue_status \"issueStatus\",t.report_time \"reportTime\", ")
		.append(" ( ")
		.append(" select n.storage_path from bao_t_attachment_info n where n.relate_primary = t.id and n.doc_type = 'PNG' ")
		.append(" ) as png, ")
		.append(" ( ")
		.append(" select n.storage_path from bao_t_attachment_info n where n.relate_primary = t.id and n.doc_type = 'PDF' ")
		.append(" ) as pdf ")
		.append(" from bao_t_info_document t ")
		.append(" where  t.content is null  ")
		.append(" and t.issue_status='已发布' ");
		
		List<Object> objList = new ArrayList<Object>();
		
		if(!StringUtils.isEmpty(param.get("reportTime"))){
			sqlString.append(" and t.report_time = ? ");
			objList.add(param.get("reportTime"));
		}
		
		sqlString.append(" order by t.create_date desc ");
		
		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), (int)param.get("start"), (int)param.get("length"));
	}

	@Override
	public Map<String, Object> findByReportId(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<String, Object>();
		StringBuffer sqlString= new StringBuffer()
		.append(" select t.id \"reportId\",t.last_update_date \"updateTime\", t.last_update_user \"updateUser\", t.title \"reportName\", ")
		.append("  t.issue_status \"issueStatus\",a.storage_path \"reportCover\",t.report_time \"reportTime\" ")
		.append(" from bao_t_info_document t left join bao_t_attachment_info a on t.id=a.relate_primary ")
		.append(" where t.id=? ")
		.append(" and a.doc_type ='PNG'")
		.append(" and t.content is null ");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[] { (String)param.get("reportId")});
		if(list == null || list.size() == 0) {
			return result;
		}
		result.putAll(list.get(0));
		return result;
	}

	@Override
	public List<Map<String, Object>> findAllReportTime(Map<String, Object> params) {
		StringBuffer sqlString= new StringBuffer()
		.append(" select distinct t.report_time \"reportTime\" ")
		.append(" from bao_t_info_document t ")
		.append(" where t.report_time is not null ")
		.append(" order by t.report_time desc ");
		
		return repositoryUtil.queryForMap(sqlString.toString(), null);
	}
	
	@Override
	public Page<Map<String, Object>> findAllLawsAndRegulationsList(
			Map<String, Object> params) {
		StringBuffer sqlString= new StringBuffer()
		.append(" select t.id \"lawsAndRegulationsId\" ,t.last_update_date \"updateTime\",t.title \"title\", ")
		.append(" t.issue_status \"issueStatus\",t.content \"content\",a.storage_path \"lawsFile\",a.doc_type \"docType\" ")
		.append(" from bao_t_info_document t left join bao_t_attachment_info a on t.id=a.relate_primary ")
		.append(" where t.content is not null ");
		List<Object> objList = new ArrayList<Object>();
		if(!StringUtils.isEmpty(params.get("title"))){
			sqlString.append(" and t.title = ? ");
			objList.add(params.get("title"));
		}
		
		if(!StringUtils.isEmpty(params.get("issueStatus"))){
			sqlString.append(" and t.issue_status = ? ");
			objList.add(params.get("issueStatus"));
		}
		

		
		sqlString.append(" order by t.create_date desc ");
		
		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), (int)params.get("start"), (int)params.get("length"));
	}
	
	@Override
	public Page<Map<String, Object>> backFindAllLawsAndRegulationsList(
			Map<String, Object> params) {
		StringBuffer sqlString= new StringBuffer()
		.append(" select t.id \"lawsAndRegulationsId\" ,t.last_update_date \"updateTime\",t.title \"title\", ")
		.append(" t.issue_status \"issueStatus\",t.content \"content\",a.storage_path \"lawsFile\" ")
		.append(" from bao_t_info_document t left join bao_t_attachment_info a on t.id=a.relate_primary ")
		.append(" where t.content is not null ");
		List<Object> objList = new ArrayList<Object>();
		if(!StringUtils.isEmpty(params.get("title"))){
			sqlString.append(" and t.title = ? ");
			objList.add(params.get("title"));
		}
		
		if(!StringUtils.isEmpty(params.get("issueStatus"))){
			sqlString.append(" and t.issue_status = ? ");
			objList.add(params.get("issueStatus"));
		}
		

		
		sqlString.append(" order by t.last_update_date desc ");
		
		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), (int)params.get("start"), (int)params.get("length"));
	}

	@Override
	public Map<String, Object> findBylawsAndRegulationsId(
			Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		StringBuffer sqlString= new StringBuffer()
		.append(" select t.id \"lawsAndRegulationsId\",t.last_update_date \"updateTime\", t.last_update_user \"updateUser\", t.title \"title\", ")
		.append(" t.issue_status \"issueStatus\",a.storage_path \"lawsFile\",t.content \"content\",a.doc_type \"docType\" ")
		.append(" from bao_t_info_document t left join bao_t_attachment_info a on t.id=a.relate_primary ")
		.append(" where t.id=? ")
		.append(" and t.content is not null ");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[] { (String)params.get("lawsAndRegulationsId")});
		if(list == null || list.size() == 0) {
			return result;
		}
		result.putAll(list.get(0));
		return result;
	}


	
}
