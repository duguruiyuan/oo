package com.slfinance.modules.test.ext;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 扩展H2使之支持Oracle扩展函数
 * 
 * @author larry
 * 
 */
public class Function4Oracle {
	/**
	 * 将输入值转换为指定格式强制使用格式为yyyy-MM-dd
	 * 
	 * @param source
	 * @param format
	 * @return 转换后的值
	 * @throws ParseException
	 */
	public static java.sql.Date to_date(String source, String format)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return new java.sql.Date(sdf.parse(source).getTime());
	}
}
