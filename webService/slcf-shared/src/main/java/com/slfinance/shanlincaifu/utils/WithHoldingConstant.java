package com.slfinance.shanlincaifu.utils;

/**
 *
 * 代扣业务常量类封装
 * Created by 张祥 on 2017/7/13.
 */
public class WithHoldingConstant {

    //请求代扣平台常量
    public static final String PLATFORM = "lianpay";

    //用户设备
    public static final  String USER_DEVICE_PC = "PC";

    //代扣Service name
    public static final String WITH_HOLDING_SERVICE_NAME = "BANK_CARD_REPAYMENT_DEBIT";

    //还款计划变更 ServiceName
    public static final String REPAYMENT_CHANGE_SERVICE_NAME = "REPAYMENT_PLAN_CHANGE";

    //代扣结果查询ServiceName
    public static final String WITH_HOLDING_QUERY_SERVICE_NAME = "QUERY_PAYMENT_RESULT";

    //请求成功常量
    public static final String RESPONSE_SUCCESS_CODE = "0000";

    //余额不足
    public static final String LACK_OF_BALANCE_CODE = "1005";

    //扣款账户 = 用户
    public static final String WITHHOLD_TRADE_TYPE_USER = "用户银行卡账户";
    //扣款账户 = 资产端
    public static final String WITHHOLD_TRADE_TYPE_ZCBF = "资产端备付金账户";
    //扣款账户 = 财富
    public static final String WITHHOLD_TRADE_TYPE_CFBF = "财富备付金账户";

    //是否逾期
    public static final String REPAYMENT_IS_TIME_LIMIT_YES = "是";

    //是否逾期
    public static final String REPAYMENT_IS_TIME_LIMIT_NO = "否";

    //常量是
    public static final String Constant_YES = "是";

    //常量否
    public static final String Constant_NO = "否";

    //扣款通知接口
    public static final String REPAYMENT_INTERFACE_INFO = "代扣扣款通知";




}
