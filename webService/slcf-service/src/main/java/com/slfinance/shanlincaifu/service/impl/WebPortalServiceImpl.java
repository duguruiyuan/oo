package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.service.WebPortalService;
import com.slfinance.shanlincaifu.utils.CommonUtils;
/**
 * 
 * <网贷天眼信息公示接口实现>
 * <功能详细描述>
 * 
 * @author  Mgp
 * @version  [版本号, 2017年6月14日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@SuppressWarnings("all")
@Service("webPortalService")
@Transactional(readOnly=true)
public class WebPortalServiceImpl implements WebPortalService {
	
	@Autowired
	RepositoryUtil repositoryUtil;
	
	@Value("${webportal.loan.url}")
	private String loanUrl;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 返回状态码
	 */
	private int result_code;
	
	/**
	 * 返回消息
	 */
	private String result_msg;

	/**
	 * 借款查询主方法
	 * @LoanDataSql() 借款sql拼接
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public Map<String,Object> loanInfoMap(Map<String, Object> params) throws ParseException {
		if(check(CommonUtils.emptyToString(params.get("token")), "p2pEye")) {
			return WebPortalData(params,LoanDataSql(),transferLoanDataSql(),0,"credit");
		}
		return null;
	}

	/**
	 * 投资查询主方法
	 * @sqlDataInvest() 投资sql拼接
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public Map<String,Object> investInfoMap(Map<String, Object> params) throws ParseException{
		if(check(CommonUtils.emptyToString(params.get("token")), "p2pEye")) {
			return WebPortalData(params,InvestDataSql(),transferInvestDataSql(),1,"credit");
		}
		return null;
	}

	@Override
	public Map<String, Object> token(String username, String password) {
		final String sql = "select username, password from BP_T_EYE_SKY where record_status='有效' and username = ?";
		String result = "-1";
		Map<String, String> data = ImmutableMap.of("token", "");

		ImmutableMap.Builder<String, Object> builder = new ImmutableMap.Builder<>();

		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql, new Object[]{username});
		if (!list.isEmpty()) {
			// 媒体帐号
			Map<String, Object> account = list.get(0);
			if (DigestUtils.md5DigestAsHex(password.getBytes()).equals(CommonUtils.emptyToString(account.get("password")))) {
				result = "1";
				data = ImmutableMap.of("token", genToken(username));
			}
		}
		builder.put("result", result)
				.put("data", data);

		return builder.build();
	}

	private String genToken(String username) {
		final String token = UUID.randomUUID().toString().replaceAll("-", "");
		final String updateSql = "update BP_T_EYE_SKY set token=? where username=?";
		jdbcTemplate.update(updateSql, token, username);

		return token;
	}


	@Override
	public Boolean check(String token, String username) {
		final String sql = "select count(1) from BP_T_EYE_SKY where username=? and token=? and record_status='有效'";

		int count = jdbcTemplate.queryForObject(sql, new Object[]{username, token}, new RowMapper<Integer>() {
			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getInt(1);
			}
		});
		return count >= 1;
	}

	/**
	 * 借款sql
	 * @return
	 */
	private StringBuffer LoanDataSql() {
		StringBuffer Sqlloan = new StringBuffer();
		Sqlloan.append(" SELECT DISTINCT LOAN.ID \"id\", " );
		Sqlloan.append(" 		LOAN.LOAN_DESC \"title\",  " );
		/*Sqlloan.append(" 		DECODE (TIN.INVEST_MODE,'加入',0,'转让',4) \"c_type\", " );*/
		Sqlloan.append(" 		Loan.LOAN_TERM \"period\", " );
		Sqlloan.append(" 		TO_CHAR(LOAN.INVEST_START_DATE,'yyyy-MM-dd HH24:mi:ss')\"end_time\", " );
		Sqlloan.append("  		TO_CHAR(LOAN.PUBLISH_DATE,'yyyy-MM-dd HH24:mi:ss') \"start_time\" , " );
		Sqlloan.append(" 		DECODE(LOAN.REPAYMENT_METHOD,'等额本息',1,'到期还本付息',3,'先息后本',7) \"pay_way\", " );
		Sqlloan.append(" 		DECODE(LOAN.LOAN_UNIT,'天',0,'月',1) \"p_type\", " );
		Sqlloan.append(" 		CUST.ID \"username\", " );
		Sqlloan.append(" 		CUST.ID \"userid\", " );
		Sqlloan.append(" 		DECODE(LOAN.LOAN_STATUS ,'募集中', 0, '满标复核',0,'正常', 1, '逾期', 1,'已到期',1,'提前结清',1) \"status\", " );
		Sqlloan.append(" 		TRUNC(LOAN.LOAN_AMOUNT,2) \"amount\", " );
		Sqlloan.append(" 		TRUNC(DI.YEAR_IRR,4) \"rate\", " );
		Sqlloan.append(" 		A.invest_num, ");
		Sqlloan.append(" 		NVL(TRUNC(JI.ALREADY_INVEST_SCALE*100)/100,1) \"process\" " );
		Sqlloan.append(" FROM  " );
		Sqlloan.append(" 		BAO_T_LOAN_INFO LOAN " );
		Sqlloan.append(" 		INNER JOIN BAO_T_LOAN_CUST_INFO CUST ON LOAN.RELATE_PRIMARY = CUST.ID " );
		Sqlloan.append(" 		INNER JOIN BAO_T_LOAN_DETAIL_INFO DI ON DI.LOAN_ID = LOAN.ID " );
		Sqlloan.append(" 		INNER JOIN BAO_T_INVEST_INFO TIN ON TIN.LOAN_ID = LOAN. ID " );
		Sqlloan.append(" 		INNER JOIN BAO_T_PROJECT_INVEST_INFO JI ON LOAN.ID = JI.LOAN_ID " );
		Sqlloan.append(" 		LEFT JOIN ( " );
		Sqlloan.append(" 			SELECT " );
		Sqlloan.append(" 				COUNT (LOAN_ID) invest_num, " );
		Sqlloan.append(" 				LOAN_ID " );
		Sqlloan.append(" 			FROM " );
		Sqlloan.append(" 			 	BAO_T_INVEST_INFO " );
		Sqlloan.append(" 			WHERE " );		
		Sqlloan.append(" 				TRUNC (INVEST_AMOUNT, 2) >= 50 " );		
		Sqlloan.append(" 				AND INVEST_MODE = '加入' " );	
		Sqlloan.append(" 			GROUP BY  LOAN_ID) A ON A .LOAN_ID = LOAN. ID" );		
		Sqlloan.append(" WHERE " );
		Sqlloan.append("   		TRUNC(LOAN.LOAN_AMOUNT,2)>=50 " );
		Sqlloan.append("   		AND TRUNC(TIN.INVEST_AMOUNT,2)>=50 " );
		Sqlloan.append(" 		AND LOAN.INVEST_START_DATE IS NOT NULL " );
		Sqlloan.append(" 		AND LOAN.PUBLISH_DATE IS NOT NULL " );
		/*Sqlloan.append("       AND LOAN.LOAN_STATUS IN('募集中', '正常','已到期')");*/
		return Sqlloan;
	}
	
	/**
	 * 投资sql
	 * @return
	 */
	private StringBuffer InvestDataSql() {
		StringBuffer SqlINVEST = new StringBuffer()
		.append(" SELECT LOAN.ID \"id\", " )
		.append(" 		CUST.ID \"username\", " )
		.append(" 		CUST.ID \"userid\", " )
		.append(" 		TRUNC(LOAN.LOAN_AMOUNT,2) \"money\", " )
		.append(" 		TRUNC(INVEST.INVEST_AMOUNT, 2) \"account\", " )
		/*.append(" 		TO_CHAR(LOAN.INVEST_START_DATE,'yyyy-MM-dd HH24:mi:ss') \"add_time\" " )*/
		.append(" 		TO_CHAR(DEINVEST.CREATE_DATE,'yyyy-MM-dd HH24:mi:ss') \"add_time\" " )
		.append(" FROM " )
		.append(" 		BAO_T_INVEST_INFO INVEST  " )
		.append(" 		INNER JOIN BAO_T_CUST_INFO CUST ON CUST.ID = INVEST.CUST_ID " )
		.append(" 		INNER JOIN BAO_T_LOAN_INFO LOAN  ON INVEST.LOAN_ID = LOAN.ID " )		
		.append(" 		INNER JOIN BAO_T_INVEST_DETAIL_INFO DEINVEST ON DEINVEST.INVEST_ID = INVEST.ID " )
		.append(" WHERE " )
		.append(" 		TRUNC(INVEST.INVEST_AMOUNT, 2)>=50 " )
		.append("   	AND TRUNC(LOAN.LOAN_AMOUNT,2)>=50 " )
		.append(" 		AND LOAN.INVEST_START_DATE IS NOT NULL " )
		.append(" 		AND LOAN.PUBLISH_DATE IS NOT NULL " )
		.append(" 		AND INVEST.INVEST_MODE = '加入' " )
		.append(" 		AND INVEST.INVEST_STATUS IN ('收益中','已到期','到期赎回','提前赎回','已转让')" );
		return SqlINVEST;
	}
	
	/**
	 * 转让借款标的信息sql拼接
	 * @return
	 */
	private StringBuffer transferLoanDataSql() {
		StringBuffer SqltransferLoan = new StringBuffer()
		.append(" SELECT DISTINCT " )
		.append("   	APPLY.ID \"id\", " )
		.append(" 		LOAN.LOAN_DESC \"title\",  " )
		.append(" 		LOAN.ID \"loanId\",  " )
		.append("  		TRANSFER.SENDER_CUST_ID \"username\", " )
		.append("  		TRANSFER.SENDER_CUST_ID \"userid\", " )
		.append("  		TRUNC(APPLY.TRADE_AMOUNT,2) \"amount\", " )
		.append("  		DI.year_irr \"rate\", " )
		.append("  		Loan.LOAN_TERM \"period\", " )
		.append(" 		DECODE(LOAN.LOAN_STATUS ,'募集中', 0, '满标复核',0,'正常', 1, '逾期', 1,'已到期',1,'提前结清',1) \"status\", " )
		.append("  		DECODE(LOAN.LOAN_UNIT,'天',0,'月',1) \"p_type\", " )
		.append("  		DECODE(LOAN.REPAYMENT_METHOD,'等额本息',1,'到期还本付息',3,'先息后本',7) \"pay_way\", " )
		.append("  		NVL(TRUNC(JI.ALREADY_INVEST_SCALE*100)/100,1) \"process\", " )
		.append("  		TO_CHAR(APPLY.TRANSFER_START_DATE,'yyyy-MM-dd HH24:mi:ss') \"start_time\", " )
		.append("  		TO_CHAR(APPLY.LAST_UPDATE_DATE,'yyyy-MM-dd HH24:mi:ss') \"end_time\", " )
		.append("  	    A.invest_num \"invest_num\" " )
		.append(" FROM " )
		.append("  		BAO_T_LOAN_TRANSFER TRANSFER " )
		.append("  		INNER JOIN BAO_T_LOAN_TRANSFER_APPLY APPLY ON TRANSFER.TRANSFER_APPLY_ID = APPLY.ID" )
		.append("  		INNER JOIN BAO_T_LOAN_INFO LOAN ON  LOAN.ID = TRANSFER.SENDER_LOAN_ID " )
		.append("  		INNER JOIN BAO_T_LOAN_DETAIL_INFO DI ON  DI.LOAN_ID = TRANSFER.SENDER_LOAN_ID " )
		.append("  		INNER JOIN BAO_T_PROJECT_INVEST_INFO JI ON TRANSFER.SENDER_LOAN_ID = JI.LOAN_ID  " )
		.append(" 		LEFT JOIN ( " )
		.append(" 			SELECT " )
		.append(" 				COUNT (TRANSFER_APPLY_ID) invest_num, " )
		.append(" 				TRANSFER_APPLY_ID " )
		.append(" 			FROM " )
		.append(" 			 	BAO_T_LOAN_TRANSFER " )
		.append(" 			WHERE " )	
		.append(" 				TRUNC (TRADE_AMOUNT, 2) >= 50 " )	
		.append(" 			GROUP BY  TRANSFER_APPLY_ID) A ON A .TRANSFER_APPLY_ID = APPLY. ID" )
		.append(" WHERE " )
		.append("  		TRUNC(APPLY.TRADE_AMOUNT,2)>=50 " )
		.append("  		AND APPLY.APPLY_STATUS IN('转让成功','部分转让成功') " )
		.append("  		AND APPLY.TRANSFER_START_DATE IS NOT NULL ")
		.append("  		AND APPLY.TRANSFER_end_DATE IS NOT NULL ");
		return SqltransferLoan;
	}

	/**
	 * 转让投资标的信息sql拼接
	 * @return
	 */
	private StringBuffer transferInvestDataSql() {
		StringBuffer SqltransferInvest = new StringBuffer()
		.append(" SELECT " )
		.append(" 		APPLY.ID \"id\", " )
		.append(" 		TRANSFER.SENDER_CUST_ID \"username\", " )
		.append(" 		TRANSFER.SENDER_CUST_ID \"userid\", " )
		.append(" 		TRUNC(TRANSFER.TRADE_AMOUNT,2) \"account\"," )
		/*.append(" 		TO_CHAR(APPLy.TRANSFER_START_DATE,'yyyy-MM-dd HH24:mi:ss') \"add_time\" " )*/
		.append(" 		TO_CHAR(TRANSFER.CREATE_DATE,'yyyy-MM-dd HH24:mi:ss') \"add_time\" " )
		.append(" FROM " )
		.append(" 		BAO_T_LOAN_TRANSFER TRANSFER " )
		.append(" 		INNER JOIN BAO_T_LOAN_TRANSFER_APPLY APPLY ON TRANSFER.TRANSFER_APPLY_ID = APPLY.ID " )
		.append(" 		INNER JOIN BAO_T_LOAN_INFO LOAN ON TRANSFER.SENDER_LOAN_ID  = LOAN.ID " )
		.append(" WHERE" )
		.append("  		TRUNC(APPLY.TRADE_AMOUNT,2)>=50 " )
		.append(" 		AND TRANSFER.RECORD_STATUS = '有效' " )
		.append("  		AND APPLY.TRANSFER_START_DATE IS NOT NULL ")
		.append("  		AND APPLY.TRANSFER_END_DATE IS NOT NULL ");
		return SqltransferInvest;
	}
	
	/**
	 * 投资借款信息公共处理逻辑(包括债券转让)
	 * @param params
	 * @param Sql
	 * @throws ParseException
	 */
	private Map WebPortalData(Map<String, Object> params,StringBuffer creditSql,StringBuffer TransferSql,int sta,String type) throws ParseException {
		
		//获取参数
		int status=CommonUtils.emptyToInt(params.get("status"));
		
		//每页记录条数
		int page_size=CommonUtils.emptyToInt(params.get("page_size"));
				
		//请求的页码
		int page_index=CommonUtils.emptyToInt(params.get("page_index"));
		
		//存储参数值
		List<Object> paramList=new ArrayList<Object>();
		
		/**
		 * 起始时间状态
		 * 为1是对应平台满标字段的值检索
		 * 状态为0就以平台发标时间字段检索
		 */
		Date time_from =null;
		
		/**
		 * 截止时间状态
		 * 为1是对应平台满标字段的值检索
		 * 状态为0就以平台发标时间字段检索
		 */
		Date time_to =null;
		
		//存储标的信息(all)
		//Map<String,Object> allData = new HashMap<String, Object>();
		
		//返回信用标的查询数据
		Page<Map<String, Object>> PageCreditData = null;
		
		//返回债券转让标的查询数据
		Page<Map<String, Object>> PageTransferData  = null;
		
		time_from = (Date)new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String) params.get("time_from"));
		time_to = (Date)new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String) params.get("time_to"));
		
		//返回信用标的信息
		if(!StringUtils.isEmpty(params.get("time_from")) && !StringUtils.isEmpty(params.get("time_to"))){
			switch(status){
				case 0:	
					creditSql.append(" AND LOAN.PUBLISH_DATE BETWEEN ? AND ? ");
					creditSql.append(" AND LOAN.LOAN_STATUS IN ('募集中','满标复核') ");
					paramList.add(time_from);
					paramList.add(time_to);
					break;
				case 1:
					creditSql.append(" AND LOAN.INVEST_START_DATE BETWEEN ? AND ? ");
					creditSql.append(" AND LOAN.LOAN_STATUS IN ('正常','逾期','提前结清','已到期') ");
					paramList.add(time_from);
					paramList.add(time_to);
					break;
			}
			
			try {
				PageCreditData = repositoryUtil.queryForPageMap(creditSql.toString(), paramList.toArray(), (page_index - 1 ) * page_size, page_size);
				result_code = 1;
				result_msg = "获取数据成功";
			} catch (Exception e) {
				result_msg = e.getMessage();
				result_code = -1;
			}
		}else{
			result_msg = "参数错误";
			result_code = -1;
		}
		
		//返回债券转让标的信息
		if(!StringUtils.isEmpty(params.get("time_from")) && !StringUtils.isEmpty(params.get("time_to"))){
			switch(status){
				case 0:	
					TransferSql.append(" AND APPLY.TRANSFER_START_DATE BETWEEN ? AND ? ");
					TransferSql.append(" AND LOAN.LOAN_STATUS IN ('募集中','满标复核') ");
					System.err.println(TransferSql);
					break;
				case 1:
					TransferSql.append(" AND APPLY.LAST_UPDATE_DATE  BETWEEN ? AND ? ");
					TransferSql.append(" AND LOAN.LOAN_STATUS IN ('正常','逾期','提前结清','已到期') ");
					System.err.println(TransferSql);
					break;
			}
			
			try {
				PageTransferData = repositoryUtil.queryForPageMap(TransferSql.toString(), paramList.toArray(), (page_index - 1 ) * page_size, page_size);
				result_code = 1;
				result_msg = "获取数据成功";
			} catch (Exception e) {
				result_msg = e.getMessage();
				result_code = -1;
			}
		}else{
			result_msg = "参数错误";
			result_code = -1;
		}
		return resultData(PageCreditData,PageTransferData,page_index,sta);	
	}
	

	/**
	 * 拼接数据
	 * @param 
	 * @return
	 */
	private Map resultData(Page<Map<String, Object>> PageCreditData,Page<Map<String, Object>> PageTransferData,int page_index,int sta) {
		
		//返回数据map
		Map data = new HashMap();
		
		int i = PageCreditData.getTotalPages();
		int j = PageTransferData.getTotalPages();
		
		//无查询数据时返回信息
		if(PageCreditData.getTotalPages() == 0 && PageTransferData.getTotalPages()==0){
			data.put("result_code",-1);
			data.put("result_msg", "未授权的访问");
			data.put("page_count",0);
			data.put("page_index",0);
			data.put("loans",null);
			return data;
		}else{
			//存储信用标的查询数据
			List dataCredit = new ArrayList();
			
			//存储债券转让查询数据
			List dataTransfer = new ArrayList();
			
			//总页数
			int page_count = 0;
			
			if(PageCreditData.getTotalPages()>PageTransferData.getTotalPages()){
				page_count = CommonUtils.emptyToInt(PageCreditData.getTotalPages());
			}else{
				page_count = CommonUtils.emptyToInt(PageTransferData.getTotalPages());
			}
			
			//当前页
			page_index = CommonUtils.emptyToInt(PageCreditData.getNumber()); 
			
			if(PageCreditData != null){
				//根据需求重新封装数据
				for(Map<String, Object> map:PageCreditData){
					map.remove("ROWNUM_");
					map.put("platform_name", "善林财富");
					switch(sta){
						case 0://拼接的是借款的数据
							map.put("pay_way",CommonUtils.emptyToInt(map.get("pay_way")));
							/*map.put("c_type",CommonUtils.emptyToInt(map.get("c_type")));*/
							map.put("c_type",0);
							map.put("amount", new DecimalFormat("0.00").format(map.get("amount")));
							// 异常数据
							if (CommonUtils.emptyToDecimal(map.get("rate")).compareTo(BigDecimal.ZERO) <= 0) {
								map.put("rate", 0.06);
							}
							map.put("rate", new DecimalFormat("0.0000").format(map.get("rate")));
							map.put("process", ((BigDecimal)(map.get("process"))).doubleValue());
							map.put("url", loanUrl+"?id="+map.get("id"));
							break;
						case 1://拼接的是投资的数据
							map.put("account", ((BigDecimal)(map.get("account"))).doubleValue());
							//map.put("link", loanUrl+"?id="+map.get("id"));
							break;	
					}
					dataCredit.add(map);
				}
			}
			
			//根据需求重新封装数据
			if(PageTransferData != null){
				for(Map<String, Object> map:PageTransferData){
					map.remove("ROWNUM_");
					map.put("platform_name", "善林财富");
					switch(sta){
						case 0://拼接的是借款的数据
							map.put("pay_way",CommonUtils.emptyToInt(map.get("pay_way")));
							map.put("c_type",4);
							map.put("amount", new DecimalFormat("0.00").format(map.get("amount")));
							// 异常数据
							if (CommonUtils.emptyToDecimal(map.get("rate")).compareTo(BigDecimal.ZERO) <= 0) {
								map.put("rate", 0.06);
							}
							map.put("rate", new DecimalFormat("0.0000").format(map.get("rate")));
							map.put("process", ((BigDecimal)(map.get("process"))).doubleValue());
							map.put("url", loanUrl+"?id="+map.get("loanId"));
							map.remove("loanId");
							break;
						case 1://拼接的是投资的数据
							map.put("account", ((BigDecimal)(map.get("account"))).doubleValue());
							//map.put("link", loanUrl+"?id="+map.get("id"));
							break;	
					}
					dataTransfer.add(map);
				}
			}
			dataTransfer.addAll(dataCredit);
			data.put("result_code",result_code);
			data.put("result_msg",result_msg);
			data.put("page_index",page_index);
			data.put("page_count",page_count);
			data.put("loans",dataTransfer);
			return data;
		}
	}
	
}
