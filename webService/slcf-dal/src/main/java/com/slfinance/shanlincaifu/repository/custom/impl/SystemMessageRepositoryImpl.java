package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.SystemMessageRepositoryCustom;

/**   
 * 消息中心数据自定义接口实现类
 *  
 * @author  Ric.w
 */
@Repository
public class SystemMessageRepositoryImpl implements SystemMessageRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 根据用户id查询所有消息
	 */
	public Page<Map<String, Object>> findAllByReceiveCustId(Map<String, Object> param) throws Exception {
		
		StringBuffer sqlString= new StringBuffer()
		.append(" select smi.id \"id\",smi.send_title \"sendTitle\",smi.send_content \"sendContent\",smi.send_date \"sendDate\", smi.record_status \"recordStatus\", smi.create_user \"createUser\",smri.create_date \"readDate\", smri.is_read \"isRead\" ")
		.append(" from bao_t_system_message_info smi left join bao_t_system_message_read_info smri on smi.id = smri.message_id and smri.receive_cust_id = ?")
		.append(" where smi.receive_cust_id in(?,'ALL') and smi.create_date >= (select u.create_date from bao_t_cust_info u where u.id = ?) order by smi.create_date desc");
		
		List<Object> objList=new ArrayList<Object>();
		objList.add(param.get("custId"));
		objList.add(param.get("custId"));
		objList.add(param.get("custId"));
		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), Integer.parseInt((String)param.get("start")),Integer.parseInt((String)param.get("length")));
	}
	
	/**
	 * 根据用户id查询所有消息 By salesMan APP
	 * @author  lyy
	 * @date    2016年11月11日 下午7:21:10
	 */
	public Page<Map<String, Object>> findAllByReceiveCustIdOrSalesMan(Map<String, Object> param) throws Exception {
		
		StringBuffer sqlString= new StringBuffer()
		.append(" select smi.id \"id\",smi.send_title \"sendTitle\",smi.send_content \"sendContent\",smi.send_date \"sendDate\", smi.record_status \"recordStatus\", smi.create_user \"createUser\",smri.create_date \"readDate\", smri.is_read \"isRead\" ")
		.append(" from bao_t_system_message_info smi left join bao_t_system_message_read_info smri on smi.id = smri.message_id and smri.receive_cust_id = ?")
		.append(" where smi.receive_cust_id in(?,'ALL','salesMan') and smi.create_date >= (select u.create_date from bao_t_cust_info u where u.id = ?) order by smi.create_date desc");
		
		List<Object> objList=new ArrayList<Object>();
		objList.add(param.get("custId"));
		objList.add(param.get("custId"));
		objList.add(param.get("custId"));
		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), Integer.parseInt((String)param.get("start")),Integer.parseInt((String)param.get("length")));
	}

}
