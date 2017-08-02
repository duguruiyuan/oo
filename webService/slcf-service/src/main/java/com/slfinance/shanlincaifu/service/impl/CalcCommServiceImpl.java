package com.slfinance.shanlincaifu.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CommissionRateEntity;
import com.slfinance.shanlincaifu.entity.CommissionSendBackEntity;
import com.slfinance.shanlincaifu.repository.custom.CalcCommissionRepositoryCustom;
import com.slfinance.shanlincaifu.service.CalcCommService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.vo.ResultVo;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Laurance on 2017/4/27.
 */
@Slf4j
@Service(value = "calcCommService")
public class CalcCommServiceImpl implements CalcCommService {

    private static String YYYYMM = "yyyyMM";
    private static String YYYYMMDD = "yyyyMMdd";
    private static String YYYY_MM_DD = "yyyy-MM-dd";
    private static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static String paymentStatus_paid = "已还款";
    private static String invest_mode_trans = "转让";
    private static String repaymentMethod_acpi = "等额本息";
    private static String repaymentMethod_not_acpi = "非等额本息";
    private static String unTrade = "-1";

    @Autowired
    private CalcCommissionRepositoryCustom repository;

    /**
     * ---------------------------------------存续金额---------------------------------
     * 等额本息     本期计息满30天     存续金额 = 还款之后重新计算的金额
     * 本期计息未满30天   存续金额 = 投资金额
     * <p>
     * 非等额本息                     存续金额 = 投资金额
     * <p>
     * ---------------------------------------业绩------------------------------------
     * 流转标业绩 = 存续金额*（上个月持有天数/360）
     * <p>
     * 非流转标业绩 = 存续金额*（标的期限/360）
     * 标的期限 : 一年    --->  标的期限/360 = 1 （即：360/360）
     * 一个月   --->  标的期限/360 = 1/12 （即：30/360）
     * 28天     --->  标的期限/360 = 1/12
     * 天标     --->  标的期限/360 = 天数/360
     */
    @Override
//    public ResultVo calcPerformance(Map<String, Object> param) throws SLException {
//        try {
//            Date sysDate = new Date();
//
//            List<Object[]> update = Lists.newArrayList();
//            List<Object[]> update2 = Lists.newArrayList();
//            String preMonth = DateUtils.formatDate(DateUtils.getAfterMonth(sysDate, -1), YYYYMM);// 上个月
//            Date now = DateUtils.getMonthStartDate(sysDate);// 每个月1号结算
//            Date firstDayOfPreMonth = DateUtils.getMonthStartDate(DateUtils.getAfterMonth(now, -1));// 上个月1号
//            String startDate = "20170401";
//            String endDate = DateUtils.formatDate(now, YYYYMMDD);
//            Object queryDateObj = param.get("currentDate");//
//
//            // <不可转让>投资信息
//            List<Map<String, Object>> initInfo;
//            // <可转让>投资信息
//            List<Map<String, Object>> initInfo2;
//            boolean isConditionQuery = false;
//            if (!CommonUtils.isEmpty(queryDateObj)) {
//                isConditionQuery = true;
//                // 指定查询月的1号
//                String queryDate = CommonUtils.emptyToString(queryDateObj) + "01";
//                // 截止日期为指定查询月的下一个月1号
//                endDate = DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(queryDate, YYYYMMDD), 1), YYYYMMDD);
//                /** 2017-6-1 起始生效日永远都是4月1日 */
//                // 如果指定了截止时间，开始时间就是截止时间的前一个月的1号
////                startDate = queryDate;
//
//                preMonth = CommonUtils.emptyToString(queryDateObj);
//
//                // 查询指定月<不可转让>的投资信息
//                initInfo = repository.getUnTransferableInvestInfo(startDate, endDate);
//                // 查询指定月<可转让>投资信息
//                initInfo2 = repository.getTransferableInvestInfo(preMonth, startDate, endDate);
//            } else {
//                // 查询上个月<不可转让>的投资信息
//                initInfo = repository.getUnTransferableInvestInfo(DateUtils.formatDate(firstDayOfPreMonth, YYYYMMDD), endDate);
//                // 查询上个月<可转让>投资信息
//                initInfo2 = repository.getTransferableInvestInfo(preMonth, startDate, endDate);
//            }
//
//
//
//            /** 1.不可转让 */
//            for (Map<String, Object> map : initInfo) {
//                List<Object> arr = Lists.newArrayList();
//                String investId = CommonUtils.emptyToString(map.get("investId"));
//                String custName = CommonUtils.emptyToString(map.get("custName"));
//                String managerName = CommonUtils.emptyToString(map.get("managerName"));
//                String repaymentMethod = CommonUtils.emptyToString(map.get("repaymentMethod"));
//                BigDecimal holdAmount = CommonUtils.emptyToDecimal(map.get("investAmount"));// 非等额本息的投资金额就是存续金额
//                String loanUnit = CommonUtils.emptyToString(map.get("loanUnit"));
//                int loanTerm = CommonUtils.emptyToInt(map.get("loanTerm"));
//                String seatTerm = CommonUtils.emptyToString(map.get("seatTerm"));
//                String jobNo = CommonUtils.emptyToString(map.get("jobNo"));
//                String effectDateStr = CommonUtils.emptyToString(map.get("effectDate"));
//                Date effectDate = DateUtils.parseDate(effectDateStr, YYYY_MM_DD);
//                String expireDateStr = CommonUtils.emptyToString(map.get("expireDate"));
//                Date expireDate = DateUtils.parseDate(expireDateStr, YYYY_MM_DD);
//                String shop = CommonUtils.emptyToString(map.get("shop"));
//                String transDateStr = CommonUtils.emptyToString(map.get("transDate"));
//                if (!CommonUtils.isEmpty(transDateStr) && DateUtils.parseDate(transDateStr, YYYY_MM_DD_HH_MM_SS).compareTo(now) >= 0) {
//                    // 如果转让日在结算月，重新查询结算月之前月份的最大转让日
//                    List<Map<String, Object>> date = repository.getMaxTransDatePreMonth(investId, preMonth);
//                    if (CommonUtils.isEmpty(date)) {
//                        transDateStr = "";// 如果之前没有转让，则将“转让日期”置空
//                    } else {
//                        if (!CommonUtils.isEmpty(date.get(0).get("maxTransDate"))) {
//                            Date maxTransDate = DateUtils.parseDate(date.get(0).get("maxTransDate").toString(), YYYY_MM_DD_HH_MM_SS);
//                            if (maxTransDate.compareTo(effectDate) > 0) {
//                                // 如果转让日大于生效日，则为当前持有者执行的转让
//                                transDateStr = date.get(0).get("maxTransDate").toString();
//                            } else {
//                                // 如果转让日小于等于生效日，则为上一个持有者执行的转让。将“转让日期”置空
//                                transDateStr = "";
//                            }
//                        } else {
//                            transDateStr = "";
//                        }
//                    }
//                }
//                BigDecimal transScale = CommonUtils.emptyToDecimal(map.get("transScale"));
//                if (CommonUtils.isEmpty(transDateStr)) {
//                    transScale = new BigDecimal(0);
//                }
//
//                // 计算业绩
//                BigDecimal performance = calcUnTransLoanPerformance(holdAmount, loanUnit, loanTerm);
//
//                if (performance.compareTo(new BigDecimal(0)) == 0) {
//                    continue;
//                }
//
//                arr.add(UUID.randomUUID().toString());
//                arr.add(investId);
//                arr.add(custName);
//                arr.add(managerName);
//                arr.add(jobNo);
//                arr.add(holdAmount);
//                arr.add(performance);
//                arr.add(effectDate);
//                arr.add(expireDate);
//                arr.add(shop);
//                if (CommonUtils.isEmpty(transDateStr)) {
//                    arr.add(transDateStr);
//                } else {
//                    arr.add(DateUtils.parseDate(transDateStr, YYYY_MM_DD_HH_MM_SS));
//                }
//                arr.add(transScale.compareTo(new BigDecimal(0)) == 0 ? "" : transScale);
//                arr.add(repaymentMethod);
//                arr.add("不可转让");
//
//                update.add(arr.toArray());
//            }
//
//            /** 2.可转让 */
//            List<String> zero = Lists.newArrayList();
//            for (Map<String, Object> map : initInfo2) {
//                List<Object> arr = Lists.newArrayList();
//                String investId = CommonUtils.emptyToString(map.get("investId"));
//                String custName = CommonUtils.emptyToString(map.get("custName"));
//                String managerName = CommonUtils.emptyToString(map.get("managerName"));
//                String repaymentMethod = CommonUtils.emptyToString(map.get("repaymentMethod"));
//                BigDecimal investAmount = CommonUtils.emptyToDecimal(map.get("investAmount"));
//                String loanUnit = CommonUtils.emptyToString(map.get("loanUnit"));
//                int loanTerm = CommonUtils.emptyToInt(map.get("loanTerm"));
//                String seatTerm = CommonUtils.emptyToString(map.get("seatTerm"));
//                BigDecimal repaymentAmount = CommonUtils.emptyToDecimal(map.get("repaymentAmount"));// 结算日所在月的还款金额
//                BigDecimal remainderPrincipal = CommonUtils.emptyToDecimal(map.get("remainderPrincipal"));// 结算日所在月的剩余待还金额
//                String repaymentStatus = CommonUtils.emptyToString(map.get("repaymentStatus"));// 还款状态（已还款、未还款）
//                BigDecimal holdScale = CommonUtils.emptyToDecimal(map.get("holdScale"));// 持有比例
//                String investMode = CommonUtils.emptyToString(map.get("investMode"));// 加入/转让
//                String repayDateStr = CommonUtils.emptyToString(map.get("repayDate"));// 还款日
//                Date repayDate = null;// 还款日
//                if (!CommonUtils.isEmpty(repayDateStr)) {
//                    repayDate = DateUtils.parseDate(repayDateStr, YYYYMMDD);
//                }
//                String jobNo = CommonUtils.emptyToString(map.get("jobNo"));
//                String effectDateStr = CommonUtils.emptyToString(map.get("effectDate"));
//                Date effectDate = DateUtils.parseDate(effectDateStr, YYYY_MM_DD);
//                String expireDateStr = CommonUtils.emptyToString(map.get("expireDate"));
//                Date expireDate = DateUtils.parseDate(expireDateStr, YYYY_MM_DD);
//                String shop = CommonUtils.emptyToString(map.get("shop"));
//                String transDateStr = CommonUtils.emptyToString(map.get("transDate"));
//                if (!CommonUtils.isEmpty(transDateStr) && DateUtils.parseDate(transDateStr, YYYY_MM_DD_HH_MM_SS).compareTo(now) >= 0) {
//                    // 如果转让日在结算月，重新查询结算月之前月份的最大转让日
//                    List<Map<String, Object>> date = repository.getMaxTransDatePreMonth(investId, preMonth);
//                    if (CommonUtils.isEmpty(date)) {
//                        transDateStr = "";// 如果之前没有转让，则将“转让日期”置空
//                    } else {
//                        if (!CommonUtils.isEmpty(date.get(0).get("maxTransDate"))) {
//                            Date maxTransDate = DateUtils.parseDate(date.get(0).get("maxTransDate").toString(), YYYY_MM_DD_HH_MM_SS);
//                            if (maxTransDate.compareTo(effectDate) > 0) {
//                                // 如果转让日大于生效日，则为当前持有者执行的转让
//                                transDateStr = date.get(0).get("maxTransDate").toString();
//                            } else {
//                                // 如果转让日小于等于生效日，则为上一个持有者执行的转让。将“转让日期”置空
//                                transDateStr = "";
//                            }
//                        } else {
//                            transDateStr = "";
//                        }
//                    }
//                }
//                BigDecimal transScale = CommonUtils.emptyToDecimal(map.get("transScale"));
//                BigDecimal performance;
//                if (repaymentMethod_not_acpi.equals(repaymentMethod)) {
//                    if ("".equals(investId)) {
//                        System.out.println("");
//                    }
//                    if (CommonUtils.isEmpty(transDateStr)) {
//                        transScale = new BigDecimal(0);
//                    }
//                    // 计算业绩
//                    performance = calcPerformanceAmount(transDateStr, investAmount, transScale, firstDayOfPreMonth, effectDate, expireDate, now, loanUnit, loanTerm, seatTerm);
//                } else {
//                    if (CommonUtils.isEmpty(transDateStr) && new BigDecimal(0).compareTo(holdScale) == 0) {
//                        transScale = new BigDecimal(0);
//                        holdScale = CommonUtils.emptyToDecimal(repository.getHoldScaleBeforeTrans(investId, DateUtils.getLastDay(firstDayOfPreMonth, YYYYMMDD)));
//                    }
//
//                    BigDecimal holdAmountBef = new BigDecimal(0);// 还款前存续金额
//                    BigDecimal holdAmountAft = new BigDecimal(0);// 还款后存续金额
//                    if (!CommonUtils.isEmpty(transDateStr)) {
//                        if (new BigDecimal(0).compareTo(holdScale) == 0 && DateUtils.parseDate(transDateStr, YYYY_MM_DD_HH_MM_SS).compareTo(firstDayOfPreMonth) >= 0) {
//                            // 如果上个月全部转让，查询上个月未转让之前持有的比例
//                            if (!isConditionQuery) {
//                                // 如果上个月全部转让，查询上个月未转让之前持有的比例
//                                holdScale = CommonUtils.emptyToDecimal(repository.queryHoldScalePreMonth(investId, preMonth));
//                            } else {
//                                holdScale = CommonUtils.emptyToDecimal(repository.queryHoldScalePreMonth(investId, null));
//                            }
//                        }
//                    }
//                    /**
//                     * 如果生效月是结算日的前一个月，说明是新增的业绩；如果是结算日的前一个月之前，说明是留存的业绩。
//                     *
//                     * 新增的业绩&&购买自转让专区      还款前金额 = 投资金额
//                     *                              还款后金额 = 剩余待还金额*持有比例
//                     *
//                     * 留存的业绩                    还款前金额 = （本期待还金额+剩余待还金额）*持有比例
//                     *                              还款后金额 = 剩余待还金额*持有比例
//                     */
//                    if (CommonUtils.isEmpty(repaymentStatus)) {// 如果没有还款状态，说明上个月是首月，存续金额就是投资金额
//                        holdAmountBef = investAmount;
//                    } else if (paymentStatus_paid.equals(repaymentStatus)) {// 如果已还款，分别计算还款前和还款后的金额
//                        holdAmountBef = ArithUtil.mul(ArithUtil.add(repaymentAmount, remainderPrincipal), holdScale);
//                        holdAmountAft = ArithUtil.mul(remainderPrincipal, holdScale);
//                    } else {// 如果未还款，存续金额为还款前的金额
//                        holdAmountBef = ArithUtil.mul(ArithUtil.add(repaymentAmount, remainderPrincipal), holdScale);
//                    }
//                    // 新增的业绩&&购买自转让专区
//                    if (effectDate.compareTo(firstDayOfPreMonth) >= 0 && effectDate.compareTo(now) < 0 && invest_mode_trans.equals(investMode)) {
//                        holdAmountBef = investAmount;
//                    }
//
//                    // 计算业绩
//                    performance = calcPerformanceAmount2(transDateStr, holdAmountBef, holdAmountAft, transScale,
//                            firstDayOfPreMonth, effectDate, expireDate, now, loanUnit, loanTerm, seatTerm, repayDate);
//                }
//
//                if (performance.compareTo(new BigDecimal(0)) == 0) {
//                	zero.add(investId);
//                    continue;
//                }
//
//                arr.add(UUID.randomUUID().toString());
//                arr.add(investId);
//                arr.add(custName);
//                arr.add(managerName);
//                arr.add(jobNo);
//                arr.add(investAmount);
//                arr.add(performance);
//                arr.add(effectDate);
//                arr.add(expireDate);
//                arr.add(shop);
//                if (CommonUtils.isEmpty(transDateStr)) {
//                    arr.add(transDateStr);
//                } else {
//                    arr.add(DateUtils.parseDate(transDateStr, YYYY_MM_DD_HH_MM_SS));
//                }
//                arr.add(transScale.compareTo(new BigDecimal(0)) == 0 ? "" : transScale);
//                arr.add(repaymentMethod);
//                arr.add("可转让");// 是否可转让
//
//                update2.add(arr.toArray());
//            }
//            savePerformance(update, update2);
//
//            return new ResultVo(true, "业绩计算成功");
//        } catch (Exception e) {
//            log.error("业绩计算失败" + e.getMessage());
//            return new ResultVo(false, "业绩计算失败", e);
//        }
//    }

    public ResultVo calcPerformance(Map<String, Object> param) throws SLException {
//        try {
            Date sysDate = new Date();

            List<Object[]> update = Lists.newArrayList();
            List<Object[]> update2 = Lists.newArrayList();
            String preMonth = DateUtils.formatDate(DateUtils.getAfterMonth(sysDate, -1), YYYYMM);// 上个月
            Date now = DateUtils.getMonthStartDate(sysDate);// 每个月1号结算
            Date firstDayOfPreMonth = DateUtils.getMonthStartDate(DateUtils.getAfterMonth(now, -1));// 上个月1号
            String startDate = "20170401";
            String endDate = DateUtils.formatDate(now, YYYYMMDD);
            String currMonth = DateUtils.formatDate(now, YYYYMM);
            Object queryDateObj = param.get("currentDate");//
            boolean isConditionQuery = false;
            if (!CommonUtils.isEmpty(queryDateObj)) {
                isConditionQuery = true;
                // 指定查询月的1号
                String queryDate = CommonUtils.emptyToString(queryDateObj) + "01";
                // 如果指定查询某月，now为查询月的后一个月1号
                now = DateUtils.getAfterMonth(DateUtils.parseDate(queryDate, YYYYMMDD), 1);
                // 截止日期为指定查询月的下一个月1号
                endDate = DateUtils.formatDate(now, YYYYMMDD);
                /** 2017-6-1 起始生效日永远都是4月1日 */
//                // 如果指定了截止时间，开始时间就是截止时间的前一个月的1号
//                startDate = queryDate;
                preMonth = CommonUtils.emptyToString(queryDateObj);
                currMonth = endDate.substring(0, 6);

                // 如果指定查询某月，firstDayOfPreMonth为查询月的1号
                firstDayOfPreMonth = DateUtils.parseDate(queryDate, YYYYMMDD);

            }
            // <非等额本息>投资信息
            List<Map<String, Object>> initInfo = repository.getInitInvestInfo(preMonth, currMonth, startDate, endDate);
            // <等额本息>投资信息
            List<Map<String, Object>> initInfo2 = repository.getInitInvestInfo2(preMonth, currMonth, startDate, endDate);

            /** 1.非等额本息 */
            if (!CommonUtils.isEmpty(initInfo)) {
                for (Map<String, Object> map : initInfo) {
                    List<Object> arr = Lists.newArrayList();
                    String investId = CommonUtils.emptyToString(map.get("investId"));
                    String custName = CommonUtils.emptyToString(map.get("custName"));
                    String managerName = CommonUtils.emptyToString(map.get("managerName"));
                    BigDecimal holdAmount = CommonUtils.emptyToDecimal(map.get("investAmount"));// 非等额本息的投资金额就是存续金额
                    String loanUnit = CommonUtils.emptyToString(map.get("loanUnit"));
                    int loanTerm = CommonUtils.emptyToInt(map.get("loanTerm"));
                    String seatTerm = CommonUtils.emptyToString(map.get("seatTerm"));
                    String jobNo = CommonUtils.emptyToString(map.get("jobNo"));
                    String effectDateStr = CommonUtils.emptyToString(map.get("effectDate"));
                    Date effectDate = DateUtils.parseDate(effectDateStr, YYYY_MM_DD);
                    String expireDateStr = CommonUtils.emptyToString(map.get("expireDate"));
                    Date expireDate = DateUtils.parseDate(expireDateStr, YYYY_MM_DD);
                    String shop = CommonUtils.emptyToString(map.get("shop"));
                    String transDateStr = CommonUtils.emptyToString(map.get("transDate"));
                    BigDecimal holdScale = CommonUtils.emptyToDecimal(map.get("holdScale"));// 持有比例
                    BigDecimal tradeScalePre = CommonUtils.emptyToDecimal(map.get("tradeScalePre"));// 上个月转让比例（占标的金额的比例）
                    BigDecimal tradeScaleCurr = CommonUtils.emptyToDecimal(map.get("tradeScaleCurr"));// 本月转让比例


                    // 上个月持有比例 = 当前持有比例 + 本月转让比例（本月转让比例可能为0）
                    holdScale = ArithUtil.add(holdScale, tradeScaleCurr);

                    // 上个月转让前的持有比例
                    BigDecimal holdScaleBefTrans = ArithUtil.add(holdScale, tradeScalePre);

                    // 上个转让比例（占投资金额的比例） = 上个月转让比例/上个月转让前的持有比例
                    BigDecimal transScale;
                    if (BigDecimal.ZERO.compareTo(holdScaleBefTrans) == 0) {
                        transScale = BigDecimal.ZERO;
                    } else {
                        transScale = ArithUtil.div(tradeScalePre, holdScaleBefTrans);
                    }

//                    seatTerm = "30";

                    // 计算业绩
                    BigDecimal performance = calcPerformanceAmount(transDateStr, holdAmount, transScale, firstDayOfPreMonth, effectDate, expireDate, now, loanUnit, loanTerm, seatTerm);

                    if (performance.compareTo(new BigDecimal(0)) == 0) {
                        continue;
                    }

                    arr.add(UUID.randomUUID().toString());
                    arr.add(investId);
                    arr.add(custName);
                    arr.add(managerName);
                    arr.add(jobNo);
                    arr.add(holdAmount);
                    arr.add(performance);
                    arr.add(effectDate);
                    arr.add(expireDate);
                    arr.add(shop);
                    if (CommonUtils.isEmpty(transDateStr)) {
                        arr.add(transDateStr);
                    } else {
                        arr.add(DateUtils.parseDate(transDateStr, YYYY_MM_DD_HH_MM_SS));
                    }
                    arr.add(transScale.compareTo(new BigDecimal(0)) == 0 ? "" : transScale);
                    arr.add("非等额本息");// 非等额本息
                    arr.add(unTrade.equals(seatTerm) ? "不可转让" : "可转让");// 是否可转让

                    update.add(arr.toArray());
                }
            }

            /** 2.等额本息 */
            if (!CommonUtils.isEmpty(initInfo2)) {
                for (Map<String, Object> map : initInfo2) {
                    List<Object> arr = Lists.newArrayList();
                    String investId = CommonUtils.emptyToString(map.get("investId"));
                    String custName = CommonUtils.emptyToString(map.get("custName"));
                    String managerName = CommonUtils.emptyToString(map.get("managerName"));
                    BigDecimal investAmount = CommonUtils.emptyToDecimal(map.get("investAmount"));
                    String loanUnit = CommonUtils.emptyToString(map.get("loanUnit"));
                    int loanTerm = CommonUtils.emptyToInt(map.get("loanTerm"));
                    String seatTerm = CommonUtils.emptyToString(map.get("seatTerm"));
                    BigDecimal repaymentAmount = CommonUtils.emptyToDecimal(map.get("repaymentAmount"));// 结算日所在月的还款金额
                    BigDecimal remainderPrincipal = CommonUtils.emptyToDecimal(map.get("remainderPrincipal"));// 结算日所在月的剩余待还金额
                    String repaymentStatus = CommonUtils.emptyToString(map.get("repaymentStatus"));// 还款状态（已还款、未还款）
                    BigDecimal holdScale = CommonUtils.emptyToDecimal(map.get("holdScale"));// 持有比例
                    String investMode = CommonUtils.emptyToString(map.get("investMode"));// 加入/转让
                    String repayDateStr = CommonUtils.emptyToString(map.get("repayDate"));// 还款日
                    Date repayDate = null;// 还款日
                    if (!CommonUtils.isEmpty(repayDateStr)) {
                        repayDate = DateUtils.parseDate(repayDateStr, YYYYMMDD);
                    }
                    String jobNo = CommonUtils.emptyToString(map.get("jobNo"));
                    String effectDateStr = CommonUtils.emptyToString(map.get("effectDate"));
                    Date effectDate = DateUtils.parseDate(effectDateStr, YYYY_MM_DD);
                    String expireDateStr = CommonUtils.emptyToString(map.get("expireDate"));
                    Date expireDate = DateUtils.parseDate(expireDateStr, YYYY_MM_DD);
                    String shop = CommonUtils.emptyToString(map.get("shop"));
                    String transDateStr = CommonUtils.emptyToString(map.get("transDate"));

                    BigDecimal holdAmountBef = new BigDecimal(0);// 还款前存续金额
                    BigDecimal holdAmountAft = new BigDecimal(0);// 还款后存续金额

                    BigDecimal tradeScalePre = CommonUtils.emptyToDecimal(map.get("tradeScalePre"));// 上个月转让比例（占标的金额的比例）
                    BigDecimal tradeScaleCurr = CommonUtils.emptyToDecimal(map.get("tradeScaleCurr"));// 本月转让比例


                    // 持有比例 = 本月转让比例 + 当前持有比例（本月转让比例可能为0）
                    holdScale = ArithUtil.add(tradeScaleCurr, holdScale);

                    // 上个月转让前的持有比例
                    BigDecimal holdScaleBefTrans = ArithUtil.add(holdScale, tradeScalePre);

                    // 上个转让比例（占投资金额的比例） = 上个月转让比例/上个月转让前的持有比例
                    BigDecimal transScale;
                    if (BigDecimal.ZERO.compareTo(holdScaleBefTrans) == 0) {
                        transScale = BigDecimal.ZERO;
                    } else {
                        transScale = ArithUtil.div(tradeScalePre, holdScaleBefTrans);
                    }

//                    seatTerm = "30";

                    boolean isNewTrans = false;
                    if (unTrade.equals(seatTerm)) {// 非流转标的存续金额为投资金额
                        holdAmountBef = investAmount;
                    } else {// 流转标
                        if (CommonUtils.isEmpty(repaymentStatus)) {// 如果没有还款状态，说明上个月是首月，存续金额就是投资金额
                            holdAmountBef = investAmount;
                            isNewTrans = true;
                        } else if (paymentStatus_paid.equals(repaymentStatus)) {// 如果已还款，分别计算还款前和还款后的金额
                            holdAmountBef = ArithUtil.add(repaymentAmount, remainderPrincipal);
                            holdAmountAft = remainderPrincipal;
                        } else {// 如果未还款，存续金额为还款前的金额
                            holdAmountBef = ArithUtil.add(repaymentAmount, remainderPrincipal);
                        }
                        // 新增的业绩&&购买自转让专区
                        if (effectDate.compareTo(firstDayOfPreMonth) >= 0 && effectDate.compareTo(now) < 0 && invest_mode_trans.equals(investMode)) {
                            holdAmountBef = investAmount;
                            isNewTrans = true;
                        }
                    }


                    // 计算业绩
                    BigDecimal performance = calcPerformanceAmount2(transDateStr, holdAmountBef, holdAmountAft, tradeScalePre,
                            firstDayOfPreMonth, effectDate, expireDate, now, loanUnit, loanTerm, seatTerm, repayDate, holdScale, isNewTrans);

                    if (performance.compareTo(new BigDecimal(0)) == 0) {
                        continue;
                    }

                    arr.add(UUID.randomUUID().toString());
                    arr.add(investId);
                    arr.add(custName);
                    arr.add(managerName);
                    arr.add(jobNo);
                    arr.add(investAmount);
                    arr.add(performance);
                    arr.add(effectDate);
                    arr.add(expireDate);
                    arr.add(shop);
                    if (CommonUtils.isEmpty(transDateStr)) {
                        arr.add(transDateStr);
                    } else {
                        arr.add(DateUtils.parseDate(transDateStr, YYYY_MM_DD_HH_MM_SS));
                    }
                    arr.add(transScale.compareTo(new BigDecimal(0)) == 0 ? "" : transScale);
                    arr.add("等额本息");// 等额本息
                    arr.add(unTrade.equals(seatTerm) ? "不可转让" : "可转让");// 是否可转让

                    update2.add(arr.toArray());
                }
            }

            savePerformance(update, update2);

            return new ResultVo(true, "业绩计算成功");
//        } catch (Exception e) {
//            log.error("业绩计算失败" + e.getMessage());
//            return new ResultVo(false, "业绩计算失败", e);
//        }
    }

    // 保存
    @Transactional(readOnly = false, rollbackFor = SLException.class)
    private void savePerformance(List<Object[]> update, List<Object[]> update2) {
        // 删除本月已保存的数据，避免重复保存
        repository.deleteSavedInCurrentMonth();
        // 非等额本息
        if (!CommonUtils.isEmpty(update)) {
            repository.batchUpdatePerformance(update);
        }
        // 等额本息
        if (!CommonUtils.isEmpty(update2)) {
            repository.batchUpdatePerformance(update2);
        }
    }

    /**
     * 计算业绩
     *
     * @param holdAmount         存续金额
     * @param firstDayOfPreMonth 上个月一号
     * @param effectDate         生效日期
     * @param expireDate         到期日期
     * @param now                结算日期
     * @param loanUnit           标的单位（天、月）
     * @param loanTerm           标的期数
     * @param seatTerm           是否流转标
     * @return
     */
//    private BigDecimal calcPerformanceAmount(BigDecimal holdAmount, Date firstDayOfPreMonth, Date effectDate, Date expireDate,
//                                             Date now, String loanUnit, int loanTerm, String seatTerm) {
//        BigDecimal performance = new BigDecimal(0);// 业绩
//        int holdDays = 0;// 上个月持有天数
//
//        if (unTrade.equals(seatTerm)) {// 非流转标
//            performance = calcUnTransLoanPerformance(holdAmount, loanUnit, loanTerm);
//        } else {// 流转标未转让
//            if (CommonUtils.isEmpty(transDateStr)) {//
//                /**
//                 *
//                 */
//                if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
//                    if (expireDate.compareTo(now) >= 0) {// 上个月没到期
//                        performance = ArithUtil.mul(holdAmount, new BigDecimal((double) 30 / 360));
//                    } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                        int days = DateUtils.getDay(expireDate);
//                        // 如果到期日是31号；或者到期日是2月的最后一天，该月的总天数置为30
//                        if (days == 31 || (DateUtils.getMonth(expireDate) == 1 && DateUtils.getDay(effectDate) == DateUtils.getDaysOfMonth(effectDate))) {
//                            days = 30;
//                        }
//                        performance = ArithUtil.mul(holdAmount, new BigDecimal((double) days / 360));
//                    }
//                } else {// 上个月生效
//                    if (expireDate.compareTo(now) >= 0) {// 上个月没到期
//                        if (DateUtils.getMonth(effectDate) == 1 && DateUtils.getDay(effectDate) == DateUtils.getDaysOfMonth(effectDate)) {// 生效月是2月最后一天
//                            holdDays = 1;
//                        } else if (DateUtils.getDay(effectDate) == 30 || DateUtils.getDay(effectDate) == 31) {// 生效月是30日或者31日
//                            holdDays = 1;
//                        } else {
//                            holdDays = 30 - DateUtils.getDay(effectDate) + 1;
//                        }
//                    } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                        holdDays = DateUtils.getDay(expireDate) - DateUtils.getDay(effectDate);
//                    }
//                    performance = ArithUtil.mul(holdAmount, new BigDecimal((double) holdDays / 360));
//                }
//            } else if (transScale.compareTo(new BigDecimal(1)) == 0) {// 全部转让
//                Date transDate = DateUtils.parseDate(transDateStr, YYYY_MM_DD);
//                int differ_trans = DateUtils.datePhaseDiffer(firstDayOfPreMonth, transDate);// 转让日 - 上个月月初
//                if (DateUtils.getMonthStartDate(transDate).compareTo(DateUtils.getMonthStartDate(firstDayOfPreMonth)) == 0) {// 上个月转让
//                    if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
//                        holdDays = differ_trans;// 持有天数 = 转让日 - 上个月月初
//                    } else {// 上个月生效
//                        holdDays = DateUtils.datePhaseDiffer(effectDate, transDate);// 持有天数 = 转让日 - 生效日
//                    }
//                } else if (DateUtils.getMonthStartDate(transDate).compareTo(now) == 0) {// 上个月之后转让（结算日所在月转让）
//                    holdDays = 0;
//                }
//                performance = ArithUtil.mul(holdAmount, new BigDecimal((double) holdDays / 360));
//            } else {// 部分转让
//                Date transDate = DateUtils.parseDate(transDateStr, YYYY_MM_DD);
//
//                Map<String, Integer> holdDayMap = Maps.newHashMap();
//                holdDayMap.put("holdDays_remain", 0);
//                holdDayMap.put("holdDays_trans", 0);
//
//                getHoldDays(effectDate, firstDayOfPreMonth, expireDate, now, holdDayMap, transDate);
//
//                // 剩余金额 = 存续金额*（1-转让比例）
//                BigDecimal holdAmountRemain = ArithUtil.mul(holdAmount, ArithUtil.sub(new BigDecimal(1), transScale));
//                // 转让金额 = 存续金额*转让比例
//                BigDecimal holdAmountTrans = ArithUtil.mul(holdAmount, transScale);
//
//                BigDecimal performance_remain = ArithUtil.mul(holdAmountRemain, new BigDecimal((double) holdDayMap.get("holdDays_remain") / 360));
//                BigDecimal performance_trans = ArithUtil.mul(holdAmountTrans, new BigDecimal((double) holdDayMap.get("holdDays_trans") / 360));
//                performance = ArithUtil.add(performance_remain, performance_trans);
//            }
//        }
//        return performance;
//    }
    /**
     * 计算业绩
     *
     * @param transDateStr       转让日
     * @param holdAmount         存续金额
     * @param transScale         转让比例
     * @param firstDayOfPreMonth 上个月一号
     * @param effectDate         生效日期
     * @param expireDate         到期日期
     * @param now                结算日期
     * @param loanUnit           标的单位（天、月）
     * @param loanTerm           标的期数
     * @param seatTerm           是否流转标
     * @return
     */
    private BigDecimal calcPerformanceAmount(String transDateStr, BigDecimal holdAmount, BigDecimal transScale,
                                             Date firstDayOfPreMonth, Date effectDate, Date expireDate,
                                             Date now, String loanUnit, int loanTerm, String seatTerm) {
        BigDecimal performance = new BigDecimal(0);// 业绩
        int holdDays = 0;// 上个月持有天数

        if (unTrade.equals(seatTerm)) {// 非流转标
            /** 非流转标只在生效月计算一次业绩 */
            if (DateUtils.isInSameMonth(effectDate, DateUtils.getAfterMonth(new Date(), -1))) {
                performance = calcUnTransLoanPerformance(holdAmount, loanUnit, loanTerm);
            }
        } else {// 流转标未转让
            if (CommonUtils.isEmpty(transDateStr)) {//
                /**
                 *
                 */
                if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
                    if (expireDate.compareTo(now) >= 0) {// 上个月没到期
                        performance = ArithUtil.mul(holdAmount, new BigDecimal((double) 30 / 360));
                    } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                        int days = DateUtils.getDay(expireDate);
                        // 如果到期日是31号；或者到期日是2月的最后一天，该月的总天数置为30
                        if (days == 31 || (DateUtils.getMonth(expireDate) == 1 && DateUtils.getDay(effectDate) == DateUtils.getDaysOfMonth(effectDate))) {
                            days = 30;
                        }
                        performance = ArithUtil.mul(holdAmount, new BigDecimal((double) days / 360));
                    }
                } else {// 上个月生效
                    if (expireDate.compareTo(now) >= 0) {// 上个月没到期
                        if (DateUtils.getMonth(effectDate) == 1 && DateUtils.getDay(effectDate) == DateUtils.getDaysOfMonth(effectDate)) {// 生效月是2月最后一天
                            holdDays = 1;
                        } else if (DateUtils.getDay(effectDate) == 30 || DateUtils.getDay(effectDate) == 31) {// 生效月是30日或者31日
                            holdDays = 1;
                        } else {
                            holdDays = 30 - DateUtils.getDay(effectDate) + 1;
                        }
                    } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                        holdDays = DateUtils.getDay(expireDate) - DateUtils.getDay(effectDate);
                    }
                    performance = ArithUtil.mul(holdAmount, new BigDecimal((double) holdDays / 360));
                }
            } else if (transScale.compareTo(new BigDecimal(1)) == 0) {// 全部转让
                Date transDate = DateUtils.parseDate(transDateStr, YYYY_MM_DD);
                int differ_trans = DateUtils.datePhaseDiffer(firstDayOfPreMonth, transDate);// 转让日 - 上个月月初
                if (DateUtils.getMonthStartDate(transDate).compareTo(DateUtils.getMonthStartDate(firstDayOfPreMonth)) == 0) {// 上个月转让
                    if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
                        holdDays = differ_trans;// 持有天数 = 转让日 - 上个月月初
                    } else {// 上个月生效
                        holdDays = DateUtils.datePhaseDiffer(effectDate, transDate);// 持有天数 = 转让日 - 生效日
                    }
                } else if (DateUtils.getMonthStartDate(transDate).compareTo(now) == 0) {// 上个月之后转让（结算日所在月转让）
//                    if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
//                        holdDays = 30;
//                    } else {// 上个月生效
//                        // 生效月是2月，且生效日是2月最后一天
//                        if ((DateUtils.getMonth(effectDate) == 1 && DateUtils.getDay(effectDate) == DateUtils.getDaysOfMonth(effectDate))
//                                // 生效月是30日或者31日
//                                || DateUtils.getDay(effectDate) == 30 || DateUtils.getDay(effectDate) == 31) {
//                            holdDays = 1;
//                        } else {
//                            holdDays = 30 - DateUtils.getDay(effectDate);
//                        }
//                    }
                    holdDays = 0;
                }
                performance = ArithUtil.mul(holdAmount, new BigDecimal((double) holdDays / 360));
            } else {// 部分转让
                Date transDate = DateUtils.parseDate(transDateStr, YYYY_MM_DD);

                Map<String, Integer> holdDayMap = Maps.newHashMap();
                holdDayMap.put("holdDays_remain", 0);
                holdDayMap.put("holdDays_trans", 0);

                getHoldDays(effectDate, firstDayOfPreMonth, expireDate, now, holdDayMap, transDate);

                // 剩余金额 = 存续金额*（1-转让比例）
                BigDecimal holdAmountRemain = ArithUtil.mul(holdAmount, ArithUtil.sub(new BigDecimal(1), transScale));
                // 转让金额 = 存续金额*转让比例
                BigDecimal holdAmountTrans = ArithUtil.mul(holdAmount, transScale);

                BigDecimal performance_remain = ArithUtil.mul(holdAmountRemain, new BigDecimal((double) holdDayMap.get("holdDays_remain") / 360));
                BigDecimal performance_trans = ArithUtil.mul(holdAmountTrans, new BigDecimal((double) holdDayMap.get("holdDays_trans") / 360));
                performance = ArithUtil.add(performance_remain, performance_trans);
            }
        }
        return performance;
    }

    /**
     * 计算非流转标的业绩
     *
     * @param holdAmount 存续金额
     * @param loanUnit   标的单位
     * @param loanTerm   标的期限
     * @return
     */
    private BigDecimal calcUnTransLoanPerformance(BigDecimal holdAmount, String loanUnit, int loanTerm) {
        double y1 = 0d;
        if ("月".equals(loanUnit)) {
            // 月标折年系数 = (标的期限*30)/360
            y1 = ArithUtil.div(ArithUtil.mul(loanTerm, 30), 360);
        } else if ("天".equals(loanUnit)) {
            // 天标折年系数 = 标的期限/360
            if (28 == loanTerm) {
                // 如果标的天数是28天，也当做30天来计算
                y1 = ArithUtil.div((double) 30, 360);
            } else {
                y1 = ArithUtil.div((double) loanTerm, 360);
            }
        }
        // 非流转标业绩 = 存续金额*折年系数
        return ArithUtil.mul(holdAmount, new BigDecimal(y1));
    }

    /**
     * 计算业绩
     *
     * @param transDateStr       转让日
     * @param holdAmountBef      还款前的存续金额
     * @param holdAmountAft      还款后的存续金额
     * @param transScale         转让比例
     * @param firstDayOfPreMonth 上个月一号
     * @param effectDate         生效日期
     * @param expireDate         到期日期
     * @param now                结算日期
     * @param loanUnit           标的单位（天、月）
     * @param loanTerm           标的期数
     * @param seatTerm           是否流转标
     * @param repayDate          还款日
     * @param holdScale          持有比例
     * @param isNewTrans         是否首月且购买自转让专区
     * @return
     */
    private BigDecimal calcPerformanceAmount2(String transDateStr, BigDecimal holdAmountBef, BigDecimal holdAmountAft, BigDecimal transScale,
                                              Date firstDayOfPreMonth, Date effectDate, Date expireDate,
                                              Date now, String loanUnit, int loanTerm, String seatTerm, Date repayDate, BigDecimal holdScale, boolean isNewTrans) {
        BigDecimal performance = new BigDecimal(0);// 业绩
        if (unTrade.equals(seatTerm)) {// 非流转标
            if (DateUtils.isInSameMonth(effectDate, DateUtils.getAfterMonth(new Date(), -1))) {
                performance = calcUnTransLoanPerformance(holdAmountBef, loanUnit, loanTerm);
            }
        } else {// 流转标
            BigDecimal performanceBef = new BigDecimal(0);// 还款前业绩
            BigDecimal performanceAft = new BigDecimal(0);// 还款后业绩
            if (CommonUtils.isEmpty(transDateStr)) {// 未转让
//                if (null == repayDate) {
                if (isNewTrans) {
                    if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
                        /** 此情况不会出现 */
                        if (expireDate.compareTo(now) >= 0) {// 上个月没到期
                            performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) 30 / 360));
                        } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                            performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) DateUtils.datePhaseDiffer(firstDayOfPreMonth, expireDate) / 360));
                        }
                    } else {// 上个月生效
                        if (expireDate.compareTo(now) >= 0) {// 上个月没到期
                            int effectDay = DateUtils.getDay(effectDate);
                            int days = 30 - effectDay;
                            if (days <= 0) {
                                days = 1;
                            } else {
                                days = days + 1;
                            }
                            performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) days / 360));
                        } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                            /** 此情况不会出现 */
                            performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) DateUtils.datePhaseDiffer(effectDate, expireDate) / 360));
                        }
                    }
                } else {
                    if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
                        if (expireDate.compareTo(now) >= 0) {// 上个月没到期
                            // 例：10号还款，还款前10000，还款后9000
                            // performanceBef = 10000*(10-1)/360
                            // performanceAft = 9000*(30-10)/360
                            performanceBef = ArithUtil.mul(ArithUtil.mul(holdAmountBef, holdScale), new BigDecimal((double) (DateUtils.getDay(repayDate) - 1) / 360));
                            int days = 30 - DateUtils.getDay(repayDate);
                            if (days <= 0) {
                                days = 1;
                            } else {
                                days = days + 1;
                            }
                            performanceAft = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) days / 360));
                            performance = ArithUtil.add(performanceBef, performanceAft);
                        } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                            if (expireDate.compareTo(repayDate) > 0) {// 到期日大于还款日
                                BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, holdScale), new BigDecimal((double) (DateUtils.getDay(repayDate) - 1) / 360));
                                BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) (DateUtils.getDay(expireDate) - DateUtils.getDay(repayDate)) / 360));
                                performance = ArithUtil.add(performance1, performance2);
                            } else {// 到期日小于还款日
                                performance = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) (DateUtils.getDay(expireDate) - 1) / 360));
                            }
                        }
                    } else {// 上个月生效
                        // 生效日与还款日同一个月
                        if (DateUtils.getMonthStartDate(effectDate).compareTo(firstDayOfPreMonth) == 0) {
                            int effectDay = DateUtils.getDay(effectDate);
                            int expireDay = DateUtils.getDay(expireDate);
//                            if (null == repayDate) {
                            if (isNewTrans) {
                                if (expireDate.compareTo(now) >= 0) {// 上个月没到期
                                    // 生效日~月底
                                    int days = 30 - effectDay;
                                    if (days <= 0) {
                                        days = 1;
                                    } else {
                                        days = days + 1;
                                    }
                                    performance = ArithUtil.mul(holdAmountAft, new BigDecimal((double) days / 360));
//                                    performance = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) days / 360));
                                } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                                    performance = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) (expireDay - effectDay) / 360));
                                    performance = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (expireDay - effectDay) / 360));
                                }
                            } else {
                                int repayDay = DateUtils.getDay(repayDate);
                                if (expireDate.compareTo(now) >= 0) {// 上个月没到期
                                    if (repayDate.compareTo(effectDate) < 0) {// 还款日 < 生效日
                                        // 生效日~月底
                                        int days = 30 - effectDay;
                                        if (days <= 0) {
                                            days = 1;
                                        } else {
                                            days = days + 1;
                                        }
                                        performance = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) days / 360));
                                    } else if (repayDate.compareTo(effectDate) > 0) {// 还款日 > 生效日
                                        // 生效日~还款日
                                        BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, holdScale), new BigDecimal((double) (repayDay - effectDay) / 360));
                                        // 还款日~月底
                                        int days = 30 - repayDay;
                                        if (days <= 0) {
                                            days = 1;
                                        } else {
                                            days = days + 1;
                                        }
                                        BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) days / 360));
                                        performance = ArithUtil.add(performance1, performance2);
                                    } else {// 还款日 = 生效日
                                        int days = 30 - effectDay;
                                        if (days <= 0) {
                                            days = 1;
                                        } else {
                                            days = days + 1;
                                        }
                                        performance = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) days / 360));
//                                        if (isNewTrans) {
//                                            int days = 30 - effectDay;
//                                            if (days <= 0) {
//                                                days = 1;
//                                                performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) days / 360));
//                                            } else {
//                                                performance = ArithUtil.add(
//                                                        ArithUtil.mul(holdAmountBef, new BigDecimal((double) 1 / 360)),
//                                                        ArithUtil.mul(holdAmountAft, new BigDecimal((double) days / 360)));
//                                            }
//
//                                        } else {
//                                            int days = 30 - effectDay;
//                                            if (days <= 0) {
//                                                days = 1;
//                                            } else {
//                                                days = days + 1;
//                                            }
//                                            performance = ArithUtil.mul(holdAmountAft, new BigDecimal((double) days / 360));
//                                        }
                                    }
                                } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                                    if (repayDate.compareTo(effectDate) < 0) {// 还款日 < 生效日
                                        if (effectDate.compareTo(expireDate) < 0) {// 生效日 < 到期日
                                            // 生效日~到期日
                                            performance = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) (repayDay - effectDay + 1) / 360));
                                        }
                                    } else if (repayDate.compareTo(effectDate) > 0) {// 还款日 > 生效日
                                        if (repayDate.compareTo(expireDate) == 0) {
                                            // 生效日~还款日
                                            performance = ArithUtil.mul(ArithUtil.mul(holdAmountBef, holdScale), new BigDecimal((double) (repayDay - effectDay) / 360));
                                        }// 不存在还款日与到期日同月不同日（最后一期的还款日就是到期日）
                                    } else {// 还款日 = 生效日
                                        if (effectDate.compareTo(expireDate) < 0) {// 生效日 < 到期日
                                            // 生效日~还款日
                                            performance = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) (repayDay - effectDay) / 360));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (transScale.compareTo(new BigDecimal(1)) == 0) {// 全部转让
                Date transDate = DateUtils.parseDate(transDateStr, YYYY_MM_DD);
                int transDay = DateUtils.getDay(transDate);
                int expireDay = DateUtils.getDay(expireDate);
                int repayDay;
//                if (null == repayDate) {
                if (isNewTrans) {
                    if (transDate.compareTo(now) < 0) {// 结算日之前转让
                        if (transDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月转让
                            if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
                                // 因为全部转让，所以不用判断到期日
                                performance = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (transDay - 1) / 360));
                            } else {// 上个月生效
                                // 业绩 = 10000*(转让日-生效日)/360
                                performance = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (DateUtils.getDay(transDate) - DateUtils.getDay(effectDate)) / 360));
                            }
                        } else {// 上个月之前转让（结算日所在月转让）
                            // 上个月之前全部转让，存续金额为0
                        }
                    }
                } else {
                    repayDay = DateUtils.getDay(repayDate);
                    if (transDate.compareTo(now) < 0) {// 结算日之前转让
                        if (transDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月转让
                            if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
                                if (expireDate.compareTo(now) >= 0) {// 上个月没到期
                                    if (transDay > repayDay) {// 转让日 > 还款日
                                        // 例：10号还款，还款前10000，还款后9000，15号转让100%
                                        //  10000*(10-1)/360
                                        //  9000*(15-10+1)/360
                                        // 1号-10号
                                        BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, transScale), new BigDecimal((double) (repayDay - 1) / 360));
                                        // 10号-15号
                                        BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, transScale), new BigDecimal((double) (transDay - repayDay) / 360));

                                        performance = ArithUtil.add(performance1, performance2);
                                    } else if (transDay == repayDay) {
                                        // 1号-10号
                                        performance = ArithUtil.mul(ArithUtil.mul(holdAmountBef, transScale), new BigDecimal((double) (repayDay - 1) / 360));
                                    } else {
                                        performance = ArithUtil.mul(ArithUtil.mul(holdAmountBef, transScale), new BigDecimal((double) (transDay - 1) / 360));
                                    }
                                } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                                    if (transDay > repayDay) {// 转让日在还款日之后
                                        if (expireDate.compareTo(transDate) >= 0) {// 还款日 < 转让日 <= 到期日
                                            // 1号-10号
                                            BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, transScale), new BigDecimal((double) (repayDay - 1) / 360));
                                            // 10号-15号
                                            BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, transScale), new BigDecimal((double) (transDay - repayDay) / 360));

                                            performance = ArithUtil.add(performance1, performance2);
                                        } else if (expireDate.compareTo(repayDate) >= 0 && expireDate.compareTo(transDate) < 0) {//  还款日 <= 到期日 < 转让日
                                            // 1号-10号
                                            BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, transScale), new BigDecimal((double) (repayDay - 1) / 360));
                                            // 10号-到期日
                                            BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, transScale), new BigDecimal((double) (expireDay - repayDay) / 360));

                                            performance = ArithUtil.add(performance1, performance2);
                                        } else {//  到期日 <= 还款日
                                            performance = ArithUtil.mul(ArithUtil.mul(holdAmountBef, transScale), new BigDecimal((double) (expireDay - 1) / 360));
                                        }
                                    } else if (transDay == repayDay) {// 转让日与还款日同一天
                                        if (expireDate.compareTo(transDate) > 0) {// 还款日 = 转让日 <= 到期日
                                            // 1号-转让日(还款日)
                                            performance = ArithUtil.mul(ArithUtil.mul(holdAmountBef, transScale), new BigDecimal((double) (repayDay - 1) / 360));
                                        } else {// 还款日 = 转让日 = 到期日
                                            performance = ArithUtil.mul(ArithUtil.mul(holdAmountBef, transScale), new BigDecimal((double) (expireDay - 1) / 360));
                                        }
                                    } else {// 转让日在还款日之前
                                        if (expireDate.compareTo(transDate) < 0) {// 到期日 < 转让日
                                            // 1号-到期日
                                            /** 不会出现 */
                                            performance = ArithUtil.mul(ArithUtil.mul(holdAmountBef, transScale), new BigDecimal((double) (expireDay - 1) / 360));
                                        } else {// 到期日 >= 转让日
                                            performance = ArithUtil.mul(ArithUtil.mul(holdAmountBef, transScale), new BigDecimal((double) (transDay - 1) / 360));
                                        }
                                    }
                                }
                            } else {// 上个月生效
                                // 业绩 = 10000*(转让日-生效日)/360
                                performance = ArithUtil.mul(ArithUtil.mul(holdAmountBef, transScale), new BigDecimal((double) (DateUtils.getDay(transDate) - DateUtils.getDay(effectDate)) / 360));
                            }
                        } else {// 上个月之前转让（结算日所在月转让）
                            // 上个月之前全部转让，存续金额为0
                        }
                    }
                }

            } else {// 部分转让
                Date transDate = DateUtils.parseDate(transDateStr, YYYY_MM_DD);
                int transDay = DateUtils.getDay(transDate) == 31 ? 30 : DateUtils.getDay(transDate);
                int effectDay = DateUtils.getDay(effectDate);
                int expireDay = DateUtils.getDay(expireDate);
//                if (null == repayDate) {
                if (isNewTrans) {
                    // 剩余金额 = 投资金额*（1-转让比例）
                    BigDecimal holdAmountRemain = ArithUtil.mul(holdAmountBef, ArithUtil.sub(new BigDecimal(1), transScale));
                    // 转让金额 = 投资金额*转让比例
                    BigDecimal holdAmountTrans = ArithUtil.mul(holdAmountBef, transScale);

                    if (transDate.compareTo(now) < 0) {// 上个月转让
                        if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
                            if (expireDate.compareTo(now) >= 0) {// 上个月没到期
                                // 转让前 = 还款前金额*(持有比例+转让比例)*(转让日-月初)/360
//                                BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (transDay - 1) / 360));
                                BigDecimal performance1 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (transDay - 1) / 360));
                                // 转让后 = 还款前金额*(持有比例)*(30-转让日)/360
//                                BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, holdScale), new BigDecimal((double) (30 - transDay) / 360));
                                BigDecimal performance2 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (30 - transDay) / 360));

                                performance = ArithUtil.add(performance1, performance2);
                            } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                                // 转让前 = 还款前金额*(持有比例+转让比例)*(转让日-月初)/360
//                                BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (transDay - 1) / 360));
                                BigDecimal performance1 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (transDay - 1) / 360));
                                // 转让后 = 还款前金额*(持有比例)*(到期日-转让日)/360
//                                BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, holdScale), new BigDecimal((double) (expireDay - transDay) / 360));
                                BigDecimal performance2 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (expireDay - transDay) / 360));

                                performance = ArithUtil.add(performance1, performance2);
                            }
                        } else {// 上个月生效
                            if (expireDate.compareTo(now) >= 0) {// 上个月没到期
                                // 转让前 = 还款前金额*(持有比例+转让比例)*(转让日-生效日)/360
//                                BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (transDay - effectDay) / 360));
                                BigDecimal performance1 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (transDay - effectDay) / 360));
                                // 转让后 = 还款前金额*(持有比例)*(30-转让日)/360
                                int days = 30 - effectDay;
                                if (days <= 0) {
                                    days = 1;
                                } else {
                                    days = days + 1;
                                }
//                                BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, holdScale), new BigDecimal((double) days / 360));
                                BigDecimal performance2 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) days / 360));

                                performance = ArithUtil.add(performance1, performance2);
                            } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                                /** 不会出现此情况 */
                                // 剩余
                                BigDecimal performance1 = ArithUtil.mul(holdAmountRemain, new BigDecimal((double) (expireDay - effectDay) / 360));
                                // 转让
                                BigDecimal performance2 = ArithUtil.mul(holdAmountTrans, new BigDecimal((double) (transDay - effectDay) / 360));
                                performance = ArithUtil.add(performance1, performance2);
                            }
                        }
                    }
                } else {
                    int repayDay = DateUtils.getDay(repayDate);
                    // 还款前剩余金额 = 还款前存续金额*（1-转让比例）
                    BigDecimal holdAmountRemainBef = ArithUtil.mul(holdAmountBef, ArithUtil.sub(new BigDecimal(1), transScale));
                    // 还款后剩余金额 = 还款后存续金额*（1-转让比例）
                    BigDecimal holdAmountRemainAft = ArithUtil.mul(holdAmountAft, ArithUtil.sub(new BigDecimal(1), transScale));

                    if (transDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前转让，说明上个月没有转让，只计算还款前和还款后
                        if (expireDate.compareTo(now) >= 0) {// 上个月没到期
                            // 例：10号还款，还款前10000，还款后9000
                            //  10000*(10-1)/360
                            //  9000*(15-10+1)/360
                            // 1号-10号
                            BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, holdScale), new BigDecimal((double) (repayDay - 1) / 360));
                            // 10号-月底
                            int days = 30 - repayDay;
                            if (days <= 0) {
                                days = 1;
                            } else {
                                days = days + 1;
                            }
                            BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) days / 360));

                            performance = ArithUtil.add(performance1, performance2);
                        } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                            // 1号-10号
                            BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, holdScale), new BigDecimal((double) (repayDay - 1) / 360));
                            // 10号-到期日
                            BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) (expireDay - repayDay) / 360));

                            performance = ArithUtil.add(performance1, performance2);
                        }
                    } else if (transDate.compareTo(now) < 0) {// 上个月转让
                        if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
                            if (expireDate.compareTo(now) >= 0) {// 上个月没到期
                                if (transDay > repayDay) {// 转让日在还款日之后
                                    // 例：10号还款，还款前10000，还款后9000，15号转让60%
                                    //  10000*(10-1)/360
                                    //  9000*(15-10+1)/360
                                    //  9000*40%*(30-15)/360
                                    // 1号-10号
                                    BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (repayDay - 1) / 360));
                                    // 10号-15号（10号当天也是9000，所以要+1）
                                    BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (transDay - repayDay) / 360));
                                    // 15号-月底
                                    int days = 30 - transDay;
                                    if (days <= 0) {
                                        days = 1;
                                    } else {
                                        days = days + 1;
                                    }
                                    BigDecimal performance3 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) days / 360));

                                    performance = ArithUtil.add(ArithUtil.add(performance1, performance2), performance3);
                                } else if (transDay == repayDay) {// 转让日与还款日同一天
                                    // 例：10号还款，还款前10000，还款后9000，10号转让60%
                                    // 10000*(10-1)/360
                                    // 9000*40%*(30-10+1)/360
                                    // 1号-10号
                                    BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (repayDay - 1) / 360));
                                    // 10号-月底
                                    int days = 30 - repayDay;
                                    if (days <= 0) {
                                        days = 1;
                                    } else {
                                        days = days + 1;
                                    }
                                    BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) days / 360));

                                    performance = ArithUtil.add(performance1, performance2);
                                } else {// 转让日在还款日之前
                                    // 例：10号转让，还款前10000，还款后9000，15号还款
                                    //  10000*(10-1)/360
                                    //  10000*40%*(15-10+1)/360
                                    //  9000*40%*(30-15)/360
                                    // 1号-10号
                                    BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (transDay - 1) / 360));
                                    // 10号-15号
                                    BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, holdScale), new BigDecimal((double) (repayDay - transDay + 1) / 360));
                                    // 15号-月底
                                    int days = 30 - repayDay;
                                    if (days <= 0) {
                                        days = 1;
                                    }
                                    BigDecimal performance3 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) days / 360));

                                    performance = ArithUtil.add(ArithUtil.add(performance1, performance2), performance3);
                                }
                            } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                                if (transDay > repayDay) {// 转让日在还款日之后
                                    if (expireDate.compareTo(transDate) >= 0) {// 还款日 < 转让日 <= 到期日
                                        // 1号-10号
                                        BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (repayDay - 1) / 360));
                                        // 10号-15号（10号当天也是9000，所以要+1）
                                        BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (transDay - repayDay) / 360));
                                        // 15号-到期日
                                        BigDecimal performance3 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, holdScale), new BigDecimal((double) (expireDay - transDay) / 360));

                                        performance = ArithUtil.add(ArithUtil.add(performance1, performance2), performance3);
                                    } else if (expireDate.compareTo(repayDate) >= 0 && expireDate.compareTo(transDate) < 0) {//  还款日 <= 到期日 < 转让日
                                        /** 不会出现 */
                                        // 1号-还款日
                                        BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (repayDay - 1) / 360));
                                        // 10号-到期日
                                        BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (expireDay - repayDay + 1) / 360));

                                        performance = ArithUtil.add(performance1, performance2);
                                    } else {//  到期日 <= 还款日
                                        /** 不会出现 */
                                        performance = ArithUtil.mul(ArithUtil.mul(holdAmountBef, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (expireDay - 1) / 360));
                                    }
                                } else if (transDay == repayDay) {// 转让日与还款日同一天
                                    if (expireDate.compareTo(transDate) > 0) {// 还款日 = 转让日 <= 到期日
                                        // 1号-还款日
                                        BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (repayDay - 1) / 360));
                                        // 还款日-到期日
                                        BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) (expireDay - repayDay) / 360));

                                        performance = ArithUtil.add(performance1, performance2);
                                    } else {// 还款日 = 转让日 = 到期日
                                        performance = ArithUtil.mul(ArithUtil.mul(holdAmountBef, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (expireDay - 1) / 360));
                                    }
                                } else {// 转让日在还款日之前
                                    // 1号-还款日
                                    performance = ArithUtil.mul(ArithUtil.mul(holdAmountBef, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (transDay - 1) / 360));
                                }
                            }
                        } else {
                            if (DateUtils.getMonthStartDate(effectDate).compareTo(firstDayOfPreMonth) == 0) {// 上个月生效&上个月转让
                                // 生效日与还款日同一个月，说明购买的是债权转让 存续金额 = 10000*(转让日-生效日)/360
                                if (repayDate.compareTo(effectDate) < 0) {// 还款日 < 生效日
                                    if (expireDate.compareTo(now) < 0 && expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                                        if (effectDate.compareTo(transDate) > 0) {// 还款日 < 生效日 < 转让日
                                            // 生效日~转让日
                                            BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (transDay - effectDay) / 360));
                                            // 转让日~到期日
                                            int days = expireDay - transDay;
                                            if (days <= 0) {
                                                days = 1;
                                            }
                                            BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) days / 360));
                                            performance = ArithUtil.add(performance1, performance2);
                                        }
                                    } else if (expireDate.compareTo(now) >= 0) {// 上个月之后到期
                                        if (effectDate.compareTo(transDate) > 0) {// 还款日 < 生效日 < 转让日
                                            // 生效日~转让日
                                            BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (transDay - effectDay) / 360));
                                            // 转让日~月底
                                            int days = 30 - transDay;
                                            if (days <= 0) {
                                                days = 1;
                                            }
                                            BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) days / 360));
                                            performance = ArithUtil.add(performance1, performance2);
                                        }
                                    }
                                } else if (repayDate.compareTo(effectDate) > 0) {// 还款日 > 生效日
                                    if (expireDate.compareTo(now) < 0 && expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                                        // 生效日~还款日
                                        BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (repayDay - effectDay) / 360));
                                        // 还款日~转让日
                                        BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (transDay - repayDay) / 360));
                                        // 转让日~月底
                                        int days = expireDay - transDay;
                                        if (days <= 0) {
                                            days = 1;
                                        }
                                        BigDecimal performance3 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) days / 360));
                                        performance = ArithUtil.add(ArithUtil.add(performance1, performance2), performance3);
                                    } else if (expireDate.compareTo(now) >= 0) {// 上个月之后到期
                                        // 生效日~还款日
                                        BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountBef, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (repayDay - effectDay) / 360));
                                        // 还款日~转让日
                                        BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (transDay - repayDay) / 360));
                                        // 转让日~月底
                                        int days = 30 - transDay;
                                        if (days <= 0) {
                                            days = 1;
                                        }
                                        BigDecimal performance3 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) days / 360));
                                        performance = ArithUtil.add(ArithUtil.add(performance1, performance2), performance3);
                                    }

                                } else {// 还款日 = 生效日
                                    if (expireDate.compareTo(now) < 0 && expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                                        // 还款日~转让日
                                        BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (transDay - effectDay) / 360));
                                        // 转让日~到期日
                                        int days = expireDay - transDay;
                                        if (days <= 0) {
                                            days = 1;
                                        }
                                        BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) days / 360));
                                        performance = ArithUtil.add(performance1, performance2);
                                    } else if (expireDate.compareTo(now) >= 0) {// 上个月之后到期
                                        // 还款日~转让日
                                        BigDecimal performance1 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, ArithUtil.add(holdScale, transScale)), new BigDecimal((double) (transDay - effectDay) / 360));
                                        // 转让日~月底
                                        int days = 30 - transDay;
                                        if (days <= 0) {
                                            days = 1;
                                        }
                                        BigDecimal performance2 = ArithUtil.mul(ArithUtil.mul(holdAmountAft, holdScale), new BigDecimal((double) days / 360));
                                        performance = ArithUtil.add(performance1, performance2);
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        return performance;
    }
//    private BigDecimal calcPerformanceAmount2(String transDateStr, BigDecimal holdAmountBef, BigDecimal holdAmountAft, BigDecimal transScale,
//                                              Date firstDayOfPreMonth, Date effectDate, Date expireDate,
//                                              Date now, String loanUnit, int loanTerm, String seatTerm, Date repayDate) {
//        BigDecimal performance = new BigDecimal(0);// 业绩
//        if (unTrade.equals(seatTerm)) {// 非流转标
//            if (DateUtils.isInSameMonth(effectDate, DateUtils.getAfterMonth(new Date(), -1))) {
//                performance = calcUnTransLoanPerformance(holdAmountBef, loanUnit, loanTerm);
//            }
//        } else {// 流转标
//            BigDecimal performanceBef = new BigDecimal(0);// 还款前业绩
//            BigDecimal performanceAft = new BigDecimal(0);// 还款后业绩
//            if (CommonUtils.isEmpty(transDateStr)) {// 未转让
//                if (null == repayDate) {
//                    if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
//                        /** 此情况不会出现 */
//                        if (expireDate.compareTo(now) >= 0) {// 上个月没到期
//                            performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) 30 / 360));
//                        } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                            performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) DateUtils.datePhaseDiffer(firstDayOfPreMonth, expireDate) / 360));
//                        }
//                    } else {// 上个月生效
//                        if (expireDate.compareTo(now) >= 0) {// 上个月没到期
//                            int effectDay = DateUtils.getDay(effectDate);
//                            int days = 30 - effectDay;
//                            if (days <= 0) {
//                                days = 1;
//                            } else {
//                                days = days + 1;
//                            }
//                            performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) days / 360));
//                        } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                            /** 此情况不会出现 */
//                            performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) DateUtils.datePhaseDiffer(effectDate, expireDate) / 360));
//                        }
//                    }
//                } else {
//                    if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
//                        if (expireDate.compareTo(now) >= 0) {// 上个月没到期
//                            // 例：10号还款，还款前10000，还款后9000
//                            // performanceBef = 10000*(10-1)/360
//                            // performanceAft = 9000*(30-10)/360
//                            performanceBef = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (DateUtils.getDay(repayDate) - 1) / 360));
//                            int days = 30 - DateUtils.getDay(repayDate);
//                            if (days <= 0) {
//                                days = 1;
//                            } else {
//                                days = days + 1;
//                            }
//                            performanceAft = ArithUtil.mul(holdAmountAft, new BigDecimal((double) days / 360));
//                            performance = ArithUtil.add(performanceBef, performanceAft);
//                        } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                            if (expireDate.compareTo(repayDate) > 0) {// 到期日大于还款日
//                                BigDecimal performance1 = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (DateUtils.getDay(repayDate) - 1) / 360));
//                                BigDecimal performance2 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (DateUtils.getDay(expireDate) - DateUtils.getDay(repayDate)) / 360));
//                                performance = ArithUtil.add(performance1, performance2);
//                            } else {// 到期日小于还款日
//                                performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (DateUtils.getDay(expireDate) - 1) / 360));
//                            }
//                        }
//                    } else {// 上个月生效
//                        // 生效日与还款日同一个月
//                        if (DateUtils.getMonthStartDate(effectDate).compareTo(firstDayOfPreMonth) == 0) {
//                            int effectDay = DateUtils.getDay(effectDate);
//                            int expireDay = DateUtils.getDay(expireDate);
//                            if (null == repayDate) {
//                                if (expireDate.compareTo(now) >= 0) {// 上个月没到期
//                                    // 生效日~月底
//                                    int days = 30 - effectDay;
//                                    if (days <= 0) {
//                                        days = 1;
//                                    } else {
//                                        days = days + 1;
//                                    }
//                                    performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) days / 360));
//                                } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                                    performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (expireDay - effectDay) / 360));
//                                }
//                            } else {
//                                int repayDay = DateUtils.getDay(repayDate);
//                                if (expireDate.compareTo(now) >= 0) {// 上个月没到期
//                                    if (repayDate.compareTo(effectDate) < 0) {// 还款日 < 生效日
//                                        // 生效日~月底
//                                        int days = 30 - effectDay;
//                                        if (days <= 0) {
//                                            days = 1;
//                                        } else {
//                                            days = days + 1;
//                                        }
//                                        performance = ArithUtil.mul(holdAmountAft, new BigDecimal((double) days / 360));
//                                    } else if (repayDate.compareTo(effectDate) > 0) {// 还款日 > 生效日
//                                        // 生效日~还款日
//                                        BigDecimal performance1 = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (repayDay - effectDay) / 360));
//                                        // 还款日~月底
//                                        int days = 30 - repayDay;
//                                        if (days <= 0) {
//                                            days = 1;
//                                        } else {
//                                            days = days + 1;
//                                        }
//                                        BigDecimal performance2 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) days / 360));
//                                        performance = ArithUtil.add(performance1, performance2);
//                                    } else {// 还款日 = 生效日
//                                        int days = 30 - effectDay;
//                                        if (days <= 0) {
//                                            days = 1;
//                                        } else {
//                                            days = days + 1;
//                                        }
//                                        performance = ArithUtil.mul(holdAmountAft, new BigDecimal((double) days / 360));
////                                        if (isNewTrans) {
////                                            int days = 30 - effectDay;
////                                            if (days <= 0) {
////                                                days = 1;
////                                                performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) days / 360));
////                                            } else {
////                                                performance = ArithUtil.add(
////                                                        ArithUtil.mul(holdAmountBef, new BigDecimal((double) 1 / 360)),
////                                                        ArithUtil.mul(holdAmountAft, new BigDecimal((double) days / 360)));
////                                            }
////
////                                        } else {
////                                            int days = 30 - effectDay;
////                                            if (days <= 0) {
////                                                days = 1;
////                                            } else {
////                                                days = days + 1;
////                                            }
////                                            performance = ArithUtil.mul(holdAmountAft, new BigDecimal((double) days / 360));
////                                        }
//                                    }
//                                } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                                    if (repayDate.compareTo(effectDate) < 0) {// 还款日 < 生效日
//                                        if (effectDate.compareTo(expireDate) < 0) {// 生效日 < 到期日
//                                            // 生效日~到期日
//                                            performance = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (repayDay - effectDay + 1) / 360));
//                                        }
//                                    } else if (repayDate.compareTo(effectDate) > 0) {// 还款日 > 生效日
//                                        if (repayDate.compareTo(expireDate) == 0) {
//                                            // 生效日~还款日
//                                            performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (repayDay - effectDay) / 360));
//                                        }// 不存在还款日与到期日同月不同日（最后一期的还款日就是到期日）
//                                    } else {// 还款日 = 生效日
//                                        if (effectDate.compareTo(expireDate) < 0) {// 生效日 < 到期日
//                                            // 生效日~还款日
//                                            performance = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (repayDay - effectDay) / 360));
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            } else if (transScale.compareTo(new BigDecimal(1)) == 0) {// 全部转让
//                Date transDate = DateUtils.parseDate(transDateStr, YYYY_MM_DD);
//                int transDay = DateUtils.getDay(transDate);
//                int expireDay = DateUtils.getDay(expireDate);
//                int repayDay;
//                if (null == repayDate) {
//                    if (transDate.compareTo(now) < 0) {// 结算日之前转让
//                        if (transDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月转让
//                            if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
//                                // 因为全部转让，所以不用判断到期日
//                                performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (transDay - 1) / 360));
//                            } else {// 上个月生效
//                                // 业绩 = 10000*(转让日-生效日)/360
//                                performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (DateUtils.getDay(transDate) - DateUtils.getDay(effectDate)) / 360));
//                            }
//                        } else {// 上个月之前转让（结算日所在月转让）
//                            // 上个月之前全部转让，存续金额为0
//                        }
//                    }
//                } else {
//                    repayDay = DateUtils.getDay(repayDate);
//                    if (transDate.compareTo(now) < 0) {// 结算日之前转让
//                        if (transDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月转让
//                            if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
//                                if (expireDate.compareTo(now) >= 0) {// 上个月没到期
//                                    if (transDay > repayDay) {// 转让日 > 还款日
//                                        // 例：10号还款，还款前10000，还款后9000，15号转让100%
//                                        //  10000*(10-1)/360
//                                        //  9000*(15-10+1)/360
//                                        // 1号-10号
//                                        BigDecimal performance1 = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (repayDay - 1) / 360));
//                                        // 10号-15号
//                                        BigDecimal performance2 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (transDay - repayDay) / 360));
//
//                                        performance = ArithUtil.add(performance1, performance2);
//                                    } else if (transDay == repayDay) {
//                                        // 1号-10号
//                                        performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (repayDay - 1) / 360));
//                                    } else {
//                                        performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (transDay - 1) / 360));
//                                    }
//                                } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                                    if (transDay > repayDay) {// 转让日在还款日之后
//                                        if (expireDate.compareTo(transDate) >= 0) {// 还款日 < 转让日 <= 到期日
//                                            // 1号-10号
//                                            BigDecimal performance1 = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (repayDay - 1) / 360));
//                                            // 10号-15号
//                                            BigDecimal performance2 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (transDay - repayDay) / 360));
//
//                                            performance = ArithUtil.add(performance1, performance2);
//                                        } else if (expireDate.compareTo(repayDate) >= 0 && expireDate.compareTo(transDate) < 0) {//  还款日 <= 到期日 < 转让日
//                                            // 1号-10号
//                                            BigDecimal performance1 = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (repayDay - 1) / 360));
//                                            // 10号-到期日
//                                            BigDecimal performance2 = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (expireDay - repayDay) / 360));
//
//                                            performance = ArithUtil.add(performance1, performance2);
//                                        } else {//  到期日 <= 还款日
//                                            performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (expireDay - 1) / 360));
//                                        }
//                                    } else if (transDay == repayDay) {// 转让日与还款日同一天
//                                        if (expireDate.compareTo(transDate) > 0) {// 还款日 = 转让日 <= 到期日
//                                            // 1号-转让日(还款日)
//                                            performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (repayDay - 1) / 360));
//                                        } else {// 还款日 = 转让日 = 到期日
//                                            performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (expireDay - 1) / 360));
//                                        }
//                                    } else {// 转让日在还款日之前
//                                        if (expireDate.compareTo(transDate) < 0) {// 到期日 < 转让日
//                                            // 1号-到期日
//                                            performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (expireDay - 1) / 360));
//                                        } else {// 到期日 >= 转让日
//                                            performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (transDay - 1) / 360));
//                                        }
//                                    }
//                                }
//                            } else {// 上个月生效
//                                // 业绩 = 10000*(转让日-生效日)/360
//                                performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (DateUtils.getDay(transDate) - DateUtils.getDay(effectDate)) / 360));
//                            }
//                        } else {// 上个月之前转让（结算日所在月转让）
//                            // 上个月之前全部转让，存续金额为0
//                        }
//                    }
//                }
//
//            } else {// 部分转让
//                Date transDate = DateUtils.parseDate(transDateStr, YYYY_MM_DD);
//                int transDay = DateUtils.getDay(transDate) == 31 ? 30 : DateUtils.getDay(transDate);
//                int effectDay = DateUtils.getDay(effectDate);
//                int expireDay = DateUtils.getDay(expireDate);
//                if (null == repayDate) {
//                    // 剩余金额 = 投资金额*（1-转让比例）
//                    BigDecimal holdAmountRemain = ArithUtil.mul(holdAmountBef, ArithUtil.sub(new BigDecimal(1), transScale));
//                    // 转让金额 = 投资金额*转让比例
//                    BigDecimal holdAmountTrans = ArithUtil.mul(holdAmountBef, transScale);
//                    if (transDate.compareTo(now) < 0) {// 上个月转让
//                        if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
//                            if (expireDate.compareTo(now) >= 0) {// 上个月没到期
//                                // 剩余
//                                BigDecimal performance1 = ArithUtil.mul(holdAmountRemain, new BigDecimal((double) 30 / 360));
//                                // 转让
//                                BigDecimal performance2 = ArithUtil.mul(holdAmountTrans, new BigDecimal((double) (transDay - 1) / 360));
//                                performance = ArithUtil.add(performance1, performance2);
//                            } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                                // 剩余
//                                BigDecimal performance1 = ArithUtil.mul(holdAmountRemain, new BigDecimal((double) (expireDay - 1) / 360));
//                                // 转让
//                                BigDecimal performance2 = ArithUtil.mul(holdAmountTrans, new BigDecimal((double) (transDay - 1) / 360));
//                                performance = ArithUtil.add(performance1, performance2);
//                            }
//                        } else {// 上个月生效
//                            if (expireDate.compareTo(now) >= 0) {// 上个月没到期
//                                int days = 30 - effectDay;
//                                if (days <= 0) {
//                                    days = 1;
//                                } else {
//                                    days = days + 1;
//                                }
//                                // 剩余
//                                BigDecimal performance1 = ArithUtil.mul(holdAmountRemain, new BigDecimal((double) days / 360));
//                                // 转让
//                                BigDecimal performance2 = ArithUtil.mul(holdAmountTrans, new BigDecimal((double) (transDay - effectDay) / 360));
//                                performance = ArithUtil.add(performance1, performance2);
//                            } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                                // 剩余
//                                BigDecimal performance1 = ArithUtil.mul(holdAmountRemain, new BigDecimal((double) (expireDay - effectDay) / 360));
//                                // 转让
//                                BigDecimal performance2 = ArithUtil.mul(holdAmountTrans, new BigDecimal((double) (transDay - effectDay) / 360));
//                                performance = ArithUtil.add(performance1, performance2);
//                            }
//                        }
//                    }
//                } else {
//                    int repayDay = DateUtils.getDay(repayDate);
//                    // 还款前剩余金额 = 还款前存续金额*（1-转让比例）
//                    BigDecimal holdAmountRemainBef = ArithUtil.mul(holdAmountBef, ArithUtil.sub(new BigDecimal(1), transScale));
//                    // 还款后剩余金额 = 还款后存续金额*（1-转让比例）
//                    BigDecimal holdAmountRemainAft = ArithUtil.mul(holdAmountAft, ArithUtil.sub(new BigDecimal(1), transScale));
//
//                    if (transDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前转让，说明上个月没有转让，只计算还款前和还款后
//                        if (expireDate.compareTo(now) >= 0) {// 上个月没到期
//                            // 例：10号还款，还款前10000，还款后9000
//                            //  10000*(10-1)/360
//                            //  9000*(15-10+1)/360
//                            // 1号-10号
////                            BigDecimal performance1 = ArithUtil.mul(holdAmountRemainBef, new BigDecimal((double) (repayDay - 1) / 360));
//                            BigDecimal performance1 = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (repayDay - 1) / 360));
//                            // 10号-月底
//                            int days = 30 - repayDay;
//                            if (days <= 0) {
//                                days = 1;
//                            } else {
//                                days = days + 1;
//                            }
////                            BigDecimal performance2 = ArithUtil.mul(holdAmountRemainAft, new BigDecimal((double) days / 360));
//                            BigDecimal performance2 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) days / 360));
//
//                            performance = ArithUtil.add(performance1, performance2);
//                        } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                            // 1号-10号
//                            BigDecimal performance1 = ArithUtil.mul(holdAmountRemainBef, new BigDecimal((double) (repayDay - 1) / 360));
//                            // 10号-到期日
//                            BigDecimal performance2 = ArithUtil.mul(holdAmountRemainAft, new BigDecimal((double) (expireDay - repayDay) / 360));
//
//                            performance = ArithUtil.add(performance1, performance2);
//                        }
//                    } else if (transDate.compareTo(now) < 0) {// 上个月转让
//                        if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
//                            if (expireDate.compareTo(now) >= 0) {// 上个月没到期
//                                if (transDay > repayDay) {// 转让日在还款日之后
//                                    // 例：10号还款，还款前10000，还款后9000，15号转让60%
//                                    //  10000*(10-1)/360
//                                    //  9000*(15-10+1)/360
//                                    //  9000*40%*(30-15)/360
//                                    // 1号-10号
//                                    BigDecimal performance1 = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (repayDay - 1) / 360));
//                                    // 10号-15号（10号当天也是9000，所以要+1）
//                                    BigDecimal performance2 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (transDay - repayDay) / 360));
//                                    // 15号-月底
//                                    int days = 30 - transDay;
//                                    if (days <= 0) {
//                                        days = 1;
//                                    } else {
//                                        days = days + 1;
//                                    }
//                                    BigDecimal performance3 = ArithUtil.mul(holdAmountRemainAft, new BigDecimal((double) days / 360));
//
//                                    performance = ArithUtil.add(ArithUtil.add(performance1, performance2), performance3);
//                                } else if (transDay == repayDay) {// 转让日与还款日同一天
//                                    // 例：10号还款，还款前10000，还款后9000，10号转让60%
//                                    // 10000*(10-1)/360
//                                    // 9000*40%*(30-10+1)/360
//                                    // 1号-10号
//                                    BigDecimal performance1 = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (repayDay - 1) / 360));
//                                    // 10号-月底
//                                    int days = 30 - repayDay;
//                                    if (days <= 0) {
//                                        days = 1;
//                                    } else {
//                                        days = days + 1;
//                                    }
//                                    BigDecimal performance2 = ArithUtil.mul(holdAmountRemainAft, new BigDecimal((double) days / 360));
//
//                                    performance = ArithUtil.add(performance1, performance2);
//                                } else {// 转让日在还款日之前
//                                    // 例：10号转让，还款前10000，还款后9000，15号还款
//                                    //  10000*(10-1)/360
//                                    //  10000*40%*(15-10+1)/360
//                                    //  9000*40%*(30-15)/360
//                                    // 1号-10号
//                                    BigDecimal performance1 = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (transDay - 1) / 360));
//                                    // 10号-15号
//                                    BigDecimal performance2 = ArithUtil.mul(holdAmountRemainBef, new BigDecimal((double) (repayDay - transDay + 1) / 360));
//                                    // 15号-月底
//                                    int days = 30 - repayDay;
//                                    if (days <= 0) {
//                                        days = 1;
//                                    }
//                                    BigDecimal performance3 = ArithUtil.mul(holdAmountRemainAft, new BigDecimal((double) days / 360));
//
//                                    performance = ArithUtil.add(ArithUtil.add(performance1, performance2), performance3);
//                                }
//                            } else if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                                if (transDay > repayDay) {// 转让日在还款日之后
//                                    if (expireDate.compareTo(transDate) >= 0) {// 还款日 < 转让日 <= 到期日
//                                        // 1号-10号
//                                        BigDecimal performance1 = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (repayDay - 1) / 360));
//                                        // 10号-15号（10号当天也是9000，所以要+1）
//                                        BigDecimal performance2 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (transDay - repayDay) / 360));
//                                        // 15号-到期日
//                                        BigDecimal performance3 = ArithUtil.mul(holdAmountRemainAft, new BigDecimal((double) (expireDay - transDay) / 360));
//
//                                        performance = ArithUtil.add(ArithUtil.add(performance1, performance2), performance3);
//                                    } else if (expireDate.compareTo(repayDate) >= 0 && expireDate.compareTo(transDate) < 0) {//  还款日 <= 到期日 < 转让日
//                                        // 1号-还款日
//                                        BigDecimal performance1 = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (repayDay - 1) / 360));
//                                        // 10号-到期日
//                                        BigDecimal performance2 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (expireDay - repayDay + 1) / 360));
//
//                                        performance = ArithUtil.add(performance1, performance2);
//                                    } else {//  到期日 <= 还款日
//                                        performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (expireDay - 1) / 360));
//                                    }
//                                } else if (transDay == repayDay) {// 转让日与还款日同一天
//                                    if (expireDate.compareTo(transDate) > 0) {// 还款日 = 转让日 <= 到期日
//                                        // 1号-还款日
//                                        BigDecimal performance1 = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (repayDay - 1) / 360));
//                                        // 还款日-到期日
//                                        BigDecimal performance2 = ArithUtil.mul(holdAmountRemainAft, new BigDecimal((double) (expireDay - repayDay) / 360));
//
//                                        performance = ArithUtil.add(performance1, performance2);
//                                    } else {// 还款日 <= 转让日 = 到期日
//                                        performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (expireDay - 1) / 360));
//                                    }
//                                } else {// 转让日在还款日之前
//                                    // 1号-还款日
//                                    performance = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (transDay - 1) / 360));
//                                }
//                            }
//                        } else {
//                            if (DateUtils.getMonthStartDate(effectDate).compareTo(firstDayOfPreMonth) == 0) {// 上个月生效&上个月转让
//                                // 生效日与还款日同一个月，说明购买的是债权转让 存续金额 = 10000*(转让日-生效日)/360
//                                if (repayDate.compareTo(effectDate) < 0) {// 还款日 < 生效日
//                                    if (expireDate.compareTo(now) < 0 && expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                                        if (effectDate.compareTo(transDate) > 0) {// 还款日 < 生效日 < 转让日
//                                            // 生效日~转让日
//                                            BigDecimal performance1 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (transDay - effectDay) / 360));
//                                            // 转让日~到期日
//                                            int days = expireDay - transDay;
//                                            if (days <= 0) {
//                                                days = 1;
//                                            }
//                                            BigDecimal performance2 = ArithUtil.mul(holdAmountRemainAft, new BigDecimal((double) days / 360));
//                                            performance = ArithUtil.add(performance1, performance2);
//                                        }
//                                    } else if (expireDate.compareTo(now) >= 0) {// 上个月之后到期
//                                        if (effectDate.compareTo(transDate) > 0) {// 还款日 < 生效日 < 转让日
//                                            // 生效日~转让日
//                                            BigDecimal performance1 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (transDay - effectDay) / 360));
//                                            // 转让日~月底
//                                            int days = 30 - transDay;
//                                            if (days <= 0) {
//                                                days = 1;
//                                            }
//                                            BigDecimal performance2 = ArithUtil.mul(holdAmountRemainAft, new BigDecimal((double) days / 360));
//                                            performance = ArithUtil.add(performance1, performance2);
//                                        }
//                                    }
//                                } else if (repayDate.compareTo(effectDate) > 0) {// 还款日 > 生效日
//                                    if (expireDate.compareTo(now) < 0 && expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                                        // 生效日~还款日
//                                        BigDecimal performance1 = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (repayDay - effectDay) / 360));
//                                        // 还款日~转让日
//                                        BigDecimal performance2 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (transDay - repayDay) / 360));
//                                        // 转让日~月底
//                                        int days = expireDay - transDay;
//                                        if (days <= 0) {
//                                            days = 1;
//                                        }
//                                        BigDecimal performance3 = ArithUtil.mul(holdAmountRemainAft, new BigDecimal((double) days / 360));
//                                        performance = ArithUtil.add(ArithUtil.add(performance1, performance2), performance3);
//                                    } else if (expireDate.compareTo(now) >= 0) {// 上个月之后到期
//                                        // 生效日~还款日
//                                        BigDecimal performance1 = ArithUtil.mul(holdAmountBef, new BigDecimal((double) (repayDay - effectDay) / 360));
//                                        // 还款日~转让日
//                                        BigDecimal performance2 = ArithUtil.mul(holdAmountAft, new BigDecimal((double) (transDay - repayDay) / 360));
//                                        // 转让日~月底
//                                        int days = 30 - transDay;
//                                        if (days <= 0) {
//                                            days = 1;
//                                        }
//                                        BigDecimal performance3 = ArithUtil.mul(holdAmountRemainAft, new BigDecimal((double) days / 360));
//                                        performance = ArithUtil.add(ArithUtil.add(performance1, performance2), performance3);
//                                    }
//
//                                } else {// 还款日 = 生效日
//                                    if (expireDate.compareTo(now) < 0 && expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                                        // 还款日~转让日
//                                        BigDecimal performance1 = ArithUtil.mul(holdAmountRemainAft, new BigDecimal((double) (transDay - effectDay) / 360));
//                                        // 转让日~到期日
//                                        int days = expireDay - transDay;
//                                        if (days <= 0) {
//                                            days = 1;
//                                        }
//                                        BigDecimal performance2 = ArithUtil.mul(holdAmountRemainAft, new BigDecimal((double) days / 360));
//                                        performance = ArithUtil.add(performance1, performance2);
//                                    } else if (expireDate.compareTo(now) >= 0) {// 上个月之后到期
//                                        // 还款日~转让日
//                                        BigDecimal performance1 = ArithUtil.mul(holdAmountRemainAft, new BigDecimal((double) (transDay - effectDay) / 360));
//                                        // 转让日~月底
//                                        int days = 30 - transDay;
//                                        if (days <= 0) {
//                                            days = 1;
//                                        }
//                                        BigDecimal performance2 = ArithUtil.mul(holdAmountRemainAft, new BigDecimal((double) days / 360));
//                                        performance = ArithUtil.add(performance1, performance2);
//                                    }
//
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return performance;
//    }

    /**
     * 计算持有天数
     *
     * @param effectDate         生效日期
     * @param firstDayOfPreMonth 上个月一号
     * @param expireDate         到期日期
     * @param now                结算日期
     * @param holdDayMap         剩余部分持有天数&转让部分持有天数
     * @param transDate          转让日期
     */
    private void getHoldDays(Date effectDate, Date firstDayOfPreMonth, Date expireDate,
                             Date now, Map<String, Integer> holdDayMap, Date transDate) {
        Integer holdDays_remain = 0;
        Integer holdDays_trans = 0;
        Integer differ_trans = DateUtils.datePhaseDiffer(firstDayOfPreMonth, transDate);// 转让日 - 上个月月初
        if (differ_trans >= 0) {// 上个月转让
            if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
                if (expireDate.compareTo(DateUtils.getMonthStartDate(now)) < 0) {// 到期日是上个月或上个月之前
                    if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                        holdDays_remain = DateUtils.datePhaseDiffer(firstDayOfPreMonth, expireDate);// 剩余部分持有天数 = 到期日 - 月初
                    }
                } else {// 上个月没到期
                    holdDays_remain = 30;
                }
                holdDays_trans = DateUtils.datePhaseDiffer(firstDayOfPreMonth, transDate);// 转让部分持有天数 = 转让日 - 月初
            } else {// 上个月生效
                if (expireDate.compareTo(DateUtils.getMonthStartDate(now)) < 0) {// 上个月或上个月之前到期
                    if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                        holdDays_remain = DateUtils.datePhaseDiffer(effectDate, expireDate);// 剩余部分持有天数 = 到期日 - 生效日
                    }
                } else {// 上个月没到期
                    holdDays_remain = 30 - DateUtils.getDay(effectDate) + 1;// 持有天数 = 30 - 生效日 + 1
                }
                holdDays_trans = DateUtils.datePhaseDiffer(effectDate, transDate);// 转让部分持有天数 = 转让日 - 生效日
            }
        } else {
            // 上个月之前部分转让，只计算剩余部分
            if (expireDate.compareTo(DateUtils.getMonthStartDate(now)) < 0) {// 上个月或上个月之前到期
                if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
                    holdDays_remain = DateUtils.datePhaseDiffer(firstDayOfPreMonth, expireDate);// 剩余部分持有天数 = 到期日 - 上个月月初
                }
            } else {// 上个月没到期
                holdDays_remain = 30;// 剩余部分持有天数 = 30
            }
        }
        holdDayMap.put("holdDays_remain", holdDays_remain);
        holdDayMap.put("holdDays_trans", holdDays_trans);
    }

    @Override
    public ResultVo calcCommission(Map<String, Object> param) throws SLException {
    	
        
		Map<String, HashMap<String, CommissionRateEntity>> rateMonthMap = Maps.newHashMap();

		// 查询所有提点所属月
		List<Map<String, Object>> list = repository.getAllRateMonth();
		for (Map<String, Object> map : list) {
		    String rateMonth = CommonUtils.emptyToString(map.get("RATE_MONTH"));
		    rateMonthMap.put(rateMonth, new HashMap<String, CommissionRateEntity>());
		}

		// 查询所有提点信息
		List<CommissionRateEntity> rateList = repository.getAllCommissionRate();
		// 按月份分类
		for (CommissionRateEntity commissionRateEntity : rateList) {
		    for (Map.Entry<String, HashMap<String, CommissionRateEntity>> entry : rateMonthMap.entrySet()) {
		        String rateMonth = entry.getKey();
		        HashMap<String, CommissionRateEntity> map = entry.getValue();
		        if (commissionRateEntity.getRateMonth().equals(rateMonth)) {
		            map.put(commissionRateEntity.getJobNo(), commissionRateEntity);
		        }
		    }
		}

		List<CommissionSendBackEntity> investList = repository.getInvestInfoByJobNo(null);
        Set<String> error = Sets.newHashSet();
        Map<String, CommissionRateEntity> commissionMap = Maps.newHashMap();// 提点临时表
//		try {
			for (CommissionSendBackEntity investInfo : investList) {
			    String jobNo = investInfo.getJobNo();// 工号
			    String ranking = investInfo.getRanking();// 级别
			    String effectDateStr = investInfo.getEffectDate();// 投资生效日
			    Date effectDate = DateUtils.parseDate(effectDateStr, YYYY_MM_DD);
			    String rateMonth = DateUtils.formatDate(effectDate, YYYYMM);
			    String makeUp = investInfo.getMakeUp();
			    
			    HashMap<String, CommissionRateEntity> rateMap;
			    if (CommonUtils.isEmpty(makeUp)) {
			    	// 根据投资生效日获取对应的提点
			    	rateMap = rateMonthMap.get(rateMonth);
			    } else {
			    	// TODO 暂时写死
//			    	rateMap = rateMonthMap.get("201706");
			    	rateMap = rateMonthMap.get(DateUtils.formatDate(DateUtils.getAfterMonth(new Date(), -1), YYYYMM));
			    }

			    if (!CommonUtils.isEmpty(ranking)) {
			        String memo = investInfo.getMemo();
			        String shopName = investInfo.getShopName();
			        BigDecimal investAmount = CommonUtils.emptyToDecimal(investInfo.getInvestAmount());// 入账金额
			        BigDecimal holdAmount = CommonUtils.emptyToDecimal(investInfo.getHoldAmount());// 业绩

			        String transDateStr = investInfo.getTransDate();// 转让日期
			        BigDecimal transScale = CommonUtils.emptyToDecimal(investInfo.getTransScale());// 转让比例

			        // 根据工号获取生效月对应的提点对象
			        CommissionRateEntity rateEntity = rateMap.get(jobNo);
			        if (rateEntity == null) {
			        	error.add(jobNo);
			        	continue;
			        }
			        BigDecimal commissionRateTotal = rateEntity.getCommissionRateTotal();
			        BigDecimal commissionRatePersonal = rateEntity.getCommissionRatePersonal();// 个人提点
			        BigDecimal commissionRateGrant = rateEntity.getCommissionRateGrant();// 提成发放比例
			        
			        CommissionRateEntity existEntity = commissionMap.get(jobNo);
			        if (null == existEntity) {
			            // 保存到提点临时表中
			        	existEntity = rateEntity;
			            commissionMap.put(jobNo, rateEntity);
			        }

			        /** 1.计算做单人员的佣金和补贴 */
			        Date now = DateUtils.getMonthStartDate(new Date());
			        Date firstDayOfPreMonth = DateUtils.getMonthStartDate(DateUtils.getAfterMonth(now, -1));
			        BigDecimal commissionRate;
			        if (ranking.equals("1")) {
			            // 业务员提点 = 个人提点+总账提点
			            commissionRate = ArithUtil.add(commissionRatePersonal, commissionRateTotal);
			        } else {
			            // 管理岗提点 = 0.02
			            commissionRate = new BigDecimal("0.02");
			        }
			        /**
			         * 佣金   业务员 = 业绩*(总账提点+个人提点)
			         *        管理岗 = 业绩*2%
			         */
			        BigDecimal commission = ArithUtil.mul(holdAmount, commissionRate);
			        /**
			         * 补贴计算
			         *
			         * 补贴 = 佣金 * 12% * (n-1)*30 / 360
			         *
			         * n表示到上一个月为止，共经过了几个月（生效月为一个整月）
			         *
			         */
			        BigDecimal subsidy = new BigDecimal(0);
			        int n = DateUtils.monthPhaseDiffer(DateUtils.getMonthStartDate(effectDate), now);// n = 结算月 - 生效月
			        // “未转让”或者“转让发生在上个月”都计算上个月的补贴
			        if (transScale.compareTo(new BigDecimal(0)) == 0
			                || (transScale.compareTo(new BigDecimal(0)) > 0)
			                && DateUtils.parseDate(transDateStr, YYYY_MM_DD_HH_MM_SS).compareTo(firstDayOfPreMonth) >= 0
			                && DateUtils.parseDate(transDateStr, YYYY_MM_DD_HH_MM_SS).compareTo(now) < 0) {
			            if (n > 1) {
			                double div = ArithUtil.div(ArithUtil.mul(ArithUtil.sub((double) n, (double) 1), 30), 360);
			                BigDecimal mul = ArithUtil.mul(commission, new BigDecimal(0.12));
			                subsidy = ArithUtil.mul(mul, new BigDecimal(div));
			            }
			        }
			        BigDecimal allowance = BigDecimal.ZERO;
			        if (ranking.equals("1")) {
			        	existEntity.setInvestAmount(ArithUtil.add(existEntity.getInvestAmount(), investAmount));// 单笔入账金额
			        	existEntity.setHoldAmount(ArithUtil.add(existEntity.getHoldAmount(), holdAmount));// 单笔业绩
			        	existEntity.setCommissionAmount(ArithUtil.add(existEntity.getCommissionAmount(), commission));// 单笔佣金
			        } else {
			        	existEntity.setInvestAmountManager(ArithUtil.add(existEntity.getInvestAmountManager(), investAmount));
			        	existEntity.setHoldAmountManager(ArithUtil.add(existEntity.getHoldAmountManager(), holdAmount));
			        	existEntity.setCommissionAmountManager(ArithUtil.add(existEntity.getCommissionAmountManager(), commission));
			            allowance = getRateTotalByCondition(jobNo, memo, shopName, holdAmount, commissionRateTotal);
			            existEntity.setAllowanceAmount(ArithUtil.add(existEntity.getAllowanceAmount(), allowance));
			        }
			        existEntity.setSubsidyAmount(ArithUtil.add(existEntity.getSubsidyAmount(), subsidy));// 单笔补贴
			        /** 2.计算最终金额 */
			        BigDecimal total = ArithUtil.mul(ArithUtil.add(ArithUtil.add(commission, subsidy), allowance), commissionRateGrant);
			        existEntity.setTotalAmount(ArithUtil.add(existEntity.getTotalAmount(), total));

			        /** 3.计算该笔投资对应的上级管理岗的津贴 */
			        calcAllowance(rateMap, investInfo, commission, commissionMap);
			    } else {
			        error.add(jobNo);
			    }
			}

			/** 4.批量保存 */
			calcManagerAllowance(commissionMap, error);

			return new ResultVo(true, "佣金计算成功");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResultVo(false, "佣金计算失败");
//		}
    }


    /**
     * 处理特殊情况的总账提点，并以此计算津贴
     *
     * @param jobNo       			工号
     * @param memo        			备注
     * @param shopName    			店门
     * @param holdAmount  			一笔投资的存续金额
     * @param commissionRateTotal  	总账提点
     *
     * @return  岗位津贴
     */
    private BigDecimal getRateTotalByCondition(String jobNo, String memo, String shopName, BigDecimal holdAmount, BigDecimal commissionRateTotal) {
    	// 城市经理部分营业部按营业部经理0.2%提点拿
    	// 城市经理拿营业部经理提点，0.2%
    	// 城市经理拿这些营业部的业绩，并按营业部经理提点0.2%拿
    	
    	// 城市经理郑顺拿营业部经理提点，0.2%
    	// 田景升拿这些营业部的提成，提点按0.02%拿
    	// 田景升拿这些营业部的业绩，按0.02%的提点

        // 城市经理按营业部经理0.2%提点拿
    	
        if (CommonUtils.emptyToString(memo).equals("城市经理拿营业部经理提点，0.2%") && "010009591".equals(jobNo)) {// 郑顺
            commissionRateTotal = new BigDecimal("0.002");
        } else if ((CommonUtils.emptyToString(memo).equals("田景升拿这些营业部的提成，提点按0.02%拿")
                || CommonUtils.emptyToString(memo).equals("田景升拿这些营业部的业绩，按0.02%的提点")
                || CommonUtils.emptyToString(memo).equals("田景升拿此营业部的提成"))
                && "010001168A".equals(jobNo)) {
            commissionRateTotal = new BigDecimal("0.0002");
        } else if (Arrays.asList(specialManagerJobNos1).contains(jobNo) && memo1.equals(memo)) {
            commissionRateTotal = new BigDecimal("0.002");
        } else if (Arrays.asList(specialManagerJobNos2).contains(jobNo) && memo2.equals(memo)) {
            commissionRateTotal = new BigDecimal("0.002");
        } else if (Arrays.asList(specialManagerJobNos1).contains(jobNo) && memo3.equals(memo)) {
            commissionRateTotal = new BigDecimal("0.002");
        }
        return ArithUtil.mul(holdAmount, commissionRateTotal);
    }
//    private BigDecimal getRateTotalByCondition(String jobNo, String memo, String shopName, BigDecimal holdAmount, CommissionRateEntity rateEntity) {
//    	BigDecimal commissionRateTotal;
//    	if (CommonUtils.emptyToString(memo).equals("城市经理拿营业部经理提点，0.2%") && "010009591".equals(jobNo)) {
//    		commissionRateTotal = new BigDecimal("0.002");
//    	} else if ((CommonUtils.emptyToString(memo).equals("田景升拿这些营业部的提成，提点按0.02%拿")
//    			|| CommonUtils.emptyToString(memo).equals("田景升拿这些营业部的业绩，按0.02%的提点"))
//    			&& "010001168A".equals(jobNo)) {
//    		commissionRateTotal = new BigDecimal("0.0002");
//    	} else if ((Arrays.asList(specialShops).contains(shopName) && Arrays.asList(specialManagerJobNos).contains(jobNo))) {
//    		commissionRateTotal = new BigDecimal("0.002");
//    	} else {
//    		commissionRateTotal = rateEntity.getCommissionRateTotal();
//    	}
//    	return ArithUtil.mul(holdAmount, commissionRateTotal);
//    }


    private static String[] specialShops = {"本溪小市理财营业部", "本溪小市理财营业部辽阳大润发门店", "大连金州理财营业部瓦房店中心门店", "大连西安路理财营业部", "沈阳大东龙之梦理财营业部"};
    private static String[] specialManagers = {"徐海", "裴立凤", "裴景义", "李明霏"};
    
    
//    private static String[] specialManagerJobNos1 = {"010000256", "010016976", "010017582", "010021220", "010078657"};
    private static String[] specialManagerJobNos1 = {"010000256", "010017582", "010021220", "010078657"};
    private static String memo1 = "城市经理部分营业部按营业部经理0.2%提点拿";

    private static String[] specialManagerJobNos2 = {"010034407", "010078657"};
    private static String memo2 = "城市经理拿这些营业部的业绩，并按营业部经理提点0.2%拿";

    private static String memo3 = "城市经理按营业部经理0.2%提点拿";


    /**
     * 计算一笔投资中上级的津贴
     *
     * @param commissionMap  {工号:提点信息}
     * @param investInfo     投资信息
     * @param commission     一笔投资的佣金
     * @param commissionMap
     */
    private void calcAllowance(Map<String, CommissionRateEntity> rateMap, CommissionSendBackEntity investInfo,
    		BigDecimal commission, Map<String, CommissionRateEntity> commissionMap) {
        String ranking = investInfo.getRanking();
        BigDecimal holdAmount = investInfo.getHoldAmount();
        String shopName = investInfo.getShopName();
        String rank_02_jobNo = investInfo.getRank_02_JobNo();// 团队经理工号
        String rank_03_jobNo = investInfo.getRank_03_JobNo();// 大团队经理工号
        String rank_04_jobNo = investInfo.getRank_04_JobNo();// 门店店长工号
        String rank_05_jobNo = investInfo.getRank_05_JobNo();// 营业部经理工号
        String rank_06_jobNo = investInfo.getRank_06_JobNo();// 城市经理工号
        String rank_07_jobNo = investInfo.getRank_07_JobNo();// 区域经理工号
        String rank_08_jobNo = investInfo.getRank_08_JobNo();// 副总裁工号
        String rank_09_jobNo = investInfo.getRank_09_JobNo();// 最高负责人工号
        String memo = CommonUtils.emptyToString(investInfo.getMemo());// 备注

        for (int i = Integer.valueOf(ranking); i < 9; i++) {
            CommissionRateEntity rateEntity = null;
            switch (i) {
                case 1:
                    rateEntity = rateMap.get(rank_02_jobNo);
                    break;
                case 2:
                    rateEntity = rateMap.get(rank_03_jobNo);
                    break;
                case 3:
                    rateEntity = rateMap.get(rank_04_jobNo);
                    break;
                case 4:
                    rateEntity = rateMap.get(rank_05_jobNo);
                    break;
                case 5:
                    rateEntity = rateMap.get(rank_06_jobNo);
                    break;
                case 6:
                    rateEntity = rateMap.get(rank_07_jobNo);
                    break;
                case 7:
                    rateEntity = rateMap.get(rank_08_jobNo);
                    break;
                case 8:
                    rateEntity = rateMap.get(rank_09_jobNo);
                    break;
            }
            if (rateEntity != null) {
                String jobNo = rateEntity.getJobNo();
//                if (jobNo.equals("010002751")) {
//                	BigDecimal commissionRateTotal = rateEntity.getCommissionRateTotal();
//                	System.out.println("");
//                }
                CommissionRateEntity existEntity = commissionMap.get(jobNo);
                if (null == existEntity) {
                	// 保存到提点临时表中
                	existEntity = rateEntity;
                	commissionMap.put(jobNo, existEntity);
                }
                BigDecimal allowance = getRateTotalByCondition(jobNo, memo, shopName, holdAmount, rateEntity.getCommissionRateTotal());

//                rateEntity.setAllowanceAmount(ArithUtil.add(rateEntity.getAllowanceAmount(), allowance));
//                rateEntity.setInvestAmount(ArithUtil.add(rateEntity.getInvestAmount(), investInfo.getInvestAmount()));
//                rateEntity.setCommissionAmount(ArithUtil.add(rateEntity.getCommissionAmount(), commission));
//                rateEntity.setHoldAmount(ArithUtil.add(rateEntity.getHoldAmount(), investInfo.getHoldAmount()));

                existEntity.setInvestAmountManager(ArithUtil.add(existEntity.getInvestAmountManager(), investInfo.getInvestAmount()));
                existEntity.setHoldAmountManager(ArithUtil.add(existEntity.getHoldAmountManager(), investInfo.getHoldAmount()));
                existEntity.setCommissionAmountManager(ArithUtil.add(existEntity.getCommissionAmountManager(), commission));
                existEntity.setAllowanceAmount(ArithUtil.add(existEntity.getAllowanceAmount(), allowance));

                // 计算最终金额
                BigDecimal total = ArithUtil.mul(allowance, rateEntity.getCommissionRateGrant());
                existEntity.setTotalAmount(ArithUtil.add(existEntity.getTotalAmount(), total));
            }
        }
    }

    /**
     * 封装批量保存的数据
     *
     * @param commissionMap 提点信息
     * @param error         组织架构中存在但提点中不存在的工号
     *
     * @throws SLException
     */
    private void calcManagerAllowance(Map<String, CommissionRateEntity> commissionMap, Set<String> error) throws SLException {
        List<Object[]> list = Lists.newArrayList();
        for (String jobNo : commissionMap.keySet()) {
            CommissionRateEntity entity = commissionMap.get(jobNo);
            String ranking = entity.getRanking();
            if (!CommonUtils.isEmpty(ranking)) {
                List<Object> arrToBeList = Lists.newArrayList();

                /** 2017-6-7 增加主键、工号、姓名 */
                arrToBeList.add(UUID.randomUUID().toString());// [0] 主键
                arrToBeList.add(CommonUtils.emptyToString(entity.getJobNo()));// [1] 工号
                arrToBeList.add(CommonUtils.emptyToString(entity.getCustName()));// [2] 姓名
                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getInvestAmount()));// [3] 下属总投资额
                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getHoldAmount()));// [4] 下属业绩
                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getCommissionAmount()));// [5] 下属佣金
                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getInvestAmountManager()));// [6] 管理岗投资额
                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getHoldAmountManager()));// [7] 管理岗业绩
                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getCommissionAmountManager()));// [8] 管理岗佣金
                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getAllowanceAmount()));// [9] 管理岗津贴
                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getSubsidyAmount()));// [10] 补贴
                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getTotalAmount()));// [11] 最终佣金
                arrToBeList.add(new Date());// [12] 创建时间

                list.add(arrToBeList.toArray());
            } else {
                error.add(jobNo);
            }
        }

        if (error.size() > 0) {
            for (String s : error) {
                System.out.println("~~~~~~~~~~~~~~~~~~~ " + s);
            }
        }
        repository.updateAmount(list);
    }
//    private void calcManagerAllowance(Map<String, CommissionRateEntity> commissionMap, List<String> error) throws SLException {
//        List<Object[]> list = Lists.newArrayList();// [下属总投资额， 下属总佣金， 团队总投资额， 管理岗佣金， 管理岗津贴， 最终佣金， 补贴， 工号]
//        for (String jobNo : commissionMap.keySet()) {
//            CommissionRateEntity entity = commissionMap.get(jobNo);
//            String ranking = entity.getRanking();
//            if (!CommonUtils.isEmpty(ranking)) {
//                List<Object> arrToBeList = Lists.newArrayList();
//
//                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getInvestAmount()));// [0] 下属总投资额
//                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getCommissionAmount()));// [1] 下属佣金
//                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getInvestAmountManager()));// [2] 团队总投资额 = 下属总投资额 + 管理岗个人总投资额
//                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getCommissionAmountManager()));// [3] 管理岗佣金
//                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getAllowanceAmount()));// [4] 管理岗津贴
//                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getTotalAmount()));// [5] 最终佣金
//                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getHoldAmount()));// [6] 业务员业绩
//                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getHoldAmountManager()));// [7] 管理岗业绩
//                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getSubsidyAmount()));// [8] 补贴
//                arrToBeList.add(jobNo);// [9] 工号
//                list.add(arrToBeList.toArray());
//            } else {
//                error.add(jobNo);
//            }
//        }
//
//        if (error.size() > 0) {
//            for (String s : error) {
//                System.out.println("~~~~~~~~~~~~~~~~~~~ " + s);
//            }
//        }
//
//        repository.updateAmount(list);
//    }

//    public ResultVo calcCommission(Map<String, Object> param) throws SLException {
//        try {
//            Map<String, CommissionRateEntity> commissionMap = Maps.newHashMap();
//            // 查询所有提点信息
//            List<CommissionRateEntity> rateList = repository.getAllCommissionRate();
//            if (null != rateList && rateList.size() > 0) {
//                for (CommissionRateEntity commissionRateEntity : rateList) {
//                    String jobNo = commissionRateEntity.getJobNo();
//                    String ranking = commissionRateEntity.getRanking();
//                    BigDecimal commissionRateTotal = commissionRateEntity.getCommissionRateTotal();// 基础提点
//                    BigDecimal commissionRatePersonal = commissionRateEntity.getCommissionRatePersonal();// 个人提点
//                    BigDecimal commissionRateGrant = commissionRateEntity.getCommissionRateGrant();
//                    // 根据工号查询投资信息
////                    if ("010057453".equals(jobNo)) {
//                        //                if ("010000695".equals(jobNo) || "010038725".equals(jobNo) || "010049866".equals(jobNo)
//                        //                        || "010057453".equals(jobNo) || "010074916".equals(jobNo) || "010082309".equals(jobNo)
//                        //                        || "010049870".equals(jobNo)
//                        //                        || "010000277".equals(jobNo)
//                        //                        ) {
//                        List<CommissionSendBackEntity> investList = repository.getInvestInfoByJobNo(jobNo);
//                        BigDecimal commissionSum = new BigDecimal(0);// 佣金汇总
//                        BigDecimal subsidySum = new BigDecimal(0);// 补贴汇总
//                        BigDecimal investAmountSum = new BigDecimal(0);// 投资金额汇总
//                        BigDecimal holdAmountSum = new BigDecimal(0);// 业绩汇总
//                        if (null != investList && investList.size() > 0) {
//                            for (CommissionSendBackEntity investInfo : investList) {
//                                String effectDateStr = investInfo.getEffectDate();// 投资生效日
//                                Date effectDate = DateUtils.parseDate(effectDateStr, YYYY_MM_DD);
//                                BigDecimal holdAmount = CommonUtils.emptyToDecimal(investInfo.getHoldAmount());
//                                BigDecimal investAmount = CommonUtils.emptyToDecimal(investInfo.getInvestAmount());
//                                investAmountSum = ArithUtil.add(investAmountSum, investAmount);
//                                holdAmountSum = ArithUtil.add(holdAmountSum, holdAmount);
//                                BigDecimal transScale = CommonUtils.emptyToDecimal(investInfo.getTransScale());
//                                String transDateStr = investInfo.getTransDate();
//
//                                Date now = DateUtils.getMonthStartDate(new Date());
//                                Date firstDayOfPreMonth = DateUtils.getMonthStartDate(DateUtils.getAfterMonth(now, -1));
//
//                                BigDecimal commissionRate;
//                                if (ranking.equals("1")) {
//                                    // 业务员提点 = 个人提点+总账提点
//                                    commissionRate = ArithUtil.add(commissionRatePersonal, commissionRateTotal);
//                                } else {
//                                    // 管理岗提点 = 0.02
//                                    commissionRate = new BigDecimal("0.02");
//                                }
//                                /**
//                                 * 佣金   业务员 = 业绩*(总账提点+个人提点)
//                                 *        管理岗 = 业绩*2%
//                                 */
//                                BigDecimal commission = ArithUtil.mul(holdAmount, commissionRate);
//                                // 佣金汇总
//                                commissionSum = ArithUtil.add(commissionSum, commission);
//                                /**
//                                 * 补贴计算
//                                 *
//                                 * 补贴 = 佣金 * 12% * (n-1)*30 / 360
//                                 *
//                                 * n表示到上一个月为止，共经过了几个月（生效月为一个整月）
//                                 *
//                                 */
//                                BigDecimal subsidy = new BigDecimal(0);
//                                int n = DateUtils.monthPhaseDiffer(DateUtils.getMonthStartDate(effectDate), now);// n = 结算月 - 生效月
//                                // “未转让”或者“转让发生在上个月”都计算上个月的补贴
//                                if (transScale.compareTo(new BigDecimal(0)) == 0
//                                        || (transScale.compareTo(new BigDecimal(0)) > 0)
//                                        && DateUtils.parseDate(transDateStr, YYYY_MM_DD_HH_MM_SS).compareTo(firstDayOfPreMonth) >= 0
//                                        && DateUtils.parseDate(transDateStr, YYYY_MM_DD_HH_MM_SS).compareTo(now) < 0) {
//                                    if (n > 1) {
//                                        double div = ArithUtil.div(ArithUtil.mul(ArithUtil.sub((double) n, (double) 1), 30), 360);
//                                        BigDecimal mul = ArithUtil.mul(commission, new BigDecimal(0.12));
//                                        subsidy = ArithUtil.mul(mul, new BigDecimal(div));
//                                    }
//                                    // 补贴汇总
//                                    subsidySum = ArithUtil.add(subsidySum, subsidy);
//                                }
//
//                                // 团队总业绩：本次投资的业绩同时算作管理岗的一单投资业绩
//                                calcManagerInvest(ranking, investInfo, commissionMap, commission);
//                            }
//                        }
//                        if ("010000277".equals(jobNo)) {
//                            System.out.println("");
//                        }
//                        // 汇总个人佣金
//                        CommissionRateEntity entity = commissionMap.get(jobNo);
//                        if (entity == null) {
//                            entity = new CommissionRateEntity();
//                        }
//                        if (ranking.equals("1")) {
//                            entity.setInvestAmount(ArithUtil.add(entity.getInvestAmount(), investAmountSum));
//                            entity.setHoldAmount(ArithUtil.add(entity.getHoldAmount(), holdAmountSum));
//                            entity.setCommissionAmount(ArithUtil.add(entity.getCommissionAmount(), commissionSum));
//                        } else {
//                            entity.setInvestAmountManager(ArithUtil.add(entity.getInvestAmountManager(), investAmountSum));
//                            entity.setHoldAmountManager(ArithUtil.add(entity.getHoldAmountManager(), holdAmountSum));
//                            entity.setCommissionAmountManager(ArithUtil.add(entity.getCommissionAmountManager(), commissionSum));
//                        }
//                        entity.setSubsidyAmount(subsidySum);
//                        entity.setCommissionRateTotal(commissionRateTotal);
//                        entity.setCommissionRatePersonal(commissionRatePersonal);
//                        entity.setCommissionRateGrant(commissionRateGrant);
//                        entity.setRanking(ranking);
//                        entity.setJobNo(jobNo);
//                        commissionMap.put(jobNo, entity);
//                    }
////                }
//            }
//            // 管理岗计算金额
//            calcManagerAllowance(commissionMap);
//
//            return new ResultVo(true, "佣金计算成功");
//        } catch (Exception e) {
//            log.error("佣金计算失败" + e.getMessage());
//            return new ResultVo(false, "佣金计算失败", e);
//        }
//    }

//    /**
//     * 本次投资的业绩也算作对应管理岗的业绩
//     *
//     * @param ranking       本次投资的业务员级别
//     * @param investInfo    投资信息
//     * @param commissionMap key:工号，value:提点信息对象
//     * @param commission    本次投资计算得到的业绩
//     */
//    private void calcManagerInvest(String ranking, CommissionSendBackEntity investInfo,
//                                   Map<String, CommissionRateEntity> commissionMap, BigDecimal commission) {
//        String jobNo_n = "";
//        for (int i = Integer.valueOf(ranking); i < 9; i++) {
//            switch (i) {
//                case 1:
//                    jobNo_n = investInfo.getRank_02_JobNo();
//                    break;
//                case 2:
//                    jobNo_n = investInfo.getRank_03_JobNo();
//                    break;
//                case 3:
//                    jobNo_n = investInfo.getRank_04_JobNo();
//                    break;
//                case 4:
//                    jobNo_n = investInfo.getRank_05_JobNo();
//                    break;
//                case 5:
//                    jobNo_n = investInfo.getRank_06_JobNo();
//                    break;
//                case 6:
//                    jobNo_n = investInfo.getRank_07_JobNo();
//                    break;
//                case 7:
//                    jobNo_n = investInfo.getRank_08_JobNo();
//                    break;
//                case 8:
//                    jobNo_n = investInfo.getRank_09_JobNo();
//                    break;
//            }
//            if (StringUtils.isNotEmpty(jobNo_n)) {
//                if ("010000277".equals(jobNo_n)) {
//                    System.out.println("");
//                }
//                CommissionRateEntity entity = commissionMap.get(jobNo_n);
//                if (entity == null) {
//                    entity = new CommissionRateEntity();
//                }
//                if ((Arrays.asList(specialShops).contains(investInfo.getShopName())
//                        && Arrays.asList(specialManagers).contains(investInfo.getManagerName()))) {
//                    entity.setShopName(investInfo.getShopName());
//                    entity.setCustName(investInfo.getManagerName());
//                }
//                // 田景升拿这些营业部的业绩，按0.02%的提点
//                if ((CommonUtils.emptyToString(investInfo.getMemo()).matches("城市经理郑顺按营业部经理提点0.2%拿提成") && "010009591".equals(jobNo_n))
//                        || (CommonUtils.emptyToString(investInfo.getMemo()).matches("田景升拿这些营业部的业绩，按0.02%的提点") && "010001168A".equals(jobNo_n))) {
//                    entity.setMemo(investInfo.getMemo());
//                }
//
//                entity.setInvestAmount(ArithUtil.add(entity.getInvestAmount(), investInfo.getInvestAmount()));
//                entity.setHoldAmount(ArithUtil.add(entity.getHoldAmount(), investInfo.getHoldAmount()));
//                entity.setCommissionAmount(ArithUtil.add(entity.getCommissionAmount(), commission));
//                commissionMap.put(jobNo_n, entity);
//            }
//        }
//    }

//    /**
//     * 计算管理岗津贴，更新数据库
//     *
//     * @param commissionMap
//     */
//    private void calcManagerAllowance(Map<String, CommissionRateEntity> commissionMap) throws SLException {
//        List<String> error = new ArrayList<>();
//        List<Object[]> list = Lists.newArrayList();// [下属总投资额， 下属总佣金， 团队总投资额， 管理岗佣金， 管理岗津贴， 最终佣金， 补贴， 工号]
//        for (String jobNo : commissionMap.keySet()) {
//            CommissionRateEntity entity = commissionMap.get(jobNo);
//            String ranking = entity.getRanking();
//            if (!CommonUtils.isEmpty(ranking)) {
//                List<Object> arrToBeList = Lists.newArrayList();
//
//                BigDecimal subsidyAmount = entity.getSubsidyAmount();// 补贴
//                BigDecimal commissionRateGrant = entity.getCommissionRateGrant();// 提成发放比例
//                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getInvestAmount()));// [0] 下属总投资额
//                arrToBeList.add(CommonUtils.emptyToDecimal(entity.getCommissionAmount()));// [1] 下属佣金
//                if (!ranking.equals("1")) {
//
//                    BigDecimal holdAmountManager = entity.getHoldAmountManager();// 管理岗业绩
//                    BigDecimal holdAmount = entity.getHoldAmount();// 下属业绩
//                    BigDecimal teamHoldAmount = ArithUtil.add(holdAmount, holdAmountManager);// 团队总业绩 = 下属业绩 + 管理岗业绩
//                    BigDecimal commissionRateTotal = entity.getCommissionRateTotal();// 总账提点
//                    // 津贴 = 团队总业绩*管理岗总账提点
//                    BigDecimal allowance;
//                    if ((Arrays.asList(specialShops).contains(entity.getShopName()) && Arrays.asList(specialManagers).contains(entity.getCustName()))
//                            || CommonUtils.emptyToString(entity.getMemo()).matches("城市经理郑顺按营业部经理提点0.2%拿提成")) {
//                        allowance = ArithUtil.mul(teamHoldAmount, new BigDecimal("0.002"));
//                    } else if (CommonUtils.emptyToString(entity.getMemo()).matches("田景升拿这些营业部的业绩，按0.02%的提点")) {
//                        allowance = ArithUtil.mul(teamHoldAmount, new BigDecimal("0.0002"));
//                    } else {
//                        allowance = ArithUtil.mul(teamHoldAmount, commissionRateTotal);
//                    }
//
//                    // 最终金额 = (管理岗个人佣金 + 津贴 + 补贴)*提成发放比例
//                    BigDecimal commissionAmountManager = entity.getCommissionAmountManager();// 管理岗个人佣金
//                    BigDecimal totalAmount = ArithUtil.mul(ArithUtil.add(ArithUtil.add(commissionAmountManager, allowance), subsidyAmount), commissionRateGrant);
//
//                    arrToBeList.add(CommonUtils.emptyToDecimal(entity.getInvestAmountManager()));// [2] 团队总投资额 = 下属总投资额 + 管理岗个人总投资额
//                    arrToBeList.add(CommonUtils.emptyToDecimal(commissionAmountManager));// [3] 管理岗佣金
//                    arrToBeList.add(CommonUtils.emptyToDecimal(allowance));// [4] 管理岗津贴
//                    arrToBeList.add(CommonUtils.emptyToDecimal(totalAmount));// [5] 最终佣金
//                    arrToBeList.add(CommonUtils.emptyToDecimal(entity.getHoldAmount()));// [6] 业务员业绩
//                    arrToBeList.add(CommonUtils.emptyToDecimal(entity.getHoldAmountManager()));// [7] 管理岗业绩
//                } else {
//                    BigDecimal commissionAmount = entity.getCommissionAmount();
//                    // 最终金额 = (佣金 + 补贴)*提成发放比例
//                    BigDecimal totalAmount = ArithUtil.mul(ArithUtil.add(commissionAmount, subsidyAmount), commissionRateGrant);
//
//                    arrToBeList.add(0);// [2] 团队总投资额 = 下属总投资额 + 管理岗个人总投资额
//                    arrToBeList.add(0);// [3] 管理岗佣金
//                    arrToBeList.add(0);// [4] 管理岗津贴
//                    arrToBeList.add(totalAmount);// [5] 最终佣金
//                    arrToBeList.add(CommonUtils.emptyToDecimal(entity.getHoldAmount()));// [6] 业务员业绩
//                    arrToBeList.add(0);// [7] 管理岗业绩
//                }
//                arrToBeList.add(subsidyAmount);// [8] 补贴
//                arrToBeList.add(jobNo);// [9] 工号
//                list.add(arrToBeList.toArray());
//            } else {
//                error.add(jobNo);
//            }
//        }
//
//        for (String s : error) {
//            System.out.println("~~~~~~~~~~~~~~~~~~~ " + s);
//        }
//
//        repository.updateAmount(list);
//    }

    @Override
    public ResultVo queryPerformanceList(Map<String, Object> param) {
        try {

//            this.calcPerformance(null);

            Map<String, Object> data = new HashMap<>();
            Page<Map<String, Object>> page = repository.queryPerformanceList(param);

            data.put("iTotalDisplayRecords", page.getTotalElements());
            data.put("data", page.getContent());
            return new ResultVo(true, "业绩查询成功", data);
        } catch (Exception e) {
            log.error("业绩查询失败" + e.getMessage());
            return new ResultVo(false, "业绩查询失败");
        }
    }

    @Override
    public ResultVo saveConstruct(Map<String, Object> param) throws SLException {
//        try {
            List<Map<String, Object>> params = (List<Map<String, Object>>) param.get("bigList");

            int batchInsertBusinessDept = repository.batchSaveConstruct(params);
            if (batchInsertBusinessDept == 0) {
                return new ResultVo(false, "导入组织架构数据失败");
            }

            repository.updateRank(1);// 批量更新职级

            return new ResultVo(true, "导入组织架构数据成功", new HashMap<>());
//        } catch (Exception e) {
//            log.error("导入组织架构数据失败" + e.getMessage());
//            return new ResultVo(false, "导入组织架构数据失败", e.getMessage());
//        }
    }

    @Override
    public ResultVo saveCommissionRate(Map<String, Object> param) {
        try {
            List<Map<String, Object>> params = (List<Map<String, Object>>) param.get("bigList");
            String rateMonth = CommonUtils.emptyToString(param.get("rateMonth"));

            int batchInsertBusinessDept = repository.batchSaveCommissionRate(params, rateMonth);

            if (batchInsertBusinessDept == 0) {
                return new ResultVo(false, "导入提点数据失败");
            }

            repository.updateRank(2);// 批量更新职级

            return new ResultVo(true, "导入提点数据成功", new HashMap<>());
        } catch (Exception e) {
            log.error("导入组织架构数据失败" + e.getMessage());
            return new ResultVo(false, "导入提点数据失败", e.getMessage());
        }
    }

    @Override
    public ResultVo queryCommissionAmountList(Map<String, Object> param) {
        try {

//            this.calcCommission(null);

            Map<String, Object> data = new HashMap<>();
            Page<Map<String, Object>> page = repository.queryCommissionAmountList(param);

            data.put("iTotalDisplayRecords", page.getTotalElements());
            data.put("data", page.getContent());
            return new ResultVo(true, "最终发放金额查询成功", data);
        } catch (Exception e) {
            log.error("最终发放金额查询失败" + e.getMessage());
            return new ResultVo(false, "最终发放金额查询失败");
        }
    }

    @Override
    public ResultVo updateRank() {
        try {
            repository.updateRank(2);

            return new ResultVo(true, "批量更新职级成功", null);
        } catch (Exception e) {
            log.error("批量更新职级失败" + e.getMessage());
            return new ResultVo(false, "批量更新职级失败");
        }
    }


    public static void main(String[] args) {

    }

    public static double add(double... v) {
        BigDecimal b = new BigDecimal(0);
        for (int i = 0; i < v.length; i += 2) {
            double v1 = v[i];
            double v2;
            if ((i + 2) <= v.length) {
                v2 = v[i + 1];
            } else {
                v2 = 0;
            }
            BigDecimal b1 = new BigDecimal(Double.toString(v1));
            BigDecimal b2 = new BigDecimal(Double.toString(v2));
            b = b.add(b1.add(b2));
        }

        return b.doubleValue();
    }
    
//  @Override
//  public void calcCommission() {
//      Map<String, CommissionRateEntity> commissionMap = Maps.newHashMap();
//      // 查询所有提点信息
//      List<CommissionRateEntity> rateList = repository.getAllCommissionRate();
//      if (null != rateList && rateList.size() > 0) {
//          for (CommissionRateEntity commissionRateEntity : rateList) {
//              String jobNo = commissionRateEntity.getJobNo();
//              String ranking = commissionRateEntity.getRanking();
//              BigDecimal commissionRateTotal = commissionRateEntity.getCommissionRateTotal();// 基础提点
//              BigDecimal commissionRatePersonal = commissionRateEntity.getCommissionRatePersonal();// 个人提点
//              BigDecimal commissionRateGrant = commissionRateEntity.getCommissionRateGrant();
//              // 根据工号查询投资信息
////              if ("010000277".equals(jobNo)) {
//              List<CommissionSendBackEntity> investList = repository.getInvestInfoByJobNo(jobNo);
//              BigDecimal commissionSum = new BigDecimal(0);// 佣金汇总
//              BigDecimal subsidySum = new BigDecimal(0);// 补贴汇总
//              BigDecimal holdAmountSum = new BigDecimal(0);// 业绩汇总
//              if (null != investList && investList.size() > 0) {
//                  for (CommissionSendBackEntity investInfo : investList) {
//                      String effectDateStr = investInfo.getEffectDate();// 投资生效日
//                      String expireDateStr = investInfo.getExpireDate();// 计息截止日
//                      Date effectDate = DateUtils.parseDate(effectDateStr, YYYY_MM_DD);
//                      Date expireDate = DateUtils.parseDate(expireDateStr, YYYY_MM_DD);
//                      String transDateStr = investInfo.getTransDate();// 转让日
//                      BigDecimal transScale = CommonUtils.emptyToDecimal(investInfo.getTransScale());// 转让比例
//                      BigDecimal holdAmount = CommonUtils.emptyToDecimal(investInfo.getHoldAmount());
//                      holdAmountSum = ArithUtil.add(holdAmountSum, holdAmount);
//                      /**
//                       * 流转标
//                       *
//                       * 佣金 = 存续金额*（0.018+个人提点）*（天数/360）
//                       *
//                       */
//                      Date now = new Date();
//                      Date firstDayOfPreMonth = DateUtils.getMonthStartDate(DateUtils.getAfterMonth(now, -1));
//                      Date lastDayOfPreMonth = DateUtils.getLastDay(DateUtils.getAfterMonth(now, -1));
//                      BigDecimal oneYear = new BigDecimal(360);
//
//                      BigDecimal commission = new BigDecimal(0);
//                      BigDecimal commissionRate;
//                      if (ranking.equals("1")) {
//                          // 业务员提点 = 个人提点+总账提点
//                          commissionRate = ArithUtil.add(commissionRatePersonal, commissionRateTotal);
//                      } else {
//                          // 管理岗提点 = 0.02
//                          commissionRate = new BigDecimal("0.02");
//                      }
//                      int holdDays = 0;// 上个月持有天数
//                      if (StringUtils.isEmpty(transDateStr)) {// 未转让
//                          // 佣金 = 存续金额*（0.018+个人提点）*（天数/360）
//                          if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
//                              if (expireDate.compareTo(DateUtils.getMonthStartDate(now)) < 0) {// 到期日是上个月或上个月之前
//                                  if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                                      holdDays = DateUtils.datePhaseDiffer(firstDayOfPreMonth, expireDate);// 持有天数 = 到期日 - 月初
//                                  }
//                              } else {// 上个月没到期
//                                  holdDays = 30;//
//                              }
//                          } else {// 上个月生效
//                              if (expireDate.compareTo(DateUtils.getMonthStartDate(now)) < 0) {// 上个月或上个月之前到期
//                                  if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
//                                      holdDays = DateUtils.datePhaseDiffer(effectDate, expireDate);// 持有天数 = 到期日 - 生效日
//                                  }
//                              } else {// 上个月没到期
//                                  /** 每个月按30天来计算 */
//                                  holdDays = 30 - DateUtils.getDay(effectDate);// 持有天数 = 30 - 生效日
//                                  //holdDays = DateUtils.datePhaseDiffer(effectDate, lastDayOfPreMonth);// 持有天数 = 月底 - 生效日
//                              }
//                          }
//                          commission = ArithUtil.mul(
//                                  ArithUtil.mul(holdAmount, commissionRate),
//                                  ArithUtil.div(new BigDecimal(holdDays), oneYear));
//                      } else if (transScale.compareTo(new BigDecimal(1)) == 0) {// 全部转让
//                          Date transDate = DateUtils.parseDate(transDateStr, YYYY_MM_DD);
//                          int differ_trans = DateUtils.datePhaseDiffer(firstDayOfPreMonth, transDate);// 转让日 - 上个月月初
//                          if (differ_trans >= 0) {// 上个月转让
//                              if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
//                                  holdDays = differ_trans;// 持有天数 = 转让日 - 上个月月初
//                              } else {// 上个月生效
//                                  holdDays = DateUtils.datePhaseDiffer(effectDate, transDate);// 持有天数 = 转让日 - 生效日
//                              }
//                          } else {
//                              // 上个月之前全部转让，上个月不再计算
//                              continue;
//                          }
//                          commission = ArithUtil.mul(
//                                  ArithUtil.mul(holdAmount, commissionRate),
//                                  ArithUtil.div(new BigDecimal(holdDays), oneYear));
//
//                      } else {// 部分转让
//                          Date transDate = DateUtils.parseDate(transDateStr, YYYY_MM_DD);
//                          Map<String, Integer> holdDayMap = Maps.newHashMap();
//                          holdDayMap.put("holdDays_remain", 0);
//                          holdDayMap.put("holdDays_trans", 0);
//                          getHoldDays(effectDate, firstDayOfPreMonth, expireDate, now, holdDayMap, transDate);
////                              if (differ_trans >= 0) {// 上个月转让
////                                  if (effectDate.compareTo(firstDayOfPreMonth) < 0) {// 上个月之前生效
////                                      if (expireDate.compareTo(DateUtils.getMonthStartDate(now)) < 0) {// 到期日是上个月或上个月之前
////                                          if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
////                                              holdDays_remain = DateUtils.datePhaseDiffer(firstDayOfPreMonth, expireDate);// 剩余部分持有天数 = 到期日 - 月初
////                                          }
////                                      } else {// 上个月没到期
////                                          holdDays_remain = 30;
////                                      }
////                                      holdDays_trans = DateUtils.datePhaseDiffer(firstDayOfPreMonth, transDate);// 转让部分持有天数 = 转让日 - 月初
////                                  } else {// 上个月生效
////                                      if (expireDate.compareTo(DateUtils.getMonthStartDate(now)) < 0) {// 上个月或上个月之前到期
////                                          if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
////                                              holdDays_remain = DateUtils.datePhaseDiffer(effectDate, expireDate);// 剩余部分持有天数 = 到期日 - 生效日
////                                          }
////                                      } else {// 上个月没到期
////                                          holdDays_remain = 30 - DateUtils.getDay(effectDate);// 持有天数 = 30 - 生效日
////                                          //holdDays_remain = DateUtils.datePhaseDiffer(effectDate, lastDayOfPreMonth);// 剩余部分持有天数 = 上个月月底 - 生效日
////                                      }
////                                      holdDays_trans = DateUtils.datePhaseDiffer(effectDate, transDate);// 转让部分持有天数 = 转让日 - 生效日
////                                  }
////                              } else {
////                                  // 上个月之前部分转让，只计算剩余部分
////                                  if (expireDate.compareTo(DateUtils.getMonthStartDate(now)) < 0) {// 上个月或上个月之前到期
////                                      if (expireDate.compareTo(firstDayOfPreMonth) >= 0) {// 上个月到期
////                                          holdDays_remain = DateUtils.datePhaseDiffer(firstDayOfPreMonth, expireDate);// 剩余部分持有天数 = 到期日 - 上个月月初
////                                      }
////                                  } else {// 上个月没到期
////                                      holdDays_remain = 30;// 剩余部分持有天数 = 30
////                                  }
////                              }
//                          // 剩余金额 = 存续金额*（1-转让比例）
//                          BigDecimal holdAmountRemain = ArithUtil.mul(holdAmount, ArithUtil.sub(new BigDecimal(1), transScale));
//                          // 转让金额 = 存续金额*转让比例
//                          BigDecimal holdAmountTrans = ArithUtil.mul(holdAmount, transScale);
//
//                          // 剩余部分佣金
//                          BigDecimal commissionRemain = ArithUtil.mul(
//                                  ArithUtil.mul(holdAmountRemain, commissionRate),
//                                  ArithUtil.div(new BigDecimal(holdDayMap.get("holdDays_remain")), oneYear));
//                          // 转让部分佣金
//                          BigDecimal commissionTrans = ArithUtil.mul(
//                                  ArithUtil.mul(holdAmountTrans, commissionRate),
//                                  ArithUtil.div(new BigDecimal(holdDayMap.get("holdDays_trans")), oneYear));
//                          commission = ArithUtil.add(commissionRemain, commissionTrans);
//
//                      }
//                      /**
//                       * 补贴计算
//                       *
//                       * 补贴 = 佣金 * 12% * (n-1)*30 / 360
//                       *
//                       * n表示到上一个月为止，共经过了几个月（生效月为一个整月）
//                       *
//                       */
//                      int n = DateUtils.monthPhaseDiffer(effectDate, firstDayOfPreMonth);// n = 上个月 - 生效月
//                      double div = ArithUtil.div(ArithUtil.mul(ArithUtil.sub((double) n, (double) 1),30), 360);
//                      BigDecimal mul = ArithUtil.mul(commission, new BigDecimal(0.12));
//                      BigDecimal subsidy = ArithUtil.mul(mul, new BigDecimal(div));
//
//                      // 佣金汇总
//                      commissionSum = ArithUtil.add(commissionSum, commission);
//                      // 补贴汇总
//                      subsidySum = ArithUtil.add(subsidySum, subsidy);
//                      // 团队总业绩：本次投资的业绩同时算作管理岗的一单投资业绩
//                      calcManagerInvest(ranking, investInfo, commissionMap, commission);
//                  }
//              }
//              // 汇总个人佣金
//              CommissionRateEntity entity = commissionMap.get(jobNo);
//              if (entity == null) {
//                  entity = new CommissionRateEntity();
//              }
//              if (ranking.equals("1")) {
//                  entity.setInvestAmount(ArithUtil.add(entity.getInvestAmount(), holdAmountSum));
//                  entity.setCommissionAmount(ArithUtil.add(entity.getCommissionAmount(), commissionSum));
//              } else {
//                  entity.setInvestAmountManager(ArithUtil.add(entity.getInvestAmountManager(), holdAmountSum));
//                  entity.setCommissionAmountManager(ArithUtil.add(entity.getCommissionAmountManager(), commissionSum));
//              }
//              entity.setSubsidyAmount(subsidySum);
//              entity.setCommissionRateTotal(commissionRateTotal);
//              entity.setCommissionRatePersonal(commissionRatePersonal);
//              entity.setCommissionRateGrant(commissionRateGrant);
//              entity.setRanking(ranking);
//              entity.setJobNo(jobNo);
//              commissionMap.put(jobNo, entity);
////              }
//          }
//      }
//      // 管理岗计算金额
//      calcManagerAllowance(commissionMap);
//  }

//  /**
//   * 本次投资的业绩也算作对应管理岗的业绩
//   *
//   * @param ranking       本次投资的业务员级别
//   * @param investInfo    投资信息
//   * @param commissionMap key:工号，value:提点信息对象
//   * @param commission    本次投资计算得到的业绩
//   */
//  private void calcManagerInvest(String ranking, CommissionSendBackEntity investInfo,
//                                 Map<String, CommissionRateEntity> commissionMap, BigDecimal commission) {
//      String jobNo_n = "";
//      for (int i = Integer.valueOf(ranking); i < 5; i++) {
//          switch (i) {
//              case 1:
//                  jobNo_n = investInfo.getRank_02_JobNo();
//                  break;
//              case 2:
//                  jobNo_n = investInfo.getRank_03_JobNo();
//                  break;
//              case 3:
//                  jobNo_n = investInfo.getRank_04_JobNo();
//                  break;
//              case 4:
//                  jobNo_n = investInfo.getRank_05_JobNo();
//                  break;
//          }
//          if (StringUtils.isNotEmpty(jobNo_n)) {
//              if ("010000277".equals(jobNo_n)) {
//                  System.out.println("");
//              }
//              CommissionRateEntity entity = commissionMap.get(jobNo_n);
//              if (entity == null) {
//                  entity = new CommissionRateEntity();
//              }
//              entity.setInvestAmount(ArithUtil.add(entity.getInvestAmount(), investInfo.getInvestAmount()));
//              entity.setHoldAmount(ArithUtil.add(entity.getHoldAmount(), investInfo.getHoldAmount()));
////              entity.setInvestAmountManager(ArithUtil.add(entity.getInvestAmountManager(), investInfo.getInvestAmount()));
////              entity.setHoldAmountManager(ArithUtil.add(entity.getHoldAmountManager(), investInfo.getHoldAmount()));
////              entity.setCommissionAmountManager(ArithUtil.add(entity.getCommissionAmountManager(), commission));
//              commissionMap.put(jobNo_n, entity);
//          }
//      }
//  }

//  /**
//   * 计算管理岗津贴，更新数据库
//   *
//   * @param commissionMap
//   */
//  private void calcManagerAllowance(Map<String, CommissionRateEntity> commissionMap) {
//      List<Object[]> list = Lists.newArrayList();// [下属总投资额， 下属总佣金， 团队总投资额， 管理岗佣金， 管理岗津贴， 最终佣金， 补贴， 工号]
//      for (String jobNo : commissionMap.keySet()) {
//          List<Object> arrToBeList = Lists.newArrayList();
//          CommissionRateEntity entity = commissionMap.get(jobNo);
//          String ranking = entity.getRanking();
//          BigDecimal subsidyAmount = entity.getSubsidyAmount();// 补贴
//          BigDecimal commissionRateGrant = entity.getCommissionRateGrant();// 提成发放比例
//          arrToBeList.add(CommonUtils.emptyToDecimal(entity.getInvestAmount()));// [0] 下属总投资额
//          arrToBeList.add(CommonUtils.emptyToDecimal(entity.getCommissionAmount()));// [1] 下属佣金
//          if ("010000277".equals(jobNo)) {
//              if (!ranking.equals("1")) {
//
//                  BigDecimal holdAmountManager = entity.getHoldAmountManager();// 管理岗业绩
//                  BigDecimal holdAmount = entity.getHoldAmount();// 下属业绩
//                  BigDecimal teamHoldAmount = ArithUtil.add(holdAmount, holdAmountManager);// 团队总业绩 = 下属业绩 + 管理岗业绩
//                  BigDecimal commissionRateTotal = entity.getCommissionRateTotal();// 总账提点
//                  // 津贴 = 团队总业绩*管理岗总账提点
//                  BigDecimal allowance = ArithUtil.mul(teamHoldAmount, commissionRateTotal);
//
//                  // 最终金额 = (管理岗个人佣金 + 津贴 + 补贴)*提成发放比例
//                  BigDecimal commissionAmountManager = entity.getCommissionAmountManager();// 管理岗个人佣金
//                  BigDecimal totalAmount = ArithUtil.mul(ArithUtil.add(ArithUtil.add(commissionAmountManager, allowance), subsidyAmount), commissionRateGrant);
//
//                  arrToBeList.add(CommonUtils.emptyToDecimal(entity.getInvestAmountManager()));// [2] 团队总投资额 = 下属总投资额 + 管理岗个人总投资额
//                  arrToBeList.add(CommonUtils.emptyToDecimal(commissionAmountManager));// [3] 管理岗佣金
//                  arrToBeList.add(CommonUtils.emptyToDecimal(allowance));// [4] 管理岗津贴
//                  arrToBeList.add(CommonUtils.emptyToDecimal(totalAmount));// [5] 最终佣金
//                  arrToBeList.add(0);// [6] 业务员业绩
//                  arrToBeList.add(CommonUtils.emptyToDecimal(entity.getHoldAmountManager()));// [7] 管理岗业绩
//              } else {
//                  BigDecimal commissionAmount = entity.getCommissionAmount();
//                  // 最终金额 = (佣金 + 补贴)*提成发放比例
//                  BigDecimal totalAmount = ArithUtil.mul(ArithUtil.add(commissionAmount, subsidyAmount), commissionRateGrant);
//
//                  arrToBeList.add(0);// [2] 团队总投资额 = 下属总投资额 + 管理岗个人总投资额
//                  arrToBeList.add(0);// [3] 管理岗佣金
//                  arrToBeList.add(0);// [4] 管理岗津贴
//                  arrToBeList.add(totalAmount);// [5] 最终佣金
//                  arrToBeList.add(CommonUtils.emptyToDecimal(entity.getHoldAmount()));// [6] 业务员业绩
//                  arrToBeList.add(0);// [7] 管理岗业绩
//              }
//              arrToBeList.add(subsidyAmount);// [8] 补贴
//              arrToBeList.add(jobNo);// [9] 工号
//              list.add(arrToBeList.toArray());
//          }
//      }
//
////      repository.updateAmount(list);
//  }
    
//  public ResultVo calcCommission(Map<String, Object> param) throws SLException {
//  try {
//      Map<String, CommissionRateEntity> commissionMap = Maps.newHashMap();
//      // 查询所有提点信息
//      List<CommissionRateEntity> rateList = repository.getAllCommissionRate();
//      for (CommissionRateEntity commissionRateEntity : rateList) {
//          commissionMap.put(commissionRateEntity.getJobNo(), commissionRateEntity);
//      }
//      List<CommissionSendBackEntity> investList = repository.getInvestInfoByJobNo(null);
//      List<String> error = Lists.newArrayList();
//      for (CommissionSendBackEntity investInfo : investList) {
////          if ("f099b78c-837c-4a2a-a13b-cf9270ed5a97".equals(investInfo.getInvestId())) {
//          String jobNo = investInfo.getJobNo();// 工号
//          String ranking = investInfo.getRanking();// 级别
//          if (!CommonUtils.isEmpty(ranking)) {
//              String memo = investInfo.getMemo();
//              String shopName = investInfo.getShopName();
//              BigDecimal investAmount = CommonUtils.emptyToDecimal(investInfo.getInvestAmount());// 入账金额
//              BigDecimal holdAmount = CommonUtils.emptyToDecimal(investInfo.getHoldAmount());// 业绩
//              String effectDateStr = investInfo.getEffectDate();// 投资生效日
//              Date effectDate = DateUtils.parseDate(effectDateStr, YYYY_MM_DD);
//              String transDateStr = investInfo.getTransDate();// 转让日期
//              BigDecimal transScale = CommonUtils.emptyToDecimal(investInfo.getTransScale());// 转让比例
//
//              CommissionRateEntity rateEntity = commissionMap.get(jobNo);
//              BigDecimal commissionRateTotal = rateEntity.getCommissionRateTotal();// 总账提点
//              BigDecimal commissionRatePersonal = rateEntity.getCommissionRatePersonal();// 个人提点
//
//
//              /** 1.计算做单人员的佣金和补贴 */
//              Date now = DateUtils.getMonthStartDate(new Date());
//              Date firstDayOfPreMonth = DateUtils.getMonthStartDate(DateUtils.getAfterMonth(now, -1));
//              BigDecimal commissionRate;
//              if (ranking.equals("1")) {
//                  // 业务员提点 = 个人提点+总账提点
//                  commissionRate = ArithUtil.add(commissionRatePersonal, commissionRateTotal);
//              } else {
//                  // 管理岗提点 = 0.02
//                  commissionRate = new BigDecimal("0.02");
//              }
//              /**
//               * 佣金   业务员 = 业绩*(总账提点+个人提点)
//               *        管理岗 = 业绩*2%
//               */
//              BigDecimal commission = ArithUtil.mul(holdAmount, commissionRate);
//              /**
//               * 补贴计算
//               *
//               * 补贴 = 佣金 * 12% * (n-1)*30 / 360
//               *
//               * n表示到上一个月为止，共经过了几个月（生效月为一个整月）
//               *
//               */
//              BigDecimal subsidy = new BigDecimal(0);
//              int n = DateUtils.monthPhaseDiffer(DateUtils.getMonthStartDate(effectDate), now);// n = 结算月 - 生效月
//              // “未转让”或者“转让发生在上个月”都计算上个月的补贴
//              if (transScale.compareTo(new BigDecimal(0)) == 0
//                      || (transScale.compareTo(new BigDecimal(0)) > 0)
//                      && DateUtils.parseDate(transDateStr, YYYY_MM_DD_HH_MM_SS).compareTo(firstDayOfPreMonth) >= 0
//                      && DateUtils.parseDate(transDateStr, YYYY_MM_DD_HH_MM_SS).compareTo(now) < 0) {
//                  if (n > 1) {
//                      double div = ArithUtil.div(ArithUtil.mul(ArithUtil.sub((double) n, (double) 1), 30), 360);
//                      BigDecimal mul = ArithUtil.mul(commission, new BigDecimal(0.12));
//                      subsidy = ArithUtil.mul(mul, new BigDecimal(div));
//                  }
//              }
//              if (ranking.equals("1")) {
//                  rateEntity.setInvestAmount(ArithUtil.add(rateEntity.getInvestAmount(), investAmount));// 单笔入账金额
//                  rateEntity.setHoldAmount(ArithUtil.add(rateEntity.getHoldAmount(), holdAmount));// 单笔业绩
//                  rateEntity.setCommissionAmount(ArithUtil.add(rateEntity.getCommissionAmount(), commission));// 单笔佣金
//              } else {
//                  rateEntity.setInvestAmountManager(ArithUtil.add(rateEntity.getInvestAmountManager(), investAmount));
//                  rateEntity.setHoldAmountManager(ArithUtil.add(rateEntity.getHoldAmountManager(), holdAmount));
//                  rateEntity.setCommissionAmountManager(ArithUtil.add(rateEntity.getCommissionAmountManager(), commission));
//                  BigDecimal allowance = getRateTotalByCondition(jobNo, memo, shopName, holdAmount, rateEntity);
//                  rateEntity.setAllowanceAmount(ArithUtil.add(rateEntity.getAllowanceAmount(), allowance));
//              }
//              rateEntity.setSubsidyAmount(ArithUtil.add(rateEntity.getSubsidyAmount(), subsidy));// 单笔补贴
//
//              /** 2.计算该笔投资对应的上级管理岗的津贴 */
//              calcAllowance(commissionMap, investInfo, commission);
//          } else {
//              error.add(jobNo);
//          }
////          }
//      }
//
//      /** 3.计算最终金额 */
//      for (String jobNo : commissionMap.keySet()) {
//          CommissionRateEntity rateEntity = commissionMap.get(jobNo);
//          String ranking = rateEntity.getRanking();
//          if (!CommonUtils.isEmpty(ranking)) {
//              BigDecimal commissionRateGrant = rateEntity.getCommissionRateGrant();// 提成发放比例
//              BigDecimal commissionAmount;// 业绩
//              BigDecimal subsidyAmount = rateEntity.getSubsidyAmount();// 补贴
//              BigDecimal allowanceAmount = new BigDecimal(0);// 津贴
//              if (ranking.equals("1")) {
//                  commissionAmount = rateEntity.getCommissionAmount();
//              } else {
//                  commissionAmount = rateEntity.getCommissionAmountManager();
//                  allowanceAmount = rateEntity.getAllowanceAmount();
//              }
//              // 最终发放金额 = (业绩 + 补贴 + 津贴（非管理岗为0）)*提成发放比例
//              rateEntity.setTotalAmount(ArithUtil.mul(ArithUtil.add(ArithUtil.add(commissionAmount, subsidyAmount), allowanceAmount), commissionRateGrant));
//          } else {
//              error.add(jobNo);
//          }
//      }
//
//      /** 4.批量保存 */
//      calcManagerAllowance(commissionMap, error);
//
//      return new ResultVo(true, "佣金计算成功");
//  } catch (Exception e) {
//      log.error("佣金计算失败" + e.getMessage());
//      return new ResultVo(false, "佣金计算失败", e);
//  }
//}
}
