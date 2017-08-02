package com.slfinance.shanlincaifu.repository.custom.impl;

import com.google.common.collect.Lists;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CommissionRateEntity;
import com.slfinance.shanlincaifu.entity.CommissionSendBackEntity;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.CalcCommissionRepositoryCustom;
import com.slfinance.shanlincaifu.utils.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Laurance on 2017/4/20.
 */
@Repository
public class CalcCommissionRepositoryCustomImpl implements CalcCommissionRepositoryCustom {

    @Autowired
    private RepositoryUtil repositoryUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    //    public List<Map<String, Object>> getInitInvestInfo(String startDate, String now) {
//        String sql = "with tt as ( " +
//                "select q.custId, q.quiltCustId, q.productId, q.investId, sum(q.investAmount) investAmount, q.LOAN_UNIT, q.LOAN_TERM, q.SEAT_TERM" +
//                "    from (select s.cust_id custId, s.quilt_cust_id quiltCustId, t.loan_id productId, t.id investId," +
//                "                 sum(t.INVEST_AMOUNT) investAmount, n.LOAN_UNIT, n.LOAN_TERM, n.SEAT_TERM" +
//                "            from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_loan_info n" +
//                "           where t.cust_id = s.quilt_cust_id" +
//                "             and t.loan_id = n.id" +
//                "             and t.loan_id is not null" +
//                "             and t.invest_status in ('收益中', '已到期', '提前结清', '已转让')" +
//                "             and n.REPAYMENT_METHOD != '等额本息'" +
//                "             and t.EFFECT_DATE >= ? and t.EFFECT_DATE < ?" +
//                "             and s.record_status = '有效'" +
//                "             and t.cust_id not in (select id from bao_t_cust_info where is_recommend = '是')" +
//                "           group by s.cust_id, s.quilt_cust_id, t.loan_id, t.id, n.LOAN_UNIT, n.LOAN_TERM, n.SEAT_TERM" +
//                "           union all" +
//                "          select t.cust_id custId, t.cust_id quiltCustId, t.loan_id productId, t.id investId, " +
//                "                 sum(t.INVEST_AMOUNT) investAmount, n.LOAN_UNIT, n.LOAN_TERM, n.SEAT_TERM" +
//                "            from bao_t_invest_info t, bao_t_loan_info n" +
//                "           where t.loan_id = n.id" +
//                "             and t.loan_id is not null" +
//                "             and n.REPAYMENT_METHOD != '等额本息'" +
//                "             and t.EFFECT_DATE >= ? and t.EFFECT_DATE < ?" +
//                "             and t.cust_id in (select id from bao_t_cust_info where is_recommend = '是')" +
//                "             and t.invest_status in ('收益中', '已到期', '提前结清', '已转让')" +
//                "           group by t.cust_id, t.loan_id, t.id, n.LOAN_UNIT, n.LOAN_TERM, n.SEAT_TERM) q " +
//                "           group by q.custId, q.quiltCustId, q.productId, q.investId, q.LOAN_UNIT, q.LOAN_TERM, q.SEAT_TERM" +
//                ") " +
//                "       select tt.investId," +
//                "       a.cust_name custName," +
//                "       c.cust_name managerName," +
//                "       tt.investAmount," +
//                "       tt.LOAN_UNIT loanUnit, " +
//                "       tt.LOAN_TERM loanTerm," +
//                "       tt.SEAT_TERM seatTerm," +
//                "       h.工号 jobNo," +
//                "       to_date(i.effect_date, 'yyyymmdd') effectDate," +
//                "       to_date(i.expire_date, 'yyyymmdd') expireDate," +
//                "       h.组织名称 shop," +
//                "       (select max(lt.create_date)" +
//                "          from bao_t_loan_transfer lt" +
//                "         inner join bao_t_wealth_hold_info hi on hi.id = lt.sender_hold_id" +
//                "         where hi.invest_id = tt.investId) transDate," +
//                "       (select case" +
//                "                 when nvl(hi.trade_scale / (hi.trade_scale + hi.hold_scale), 0) = 0 then ''" +
//                "                 else nvl(hi.trade_scale / (hi.trade_scale + hi.hold_scale), 0) || ''" +
//                "               end" +
//                "          from bao_t_wealth_hold_info hi" +
//                "         where hi.invest_id = tt.investId) transScale" +
//                "  from tt" +
//                " inner join bao_t_cust_info a on a.id = tt.quiltCustId" +
//                " inner join bao_t_cust_info c on c.id = tt.custId" +
//                " inner join bao_t_invest_info i on i.id = tt.investId" +
//                " inner join vw_bd_psndoc h on upper(h.证件号码) = upper(c.credentials_code)" +
//                " where i.EFFECT_DATE >= ? and i.EFFECT_DATE < ?" +
//                " order by i.create_date";
//
//        List<Object> args = Lists.newArrayList();
//        args.add(startDate);
//        args.add(now);
//        args.add(startDate);
//        args.add(now);
//        args.add(startDate);
//        args.add(now);
//        return jdbcTemplate.queryForList(sql, args.toArray());
//    }
    // 非等额本息
    public List<Map<String, Object>> getInitInvestInfo(String preMonth, String currMonth, String startDate, String now) {
        String sql = "select s.invest_id investId, a.cust_name custName, c.cust_name managerName, " +
                "        h.工号 jobNo, h.组织名称 shop," +
                "        s.invest_amount investAmount, " +
                "        to_date(i.effect_date, 'yyyymmdd') effectDate, to_date(i.expire_date, 'yyyymmdd') expireDate, " +
                "        td.transfer_date transDate," +
                "        cur.sum_trade_scale tradeScaleCurr, " +
                "        pre.sum_trade_scale tradeScalePre, " +
                "        w.hold_scale holdScale, " +
                "        l.loan_unit loanUnit, l.LOAN_TERM loanTerm, case when ta.id is null then l.seat_term else ta.transfer_seat_term end seatTerm" +
                " from bao_t_commission_info t " +
                " inner join bao_t_commission_detail_info s on t.id = s.commission_id " +
                " inner join bao_t_invest_info i on s.invest_id = i.id" +
                " inner join bao_t_cust_info a on a.id = s.quilt_cust_id" +
                " inner join bao_t_cust_info c on c.id = t.cust_id" +
                " inner join bao_t_loan_info l on l.id = i.loan_id" +
                " inner join bao_t_wealth_hold_info w on w.invest_id = i.id" +
                "  left join BAO_T_LOAN_TRANSFER_APPLY ta on ta.id = i.transfer_apply_id " +
                "  left join (select hi.invest_id, max(lt.create_date) transfer_date " +
                "               from slcf.bao_t_loan_transfer lt " +
                "              inner join slcf.bao_t_wealth_hold_info hi " +
                "                 on hi.id = lt.sender_hold_id and to_char(lt.create_date, 'yyyymm') = ? " +
                "              group by hi.invest_id ) td on td.invest_id = i.id " +
                "  left join (select hi.invest_id, sum(lt.trade_scale) sum_trade_scale " +
                "               from bao_t_loan_transfer lt " +
                "              inner join slcf.bao_t_wealth_hold_info hi " +
                "                 on hi.id = lt.sender_hold_id and to_char(lt.create_date, 'YYYYMM') = ? " +
                "              group by hi.invest_id) cur on cur.invest_id = i.id " +
                "  left join (select hi.invest_id, sum(lt.trade_scale) sum_trade_scale " +
                "               from bao_t_loan_transfer lt " +
                "              inner join slcf.bao_t_wealth_hold_info hi " +
                "                 on hi.id = lt.sender_hold_id and to_char(lt.create_date, 'YYYYMM') = ? " +
                "              group by hi.invest_id) pre on pre.invest_id = i.id " +
                " inner join vw_bd_psndoc h on upper(h.证件号码) = upper(c.credentials_code)" +
                " where i.effect_date >= ? and i.effect_date < ? and l.REPAYMENT_METHOD != '等额本息'" +
//                " and i.id in (" +
//                " '14b378da-a07f-46ae-b0e4-7cde52db788f'" +
//                ")" +
                " order by i.create_date";

        List<Object> args = Lists.newArrayList();
        args.add(preMonth);
        args.add(currMonth);
        args.add(preMonth);
        args.add(startDate);
        args.add(now);
        return jdbcTemplate.queryForList(sql, args.toArray());
    }

    @Override
    // 等额本息
    public List<Map<String, Object>> getInitInvestInfo2(String preMonth, String currMonth, String startDate, String now) {
        String sql = "SELECT t1.*, r.REPAYMENT_PRINCIPAL repaymentAmount, " +
                "r.REMAINDER_PRINCIPAL remainderPrincipal, r.REPAYMENT_STATUS repaymentStatus, r.EXPECT_REPAYMENT_DATE repayDate" +
                " from (" +
                "select s.invest_id investId, a.cust_name custName, c.cust_name managerName, " +
                "       h.工号 jobNo, h.组织名称 \"shop\"," +
                " s.invest_amount investAmount, " +
                " to_date(i.effect_date, 'yyyymmdd') effectDate, to_date(i.expire_date, 'yyyymmdd') expireDate, " +
                "  td.transfer_date transDate," +
                "  cur.sum_trade_scale tradeScaleCurr, " +
                "  pre.sum_trade_scale tradeScalePre " +
                ", w.hold_scale holdScale, i.loan_id, i.create_date, l.loan_unit loanUnit, l.loan_term loanTerm" +
                ", case when ta.id is null then l.seat_term else ta.transfer_seat_term end seatTerm" +
                ", i.invest_mode investMode" +
                " from bao_t_commission_info t " +
                " inner join bao_t_commission_detail_info s on t.id = s.commission_id " +
                " inner join bao_t_invest_info i on s.invest_id = i.id" +
                " inner join bao_t_cust_info a on a.id = s.quilt_cust_id" +
                " inner join bao_t_cust_info c on c.id = t.cust_id" +
                " inner join bao_t_loan_info l on l.id = i.loan_id" +
                " inner join bao_t_wealth_hold_info w on w.invest_id = i.id" +
                "  left join BAO_T_LOAN_TRANSFER_APPLY ta on ta.id = i.transfer_apply_id" +
                "  left join (select hi.invest_id, max(lt.create_date) transfer_date " +
                "               from slcf.bao_t_loan_transfer lt " +
                "              inner join slcf.bao_t_wealth_hold_info hi " +
                "                 on hi.id = lt.sender_hold_id and to_char(lt.create_date, 'yyyymm') = ? " +
                "              group by hi.invest_id ) td on td.invest_id = i.id " +
                "  left join (select hi.invest_id, sum(lt.trade_scale) sum_trade_scale " +
                "               from bao_t_loan_transfer lt " +
                "              inner join slcf.bao_t_wealth_hold_info hi " +
                "                 on hi.id = lt.sender_hold_id and to_char(lt.create_date, 'YYYYMM') = ? " +
                "              group by hi.invest_id) cur on cur.invest_id = i.id " +
                "  left join (select hi.invest_id, sum(lt.trade_scale) sum_trade_scale " +
                "               from bao_t_loan_transfer lt " +
                "              inner join slcf.bao_t_wealth_hold_info hi " +
                "                 on hi.id = lt.sender_hold_id and to_char(lt.create_date, 'YYYYMM') = ? " +
                "              group by hi.invest_id) pre on pre.invest_id = i.id " +
                " inner join vw_bd_psndoc h on upper(h.证件号码) = upper(c.credentials_code)" +
                " where i.effect_date >= ? and i.effect_date < ? and l.REPAYMENT_METHOD = '等额本息' " +
//                " and i.id in (" +
//                " '1722b608-f7be-4e00-8bba-7d0530b24c4d'" +
//                ")" +
                ") t1" +
                " LEFT JOIN BAO_T_REPAYMENT_PLAN_INFO r on t1.loan_id = r.loan_id " +
                " and substr(r.EXPECT_REPAYMENT_DATE, 0, 6) = ?" +
                " order by t1.create_date";

        List<Object> args = Lists.newArrayList();
        args.add(preMonth);
        args.add(currMonth);
        args.add(preMonth);
        args.add(startDate);
        args.add(now);
        args.add(preMonth);
        return jdbcTemplate.queryForList(sql, args.toArray());
    }

    @Override
    // <不可转让>投资信息
    public List<Map<String, Object>> getUnTransferableInvestInfo(String startDate, String endDate) {
        String sql = "select t1.* from (" +
                "select s.invest_id investId, " +
                "       a.cust_name custName, " +
                "       c.cust_name managerName, " +
                "       l.repayment_method repaymentMethod, " +
//                "       h.工号 jobNo, h.组织名称 shop," +
                "       s.invest_amount investAmount, " +
                "       i.loan_id, " +
                "       l.loan_unit loanUnit, " +
                "       l.loan_term loanTerm, " +
                "       i.invest_mode investMode, " +
                "       to_date(i.effect_date, 'yyyymmdd') effectDate, " +
                "       to_date(i.expire_date, 'yyyymmdd') expireDate, " +
                "       (select max(lt.create_date) " +
                "          from bao_t_loan_transfer lt " +
                "         inner join bao_t_wealth_hold_info hi on hi.id = lt.sender_hold_id " +
                "         where hi.invest_id = s.invest_id) transDate, " +
                "       (select case when nvl(hi.trade_scale / (hi.trade_scale + hi.hold_scale), 0) = 0 then '' " +
                "                    else nvl(hi.trade_scale / (hi.trade_scale + hi.hold_scale), 0) || '' end" +
                "          from bao_t_wealth_hold_info hi " +
                "         where hi.invest_id = s.invest_id and hi.trade_scale + hi.hold_scale != 0) transScale, " +
                "       case when ta.id is null then l.seat_term else ta.transfer_seat_term end seatTerm" +
                "  from bao_t_commission_info t " +
                " inner join bao_t_commission_detail_info s on t.id = s.commission_id " +
                " inner join bao_t_invest_info i on s.invest_id = i.id " +
                " inner join bao_t_cust_info a on a.id = s.quilt_cust_id " +
                " inner join bao_t_cust_info c on c.id = t.cust_id " +
                " inner join bao_t_loan_info l on l.id = i.loan_id " +
                "  left join bao_t_loan_transfer_apply ta on ta.id = i.transfer_apply_id " +
//                " inner join vw_bd_psndoc h on upper(h.证件号码) = upper(c.credentials_code) " +
                " where i.effect_date >= ? and i.effect_date < ?) t1 " +
                "where t1.seatTerm = '-1' " +
                "order by t1.effectDate ";

        List<Object> args = Lists.newArrayList();
        args.add(startDate);
        args.add(endDate);
        return jdbcTemplate.queryForList(sql, args.toArray());
    }

    @Override
    // <可转让>投资信息
    public List<Map<String, Object>> getTransferableInvestInfo(String preMonth, String startDate, String endDate) {
        String sql = "select t1.* from ( " +
                "select s.invest_id investId, " +
                "       a.cust_name custName, " +
                "       c.cust_name managerName, " +
                "       l.repayment_method repaymentMethod, " +
//                "       h.工号 jobNo, h.组织名称 shop, " +
                "       s.invest_amount investAmount, " +
                "       i.loan_id, " +
                "       l.loan_unit loanUnit, " +
                "       l.loan_term loanTerm, " +
                "       i.invest_mode investMode, " +
                "       to_date(i.effect_date, 'yyyymmdd') effectDate, " +
                "       to_date(i.expire_date, 'yyyymmdd') expireDate, " +
                "       (select max(lt.create_date) " +
                "          from bao_t_loan_transfer lt " +
                "         inner join bao_t_wealth_hold_info hi on hi.id = lt.sender_hold_id " +
                "         where hi.invest_id = s.invest_id) transDate, " +
                "       (select case when nvl(hi.trade_scale / (hi.trade_scale + hi.hold_scale), 0) = 0 then '' " +
                "                    else nvl(hi.trade_scale / (hi.trade_scale + hi.hold_scale), 0) || '' end " +
                "          from bao_t_wealth_hold_info hi " +
                "         where hi.invest_id = s.invest_id " +
                "           and hi.trade_scale + hi.hold_scale != 0) transScale, " +
                "       case when ta.id is null then l.seat_term else ta.transfer_seat_term end seatTerm, " +
                "       r.repayment_principal repaymentAmount, " +
                "       r.remainder_principal remainderPrincipal, " +
                "       r.repayment_status repaymentStatus, " +
                "       r.expect_repayment_date repayDate, " +
                "       w.hold_scale holdScale " +
                "  from bao_t_commission_info t " +
                " inner join bao_t_commission_detail_info s on t.id = s.commission_id " +
                " inner join bao_t_invest_info i on s.invest_id = i.id " +
                " inner join bao_t_cust_info a on a.id = s.quilt_cust_id " +
                " inner join bao_t_cust_info c on c.id = t.cust_id " +
                " inner join bao_t_loan_info l on l.id = i.loan_id " +
                "  left join bao_t_loan_transfer_apply ta on ta.id = i.transfer_apply_id " +
                "  left join bao_t_repayment_plan_info r on l.id = r.loan_id and substr(r.expect_repayment_date, 0, 6) = ? " +
                " inner join bao_t_wealth_hold_info w on i.id = w.invest_id" +
//                " inner join vw_bd_psndoc h on upper(h.证件号码) = upper(c.credentials_code) " +
                " where i.effect_date >= ? and i.effect_date < ?) t1 " +
                "where t1.seatTerm != '-1' " +
                "order by t1.effectDate";
        List<Object> args = Lists.newArrayList();
        args.add(preMonth);
        args.add(startDate);
        args.add(endDate);
        return jdbcTemplate.queryForList(sql, args.toArray());
    }
//    public List<Map<String, Object>> getInitInvestInfo2(String startDate, String preMonth, String now) {
//        String sql = "with tt as ( " +
//                "SELECT t1.*, r.REPAYMENT_PRINCIPAL, r.REMAINDER_PRINCIPAL, r.REPAYMENT_STATUS, r.EXPECT_REPAYMENT_DATE from (" +
//                "   select q.custId, q.quiltCustId, q.productId, q.investId, sum(q.investAmount) investAmount, " +
//                "          q.holdScale, q.LOAN_UNIT loanUnit, q.LOAN_TERM loanTerm, q.SEAT_TERM seatTerm" +
//                "       from (select s.cust_id custId, s.quilt_cust_id quiltCustId, t.loan_id productId, " +
//                "                    t.id investId, sum(t.invest_amount) investAmount, w.hold_scale holdScale, n.LOAN_UNIT, n.LOAN_TERM, n.SEAT_TERM" +
//                "            from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_loan_info n, bao_t_wealth_hold_info w" +
//                "           where t.cust_id = s.quilt_cust_id" +
//                "             and t.loan_id = n.id" +
//                "             and t.loan_id = w.loan_id" +
//                "             and t.id = w.invest_id" +
//                "             and t.loan_id is not null" +
//                "             and t.invest_status in ('收益中', '已到期', '提前结清', '已转让')" +
//                "             and n.REPAYMENT_METHOD = '等额本息'" +
//                "             and t.expire_date >= ?" +
//                "             and s.record_status = '有效'" +
//                "             and t.cust_id not in (select id from bao_t_cust_info where is_recommend = '是')" +
//                "           group by s.cust_id, s.quilt_cust_id, t.loan_id, t.id, w.hold_scale, n.LOAN_UNIT, n.LOAN_TERM, n.SEAT_TERM" +
//                "           union all" +
//                "          select t.cust_id custId, t.cust_id quiltCustId, t.loan_id productId, " +
//                "                 t.id investId, sum(t.INVEST_AMOUNT) investAmount, w.hold_scale holdScale, n.LOAN_UNIT, n.LOAN_TERM, n.SEAT_TERM" +
//                "            from bao_t_invest_info t, bao_t_loan_info n, bao_t_wealth_hold_info w" +
//                "           where t.loan_id = n.id" +
//                "             and t.loan_id = w.loan_id" +
//                "             and t.id = w.invest_id" +
//                "             and t.loan_id is not null" +
//                "             and n.REPAYMENT_METHOD = '等额本息'" +
//                "             and t.expire_date >= ?" +
//                "             and t.cust_id in (select id from bao_t_cust_info where is_recommend = '是')" +
//                "             and t.invest_status in ('收益中', '已到期', '提前结清', '已转让')" +
//                "           group by t.cust_id, t.loan_id, t.id, w.hold_scale, n.LOAN_UNIT, n.LOAN_TERM, n.SEAT_TERM) q" +
//                "           group by q.custId, q.quiltCustId, q.productId, q.investId, q.holdScale, q.LOAN_UNIT, q.LOAN_TERM, q.SEAT_TERM" +
//                ") t1 LEFT JOIN bao_t_repayment_plan_info r on t1.productId = r.LOAN_ID and substr(r.EXPECT_REPAYMENT_DATE, 0, 6) = ?" +
//                ")" +
//                "select tt.investId," +
//                "       a.cust_name custName," +
//                "       c.cust_name managerName," +
//                "       tt.investAmount," +
//                "       tt.REPAYMENT_PRINCIPAL repaymentAmount," +
//                "       tt.REMAINDER_PRINCIPAL remainderPrincipal," +
//                "       tt.REPAYMENT_STATUS repaymentStatus," +
//                "       tt.holdScale, tt.loanUnit, tt.loanTerm, tt.seatTerm," +
//                "       tt.EXPECT_REPAYMENT_DATE \"repayDate\"," +
//                "        h.工号 jobNo," +
//                "       to_date(i.effect_date, 'yyyymmdd') effectDate," +
//                "       to_date(i.expire_date, 'yyyymmdd') expireDate," +
//                "        h.组织名称 shop," +
//                "       (select max(lt.create_date)" +
//                "          from bao_t_loan_transfer lt" +
//                "         inner join bao_t_wealth_hold_info hi on hi.id = lt.sender_hold_id" +
//                "         where hi.invest_id = tt.investId) transDate," +
//                "       (select case" +
//                "                 when nvl(hi.trade_scale / (hi.trade_scale + hi.hold_scale), 0) = 0 then ''" +
//                "                 else nvl(hi.trade_scale / (hi.trade_scale + hi.hold_scale), 0) || ''" +
//                "               end" +
//                "          from bao_t_wealth_hold_info hi" +
//                "         where hi.invest_id = tt.investId) transScale" +
//                "  from tt" +
//                " inner join bao_t_cust_info a on a.id = tt.quiltCustId" +
//                " inner join bao_t_cust_info c on c.id = tt.custId" +
//                " inner join bao_t_invest_info i on i.id = tt.investId" +
//                " inner join vw_bd_psndoc h on upper(h.证件号码) = upper(c.credentials_code)" +
//                " where i.expire_date >= ? and i.effect_date < ?" +
//                " order by i.create_date";
//
//        List<Object> args = Lists.newArrayList();
//        args.add(startDate);
//        args.add(startDate);
//        args.add(preMonth);
//        args.add(startDate);
//        args.add(now);
//        return jdbcTemplate.queryForList(sql, args.toArray());
//    }

    @Override
    public Integer getTotalCountOfInvest(String startDate, String preMonth, String endDate) {
        String sql = "select count(1) from (" +
                " select s.invest_id \"investId\", a.cust_name \"custName\", c.cust_name \"managerName\", " +
//                "        h.工号 \"jobNo\", h.组织名称 \"shop\", " +
                "        s.invest_amount \"investAmount\", " +
                "        to_date(i.effect_date, 'yyyymmdd') \"effectDate\", to_date(i.expire_date, 'yyyymmdd') \"expireDate\"," +
                "        (select max(lt.create_date) " +
                "          from bao_t_loan_transfer lt inner join bao_t_wealth_hold_info hi on hi.id = lt.sender_hold_id" +
                "         where hi.invest_id = s.invest_id) \"transDate\"," +
                "        (select case when nvl(hi.trade_scale / (hi.trade_scale + hi.hold_scale), 0) = 0 then ''" +
                "                     else nvl(hi.trade_scale / (hi.trade_scale + hi.hold_scale), 0) || '' end" +
                "          from bao_t_wealth_hold_info hi where hi.invest_id = s.invest_id) \"transScale\"" +
                "        , i.loan_id, i.create_date, l.loan_unit \"loanUnit\", l.loan_term \"loanTerm\"" +
                "        , case when ta.id is null then l.seat_term else ta.transfer_seat_term end \"seatTerm\"" +
                "        , l.repayment_method \"repaymentMethod\"" +
                "   from bao_t_commission_info t" +
                "  inner join bao_t_commission_detail_info s on t.id = s.commission_id" +
                "  inner join bao_t_invest_info i on s.invest_id = i.id" +
                "  inner join bao_t_cust_info a on a.id = s.quilt_cust_id" +
                "  inner join bao_t_cust_info c on c.id = t.cust_id" +
                "  inner join bao_t_loan_info l on l.id = i.loan_id" +
                "   left join bao_t_loan_transfer_apply ta on ta.id = i.transfer_apply_id" +
//                "  inner join vw_bd_psndoc h on upper(h.证件号码) = upper(c.credentials_code)" +
                " where i.effect_date >= ? and i.effect_date < ?) t1" +
                " left join bao_t_repayment_plan_info r on t1.loan_id = r.loan_id and substr(r.expect_repayment_date, 0, 6) = ?"
                ;
        List<Object> args = Lists.newArrayList();
        args.add(startDate);
        args.add(endDate);
        args.add(preMonth);
        return jdbcTemplate.queryForObject(sql, args.toArray(), Integer.class);
    }

    @Override
    public List<Map<String, Object>> getInitInvestInfo3(String startDate, String preMonth, String endDate, Integer begin, Integer end) {
        String sql = "select * from (" +
                "select t1.*, r.repayment_principal \"repaymentAmount\", r.remainder_principal \"remainderPrincipal\", " +
                "       r.repayment_status \"repaymentStatus\", r.expect_repayment_date \"repayDate\", ROWNUM RN from (" +
                " select s.invest_id \"investId\", a.cust_name \"custName\", c.cust_name \"managerName\", " +
//                "        h.工号 \"jobNo\", h.组织名称 \"shop\", " +
                "        s.invest_amount \"investAmount\", " +
                "        to_date(i.effect_date, 'yyyymmdd') \"effectDate\", to_date(i.expire_date, 'yyyymmdd') \"expireDate\"," +
                "        (select max(lt.create_date) " +
                "          from bao_t_loan_transfer lt inner join bao_t_wealth_hold_info hi on hi.id = lt.sender_hold_id" +
                "         where hi.invest_id = s.invest_id) \"transDate\"," +
                "        (select case when nvl(hi.trade_scale / (hi.trade_scale + hi.hold_scale), 0) = 0 then ''" +
                "                     else nvl(hi.trade_scale / (hi.trade_scale + hi.hold_scale), 0) || '' end" +
                "          from bao_t_wealth_hold_info hi where hi.invest_id = s.invest_id) \"transScale\"" +
                "        , i.loan_id, i.create_date, l.loan_unit \"loanUnit\", l.loan_term \"loanTerm\"" +
                "        , case when ta.id is null then l.seat_term else ta.transfer_seat_term end \"seatTerm\"" +
                "        , l.repayment_method \"repaymentMethod\"" +
                "   from bao_t_commission_info t" +
                "  inner join bao_t_commission_detail_info s on t.id = s.commission_id" +
                "  inner join bao_t_invest_info i on s.invest_id = i.id" +
                "  inner join bao_t_cust_info a on a.id = s.quilt_cust_id" +
                "  inner join bao_t_cust_info c on c.id = t.cust_id" +
                "  inner join bao_t_loan_info l on l.id = i.loan_id" +
                "   left join bao_t_loan_transfer_apply ta on ta.id = i.transfer_apply_id" +
//                "  inner join vw_bd_psndoc h on upper(h.证件号码) = upper(c.credentials_code)" +
                " where i.effect_date >= ? and i.effect_date < ?) t1" +
                " left join bao_t_repayment_plan_info r on t1.loan_id = r.loan_id and substr(r.expect_repayment_date, 0, 6) = ?" +
                " where ROWNUM <= ? ) where RN > ?"
                ;
        List<Object> args = Lists.newArrayList();
        args.add(startDate);
        args.add(endDate);
        args.add(preMonth);
        args.add(end);
        args.add(begin);
        return jdbcTemplate.queryForList(sql, args.toArray());
    }

    @Override
    public List<Map<String, Object>> getInvestValue(String investId, String currMonth) {
//        investId = "b2bc966c-6fbe-420c-a8fe-099b322b31f1";
//        currMonth = "201704";

        String sql = "SELECT C.LOAN_ID," +
                "       C.VALUE_REPAYMENT_BEFORE \"valueBefore\"," +
                "       C.VALUE_REPAYMENT_AFTER \"valueAfter\"," +
                "       R.REPAYMENT_STATUS \"repaymentStatus\"" +
                "  FROM bao_t_CREDIT_RIGHT_VALUE C, bao_t_REPAYMENT_PLAN_INFO R" +
                " WHERE C.LOAN_ID = R.LOAN_ID" +
                "   AND C.LOAN_ID =" +
                "       (SELECT T.LOAN_ID" +
                "          FROM bao_t_INVEST_INFO T" +
                "         WHERE T.ID = ?)" +
                "   AND SUBSTR(C.VALUE_DATE, 1, 6) = ?" +
                "   AND SUBSTR(R.EXPECT_REPAYMENT_DATE, 1, 6) = ?" +
                "   AND C.VALUE_REPAYMENT_AFTER != C.VALUE_REPAYMENT_BEFORE" +
                " GROUP BY C.LOAN_ID," +
                "          C.VALUE_DATE," +
                "          C.VALUE_REPAYMENT_BEFORE," +
                "          C.VALUE_REPAYMENT_AFTER," +
                "          R.REPAYMENT_STATUS";
        return jdbcTemplate.queryForList(sql, new Object[]{investId, currMonth, currMonth});
    }

    @Override
    public void deleteSavedInCurrentMonth() {
        // 保存之前先删除本月已经保存过的数据，避免重复保存
        String currMonth = DateUtils.formatDate(new Date(), "yyyy-MM");
        String sql = "DELETE FROM bao_t_COMMISSION_PERFORMANCE T WHERE TO_CHAR(T.CREATE_DATE, 'YYYY-MM') = ?";
        jdbcTemplate.update(sql, new Object[]{currMonth});
    }

    @Override
    public void batchUpdatePerformance(List<Object[]> update) {
        String sql = "INSERT INTO bao_t_COMMISSION_PERFORMANCE" +
                "  (ID, INVEST_ID," +
                "   CUST_NAME," +
                "   MANAGER_NAME," +
                "   JOB_NO," +
                "   INVEST_AMOUNT," +
                "   PERFORMANCE_AMOUNT," +
                "   EFFECT_DATE," +
                "   EXPIRE_DATE," +
                "   SHOP," +
                "   TRANS_DATE," +
                "   TRANS_SCALE," +
                "   ACPI," +
                "   TRANSFUL)" +
                "VALUES" +
                "  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, update);
    }

    @Override
    public List<CommissionRateEntity> getAllCommissionRate() {
    	
        String sql = "SELECT * FROM BAO_T_COMMISSION_RATE T WHERE T.RANKING IS NOT NULL " +
//                "AND JOB_NO = '010001523' " +
                "ORDER BY RATE_MONTH, T.RANKING, T.JOB_NO";
        return repositoryUtil.queryForList(sql, CommissionRateEntity.class);
    }

    @Override
    public List<CommissionSendBackEntity> getInvestInfoByJobNo(String jobNo) {
        String sql = "SELECT * FROM BAO_T_COMMISSION_SENDBACK T";
        if (!CommonUtils.isEmpty(jobNo)) {
            sql += " WHERE T.JOB_NO = ? AND TO_CHAR(T.CREATE_DATE, 'yyyymm') = ? ORDER BY JOB_NO";
//            sql += " WHERE T.RANK_02_JOBNO = ? AND TO_CHAR(T.CREATE_DATE, 'yyyymm') = ? ORDER BY JOB_NO";
            return repositoryUtil.queryForList(sql, new Object[]{jobNo, DateUtils.getCurrentDate("yyyyMM")}, CommissionSendBackEntity.class);
        } else {
            sql += " WHERE TO_CHAR(T.CREATE_DATE, 'yyyymm') = ? ORDER BY JOB_NO";
            return repositoryUtil.queryForList(sql, new Object[]{DateUtils.getCurrentDate("yyyyMM")}, CommissionSendBackEntity.class);
        }
    }

    @Override
    public void updateAmount(List<Object[]> list) {
//        String currMonth = DateUtils.formatDate(new Date(), "yyyy-MM");
//        String sql = "UPDATE BAO_T_COMMISSION_RATE T " +
//                " SET T.COMMISSION_AMOUNT= ''," +
//                " T.INVEST_AMOUNT= ''," +
//                " T.COMMISSION_AMOUNT_MANAGER = ''," +
//                " T.INVEST_AMOUNT_MANAGER = ''," +
//                " T.ALLOWANCE_AMOUNT= ''," +
//                " T.TOTAL_AMOUNT= ''," +
//                " T.SUBSIDY_AMOUNT= ''," +
//                " T.HOLD_AMOUNT= ''," +
//                " T.HOLD_AMOUNT_MANAGER = ''" +
//                "WHERE TO_CHAR(T.UPDATE_DATE, 'YYYY-MM') = ?";
//        jdbcTemplate.update(sql, new Object[]{currMonth});
//
//        sql = "UPDATE BAO_T_COMMISSION_RATE T " +
//                " SET T.INVEST_AMOUNT = ?" +
//                ", T.COMMISSION_AMOUNT = ?" +
//                ", T.INVEST_AMOUNT_MANAGER = ?" +
//                ", T.COMMISSION_AMOUNT_MANAGER = ?" +
//                ", T.ALLOWANCE_AMOUNT = ?" +
//                ", T.TOTAL_AMOUNT = ?" +
//                ", T.HOLD_AMOUNT = ?" +
//                ", T.HOLD_AMOUNT_MANAGER = ?" +
//                ", T.SUBSIDY_AMOUNT = ?" +
//                ", T.UPDATE_DATE = SYSDATE" +
//                " WHERE T.JOB_NO = ?";
//        jdbcTemplate.batchUpdate(sql, list);
    	String sql  = "DELETE FROM BAO_T_COMMISSION_FINAL WHERE TO_CHAR(CREATE_DATE, 'YYYYMM') = ?";
    	jdbcTemplate.update(sql, new Object[]{DateUtils.getCurrentDate("yyyyMM")});
    	
        sql = "INSERT INTO BAO_T_COMMISSION_FINAL (" +
                "ID, JOB_NO, CUST_NAME, INVEST_AMOUNT, HOLD_AMOUNT, COMMISSION_AMOUNT, " +
                "INVEST_AMOUNT_MANAGER, HOLD_AMOUNT_MANAGER, COMMISSION_AMOUNT_MANAGER, " +
                "ALLOWANCE_AMOUNT, SUBSIDY_AMOUNT, TOTAL_AMOUNT, CREATE_DATE) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, list);
    }

    @Override
    public List<Map<String, Object>> getMaxTransDatePreMonth(String investId, String preMonth) {
        String sql = "select max(lt.create_date) \"maxTransDate\"" +
                "          from bao_t_loan_transfer lt" +
                "         inner join bao_t_wealth_hold_info hi on hi.id = lt.sender_hold_id" +
                "         where hi.invest_id = ?" +
                "and to_char(lt.create_date, 'YYYYMM') <= ?";
        return jdbcTemplate.queryForList(sql, new Object[]{investId, preMonth});
    }

    @Override
    public BigDecimal queryHoldScalePreMonth(String investId, String preMonth) {
        List<Object> args = Lists.newArrayList();
        String sql = "" +
//                "SELECT TT.TRADE_SCALE FROM (" +
                "SELECT SUM(T.TRADE_SCALE) FROM bao_t_WEALTH_HOLD_INFO W, bao_t_LOAN_TRANSFER T " +
                " WHERE W.ID = T.SENDER_HOLD_ID AND W.INVEST_ID = ? ";
        args.add(investId);
        if (!CommonUtils.isEmpty(preMonth)) {
            sql += " AND TO_CHAR(T.CREATE_DATE, 'YYYYMM') = ? ";
            args.add(preMonth);
        }
//         sql += " ORDER BY T.CREATE_DATE DESC ) TT WHERE ROWNUM = 1";
        BigDecimal holdScale = jdbcTemplate.queryForObject(sql, args.toArray(), BigDecimal.class);
        return holdScale;
    }

    @Override
    public Page<Map<String, Object>> queryPerformanceList(Map<String, Object> param) {
        SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
                .addSql("SELECT T.INVEST_ID \"investId\", T.CUST_NAME \"custName\", T.MANAGER_NAME \"managerName\", ")
                .addSql("T.JOB_NO \"jobNo\", TRUNC(NVL(T.INVEST_AMOUNT, 0), 2) \"investAmount\", TRUNC(NVL(T.PERFORMANCE_AMOUNT, 0), 2) \"performanceAmount\",")
                .addSql("to_char(T.EFFECT_DATE, 'YYYY/MM/DD') \"effectDate\", to_char(T.EXPIRE_DATE, 'YYYY/MM/DD') \"expireDate\", T.SHOP \"shop\", ")
                .addSql(" CASE WHEN T.TRANS_DATE IS NULL THEN '' ELSE TO_CHAR(T.TRANS_DATE, 'YYYY/MM/DD HH24:MI:SS') END \"transDate\",")
                .addSql(" CASE WHEN T.TRANS_SCALE IS NULL THEN '' ELSE TRUNC(T.TRANS_SCALE, 2) * 100 || '%' END \"transScale\"")
                .addSql("FROM bao_t_COMMISSION_PERFORMANCE T ORDER BY T.EFFECT_DATE")
                ;
        return repositoryUtil.queryForPageMap(sqlCon.toString(), sqlCon.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
    }

    @Override
    public int batchSaveConstruct(final List<Map<String, Object>> params) throws SLException {
        // 保存之前先删除本月已经保存过的数据，避免重复保存
        String currMonth = DateUtils.formatDate(new Date(), "yyyy-MM");
//        String sql1 = "DELETE FROM BAO_T_COMMISSION_SENDBACK T WHERE TO_CHAR(T.CREATE_DATE, 'YYYY-MM') = ?";
//        jdbcTemplate.update(sql1, new Object[]{currMonth});

        StringBuffer sql = new StringBuffer()
                .append("INSERT INTO BAO_T_COMMISSION_SENDBACK ")
                .append("(INVEST_ID, CUST_NAME, MANAGER_NAME, JOB_NO, RANKING_NAME, RANKING,")
                .append("INVEST_AMOUNT, HOLD_AMOUNT, EFFECT_DATE, EXPIRE_DATE, SHOP_NAME, ")
                .append("TRANS_DATE, TRANS_SCALE, RANK_02_JOBNO, RANK_02_NAME, RANK_03_JOBNO, RANK_03_NAME, ")
                .append("RANK_04_JOBNO, RANK_04_NAME, RANK_05_JOBNO, RANK_05_NAME, RANK_06_JOBNO, RANK_06_NAME, ")
                .append("RANK_07_JOBNO, RANK_07_NAME, RANK_08_JOBNO, RANK_08_NAME, RANK_09_JOBNO, RANK_09_NAME, MEMO, ")
                .append("CREATE_DATE, ID, MAKE_UP) ")
                .append("VALUES (?, ?, ?, ?, ?, ?,")// 1-6
                .append("?, ?, ?, ?, ?,")// 7-11
                .append("?, ?, ?, ?, ?, ?,")// 12-17
                .append("?, ?, ?, ?, ?, ?,")// 18-23
                .append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")// 24-32
                ;

        int[] count = jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
                public void setValues(PreparedStatement ps, int i) throws SQLException {

                    Timestamp effectDate = null, expireDate = null, transDate = null;
                    if (!CommonUtils.isEmpty(params.get(i).get("effectDate"))) {
                        effectDate = new Timestamp(DateUtils.parseDate((String) params.get(i).get("effectDate"), "yyyy-MM-dd").getTime());
                    }
                    if (!CommonUtils.isEmpty(params.get(i).get("expireDate"))) {
                        expireDate = new Timestamp(DateUtils.parseDate((String) params.get(i).get("expireDate"), "yyyy-MM-dd").getTime());
                    }
                    if (!CommonUtils.isEmpty(params.get(i).get("transDate"))) {
                        transDate = new Timestamp(DateUtils.parseDate((String) params.get(i).get("transDate"), "yyyy-MM-dd").getTime());
                    }
                    BigDecimal transScaleBig = null;
                    if (!CommonUtils.isEmpty(params.get(i).get("transScale"))) {
                        String transScale = (String) params.get(i).get("transScale");
    //                    transScale = transScale.replace("%", "");
    //                    Float f = Float.valueOf(transScale);
                        transScaleBig = new BigDecimal(transScale);
                    }

                    ps.setString(1, CommonUtils.emptyToString(params.get(i).get("investId")));
                    ps.setString(2, CommonUtils.emptyToString(params.get(i).get("custName")));
                    ps.setString(3, CommonUtils.emptyToString(params.get(i).get("managerName")));
                    ps.setString(4, CommonUtils.emptyToString(params.get(i).get("jobNo")));
                    ps.setString(5, CommonUtils.emptyToString(params.get(i).get("rankingName")));
                    ps.setString(6, "");
                    ps.setString(7, CommonUtils.emptyToString(params.get(i).get("investAmount")));
                    ps.setString(8, CommonUtils.emptyToString(params.get(i).get("holdAmount")));
                    ps.setTimestamp(9, effectDate);
                    ps.setTimestamp(10, expireDate);
                    ps.setString(11, CommonUtils.emptyToString(params.get(i).get("shop")));
                    ps.setTimestamp(12, transDate);
                    ps.setBigDecimal(13, transScaleBig);
                    ps.setString(14, CommonUtils.emptyToString(params.get(i).get("rank_02_JobNo")));
                    ps.setString(15, CommonUtils.emptyToString(params.get(i).get("rank_02_Name")));
                    ps.setString(16, CommonUtils.emptyToString(params.get(i).get("rank_03_JobNo")));
                    ps.setString(17, CommonUtils.emptyToString(params.get(i).get("rank_03_Name")));
                    ps.setString(18, CommonUtils.emptyToString(params.get(i).get("rank_04_JobNo")));
                    ps.setString(19, CommonUtils.emptyToString(params.get(i).get("rank_04_Name")));
                    ps.setString(20, CommonUtils.emptyToString(params.get(i).get("rank_05_JobNo")));
                    ps.setString(21, CommonUtils.emptyToString(params.get(i).get("rank_05_Name")));
                    ps.setString(22, CommonUtils.emptyToString(params.get(i).get("rank_06_JobNo")));
                    ps.setString(23, CommonUtils.emptyToString(params.get(i).get("rank_06_Name")));
                    ps.setString(24, CommonUtils.emptyToString(params.get(i).get("rank_07_JobNo")));
                    ps.setString(25, CommonUtils.emptyToString(params.get(i).get("rank_07_Name")));
                    ps.setString(26, CommonUtils.emptyToString(params.get(i).get("rank_08_JobNo")));
                    ps.setString(27, CommonUtils.emptyToString(params.get(i).get("rank_08_Name")));
                    ps.setString(28, CommonUtils.emptyToString(params.get(i).get("rank_09_JobNo")));
                    ps.setString(29, CommonUtils.emptyToString(params.get(i).get("rank_09_Name")));
                    ps.setString(30, CommonUtils.emptyToString(params.get(i).get("memo")));
                    ps.setTimestamp(31, new Timestamp(new Date().getTime()));
                    ps.setString(32, UUID.randomUUID().toString());
                    ps.setString(33, CommonUtils.emptyToString(params.get(i).get("makeUp")));

                }

                public int getBatchSize() {
                    return params.size();
                }
            });
        return (count != null ? count.length : 0);
    }

    @Override
    public int batchSaveCommissionRate(final List<Map<String, Object>> params, final String rateMonth) throws SLException {
        // 保存之前先删除本月已经保存过的数据，避免重复保存
//        String currMonth = DateUtils.formatDate(new Date(), "yyyy-MM");
        // 删除以保存的指定月的数据
        String sql1 = "DELETE FROM BAO_T_COMMISSION_RATE T WHERE RATE_MONTH = ?";
        jdbcTemplate.update(sql1, new Object[]{rateMonth});

        StringBuffer sql = new StringBuffer()
                .append("INSERT INTO BAO_T_COMMISSION_RATE ")
                .append("(SHOP_NAME, SHOP_TYPE, JOB_NO, CUST_NAME, RANKING_NAME, RANKING, ")
                .append("COMMISSION_RATE_TOTAL, COMMISSION_RATE_PERSONAL, COMMISSION_RATE_GRANT, CREATE_DATE, RATE_MONTH, ID) ")
                .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                ;

        int[] count = jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {

                ps.setString(1, CommonUtils.emptyToString(params.get(i).get("shop")));
                ps.setString(2, CommonUtils.emptyToString(params.get(i).get("shopType")));
                ps.setString(3, CommonUtils.emptyToString(params.get(i).get("jobNo")));
                ps.setString(4, CommonUtils.emptyToString(params.get(i).get("custName")));
                ps.setString(5, CommonUtils.emptyToString(params.get(i).get("rankingName")));
                ps.setString(6, "");
                ps.setBigDecimal(7, CommonUtils.emptyToDecimal(params.get(i).get("commissionRateTotal")));
                ps.setBigDecimal(8, CommonUtils.emptyToDecimal(params.get(i).get("commissionRatePersonal")));
                ps.setBigDecimal(9, CommonUtils.emptyToDecimal(params.get(i).get("commissionRateGrant")));
                ps.setTimestamp(10, new Timestamp(new Date().getTime()));
                ps.setString(11, rateMonth);
                ps.setString(12, UUID.randomUUID().toString());
            }
            public int getBatchSize() {
                return params.size();
            }
        });
        return (count != null ? count.length : 0);
    }

    @Override
    public Page<Map<String, Object>> queryCommissionAmountList(Map<String, Object> param) {
        SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
                .addSql("SELECT T.CUST_NAME \"custName\", T.JOB_NO \"jobNo\", TRUNC(NVL(T.TOTAL_AMOUNT, 0), 2) \"totalAmount\"")
                .addSql(" FROM BAO_T_COMMISSION_FINAL T ORDER BY JOB_NO");
        return repositoryUtil.queryForPageMap(sqlCon.toString(), sqlCon.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
    }

    @Override
    public void updateRank(int i) {
        String tableName;
        if (i == 1) {
            tableName = "BAO_T_COMMISSION_SENDBACK";
        } else {
            tableName = "BAO_T_COMMISSION_RATE";
        }
        StringBuilder sql1 = new StringBuilder()
                .append("UPDATE " + tableName + " T")
                .append(" SET T.RANKING = 1")
                .append(" WHERE T.RANKING IS NULL AND (T.RANKING_NAME LIKE '%初级理财顾问%'")
                .append(" OR T.RANKING_NAME LIKE '%中级理财顾问%'")
                .append(" OR T.RANKING_NAME LIKE '%中级理财顾问%'")
                .append(" OR T.RANKING_NAME LIKE '%高级理财顾问%'")
                .append(" OR T.RANKING_NAME LIKE '%初级理财经理%'")
                .append(" OR T.RANKING_NAME LIKE '%中级理财经理%'")
                .append(" OR T.RANKING_NAME LIKE '%高级理财经理%'")
                .append(" OR T.RANKING_NAME LIKE '%资深理财经理%'")
                .append(" OR T.RANKING_NAME LIKE '%VIP理财经理%'")
                .append(" OR T.RANKING_NAME LIKE '%金牌理财经理%')")
                ;
        StringBuilder sql2 = new StringBuilder()
                .append("UPDATE " + tableName + " T")
                .append(" SET T.RANKING = 2")
                .append(" WHERE T.RANKING IS NULL AND (T.RANKING_NAME LIKE '%初级团队经理%'")
                .append(" OR T.RANKING_NAME LIKE '%中级团队经理%'")
                .append(" OR T.RANKING_NAME LIKE '%高级团队经理%'")
                .append(" OR T.RANKING_NAME LIKE '%资深团队经理%')")
                ;
        StringBuilder sql3 = new StringBuilder()
                .append("UPDATE " + tableName + " T")
                .append(" SET T.RANKING = 3")
                .append(" WHERE T.RANKING IS NULL AND (T.RANKING_NAME LIKE '%初级大团队经理%'")
                .append(" OR T.RANKING_NAME LIKE '%高级大团队经理%')")
                ;
        StringBuilder sql4 = new StringBuilder()
                .append("UPDATE " + tableName + " T")
                .append(" SET T.RANKING = 4")
                .append(" WHERE T.RANKING IS NULL AND (T.RANKING_NAME LIKE '%初级店长%'")
                .append(" OR T.RANKING_NAME LIKE '%中级店长%'")
                .append(" OR T.RANKING_NAME LIKE '%高级店长%'")
                .append(" OR T.RANKING_NAME LIKE '%资深店长%')")
                ;
        StringBuilder sql5 = new StringBuilder()
                .append("UPDATE " + tableName + " T")
                .append(" SET T.RANKING = 5")
                .append(" WHERE T.RANKING IS NULL AND (T.RANKING_NAME LIKE '%初级营业部经理%'")
                .append(" OR T.RANKING_NAME LIKE '%中级营业部经理%'")
                .append(" OR T.RANKING_NAME LIKE '%高级营业部经理%'")
                .append(" OR T.RANKING_NAME LIKE '%资深营业部经理%')")
                ;
        StringBuilder sql6 = new StringBuilder()
                .append("UPDATE " + tableName + " T")
                .append(" SET T.RANKING = 6")
                .append(" WHERE T.RANKING IS NULL AND (T.RANKING_NAME LIKE '%初级城市经理%'")
                .append(" OR T.RANKING_NAME LIKE '%中级城市经理%'")
                .append(" OR T.RANKING_NAME LIKE '%高级城市经理%')")
                ;
        StringBuilder sql7 = new StringBuilder()
                .append("UPDATE " + tableName + " T")
                .append(" SET T.RANKING = 7")
                .append(" WHERE T.RANKING IS NULL AND (T.RANKING_NAME LIKE '%初级区域经理%'")
                .append(" OR T.RANKING_NAME LIKE '%中级区域经理%'")
                .append(" OR T.RANKING_NAME LIKE '%高级区域经理%')")
                ;
        StringBuilder sql8 = new StringBuilder()
                .append("UPDATE " + tableName + " T")
                .append(" SET T.RANKING = 8")
                .append(" WHERE T.RANKING IS NULL AND T.RANKING_NAME LIKE '%副总裁%'")
                ;
        StringBuilder sql9 = new StringBuilder()
                .append("UPDATE " + tableName + " T")
                .append(" SET T.RANKING = 9")
                .append(" WHERE T.RANKING IS NULL AND T.RANKING_NAME LIKE '%执行总裁%'")
                ;
        jdbcTemplate.batchUpdate(sql1.toString(), sql2.toString(), sql3.toString(), sql4.toString(),
                sql5.toString(), sql6.toString(), sql7.toString(), sql8.toString(), sql9.toString());
    }

    @Override
    public List<Map<String, Object>> getRankingByCredentialsCode(String credentialsCode) {
//        String sql = "SELECT * FROM BAO_T_COMMISSION_RATE R WHERE R.JOB_NO = (" +
//                " SELECT H.工号 FROM BAO_T_CUST_INFO C" +
//                " INNER JOIN vw_bd_psndoc H ON UPPER(H.证件号码) = UPPER(C.credentials_code)" +
//                " WHERE C.ID = ?)";
        // 这里用“IN”而不用“=”是为了避免从人事系统同步过来的数据有重复
        String sql = "SELECT R.JOB_NO, R.RANKING FROM BAO_T_COMMISSION_RATE R WHERE R.JOB_NO IN " +
                " (SELECT 工号 FROM VW_BD_PSNDOC T WHERE T.证件号码 = ?) ORDER BY R.RATE_MONTH DESC ";
        return jdbcTemplate.queryForList(sql, new Object[]{credentialsCode});
    }

    @Override
    public List<Map<String, Object>>  getTransInfoCurrMonth(String investId, String currMonth) {
        String sql = "select sum(t.trade_scale) TRADE_SCALE  from bao_t_loan_transfer t, bao_t_wealth_hold_info w "+
                "where t.sender_hold_id = w.id and w.invest_id = ? and to_char(t.create_date, 'YYYYMM') = ?";
        return jdbcTemplate.queryForList(sql, new Object[]{investId, currMonth});
    }

    @Override
    public List<Map<String, Object>> getAllRateMonth() {
        String sql = "SELECT T.RATE_MONTH FROM BAO_T_COMMISSION_RATE T GROUP BY T.RATE_MONTH ORDER BY RATE_MONTH";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
	public BigDecimal getHoldScaleBeforeTrans(String investId, String lastDay) {
		String sql = "select sum(t.trade_scale) from bao_t_loan_transfer t, bao_t_wealth_hold_info w "+
					"where t.sender_hold_id = w.id and w.invest_id = ? and to_char(t.create_date, 'YYYYMMDD') > ?";
		return jdbcTemplate.queryForObject(sql, new Object[]{investId, lastDay}, BigDecimal.class);
	}
}
