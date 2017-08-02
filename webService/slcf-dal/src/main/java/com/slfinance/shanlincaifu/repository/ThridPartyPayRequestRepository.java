/** 
 * @(#)ThridPartyPayRequestRepository.java 1.0.0 2015年4月28日 下午12:10:45  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.slfinance.shanlincaifu.entity.ThridPartyPayRequestEntity;

/**   
 * 
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月28日 下午12:10:45 $ 
 */
@RepositoryRestResource(collectionResourceRel = "thridPartyPayRequest", path = "thridPartyPayRequests")
public interface ThridPartyPayRequestRepository extends PagingAndSortingRepository<ThridPartyPayRequestEntity, String>{

	@Modifying
	@Query(value = "insert into bao_t_thrid_party_pay_request (create_date, exception, last_update_date, memo, request_batch_number, request_headers, request_method, request_time, request_url, response_headers, response_status_code, response_status_text, response_time, version, id, request_Body, response_Body) " //
			+ "values ( ?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12, ?13, ?14, ?15, ?16, ?17)", nativeQuery = true )
	public void insert(Date createDate, String exception, Date lastUpdateDate, String memo, String requestBatchNumber, String requestHeaders, String requestMethod, Timestamp requestTime, String requestUrl, String responseHeaders, String responseStatusCode, String responseStatusText, Timestamp responseTime, Integer version, String id);//, byte[] requestBody, byte[] responseBody
	
}
