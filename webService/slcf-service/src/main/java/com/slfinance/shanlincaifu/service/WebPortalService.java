package com.slfinance.shanlincaifu.service;

import java.text.ParseException;
import java.util.Map;
/**
 * 
 * <网贷天眼信息公示接口>
 * <功能详细描述>
 * 
 * @author  Mgp
 * @version  [版本号, 2017年6月14日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface WebPortalService {

	/**
	 * 借款数据
	 * @param params
	 * @return
	 * @throws ParseException
	 */
	Map<String, Object> loanInfoMap(Map<String,Object> params) throws ParseException;
	
	/**
	 * 投资人数据
	 * @param params
	 * @return
	 * @throws ParseException
	 */
	Map<String,Object> investInfoMap(Map<String,Object>params) throws ParseException;

	/**
	 * 校验第三方媒体帐号密码
	 *
	 * @param username 用户名
	 * @param password 密码
	 * @return 成功返回有效token 失败返回null
	 */
	Map<String, Object> token(String username, String password);

	/**
	 * 校验token是否有效
	 *
	 * @param token token
	 * @param username
	 * @return true/false
	 */
	Boolean check(String token, String username);
}
