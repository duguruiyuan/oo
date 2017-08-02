/** 
 * @(#)CommissionInfoRepositoryImpl.java 1.0.0 2015年8月24日 下午7:18:59  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.*;


import com.google.common.collect.Sets;
import com.slfinance.shanlincaifu.utils.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.shanlincaifu.entity.BusinessImportInfoEntity;
import com.slfinance.shanlincaifu.entity.CommissionInfoEntity;
import com.slfinance.shanlincaifu.repository.BusinessImportInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductInfoRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.CommissionInfoRepositoryCustom;

/**   
 * 自定义提成数据实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月24日 下午7:18:59 $ 
 */
@Repository
public class CommissionInfoRepositoryImpl implements
		CommissionInfoRepositoryCustom {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private ProductInfoRepository productInfoRepository;
	
	private String DEFAUTL_FORMATER = "yyyyMM",DEFAUTL_FORMATER_NORMAL = "yyyyMMdd";
	
	@Autowired
	private BusinessImportInfoRepository businessImportInfoRepository;

	@Override
	public void batchInsert(CommissionInfoEntity commissionInfoEntity) {
		
		// 插入提成表
		StringBuffer sqlString = new StringBuffer()
		.append(" insert into bao_t_commission_info ")
		.append("      (id, cust_id, comm_date,  ")
		.append("       invest_amount, commission_amount, reward_amount,  ")
		.append("       trade_status, record_status, create_user,  ")
		.append("       create_date, last_update_user, last_update_date,  ")
		.append("       version, memo, product_type_id, comm_month) ")
		.append("    select ")
		.append("      sys_guid(), custId, ?,  ")
		.append("      NVL(investAmount, 0), NVL(income, 0), NVL(award, 0),  ")
		.append("      ?, '有效', ?,  ")
		.append("      ?, ?, ?,  ")
		.append("      0, null, ?, ? ")
		.append("    from  ")
		.append("      (  ")
		.append("        select custId,  ")
		.append("         (select trunc(year_rate/365*p.investAmount, 8)  ")
		.append("            from bao_t_product_rate_info a  ")
		.append("           where a.product_id = ?  ")
		.append("             and p.investAmount between a.lower_limit_day and  ")
		.append("                 a.upper_limit_day) income,  ")
		.append("         (select trunc(award_rate/365*p.investAmount, 8)  ")
		.append("            from bao_t_product_rate_info a  ")
		.append("           where a.product_id = ?  ")
		.append("             and p.investAmount between a.lower_limit_day and  ")
		.append("                 a.upper_limit_day) award,  ")
		.append("          investAmount  ")
		.append("          from ( ")
		.append("                 select q.custId, sum(q.investAmount) investAmount ")
		.append("                 from ( ")
		.append("                     select s.cust_id custId, sum(i.account_available_value) investAmount  ")
		.append("                      from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_sub_account_info i  ")
		.append("                     where t.cust_id = s.quilt_cust_id and t.id = i.relate_primary  ")
		.append("                       and t.product_id in  ")
		.append("                           (select id  ")
		.append("                              from bao_t_product_info n  ")
		.append("                             where n.product_type in  ")
		.append("                                   (select id  ")
		.append("                                      from bao_t_product_type_info m  ")
		.append("                                     where m.type_name = ?))  ")
		.append("                       and t.invest_status = ?  ")
		//.append("                       and s.start_date > add_months(sysdate, -12 * 2)   ")
		.append("                       and s.record_status = '有效' ")
		.append("                       and t.cust_id not in (select id  ")
		.append("                                             from bao_t_cust_info  ")
		.append("                                             where is_recommend = '是')  ")
		.append("                     group by s.cust_id ") // 此部分为被推荐人在投
		.append("                     union all ")
		.append("                     select t.cust_id custId, sum(i.account_available_value) investAmount  ")
		.append("                      from bao_t_invest_info t, bao_t_sub_account_info i ")
		.append("                     where t.id = i.relate_primary ")
		.append("                     and   t.cust_id in (select id  ")
		.append("                                         from bao_t_cust_info  ")
		.append("                                         where is_recommend = '是')  ")
		.append("                     and t.product_id in  ")
		.append("                           (select id  ")
		.append("                              from bao_t_product_info n  ")
		.append("                             where n.product_type in  ")
		.append("                                   (select id  ")
		.append("                                      from bao_t_product_type_info m  ")
		.append("                                     where m.type_name = ?))  ")
		.append("                     and t.invest_status = ?  ")		
		.append("                     group by t.cust_id  ") // 此部分为推荐人在投
		.append("                 ) q ")
		.append("                 group by q.custId ")
		.append("               ) p   ")
		.append("          ) b   ");
		
		// 插入提成明细
		jdbcTemplate.update(sqlString.toString(), 
				new Object[]{	commissionInfoEntity.getCommDate(),
								commissionInfoEntity.getTradeStatus(),
								commissionInfoEntity.getCreateUser(),
								commissionInfoEntity.getCreateDate(),
								commissionInfoEntity.getLastUpdateUser(),
								commissionInfoEntity.getLastUpdateDate(),
								commissionInfoEntity.getProductTypeId(),
								commissionInfoEntity.getCommMonth(),
								Constant.PRODUCT_ID_GOLD_01,
								Constant.PRODUCT_ID_GOLD_01,
								Constant.PRODUCT_TYPE_01,
								Constant.VALID_STATUS_VALID,
								Constant.PRODUCT_TYPE_01,
								Constant.VALID_STATUS_VALID} );
		
		sqlString = new StringBuffer()
		.append("   insert into bao_t_commission_detail_info ")
		.append("      (id, commission_id, quilt_cust_id,  ")
		.append("       invest_amount, commission_amount, reward_amount,  ")
		.append("       trade_status, record_status, create_user,  ")
		.append("       create_date, last_update_user, last_update_date,  ")
		.append("       version, memo, product_id) ")
		.append("    select ")
		.append("      sys_guid(), c.id, b.quiltCustId,  ")
		.append("       NVL(b.investAmount, 0), NVL(b.income, 0), NVL(b.award, 0),  ")
		.append("       ?, '有效', ?,  ")
		.append("       ?, ?, ?,  ")
		.append("       0, null, ? ")
		.append("       from ")
		.append("       ( ")
		.append("        select custId,  ")
		.append("         quiltCustId,  ")
		.append("         (select trunc(year_rate/365*p.investAmount, 8)  ")
		.append("            from bao_t_product_rate_info a  ")
		.append("           where a.product_id = ?  ")
		.append("             and p.investAmount between a.lower_limit_day and  ")
		.append("                 a.upper_limit_day) income,  ")
		.append("         (select trunc(award_rate/365*p.investAmount, 8)  ")
		.append("            from bao_t_product_rate_info a  ")
		.append("           where a.product_id = ?  ")
		.append("             and p.investAmount between a.lower_limit_day and  ")
		.append("                 a.upper_limit_day) award,  ")
		.append("          investAmount  ")
		.append("          from (")
		.append("          select custId, quiltCustId, sum(investAmount) investAmount ")
		.append("          from ( ")
		.append("                 select s.cust_id custId, s.quilt_cust_id quiltCustId, sum(i.account_available_value) investAmount  ")
		.append("                  from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_sub_account_info i  ")
		.append("                 where t.cust_id = s.quilt_cust_id and t.id = i.relate_primary ")
		.append("                   and t.product_id in  ")
		.append("                       (select id  ")
		.append("                          from bao_t_product_info n  ")
		.append("                         where n.product_type in  ")
		.append("                               (select id  ")
		.append("                                  from bao_t_product_type_info m  ")
		.append("                                 where m.type_name = ?))  ")
		.append("                   and t.invest_status = ?  ")
		//.append("                   and s.start_date > add_months(sysdate, -12 * 2)  ")
		.append("                   and s.record_status = '有效' ")
		.append("                   and t.cust_id not in (select id  ")
		.append("                                         from bao_t_cust_info  ")
		.append("                                         where is_recommend = '是')  ")
		.append("                 group by s.cust_id, s.QUILT_CUST_ID ") // 此部分为被推荐人在投
		.append("                 union all ")
		.append("                 select t.cust_id custId, t.cust_id quiltCustId, sum(i.account_available_value) investAmount  ")
		.append("                  from bao_t_invest_info t, bao_t_sub_account_info i  ")
		.append("                 where t.id = i.relate_primary ")
		.append("                 and t.cust_id in (select id  ")
		.append("                                   from bao_t_cust_info  ")
		.append("                                   where is_recommend = '是')  ")
		.append("                 and t.product_id in  ")
		.append("                       (select id  ")
		.append("                          from bao_t_product_info n  ")
		.append("                         where n.product_type in  ")
		.append("                               (select id  ")
		.append("                                  from bao_t_product_type_info m  ")
		.append("                                 where m.type_name = ?))  ")
		.append("                 and t.invest_status = ?  ")		
		.append("                 group by t.cust_id  ") // 此部分为被推荐人在投
		.append("                 ) q   ")
		.append("                 group by custId, quiltCustId ")
		.append("              ) p   ")
		.append("           ) b, bao_t_commission_info c  ")
		.append("           where b.custId = c.cust_id and trunc(c.comm_date) = trunc(?)");
		
		jdbcTemplate.update(sqlString.toString(), 
				new Object[]{	commissionInfoEntity.getTradeStatus(),
								commissionInfoEntity.getCreateUser(),
								commissionInfoEntity.getCreateDate(),
								commissionInfoEntity.getLastUpdateUser(),
								commissionInfoEntity.getLastUpdateDate(),
								productInfoRepository.findProductInfoByProductTypeName(Constant.PRODUCT_TYPE_01).getId(),
								Constant.PRODUCT_ID_GOLD_01,
								Constant.PRODUCT_ID_GOLD_01,
								Constant.PRODUCT_TYPE_01,
								Constant.VALID_STATUS_VALID,
								Constant.PRODUCT_TYPE_01,
								Constant.VALID_STATUS_VALID,
								commissionInfoEntity.getCommDate()} );
	}

	@Override
	public List<Map<String, Object>> findTransfer(Map<String, Object> params) {
		StringBuffer sqlString = new StringBuffer()
		.append(" select t.cust_id \"custId\", s.id \"accountId\",  s.account_total_amount \"accountTotalAmount\", s.account_available_amount \"accountAvailableAmount\", s.account_freeze_amount \"accountFreezeAmount\",  ")
		.append("      s.version \"accountVersion\", ")
		.append("      NVL(sum(t.commission_amount), 0) \"commissionAmount\",  NVL(sum(t.reward_amount), 0) \"rewardAmount\" ")
		.append(" from bao_t_commission_info t, bao_t_account_info s ")
		.append(" where t.cust_id = s.cust_id and t.trade_status = ?  ")
		.append(" and t.comm_date < ? and product_type_id = ? ")
		.append(" group by t.cust_id, s.id, s.account_total_amount, s.account_available_amount, s.account_freeze_amount, s.version ");
		
		return repositoryUtil.queryForMap(sqlString.toString(), new Object[] { Constant.USER_ACTIVITY_TRADE_STATUS_06, DateUtils.getMonthStartDate((Date)params.get("date")), (String)params.get("productTypeId")});
	}

	@Override
	public void batchTermInsert(CommissionInfoEntity commissionInfoEntity) {
			
		// 插入提成表
		StringBuffer sqlString = new StringBuffer()
		.append(" insert into bao_t_commission_info ")
		.append("      (id, cust_id, comm_date,  ")
		.append("       invest_amount, commission_amount, reward_amount,  ")
		.append("       trade_status, record_status, create_user,  ")
		.append("       create_date, last_update_user, last_update_date,  ")
		.append("       version, memo, year_invest_amount, ")
		.append("       comm_month, product_type_id)	")
		.append("    select ")
		.append("      sys_guid(), custId, ?,  ")
		.append("      NVL(investAmount, 0), NVL(income, 0), NVL(award, 0),  ")
		.append("      ?, '有效', ?,  ")
		.append("      ?, ?, ?,  ")
		.append("      0, null, NVL(commissionInvestAmount, 0), ")
		.append("      ?, ? ")
		.append("    from  ")
		.append("      (  ")
		.append("        select custId,  ")
		.append("         0 income,  ")
		.append("         0 award,  ")
		.append("          trunc(investAmount, 8) investAmount, ")
		.append("          trunc(commissionInvestAmount, 8) commissionInvestAmount ")
		.append("          from ( ")
		.append("                select q.custId, sum(q.investAmount) investAmount, sum(q.commissionInvestAmount) commissionInvestAmount ")
		.append("                from (  ")
		.append("                    select s.cust_id custId, t.product_id productId, sum(t.invest_amount) investAmount,")
		.append("                    sum(t.invest_amount*m.type_term/12) commissionInvestAmount   ")
		.append("                     from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_product_info m  ")
		.append("                    where t.cust_id = s.quilt_cust_id and t.product_id = m.id ")
		.append("                      and t.product_id in   ")
		.append("                          (select id   ")
		.append("                             from bao_t_product_info n   ")
		.append("                            where n.product_type in   ")
		.append("                                  (select id   ")
		.append("                                     from bao_t_product_type_info m   ")
		.append("                                    where m.type_name = ?))   ")
		//.append("                      and s.start_date > add_months(sysdate, -12 * 2)    ")
		.append("                      and t.invest_date >= ? and t.invest_date < ? ")
		.append("                      and s.record_status = '有效' ")
		.append("                      and t.cust_id not in (select id   ")
		.append("                                            from bao_t_cust_info   ")
		.append("                                            where is_recommend = '是' )   ")
		.append("                    group by s.cust_id, t.product_id    ")
		.append("                    union all ")
		.append("                    select t.cust_id custId, t.product_id productId, sum(t.invest_amount) investAmount,")
		.append("                    sum(t.invest_amount*m.type_term/12) commissionInvestAmount   ")
		.append("                     from bao_t_invest_info t, bao_t_product_info m    ")
		.append("                    where t.product_id = m.id ")
		.append("                    and t.cust_id in (select id   ")
		.append("                                      from bao_t_cust_info   ")
		.append("                                      where is_recommend = '是' )   ")
		.append("                    and t.product_id in   ")
		.append("                          (select id   ")
		.append("                             from bao_t_product_info n   ")
		.append("                            where n.product_type in   ")
		.append("                                  (select id   ")
		.append("                                     from bao_t_product_type_info m   ")
		.append("                                    where m.type_name = ?))   ")
		.append("                    and t.invest_date >= ? and t.invest_date < ? ")
		.append("                    group by t.cust_id, t.product_id    ")
		.append("                ) q  ")
		.append("                group by q.custId  ")
		.append("               ) p   ")
		.append("          ) b   ");
		
		// 插入提成明细
		jdbcTemplate.update(sqlString.toString(), 
				new Object[]{	commissionInfoEntity.getCommDate(),
								commissionInfoEntity.getTradeStatus(),
								commissionInfoEntity.getCreateUser(),
								commissionInfoEntity.getCreateDate(),
								commissionInfoEntity.getLastUpdateUser(),
								commissionInfoEntity.getLastUpdateDate(),
								commissionInfoEntity.getCommMonth(),
								commissionInfoEntity.getProductTypeId(),
								Constant.PRODUCT_TYPE_04,
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
								Constant.PRODUCT_TYPE_04,
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd")} );
		
		sqlString = new StringBuffer()
		.append("   insert into bao_t_commission_detail_info ")
		.append("      (id, commission_id, quilt_cust_id,  ")
		.append("       invest_amount, commission_amount, reward_amount,  ")
		.append("       trade_status, record_status, create_user,  ")
		.append("       create_date, last_update_user, last_update_date,  ")
		.append("       version, memo, product_id, " )
		.append("       year_invest_amount, invest_id) ")
		.append("    select ")
		.append("      sys_guid(), c.id, b.quiltCustId,  ")
		.append("       NVL(b.investAmount, 0), NVL(b.income, 0), NVL(b.award, 0),  ")
		.append("       ?, '有效', ?,  ")
		.append("       ?, ?, ?,  ")
		.append("       0, null, productId, ")
		.append("       NVL(commissionInvestAmount, 0), investId ")
		.append("       from ")
		.append("       ( ")
		.append("        select custId,  ")
		.append("         quiltCustId,  ")
		.append("         productId,  ")
		.append("         investId,  ")
		.append("         0 income,  ")
		.append("         0 award,  ")
		.append("         trunc(investAmount, 8) investAmount, ")
		.append("         trunc(commissionInvestAmount, 8) commissionInvestAmount ")
		.append("         from ( ")
		.append("                select q.custId, q.quiltCustId, q.productId, q.investId, sum(q.investAmount) investAmount, sum(q.commissionInvestAmount) commissionInvestAmount ")
		.append("                from (  ")
		.append("                    select s.cust_id custId, s.quilt_cust_id quiltCustId, t.product_id productId, t.id investId, sum(t.invest_amount) investAmount,")
		.append("                    sum(t.invest_amount*m.type_term/12) commissionInvestAmount   ")
		.append("                     from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_product_info m  ")
		.append("                    where t.cust_id = s.quilt_cust_id and t.product_id = m.id ")
		.append("                      and t.product_id in   ")
		.append("                          (select id   ")
		.append("                             from bao_t_product_info n   ")
		.append("                            where n.product_type in   ")
		.append("                                  (select id   ")
		.append("                                     from bao_t_product_type_info m   ")
		.append("                                    where m.type_name = ?))   ")
		.append("                      and t.invest_date >= ? and t.invest_date < ? ")
		//.append("                      and s.start_date > add_months(sysdate, -12 * 2)    ")
		.append("                      and s.record_status = '有效' ")
		.append("                      and t.cust_id not in (select id   ")
		.append("                                            from bao_t_cust_info   ")
		.append("                                            where is_recommend = '是' )   ")
		.append("                    group by s.cust_id, s.quilt_cust_id, t.product_id, t.id  ")
		.append("                    union all ")
		.append("                    select t.cust_id custId, t.cust_id quiltCustId, t.product_id productId, t.id investId, sum(t.invest_amount) investAmount,")
		.append("                    sum(t.invest_amount*m.type_term/12) commissionInvestAmount   ")
		.append("                     from bao_t_invest_info t, bao_t_product_info m    ")
		.append("                    where t.product_id = m.id ")
		.append("                    and t.cust_id in (select id   ")
		.append("                                      from bao_t_cust_info   ")
		.append("                                      where is_recommend = '是' )   ")
		.append("                    and t.product_id in   ")
		.append("                          (select id   ")
		.append("                             from bao_t_product_info n   ")
		.append("                            where n.product_type in   ")
		.append("                                  (select id   ")
		.append("                                     from bao_t_product_type_info m   ")
		.append("                                    where m.type_name = ?))   ")
		.append("                    and t.invest_date >= ? and t.invest_date < ? ")
		.append("                    group by t.cust_id, t.product_id, t.id  ")
		.append("                ) q  ")
		.append("                group by q.custId, q.quiltCustId, q.productId, q.investId ")
		.append("               ) p   ")
		.append("           ) b, bao_t_commission_info c  ")
		.append("           where b.custId = c.cust_id and trunc(c.comm_date) = trunc(?) and c.comm_month = ?");
		
		jdbcTemplate.update(sqlString.toString(), 
				new Object[]{	commissionInfoEntity.getTradeStatus(),
								commissionInfoEntity.getCreateUser(),
								commissionInfoEntity.getCreateDate(),
								commissionInfoEntity.getLastUpdateUser(),
								commissionInfoEntity.getLastUpdateDate(),
								Constant.PRODUCT_TYPE_04,
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
								Constant.PRODUCT_TYPE_04,
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
								commissionInfoEntity.getCommDate(),
								commissionInfoEntity.getCommMonth()} );	
	}

	
	/**
	 * 分页查询提成信息
	 * @param custId
	 * @param nowDate
	 * @return
	 */
	@Override
	public Map<String, Object> findCommissInfoPage(String custId,Date nowDate,String typeName,String startDate,String endDate,int pageNum,int pageSize){
		List<Object> objList = new ArrayList<Object>();//参数信息
		
		String currDate = DateFormatUtils.format(nowDate, DEFAUTL_FORMATER);
		boolean queryCurr = false;
		if( (StringUtils.isNotEmpty(startDate) && StringUtils.isEmpty(endDate) &&( startDate.replaceAll("-", "").equals(DateFormatUtils.format(nowDate, "yyyyMM")) || DateUtils.parseDate((String)startDate.replaceAll("-", ""), "yyyyMM").before(DateUtils.parseDate(DateFormatUtils.format(nowDate, "yyyyMM"),"yyyyMM") )) )
				||(StringUtils.isEmpty(startDate) && StringUtils.isNotEmpty(endDate) && ( endDate.replaceAll("-", "").equals(DateFormatUtils.format(nowDate, "yyyyMM")) || DateUtils.parseDate((String)endDate.replaceAll("-", ""), "yyyyMM").after(DateUtils.parseDate(DateFormatUtils.format(nowDate, "yyyyMM"),"yyyyMM") )))
				||(StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate))
				||(StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate) && startDate.equals(endDate) && startDate.equals(DateFormatUtils.format(nowDate, "yyyy-MM")))
				||(StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate) && !startDate.equals(endDate) &&
						!DateUtils.parseDate((String)endDate.replaceAll("-", ""), "yyyyMM").before(DateUtils.parseDate(DateFormatUtils.format(nowDate, "yyyyMM"),"yyyyMM") ) &&
						( 
						  DateUtils.parseDate((String)startDate.replaceAll("-", ""), "yyyyMM").equals(DateUtils.parseDate(DateFormatUtils.format(nowDate, "yyyyMM"),"yyyyMM") )|| 
						  DateUtils.parseDate((String)endDate.replaceAll("-", ""), "yyyyMM").equals(DateUtils.parseDate(DateFormatUtils.format(nowDate, "yyyyMM"),"yyyyMM") )|| 
						  DateUtils.parseDate((String)endDate.replaceAll("-", ""), "yyyyMM").after(DateUtils.parseDate(DateFormatUtils.format(nowDate, "yyyyMM"),"yyyyMM") )))){
					queryCurr = true;
				}
			
		StringBuilder sql = new StringBuilder("select t.id as \"id\",t.comm_month as \"month\" ,t.year_invest_amount as \"monthInvestAmount\" from bao_t_commission_info t where t.product_type_id =");
		sql.append(" ( select id from bao_t_product_type_info  where type_name = ? and rownum = 1 ) and t.cust_id = ? and (to_number(t.comm_month) between ? and ?)");
		objList.add(typeName);
		objList.add(custId);
		objList.add(DateFormatUtils.format(StringUtils.isEmpty(startDate) ? org.apache.commons.lang3.time.DateUtils.addYears(new Date(), -100) : DateTime.parse(startDate).toDate(), DEFAUTL_FORMATER));
		objList.add(DateFormatUtils.format(StringUtils.isEmpty(endDate) ? org.apache.commons.lang3.time.DateUtils.addYears(new Date(), 100) : DateTime.parse(endDate).toDate(), DEFAUTL_FORMATER));
		
		StringBuilder currSql = new StringBuilder();
		if(queryCurr){
			currSql.append(" select  '0' id,  ")
			.append("'").append(currDate).append("'").append(" \"month\",  ")
			.append("          sum(trunc(commissionInvestAmount, 8)) \"monthInvestAmount\" ")
			.append("          from ( ")
			.append("                select q.custId, sum(q.investAmount) investAmount, sum(q.commissionInvestAmount) commissionInvestAmount ")
			.append("                from (  ")
			.append("                    select t.cust_id custId, t.product_id productId, sum(t.invest_amount) investAmount,")
			.append("                    sum(t.invest_amount*m.type_term/12) commissionInvestAmount   ")
			.append("                     from bao_t_invest_info t, bao_t_product_info m    ")
			.append("                    where t.product_id = m.id ")
			.append("                    and t.cust_id in (select id   ")
			.append("                                      from bao_t_cust_info   ")
			.append("                                      where (id = ? and IS_RECOMMEND = '是') ")
			.append("                                      or ( INVITE_ORIGIN_ID = ? and (IS_RECOMMEND is null or IS_RECOMMEND != '是')) )   ")
			.append("                    and t.product_id in   ")
			.append("                          (select id   ")
			.append("                             from bao_t_product_info n   ")
			.append("                            where n.product_type in   ")
			.append("                                  (select id   ")
			.append("                                     from bao_t_product_type_info m   ")
			.append("                                    where m.type_name = ?))   ")
			.append("                    and t.invest_date >= ? and t.invest_date <= ? ")
			.append("                    group by t.cust_id, t.product_id    ")
			.append("                ) q  ")
			.append("                group by q.custId  ")
			.append("               ) p   ");
			
			objList.add(custId);
			objList.add(custId);
			objList.add(typeName);
			objList.add(DateUtils.getFirstDay(nowDate, DEFAUTL_FORMATER_NORMAL));
			objList.add(DateUtils.getLastDay(nowDate,DEFAUTL_FORMATER_NORMAL));
			sql.append(" union all ").append(currSql);
		}
		
		Page<Map<String,Object>> page = repositoryUtil.queryForPageMap(sql.append(" order by \"month\" desc").toString(), objList.toArray(), pageNum, pageSize);
		if(page != null && page.getTotalElements() != 0 )
			return PageFuns.pageVoToMap(page);
		
		return Maps.newHashMap();
	}
	
	/**
	 * 分页查询提成信息详情
	 * @param custId
	 * @param nowDate
	 * @return
	 */
	@Override
	public Map<String, Object> findCommissDetailPage(String id,String custId,Date nowDate, String typeName, int pageNum, int pageSize) {
		List<Object> objList = new ArrayList<Object>();//参数信息
		StringBuilder sql = new StringBuilder();
		switch (id) {
		case "0":
		sql.append("          select custName \"custName\",productName \"productName\",currTerm as \"currTerm\", trunc(commissionInvestAmount, 8) \"yearInvestAmount\", ");
		sql.append("'").append(DateFormatUtils.format(nowDate, DEFAUTL_FORMATER)).append("'").append(" \"month\" ");
		sql.append("           from (select q.productName, 															 ");
		sql.append("                        q.custName,q.currTerm,q.createDate,						                     ");
		sql.append("                        sum(q.commissionInvestAmount) commissionInvestAmount                     ");
		sql.append("                   from (select m.product_name productName,                                      ");
		sql.append("                                u.cust_name custName,t.curr_term currTerm,t.create_date createDate,                       ");
		sql.append("                                sum(t.invest_amount * m.type_term / 12) commissionInvestAmount   ");
		sql.append("                           from bao_t_invest_info         t,                                     ");
		sql.append("                                bao_t_cust_recommend_info s,                                     ");
		sql.append("                                bao_t_product_info        m,                                     ");
		sql.append("                                bao_t_cust_info           u                                      ");
		sql.append("                          where t.cust_id = s.quilt_cust_id                                      ");
		sql.append("                            and t.product_id = m.id                                              ");
		sql.append("                            and u.id = t.cust_id                                                 ");
		sql.append("                            and t.product_id in                                                  ");
		sql.append("                                (select id                                                       ");
		sql.append("                                   from bao_t_product_info n                                     ");
		sql.append("                                  where n.product_type in                                        ");
		sql.append("                                        (select id                                               ");
		sql.append("                                           from bao_t_product_type_info m                        ");
		sql.append("                                          where m.type_name = ?))                         ");
		sql.append("                            and t.invest_date >= ?			                                      ");
		sql.append("                            and t.invest_date <= ?                                      ");
		//sql.append("                            and s.start_date > add_months(sysdate, -12 * 2)                      ");
		sql.append("                            and s.record_status = '有效'                                         ");
		sql.append("                            and t.cust_id  in                                                 ");
		sql.append("                                (select id                                                       ");
		sql.append("                                   from bao_t_cust_info                                          ");
		sql.append("                                      where ( INVITE_ORIGIN_ID = ? and (IS_RECOMMEND is null or IS_RECOMMEND != '是')   ) )  ");
		sql.append("                          group by t.id, u.cust_name, m.product_name,t.curr_term,t.create_date                 ");
		
		sql.append("  union all ");
		
		sql.append("                   		select m.product_name productName,                                      ");
		sql.append("                                u.cust_name custName,t.curr_term currTerm,t.create_date createDate,                       ");
		sql.append("                                sum(t.invest_amount * m.type_term / 12) commissionInvestAmount   ");
		sql.append("                           from bao_t_invest_info         t,                                     ");
		sql.append("                                bao_t_product_info        m,                                     ");
		sql.append("                                bao_t_cust_info           u                                      ");
		sql.append("                          where t.product_id = m.id                                              ");
		sql.append("                            and u.id = t.cust_id                                                 ");
		sql.append("                            and t.product_id in                                                  ");
		sql.append("                                (select id                                                       ");
		sql.append("                                   from bao_t_product_info n                                     ");
		sql.append("                                  where n.product_type in                                        ");
		sql.append("                                        (select id                                               ");
		sql.append("                                           from bao_t_product_type_info m                        ");
		sql.append("                                          where m.type_name = ?))                         ");
		sql.append("                            and t.invest_date >= ?			                                      ");
		sql.append("                            and t.invest_date <= ?                                      ");
		sql.append("                            and t.cust_id  in                                                 ");
		sql.append("                                (select id                                                       ");
		sql.append("                                   from bao_t_cust_info                                          ");
		sql.append("                                      where (id = ? and IS_RECOMMEND = '是') ");
		sql.append("                                       )  ");
		sql.append("                          group by t.id, u.cust_name, m.product_name,t.curr_term,t.create_date                 ");
		
		sql.append("  ) q                          ");
		sql.append("                  group by q.custName, q.productName,q.currTerm,q.createDate) p order by p.createDate desc                                ");
			
		objList.add(typeName);
		objList.add(DateUtils.getFirstDay(nowDate, DEFAUTL_FORMATER_NORMAL));
		objList.add(DateUtils.getLastDay(nowDate, DEFAUTL_FORMATER_NORMAL));
		objList.add(custId);
		
		objList.add(typeName);
		objList.add(DateUtils.getFirstDay(nowDate, DEFAUTL_FORMATER_NORMAL));
		objList.add(DateUtils.getLastDay(nowDate, DEFAUTL_FORMATER_NORMAL));
		objList.add(custId);
			break;
		default:
			sql.append("select c.comm_month as \"month\",u.cust_name as \"custName\",p.product_name as \"productName\",cd.year_invest_amount as \"yearInvestAmount\",o.curr_term as \"currTerm\" ");
			sql.append(" from bao_t_commission_info c,bao_t_commission_detail_info cd,bao_t_cust_info u,bao_t_product_info p,bao_t_invest_info o ");          
			sql.append(" where p.id = cd.product_id and cd.quilt_cust_id = u.id  and c.id = cd.commission_id and cd.invest_id = o.id and c.product_type_id = ");
			sql.append("  (select id from bao_t_product_type_info   where type_name = ? and rownum = 1) and c.cust_id = ? and c.id = ? ");
			sql.append(" order by o.create_date desc");			
			objList.add(typeName);
			objList.add(custId);
			objList.add(id);
			break;
		}
		
		Page<Map<String,Object>> page = repositoryUtil.queryForPageMap(sql.toString(), objList.toArray(), pageNum, pageSize);
		if(page != null && page.getTotalElements() != 0 )
			return PageFuns.pageVoToMap(page);
		
		return Maps.newHashMap();
	}

	@Override
	public void batchProjectAndWealth(CommissionInfoEntity commissionInfoEntity) {

		// 1) 批量插入项目
		// 插入提成表
		StringBuilder sqlString = new StringBuilder()
		.append(" insert into bao_t_commission_info ")
		.append("      (id, cust_id, comm_date,  ")
		.append("       invest_amount, commission_amount, reward_amount,  ")
		.append("       trade_status, record_status, create_user,  ")
		.append("       create_date, last_update_user, last_update_date,  ")
		.append("       version, memo, year_invest_amount, ")
		.append("       comm_month, product_type_id)	")
		.append("    select ")
		.append("      sys_guid(), custId, ?,  ")
		.append("      NVL(investAmount, 0), NVL(income, 0), NVL(award, 0),  ")
		.append("      ?, '有效', ?,  ")
		.append("      ?, ?, ?,  ")
		.append("      0, null, NVL(commissionInvestAmount, 0), ")
		.append("      ?, '5' ")
		.append("    from  ")
		.append("      (  ")
		.append("        select custId,  ")
		.append("         0 income,  ")
		.append("         0 award,  ")
		.append("          trunc(investAmount, 8) investAmount, ")
		.append("          trunc(commissionInvestAmount, 8) commissionInvestAmount ")
		.append("          from ( ")
		.append("                select q.custId, sum(q.investAmount) investAmount, sum(q.commissionInvestAmount) commissionInvestAmount ")
		.append("                from (  ")
		.append("                    select s.cust_id custId, t.project_id productId, sum(t.invest_amount) investAmount,")
		.append("                    sum(t.invest_amount*m.type_term/12) commissionInvestAmount   ")
		.append("                     from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_project_info m ")
		.append("                    where t.cust_id = s.quilt_cust_id and t.project_id = m.id ")
		.append("                      and t.project_id is not null   ")
		//.append("                      and s.start_date > add_months(sysdate, -12 * 2)    ")
		.append("                      and t.effect_date >= ? and t.effect_date < ? ")
		.append("                      and m.project_status in ('收益中','已逾期','已到期','提前结清') ")
		.append("                      and s.record_status = '有效' ")
		.append("                      and t.cust_id not in (select id   ")
		.append("                                            from bao_t_cust_info   ")
		.append("                                            where is_recommend = '是' )   ")
		.append("                    group by s.cust_id, t.project_id    ")
		.append("                    union all ")
		.append("                    select t.cust_id custId, t.project_id productId, sum(t.invest_amount) investAmount,")
		.append("                    sum(t.invest_amount*m.type_term/12) commissionInvestAmount   ")
		.append("                     from bao_t_invest_info t, bao_t_project_info m  ")
		.append("                    where t.project_id = m.id and t.project_id is not null ")
		.append("                    and t.cust_id in (select id   ")
		.append("                                      from bao_t_cust_info   ")
		.append("                                      where is_recommend = '是' )   ")
		.append("                    and t.effect_date >= ? and t.effect_date < ? ")
		.append("                    and m.project_status in ('收益中','已逾期','已到期','提前结清') ")
		.append("                    group by t.cust_id, t.project_id    ")
		.append("                ) q  ")
		.append("                group by q.custId  ")
		.append("               ) p   ")
		.append("          ) b   ");
		
		// 插入提成明细
		jdbcTemplate.update(sqlString.toString(), 
				new Object[]{	commissionInfoEntity.getCommDate(),
								commissionInfoEntity.getTradeStatus(),
								commissionInfoEntity.getCreateUser(),
								commissionInfoEntity.getCreateDate(),
								commissionInfoEntity.getLastUpdateUser(),
								commissionInfoEntity.getLastUpdateDate(),
								commissionInfoEntity.getCommMonth(),
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd")} );
		
		sqlString = new StringBuilder()
		.append("   insert into bao_t_commission_detail_info ")
		.append("      (id, commission_id, quilt_cust_id,  ")
		.append("       invest_amount, commission_amount, reward_amount,  ")
		.append("       trade_status, record_status, create_user,  ")
		.append("       create_date, last_update_user, last_update_date,  ")
		.append("       version, memo, relate_primary, relate_type," )
		.append("       year_invest_amount, invest_id) ")
		.append("    select ")
		.append("      sys_guid(), c.id, b.quiltCustId,  ")
		.append("       NVL(b.investAmount, 0), NVL(b.income, 0), NVL(b.award, 0),  ")
		.append("       ?, '有效', ?,  ")
		.append("       ?, ?, ?,  ")
		.append("       0, null, productId, 'BAO_T_PROJECT_INFO',")
		.append("       NVL(commissionInvestAmount, 0), investId ")
		.append("       from ")
		.append("       ( ")
		.append("        select custId,  ")
		.append("         quiltCustId,  ")
		.append("         productId,  ")
		.append("         investId,  ")
		.append("         0 income,  ")
		.append("         0 award,  ")
		.append("         trunc(investAmount, 8) investAmount, ")
		.append("         trunc(commissionInvestAmount, 8) commissionInvestAmount ")
		.append("         from ( ")
		.append("                select q.custId, q.quiltCustId, q.productId, q.investId, sum(q.investAmount) investAmount, sum(q.commissionInvestAmount) commissionInvestAmount ")
		.append("                from (  ")
		.append("                    select s.cust_id custId, s.quilt_cust_id quiltCustId, t.project_id productId, t.id investId, sum(t.invest_amount) investAmount,")
		.append("                    sum(t.invest_amount*m.type_term/12) commissionInvestAmount   ")
		.append("                     from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_project_info m ")
		.append("                    where t.cust_id = s.quilt_cust_id and t.project_id = m.id ")
		.append("                      and t.project_id is not null ")
		.append("                      and t.effect_date >= ? and t.effect_date < ? ")
		.append("                      and m.project_status in ('收益中','已逾期','已到期','提前结清') ")
		//.append("                      and s.start_date > add_months(sysdate, -12 * 2)    ")
		.append("                      and s.record_status = '有效' ")
		.append("                      and t.cust_id not in (select id   ")
		.append("                                            from bao_t_cust_info   ")
		.append("                                            where is_recommend = '是' )   ")
		.append("                    group by s.cust_id, s.quilt_cust_id, t.project_id, t.id  ")
		.append("                    union all ")
		.append("                    select t.cust_id custId, t.cust_id quiltCustId, t.project_id productId, t.id investId, sum(t.invest_amount) investAmount,")
		.append("                    sum(t.invest_amount*m.type_term/12) commissionInvestAmount   ")
		.append("                     from bao_t_invest_info t, bao_t_project_info m  ")
		.append("                    where t.project_id = m.id and t.project_id is not null ")
		.append("                    and t.cust_id in (select id   ")
		.append("                                      from bao_t_cust_info   ")
		.append("                                      where is_recommend = '是' )   ")
		.append("                    and t.effect_date >= ? and t.effect_date < ? ")
		.append("                    and m.project_status in ('收益中','已逾期','已到期','提前结清') ")
		.append("                    group by t.cust_id, t.project_id, t.id  ")
		.append("                ) q  ")
		.append("                group by q.custId, q.quiltCustId, q.productId, q.investId ")
		.append("               ) p   ")
		.append("           ) b, bao_t_commission_info c  ")
		.append("           where b.custId = c.cust_id and trunc(c.comm_date) = trunc(?) and c.comm_month = ? and c.product_type_id = '5' ");
		
		jdbcTemplate.update(sqlString.toString(), 
				new Object[]{	commissionInfoEntity.getTradeStatus(),
								commissionInfoEntity.getCreateUser(),
								commissionInfoEntity.getCreateDate(),
								commissionInfoEntity.getLastUpdateUser(),
								commissionInfoEntity.getLastUpdateDate(),
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
								commissionInfoEntity.getCommDate(),
								commissionInfoEntity.getCommMonth()} );	
		
		// 2) 批量插入理财计划
		// 插入提成表
		sqlString = new StringBuilder()
		.append(" insert into bao_t_commission_info ")
		.append("      (id, cust_id, comm_date,  ")
		.append("       invest_amount, commission_amount, reward_amount,  ")
		.append("       trade_status, record_status, create_user,  ")
		.append("       create_date, last_update_user, last_update_date,  ")
		.append("       version, memo, year_invest_amount, ")
		.append("       comm_month, product_type_id)	")
		.append("    select ")
		.append("      sys_guid(), custId, ?,  ")
		.append("      NVL(investAmount, 0), NVL(income, 0), NVL(award, 0),  ")
		.append("      ?, '有效', ?,  ")
		.append("      ?, ?, ?,  ")
		.append("      0, null, NVL(commissionInvestAmount, 0), ")
		.append("      ?, '4' ")
		.append("    from  ")
		.append("      (  ")
		.append("        select custId,  ")
		.append("         0 income,  ")
		.append("         0 award,  ")
		.append("          trunc(investAmount, 8) investAmount, ")
		.append("          trunc(commissionInvestAmount, 8) commissionInvestAmount ")
		.append("          from ( ")
		.append("                select q.custId, sum(q.investAmount) investAmount, sum(q.commissionInvestAmount) commissionInvestAmount ")
		.append("                from (  ")
		.append("                    select s.cust_id custId, t.wealth_id productId, sum(t.invest_amount) investAmount,")
		.append("                    sum(t.invest_amount*m.rebate_ratio) commissionInvestAmount   ")
		.append("                     from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_wealth_info n, bao_t_wealth_type_info m ")
		.append("                    where t.cust_id = s.quilt_cust_id and t.wealth_id = n.id and n.wealth_type_id = m.id ")
		.append("                      and t.wealth_id is not null   ")
		//.append("                      and s.start_date > add_months(sysdate, -12 * 2)    ")
		.append("                      and t.effect_date >= ? and t.effect_date < ? ")
		.append("                      and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回') ")
		.append("                      and s.record_status = '有效' ")
		.append("                      and t.cust_id not in (select id   ")
		.append("                                            from bao_t_cust_info   ")
		.append("                                            where is_recommend = '是' )   ")
		.append("                    group by s.cust_id, t.wealth_id    ")
		.append("                    union all ")
		.append("                    select t.cust_id custId, t.wealth_id productId, sum(t.invest_amount) investAmount,")
		.append("                    sum(t.invest_amount*m.rebate_ratio) commissionInvestAmount   ")
		.append("                     from bao_t_invest_info t, bao_t_wealth_info n, bao_t_wealth_type_info m  ")
		.append("                    where t.wealth_id = n.id and n.wealth_type_id = m.id and t.wealth_id is not null ")
		.append("                    and t.cust_id in (select id   ")
		.append("                                      from bao_t_cust_info   ")
		.append("                                      where is_recommend = '是' )   ")
		.append("                    and t.effect_date >= ? and t.effect_date < ? ")
		.append("                    and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回') ")
		.append("                    group by t.cust_id, t.wealth_id    ")
		.append("                ) q  ")
		.append("                group by q.custId  ")
		.append("               ) p   ")
		.append("          ) b   ");
		
		// 插入提成明细
		jdbcTemplate.update(sqlString.toString(), 
				new Object[]{	commissionInfoEntity.getCommDate(),
								commissionInfoEntity.getTradeStatus(),
								commissionInfoEntity.getCreateUser(),
								commissionInfoEntity.getCreateDate(),
								commissionInfoEntity.getLastUpdateUser(),
								commissionInfoEntity.getLastUpdateDate(),
								commissionInfoEntity.getCommMonth(),
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd")} );
		
		sqlString = new StringBuilder()
		.append("   insert into bao_t_commission_detail_info ")
		.append("      (id, commission_id, quilt_cust_id,  ")
		.append("       invest_amount, commission_amount, reward_amount,  ")
		.append("       trade_status, record_status, create_user,  ")
		.append("       create_date, last_update_user, last_update_date,  ")
		.append("       version, memo, relate_primary, relate_type," )
		.append("       year_invest_amount, invest_id) ")
		.append("    select ")
		.append("      sys_guid(), c.id, b.quiltCustId,  ")
		.append("       NVL(b.investAmount, 0), NVL(b.income, 0), NVL(b.award, 0),  ")
		.append("       ?, '有效', ?,  ")
		.append("       ?, ?, ?,  ")
		.append("       0, null, productId, 'BAO_T_WEALTH_INFO',")
		.append("       NVL(commissionInvestAmount, 0), investId ")
		.append("       from ")
		.append("       ( ")
		.append("        select custId,  ")
		.append("         quiltCustId,  ")
		.append("         productId,  ")
		.append("         investId,  ")
		.append("         0 income,  ")
		.append("         0 award,  ")
		.append("         trunc(investAmount, 8) investAmount, ")
		.append("         trunc(commissionInvestAmount, 8) commissionInvestAmount ")
		.append("         from ( ")
		.append("                select q.custId, q.quiltCustId, q.productId, q.investId, sum(q.investAmount) investAmount, sum(q.commissionInvestAmount) commissionInvestAmount ")
		.append("                from (  ")
		.append("                    select s.cust_id custId, s.quilt_cust_id quiltCustId, t.wealth_id productId, t.id investId, sum(t.invest_amount) investAmount,")
		.append("                    sum(t.invest_amount*m.rebate_ratio) commissionInvestAmount   ")
		.append("                     from bao_t_invest_info t, bao_t_cust_recommend_info s , bao_t_wealth_info n, bao_t_wealth_type_info m ")
		.append("                    where t.cust_id = s.quilt_cust_id and t.wealth_id = n.id and n.wealth_type_id = m.id ")
		.append("                      and t.wealth_id is not null ")
		.append("                      and t.effect_date >= ? and t.effect_date < ? ")
		.append("                      and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回') ")
		//.append("                      and s.start_date > add_months(sysdate, -12 * 2)    ")
		.append("                      and s.record_status = '有效' ")
		.append("                      and t.cust_id not in (select id   ")
		.append("                                            from bao_t_cust_info   ")
		.append("                                            where is_recommend = '是' )   ")
		.append("                    group by s.cust_id, s.quilt_cust_id, t.wealth_id, t.id  ")
		.append("                    union all ")
		.append("                    select t.cust_id custId, t.cust_id quiltCustId, t.wealth_id productId, t.id investId, sum(t.invest_amount) investAmount,")
		.append("                    sum(t.invest_amount*m.rebate_ratio) commissionInvestAmount   ")
		.append("                     from bao_t_invest_info t , bao_t_wealth_info n, bao_t_wealth_type_info m ")
		.append("                    where t.wealth_id = n.id and n.wealth_type_id = m.id and t.wealth_id is not null ")
		.append("                    and t.cust_id in (select id   ")
		.append("                                      from bao_t_cust_info   ")
		.append("                                      where is_recommend = '是' )   ")
		.append("                    and t.effect_date >= ? and t.effect_date < ? ")
		.append("                    and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回') ")
		.append("                    group by t.cust_id, t.wealth_id, t.id  ")
		.append("                ) q  ")
		.append("                group by q.custId, q.quiltCustId, q.productId, q.investId ")
		.append("               ) p   ")
		.append("           ) b, bao_t_commission_info c  ")
		.append("           where b.custId = c.cust_id and trunc(c.comm_date) = trunc(?) and c.comm_month = ? and c.product_type_id = '4' ");
		
		jdbcTemplate.update(sqlString.toString(), 
				new Object[]{	commissionInfoEntity.getTradeStatus(),
								commissionInfoEntity.getCreateUser(),
								commissionInfoEntity.getCreateDate(),
								commissionInfoEntity.getLastUpdateUser(),
								commissionInfoEntity.getLastUpdateDate(),
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
								commissionInfoEntity.getCommDate(),
								commissionInfoEntity.getCommMonth()} );

		// 3) 批量插入散标投资
		// 插入提成表
		sqlString = new StringBuilder()
		.append(" insert into bao_t_commission_info ")
		.append("      (id, cust_id, comm_date,  ")
		.append("       invest_amount, commission_amount, reward_amount,  ")
		.append("       trade_status, record_status, create_user,  ")
		.append("       create_date, last_update_user, last_update_date,  ")
		.append("       version, memo, year_invest_amount, ")
		.append("       comm_month, product_type_id)	")
		.append("    select ")
		.append("      sys_guid(), custId, ?,  ")
		.append("      NVL(investAmount, 0), NVL(income, 0), NVL(award, 0),  ")
		.append("      ?, '有效', ?,  ")
		.append("      ?, ?, ?,  ")
		.append("      0, null, NVL(commissionInvestAmount, 0), ")
		.append("      ?, '5' ")
		.append("    from  ")
		.append("      (  ")
		.append("        select custId,  ")
		.append("         0 income,  ")
		.append("         0 award,  ")
		.append("          trunc(investAmount, 8) investAmount, ")
		.append("          trunc(commissionInvestAmount, 8) commissionInvestAmount ")
		.append("          from ( ")
		.append("                select q.custId, sum(q.investAmount) investAmount, sum(q.commissionInvestAmount) commissionInvestAmount ")
		.append("                from (  ")
		.append("                    select s.cust_id custId, t.loan_id productId, sum(t.invest_amount) investAmount,")
		.append("                    sum(t.invest_amount*n.rebate_ratio) commissionInvestAmount   ")
		.append("                     from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_loan_info n ")
		.append("                    where t.cust_id = s.quilt_cust_id and t.loan_id = n.id ")
		.append("                      and t.loan_id is not null   ")
		//.append("                      and s.start_date > add_months(sysdate, -12 * 2)    ")
		.append("                      and t.effect_date >= ? and t.effect_date < ? ")
		.append("                      and t.invest_status in ('收益中','已到期','提前结清') ")
		.append("                      and s.record_status = '有效' ")
		.append("                      and t.cust_id not in (select id   ")
		.append("                                            from bao_t_cust_info   ")
		.append("                                            where is_recommend = '是' )   ")
		.append("                    group by s.cust_id, t.loan_id    ")
		.append("                    union all ")
		.append("                    select t.cust_id custId, t.loan_id productId, sum(t.invest_amount) investAmount,")
		.append("                    sum(t.invest_amount*n.rebate_ratio) commissionInvestAmount   ")
		.append("                     from bao_t_invest_info t, bao_t_loan_info n  ")
		.append("                    where t.loan_id = n.id ")
		.append("                    and t.loan_id is not null ")
		.append("                    and t.cust_id in (select id   ")
		.append("                                      from bao_t_cust_info   ")
		.append("                                      where is_recommend = '是' )   ")
		.append("                    and t.effect_date >= ? and t.effect_date < ? ")
		.append("                    and t.invest_status in ('收益中','已到期','提前结清') ")
		.append("                    group by t.cust_id, t.loan_id    ")
		.append("                ) q  ")
		.append("                group by q.custId  ")
		.append("               ) p   ")
		.append("          ) b   ");
		
		// 插入提成明细
		jdbcTemplate.update(sqlString.toString(), 
				new Object[]{	commissionInfoEntity.getCommDate(),
								commissionInfoEntity.getTradeStatus(),
								commissionInfoEntity.getCreateUser(),
								commissionInfoEntity.getCreateDate(),
								commissionInfoEntity.getLastUpdateUser(),
								commissionInfoEntity.getLastUpdateDate(),
								commissionInfoEntity.getCommMonth(),
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd")} );
		
		sqlString = new StringBuilder()
		.append("   insert into bao_t_commission_detail_info ")
		.append("      (id, commission_id, quilt_cust_id,  ")
		.append("       invest_amount, commission_amount, reward_amount,  ")
		.append("       trade_status, record_status, create_user,  ")
		.append("       create_date, last_update_user, last_update_date,  ")
		.append("       version, memo, relate_primary, relate_type," )
		.append("       year_invest_amount, invest_id) ")
		.append("    select ")
		.append("      sys_guid(), c.id, b.quiltCustId,  ")
		.append("       NVL(b.investAmount, 0), NVL(b.income, 0), NVL(b.award, 0),  ")
		.append("       ?, '有效', ?,  ")
		.append("       ?, ?, ?,  ")
		.append("       0, null, productId, 'BAO_T_LOAN_INFO',")
		.append("       NVL(commissionInvestAmount, 0), investId ")
		.append("       from ")
		.append("       ( ")
		.append("        select custId,  ")
		.append("         quiltCustId,  ")
		.append("         productId,  ")
		.append("         investId,  ")
		.append("         0 income,  ")
		.append("         0 award,  ")
		.append("         trunc(investAmount, 8) investAmount, ")
		.append("         trunc(commissionInvestAmount, 8) commissionInvestAmount ")
		.append("         from ( ")
		.append("                select q.custId, q.quiltCustId, q.productId, q.investId, sum(q.investAmount) investAmount, sum(q.commissionInvestAmount) commissionInvestAmount ")
		.append("                from (  ")
		.append("                    select s.cust_id custId, s.quilt_cust_id quiltCustId, t.loan_id productId, t.id investId, sum(t.invest_amount) investAmount,")
		.append("                    sum(t.invest_amount*n.rebate_ratio) commissionInvestAmount   ")
		.append("                     from bao_t_invest_info t, bao_t_cust_recommend_info s , bao_t_loan_info n ")
		.append("                    where t.cust_id = s.quilt_cust_id and t.loan_id = n.id ")
		.append("                      and t.loan_id is not null ")
		.append("                      and t.effect_date >= ? and t.effect_date < ? ")
		.append("                      and t.invest_status in ('收益中','已到期','提前结清') ")
		//.append("                      and s.start_date > add_months(sysdate, -12 * 2)    ")
		.append("                      and s.record_status = '有效' ")
		.append("                      and t.cust_id not in (select id   ")
		.append("                                            from bao_t_cust_info   ")
		.append("                                            where is_recommend = '是' )   ")
		.append("                    group by s.cust_id, s.quilt_cust_id, t.loan_id, t.id  ")
		.append("                    union all ")
		.append("                    select t.cust_id custId, t.cust_id quiltCustId, t.loan_id productId, t.id investId, sum(t.invest_amount) investAmount,")
		.append("                    sum(t.invest_amount*n.rebate_ratio) commissionInvestAmount   ")
		.append("                     from bao_t_invest_info t , bao_t_loan_info n ")
		.append("                    where t.loan_id = n.id ")
		.append("                    and t.loan_id is not null ")
		.append("                    and t.cust_id in (select id   ")
		.append("                                      from bao_t_cust_info   ")
		.append("                                      where is_recommend = '是' )   ")
		.append("                    and t.effect_date >= ? and t.effect_date < ? ")
		.append("                    and t.invest_status in ('收益中','已到期','提前结清') ")
		.append("                    group by t.cust_id, t.loan_id, t.id  ")
		.append("                ) q  ")
		.append("                group by q.custId, q.quiltCustId, q.productId, q.investId ")
		.append("               ) p   ")
		.append("           ) b, bao_t_commission_info c  ")
		.append("           where b.custId = c.cust_id and trunc(c.comm_date) = trunc(?) and c.comm_month = ? and c.product_type_id = '5' ");
		
		jdbcTemplate.update(sqlString.toString(), 
				new Object[]{	commissionInfoEntity.getTradeStatus(),
								commissionInfoEntity.getCreateUser(),
								commissionInfoEntity.getCreateDate(),
								commissionInfoEntity.getLastUpdateUser(),
								commissionInfoEntity.getLastUpdateDate(),
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
								commissionInfoEntity.getCommDate(),
								commissionInfoEntity.getCommMonth()} );
	}

	@Override
	public Map<String, Object> sumProjectAndWealthCommission(Map<String, Object> params) {
		
		Map<String, Object> result = Maps.newHashMap();
		String currentMonth = (String) params.get("currentMonth");
		String preMonth = (String) params.get("preMonth");

		// 统计本月业绩
		/*
		StringBuilder sqlString = new StringBuilder()
		.append("       select trunc(nvl(sum(q.investamount), 0), 2) \"totalMonthlyIncomeAmount\", trunc(nvl(sum(q.commissioninvestamount), 0), 2) \"totalMonthlyInvestAmount\"  ")
		.append("       from (   ")
		.append("           select sum(t.invest_amount) investamount, ")
		.append("           sum(t.invest_amount*m.type_term/12) commissioninvestamount    ")
		.append("            from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_project_info m  ")
		.append("           where t.cust_id = s.quilt_cust_id and t.project_id = m.id  ")
		.append("             and t.project_id is not null    ")
		//.append("             and s.start_date > add_months(sysdate, -12 * 2)     ")
		.append("             and t.effect_date >= ? and t.effect_date < ? ")
		.append("             and m.project_status in ('还款中','已逾期','已到期','提前结清') ")
		.append("             and s.record_status = '有效'  ")
		.append("             and t.cust_id not in (select id    ")
		.append("                                   from bao_t_cust_info    ")
		.append("                                   where is_recommend = '是' )    ")
		.append("             and s.cust_id = ? ") // 汇总业务员旗下所有客户投资情况（项目）
		.append("           union all  ")
		.append("           select sum(t.invest_amount) investamount, ")
		.append("           sum(t.invest_amount*m.type_term/12) commissioninvestamount    ")
		.append("            from bao_t_invest_info t, bao_t_project_info m   ")
		.append("           where t.project_id = m.id and t.project_id is not null  ")
		.append("           and t.cust_id in (select id    ")
		.append("                             from bao_t_cust_info    ")
		.append("                             where is_recommend = '是' )    ") 
		.append("           and t.effect_date >= ? and t.effect_date < ? ")
		.append("           and m.project_status in ('还款中','已逾期','已到期','提前结清') ")
		.append("           and t.cust_id = ? ") // 汇总业务员自己投资情况（项目）
		.append("           union all     ")
		.append("           select sum(t.invest_amount) investamount, ")
		.append("           sum(t.invest_amount*m.rebate_ratio) commissioninvestamount    ")
		.append("            from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_wealth_info n, bao_t_wealth_type_info m  ")
		.append("           where t.cust_id = s.quilt_cust_id and t.wealth_id = n.id and n.wealth_type_id = m.id  ")
		.append("             and t.wealth_id is not null    ")
		//.append("             and s.start_date > add_months(sysdate, -12 * 2)     ")
		.append("             and t.effect_date >= ? and t.effect_date < ? ")
		.append("             and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回') ")
		.append("             and s.record_status = '有效'  ")
		.append("             and t.cust_id not in (select id    ")
		.append("                                   from bao_t_cust_info    ")
		.append("                                   where is_recommend = '是' )    ")
		.append("             and s.cust_id = ? ")// 汇总业务员旗下所有客户投资情况（计划）
		.append("           union all  ")
		.append("           select sum(t.invest_amount) investamount, ")
		.append("           sum(t.invest_amount*m.rebate_ratio) commissioninvestamount    ")
		.append("            from bao_t_invest_info t, bao_t_wealth_info n, bao_t_wealth_type_info m   ")
		.append("           where t.wealth_id = n.id and n.wealth_type_id = m.id and t.wealth_id is not null  ")
		.append("             and t.cust_id in (select id    ")
		.append("                             from bao_t_cust_info    ")
		.append("                             where is_recommend = '是' )    ")
		.append("             and t.effect_date >= ? and t.effect_date < ? ")
		.append("             and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回') ")
		.append("             and t.cust_id = ? ")// 汇总业务员自己投资情况（计划）
		.append("           union all  ")
		.append("           select  sum(t.invest_amount) investamount, ")
		.append("           sum(t.invest_amount*n.rebate_ratio) commissioninvestamount    ")
		.append("            from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_loan_info n, bao_t_loan_detail_info m, bao_t_cust_info c  ")
		.append("           where t.cust_id = s.quilt_cust_id and t.loan_id = n.id and n.id = m.loan_id and t.cust_id = c.id ")
		.append("             and t.loan_id is not null  ")
		.append("             and t.effect_date >= ? and t.effect_date < ? ")
		.append("             and t.invest_status in ('收益中','提前结清','已到期', '已转让') ")
		//.append("             and s.start_date > add_months(sysdate, -12 * 2)     ")
		.append("             and s.record_status = '有效'  ")
		.append("             and t.cust_id not in (select id    ")
		.append("                                   from bao_t_cust_info    ")
		.append("                                   where is_recommend = '是' )    ")
		.append("             and s.cust_id = ? ") // 查询当月业务员旗下所有客户【散标】投资情况
		.append("           union all  ")
		.append("           select  sum(t.invest_amount) investamount, ")
		.append("           sum(t.invest_amount*n.rebate_ratio) commissioninvestamount    ")
		.append("            from bao_t_invest_info t, bao_t_loan_info n, bao_t_loan_detail_info m, bao_t_cust_info c     ")
		.append("           where t.loan_id = n.id and n.id = m.loan_id and t.cust_id = c.id  ")
		.append("           and t.loan_id is not null  ")
		.append("           and t.cust_id in (select id    ")
		.append("                             from bao_t_cust_info    ")
		.append("                             where is_recommend = '是' )    ")
		.append("           and t.effect_date >= ? and t.effect_date < ? ")
		.append("           and t.invest_status in ('收益中','提前结清','已到期', '已转让') ")
		.append("           and t.cust_id = ?  ")// 查询当月业务员自己【散标】投资情况
		.append("       ) q  ");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), 
				new Object[] { 
					currentMonth + "01",
					DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
					(String)params.get("custId"), 
					currentMonth + "01",
					DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
					(String)params.get("custId"),
					currentMonth + "01",
					DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
					(String)params.get("custId"),
					currentMonth + "01",
					DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
					(String)params.get("custId"),
					currentMonth + "01",
					DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
					(String)params.get("custId"),
					currentMonth + "01",
					DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
					(String)params.get("custId")});
		*/
		// 2017/1/12update by liyy
		StringBuilder sqlString = new StringBuilder()
		.append("       select trunc(nvl(sum(q.invest_amount), 0), 2) \"totalMonthlyIncomeAmount\", trunc(nvl(sum(q.YEAR_INVEST_AMOUNT), 0), 2) \"totalMonthlyInvestAmount\"  ")
		.append("       from (   ")
		.append("          select t.invest_amount ")
		.append("               , t.YEAR_INVEST_AMOUNT ")
		.append("            from bao_t_commission_info t ")
		.append("           where 1=1")
		.append("             AND EXISTS (SELECT * FROM bao_t_commission_detail_info s, BAO_T_INVEST_INFO i ")
		.append("                          WHERE  i.ID = s.INVEST_ID")
		.append("                            AND i.effect_date >= ? and i.effect_date < ? ")
		.append("                            AND s.commission_id = t.id) ")
		.append("             and t.cust_id = ? ")
		.append("       ) q  ")
		;
        List<Map<String, Object>> list_curr = repositoryUtil.queryForMap(sqlString.toString(),
                new Object[] {
                        currentMonth + "01",
                        DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
                        params.get("custId")
                });
        // 2017/5/11 update by mali
        sqlString = new StringBuilder()
                .append("SELECT NVL(SUM(S.HOLD_AMOUNT), 0) \"totalMonthlyInvestAmount\" ")
                .append("  FROM BAO_T_COMMISSION_SENDBACK S ")
                .append(" WHERE S.JOB_NO = ?")
                .append("   AND S.EFFECT_DATE >= TO_DATE(?, 'YYYYMMDD') ")
                .append("   AND S.EFFECT_DATE < TO_DATE(?, 'YYYYMMDD') ")
                .append("   AND TO_CHAR(S.CREATE_DATE, 'YYYYMM') = ? ")
                ;
		List<Map<String, Object>> list_pre = repositoryUtil.queryForMap(sqlString.toString(),
				new Object[] {
            params.get("jobNo"),
            preMonth + "01",
			currentMonth + "01",
            currentMonth
		});
        /*if(list != null && list.size() > 0) {
            result = list.get(0);
        }
        else {
            result.put("totalMonthlyIncomeAmount", 0);
            result.put("totalMonthlyInvestAmount", 0);
        }*/
        if(!CommonUtils.isEmpty(list_curr)) {
            // 进账金额取实时数据
            result.put("totalMonthlyIncomeAmount", list_curr.get(0).get("totalMonthlyIncomeAmount"));
        } else {
            result.put("totalMonthlyIncomeAmount", 0);
        }
        if(!CommonUtils.isEmpty(list_pre)) {
            // 年化投资额取上个月数据
            result.put("totalMonthlyInvestAmount", list_pre.get(0).get("totalMonthlyInvestAmount"));
        } else {
            result.put("totalMonthlyInvestAmount", 0);
        }

        /*
         *   2017-5-27 update by mali
         *   年化投资按照新SOP计算
         */
        // 查询所有业绩
        /*StringBuilder sqlString = new StringBuilder()
                .append("select s.invest_id \"investId\", l.repayment_method \"repaymentMethod\", s.invest_amount \"investAmount\", ")
                .append("       to_date(i.effect_date, 'yyyymmdd') \"effectDate\", to_date(i.expire_date, 'yyyymmdd') \"expireDate\", ")
                .append("       l.loan_unit \"loanUnit\", l.loan_term \"loanTerm\", l.seat_term \"seatTerm\" ")
                .append("  from bao_t_commission_info t ")
                .append(" inner join bao_t_commission_detail_info s on t.id = s.commission_id")
                .append(" inner join bao_t_invest_info i on s.invest_id = i.id")
                .append(" inner join bao_t_loan_info l on l.id = i.loan_id")
                .append(" where i.effect_date >= ? and i.effect_date < ? and t.cust_id = ?")
                ;
        List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(),
                new Object[] {
                        currentMonth + "01",
                        DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
                        params.get("custId")
                });

        if (CommonUtils.isEmpty(list)) {
            result.put("totalMonthlyIncomeAmount", 0);
            result.put("totalMonthlyInvestAmount", 0);
        } else {
            BigDecimal yearInvestAmtSum = BigDecimal.ZERO;
            for (Map<String, Object> map : list) {
                String investId = CommonUtils.emptyToString(map.get("investId"));// 投资id
                String seatTerm = CommonUtils.emptyToString(map.get("seatTerm"));// 是否流转标 -1：非流转标 30：流转标
                String loanUnit = CommonUtils.emptyToString(map.get("loanUnit"));// 标的单位（天、月）
                int loanTerm = CommonUtils.emptyToInt(map.get("loanTerm"));// 标的期数
                BigDecimal investAmount = CommonUtils.emptyToDecimal(map.get("investAmount"));// 投资金额
                if ("-1".equals(seatTerm)) {
                    // 非流转标
                    yearInvestAmtSum = getYearAmtForUnTransable(yearInvestAmtSum, investAmount, loanUnit, loanTerm);
                } else {
                    // 流转标
                }
            }
        }*/


		// 统计历史业绩
		sqlString = new StringBuilder()
		.append("       select trunc(nvl(sum(t.invest_amount), 0), 2) \"totalIncomeAmount\", trunc(nvl(sum(t.year_invest_amount), 0), 2) \"totalInvestAmount\" ")
		.append("       from bao_t_commission_info t ")
		.append("       where t.cust_id = ? ");
		List<Map<String, Object>> list2_curr = repositoryUtil.queryForMap(sqlString.toString(), new Object[] {params.get("custId")});

        sqlString = new StringBuilder()
                .append("SELECT NVL(SUM(S.INVEST_AMOUNT), 0) \"totalIncomeAmount\", ")
                .append("       NVL(SUM(S.HOLD_AMOUNT), 0) \"totalInvestAmount\" ")
                .append("  FROM BAO_T_COMMISSION_SENDBACK S ")
                .append(" WHERE S.JOB_NO = ?")
                ;
		List<Map<String, Object>> list2_pre = repositoryUtil.queryForMap(sqlString.toString(), new Object[] {params.get("jobNo")});

		/*if(list2 != null && list2.size() > 0) {
			result.putAll(list2.get(0));
		}
		else {
			result.put("totalIncomeAmount", 0);
			result.put("totalInvestAmount", 0);
		}*/
        if(!CommonUtils.isEmpty(list2_curr)) {
            // 进账金额取实时数据
            result.put("totalIncomeAmount", list2_curr.get(0).get("totalIncomeAmount"));
        } else {
            result.put("totalIncomeAmount", 0);
        }
        if(!CommonUtils.isEmpty(list2_pre)) {
            // 年化投资额取上个月之前（包含上个月）数据
            result.put("totalInvestAmount", list2_pre.get(0).get("totalInvestAmount"));
        } else {
            result.put("totalInvestAmount", 0);
        }

		// 统计当月注册人数
		sqlString = new StringBuilder()
		.append("   select count(1) \"totalMonthlyRegister\" ")
		.append("   from bao_t_cust_recommend_info t, bao_t_cust_info s ")
		.append("   where t.quilt_cust_id = s.id ")
		.append("   and t.record_status = '有效' ")
		.append("   and s.create_date >= ? and s.create_date < ? ")
		.append("   and t.cust_id = ? ");
		
		List<Map<String, Object>> list3 = repositoryUtil.queryForMap(sqlString.toString(), 
				new Object[] { 
							    DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"),
								DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), 
								(String)params.get("custId") });
		if(list3 != null && list3.size() > 0) {
			result.putAll(list3.get(0));
		}
		else {
			result.put("totalMonthlyRegister", 0);
		}
		
		// 统计累积注册人数
		sqlString = new StringBuilder()
		.append("   select count(1) \"totalRegister\" ")
		.append("   from bao_t_cust_recommend_info t, bao_t_cust_info s ")
		.append("   where t.quilt_cust_id = s.id ")
		.append("   and t.record_status = '有效' ")
		.append("   and t.cust_id = ? ");

		List<Map<String, Object>> list4 = repositoryUtil.queryForMap(sqlString.toString(),
				new Object[] { (String)params.get("custId") });
		if(list4 != null && list4.size() > 0) {
			result.putAll(list4.get(0));
		}
		else {
			result.put("totalRegister", 0);
		}
		
		// 当月投资人数
		/*
		sqlString = new StringBuilder()
		.append("   select nvl(count(distinct q.custId), 0) \"totalMonthlyInvestCount\" ")
		.append("   from  ")
		.append("   ( ")
		.append("   select t.cust_id custId ")
		.append("   from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_project_info m ")
		.append("   where t.cust_id = s.quilt_cust_id and t.project_id = m.id ")
		.append("   and t.project_id is not null ")
		.append("   and t.effect_date >= ? and t.effect_date < ? ")
		.append("   and m.project_status in ('还款中','已逾期','已到期','提前结清') ")
		.append("   and s.record_status = '有效' ")
		.append("   and t.cust_id not in (select id     ")
		.append("                         from bao_t_cust_info    ")
		.append("                         where is_recommend = '是' )  ")
		.append("   and s.cust_id = ? ")
		.append("   union all ")
		.append("   select t.cust_id custId ")
		.append("     from bao_t_invest_info t, bao_t_project_info m    ")
		.append("    where t.project_id = m.id and t.project_id is not null   ")
		.append("    and t.cust_id in (select id     ")
		.append("                      from bao_t_cust_info     ")
		.append("                      where is_recommend = '是' )      ")
		.append("    and t.effect_date >= ? and t.effect_date < ? ")
		.append("    and m.project_status in ('还款中','已逾期','已到期','提前结清')  ")
		.append("    and t.cust_id = ?  ")
		.append("    union all   ")
		.append("   select t.cust_id custId ")
		.append("     from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_wealth_info n, bao_t_wealth_type_info m   ")
		.append("    where t.cust_id = s.quilt_cust_id and t.wealth_id = n.id and n.wealth_type_id = m.id   ")
		.append("      and t.wealth_id is not null       ")
		.append("      and t.effect_date >= ? and t.effect_date < ? ")
		.append("      and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回')  ")
		.append("      and s.record_status = '有效'   ")
		.append("      and t.cust_id not in (select id     ")
		.append("                            from bao_t_cust_info     ")
		.append("                            where is_recommend = '是' )     ")
		.append("      and s.cust_id = ?  ")
		.append("    union all   ")
		.append("    select t.cust_id custId ")
		.append("     from bao_t_invest_info t, bao_t_wealth_info n, bao_t_wealth_type_info m    ")
		.append("    where t.wealth_id = n.id and n.wealth_type_id = m.id and t.wealth_id is not null   ")
		.append("    and t.cust_id in (select id     ")
		.append("                      from bao_t_cust_info     ")
		.append("                      where is_recommend = '是' )     ")
		.append("    and t.effect_date >= ? and t.effect_date < ? ")
		.append("    and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回')  ")
		.append("    and t.cust_id = ?  ")
		.append("    union all   ")
		.append("    select  t.cust_id custId  ")
		.append("    from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_loan_info n, bao_t_loan_detail_info m, bao_t_cust_info c  ")
		.append("    where t.cust_id = s.quilt_cust_id and t.loan_id = n.id and n.id = m.loan_id and t.cust_id = c.id ")
		.append("    and t.loan_id is not null  ")
		.append("    and t.effect_date >= ? and t.effect_date < ? ")
		.append("    and t.invest_status in ('收益中','提前结清','已到期', '已转让') ")
		//.append("     and s.start_date > add_months(sysdate, -12 * 2)     ")
		.append("    and s.record_status = '有效'  ")
		.append("    and t.cust_id not in (select id    ")
		.append("    						from bao_t_cust_info    ")
		.append("    						where is_recommend = '是' )    ")
		.append("    and s.cust_id = ? ") // 查询当月业务员旗下所有客户【散标】投资情况
		.append("    union all  ")
		.append("    select  t.cust_id custId ")
		.append("    from bao_t_invest_info t, bao_t_loan_info n, bao_t_loan_detail_info m, bao_t_cust_info c     ")
		.append("    where t.loan_id = n.id and n.id = m.loan_id and t.cust_id = c.id  ")
		.append("    and t.loan_id is not null  ")
		.append("    and t.cust_id in (select id    ")
		.append("    					from bao_t_cust_info    ")
		.append("    					where is_recommend = '是' )    ")
		.append("    and t.effect_date >= ? and t.effect_date < ? ")
		.append("    and t.invest_status in ('收益中','提前结清','已到期', '已转让') ")
		.append("    and t.cust_id = ?  ")// 查询当月业务员自己【散标】投资情况
		.append("    ) q ");
		
		List<Map<String, Object>> list5 = repositoryUtil.queryForMap(sqlString.toString(), 
				new Object[] { 
			currentMonth + "01",
			DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
			(String)params.get("custId"), 
			currentMonth + "01",
			DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
			(String)params.get("custId"),
			currentMonth + "01",
			DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
			(String)params.get("custId"),
			currentMonth + "01",
			DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
			(String)params.get("custId"),
			currentMonth + "01",
			DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
			(String)params.get("custId"),
			currentMonth + "01",
			DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
			(String)params.get("custId")});
		*/
		// 2017/1/12 update by liyy
		sqlString = new StringBuilder()
		.append("   select nvl(count(distinct q.custId), 0) \"totalMonthlyInvestCount\" ")
		.append("   from  ")
		.append("   ( ")
		.append("    select s.quilt_cust_id custId ")
		.append("    from bao_t_commission_info t, bao_t_commission_detail_info s ")
		.append("    , BAO_T_INVEST_INFO i ")
		.append("    where 1=1 ")
		.append("    AND i.effect_date >= ? and i.effect_date < ? ")
		.append("    AND i.ID = s.INVEST_ID ")
		.append("    and t.id = s.commission_id ")
		.append("    and t.cust_id = ? ")
		.append("    ) q ")
		;
		List<Map<String, Object>> list5 = repositoryUtil.queryForMap(sqlString.toString(), 
				new Object[] { 
			currentMonth + "01",
			DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
			(String)params.get("custId")
		});
		if(list5 != null && list5.size() > 0) {
			result.putAll(list5.get(0));
		}
		else {
			result.put("totalMonthlyInvestCount", 0);
		}
		
		// 总投资人数
		/*
		sqlString = new StringBuilder()
		.append("   select nvl(count(distinct q.custId), 0) \"totalInvestCount\" ")
		.append("   from  ")
		.append("   ( ")
		.append("   select t.cust_id custId ")
		.append("   from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_project_info m ")
		.append("   where t.cust_id = s.quilt_cust_id and t.project_id = m.id ")
		.append("   and t.project_id is not null ")
		.append("   and t.effect_date >= ? and t.effect_date < ? ")
		.append("   and m.project_status in ('还款中','已逾期','已到期','提前结清') ")
		.append("   and s.record_status = '有效' ")
		.append("   and t.cust_id not in (select id     ")
		.append("                         from bao_t_cust_info    ")
		.append("                         where is_recommend = '是' )  ")
		.append("   and s.cust_id = ? ")
		.append("   union all ")
		.append("   select t.cust_id custId ")
		.append("     from bao_t_invest_info t, bao_t_project_info m    ")
		.append("    where t.project_id = m.id and t.project_id is not null   ")
		.append("    and t.cust_id in (select id     ")
		.append("                      from bao_t_cust_info     ")
		.append("                      where is_recommend = '是' )      ")
		.append("   and t.effect_date >= ? and t.effect_date < ? ")
		.append("    and m.project_status in ('还款中','已逾期','已到期','提前结清')  ")
		.append("    and t.cust_id = ?  ")
		.append("    union all   ")
		.append("   select t.cust_id custId ")
		.append("     from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_wealth_info n, bao_t_wealth_type_info m   ")
		.append("    where t.cust_id = s.quilt_cust_id and t.wealth_id = n.id and n.wealth_type_id = m.id   ")
		.append("      and t.wealth_id is not null       ")
		.append("      and t.effect_date >= ? and t.effect_date < ? ")
		.append("      and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回')  ")
		.append("      and s.record_status = '有效'   ")
		.append("      and t.cust_id not in (select id     ")
		.append("                            from bao_t_cust_info     ")
		.append("                            where is_recommend = '是' )     ")
		.append("      and s.cust_id = ?  ")
		.append("    union all   ")
		.append("    select t.cust_id custId ")
		.append("     from bao_t_invest_info t, bao_t_wealth_info n, bao_t_wealth_type_info m    ")
		.append("    where t.wealth_id = n.id and n.wealth_type_id = m.id and t.wealth_id is not null   ")
		.append("    and t.cust_id in (select id     ")
		.append("                      from bao_t_cust_info     ")
		.append("                      where is_recommend = '是' )     ")
		.append("      and t.effect_date >= ? and t.effect_date < ? ")
		.append("    and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回')  ")
		.append("    and t.cust_id = ?  ")
		.append("    union all   ")
		.append("           select  t.cust_id custId  ")
		.append("            from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_loan_info n, bao_t_loan_detail_info m, bao_t_cust_info c  ")
		.append("           where t.cust_id = s.quilt_cust_id and t.loan_id = n.id and n.id = m.loan_id and t.cust_id = c.id ")
		.append("             and t.loan_id is not null  ")
		.append("             and t.effect_date >= ? and t.effect_date < ? ")
		.append("             and t.invest_status in ('收益中','提前结清','已到期', '已转让') ")
		//.append("             and s.start_date > add_months(sysdate, -12 * 2)     ")
		.append("             and s.record_status = '有效'  ")
		.append("             and t.cust_id not in (select id    ")
		.append("                                   from bao_t_cust_info    ")
		.append("                                   where is_recommend = '是' )    ")
		.append("             and s.cust_id = ? ") // 查询当月业务员旗下所有客户【散标】投资情况
		.append("           union all  ")
		.append("           select  t.cust_id custId ")
		.append("            from bao_t_invest_info t, bao_t_loan_info n, bao_t_loan_detail_info m, bao_t_cust_info c     ")
		.append("           where t.loan_id = n.id and n.id = m.loan_id and t.cust_id = c.id  ")
		.append("           and t.loan_id is not null  ")
		.append("           and t.cust_id in (select id    ")
		.append("                             from bao_t_cust_info    ")
		.append("                             where is_recommend = '是' )    ")
		.append("           and t.effect_date >= ? and t.effect_date < ? ")
		.append("           and t.invest_status in ('收益中','提前结清','已到期', '已转让') ")
		.append("           and t.cust_id = ?  ")// 查询当月业务员自己【散标】投资情况
		.append("    union all ")
		.append("    select s.quilt_cust_id custId ")
		.append("    from bao_t_commission_info t, bao_t_commission_detail_info s ")
		.append("    where t.id = s.commission_id ")
		.append("    and t.cust_id = ? ")
		.append("    ) q ");
		
		List<Map<String, Object>> list6 = repositoryUtil.queryForMap(sqlString.toString(), 
				new Object[] { 
			currentMonth + "01",
			DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
			(String)params.get("custId"), 
			currentMonth + "01",
			DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
			(String)params.get("custId"),
			currentMonth + "01",
			DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
			(String)params.get("custId"),
			currentMonth + "01",
			DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
			(String)params.get("custId"),
			currentMonth + "01",
			DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
			(String)params.get("custId"),
			currentMonth + "01",
			DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
			(String)params.get("custId"),
			(String)params.get("custId")});
		*/
		
		// 2017/1/12 update by liyy
		sqlString = new StringBuilder()
		.append("   select nvl(count(distinct q.custId), 0) \"totalInvestCount\" ")
		.append("   from  ")
		.append("   ( ")
		.append("    select s.quilt_cust_id custId ")
		.append("    from bao_t_commission_info t, bao_t_commission_detail_info s ")
		.append("    where t.id = s.commission_id ")
		.append("    and t.cust_id = ? ")
		.append("    ) q ")
		;
		List<Map<String, Object>> list6 = repositoryUtil.queryForMap(sqlString.toString(), 
				new Object[] { 
			(String)params.get("custId")
		});
		if(list6 != null && list6.size() > 0) {
			result.putAll(list6.get(0));
		}
		else {
			result.put("totalInvestCount", 0);
		}

		/** 2017-5-11 查询团队上个月进账金额、上个月年化投资金额 */
        String ranking = CommonUtils.emptyToString(params.get("ranking"));
        /*if (CommonUtils.isEmpty(ranking) || "0".equals(ranking)) {
            result.put("teamTotalMonthlyIncomeAmount", 0);
            result.put("teamTotalMonthlyInvestAmount", 0);
        } else {
            String jobNo_col;
            if ("1".equals(ranking)) {
                jobNo_col = "JOB_NO";
            } else {
                jobNo_col = "RANK_0" + ranking + "_JOBNO";
            }
            sqlString = new StringBuilder()
                    .append("SELECT NVL(SUM(S.INVEST_AMOUNT), 0) \"teamTotalMonthlyIncomeAmount\", ")
                    .append("       NVL(SUM(S.HOLD_AMOUNT), 0) \"teamTotalMonthlyInvestAmount\" ")
                    .append("  FROM BAO_T_COMMISSION_SENDBACK S ")
                    .append(" WHERE S." + jobNo_col + " = ?");
            sqlString.append("   AND S.EFFECT_DATE >= TO_DATE(?, 'YYYYMMDD') ")
                    .append("   AND S.EFFECT_DATE < TO_DATE(?, 'YYYYMMDD') ")
            ;
            List<Map<String, Object>> list7 = repositoryUtil.queryForMap(sqlString.toString(),
                    new Object[] {
                            params.get("jobNo"),
                            preMonth + "01",
                            currentMonth + "01"

                    });
            if (list7 != null && list7.size() > 0) {
                result.putAll(list7.get(0));
            } else {
                result.put("teamTotalMonthlyIncomeAmount", 0);
                result.put("teamTotalMonthlyInvestAmount", 0);
            }
        }*/

        // 查询指定工号的每个月的级别
        sqlString = new StringBuilder()
                .append("SELECT T.RANKING FROM BAO_T_COMMISSION_RATE T WHERE T.JOB_NO = ? GROUP BY T.RANKING ");
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlString.toString(), new Object[]{params.get("jobNo")});
        String[] ranks = {};
        if (CommonUtils.isEmpty(list)) {
            result.put("teamTotalMonthlyIncomeAmount", 0);
            result.put("teamTotalMonthlyInvestAmount", 0);
        } else {
            ranks = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                String rank = CommonUtils.emptyToString(map.get("RANKING"));
                ranks[i] = rank;
            }
        }
        List<Object> args = Lists.newArrayList();
        sqlString = new StringBuilder()
                .append("SELECT NVL(SUM(S.INVEST_AMOUNT), 0) \"teamTotalMonthlyIncomeAmount\", ")
                .append("       NVL(SUM(S.HOLD_AMOUNT), 0) \"teamTotalMonthlyInvestAmount\" ")
                .append("  FROM BAO_T_COMMISSION_SENDBACK S WHERE 1 = 1 AND (");

        String jobNo_col;
        for (int i = 0; i < ranks.length; i++) {
            if ("1".equals(ranks[i])) {
                jobNo_col = "JOB_NO";
            } else {
                jobNo_col = "RANK_0" + ranks[i] + "_JOBNO";
            }
            if (i != 0) {
                sqlString.append(" OR ");
            }
            sqlString.append(" S." + jobNo_col + " = ? ");
            args.add(params.get("jobNo"));
        }
        sqlString.append("  ) AND S.EFFECT_DATE >= TO_DATE(?, 'YYYYMMDD') ")
                .append("   AND S.EFFECT_DATE < TO_DATE(?, 'YYYYMMDD') ")
        ;
        args.add(preMonth + "01");
        args.add(currentMonth + "01");
        List<Map<String, Object>> list7 = repositoryUtil.queryForMap(sqlString.toString(), args.toArray());
        if (list7 != null && list7.size() > 0) {
            result.putAll(list7.get(0));
        } else {
            result.put("teamTotalMonthlyIncomeAmount", 0);
            result.put("teamTotalMonthlyInvestAmount", 0);
        }

        /** 2017-5-11 查询团队累计进账金额、累计年化投资金额 */
        /*if (CommonUtils.isEmpty(ranking) || "0".equals(ranking)) {
            result.put("teamTotalIncomeAmount", 0);
            result.put("teamTotalInvestAmount", 0);
        } else {
            String jobNo_col;
            if ("1".equals(ranking)) {
                jobNo_col = "JOB_NO";
            } else {
                jobNo_col = "RANK_0" + ranking + "_JOBNO";
            }
            sqlString = new StringBuilder()
                    .append("SELECT NVL(SUM(S.HOLD_AMOUNT), 0) \"teamTotalInvestAmount\" ")
                    .append("  FROM BAO_T_COMMISSION_SENDBACK S ")
                    .append(" WHERE S." + jobNo_col + " = ?")
            ;
            List<Map<String, Object>> list8 = repositoryUtil.queryForMap(sqlString.toString(), new Object[] {params.get("jobNo")});
            if (list8 != null && list8.size() > 0) {
                result.putAll(list8.get(0));
            } else {
                result.put("teamTotalInvestAmount", 0);
            }
            sqlString = new StringBuilder()
                    .append("SELECT NVL(SUM(INVEST_AMOUNT), 0) \"teamTotalIncomeAmount\" ")
                    .append("  FROM BAO_T_COMMISSION_SENDBACK S ")
                    .append(" WHERE S." + jobNo_col + " = ? ")
                    .append(" AND S.CREATE_DATE IN (SELECT MIN(CREATE_DATE) ")
                    .append("                         FROM BAO_T_COMMISSION_SENDBACK T ")
                    .append("                        WHERE T." + jobNo_col + " = S." + jobNo_col + " ")
                    .append("                        GROUP BY T.INVEST_ID) ")
            ;
            List<Map<String, Object>> list9 = repositoryUtil.queryForMap(sqlString.toString(), new Object[] {params.get("jobNo")});
            if (list9 != null && list9.size() > 0) {
                result.putAll(list9.get(0));
            } else {
                result.put("teamTotalIncomeAmount", 0);
            }
        }*/
        sqlString = new StringBuilder()
                .append("SELECT NVL(SUM(S.HOLD_AMOUNT), 0) \"teamTotalInvestAmount\" ")
                .append("  FROM BAO_T_COMMISSION_SENDBACK S ")
                .append(" WHERE 1 = 1 AND (")
        ;
        args = Lists.newArrayList();
        for (int i = 0; i < ranks.length; i++) {
            if ("1".equals(ranks[i])) {
                jobNo_col = "JOB_NO";
            } else {
                jobNo_col = "RANK_0" + ranks[i] + "_JOBNO";
            }
            if (i != 0) {
                sqlString.append(" OR ");
            }
            sqlString.append(" S." + jobNo_col + " = ? ");

            args.add(params.get("jobNo"));
        }
        sqlString.append(")");

        List<Map<String, Object>> list8 = repositoryUtil.queryForMap(sqlString.toString(), args.toArray());
        if (list8 != null && list8.size() > 0) {
            result.putAll(list8.get(0));
        } else {
            result.put("teamTotalInvestAmount", 0);
        }

        sqlString = new StringBuilder("SELECT T.INVEST_ID, T.INVEST_AMOUNT FROM BAO_T_COMMISSION_SENDBACK T WHERE 1 = 1 AND (");
        args = Lists.newArrayList();
        for (int i = 0; i < ranks.length; i++) {
            if ("1".equals(ranks[i])) {
                jobNo_col = "JOB_NO";
            } else {
                jobNo_col = "RANK_0" + ranks[i] + "_JOBNO";
            }
            if (i != 0) {
                sqlString.append(" OR ");
            }
            sqlString.append(" T." + jobNo_col + " = ? ");
            args.add(params.get("jobNo"));
        }
        sqlString.append(")");

        List<Map<String, Object>> list9 = repositoryUtil.queryForMap(sqlString.toString(), args.toArray());
        if (CommonUtils.isEmpty(list9)) {
            result.put("teamTotalIncomeAmount", 0);
        } else {
            Map<String, BigDecimal> tempMap = new HashMap<>();
            for (Map<String, Object> map : list9) {
                String invest_id = CommonUtils.emptyToString(map.get("INVEST_ID"));
                BigDecimal invest_amount = CommonUtils.emptyToDecimal(map.get("INVEST_AMOUNT"));
                tempMap.put(invest_id, invest_amount);
            }
            BigDecimal tempAmt = BigDecimal.ZERO;
            for (String invest_id : tempMap.keySet()) {
                BigDecimal invest_amount = tempMap.get(invest_id);
                tempAmt = ArithUtil.add(tempAmt, invest_amount);
            }
            result.put("teamTotalIncomeAmount", tempAmt);
        }

        return result;
	}

    /**
     * 计算非流转标的年化投资额
     *
     * @param yearInvestAmtSum  总年化投资额
     * @param investAmount      投资金额
     * @param loanUnit          标的单位
     * @param loanTerm          标的期数
     * @return
     */
    private BigDecimal getYearAmtForUnTransable(BigDecimal yearInvestAmtSum, BigDecimal investAmount, String loanUnit, int loanTerm) {
    /*
     *  非流转标
     *  年化投资额 = 投资金额 * 折年系数
     */
        double y1 = 0d;
        if ("月".equals(loanUnit)) {
            // 月标折年系数 = (标的期数*30)/360
            y1 = ArithUtil.div(ArithUtil.mul(loanTerm, 30), 360);
        } else if ("天".equals(loanUnit)) {
            // 天标折年系数 = 标的期数/360
            if (28 == loanTerm) {
                // 如果标的天数是28天，也当做30天来计算
                y1 = ArithUtil.div((double) 30, 360);
            } else {
                y1 = ArithUtil.div((double) loanTerm, 360);
            }
        }
        // 非流转标业绩 = 存续金额*折年系数
        return ArithUtil.add(yearInvestAmtSum, ArithUtil.mul(investAmount, new BigDecimal(y1)));
    }

    @Override
	public Page<Map<String, Object>> findProjectAndWealthCommission(
			Map<String, Object> params) {
		
		/*String currentMonth = (String) params.get("currentMonth");
		
		StringBuilder sqlString = new StringBuilder()
		.append("       select \"custId\", \"quiltCustId\",  ")
		.append("        \"productId\", \"investId\", \"investAmount\", ")
		.append("        \"investStatus\", \"investDate\",  ")
		.append("        \"typeTerm\", \"lendingType\", \"lendingNo\", ")
		.append("        \"custName\", \"mobile\", \"credentialsCode\", \"registerDate\", ")
		.append("        \"yearRate\", \"awardRate\", \"custMobile\", \"productType\", \"typeUnit\" ")
		.append("        from ( ")
		.append("           select s.cust_id \"custId\", s.quilt_cust_id \"quiltCustId\",  ")
		.append("            t.project_id \"productId\", t.id \"investId\", t.invest_amount \"investAmount\", ")
		.append("            decode(m.project_status, '发布中','投资中',  '满标复核','投资中', '还款中','收益中', '已逾期','收益中', '已到期','已结束', '提前结清','已结束', '流标','已结束') \"investStatus\", t.create_date \"investDate\",  ")
		.append("            m.type_term \"typeTerm\", m.project_name \"lendingType\", m.project_no \"lendingNo\", ")
		.append("            c.cust_name \"custName\", substr(c.MOBILE, 1, 3) || '****' || substr(c.mobile, -3) \"mobile\", substr(c.CREDENTIALS_CODE, 1, 4) || '****' || substr(c.credentials_code, -4) \"credentialsCode\", c.create_date \"registerDate\", ")
		.append("            m.year_rate \"yearRate\", m.award_rate \"awardRate\", c.MOBILE \"custMobile\", ")
		.append("            '企业借款' \"productType\", '月' \"typeUnit\" ")
		.append("            from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_project_info m, bao_t_cust_info c  ")
		.append("           where t.cust_id = s.quilt_cust_id and t.project_id = m.id and t.cust_id = c.id ")
		.append("             and t.project_id is not null  ")
		.append("             and t.effect_date >= ? and t.effect_date < ? ")
		.append("             and m.project_status in ('还款中','已逾期','已到期','提前结清') ")
		//.append("             and s.start_date > add_months(sysdate, -12 * 2)     ")
		.append("             and s.record_status = '有效'  ")
		.append("             and t.cust_id not in (select id    ")
		.append("                                   from bao_t_cust_info    ")
		.append("                                   where is_recommend = '是' )   ")
		.append("             and s.cust_id = ?  ") // 查询当月业务员旗下所有客户【项目】投资情况
		.append("           union all  ")
		.append("           select t.cust_id \"custId\", t.cust_id \"quiltCustId\",  ")
		.append("           t.project_id \"productId\", t.id \"investId\", t.invest_amount investAmount, ")
		.append("            decode(m.project_status, '发布中','投资中',  '满标复核','投资中', '还款中','收益中', '已逾期','收益中', '已到期','已结束', '提前结清','已结束', '流标','已结束') \"investStatus\", t.create_date \"investDate\",  ")
		.append("            m.type_term \"typeTerm\", m.project_name \"lendingType\", m.project_no \"lendingNo\", ")
		.append("            c.cust_name \"custName\", substr(c.MOBILE, 1, 3) || '****' || substr(c.mobile, -3) \"mobile\", substr(c.CREDENTIALS_CODE, 1, 4) || '****' || substr(c.credentials_code, -4) \"credentialsCode\", c.create_date \"registerDate\", ")
		.append("            m.year_rate \"yearRate\", m.award_rate \"awardRate\", c.MOBILE \"custMobile\", ")
		.append("            '企业借款' \"productType\", '月' \"typeUnit\" ")
		.append("            from bao_t_invest_info t, bao_t_project_info m, bao_t_cust_info c     ")
		.append("           where t.project_id = m.id and t.cust_id = c.id and t.project_id is not null  ")
		.append("           and t.cust_id in (select id    ")
		.append("                             from bao_t_cust_info    ")
		.append("                             where is_recommend = '是' )    ")
		.append("           and t.effect_date >= ? and t.effect_date < ? ")
		.append("           and m.project_status in ('还款中','已逾期','已到期','提前结清') ")
		.append("           and t.cust_id = ?  ") // 查询当月业务员自己【项目】投资情况
		.append("           union all ")
		.append("           select p.cust_id \"custId\", q.quilt_cust_id \"quiltCustId\",  ")
		.append("            t.project_id \"productId\", t.id \"investId\", t.invest_amount \"investAmount\",  ")
		.append("            decode(m.project_status, '发布中','投资中',  '满标复核','投资中', '还款中','收益中', '已逾期','收益中', '已到期','已结束', '提前结清','已结束', '流标','已结束') \"investStatus\", t.create_date \"investDate\",  ")
		.append("            m.type_term \"typeTerm\", m.project_name \"lendingType\", m.project_no \"lendingNo\", ")
		.append("            c.cust_name \"custName\", substr(c.MOBILE, 1, 3) || '****' || substr(c.mobile, -3) \"mobile\", substr(c.CREDENTIALS_CODE, 1, 4) || '****' || substr(c.credentials_code, -4) \"credentialsCode\", c.create_date \"registerDate\", ")
		.append("            m.year_rate \"yearRate\", m.award_rate \"awardRate\", c.MOBILE \"custMobile\", ")
		.append("            '企业借款' \"productType\", '月' \"typeUnit\" ")
		.append("           from bao_t_commission_info p, bao_t_commission_detail_info q, bao_t_invest_info t, bao_t_project_info m, bao_t_cust_info c  ")
		.append("           where p.id = q.commission_id and p.product_type_id = '5' and q.invest_id = t.id and t.project_id = m.id and t.cust_id = c.id ")
		.append("           and p.cust_id = ? ") // 查询历史业务员旗下所有客户【项目】投资情况
		.append("           and m.project_status in ('还款中','已逾期','已到期','提前结清') ")
		.append("           union all ")
		.append("           select s.cust_id \"custId\", s.quilt_cust_id \"quiltCustId\",  ")
		.append("            t.project_id \"productId\", t.id \"investId\", t.invest_amount \"investAmount\", ")
		.append("            t.invest_status \"investStatus\", t.create_date \"investDate\",  ")
		.append("            m.type_term \"typeTerm\", m.lending_type || n.lending_no || '期' \"lendingType\", n.lending_no \"lendingNo\", ")
		.append("            c.cust_name \"custName\", substr(c.MOBILE, 1, 3) || '****' || substr(c.mobile, -3) \"mobile\", substr(c.CREDENTIALS_CODE, 1, 4) || '****' || substr(c.credentials_code, -4) \"credentialsCode\", c.create_date \"registerDate\", ")
		.append("            n.year_rate \"yearRate\", n.award_rate \"awardRate\", c.MOBILE \"custMobile\", ")
		.append("            '优选计划' \"productType\", '月' \"typeUnit\" ")
		.append("            from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_wealth_info n, bao_t_wealth_type_info m, bao_t_cust_info c  ")
		.append("           where t.cust_id = s.quilt_cust_id and t.wealth_id = n.id and n.wealth_type_id = m.id and t.cust_id = c.id ")
		.append("             and t.wealth_id is not null  ")
		.append("             and t.effect_date >= ? and t.effect_date < ? ")
		.append("             and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回') ")
		//.append("             and s.start_date > add_months(sysdate, -12 * 2)     ")
		.append("             and s.record_status = '有效'  ")
		.append("             and t.cust_id not in (select id    ")
		.append("                                   from bao_t_cust_info    ")
		.append("                                   where is_recommend = '是' )    ")
		.append("             and s.cust_id = ? ") // 查询当月业务员旗下所有客户【计划】投资情况
		.append("           union all  ")
		.append("           select t.cust_id \"custId\", t.cust_id \"quiltCustId\",  ")
		.append("           t.project_id \"productId\", t.id \"investId\", t.invest_amount investAmount, ")
		.append("            t.invest_status \"investStatus\", t.create_date \"investDate\",  ")
		.append("            m.type_term \"typeTerm\", m.lending_type || n.lending_no || '期' \"lendingType\", n.lending_no \"lendingNo\", ")
		.append("            c.cust_name \"custName\", substr(c.MOBILE, 1, 3) || '****' || substr(c.mobile, -3) \"mobile\", substr(c.CREDENTIALS_CODE, 1, 4) || '****' || substr(c.credentials_code, -4) \"credentialsCode\", c.create_date \"registerDate\", ")
		.append("            n.year_rate \"yearRate\", n.award_rate \"awardRate\", c.MOBILE \"custMobile\", ")
		.append("            '优选计划' \"productType\", '月' \"typeUnit\" ")
		.append("            from bao_t_invest_info t, bao_t_wealth_info n, bao_t_wealth_type_info m, bao_t_cust_info c     ")
		.append("           where t.wealth_id = n.id and n.wealth_type_id = m.id and t.cust_id = c.id  ")
		.append("           and t.wealth_id is not null  ")
		.append("           and t.cust_id in (select id    ")
		.append("                             from bao_t_cust_info    ")
		.append("                             where is_recommend = '是' )    ")
		.append("           and t.effect_date >= ? and t.effect_date < ? ")
		.append("           and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回') ")
		.append("           and t.cust_id = ?  ")// 查询当月业务员自己【计划】投资情况
		.append("           union all ")
		.append("           select p.cust_id \"custId\", q.quilt_cust_id \"quiltCustId\",  ")
		.append("            t.project_id \"productId\", t.id \"investId\", t.invest_amount \"investAmount\",  ")
		.append("            t.invest_status \"investStatus\", t.create_date \"investDate\",  ")
		.append("            m.type_term \"typeTerm\", m.lending_type || n.lending_no || '期' \"lendingType\", n.lending_no \"lendingNo\", ")
		.append("            c.cust_name \"custName\", substr(c.MOBILE, 1, 3) || '****' || substr(c.mobile, -3) \"mobile\", substr(c.CREDENTIALS_CODE, 1, 4) || '****' || substr(c.credentials_code, -4) \"credentialsCode\", c.create_date \"registerDate\", ")
		.append("            n.year_rate \"yearRate\", n.award_rate \"awardRate\", c.MOBILE \"custMobile\", ")
		.append("            '优选计划' \"productType\", '月' \"typeUnit\" ")
		.append("           from bao_t_commission_info p, bao_t_commission_detail_info q, bao_t_invest_info t, bao_t_wealth_info n, bao_t_wealth_type_info m, bao_t_cust_info c  ")
		.append("           where p.id = q.commission_id and p.product_type_id = '4' and q.invest_id = t.id and t.wealth_id = n.id and n.wealth_type_id = m.id and t.cust_id = c.id ")
		.append("           and p.cust_id = ? ")// 查询历史业务员旗下所有客户【计划】投资情况
		.append("           and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回') ")
		.append("           union all ")
		.append("           select s.cust_id \"custId\", s.quilt_cust_id \"quiltCustId\",  ")
		.append("            t.loan_id \"productId\", t.id \"investId\", t.invest_amount \"investAmount\", ")
		.append("            t.invest_status \"investStatus\", t.create_date \"investDate\",  ")
		.append("            n.loan_term \"typeTerm\", n.LOAN_TITLE \"lendingType\", n.loan_code \"lendingNo\", ")
		.append("            c.cust_name \"custName\", substr(c.MOBILE, 1, 3) || '****' || substr(c.mobile, -3) \"mobile\", substr(c.CREDENTIALS_CODE, 1, 4) || '****' || substr(c.credentials_code, -4) \"credentialsCode\", c.create_date \"registerDate\", ")
		.append("            m.year_irr \"yearRate\", 0 \"awardRate\", c.MOBILE \"custMobile\", ")
		.append("            '优选项目' \"productType\", n.loan_unit \"typeUnit\" ")
		.append("            from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_loan_info n, bao_t_loan_detail_info m, bao_t_cust_info c  ")
		.append("           where t.cust_id = s.quilt_cust_id and t.loan_id = n.id and n.id = m.loan_id and t.cust_id = c.id ")
		.append("             and t.loan_id is not null  ")
		.append("             and t.effect_date >= ? and t.effect_date < ? ")
		.append("             and t.invest_status in ('收益中','提前结清','已到期') ")
		//.append("             and s.start_date > add_months(sysdate, -12 * 2)     ")
		.append("             and s.record_status = '有效'  ")
		.append("             and t.cust_id not in (select id    ")
		.append("                                   from bao_t_cust_info    ")
		.append("                                   where is_recommend = '是' )    ")
		.append("             and s.cust_id = ? ") // 查询当月业务员旗下所有客户【散标】投资情况
		.append("           union all  ")
		.append("           select t.cust_id \"custId\", t.cust_id \"quiltCustId\",  ")
		.append("           t.loan_id \"productId\", t.id \"investId\", t.invest_amount investAmount, ")
		.append("            t.invest_status \"investStatus\", t.create_date \"investDate\",  ")
		.append("            n.loan_term \"typeTerm\", n.LOAN_TITLE \"lendingType\", n.loan_code \"lendingNo\", ")
		.append("            c.cust_name \"custName\", substr(c.MOBILE, 1, 3) || '****' || substr(c.mobile, -3) \"mobile\", substr(c.CREDENTIALS_CODE, 1, 4) || '****' || substr(c.credentials_code, -4) \"credentialsCode\", c.create_date \"registerDate\", ")
		.append("            m.year_irr \"yearRate\", 0 \"awardRate\", c.MOBILE \"custMobile\", ")
		.append("            '优选项目' \"productType\", n.loan_unit \"typeUnit\" ")
		.append("            from bao_t_invest_info t, bao_t_loan_info n, bao_t_loan_detail_info m, bao_t_cust_info c     ")
		.append("           where t.loan_id = n.id and n.id = m.loan_id and t.cust_id = c.id  ")
		.append("           and t.loan_id is not null  ")
		.append("           and t.cust_id in (select id    ")
		.append("                             from bao_t_cust_info    ")
		.append("                             where is_recommend = '是' )    ")
		.append("           and t.effect_date >= ? and t.effect_date < ? ")
		.append("           and t.invest_status in ('收益中','提前结清','已到期') ")
		.append("           and t.cust_id = ?  ")// 查询当月业务员自己【散标】投资情况
		.append("           union all ")
		.append("           select p.cust_id \"custId\", q.quilt_cust_id \"quiltCustId\",  ")
		.append("            t.project_id \"productId\", t.id \"investId\", t.invest_amount \"investAmount\",  ")
		.append("            t.invest_status \"investStatus\", t.create_date \"investDate\",  ")
		.append("            n.loan_term \"typeTerm\", n.LOAN_TITLE \"lendingType\", n.loan_code \"lendingNo\", ")
		.append("            c.cust_name \"custName\", substr(c.MOBILE, 1, 3) || '****' || substr(c.mobile, -3) \"mobile\", substr(c.CREDENTIALS_CODE, 1, 4) || '****' || substr(c.credentials_code, -4) \"credentialsCode\", c.create_date \"registerDate\", ")
		.append("            m.year_irr \"yearRate\", 0 \"awardRate\", c.MOBILE \"custMobile\", ")
		.append("            '优选项目' \"productType\", n.loan_unit \"typeUnit\" ")
		.append("           from bao_t_commission_info p, bao_t_commission_detail_info q, bao_t_invest_info t, bao_t_loan_info n, bao_t_loan_detail_info m, bao_t_cust_info c  ")
		.append("           where p.id = q.commission_id and p.product_type_id = '5' and q.invest_id = t.id and t.loan_id = n.id and n.id = m.loan_id and t.cust_id = c.id ")
		.append("           and p.cust_id = ? ")// 查询历史业务员旗下所有客户【散标】投资情况
		.append("           and t.invest_status in ('收益中','提前结清','已到期') ")
		.append("        )q ")
		.append("        where 1 = 1 ");
		
		List<Object> objList = Lists.newArrayList();
		objList.add(currentMonth + "01");
		objList.add(DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"));
		objList.add((String)params.get("custId"));
		objList.add(currentMonth + "01");
		objList.add(DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"));
		objList.add((String)params.get("custId"));
		objList.add((String)params.get("custId"));
		objList.add(currentMonth + "01");
		objList.add(DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"));
		objList.add((String)params.get("custId"));
		objList.add(currentMonth + "01");
		objList.add(DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"));
		objList.add((String)params.get("custId"));
		objList.add((String)params.get("custId"));
		objList.add(currentMonth + "01");
		objList.add(DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"));
		objList.add((String)params.get("custId"));
		objList.add(currentMonth + "01");
		objList.add(DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"));
		objList.add((String)params.get("custId"));
		objList.add((String)params.get("custId"));*/
		
		StringBuilder sqlString = new StringBuilder()
		.append("  select \"custId\", \"quiltCustId\",   ")
		.append("       \"productId\", \"investId\", \"investAmount\",  ")
		.append("       \"investStatus\", \"investDate\",   ")
		.append("       \"effectDate\", ")
		.append("       \"typeTerm\", \"lendingType\", \"lendingNo\",  ")
		.append("       \"custName\", \"mobile\", \"credentialsCode\", \"registerDate\",  ")
		.append("       \"yearRate\", \"awardRate\", \"custMobile\", ")
		.append("       \"productType\", \"typeUnit\",  \"investMode\", ")
		.append("       \"newerFlag\" ")
		.append("       from (                ")
		.append("          select p.cust_id \"custId\", q.quilt_cust_id \"quiltCustId\",   ")
		.append("           t.project_id \"productId\", t.id \"investId\", t.invest_amount \"investAmount\",   ")
		.append("           decode(m.project_status, '发布中','投资中',  '满标复核','投资中', '还款中','收益中', '已逾期','收益中', '已到期','已结束', '提前结清','已结束', '流标','已结束') \"investStatus\", t.create_date \"investDate\",   ")
		.append("           to_date(t.EFFECT_DATE,'yyyy-MM-dd') \"effectDate\", ")
		.append("           m.type_term \"typeTerm\", m.project_name \"lendingType\", m.project_no \"lendingNo\",  ")
		.append("           c.cust_name \"custName\", substr(c.MOBILE, 1, 3) || '****' || substr(c.mobile, -3) \"mobile\", substr(c.CREDENTIALS_CODE, 1, 4) || '****' || substr(c.credentials_code, -4) \"credentialsCode\", c.create_date \"registerDate\",  ")
		.append("           m.year_rate \"yearRate\", m.award_rate \"awardRate\", c.MOBILE \"custMobile\",  ")
		.append("           '企业借款' \"productType\", '月' \"typeUnit\", t.invest_mode \"investMode\"  ")
		.append("           , '普通标' \"newerFlag\" ")
		.append("          from bao_t_commission_info p, bao_t_commission_detail_info q, bao_t_invest_info t, bao_t_project_info m, bao_t_cust_info c   ")
		.append("          where p.id = q.commission_id and p.product_type_id = '6' and q.invest_id = t.id and t.project_id = m.id and t.cust_id = c.id  ")
		.append("          and p.cust_id = ?   ") // 查询历史业务员旗下所有客户【项目】投资情况
		.append("          and m.project_status in ('还款中','已逾期','已到期','提前结清')                ")
		.append("          union all  ")
		.append("          select p.cust_id \"custId\", q.quilt_cust_id \"quiltCustId\",   ")
		.append("           t.project_id \"productId\", t.id \"investId\", t.invest_amount \"investAmount\",   ")
		.append("           t.invest_status \"investStatus\", t.create_date \"investDate\",   ")
		.append("           to_date(t.EFFECT_DATE,'yyyy-MM-dd') \"effectDate\", ")
		.append("           m.type_term \"typeTerm\", m.lending_type || n.lending_no || '期' \"lendingType\", n.lending_no \"lendingNo\",  ")
		.append("           c.cust_name \"custName\", substr(c.MOBILE, 1, 3) || '****' || substr(c.mobile, -3) \"mobile\", substr(c.CREDENTIALS_CODE, 1, 4) || '****' || substr(c.credentials_code, -4) \"credentialsCode\", c.create_date \"registerDate\",  ")
		.append("           n.year_rate \"yearRate\", n.award_rate \"awardRate\", c.MOBILE \"custMobile\",  ")
		.append("           '优选计划' \"productType\", '月' \"typeUnit\", t.invest_mode \"investMode\"  ")
		.append("           , '普通标' \"newerFlag\" ")
		.append("          from bao_t_commission_info p, bao_t_commission_detail_info q, bao_t_invest_info t, bao_t_wealth_info n, bao_t_wealth_type_info m, bao_t_cust_info c   ")
		.append("          where p.id = q.commission_id and p.product_type_id = '4' and q.invest_id = t.id and t.wealth_id = n.id and n.wealth_type_id = m.id and t.cust_id = c.id  ")
		.append("          and p.cust_id = ?  ") // 查询历史业务员旗下所有客户【计划】投资情况
		.append("          and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回')  ")
		.append("          union all  ")
		.append("          select p.cust_id \"custId\", q.quilt_cust_id \"quiltCustId\",   ")
		.append("           t.loan_id \"productId\", t.id \"investId\", t.invest_amount \"investAmount\",   ")
		.append("           t.invest_status \"investStatus\", t.create_date \"investDate\",   ")
		.append("           to_date(t.EFFECT_DATE,'yyyy-MM-dd') \"effectDate\", ")
		.append("           n.loan_term \"typeTerm\", n.LOAN_TITLE \"lendingType\", n.loan_code \"lendingNo\",  ")
		.append("           c.cust_name \"custName\", substr(c.MOBILE, 1, 3) || '****' || substr(c.mobile, -3) \"mobile\", substr(c.CREDENTIALS_CODE, 1, 4) || '****' || substr(c.credentials_code, -4) \"credentialsCode\", c.create_date \"registerDate\",  ")
		.append("           m.year_irr \"yearRate\", 0 \"awardRate\", c.MOBILE \"custMobile\",  ")
		.append("           decode(t.invest_mode, '加入', '优选项目', '债权转让') \"productType\", n.loan_unit \"typeUnit\", t.invest_mode \"investMode\"  ")
		.append("           , nvl(n.newer_flag,'普通标') \"newerFlag\" ")
		.append("          from bao_t_commission_info p, bao_t_commission_detail_info q, bao_t_invest_info t, bao_t_loan_info n, bao_t_loan_detail_info m, bao_t_cust_info c   ")
		.append("          where p.id = q.commission_id and p.product_type_id = '5' and q.invest_id = t.id and t.loan_id = n.id and n.id = m.loan_id and t.cust_id = c.id  ")
		.append("          and p.cust_id = ?  ") // 查询历史业务员旗下所有客户【散标】投资情况
		.append("          and t.invest_status in ('收益中','提前结清','已到期', '已转让')  ")
		.append("       )q  ")
		.append("       where 1 = 1  ");
		
		List<Object> objList = Lists.newArrayList();
		objList.add((String)params.get("custId"));
		objList.add((String)params.get("custId"));
		objList.add((String)params.get("custId"));
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, params, objList);
		sqlCondition.addString("custName", "q.\"custName\"")
					.addString("credentialsCode", "q.\"credentialsCode\"")
					.addString("mobile", "q.\"custMobile\"")
					.addBeginDate("beginInvestDate", "q.\"investDate\"")
					.addEndDate("endInvestDate", "q.\"investDate\"")
					.addBeginDate("beginEffectDate", "q.\"effectDate\"")
					.addEndDate("endEffectDate", "q.\"effectDate\"")
					.addBeginDate("beginRegisterDate", "q.\"registerDate\"")
					.addEndDate("endRegisterDate", "q.\"registerDate\"");
		String sql = sqlCondition.toString() + " order by q.\"effectDate\" desc ";
		return repositoryUtil.queryForPageMap(sql, sqlCondition.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}

	@Override
	public void batchProjectAndWealthAndLoan(
			CommissionInfoEntity commissionInfoEntity) {
		
		// 插入优选项目提成明细
		StringBuilder sqlString = new StringBuilder()
		.append("    insert into bao_t_commission_detail_info  ")
		.append("     (id, commission_id, quilt_cust_id,   ")
		.append("      invest_amount, commission_amount, reward_amount,   ")
		.append("      trade_status, record_status, create_user,   ")
		.append("      create_date, last_update_user, last_update_date,   ")
		.append("      version, memo, relate_primary, relate_type, ")
		.append("      year_invest_amount, invest_id)  ")
		.append("   select  ")
		.append("     sys_guid(), b.commission_id, b.quiltCustId,   ")
		.append("      NVL(b.investAmount, 0), NVL(b.income, 0), NVL(b.award, 0),   ")
		.append("      ?, '有效', ?,   ")
		.append("      ?, ?, ?,   ")
		.append("      0, null, productId, 'BAO_T_LOAN_INFO', ")
		.append("      NVL(commissionInvestAmount, 0), investId  ")
		.append("      from  ")
		.append("      (  ")
		.append("       select custId || to_char(?, 'yyyyMMddHH24MISS') commission_id,   ")
		.append("        quiltCustId,   ")
		.append("        productId,   ")
		.append("        investId,   ")
		.append("        0 income,   ")
		.append("        0 award,   ")
		.append("        trunc(investAmount, 8) investAmount,  ")
		.append("        trunc(commissionInvestAmount, 8) commissionInvestAmount  ")
		.append("        from (  ")
		.append("               select q.custId, q.quiltCustId, q.productId, q.investId, sum(q.investAmount) investAmount, sum(q.commissionInvestAmount) commissionInvestAmount  ")
		.append("               from (   ")
		.append("                   select s.cust_id custId, s.quilt_cust_id quiltCustId, t.loan_id productId, t.id investId, sum(t.invest_amount) investAmount, ")
		.append("                   sum(t.invest_amount*n.rebate_ratio) commissionInvestAmount    ")
		.append("                    from bao_t_invest_info t, bao_t_cust_recommend_info s , bao_t_loan_info n  ")
		.append("                   where t.cust_id = s.quilt_cust_id and t.loan_id = n.id  ")
		.append("                     and t.loan_id is not null  ")
		.append("                     and t.invest_status in ('收益中','已到期','提前结清', '已转让')   ")
		.append("                     and t.effect_date >= ? and t.effect_date < ? ")
		.append("                     and s.record_status = '有效'  ")
		.append("                     and t.cust_id not in (select id    ")
		.append("                                           from bao_t_cust_info    ")
		.append("                                           where is_recommend = '是' and nvl(WORKING_STATE, '在职') != '离职' )    ")
//		.append("                     and t.create_date > ( ")
//		.append("                                           select max(b.last_update_date) ")
//		.append("                                           from bao_t_cust_apply_info a ")
//		.append("                                           inner join bao_t_audit_info b on b.relate_primary = a.id ")
//		.append("                                           where a.cust_id = s.cust_id     ")
//		.append("                                           and a.apply_type = '金牌推荐人申请'    ")
//		.append("                                           and b.apply_type = '金牌推荐人申请'    ")
//		.append("                   ) ")
		.append("                   group by s.cust_id, s.quilt_cust_id, t.loan_id, t.id   ")
		.append("                   union all  ")
		.append("                   select t.cust_id custId, t.cust_id quiltCustId, t.loan_id productId, t.id investId, sum(t.invest_amount) investAmount, ")
		.append("                   sum(t.invest_amount*n.rebate_ratio) commissionInvestAmount    ")
		.append("                    from bao_t_invest_info t , bao_t_loan_info n  ")
		.append("                   where t.loan_id = n.id  ")
		.append("                   and t.loan_id is not null  ")
		.append("                   and t.effect_date >= ? and t.effect_date < ? ")
		.append("                   and t.cust_id in (select id    ")
		.append("                                     from bao_t_cust_info    ")
		.append("                                     where is_recommend = '是' and nvl(WORKING_STATE, '在职') != '离职' )    ")		
		.append("                   and t.invest_status in ('收益中','已到期','提前结清', '已转让')  ")
//		.append("                   and t.create_date > ( ")
//		.append("                                           select max(b.last_update_date) ")
//		.append("                                           from bao_t_cust_apply_info a ")
//		.append("                                           inner join bao_t_audit_info b on b.relate_primary = a.id ")
//		.append("                                           where a.cust_id = t.cust_id     ")
//		.append("                                           and a.apply_type = '金牌推荐人申请'    ")
//		.append("                                           and b.apply_type = '金牌推荐人申请'    ")
//		.append("                   ) ")
		.append("                   group by t.cust_id, t.loan_id, t.id   ")
		.append("               ) q   ")
		.append("               where not exists ( ")
		.append("                     select 1 ")
		.append("                     from bao_t_commission_detail_info c ")
		.append("                     where c.invest_id = q.investId ")
		.append("               ) ")
		.append("               group by q.custId, q.quiltCustId, q.productId, q.investId  ")
		.append("              ) p    ")
		.append("          ) b ");
		
		jdbcTemplate.update(sqlString.toString(), 
				new Object[]{	
								commissionInfoEntity.getTradeStatus(),
								commissionInfoEntity.getCreateUser(),
								commissionInfoEntity.getCreateDate(),
								commissionInfoEntity.getLastUpdateUser(),
								commissionInfoEntity.getLastUpdateDate(),
								commissionInfoEntity.getCreateDate(),
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
								commissionInfoEntity.getCommMonth() + "01",
								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd")} );	
		
//		// 插入优选计划提成明细
//		sqlString = new StringBuilder()
//		.append("    insert into bao_t_commission_detail_info  ")
//		.append("     (id, commission_id, quilt_cust_id,   ")
//		.append("      invest_amount, commission_amount, reward_amount,   ")
//		.append("      trade_status, record_status, create_user,   ")
//		.append("      create_date, last_update_user, last_update_date,   ")
//		.append("      version, memo, relate_primary, relate_type, ")
//		.append("      year_invest_amount, invest_id)  ")
//		.append("    select  ")
//		.append("     sys_guid(), b.commission_id, b.quiltCustId,   ")
//		.append("      NVL(b.investAmount, 0), NVL(b.income, 0), NVL(b.award, 0),   ")
//		.append("      ?, '有效', ?,   ")
//		.append("      ?, ?, ?,   ")
//		.append("      0, null, productId, 'BAO_T_WEALTH_INFO', ")
//		.append("      NVL(commissionInvestAmount, 0), investId  ")
//		.append("      from  ")
//		.append("      (  ")
//		.append("       select custId || to_char(?, 'yyyyMMddHH24MISS') commission_id, ")
//		.append("        quiltCustId,   ")
//		.append("        productId,   ")
//		.append("        investId,   ")
//		.append("        0 income,   ")
//		.append("        0 award,   ")
//		.append("        trunc(investAmount, 8) investAmount,  ")
//		.append("        trunc(commissionInvestAmount, 8) commissionInvestAmount  ")
//		.append("        from (  ")
//		.append("               select q.custId, q.quiltCustId, q.productId, q.investId, sum(q.investAmount) investAmount, sum(q.commissionInvestAmount) commissionInvestAmount  ")
//		.append("               from (   ")
//		.append("                   select s.cust_id custId, s.quilt_cust_id quiltCustId, t.wealth_id productId, t.id investId, sum(t.invest_amount) investAmount, ")
//		.append("                   sum(t.invest_amount*m.rebate_ratio) commissionInvestAmount    ")
//		.append("                    from bao_t_invest_info t, bao_t_cust_recommend_info s , bao_t_wealth_info n, bao_t_wealth_type_info m  ")
//		.append("                   where t.cust_id = s.quilt_cust_id and t.wealth_id = n.id and n.wealth_type_id = m.id  ")
//		.append("                     and t.wealth_id is not null  ")
//		.append("                     and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回')  ")
//		.append("                     and t.effect_date >= ? and t.effect_date < ? ")
//		.append("                     and s.record_status = '有效'  ")
//		.append("                     and t.cust_id not in (select id    ")
//		.append("                                           from bao_t_cust_info    ")
//		.append("                                           where is_recommend = '是' )    ")
////		.append("                     and t.create_date > ( ")
////		.append("                                           select max(b.last_update_date) ")
////		.append("                                           from bao_t_cust_apply_info a ")
////		.append("                                           inner join bao_t_audit_info b on b.relate_primary = a.id ")
////		.append("                                           where a.cust_id = s.cust_id     ")
////		.append("                                           and a.apply_type = '金牌推荐人申请'    ")
////		.append("                                           and b.apply_type = '金牌推荐人申请'    ")
////		.append("                   ) ")
//		.append("                   group by s.cust_id, s.quilt_cust_id, t.wealth_id, t.id   ")
//		.append("                   union all  ")
//		.append("                   select t.cust_id custId, t.cust_id quiltCustId, t.wealth_id productId, t.id investId, sum(t.invest_amount) investAmount, ")
//		.append("                   sum(t.invest_amount*m.rebate_ratio) commissionInvestAmount    ")
//		.append("                    from bao_t_invest_info t , bao_t_wealth_info n, bao_t_wealth_type_info m  ")
//		.append("                   where t.wealth_id = n.id and n.wealth_type_id = m.id and t.wealth_id is not null  ")
//		.append("                   and t.cust_id in (select id    ")
//		.append("                                     from bao_t_cust_info    ")
//		.append("                                     where is_recommend = '是' )    ")
//		.append("                   and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回')  ")
//		.append("                   and t.effect_date >= ? and t.effect_date < ? ")
////		.append("                   and t.create_date > ( ")
////		.append("                                           select max(b.last_update_date) ")
////		.append("                                           from bao_t_cust_apply_info a ")
////		.append("                                           inner join bao_t_audit_info b on b.relate_primary = a.id ")
////		.append("                                           where a.cust_id = t.cust_id     ")
////		.append("                                           and a.apply_type = '金牌推荐人申请'    ")
////		.append("                                           and b.apply_type = '金牌推荐人申请'    ")
////		.append("                   ) ")
//		.append("                   group by t.cust_id, t.wealth_id, t.id   ")
//		.append("               ) q   ")
//		.append("               where not exists ( ")
//		.append("                     select 1 ")
//		.append("                     from bao_t_commission_detail_info c ")
//		.append("                     where c.invest_id = q.investId ")
//		.append("               ) ")
//		.append("               group by q.custId, q.quiltCustId, q.productId, q.investId  ")
//		.append("              ) p    ")
//		.append("          ) b ");
//		
//		jdbcTemplate.update(sqlString.toString(), 
//				new Object[]{	
//								commissionInfoEntity.getTradeStatus(),
//								commissionInfoEntity.getCreateUser(),
//								commissionInfoEntity.getCreateDate(),
//								commissionInfoEntity.getLastUpdateUser(),
//								commissionInfoEntity.getLastUpdateDate(),
//								DateUtils.getDateAfterByHour(commissionInfoEntity.getCreateDate(), 1),
//								commissionInfoEntity.getCommMonth() + "01",
//								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
//								commissionInfoEntity.getCommMonth() + "01",
//								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd")} );	
//		
//		// 插入企业借款提成明细
//		sqlString = new StringBuilder()
//		.append("    insert into bao_t_commission_detail_info  ")
//		.append("     (id, commission_id, quilt_cust_id,   ")
//		.append("      invest_amount, commission_amount, reward_amount,   ")
//		.append("      trade_status, record_status, create_user,   ")
//		.append("      create_date, last_update_user, last_update_date,   ")
//		.append("      version, memo, relate_primary, relate_type, ")
//		.append("      year_invest_amount, invest_id)  ")
//		.append("   select  ")
//		.append("     sys_guid(), b.commission_id, b.quiltCustId,   ")
//		.append("      NVL(b.investAmount, 0), NVL(b.income, 0), NVL(b.award, 0),   ")
//		.append("      ?, '有效', ?,   ")
//		.append("      ?, ?, ?,   ")
//		.append("      0, null, productId, 'BAO_T_PROJECT_INFO', ")
//		.append("      NVL(commissionInvestAmount, 0), investId  ")
//		.append("      from  ")
//		.append("      (  ")
//		.append("       select custId || to_char(?, 'yyyyMMddHH24MISS') commission_id, ")
//		.append("        quiltCustId,   ")
//		.append("        productId,   ")
//		.append("        investId,   ")
//		.append("        0 income,   ")
//		.append("        0 award,   ")
//		.append("        trunc(investAmount, 8) investAmount,  ")
//		.append("        trunc(commissionInvestAmount, 8) commissionInvestAmount  ")
//		.append("        from (  ")
//		.append("               select q.custId, q.quiltCustId, q.productId, q.investId, sum(q.investAmount) investAmount, sum(q.commissionInvestAmount) commissionInvestAmount  ")
//		.append("               from (   ")
//		.append("                   select s.cust_id custId, s.quilt_cust_id quiltCustId, t.project_id productId, t.id investId, sum(t.invest_amount) investAmount, ")
//		.append("                   sum(t.invest_amount*m.type_term/12) commissionInvestAmount    ")
//		.append("                    from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_project_info m  ")
//		.append("                   where t.cust_id = s.quilt_cust_id and t.project_id = m.id  ")
//		.append("                     and t.project_id is not null  ")
//		.append("                     and m.project_status in ('收益中','已逾期','已到期','提前结清')    ")
//		.append("                     and t.effect_date >= ? and t.effect_date < ? ")
//		.append("                     and s.record_status = '有效'  ")
//		.append("                     and t.cust_id not in (select id    ")
//		.append("                                           from bao_t_cust_info    ")
//		.append("                                           where is_recommend = '是' )    ")
////		.append("                     and t.create_date > ( ")
////		.append("                                           select max(b.last_update_date) ")
////		.append("                                           from bao_t_cust_apply_info a ")
////		.append("                                           inner join bao_t_audit_info b on b.relate_primary = a.id ")
////		.append("                                           where a.cust_id = s.cust_id     ")
////		.append("                                           and a.apply_type = '金牌推荐人申请'    ")
////		.append("                                           and b.apply_type = '金牌推荐人申请'    ")
////		.append("                   ) ")
//		.append("                   group by s.cust_id, s.quilt_cust_id, t.project_id, t.id   ")
//		.append("                   union all  ")
//		.append("                   select t.cust_id custId, t.cust_id quiltCustId, t.project_id productId, t.id investId, sum(t.invest_amount) investAmount, ")
//		.append("                   sum(t.invest_amount*m.type_term/12) commissionInvestAmount    ")
//		.append("                    from bao_t_invest_info t, bao_t_project_info m   ")
//		.append("                   where t.project_id = m.id and t.project_id is not null  ")
//		.append("                   and t.cust_id in (select id    ")
//		.append("                                     from bao_t_cust_info    ")
//		.append("                                     where is_recommend = '是' )    ")
//		.append("                   and m.project_status in ('收益中','已逾期','已到期','提前结清')  ")
//		.append("                   and t.effect_date >= ? and t.effect_date < ? ")
////		.append("                   and t.create_date > ( ")
////		.append("                                           select max(b.last_update_date) ")
////		.append("                                           from bao_t_cust_apply_info a ")
////		.append("                                           inner join bao_t_audit_info b on b.relate_primary = a.id ")
////		.append("                                           where a.cust_id = t.cust_id     ")
////		.append("                                           and a.apply_type = '金牌推荐人申请'    ")
////		.append("                                           and b.apply_type = '金牌推荐人申请'    ")
////		.append("                   ) ")
//		.append("                   group by t.cust_id, t.project_id, t.id   ")
//		.append("               ) q   ")
//		.append("               where not exists ( ")
//		.append("                     select 1 ")
//		.append("                     from bao_t_commission_detail_info c ")
//		.append("                     where c.invest_id = q.investId ")
//		.append("               ) ")
//		.append("               group by q.custId, q.quiltCustId, q.productId, q.investId  ")
//		.append("              ) p    ")
//		.append("          ) b ");
//		
//		jdbcTemplate.update(sqlString.toString(), 
//				new Object[]{	
//								commissionInfoEntity.getTradeStatus(),
//								commissionInfoEntity.getCreateUser(),
//								commissionInfoEntity.getCreateDate(),
//								commissionInfoEntity.getLastUpdateUser(),
//								commissionInfoEntity.getLastUpdateDate(),
//								DateUtils.getDateAfterByHour(commissionInfoEntity.getCreateDate(), 2),
//								commissionInfoEntity.getCommMonth() + "01",
//								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
//								commissionInfoEntity.getCommMonth() + "01",
//								DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(commissionInfoEntity.getCommMonth() + "01", "yyyyMMdd"), 1), "yyyyMMdd")} );
		
		// 插入提成表
		sqlString = new StringBuilder()
		.append("  insert into bao_t_commission_info  ")
		.append("   (id, cust_id, comm_date,   ")
		.append("    invest_amount, commission_amount, reward_amount,   ")
		.append("    trade_status, record_status, create_user,   ")
		.append("    create_date, last_update_user, last_update_date,   ")
		.append("    version, memo, year_invest_amount,  ")
//		.append("    comm_month, product_type_id)   ")
		.append("         comm_month, product_type_id, DEPT_NAME, PROVINCE_NAME, CITY_NAME)    ")
		.append("   select  ")
		.append("     commission_id, custId, ?,   ")
		.append("     NVL(investAmount, 0), NVL(income, 0), NVL(award, 0),   ")
		.append("     ?, '有效', ?,   ")
		.append("     ?, ?, ?,   ")
		.append("     0, null, NVL(commissionInvestAmount, 0),  ")
//		.append("     ?, decode(b.relate_type, 'BAO_T_LOAN_INFO', '5', 'BAO_T_WEALTH_INFO', '4', '6')  ")
		.append("          ?, decode(b.relate_type, 'BAO_T_LOAN_INFO', '5', 'BAO_T_WEALTH_INFO', '4', '6'), ")
		.append("          d.dept_Name,d.province_Name,d.city_Name ")
		.append("   from   ")
		.append("     (   ")
		.append("       select c.commission_id,  ")
		.append("        substr(c.commission_id, 1, length(c.commission_id) - 14)  custId, ")
		.append("        c.relate_type, ")
		.append("        0 income,   ")
		.append("        0 award,   ")
		.append("         trunc(sum(c.invest_amount), 8) investAmount,  ")
		.append("              trunc(sum(c.year_invest_amount), 8) commissionInvestAmount ")
		.append("         from bao_t_commission_detail_info c ")
		.append("         where to_char(c.create_date, 'yyyyMMddhh24miss') = to_char(?, 'yyyyMMddhh24miss') ")
		.append("         group by c.commission_id, c.relate_type    ")
		.append("         ) b left join bao_t_business_dept_info d on d.cust_id = b.custId and d.BUSSINESS_IMPORT_ID = ? ");
		
		String id = "";
		BusinessImportInfoEntity businessImportInfoEntity = businessImportInfoRepository.findByMaxCreateDate();
		if(businessImportInfoEntity != null) {
			id = businessImportInfoEntity.getId();
		}
		
		jdbcTemplate.update(sqlString.toString(), 
				new Object[]{	commissionInfoEntity.getCommDate(),
								commissionInfoEntity.getTradeStatus(),
								commissionInfoEntity.getCreateUser(),
								commissionInfoEntity.getCreateDate(),
								commissionInfoEntity.getLastUpdateUser(),
								commissionInfoEntity.getLastUpdateDate(),
								commissionInfoEntity.getCommMonth(),
								commissionInfoEntity.getCreateDate(),
								id} );
	}

    @Override
    public Page<Map<String, Object>> queryNextTeamCustWealthList(Map<String, Object> params) {
	    // 根据客户id查询工号
        StringBuilder sqlString = new StringBuilder();
        List<Object> objList;
        String ranking = params.get("ranking").toString();
        String jobNo = params.get("jobNo").toString();
        int start = Integer.parseInt(params.get("start").toString());
        int length = Integer.parseInt(params.get("length").toString());
        if (CommonUtils.isEmpty(ranking) || "0".equals(ranking)) {
            return null;
        }
        if ("1".equals(ranking)) {
            // TODO 级别为1的不调用当前接口，走原来的逻辑
            /*sqlString.append("SELECT T.JOB_NO \"jobNo\", ")
                    .append("        T.SHOP_NAME \"shopName\",")
                    .append("        T.RANKING \"subRanking\", ")
                    .append("        TRUNC(NVL(T.HOLD_AMOUNT, 0), 2) \"investAmount\"")
                    .append("   FROM BAO_T_COMMISSION_SENDBACK T")
                    .append("  WHERE T.JOB_NO = ? ")
                    .append("    AND T.EFFECT_DATE >= TO_DATE(?, 'YYYYMMDD') ")
                    .append("    AND T.EFFECT_DATE < TO_DATE(?, 'YYYYMMDD') + 1 ")
                    .append("  ORDER BY T.EFFECT_DATE DESC ");
            objList = Lists.newArrayList();
            objList.add(jobNo);
            // 起始时间默认为上个月1号
            Object beginInvestDate = CommonUtils.isEmpty(params.get("beginInvestDate"))
                    ? DateUtils.formatDate(DateUtils.getAfterMonth(new Date(), -1), "yyyyMM") + "01"
                    : params.get("beginInvestDate");
            objList.add(beginInvestDate);
            // 截止时间默认为上个月最后一天
            Object endInvestDate = CommonUtils.isEmpty(params.get("endInvestDate"))
                    ? DateUtils.getLastDay(DateUtils.getAfterMonth(new Date(), -1), "yyyyMMdd")
                    : params.get("endInvestDate");
            objList.add(endInvestDate);
                    */
            return null;
        } else {
            // 查询指定管理人工号的所有级别
            sqlString = new StringBuilder("SELECT T.RANKING FROM BAO_T_COMMISSION_RATE T WHERE T.JOB_NO = ? GROUP BY T.RANKING ");
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlString.toString(), new Object[]{jobNo});
            String[] ranks = {};
            ranks = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                String rank = CommonUtils.emptyToString(map.get("RANKING"));
                ranks[i] = rank;
            }
            List<Map<String, Object>> content = Lists.newArrayList();
            int total = 0;

            for (int n = 0; n < ranks.length; n++) {
                String currRank = ranks[n];
                if ("1".equals(currRank)) {
                    continue;
                }
                sqlString = new StringBuilder();
                sqlString.append("SELECT R.JOB_NO \"jobNo\", R.RANKING \"rank\" FROM BAO_T_COMMISSION_RATE R ")
                        .append(" WHERE R.JOB_NO IN (")
                        .append(" SELECT DISTINCT ");
                String str;
                if ("2".equals(currRank)) {
                    str = "T.JOB_NO";
                } else if ("3".equals(currRank)) {
                    str = "NVL(T.RANK_02_JOBNO, T.JOB_NO)";
                } else {
                    str = "NVL(T.RANK_0" + (Integer.valueOf(currRank) - 1) + "_JOBNO, T.RANK_0" + (Integer.valueOf(currRank) - 2) + "_JOBNO)";
                    for (int i = Integer.valueOf(currRank) - 3; i > 0; i--) {
                        if (i > 1) {
                            str = "NVL(" + str + ", T.RANK_0" + i + "_JOBNO)";
                        } else {
                            str = "NVL(" + str + ", T.JOB_NO)";
                        }
                    }
                }

                sqlString.append(str).append(" FROM BAO_T_COMMISSION_SENDBACK T WHERE T.RANK_0" + currRank + "_JOBNO = ? )");
                objList = Lists.newArrayList();
                objList.add(jobNo);
                if (!CommonUtils.isEmpty(params.get("managerName"))) {
                    sqlString.append(" AND R.CUST_NAME LIKE ? ");
                    objList.add("%" + params.get("managerName") + "%");
                }
                sqlString.append(" GROUP BY R.JOB_NO, R.RANKING ORDER BY R.JOB_NO");
                List<Map<String, Object>> subList = jdbcTemplate.queryForList(sqlString.toString(), objList.toArray());

                sqlString = new StringBuilder();
                objList = Lists.newArrayList();
                if (!CommonUtils.isEmpty(subList)) {
                    List<Map<String, Object>> temp = Lists.newArrayList();
                    for (Map<String, Object> map : subList) {
                        String jobNoCurr = CommonUtils.emptyToString(map.get("jobNo"));
                        if (!jobNo.equals(jobNoCurr)) {
                            temp.add(map);
                        }
                    }

                    sqlString = new StringBuilder();
                    objList = Lists.newArrayList();
                    List<String> jobTemp = Lists.newArrayList();
                    for (int i = 0; i < temp.size(); i++) {
                        Map<String, Object> rankingMap = temp.get(i);
                        String subJobNo = CommonUtils.emptyToString(rankingMap.get("jobNo"));
                        String subRanking = CommonUtils.emptyToString(rankingMap.get("rank"));
                        if (Integer.valueOf(subRanking) > 1) {
                            sqlString.append("SELECT T.INVEST_ID, T.RANK_0" + subRanking + "_JOBNO \"jobNo\", ")
                                    .append("        T.RANK_0" + subRanking + "_NAME \"subName\", ")
                                    .append("        " + subRanking + " \"subRanking\", ")
                                    .append("        T.INVEST_AMOUNT \"investAmount\" ")
                                    .append("  FROM BAO_T_COMMISSION_SENDBACK T WHERE T.JOB_NO != ? AND T.RANK_0" + subRanking + "_JOBNO = ? AND ( ");
                            objList.add(jobNo);
                            objList.add(subJobNo);

                            sqlString.append(" T.RANK_0" + currRank + "_JOBNO = ? ");
                            objList.add(jobNo);

//                            String jobNo_col;
//                            for (int j = 0; j < ranks.length; j++) {
//                                if (Integer.valueOf(ranks[j]) > Integer.valueOf(subRanking)) {
//                                    sqlString.append(" T.RANK_0" + ranks[j] + "_JOBNO = ? OR ");
//                                    objList.add(jobNo);
//                                }
//                            }
//                            sqlString = new StringBuilder(sqlString.substring(0, sqlString.lastIndexOf("OR ")));

                            sqlString.append("  ) AND T.CREATE_DATE IN (SELECT MIN(CREATE_DATE) ")
                                    .append("                           FROM BAO_T_COMMISSION_SENDBACK S ")
                                    .append("                          WHERE T.RANK_0" + subRanking + "_JOBNO = S.RANK_0" + subRanking + "_JOBNO ")
                                    .append("                          GROUP BY S.INVEST_ID) ");
                        } else {
                            sqlString.append("SELECT T.INVEST_ID, T.JOB_NO \"jobNo\", ")
                                    .append("        T.MANAGER_NAME \"subName\", ")
                                    .append("        " + subRanking + " \"subRanking\", ")
                                    .append("        T.INVEST_AMOUNT \"investAmount\" ")
                                    .append("  FROM BAO_T_COMMISSION_SENDBACK T WHERE T.JOB_NO = ? AND ( ");
                            objList.add(subJobNo);

                            sqlString.append(" T.RANK_02_JOBNO = ? ");
                            objList.add(jobNo);

                            sqlString.append("  ) AND T.CREATE_DATE IN (SELECT MIN(CREATE_DATE)")
                                    .append("                           FROM BAO_T_COMMISSION_SENDBACK S ")
                                    .append("                          WHERE T.JOB_NO = S.JOB_NO ")
                                    .append("                          GROUP BY S.INVEST_ID) ");
                        }
                        if (!CommonUtils.isEmpty(params.get("beginInvestDate"))) {
                            sqlString.append(" AND T.EFFECT_DATE >= TO_DATE(?, 'YYYY-MM-DD') ");
                            objList.add(params.get("beginInvestDate"));
                        }
                        if (!CommonUtils.isEmpty(params.get("endInvestDate"))) {
                            sqlString.append(" AND T.EFFECT_DATE < TO_DATE(?, 'YYYY-MM-DD') + 1 ");
                            objList.add(params.get("endInvestDate"));
                        }
                        if (Integer.valueOf(subRanking) > 1) {
                            sqlString.append("  GROUP BY T.INVEST_ID, T.RANK_0" + subRanking + "_JOBNO, T.RANK_0" + subRanking + "_NAME, T.INVEST_AMOUNT ");
                        } else {
                            sqlString.append("  GROUP BY T.INVEST_ID, T.JOB_NO, T.MANAGER_NAME, T.INVEST_AMOUNT ");
                        }

                        if (i < temp.size() - 1) {
                            sqlString.append(" UNION ALL ");
                        }
                    }
                }
                if (sqlString.length() > 0) {
                    sqlString.insert(0, "SELECT \"jobNo\", SUM(\"investAmount\") \"investAmount\"" +
                            ", \"subRanking\", \"subName\" " +// 增加查询级别、姓名
                            "  FROM (SELECT \"jobNo\", \"investAmount\" " +
                            " , \"subRanking\", \"subName\"" +// 增加查询级别、姓名
                            " FROM (");

                    sqlString.append(") GROUP BY INVEST_ID, \"jobNo\", \"investAmount\", \"subRanking\", \"subName\") ")
                            .append(" GROUP BY \"jobNo\", \"subRanking\", \"subName\" ORDER BY \"investAmount\" DESC");

//                        return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), start, length);

                    Page<Map<String, Object>> page = repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), start, length);
                    content.addAll(page.getContent());
                    total += page.getTotalElements();
                } else {
                    return null;
                }
            }

            return new PageImpl<Map<String, Object>>(content,new PageRequest(start, length), total);

            /*if ("2".equals(ranking)) {
                sqlString.append("SELECT T.JOB_NO \"jobNo\", ")
                        .append("        T.MANAGER_NAME \"subName\", ")
                        .append("        T.RANKING \"subRanking\", ")
                        .append("        SUM(T.INVEST_AMOUNT) \"investAmount\" ")
                        .append("   FROM BAO_T_COMMISSION_SENDBACK T ")
                        .append("  WHERE T.RANK_02_JOBNO = ? ")
                        .append("  AND T.CREATE_DATE IN (SELECT MIN(CREATE_DATE)")
                        .append("                           FROM BAO_T_COMMISSION_SENDBACK S")
                        .append("                          WHERE T.RANK_02_JOBNO = S.RANK_02_JOBNO")
                        .append("                          GROUP BY S.INVEST_ID) ")
                        ;
                objList = Lists.newArrayList();
                objList.add(jobNo);
                if (!CommonUtils.isEmpty(params.get("beginInvestDate"))) {
                    sqlString.append(" AND T.EFFECT_DATE >= TO_DATE(?, 'YYYY-MM-DD') ");
                    objList.add(params.get("beginInvestDate"));
                }
                if (!CommonUtils.isEmpty(params.get("endInvestDate"))) {
                    sqlString.append(" AND T.EFFECT_DATE < TO_DATE(?, 'YYYY-MM-DD') + 1 ");
                    objList.add(params.get("endInvestDate"));
                }
                if (!CommonUtils.isEmpty(params.get("managerName"))) {
                    sqlString.append(" AND T.MANAGER_NAME LIKE ? ");
                    objList.add("%" + params.get("managerName") + "%");
                }

                sqlString.append(" GROUP BY T.JOB_NO, T.MANAGER_NAME, T.RANKING")
                        .append(" ORDER BY SUM(T.INVEST_AMOUNT) DESC")
                ;

                return repositoryUtil.queryForPageMap(sqlString.toString(),
                        objList.toArray(),
                        Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
            } else {
                sqlString.append("SELECT R.JOB_NO \"jobNo\", R.RANKING \"rank\" FROM BAO_T_COMMISSION_RATE R ")
                        .append(" WHERE R.JOB_NO IN (")
                        .append(" SELECT DISTINCT ");
                String str;
                if (Integer.valueOf(ranking) == 3) {
                    str = "NVL(T.RANK_02_JOBNO, T.JOB_NO)";
                } else {
                    str = "NVL(T.RANK_0" + (Integer.valueOf(ranking) - 1) + "_JOBNO, T.RANK_0" + (Integer.valueOf(ranking) - 2) + "_JOBNO)";
                    for (int i = Integer.valueOf(ranking) - 3; i > 0; i--) {
                        if (i > 1) {
                            str = "NVL(" + str + ", T.RANK_0" + i + "_JOBNO)";
                        } else {
                            str = "NVL(" + str + ", T.JOB_NO)";
                        }
                    }
                }

                sqlString.append(str).append(" FROM BAO_T_COMMISSION_SENDBACK T WHERE T.RANK_0" + ranking + "_JOBNO = ? )");
                objList = Lists.newArrayList();
                objList.add(jobNo);
                if (!CommonUtils.isEmpty(params.get("managerName"))) {
                    sqlString.append(" AND R.CUST_NAME LIKE ? ");
                    objList.add("%" + params.get("managerName") + "%");
                }
                sqlString.append(" GROUP BY R.JOB_NO, R.RANKING ORDER BY R.JOB_NO");
                List<Map<String, Object>> subList = jdbcTemplate.queryForList(sqlString.toString(), objList.toArray());

                sqlString = new StringBuilder();
                objList = Lists.newArrayList();
                if (!CommonUtils.isEmpty(subList)) {
                    List<Map<String, Object>> temp = Lists.newArrayList();
                    for (Map<String, Object> map : subList) {
                        String jobNoCurr = CommonUtils.emptyToString(map.get("jobNo"));
                        if (!jobNo.equals(jobNoCurr)) {
                            temp.add(map);
                        }
                    }

                    sqlString = new StringBuilder();
                    objList = Lists.newArrayList();
                    for (int i = 0; i < temp.size(); i++) {
                        Map<String, Object> rankingMap = temp.get(i);
                        String subJobNo = CommonUtils.emptyToString(rankingMap.get("jobNo"));
                        String subRanking = CommonUtils.emptyToString(rankingMap.get("rank"));
                        *//*if (Integer.valueOf(subRanking) > 1) {
                            sqlString.append(" SELECT T.RANK_0" + subRanking + "_JOBNO \"jobNo\", ")
                                    .append("        T.RANK_0" + subRanking + "_NAME \"subName\", ")
                                    .append("        " + subRanking + " \"subRanking\", ")
                                    .append("        SUM(T.INVEST_AMOUNT) \"investAmount\" ")
                                    .append("   FROM BAO_T_COMMISSION_SENDBACK T WHERE T.RANK_0" + subRanking + "_JOBNO = ? ")
                                    .append("    AND T.RANK_0" + ranking + "_JOBNO = ? ")
                                    .append("  AND T.CREATE_DATE IN (SELECT MIN(CREATE_DATE) ")
                                    .append("                           FROM BAO_T_COMMISSION_SENDBACK S ")
                                    .append("                          WHERE T.RANK_0" + subRanking + "_JOBNO = S.RANK_0" + subRanking + "_JOBNO ")
                                    .append("                          GROUP BY S.INVEST_ID) ");
                        } else {
                            sqlString.append(" SELECT T.JOB_NO \"jobNo\", ")
                                    .append("        T.CUST_NAME \"subName\", ")
                                    .append("        " + subRanking + " \"subRanking\", ")
                                    .append("        SUM(T.INVEST_AMOUNT) \"investAmount\" ")
                                    .append("   FROM BAO_T_COMMISSION_SENDBACK T WHERE T.JOB_NO = ? ")
                                    .append("    AND T.RANK_0" + ranking + "_JOBNO = ? ")
                                    .append("  AND T.CREATE_DATE IN (SELECT MIN(CREATE_DATE)")
                                    .append("                           FROM BAO_T_COMMISSION_SENDBACK S ")
                                    .append("                          WHERE T.JOB_NO = S.JOB_NO ")
                                    .append("                          GROUP BY S.INVEST_ID) ");
                        }
                        objList.add(subJobNo);
                        objList.add(jobNo);*//*
                        if (Integer.valueOf(subRanking) > 1) {
                            sqlString.append("SELECT T.RANK_0" + subRanking + "_JOBNO \"jobNo\", ")
                                    .append("        T.RANK_0" + subRanking + "_NAME \"subName\", ")
                                    .append("        " + subRanking + " \"subRanking\", ")
                                    .append("        T.INVEST_AMOUNT \"investAmount\" ")
                                    .append("  FROM BAO_T_COMMISSION_SENDBACK T WHERE T.RANK_0" + subRanking + "_JOBNO = ? AND ( ");
                            objList.add(subJobNo);
                            String jobNo_col;
                            for (int j = 0; j < ranks.length; j++) {
                                if (Integer.valueOf(ranks[j]) > Integer.valueOf(subRanking)) {
                                    sqlString.append(" T.RANK_0" + ranks[j] + "_JOBNO = ? OR ");
                                    objList.add(jobNo);
                                }
                            }
                            sqlString = new StringBuilder(sqlString.substring(0, sqlString.lastIndexOf("OR ")));

                            sqlString.append("  ) AND T.CREATE_DATE IN (SELECT MIN(CREATE_DATE) ")
                                    .append("                           FROM BAO_T_COMMISSION_SENDBACK S ")
                                    .append("                          WHERE T.RANK_0" + subRanking + "_JOBNO = S.RANK_0" + subRanking + "_JOBNO ")
                                    .append("                          GROUP BY S.INVEST_ID) ");
                        } else {
                            sqlString.append("SELECT T.JOB_NO \"jobNo\", ")
                                    .append("        T.CUST_NAME \"subName\", ")
                                    .append("        " + subRanking + " \"subRanking\", ")
                                    .append("        T.INVEST_AMOUNT \"investAmount\" ")
                                    .append("  FROM BAO_T_COMMISSION_SENDBACK T WHERE T.JOB_NO = ? AND ( ");
                            objList.add(subJobNo);
                            String jobNo_col;
                            for (int j = 0; j < ranks.length; j++) {
                                if (Integer.valueOf(ranks[j]) > Integer.valueOf(subRanking)) {
                                    sqlString.append(" T.RANK_0" + ranks[j] + "_JOBNO = ? OR ");
                                    objList.add(jobNo);
                                }
                            }
                            sqlString = new StringBuilder(sqlString.substring(0, sqlString.lastIndexOf("OR ")));

                            sqlString.append("  ) AND T.CREATE_DATE IN (SELECT MIN(CREATE_DATE)")
                                    .append("                           FROM BAO_T_COMMISSION_SENDBACK S ")
                                    .append("                          WHERE T.JOB_NO = S.JOB_NO ")
                                    .append("                          GROUP BY S.INVEST_ID) ");
                        }
                        if (!CommonUtils.isEmpty(params.get("beginInvestDate"))) {
                            sqlString.append(" AND T.EFFECT_DATE >= TO_DATE(?, 'YYYY-MM-DD') ");
                            objList.add(params.get("beginInvestDate"));
                        }
                        if (!CommonUtils.isEmpty(params.get("endInvestDate"))) {
                            sqlString.append(" AND T.EFFECT_DATE < TO_DATE(?, 'YYYY-MM-DD') + 1 ");
                            objList.add(params.get("endInvestDate"));
                        }
                        if (Integer.valueOf(subRanking) > 1) {
                            sqlString.append("  GROUP BY T.RANK_0" + subRanking + "_JOBNO, T.RANK_0" + subRanking + "_NAME, T.INVEST_AMOUNT ");
                        } else {
                            sqlString.append("  GROUP BY T.JOB_NO, T.CUST_NAME, T.INVEST_AMOUNT ");
                        }

                        if (i < temp.size() - 1) {
                            sqlString.append(" UNION ALL ");
                        }
                    }
                }
                if (sqlString.length() > 0) {
                    *//*sqlString.insert(0, "SELECT \"jobNo\", \"subName\", \"subRanking\", " +
                            //" CASE WHEN \"investAmount\" >= 10000 THEN TRUNC(\"investAmount\"/10000, 2) || '万' ELSE TRUNC(\"investAmount\", 2) || '' END \"investAmount\" FROM ( ")
                            " \"investAmount\" FROM ( ")
                    ;
                    sqlString.append(" ) ORDER BY \"investAmount\" DESC ");*//*

                    sqlString.insert(0, "SELECT \"jobNo\", SUM(\"investAmount\") \"investAmount\" FROM (" +
                            "SELECT \"jobNo\", \"subName\", MAX(\"subRanking\") \"subRanking\", \"investAmount\" FROM (");

                    sqlString.append(") GROUP BY \"jobNo\", \"subName\", \"investAmount\") ")
                            .append(" GROUP BY \"jobNo\" ORDER BY \"investAmount\" DESC");
                    return repositoryUtil.queryForPageMap(sqlString.toString(),
                            objList.toArray(),
                            Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
                } else {
                    return null;
                }
            }*/
        }
    }

    @Override
    public int getCurrentRanking(String jobNo) {
        String sql = "SELECT RANKING " +
                "       FROM BAO_T_COMMISSION_RATE T " +
                "      WHERE T.JOB_NO = ? " +
                "        AND T.RATE_MONTH = (SELECT MAX(RATE_MONTH) " +
                "                              FROM BAO_T_COMMISSION_RATE R " +
                "                             WHERE T.JOB_NO = R.JOB_NO)";
        return jdbcTemplate.queryForObject(sql, new Object[]{jobNo}, Integer.class);
    }

    @Override
    public String getJobNoByManagerName(String jobNo) {
        String sql = "SELECT DISTINCT T.CUST_NAME FROM BAO_T_COMMISSION_RATE T WHERE T.JOB_NO = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{jobNo}, String.class);
    }

    @Override
    /*public Page<Map<String, Object>> queryNextTeamCustWealthDetail(Map<String, Object> params) {
        String ranking = params.get("ranking").toString();
        String jobNo = params.get("jobNo").toString();
        String subRanking = params.get("subRanking").toString();
        String subJobNo = params.get("subJobNo").toString();

        StringBuilder sqlString = new StringBuilder();
        List<Object> objList = Lists.newArrayList();

        if (CommonUtils.isEmpty(ranking) || "0".equals(ranking)) {
            return null;
        }
        if ("1".equals(ranking)) {
            // TODO 级别为1的不调用当前接口，走原来的逻辑
        } else {
            String rankingCol;
            if (ranking.equals(subRanking)) {
                rankingCol = "JOB_NO";
            } else if ("1".equals(subRanking)) {
                rankingCol = "JOB_NO";
            } else {
                rankingCol = "RANK_0" + subRanking + "_JOBNO";
            }
            sqlString = new StringBuilder();
            objList = Lists.newArrayList();
            sqlString.append("SELECT TO_CHAR(T.EFFECT_DATE, 'YYYY-MM-DD') \"investDate\", ")
                    .append("        SUM(T.INVEST_AMOUNT) \"investAmount\" ")
                    .append("   FROM BAO_T_COMMISSION_SENDBACK T ")
                    .append("  WHERE T." + rankingCol + " = ? ")
                    .append("    AND T.RANK_0" + ranking + "_JOBNO = ? ")
            ;
            objList.add(subJobNo);
            objList.add(jobNo);
            if (!CommonUtils.isEmpty(params.get("beginInvestDate"))) {
                sqlString.append(" AND T.EFFECT_DATE >= TO_DATE(?, 'YYYY-MM-DD') ");
                objList.add(params.get("beginInvestDate"));
            }
            if (!CommonUtils.isEmpty(params.get("endInvestDate"))) {
                sqlString.append(" AND T.EFFECT_DATE < TO_DATE(?, 'YYYY-MM-DD') + 1 ");
                objList.add(params.get("endInvestDate"));
            }
            sqlString.append(" GROUP BY TO_CHAR(T.EFFECT_DATE, 'YYYY-MM-DD') ")
            .append(" ORDER BY TO_CHAR(T.EFFECT_DATE, 'YYYY-MM-DD') DESC ")
            ;
        }
        return repositoryUtil.queryForPageMap(sqlString.toString(),
                objList.toArray(),
                Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
    }*/
    public Page<Map<String, Object>> queryNextTeamCustWealthDetail(Map<String, Object> params) {
        String ranking = params.get("ranking").toString();
        String jobNo = params.get("jobNo").toString();
        String subRanking = params.get("subRanking").toString();
        String subJobNo = params.get("subJobNo").toString();

        StringBuilder sqlString = new StringBuilder();
        List<Object> objList = Lists.newArrayList();

        if (CommonUtils.isEmpty(ranking) || "0".equals(ranking)) {
            return null;
        }
        if ("1".equals(ranking)) {
            // TODO 级别为1的不调用当前接口，走原来的逻辑
        } else {
            // 查询指定工号的每个月的级别
            sqlString.append("SELECT T.RANKING, T.RATE_MONTH FROM BAO_T_COMMISSION_RATE T WHERE T.JOB_NO = ? ORDER BY T.RATE_MONTH ");
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlString.toString(), new Object[]{subJobNo});
            if (CommonUtils.isEmpty(list)) {
                return null;
            } else {
                sqlString = new StringBuilder();
                sqlString.append("SELECT EFFECT_DATE \"investDate\", SUM(INVEST_AMOUNT) \"investAmount\" FROM ( ");
                for (int i = 0; i < list.size(); i++) {
                    Map<String, Object> map = list.get(i);
                    String rank = CommonUtils.emptyToString(map.get("RANKING"));
                    String rate_month = CommonUtils.emptyToString(map.get("RATE_MONTH"));
                    // 获取rate_month的下个月的字符串形式
                    String afterMonth = DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(rate_month, "yyyyMM"), 1), "yyyyMM");
                    String rankingCol;
                    if ("1".equals(rank)) {
                        rankingCol = "JOB_NO";
                    } else {
                        rankingCol = "RANK_0" + rank + "_JOBNO";
                    }
                    sqlString.append(" SELECT TO_CHAR(T.EFFECT_DATE, 'YYYY-MM-DD') EFFECT_DATE, T.INVEST_AMOUNT ")
                             .append(" FROM BAO_T_COMMISSION_SENDBACK T WHERE T." + rankingCol + " = ? ")
                             .append(" AND TO_CHAR(T.EFFECT_DATE, 'YYYYMM') = ? AND TO_CHAR(T.CREATE_DATE, 'YYYYMM') = ? ")
                    ;
                    if (i < list.size() - 1) {
                        sqlString.append(" UNION ALL ");
                    }
                    objList.add(subJobNo);
                    objList.add(rate_month);
                    objList.add(afterMonth);
                }
                sqlString.append(" ) GROUP BY EFFECT_DATE ORDER BY EFFECT_DATE DESC");
            }
        }
        return repositoryUtil.queryForPageMap(sqlString.toString(),
                objList.toArray(),
                Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
    }

	/**
	 * 查询平台数据
	 * @return
	 */
	@Override
	public Map<String, Object> queryPlatformData() {
		 StringBuilder sqlString = new StringBuilder()
		 .append("	select *  ")
		 .append("		from bao_t_platform_trade_data t")
		 .append("			order by t.create_date desc");
		 List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), 
					null);
		Map<String, Object> result = Maps.newHashMap();
		Map<String, Object> nullMap = Maps.newHashMap();
		 if(list != null && list.size() > 0) {
			 result.put("data", list.get(0));
			}
			else {
				result.put("data", nullMap.put("message", "数据为空"));
			}
		return result;
	}
/**
 * 查询用户数据
 * @param custType
 * @return
 */
	@Override
	public Map<String, Object> queryCustData(String custType) {

		 StringBuilder sqlString = new StringBuilder()
		 .append("	select *  ")
		 .append("		from bao_t_cust_data t")
		 .append("        where cust_type = ? ")
		 .append("			 order by t.create_date desc");
		 List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), 
				 new Object[] { 
				custType
			});
		Map<String, Object> result = Maps.newHashMap();
		Map<String, Object> nullMap = Maps.newHashMap();
		 if(list != null && list.size() > 0) {
			 result.put("data", list.get(0));
			}
			else {
				result.put("data", nullMap.put("message", "数据为空"));
			}
		return result;
	}

	/**
	 * 交易总额
	 * @return
	 */
@Override
public Map<String, Object> findTradeTotalAmount() {
	//交易总额
	Map<String, Object> tradeTotalAmount = Maps.newHashMap();
			StringBuilder sqlString = new StringBuilder()
			.append("       select trunc(nvl(sum(q.invest_amount), 0), 2) \"totalInvestAmount\",count(1) as \"totalTradeVolume\" ")
			.append("       from (   ")
			.append("       select t.invest_amount ")
			.append("       from BAO_T_INVEST_INFO t ")
			.append("       where 1=1")
			.append("       AND t.invest_status != '流标' and t.invest_status != '投资中' and t.invest_status != '无效' ")
			.append("       and t.record_status='有效' ")
			.append("       and (t.red_packet_type != '体验金' or t.red_packet_type is null) ")
			.append("       ) q  ");
			List<Map<String, Object>> list1 = repositoryUtil.queryForMap(sqlString.toString(), 
					null);
			if(list1 != null && list1.size() > 0) {
				tradeTotalAmount.putAll(list1.get(0));
			}
			else {
				tradeTotalAmount.put("totalInvestCount", 0);
			}
	return tradeTotalAmount;
}

@Override
/**
 * 累计注册人数
 * @return
 */
public Map<String, Object> findRegisterTotalNumber() {
	// 累计注册人数
	Map<String, Object> registerTotalNumber = Maps.newHashMap();
	StringBuilder sqlString = new StringBuilder()
			.append("   select count(1) \"totalRegister\" ")
			.append("   from bao_t_cust_info t ")
			.append("   where create_date<trunc(sysdate,'dd') and record_status='有效' and enable_status='正常' ");
			
			List<Map<String, Object>> list2 = repositoryUtil.queryForMap(sqlString.toString(), null
					);
			if(list2 != null && list2.size() > 0) {
				registerTotalNumber.putAll(list2.get(0));
			}
			else {
				registerTotalNumber.put("totalRegister", 0);
			}
	return registerTotalNumber;
}

/**
 *逾期金额
 * @return
 */
@Override
public Map<String, Object> findOverdueAmount() {
	Map<String, Object> overdueAmount = Maps.newHashMap();
	//StringBuilder sqlString = new StringBuilder()
	//逾期金额，暂为0
	overdueAmount.put("OverdueAmount", 0);
			
	return overdueAmount;
}

/**
 * 代偿还金额
 * @return
 */
@Override
public Map<String, Object> findUnRepaymentAmount() {
	Map<String, Object> unRepaymentAmount = Maps.newHashMap();
	//代偿金额
	StringBuilder sqlString = new StringBuilder()
	.append(" select sum(t.repayment_principal) \"compensatoryAmount\" ")
	.append("   from bao_t_repayment_plan_info t ")
	.append("     where t.repayment_status = '未还款' ")
	.append(" and t.loan_id is not null")
	.append(" and t.loan_id in (select bli.id")
	.append(" from bao_t_loan_info bli")
	.append(" where bli.loan_status in ('正常', '已逾期')")
	.append(" and bli.newer_flag != '体验标')");
	List<Map<String, Object>> list14 = repositoryUtil.queryForMap(sqlString.toString(), null
			);
	if(list14 != null && list14.size() > 0) {
		unRepaymentAmount.putAll(list14.get(0));
	}
	else {
		unRepaymentAmount.put("compensatoryAmount", 0);
	}
	return unRepaymentAmount;
}

/**
 * 借款成功人数
 * @return
 */
@Override
public Map<String, Object> findBorrowerNumber() {
	Map<String, Object> borrowerNumber = Maps.newHashMap();
	//借款成功的人数
	StringBuilder sqlString = new StringBuilder()
			.append("   select (atm.acc + ctm.bcc) \"lendNumber\" ")
			.append("   from (select count(distinct btc.credentials_code) bcc ")
			.append("   from bao_t_loan_cust_info btc ")
			.append("   where btc.id in ")
			.append("   (select bti.relate_primary ")
			.append("   from bao_t_loan_info bti ")
			.append("   where bti.id in ")
			.append("   (select distinct btii.loan_id ")
			.append("   from bao_t_invest_info btii ")
			.append("   where btii.invest_status != '流标' ")
			.append("    and btii.invest_status != '投资中' ")
			.append(" and btii.invest_status != '无效' ")
			.append(" and btii.record_status = '有效' ")
			.append(" and btii.loan_id is not null) and bti.newer_flag != '体验标') ")
			.append("  and btc.credentials_type != '公司') ctm, ")
			.append(" (select count(distinct a.credentials_code) acc ")
			.append("  from BAO_T_ASSET_INFO a ")
			.append("  where a.loan_id in ")
			.append(" (select distinct btii.loan_id ")
			.append(" from bao_t_invest_info btii ")
			.append(" where btii.invest_status != '流标' ")
			.append(" and btii.invest_status != '投资中' ")
			.append(" and btii.invest_status != '无效' ")
			.append(" and btii.record_status = '有效' ")
			.append(" and btii.loan_id is not null)) atm");
			List<Map<String, Object>> list5 = repositoryUtil.queryForMap(sqlString.toString(), null
					);
			if(list5 != null && list5.size() > 0) {
				borrowerNumber.putAll(list5.get(0));
			}
			else {
				borrowerNumber.put("lendNumber", 0);
			}
	return borrowerNumber;
}

/**
 * 每月成交量
 * @return
 */
@Override
public String findTradeAmountMonth() {
	//月度成交量
	StringBuilder sqlString = new StringBuilder()
			.append("    select to_char(t.create_date,'yyyy.mm') as dt,sum(t.invest_amount) as monthTradeAmount,count(*) as tradeNumber ")
			.append("    from BAO_T_INVEST_INFO t ")
			.append("    where 1=1")
			.append("    and t.invest_status != '流标' and t.invest_status != '投资中' and t.invest_status != '无效' ")
			.append("    and (t.red_packet_type != '体验金' or t.red_packet_type is null) ")
			.append("    and t.record_status='有效' ")
			.append("    and t.create_date < trunc(sysdate,'mm') group by to_char(t.create_date,'yyyy.mm') order by to_char(t.create_date,'yyyy.mm') ");
			List<Map<String, Object>> list4 = new ArrayList<Map<String,Object>>();
			list4 = repositoryUtil.queryForMap(sqlString.toString(), null);
			String resultString = "";
			if(list4 != null && list4.size() > 0) {
				resultString = monthTrade(list4);
				//tradeAmountMonth.put("monthAmountMap", list4);
			}
	return resultString;
}

/**
 * 每月投资成功人数
 * @return
 */
@Override
public String findInvestNumber() {
	//Map<String, Object> investNumber = Maps.newHashMap();
	//投资成功人数
	StringBuilder sqlString = new StringBuilder()
			.append("   select to_char(t.create_date,'yyyy.mm') as dt,count(distinct c.id) \"investSuccessNumber\" ")
			.append("   from bao_t_cust_info c")
			.append(" inner join bao_t_invest_info t on c.id = t.cust_id")
			.append(" where t.invest_status != '流标'")
			.append(" and t.invest_status != '投资中'")
			.append(" and t.invest_status != '无效'")
			.append(" and t.record_status = '有效'")
			.append(" and (t.red_packet_type != '体验金' or t.red_packet_type is null) ")
			.append(" and t.create_date < trunc(sysdate, 'mm')")
			.append("  group by to_char(t.create_date,'yyyy.mm') order by to_char(t.create_date,'yyyy.mm')");
			List<Map<String, Object>> list6 = new ArrayList<Map<String,Object>>();
			list6 = repositoryUtil.queryForMap(sqlString.toString(), null);
			String resultString = "";
			if(list6 != null && list6.size() > 0) {
				resultString = monthTrade(list6);
				//tradeAmountMonth.put("monthAmountMap", list4);
			}
			
	return resultString;
}

/**
 * 
 * <把查询出的map数据  拼接成JSON字符串>
 * <功能详细描述>
 *
 * @param list4
 * @return [参数说明]
 * @return String [返回类型说明]
 * @see [类、类#方法、类#成员]
 */
private String monthTrade(List<Map<String, Object>> list4)
{
	StringBuffer monthTrade = new StringBuffer();
	//1先把相同年份的数据进行归纳
	Map<String, List<Map<String, Object>>> resultMap = new HashMap<String, List<Map<String,Object>>>();
	/********开始年份数据归纳开始*********/
	for (Map<String, Object> map : list4) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for (String key : map.keySet()) {
			if("DT".equals(key))
			{
				//获取年份
				String year = map.get(key).toString().substring(0, 4);
				if(resultMap.entrySet().size() < 1)
				{
					list.add(map);
					resultMap.put(year, list);
				}else if(resultMap.get(year) != null)
				{
					resultMap.get(year).add(map);
				}
				else {
					list.add(map);
					resultMap.put(year, list);
				}
				break;
			}
		}
	}
	/********开始年份数据归纳结束*********/
	int i = 0;
	for (String key : resultMap.keySet()) {
		if(i == 0)
		{
			monthTrade.append("{");
		}
		int j = 0;
		int y = 0;
		for (Map<String, Object> map : resultMap.get(key)) {
			if (y == 0) {
				
				monthTrade.append("\"" + key + "\"" + ":");
			}
			if(j == 0)
			{
				monthTrade.append("[");
			}
			int z = 0;
			for (String key1 : map.keySet()) {
				if(z == 0)
				{
					monthTrade.append("{");
				}
				monthTrade.append("\"" + key1 + "\" :"  + "\"" + map.get(key1).toString() + "\""  + ",");
				z++;
			}
			//去掉逗号
			monthTrade.deleteCharAt(monthTrade.length() - 1);
			monthTrade.append("},");
			j++;
			y++;
		}
		monthTrade.deleteCharAt(monthTrade.length() - 1);
		monthTrade.append("],");
		i++;
	}
	monthTrade.deleteCharAt(monthTrade.length() - 1);
	monthTrade.append("}");
	return monthTrade.toString();
}

/**
 * 男女所占比例
 * @return
 */
@Override
public Map<String, Object> findGenderRatio() {
	Map<String, Object> genderRatio = Maps.newHashMap();
	//男女所占比例
	StringBuilder sqlString = new StringBuilder()
			.append(" SELECT CASE WHEN t.cust_gender = '男' THEN '男' ")
			.append(" WHEN t.cust_gender = '女' THEN '女' END \"sex\",TO_CHAR(100 * round(COUNT(*) / SUM(COUNT(*)) OVER(), 4), 'fm990.90') || '%' \"percent\" FROM bao_t_cust_info t  ")
			.append(" where t.cust_gender is not null and t.enable_status = '正常' GROUP BY CASE ")
			.append(" WHEN t.cust_gender = '男' THEN '男' ")
			.append(" WHEN t.cust_gender = '女' THEN '女' end ");
			List<Map<String, Object>> list7 = repositoryUtil.queryForMap(sqlString.toString(), 
					null);
			if(list7 != null && list7.size() > 0) {
				for (int i = 0; i < list7.size(); i++) {
//					result.putAll(list7.get(i));
					Map<String, Object> map = list7.get(i);
					genderRatio.put((String) map.get("sex"), map.get("percent"));
				}
			}
			else {
				genderRatio.put("男", 0);
				genderRatio.put("女", 0);
			}
	return genderRatio;
}

/**
 * 年龄分布比例
 * @return
 */
@Override
public Map<String, Object> findAgeRatio() {
	Map<String, Object> ageRatio = Maps.newHashMap();
	//年龄分布
	StringBuilder sqlString = new StringBuilder()
			.append("select CASE")
			.append(" WHEN nianling <= 25 THEN '25岁以下'")
			.append(" WHEN nianling >= 26 and nianling <= 35 THEN '26-35岁占比'")
			.append(" WHEN nianling >= 36 and nianling <= 45 THEN '36-45岁占比'")
			.append(" WHEN nianling >= 46 and nianling <= 55 THEN '46-55岁占比'")
			.append(" WHEN nianling >= 56 THEN '56岁以上'")
			.append(" END \"age\",")
			.append(" TO_CHAR(100 * round(COUNT(*) / SUM(COUNT(*)) OVER(), 4), 'fm990.99') || '%' \"percent\" ")
			.append(" from (")
			.append(" select case when length(CREDENTIALS_CODE) = 18")
			.append(" then trunc((to_char(sysdate, 'yyyymmdd') - to_char(substr(CREDENTIALS_CODE, 7, 8))) / 10000, 0) ")
			.append(" when length(CREDENTIALS_CODE) = 15")
			.append(" then trunc((to_char(sysdate, 'yyyymmdd') - to_char('19' || substr(CREDENTIALS_CODE, 7, 6))) / 10000, 0) ")
			.append(" end nianling")
			.append(" from bao_t_cust_info a")
			.append(" where a.credentials_code is not null and a.credentials_type = '身份证' and a.enable_status = '正常' and a.record_status = '有效') t")
			.append(" group by case")
			.append(" WHEN nianling <= 25 THEN '25岁以下'")
			.append(" WHEN nianling >= 26 and nianling <= 35 THEN '26-35岁占比'")
			.append(" WHEN nianling >= 36 and nianling <= 45 THEN '36-45岁占比'")
			.append(" WHEN nianling >= 46 and nianling <= 55 THEN '46-55岁占比'")
			.append(" WHEN nianling >= 56 THEN '56岁以上'")
			.append("  end");
			List<Map<String, Object>> list8 = repositoryUtil.queryForMap(sqlString.toString(), 
					null);
			if(list8 != null && list8.size() > 0) {
				for (int i = 0; i < list8.size(); i++) {
					//result.putAll(list8.get(i));
					Map<String, Object> map = list8.get(i);
					ageRatio.put((String) map.get("age"), map.get("percent"));
				}
			}
			else {
				ageRatio.put("25岁以下", 0);
				ageRatio.put("26到35岁占比", 0);
				ageRatio.put("36到45岁占比", 0);
				ageRatio.put("46到55岁占比", 0);
				ageRatio.put("56岁以上", 0);
			}
	return ageRatio;
}

/**
 * 投资人区域分布比例
 * @return
 */
@Override
public Map<String, Object> findInvesterAreaRatio() {
	Map<String, Object> investerAreaRatio = Maps.newLinkedHashMap();
	//投资人地区分布
	StringBuilder sqlString = new StringBuilder()
			.append(" select are, TO_CHAR(perc, 'fm990.990') || '%' percent from ( ")
			.append(" select a are ,100 * round(sum(c), 5) perc")
			.append(" from (select distinct substr(tc.area,")
			.append(" 0,")
			.append(" case instr(tc.area, ' ', 1, 1)")
			.append(" when 0 then")
			.append(" length(tc.area)")
			.append(" else")
			.append(" instr(tc.area, ' ', 1, 1) - 1")
			.append(" end) a,")
			.append(" tc.area_code,")
			.append(" c")
			.append(" from (select count(1) /")
			.append(" (select sum(t.c)")
			.append(" from (select count(1) c, tc.area_code")
			.append(" from (select distinct tci.mobile,substr(tci.mobile, 0, 7) number_segment")
			.append(" from BAO_T_CUST_INFO tci left join BAO_T_INVEST_INFO tii on tci.id = tii.cust_id ")
			.append(" where tci.mobile is not null and tci.enable_status = '正常' and tci.record_status = '有效'")
			.append(" and tii.invest_status != '投资中' and tii.invest_status != '流标' and tii.invest_status != '无效' ")
			.append(" and length(tci.mobile) = 11) t")
			.append(" left join BAO_T_CALLERLOC tc")
			.append(" on t.number_segment = tc.number_segment")
			.append(" where tc.area_code is not null")
			.append(" group by tc.area_code) t) c,")
			.append(" tc.area_code")
			.append(" from (select distinct tci.mobile,substr(tci.mobile, 0, 7) number_segment")
			.append(" from BAO_T_CUST_INFO tci left join BAO_T_INVEST_INFO tii on tci.id = tii.cust_id ")
			.append(" where tci.mobile is not null and tci.enable_status = '正常' and tci.record_status = '有效'")
			.append(" and tii.invest_status != '投资中' and tii.invest_status != '流标' and tii.invest_status != '无效' ")
			.append(" and (tii.red_packet_type != '体验金' or tii.red_packet_type is null) ")
			.append(" and length(tci.mobile) = 11) t")
			.append(" left join BAO_T_CALLERLOC tc")
			.append(" on t.number_segment = tc.number_segment")
			.append(" where tc.area_code is not null")
			.append(" group by tc.area_code) t")
			.append(" left join BAO_T_CALLERLOC tc")
			.append(" on t.area_code = tc.area_code)")
			.append(" group by a) order by perc desc");
			
			List<Map<String, Object>> list9 = repositoryUtil.queryForMap(sqlString.toString(), 
					null);
			if(list9 != null && list9.size() > 0) {
				for (int i = 0; i < list9.size(); i++) {
					//result.putAll(list9.get(i));
					Map<String, Object> map = list9.get(i);
					investerAreaRatio.put((String) map.get("are"), map.get("percent"));
				}
			}
			else {
				investerAreaRatio.put("are", "");
			}
	
	return investerAreaRatio;
}

/**
 * 借款金额分布比例
 * @return
 */
@Override
public Map<String, Object> findBorrowAmountRatio() {
	Map<String, Object> borrowAmountRatio = Maps.newHashMap();
	//借款金额分布
	StringBuilder sqlString = new StringBuilder()
			.append(" select case")
			.append(" when loan_amount < 10000 then '1万以下占比'")
			.append(" when loan_amount >= 10000 and loan_amount < 50000 then '1-5万占比'")
			.append(" when loan_amount >= 50000 and loan_amount < 100000 then '5-10万占比'")
			.append(" when loan_amount >= 100000 and loan_amount < 200000 then '10-20万占比'")
			.append(" when loan_amount >= 200000 and loan_amount <= 500000 then '20-50万占比'")
			.append(" when loan_amount > 50000 then '50万以上占比'")
			.append(" end \"amount\",")
			.append(" TO_CHAR(100 * round(COUNT(*) / SUM(COUNT(*)) OVER(), 4), 'fm990.90') || '%' \"percent\"")
			.append(" from bao_t_loan_info l where l.id in (")
			.append(" select distinct bti.loan_id")
			.append("  from bao_t_invest_info bti")
			.append(" where bti.invest_status != '流标'")
			.append(" and bti.invest_status != '投资中'")
			.append(" and bti.invest_status != '无效'")
			.append(" and bti.record_status = '有效'")
			.append(" and bti.loan_id is not null) and l.newer_flag != '体验标' ")
			.append(" group by case ")
			.append(" when loan_amount < 10000 then '1万以下占比'")
			.append(" when loan_amount >= 10000 and loan_amount < 50000 then '1-5万占比'")
			.append(" when loan_amount >= 50000 and loan_amount < 100000 then '5-10万占比'")
			.append(" when loan_amount >= 100000 and loan_amount < 200000 then '10-20万占比'")
			.append(" when loan_amount >= 200000 and loan_amount <= 500000 then '20-50万占比'")
			.append(" when loan_amount > 50000 then '50万以上占比' end");
			List<Map<String, Object>> list10 = repositoryUtil.queryForMap(sqlString.toString(), 
					null);
			if(list10 != null && list10.size() > 0) {
				for (int i = 0; i < list10.size(); i++) {
					//result.putAll(list10.get(i));
					Map<String, Object> map = list10.get(i);
					borrowAmountRatio.put((String) map.get("amount"), map.get("percent"));
				}
			}
			else {
				borrowAmountRatio.put("1万以下占比", 0);
				borrowAmountRatio.put("1-5万占比", 0);
				borrowAmountRatio.put("5-10万占比", 0);
				borrowAmountRatio.put("10-20万占比", 0);
				borrowAmountRatio.put("20-50万占比", 0);
				borrowAmountRatio.put("50万以上占比", 0);
			}
	return borrowAmountRatio;
}

/**
 * 不同期限标的分布比例
 * @return
 */
@Override
public Map<String, Object> findProjectNumberNotTerm() {
	Map<String, Object> projectNumberNotTerm = Maps.newHashMap();
	//不同期限标的
	StringBuilder sqlString = new StringBuilder()
			.append(" select case")
			.append("  when loan_unit = '天' then")
			.append(" (case when loan_term < 30 then '0-1个月'")
			.append(" when loan_term >= 30 and loan_term < 90 then '1-3个月'")
			.append(" when loan_term >= 90 and loan_term < 180 then '3-6个月'")
			.append(" when loan_term >= 180 and loan_term <= 360 then '6-12个月'")
			.append(" when loan_term > 360 then '12个月以上'")
			.append(" end)")
			.append("  when loan_unit = '月' or loan_unit is null then")
			.append(" (case")
			.append(" when loan_term >= 1 and loan_term < 3 then")
			.append(" '1-3个月'")
			.append("  when loan_term >= 3 and loan_term < 6 then")
			.append(" '3-6个月'")
			.append(" when loan_term >= 6 and loan_term <= 12 then")
			.append(" '6-12个月'")
			.append(" when loan_term > 12 then")
			.append(" '12个月以上'")
			.append("  end)")
			.append(" end \"term\",")
			.append(" TO_CHAR(100 * round(COUNT(*) / SUM(COUNT(*)) OVER(), 4), 'fm990.90') || '%' \"perc\"")
			.append(" from bao_t_loan_info l")
			.append(" where l.id in (select distinct bti.loan_id")
			.append(" from bao_t_invest_info bti")
			.append(" where bti.invest_status != '投资中'")
			.append(" and bti.invest_status != '流标'")
			.append(" and bti.invest_status != '无效'")
			.append(" and bti.record_status = '有效'")
			.append(" and bti.loan_id is not null) and l.newer_flag != '体验标'")
			.append(" group by case")
			.append(" when loan_unit = '天' then")
			.append(" (case when loan_term < 30 then '0-1个月'")
			.append(" when loan_term >= 30 and loan_term < 90 then '1-3个月'")
			.append(" when loan_term >= 90 and loan_term < 180 then '3-6个月'")
			.append(" when loan_term >= 180 and loan_term <= 360 then '6-12个月'")
			.append(" when loan_term > 360 then '12个月以上'")
			.append(" end)")
			.append("  when loan_unit = '月' or loan_unit is null then")
			.append(" (case")
			.append(" when loan_term >= 1 and loan_term < 3 then")
			.append(" '1-3个月'")
			.append(" when loan_term >= 3 and loan_term < 6 then")
			.append(" '3-6个月'")
			.append(" when loan_term >= 6 and loan_term <= 12 then")
			.append(" '6-12个月'")
			.append(" when loan_term > 12 then")
			.append(" '12个月以上'")
			.append(" end)")
			.append(" end");
			List<Map<String, Object>> list11 = repositoryUtil.queryForMap(sqlString.toString(), 
					null);
			if(list11 != null && list11.size() > 0) {
				for (int i = 0; i < list11.size(); i++) {
					Map<String, Object> map = list11.get(i);
					projectNumberNotTerm.put((String) map.get("term"), map.get("perc"));
				}
			}
			else {
				projectNumberNotTerm.put("0-1个月", 0);
				projectNumberNotTerm.put("1-3个月", 0);
				projectNumberNotTerm.put("3-6个月", 0);
				projectNumberNotTerm.put("6-12个月", 0);
				projectNumberNotTerm.put("12个月以上", 0);
			}
	return projectNumberNotTerm;
}

/**
 * 不同还款方式所占比例
 * @return
 */
@Override
public Map<String, Object> findRepaymentType() {
	Map<String, Object> repaymentType = Maps.newHashMap();
	//还款方式
	StringBuilder sqlString = new StringBuilder()
			.append("select case")
			.append(" when repayment_method = '等额本息' then '等额本息'")
			.append(" when repayment_method = '到期还本付息' then '到期还本付息'")
			.append(" when repayment_method = '每期还息到期付本' then '每期还息到期付本'")
			.append(" end \"repaymentmethod\",")
			.append(" TO_CHAR(100 * round(COUNT(*) / SUM(COUNT(*)) OVER(), 4),'fm990.90') || '%' \"percent\"")
			.append(" from bao_t_loan_info l")
			.append(" where l.id in(select distinct bti.loan_id from bao_t_invest_info bti where bti.invest_status !='投资中' and bti.invest_status !='流标' and bti.invest_status !='无效' and bti.loan_id is not null)")
			.append(" and l.newer_flag != '体验标' group by case")
			.append(" when repayment_method = '等额本息' then '等额本息'")
			.append(" when repayment_method = '到期还本付息' then '到期还本付息'")
			.append(" when repayment_method = '每期还息到期付本' then '每期还息到期付本'")
			.append("  end");
			List<Map<String, Object>> list12 = repositoryUtil.queryForMap(sqlString.toString(), 
					null);
			if(list12 != null && list12.size() > 0) {
				for (int i = 0; i < list12.size(); i++) {
					//result.putAll(list12.get(i));
					Map<String, Object> map = list12.get(i);
					repaymentType.put((String) map.get("repaymentmethod"), map.get("percent"));
				}
			}
			else {
				repaymentType.put("repaymentmethod", "");
			}
	return repaymentType;
}

/**
 * 借款人区域分布比例
 * @return
 */
@Override
public Map<String, Object> findBorrowerAreaRatio() {
	Map<String, Object> borrowerAreaRatio = Maps.newLinkedHashMap();
	//借款人地区分布
	StringBuilder sqlString = new StringBuilder()

			.append(" select are, TO_CHAR(perc, 'fm990.990') || '%' percent from (")
			.append(" select a are,100 * round(sum(c), 5) perc")
			.append(" from (select distinct substr(tc.area,")
			.append(" 0,")
			.append(" case instr(tc.area, ' ', 1, 1)")
			.append(" when 0 then")
			.append(" length(tc.area)")
			.append(" else")
			.append(" instr(tc.area, ' ', 1, 1) - 1")
			.append(" end) a,")
			.append(" tc.area_code,")
			.append(" c")
			.append(" from (select count(1) /")
			.append(" (select sum(t.c)")
			.append(" from (select count(1) c, tc.area_code")
			.append(" from (select distinct tii.mobile,substr(tii.mobile, 0, 7) number_segment")
			.append(" from Bao_t_Loan_Cust_Info tii ")
			.append(" where tii.mobile is not null and tii.id in")
			.append(" (select bti.relate_primary")
			.append(" from bao_t_loan_info bti")
			.append("  where bti.id in")
			.append(" (select distinct btii.loan_id")
			.append(" from bao_t_invest_info btii")
			.append(" where btii.invest_status != '投资中'")
			.append("  and btii.invest_status != '流标'")
			.append(" and btii.invest_status != '无效'")
			.append("  and btii.loan_id is not null) and bti.newer_flag != '体验标')")
			.append(" and length(tii.mobile) = 11) t")
			.append(" left join BAO_T_CALLERLOC tc")
			.append(" on t.number_segment = tc.number_segment")
			.append(" where tc.area_code is not null")
			.append(" group by tc.area_code) t) c,")
			.append(" tc.area_code")
			.append(" from (select distinct tii.mobile,substr(tii.mobile, 0, 7) number_segment")
			.append(" from Bao_t_Loan_Cust_Info tii ")
			.append(" where tii.mobile is not null ")
			.append(" and tii.id in")
			.append(" (select bti.relate_primary")
			.append(" from bao_t_loan_info bti")
			.append(" where bti.id in")
			.append(" (select distinct btii.loan_id")
			.append(" from bao_t_invest_info btii")
			.append(" where btii.invest_status != '投资中'")
			.append(" and btii.invest_status != '流标'")
			.append(" and btii.invest_status != '无效'")
			.append("  and btii.loan_id is not null))")
			.append("")
			.append(" and length(tii.mobile) = 11) t")
			.append(" left join BAO_T_CALLERLOC tc")
			.append(" on t.number_segment = tc.number_segment")
			.append(" where tc.area_code is not null")
			.append(" group by tc.area_code) t")
			.append(" left join BAO_T_CALLERLOC tc")
			.append(" on t.area_code = tc.area_code)")
			.append(" group by a) order by perc desc");
			List<Map<String, Object>> list13 = repositoryUtil.queryForMap(sqlString.toString(), 
					null);
			if(list13 != null && list13.size() > 0) {
				for (int i = 0; i < list13.size(); i++) {
					//result.putAll(list13.get(i));
					Map<String, Object> map = list13.get(i);
					borrowerAreaRatio.put((String) map.get("are"), map.get("percent"));
				}
			}
			else {
				borrowerAreaRatio.put("are", "");
			}
	return borrowerAreaRatio;
}
}
