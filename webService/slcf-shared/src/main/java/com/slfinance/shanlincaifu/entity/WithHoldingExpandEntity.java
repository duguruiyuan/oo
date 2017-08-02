package com.slfinance.shanlincaifu.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * 代扣通知实体类
 * Created by 张祥 on 2017/7/12.
 */
@Entity
@Table(name = "BAO_T_With_Holding_Expand")
public class WithHoldingExpandEntity  extends  BaseEntity{

    private static final long serialVersionUID = -7061355740321831168L;

    /**交易流水号*/
    private String batchCode;

    /**还款编号（借款编号）***/
    private String repaymentNo;

    /***还款期数**/
    private Integer repaymentTerm;

    /**还款日期****/
    private String repaymentDate;

    /**交易金额**/
    private String tradeAmout;

    /**资产端名称**/
    private String thirdPartyType;

    /***资产端借口权限名称**/
    private String interfaceType;

    /**资产端编码**/
    private String merchantCode;

    /**请求号**/
    private String requestNo;

    /**处理状态**/
    private String execStatus;

    /**通知次数***/
    private int alreadyNotifyTimes;

    /***扣款类型**/
    private String tradeType;

    /**扣款状态**/
    private String tradeStatus;

    /**扣款账户Id**/
    private String accountId;

    /**是否逾期**/
    private String isTimeLimit;

    @Column(name = "REPAYMENT_NO", length = 150)
    public String getRepaymentNo() {
        return repaymentNo;
    }

    public void setRepaymentNo(String repaymentNo) {
        this.repaymentNo = repaymentNo;
    }

    @Column(name = "REPAYMENT_TERM")
    public Integer getRepaymentTerm() {
        return repaymentTerm;
    }

    public void setRepaymentTerm(Integer repaymentTerm) {
        this.repaymentTerm = repaymentTerm;
    }

    @Column(name = "REPAYMENT_DATE", length = 50)
    public String getRepaymentDate() {
        return repaymentDate;
    }

    public void setRepaymentDate(String repaymentDate) {
        this.repaymentDate = repaymentDate;
    }

    @Column(name = "TRADE_AMOUT")
    public String getTradeAmout() {
        return tradeAmout;
    }

    public void setTradeAmout(String tradeAmout) {
        this.tradeAmout = tradeAmout;
    }

    @Column(name = "THIRD_PARTY_TYPE", length = 150)
    public String getThirdPartyType() {
        return thirdPartyType;
    }

    public void setThirdPartyType(String thirdPartyType) {
        this.thirdPartyType = thirdPartyType;
    }

    @Column(name = "INTERFACE_TYPE", length = 150)
    public String getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Column(name = "MERCHANT_CODE", length = 150)
    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    @Column(name = "REQUEST_NO", length = 150)
    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    @Column(name = "EXEC_STATUS", length = 150)
    public String getExecStatus() {
        return execStatus;
    }

    public void setExecStatus(String execStatus) {
        this.execStatus = execStatus;
    }

    @Column(name = "ALREADY_NOTIFY_TIMES")
    public int getAlreadyNotifyTimes() {
        return alreadyNotifyTimes;
    }

    public void setAlreadyNotifyTimes(int alreadyNotifyTimes) {
        this.alreadyNotifyTimes = alreadyNotifyTimes;
    }

    @Column(name = "BATCH_CODE", length = 150)
    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    @Column(name = "TRADE_TYPE", length = 150)
    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    @Column(name = "TRADE_STATUS", length = 150)
    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    @Column(name = "ACCOUNT_ID", length = 150)
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Column(name = "IS_TIME_LIMIT", length = 150)
    public String getIsTimeLimit() {
        return isTimeLimit;
    }

    public void setIsTimeLimit(String isTimeLimit) {
        this.isTimeLimit = isTimeLimit;
    }
}
