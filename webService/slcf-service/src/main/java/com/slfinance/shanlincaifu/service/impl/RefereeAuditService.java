package com.slfinance.shanlincaifu.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.entity.CustApplyInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.CustRecommendInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.repository.AuditInfoRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRespository;
import com.slfinance.shanlincaifu.repository.CustApplyInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.CustRecommendInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.UserRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.CustRecommendInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**
 * 金牌推荐人
 * 
 * @author zhumin
 *
 */
@Service
public class RefereeAuditService {

	@Autowired
	private RepositoryUtil repositoryUtil;

	@Autowired
	private CustApplyInfoRepository custApplyInfoRepository;

	@Autowired
	private AuditInfoRepository auditInfoRepository;
	
	@Autowired
	private AuditInfoRespository auditInfoRespository;

	@Autowired
	private CustInfoRepository custInfoRepository;

	@Autowired
	private CustRecommendInfoRepository custRecommendInfoRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FlowNumberService flowNumberService;
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
//	@Autowired
//	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	CustRecommendInfoRepositoryCustom custRecommendInfoRepositoryCustom;
	
	/**
	 * 查询数据（金牌推荐人）
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> list(Map<String, Object> param) {
		Map<String, Object> data = new HashMap<String, Object>();
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select t.btcpid as btcpid, t.id,t.login_name,t.cust_name, t.credentials_type," + "t.credentials_code,t.mobile,t.spread_level,t.APPLY_STATUS, t.create_date, h.工号 user_number,t.salesman_type" + " from " + "(select btcpi.id as btcpid, btci.id, btci.login_name,btci.cust_name, btci.credentials_type, btci.credentials_code," + " btci.mobile,btci.spread_level, btcpi.APPLY_STATUS,btcpi.apply_date  as create_date,btcpi.salesman_type from bao_t_cust_info btci, BAO_T_CUST_APPLY_INFO btcpi" + "  where btcpi.cust_id = btci.id  and btcpi.apply_type = '金牌推荐人申请'  ) t  left join vw_bd_psndoc h on upper(h.证件号码) = upper(t.credentials_code) WHERE 1=1 ");

		int pageNum = Integer.valueOf(param.get("start") + "");
		int pageSize = Integer.valueOf(param.get("length") + "");
		// 客户昵称
		String loginName = (String) param.get("loginName");
		// 证件类型
		String credentialsType = (String) param.get("credentialsType");
		// 证件号码
		String credentialsCode = (String) param.get("credentialsCode");
		// 手机号码
		String mobile = (String) param.get("mobile");
		// 申请日期(开始)
		String startDate = (String) param.get("startDate");
		// 申请日期(结束)
		String endDate = (String) param.get("endDate");
		// 审核状态
		String auditStatus = (String) param.get("auditStatus");
		// 查询条件
		// 总记录数
		if (!StringUtils.isEmpty(loginName)) {
			sb.append(" and t.login_name like ? ");
			listObject.add("%" + loginName + "%");
		}

		if (!StringUtils.isEmpty(credentialsType)) {
			sb.append(" and t.credentials_type = ? ");
			listObject.add(credentialsType);
		}

		if (!StringUtils.isEmpty(credentialsCode)) {
			sb.append(" and t.credentials_code = ? ");
			listObject.add(credentialsCode);
		}

		if (!StringUtils.isEmpty(mobile)) {
			sb.append(" and t.mobile = ? ");
			listObject.add(mobile);
		}

		if (!StringUtils.isEmpty(auditStatus)) {
			sb.append(" and t.APPLY_STATUS = ? ");
			listObject.add(auditStatus);
		}

		if (!StringUtils.isEmpty(startDate)) {
			sb.append(" AND to_char(t.create_date,'YYYY-MM-DD') >=? ");
			listObject.add(startDate);
		}
		if (!StringUtils.isEmpty(endDate)) {
			sb.append(" AND to_char(t.create_date,'YYYY-MM-DD') <=? ");
			listObject.add(endDate);
		}
		sb.append(" order by t.create_date desc ");

		Page<Map<String, Object>> page = repositoryUtil.queryForPageMap(sb.toString(), listObject.toArray(), pageNum, pageSize);
		data.put("iTotalDisplayRecords", page.getTotalElements());
		data.put("data", page.getContent());
		return data;
	}

	/**
	 * 根据条件查询推荐人信息
	 * 
	 * @return
	 */
	public Map<String, Object> findUserRefereeAudit(Map<String, Object> param) {
		StringBuffer sb = new StringBuffer("select T.USER_LOGIN_ACCOUNT,T.USER_NAME, FROM COM_T_USER T ");
		int pageNum = Integer.valueOf(param.get("start") + "");
		int pageSize = Integer.valueOf(param.get("length") + "");
		List<Object> listObject = new ArrayList<Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		Page<Map<String, Object>> page = repositoryUtil.queryForPageMap(sb.toString(), listObject.toArray(), pageNum, pageSize);
		data.put("iTotalDisplayRecords", page.getTotalElements());
		data.put("data", page.getContent());
		return data;
	}

	/**
	 * 查询明细
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> distributionloanDetail(Map<String, Object> param) {
		String custId = (String) param.get("queryCustId");
		String caiId = (String) param.get("caiId");
		// CustInfoEntity custInfoEntity =
		// custInfoRepository.findOne(lie.getCreateUser());
		// if (StringUtils.isEmpty(custInfoEntity)) {
		// String userName =
		// userRepository.findUserNameById(lie.getCreateUser());
		// if (!StringUtils.isEmpty(userName) && !"".equals(userName))
		// lie.setOperPerson(userName);
		// else
		// lie.setOperPerson(lie.getCreateUser());
		// } else {
		// lie.setOperPerson(custInfoEntity.getLoginName());
		// }

		HashMap<String, Object> result = new HashMap<String, Object>();

		CustInfoEntity custInfoEntityResult = custInfoRepository.findOne(custId);

		List<CustApplyInfoEntity> list = new ArrayList<CustApplyInfoEntity>();
		list.add(custApplyInfoRepository.findOne(caiId));
		for (CustApplyInfoEntity caie : list) {
			if (!StringUtils.isEmpty(caie.getCreateUser())) {
				CustInfoEntity custInfoEntity = custInfoRepository.findOne(caie.getCreateUser());
				if (StringUtils.isEmpty(custInfoEntity)) {
					String userName = userRepository.findUserNameById(caie.getCreateUser());
					if (!StringUtils.isEmpty(userName) && !"".equals(userName))
						caie.setCreateUser(userName);
					else
						caie.setCreateUser(custInfoEntity.getLoginName());
				} else {
					// lie.setOperPerson(custInfoEntity.getLoginName());
					caie.setCreateUser(custInfoEntity.getLoginName());
				}
			}
		}

		result.put("data", list);
		result.put("custInfoEntityResult", custInfoEntityResult);
		return result;
	}

	/**
	 * 审计
	 * 
	 * @param param
	 * @return
	 */
	/**
	 * 防止并发
	 */
    private final ReentrantLock mainLock = new ReentrantLock();
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo auditOper(Map<String, Object> param) throws SLException {
		final ReentrantLock mainLock = this.mainLock;
		try {
			mainLock.lock();
			
			String auditStatus = (String) param.get("auditStatus");
			String auditId = (String) param.get("auditId");// 申请表Id
			String memo = (String) param.get("memo");
			String userId = (String) param.get("userId");
			// 审核表信息
			AuditInfoEntity auditInfoEntity = auditInfoRepository.findAuditInfoEntityByRelatePrimary(auditId);
			String custId = auditInfoEntity.getCustId();// 客户经理Id
			auditInfoEntity.setAuditTime(new Date());
			auditInfoEntity.setAuditUser(userId);
			auditInfoEntity.setMemo(memo);
			// 申请表信息
			CustApplyInfoEntity caie = custApplyInfoRepository.findOne(auditId);
			//String applyMemo = caie.getMemo();
			caie.setBasicModelProperty((String) param.get("userId"), false);
			// 客户表信息
			CustInfoEntity cieUpdate = custInfoRepository.findOne(custId);
			if(!StringUtils.isEmpty(cieUpdate.getIsRecommend()) && Constant.IS_RECOMMEND_YES.equals(cieUpdate.getIsRecommend())){
				throw new SLException("该用户已经是业务员");
			}
			
			// 审核中
			if (Constant.AUDIT_STATUS_PASS.equals(auditStatus)) {
				// 审核信息表
				caie.setApplyStatus(Constant.AUDIT_STATUS_PASS);
				caie.setRecordStatus(Constant.VALID_STATUS_VALID);

				auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_PASS);
				auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03);
				auditInfoEntity.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));

				// 更新状态
				cieUpdate.setIsRecommend(Constant.IS_RECOMMEND_YES);
				cieUpdate.setIsEmployee(Constant.IS_RECOMMEND_YES);
				cieUpdate.setWorkingState(Constant.WORKING_STATE_IN);
				
				// 判断是否存在解除业务员的数据，若存在解除业务员的数据则把该业务员之前的客户全部恢复，否则通过客户表推荐码来找
				List<CustApplyInfoEntity> appList =  custApplyInfoRepository.findByCustIdAndApplyTypeAndApplyStatusOrderByCreateDateDesc(custId, Constant.OPERATION_TYPE_23, Constant.AUDIT_STATUS_PASS_RELIEVE);

				// update by liyy @2016/04/12 bug 906 Start				
				List<CustRecommendInfoEntity> newRecommendList = new ArrayList<CustRecommendInfoEntity>();
				List<String> quiltCustIdList = Lists.newArrayList();
				if(appList != null && appList.size() > 0){ // 存在解除数据
					// 906修改的处理
					CustApplyInfoEntity latestCustApply = appList.get(0);
					
					// 上一次解除前有效的数据
					List<CustRecommendInfoEntity> oldRecommendList = custRecommendInfoRepository.findRecommendByCustIdAndApplyId(custId, latestCustApply.getId());
					for (CustRecommendInfoEntity recommendOld : oldRecommendList) {
						// BAO推荐人客户关系表
						CustRecommendInfoEntity recommendNew = new CustRecommendInfoEntity();
						recommendNew.setCustId(recommendOld.getCustId());
						recommendNew.setQuiltCustId(recommendOld.getQuiltCustId());
						recommendNew.setApplyId(auditId);// 新申请的Id
						recommendNew.setSource(Constant.CUST_RECOMMEND_SOURCE_REGISTER);
						recommendNew.setStartDate(new Timestamp(System.currentTimeMillis()));
						recommendNew.setMemo("解除再新增");
						recommendNew.setBasicModelProperty(userId, true);
						newRecommendList.add(recommendNew);
						quiltCustIdList.add(recommendOld.getQuiltCustId());
					}
					
					// 找出解除业务员关系之后又推荐的用户
					List<CustInfoEntity> listCustInfoEntity = custInfoRepository.findByInviteOriginIdAndCreateDate(custId, latestCustApply.getCreateDate());
					for (CustInfoEntity cie : listCustInfoEntity) {
						
						if(quiltCustIdList.contains(cie.getId())) { // 去除已推荐的用户
							continue;
						}
						
						// BAO推荐人客户关系表
						CustRecommendInfoEntity crie = new CustRecommendInfoEntity();
						crie.setCustId(custId);
						crie.setQuiltCustId(cie.getId());
						crie.setApplyId(auditId);
						crie.setSource(Constant.CUST_RECOMMEND_SOURCE_REGISTER);
						crie.setStartDate(new Timestamp(System.currentTimeMillis()));
						crie.setMemo("");
						crie.setBasicModelProperty(userId, true);
						newRecommendList.add(crie);
						quiltCustIdList.add(cie.getId());
					}
									
				} else {
					// 906修改前的处理
					// 推荐人数
					List<CustInfoEntity> listCustInfoEntity = custInfoRepository.findByInviteOriginId(custId);					
					for (CustInfoEntity cie : listCustInfoEntity) {
						// BAO推荐人客户关系表
						CustRecommendInfoEntity crie = new CustRecommendInfoEntity();
						crie.setCustId(custId);
						crie.setQuiltCustId(cie.getId());
						crie.setApplyId(auditId);
						crie.setSource(Constant.CUST_RECOMMEND_SOURCE_REGISTER);
						crie.setStartDate(new Timestamp(System.currentTimeMillis()));
						crie.setBasicModelProperty(userId, true);
						newRecommendList.add(crie);
						quiltCustIdList.add(cie.getId());
					}

//					StringBuffer sql = new StringBuffer();
//					sql.append("INSERT INTO BAO_T_CUST_RECOMMEND_INFO(ID,CUST_ID,QUILT_CUST_ID,APPLY_ID,SOURCE").append(",START_DATE,RECORD_STATUS,CREATE_DATE,VERSION)").append(" VALUES(?,?,?,?,?,?,?,?,?)");
//	
//					jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
//						public void setValues(PreparedStatement ps, int i) throws SQLException {
//							ps.setString(1, UUID.randomUUID().toString());
//							ps.setString(2, listCustRecommendInfoEntity.get(i).getCustId());
//							ps.setString(3, listCustRecommendInfoEntity.get(i).getQuiltCustId());
//							ps.setString(4, listCustRecommendInfoEntity.get(i).getApplyId());
//							ps.setString(5, listCustRecommendInfoEntity.get(i).getSource());
//							ps.setTimestamp(6, listCustRecommendInfoEntity.get(i).getStartDate());
//							ps.setString(7, listCustRecommendInfoEntity.get(i).getRecordStatus());
//							ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
//							ps.setInt(9, 0);
//						}
//	
//						public int getBatchSize() {
//							return listCustRecommendInfoEntity.size();
//						}
//					});
				}
				
				List<CustRecommendInfoEntity> recommendList = Lists.newArrayList();
				// 判断推荐的用户是否存在客户转移
				Map<String, Object> quiltCustIdMap = Maps.newConcurrentMap();
				quiltCustIdMap.put("recordStatus", Constant.VALID_STATUS_VALID);
				quiltCustIdMap.put("quiltCustIdList", quiltCustIdList);
				List<Map<String, Object>> existsQuiltCustIdList = Lists.newArrayList();
				if(quiltCustIdList.size() > 0) {
					existsQuiltCustIdList = custRecommendInfoRepositoryCustom.queryTransferByQuiltCustId(quiltCustIdMap);
				}
				if(existsQuiltCustIdList != null && existsQuiltCustIdList.size() > 0) {	
					for(CustRecommendInfoEntity c : newRecommendList){
						boolean isFound = false;
						for(Map<String, Object> m : existsQuiltCustIdList) {
							if(m.get("quiltCustId").toString().equals(c.getQuiltCustId())) {
								isFound = true;
								break;
							}
						}
						if(!isFound) {
							recommendList.add(c);
						}
					}
				}
				else {
					recommendList.addAll(newRecommendList);
				}
				
				custRecommendInfoRepositoryCustom.batchUpdateRecommend(recommendList);
				// update by liyy @2016/04/12 bug 906 End
			} else {
				// 审核信息表
				caie.setApplyStatus(Constant.AUDIT_STATUS_REfUSE);
				caie.setRecordStatus(Constant.VALID_STATUS_INVALID);

				auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_REfUSE);
				auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_04);
				auditInfoEntity.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));
//				cieUpdate.setIsRecommend(Constant.IS_RECOMMEND_NO);

			}
			auditInfoRepository.save(auditInfoEntity);
			custApplyInfoRepository.save(caie);
			custInfoRepository.save(cieUpdate);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SLException("系统异常");
		}finally{
			mainLock.unlock();
		}

		return new ResultVo(true);
	}

	/**
	 * 添加金牌推荐人
	 * 
	 * @param param
	 * @return
	 */

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo createOper(Map<String, Object> param) throws SLException {
		CustApplyInfoEntity caie = null;
		try {
			String custId = (String) param.get("ids");
			String mone = (String) param.get("meno");
			String salesmanType = (String) param.get("auditStatus");

			CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
			if (null != custInfoEntity && Constant.IS_RECOMMEND_YES.equals(custInfoEntity.getIsRecommend()))
				return new ResultVo(false, "该用户已经是业务员");
			/*UserEntity ue = userRepository.findUserByCredentialsCode(custInfoEntity.getCredentialsCode());
			if (StringUtils.isEmpty(ue)) 
				return new ResultVo(false, "该用户不是内部员工，请确认");*/
			Map<String, Object> employeeParams = Maps.newConcurrentMap();
			employeeParams.put("credentialsCode", custInfoEntity.getCredentialsCode());
			employeeParams.put("custName", custInfoEntity.getCustName());
			if(!custInfoRepository.judgeUserIsEmployee(employeeParams)) {
				return new ResultVo(false, "该用户不是内部员工，请确认");
			}
			
			// update by liyy @2016/04/12 bug 906 Start
//			AuditInfoEntity auditInfo = auditInfoRepository.findFirstByCustIdAndApplyTypeAndRecordStatusOrderByApplyTimeDesc(custInfoEntity.getId(), Constant.OPERATION_TYPE_23, Constant.VALID_STATUS_VALID);
//			if (null != auditInfo && (Arrays.asList(Constant.AUDIT_STATUS_REVIEWD_RECOMMEND, Constant.AUDIT_STATUS_PASS_RECOMMEND).contains(auditInfo.getAuditStatus())))
//				return new ResultVo(false, "该用户已经提交过业务员申请");
			
			// 审核信息表
			//CustApplyInfoEntity caie = new CustApplyInfoEntity();
			// 检查是否存在解除记录
			List<CustApplyInfoEntity> appList =  custApplyInfoRepository.findByCustIdAndApplyTypeAndRecordStatusOrderByCreateDateDesc(custInfoEntity.getId(), Constant.OPERATION_TYPE_23, Constant.VALID_STATUS_VALID);
			// 如果有记录
			if(appList != null && appList.size() > 0) {
				CustApplyInfoEntity applyInfo = appList.get(0);
				if (Constant.AUDIT_STATUS_REVIEWD_RECOMMEND.equals(applyInfo.getApplyStatus())) {
					throw new SLException("该用户已经提交过业务员申请，并且非解除状态，不能再次申请");
				} else if(Constant.AUDIT_STATUS_PASS_RECOMMEND.equals(applyInfo.getApplyStatus())) {
					throw new SLException("该用户已经是业务员，不能再次申请");
				} else if(Constant.AUDIT_STATUS_PASS_RELIEVE.equals(applyInfo.getApplyStatus())) {
					//String applyIdOld = applyInfo.getId(); // 解除对应的 申请表的Id
					
					//caie.setMemo(Constant.AUDIT_STATUS_PASS_RELIEVE + ":" + applyIdOld );
					
				} else if(Constant.AUDIT_STATUS_REfUSE_RECOMMEND.equals(applyInfo.getApplyStatus())) {
					// Do nothing
				} else {
					throw new SLException("该用户提交过业务员申请，并且非解除状态，不能再次申请");
				}
			}
			// update by liyy @2016/04/12 bug 906 End
			
//			// 审核信息表 -> 移动一下位置（向上看）
			caie = new CustApplyInfoEntity();
			caie.setCustId(custId);
			caie.setApplyType(Constant.OPERATION_TYPE_23);
			caie.setApplyStatus(Constant.AUDIT_STATUS_REVIEWD_RECOMMEND);
			caie.setApplyDesc(mone);
			caie.setApplyDate(new Date());
			caie.setCreateDate(new Date());
			caie.setRecordStatus(Constant.VALID_STATUS_VALID);
			caie.setSalesmanType(salesmanType);
			caie.setApplyNo(flowNumberService.generateRecCustApplyNo());
			caie.setBasicModelProperty((String) param.get("userId"), true);
			caie = custApplyInfoRepository.save(caie);

			AuditInfoEntity auditInfoEntity = new AuditInfoEntity(custInfoEntity.getId(), Constant.TABLE_BAO_T_CUST_APPLY_INFO, caie.getId(), Constant.OPERATION_TYPE_23, new Date(), Constant.AUDIT_STATUS_REVIEWD_RECOMMEND, null);
			auditInfoEntity.setBasicModelProperty(custInfoEntity.getId(), true);
			auditInfoRepository.save(auditInfoEntity);

			LogInfoEntity recLog = new LogInfoEntity(Constant.TABLE_BAO_T_CUST_INFO, custInfoEntity.getId(), Constant.OPERATION_TYPE_23, null, null, caie.getApplyDesc(), custInfoEntity.getId());
			recLog.setBasicModelProperty(caie.getId(), true);
			// recLog.setOperIpaddress((String) param.get("operIpaddress"));
			logInfoEntityRepository.save(recLog);

			// update by liyy @2016/04/13 申请的时候不需要更新吧！
//			custInfoEntity.setIsRecommend(Constant.IS_RECOMMEND_YES);
//			custInfoRepository.save(custInfoEntity);

		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new SLException("" + e.getMessage());
		}  
		return new ResultVo(true, "操作成功", caie.getId());
	}
	
	/**
	 * 解除操作
	 * @param param
	 * @return ResultVo
	 * @throws SLException 
	 */
	@Rules(rules = { 
			@Rule(name = "applyId", required = true, requiredMessage = "申请Id不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户Id不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "操作人ID不能为空!")
	})
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo relieveReferee(Map<String, Object> param) throws SLException {
		String applyId = param.get("applyId").toString();
		String custId = param.get("custId").toString();
		String userId = param.get("userId").toString();
		
		// 申请表 状态更新
		CustApplyInfoEntity custApplyInfoEntity = custApplyInfoRepository.findOne(applyId);
		if(custApplyInfoEntity == null){
			throw new SLException("申请记录不存在");
		} else if(Constant.AUDIT_STATUS_PASS_RELIEVE.equals(custApplyInfoEntity.getApplyStatus())) {
			throw new SLException("该记录已经解除，请刷新数据");
		}
		custApplyInfoEntity.setApplyStatus(Constant.AUDIT_STATUS_PASS_RELIEVE);
		custApplyInfoEntity.setBasicModelProperty(userId, false);
		
		// 审核表 状态更新
		AuditInfoEntity auditInfoEntity = auditInfoRespository.findByRelatePrimary(applyId);
		if(auditInfoEntity == null){
			throw new SLException("审核记录不存在");
		} else if(Constant.AUDIT_STATUS_PASS_RELIEVE.equals(auditInfoEntity.getAuditStatus())) {
			throw new SLException("该记录已经解除，请刷新数据");
		}
		String beforeAuditStatus = auditInfoEntity.getAuditStatus();
		auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_PASS_RELIEVE);
		auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03);
		auditInfoEntity.setBasicModelProperty(userId, false);
		
		// 客户信息 身份更新
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null){
			throw new SLException("客户不存在不存在");
		}
		custInfoEntity.setIsRecommend("");
		custInfoEntity.setIsEmployee("");
		custInfoEntity.setWorkingState(Constant.WORKING_STATE_OUT);
		custInfoEntity.setBasicModelProperty(userId, false);
		
		// 对应关系表 关系解除(将所有recommend的备注字段都改为申请ID，方便后续找解除关系)
		custRecommendInfoRepository.setRelieveByCustId(custId, userId, new Date(), applyId);
		
		// 日志表 追加日志
		LogInfoEntity recLog = new LogInfoEntity(Constant.TABLE_BAO_T_CUST_APPLY_INFO, custApplyInfoEntity.getId(), "业务员解除", beforeAuditStatus, Constant.AUDIT_STATUS_PASS_RELIEVE, Constant.AUDIT_STATUS_PASS_RELIEVE, userId);
		recLog.setBasicModelProperty(userId, true);
		logInfoEntityRepository.save(recLog);
		
		return new ResultVo(true, "解除成功");
	}
	
	/**
	 * 标记客户为业务员
	 * 
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo createEmployee(Map<String, Object> paramsMap) throws SLException {
		
		String custId = (String)paramsMap.get("custId");
		
		Map<String, Object> createParam = Maps.newHashMap();
		createParam.put("ids", custId);
		createParam.put("meno", "自动认证");
		createParam.put("auditStatus", "内部员工");
		ResultVo resultVo = createOper(createParam);
		
		if(ResultVo.isSuccess(resultVo)) {
			Map<String, Object> auditParam = Maps.newHashMap();
			auditParam.put("auditStatus", Constant.AUDIT_STATUS_PASS);
			auditParam.put("auditId", (String)resultVo.getValue("data"));
			auditParam.put("memo", "实名认证时自动认证");
			auditParam.put("userId", custId);
			resultVo = auditOper(auditParam);
		}
		
		/*// 生成客户申请
		CustApplyInfoEntity caie = new CustApplyInfoEntity();
		caie.setCustId(custId);
		caie.setApplyType(Constant.OPERATION_TYPE_23);
		caie.setApplyStatus(Constant.AUDIT_STATUS_PASS_RECOMMEND);
		caie.setApplyDesc("自动认证");
		caie.setApplyDate(new Date());
		caie.setCreateDate(new Date());
		caie.setRecordStatus(Constant.VALID_STATUS_VALID);
		caie.setSalesmanType("内部员工");
		caie.setApplyNo(flowNumberService.generateRecCustApplyNo());
		caie.setBasicModelProperty(custId, true);
		caie = custApplyInfoRepository.save(caie);
		
		// 生成客户与业务员关系
		List<CustInfoEntity> listCustInfoEntity = custInfoRepository.findByInviteOriginId(custId);
		List<CustRecommendInfoEntity> custRecommendList = Lists.newArrayList();
		for (CustInfoEntity cie : listCustInfoEntity) {
			// BAO推荐人客户关系表
			CustRecommendInfoEntity crie = new CustRecommendInfoEntity();
			crie.setCustId(custId);
			crie.setQuiltCustId(cie.getId());
			crie.setApplyId(caie.getId());
			crie.setSource(Constant.CUST_RECOMMEND_SOURCE_REGISTER);
			crie.setStartDate(new Timestamp(System.currentTimeMillis()));
			crie.setBasicModelProperty(custId, true);
			custRecommendList.add(crie);
		}
		custRecommendInfoRepositoryCustom.batchUpdateRecommend(custRecommendList);
		
		// 生成审核信息
		AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
		auditInfoEntity.setCustId(custId);
		auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_APPLY_INFO);
		auditInfoEntity.setRelatePrimary(caie.getId());
		auditInfoEntity.setApplyType(Constant.OPERATION_TYPE_23);
		auditInfoEntity.setApplyTime(new Date());
		auditInfoEntity.setAuditTime(new Date());
		auditInfoEntity.setAuditUser("1");
		auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_PASS_RECOMMEND);
		auditInfoEntity.setTradeStatus(Constant.VALID_STATUS_VALID);
		auditInfoEntity.setBasicModelProperty(custId, true);
		auditInfoRepository.save(auditInfoEntity);
		
		// 修改客户信息
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		custInfoEntity.setIsRecommend(Constant.IS_RECOMMEND_YES);
		custInfoEntity.setIsEmployee(Constant.IS_RECOMMEND_YES);
		custInfoEntity.setWorkingState(Constant.WORKING_STATE_IN);
		custInfoEntity.setBasicModelProperty(custId, false);

		// 生成日志信息
		LogInfoEntity recLog = new LogInfoEntity(Constant.TABLE_BAO_T_CUST_INFO, custId, Constant.OPERATION_TYPE_23, null, null, caie.getApplyDesc(), custId);
		recLog.setBasicModelProperty(caie.getId(), true);
		logInfoEntityRepository.save(recLog);*/
		
		return resultVo;
	}
	
	/**
	 * 业务员离职
	 * 背景：业务员离职后其业绩仍需统计，但不允许业务员再使用本系统。
	 * 问题：为了统计离职业务员业绩，业务员统计业绩在每月1号凌晨1点触发；通过人事系统对业务员做离职操作在每月1号17点触发；
	 *     故业务员虽已离职但仍可以登录本系统，现需要对业务员做标记，表示其已离职，不允许登录系统，但在系统中其身份仍为业务员，
	 *     直到离职操作触发。
	 * 解决：在CustInfoEntity中新增WorkingState字段表示在职或离职。
	 *     每天凌晨1点触发本任务，将系统中离职的业务员，其WorkingState标记为离职。
	 * 影响：业务员离职WorkingState标记为离职，若该业务员再入职须将WorkingState标记为在职，否则无法登录。
	 *     故须修改业务员申请审核成为业务员、数据库定时Job自动添加业务员、业务员实名认证之后自动成为业务员。
	 * 
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo leaveJob(Map<String, Object> params) throws SLException {
		
		custInfoRepository.leaveJob(params);
		
		return new ResultVo(true);
	}
}
