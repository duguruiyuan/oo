package com.slfinance.shanlincaifu.service.impl;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.*;
import com.slfinance.shanlincaifu.repository.*;
import com.slfinance.shanlincaifu.repository.custom.WithHoldingRepositoryCustom;
import com.slfinance.shanlincaifu.service.*;
import com.slfinance.shanlincaifu.utils.*;
import com.slfinance.shanlincaifu.validate.RuleUtils;
import com.slfinance.shanlincaifu.vo.withHoldingRequet.*;
import com.slfinance.spring.mail.MailType;
import com.slfinance.thirdpp.util.ShareUtil;
import com.slfinance.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestOperations;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * 代扣业务实现类
 * @author 张祥
 * @create 2017-07-13 10:55
 **/
@Slf4j
@Service
public class WithHoldingServiceImpl implements WithHoldingService{

    @Autowired
    private WithHoldingRepositoryCustom withHoldingRepositoryCustom;

    @Autowired
    private InnerWithHoldingBusiness innerWithHoldingBusiness;

    @Autowired
    private FlowNumberService flowNumberService;

    @Autowired
    private LoanInfoRepository loanInfoRepository;

    @Autowired
    private WithholdAccountRepository withholdAccountRepository;

    @Autowired
    private CustInfoRepository custInfoRepository;

    @Autowired
    private CustAccountService custAccountService;

    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @Autowired
    private RepaymentPlanCopeService repaymentPlanCopeService;

    @Autowired
    private WithHoldingFlowService withHoldingFlowService;

    @Autowired
    private WithHoldingExpandRepostory withHoldingExpandRepostory;

    @Autowired
    @Qualifier("grantThreadPoolTaskExecutor")
    Executor executor;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    LogInfoService logInfoService;

    @Autowired
    RepaymentChangeService repaymentChangeService;

    @Override
    @Transactional(readOnly = false,rollbackFor = SLException.class)
    public void withHoldingSuccess(Map<String, Object> paramMap) throws SLException{

        //代扣成功，修改账户，添加流水
        //获取公司名称
        LoanInfoEntity loanInfoEntity = loanInfoRepository.findByLoanCode(paramMap.get("repaymentNo").toString());
        WithholdAccountEntity withholdAccountEntity = withholdAccountRepository.findByCompanyName(loanInfoEntity.getCompanyName());

        CustInfoEntity custInfoEntity = custInfoRepository.findByLoginName(withholdAccountEntity.getLoginName());
        AccountInfoEntity companyAccount = accountInfoRepository.findByCustId(custInfoEntity.getId());

        String isLimit = paramMap.get("isLimit").toString();

        //逾期之后请求代扣
        if (isLimit.equals(WithHoldingConstant.REPAYMENT_IS_TIME_LIMIT_YES)){
            RepaymentPlanCopeEntity copePlanEntity = repaymentPlanCopeService.doFindPlanByLoanNoAndTerm(paramMap.get("repaymentNo").toString(),paramMap.get("repaymentTerm").toString());
            AccountInfoEntity rieskAccount = accountInfoRepository.findByCustId(copePlanEntity.getRieskamoutAccountId());
            custAccountService.updateAccount(rieskAccount,null,null,null,
                    "5",SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_DISOUNT_LOAN,paramMap.get("requestNo").toString(),
                    new BigDecimal(paramMap.get("moneyOrder").toString()),
                    SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_DISOUNT_LOAN,Constant.TABLE_BAO_T_LOAN_INFO,
                    loanInfoEntity.getId(),String.format("代扣用户银行卡进行还款(换到垫付账户)"),Constant.SYSTEM_USER_BACK);

        }else{
            //正常扣款请求代扣
            /**修改账户信息***/
            custAccountService.updateAccount(companyAccount,null,null,null,
                    "5",SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_DISOUNT_LOAN,paramMap.get("requestNo").toString(),
                    new BigDecimal(paramMap.get("moneyOrder").toString()),
                    SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_DISOUNT_LOAN,Constant.TABLE_BAO_T_LOAN_INFO,
                    loanInfoEntity.getId(),String.format("代扣用户银行卡进行还款"),Constant.SYSTEM_USER_BACK);
        }

        //修改还款计划副本状态
        repaymentPlanCopeService.doUpdatePlanCopeStatus(paramMap.get("repaymentNo").toString(),paramMap.get("repaymentTerm").toString(),
                Constant.REPAYMENT_STATUS_CLEAN,isLimit,null,WithHoldingConstant.Constant_NO);
        //修改流水信息
        withHoldingFlowService.doUpdateWithHoldingFlow(paramMap.get("batchCode").toString(),paramMap.get("requestNo").toString(),
               Constant.TRADE_STATUS_03,paramMap.get("status").toString(),WithHoldingConstant.Constant_NO,WithHoldingConstant.WITHHOLD_TRADE_TYPE_USER);

        //记录异步通知信息
        WithHoldingExpandEntity expandEntity = new WithHoldingExpandEntity();
        expandEntity.setAlreadyNotifyTimes(0);
        expandEntity.setBatchCode(paramMap.get("batchCode").toString());
        expandEntity.setRequestNo(paramMap.get("requestNo").toString());
        expandEntity.setRepaymentNo(paramMap.get("repaymentNo").toString());
        expandEntity.setRepaymentTerm(Integer.parseInt(paramMap.get("repaymentTerm").toString()));
        expandEntity.setRepaymentDate(paramMap.get("repaymentDate").toString());
        expandEntity.setTradeAmout(paramMap.get("moneyOrder").toString());
        expandEntity.setIsTimeLimit(isLimit);
        expandEntity.setThirdPartyType(loanInfoEntity.getCompanyName());
        expandEntity.setInterfaceType(WithHoldingConstant.REPAYMENT_INTERFACE_INFO);
        expandEntity.setTradeStatus(paramMap.get("status").toString());
        expandEntity.setTradeType(WithHoldingConstant.WITHHOLD_TRADE_TYPE_USER);

        //新增通知扩展表
        withHoldingExpandRepostory.save(expandEntity);
    }

    @Override
    @Transactional(readOnly = false,rollbackFor = SLException.class)
    public void withHoldingFailed(Map<String, Object> paramMap) throws SLException{
        log.info("进行备付金扣款逻辑..................");
        log.info("进行备付金扣款逻辑....余额不足..............");
        //获取公司名称
        LoanInfoEntity loanInfoEntity = loanInfoRepository.findByLoanCode(paramMap.get("repaymentNo").toString());
        WithholdAccountEntity withholdAccountEntity = withholdAccountRepository.findByCompanyName(loanInfoEntity.getCompanyName());
        //资产端公司账户
        CustInfoEntity custInfoEntity = custInfoRepository.findByLoginName(withholdAccountEntity.getLoginName());
        //获取资产端备付金账户
        AccountInfoEntity prepareAccount = accountInfoRepository.findOne(withholdAccountEntity.getAccountPrepareId());

        AccountInfoEntity companyAccount = accountInfoRepository.findByCustId(custInfoEntity.getId());
        if (prepareAccount == null){
            log.error("代扣失败，失败原因：备付金账户不存在");
            withHoldingFlowService.doUpdateWithHoldingFlow(paramMap.get("batchCode").toString(),paramMap.get("requestNo").toString(),
                    Constant.TRADE_STATUS_04,paramMap.get("status").toString(), WithHoldingConstant.Constant_NO,"备付金账户不存在");
        }
        BigDecimal tradeAmount =  new BigDecimal(paramMap.get("moneyOrder").toString());
        if (prepareAccount.getAccountAvailableAmount().compareTo(tradeAmount)<0){//资产端口备付金不足 进行财富备付金扣款
            //查询财富备付金账户
            AccountInfoEntity cfAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_RISK);
            /**修改账户，并记录账户流水记录***/
            custAccountService.updateAccount(cfAccount,null,companyAccount,null,
                    "1",SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_DISOUNT_LOAN,paramMap.get("requestNo").toString(),
                    tradeAmount,SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_DISOUNT_LOAN,Constant.TABLE_BAO_T_LOAN_INFO,
                    loanInfoEntity.getId(),String.format("财富备付金还款"),Constant.SYSTEM_USER_BACK);

            //修改还款计划副本状态
            repaymentPlanCopeService.doUpdatePlanCopeStatus(paramMap.get("repaymentNo").toString(),paramMap.get("repaymentTerm").toString(),
                    Constant.REPAYMENT_STATUS_WAIT,WithHoldingConstant.REPAYMENT_IS_TIME_LIMIT_YES,cfAccount.getId(),null);
            //更新交易流水
            withHoldingFlowService.doUpdateWithHoldingFlow(paramMap.get("batchCode").toString(),paramMap.get("requestNo").toString(),
                    Constant.TRADE_STATUS_03,paramMap.get("status").toString(),WithHoldingConstant.Constant_NO,
                    WithHoldingConstant.WITHHOLD_TRADE_TYPE_CFBF);
            //新增通知扩展表
            WithHoldingExpandEntity expandEntity = new WithHoldingExpandEntity();
            expandEntity.setAlreadyNotifyTimes(0);
            expandEntity.setBatchCode(paramMap.get("batchCode").toString());
            expandEntity.setRequestNo(paramMap.get("requestNo").toString());
            expandEntity.setRepaymentNo(paramMap.get("repaymentNo").toString());
            expandEntity.setRepaymentTerm(Integer.parseInt(paramMap.get("repaymentTerm").toString()));
            expandEntity.setRepaymentDate(paramMap.get("repaymentDate").toString());
            expandEntity.setTradeAmout(tradeAmount.toString());
            expandEntity.setIsTimeLimit(WithHoldingConstant.REPAYMENT_IS_TIME_LIMIT_YES);
            expandEntity.setThirdPartyType(loanInfoEntity.getCompanyName());
            expandEntity.setInterfaceType(WithHoldingConstant.REPAYMENT_INTERFACE_INFO);
            expandEntity.setTradeStatus("交易成功");
            expandEntity.setTradeType(WithHoldingConstant.WITHHOLD_TRADE_TYPE_CFBF);
            expandEntity.setAccountId(cfAccount.getId());
            withHoldingExpandRepostory.save(expandEntity);
        }else{//进行备付金扣款逻辑

            if (custInfoEntity!=null){

                /**修改账户，并记录账户流水记录***/
                custAccountService.updateAccount(prepareAccount,null,companyAccount,null,
                        "1",SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_DISOUNT_LOAN,paramMap.get("requestNo").toString(),
                        tradeAmount,SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_DISOUNT_LOAN,Constant.TABLE_BAO_T_LOAN_INFO,
                        loanInfoEntity.getId(),String.format("资产端备付金还款"),Constant.SYSTEM_USER_BACK);
                //修改还款计划副本状态
                repaymentPlanCopeService.doUpdatePlanCopeStatus(paramMap.get("repaymentNo").toString(),paramMap.get("repaymentTerm").toString(),
                        Constant.REPAYMENT_STATUS_WAIT,WithHoldingConstant.REPAYMENT_IS_TIME_LIMIT_YES,prepareAccount.getId(),null);
                //更新交易流水
                withHoldingFlowService.doUpdateWithHoldingFlow(paramMap.get("batchCode").toString(),paramMap.get("requestNo").toString(),
                        Constant.TRADE_STATUS_03,paramMap.get("status").toString(),WithHoldingConstant.Constant_NO,
                        WithHoldingConstant.WITHHOLD_TRADE_TYPE_ZCBF);

                //新增通知扩展表
                WithHoldingExpandEntity expandEntity = new WithHoldingExpandEntity();
                expandEntity.setAlreadyNotifyTimes(0);
                expandEntity.setBatchCode(paramMap.get("batchCode").toString());
                expandEntity.setRequestNo(paramMap.get("requestNo").toString());
                expandEntity.setRepaymentNo(paramMap.get("repaymentNo").toString());
                expandEntity.setRepaymentTerm(Integer.parseInt(paramMap.get("repaymentTerm").toString()));
                expandEntity.setRepaymentDate(paramMap.get("repaymentDate").toString());
                expandEntity.setTradeAmout(tradeAmount.toString());
                expandEntity.setIsTimeLimit(WithHoldingConstant.REPAYMENT_IS_TIME_LIMIT_YES);
                expandEntity.setThirdPartyType(loanInfoEntity.getCompanyName());
                expandEntity.setInterfaceType(WithHoldingConstant.REPAYMENT_INTERFACE_INFO);
                expandEntity.setTradeStatus("交易成功");
                expandEntity.setTradeType(WithHoldingConstant.WITHHOLD_TRADE_TYPE_ZCBF);
                expandEntity.setAccountId(prepareAccount.getId());
                withHoldingExpandRepostory.save(expandEntity);
            }else{
                log.error("代扣失败，失败原因：公司账户不存在");
                withHoldingFlowService.doUpdateWithHoldingFlow(paramMap.get("batchCode").toString(),paramMap.get("requsetNo").toString(),
                        Constant.TRADE_STATUS_04,paramMap.get("status").toString(),WithHoldingConstant.Constant_NO,"公司账户不存在");
            }
        }
    }
    
    @Override
    @Transactional(readOnly = false,rollbackFor = SLException.class)
    public void timeLimitWithHoldingFailed(Map<String, Object> paramMap) throws SLException{
    	withHoldingFlowService.doUpdateWithHoldingFlow(paramMap.get("batchCode").toString(),paramMap.get("requestNo").toString(),
                Constant.TRADE_STATUS_04,paramMap.get("status").toString(),WithHoldingConstant.Constant_NO,"逾期扣款失败");
         
    	LoanInfoEntity loanInfoEntity = loanInfoRepository.findByLoanCode(paramMap.get("repaymentNo").toString());
		//新增通知扩展表
		WithHoldingExpandEntity expandEntity = new WithHoldingExpandEntity();
		expandEntity.setAlreadyNotifyTimes(0);
		expandEntity.setBatchCode(paramMap.get("batchCode").toString());
		expandEntity.setRequestNo(paramMap.get("requestNo").toString());
		expandEntity.setRepaymentNo(paramMap.get("repaymentNo").toString());
		expandEntity.setRepaymentTerm(Integer.parseInt(paramMap.get("repaymentTerm").toString()));
		expandEntity.setRepaymentDate(paramMap.get("repaymentDate").toString());
		expandEntity.setTradeAmout(paramMap.get("moneyOrder").toString());
		expandEntity.setIsTimeLimit(WithHoldingConstant.REPAYMENT_IS_TIME_LIMIT_YES);
		expandEntity.setThirdPartyType(loanInfoEntity.getCompanyName());
		expandEntity.setInterfaceType(WithHoldingConstant.REPAYMENT_INTERFACE_INFO);
		expandEntity.setTradeStatus("扣款失败");
		expandEntity.setTradeType(WithHoldingConstant.WITHHOLD_TRADE_TYPE_USER);
		withHoldingExpandRepostory.save(expandEntity);
    }

    @Override
    public ResultVo timeLimitWithHold(Map<String, Object> paramMap,List<Map<String,Object>> repaymentList) throws SLException {

        String loanNo = paramMap.get("loanNo").toString();
        //校验借款信息
        LoanInfoEntity loanInfo = loanInfoRepository.findByLoanCode(loanNo);

        if (!RuleUtils.required(loanInfo)){
            return new ResultVo(false, "借款不存在");
        }
        if (Constant.LOAN_STATUS_08.equals(loanInfo.getLoanStatus())){
            return new ResultVo(false,"项目非正常状态，不能做逾期还款");
        }
        //校验还款计划数据是否允许变更
        List<RepaymentPlanCopeEntity> copePlanList = repaymentPlanCopeService.queryByLoanCode(loanNo);
        if (copePlanList == null || copePlanList.size()<=0){
            return new ResultVo(false,"没有找到相应的还款计划");
        }
        for (Map<String,Object> map : repaymentList){
            Boolean isChange = false;
            for (RepaymentPlanCopeEntity cope : copePlanList){
                if (Integer.parseInt(map.get("currentTerm").toString()) == cope.getCurrentTerm()){
                    if ((!cope.getIsLimit().equals(WithHoldingConstant.REPAYMENT_IS_TIME_LIMIT_YES) &&
                            !cope.getRepaymentStatus().equals(Constant.REPAYMENT_STATUS_WAIT))
                            ||cope.getChangeEnable().equals(WithHoldingConstant.Constant_NO)){
                        log.warn(String.format("借款[%s]期数[%s]不能执行变更，不能进行代扣", loanNo, cope.getCurrentTerm().toString()));
                        return new ResultVo(false,String.format("借款[%s]期数[%s]非逾期未还款状态，不能进行代扣",loanNo, cope.getCurrentTerm().toString()));
                    }
                    isChange = true;
                }
            }
            if (!isChange){
                return new ResultVo(false, String.format("还款数据异常%s，未找到期数%s，还款时间为%s的还款数据。",
                        loanNo, map.get("currentTerm").toString(), map.get("expectRepaymentDate").toString()));
            }
        }
        //请求变更
        ResultVo resultVo =repaymentChangeService.repaymentChange(paramMap,repaymentList);
        if (ResultVo.isSuccess(resultVo)){
            //2.请求逾期代扣
            if (ResultVo.isSuccess(resultVo)){
                for (Map<String,Object> map:repaymentList){
                    limitWithHolding(loanNo,map.get("currentTerm").toString());
                }
            }
        }

        return resultVo;
    }


    @Override
    public ResultVo autoWithHoldingRepayment(String expectRepaymentDate) throws SLException {

        final List<Map<String,Object>> repaymentList = withHoldingRepositoryCustom.doFindWatingRepayPlanCope(expectRepaymentDate);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if(repaymentList!=null && repaymentList.size()>0){
                    Map<String, Object> resultMap = Maps.newHashMap();
                    for (Map<String,Object> map : repaymentList){
                        //进行扣款逻辑
                        try {
                            map.put("batchCode",flowNumberService.generateTradeBatchNumber());
                            map.put("requestNo",flowNumberService.generateAllotNumber());
                            map.put("isLimit",WithHoldingConstant.REPAYMENT_IS_TIME_LIMIT_NO);
                            ResultVo result = innerWithHoldingBusiness.trustWithHold(map);
                            if(!ResultVo.isSuccess(result)) {
                                resultMap.put(map.get("repaymentNo").toString(), result.getValue("message"));
                            }
                        } catch (SLException e) {
                            resultMap.put(map.get("repaymentNo").toString(), e.getMessage());
                            e.printStackTrace();
                        }
                        //发送通知邮件
                        String email = "";
                        try {
                            UserEntity user = userRepository.findOne("1");
                            email = user.getEmail();
                            Map<String, Object> smsInfo = Maps.newHashMap();
                            smsInfo.put("to", email);// 收件人邮箱地址
                            smsInfo.put("type", MailType.TEXT);
                            smsInfo.put("title", "优选项目代扣[正常]反馈");
                            smsInfo.put("content", getContent(repaymentList, resultMap));
                            smsInfo.put("fromNickName",Constant.FORM_PLAT_NAME);
                            smsInfo.put("fromAddress",Constant.PLAT_EMAIL_ADDRESS);
                            emailService.sendEmail(smsInfo);
                        } catch (SLException e) {
                            log.error(String.format("优选项目代扣[正常]反馈邮件发送异常！[loanNo=%s，userId=%s，userEmail=%s]", map.get("repaymentNo").toString(), "1", email));
                            LogInfoEntity log = new LogInfoEntity();
                            log.setBasicModelProperty("1", true);
                            log.setRelateType("COM_T_USER");
                            log.setRelatePrimary("1");
                            log.setLogType("优选项目代扣[正常]反馈");
                            log.setOperDesc(String.format("优选项目代扣[正常]反馈邮件发送异常！[loanNo=%s，userId=%s，userEmail=%s]", map.get("repaymentNo").toString(), "1", email));
                            log.setOperPerson("1");
                            log.setMemo("");
                            logInfoService.saveLogInfo(log);
                        }
                    }

                }
            }
        });

        return new ResultVo(true,"系统正在批量执行代扣， 稍后登陆邮箱查看结果");
    }

    @Override
    public ResultVo limitWithHolding(String loanNo, String currentTerm) {

        final List<Map<String,Object>> repaymentList = withHoldingRepositoryCustom.doFindWatingRepayPlanCopeByLoanInfo(loanNo,currentTerm);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                if(repaymentList!=null && repaymentList.size()>0){
                    Map<String, Object> resultMap = Maps.newHashMap();
                    for (Map<String,Object> map : repaymentList){

                        if (map.get("repaymentStatus").equals(Constant.REPAYMENT_STATUS_WAIT)){
                            //进行扣款逻辑
                            try {
                                map.put("batchCode",flowNumberService.generateTradeBatchNumber());
                                map.put("requestNo",flowNumberService.generateAllotNumber());
                                map.put("isLimit",WithHoldingConstant.REPAYMENT_IS_TIME_LIMIT_YES);
                                ResultVo result = innerWithHoldingBusiness.trustWithHold(map);
                                if(!ResultVo.isSuccess(result)) {
                                    resultMap.put(map.get("repaymentNo").toString(), result.getValue("message"));
                                }
                            } catch (SLException e) {
                                resultMap.put(map.get("repaymentNo").toString(), e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        //发送通知邮件
                        String email = "";
                        try {
                            UserEntity user = userRepository.findOne("1");
                            email = user.getEmail();
                            Map<String, Object> smsInfo = Maps.newHashMap();
                            smsInfo.put("to", email);// 收件人邮箱地址
                            smsInfo.put("type", MailType.TEXT);
                            smsInfo.put("title", "优选项目代扣[逾期]反馈");
                            smsInfo.put("content", getContent(repaymentList, resultMap));
                            smsInfo.put("fromNickName",Constant.FORM_PLAT_NAME);
                            smsInfo.put("fromAddress",Constant.PLAT_EMAIL_ADDRESS);
                            emailService.sendEmail(smsInfo);
                        } catch (SLException e) {
                            log.error(String.format("优选项目代扣[逾期]反馈邮件发送异常！[loanNo=%s，userId=%s，userEmail=%s]", map.get("repaymentNo").toString(), "1", email));
                            LogInfoEntity log = new LogInfoEntity();
                            log.setBasicModelProperty("1", true);
                            log.setRelateType("COM_T_USER");
                            log.setRelatePrimary("1");
                            log.setLogType("优选项目[逾期]代扣反馈");
                            log.setOperDesc(String.format("优选项目代扣[逾期]反馈邮件发送异常！[loanNo=%s，userId=%s，userEmail=%s]", map.get("repaymentNo").toString(), "1", email));
                            log.setOperPerson("1");
                            log.setMemo("");
                            logInfoService.saveLogInfo(log);
                        }
                    }

                }


            }
        });

        return new ResultVo(true,"代扣请求处理成功");

    }

    private String getContent(List<Map<String, Object>> list, Map<String, Object> ids) {
        StringBuffer sb = new StringBuffer();
        sb.append("你好,代扣结果如下：");
        sb.append("\n");
        sb.append("借款编号|还款期数|还款时间|借款金额（元）|放款结果|原因");
        for(Map<String, Object> info : list) {
            sb.append("\n");
            String message = "success".equals((String)ids.get((String) info.get("repaymentNo"))) ? "成功|无" : "失败|" + (String)ids.get((String) info.get("repaymentNo"));
            sb.append(info.get("repaymentNo") + "|"+ info.get("repaymentTerm")+"|"+ info.get("scheduleRepaymentDate") +"|" + info.get("moneyOrder") + "|" + message);
        }

        return sb.toString();
    }


    @Service
    public static class InnerWithHoldingBusiness{

        @Autowired
        private WithHoldingFlowRepository withHoldingFlowRepository;

        @Autowired
        private LoanCustInfoRepository loanCustInfoRepository;

        @Autowired
        @Qualifier("thirdPartyPayRestClientService")
        private RestOperations slRestClient;

        @Value("${WITH_HOLDING_NOTIFY_URL}")
        private String withHoldingNotifyUrl;

        @Value("${WITH_HOLDING_REQUEST_URL}")
        private String withholdingRequestUrl;

        /***
         * 请求第三方代扣
         * @param params
         * @return
         * @throws SLException
         */
        public ResultVo trustWithHold(Map<String, Object> params) throws SLException {

            try {
                /**构建请求参数**/
                String batchCode = params.get("batchCode").toString();
                String requestNo = params.get("requestNo").toString();
                String moneyOrder = params.get("moneyOrder").toString();

                ////时间转换有问题
                //Date repaymentDate = new Date(Long.parseLong(params.get("scheduleRepaymentDate").toString()));
                String custId = params.get("custId").toString();
                String repaymentNo = params.get("repaymentNo").toString();

                BaseRequestVo<WithHoldingReqVo> requestVo= new BaseRequestVo<WithHoldingReqVo>();
                requestVo.setBatchCode(batchCode);
                requestVo.setBuzName(Constant.DEBT_SOURCE_CODE_SLCF);
                requestVo.setPlatform(WithHoldingConstant.PLATFORM);
                requestVo.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
                requestVo.setUserDevice(WithHoldingConstant.USER_DEVICE_PC);
                requestVo.setServiceName(WithHoldingConstant.WITH_HOLDING_SERVICE_NAME);

                WithHoldingReqVo withHoldingReqVo = new WithHoldingReqVo();
                withHoldingReqVo.setTradeAmount(moneyOrder);
                //withHoldingReqVo.setTradeAmount("0.01");
                withHoldingReqVo.setNoAgree(params.get("noAgree").toString());
                LoanCustInfoEntity loanCustInfo =  loanCustInfoRepository.findOne(custId);
                withHoldingReqVo.setPlatformUserNo(loanCustInfo.getCredentialsCode());
                //withHoldingReqVo.setPlatformUserNo("00001");
                withHoldingReqVo.setRepaymentNo(repaymentNo);
                withHoldingReqVo.setRepaymentTerm(params.get("repaymentTerm").toString());
                String time = params.get("scheduleRepaymentDate").toString();
                withHoldingReqVo.setRepaymentDate(DateUtils.formatDate(new SimpleDateFormat("yyyyMMdd")
                        .parse(params.get("scheduleRepaymentDate").toString()),"yyyy-MM-dd"));
                withHoldingReqVo.setIsLimit(params.get("isLimit").toString());
                withHoldingReqVo.setNotifyUrl(withHoldingNotifyUrl);
                withHoldingReqVo.setRequestNo(requestNo);
                requestVo.setReqData(withHoldingReqVo);

                HttpHeaders headers = new HttpHeaders();
                MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
                headers.setContentType(type);
                headers.add("Accept", MediaType.APPLICATION_JSON.toString());
                Map<String,String> requestMap = ShareUtil.jsonToMap(Json.ObjectMapper.writeValue(requestVo));
                HttpEntity<Map<String,String>> formEntity = new HttpEntity<Map<String,String>>(requestMap,headers);
                Map<String,String> result = slRestClient.postForObject(withholdingRequestUrl, formEntity,Map.class);
                log.info("===========接收到的报文：{}",result);
                //BaseResponseVo<WithHoldingRespVo> responseVo = (BaseResponseVo<WithHoldingRespVo>)ShareUtil.jsonToObject(Json.ObjectMapper.writeValue(result),BaseResponseVo.class);
                BaseResponseVo responseVo = (BaseResponseVo)ShareUtil.jsonToObject(Json.ObjectMapper.writeValue(result),BaseResponseVo.class);
                WithHoldingRespVo withHoldingRespVo =  (WithHoldingRespVo)ShareUtil.jsonToObject(Json.ObjectMapper.writeValue(responseVo.getRespData()),WithHoldingRespVo.class);
                if (withHoldingRespVo.getCode().equals(WithHoldingConstant.RESPONSE_SUCCESS_CODE)){
                    WithHoldingFlowEntity holdingFlow = new WithHoldingFlowEntity();
                    holdingFlow.setResponseCode(withHoldingRespVo.getCode());
                    holdingFlow.setBatchCode(batchCode);
                    holdingFlow.setRequestNo(requestNo);
                    holdingFlow.setCustId(custId);
                    holdingFlow.setRepaymentNo(repaymentNo);
                    holdingFlow.setTradeAmount(new BigDecimal(moneyOrder));
                    holdingFlow.setRepaymentTerm(Integer.parseInt(params.get("repaymentTerm").toString()));
                    holdingFlow.setTradeDate(new Date());
                    holdingFlow.setTradeStatus(Constant.TRADE_STATUS_02);
                    holdingFlow.setTradeType(Constant.REPAYMENT_TYPE_01);
                    holdingFlow.setIsNeedQuery(WithHoldingConstant.Constant_YES);
                    withHoldingFlowRepository.save(holdingFlow);
                    return new ResultVo(true);
                }
                return new ResultVo(false,"代扣失败");
            } catch (Exception e) {
                log.error("代扣请求第三方异常:" + e.toString());
                e.printStackTrace();
                return new ResultVo(false,"代扣请求第三方异常");
            }


        }


    }

}
