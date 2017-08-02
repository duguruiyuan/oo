/** 
 * @(#)ProjectPaymentRepositoryImpl.java 1.0.0 2016年1月14日 下午8:08:41  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.ProjectPaymentRepositoryCustom;
import com.slfinance.shanlincaifu.utils.SqlCondition;

/**   
 * 自定义付款数据访问实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月14日 下午8:08:41 $ 
 */
@Repository
public class ProjectPaymentRepositoryImpl implements
		ProjectPaymentRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Override
	public List<Map<String, Object>> queryPaymentList(Map<String, Object> params) {
		
		StringBuilder sqlString = new StringBuilder()
		.append(" select s.create_date \"createDate\", s.subject_type \"tradeType\", s.trade_amount \"tradeAmount\",  ")
		.append("        s.subject_direction \"bankrollFlowDirection\" ")
		.append("  from BAO_T_PAYMENT_RECORD_INFO t, BAO_T_PAYMENT_RECORD_DETAIL s ")
		.append(" where t.id = s.pay_record_id ");
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, params);
		sqlCondition.addString("accountFlowId", "t.relate_primary")
					.addString("custId", "t.cust_id");
		
		return repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
	}

}
