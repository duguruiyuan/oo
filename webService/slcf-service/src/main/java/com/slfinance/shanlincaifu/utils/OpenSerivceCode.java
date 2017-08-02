/** 
 * @(#)OpenSerivceCode.java 1.0.0 2015年7月2日 下午1:55:45  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.utils;

import java.util.HashMap;
import java.util.Map;

/**   
 * 开放服务编码
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月2日 下午1:55:45 $ 
 */
public enum OpenSerivceCode {
	SUCCESS							("0000", "成功"), 
	ERR_SIGN						("0001", "验签错误"),
	ERR_MOBILE_REPEATE				("0002", "手机号码重复"),
	ERR_LOGINNAME_REPEATE			("0003", "登录用户名重复"),
	ERR_MOBILE_RULE					("0004", "手机格式错误"),
	ERR_LOGINNAME_RULE				("0005", "昵称格式错误"),
	ERR_PASSWORD_RULE				("0006", "密码格式错误"),
	ERR_REQUESTTIME_RULE			("0007", "请求时间格式错误"),
	ERR_LACK_FIELD					("0008", "字段不全或缺少必填字段"),
	ERR_INVALID_CHANNEL				("0009", "不存在此接口"),
	ERR_SMSCODE_RULE        		("0010", "短信验证码格式错误"),
	ERR_VALIDATE_SMSCODE			("0011", "短信验证码校验错误"),
	ERR_INVALID_APPSOURCE			("0012", "请求来源格式错误"),
	ERR_FREQUENCY_SMSCODE			("0013", "获取短信验证码过于频繁"),
	ERR_USER_NO_DATA_FOUND			("0014", "该手机号用户不存在"),
	ERR_LOAN_NO_REPEATE   			("0023", "借款编号重复"),
	ERR_NOT_EXISTS_PROJECT			("0028", "项目不存在"),
	ERR_TRADE_NO_REPEATE			("0101", "交易编号重复"),
	ERR_NOT_EXISTS_CUST     		("0120", "客户不存在"),
	ERR_INVALID_REPAYMENT   		("0121", "还款计划总本金与借款金额不一致"),
	ERR_LOAN_NOT_EXIST   			("0122", "借款信息不存在"),
	ERR_REPAYMENTPLAN_NOT_EXIST   	("0123", "还款计划不存在"),
	ERR_OTHER						("9999", "其他异常");
	
	private String code;
	private String message;
	private OpenSerivceCode(String code, String message){
		this.code = code;
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String toString() {
		return String.format("{\"retCode\":%s, \"retMsg\":%s}", this.code, this.message);
	}
	
	public Map<String, Object> toMap() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("retCode", this.code);
		result.put("retMsg", this.message);
		return result;
	}
}
