/** 
 * @(#)ParamRepositoryImpl.java 1.0.0 2015年10月22日 下午4:59:10  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.ParamRepositoryCustom;
import com.slfinance.shanlincaifu.utils.SqlCondition;

/**   
 * 自定义参数数据访问类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年10月22日 下午4:59:10 $ 
 */
@Repository
public class ParamRepositoryImpl implements ParamRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Override
	public List<Map<String, Object>> findByCityIdAndBankName(
			Map<String, Object> map) throws Exception {
		
		StringBuilder strSql = new StringBuilder()
		.append(" select t.bank_name \"bankName\", t.sub_bank_name  \"subBankName\" ")
		.append(" from com_t_bank t  ")
		.append(" where city_id = ? and bank_name = ? and status = '1' ");
		
		List<Object> objList = new ArrayList<Object>();
		objList.add((String)map.get("cityId"));
		objList.add((String)map.get("bankName"));
		if(!StringUtils.isEmpty(map.get("subBankName"))) {
			strSql.append(" and t.sub_bank_name like ? ");
			objList.add("%" + (String)map.get("subBankName") + "%");
		}
		
		strSql.append(" order by sort ");

		return repositoryUtil.queryForMap(strSql.toString(), objList.toArray());
	}

	@Override
	public List<Map<String, Object>> findBankByType(Map<String, Object> params) {
		
		StringBuilder strSql = new StringBuilder()
		.append("  select t.value \"bankCode\", t.parameter_name \"bankName\"  ")
		.append("  from com_t_param t ")
		.append("  where t.status = '1' ");
		
		SqlCondition sqlCondition = new SqlCondition(strSql, params);
		sqlCondition.addString("type", "t.type");

		return repositoryUtil.queryForMap(sqlCondition.toString() ,sqlCondition.toArray());
	}

	/**
	 * 划扣公司
	 * @return
	 */
	@Override
	public List<Map<String, Object>> findThirdPayList(Map<String, Object> params) {
		SqlCondition sqlCondition = new SqlCondition(new StringBuilder(), params)
		.addSql(" SELECT t.value \"thirdPayName\", s.value \"showType\" ")
		.addSql("   FROM com_t_param t, com_t_param s  ")
		.addSql("  WHERE t.id = s.parent_id  ")
		.addSql("    AND t.STATUS = '1'  ")
		.addString("type", "t.type")
		;

		return repositoryUtil.queryForMap(sqlCondition.toString() ,sqlCondition.toArray());
	}
}
