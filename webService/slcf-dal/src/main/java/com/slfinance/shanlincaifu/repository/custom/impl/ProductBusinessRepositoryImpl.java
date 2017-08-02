/** 
 * @(#)TradeFlowInfoRepository.java 1.0.0 2015年4月21日 下午2:04:46  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.ProductBusinessRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 
 *  
 * @author  caoyi
 * @version $Revision:1.0.0, $Date: 2015年4月21日 下午2:04:46 $ 
 */
@Repository
public class ProductBusinessRepositoryImpl implements ProductBusinessRepositoryCustom{

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 查询活期宝、定期宝今日明细
	 * 
	 * @author caoyi
	 * @date 2015年4月28日 上午11:26:36
	 * @param params
	 * @return Map
	 */
	@Override
	public Map<String, Object> findBaoCurrentDetailInfo(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append(" select custAmount as \"custAmount\", ")
		.append("        case ")
		.append("          when (companyIncomeAmount + companyLoanAmount - todayRepaymentAmount - ")
		.append("               untreatedRepaymentAmount - untreatedAllotAmount - usableValue + atoneAmount) < 0 then ")
		.append("           0 ")
		.append("          else ")
		.append("           companyIncomeAmount + companyLoanAmount - todayRepaymentAmount - ")
		.append("           untreatedRepaymentAmount - untreatedAllotAmount - usableValue + atoneAmount ")
		.append("        end as \"canOpenAmount\", ")
		.append("        usableValue as \"usableValue\", ")
		.append("        companyIncomeAmount as \"companyIncomeAmount\", ")
		.append("        companyLoanAmount as \"companyLoanAmount\", ")
		.append("        todayRepaymentAmount as \"todayRepaymentAmount\", ")
		.append("        untreatedRepaymentAmount as \"untreatedRepaymentAmount\", ")
		.append("        untreatedAllotAmount as \"untreatedAllotAmount\", ")
		.append("        canUseAllotAmount as \"canUseAllotAmount\", ")
		.append("        atoneAmount as \"atoneAmount\", ")
		.append("        (companyIncomeAmount + companyLoanAmount - todayRepaymentAmount - ")
		.append("               untreatedRepaymentAmount - untreatedAllotAmount - usableValue + atoneAmount) \"canRealOpenAmount\" ")
		.append("   from (select (select nvl(sum(d.account_total_value), 0) ")
		.append("                   from BAO_T_PRODUCT_TYPE_INFO a, ")
		.append("                        BAO_T_PRODUCT_INFO      b, ")
		.append("                        BAO_T_INVEST_INFO       c, ")
		.append("                        BAO_T_SUB_ACCOUNT_INFO  d ")
		.append("                  where a.id = b.product_type ")
		.append("                    and b.id = c.product_id ")
		.append("                    and c.id = d.relate_primary ")
		.append("                    and a.type_name = ? ) as custAmount, ") //--投资人当前金额(元)
		.append("                (select nvl(sum(c.curr_usable_value), 0) ")
		.append("                   from BAO_T_PRODUCT_TYPE_INFO   a, ")
		.append("                        BAO_T_PRODUCT_INFO        b, ")
		.append("                        BAO_T_PRODUCT_DETAIL_INFO c ")
		.append("                  where a.id = b.product_type ")
		.append("                    and b.id = c.product_id ")
		.append("                    and a.type_name = ? and b.enable_status = '启用' ) as usableValue, ") //--开放中价值(元)
		.append("                (select nvl(sum(a.account_total_value), 0) ")
		.append("                   from BAO_T_SUB_ACCOUNT_INFO a ")
		.append("                  where a.sub_account_no = ? ) as companyIncomeAmount, ") //--公司收益账户持有价值(元)
		.append("                (select nvl(sum(a.account_total_value), 0) ")
		.append("                   from BAO_T_SUB_ACCOUNT_INFO a ")
		.append("                  where a.sub_account_no = ? ) as companyLoanAmount, ") //--最大债权人持有价值(元) 
		.append("                (select nvl(sum(e.REPAYMENT_TOTAL_AMOUNT), 0) ")
		.append("                   from BAO_T_PRODUCT_TYPE_INFO   a, ")
		.append("                        BAO_T_ALLOT_INFO          b, ")
		.append("                        BAO_T_ALLOT_DETAIL_INFO   c, ")
		.append("                        BAO_T_LOAN_INFO           d, ")
		.append("                        BAO_T_REPAYMENT_PLAN_INFO e, ")
		.append("                        BAO_T_LOAN_DETAIL_INFO    f ")
		.append("                  where a.id = b.relate_primary ")
		.append("                    and b.id = c.allot_id ")
		.append("                    and c.loan_id = d.id ")
		.append("                    and d.id = e.loan_id ")
		.append("                    and d.id = f.loan_id ")
		.append("                    and b.allot_status in ('已分配', '已使用') ")
		.append("                    and f.credit_right_status = '正常' ")
		.append("                    and e.repayment_status = '未还款' ")
		.append("                    and a.type_name = ? ")
		.append("                    and e.expect_repayment_date = ? ) as todayRepaymentAmount, ") //--今日还款金额(元) 
		.append("                (select nvl(sum(e.repay_amount - e.already_repay_amt), 0) ")
		.append("                   from BAO_T_PRODUCT_TYPE_INFO     a, ")
		.append("                        BAO_T_ALLOT_INFO            b, ")
		.append("                        BAO_T_ALLOT_DETAIL_INFO     c, ")
		.append("                        BAO_T_LOAN_INFO             d, ")
		.append("                        BAO_T_REPAYMENT_RECORD_INFO e ")
		.append("                  where a.id = b.relate_primary ")
		.append("                    and b.id = c.allot_id ")
		.append("                    and c.loan_id = d.id ")
		.append("                    and d.id = e.loan_id ")
		.append("                    and b.allot_status in ('已分配', '已使用') ")
		.append("                    and e.handle_status = '未处理' ")
		.append("                    and a.type_name = ? ) as untreatedRepaymentAmount, ") //--未处理还款金额(元) 
		.append("                (select nvl(sum(e.value_repayment_after), 0) ")
		.append("                   from BAO_T_PRODUCT_TYPE_INFO  a, ")
		.append("                        BAO_T_ALLOT_INFO         b, ")
		.append("                        BAO_T_ALLOT_DETAIL_INFO  c, ")
		.append("                        BAO_T_LOAN_INFO          d, ")
		.append("                        BAO_T_CREDIT_RIGHT_VALUE e, ")
		.append("                        BAO_T_LOAN_DETAIL_INFO   f ")
		.append("                  where a.id = b.relate_primary ")
		.append("                    and b.id = c.allot_id ")
		.append("                    and c.loan_id = d.id ")
		.append("                    and d.id = e.loan_id ")
		.append("                    and d.id = f.loan_id ")
		.append("                    and b.allot_status in ('已分配') ")
		.append("                    and f.credit_right_status = '正常' ")
		.append("                    and a.type_name = ? ")
		.append("                    and e.value_date = ? ) as untreatedAllotAmount, ") //--分配未处理债权价值(元) 
		.append("                (select nvl(sum(e.value_repayment_after), 0) ")
		.append("                   from BAO_T_PRODUCT_TYPE_INFO  a, ")
		.append("                        BAO_T_ALLOT_INFO         b, ")
		.append("                        BAO_T_ALLOT_DETAIL_INFO  c, ")
		.append("                        BAO_T_LOAN_INFO          d, ")
		.append("                        BAO_T_CREDIT_RIGHT_VALUE e, ")
		.append("                        BAO_T_LOAN_DETAIL_INFO   f ")
		.append("                  where a.id = b.relate_primary ")
		.append("                    and b.id = c.allot_id ")
		.append("                    and c.loan_id = d.id ")
		.append("                    and d.id = e.loan_id ")
		.append("                    and d.id = f.loan_id ")
		.append("                    and b.allot_status in ('已分配') ")
		.append("                    and f.credit_right_status = '正常' ")
		.append("                    and trunc(b.use_date)=to_date(?,'yyyy-mm-dd') ")
		.append("                    and a.type_name = ? ")
		.append("                    and e.value_date = ? ) as canUseAllotAmount, ") //--分配可使用债权价值(元) 
		.append(" 				(select nvl(sum(case when a.type_name = '"+Constant.PRODUCT_TYPE_01+"' then 0 ")
		.append("            		else nvl(d.account_freeze_value, 0) ")
		.append("           	    end),0) aa ")
		.append("   				from BAO_T_PRODUCT_TYPE_INFO a, ")
		.append("        			     BAO_T_PRODUCT_INFO      b, ")
		.append("        				 BAO_T_INVEST_INFO       c, ")
		.append("        			     BAO_T_SUB_ACCOUNT_INFO  d ")
		.append("  					where a.id = b.product_type ")
		.append("    				and b.id = c.product_id ")
		.append("    				and c.id = d.relate_primary ")
		.append("    				and a.type_name = ? ) as atoneAmount ")  //--赎回中的价值(元) 
		.append("           from dual) ")
		.append("  ");
		List<Object> objList=new ArrayList<Object>();
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
			objList.add(param.get("productType"));
		}
		if(param.get("eranAccount")!=null){
			objList.add(param.get("eranAccount"));
		}
		if(param.get("centerAccount")!=null){
			objList.add(param.get("centerAccount"));
		}
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		if(param.get("dateFormat")!=null){
			objList.add(param.get("dateFormat"));
		}
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		if(param.get("dateFormat")!=null){
			objList.add(param.get("dateFormat"));
		}
		if(param.get("dateFormat")!=null){
			objList.add(param.get("dateFormat"));
		}
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		if(param.get("dateFormat")!=null){
			objList.add(param.get("dateFormat"));
		}
		objList.add(param.get("productType"));
		List<Map<String, Object>>  list=repositoryUtil.queryForMap(sql.toString(), objList.toArray());
		return list.get(0);
	}

	/**
	 * 查询活期宝当前价值分配
	 * 
	 * @author caoyi
	 * @date 2015年4月28日  下午16:43:36
	 * @param params
	 * @return Map
	 */
	@Override
	public Map<String, Object> findBaoCurrentVauleSum() {
		StringBuilder sql=new StringBuilder()
		.append(" select nvl(b.open_value,0)        as \"openValue\", ")
		.append("        nvl(a.already_pre_value,0) as \"alreadyPreValue\", ")
		.append("        nvl(a.expect_pre_value,0)  as \"expectPreValue\", ")
		.append("        nvl(a.unopen_value,0)  as \"unopenValue\", ")
		.append("        nvl(b.open_value,0) + nvl(a.already_pre_value,0) + nvl(a.unopen_value,0) as \"canOpenValue\" ")
		.append("   from BAO_T_PRODUCT_TYPE_INFO a, BAO_T_VALUE_ALLOT_RULE b ")
		.append("  where a.id = b.product_type_id ")
		.append("    and a.type_name = ? ");
		List<Map<String, Object>>  list=repositoryUtil.queryForMap(sql.toString(), new Object[]{Constant.PRODUCT_TYPE_01});
		return list.get(0);
	}
	

	/**
	 * 查询活期宝分配规则
	 * 
	 * @author caoyi
	 * @date 2015年4月28日  下午17:07:36
	 * @param params
	 * @return Map
	 */
	@Override
	public Map<String, Object> findBaoCurrentVauleSet() {
		StringBuilder sql=new StringBuilder()
		.append(" select b.allot_scale        as \"allotScale\", ")
		.append("        a.expect_pre_value as \"expectPreValue\" ")
		.append("   from BAO_T_PRODUCT_TYPE_INFO a, BAO_T_VALUE_ALLOT_RULE b ")
		.append("  where a.id = b.product_type_id ")
		.append("    and a.type_name = ? ")
		.append("  ");
		List<Map<String, Object>>  list=repositoryUtil.queryForMap(sql.toString(), new Object[]{Constant.PRODUCT_TYPE_01});
		return list.get(0);
	}

	/**
	 * 更新活期宝、定期宝计划预留价值
	 * 
	 * @author caoyi
	 * @date 2015年4月28日  下午17:29:36
	 * @param params
	 * @return Map
	 */
	@Override
	public int updateBaoPreVaule(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append(" update BAO_T_PRODUCT_TYPE_INFO a ")
		.append("    set a.EXPECT_PRE_VALUE = ?, ")
		.append("        a.last_update_date = ?, ")
		.append("        a.version          = a.version + 1 ")
		.append("  where a.TYPE_NAME = ? ");
		List<Object> objList=new ArrayList<Object>();
		if(param.get("expectPreValue")!=null){
			objList.add(param.get("expectPreValue"));
		}
		if(param.get("lastUpdateDate")!=null){
			objList.add(param.get("lastUpdateDate"));
		}
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		return jdbcTemplate.update(sql.toString(),objList.toArray());
	}
	
	
	/**
	 * 更新活期宝开放比例
	 * 
	 * @author caoyi
	 * @date 2015年4月29日  下午15:38:36
	 * @param params
	 * @return Map
	 */
	@Override
	public int updateBaoOpenScale(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append("   update BAO_T_VALUE_ALLOT_RULE a ")
		.append("      set a.Allot_Scale = ? , a.last_update_date = ?, ")
		.append("          a.version     = a.version + 1 ")
		.append("    where exists (select 1 ")
		.append("             from BAO_T_PRODUCT_TYPE_INFO b ")
		.append("            where a.product_type_id = b.id ")
		.append("              and b.type_name = ? ) ");
		List<Object> objList=new ArrayList<Object>();
		if(param.get("allotScale")!=null){
			objList.add(param.get("allotScale"));
		}
		if(param.get("lastUpdateDate")!=null){
			objList.add(param.get("lastUpdateDate"));
		}
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		return jdbcTemplate.update(sql.toString(),objList.toArray());
	}
	
	/**
	 * 更新活期宝预计开放金额
	 * 
	 * @author caoyi
	 * @date 2015年4月29日  下午17:25:36
	 * @param params
	 * @return Map
	 */
	@Override
	public int updateBaoOpenValue(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append("   update BAO_T_VALUE_ALLOT_RULE a ")
		.append("      set a.Open_Value = ? , a.last_update_date = ?, ")
		.append("          a.version     = a.version + 1 ")
		.append("    where exists (select 1 ")
		.append("             from BAO_T_PRODUCT_TYPE_INFO b, bao_t_product_info c ")
		.append("            where b.id = c.product_type and a.product_id = c.id ")
		.append("              and b.type_name = ? and c.enable_status = '启用') ");
		List<Object> objList=new ArrayList<Object>();
		if(param.get("openValue")!=null){
			objList.add(param.get("openValue"));
		}
		if(param.get("lastUpdateDate")!=null){
			objList.add(param.get("lastUpdateDate"));
		}
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		return jdbcTemplate.update(sql.toString(),objList.toArray());
	}

	/**
	 *更新活期宝实际预留价值
	 * 
	 * @author caoyi
	 * @date 2015年4月28日  下午17:40:16
	 * @param params
	 * @return Map
	 */
	@Override
	public int updateBaoAlreadyPreValue(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append(" update BAO_T_PRODUCT_TYPE_INFO a ")
		.append("    set a.Already_Pre_Value = ?, ")
		.append("        a.last_update_date = ?, ")
		.append("        a.version          = a.version + 1 ")
		.append("  where a.TYPE_NAME = ? ");
		List<Object> objList=new ArrayList<Object>();
		if(param.get("alreadyPreValue")!=null){
			objList.add(param.get("alreadyPreValue"));
		}
		if(param.get("lastUpdateDate")!=null){
			objList.add(param.get("lastUpdateDate"));
		}
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		return jdbcTemplate.update(sql.toString(),objList.toArray());
	}
	
	/**
	 * 更新活期宝未开放价值
	 * 
	 * @author caoyi
	 * @date 2015年4月29日  下午17:41:39
	 * @param params
	 * @return Map
	 */
	@Override
	public int updateBaoUnOpenVaule(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append(" update BAO_T_PRODUCT_TYPE_INFO a ")
		.append("    set a.Unopen_Value = ?, ")
		.append("        a.last_update_date = ?, ")
		.append("        a.version          = a.version + 1 ")
		.append("  where a.TYPE_NAME = ? ");
		List<Object> objList=new ArrayList<Object>();
		if(param.get("unopenValue")!=null){
			objList.add(param.get("unopenValue"));
		}
		if(param.get("lastUpdateDate")!=null){
			objList.add(param.get("lastUpdateDate"));
		}
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		return jdbcTemplate.update(sql.toString(),objList.toArray());
	}

	/**
	 * 查询债权还款预算
	 * 
	 * @author caoyi
	 * @date 2015年4月29日  下午19:35:51
	 * @param params
	 * @return Map
	 */
	@Override
	public Map<String, Object> loanRepaymentForecast(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append(" select (select nvl(sum(e.REPAYMENT_TOTAL_AMOUNT), 0) ")
		.append("           from BAO_T_PRODUCT_TYPE_INFO   a, ")
		.append("                BAO_T_ALLOT_INFO          b, ")
		.append("                BAO_T_ALLOT_DETAIL_INFO   c, ")
		.append("                BAO_T_LOAN_INFO           d, ")
		.append("                BAO_T_REPAYMENT_PLAN_INFO e, ")
		.append("                BAO_T_LOAN_DETAIL_INFO    f ")
		.append("          where a.id = b.relate_primary ")
		.append("            and b.id = c.allot_id ")
		.append("            and c.loan_id = d.id ")
		.append("            and d.id = e.loan_id ")
		.append("            and d.id = f.loan_id ")
		.append("            and b.allot_status in ('已分配', '已使用') ")
		.append("            and f.credit_right_status = '正常' ")
		.append("            and e.repayment_status = '未还款' ")
		.append("            and a.type_name = ? ")
		.append("            and e.expect_repayment_date >= ? ")
		.append("            and e.expect_repayment_date <= ?) as \"preRepayAmount\", ") //预计还款金额(元)
		.append("        (select nvl(sum(e.repay_amount - e.already_repay_amt), 0) ")
		.append("           from BAO_T_PRODUCT_TYPE_INFO     a, ")
		.append("                BAO_T_ALLOT_INFO            b, ")
		.append("                BAO_T_ALLOT_DETAIL_INFO     c, ")
		.append("                BAO_T_LOAN_INFO             d, ")
		.append("                BAO_T_REPAYMENT_RECORD_INFO e ")
		.append("          where a.id = b.relate_primary ")
		.append("            and b.id = c.allot_id ")
		.append("            and c.loan_id = d.id ")
		.append("            and d.id = e.loan_id ")
		.append("            and b.allot_status in ('已分配', '已使用') ")
		.append("            and e.handle_status = '未处理' ")
		.append("            and a.type_name = ? ) as \"untreatedRepaymentAmount\", ")  //未处理还款金额(元)
		.append("        (select nvl(a.already_pre_value, 0) ")
		.append("           from BAO_T_PRODUCT_TYPE_INFO a ")
		.append("          where a.type_name = ?) as \"alreadyPreValue\" ")  //已预留债权价值(元)
		.append("   from dual ")
		.append("  ");
		List<Object> objList=new ArrayList<Object>();
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		if(param.get("startDate")!=null){
			objList.add(param.get("startDate"));
		}
		if(param.get("endDate")!=null){
			objList.add(param.get("endDate"));
		}
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		List<Map<String, Object>>  list=repositoryUtil.queryForMap(sql.toString(), objList.toArray());
		return list.get(0);
	}

	
	/**
	 * 查询产品预计还款明细
	 * 
	 * @author caoyi
	 * @date 2015年4月30日  上午9:49:14
	 * @param params
	 * @return Map
	 */
	@Override
	public Page<Map<String, Object>> findLoanRepaymentList(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append(" 		 select to_char(to_date(e.expect_repayment_date,'yyyy-mm-dd'),'yyyy-mm-dd') as \"repaymentDate\", e.repayment_total_amount as \"repaymentAmount\" ")
		.append(" 		           from BAO_T_PRODUCT_TYPE_INFO   a, ")
		.append(" 		                BAO_T_ALLOT_INFO          b, ")
		.append(" 		                BAO_T_ALLOT_DETAIL_INFO   c, ")
		.append(" 		                BAO_T_LOAN_INFO           d, ")
		.append(" 		                BAO_T_REPAYMENT_PLAN_INFO e, ")
		.append("                       BAO_T_LOAN_DETAIL_INFO    f ")
		.append(" 		          where a.id = b.relate_primary ")
		.append(" 		            and b.id = c.allot_id ")
		.append(" 		            and c.loan_id = d.id ")
		.append(" 		            and d.id = e.loan_id ")
		.append("                   and d.id = f.loan_id ")
		.append(" 		            and b.allot_status in ('已分配', '已使用') ")
		.append("                   and f.credit_right_status = '正常' ")
		.append("                   and e.repayment_status = '未还款' ")
		.append(" 		            and a.type_name = ? ")
		.append(" 		            and e.expect_repayment_date >= ? ")
		.append(" 		            and e.expect_repayment_date <= ? ")
		.append("  ");
		List<Object> objList=new ArrayList<Object>();
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		if(param.get("startDate")!=null){
			objList.add(param.get("startDate"));
		}
		if(param.get("endDate")!=null){
			objList.add(param.get("endDate"));
		}	
		sql.append(" order by 	e.expect_repayment_date desc");
	
		return repositoryUtil.queryForPageMap(sql.toString(), objList.toArray(),(int)param.get("start"), (int)param.get("length"));
	}

	
	/**
	 * 获取债权价值预算
	 * 
	 * @author caoyi
	 * @date 2015年4月30日  上午10:59:28
	 * @param params
	 * @return Map
	 */
	@Override
	public Map<String, Object> loanValueForecast(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append(" select case when custAmount + companyIncomeAmount + companyLoanAmount=0 then 0 else ")
		.append("        companyIncomeAmount / ")
		.append("        (custAmount + companyIncomeAmount + companyLoanAmount) * ")
		.append("        value_repayment_before end as \"eranAccountValue\", ")
		.append("        case when custAmount + companyIncomeAmount + companyLoanAmount=0 then 0 else ")
		.append("        companyLoanAmount / ")
		.append("        (custAmount + companyIncomeAmount + companyLoanAmount) * ")
		.append("        value_repayment_before end as \"centerAccountValue\", ")
		.append("        case when custAmount + companyIncomeAmount + companyLoanAmount=0 then 0 else ")
		.append("        (companyIncomeAmount + companyLoanAmount) / ")
		.append("        (custAmount + companyIncomeAmount + companyLoanAmount) * ")
		.append("        value_repayment_before end as \"sum\" ")
		.append("   from (select (select nvl(sum(d.account_total_value), 0) ")
		.append("                   from BAO_T_PRODUCT_TYPE_INFO a, ")
		.append("                        BAO_T_PRODUCT_INFO      b, ")
		.append("                        BAO_T_INVEST_INFO       c, ")
		.append("                        BAO_T_SUB_ACCOUNT_INFO  d ")
		.append("                  where a.id = b.product_type ")
		.append("                    and b.id = c.product_id ")
		.append("                    and c.id = d.relate_primary ")
		.append("                    and a.type_name = ? ) as custAmount, ") //--投资人当前金额(元) 
		.append("        (select nvl(sum(a.account_total_value), 0) ")
		.append("           from BAO_T_SUB_ACCOUNT_INFO a ")
		.append("          where a.sub_account_no = ? ) as companyIncomeAmount, ") //--公司收益账户持有价值(元) 
		.append("        (select nvl(sum(a.account_total_value), 0) ")
		.append("           from BAO_T_SUB_ACCOUNT_INFO a ")
		.append("          where a.sub_account_no = ? ) as companyLoanAmount, ") //--最大债权人持有价值(元) 
		.append("        (select nvl(sum(e.value_repayment_before), 0) as value_repayment_before ")
		.append("           from BAO_T_PRODUCT_TYPE_INFO  a, ")
		.append("                BAO_T_ALLOT_INFO         b, ")
		.append("                BAO_T_ALLOT_DETAIL_INFO  c, ")
		.append("                BAO_T_LOAN_INFO          d, ")
		.append("                BAO_T_CREDIT_RIGHT_VALUE e, ")
		.append("                BAO_T_LOAN_DETAIL_INFO   f ")
		.append("          where a.id = b.relate_primary ")
		.append("            and b.id = c.allot_id ")
		.append("            and c.loan_id = d.id ")
		.append("            and d.id = e.loan_id ")
		.append("            and d.id = f.loan_id ")
		.append("            and b.allot_status in ('已分配', '已使用') ")
		.append("            and f.credit_right_status = '正常' ")
		.append("            and a.type_name = ? ")
		.append("            and e.value_date = ? ) as value_repayment_before ") //--债权价值
		.append("   from dual) a ");
		List<Object> objList=new ArrayList<Object>();
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		if(param.get("eranAccount")!=null){
			objList.add(param.get("eranAccount"));
		}
		if(param.get("centerAccount")!=null){
			objList.add(param.get("centerAccount"));
		}
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		if(param.get("queryDate")!=null){
			objList.add(param.get("queryDate"));
		}
		List<Map<String, Object>>  list=repositoryUtil.queryForMap(sql.toString(), objList.toArray());
		return list.get(0);
	}

	/**
	 * 更新产品状态
	 * 
	 * @author caoyi
	 * @date 2015年5月01日  下午15:01:29
	 * @param params
	 * @return Map
	 */
	@Override
	public int updateProductStatus(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append(" update BAO_T_PRODUCT_INFO a ")
		.append("    set a.product_status   = ? , ")
		.append("        a.last_update_date = ? , ")
		.append("        a.version          = a.version + 1 ")
		.append("  where exists (select 1 ")
		.append("           from BAO_T_VALUE_ALLOT_RULE b, BAO_T_PRODUCT_TYPE_INFO c ")
		.append("          where a.id = b.product_id ")
		.append("            and b.product_type_id = c.id ")
		.append("            and b.open_value > 0 ")
		.append("            and c.type_name = ? ) ")
		.append("  and   a.enable_status = '启用' ");
		List<Object> objList=new ArrayList<Object>();
		if(param.get("productStatus")!=null){
			objList.add(param.get("productStatus"));
		}
		if(param.get("lastUpdateDate")!=null){
			objList.add(param.get("lastUpdateDate"));
		}
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		return jdbcTemplate.update(sql.toString(),objList.toArray());
	}


	/**
     * 更新产品详情
     * 参与机构数、参与债权数、累计收益、当前产品可用价值
	 * 
	 * @author caoyi
	 * @date 2015年5月01日  下午15:14:21
	 * @param params
	 * @return Map
	 */
	@Override
	public int updateProductDetail(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append(" update BAO_T_PRODUCT_DETAIL_INFO a ")
		.append("    set a.curr_usable_value  = a.curr_usable_value + ")
		.append("                               (select c.open_value ")
		.append("                                  from BAO_T_PRODUCT_INFO      b, ")
		.append("                                       BAO_T_VALUE_ALLOT_RULE  c, ")
		.append("                                       BAO_T_PRODUCT_TYPE_INFO d ")
		.append("                                 where a.product_id = b.id ")
		.append("                                   and b.id = c.product_id ")
		.append("                                   and c.product_type_id = d.id ")
		.append("                                   and d.type_name = ? ), ")
		.append("        a.partake_organizs  = ")
		.append("        (select count(distinct d.debt_source_code) ")
		.append("           from BAO_T_PRODUCT_TYPE_INFO a, ")
		.append("                BAO_T_ALLOT_INFO        b, ")
		.append("                BAO_T_ALLOT_DETAIL_INFO c, ")
		.append("                BAO_T_LOAN_INFO         d, ")
		.append("                BAO_T_LOAN_DETAIL_INFO  e ")
		.append("          where a.id = b.relate_primary ")
		.append("            and b.id = c.allot_id ")
		.append("            and c.loan_id = d.id ")
		.append("            and d.id = e.loan_id ")
		.append("            and b.allot_status in ('已分配', '已使用') ")
		.append("            and e.credit_right_status = '正常' ")
		.append("            and a.type_name = ? ), ")
		.append("        a.partake_crerigs   = ")
		.append("        (select count(d.id) ")
		.append("           from BAO_T_PRODUCT_TYPE_INFO a, ")
		.append("                BAO_T_ALLOT_INFO        b, ")
		.append("                BAO_T_ALLOT_DETAIL_INFO c, ")
		.append("                BAO_T_LOAN_INFO         d, ")
		.append("                BAO_T_LOAN_DETAIL_INFO  e ")
		.append("          where a.id = b.relate_primary ")
		.append("            and b.id = c.allot_id ")
		.append("            and c.loan_id = d.id ")
		.append("            and d.id = e.loan_id ")
		.append("            and b.allot_status in ('已分配', '已使用') ")
		.append("            and e.credit_right_status = '正常' ")
		.append("            and a.type_name = ? ), ")
		.append("        a.accumulative_lucre = ")
		.append("        (select nvl(sum(b.trade_amount), 0) ")
		.append("           from BAO_T_ACCOUNT_FLOW_INFO b ")
		.append("          where b.trade_type = ? ), ")
		.append("        a.last_update_date = ? , ")
		.append("        a.version          = a.version + 1, ")
		.append("        a.open_date          = ? , ")
		.append("        a.curr_term         = ")
		.append("        (select case ")
		.append("                  when a.curr_term is null or trunc(a.open_date, 'yyyy') != ")
		.append("                       trunc( ?, 'yyyy') then ")
		.append("                   'T' || to_char( ?, 'yyyy') || b.product_code || '001' ")
		.append("                  else ")
		.append("                   case ")
		.append("                     when trunc(a.open_date) != trunc(?) then ")
		.append("                      'T' || to_char(?, 'yyyy') || b.product_code || ")
		.append("                      LPAD(to_number(substr(a.curr_term, ")
		.append("                                            length(a.curr_term) - 2, ")
		.append("                                            length(a.curr_term))) + 1, ")
		.append("                           '3', ")
		.append("                           '0') ")
		.append("                     else ")
		.append("                      a.curr_term ")
		.append("                   end ")
		.append("                end curr_term ")
		.append("           from BAO_T_PRODUCT_INFO b, BAO_T_PRODUCT_TYPE_INFO c ")
		.append("          where a.product_id = b.id ")
		.append("            and b.product_type = c.id ")
		.append("            and c.type_name = ? ) ")
		.append("  where exists (select 1 ")
		.append("           from BAO_T_PRODUCT_INFO      b, ")
		.append("                BAO_T_VALUE_ALLOT_RULE  c, ")
		.append("                BAO_T_PRODUCT_TYPE_INFO d ")
		.append("          where a.product_id = b.id ")
		.append("            and b.id = c.product_id ")
		.append("            and c.product_type_id = d.id ")
		.append("            and d.type_name = ? and b.enable_status = '启用') ")
		.append("  ");
		List<Object> objList=new ArrayList<Object>();
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
			objList.add(param.get("productType"));
			objList.add(param.get("productType"));
		}
		if(param.get("tradeType")!=null){
			objList.add(param.get("tradeType"));
		}
		if(param.get("lastUpdateDate")!=null){
			objList.add(param.get("lastUpdateDate"));
			objList.add(param.get("lastUpdateDate"));
			objList.add(param.get("lastUpdateDate"));
			objList.add(param.get("lastUpdateDate"));
			objList.add(param.get("lastUpdateDate"));
			objList.add(param.get("lastUpdateDate"));
		}
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
			objList.add(param.get("productType"));
		}
		return jdbcTemplate.update(sql.toString(),objList.toArray());
	}

	/**
     * 更新产品详情
     * 参与机构数、参与债权数、累计收益、当前产品可用价值
	 * 
	 * @author caoyi
	 * @date 2015年5月01日  下午15:14:21
	 * @param params
	 * @return Map
	 */
	@Override
	public int updateProductDetailDebtSource(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append(" update BAO_T_PRODUCT_DETAIL_INFO a ")
		.append("    set  ")
		.append("        a.partake_organizs  = ")
		.append("        (select count(distinct d.debt_source_code) ")
		.append("           from BAO_T_PRODUCT_TYPE_INFO a, ")
		.append("                BAO_T_ALLOT_INFO        b, ")
		.append("                BAO_T_ALLOT_DETAIL_INFO c, ")
		.append("                BAO_T_LOAN_INFO         d, ")
		.append("                BAO_T_LOAN_DETAIL_INFO  e ")
		.append("          where a.id = b.relate_primary ")
		.append("            and b.id = c.allot_id ")
		.append("            and c.loan_id = d.id ")
		.append("            and d.id = e.loan_id ")
		.append("            and b.allot_status in ('已分配', '已使用') ")
		.append("            and e.credit_right_status = '正常' ")
		.append("            and a.type_name = ? ), ")
		.append("        a.partake_crerigs   = ")
		.append("        (select count(d.id) ")
		.append("           from BAO_T_PRODUCT_TYPE_INFO a, ")
		.append("                BAO_T_ALLOT_INFO        b, ")
		.append("                BAO_T_ALLOT_DETAIL_INFO c, ")
		.append("                BAO_T_LOAN_INFO         d, ")
		.append("                BAO_T_LOAN_DETAIL_INFO  e ")
		.append("          where a.id = b.relate_primary ")
		.append("            and b.id = c.allot_id ")
		.append("            and c.loan_id = d.id ")
		.append("            and d.id = e.loan_id ")
		.append("            and b.allot_status in ('已分配', '已使用') ")
		.append("            and e.credit_right_status = '正常' ")
		.append("            and a.type_name = ? ), ")
		.append("        a.version          = a.version + 1 ")
		.append("  where exists (select 1 ")
		.append("           from BAO_T_PRODUCT_INFO      b, ")
		.append("                BAO_T_VALUE_ALLOT_RULE  c, ")
		.append("                BAO_T_PRODUCT_TYPE_INFO d ")
		.append("          where a.product_id = b.id ")
		.append("            and b.id = c.product_id ")
		.append("            and c.product_type_id = d.id ")
		.append("            and d.type_name = ? ) ");
		List<Object> objList=new ArrayList<Object>();
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
			objList.add(param.get("productType"));
			objList.add(param.get("productType"));
		}
		return jdbcTemplate.update(sql.toString(),objList.toArray());
	}
	
	/**
     * 更新活期宝分配状态
	 * 
	 * @author caoyi
	 * @date 2015年5月05日  下午14:43:21
	 * @param params
	 * @return Map
	 */
	@Override
	public int updateBaoAllotStatus(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append("             update BAO_T_ALLOT_INFO a ")
		.append("                set a.allot_status     = ? , ")
		.append("                    a.last_update_date = ? , ")
		.append("                    a.version          = a.version + 1 ")
		.append("              where exists (select 1 ")
		.append("                       from BAO_T_PRODUCT_TYPE_INFO b ")
		.append("                      where a.relate_primary = b.id ")
		.append("                        and b.type_name = ? ")
		.append("                        and a.allot_status = ? ")
		.append("                        and trunc(a.use_date)=to_date(?,'yyyy-mm-dd') ) ");
		List<Object> objList=new ArrayList<Object>();
		if(param.get("allotStatus")!=null){
			objList.add(param.get("allotStatus"));
		}
		if(param.get("lastUpdateDate")!=null){
			objList.add(param.get("lastUpdateDate"));
		}
		if(param.get("productType")!=null){
			objList.add(param.get("productType"));
		}
		if(param.get("oldAllotStatus")!=null){
			objList.add(param.get("oldAllotStatus"));
		}
		if(param.get("dateFormat")!=null){
			objList.add(param.get("dateFormat"));
		}
		return jdbcTemplate.update(sql.toString(),objList.toArray());
	}

	
	/**
     * 获取累计成交统计
	 * 
	 * @author caoyi
	 * @date 2015年5月05日  下午16:53:21
	 * @param params
	 * @return Map
	 */
	@Override
	public Map<String, Object> findTotalTradetInfo(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append("                    select * ")
		.append("                      from (select count(1) as \"custCount\" from bao_t_cust_info), ")
		.append("                           (select sum(a.invest_amount) as \"tradeAmount\", ")
		.append("                                   count(1) as \"tradeCount\" ")
		.append("                              from BAO_T_INVEST_DETAIL_INFO a), ")
		.append("                           (select nvl(sum(a.trade_amount), 0) as \"incomeAmount\" ")
		.append("                              from BAO_T_ACCOUNT_FLOW_INFO a ")
		.append("                             where a.trade_type in (?,?,?)) ")
		.append("  ");
		List<Object> objList=new ArrayList<Object>();
		if(param.get("baoTradeType01")!=null){
			objList.add(param.get("baoTradeType01"));
		}
		if(param.get("baoTradeType03")!=null){
			objList.add(param.get("baoTradeType03"));
		}
		if(param.get("baoTradeType04")!=null){
			objList.add(param.get("baoTradeType04"));
		}
		List<Map<String, Object>>  list=repositoryUtil.queryForMap(sql.toString(), objList.toArray());
		return list.get(0);
	}

	
	/**
     * 查询定期宝当前价值分配
     * 
     * @author  caoyi
	 * @date    2015年8月18日 下午19:28:13
	 * @param param
	 * @return
     */
	@Override
	public List<Map<String, Object>> findTermCurrentVauleSum() {
		StringBuilder sql=new StringBuilder()
		.append(" select b.product_name as \"name\",'product' as \"type\", c.open_value as \"value\", b.product_code as \"code\" ")
		.append("   from BAO_T_PRODUCT_TYPE_INFO a, ")
		.append("        BAO_T_PRODUCT_INFO      b, ")
		.append("        BAO_T_VALUE_ALLOT_RULE  c ")
		.append("  where a.id = b.product_type ")
		.append("    and b.id = c.product_id ")
		.append("    and a.type_name = ? and b.enable_status = '启用' ")
		.append(" union ")
		.append(" select '已预留价值' as \"name\",'alreadyPreValue' as \"type\", a.already_pre_value as \"value\" ,'alreadyPreValue' as \"code\" ")
		.append("   from BAO_T_PRODUCT_TYPE_INFO a ")
		.append("  where a.type_name = ? ")
		.append(" union ")
		.append(" select '预计预留价值' as \"name\",'expectPreValue' as \"type\", a.expect_pre_value as \"value\" ,'expectPreValue' as \"code\" ")
		.append("   from BAO_T_PRODUCT_TYPE_INFO a ")
		.append("  where a.type_name = ? ")
		.append(" union ")
		.append(" select '未开放价值' as \"name\",'unopenValue' as \"type\", a.unopen_value as \"value\" ,'unopenValue' as \"code\" ")
		.append("   from BAO_T_PRODUCT_TYPE_INFO a ")
		.append("  where a.type_name = ? ")
		.append("  order by \"code\" ");
		List<Object> objList=new ArrayList<Object>();
		objList.add(Constant.PRODUCT_TYPE_04);
		objList.add(Constant.PRODUCT_TYPE_04);
		objList.add(Constant.PRODUCT_TYPE_04);
		objList.add(Constant.PRODUCT_TYPE_04);
		List<Map<String, Object>>  list=repositoryUtil.queryForMap(sql.toString(), objList.toArray());
		return list;
	}
	
	/**
	 * 查询活期宝当前价值分配--map版本
	 * 
	 * @author caoyi
	 * @date 2015年4月28日  下午16:43:36
	 * @param params
	 * @return Map
	 */
	@Override
	public Map<String, Object> findTermCurrentVauleSum2() {
		StringBuilder sql=new StringBuilder()
		.append(" select openValue as \"openValue\", ")
		.append("        alreadyPreValue as \"alreadyPreValue\", ")
		.append("        expectPreValue as \"expectPreValue\", ")
		.append("        unopenValue as \"unopenValue\", ")
		.append("        openValue + alreadyPreValue + unopenValue as \"canOpenValue\" ")
		.append("   from (select nvl(sum(b.open_value), 0) as openValue ")
		.append("           from BAO_T_PRODUCT_TYPE_INFO a, BAO_T_VALUE_ALLOT_RULE b, bao_t_product_info c ")
		.append("          where a.id = c.product_type and c.id = b.product_id ")
		.append("            and a.type_name = ? and c.enable_status = '启用'), ")
		.append("        (select nvl(a.already_pre_value, 0) as alreadyPreValue, ")
		.append("                nvl(a.expect_pre_value, 0) as expectPreValue, ")
		.append("                nvl(a.unopen_value, 0) as unopenValue ")
		.append("           from BAO_T_PRODUCT_TYPE_INFO a ")
		.append("          where a.type_name = ? ) ");
		List<Map<String, Object>>  list=repositoryUtil.queryForMap(sql.toString(), new Object[]{Constant.PRODUCT_TYPE_04,Constant.PRODUCT_TYPE_04});
		return list.get(0);
	}

	/**
     * 查询定期宝分配规则
     * 
     * @author  caoyi
	 * @date    2015年8月17日 下午20:40:43
	 * @param param
	 * @return
     */
	@Override
	public List<Map<String, Object>> findTermCurrentVauleSet() {
		StringBuilder sql=new StringBuilder()
		.append(" select b.product_name as \"name\", ")
		.append("        'product' as \"type\", ")
		.append("        c.allot_scale  as \"value\", ")
		.append("        b.product_code as \"code\" ")
		.append("   from BAO_T_PRODUCT_TYPE_INFO a, ")
		.append("        BAO_T_PRODUCT_INFO      b, ")
		.append("        BAO_T_VALUE_ALLOT_RULE  c ")
		.append("  where a.id = b.product_type ")
		.append("    and b.id = c.product_id ")
		.append("    and a.type_name = ? and b.enable_status = '启用' ")
		.append(" union ")
		.append(" select '预计预留价值' as \"name\", ")
		.append("        'expectPreValue' as \"type\", ")
		.append("        a.expect_pre_value as \"value\", ")
		.append("        'expectPreValue' as \"code\" ")
		.append("   from BAO_T_PRODUCT_TYPE_INFO a ")
		.append("  where a.type_name = ? ")
		.append("  order by \"code\" ");
		
		List<Object> objList=new ArrayList<Object>();
		objList.add(Constant.PRODUCT_TYPE_04);
		objList.add(Constant.PRODUCT_TYPE_04);
		List<Map<String, Object>>  list=repositoryUtil.queryForMap(sql.toString(), objList.toArray());
		return list;
	}

	
	/**
	 * 更新定期宝开放比例
	 * 
	 * @author caoyi
	 * @date 2015年8月18日  下午14:31:16
	 * @param params
	 * @return Map
	 */
	@Override
	public int updateTermOpenScale(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append("   update BAO_T_VALUE_ALLOT_RULE a ")
		.append("      set a.Allot_Scale = ? , a.last_update_date = ?, ")
		.append("          a.version     = a.version + 1 ")
		.append("    where exists (select 1 ")
		.append("             from BAO_T_PRODUCT_INFO b ")
		.append("            where a.product_id = b.id ")
		.append("              and b.product_name = ? and b.enable_status = '启用') ");
		List<Object> objList=new ArrayList<Object>();
		if(param.get("allotScale")!=null){
			objList.add(param.get("allotScale"));
		}
		if(param.get("lastUpdateDate")!=null){
			objList.add(param.get("lastUpdateDate"));
		}
		if(param.get("productName")!=null){
			objList.add(param.get("productName"));
		}
		return jdbcTemplate.update(sql.toString(),objList.toArray());
	}

	
	/**
     * 更新定期宝预计开放金额
     * 
     * @author  caoyi
	 * @date    2015年8月18日 下午20:03:21
	 * @param param
	 * @return
     */
	@Override
	public int updateTermOpenValue(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append("   update BAO_T_VALUE_ALLOT_RULE a ")
		.append("      set a.Open_Value = ? , a.last_update_date = ?, ")
		.append("          a.version     = a.version + 1 ")
		.append("    where exists (select 1 ")
		.append("             from BAO_T_PRODUCT_INFO b ")
		.append("            where a.product_id = b.id ")
		.append("              and b.product_name = ? and b.enable_status = '启用') ");
		List<Object> objList=new ArrayList<Object>();
		if(param.get("openValue")!=null){
			objList.add(param.get("openValue"));
		}
		if(param.get("lastUpdateDate")!=null){
			objList.add(param.get("lastUpdateDate"));
		}
		if(param.get("productName")!=null){
			objList.add(param.get("productName"));
		}
		return jdbcTemplate.update(sql.toString(),objList.toArray());
	}
	
}
