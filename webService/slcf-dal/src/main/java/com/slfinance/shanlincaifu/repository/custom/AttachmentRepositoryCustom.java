/** 
 * @(#)AttachmentRepositoryCustom.java 1.0.0 2015年7月8日 下午4:35:29  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AttachmentInfoEntity;
import com.slfinance.vo.ResultVo;

/**   
 * 自定义附件数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月8日 下午4:35:29 $ 
 */
public interface AttachmentRepositoryCustom {

	/**
	 * 批量插入
	 *
	 * @author  wangjf
	 * @date    2015年7月8日 下午4:38:51
	 * @param attachmentList
	 * @return
	 * @throws SLException
	 */
	public ResultVo batchInsert(List<AttachmentInfoEntity> attachmentList) throws SLException; 
	
	/**
	 * 查询附件信息
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public List<Map<String, Object>> findListByRelatePrimary(Map<String, Object> params);
	
	/**
	 * 查询审核附件
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-03
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findAuditAttachmentInfoByLoanId(Map<String, Object> params);
	
	/**
	 * 查询审核附件
	 * @author liyy
	 * @date 2017-2-7
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryAttachmentByLoanIdInEdit(Map<String, Object> params);
}
