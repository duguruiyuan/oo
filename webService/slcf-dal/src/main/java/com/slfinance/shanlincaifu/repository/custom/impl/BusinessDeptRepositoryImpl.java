package com.slfinance.shanlincaifu.repository.custom.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.slfinance.shanlincaifu.repository.custom.BusinessDeptRepositoryCustom;
import com.slfinance.shanlincaifu.utils.SharedUtil;

@Repository
public class BusinessDeptRepositoryImpl implements BusinessDeptRepositoryCustom {
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	
	@Override
	public int batchInsertBusinessDept(final List<Map<String, Object>> params) {

		StringBuffer sql = new StringBuffer()
		.append("INSERT INTO bao_t_business_dept_info (")
		.append("ID,BUSSINESS_IMPORT_ID,CUST_ID	,CREDENTIALS_CODE,EMP_NO")
		.append(",EMP_NAME,DEPT_NAME,PROVINCE_NAME,CITY_NAME")
		.append(",RECORD_STATUS,CREATE_USER,CREATE_DATE,LAST_UPDATE_USER,LAST_UPDATE_DATE,VERSION,MEMO)")
		.append(" VALUES(?,?,?,?,?")//1-5
		.append(",?,?,?,?")// 6-9
		.append(",?,?,?,?,?,?,?)")// 10-16
		;

		int[] count = jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, SharedUtil.getUniqueString());
				ps.setString(2, (String)params.get(i).get("bussinessImportId"));
				ps.setString(3, "");
				ps.setString(4, "");
				ps.setString(5, (String)params.get(i).get("empNo"));
				ps.setString(6, (String)params.get(i).get("empName"));
				ps.setString(7, (String)params.get(i).get("debtName"));
				ps.setString(8, (String)params.get(i).get("provinceName"));
				ps.setString(9, (String)params.get(i).get("cityName"));
				ps.setString(10, "有效");
				ps.setString(11, (String)params.get(i).get("userId"));
				ps.setTimestamp(12, new Timestamp(new Date().getTime()));
				ps.setString(13, "");
				ps.setTimestamp(14, null);
				ps.setInt(15, 1);
				ps.setString(16, "");
				
			}

			public int getBatchSize() {
				return params.size();
			}
		});
		// 插入之后删除相同数据
		StringBuilder sqlString = new StringBuilder()
		.append(" delete from bao_t_business_dept_info t  ")
		.append(" where t.id !=  ")
		.append(" ( ")
		.append(" select max(s.id) from bao_t_business_dept_info s ")
		.append(" where s.bussiness_import_id = t.bussiness_import_id ")
		.append(" and s.emp_no = t.emp_no ")
		.append(" and s.emp_name = t.emp_name ")
		.append(" and s.dept_name = t.dept_name ")
		.append(" ) ");
		jdbcTemplate.execute(sqlString.toString());
		return (count!=null?count.length:0);
	
	}

}
