package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.ParamEntity;
import com.slfinance.shanlincaifu.repository.ParamRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.WdzjRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.vo.WdzjInvestinfoVo;
import com.slfinance.shanlincaifu.vo.WdzjLoaninfoVO;

@Repository
public class WdzjRepositoryImpl implements WdzjRepositoryCustom {

	@Autowired
	RepositoryUtil repositoryUtil;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired 
	private ParamRepository paramRepository;
	public static String SLCF_LOAN_BASEURL =null; 
	public static String SLCF_LOAN_TRANSFER_BASEURL =null; 
	/**
	 * 网贷之家返回数据
	 * 
	 * @author fengyl
	 * @date 2017-4-17
	 * @param params
	 * @return
	 *         <tt>totalPage  :String:总页数(根据pageSize计算)</tt><br>
	 *         <tt>currentPage:String:当前页数</tt><br>
	 *         <tt>totalCount:     String:总标数</tt><br>
	 *         <tt>totalAmount    :String:当天借款标总额</tt><br>
	 *         <tt>borrowList: List<WdzjLoaninfoVO>:借款标信息</tt><br>
	 * @throws SLException
	 */
	@Override
	public Map<String, Object> queryLoaninfoAndInvestlistMap(Map<String, Object> params) {
		LinkedHashMap<String, Object> result = Maps.newLinkedHashMap();
		if (SLCF_LOAN_BASEURL == null || SLCF_LOAN_TRANSFER_BASEURL == null) {
			ParamEntity paramEntity1 = paramRepository.findByTypeNameAndParameterName(Constant.WDZJ,Constant.WDZJ_LOAN);
			ParamEntity paramEntity2 = paramRepository.findByTypeNameAndParameterName(Constant.WDZJ,Constant.WDZJ_LOAN_TRANSFER);
			SLCF_LOAN_BASEURL = paramEntity1.getValue();
			SLCF_LOAN_TRANSFER_BASEURL = paramEntity2.getValue();
		}
		// 查询标的和转让信息
		Page<Map<String, Object>> pageMap = queryLoaninfoAndInvestlist(params);
		List<WdzjLoaninfoVO> wdzjList = buildVO(pageMap.getContent());

		// 查询标的的投资信息
		List<WdzjInvestinfoVo> wdzj1 = queryDisperseInvestDeatailList(params);
		// 查询转让的投资信息
		List<WdzjInvestinfoVo> wdzj2 = queryCreditInvestDeatailList(params);
		for (WdzjLoaninfoVO wdzjlistMap : wdzjList) {
			List<WdzjInvestinfoVo> wdzj = new ArrayList<WdzjInvestinfoVo>();
			if ("信用标".equals(wdzjlistMap.getType())) {
				for(WdzjInvestinfoVo wdzjInvest : wdzj1) {
					String loanId = (String) wdzjlistMap.getProjectId();
					if(wdzjInvest.getProjectId().equals(loanId)) {
						wdzj.add(wdzjInvest);
					}
				}
			}
			else if ("债权转让标".equals(wdzjlistMap.getType())) {
				for(WdzjInvestinfoVo wdzjInvest : wdzj2) {
					String loanId = (String) wdzjlistMap.getProjectId();
					loanId=loanId.substring(0, loanId.indexOf("_"));
					if(wdzjInvest.getProjectId().equals(loanId)) {
						wdzj.add(wdzjInvest);
					}
				}
			}
			wdzjlistMap.setSubscribes(wdzj);
		}

		/*for (WdzjLoaninfoVO wdzjlistMap : wdzjList) {
			List<WdzjInvestinfoVo> wdzj = new ArrayList<WdzjInvestinfoVo>();
			String loanId = (String) wdzjlistMap.getProjectId();
			if ("信用标".equals(wdzjlistMap.getType())) {
				wdzj = queryDisperseInvestDeatailList(loanId);
			} else if ("债权转让标".equals(wdzjlistMap.getType())) {
				loanId=loanId.substring(0, loanId.indexOf("_"));
				wdzj = queryCreditInvestDeatailList(loanId);
			}
			wdzjlistMap.setSubscribes(wdzj);
		}*/
		
		

		BigDecimal totalAmount = queryCurrentTotalAmount(params);
		long totalCount = pageMap.getTotalElements();
		int currentPage = (Integer)params.get("page");
		int pageSize =(Integer) params.get("pageSize");
		result.put("totalPage", (totalCount + pageSize- 1) / pageSize+"");
		result.put("currentPage", currentPage + "");
		result.put("totalCount", totalCount+"");
		result.put("totalAmount",totalAmount+"");
		result.put("borrowList", wdzjList);
		return result;
	}

	private Page<Map<String, Object>> queryLoaninfoAndInvestlist(Map<String, Object> params) {
		String sqlCondition = getWdzjSql(params);
		int currentPage = (Integer)params.get("page");
		int pageSize = (Integer) params.get("pageSize");
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("date"));
		args.add(params.get("date"));
		args.add(params.get("date"));
		args.add(params.get("date"));
		args.add(params.get("date"));
		return repositoryUtil.queryForPageMap(sqlCondition.toString(),args.toArray(), pageSize * (currentPage - 1), pageSize);
	}

	/**
	 * 网贷之家优选项目和债权转让：借款标信息
	 * 
	 * @author fengyl
	 * @date 2017-4-17
	 * @param params
	 * @return <tt>projectId    :String:项目主键(唯一)</tt><br>
	 *         <tt>title    :String:借款标题</tt><br>
	 *         <tt>amount    :String:借款金额(若标未满截标，以投标总额为准)</tt><br>
	 *         <tt>schedule    :String:String	进度*例如：100（只传满标数据，进度均为100）</tt><br>
	 *         <tt>interestRate    :String:利率* 百分比 例如：24.5% 统一转化为年化利率传过来。</tt><br>
	 *         <tt>deadline:String:借款期限</tt><br>
	 *         <tt>deadlineUnit:String:期限单位* 仅限 ‘天’ 或 ‘月’</tt><br>
	 *         <tt>reward:String:奖励 平台系统无奖励字段，则统一返回0</tt><br>
	 *         <tt>type:String:信用标，债权转让标</tt><br>
	 *         <tt>repaymentType:String:还款方式</tt><br>
	 *         <tt>userName:String:发标人ID</tt><br>
	 *         <tt>loanUrl:String:标的详细页面地址链接</tt><br>
	 *         <tt>successTime:String:标被投满的时间</tt><br>
	 * @throws SLException
	 */
	private String getWdzjSql(Map<String, Object> param) {
		StringBuffer sql = new StringBuffer()
				.append(" SELECT loan.ID \"projectId\", ")
				.append("                loan.LOAN_TITLE \"title\", ")			
				.append("                loan.loan_amount \"amount\", ")
				.append("                nvl(TRUNC(pi.ALREADY_INVEST_SCALE * 100), 0) \"schedule\", ")
				.append("                TRUNC(nvl(ld.YEAR_IRR*100,0),2)||'%' \"interestRate\", ")
				.append("                nvl(loan.LOAN_TERM, 0) \"deadline\", ")
				.append("                loan.LOAN_UNIT \"deadlineUnit\", ")
				.append("                0 \"reward\", ")
				.append("                '信用标' \"type\", ")			
				.append("                decode(loan.repayment_method, ")
				.append("                       '到期还本付息', ")
				.append("                       '1', ")
				.append("                       '等额本息', ")
				.append("                       '2','先息后本','5', ")
				.append("                       '每期还息到期付本', ")
				.append("                       '5') \"repaymentType\", ")				
				.append("                lc.id \"userName\", ")
				.append("               '"+SLCF_LOAN_BASEURL+"'||loan.ID \"loanUrl\", ")
				.append("                to_char(pi.last_update_date,'yyyy-mm-dd hh24:mi:ss') \"successTime\" ")
				.append("           FROM BAO_T_LOAN_INFO loan ")
				.append("          INNER JOIN BAO_T_LOAN_CUST_INFO lc ")
				.append("             ON lc.ID = loan.RELATE_PRIMARY ")
				.append("           LEFT JOIN BAO_T_LOAN_DETAIL_INFO ld ")
				.append("             ON ld.LOAN_ID = loan.ID ")
				.append("           LEFT JOIN BAO_T_PROJECT_INVEST_INFO pi ")
				.append("             ON pi.LOAN_ID = loan.ID ")
				.append("          WHERE 1 = 1 ")
				.append("            AND loan.LOAN_STATUS in ('已到期','正常') ")
				.append("            and to_date(pi.last_update_date) = to_date(?,'yyyy-mm-dd hh24:mi:ss') ")
				.append("                      UNION ALL ")
				// UNION ALL 前面查询优选项目借款标信息 ，之后查询债权转让借款标信息
				.append(" SELECT lta.ID||'_'||to_char(lta.create_date,'yyyyMMdd') \"projectId\", ")
				.append("        loan.LOAN_TITLE \"title\", ")
				.append("        lta.trade_amount \"amount\", ")	
				.append("        trunc((1 - lta.REMAINDER_TRADE_SCALE / lta.TRADE_SCALE) * 100, 0) AS \"schedule\", ")
				.append("         TRUNC(nvl(ld.YEAR_IRR*100,0),2)||'%' \"interestRate\", ")
				.append("        ceil(months_between(loan.invest_end_date,to_date(?,'yyyy-mm-dd hh24:mi:ss'))) AS \"deadline\",")
				.append("        loan.LOAN_UNIT \"deadlineUnit\", ")
				.append("        0 \"reward\", ")
				.append("        '债权转让标' \"type\", ")						
				.append("                decode(loan.repayment_method, ")
				.append("                       '到期还本付息', ")
				.append("                       '1', ")
				.append("                       '等额本息', ")
				.append("                       '2','先息后本','5', ")
				.append("                       '每期还息到期付本', ")
				.append("                       '5') \"repaymentType\", ")
				.append("        lta.sender_cust_id \"userName\", ")				
				.append("        '"+SLCF_LOAN_TRANSFER_BASEURL+"'||lta.ID \"loanUrl\", ")
				.append("        to_char(lta.last_update_date,'yyyy-mm-dd hh24:mi:ss') \"successTime\" ")
				.append("   FROM BAO_T_LOAN_TRANSFER_APPLY lta, ")
				.append("        BAO_T_WEALTH_HOLD_INFO    wh, ")
				.append("        BAO_T_LOAN_INFO           loan, ")
				.append("        BAO_T_LOAN_DETAIL_INFO    ld ")
				.append("  WHERE 1 = 1 ")
				.append("    AND ld.LOAN_ID = loan.ID ")
				.append("    AND loan.ID = wh.LOAN_ID ")
				.append("    AND wh.ID = lta.SENDER_HOLD_ID ")
				.append("    AND lta.APPLY_STATUS in ('部分转让成功') ")
				.append("    AND lta.CANCEL_STATUS = '已撤销' ")
				.append("    and lta.AUDIT_STATUS = '通过' ")
				.append("    and to_date(lta.last_update_date) = to_date(?,'yyyy-mm-dd hh24:mi:ss') ")
				.append("                      UNION ALL ")
				.append(" SELECT lta.ID||'_'||to_char(lta.create_date,'yyyyMMdd') \"projectId\", ")
				.append("        loan.LOAN_TITLE \"title\", ")
				.append("        lta.trade_amount \"amount\", ")	
				.append("        trunc((1 - lta.REMAINDER_TRADE_SCALE / lta.TRADE_SCALE) * 100, 0) AS \"schedule\", ")
				.append("         TRUNC(nvl(ld.YEAR_IRR*100,0),2)||'%' \"interestRate\", ")
				.append("        ceil(months_between(loan.invest_end_date,to_date(?,'yyyy-mm-dd hh24:mi:ss'))) AS \"deadline\",")
				.append("        loan.LOAN_UNIT \"deadlineUnit\", ")
				.append("        0 \"reward\", ")
				.append("        '债权转让标' \"type\", ")						
				.append("                decode(loan.repayment_method, ")
				.append("                       '到期还本付息', ")
				.append("                       '1', ")
				.append("                       '等额本息', ")
				.append("                       '2','先息后本','5', ")
				.append("                       '每期还息到期付本', ")
				.append("                       '5') \"repaymentType\", ")
				.append("        lta.sender_cust_id \"userName\", ")				
				.append("        '"+SLCF_LOAN_TRANSFER_BASEURL+"'||lta.ID \"loanUrl\", ")
				.append("        to_char(lta.last_update_date,'yyyy-mm-dd hh24:mi:ss') \"successTime\" ")
				.append("   FROM BAO_T_LOAN_TRANSFER_APPLY lta, ")
				.append("        BAO_T_WEALTH_HOLD_INFO    wh, ")
				.append("        BAO_T_LOAN_INFO           loan, ")
				.append("        BAO_T_LOAN_DETAIL_INFO    ld ")
				.append("  WHERE 1 = 1 ")
				.append("    AND ld.LOAN_ID = loan.ID ")
				.append("    AND loan.ID = wh.LOAN_ID ")
				.append("    AND wh.ID = lta.SENDER_HOLD_ID ")
				.append("    AND lta.APPLY_STATUS in ('转让成功') ")
				.append("    AND lta.CANCEL_STATUS = '未撤销' ")
				.append("    and lta.AUDIT_STATUS = '通过' ")
				.append("    and to_date(lta.last_update_date) = to_date(?,'yyyy-mm-dd hh24:mi:ss') ")
;
		return sql.toString();
	}



	/**
	 * 网贷之家优选项目： 单个标的投标列表信息
	 * 
	 * @author fengyl
	 * @date 2017-4-17
	 * @param params
	 * @return <tt>subscribeUserName    :String:投标人ID</tt><br>
	 *         <tt>amount:String:投标金额（元）</tt><br>
	 *         <tt>validAmount  :String:有效金额（元）(平台无’投标金额’和’有效金额’之分,传一样的即可)</tt><br>
	 *         <tt>addDate:     String:投标时间</tt><br>
	 *         <tt>status: String:投标状态</tt><br>
	 *         <tt>type: String:标识手动或自动投标0：手动 1：自动注意:平台没有这个字段的默认为0 </tt><br>
	 * @throws SLException
	 */
	public List<WdzjInvestinfoVo> queryDisperseInvestDeatailList(
			Map<String, Object> param) {

		/*StringBuffer sql = new StringBuffer()
				.append(" SELECT  ")
				.append("        invest.cust_id \"subscribeUserName\", ")
				.append("        TRUNC(invest.INVEST_AMOUNT, 2) \"amount\", ")
				.append("        TRUNC(invest.INVEST_AMOUNT, 2) \"validAmount\", ")
				.append("        to_char(investDetail.create_Date, 'yyyy-mm-dd hh24:mi:ss') \"addDate\", ")
				.append("        1 \"status\", ")
				.append("        decode(investDetail.Invest_Source, 'auto', 1, 0) \"type\" ")
				.append("   FROM BAO_T_INVEST_INFO invest, bao_t_invest_detail_info investDetail ")
				.append("  WHERE invest.id = investDetail.Invest_Id ")
				.append("    AND invest.INVEST_MODE = '加入' ")
				.append("    and invest.loan_id = ? ");
		List<Object> args = new ArrayList<Object>();
		args.add(projectId);
		return jdbcTemplate.query(sql.toString(), args.toArray(),new WdzjInvestinfoVOMapper());*/
		
		StringBuilder sql = new StringBuilder()
		.append(" SELECT   ")
		.append("         invest.loan_id \"projectId\", ")
		.append("         invest.cust_id \"subscribeUserName\",  ")
		.append("         TRUNC(invest.INVEST_AMOUNT, 2) \"amount\",  ")
		.append("         TRUNC(invest.INVEST_AMOUNT, 2) \"validAmount\",  ")
		.append("         to_char(investDetail.create_Date, 'yyyy-mm-dd hh24:mi:ss') \"addDate\",  ")
		.append("         1 \"status\",  ")
		.append("         decode(investDetail.Invest_Source, 'auto', 1, 0) \"type\"  ")
		.append("    FROM BAO_T_INVEST_INFO invest, bao_t_invest_detail_info investDetail  ")
		.append("   WHERE invest.id = investDetail.Invest_Id  ")
		.append("     AND invest.INVEST_MODE = '加入'  ")
		.append("     and invest.loan_id in ( ")
		.append("         SELECT loan.ID  ")
		.append("        FROM BAO_T_LOAN_INFO loan  ")
		.append("       INNER JOIN BAO_T_LOAN_CUST_INFO lc  ")
		.append("          ON lc.ID = loan.RELATE_PRIMARY  ")
		.append("        LEFT JOIN BAO_T_LOAN_DETAIL_INFO ld  ")
		.append("          ON ld.LOAN_ID = loan.ID  ")
		.append("        LEFT JOIN BAO_T_PROJECT_INVEST_INFO pi  ")
		.append("          ON pi.LOAN_ID = loan.ID  ")
		.append("       WHERE loan.LOAN_STATUS in ('已到期','正常')  ")
		.append("         and to_date(pi.last_update_date) = to_date(?,'yyyy-mm-dd hh24:mi:ss')  ")
		.append("     )  ")
		.append("     order by invest.loan_id ");
		return jdbcTemplate.query(sql.toString(), new Object[]{param.get("date")}, new WdzjInvestinfoVOMapper());
	}

	/**
	 * 网贷之家债权转让： 单个标的投标列表信息
	 * 
	 * @author fengyl
	 * @date 2017-4-17
	 * @param params
	 * @return <tt>subscribeUserName    :String:投标人ID</tt><br>
	 *         <tt>amount:String:投标金额（元）</tt><br>
	 *         <tt>validAmount  :String:有效金额（元）(平台无’投标金额’和’有效金额’之分,传一样的即可)</tt><br>
	 *         <tt>addDate:     String:投标时间</tt><br>
	 *         <tt>status: String:投标状态</tt><br>
	 *         <tt>type: String:标识手动或自动投标0：手动 1：自动注意:平台没有这个字段的默认为0 </tt><br>
	 * @throws SLException
	 */
	public List<WdzjInvestinfoVo> queryCreditInvestDeatailList(Map<String, Object> param) {
		/*StringBuffer sql = new StringBuffer()
				.append("  SELECT receiveCust.Id \"subscribeUserName\",lt.trade_amount \"amount\",lt.trade_amount \"validAmount\", ")
				.append("        to_char(investDetail.create_Date, 'yyyy-mm-dd hh24:mi:ss') \"addDate\", ")
				.append("        1 \"status\", ")
				.append("        decode(investDetail.Invest_Source, 'auto', 1, 0) \"type\" ")
				.append("   FROM BAO_T_LOAN_TRANSFER lt ")
				.append("  INNER JOIN BAO_T_CUST_INFO receiveCust ")
				.append("     ON receiveCust.ID = lt.RECEIVE_CUST_ID ")
				.append("  INNER JOIN BAO_T_CUST_INFO senderCust ")
				.append("     ON senderCust.ID = lt.SENDER_CUST_ID ")
				.append("   inner join  bao_t_wealth_hold_info wh ")
				.append("   on wh.id = lt.receive_hold_id ")
				.append("   inner join  BAO_T_INVEST_INFO invest ")
				.append("   on invest.id = wh.invest_id ")
				.append("   inner join  bao_t_invest_detail_info investDetail ")
				.append("   on investDetail.invest_id = invest.id ")
				.append("  WHERE 1 = 1 ")
				.append("  and lt.TRANSFER_APPLY_ID = ? ");
		List<Object> args = new ArrayList<Object>();
		args.add(projectId);
		return jdbcTemplate.query(sql.toString(), args.toArray(),new WdzjInvestinfoVOMapper());*/
		
		StringBuilder sql = new StringBuilder()
		.append("   SELECT lt.TRANSFER_APPLY_ID \"projectId\",  ")
		.append("                 receiveCust.Id \"subscribeUserName\",lt.trade_amount \"amount\",lt.trade_amount \"validAmount\",  ")
		.append(" 				        to_char(investDetail.create_Date, 'yyyy-mm-dd hh24:mi:ss') \"addDate\",  ")
		.append(" 				        1 \"status\",  ")
		.append(" 				        decode(investDetail.Invest_Source, 'auto', 1, 0) \"type\"  ")
		.append(" 				   FROM BAO_T_LOAN_TRANSFER lt  ")
		.append(" 				  INNER JOIN BAO_T_CUST_INFO receiveCust  ")
		.append(" 				     ON receiveCust.ID = lt.RECEIVE_CUST_ID  ")
		.append(" 				  INNER JOIN BAO_T_CUST_INFO senderCust  ")
		.append(" 				     ON senderCust.ID = lt.SENDER_CUST_ID  ")
		.append(" 				   inner join  bao_t_wealth_hold_info wh  ")
		.append(" 				   on wh.id = lt.receive_hold_id  ")
		.append(" 				   inner join  BAO_T_INVEST_INFO invest  ")
		.append(" 				   on invest.id = wh.invest_id  ")
		.append(" 				   inner join  bao_t_invest_detail_info investDetail  ")
		.append(" 				   on investDetail.invest_id = invest.id  ")
		.append(" 				  WHERE 1 = 1  ")
		.append(" 				  and lt.TRANSFER_APPLY_ID in ( ")
		.append("              SELECT lta.ID ")
		.append("              FROM BAO_T_LOAN_TRANSFER_APPLY lta,  ")
		.append("                   BAO_T_WEALTH_HOLD_INFO    wh,  ")
		.append("                   BAO_T_LOAN_INFO           loan,  ")
		.append("                   BAO_T_LOAN_DETAIL_INFO    ld  ")
		.append("             WHERE 1 = 1  ")
		.append("               AND ld.LOAN_ID = loan.ID  ")
		.append("               AND loan.ID = wh.LOAN_ID  ")
		.append("               AND wh.ID = lta.SENDER_HOLD_ID  ")
		.append("               AND lta.APPLY_STATUS in ('转让成功')  ")
		.append("               AND lta.CANCEL_STATUS = '未撤销'  ")
		.append("               and lta.AUDIT_STATUS = '通过'  ")
		.append("               and to_date(lta.last_update_date) = to_date(?,'yyyy-mm-dd hh24:mi:ss')  ")
		.append("           ) ")
		.append("     order by lt.TRANSFER_APPLY_ID ");;
		
		return jdbcTemplate.query(sql.toString(), new Object[]{param.get("date")}, new WdzjInvestinfoVOMapper());
	}

	@Override
	public BigDecimal queryCurrentTotalAmount(Map<String, Object> param) {
		String sqlCondition = getWdzjSql(param);
		StringBuilder sql = new StringBuilder()
				.append(" SELECT nvl(trunc(sum(\"amount\"),2),0) \"totalAmount\" FROM ( ")
				.append(sqlCondition.toString()).append(" ) ");
		List<Object> args = new ArrayList<Object>();
		 args.add(param.get("date"));
		 args.add(param.get("date"));
		 args.add(param.get("date"));
		 args.add(param.get("date"));
		 args.add(param.get("date"));
		BigDecimal totalAmount = jdbcTemplate.queryForObject(sql.toString(),args.toArray(), BigDecimal.class);
		return totalAmount;
	}
	private List<WdzjLoaninfoVO> buildVO(List<Map<String, Object>> mapList) {
		ArrayList<WdzjLoaninfoVO> list = new ArrayList<WdzjLoaninfoVO>();
		for (Map<String, Object> voMap : mapList) {
			WdzjLoaninfoVO vo = new WdzjLoaninfoVO();
			vo.setProjectId((String) voMap.get("projectId"));
			vo.setUserName((String) voMap.get("userName"));
			vo.setInterestRate((String) voMap.get("interestRate"));
			vo.setDeadline((voMap.get("deadline").toString()));
			vo.setDeadlineUnit((String) voMap.get("deadlineUnit"));
			vo.setReward((voMap.get("reward").toString()));
			vo.setAmount((voMap.get("amount").toString()));
			vo.setSchedule(voMap.get("schedule").toString());
			vo.setSuccessTime(voMap.get("successTime").toString());
			vo.setRepaymentType((String)voMap.get("repaymentType"));
			vo.setTitle((String) voMap.get("title"));
			vo.setLoanUrl((String) voMap.get("loanUrl"));
			vo.setType((String) voMap.get("type"));

			list.add(vo);
		}
		return list;
	}

	private static class WdzjInvestinfoVOMapper implements RowMapper<WdzjInvestinfoVo> {
		@Override
		public WdzjInvestinfoVo mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			WdzjInvestinfoVo vo = new WdzjInvestinfoVo();
			vo.setSubscribeUserName(rs.getString("subscribeUserName"));
			vo.setAmount(rs.getString("amount"));
			vo.setValidAmount(rs.getString("validAmount"));
			vo.setAddDate(rs.getString("addDate"));
			vo.setStatus(rs.getString("status"));
			vo.setType(rs.getString("type"));
			vo.setProjectId(rs.getString("projectId"));
			return vo;
		}
	}

	}
