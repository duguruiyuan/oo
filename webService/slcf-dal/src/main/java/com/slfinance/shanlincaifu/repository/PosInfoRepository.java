package com.slfinance.shanlincaifu.repository;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.PosInfoEntity;


/**   
 * POS信息表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-02-23 09:44:53 $ 
 */
public interface PosInfoRepository extends PagingAndSortingRepository<PosInfoEntity, String>{
	
	/**
	 * 根据交易过程流水id查询
	 * 
	 * @author zhangt
	 * @date   2016年2月24日下午4:12:40
	 * @param tradeFlowId
	 * @param recordStatus
	 * @return
	 */
	@Query("select new Map(p.id as posId, p.posNo as posNo, p.referenceNo as referenceNo) from PosInfoEntity p where p.tradeFlowId = :tradeFlowId and p.recordStatus = :recordStatus order by p.createDate desc")
	public List<Map<String, Object>> findListByTradeFlowIdAndRecordStatus(@Param("tradeFlowId") String tradeFlowId, @Param("recordStatus") String recordStatus);
	
	
	/**
	 * 根据交易过程流水id查询
	 * 
	 * @author zhangt
	 * @date   2016年3月3日下午2:12:03
	 * @param tradeFlowId
	 * @param recordStatus
	 * @return
	 */
	public List<PosInfoEntity> findByTradeFlowIdAndRecordStatus(String tradeFlowId, String recordStatus);
	
	/**
	 * 根据流水id将pos信息置为无效
	 * 
	 * @author zhangt
	 * @date   2016年3月3日下午2:16:05
	 * @param tradeFlowId
	 * @return
	 */
	@Modifying 
	@Query("update PosInfoEntity p set p.recordStatus = '无效' where p.tradeFlowId = ? and p.recordStatus = '有效' ")
	public int updateByTradeFlowId(String tradeFlowId);
	
	/**
	 * 根据终端号+参考号+有效状态查询
	 * @author zhangt
	 * @date   2016年3月16日下午3:48:19
	 * @param posNo
	 * @param referenceNo
	 * @param recordStatus
	 * @return
	 */
	public int countByPosNoAndReferenceNoAndRecordStatus(String posNo, String referenceNo, String recordStatus);
	
	/**
	 * 监控一个POS单线上线下同时使用 线下理财系统FT_T_INVEST_EXPAND_INFO表中参考号与善林财富系统中的参考号做对比，
	 * 若有相同，则发送邮件给 zhuyishan@shanlinjinrong.com
	 */
	@Query(value = " select p.REFERENCE_NO \"peference\" "
			+ " FROM BAO_T_POS_INFO p "
			+ " INNER JOIN BAO_T_AUDIT_INFO aud ON aud.RELATE_PRIMARY = p.TRADE_FLOW_ID AND aud.RELATE_TYPE='BAO_T_TRADE_FLOW_INFO' AND aud.AUDIT_STATUS NOT IN ('初审拒绝', '复审拒绝', '终审拒绝', '拒绝') "
			+ " WHERE EXISTS ( "
			+ " 		SELECT *  "
			+ " 		  FROM FT_T_INVEST_EXPAND_INFO@SLCF_WEALTH_LINK wealthExpand "
			+ "          INNER JOIN FT_T_INVEST_INFO@SLCF_WEALTH_LINK wealthInvest ON wealthInvest.ID = wealthExpand.INVEST_ID AND wealthInvest.STATUS NOT IN ('3') " /* (type=9)0-新建 1-待审核  2-通过  3-拒绝  4-审核回退 */
			+ " 		 WHERE wealthExpand.TRADE_CODE = p.REFERENCE_NO ) "
			, nativeQuery = true)
	public List<String> countMonitorPosData();
	/**
	 * 监控一个POS单线上线下同时使用 线下理财系统FT_T_INVEST_EXPAND_INFO表中参考号与善林财富系统中的参考号做对比，
	 * 若有相同，则发送邮件给 zhuyishan@shanlinjinrong.com
	 */
	@Query(value = " select p.REFERENCE_NO \"peference\" "
			+ " FROM BAO_T_POS_INFO p "
			+ " WHERE EXISTS ( "
			+ " 		SELECT *  "
			+ " 		  FROM FT_T_INVEST_EXPAND_INFO@SLCF_WEALTH_LINK wealthInvest "
			+ " 		 WHERE wealthInvest.TRADE_CODE = p.REFERENCE_NO ) "
			+ "   AND p.create_date  > ?1 AND p.create_date <= ?2 "
			, nativeQuery = true)
	public List<String> countMonitorPosData(Date lastDate, Date nowDate);
}
