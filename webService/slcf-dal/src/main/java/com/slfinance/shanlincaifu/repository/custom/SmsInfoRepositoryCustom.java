/** 
 * @(#)SmsInfoRepositoryCustom.java 1.0.0 2015年5月7日 下午2:21:23  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

/**   
 * 
 *  
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年5月7日 下午2:21:23 $ 
 */
public interface SmsInfoRepositoryCustom {
	
	/**
	 * 根据手机号,类型,开始,结束日期查询数据
	 * @param address		手机号
	 * @param type			类型Constant.SMS_TYPE
	 * @param dt			当天日期(yyyy-MM-dd)
	 * @param sd			开始日期(yyyy-MM-dd)
	 * @param ed			结束日期(yyyy-MM-dd)
	 * @return				
	 */
	public List<Object[]> findByAddressAndTypeAndDate(String address,String type,String dt,String sd,String ed);

}
