package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.slfinance.shanlincaifu.entity.ProductRateInfoEntity;
import com.slfinance.shanlincaifu.repository.ProductRateInfoRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**
 * 善林财富投资信息类
 * 
 * @author zhumin
 *
 */
@Service
public class FixedInvestmentService {

	@Autowired
	private RepositoryUtil repositoryUtil;

	@Autowired
	private ProductRateInfoRepository productRateInfoRepository;

	/**
	 * 投资信息
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> findFixedInvestmentList(Map<String, Object> param) {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("select btii.cust_id,tbtci.login_name,tbtci.btjname,btii.invest_amount,btri.product_name,btii.curr_term,btii.invest_status,tbtci.tjname,TO_DATE(btii.invest_date,'YYYY-MM-DD HH24:MI:SS') invest_date,TO_DATE(btii.expire_date,'YYYY-MM-DD HH24:MI:SS') expire_date,btai.atone_total_amount,(case invest_status when '收益中' then '1' when '提前赎回中' then '2' when '到期处理中' then '3' when '提前赎回' then '4' when '已到期' then '5' else  '6' end) as investStatusOrder,btai.create_date  from ");
		sb.append(" BAO_T_INVEST_INFO btii left join BAO_T_ATONE_INFO btai on btai.invest_id = btii.id left join BAO_T_PRODUCT_INFO btri on btii.product_id = btri.id left join ");
		sb.append(" (select a.cust_name tjname,a.id  tjid,b.cust_name btjname,b.login_name ,b.id btjid  From BAO_T_CUST_INFO a, BAO_T_CUST_INFO b where b.invite_origin_id = a.id ");
		sb.append("  union all ");
		sb.append("  select '' as tjname,'' as tjid,cust_name as btjname,login_name ,id btjid from BAO_T_CUST_INFO tci where invite_origin_id = '0') tbtci on  tbtci.btjid = btii.cust_id where btri.product_type='3' ");

		// 用户名
		String userName = (String) param.get("userName");
		if (!StringUtils.isEmpty(userName)) {

			sb.append(" AND tbtci.btjname like ? ");
			listObject.add("%" + userName + "%");
		}
		// 产品期数
		String currTerm = (String) param.get("currTerm");
		if (!StringUtils.isEmpty(currTerm)) {
			sb.append(" AND btii.curr_term like ? ");
			listObject.add("%" + currTerm + "%");
		}
		// 期限
		String productName = (String) param.get("productName");
		if (!StringUtils.isEmpty(productName)) {
			sb.append(" AND btri.product_name=? ");
			listObject.add(productName);
		}
		// 产品状态
		String productStatus = (String) param.get("productStatus");
		if (!StringUtils.isEmpty(productStatus)) {
			sb.append(" AND btii.invest_status=? ");
			listObject.add(productStatus);
		}
		// 理财顾问
		String financialAdvisor = (String) param.get("financialAdvisor");
		if (!StringUtils.isEmpty(financialAdvisor)) {
			sb.append(" AND tbtci.tjname like ? ");
			listObject.add("%" + financialAdvisor + "%");
		}
		// 锁定日期
		String lockStartDate = (String) param.get("lockStartDate");
		if (!StringUtils.isEmpty(lockStartDate)) {
			sb.append(" AND  btii.invest_date >=? ");
			listObject.add(lockStartDate.replace("-", ""));
		}
		String lockEndDate = (String) param.get("lockEndDate");
		if (!StringUtils.isEmpty(lockEndDate)) {
			sb.append(" AND  btii.invest_date  <=? ");
			listObject.add(lockEndDate.replace("-", ""));
		}
		// 到期日期
		String planStartEndDate = (String) param.get("planStartEndDate");
		if (!StringUtils.isEmpty(planStartEndDate)) {
			sb.append(" AND btii.expire_date >=? ");
			listObject.add(planStartEndDate.replace("-", ""));
		}
		String planEndEndDate = (String) param.get("planEndEndDate");
		if (!StringUtils.isEmpty(planEndEndDate)) {
			sb.append(" AND btii.expire_date <=? ");
			listObject.add(planEndEndDate.replace("-", ""));
		}
		// 赎回时间
		String applyTimeStart = (String) param.get("applyTimeStart");
		if (!StringUtils.isEmpty(applyTimeStart)) {
			sb.append(" AND to_char(btai.create_date,'YYYY-MM-DD') >=? ");
			listObject.add(applyTimeStart);
		}
		String applyTimeEnd = (String) param.get("applyTimeEnd");
		if (!StringUtils.isEmpty(applyTimeEnd)) {
			sb.append(" AND to_char(btai.create_date,'YYYY-MM-DD') <=? ");
			listObject.add(applyTimeEnd);
		}
		sb.append(" ORDER BY investStatusOrder asc,btii.invest_date desc");
		int pageNum = Integer.valueOf(param.get("start") + "");
		int pageSize = Integer.valueOf(param.get("length") + "");

		Page<Map<String, Object>> page = repositoryUtil.queryForPageMap(
				sb.toString(), listObject.toArray(), pageNum, pageSize);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}
	/**
	 *投资总金额和赎回总金额 
	 * */   
	public Map<String, Object> findTotalFixedInvestment(Map<String, Object> param) {
		List<Object> listObject = new ArrayList<Object>();

		StringBuffer sb = new StringBuffer();
		sb.append("select btii.invest_amount,btai.atone_total_amount from ");
		sb.append(" BAO_T_INVEST_INFO btii left join BAO_T_ATONE_INFO btai on btai.invest_id = btii.id left join BAO_T_PRODUCT_INFO btri on btii.product_id = btri.id left join ");
		sb.append(" (select a.cust_name tjname,a.id  tjid,b.cust_name btjname,b.login_name ,b.id btjid  From BAO_T_CUST_INFO a, BAO_T_CUST_INFO b where b.invite_origin_id = a.id ");
		sb.append("  union all ");
		sb.append("  select '' as tjname,'' as tjid,cust_name as btjname,login_name ,id btjid from BAO_T_CUST_INFO tci where invite_origin_id = '0') tbtci on  tbtci.btjid = btii.cust_id where btri.product_type='3' ");

		// 用户名
		String userName = (String) param.get("userName");
		if (!StringUtils.isEmpty(userName)) {

			sb.append(" AND tbtci.btjname like ? ");
			listObject.add("%" + userName + "%");
		}
		// 产品期数
		String currTerm = (String) param.get("currTerm");
		if (!StringUtils.isEmpty(currTerm)) {
			sb.append(" AND btii.curr_term like ? ");
			listObject.add("%" + currTerm + "%");
		}
		// 期限
		String productName = (String) param.get("productName");
		if (!StringUtils.isEmpty(productName)) {
			sb.append(" AND btri.product_name=? ");
			listObject.add(productName);
		}
		// 产品状态
		String productStatus = (String) param.get("productStatus");
		if (!StringUtils.isEmpty(productStatus)) {
			sb.append(" AND btii.invest_status=? ");
			listObject.add(productStatus);
		}
		// 理财顾问
		String financialAdvisor = (String) param.get("financialAdvisor");
		if (!StringUtils.isEmpty(financialAdvisor)) {
			sb.append(" AND tbtci.tjname like ? ");
			listObject.add("%" + financialAdvisor + "%");
		}
		// 锁定日期
		String lockStartDate = (String) param.get("lockStartDate");
		if (!StringUtils.isEmpty(lockStartDate)) {
			sb.append(" AND  btii.invest_date >=? ");
			listObject.add(lockStartDate.replace("-", ""));
		}
		String lockEndDate = (String) param.get("lockEndDate");
		if (!StringUtils.isEmpty(lockEndDate)) {
			sb.append(" AND  btii.invest_date  <=? ");
			listObject.add(lockEndDate.replace("-", ""));
		}
		// 到期日期
		String planStartEndDate = (String) param.get("planStartEndDate");
		if (!StringUtils.isEmpty(planStartEndDate)) {
			sb.append(" AND btii.expire_date >=? ");
			listObject.add(planStartEndDate.replace("-", ""));
		}
		String planEndEndDate = (String) param.get("planEndEndDate");
		if (!StringUtils.isEmpty(planEndEndDate)) {
			sb.append(" AND btii.expire_date <=? ");
			listObject.add(planEndEndDate.replace("-", ""));
		}
		// 赎回时间
		String applyTimeStart = (String) param.get("applyTimeStart");
		if (!StringUtils.isEmpty(applyTimeStart)) {
			sb.append(" AND to_char(btai.create_date,'YYYY-MM-DD') >=? ");
			listObject.add(applyTimeStart);
		}
		String applyTimeEnd = (String) param.get("applyTimeEnd");
		if (!StringUtils.isEmpty(applyTimeEnd)) {
			sb.append(" AND to_char(btai.create_date,'YYYY-MM-DD') <=? ");
			listObject.add(applyTimeEnd);
		}		
		
		
	 
		return   repositoryUtil.queryForMap("select sum(a.invest_amount) \"totalInvestAmount\",sum(a.atone_total_amount)  \"totalAtoneAmount\"from("+sb.toString()+") a", listObject.toArray()).get(0);
	}

	/**
	 * 定期包 用于统计信息（预期收益，在投金额，在投金额，加入总额）
	 * 
	 * @param paramsMap
	 * @return
	 */
	public ResultVo queryFixedInvestmentCount(Map<String, Object> paramsMap) {
		// 返回结果
		Map<String, Object> result = new HashMap<String, Object>();

		String userId = (String) paramsMap.get("userId");
		String productType = (String) paramsMap.get("productType");
		if (StringUtils.isEmpty(productType))
			return new ResultVo(false, "产品类型不能");
		//StringBuffer sb = new StringBuffer(
		//		"select c.INVESTAMOUNT, c.TYPETERM, c.INVESTSTATUS, btpri.YEAR_RATE, btpri.AWARD_RATE  from (select sum(b.invest_amount) as INVESTAMOUNT, b.id, b.invest_status INVESTSTATUS, NVL(b.type_term, 0) as TYPETERM   from (select btii.invest_amount,   btii.cust_id,  btii.invest_status,btpi.id,btpi.type_term from BAO_T_INVEST_INFO       btii,BAO_T_PRODUCT_INFO      btpi,BAO_T_PRODUCT_TYPE_INFO btpti where btii.product_id = btpi.id  and btpi.product_type = btpti.id");

		StringBuffer accumulatedIncomeSB = new StringBuffer(
				"select NVL(sum(btai.ATONE_TOTAL_AMOUNT - btii.invest_amount + btai.ATONE_EXPENSES),0) as TRADE_AMOUNT from BAO_T_INVEST_INFO btii,BAO_T_PRODUCT_INFO btpi,BAO_T_PRODUCT_TYPE_INFO btpti,BAO_T_ATONE_INFO btai  where btii.product_id = btpi.id and btpi.product_type = btpti.id and btai.invest_id = btii.id");
		if (!StringUtils.isEmpty(productType))
			accumulatedIncomeSB.append(" and btpti.type_name = '").append(productType)
					.append("' ");
		accumulatedIncomeSB.append(" and btii.invest_status in('"
				+ Constant.TERM_INVEST_STATUS_FINISH + "','"
				+ Constant.TERM_INVEST_STATUS_ADVANCE_FINISH + "') ");
		
		
		StringBuffer addTotalSB = new StringBuffer(
				"select sum(btii.invest_amount) as INVESTAMOUNT from BAO_T_INVEST_INFO btii, BAO_T_PRODUCT_INFO btpi,BAO_T_PRODUCT_TYPE_INFO btpti where btii.product_id = btpi.id and btpi.product_type = btpti.id");
		addTotalSB.append(" and btpti.type_name = '定期宝'").append(
				"  and btpti.type_status = '正常' ");

		// 加入总额
		StringBuffer investmentSQL = new StringBuffer(
				"SELECT SUM(BTII.INVEST_AMOUNT) AS investment FROM BAO_T_INVEST_INFO BTII WHERE BTII.INVEST_STATUS IN('"
						+ Constant.TERM_INVEST_STATUS_EARN
						+ "','"
						+ Constant.TERM_INVEST_STATUS_WAIT
						+ "','"
						+ Constant.TERM_INVEST_STATUS_ADVANCE + "')");
		if (!StringUtils.isEmpty(userId)) {

			investmentSQL.append(" AND BTII.cust_id='").append(userId)
					.append("'");
			//sb.append(" and  btii.cust_id='").append(userId).append("'");

			addTotalSB.append(" and  btii.cust_id='").append(userId)
					.append("'");

			accumulatedIncomeSB.append(" and btii.CUST_ID='").append(userId)
					.append("'");
		}

//		sb.append(" and btii.invest_status in ('"
//						+ Constant.TERM_INVEST_STATUS_EARN
//						+ "','"
//						+ Constant.TERM_INVEST_STATUS_WAIT
//						+ "','"
//						+ Constant.TERM_INVEST_STATUS_ADVANCE + "')" + ") b");
//		sb.append(" group by b.id, b.type_term, b.invest_status) c, BAO_T_PRODUCT_RATE_INFO btpri  where btpri.product_id = c.id ");
		StringBuilder strSql = new StringBuilder()
		.append(" SELECT TRUNC(NVL(SUM(tradeMount ), 0), 8) \"expectedRevenue\" ")
		.append(" FROM  ")
		.append(" ( ")
		.append("   SELECT ")
		.append("       SUM(it.invest_amount * (pri.year_rate + pri.award_rate) * pi.type_term / 12) tradeMount ")
		.append("   FROM ")
		.append("       bao_t_invest_info it, ")
		.append("       bao_t_product_info pi, ")
		.append("       bao_t_product_rate_info pri, ")
		.append("       bao_t_product_type_info pti, ")
		.append("       bao_t_cust_info cust ")
		.append("   WHERE ")
		.append("       it.product_id = pi.id ")
		.append("   AND pri.product_id = pi.id ")
		.append("   AND pti.id = pi.product_type ")
		.append("   AND pti.type_name IN (?) ")
		.append("   AND it.invest_status IN (?, ?) ")
		.append("   AND cust.id=IT.CUST_ID ")
		.append("   %s ")
		.append("   UNION  ")
		.append("   SELECT ")
		.append("       SUM(it.invest_amount * (pri.year_rate) * (TRUNC(ato.create_date) - TRUNC(to_date(it.invest_date, 'yyyyMMdd'))) / 365) tradeMount ")
		.append("   FROM ")
		.append("       bao_t_invest_info it, ")
		.append("       bao_t_product_info pi, ")
		.append("       bao_t_product_rate_info pri, ")
		.append("       bao_t_product_type_info pti, ")
		.append("       bao_t_cust_info cust, ")
		.append("       bao_t_atone_info ato ")
		.append("   WHERE ")
		.append("       it.product_id = pi.id ")
		.append("   AND pri.product_id = pi.id ")
		.append("   AND pti.id = pi.product_type ")
		.append("   AND it.id = ato.invest_id ")
		.append("   AND pti.type_name IN (?) ")
		.append("   AND it.invest_status IN (?) ")
		.append("   AND cust.id=IT.CUST_ID ")
		.append("   %s ")
		.append(" ) ");

		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				String.format(strSql.toString(), 
				StringUtils.isEmpty(userId) ? "" : String.format("AND cust.id = '%s'", userId),
				StringUtils.isEmpty(userId) ? "" : String.format("AND cust.id = '%s'", userId)), new Object[] { productType, Constant.TERM_INVEST_STATUS_EARN, Constant.TERM_INVEST_STATUS_WAIT, productType, Constant.TERM_INVEST_STATUS_ADVANCE});
		BigDecimal expectedRevenue = new BigDecimal(0);

        /*for (Map<String, Object> param : list) {
			// 投资金额
			BigDecimal investAmount = (BigDecimal) param.get("INVESTAMOUNT");
			// 年化利率
			BigDecimal yearRate = (BigDecimal) param.get("YEAR_RATE");
			// 奖励利率
			BigDecimal awardRate = (BigDecimal) param.get("AWARD_RATE");
			// 期数
			BigDecimal typeTerm = (BigDecimal) param.get("TYPETERM");


			BigDecimal rate = yearRate.add(awardRate);
			BigDecimal rateAmount = investAmount.multiply(rate).multiply(
						typeTerm);
			// 投资金额 * (年化利率+奖励利率)*期数 /12
			BigDecimal expectedRevenueTemp = rateAmount.divide(new BigDecimal(
						"12"));
			expectedRevenue = expectedRevenue.add(expectedRevenueTemp);
		}
		*/
		if(list != null && list.size() > 0) {
			Map<String, Object> expectedRevenueMap = list.get(0);
			if(expectedRevenueMap != null && expectedRevenueMap.size() > 0) {
				expectedRevenue = new BigDecimal(expectedRevenueMap.get("expectedRevenue").toString());
			}
		}
		
		// 预期收益
		result.put("expectedRevenue", expectedRevenue);
		// 在线投资金额
		// 状态为：收益中、到期处理中、提前赎回中的投资
		List<Map<String, Object>> listInvestment = repositoryUtil.queryForMap(
				investmentSQL.toString(), null);
		result.put("investment", listInvestment.get(0).get("INVESTMENT"));
		//
		List<Map<String, Object>> list2 = repositoryUtil.queryForMap(
				accumulatedIncomeSB.toString(), null);
		// 累计收益
		BigDecimal accumulatedIncome = (BigDecimal) list2.get(0).get(
				"TRADE_AMOUNT");
		result.put("accumulatedIncome", accumulatedIncome == null ? 0
				: accumulatedIncome);

		BigDecimal addTotal = (BigDecimal) repositoryUtil
				.queryForMap(addTotalSB.toString(), null).get(0)
				.get("INVESTAMOUNT");
		// 加入总额
		result.put("addTotal", addTotal);

		ResultVo resultVo = new ResultVo(true, "查询成功", result);
		return resultVo;
	}

	/**
	 * 定期宝查询数据（持有中，赎回中）
	 * 
	 * @param paramsMap
	 * @return
	 */
	public ResultVo queryFixedInvestment(Map<String, Object> paramsMap) {

		Map<String, Object> mapTemp = new LinkedHashMap<String, Object>();
		for (ProductRateInfoEntity prie : productRateInfoRepository.findProductRateInfoByTypeName(Constant.PRODUCT_TYPE_04)) {
			mapTemp.put(prie.getProductId(), prie);
		}

		Map<String, Object> data = new HashMap<String, Object>();
		String userId = (String) paramsMap.get("userId");
		String productType = (String) paramsMap.get("productType");
		String status = (String) paramsMap.get("status");
		String pageNumStr = (String) paramsMap.get("pageNum");
		String pageSizeStr = (String) paramsMap.get("pageSize");

		int pageNum = Integer.valueOf(pageNumStr);
		int pageSize = Integer.valueOf(pageSizeStr);

		if (StringUtils.isEmpty(status))
			return new ResultVo(false, "定期宝状态为空");

		StringBuffer sb = null;
		if (Constant.BAO_FIXEDINVESTMENT_TYPE_CY.equals(status)) {
			sb = new StringBuffer(
					"select btii.curr_term \"termName\", btpi.product_name \"productName\"  ,NVL(btpi.type_term,0)    \"typeTerm\",btii.invest_date  \"startDate\" ,btii.expire_date  \"endDate\" ,btii.invest_amount  \"investAmount\"   , btpi.id  \"productId\", btii.id   \"invenstId\" ");
			sb.append(" from BAO_T_INVEST_INFO btii,BAO_T_PRODUCT_INFO btpi,BAO_T_PRODUCT_TYPE_INFO btpti ");
			sb.append(" where btii.product_id = btpi.id ");
			sb.append(" and btpi.product_type = btpti.id ");
			if (!StringUtils.isEmpty(productType))
				sb.append(" and btpti.type_name = '").append(productType)
						.append("' ");
			sb.append(" and btpti.type_status = '正常' ");
			sb.append(" and btii.invest_status= '"
					+ Constant.TERM_INVEST_STATUS_EARN
					+ "' and btii.id not in (select invest_id from BAO_T_ATONE_INFO where record_status='有效' and invest_id is not null) ");

		} else if (Constant.BAO_FIXEDINVESTMENT_TYPE_SH.equals(status)) {
			sb = new StringBuffer(
					"select btii.curr_term \"termName\",btpi.product_name \"productName\"  ,NVL(btpi.type_term,0)    \"typeTerm\",btii.invest_date  \"startDate\" ,btii.expire_date  \"endDate\" ,btii.invest_amount  \"investAmount\"   , btpi.id \"productId\", btii.id   \"invenstId\",btai.ATONE_TOTAL_AMOUNT  \"expectedRevenue\" ");
			sb.append(" from BAO_T_INVEST_INFO btii,BAO_T_PRODUCT_INFO btpi,BAO_T_PRODUCT_TYPE_INFO btpti ,BAO_T_ATONE_INFO btai");
			sb.append(" where btii.product_id = btpi.id  and btai.invest_id =btii.id ");
			sb.append(" and btpi.product_type = btpti.id ");
			if (!StringUtils.isEmpty(productType))
				sb.append(" and btpti.type_name = '").append(productType)
						.append("' ");
			sb.append(" and btpti.type_status = '正常' ");
			sb.append(" and btii.invest_status in('"
					+ Constant.TERM_INVEST_STATUS_WAIT
					+ "','"
					+ Constant.TERM_INVEST_STATUS_ADVANCE
					+ "') and btii.id  in (select invest_id from BAO_T_ATONE_INFO where atone_status='未处理') ");
		} else if (Constant.BAO_FIXEDINVESTMENT_TYPE_TC.equals(status)) {
			sb = new StringBuffer(
					"select btii.curr_term \"termName\",btpi.product_name \"productName\",btii.invest_date \"startDate\",btii.expire_date \"endDate\"  ,btii.invest_amount  \"investAmount\",btai.create_date \"atoneDate\", btpi.id \"productId\",btai.ATONE_TOTAL_AMOUNT \"atoneAmount\" , btii.id   \"invenstId\",NVL(btpi.type_term,0)    \"typeTerm\"");
			sb.append(" from BAO_T_INVEST_INFO btii,BAO_T_PRODUCT_INFO btpi,BAO_T_PRODUCT_TYPE_INFO btpti,BAO_T_ATONE_INFO btai");
			sb.append(" where btii.product_id = btpi.id");
			sb.append(" and btpi.product_type = btpti.id  and btai.invest_id =btii.id ");
			if (!StringUtils.isEmpty(productType))
				sb.append(" and btpti.type_name = '").append(productType)
						.append("' ");
			sb.append(" and btii.invest_status in('"
					+ Constant.TERM_INVEST_STATUS_FINISH + "','"
					+ Constant.TERM_INVEST_STATUS_ADVANCE_FINISH + "') ");

		}

		if (!StringUtils.isEmpty(userId))
			sb.append("  and btii.cust_id='").append(userId).append("' ");

		sb.append(" order by btii.create_date desc");
		Page<Map<String, Object>> page = repositoryUtil.queryForPageMap(
				sb.toString(), null, pageNum*pageSize, pageSize);

		if (Constant.BAO_FIXEDINVESTMENT_TYPE_CY.equals(status)
				|| Constant.BAO_FIXEDINVESTMENT_TYPE_SH.equals(status)) {
			List<Map<String, Object>> listMap = page.getContent();
			for (Map<String, Object> paramMap : listMap) {
				paramMap.put("dueProcess", "自动赎回");
				if (!Constant.BAO_FIXEDINVESTMENT_TYPE_SH.equals(status)) {
					String id = (String) paramMap.get("productId");
					ProductRateInfoEntity prie = (ProductRateInfoEntity) mapTemp
							.get(id);
					if (!StringUtils.isEmpty(prie)) {
						// 投资金额
						BigDecimal investAmount = (BigDecimal) paramMap
								.get("investAmount");
						// 期数
						BigDecimal typeTerm = (BigDecimal) paramMap
								.get("typeTerm");
						BigDecimal rate = prie.getYearRate().add(
								prie.getAwardRate());
						BigDecimal rateAmount = ArithUtil.mul(ArithUtil.mul(rate, typeTerm), investAmount);
						// 投资金额 * (年化利率+奖励利率)*期数 /12
						BigDecimal expectedRevenueTemp = ArithUtil.div(rateAmount, new BigDecimal("12"));
//						BigDecimal rateAmount = investAmount.multiply(rate)
//								.multiply(typeTerm);
//						// 投资金额 * (年化利率+奖励利率)*期数 /12
//						BigDecimal expectedRevenueTemp = rateAmount
//								.divide(new BigDecimal("12"));
						paramMap.put("expectedRevenue", expectedRevenueTemp);
					}

				}
			}
		}
		data.put("iTotalDisplayRecords", page.getTotalElements());
		data.put("data", page.getContent());

		return new ResultVo(true, "查询成功", data);
	}

}
