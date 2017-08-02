package com.slfinance.shanlincaifu.service.impl;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.slfinance.shanlincaifu.entity.LoanDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.repository.LoanDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanInfoRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.service.ExperienceLabelService;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.vo.ResultVo;
import com.slfinance.shanlincaifu.service.FlowNumberService;
/**
 * 体验标接口实现类
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  Mgp
 * @version  [版本号, 2017年6月29日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@SuppressWarnings("all")
@Service("experienceLabelService")
@Transactional(readOnly=true)
public class ExperienceLabelServiceImpl implements ExperienceLabelService {
	
	@Autowired
	private FlowNumberService flowNumberService;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private LoanInfoRepository loanInfoRepository;
	
	@Autowired
	private LoanDetailInfoRepository loanDetailInfoRepository;
	
	/**
	 * 体验标的列表查询
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> querySubject(Map<String, Object> params) throws Exception {
		StringBuffer Sql=new StringBuffer()
		.append("SELECT LOAN.ID \"id\", ")
		.append("   LOAN.LOAN_CODE \"loanCode\", ")
		.append("	LOAN.LOAN_TERM \"loanTerm\", ")
		.append("	LOAN.MEMO \"memo\", ")
		.append("	LOAN.LOAN_TITLE \"loanTitle\", ")
		.append("	TRUNC(DETAIL.YEAR_IRR*100,2) \"yearIrr\", ")
		.append("	LOAN.LOAN_STATUS \"loanStatus\", ")
		.append("   LOAN.CUST_ID \"custId\" ")
//		.append("   LOAN.PROTOCAL_TYPE \"protocalType\" ")
		.append(" FROM ")
		.append("	BAO_T_LOAN_INFO LOAN ")
		.append(" 	LEFT OUTER JOIN BAO_T_LOAN_DETAIL_INFO DETAIL ON DETAIL.LOAN_ID= LOAN.ID")
		.append(" WHERE ")
		.append(" 	LOAN.LOAN_STATUS IN('启用','禁用') ORDER BY LOAN.CREATE_DATE DESC ");
		
		Map<String,Object> map = new HashMap<String, Object>();
		Page<Map<String, Object>> page =  repositoryUtil.queryForPageMap(Sql.toString(),null,Integer.parseInt(params.get("start").toString()),Integer.parseInt(params.get("length").toString()));
		
		map.put("data",page.getContent());
		map.put("iTotalDisplayRecords", page.getTotalElements());
		return map;
	}
	/**
	 * 新增体验标
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional
	public ResultVo insertLabel(Map<String, Object> params) throws Exception {
		// TODO Administrator 简要描述
		String loanTitle=CommonUtils.emptyToString(params.get("loanTitle"));
		Long loanTerm=Long.valueOf(params.get("loanTerm").toString());
		
		//优化
		BigDecimal yearIrr=CommonUtils.emptyToDecimal(params.get("yearIrr")).
				divide(new BigDecimal(100));
//		
//		String loanStatus=CommonUtils.emptyToString(params.get("loanStatus"));
//		String protocalType=CommonUtils.emptyToString(params.get("protocalType"));
//		if(!StringUtils.isEmpty(loanStatus) && "启用".equals(loanStatus)){
//			StringBuffer sbf=new StringBuffer()
//			.append("SELECT * FROM BAO_T_LOAN_INFO T ")
//			.append("WHERE ")
//			.append("T.LOAN_STATUS='启用' ");
//			List<Map<String,Object>> lis= repositoryUtil.queryForMap(sbf.toString(), null);
//			if(lis.size()>=1 && null !=lis){
//				return new ResultVo(false,"仅可设置一个启用中的体验标");
//			}
//			
//		}	
		String memo=CommonUtils.emptyToString(params.get("memo"));
		String loanInfo=CommonUtils.emptyToString(params.get("loanInfo"));
		
		LoanInfoEntity loanInfoe =new LoanInfoEntity();
		LoanDetailInfoEntity loandetail =new LoanDetailInfoEntity();
		loanInfoe.setLoanTitle(loanTitle);
		loanInfoe.setLoanTerm(loanTerm);
		loanInfoe.setLoanStatus("禁用");
		loandetail.setYearIrr(yearIrr);
		loandetail.setLoanInfoEntity(loanInfoe);
		loanInfoe.setMemo(memo);
		loanInfoe.setLoanInfo(loanInfo);
		loanInfoe.setLoanCode(flowNumberService.generateExperienceTheNumber());
//		loanInfoe.setProtocalType(protocalType);
		loanInfoe.setLoanUnit("天");
		loanInfoe.setNewerFlag("体验标");
		
		loanInfoRepository.save(loanInfoe);
		loanDetailInfoRepository.save(loandetail);
		return new ResultVo(true,"添加成功！");
	}
	
	/**
	 * 预览体验标的
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> findByLabel(Map<String, Object> params)
			throws Exception {
		Map<String,Object> map =new HashMap<String, Object>();
		// TODO Administrator 简要描述
		StringBuffer SqlString=new StringBuffer()
		.append("SELECT LOAN.ID  \"id\", ")
		.append("		LOAN.LOAN_TERM \"loanTerm\", ")
		.append("		LOAN.MEMO \"memo\", ")
		.append("		LOAN.LOAN_TITLE \"loanTitle\", ")
		//新加
		.append("		TRUNC(DETAIL.YEAR_IRR*100,2) \"yearIrr\", ")
		.append("		LOAN.LOAN_STATUS \"loanStatus\", ")
		.append("		LOAN.LOAN_INFO \"loanInfo\", ")
		.append("       LOAN.LOAN_CODE \"loanCode\"  ")
//		.append("       LOAN.PROTOCAL_TYPE \"protocalType\" ")
		.append("FROM ")
		.append("		BAO_T_LOAN_INFO LOAN ")
		.append(" 		LEFT OUTER JOIN BAO_T_LOAN_DETAIL_INFO DETAIL ON DETAIL.LOAN_ID= LOAN.ID ")
		.append(" WHERE ")
		.append("		LOAN.ID=? ");
				
		List<Map<String,Object>> list= repositoryUtil.queryForMap(SqlString.toString(), new Object[]{(String)params.get("id")});
		if(null !=list&&list.size()>0){
			map=list.get(0);
		}
		return map;
	}
	/**
	 * 编辑体验标的
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional
	public ResultVo updateLabel(Map<String, Object> params) throws Exception {
		// TODO Administrator 简要描述
		
		List<Object> list=new ArrayList<Object>();
		String id=CommonUtils.emptyToString(params.get("id"));
		String loanTitle=CommonUtils.emptyToString(params.get("loanTitle"));
		Long loanTerm = Long.valueOf(params.get("loanTerm").toString());
		BigDecimal yearIrr=CommonUtils.emptyToDecimal(params.get("yearIrr"));
		//新加
		yearIrr = yearIrr.divide(new BigDecimal(100));
		String loanStatus=CommonUtils.emptyToString(params.get("loanStatus"));
		String loanCode=CommonUtils.emptyToString(params.get("loanCode"));
//		String protocalType=CommonUtils.emptyToString(params.get("protocalType"));
		if(!StringUtils.isEmpty(loanStatus)&&"启用".equals(loanStatus)){
			StringBuffer sbf=new StringBuffer()
			
			.append("SELECT * FROM BAO_T_LOAN_INFO T ")
			.append("WHERE ")
			.append("T.LOAN_STATUS='启用' ");
			List<Map<String,Object>> lis= repositoryUtil.queryForMap(sbf.toString(), null);
			if(lis.size()>=1 && null !=lis){
				return new ResultVo(false,"仅可设置一个启用中的体验标");
			}
			
		}
		String memo=CommonUtils.emptyToString(params.get("memo"));
		String loanInfo=CommonUtils.emptyToString(params.get("loanInfo"));
		
		StringBuffer Sqlbuffer=new StringBuffer()
		.append("update BAO_T_LOAN_INFO LOAN ")
		.append("SET ");
		if(!StringUtils.isEmpty(params.get("loanTitle"))){
			Sqlbuffer.append("LOAN.LOAN_TITLE=?, ");
			list.add(loanTitle);
		}
		if(!StringUtils.isEmpty(params.get("loanTerm"))){
			Sqlbuffer.append("LOAN.LOAN_TERM=?, ");
			list.add(loanTerm);
		}
		if(!StringUtils.isEmpty(params.get("loanStatus"))){
			Sqlbuffer.append("LOAN.LOAN_STATUS=?, ");
			list.add(loanStatus);
		}
		if(!StringUtils.isEmpty(params.get("memo"))){
			Sqlbuffer.append("LOAN.MEMO=?, ");
			list.add(memo);
		}
		if(!StringUtils.isEmpty(params.get("loanInfo"))){
			Sqlbuffer.append("LOAN.LOAN_INFO=?, ");
			list.add(loanInfo);
		}
		if(!StringUtils.isEmpty(params.get("loanCode"))){
			Sqlbuffer.append("LOAN.LOAN_CODE=? ");
			list.add(loanCode);
		}
		//新加
//		if(!StringUtils.isEmpty(params.get("protocalType"))){
//			Sqlbuffer.append("LOAN.PROTOCAL_TYPE=? ");
//			list.add(protocalType);
//		}
		Sqlbuffer.append("WHERE LOAN.ID=? ");
		list.add(id);
		jdbcTemplate.update(Sqlbuffer.toString(), list.toArray());
		if(!StringUtils.isEmpty(params.get("yearIrr"))){
			List<Object> list1=new ArrayList<Object>();
			Sqlbuffer =new StringBuffer()
			.append("UPDATE BAO_T_LOAN_DETAIL_INFO ")
			.append("SET ")
			.append("YEAR_IRR=? ");
			list1.add(yearIrr);
			Sqlbuffer.append("WHERE LOAN_ID=? ");
			list1.add(id);
			jdbcTemplate.update(Sqlbuffer.toString(), list1.toArray());
		}
		return new ResultVo(true);
		
	}
	/**
	 * 启用禁用体验标的
	 * @param params
	 * @return
	 */
	@Override
	public ResultVo enableLabel(Map<String, Object> params) {
		// TODO Administrator 简要描述
		String loanStatus=CommonUtils.emptyToString(params.get("loanStatus"));
		String id=CommonUtils.emptyToString(params.get("id"));
		if(!StringUtils.isEmpty(loanStatus)&&"启用".equals(loanStatus)){
			StringBuffer sbf=new StringBuffer()			
			.append("SELECT * FROM BAO_T_LOAN_INFO T ")
			.append("WHERE ")
			.append("T.LOAN_STATUS='启用' ");
			List<Map<String,Object>> lis= repositoryUtil.queryForMap(sbf.toString(), null);
			if(lis.size()>=1 && null !=lis){
				return new ResultVo(false,"仅可设置一个启用中的体验标");
			}
		}
		List<Object> list=new ArrayList<Object>(); 
		StringBuffer str =new StringBuffer()
		.append("UPDATE BAO_T_LOAN_INFO ")
		.append("SET ")
		.append("LOAN_STATUS=? ");
		list.add(loanStatus);
		str.append("WHERE ID=? ");
		list.add(id);
		jdbcTemplate.update(str.toString(), list.toArray());
		return new ResultVo(true);
	}
}
