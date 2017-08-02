package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.FixedInvestCommRepositoryCustom;

/**
 * yanq
 * 
 * */
@Repository
public class FixedInvestCommRepositoryImpl implements FixedInvestCommRepositoryCustom {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private RepositoryUtil repositoryUtil;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Page<Map<String, Object>> getFixedInvestCommListPage(Map<String, Object> param) throws SLException {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SUM(btci.YEAR_INVEST_AMOUNT)  \"yearInvestAmount\",COMM_MONTH \"commMonth\",MAX(btci.CREATE_DATE) \"createDate\",max(btci.PRODUCT_TYPE_ID) \"productTypeId\" FROM BAO_T_COMMISSION_INFO btci, com_t_user ctu, bao_t_cust_info btcii,bao_t_product_type_info btpt where btci.cust_id = btcii.id and btcii.credentials_code = ctu.credentials_code and btci.product_type_id = btpt.id and btpt.type_name = '定期宝' and ctu.user_type='50' ");
		// 报告日期查询

		if (!StringUtils.isEmpty(param.get("applyTimeStart"))) {
			String beginTime = (String) param.get("applyTimeStart");

			sb.append(" AND COMM_MONTH >=? ");
			listObject.add(beginTime.replace("-", ""));

		}
		if (!StringUtils.isEmpty(param.get("applyTimeEnd"))) {
			String endTime = (String) param.get("applyTimeEnd");
			sb.append(" AND COMM_MONTH <=? ");
			listObject.add(endTime.replace("-", ""));
		}
		
		// 按照定期宝业绩月份分组
		sb.append(" GROUP BY COMM_MONTH");
		// 按业绩降序排列
		sb.append(" ORDER BY COMM_MONTH DESC");
		return repositoryUtil.queryForPageMap(sb.toString(), listObject.toArray(), (Integer) param.get("start"), (Integer) param.get("length"));
	}

	@Override
	public Page<Map<String, Object>> getFixedInvestCommListByMonth(Map<String, Object> param) throws SLException {
		StringBuffer sb = new StringBuffer();
		List<Object> listObject = new ArrayList<Object>();
		sb.append("SELECT D.comm_month \"commMonth\",C.User_Number \"userNumber\",C.USER_NAME \"custName\",C.CREDENTIALS_CODE \"credentialsCode\",D.year_invest_amount \"yearInvestAmount\",C.TERM_NAME \"agentName\",C.BRANCH_NAME \"yyTearm\",C.CITY,C.province FROM");
		sb.append("(");
		sb.append("SELECT US.USER_NAME,");
		sb.append("US.User_Number,");
		sb.append("US.CREDENTIALS_CODE,");
		sb.append(" A.TERM_NAME,");
		sb.append(" DECODE(A.TERM_NAME, '', B.AGENT_NAME, A.BRANCH_NAME) BRANCH_NAME,");
		sb.append(" DECODE(A.City_Id, '', B.City_Id, A.City_Id) cityCode,");
		sb.append(" B.province,");
		sb.append(" B.CITY,");
		sb.append(" US.USER_TYPE");
		sb.append(" FROM COM_T_USER US");
		sb.append(" LEFT JOIN (SELECT TERM.ID,");
		sb.append(" TERM.AGENT_NAME TERM_NAME,");
		sb.append(" T.AGENT_NAME BRANCH_NAME,");
		sb.append(" TERM.City_Id");
		sb.append(" FROM (SELECT * FROM COM_T_AGENT WHERE AGENT_TYPE = '60') TERM,");
		sb.append(" COM_T_AGENT T");
		sb.append(" WHERE TERM.AGENT_PARENT_ID = T.ID) A ON US.AGENT_ID = A.ID");
		sb.append(" LEFT JOIN (SELECT * FROM COM_T_AGENT WHERE AGENT_TYPE = '50') B ON US.AGENT_ID = B.ID");
		sb.append(" LEFT JOIN (select t1.parameter_name province,");
		sb.append(" t2.parameter_name city,");
		sb.append(" t2.value");
		sb.append(" FROM com_t_param t1, com_t_param t2");
		sb.append(" where t1.id = t2.parent_id");
		sb.append(" and t2.type = 'city') B on DECODE(A.City_Id,'',B.City_Id,A.City_Id) = B.VALUE) C ");
		sb.append("right  join(");
		sb.append("SELECT  btcii.comm_month ,btci.credentials_code ,btcii.year_invest_amount,btci.cust_name FROM bao_t_commission_info btcii,bao_t_cust_info btci,bao_t_product_type_info btpt where btci.id=btcii.cust_id");
		sb.append(" and btcii.product_type_id = btpt.id and btpt.type_name = '定期宝'");
		sb.append(")D  ON D.credentials_code  = C.CREDENTIALS_CODE WHERE C.USER_TYPE='50' ");	
		
		// 统计月份查詢
		if (!StringUtils.isEmpty(param.get("commMonth"))) {
			String commMonth = (String) param.get("commMonth");
			sb.append(" AND D.comm_month =  ? ");
			listObject.add(commMonth);
		}

		// 姓名加入查詢
		if (!StringUtils.isEmpty(param.get("custName"))) {
			String custName = (String) param.get("custName");
			sb.append(" AND  C.USER_NAME  LIKE ? ");
			listObject.add("%" + custName + "%");
		}
		// 员工工号加入查询
		if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
			String credentialsCode = (String) param.get("credentialsCode");
			sb.append(" AND  C.CREDENTIALS_CODE= ? ");
			listObject.add(credentialsCode);
		}
		// 证件号加入查询
		if (!StringUtils.isEmpty(param.get("userNumber"))) {
			String userNumber = (String) param.get("userNumber");
			sb.append(" AND C.User_Number= ? ");
			listObject.add(userNumber);
		}
		sb.append(" order by C.TERM_NAME asc,C.BRANCH_NAME asc");
		return repositoryUtil.queryForPageMap(sb.toString(), listObject.toArray(), (Integer) param.get("start"), (Integer) param.get("length"));
	}

	/**
	 * 导出数据
	 * 
	 * @param param
	 * @return
	 * @throws SLException
	 */
	public List<Map<String, Object>> export(Map<String, Object> param) throws SLException {
		StringBuffer sb = new StringBuffer();
		List<Object> listObject = new ArrayList<Object>();
		sb.append("SELECT D.comm_month \"commMonth\",C.User_Number \"userNumber\",C.USER_NAME \"custName\",C.CREDENTIALS_CODE \"credentialsCode\",D.year_invest_amount \"yearInvestAmount\",C.TERM_NAME \"agentName\",C.BRANCH_NAME \"yyTearm\",C.CITY,C.province FROM");
		sb.append("(");
		sb.append("SELECT US.USER_NAME,");
		sb.append("US.User_Number,");
		sb.append("US.CREDENTIALS_CODE,");
		sb.append(" A.TERM_NAME,");
		sb.append(" DECODE(A.TERM_NAME, '', B.AGENT_NAME, A.BRANCH_NAME) BRANCH_NAME,");
		sb.append(" DECODE(A.City_Id, '', B.City_Id, A.City_Id) cityCode,");
		sb.append(" B.province,");
		sb.append(" B.CITY,");
		sb.append(" US.USER_TYPE");
		sb.append(" FROM COM_T_USER US");
		sb.append(" LEFT JOIN (SELECT TERM.ID,");
		sb.append(" TERM.AGENT_NAME TERM_NAME,");
		sb.append(" T.AGENT_NAME BRANCH_NAME,");
		sb.append(" TERM.City_Id");
		sb.append(" FROM (SELECT * FROM COM_T_AGENT WHERE AGENT_TYPE = '60') TERM,");
		sb.append(" COM_T_AGENT T");
		sb.append(" WHERE TERM.AGENT_PARENT_ID = T.ID) A ON US.AGENT_ID = A.ID");
		sb.append(" LEFT JOIN (SELECT * FROM COM_T_AGENT WHERE AGENT_TYPE = '50') B ON US.AGENT_ID = B.ID");
		sb.append(" LEFT JOIN (select t1.parameter_name province,");
		sb.append(" t2.parameter_name city,");
		sb.append(" t2.value");
		sb.append(" FROM com_t_param t1, com_t_param t2");
		sb.append(" where t1.id = t2.parent_id");
		sb.append(" and t2.type = 'city') B on DECODE(A.City_Id,'',B.City_Id,A.City_Id) = B.VALUE) C ");
		sb.append("right  join(");
		sb.append("SELECT  btcii.comm_month ,btci.credentials_code ,btcii.year_invest_amount,btci.cust_name FROM bao_t_commission_info btcii,bao_t_cust_info btci,bao_t_product_type_info btpt where btci.id=btcii.cust_id");
		sb.append(" and btcii.product_type_id = btpt.id and btpt.type_name = '定期宝'");
		sb.append(")D  ON D.credentials_code  = C.CREDENTIALS_CODE WHERE C.USER_TYPE='50' ");	

		// 统计月份查詢
		if (!StringUtils.isEmpty(param.get("commMonth"))) {
			String commMonth = (String) param.get("commMonth");
			sb.append(" AND D.comm_month =  ? ");
			listObject.add(commMonth);
		}
		
		sb.append(" order by C.TERM_NAME asc,C.BRANCH_NAME asc");
		
		return repositoryUtil.queryForMap(sb.toString(), listObject.toArray());
	}
}
