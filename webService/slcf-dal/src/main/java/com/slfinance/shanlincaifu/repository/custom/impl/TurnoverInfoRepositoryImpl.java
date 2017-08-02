package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.TurnoverInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SqlCondition;
@Repository
public class TurnoverInfoRepositoryImpl implements TurnoverInfoRepositoryCustom {

	@Autowired
	public  JdbcTemplate jdbcTemplate;
	@Autowired
	public RepositoryUtil repositoryUtil;
	/**
	 * 查询营业额金额汇总
	 * @return totalAmount:String:购买总金额汇总
	 */
	public BigDecimal queryTurnoverTotalAmount(Map<String, Object> param) {
		SqlCondition sqlCondition = getTurnoverInfoListSql(param);
		StringBuilder sql = new StringBuilder()
		.append(" SELECT sum(\"investAmount\") \"totalInvestAmount\" FROM ( ")
		.append(sqlCondition.toString())
		.append(" ) ");
		BigDecimal totalAmount = jdbcTemplate.queryForObject(sql.toString(), sqlCondition.toArray(), BigDecimal.class);
		return totalAmount;
	}

	/***
	 * 营业额列表查询
	 * @author  guoyk
	 * @date    2017-4-17 
	 */
	public Page<Map<String, Object>> queryTurnoverInfoList(Map<String, Object> params) {
		SqlCondition sqlCondition = getTurnoverInfoListSql(params);
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}
	
	private SqlCondition getTurnoverInfoListSql(Map<String, Object> param){
		StringBuilder sql = new StringBuilder()
		.append("  select           cust.cust_name \"investName\",  ")
		.append("           loan.loan_code \"loanCode\",  ")
		.append("           case when invest.invest_mode ='转让' then '转让标'  ")
		.append("           when loan.newer_flag = '普通标' then '优选标'  ")
		.append("           when loan.newer_flag = '新手标' then '新手标'  ")
		.append("           end \"flagType\",  ")
		.append("           loan.loan_term \"loanTerm\",  ")
		.append("           loan.loan_unit \"loanUnit\",  ")
		.append("           loan.repayment_method \"repaymentMethod\",  ")
		.append("           trunc(d.year_irr*100,2) \"yearRate\",  ")
		.append("           decode(loan.seat_term,-1,'不可流转标','可流转标') \"seatTerm\",  ")
		.append("           invest.invest_amount  \"investAmount\",  ")
		.append("           substr(invest.effect_date,1,4)||'-'||substr(invest.effect_date,5,2)||'-'||substr(invest.effect_date,7)  \"investDate\",  ")
		.append("           custManager.Cust_Name \"empName\"  ")
		.append("           from     ")
		.append("              bao_t_commission_detail_info comi,               ")
		.append("              bao_t_commission_info com,  ")
		.append("               bao_t_invest_info invest,  ")
		.append("                bao_t_loan_info loan,  ")
		.append("                 bao_t_loan_detail_info d,   ")
		.append("                  bao_t_cust_info cust,  ")
		.append("                  bao_t_cust_info custManager  ")
		.append("         where  comi.commission_id = com.id  ")
		.append("           and   comi.invest_id = invest.id  ")
		.append("           and   invest.loan_id = loan.id  ")
		.append("           and   cust.id = invest.cust_id  ")
		.append("           and   d.loan_id = loan.id  ")
		.append("           and   custManager.id = com.cust_id  ")
		.append("      and   com.dept_name = ? and com.city_name = ? and com.province_name=? ");
		ArrayList<Object> listObject=new ArrayList<Object>();
		listObject.add(param.get("debtName"));
		listObject.add(param.get("cityName"));
		listObject.add(param.get("provinceName"));
		if(!StringUtils.isEmpty(param.get("flagType"))){
			if("新手标".equals(param.get("flagType"))){
				sql.append("and loan.newer_flag= '新手标'and invest.invest_mode != '转让'");
			}
			if ("转让标".equals(param.get("flagType"))) {
				sql.append("and invest.invest_mode= '转让'");
			}
			if ("优选标".equals(param.get("flagType"))) {
				sql.append("and loan.newer_flag!= '新手标' and  invest.invest_mode != '转让'");
			}
		}
		if(!StringUtils.isEmpty(param.get("isTransfer"))){
			sql.append("and loan.seat_term = ?");
			listObject.add(param.get("isTransfer").toString());
		}
		if(!StringUtils.isEmpty(param.get("repaymentMethod"))){
			sql.append("and loan.repayment_method = ?");
			listObject.add(param.get("repaymentMethod").toString());
		}
		
		if(!StringUtils.isEmpty(param.get("minLoanTerm"))){
			sql.append(" and loan.LOAN_TERM*decode(loan.LOAN_UNIT, '天',1,'月',30,1) > ? ");
			int minTerm = Integer.parseInt(param.get("minLoanTerm").toString())*30;
			listObject.add(minTerm);
		}
		if(!StringUtils.isEmpty(param.get("maxLoanTerm"))){
			sql.append(" and loan.LOAN_TERM*decode(loan.LOAN_UNIT, '天',1,'月',30,1) <= ? ");
			int maxTerm = Integer.parseInt(param.get("maxLoanTerm").toString())*30;
			listObject.add(maxTerm);
		}

		SqlCondition sqlCondition = new SqlCondition(sql,param,listObject);
		return sqlCondition;
	}

	/***
	 * 营业额数据保存
	 * @author  guoyk
	 * @date    2017-4-17 
	 */
	public Integer updateTurnover(Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
		.append("  update bao_t_business_dept_info t  ")
		.append("      set (t.cust_id,t.credentials_code ,")
		.append("         t.last_update_user,t.last_update_date ) =  ")
		.append("           (  ")
		.append("          select c.id ,c.credentials_code, ?,? ")
		.append("          from vw_bd_psndoc s,  ")
		.append("               bao_t_cust_info c  ")
		.append("          where s.证件号码 = c.credentials_code  ")
		.append("          and s.工号 = t.emp_no  ")
		.append("      )  ")
		.append("      where t.bussiness_import_id = ? and exists (  ")
		.append("      select 1  ")
		.append("          from vw_bd_psndoc s,  ")
		.append("               bao_t_cust_info c  ")
		.append("          where s.证件号码 = c.credentials_code  ")
		.append("          and s.工号 = t.emp_no  ")
		.append("      )  ");
		Date nowDate= new Date();
		int update = jdbcTemplate.update(sql.toString(), 
				new Object[]{	param.get("userId"),
								nowDate,
								param.get("bussinessImportId")
								} );
		return Integer.valueOf(update);
		
	}

	/***
	 * 营业额数据删除重复数据
	 * @author  guoyk
	 * @date    2017-4-25 
	 */
	public void deleteRepeatTurnover() {
		StringBuilder sql = new StringBuilder()
		.append(" delete from bao_t_business_dept_info t  ")
		.append(" where t.id !=  ")
		.append(" ( ")
		.append(" select max(s.id) from bao_t_business_dept_info s ")
		.append(" where s.bussiness_import_id = t.bussiness_import_id ")
		.append(" and s.emp_no = t.emp_no ")
		.append(" and s.emp_name = t.emp_name ")
		.append(" and s.dept_name = t.dept_name ")
		.append(" ) ");
		jdbcTemplate.execute(sql.toString());
	}
	
	/***
	 * 营业额导入记录查询
	 * @author  guoyk
	 * @date    2017-4-18 
	 */
	public List<Map<String, Object>> queryImportRecord() {
		StringBuilder sql = new StringBuilder()
		.append("     select   ")
		.append(" 		 bii.import_date \"importDate\",  ")
		.append(" 		 bii.import_status \"importStatus\",  ")
		.append(" 		 cu.user_name \"createUser\"  ")
		.append(" 		 from  ")
		.append("      bao_t_business_import_info bii  ")
		.append("      inner join com_t_user cu on bii.create_user = cu.id ")
		.append(" 		 order by bii.import_date desc  ");
		List<Object> args = new ArrayList<Object>();
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}

	@Override
	public Page<Map<String, Object>> queryTurnoverList(
			Map<String, Object> params) {
		SqlCondition sqlCondition = getTurnoverListSql(params);
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}
	
	public SqlCondition getTurnoverListSql(Map<String, Object> params){
		StringBuilder sql = new StringBuilder()
		.append(" select t.*,t.\"aturingDebtIsTransfer\"+t.\"aturingDebtIsNotTransfer\"+t.\"averageCapitalIsTransfer\"+t.\"averageCapitalIsNotTransfer\" +t.\"newFlag\"+t.\"turnover\" \"totalAmount\"")
		.append(" from (")
		.append(" select t.province_name \"provinceName\", t.city_name \"cityName\", t.dept_name \"deptName\",  ")
		.append(" sum(case when l.repayment_method = '到期还本付息' and l.seat_term != -1 and l.newer_flag!='新手标' and i.invest_mode!='转让' then i.invest_amount else 0 end) \"aturingDebtIsTransfer\", ")
		.append(" sum(case when l.repayment_method = '到期还本付息' and l.seat_term = -1 and l.newer_flag!='新手标' and i.invest_mode!='转让' then i.invest_amount else 0 end) \"aturingDebtIsNotTransfer\", ")
		.append(" sum(case when l.repayment_method = '等额本息' and l.seat_term != -1 and l.newer_flag!='新手标' and i.invest_mode!='转让' then i.invest_amount else 0 end) \"averageCapitalIsTransfer\", ")
		.append(" sum(case when l.repayment_method = '等额本息' and l.seat_term = -1 and l.newer_flag!='新手标' and i.invest_mode!='转让' then i.invest_amount else 0 end) \"averageCapitalIsNotTransfer\", ")
		.append(" sum(case when l.newer_flag ='新手标' then i.invest_amount else 0 end) \"newFlag\",")
		.append(" sum(case when i.invest_mode='转让' then i.invest_amount else 0 end) \"turnover\" ")
		.append(" from bao_t_commission_info t ")
		.append(" inner join bao_t_commission_detail_info s on s.commission_id = t.id ")
		.append(" inner join bao_t_invest_info i on i.id = s.invest_id ")
		.append(" inner join bao_t_loan_info l on l.id = i.loan_id ")
		.append(" where l.loan_status is not null  and t.dept_name is not null ");
		
		ArrayList<Object> listObject=new ArrayList<Object>();
		if(!StringUtils.isEmpty(params.get("deptName"))){
			sql.append("and t.dept_name = ?");
			listObject.add(params.get("deptName").toString());
		}
		if(!StringUtils.isEmpty(params.get("cityName"))){
			sql.append("and t.city_name = ?");
			listObject.add(params.get("cityName").toString());
		}
		if(!StringUtils.isEmpty(params.get("provinceName"))){
			sql.append("and t.province_name = ?");
			listObject.add(params.get("provinceName").toString());
		}
		if(!StringUtils.isEmpty(params.get("minTime"))){
			sql.append("and i.create_date >=  ? ");
			listObject.add(DateUtils.parseStandardDate(params.get("minTime").toString()));
		}
		if(!StringUtils.isEmpty(params.get("maxTime"))){
			sql.append("and i.create_date <  ? ");
			listObject.add(DateUtils.getAfterDay(DateUtils.parseStandardDate(params.get("maxTime").toString()), 1));
		}
		if(!StringUtils.isEmpty(params.get("repaymentMethod"))){
			sql.append("and l.repayment_method = ?");
			listObject.add(params.get("repaymentMethod").toString());
		}
		if(!StringUtils.isEmpty(params.get("isTransfer"))){
			if (Integer.valueOf((String)params.get("isTransfer"))==-1) {
				sql.append("and l.seat_term = -1");
			}else {
				sql.append("and l.seat_term != -1");
			}
		}
		if(!StringUtils.isEmpty(params.get("flagType"))){
			if ("新手标".equals((String)params.get("flagType"))) {
				sql.append("and l.newer_flag = '新手标' and i.invest_mode != '转让' ");
			}
			if ("优选标".equals((String)params.get("flagType"))) {
				sql.append("and l.newer_flag != '新手标' and i.invest_mode != '转让'");
			}
			if ("转让标".equals((String)params.get("flagType"))) {
				sql.append("and i.invest_mode = '转让'");
			}
		}
		
		SqlCondition sqlCondition = new SqlCondition(sql,params,listObject);
		sqlCondition.addSql("group by t.province_name, t.city_name, t.dept_name )t");
		return sqlCondition;  
	}

	@Override
	public BigDecimal queryTurnoverTotalAmountList(Map<String, Object> param) {
		SqlCondition sqlCondition = getTurnoverListSql(param);
		StringBuilder sql = new StringBuilder()
		.append(" SELECT sum(\"totalAmount\") \"totalInvestAmount\" FROM ( ")
		.append(sqlCondition.toString())
		.append(" ) ");
		BigDecimal totalAmount = jdbcTemplate.queryForObject(sql.toString(), sqlCondition.toArray(), BigDecimal.class);
		return totalAmount;
	}
}
