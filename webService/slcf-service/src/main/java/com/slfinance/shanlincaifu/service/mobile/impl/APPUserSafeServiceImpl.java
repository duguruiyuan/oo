/** 
 * @(#)APPUserSafeServiceImpl.java 1.0.0 2015年5月18日 上午10:30:29  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.mobile.impl;

import java.math.BigDecimal;
import java.util.*;

import com.google.common.collect.Lists;
import com.slfinance.shanlincaifu.repository.custom.CalcCommissionRepositoryCustom;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.repository.BankCardInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.custom.LoanManagerRepositoryCustom;
import com.slfinance.shanlincaifu.service.CustomerService;
import com.slfinance.shanlincaifu.service.RealNameAuthenticationService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.service.mobile.APPUserSafeService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.util.BeanMapConvertUtil;
import com.slfinance.vo.ResultVo;

/**   
 * 用户登陆相关测试
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月18日 上午10:30:29 $ 
 */
@Service
public class APPUserSafeServiceImpl implements APPUserSafeService {

	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private RealNameAuthenticationService realNameAuthenticationService;
	
	@Autowired
	private SMSService sMSService;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	
	@Autowired
	private BankCardInfoRepository bankCardInfoRepository;
	
	@Autowired
	private InvestInfoRepository  investInfoRepository;
	@Autowired
	private LoanManagerRepositoryCustom  loanManagerRepositoryCustom;

	@Autowired
    private CalcCommissionRepositoryCustom calcCommissionRepositoryCustom;

	/**
	 * 忘记密码-发送手机验证码
	 */
	@Override
	public ResultVo checkRegistAndSendSMS(Map<String, Object> parasMap)throws SLException {
		/**校验是否注册**/
		if(!ResultVo.isSuccess(customerService.findByMobile(parasMap)))
			return new ResultVo(false,"手机未注册");
		/**发送短信**/
		return sMSService.sendSMS(parasMap);
	}

	/**
	 * 忘记密码-验证码是否正确
	 */
	@Override
	public ResultVo checkCodeAndUserVerified(Map<String, Object> parasMap)throws SLException {
		/**校验验证码**/
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("verityCode", (String) parasMap.get("code"));
		param.put("messageType",(String)parasMap.get("messageType"));
		param.put("targetAddress", (String)parasMap.get("mobile"));
		ResultVo  result = sMSService.checkSmsCode(param);
		if(!ResultVo.isSuccess(result))
			return result;
		/**查询实名认证信息**/
		CustInfoEntity custInfo = custInfoRepository.findByMobile((String)parasMap.get("mobile"));
		if(null == custInfo)
			throw new SLException("客户信息不存在");
		Map<String,Object> realNameMap = Maps.newHashMap();
		String isRealName = "N";
		if(!StringUtils.isEmpty(custInfo.getCustName()) && !StringUtils.isEmpty(custInfo.getCredentialsCode()) )
			isRealName = "Y";
		realNameMap.put("isRealName",isRealName);
		result.putValue("data",realNameMap);	
		return result;
	}

	/**
	 * 忘记密码-实名认证
	 */
	@Override
	public ResultVo validateIdentity(Map<String, Object> parasMap)throws SLException {
		/**查询实名认证信息**/
		CustInfoEntity custInfo = custInfoRepository.findByMobile((String)parasMap.get("mobile"));
		if(null == custInfo)
			throw new SLException("客户信息不存在");
		if( 1 > custInfoRepository.countByCustNameAndCredentialsCode((String)parasMap.get("custName"), (String)parasMap.get("credentialsCode")) || !custInfo.getCustName().equals((String)parasMap.get("custName")) || !custInfo.getCredentialsCode().equals((String)parasMap.get("credentialsCode")))
			return new ResultVo(false,"姓名或者身份证号错误");
		return new ResultVo(true,"用户通过实名认证");
	}

	/**
	 * 忘记密码-修改
	 */
	@Override
	@Deprecated
	@Transactional
	public ResultVo resetPwd(Map<String, Object> parasMap) throws SLException {
		if(parasMap == null || ( parasMap != null && parasMap.get("smsCode") == null ))
			return new ResultVo(false,"找回密码需要先提交验证码，请更新新版本！");
		Map<String,Object> params = Maps.newHashMap();
		params.put("messageType", Constant.SMS_TYPE_FIND_PASSWORD);
		params.put("verityCode", parasMap.get("smsCode"));
		params.put("targetAddress", parasMap.get("mobile"));
		ResultVo resultVo = sMSService.checkVerityCode(params);
		if(!ResultVo.isSuccess(resultVo))
			return resultVo;
		CustInfoEntity custInfo = custInfoRepository.findByMobile((String)parasMap.get("mobile"));
		if( null == custInfo)
			throw new SLException("用户不存在");
		parasMap.put("id", custInfo.getId());
		parasMap.put("oldPassword", custInfo.getLoginPassword());
		return  customerService.updatePasswordByType(parasMap);
	}

	/**
	 * 忘记密码-修改(校验验证码)
	 */
	@Override
	@Transactional(readOnly=false,rollbackFor = SLException.class)
	public ResultVo resetPassWord(Map<String,Object> parasMap)throws SLException{
		Map<String,Object> params = Maps.newHashMap();
		params.put("messageType", Constant.SMS_TYPE_FIND_PASSWORD);
		params.put("verityCode", parasMap.get("smsCode"));
		params.put("targetAddress", parasMap.get("mobile"));
		ResultVo resultVo = sMSService.checkVerityCode(params);
		if(!ResultVo.isSuccess(resultVo))
			return resultVo;
		CustInfoEntity custInfo = custInfoRepository.findByMobile((String)parasMap.get("mobile"));
		if(null == custInfo)
			throw new SLException("客户信息不存在");
		
		if (custInfo.getTradePassword() != null && custInfo.getTradePassword().equals((String)parasMap.get("password")))
			throw new SLException("登录密码不能和交易密码相同");

		if (!custInfo.getLoginPassword().equals((String)parasMap.get("password"))){
			custInfo.setLoginPassword((String)parasMap.get("password"));
			custInfo.setBasicModelProperty(custInfo.getId(), false);
			custInfoRepository.save(custInfo);
		}
		LogInfoEntity logInfoEntity = new LogInfoEntity(Constant.TABLE_BAO_T_CUST_INFO, custInfo.getId(), Constant.OPERATION_TYPE_19, "", "", String.format("%s修改了登录密码",custInfo.getLoginName()), custInfo.getId());
		logInfoEntity.setBasicModelProperty(custInfo.getId(), true);
		logInfoEntity.setOperIpaddress((String)parasMap.get("operIpaddress"));
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true,"修改成功");
	}
	
	/**
	 * 重设交易密码(校验验证码)
	 */
	@Override
	@Transactional(readOnly=false)
	public ResultVo resetTradePassword(Map<String, Object> params) throws SLException{
		Map<String,Object> parasMap = Maps.newHashMap();
		parasMap.put("messageType", Constant.SMS_TYPE_TRADE_PASSWD);
		parasMap.put("verityCode", params.get("smsCode"));
		parasMap.put("targetAddress", params.get("mobile"));
		ResultVo resultVo = sMSService.checkVerityCode(parasMap);
		if(!ResultVo.isSuccess(resultVo))
			return resultVo;
		String tradePassword = (String) params.get("tradePassword");
		String id = (String) params.get("id");
		CustInfoEntity cie = custInfoRepository.findOne(id);
		if (tradePassword != null && tradePassword.equals(cie.getLoginPassword()))
			return new ResultVo(false, "交易密码不能和登录密码一致！");
		cie.setTradePassword(tradePassword);
		custInfoRepository.save(cie);
		LogInfoEntity logInfoEntity = new LogInfoEntity(Constant.TABLE_BAO_T_CUST_INFO, cie.getId(), Constant.OPERATION_TYPE_19, null, null, String.format("%s重置了交易密码",cie.getLoginName()), cie.getId());
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setBasicModelProperty(cie.getId(), true);
		logInfoEntityRepository.save(logInfoEntity);
		return new ResultVo(true, "操作成功");
	}
	
	/**
	 * 修改手机-修改绑定手机-获取验证码
	 */
	@Override
	public ResultVo validCode(Map<String,Object> parasMap)throws SLException{
		if(null != custInfoRepository.findByMobile((String)parasMap.get("mobile")))
			return new ResultVo(false,"手机号已存在");
		sMSService.sendSMS(parasMap);
		return new ResultVo(true,"获取验证码成功");
	}
	
	/**
	 * 验证码发送次数
	 */
	@Override
	public ResultVo codeSendCount(Map<String, Object> parasMap)throws SLException{
		return new ResultVo(true,"验证码发送次数",sMSService.findByAddressAndTypeAndDate(parasMap));
	}

	/**
	 * 查询客户基本信息和联系人基本信息(善林财富二期)
	 */
	@Override
	public ResultVo getCustAndContactCustInfo(Map<String, Object> param)throws SLException {
		return new ResultVo(true,"用户基本信息",customerService.getCustAndContactCustInfo(param));
	}

	/**
	 * 修改账户用户信息和联系人信息(善林财富二期)
	 */
	@Override
	public ResultVo postCustAndContactCustInfo(Map<String, Object> custInfoMap)throws SLException {
		Map<String,Object> contanctInfo = Maps.newConcurrentMap();
		if(custInfoMap != null && custInfoMap.containsKey("contactId"))
			contanctInfo.put("id", custInfoMap.get("contactId"));
		contanctInfo.put("contactName", custInfoMap.get("contactName"));
		contanctInfo.put("relationType", custInfoMap.get("relationType"));
		contanctInfo.put("contanctTelePhone", custInfoMap.get("contanctTelePhone"));
		custInfoMap.put("contanctInfo", contanctInfo);
		return customerService.updateCustAndContactCustInfo(custInfoMap);
	}

	/**
	 * 登陆返回已绑定银行卡信息(善林财富二期)
	 */
	@Override
	public ResultVo loginMobile(Map<String, Object> parasMap)throws SLException {
		ResultVo resultVo = customerService.loginMobile(parasMap);
		if(!ResultVo.isSuccess(resultVo)) {
			return resultVo;
		}
		CustInfoEntity cie = (CustInfoEntity)resultVo.getValue("data");
		cie.setBankCardInfoEntitys(bankCardInfoRepository.findBingBankListByCustId(cie.getId()));
		return new ResultVo(true, "登录成功", BeanMapConvertUtil.toMap(cie));
	}

	@Override
	public ResultVo getUserInfo(Map<String, Object> parasMap)
			throws SLException {
		String mobile = (String) parasMap.get("mobile");
		CustInfoEntity cie = custInfoRepository.findByMobile(mobile);
		if(cie == null){
			return new ResultVo(false, "客户信息获取失败");
		}

		Map<String, Object> data = BeanMapConvertUtil.toMap(cie);
		data.remove("loginPassword");
//		data.remove("tradePassword");
		BigDecimal  investCount=investInfoRepository.investCountInfoByCustId(cie.getId());
		if(investCount.compareTo(BigDecimal.ZERO)>0){
			data.put("isNewerFlag", "1");//isNewerFlag：  是否投资过
		}else{
			data.put("isNewerFlag", "2");
		}

		/** 2017-5-11 查询登录人的工号、级别 */
        List<Map<String, Object>> rankingInfo = calcCommissionRepositoryCustom.getRankingByCredentialsCode(cie.getCredentialsCode());
        if (!CommonUtils.isEmpty(rankingInfo)) {
            data.put("ranking", rankingInfo.get(0).get("RANKING"));
            data.put("jobNo", rankingInfo.get(0).get("JOB_NO"));
        } else {
            data.put("ranking", "");
            data.put("jobNo", "");
        }
        /** 2017-6-9 查询用户投资保有量对应的级别 */
        Map<String, Object> holdAmountRanking = getUserRanking(cie.getId());
        if (!CommonUtils.isEmpty(holdAmountRanking)) {
            data.put("holdAmountRanking", holdAmountRanking.get("ranking"));
            data.put("showHoldAmountRanking", holdAmountRanking.get("isShow"));
        } else {
            data.put("holdAmountRanking", 0);
            data.put("showHoldAmountRanking", "0");
        }

		return new ResultVo(true, "登录成功", data);
	}

	@Override
	public ResultVo checkLoginPassword(Map<String, Object> parasMap) {
		String custId = (String) parasMap.get("custId");
		String password = (String) parasMap.get("password");
		CustInfoEntity cie = custInfoRepository.findOne(custId);
		if(cie == null || !cie.getLoginPassword().equals(password)){
			return new ResultVo(false, "密码错误");
		}
		return new ResultVo(true, "密码正确");
	}

    @Override
    public ResultVo getUserRanking(Map<String, Object> paramsMap) {
        Map<String, Object> result = Maps.newHashMap();

        String custId = (String) paramsMap.get("custId");
        // 查询用户当前持有的总存续金额
        BigDecimal holdAmount = CommonUtils.emptyToDecimal(custInfoRepository.getHoldAmount(custId));

        // 查询历史最大级别
        int initRank = CommonUtils.emptyToInt(custInfoRepository.findMaxRanking(custId));
        // 计算当前级别
        int currRank = getRank(holdAmount);

        int raking = initRank;// 默认级别为历史最高级别
        String isShow = "0";// 图标默认不点亮
        if (currRank > 0 && currRank >= initRank) {// 如果当前级别大于历史最大级别，则返回当前级别，并点亮图标
            raking = currRank;
            isShow = "1";
        }

        result.put("ranking", raking);
        result.put("isShow", isShow);

        return new ResultVo(true, "查询成功", result);
    }

    @Override
    public Map<String, Object> getUserRanking(String custId) {
        Map<String, Object> result = Maps.newHashMap();

        // 查询用户当前持有的总存续金额
        BigDecimal holdAmount = CommonUtils.emptyToDecimal(custInfoRepository.getHoldAmount(custId));

        // 查询历史最大级别
        int initRank = CommonUtils.emptyToInt(custInfoRepository.findMaxRanking(custId));
        // 计算当前级别
        int currRank = getRank(holdAmount);

        int raking = initRank;// 默认级别为历史最高级别
        String isShow = "0";// 图标默认不点亮
        if (currRank > 0 && currRank >= initRank) {// 如果当前级别大于历史最大级别，则返回当前级别，并点亮图标
            raking = currRank;
            isShow = "1";
        }

        result.put("ranking", raking);
        result.put("isShow", isShow);
        return result;
    }

    @Override
    public ResultVo getUserRankingDetail(Map<String, Object> paramsMap) {
        String custId = (String) paramsMap.get("custId");
        HashMap<String, Object> result = Maps.newHashMap();

        /** 1.当前级别 */
        String showCurr = "0", showInit = "0";// 图标默认不点亮
        // 查询用户当前持有的总存续金额
        BigDecimal holdAmount = CommonUtils.emptyToDecimal(custInfoRepository.getHoldAmount(custId));
        int currRank = getRank(holdAmount);
        if (currRank > 0) {
            showCurr = "1";
        }
        // 计算距下个级别相差的金额
        Map<String, Object> gepMap = getGapToNextRanking(holdAmount);

        /** 2.查询历史最大级别 */
        int initRank = CommonUtils.emptyToInt(custInfoRepository.findMaxRanking(custId));
        if (currRank >= initRank) {// 如果当前级别大于历史最大级别，则返回当前级别，并点亮图标
            initRank = currRank;
            showInit = "1";
        }

        /** 3.查询历史点亮记录 */
        // 查询历史级别和点亮该级别的日期
        List<Map<String, Object>> dateAndRanking = custInfoRepository.getFirstAchievingDateAndRanking(custId);

        List<Map<String, Object>> records = Lists.newArrayList();
        if (!CommonUtils.isEmpty(dateAndRanking)) {
            // 如果历史记录多于1条，根据第n+1条记录的开始时间查询第n条记录的结束时间
            if (dateAndRanking.size() > 1) {
                for (int i = 0; i < dateAndRanking.size(); i++) {
                    Map<String, Object> record = Maps.newHashMap();
                    Map<String, Object> map = dateAndRanking.get(i);
                    int ranking = CommonUtils.emptyToInt(map.get("RANKING"));
                    String beginDate = CommonUtils.emptyToString(map.get("MIN_DATE"));
                    if (i < dateAndRanking.size() - 1) {
                        Map<String, Object> next = dateAndRanking.get(i + 1);
                        String nextBeginDate = CommonUtils.emptyToString(next.get("MIN_DATE"));
                        // 查询首次达到当前等级的时间段的最后时间
                        List<Map<String, Object>> endDate = custInfoRepository.getEndDateOfFirstAchieving(custId, ranking, nextBeginDate);
                        String max_date = "";
                        if (!CommonUtils.isEmpty(endDate)) {
                            max_date = CommonUtils.emptyToString(endDate.get(0).get("MAX_DATE"));
                        }
                        record.put("ranking", ranking);
                        record.put("beginDate", DateUtils.formatDate(DateUtils.parseDate(beginDate, "yyyyMMdd"), "yyyy-MM-dd"));
                        record.put("endDate", DateUtils.formatDate(DateUtils.parseDate(max_date, "yyyyMMdd"), "yyyy-MM-dd"));
                        records.add(record);
                    } else {
                        if (ranking != currRank) {
                            record.put("ranking", ranking);
                            record.put("beginDate", DateUtils.formatDate(DateUtils.parseDate(beginDate, "yyyyMMdd"), "yyyy-MM-dd"));
                            record.put("endDate", DateUtils.formatDate(DateUtils.getAfterDay(new Date(), -1), "yyyy-MM-dd"));
                            records.add(record);
                        }
                    }
                }
            } else {
                // 如果只有一条数据，则不需要查询历史记录
                Map<String, Object> record = Maps.newHashMap();

                Map<String, Object> map = dateAndRanking.get(0);
                int ranking = CommonUtils.emptyToInt(map.get("RANKING"));
                String beginDate = CommonUtils.emptyToString(map.get("MIN_DATE"));
                /*
                    如果唯一的一条历史级别与当前级别一致，则不需要显示历史记录；
                    如果不一致，则的说明投资金额保有量在当前日发生了变化，则变化之前的结束日就是当前日的前一天。
                 */
                if (ranking != currRank) {
                    record.put("ranking", ranking);
                    record.put("beginDate", DateUtils.formatDate(DateUtils.parseDate(beginDate, "yyyyMMdd"), "yyyy-MM-dd"));
                    record.put("endDate", DateUtils.formatDate(DateUtils.getAfterDay(new Date(), -1), "yyyy-MM-dd"));
                }

                records.add(record);
            }
        }

        result.put("currRank", currRank);
        result.put("initRank", initRank);
        result.put("showCurr", showCurr);
        result.put("showInit", showInit);
        result.put("gap", gepMap.get("gap"));
        result.put("records", records);
        return new ResultVo(true, "查询成功", result);
    }

    /**
     *      根据持有存续判断级别
     *      1星白金推荐人：>= 10万  1
     *      2星白金推荐人：>= 30万  2
     *      3星白金推荐人：>= 50万  3
     *      1星钻石推荐人：>= 100万 4
     *      2星钻石推荐人：>= 300万 5
     *      3星钻石推荐人：>= 500万 6
     *      黑钻推荐人：>= 1000万   7
     *
     * @param holdAmount 持有金额
     * @return
     */
    private int getRank(BigDecimal holdAmount) {
        int rank;
        if (holdAmount.compareTo(new BigDecimal(100000)) >= 0
                && holdAmount.compareTo(new BigDecimal(300000)) < 0) {
            rank = 1;
        } else if (holdAmount.compareTo(new BigDecimal(300000)) >= 0
                && holdAmount.compareTo(new BigDecimal(500000)) < 0) {
            rank = 2;
        } else if (holdAmount.compareTo(new BigDecimal(500000)) >= 0
                && holdAmount.compareTo(new BigDecimal(1000000)) < 0) {
            rank = 3;
        } else if (holdAmount.compareTo(new BigDecimal(1000000)) >= 0
                && holdAmount.compareTo(new BigDecimal(3000000)) < 0) {
            rank = 4;
        } else if (holdAmount.compareTo(new BigDecimal(3000000)) >= 0
                && holdAmount.compareTo(new BigDecimal(5000000)) < 0) {
            rank = 5;
        } else if (holdAmount.compareTo(new BigDecimal(5000000)) >= 0
                && holdAmount.compareTo(new BigDecimal(10000000)) < 0) {
            rank = 6;
        } else if (holdAmount.compareTo(new BigDecimal(10000000)) >= 0) {
            rank = 7;
        } else {
            // 低于10万没有等级
            rank = 0;
        }
        return rank;
    }

    /**
     * 计算持有金额距下个级别相差的金额
     *
     * @param holdAmount 持有金额
     * @return
     */
    private Map<String, Object> getGapToNextRanking(BigDecimal holdAmount) {
        Map<String, Object> getMap = new HashMap<>();
        int amount;// 距下个级别相差的金额
        int nextRank;// 下个级别
        if (holdAmount.compareTo(new BigDecimal(100000)) >= 0
                && holdAmount.compareTo(new BigDecimal(300000)) < 0) {
            amount = 300000;
            nextRank = 2;
        } else if (holdAmount.compareTo(new BigDecimal(300000)) >= 0
                && holdAmount.compareTo(new BigDecimal(500000)) < 0) {
            amount = 500000;
            nextRank = 3;
        } else if (holdAmount.compareTo(new BigDecimal(500000)) >= 0
                && holdAmount.compareTo(new BigDecimal(1000000)) < 0) {
            amount = 1000000;
            nextRank = 4;
        } else if (holdAmount.compareTo(new BigDecimal(1000000)) >= 0
                && holdAmount.compareTo(new BigDecimal(3000000)) < 0) {
            amount = 3000000;
            nextRank = 5;
        } else if (holdAmount.compareTo(new BigDecimal(3000000)) >= 0
                && holdAmount.compareTo(new BigDecimal(5000000)) < 0) {
            amount = 5000000;
            nextRank = 6;
        } else if (holdAmount.compareTo(new BigDecimal(5000000)) >= 0
                && holdAmount.compareTo(new BigDecimal(10000000)) < 0) {
            amount = 10000000;
            nextRank = 7;
        } else if (holdAmount.compareTo(new BigDecimal(10000000)) >= 0) {
            amount = 0;
            nextRank = -1;
        } else {
            amount = 100000;
            nextRank = 1;
        }
        BigDecimal gap;
        if (amount == 0) {
            gap = BigDecimal.ZERO;
        } else {
            gap = ArithUtil.sub(new BigDecimal(amount), holdAmount);
        }
        getMap.put("nextRank", nextRank);
        getMap.put("gap", gap);
        return getMap;
    }
}
