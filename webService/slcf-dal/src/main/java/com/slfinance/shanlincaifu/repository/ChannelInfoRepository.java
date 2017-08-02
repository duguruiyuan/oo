package com.slfinance.shanlincaifu.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.ChannelInfoEntity;

/**
 * 渠道表数据访问接口
 * 
 * @author Tool
 * @version $Revision:1.0.0, $Date: 2017-06-08 19:41:53 $
 */
public interface ChannelInfoRepository extends
		PagingAndSortingRepository<ChannelInfoEntity, String> {

	@Query(value = " select count(*) from BAO_T_CHANNEL_INFO t  where t.channel_no=?  and t.record_status=? ", nativeQuery = true)
	public BigDecimal getCountByChannelNo(String channelNo, String recordStatus);

	ChannelInfoEntity findByChannelNoAndRecordStatus(String channelNo,String recordStatus);

}
