/** 
 * @(#)AttachmentRepositoryImpl.java 1.0.0 2015年7月8日 下午4:36:10  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AttachmentInfoEntity;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.AttachmentRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SqlCondition;
import com.slfinance.vo.ResultVo;

/**   
 * 自定义附件数据访问实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月8日 下午4:36:10 $ 
 */
@Repository
public class AttachmentRepositoryImpl implements AttachmentRepositoryCustom {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Override
	public ResultVo batchInsert(List<AttachmentInfoEntity> attachmentList)
			throws SLException {
		for (int i = 0; i < attachmentList.size(); i++) {
			manager.persist(attachmentList.get(i));
		}
		manager.flush();
		manager.clear();
		
		return new ResultVo(true);
	}

	@Override
	public List<Map<String, Object>> findListByRelatePrimary(Map<String, Object> params) {

		StringBuilder sql = new StringBuilder()
		.append(" select btai.id \"attachmentId\", btai.relate_primary \"relatePrimary\", btai.attachment_type \"attachmentType\", ")
		.append("        btai.attachment_name \"attachmentName\", btai.storage_path \"storagePath\", btai.show_type \"showType\" ")
		.append("   from bao_t_attachment_info btai ")
		.append("  where btai.record_status = '" + Constant.VALID_STATUS_VALID + "' ");
		
		SqlCondition sqlCondition = new SqlCondition(sql, params);
		sqlCondition.addString("projectId", "btai.relate_primary")
		            .addString("showType", "btai.show_type")
		            .addList("relatePrimaryList", "btai.relate_primary");

		return repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
	}

	@Override
	public List<Map<String, Object>> findAuditAttachmentInfoByLoanId(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
				.append("  select att.id                \"attachmentId\", ")
				.append("         att.attachment_type   \"attachmentType\", ")
				.append("         att.attachment_name   \"attachmentName\", ")
				.append("         att.storage_path      \"storagePath\" ,")
				.append("         att.relate_primary    \"relateId\" ")
				.append("         , au.apply_type    \"applyType\" ")
				.append("  from bao_t_audit_info au ")
				.append("  inner join bao_t_attachment_info att on att.relate_primary = au.id ")
				.append("  where att.show_type = ?  ")
				.append("    and au.relate_primary = ? ")
				.append("    and au.apply_type not in ('"+Constant.OPERATION_TYPE_60+"', '"+Constant.OPERATION_TYPE_06+"')  ");
		List<Object> args = new ArrayList<Object>();
		if(!StringUtils.isEmpty((String) params.get("showType"))) //若果传可暂时类型就用传的， 否者用外面类型
			args.add(params.get("showType"));
		else 
			args.add(Constant.SHOW_TYPE_EXTERNAL);
		
		args.add(params.get("loanId"));
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}
	
	@Override
	public List<Map<String, Object>> queryAttachmentByLoanIdInEdit(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT * FROM ( ")
		.append("  select att.id                \"attachmentId\", ")
		.append("         att.attachment_type   \"attachmentType\", ")
		.append("         att.attachment_name   \"attachmentName\", ")
		.append("         att.storage_path      \"storagePath\" ,")
		.append("         att.relate_primary    \"relateId\" ")
		.append("         , au.apply_type    \"applyType\" ")
		.append("         , att.SHOW_TYPE    \"showType\" ")
		.append("         , att.MEMO         \"memo\" ")
		.append("  from bao_t_audit_info au ")
		.append("  inner join bao_t_attachment_info att on att.relate_primary = au.id ")
		.append("  where att.show_type = '外部'  ")
		.append("    and au.relate_primary = ? ")
		.append("    and au.apply_type not in ('"+Constant.OPERATION_TYPE_60+"', '"+Constant.OPERATION_TYPE_06+"')  ")
		.append(" UNION ALL ")
		.append("  select att.id                \"attachmentId\", ")
		.append("         att.attachment_type   \"attachmentType\", ")
		.append("         att.attachment_name   \"attachmentName\", ")
		.append("         att.storage_path      \"storagePath\" ,")
		.append("         att.relate_primary    \"relateId\" ")
		.append("         , au.apply_type    \"applyType\" ")
		.append("         , att.SHOW_TYPE    \"showType\" ")
		.append("         , att.MEMO         \"memo\" ")
		.append("  from bao_t_audit_info au ")
		.append("  inner join bao_t_attachment_info att on att.relate_primary = au.id ")
		.append("  where att.show_type = '内部'  ")
		.append("    and au.relate_primary = ? ")
		.append("    and au.apply_type not in ('"+Constant.OPERATION_TYPE_60+"', '"+Constant.OPERATION_TYPE_06+"')  ")
		.append("    and att.ID NOT IN ( ")
		.append("                select att.MEMO ") // 内部排除外部的
		.append("                  from bao_t_audit_info au ")
		.append("            inner join bao_t_attachment_info att on att.relate_primary = au.id ")
		.append("    	          WHERE att.MEMO IS NOT NULL ")
		.append("    	            AND att.show_type = '外部' ")
		.append("                   and au.relate_primary = ? ")
		.append("                   and au.apply_type not in ('"+Constant.OPERATION_TYPE_60+"', '"+Constant.OPERATION_TYPE_06+"')  ")
		.append("   ) ")
		.append(" ) ")
		;
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("loanId"));
		args.add(params.get("loanId"));
		args.add(params.get("loanId"));
		
		sql.append(" order by decode(\"showType\", '外部', 1, '内部', 2, 99), \"attachmentType\" ");
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}

}
