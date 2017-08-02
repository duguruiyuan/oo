package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CreditRightValueEntity;
import com.slfinance.shanlincaifu.entity.LoanCustInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.repository.LoanInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.ParamRepository;
import com.slfinance.shanlincaifu.repository.UserRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.LoanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.ParamsNameConstant;
import com.slfinance.shanlincaifu.utils.SqlCondition;
import com.slfinance.shanlincaifu.vo.RepaymentPlanVo;
import com.slfinance.vo.ResultVo;

/**   
 * 
 * 借款业务自定义数据访问类
 * @author  zhoudl
 * @version $Revision:1.0.0, $Date: 2015年4月23日 上午10:04:46 $ 
 */
@Repository
public class LoanInfoRepositoryCustomImpl implements LoanInfoRepositoryCustom {
	
	@Autowired
	EntityManager entityManager;
	@Autowired
	RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private LoanInfoRepository loanInfoRepository;
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	ParamRepository paramRepository;
	/**
	 * @desc 查询未执行PV计算的债权
	 * @author zhoudl
	 * @return java.util.List
	 * @date 2015年04月15日 下午午13:41:36
	 * **/
	@Override
	public List<RepaymentPlanVo> queryUnExecutePvLoan() throws SLException {
		List<RepaymentPlanVo> voList=new Vector<RepaymentPlanVo>();
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT")
		   .append(" L.ID \"id\",L.INVEST_START_DATE \"investStartDate\",RP.CURRENT_TERM \"currentTerm\",TO_CHAR(TO_DATE(RP.EXPECT_REPAYMENT_DATE,'YYYYMMDD'),'YYYY-MM-DD') \"expectRepaymentDate\",")
		   .append(" RP.REMAINDER_PRINCIPAL \"remainderPrincipal\",RP.REPAYMENT_PRINCIPAL \"repaymentPrincipal\",RP.REPAYMENT_INTEREST \"repaymentInterest\",")
		   .append(" RP.REPAYMENT_TOTAL_AMOUNT \"repaymentTotalAmount\",L.LOAN_AMOUNT \"loanAmount\",")
		   .append(" L.HOLD_AMOUNT \"holdAmount\",L.HOLD_SCALE \"holdScale\"")
		   .append(" FROM BAO_T_REPAYMENT_PLAN_INFO RP, BAO_T_LOAN_INFO L,BAO_T_LOAN_DETAIL_INFO D")
		   .append(" WHERE RP.LOAN_ID = L.ID AND L.ID=D.LOAN_ID ")
		   .append(" AND D.EXEC_PV_STATUS=? AND RP.EXPECT_REPAYMENT_DATE is not null")
		   .append(" AND L.INVEST_START_DATE is not null");
		 List<Map<String, Object>> queryForMap = repositoryUtil.queryForMap(sql.toString(), new String[]{Constant.EXEC_UN_STATUS});
		 for (Map<String, Object> map : queryForMap) {
			 RepaymentPlanVo vo=new RepaymentPlanVo();
			 vo.setLoanId(CommonUtils.emptyToString(map.get("id")));
			 vo.setInvestStartDate(DateUtils.parseDate(CommonUtils.emptyToString(map.get("investStartDate")), "yyyy-MM-dd"));
			 vo.setCurrentTerm(CommonUtils.emptyToInt(map.get("currentTerm")));
			 vo.setExpectRepaymentDate(DateUtils.parseDate(CommonUtils.emptyToString(map.get("expectRepaymentDate")), "yyyy-MM-dd"));
			 vo.setRemainderPrincipal(CommonUtils.emptyToDecimal(CommonUtils.emptyToString(map.get("remainderPrincipal"))));
			 vo.setRepaymentPrincipal(CommonUtils.emptyToDecimal(CommonUtils.emptyToString(map.get("repaymentPrincipal"))));
			 vo.setRepaymentInterest(CommonUtils.emptyToDecimal(CommonUtils.emptyToString(map.get("repaymentInterest"))));
			 vo.setRepaymentTotalAmount(CommonUtils.emptyToDecimal(CommonUtils.emptyToString(map.get("repaymentTotalAmount"))));
			 vo.setLoanAmount(CommonUtils.emptyToDecimal(CommonUtils.emptyToString(map.get("loanAmount"))));
			 vo.setHoldAmount(CommonUtils.emptyToDecimal(CommonUtils.emptyToString(map.get("holdAmount"))));
			 vo.setHoldScale(CommonUtils.emptyToDecimal(CommonUtils.emptyToString(map.get("holdScale"))));
			 voList.add(vo);
		}
		return voList;
	}
	
	/**
	 * @desc 根据参数条件,查询相应的债权信息
	 * @param debtSourceCode 债权来源编号
	 * @param repaymentDay   还款日
	 * @param repaymentMethod 还款方式
	 * @param importDate     债权导入日期
	 * @param repaymentCycle 还款频率
	 * @param assetTypeCode  资产类型
	 * @param start          起始记录参数
	 * @param length         每页记录大小
	 * @return java.util.Map
	 * @author zhoudl
	 * @date 2015年04月15日 下午午13:41:36
	 * **/
	@Override
	public Map<String, Object> queryConditionLoan(Map<String,Object> conditionMap){
		List<Object> paramList=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT L.ID \"id\",")
		   .append(" L.DEBT_SOURCE_CODE \"debtSourceCode\",P.PARAMETER_NAME \"parameterName\",L.LOAN_CODE \"loanCode\",LC.CUST_NAME \"custName\",LC.CREDENTIALS_CODE \"credentialsCode\",L.LOAN_AMOUNT \"loanAmount\",L.LOAN_DESC \"loanDesc\",")
		   .append(" L.LOAN_TERM \"loanTerm\",L.IMPORT_DATE \"importDate\",L.HOLD_AMOUNT \"holdAmount\",L.REPAYMENT_METHOD \"repaymentMethod\",L.REPAYMENT_CYCLE \"repaymentCycle\",L.ASSET_TYPE_CODE \"assetTypeCode\"")
		   .append(" FROM BAO_T_LOAN_INFO L,")
		   .append(" (SELECT CP.VALUE,CP.PARAMETER_NAME FROM COM_T_PARAM CP WHERE CP.TYPE = '").append(ParamsNameConstant.LOAN_SOURCE_TYPE).append("') P,")
		   .append(" BAO_T_LOAN_CUST_INFO LC")
		   .append(" WHERE L.DEBT_SOURCE_CODE = P.VALUE AND L.RELATE_PRIMARY=LC.ID AND L.RELATE_TYPE='BAO_T_LOAN_CUST_INFO'");
		
		if(!StringUtils.isEmpty(conditionMap.get("debtSourceCode"))){
			sql.append(" AND L.DEBT_SOURCE_CODE=?");
			paramList.add(conditionMap.get("debtSourceCode"));
		}
		
		if(!StringUtils.isEmpty(conditionMap.get("repaymentDay"))){
			sql.append(" AND L.REPAYMENT_DAY=?");
			paramList.add(conditionMap.get("repaymentDay"));
		}
		
		if(!StringUtils.isEmpty(conditionMap.get("repaymentMethod"))){
			sql.append(" AND L.REPAYMENT_METHOD=?");
			paramList.add(conditionMap.get("repaymentMethod"));
		}
		
		if(!StringUtils.isEmpty(conditionMap.get("repaymentCycle"))){
			sql.append(" AND L.REPAYMENT_CYCLE=?");
			paramList.add(conditionMap.get("repaymentCycle"));
		}
		
		if(!StringUtils.isEmpty(conditionMap.get("assetTypeCode"))){
			sql.append(" AND L.ASSET_TYPE_CODE=?");
			paramList.add(conditionMap.get("assetTypeCode"));
		}
		
		if(!StringUtils.isEmpty(conditionMap.get("importDate"))){
			sql.append(" AND L.IMPORT_DATE >= TO_DATE(?,'YYYY-MM-DD')")
			   .append(" AND L.IMPORT_DATE<=TRUNC(TO_DATE(?,'YYYY-MM-DD'))+0.99999");
			paramList.add(conditionMap.get("importDate"));
			paramList.add(conditionMap.get("importDate"));
		}
		sql.append(" ORDER BY L.IMPORT_DATE DESC");
		
		int start=CommonUtils.emptyToInt(conditionMap.get("start"));
		int length=CommonUtils.emptyToInt(conditionMap.get("length"));
		Page<Map<String, Object>> queryPage = repositoryUtil.queryForPageMap(sql.toString(), paramList.toArray(), start, length);
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("iTotalDisplayRecords", queryPage.getTotalElements());
		queryMap.put("data", queryPage.getContent());
		return queryMap;
   }

	/**
	 * @desc 根据主键ID,查询还款明细
	 * @param loanId 债权主键ID
	 * @return java.util.List
	 * @author zhoudl
	 * **/
	@Override
	public List<Map<String, Object>> queryLoanRepaymentDetail(Map<String, Object> conditionMap) throws SLException {
		if(StringUtils.isEmpty(conditionMap.get("loanId"))){
			throw new SLException("借款记录主键不能为空!");
		}
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT")
		   .append(" L.DEBT_SOURCE_CODE \"debtSourceCode\",L.LOAN_CODE \"loanCode\",REP.CURRENT_TERM \"currentTerm\",TO_CHAR(TO_DATE(REP.EXPECT_REPAYMENT_DATE,'YYYYMMDD'),'YYYY-MM-DD') \"expectRepaymentDate\",")
		   .append(" REP.REPAYMENT_TOTAL_AMOUNT/L.HOLD_SCALE \"repaymentTotalAmount\",REP.REMAINDER_PRINCIPAL/L.HOLD_SCALE \"remainderPrincipal\",REP.ADVANCE_CLEANUP_TOTAL_AMOUNT/L.HOLD_SCALE \"advanceCleanupTotalAmount\"")
		   .append(" ,LC.CUST_NAME \"custName\",L.REPAYMENT_CYCLE \"repaymentCycle\",L.ASSET_TYPE_CODE \"assetTypeCode\"")
		   .append(" FROM BAO_T_LOAN_INFO L,BAO_T_REPAYMENT_PLAN_INFO REP,BAO_T_LOAN_CUST_INFO LC")
		   .append(" WHERE L.ID=REP.LOAN_ID(+) AND L.RELATE_PRIMARY=LC.ID AND L.RELATE_TYPE='BAO_T_LOAN_CUST_INFO'")
		   .append(" AND L.ID='").append(conditionMap.get("loanId").toString()).append("'")
		   .append(" ORDER BY REP.CURRENT_TERM");
		List<Map<String, Object>> queryMapList = jdbcTemplate.queryForList(sql.toString());
		return queryMapList;
	}

	/**
	 * @desc 批量插入债权PV值
	 * @param loanId 债权主键ID
	 * @param valueDate 价值对应日期
	 * @param valueRepaymentBefore 还款前债权价值
	 * @param valueRepaymentAfter 还款后债权价值
	 * @return boolean
	 * @author zhoudl
	 * **/
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Override
	public boolean batchInsertPv(final List<CreditRightValueEntity> list) throws SLException {
		String sql="INSERT INTO BAO_T_CREDIT_RIGHT_VALUE(ID,LOAN_ID,VALUE_DATE,VALUE_REPAYMENT_BEFORE,VALUE_REPAYMENT_AFTER,CREATE_DATE) VALUES(?,?,?,?,?,?)";
		
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, list.get(i).getId());
				ps.setString(2, list.get(i).getLoanId());
				ps.setString(3, list.get(i).getValueDate());
				ps.setString(4, list.get(i).getValueRepaymentBefore().toString());
				ps.setString(5, list.get(i).getValueRepaymentAfter().toString());
				ps.setTimestamp(6, list.get(i).getCreateDate());
			}
			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
		return true;
	}
	
	/**
	 * @desc 批量更新执行PV状态
	 * @param loanId 债权主键ID
	 * @return boolean
	 * @author zhoudl
	 * **/
	@Override
	public boolean batchUpdateLoanExecStatus(final  List<String> loanIds) throws SLException{
		String sql="UPDATE BAO_T_LOAN_DETAIL_INFO T SET T.EXEC_PV_STATUS=? WHERE T.LOAN_ID=?";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, Constant.EXEC_STATUS);
				ps.setString(2, loanIds.get(i));
			}
			
			@Override
			public int getBatchSize() {
				return loanIds.size();
			}
		});
		return true;
	}
	
	@Override
	public boolean batchModifyTransferSeatTerm(final List<String> loanIds,final String seatTerm,final String userId)
			throws SLException {
		List<Object> args = new ArrayList<Object>();
		StringBuilder strBuilder = new StringBuilder("UPDATE BAO_T_LOAN_INFO T SET T.SEAT_TERM=? WHERE 1=0 ");
		args.add(seatTerm);

		for (String loanId : loanIds) {
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
			if (loanInfoEntity == null|| !Constant.PROJECT_STATUS_05.equals(loanInfoEntity.getLoanStatus())) {
				return false;
			}
			strBuilder.append(" OR t.ID = ? ");
			args.add(loanId);
			Integer  beforeSeatTerm=loanInfoEntity.getSeatTerm();
			String   beforeSeatTermValue=loanInfoEntity.getSeatTerm()==null?"":beforeSeatTerm.toString();
			
			// 日志信息
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setBasicModelProperty(userId, true);
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
			logInfoEntity.setRelatePrimary(loanId);
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_83);
			logInfoEntity.setOperDesc(userId);
			logInfoEntity.setMemo(String.format("修改转让设置loanId=%s",loanId));
			logInfoEntity.setOperPerson(userId);
			logInfoEntity.setOperBeforeContent(beforeSeatTermValue);
			logInfoEntity.setOperAfterContent(seatTerm);
			logInfoEntityRepository.save(logInfoEntity);
		}
		int count = jdbcTemplate.update(strBuilder.toString(), args.toArray());
		return count > 0;
	}
	
	/**
	 * @desc 批量插入客户信息
	 * @param LoanCustInfoEntity 客户信息实体对象
	 * @return boolean
	 * @author zhoudl
	 * **/
	@Override
	public boolean batchInsertLoanCust(final List<LoanCustInfoEntity> loanCustList) throws SLException {
		StringBuffer  loanCustSql=new StringBuffer();
		loanCustSql.append("INSERT INTO BAO_T_LOAN_CUST_INFO(ID,CUST_NAME,CREDENTIALS_TYPE,CREDENTIALS_CODE,CREATE_DATE,VERSION,MEMO,CUST_GENDER, JOB_TYPE, CUST_EDUCATION, MARRIAGE_STATE) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
		jdbcTemplate.batchUpdate(loanCustSql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, loanCustList.get(i).getId());
				ps.setString(2, loanCustList.get(i).getCustName());
				ps.setString(3, loanCustList.get(i).getCredentialsType());
				ps.setString(4, loanCustList.get(i).getCredentialsCode());
				ps.setTimestamp(5, new Timestamp(new java.util.Date().getTime()));
				ps.setInt(6, loanCustList.get(i).getVersion());
				ps.setString(7, loanCustList.get(i).getMemo());
				ps.setString(8, loanCustList.get(i).getCustGender());
				ps.setString(9, loanCustList.get(i).getJobType());
				ps.setString(10, loanCustList.get(i).getCustEducation());
				ps.setString(11, loanCustList.get(i).getMarriageState());
			}
			
			@Override
			public int getBatchSize() {
				return loanCustList.size();
			}
		});
		return true;
	}

	/**
	 * @desc 批量插入借款信息
	 * @param LoanInfoEntity 借款信息实体对象
	 * @return boolean
	 * @author zhoudl
	 * **/
	@Override
	public boolean batchInsertLoan(final List<LoanInfoEntity> loanList) throws SLException {
		StringBuffer  loanSql=new StringBuffer();
		loanSql.append("INSERT INTO BAO_T_LOAN_INFO(ID,RELATE_TYPE,RELATE_PRIMARY,PRODUCT_CODE,DEBT_SOURCE_CODE,LOAN_CODE,CREDIT_ACCT_STATUS,LOAN_TERM,GRANT_DATE,IMPORT_DATE,INVEST_START_DATE,INVEST_END_DATE,")
		       .append("REPAYMENT_DAY,LOAN_AMOUNT,WORK_TYPE,LOAN_DESC,RECORD_STATUS,CREATE_USER,CREATE_DATE,VERSION,MEMO,HOLD_AMOUNT,HOLD_SCALE,REPAYMENT_METHOD,REPAYMENT_CYCLE,ASSET_TYPE_CODE)")
		       .append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		jdbcTemplate.batchUpdate(loanSql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, loanList.get(i).getId());
				ps.setString(2, "BAO_T_LOAN_CUST_INFO");
				ps.setString(3, loanList.get(i).getLoanCustInfoEntity().getId());
				ps.setString(4, loanList.get(i).getProductCode());
				ps.setString(5, loanList.get(i).getDebtSourceCode());
				ps.setString(6, loanList.get(i).getLoanCode());
				ps.setString(7, loanList.get(i).getCreditAcctStatus());
				ps.setString(8, CommonUtils.emptyToString(loanList.get(i).getLoanTerm()));
				ps.setTimestamp(9, loanList.get(i).getGrantDate());
				ps.setTimestamp(10, loanList.get(i).getImportDate());
				ps.setTimestamp(11, loanList.get(i).getInvestStartDate());
				ps.setTimestamp(12, loanList.get(i).getInvestEndDate());
				ps.setString(13, loanList.get(i).getRepaymentDay());
				ps.setBigDecimal(14, loanList.get(i).getLoanAmount());
				ps.setString(15, loanList.get(i).getWorkType());
				ps.setString(16, loanList.get(i).getLoanDesc());
				ps.setString(17, Constant.VALID_STATUS_VALID);
				ps.setString(18, loanList.get(i).getCreateUser());
				ps.setTimestamp(19,new Timestamp(new java.util.Date().getTime()));
				ps.setString(20, "0");
				ps.setString(21, "");
				ps.setBigDecimal(22, loanList.get(i).getHoldAmount());
				ps.setBigDecimal(23, loanList.get(i).getHoldScale());
				ps.setString(24, CommonUtils.emptyToString(loanList.get(i).getRepaymentMethod()));
				ps.setString(25, CommonUtils.emptyToString(loanList.get(i).getRepaymentCycle()));
				ps.setString(26, CommonUtils.emptyToString(loanList.get(i).getAssetTypeCode()));
			}
			
			@Override
			public int getBatchSize() {
				return loanList.size();
			}
		});
		return true;
	}

	/**
	 * @desc 批量插入借款详情信息
	 * @param LoanDetailInfoEntity 借款详情信息实体对象
	 * @return boolean
	 * @author zhoudl
	 * **/
	@Override
	public boolean batchInsertLoanDetail(final List<LoanDetailInfoEntity> loanDetailList) throws SLException {
		StringBuffer loanDetailSql=new StringBuffer();
		loanDetailSql.append("INSERT INTO BAO_T_LOAN_DETAIL_INFO(ID,LOAN_ID,CURR_TREM_END_DATE,LAST_EXPIRY,NEXT_EXPIRY,ALREADY_PAYMENT_TERM,CURR_TERM,CREDIT_REMAINDER_PRINCIPAL,")
		             .append("WEALTH_REMAINDER_PRINCIPAL,CURR_PAYABLE_INTEREST,CURR_RECEIVABLE_INTEREST,CREDIT_RIGHT_STATUS,EXEC_PV_STATUS,RECORD_STATUS,CREATE_USER,CREATE_DATE,VERSION,MEMO)")
		             .append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
        jdbcTemplate.batchUpdate(loanDetailSql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, loanDetailList.get(i).getId());
				ps.setString(2, loanDetailList.get(i).getLoanInfoEntity().getId());
				ps.setTimestamp(3, loanDetailList.get(i).getCurrTremEndDate());
				ps.setTimestamp(4, loanDetailList.get(i).getLastExpiry());
				ps.setTimestamp(5, loanDetailList.get(i).getNextExpiry());
				ps.setString(6, CommonUtils.emptyToString(loanDetailList.get(i).getAlreadyPaymentTerm()));
				ps.setString(7, CommonUtils.emptyToString(loanDetailList.get(i).getCurrTerm()));
				ps.setBigDecimal(8,loanDetailList.get(i).getCreditRemainderPrincipal());
				ps.setBigDecimal(9,loanDetailList.get(i).getWealthRemainderPrincipal());
				ps.setBigDecimal(10,loanDetailList.get(i).getCurrPayableInterest());
				ps.setBigDecimal(11,loanDetailList.get(i).getCurrReceivableInterest());
				ps.setString(12,loanDetailList.get(i).getCreditRightStatus());
				ps.setString(13,Constant.EXEC_UN_STATUS);
				ps.setString(14,Constant.VALID_STATUS_VALID);
				ps.setString(15,loanDetailList.get(i).getCreateUser());
				ps.setTimestamp(16,new Timestamp(new java.util.Date().getTime()));
				ps.setInt(17,0);
				ps.setString(18, "");
			}
			
			@Override
			public int getBatchSize() {
				return loanDetailList.size();
			}
		});
        return true;
	}
	
	/**
	 * @desc 查询债权来源编号
	 * @param debtSourceCode 债权来源编号
	 * @return java.util.List
	 * @author zhoudl
	 * **/
	@Override
	public List<Map<String, Object>> queryDebtSourceCode(Map<String, List<String>> debtSourceCodeMap) throws SLException {
		StringBuffer sql=new StringBuffer();
		
		sql.append("SELECT T.VALUE FROM COM_T_PARAM T WHERE T.TYPE='").append(ParamsNameConstant.LOAN_SOURCE_TYPE).append("' AND T.VALUE IN (");
		int len=0;
		for (Entry<String, List<String>> entry : debtSourceCodeMap.entrySet()) {
			String debtSourceCode=entry.getKey();
			len++;
				
			if(len==debtSourceCodeMap.size()){
				sql.append("'").append(debtSourceCode).append("'");
			}else{
				sql.append("'").append(debtSourceCode).append("',");
			}
				
		}
		sql.append(")");
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql.toString());
		return queryForList;
	}
	
	/**
	 * @desc 查询所有第三方机构信息
	 * @return java.util.List
	 * @author zhoudl
	 * **/
	@Override
	public List<Map<String,Object>> queryAllDebtSourceCode() throws SLException {
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT T.PARAMETER_NAME \"parameterName\",T.VALUE \"value\" FROM COM_T_PARAM T WHERE T.TYPE='").append(ParamsNameConstant.LOAN_SOURCE_TYPE).append("'");
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql.toString());
		return queryForList;
	}
	
	/**
	 * @desc 查询所有债权资质类型
	 * @return java.util.Map
	 * @author zhoudl
	 * **/
	@Override
	public Map<String,String> queryAllAssetType() throws SLException{
		Map<String,String> assetContainerMap=new HashMap<String, String>();
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT T.PARAMETER_NAME \"parameterName\",T.VALUE \"value\" FROM COM_T_PARAM T WHERE T.TYPE='").append(ParamsNameConstant.LOAN_ASSET_TYPE).append("'");
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql.toString());
		for (Map<String, Object> assetMap : queryForList) {
			//String assetName=CommonUtils.emptyToString(assetMap.get("parameterName"));
			String assetCode=CommonUtils.emptyToString(assetMap.get("value"));
			assetContainerMap.put(assetCode, assetCode);
		}
		return assetContainerMap;
	}

	/**
	 * @desc 验证债权编号是否存在
	 * @param loanCode 债权编号
	 * @return String
	 * @author zhoudl
	 * **/
	@SuppressWarnings("deprecation")
	@Override
	public String validateLoanCode(Map<String, List<String>> loanCodeMap) throws SLException {
		String validateMessage="";
		List<String> LoanCodeList=new Vector<String>();
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT COUNT(*) FROM BAO_T_LOAN_INFO T WHERE 1=1 ");
		for (Entry<String, List<String>> entry : loanCodeMap.entrySet()) {
			//String debtSourceCode=entry.getKey();
			List<String> loanCodeList=entry.getValue();
			LoanCodeList.addAll(loanCodeList);
		}
		
		if(!LoanCodeList.isEmpty()){
			sql.append(" AND (");
			int len=0;
			for (String loanCode : LoanCodeList) {
				len++;
				if(len==LoanCodeList.size()){
					sql.append(" T.LOAN_CODE='").append(loanCode).append("'");
				}else{
					sql.append(" T.LOAN_CODE='").append(loanCode).append("' OR ");
				}
			}
			sql.append(")");
		}
		
		int rows = jdbcTemplate.queryForInt(sql.toString());
		if(rows>0){
			validateMessage="债权编号存在重复,请检查!!!";
		}
		return validateMessage;
	}
	
	/**
	 * @desc 批量插入还款计划
	 * @param RepaymentPlanInfoEntity 还款计划对象
	 * @return boolean
	 * @author zhoudl
	 * **/
	@Override
	public boolean batchInsertRepaymentPlan(final List<RepaymentPlanInfoEntity> repaymentPlanList) throws SLException{
		StringBuffer sql=new StringBuffer();
		sql.append("INSERT INTO BAO_T_REPAYMENT_PLAN_INFO(ID,LOAN_ID,CURRENT_TERM,EXPECT_REPAYMENT_DATE,REPAYMENT_TOTAL_AMOUNT")
		   .append(",TERM_ALREADY_REPAY_AMOUNT,REPAYMENT_INTEREST,REPAYMENT_PRINCIPAL,REMAINDER_PRINCIPAL")
		   .append(",ADVANCE_CLEANUP_TOTAL_AMOUNT,REPAYMENT_STATUS,FACT_REPAYMENT_DATE,PENALTY_START_DATE")
		   .append(",ACCOUNT_MANAGE_EXPENSE,RECORD_STATUS,CREATE_USER,CREATE_DATE,VERSION,MEMO)")
		   .append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
	    jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, UUID.randomUUID().toString());
				ps.setString(2, repaymentPlanList.get(i).getLoanEntity().getId());
				ps.setInt(3, repaymentPlanList.get(i).getCurrentTerm());
				//预计还款日期转换为字符串yyyyMMdd
				String expectRepaymentDateString=DateUtils.showDateString(DateUtils.parseDate(repaymentPlanList.get(i).getExpectRepaymentDate(),"yyyy-MM-dd"));
				ps.setString(4, expectRepaymentDateString);
				ps.setBigDecimal(5, repaymentPlanList.get(i).getRepaymentTotalAmount());
				ps.setBigDecimal(6, repaymentPlanList.get(i).getTermAlreadyRepayAmount());
				ps.setBigDecimal(7, repaymentPlanList.get(i).getRepaymentInterest());
				ps.setBigDecimal(8, repaymentPlanList.get(i).getRepaymentPrincipal());
				ps.setBigDecimal(9, repaymentPlanList.get(i).getRemainderPrincipal());
				ps.setBigDecimal(10, repaymentPlanList.get(i).getAdvanceCleanupTotalAmount());
				ps.setString(11, repaymentPlanList.get(i).getRepaymentStatus());
				ps.setTimestamp(12, repaymentPlanList.get(i).getFactRepaymentDate());
				ps.setTimestamp(13, repaymentPlanList.get(i).getPenaltyStartDate());
				ps.setBigDecimal(14, repaymentPlanList.get(i).getAccountManageExpense());
				ps.setString(15, Constant.VALID_STATUS_VALID);
				ps.setString(16, repaymentPlanList.get(i).getCreateUser());
				ps.setTimestamp(17, new Timestamp(new java.util.Date().getTime()));
				ps.setInt(18, 0);
				ps.setString(19, "");
			}
			
			@Override
			public int getBatchSize() {
				return repaymentPlanList.size();
			}
		});
	    return true;
	}
	
	/**
	 * @desc 批量更新借款详情信息
	 * @param LoanDetailInfoEntity 借款详情信息对象
	 * @return boolean
	 * @author zhoudl
	 * **/
	@Override
	public boolean batchUpdateLoanDetail(final List<LoanDetailInfoEntity> loanDetailList) throws SLException {
		StringBuffer sql=new StringBuffer();
		sql.append("UPDATE BAO_T_LOAN_DETAIL_INFO D SET D.DAY_IRR=?,D.MONTH_IRR=?,D.YEAR_IRR=?,D.CURR_TREM_END_DATE=?,D.LAST_EXPIRY=?,D.NEXT_EXPIRY=?,D.ALREADY_PAYMENT_TERM=?,D.CURR_TERM=? WHERE D.LOAN_ID=?");
	    jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
                     ps.setBigDecimal(1, loanDetailList.get(i).getDayIrr());
                     ps.setBigDecimal(2, loanDetailList.get(i).getMonthIrr());
                     ps.setBigDecimal(3, loanDetailList.get(i).getYearIrr());
                     ps.setTimestamp(4,  loanDetailList.get(i).getCurrTremEndDate());
                     ps.setTimestamp(5, loanDetailList.get(i).getLastExpiry());
                     ps.setTimestamp(6, loanDetailList.get(i).getNextExpiry());
                     ps.setBigDecimal(7, loanDetailList.get(i).getAlreadyPaymentTerm());
                     ps.setBigDecimal(8, loanDetailList.get(i).getCurrTerm());
                     ps.setString(9, loanDetailList.get(i).getLoanInfoEntity().getId());
			}
			
			@Override
			public int getBatchSize() {
				return loanDetailList.size();
			}
		});
	    return true;
	}

	/**
	 * @desc 验证债权对应的还款计划是否已导入
	 * @param loanCode 债权编号
	 * @return String
	 * @author zhoudl
	 * **/
	@SuppressWarnings("deprecation")
	@Override
	public String repaymentPlanFindByLoanCode(Set<String> loanCodeSet) throws SLException {
		StringBuffer message=new StringBuffer();
        StringBuffer sql=new StringBuffer();
        sql.append("SELECT COUNT(*) FROM BAO_T_LOAN_INFO L,BAO_T_REPAYMENT_PLAN_INFO P WHERE L.ID=P.LOAN_ID");
        
        if((loanCodeSet!=null)&&(!loanCodeSet.isEmpty())){
			sql.append(" AND (");
			int len=0;
			for (String loanCode : loanCodeSet) {
				len++;
				if(len==loanCodeSet.size()){
					sql.append(" L.LOAN_CODE='").append(loanCode).append("'");
				}else{
					sql.append(" L.LOAN_CODE='").append(loanCode).append("' OR");
				}
				
			}
			sql.append(")");
		}
        int rows = jdbcTemplate.queryForInt(sql.toString());
        if(rows>0){
        	message.append("还款计划不能重复导入,请检查！");
        }
		return message.toString();
	}
	
	/**
	 * @desc 根据借款编号查询对应债权信息
	 * @param loanCode 债权编号
	 * @return java.util.List
	 * @author zhoudl
	 * **/
	@Override
	public List<Map<String, Object>> findByLoanCode(Set<String> loanCodeSet) throws SLException{
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT L.ID \"id\",L.LOAN_CODE \"loanCode\",L.IMPORT_DATE \"importDate\",L.INVEST_START_DATE \"investStartDate\",L.INVEST_END_DATE \"investEndDate\",L.LOAN_AMOUNT \"loanAmount\",")
           .append(" L.HOLD_AMOUNT \"holdAmount\",L.HOLD_SCALE \"holdScale\",L.REPAYMENT_METHOD \"repaymentMethod\",L.REPAYMENT_DAY \"repaymentDay\",L.LOAN_TERM \"loanTerm\",L.REPAYMENT_CYCLE \"repaymentCycle\"  FROM BAO_T_LOAN_INFO L WHERE 1=1 ");
		
		if((loanCodeSet!=null)&&(!loanCodeSet.isEmpty())){
			sql.append(" AND (");
			int len=0;
			for (String loanCode : loanCodeSet) {
				len++;
				if(len==loanCodeSet.size()){
					sql.append(" L.LOAN_CODE='").append(loanCode).append("'");
				}else{
					sql.append(" L.LOAN_CODE='").append(loanCode).append("' OR");
				}
				
			}
			sql.append(")");
		}
		List<Map<String, Object>> queryMapList = jdbcTemplate.queryForList(sql.toString());
		return queryMapList;
	}
	
	@Override
	public Page<Map<String, Object>> findLoanList(Map<String, Object> param) throws SLException {
		StringBuffer sqlString= new StringBuffer()
		.append(" select * ")
		.append(" from  ")
		.append("  (select a.*,row_number() over (partition by \"debtSourceCode\" order by create_date desc) rn from ")
		.append("  ( ")
		.append(" SELECT A.ID \"id\", A.LOAN_CODE \"loanCode\", N.PARAMETER_NAME \"debtSourceCode\", A.PRODUCT_CODE \"productCode\", ")
		.append("                  C.CUST_NAME \"custName\", TRUNC_AMOUNT_WEB(A.LOAN_AMOUNT) \"loanAmount\", TRUNC_AMOUNT_WEB(D.VALUE_REPAYMENT_BEFORE) \"currentValue\", ")
		.append("                  A.LOAN_TERM/A.REPAYMENT_CYCLE \"loanTerm\", A.REPAYMENT_DAY \"repaymentDay\", A.GRANT_DATE \"grantDate\", ")
		.append("                  A.IMPORT_DATE \"importDate\", B.CREDIT_RIGHT_STATUS \"creditRightStatus\", B.YEAR_IRR \"yearRate\",  ")
		.append("                  S.ALLOT_DATE \"allotDate\",B.ALREADY_PAYMENT_TERM \"alreadyPaymentTerm\", TRUNC_AMOUNT_WEB(A.HOLD_AMOUNT) \"holdAmount\", ")
		.append("                  A.HOLD_SCALE \"holdScale\", a.create_date, A.ASSET_TYPE_CODE \"assetTypeCode\", A.LOAN_DESC \"loanDesc\", A.REPAYMENT_METHOD \"repaymentMethod\"  ")
		.append("             FROM BAO_T_LOAN_INFO A ")
		.append("            INNER JOIN BAO_T_LOAN_DETAIL_INFO B ON B.LOAN_ID = A.ID ")
		.append("            INNER JOIN BAO_T_LOAN_CUST_INFO C ON C.ID = A.RELATE_PRIMARY ")
		.append("            INNER JOIN BAO_T_CREDIT_RIGHT_VALUE D ON D.LOAN_ID = A.ID AND D.VALUE_DATE = TO_CHAR(?, 'yyyyMMdd') ")
		.append("            INNER JOIN BAO_T_ALLOT_DETAIL_INFO T ON T.LOAN_ID = A.ID ")
		.append("            INNER JOIN BAO_T_ALLOT_INFO S ON S.ID = T.ALLOT_ID ")
		.append("            INNER JOIN BAO_T_PRODUCT_TYPE_INFO M ON M.ID = S.RELATE_PRIMARY ")
		.append(String.format(" INNER JOIN COM_T_PARAM N ON N.VALUE = A.DEBT_SOURCE_CODE AND N.TYPE = '%s'", ParamsNameConstant.LOAN_SOURCE_TYPE))
		.append("           WHERE  B.CREDIT_RIGHT_STATUS = '正常' ")
		.append("           AND S.ALLOT_STATUS != '已撤销' ")
		.append("           AND S.RELATE_TYPE = 'BAO_T_PRODUCT_TYPE_INFO' ")
		.append("           AND A.DEBT_SOURCE_CODE NOT IN ('052', '054') ") // 排除债权来源“企业借款”和“海程融资租赁”
		.append("           AND M.TYPE_NAME = ? %s ");
		
		StringBuffer whereSql = new StringBuffer(); 
		List<Object> objList=new ArrayList<Object>();
		objList.add(new java.util.Date());
		objList.add((String)param.get("productType"));
		
		if(!StringUtils.isEmpty(param.get("allotDateBegin"))){
			whereSql.append(" and ALLOT_DATE >= ?");
			objList.add(DateUtils.parseStandardDate((String)param.get("allotDateBegin")));
		}
		
		if(!StringUtils.isEmpty(param.get("allotDateEnd"))){
			whereSql.append(" and ALLOT_DATE <= ?");
			objList.add(DateUtils.getEndDate(DateUtils.parseStandardDate((String)param.get("allotDateEnd"))));
		}
		
		if(!StringUtils.isEmpty(param.get("debtSource"))){
			sqlString.append(" and DEBT_SOURCE_CODE = ?");
			objList.add(param.get("debtSource"));
		}
		
		if(!StringUtils.isEmpty(param.get("productCode"))){
			sqlString.append(" and PRODUCT_CODE = ?");
			objList.add(param.get("productCode"));
		}
		
		if(!StringUtils.isEmpty(param.get("yearRateBegin"))){
			sqlString.append(" and B.YEAR_IRR >= ?");
			objList.add(new BigDecimal((String)param.get("yearRateBegin")));
		}
		
		if(!StringUtils.isEmpty(param.get("yearRateEnd"))){
			sqlString.append(" and B.YEAR_IRR <= ?");
			objList.add(new BigDecimal((String)param.get("yearRateEnd")));
		}
		
		if(!StringUtils.isEmpty(param.get("loanTerm"))){
			sqlString.append(" and LOAN_TERM = ?");
			objList.add(param.get("loanTerm"));
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
		
		sqlString.append(")a ) order by rn,\"debtSourceCode\" ");
		
		return repositoryUtil.queryForPageMap(String.format(sqlString.toString(), whereSql.toString()), objList.toArray(), (int)param.get("start"), (int)param.get("length"));
	}

	@Override
	public List<Map<String, Object>> findRepaymentPlanList(String id, BigDecimal holdScale) throws SLException {
		// TODO Auto-generated method stub
		StringBuffer sqlString= new StringBuffer()
		.append(" SELECT T.CURRENT_TERM \"currentTerm\", NVL((T.REPAYMENT_PRINCIPAL + T.REPAYMENT_INTEREST) / ? ,0) \"repaymentAmount\", T.REPAYMENT_STATUS \"repaymentStatus\" ")
		.append(" FROM BAO_T_REPAYMENT_PLAN_INFO T ")
		.append(" WHERE T.LOAN_ID = ? ")
		.append(" ORDER BY T.CURRENT_TERM ASC");
		
		return repositoryUtil.queryForMap(sqlString.toString(), new Object[]{ holdScale, id});
	}
	
	@Override
	public Map<String, Object> findLoanDeatilById(String loanId)throws SLException	{
		StringBuffer sqlString= new StringBuffer()
		.append(" SELECT A.ID \"id\", A.LOAN_CODE \"loanCode\", N.PARAMETER_NAME \"debtSourceCode\", A.PRODUCT_CODE \"productCode\",  ")
		.append("        C.CUST_NAME \"custName\", TRUNC_AMOUNT_WEB(A.LOAN_AMOUNT) \"loanAmount\", TRUNC_AMOUNT_WEB(D.VALUE_REPAYMENT_BEFORE) \"currentValue\",  ")
		.append("        A.LOAN_TERM/A.REPAYMENT_CYCLE \"loanTerm\", A.REPAYMENT_DAY \"repaymentDay\", A.GRANT_DATE \"grantDate\",  ")
		.append("        A.IMPORT_DATE \"importDate\", B.CREDIT_RIGHT_STATUS \"creditRightStatus\", B.YEAR_IRR \"yearRate\", " )
		.append("        TRUNC_AMOUNT_WEB(A.HOLD_AMOUNT) \"holdAmount\", A.HOLD_SCALE \"holdScale\", A.REPAYMENT_METHOD \"repaymentMethod\" ")
		.append("   FROM BAO_T_LOAN_INFO A ")
		.append("  INNER JOIN BAO_T_LOAN_DETAIL_INFO B ON B.LOAN_ID = A.ID ")
		.append("  INNER JOIN BAO_T_LOAN_CUST_INFO C ON C.ID = A.RELATE_PRIMARY ")
		.append("  INNER JOIN BAO_T_CREDIT_RIGHT_VALUE D ON D.LOAN_ID = A.ID AND D.VALUE_DATE = TO_CHAR(?, 'yyyyMMdd') ")
		.append(String.format(" INNER JOIN COM_T_PARAM N ON N.VALUE = A.DEBT_SOURCE_CODE AND N.TYPE = '%s'", ParamsNameConstant.LOAN_SOURCE_TYPE))
		.append("  WHERE B.CREDIT_RIGHT_STATUS = '正常' AND A.ID = ?");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[]{new java.util.Date(), loanId});
		if(list == null || list.size() == 0) {
			return new HashMap<String, Object>();
		}
		return list.get(0);
	}

	@Override
	public Map<String, Object> findLoanListCount(Map<String, Object> param) {
		
		StringBuffer sqlString= new StringBuffer()
		.append(" SELECT NVL(SUM(TRUNC_AMOUNT_WEB(D.VALUE_REPAYMENT_BEFORE)), 0) \"totalPv\", NVL(SUM(TRUNC_AMOUNT_WEB(A.LOAN_AMOUNT)),0) \"totalLoanAmount\", COUNT(A.ID) \"totalLoans\" ")
		.append(" FROM BAO_T_LOAN_INFO A  ")
		.append(" INNER JOIN BAO_T_LOAN_DETAIL_INFO B ON B.LOAN_ID = A.ID ")
		.append(" INNER JOIN BAO_T_CREDIT_RIGHT_VALUE D ON A.ID = D.LOAN_ID AND D.VALUE_DATE = TO_CHAR(?, 'YYYYMMDD') ")
		.append("      WHERE B.CREDIT_RIGHT_STATUS = '正常' ")
		.append("      AND A.DEBT_SOURCE_CODE NOT IN ('052', '054') ") // 排除债权来源“企业借款”和“海程融资租赁”
		.append("      AND A.ID IN ( ")
		.append("            SELECT T.LOAN_ID ")
		.append("            FROM BAO_T_ALLOT_DETAIL_INFO T ")
		.append("            INNER JOIN BAO_T_ALLOT_INFO S ON S.ID = T.ALLOT_ID ")
		.append("            INNER JOIN BAO_T_PRODUCT_TYPE_INFO M ON M.ID = S.RELATE_PRIMARY ")
		.append("            WHERE S.ALLOT_STATUS != '已撤销'  ")
		.append("            AND S.RELATE_TYPE = 'BAO_T_PRODUCT_TYPE_INFO' ")
		.append("            AND M.TYPE_NAME = ? %s")
		.append("      )  ");
		
		StringBuffer whereSql = new StringBuffer(); 
		List<Object> objList=new ArrayList<Object>();
		objList.add(new java.util.Date());
		objList.add((String)param.get("productType"));
			
		if(!StringUtils.isEmpty(param.get("allotDateBegin"))){
			whereSql.append(" and ALLOT_DATE >= ?");
			objList.add(DateUtils.parseStandardDate((String)param.get("allotDateBegin")));
		}
		
		if(!StringUtils.isEmpty(param.get("allotDateEnd"))){
			whereSql.append(" and ALLOT_DATE <= ?");
			objList.add(DateUtils.getEndDate(DateUtils.parseStandardDate((String)param.get("allotDateEnd"))));
		}
		
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
			objList.add(param.get("yearRateBegin"));
		}
		
		if(!StringUtils.isEmpty(param.get("yearRateEnd"))){
			sqlString.append(" and YEAR_IRR <= ?");
			objList.add(param.get("yearRateEnd"));
		}
		
		if(!StringUtils.isEmpty(param.get("loanTerm"))){
			sqlString.append(" and LOAN_TERM = ?");
			objList.add(param.get("loanTerm"));
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
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(String.format(sqlString.toString(), whereSql.toString()), objList.toArray());
		if(list == null || list.size() == 0) {
			return new HashMap<String, Object>();
		}
		return list.get(0);
	}

	@Override
	public Map<String, Object> queryLoanInfoDetailByLoanId(String loanId) throws SLException {
		StringBuffer SqlString = new StringBuffer()
				.append("   select l.loan_code        \"loanCode\",   ")
				.append("          p.parameter_name   \"assetTypeCode\",   ")
				.append("          l.loan_desc        \"loanDesc\",   ")
				.append("          l.repayment_method \"repaymentMethod\",   ")
				.append("          l.loan_amount      \"loanAmount\",   ")
				.append("          l.loan_term        \"loanTerm\",   ")
				.append("          '风险保障金'       \"guaranteeMethod\",   ")
				.append("          substr(lc.cust_name, 0, 1) || '**'      \"loanCustName\",   ")
				.append("          lc.cust_gender     \"sex\",   ")
				.append("          case when length(lc.credentials_code)=18 then to_number(to_char(sysdate, 'yyyy')) - to_number(substr(lc.credentials_code,7,4)) else 0 end \"age\",   ")
				.append("          lc.job_type                                      \"jobType\",   ")
				.append("          lc.cust_education                                 \"education\",   ")
				.append("          lc.marriage_state                                 \"marriage\",   ")
				.append("          decode(lc.credentials_type, '1', '身份证') \"credentialsType\",   ")
				.append("          substr(lc.credentials_code, 0, 2) || '**************' || substr(lc.credentials_code, length(lc.credentials_code) - 1, 2)   \"credentialsCode\"       ")
				.append("   from bao_t_loan_info l  ")
				.append("      inner join bao_t_loan_cust_info lc on lc.id = l.relate_primary    ")
				.append("      inner join com_t_param p on l.debt_source_code = p.value and p.type = 'baoLoanSource'   ")
				.append("   where l.id = ? ");
		
		List<Object> objList = Lists.newArrayList();
		objList.add(loanId);
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(SqlString.toString(), objList.toArray());
		if(list == null || list.size() == 0) {
			return new HashMap<String, Object>();
		}
		return list.get(0);
	}

	/**
	 * 业务管理列表查询
	 * 
	 * 
	 * @author zhiwen_feng
	 * @date 2016-11-29
	 * 
	 * @param params
	 * @throws SLException
	 */
	@Override
	public Map<String, Object> queryBusinessManageList(Map<String, Object> params) throws SLException {
		List<Object> paramList=new ArrayList<Object>();
		StringBuilder SqlString = new StringBuilder()
				.append("  select loan.id                \"loanId\", ")
				.append("        loan.loan_code         \"loanCode\", ")
				.append("        lc.cust_name           \"custName\", ")
				.append("        loan.loan_type         \"loanType\", ")
				.append("        loan.publish_date      \"publishDate\", ")
				.append("        loan.rasie_days        \"rasieEndDate\", ")
				.append("        loan.loan_amount       \"loanAmount\", ")
				.append("        round(d.year_irr*100,1)             \"yearIrr\", ")
				.append("        loan.loan_term         \"loanTerm\", ")
				.append("        loan.loan_unit         \"loanUnit\", ")
				.append("        nvl(trunc(round(loan.award_rate,8)*100,2),0) \"awardRate\", ")
				.append("        loan.repayment_method  \"repaymentMethod\", ")
				.append("        loan.loan_status       \"loanStatus\", ")
				.append("        '否'       \"isEdit\", ")
				.append("        loan.attachment_flag  \"attachmentFlag\" ")
				.append("      , CASE WHEN loan.SEAT_TERM = -1 OR loan.SEAT_TERM IS NULL THEN '不可转让' WHEN loan.SEAT_TERM = 0 THEN '可立即转让' ELSE '持有'||loan.SEAT_TERM||'天可转让' END \"seatTerm\" ")
				//by guoyk 
				.append("       , case when loan.newer_flag = '新手标' then 'Y' else 'N' end \"isNewerFlag\", ")
				.append("        case when loan.is_allow_auto_invest = '是' then 'Y' else 'N' end\"isAllowAutoInvest\", ")
				.append("        loan.loan_title  \"loanTitle\" ")
//				.append("        , (SELECT CASE WHEN count(1) > 0 THEN '是' ELSE '否' END ")
//				.append("             FROM BAO_T_ATTACHMENT_INFO att ")
//				.append("            WHERE att.SHOW_TYPE='外部' ")
//				.append("              AND att.RELATE_PRIMARY IN ( ")
//				.append("                                SELECT ID FROM BAO_T_AUDIT_INFO ")
//				.append("                                 WHERE RELATE_PRIMARY = loan.ID) ")
//				.append("          ) \"isEdit\" ")
				.append("  from bao_t_loan_info loan ")
				.append("  inner join bao_t_loan_cust_info lc on lc.id = loan.relate_primary")
				.append("  inner join BAO_T_LOAN_DETAIL_INFO d on d.loan_id = loan.id")
				.append("  where loan.loan_status NOT IN ('待审核','审核回退','拒绝')   ");
		if(!StringUtils.isEmpty(params.get("loanCode"))){
			SqlString.append(" and loan.loan_code = ?");
			paramList.add(params.get("loanCode"));	
		}
		if(!StringUtils.isEmpty(params.get("custName"))){
			SqlString.append(" and lc.cust_name = ?");
			paramList.add(params.get("custName"));
		}
		if(!StringUtils.isEmpty(params.get("loanType"))){
			SqlString.append(" and loan.loan_type = ?");
			paramList.add(params.get("loanType"));
		}
		if(!StringUtils.isEmpty(params.get("loanStatus"))){
			SqlString.append(" and loan.loan_status = ?");
			paramList.add(params.get("loanStatus"));
		}
		if(!StringUtils.isEmpty(params.get("loanTerm"))){
			SqlString.append(" and loan.loan_term = ?");
			paramList.add(params.get("loanTerm"));
		}
		if(!StringUtils.isEmpty(params.get("loanUnit"))){
			SqlString.append(" and loan.loan_unit = ?");
			paramList.add(params.get("loanUnit"));
		}
		if(!StringUtils.isEmpty(params.get("repaymentMethod"))){
			SqlString.append(" and loan.repayment_method = ?");
			paramList.add(params.get("repaymentMethod"));
		}
		if(!StringUtils.isEmpty(params.get("publishDateStart"))){
			SqlString.append(" and to_char(loan.publish_date, 'yyyy-MM-dd') >= ?");
			paramList.add(params.get("publishDateStart"));
		}
		if(!StringUtils.isEmpty(params.get("publishDateEnd"))){
			SqlString.append(" and to_char(loan.publish_date, 'yyyy-MM-dd') <= ?");
			paramList.add(params.get("publishDateEnd"));
		}
		if(!StringUtils.isEmpty(params.get("attachmentFlag"))){
			SqlString.append(" and loan.attachment_flag = ?");
			paramList.add(params.get("attachmentFlag"));
		}
		
		SqlString.append("  order by decode(loan.loan_status, '待审核', 1, '审核回退', 2, '通过', 3, '待发布', 4, '募集中', 5, '满标复核', 6, '复核通过', 7, '正常', 8, '逾期', 9, '提前结清', 10, '已到期', 11, '流标', 12, '复核拒绝', 13, '拒绝', 14), loan.loan_term, loan.loan_code desc ");
		int start=CommonUtils.emptyToInt(params.get("start"));
		int length=CommonUtils.emptyToInt(params.get("length"));
		Page<Map<String, Object>> queryPage = repositoryUtil.queryForPageMap(SqlString.toString(), paramList.toArray(), start, length);
		Map<String, Object> mapAmount=queryAmoutInfo(params);
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("iTotalDisplayRecords", queryPage.getTotalElements());
		queryMap.put("data", queryPage.getContent());
		queryMap.put("investCustReserveAmount",mapAmount.get("investCustReserveAmount"));
		queryMap.put("untreatedAmount",mapAmount.get("untreatedAmount"));
		queryMap.put("raiseSuccess",mapAmount.get("raiseSuccess"));
		SqlString = new StringBuilder()
		.append("     SELECT a.RELATE_PRIMARY \"loanId\", CASE WHEN count(1) > 0 THEN '是' ELSE '否' END \"isEdit\" ")
		.append("      FROM BAO_T_ATTACHMENT_INFO att,  BAO_T_AUDIT_INFO a ")
		.append("     WHERE att.RELATE_PRIMARY = a.id ")
		.append("     and att.SHOW_TYPE='外部'  ");
		
		List<Map<String, Object>> list = queryPage.getContent();
		if(list != null && list.size() > 0){
			List<String> loanIds = Lists.transform(list, new Function<Map<String, Object>, String>() {
				@Override
				public String apply(Map<String, Object> input) {
					return (String)input.get("loanId");
				}
			});
			Map<String, Object> searchParam = Maps.newConcurrentMap();
			searchParam.put("loanIds", loanIds);
			SqlCondition sql = new SqlCondition(SqlString, searchParam);
			sql.addList("loanIds", "a.RELATE_PRIMARY")
			.addSql(" group by a.RELATE_PRIMARY");
			
			List<Map<String, Object>> editList = repositoryUtil.queryForMap(sql.toString(), sql.toArray());
			for(Map<String, Object> m : list) {
				for(Map<String, Object> e : editList) {
					if(e.get("loanId").toString().equals(m.get("loanId").toString())) {
						m.put("isEdit", e.get("isEdit"));
						break;
					}
				}
			}
		}
		
		return queryMap;
	}

	/*
	1.invest_mode加入(是否转让通过持有表转让比例是否为0判断)
	1)未转让，持有比例*当期本息 
	2)转让
	  aa.时间<2017.5.31，持有比例*当期本息 
	  ab.时间>=2017.5.31
		选出上个还款日<转让时间<=下个还款日(从转让表里查询)          	  
		i.非还款日[转让时间！=下个还款日](转让时间-上个还款日)/(下个还款日-上个还款日)*转让比例*当期本息
		ii.还款日(未还款的情况下转让时间<还款计划表实际还款时间) (转让时间-上个还款日-1)/(下个还款日-上个还款日)*转让比例*当期本息

	2、invest_mode转让
	1)未转让
	非购买当期 持有比例*当期本息 
	购买当期
		ba.非还款日当天购买(下个还款日-转让时间)/(下个还款日-上个还款日）*持有比例*当期本息
		bb.还款日当天购买(未还款的情况下)1/(下个还款日-上个还款日)*持有比例*当期本息
	2)转让
		同[invest_mode加入]的[2)已转让]
	总结：待收收益等于 = 剩余持比的收益(如果当月是转让买进=剩余持比当月的收益+剩余收益) + 转让人当月转出持比的收益
	*/
	@Override
	public Map<String, Object> queryMyDisperseList(Map<String, Object> params) throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		List<Object> listObject = new ArrayList<Object>();
		listObject.add((String)params.get("onlineTime"));/* 债权转让改造上线日2017-06-01(具体时间这天左右) */
		StringBuilder sql = new StringBuilder()
		.append(" select nvl(cai.total_amount, 0) \"award\" , ")
				.append(" i.invest_red_packet as \"investRedPacket\",i.red_packet_type as \"redPacketType\",case when l.loan_status in ('流标', '已到期', '提前结清') then 0 ")
		.append("             when lta.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') ")/* 债权转让改造上线日2017-06-01(具体时间这天左右) */
		.append("             THEN trunc(decode(i.invest_status, '投资中', i.invest_amount/l.loan_amount, h.hold_scale)*(select nvl(sum(r.repayment_interest), 0) from bao_t_repayment_plan_info r where r.loan_id = l.id and r.repayment_status = '未还款') , 2) ")
		.append("             else ")
		.append("             /* 剩余持比的收益 */ ")
		.append("             CASE WHEN i.INVEST_MODE = '转让' THEN  ")
		.append("                  CASE WHEN to_char(?, 'yyyyMMdd') /* ba.非还款日当天购买(下个还款日-转让时间)/(下个还款日-上个还款日）*持有比例*当期本息 */ ")
		.append("                            < (SELECT min(r.EXPECT_REPAYMENT_DATE) /* 购买时间当期的还款日时间 */ ")
		.append("                                 FROM bao_t_repayment_plan_info r  ")
		.append("                                WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date ")
		.append("                                  AND r.loan_id = l.id) ")
		.append("                       THEN trunc(h.hold_scale * (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                     from bao_t_repayment_plan_info r  ")
		.append("                                    where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                      and r.loan_id = l.id )  ")
		.append("                                * (d.NEXT_EXPIRY - to_date(i.effect_date, 'yyyyMMdd')) ")
		.append("                                / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE))) ")
		.append("                            , 2) ")
		.append("                          + trunc(h.hold_scale *  ")
		.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r  ")
		.append("                                   where r.EXPECT_REPAYMENT_DATE > to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                     and r.loan_id = l.id ) ")
		.append("                            , 2) ")
		.append("                       WHEN to_char(?, 'yyyyMMdd') /* bb.还款日当天购买(未还款的情况下)1/(下个还款日-上个还款日)*持有比例*当期本息  */ ")
		.append("                            >= (SELECT min(r.EXPECT_REPAYMENT_DATE) /* 购买时间当期的还款日时间 */ ")
		.append("                                FROM bao_t_repayment_plan_info r  ")
		.append("                               WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date ")
		.append("                                 AND r.loan_id = l.id) ")
		.append("                                  AND '未还款' = (SELECT r.REPAYMENT_STATUS ")
		.append("                                                  FROM bao_t_repayment_plan_info r  ")
		.append("                                                 WHERE r.EXPECT_REPAYMENT_DATE = (SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date AND r.loan_id = l.id) ")
		.append("                                                   AND r.loan_id = l.id) ")
		.append("                       THEN trunc(h.hold_scale * (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                     from bao_t_repayment_plan_info r  ")
		.append("                                    where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                      and r.loan_id = l.id )  ")
		.append("                                  * (d.NEXT_EXPIRY - to_date(i.effect_date, 'yyyyMMdd') + case when to_date(i.effect_date, 'yyyyMMdd') = d.NEXT_EXPIRY then 1 else 0 end) ")
		.append("                                  / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE))) ")
		.append("                            , 2) ")
		.append("                          + trunc(h.hold_scale *  ")
		.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r  ")
		.append("                                   where r.EXPECT_REPAYMENT_DATE > to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                     and r.loan_id = l.id ) ")
		.append("                            , 2) ")
		.append("                       ELSE trunc(h.hold_scale *  ")
		.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r  ")
		.append("                                   where r.repayment_status = '未还款' ")
		.append("                                     and r.loan_id = l.id)  ")
		.append("                            , 2) END ")
		.append("             ELSE trunc(h.hold_scale *  ")
		.append("                       (select nvl(sum(r.repayment_interest), 0) ")
		.append("                          from bao_t_repayment_plan_info r  ")
		.append("                         where r.repayment_status = '未还款' ")
		.append("                           and r.loan_id = l.id)  ")
		.append("                  , 2)   ")
//		.append(" trunc(case when l.newer_flag = '新手标' ")
//		.append("            then ")
//		.append("                 case when l.loan_unit = '天'  ")
//		.append("                      then i.invest_amount * l.loan_term/360 * l.award_rate  ")
//		.append("                      else (select sum((cc.repayment_principal + cc.remainder_principal)*h.hold_scale* ")
//		.append("                       (case when l.repayment_method = '到期还本付息' then l.loan_term when l.repayment_method = '等额本息'then 1 when l.repayment_method = '每期还息到期付本' then 1 else 0 end)    ")
//		.append("                       * l.award_rate /12 )from bao_t_repayment_plan_info cc where cc.loan_id = l.id and cc.repayment_status = '未还款' )  ")
//		.append("                      end  ")
//		.append("            else 0 END ,2) end")
 		.append(" +(SELECT  trunc(sum(case when l.repayment_method = '等额本息'  ")
		.append("              then cc.REPAYMENT_INTEREST * nvl(l.award_rate,0)/d.year_irr * h.hold_scale         ")
		.append("              when l.repayment_method = '每期还息到期付本'         ")
		.append("              then  (cc.repayment_principal + cc.remainder_principal)* nvl(l.award_rate,0) *30/360*h.hold_scale         ")
		.append("              else  (cc.repayment_principal + cc.remainder_principal)* nvl(l.award_rate,0) *decode(l.loan_unit,'天',1,'月',30)*l.loan_term/360*h.hold_scale                        ")
		.append("              end ) ,2) from  bao_t_repayment_plan_info cc where cc.loan_id = l.id and cc.repayment_status = '未还款' ) ")
		.append("           end+ /* 转让人当月转出持比的收益 */ ")
		.append("             /* i.非还款日[转让时间！=下个还款日](转让时间-上个还款日)/(下个还款日-上个还款日)*转让比例*当期本息 */ ")
		.append("             CASE WHEN TRUNC(?) < d.NEXT_EXPIRY /* (c1)条件包括上个还款日当天已还款之后购买的的转让 */ ")
		.append("                  THEN TRUNC(nvl((SELECT sum(lt.TRADE_SCALE /* 转让比例 */ ")
		.append("                                          * (select nvl(sum(r.repayment_interest), 0)  ")
		.append("                                               from bao_t_repayment_plan_info r  ")
		.append("                                              where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                                and r.loan_id = l.id ) ")
		.append("                                          * (to_date(invest.effect_date, 'yyyyMMdd') - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)))/* 还款日当天已还款之后的数据，下期没有收益 */ ")
		.append("                                          / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)))) ")
		.append("                                    FROM BAO_T_LOAN_TRANSFER lt, bao_t_wealth_hold_info wh, bao_t_invest_info invest ")
		.append("                                   WHERE invest.id = wh.invest_id ")
		.append("                                     AND wh.id = lt.receive_hold_id ")
		.append("                                     AND to_date(invest.effect_date, 'yyyyMMdd') < ? ")
		.append("                                     AND to_date(invest.effect_date, 'yyyyMMdd') >= nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)) /* 配合(c1) */ ")
		.append("                                     AND lt.SENDER_HOLD_ID = h.id) ")
		.append("                       , 0), 2) ")
		.append("                  /* ii.还款日(未还款的情况下转让时间<还款计划表实际还款时间) (转让时间-上个还款日-1)/(下个还款日-上个还款日)*转让比例*当期本息  */ ")
		.append("                  WHEN TRUNC(?) >= d.NEXT_EXPIRY /* (c2)当期未还款,如果已还款d.NEXT_EXPIRY变为下期还款日期在上面的when条件中 */ ")
		.append("                  THEN TRUNC(nvl((SELECT sum(lt.TRADE_SCALE /* 转让比例 */ ")
		.append("                                          * (select nvl(r.repayment_interest, 0) /* 当期本息 */ ")
		.append("                                               from bao_t_repayment_plan_info r  ")
		.append("                                              where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                                and r.loan_id = l.id ) ")
		.append("                                          * (to_date(invest.effect_date, 'yyyyMMdd') - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)) - case when to_date(invest.effect_date, 'yyyyMMdd') = d.next_expiry then 1 else 0 end) ")
		.append("                                          / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)))) ")
		.append("                                    FROM BAO_T_LOAN_TRANSFER lt, bao_t_wealth_hold_info wh, bao_t_invest_info invest ")
		.append("                                   WHERE invest.id = wh.invest_id ")
		.append("                                     AND wh.id = lt.receive_hold_id ")
		.append("                                     AND to_date(invest.effect_date, 'yyyyMMdd') <= d.NEXT_EXPIRY ")
		.append("                                     AND to_date(invest.effect_date, 'yyyyMMdd') >= nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)) ")
		.append("                                     AND lt.SENDER_HOLD_ID = h.id) ")
		.append("                       , 0), 2) ")
		.append("                  ELSE 0 END")
		.append("           END \"waitingIncome\" ")
		.append("           , l.id              \"disperseId\",     ")
		.append("           i.INVEST_DATE              \"expLoanInvestDate\",     ")//体验标购买时间-huifei
		.append("           i.EXPIRE_DATE              \"expLoanExpireDate\",     ")//体验标到期时间
		.append("           l.loan_type       \"disperseType\",     ")
		.append("           l.loan_code       \"creditNo\",     ")
		.append("           d.year_irr        \"yearRate\",     ")
		.append("           nvl(l.award_rate ,0)  \"awardRate\",    ")
		.append("           i.invest_amount   \"investAmount\",     ")
		.append("           l.loan_term       \"typeTerm\",     ")
		.append("           trunc(p.already_invest_scale*100, 2)      \"investScale\",     ")
		.append("           decode(i.invest_status,'投资中','出借中',i.invest_status)    \"disperseStatus\",     ")
		.append("           to_char(i.create_date, 'yyyy-MM-dd HH24:mi:ss')     \"disperseDate\",    ")
//		.append("           trunc(decode(i.invest_status, '投资中', i.invest_amount/l.loan_amount, h.hold_scale)*(select nvl(sum(r.repayment_interest), 0) from bao_t_repayment_plan_info r where r.loan_id = l.id and r.repayment_status = '未还款') , 2) \"waitingIncome\",    ")
//		.append("           (select trunc(nvl(sum(rd.trade_amount), 0), 2) from bao_t_account_flow_info f, bao_t_payment_record_info pr, bao_t_payment_record_detail rd where f.relate_primary = i.id and f.id = pr.relate_primary and pr.id = rd.pay_record_id and rd.subject_type in ('还风险金逾期费用', '利息', '逾期费用', '违约金') ) \"getIncome\",   ")
		.append("           nvl(afi.trade_amount,0) \"getIncome\",  ")
		.append("           TRUNC_AMOUNT_WEB(decode(i.invest_status, '投资中', i.invest_amount/l.loan_amount, h.hold_scale)*d.credit_remainder_principal) \"remainderPrincipal\", ")
		.append("           to_char(to_date(i.expire_date, 'yyyyMMdd'), 'yyyy-MM-dd') \"endDate\",   ")
		.append("           to_char(to_date(i.effect_date, 'yyyyMMdd'), 'yyyy-MM-dd') \"releaseDate\",   ")
		.append("           l.loan_desc         \"loanUse\",  ")
		.append("           i.id                \"investId\",   ")
		.append("           l.LOAN_TITLE        \"loanTitle\",   ")
		.append("           i.TRANSFER_APPLY_ID \"transferApplyId\"  ")
		.append("           , l.newer_flag \"newerFlag\"  ")
		.append("           , l.LOAN_UNIT \"loanUnit\"  ")
		.append("           , case when b.invest_source = 'auto' then '自动投标' when b.invest_source = 'reserveinvest' then '优先投' else '手动投标' end \"investSource\"  ")
		//.append("           , CASE WHEN (SELECT instr(value, l.LOAN_TYPE) FROM COM_T_PARAM WHERE TYPE='shanLinCaiFuSet' AND PARAMETER_NAME='可投产品') > 0 then '否' else '否' end \"reseverFlag\" ")
		.append("           , '否' \"reseverFlag\" ")
		.append("      from BAO_T_INVEST_INFO i ")
		.append("      inner join bao_t_invest_detail_info b on b.invest_id = i.id ")
		.append("      inner join bao_t_loan_info l on l.id = i.loan_id   ")
		.append("       left join bao_t_wealth_hold_info h on h.invest_id = i.id and h.loan_id = i.loan_id ")
		.append("      inner join BAO_T_PROJECT_INVEST_INFO p on p.LOAN_ID = l.id  ")
		.append("      inner join bao_t_loan_detail_info d on d.loan_id = l.id      ")
		.append("       LEFT JOIN BAO_T_LOAN_TRANSFER_APPLY lta ON lta.ID = i.TRANSFER_APPLY_ID ")
		.append("       LEFT JOIN ( ")
		.append("                 select f.relate_primary, trunc(nvl(sum(rd.trade_amount), 0), 2) trade_amount ")
		.append("                    from bao_t_account_flow_info     f, ")
		.append("                         bao_t_payment_record_info   pr, ")
		.append("                         bao_t_payment_record_detail rd ")
		.append("                   where f.id = pr.relate_primary ")
		.append("                     and pr.id = rd.pay_record_id ")
		.append("                     and rd.subject_type in ")
		.append("                         ('还风险金逾期费用', '利息', '逾期费用', '违约金','优选项目奖励收益') ")
		.append("                     group by f.relate_primary ")
		.append("            ) afi on afi.relate_primary = i.id ")
				.append(" left join BAO_T_CUST_ACTIVITY_INFO cai on cai.loan_id = l.id and cai.cust_id = ? and i.CUST_ACTIVITY_ID = cai.id ")
		.append("      where i.cust_id = ? ");
		
		Date date = new Date();
		listObject.add(date);
//		listObject.add(date);
		listObject.add(date);
		listObject.add(date);
		listObject.add(date);
		listObject.add(date);



		listObject.add(params.get("custId"));
		listObject.add(params.get("custId"));

		if(!StringUtils.isEmpty(params.get("startDate"))){
			sql.append(" and i.CREATE_DATE >= ? ");
			listObject.add(DateUtils.parseStandardDate(params.get("startDate").toString()));
		}
		if(!StringUtils.isEmpty(params.get("endDate"))){
			sql.append(" and i.CREATE_DATE < ? ");
			listObject.add(DateUtils.getAfterDay(DateUtils.parseStandardDate(params.get("endDate").toString()), 1));
		}
		if (!StringUtils.isEmpty(params.get("productType"))) {
			if ("优选项目".equals((String) params.get("productType"))) {
				sql.append("   and i.invest_mode ='加入' ");
			} else if ("债权转让".equals((String) params.get("productType"))) {
				sql.append("   and i.invest_mode ='转让' ");
			} else if ("优先投".equals((String) params.get("productType"))) {
				sql.append("   and i.product_id is not null ");
			}
		}
		if (!StringUtils.isEmpty(params.get("disperseStatus"))) {
			if ("已结束".equals((String) params.get("disperseStatus"))) {
				sql.append("   and i.invest_status in('已到期', '提前结清','流标', '已转让') ");
				sql.append(" order by l.invest_end_date desc nulls LAST, i.effect_date desc nulls LAST, decode(i.invest_status, '已到期', 1, '已转让', 2, '流标', 3, '提前结清', 4, 5)");
			} else {
				sql.append("   and i.invest_status = ? ");
				listObject.add(params.get("disperseStatus"));
				if("投资中".equals((String) params.get("disperseStatus"))) {
					sql.append(" order by i.create_date desc");
				}else if("收益中".equals((String) params.get("disperseStatus"))) {
					sql.append(" order by  i.effect_date desc, l.invest_end_date asc, i.create_date desc");
				}
			}
		}else {
			sql.append(" order by i.create_date desc ");
		}
		
		int start=CommonUtils.emptyToInt(params.get("start"));
		int length=CommonUtils.emptyToInt(params.get("length"));
		Page<Map<String, Object>> queryPage = repositoryUtil.queryForPageMap(sql.toString(), listObject.toArray(), start, length);
		result.put("iTotalDisplayRecords", queryPage.getTotalElements());
		result.put("data", queryPage.getContent());
		return result;
	}

	@Override
	public Map<String, Object> queryMyDisperseDetail(Map<String, Object> params) throws SLException {
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
		.append(" select  nvl(cai.total_amount, 0) \"award\" , ")
				.append(" case when l.loan_status in ('流标', '已到期', '提前结清') then 0 ")/* 债权转让改造上线日2017-06-01(具体时间这天左右) */
		.append("             when a.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') ")
		.append("             THEN trunc(decode(i.invest_status, '投资中', i.invest_amount/l.loan_amount, h.hold_scale)*(select nvl(sum(r.repayment_interest), 0) from bao_t_repayment_plan_info r where r.loan_id = l.id and r.repayment_status = '未还款') , 2) ")
		.append("             else ")
		.append("             /* 剩余持比的收益 */ ")
		.append("             CASE WHEN i.INVEST_MODE = '转让' THEN  ")
		.append("                  CASE WHEN to_char(?, 'yyyyMMdd') /* ba.非还款日当天购买(下个还款日-转让时间)/(下个还款日-上个还款日）*持有比例*当期本息 */ ")
		.append("                            < (SELECT min(r.EXPECT_REPAYMENT_DATE) /* 购买时间当期的还款日时间 */ ")
		.append("                                 FROM bao_t_repayment_plan_info r  ")
		.append("                                WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date ")
		.append("                                  AND r.loan_id = l.id) ")
		.append("                       THEN trunc(h.hold_scale * (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                     from bao_t_repayment_plan_info r  ")
		.append("                                    where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                      and r.loan_id = l.id )  ")
		.append("                                * (d.NEXT_EXPIRY - to_date(i.effect_date, 'yyyyMMdd')) ")
		.append("                                / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE))) ")
		.append("                            , 2) ")
		.append("                          + trunc(h.hold_scale *  ")
		.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r  ")
		.append("                                   where r.EXPECT_REPAYMENT_DATE > to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                     and r.loan_id = l.id ) ")
		.append("                            , 2) ")
		.append("                       WHEN to_char(?, 'yyyyMMdd') /* bb.还款日当天购买(未还款的情况下)1/(下个还款日-上个还款日)*持有比例*当期本息  */ ")
		.append("                            >= (SELECT min(r.EXPECT_REPAYMENT_DATE) /* 购买时间当期的还款日时间 */ ")
		.append("                                FROM bao_t_repayment_plan_info r  ")
		.append("                               WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date ")
		.append("                                 AND r.loan_id = l.id) ")
		.append("                                  AND '未还款' = (SELECT r.REPAYMENT_STATUS ")
		.append("                                                  FROM bao_t_repayment_plan_info r  ")
		.append("                                                 WHERE r.EXPECT_REPAYMENT_DATE = (SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date AND r.loan_id = l.id) ")
		.append("                                                   AND r.loan_id = l.id) ")
		.append("                       THEN trunc(h.hold_scale * (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                     from bao_t_repayment_plan_info r  ")
		.append("                                    where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                      and r.loan_id = l.id )  ")
		.append("                                  * (d.NEXT_EXPIRY - to_date(i.effect_date, 'yyyyMMdd') + case when to_date(i.effect_date, 'yyyyMMdd') = d.NEXT_EXPIRY then 1 else 0 end ) ")
		.append("                                  / (d.NEXT_EXPIRY - nvl(d.Last_expiry,trunc(l.INVEST_START_DATE))) ")
		.append("                            , 2) ")
		.append("                          + trunc(h.hold_scale *  ")
		.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r  ")
		.append("                                   where r.EXPECT_REPAYMENT_DATE > to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                     and r.loan_id = l.id ) ")
		.append("                            , 2) ")
		.append("                       ELSE trunc(h.hold_scale *  ")
		.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r  ")
		.append("                                   where r.repayment_status = '未还款' ")
		.append("                                     and r.loan_id = l.id)  ")
		.append("                            , 2) END ")
		.append("             ELSE  ")
		.append("                       (select nvl(sum(TRUNC_AMOUNT_WEB(h.hold_scale * r.repayment_interest)), 0) ")
		.append("                          from bao_t_repayment_plan_info r  ")
		.append("                         where r.repayment_status = '未还款' ")
		.append("                           and r.loan_id = l.id)  ")
//		.append(" 		+ trunc(case when l.newer_flag = '新手标' ")
//		.append("            then ")
//		.append("                 case when l.loan_unit = '天'  ")
//		.append("                      then i.invest_amount * l.loan_term/360 * l.award_rate  ")
//		.append("                      else (select sum((cc.repayment_principal + cc.remainder_principal)*h.hold_scale* ")
//		.append("                       (case when l.repayment_method = '到期还本付息' then l.loan_term when l.repayment_method = '等额本息'then 1 when l.repayment_method = '每期还息到期付本' then 1 else 0 end)    ")
//		.append("                       * l.award_rate /12 )from bao_t_repayment_plan_info cc where cc.loan_id = l.id and cc.repayment_status = '未还款' )  ")
//		.append("                      end  ")
//		.append("            else 0 END ,2) ")
		.append(" +(SELECT  trunc(nvl(sum(case when l.repayment_method = '等额本息'  ")
		.append("              then cc.REPAYMENT_INTEREST * nvl(l.award_rate,0)/d.year_irr * h.hold_scale         ")
		.append("              when l.repayment_method = '每期还息到期付本'         ")
		.append("              then  (cc.repayment_principal + cc.remainder_principal)*l.award_rate *30/360*h.hold_scale         ")
		.append("              else  (cc.repayment_principal + cc.remainder_principal)*nvl(l.award_rate,0) *decode(l.loan_unit,'天',1,'月',30)*l.loan_term/360*h.hold_scale                        ")
		.append("              end ) ,0),2) from  bao_t_repayment_plan_info cc where cc.loan_id = l.id and cc.repayment_status = '未还款' ) ")
		.append("           END+ /* 转让人当月转出持比的收益 */ ")
		.append("             /* i.非还款日[转让时间！=下个还款日](转让时间-上个还款日)/(下个还款日-上个还款日)*转让比例*当期本息 */ ")
		.append("             CASE WHEN TRUNC(?) < d.NEXT_EXPIRY /* (c1)条件包括上个还款日当天已还款之后购买的的转让 */ ")
		.append("                  THEN TRUNC(nvl((SELECT sum(lt.TRADE_SCALE /* 转让比例 */ ")
		.append("                                          * (select nvl(sum(r.repayment_interest), 0)  ")
		.append("                                               from bao_t_repayment_plan_info r  ")
		.append("                                              where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                                and r.loan_id = l.id ) ")
		.append("                                          * (to_date(invest.effect_date, 'yyyyMMdd') - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)))/* 还款日当天已还款之后的数据，下期没有收益 */ ")
		.append("                                          / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)))) ")
		.append("                                    FROM BAO_T_LOAN_TRANSFER lt, bao_t_wealth_hold_info wh, bao_t_invest_info invest ")
		.append("                                   WHERE invest.id = wh.invest_id ")
		.append("                                     AND wh.id = lt.receive_hold_id ")
		.append("                                     AND to_date(invest.effect_date, 'yyyyMMdd') < ? ")
		.append("                                     AND to_date(invest.effect_date, 'yyyyMMdd') >= nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)) /* 配合(c1) */ ")
		.append("                                     AND lt.SENDER_HOLD_ID = h.id) ")
		.append("                       , 0), 2) ")
		.append("                  /* ii.还款日(未还款的情况下转让时间<还款计划表实际还款时间) (转让时间-上个还款日-1)/(下个还款日-上个还款日)*转让比例*当期本息  */ ")
		.append("                  WHEN TRUNC(?) >= d.NEXT_EXPIRY /* (c2)当期未还款,如果已还款d.NEXT_EXPIRY变为下期还款日期在上面的when条件中 */ ")
		.append("                  THEN TRUNC(nvl((SELECT sum(lt.TRADE_SCALE /* 转让比例 */ ")
		.append("                                          * (select nvl(r.repayment_interest, 0) /* 当期本息 */ ")
		.append("                                               from bao_t_repayment_plan_info r  ")
		.append("                                              where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                                and r.loan_id = l.id ) ")
		.append("                                          * (to_date(invest.effect_date, 'yyyyMMdd') - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)) - case when to_date(invest.effect_date, 'yyyyMMdd')=d.next_expiry then 1 else 0 end) ")
		.append("                                          / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)))) ")
		.append("                                    FROM BAO_T_LOAN_TRANSFER lt, bao_t_wealth_hold_info wh, bao_t_invest_info invest ")
		.append("                                   WHERE invest.id = wh.invest_id ")
		.append("                                     AND wh.id = lt.receive_hold_id ")
		.append("                                     AND to_date(invest.effect_date, 'yyyyMMdd') <= d.NEXT_EXPIRY ")
		.append("                                     AND to_date(invest.effect_date, 'yyyyMMdd') >= nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)) ")
		.append("                                     AND lt.SENDER_HOLD_ID = h.id) ")
		.append("                       , 0), 2) ")
		.append("                  ELSE 0 END")
		.append("           END \"waitingIncome\" ")
		.append("                , l.id                  \"disperseId\",    ")
		.append("                l.loan_code           \"loanNo\",    ")
		.append("                i.invest_amount       \"investAmount\",    ")
		.append("                to_char(i.create_date, 'yyyy-MM-dd HH24:mi:ss')         \"investDate\",    ")
//		.append("                CASE WHEN l.invest_end_date IS NULL ") 
//		.append("                     THEN l.LOAN_TERM ")
//		.append("                     ELSE ceil(months_between(l.invest_end_date, to_date(i.effect_date,'yyyy-MM-dd'))) ")
//		.append("                END \"investTerm\",    ")
		.append("                l.LOAN_TERM \"investTerm\",    ")
		.append("                l.LOAN_UNIT           \"typeUnit\",    ")
//		.append("                (case when l.loan_status in ('流标', '已到期', '提前结清') then 0 else  ")
//		.append("                 trunc(nvl((select sum(r.repayment_interest) from bao_t_repayment_plan_info r where r.loan_id = l.id and r.repayment_status = '未还款'), 0)* nvl(h.hold_scale,0), 2) end) \"waitingIncome\",    ")
		.append("                i.effect_date   \"interestDate\",    ")
		.append("                d.next_expiry         \"nextPaymentDay\",    ")
		.append("                d.year_irr            \"yearRate\",    ")
		.append("                nvl(l.award_rate ,0)  \"awardRate\",    ")
		.append("                (select trunc(nvl(sum(rd.trade_amount), 0), 2) from bao_t_account_flow_info f, bao_t_payment_record_info pr, bao_t_payment_record_detail rd where f.relate_primary = i.id and f.id = pr.relate_primary and pr.id = rd.pay_record_id and rd.subject_type in ('还风险金逾期费用', '利息', '逾期费用', '违约金','优选项目奖励收益') )   \"getIncome\",    ")
		.append("                l.invest_end_date     \"expireDate\",    ")
		.append("                l.repayment_method    \"repaymentMethod\",    ")
		.append("                TRUNC_AMOUNT_WEB(nvl(h.hold_scale,0)*d.credit_remainder_principal) \"remainPrincipal\",    ")
		.append("                trunc(p.already_invest_scale * 100, 0)         \"investScale\",    ")
		.append("                decode(i.invest_status ,'投资中','出借中',i.invest_status)      \"investStatus\",    ")
		.append("                l.loan_type           \"disperseType\",    ")
		.append("                l.loan_desc      \"loanUse\",   ")
		.append("                l.loan_amount    \"loanAmount\",   ")
		.append("                l.LOAN_TITLE     \"loanTitle\",  ")
		.append("                decode(i.invest_mode, '加入', l.PROTOCAL_TYPE, a.protocol_type) \"protocolType\",  ")
		.append("                i.transfer_apply_id \"transferApplyId\", ")
		.append("                a.transfer_no \"transferNo\" ")
		.append("                , l.newer_flag \"newerFlag\"  ")
		//.append("                , CASE WHEN (SELECT instr(value, l.LOAN_TYPE) FROM COM_T_PARAM WHERE TYPE='shanLinCaiFuSet' AND PARAMETER_NAME='可投产品') > 0 then '否' else '否' end \"reseverFlag\" ")
		.append("                , '否' \"reseverFlag\" ")
		.append("         from bao_t_invest_info i ")
		.append("          left join bao_t_wealth_hold_info h on h.invest_id = i.id ")
		.append("         inner join bao_t_loan_info l on l.id = i.loan_id ")
		.append("         inner join bao_t_loan_detail_info d on d.loan_id = l.id    ")
		.append("         inner join bao_t_project_invest_info p on p.loan_id = l.id    ")
		.append("         left  join bao_t_loan_transfer_apply a on a.id = i.transfer_apply_id ")
				.append(" left join BAO_T_CUST_ACTIVITY_INFO cai on cai.loan_id = l.id and i.cust_id = cai.cust_id and cai.id = i.CUST_ACTIVITY_ID ")
		.append("        where i.id = ? ");
		
		listObject.add((String)params.get("onlineTime"));/* 债权转让改造上线日2017-06-01(具体时间这天左右) */
		Date date = new Date();
		listObject.add(date);
//		listObject.add(date);
		listObject.add(date);
		listObject.add(date);
		listObject.add(date);
		listObject.add(date);

		listObject.add(params.get("investId"));
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
		if (list.isEmpty()) {
			return  new HashMap<String, Object>();
		}
		Map<String, Object> result = list.get(0);
		final String awardSql = "  select nvl(award_amount, 0) \"awardAmount\" from BAO_T_PURCHASE_AWARD where loan_id = ? and award_status = ?   ";
		List<Map<String, Object>> awardList = repositoryUtil.queryForMap(awardSql.toString(), new Object[]{result.get("disperseId"), Constant.PURCHASE_AWARD_STATUS_NO});
		
		StringBuilder Sql = new StringBuilder()
		.append("  select TRUNC_AMOUNT_WEB(nvl(sum(award_amount),0)) \"awardAmount\"  from bao_t_purchase_award  where invest_id=? and award_status='未结清' ")
		.append("  union all ")
		.append("  select TRUNC_AMOUNT_WEB(nvl(sum(award_amount),0)) \"awardAmount\"  from bao_t_purchase_award  where invest_id=? and award_status='已结清' ");
		List<Map<String, Object>> list1 = repositoryUtil.queryForMap(Sql.toString(), new Object[]{params.get("investId"),params.get("investId")});
		if (awardList.isEmpty()) {
			result.put("awardAmount", 0);
		} else {
			result.put("awardAmount", awardList.get(0).get("awardAmount"));
		}
		if (!list1.isEmpty()) {
			result.put("waitingIncome", new BigDecimal(result.get("waitingIncome").toString()).add(new BigDecimal(list1.get(0).get("awardAmount").toString())));
			result.put("getIncome", new BigDecimal(result.get("getIncome").toString()).add(new BigDecimal(list1.get(1).get("awardAmount").toString())));
		}
		return result;
	}

	@Override
	public Map<String, Object> queryMyDispersePaybackPlan(Map<String, Object> params) throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
				.append("  select nvl(pa.AWARD_AMOUNT, 0) \"awardAmount\" , ")
				.append(" to_CHAR(to_date(r.expect_repayment_date,'yyyyMMdd') ,'yyyy-MM-dd')    \"paybackDate\", ")
//				.append("        trunc(r.repayment_total_amount*h.hold_scale, 2)   \"paybackTotal\", ")
				.append("        TRUNC_AMOUNT_WEB(r.repayment_principal*h.hold_scale) + TRUNC_AMOUNT_WEB(r.repayment_interest*h.hold_scale) \"paybackTotal\", ")
				.append("        TRUNC_AMOUNT_WEB(r.repayment_principal*h.hold_scale)      \"paybackPrincipal\", ")
				.append("        TRUNC_AMOUNT_WEB(r.repayment_interest*h.hold_scale)+TRUNC_AMOUNT_WEB(nvl(pa.AWARD_AMOUNT, 0))       \"paybackInterest\", ")
				.append("        0                          \"punishAmount\", ")
				.append("        0                          \"makeupAmount\", ")
				.append("        r.repayment_status         \"paybackStatus\" ")
				.append("  from bao_t_repayment_plan_info r  ")
				.append("   inner join bao_t_loan_info l on l.id = r.loan_id  ")
				.append("   inner join bao_t_invest_info i on i.loan_id = l.id  ")
				.append("   inner join bao_t_wealth_hold_info h on h.INVEST_ID = i.id ")
				.append(" left join BAO_T_PURCHASE_AWARD pa on pa.PAYMENT_PLAN_ID = r.id ")
				.append("  where (r.expect_repayment_date > i.effect_date or (r.expect_repayment_date = i.effect_date and i.create_date < r.last_update_date)) and l.id = ? ")
				.append("  and i.id = ? ");
		// 注意：取投资起息日期之后的，或者投资日期等于还款日期时取投资时间是还款之前的
		listObject.add(params.get("disperseId"));
		listObject.add(params.get("investId"));
		sql.append(" order by r.current_term asc  ");
		int start=CommonUtils.emptyToInt(params.get("start"));
		int length=CommonUtils.emptyToInt(params.get("length"));
		Page<Map<String, Object>> queryPage = repositoryUtil.queryForPageMap(sql.toString(), listObject.toArray(), start, length);
		result.put("iTotalDisplayRecords", queryPage.getTotalElements());
		result.put("data", queryPage.getContent());
		return result;
	}

	@Override
	public int updateLoanGrantStatus(Map<String, Object> params)
			throws SLException {
		if(Constant.GRANT_STATUS_04.equals((String)params.get("grantStatus"))) {
			StringBuilder sql = new StringBuilder()
			.append(" UPDATE BAO_T_LOAN_INFO T  ")
			.append("   SET T.GRANT_STATUS = ?,  ")
			.append("       T.GRANT_USER = ?,  ")
			//.append("       T.VERSION = T.VERSION + 1,  ")
			.append("       T.LAST_UPDATE_USER = ?, ")
			.append("       T.LAST_UPDATE_DATE = ? ")
			.append("   WHERE T.ID=? ")
			.append("   AND (T.GRANT_STATUS = '待放款'  ")
			.append("   OR   T.GRANT_STATUS = '放款失败') ");
			
			return jdbcTemplate.update(sql.toString(), 
					new Object[]{
						(String)params.get("grantStatus"),
						(String)params.get("lastUpdateUser"),
						(String)params.get("lastUpdateUser"),
						(Date)params.get("lastUpdateDate"),
						(String)params.get("loanId")});	
		}
		else {
			StringBuilder sql = new StringBuilder()
			.append(" UPDATE BAO_T_LOAN_INFO T  ")
			.append("   SET T.GRANT_STATUS = ?,  ")
			.append("       T.GRANT_USER = ?,  ")
			//.append("       T.VERSION = T.VERSION + 1,  ")
			.append("       T.LAST_UPDATE_USER = ?, ")
			.append("       T.LAST_UPDATE_DATE = ? ")
			.append("   WHERE T.ID=? ")
			.append("   AND T.GRANT_STATUS = '放款中'  ");
			
			return jdbcTemplate.update(sql.toString(), 
					new Object[]{
						(String)params.get("grantStatus"),
						(String)params.get("lastUpdateUser"),
						(String)params.get("lastUpdateUser"),
						(Date)params.get("lastUpdateDate"),
						(String)params.get("loanId")});	
		}
	}

	@Override
	public Map<String, Object> queryMyCreditList(Map<String, Object> params) throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		SqlCondition sqlCondition = queryMyCreditListSql(params);
		
		sqlCondition.append("order by decode(\"auditStatus\", '未通过', 1, '审核中', 2, '未提交', 3, '通过', 4, 99)")
		.append(" , decode(\"auditStatus\" ,'未通过' ,apply.last_update_date, '审核中',apply.create_date, '未提交',i.create_date ) ");
	
		int start=CommonUtils.emptyToInt(params.get("start"));
		int length=CommonUtils.emptyToInt(params.get("length"));
		Page<Map<String, Object>> queryPage = repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), start, length);
		result.put("iTotalDisplayRecords", queryPage.getTotalElements());
		result.put("data", queryPage.getContent());
		return result;
	}
	
	private SqlCondition queryMyCreditListSql(Map<String, Object> params){
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sqlString = new StringBuilder()
		.append("    select h.id \"holdId\",  ")
				.append(" i.INVEST_RED_PACKET \"investRedPacket\", ")
				.append(" i.RED_PACKET_TYPE \"redPacketType\" ,")
				.append(" i.CUST_ACTIVITY_ID \"custActivityId\", ")
		.append("               h.invest_id \"investId\",  ")
		.append("               h.loan_id \"disperseId\",  ")
		.append("               l.loan_title \"loanTitle\",  ")
		.append("               l.loan_code \"loanCode\",  ")
		.append("               d.year_irr \"yearRate\",  ")
		.append("               h.hold_amount \"investAmount\",  ")
//		.append("               h.HOLD_SCALE*trunc((select nvl(sum(r.repayment_principal + r.repayment_interest), 0) from bao_t_repayment_plan_info r where r.loan_id = l.id and r.repayment_status = '未还款'), 2) \"exceptRepayAmount\",  ")
		.append("       case when investSrc.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') ")/* 债权转让改造上线日2017-06-01(具体时间这天左右) */
		.append("             THEN trunc(decode(i.invest_status, '投资中', i.invest_amount/l.loan_amount, h.hold_scale)*(select nvl(sum(r.repayment_principal + r.repayment_interest), 0) from bao_t_repayment_plan_info r where r.loan_id = l.id and r.repayment_status = '未还款') , 2) ")
		.append("             else TRUNC_AMOUNT_WEB(h.HOLD_SCALE * d.CREDIT_REMAINDER_PRINCIPAL) + ")// 持有本金
		.append("             /* 剩余持比的收益 */ ")
		.append("             CASE WHEN i.INVEST_MODE = '转让' THEN  ")
		.append("                  CASE WHEN to_char(?, 'yyyyMMdd') /* ba.非还款日当天购买(下个还款日-转让时间)/(下个还款日-上个还款日）*持有比例*当期本息 */ ")
		.append("                            < (SELECT min(r.EXPECT_REPAYMENT_DATE) /* 购买时间当期的还款日时间 */ ")
		.append("                                 FROM bao_t_repayment_plan_info r  ")
		.append("                                WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date ")
		.append("                                  AND r.loan_id = l.id) ")
		.append("                       THEN trunc(h.hold_scale * (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                     from bao_t_repayment_plan_info r  ")
		.append("                                    where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                      and r.loan_id = l.id )  ")
		.append("                                * (d.NEXT_EXPIRY - TRUNC(?)) ")
		.append("                                / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE))) ")
		.append("                            , 2) ")
		.append("                          + trunc(h.hold_scale *  ")
		.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r  ")
		.append("                                   where r.EXPECT_REPAYMENT_DATE > to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                     and r.loan_id = l.id ) ")
		.append("                            , 2) ")
		.append("                       WHEN to_char(?, 'yyyyMMdd') /* bb.还款日当天购买(未还款的情况下)1/(下个还款日-上个还款日)*持有比例*当期本息  */ ")
		.append("                            = (SELECT min(r.EXPECT_REPAYMENT_DATE) /* 购买时间当期的还款日时间 */ ")
		.append("                                FROM bao_t_repayment_plan_info r  ")
		.append("                               WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date ")
		.append("                                 AND r.loan_id = l.id) ")
		.append("                        AND '未还款' = (SELECT r.REPAYMENT_STATUS ")
		.append("                                        FROM bao_t_repayment_plan_info r  ")
		.append("                                       WHERE r.EXPECT_REPAYMENT_DATE = (SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date AND r.loan_id = l.id) ")
		.append("                                         AND r.loan_id = l.id) ")
		.append("                       THEN trunc(h.hold_scale * (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                     from bao_t_repayment_plan_info r  ")
		.append("                                    where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                      and r.loan_id = l.id )  ")
		.append("                                  * 1 ")
		.append("                                  / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE))) ")
		.append("                            , 2) ")
		.append("                          + trunc(h.hold_scale *  ")
		.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r  ")
		.append("                                   where r.EXPECT_REPAYMENT_DATE > to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                     and r.loan_id = l.id ) ")
		.append("                            , 2) ")
		.append("                       ELSE trunc(h.hold_scale *  ")
		.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r  ")
		.append("                                   where r.repayment_status = '未还款' ")
		.append("                                     and r.loan_id = l.id)  ")
		.append("                            , 2) END ")
		.append("             ELSE    ")
		.append("                       (select nvl(sum(TRUNC_AMOUNT_WEB(h.hold_scale *r.repayment_interest)), 0) ")
		.append("                          from bao_t_repayment_plan_info r  ")
		.append("                         where r.repayment_status = '未还款' ")
		.append("                           and r.loan_id = l.id)  ")
		.append("             END ")
//		.append("           + /* 转让人当月转出持比的收益 */ ") // 转出的不在这显示
//		.append("             /* i.非还款日[转让时间！=下个还款日](转让时间-上个还款日)/(下个还款日-上个还款日)*转让比例*当期本息 */ ")
//		.append("             CASE WHEN TRUNC(?) < d.NEXT_EXPIRY /* (c1)条件包括上个还款日当天已还款之后购买的的转让 */ ")
//		.append("                  THEN TRUNC(nvl((SELECT sum(lt.TRADE_SCALE /* 转让比例 */ ")
//		.append("                                          * (select nvl(sum(r.repayment_interest), 0)  ")
//		.append("                                               from bao_t_repayment_plan_info r  ")
//		.append("                                              where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
//		.append("                                                and r.loan_id = l.id ) ")
//		.append("                                          * (TRUNC(lt.CREATE_DATE) - decode(d.Last_expiry, null, trunc(l.INVEST_START_DATE), d.Last_expiry))/* 还款日当天已还款之后的数据，下期没有收益 */ ")
//		.append("                                          / (d.NEXT_EXPIRY - decode(d.Last_expiry, null, trunc(l.INVEST_START_DATE), d.Last_expiry))) ")
//		.append("                                    FROM BAO_T_LOAN_TRANSFER lt  ")
//		.append("                                   WHERE lt.CREATE_DATE < ? ")
//		.append("                                     AND lt.CREATE_DATE >= d.Last_expiry /* 配合(c1) */ ")
//		.append("                                     AND lt.SENDER_HOLD_ID = h.id) ")
//		.append("                       , 0), 2) ")
//		.append("                  /* ii.还款日(未还款的情况下转让时间<还款计划表实际还款时间) (转让时间-上个还款日-1)/(下个还款日-上个还款日)*转让比例*当期本息  */ ")
//		.append("                  WHEN TRUNC(?) = d.NEXT_EXPIRY /* (c2)当期未还款,如果已还款d.NEXT_EXPIRY变为下期还款日期在上面的when条件中 */ ")
//		.append("                  THEN TRUNC(nvl((SELECT sum(lt.TRADE_SCALE /* 转让比例 */ ")
//		.append("                                          * (select nvl(sum(r.repayment_interest), 0) /* 当期本息 */ ")
//		.append("                                               from bao_t_repayment_plan_info r  ")
//		.append("                                              where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
//		.append("                                                and r.loan_id = l.id ) ")
//		.append("                                          * (TRUNC(lt.CREATE_DATE) - decode(d.Last_expiry, null, trunc(l.INVEST_START_DATE), d.Last_expiry) - 1) ")
//		.append("                                          / (d.NEXT_EXPIRY - decode(d.Last_expiry, null, trunc(l.INVEST_START_DATE), d.Last_expiry))) ")
//		.append("                                    FROM BAO_T_LOAN_TRANSFER lt  ")
//		.append("                                   WHERE lt.CREATE_DATE = d.NEXT_EXPIRY ")
//		.append("                                     AND lt.SENDER_HOLD_ID = h.id) ")
//		.append("                       , 0), 2) ")
//		.append("                  ELSE 0 END")
		.append("           END \"exceptRepayAmount\" ")
		.append("               , ceil(months_between(l.invest_end_date, ?)) || '个月' \"remainTerm\",  ")
		.append("               decode(i.invest_status,'投资中','出借中',i.invest_status) \"investStatus\",  ")
		.append("               d.next_expiry \"nextPaymentDay\",     ")
		.append("               i.invest_mode \"investMode\" ")
		.append("               , i.TRANSFER_APPLY_ID \"transferApplyId\" ")
		.append("          , case when apply.id is not null ")
		.append("                 then case when apply.audit_status='待审核' then '审核中' when apply.audit_status='拒绝' then '未通过' ")
		.append("                      when apply.audit_status = '通过' then '未提交'  ")
		.append("                      else apply.audit_status end ")
		.append("                 else '未提交' ")
		.append("                 end \"auditStatus\" ")
		.append("       , case when b.invest_source = 'auto' then '自动投标' when b.invest_source = 'reserveinvest' then '优先投' else '手动投标' end  \"investSource\" ")
		.append("        from bao_t_wealth_hold_info h  ")
		.append("        inner join bao_t_loan_info l on l.id = h.loan_id  ")
		.append("        inner join bao_t_loan_detail_info d on d.loan_id = l.id  ")
		.append("        inner join bao_t_invest_info i on i.id = h.invest_id and i.loan_id = h.loan_id ")
		.append("        inner join bao_t_invest_detail_info b on b.invest_id = i.id ")
		.append("               left join (select a.* from bao_t_loan_transfer_apply a  ")
		.append("                                       , (select a.sender_hold_id, max(a.create_date) create_date  ")
		.append("                                            from bao_t_loan_transfer_apply a  ")
		.append("                                           group by a.sender_hold_id) ga ")
		.append("                                    where a.sender_hold_id = ga.sender_hold_id ")
		.append("                                      and a.create_date = ga.create_date ")
		.append("                        ) apply on apply.sender_hold_id = h.id ") // 如果之前提交过转让，取最新数据的状态
		.append("               left join bao_t_loan_transfer_apply investSrc on investSrc.id = i.TRANSFER_APPLY_ID ")// 如果投资来源于转让标，看转让标能否可以继续转让
		.append("        where i.invest_status in ('收益中')  ")
		.append("        and i.effect_date <= to_char(? - ?, 'yyyyMMdd')  ")  // 持有债权时间大于30天
		.append("        and l.invest_end_date > ? + ?  ") // 距离债权到期日大于30天
		.append("        and not exists ( ") // 排除已经在转让中的债权
		.append("            select 1 ")
		.append("            from bao_t_loan_transfer_apply a ")
		.append("            where a.sender_hold_id = h.id ")
		.append("            and a.cancel_status = '未撤销' ")
		.append("            AND a.APPLY_STATUS != '转让成功' ")
		.append("            AND a.AUDIT_STATUS = '通过' ")
		.append("        ) ")
//		.append("        AND h.HOLD_SCALE * (SELECT decode(nvl(rp.repayment_status, '未还款'),'未还款' ")
//		.append("                                         , cr.value_repayment_before ")
//		.append("                                         , cr.value_repayment_after) ")
//		.append("                              FROM bao_t_credit_right_value cr ")
//		.append("                              left JOIN bao_t_repayment_plan_info rp  ")
//		.append("                                     ON rp.loan_id = cr.loan_id  ")
//		.append("                                    and expect_repayment_date = to_char(?, 'yyyyMMdd') ")
//		.append("                             WHERE cr.loan_id = h.loan_id ")
//		.append("                               AND cr.value_date = to_char(?, 'yyyyMMdd') ")
//		.append("            ) >= 1000 ")
		// update by http://192.16.150.101:8080/browse/SLCF-2823 at 2017-05-18
//		.append("        AND h.HOLD_SCALE * d.CREDIT_REMAINDER_PRINCIPAL >= 100 ") // 债权转让调整本金>=1000转让 //update 2017-07-01
//		.append("        AND l.SEAT_TERM != -1 ")
//		.append("        AND (investSrc.transfer_seat_term is null /* 1.没数据 2.没设值 */ or investSrc.transfer_seat_term != -1) ") // 如果投资来源于转让标，看转让标能否可以继续转让
		.append("        AND decode(i.invest_mode,'加入',l.SEAT_TERM,investSrc.transfer_seat_term) != -1 ")
		.append("        AND h.cust_id = ? ")
		;

		listObject.add((String)params.get("onlineTime"));/* 债权转让改造上线日2017-06-01(具体时间这天左右) */
		Date now = new Date();
		listObject.add(now);
		listObject.add(now);
//		listObject.add(now);
//		listObject.add(now);
//		listObject.add(now);
		listObject.add(now);/* 债权转让改造上线日2017-06-01(具体时间这天左右) */
		
		Date date = DateUtils.truncateDate(new Date());
		listObject.add(date);		
		listObject.add(date);
		listObject.add(params.get("needHoldDay"));
		listObject.add(date);
		listObject.add(params.get("fromEndDay"));
//		listObject.add(date);
//		listObject.add(date);
		listObject.add(params.get("custId"));
		
		return new SqlCondition(sqlString, params, listObject);
	}

	@Override
	public Map<String, Object> queryMyCreditDetail(Map<String, Object> params) throws SLException {
		SqlCondition sqlCon = queryMyCreditDetailSql(params);
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlCon.toString(), sqlCon.toArray());
		return list.size() == 0 ? new HashMap<String, Object>() : list.get(0);
	}
	
	public List<Map<String, Object>> queryMyCreditDetailForBatch(Map<String, Object> params) throws SLException {
		SqlCondition sqlCon = queryMyCreditDetailSql(params);
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlCon.toString(), sqlCon.toArray());
		return list;
	}

	private SqlCondition queryMyCreditDetailSql(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT wh.ID \"holdId\" ")
//		.append("      , TRUNC(wh.HOLD_SCALE * decode(nvl(rp.repayment_status, '未还款'), ")
//		.append("                               '未还款', ")
//		.append("                               cr.value_repayment_before, ")
//		.append("                               cr.value_repayment_after), 2) \"nowLoanValue\" ") // 当前债权价值
//		.append("      , TRUNC(wh.HOLD_SCALE * (SELECT sum(REPAYMENT_INTEREST+REPAYMENT_PRINCIPAL)  ")
//		.append("                                 FROM bao_t_repayment_plan_info  ")
//		.append("                                WHERE LOAN_ID=loan.ID  ")
//		.append("                                  AND REPAYMENT_STATUS='未还款') ")
//		.append("             , 2) \"exceptRepayAmount\" ") // 待收本息
//		.append("      , TRUNC(wh.HOLD_SCALE*decode(nvl(rp.repayment_status, '未还款'), ")
//		.append("                               '未还款', ")
//		.append("                               cr.value_repayment_before, ")
//		.append("                               cr.value_repayment_after), 2) \"tradeAmount\" ")
//		.append("      , TRUNC(wh.HOLD_SCALE*decode(nvl(rp.repayment_status, '未还款'), ")
//		.append("                               '未还款', ")
//		.append("                               cr.value_repayment_before, ")
//		.append("                               cr.value_repayment_after) ")
//		.append("             * ? ")
//		.append("             , 2) \"transferExpense\" ")
//		.append("      , TRUNC(wh.HOLD_SCALE*decode(nvl(rp.repayment_status, '未还款'), ")
//		.append("                               '未还款', ")
//		.append("                               cr.value_repayment_before, ")
//		.append("                               cr.value_repayment_after), 2) ")
//		.append("      - TRUNC(wh.HOLD_SCALE*decode(nvl(rp.repayment_status, '未还款'), ")
//		.append("                               '未还款', ")
//		.append("                               cr.value_repayment_before, ")
//		.append("                               cr.value_repayment_after)*?, 2) \"exceptArriveAmount\"")
		.append("      , TRUNC_AMOUNT_WEB(wh.HOLD_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL) \"remainPrincipal\" ") // 当前债权 = 剩余本金
		.append("      , ceil(months_between(loan.invest_end_date, ?)) || '个月' \"remainTerm\" ") // 剩余期限
		.append("      , TRUNC(wh.HOLD_SCALE * (SELECT sum(REPAYMENT_INTEREST)  ")
		.append("                                 FROM bao_t_repayment_plan_info  ")
		.append("                                WHERE LOAN_ID=loan.ID  ")
		.append("                                  AND REPAYMENT_STATUS='未还款') ")
		.append("             , 2) \"exceptRepayInterest\" ") // 待收总利息
		.append("      , TRUNC(wh.HOLD_SCALE * (SELECT REPAYMENT_INTEREST  ")
		.append("                                 FROM bao_t_repayment_plan_info  ")
		.append("                                WHERE LOAN_ID = loan.ID  ")
		.append("                                  AND EXPECT_REPAYMENT_DATE = to_char(ld.NEXT_EXPIRY,'yyyyMMdd')) ")
		.append("             , 2) \"currentInterest\" ") // 当期收益
		.append("      , 1 \"tradeScale\" ")
		.append("      , 1 \"reducedScale\" ")
		.append("      , (trunc(?) - decode(ld.Last_expiry,null,trunc(loan.INVEST_START_DATE),ld.Last_expiry) - case when trunc(?) >= ld.NEXT_EXPIRY then 1 else 0 end) /* 还款日当天未还款前转让（还款可能出现延期一两天），让一天利息 */ \"holdDays\" ")// 持有天数
		.append("      , (ld.NEXT_EXPIRY - decode(ld.Last_expiry,null,trunc(loan.INVEST_START_DATE),ld.Last_expiry)) \"daysOfMonth\" ")// 当月天数
		.append("      , TRUNC_AMOUNT_WEB(wh.HOLD_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL) \"transAmount\" ")   // 转让金额=剩余本金*转让比例
		.append("      , TRUNC_AMOUNT_WEB(wh.HOLD_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL) \"reducedAmount\" ") // 折价金额=转让金额*折价比例
		.append("      , TRUNC_AMOUNT_WEB(wh.HOLD_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL * ?) \"transferExpense\" ") // 转让费用
		.append("      , TRUNC_AMOUNT_WEB(wh.HOLD_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL) ") 
		.append("      - TRUNC_AMOUNT_WEB(wh.HOLD_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL * ?) \"exceptArrivePrincipal\" ")// 预计到账本金=折价金额 - 转让费用
		.append("      , TRUNC(wh.HOLD_SCALE * (SELECT REPAYMENT_INTEREST  ")
		.append("                                 FROM bao_t_repayment_plan_info  ")
		.append("                                WHERE LOAN_ID = loan.ID  ")
		.append("                                  AND EXPECT_REPAYMENT_DATE = to_char(ld.NEXT_EXPIRY,'yyyyMMdd')) ")
		.append("             * (trunc(?) - decode(ld.Last_expiry,null,trunc(loan.INVEST_START_DATE),ld.Last_expiry) - case when trunc(?) >= ld.NEXT_EXPIRY then 1 else 0 end) /* 还款日当天未还款前转让（还款可能出现延期一两天），让一天利息 */ ")
		.append("             / (ld.NEXT_EXPIRY - decode(ld.Last_expiry,null,trunc(loan.INVEST_START_DATE),ld.Last_expiry)) ")
		.append("             , 2) \"exceptArriveInterest\" ") // 预计到账利息=当期收益 / 当期天数 * 持有天数（不含当天）
		.append("      , TRUNC_AMOUNT_WEB(wh.HOLD_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL) ")
		.append("      - TRUNC_AMOUNT_WEB(wh.HOLD_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL * ?) ")// 预计到账本金
		.append("      + TRUNC(wh.HOLD_SCALE * (SELECT REPAYMENT_INTEREST  ") // 预计到账利息
		.append("                                 FROM bao_t_repayment_plan_info  ")
		.append("                                WHERE LOAN_ID = loan.ID  ")
		.append("                                  AND EXPECT_REPAYMENT_DATE = to_char(ld.NEXT_EXPIRY,'yyyyMMdd')) ")
		.append("             * (trunc(?) - decode(ld.Last_expiry,null,trunc(loan.INVEST_START_DATE),ld.Last_expiry) - case when trunc(?) >= ld.NEXT_EXPIRY then 1 else 0 end)  /* 还款日当天未还款前转让（还款可能出现延期一两天），让一天利息 */ ")
		.append("             / (ld.NEXT_EXPIRY - decode(ld.Last_expiry,null,trunc(loan.INVEST_START_DATE),ld.Last_expiry)) ")
		.append("             , 2) \"exceptArriveAmount\" ") // 预计到账金额 =  预计到账本金  +  预计到账利息
		.append("      , case when loan.invest_end_date - (?+?) <= ?  ") // 不能超过截止日期
		.append("             then to_date(to_char((loan.invest_end_date - ?), 'yyyy-MM-dd hh24')||':00:00', 'yyyy-MM-dd hh24:mi:ss') ")
		.append("             else to_date(to_char((?+?), 'yyyy-MM-dd hh24')||':00:00', 'yyyy-MM-dd hh24:mi:ss') end \"tradeEndDate\" ")  
		.append("      , ? \"transferRate\" ")
		.append("      , ? \"transferDay\" ")
		.append("      , loan.loan_title \"loanTitle\" ")
		.append("      , ? \"protocolType\" ")
		.append("   FROM BAO_T_WEALTH_HOLD_INFO wh ")
		.append("  INNER JOIN BAO_T_LOAN_INFO loan ")
		.append("     ON loan.ID = wh.LOAN_ID ")
		.append("  INNER JOIN BAO_T_LOAN_DETAIL_INFO ld ")
		.append("     ON ld.LOAN_ID = loan.ID ")
//		.append("  INNER JOIN bao_t_credit_right_value cr ")
//		.append("     ON cr.loan_id = wh.loan_id ")
//		.append("    AND cr.value_date = to_char(?, 'yyyyMMdd') ")
		.append("   LEFT JOIN bao_t_repayment_plan_info rp ON rp.loan_id = wh.loan_id and expect_repayment_date = to_char(?, 'yyyyMMdd') ")
		.append("  WHERE 1=1 ")
//		.append("    AND wh.ID = ? ") // update by 20170701
		;
		Date date = new Date();
		List<Object> listObject = new ArrayList<Object>();
		listObject.add(date);
		listObject.add(date);// 持有天数
		listObject.add(date);// 持有天数
		listObject.add(params.get("transferRate"));// 转让费用
		listObject.add(params.get("transferRate"));// 预计到账本金
		
		listObject.add(date);// 预计到账利息
		listObject.add(date);// 预计到账利息
		
		listObject.add(params.get("transferRate"));// 预计到账金额
		listObject.add(date);// 预计到账金额
		listObject.add(date);// 预计到账金额
		
		listObject.add(date); // 截止日期
		listObject.add(params.get("transferDay"));// 截止日期
		listObject.add(params.get("fromEndDay"));// 截止日期
		listObject.add(params.get("fromEndDay"));// 截止日期
		listObject.add(date);// 截止日期
		listObject.add(params.get("transferDay"));// 截止日期
		
		listObject.add(params.get("transferRate"));// 转让费率
		listObject.add(params.get("transferDay"));// 转让天数
		listObject.add(params.get("transferProtocalType"));// 协议
		// 条件的参数
//		listObject.add(date);// 债权价值
		listObject.add(date);
		
//		listObject.add(params.get("holdId")); // update by 20170701
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(sql.toString()), params, listObject)
		.addString("holdId", "wh.ID")
		.addList("holdIdList", "wh.ID");
		return sqlCon;
	}
	
	@Override
	public Map<String, Object> queryMyCreditPaybackPlan(Map<String, Object> params) throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
				.append("  select r.expect_repayment_date         \"paybackDate\", ")
				.append("         r.repayment_total_amount        \"paybackTotal\", ")
				.append("         r.repayment_principal           \"paybackPrincipal\", ")
				.append("         r.repayment_interest            \"paybackInterest\", ")
				.append("         0                               \"punishAmount\", ")
				.append("         r.penalty_amount                \"makeupAmount\", ")
				.append("         r.repayment_status              \"paybackStatus\" ")
				.append("  from bao_t_loan_info l ")
				.append("  inner join bao_t_loan_detail_info d on d.loan_id = l.id ")
				.append("  inner join bao_t_wealth_hold_info h on h.loan_id = l.id and h.hold_scale > 0 ")
				.append("  inner join bao_t_invest_info i on i.id = h.invest_id ")
				.append("  inner join bao_t_repayment_plan_info r on r.loan_id = l.id ")
				.append("  where l.id = ? ")
				.append("    and i.cust_id = ? ");
		sql.append("  order by r.current_term ");
		listObject.add(params.get("creditId"));
		listObject.add(params.get("custId"));
		int start=CommonUtils.emptyToInt(params.get("start"));
		int length=CommonUtils.emptyToInt(params.get("length"));
		Page<Map<String, Object>> queryPage = repositoryUtil.queryForPageMap(sql.toString(), listObject.toArray(), start, length);
		result.put("iTotalDisplayRecords", queryPage.getTotalElements());
		result.put("data", queryPage.getContent());
		return result;
	}

	@Override
	public Map<String, Object> queryRecentlyRepaymentList(Map<String, Object> params) throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
		.append("  select r.id                              \"repaymentPlanId\", ")
		.append("         l.loan_code                       \"loanCode\", ")
		.append("         r.repayment_total_amount          \"repaymentTotalAmount\", ")
		.append("         to_char(to_date(r.expect_repayment_date, 'yyyyMMdd'), 'yyyy-MM-dd')           \"expectRpaymentDate\", ")
		.append("         r.current_term || '/' || decode(l.repayment_method, '到期还本付息', 1, (l.loan_term/l.repayment_cycle))  \"currentTerm\", ")
		.append("         round(d.year_irr*100, 1)                        \"yearIrr\", ")
		.append("      	  nvl(trunc(round(l.award_rate,8)*100,2),0) \"awardRate\" , ")// 奖励利率
		.append("         l.repayment_method                \"repaymentMethod\", ")
		.append("         l.loan_amount                     \"loanAmount\", ")
		.append("         lc.cust_name                      \"custName\", ")
		.append("         l.loan_type                       \"loanType\" ")
		.append("          , l.id                       \"loanId\" ")
		.append("  from bao_t_loan_info l  ")
		.append("  inner join bao_t_loan_cust_info lc on lc.id = l.relate_primary ")
		.append("  inner join bao_t_loan_detail_info d on d.loan_id = l.id ")
		.append("  inner join bao_t_repayment_plan_info r on r.loan_id = l.id ")
		.append("  where r.repayment_status = '未还款'  ")
		.append("    and l.loan_status = '正常' ")
		;
		if(!StringUtils.isEmpty(params.get("loanCode"))) {
			sql.append("   and l.loan_code = ? ");
			listObject.add(params.get("loanCode"));
		}
		if(!StringUtils.isEmpty(params.get("custName"))) {
			sql.append("   and lc.cust_name = ? ");
			listObject.add(params.get("custName"));
		}
		if(!StringUtils.isEmpty(params.get("repaymentMethod"))) {
			sql.append("   and l.repayment_method = ? ");
			listObject.add(params.get("repaymentMethod"));
		}
		if(!StringUtils.isEmpty(params.get("loanType"))) {
			sql.append("   and l.loan_type = ? ");
			listObject.add(params.get("loanType"));
		}
		if(!StringUtils.isEmpty(params.get("expectRpaymentDateStart"))) {
			sql.append("  and r.expect_repayment_date >= ? ");
			listObject.add(params.get("expectRpaymentDateStart").toString().replaceAll("-", ""));
		}
		if(!StringUtils.isEmpty(params.get("expectRpaymentDateEnd"))) {
			sql.append("  and r.expect_repayment_date <= ? ");
			listObject.add(params.get("expectRpaymentDateEnd").toString().replaceAll("-", ""));
		}
		sql.append("  order by r.expect_repayment_date, l.loan_code ");
		int start=CommonUtils.emptyToInt(params.get("start"));
		int length=CommonUtils.emptyToInt(params.get("length"));
		Page<Map<String, Object>> queryPage = repositoryUtil.queryForPageMap(sql.toString(), listObject.toArray(), start, length);
		result.put("iTotalDisplayRecords", queryPage.getTotalElements());
		result.put("data", queryPage.getContent());
		return result;
	}

	@Override
	public Map<String, Object> queryRecentlyRepaymentCount(Map<String, Object> params) throws SLException {
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
		.append("  select count(r.id) \"totalCount\", trunc(nvl(sum(r.repayment_total_amount), 0), 2) \"totalAmount\" ")
		.append("  from bao_t_loan_info l  ")
		.append("  inner join bao_t_loan_cust_info lc on lc.id = l.relate_primary ")
		.append("  inner join bao_t_loan_detail_info d on d.loan_id = l.id ")
		.append("  inner join bao_t_repayment_plan_info r on r.loan_id = l.id ")
		.append("  where r.repayment_status = '未还款'  ")
		.append("    and l.loan_status = '正常' ")
		;
		if(!StringUtils.isEmpty(params.get("loanCode"))) {
			sql.append("   and l.loan_code = ? ");
			listObject.add(params.get("loanCode"));
		}
		if(!StringUtils.isEmpty(params.get("custName"))) {
			sql.append("   and lc.cust_name = ? ");
			listObject.add(params.get("custName"));
		}
		if(!StringUtils.isEmpty(params.get("repaymentMethod"))) {
			sql.append("   and l.repayment_method = ? ");
			listObject.add(params.get("repaymentMethod"));
		}
		if(!StringUtils.isEmpty(params.get("loanType"))) {
			sql.append("   and l.loan_type = ? ");
			listObject.add(params.get("loanType"));
		}
		if(!StringUtils.isEmpty(params.get("expectRpaymentDateStart"))) {
			sql.append("  and r.expect_repayment_date >= ? ");
			listObject.add(params.get("expectRpaymentDateStart").toString().replaceAll("-", ""));
		}
		if(!StringUtils.isEmpty(params.get("expectRpaymentDateEnd"))) {
			sql.append("  and r.expect_repayment_date <= ? ");
			listObject.add(params.get("expectRpaymentDateEnd").toString().replaceAll("-", ""));
		}
		sql.append("  order by r.expect_repayment_date, l.loan_code ");
		return repositoryUtil.queryForMap(sql.toString(), listObject.toArray()).get(0);
	}

	@Override
	public Map<String, Object> queryOverdueDataList(Map<String, Object> params) throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
				.append("   select r.id                              \"repaymentPlanId\",  ")
				.append("          l.loan_code                       \"loanCode\",  ")
				.append("          r.repayment_total_amount          \"repaymentTotalAmount\",  ")
				.append("          r.expect_repayment_date           \"expectRpaymentDate\",  ")
//				.append("          r.current_term||'/'||l.loan_term  \"currentTerm\"  ")
				.append("          r.current_term||'/'||(SELECT count(1) FROM bao_t_repayment_plan_info WHERE LOAN_ID=l.ID)  \"currentTerm\"  ")
				.append("          trunc(sysdate) - to_date(r.expect_repayment_date, 'yyyyMMdd')  \"overdueDays\" ")
				.append("          r.penalty_amount                  \"overdueExpens\", ")
				.append("          round(d.year_irr*100,1)                        \"yearIrr\",  ")
				.append("          l.repayment_method                \"repaymentMethod\",  ")
				.append("          l.loan_amount                     \"loanAmount\",  ")
				.append("          lc.cust_name                      \"custName\",  ")
				.append("          l.loan_type                       \"loanType\"  ")
				.append("          , l.id                       \"loanId\" ")
				.append("       from bao_t_loan_info l   ")
				.append("       inner join bao_t_loan_cust_info lc on lc.id = l.relate_primary  ")
				.append("       inner join bao_t_loan_detail_info d on d.loan_id = l.id  ")
				.append("       inner join bao_t_repayment_plan_info r on r.loan_id = l.id  ")
				.append("       where r.repayment_status = '未还款'  and r.expect_repayment_date <to_char(sysdate, 'yyyyMMdd') ")
				.append("         and l.loan_status is not null ")
				;
		if(!StringUtils.isEmpty(params.get("loanCode"))) {
			sql.append("   and l.loan_code = ? ");
			listObject.add(params.get("loanCode"));
		}
		if(!StringUtils.isEmpty(params.get("custName"))) {
			sql.append("   and lc.cust_name = ? ");
			listObject.add(params.get("custName"));
		}
		if(!StringUtils.isEmpty(params.get("repaymentMethod"))) {
			sql.append("   and l.repayment_method = ? ");
			listObject.add(params.get("repaymentMethod"));
		}
		if(!StringUtils.isEmpty(params.get("loanType"))) {
			sql.append("   and l.loan_type = ? ");
			listObject.add(params.get("loanType"));
		}
		if(!StringUtils.isEmpty(params.get("expectRpaymentDateStart"))) {
			sql.append("   and to_char(r.expect_repayment_date, 'yyyy-MM-dd') >= ? ");
			listObject.add(params.get("expectRpaymentDateStart"));
		}
		if(!StringUtils.isEmpty(params.get("expectRpaymentDateEnd"))) {
			sql.append("   and to_char(r.expect_repayment_date, 'yyyy-MM-dd') <= ? ");
			listObject.add(params.get("expectRpaymentDateEnd"));
		}
		sql.append("  order by r.expect_repayment_date, l.loan_code ");
		int start=CommonUtils.emptyToInt(params.get("start"));
		int length=CommonUtils.emptyToInt(params.get("length"));
		Page<Map<String, Object>> queryPage = repositoryUtil.queryForPageMap(sql.toString(), listObject.toArray(), start, length);
		result.put("iTotalDisplayRecords", queryPage.getTotalElements());
		result.put("data", queryPage.getContent());
		return result;
	}

	@Override
	public Map<String, Object> queryOverdueDataCount(Map<String, Object> params) throws SLException {
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
				.append("   select count(r.id) \"totalCount\",  ")
				.append("          trunc(nvl(sum(r.repayment_total_amount ), 0), 2) + trunc(nvl(sum(r.penalty_amount), 0), 2) \"totalAmount\",  ")
				.append("          trunc(nvl(sum(r.penalty_amount), 0), 2) \"totalOverdueExpense\" ")
				.append("       from bao_t_loan_info l   ")
				.append("       inner join bao_t_loan_cust_info lc on lc.id = l.relate_primary  ")
				.append("       inner join bao_t_loan_detail_info d on d.loan_id = l.id  ")
				.append("       inner join bao_t_repayment_plan_info r on r.loan_id = l.id  ")
				.append("       where r.repayment_status = '未还款'  and r.expect_repayment_date <to_char(sysdate, 'yyyyMMdd') ")
				.append("         and l.loan_status is not null ")
				;
		if(!StringUtils.isEmpty(params.get("loanCode"))) {
			sql.append("   and l.loan_code = ? ");
			listObject.add(params.get("loanCode"));
		}
		if(!StringUtils.isEmpty(params.get("custName"))) {
			sql.append("   and lc.cust_name = ? ");
			listObject.add(params.get("custName"));
		}
		if(!StringUtils.isEmpty(params.get("repaymentMethod"))) {
			sql.append("   and l.repayment_method = ? ");
			listObject.add(params.get("repaymentMethod"));
		}
		if(!StringUtils.isEmpty(params.get("loanType"))) {
			sql.append("   and l.loan_type = ? ");
			listObject.add(params.get("loanType"));
		}
		if(!StringUtils.isEmpty(params.get("expectRpaymentDateStart"))) {
			sql.append("   and to_char(r.expect_repayment_date, 'yyyy-MM-dd') >= ? ");
			listObject.add(params.get("expectRpaymentDateStart"));
		}
		if(!StringUtils.isEmpty(params.get("expectRpaymentDateEnd"))) {
			sql.append("   and to_char(r.expect_repayment_date, 'yyyy-MM-dd') <= ? ");
			listObject.add(params.get("expectRpaymentDateEnd"));
		}
		return repositoryUtil.queryForMap(sql.toString(), listObject.toArray()).get(0);
	}

	/**
	 * 还款数据查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-01
	 * @param params
     *      <tt>start               :String:起始值</tt><br>
     *      <tt>length              :String:长度</tt><br>
     *      <tt>loanCode            :String:借款编号(可以为空)</tt><br>
     *      <tt>custName            :String:客户姓名(可以为空)</tt><br>
     *      <tt>loanTerm            :String:借款期限(可以为空)</tt><br>
     *      <tt>repaymentMethod     :String:还款方式(可以为空)</tt><br>
     *      <tt>loanType            :String:借款类型(可以为空)</tt><br>
     *      <tt>loanStatus          :String:借款状态(可以为空)</tt><br>
     *      <tt>investStartDateStart:String:起息日期(可以为空)</tt><br>
     *      <tt>investStartDateEnd  :String:起息日期(可以为空)</tt><br>
     *      <tt>investEndDateStart  :String:到期日期</tt><br>
     *      <tt>investEndDateEnd    :String:到期日期</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Override
	public Map<String, Object> queryAlreadyRepayList(Map<String, Object> params) throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
				.append("   select l.id                        \"loanId\",  ")
				.append("          l.loan_code                 \"loanCode\",  ")
				.append("          lc.cust_name                \"custName\",  ")
				.append("          l.loan_type                 \"loanType\",  ")
				.append("          l.loan_amount               \"loanAmount\",  ")
				.append("          l.loan_term                 \"loanTerm\",  ")
				.append("          l.loan_unit                 \"loanUnit\",  ")
				.append("          round(d.year_irr*100,1)                  \"yearIrr\",  ")
				.append("          nvl(trunc(round(l.award_rate,8)*100,2),0) \"awardRate\",  ")
				.append("          l.repayment_method          \"repaymentMethod\",  ")
				.append("          l.loan_status               \"loanStatus\",  ")
				.append("          trunc((select nvl(sum(r.repayment_total_amount), 0) from bao_t_repayment_plan_info r where r.repayment_status = '未还款' and r.loan_id = l.id), 2) \"waitingPayment\",  ")
				.append("          trunc((select nvl(sum(r.repayment_total_amount), 0) from bao_t_repayment_plan_info r where r.repayment_status = '已还款' and r.loan_id = l.id), 2) \"hasPayment\",   ")
				.append("          l.invest_start_date         \"startDate\",  ")
				.append("          l.invest_end_date           \"endDate\" ")
				.append("                    ")
				.append("   from bao_t_loan_info l  ")
				.append("   inner join bao_t_loan_detail_info d on d.loan_id = l.id  ")
				.append("   inner join bao_t_loan_cust_info lc on lc.id = l.relate_primary  ")
				.append("   where l.loan_status is not null  ");
		if(!StringUtils.isEmpty(params.get("loanCode"))) {
			sql.append("   and l.loan_code = ? ");
			listObject.add(params.get("loanCode"));
		}
		if(!StringUtils.isEmpty(params.get("custName"))) {
			sql.append("   and lc.cust_name = ? ");
			listObject.add(params.get("custName"));
		}
		if(!StringUtils.isEmpty(params.get("loanTerm"))) {
			sql.append("   and l.loan_term = ? ");
			listObject.add(params.get("loanTerm"));
		}
		if(!StringUtils.isEmpty(params.get("loanUnit"))) {
			sql.append("   and l.loan_unit = ? ");
			listObject.add(params.get("loanUnit"));
		}
		if(!StringUtils.isEmpty(params.get("repaymentMethod"))) {
			sql.append("   and l.repayment_method = ? ");
			listObject.add(params.get("repaymentMethod"));
		}
		if(!StringUtils.isEmpty(params.get("loanType"))) {
			sql.append("   and l.loan_type = ? ");
			listObject.add(params.get("loanType"));
		}
		if(!StringUtils.isEmpty(params.get("loanStatus"))) {
			sql.append("   and l.loan_status = ? ");
			listObject.add(params.get("loanStatus"));
		}
		if(!StringUtils.isEmpty(params.get("investStartDateStart"))) {
			sql.append("   and to_char(l.invest_start_date, 'yyyy-MM-dd') >= ? ");
			listObject.add(params.get("investStartDateStart"));
		}
		if(!StringUtils.isEmpty(params.get("investStartDateEnd"))) {
			sql.append("   and to_char(l.invest_start_date, 'yyyy-MM-dd') <= ? ");
			listObject.add(params.get("investStartDateEnd"));
		}
		if(!StringUtils.isEmpty(params.get("investEndDateStart"))) {
			sql.append("   and to_char(l.invest_end_date, 'yyyy-MM-dd') >= ? ");
			listObject.add(params.get("investEndDateStart"));
		}
		if(!StringUtils.isEmpty(params.get("investEndDateEnd"))) {
			sql.append("   and to_char(l.invest_end_date, 'yyyy-MM-dd') <= ? ");
			listObject.add(params.get("investEndDateEnd"));
		}
		
		sql.append("  order by l.invest_end_date, l.loan_code ");
		int start=CommonUtils.emptyToInt(params.get("start"));
		int length=CommonUtils.emptyToInt(params.get("length"));
		Page<Map<String, Object>> queryPage = repositoryUtil.queryForPageMap(sql.toString(), listObject.toArray(), start, length);
		result.put("iTotalDisplayRecords", queryPage.getTotalElements());
		result.put("data", queryPage.getContent());
		return result;
	}

	@Override
	public Map<String, Object> queryAlreadyRepayCount(Map<String, Object> params) throws SLException {
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
				.append("   select count(l.id)                         \"totalCount\", ")
				.append("          nvl(sum(l.loan_amount), 0)          \"totalAmount\",  ")
				.append("          trunc(nvl(sum((select sum(r.repayment_total_amount) from bao_t_repayment_plan_info r where r.repayment_status = '未还款' and r.loan_id = l.id)), 0), 2) \"totalRepaymentAmount\",  ")
				.append("          trunc(nvl(sum((select sum(r.repayment_total_amount) from bao_t_repayment_plan_info r where r.repayment_status = '已还款' and r.loan_id = l.id)), 0), 2) \"totalAlreadyRepayAmount\"                     ")
				.append("   from bao_t_loan_info l  ")
				.append("   inner join bao_t_loan_detail_info d on d.loan_id = l.id  ")
				.append("   inner join bao_t_loan_cust_info lc on lc.id = l.relate_primary  ")
				.append("   where l.loan_status is not null  ");
		if(!StringUtils.isEmpty(params.get("loanCode"))) {
			sql.append("   and l.loan_code = ? ");
			listObject.add(params.get("loanCode"));
		}
		if(!StringUtils.isEmpty(params.get("custName"))) {
			sql.append("   and lc.cust_name = ? ");
			listObject.add(params.get("custName"));
		}
		if(!StringUtils.isEmpty(params.get("loanTerm"))) {
			sql.append("   and l.loan_term = ? ");
			listObject.add(params.get("loanTerm"));
		}
		if(!StringUtils.isEmpty(params.get("loanUnit"))) {
			sql.append("   and l.loan_unit = ? ");
			listObject.add(params.get("loanUnit"));
		}
		if(!StringUtils.isEmpty(params.get("repaymentMethod"))) {
			sql.append("   and l.repayment_method = ? ");
			listObject.add(params.get("repaymentMethod"));
		}
		if(!StringUtils.isEmpty(params.get("loanType"))) {
			sql.append("   and l.loan_type = ? ");
			listObject.add(params.get("loanType"));
		}
		if(!StringUtils.isEmpty(params.get("loanStatus"))) {
			sql.append("   and l.loan_status = ? ");
			listObject.add(params.get("loanStatus"));
		}
		if(!StringUtils.isEmpty(params.get("investStartDateStart"))) {
			sql.append("   and to_char(l.invest_start_date, 'yyyy-MM-dd') >= ? ");
			listObject.add(params.get("investStartDateStart"));
		}
		if(!StringUtils.isEmpty(params.get("investStartDateEnd"))) {
			sql.append("   and to_char(l.invest_start_date, 'yyyy-MM-dd') <= ? ");
			listObject.add(params.get("investStartDateEnd"));
		}
		if(!StringUtils.isEmpty(params.get("investEndDateStart"))) {
			sql.append("   and to_char(l.invest_end_date, 'yyyy-MM-dd') >= ? ");
			listObject.add(params.get("investEndDateStart"));
		}
		if(!StringUtils.isEmpty(params.get("investEndDateEnd"))) {
			sql.append("   and to_char(l.invest_end_date, 'yyyy-MM-dd') <= ? ");
			listObject.add(params.get("investEndDateEnd"));
		}
		return repositoryUtil.queryForMap(sql.toString(), listObject.toArray()).get(0);
	}

	@Override
	public Map<String, Object> queryLendMoneyList(Map<String, Object> params) throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
				.append("  select l.loan_code                 \"loanCode\", ")
				.append("         lc.cust_name                \"custName\", ")
				.append("         l.loan_title                \"loanTitle\", ")
				.append("         substr(lc.credentials_code, 0, 3) || '****' || substr(lc.credentials_code, length(lc.credentials_code) - 4, length(lc.credentials_code))  \"credentialsCode\", ")
				.append("         l.loan_type                 \"loanType\", ")
				.append("         l.loan_amount               \"loanAmount\", ")
				.append("         l.loan_term               \"loanTerm\", ")
				.append("         l.apply_time               \"applyTime\", ")
				.append("         l.loan_status               \"loanStatus\", ")
				.append("         trunc(ldi.year_irr*100,2)               \"yearRate\", ")
				.append("         to_char(pi.last_update_date, 'yyyy-MM-dd') \"fullScaleDate\", ")
				.append("         to_char(l.rasie_end_date, 'yyyy-MM-dd')            \"reviewedDate\", ")
				.append("         to_char(l.grant_date, 'yyyy-MM-dd')                \"lendDate\", ")
				.append("         l.grant_status              \"lendStatus\", ")
				.append("         u.user_name                 \"auditorName\", ")
				.append("         l.id						  \"loanId\" ")
				.append("  from bao_t_loan_info l  ")
				.append("  inner join bao_t_loan_detail_info d on d.loan_id = l.id ")
				.append("  inner join bao_t_loan_cust_info lc on lc.id = l.relate_primary ")
				.append("  inner join BAO_T_PROJECT_INVEST_INFO pi on pi.loan_id = l.id ")
				.append("  inner join BAO_T_LOAN_DETAIL_INFO ldi on l.id = ldi.loan_id ")
				.append("  left join com_t_user u on u.id = l.grant_user")
//				.append("  where l.loan_status = '满标复核' ");
				.append("  where l.loan_status IN ('满标复核','正常', '已到期') ")
				// 剔除雪橙金服的满标复核数据
				.append("   and l.id not in (select id  from bao_t_loan_info where loan_status = '满标复核' and company_name ='" + Constant.DEBT_SOURCE_XCJF + "') ");
		if(!StringUtils.isEmpty(params.get("loanCode"))) {
			sql.append("   and l.loan_code = ? ");
			listObject.add(params.get("loanCode"));
		}
		if(!StringUtils.isEmpty(params.get("custName"))) {
			sql.append("   and lc.cust_name = ? ");
			listObject.add(params.get("custName"));
		}
		if(!StringUtils.isEmpty(params.get("loanType"))) {
			sql.append("   and l.loan_type = ? ");
			listObject.add(params.get("loanType"));
		}
		if(!StringUtils.isEmpty(params.get("lendStatus"))) {
			sql.append("   and l.grant_status = ? ");
			listObject.add(params.get("lendStatus"));
		}
		if(!StringUtils.isEmpty(params.get("lendDateStart"))) {
			sql.append("   and to_char(l.grant_date, 'yyyy-MM-dd') >= ? ");
			listObject.add(params.get("lendDateStart"));
		}
		if(!StringUtils.isEmpty(params.get("lendDateEnd"))) {
			sql.append("   and to_char(l.grant_date, 'yyyy-MM-dd') <= ? ");
			listObject.add(params.get("lendDateEnd"));
		}
		if(!StringUtils.isEmpty(params.get("fullScaleDateStart"))) {
			sql.append("  and to_char(pi.last_update_date, 'yyyy-MM-dd') >= ?");
			listObject.add(params.get("fullScaleDateStart"));
		}
		if(!StringUtils.isEmpty(params.get("fullScaleDateEnd"))) {
			sql.append("  and to_char(pi.last_update_date, 'yyyy-MM-dd') <= ? ");
			listObject.add(params.get("fullScaleDateEnd"));
		}
		if(!StringUtils.isEmpty(params.get("reviewedDateStart"))) {
			sql.append("  and to_char(l.rasie_end_date, 'yyyy-MM-dd') >= ?");
			listObject.add(params.get("reviewedDateStart"));
		}
		if(!StringUtils.isEmpty(params.get("reviewedDateEnd"))) {
			sql.append("  and to_char(l.rasie_end_date, 'yyyy-MM-dd') <= ? ");
			listObject.add(params.get("reviewedDateEnd"));
		}
		
		sql.append("  order by decode(l.grant_status, '待放款', 1, 2), pi.last_update_date");
		int start=CommonUtils.emptyToInt(params.get("start"));
		int length=CommonUtils.emptyToInt(params.get("length"));
		Page<Map<String, Object>> queryPage = repositoryUtil.queryForPageMap(sql.toString(), listObject.toArray(), start, length);
		result.put("iTotalDisplayRecords", queryPage.getTotalElements());
		result.put("data", queryPage.getContent());
		return result;
	}

	@Override
	public List<Map<String, Object>> queryLoanInfobyLoanIds(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append("  select l.loan_code             \"loanCode\", ")
		.append("         lc.cust_name            \"custName\", ")
		.append("         substr(lc.credentials_code, 0, 3) || '****' || substr(lc.credentials_code, length(lc.credentials_code) - 4, length(lc.credentials_code))     \"credentialsCode\", ")
		.append("         l.loan_type             \"loanType\", ")
		.append("         round(l.loan_amount, 2) \"loanAmount\", ")
		.append("         l.id					  \"id\" ")
		.append("  from bao_t_loan_info l ")
		.append("  inner join bao_t_loan_cust_info lc on lc.id = l.relate_primary ")
		.append("  where 1 = 1 ");
		SqlCondition sqlCondition = new SqlCondition(sql, params);
		sqlCondition.addList("loanIds", "l.ID");
		
		return repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
	}
	
	/*
	     *      <tt>earnTotalAmount  	 :String:已赚金额</tt><br>
	     *      <tt>exceptTotalAmount	 :String:待收收益</tt><br>
	     *      <tt>investTotalAmount	 :String:在投金额</tt><br>
	     *      <tt>tradeTotalAmount 	 :String:投资总金额</tt><br>
	     *      <tt>exceptTotalPrincipal :String:待收本金</tt><br>
	     *      <tt>notStatyInvestAmount :String:投资中本金</tt><br>
	 */
	@Override
	public Map<String, Object> queryMyDisperseIncome(Map<String, Object> params) throws SLException {
		StringBuilder sql = new StringBuilder()
				.append(" select TRUNC_AMOUNT_WEB(sum(rd.trade_amount)) \"amount\" ")// 0
				.append("   from bao_t_account_flow_info     f, ")
				.append("        bao_t_payment_record_info   pr, ")
				.append("        bao_t_payment_record_detail rd, ")
				.append("        bao_t_invest_info i ")
				.append("  where f.relate_primary = i.id ")
				.append("    and f.id = pr.relate_primary ")
				.append("    and pr.id = rd.pay_record_id ")
				.append("    and rd.subject_type in ")
				.append("        ('还风险金逾期费用', '利息', '逾期费用', '违约金','优选项目奖励收益') ")
				.append("    and i.loan_id is not null ")
				.append("    and i.cust_id = ? ")
				.append("  union all ")
				.append("  select TRUNC_AMOUNT_WEB(sum(r.repayment_interest*h.hold_scale)) \"amount\" ")// 1
				.append("  from bao_t_invest_info i ")
				.append("   inner join bao_t_wealth_hold_info h on h.invest_id = i.id and h.loan_id = i.loan_id ")
				.append("   inner join bao_t_loan_info l on l.id = h.loan_id ")
				.append("   inner join bao_t_repayment_plan_info r on r.loan_id = l.id  ")
				.append("  where r.repayment_status = '未还款' ")
				.append("    and i.invest_status = '收益中' ")
				.append("    and i.cust_id = ? ")
				.append("   union all ")
				.append("    select TRUNC_AMOUNT_WEB(sum(i.invest_amount)) \"amount\" ")// 2
				.append("   from bao_t_invest_info i  ")
				.append("   inner join bao_t_loan_info l on l.id = i.loan_id ")
				.append("   where i.invest_status in ('投资中' ,'收益中', '已到期', '提前结清') ")
				.append("     and i.cust_id = ? ")
				.append("   union all ")
				.append("   select TRUNC_AMOUNT_WEB(sum(r.repayment_principal*h.hold_scale)) \"amount\" ") // 3
				.append("   from bao_t_invest_info i ")
				.append("   inner join bao_t_wealth_hold_info h on h.invest_id = i.id and h.loan_id = i.loan_id ")
				.append("   inner join bao_t_loan_info l on l.id = h.loan_id ")
				.append("   inner join bao_t_repayment_plan_info r on r.loan_id = l.id  ")
				.append("   where r.repayment_status = '未还款' ")
				.append("     and i.invest_status = '收益中' ")
				.append("     and i.cust_id = ? ")
				.append("   union all ")
				.append("    select TRUNC_AMOUNT_WEB(sum(i.invest_amount)) \"amount\" ") // 4
				.append("   from bao_t_invest_info i  ")
				.append("   inner join bao_t_loan_info l on l.id = i.loan_id ")
				.append("   where i.invest_status = '投资中' ")
				.append("     and i.cust_id = ? ")
				// 待收收益改造
				.append("   union all ") // 5 部分转让，还有持有
				.append(" select  ")
				.append("          nvl(sum(CASE when i.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') ")/* 债权转让改造上线日2017-06-01(具体时间这天左右) */
				.append("             THEN trunc(h.hold_scale*(select nvl(sum(r.repayment_interest), 0) from bao_t_repayment_plan_info r where r.loan_id = l.id and r.repayment_status = '未还款'), 2) ")
				.append("             /* 剩余持比的收益 */ ")
				.append("                  WHEN i.INVEST_MODE = '转让' THEN  ")
				.append("                  CASE WHEN to_char(?, 'yyyyMMdd') /* ba.非还款日当天购买(下个还款日-转让时间)/(下个还款日-上个还款日）*持有比例*当期本息 */ ")
				.append("                            < (SELECT min(r.EXPECT_REPAYMENT_DATE) /* 购买时间当期的还款日时间 */ ")
				.append("                                 FROM bao_t_repayment_plan_info r  ")
				.append("                                WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date ")
				.append("                                  AND r.loan_id = l.id) ")
				.append("                       THEN trunc(h.hold_scale * (select nvl(sum(r.repayment_interest), 0) ")
				.append("                                     from bao_t_repayment_plan_info r  ")
				.append("                                    where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
				.append("                                      and r.loan_id = l.id )  ")
				.append("                                * (d.NEXT_EXPIRY - to_date(i.effect_date, 'yyyyMMdd')) ")
				.append("                                / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE))) ")
				.append("                            , 2) ")
				.append("                          + trunc(h.hold_scale *  ")
				.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
				.append("                                    from bao_t_repayment_plan_info r  ")
				.append("                                   where r.EXPECT_REPAYMENT_DATE > to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
				.append("                                     and r.loan_id = l.id ) ")
				.append("                            , 2) ")
				.append("                       WHEN to_char(?, 'yyyyMMdd') /* bb.还款日当天购买(未还款的情况下)1/(下个还款日-上个还款日)*持有比例*当期本息  */ ")
				.append("                            >= (SELECT min(r.EXPECT_REPAYMENT_DATE) /* 购买时间当期的还款日时间 */ ")
				.append("                                FROM bao_t_repayment_plan_info r  ")
				.append("                               WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date ")
				.append("                                 AND r.loan_id = l.id) ")
				.append("                                  AND '未还款' = (SELECT r.REPAYMENT_STATUS ")
				.append("                                                  FROM bao_t_repayment_plan_info r  ")
				.append("                                                 WHERE r.EXPECT_REPAYMENT_DATE = (SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date AND r.loan_id = l.id) ")
				.append("                                                   AND r.loan_id = l.id) ")
				.append("                       THEN trunc(h.hold_scale * (select nvl(sum(r.repayment_interest), 0) ")
				.append("                                     from bao_t_repayment_plan_info r  ")
				.append("                                    where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
				.append("                                      and r.loan_id = l.id )  ")
				.append("                                  * (d.NEXT_EXPIRY - to_date(i.effect_date, 'yyyyMMdd') + case when to_date(i.effect_date, 'yyyyMMdd') = d.NEXT_EXPIRY then 1 else 0 end) ")
				.append("                                  / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE))) ")
				.append("                            , 2) ")
				.append("                          + trunc(h.hold_scale *  ")
				.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
				.append("                                    from bao_t_repayment_plan_info r  ")
				.append("                                   where r.EXPECT_REPAYMENT_DATE > to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
				.append("                                     and r.loan_id = l.id ) ")
				.append("                            , 2) ")
				.append("                       ELSE trunc(h.hold_scale *  ")
				.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
				.append("                                    from bao_t_repayment_plan_info r  ")
				.append("                                   where r.repayment_status = '未还款' ")
				.append("                                     and r.loan_id = l.id)  ")
				.append("                            , 2) END ")
				.append("             ELSE trunc(h.hold_scale *  ")
				.append("                       (select nvl(sum(r.repayment_interest), 0) ")
				.append("                          from bao_t_repayment_plan_info r  ")
				.append("                         where r.repayment_status = '未还款' ")
				.append("                           and r.loan_id = l.id)  ")
				.append("                  , 2)  ")// by gyk
//				.append(" + trunc(case when l.newer_flag = '新手标' ")
//				.append("            then ")
//				.append("                 case when l.loan_unit = '天'  ")
//				.append("                      then i.invest_amount * l.loan_term/360 * l.award_rate  ")
//				.append("                      else (select sum((cc.repayment_principal + cc.remainder_principal)*h.hold_scale* ")
//				.append("                       (case when l.repayment_method = '到期还本付息' then l.loan_term when l.repayment_method = '等额本息'then 1 when l.repayment_method = '每期还息到期付本' then 1 else 0 end)    ")
//				.append("                       * l.award_rate /12 )from bao_t_repayment_plan_info cc where cc.loan_id = l.id and cc.repayment_status = '未还款' )  ")
//				.append("                      end  ")
//				.append("            else 0 END ,2) ")
				.append(" +(SELECT  trunc(sum(case when l.repayment_method = '等额本息'  ")
				.append("              then cc.REPAYMENT_INTEREST * nvl(l.award_rate,0)/d.year_irr * h.hold_scale         ")
				.append("              when l.repayment_method = '每期还息到期付本'         ")
				.append("              then  (cc.repayment_principal + cc.remainder_principal)*l.award_rate *30/360*h.hold_scale         ")
				.append("              else  (cc.repayment_principal + cc.remainder_principal)*nvl(l.award_rate,0) *decode(l.loan_unit,'天',1,'月',30)*l.loan_term/360*h.hold_scale                        ")
				.append("              end ) ,2) from  bao_t_repayment_plan_info cc where cc.loan_id = l.id and cc.repayment_status = '未还款' ) ")
				.append("           END + /* 转让人当月转出持比的收益 */ ")
				.append("             /* i.非还款日[转让时间！=下个还款日](转让时间-上个还款日)/(下个还款日-上个还款日)*转让比例*当期本息 */ ")
				.append("             CASE WHEN TRUNC(?) < d.NEXT_EXPIRY /* (c1)条件包括上个还款日当天已还款之后购买的的转让 */ ")
				.append("                  THEN TRUNC(nvl((SELECT sum(lt.TRADE_SCALE /* 转让比例 */ ")
				.append("                                          * (select nvl(sum(r.repayment_interest), 0)  ")
				.append("                                               from bao_t_repayment_plan_info r  ")
				.append("                                              where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
				.append("                                                and r.loan_id = l.id ) ")
				.append("                                          * (to_date(invest.effect_date, 'yyyyMMdd') - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)))/* 还款日当天已还款之后的数据，下期没有收益 */ ")
				.append("                                          / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)))) ")
				.append("                                    FROM BAO_T_LOAN_TRANSFER lt, bao_t_wealth_hold_info wh, bao_t_invest_info invest ")
				.append("                                   WHERE invest.id = wh.invest_id ")
				.append("                                     AND wh.id = lt.receive_hold_id ")
				.append("                                     AND to_date(invest.effect_date, 'yyyyMMdd') < ? ")
				.append("                                     AND to_date(invest.effect_date, 'yyyyMMdd') >= nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)) /* 配合(c1) */ ")
				.append("                                     AND lt.SENDER_HOLD_ID = h.id) ")
				.append("                       , 0), 2) ")
				.append("                  /* ii.还款日(未还款的情况下转让时间<还款计划表实际还款时间) (转让时间-上个还款日-1)/(下个还款日-上个还款日)*转让比例*当期本息  */ ")
				.append("                  WHEN TRUNC(?) >= d.NEXT_EXPIRY /* (c2)当期未还款,如果已还款d.NEXT_EXPIRY变为下期还款日期在上面的when条件中 */ ")
				.append("                  THEN TRUNC(nvl((SELECT sum(lt.TRADE_SCALE /* 转让比例 */ ")
				.append("                                          * (select nvl(r.repayment_interest, 0) /* 当期本息 */ ")
				.append("                                               from bao_t_repayment_plan_info r  ")
				.append("                                              where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
				.append("                                                and r.loan_id = l.id ) ")
				.append("                                          * (to_date(invest.effect_date, 'yyyyMMdd') - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)) - case when to_date(invest.effect_date, 'yyyyMMdd') = d.next_expiry then 1 else 0 end) ")
				.append("                                          / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)))) ")
				.append("                                    FROM BAO_T_LOAN_TRANSFER lt, bao_t_wealth_hold_info wh, bao_t_invest_info invest ")
				.append("                                   WHERE invest.id = wh.invest_id ")
				.append("                                     AND wh.id = lt.receive_hold_id ")
				.append("                                     AND to_date(invest.effect_date, 'yyyyMMdd') <= d.NEXT_EXPIRY ")
				.append("                                     AND to_date(invest.effect_date, 'yyyyMMdd') >= nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)) ")
				.append("                                     AND lt.SENDER_HOLD_ID = h.id) ")
				.append("                       , 0), 2) ")
				.append("                  ELSE 0 END), 0) \"amount\" ")
				.append("  from bao_t_invest_info i ")
				.append("   inner join bao_t_wealth_hold_info h on h.invest_id = i.id and h.loan_id = i.loan_id ")
				.append("   inner join bao_t_loan_info l on l.id = h.loan_id ")
				.append("   inner join BAO_T_LOAN_DETAIL_INFO d on d.loan_id= l.id ")
				.append("  where 1=1  ")
				.append("    and i.invest_status = '收益中' ")
				.append("    and i.cust_id = ? ")
				.append("   union all ") // 6 全部转让那部分
				.append(" SELECT trunc(nvl(sum(CASE WHEN to_date(ri.EFFECT_DATE, 'yyyyMMdd') < d.NEXT_EXPIRY ") 
				.append("            AND to_date(ri.EFFECT_DATE, 'yyyyMMdd') >= nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)) /* (c1)条件包括还款日当天已还款之后购买的的转让 */  ")
				.append("           THEN t.TRADE_SCALE /* 转让比例 */  ")
				.append("                   * (select nvl(sum(r.repayment_interest), 0)  ")
				.append("                        from bao_t_repayment_plan_info r  ")
				.append("                       where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd')  ")
				.append("                         and r.loan_id = l.id )  ")
				.append("                   * (to_date(ri.EFFECT_DATE, 'yyyyMMdd') - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE))) /* 还款日当天已还款之后的数据（还款可能出现延期一两天），下期没有收益 */  ")
				.append("                   / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)))  ")
				.append("          /* ii.还款日(未还款的情况下转让时间<还款计划表实际还款时间) (转让时间-上个还款日-1)/(下个上个还款日)*转让比例*当期本息  */  ")
				.append("           WHEN to_date(ri.EFFECT_DATE, 'yyyyMMdd') >= d.NEXT_EXPIRY /* (c2)当期未还款,如果已还款d.NEXT_EXPIRY变为下期还款日期在上面的when条件中 */  ")
				.append("           THEN t.TRADE_SCALE /* 转让比例 */  ")
				.append("                   * (select nvl(sum(r.repayment_interest), 0) /* 当期本息 */  ")
				.append("                        from bao_t_repayment_plan_info r  ")
				.append("                       where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd')  ")
				.append("                         and r.loan_id = l.id )  ")
				.append("                   * (to_date(ri.EFFECT_DATE, 'yyyyMMdd') - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)) -1)  /* 还款日当天未还款前转让（还款可能出现延期一两天），让一天利息 */ ") 
				.append("                   / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)))  ")
				.append("           ELSE 0 END),0),2) \"amount\"/* 其他时间为零 */ ")
				.append("  from bao_t_invest_info i  ")
				.append(" INNER JOIN bao_t_wealth_hold_info h on h.invest_id = i.id and h.loan_id = i.loan_id  ")
				.append(" INNER JOIN bao_t_loan_info l on l.id = h.loan_id  ")
				.append(" INNER JOIN BAO_T_LOAN_DETAIL_INFO d on d.loan_id= l.id ")
				.append(" INNER JOIN BAO_T_LOAN_TRANSFER t ON t.SENDER_HOLD_ID = h.ID AND t.CREATE_DATE > to_date(?, 'yyyy-MM-dd HH24:mi:ss') ")
				.append(" INNER JOIN bao_t_wealth_hold_info rh ON rh.ID = t.RECEIVE_HOLD_ID ")
				.append(" INNER JOIN bao_t_invest_info ri ON ri.INVEST_STATUS IN ('收益中', '已转让') /* 受让人还可以再转让 */ ")
				.append("        AND to_date(ri.EFFECT_DATE, 'yyyyMMdd') >= nvl(d.LAST_EXPIRY, TRUNC(l.INVEST_START_DATE)) ")
				.append("        AND to_date(ri.EFFECT_DATE, 'yyyyMMdd') <= d.NEXT_EXPIRY ")
				.append("        AND ri.id = rh.INVEST_ID  ")
				.append(" where 1=1 ")
				.append("   and i.invest_status = '已转让' ")
				.append("   and i.cust_id = ? ")
		;
		List<Object> listObject = new ArrayList<Object>();
		listObject.add(params.get("custId"));
		listObject.add(params.get("custId"));
		listObject.add(params.get("custId"));
		listObject.add(params.get("custId"));
		listObject.add(params.get("custId"));
		// 待收收益新算法
		// 1.收益中
		Date date = new Date();
		String onlineTime = (String) params.get("onlineTime");
		listObject.add(onlineTime);
		listObject.add(date);
		listObject.add(date);
		listObject.add(date);
		listObject.add(date);
		listObject.add(date);
		listObject.add(params.get("custId"));
		// 2.已转让
		listObject.add(onlineTime);
		listObject.add(params.get("custId"));
		
		Map<String, Object> result = Maps.newHashMap();
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
		result.put("earnTotalAmount", list.get(0).get("amount"));
//		result.put("exceptTotalAmount", list.get(1).get("amount"));
		result.put("exceptTotalAmount", ArithUtil.add(new BigDecimal(list.get(5).get("amount").toString()), new BigDecimal(list.get(6).get("amount").toString())));
		result.put("investTotalAmount", ArithUtil.add(new BigDecimal(list.get(3).get("amount").toString()), new BigDecimal(list.get(4).get("amount").toString())));
		result.put("tradeTotalAmount", list.get(2).get("amount"));
		result.put("exceptTotalPrincipal", list.get(3).get("amount"));
		result.put("notStatyInvestAmount", list.get(4).get("amount"));
		return result;
	}

	@Override
	public ResultVo queryAllShowLoanList(Map<String, Object> params)
			throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		
		StringBuilder sqlString = new StringBuilder()
		.append("  select  t.id                       \"wealthId\",    ")
		.append("           t.LOAN_TITLE \"lendingType\",    ")
		.append("           t.rebate_ratio             \"rebateRatio\",    ")
		.append("           t.loan_code                \"lendingNo\",    ")
		.append("           t.loan_amount              \"planTotalAmount\",    ")
		.append("           t.invest_min_amount        \"investMinAmount\",    ")
		.append("           pro.already_invest_amount  \"alreadyInvestAmount\",        ")
		.append("           decode(t.loan_status, '募集中', pro.already_invest_scale, 1) \"investScale\",    ")
		.append("           t.loan_term               \"typeTerm\",    ")
		.append("           t.LOAN_UNIT               \"typeUnit\",    ")
		.append("           s.year_irr                \"yearRate\",    ")
		.append("           0                         \"awardRate\",    ")
		.append("           t.repayment_method        \"incomeType\",    ")
		.append("           t.publish_date            \"releaseDate\",    ")
		.append("           null                      \"effectDate\",    ")
		.append("           null                      \"endDate\",    ")
		.append("           t.loan_status             \"wealthStatus\",  ")
		.append("           decode(t.loan_status, '募集中', t.loan_amount - pro.already_invest_amount, 0) \"currUsableValue\"    ")
		.append("         , CASE WHEN t.SEAT_TERM = -1 OR t.SEAT_TERM IS NULL THEN '不允许转让' WHEN t.SEAT_TERM = 0 THEN '可以立即转让' ELSE '出借'||t.SEAT_TERM||'天即可转让' END \"transferCondition\" ")
		.append("          from bao_t_loan_info t    ")
		.append("         inner join bao_t_loan_detail_info s on s.loan_id = t.id ")
		.append("         inner join bao_t_project_invest_info pro on pro.loan_id = t.id   ")
		.append("         where t.LOAN_STATUS in ( '募集中','满标复核','正常','逾期','提前结清','已到期','流标 ')   ");
		if(Constant.PRODUCT_TYPE_11.equals(params.get("productType"))){
			sqlString.append(" and t.newer_flag = '新手标' ");
		}
		SqlCondition sql = new SqlCondition(sqlString, params);
		sql.addList("ids", "t.id");
		sql.addSql("  order by decode(t.LOAN_STATUS,'募集中',5,'满标复核',6,'正常',8,'逾期',9,'提前结清',10,'已到期',11,'流标',12)  ")
		   .addSql("  , t.RASIE_END_DATE - SYSDATE  ")
		   .addSql("  , nvl(pro.ALREADY_INVEST_SCALE,0) desc  ")
		   .addSql("  , t.LOAN_TERM*decode(t.LOAN_UNIT, '天',1,'月',30,1)  ");
	
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), sql.toArray());
		result.put("iTotalDisplayRecords", list.size());
		result.put("data", list);
		return new ResultVo(true, "查询成功", result);
	}

	@Override
	public Page<Map<String, Object>> queryThirdCompanyRepaymentList(
			Map<String, Object> params) {
		
		StringBuilder sqlString = new StringBuilder()
		.append("   select r.loan_code \"projectName\",  ")
		.append("              r.loan_type \"loanType\",  ")
		.append("              s.current_term \"currentTerm\",   ")
		.append("              (SELECT count(1) FROM bao_t_repayment_plan_info WHERE LOAN_ID=r.ID) \"sumTerm\",   ")
		.append("              to_char(to_date(s.expect_repayment_date, 'yyyyMMdd'), 'yyyy-MM-dd') \"repaymentDate\",   ")
		.append("              to_char(s.repayment_principal + s.repayment_interest, 'fm9999990.00') \"repaymentPI\",   ")
		.append("              to_char(0, 'fm9999990.00') \"overdueExpense\",   ")
		.append("              to_char(nvl(decode(r.loan_type, '善转贷', r.PLAT_SERVICE_AMOUNT, s.account_manage_expense),0), 'fm9999990.00') \"manageExpense\",   ")
		.append("              to_char(nvl(decode(r.loan_type, '善转贷', r.PLAT_SERVICE_AMOUNT, s.account_manage_expense),0), 'fm9999990.00')  \"serviceExpense\",   ")
		.append("              to_char(s.repayment_total_amount, 'fm9999990.00')\"repaymentTotalAmount\",   ")
		.append("              decode(s.repayment_status, '未还款', decode(s.is_amount_frozen, '是', '已还款（还款冻结）', s.repayment_status), s.repayment_status) \"repaymentStatus\",   ")
		.append("              s.id \"repaymentPlanId\",   ")
		.append("              r.id \"projectId\",   ")
		.append("              decode(s.repayment_status, '已还款', '是', s.is_amount_frozen) \"isRepayed\",   ")
		.append("              r.loan_status \"projectStatus\",  ")
		.append("              'repayed' \"isWarning\", ")
		.append("              decode(r.loan_type, '善转贷', 'Y', 'N') \"isGreen\" ")
		.append("    FROM bao_t_loan_info r  ")
		.append("    inner join bao_t_repayment_plan_info s on s.loan_id = r.id  ")
		.append("    where r.loan_status = '正常'  ");
		
		// 注：善转贷的服务费从借款服务费中获取，且不算入应还总额。仅做展示用途。
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, params);
		sqlCondition.addString("userId", "cust_id")
					.addLike("projectName", "r.loan_code")
					.addBeginDate("startRepaymentDate", "to_date(s.expect_repayment_date, 'yyyyMMdd')")
					.addEndDate("endRepaymentDate", "to_date(s.expect_repayment_date, 'yyyyMMdd')")
					.addString("repaymentStatus", "s.repayment_status")
					.addString("repaymentPlanId", "s.id")
					.addSql("order by decode(s.repayment_status, '未还款', 1, '已还款', 2), ")
					.addSql("case when s.repayment_status = '未还款' then s.expect_repayment_date end asc, ")
					.addSql("case when s.repayment_status = '已还款' then s.expect_repayment_date end desc ");
		
		return repositoryUtil.queryForPageMap(
				sqlCondition.toString(), 
				sqlCondition.toArray(), 
				Integer.valueOf(params.get("start").toString()),
				Integer.valueOf(params.get("length").toString()));
	}

	@Override
	public Page<Map<String, Object>> queryMyCreditTransferingList(
			Map<String, Object> params) throws SLException {
		
		StringBuilder sql = new StringBuilder()
		.append("   select a.id \"transferApplyId\", ")
		.append("          h.loan_id \"disperseId\", ")
		.append("          l.loan_title \"loanTitle\", ")
		.append("          l.loan_code \"loanCode\", ")
		.append("          d.year_irr \"yearRate\", ")
		.append("          ceil(months_between(l.invest_end_date, sysdate)) || '个月' \"remainTerm\", ")
		// update by http://192.16.150.101:8080/browse/SLCF-2823 at 2017-05-18
//		.append("          trunc(a.remainder_trade_scale*decode(nvl(r.repayment_status, '未还款'), '未还款', c.value_repayment_before, c.value_repayment_after), 2) \"holdValue\", ") // 剩余转让价值
//		.append("          TRUNC(TRUNC(a.remainder_trade_scale *  ")
//		.append("                      decode(nvl(r.repayment_status, '未还款'), '未还款', ")
//		.append("                      c.value_repayment_before, ")
//		.append("                      c.value_repayment_after), ")
//		.append("                     2) * a.REDUCED_SCALE, ")
//		.append("               2) \"tradeAmount\", ") // 剩余转让金额
		.append("          TRUNC_AMOUNT_WEB(a.remainder_trade_scale * d.CREDIT_REMAINDER_PRINCIPAL) \"remainPrincipal\", ") // 剩余转让金额
		.append("          a.TRADE_PRINCIPAL \"transferOut\", ") // 转出金额
		.append("          a.transfer_end_date \"transferEndDate\", ")
		.append("          i.invest_mode \"investMode\" ")
		.append("   from bao_t_loan_transfer_apply a ")
		.append("   inner join bao_t_wealth_hold_info h on h.id = a.sender_hold_id ")
		.append("   inner join bao_t_invest_info i on i.id = h.invest_id ")
		.append("   inner join bao_t_loan_info l on l.id = h.loan_id ")
		.append("   inner join bao_t_loan_detail_info d on d.loan_id = l.id ")
//		.append("   inner join bao_t_credit_right_value c on c.loan_id = h.loan_id and c.value_date = to_char(sysdate, 'yyyyMMdd') ")
//		.append("   left  join bao_t_repayment_plan_info r on r.loan_id = h.loan_id and r.expect_repayment_date = to_char(sysdate, 'yyyyMMdd') ")
		.append("   where a.cancel_status = '未撤销'  ")
		.append("   and a.apply_status in ('待转让', '部分转让成功')  ")
		.append("    and a.audit_status = '通过' ");//gyk 2017.3.14
		
		SqlCondition sqlCondition = new SqlCondition(sql, params);
		sqlCondition.addString("custId", "a.sender_cust_id");
		
		sqlCondition.addSql(" order by a.transfer_end_date desc ");
		return repositoryUtil.queryForPageMap(
				sqlCondition.toString(), 
				sqlCondition.toArray(), 
				Integer.valueOf(params.get("start").toString()),
				Integer.valueOf(params.get("length").toString()));
	}

	@Override
	public Page<Map<String, Object>> queryMyCreditBeTransferedList(Map<String, Object> params)
			throws SLException {
		StringBuilder sql = new StringBuilder()
		.append("   select t.id \"transferId\", ")
		.append("          t.sender_loan_id \"disperseId\", ")
		.append("          l.loan_title \"loanTitle\", ")
		.append("          l.loan_code \"loanCode\", ")
		.append("          d.year_irr \"yearRate\", ")
		.append("          t.transfer_apply_id \"transferApplyId\", ")
//		.append("          t.trade_amount \"transferAmount\", ")
//		.append("          t.trade_value \"transferValue\", ")
//		.append("          TRUNC(t.trade_amount - t.trade_value, 2)  \"transferInterest\", ")
		.append("          case when lta.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then t.trade_amount  ") // 转让金额
		.append("               else TRUNC(lta.trade_scale * ( ")
		.append("                    SELECT nvl(sum(CASE WHEN to_char(lta.create_date,'yyyyMMdd') = r.EXPECT_REPAYMENT_DATE ")
		.append("                                     AND lta.create_date > r.FACT_REPAYMENT_DATE ")
		.append("                                    THEN 0 ELSE r.REPAYMENT_PRINCIPAL END), 0) ")
		.append("                                    FROM BAO_T_REPAYMENT_PLAN_INFO r ")
		.append("                                   WHERE r.EXPECT_REPAYMENT_DATE >= to_char(lta.create_date,'yyyyMMdd') ")
		.append("                                     AND r.LOAN_Id = l.id ")
		.append("          ), 2) end \"remainPrincipal\", ")
		.append("          case when lta.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then t.trade_value ") // 转出金额
//		.append("               else TRUNC(t.TRADE_PRINCIPAL, 2) end \"transferAmount\", ") // 
		.append("               else TRUNC(t.trade_scale * ( ")
		.append("                    SELECT nvl(sum(CASE WHEN to_char(t.create_date,'yyyyMMdd') = r.EXPECT_REPAYMENT_DATE ")
		.append("                                     AND t.create_date > r.FACT_REPAYMENT_DATE ")
		.append("                                    THEN 0 ELSE r.REPAYMENT_PRINCIPAL END), 0) ")
		.append("                                    FROM BAO_T_REPAYMENT_PLAN_INFO r ")
		.append("                                   WHERE r.EXPECT_REPAYMENT_DATE >= to_char(t.create_date,'yyyyMMdd') ")
		.append("                                     AND r.LOAN_Id = l.id ")
		.append("          ), 2) end \"transferAmount\", ")
		.append("          case when lta.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then TRUNC(t.trade_amount - t.trade_value, 2) else trunc(t.TRADE_PRINCIPAL * lta.REDUCED_SCALE, 2) - trunc(t.TRADE_PRINCIPAL, 2) end \"transferInterest\", ")// 转让盈亏
		.append("          l.invest_end_date \"investEndDate\", ")
		.append("          i.invest_mode \"investMode\" ")
		.append("         , case when b.invest_source = 'auto' then '自动投标' when b.invest_source = 'reserveinvest' then '优先投' else '手动投标' end \"investSource\" ")
		.append("   from bao_t_loan_transfer t ")
		.append("   inner join bao_t_wealth_hold_info h on h.id = t.sender_hold_id")
		.append("   inner join bao_t_invest_info i on i.id = h.invest_id and i.loan_id = h.loan_id ")
		.append("   inner join bao_t_invest_detail_info b on b.invest_id = i.id ")
		.append("   inner join bao_t_loan_info l on l.id = t.sender_loan_id ")
		.append("   inner join bao_t_loan_detail_info d on d.loan_id = l.id ")
		.append("   INNER JOIN BAO_T_LOAN_TRANSFER_APPLY lta ON lta.ID = t.TRANSFER_APPLY_ID ")
		.append("   where t.transfer_apply_id is not null ");
		List<Object> listObject = new ArrayList<Object>();
		listObject.add((String)params.get("onlineTime"));
		listObject.add((String)params.get("onlineTime"));
		listObject.add((String)params.get("onlineTime"));
		
		SqlCondition sqlCondition = new SqlCondition(sql, params,listObject);
		sqlCondition.addString("custId", "t.sender_cust_id");
		
		sqlCondition.addSql(" order by t.create_date desc ");
		return repositoryUtil.queryForPageMap(
				sqlCondition.toString(), 
				sqlCondition.toArray(), 
				Integer.valueOf(params.get("start").toString()),
				Integer.valueOf(params.get("length").toString()));
	}

	/**
	 * 查看转让详细信息（转让中债权列表-查看）
	 *
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
     *      <tt>transferApplyId:String:转让申请Id(转让中债权\转出的债权)</tt><br>
	 * @return
     *      <tt>transferApplyId  :String:转让申请Id</tt><br>
     *      <tt>nowLoanValue     :String:当前债权价值</tt><br>
     *      <tt>remainPrincipal  :String:剩余本金</tt><br>
     *      <tt>remainTerm       :String:剩余期限(月)</tt><br>
     *      <tt>exceptRepayAmount:String:待收本息</tt><br>
     *      <tt>tradeScale       :String:转让比例</tt><br>
     *      <tt>discountScale    :String:折价比例</tt><br>
     *      <tt>transferAmount   :String:转让金额</tt><br>
     *      <tt>transferExpense  :String:转让费用</tt><br>
     *      <tt>exceptAmount     :String:预计到账金额（收入金额）</tt><br>
     *      <tt>tradeEndDate     :String:转让结束日期</tt><br>
	 * @throws SLException
	 */
	public Object queryMyCreditTransferingDetail(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT wh.ID \"holdId\" ")
//		.append("      , TRUNC(lta.TRADE_SCALE * decode(nvl(rp.repayment_status, '未还款'), ")
//		.append("                               '未还款', ")
//		.append("                               cr.value_repayment_before, ")
//		.append("                               cr.value_repayment_after), 2) \"nowLoanValue\" ")
//		.append("      , TRUNC(lta.TRADE_SCALE * (SELECT sum(REPAYMENT_INTEREST+REPAYMENT_PRINCIPAL)  ")
//		.append("                                 FROM bao_t_repayment_plan_info  ")
//		.append("                                WHERE LOAN_ID=loan.ID  ")
//		.append("                                  AND REPAYMENT_STATUS='未还款') ")
//		.append("             , 2) \"exceptRepayAmount\" ") //待收本息
		.append("      , TRUNC_AMOUNT_WEB(lta.TRADE_SCALE / lta.TRANSFER_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL) \"remainPrincipal\" ") // 持有本金
		.append("      , TRUNC_AMOUNT_WEB(lta.TRADE_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL) \"transAmount\" ") // 转让金额
		.append("      , TRUNC_AMOUNT_WEB(lta.TRADE_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL * lta.REDUCED_SCALE) \"reducedAmount\" ")// 折价金额
		.append("      , TRUNC_AMOUNT_WEB(lta.REMAINDER_TRADE_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL) \"factRemainPrincipal\" ") // 转让剩余本金
		.append("      , lta.TRADE_PRINCIPAL \"transferOut\" ") // 已转让金额
		.append("      , TRUNC(lta.TRADE_SCALE * (SELECT REPAYMENT_INTEREST  ")
		.append("                                 FROM bao_t_repayment_plan_info  ")
		.append("                                WHERE LOAN_ID = loan.ID  ")
		.append("                                  AND EXPECT_REPAYMENT_DATE = to_char(ld.NEXT_EXPIRY,'yyyyMMdd')) ")
		.append("             , 2) \"currentInterest\" ") // 当期收益（不转让时原始收益）
		.append("      , TRUNC(wh.HOLD_SCALE * (SELECT sum(REPAYMENT_INTEREST)  ")
		.append("                                 FROM bao_t_repayment_plan_info  ")
		.append("                                WHERE LOAN_ID=loan.ID  ")
		.append("                                  AND REPAYMENT_STATUS='未还款') ")
		.append("             , 2) \"exceptRepayInterest\" ") // 待收总收益
		.append("      , ceil(months_between(loan.invest_end_date, lta.transfer_start_date)) || '个月' \"remainTerm\" ")
		.append("      , lta.TRANSFER_SCALE \"tradeScale\" ")
		.append("      , lta.REDUCED_SCALE \"reducedScale\" ")
//		.append("      , TRUNC(TRUNC(lta.TRADE_SCALE * decode(nvl(rp.repayment_status, '未还款'), ")
//		.append("                               '未还款', ")
//		.append("                               cr.value_repayment_before, ")
//		.append("                               cr.value_repayment_after), 2)* lta.REDUCED_SCALE, 2) \"transferAmount\" ")
//		.append("      , TRUNC(TRUNC(TRUNC(lta.TRADE_SCALE * decode(nvl(rp.repayment_status, '未还款'), ")
//		.append("                               '未还款', ")
//		.append("                               cr.value_repayment_before, ")
//		.append("                               cr.value_repayment_after), 2)* lta.REDUCED_SCALE, 2) ")
//		.append("             * ? ") // 费率
//		.append("             , 2) \"transferExpense\" ")
//		.append("      , TRUNC(TRUNC(lta.TRADE_SCALE * decode(nvl(rp.repayment_status, '未还款'), ")
//		.append("                               '未还款', ")
//		.append("                               cr.value_repayment_before, ")
//		.append("                               cr.value_repayment_after), 2)* lta.REDUCED_SCALE, 2)  ")
//		.append("      - TRUNC(TRUNC(TRUNC(lta.TRADE_SCALE * decode(nvl(rp.repayment_status, '未还款'), ")
//		.append("                               '未还款', ")
//		.append("                               cr.value_repayment_before, ")
//		.append("                               cr.value_repayment_after), 2)* lta.REDUCED_SCALE, 2) ")
//		.append("             * ? ") // 费率
//		.append("             , 2) \"exceptArriveAmount\" ")
		.append("      , TRUNC(TRUNC(lta.TRADE_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL, 2) * ?, 2) \"transferExpense\" ")// 管理费
		.append("      , TRUNC(TRUNC(lta.TRADE_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL, 2) * lta.REDUCED_SCALE, 2) ")
		.append("      - TRUNC(TRUNC(lta.TRADE_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL, 2) * ?, 2) \"exceptArrivePrincipal\" ")// 预计到账本金 = 折价金额 - 转让费用
		.append("      , TRUNC(lta.TRADE_SCALE * (SELECT REPAYMENT_INTEREST  ") // 预计到账利息
		.append("                                 FROM bao_t_repayment_plan_info  ")
		.append("                                WHERE LOAN_ID = loan.ID  ")
		.append("                                  AND EXPECT_REPAYMENT_DATE = to_char(ld.NEXT_EXPIRY,'yyyyMMdd')) ")
		.append("             * (trunc(lta.create_date) - decode(ld.Last_expiry,null,trunc(loan.INVEST_START_DATE),ld.Last_expiry) - case when trunc(lta.create_date) >= ld.NEXT_EXPIRY then 1 else 0 end) /* 还款日当天未还款前转让（还款可能出现延期一两天），让一天利息 */ ")
		.append("             / (ld.NEXT_EXPIRY - decode(ld.Last_expiry,null,trunc(loan.INVEST_START_DATE),ld.Last_expiry)) ")
		.append("             , 2) \"exceptArriveInterest\" ") // 预计到账利息
		.append("      , TRUNC(TRUNC(lta.TRADE_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL, 2) * lta.REDUCED_SCALE, 2) ")
		.append("      - TRUNC(TRUNC(lta.TRADE_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL, 2) * ?, 2) ")// 预计到账本金 = 折价金额 - 转让费用
		.append("      + TRUNC(lta.TRADE_SCALE * (SELECT REPAYMENT_INTEREST  ") // 预计到账利息
		.append("                                 FROM bao_t_repayment_plan_info  ")
		.append("                                WHERE LOAN_ID = loan.ID  ")
		.append("                                  AND EXPECT_REPAYMENT_DATE = to_char(ld.NEXT_EXPIRY,'yyyyMMdd')) ")
		.append("             * (trunc(lta.create_date) - decode(ld.Last_expiry,null,trunc(loan.INVEST_START_DATE),ld.Last_expiry) - case when trunc(lta.create_date) >= ld.NEXT_EXPIRY then 1 else 0 end) /* 还款日当天未还款前转让（还款可能出现延期一两天），让一天利息 */ ")
		.append("             / (ld.NEXT_EXPIRY - decode(ld.Last_expiry,null,trunc(loan.INVEST_START_DATE),ld.Last_expiry)) ")
		.append("             , 2) \"exceptArriveAmount\" ") // 预计到账金额 =  预计到账本金  +  预计到账利息
		.append("      , lta.TRANSFER_END_DATE \"tradeEndDate\" ") 
//		.append("      , lta.TRADE_AMOUNT \"tradeAmount\" ") 
//		.append("      , lta.TRADE_VALUE \"tradeValue\" ")  
		.append("      , lta.MANAGE_AMOUNT \"manageAmount\" ")  
		.append("      , i.INVEST_MODE \"investMode\" ")  
		.append("      , loan.LOAN_TITLE \"loanTitle\" ") 
		.append("   FROM BAO_T_LOAN_TRANSFER_APPLY lta ")
		.append("  INNER JOIN BAO_T_WEALTH_HOLD_INFO wh  ")
		.append("     ON lta.SENDER_HOLD_ID = wh.ID ")
		.append("  INNER JOIN BAO_T_INVEST_INFO i ")
		.append("     ON i.id = wh.invest_id ")
		.append("  INNER JOIN BAO_T_LOAN_INFO loan ")
		.append("     ON loan.ID = wh.LOAN_ID ")
		.append("  INNER JOIN BAO_T_LOAN_DETAIL_INFO ld ")
		.append("     ON ld.LOAN_ID = loan.ID ")
//		.append("  INNER JOIN bao_t_credit_right_value cr ")
//		.append("     ON cr.loan_id = wh.loan_id ")
//		.append("    AND cr.value_date = to_char(lta.transfer_start_date, 'yyyyMMdd') ")
//		.append("   LEFT JOIN bao_t_repayment_plan_info rp ")
//		.append("     ON rp.loan_id = wh.loan_id and expect_repayment_date = to_char(lta.transfer_start_date, 'yyyyMMdd') ")
		.append("  WHERE 1=1 ")
		.append("    AND lta.ID = ? ");
		List<Object> listObject = new ArrayList<Object>();
		listObject.add(params.get("transferRate"));
		listObject.add(params.get("transferRate"));
		listObject.add(params.get("transferRate"));		
		listObject.add(params.get("transferApplyId"));
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
		return list.size() == 0 ? new HashMap<String, Object>() : list.get(0);
	}

	/**
	 * 查看转让详细信息（转出的债权列表-查看）
	 *
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
     *      <tt>transferId:String:转让记录表ID</tt><br>
	 * @return
     *      <tt>transferValue    :String:转出债权价值</tt><br>
     *      <tt>remainTerm       :String:剩余期限(月)</tt><br>
     *      <tt>exceptRepayAmount:String:待收本息</tt><br>
     *      <tt>tradeAmount      :String:转让金额</tt><br>
     *      <tt>transferExpense  :String:转让费用</tt><br>
     *      <tt>exceptAmount     :String:预计到账金额</tt><br>
     *      <tt>tradeDate        :String:成交时间</tt><br>
     *      <tt>profitAmount     :String:盈亏</tt><br>
	 * @throws SLException
	 */
	public Object queryMyCreditBeTransferedDetail(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT loan.LOAN_TITLE \"loanTitle\" ")
//		.append("      , lt.TRADE_VALUE \"transferValue\" ")
		.append("      , ceil(months_between(to_date(invest.EXPIRE_DATE, 'yyyy-MM-dd') ")
		.append("                          , to_date(invest.EFFECT_DATE, 'yyyy-MM-dd')))  ")
		.append("        || '个月' \"remainTerm\" ")
//		.append("      , TRUNC(lt.TRADE_SCALE * (SELECT sum(REPAYMENT_INTEREST + REPAYMENT_PRINCIPAL)  ")
//		.append("                                 FROM bao_t_repayment_plan_info  ")
//		.append("                                WHERE LOAN_ID = lt.SENDER_LOAN_ID ")
//		.append("                                  AND REPAYMENT_STATUS='未还款') ")
//		.append("        , 2) \"exceptRepayAmount\" ")
//		.append("      , lt.TRADE_AMOUNT \"tradeAmount\" ") 
//		.append("      , lt.TRADE_AMOUNT - lt.TRANSFER_EXPENSES \"exceptAmount\" ")
//		.append("      , TRUNC(lt.TRADE_AMOUNT - lt.TRADE_VALUE,2) \"profitAmount\" ")
		.append("      , case when a.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then lt.TRADE_AMOUNT ") // 转出金额
		
//		.append("               else lt.TRADE_PRINCIPAL end \"transferOut\" ") 
		.append("               else TRUNC(lt.trade_scale * ( ")
		.append("                    SELECT nvl(sum(CASE WHEN to_char(lt.create_date,'yyyyMMdd') = r.EXPECT_REPAYMENT_DATE ")
		.append("                                     AND lt.create_date > r.FACT_REPAYMENT_DATE ")
		.append("                                    THEN 0 ELSE r.REPAYMENT_PRINCIPAL END), 0) ")
		.append("                                    FROM BAO_T_REPAYMENT_PLAN_INFO r ")
		.append("                                   WHERE r.EXPECT_REPAYMENT_DATE >= to_char(lt.create_date,'yyyyMMdd') ")
		.append("                                     AND r.LOAN_Id = loan.id ")
		.append("          ), 2) end \"transferOut\" ")
		
		.append("      , case when a.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then lt.TRADE_VALUE  ") // 转让金额
		.append("             else TRUNC(a.trade_scale * ( ")
		.append("                  SELECT nvl(sum(CASE WHEN to_char(a.create_date,'yyyyMMdd') = r.EXPECT_REPAYMENT_DATE ")
		.append("                                   AND a.create_date > r.FACT_REPAYMENT_DATE ")
		.append("                                  THEN 0 ELSE r.REPAYMENT_PRINCIPAL END), 0) ")
		.append("                                  FROM BAO_T_REPAYMENT_PLAN_INFO r ")
		.append("                                 WHERE r.EXPECT_REPAYMENT_DATE >= to_char(a.create_date,'yyyyMMdd') ")
		.append("                                   AND r.LOAN_Id = loan.id ")
		.append("        ), 2) end \"tradePrincipal\" ")
		.append("      , lt.TRANSFER_EXPENSES \"transferExpense\" ")// 转出费用
		.append("      , case when a.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then 0 ")
		.append("             else CASE WHEN to_date(receiveInvest.EFFECT_DATE, 'yyyyMMdd') < ld.NEXT_EXPIRY ")
		.append("                        AND to_date(receiveInvest.EFFECT_DATE, 'yyyyMMdd') >= nvl(ld.Last_expiry, trunc(loan.INVEST_START_DATE)) /* (c1)条件包括还款日当天已还款之后购买的的转让 */ ")
		.append("                       THEN TRUNC(lt.TRADE_SCALE /* 转让比例 */ ")
		.append("                               * (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r ")
		.append("                                   where r.EXPECT_REPAYMENT_DATE = to_char(ld.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                     and r.loan_id = loan.id ) ")
		.append("                               * (to_date(receiveInvest.EFFECT_DATE, 'yyyyMMdd') - nvl(ld.Last_expiry, trunc(loan.INVEST_START_DATE))) /* 还款日当天已还款之后的数据（还款可能出现延期一两天），下期没有收益 */ ")
		.append("                               / (ld.NEXT_EXPIRY - nvl(ld.Last_expiry, trunc(loan.INVEST_START_DATE))) ")
		.append("                            , 2) ")
		.append("                       /* ii.还款日(未还款的情况下转让时间<还款计划表实际还款时间) (转让时间-上个还款日-1)/(下个上个还款日)*转让比例*当期本息  */ ")
		.append("                       WHEN to_date(receiveInvest.EFFECT_DATE, 'yyyyMMdd') >= ld.NEXT_EXPIRY /* (c2)当期未还款,如果已还款d.NEXT_EXPIRY变为下期还款日期在上面的when条件中 */ ")
		.append("                       THEN TRUNC(lt.TRADE_SCALE /* 转让比例 */ ")
		.append("                               * (select nvl(sum(r.repayment_interest), 0) /* 当期本息 */ ")
		.append("                                    from bao_t_repayment_plan_info r ")
		.append("                                   where r.EXPECT_REPAYMENT_DATE = to_char(ld.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                     and r.loan_id = loan.id ) ")
		.append("                               * (to_date(receiveInvest.EFFECT_DATE, 'yyyyMMdd') - nvl(ld.Last_expiry, trunc(loan.INVEST_START_DATE)) -1)  /* 还款日当天未还款前转让（还款可能出现延期一两天），让一天利息 */ ")
		.append("                               / (ld.NEXT_EXPIRY - nvl(ld.Last_expiry, trunc(loan.INVEST_START_DATE))) ")
		.append("                            , 2) ")
		.append("                       ELSE 0 END /* 其他时间为零 */")
		.append("        end \"exceptInterest\" ") // 待收收益= 上个还款日到受让人购买日的利息（不含当天）
		.append("      , case when a.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then (lt.TRADE_AMOUNT - lt.TRANSFER_EXPENSES)")
		.append("             else TRUNC(lt.TRADE_PRINCIPAL * a.REDUCED_SCALE, 2) - lt.TRANSFER_EXPENSES ") // 实际到账本金
		.append("                + case when to_date(receiveInvest.EFFECT_DATE,'yyyyMMdd') <= nvl(ld.Last_expiry, trunc(loan.INVEST_START_DATE)) then ")
		.append("                  case when receiveInvest.EFFECT_DATE ")
		.append("                            < (SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r  WHERE r.EXPECT_REPAYMENT_DATE >= receiveInvest.EFFECT_DATE  AND r.loan_id = loan.id) ")/* 购买时间当期的还款日时间 */
		.append("                       THEN TRUNC(lt.TRADE_SCALE ")
		.append("                               * (select nvl(sum(r.repayment_interest), 0)  ")
		.append("                                    from bao_t_repayment_plan_info r ")
		.append("                                   where r.EXPECT_REPAYMENT_DATE = (SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r  WHERE r.EXPECT_REPAYMENT_DATE >= receiveInvest.EFFECT_DATE  AND r.loan_id = loan.id) ")
		.append("                                     and r.loan_id = loan.id ) ")
		.append("                               * (to_date(receiveInvest.EFFECT_DATE, 'yyyyMMdd') - nvl(to_date((SELECT max(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r  WHERE r.EXPECT_REPAYMENT_DATE < receiveInvest.EFFECT_DATE  AND r.loan_id = loan.id), 'yyyyMMdd'), trunc(loan.INVEST_START_DATE))) ")
		.append("                               / (to_date((SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r  WHERE r.EXPECT_REPAYMENT_DATE >= receiveInvest.EFFECT_DATE  AND r.loan_id = loan.id), 'yyyyMMdd') - nvl(to_date((SELECT max(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r  WHERE r.EXPECT_REPAYMENT_DATE < receiveInvest.EFFECT_DATE  AND r.loan_id = loan.id), 'yyyyMMdd'), trunc(loan.INVEST_START_DATE))) ")
		.append("                            , 2) ")
		.append("                       when receiveInvest.EFFECT_DATE ")
		.append("                            = (SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r  WHERE r.EXPECT_REPAYMENT_DATE >= receiveInvest.EFFECT_DATE  AND r.loan_id = loan.id) ")/* 购买时间当期的还款日时间 */
		.append("                        and lt.create_date < nvl((select r.fact_repayment_date from bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE = receiveInvest.EFFECT_DATE  AND r.loan_id = loan.id), lt.create_date +1) ")
		.append("                       THEN TRUNC(lt.TRADE_SCALE  ")
		.append("                               * (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r ")
		.append("                                   where r.EXPECT_REPAYMENT_DATE = (SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r  WHERE r.EXPECT_REPAYMENT_DATE >= receiveInvest.EFFECT_DATE  AND r.loan_id = loan.id) ")
		.append("                                     and r.loan_id = loan.id ) ")
		.append("                               * (to_date(receiveInvest.EFFECT_DATE, 'yyyyMMdd') - nvl(to_date((SELECT max(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r  WHERE r.EXPECT_REPAYMENT_DATE < receiveInvest.EFFECT_DATE  AND r.loan_id = loan.id), 'yyyyMMdd'), trunc(loan.INVEST_START_DATE)) - 1) ")
		.append("                               / (to_date((SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r  WHERE r.EXPECT_REPAYMENT_DATE >= receiveInvest.EFFECT_DATE  AND r.loan_id = loan.id), 'yyyyMMdd') - nvl(to_date((SELECT max(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r  WHERE r.EXPECT_REPAYMENT_DATE < receiveInvest.EFFECT_DATE  AND r.loan_id = loan.id), 'yyyyMMdd'), trunc(loan.INVEST_START_DATE))) ")
		.append("                            , 2) ")
		.append("                       else 0 end ")
		.append("                else 0 end ")
		.append("        end \"factAmount\" ")// 实际到账=折价金额（转出金额*折价比例） - 转让费用 + [上个还款日到受让人购买日的利息（不含当天）](当期不加进去，还款后加进去)
		.append("      , case when a.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then (TRUNC(lt.TRADE_AMOUNT - lt.TRADE_VALUE,2)) else TRUNC(lt.TRADE_PRINCIPAL * a.REDUCED_SCALE, 2) - TRUNC(lt.TRADE_PRINCIPAL, 2) end \"profitAmount\" ") // 盈亏
		.append("      , lt.CREATE_DATE \"tradeDate\" ")
		.append("      , invest.INVEST_MODE \"investMode\" ")
		.append("      , a.protocol_type \"protocolType\" ") 
		.append("      , lt.ID \"transferId\" ") 
		.append("   FROM BAO_T_LOAN_TRANSFER lt ")
		.append("  INNER JOIN BAO_T_WEALTH_HOLD_INFO receive ")
		.append("     ON receive.ID = lt.RECEIVE_HOLD_ID ")
		.append("  INNER JOIN BAO_T_INVEST_INFO receiveInvest ")
		.append("     ON receiveInvest.ID = receive.INVEST_ID ")
		.append("  INNER JOIN BAO_T_WEALTH_HOLD_INFO sender ")
		.append("     ON sender.ID = lt.SENDER_HOLD_ID ")
		.append("  INNER JOIN BAO_T_LOAN_INFO loan ")
		.append("     ON loan.ID = sender.LOAN_ID ")
		.append("  INNER JOIN BAO_T_LOAN_DETAIL_INFO ld ")
		.append("     ON ld.LOAN_ID = loan.ID ")
		.append("  INNER JOIN BAO_T_INVEST_INFO invest ")
		.append("     ON invest.ID = sender.INVEST_ID ")
		.append("  inner join bao_t_loan_transfer_apply a on a.id = lt.transfer_apply_id ")
		.append("  WHERE 1=1 ")
		.append("    AND lt.ID = ? ")
		;
		List<Object> listObject = new ArrayList<Object>();
		listObject.add((String)params.get("onlineTime"));
		listObject.add((String)params.get("onlineTime"));
		listObject.add((String)params.get("onlineTime"));
		listObject.add((String)params.get("onlineTime"));
		listObject.add((String)params.get("onlineTime"));
		
//		Date now = new Date();
//		listObject.add(now);
//		listObject.add(now);
		
		listObject.add(params.get("transferId"));
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
		return list.size() == 0 ? new HashMap<String, Object>() : list.get(0);
	}

	@Override
	public ResultVo queryAllShowTransferList(Map<String, Object> params)
			throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		
		StringBuilder sqlString = new StringBuilder()
		.append("    select a.id                       \"wealthId\",     ")
		.append("           t.LOAN_TITLE               \"lendingType\",    ")
		.append("           t.rebate_ratio             \"rebateRatio\",     ")
		.append("           t.loan_code                \"lendingNo\",     ")
		.append("           TRUNC(a.TRADE_SCALE*a.REDUCED_SCALE   ")
		.append(" 		                  * decode(nvl(r.repayment_status, '未还款'),'未还款'  ")
		.append(" 		                    , cr.value_repayment_before  ")
		.append(" 		                    , cr.value_repayment_after)  ")
		.append(" 		               , 2)             \"planTotalAmount\",     ")
		.append("            t.invest_min_amount        \"investMinAmount\",     ")
		.append("            a.trade_amount  \"alreadyInvestAmount\",         ")
		.append("            (1-a.remainder_trade_scale)/a.trade_scale \"investScale\",     ")
		.append("            ceil(months_between(t.invest_end_date, ?)) \"typeTerm\",     ")
		.append("            t.LOAN_UNIT               \"typeUnit\",     ")
		.append("            s.year_irr                \"yearRate\",     ")
		.append("            0                         \"awardRate\",     ")
		.append("            t.repayment_method        \"incomeType\",     ")
		.append("            a.transfer_start_date     \"releaseDate\",     ")
		.append("            null                      \"effectDate\",     ")
		.append("            a.transfer_end_date       \"endDate\",     ")
		.append("            t.loan_status             \"wealthStatus\",   ")
		.append("            TRUNC(a.remainder_trade_scale*a.REDUCED_SCALE   ")
		.append("                   * decode(nvl(r.repayment_status, '未还款'),'未还款'  ")
		.append("                     , cr.value_repayment_before  ")
		.append("                     , cr.value_repayment_after)  ")
		.append("                , 2)                  \"currUsableValue\"      ")
		.append("    from bao_t_loan_transfer_apply a ")
		.append("    inner join bao_t_wealth_hold_info h on h.id = a.sender_hold_id ")
		.append("    inner join bao_t_loan_info t on t.id = h.loan_id ")
		.append("    inner join bao_t_loan_detail_info s on s.loan_id = t.id  ")
		.append("    inner join bao_t_credit_right_value cr on cr.loan_id = t.id and cr.value_date = ? ")
		.append("    left join bao_t_repayment_plan_info r on r.loan_id = t.id and r.expect_repayment_date = ? ")
		.append("    where t.LOAN_STATUS in ( '募集中','满标复核','正常','逾期','提前结清','已到期','流标 ') ");
		
		Date now = new Date();
		List<Object> objList = Lists.newArrayList();
		objList.add(now);
		objList.add(DateUtils.formatDate(now, "yyyyMMdd"));
		objList.add(DateUtils.formatDate(now, "yyyyMMdd"));
		
		SqlCondition sql = new SqlCondition(sqlString, params, objList);
		sql.addList("ids", "a.id");
		sql.addSql("  order by a.TRANSFER_END_DATE, a.REMAINDER_TRADE_SCALE");
	
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), sql.toArray());
		result.put("iTotalDisplayRecords", list.size());
		result.put("data", list);
		return new ResultVo(true, "查询成功", result);
	}

	@Override
	public Map<String, Object> queryTransferContract(Map<String, Object> params)
			throws SLException {
		
		StringBuilder sqlString = new StringBuilder()
		.append("   select t.id \"transferId\", ")
		.append("          t.trade_no \"transferCode\", ")
		.append("          s.cust_name \"senderCustName\", ")
		.append("          s.credentials_code \"senderCredentialsCode\", ")
		.append("          r.cust_name \"receiverCustName\", ")
		.append("          r.credentials_code \"receiverCredentialsCode\", ")
		.append("          l.loan_code \"loanNo\", ")
		.append("          l.loan_title \"loanTitle\", ")
		.append("          d.year_irr \"yearRate\", ")
		.append("          ceil(months_between(l.invest_end_date, t.create_date)) \"remainTermMonth\", ")
//		.append("          trunc((select sum(p.repayment_principal)  ")
//		.append("           from bao_t_repayment_plan_info p  ")
//		.append("           where p.loan_id = t.sender_loan_id  ")
//		.append("           and p.expect_repayment_date > to_char(t.create_date, 'yyyyMMdd') ")
//		.append("          )*t.trade_scale, 2) \"remainPrincipal\", ")
//		.append("          trunc((select sum(p.repayment_interest)  ")
//		.append("           from bao_t_repayment_plan_info p  ")
//		.append("           where p.loan_id = t.sender_loan_id  ")
//		.append("           and p.expect_repayment_date > to_char(t.create_date, 'yyyyMMdd') ")
//		.append("          )*t.trade_scale, 2) \"remainInterest\", ")
//		.append("          trunc(t.trade_amount,2) \"transferAmout\", ")
		
		.append("          case when a.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then 'old' else 'new' end \"oldFlag\", ")
		.append("          case when a.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') ")
		.append("               then trunc((select sum(p.repayment_principal)  ")
		.append("                             from bao_t_repayment_plan_info p  ")
		.append("                            where p.loan_id = t.sender_loan_id  ")
		.append("                              and p.expect_repayment_date > to_char(t.create_date, 'yyyyMMdd') ")
		.append("                          )*t.trade_scale, 2) ")
		.append("               else TRUNC_AMOUNT_WEB(i.invest_amount/a.reduced_scale) end \"remainPrincipal\",")// 待收本金
		
		.append("          case when a.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') ")
		.append("               then trunc(t.trade_amount,2)  ")
		.append("               else TRUNC_AMOUNT_WEB(i.invest_amount/a.reduced_scale) end \"transferAmout\", ")// 转让债权价值
		
		.append("          i.invest_amount \"payAmount\", ")// 债权转让价格、支付金额
		.append("          case when a.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') ")
		.append("               then trunc((select sum(p.repayment_interest)  ")
		.append("                             from bao_t_repayment_plan_info p  ")
		.append("                            where p.loan_id = t.sender_loan_id  ")
		.append("                              and p.expect_repayment_date > to_char(t.create_date, 'yyyyMMdd') ")
		.append("                          )*t.trade_scale, 2) else ")
		.append("                  CASE WHEN i.effect_date /* ba.非还款日当天购买(下个还款日-转让时间)/(下个还款日-上个还款日）*持有比例*当期本息 */ ")
		.append("                            < (SELECT min(r.EXPECT_REPAYMENT_DATE) /* 购买时间当期的还款日时间 */ ")
		.append("                                 FROM bao_t_repayment_plan_info r  ")
		.append("                                WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date ")
		.append("                                  AND r.loan_id = l.id) ")
		.append("                       THEN trunc(wh.hold_scale * (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                     from bao_t_repayment_plan_info r  ")
		.append("                                    where r.EXPECT_REPAYMENT_DATE = (SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date AND r.loan_id = l.id) ")
		.append("                                      and r.loan_id = l.id )  ")
		.append("                                * ((SELECT to_date(min(r.EXPECT_REPAYMENT_DATE),'yyyyMMdd') FROM bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date AND r.loan_id = l.id) ")
		.append("                                    - TRUNC(to_date(i.effect_date, 'yyyyMMdd'))) ")
		.append("                                / ((SELECT to_date(min(r.EXPECT_REPAYMENT_DATE),'yyyyMMdd') FROM bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date AND r.loan_id = l.id) ")
		.append("                                   - nvl((SELECT to_date(max(r.EXPECT_REPAYMENT_DATE), 'yyyyMMdd') FROM bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE < i.effect_date AND r.loan_id = l.id), trunc(l.INVEST_START_DATE))) ")
		.append("                            , 2) ")
		.append("                          + trunc(wh.hold_scale *  ")
		.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r  ")
		.append("                                   where r.EXPECT_REPAYMENT_DATE > (SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date AND r.loan_id = l.id) ")
		.append("                                     and r.loan_id = l.id ) ")
		.append("                            , 2) ")
		.append("                       WHEN i.effect_date /* bb.还款日当天购买(未还款的情况下)1/(下个还款日-上个还款日)*持有比例*当期本息  */ ")
		.append("                            = (SELECT min(r.EXPECT_REPAYMENT_DATE) /* 购买时间当期的还款日时间 */ ")
		.append("                                FROM bao_t_repayment_plan_info r  ")
		.append("                               WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date ")
		.append("                                 AND r.loan_id = l.id) ")
		.append("                            AND i.create_date < nvl((SELECT r.FACT_REPAYMENT_DATE ")
		.append("                                            FROM bao_t_repayment_plan_info r  ")
		.append("                                           WHERE r.EXPECT_REPAYMENT_DATE = (SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date AND r.loan_id = l.id) ")
		.append("                                             AND r.loan_id = l.id), i.create_date + 1) ")
		.append("                       THEN trunc(wh.hold_scale * (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                     from bao_t_repayment_plan_info r  ")
		.append("                                    where r.EXPECT_REPAYMENT_DATE = (SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date AND r.loan_id = l.id) ")
		.append("                                      and r.loan_id = l.id )  ")
		.append("                                  * 1 ")
		.append("                                / ((SELECT to_date(min(r.EXPECT_REPAYMENT_DATE),'yyyyMMdd') FROM bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date AND r.loan_id = l.id) ")
		.append("                                   - nvl((SELECT to_date(max(r.EXPECT_REPAYMENT_DATE), 'yyyyMMdd') FROM bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE < i.effect_date AND r.loan_id = l.id), trunc(l.INVEST_START_DATE))) ")
		.append("                            , 2) ")
		.append("                          + trunc(wh.hold_scale *  ")
		.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r  ")
		.append("                                   where r.EXPECT_REPAYMENT_DATE > (SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date AND r.loan_id = l.id) ")
		.append("                                     and r.loan_id = l.id ) ")
		.append("                            , 2) ")
		.append("                       ELSE trunc(wh.hold_scale *  ")
		.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r  ")
		.append("                                   where r.EXPECT_REPAYMENT_DATE > (SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date AND r.loan_id = l.id) ")
		.append("                                     and r.loan_id = l.id)  ")
		.append("                            , 2) END ")
		.append("               END \"remainInterest\", ")//待收收益
		
		.append("          trunc(t.transfer_expenses,2) \"transferExpense\", ")
		.append("          t.create_date \"transferDate\", ")
		.append("          a.protocol_type \"protocolType\", ")
		.append("          (select count(1) from bao_t_repayment_plan_info rp where rp.loan_id = t.sender_loan_id and rp.expect_repayment_date > to_char(t.create_date, 'yyyyMMdd') ) \"remainTerm\" ")
		.append("   from bao_t_loan_transfer t ")
		.append("   INNER JOIN BAO_T_WEALTH_HOLD_INFO wh ON wh.ID = t.RECEIVE_HOLD_ID ")
		.append("   INNER JOIN BAO_T_INVEST_INFO i ON i.ID = wh.INVEST_ID ")
		.append("   inner join bao_t_cust_info s on s.id = t.sender_cust_id ")
		.append("   inner join bao_t_cust_info r on r.id = t.receive_cust_id ")
		.append("   inner join bao_t_loan_info l on l.id = t.sender_loan_id ")
		.append("   inner join bao_t_loan_detail_info d on d.loan_id = l.id ")
		.append("   inner join bao_t_loan_transfer_apply a on a.id = t.transfer_apply_id ")
		.append("   where t.id = ? ");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[]{params.get("onlineTime"),params.get("onlineTime"),params.get("onlineTime"),params.get("onlineTime"),params.get("transferId")});
		
		return list.size() == 0 ? new HashMap<String, Object>() : list.get(0);
	}

	@Override
	public Map<String, Object> queryFinancingContract(
			Map<String, Object> params) throws SLException {
		StringBuilder sqlString = new StringBuilder()
		.append(" SELECT invest.ID \"investId\" ")
		.append(" 	, invest.CUST_ID \"custId\" ")
		.append(" 	, invest.LOAN_ID \"loanId\" ")
		.append(" 	, loan.LOAN_CODE \"loanCode\" ")
		.append(" 	, cust.CUST_NAME \"receiverCustName\" ")
		.append(" 	, cust.CREDENTIALS_CODE \"receiverCredentialsCode\" ")
		.append(" 	, loan.SERVICE_NAME \"senderCustName\" ")
		.append(" 	, loan.SERVICE_CODE \"senderCredentialsCode\" ")
		.append(" 	, loan.INVEST_START_DATE \"effectDate\" ")
		.append(" 	, loan.INVEST_END_DATE \"endDate\" ")
		.append(" 	, loan.SOURCE_CONTRACT_NAME \"sourceContractName\" ")
		.append(" 	, loan.SOURCE_CONTRACT_CODE \"sourceContractCode\" ")
		.append(" 	, loan.SOURCE_LOAN_USER \"sourceLoanUser\" ")
		.append(" 	, invest.INVEST_AMOUNT \"investAmount\" ")
		.append(" 	, ld.YEAR_IRR \"yearRate\" ")
		.append(" 	, loan.LOAN_TERM \"typeTerm\" ")
		.append(" 	, loan.REPAYMENT_METHOD \"repaymentMethod\" ")
		.append(" 	, investD.TRADE_NO \"investCode\" ")
		.append("   , loan.PROTOCAL_TYPE \"protocolType\" ")
		.append("   FROM BAO_T_LOAN_INFO loan ")
		.append("    , BAO_T_LOAN_DETAIL_INFO ld ")
		.append("    , BAO_T_INVEST_INFO invest ")
		.append("    , BAO_T_INVEST_DETAIL_INFO investD ")
		.append("    , BAO_T_CUST_INFO cust ")
		.append("  WHERE ld.LOAN_ID = loan.ID ")
		.append("    AND loan.ID = invest.LOAN_ID ")
		.append("    AND cust.ID = invest.CUST_ID ")
		.append("    AND investD.INVEST_ID = invest.ID ")
		.append("    AND invest.ID = ? ")
		;
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[]{params.get("investId")});
		return list.size() == 0 ? new HashMap<String, Object>() : list.get(0);
	}
	
	@Override
	public Map<String, Object> queryFingertipContract(
			Map<String, Object> params) throws SLException {
		StringBuilder sqlString = new StringBuilder()
		.append("  SELECT invest.ID \"investId\"  ")
		.append("  	, invest.CUST_ID \"custId\"  ")
		.append("  	, invest.LOAN_ID \"loanId\"  ")
		.append("  	, loan.LOAN_CODE \"loanCode\"  ")
		.append("  	, loan.LOAN_AMOUNT \"loanAmount\" ")
		.append(" 	, 0 \"waitingIncome\" ")
		.append(" 	, ld.YEAR_IRR \"yearRate\"  ")
		.append(" 	, loan.LOAN_TERM \"typeTerm\"  ")
		.append(" 	, loan.LOAN_UNIT \"loanUnit\"  ")
		.append(" 	, loan.REPAYMENT_METHOD \"incomeType\"  ")
		.append("  	, loan.INVEST_START_DATE \"effectDate\"  ")
		.append("  	, loan.INVEST_END_DATE \"endDate\"  ")
		.append("     , loan.PROTOCAL_TYPE \"protocolType\" ")
		.append("    FROM BAO_T_LOAN_INFO loan  ")
		.append("     , BAO_T_LOAN_DETAIL_INFO ld  ")
		.append("     , BAO_T_INVEST_INFO invest  ")
		.append("     , BAO_T_INVEST_DETAIL_INFO investD  ")
		.append("   WHERE ld.LOAN_ID = loan.ID  ")
		.append("     AND loan.ID = invest.LOAN_ID  ")
		.append("     AND investD.INVEST_ID = invest.ID  ")
		.append("     AND invest.ID = ?  ")
		;
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[]{params.get("investId")});
		return list.size() == 0 ? new HashMap<String, Object>() : list.get(0);
	}

	@Override
	public List<Map<String, Object>> queryMyCreditListForJob(
			Map<String, Object> params) throws SLException {
	    SqlCondition sqlCon=queryMyCreditListSql(params);
		Date date = DateUtils.truncateDate(new Date());
        StringBuffer strBuff=new StringBuffer(sqlCon.toString());
        List<Object> args=sqlCon.getObjectList();
		if(!StringUtils.isEmpty(params.get("limitedTerm"))){
			strBuff.append(" and i.effect_date <= to_char(? - ?, 'yyyyMMdd') ");
			int days = Integer.parseInt(params.get("limitedTerm").toString())*30;
			args.add(date);
			args.add(days);
		}
//		if (!StringUtils.isEmpty(params.get("minTerm"))) {
//			strBuff.append(" and l.loan_term >= ? ");
//			args.add(params.get("minTerm"));
//		}
//		if (!StringUtils.isEmpty(params.get("maxTerm"))) {
//			strBuff.append(" and l.loan_term <= ? ");
//			args.add(params.get("maxTerm"));
//		}
		
		if(!StringUtils.isEmpty(params.get("minTerm"))){
			strBuff.append(" and l.LOAN_TERM*decode(l.LOAN_UNIT, '天',1,'月',30,1) >= ? ");
			int days = Integer.parseInt(params.get("minTerm").toString())*30;
			args.add(days);
		}
		if(!StringUtils.isEmpty(params.get("maxTerm"))){
			strBuff.append(" and l.LOAN_TERM*decode(l.LOAN_UNIT, '天',1,'月',30,1) <= ? ");
			int days = Integer.parseInt(params.get("maxTerm").toString())*30;
			args.add(days);
		}
		if (!StringUtils.isEmpty(params.get("minYearRate"))) {
			strBuff.append(" and d.YEAR_IRR >= ? ");
			args.add(params.get("minYearRate"));
		}
		if (!StringUtils.isEmpty(params.get("maxYearRate"))) {
			strBuff.append(" and d.YEAR_IRR <= ? ");
			args.add(params.get("maxYearRate"));
		}

		if (!StringUtils.isEmpty(params.get("repaymentMethod"))) {
			String repaymentMethodSplit = (String) params.get("repaymentMethod");
			String[] repaymentMethodList = repaymentMethodSplit.split(",");
			strBuff.append(" and ( ");
			for (int i = 0; i < repaymentMethodList.length; i++) {
				if (i == 0) {
					strBuff.append(" l.repayment_method = ? ");
				} else {
					strBuff.append(" OR l.repayment_method = ? ");
				}
				args.add(repaymentMethodList[i]);
			}
			strBuff.append(" ) ");
		}
		if (!StringUtils.isEmpty(params.get("canTransferProduct"))) {
			String canTransferProductSplit = (String) params.get("canTransferProduct");
			String[] canTransferProductList = canTransferProductSplit.split(",");
			strBuff.append(" and ( ");
			for (int i = 0; i < canTransferProductList.length; i++) {
				String canTransfer = canTransferProductList[i];
				if ("普通标的".equals(canTransfer)) {
					canTransfer = "加入";
				} else if ("转让标的".equals(canTransfer)) {
					canTransfer = "转让";
				}
				if (i == 0) {
					strBuff.append(" i.invest_mode = ? ");
				} else {
					strBuff.append(" OR i.invest_mode = ? ");
				}
				args.add(canTransfer);
			}
			strBuff.append(" ) ");
		}
		strBuff.append(" and (apply.audit_status!='待审核' or apply.audit_status is null) ");
		strBuff.append(" ORDER BY i.create_date desc ");
		return repositoryUtil.queryForMap(strBuff.toString(), args.toArray());
	}

	@Override
	public List<Map<String, Object>> queryWaitingAdvanceList(
			Map<String, Object> params) throws SLException {
		StringBuilder sql = new StringBuilder()
		.append(" select h.id \"holdId\", i.cust_id \"custId\" ")
		.append(" from bao_t_wealth_hold_info h, bao_t_invest_info i ")
		.append(" where h.invest_id = i.id ")
		.append(" and i.cust_id = ? and i.effect_date = ? ");
		
		
		return repositoryUtil.queryForMap(sql.toString(), new Object[] {params.get("custId").toString(), params.get("effectDate").toString()});
	}

	@Override
	public boolean batchModifyReceiveStaus(List<String> loanCodes,boolean receiveFlag,String userId) {

		for (String loanCode : loanCodes) {
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanCode);
			//传true,领取,false取消领取
			if(!receiveFlag&&loanInfoEntity.getLoanStatus().equals("待审核")){
				loanInfoEntity.setReceiveStatus(Constant.RECEIVE_STATUS_02);
				loanInfoEntity.setReceiveUser("");
			}else if(receiveFlag&&loanInfoEntity.getLoanStatus().equals("待审核")){
				loanInfoEntity.setReceiveStatus(Constant.RECEIVE_STATUS_01);
				loanInfoEntity.setReceiveUser(userId);
			}else{
				return false;
			}
			loanInfoRepository.save(loanInfoEntity);
		}
		
		return true;
	}

	@Override
	public Map<String, Object> queryLoanInfoByApplyId(String applyId) {
		StringBuffer sql = new StringBuffer()
		.append(" select ")
		.append("       loan.invest_end_date - trunc(sysdate)   \"loanTerm\", ")
		.append("       '天' \"loanUnit\", ")
		.append("       ld.year_irr \"yearRate\", ")
		.append("       loan.repayment_method \"repaymentMethod\", ")
		.append("       loan.seat_term \"seatTerm\" ")
		.append("   FROM BAO_T_LOAN_TRANSFER_APPLY lta, ")
		.append("        BAO_T_WEALTH_HOLD_INFO wh, ")
		.append("        BAO_T_LOAN_INFO loan, ")
		.append("        BAO_T_LOAN_DETAIL_INFO ld ")
		.append("  WHERE ld.LOAN_ID = loan.ID ")
		.append("    AND loan.ID = wh.LOAN_ID ")
		.append("    AND wh.ID = lta.SENDER_HOLD_ID ")
		.append("    and lta.id = ? ");
		
		List<Object> args = new ArrayList<Object>();
		args.add(applyId);
		List<Map<String, Object>> result = repositoryUtil.queryForMap(sql.toString(), args.toArray());
		return result.size()==0 ? new HashMap<String, Object>():result.get(0);
	}

	public Map<String, Object> queryAmoutInfo(Map<String, Object> param) {
		List<Object> listObject = new ArrayList<Object>();
		Map<String, Object> result = Maps.newHashMap();
		String paramEntity=paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_05).getValue();
		StringBuilder sql = new StringBuilder()
				.append("  select nvl(SUM(T.RESERVE_AMOUNT), 0) \"amount\" ")//预约总额
				.append("    from BAO_T_RESERVE_INVEST_INFO T ")
			    .append("    where  t.reserve_status in ('预约成功','排队中') ")
				.append("  union all ")
				.append("  select nvl(sum(i.invest_amount), 0) \"amount\" ")//募集成功总额
				.append("    from bao_t_loan_info loan ")
				.append("   inner join bao_t_invest_info i ")
				.append("      on i.loan_id = loan.id ")
				.append("      and loan.loan_unit='天'   ")
//				.append("   inner join BAO_T_RESERVE_INVEST_INFO r ")
//				.append("      on i.product_id = r.id ")
//				.append("     and i.cust_id = r.cust_id ")
//				.append("     and r.reserve_status ='预约成功' ")
				.append("     and i.product_id is not null ")
				.append("     AND loan.LOAN_STATUS in ('已到期', '正常') ")
				.append("     where 1=0  ");
		if(!StringUtils.isEmpty((paramEntity))){
			String[] paramEntityValue = paramEntity.split(",");
			for (int i = 0; i < paramEntityValue.length; i++) {
				sql.append("or loan.loan_type = ? ");
				listObject.add(paramEntityValue[i]);
		}}
				sql.append("  union all ")
				.append("  select nvl(sum(loan.loan_amount), 0) \"amount\" ")//资产端推送总额
				.append("    from bao_t_loan_info loan ")
				.append("   where loan.LOAN_STATUS in ('募集中','满标复核','正常','提前结清','已到期') ");
				
		if (!StringUtils.isEmpty((paramEntity))) {
			String[] paramEntityValue = paramEntity.split(",");
			sql.append(" and ( ");
			for (int i = 0; i < paramEntityValue.length; i++) {
				if (i != 0) {
					sql.append(" OR ");
				}
				sql.append(" loan.loan_type = ? ");

				listObject.add(paramEntityValue[i]);
			}
			sql.append(" ) ");
		}
				
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
		result.put("investCustReserveAmount",ArithUtil.sub(new BigDecimal(list.get(0).get("amount").toString()), new BigDecimal(list.get(1).get("amount").toString())));
		result.put("untreatedAmount",ArithUtil.sub(new BigDecimal(list.get(2).get("amount").toString()), new BigDecimal(list.get(1).get("amount").toString())));
		result.put("raiseSuccess", list.get(1).get("amount"));

		return result;
	}


	@Override
	public Map<String, Object> queryEarnTotalPrincipal(
			Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		StringBuilder sql = new StringBuilder()
		.append(" select TRUNC_AMOUNT_WEB(sum(rd.trade_amount)) \"amount\" ")//0
		.append(" from bao_t_account_flow_info     f, ")
		.append(" bao_t_payment_record_info   pr, ")
		.append(" bao_t_payment_record_detail rd, ")
		.append(" bao_t_invest_info           i ")
		.append(" where f.relate_primary = i.id ")
		.append(" and f.id = pr.relate_primary ")
		.append(" and pr.id = rd.pay_record_id ")
		.append("  and rd.subject_type = '本金' ")
		.append(" and i.loan_id is not null ")
		.append("  and i.cust_id = ? ")
		.append(" union all ")
		.append(" select nvl(TRUNC_AMOUNT_WEB(sum(lt.trade_principal * lta.reduced_scale * lta.deviation_scale)),0) \"amount\" ")//1
		.append("  from bao_t_loan_transfer             lt ")
		.append(" right join bao_t_loan_transfer_apply  lta ")
		.append(" on lt.transfer_apply_id = lta.id ")
		.append(" right join (select distinct i.transfer_apply_id applyId ")
		.append(" from bao_t_account_flow_info t ")
		.append(" left join bao_t_invest_info i ")
		.append(" on i.id = t.relate_primary ")
		.append(" where t.trade_type = '购买债权转让' ")
		.append(" and t.account_type = '总账' ")
		.append(" and t.bankroll_flow_direction = '收入' ")
		.append(" and i.loan_id is not null ")
		.append(" and t.cust_id = ? )   apply  ")
		.append(" on lta.id = apply.applyid ")
		.append(" union all ")
		.append(" select TRUNC_AMOUNT_WEB(sum(rd.trade_amount)) \"amount\" ")//2
		.append(" from bao_t_account_flow_info     f, ")
		.append(" bao_t_payment_record_info       pr, ")
		.append(" bao_t_payment_record_detail     rd,  ")
		.append(" bao_t_invest_info               i ")
		.append(" where f.relate_primary = i.id ")
		.append(" and f.id = pr.relate_primary ")
		.append(" and pr.id = rd.pay_record_id ")
		.append(" and rd.subject_type in ('还风险金逾期费用', '利息', '逾期费用', '违约金') ")
		.append(" and i.loan_id is not null ")
		.append(" and i.cust_id = ? ")
		.append(" union all ")
		.append(" select nvl(TRUNC_AMOUNT_WEB(sum(lt.trade_amount-lt.trade_principal * lta.reduced_scale * lta.deviation_scale	)),0) \"amount\" ")//3
		.append(" from bao_t_loan_transfer      lt ")
		.append(" right join bao_t_loan_transfer_apply    lta ")
		.append("  on lt.transfer_apply_id = lta.id ")
		.append(" right join (select distinct i.transfer_apply_id applyId  ")
		.append(" from bao_t_account_flow_info   t ")
		.append(" left join bao_t_invest_info     i  ")
		.append(" on i.id = t.relate_primary   ")
		.append(" where t.trade_type = '购买债权转让' ")
		.append(" and t.account_type = '总账'  ")
		.append(" and t.bankroll_flow_direction = '收入' ")
		.append(" and i.loan_id is not null  ")
		.append(" and t.cust_id = ? )     apply ")
		.append(" on lta.id = apply.applyid   ")
		.append(" where lt.create_date<to_date(?,'yyyy-MM-dd HH24:mi:ss') ")
		.append(" union all ")
		.append(" select TRUNC_AMOUNT_WEB(nvl(sum(r.award_amount * w.hold_scale), 0)) \"amount\" ")//4
		.append(" from bao_t_invest_info i ")
		.append(" right join bao_t_wealth_hold_info w ")
		.append(" on i.id=w.invest_id ")
		.append("  right join bao_t_loan_info t ")
		.append(" on w.loan_id = t.id ")
		.append(" right join bao_t_repayment_plan_info r ")
		.append(" on r.loan_id = t.id ")
		.append(" where t.loan_type = '橙信贷' ")
		.append(" and r.repayment_status = '未还款' ")
		.append(" and i.cust_id =? ")
		.append(" union all ")
		.append(" select TRUNC_AMOUNT_WEB(nvl(sum(r.award_amount * w.hold_scale), 0)) \"amount\" ")//5
		.append(" from bao_t_invest_info i ")
		.append(" right join bao_t_wealth_hold_info w ")
		.append(" on i.id=w.invest_id ")
		.append("  right join bao_t_loan_info t ")
		.append(" on w.loan_id = t.id ")
		.append(" right join bao_t_repayment_plan_info r ")
		.append(" on r.loan_id = t.id ")
		.append(" where t.loan_type = '橙信贷' ")
		.append(" and r.repayment_status = '已还款' ")
		.append(" and i.cust_id =? ")
		.append(" union all ")
		.append(" select nvl(TRUNC_AMOUNT_WEB(sum(t.award_amount)), 0) \"amount\" ")//6
		.append("   from bao_t_invest_info i ")
		.append("  right join bao_t_purchase_award t ")
		.append("     on i.id = t.invest_id ")
		.append("  where t.award_status = '未结清' ")
		.append(" and i.cust_id =? ")
		.append(" union all ")
		.append(" select nvl(TRUNC_AMOUNT_WEB(sum(t.award_amount)), 0) \"amount\" ")//7
		.append("   from  bao_t_purchase_award t ")
		.append("  right join bao_t_invest_info i ")
		.append("     on i.id = t.invest_id ")
		.append("  where t.award_status = '已结清' ")
		.append(" and i.cust_id =? ");

		
		List<Object> objectList = new ArrayList<Object>();
		objectList.add(params.get("custId"));
		objectList.add(params.get("custId"));
		objectList.add(params.get("custId"));
		objectList.add(params.get("custId"));
		objectList.add(params.get("onlineTime"));
		objectList.add(params.get("custId"));
		objectList.add(params.get("custId"));
		objectList.add(params.get("custId"));
		objectList.add(params.get("custId"));
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), objectList.toArray());
		if(null == list || list.size() == 0) {
			return result;
		}
		result.put("earnTotalPrincipal", ArithUtil.add(new BigDecimal( list.get(0).get("amount").toString()),new BigDecimal(list.get(1).get("amount").toString())));
		result.put("earnTotalAmount", ArithUtil.add(new BigDecimal( list.get(2).get("amount").toString()),new BigDecimal(list.get(3).get("amount").toString())));
		result.put("exceptTotalAward",list.get(4).get("amount"));
		result.put("earnTotalAward", list.get(5).get("amount"));
		result.put("exceptTotalIncreaseInterest", list.get(6).get("amount"));
		result.put("earnTotalIncreaseInterest", list.get(7).get("amount"));
		return result;
	}

	@Override
	public Map<String, Object> queryEarnAndExceptTotalExperience(
			Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		StringBuilder sql = new StringBuilder()
//		.append(" select TRUNC_AMOUNT_WEB(sum(i.invest_amount* l.loan_term*d.year_irr/360)) \"amount\" ")
//		.append(" from bao_t_invest_info i ")
//		.append(" right join bao_t_loan_info l  ")
//		.append(" on i.loan_id = l.id ")
//		.append(" inner join bao_t_loan_detail_info d ")
//		.append(" on d.loan_id = l.id ")
//		.append(" where l.newer_flag = '体验标' ")
//		.append(" and i.invest_status = '已到期' ")
//		.append(" and i.cust_id=? ")
		.append("  select TRUNC_AMOUNT_WEB(nvl(sum(t.trade_amount), 0)) \"amount\"  ")
		.append(" from bao_t_account_flow_info t  ")
		.append(" where t.account_type = '总账' ")
		.append(" and t.trade_type = '活动金额提现' ")
		.append(" and t.cust_id = ? ")
		.append(" union all ")
		.append(" select TRUNC_AMOUNT_WEB(sum(i.invest_amount* l.loan_term*d.year_irr/360)) \"amount\" ")
		.append(" from bao_t_invest_info i ")
		.append(" right join bao_t_loan_info l  ")
		.append(" on i.loan_id = l.id ")
		.append(" inner join bao_t_loan_detail_info d ")
		.append(" on d.loan_id = l.id ")
		.append(" where l.newer_flag = '体验标' ")
		.append(" and i.invest_status = '收益中' ")
		.append(" and i.cust_id=? ");
		List<Object> objectList = new ArrayList<Object>();
		objectList.add(params.get("custId"));
		objectList.add(params.get("custId"));
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), objectList.toArray());
		if(null == list || list.size() == 0) {
			return result;
		}
		result.put("earnTotalExperience", list.get(0).get("amount"));
		result.put("exceptTotalExperience", list.get(1).get("amount"));
		return result;
	}
	
	@Override
	public List<String> findListReserveLoan() {
		String paramEntity=paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_05).getValue();
		String loanValue = "'" + paramEntity.replaceAll(",", "','") + "'";
		
		StringBuilder sql=new StringBuilder()
		.append(" select loan.id ")
		.append(" from BAO_T_LOAN_INFO loan ") 
		.append(" WHERE 1=1 ") 
		.append("   AND loan.LOAN_TERM IN ('7', '14' ,'21', '28', '60') ") 
		.append("   AND loan.LOAN_UNIT = '天' ") 
		.append("   AND loan.LOAN_TYPE in ("+loanValue+") ") 
//		.append("   AND loan.LOAN_TYPE) FROM COM_T_PARAM WHERE TYPE='shanLinCaiFuSet' AND PARAMETER_NAME='可投产品') > 0 ") 
		.append("   AND loan.NEWER_FLAG = '普通标' ") 
		.append("   AND loan.LOAN_STATUS = '募集中' ") 
		.append(" order by loan.create_date ");
		return jdbcTemplate.queryForList(sql.toString(), String.class);
	}
	
	@Override
	public String findReserveLoan(String loanId) {
		String paramEntity=paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_05).getValue();
		String loanValue = "'" + paramEntity.replaceAll(",", "','") + "'";
		
		StringBuilder sql=new StringBuilder()
		.append(" select loan.id \"id\" ")
		.append(" from BAO_T_LOAN_INFO loan ")
		.append(" WHERE 1=1 ")
		.append("   AND loan.LOAN_TERM IN ('7', '14' ,'21', '28', '60') ")
		.append("   AND loan.LOAN_UNIT = '天' ")
		.append("   AND loan.LOAN_TYPE in ("+loanValue+ ") ")
		.append("   AND loan.NEWER_FLAG = '普通标' ")
		.append("   AND loan.LOAN_STATUS = '募集中' ")
		.append("   and loan.id = ?  ");
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), new Object[]{loanId});
		if(list != null && list.size()>0){
			return (String) list.get(0).get("id");
		}
		return null;
	}
	
	@Override
	public String findInfoToAutoInvest(String loanId) {
		String paramEntity=paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_05).getValue();
		String loanValue = "'" + paramEntity.replaceAll(",", "','") + "'";
		
		StringBuilder sql=new StringBuilder()
		.append(" select loan.id \"id\" ")
		.append(" from BAO_T_LOAN_INFO loan ")
		.append("    , BAO_T_LOAN_DETAIL_INFO ld  ")
		.append(" WHERE 1=1 ")
		.append("   AND ld.YEAR_IRR >= 0.05 ")
		.append("   AND ld.YEAR_IRR <= 0.06 ")
		.append("   AND ld.Loan_id = loan.id  ")
		.append("   AND loan.LOAN_TERM IN ('7', '14' ,'21', '28', '60') ")
		.append("   AND loan.LOAN_UNIT = '天' ")
		.append("   AND loan.SEAT_TERM = -1 ")
		.append("   AND loan.REPAYMENT_METHOD = '到期还本付息' ")
		.append("   AND loan.LOAN_TYPE in ("+loanValue+") ")
//		.append("   AND (SELECT instr(value, loan.LOAN_TYPE) FROM COM_T_PARAM WHERE TYPE='shanLinCaiFuSet' AND PARAMETER_NAME='可投产品') > 0 ")
		.append("   AND loan.NEWER_FLAG = '普通标' ")
		.append("   AND loan.LOAN_STATUS = '募集中' ")
		.append("   and loan.id = ?  ");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), new Object[]{loanId});
		if(list != null && list.size()>0){
			return (String) list.get(0).get("id");
		}
		return null;
	}
}
