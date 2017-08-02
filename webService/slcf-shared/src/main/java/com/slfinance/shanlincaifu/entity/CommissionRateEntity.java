package com.slfinance.shanlincaifu.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 提点表实体
 * <p>
 * Created by Laurance on 2017/4/20.
 */
@Getter
@Setter
@Table(name = "BAO_T_COMMISSION_RATE")
public class CommissionRateEntity {
    @Column(name = "SHOP_NAME")
    private String shopName;// 门店
    @Column(name = "JOB_NO")
    private String jobNo;// 工号
    @Column(name = "CUST_NAME")
    private String custName;// 业务员姓名
    @Column(name = "RANKING_NAME")
    private String rankingName;// 级别
    @Column(name = "RANKING")
    private String ranking;// 级别
    @Column(name = "COMMISSION_RATE_TOTAL")
    private BigDecimal commissionRateTotal;// 总账提点
    @Column(name = "COMMISSION_RATE_PERSONAL")
    private BigDecimal commissionRatePersonal;// 个人提点
    @Column(name = "COMMISSION_RATE_GRANT")
    private BigDecimal commissionRateGrant;// 提成发放比例
    @Column(name = "INVEST_AMOUNT")
    private BigDecimal investAmount;// 投资金额
    @Column(name = "HOLD_AMOUNT")
    private BigDecimal holdAmount;// 业绩
    @Column(name = "COMMISSION_AMOUNT")
    private BigDecimal commissionAmount;// 佣金
    @Column(name = "INVEST_AMOUNT_MANAGER")
    private BigDecimal investAmountManager;// 管理岗下属的团队（含自己）投资金额
    @Column(name = "HOLD_AMOUNT_MANAGER")
    private BigDecimal holdAmountManager;// 管理岗下属的团队（含自己）业绩
    @Column(name = "COMMISSION_AMOUNT_MANAGER")
    private BigDecimal commissionAmountManager;// 管理岗的佣金
    @Column(name = "ALLOWANCE_AMOUNT")
    private BigDecimal allowanceAmount;// 管理岗津贴
    // 最终发放金额
    // 业务员 = (∑佣金+∑补贴)*提成发放比例
    // 管理岗 = (∑管理岗佣金+∑管理岗补贴+∑管理岗津贴)*提成发放比例
    @Column(name = "TOTAL_AMOUNT")
    private BigDecimal totalAmount;
    @Column(name = "CREATE_DATE")
    private Date createDate;
    @Column(name = "UPDATE_DATE")
    private Date updateDate;
    @Column(name = "SUBSIDY_AMOUNT")
    private BigDecimal subsidyAmount;// 补贴金额 = 佣金*12%*(n-1)*30/360
    @Column(name = "RATE_MONTH")
    private String rateMonth;// 提点所属月

    private String memo;
}
