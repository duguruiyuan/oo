/** 
 * @(#)SubjectConstant.java 1.0.0 2015年04月23日 上午11:23:01  
 *  
 * Copyright © 2014 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.utils;

/**
 * 账户流水科目分类
 * 
 * @author caoyi
 * @version $Revision:1.0.0, $Date: 2015年04月23日 上午11:23:01   $
 */
public class SubjectConstant {
	// ---------------------------------------------------账户流水科目---------------------------------------------------
	/** 资金流水--科目--公用--充值 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RECHARGE = "充值";
	/** 资金流水--科目--公用--提现 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW = "提现";
	/** 资金流水--科目--公用--提现手续费 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW_EXPENSE = "提现手续费";
	/** 资金流水--科目--公用--充值手续费 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RECHARGE_EXPENSE = "充值手续费";
	/** 资金流水--科目--投资人--购买债权 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_TENDER_MAKEOVER_IN = "购买债权";
	/** 资金流水--科目--投资人--出售债权 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_TENDER_MAKEOVER_OUT = "出售债权";
	/** 资金流水--科目--公用--提现冻结 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW_FREEZE = "提现冻结";
	/** 资金流水--科目--公用--提现解冻 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW = "提现解冻";
	/** 资金流水--科目--公用--购买活期宝 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN = "购买活期宝";
	/** 资金流水--科目--公用--购买体验宝 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_EXPERIENCE = "购买体验宝";
	/** 资金流水--科目--公用--提现额度 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_WITHDRAW_LIMITED = "快速赎回额度调整";
	/** 资金流水--科目--公用--快速赎回活期宝 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE = "快速赎回活期宝";
	/** 资金流水--科目--公用--还款 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAY = "还款";
	/** 资金流水--科目--投资人--分配 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_ALLOT = "债权分配";
	/** 资金流水--科目--公用--普通赎回活期宝 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_NORMAL = "普通赎回活期宝";
	/** 资金流水--科目--公用--普通赎回活期宝冻结 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_NORMAL_FREEZE = "普通赎回活期宝冻结";
	/** 资金流水--科目--公用--普通赎回活期宝解冻 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_NORMAL_UNFREEZE = "普通赎回活期宝解冻";
	/** 资金流水--科目--投资人--撤销分配 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_ALLOT_CANCEL = "撤销分配";
	/** 资金流水--科目--实名认证从公司居间人账户扣除手续费 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_REALNAME_AUTH = "实名认证";
	/** 资金流水--科目--调帐 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TANS_ACCOUNT_TYPE_03="调帐";
	/** 资金流水--科目--调帐 --投资奖励**/
	public static final String ACCOUNT_FLOW_SUBJECT_TANS_ACCOUNT_TYPE_01="投资奖励";
	/** 资金流水--科目--调帐 --推荐奖励**/
	public static final String ACCOUNT_FLOW_SUBJECT_TANS_ACCOUNT_TYPE_02="推荐奖励";
	/** 资金流水--科目--推广佣金*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_GOLD_INCOME = "活期宝推广佣金";
	/** 资金流水--科目--推广奖金*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_GOLD_AWARD = "活期宝推广奖金";
	/** 资金流水--科目--风险金充值*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_RECHARGE = "企业借款风险金充值";
	/** 资金流水--科目--购买优选计划*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_WEALTH_JOIN = "购买优选计划";
	/** 资金流水--科目--优选计划债权匹配*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_WEALTH_AUTO_MATCH = "购买债权转让";
	/** 资金流水--科目--线下提现*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_OFFLINE_WITHDRAW = "线下提现";
	/** 资金流水--科目--线下充值*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_OFFLINE_RECHARGE = "线下充值";
	/** 资金流水--科目--公用--线下提现冻结 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_OFFLINE_WITHDRAW_FREEZE = "线下提现冻结";
	/** 资金流水--科目--公用--线下提现解冻 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_OFFLINE_UNFREEZE_WITHDRAW = "线下提现解冻";
	/** 账户流水交易类型-活动金提现*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_ACTIVITY_AMOUNT_WITHDRAW = "活动金额提现";
	/** 账户流水交易类型-活动金提现冻结*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_ACTIVITY_AMOUNT_FREEZE = "活动金额提现冻结";
	/** 账户流水交易类型-活动金提现解冻*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_ACTIVITY_AMOUNT_UNFREEZE = "活动额金提现解冻";
	
	
	
	
	/** 账户流水交易类型-领取体验金*/
	public static final String TRADE_FLOW_TYPE_EXPERIENCE_AMOUNT = "领取体验金";
	/** 账户流水交易类型-活期宝收益*/
	public static final String TRADE_FLOW_TYPE_01 = "活期宝收益";
	/** 账户流水交易类型-善林理财收益*/
	public static final String TRADE_FLOW_TYPE_02 = "善林理财收益";
	/** 账户流水交易类型-体验宝收益*/
	public static final String TRADE_FLOW_TYPE_03 = "体验宝收益";
	/** 账户流水交易类型-定期宝收益*/
	public static final String TRADE_FLOW_TYPE_04 = "定期宝收益";
	/** 账户流水交易类型-债权分配*/
	public static final String TRADE_FLOW_TYPE_ALLOT = "债权分配";
	/** 账户流水交易类型-充值*/
	public static final String TRADE_FLOW_TYPE_RECHARGE = "充值";
	/** 账户流水交易类型-充值手续费*/
	public static final String TRADE_FLOW_TYPE_RECHARGE_EXPENSE = "充值手续费";
	/** 账户流水交易类型-提现*/
	public static final String TRADE_FLOW_TYPE_WITHDRAW = "提现";
	/** 账户流水交易类型-提现冻结*/
	public static final String TRADE_FLOW_TYPE_FREEZE = "提现冻结";
	/** 账户流水交易类型-提现冻结*/
	public static final String TRADE_FLOW_TYPE_UNFREEZE = "提现解冻";
	/** 账户流水交易类型-活动金提现*/
	public static final String TRADE_FLOW_TYPE_ACTIVITY_AMOUNT_WITHDRAW = "活动金额提现";
	/** 账户流水交易类型-活动金提现冻结*/
	public static final String TRADE_FLOW_TYPE_ACTIVITY_AMOUNT_FREEZE = "活动金额提现冻结";
	/** 账户流水交易类型-活动金提现解冻*/
	public static final String TRADE_FLOW_TYPE_ACTIVITY_AMOUNT_UNFREEZE = "活动金额提现解冻";
	/** 账户流水交易类型-提现冻结*/
	public static final String TRADE_FLOW_TYPE_EXPENSE = "提现手续费";
	/** 账户流水交易类型-购买活期宝*/
	public static final String TRADE_FLOW_TYPE_JOIN = "购买活期宝";
	/** 账户流水交易类型-购买体验宝*/
	public static final String TRADE_FLOW_TYPE_JOIN_EXPERIENCE = "购买体验宝";
	/** 账户流水交易类型-提现额度*/
	public static final String TRADE_FLOW_TYPE_WITHDRAW_LIMITED = "快速赎回额度调整";
	/** 账户流水交易类型-赎回活期宝*/
	public static final String TRADE_FLOW_TYPE_ATONE = "快速赎回活期宝";
	/** 账户流水交易类型-还款*/
	public static final String TRADE_FLOW_TYPE_REPAY = "还款";
	/** 账户流水交易类型--实名认证从公司居间人账户扣除手续费 **/
	public static final String TRADE_FLOW_TYPE_REALNAME_AUTH = "实名认证手续费";
	/** 账户流水交易类型-解绑*/
	public static final String TRADE_FLOW_TYPE_UNBIND_CARD = "解绑银行卡";
	/** 账户流水交易类型-推广佣金*/
	public static final String TRADE_FLOW_TYPE_GOLD_INCOME = "活期宝推广佣金";
	/** 账户流水交易类型-推广奖金*/
	public static final String TRADE_FLOW_TYPE_GOLD_AWARD = "活期宝推广奖金";
	/** 账户流水交易类型-活期宝价值回收*/
	public static final String TRADE_FLOW_TYPE_BAO_UNATONE = "活期宝价值回收";
	/** 公司收益*/
	public static final String TRADE_FLOW_TYPE_COMPANY = "公司收益";
	/** 每日计息*/
	public static final String TRADE_FLOW_DAILY_INTEREST = "每日计息";
	/** 普通赎回活期宝*/
	public static final String TRADE_FLOW_TYPE_ATONE_NORMAL = "普通赎回活期宝";
	/** 普通赎回活期宝冻结*/
	public static final String TRADE_FLOW_TYPE_ATONE_NORMAL_FREEZE = "普通赎回活期宝冻结";
	/** 普通赎回活期宝解冻*/
	public static final String TRADE_FLOW_TYPE_ATONE_NORMAL_UNFREEZE = "普通赎回活期宝解冻";
	/** 账户流水交易类型-撤销分配*/
	public static final String TRADE_FLOW_TYPE_ALLOT_CANCEL = "撤销分配";
	/** 赎回体验宝*/
	public static final String TRADE_FLOW_TYPE_TYBWITHDRAW = "赎回体验宝";
	/** 赎回体验宝收益*/
	public static final String TRADE_FLOW_TYPE_TYBINCOME_WITHDRAW = "赎回体验宝收益";
	/** 回收体验金*/
	public static final String TRADE_FLOW_TYPE_TYBAMOUNT_WITHDRAW = "回收体验金";
	/** 资金流水--科目--投资人--领取体验金 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_EXPERIENCE_AMOUNT = "领取体验金";
	
	/** 账户流水交易类型-风险金充值*/
	public static final String TRADE_FLOW_TYPE_RISK_RECHARGE = "企业借款风险金充值";
	
	/** 科目方向 */
	public static final String SUBJECT_TYPE_DIRECTION_OUT = "支出";
	public static final String SUBJECT_TYPE_DIRECTION_COMING = "收入";
	
	/** 科目类型-价值 */
	public static final String SUBJECT_TYPE_VALUE="价值";
	/** 科目类型-金额 */
	public static final String SUBJECT_TYPE_AMOUNT="金额";
	/**
	 * 科目类型-金额-红包
	 */
	public static final String SUBJECT_TYPE_AMOUNT_RED_ENVELOPE="红包";

	/**科目类型-体验金*/
	public static final String SUBJECT_TYPE_EXPERIENCE_AMOUNT = "体验金";

	//--------------------申请类型--------------------------------------------------------------------------------------------------
	public static final String APPLY_TYPE_WITHDRAW = "提现";
	
	public static final String TRADE_STATUS_SUCCESS_CODE = "000000";
	
	public static final String TRADE_STATUS_SUCCESS = "SUCCESS";
	
	
	// ---------------------------------------------------定期宝流水科目---------------------------------------------------
	/** 资金流水--科目--公用--购买定期宝 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_TERM = "购买定期宝";
	/** 资金流水--科目--公用--提前赎回冻结 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TERM_FREEZE = "定期宝提前赎回冻结";
	/** 资金流水--科目--公用--到期赎回冻结 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TERM_FREEZE_TERM = "定期宝到期赎回冻结";
	/** 资金流水--科目--公用--公司回购 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_BUY_BACK = "回购定期宝";
	/** 资金流水--科目--公用--公司补差 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_DIFF = "定期宝补差收益";
	/** 资金流水--科目--公用--提前赎回手续费 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_EXPENSE = "定期宝提前赎回手续费";
	/** 资金流水--科目--公用--计提风险金 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_RISK = "定期宝计提风险金";
	/** 资金流水--科目--公用--账户管理费 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_MANAGE = "定期宝账户管理费";
	/** 资金流水--科目--公用--奖励收益 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_AWARD = "定期宝到期奖励";
	/** 资金流水--科目--公用--到期赎回 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TERM_FINISH = "到期赎回定期宝";
	/** 资金流水--科目--公用--提前赎回 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TERM_ADVANCE = "提前赎回定期宝";
	/** 资金流水--科目--公用--转让定期宝 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TRANSFER = "转让定期宝";
	/** 资金流水--科目--公用--赎回定期宝 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TERM = "赎回定期宝";
	/** 资金流水--科目--公用--定期宝收益 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_INCOME = "定期宝到期收益";
	/** 资金流水--科目--公用--公司定期宝收益 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_DAILY_INCOME = "公司定期宝收益";
	/** 资金流水--科目--公用--扣除活动奖励 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_AWARD_EXPENSE = "扣除活动奖励";
	
	/** 账户流水交易类型-购买定期宝*/
	public static final String TRADE_FLOW_TYPE_JOIN_TERM = "购买定期宝";
	/** 赎回定期宝冻结*/
	public static final String TRADE_FLOW_TYPE_ATONE_TERM_FREEZE = "定期宝提前赎回冻结";
	/** 到期赎回冻结*/
	public static final String TRADE_FLOW_TYPE_ATONE_TERM_FREEZE_TERM = "定期宝到期赎回冻结";
	/** 公司回购*/
	public static final String TRADE_FLOW_TYPE_ATONE_BUY_BACK = "回购定期宝";
	/** 公司补差 **/
	public static final String TRADE_FLOW_TYPE_ATONE_DIFF = "定期宝补差收益";
	/** 提前赎回手续费 **/
	public static final String TRADE_FLOW_TYPE_ATONE_EXPENSE = "定期宝提前赎回手续费";
	/** 计提风险金 **/
	public static final String TRADE_FLOW_TYPE_ATONE_RISK = "定期宝计提风险金";
	/** 账户管理费 **/
	public static final String TRADE_FLOW_TYPE_ATONE_MANAGE = "定期宝账户管理费";
	/** 奖励收益 **/
	public static final String TRADE_FLOW_TYPE_ATONE_AWARD = "定期宝到期奖励";
	/** 到期赎回 **/
	public static final String TRADE_FLOW_TYPE_ATONE_TERM_FINISH = "到期赎回定期宝";
	/** 提前赎回 **/
	public static final String TRADE_FLOW_TYPE_ATONE_TERM_ADVANCE = "提前赎回定期宝";
	/** 转让定期宝 **/
	public static final String TRADE_FLOW_TYPE_ATONE_TRANSFER = "转让定期宝";
	/** 赎回定期宝 **/
	public static final String TRADE_FLOW_TYPE_ATONE_TERM = "赎回定期宝";
	/** 定期宝到期收益 **/
	public static final String TRADE_FLOW_TYPE_ATONE_INCOME = "定期宝到期收益";
	/** 公司定期宝收益 **/
	public static final String TRADE_FLOW_TYPE_DAILY_INCOME = "公司定期宝收益";
	/** 扣除活动奖励 **/
	public static final String TRADE_FLOW_TYPE_AWARD_EXPENSE = "扣除活动奖励";
	
	// ---------------------------------------------------企业借款流水科目---------------------------------------------------
	/** 资金流水--科目--公用--购买企业借款 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_PROJECT = "购买企业借款";
	/** 资金流水--科目--公用--企业借款流标 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNRELEASE_PROJECT = "企业借款流标";
	/** 资金流水--科目--公用--企业借款生效 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RELEASE_PROJECT = "企业借款生效";
	/** 资金流水--科目--公用--企业借款还款 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_PROJECT = "企业借款还款";
	/** 资金流水--科目--公用--企业借款逾期还款 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_OVERDUE_REPAYMENT_PROJECT = "企业借款逾期还款";
	/** 资金流水--科目--公用--企业借款提前还款 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_EARLY_REPAYMENT_PROJECT = "企业借款提前还款";
	/** 资金流水--科目--公用--企业借款风险金垫付 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RISK_REPAYMENT_PROJECT = "企业借款风险金垫付";
	/** 资金流水--科目--公用--还企业借款风险金*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RISK_REPAY_PROJECT = "还企业借款风险金";
	/** 资金流水--科目--公用--企业借款还款冻结 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_FREEZE_PROJECT = "企业借款还款冻结";
	/** 资金流水--科目--公用--企业借款还款解冻 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_UNFREEZE_PROJECT = "企业借款还款解冻";
	/** 资金流水--科目--风险金--风险金本金 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_PRINCIPAL_PROJECT = "还风险金本金";
	/** 资金流水--科目--风险金--风险金利息 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_INTEREST_PROJECT = "还风险金利息";
	/** 资金流水--科目--风险金--风险金管理费 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_EXPENSE_PROJECT = "还风险金账户管理费";
	/** 资金流水--科目--风险金--风险金罚息 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_PENALTY_PROJECT = "还风险金逾期费用";
	/** 资金流水--科目--用户--本金 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_PROJECT = "本金";
	/** 资金流水--科目--用户--利息 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_INTEREST_PROJECT = "利息";
	/** 资金流水--科目--用户--逾期费用 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PENALTY_PROJECT = "逾期费用";
	/** 资金流水--科目--用户--企业借款回款 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_ALL_PROJECT = "企业借款回款";
	/** 资金流水--科目--用户--公司企业借款收益 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_ALL_PROJECT = "公司企业借款收益";
	/** 资金流水--科目--公司--账户管理费 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_EXPENSE_PROJECT = "账户管理费";
	/** 资金流水--科目--公司--逾期费用 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_PENALTY_PROJECT = "公司逾期费用";
	/** 资金流水--科目--风险金--风险金本金 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PRINCIPAL_PROJECT = "风险金垫付本金";
	/** 资金流水--科目--风险金--风险金利息 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_INTEREST_PROJECT = "风险金垫付利息";
	/** 资金流水--科目--风险金--风险金管理费 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_EXPENSE_PROJECT = "风险金垫付账户管理费";
	/** 资金流水--科目--风险金--风险金罚息 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PENALTY_PROJECT = "风险金垫付逾期费用";
	/** 资金流水--科目--风险金--违约金 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_BREACH_PROJECT = "违约金";
	/** 资金流水--科目--用户--企业借款奖励收益 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_AWARD_PROJECT = "企业借款奖励收益";
	/** 资金流水--科目--用户--优选项目奖励收益 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_DISPERSE_AWARD_PROJECT = "优选项目奖励收益";

	/** 账户流水交易类型-购买企业借款*/
	public static final String TRADE_FLOW_TYPE_JOIN_PROJECT = "购买企业借款";
	/** 账户流水交易类型-企业借款流标*/
	public static final String TRADE_FLOW_TYPE_UNRELEASE_PROJECT = "企业借款流标";
	/** 账户流水交易类型-企业借款生效*/
	public static final String TRADE_FLOW_TYPE_RELEASE_PROJECT = "企业借款生效";
	/** 账户流水交易类型-企业借款还款*/
	public static final String TRADE_FLOW_TYPE_REPAYMENT_PROJECT = "企业借款还款";
	/** 账户流水交易类型-企业借款逾期还款*/
	public static final String TRADE_FLOW_TYPE_OVEDUE_REPAYMENT_PROJECT = "企业借款逾期还款";
	/** 账户流水交易类型-企业借款提前还款*/
	public static final String TRADE_FLOW_TYPE_EARLY_REPAYMENT_PROJECT = "企业借款提前还款";
	/** 账户流水交易类型-企业借款风险金垫付*/
	public static final String TRADE_FLOW_TYPE_RISK_REPAYMENT_PROJECT = "企业借款风险金垫付";
	/** 账户流水交易类型-还企业借款风险金*/
	public static final String TRADE_FLOW_TYPE_RISK_REPAY_PROJECT = "还企业借款风险金";
	/** 账户流水交易类型-企业借款还款冻结*/
	public static final String TRADE_FLOW_TYPE_REPAYMENT_FREEZE_PROJECT = "企业借款还款冻结";
	/** 账户流水交易类型-企业借款还款解冻*/
	public static final String TRADE_FLOW_TYPE_REPAYMENT_UNFREEZE_PROJECT = "企业借款还款解冻";
	/** 账户流水交易类型--风险金--风险金本金 **/
	public static final String TRADE_FLOW_TYPE_RISK_REPAYMENT_PRINCIPAL_PROJECT = "还风险金本金";
	/** 账户流水交易类型--风险金--风险金利息 **/
	public static final String TRADE_FLOW_TYPE_RISK_REPAYMENT_INTEREST_PROJECT = "还风险金利息";
	/** 账户流水交易类型--风险金--风险金管理费 **/
	public static final String TRADE_FLOW_TYPE_RISK_REPAYMENT_EXPENSE_PROJECT = "还风险金账户管理费";
	/** 账户流水交易类型--风险金--风险金罚息 **/
	public static final String TRADE_FLOW_TYPE_RISK_REPAYMENT_PENALTY_PROJECT = "还风险金逾期费用";
	/** 账户流水交易类型--用户--本金 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAYMENT_PRINCIPAL_PROJECT = "本金";
	/** 账户流水交易类型--用户--利息 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAYMENT_INTEREST_PROJECT = "利息";
	/** 账户流水交易类型--用户--逾期费用 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAYMENT_PENALTY_PROJECT = "逾期费用";
	/** 账户流水交易类型--用户--企业借款回款 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_PROJECT = "企业借款回款";
	/** 账户流水交易类型--公司--公司企业借款收益 **/
	public static final String TRADE_FLOW_TYPE_COMPANY_REPAYMENT_ALL_PROJECT = "公司企业借款收益";
	/** 账户流水交易类型--公司--账户管理费 **/
	public static final String TRADE_FLOW_TYPE_COMPANY_REPAYMENT_EXPENSE_PROJECT = "账户管理费";
	/** 账户流水交易类型--公司--逾期费用 **/
	public static final String TRADE_FLOW_TYPE_COMPANY_REPAYMENT_PENALTY_PROJECT = "公司逾期费用";
	/** 账户流水交易类型--风险金--风险金本金 **/
	public static final String TRADE_FLOW_TYPE_RISK_OVERDUE_PRINCIPAL_PROJECT = "风险金垫付本金";
	/** 账户流水交易类型--风险金--风险金利息 **/
	public static final String TRADE_FLOW_TYPE_RISK_OVERDUE_INTEREST_PROJECT = "风险金垫付利息";
	/** 账户流水交易类型--风险金--风险金管理费 **/
	public static final String TRADE_FLOW_TYPE_RISK_OVERDUE_EXPENSE_PROJECT = "风险金垫付账户管理费";
	/** 账户流水交易类型--风险金--风险金罚息 **/
	public static final String TRADE_FLOW_TYPE_RISK_OVERDUE_PENALTY_PROJECT = "风险金垫付逾期费用";
	/** 账户流水交易类型--风险金--违约金 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAYMENT_BREACH_PROJECT = "违约金";
	/** 账户流水交易类型--风险金--企业借款贴息 **/
	public static final String TRADE_FLOW_TYPE_REPAYMENT_COMPENSATE_PROJECT = "企业借款贴息";
	/** 账户流水交易类型--用户--企业借款奖励收益 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAYMENT_AWARD_PROJECT = "企业借款奖励收益";
	
	// ---------------------------------------------------优选计划流水科目---------------------------------------------------
	/** 账户流水交易类型--用户--优选计划回款 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_WEALTH = "优选计划回款";
	/** 账户流水交易类型--用户--本金 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAYMENT_PRINCIPAL_WEALTH = "优选计划回款本金";
	/** 账户流水交易类型--用户--利息 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAYMENT_INTEREST_WEALTH = "优选计划回款利息";
	/** 账户流水交易类型--用户--返息 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAY_INTEREST_WEALTH = "优选计划返息";
	/** 账户流水交易类型--用户--*返息债权转让 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAY_LOAN_TRANSFER_WEALTH = "优选计划返息转让债权";
	/** 账户流水交易类型--用户--返息贴息 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAY_INTEREST_OTHER_WEALTH = "优选计划返息贴息";
	/** 账户流水交易类型--用户--*赎回债权转让 **/
	public static final String TRADE_FLOW_TYPE_USER_ATONE_LOAN_TRANSFER_WEALTH = "优选计划赎回债权";
	/** 账户流水交易类型--用户--赎回贴息 **/
	public static final String TRADE_FLOW_TYPE_USER_ATONE_INTEREST_OTHER_WEALTH = "优选计划赎回贴息";
	/** 账户流水交易类型--用户--赎回服务费 **/
	public static final String TRADE_FLOW_TYPE_USER_ATONE_EXPENSE_OTHER_WEALTH = "优选计划服务费";
	/** 账户流水交易类型--用户--到期赎回优选计划 **/
	public static final String TRADE_FLOW_TYPE_USER_DUE_ATONE_WEALTH = "到期赎回优选计划";
	/** 账户流水交易类型--用户--提前赎回优选计划 **/
	public static final String TRADE_FLOW_TYPE_USER_ADVANCE_ATONE_WEALTH = "提前赎回优选计划";
	/** 账户流水交易类型--用户--提前退出费用 **/
	public static final String TRADE_FLOW_TYPE_USER_ADVANCED_ATONE_EXPENSE_WEALTH = "提前退出优选计划费用";
	/** 账户流水交易类型--用户--到期赎回优选计划奖励收益 **/
	public static final String TRADE_FLOW_TYPE_USER_DUE_ATONE_AWARD_WEALTH = "到期赎回优选计划奖励收益";
	/** 账户流水交易类型-购买优选计划*/
	public static final String TRADE_FLOW_TYPE_JOIN_WEALTH = "购买优选计划";
	/**账户流水类型-*债权匹配 */
	public static final String TRADE_FLOW_TYPE_AUTO_MATCH= "匹配债权";
	/** 账户流水交易类型-线下提现*/
	public static final String TRADE_FLOW_TYPE_OFFLINE_WITHDRAW = "线下提现";
	/** 账户流水交易类型-线下充值*/
	public static final String TRADE_FLOW_TYPE_OFFLINE_RECHARGE = "线下充值";
	/** 账户流水交易类型-线下提现冻结*/
	public static final String TRADE_FLOW_TYPE_OFFLINE_FREEZE = "线下提现冻结";
	/** 账户流水交易类型-线下提现解冻*/
	public static final String TRADE_FLOW_TYPE_OFFLINE_UNFREEZE = "线下提现解冻";
	
	// ---------------------------------------------------优选项目流水科目---------------------------------------------------
	/** 资金流水--科目--公用--购买优选项目 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN = "购买优选项目";
	/** 资金流水--科目--公用--优选项目流标 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNRELEASE_LOAN = "优选项目流标";
	/** 资金流水--科目--公用--优选项目生效 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RELEASE_LOAN = "优选项目生效";
	/** 资金流水--科目--公用--优选项目还款 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_LOAN = "优选项目还款";
	/** 资金流水--科目--公用--优选项目还款充值 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_RECHARGE_LOAN = "优选项目还款充值";
	/** 资金流水--科目--公用--优选项目还款代扣 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_DISOUNT_LOAN = "优选项目还款代扣";
	/** 资金流水--科目--公用--优选项目逾期还款 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_OVERDUE_REPAYMENT_LOAN = "优选项目逾期还款";
	/** 资金流水--科目--公用--优选项目提前还款 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_EARLY_REPAYMENT_LOAN = "优选项目提前还款";
	/** 资金流水--科目--公用--优选项目风险金垫付 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RISK_REPAYMENT_LOAN = "优选项目风险金垫付";
	/** 资金流水--科目--公用--还优选项目风险金*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RISK_REPAY_LOAN = "还优选项目风险金";
	/** 资金流水--科目--公用--优选项目还款冻结 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_FREEZE_LOAN = "优选项目还款冻结";
	/** 资金流水--科目--公用--优选项目还款解冻 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_UNFREEZE_LOAN = "优选项目还款解冻";
	/** 资金流水--科目--风险金--风险金本金 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_PRINCIPAL_LOAN = "还风险金本金";
	/** 资金流水--科目--风险金--风险金利息 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_INTEREST_LOAN = "还风险金利息";
	/** 资金流水--科目--风险金--风险金管理费 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_EXPENSE_LOAN = "还风险金账户管理费";
	/** 资金流水--科目--风险金--风险金罚息 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_PENALTY_LOAN = "还风险金逾期费用";
	/** 资金流水--科目--用户--本金 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_LOAN = "本金";
	/** 资金流水--科目--用户--利息 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_INTEREST_LOAN = "利息";
	/** 资金流水--科目--用户--逾期费用 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PENALTY_LOAN = "逾期费用";
	/** 资金流水--科目--用户--优选项目回款 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_ALL_LOAN = "优选项目回款";
	/** 资金流水--科目--用户--公司优选项目收益 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_ALL_LOAN = "公司优选项目收益";
	/** 资金流水--科目--公司--账户管理费 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_EXPENSE_LOAN = "优选项目账户管理费";
	/** 资金流水--科目--公司--逾期费用 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_PENALTY_LOAN = "公司逾期费用";
	/** 资金流水--科目--风险金--风险金本金 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PRINCIPAL_LOAN = "风险金垫付本金";
	/** 资金流水--科目--风险金--风险金利息 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_INTEREST_LOAN = "风险金垫付利息";
	/** 资金流水--科目--风险金--风险金管理费 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_EXPENSE_LOAN = "风险金垫付账户管理费";
	/** 资金流水--科目--风险金--风险金罚息 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PENALTY_LOAN = "风险金垫付逾期费用";
	/** 资金流水--科目--风险金--违约金 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_BREACH_LOAN = "违约金";
	/** 资金流水--科目--用户--优选项目奖励收益 **/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_AWARD_LOAN = "优选项目奖励收益";
	/** 账户流水交易类型-优选项目充值*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RECHARGE_LOAN = "优选项目充值";
	/** 账户流水交易类型-优选项目平台服务费*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_MANAGE_LOAN = "优选项目平台服务费";
	/** 账户流水交易类型-推荐奖励*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_AWARD_LOAN = "推荐奖励";
	/** 账户流水交易类型-购买债权转让*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN_TRANSFER = "购买债权转让";
	/** 账户流水交易类型-债权转让手续费*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_EXPENSE_LOAN_TRANSFER = "债权转让手续费";
	/** 账户流水交易类型-优选项目放款*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_GRANT_LOAN = "优选项目放款";
	/** 账户流水交易类型-优选项目放款冲正*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_GRANT_LOAN_REVERT = "优选项目放款冲正";
	/**
	 * 账户流水交易类型-红包发放冲正
	 */
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_RED_ENVELOPE_REVERT = "红包发放冲正";
	/** 账户流水交易类型-优选项目平台服务费冲正*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_MANAGE_LOAN_REVERT = "优选项目平台服务费冲正";
	/** 账户流水交易类型-加息券奖励发放*/
	public static final String ACCOUNT_FLOW_SUBJECT_TYPE_PURCHASE_AWARD_RELEASE = "加息券奖金发放";

	/** 账户流水交易类型-购买优选项目*/
	public static final String TRADE_FLOW_TYPE_JOIN_LOAN = "购买优选项目";
	/** 账户流水交易类型-优选项目预约出借*/
	public static final String TRADE_FLOW_TYPE_JOIN_LOAN_FOR_RESEVER = "优选项目预约出借";
	/** 账户流水交易类型-优选项目流标*/
	public static final String TRADE_FLOW_TYPE_UNRELEASE_LOAN = "优选项目流标";
	/** 账户流水交易类型-优选项目生效*/
	public static final String TRADE_FLOW_TYPE_RELEASE_LOAN = "优选项目生效";
	/** 账户流水交易类型-优选项目还款*/
	public static final String TRADE_FLOW_TYPE_REPAYMENT_LOAN = "优选项目还款";
	/** 账户流水交易类型-优选项目还款充值*/
	public static final String TRADE_FLOW_TYPE_REPAYMENT_RECHARGE_LOAN = "优选项目还款充值";

	/**优选项目代扣还款***/
	public static final String TRADE_FLOW_TYPE_REPAYMENT_DISOUNT_LOAN = "优选项目代扣还款";

	/** 账户流水交易类型-优选项目逾期还款*/
	public static final String TRADE_FLOW_TYPE_OVEDUE_REPAYMENT_LOAN = "优选项目逾期还款";
	/** 账户流水交易类型-优选项目提前还款*/
	public static final String TRADE_FLOW_TYPE_EARLY_REPAYMENT_LOAN = "优选项目提前还款";
	/** 账户流水交易类型-优选项目风险金垫付*/
	public static final String TRADE_FLOW_TYPE_RISK_REPAYMENT_LOAN = "优选项目风险金垫付";
	/** 账户流水交易类型-还优选项目风险金*/
	public static final String TRADE_FLOW_TYPE_RISK_REPAY_LOAN = "还优选项目风险金";
	/** 账户流水交易类型-优选项目还款冻结*/
	public static final String TRADE_FLOW_TYPE_REPAYMENT_FREEZE_LOAN = "优选项目还款冻结";
	/** 账户流水交易类型-优选项目还款解冻*/
	public static final String TRADE_FLOW_TYPE_REPAYMENT_UNFREEZE_LOAN = "优选项目还款解冻";
	/** 账户流水交易类型--风险金--风险金本金 **/
	public static final String TRADE_FLOW_TYPE_RISK_REPAYMENT_PRINCIPAL_LOAN = "还风险金本金";
	/** 账户流水交易类型--风险金--风险金利息 **/
	public static final String TRADE_FLOW_TYPE_RISK_REPAYMENT_INTEREST_LOAN = "还风险金利息";
	/** 账户流水交易类型--风险金--风险金管理费 **/
	public static final String TRADE_FLOW_TYPE_RISK_REPAYMENT_EXPENSE_LOAN = "还风险金账户管理费";
	/** 账户流水交易类型--风险金--风险金罚息 **/
	public static final String TRADE_FLOW_TYPE_RISK_REPAYMENT_PENALTY_LOAN = "还风险金逾期费用";
	/** 账户流水交易类型--用户--本金 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAYMENT_PRINCIPAL_LOAN = "本金";
	/** 账户流水交易类型--用户--利息 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAYMENT_INTEREST_LOAN = "利息";
	/** 账户流水交易类型--用户--逾期费用 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAYMENT_PENALTY_LOAN = "逾期费用";
	/** 账户流水交易类型--用户--优选项目回款 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_LOAN = "优选项目回款";
	/** 账户流水交易类型--公司--公司优选项目收益 **/
	public static final String TRADE_FLOW_TYPE_COMPANY_REPAYMENT_ALL_LOAN = "公司优选项目收益";
	/** 账户流水交易类型--公司--账户管理费 **/
	public static final String TRADE_FLOW_TYPE_COMPANY_REPAYMENT_EXPENSE_LOAN = "优选项目账户管理费";
	/** 账户流水交易类型--公司--逾期费用 **/
	public static final String TRADE_FLOW_TYPE_COMPANY_REPAYMENT_PENALTY_LOAN = "公司逾期费用";
	/** 账户流水交易类型--风险金--风险金本金 **/
	public static final String TRADE_FLOW_TYPE_RISK_OVERDUE_PRINCIPAL_LOAN = "风险金垫付本金";
	/** 账户流水交易类型--风险金--风险金利息 **/
	public static final String TRADE_FLOW_TYPE_RISK_OVERDUE_INTEREST_LOAN = "风险金垫付利息";
	/** 账户流水交易类型--风险金--风险金管理费 **/
	public static final String TRADE_FLOW_TYPE_RISK_OVERDUE_EXPENSE_LOAN = "风险金垫付账户管理费";
	/** 账户流水交易类型--风险金--风险金罚息 **/
	public static final String TRADE_FLOW_TYPE_RISK_OVERDUE_PENALTY_LOAN = "风险金垫付逾期费用";
	/** 账户流水交易类型--风险金--违约金 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAYMENT_BREACH_LOAN = "违约金";
	/** 账户流水交易类型--风险金--优选项目贴息 **/
	public static final String TRADE_FLOW_TYPE_REPAYMENT_COMPENSATE_LOAN = "优选项目贴息";
	/** 账户流水交易类型--用户--优选项目奖励收益 **/
	public static final String TRADE_FLOW_TYPE_USER_REPAYMENT_AWARD_LOAN = "优选项目奖励收益";
	/** 账户流水交易类型-优选项目充值*/
	public static final String TRADE_FLOW_TYPE_RECHARGE_LOAN = "优选项目充值";
	/** 账户流水交易类型-优选项目平台服务费*/
	public static final String TRADE_FLOW_TYPE_MANAGE_LOAN = "优选项目平台服务费";
	/** 账户流水交易类型-推荐奖励*/
	public static final String TRADE_FLOW_TYPE_COMMISION_AWARD_LOAN = "推荐奖励";
	/** 账户流水交易类型-购买债权转让*/
	public static final String TRADE_FLOW_TYPE_JOIN_LOAN_TRANSFER = "购买债权转让";
	/** 账户流水交易类型-债权转让手续费*/
	public static final String TRADE_FLOW_TYPE_EXPENSE_LOAN_TRANSFER = "债权转让手续费";
	/** 账户流水交易类型-优选项目放款*/
	public static final String TRADE_FLOW_TYPE_GRANT_LOAN = "优选项目放款";
	/** 账户流水交易类型-优先投预约金额冻结*/
	public static final String TRADE_FLOW_TYPE_RESERVE_INVEST_01 = "优先投预约金额冻结";
	/** 账户流水交易类型-优选项目预约撤销*/
	public static final String TRADE_FLOW_TYPE_RESERVE_INVEST_02 = "优选项目预约撤销";
	/** 账户流水交易类型-优选项目预约金额退回*/
	public static final String TRADE_FLOW_TYPE_RESERVE_INVEST_03 = "优选项目预约金额退回";
	/** 账户流水交易类型-优选项目放款冲正*/
	public static final String TRADE_FLOW_TYPE_GRANT_LOAN_REVERT = "优选项目放款冲正";
	/** 账户流水交易类型-优选项目平台服务费冲正*/
	public static final String TRADE_FLOW_TYPE_MANAGE_LOAN_REVERT = "优选项目平台服务费冲正";
	/**
	 * 账户流水交易类型-红包冲正
	 */
	public static final String TRADE_FLOW_TYPE_RED_ENVELOPE_REVERT = "红包冲正";
	/** 出借%s轻松投 */
	public static final String TRADE_FLOW_TYPE_BUY_GROUP_FREEZEN = "出借优选项目冻结";
	/** "出借优选项目解冻" */
	public static final String TRADE_FLOW_TYPE_BUY_GROUP_UNFREEZE = "出借优选项目解冻";
	/** "出借优选项目资金解冻" */
	public static final String TRADE_FLOW_TYPE_BUY_GROUP_UNFREEZE_MEMO = "出借优选项目资金解冻";
	/** 出借优选项目失败 */
	public static final String TRADE_FLOW_TYPE_REMIAN_GROUP_UNFREEZE = "出借优选项目失败";
	/**账户流水交易类型-加息券奖金发放 **/
	public static final String TRADE_FLOW_TYPE_PURCHASE_AWARD_RELEASE = "加息券奖金发放";
}
