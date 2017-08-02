package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.entity.CustActivityDetailEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.TransAccountInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRepository;
import com.slfinance.shanlincaifu.repository.CustActivityDetailEntityRepository;
import com.slfinance.shanlincaifu.repository.CustActivityInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.TransAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.UserRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.AccountFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.TransAccountInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.TransAccountInfoService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.ParamsNameConstant;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

/**
 * 
 * @author zhumin
 *
 */
@Repository
public class TransAccountInfoServiceImpl implements TransAccountInfoService {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private RepositoryUtil repositoryUtil;

	@Autowired
	private TransAccountInfoRepository transAccountInfoRepository;

	@Autowired
	private TransAccountInfoRepositoryCustom transAccountInfoRepositoryCustom;
	@Autowired
	private AccountFlowInfoRepositoryCustom accountFlowInfoRepositoryCustom;
	@Autowired
	private AccountFlowInfoRepository accountFlowInfoRepository;
//	@Autowired
//	private AccountFlowDetailRepository accountFlowDetailRepository;

	@Autowired
	private AccountInfoRepository accountInfoRepository;
	@Autowired
	FlowNumberService numberService;
	@Autowired
	AuditInfoRepository auditInfoRepository;

	@Autowired
	private CustInfoRepository custInfoRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CustActivityInfoRepository custActivityInfoRepository;
//	@Autowired
//	private FlowBusiRelationRepository flowBusiRelationRepository;
	@Autowired
	private CustActivityDetailEntityRepository custActivityDetailEntityRepository;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;

	@Override
	public boolean batchImport(final List<Map<String, Object>> listMap) throws NullPointerException {
		List<String> listConcad = new ArrayList<String>();
		for (Map<String, Object> result : listMap) {
			String custName = (String) result.get("custName");
			String credentialsCode = (String) result.get("credentialsCode");
			String corporateAccount = (String) result.get("corporateAccount");
			String adjustAmount = (String) result.get("adjustAmount");
			String adjustType = (String) result.get("adjustType");// 调账
			if (StringUtils.isEmpty(custName))
				throw new NullPointerException("客户姓名为空");
			if (StringUtils.isEmpty(credentialsCode))
				throw new NullPointerException("身份证号为空");
			if (StringUtils.isEmpty(adjustType))
				throw new NullPointerException("类型为空");			
			if (StringUtils.isEmpty(adjustAmount))
				throw new NullPointerException("金额为空");		
			if (StringUtils.isEmpty(corporateAccount))
				throw new NullPointerException("公司账户为空");	
			if(!Constant.TANS_ACCOUNT_TYPE_03.equals(adjustType)  &&  !Constant.TANS_ACCOUNT_TYPE_02.equals(adjustType) && !Constant.TANS_ACCOUNT_TYPE_01.equals(adjustType)){
				throw new NullPointerException("类型错误(只能是"+Constant.TANS_ACCOUNT_TYPE_03+","+Constant.TANS_ACCOUNT_TYPE_02+","+Constant.TANS_ACCOUNT_TYPE_01+")");
			}
			
			
			boolean flat = listConcad.contains(custName + credentialsCode + adjustType);
			if (flat) {
				throw new NullPointerException("用户名:" + custName + ",身份证:" + credentialsCode + "重复");
			} else {
				listConcad.add(custName + credentialsCode + adjustType);
			}
			String sqlAccountCode = "SELECT T.ACCOUNT_NO,T.CUST_ID,TT.CREDENTIALS_CODE FROM BAO_T_ACCOUNT_INFO T,BAO_T_CUST_INFO TT WHERE T.CUST_ID=TT.ID AND TT.CUST_NAME = ? AND TT.CREDENTIALS_CODE = ?";
			List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlAccountCode, new Object[] { StringUtils.trimAllWhitespace(custName),  StringUtils.trimAllWhitespace(credentialsCode)});
			if (list == null || list.size() == 0)
				throw new NullPointerException("用户名" + custName + "不存在");
			Map<String, Object> map = list.get(0);
			if (map == null || map.size() == 0)
				throw new NullPointerException("用户名" + custName + "不存在");
			boolean flag = false;
			for (Map<String, Object> param : list) {
				if (!StringUtils.isEmpty(param.get("CREDENTIALS_CODE"))) {
					if (param.get("CREDENTIALS_CODE").equals(credentialsCode))
						flag = true;
				}
			}
			if (!flag)
				throw new NullPointerException("用户名" + custName + "身份证号错误");

			if (org.apache.commons.lang3.StringUtils.isEmpty(adjustAmount))
				throw new NullPointerException("调账金额不能为空");
			if (new BigDecimal(adjustAmount).compareTo(new BigDecimal(0)) <= 0) {
				throw new NullPointerException(custName + "调账金额不能小于0元");
			}
			if (StringUtils.isEmpty(adjustType)) {
				throw new NullPointerException(custName + "调账类型不能为空");
			}
			// 转让账号
			result.put("intoAccount", map.get("ACCOUNT_NO"));
			result.put("custId", map.get("CUST_ID"));
			// 转出账号
			String sqlCorporateAccount = "SELECT T.ACCOUNT_NO FROM BAO_T_ACCOUNT_INFO T WHERE T.CUST_ID = (SELECT TT.ID FROM BAO_T_CUST_INFO TT WHERE TT.LOGIN_NAME=? )";
			List<Map<String, Object>> listCorporateAccount = repositoryUtil.queryForMap(sqlCorporateAccount, new Object[] { StringUtils.trimAllWhitespace(corporateAccount) });
			if (listCorporateAccount == null || listCorporateAccount.size() == 0)
				throw new NullPointerException("公司账户" + corporateAccount + "不存在");
			Map<String, Object> mapCorporateAccount = listCorporateAccount.get(0);
			if (map == null || map.size() == 0)
				throw new NullPointerException("公司账户" + corporateAccount + "不存在");
			// EXPEND_ACCOUNT 支出账户
			result.put("expendAccount", mapCorporateAccount.get("ACCOUNT_NO"));
		}

		// 转账表
		StringBuffer baotTransAccountInfoSQL = new StringBuffer();
		baotTransAccountInfoSQL.append("INSERT INTO BAO_T_TRANS_ACCOUNT_INFO(ID,INTO_ACCOUNT,EXPEND_ACCOUNT,TRADE_AMOUNT,TRANS_TYPE").append(",RECORD_STATUS,CREATE_USER,CREATE_DATE,LAST_UPDATE_USER,LAST_UPDATE_DATE,VERSION,MEMO)").append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
		jdbcTemplate.batchUpdate(baotTransAccountInfoSQL.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				String taciId = UUID.randomUUID().toString();
				ps.setString(1, taciId);
				listMap.get(i).put("taciId", taciId);
				ps.setString(2, (String) listMap.get(i).get("intoAccount"));
				ps.setString(3, (String) listMap.get(i).get("expendAccount"));
				ps.setBigDecimal(4, new BigDecimal((String) listMap.get(i).get("adjustAmount")));
				ps.setString(5, (String) listMap.get(i).get("adjustType"));
				ps.setString(6, Constant.AUDIT_STATUS_REVIEWD);
				ps.setString(7, (String) listMap.get(i).get("createUser"));
				ps.setTimestamp(8, new Timestamp(new java.util.Date().getTime()));
				ps.setString(9, (String) listMap.get(i).get("createUser"));
				ps.setTimestamp(10, new Timestamp(new java.util.Date().getTime()));
				ps.setInt(11, Integer.valueOf(0));
				ps.setString(12, "");
			}

			@Override
			public int getBatchSize() {
				return listMap.size();
			}
		});

		// 审核表
		StringBuffer baotAuditInfoSQL = new StringBuffer();
		baotAuditInfoSQL.append("INSERT INTO BAO_T_AUDIT_INFO(ID,CUST_ID,RELATE_TYPE,RELATE_PRIMARY,APPLY_TYPE").append(",TRADE_AMOUNT,APPLY_TIME,AUDIT_STATUS,TRADE_STATUS,RECORD_STATUS,CREATE_USER,CREATE_DATE,VERSION,MEMO)").append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		jdbcTemplate.batchUpdate(baotAuditInfoSQL.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, UUID.randomUUID().toString());
				ps.setString(2, (String) listMap.get(i).get("custId"));
				ps.setString(3, Constant.TABLE_BAO_T_TRANS_ACCOUNT_INFO);
				ps.setString(4, (String) listMap.get(i).get("taciId"));
				ps.setString(5, (String) listMap.get(i).get("adjustType"));
				ps.setBigDecimal(6, new BigDecimal((String) listMap.get(i).get("adjustAmount")));
				ps.setTimestamp(7, new Timestamp(new java.util.Date().getTime()));
				ps.setString(8, Constant.AUDIT_STATUS_REVIEWD);
				ps.setString(9, Constant.TRADE_STATUS_01);
				ps.setString(10, Constant.VALID_STATUS_VALID);
				ps.setString(11, (String) listMap.get(i).get("createUser"));
				ps.setTimestamp(12, new Timestamp(new java.util.Date().getTime()));
				ps.setInt(13, Integer.valueOf(0));
				ps.setString(14, "");
			}

			@Override
			public int getBatchSize() {
				return listMap.size();
			}
		});

		return true;
	}

	public Map<String, Object> list(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = transAccountInfoRepositoryCustom.findTransAccountInfoEntity(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	// @Transactional(rollbackFor = SLException.class, propagation =
	// Propagation.REQUIRED)
	@Transactional(readOnly = false)
	public ResultVo sendAdjustAccountProcess(Map<String, Object> param) {
		String rewardSendId = (String) param.get("rewardSendId");
		String rewardSendIdArray[] = rewardSendId.trim().split(",");
		List<CustActivityDetailEntity> listCustActivityDetailEntity = null;
		AccountInfoEntity accountInfoEntityInto = null;
		try {
			for (String str : rewardSendIdArray) {
				if ("".equals(str)) {
					continue;
				}

				TransAccountInfoEntity atie = transAccountInfoRepository.findOne(str);
				if (StringUtils.isEmpty(atie))
					continue;

				if (Constant.AUDIT_STATUS_PASS.equals(atie.getAuditStatus()))
					continue;

				AuditInfoEntity auditInfoEntity = auditInfoRepository.findAuditInfoEntityByRelatePrimary(str);

				auditInfoEntity.setAuditTime(new java.util.Date());
				atie.setMemo((String) param.get("memo"));
				// custId
				String custId = (String) param.get("custId");
				CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
				if (StringUtils.isEmpty(custInfoEntity)) {
					String userName = userRepository.findUserNameById(custId);
					if (!StringUtils.isEmpty(userName) && !"".equals(userName))
						auditInfoEntity.setAuditUser(userName);
					else
						auditInfoEntity.setAuditUser(custId);
				} else {
					auditInfoEntity.setAuditUser(custInfoEntity.getLoginName());
				}

				if ("通过".equals(param.get("result"))) {
					if (!StringUtils.isEmpty(atie)) {

						//请求号
						String rquestNo = numberService.generateTradeBatchNumber();
						
						if (atie.getTransType().equals(Constant.TANS_ACCOUNT_TYPE_03) || atie.getTransType().equals(Constant.TANS_ACCOUNT_TYPE_01)) {
							// 公司出 BAO账户
							AccountInfoEntity accountInfoEntityExpend = accountInfoRepository.findByAccountNo(atie.getExpendAccount());
							if (StringUtils.isEmpty(accountInfoEntityExpend))
								throw new SLException(atie.getExpendAccount() + "为空");
							// 个人账户
							accountInfoEntityInto = accountInfoRepository.findByAccountNo(atie.getIntoAccount());
							if (StringUtils.isEmpty(accountInfoEntityInto))
								throw new SLException(atie.getIntoAccount() + "为空");

							// 公司出 BAO账户
							BigDecimal accountInfoEntityExpendAmount = accountInfoEntityExpend.getAccountTotalAmount().subtract(atie.getTradeAmount());
							if (accountInfoEntityExpendAmount.compareTo(new BigDecimal(0)) == -1) {
								throw new SLException(accountInfoEntityExpend.getAccountNo() + "账户总金额不足");

							}
							BigDecimal accountInfoEntityExpendAmountAvailable = accountInfoEntityExpend.getAccountAvailableAmount().subtract(atie.getTradeAmount());
							if (accountInfoEntityExpendAmountAvailable.compareTo(new BigDecimal(0)) == -1) {
								throw new SLException(accountInfoEntityExpend.getAccountNo() + "账户可用金额不足");

							}
							//转出账户
							AccountFlowInfoEntity accountFlowInfoEntityExpend = new AccountFlowInfoEntity();
							accountFlowInfoEntityExpend.setCustId(accountInfoEntityExpend.getCustId());
							accountFlowInfoEntityExpend.setAccountId(accountInfoEntityExpend.getId());
							accountFlowInfoEntityExpend.setAccountType(Constant.ACCOUNT_TYPE_MAIN);
							accountFlowInfoEntityExpend.setTradeType(atie.getTransType());
							accountFlowInfoEntityExpend.setRequestNo(rquestNo);
							accountFlowInfoEntityExpend.setTradeNo(numberService.generateTradeNumber());
							accountFlowInfoEntityExpend.setBankrollFlowDirection(Constant.BANKROLL_FLOW_DIRECTION_OUT);
							accountFlowInfoEntityExpend.setCreateDate(new java.util.Date());
							accountFlowInfoEntityExpend.setTradeAmount(atie.getTradeAmount());
							accountFlowInfoEntityExpend.setTradeDate(new java.util.Date());
							accountFlowInfoEntityExpend.setAccountTotalAmount(accountInfoEntityExpendAmount);
							accountFlowInfoEntityExpend.setAccountFreezeAmount(accountInfoEntityExpend.getAccountFreezeAmount());
							accountFlowInfoEntityExpend.setAccountAvailable(accountInfoEntityExpendAmountAvailable);
							accountFlowInfoEntityExpend.setFlowType(SubjectConstant.SUBJECT_TYPE_AMOUNT);
							accountFlowInfoEntityExpend.setTargetAccount(accountInfoEntityInto.getId());
							accountFlowInfoEntityExpend = accountFlowInfoRepository.save(accountFlowInfoEntityExpend);

//							AccountFlowDetailEntity accountFlowDetailEntityExpend = new AccountFlowDetailEntity();
//							accountFlowDetailEntityExpend.setAccountFlowId(accountFlowInfoEntityExpend.getId());
//							accountFlowDetailEntityExpend.setSubjectType(atie.getTransType());
//							accountFlowDetailEntityExpend.setSubjectDirection(Constant.BANKROLL_FLOW_DIRECTION_OUT);
//							accountFlowDetailEntityExpend.setTradeAmount(atie.getTradeAmount());
//							accountFlowDetailEntityExpend.setTradeDesc(atie.getTransType());
//							accountFlowDetailEntityExpend.setCreateDate(new java.util.Date());
//							accountFlowDetailEntityExpend.setTargetAccount(accountInfoEntityInto.getId());
//							accountFlowDetailEntityExpend.setCreateUser(custId);
//							accountFlowDetailRepository.save(accountFlowDetailEntityExpend);

							//转入账户
							AccountFlowInfoEntity accountFlowInfoEntityInto = new AccountFlowInfoEntity();
							accountFlowInfoEntityInto.setCustId(accountInfoEntityInto.getCustId());
							accountFlowInfoEntityInto.setAccountId(accountInfoEntityInto.getId());
							accountFlowInfoEntityInto.setAccountType(Constant.ACCOUNT_TYPE_MAIN);
							accountFlowInfoEntityInto.setTradeType(atie.getTransType());
							accountFlowInfoEntityInto.setRequestNo(rquestNo);
							accountFlowInfoEntityInto.setTradeNo(numberService.generateTradeNumber());
							accountFlowInfoEntityInto.setBankrollFlowDirection(Constant.BANKROLL_FLOW_DIRECTION_IN);
							accountFlowInfoEntityInto.setTradeAmount(atie.getTradeAmount());
							accountFlowInfoEntityInto.setCreateDate(new java.util.Date());
							accountFlowInfoEntityInto.setTradeDate(new java.util.Date());
							BigDecimal accountFlowInfoEntityIntoAccountTotalAmount = accountInfoEntityInto.getAccountTotalAmount().add(atie.getTradeAmount());
							BigDecimal accountFlowInfoEntityIntoAccountAvailableAmount = accountInfoEntityInto.getAccountAvailableAmount().add(atie.getTradeAmount());
							//
							accountFlowInfoEntityInto.setAccountTotalAmount(accountFlowInfoEntityIntoAccountTotalAmount);
							accountFlowInfoEntityInto.setAccountFreezeAmount(accountInfoEntityInto.getAccountFreezeAmount());
							accountFlowInfoEntityInto.setAccountAvailable(accountFlowInfoEntityIntoAccountAvailableAmount);
							accountFlowInfoEntityInto.setFlowType(SubjectConstant.SUBJECT_TYPE_AMOUNT);
							accountFlowInfoEntityInto.setTargetAccount(accountInfoEntityExpend.getId());
							accountFlowInfoEntityInto = accountFlowInfoRepository.save(accountFlowInfoEntityInto);

//							AccountFlowDetailEntity accountFlowDetailEntityInto = new AccountFlowDetailEntity();
//							accountFlowDetailEntityInto.setAccountFlowId(accountFlowInfoEntityInto.getId());
//							accountFlowDetailEntityInto.setSubjectType(atie.getTransType());
//							accountFlowDetailEntityInto.setSubjectDirection(Constant.BANKROLL_FLOW_DIRECTION_IN);
//							accountFlowDetailEntityInto.setTradeAmount(atie.getTradeAmount());
//							accountFlowDetailEntityInto.setTradeDesc(atie.getTransType());
//							accountFlowDetailEntityInto.setCreateDate(new java.util.Date());
//							accountFlowDetailEntityInto.setTargetAccount(accountInfoEntityExpend.getId());
//							accountFlowDetailEntityInto.setCreateUser(custId);
//							accountFlowDetailRepository.save(accountFlowDetailEntityInto);

							accountInfoEntityExpend.setAccountTotalAmount(accountInfoEntityExpendAmount);
							accountInfoEntityExpend.setAccountAvailableAmount(accountInfoEntityExpendAmountAvailable);
							accountInfoRepository.save(accountInfoEntityExpend);
							// 个人账户
							accountInfoEntityInto.setAccountTotalAmount(accountFlowInfoEntityIntoAccountTotalAmount);
							accountInfoEntityInto.setAccountAvailableAmount(accountFlowInfoEntityIntoAccountAvailableAmount);
							accountInfoRepository.save(accountInfoEntityInto);

						} else if (Constant.TANS_ACCOUNT_TYPE_02.equals(atie.getTransType())) {
							BigDecimal tradeAmount = atie.getTradeAmount();
							String intoAccount = atie.getIntoAccount();

							// 公司出 BAO账户流水信息表
							AccountInfoEntity accountInfoEntityExpend = accountInfoRepository.findByAccountNo(atie.getExpendAccount());
							if (StringUtils.isEmpty(accountInfoEntityExpend))
								throw new SLException(atie.getExpendAccount() + "为空");

							accountInfoEntityInto = accountInfoRepository.findByAccountNo(intoAccount);
							if (StringUtils.isEmpty(accountInfoEntityInto))
								throw new SLException(intoAccount + "为空");

							BigDecimal accountInfoEntityExpendAmount = accountInfoEntityExpend.getAccountTotalAmount().subtract(atie.getTradeAmount());
							if (accountInfoEntityExpendAmount.compareTo(new BigDecimal(0)) == -1) {
								throw new SLException(accountInfoEntityExpend.getAccountNo() + "账户总金额不足");

							}
							BigDecimal accountInfoEntityExpendAmountAvailable = accountInfoEntityExpend.getAccountAvailableAmount().subtract(atie.getTradeAmount());
							if (accountInfoEntityExpendAmountAvailable.compareTo(new BigDecimal(0)) == -1) {
								throw new SLException(accountInfoEntityExpend.getAccountNo() + "账户可用金额不足");

							}

						

							Calendar cal = Calendar.getInstance();
							cal.add(Calendar.MONTH, -1);
							int month = cal.get(Calendar.MONTH) + 1;

							String queryMonth = String.format("%02d", month);

							listCustActivityDetailEntity = custActivityDetailEntityRepository.findCustActivityDetailByDefine(ParamsNameConstant.ACTIVITY_RECOMMEND_NAME, Constant.USER_ACTIVITY_TRADE_STATUS_06, accountInfoEntityInto.getCustId(), queryMonth);
							StringBuffer sbId_t = new StringBuffer();
							BigDecimal total = new BigDecimal(0);
							for (CustActivityDetailEntity custActivityDetailEntity : listCustActivityDetailEntity) {
								sbId_t.append("'").append(custActivityDetailEntity.getCustActivityId()).append("',");
								total = total.add(custActivityDetailEntity.getTradeAmount());
								// custActivityDetailEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_05);
							}
							if (tradeAmount.compareTo(total) != 0) {
								CustInfoEntity ci = custInfoRepository.findOne(accountInfoEntityInto.getCustId());
								return new ResultVo(false, ci.getCustName() + "调账失败有效金额和调账金额不符");
							}
							
							// 公司出 BAO账户流水信息表
							accountInfoEntityExpend.setAccountTotalAmount(accountInfoEntityExpendAmount);
							accountInfoEntityExpend.setAccountAvailableAmount(accountInfoEntityExpendAmountAvailable);
							accountInfoRepository.save(accountInfoEntityExpend);
							// 个人账户
							accountInfoEntityInto.setAccountTotalAmount(accountInfoEntityInto.getAccountTotalAmount().add(atie.getTradeAmount()));
							accountInfoEntityInto.setAccountAvailableAmount(accountInfoEntityInto.getAccountAvailableAmount().add(atie.getTradeAmount()));
							accountInfoRepository.save(accountInfoEntityInto);
							
							// 更新事务状态
							updateCustActivityDetailEntityTradeStatus(listCustActivityDetailEntity, Constant.USER_ACTIVITY_TRADE_STATUS_05);

							// 公司出 BAO账户
							AccountFlowInfoEntity accountFlowInfoEntityExpend = new AccountFlowInfoEntity();
							accountFlowInfoEntityExpend = new AccountFlowInfoEntity();
							accountFlowInfoEntityExpend.setCustId(accountInfoEntityExpend.getCustId());
							accountFlowInfoEntityExpend.setAccountId(accountInfoEntityExpend.getId());
							accountFlowInfoEntityExpend.setAccountType(Constant.ACCOUNT_TYPE_MAIN);
							accountFlowInfoEntityExpend.setTradeType(atie.getTransType());
							accountFlowInfoEntityExpend.setRequestNo(rquestNo);
							accountFlowInfoEntityExpend.setTradeNo(numberService.generateTradeNumber());
							accountFlowInfoEntityExpend.setBankrollFlowDirection(Constant.BANKROLL_FLOW_DIRECTION_OUT);
							accountFlowInfoEntityExpend.setCreateDate(new java.util.Date());
							accountFlowInfoEntityExpend.setTradeAmount(atie.getTradeAmount());
							accountFlowInfoEntityExpend.setTradeDate(new java.util.Date());
							accountFlowInfoEntityExpend.setFlowType(SubjectConstant.SUBJECT_TYPE_AMOUNT); // 公司金额
							accountFlowInfoEntityExpend.setAccountTotalAmount(accountInfoEntityExpendAmount);
							accountFlowInfoEntityExpend.setAccountAvailable(accountInfoEntityExpendAmountAvailable);
							accountFlowInfoEntityExpend.setAccountFreezeAmount(accountInfoEntityExpend.getAccountFreezeAmount());
							accountFlowInfoEntityExpend.setTargetAccount(accountInfoEntityInto.getId());
							accountFlowInfoEntityExpend = accountFlowInfoRepository.save(accountFlowInfoEntityExpend);
							// 公司出 BAO账户流水
//							AccountFlowDetailEntity accountFlowDetailEntityExpend = new AccountFlowDetailEntity();
//							accountFlowDetailEntityExpend.setAccountFlowId(accountFlowInfoEntityExpend.getId());
//							accountFlowDetailEntityExpend.setSubjectType(atie.getTransType());
//							accountFlowDetailEntityExpend.setSubjectDirection(Constant.BANKROLL_FLOW_DIRECTION_OUT);
//							accountFlowDetailEntityExpend.setTradeAmount(atie.getTradeAmount());
//							accountFlowDetailEntityExpend.setTradeDesc(atie.getTransType());
//							accountFlowDetailEntityExpend.setCreateDate(new java.util.Date());
//							accountFlowDetailEntityExpend.setTargetAccount(accountInfoEntityInto.getId());
//							accountFlowDetailRepository.save(accountFlowDetailEntityExpend); // 转出账号
							// 更新公司账户

							// 转让账号
							AccountFlowInfoEntity accountFlowInfoEntityInto = new AccountFlowInfoEntity();
							accountFlowInfoEntityInto.setCustId(accountInfoEntityInto.getCustId());
							accountFlowInfoEntityInto.setAccountId(accountInfoEntityInto.getId());
							accountFlowInfoEntityInto.setAccountType(Constant.ACCOUNT_TYPE_MAIN);
							accountFlowInfoEntityInto.setTradeType(atie.getTransType());
							accountFlowInfoEntityInto.setRequestNo(rquestNo);
							accountFlowInfoEntityInto.setTradeNo(numberService.generateTradeNumber());
							accountFlowInfoEntityInto.setBankrollFlowDirection(Constant.BANKROLL_FLOW_DIRECTION_IN);
							accountFlowInfoEntityInto.setTradeAmount(atie.getTradeAmount());
							accountFlowInfoEntityInto.setCreateDate(new java.util.Date());
							accountFlowInfoEntityInto.setTradeDate(new java.util.Date());
							accountFlowInfoEntityInto.setFlowType(SubjectConstant.SUBJECT_TYPE_AMOUNT);
							// mzhu 20150615 更新流水
							accountFlowInfoEntityInto.setAccountTotalAmount(accountInfoEntityInto.getAccountTotalAmount());
							accountFlowInfoEntityInto.setAccountAvailable(accountInfoEntityInto.getAccountAvailableAmount());
							accountFlowInfoEntityInto.setAccountFreezeAmount(accountInfoEntityInto.getAccountFreezeAmount());
							accountFlowInfoEntityInto.setTargetAccount(accountInfoEntityExpend.getId());
							accountFlowInfoEntityInto = accountFlowInfoRepository.save(accountFlowInfoEntityInto);

//							// 转让账号 流水
//							AccountFlowDetailEntity accountFlowDetailEntityInto = new AccountFlowDetailEntity();
//							accountFlowDetailEntityInto.setAccountFlowId(accountFlowInfoEntityInto.getId());
//							accountFlowDetailEntityInto.setSubjectType(atie.getTransType());
//							accountFlowDetailEntityInto.setSubjectDirection(Constant.BANKROLL_FLOW_DIRECTION_IN);
//							accountFlowDetailEntityInto.setTradeAmount(atie.getTradeAmount());
//							accountFlowDetailEntityInto.setTradeDesc(atie.getTransType());
//							accountFlowDetailEntityInto.setCreateDate(new java.util.Date());
//							accountFlowDetailEntityInto.setTargetAccount(accountInfoEntityExpend.getId());
//							accountFlowDetailRepository.save(accountFlowDetailEntityInto);

							String sbId = sbId_t.substring(0, sbId_t.length() - 1);
							String custActivityDetailEntityTradeStatusSQL = "SELECT T.CUST_ACTIVITY_ID, SUM(DECODE(T.TRADE_STATUS, '未结算', 1, 0)) AS UN_TRADE_STATUS,SUM(DECODE(T.TRADE_STATUS, '已结算', 1, 0)) AS TRADE_STATUS, COUNT(1) AS TOTAL  FROM BAO_T_CUST_ACTIVITY_DETAIL T WHERE T.CUST_ACTIVITY_ID IN(" + sbId + ")  AND TO_CHAR(T.CREATE_DATE,'mm')='" + queryMonth + "'  GROUP BY CUST_ACTIVITY_ID";
							List<Map<String, Object>> listMap = repositoryUtil.queryForMap(custActivityDetailEntityTradeStatusSQL, null);
							String tradeStatusId = "";
							if (null != listMap && listMap.size() > 0) {
								for (Map<String, Object> paramMap : listMap) {
									BigDecimal tradeStatus = (BigDecimal) paramMap.get("TRADE_STATUS");
									BigDecimal totalBg = (BigDecimal) paramMap.get("TOTAL");
									if (totalBg.compareTo(tradeStatus) == 0) {
										tradeStatusId = tradeStatusId.concat("'").concat("" + paramMap.get("CUST_ACTIVITY_ID")).concat("',");
									}
								}

							}

							if (!"".equals(tradeStatusId)) {
								String tradeStatusIdSQL = tradeStatusId.substring(0, tradeStatusId.length() - 1);
								String updateSQL = "UPDATE BAO_T_CUST_ACTIVITY_INFO SET TRADE_STATUS='" + Constant.USER_ACTIVITY_TRADE_STATUS_05 + "' WHERE ID IN(" + tradeStatusIdSQL + ")";
								jdbcTemplate.update(updateSQL);
							}

//							List<FlowBusiRelationEntity> listFlowBusiRelationEntity = new ArrayList<FlowBusiRelationEntity>();
//
//							for (CustActivityDetailEntity custActivityDetailEntity : listCustActivityDetailEntity) {
//								FlowBusiRelationEntity flowBusiRelationEntity = new FlowBusiRelationEntity();
//								flowBusiRelationEntity.setAccountFlowId(accountFlowInfoEntityInto.getId());
//								flowBusiRelationEntity.setRelatePrimary(custActivityDetailEntity.getId());
//								flowBusiRelationEntity.setRelateType(Constant.TABLE_BAO_T_CUST_ACTIVITY_DETAIL);
//								flowBusiRelationEntity.setCreateDate(new java.util.Date());
//								listFlowBusiRelationEntity.add(flowBusiRelationEntity);
//							}
//							flowBusiRelationRepository.save(listFlowBusiRelationEntity);

						}

					}

				}

				atie.setRecordStatus(Constant.TRADE_STATUS_03);
				atie.setAuditStatus((String) param.get("result"));
				auditInfoEntity.setAuditStatus((String) param.get("result"));
				atie.setTradeStatus(Constant.TRADE_STATUS_03);
				auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03);

				transAccountInfoRepository.save(atie);
				auditInfoRepository.save(auditInfoEntity);
			}
		} catch (Exception e) {

			updateCustActivityDetailEntityTradeStatus(listCustActivityDetailEntity, Constant.USER_ACTIVITY_TRADE_STATUS_06);
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}

		return new ResultVo(true, "账户调账成功");
	}

	@Autowired
	private TransAccountInfoServiceUtilImpl transAccountInfoServiceUtilImpl;

	/**
	 * 新事物
	 * 
	 * @param listCustActivityDetailEntity
	 * @param userActivityTradeStatus
	 */
	public void updateCustActivityDetailEntityTradeStatus(List<CustActivityDetailEntity> listCustActivityDetailEntity, String userActivityTradeStatus) {
		ArrayList<String> listIds = new ArrayList<String>();
		for (CustActivityDetailEntity cade : listCustActivityDetailEntity) {
			listIds.add(cade.getId());
		}
		transAccountInfoServiceUtilImpl.updateCustActivityDetailEntityTradeStatus(listIds, userActivityTradeStatus);
	}

	@Override
	public ResultVo queryCompanyTransAccountList(Map<String, Object> param) {
		ResultVo resultVo = new ResultVo(true);
		Page<Map<String, Object>> page = transAccountInfoRepositoryCustom.findCompanyTransAccountList(param);
		resultVo.putValue("iTotalDisplayRecords", page.getTotalElements());
		resultVo.putValue("data", page.getContent());
		return resultVo;
	}

	@Transactional
	@Override
	public ResultVo saveCompanyTransAccount(Map<String, Object> params) {
		String companyId = (String) params.get("companyId");
		if(StringUtils.isEmpty(companyId)){
			return new ResultVo(false, "商户id不能为空");
		}
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(companyId);
		if(custInfoEntity == null){
			return new ResultVo(false, "没有找到该商户信息");
		}
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(custInfoEntity.getId());
		TransAccountInfoEntity transAccountInfoEntity = new TransAccountInfoEntity();
		//转账类型
		String transType = (String) params.get("transType");
		if(Constant.ACCOUNT_TRANS_DIRECTION_IN.equals(transType)){
			transAccountInfoEntity.setIntoAccount(accountInfoEntity.getAccountNo());
		}else if(Constant.ACCOUNT_TRANS_DIRECTION_OUT.equals(transType)){
			transAccountInfoEntity.setExpendAccount(accountInfoEntity.getAccountNo());
		}
		transAccountInfoEntity.setTransType(transType);
		transAccountInfoEntity.setAuditStatus(Constant.AUDIT_PROJECT_STATUS_REVIEWING);
		transAccountInfoEntity.setBasicModelProperty((String)params.get("userId"), true);
		transAccountInfoEntity.setMemo((String) params.get("transMemo"));
		transAccountInfoRepository.save(transAccountInfoEntity);
		return new ResultVo(true);
	}

	@Override
	public ResultVo queryCompanyTransAccountById(Map<String, Object> params) {
		return transAccountInfoRepositoryCustom.findCompanyTransAccountById(params);
	}

	@Transactional
	@Override
	public ResultVo auditCompanyTransAccount(Map<String, Object> params) {
		String transId = (String) params.get("transId");
		String auditStatus = (String) params.get("auditStatus");
		String auditUser = (String) params.get("auditUser");
		String auditMemo = (String) params.get("auditMemo");
		
		if(StringUtils.isEmpty(transId)){
			return new ResultVo(false, "转账id不能为空");
		}
		//转账信息
		TransAccountInfoEntity transAccountInfoEntity = transAccountInfoRepository.findOne(transId);
		if(transAccountInfoEntity == null){
			return new ResultVo(false, "没有此转账记录");
		}
		String operBeforeStatus = transAccountInfoEntity.getAuditStatus();
		if(!Constant.AUDIT_PROJECT_STATUS_REVIEWING.equals(operBeforeStatus)){
			return new ResultVo(false, "转账不是未审核状态，不能进行审核操作");
		}
		transAccountInfoEntity.setAuditStatus(auditStatus);
		transAccountInfoEntity.setBasicModelProperty(auditUser, false);
		
		//审核
		AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
		auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRANS_ACCOUNT_INFO);
		auditInfoEntity.setRelatePrimary(transId);
		auditInfoEntity.setApplyType(transAccountInfoEntity.getTransType());
		auditInfoEntity.setTradeAmount(transAccountInfoEntity.getTradeAmount());
		auditInfoEntity.setApplyTime(new Date());
		auditInfoEntity.setAuditTime(new Date());
		auditInfoEntity.setAuditUser(auditUser);
		auditInfoEntity.setAuditStatus(auditStatus);
		auditInfoEntity.setBasicModelProperty(auditUser, true);
		auditInfoEntity.setMemo(auditMemo);
		auditInfoRepository.save(auditInfoEntity);
		
		//日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUDIT_INFO);
		logInfoEntity.setRelatePrimary(auditInfoEntity.getId());
		logInfoEntity.setLogType(Constant.LOG_TYPE_AUDIT);
		logInfoEntity.setOperBeforeContent(operBeforeStatus);
		logInfoEntity.setOperAfterContent(auditStatus);
		logInfoEntity.setOperPerson(auditUser);
		logInfoEntity.setBasicModelProperty(auditUser, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		String accountId = "";
		String tradeType = "";
		BigDecimal tradeAmount = transAccountInfoEntity.getTradeAmount();
		if(Constant.ACCOUNT_TRANS_DIRECTION_IN.equals(transAccountInfoEntity.getTransType())){
			accountId = transAccountInfoEntity.getIntoAccount();
			tradeType = Constant.OPERATION_TYPE_05;
		} else {
			accountId = transAccountInfoEntity.getExpendAccount();
			tradeType = Constant.OPERATION_TYPE_06;
			tradeAmount = new BigDecimal("-" + tradeAmount.toString());
		}
		//账户
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findOne(accountId);
		//审核--客户id
		auditInfoEntity.setCustId(accountInfoEntity.getCustId());
		
		//审核拒绝，不记录账户和账户流水信息
		if(Constant.AUDIT_PROJECT_STATUS_REfUSE.equals(auditStatus)){
			return new ResultVo(true);
		}
		
		BigDecimal accountTotalAmount = accountInfoEntity.getAccountTotalAmount().add(tradeAmount);
		BigDecimal accountFreezeAmount = accountInfoEntity.getAccountFreezeAmount();
		BigDecimal accountAvailableAmount = accountInfoEntity.getAccountAvailableAmount().add(tradeAmount);
		accountInfoEntity.setAccountTotalAmount(accountTotalAmount);
		accountInfoEntity.setAccountAvailableAmount(accountAvailableAmount);
		accountInfoEntity.setBasicModelProperty(auditUser, false);
		
		//账户流水
		AccountFlowInfoEntity accountFlowInfoEntity = new AccountFlowInfoEntity();
		//请求号
		String requestNo = numberService.generateTradeBatchNumber();
		accountFlowInfoEntity.setAccountId(accountId);
		accountFlowInfoEntity.setAccountType(Constant.ACCOUNT_TYPE_MAIN);
		accountFlowInfoEntity.setTradeType(tradeType);
		accountFlowInfoEntity.setRequestNo(requestNo);
		accountFlowInfoEntity.setTradeNo(numberService.generateTradeNumber());
		accountFlowInfoEntity.setBankrollFlowDirection(transAccountInfoEntity.getTransType());
		accountFlowInfoEntity.setTradeAmount(transAccountInfoEntity.getTradeAmount());
		accountFlowInfoEntity.setTradeDate(new Date());
		accountFlowInfoEntity.setAccountTotalAmount(accountTotalAmount);
		accountFlowInfoEntity.setAccountAvailable(accountAvailableAmount);
		accountFlowInfoEntity.setAccountFreezeAmount(accountFreezeAmount);
		accountFlowInfoEntity.setBasicModelProperty(auditUser, true);
		accountFlowInfoRepository.save(accountFlowInfoEntity);
		
		return new ResultVo(true);
	}

}
