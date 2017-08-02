/** 
 * @(#)InvestListVO.java 1.0.0 2015年4月25日 下午3:48:21  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.vo;

/**   
 * 
 *  后台投资列表查询
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年4月25日 下午3:48:21 $ 
 */
public class InvestListVO {
	private String loginName;
	private String custName;
	private String credentialsCode;
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getCredentialsCode() {
		return credentialsCode;
	}
	public void setCredentialsCode(String credentialsCode) {
		this.credentialsCode = credentialsCode;
	}
	
}
