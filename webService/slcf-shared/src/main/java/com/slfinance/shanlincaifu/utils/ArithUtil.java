package com.slfinance.shanlincaifu.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ArithUtil {

	// 默认除法运算精度
	private static final int DEF_DIV_SCALE = 18;

	/**
	 * 乘法运算。
	 * 
	 * @param v1
	 *            被乘数
	 * @param v2
	 *            乘数
	 * @return 两个参数的积
	 */
	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 提供精确的减法运算。
	 * 
	 * @param v1
	 *            被减数
	 * @param v2
	 *            减数
	 * @return 两个参数的差
	 */

	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 提供精确的加法运算。
	 * 
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
	 */

	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 提供4舍5入的加法
	 * @param v1
	 * @param v2
	 * @param scale
	 * @return
	 */
	public static double add(double v1, double v2, int scale) {

		BigDecimal b1 = new BigDecimal(Double.toString(v1));

		BigDecimal b2 = new BigDecimal(Double.toString(v2));

		return (b1.add(b2)).setScale(scale, 5).doubleValue();

	}
	
	
	/**
	 * 除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2) {
		return div(v1, v2, DEF_DIV_SCALE);
	}

	/**
	 * 除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}


	/**
	 * 
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * 
	 * @param scale
	 *            小数点后保留几位
	 * 
	 * @return 四舍五入后的结果
	 * 
	 */

	public static double round(double v, int scale) {

		if (scale < 0) {

			throw new IllegalArgumentException(

			"The scale must be a positive integer or zero");

		}

		BigDecimal b = new BigDecimal(Double.toString(v));

		BigDecimal one = new BigDecimal("1");

		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();

	}
	
	/**
	 * 4舍5入
	 * @param value
	 * @param scale
	 * @return
	 */
	public static double forScale(double value,int scale){
		if (scale < 0) {

			throw new IllegalArgumentException(

			"The scale must be a positive integer or zero");

		}
		BigDecimal b=new BigDecimal(value).setScale(scale,BigDecimal.ROUND_HALF_UP);
		return b.doubleValue();
	} 
	/**
	 * 4舍5入
	 * @param value
	 * @param scale
	 * @return
	 */
	public static double[] forScale(double value[],int scale){
		if (scale < 0) {

			throw new IllegalArgumentException(

			"The scale must be a positive integer or zero");

		}
		if(value!=null&&value.length>0){
			for(int i=0;i<value.length;i++){
				value[i]=forScale(value[i],scale);
			}
		}
		return value;
	}


	/**
	 * 补位运算。当要求保留小数位时，由scale参数指 定精度，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            补位前的值
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 补位后的值
	 */
	public static double formatScale(double v1, int scale) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		return (new BigDecimal(MarkUpZero(b1.toString(), 2))).doubleValue();
	}

	
	/**
	 * Bigdecimal截取两位小数
	 * 
	 * @param v1
	 *           截取值
	 * @return 补位后的值
	 */
	public static BigDecimal formatScale2(BigDecimal v1) {
		if(v1==null)
			return new BigDecimal("0");
		return v1.setScale(2, BigDecimal.ROUND_DOWN);
	}
	
	/**
	 * Bigdecimal截取小数
	 * 
	 * @param v1
	 *           截取值
	 * @return 补位后的值
	 */
	public static BigDecimal formatScale(BigDecimal v1,int scale) {
		if(v1==null)
			return new BigDecimal("0");
		return v1.setScale(scale, BigDecimal.ROUND_DOWN);
	}
	
	/**
	 * 字符串取小点后2位
	 * 
	 * @param str
	 *            字串(数字)
	 * @param precision
	 *            小数点位
	 * @return 取位后的字符串
	 */
	public static String MarkUpZero(String str, int precision) {
		String retVal = str;
		String maskup = "";
		if (precision == 0) {
			int idx = retVal.indexOf(".");
			if (idx == -1)
				return retVal;
			else
				return retVal.substring(0, idx);
		}
		if (precision > 0 && retVal.indexOf(".") == -1) {
			retVal += ".";
		}
		for (int i = 0; i < precision; i++) {
			maskup += "0";
		}
		int len = retVal.indexOf(".");

		return (retVal + maskup).substring(0, retVal.indexOf(".")) + (retVal + maskup).substring(retVal.indexOf("."), len + precision + 1);
	}

	/**
	 * 4位序号取自sequence
	 */
	public static String getSequenceStr(String sequence) {
		int bwStr = 4 - sequence.length();
		for (int i = 0; i < bwStr; i++) {
			sequence = "0".concat(sequence);
		}
		return sequence;
	}

	/**
	 * 舍去小数点后两位数据
	 * 
	 * @param v
	 * @return
	 */
	public static Double DoubleAccurate(Double v) {
		if (v == null) {
			return 0D;
		}
		DecimalFormat df = new DecimalFormat("####.#########################");
		String vs = df.format(v);
		String[] varr = vs.split("[.]");

		int l = 0;
		if (varr.length == 2) {
			l = varr[1].length() >= 2 ? 2 : varr[1].length();
			vs = varr[1].substring(0, l);
			return Double.valueOf(varr[0] + "." + vs);
		} else {
			return v;
		}
	}

	/**
	 * 舍去小数点后L位数据
	 * 
	 * @param v
	 * @return
	 */
	public static Double DoubleAccurate(Double v, int L) {
		if (v == null) {
			return 0D;
		}
		DecimalFormat df = new DecimalFormat("####.#########################");
		String vs = df.format(v);
		String[] varr = vs.split("[.]");

		int l = 0;
		if (varr.length == 2) {
			l = varr[1].length() >= 2 ? 2 : varr[1].length();
			vs = varr[1].substring(0, l);

			return Double.valueOf(varr[0] + "." + vs);
		} else {
			return v;
		}
	}
	/**
	 * 加法运算
	 * 
	 * @param v1
	 * 
	 * 
	 * @param v2
	 * 
	 * 
	 * @return
	 */

	public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
		if (v1 == null) {
			v1 = BigDecimal.valueOf(0);
		}
		if (v2 == null) {
			v2 = BigDecimal.valueOf(0);
		}
		BigDecimal b1 = new BigDecimal(v1.toString());

		BigDecimal b2 = new BigDecimal(v2.toString());

		return b1.add(b2);

	}
	/**
	 * 减法运算
	 * 
	 * @param v1
	 * 
	 * @param v2
	 * 
	 * @return
	 */

	public static BigDecimal sub(BigDecimal v1, BigDecimal v2) {
		v1 = v1 == null ? new BigDecimal(0) : v1;
		v2 = v2 == null ? new BigDecimal(0) : v2;

		BigDecimal b1 = new BigDecimal(v1.toString());

		BigDecimal b2 = new BigDecimal(v2.toString());

		return b1.subtract(b2);

	}
	/**
	 * 乘法运算
	 * 
	 * @param v1
	 * 
	 * 
	 * @param v2
	 * 
	 * 
	 * @return
	 */

	public static BigDecimal mul(BigDecimal v1, BigDecimal v2, int scale) {
		v1 = v1 == null ? new BigDecimal(0) : v1;
		v2 = v2 == null ? new BigDecimal(0) : v2;
		
		if (scale < 0) {

			throw new IllegalArgumentException("The scale must be a positive integer or zero");

		}

		BigDecimal b1 = new BigDecimal(v1.toString());

		BigDecimal b2 = new BigDecimal(v2.toString());

		return b1.multiply(b2).setScale(scale, BigDecimal.ROUND_DOWN); // 2017-01-18 改 由于LoanProjectServiceImpl.buyCredit的  剩余可投金额=剩余可投持有比例×PV×折价系数

	}
	
	/**
	 * 乘法运算
	 * 
	 * @param v1
	 * 
	 * 
	 * @param v2
	 * 
	 * 
	 * @return
	 */

	public static BigDecimal mul(BigDecimal v1, BigDecimal v2) {
		
		v1 = v1 == null ? new BigDecimal(0) : v1;
		v2 = v2 == null ? new BigDecimal(0) : v2;
		
		BigDecimal b1 = new BigDecimal(v1.toString());

		BigDecimal b2 = new BigDecimal(v2.toString());

		return b1.multiply(b2);

	}
	
	
	/**
	 * 除法运算
	 * 
	 * @param v1
	 * 
	 * 
	 * @param v2
	 * 
	 * 
	 * @return
	 */

	public static BigDecimal div(BigDecimal v1, BigDecimal v2) {

		return div(v1, v2, DEF_DIV_SCALE);

	}
	/**
	 * 除法运算
	 * 
	 * @param v1
	 * 
	 * 
	 * @param v2
	 * 
	 * 
	 * @param scale
	 *            精度
	 * 
	 * @return
	 */

	public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale) {
		v1 = v1 == null ? new BigDecimal(0) : v1;
		v2 = v2 == null ? new BigDecimal(0) : v2;

		if (scale < 0) {

			throw new IllegalArgumentException("The scale must be a positive integer or zero");

		}

		BigDecimal b1 = new BigDecimal(v1.toString());

		BigDecimal b2 = new BigDecimal(v2.toString());

		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP);

	}
	
	public static boolean checkIsDecimalNumber(String number){
		String NUMBER_PATTERN = "^[0-9]+(.[0-9]{1,8})?$";// 判断小数点后八位的数字的正则表达式
		Pattern p = Pattern.compile(NUMBER_PATTERN);
		Matcher m = p.matcher(number);
		return m.find();
	}
	
	/**
	 * 小数点截取取整 供前端页面展示用
	 */
	public static String subIntDecimal(Object object) {
		String resultValue = "0";
		if (object != null && StringUtils.isNotBlank(object.toString())) {
			double amount = Double.parseDouble(object.toString());
			int value = (int) Math.floor(amount);
			DecimalFormat formater = new DecimalFormat("###,###,###,##0");
			formater.setMaximumFractionDigits(0);
			formater.setGroupingSize(3);
			resultValue = formater.format(value);
		}
		return resultValue;
	}
	
	/**
	 * 将数字格式化为百分比格式，如0.1801——>18.01%
	 *
	 * @author  wangjf
	 * @date    2016年1月21日 上午10:11:12
	 * @param val
	 * @return
	 */
	public static String formatPercent(BigDecimal val) {
		return formatPercent(val, 2);
	}
	
	/**
	 * 将数字格式化为百分比格式，如0.1801——>18.01%
	 *
	 * @author  wangjf
	 * @date    2016年3月7日 下午9:54:54
	 * @param val 
	 * @param fractionDigits小数位数
	 * @return
	 */
	public static String formatPercent(BigDecimal val, int fractionDigits) {
		if(val == null) {
			val = BigDecimal.ZERO;
		}
		NumberFormat ft = NumberFormat.getPercentInstance();
		ft.setMinimumFractionDigits(fractionDigits);
		return ft.format(val);
	}
	
	/**
	 * 将数字格式化为百分比格式，如0.1801——>18.01%（有多少小数保留多少小数）
	 *
	 * @author  wangjf
	 * @date    2016年12月12日 下午4:01:58
	 * @param val
	 * @param minFractionDigits
	 * @param maxFractionDigits
	 * @return
	 */
	public static String formatPercent2(BigDecimal val, int minFractionDigits, int maxFractionDigits) {
		if(val == null) {
			val = BigDecimal.ZERO;
		}
		NumberFormat ft = NumberFormat.getPercentInstance();
		ft.setMinimumFractionDigits(minFractionDigits);
		ft.setMaximumFractionDigits(maxFractionDigits);
		return ft.format(val);
	}
	
/*	public static void main(String[] args) {
		BigDecimal val1 = new BigDecimal("0.1230");
		System.out.println(formatPercent2(val1, 1, 5));
		BigDecimal val2 = new BigDecimal("0.12340");
		System.out.println(formatPercent2(val2, 1, 5));
		BigDecimal val3 = new BigDecimal("0.123450");
		System.out.println(formatPercent2(val3, 1, 5));
	}*/
	
	/**
	 * 比较大小
	 *
	 * @author  wangjf
	 * @date    2016年1月23日 上午11:54:40
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static int compare(BigDecimal v1, BigDecimal v2) {
		v1 = v1 == null ? BigDecimal.ZERO : v1;
		v2 = v2 == null ? BigDecimal.ZERO : v2;
		return v1.compareTo(v2);
	}
	
	/**
	 * 格式化输出
	 *
	 * @author  wangjf
	 * @date    2016年3月7日 下午9:21:15
	 * @param object
	 * @return
	 */
	public static String formatNumber(Object object) {
		String resultValue = "0";
		if (object != null && StringUtils.isNotBlank(object.toString())) {
			double amount = Double.parseDouble(object.toString());
			
			DecimalFormat formater = new DecimalFormat("###,###,###,##0.00");
			formater.setRoundingMode(RoundingMode.FLOOR);
			formater.setMaximumFractionDigits(2);
			formater.setGroupingSize(3);
			resultValue = formater.format(amount);
		}
		return resultValue;
	}
	
	/**
	 * 判断是否整除
	 * @param investMinAmount 被除数
	 * @param increaseAmount 除数
	 * @return
	 */
	public static boolean isDivInt(BigDecimal investMinAmount, BigDecimal increaseAmount){
		
		BigDecimal multiple = ArithUtil.div(increaseAmount, investMinAmount);
		BigDecimal mil = ArithUtil.formatScale(multiple, 0); 
		if(multiple.compareTo(mil) > 0) {
			return false;
		}
		return true;
	}
}
