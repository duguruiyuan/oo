package com.slfinance.shanlincaifu.repository;

import com.slfinance.shanlincaifu.entity.LoanRebateInfoEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * 用于计算业绩，录入借款时需判断是否在本表范围内数据访问接口
 *
 * @author Tool
 * @version $Revision:1.0.0, $Date: 2016-11-29 20:51:29 $
 */
public interface LoanRebateInfoRepository extends PagingAndSortingRepository<LoanRebateInfoEntity, String> {

    LoanRebateInfoEntity findByRepaymentMethodAndLoanTermAndProductTypeAndLoanUnit(String repaymentMethod, Integer loanTerm, String productType, String loanUnit);

    LoanRebateInfoEntity findByRepaymentMethodAndLoanTermAndProductTypeAndLoanUnitAndAwardRate(String repaymentMethod, Integer loanTerm,
                                                                                               String productType, String loanUnit, BigDecimal awardRate);

    @Query(value = " SELECT DISTINCT PRODUCT_TYPE \"loanType\" FROM BAO_T_LOAN_REBATE_INFO WHERE FLAG = ? ", nativeQuery = true)
    List<Map<String, Object>> queryLoanTypeList(String flag);

    @Query(value = " SELECT DISTINCT bank_code \"bankCode\", BANK_NAME \"bankName\" FROM BAO_T_BANK_LIMITED_INFO ORDER BY BANK_CODE ", nativeQuery = true)
    List<Map<String, Object>> queryBankData();
}
