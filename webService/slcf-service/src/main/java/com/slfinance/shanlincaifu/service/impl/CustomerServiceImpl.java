package com.slfinance.shanlincaifu.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.ActivityInfoEntity;
import com.slfinance.shanlincaifu.entity.AttachmentInfoEntity;
import com.slfinance.shanlincaifu.entity.ContactInfoEntity;
import com.slfinance.shanlincaifu.entity.CustApplyInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.CustRecommendInfoEntity;
import com.slfinance.shanlincaifu.entity.ExpandInfoEntity;
import com.slfinance.shanlincaifu.entity.InterfaceDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.LoginLogInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductInfoEntity;
import com.slfinance.shanlincaifu.entity.SmsInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.ActivityInfoRepository;
import com.slfinance.shanlincaifu.repository.AtoneInfoRepository;
import com.slfinance.shanlincaifu.repository.AttachmentRepository;
import com.slfinance.shanlincaifu.repository.CustActivityInfoRepository;
import com.slfinance.shanlincaifu.repository.CustApplyInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.CustRecommendInfoRepository;
import com.slfinance.shanlincaifu.repository.ExpandInfoRepository;
import com.slfinance.shanlincaifu.repository.InterfaceDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.LoginLogInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductInfoRepository;
import com.slfinance.shanlincaifu.repository.SmsInfoRepository;
import com.slfinance.shanlincaifu.repository.TradeFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.AccountFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.AccountInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.AtoneInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.CustActivityInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.impl.CustInfoRepositoryImpl;
import com.slfinance.shanlincaifu.repository.searchFilter.DynamicSpecifications;
import com.slfinance.shanlincaifu.repository.searchFilter.SearchFilter;
import com.slfinance.shanlincaifu.repository.searchFilter.SearchFilter.Operator;
import com.slfinance.shanlincaifu.service.CustActivityInfoService;
import com.slfinance.shanlincaifu.service.CustomerService;
import com.slfinance.shanlincaifu.service.DeviceService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.OpenSerivceCode;
import com.slfinance.shanlincaifu.utils.PropertiesLoader;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.shanlincaifu.utils.UserUtils;
import com.slfinance.shanlincaifu.validate.RuleUtils;
import com.slfinance.util.BeanMapConvertUtil;
import com.slfinance.vo.ResultVo;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

@Slf4j
@Service("customerService")
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {
	@Autowired
	private CustInfoRepositoryImpl custInfoRepositoryImpl;

	@Autowired
	private SmsInfoRepository smsInfoRepository;

	@Autowired
	private SMSService sMSService;

	@Autowired
	private CustInfoRepository custInfoRepository;

	@Autowired
	private LoginLogInfoRepository loginLogInfoRepository;

	@Autowired
	private TradeFlowInfoRepository tradeFlowInfoRepository;

	@Autowired
	private InvestInfoRepository investInfoRepository;

	@Autowired
	private AccountFlowInfoRepositoryCustom accountFlowInfoRepositoryCustom;

	@Autowired
	private AccountInfoRepositoryCustom accountInfoRepositoryCustom;

	@Autowired
	private AccountInfoRepository accountInfoRepository;

	@Autowired
	private RepositoryUtil repositoryUtil;

	@Autowired
	private FlowNumberService flowNumberService;

	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;

	@Autowired
	private ParamService paramService;

	@Autowired
	private AtoneInfoRepository atoneInfoRepository;

	@Autowired
	private ProductInfoRepository productInfoRepository;

	@Autowired
	private LoanInfoRepositoryCustom loanInfoRepositoryCustom;

	@Autowired
	private AtoneInfoRepositoryCustom atoneInfoRepositoryCustom;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private InterfaceDetailInfoRepository interfaceDetailInfoRepository;
	
	@Autowired
	private ExpandInfoRepository expandInfoRepository;
	
	@Autowired
	private AttachmentRepository attachmentRepository;
	
	@Autowired
	private CustApplyInfoRepository custApplyInfoRepository;
	
	@Autowired
	private CustRecommendInfoRepository custRecommendInfoRepository;
	
	@Autowired
	private CustActivityInfoService custActivityInfoService;
	
	@Autowired
	private CustActivityInfoRepository custActivityInfoRepository;
	 
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private CustActivityInfoRepositoryCustom custActivityInfoRepositoryCustom;
	
	private static String [] excludeInviteCodePrefix = { "swp", "tmp", "bak", "pwd", "etc"};
	
	@Autowired
	private StringRedisTemplate redisTemplate2;

	@Autowired
	private ActivityInfoRepository activityInfoRepository;
	
	@Autowired
	private SMSService smsService;
	
	/**
	 * 设置、修改 登录交易密码
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo updatePasswordByType(Map<String, Object> params) {
		String type = (String) params.get("type");
		String id = (String) params.get("id");
		CustInfoEntity cie = custInfoRepository.findOne(id);
		if (null == cie)
			return new ResultVo(false, "没有数据！");
		// 新密码
		String newPassword = (String) params.get("password");
		String oldPassword = (String) params.get("oldPassword");
		if (newPassword.equals(oldPassword))
			return new ResultVo(false, "新密码不能和旧密码相同！");
		if (null == oldPassword)
			return new ResultVo(false, "oldPassword没有数据！");

		if (type.equals(Constant.PASS_TYPE_LOGIN)) {
			if (!cie.getLoginPassword().equals(oldPassword))
				return new ResultVo(false, "登录原始密码错误！");
		} else if (type.equals(Constant.PASS_TYPE_TRADE)) {
			// String oldPassTemp = Hashing.md5().hashString(oldPassword,
			// Charsets.UTF_8).toString();
			if (!oldPassword.equals(cie.getTradePassword())
					&& !"".equals(cie.getTradePassword())) {
				return new ResultVo(false, "原交易密码错误");
			}

			// if (StringUtils.isEmpty(cie.getTradePassword())) {
			// cie.setTradePassword(Hashing.md5().hashString(newPassword,
			// Charsets.UTF_8).toString());
			// } else {
			// if
			// (cie.getTradePassword().equals(Hashing.md5().hashString(oldPassword,
			// Charsets.UTF_8).toString())) {
			// cie.setTradePassword(Hashing.md5().hashString(newPassword,
			// Charsets.UTF_8).toString());
			// }
			// }
		}
		// 密码校验顺序调整 bug SLB-75
		// 2、修改登录密码时，除页面js验证外，先验证原登录密码是否正确，然后再验证是否跟交易密码，登录密码相同
		// 新密码
		// String newPasswordTemp = Hashing.md5().hashString(newPassword,
		// Charsets.UTF_8).toString();
		if (newPassword.equals(cie.getLoginPassword())
				&& type.equals(Constant.PASS_TYPE_TRADE)) {
			return new ResultVo(false, "交易密码不能和登录密码相同！");
		} else if (newPassword.equals(cie.getTradePassword())
				&& type.equals(Constant.PASS_TYPE_LOGIN)) {
			return new ResultVo(false, " 登录密码不能和交易密码相同！");
		} else {
			if (type.equals(Constant.PASS_TYPE_TRADE))
				cie.setTradePassword(newPassword);
			if (type.equals(Constant.PASS_TYPE_LOGIN))
				cie.setLoginPassword(newPassword);
		}
		if (!StringUtils.isEmpty(params.get("loginPwdLevel")))
			cie.setLoginPwdLevel((String) params.get("loginPwdLevel"));
		if (!StringUtils.isEmpty(params.get("tradePwdLevel")))
			cie.setTradePwdLevel((String) params.get("tradePwdLevel"));
		custInfoRepository.save(cie);
		
		// 新增记录日志 add by wangjf 2015-07-30
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
		logInfoEntity.setRelatePrimary(cie.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_19);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setOperPerson(cie.getId());
		logInfoEntity.setMemo(String.format("%s修改了%s",
				cie.getLoginName(), type.equals(Constant.PASS_TYPE_LOGIN) ? "登录密码" : "交易密码"));
		logInfoEntity.setBasicModelProperty(cie.getId(), true);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true, "处理成功");
	}

	/**
	 * 找回交易密码
	 * 
	 * @param params
	 * @return
	 * @throws SLException
	 */
	// @Transactional
	// public ResultVo updateTradePassword(Map<String, Object> params) throws
	// SLException {
	// String tradePassword = (String) params.get("tradePassword");
	// String id = (String) params.get("id");
	// String smsCode = (String) params.get("smsCode");
	// CustInfoEntity cie = custInfoRepository.findOne(id);
	// HashMap<String, Object> param = new HashMap<String, Object>();
	// param.put("verityCode", smsCode);
	// param.put("messageType", Constant.SMS_TYPE_TRADE_PASSWD);
	// param.put("targetAddress", cie.getMobile());
	// try {
	// ResultVo rv = sMSService.checkVerityCode(param);
	// if (ResultVo.isSuccess(rv)) {
	// // cie.setTradePassword(Hashing.md5().hashString(tradePassword,
	// // Charsets.UTF_8).toString());
	//
	// if (tradePassword.equals(cie.getLoginPassword())) {
	// return new ResultVo(false, "交易密码不能和登录密码一致！");
	// }
	//
	// cie.setTradePassword(tradePassword);
	// } else {
	// return new ResultVo(false, "操作失败");
	// }
	// custInfoRepository.save(cie);
	// } catch (Exception e) {
	// return new ResultVo(false, "操作失败");
	// }
	// return new ResultVo(true, "操作成功");
	//
	// }

	/**
	 * 设置交易密码
	 * 
	 * @author mzhu
	 * @param custInfoMap
	 *            <tt>mobile： String:手机号码</tt><br>
	 *            <tt>tradePassword： String:交易密码</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo updateTradePasswrod(Map<String, Object> params) {
		String mobile = (String) params.get("mobile");
		String tradePasswrod = (String) params.get("tradePassword");
		String smsCode = (String) params.get("smsCode");
		if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(tradePasswrod)) {
			return new ResultVo(false, "手机号或交易密码为空");
		}
		CustInfoEntity custInfoEntity = custInfoRepository.findByMobile(mobile);
		if (StringUtils.isEmpty(custInfoEntity))
			return new ResultVo(false, "不存在该用户");

		// String newPasswordTemp = Hashing.md5().hashString(tradePasswrod,
		// Charsets.UTF_8).toString();
		String newPasswordTemp = tradePasswrod;
		if (newPasswordTemp.equals(custInfoEntity.getLoginPassword()))
			return new ResultVo(false, "交易密码不能和登录密码相同！");
		// if (StringUtils.isEmpty(custInfoEntity)) {
		// return new ResultVo(false, "不存在该用户");
		// }
		// custInfoEntity.setTradePassword(Hashing.md5().hashString(tradePasswrod,
		// Charsets.UTF_8).toString());
		custInfoEntity.setTradePassword(tradePasswrod);
		if (!StringUtils.isEmpty(params.get("tradePwdLevel")))
			custInfoEntity.setTradePwdLevel((String) params
					.get("tradePwdLevel"));
		custInfoRepository.save(custInfoEntity);
		updataOldSMSStatus(mobile, Constant.SMS_TYPE_TRADE_PASSWD, smsCode);
		
		// 新增记录日志 add by wangjf 2015-07-30
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
		logInfoEntity.setRelatePrimary(custInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_19);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setOperPerson(custInfoEntity.getId());
		logInfoEntity.setMemo(String.format("%s设置了交易密码",
				custInfoEntity.getLoginName()));
		logInfoEntity.setBasicModelProperty(custInfoEntity.getId(), true);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true, "操作成功");
	}

	/**
	 * 
	 * 设置密码
	 */
	public ResultVo setLoginPassword(Map<String, Object> params) {
		String mobile = params.get("mobile") + "";

		String passwd = params.get("loginPasswd") + "";
		
		CustInfoEntity cie = custInfoRepository.findByMobile(mobile);
		cie.setLoginPassword(Hashing.md5().hashString(passwd, Charsets.UTF_8)
				.toString());
		custInfoRepository.save(cie);
		
		// 新增记录日志 add by wangjf 2015-07-30
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
		logInfoEntity.setRelatePrimary(cie.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_19);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setOperPerson(cie.getId());
		logInfoEntity.setMemo(String.format("%s设置了登录密码",
				cie.getLoginName()));
		logInfoEntity.setBasicModelProperty(cie.getId(), true);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true, "操作成功");
	}

	/**
	 * 更新绑定手机
	 * 
	 * mzhu
	 * 
	 * @param params
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo updateBindMobile(Map<String, Object> params) {
		String mobile = (String) params.get("mobile");
		String id = (String) params.get("id");
		String smsCode = (String) params.get("smsCode");
		CustInfoEntity cie = custInfoRepository.findOne(id);
		String oldMobile = "";
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("verityCode", smsCode);
		param.put("messageType", Constant.SMS_TYPE_BINDING_NEW_MOBILE);
		param.put("targetAddress", mobile);
		if (!StringUtils.isEmpty(cie)) {
			oldMobile = cie.getMobile();
			if (mobile.equals(cie.getMobile()))
				return new ResultVo(false, "手机号不能相同");
		}

		try {
			if (mobile.equals(cie.getMobile()))
				return new ResultVo(false, "手机不能重复绑定");
			ResultVo rv = sMSService.checkVerityCode(param);
			if (ResultVo.isSuccess(rv)) {
				cie.setMobile(mobile);
			} else {
				return new ResultVo(false, rv.getValue("message"));
			}
			if (!StringUtils.isEmpty(cie)) {
				custInfoRepository.save(cie);
			}
			// 更新旧的短信状态
			updataOldSMSStatus(oldMobile, Constant.SMS_TYPE_UPDATE_MOBILE,
					smsCode);
			
			// 新增记录日志 add by wangjf 2015-07-30
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
			logInfoEntity.setRelatePrimary(cie.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_19);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setOperPerson(cie.getId());
			logInfoEntity.setMemo(String.format("%s手机由%s变更为%s",
					cie.getLoginName(), oldMobile, mobile));
			logInfoEntity.setBasicModelProperty(cie.getId(), true);
			logInfoEntityRepository.save(logInfoEntity);
			
		} catch (Exception e) {
			return new ResultVo(false, "操作失败");
		}
		return new ResultVo(true, "操作成功");
	}

	/**
	 * 更新旧的短信状态
	 * 
	 * @param oldMobile
	 * @param smsType
	 * @throws SLException
	 */
	private void updataOldSMSStatus(String oldMobile, String smsType,
			String smsCode) {
		List<SmsInfoEntity> listSmsInfoEntity = smsInfoRepository
				.findByTargetAddressAndTargetTypeOrderBySendDateDesc(oldMobile,
						smsType);
		if (null != listSmsInfoEntity && listSmsInfoEntity.size() > 0) {
			SmsInfoEntity smsInfoEntity = listSmsInfoEntity.get(0);
			try {
				sMSService.updateVerificationCodeStatus(smsInfoEntity);
			} catch (SLException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}

		// for (SmsInfoEntity smsInfoEntity : listSmsInfoEntity) {
		// if (smsCode.equals(smsInfoEntity.getVerityCode())) {
		// try {
		// sMSService.updateVerificationCodeStatus(smsInfoEntity);
		// } catch (SLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// throw new RuntimeException(e.getMessage());
		// }
		// }
		//
		// }
	}

	public ResultVo findByLoginName(Map<String, Object> params) {
		CustInfoEntity cie = custInfoRepository.findByLoginName(params
				.get("loginName") + "");
		if (cie == null) {
			return new ResultVo(false, "没有数据！");
		} else {
			return new ResultVo(true, "查询成功", cie);
		}
	}

	public ResultVo findByMobile(Map<String, Object> params) {
		CustInfoEntity cie = custInfoRepository.findByMobile(params
				.get("mobile") + "");
		if (cie == null) {
			return new ResultVo(false, "没有数据！");
		} else {
			return new ResultVo(true, "查询成功", cie);
		}
	}

	public ResultVo findByLoginNameAndCredentialsCode(Map<String, Object> params) {
		String loginName = params.get("loginName") + "";
		String credentialsCode = params.get("credentialsCode") + "";
		CustInfoEntity cie = custInfoRepository
				.findByLoginNameAndCredentialsCode(loginName, credentialsCode);
		if (cie == null) {
			return new ResultVo(false, "没有数据！");
		} else {
			return new ResultVo(true, "查询成功", cie);
		}
	}

	public ResultVo findByCondition(Map<String, Object> param) {
		List<SearchFilter> filters = Lists.newArrayList();

		if (!StringUtils.isEmpty(param.get("tradeType")))
			filters.add(new SearchFilter("tradeType", Operator.EQ, param
					.get("tradeType")));

		if (!StringUtils.isEmpty(param.get("tradeStatus")))
			filters.add(new SearchFilter("tradeStatus", Operator.EQ, param
					.get("tradeStatus")));

		if (param.get("beginDate") != null)
			filters.add(new SearchFilter("tradeDate", Operator.GT, param
					.get("beginDate")));

		if (param.get("endDate") != null)
			filters.add(new SearchFilter("tradeDate", Operator.LT, param
					.get("endDate")));

		Specification<CustInfoEntity> spec = DynamicSpecifications
				.bySearchFilter(filters, CustInfoEntity.class);
		List<CustInfoEntity> l = custInfoRepository.findAll(spec);
		return new ResultVo(true, "操作成功", l);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo register(Map<String, Object> params) {
		ResultVo resultVo = innerRegister(params);
		
		if(ResultVo.isSuccess(resultVo)) { // 注册成功记录设备信息
			CustInfoEntity custInfoEntity = custInfoRepository.findByMobile((String)params.get("mobile"));			
			if(params.containsKey("channelNo")) { // 若存在渠道号，需判断该渠道来源是否需要回调通知
				InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByInterfaceMerchantCode(Constant.OPERATION_TYPE_16, (String)params.get("channelNo"));
				if(interfaceDetailInfoEntity != null && "Y".equals(interfaceDetailInfoEntity.getIsNotice())) {
					switch(interfaceDetailInfoEntity.getThirdPartyType()) {
					case Constant.THIRD_PARTY_TYPE_JIRONGTONG: 
						{
							// 活动6：吉融需判断活动是否有效，有效需同步（记录Expand），否则无需同步（不记Expand）
							params.put("activityId", Constant.ACTIVITY_ID_REGIST_06);
							params.put("custInfoEntity", custInfoEntity);
							params.put("interfaceDetailInfoEntity", interfaceDetailInfoEntity);
							custActivityInfoService.custActivityRecommend(params);
							break;
						}					
					default:
						{
							ExpandInfoEntity expandInfoEntity = new ExpandInfoEntity();
							expandInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_CUST_INFO);
							expandInfoEntity.setRelatePrimary(custInfoEntity.getId());
							expandInfoEntity.setInnerTradeCode(flowNumberService.generateOpenServiceTradeNumber());
							expandInfoEntity.setTradeCode((String)params.get("utid"));
							expandInfoEntity.setThirdPartyType(interfaceDetailInfoEntity.getThirdPartyType());
							expandInfoEntity.setInterfaceType(interfaceDetailInfoEntity.getInterfaceType());
							expandInfoEntity.setMerchantCode(interfaceDetailInfoEntity.getMerchantCode());
							expandInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
							expandInfoEntity.setAlreadyNotifyTimes(0);
							expandInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
							expandInfoEntity.setMemo("waiting");// 此标识表示需要同步
							expandInfoEntity.setMeId((String)params.get("meId"));
							expandInfoEntity.setMeVersion((String)params.get("meVersion"));
							expandInfoEntity.setAppSource(custInfoEntity.getCustSource());
							expandInfoRepository.save(expandInfoEntity);
						}
					}
				}
			}
		}

		return resultVo;
	}

	/**
	 * mzhu 手机登录
	 * 
	 * @param param
	 * @return
	 */
	@Transactional(readOnly = true, rollbackFor = SLException.class)
	public ResultVo loginMobile(Map<String, Object> param) {
		String mobile = (String) param.get("mobile");
		String password = (String) param.get("loginPassword");
		CustInfoEntity cie = custInfoRepository.findByMobile(mobile);
		if (null == cie)
			return new ResultVo(false, "手机号或密码错误!");
		else if (!password.equals(cie.getLoginPassword()))
			return new ResultVo(false, "手机号或密码错误!");
		return new ResultVo(true, "登录成功", cie);
	}

	public Long findTotalSpreadLevel() {
		StringBuffer sqlString = new StringBuffer()
				.append(String
						.format("SELECT COUNT(*) as totalSpreadLevel FROM  BAO_T_CUST_INFO WHERE SPREAD_LEVEL='0'"));
		List<Map<String, Object>> result = repositoryUtil.queryForMap(
				sqlString.toString(), null);
		Map<String, Object> param = result.get(0);
		BigDecimal total = (BigDecimal) param.get("totalSpreadLevel");
		return total.add(new BigDecimal("1")).longValue();
	}

	/*
	 * 查询QUERYCODE
	 */
	public String findQueryCode(String spreadLevel) {
		StringBuffer sqlString = new StringBuffer().append(String.format(
				"select GET_QUERY_CODE('%s')  as QUERYCODE from dual",
				spreadLevel));

		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				sqlString.toString(), new Object[] {});
		if (list == null || list.size() == 0)
			return "";
		Map<String, Object> map = list.get(0);
		if (map == null || map.size() == 0)
			return "";

		return (String) map.get("QUERYCODE");
	}

	/**
	 * 生产邀请码
	 * 
	 * @param spreadLevel
	 * @return
	 */
	public String findGetInviteCode() {
		StringBuffer sqlString = new StringBuffer()
				.append("select GET_INVITE_CODE()  as inviteCode from dual");
		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				sqlString.toString(), new Object[] {});
		if (list == null || list.size() == 0)
			return "";
		Map<String, Object> map = list.get(0);
		if (map == null || map.size() == 0)
			return "";
		String chars = "acefghijklmnopqrstuvwxyz";
		StringBuilder inviteCodePrefix = null;
		do {
			inviteCodePrefix = new StringBuilder();
			for (int i = 1; i <= 3; i++) {
				inviteCodePrefix.append(chars.charAt((int) (Math.random() * 24)));
			}
			
			// 若生成的验证码前缀为"swp", "tmp", "bak", "pwd", "etc"之一，则重新生成
			if (!Arrays.asList(excludeInviteCodePrefix).contains(inviteCodePrefix.toString())) {
				break;
			}
		} while (true);
		
		return inviteCodePrefix.toString().toUpperCase() + (String) map.get("inviteCode");
		// return (String) map.get("inviteCode");
	}
	
	/**
	 * @author HuangXiaodong 2015-04-17 用于用户登录
	 * @param Map
	 *            <String, Object> mobile: 用户手机号
	 * @return ResultVo
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo login(Map<String, Object> param) {
		String mobile = param.get("mobile") + "";
		if (StringUtils.isEmpty(mobile)) {
			return new ResultVo(false, "手机号不能为空!");
		}
		CustInfoEntity custInfo = custInfoRepository.findByMobile(mobile);
		if (null == custInfo) {
			return new ResultVo(false, "用户不存在!");
		}
		
		ResultVo resultVo = new ResultVo(true);
		resultVo.putValue("data", custInfo);
		//判断 是否参与活动 （集团活动）
		if(!StringUtils.isEmpty(param.get("activityCode")) && Constant.ACTIVITY_ID_REGIST_10.equals(param.get("activityCode").toString())){
			Map<String, Object> paraMap = Maps.newHashMap();
			paraMap.put("mobile", mobile);
			paraMap.put("custId", custInfo.getId());
			paraMap.put("activityCode", CommonUtils.emptyToString(param.get("activityCode")));
			ResultVo vo = custActivityInfoService.custActivityInvestRecommendByLogin(paraMap);
			if (ResultVo.isSuccess(vo)) {
				resultVo.putValue("activity", "成功");
			}
		}
		
		//判断 是否参与活动 （市场部棋王争霸赛活动）
		if(!StringUtils.isEmpty(param.get("activityCode")) && Constant.ACTIVITY_ID_REGIST_14.equals(param.get("activityCode").toString())){
			Map<String, Object> paraMap = Maps.newHashMap();
			paraMap.put("mobile", mobile);
			paraMap.put("custId", custInfo.getId());
			paraMap.put("activityId", Constant.ACTIVITY_ID_REGIST_14_LOGIN);
			ResultVo vo = custActivityInfoService.custActivityRecommend(paraMap);
			if (ResultVo.isSuccess(vo)) {
				resultVo.putValue("activity", "成功");
			}
		}
		
		//判断是否是微信公众号推送
		if(!StringUtils.isEmpty(param.get("openid"))){
				Map<String, Object> paraMap = Maps.newHashMap();
				paraMap.put("mobile", mobile);
				paraMap.put("openId",param.get("openid"));
				custInfoRepository.updateOpenIdByMobile(paraMap);
		}
		return resultVo;
	}

	/**
	 * @author HuangXiaodong 2015-04-17 用于用户登录记录登录日志用
	 * @param Map
	 *            <String, Object> custId: 用户id loginIp：登录ip
	 * @return ResultVo
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo recordLoginLog(Map<String, Object> param) {
		try {
			String custId = param.get("custId") + "";
			if (StringUtils.isEmpty(custId)) {
				return new ResultVo(false, "用户id不能为空!");
			}
			CustInfoEntity custInfo = custInfoRepository.findOne(custId);
			if (null == custInfo) {
				return new ResultVo(false, "用户不存在!");
			}

			// 记录日志
			LoginLogInfoEntity loginfo = new LoginLogInfoEntity();
			loginfo.setCustId(custId);
			loginfo.setLoginDate(new Date());
			loginfo.setLoginIp((String)param.get("loginIp"));
			loginfo.setAppSource((String)param.get("appSource"));
			loginfo.setMeId((String)param.get("meId"));
			loginfo.setMeVersion((String)param.get("meVersion"));
			loginfo.setAppVersion((String)param.get("appVersion"));
			loginLogInfoRepository.save(loginfo);

			LogInfoEntity lie = new LogInfoEntity();
			// 创建时间
			lie.setCreateDate(new Date());
			// 外键
			lie.setRelatePrimary(custId);
			// 操作日志
			lie.setOperDesc(custInfo.getLoginName() + "登录系统！");
			// 被操作人id
			lie.setOperPerson(custId);
			// 操作人 id
			lie.setCreateUser(custId);
			// 日志类型
			lie.setLogType(Constant.OPERATION_TYPE_04);
			// 关联表标示
			lie.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
			// 登录IP
			lie.setOperIpaddress((String)param.get("loginIp"));
			// 备注
			lie.setMemo(custInfo.getLoginName() + "登录系统！");
			// 保存日志
			logInfoEntityRepository.save(lie);
			return new ResultVo(true, "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultVo(false, "系统异常");
		}
	}

	/**
	 * @author HuangXiaodong 修改个人信息
	 * @return ResultVo
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo updateCust(Map<String, Object> param) {
		try {
			CustInfoEntity cie = BeanMapConvertUtil.toBean(
					CustInfoEntity.class, param);
			custInfoRepository.save(cie);
			return new ResultVo(true, "操作成功");
		} catch (SLException e) {
			e.printStackTrace();
			return new ResultVo(false, "系统异常");
		}
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo updateCustStatus(Map<String, Object> param) {
		try {
			String id = (String) param.get("id");
			String userNumber = (String) param.get("userNumber");
			CustInfoEntity custInfoEntity = custInfoRepository.findOne(id);
			if (null != custInfoEntity) {

				LogInfoEntity lie = new LogInfoEntity();
				lie.setOperIpaddress((String) param.get("ipAddress"));
				StringBuffer sb = new StringBuffer("工号:").append(
						param.get("userNumber")).append(",操作:");
				// 解冻
				if ("正常".equals((String) param.get("enableStatus"))) {
					lie.setLogType(Constant.OPERATION_TYPE_02);
					sb.append(Constant.OPERATION_TYPE_02);
					custInfoEntity.setRealNameAuthCount(BigDecimal.ZERO);
					String mKey = new StringBuilder()
							.append(custInfoEntity.getId())
							.append(".daily.")
							.append("RealNameAuthenticationService.verifyIdentification")
							.toString();
					redisTemplate.delete(mKey);
				} else {
					// 冻结
					lie.setLogType(Constant.OPERATION_TYPE_01);
					sb.append(Constant.OPERATION_TYPE_01);
				}
				sb.append(",用户:").append(custInfoEntity.getCustName())
						.append(",用户ID:").append(custInfoEntity.getId());
				lie.setCreateDate(new Date());
				// 关联表标识
				lie.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
				// 外键
				lie.setRelatePrimary(custInfoEntity.getId());
				// 操作日志
				lie.setOperDesc(sb.toString());
				// 被操作人id
				lie.setOperPerson(custInfoEntity.getId());
				// 操作人 id
				lie.setCreateUser(userNumber);
				// 描述信息
				lie.setMemo((String) param.get("memo"));
				// 保存日志
				logInfoEntityRepository.save(lie);

				custInfoEntity.setEnableStatus((String) param
						.get("enableStatus"));
				custInfoRepository.save(custInfoEntity);
			}
			return new ResultVo(true, "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultVo(false, "系统异常");
		}

	}

	/**
	 * @author HuangXiaodong 2015-04-22 获取上次登录信息
	 * @param Map
	 *            <String, Object> custId: 用户id
	 * @return ResultVo
	 */
	@Override
	public ResultVo findLastLoginInfo(Map<String, Object> param) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String custId = param.get("custId") + "";
		if (StringUtils.isEmpty(custId)) {
			return new ResultVo(false, "用户id不能为空!");
		}
		List<Object[]> loginlist = loginLogInfoRepository
				.findLastLoginInfoOrderByLoginDateDesc(custId, new Date());
		if (loginlist != null && loginlist.size() > 1) {
			resultMap.put("lastLoginDate", loginlist.get(1));
		}
		ResultVo resultVo = new ResultVo(true);
		resultVo.putValue("data", resultMap);
		return resultVo;
	}

	/**
	 * @author HuangXiaodong 2015-04-22 用户活期宝信息
	 * @param Map
	 *            <String, Object> custId: 用户id
	 * @return ResultVo
	 */
	@Override
	public ResultVo findBaoCountInfoByCustId(Map<String, Object> param) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String custId = param.get("custId") + "";
		if (StringUtils.isEmpty(custId)) {
			return new ResultVo(false, "用户id不能为空!");
		}

		String productType = (String) param.get("productType");
		String tradeType = "";
		if (StringUtils.isEmpty(productType)) {
			productType = Constant.PRODUCT_TYPE_01;
			tradeType = SubjectConstant.TRADE_FLOW_TYPE_01;
		} else if (productType.equals(Constant.PRODUCT_TYPE_01)) {
			tradeType = SubjectConstant.TRADE_FLOW_TYPE_01;
		} else if (productType.equals(Constant.PRODUCT_TYPE_03)) {
			tradeType = SubjectConstant.TRADE_FLOW_TYPE_03;
		}

		Date starDate = DateUtils.getAfterDay(new Date(), -1);

		// 昨日收益
		param.put("tradeDate", starDate);
		param.put("tradeType", Arrays.asList(tradeType));
		BigDecimal yesterdayTradeAmount = accountFlowInfoRepositoryCustom
				.findIncomeByCustId(param);

		// 累计收益
		param.remove("tradeDate");
		BigDecimal sumTradeAmount = accountFlowInfoRepositoryCustom
				.findIncomeByCustId(param);

		// 持有价值
		param.put("typeName", productType);
		Map<String, Object> accountAmountMap = accountInfoRepositoryCustom
				.findAllValueByCustId(param);
		BigDecimal accountTotalValue = accountAmountMap
				.containsKey("accountTotalValue") ? (BigDecimal) accountAmountMap
				.get("accountTotalValue") : new BigDecimal("0");
		BigDecimal accountAvailableValue = accountAmountMap
				.containsKey("accountAvailableValue") ? (BigDecimal) accountAmountMap
				.get("accountAvailableValue") : new BigDecimal("0");

		// 累积加入金额
		ProductInfoEntity productInfoEntity = productInfoRepository
				.findProductInfoByProductTypeName(productType);
		BigDecimal sumJoinAmount = investInfoRepository
				.queryTotalInvestAmountByCustId(custId,
						productInfoEntity.getId(), Constant.VALID_STATUS_VALID);

		// 累积赎回金额
		BigDecimal sumAtoneAmount = atoneInfoRepository
				.queryTotalAtoneAmountByCustId(custId,
						productInfoEntity.getId());

		// 持有债权数
		Map<String, Object> loanMap = loanInfoRepositoryCustom
				.findLoanListCount(param);
		BigDecimal totalLoans = (BigDecimal) loanMap.get("totalLoans");

		// 赎回中金额
		BigDecimal sumAtoningAmount = atoneInfoRepository
				.queryTotalAtoningAmountByCustId(custId,
						productInfoEntity.getId());

		resultMap.put("yesterdayTradeAmount",
				yesterdayTradeAmount == null ? "0"
						: yesterdayTradeAmount.toPlainString());
		resultMap.put("sumTradeAmount",
				sumTradeAmount == null ? "0" : sumTradeAmount.toPlainString());
		resultMap
				.put("accountAmount",
						accountTotalValue == null ? BigDecimal.ZERO
								: accountTotalValue);
		resultMap.put("accountAvailableValue",
				accountAvailableValue == null ? BigDecimal.ZERO
						: accountAvailableValue);
		resultMap.put("sumJoinAmount", sumJoinAmount == null ? BigDecimal.ZERO
				: sumJoinAmount);
		resultMap.put("sumAtoneAmount",
				sumAtoneAmount == null ? BigDecimal.ZERO : sumAtoneAmount);
		resultMap.put("sumHoldLoan", totalLoans == null ? BigDecimal.ZERO
				: totalLoans);
		resultMap.put("sumAtoningAmount",
				sumAtoningAmount == null ? BigDecimal.ZERO : sumAtoningAmount);
		ResultVo resultVo = new ResultVo(true);
		resultVo.putValue("data", resultMap);
		return resultVo;
	}

	/**
	 * @author HuangXiaodong 2015-04-22 用户善林理财信息
	 * @param Map
	 *            <String, Object> custId: 用户id
	 * @return ResultVo
	 */
	@Override
	public ResultVo findWealthCountInfoByCustId(Map<String, Object> param) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String custId = param.get("custId") + "";
		if (StringUtils.isEmpty(custId)) {
			return new ResultVo(false, "用户id不能为空!");
		}
		List<Object[]> tradelist = investInfoRepository
				.investAmountBycustIdAndProductType(custId,
						Constant.PRODUCT_TYPE_02);
		if (tradelist != null && tradelist.size() > 0) {
			resultMap.put(
					"investAmount",
					tradelist.get(0)[0] == null ? BigDecimal.ZERO : tradelist
							.get(0)[0]);
			resultMap.put(
					"investMubers",
					tradelist.get(0)[1] == null ? BigDecimal.ZERO : tradelist
							.get(0)[1]);

		}
		BigDecimal sumTradeAmount = tradeFlowInfoRepository
				.sumTradeAmountBycustId(custId,
						SubjectConstant.TRADE_FLOW_TYPE_02);
		resultMap.put("sumTradeAmount",
				sumTradeAmount == null ? BigDecimal.ZERO : sumTradeAmount);
		ResultVo resultVo = new ResultVo(true);
		resultVo.putValue("data", resultMap);
		return resultVo;
	}

	/**
	 * 我的账户-我的投资-活期宝-交易明细
	 *
	 * @author wangjf
	 * @date 2015年4月28日 下午4:38:49
	 * @param param
	 *            <tt>custId： String:客户ID</tt><br>
	 *            <tt>tradeType： String:交易类型(可以为空)</tt><br>
	 *            <tt>opearteDateBegin：String:操作开始时间(可以为空)</tt><br>
	 *            <tt>opearteDateEnd：String:操作开始时间(可以为空)</tt><br>
	 * @return iTotalDisplayRecords: 总条数 data:List<Map<String, object>>
	 *         Map<String, object>: <tt>requestNo： String:请求编号</tt><br>
	 *         <tt>tradeDate： String:交易日期</tt><br>
	 *         <tt>tradeType： String:交易类型</tt><br>
	 *         <tt>tradeAmount： BigDecimal:交易金额</tt><br>
	 */
	@Override
	public Map<String, Object> findAllBaoAccountDetailByCustId(
			Map<String, Object> param) {

		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = accountFlowInfoRepositoryCustom
				.findAllBaoAccountDetailByCustId(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());

		return result;
	}

	public Map<String, Object> findAllCustInfo(Map<String, Object> param) {

		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = custInfoRepository
				.findCustInfoEntityByPage(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	@Override
	public ResultVo findTotalIncomeByCustId(Map<String, Object> param) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		param.put("tradeType", Arrays.asList(
				SubjectConstant.TRADE_FLOW_TYPE_01,
				SubjectConstant.TRADE_FLOW_TYPE_02,
				SubjectConstant.TRADE_FLOW_TYPE_03));
		BigDecimal sumTradeAmount = accountFlowInfoRepositoryCustom
				.findIncomeByCustId(param);
		BigDecimal sumAccountAmount = accountFlowInfoRepositoryCustom
				.findTotalAccountAmount(param);
		resultMap.put("sumTradeAmount",
				sumTradeAmount == null ? "0" : sumTradeAmount.toPlainString());// 累积收益
		resultMap.put("sumAccountAmount",
				sumAccountAmount == null ? BigDecimal.ZERO : sumAccountAmount);// 用户总资产
		ResultVo resultVo = new ResultVo(true);
		resultVo.putValue("data", resultMap);
		return resultVo;
	}

	@Override
	public ResultVo queryInviteData(Map<String, Object> custMap)
			throws SLException {
		return custInfoRepositoryImpl.queryInviteData(custMap);
	}

	@Override
	public ResultVo queryCustSpreadLevel(Map<String, Object> custMap)
			throws SLException {
		return custInfoRepositoryImpl.queryCustSpreadLevel(custMap);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo sendSMS(Map<String, Object> params) {
		
		String mobile = (String)params.get("mobile");
		String messageType = (String)params.get("messageType");
		String mobileDeviceFlag = "";
		
		// 判断是否存在设备来源
		if(!StringUtils.isEmpty((String)params.get("appSource"))) {
			String appSource = (String)params.get("appSource");
			String meId = (String)params.get("meId");
			
			// 若android或者ios，则验证短信发送次数，同一台设备同一种业务类型每日不能超过10次
			if(Constant.APP_SOURCE_ANDROID.compareToIgnoreCase(appSource) == 0
					||Constant.APP_SOURCE_IOS.compareToIgnoreCase(appSource) == 0) { 
				
				mobileDeviceFlag = "SMS_MOBILE_" + meId + "_" + messageType;
				Long maxTimes = paramService.findMaxSendSmsTimes();
				
				String dateCounts = redisTemplate2.opsForValue().get(mobileDeviceFlag);
				if(!StringUtils.isEmpty(dateCounts) && Long.valueOf(dateCounts) >= maxTimes) {
					log.info("手机{}设备号{}业务{}当天发短信次数超过{}次，已发送{}次", mobile, meId, messageType, maxTimes, dateCounts);	
					return new ResultVo(false, String.format("每日限发%d条短信，您已经超过规定次数！", maxTimes));
				}
			}
		}

		// 根据手机号查询用户
		CustInfoEntity custInfoEntity = custInfoRepository.findByMobile(mobile);
		if (custInfoEntity != null) {
			return new ResultVo(false, "该用户手机号已存在");
		}
		
		ResultVo resultVo = sMSService.sendSMS(params);
		if(ResultVo.isSuccess(resultVo)) {		
			if(!StringUtils.isEmpty(mobileDeviceFlag)) {
				long counts = redisTemplate2.opsForValue().increment(mobileDeviceFlag, 1);
				if(counts == 1) {
					// 设置为第二天0点过期
					redisTemplate2.expireAt(mobileDeviceFlag, DateUtils.getAfterDay(new Date(), 1));
				}
			}
		}
		
		return resultVo;		
	}

	@Override
	public Map<String, Object> findCustInfoEntityDetail(
			Map<String, Object> custMap) throws SLException {
		return custInfoRepository.findCustInfoEntityDetail(custMap);
	}

	/**
	 * 根据手机号修改登录密码
	 * 
	 * @param param
	 *            <tt>mobile： 手机号</tt><br>
	 *            <tt>verityCode：验证码</tt><br>
	 *            <tt>password：登录密码</tt><br>
	 *            <tt>messageType： 短信类型</tt><br
	 * @param params
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo updateLoginPasswrodByMobile(Map<String, Object> params) {
		String messageType = (String) params.get("messageType");
		String verityCode = (String) params.get("verityCode");
		String mobile = (String) params.get("mobile");
		String password = (String) params.get("password");
		String loginPwdLevel = (String) params.get("loginPwdLevel");

		if (StringUtils.isEmpty(messageType))
			return new ResultVo(false, "手机号或交易密码为空");
		if (StringUtils.isEmpty(verityCode))
			return new ResultVo(false, "验证码为空");
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("verityCode", verityCode);
		param.put("messageType", messageType);
		param.put("targetAddress", mobile);

		try {
			
			CustInfoEntity cie = custInfoRepository.findByMobile(mobile);
			if (password.equals(cie.getTradePassword()))
				return new ResultVo(false, "登录密码不能和交易密码相同");
			
			ResultVo rv = sMSService.checkVerityCode(param);
			if (ResultVo.isSuccess(rv)) {

				cie.setLoginPassword(password);
				cie.setLoginPwdLevel(loginPwdLevel);
				custInfoRepository.save(cie);
				
				// 新增记录日志 add by wangjf 2015-07-30
				LogInfoEntity logInfoEntity = new LogInfoEntity();
				logInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
				logInfoEntity.setRelatePrimary(cie.getId());
				logInfoEntity.setLogType(Constant.OPERATION_TYPE_19);
				logInfoEntity.setOperDesc("");
				logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
				logInfoEntity.setOperPerson(cie.getId());
				logInfoEntity.setMemo(String.format("%s修改了登录密码",
						cie.getLoginName()));
				logInfoEntity.setBasicModelProperty(cie.getId(), true);
				logInfoEntityRepository.save(logInfoEntity);
			} else {
				return new ResultVo(false, "操作失败");
			}

		} catch (Exception e) {
			return new ResultVo(false, "操作失败");
		}

		return new ResultVo(true, "操作成功");
	}

	@Override
	public CustInfoEntity findByCustId(String custId) {
		return custInfoRepository.findOne(custId);
	}

	/**
	 * @author HuangXiaodong 2015-04-22 用户活期宝信息
	 * @param Map
	 *            <String, Object> custId: 用户id
	 * @return ResultVo
	 */
	@Override
	public ResultVo findExperienceBaoCountInfoByCustId(Map<String, Object> param) {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		param.put("tradeType",
				Arrays.asList(SubjectConstant.TRADE_FLOW_TYPE_03));
		BigDecimal sumTradeAmount = accountFlowInfoRepositoryCustom
				.findIncomeByCustId(param);

		// 持有价值
		param.put("typeName", Constant.PRODUCT_TYPE_03);
		Map<String, Object> accountAmountMap = accountInfoRepositoryCustom
				.findAllValueByCustId(param);
		BigDecimal accountTotalValue = accountAmountMap
				.containsKey("accountTotalValue") ? (BigDecimal) accountAmountMap
				.get("accountTotalValue") : new BigDecimal("0");
		BigDecimal accountAvailableValue = accountAmountMap
				.containsKey("accountAvailableValue") ? (BigDecimal) accountAmountMap
				.get("accountAvailableValue") : new BigDecimal("0");

		Date starDate = DateUtils.getAfterDay(new Date(), -1);
		// 昨日收益
		param.put("tradeDate", starDate);
		BigDecimal yesterdayTradeAmount = accountFlowInfoRepositoryCustom
				.findIncomeByCustId(param);
		resultMap.put("yesterdayTradeAmount",
				yesterdayTradeAmount == null ? "0"
						: yesterdayTradeAmount.toPlainString());// 昨日收益
		resultMap.put("sumTradeAmount",
				sumTradeAmount == null ? "0" : sumTradeAmount.toPlainString());// 累积收益
		resultMap
				.put("accountAmount",
						accountTotalValue == null ? BigDecimal.ZERO
								: accountTotalValue);// 持有价值
		resultMap.put("accountAvailableValue",
				accountAvailableValue == null ? BigDecimal.ZERO
						: accountAvailableValue);// 可用价值
		ResultVo resultVo = new ResultVo(true);
		resultVo.putValue("data", resultMap);
		return resultVo;
	}

	/**
	 * @author HuangXiaodong 2015-04-22 用户活期宝信息
	 * @param Map
	 *            <String, Object> custId: 用户id
	 * @return ResultVo
	 */
	@Override
	public ResultVo findBaoTradeInfo(Map<String, Object> param) {
		param.put("tradeType", Arrays.asList(
				SubjectConstant.TRADE_FLOW_TYPE_01,
				SubjectConstant.TRADE_FLOW_TYPE_03,
				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_INCOME));
		param.put("subjectType", Arrays.asList(
				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_PROJECT, 
				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PRINCIPAL_PROJECT));
//		List<Map<String, Object>> lsit = accountFlowInfoRepositoryCustom
//				.findSumTradeAmount(param);
		List<Map<String, Object>> list = accountFlowInfoRepositoryCustom.findSumTradeAmountNew(param);
		ResultVo resultVo = new ResultVo(true);
		resultVo.putValue("data", list);
		return resultVo;
	}

	/**
	 * 重设交易密码
	 * 
	 * @param params
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo resetTradePassword(Map<String, Object> params)
			throws SLException {
		String tradePassword = (String) params.get("tradePassword");
		String id = (String) params.get("id");
		CustInfoEntity cie = custInfoRepository.findOne(id);
		if (tradePassword.equals(cie.getLoginPassword()))
			return new ResultVo(false, "交易密码不能和登录密码一致！");
		cie.setTradePassword(tradePassword);
		custInfoRepository.save(cie);
		
		// 新增记录日志 add by wangjf 2015-07-30
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
		logInfoEntity.setRelatePrimary(cie.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_19);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setOperPerson(cie.getId());
		logInfoEntity.setMemo(String.format("%s修重置了交易密码",
				cie.getLoginName()));
		logInfoEntity.setBasicModelProperty(cie.getId(), true);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true, "操作成功");
	}

	@Override
	public Map<String, Object> findAllAtoneByCustId(Map<String, Object> param) {

		if (!param.containsKey("productType")) { // 默认为活期宝
			param.put("productType", Constant.PRODUCT_TYPE_01);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = atoneInfoRepositoryCustom
				.findAllAtoneByCustId(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());

		return result;
	}

	/**
	 * 更新用户信息和新增日志
	 * 
	 * @throws SLException
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = SLException.class)
	public void updCustAndInsertLog(CustInfoEntity updateInfo,
			List<LogInfoEntity> logList, String custId) throws SLException {
		CustInfoEntity custInfo = custInfoRepository.findOne(custId);
		if (!custInfo.update(updateInfo))
			throw new SLException("更新用户信息失败");
		logInfoEntityRepository.save(logList);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo innerRegister(Map<String, Object> custInfoMap) {
		try {
			// 根据手机号_验证码_类型_状态，删除缓存数据
			CustInfoEntity unSaveEntity = BeanMapConvertUtil.toBean(
					CustInfoEntity.class, custInfoMap);
			unSaveEntity.setCustSource(Strings.nullToEmpty(unSaveEntity.getCustSource()).toLowerCase());
			//mzhu 20150706
			ResultVo rv = UserUtils.regularLoginName(unSaveEntity.getLoginName());
			if(!ResultVo.isSuccess(rv))
				return rv;
			
			
			// 校验手机唯一性
			if (custInfoRepository.findByMobile(unSaveEntity.getMobile()) != null) {
				return new ResultVo(false, "手机号已存在!",
						OpenSerivceCode.ERR_MOBILE_REPEATE);
			}

			// 用户名唯一性
			if (custInfoRepository.findByLoginName(unSaveEntity.getLoginName()) != null) {
				return new ResultVo(false, "用户名已存在!",
						OpenSerivceCode.ERR_LOGINNAME_REPEATE);
			}

			// 1)若存在渠道编号非空则需验证渠道号合法性 add by wangjf 2015-7-13
			// 
			// 2)update by wangjf 2015-10-19
			// 渠道号包含了从appStore，豌豆荚，小米商城等渠道，此处不再判断渠道号是否非法，
			// 若在接口表中存在则取接口表的数据，否则不取直接存储
			if(custInfoMap.containsKey("channelNo")) {
				// 验证渠道是否有效
				InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository
						.findByInterfaceMerchantCode(
								Constant.OPERATION_TYPE_16,
								(String) custInfoMap.get("channelNo"));
				if(interfaceDetailInfoEntity != null) {
					unSaveEntity.setChannelSource(interfaceDetailInfoEntity.getThirdPartyType());
				}
				else {
					unSaveEntity.setChannelSource((String) custInfoMap.get("channelNo"));
				}
//				if (interfaceDetailInfoEntity == null) {
//					return new ResultVo(false, "渠道非法!",
//							OpenSerivceCode.ERR_INVALID_CHANNEL);
//				}
			}
			
			// 若参数中无不做验证码判断标识，则需验证验证码
			if(!custInfoMap.containsKey("noValidateCode")) {
				// 如果当前验证码跟缓存一致,则删除缓存,标记已使用
				ResultVo vo = sMSService.checkMobileAndMessageType(custInfoMap);
				if (!ResultVo.isSuccess(vo)) {
					// 校验失败
					return vo;
				}
			}

			String parentCustId = "";
			synchronized (this) {
				/**
				 * mzhu 20150501 判断是否存在邀请码 不存在邀请码 新增判断
				 */
				CustInfoEntity cieFather = null;
				String inviteCode = unSaveEntity.getInviteCode();
				if(StringUtils.isEmpty(inviteCode) && custInfoMap.containsKey("channelNo")) {
					inviteCode = PropertiesLoader.getContextProperty(custInfoMap.get("channelNo").toString());
					if(!StringUtils.isEmpty(inviteCode)) {
						cieFather = custInfoRepository.findByInviteCode(inviteCode);
						if(cieFather == null) {
							inviteCode = "";
						}
					}
				}
				
				if (StringUtils.isEmpty(inviteCode)) {
					// 1) 设置推广等级
					unSaveEntity.setSpreadLevel(Constant.SPREAD_LEVEL_ROOT);
					// 2)生成邀请码 调用存储过程
					unSaveEntity.setInviteCode(findGetInviteCode());
					// 3)推广来源ID
					unSaveEntity
							.setInviteOriginId(Constant.INVITE_ORIGIN_ID_ROOT);
					// 4)查看权限 调用函数
					String queryPermission = String.format("%09d",
							findTotalSpreadLevel());
					unSaveEntity.setQueryPermission(queryPermission);
				} else {
					if(cieFather == null) {
						cieFather = custInfoRepository.findByInviteCode(inviteCode);
						// 判断邀请码是否存在
						if (null == cieFather) {
							// 邀请码不存在
							return new ResultVo(false, "邀请码不存在!");
						}
					}
					
					String queryCode = findQueryCode(cieFather.getId());
					if ("".equals(queryCode))
						return new ResultVo(false, "queryCode不存在!");
					// 1) 设置推广等级
					int level = new BigDecimal(cieFather.getSpreadLevel()).add(
							new BigDecimal("1")).intValue();
					unSaveEntity.setSpreadLevel(String.valueOf(level));
					// 2)推广来源ID
					unSaveEntity.setInviteOriginId(cieFather.getId());
					// 3)查看权限 调用函数
					unSaveEntity.setQueryPermission(cieFather
							.getQueryPermission() + queryCode);
					// 4)生成邀请码 调用存储过程

					unSaveEntity.setInviteCode(findGetInviteCode());
					
					// 若推荐人是金牌推荐人，则保存推荐热人ID，在注册最后保存金牌推荐人与本客户的关系
					if(Constant.IS_RECOMMEND_YES.equals(cieFather.getIsRecommend())) {
						parentCustId = cieFather.getId();
					}
				}
			}
			
			unSaveEntity.setCustType("");
			unSaveEntity.setEnableStatus(Constant.REG_ENABLE_STATUS);
			// 找到符合条件验证码,验证通过
			unSaveEntity.setCreateDate(new Date());
			unSaveEntity.setLastUpdateDate(new Date());
			unSaveEntity
					.setCustCode(flowNumberService.generateCustomerNumber());
			CustInfoEntity savedEntity = custInfoRepository.save(unSaveEntity);
			
			// 创建总账信息
			AccountInfoEntity accountInfoEntity = new AccountInfoEntity();
			// 客户信息表主键ID
			accountInfoEntity.setCustId(savedEntity.getId());
			// 账户编号
			accountInfoEntity.setAccountNo(flowNumberService
					.generateCustomerNumber());
			// 创建时间
			accountInfoEntity.setCreateDate(new Date());

			// BAO_T_LOG_INFO
			LogInfoEntity lie = new LogInfoEntity();
			// 创建时间
			lie.setCreateDate(new Date());
			// 外键
			lie.setRelatePrimary("");
			// 操作日志
			lie.setOperDesc("");
			// 被操作人id
			lie.setOperPerson(savedEntity.getId());
			// 操作人 id
			lie.setCreateUser(savedEntity.getId());
			// 日志类型
			lie.setLogType(Constant.OPERATION_TYPE_16);
			// 关联表标示
			lie.setRelateType("");
			// 备注
			lie.setMemo("");
			// 注册IP
			lie.setOperIpaddress((String) custInfoMap.get("ipAddress"));
			// 保存日志
			logInfoEntityRepository.save(lie);

			// ACCOUNT_TYPE 账户类型
			AccountInfoEntity savedAccountInfoEntity = accountInfoRepository
					.save(accountInfoEntity);

			/** 活动信息 20150516 */
			// 活动1 注册送体验金
			Map<String, Object> custActivityMap = new HashMap<String, Object>();
			custActivityMap.put("activityId", Constant.ACTIVITY_ID_REGIST_01);
			custActivityMap.put("custInfoEntity", savedEntity);
			custActivityMap.put("custAccount", savedAccountInfoEntity);
			custActivityMap.put("tradeNo", flowNumberService.generateTradeNumber());
			custActivityInfoService.custActivityRecommend(custActivityMap);

			// 注册送888红包--20170628 现金券、加息券、体验金
			ImmutableMap.Builder<String, Object> custActivityParamsMapBuilder = new ImmutableMap.Builder<String, Object>();
			custActivityParamsMapBuilder.put("activityId", Constant.ACTIVITY_ID_REGIST_15);
			custActivityParamsMapBuilder.put("custInfoEntity", savedEntity);
			custActivityParamsMapBuilder.put("custAccount", savedAccountInfoEntity);
			custActivityParamsMapBuilder.put("tradeNo", flowNumberService.generateTradeNumber());
			custActivityInfoService.custActivityRecommend(custActivityParamsMapBuilder.build());

			// 若参数中无不做验证码判断标识，则需验证验证码
			if(!custInfoMap.containsKey("noValidateCode")) {
				// 更新日志状态
				int updateCount = smsInfoRepository.updateByTargetAddressAndVerityCodeAndMessageType(new Date(), custInfoMap.get("mobile") + "", custInfoMap.get("verityCode") + "", custInfoMap.get("messageType") + "");
				if (updateCount == 0) {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					// 否则,返回失败原因
					return new ResultVo(false, "验证码错误!");
				}
			}
			
			// 若推荐人为金牌推荐人（ID不为空），则需保存推荐人与注册用户的关系
			if(!StringUtils.isEmpty(parentCustId)) {
				List<CustApplyInfoEntity> custApplyList = custApplyInfoRepository.findCustApplyInfoEntityCustomer(parentCustId);
				if(custApplyList != null && custApplyList.size() > 0) {
					for(CustApplyInfoEntity c : custApplyList) {
						if(Constant.OPERATION_TYPE_23.equals(c.getApplyType()) 
								&& Constant.AUDIT_STATUS_PASS.equals(c.getApplyStatus())) {
							CustRecommendInfoEntity custRecommendInfoEntity = new CustRecommendInfoEntity();
							custRecommendInfoEntity.setCustId(parentCustId);
							custRecommendInfoEntity.setQuiltCustId(savedEntity.getId());
							custRecommendInfoEntity.setApplyId(c.getId());
							custRecommendInfoEntity.setSource(Constant.CUST_RECOMMEND_SOURCE_REGISTER);
							custRecommendInfoEntity.setStartDate(new Timestamp(new Date().getTime()));
							custRecommendInfoEntity.setBasicModelProperty(savedEntity.getId(), true);
							custRecommendInfoRepository.save(custRecommendInfoEntity);
							break;
						}
					}
					
				}
			}
			
			// 记录设备号
			deviceService.saveDeviceInfo(savedEntity, custInfoMap);
			
			//判断 是否参与活动 ，保存活动信息
			if (!StringUtils.isEmpty(custInfoMap.get("activityCode")) && Constant.ACTIVITY_ID_REGIST_10.equals(custInfoMap.get("activityCode").toString())) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("activityCode", custInfoMap.get("activityCode"));
				map.put("savedEntity", savedEntity);
				custActivityInfoService.custActivityInvestRecommendByRegister(map);
			}
			
			// 2017 6月市场部活动 // 追加两个邀请人的关系
			if (!StringUtils.isEmpty(custInfoMap.get("activityCode"))&& Constant.ACTIVITY_ID_REGIST_13.equals(custInfoMap.get("activityCode").toString())) {
				savedEntity.setMemo2(Constant.ACTIVITY_ID_13);// 表示本次活动区分
				if(custInfoMap.containsKey("bandInviteNew")){
					String inviteCustId1 = (String) custInfoMap.get("inviteCustId1");
					createCustRecommendFor201706(inviteCustId1, savedEntity.getId(), savedEntity.getId());
				}
				
				if(custInfoMap.containsKey("bandInvite2")){
					String inviteCustId1 = (String) custInfoMap.get("inviteCustId1");
					String inviteCustId2 = (String) custInfoMap.get("inviteCustId2");
					if(!inviteCustId1.equals(inviteCustId2)) {
						CustInfoEntity cust2 = custInfoRepository.findOne(inviteCustId2);
						cust2.setInviteOriginId(inviteCustId1);
						cust2.setBasicModelProperty(savedEntity.getId(), false);
						createCustRecommendFor201706(inviteCustId1, inviteCustId2, savedEntity.getId());
					}
				}
			}
			
			//市场部棋王争霸赛活动
			if (!StringUtils.isEmpty(custInfoMap.get("activityCode")) && Constant.ACTIVITY_ID_REGIST_14.equals(custInfoMap.get("activityCode").toString())) {
				savedEntity.setMemo2(Constant.ACTIVITY_ID_14);// 表示本次活动区分
				Map<String, Object> map = Maps.newHashMap();
				map.put("activityId", Constant.ACTIVITY_ID_REGIST_14_REGISTER);
				map.put("custId", savedEntity.getId());
				map.put("inviteCode", custInfoMap.get("inviteCode"));
				map.put("activityPlaceCity", custInfoMap.get("activityPlaceCity"));
				custActivityInfoService.custActivityRecommend(map);
			}
			
			//市场部大转盘活动
			if (!StringUtils.isEmpty(custInfoMap.get("activityCode")) && Constant.ACTIVITY_ID_REGIST_16.equals(custInfoMap.get("activityCode").toString())) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("activityId", Constant.ACTIVITY_ID_REGIST_16);
				map.put("custInfoEntity", savedEntity);
				map.put("tradeNo", flowNumberService.generateActivitySequnce());
				custActivityInfoService.custActivityRecommend(map);
			}
			
			return new ResultVo(true, "注册成功.");
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus()
					.setRollbackOnly();
			log.error(e.getMessage(), e);
			return new ResultVo(false);
		}
	}
	
	// 2017 市场部6月活动，没有客户经理的客户分享，如果客户经理邀请栏填了，也建立关系
	private synchronized void createCustRecommendFor201706(String mgrId, String qCustId, String opId){
		// 客户经理Id，客户Id
		// 建立邀请人客户经理 和 邀请人客户 对应关系
		List<CustApplyInfoEntity> custApplyList = custApplyInfoRepository.findCustApplyInfoEntityCustomer(mgrId);
		if(custApplyList != null && custApplyList.size() > 0) {
			for(CustApplyInfoEntity c : custApplyList) {
				if(Constant.OPERATION_TYPE_23.equals(c.getApplyType()) 
						&& Constant.AUDIT_STATUS_PASS.equals(c.getApplyStatus())) {
					CustRecommendInfoEntity custRecommendInfoEntity = new CustRecommendInfoEntity();
					custRecommendInfoEntity.setCustId(mgrId);
					custRecommendInfoEntity.setQuiltCustId(qCustId);
					custRecommendInfoEntity.setApplyId(c.getId());
					custRecommendInfoEntity.setSource(Constant.CUST_RECOMMEND_SOURCE_REGISTER);
					custRecommendInfoEntity.setStartDate(new Timestamp(new Date().getTime()));
					custRecommendInfoEntity.setBasicModelProperty(opId, true);
					custRecommendInfoEntity.setMemo("市场部6月活动(2017)");
					custRecommendInfoRepository.save(custRecommendInfoEntity);
					break;
				}
			}
		}
	}

	/**
	 * 查询客户基本信息和联系人基本信息(善林财富二期)
	 */
	@Override
	public Map<String,Object> getCustAndContactCustInfo(Map<String,Object> param) throws SLException {
		
		/**用户基本信息**/
		CustInfoEntity custInfo = custInfoRepository.findOne((String)param.get("custId"));
		if(custInfo==null)
			throw new SLException("用户不存在");
		Map<String,Object> custMap = Maps.newHashMap();
		custMap.put("id", custInfo.getId());
		custMap.put("custName", custInfo.getCustName());
		custMap.put("portraitPath", custInfo.getPortraitPath());
		custMap.put("credentialsCode", custInfo.getCredentialsCode());
		custMap.put("mobile", custInfo.getMobile());
		custMap.put("email", custInfo.getEmail());
		custMap.put("natvicePlaceProvince", custInfo.getNatvicePlaceProvince());
		custMap.put("natvicePlaceCity", custInfo.getNatvicePlaceCity());
		custMap.put("natvicePlaceCounty", custInfo.getNatvicePlaceCounty());
		custMap.put("communAddress", custInfo.getCommunAddress());
		custMap.put("qqCode", custInfo.getQqCode());
		custMap.put("zipCode", custInfo.getZipCode());
		/**联系人信息**/
		Map<String,Object> contanctInfo = Maps.newHashMap();
		ContactInfoEntity contantUser = custInfo.getContanctInfo();
		if(contantUser != null){
			contanctInfo.put("id", contantUser.getId());
			contanctInfo.put("contactName", contantUser.getContactName());
			contanctInfo.put("ralationType", contantUser.getRelationType());
			contanctInfo.put("contanctTelePhone", contantUser.getContanctTelePhone());	
		}
		custMap.put("contanctInfo",contanctInfo);
		
		return custMap;
		
	}
	
	/***
	 * 修改账户用户信息和联系人信息
	 */
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Override
	public ResultVo updateCustAndContactCustInfo(Map<String,Object> custInfoMap) throws SLException{
		CustInfoEntity custInfo = JSONObject.toJavaObject((JSON)JSON.toJSON(custInfoMap), CustInfoEntity.class);
		CustInfoEntity originalCust = custInfoRepository.findOne(custInfo.getId());
		if(originalCust == null)
			return new ResultVo(false,"用户信息不存在");
		
		ContactInfoEntity contanctUser = custInfo.getContanctInfo();
		ContactInfoEntity  originalContanct =  originalCust.getContanctInfo();
		if( contanctUser == null )
			return new ResultVo(false,"联系人信息不能为空");
		
		if( contanctUser.getId() == null && originalContanct != null && originalContanct.getId() != null )
			return new ResultVo(false,"联系人信息不完整");
		
		if( contanctUser.getId() != null && originalContanct != null && !contanctUser.getId().equals(originalContanct.getId()))
			return new ResultVo(false,"联系人信息不一致");
		/**校验联系人信息**/
		ResultVo checkResult = checkData(contanctUser,custInfo);
		if(!ResultVo.isSuccess(checkResult))
			return checkResult;
		/**更新用户信息**/
		originalCust.setNatvicePlaceProvince(custInfo.getNatvicePlaceProvince());
		originalCust.setNatvicePlaceCity(custInfo.getNatvicePlaceCity());
		originalCust.setNatvicePlaceCounty(custInfo.getNatvicePlaceCounty());
		originalCust.setCommunAddress(custInfo.getCommunAddress());
		originalCust.setZipCode(custInfo.getZipCode());
		originalCust.setQqCode(custInfo.getQqCode());
		/**更新或者创建联系人信息**/
		if(originalContanct == null){
			originalContanct = new ContactInfoEntity();
			originalContanct.setCustInfo(originalCust);
		}
		originalContanct.setContactName(contanctUser.getContactName());
		originalContanct.setRelationType(contanctUser.getRelationType());
		originalContanct.setContanctTelePhone(contanctUser.getContanctTelePhone());
		originalContanct.setBasicModelProperty(custInfo.getId(), contanctUser.getId() != null ? false : true);
		
		originalCust.setContanctInfo(originalContanct);
		originalCust.setBasicModelProperty(custInfo.getId(), false);
		
		/**更新信息**/
		custInfoRepository.save(originalCust);
		/**记录日志**/
		logInfoEntityRepository.save(getLogList(custInfoMap,custInfo,originalCust));
		return new ResultVo(true,"修改成功");
		
	}

//------私有方法------------------------------------------------------------------------------------------------------------------------------------------	
	
	/**
	 * 添加日志
	 * @param custInfoMap
	 * @param custInfo
	 * @return
	 */
	private List<LogInfoEntity> getLogList(Map<String, Object> custInfoMap,CustInfoEntity custInfo,CustInfoEntity originalCustInfo) {
		List<LogInfoEntity> list = new ArrayList<LogInfoEntity>();
		LogInfoEntity custInfoLog = new LogInfoEntity(Constant.TABLE_BAO_T_CUST_INFO, custInfo.getId(), Constant.OPERATION_TYPE_19, null, null, "用户:"+originalCustInfo.getLoginName()+"做"+Constant.OPERATION_TYPE_19, custInfo.getId());
		custInfoLog.setOperIpaddress((String)custInfoMap.get("operIpaddress"));
		custInfoLog.setMemo(custInfoLog.getOperDesc());
		custInfoLog.setBasicModelProperty(custInfo.getId(), true);
		list.add(custInfoLog);
//		String logType = StringUtils.isEmpty(((Map<String,Object>)custInfoMap.get("contanctInfo")).get("id")) ? Constant.OPERATION_TYPE_20 : Constant.OPERATION_TYPE_21 ;
		LogInfoEntity contactCustInfoLog = new LogInfoEntity(Constant.TABLE_BAO_T_CONTACT_INFO, originalCustInfo.getContanctInfo().getId(),Constant.OPERATION_TYPE_20 , null,null, "用户:"+originalCustInfo.getLoginName()+"做"+Constant.OPERATION_TYPE_20, custInfo.getId());
		contactCustInfoLog.setOperIpaddress((String)custInfoMap.get("operIpaddress"));
		contactCustInfoLog.setMemo(contactCustInfoLog.getOperDesc());
		contactCustInfoLog.setBasicModelProperty(custInfo.getId(), true);
		list.add(contactCustInfoLog);
		return list;
	}

	/**
	 * 校验联系人和用户数据
	 */
	private ResultVo checkData( ContactInfoEntity contanctUser,CustInfoEntity custInfo ) throws SLException{
		/**校验联系方式**/
		if(!RuleUtils.isMobile(contanctUser.getContanctTelePhone()) && !RuleUtils.isTel(contanctUser.getContanctTelePhone()))
			return new ResultVo(false,"联系电话格式不正确");
		
		/**校验QQ**/
		if( !StringUtils.isEmpty(custInfo.getQqCode())  && (  !RuleUtils.isDigist(custInfo.getQqCode()) || custInfo.getQqCode().length() > 16  ) )
			return new ResultVo(false,"请检查QQ数据");
		return new ResultVo(true);
	}

	@Override
	public ResultVo checkInvestCode(Map<String, Object> map) throws SLException {
		
		if(RuleUtils.isMobile(map.get("investCode"))){ // 验证邀请码为手机号
			CustInfoEntity custInfoEntity = custInfoRepository.findByMobile((String)map.get("investCode"));
			if(custInfoEntity == null) {
				return new ResultVo(false, "推荐人不存在");
			}
			else {
				if(map.containsKey("custMgr") && "true".equals(map.get("custMgr"))){
					if("是".equals(custInfoEntity.getIsRecommend())){
						return new ResultVo(true);
					}else{
						return new ResultVo(false, "该用户不是业务员");
					}
				}
				return new ResultVo(true);
			}
		}
		
		CustInfoEntity cieFather = custInfoRepository
				.findByInviteCode((String)map.get("investCode"));
		// 判断邀请码是否存在
		if (null == cieFather) {
			// 邀请码不存在
			return new ResultVo(false, "推荐人不存在");
		}else{
			if(map.containsKey("custMgr") && "true".equals(map.get("custMgr"))){
				if("是".equals(cieFather.getIsRecommend())){
					return new ResultVo(true);
				}else{
					return new ResultVo(false, "该用户不是业务员");
				}
			}
		}
		
		return new ResultVo(true);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo uploadCustomPhoto(Map<String, Object> m)
			throws SLException {
		
		String custId = (String) m.get("custId");
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null){
			throw new SLException("用户不存在！");
		}
		custInfoEntity.setPortraitPath((String)m.get("storagePath"));
		custInfoEntity.setBasicModelProperty(custId, false);
		
		// 记录附件
		AttachmentInfoEntity attachmentInfoEntity = new AttachmentInfoEntity();
		attachmentInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
		attachmentInfoEntity.setRelatePrimary(custInfoEntity.getId());
		attachmentInfoEntity.setAttachmentType((String)m.get("attachmentType"));
		attachmentInfoEntity.setAttachmentName((String)m.get("attachmentName"));
		attachmentInfoEntity.setStoragePath((String)m.get("storagePath"));
		attachmentInfoEntity.setDocType((String)m.get("docType"));
		attachmentInfoEntity.setBasicModelProperty(custId, true);
		attachmentRepository.save(attachmentInfoEntity);
		
		return new ResultVo(true);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo registerNew(Map<String, Object> custInfoMap) {
		
		// 生成昵称
		String loginName = flowNumberService.generateUserNickName();
		custInfoMap.put("loginName", loginName);
		
		// 判断邀请码是否为手机号，若为手机号则找到对应的邀请码
		if(custInfoMap.containsKey("inviteCode")) { 
			if(RuleUtils.isMobile(custInfoMap.get("inviteCode"))){ // 验证邀请码为手机号
				CustInfoEntity custInfoEntity = custInfoRepository.findByMobile((String)custInfoMap.get("inviteCode"));
				if(custInfoEntity == null) {
					return new ResultVo(false, "推荐人不存在");
				}
				custInfoMap.put("inviteCode", custInfoEntity.getInviteCode());
			}
		}
		//判断是否传来 集团活动id
		if (!StringUtils.isEmpty(custInfoMap.get("activityCode"))&& Constant.ACTIVITY_ID_REGIST_10.equals(custInfoMap.get("activityCode").toString())) {
			custInfoMap.put("activityCode", custInfoMap.get("activityCode").toString());
		}
		// 2017 6月市场部活动
		if (!StringUtils.isEmpty(custInfoMap.get("activityCode"))&& Constant.ACTIVITY_ID_REGIST_13.equals(custInfoMap.get("activityCode").toString())) {
			ResultVo activity201706Vo = activity201706(custInfoMap);
			if(!ResultVo.isSuccess(activity201706Vo)){
				return activity201706Vo;
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>) activity201706Vo.getValue("data");
			custInfoMap.putAll(data);
		}
		
		ResultVo result = register(custInfoMap);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("loginName", loginName);
		result.putValue("data", map);
		
		return result;
	}
	
	// 2017 6月市场部活动
	private ResultVo activity201706(Map<String, Object> custInfoMap) {
		String inviteCode1 = (String) custInfoMap.get("inviteCode1"); // 客户经理邀请码
		String inviteCode2 = (String) custInfoMap.get("inviteCode2"); // 邀请人邀请码
		String activityCode =  (String) custInfoMap.get("activityCode");
		if(!Constant.ACTIVITY_ID_REGIST_13.equals(activityCode)){
			return new ResultVo(false, "内部调用出错");
		}
		ActivityInfoEntity activityInfoEntity = activityInfoRepository.findByIdAndActivityStatusAndDate(Constant.ACTIVITY_ID_REGIST_13, Constant.VALID_STATUS_VALID, DateUtils.getStartDate(new Date()));
		if(activityInfoEntity == null) { // 活动不存在或者已结束不予奖励
			return new ResultVo(false, "活动不存在或者已结束!请用其他渠道进行注册!");
		}
		
		if(StringUtils.isEmpty(inviteCode1)){
			return new ResultVo(false, "客户经理邀请码或手机号不能为空");
		}
		CustInfoEntity custInfo1;
		if(RuleUtils.isMobile(inviteCode1)){ // 验证邀请码为手机号
			custInfo1 = custInfoRepository.findByMobile(inviteCode1);
			if(custInfo1 == null || !"是".equals(custInfo1.getIsRecommend())) {
				return new ResultVo(false, "客户经理不存在或输入用户不是客户经理");
			}
		} else {
			custInfo1 = custInfoRepository.findByInviteCode(inviteCode1);
			if(custInfo1 == null || !"是".equals(custInfo1.getIsRecommend())) {
				return new ResultVo(false, "客户经理不存在或输入用户不是客户经理");
			}
		}
		String inviteCustId1 = custInfo1.getId(); // 邀请1的custId
		Map<String, Object> data = Maps.newHashMap();
		data.put("inviteCode", custInfo1.getInviteCode());// 匹配原来的逻辑（直接邀请人）
		
		// 
		if(!StringUtils.isEmpty(inviteCode2)){
			CustInfoEntity custInfo2;
			if(RuleUtils.isMobile(inviteCode2)){ // 验证邀请码为手机号
				custInfo2 = custInfoRepository.findByMobile(inviteCode2);
				if(custInfo2 == null) {
					return new ResultVo(false, "邀请人不存在");
				}
			} else {
				custInfo2 = custInfoRepository.findByInviteCode(inviteCode2);
				if(custInfo2 == null) {
					return new ResultVo(false, "邀请人不存在");
				}
			}
			String inviteCustId2 = custInfo2.getId(); // 邀请2的custId
			// 根据关系表查 update 2017-06-23
			CustRecommendInfoEntity custRecommendInfo = custRecommendInfoRepository.findInfoCustRecommendByQuiltCustId(inviteCustId2);
			if(custRecommendInfo != null) {
				if(!inviteCustId1.equals(custRecommendInfo.getCustId())){
					return new ResultVo(false, "邀请人的客户经理不是当前输入的客户经理");
				}
				data.put("inviteCode", custInfo2.getInviteCode()); // 匹配原来的逻辑（直接邀请人） // 【可能导致注册人和经理没有建立关系】
				data.put("inviteCustId1", inviteCustId1);// 【A(经理) B(邀请人) C】是A和C建立关系
				data.put("bandInviteNew", inviteCustId1);
			} else if(custRecommendInfo == null && !inviteCustId2.equals(inviteCustId1)){ // 邀请人没有客户关系-》建立， 防止客户经理把邀请人也填自己
				data.put("bandInvite2", true); // 邀请人没有经理，就把输入的经理也给邀请人
				data.put("inviteCustId1", inviteCustId1);
				data.put("inviteCustId2", inviteCustId2);
//				data.put("inviteCode", custInfo1.getInviteCode());// 如果邀请人没有客户经理，邀请人设置是为了后面方法新建客户关系表，注释是该值没有被重新复制
			}
			
//			// 判断邀请人的邀请经理是不是 输入的客户经理
//			CustInfoEntity cust = custInfo2;
//			while(true) {
//				if(StringUtils.isEmpty(cust.getInviteOriginId())){
//					data.put("inviteCustId1", inviteCustId1);
//					data.put("inviteCustId2", inviteCustId2);
//					data.put("bandInvite2", true);
//					data.put("inviteCode", custInfo2.getInviteCode()); // 邀请人设置
//					break;
//				}
//				CustInfoEntity parent = custInfoRepository.findOne(cust.getInviteOriginId());
//				if(parent == null){
//					data.put("inviteCustId1", inviteCustId1);
//					data.put("inviteCustId2", inviteCustId2);
//					data.put("bandInvite2", true);
//					data.put("inviteCode", custInfo2.getInviteCode());// 邀请人设置
//					break;
//				} else {
//					// 邀请人的邀请经理 不是 输入的客户经理
//					if("是".equals(parent.getIsRecommend())){
//						if(!inviteCustId1.equals(parent.getId())){
//							return new ResultVo(false, "邀请人的邀请客户经理不是当前输入的客户经理");
//						} else {
//							// 如果是同一个客经理，则就是注册客户的邀请人
//							data.put("inviteCode", custInfo2.getInviteCode());// 匹配原来的逻辑（直接邀请人）
//							break;
//						}
//					} else {
//						cust = parent;
//						continue;
//					}
//				}
//			}
		}
		return new ResultVo(true, "处理成功", data);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo updateUserNickName(Map<String, Object> params) {
		
		// 1) 验证用户是否有效
		String custId = (String) params.get("custId");
		String newLoginName = (String)params.get("newLoginName");
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null){
			return new ResultVo(false, "用户信息不存在！");
		}
		
		// 非正常状态
		if(!Constant.REG_ENABLE_STATUS.equals(custInfoEntity.getEnableStatus())) {
			return new ResultVo(false, "您的账户处于非正常状态，不能修改用户名！");
		}
		
		// 2) 检查新昵称是否存在
		CustInfoEntity tmpCustInfoEntity = custInfoRepository.findByLoginName(newLoginName);
		if(tmpCustInfoEntity != null) {
			return new ResultVo(false, "用户名已存在，请重新输入");
		}
		
		// 3) 检查是否第一次修改
		List<LogInfoEntity> logList = logInfoEntityRepository.findByRelatePrimaryAndLogType(custId, Constant.OPERATION_TYPE_25);
		if(logList != null && logList.size() > 0) {
			return new ResultVo(false, "您已经修改过一次用户名，不能再修改");
		}
		
		// 4) 更新用户昵称
		String oldLoginName = custInfoEntity.getLoginName(); 
		custInfoEntity.setLoginName(newLoginName);
		custInfoEntity.setBasicModelProperty(custId, false);
		
		// 5) 记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
		logInfoEntity.setRelatePrimary(custId);
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_25);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setMemo(String.format("用户名由[%s]修改为[%s]",
				oldLoginName, newLoginName));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true, "修改用户名成功");
	}

	@Override
	public ResultVo checkUpdatedUserNickName(Map<String, Object> params) {
		List<LogInfoEntity> logList = logInfoEntityRepository.findByRelatePrimaryAndLogType((String)params.get("custId"), Constant.OPERATION_TYPE_25);
		if(logList != null && logList.size() > 0) {
			return new ResultVo(false, "已经修改过用户名");
		}
		return new ResultVo(true);
	}
	
	/** 线上线下-同步状态变更 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void changeWealthFlagById(String custId, String wealthFlag) {
		CustInfoEntity custInfo = custInfoRepository.findOne(custId);
		custInfo.setWealthFlag(wealthFlag);
		custInfo.setBasicModelProperty(custId, false);
	}

	@Override
	public CustInfoEntity findCustInfoByCardIdAndIsEmployee(
			String credentialsCode) {
		return  custInfoRepository.findCustInfoByCardIdAndIsEmployee(credentialsCode);
	}

	@Override
	public CustInfoEntity findCustInfoById(Map<String, Object> params) {
		
		CustInfoEntity custInfo = custInfoRepository.findOne((String) params.get("custId"));
		if( null != custInfo) {
			String mobile = custInfo.getMobile();
			String idcard = custInfo.getCredentialsCode();
			custInfo.setMobile(Strings.isNullOrEmpty(mobile) ? null : mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 3, mobile.length()));
			custInfo.setCredentialsCode(Strings.isNullOrEmpty(idcard) ? null : idcard.substring(0, 4) + "****" + idcard.substring(idcard.length() - 4, idcard.length()));
		}
		return custInfo;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo createLoanCust(Map<String, Object> params) throws SLException {
		
		String credentialsCode = (String)params.get("credentialsCode");
		String custName = (String)params.get("custName");
		String custCode = (String)params.get("custCode");
		String userId = (String)params.get("userId");
		Map<String, Object> custMap = (Map<String, Object>) params.get("cust");
		
		List<CustInfoEntity> custInfoList = custInfoRepository.findByCredentialsCode(credentialsCode);
		CustInfoEntity custInfo = null;
		
		// 商户申请判断
		if(custMap != null && !StringUtils.isEmpty(custMap.get("accountType")) && Constant.ACCOUNT_TYPE_02.equals(custMap.get("accountType"))) {
			if(custInfoList != null && custInfoList.size() > 0){
				throw new SLException("银行卡号已存在！");
			}
			int names = custInfoRepository.findByCustTypeAndCustName(Constant.CUST_TYPE_COMPNAY ,custName);
			if(names > 0){
				throw new SLException("商户名称已存在！");
			}
		} else if(custInfoList != null && custInfoList.size() > 0){// 借款用户申请 和 其他
			if(custInfoList.size()==1 && custName.equals(custInfoList.get(0).getCustName())){
				custInfo = custInfoList.get(0);
			} else if(custInfoList.size()==1 && !custName.equals(custInfoList.get(0).getCustName())){
				throw new SLException("证件号码已存在，客户名称不一致！");
			} else {
				throw new SLException("客户信息已存在！");
			}
		}
		
//		CustInfoEntity custInfo = custInfoRepository.findByCredentialsCodeAndCustName(credentialsCode, custName);
		// 判断理财客户表有没有 借款客户的账号，没有生成一个并生成总账和分账
		if(custInfo == null){
			// 身份证号码（"来源-身份证号码"）相同且客户类型为借款客户 -----修改项目客户ID为当前用户
			custInfo = new CustInfoEntity();
			custInfo.setBasicModelProperty(userId, true);
			custInfo.setCredentialsCode(credentialsCode);
			custInfo.setCustName(custName);
			
			custInfo.setCredentialsType(Constant.CREDENTIALS_ID_CARD);
			custInfo.setCustType(Constant.CUST_TYPE_LOANER);
			custInfo.setCustCode(StringUtils.isEmpty(custCode) ? flowNumberService.generateCustomerNumber() : custCode);
			custInfo.setEnableStatus(Constant.ENABLE_STATUS_02); // 默认冻结
			custInfo = custInfoRepository.save(custInfo);
			
			// 生成总账和分账 (总账-custId，分账-loanId)
			// 总账
			AccountInfoEntity loanerAccount = new AccountInfoEntity();
			loanerAccount.setBasicModelProperty(userId, true);
			loanerAccount.setCustId(custInfo.getId());
			loanerAccount.setAccountNo(flowNumberService.generateCustomerNumber());
			loanerAccount = accountInfoRepository.save(loanerAccount);
			
			// 更新额外的客户信息
			if(custMap != null) {
				String accountType = (String)custMap.get("accountType");
				String telephone = (String)custMap.get("telephone");
				String address = (String)custMap.get("address");
				String password = (String)custMap.get("password");
				
				if(!StringUtils.isEmpty(telephone)) {
					custInfo.setTel(telephone);
				}
				if(!StringUtils.isEmpty(address)) {
					custInfo.setCommunAddress(address);
				}
				if(!StringUtils.isEmpty(password)) {
					custInfo.setLoginPassword(Hashing.md5().hashString(password, Charsets.UTF_8).toString());
				}
				if(!StringUtils.isEmpty(accountType)) { 
					if(Constant.ACCOUNT_TYPE_02.equals(accountType)) {
						custInfo.setEnableStatus(Constant.ENABLE_STATUS_01);
						custInfo.setCredentialsType(Constant.ACCOUNT_TYPE_02);
						custInfo.setCustType(Constant.CUST_TYPE_COMPNAY);
						custInfo.setLoginName(custName);
						
						loanerAccount.setAccountType(Constant.ACCOUNT_TYPE_02);
					}
				}
			}
		}
		return new ResultVo(true, "保存客户成功", custInfo);
	}

	@Override
	public ResultVo queryEmployee(String mobile) {
		CustInfoEntity custInfoEntity = custInfoRepository.findByMobile(mobile);
		if(custInfoEntity == null) {
			return new ResultVo(false, "业务员不存在");
		}
		
		if(!Constant.IS_RECOMMEND_YES.equals(custInfoEntity.getIsEmployee())) {
			return new ResultVo(false, "业务员不存在");
		}
		
		return new ResultVo(true, "", custInfoEntity);
	}

	/**
	 * 企业商户-商户列表
	 * @author liyy
	 * @date   2016年12月16日下午15:30:00
	 * @param param
	 *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
	 * @return
     *      <tt>custId                :String:客户编号</tt><br>
     *      <tt>custName              :String:姓名</tt><br>
     *      <tt>accountAvailableAmount:String:账户余额</tt><br>
     *      <tt>bankName              :String:银行卡名称</tt><br>
     *      <tt>cardNo                :String:银行卡卡号</tt><br>
     *      <tt>tel                   :String:联系电话（客户表联系电话）</tt><br>
     *      <tt>address               :String:地址（客户表通讯地址）</tt><br>
	 */
	public ResultVo queryCompanyUserList(Map<String, Object> params) {
		Page<Map<String, Object>> pageVo = custInfoRepositoryImpl.queryCompanyUserList(params);
		
		Map<String, Object> data = Maps.newHashMap();
		data.put("iTotalDisplayRecords", pageVo.getTotalElements());
		data.put("data", pageVo.getContent());
		return new ResultVo(true, "查询成功", data);
	}

	@Override
	public CustInfoEntity findByLoginNameOrMobile(Map<String, Object> param) {
		String loginName = (String) param.get("loginName");
		CustInfoEntity cust = custInfoRepository.findByLoginName(loginName);
		if (cust != null) {
			return cust;
		}
		cust = custInfoRepository.findByMobile(loginName);
		return cust;
	}

	@Override
	public ResultVo checkUserTradePassword(String custId, String tradePassword) {
		
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null){
			return new ResultVo(false, "用户不存在！");
		}
		// 判断客户状态
		if(!Constant.ENABLE_STATUS_01.equals(custInfoEntity.getEnableStatus())){
			return new ResultVo(false, "账户为非正常状态，可能被冻结，请联系客服！");
		}
		// 判断是否实名认证
		if (StringUtils.isEmpty(custInfoEntity.getCustName())
				|| StringUtils.isEmpty(custInfoEntity.getCredentialsCode())) {
			return new ResultVo(false, "未实名认证，请先做实名认证!");
		}
		// 判断交易密码是否正确
		if (StringUtils.isEmpty(tradePassword) || !tradePassword.equals(custInfoEntity.getTradePassword())) {
			return new ResultVo(false, "交易密码错误,请重新输入!");
		}
		return new ResultVo(true, "校验成功", custInfoEntity);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo setRiskAssessment(Map<String, Object> params) {
		String custId = (String)params.get("custId");
		String riskAssessment = (String) params.get("riskAssessment");
		String riskAssessmentAnswer = (String) params.get("riskAssessmentAnswer");
		
		CustInfoEntity cie = custInfoRepository.findOne(custId);
		cie.setBasicModelProperty(custId, false);
		cie.setRiskAssessment(riskAssessment);
		cie.setRiskAssessmentAnswer(riskAssessmentAnswer);
		cie.setAssessTime(new Date());
		custInfoRepository.save(cie);
		
		// 新增记录日志 add by liyy 2017-03-23
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
		logInfoEntity.setRelatePrimary(cie.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_19);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setOperPerson(cie.getId());
		logInfoEntity.setMemo(String.format("%s设置了风险评估",cie.getLoginName()));
		logInfoEntity.setBasicModelProperty(cie.getId(), true);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true, "操作成功");
	}

	@Override
	public ResultVo queryCustRiskList(Map<String, Object> params)  throws SLException{

		Page<Map<String, Object>> pageVo = custInfoRepositoryImpl.queryCustRiskList(params);
		
		Map<String, Object> data = Maps.newHashMap();
		data.put("iTotalDisplayRecords", pageVo.getTotalElements());
		data.put("data", pageVo.getContent());
		return new ResultVo(true, "查询成功", data);
	}

}
