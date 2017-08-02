package com.slfinance.shanlincaifu.repository.custom;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CommissionRateEntity;
import com.slfinance.shanlincaifu.entity.CommissionSendBackEntity;

import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by Laurance on 2017/4/20.
 */
public interface CalcCommissionRepositoryCustom {

    // <非等额本息>投资信息
    List<Map<String, Object>> getInitInvestInfo(String preMonth, String currMonth, String firstDayOfPreMonth, String now);

    // <等额本息>投资信息
    List<Map<String, Object>> getInitInvestInfo2(String preMonth, String currMonth, String firstDayOfPreMonth, String now);

    // <不可转让>投资信息
    List<Map<String, Object>> getUnTransferableInvestInfo(String startDate, String endDate);

    // <可转让>投资信息
    List<Map<String, Object>> getTransferableInvestInfo(String preMonth, String startDate, String endDate);

    // 投资总数
    Integer getTotalCountOfInvest(String startDate, String preMonth, String endDate);

    // 分页查询投资信息
    List<Map<String, Object>> getInitInvestInfo3(String startDate, String preMonth, String endDate, Integer begin, Integer end);

    // 查询指定投资的还款信息
    List<Map<String, Object>> getInvestValue(String investId, String currMonth);

    // 删除本月已保存的数据，避免重复保存
    void deleteSavedInCurrentMonth();

    // 保存业绩
    void batchUpdatePerformance(List<Object[]> update);

    // 查询所有提点信息
    List<CommissionRateEntity> getAllCommissionRate();

    // 根据工号查询业务员的所有投资
    List<CommissionSendBackEntity> getInvestInfoByJobNo(String jobNo);

    // 保存最终佣金
    void updateAmount(List<Object[]> list);

    // 查询上个月的最后一笔转让的日期
    List<Map<String, Object>> getMaxTransDatePreMonth(String investId, String preMonth);

    // 查询结算日所在月底所有转让比例之和
    BigDecimal getHoldScaleBeforeTrans(String investId, String lastDay);

    // 查询上个月未转让之前持有的比例
    BigDecimal queryHoldScalePreMonth(String investId, String preMonth);

    // 查询所有业绩信息
    Page<Map<String, Object>> queryPerformanceList(Map<String, Object> param);

    // 批量保存组织架构
    int batchSaveConstruct(List<Map<String, Object>> data) throws SLException;

    // 批量保存提点
    int batchSaveCommissionRate(List<Map<String, Object>> params, String rateMonth) throws SLException;

    // 查询最终发放金额
    Page<Map<String, Object>> queryCommissionAmountList(Map<String, Object> param);

    // 更新级别
    void updateRank(int i);

    // 根据用户身份证号查询用户的工号、级别
    List<Map<String, Object>> getRankingByCredentialsCode(String credentialsCode);

    // 查询本月是否有转让
    List<Map<String, Object>> getTransInfoCurrMonth(String investId, String currMonth);

    // 查询所有提点所属月
    List<Map<String,Object>> getAllRateMonth();

    // 查询该笔投资上个月的所有转让情况
//    List<Map<String,Object>> queryTransInfoPreMonth(String investId, String preMonth);
}
