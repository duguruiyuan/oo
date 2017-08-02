package com.slfinance.shanlincaifu.entity;/**
 * <描述：<b>TODO</b>
 *
 * @author:张祥
 * @date 2017/7/13
 */


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 代扣流水表
 * @author 张祥
 * @create 2017-07-13 11:15
 **/
@Entity
@Table(name = "BAO_T_WITH_HOLDING_FLOW")
public class WithHoldingFlowEntity extends BaseEntity{

    private static final long serialVersionUID = -6208060760672826959L;

    /**请求code**/
    private String batchCode;

    /**请求编号**/
    private String requestNo;

    /**还款计划期数***/
    private Integer repaymentTerm;

    /***还款计划编号***/
    private String repaymentNo;

    /***交易状态***/
    private String tradeStatus;

    /***客户id**/
    private String custId;

    /**交易金额***/
    private BigDecimal tradeAmount;

    /**交易时间*/
    private Date tradeDate;

    /**交易类型（预期扣款，正常扣款）***/
    private String tradeType;

    /**返回码*****/
    private String responseCode;

    /***返回信息***/
    private String  responseInfo;

    /**是否需要定时查询***/
    private String  isNeedQuery;

    @Column(name = "BATCH_CODE",length = 150)
    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    @Column(name = "REQUEST_NO",length = 150)
    public String getRequestNo() {return requestNo;}

    public void setRequestNo(String requestNo) {this.requestNo = requestNo;}

    @Column(name = "REPAYMENT_TERM")
    public Integer getRepaymentTerm() {
        return repaymentTerm;
    }

    public void setRepaymentTerm(Integer repaymentTerm) {
        this.repaymentTerm = repaymentTerm;
    }

    @Column(name = "REPAYMENT_NO",length = 150)
    public String getRepaymentNo() {
        return repaymentNo;
    }

    public void setRepaymentNo(String repaymentNo) {
        this.repaymentNo = repaymentNo;
    }

    @Column(name = "TRADE_STATUS",length = 50)
    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    @Column(name = "CUST_ID", length = 50)
    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    @Column(name = "TRADE_AMOUNT", precision = 22, scale = 8)
    public BigDecimal getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(BigDecimal tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    @Column(name = "TRADE_DATE", length = 7)
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    @Column(name = "TRADE_TYPE", length = 50)
    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    @Column(name = "RESPONSE_CODE", length = 50)
    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @Column(name = "RESPONSE_INFO", length = 225)
    public String getResponseInfo() {
        return responseInfo;
    }

    public void setResponseInfo(String responseInfo) {
        this.responseInfo = responseInfo;
    }

    @Column(name = "IS_NEED_QUERY", length = 50)
    public String getIsNeedQuery() {
        return isNeedQuery;
    }

    public void setIsNeedQuery(String isNeedQuery) {
        this.isNeedQuery = isNeedQuery;
    }
}
