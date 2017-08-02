/** 
 * @(#)AllotRepositoryImpl.java 1.0.0 2015年4月23日 下午4:12:55  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.shanlincaifu.entity.AllotDetailInfoEntity;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.AllotRepositoryCustom;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.ParamsNameConstant;

/**   
 * 自定义分配信息接口实现
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月23日 下午4:12:55 $ 
 */
@Repository
public class AllotRepositoryImpl implements AllotRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@PersistenceContext
	private EntityManager manager;
	
	/**
	 * 根据条件查询所有分配信息
	 *
	 * @author  wangjf
	 * @date    2015年4月23日 下午4:00:16
	 * @param 查询条件<br>
	        <tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
			<tt>allotCode：String:分配编号/交易编号(可为空)</tt><br>
			<tt>allotType：String:分配类型(可为空)</tt><br>
			<tt>allotStatus：String:分配状态(可为空)</tt><br>
	 * @return
	  		<tt>id：String:分配信息ID</tt><br>
	  		<tt>typeName：String:分配对象名称</tt><br>
	  		<tt>allotCode：String:分配编号/交易编号</tt><br>
	  		<tt>allotDate：Date:分配日期</tt><br>
	  		<tt>allotAmount：Date:分配总价值</tt><br>
			<tt>allotStatus：String:分配状态</tt><br>
			<tt>useDate： Date:使用日期</tt><br>
	 */
	@Override
	public Page<Map<String, Object>> findAllot(Map<String, Object> param) {
		StringBuffer sqlString= new StringBuffer()
		.append(" select T.ID \"id\", S.TYPE_NAME \"typeName\", T.ALLOT_CODE \"allotCode\", ")
		.append("        T.ALLOT_DATE \"allotDate\", TRUNC_AMOUNT_WEB(T.ALLOT_AMOUNT) \"allotAmount\", ")
		.append("        T.ALLOT_STATUS \"allotStatus\", T.USE_DATE \"useDate\" ")
		.append(" from BAO_T_ALLOT_INFO T, BAO_T_PRODUCT_TYPE_INFO S ")
		.append(" where T.RELATE_TYPE = 'BAO_T_PRODUCT_TYPE_INFO' AND T.RELATE_PRIMARY = S.ID ");
		
		List<Object> objList=new ArrayList<Object>();
		if(!StringUtils.isEmpty(param.get("allotCode"))){
			sqlString.append(" and ALLOT_CODE = ?");
			objList.add(param.get("allotCode"));
		}
		
		if(!StringUtils.isEmpty(param.get("allotType"))){
			sqlString.append(" and S.TYPE_NAME = ?");
			objList.add(param.get("allotType"));
		}
		
		if(!StringUtils.isEmpty(param.get("allotStatus"))){
			sqlString.append(" and ALLOT_STATUS = ?");
			objList.add(param.get("allotStatus"));
		}
		
		if(!StringUtils.isEmpty(param.get("useDateBegin"))){
			sqlString.append(" and USE_DATE >= ?");
			objList.add(DateUtils.parseStandardDate((String)param.get("useDateBegin")));
		}
		
		if(!StringUtils.isEmpty(param.get("useDateEnd"))){
			sqlString.append(" and USE_DATE <= ?");
			objList.add(DateUtils.getEndDate(DateUtils.parseStandardDate((String)param.get("useDateEnd"))));
		}
		
		sqlString.append(" ORDER BY T.ALLOT_DATE DESC");

		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), (int)param.get("start"), (int)param.get("length"));
	}

	/**
	 * 查询可以分配的债权
	 *
	 * @author  wangjf
	 * @date    2015年4月24日 上午9:45:44
	 * @param param
	        <tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
			<tt>debtSource： String:来源机构(可为空)</tt><br>
			<tt>productCode： String:借款产品(可为空)</tt><br>
			<tt>yearRateBegin： String:年化利率开始(可为空)</tt><br>
			<tt>yearRateEnd： String:年化利率结束(可为空)</tt><br>
			<tt>loanTerm： String:借款期限(可为空)</tt><br>
			<tt>creditRightStatus： String:借款状态(可为空)</tt><br>
			<tt>repaymentDay： String:还款日(可为空)</tt><br>
			<tt>importDateBegin： String:导入日期开始(可为空)</tt><br>
			<tt>importDateEnd： String:导入日期结束(可为空)</tt><br>
	 * @return
	 		<tt>id： String:债权ID</tt><br>
	 		<tt>loanCode： String:债权编号</tt><br>
	 		<tt>debtSource： String:来源机构</tt><br>
			<tt>productCode： String:借款产品</tt><br>
			<tt>custName： String:借款人</tt><br>
			<tt>loanAmount： BigDecimal:借款金额（元）</tt><br>
			<tt>currentValue： BigDecimal:当前价值</tt><br>
			<tt>loanTerm： String:借款期限（月）</tt><br>
			<tt>repaymentDay： String:还款日</tt><br>
			<tt>grantDate： Date:放款日期</tt><br>
			<tt>importDate： Date:导入日期</tt><br>
			<tt>creditRightStatus： String:债权状态</tt><br>
			<tt>yearRate： BigDecimal:年化利率</tt><br>
	 */
	@Override
	public Page<Map<String, Object>> findCanAllotLoan(Map<String, Object> param) {
		StringBuffer sqlString= new StringBuffer()
		.append(" SELECT A.ID \"id\", A.LOAN_CODE \"loanCode\", N.PARAMETER_NAME \"debtSourceCode\", A.PRODUCT_CODE \"productCode\",  ")
		.append("        C.CUST_NAME \"custName\", TRUNC_AMOUNT_WEB(A.LOAN_AMOUNT) \"loanAmount\", TRUNC_AMOUNT_WEB(D.VALUE_REPAYMENT_BEFORE) \"currentValue\",  ")
		.append("        A.LOAN_TERM \"loanTerm\", A.REPAYMENT_DAY \"repaymentDay\", A.GRANT_DATE \"grantDate\",  ")
		.append("        A.IMPORT_DATE \"importDate\", B.CREDIT_RIGHT_STATUS \"creditRightStatus\", B.YEAR_IRR \"yearRate\"")
		.append("   FROM BAO_T_LOAN_INFO A ")
		.append("  INNER JOIN BAO_T_LOAN_DETAIL_INFO B ON B.LOAN_ID = A.ID ")
		.append("  INNER JOIN BAO_T_LOAN_CUST_INFO C ON C.ID = A.RELATE_PRIMARY ")
		.append("  INNER JOIN BAO_T_CREDIT_RIGHT_VALUE D ON D.LOAN_ID = A.ID AND D.VALUE_DATE = TO_CHAR(?, 'yyyyMMdd') ")
		.append(String.format(" INNER JOIN COM_T_PARAM N ON N.VALUE = A.DEBT_SOURCE_CODE AND N.TYPE = '%s'", ParamsNameConstant.LOAN_SOURCE_TYPE))
		.append(" WHERE B.CREDIT_RIGHT_STATUS = '正常' AND A.ID NOT IN ( ")
		.append("       SELECT T.LOAN_ID ")
		.append("       FROM BAO_T_ALLOT_DETAIL_INFO T ")
		.append("       WHERE T.ALLOT_ID IN  ")
		.append("       ( ")
		.append("             SELECT S.ID FROM BAO_T_ALLOT_INFO S ")
		.append("             WHERE S.ALLOT_STATUS != '已撤销' ")
		.append("       ) ")
		.append(" ) ");
		
		List<Object> objList=new ArrayList<Object>();
		objList.add(new Date());
		
		if(!StringUtils.isEmpty(param.get("debtSource"))){
			sqlString.append(" and DEBT_SOURCE_CODE = ?");
			objList.add(param.get("debtSource"));
		}
		
		if(!StringUtils.isEmpty(param.get("productCode"))){
			sqlString.append(" and PRODUCT_CODE = ?");
			objList.add(param.get("productCode"));
		}
		
		if(!StringUtils.isEmpty(param.get("yearRateBegin"))){
			sqlString.append(" and YEAR_IRR >= ?");
			objList.add(new BigDecimal((String)param.get("yearRateBegin")));
		}
		
		if(!StringUtils.isEmpty(param.get("yearRateEnd"))){
			sqlString.append(" and YEAR_IRR <= ?");
			objList.add(new BigDecimal((String)param.get("yearRateEnd")));
		}
		
		if(!StringUtils.isEmpty(param.get("loanTerm"))){
			sqlString.append(" and LOAN_TERM = ?");
			objList.add(Integer.parseInt((String)param.get("loanTerm")));
		}
		
		if(!StringUtils.isEmpty(param.get("creditRightStatus"))){
			sqlString.append(" and CREDIT_RIGHT_STATUS = ?");
			objList.add(param.get("creditRightStatus"));
		}
		
		if(!StringUtils.isEmpty(param.get("repaymentDay"))){
			sqlString.append(" and REPAYMENT_DAY = ?");
			objList.add(param.get("repaymentDay"));
		}
		
		if(!StringUtils.isEmpty(param.get("importDateBegin"))){
			sqlString.append(" and IMPORT_DATE >= ?");
			objList.add(DateUtils.parseStandardDate((String)param.get("importDateBegin")));
		}
		
		if(!StringUtils.isEmpty(param.get("importDateEnd"))){
			sqlString.append(" and IMPORT_DATE <= ?");
			objList.add(DateUtils.getEndDate(DateUtils.parseStandardDate((String)param.get("importDateEnd"))));
		}
		
		sqlString.append(" ORDER BY A.IMPORT_DATE DESC");
		
		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), (int)param.get("start"), (int)param.get("length"));
	}

	/**
	 * 查询可以分配的债权价值、金额、笔数
	 *
	 * @author  wangjf
	 * @date    2015年4月24日 下午2:25:50
	 * @param param
	        <tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
			<tt>debtSource： String:来源机构(可为空)</tt><br>
			<tt>productCode： String:借款产品(可为空)</tt><br>
			<tt>yearRateBegin： String:年化利率开始(可为空)</tt><br>
			<tt>yearRateEnd： String:年化利率结束(可为空)</tt><br>
			<tt>loanTerm： String:借款期限(可为空)</tt><br>
			<tt>creditRightStatus： String:借款状态(可为空)</tt><br>
			<tt>repaymentDay： String:还款日(可为空)</tt><br>
			<tt>importDateBegin： String:导入日期开始(可为空)</tt><br>
			<tt>importDateEnd： String:导入日期结束(可为空)</tt><br>
	 * @return
	  		<tt>totalPv： BigDecimal:价值汇总</tt><br>
	 		<tt>totalLoanAmount： BigDecimal:债权金额汇总</tt><br>
	 		<tt>totalLoans： Integer:总笔数</tt><br>
	 */
	@Override
	public Map<String, Object> findCanAllotLoanCount(Map<String, Object> param) {
		StringBuffer sqlString= new StringBuffer()
		.append(" SELECT NVL(SUM(TRUNC_AMOUNT_WEB(D.VALUE_REPAYMENT_BEFORE)), 0) \"totalPv\", NVL(SUM(TRUNC_AMOUNT_WEB(A.LOAN_AMOUNT)),0) \"totalLoanAmount\", COUNT(A.ID) \"totalLoans\" ")
		.append(" FROM BAO_T_LOAN_INFO A  ")
		.append(" INNER JOIN BAO_T_LOAN_DETAIL_INFO B ON B.LOAN_ID = A.ID ")
		.append(" INNER JOIN BAO_T_CREDIT_RIGHT_VALUE D ON A.ID = D.LOAN_ID AND D.VALUE_DATE = TO_CHAR(?, 'YYYYMMDD') ")
		.append(String.format(" INNER JOIN COM_T_PARAM N ON N.VALUE = A.DEBT_SOURCE_CODE AND N.TYPE = '%s'", ParamsNameConstant.LOAN_SOURCE_TYPE))
		.append(" WHERE B.CREDIT_RIGHT_STATUS = '正常' AND A.ID NOT IN ( ")
		.append("       SELECT T.LOAN_ID ")
		.append("       FROM BAO_T_ALLOT_DETAIL_INFO T ")
		.append("       WHERE T.ALLOT_ID IN  ")
		.append("       ( ")
		.append("             SELECT S.ID FROM BAO_T_ALLOT_INFO S ")
		.append("             WHERE S.ALLOT_STATUS != '已撤销' ")
		.append("       ) ")
		.append(" ) ");
		
		List<Object> objList=new ArrayList<Object>();
		objList.add(new Date());
		
		if(!StringUtils.isEmpty(param.get("debtSource"))){
			sqlString.append(" and DEBT_SOURCE_CODE = ?");
			objList.add(param.get("debtSource"));
		}
		
		if(!StringUtils.isEmpty(param.get("productCode"))){
			sqlString.append(" and PRODUCT_CODE = ?");
			objList.add(param.get("productCode"));
		}
		
		if(!StringUtils.isEmpty(param.get("yearRateBegin"))){
			sqlString.append(" and YEAR_IRR >= ?");
			objList.add(new BigDecimal((String)param.get("yearRateBegin")));
		}
		
		if(!StringUtils.isEmpty(param.get("yearRateEnd"))){
			sqlString.append(" and YEAR_IRR <= ?");
			objList.add(new BigDecimal((String)param.get("yearRateEnd")));
		}
		
		if(!StringUtils.isEmpty(param.get("loanTerm"))){
			sqlString.append(" and LOAN_TERM = ?");
			objList.add(Integer.parseInt((String)param.get("loanTerm")));
		}
		
		if(!StringUtils.isEmpty(param.get("creditRightStatus"))){
			sqlString.append(" and CREDIT_RIGHT_STATUS = ?");
			objList.add(param.get("creditRightStatus"));
		}
		
		if(!StringUtils.isEmpty(param.get("repaymentDay"))){
			sqlString.append(" and REPAYMENT_DAY = ?");
			objList.add(param.get("repaymentDay"));
		}
		
		if(!StringUtils.isEmpty(param.get("importDateBegin"))){
			sqlString.append(" and IMPORT_DATE >= ?");
			objList.add(DateUtils.parseStandardDate((String)param.get("importDateBegin")));
		}
		
		if(!StringUtils.isEmpty(param.get("importDateEnd"))){
			sqlString.append(" and IMPORT_DATE <= ?");
			objList.add(DateUtils.getEndDate(DateUtils.parseStandardDate((String)param.get("importDateEnd"))));
		}
			
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), objList.toArray());
		return list.get(0);
	}
	
	/**
	 * 根据分配信息主键查询债权信息
	 *
	 * @author  wangjf
	 * @date    2015年4月24日 下午3:40:39
	 * @param 
	 *		<tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
	  		<tt>id：String:分配信息ID</tt><br>
	 * @return
	 		<tt>id： String:债权ID</tt><br>
	 		<tt>loanCode： String:债权编号</tt><br>
	 		<tt>debtSource： String:来源机构</tt><br>
			<tt>productCode： String:借款产品</tt><br>
			<tt>custName： String:借款人</tt><br>
			<tt>loanAmount： BigDecimal:借款金额（元）</tt><br>
			<tt>currentValue： BigDecimal:当前价值</tt><br>
			<tt>loanTerm： String:借款期限（月）</tt><br>
			<tt>repaymentDay： String:还款日</tt><br>
			<tt>grantDate： Date:放款日期</tt><br>
			<tt>importDate： Date:导入日期</tt><br>
			<tt>creditRightStatus： String:债权状态</tt><br>
			<tt>yearRate： BigDecimal:年化利率</tt><br>
	 */
	public Page<Map<String, Object>> findAllotLoanById(Map<String, Object> param)
	{
		StringBuffer sqlString= new StringBuffer()
		.append(" SELECT A.ID \"id\", A.LOAN_CODE \"loanCode\", N.PARAMETER_NAME \"debtSourceCode\", A.PRODUCT_CODE \"productCode\",  ")
		.append("        C.CUST_NAME \"custName\", TRUNC_AMOUNT_WEB(A.LOAN_AMOUNT) \"loanAmount\", NVL(TRUNC_AMOUNT_WEB(D.VALUE_REPAYMENT_BEFORE), 0) \"currentValue\",  ")
		.append("        A.LOAN_TERM \"loanTerm\", A.REPAYMENT_DAY \"repaymentDay\", A.GRANT_DATE \"grantDate\",  ")
		.append("        A.IMPORT_DATE \"importDate\", B.CREDIT_RIGHT_STATUS \"creditRightStatus\", B.YEAR_IRR \"yearRate\"")
		.append("   FROM BAO_T_LOAN_INFO A ")
		.append("  INNER JOIN BAO_T_LOAN_DETAIL_INFO B ON B.LOAN_ID = A.ID ")
		.append("  INNER JOIN BAO_T_LOAN_CUST_INFO C ON C.ID = A.RELATE_PRIMARY ")
		.append(String.format(" INNER JOIN COM_T_PARAM N ON N.VALUE = A.DEBT_SOURCE_CODE AND N.TYPE = '%s'", ParamsNameConstant.LOAN_SOURCE_TYPE))
		.append("  LEFT JOIN BAO_T_CREDIT_RIGHT_VALUE D ON D.LOAN_ID = A.ID AND D.VALUE_DATE = TO_CHAR(?, 'yyyyMMdd') ")
		.append("  WHERE A.ID IN ")
		.append("        (SELECT T.LOAN_ID FROM BAO_T_ALLOT_DETAIL_INFO T WHERE T.ALLOT_ID = ?) ");
		
		return repositoryUtil.queryForPageMap(sqlString.toString(), new Object[]{new Date(), (String)param.get("id")}, (int)param.get("start"), (int)param.get("length"));
	}

	@Transactional
	@Override
	public void batchInsertAllotDetail(List<AllotDetailInfoEntity> list) {
		// TODO Auto-generated method stub
		for (int i = 0; i < list.size(); i++) {
			manager.persist(list.get(i));
		}
		manager.flush();
		manager.clear();
	}

	@Override
	public BigDecimal findAllotLoanCount(Map<String, Object> param) {
		StringBuffer sqlString= new StringBuffer()
		.append(" SELECT NVL(SUM(TRUNC_AMOUNT_WEB(D.VALUE_REPAYMENT_BEFORE)), 0) \"currentValue\" ")
		.append("   FROM BAO_T_LOAN_INFO A ")
		.append("  INNER JOIN BAO_T_LOAN_DETAIL_INFO B ON B.LOAN_ID = A.ID ")
		.append("  INNER JOIN BAO_T_LOAN_CUST_INFO C ON C.ID = A.RELATE_PRIMARY ")
		.append("  LEFT JOIN BAO_T_CREDIT_RIGHT_VALUE D ON D.LOAN_ID = A.ID AND D.VALUE_DATE = TO_CHAR(?, 'yyyyMMdd') ")
		.append("  WHERE A.ID IN ")
		.append("        (SELECT T.LOAN_ID FROM BAO_T_ALLOT_DETAIL_INFO T WHERE T.ALLOT_ID = ?) ");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[]{new Date(), (String)param.get("id")});
		if(list == null || list.size() == 0){
			return new BigDecimal("0");
		}
		return new BigDecimal(list.get(0).get("currentValue").toString());
	}

	@Override
	public List<Map<String, Object>> findByLoanIdList(
			List<Map<String, Object>> loanList) {
		StringBuffer sqlString= new StringBuffer()
		.append(" SELECT D.LOAN_ID \"loanId\", D.VALUE_REPAYMENT_BEFORE \"currentValue\" ")
		.append(" FROM BAO_T_CREDIT_RIGHT_VALUE D")
		.append(" WHERE D.VALUE_DATE = TO_CHAR(?, 'yyyyMMdd') ");
		
		List<Object> objList=new ArrayList<Object>();
		objList.add(new Date());
		if(loanList != null && loanList.size() > 0) {
			sqlString.append(" and ( ");
			for(int i = 0; i < loanList.size(); i ++)
			{
				if(i == 0) {
					sqlString.append(" LOAN_ID = ? ");
				}
				else{
					sqlString.append(" OR LOAN_ID = ? ");
				}					
				objList.add(loanList.get(i).get("loanId"));
			}
			sqlString.append(" ) ");
		}
		
		return repositoryUtil.queryForMap(sqlString.toString(), objList.toArray());
	}

	@Override
	public BigDecimal sumByLoanIdList(List<Map<String, Object>> loanList) {
		StringBuffer sqlString= new StringBuffer()
		.append(" SELECT NVL(SUM(D.VALUE_REPAYMENT_BEFORE),0) \"currentValue\" ")
		.append(" FROM BAO_T_CREDIT_RIGHT_VALUE D")
		.append(" WHERE D.VALUE_DATE = TO_CHAR(?, 'yyyyMMdd') ");
		
		List<Object> objList=new ArrayList<Object>();
		objList.add(new Date());
		if(loanList != null && loanList.size() > 0) {
			sqlString.append(" and ( ");
			for(int i = 0; i < loanList.size(); i ++)
			{
				if(i == 0) {
					sqlString.append(" LOAN_ID = ? ");
				}
				else{
					sqlString.append(" OR LOAN_ID = ? ");
				}					
				objList.add(loanList.get(i).get("loanId"));
			}
			sqlString.append(" ) ");
		}
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), objList.toArray());
		if(list == null || list.size() == 0){
			return new BigDecimal("0");
		}
		return new BigDecimal(list.get(0).get("currentValue").toString());
	}

	@Override
	public BigDecimal judgeAllotedLoanIdList(List<Map<String, Object>> loanList) {
		StringBuffer sqlString= new StringBuffer()
		.append(" select count(1) \"countsAllot\" from bao_t_allot_info t, bao_t_allot_detail_info s ")
		.append(" where t.id = s.allot_id and t.allot_status != '已撤销' ");
		
		List<Object> objList=new ArrayList<Object>();
		if(loanList != null && loanList.size() > 0) {
			sqlString.append(" and ( ");
			for(int i = 0; i < loanList.size(); i ++)
			{
				if(i == 0) {
					sqlString.append(" LOAN_ID = ? ");
				}
				else{
					sqlString.append(" OR LOAN_ID = ? ");
				}					
				objList.add(loanList.get(i).get("loanId"));
			}
			sqlString.append(" ) ");
		}
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), objList.toArray());
		if(list == null || list.size() == 0){
			return new BigDecimal("0");
		}
		return new BigDecimal(list.get(0).get("countsAllot").toString());
	}

}
