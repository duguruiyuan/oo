package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.ActivityAwardEntity;
import com.slfinance.shanlincaifu.entity.CustActivityInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.SystemMessageInfoEntity;
import com.slfinance.shanlincaifu.repository.ActivityAwardRepository;
import com.slfinance.shanlincaifu.repository.CustActivityInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.SystemMessageInfoRepository;
import com.slfinance.shanlincaifu.repository.UserRepository;
import com.slfinance.shanlincaifu.repository.custom.ActivityAwardRepositoryCustom;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.RedPacketManagementService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

@Service("redPacketManagementService")
public class RedPacketManagementServiceImpl implements RedPacketManagementService{
	
	@Autowired
	private ActivityAwardRepository activityAwardRepository;

	@Autowired
	private ActivityAwardRepositoryCustom activityAwardRepositoryCustom;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private CustActivityInfoRepository custActivityInfoRepository;
	
	@Autowired
	private SystemMessageInfoRepository systemMessageInfoRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private  SMSService smsService;
	/**
	 * 查询红包信息
	 * @param params
	 * @return
	 */
	@Override
	public Map<String, Object> queryRedPacketList(Map<String, Object> params) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Page<Map<String, Object>> page = activityAwardRepositoryCustom.queryRedPackedList(params);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		return resultMap;
	}
	
	/**
	 * 新增红包
	 * @param params
	 * @return
	 * @throws SLException 
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveRedPacketInfo(Map<String, Object> params) throws SLException {
		//ActivityAwardEntity activityAwardEntity = BeanMapConvertUtil.toBean(ActivityAwardEntity.class, params);
		String awardName = (String) params.get("awardName");
		String awardType = (String) params.get("awardType");
		String grantAmount = (String) params.get("grantAmount");
		String startTime = (String) params.get("startTime");
		String deadlineTime = (String) params.get("deadlineTime");
		String startAmount =(String) (params.get("startAmount") == null ? "0" : (String) params.get("startAmount"));
		String useScope =(String) (params.get("useScope") == null ? "" : (String) params.get("useScope"));
		Integer loanAllottedTime = (String) params.get("loanAllottedTime") == null ? null : Integer.valueOf((String) params.get("loanAllottedTime"));
		Integer seatTerm = (String) params.get("seatTerm") == null ? new Integer(0) : Integer.valueOf((String) params.get("seatTerm"));
		String isTransfer =(String) (params.get("isTransfer") == null ? "" : (String) params.get("isTransfer"));
		String subjectRepaymentMethods =(String) (params.get("subjectRepaymentMethods") == null ? "" : (String) params.get("subjectRepaymentMethods"));
		String increaseUnit =(String) (params.get("increaseUnit") == null ? "" : (String) params.get("increaseUnit"));
		Date startDate = null;
		Date endDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(deadlineTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		ActivityAwardEntity activityAwardEntity = new ActivityAwardEntity();
		activityAwardEntity.setAwardName(awardName);
		activityAwardEntity.setAwardType(awardType);
		activityAwardEntity.setGrantAmount(new BigDecimal(grantAmount));
		activityAwardEntity.setStartTime(startDate);
		activityAwardEntity.setDeadlineTime(endDate);
		activityAwardEntity.setStartAmount(new BigDecimal(startAmount));
		activityAwardEntity.setUseScope(useScope);
		activityAwardEntity.setLoanAllottedTime(loanAllottedTime);
		activityAwardEntity.setSeatTerm(seatTerm);
		activityAwardEntity.setIsTransfer(isTransfer);
		activityAwardEntity.setIncreaseUnit(increaseUnit);
		activityAwardEntity.setSubjectRepaymentMethods(subjectRepaymentMethods);
		activityAwardEntity.setAwardStatus(Constant.DISABLE_RED_PACKETS);
		activityAwardEntity.setBasicModelProperty((String) params.get("custId"), true);
		activityAwardRepository.save(activityAwardEntity);
		return new ResultVo(true, "新增红包成功");
	}

	/**
	 * 查询红包详情
	 * @param params
	 * @return
	 * @throws ParseException 
	 */
	@Override
	public Map<String, Object> queryRedPacketUseDetails(
			Map<String, Object> params) throws ParseException {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = activityAwardRepositoryCustom.queryRedPacketUseDetails(params);
		Map<String, Object> usedData = activityAwardRepositoryCustom.queryUsedData(params);
		Map<String, Object> TotalData = activityAwardRepositoryCustom.queryTotalData(params);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		result.put("usedData", usedData);
		result.put("TotalData", TotalData);
		return result;
	}

	/**
	 * 更新红包状态
	 * @param params
	 * @return
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo updateRedPacketStatus(Map<String, Object> params) {

		//红包状态
		String awardStatus = (String) params.get("awardStatus");
		//红包Id
		String awardId = (String) params.get("awardId");
		//大后台操作人
		String updateUser = (String) params.get("custId");
		//是否发送短信
//		String isSendMsg = (String) params.get("isSendMsg");
		//短信内容
//		String sendContent = (String) params.get("sendContent");
		ActivityAwardEntity activityAwardEntity = activityAwardRepository.findOne(awardId);
//		Set<String> messageSet = new HashSet<String>();
		//如果红包状态为启用
		if ((Constant.ENABLE_RED_PACKETS).equals(awardStatus)) {
			
			activityAwardEntity.setAwardStatus(Constant.ENABLE_RED_PACKETS);
			activityAwardEntity.setLastUpdateUser(updateUser);
			activityAwardEntity.setLastUpdateDate(new Date());
//			String amount = "";
//			//根据红包Id和用户红包交易状态查询出交易状态为已发放的红包
//			List<CustActivityInfoEntity> list = custActivityInfoRepository.findByAwardIdAndTradeStatus(awardId,Constant.ALREADY_ISSUED);
//			if (null != list && list.size() > 0) {
//				
//				for (CustActivityInfoEntity custActivityInfoEntity : list) {
//					//红包类型
//					String awardType = custActivityInfoEntity.getRewardShape();
//					//用户Id
//					String custId = custActivityInfoEntity.getCustId();
//					//红包金额
//					String totalAmount = custActivityInfoEntity.getTotalAmount().toString();
//					//根据用户Id查询用户信息
//					CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
//					//用户手机号
//					String mobile = custInfoEntity.getMobile();
//					CustInfoEntity systemEntity = custInfoRepository.findOne(Constant.CUST_ADMIN_ID);
//					//更改用户红包状态（已发放—→已领取）
//					custActivityInfoEntity.setTradeStatus(Constant.ALREADY_RECEIVED);
//					//发送系统站内信
//					SystemMessageInfoEntity systemMessageInfoEntity = new SystemMessageInfoEntity();
//					systemMessageInfoEntity.setSendCust(systemEntity);
//					systemMessageInfoEntity.setReceiveCust(custInfoEntity);
//					systemMessageInfoEntity.setSendTitle(Constant.VOUCHER_ARRIVAL_NOTICE);
//					systemMessageInfoEntity.setSendContent(sendContent);
//					//加息劵
//					if (Constant.REAWARD_SPREAD_05.equals(awardType)) {
//						systemMessageInfoEntity.setSendContent(String.format("【善林财富 】 恭喜您获得%s%%的加息券，快去登录善林财富客户端使用吧~",totalAmount));
//						amount = totalAmount +"%";
//					//满减红包
//					}else if (Constant.REAWARD_SPREAD_04.equals(awardType)){
//						systemMessageInfoEntity.setSendContent(String.format("【善林财富 】 恭喜您获得%s元满减红包，快去登录善林财富客户端使用吧~",totalAmount));
//						amount = totalAmount +"元";
//					//体验金
//					}else {
//						systemMessageInfoEntity.setSendContent(String.format("【善林财富 】 恭喜您获得%s元体验金，快去登录善林财富客户端使用吧~",totalAmount));
//						amount = totalAmount + "元";
//					}
//					systemMessageInfoEntity.setSendDate(new Date());
//					systemMessageInfoEntity.setIsRead(Constant.SITE_MESSAGE_NOREAD);
//					systemMessageInfoEntity.setBasicModelProperty(updateUser, true);
//					systemMessageInfoRepository.save(systemMessageInfoEntity);
//					//是否发送短信，如果是
//					if (Constant.IS_SEND_MSG.equals(isSendMsg)) {
//						//如果true
//						if (messageSet.add(mobile)) {
//							//发送短信
//							Map<String, Object> smsParams = new HashMap<String, Object>();
//							smsParams.put("mobile", mobile);
//							smsParams.put("custId", custInfoEntity.getId());
//							smsParams.put("sendContent", sendContent);
////							smsParams.put("values",new Object[]{
////									amount,
////									awardType
////							});
//							smsService.asnySendRedSMS(smsParams);
//						}
//					}
//				}
//			}
		}else if(Constant.DISABLE_RED_PACKETS.equals(awardStatus)) {
			activityAwardEntity.setAwardStatus(Constant.DISABLE_RED_PACKETS);
			activityAwardEntity.setLastUpdateUser(updateUser);
			activityAwardEntity.setLastUpdateDate(new Date());
		}else{//系统红包的失效操作
			activityAwardEntity.setAwardStatus(Constant.UNABLE_RED_PACKETS);
			activityAwardEntity.setLastUpdateUser(updateUser);
			activityAwardEntity.setLastUpdateDate(new Date());
		}
		return new ResultVo(true,"状态更新成功");
	}
	
	
	/**
	 * 导入用户红包信息
	 * @param params
	 * @return
	 * @throws ParseException 
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@SuppressWarnings("unchecked")
	public ResultVo importCustRedPacketInfo(Map<String, Object> params) throws ParseException {
		//大后台操作人Id
		String creatId = (String) params.get("custId");
		//红包金额
		String totalAmount = (String) params.get("totalAmount");
		//红包有效期-开始时间
		String startDate = ((String) params.get("startDate")).replaceAll("-", "/");
		//红包有效期-过期时间
		String expireDate = ((String) params.get("expireDate")).replaceAll("-", "/");
		//红包有效期-红包类型
		String rewardShape = (String) params.get("rewardShape");
		//红包Id
		String awardId  = (String) params.get("awardId");
		//红包名字
		String activitySource  = (String) params.get("awardName");
		//短信内容
		String sendContent = (String) params.get("sendContent");
		//是否发送短信
		String isSendMsg = (String) params.get("isSendMsg");
		ActivityAwardEntity activityAwardEntity = activityAwardRepository.findOne(awardId);
		String awardStatus = activityAwardEntity.getAwardStatus();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		List<Map<String, Object>> list = (List<Map<String, Object>>) params.get("bigList");
		Set<String> messageSet = new HashSet<String>();
		for (int i = 0; i < list.size(); i++) {
			String mobile = ((String) list.get(i).get("mobile")).trim();                                  
			//验证手机号码是否正确
			if (StringUtils.isBlank(mobile)) {
				return new ResultVo(false, "手机号不能为空");
			}
			Pattern pattern = Pattern.compile("^((13[0-9])|(147)|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$");
			Matcher matcher = pattern.matcher(mobile);
			if (!matcher.matches()){
				return new ResultVo(false, "手机号"+mobile+"格式不正确");
			}
			CustInfoEntity custInfo= custInfoRepository.findByMobile(mobile);
			if (null == custInfo) {
				return new ResultVo(false,"手机号为"+mobile+"用户不存在");
			}     
			CustActivityInfoEntity custActivityInfoEntity = new CustActivityInfoEntity();
			custActivityInfoEntity.setCustId(custInfo.getId());
			custActivityInfoEntity.setTotalAmount(new BigDecimal(totalAmount));
			custActivityInfoEntity.setTradeCode(numberService.generateActivitySequnce());
//			if (Constant.ENABLE_RED_PACKETS.equals(awardStatus)) {
//				custActivityInfoEntity.setTradeStatus(Constant.ALREADY_RECEIVED);
//			}else {
//				custActivityInfoEntity.setTradeStatus(Constant.ALREADY_ISSUED);
//			}
			custActivityInfoEntity.setTradeStatus(Constant.ALREADY_RECEIVED);
			custActivityInfoEntity.setStartDate(sdf.parse(startDate));
			custActivityInfoEntity.setExpireDate(sdf.parse(expireDate));
			custActivityInfoEntity.setRecordStatus(Constant.EFFECTIVE_RED_PACKETS);
			custActivityInfoEntity.setCreateUser(creatId);
			custActivityInfoEntity.setCreateDate(new Date());
			custActivityInfoEntity.setRewardShape(rewardShape);
			custActivityInfoEntity.setActivityAwardId(awardId);
			custActivityInfoEntity.setActivitySource(activitySource);
			custActivityInfoEntity.setActivityDesc(activitySource);
			custActivityInfoEntity.setUsableAmount(new BigDecimal(totalAmount));
			custActivityInfoEntity.setActivityId(Constant.ACTIVITY_ID_REGIST_15);
			custActivityInfoRepository.save(custActivityInfoEntity);
			
			//导入时发送消息
			String amount = "";
//			if (Constant.ENABLE_RED_PACKETS.equals(awardStatus)) {
				CustInfoEntity custInfoEntity = custInfoRepository.findByMobile(mobile);
//				//加息券
//				if (Constant.REAWARD_SPREAD_05.equals(rewardShape)) {
//					//String awardAmount = custActivityInfoEntity.getTotalAmount().multiply(new BigDecimal(100)).toString();
//					systemMessageInfoEntity.setSendContent(String.format("【善林财富 】 恭喜您获得%s%%的加息券，快去登录善林财富客户端使用吧~",totalAmount));
//					amount = totalAmount +"%";
//				//满减红包	
//				}else if (Constant.REAWARD_SPREAD_04.equals(rewardShape)){
//					systemMessageInfoEntity.setSendContent(String.format("【善林财富 】 恭喜您获得%s元满减红包，快去登录善林财富客户端使用吧~",totalAmount));
//					amount = totalAmount + "元";
//				//体验金
//				}else {
//					systemMessageInfoEntity.setSendContent(String.format("【善林财富 】 恭喜您获得%s元体验金，快去登录善林财富客户端使用吧~",totalAmount));
//					amount = totalAmount + "元";
//				}
				//是否发送短信-如果是
				if (Constant.IS_SEND_MSG.equals(isSendMsg)) {
					if (messageSet.add(mobile)) {
						//发送系统站内消息
						CustInfoEntity systemEntity = custInfoRepository.findOne(Constant.CUST_ADMIN_ID);
						SystemMessageInfoEntity systemMessageInfoEntity = new SystemMessageInfoEntity();
						systemMessageInfoEntity.setSendCust(systemEntity);
						systemMessageInfoEntity.setReceiveCust(custInfoEntity);
						systemMessageInfoEntity.setSendTitle(Constant.VOUCHER_ARRIVAL_NOTICE);
						systemMessageInfoEntity.setSendContent(sendContent);
						systemMessageInfoEntity.setSendDate(new Date());
						systemMessageInfoEntity.setIsRead(Constant.SITE_MESSAGE_NOREAD);
						systemMessageInfoEntity.setBasicModelProperty(creatId, true);
						systemMessageInfoRepository.save(systemMessageInfoEntity);
						//发送短信
						Map<String, Object> smsParams = new HashMap<String, Object>();
						smsParams.put("mobile", mobile);
						smsParams.put("custId", custInfoEntity.getId());
						smsParams.put("sendContent", sendContent);
//					smsParams.put("values",new Object[]{
//							amount,
//							rewardShape
//					});
						smsService.asnySendRedSMS(smsParams);
					}
				}
			}
//		}
		return new ResultVo(true,"导入数据成功");
	}

}
