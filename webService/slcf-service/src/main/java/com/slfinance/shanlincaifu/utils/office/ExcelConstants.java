/** 
 * @(#)ExcelConstants.java 1.0.0 2015年1月4日 下午10:30:58  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.utils.office;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * excel 工具常量 库
 * 
 * @author 孟山
 * @version $Revision:1.0.0, $Date: 2015年1月4日 下午10:30:58 $
 */
public class ExcelConstants {
	
	/**
	 * 当前未还款记录excel对应标题
	 */
	public static Map<Integer, String> REPAYMENT_TITLE_COLUMINDEX2KEY_MAP = new LinkedHashMap<Integer, String>() {
		private static final long serialVersionUID = 5343131648262124547L;
		{
			 put(0, "债权编号");
	         put(1, "债权金额(元)");
	         put(2, "持有金额(元)");
	         put(3, "持有比例");
	         put(4, "借款期限(月)");
	         put(5, "开始日期");
	         put(6, "结束日期");
	         put(7, "网贷平台");
	         put(8, "当前期数");
	         put(9, "还款日期");
	         put(10, "还款金额(元)");
	         put(11, "借款描述");
	         put(12, "还款方式");
		}
	};
	
	/**
	 * 当前未还款记录excel对应key值
	 */
	public static Map<Integer, String> REPAYMENT_COLUMINDEX2KEY_MAP = new LinkedHashMap<Integer, String>() {
		private static final long serialVersionUID = 3827820850306212023L;
		{
			put(0, "loanCode");
			put(1, "loanAmount");
			put(2, "holdAmount");
			put(3, "holdScale");
			put(4, "loanTerm");
			put(5, "investStartDate");
			put(6, "investEndDate");
			put(7, "parameterName");
			put(8, "currentTerm");
			put(9, "expectRepaymentDate");
			put(10, "repaymentTotalAmount");
			put(11, "loanDesc");
			put(12, "repaymentMethod");
		}
	};
	
	/**
	 * 线下提现导入返回excel对应标题
	 */
	public static Map<Integer, String> WITHDRAW_TITLE_COLUMINDEX2KEY_MAP = new LinkedHashMap<Integer, String>() {
		private static final long serialVersionUID = 5343131648262124547L;
		{
			 put(0, "指令类型");
	         put(1, "付款方账号");
	         put(2, "收款方户名");
	         put(3, "收款方账号");
	         put(4, "币种");
	         put(5, "金额");
	         put(6, "处理时间");
	         put(7, "处理结果");
	         put(8, "系统处理时间");
	         put(9, "系统处理结果");
	         put(10, "备注");
		}
	};
	
	/**
	 * 线下提现导入返回excel对应key值
	 */
	public static Map<Integer, String> WITHDRAW_COLUMINDEX2KEY_MAP = new LinkedHashMap<Integer, String>() {
		private static final long serialVersionUID = 3827820850306212023L;
		{
			put(0, "transferType");
			put(1, "payeeBankCardNo");
			put(2, "payeyCustName");
			put(3, "payeyBankCardNo");
			put(4, "currencyType");
			put(5, "tradeAmount");
			put(6, "fileTradeDate");
			put(7, "fileTradeStatus");
			put(8, "tradeDate");
			put(9, "tradeStatus");
			put(10, "memo");
		}
	};
}
