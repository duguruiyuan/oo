package com.slfinance.shanlincaifu.utils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 系统常量类
 * 
 * @author HuangXiaodong
 */
public class Constant {

	// ------------------------------------------------关联表标识------------------------------------------------------
	/** 产品类型表 */
	public static final String TABLE_BAO_T_PRODUCT_TYPE_INFO = "BAO_T_PRODUCT_TYPE_INFO";
	/** 分配信息表 */
	public static final String TABLE_BAO_T_ALLOT_INFO = "BAO_T_ALLOT_INFO";
	/** 账户表 */
	public static final String TABLE_BAO_T_ACCOUNT_INFO = "BAO_T_ACCOUNT_INFO";
	/** 审核信息表 */
	public static final String TABLE_BAO_T_AUDIT_INFO = "BAO_T_AUDIT_INFO";
	/** 日志信息表 */
	public static final String TABLE_BAO_T_LOG_INFO = "BAO_T_LOG_INFO";
	/** 系统信息表 */
	public static final String TABLE_BAO_T_SYSTEM_MESSAGE_INFO = "BAO_T_SYSTEM_MESSAGE_INFO";
	/** 赎回信息表 */
	public static final String TABLE_BAO_T_ATONE_INFO = "BAO_T_ATONE_INFO";
	/** 还款记录表 */
	public static final String TABLE_BAO_T_REPAYMENT_RECORD_INFO = "BAO_T_REPAYMENT_RECORD_INFO";
	/** 投资信息表 */
	public static final String TABLE_BAO_T_INVEST_INFO = "BAO_T_INVEST_INFO";
	/** 用户信息表 */
	public static final String TABLE_BAO_T_CUST_INFO = "BAO_T_CUST_INFO";
	/** 用户联系人信息表 */
	public static final String TABLE_BAO_T_CONTACT_INFO = "BAO_T_CONTACT_INFO";
	/** 交易流水表 */
	public static final String TABLE_BAO_T_TRADE_FLOW_INFO = "BAO_T_TRADE_FLOW_INFO";
	/** 银行表 */
	public static final String TABLE_BAO_T_BANK_CARD_INFO = "BAO_T_BANK_CARD_INFO";
	/** BAO转账信息表 */
	public static final String TABLE_BAO_T_TRANS_ACCOUNT_INFO = "BAO_T_TRANS_ACCOUNT_INFO";
	/** 客户活动明细表 */
	public static final String TABLE_BAO_T_CUST_ACTIVITY_DETAIL="BAO_T_CUST_ACTIVITY_DETAIL";
	/** 银行卡解绑申请表 */
	public static final String TABLE_BAO_T_UNBIND_INFO = "BAO_T_UNBIND_INFO";
	/** 扩展表 */
	public static final String TABLE_BAO_T_EXPAND_INFO = "BAO_T_EXPAND_INFO";
	/** 客户申请表 */
	public static final String TABLE_BAO_T_CUST_APPLY_INFO = "BAO_T_CUST_APPLY_INFO";
	/** BANNER表 */
	public static final String TABLE_BAO_T_BANNER_INFO = "BAO_T_BANNER_INFO";
	/** 客户活动表 */
	public static final String TABLE_BAO_T_CUST_ACTIVITY_INFO = "BAO_T_CUST_ACTIVITY_INFO";
	/** 公告表 */
	public static final String TABLE_BAO_T_AFFICHE_INFO = "BAO_T_AFFICHE_INFO";
	/** 项目信息表*/
	public static final String TABLE_BAO_T_PROJECT_INFO = "BAO_T_PROJECT_INFO";
	/** 还款计划表*/
	public static final String TABLE_BAO_T_REPAYMENT_PLAN_INFO = "BAO_T_REPAYMENT_PLAN_INFO";
	/** 账户流水表*/
	public static final String TABLE_BAO_T_ACCOUNT_FLOW_INFO = "BAO_T_ACCOUNT_FLOW_INFO";
	/** 优选计划表*/
	public static final String TABLE_BAO_T_WEALTH_INFO = "BAO_T_WEALTH_INFO";
	/** 债权转让表*/
	public static final String TABLE_BAO_T_LOAN_TRANSFER = "BAO_T_LOAN_TRANSFER";
	/** 债权持有表*/
	public static final String TABLE_BAO_T_WEALTH_HOLD_INFO = "BAO_T_WEALTH_HOLD_INFO";
	/** 借款信息表*/
	public static final String TABLE_BAO_T_LOAN_INFO = "BAO_T_LOAN_INFO";
	/** 借款客户信息表*/
	public static final String TABLE_BAO_T_LOAN_CUST_INFO = "BAO_T_LOAN_CUST_INFO";
	/** 业务员业绩奖励表*/
	public static final String TABLE_BAO_T_USER_COMMISSION_INFO = "BAO_T_USER_COMMISSION_INFO";
	/** 债权转让申请表*/
	public static final String TABLE_BAO_T_LOAN_TRANSFER_APPLY = "BAO_T_LOAN_TRANSFER_APPLY";
	/** 智能投顾表*/
	public static final String TABLE_BAO_T_AUTO_INVEST_INFO = "BAO_T_AUTO_INVEST_INFO";
	/** 自动转让表*/
	public static final String TABLE_BAO_T_AUTO_TRANSFER_INFO = "BAO_T_AUTO_TRANSFER_INFO";
	/** 自动发布规则表*/
	public static final String TABLE_BAO_T_AUTO_PUBLISH_INFO = "BAO_T_AUTO_PUBLISH_INFO";
	/** 营业部组织结构表*/
	public static final String TABLE_BAO_T_BUSINESS_DEPT_INFO = "BAO_T_BUSINESS_DEPT_INFO";
	/** 客户活动信息表*/
	public static final String TABLE_BAO_T_CUST_RECOMMEND_INFO = "BAO_T_CUST_RECOMMEND_INFO";
	/** 活动信息表*/
	public static final String TABLE_BAO_T_ACTIVITY_INFO = "BAO_T_ACTIVITY_INFO";
	/** 用户预约投资表*/
	public static final String TABLE_BAO_T_RESERVE_INVEST_INFO = "BAO_T_RESERVE_INVEST_INFO";
	/** 信息披露文档表**/
	public static final String TABLE_BAO_T_INFO_DOCUMENT ="BAO_T_INFO_DOCUMENT";
	/** 渠道表*/
	public static final String TABLE_BAO_T_CHANNEL_INFO = "BAO_T_CHANNEL_INFO";
	/** 实名认证表*/
	public static final String TABLE_BAO_T_REAL_NAME_INFO = "BAO_T_REAL_NAME_INFO";
	/** 预约表 */
	public static final String TABLE_BAO_T_LOAN_RESERVE = "BAO_T_LOAN_RESERVE";
	/** 加息奖励表*/
	public static final String TABLE_BAO_T_PURCHASE_AWARD = "BAO_T_PURCHASE_AWARD";
	/** 代扣扩展表*/
	public static final String BAO_T_WITH_HOLDING_EXPAND = "BAO_T_WITH_HOLDING_EXPAND";
	// ------------------------------------------------关联表字段名------------------------------------------------------
	/** 公共ID列名 */
	public static final String RELATE_COLUMN_NAME_ID = "ID";

	/** 公共客户ID列名 */
	public static final String RELATE_COLUMN_NAME_CUSTID = "CUST_ID";

	// ------------------------------------------------附件类型------------------------------------------------------
	/** 有效 **/
	public final static String VALID_STATUS_VALID = "有效";

	/** 无效 **/
	public final static String VALID_STATUS_INVALID = "无效";
	
	// ------------------------------------------------附件展示------------------------------------------------------
	/** 附件展示--内部 */
	public static final String SHOW_TYPE_INTERNAL = "内部";
	/** 附件展示--外部 */
	public static final String SHOW_TYPE_EXTERNAL = "外部";

	// -----------------------------------------------注册类型-------------------------------------------------------
	public static final String REG_ENABLE_STATUS = "正常";

	// -----------------------------------------------短信类型-------------------------------------------------------
	public static final String[] SMS_TYPE = { "注册", "手机找回密码", "绑定银行卡", "绑定邮箱", "邮件找回密码", "绑定手机", "提现", "手机找回提现密码", "修改绑定银行卡", "手机找回交易密码" };

	/** 短信类型 --注册 */
	public static final String SMS_TYPE_REGISTER = "注册";

	/** 短信类型 --手机找回密码 */
	public static final String SMS_TYPE_FIND_PASSWORD = "手机找回密码";

	/** 短信类型 --绑定银行卡 */
	public static final String SMS_TYPE_BANKCARD = "绑定银行卡";

	/** 短信类型 --修改绑定银行卡 */
	public static final String SMS_TYPE_MODIFY_BANKCARD = "修改绑定银行卡";

	/** 短信类型 --绑定邮箱 */
	public static final String SMS_TYPE_BINDING_EMAIL = "绑定邮箱";

	/** 短信类型 --邮件找回密码 */
	public static final String SMS_TYPE_FIND_PASSWORD_EMAIL = "邮件找回密码";

	/** 短信类型 --绑定手机 */
	public static final String SMS_TYPE_BINDING_MOBILE = "绑定手机";

	/** 短信类型 --绑定新手机 **/
	public static final String SMS_TYPE_BINDING_NEW_MOBILE = "绑定新手机";
	
	/** 短信类型 --修改绑定手机 **/
	public static final String SMS_TYPE_UPDATE_MOBILE = "修改绑定手机";

	/** 短信类型 --提现 */
	public static final String SMS_TYPE_WITHDRAWAL = "提现";

	/** 短信类型 --手机找回提现密码 */
	public static final String SMS_TYPE_WITHDRAWAL_BACK = "手机找回提现密码";

	/** 短信类型 --手机找回交易密码 */
	public static final String SMS_TYPE_TRADE_PASSWD = "手机找回交易密码";
	
	/** 短信类型 --提现申请成功 */
	public static final String SMS_TYPE_WITHDRAWAL_APPLY = "提现申请成功";
	
	/** 短信类型 --提现审核通过*/
	public static final String SMS_TYPE_WITHDRAW_SUCCESS = "提现审核通过";
	
	/** 短信类型 --提现审核拒绝*/
	public static final String SMS_TYPE_WITHDRAW_FAIL = "提现审核拒绝";
	
	/** 短信类型 --体验宝赎回*/
	public static final String SMS_TYPE_EXPERIENCE_WITHDRAW = "体验宝赎回";
	
	/** 短信类型 --解绑申请拒绝*/
	public static final String SMS_TYPE_UNBIND_REFUSE = "解绑申请拒绝";
	
	/** 短信类型 --解绑申请通过*/
	public static final String SMS_TYPE_UNBIND_PASS = "解绑申请通过";
	
	/** 短信类型 --到期赎回定期宝*/
	public static final String SMS_TYPE_TERM_ATONE = "到期赎回定期宝";
	
	/** 短信类型 --提前赎回定期宝申请*/
	public static final String SMS_TYPE_ADVANCED_ATONE_APPLY = "提前赎回定期宝申请";
	
	/** 短信类型 --提前赎回定期宝到帐*/
	public static final String SMS_TYPE_ADVANCED_ATONE_SUCCESS = "提前赎回定期宝到帐";
	
	/** 短信类型 --企业借款流标*/
	public static final String SMS_TYPE_PROJECT_UNRELEASE = "企业借款流标";
	
	/** 短信类型 --企业借款生效*/
	public static final String SMS_TYPE_PROJECT_RELEASE = "企业借款生效";
	
	/** 短信类型 --企业借款回款*/
	public static final String SMS_TYPE_PROJECT_NORMAL_REPAYMENT = "企业借款回款";
	
	/** 短信类型 --企业借款到期*/
	public static final String SMS_TYPE_PROJECT_FINAL_REPAYMENT = "企业借款到期";
	
	/** 短信类型 --企业借款提前结清*/
	public static final String SMS_TYPE_PROJECT_EARLY_REPAYMENT = "企业借款提前结清";
	
	/** 短信类型 --提前赎回优选计划申请*/
	public static final String SMS_TYPE_WEALTH_ADVANCED_ATONE_APPLY = "提前赎回优选计划申请";
	
	/** 短信类型 --优选计划提前赎回*/
	public static final String SMS_TYPE_WEALTH_ADVANCE_ATONE = "优选计划提前赎回到帐";
	
	/** 短信类型 --优选计划到期赎回*/
	public static final String SMS_TYPE_WEALTH_DUE_ATONE = "优选计划到期赎回";
	
	/** 短信类型 --优选计划生效*/
	public static final String SMS_TYPE_WEALTH_RELEASE = "优选计划生效";
	
	/** 短信类型 --优选计划返息*/
	public static final String SMS_TYPE_WEALTH_INTEREST = "优选计划返息";
	
	/** 短信类型 --线下充值成功*/
	public static final String SMS_TYPE_OFFLINE_RECHARGE = "线下充值成功";
	
	/** 短信类型 --银行卡绑定成功*/
	public static final String SMS_TYPE_BIND_BANKCARD = "银行卡绑定成功";
	
	/** 短信类型 --优选项目流标*/
	public static final String SMS_TYPE_LOAN_UNRELEASE = "优选项目流标";
	
	/** 短信类型 --优选项目生效*/
	public static final String SMS_TYPE_LOAN_RELEASE = "优选项目生效";
	
	/** 短信类型 --优选项目回款*/
	public static final String SMS_TYPE_LOAN_NORMAL_REPAYMENT = "优选项目回款";
	
	/** 短信类型 --债权转让*/
	public static final String SMS_TYPE_LOAN_TRASNFER = "债权转让";
	
	/** 短信类型 --债权转让(原始投资人)*/
	public static final String SMS_TYPE_LOAN_TRASNFER_OWNER = "债权转让(原始投资人)";
	
	/** 短信类型 --投资满额奖励活动*/
	public static final String SMS_TYPE_ACTIVITY_REWARD = "投资满额奖励活动";
	
	/** 短信类型 --红包到账通知*/
	public static final String SMS_TYPE_RED_BAG = "红包活动";
	
	/** 短信类型 --活动金额提现审核通过*/
	public static final String SMS_TYPE_ACTIVITY_AMOUNT_WITHDRAW_SUCCESS = "活动金额提现审核通过";
	
	/** 短信类型 --活动金额提现审核拒绝*/
	public static final String SMS_TYPE_ACTIVITY_AMOUNT_WITHDRAW_FAIL = "活动金额提现审核拒绝";

	// -----------------------------------------------单独发送的站内信-------------------------------------------------------
	/** 站内信类型 --线下充值审核拒绝、回退*/
	public static final String SYS_TYPE_OFFLINE_RECHARGE_FAIL = "线下充值审核拒绝、回退";
	
	/** 站内信类型 --绑定线下银行卡审核拒绝、回退 */
	public static final String SYS_TYPE_BIND_BANKCARD_FAIL = "绑定线下银行卡审核拒绝、回退";
	
	// -----------------------------------------------短信状态-------------------------------------------------------
	/** 短信状态 --已发送 */
	public static final String SMS_SEND_STATUS_SENT = "已发送";

	/** 短信状态 --未发送 */
	public static final String SMS_SEND_STATUS_UNSENT = "未发送";

	// -----------------------------------------------邮箱模板名称-------------------------------------------------------
	/** 邮箱绑定模板 **/
	public static final String BIND_MAIL_TEMPLATE = "slcf_bindEmailTemplate.ftl";

	// -----------------------------------------------客服信息-------------------------------------------------------
	/** 平台客服电话 */
	public static final String PLATFORM_SERVICE_TEL = "400-677-3030";

	/** 平台客服邮箱 */
	public static final String CUSTOMER_EMAIL = "slcf@shanlinjinrong.com";

	// ------------------------------------------------------------------------------------------------------
	/** 目标类型(手机,邮箱) */
	public static final String TARGET_TYPE_TEL = "手机";

	public static final String TARGET_TYPE_MAIL = "邮箱";

	// ---------------------------------------------客户状态---------------------------------------------------------
	/** 客户状态--是否启用-正常 **/
	public static final String ENABLE_STATUS_01 = "正常";

	/** 客户状态--是否启用-冻结 **/
	public static final String ENABLE_STATUS_02 = "冻结";

	// ------------------------------------------------客户性别------------------------------------------------------
	/** 客户性别--男 **/
	public static final String SEX_MAN = "男";

	/** 客户性别--女 **/
	public static final String SEX_WOMAN = "女";
	
	// ----------------------------------------------客户类型---------------------------------------------------------
	/** 客户类型--线上*/
	public static final String CUST_TYPE_ONLINE = "线上";
	
	/** 客户类型--线下*/
	public static final String CUST_TYPE_OFFLINE = "线下";
	
	/** 客户类型--借款客户*/
	public static final String CUST_TYPE_LOANER = "借款客户";
	
	/** 客户类型--公司客户*/
	public static final String CUST_TYPE_COMPNAY = "公司客户";

	// ----------------------------------------------证件类型--------------------------------------------------------
	/** 证件类型--身份证 */
	public static final String CREDENTIALS_ID_CARD = "身份证";
	/** 证件类型--借款客户信息身份证 */
	public static final String LOAN_ID_CARD = "1";

	// ---------------------------------------------认证状态-----------------------------------------------------------
	/** 认证状态-- 未审核 */
	public static final String AUTH_STATUS_UNAUDIT = "未审核";

	/** 认证状态-- 通过 */
	public static final String AUTH_STATUS_PASS = "通过";

	/** 认证状态-- 驳回 */
	public static final String AUTH_STATUS_REJECT = "驳回";

	/** 认证状态-- 失效 */
	public static final String AUTH_STATUS_INVALID = "失效";

	
	// ---------------------------------------------善林财富-产品类型-----------------------------------------------------------
	/** 产品类型-活期宝 */
	public static final String PRODUCT_TYPE_01 = "活期宝";
	/** 产品类型-善林理财 */
	public static final String PRODUCT_TYPE_02 = "善林理财"; 
	/** 产品类型-体验宝 */
	public static final String PRODUCT_TYPE_03 = "体验宝";
	/** 产品类型-定期宝 */
	public static final String PRODUCT_TYPE_04 = "定期宝";
	/** 产品类型-企业借款 */
	public static final String PRODUCT_TYPE_05 = "企业借款";
	/** 产品类型-优选计划 */
	public static final String PRODUCT_TYPE_06 = "优选计划";
	/** 产品类型-散标投资 */
	public static final String PRODUCT_TYPE_07 = "散标投资";
	/** 产品类型（优选项目）*/
	public static final String PRODUCT_TYPE_08 = "优选项目";
	/** 产品类型（转让专区）*/
	public static final String PRODUCT_TYPE_09 = "转让专区";
	/** 产品类型（债权转让）*/
	public static final String PRODUCT_TYPE_10 = "债权转让";
	/** 产品类型（新手专区）*/
	public static final String PRODUCT_TYPE_11 = "新手专区";
	
	// ---------------------------------------------善林财富-产品名称-----------------------------------------------------------
	/** 产品名称-单季盈 */
	public static final String PRODUCT_NAME_01 = "单季盈";
	/** 产品名称-双季盈 */
	public static final String PRODUCT_NAME_02 = "双季盈";
	/** 产品名称-全年盈 */
	public static final String PRODUCT_NAME_03 = "全年盈";

	// ----------------------------------------------分配状态--------------------------------------------------------
	public static final String ALLOT_STATUS_01 = "已分配";
	public static final String ALLOT_STATUS_02 = "已使用";
	public static final String ALLOT_STATUS_03 = "已撤销";

	/** 执行状态-已执行 */
	public static final String EXEC_STATUS = "已执行";
	/** 执行状态-未执行 */
	public static final String EXEC_UN_STATUS = "未执行";
	/** 执行状态-运行中 */
	public static final String EXEC_STATUS_RUNNING = "运行中";
	

	public static final String PASS_TYPE_LOGIN = "1";

	public static final String PASS_TYPE_TRADE = "2";

	// -----------------------------------------------交易状态-------------------------------------------------------
	/** 交易状态--未处理 */
	public static final String TRADE_STATUS_01 = "未处理";

	/** 交易状态--处理中 */
	public static final String TRADE_STATUS_02 = "处理中";

	/** 交易状态--处理成功 */
	public static final String TRADE_STATUS_03 = "处理成功";

	/** 交易状态--处理失败 */
	public static final String TRADE_STATUS_04 = "处理失败";

	// ---------------------------------------------------提现审核状态---------------------------------------------------
	/** 提现审核状态--待审核*/
	public static final String AUDIT_STATUS_UNREVIEW = "待审核";
	
	/** 提现审核状态--审核中 */
	public static final String AUDIT_STATUS_REVIEWD = "审核中";

	/** 提现审核状态--拒绝 */
	public static final String AUDIT_STATUS_REfUSE = "拒绝";

	/** 提现审核状态--通过 */
	public static final String AUDIT_STATUS_PASS = "通过";
	
	/** 提现审核状态--回退 */
	public static final String AUDIT_STATUS_FALLBACK = "回退";
	
	// --------------------------------------------------企业借款调账审核状态-------------------------------------------------
	/** 企业借款调账审核状态 --待审核*/
	public static final String AUDIT_PROJECT_STATUS_REVIEWING = "待审核";
	
	/** 企业借款调账审核状态 --拒绝*/
	public static final String AUDIT_PROJECT_STATUS_REfUSE = "拒绝";
	
	/** 企业借款调账审核状态 --通过*/
	public static final String AUDIT_PROJECT_STATUS_PASS = "通过";

	// ---------------------------------------------------审核日志类型---------------------------------------------------
	/** 审核日志类型--审核中 */
	public static final String LOG_TYPE_AUDIT = "审核";

	// ---------------------------------------------网站公告状态-----------------------------------------------------------
	/** 网站公告状态-新建 **/
	public static final String AFFICHE_STATUS_NEW = "新建";

	/** 网站公告状态-已发布 **/
	public static final String AFFICHE_STATUS_PUBLISHED = "已发布";

	/** 网站公告状态-已失效 **/
	public static final String AFFICHE_STATUS_INVALIED = "已失效";

	/** 网站公告状态-已删除 **/
	public static final String AFFICHE_STATUS_DELETED = "已删除";

	// ---------------------------------------------网站公告类型-----------------------------------------------------------
	/** 网站公告类型-网站公告 **/
	public static final String AFFICHE_TYPE_ALL = "网站公告";

	/** 网站公告类型-网站通知 **/
	public static final String AFFICHE_TYPE_NOTICE = "网站通知";

	/** 网站公告类型 -行业动态 **/
	public static final String AFFICHE_TYPE_NEWS = "行业动态";

	/** 网站公告类型-公司动态 **/
	public static final String AFFICHE_TYPE_COMPANY = "公司动态";

	/** 网站公告类型-banner **/
	public static final String AFFICHE_TYPE_BANNER = "banner";
	
	/** 网站公告类型-媒体报道 **/
	public static final String AFFICHE_TYPE_MEDIA = "媒体报道";
	
	/** 网站公告类型-通知-业务员 **/
	public static final String AFFICHE_TYPE_SALESMAN = "通知-业务员";

	// -----------------------------------------------消息状态-------------------------------------------------------
	/** 消息状态 未读 */
	public static final String SITE_MESSAGE_NOREAD = "未读";

	/** 消息状态 已读 */
	public static final String SITE_MESSAGE_ISREAD = "已读";

	// ----------------------------------------------产品状态--------------------------------------------------------
	/** 产品状态-开放中 */
	public static final String PRODUCT_STATUS_BID_ING = "开放中";
	/** 产品状态-已满标 */
	public static final String PRODUCT_STATUS_BID_FINISH = "已满额";

	// ----------------------------------------------投资方式--------------------------------------------------------
	/** 投资方式-加入 */
	public static final String INVEST_METHOD_JOIN = "加入";
	/** 投资方式-转让 */
	public static final String INVEST_METHOD_TRANSFER = "转让";

	// ----------------------------------------------投资来源--------------------------------------------------------
	/** 投资来源-PC端 */
	public static final String INVEST_SOURCE_PC = "web";
	/** 投资来源-APP端 */
	public static final String INVEST_SOURCE_APP = "APP端";

	// ----------------------------------------------赎回方式--------------------------------------------------------
	/** 赎回方式-快速赎回 */
	public static final String ATONE_METHOD_IMMEDIATE = "快速赎回";
	/** 赎回方式-普通赎回 */
	public static final String ATONE_METHOD_NORMAL = "普通赎回";
	/** 赎回方式-提前赎回 */
	public static final String ATONE_METHOD_ADVANCE = "提前赎回";
	/** 赎回方式-到期赎回 */
	public static final String ATONE_METHOD_EXPIRE = "到期赎回";

	// ----------------------------------------------账户类型--------------------------------------------------------
	public static final String ACCOUNT_TYPE_BASIC = "01";

	// ----------------------------------------------资金方向--------------------------------------------------------
	/** 资金方向--转入 */
	public static final String BANKROLL_FLOW_DIRECTION_IN = "收入";

	/** 资金方向--转出 */
	public static final String BANKROLL_FLOW_DIRECTION_OUT = "支出";

	/** 资金方向--无 */
	public static final String BANKROLL_FLOW_DIRECTION_NONE = "";

	// ----------------------------------------------还款状态--------------------------------------------------------
	/** 还款状态-未还款 */
	public static final String REPAYMENT_STATUS_WAIT = "未还款";
	/** 还款状态-已还款 */
	public static final String REPAYMENT_STATUS_CLEAN = "已还款";
	
	// ----------------------------------------------债权状态--------------------------------------------------------
	public static final String CREDIT_RIGHT_STATUS_CLEAN = "结清";
	public static final String CREDIT_RIGHT_STATUS_NORMAL = "正常";

	// ----------------------------------------------账户类型--------------------------------------------------------
	/** 账户类型-总帐 */
	public static final String ACCOUNT_TYPE_MAIN = "总账";
	/** 账户类型-分账 */
	public static final String ACCOUNT_TYPE_SUB = "分账";

	// -----------------------------------------------客户类型-------------------------------------------------------
	/**
	 * 公司收益账户
	 */
	public static final String ACCOUNT_TYPE_COMPANY = "公司收益账户";
	/**
	 * 客户账户
	 */
	public static final String ACCOUNT_TYPE_CUSTOMER = "客户账户";
	/**
	 * 居间人账户
	 */
	public static final String ACCOUNT_TYPE_MIDDLE = "居间人账户";
	/**
	 * 定期宝账户
	 */
	public static final String ACCOUNT_TYPE_SLBAO = "定期宝账户";	

	
	/** 初始推广等级 */
	public static final String SPREAD_LEVEL_ROOT = "0";
	/** 推广来源ID */
	public static final String INVITE_ORIGIN_ID_ROOT = "0";

	// ----------------------------------------------系统初始化用户ID--------------------------------------------------------
	/** 系统管理员用户主键ID */
	public static final String CUST_ADMIN_ID = "SYSTEM_ADMIN";

	/** 客服用户主键ID */
	public static final String CUST_KF_ID = "KF0001";
	
	/** 居间人主键ID*/
	public static final String CUST_ID_CENTER = "C00001";
	/** 收益人主键ID*/
	public static final String CUST_ID_ERAN = "C00002";
	/** 风险金主键ID*/
	public static final String CUST_ID_RISK = "C00003";
	/** 融资租赁收益账户主键ID*/
	public static final String CUST_ID_PROJECT_ERAN = "C00004";
	/** 融资租赁风险金账户ID*/
	public static final String CUST_ID_PROJECT_RISK = "C00005";
	/** 公司主账户-优选计划还款专用账户 ID*/
	public static final String CUST_ID_REPAYMENT = "C00006";
	/** 公司主账户-优选计划居间人账户 ID*/
	public static final String CUST_ID_WEALTH_CENTER = "C00007";
	/** 公司主账户-业绩奖励账户 ID*/
	public static final String CUST_ID_COMMISION = "C00008";
	/**
	 * 公司主账户-红包 ID
	 */
	public static final String CUST_ID_RED_ENVELOP = "C00009";
	/**
	 * 公司主账户-优选项目奖励 ID
	 */
	public static final String CUST_ID_LOAN_AWARD = "C00010";

	// ----------------------------------------------系统用户--------------------------------------------------------
	/** 系统用户 */
	public static final String SYSTEM_USER_BACK = "root";
	// ----------------------------------------------公司主账户ID--------------------------------------------------------
	/** 公司主账户-居间人账户 ID*/
	public static final String ACCOUNT_ID_CENTER = "A00001";
	/** 公司主账户-收益账户 ID*/
	public static final String ACCOUNT_ID_ERAN = "A00002";
	/** 公司主账户-风险金账户 ID*/
	public static final String ACCOUNT_ID_RISK = "A00003";
	/** 公司主账户- 融资租赁收益账户 ID*/
	public static final String ACCOUNT_ID_PROJECT_ERAN = "A00004";
	/** 公司主账户-融资租赁风险金账户 ID*/
	public static final String ACCOUNT_ID_PROJECT_RISK = "A00005";
	/** 公司主账户-优选计划还款专用账户 ID*/
	public static final String ACCOUNT_ID_REPAYMENT = "A00006";
	/** 公司主账户-优选计划居间人账户 ID*/
	public static final String ACCOUNT_ID_WEALTH_CENTER = "A00007";
	/** 公司主账户-业绩奖励账户 ID*/
	public static final String ACCOUNT_ID_COMMISION = "A00008";
	
	/** 公司主账户-优选项目奖励 ID*/
	public static final String ACCOUNT_ID_LOAN_AWARD = "A00010";
	
	// ----------------------------------------------公司分账户ID--------------------------------------------------------
	/** 公司分账户-居间人账户ID */
	public static final String SUB_ACCOUNT_ID_CENTER = "SA0001";
	/** 公司分账户-收益账户 ID*/
	public static final String SUB_ACCOUNT_ID_ERAN = "SA0002";
	
	/** 定期宝公司分账户-居间人账户ID */
	public static final String SUB_ACCOUNT_ID_CENTER_11 = "SA0011";
	/** 定期宝公司分账户-收益账户 ID*/
	public static final String SUB_ACCOUNT_ID_ERAN_12 = "SA0012";
	/** 定期宝公司分账户-风险金账户 ID*/
	public static final String SUB_ACCOUNT_ID_ERAN_13 = "SA0013";
	
	// ----------------------------------------------公司主账户编号--------------------------------------------------------
	/** 公司主账户-居间人账户 编号*/
	public static final String ACCOUNT_NO_CENTER = "ACCT000001";
	/** 公司主账户-收益账户 编号*/
	public static final String ACCOUNT_NO_ERAN = "ACCT000002";
	/** 公司主账户-风险金账户 编号*/
	public static final String ACCOUNT_NO_RISK = "ACCT000003";
	/**
	 *公司主账户-红包账户 编号
	 */
	public static final String ACCOUNT_NO_RED_ENVELOPE = "ACCT000009";

	// ----------------------------------------------公司分账户编号--------------------------------------------------------
	/** 公司分账户-居间人账户编号 */
	public static final String SUB_ACCOUNT_NO_CENTER = "SACCT00001";
	/** 公司分账户-收益账户 编号*/
	public static final String SUB_ACCOUNT_NO_ERAN = "SACCT00002";
	
	/** 公司分账户-居间人账户编号 */
	public static final String SUB_ACCOUNT_NO_CENTER_11 = "SACCT00011";
	/** 公司分账户-收益账户 编号*/
	public static final String SUB_ACCOUNT_NO_ERAN_12 = "SACCT00012";	
	/** 公司分账户-风险金账户 编号*/
	public static final String SUB_ACCOUNT_NO_RISK_13 = "SACCT00013";
	
	// ---------------------------------------------公司银行卡Id---------------------------------------------------------
	/** 公司银行卡Id*/
	public static final String BANK_ID_WEALTH_CENTER = "B00001";

	// -----------------------------------------------------job名称-------------------------------------------------
	/** 定时任务--债权价值计算 */
	public static final String JOB_NAME_LOANVAULECALCULATEJOB = "债权价值计算";
	/** 定时任务--定时发标 */
	public static final String JOB_NAME_RELEASEJOB = "定时发标";
	/** 定时任务--定时关标 */
	public static final String JOB_NAME_CLOSEJOB = "定时关标";
	/** 定时任务--定时授权申请 */
	public static final String JOB_NAME_ACCREDITREQUESTJOB = "定时授权申请";
	/** 定时任务--每日结息 */
	public static final String JOB_NAME_DAILYACCRUALJOB = "每日结息";
	/** 定时任务--可开放价值计算 */
	public static final String JOB_NAME_OPENVALUEJOB = "可开放价值计算";
	/** 定时任务--还款计算 */
	public static final String JOB_NAME_REPAYMENTJOB = "还款计算";
	/** 定时任务--赎回详情 */
	public static final String JOB_NAME_ATONEDETAIL = "赎回详情";	
	/** 定时任务--体验宝每日结息 */
	public static final String JOB_NAME_TYBDAILYACCRUALJOB = "体验宝每日结息";	
	/** 定时任务--体验宝到期赎回 */
	public static final String JOB_NAME_TYBWITHDRAWJOB = "体验宝到期赎回";
	/** 定时任务--推荐奖励 */
	public static final String JOB_NAME_RECOMMENDEDAWARDS = "推荐奖励记录生成";
	/** 定时任务--对外定时通知 */
	public static final String JOB_NAME_OPENSERVICE_NOTIFY = "对外定时通知";
	/** 定时任务--体验宝到期赎回 短信通知用户任务*/
	public static final String JOB_NAME_TYBWITHDRAWSENDSMSJOB = "体验宝到期赎回短信通知用户";
	/** 定时任务--补银行卡 */
	public static final String JOB_NAME_MENDBANK = "补银行卡信息";	
	/** 定时任务--每日还款数据发送邮件 */
	public static final String JOB_REPAYMENT_EMAIL = "每日还款数据发送邮件";	
	/** 定时任务--公司回购 */
	public static final String JOB_NAME_TERM_ATONE_BUY = "公司回购";	
	/** 定时任务--定期宝赎回到帐 */
	public static final String JOB_NAME_TERM_ATONE_SETTLEMENT = "定期宝赎回到帐";	
	/** 定时任务--到期赎回 */
	public static final String JOB_NAME_TERM_ATONE_WITHDRAW = "到期赎回";	
	/** 定时任务--定期宝每日结息 */
	public static final String JOB_NAME_TERM_DALIY_SETTLEMENT = "定期宝每日结息";
	/** 定时任务--金牌推荐人每日结息 */
	public static final String JOB_NAME_GOLD_DALIY_SETTLEMENT = "金牌推荐人每日结息";
	/** 定时任务--金牌推荐人到期处理 */
	public static final String JOB_NAME_GOLD_WITHDRAW = "金牌推荐人到期处理";
	/** 定时任务--数据汇总*/
	public static final String JOB_NAME_SUMMARIZATION = "数据汇总";
	/** 定时任务--活期宝价值回收*/
	public static final String JOB_NAME_RECOVER_ATONE = "活期宝价值回收";
	/** 定时任务--企业借款定时发布*/
	public static final String JOB_NAME_PROJECT_PUBLISH = "企业借款定时发布";
	/** 定时任务--企业借款定时生效*/
	public static final String JOB_NAME_PROJECT_RELEASE = "企业借款定时生效";
	/** 定时任务--企业借款定时流标*/
	public static final String JOB_NAME_PROJECT_UNRELEASE = "企业借款定时流标";
	/** 定时任务--企业借款定时贴息*/
	public static final String JOB_NAME_PROJECT_COMPENSATE = "企业借款定时贴息";
	/** 定时任务--企业借款定时还款*/
	public static final String JOB_NAME_PROJECT_REPAYMENT = "企业借款定时还款";
	/** 定时任务--企业借款定时垫付*/
	public static final String JOB_NAME_PROJECT_RISK_REPAYMENT = "企业借款定时垫付";
	/** 定时任务--企业借款定时审核拒绝*/
	public static final String JOB_NAME_PROJECT_AUDIT_REFUSE = "企业借款定时审核拒绝";
	/** 定时任务--发布理财我计划*/
	public static final String JOB_NAME_WEALTH_PUBLISH = "优选计划发布";
	/** 定时任务--生效优选计划 */
	public static final String JOB_NAME_WEALTH_RELEASE = "优选计划生效";
	/** 定时任务--生效优选计划 */
	public static final String JOB_NAME_WEALTH_MATCH_LOAN = "优选计划撮合";
	/** 定时任务--生效优选计划 */
	public static final String JOB_NAME_WEALTH_REPAYMENT = "优选计划还款";
	/** 定时任务--生效优选计划 */
	public static final String JOB_NAME_WEALTH_RECOVERY = "优选计划返息";
	/** 定时任务--生效优选计划 */
	public static final String JOB_NAME_WEALTH_ATONE = "优选计划赎回";
	/** 定时任务--生效优选计划 */
	public static final String JOB_NAME_WEALTH_UN_RELEASE = "优选计划流标";
	/** 定时任务--线下业务员导入原始数据处理 */
	public static final String JOB_NAME_EMPLOYEELOAD_HANDLE = "线下业务员原始数据处理";
	/** 定时任务--线下业务员同步到线上 */
	public static final String JOB_NAME_TRANSFER_ONLINE = "线下客户经理同步到线上";
	/** 定时任务--线下充值、附属银行卡，有待审核的数据 */
	public static final String JOB_NAME_RECHARGE_OFFLINE_UNREVIEW = "附属银行卡线下充值有待审的数据发送邮件";
	/** 定时任务--优选项目业绩统计 */
	public static final String JOB_NAME_GOLD_COMMISION = "优选项目业绩统计";
	/** 定时任务--优选项目流标 */
	public static final String JOB_NAME_LOAN_UN_RELEASE = "优选项目流标";
	/** 定时任务--优选项目还款 */
	public static final String JOB_NAME_LOAN_REPAYMENT = "优选项目还款";
	/** 定时任务--优选项目放款 */
	public static final String JOB_NAME_LOAN_GRANT = "优选项目放款";
	/** 定时任务--优选项目放款意真 */
    public static final String JOB_NAME_LOAN_GRANT_YZ = "优选项目放款意真";
    /** 定时任务--优选项目放款巨涟 */
    public static final String JOB_NAME_LOAN_GRANT_JL = "优选项目放款巨涟";
    /** 定时任务--优选项目放款财富现金贷 */
    public static final String JOB_NAME_LOAN_GRANT_CFXJD = "优选项目放款财富现金贷";
	/** 定时任务--优选项目放款确认 */
	public static final String JOB_NAME_LOAN_GRANT_CONFIRM = "优选项目放款确认";
	/** 定时任务--优选项目放款确认意真 */
	public static final String JOB_NAME_LOAN_GRANT_CONFIRM_YZ = "优选项目放款确认意真";
	/** 定时任务--优选项目放款确认巨涟 */
	public static final String JOB_NAME_LOAN_GRANT_CONFIRM_JL = "优选项目放款确认巨涟";
	/** 定时任务--优选项目放款确认财富现金贷 */
	public static final String JOB_NAME_LOAN_GRANT_CONFIRM_CFXJD = "优选项目放款确认财富现金贷";
	/** 定时任务--代扣 */
	public static final String JOB_NAME_WITHHOLD = "代扣扣款定时任务";

	/** 定时任务--代扣结果查询 */
	public static final String JOB_NAME_WITHHOLD_QUERY = "扣款结果查询定时任务";
	/** 定时任务--代扣通知推送 */
	public static final String JOB_NAME_WITHHOLD_NOTIFY = "代扣通知定时任务";
	/** 定时任务--优选项目取消债权转让申请 */
	public static final String JOB_NAME_LOAN_CANCEL = "优选项目取消债权转让申请";
	/** 定时任务--优选项目发布 */
	public static final String JOB_NAME_LOAN_PUBLISH = "优选项目发布";
	/** 定时任务--智能投顾 */
	public static final String JOB_NAME_AUTO_INVEST = "智能投顾";
	/** 定时任务--自动转让 */
	public static final String JOB_NAME_AUTO_TRANSFER = "自动转让";
	/** 定时任务--自动发布 */
	public static final String JOB_NAME_AUTO_PUBLISH = "自动发布";
	/** 定时任务--平台每日数据汇总 */
	public static final String JOB_NAME_DAILY_DATA_SUMMARY = "平台每日数据汇总";
	/** 定时任务--平台每日推送数据汇总 */
	public static final String JOB_NAME_DAILY_PUSH_DATA_SUMMARY = "平台每日推送数据汇总";
	/** 定时任务--发送手动还款请求 */
	public static final String JOB_NAME_SEND_MANUAL_REPAYMENT = "发送手动还款请求";
	/** 定时任务--自动审核通过拿米*/
    public static final String JOB_NAME_AUTO_AUDIT_PASS_NM = "自动审核通过拿米";
	/** 定时任务--自动审核通过 */
    public static final String JOB_NAME_AUTO_AUDIT_PASS = "自动审核通过";
    /** 定时任务--自动审核通过巨涟 */
    public static final String JOB_NAME_AUTO_AUDIT_PASS_JL = "自动审核通过巨涟";
    /** 定时任务--自动审核通过财富现金贷 */
    public static final String JOB_NAME_AUTO_AUDIT_PASS_CFXJD = "自动审核通过财富现金贷";
    /** 定时任务--每日定时统计平台数据 */
    public static final String JOB_NAME_AUTO_PLATFORM_DATA_DAY = "每日定时统计平台数据";
    /** 定时任务--意真每日放款数据汇总 */
  	public static final String JOB_NAME_DAILY_DATA_GRANT_AMOUNT_YZ = "意真每日放款数据汇总";
    /** 定时任务--每月定时统计平台数据 */
    public static final String JOB_NAME_AUTO_PLATFORM_DATA_MONTH = "每月定时统计平台数据";
    /** 定时任务--预约撤销 */
    public static final String JOB_NAME_AUTO_RESERVE_CANCEL = "预约撤销 ";

    /** 定时任务--每日更新过期红包状态 */
    public static final String JOB_NAME_AUTO_EXPIRE_DAY = "每日更新过期红包状态";

	/**
	 * 定时任务--红包账户扣款流水
	 */
	public static final String JOB_NAME_RED_ENVELOPE_FLOW = "红包账户扣款流水";
	
	/**
	 * 定时任务--一键出借出队任务
	 */
	public static final String JOB_NAME_GROUP_POP = "一键出借出队任务";

	// ------------------------------------------------日期格式-----------------------------------------------------------------
	public static final String DATE_FORMAT_STYLE_YYYYMMDD = "yyyyMMdd";

	// ------------------------------------------------操作类型-----------------------------------------------------------------
	/** 操作类型--客户冻结 */
	public static final String OPERATION_TYPE_01 = "客户冻结";
	/** 操作类型--客户解冻 */
	public static final String OPERATION_TYPE_02 = "客户解冻";
	/** 操作类型--实名认证 */
	public static final String OPERATION_TYPE_03 = "实名认证";
	/** 操作类型--登录 */
	public static final String OPERATION_TYPE_04 = "登录";
	/** 操作类型--充值 */
	public static final String OPERATION_TYPE_05 = "充值";
	/** 操作类型--提现 */
	public static final String OPERATION_TYPE_06 = "提现";
	/** 操作类型--绑定银行卡 */
	public static final String OPERATION_TYPE_07 = "绑定银行卡";
	/** 操作类型--购买活期宝 */
	public static final String OPERATION_TYPE_08 = "购买活期宝";
	/** 操作类型--赎回活期宝 */
	public static final String OPERATION_TYPE_09 = "赎回活期宝";
	/** 操作类型--提现审核 */
	public static final String OPERATION_TYPE_10 = "提现审核";
	/** 操作类型--赎回审核 */
	public static final String OPERATION_TYPE_12 = "赎回审核";
	/** 操作类型--注册送体验金 */
	public static final String OPERATION_TYPE_13 = "注册送体验金";
	/** 操作类型--购买体验宝 */
	public static final String OPERATION_TYPE_14 = "购买体验宝";
	/** 操作类型--赎回体验宝 */
	public static final String OPERATION_TYPE_15 = "赎回体验宝";
	/** 操作类型--注册 */
	public static final String OPERATION_TYPE_16 = "注册";
	/** 操作类型--客户反馈审核 */
	public static final String OPERATION_TYPE_17 = "客户反馈审核";
	/** 操作类型--解绑银行卡 */
	public static final String OPERATION_TYPE_18 = "解绑银行卡";
	/** 操作类型--用户信息修改 */
	public static final String OPERATION_TYPE_19 = "用户信息修改";
	/** 操作类型--联系人信息修改 */
	public static final String OPERATION_TYPE_20 = "联系人信息修改";
	/** 操作类型--购买定期宝 */
	public static final String OPERATION_TYPE_21 = "购买定期宝";
	/** 操作类型--赎回定期宝 */
	public static final String OPERATION_TYPE_22 = "赎回定期宝";
	/** 操作类型--金牌推荐人申请 */
	public static final String OPERATION_TYPE_23 = "金牌推荐人申请";
	/** 操作类型--下载 */
	public static final String OPERATION_TYPE_24 = "下载";
	/** 操作类型--用户昵称修改 */
	public static final String OPERATION_TYPE_25 = "用户昵称修改";
	/** 操作类型--金牌推荐人变更 */
	public static final String OPERATION_TYPE_26 = "金牌推荐人客户经理变更";
	/** 操作类型--购买企业借款 */
	public static final String OPERATION_TYPE_27 = "购买企业借款";
	/** 操作类型--发布企业借款 */
	public static final String OPERATION_TYPE_28 = "发布企业借款";
	/** 操作类型--企业借款流标 */
	public static final String OPERATION_TYPE_29 = "企业借款流标";
	/** 操作类型--企业借款生效*/
	public static final String OPERATION_TYPE_30 = "企业借款生效";
	/** 操作类型--企业借款还款冻结*/
	public static final String OPERATION_TYPE_31 = "企业借款还款冻结";
	/** 操作类型--企业借款逾期还款*/
	public static final String OPERATION_TYPE_32 = "企业借款逾期还款";
	/** 操作类型--企业借款正常还款*/
	public static final String OPERATION_TYPE_33 = "企业借款正常还款";
	/** 操作类型--企业借款提前还款*/
	public static final String OPERATION_TYPE_34 = "企业借款提前还款";
	/** 操作类型--企业借款风险金垫付*/
	public static final String OPERATION_TYPE_35 = "企业借款风险金垫付";
	/** 操作类型--风险金充值*/
	public static final String OPERATION_TYPE_36 = "企业借款风险金充值";
	/** 操作类型--流量活动 */
	public static final String OPERATION_TYPE_40 = "流量活动";
	/** 操作类型--修改银行卡*/
	public static final String OPERATION_TYPE_41 = "修改银行卡";
	/** 操作类型--线下充值*/
	public static final String OPERATION_TYPE_42 = "线下充值";
	/** 操作类型--线下提现*/
	public static final String OPERATION_TYPE_43 = "线下提现";
	/** 操作类型--附属银行卡申请*/
	public static final String OPERATION_TYPE_44 = "附属银行卡申请";
	/** 操作类型--附属银行卡审核*/
	public static final String OPERATION_TYPE_45 = "附属银行卡审核";
	/** 操作类型--赎回 */
	public static final String OPERATION_TYPE_50 = "赎回优选计划";
	/** 操作类型--购买优选计划 */
	public static final String OPERATION_TYPE_51 = "购买优选计划";
	/** 操作类型--购买优选计划 */
	public static final String OPERATION_TYPE_52 = "流标优选计划";
	/** 操作类型--购买优选计划 */
	public static final String OPERATION_TYPE_53 = "提前赎回优选计划";
	/** 操作类型--客户经理变更 */
	public static final String OPERATION_TYPE_54 = "客户经理变更";
	/** 操作类型--客户经理变更申请 */
	public static final String OPERATION_TYPE_55 = "客户经理变更申请";
	/** 操作类型--客户经理变更审核*/
	public final static String OPERATION_TYPE_56 = "客户经理变更审核";
	/** 操作类型--优选计划审核 */
	public static final String OPERATION_TYPE_57 = "优选计划审核";
	/** 操作类型--优选计划发布 */
	public static final String OPERATION_TYPE_58 = "优选计划发布";
	/** 操作类型--优选计划生效 */
	public static final String OPERATION_TYPE_59 = "优选计划生效";
	/** 操作类型--购买优选项目 */
	public static final String OPERATION_TYPE_67 = "购买优选项目";
	/** 操作类型--发布优选项目 */
	public static final String OPERATION_TYPE_68 = "发布优选项目";
	/** 操作类型--优选项目流标 */
	public static final String OPERATION_TYPE_69 = "优选项目流标";
	/** 操作类型--优选项目生效*/
	public static final String OPERATION_TYPE_70 = "优选项目生效";
	/** 操作类型--优选项目还款冻结*/
	public static final String OPERATION_TYPE_71 = "优选项目还款冻结";
	/** 操作类型--优选项目逾期还款*/
	public static final String OPERATION_TYPE_72 = "优选项目逾期还款";
	/** 操作类型--优选项目正常还款*/
	public static final String OPERATION_TYPE_73 = "优选项目正常还款";
	/** 操作类型--优选项目提前还款*/
	public static final String OPERATION_TYPE_74 = "优选项目提前还款";
	/** 操作类型--优选项目风险金垫付*/
	public static final String OPERATION_TYPE_75 = "优选项目风险金垫付";
	/** 操作类型--风险金充值*/
	public static final String OPERATION_TYPE_76 = "优选项目风险金充值";
	/** 操作类型--优选项目审核 */
	public static final String OPERATION_TYPE_60 = "优选项目审核";
	/** 操作类型--优选项目放款 */
	public static final String OPERATION_TYPE_77 = "优选项目放款";
	/** 操作类型--代付 */
	public static final String OPERATION_TYPE_78 = "代付";
	/** 操作类型--优选项目债权转让申请 */
	public static final String OPERATION_TYPE_79 = "优选项目债权转让申请";
	/** 操作类型--优选项目债权转让撤销 */
	public static final String OPERATION_TYPE_80 = "优选项目债权转让撤销";
	/** 操作类型--购买债权转让 */
	public static final String OPERATION_TYPE_81 = "购买债权转让";
	/** 操作类型--债权转让成功 */
	public static final String OPERATION_TYPE_82 = "债权转让成功";
	/** 操作类型--修改转让设置 */
	public static final String OPERATION_TYPE_83 = "修改转让设置 ";
	/** 操作类型--优选项目满标 */
	public static final String OPERATION_TYPE_84 = "优选项目满标";
	/** 操作类型--新建渠道类型 */
	public static final String OPERATION_TYPE_85 = "新建渠道类型";
	/** 操作类型--设置渠道类型失效 */
	public static final String OPERATION_TYPE_86 = "设置渠道类型失效";
	/** 操作类型--直投预约/追加预约 */
	public static final String OPERATION_TYPE_87 = "直投预约/追加预约";
	/** 操作类型--预约撤销 */
	public static final String OPERATION_TYPE_88 = "预约撤销";
	/** 操作类型--大后台实名认证 */
	public static final String OPERATION_TYPE_89 = "大后台实名认证";
	/** 操作类型--购买体验标*/
	public static final String OPERATION_TYPE_90 = "购买体验标";
	/** 操作类型--活动金额提现 */
	public static final String OPERATION_TYPE_91 = "活动金额提现";
	/** 操作类型--一键出借 */
	public static final String OPERATION_TYPE_92 = "一键出借";
	
	// ------------------------------------------------保障方式-----------------------------------------------------------------
	/** 保障方式--01 */
	public static final String ENSURE_METHOD_01 = "风险保障金";
	
	// -------------------------------------------注册------------------------------
	public static final String CUST_SOURCE_WAP = "wap";
	public static final String CUST_TYPE = "理财客户";
	
	// --------------------------------------头像上传----------------------------------
	public static final List<String> portraitExtList = Arrays.asList("jpg", "png");
	
	// ----------------------------------------------活动ID--------------------------------------------------------
	/** 活动-注册送体验金 ID*/
	public static final String ACTIVITY_ID_REGIST_01 = "1";
	/** 活动-推荐有奖 ID*/
	public static final String ACTIVITY_ID_REGIST_02 = "2";
	/** 活动-推荐送体验金 ID*/
	public static final String ACTIVITY_ID_REGIST_03 = "3";
	/** 活动-购买定期宝送体验金 ID*/
	public static final String ACTIVITY_ID_REGIST_04 = "4";
	/** 活动-购买定期宝送流量 ID*/
	public static final String ACTIVITY_ID_REGIST_05 = "5";
	/** 活动-注册送流量 ID*/
	public static final String ACTIVITY_ID_REGIST_06 = "6";
	/** 活动-购买定期宝推荐有奖送体验金ID*/
	public static final String ACTIVITY_ID_REGIST_07 = "7";
	/** 活动-投资返现活动*/
	public static final String ACTIVITY_ID_REGIST_08 = "8";
	/** 活动-绑卡送奖励*/
	public static final String ACTIVITY_ID_REGIST_09 = "9";
	/** 活动-集团活动*/
    public static final String ACTIVITY_ID_REGIST_10 = "10";
	/** 活动-五月活动*/
	public static final String ACTIVITY_ID_REGIST_11 = "11";
	/** 活动-六月加息活动*/
	public static final String ACTIVITY_ID_REGIST_12 = "12";
	/** 活动-2017-6月市场部活动 */
	public static final String ACTIVITY_ID_REGIST_13 = "13";
	/** 活动-市场部棋王争霸赛*/
	public static final String ACTIVITY_ID_REGIST_14 = "14";
	/**
	 * 红包加息体验金
	 */
	public static final String ACTIVITY_ID_REGIST_15 = "15";
	/** 活动-市场部大转盘抽奖活动*/
	public static final String ACTIVITY_ID_REGIST_16 = "16";
	/** 好友投资返现*/
	public static final String ACTIVITY_ID_REGIST_17 = "17";
	/** 投资排行拿豪礼*/
	public static final String ACTIVITY_ID_REGIST_18 = "18";

	public static final String ACTIVITY_ID_REGIST_14_LOGIN = "14_LOGIN";
	public static final String ACTIVITY_ID_REGIST_14_REGISTER = "14_REGISTER";
	
	// ------------------------------------------6月市场部活动的标记------------------------------------
	public static final String ACTIVITY_ID_13 = "activityCode13";
	// ------------------------------------------市场部棋王争霸赛------------------------------------
	public static final String ACTIVITY_ID_14 = "activityCode14";
	
	// ------------------------------------------密码等级------------------------------------
	public static final String PWD_LEVEL_LOW = "低";
	public static final String PWD_LEVEL_MIDDLE = "中";
	public static final String PWD_LEVEL_HIGH = "高";

	
	/** 体验宝体验天数：15天 */
	//public static final String EXPIRE_DAYS="15";
	
	//---------------------------------------------意见类型---------------------------------------------
	public static final String SUGGESTION_TYPE_FEEDBACK = "意见反馈";

	// ----------------------------------------------体验金交易状态--------------------------------------------------------
	/** 交易状态-已领取*/
	public static final String USER_ACTIVITY_TRADE_STATUS_01 = "已领取";
	/** 交易状态-部分使用*/
	public static final String USER_ACTIVITY_TRADE_STATUS_02 = "部分使用";
	/** 交易状态-全部使用*/
	public static final String USER_ACTIVITY_TRADE_STATUS_03 = "全部使用";
	/** 交易状态-已过期*/
	public static final String USER_ACTIVITY_TRADE_STATUS_04 = "已过期";
	
	// ----------------------------------------------推荐有奖交易状态--------------------------------------------------------
	/** 交易状态-已结算*/
	public static final String USER_ACTIVITY_TRADE_STATUS_05 = "已结算";
	/** 交易状态-未结算*/
	public static final String USER_ACTIVITY_TRADE_STATUS_06 = "未结算";
	/**
	 * 交易状态-已失效
	 */
	public static final String USER_ACTIVITY_TRADE_STATUS_07 = "已失效";


	// ----------------------------------------------支付方式--------------------------------------------------------
	/** 支付方式-认证支付*/
	public static final String PAY_TYPE_01 = "AUTH_PAY";
	/** 支付方式-网银支付*/
	public static final String PAY_TYPE_02 = "ONLINE_BANK_PAY";
	/** 支付方式-网银支付*/
	public static final String PAY_TYPE_03 = "COMPANY_BANK_PAY";
	// ----------------------------------------------推荐有奖奖励层级--------------------------------------------------------
	/** 推荐有奖奖励层级*/
	public static final int AWARD_SPREAD_LEVEL = 8;
	
	// ----------------------------------------------奖励形式--------------------------------------------------------
	/** 奖励形式-体验金*/
	public static final String REAWARD_SPREAD_01 = "体验金";
	/** 奖励形式-现金*/
	public static final String REAWARD_SPREAD_02 = "现金";
	/** 奖励形式-优惠劵*/
	public static final String REAWARD_SPREAD_03 = "优惠劵";
	/**
	 * 奖励形式-满减红包
	 */
	public static final String REAWARD_SPREAD_04 = "满减红包";
	/**
	 * 奖励类型-加息劵
	 */
	public static final String REAWARD_SPREAD_05 = "加息券";


	// ----------------------------------------------活动来源--------------------------------------------------------
	/** 活动来源-注册*/
	public static final String ACTIVITY_SOURCE_01 = "注册";
	/** 活动来源-投资*/
	public static final String ACTIVITY_SOURCE_02 = "投资";
	/** 活动来源-推荐送体验金*/
	public static final String ACTIVITY_SOURCE_03 = "推荐送体验金";
	/** 活动来源-定期宝送体验金*/
	public static final String ACTIVITY_SOURCE_04 = "定期宝送体验金";
	/** 活动来源-定期宝送现金*/
	public static final String ACTIVITY_SOURCE_05 = "定期宝送现金";
	/** 活动来源-市场部大转盘抽奖*/
	public static final String ACTIVITY_SOURCE_06 = "市场部大转盘活动";
	
	// ----------------------------------------------活动描述--------------------------------------------------------
	/** 活动描述-注册*/
	public static final String ACTIVITY_DESC_01 = "集团活动-注册";
	/** 活动描述-登陆*/
	public static final String ACTIVITY_DESC_02 = "集团活动-登录";
	/** 市场部棋王活动-注册*/
	public static final String ACTIVITY_DESC_03 = "市场部棋王活动-注册";
	/** 市场部棋王活动-注册-无邀请码*/
	public static final String ACTIVITY_DESC_03_01 = "市场部棋王活动-注册-无邀请码";
	/** 市场部棋王活动-注册-有邀请码*/
	public static final String ACTIVITY_DESC_03_02 = "市场部棋王活动-注册-有邀请码";
	/** 市场部棋王活动-登录*/
	public static final String ACTIVITY_DESC_04 = "市场部棋王活动-登录";
	/**
	 * 红包加息体验金-现金红包
	 */
	public static final String ACTIVITY_DESC_15_01 = "红包加息体验金-现金红包";
	
	/**
	 * 红包加息体验金-体验金
	 */
	public static final String ACTIVITY_DESC_15_02 = "红包加息体验金-体验金";
	
	/**
	 * 红包加息体验金-加息券
	 */
	public static final String ACTIVITY_DESC_15_03 = "红包加息体验金-加息券";
	
	/**
	 * 市场部大转盘抽奖-现金红包-注册
	 */
	public static final String ACTIVITY_DESC_16_01 = "市场部大转盘活动-现金红包-注册";
	/**
	 * 活动奖励记录ID-1元金额现金券红包
	 */
	public static final String ACTIVITY_AWARD_ID_15_1 = "1";
	/**
	 * 活动奖励记录ID-5元金额现金券红包
	 */
	public static final String ACTIVITY_AWARD_ID_15_5 = "2";
	/**
	 * 活动奖励记录ID-10元金额现金券红包
	 */
	public static final String ACTIVITY_AWARD_ID_15_10 = "3";
	/**
	 * 活动奖励记录ID-30元金额现金券红包
	 */
	public static final String ACTIVITY_AWARD_ID_15_30 = "4";
	/**
	 * 活动奖励记录ID-80元金额现金券红包
	 */
	public static final String ACTIVITY_AWARD_ID_15_80 = "5";
	/**
	 * 活动奖励记录ID-180元金额现金券红包
	 */
	public static final String ACTIVITY_AWARD_ID_15_180 = "6";
	/**
	 * 活动奖励记录ID-380元金额现金券红包
	 */
	public static final String ACTIVITY_AWARD_ID_15_380 = "7";
	
	/**
	 * 活动奖励记录ID-5000元体验金
	 */
	public static final String ACTIVITY_AWARD_ID_15_5000 = "20";
	
	/**
	 * 活动奖励记录ID-加息券2%
	 */
	public static final String ACTIVITY_AWARD_ID_15_201 = "21";
	
	/**
	 * 活动奖励记录ID-加息券1%
	 */
	public static final String ACTIVITY_AWARD_ID_15_202 = "22";
	
	/**
	 * 大转盘抽奖奖励记录ID-10元金额现金券红包
	 */
	public static final String ACTIVITY_AWARD_ID_16_10 = "11";
	/**
	 * 大转盘抽奖奖励记录ID-20元金额现金券红包
	 */
	public static final String ACTIVITY_AWARD_ID_16_20 = "12";
	/**
	 * 大转盘抽奖奖励记录ID-50元金额现金券红包
	 */
	public static final String ACTIVITY_AWARD_ID_16_50 = "13";
	/**
	 * 大转盘抽奖奖励记录ID-100元金额现金券红包
	 */
	public static final String ACTIVITY_AWARD_ID_16_100 = "14";
	/**
	 * 大转盘抽奖奖励记录ID-200元金额现金券红包
	 */
	public static final String ACTIVITY_AWARD_ID_16_200= "15";
	/**
	 * 大转盘抽奖奖励记录ID-500元金额现金券红包
	 */
	public static final String ACTIVITY_AWARD_ID_16_500 = "16";
	/**
	 * 奖励使用范围-新手专区
	 */
	public static final String 	ACTIVITY_AWARD_USE_SCOPE_0 = "新手专区";
	/**
	 * 奖励使用范围-优选项目
	 */
	public static final String ACTIVITY_AWARD_USE_SCOPE_1 = "优选列表";
	/**
	 * 奖励使用范围-转让专区
	 */
	public static final String ACTIVITY_AWARD_USE_SCOPE_2 = "转让专区";


	//-----------------------------------------------还款方式---------------------------------------------------------
	/** 还款方式-等额本息*/
	public static final String REPAYMENT_METHOD_01="等额本息";
	/** 还款方式-每期还息到期付本*/
	public static final String REPAYMENT_METHOD_02="先息后本";
	/** 还款方式-到期一次性还本付息*/
	public static final String REPAYMENT_METHOD_03="到期还本付息";
	/** 还款方式-到期还本付息(按季)*/
	public static final String REPAYMENT_METHOD_04="按季付息，到期还本";
	/** 还款方式-到期还本付息(按月)*/
	public static final String REPAYMENT_METHOD_05="按月付息，到期还本";
	/** 还款方式-每期还息到期付本*/
	public static final String REPAYMENT_METHOD_06="每期还息到期付本";
	
	public static final String REPAYMENT_METHOD = "到期还本付息,等额本息,每期还息到期付本";
	
	//-----------------------------------------------还款方式---------------------------------------------------------
	/**银行卡是否默认**/
	public static final String BANKCARD_DEFAULT="1";
	//-----------------------------------------------调帐类型---------------------------------------------------------

	public static final String TANS_ACCOUNT_TYPE_03="调账";
	public static final String TANS_ACCOUNT_TYPE_01="投资奖励";
	public static final String TANS_ACCOUNT_TYPE_02="推荐奖励";
	public static final String TANS_ACCOUNT_TYPE_04="推广佣金";
	public static final String TANS_ACCOUNT_TYPE_05="推广奖励";

	//-----------------------------------------------通道类型---------------------------------------------------------
	public static final String CHANNEL_NO_DIANZAN = "2015070100000001";
	public static final String CHANNEL_NO_SHANLIN = "善林财富网站";
	
	//-----------------------------------------------通道类型---------------------------------------------------------
	public static final String THIRD_PARTY_TYPE_DIANZAN = "点赞网";
	public static final String THIRD_PARTY_TYPE_SHANLIN = "善林财富";
	public static final String THIRD_PARTY_TYPE_JUPENG = "巨宝朋";
	public static final String THIRD_PARTY_TYPE_JIRONGTONG = "吉融通";
	public static final String THIRD_PARTY_TYPE_ZHANGSHANG = "掌上互动";
	
	//-----------------------------------------------银行卡解约类型---------------------------------------------------------
	/**银行卡解约类型-已丢失**/
	public static final String UNBIND_CARD_TYPE_LOST = "已丢失";
	/**银行卡解约类型-未丢失**/
	public static final String UNBIND_CARD_TYPE_UNLOST = "未丢失";
	/**银行卡解约附件类型-手持银行卡照片**/
	public static final String UNBIND_CARD_ATTACHMENT_TYPE_BANK_CARD = "手持银行卡照片";
	/**银行卡解约附件类型-银行挂失证明**/
	public static final String UNBIND_CARD_ATTACHMENT_TYPE_BANK_LOST = "银行挂失证明";
	/**银行卡解约附件类型-手持身份证正面**/
	public static final String UNBIND_CARD_ATTACHMENT_TYPE_PAPER_BEFORE = "手持身份证正面";
	/**银行卡解约附件类型-手持身份证反面**/
	public static final String UNBIND_CARD_ATTACHMENT_TYPE_PAPER_BEHIND = "手持身份证反面";
	/**银行卡解约附件类型-BANNER图片**/
	public static final String BANNER_ATTACHMENT_TYPE_INDEX = "BANNER图片";
	/**银行卡解约附件文件类型-WORD**/
	public static final String UNBIND_CARD_ATTACHMENT_DOC_TYPE_WORD = "WORD";
	/**银行卡解约附件文件类型-EXCEL**/
	public static final String UNBIND_CARD_ATTACHMENT_DOC_TYPE_EXCEL = "EXCEL";
	/**银行卡解约附件文件类型-PIG**/
	public static final String UNBIND_CARD_ATTACHMENT_DOC_TYPE_PIG = "PIG";
	
	/**债权最小年化利率**/
	public static final String LOAN_MIN_YEAR_RATE="0.07";
	/**债权最大年化利率**/
	public static final String LOAN_MAX_YEAR_RATE="0.3";
	
	//-----------------------------------------------邮件服务器参数定义---------------------------------------------------------
	/**邮件发送平台名称**/
	public static final String FORM_PLAT_NAME = "善林财富";
	/**邮件发送平台服务邮件地址**/
	public static final String PLAT_EMAIL_ADDRESS = "noreply@shanlinjinrong.com";
	
	// ----------------------------------------------定期宝类型---------------------------------------------------
	public final static String BAO_FIXEDINVESTMENT_TYPE_CY = "持有中";
	public final static String BAO_FIXEDINVESTMENT_TYPE_SH = "赎回中";
	public final static String BAO_FIXEDINVESTMENT_TYPE_TC = "已退出";
	
	// ----------------------------------------------定期宝投资状态---------------------------------------------------
	/**定期宝投资状态--收益中**/
	public final static String TERM_INVEST_STATUS_EARN = "收益中";
	/**定期宝投资状态--到期处理中**/
	public final static String TERM_INVEST_STATUS_WAIT = "到期处理中";
	/**定期宝投资状态--已到期**/
	public final static String TERM_INVEST_STATUS_FINISH = "已到期";
	/**定期宝投资状态--提前赎回中**/
	public final static String TERM_INVEST_STATUS_ADVANCE = "提前赎回中";
	/**定期宝投资状态--提前赎回**/
	public final static String TERM_INVEST_STATUS_ADVANCE_FINISH = "提前赎回";
	
	/**金牌推荐人奖励利率科目--金牌推荐人佣金与奖励**/	
	public final static String PRODUCT_ID_GOLD = "金牌推荐人佣金与奖励";
	/**金牌推荐人奖励利率科目--金牌推荐人活期宝奖励**/	
	public final static String PRODUCT_ID_GOLD_01 = "金牌推荐人活期宝奖励";

	//-----------------金牌推荐人申请条件常量-----------------------------------------------------------------------------------------------------------------------------------------------
	/**定期宝在投金额最小金额**/
	public final static BigDecimal INEST_AMOUNT_MIN = new BigDecimal("1000");
	/**推荐人数**/
	public final static int RECOMMEND_COUNT = 5;
	
	//-----------------金牌推荐人申请状态-----------------------------------------------------------------------------------------------------------------------------------------------
	/**申请状态-没有申请**/
	public final static String UNAPPLY_RECOMMEND = "没有申请";
	/**申请状态-申请审核中**/    
	public final static String APPLYING_RECOMMEND = "申请审核中";
	/**申请状态-已申请**/
	public final static String APPLYED_RECOMMEND = "已申请";
	/**申请状态-申请拒绝**/
	public final static String APPLYED_RECOMMEND_REFUSE = "申请拒绝";
	
	// ---------------金牌推荐人申请申核状态------------------------------------------------------------------------------------------------------------------------------------------
	/** 金牌推荐人申请申核状态--审核中 */
	public static final String AUDIT_STATUS_REVIEWD_RECOMMEND = "审核中";
	/** 金牌推荐人申请申核状态--拒绝 */
	public static final String AUDIT_STATUS_REfUSE_RECOMMEND = "拒绝";
	/** 金牌推荐人申请申核状态--通过 */
	public static final String AUDIT_STATUS_PASS_RECOMMEND = "通过";
	/** 金牌推荐人申请申核状态--解除 */
	public static final String AUDIT_STATUS_PASS_RELIEVE = "解除";
	
	// ---------------金牌推荐人状态------------------------------------------------------------------------------------------------------------------------------------------
	/** 是否金牌推荐人状态--是 */
	public static final String IS_RECOMMEND_YES = "是";
	/** 是否金牌推荐人状态--否 */
	public static final String IS_RECOMMEND_NO = "否";
	
	// ---------------请求来源类型------------------------------------------------------------------------------------------------------------------------------------------
	public static final String[] APP_SOURCES = { "web", "wap", "android", "ios", "winphone", "others" };

	// ---------------金牌推荐人来源类型------------------------------------------------------------------------------------------------------------------------------------------
	/** 金牌推荐人来源类型--注册 */
	public static final String CUST_RECOMMEND_SOURCE_REGISTER = "注册";
	/** 金牌推荐人来源类型--变更 */
	public static final String CUST_RECOMMEND_SOURCE_MODIFY = "变更";
	
	// ---------------金牌推荐人业务员类型------------------------------------------------------------------------------------------------------------------------------------------
	/** 金牌推荐人业务员类型--内部员工 */
	public static final String CUST_APPLY_SALESMAN_TYPE_INTERNAL = "内部员工";
	/** 金牌推荐人业务员类型--外部员工 */
	public static final String CUST_APPLY_SALESMAN_TYPE_EXTERNAL = "外部员工";
	
	// ---------------设备来源类型------------------------------------------------------------------------------------------------------------------------------------------
	/** 设备来源类型--ios */
	public static final String APP_SOURCE_IOS = "ios";
	/** 设备来源类型--android */
	public static final String APP_SOURCE_ANDROID = "android";
	/** 设备来源类型--web/pc */
	public static final String APP_SOURCE_WEB = "web";
	/** 设备来源类型--wap */
	public static final String APP_SOURCE_WAP = "wap";
	/** 设备来源类型--winphone */
	public static final String APP_SOURCE_WINPHONE = "winphone";
	/** 设备来源类型--others */
	public static final String APP_SOURCE_OHTERS = "others";
	
	// ---------------Banner类型------------------------------------------------------------------------------------------------------------------------------------------
	/** Banner类型--内部 */
	public static final String BANNER_TYPE_INTERNAL = "内部";
	/** Banner类型--外部 */
	public static final String BANNER_TYPE_EXTERNAL = "外部";
	/** Banner类型--推荐有奖 */
	public static final String BANNER_TYPE_AWARD = "APP推荐有奖分享页";
	
	// ---------------Banner业务类型------------------------------------------------------------------------------------------------------------------------------------------
	/** Banner类型--首页 */
	public static final String BANNER_TRADE_TYPE_INDEX = "首页";
	/** Banner类型--引导页 */
	public static final String BANNER_TRADE_TYPE_GUIDE = "引导页";
	/** Banner类型--启动页 */
//	public static final String BANNER_TRADE_TYPE_START = "启动页";
	/** Banner类型--加载页 */
	public static final String BANNER_TRADE_TYPE_START = "加载页";
	/** Banner类型--分享页 */
	public static final String BANNER_TRADE_TYPE_SHARE = "分享页";
	/** Banner类型--活动地址 */
	public static final String BANNER_TRADE_TYPE_ACTIVITY_URL = "活动地址";
	
	
	// ---------------APP版本号类型------------------------------------------------------------------------------------------------------------------------------------------
	/** iOS版本号类型*/
	public static final String APP_VERSION_NO_IOS = "1.2";// 小于等于APP_VERSION_NO_IOS都需要更新
	/** android版本号类型*/
	public static final String APP_VERSION_NO_ANDROID = "1.1"; // 小于等于APP_VERSION_NO_ANDROID都需要更新
	
	// ---------------AMQP 队列名字------------------------------------------------------------------------------------------------------------------------------------------
	/** 队列名字-openservice*/
	public static final String AMQP_QUEUE_OPENSERVICE = "openservice";
	
	// ---------------项目状态-----------------------------------------------------------------------------------------------------------------------------------------------
	/** 项目状态--暂存*/
	public static final String PROJECT_STATUS_01 = "暂存";
	/** 项目状态--待审核*/
	public static final String PROJECT_STATUS_02 = "待审核";
	/** 项目状态--审核回退*/
	public static final String PROJECT_STATUS_03 = "审核回退";
	/** 项目状态--拒绝*/
	public static final String PROJECT_STATUS_04 = "拒绝";
	/** 项目状态--待发布*/
	public static final String PROJECT_STATUS_05 = "待发布";
	/** 项目状态--发布中*/
	public static final String PROJECT_STATUS_06 = "发布中";
	/** 项目状态--满标复核*/
	public static final String PROJECT_STATUS_07 = "满标复核";
	/** 项目状态--还款中*/
	public static final String PROJECT_STATUS_08 = "还款中";
	/** 项目状态--已到期*/
	public static final String PROJECT_STATUS_09 = "已到期";
	/** 项目状态--已逾期*/
	public static final String PROJECT_STATUS_10 = "已逾期";
	/** 项目状态--提前结清*/
	public static final String PROJECT_STATUS_11 = "提前结清";
	/** 项目状态--流标*/
	public static final String PROJECT_STATUS_12 = "流标";
	/** 项目状态--募集中*/
	public static final String PROJECT_STATUS_13 = "募集中";
	
	// ---------------财务是否还款------------------------------------------------------------------------------------------------------------------------------------------
	/** 财务是否还款--是 */
	public static final String IS_AMOUNT_FROZEN_YES = "是";
	/** 财务是否还款--否 */
	public static final String IS_AMOUNT_FROZEN_NO = "否";
	
	// ---------------风险金是否垫付------------------------------------------------------------------------------------------------------------------------------------------
	/** 风险金是否垫付--是 */
	public static final String IS_RISKAMOUNT_REPAY_YES = "是";
	/** 风险金是否垫付--否 */
	public static final String IS_RISKAMOUNT_REPAY_NO = "否";
	
	// ---------------审核类型------------------------------------------------------------------------------------------------------------------------------------------
	/** 审核类型--满标复核 */
	public static final String APPLY_TYPE_07 = "满标复核";
	/** 审核类型--发布前审核 */
	public static final String APPLY_TYPE_08 = "发布前审核";
	
	//----------------调账资金转向-------------------------------------------------------------------------------------------------------------------------------------------
	/** 调账资金转向--转入*/
	public static final String ACCOUNT_TRANS_DIRECTION_IN = "转入";
	/** 调账资金转向--转出*/
	public static final String ACCOUNT_TRANS_DIRECTION_OUT = "转出";
	
	// ---------------还款类型------------------------------------------------------------------------------------------------------------------------------------------
	/** 还款类型--正常还款 */
	public static final String REPAYMENT_TYPE_01 = "正常还款";
	/** 还款类型--逾期还款 */
	public static final String REPAYMENT_TYPE_02 = "逾期还款";
	/** 还款类型--提前还款 */
	public static final String REPAYMENT_TYPE_03 = "提前还款";
	/** 还款类型--风险金垫付 */
	public static final String REPAYMENT_TYPE_04 = "风险金垫付";
	/** 还款类型--加息券奖励金发放*/
	public static final String REPAYMENT_TYPE_05 = "加息券奖励金发放";
	// ---------------客户类型------------------------------------------------------------------------------------------------------------------------------------------
	/** 客户类型--网站用户 */
	public static final String CUST_KIND_01 = "网站用户";
	/** 客户类型--融资租赁 */
	public static final String CUST_KIND_02 = "融资租赁";
	/** 客户类型--私募基金 */
	public static final String CUST_KIND_03 = "私募基金";
	
	// ---------------附件类型-------------------------------------------------------------------------------------------------------------------------------------------
	/** 附件类型--项目资料*/
	public static final String ATTACHMENT_TYPE_01 = "项目资料";
	
	/** 附件类型--线下充值材料*/
	public static final String ATTACHMENT_TYPE_02 = "Pos单据";
	
	/** 附件类型--附属银行卡材料*/
	public static final String ATTACHMENT_TYPE_03 = "附属银行卡材料";
	
	/** 附件类型--附属银行卡材料*/
	public static final List<String> ATTACHMENT_TYPE_BANK_LIST = Arrays.asList("银行卡", "手持身份证正面", "手持身份证反面");
	
	// ---------------项目是否可赎回---------------------------------------------------------------------------------------------------------------------------------------
	/**项目是否可赎回--不可赎回*/
	public static final String IS_ATONE_NO = "不可赎回";
	
	/**项目是否可赎回--不可赎回*/
	public static final String IS_ATONE_YES = "可以赎回";
	
	// --------------审核日志类型---------------------------------------------------------------------------------------------------------------------------------------------
	/** 日志类型--线下充值申请*/
	public static final String LOG_TYPE_OFFLINE_RECHARGE_APPLY = "线下充值申请";
	
	/** 日志类型--线下充值审核*/
	public static final String LOG_TYPE_OFFLINE_RECHARGE_AUDIT = "线下充值审核";
	
	/** 日志类型--线下提现申请*/
	public static final String LOG_TYPE_OFFLINE_WITHDRAW_APPLY = "线下提现申请";
	
	/** 日志类型--线下提现审核*/
	public static final String LOG_TYPE_OFFLINE_WITHDRAW_AUDIT = "线下提现审核";
	
	//--------------线下提现状态--------------------------------------------------------------------------------------------------------------------------------------------
	/** 线下提现状态--待处理*/
	public static final String OFFLINE_WITHDRAW_STATUS_UNPROCESS = "未处理";
	
	/** 线下提现状态--处理中*/
	public static final String OFFLINE_WITHDRAW_STATUS_PROCESSING = "处理中";
	
	/** 线下提现状态--处理成功*/
	public static final String OFFLINE_WITHDRAW_STATUS_SUCCESS = "处理成功";
	
	/** 线下提现状态--处理失败*/
	public static final String OFFLINE_WITHDRAW_STATUS_FAIL = "处理失败";
	
	//--------------线下充值审核状态----------------------------------------------------------------------------------------------------------------------------------------
	/** 线下充值审核状态--待审核*/
	public static final String OFFLINE_RECHARGE_AUDIT_STATUS_01 = "待审核";
	
	/** 线下充值审核状态--初审回退*/
	public static final String OFFLINE_RECHARGE_AUDIT_STATUS_02 = "初审回退";
	
	/** 线下充值审核状态--终审回退*/
	public static final String OFFLINE_RECHARGE_AUDIT_STATUS_03 = "终审回退";
	
	/** 线下充值审核状态--初审通过*/
	public static final String OFFLINE_RECHARGE_AUDIT_STATUS_04 = "初审通过";
	
	/** 线下充值审核状态--终审通过*/
	public static final String OFFLINE_RECHARGE_AUDIT_STATUS_05 = "终审通过";
	
	/** 线下充值审核状态--初审拒绝*/
	public static final String OFFLINE_RECHARGE_AUDIT_STATUS_06 = "初审拒绝";
	
	/** 线下充值审核状态--终审拒绝*/
	public static final String OFFLINE_RECHARGE_AUDIT_STATUS_07 = "终审拒绝";
	
	// @2016/4/5 add by liyy Start 
	/** 线下充值审核状态--复审回退 */
	public static final String OFFLINE_RECHARGE_AUDIT_STATUS_08 = "复审回退";
	
	/** 线下充值审核状态--复审通过 */
	public static final String OFFLINE_RECHARGE_AUDIT_STATUS_09 = "复审通过";
	
	/** 线下充值审核状态--复审拒绝 */
	public static final String OFFLINE_RECHARGE_AUDIT_STATUS_10 = "复审拒绝";
	// @2016/4/5 add by liyy End 
	
	//--------------线下充值状态--------------------------------------------------------------------------------------------------------------------------------------------
	/** 线下充值状态--待处理*/
	public static final String OFFLINE_RECHARGE_STATUS_UNPROCESS = "未处理";
	
	/** 线下充值状态--处理中*/
	public static final String OFFLINE_RECHARGE_STATUS_PROCESSING = "处理中";
	
	/** 线下充值状态--处理失败*/
	public static final String OFFLINE_RECHARGE_STATUS_FAIL = "处理失败";
	
	/** 线下充值状态--处理成功*/
	public static final String OFFLINE_RECHARGE_STATUS_SUCCESS = "处理成功";
	
	
	// ---------------优选计划-------------------------------------------------------------------------------------------------------------------------------------------
	
	/** 状态 -- 启用*/
	public static final String ENABLE_STATUS_QY = "启用";
	/** 状态 -- 停用*/
	public static final String ENABLE_STATUS_TY = "停用";
	
	/** 赎回审核状态 -- 通过 */
	public static final String ATONE_AUDIT_STATUS_PASS = "通过";
	
	/**优选计划审核状态 */
	
	/**优选计划审核状态 --待审核*/
	public static final String WEALTH_AUDIT_STATUS_UNREVIEW = "待审核";
	/**优选计划审核状态 --通过*/
	public static final String WEALTH_AUDIT_STATUS_PASS = "通过";
	/**优选计划审核状态 --拒绝*/
	public static final String WEALTH_AUDIT_STATUS_REfUSE = "拒绝";
	/**优选计划审核状态 --审核回退*/
	public static final String WEALTH_AUDIT_STATUS_FALLBACK ="审核回退";
	
	/** 优选计划投资状态--投资中 */
	public static final String INVEST_STATUS_INVESTING = "投资中";
	/** 优选计划投资状态--收益中**/
	public final static String INVEST_STATUS_EARN = "收益中";
	/** 优选计划投资状态--提前赎回中**/
	public final static String INVEST_STATUS_ADVANCE = "提前赎回中";
	/** 优选计划投资状态--提前赎回**/
	public final static String INVEST_STATUS_ADVANCE_FINISH = "提前赎回";
	/** 优选计划投资状态--到期赎回中**/
	public final static String INVEST_STATUS_WAIT = "到期赎回中";
	/** 优选计划投资状态--到期赎回**/
	public final static String INVEST_STATUS_FINISH = "到期赎回";
	/** 优选计划投资状态--已到期**/
	public final static String INVEST_STATUS_END = "已到期";
	/** 优选计划投资状态--流标**/
	public final static String INVEST_STATUS_UNRELEASE = "流标";
	/** 优选计划投资状态--提前结清**/
	public final static String INVEST_STATUS_CLEAN = "提前结清";
	/** 优选计划投资状态--已转让**/
	public final static String INVEST_STATUS_TRANSFER = "已转让";
	
	// ---------------优选计划结算方式---------------------------------------------------------------------------------------------------------------------------------------
	/**优选计划结算方式--到期结算本息*/
	public static final String WEALTH_INCOME_TYPE_01 = "到期结算本息";
	/**优选计划结算方式--按月返息，到期返本*/
	public static final String WEALTH_INCOME_TYPE_02 = "按月返息，到期返本";
	
	// ---------------计划状态-----------------------------------------------------------------------------------------------------------------------------------------------
	/** 计划状态--待审核*/
	public static final String WEALTH_STATUS_01 = "待审核";
	/** 计划状态--审核回退*/
	public static final String WEALTH_STATUS_02 = "审核回退";
	/** 计划状态--拒绝*/
	public static final String WEALTH_STATUS_03 = "拒绝";
	/** 计划状态--待发布*/
	public static final String WEALTH_STATUS_04 = "待发布";
	/** 计划状态--发布中*/
	public static final String WEALTH_STATUS_05 = "发布中";
	/** 计划状态--已满额*/
	public static final String WEALTH_STATUS_06 = "已满额";
	/** 计划状态--收益中*/
	public static final String WEALTH_STATUS_07 = "收益中";
	/** 计划状态--到期处理中*/
	public static final String WEALTH_STATUS_08 = "到期处理中";
	/** 计划状态--已到期*/
	public static final String WEALTH_STATUS_09 = "已到期";
	/** 计划状态--流标*/
	public static final String WEALTH_STATUS_10 = "流标";

	// ---------------债权持有状态-----------------------------------------------------------------------------------------------------------------------------------------------
	/** 计划状态--持有中*/
	public final static String HOLD_STATUS_01 = "持有中";
	/** 计划状态--待转让*/
	public final static String HOLD_STATUS_02 = "待转让";
	/** 计划状态--转让中*/
	public final static String HOLD_STATUS_03 = "转让中";
	/** 计划状态--已转让*/
	public final static String HOLD_STATUS_04 = "已转让";
	/** 计划状态--已结清*/
	public final static String HOLD_STATUS_05 = "已结清";
	
	// ---------------------------------------------是否是居间人---------------------------------------------------------
	/** 客户状态--是否是居间人-是 **/
	public static final String IS_CENTER_01 = "是";
	/** 客户状态--是否是居间人-否 **/
	public static final String IS_CENTER_02 = "否";

	// ---------------------------------------------------附属银行卡审核状态---------------------------------------------------
	/** 附属银行卡审核状态--通过 */
	public static final String BANK_AUDIT_STATUS_PASS = "通过";
	/** 附属银行卡审核状态--审核回退 */
	public static final String BANK_AUDIT_STATUS_FALLBACK = "审核回退";
	/** 附属银行卡审核状态--拒绝 */
	public static final String BANK_AUDIT_STATUS_REFUSE = "拒绝";
	/** 附属银行卡审核状态--待审核*/
	public static final String BANK_AUDIT_STATUS_UNREVIEW = "待审核";
	/** 附属银行卡审核状态--审核中 */
	public static final String BANK_AUDIT_STATUS_REVIEWD = "审核中";
	/** 附属银行卡审核状态--作废 */
	public static final String BANK_AUDIT_STATUS_INVALID = "作废";
	// ---------------------------------------------------银行卡默认---------------------------------------------------
	/** 银行卡默认状态--是 */
	public static final String BANK_IS_DEFAULT_YES = "1";
	/** 银行卡默认状态--否 */
	public static final String BANK_IS_DEFAULT_NO = "0";
		
	// ---------------------------------------------------客户变更审核状态---------------------------------------------------
	/** 客户变更审核状态--通过 */
	public static final String CUST_MANAGER_AUDIT_STATUS_PASS = "通过";
	/** 客户变更审核状态--审核回退 */
	public static final String CUST_MANAGER_AUDIT_STATUS_FALLBACK = "审核回退";
	/** 客户变更审核状态--拒绝 */
	public static final String CUST_MANAGER_AUDIT_STATUS_REFUSE = "拒绝";
	/** 客户变更审核状态--待审核*/
	public static final String CUST_MANAGER_AUDIT_STATUS_UNREVIEW = "待审核";
	// ---------------------------------------------------客户变更审核状态---------------------------------------------------
	/** 客户变更申请表申请状态--审核中 */
	public static final String CUST_MANAGER_APPLY_STATUS_REVIEWD = "审核中";
	
	//--------------线下提现批量导入处理状态----------------------------------------------------------------------------------
	/** 线下提现批量导入处理状态--成功*/
	public static final String OFFLINE_WITHDRAW_SUCCESS = "成功";
	/** 线下提现批量导入处理状态--失败*/
	public static final String OFFLINE_WITHDRAW_FAIL = "失败";
	
	//--------------线下提现批量导入系统处理状态----------------------------------------------------------------------------------
	/** 线下提现批量导入系统处理状态--成功*/
	public static final String OFFLINE_WITHDRAW_MANAGER_SUCCESS = "成功";
	/** 线下提现批量导入系统处理状态--失败*/
	public static final String OFFLINE_WITHDRAW_MANAGER_FAIL = "失败";
	
	//--------------计划返款----------------------------------------------------------------------------------
	/** 计划反息--反息状态 --已回收*/
	public static final String WEALTH_PAYMENT_PLAN_STATUS_ALREADY = "已回收";
	/** 计划反息--反息状态 --未回收*/
	public static final String WEALTH_PAYMENT_PLAN_STATUS_NOT = "未回收";
	
	//-------------线下充值终端处理状态-----------------------------------------------------------------------------------------
	/** 线下充值终端处理状态--未结算*/
	public static final String OFFLINE_RECHARGE_POS_STATUS_UNPROCCESS = "未结算";
	
	/** 线下充值终端处理状态--结算成功*/
	public static final String OFFLINE_RECHARGE_POS_STATUS_SUCCESS = "结算成功";
	
	/** 线下充值终端处理状态--结算失败*/
	public static final String OFFLINE_RECHARGE_POS_STATUS_FAIL = "结算失败";
	
	// -------------银行卡类型----------------------------------------------------------------------------------------------------
	/** 银行卡类型--线上*/
	public static final String BANK_FLAG_ONLINE = "线上";
	
	/** 银行卡类型--线下*/
	public static final String BANK_FLAG_OFFLINE = "线下";
	
	// -------------导出协议文件类型----------------------------------------------------------------------------------------------------
	/** 导出协议文件类型--优选计划协议*/
	public static final String CONTRACT_TYPE_WEALTH = "优选计划协议";
	/** 导出协议文件类型--优选计划债权协议*/
	public static final String CONTRACT_TYPE_LOAN = "优选计划债权协议";
	/** 导出协议文件类型--企业借款协议*/
	public static final String CONTRACT_TYPE_PROJECT = "企业借款协议";
	/** 导出协议文件类型--优选项目协议*/
	public static final String CONTRACT_TYPE_STAND = "优选项目协议";
	/** 导出协议文件类型--债权转让协议*/
	public static final String CONTRACT_TYPE_TRANSFER = "债权转让协议";
	/** 导出协议文件类型--债权转让及回购协议*/
	public static final String CONTRACT_TYPE_TRANSFER_ATONE = "债权转让及回购协议";
	/** 导出协议文件类型--应收账款转让协议*/
	public static final String CONTRACT_TYPE_FINANCING = "应收账款转让协议";
	
	//--------------理财计划网站-----------------------------------------------------------------------------------------------------
	/** 善林财富网址 */
	public static final String SHANLINCAIFU_URL = "shanlincaifu.com";
	
	//---------------------报表----------------------------------------------------------------------------------------------------
	public static final String EMPLOYEE_LOAD_STATUS_01 = "未处理";
	
	/** 客户信息--线下理财标识--未处理*/
	public static final String WEALTH_FLAG_00 = "00";
	/** 客户信息--线下理财标识--已处理*/
	public static final String WEALTH_FLAG_01 = "01";
	
	/** waitingAuditDate */
	public static final String WAITING_AUDIT_DATE = "waitingAuditDate";
	/** waitingAuditDate */
	public static final String MONITOR_DATE = "monitorDate";
	
	//---------------------应用名称----------------------------------------------------------------------------------------------------
	/** 应用名称--善林财富 */
	public static final String APP_NAME_WEALTH = "善林财富";
	/** 应用名称--善林大师 */
	public static final String APP_NAME_SALES = "善林大师";
	
	//---------------------职位状态----------------------------------------------------------------------------------------------------
	/** 职位状态--在职 */
	public static final String WORKING_STATE_IN = "在职";
	/** 职位状态--离职 */
	public static final String WORKING_STATE_OUT = "离职";

	
	//---------------------借款状态----------------------------------------------------------------------------------------------------
	/** 借款状态-待审核 */
	public static final String LOAN_STATUS_01 = "待审核";
	/** 借款状态-审核回退 */
	public static final String LOAN_STATUS_02 = "审核回退";
	/** 借款状态-通过 */
	public static final String LOAN_STATUS_03 = "通过";
	/** 借款状态-待发布 */
	public static final String LOAN_STATUS_04 = "待发布";
	/** 借款状态-募集中 */
	public static final String LOAN_STATUS_05 = "募集中";
	/** 借款状态-满标复核 */
	public static final String LOAN_STATUS_06 = "满标复核";
	/** 借款状态-复核通过 */
	public static final String LOAN_STATUS_07 = "复核通过";
	/** 借款状态-正常 */
	public static final String LOAN_STATUS_08 = "正常";
	/** 借款状态-逾期 */
	public static final String LOAN_STATUS_09 = "逾期";
	/** 借款状态-提前结清 */
	public static final String LOAN_STATUS_10 = "提前结清";
	/** 借款状态-已到期 */
	public static final String LOAN_STATUS_11 = "已到期";
	/** 借款状态-流标 */
	public static final String LOAN_STATUS_12 = "流标";
	/** 借款状态-复核拒绝 */
	public static final String LOAN_STATUS_13 = "复核拒绝";
	/** 借款状态-拒绝 */
	public static final String LOAN_STATUS_14 = "拒绝";
	
	// ---------------通知商务---------------------------------------------------------------------------------------------------------
	/** 通知商务公司名称--善林商务*/
	public static final String NOTIFY_LOAN_COMPANY_01 = "善信融资";
	
	/** 通知类型--通知项目状态*/
	public static final String NOTIFY_TYPE_LOAN_STATUS = "通知项目状态";
	
	/** 通知类型--保存优选项目（商务通知我们）*/
	public static final String NOTIFY_LOAN_LOAN_PROJECT = "保存优选项目";
	
	/***接口类型 ---  还款计划变更***/
	public static final String REPAYMENT_CHANGE = "还款计划变更";
	
	/** 通知类型--流标（商务通知我们）*/
	public static final String NOTIFY_TYPE_LOAN_BIDDERS = "项目流标通知";
	
	/** 通知类型--放款（商务通知我们）*/
	public static final String NOTIFY_TYPE_LOAN_LENDING = "项目放款通知";
	
    /** 通知类型--撤销（商务通知我们）*/
	public static final String NOTIFY_TYPE_LOAN_CANCEL = "项目撤销通知";
		
	/** 通知类型--绑卡（商务通知我们）*/
	public static final String NOTIFY_TYPE_LOAN_BIND_CARD = "绑定银行卡";
	
	/** 通知类型--解绑银行卡（商务通知我们）*/
	public static final String NOTIFY_TYPE_LOAN_UNBIND_CARD = "解绑银行卡";
	
	/** 通知类型--实名认证查询（商务通知我们）*/
	public static final String NOTIFY_TYPE_LOAN_QUERY_REAL_NAME = "实名认证查询";
	
	/** 通知类型--还款通知*/
	public static final String NOTIFY_TYPE_LOAN_REPAYMENT = "还款通知";
	
	/** 通知类型--查询协议*/
	public static final String NOTIFY_TYPE_LOAN_PROTOCAL = "查询协议";
	
	/** 通知类型--查询预约金额*/
	public static final String NOTIFY_TYPE_LOAN_AMOUNT = "查询预约金额";
	
	// ---------------通知还款类型------------------------------------------------------------------------------------------------------------------------------------------
	/** 通知还款类型--正常还款 */
	public static final String NOTIFY_TYPE_REPAYMENT_TYPE_01 = "正常还款";
	/** 通知还款类型--逾期还款 */
	public static final String NOTIFY_TYPE_REPAYMENT_TYPE_02 = "逾期还款";
	/** 通知还款类型--提前还款 */
	public static final String NOTIFY_TYPE_REPAYMENT_TYPE_03 = "一次性结清";
	
	// ---------------通知编码---------------------------------------------------------------------------------------------------------
	/** 通知编码--通知成功*/
	public static final String NOTIFY_TYPE_CODE_SUCCESS = "000000";
	
	// ---------------通知交易类型---------------------------------------------------------------------------------------------------------
	/** 通知交易类型--绑卡*/
	public static final String NOTIFY_TYPE_TRADE_TYPE_BIND_CARD = "绑卡";
	
	/** 通知交易类型--解绑*/
	public static final String NOTIFY_TYPE_TRADE_TYPE_UNBIND_CARD = "解绑";
	
	/** 通知交易类型--流标*/
	public static final String NOTIFY_TYPE_TRADE_TYPE_BIDDERS = "流标";
	
	/** 通知交易类型--放款*/
	public static final String NOTIFY_TYPE_TRADE_TYPE_LENDING = "放款";
	
	/** 通知交易类型--撤销*/
    public static final String NOTIFY_TYPE_TRADE_TYPE_CANCEL = "撤销";
	
	// ---------------放款状态---------------------------------------------------------------------------------------------------------
	/** 放款状态--待放款*/
	public static final String GRANT_STATUS_01 = "待放款";
	/** 放款状态--放款成功*/
	public static final String GRANT_STATUS_02 = "放款成功";
	/** 放款状态--放款失败*/
	public static final String GRANT_STATUS_03 = "放款失败";
	/** 放款状态--放款中*/
	public static final String GRANT_STATUS_04 = "放款中";
	
	// ----------------------------------------------业务员佣金表-结算状态--------------------------------------------------------
	/** 业务员佣金表-结算状态-已结算*/
	public static final String PAYMENT_STATUS_01 = "已结算";
	/** 业务员佣金表-结算状态-未结算*/
	public static final String PAYMENT_STATUS_02 = "未结算";
	/** 业务员佣金表-结算状态-已废弃*/
	public static final String PAYMENT_STATUS_03 = "已废弃";
	
	// -----------------------------------------------账户类型-------------------------------------------------------
	/**账户类型--个人*/
	public static final String ACCOUNT_TYPE_01 = "个人";
	/**账户类型--公司*/
	public static final String ACCOUNT_TYPE_02 = "公司";
	
	// -----------------------------------------------标记类型-------------------------------------------------------
	/**借款标记类型--商务*/
	public static final String LOAN_FLAG_01 = "商务";
	/**借款标记类型--企业*/
	public static final String LOAN_FLAG_02 = "企业";
	
	// -----------------------------------------------转让申请状态 -------------------------------------------------------
	/**转让申请状态--待转让*/
	public static final String LOAN_TRANSFER_APPLY_STATUS_01 = "待转让";
	/**转让申请状态--转让成功*/
	public static final String LOAN_TRANSFER_APPLY_STATUS_02 = "转让成功";
	/**转让申请状态--转让失败*/
	public static final String LOAN_TRANSFER_APPLY_STATUS_03 = "转让失败";
	/**转让申请状态--部分转让成功*/
	public static final String LOAN_TRANSFER_APPLY_STATUS_04 = "部分转让成功";
	
	/**转让撤销状态--未撤销*/
	public static final String LOAN_TRANSFER_CANCEL_STATUS_01 = "未撤销";
	/**转让撤销状态--已撤销*/
	public static final String LOAN_TRANSFER_CANCEL_STATUS_02 = "已撤销";

	// -----------------------------------------------优选项目产品名称 -------------------------------------------------------
	/** 善转贷 */
	public static final String LOAN_PRODUCT_NAME_01 = "善转贷";
	/** 善融贷 */
	public static final String LOAN_PRODUCT_NAME_02 = "善融贷";
	/** 善意贷 */
	public static final String LOAN_PRODUCT_NAME_03 = "善意贷";
	/** 善企贷 */
	public static final String LOAN_PRODUCT_NAME_04 = "善企贷";
	/** 善真贷 */
	public static final String LOAN_PRODUCT_NAME_05 = "善真贷";
	
	// -----------------------------------------------优选项目服务计划收款状态 -------------------------------------------------------
	/** 待收 */
	public static final String LOAN_PAYMENT_STATUS_01 = "待收"; 
	/** "已收 */
	public static final String LOAN_PAYMENT_STATUS_02 = "已收"; 
	
	// -----------------------------------------------优选项目是否自动投标 -------------------------------------------------------
	/** "启用 */
	public static final String AUTO_INVEST_INFO_OPEN_STATUS_01 = "启用"; 
	/** 禁用 */
	public static final String AUTO_INVEST_INFO_OPEN_STATUS_02 = "禁用"; 
	/** 是 */
	public static final String IS_ALLOW_AUTO_INVEST_01 = "是"; 
	/** 否 */
	public static final String IS_ALLOW_AUTO_INVEST_02 = "否"; 
	/** Y */
	public static final String IS_RUN_AUTO_INVEST_01 = "Y";
	/** N */
	public static final String IS_RUN_AUTO_INVEST_02 = "N";
	
	/**转让申请审核状态 --待审核*/
	public static final String TRANSFER_APPLY_STATUS_UNREVIEW = "待审核";
	/**转让申请审核状态 --通过*/
	public static final String TRANSFER_APPLY_STATUS_PASS = "通过";
	/**转让申请审核状态 --拒绝*/
	public static final String TRANSFER_APPLY_STATUS_REfUSE = "拒绝";
	/**转让申请审核状态 --审核回退*/
	public static final String TRANSFER_APPLY_STATUS_FALLBACK ="审核回退";
	
	// -----------------------------------------------标的单位 -------------------------------------------------------
	/** 标的单位-天 */
	public static final String LOAN_UNIT_DAY = "天";
	/**  标的单位-月  */
	public static final String LOAN_UNIT_MONTH ="月";
	
	// ------------------------------------------------------------------------------------------------------------
	/** 标的一年的天数 360 */
	public static final String DAYS_OF_YEAR = "360";
	/** 标的一年的月数 12 */
	public static final String MONTH_OF_YEAR = "12";
	/** 字符串整数 100 */
	public static final String STRING_100 = "100";
	
	// -----------------------------------------------服务费收取方式 -------------------------------------------------------
	/** 服务费收取方式--线下 */
	public static final String MANAGE_EXPENSE_DEAL_TYPE_01 = "线下";
	/** 服务费收取方式--期初 */
	public static final String MANAGE_EXPENSE_DEAL_TYPE_02 = "期初";
	/** 服务费收取方式--期末 */
	public static final String MANAGE_EXPENSE_DEAL_TYPE_03 = "期末";
	
	// -----------------------------------------------生效方式 -------------------------------------------------------
	/** 生效方式--放款即生效 */
	public static final String GRANT_TYPE_01 = "放款即生效";
	/** 服务费收取方式--放款后到帐生效 */
	public static final String GRANT_TYPE_02 = "放款到账后生效";
	
	// -----------------------------------------------债权来源代码-------------------------------------------------------
	/** 债权来源代码--SLCF */
	public static final String DEBT_SOURCE_CODE_SLCF = "SLCF";
	
	// -----------------------------------------------CREDIT_ACCT_STATUS-------------------------------------------------------
	/** CREDIT_ACCT_STATUS--正常 */
	public static final String CREDIT_ACCT_STATUS_01 = "正常";
	/** CREDIT_ACCT_STATUS--冻结 */
	public static final String CREDIT_ACCT_STATUS_02 = "冻结";
	/** CREDIT_ACCT_STATUS--失效 */
	public static final String CREDIT_ACCT_STATUS_03 = "失效";
	/** CREDIT_ACCT_STATUS--结清 */
	public static final String CREDIT_ACCT_STATUS_04 = "结清";
	/** CREDIT_ACCT_STATUS--逾期 */
	public static final String CREDIT_ACCT_STATUS_05 = "逾期";
	/** CREDIT_ACCT_STATUS--核销 */
	public static final String CREDIT_ACCT_STATUS_06 = "核销";
	
	// -----------------------------------------------资产类型-------------------------------------------------------
	/** ASSET_TYPE_CODE--核销 */
	public static final String ASSET_TYPE_CODE_01 = "散标投资";
	
	// -----------------------------------------------默认投资最大金额-------------------------------------------------------
	/** INVEST_MAX_AMOUNT 1000000 */
	public static final String INVEST_MAX_AMOUNT = "1000000";
	
	// -----------------------------------------------附件编辑标识 -------------------------------------------------------
	/** 附件编辑标识--未脱敏*/
	public static final String ATTACHMENT_FLAG_01 = "未脱敏";
	/** 附件编辑标识--脱敏中*/
	public static final String ATTACHMENT_FLAG_02 = "脱敏中";	
	/** 附件编辑标识--已完成*/
	public static final String ATTACHMENT_FLAG_03 = "已完成";	
	// -----------------------------------------------资产来源 -------------------------------------------------------
	/**
	 *  善林商务--SLSW
		雪橙--63cf2262-6e64-4683-bd0f-18bfa80edfb6
		指尖贷--1708c467-0ef4-4850-b2ac-3f5aad9853a4
		拿米--2a7dc929-9fad-49ba-afbb-654f1a39014a
		巨涟--41924839-9efd-4842-b485-d9593ff09810
	 */
	public static final String[] DEBT_SOURCE = { "SLSW", "63cf2262-6e64-4683-bd0f-18bfa80edfb6", "1708c467-0ef4-4850-b2ac-3f5aad9853a4", "2a7dc929-9fad-49ba-afbb-654f1a39014a", "41924839-9efd-4842-b485-d9593ff09810"};
	public static final String  DEBT_SOURCE_STRING = "SLSW,63cf2262-6e64-4683-bd0f-18bfa80edfb6,1708c467-0ef4-4850-b2ac-3f5aad9853a4,2a7dc929-9fad-49ba-afbb-654f1a39014a,41924839-9efd-4842-b485-d9593ff09810";
	public static final String DEBT_SOURCE_SLSW_CODE = "SLSW";
	public static final String DEBT_SOURCE_XC_CODE = "63cf2262-6e64-4683-bd0f-18bfa80edfb6";
	public static final String DEBT_SOURCE_ZJD_CODE = "1708c467-0ef4-4850-b2ac-3f5aad9853a4";
	public static final String DEBT_SOURCE_NM_CODE = "2a7dc929-9fad-49ba-afbb-654f1a39014a";
	public static final String DEBT_SOURCE_JL_CODE = "41924839-9efd-4842-b485-d9593ff09810";
	
	public static final String DEBT_SOURCE_SLSW = "善林商务";
	public static final String DEBT_SOURCE_XC = "雪橙";
	public static final String DEBT_SOURCE_XCJF = "雪橙金服";
	public static final String DEBT_SOURCE_YZJR = "意真金融";
	public static final String DEBT_SOURCE_JLJR = "巨涟金融";
	public static final String DEBT_SOURCE_ZJD = "指尖贷";
	public static final String DEBT_SOURCE_NM = "拿米";
	public static final String DEBT_SOURCE_NMJR = "拿米金融";
	public static final String DEBT_SOURCE_CFXJD = "财富现金贷";
	public static final String DEBT_SOURCE_JL = "巨涟";
	
	/** 雪澄金服*/
	public static final String DEBT_SOURCE_CODE_XCJF = "XCJF";
	
	/** 资产端公司的账号 */
    public static final String COMPANY_LOGIN_NAME_NMJR = "上海拿米金融信息服务有限公司";
    public static final String COMPANY_LOGIN_NAME_YZJR = "意真（上海）金融信息服务有限公司";
	public static final String COMPANY_LOGIN_NAME_JLJR = "上海聚蓝金融信息服务有限公司";
	public static final String COMPANY_LOGIN_NAME_XCJF = "张甜甜";
	
	/** 雪澄金服-橙信贷 */
	public static final String LOAN_TYPE_XCJF = "橙信贷";
	
	// -----------------------------------------------新手专区 -------------------------------------------------------
	/** 新手标*/
	public static final String LOAN_INFO_NEWER_FLAG = "新手标"; 
	// -----------------------------------------------网贷之家 -------------------------------------------------------
	/** 网贷之家*/
	public static final String WDZJ="网贷之家";
	/** 网贷之家优选项目*/
	public static final String WDZJ_LOAN = "网贷之家优选项目"; 
	/** 网贷之家债权转让*/
	public static final String WDZJ_LOAN_TRANSFER = "网贷之家债权转让"; 
	// -----------------------------------------------智能投顾页面：提醒设置类型 -------------------------------------------------------
	public static final String ZNTG_SET_POINT_TYPE_01 = "登录"; 
	public static final String ZNTG_SET_POINT_TYPE_02 = "充值"; 
	
	public static final String TRANSFERRE_FROM_ONLINE_TIME_20170531 = "债权转让调整20170531";
	// ----------------------------------------------- 端午活动日期 -------------------------------------------------------
	/** 端午优选项目加息活动--开始日期 */
	public static final String DWHD_START_TIME = "'2017-05-25'";
	/** 端午优选项目加息活动--结束日期 */
	public static final String DWHD_END_TIME = "'2017-06-09'";
	
	/**项目状态推送通知标识--通知*/
	public static final String PUSH_FLAG_YES = "是";
	/**项目状态推送通知标识--不通知*/
	public static final String PUSH_FLAG_NO = "否";	
	/**上线时间**/
	public static final String ONLINE_TIME = "2016-05-10";
	/**理财客户**/
	public static final String FINANCIAL_CLIENTS = "理财客户";
	/**信贷客户**/
	public static final String CREDIT_CUSTOMERS = "信贷客户";
	// ----------------------------------------------- 运营报告发布状态 -------------------------------------------------------
	/** 发布状态--新建**/
	public static final String RELEASE_STATUS01 = "新建";
	/** 发布状态--已发布**/
	public static final String RELEASE_STATUS02 ="已发布";
	/** 发布状态--已失效**/
	public static final String RELEASE_STATUS03 ="已失效";
	/** 发布状态--发布**/
	public static final String RELEASE_STATUS_RELEASE = "发布";
	/** 发布状态--失效**/
	public static final String RELEASE_STATUS_FAILURE = "失效";
	/** 发布状态--修改**/
	public static final String RELEASE_STATUS_ALTER = "修改";
	// ---------------借款信息领取状态------------------------------------------------------------------------------------------------------------------------------------------
	/**已领取*/
	public static final String RECEIVE_STATUS_01="已领取";
	/**未领取*/
	public static final String RECEIVE_STATUS_02="未领取";
	// ---------------预约投资状态------------------------------------------------------------------------------------------------------------------------------------------
	/** 预约投资状态  - 排队中*/
	public static final String RESERVE_INVEST_01 = "排队中";
	/** 预约投资状态  - 预约成功*/
	public static final String RESERVE_INVEST_02 = "预约成功";
	/** 预约投资状态  - 待投金额已撤销*/
	public static final String RESERVE_INVEST_03 = "待出借金额已撤销";
	/** 预约投资状态  - 待投金额超时退回*/
	public static final String RESERVE_INVEST_04 = "待出借金额超时退回";
	/** 参数表  - 善林财富设置*/
	public static final String SLCF_COM_PARAM = "善林财富设置";
	/** 参数表  - 最低历史平均年化收益率*/
	public static final String SLCF_COM_PARAM_01 = "最低历史平均年化收益率";
	/** 参数表  - 最高历史平均年化收益率*/
	public static final String SLCF_COM_PARAM_02 = "最高历史平均年化收益率";
	/** 参数表  - 起投金额*/
	public static final String SLCF_COM_PARAM_03 = "起投金额";
	/** 参数表  - 递增金额*/
	public static final String SLCF_COM_PARAM_04 = "递增金额";
	/** 参数表  - 可投产品*/
	public static final String SLCF_COM_PARAM_05 = "可投产品";
	/** 参数表  - 预约结束时间*/
	public static final String SLCF_COM_PARAM_06 = "预约结束时间";
	/** 参数表  - 优选项目列表可投产品*/
	public static final String SLCF_COM_PARAM_20 = "优选项目列表可投产品";
	

	/** 红包状态  - 启用*/
	public static final String ENABLE_RED_PACKETS = "启用";
	/** 红包状态  - 禁用*/
	public static final String DISABLE_RED_PACKETS = "禁用";
	/** 红包状态  - 失效*/
	public static final String UNABLE_RED_PACKETS = "失效";
	/** 用户红包交易状态  -已发放*/
	public static final String ALREADY_ISSUED = "已发放";
	/** 用户红包交易状态  - 已领取*/
	public static final String ALREADY_RECEIVED = "已领取";
	/**有效*/
	public static final String EFFECTIVE_RED_PACKETS = "有效";
	/**礼券到账通知*/
	public static final String VOUCHER_ARRIVAL_NOTICE = "礼券到账通知";
	/**是否发送短信-是*/
	public static final String IS_SEND_MSG = "是";
	/**是否发送短信-否*/
	public static final String NOT_SEND_MSG = "否";
	
	/**满减红包**/
	public static final String RED_PACKET = "满减红包";
	/**加息券**/
	public static final String RATE_COUPON= "加息券";
	/**体验金**/
	public static final String EXPERIENCE_AMOUNT = "体验金";
// ---------------一键出借------------------------------------------------------------------------------------------------------------------------------------------
	/**一键出借**/
	public static final String LOAN_MANAGER_GROUP_01 = "一键出借";
	/** 一键出借预约 */
	public static final String LOAN_MANAGER_GROUP_02 = "一键出借预约";
	/** 一键出借募集中 */
	public static final String LOAN_MANAGER_GROUP_03 ="一键出借募集中";
	
	// ---------------一键出借KEY-------------------------------------------
	/** 一键出借募集中 */
	public static final String LIST_REDIS_KEY = "slcf:yjcj:list";     /** 加息计划结算状态 **/
    public static final String PURCHASE_AWARD_STATUS_YES = "已结清";
    public static final String PURCHASE_AWARD_STATUS_NO = "未结清";

	// ---------------微信公众号推送------------------------------------------------------------------------------------------------------------------------------------------
	/** Key*/
	public static final String SMS_VX_Key = "oOo1iv681nIaFKvvnu0uVqkO";
	/** Url*/
	public static final String SMS_VX_Url = "http://121.196.221.152/jeewx/Template/send.do";
	/**提现资金到账通知（提现成功）*/
	public static final String SMS_TYPE_VX_1="withdrawSuccess";
	/**提现申请拒绝（提现失败）*/
	public static final String SMS_TYPE_VX_2="withdrawFail";
	/**优选项目流标*/
	public static final String SMS_TYPE_VX_3="loanUnrelease";
	/**优选项目生效（投资成功通知）*/
	public static final String SMS_TYPE_VX_4="loanRelease";
	/**优选项目回款*/
	public static final String SMS_TYPE_VX_5="loanRepayment";
	/**债权转让通知（转让人）*/
	public static final String SMS_TYPE_VX_6="loanTransfer";
	
	
	
	
	//-----------------授权申请响----------------------------------------------------------------------------------------------------------------------------------------------
	/** 响应code 成功 */
	public static final String ACCREDIT_REQUEST_RESP_CODE = "0000";
	/** 调用tpp接口url*/
	public static final String XCJF_SQSQ_TPP_URL = "http://10.6.115.115:9000/api/authAlpply";
	}