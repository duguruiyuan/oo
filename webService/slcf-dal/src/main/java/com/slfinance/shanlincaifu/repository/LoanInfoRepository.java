package com.slfinance.shanlincaifu.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.LoanInfoEntity;

public interface LoanInfoRepository extends PagingAndSortingRepository<LoanInfoEntity, String> {

	@Query("select M FROM LoanInfoEntity M JOIN M.loanDetailList N"
			+ " where N.creditRightStatus = '正常' ")
	List<LoanInfoEntity> queryLoan();
	
	LoanInfoEntity findByLoanCode(String loanCode);

	/**
	 * 根据状态 查询
	 * @param loanStatus
	 * @return
	 */
	public List<LoanInfoEntity> findByLoanStatus(String loanStatus);
	
	/**
	 * 通过客户ID查询
	 *
	 * @author  wangjf
	 * @date    2016年12月1日 下午8:55:22
	 * @param custId
	 * @return
	 */
	public List<LoanInfoEntity> findByCustId(String custId);

	/**
	 * 获取已募集金额 
	 * @author  liyy
	 * @date    2016年12月21日 下午8:55:22
	 * @param custId
	 * @return
	 */
	@Query(value=" SELECT sum(TRUNC(pi.ALREADY_INVEST_AMOUNT,2)) FROM BAO_T_LOAN_INFO loan, BAO_T_PROJECT_INVEST_INFO pi WHERE pi.LOAN_ID = loan.ID AND loan.LOAN_STATUS = '募集中' ", nativeQuery=true)
	public BigDecimal getAlreadyAmount();

	@Query(value=" SELECT count(1) \"count\" FROM BAO_T_LOAN_INFO WHERE LOAN_STATUS='募集中' and NEWER_FLAG != '新手标' UNION ALL SELECT count(1) \"count\" FROM BAO_T_LOAN_TRANSFER_APPLY WHERE APPLY_STATUS in ('待转让', '部分转让成功') AND CANCEL_STATUS = '未撤销' AND AUDIT_STATUS = '通过' and is_run_auto_invest = 'Y' " , nativeQuery=true)
	List<BigDecimal> isExistsLoan();

	@Query(value=" SELECT count(1)  FROM BAO_T_LOAN_INFO WHERE LOAN_STATUS='募集中'  and NEWER_FLAG != '新手标' and attachment_flag = '已完成'" , nativeQuery=true)
	Integer countLoan();
	
	@Query(value=" select loan.* from BAO_T_LOAN_INFO loan, BAO_T_INVEST_INFO invest where loan.id = invest.loan_id and invest.id = ? " , nativeQuery=true)
	LoanInfoEntity findByLoanInfoByInvestId(String investId);
	
	List<LoanInfoEntity> findByLoanStatusAndAttachmentFlag(String loanStatus,String attachmentFlag);
	
//	@Query(value=" select loan.id " +
//			" from BAO_T_LOAN_INFO loan " +
////			"    , BAO_T_LOAN_DETAIL_INFO ld  " +
//			" WHERE 1=1 " +
////			"   AND ld.YEAR_IRR >= 0.05 " +
////			"   AND ld.YEAR_IRR <= 0.06 " +
////			"   AND ld.Loan_id = loan.id  " +
//			"   AND loan.LOAN_TERM IN ('7', '14' ,'21', '28', '60') " +
//			"   AND loan.LOAN_UNIT = '天' " +
////			"   AND loan.SEAT_TERM = -1 " +
////			"   AND loan.REPAYMENT_METHOD = '到期还本付息' " +
//			"   AND (SELECT instr(value, loan.LOAN_TYPE) FROM COM_T_PARAM WHERE TYPE='shanLinCaiFuSet' AND PARAMETER_NAME='可投产品') > 0 " +
//			"   AND loan.NEWER_FLAG = '普通标' " +
//			"   AND loan.LOAN_STATUS = '募集中' " +
//			" order by loan.create_date " , nativeQuery=true)
//	List<String> findListReserveLoan();
	
//	@Query(value=" select loan.* " +
//				" from BAO_T_LOAN_INFO loan " +
////				"    , BAO_T_LOAN_DETAIL_INFO ld  " +
//				" WHERE 1=1 " +
////				"   AND ld.YEAR_IRR >= 0.05 " +
////				"   AND ld.YEAR_IRR <= 0.06 " +
////				"   AND ld.Loan_id = loan.id  " +
//				"   AND loan.LOAN_TERM IN ('7', '14' ,'21', '28', '60') " +
//				"   AND loan.LOAN_UNIT = '天' " +
////				"   AND loan.SEAT_TERM = -1 " +
////				"   AND loan.REPAYMENT_METHOD = '到期还本付息' " +
//				"   AND (SELECT instr(value, loan.LOAN_TYPE) FROM COM_T_PARAM WHERE TYPE='shanLinCaiFuSet' AND PARAMETER_NAME='可投产品') > 0 " +
//				"   AND loan.NEWER_FLAG = '普通标' " +
//				"   AND loan.LOAN_STATUS = '募集中' " +
//				"   and loan.id = ?  " , nativeQuery=true)
//	LoanInfoEntity findReserveLoan(String loanId);

//	@Query(value=" select loan.* " +
//			" from BAO_T_LOAN_INFO loan " +
//			"    , BAO_T_LOAN_DETAIL_INFO ld  " +
//			" WHERE 1=1 " +
//			"   AND ld.YEAR_IRR >= 0.05 " +
//			"   AND ld.YEAR_IRR <= 0.06 " +
//			"   AND ld.Loan_id = loan.id  " +
//			"   AND loan.LOAN_TERM IN ('7', '14' ,'21', '28', '60') " +
//			"   AND loan.LOAN_UNIT = '天' " +
//			"   AND loan.SEAT_TERM = -1 " +
//			"   AND loan.REPAYMENT_METHOD = '到期还本付息' " +
//			"   AND (SELECT instr(value, loan.LOAN_TYPE) FROM COM_T_PARAM WHERE TYPE='shanLinCaiFuSet' AND PARAMETER_NAME='可投产品') > 0 " +
//			"   AND loan.NEWER_FLAG = '普通标' " +
//			"   AND loan.LOAN_STATUS = '募集中' " +
//			"   and loan.id = ?  ", nativeQuery=true)
//	LoanInfoEntity findInfoToAutoInvest(String loanId);
	
	/***
	 * 根据loanId查询优选项目 标的剩余可投金额
	 * @author  guoyk
	 * @date    2017年06月17日 下午17:13:22
	 * @param   loanId
	 */
	@Query(value= "select TRUNC(loan.LOAN_AMOUNT,2) - nvl(TRUNC(pi.ALREADY_INVEST_AMOUNT,2),0) \"remainAmount\" FROM BAO_T_LOAN_INFO loan LEFT JOIN BAO_T_PROJECT_INVEST_INFO pi ON pi.LOAN_ID=loan.ID  WHERE loan.id = ?",nativeQuery = true)
	public BigDecimal findRemainAmountForDisperseByLoanId(String loanId);
	
	/***
	 * 根据applyId查询转让专区 标的剩余可投金额
	 * @author  guoyk
	 * @date    2017年06月17日 下午17:13:22
	 * @param   applyId
	 */
	@Query(value= "select TRUNC(TRUNC(lta.REMAINDER_TRADE_SCALE * ld.CREDIT_REMAINDER_PRINCIPAL, 2) * lta.REDUCED_SCALE, 2) \"remainAmount\"FROM BAO_T_LOAN_TRANSFER_APPLY lta , BAO_T_WEALTH_HOLD_INFO wh , BAO_T_LOAN_INFO loan , BAO_T_LOAN_DETAIL_INFO ld WHERE ld.LOAN_ID = loan.ID AND loan.ID = wh.LOAN_ID  AND wh.ID = lta.SENDER_HOLD_ID  and lta.id = ?",nativeQuery = true)
	public BigDecimal findRemainAmountForCreditByApplyId(String applyId);

	/**
	 * 根据标的类型和状态查询体验标的
	 * @return
	 */
	@Query("select t from LoanInfoEntity t where t.newerFlag='体验标' and t.loanStatus ='启用'")
	LoanInfoEntity findByNewerFlagAndLoanStatus();
}
