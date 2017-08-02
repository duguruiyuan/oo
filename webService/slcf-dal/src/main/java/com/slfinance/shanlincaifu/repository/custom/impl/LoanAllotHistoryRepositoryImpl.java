/** 
 * @(#)LoanAllotHistoryRepositoryImpl.java 1.0.0 2015年5月23日 下午1:47:30  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustDailyValueHistoryEntity;
import com.slfinance.shanlincaifu.repository.CustDailyValueHistoryRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.CustDailyValueHistoryRepositroyCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanAllotHistoryRepositoryCustom;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.ParamsNameConstant;

/**   
 * 债权历史情况数据访问实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月23日 下午1:47:30 $ 
 */
@Repository
public class LoanAllotHistoryRepositoryImpl implements
		LoanAllotHistoryRepositoryCustom {
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private CustDailyValueHistoryRepository custDailyValueHistoryRepository;
	
	@Autowired 
	private CustDailyValueHistoryRepositroyCustom custDailyValueHistoryRepositroyCustom;

	@Override
	public Page<Map<String, Object>> findDailyLoanList(Map<String, Object> param) {
		
		CustDailyValueHistoryEntity custDailyValueHistoryEntity = null;
		String sql = "";
		if("today_product_1".equals((String) param.get("dailyValueId"))) { // 活期宝
			custDailyValueHistoryEntity = new CustDailyValueHistoryEntity();
			BigDecimal totalValue = custDailyValueHistoryRepositroyCustom.queryCurrentValue(Constant.PRODUCT_TYPE_01);
			BigDecimal userValue = custDailyValueHistoryRepositroyCustom.queryUserValue((String)param.get("custId"), "", Constant.PRODUCT_TYPE_01);
			custDailyValueHistoryEntity.setHoldScale(ArithUtil.div(userValue, totalValue, 18));
			custDailyValueHistoryEntity.setRecordDate(DateUtils.getCurrentDate("yyyyMMdd"));
			StringBuffer buffer = new StringBuffer()
			.append("            INNER JOIN BAO_T_ALLOT_DETAIL_INFO E ON E.LOAN_ID = A.ID ")
			.append("            INNER JOIN BAO_T_ALLOT_INFO S ON E.ALLOT_ID = S.ID AND S.ALLOT_STATUS != '已撤销' ")
			.append(String.format(" INNER JOIN BAO_T_PRODUCT_TYPE_INFO M ON M.ID = S.RELATE_PRIMARY AND M.TYPE_NAME = '%s' ", Constant.PRODUCT_TYPE_01));
			sql = buffer.toString();
		}
		else if("today_product_4".equals((String) param.get("dailyValueId"))) { // 定期宝
			custDailyValueHistoryEntity = new CustDailyValueHistoryEntity();
			BigDecimal totalValue = custDailyValueHistoryRepositroyCustom.queryCurrentValue(Constant.PRODUCT_TYPE_04);
			BigDecimal userValue = custDailyValueHistoryRepositroyCustom.queryUserValue((String)param.get("custId"), (String)param.get("subAccountId"), Constant.PRODUCT_TYPE_04);
			custDailyValueHistoryEntity.setHoldScale(ArithUtil.div(userValue, totalValue, 18));
			custDailyValueHistoryEntity.setRecordDate(DateUtils.getCurrentDate("yyyyMMdd"));
			StringBuffer buffer = new StringBuffer()
			.append("            INNER JOIN BAO_T_ALLOT_DETAIL_INFO E ON E.LOAN_ID = A.ID ")
			.append("            INNER JOIN BAO_T_ALLOT_INFO S ON E.ALLOT_ID = S.ID AND S.ALLOT_STATUS != '已撤销' ")
			.append(String.format(" INNER JOIN BAO_T_PRODUCT_TYPE_INFO M ON M.ID = S.RELATE_PRIMARY AND M.TYPE_NAME = '%s' ", Constant.PRODUCT_TYPE_04));
			sql = buffer.toString();
		}
		else {
			custDailyValueHistoryEntity = custDailyValueHistoryRepository.findOne((String) param.get("dailyValueId"));
			sql = String.format(" INNER JOIN BAO_T_LOAN_ALLOT_HISTORY E ON E.LOAN_ID = A.ID AND E.RECORD_DATE = ? AND E.PRODUCT_NAME = '%s'", custDailyValueHistoryEntity.getProductName());
		}
		
		StringBuffer sqlString= new StringBuffer()
		.append(" select * ")
		.append(" from  (select a.*,row_number() over (partition by \"debtSource\" order by \"importDate\" desc) rn from  ")
		.append(" ( ")
		.append("      SELECT A.ID \"id\", A.LOAN_CODE \"loanCode\", N.PARAMETER_NAME \"debtSource\", A.PRODUCT_CODE \"productCode\", ")
		.append("             C.CUST_NAME \"custName\", TRUNC_AMOUNT_WEB(A.LOAN_AMOUNT) \"loanAmount\", TRUNC_AMOUNT_WEB(D.VALUE_REPAYMENT_AFTER*?) \"holdValue\", ")
		.append("             A.LOAN_TERM \"loanTerm\", A.REPAYMENT_DAY \"repaymentDay\", A.GRANT_DATE \"grantDate\", ")
		.append("             A.IMPORT_DATE \"importDate\", B.CREDIT_RIGHT_STATUS \"creditRightStatus\", B.YEAR_IRR \"yearRate\", ")
		.append("             A.ASSET_TYPE_CODE \"assetTypeCode\", A.LOAN_DESC \"loanDesc\", A.REPAYMENT_METHOD \"repaymentMethod\" ")
		.append("        FROM BAO_T_LOAN_INFO A ")
		.append("       INNER JOIN BAO_T_LOAN_DETAIL_INFO B ON B.LOAN_ID = A.ID ")
		.append("       INNER JOIN BAO_T_LOAN_CUST_INFO C ON C.ID = A.RELATE_PRIMARY ")
		.append("       INNER JOIN BAO_T_CREDIT_RIGHT_VALUE D ON D.LOAN_ID = A.ID AND D.VALUE_DATE = ? ")
		.append(sql)
		.append(String.format(" INNER JOIN COM_T_PARAM N ON N.VALUE = A.DEBT_SOURCE_CODE AND N.TYPE = '%s'", ParamsNameConstant.LOAN_SOURCE_TYPE))
		.append("       WHERE B.CREDIT_RIGHT_STATUS = '正常'  ")
		.append("       AND A.DEBT_SOURCE_CODE NOT IN ('052', '054') ") // 排除债权来源“企业借款”和“海程融资租赁”
		.append(" ) a ")
		.append(" ) ")
		.append(" order by rn,\"debtSource\" ");
		
		List<Object> objList=new ArrayList<Object>();
		objList.add(custDailyValueHistoryEntity.getHoldScale()); // 用户持有比例
		objList.add(custDailyValueHistoryEntity.getRecordDate());
		if(!"today_product_1".equals((String) param.get("dailyValueId"))
				&& !"today_product_4".equals((String) param.get("dailyValueId"))){
			objList.add(custDailyValueHistoryEntity.getRecordDate());
		}
		
		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), (int)param.get("start"), (int)param.get("length"));
	}

	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Override
	public void saveDailyLoanList(Date now, String productName) throws SLException{
		
		StringBuffer sqlString= new StringBuffer()
		.append(" INSERT INTO BAO_T_LOAN_ALLOT_HISTORY (ID, LOAN_ID, PRODUCT_NAME, RECORD_DATE, CREATE_DATE, VERSION) ")
		.append("  SELECT sys_guid(), A.ID, ?, TO_CHAR(?, 'YYYYMMDD'), ?, 0 ")
		.append("      FROM BAO_T_LOAN_INFO A ")
		.append("      INNER JOIN BAO_T_LOAN_DETAIL_INFO B ON B.LOAN_ID = A.ID ")
		.append("      WHERE B.CREDIT_RIGHT_STATUS = '正常' AND A.ID IN ( ")
		.append("            SELECT T.LOAN_ID ")
		.append("            FROM BAO_T_ALLOT_DETAIL_INFO T ")
		.append("            WHERE T.ALLOT_ID IN ")
		.append("            ( ")
		.append("                  SELECT S.ID FROM BAO_T_ALLOT_INFO S ")
		.append("                  WHERE S.ALLOT_STATUS != '已撤销' ")
		.append("                  AND S.RELATE_PRIMARY IN ( ")
		.append("                      SELECT ID ")
		.append("                      FROM BAO_T_PRODUCT_TYPE_INFO M ")
		.append("                      WHERE M.TYPE_NAME = ? ")
		.append("                  ) ")
		.append("            ) ")
		.append("      ) AND NOT EXISTS ( ")
		.append("      SELECT 1 FROM  BAO_T_LOAN_ALLOT_HISTORY ")
		.append("      WHERE  PRODUCT_NAME = ? AND RECORD_DATE = TO_CHAR(?, 'YYYYMMDD') )");
		
		jdbcTemplate.update(sqlString.toString(), 
				new Object[]{	productName,
								now,
								new Date(),
								productName,
								productName,
								now});	
	}
}
