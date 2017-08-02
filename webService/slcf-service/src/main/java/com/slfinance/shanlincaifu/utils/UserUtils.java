package com.slfinance.shanlincaifu.utils;

import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.slfinance.vo.ResultVo;

/**
 * 用于验证用户名
 * 
 * @author zhumin
 *
 */
public class UserUtils {

	/**
	 * 用于验证登录名规则
	 * 
	 * @param loginName
	 * @return
	 */
	public static ResultVo regularLoginName(String loginName) {
		if (loginName.matches("^[\u4E00-\u9FFF]+$"))
			return new ResultVo(false, "登录名不能有汉字");
		String regex = "\\s";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(loginName);
		if (matcher.find())
			return new ResultVo(false, "登录名不能含有空格");
		return new ResultVo(true);
	}

	/**
	 * 正则表达式密码
	 * .compile("(?=.*d)(?=.*[a-zA-Z])\\s+$\\");
	 * @param password
	 * @return
	 */
	public static ResultVo regularPassword(String password) {
		Pattern pattern = Pattern
				.compile("^(?=.*\\d)(?=.*[a-zA-Z])[\\S]+$");
		Matcher matcher = pattern.matcher(password);
		if (!matcher.matches())
			return new ResultVo(false, "密码为8-16位数字或字母(大小)组合");
		return new ResultVo(true);
	}
	
	/**
	 * 判断是否可以抢购
	 *
	 * @author  wangjf
	 * @date    2015年12月4日 下午3:57:27
	 * @param map
	 */
	public static void judgeCanSale(Map<String, Object> map) {
		Integer currentHour = Integer.valueOf(DateUtils.formatDate(new Date(), "HH"));
		if(currentHour >= 8 && currentHour < 24) {
			map.put("isSale", "true");//是否可售
			map.put("whenPurchase", "抢购");//文字描述
		}
		else {
			map.put("isSale", "false");//是否可售
			map.put("whenPurchase", "8点开抢");//文字描述
		}
	}	

	/**
	 * 转换转换转让编码方式为文字
	 *
	 * @author  wangjf
	 * @date    2017年7月21日 上午11:53:00
	 * @return
	 */
	public static String convertTransferType2Word(String transferTypeCode) {
		String transferType = "";
		switch(transferTypeCode) {
		case "000" :
			transferType = "不限";
			break;
		case "001" :
			transferType = "不可转让";
			break;	
		case "010" :
			transferType = "可转让";
			break;	
		case "100" :
			transferType = "不限";
			break;	
		default:
			transferType = "不限";
		}
		return transferType;
	}
	
	/**
	 * 转换还款方式编码方式为文字
	 *
	 * @author  wangjf
	 * @date    2017年7月21日 上午11:59:52
	 * @param repaymentTypeCode
	 * @return
	 */
	public static String convertRepaymentType2Word(String repaymentTypeCode) {
		String repaymentType = "";
		switch(repaymentTypeCode) {
		case "0000" :
			repaymentType = "不限";
			break;
		case "0001" :
			repaymentType = "等额本息";
			break;	
		case "0010" :
			repaymentType = "每期还息到期付本";
			break;	
		case "0100" :
			repaymentType = "到期还本付息";
			break;	
		case "1000" :
			repaymentType = "不限";
			break;
		default:
			repaymentType = "不限";
		}
		return repaymentType;
	}
	
	/**
	 * 转换转换转让编码方式为文字
	 *
	 * @author  wangjf
	 * @date    2017年7月21日 上午11:53:00
	 * @return
	 */
	public static String convertTransferType2Code(String transferTypeWord) {
		String transferType = "";
		switch(transferTypeWord) {
		case "不可转让" :
			transferType = "001";
			break;	
		case "可转让" :
			transferType = "010";
			break;	
		case "不限" :
			transferType = "100";
			break;	
		default:
			transferType = "100";
		}
		return transferType;
	}
	
	/**
	 * 转换还款方式编码方式为文字
	 *
	 * @author  wangjf
	 * @date    2017年7月21日 上午11:59:52
	 * @param repaymentTypeCode
	 * @return
	 */
	public static String convertRepaymentType2Code(String repaymentTypeWord) {
		String repaymentType = "";
		switch(repaymentTypeWord) {
		case "等额本息":
			repaymentType = "0001";
			break;	
		case "每期还息到期付本" :
			repaymentType = "0010";
			break;	
		case "到期还本付息" :
			repaymentType = "0100";
			break;	
		case "不限" :
			repaymentType = "1000";
			break;
		}
		return repaymentType;
	}
}
