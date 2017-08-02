package com.slfinance.shanlincaifu.repository.custom.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.slfinance.shanlincaifu.entity.EmployeeLoadInfoEntity;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.EmployeeLoadInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SharedUtil;
import com.slfinance.shanlincaifu.utils.SqlCondition;
import com.slfinance.vo.ResultVo;

@Repository
public class EmployeeLoadInfoRepositoryImpl implements EmployeeLoadInfoRepositoryCustom {

	@Autowired
	RepositoryUtil repositoryUtil; 
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public Page<Map<String, Object>> queryAllImportEmployeeLoadInfo(Map<String, Object> params) {
		StringBuffer SqlString = new StringBuffer()
				.append("  select e.create_date      \"createDate\",  ")
				.append("         u.user_name        \"createUser\",  ")
				.append("         e.import_batch_no  \"importBatchNo\"  ")
				.append("   from bao_t_employee_load_info e   ")
				.append("   inner join com_t_user u on u.id = e.create_user ")
				.append("   where 1 = 1  ")
				.append("   group by e.create_date, e.import_batch_no, u.user_name  ")
				.append("  order by e.import_batch_no desc  ");
		
		return repositoryUtil.queryForPageMap(SqlString.toString(), new Object[]{}, Integer.valueOf(params.get("start").toString()), Integer.valueOf(params.get("length").toString()));
	}
	
	@Override
	public List<Map<String, Object>> queryAllEmployeeLoadInfo() {
		return repositoryUtil.queryForMap("select t.credentials_code \"idcard\", t.emp_name \"name\" from bao_t_employee_load_info t", null);
	}

	@Override
	public Page<Map<String, Object>> queryAllEmployeeLoadInfo(Map<String, Object> params) {
		StringBuffer SqlString = new StringBuffer()
		.append("   select t.emp_no           \"empNo\", ")
		.append("          t.emp_name         \"empName\", ")
		.append("          t.job_status       \"jobStatus\", ")
		.append("          t.credentials_code \"credentialsCode\", ")
		.append("          t.province         \"province\", ")
		.append("          t.city             \"city\", ")
		.append("          t.team1            \"term1\", ")
		.append("          t.team4            \"term4\", ")
		.append("          t.team3            \"term3\", ")
		.append("          t.team2            \"term2\", ")
		.append("          t.job_position     \"jobPosition\", ")
		.append("          t.job_level        \"jobLevel\", ")
		.append("          t.cust_manager_name \"custManageName\", ")
		.append("          t.cust_manager_code  \"custManageCode\", ")
		.append("          t.id                \"id\" ")
		.append("   from bao_t_employee_load_info t ")
		.append(" where 1 = 1 %s ")
		.append(" order by t.credentials_code desc ");
		
		StringBuilder whereSqlString= new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(whereSqlString, params);
		sqlCondition.addString("empName", "t.emp_name")
					.addString("credentialsCode", "t.credentials_code")
					.addString("province", "t.province")
					.addString("city", "t.city")
					.addString("custManageCode", "t.cust_manage_code")
					.addString("term4", "t.team4")
					.addString("term2", "t.team2")
					.addString("importBatchNo", "t.import_batch_no");
		
		return repositoryUtil.queryForPageMap(String.format(SqlString.toString(), sqlCondition.toString()), sqlCondition.toArray(), 
				Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}

	@Override
	public ResultVo handleOriginalData() {
		//0> 原始数据处理 将部门格式化到团队
		StringBuilder sql = new StringBuilder()
				.append(" update bao_t_employee_load_info t set t.team3 = t.team1 where t.team1 like '%大团队%' and t.team4 is null and t.team3 is null ");
		jdbcTemplate.update(sql.toString(), new Object[]{}); 
		sql = new StringBuilder()
				.append(" update bao_t_employee_load_info t set t.team4 = t.team1 where t.team1 like '%团队%' and t.team4 is null and t.team3 is null ");
		jdbcTemplate.update(sql.toString(), new Object[]{});
				
		//1> 生成1级部门
		sql = new StringBuilder()
		.append("   insert into bao_t_dept_info ")
		.append("   (id, dept_name, dept_manager_id,  ")
		.append("    parent_id, dept_no, dept_type,  ")
		.append("    pro_name, city_name, record_status,  ")
		.append("    create_user, create_date, last_update_user,  ")
		.append("    last_update_date, version, memo) ")
		.append("   select DEPT_NUMBER_SEQ.Nextval, t.team2, null,  ")
		.append("    '0', null, '01',  ")
		.append("    t.province, t.city, '有效',  ")
		.append("    'root', sysdate, 'root',  ")
		.append("    sysdate, 0, 'new01' ")
		.append("   from (select distinct province, city, team2 from bao_t_employee_load_info d where d.status = '未处理' and d.record_status = '有效')t  ");
//		.append("   where not exists (select * from bao_t_dept_info x where x.dept_name = t.team2 and t.province = x.pro_name and t.city = x.city_name and x.dept_type = '01' ) ");
		jdbcTemplate.update(sql.toString(), new Object[]{}); 
		
		//1-1> 更新原始数据的 部门id 部门级别
		sql = new StringBuilder()
				.append(" update bao_t_employee_load_info t ")
				.append(" set (t.team_id, t.dept_type) = ( ")
				.append("     select id, s.dept_type ")
				.append("     from bao_t_dept_info s ")
				.append("     where memo = 'new01' ")
				.append("     and s.pro_name = t.province ")
				.append("     and s.city_name = t.city ")
				.append("     and s.dept_name = t.team2 ")
				.append(" ) ")
				.append(" where  ")
				.append(" t.status = '未处理' ")
				.append(" and exists ( ")
				.append("     select 1 ")
				.append("     from bao_t_dept_info s ")
				.append("     where memo = 'new01' ")
				.append("     and s.pro_name = t.province ")
				.append("     and s.city_name = t.city ")
				.append("     and s.dept_name = t.team2 ")
				.append(" )");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		
		//2> 生成编号
		sql = new StringBuilder()
				.append("     update bao_t_dept_info ")
				.append("       set dept_no = lpad(id, 4, 0) ")
				.append(" 	  where memo = 'new01' ");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		
		//3> 生成二级部门  
		sql = new StringBuilder()
		.append("  insert into bao_t_dept_info ")
		.append("  (id, dept_name, dept_manager_id,  ")
		.append("   parent_id, dept_no, dept_type,  ")
		.append("   pro_name, city_name, record_status,  ")
		.append("   create_user, create_date, last_update_user,  ")
		.append("   last_update_date, version, memo) ")
		.append("  select DEPT_NUMBER_SEQ.Nextval, t.team3, null,  ")
		.append("   s.id, s.dept_no, '02',  ")
		.append("   t.province, t.city, '有效',  ")
		.append("   'root', sysdate, 'root',  ")
		.append("   sysdate, 0, 'new02' ")
		.append("  from (select distinct province, city, team2, team3 from bao_t_employee_load_info d where decode(team3, '——', null, team3) is not null and d.status = '未处理' ) t, bao_t_dept_info s ")
		.append("  where t.province = s.pro_name and t.city = s.city_name and t.team2 = s.dept_name and s.memo = 'new01' ");
//		.append("	AND not exists (select * from bao_t_dept_info x where x.dept_name = t.team3 and t.province = x.pro_name and t.city = x.city_name and x.dept_type = '02' ) ");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		
		//3-1> 更新原始数据的 部门id 部门级别
		sql = new StringBuilder()
		.append(" update bao_t_employee_load_info t ")
		.append(" set (t.team_id, t.dept_type) = ( ")
		.append("     select s1.id, s1.dept_type ")
		.append("     from bao_t_dept_info s1, bao_t_dept_info s2 ")
		.append("     where s1.memo = 'new02' ")
		.append("     and s2.memo = 'new01' ")
		.append("     and s1.parent_id = s2.id ")
		.append("     and t.team2 = s2.dept_name ")
		.append("     and t.team3 = s1.dept_name ")
		.append(" ) ")
		.append(" where  ")
		.append(" t.status = '未处理'  ")
		.append(" and exists ( ")
		.append("     select s1.id ")
		.append("     from bao_t_dept_info s1, bao_t_dept_info s2 ")
		.append("     where s1.memo = 'new02' ")
		.append("     and s2.memo = 'new01' ")
		.append("     and s1.parent_id = s2.id ")
		.append("     and t.team2 = s2.dept_name ")
		.append("     and t.team3 = s1.dept_name ")
		.append(" ) ");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		
		//4>生成编号
		sql = new StringBuilder()
				.append("     update bao_t_dept_info ")
				.append("       set dept_no = dept_no || lpad(id, 4, 0) ")
				.append(" 	  where memo = 'new02' ");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		
		//5> 生成三级部门
		sql = new StringBuilder()
		.append(" insert into bao_t_dept_info ")
		.append("  (id, dept_name, dept_manager_id,  ")
		.append("   parent_id, dept_no, dept_type,  ")
		.append("   pro_name, city_name, record_status,  ")
		.append("   create_user, create_date, last_update_user,  ")
		.append("   last_update_date, version, memo) ")
		.append("  select DEPT_NUMBER_SEQ.Nextval, t.team4, null,  ")
		.append("   s.id, s.dept_no, '03',  ")
		.append("   t.province, t.city, '有效',  ")
		.append("   'root', sysdate, 'root',  ")
		.append("   sysdate, 0, 'new03' ")
		.append("  from (select distinct province, city, team2, team3, team4 from bao_t_employee_load_info d where d.status = '未处理'and decode(team4, '——', null, team4) is not null)t,  ")
		.append("  (select s1.id, s1.dept_no, s2.dept_name team2, s1.dept_name team3 from bao_t_dept_info s1, bao_t_dept_info s2 where s1.parent_id = s2.id) s ")
		.append("  where t.team3 = s.team3 and t.team2 = s.team2 ");
//		.append("	and not exists (select * from bao_t_dept_info x where x.dept_name = t.team4 and x.pro_name = t.province and x.city_name = t.city and x.dept_type = '03' ) ");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		
		//5-1> 更新原始数据的 部门id 部门级别
		sql = new StringBuilder()
		.append("  update bao_t_employee_load_info t ")
		.append("  set (t.team_id, t.dept_type) = ( ")
		.append("     select s1.id, s1.dept_type ")
		.append("     from bao_t_dept_info s1, bao_t_dept_info s2, bao_t_dept_info s3 ")
		.append("     where s1.memo = 'new03' ")
		.append("     and s2.memo = 'new02' ")
		.append("     and s3.memo = 'new01' ")
		.append("     and s1.parent_id = s2.id ")
		.append("     and s2.parent_id = s3.id ")
		.append("     and t.team2 = s3.dept_name ")
		.append("     and t.team3 = s2.dept_name ")
		.append("     and t.team4 = s1.dept_name ")
		.append("  )   ")
		.append("  where  ")
		.append("  t.status = '未处理' ")
		.append("  and exists ( ")
		.append("     select s1.id ")
		.append("     from bao_t_dept_info s1, bao_t_dept_info s2, bao_t_dept_info s3 ")
		.append("     where s1.memo = 'new03' ")
		.append("     and s2.memo = 'new02' ")
		.append("     and s3.memo = 'new01' ")
		.append("     and s1.parent_id = s2.id ")
		.append("     and s2.parent_id = s3.id ")
		.append("     and t.team2 = s3.dept_name ")
		.append("     and t.team3 = s2.dept_name ")
		.append("     and t.team4 = s1.dept_name ")
		.append("  ) ");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		
		//6> 生成编号
		sql = new StringBuilder()
				.append("     update bao_t_dept_info ")
				.append("       set dept_no = dept_no||lpad(id, 4, 0) ")
				.append(" 	  where memo = 'new03' ");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		
		//7> 生成三级部门(无二级部门的情况)
		sql = new StringBuilder()
		.append("  insert into bao_t_dept_info ")
		.append("  (id, dept_name, dept_manager_id,  ")
		.append("   parent_id, dept_no, dept_type,  ")
		.append("   pro_name, city_name, record_status,  ")
		.append("   create_user, create_date, last_update_user,  ")
		.append("   last_update_date, version, memo) ")
		.append("  select DEPT_NUMBER_SEQ.Nextval, t.team4, null,  ")
		.append("   s.id, s.dept_no, '04',  ")
		.append("   t.province, t.city, '有效',  ")
		.append("   'root', sysdate, 'root',  ")
		.append("   sysdate, 0, 'new04' ")
		.append("  from (select distinct province, city, team2, team3, team4 from bao_t_employee_load_info d where d.status = '未处理'and decode(team4, '——', null, team4) is not null and decode(team3, '——', null, team3) is null)t, bao_t_dept_info s ")
		.append("  where t.province = s.pro_name and t.city = s.city_name and t.team2 = s.dept_name ");
//		.append("   and not exists (select * from bao_t_dept_info x where x.dept_name = t.team4 and x.pro_name = t.province and x.city_name = t.city and x.dept_type = '04' ) ");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		
		//7-1> 更新原始数据的 部门id 部门级别
		sql = new StringBuilder()
		.append(" update bao_t_employee_load_info t ")
		.append(" set (t.team_id, t.dept_type) = ( ")
		.append("     select s1.id, s1.dept_type ")
		.append("     from bao_t_dept_info s1, bao_t_dept_info s2 ")
		.append("     where s1.memo = 'new04' ")
		.append("     and s2.memo = 'new01' ")
		.append("     and s1.parent_id = s2.id ")
		.append("     and t.team2 = s2.dept_name ")
		.append("     and t.team4 = s1.dept_name ")
		.append(" )")
		.append(" where  ")
		.append(" t.status = '未处理' ")
		.append(" and exists ( ")
		.append("     select s1.id, s1.dept_type ")
		.append("     from bao_t_dept_info s1, bao_t_dept_info s2 ")
		.append("     where s1.memo = 'new04' ")
		.append("     and s2.memo = 'new01' ")
		.append("     and s1.parent_id = s2.id ")
		.append("     and t.team2 = s2.dept_name ")
		.append("     and t.team4 = s1.dept_name ")
		.append(" ) ");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		
		//8> 生成编号
		sql = new StringBuilder()
				.append("     update bao_t_dept_info ")
				.append("       set dept_no = dept_no||lpad(id, 4, 0) ")
				.append(" 	  where memo = 'new04' ");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		
		//9> 生成用户数据
		sql = new StringBuilder()
		.append("  insert into bao_t_employee_info ")
		.append("   (id, emp_no, emp_name, job_status,  ")
		.append("    credentials_type, credentials_code, dept_id,  ")
		.append("    job_position, job_level, record_status,  ")
		.append("    create_user, create_date, last_update_user,  ")
		.append("    last_update_date, version, memo) ")
		.append("  select  ")
		.append("   t.id, t.emp_no, t.emp_name, t.job_status,  ")
		.append("   t.credentials_type, t.credentials_code, t.team_id,  ")
		.append("   t.job_position, t.job_level, '有效',  ")
		.append("   'root', sysdate, 'root',  ")
		.append("   sysdate, 0, 'new' ")
		.append("  from bao_t_employee_load_info t ")
		.append("  where t.status = '未处理'");
//		.append("	and not exists (select * from bao_t_employee_info e where e.emp_no = t.emp_no and e.emp_name = t.emp_name and t.credentials_code = e.credentials_code)");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		//9-0>清楚memo
		sql = new StringBuilder()
				.append("update bao_t_dept_info set memo = null where memo in ('new01','new02','new03','new04') ");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		
		//9-1>更新用户数据的客户表id
		sql = new StringBuilder()
				.append("update bao_t_employee_info e set e.cust_id = (select c.id from bao_t_cust_info c where c.credentials_code = e.credentials_code) ");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		
		//10> 生成营业部经理
		//10- 1>营业部经理
		sql = new StringBuilder()
		.append(" update bao_t_dept_info a ")
		.append(" set a.dept_manager_id = ( ")
		.append("     select id ")
		.append("     from bao_t_employee_info s, ")
		.append("     (select distinct t.emp_name, t.credentials_code ")
		.append("     from bao_t_employee_load_info t  ")
		.append("     where t.status = '未处理' ")
		.append("     and t.job_position like '%营业部经理%') m ")
		.append("     where s.memo = 'new' ")
		.append("     and s.credentials_code = m.credentials_code ")
		.append("     and s.emp_name = m.emp_name ")
		.append("     and a.id = s.dept_id ")
		.append(" ) ")
		.append(" where a.dept_manager_id is null and exists ( ")
		.append("     select id ")
		.append("     from bao_t_employee_info s, ")
		.append("     (select distinct t.emp_name, t.credentials_code ")
		.append("     from bao_t_employee_load_info t  ")
		.append("     where t.status = '未处理' ")
		.append("     and t.job_position like '%营业部经理%') m ")
		.append("     where s.memo = 'new' ")
		.append("     and s.credentials_code = m.credentials_code ")
		.append("     and s.emp_name = m.emp_name ")
		.append("     and a.id = s.dept_id ")
		.append(" ) ");
		jdbcTemplate.update(sql.toString(), new Object[]{});	
		
		//10-2> 店长
		sql = new StringBuilder()
		.append(" update bao_t_dept_info a ")
		.append(" set a.dept_manager_id = ( ")
		.append("     select id ")
		.append("     from bao_t_employee_info s, ")
		.append("     (select distinct t.emp_name, t.credentials_code ")
		.append("     from bao_t_employee_load_info t  ")
		.append("     where t.status = '未处理' ")
		.append("     and t.job_position like '%店长%') m ")
		.append("     where s.memo = 'new' ")
		.append("     and s.credentials_code = m.credentials_code ")
		.append("     and s.emp_name = m.emp_name ")
		.append("     and a.id = s.dept_id ")
		.append(" ) ")
		.append(" where a.dept_manager_id is null and exists ( ")
		.append("     select id ")
		.append("     from bao_t_employee_info s, ")
		.append("     (select distinct t.emp_name, t.credentials_code ")
		.append("     from bao_t_employee_load_info t  ")
		.append("     where t.status = '未处理' ")
		.append("     and t.job_position like '%店长%') m ")
		.append("     where s.memo = 'new' ")
		.append("     and s.credentials_code = m.credentials_code ")
		.append("     and s.emp_name = m.emp_name ")
		.append("     and a.id = s.dept_id ")
		.append(" ) ");
		jdbcTemplate.update(sql.toString(), new Object[]{});	
		
		//10-3大团队经理
		sql = new StringBuilder()
		.append(" update bao_t_dept_info a ")
		.append(" set a.dept_manager_id = ( ")
		.append("     select id ")
		.append("     from bao_t_employee_info s, ")
		.append("     (select distinct t.emp_name, t.credentials_code ")
		.append("     from bao_t_employee_load_info t  ")
		.append("     where t.status = '未处理' ")
		.append("     and t.job_position like '%大团队经理%') m ")
		.append("     where s.memo = 'new' ")
		.append("     and s.credentials_code = m.credentials_code ")
		.append("     and s.emp_name = m.emp_name ")
		.append("     and a.id = s.dept_id ")
		.append(" ) ")
		.append(" where a.dept_manager_id is null and exists ( ")
		.append("     select id ")
		.append("     from bao_t_employee_info s, ")
		.append("     (select distinct t.emp_name, t.credentials_code ")
		.append("     from bao_t_employee_load_info t  ")
		.append("     where t.status = '未处理' ")
		.append("     and t.job_position like '%大团队经理%') m ")
		.append("     where s.memo = 'new' ")
		.append("     and s.credentials_code = m.credentials_code ")
		.append("     and s.emp_name = m.emp_name ")
		.append("     and a.id = s.dept_id ")
		.append(" ) ");
		jdbcTemplate.update(sql.toString(), new Object[]{});	
		
		//10-4>团队经理
		sql = new StringBuilder()
		.append(" update bao_t_dept_info a ")
		.append(" set a.dept_manager_id = ( ")
		.append("     select id ")
		.append("     from bao_t_employee_info s, ")
		.append("     (select distinct t.emp_name, t.credentials_code ")
		.append("     from bao_t_employee_load_info t  ")
		.append("     where t.status = '未处理' ")
		.append("     and t.job_position like '%团队经理%'  ")
		.append("     and t.job_position not like '%大团队经理%') m ")
		.append("     where s.memo = 'new' ")
		.append("     and s.credentials_code = m.credentials_code ")
		.append("     and s.emp_name = m.emp_name ")
		.append("     and a.id = s.dept_id ")
		.append(" ) ")
		.append(" where a.dept_manager_id is null and exists ( ")
		.append("     select id ")
		.append("     from bao_t_employee_info s, ")
		.append("     (select distinct t.emp_name, t.credentials_code ")
		.append("     from bao_t_employee_load_info t  ")
		.append("     where t.status = '未处理' ")
		.append("     and t.job_position like '%团队经理%'  ")
		.append("     and t.job_position not like '%大团队经理%') m ")
		.append("     where s.memo = 'new' ")
		.append("     and s.credentials_code = m.credentials_code ")
		.append("     and s.emp_name = m.emp_name ")
		.append("     and a.id = s.dept_id ")
		.append(" ) ");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		
		sql = new StringBuilder("update bao_t_employee_info set memo = null where memo = 'new'");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		
		//9-3>更新原始数据的未处理为已处理
		sql = new StringBuilder()
				.append(" update bao_t_employee_load_info l set l.status = '已处理' where l.status = '未处理' ");
		jdbcTemplate.update(sql.toString(), new Object[]{});
		
		return new ResultVo(true, "执行成功！");
	}

	/**
	 * 批量插入原始数据
	 */
	@Override
	public ResultVo batchImportDate(final List<EmployeeLoadInfoEntity> list, String importBatchNo, final String userId) {
		//1-0> 删除批次号相同的部门数据
		StringBuilder sql = new StringBuilder()
				.append(" delete bao_t_dept_info d where exists ( ")
				.append(" 		select * from bao_t_employee_info e, bao_t_employee_load_info l where e.dept_id = d.id  and l.id = e.id and l.import_batch_no = ? ")
				.append(" ) ");
		List<Object> objList = Lists.newArrayList();
		objList.add(importBatchNo);
		jdbcTemplate.update(sql.toString(), objList.toArray());
		
		//1-1> 把批次号相同的数据清除 清除员工表
		sql = new StringBuilder()
				.append(" delete bao_t_employee_info e where exists ( ")
				.append("  		select * from bao_t_employee_load_info l where l.id = e.id and l.import_batch_no = ? ")
				.append(" )");
		jdbcTemplate.update(sql.toString(), objList.toArray());
		
		//1-2> 把批次号相同的原始数据清除
		sql = new StringBuilder()
				.append(" delete bao_t_employee_load_info l where l.import_batch_no = ? ");
		jdbcTemplate.update(sql.toString(), objList.toArray());
		
		//批量插入原始数据
		sql = new StringBuilder()
				.append(" insert into bao_t_employee_load_info ")
				.append("  (ID, EMP_NO, EMP_NAME, JOB_STATUS,")
				.append(" 	CREDENTIALS_TYPE, CREDENTIALS_CODE, PROVINCE, CITY, ")
				.append(" 	TEAM1, TEAM4, TEAM3, TEAM2, ")
				.append(" 	JOB_POSITION, JOB_LEVEL, CUST_MANAGER_NAME, CUST_MANAGER_CODE, ")
				.append(" 	STATUS, TEAM_ID, DEPT_TYPE, IMPORT_BATCH_NO, ")
				.append(" 	RECORD_STATUS, CREATE_USER, CREATE_DATE, LAST_UPDATE_USER, ")
				.append("  	LAST_UPDATE_DATE, VERSION, MEMO) ")
				.append(" values ")
				.append(" ( ?, ? ,? , ?,") //1-4
				.append("   ?, ? ,? , ?,") //5- 8
				.append("   ?, ? ,? , ?,") //9-12
				.append("   ?, ? ,? , ?,") //13-16
				.append("   ?, ? ,? , ?,") //17-20
				.append("   ?, ? ,? , ?,") //20-24
				.append("   ?, ? ,?) ");   //25-27
		int[] count = jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, SharedUtil.getUniqueString());
				ps.setString(2, list.get(i).getEmpNo());
				ps.setString(3, list.get(i).getEmpName());
				ps.setString(4, list.get(i).getJobStatus());
				
				ps.setString(5, list.get(i).getCredentialsType());
				ps.setString(6, list.get(i).getCredentialsCode());
				ps.setString(7, list.get(i).getProvince());
				ps.setString(8, list.get(i).getCity());
				
				ps.setString(9, list.get(i).getTeam1());
				ps.setString(10, list.get(i).getTeam4());
				ps.setString(11, list.get(i).getTeam3());
				ps.setString(12, list.get(i).getTeam2());
				
				ps.setString(13, list.get(i).getJobPosition());
				ps.setString(14, list.get(i).getJobLevel());
				ps.setString(15, list.get(i).getCustManagerName());
				ps.setString(16, list.get(i).getCustManagerCode());
				
				ps.setString(17, list.get(i).getStatus());
				ps.setString(18, list.get(i).getTeamId());
				ps.setString(19, list.get(i).getDeptType());
				ps.setString(20, list.get(i).getImportBatchNo());
				
				ps.setString(21, Constant.VALID_STATUS_VALID);
				ps.setString(22, userId);
				ps.setTimestamp(23, new Timestamp(list.get(i).getCreateDate().getTime()));
				ps.setString(24, userId);
				
				ps.setTimestamp(25, new Timestamp(list.get(i).getCreateDate().getTime()));
				ps.setInt(26, 0);
				ps.setString(27, "");
			}
			public int getBatchSize() {
				return list.size();
			}
		});
		return new ResultVo(true, "执行成功！", count.length);
	}

}
