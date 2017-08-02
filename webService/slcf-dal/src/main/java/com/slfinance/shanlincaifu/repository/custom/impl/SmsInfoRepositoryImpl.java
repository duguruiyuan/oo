/** 
 * @(#)SmsInfoRepositoryCustom.java 1.0.0 2015年5月7日 下午2:21:23  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slfinance.shanlincaifu.repository.custom.SmsInfoRepositoryCustom;

/**   
 * 
 *  
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年5月7日 下午2:21:23 $ 
 */
@Repository
public class SmsInfoRepositoryImpl implements SmsInfoRepositoryCustom{

	@Autowired
	private EntityManager entityManager;
	
	
	@SuppressWarnings("unchecked")
	public List<Object[]> findByAddressAndTypeAndDate(String address,String type,String dt,String sd,String ed){
		StringBuffer sql=new StringBuffer();
		sql.append("select 'day',count(id) from BAO_T_SMS_INFO"+
				" where target_address=? and message_type=? and (to_char(send_date,'yyyy-MM-dd')>=? and to_char(send_date,'yyyy-MM-dd')<=?)"+
				" union all"+
				" select 'month',count(id) from BAO_T_SMS_INFO"+
				" where target_address=? and message_type=? and (to_char(send_date,'yyyy-MM-dd')>=? and to_char(send_date,'yyyy-MM-dd')<=?)");
		
		Query query=entityManager.createNativeQuery(sql.toString());
		query.setParameter(1, address).setParameter(2, type).setParameter(3, dt).setParameter(4, dt)
		.setParameter(5, address).setParameter(6, type).setParameter(7, sd).setParameter(8, ed);
		
		return query.getResultList();
	}

}
