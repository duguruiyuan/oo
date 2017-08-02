package com.slfinance.shanlincaifu.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.springframework.util.StringUtils;

import com.slfinance.exception.SLException;

public class SharedUtil {

	/**
	 * 获取唯一字符串 当前时间+UUID
	 * */
	public static String getUniqueString() {
		String uuid = UUID.randomUUID().toString();
		String currTime = getCurrentTime();
		StringBuffer uniqueBuffer = new StringBuffer();
		String uniqueString = uniqueBuffer.append(currTime).append(uuid).toString().replaceAll("-", "");
		return uniqueString;
	}

	/**
	 * 获取当前时间字符串yyyyMMddHHmmss
	 * */
	public static String getCurrentTime() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String currTimeString = simpleDateFormat.format(new Date());
		return currTimeString;
	}

	/**
	 * 生成交易编号
	 * 
	 * @param flowNumber
	 * @return
	 */
	public static String generateTradeNumber(long flowNumber) {
		String tradeNumber = "P2P-TRADE-" + String.format("%012d", flowNumber);
		return tradeNumber;
	}

	/**
	 * 生成报盘批次号SL-BATCH-12位流水号000000000001
	 * 
	 * @return
	 */
	public static String generateTradeBatchNumber(long flowNumber) {
		String tradeBatchNumber = "P2P-BATCH-" + String.format("%012d", flowNumber);
		return tradeBatchNumber;

	}

	/**
	 * 生成提现编号
	 * 
	 * @return
	 */
	public static String generateWithdrawNumber(long flowNumber) {
		String withdrawNumber = "SLCF-WITHDRAW-" + String.format("%012d", flowNumber);
		return withdrawNumber;
	}

	/**
	 * 生成借款合同编号, 生成规则:SLCF+yyyyMMdd+当天五位序列号,起始值:00001
	 * 
	 * @return
	 */
	public static String generateLoanContractNumber(long flowNumber) {
		String loanContractNumber = "SLCF" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + String.format("%05d", flowNumber);
		return loanContractNumber;
	}

	/**
	 * 生成客户编号, 生成规则:12位,不够位前面加零,起始值：100000000000
	 * 
	 * @return
	 */
	public static String generateCustomerNumber(long flowNumber) {
		String customerNumber = String.valueOf(flowNumber);
		return customerNumber;
	}

	/**
	 * Title: 生成六位短信数字验证码
	 * 
	 * @author caoyi
	 * @param
	 * @return 六位数字验证码
	 */
	public static String getSmsVerificationCode() {
		Random random = new Random();
		String code = "";
		for (int i = 0; i < 6; i++) {
			code += random.nextInt(10);
		}
		return code;
	}

	public static void main(String[] args) {
		// System.out.println(generateLoanContractNumber(1));
		System.out.println(generateTradeBatchNumber(100001));
	}

	/**
	 * 数据流转为字符串
	 * **/
	public static String receiveInputStream(InputStream inputStream) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		if (inputStream != null) {
			int length = 0;
			while ((length = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
			}
			outputStream.flush();
		}
		byte[] responseBytes = outputStream.toByteArray();
		String responseString = new String(responseBytes, "UTF-8");
		return responseString;
	}

	/**
	 * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
	 * @param version1
	 * @param version2
	 * @return
	 */
	public static int compareVersion(String version1, String version2) throws SLException{
		if (version1 == null || version2 == null) {  
	        throw new SLException("compareVersion error:illegal params.");  
	    }  
		
		String[] versionArray1 = version1.split("\\.");//注意此处为正则匹配，不能用"."；
		String[] versionArray2 = version2.split("\\.");
		int idx = 0;
		int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
		int diff = 0;
		while (idx < minLength) {
			if(versionArray1[idx].length() < versionArray2[idx].length()) { // 若字符串不等则后面补零
				versionArray1[idx] += "0";
			}
			else if(versionArray1[idx].length() > versionArray2[idx].length()) { // 若字符串不等则后面补零
				versionArray2[idx] += "0";
			}
			
			if((diff = versionArray1[idx].compareTo(versionArray2[idx])) != 0) { // 比较字符串
				break;
			}
			
			++idx;
		}
		//如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
		diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
		return diff;
	}
	
	/**
	 * 替换敏感词
	 *
	 * @author  wangjf
	 * @date    2016年12月9日 下午8:16:10
	 * @param words
	 * @return
	 */
	public static String replaceSpecialWord(String words){
		if(StringUtils.isEmpty(words)){
			return "";
		}
		
		if(words.length() == 15 || words.length() == 18) return words.substring(0, 2) + "******" + words.substring(words.length() - 2);
		return words.substring(0, 1) + "**";
	}
}
