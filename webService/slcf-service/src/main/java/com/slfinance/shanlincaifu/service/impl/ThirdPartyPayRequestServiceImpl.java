/** 
 * @(#)ThirdPartyPayRequestServiceImpl.java 1.0.0 2015年4月28日 下午2:52:55  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.ThridPartyPayRequestEntity;
import com.slfinance.shanlincaifu.repository.ThridPartyPayRequestRepository;
import com.slfinance.shanlincaifu.service.ThirdPartyPayRequestService;
import com.slfinance.shanlincaifu.utils.DateUtils;

/**   
 * 第三方请求报文日志类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月28日 下午2:52:55 $ 
 */
@Service("thirdPartyPayRequestService")
public class ThirdPartyPayRequestServiceImpl implements ThirdPartyPayRequestService {

	@Autowired
	ThridPartyPayRequestRepository thridPartyPayRequestRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/***
	 * @modefiy add zhangzs
	 * @Date 2015年6月17日 下午2:52:55 
	 * 并发情况下业务处理失败，与保存请求信息无关,否则业务失败，日志无法保存。
	 */
	@Transactional(readOnly=false,propagation=Propagation.REQUIRES_NEW)
	@Override
	public void saveThirdPartyPayRequest(final ThridPartyPayRequestEntity thirdPartyPayRequest) throws SLException {
		
		StringBuffer  sql=new StringBuffer();
		sql.append("insert into bao_t_thrid_party_pay_request (create_date, exception, last_update_date, memo, request_batch_number, request_headers, request_method, request_time, request_url, response_headers, response_status_code, response_status_text, response_time, version, id, request_Body, response_Body) "
				+ "values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		jdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setTimestamp(1, new Timestamp(thirdPartyPayRequest.getCreateDate().getTime()));
				ps.setString(2, thirdPartyPayRequest.getException());
				ps.setTimestamp(3, new Timestamp(thirdPartyPayRequest.getLastUpdateDate().getTime()));
				ps.setString(4, thirdPartyPayRequest.getMemo());
				ps.setString(5, thirdPartyPayRequest.getRequestBatchNumber());
				ps.setString(6, thirdPartyPayRequest.getRequestHeaders());
				ps.setString(7, thirdPartyPayRequest.getRequestMethod());
				if(!StringUtils.isEmpty(thirdPartyPayRequest.getRequestTime())) {
					ps.setTimestamp(8, new Timestamp(DateUtils.parseDate(thirdPartyPayRequest.getRequestTime(), "yyyy-MM-dd HH:mm:ss").getTime()));
				}
				ps.setString(9, thirdPartyPayRequest.getRequestUrl());
				ps.setString(10, thirdPartyPayRequest.getResponseHeaders());
				ps.setString(11, thirdPartyPayRequest.getResponseStatusCode());
				ps.setString(12, thirdPartyPayRequest.getResponseStatusText());
				if(!StringUtils.isEmpty(thirdPartyPayRequest.getResponseTime())) {
					ps.setTimestamp(13, new Timestamp(DateUtils.parseDate(thirdPartyPayRequest.getResponseTime(), "yyyy-MM-dd HH:mm:ss").getTime()));
				}
				ps.setInt(14, thirdPartyPayRequest.getVersion());
				ps.setString(15, thirdPartyPayRequest.getId());
				ps.setString(16, thirdPartyPayRequest.getRequestBody());
				ps.setString(17, thirdPartyPayRequest.getResponseBody());
			}
		});
	}

}
